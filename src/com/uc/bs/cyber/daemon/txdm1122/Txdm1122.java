/**
 *  주시스템명 : 부산시 사이버지방세청
 *  업  무  명 : 연계
 *  기  능  명 : 가상계좌채번자료 구청별 연계
 *               5분단위로 동작...
 *  클래스  ID : Txdm1122
 *  변경  이력 :
 * ------------------------------------------------------------------------
 *  작성자      	소속  		    일자            Tag           내용 
 * ------------------------------------------------------------------------
 *  황종훈       유채널(주)      2012.03.08         %01%         최초작성
 */
package com.uc.bs.cyber.daemon.txdm1122;

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
public class Txdm1122 extends Codm_BaseDaemon {

	private static String govid = "";
	private static String orgcd = "";
	private static String orgdb = "";

    /*생성자*/
	public Txdm1122() throws IOException, Exception {
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
		
		Txdm1122 Txdm1122;
		ApplicationContext context  = null;
		
		try {
			// Log
			Txdm1122 = new Txdm1122();
			
			CbUtil.setupLog4jConfig(Txdm1122, "log4j.xml");
			
			try {
				String[] xmls = XMLFileReader.getAllFilePathArray(CbUtil.getResourcePath(Txdm1122, "/"), "SERVICE.XML");

				String strSrc = CbUtil.getResourcePath(Txdm1122, "/") + "config/sqlmaps.xml";

				String strDest = CbUtil.getResourcePath(Txdm1122, "/") + "config/sqlmapConfig.xml";

				XmlUtils.setXmlAttributes(Txdm1122, strSrc, strDest, "sqlMapConfig", "sqlMap", "url", xmls);

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
		
			context = new ClassPathXmlApplicationContext("config/Tax-Spring-db.xml");
			
			//context = new ClassPathXmlApplicationContext("config/Snd-Spring-db.xml");
			
			Txdm1122.setProcess("1122", "가상계좌채번자료 전송(test_chun)", "thr_1122");  /* 업무데몬 등록 */
			
			Txdm1122.setContext(context);
			
			Thread tx1122Thread = new Thread(Txdm1122);
			
			tx1122Thread.setName("thr_1122");
			
			tx1122Thread.start();
			
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
		
        //for(int i = 0; i < 1; i++) {
			
			if(!threadList[0].isAlive()) {	// 다시 살려라
				
				log.debug("=====================================================================");
				log.debug("== " + this.getClass().getName()+ " mainProcess(" + 0 + ") ==");
				log.debug("=====================================================================");
				
				log.debug(" Thread(" + threadList[0].getName() + ") is " + threadList[0].isAlive());
				
				/*govid = CbUtil.getResource("ApplicationResource", "cyber.snd.gov" + 16);
				orgcd = CbUtil.getResource("ApplicationResource", "cyber." + govid + ".org_cd");
				orgdb = "JI_" + orgcd;*/
				
				govid = "snd2_dev0";
				orgcd = "710";
				orgdb = "JI_" + "710";
				
				try {
					
					threadList[0] = new Thread(newProcess(govid, orgdb), govid);
				
					threadList[0].start();
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}			
				
			}
			
		//}
        
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
		
		//int procCnt  = CbUtil.getResourceInt("ApplicationResource", "cyber.snd.count");
		
		threadList = new Thread[1];
		
		//for(int i = 0; i < procCnt; i++) {
			
			/*govid = CbUtil.getResource("ApplicationResource", "cyber.snd.gov" + 16); //기장군 16
			log.info("govid=" + govid);
			orgcd = CbUtil.getResource("ApplicationResource", "cyber." + govid + 16 + ".org_cd");
			orgdb = "JI_" + orgcd;*/
		
			govid = "snd2_dev0";
			orgcd = "710";
			orgdb = "JI_" + "710";
						
			
			threadList[0] = new Thread(newProcess(govid, orgdb), "sub_1122thr_" + orgcd);			
			threadList[0].start();
			
		//}
		
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
	private Txdm1122_process newProcess(String govId, String orgcd) throws Exception {
		
		Txdm1122_process process = new Txdm1122_process();
		
		process.setContext(appContext);
		process.setGovId(govId);
		process.setDataSource(orgcd);
		
		process.initProcess();		
		
		return process;
	}

}
