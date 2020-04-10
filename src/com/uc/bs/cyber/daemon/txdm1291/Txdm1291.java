/**
 *  주시스템명 : 부산시 사이버지방세청
 *  업  무  명 : 연계
 *  기  능  명 : 수기고지서 주민세 법인세할 수납내역 등록(구청별) 연계 메인 
 *     
 *               5분단위로 동작...
 *  클래스  ID : Txdm1274
 *  사용  여부 : 
 *  변경  이력 :
 * ------------------------------------------------------------------------
 *  작성자      	소속  		    일자            Tag           내용
 * ------------------------------------------------------------------------
 *  송동욱       유채널(주)      2011.12.07         %01%         최초작성
 */
package com.uc.bs.cyber.daemon.txdm1291;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.dao.DuplicateKeyException;

import com.uc.bs.cyber.CbUtil;
import com.uc.bs.cyber.daemon.Codm_BaseDaemon;
import com.uc.core.MapForm;
import com.uc.core.misc.XMLFileReader;
import com.uc.core.misc.XmlUtils;

/**
 * @author Administrator
 *
 */
public class Txdm1291 extends Codm_BaseDaemon{

	private static String govid = "";
	private static String orgcd = "";
	private static String orgdb = "";
	
	private int mainloop = 0;
	
	private int loop_cnt = 0, insert_cnt = 0, update_cnt = 0;
	
	private String soinFilePath = "";
	private String moveFilePath = "";
	
	ArrayList<MapForm> alSoinList;
	
	/**
	 * 생성자
	 */
	public Txdm1291() throws IOException, Exception {
		
		super();

		log = LogFactory.getLog(this.getClass());

		this.loopTerm = 250;	
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Txdm1291 Txdm1291;
		ApplicationContext context  = null;
		
		System.out.println("== OS Name = " + System.getProperty("os.name"));
		System.out.println("== OS Version = " + System.getProperty("os.version"));
		
		try {
			
			// Log
			Txdm1291 = new Txdm1291();
			
			CbUtil.setupLog4jConfig(Txdm1291, "log4j.xml");
			
			try {
				
				String[] xmls = XMLFileReader.getAllFilePathArray(CbUtil.getResourcePath(Txdm1291, "/"), "SERVICE.XML");

				String strSrc = CbUtil.getResourcePath(Txdm1291, "/") + "config/sqlmaps.xml";

				String strDest = CbUtil.getResourcePath(Txdm1291, "/") + "config/sqlmapConfig.xml";

				XmlUtils.setXmlAttributes(Txdm1291, strSrc, strDest, "sqlMapConfig", "sqlMap", "url", xmls);

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
		
			context = new ClassPathXmlApplicationContext("config/Tax-Spring-db.xml");
			
			Txdm1291.setProcess("1274", "수기고지서 주민세 법인세할 수납내역등록(구청별)", "thr_1274");  /* 업무데몬 등록 */
			
			Txdm1291.setContext(context);
			
			Thread tx1274Thread = new Thread(Txdm1291);
			
			tx1274Thread.setName("thr_1274");
			
			tx1274Thread.start();
			
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
		
		/*초기 실행시 
		 * 1. 특별징수 소인파일을 읽어서 DB에 등록한다.
		 * 2. 특별징수 내역을 구청별로 등록한다.(멀티쓰레드)
		 * */
	
		
		/*2. 소인파일 전송(구청별)*/
		int procCnt  = CbUtil.getResourceInt("ApplicationResource", "cyber.hv.count");
		
		if (log.isDebugEnabled()){
			log.debug("기동프로세스 갯수 :: procCnt = " + procCnt);
		}
		
		threadList = new Thread[procCnt];
		
		for(int i = 0; i<procCnt; i++) {
			
			/*
			 * 1. 시청(구청)코드
			 * 2. DB연결정보
			 * 3. Context
			 * 4. 설명
			 *    소인파일을 읽어서 구청별로 입력한다.
			 *    소인파일은 구청별로 생성되지 않고 세목에 따라 하나의 파일만 생성됨
			 *    수기고지서 특별징수 소인파일
			 * */

			govid = CbUtil.getResource("ApplicationResource", "cyber.hv.gov" + i);
			orgcd = CbUtil.getResource("ApplicationResource", "cyber." + govid + ".org_cd");
			orgdb = "JI_" + orgcd;
			
			threadList[i] = new Thread(newProcess(govid, orgdb), "sub_1272thr_" + orgcd);			
			threadList[i].start();
			
		}
	}

	/*10 초 단위로 thick */
	@Override
	public void mainProcess() throws Exception {
		// TODO Auto-generated method stub
		
		mainloop++;
		
		for(int i = 0; i < threadList.length; i++) {
			
			if(!threadList[i].isAlive()) {	// 다시 살려라
				
				log.debug("=====================================================================");
				log.debug("== " + this.getClass().getName()+ " mainProcess(" + i + ") ==");
				log.debug("=====================================================================");
				
				log.debug(" Thread(" + threadList[i].getName() + ") is " + threadList[i].isAlive());
				
				govid = CbUtil.getResource("ApplicationResource", "cyber.hv.gov" + i);
				orgcd = CbUtil.getResource("ApplicationResource", "cyber." + govid + ".org_cd");
				orgdb = "JI_" + orgcd;
				
				try {
					threadList[i] = new Thread(newProcess(govid, orgdb), "sub_1271thr_" + orgcd);		
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
	private Txdm1291_process newProcess(String govId, String orgcd) throws Exception {
		
		Txdm1291_process process = new Txdm1291_process();
		
		process.setContext(appContext);
		process.setGovId(govId);
		process.setDataSource(orgcd);
		
		process.initProcess();		
		
		return process;
	}


}
