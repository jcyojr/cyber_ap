/**
 *  주시스템명 : 부산시 사이버지방세청
 *  업  무  명 : 연계
 *  기  능  명 : 상수도 정기분 부가 자료 연계 메인 
 *     
 *               5분단위로 동작...
 *  클래스  ID : Txdm4311
 *  사용  여부 : 현재는 테스트로 사용하고 만약을 위하여 주 프로세스기 기동하지 않는 경우를 대비.
 *  변경  이력 :
 * ------------------------------------------------------------------------
 *  작성자      	소속  		    일자            Tag           내용
 * ------------------------------------------------------------------------
 *  송동욱       유채널(주)      2011.10.12         %01%         최초작성
 */
package com.uc.bs.cyber.daemon.txdm4311;

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
public class Txdm4311 extends Codm_BaseDaemon{

	private static String govid = "";
	private static String orgcd = "";
	private static String orgdb = "";
	
	/**
	 * 생성자
	 */
	public Txdm4311() throws IOException, Exception {
		
		super();

		log = LogFactory.getLog(this.getClass());

		this.loopTerm = 300;	/* 300초마다 */
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Txdm4311 Txdm4311;
		ApplicationContext context  = null;
		
		try {
			// Log
			Txdm4311 = new Txdm4311();
			
			CbUtil.setupLog4jConfig(Txdm4311, "log4j.xml");
			
			try {
				String[] xmls = XMLFileReader.getAllFilePathArray(CbUtil.getResourcePath(Txdm4311, "/"), "SERVICE.XML");

				String strSrc = CbUtil.getResourcePath(Txdm4311, "/") + "config/sqlmaps.xml";

				String strDest = CbUtil.getResourcePath(Txdm4311, "/") + "config/sqlmapConfig.xml";

				XmlUtils.setXmlAttributes(Txdm4311, strSrc, strDest, "sqlMapConfig", "sqlMap", "url", xmls);

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
		
			context = new ClassPathXmlApplicationContext("config/Tax-Spring-db.xml");
			
			Txdm4311.setProcess("4311", "상수도 정기분 부가자료 자료연계(시도)", "thr_4311");  /* 업무데몬 등록 */
			
			Txdm4311.setContext(context);
			
			Thread tx4311Thread = new Thread(Txdm4311);
			
			tx4311Thread.setName("thr_4311");
			
			tx4311Thread.start();
			
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
		
		int procCnt  = CbUtil.getResourceInt("ApplicationResource", "cyber.ht.count");
		
		if (log.isDebugEnabled()){
			log.debug("기동프로세스 갯수 :: procCnt = " + procCnt);
		}
		
		threadList = new Thread[procCnt];
		
		for(int i = 0; i<procCnt; i++) {
			
			/*
			 * 1. 상수도 사업본부 코드
			 * 2. DB연결정보
			 * 3. Context
			 * 4. 시도 상수도 서버이므로 시도서버로만 연결하여 연계하고
			 *    구청이 없으므로 단일 쓰레드로 연결하고 sub thread 에서 멀티로 연계한다.
			 *    
			 * */

			govid = CbUtil.getResource("ApplicationResource", "cyber.st.gov" + i);
			orgcd = CbUtil.getResource("ApplicationResource", "cyber." + govid + ".org_cd");
			orgdb = "SI_000";
			
			threadList[i] = new Thread(newProcess(govid, orgdb), "sub_4311thr_" + orgcd);			
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
				
				govid = CbUtil.getResource("ApplicationResource", "cyber.st.gov" + i);
				orgcd = CbUtil.getResource("ApplicationResource", "cyber." + govid + ".org_cd");
				orgdb = "SI_000";
				
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
	private Txdm4311_process newProcess(String govId, String orgcd) throws Exception {
		
		Txdm4311_process process = new Txdm4311_process();
		
		process.setContext(appContext);
		process.setGovId(govId);
		process.setDataSource(orgcd);
		
		process.initProcess();		
		
		return process;
	}

}
