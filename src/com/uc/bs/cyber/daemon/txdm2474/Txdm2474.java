/**
 *  주시스템명 : 부산시 사이버지방세청
 *  업  무  명 : 연계
 *  기  능  명 : 주정차위반과태료 고지자료연계
 *               5분단위로 동작...
 *               주정차위반과태료는 별도의 구청서버에 접속하지 않고 시도서버로만 연결한다
 *  클래스  ID : Txdm2474
 *  변경  이력 :
 * ------------------------------------------------------------------------
 *  작성자      	소속  		    일자            Tag           내용
 * ------------------------------------------------------------------------
 *  송동욱       유채널(주)      2011.06.03         %01%         최초작성
 */
package com.uc.bs.cyber.daemon.txdm2474;

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
 */
public class Txdm2474 extends Codm_BaseDaemon {

	private static String govid = "";
	private static String orgcd = "";
	private static String orgdb = "";
	
	/**
	 * 생성자
	 */
	public Txdm2474() throws IOException, Exception {
		// TODO Auto-generated constructor stub
		super();
		
		log = LogFactory.getLog(this.getClass());

		this.loopTerm = 300;	/* 300마다 */
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Txdm2474 Txdm2474;
		ApplicationContext context  = null;
		
		try {
			// Log
			Txdm2474 = new Txdm2474();
			
			CbUtil.setupLog4jConfig(Txdm2474, "log4j.xml");
			
			try {
				String[] xmls = XMLFileReader.getAllFilePathArray(CbUtil.getResourcePath(Txdm2474, "/"), "SERVICE.XML");
				String strSrc = CbUtil.getResourcePath(Txdm2474, "/") + "config/sqlmaps.xml";
				String strDest = CbUtil.getResourcePath(Txdm2474, "/") + "config/sqlmapConfig.xml";
				XmlUtils.setXmlAttributes(Txdm2474, strSrc, strDest, "sqlMapConfig", "sqlMap", "url", xmls);

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
		
			context = new ClassPathXmlApplicationContext("config/Spring-db.xml");
			Txdm2474.setProcess("2474", "주정차위반과태료 고지자료연계", "thr_2474");  /* 업무데몬 등록 */
			Txdm2474.setContext(context);
			Thread tx2474Thread = new Thread(Txdm2474);
			tx2474Thread.setName("thr_2474");
			tx2474Thread.start();
			
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
		
		int procCnt  = CbUtil.getResourceInt("ApplicationResource", "cyber.bnon.count");
		
		if (log.isDebugEnabled()){
			log.debug("procCnt = " + procCnt);
		}
		
		threadList = new Thread[procCnt];
				
		for(int i = 0; i<procCnt; i++) {

			govid = CbUtil.getResource("ApplicationResource", "cyber.bnon.gov" + i); 
			orgcd = CbUtil.getResource("ApplicationResource", "cyber." + govid + ".org_cd");
			
			orgdb = "NI_"+orgcd; /*DB 연결정보*/
			
			//debug mode
			
			threadList[i] = new Thread(newProcess(govid, orgdb), "sub_2474thr_" + orgcd);			
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
				
				govid = CbUtil.getResource("ApplicationResource", "cyber.bnon.gov" + i);
				orgcd = CbUtil.getResource("ApplicationResource", "cyber." + govid + ".org_cd");
				
				orgdb = "NI_"+orgcd; /*DB 연결정보*/
				
				
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
	private Txdm2474_process newProcess(String govId, String orgcd) throws Exception {
		
		Txdm2474_process process = new Txdm2474_process();
		
		process.setContext(appContext);
		process.setGovId(govId);
		process.setDataSource(orgcd);
		
		process.initProcess();		
		
		
		return process;
	}

}
