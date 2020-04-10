/**
 *  주시스템명 : 부산시 사이버지방세청
 *  업  무  명 : 연계
 *  기  능  명 : 표준세외수입 가상계좌연계
 *               60분단위로 동작...
 *  클래스  ID : Txdm2414
 *  변경  이력 :
 * ------------------------------------------------------------------------
 *  작성자      	소속  		    일자            Tag           내용
 * ------------------------------------------------------------------------
 *  YHCHOI       유채널(주)      2015.02.12         %01%         최초작성
 */
package com.uc.bs.cyber.daemon.txdm2418;

import java.io.IOException;

import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.uc.bs.cyber.CbUtil;
import com.uc.bs.cyber.daemon.Codm_BaseDaemon;
import com.uc.core.misc.XMLFileReader;
import com.uc.core.misc.XmlUtils;

/**
 * @author Administrator
 *
 */
public class Txdm2418 extends Codm_BaseDaemon {

	private static String govid = "";
	private static String orgcd = "";
	private static String orgdb = "";

    /*생성자*/
	public Txdm2418() throws IOException, Exception {
		super();
		
		log = LogFactory.getLog(this.getClass());

		this.loopTerm = 310;	/*   310초마다 */
		
	}
		
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		Txdm2418 Txdm2418;
		ApplicationContext context  = null;
		
		try {
			// Log
			Txdm2418 = new Txdm2418();
			
			CbUtil.setupLog4jConfig(Txdm2418, "log4j.xml");
			
			try {
				String[] xmls = XMLFileReader.getAllFilePathArray(CbUtil.getResourcePath(Txdm2418, "/"), "SERVICE.XML");

				String strSrc = CbUtil.getResourcePath(Txdm2418, "/") + "config/sqlmaps.xml";

				String strDest = CbUtil.getResourcePath(Txdm2418, "/") + "config/sqlmapConfig.xml";

				XmlUtils.setXmlAttributes(Txdm2418, strSrc, strDest, "sqlMapConfig", "sqlMap", "url", xmls);

			} catch (Exception e) {

				e.printStackTrace();
			}			
		
			context = new ClassPathXmlApplicationContext("config/Sw-Spring-db.xml");
			
			Txdm2418.setProcess("2418", "표준세외수입(TX2112_TB) 가상계좌등록", "thr_2418");  /* 업무데몬 등록 */
			
			Txdm2418.setContext(context);
			
			Thread tx2418Thread = new Thread(Txdm2418);
			
			tx2418Thread.setName("thr_2418");
			
			tx2418Thread.start();
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
    /*
     * 현재는 10초단위로 thick 합니다.
     * 따라서 여기서는 현 데몬에 의해서 생성된 데몬을 관리하도록 합니다.
     * */
	public void mainProcess() throws Exception {
		
        for(int i = 0; i<threadList.length; i++) {
			
			if(!threadList[i].isAlive()) {	// 다시 살려라
				
				log.debug("=====================================================================");
				log.debug("== " + this.getClass().getName()+ " mainProcess(" + i + ") ==");
				log.debug("=====================================================================");
				
				log.debug(" Thread(" + threadList[i].getName() + ") is " + threadList[i].isAlive());
				
				govid = CbUtil.getResource("ApplicationResource", "cyber.seoi.gov" + i);
				orgcd = CbUtil.getResource("ApplicationResource", "cyber." + govid + ".org_cd");
				orgdb = "NI_" + orgcd;
				
				/* 연제구만 연계함 */
				//if(!(orgcd.equals("626")||orgcd.equals("337"))) continue;
								
				try {
					threadList[i] = new Thread(newProcess(govid, orgdb), govid);
				
					threadList[i].start();
					
				} catch (Exception e) {

					e.printStackTrace();
				}			
				
			}
			
		}
	}
	
	/*
	 * 업무 프로세스 시작합니다.
	 * 초기시작시 한번만 동작합니다.
	 * */
	public void initProcess() throws IOException, Exception {

		log.debug("=====================================================================");
		log.debug("== " + this.getClass().getName() +  " initProcess() ==");
		log.debug("=====================================================================");
		
		//int procCnt  = CbUtil.getResourceInt("ApplicationResource", "cyber.TestSeoi.count");
		//한번만
		int procCnt = 1;
		
		if(log.isDebugEnabled()){
			log.debug("시청세외수입 동작 :: procCnt = " + procCnt);
		}
	
		threadList = new Thread[procCnt];
		
		for(int i = 0; i<procCnt; i++) {
			
			/*
			 * 1. 시청(구청)코드
			 * 2. DB연결정보
			 * 3. Context
			 * */

			govid = CbUtil.getResource("ApplicationResource", "cyber.seoi.gov" + i);
			orgcd = CbUtil.getResource("ApplicationResource", "cyber." + govid + ".org_cd");
			orgdb = "NI_" + orgcd;
			
			
			threadList[i] = new Thread(newProcess(govid, orgdb), "sub_2418thr_" + orgcd);			
			threadList[i].start();
			
		}
		
	}


	public void onInterrupt() throws Exception {
		
		if(threadList == null) return;
		
		for(int i = 0; i<threadList.length; i++) {
			threadList[i].interrupt();
		}
	}


	public void transactionJob() {
		
	}

	/**
	 * @param govId
	 * @return
	 * @throws Exception
	 * 메인 로직...을 호출한다.
	 */
	private Txdm2418_process newProcess(String govId, String orgcd) throws Exception {
		
		Txdm2418_process process = new Txdm2418_process();
		
		process.setContext(appContext);
		process.setGovId(govId);
		process.setDataSource(orgcd);
		
		process.initProcess();		
		
		return process;
	}

}
