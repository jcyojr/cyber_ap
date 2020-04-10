/**
 * 
 */
package com.uc.bs.cyber.starter;

import java.text.NumberFormat;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;


import com.uc.core.MapForm;
import com.uc.core.misc.Utils;
import com.uc.core.misc.XMLFileReader;
import com.uc.core.misc.XmlUtils;

import com.uc.core.spring.service.IbatisService;
import com.uc.core.spring.service.UcContextHolder;

import com.uc.bs.cyber.daemon.txdm2511.Txdm2511;
import com.uc.bs.cyber.CbUtil;
/**
 * @author Administrator
 *
 */
public class Codm_DaemonBusan implements Runnable{

	private static IbatisService  singleService = null;
	
	private static String ClsName = "com.uc.bs.cyber.starter.Codm_DaemonBusan";

	private static Log log = LogFactory.getLog(ClsName);
	
	private ApplicationContext  appContext = null;
	private String dataSource  = null;
	
	private static String  procName = "사이버지방세청(BP) 데몬관리";
	private static String  procId   = "0003";
	private static int     loopTerm = 60;
	private static boolean isInitDaemonStart = false;
	private static long    elapseTime = 0;
	
	/**
	 * 
	 */
	public Codm_DaemonBusan() {
		// TODO Auto-generated constructor stub
		
	}
	
	public void setApp(ApplicationContext context, String dataSrc) {
		
		this.appContext = context;
		
		this.dataSource = dataSrc;

	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ApplicationContext context  = null;
		
		Codm_DaemonBusan coDaemonStart = new Codm_DaemonBusan();
		
		/*Log4j 설정*/
		CbUtil.setupLog4jConfig(coDaemonStart, "log4j.bf.xml");
		
		log.info("=======================================");
		log.info("==    CyberTaxSystem(BP) Started     ==");
		log.info("=======================================");
		log.info(System.getProperties().getProperty("java.class.path"));		
		log.info("==  OS INFO = " + System.getProperty("os.name") + System.getProperty("os.version") + System.getProperty("file.separator"));
	
		
		try {
			
			try {
				String[] xmls = XMLFileReader.getAllFilePathArray(Utils.getResourcePath(coDaemonStart, "/"), "SERVICE.XML");

				String strSrc = Utils.getResourcePath(coDaemonStart, "/") + "config/sqlmaps.xml";

				String strDest = Utils.getResourcePath(coDaemonStart, "/") + "config/sqlmapConfig.xml";

				XmlUtils.setXmlAttributes(coDaemonStart, strSrc, strDest, "sqlMapConfig", "sqlMap", "url", xmls);

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
		
			context = new ClassPathXmlApplicationContext("config/Daemon-Spring-db.xml");

			coDaemonStart.setApp(context, "LT_etax");
			
			new Thread(coDaemonStart, "Main Daemon(BP) Administrator").start();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
		UcContextHolder.setCustomerType(this.dataSource);
		
		singleService = (IbatisService) this.appContext.getBean("ibatisService_cyber");
		
		log.info("=======================================");
		log.info("==        Main Thread Started        ==");
		log.info("=======================================");

		/*처음 기준시간 셋팅*/
		elapseTime = System.currentTimeMillis();
		
		/*---------------------------------*/
		/*여기서 부터 업무데몬을 시작시킨다.*/
		/*---------------------------------*/		
		Thread[] daemonThread = null;	
		
		try {
			
			/*----------------------------------------------*/
			/*1. 결제원 소켓통신을 위한 Server.class 를 기동시킨다.*/
			/*----------------------------------------------*/
			Txdm2511 txdm2511 = new Txdm2511(appContext);

			/*----------------------------------------------*/
			/*1. 업무용 데몬을 기동시킨다.                  */
			/*----------------------------------------------*/
			/* 업무 데몬에 Spring 컨텍스트를 주입해준다 */
			daemonThread = new Thread[CbUtil.getResourceInt("ApplicationResource", "cyber.bf.count")];
			
			daemonThread[0] = new Thread((Runnable) txdm2511);
			daemonThread[0].setName("2511");
			daemonThread[0].start();

			isInitDaemonStart = true;
			
			setStateRegist();  /*데몬상태등록*/
			
			/* 업무 데몬에 Spring 컨텍스트를 주입해준다 */
			
			/* 이중화를 체크하기 위한 로직 */
			do{

				try {

					if(!isInitDaemonStart) {
						
						//startDaemon(daemonThread, appContext);
					} else {
						
						//aliveDaemon(daemonThread, appContext);
						stateDaemon(daemonThread, appContext);
					}
					
					
					/*상태정보 등록*/
					if(System.currentTimeMillis() - elapseTime >= 1000*60*5) { // 5분이상 경과됐다면 상태를 UPDATE 한다
						
						setStateRegist();
						
						/* 다시 기준시간을 셋팅한다...*/
						elapseTime = System.currentTimeMillis();
					}

				} catch (Exception e) {
					e.printStackTrace();
					
				} finally {

				}
				
				/*시스템 정보 표시*/
				viewMemory();
				
				Thread.sleep(1000 * loopTerm); /* loopTerm (분) 단위로 체크*/
				
			}while(true);
						
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			log.info("=== DAEMON(" + procId + "::" + procName + ") STATE IS DOWN!!!");
		}		

	}	
	
	private static void stateDaemon(Thread[] adThread, ApplicationContext  context) throws Exception {
		
		StringBuffer sb_key = new StringBuffer();
		StringBuffer sb_msg = new StringBuffer();
		
		for(int i = 0; i<adThread.length; i++) {
			
			sb_msg.append("데몬 ["+ i +"][" + adThread[i].getName() + "] = " + adThread[i].isAlive() + "|");
			sb_key.append("cyber.bf.class" + i);
			sb_key.delete(0, sb_key.length());
			
			sb_key.append("cyber.bf.name" + i);
			String proc_id = CbUtil.getResource("ApplicationResource", sb_key.toString());  /* ApplicationResource.properties 참조하여 명명된 프로그램ID를 찾음 */
			sb_key.delete(0, sb_key.length());
			
			sb_key.append("cyber.bf.title" + i);
			String proc_nm = CbUtil.getResource("ApplicationResource", sb_key.toString());
			sb_key.delete(0, sb_key.length());
			
			if(adThread[i] != null && !adThread[i].isAlive()){
				log.info("====데몬 [" + i + "][" + proc_id + "][" + adThread[i].getName()+ "] 데몬이 기동중지되어 있습니다.");
				log.info("====데몬 [" + i + "][" + proc_id + "][" + proc_nm + " is restrting!!!");
				adThread[i].start();
				
			} else {
				log.info("====데몬 [" + i + "][" + proc_id + "][" + adThread[i].getName()+ "] 데몬이 기동되어 있습니다.");
				log.info("====데몬 [" + i + "][" + proc_id + "][" + proc_nm + " is alive!!!");
			}
			
		}
		
	}
	
	/*
	 * 데몬정보 등록
	 * PROC_ID = "0003" 명명한다.
	 * PROC_NM = "사이버지방세청 결제원송수신 데몬관리"
	 * */
	private static void setStateRegist(){
		
		MapForm sysMap = new MapForm();
				
		sysMap.setMap("SGG_COD"      , "000");                                     /*구청코드 : 부산시(626), 사이버세청(000)*/
		sysMap.setMap("PROCESS_ID"   , procId);                                    /*프로세스 ID*/
		sysMap.setMap("PROCESS_NM"   , procName);                                  /*프로세스 명*/
		sysMap.setMap("THREAD_NM"    , "Main Daemon(BP) Administrator");           /*쓰레드 명*/
		sysMap.setMap("BIGO"         , CbUtil.getServerIp());                      /*비고 : 실행서버IP를 셋팅한다. */
		sysMap.setMap("MANE_STAT"    , "1");                                       /*기동상태*/
		sysMap.setMap("MANAGE_CD"    , "1");                                       /*관리구분 1 : 데몬 */
		sysMap.setMap("STT_DT"       , CbUtil.getCurrent("yyyyMMddHHmmss"));       /*시작일시 */
		sysMap.setMap("END_DT"       , "");                                        /*종료일시*/
		sysMap.setMap("LAST_DTM"     , CbUtil.getCurrent("yyyyMMddHHmmss"));       /*최종수정일시*/
		
		try {
			
			/**
			 * 데몬상태정보 등록
			 */
			log.info("=== DAEMON(" + procId + "::" + procName + ") STATE IS REGISTERING...");
			if(singleService.queryForUpdate("CODMBASE.CODMSYSTUpdate", sysMap) < 1) {
				singleService.queryForInsert("CODMBASE.CODMSYSTInsert", sysMap);
			}	
			log.info("=== DAEMON(" + procId + "::" + procName + ") STATE IS REGISTED.");
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			log.debug("=결제원송수신서버 메인데몬상태 등록중 오류발생 \n" + e.getStackTrace());

		}
		
	}	
	
	private static void viewMemory() {
		
		Runtime runtime = Runtime.getRuntime();

	    NumberFormat format = NumberFormat.getInstance();
   
	    long maxMemory = runtime.maxMemory();
	    long allocatedMemory = runtime.totalMemory();
	    long freeMemory = runtime.freeMemory();

	    log.info("==============================================================");
	    log.info("|free memory:       | [" + format.format(freeMemory / 1024) + "]");
	    log.info("|allocated memory:  | [" + format.format(allocatedMemory / 1024) + "]");
	    log.info("|max memory:        | [" + format.format(maxMemory / 1024) + "]");
	    log.info("|total free memory: | [" + format.format((freeMemory + (maxMemory - allocatedMemory)) / 1024) + "]");
	    log.info("==============================================================");
	    
	    if((allocatedMemory / 1024) > 1500000) {
	    	
	    	//System.runFinalization();
	    	//쓰레기 청소 한번 더...
            //불난집에 기름을 붓는격이라...사용안하는게 낳겠다.
	    }
		
	}
	
}
