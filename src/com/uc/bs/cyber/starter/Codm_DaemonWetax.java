/*
 * 데몬 관리 데몬
 * 위택스관련 데몬만...
 * 
 * */
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

import com.uc.bs.cyber.daemon.Codm_interface;
import com.uc.bs.cyber.daemon.txdm2520.Txdm2520;
import com.uc.bs.cyber.daemon.txdm2530.Txdm2530;
import com.uc.bs.cyber.daemon.txdm2540.Txdm2540;
import com.uc.bs.cyber.etax.net.RcvWorker;
import com.uc.bs.cyber.CbUtil;

public class Codm_DaemonWetax implements Runnable{
	
	private static IbatisService  singleService = null;
	
	private static String ClsName = "com.uc.bs.cyber.starter.Codm_DaemonWetax";

	private static Log log = LogFactory.getLog(ClsName);
	
	private ApplicationContext  appContext = null;
	private String dataSource  = null;
	
	private static String  procName = "사이버지방세청(WeTax) 데몬관리";
	private static String  procId   = "0002";
	private static int     loopTerm = 60;
	private static boolean isInitDaemonStart = false;
	private static long    elapseTime = 0;
	
	public Codm_DaemonWetax() {
		// TODO Auto-generated constructor stub
		log.debug("nothing...");
	}
	

	/**
	 * 
	 * @param context
	 * @param dataSrc
	 */
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
		
		Codm_DaemonWetax coDaemonStart = new Codm_DaemonWetax();
		
		/*Log4j 설정*/
		CbUtil.setupLog4jConfig(coDaemonStart, "log4j.wetax.xml");
		
		log.info("=======================================");
		log.info("==  CyberTaxSystem(WeTax) Started    ==");
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
			
			new Thread(coDaemonStart, "Main Daemon(Wetax) Administrator").start();

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
		
		setStateRegist();  /*데몬상태등록*/
		
		/*---------------------------------*/
		/*여기서 부터 업무데몬을 시작시킨다.*/
		/*---------------------------------*/		
		Thread[] daemonThread = null;	
		
		try {
			
			/*----------------------------------------------*/
			/*1. 결제원 소켓통신을 위한 Server.class 를 기동시킨다.*/
			/*----------------------------------------------*/
			Txdm2520 txdm2520 = new Txdm2520(appContext);
			Txdm2530 txdm2530 = new Txdm2530(appContext);
			Txdm2540 txdm2540 = new Txdm2540(appContext);

			/*----------------------------------------------*/
			/*1. 업무용 데몬을 기동시킨다.                  */
			/*----------------------------------------------*/
			/* 업무 데몬에 Spring 컨텍스트를 주입해준다 */
			daemonThread = new Thread[CbUtil.getResourceInt("ApplicationResource", "cyber.wetax.count")];
		
			daemonThread[0] = new Thread((Runnable) txdm2520);
			daemonThread[0].setName("2520");
			daemonThread[0].start();
			
			daemonThread[1] = new Thread((Runnable) txdm2530);
			daemonThread[1].setName("2530");
			daemonThread[1].start();
			
			daemonThread[2] = new Thread((Runnable) txdm2540);
			daemonThread[2].setName("2540");
			daemonThread[2].start();

			isInitDaemonStart = true;
			
			/* 업무 데몬에 Spring 컨텍스트를 주입해준다 */
			
			/* 이중화를 체크하기 위한 로직 */
			do{

				try {

					if(!isInitDaemonStart) {
						
						//startDaemon(daemonThread, appContext);
					} else {
						
						stateDaemon(daemonThread, appContext);
					}

					/*상태정보 등록*/
					if(System.currentTimeMillis() - elapseTime >= 1000*60*5) { // 5분이상 경과됐다면 상태를 UPDATE 한다
						
						//setStateRegist();  //2016.06.20 상태 UPDATE 할 때 소요시간이 오래걸리는 경우 비정상처리 되는 경우가 있어 주석처리 함
						
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

	/*
	 * 송수신데몬 기동
	 * */
	private static void startDaemon(Thread[] dThread, ApplicationContext  context) throws Exception {
		
		log.info("========================데몬 기동 시작========================");
		
		StringBuffer sb_key = new StringBuffer();
		
		for(int i=0; i<dThread.length; i++) {
			
			sb_key.append("cyber.wetax.class" + i);
			
			RcvWorker daemon =  (RcvWorker)Class.forName(CbUtil.getResource("ApplicationResource", sb_key.toString())).newInstance();		
			
			daemon.setContext(context);
			
			sb_key.delete(0, sb_key.length());
			
			/*
			 * 관리데몬임...여기는....
			 * daemon.initProcess();
			 * */
			dThread[i] = new Thread((Runnable) daemon);
			
			sb_key.append("cyber.wetax.name" + i);
			
			String proc_id = CbUtil.getResource("ApplicationResource", sb_key.toString());
			
			sb_key.delete(0, sb_key.length());
			
			if(daemon instanceof RcvWorker) {
			
				sb_key.append("cyber.wetax.title" + i);
				String proc_nm = CbUtil.getResource("ApplicationResource", sb_key.toString());
				sb_key.delete(0, sb_key.length());
								
				log.info("====데몬 [" + i + "][" + proc_id + "][" + proc_nm + " starting!!!");
				
				dThread[i].setName(proc_id);
				dThread[i].start();
			}
			
		}
		
		isInitDaemonStart = true;
		
		log.info("========================데몬 기동 끝 =========================");
	}	
	
	/**
	 * 정상적으로 동작하는 AP에서 관리하는 데몬 중 죽은놈이 있다면
	 * 죽은 데몬을 다시 살려준다...
	 * */
	@SuppressWarnings("unused")
	private static void aliveDaemon(Thread[] adThread, ApplicationContext  context) throws Exception {
		
		StringBuffer sb_key = new StringBuffer();
		StringBuffer sb_msg = new StringBuffer();

		for(int i = 0; i<adThread.length; i++) {
			
			if(adThread[i] != null && !adThread[i].isAlive()){
				
				sb_msg.append("데몬 ["+ i +"][" + adThread[i].getName() + "] = " + adThread[i].isAlive() + "|");
				sb_key.append("cyber.wetax.class" + i);
				
				Codm_interface daemon = (Codm_interface) Class.forName(CbUtil.getResource("ApplicationResource", sb_key.toString())).newInstance();		
				
				daemon.setContext(context); 
				sb_key.delete(0, sb_key.length());

				adThread[i] = new Thread((Runnable) daemon);
			
				sb_key.append("cyber.wetax.name" + i);
				String proc_id = CbUtil.getResource("ApplicationResource", sb_key.toString());  /* ApplicationResource.properties 참조하여 명명된 프로그램ID를 찾음 */
				sb_key.delete(0, sb_key.length());

				if(daemon instanceof RcvWorker) {
					
					sb_key.append("cyber.wetax.title" + i);
					String proc_nm = CbUtil.getResource("ApplicationResource", sb_key.toString());
					sb_key.delete(0, sb_key.length());
										
					log.info("====데몬 [" + i + "][" + proc_id + "][" + adThread[i].getName()+ "] 데몬이 기동중지되어 있어 다시기동합니다...");
					log.info("====데몬 [" + i + "][" + proc_id + "][" + proc_nm + " restarting!!!");
					
				}
				
				adThread[i].setName(proc_id);
				adThread[i].start();
				
			}else{
				/*
				 * DB의 데몬상태가 초기화 되지 않고 재 실행된 경우...
				 * 데몬 초기 기동시 관리데몬이 전 혀 실행 되지 않은 경우 해당
				 * */
				if (!isInitDaemonStart) {
					startDaemon(adThread, context);
				}
				
			}

		}
		
		log.info(sb_msg.toString());
	
	}
	
	private static void stateDaemon(Thread[] adThread, ApplicationContext  context) throws Exception {
		
		StringBuffer sb_key = new StringBuffer();
		StringBuffer sb_msg = new StringBuffer();
		
		for(int i = 0; i<adThread.length; i++) {
			
			sb_msg.append("데몬 ["+ i +"][" + adThread[i].getName() + "] = " + adThread[i].isAlive() + "|");
			sb_key.append("cyber.wetax.class" + i);
			sb_key.delete(0, sb_key.length());
			
			sb_key.append("cyber.wetax.name" + i);
			String proc_id = CbUtil.getResource("ApplicationResource", sb_key.toString());  /* ApplicationResource.properties 참조하여 명명된 프로그램ID를 찾음 */
			sb_key.delete(0, sb_key.length());
			
			sb_key.append("cyber.wetax.title" + i);
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
	 * PROC_ID = "0000" 명명한다.
	 * PROC_NM = "사이버지방세청 결제원송수신 데몬관리"
	 * */
	private static void setStateRegist(){
		
		MapForm sysMap = new MapForm();
				
		sysMap.setMap("SGG_COD"      , "000");                                     /*구청코드 : 부산시(626), 사이버세청(000)*/
		sysMap.setMap("PROCESS_ID"   , procId);                                    /*프로세스 ID*/
		sysMap.setMap("PROCESS_NM"   , procName);                                  /*프로세스 명*/
		sysMap.setMap("THREAD_NM"    , "Main Daemon(Wetax) Administrator");        /*쓰레드 명*/
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

	    }
		
	}

}