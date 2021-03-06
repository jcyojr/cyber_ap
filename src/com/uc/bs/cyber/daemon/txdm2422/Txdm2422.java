/**
 *  주시스템명 : 부산시 사이버지방세청
 *  업  무  명 : 연계
 *  기  능  명 : 교통유발부담금 수납자료전송
 *               5분단위로 동작...
 *  클래스  ID : Txdm2422
 *  변경  이력 :
 * ------------------------------------------------------------------------
 *  작성자      	소속  		    일자            Tag           내용
 * ------------------------------------------------------------------------
 *  표승한       다산시스템      2013.12.13         %01%         최초작성
 */
package com.uc.bs.cyber.daemon.txdm2422;

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
public class Txdm2422 extends Codm_BaseDaemon {

	private static String govid = "";
	private static String orgcd = "";
	private static String orgdb = "";

    /*생성자*/
	public Txdm2422() throws IOException, Exception {
		super();
		// TODO Auto-generated constructor stub
		
		log = LogFactory.getLog(this.getClass());

		this.loopTerm = 300;	/*   300초마다 */
		
	}
		
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		Txdm2422 Txdm2422;
		ApplicationContext context  = null;
		
		try {
			// Log
			Txdm2422 = new Txdm2422();
			
			CbUtil.setupLog4jConfig(Txdm2422, "log4j.xml");
			
			try {
				String[] xmls = XMLFileReader.getAllFilePathArray(CbUtil.getResourcePath(Txdm2422, "/"), "SERVICE.XML");

				String strSrc = CbUtil.getResourcePath(Txdm2422, "/") + "config/sqlmaps.xml";

				String strDest = CbUtil.getResourcePath(Txdm2422, "/") + "config/sqlmapConfig.xml";

				XmlUtils.setXmlAttributes(Txdm2422, strSrc, strDest, "sqlMapConfig", "sqlMap", "url", xmls);

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
		
			context = new ClassPathXmlApplicationContext("config/Sw-Spring-db.xml");
			
			Txdm2422.setProcess("2422", "교통유발부담금 수납자료연계", "thr_2422");  /* 업무데몬 등록 */
			
			Txdm2422.setContext(context);
			
			Thread tx2422Thread = new Thread(Txdm2422);
			
			tx2422Thread.setName("thr_2422");
			
			tx2422Thread.start();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
    /*
     * 현재는 10초단위로 thick 합니다.
     * 따라서 여기서는 현 데몬에 의해서 생성된 데몬을 관리하도록 합니다.
     * */
	@Override
	public void mainProcess() throws Exception {
		// TODO Auto-generated method stub
		
		//i=1 부터 시작하여 시청은 제외한다.
        for(int i = 1; i<threadList.length; i++) {
			
			if(!threadList[i].isAlive()) {	// 다시 살려라
				
				log.debug("=====================================================================");
				log.debug("== " + this.getClass().getName()+ " mainProcess(" + i + ") ==");
				log.debug("=====================================================================");
				
				log.debug(" Thread(" + threadList[i].getName() + ") is " + threadList[i].isAlive());
				
				govid = CbUtil.getResource("ApplicationResource", "cyber.seoi.gov" + i);
				orgcd = CbUtil.getResource("ApplicationResource", "cyber." + govid + ".org_cd");
				orgdb = "NI_" + orgcd;
				
				/* 시청 연계 안함 */
				//if(orgcd.equals("626")) continue;
				
				try {
					threadList[i] = new Thread(newProcess(govid, orgdb), govid);
				
					threadList[i].start();
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
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
		// TODO Auto-generated method stub
		log.debug("=====================================================================");
		log.debug("== " + this.getClass().getName() +  " initProcess() ==");
		log.debug("=====================================================================");
		
		int procCnt  = CbUtil.getResourceInt("ApplicationResource", "cyber.seoi.count");
		
		if (log.isDebugEnabled()){
			log.debug("시청세외수입 동작 :: procCnt = " + procCnt);
		}
		
		threadList = new Thread[procCnt];
		
		//i=1 부터 시작하여 시청은 제외한다.
		for(int i = 1; i<procCnt; i++) {
			
			/*
			 * 1. 시청(구청)코드
			 * 2. DB연결정보
			 * 3. Context
			 * */

			govid = CbUtil.getResource("ApplicationResource", "cyber.seoi.gov" + i);
			orgcd = CbUtil.getResource("ApplicationResource", "cyber." + govid + ".org_cd");
			orgdb = "NI_" + orgcd;
			
			/* 시청 연계 안함 */
			//if(orgcd.equals("626")) continue;
			
			threadList[i] = new Thread(newProcess(govid, orgdb), "sub_2422thr_" + orgcd);			
			threadList[i].start();
			
		}
		
	}

	@Override
	public void onInterrupt() throws Exception {
		// TODO Auto-generated method stub
		
		if(threadList == null) return;
		
		for(int i = 0; i<threadList.length; i++) {
			threadList[i].interrupt();
		}
	}

	@Override
	public void transactionJob() {
		// TODO Auto-generated method stub
		
	}

	/**
	 * @param govId
	 * @return
	 * @throws Exception
	 * 메인 로직...을 호출한다.
	 */
	private Txdm2422_process newProcess(String govId, String orgcd) throws Exception {
		
		Txdm2422_process process = new Txdm2422_process();
		
		process.setContext(appContext);
		process.setGovId(govId);
		process.setDataSource(orgcd);
		
		process.initProcess();		
		
		return process;
	}

}
