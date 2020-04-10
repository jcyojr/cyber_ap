/**
 *  주시스템명 : 부산시 사이버지방세청
 *  업  무  명 : 연계
 *  기  능  명 : 환경개선부담금 고지자료연계
 *               5분단위로 동작...
 *               환경개선부담금은  별도의 구청서버에 접속하지 않고 시도서버로만 연결한다
 *               간단E납부 실시간 수납 수신 - 20131204
 *  클래스  ID : Txdm2460
 *  변경  이력 : 2014.01.23 간단 e-납부 실시간 수납 수신 추가
 * ------------------------------------------------------------------------
 *  작성자      	소속  		    일자            Tag           내용
 * ------------------------------------------------------------------------
 *  송동욱       유채널(주)      2011.06.03         %01%         최초작성
 */
package com.uc.bs.cyber.daemon.txdm2460;

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
public class Txdm2460 extends Codm_BaseDaemon {

	private static String govid = "";
	private static String orgcd = "";
	private static String orgdb = "";
	
	/**
	 * 생성자
	 */
	public Txdm2460() throws IOException, Exception {
	
		super();
		
		log = LogFactory.getLog(this.getClass());

		this.loopTerm = 30;	/* 30초마다 */
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {

		Txdm2460 Txdm2460;
		ApplicationContext context  = null;
		
		try {
			// Log
			Txdm2460 = new Txdm2460();
			
			CbUtil.setupLog4jConfig(Txdm2460, "log4j.xml");
			
			try {
				String[] xmls = XMLFileReader.getAllFilePathArray(CbUtil.getResourcePath(Txdm2460, "/"), "SERVICE.XML");

				String strSrc = CbUtil.getResourcePath(Txdm2460, "/") + "config/sqlmaps.xml";

				String strDest = CbUtil.getResourcePath(Txdm2460, "/") + "config/sqlmapConfig.xml";

				XmlUtils.setXmlAttributes(Txdm2460, strSrc, strDest, "sqlMapConfig", "sqlMap", "url", xmls);

			} catch (Exception e) {
			
				e.printStackTrace();
				
			}			
		
			context = new ClassPathXmlApplicationContext("config/Tax-Spring-db.xml");
			
			Txdm2460.setProcess("2460", "환경개선부담금 고지자료연계", "thr_2460");  /* 업무데몬 등록 */
			
			Txdm2460.setContext(context);
			
			Thread tx2460Thread = new Thread(Txdm2460);
			
			tx2460Thread.setName("thr_2460");
			
			tx2460Thread.start();
			
		} catch (IOException e) {
		
			e.printStackTrace();
		} catch (Exception e) {
			
			e.printStackTrace();
		}
	}
	
	/*
	 * 업무 프로세스 시작합니다.
	 * 초기시작시 한번만 동작합니다.
	 * */
	public void initProcess() throws Exception {

		log.debug("=====================================================================");
		log.debug("== " + this.getClass().getName() +  " initProcess() ==");
		log.debug("=====================================================================");
		
		int procCnt  = CbUtil.getResourceInt("ApplicationResource", "cyber.ht.count");
		
		/*설정과 상관없이 */
		procCnt = 2;
		
		if (log.isDebugEnabled()){
			log.debug("procCnt = " + procCnt);
		}
		
		threadList = new Thread[procCnt];
		
		for(int i = 0; i<procCnt; i++) {
			
			/*
			 * 1. 환경개선부담금 고지자료연계
			 * 2. DB연결정보
			 * 3. Context
			 * 4. dataSource34 - Spring-db.xml 참조 
			 *    참고) 환경개선연계는 자료가 많지 않으므로 구청별로 쓰레드를 설정하고 않고 1쓰레드로 주기적으로 연계한다.
			 * 5. 20110928 업무별로 쓰레딩하도록 추가함.   
			 * 6. 20140102 간단E납부 관련 수납자료 처리 추가
			 * */

			govid = CbUtil.getResource("ApplicationResource", "cyber.ht.gov0");    /*다른 프로세스와 구분할것*/
			orgcd = CbUtil.getResource("ApplicationResource", "cyber." + govid + ".org_cd");
			orgdb = "HI_" + orgcd;
			
			threadList[i] = new Thread(newProcess(govid, orgdb, i), "sub_2460thr_" + orgcd);			
			threadList[i].start();
			
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
				
				govid = CbUtil.getResource("ApplicationResource", "cyber.ht.gov" + i);
				orgcd = CbUtil.getResource("ApplicationResource", "cyber." + govid + ".org_cd");
				orgdb = "HI_" + orgcd;
				
				try {
					threadList[i] = new Thread(newProcess(govid, orgdb, i), govid);
				
					threadList[i].start();
					
				} catch (Exception e) {
				
					e.printStackTrace();
				}			
				
			}
			
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
	 * @param govId 업무구분
	 * @param orgcd 기관구분
	 * @return
	 * @throws Exception
	 * 메인 로직...을 호출한다.
	 */
	private Txdm2460_process newProcess(String govId, String orgcd, int jobid) throws Exception {
		
		Txdm2460_process process = new Txdm2460_process();
		
		process.setContext(appContext);
		process.setGovId(govId);
		process.setUpId(jobid);
		process.setDataSource(orgcd);
		
		process.initProcess();		
		
		return process;
	}

}
