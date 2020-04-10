/**
 *  주시스템명 : 부산시청 사이버지방세청
 *  업  무  명 : 
 *  기  능  명 :  충당수납
 *  클래스  ID : Txdm5000.Java
 *  변경 이력 : 
 * ------------------------------------------------------------------------
 *  작성자      	소속  		    일자            Tag           내용
 * ------------------------------------------------------------------------
 *  김범준        유채널       2012.04.05    %01%      최초작성
 */


package com.uc.bs.cyber.daemon.txdm5000;

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
public class Txdm5000 extends Codm_BaseDaemon {

	private static String govid = "";
	private static String orgcd = "";
	private static String orgdb = "";
	
	/**
	 * 생성자
	 */
	public Txdm5000() throws IOException, Exception {
		
		super();

		log = LogFactory.getLog(this.getClass());

		this.loopTerm = 300 * 4;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Txdm5000 Txdm5000;
		ApplicationContext context  = null;
		
		try {
			// Log
			Txdm5000 = new Txdm5000();
			
			CbUtil.setupLog4jConfig(Txdm5000, "log4j.xml");
			
			try {
				String[] xmls = XMLFileReader.getAllFilePathArray(CbUtil.getResourcePath(Txdm5000, "/"), "SERVICE.XML");

				String strSrc = CbUtil.getResourcePath(Txdm5000, "/") + "config/sqlmaps.xml";

				String strDest = CbUtil.getResourcePath(Txdm5000, "/") + "config/sqlmapConfig.xml";

				XmlUtils.setXmlAttributes(Txdm5000, strSrc, strDest, "sqlMapConfig", "sqlMap", "url", xmls);

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
		
			context = new ClassPathXmlApplicationContext("config/Tax-Spring-db.xml");
			
			Txdm5000.setProcess("5000", "충당수납연계 (구청별)", "thr_5000");		/* 업무데몬 등록 */
			
			Txdm5000.setContext(context);
			
			Thread tx5000Thread = new Thread(Txdm5000);
			
			tx5000Thread.setName("thr_5000");
			
			tx5000Thread.start();
			
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
		
		int procCnt  = CbUtil.getResourceInt("ApplicationResource", "cyber.app.count");
		
		if (log.isDebugEnabled()){
			log.debug("Active Process Number :: procCnt = " + procCnt);
		}
		
		threadList = new Thread[procCnt];
		
		for(int i = 0; i<procCnt; i++) {
			
			/*
			 * 1. 시청(구청)코드
			 * 2. DB연결정보
			 * 3. Context
			 * */

			govid = CbUtil.getResource("ApplicationResource", "cyber.app.gov" + i);
			orgcd = CbUtil.getResource("ApplicationResource", "cyber." + govid + ".org_cd");
			orgdb = "JI_"+ orgcd;
			
			threadList[i] = new Thread(newProcess(govid, orgdb), "sub_5000thr_" + orgcd);			
			threadList[i].start();
			
		}
	}

	@Override
	public void mainProcess() throws Exception {
		// TODO Auto-generated method stub
		for(int i = 0; i<threadList.length; i++) {
			
			// 재기동
			if(!threadList[i].isAlive()) {	
				
				log.debug("=====================================================================");
				log.debug("== " + this.getClass().getName()+ " MainProcess(" + i + ") ==");
				log.debug("=====================================================================");
				
				log.debug(" Thread(" + threadList[i].getName() + ") is " + threadList[i].isAlive());
				
				govid = CbUtil.getResource("ApplicationResource", "cyber.app.gov" + i);
				orgcd = CbUtil.getResource("ApplicationResource", "cyber." + govid + ".org_cd");
				orgdb = "JI_"+ orgcd;
				
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
	 * @param govId
	 * @return
	 * @throws Exception
	 * 메인 로직...을 호출한다.
	 */
	private Txdm5000_process newProcess(String govId, String orgcd) throws Exception {
		
		Txdm5000_process process = new Txdm5000_process();
		
		process.setContext(appContext);
		process.setGovId(govId);
		process.setDataSource(orgcd);
		
		process.initProcess();		
		
		return process;
	}
}
