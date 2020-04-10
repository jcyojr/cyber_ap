/**
 *  주시스템명 : 부산시 사이버지방세청
 *  업  무  명 : 연계
 *  기  능  명 : 표준지방세 자동차 저당권 설정/말소 전송
 *               5분단위로 동작...
 *  클래스  ID : Txdm6000
 *  변경  이력 :
 * ------------------------------------------------------------------------
 *  작성자      	소속  		    일자            Tag           내용
 * ------------------------------------------------------------------------
 *  최유환       유채널(주)      2014.04.28         %01%         최초작성
 */
package com.uc.bs.cyber.daemon.txdm6000;

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
public class Txdm6000 extends Codm_BaseDaemon {

	private static String govid = "";
	private static String orgcd = "";
	private static String orgdb = "";

    /*생성자*/
	public Txdm6000() throws IOException, Exception {
		super();
		
		
		log = LogFactory.getLog(this.getClass());

		this.loopTerm = 60;	/*   120초 -- 2분 */
		
	}
		
	/**
	 * @param args
	 */
	public static void main(String[] args) {
			
		Txdm6000 Txdm6000;
		ApplicationContext context  = null;
		
		try {
			// Log
			Txdm6000 = new Txdm6000();
			
			CbUtil.setupLog4jConfig(Txdm6000, "log4j.xml");
			
			try {
				String[] xmls = XMLFileReader.getAllFilePathArray(CbUtil.getResourcePath(Txdm6000, "/"), "SERVICE.XML");

				String strSrc = CbUtil.getResourcePath(Txdm6000, "/") + "config/sqlmaps.xml";

				String strDest = CbUtil.getResourcePath(Txdm6000, "/") + "config/sqlmapConfig.xml";

				XmlUtils.setXmlAttributes(Txdm6000, strSrc, strDest, "sqlMapConfig", "sqlMap", "url", xmls);

			} catch (Exception e) {
				
				e.printStackTrace();
			}			
		
			context = new ClassPathXmlApplicationContext("config/Spring-db.xml");
			
			Txdm6000.setProcess("6000", "자동차 저당권 설정/말소 전송", "thr_6000");  /* 업무데몬 등록 */
			
			Txdm6000.setContext(context);
			
			Thread tx6000Thread = new Thread(Txdm6000);
			
			tx6000Thread.setName("thr_6000");
			
			tx6000Thread.start();
			
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
	@Override
	public void mainProcess() throws Exception {
		
        for(int i = 0; i<threadList.length; i++) {
			
			if(!threadList[i].isAlive()) {	// 다시 살려라
				
				log.debug("=====================================================================");
				log.debug("== " + this.getClass().getName()+ " mainProcess(" + i + ") ==");
				log.debug("=====================================================================");
				
				log.debug(" Thread(" + threadList[i].getName() + ") is " + threadList[i].isAlive());
				
				govid = CbUtil.getResource("ApplicationResource", "cyber.tbl.gov" + i);
				orgcd = CbUtil.getResource("ApplicationResource", "cyber." + govid + ".org_cd");
				orgdb = "LT_etax";
				
								
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
		
		int procCnt  = CbUtil.getResourceInt("ApplicationResource", "cyber.tbl.count");
		
		if (log.isDebugEnabled()){
			log.debug("TBL 동작 :: procCnt = " + procCnt);
		}
		
		threadList = new Thread[procCnt];
		
		for(int i = 0; i<procCnt; i++) {
			
			/*
			 * 1. 시청(구청)코드
			 * 2. DB연결정보
			 * 3. Context
			 * */

			govid = CbUtil.getResource("ApplicationResource", "cyber.tbl.gov" + i);
			orgcd = CbUtil.getResource("ApplicationResource", "cyber." + govid + ".org_cd");
			orgdb = "LT_etax";
			
			
			threadList[i] = new Thread(newProcess(govid, orgdb), "sub_6000thr_" + orgcd);			
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
	private Txdm6000_process newProcess(String govId, String orgcd) throws Exception {
		
		Txdm6000_process process = new Txdm6000_process();
		
		process.setContext(appContext);
		process.setGovId(govId);
		process.setDataSource(orgcd);
		
		process.initProcess();		
		
		return process;
	}

}
