/**
 *  주시스템명 : 부산시 사이버지방세청
 *  업  무  명 : 연계
 *  기  능  명 : 버스전용차로위반과태료 부과자료 연계
 *               15분단위로 동작...
 *  클래스  ID : Txdm2430
 *  변경  이력 :
 * ------------------------------------------------------------------------
 *  작성자      	소속  		    일자            Tag           내용
 * ------------------------------------------------------------------------
 *  송동욱       유채널(주)      2011.05.31         %01%         최초작성
 */
package com.uc.bs.cyber.daemon.txdm2430;

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
public class Txdm2430 extends Codm_BaseDaemon {

	private static String govid = "";
	private static String orgcd = "";
	private static String orgdb = "";
	
	/**
	 * 생성자
	 */
	public Txdm2430() throws IOException, Exception {
		// TODO Auto-generated constructor stub
		super();
		
		log = LogFactory.getLog(this.getClass());

		this.loopTerm = 30;	/* 30초마다 */
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Txdm2430 Txdm2430;
		ApplicationContext context  = null;
		
		try {
			// Log
			Txdm2430 = new Txdm2430();
			
			CbUtil.setupLog4jConfig(Txdm2430, "log4j.xml");
			
			try {
				String[] xmls = XMLFileReader.getAllFilePathArray(CbUtil.getResourcePath(Txdm2430, "/"), "SERVICE.XML");

				String strSrc = CbUtil.getResourcePath(Txdm2430, "/") + "config/sqlmaps.xml";

				String strDest = CbUtil.getResourcePath(Txdm2430, "/") + "config/sqlmapConfig.xml";

				XmlUtils.setXmlAttributes(Txdm2430, strSrc, strDest, "sqlMapConfig", "sqlMap", "url", xmls);

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
		
			context = new ClassPathXmlApplicationContext("config/Spring-db.xml");
			
			Txdm2430.setProcess("2430", "버스전용차로위반과태료 부과자료연계", "thr_2430");  /* 업무데몬 등록 */
			
			Txdm2430.setContext(context);
			
			Thread tx2430Thread = new Thread(Txdm2430);
			
			tx2430Thread.setName("thr_2430");
			
			tx2430Thread.start();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/*
	 * 업무 프로세스 시작합니다.
	 * 초기시작시 한번만 동작합니다.
	 * */
	@Override
	public void initProcess() throws Exception {
		// TODO Auto-generated method stub
		log.debug("=====================================================================");
		log.debug("== " + this.getClass().getName() +  " initProcess() ==");
		log.debug("=====================================================================");
		
		int procCnt  = CbUtil.getResourceInt("ApplicationResource", "cyber.busvir.count");
		
		if (log.isDebugEnabled()){
			log.debug("procCnt = " + procCnt);
		}
		
		threadList = new Thread[procCnt];
		
		for(int i = 0; i<procCnt; i++) {
			
			/*
			 * 1. 버스전용차로위반과태료 고지자료연계
			 * 2. DB연결정보
			 * 3. Context
			 * 4. dataSource36 - Spring-db.xml 참조 
			 *    참고) 버스전용차로위반과태료 자료가 많지 않으므로 구청별로 쓰레드를 설정하고 않고 1쓰레드로 주기적으로 연계한다.
			 * */

			govid = CbUtil.getResource("ApplicationResource", "cyber.busvir.gov" + i);
			orgcd = CbUtil.getResource("ApplicationResource", "cyber." + govid + ".org_cd");
			
			orgdb = "BI_" + orgcd; //버스전용차로위반 연결
			
			threadList[i] = new Thread(newProcess(govid, orgdb), "sub_2430thr_" + orgcd);			
			threadList[i].start();
			
		}
	}
    /*
     * 현재는 10초단위로 thick 합니다.
     * 따라서 여기서는 현 데몬에 의해서 생성된 데몬을 관리하도록 합니다.
     * */
	@Override
	public void mainProcess() throws Exception {
		// TODO Auto-generated method stub
		for(int i = 0; i<threadList.length; i++) {
			
			if(!threadList[i].isAlive()) {	// 다시 살려라
				
				log.debug("=====================================================================");
				log.debug("== " + this.getClass().getName()+ " mainProcess(" + i + ") ==");
				log.debug("=====================================================================");
				
				log.debug(" Thread(" + threadList[i].getName() + ") is " + threadList[i].isAlive());
				
				govid = CbUtil.getResource("ApplicationResource", "cyber.busvir.gov" + i);
				orgcd = CbUtil.getResource("ApplicationResource", "cyber." + govid + ".org_cd");
				
				orgdb = "BI_" + orgcd;   //버스전용차로위반 연결
				
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
	 * @param govId 업무구분
	 * @param orgcd 기관구분
	 * @return
	 * @throws Exception
	 * 메인 로직...을 호출한다.
	 */
	private Txdm2430_process newProcess(String govId, String orgcd) throws Exception {
		
		Txdm2430_process process = new Txdm2430_process();
		
		process.setContext(appContext);
		process.setGovId(govId);
		process.setDataSource(orgcd);
		
		process.initProcess();		
		
		return process;
	}

}
