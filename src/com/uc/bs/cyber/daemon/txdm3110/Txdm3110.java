
package com.uc.bs.cyber.daemon.txdm3110;

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
public class Txdm3110 extends Codm_BaseDaemon{

	private static String govid = "";
	private static String orgcd = "";
	private static String orgdb = "";
	
	/**
	 * 생성자
	 */
	public Txdm3110() throws IOException, Exception {
		
		super();

		log = LogFactory.getLog(this.getClass());

		this.loopTerm = 300;	/* 300초마다 */
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Txdm3110 Txdm3110;
		ApplicationContext context  = null;
		
		try {
			// Log
			Txdm3110 = new Txdm3110();
			
			CbUtil.setupLog4jConfig(Txdm3110, "log4j.xml");
			
			try {
				String[] xmls = XMLFileReader.getAllFilePathArray(CbUtil.getResourcePath(Txdm3110, "/"), "SERVICE.XML");

				String strSrc = CbUtil.getResourcePath(Txdm3110, "/") + "config/sqlmaps.xml";

				String strDest = CbUtil.getResourcePath(Txdm3110, "/") + "config/sqlmapConfig.xml";

				XmlUtils.setXmlAttributes(Txdm3110, strSrc, strDest, "sqlMapConfig", "sqlMap", "url", xmls);

			} catch (Exception e) {
				
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
		
			
			context = new ClassPathXmlApplicationContext("config/Tax-Spring-db.xml");
			
			Txdm3110.setProcess("3110", "지방세정기분 부가자료 자료연계(구청별)", "thr_3110");  /* 업무데몬 등록 */
			
			Txdm3110.setContext(context);
			
			Thread tx3110Thread = new Thread(Txdm3110);
			
			tx3110Thread.setName("thr_3110");
			
			tx3110Thread.start();
			
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
		
		int procCnt  = CbUtil.getResourceInt("ApplicationResource", "cyber.tax.count");
		
		if (log.isDebugEnabled()){
			log.debug("기동프로세스 갯수 :: procCnt = " + procCnt);
		}
		
		threadList = new Thread[procCnt];
		
		for(int i = 0; i < procCnt; i++) {
			
			/*
			 * 1. 시청(구청)코드
			 * 2. DB연결정보
			 * 3. Context
			 * */

			govid = CbUtil.getResource("ApplicationResource", "cyber.tax.gov" + i);
			orgcd = CbUtil.getResource("ApplicationResource", "cyber." + govid + ".org_cd");
			orgdb = "JI_"+ orgcd;
			
			log.info("orgdb     " + orgdb);
			
			threadList[i] = new Thread(newProcess(govid, orgdb), "sub_3110thr_" + orgcd);			
			threadList[i].start();
		}
	}

	@Override
	public void mainProcess() throws Exception {
		// TODO Auto-generated method stub
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
	private Txdm3110_process newProcess(String govId, String orgcd) throws Exception {
		
		Txdm3110_process process = new Txdm3110_process();
		
		process.setContext(appContext);
		process.setGovId(govId);
		process.setDataSource(orgcd);
		
		process.initProcess();		
		
		return process;
	}
}
