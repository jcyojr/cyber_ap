/**
 *  주시스템명 : 부산시 사이버지방세청
 *  업  무  명 : 연계
 *  기  능  명 : 수기입력용납세자정보수신
 *  클래스  ID : Txdm1175
 *  변경  이력 :
 * ------------------------------------------------------------------------
 *  작성자      	소속  		    일자            Tag           내용
 * ------------------------------------------------------------------------
 *  YHCHOI     유채널(주)      2015.04.30           %01%         최초작성
 */
package com.uc.bs.cyber.daemon.txdm1175;

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
public class Txdm1175 extends Codm_BaseDaemon {

	private static String govid = "";
	private static String orgcd = "";
	private static String orgdb = "";

    /*생성자*/
	public Txdm1175() throws IOException, Exception {
		super();
		
		log = LogFactory.getLog(this.getClass());

		this.loopTerm = 60 * 30;	/* 30분마다 */
		
	}
		
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		Txdm1175 Txdm1175;
		ApplicationContext context  = null;
		
		try {
			// Log
			Txdm1175 = new Txdm1175();
			
			CbUtil.setupLog4jConfig(Txdm1175, "log4j.xml");
			
			try {
				String[] xmls = XMLFileReader.getAllFilePathArray(CbUtil.getResourcePath(Txdm1175, "/"), "SERVICE.XML");

				String strSrc = CbUtil.getResourcePath(Txdm1175, "/") + "config/sqlmaps.xml";

				String strDest = CbUtil.getResourcePath(Txdm1175, "/") + "config/sqlmapConfig.xml";

				XmlUtils.setXmlAttributes(Txdm1175, strSrc, strDest, "sqlMapConfig", "sqlMap", "url", xmls);

			} catch (Exception e) {
				e.printStackTrace();
			}			
		
			context = new ClassPathXmlApplicationContext("config/Tax-Spring-db.xml");
			
			Txdm1175.setProcess("1175", "수기입력용정보수신 (구청별)", "thr_1175");  /* 업무데몬 등록 */
			
			Txdm1175.setContext(context);
			
			Thread tx1175Thread = new Thread(Txdm1175);
			
			tx1175Thread.setName("thr_1175");
			
			tx1175Thread.start();
			
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
		
        for(int i = 0; i<threadList.length-1; i++) {
			
			if(!threadList[i].isAlive()) {	// 다시 살려라
				
				log.debug("=====================================================================");
				log.debug("== " + this.getClass().getName()+ " mainProcess(" + i + ") ==");
				log.debug("=====================================================================");
				
				log.debug(" Thread(" + threadList[i].getName() + ") is " + threadList[i].isAlive());
				
				govid = CbUtil.getResource("ApplicationResource", "cyber.tax.gov" + i);
				orgcd = CbUtil.getResource("ApplicationResource", "cyber." + govid + ".org_cd");
				orgdb = "JI_" + orgcd;
				
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
		
		int procCnt  = CbUtil.getResourceInt("ApplicationResource", "cyber.tax.count");
		
		procCnt = procCnt - 1;
		//log.debug("procCnt :: " + procCnt);
		
		if (log.isDebugEnabled()){
			log.debug("수기입력용정보수신 동작 :: procCnt = " + procCnt);
		}
		
		threadList = new Thread[procCnt];
		
		for(int i = 0; i < procCnt; i++) {
			
			/*
			 * 1. 시청(구청)코드
			 * 2. DB연결정보
			 * 3. Context
			 * */

			govid = CbUtil.getResource("ApplicationResource", "cyber.tax.gov" + i);
			orgcd = CbUtil.getResource("ApplicationResource", "cyber." + govid + ".org_cd");//구청코드
			orgdb = "JI_" + orgcd;
			
			threadList[i] = new Thread(newProcess(govid, orgdb), "sub_1175thr_" + orgcd);			
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
	private Txdm1175_process newProcess(String govId, String orgcd) throws Exception {
		
		Txdm1175_process process = new Txdm1175_process();
		
		process.setContext(appContext);
		process.setGovId(govId);
		process.setDataSource(orgcd);
		
		process.initProcess();		
		
		return process;
	}

}
