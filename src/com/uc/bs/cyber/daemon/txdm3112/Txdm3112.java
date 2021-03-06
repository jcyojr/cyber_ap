/**
 *  주시스템명 : 부산시 사이버지방세청
 *  업  무  명 : 연계
 *  기  능  명 : 지방세 연대납세자 부가자료(구청별) 연계 메인 
 *               개별 구청별 및 건별 쓰레드를 결정하고 업무프로세스 기동 및 재기동.
 *     
 *               5분단위로 동작...
 *  클래스  ID : Txdm3112
 *  사용  여부 : 현재는 테스트로 사용하고 만약을 위하여 주 프로세스기 기동하지 않는 경우를 대비.
 *  변경  이력 :
 * ------------------------------------------------------------------------
 *  작성자      	소속  		    일자            Tag           내용
 * ------------------------------------------------------------------------
 *  이윤섭       유채널(주)      2014.10.02         %01%         최초작성
 */
package com.uc.bs.cyber.daemon.txdm3112;

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
public class Txdm3112 extends Codm_BaseDaemon{

	private static String govid = "";
	private static String orgcd = "";
	private static String orgdb = "";
	
	/**
	 * 생성자
	 */
	public Txdm3112() throws IOException, Exception {
		
		super();

		log = LogFactory.getLog(this.getClass());

		this.loopTerm = 300;	/* 300초마다 */
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		Txdm3112 Txdm3112;
		ApplicationContext context  = null;
		
		try {
			//Log
			Txdm3112 = new Txdm3112();
			
			CbUtil.setupLog4jConfig(Txdm3112, "log4j.xml");
			
			try {
				String[] xmls = XMLFileReader.getAllFilePathArray(CbUtil.getResourcePath(Txdm3112, "/"), "SERVICE.XML");

				String strSrc = CbUtil.getResourcePath(Txdm3112, "/") + "config/sqlmaps.xml";

				String strDest = CbUtil.getResourcePath(Txdm3112, "/") + "config/sqlmapConfig.xml";

				XmlUtils.setXmlAttributes(Txdm3112, strSrc, strDest, "sqlMapConfig", "sqlMap", "url", xmls);

			} catch (Exception e) {

				e.printStackTrace();
			}			
		
			context = new ClassPathXmlApplicationContext("config/Tax-Spring-db.xml"); 
			
			Txdm3112.setProcess("3112", "지방세 연대납세자 자료연계(구청별)", "thr_3112");  /* 업무데몬 등록 */
			
			Txdm3112.setContext(context);
			
			Thread tx3112Thread = new Thread(Txdm3112);
			
			tx3112Thread.setName("thr_3112");
			
			tx3112Thread.start();
			
		} catch (IOException e) {

			e.printStackTrace();
		} catch (Exception e) {

			e.printStackTrace();
		}
	}

	
	public void initProcess() throws Exception {

		
		log.debug("=====================================================================");
		log.debug("== " + this.getClass().getName() +  " initProcess() ==");
		log.debug("=====================================================================");
		
		int procCnt  = CbUtil.getResourceInt("ApplicationResource", "cyber.tax.count");  
		
		if (log.isDebugEnabled()){
			log.debug("기동프로세스 갯수 :: procCnt = " + procCnt);
		}
		
		//procCnt = 1;
		
		threadList = new Thread[procCnt];
		
		for(int i = 0; i<procCnt; i++) {
			
			/*
			 * 1. 시청(구청)코드
			 * 2. DB연결정보
			 * 3. Context
			 * */

			govid = CbUtil.getResource("ApplicationResource", "cyber.tax.gov" + i);
			orgcd = CbUtil.getResource("ApplicationResource", "cyber." + govid + ".org_cd");
			orgdb = "JI_"+ orgcd;
			
			log.info("orgdb = [" + orgdb + "]");
			
			threadList[i] = new Thread(newProcess(govid, orgdb), "sub_3112thr_" + orgcd);			
			threadList[i].start();
			
		}
	}

	@Override
	public void mainProcess() throws Exception {

		for(int i = 0; i<threadList.length; i++) {
			
			if(!threadList[i].isAlive()) {	// 다시 살려라
				
				log.debug("=====================================================================");
				log.debug("== " + this.getClass().getName()+ " mainProcess(" + i + ") ==");
				log.debug("=====================================================================");
				
				log.debug(" Thread(" + threadList[i].getName() + ") is " + threadList[i].isAlive());
				
				govid = CbUtil.getResource("ApplicationResource", "cyber.tax.gov" + i);
				orgcd = CbUtil.getResource("ApplicationResource", "cyber." + govid + ".org_cd");
				orgdb = "JI_"+ orgcd;
				
				try {
					threadList[i] = new Thread(newProcess(govid, orgdb), govid);
					threadList[i].start();
					
				} catch (Exception e) {

					e.printStackTrace();
				}			
				
			}
			
		}
		
	}

	@Override
	public void onInterrupt() throws Exception {

		if(threadList == null) return;
		
		for(int i = 0; i<threadList.length; i++) {
			threadList[i].interrupt();
		}
	}

	@Override
	public void transactionJob() {

		
	}
	
	/**
	 * @param govId
	 * @return
	 * @throws Exception
	 * 메인 로직...을 호출한다.
	 */
	private Txdm3112_process newProcess(String govId, String orgcd) throws Exception {
		
		Txdm3112_process process = new Txdm3112_process();
		
		process.setContext(appContext);
		process.setGovId(govId);
		process.setDataSource(orgcd);
		
		process.initProcess();		
		
		return process;
	}
}
