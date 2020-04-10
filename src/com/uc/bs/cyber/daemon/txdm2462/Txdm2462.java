/**
 *  주시스템명 : 부산시 사이버지방세청
 *  업  무  명 : 연계
 *  기  능  명 : 간단 e 납부 - 환경개선부담금 실시간 수납 전송              
 *  클래스  ID : Txdm2462
 *  변경  이력 :
 * ------------------------------------------------------------------------
 *  작성자      	소속  		    일자            Tag           내용
 * ------------------------------------------------------------------------
 *  최유환       유채널(주)      2014.01.14         %01%         최초작성
 *  
 */
package com.uc.bs.cyber.daemon.txdm2462;

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
public class Txdm2462 extends Codm_BaseDaemon {

	private static String govid = "";
	private static String orgcd = "";
	private static String orgdb = "";
	
	/**
	 * @throws Exception 
	 * @throws IOException 
	 * 
	 */
	public Txdm2462() throws IOException, Exception {
		
		super();
		
		log = LogFactory.getLog(this.getClass());

		this.loopTerm = 30;	/* 30초마다 */
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		Txdm2462 Txdm2462;
		ApplicationContext context  = null;
		
		try {
			// Log
			Txdm2462 = new Txdm2462();
			
			CbUtil.setupLog4jConfig(Txdm2462, "log4j.xml");
			
			try {
				String[] xmls = XMLFileReader.getAllFilePathArray(CbUtil.getResourcePath(Txdm2462, "/"), "SERVICE.XML");

				String strSrc = CbUtil.getResourcePath(Txdm2462, "/") + "config/sqlmaps.xml";

				String strDest = CbUtil.getResourcePath(Txdm2462, "/") + "config/sqlmapConfig.xml";

				XmlUtils.setXmlAttributes(Txdm2462, strSrc, strDest, "sqlMapConfig", "sqlMap", "url", xmls);

			} catch (Exception e) {
			
				e.printStackTrace();
			}			
		
			context = new ClassPathXmlApplicationContext("config/Spring-db.xml");
			
			Txdm2462.setProcess("2462", "환경개선부담금 실시간 수납전송", "thr_2462");  /* 업무데몬 등록 */
			
			Txdm2462.setContext(context);
			
			Thread tx2462Thread = new Thread(Txdm2462);
			
			tx2462Thread.setName("thr_2462");
			
			tx2462Thread.start();
			
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
	@Override
	public void initProcess() throws Exception {
	
		log.debug("=====================================================================");
		log.debug("== " + this.getClass().getName() +  " initProcess() ==");
		log.debug("=====================================================================");
		
		int procCnt  = CbUtil.getResourceInt("ApplicationResource", "cyber.hv.count");
		
		if (log.isDebugEnabled()){
			log.debug("procCnt = " + procCnt);
		}
		
		threadList = new Thread[procCnt];
		
		for(int i = 0; i<procCnt; i++) {
			
			/*
			 * 1. 환경개선부담금 가상계좌전송
			 * 2. DB연결정보
			 * 3. Context
			 * 4. 구청코드가 지방세와과 동일.
			 *    DB는 시청에 존재하고 구청별로 등록되어 있으므로 DB 연결은 동일하고 구청코드만 달리 셋팅
			 *    dataSource34 - Spring-db.xml 참조 
			 *    참고) 가상계좌 전송은 구청별로 한꺼번에 전송한다.
			 * */

			govid = CbUtil.getResource("ApplicationResource", "cyber.hv.gov" + i);
			orgcd = CbUtil.getResource("ApplicationResource", "cyber." + govid + ".org_cd");
			orgdb = "HI_000";
			
			threadList[i] = new Thread(newProcess(govid, orgdb), "sub_2462thr_" + orgcd);			
			threadList[i].start();
			
		}
	}
	
    /*
     * 현재는 10초단위로 thick 합니다.
     * 따라서 여기서는 현 데몬에 의해서 생성된 데몬을 관리하도록 합니다.
     * */
	@Override
	public void mainProcess() throws Exception {
		
		for(int i = 0; i<threadList.length; i++) {
			
			if(!threadList[i].isAlive()) {	// 다시 살려라
				
				log.debug("=====================================================================");
				log.debug("== " + this.getClass().getName()+ " mainProcess(" + i + ") ==");
				log.debug("=====================================================================");
				
				log.debug(" Thread(" + threadList[i].getName() + ") is " + threadList[i].isAlive());
				
				govid = CbUtil.getResource("ApplicationResource", "cyber.hv.gov" + i);
				orgcd = CbUtil.getResource("ApplicationResource", "cyber." + govid + ".org_cd");
				orgdb = "HI_000";
				
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
	 * @param govId 업무구분
	 * @param orgcd 기관구분
	 * @return
	 * @throws Exception
	 * 메인 로직...을 호출한다.
	 */
	private Txdm2462_process newProcess(String govId, String orgcd) throws Exception {
		
		Txdm2462_process process = new Txdm2462_process();
		
		process.setContext(appContext);
		process.setGovId(govId);
		process.setDataSource(orgcd);
		
		process.initProcess();		
		
		return process;
	}
}
