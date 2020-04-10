/*
 * ���� ���� ����
 * ���ý����� ����...
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
	
	private static String  procName = "���̹����漼û(WeTax) �������";
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
		
		/*Log4j ����*/
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

		/*ó�� ���ؽð� ����*/
		elapseTime = System.currentTimeMillis();
		
		setStateRegist();  /*������µ��*/
		
		/*---------------------------------*/
		/*���⼭ ���� ���������� ���۽�Ų��.*/
		/*---------------------------------*/		
		Thread[] daemonThread = null;	
		
		try {
			
			/*----------------------------------------------*/
			/*1. ������ ��������� ���� Server.class �� �⵿��Ų��.*/
			/*----------------------------------------------*/
			Txdm2520 txdm2520 = new Txdm2520(appContext);
			Txdm2530 txdm2530 = new Txdm2530(appContext);
			Txdm2540 txdm2540 = new Txdm2540(appContext);

			/*----------------------------------------------*/
			/*1. ������ ������ �⵿��Ų��.                  */
			/*----------------------------------------------*/
			/* ���� ���� Spring ���ؽ�Ʈ�� �������ش� */
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
			
			/* ���� ���� Spring ���ؽ�Ʈ�� �������ش� */
			
			/* ����ȭ�� üũ�ϱ� ���� ���� */
			do{

				try {

					if(!isInitDaemonStart) {
						
						//startDaemon(daemonThread, appContext);
					} else {
						
						stateDaemon(daemonThread, appContext);
					}

					/*�������� ���*/
					if(System.currentTimeMillis() - elapseTime >= 1000*60*5) { // 5���̻� ����ƴٸ� ���¸� UPDATE �Ѵ�
						
						//setStateRegist();  //2016.06.20 ���� UPDATE �� �� �ҿ�ð��� �����ɸ��� ��� ������ó�� �Ǵ� ��찡 �־� �ּ�ó�� ��
						
						/* �ٽ� ���ؽð��� �����Ѵ�...*/
						elapseTime = System.currentTimeMillis();
					}
					
				} catch (Exception e) {
					e.printStackTrace();
					
				} finally {

				}
				
				/*�ý��� ���� ǥ��*/
				viewMemory();
				
				Thread.sleep(1000 * loopTerm); /* loopTerm (��) ������ üũ*/
				
			}while(true);
						
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			log.info("=== DAEMON(" + procId + "::" + procName + ") STATE IS DOWN!!!");
		}		

	}

	/*
	 * �ۼ��ŵ��� �⵿
	 * */
	private static void startDaemon(Thread[] dThread, ApplicationContext  context) throws Exception {
		
		log.info("========================���� �⵿ ����========================");
		
		StringBuffer sb_key = new StringBuffer();
		
		for(int i=0; i<dThread.length; i++) {
			
			sb_key.append("cyber.wetax.class" + i);
			
			RcvWorker daemon =  (RcvWorker)Class.forName(CbUtil.getResource("ApplicationResource", sb_key.toString())).newInstance();		
			
			daemon.setContext(context);
			
			sb_key.delete(0, sb_key.length());
			
			/*
			 * ����������...�����....
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
								
				log.info("====���� [" + i + "][" + proc_id + "][" + proc_nm + " starting!!!");
				
				dThread[i].setName(proc_id);
				dThread[i].start();
			}
			
		}
		
		isInitDaemonStart = true;
		
		log.info("========================���� �⵿ �� =========================");
	}	
	
	/**
	 * ���������� �����ϴ� AP���� �����ϴ� ���� �� �������� �ִٸ�
	 * ���� ������ �ٽ� ����ش�...
	 * */
	@SuppressWarnings("unused")
	private static void aliveDaemon(Thread[] adThread, ApplicationContext  context) throws Exception {
		
		StringBuffer sb_key = new StringBuffer();
		StringBuffer sb_msg = new StringBuffer();

		for(int i = 0; i<adThread.length; i++) {
			
			if(adThread[i] != null && !adThread[i].isAlive()){
				
				sb_msg.append("���� ["+ i +"][" + adThread[i].getName() + "] = " + adThread[i].isAlive() + "|");
				sb_key.append("cyber.wetax.class" + i);
				
				Codm_interface daemon = (Codm_interface) Class.forName(CbUtil.getResource("ApplicationResource", sb_key.toString())).newInstance();		
				
				daemon.setContext(context); 
				sb_key.delete(0, sb_key.length());

				adThread[i] = new Thread((Runnable) daemon);
			
				sb_key.append("cyber.wetax.name" + i);
				String proc_id = CbUtil.getResource("ApplicationResource", sb_key.toString());  /* ApplicationResource.properties �����Ͽ� ���� ���α׷�ID�� ã�� */
				sb_key.delete(0, sb_key.length());

				if(daemon instanceof RcvWorker) {
					
					sb_key.append("cyber.wetax.title" + i);
					String proc_nm = CbUtil.getResource("ApplicationResource", sb_key.toString());
					sb_key.delete(0, sb_key.length());
										
					log.info("====���� [" + i + "][" + proc_id + "][" + adThread[i].getName()+ "] ������ �⵿�����Ǿ� �־� �ٽñ⵿�մϴ�...");
					log.info("====���� [" + i + "][" + proc_id + "][" + proc_nm + " restarting!!!");
					
				}
				
				adThread[i].setName(proc_id);
				adThread[i].start();
				
			}else{
				/*
				 * DB�� ������°� �ʱ�ȭ ���� �ʰ� �� ����� ���...
				 * ���� �ʱ� �⵿�� ���������� �� �� ���� ���� ���� ��� �ش�
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
			
			sb_msg.append("���� ["+ i +"][" + adThread[i].getName() + "] = " + adThread[i].isAlive() + "|");
			sb_key.append("cyber.wetax.class" + i);
			sb_key.delete(0, sb_key.length());
			
			sb_key.append("cyber.wetax.name" + i);
			String proc_id = CbUtil.getResource("ApplicationResource", sb_key.toString());  /* ApplicationResource.properties �����Ͽ� ���� ���α׷�ID�� ã�� */
			sb_key.delete(0, sb_key.length());
			
			sb_key.append("cyber.wetax.title" + i);
			String proc_nm = CbUtil.getResource("ApplicationResource", sb_key.toString());
			sb_key.delete(0, sb_key.length());
			
			if(adThread[i] != null && !adThread[i].isAlive()){
				log.info("====���� [" + i + "][" + proc_id + "][" + adThread[i].getName()+ "] ������ �⵿�����Ǿ� �ֽ��ϴ�.");
				log.info("====���� [" + i + "][" + proc_id + "][" + proc_nm + " is restrting!!!");
				adThread[i].start();
				
			} else {
				log.info("====���� [" + i + "][" + proc_id + "][" + adThread[i].getName()+ "] ������ �⵿�Ǿ� �ֽ��ϴ�.");
				log.info("====���� [" + i + "][" + proc_id + "][" + proc_nm + " is alive!!!");
			}
			
		}
	}
	
	/*
	 * �������� ���
	 * PROC_ID = "0000" ����Ѵ�.
	 * PROC_NM = "���̹����漼û �������ۼ��� �������"
	 * */
	private static void setStateRegist(){
		
		MapForm sysMap = new MapForm();
				
		sysMap.setMap("SGG_COD"      , "000");                                     /*��û�ڵ� : �λ��(626), ���̹���û(000)*/
		sysMap.setMap("PROCESS_ID"   , procId);                                    /*���μ��� ID*/
		sysMap.setMap("PROCESS_NM"   , procName);                                  /*���μ��� ��*/
		sysMap.setMap("THREAD_NM"    , "Main Daemon(Wetax) Administrator");        /*������ ��*/
		sysMap.setMap("BIGO"         , CbUtil.getServerIp());                      /*��� : ���༭��IP�� �����Ѵ�. */
		sysMap.setMap("MANE_STAT"    , "1");                                       /*�⵿����*/
		sysMap.setMap("MANAGE_CD"    , "1");                                       /*�������� 1 : ���� */
		sysMap.setMap("STT_DT"       , CbUtil.getCurrent("yyyyMMddHHmmss"));       /*�����Ͻ� */
		sysMap.setMap("END_DT"       , "");                                        /*�����Ͻ�*/
		sysMap.setMap("LAST_DTM"     , CbUtil.getCurrent("yyyyMMddHHmmss"));       /*���������Ͻ�*/
		
		try {
			
			/**
			 * ����������� ���
			 */
			log.info("=== DAEMON(" + procId + "::" + procName + ") STATE IS REGISTERING...");
			if(singleService.queryForUpdate("CODMBASE.CODMSYSTUpdate", sysMap) < 1) {
				singleService.queryForInsert("CODMBASE.CODMSYSTInsert", sysMap);
			}	
			log.info("=== DAEMON(" + procId + "::" + procName + ") STATE IS REGISTED.");
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			log.debug("=�������ۼ��ż��� ���ε������ ����� �����߻� \n" + e.getStackTrace());

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
	    	//������ û�� �ѹ� ��...

	    }
		
	}

}