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
package com.uc.bs.cyber.daemon.txdm2417;

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
public class Txdm2417 extends Codm_BaseDaemon {

	private static String govid = "";
	private static String orgcd = "";
	private static String orgdb = "";

    /*생성자*/
	public Txdm2417() throws IOException, Exception {
		super();
		
		log = LogFactory.getLog(this.getClass());

		this.loopTerm = 250;	/*   250초마다 */
		
	}
		
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		Txdm2417 Txdm2417;
		ApplicationContext context  = null;
		
		try {
			// Log
			Txdm2417 = new Txdm2417();
			
			CbUtil.setupLog4jConfig(Txdm2417, "log4j.xml");
			
			try {
				String[] xmls = XMLFileReader.getAllFilePathArray(CbUtil.getResourcePath(Txdm2417, "/"), "SERVICE.XML");

				String strSrc = CbUtil.getResourcePath(Txdm2417, "/") + "config/sqlmaps.xml";

				String strDest = CbUtil.getResourcePath(Txdm2417, "/") + "config/sqlmapConfig.xml";

				XmlUtils.setXmlAttributes(Txdm2417, strSrc, strDest, "sqlMapConfig", "sqlMap", "url", xmls);

			} catch (Exception e) {
				e.printStackTrace();
			}			
		
			context = new ClassPathXmlApplicationContext("config/Sw-Spring-db.xml");
			
			Txdm2417.setProcess("2417", "표준세외수입 가상계좌체번연계", "thr_2417");  /* 업무데몬 등록 */
			
			Txdm2417.setContext(context);
			
			Thread tx2417Thread = new Thread(Txdm2417);
			
			tx2417Thread.setName("thr_2417");
			
			tx2417Thread.start();
			
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
		
		int procCnt  = CbUtil.getResourceInt("ApplicationResource", "cyber.seoi.count");
		
		//기장군 제외
		procCnt = procCnt - 1;
		if (log.isDebugEnabled()){
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
			
			/* 연제구만 연계함 */
			//if(!(orgcd.equals("626")||orgcd.equals("337"))) continue;
			
			threadList[i] = new Thread(newProcess(govid, orgdb), "sub_2417thr_" + orgcd);			
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
	private Txdm2417_process newProcess(String govId, String orgcd) throws Exception {
		
		Txdm2417_process process = new Txdm2417_process();
		
		process.setContext(appContext);
		process.setGovId(govId);
		process.setDataSource(orgcd);
		
		process.initProcess();		
		
		return process;
	}

}
