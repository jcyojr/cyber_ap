/**
 *  주시스템명 : 부산시 사이버지방세청
 *  업  무  명 : 연계
 *  기  능  명 : 주거지전용주차료 연계 메인 
 *     
 *               5분단위로 동작...
 *  클래스  ID : Txdm2440
 *  변경  이력 :
 * ------------------------------------------------------------------------
 *  작성자      	소속  		    일자            Tag           내용
 * ------------------------------------------------------------------------
 *  송동욱       유채널(주)      2011.09.20         %01%         최초작성
 */
package com.uc.bs.cyber.daemon.txdm2440;

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
public class Txdm2440 extends Codm_BaseDaemon{

	private static String govid = "";
	private static String orgcd = "";
	private static String orgdb = "";
	
	/**
	 * 생성자
	 */
	public Txdm2440() throws IOException, Exception {
		
		super();

		log = LogFactory.getLog(this.getClass());

		this.loopTerm = 300;	/* 300초마다 */
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Txdm2440 Txdm2440;
		ApplicationContext context  = null;
		
		try {
			// Log
			Txdm2440 = new Txdm2440();
			
			CbUtil.setupLog4jConfig(Txdm2440, "log4j.xml");
			
			try {
				String[] xmls = XMLFileReader.getAllFilePathArray(CbUtil.getResourcePath(Txdm2440, "/"), "SERVICE.XML");

				String strSrc = CbUtil.getResourcePath(Txdm2440, "/") + "config/sqlmaps.xml";

				String strDest = CbUtil.getResourcePath(Txdm2440, "/") + "config/sqlmapConfig.xml";

				XmlUtils.setXmlAttributes(Txdm2440, strSrc, strDest, "sqlMapConfig", "sqlMap", "url", xmls);

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
		
			context = new ClassPathXmlApplicationContext("config/Spring-db.xml");
			
			Txdm2440.setProcess("2440", "주거지전용주차료 자료연계(시도)", "thr_2440");  /* 업무데몬 등록 */
			
			Txdm2440.setContext(context);
			
			Thread tx2440Thread = new Thread(Txdm2440);
			
			tx2440Thread.setName("thr_2440");
			
			tx2440Thread.start();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void initProcess() throws Exception {
		// TODO Auto-generated method stub
		
		log.debug("=====================================================================");
		log.debug("== " + this.getClass().getName() +  " initProcess() ==");
		log.debug("=====================================================================");
		
		/**
		 * 기존의 방식과 틀림...
		 * 연계, 삭제, 소인삭제 를 각각의 쓰레드로 실행한다...
		 */
		
		int procCnt  = 3;
		
		if (log.isDebugEnabled()){
			log.debug("기동프로세스 갯수 :: procCnt = " + procCnt);
		}
		
		threadList = new Thread[procCnt];
		
		for(int i = 0; i<procCnt; i++) {
			
			/*
			 * 1. 시청(구청)코드
			 * 2. DB연결정보
			 * 3. Context
			 * */
			govid = CbUtil.getResource("ApplicationResource", "cyber.bt.gov0");
			orgcd = CbUtil.getResource("ApplicationResource", "cyber." + govid + ".org_cd");
			orgdb = "JUGEOJI";  /*주거지 전용주차료 DB연결*/
			
			threadList[i] = new Thread(newProcess(govid, orgdb, i), "sub_2440thr_" + orgcd);			
			threadList[i].start();
			
		}
	}

	@Override
	public void mainProcess() throws Exception {
		// TODO Auto-generated method stub
		for(int i = 0; i<threadList.length; i++) {
			
			if(!threadList[i].isAlive()) {	// 다시 살려라
				
				log.debug("=====================================================================");
				log.debug("== " + this.getClass().getName() + " mainProcess(" + i + ") ==");
				log.debug("=====================================================================");
				
				log.debug(" Thread(" + threadList[i].getName() + ") is " + threadList[i].isAlive());
				
				govid = CbUtil.getResource("ApplicationResource", "cyber.bt.gov" + i);
				orgcd = CbUtil.getResource("ApplicationResource", "cyber." + govid + ".org_cd");
				orgdb = "JUGEOJI";
				
				try {
					threadList[i] = new Thread(newProcess(govid, orgdb, i), govid);
					threadList[i].start();
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}			
				
			}
			
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
	private Txdm2440_process newProcess(String govId, String orgcd, int jobid) throws Exception {
		
		Txdm2440_process process = new Txdm2440_process();
		
		process.setContext(appContext);
		process.setGovId(govId);
		process.setUpId(jobid);
		process.setDataSource(orgcd);
		
		process.initProcess();		
		
		return process;
	}

}
