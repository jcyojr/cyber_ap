/**
 *  주시스템명 : 부산시 사이버지방세청
 *  업  무  명 : 연계
 *  기  능  명 : 지방세수납요약정보(정기분중에서 납기내 수납된 자료)
 *               5분단위로 동작...
 *  클래스  ID : Txdm1152
 *  변경  이력 :
 * ------------------------------------------------------------------------
 *  작성자      	소속  		    일자            Tag           내용
 * ------------------------------------------------------------------------
 *  황종훈       유채널(주)      2012.03.08         %01%         최초작성
 */
package com.uc.bs.cyber.daemon.txdm1152;

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
public class Txdm1152 extends Codm_BaseDaemon {

	private static String govid = "";
	private static String orgcd = "";
	private static String orgdb = "";

    /*생성자*/
	public Txdm1152() throws IOException, Exception {
		super();
		// TODO Auto-generated constructor stub
		
		log = LogFactory.getLog(this.getClass());

		this.loopTerm = 300;	/* 300초마다 */
		
	}
		
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		Txdm1152 Txdm1152;
		ApplicationContext context  = null;
		
		try {
			// Log
			Txdm1152 = new Txdm1152();
			
			CbUtil.setupLog4jConfig(Txdm1152, "log4j.xml");
			
			try {
				String[] xmls = XMLFileReader.getAllFilePathArray(CbUtil.getResourcePath(Txdm1152, "/"), "SERVICE.XML");

				String strSrc = CbUtil.getResourcePath(Txdm1152, "/") + "config/sqlmaps.xml";

				String strDest = CbUtil.getResourcePath(Txdm1152, "/") + "config/sqlmapConfig.xml";

				XmlUtils.setXmlAttributes(Txdm1152, strSrc, strDest, "sqlMapConfig", "sqlMap", "url", xmls);

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
		
			context = new ClassPathXmlApplicationContext("config/Tax-Spring-db.xml");
			
			Txdm1152.setProcess("1152", "지방세수납요약정보 ", "thr_1152");  /* 업무데몬 등록 */
			
			Txdm1152.setContext(context);
			
			Thread tx1152Thread = new Thread(Txdm1152);
			
			tx1152Thread.setName("thr_1152");
			
			tx1152Thread.start();
			
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
		
        for(int i = 0; i < threadList.length; i++) {
			
			if(!threadList[i].isAlive()) {	// 다시 살려라
				
				log.debug("=====================================================================");
				log.debug("== " + this.getClass().getName()+ " mainProcess(" + i + ") ==");
				log.debug("=====================================================================");
				
				log.debug(" Thread(" + threadList[i].getName() + ") is " + threadList[i].isAlive());
				
				govid = CbUtil.getResource("ApplicationResource", "cyber.tax.gov" + i);
				orgcd = CbUtil.getResource("ApplicationResource", "cyber." + govid + ".org_cd");
				orgdb = "JI_" + orgcd;

				if(orgcd.equals("000")||orgcd.equals("042")) continue;

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
		
		int procCnt  = CbUtil.getResourceInt("ApplicationResource", "cyber.tax.count");

		threadList = new Thread[procCnt-1];
		
		for(int i = 0; i < procCnt; i++) {
			
			govid = CbUtil.getResource("ApplicationResource", "cyber.tax.gov" + i);
			orgcd = CbUtil.getResource("ApplicationResource", "cyber." + govid + ".org_cd");
			orgdb = "JI_" + orgcd;

			if(orgcd.equals("000")||orgcd.equals("042")) continue;

			threadList[i] = new Thread(newProcess(govid, orgdb), "sub_1152thr_" + orgcd);			
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
	private Txdm1152_process newProcess(String govId, String orgcd) throws Exception {
		
		Txdm1152_process process = new Txdm1152_process();
		
		process.setContext(appContext);
		process.setGovId(govId);
		process.setDataSource(orgcd);
		
		process.initProcess();		
		
		return process;
	}

}
