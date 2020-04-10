/*
 * 데몬 관리 데몬 (상수도연계)
 * 
 * */
package com.uc.bs.cyber.starter;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.NumberFormat;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.uc.core.MapForm;
import com.uc.core.misc.Utils;
import com.uc.core.misc.XMLFileReader;
import com.uc.core.misc.XmlUtils;
import com.uc.core.spring.service.IbatisBaseService;
import com.uc.core.spring.service.IbatisService;
import com.uc.core.spring.service.UcContextHolder;

import com.uc.bs.cyber.etax.net.RcvWorker;
import com.uc.bs.cyber.daemon.Codm_BaseDaemon;
import com.uc.bs.cyber.daemon.Codm_interface;
import com.uc.bs.cyber.CbUtil;

public class Codm_SudoStart implements Runnable{
	
	
	private static String ClsName = "com.uc.bs.cyber.starter.Codm_SudoStart";

	private static IbatisBaseService  sqlService_cyber = null;
	private static IbatisService  singleService = null;
	
	private static Log log = LogFactory.getLog(ClsName);
	
	private ApplicationContext  appContext = null;
	private String dataSource  = null;
	
	private static String  procName = "상수도 연계데몬";
	private static String  procId   = "0006";
	private static int     loopTerm = 60;
	private static long    elapseTime = 0;
	private static boolean isInitDaemonStart = false;
	
	private static MapForm sysMap = new MapForm();

	public Codm_SudoStart() {
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
		
		Codm_SudoStart coTransStart = new Codm_SudoStart();
		
		/*Log4j 설정*/
		CbUtil.setupLog4jConfig(coTransStart, "log4j.sudo.xml");
		
		/*
		log.info("=======================================");
		log.info("==      SudoSystem Started       ==");
		log.info("=======================================");
		log.info(System.getProperties().getProperty("java.class.path"));		
		log.info("==  OS INFO = " + System.getProperty("os.name") + System.getProperty("os.version") + System.getProperty("file.separator"));
		*/
		
		long elapseTime1 = 0;
		long elapseTime2 = 0;
		
		try {
			
			elapseTime1 = System.currentTimeMillis();
			
			try {
				String[] xmls = XMLFileReader.getAllFilePathArray(Utils.getResourcePath(coTransStart, "/"), "SERVICE.XML");

				String strSrc = Utils.getResourcePath(coTransStart, "/") + "config/sqlmaps.xml";

				String strDest = Utils.getResourcePath(coTransStart, "/") + "config/sqlmapConfig.xml";

				XmlUtils.setXmlAttributes(coTransStart, strSrc, strDest, "sqlMapConfig", "sqlMap", "url", xmls);

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
			
			context = new ClassPathXmlApplicationContext("config/Sudo-Spring-db.xml");
			
			coTransStart.setApp(context, "LT_etax");
			
			elapseTime2 = System.currentTimeMillis();
			
			log.info("연계메인 데몬 DB체크 시간 : " + CbUtil.formatTime(elapseTime2 - elapseTime1));
			
			
			new Thread(coTransStart, "Main Tax Daemon Administrator").start();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		

	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
		UcContextHolder.setCustomerType(this.dataSource);
		
		sqlService_cyber = (IbatisBaseService) this.appContext.getBean("baseService_cyber");
		
		singleService = (IbatisService) this.appContext.getBean("ibatisService_cyber");
		
		/*
		log.info("=======================================");
		log.info("==        Main Thread Started        ==");
		log.info("=======================================");
		*/
		
		/*처음 기준시간 셋팅*/
		elapseTime = System.currentTimeMillis();
		

		/*---------------------------------*/
		/*여기서 부터 업무데몬을 시작시킨다.*/
		/*---------------------------------*/

		Thread[] daemonThread = null;	
		

		try {
			
			/*----------------------------------------------*/
			/*1. 업무용 데몬을 기동시킨다.                  */
			/*----------------------------------------------*/
			
			/* 업무 데몬에 Spring 컨텍스트를 주입해준다 */
			daemonThread = new Thread[CbUtil.getResourceInt("ApplicationResource", "cyber.sudoDm.count")];
			
			/* 이중화를 체크하기 위한 로직 */
			do{

				try {

							       
					/*메인데몬 상태 확인*/
					switch (getDaemonState(daemonThread, appContext))
					{
					case 1: /* 한번도 실행한 적이 없는 경우, 데몬이 죽어있고 15분이 지난경우 */
						
						/*상태정보 등록*/
						setStateRegist();
						
						/*
						 * 업무용 데몬들 기동
						 */
						startDaemon(daemonThread, appContext);
						
						break;
						
					case 2: /* 데몬이 살아 있고 자신인 경우*/
						
						log.info("=== DAEMON(" + procId + "::" + procName + ") ElapsedTime IS " + ((System.currentTimeMillis() - elapseTime) / 1000));
						
						/*상태정보 등록*/
						if(System.currentTimeMillis() - elapseTime >= 1000*60*5) { // 5분이상 경과됐다면 상태를 UPDATE 한다
							
							setStateRegist();
							
							/* 다시 기준시간을 셋팅한다...*/
							elapseTime = System.currentTimeMillis();
						}

						break;
						
					case 3: /* 이런일은 여기서는 있을 수 없다...*/
						
						/*
						 * 업무용 데몬을 KILL 시킨다.
						 */
						stopDaemon(daemonThread);
						
						/**
						 * 살린다...
						 */
						startDaemon(daemonThread, appContext);
						
						break;
					}
					
					
				} catch (Exception e) {
					e.printStackTrace();
					
				} finally {

				}
				
				viewMemory();
				
				//log.info("=== DAEMON(" + procId + "::" + procName + ") STATE IS " + loopTerm + "(s) Waitting...");
				Thread.sleep(1000 * loopTerm); /* loopTerm (분) 단위로 체크*/
				
			}while(true);
						
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			//log.info("=== DAEMON(" + procId + "::" + procName + ") STATE IS DOWN!!!");
		}		
		
		
	}
	
	/*
	 * 데몬정보 등록
	 * PROC_ID = "0011" 명명한다.
	 * PROC_NM = "사이버지방세청 연계 데몬관리"
	 * */
	private static void setStateRegist(){

		sysMap = new MapForm();
				
		sysMap.setMap("SGG_COD"      , "000");                                     /*구청코드 : 부산시(626), 사이버세청(000)*/
		sysMap.setMap("PROCESS_ID"   , procId);                                    /*프로세스 ID*/
		sysMap.setMap("PROCESS_NM"   , procName);                                  /*프로세스 명*/
		sysMap.setMap("THREAD_NM"    , "Main Tax Daemon Administrator");         /*쓰레드 명*/
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
			//log.info("=== DAEMON(" + procId + "::" + procName + ") STATE IS REGISTERING...");
			if(singleService.queryForUpdate("CODMBASE.CODMSYSTUpdate", sysMap) < 1) {
				singleService.queryForInsert("CODMBASE.CODMSYSTInsert", sysMap);
			}	
			//log.info("=== DAEMON(" + procId + "::" + procName + ") STATE IS REGISTED.");
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			log.debug("=연계서버 메인데몬상태 등록중 오류발생 \n" + e.getStackTrace());

		}
		
	}
	
	/**
	 * 메인데몬 상태를 확인한다.
	 * return 1, 2, 3 의 형태로 리턴한다.
	 * 부산사이버세청에서는 3은 발생하지 않는다.
	 * */
	private static int getDaemonState(Thread[] dThread, ApplicationContext  context) {
				
		int intvalue = 0;
		long lastTerm = 0;
		
		//log.info("getDaemonState START!!!!");
		
		sysMap = new MapForm();
		
		sysMap.setMap("SGG_COD"     , "000");	  // 구청코드 p k
		sysMap.setMap("PROCESS_ID"  , procId);	  // 프로세스ID p k
		sysMap.setMap("PROCESS_NM"  , procName);  // 프로세스명
		
		//log.info("procId=" + procId);
		//log.info("procName=" + procName);

		
		try {
			/*
			 * 메인데몬 상태 확인
			 * */
			MapForm procMap = (MapForm) sqlService_cyber.queryForMap("CODMBASE.CODMSYSTSelect", sysMap);
						
			if(procMap == null || procMap.isEmpty()) {   /* 데이터가 조회되지 않았다면 한번도 실행된적 없는 데몬이다. */
				intvalue = 1;
			}else if(!procMap.getMap("MANE_STAT").equals("1")){    /* 프로세스 상태가 살아있지 않고 시간이 15분 지난데몬. */
				intvalue = 1;
			}else if( procMap.getMap("MANE_STAT").equals("1") &&  procMap.getMap("BIGO").equals(CbUtil.getServerIp())){  /*살아 있고 자신인 경우*/
				intvalue = 2;
			}else if( procMap.getMap("MANE_STAT").equals("1") && !procMap.getMap("BIGO").equals(CbUtil.getServerIp())){  /*살아 있고 자신이 아닌경우*/
				intvalue = 3;
			}
			
			/* 프로세스가 최종 상태를 등록한 시간을 확인한다 */
			if(procMap == null || procMap.isEmpty()) {
				lastTerm = 0;
			}else{
				lastTerm = 100;
			}
			
			
			//log.info("=== DAEMON(" + procId + "::" + procName + ") STATE IS [" + intvalue + "]");
			//log.info("=== DAEMON(" + procId + "::" + procName + ") IS Last RUN==" + lastTerm + "초::" + "IP=" + CbUtil.getServerIp());
			
			/*
			 * 등록된 데몬중 죽어 있는 데몬이 있다면...죽은 데몬은 살려야 하지 않을까???
			 * */
			if(intvalue == 2){
				/*
				 * 메인 데몬이 정상적으로 동작하고 
				 * 메인 데몬이 관리하는 데몬이 죽은 경우만...
				 * */
				aliveDaemon(dThread, context);
			}
			
		} catch (SQLException se) {
			
			if(se.getErrorCode() == 30006){
				log.info("리소스 사용 중. WAIT 시간 초과로 획득이 만료됨 : I'M WAITTING...");
			}else{
				se.printStackTrace();
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			log.debug("=연계서버 메인데몬상태 확인중 오류발생 \n" + e.getStackTrace());
		} finally{
						
		}	
		
		return intvalue;
	}
	
	
	/**
	 * 정상적으로 동작하는 AP에서 관리하는 데몬 중 죽은놈이 있다면
	 * 죽은 데몬을 다시 살려준다...
	 * */
	private static void aliveDaemon(Thread[] adThread, ApplicationContext  context) throws Exception {
		
		StringBuffer sb_key = new StringBuffer();
		StringBuffer sb_msg = new StringBuffer();

		for(int i = 0; i<adThread.length; i++) {
			
			if(adThread[i] != null && !adThread[i].isAlive()){
				
				sb_msg.append("데몬 ["+ i +"][" + adThread[i].getName() + "] = " + adThread[i].isAlive() + "|");
				sb_key.append("cyber.sudoDm.class" + i);
				
				Codm_interface daemon = (Codm_interface) Class.forName(CbUtil.getResource("ApplicationResource", sb_key.toString())).newInstance();		
				
				daemon.setContext(context); 
				sb_key.delete(0, sb_key.length());

				adThread[i] = new Thread((Runnable) daemon);
			
				sb_key.append("cyber.sudoDm.name" + i);
				String proc_id = CbUtil.getResource("ApplicationResource", sb_key.toString());  /* ApplicationResource.properties 참조하여 명명된 프로그램ID를 찾음 */
				sb_key.delete(0, sb_key.length());

				if(daemon instanceof Codm_BaseDaemon) { // 일반데몬일 경우 데몬의 이름과 ID를 SET 해준다
					
					sb_key.append("cyber.sudoDm.title" + i);
					String proc_nm = CbUtil.getResource("ApplicationResource", sb_key.toString());
					String thr_name = "thr_" + proc_id;
					sb_key.delete(0, sb_key.length());
					
					((Codm_BaseDaemon)daemon).setProcess(proc_id, proc_nm, thr_name);
					
					log.info("====데몬 [" + i + "][" + proc_id + "][" + adThread[i].getName()+ "] 데몬이 기동중지되어 있어 다시기동합니다...");
					log.info("====데몬 [" + i + "][" + proc_id + "][" + proc_nm + " restarting!!!");

				} else if(daemon instanceof RcvWorker) {
					
					sb_key.append("cyber.sudoDm.title" + i);
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
		
		//log.info(sb_msg.toString());
	
	}
	
	/**
	 * 업무용 데몬을 기동시킨다.
	 * ApplicationResource.properties 에 등록된 데몬에 한해서 기동시킨다.
	 * */
	private static void startDaemon(Thread[] dThread, ApplicationContext  context) throws Exception {
		
		//log.info("=========================데몬 기동 시작========================");
		
		StringBuffer sb_key = new StringBuffer();
		
		for(int i=0; i<dThread.length; i++) {
			
			//log.info("dThread.lengthdThread.length = [" + dThread.length + "]");
			
			sb_key.append("cyber.sudoDm.class" + i);
			
			Codm_interface daemon = (Codm_interface) Class.forName(CbUtil.getResource("ApplicationResource", sb_key.toString())).newInstance();		
			
			daemon.setContext(context);
			sb_key.delete(0, sb_key.length());
			
			/*
			 * 관리데몬임...여기는....
			 * daemon.initProcess();
			 * */
			dThread[i] = new Thread((Runnable) daemon);
			
			sb_key.append("cyber.sudoDm.name" + i);
			
			String proc_id = CbUtil.getResource("ApplicationResource", sb_key.toString());
			
			sb_key.delete(0, sb_key.length());
			
			if(daemon instanceof Codm_BaseDaemon) { /*일반데몬일 경우 데몬의 이름과 ID를 SET 해준다*/
				
				sb_key.append("cyber.sudoDm.title" + i);
				String proc_nm = CbUtil.getResource("ApplicationResource", sb_key.toString());
				String thr_name = "thr_" + proc_id;
				sb_key.delete(0, sb_key.length());
				
				((Codm_BaseDaemon)daemon).setProcess(proc_id, proc_nm, thr_name);
				
				//log.info("====데몬 [" + i + "][" + proc_id + "][" + proc_nm + " startingif!!!");
			
			} else if(daemon instanceof RcvWorker) {
			
				sb_key.append("cyber.sudoDm.title" + i);
				String proc_nm = CbUtil.getResource("ApplicationResource", sb_key.toString());
				sb_key.delete(0, sb_key.length());
								
				//log.info("====데몬 [" + i + "][" + proc_id + "][" + proc_nm + " startingelse!!!");
				
			} else {
				
				sb_key.append("cyber.sudoDm.title" + i);
				String proc_nm = CbUtil.getResource("ApplicationResource", sb_key.toString());
				sb_key.delete(0, sb_key.length());
								
				//log.info("====데몬 [" + i + "][" + proc_id + "][" + proc_nm + " falsed!!!");
			}
			
			dThread[i].setName(proc_id);
			dThread[i].start();
			
		}
		
		isInitDaemonStart = true;
		//log.info("=========================데몬 기동 끝 =========================");
		
	}	
	
	/**
	 * 업무데몬 KILL 시킨다.
	 * 강제로 interrupt 를 발생시켜 해당 Thread 에 thread 에 java.lang.InterruptedException 을 발생시켜
	 * 쓰레드를 종료시킨다.
	 **/
	private static void stopDaemon(Thread[] sThreads) throws Exception {
		
		log.info("=================강제 interrupting 시작 ======================");
		
		StringBuffer sb = new StringBuffer();
		
		for(int i = 0; i<sThreads.length; i++) {
			
			if(sThreads[i] != null && sThreads[i].isAlive()){
				sb.append("Thread["+ i +"](" + sThreads[i].getName()+ ").interrupting... ");
				sThreads[i].interrupt();
			}else{
				sb.append("Thread["+ i +"] state is dead. ");
			}
			
		}
		log.info(sb.toString());
		log.info("=================강제 interrupting 종료 ======================");
	}	
	
	private static void viewMemory() {
		
		Runtime runtime = Runtime.getRuntime();

	    NumberFormat format = NumberFormat.getInstance();
   
	    long maxMemory = runtime.maxMemory();
	    long allocatedMemory = runtime.totalMemory();
	    long freeMemory = runtime.freeMemory();
	    /*
	    log.info("==============================================================");
	    log.info("|free memory:       |[" + format.format(freeMemory / 1024) + "]");
	    log.info("|allocated memory:  |[" + format.format(allocatedMemory / 1024) + "]");
	    log.info("|max memory:        |[" + format.format(maxMemory / 1024) + "]");
	    log.info("|total free memory: |[" + format.format((freeMemory + (maxMemory - allocatedMemory)) / 1024) + "]");
	    log.info("==============================================================");
	    */
		
	}

}
