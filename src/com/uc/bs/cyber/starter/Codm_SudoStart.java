/*
 * ���� ���� ���� (���������)
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
	
	private static String  procName = "����� ���赥��";
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
		
		/*Log4j ����*/
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
			
			log.info("������� ���� DBüũ �ð� : " + CbUtil.formatTime(elapseTime2 - elapseTime1));
			
			
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
		
		/*ó�� ���ؽð� ����*/
		elapseTime = System.currentTimeMillis();
		

		/*---------------------------------*/
		/*���⼭ ���� ���������� ���۽�Ų��.*/
		/*---------------------------------*/

		Thread[] daemonThread = null;	
		

		try {
			
			/*----------------------------------------------*/
			/*1. ������ ������ �⵿��Ų��.                  */
			/*----------------------------------------------*/
			
			/* ���� ���� Spring ���ؽ�Ʈ�� �������ش� */
			daemonThread = new Thread[CbUtil.getResourceInt("ApplicationResource", "cyber.sudoDm.count")];
			
			/* ����ȭ�� üũ�ϱ� ���� ���� */
			do{

				try {

							       
					/*���ε��� ���� Ȯ��*/
					switch (getDaemonState(daemonThread, appContext))
					{
					case 1: /* �ѹ��� ������ ���� ���� ���, ������ �׾��ְ� 15���� ������� */
						
						/*�������� ���*/
						setStateRegist();
						
						/*
						 * ������ ����� �⵿
						 */
						startDaemon(daemonThread, appContext);
						
						break;
						
					case 2: /* ������ ��� �ְ� �ڽ��� ���*/
						
						log.info("=== DAEMON(" + procId + "::" + procName + ") ElapsedTime IS " + ((System.currentTimeMillis() - elapseTime) / 1000));
						
						/*�������� ���*/
						if(System.currentTimeMillis() - elapseTime >= 1000*60*5) { // 5���̻� ����ƴٸ� ���¸� UPDATE �Ѵ�
							
							setStateRegist();
							
							/* �ٽ� ���ؽð��� �����Ѵ�...*/
							elapseTime = System.currentTimeMillis();
						}

						break;
						
					case 3: /* �̷����� ���⼭�� ���� �� ����...*/
						
						/*
						 * ������ ������ KILL ��Ų��.
						 */
						stopDaemon(daemonThread);
						
						/**
						 * �츰��...
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
				Thread.sleep(1000 * loopTerm); /* loopTerm (��) ������ üũ*/
				
			}while(true);
						
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			//log.info("=== DAEMON(" + procId + "::" + procName + ") STATE IS DOWN!!!");
		}		
		
		
	}
	
	/*
	 * �������� ���
	 * PROC_ID = "0011" ����Ѵ�.
	 * PROC_NM = "���̹����漼û ���� �������"
	 * */
	private static void setStateRegist(){

		sysMap = new MapForm();
				
		sysMap.setMap("SGG_COD"      , "000");                                     /*��û�ڵ� : �λ��(626), ���̹���û(000)*/
		sysMap.setMap("PROCESS_ID"   , procId);                                    /*���μ��� ID*/
		sysMap.setMap("PROCESS_NM"   , procName);                                  /*���μ��� ��*/
		sysMap.setMap("THREAD_NM"    , "Main Tax Daemon Administrator");         /*������ ��*/
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
			//log.info("=== DAEMON(" + procId + "::" + procName + ") STATE IS REGISTERING...");
			if(singleService.queryForUpdate("CODMBASE.CODMSYSTUpdate", sysMap) < 1) {
				singleService.queryForInsert("CODMBASE.CODMSYSTInsert", sysMap);
			}	
			//log.info("=== DAEMON(" + procId + "::" + procName + ") STATE IS REGISTED.");
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			log.debug("=���輭�� ���ε������ ����� �����߻� \n" + e.getStackTrace());

		}
		
	}
	
	/**
	 * ���ε��� ���¸� Ȯ���Ѵ�.
	 * return 1, 2, 3 �� ���·� �����Ѵ�.
	 * �λ���̹���û������ 3�� �߻����� �ʴ´�.
	 * */
	private static int getDaemonState(Thread[] dThread, ApplicationContext  context) {
				
		int intvalue = 0;
		long lastTerm = 0;
		
		//log.info("getDaemonState START!!!!");
		
		sysMap = new MapForm();
		
		sysMap.setMap("SGG_COD"     , "000");	  // ��û�ڵ� p k
		sysMap.setMap("PROCESS_ID"  , procId);	  // ���μ���ID p k
		sysMap.setMap("PROCESS_NM"  , procName);  // ���μ�����
		
		//log.info("procId=" + procId);
		//log.info("procName=" + procName);

		
		try {
			/*
			 * ���ε��� ���� Ȯ��
			 * */
			MapForm procMap = (MapForm) sqlService_cyber.queryForMap("CODMBASE.CODMSYSTSelect", sysMap);
						
			if(procMap == null || procMap.isEmpty()) {   /* �����Ͱ� ��ȸ���� �ʾҴٸ� �ѹ��� ������� ���� �����̴�. */
				intvalue = 1;
			}else if(!procMap.getMap("MANE_STAT").equals("1")){    /* ���μ��� ���°� ������� �ʰ� �ð��� 15�� ��������. */
				intvalue = 1;
			}else if( procMap.getMap("MANE_STAT").equals("1") &&  procMap.getMap("BIGO").equals(CbUtil.getServerIp())){  /*��� �ְ� �ڽ��� ���*/
				intvalue = 2;
			}else if( procMap.getMap("MANE_STAT").equals("1") && !procMap.getMap("BIGO").equals(CbUtil.getServerIp())){  /*��� �ְ� �ڽ��� �ƴѰ��*/
				intvalue = 3;
			}
			
			/* ���μ����� ���� ���¸� ����� �ð��� Ȯ���Ѵ� */
			if(procMap == null || procMap.isEmpty()) {
				lastTerm = 0;
			}else{
				lastTerm = 100;
			}
			
			
			//log.info("=== DAEMON(" + procId + "::" + procName + ") STATE IS [" + intvalue + "]");
			//log.info("=== DAEMON(" + procId + "::" + procName + ") IS Last RUN==" + lastTerm + "��::" + "IP=" + CbUtil.getServerIp());
			
			/*
			 * ��ϵ� ������ �׾� �ִ� ������ �ִٸ�...���� ������ ����� ���� ������???
			 * */
			if(intvalue == 2){
				/*
				 * ���� ������ ���������� �����ϰ� 
				 * ���� ������ �����ϴ� ������ ���� ��츸...
				 * */
				aliveDaemon(dThread, context);
			}
			
		} catch (SQLException se) {
			
			if(se.getErrorCode() == 30006){
				log.info("���ҽ� ��� ��. WAIT �ð� �ʰ��� ȹ���� ����� : I'M WAITTING...");
			}else{
				se.printStackTrace();
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			log.debug("=���輭�� ���ε������ Ȯ���� �����߻� \n" + e.getStackTrace());
		} finally{
						
		}	
		
		return intvalue;
	}
	
	
	/**
	 * ���������� �����ϴ� AP���� �����ϴ� ���� �� �������� �ִٸ�
	 * ���� ������ �ٽ� ����ش�...
	 * */
	private static void aliveDaemon(Thread[] adThread, ApplicationContext  context) throws Exception {
		
		StringBuffer sb_key = new StringBuffer();
		StringBuffer sb_msg = new StringBuffer();

		for(int i = 0; i<adThread.length; i++) {
			
			if(adThread[i] != null && !adThread[i].isAlive()){
				
				sb_msg.append("���� ["+ i +"][" + adThread[i].getName() + "] = " + adThread[i].isAlive() + "|");
				sb_key.append("cyber.sudoDm.class" + i);
				
				Codm_interface daemon = (Codm_interface) Class.forName(CbUtil.getResource("ApplicationResource", sb_key.toString())).newInstance();		
				
				daemon.setContext(context); 
				sb_key.delete(0, sb_key.length());

				adThread[i] = new Thread((Runnable) daemon);
			
				sb_key.append("cyber.sudoDm.name" + i);
				String proc_id = CbUtil.getResource("ApplicationResource", sb_key.toString());  /* ApplicationResource.properties �����Ͽ� ���� ���α׷�ID�� ã�� */
				sb_key.delete(0, sb_key.length());

				if(daemon instanceof Codm_BaseDaemon) { // �Ϲݵ����� ��� ������ �̸��� ID�� SET ���ش�
					
					sb_key.append("cyber.sudoDm.title" + i);
					String proc_nm = CbUtil.getResource("ApplicationResource", sb_key.toString());
					String thr_name = "thr_" + proc_id;
					sb_key.delete(0, sb_key.length());
					
					((Codm_BaseDaemon)daemon).setProcess(proc_id, proc_nm, thr_name);
					
					log.info("====���� [" + i + "][" + proc_id + "][" + adThread[i].getName()+ "] ������ �⵿�����Ǿ� �־� �ٽñ⵿�մϴ�...");
					log.info("====���� [" + i + "][" + proc_id + "][" + proc_nm + " restarting!!!");

				} else if(daemon instanceof RcvWorker) {
					
					sb_key.append("cyber.sudoDm.title" + i);
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
		
		//log.info(sb_msg.toString());
	
	}
	
	/**
	 * ������ ������ �⵿��Ų��.
	 * ApplicationResource.properties �� ��ϵ� ���� ���ؼ� �⵿��Ų��.
	 * */
	private static void startDaemon(Thread[] dThread, ApplicationContext  context) throws Exception {
		
		//log.info("=========================���� �⵿ ����========================");
		
		StringBuffer sb_key = new StringBuffer();
		
		for(int i=0; i<dThread.length; i++) {
			
			//log.info("dThread.lengthdThread.length = [" + dThread.length + "]");
			
			sb_key.append("cyber.sudoDm.class" + i);
			
			Codm_interface daemon = (Codm_interface) Class.forName(CbUtil.getResource("ApplicationResource", sb_key.toString())).newInstance();		
			
			daemon.setContext(context);
			sb_key.delete(0, sb_key.length());
			
			/*
			 * ����������...�����....
			 * daemon.initProcess();
			 * */
			dThread[i] = new Thread((Runnable) daemon);
			
			sb_key.append("cyber.sudoDm.name" + i);
			
			String proc_id = CbUtil.getResource("ApplicationResource", sb_key.toString());
			
			sb_key.delete(0, sb_key.length());
			
			if(daemon instanceof Codm_BaseDaemon) { /*�Ϲݵ����� ��� ������ �̸��� ID�� SET ���ش�*/
				
				sb_key.append("cyber.sudoDm.title" + i);
				String proc_nm = CbUtil.getResource("ApplicationResource", sb_key.toString());
				String thr_name = "thr_" + proc_id;
				sb_key.delete(0, sb_key.length());
				
				((Codm_BaseDaemon)daemon).setProcess(proc_id, proc_nm, thr_name);
				
				//log.info("====���� [" + i + "][" + proc_id + "][" + proc_nm + " startingif!!!");
			
			} else if(daemon instanceof RcvWorker) {
			
				sb_key.append("cyber.sudoDm.title" + i);
				String proc_nm = CbUtil.getResource("ApplicationResource", sb_key.toString());
				sb_key.delete(0, sb_key.length());
								
				//log.info("====���� [" + i + "][" + proc_id + "][" + proc_nm + " startingelse!!!");
				
			} else {
				
				sb_key.append("cyber.sudoDm.title" + i);
				String proc_nm = CbUtil.getResource("ApplicationResource", sb_key.toString());
				sb_key.delete(0, sb_key.length());
								
				//log.info("====���� [" + i + "][" + proc_id + "][" + proc_nm + " falsed!!!");
			}
			
			dThread[i].setName(proc_id);
			dThread[i].start();
			
		}
		
		isInitDaemonStart = true;
		//log.info("=========================���� �⵿ �� =========================");
		
	}	
	
	/**
	 * �������� KILL ��Ų��.
	 * ������ interrupt �� �߻����� �ش� Thread �� thread �� java.lang.InterruptedException �� �߻�����
	 * �����带 �����Ų��.
	 **/
	private static void stopDaemon(Thread[] sThreads) throws Exception {
		
		log.info("=================���� interrupting ���� ======================");
		
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
		log.info("=================���� interrupting ���� ======================");
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
