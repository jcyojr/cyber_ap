/**
 *  주시스템명 : 부산시청 사이버지방세청
 *  업  무  명 : 
 *  기  능  명 :  충당수납
 *  클래스  ID : Txbt5000.Java
 *  변경 이력 : 
 * ------------------------------------------------------------------------
 *  작성자      	소속  		    일자            Tag           내용
 * ------------------------------------------------------------------------
 *  김범준        유채널       2012.04.27    %01%      최초작성
 */


package com.uc.bs.cyber.batch.txbt5000;

import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.uc.bs.cyber.batch.Txdm_BatchProcess;
import com.uc.core.misc.XMLFileReader;
import com.uc.core.misc.XmlUtils;
import com.uc.bs.cyber.CbUtil;

/**
 * @author Administrator
 *
 */
public class Txbt5000 extends Txdm_BatchProcess {

	private static String govid = "";
	private static String orgcd = "";
	private static String orgdb = "";
	private Thread[] threadList;
	

/**
 * 
 */
	public Txbt5000() {
		// TODO Auto-generated constructor stub
		super();
		
		log = LogFactory.getLog(this.getClass());
		
	}

	
	/**
	 * @param args
	 */
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		Txbt5000 batch;
		batch = new Txbt5000();
		ApplicationContext context  = null;
		
		try {

			// Log
			CbUtil.setupLog4jConfig(batch, "log4j.xml");
			
			try {
				String[] xmls = XMLFileReader.getAllFilePathArray(CbUtil.getResourcePath(batch, "/"), "SERVICE.XML");
				String strSrc = CbUtil.getResourcePath(batch, "/") + "config/sqlmaps.xml";
				String strDest = CbUtil.getResourcePath(batch, "/") + "config/sqlmapConfig.xml";
				XmlUtils.setXmlAttributes(batch, strSrc, strDest, "sqlMapConfig", "sqlMap", "url", xmls);

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
		
			context = new ClassPathXmlApplicationContext("config/Tax-Spring-db.xml");
			
			batch.setProcess("5000", "충당수납연계 (구청별)", "thr_5000");		/* 업무배치 등록 */
			
			batch.setContext(context);
			
			Thread tx5000Thread = new Thread(batch);
			
			tx5000Thread.setName("thr_5000");
			
			tx5000Thread.start();
			
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
		
		int procCnt  = CbUtil.getResourceInt("ApplicationResource", "cyber.hv.count");
		
		//Test (1개만 Count)
		procCnt = 1;
		 
		
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

			govid = CbUtil.getResource("ApplicationResource", "cyber.hv.gov" + i);
			orgcd = CbUtil.getResource("ApplicationResource", "cyber." + govid + ".org_cd");
			orgdb = "JI_"+ orgcd;
			
			log.info("orgdb     " + orgdb);
			
			threadList[i] = new Thread(newProcess(govid, orgdb), "sub_5000thr_" + orgcd);			
			threadList[i].start();
			
		}
	}

	public void mainProcess() throws Exception {
		// TODO Auto-generated method stub
		for(int i = 0; i < threadList.length; i++) {
			
			// 재기동
			if(!threadList[i].isAlive()) {	
				
				log.debug("=====================================================================");
				log.debug("== " + this.getClass().getName()+ " MainProcess(" + i + ") ==");
				log.debug("=====================================================================");
				
				log.debug(" Thread(" + threadList[i].getName() + ") is " + threadList[i].isAlive());
				
				govid = CbUtil.getResource("ApplicationResource", "cyber.hv.gov" + i);
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
	private Txbt5000_process newProcess(String govId, String orgcd) throws Exception {
		
		Txbt5000_process process = new Txbt5000_process();
		
		process.setContext(appContext);
		process.setGovId(govId);
		process.setDataSource(orgcd);
		
		process.initProcess();		
		
		return process;
	}


	@Override
	public void runProcess() throws Exception {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void setDatasrc(String datasrc) {
		// TODO Auto-generated method stub
		
	}

		

}
