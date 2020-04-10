/**
 *  �ֽý��۸� : ���̹����漼û ��ȭ
 *  ��  ��  �� : ����
 *  ��  ��  �� : ���̹����漼û ���� ���� ����
 *  Ŭ����  ID : Codm_BaseDaemon
 *  ����  �̷� :
 * ------------------------------------------------------------------------
 *  �ۼ���      	�Ҽ�  		    ����            Tag           ����
 * ------------------------------------------------------------------------
 *  Administrator   u c         2011.04.24         %01%         �����ۼ�
 */
package com.uc.bs.cyber.daemon;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;

import com.uc.core.MapForm;
import com.uc.core.spring.service.IbatisBaseService;
import com.uc.core.spring.service.TransactionJob;

import com.uc.bs.cyber.CbUtil;

/**
 * @author Administrator
 *
 */
public abstract class Codm_BaseDaemon extends TransactionJob implements Codm_interface, Runnable{
	
	protected Log log = LogFactory.getLog(this.getClass());
	
	protected int loopTerm = 60;
	
	protected MapForm sysMap = new MapForm();
	
	protected String scg_code    = "000";
	protected String procName    = "";
	protected String procId      = "";
	protected String threadName  = "";
	
	/**
	 * ������
	 * @throws IOException
	 * @throws Exception
	 */
	public Codm_BaseDaemon() throws IOException, Exception {
		super();
		// TODO Auto-generated constructor stub
		
	}
	
	protected ApplicationContext  appContext = null;
	
	protected Thread[] threadList    = null;
	

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	/**
	 * @param ��û�ڵ� the scg_code to set
	 */
	public void setScg_code(String scg_code) {
		this.scg_code = scg_code;
	}

	/**
	 * 
	 * @param id
	 * @param name
	 */
	public void setProcess(String id, String name, String thr_name) {
		procId = id; procName  = name; threadName = thr_name;
	}

	/**
	 * Thread ����
	 */
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
		try {
						
			sysMap.setMap("SGG_COD"      , scg_code);                             /*��û�ڵ� : �λ��(626), ���̹���û(000)*/
			sysMap.setMap("PROCESS_ID"   , procId);                               /*���μ��� ID*/
			sysMap.setMap("PROCESS_NM"   , procName);                             /*���μ��� ��*/
			sysMap.setMap("THREAD_NM"    , threadName);                           /*������ ��*/
			sysMap.setMap("BIGO"         , CbUtil.getServerIp());                 /*��� : ���༭��IP�� �����Ѵ�. */
			sysMap.setMap("MANE_STAT"    , "1");                                  /*�⵿����*/
			sysMap.setMap("MANAGE_CD"    , "1");                                  /*�������� 1 : ���� */
			sysMap.setMap("STT_DT"       , CbUtil.getCurrent("yyyyMMddHHmmss"));  /*�����Ͻ� */
			sysMap.setMap("END_DT"       , "");                                   /*�����Ͻ�*/
			sysMap.setMap("REG_DTM"      , CbUtil.getCurrent("yyyyMMddHHmmss"));  /*����Ͻ� : ó���ѹ���...*/
			sysMap.setMap("LAST_DTM"     , CbUtil.getCurrent("yyyyMMddHHmmss"));  /*���������Ͻ�*/
			sysMap.setMap("LAST_TIMEMIL" , System.currentTimeMillis());
			
			/**
			 * ����������� ���
			 * TEST�� ��츸 ����
			 */
			try{
				if(this.getService().queryForUpdate("CODMBASE.CODMSYSTUpdate", sysMap) < 1) {
					this.getService().queryForInsert("CODMBASE.CODMSYSTInsert", sysMap);
				}
			}catch (Exception e) {
				e.printStackTrace();
			}
	
			/**
			 * �ʱ� ������ �⵿
			 */
			this.initProcess();
			
			/**
			 * �����尡 ������ ���
			 */
			while(!Thread.currentThread().isInterrupted()) {
				
				/*�����Լ�ó�� ���� 10�ʴ����� Tick �Ѵ�.*/
				mainProcess();
				
				/*10�� ������ ���� ������Ʈ*/
                if(System.currentTimeMillis() - (Long)sysMap.getMap("LAST_TIMEMIL") >= 1000*60*10) {
					
					sysMap.setMap("LAST_DTM"      , CbUtil.getCurrent("yyyyMMddHHmmss"));
					sysMap.setMap("MANAGE_CD"     , "1");
					sysMap.setMap("LAST_TIMEMIL"  , System.currentTimeMillis());
					
					/*���� ������¸� ������Ʈ �Ѵ�...*/
					this.getService().queryForUpdate("CODMBASE.CODMSYSTUpdate", sysMap);
				}
				
				Thread.sleep(1000 * 10);
			}
			
			/*THREAD�� ������ �� ������ ��� �����ǵ��� ���� Interrupting �Ѵ�.*/
			log.info("[" + this.procId + "] onInterrupt() called ...");
			
			sysMap.setMap("END_DT", CbUtil.getCurrent("yyyyMMddHHmmss"));
			/*���� ���� ������¸� ������Ʈ �Ѵ�...*/
			this.getService().queryForUpdate("CODMBASE.CODMSYSTUpdate", sysMap);
			
			this.onInterrupt();
			
		} catch (InterruptedException ie) {
			
			ie.printStackTrace();
			log.info("Thread Interrupted : [" + this.procId + "] ������ ���� ��...");
			
			try {
				this.onInterrupt();
			} catch (Exception e) {
				e.printStackTrace();
				
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
    /*Codm_interface class �� �������̽� �޼��� ����*/
	@Override
	public ApplicationContext getContext() {
		// TODO Auto-generated method stub
		return appContext;
	}
	/*Codm_interface class �� �������̽� �޼��� ����*/
	@Override
	public Object getService(String beanName) {
		// TODO Auto-generated method stub
		return appContext.getBean(beanName);
	}

	/*Codm_interface class �� �������̽� �޼��� ����*/
	@Override
	public void setContext(ApplicationContext context) {
		// TODO Auto-generated method stub
		this.appContext = context;
	}	
	
	public IbatisBaseService getService(){
		return (IbatisBaseService) appContext.getBean("baseService_cyber");
	}
	
	
	/**
	 * �߻�޼��� ����
	 * @throws Exception
	 */
	public abstract void mainProcess() throws  Exception;
	
	/**
	 * �߻�޼��� ����
	 * @throws Exception
	 */
	public abstract void onInterrupt() throws Exception;
	
	
	/*Codm_interface class �� �������̽� �޼��� ����*/
	public abstract void initProcess() throws Exception;



}
