/**
 *  �ֽý��۸� : �λ�� ���̹����漼û 
 *  ��  ��  �� : ����
 *  ��  ��  �� : �κ�ī �����ڷ� ����
 *               20 �д����� ����...
 *               �κ�ī�����ڷ��  ������ ��û������ �������� �ʰ� �ְ��������θ� �����Ѵ�
 *  Ŭ����  ID : Txdm2450
 *  ����  �̷� :
 * ------------------------------------------------------------------------
 *  �ۼ���      	�Ҽ�  		    ����            Tag           ����
 * ------------------------------------------------------------------------
 *  ����       ��ä��(��)      2011.08.10         %01%         �����ۼ�
 */
package com.uc.bs.cyber.daemon.txdm2450;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.uc.bs.cyber.CbUtil;
import com.uc.bs.cyber.daemon.Codm_BaseDaemon;
import com.uc.core.MapForm;
import com.uc.core.misc.XMLFileReader;
import com.uc.core.misc.XmlUtils;
import com.uc.core.spring.service.IbatisService;
import com.uc.core.spring.service.UcContextHolder;

/**
 * @author Administrator
 *
 */
public class Txdm2450 extends Codm_BaseDaemon {
	
	/**
	 * ������
	 */
	
	protected IbatisService cyberService = null;	 // �⺻ DB �۾��� ���̹���û ���� 
	
	protected IbatisService govService  = null;	 // �⺻ DB �۾��� ��ġ��ü ����
	
	private ArrayList<MapForm> snList = null;
	
	private String tblName  = "";
	
	private int jobFlag = 0;
	
	public Txdm2450() throws IOException, Exception {
		// TODO Auto-generated constructor stub
		super();
		
		log = LogFactory.getLog(this.getClass());

		this.loopTerm = 300;	/* 5�� ���� */
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Txdm2450 Txdm2450;
		ApplicationContext context  = null;
		
		try {
			// Log
			Txdm2450 = new Txdm2450();
			
			CbUtil.setupLog4jConfig(Txdm2450, "log4j.xml");
			
			try {
				String[] xmls = XMLFileReader.getAllFilePathArray(CbUtil.getResourcePath(Txdm2450, "/"), "SERVICE.XML");

				String strSrc = CbUtil.getResourcePath(Txdm2450, "/") + "config/sqlmaps.xml";

				String strDest = CbUtil.getResourcePath(Txdm2450, "/") + "config/sqlmapConfig.xml";

				XmlUtils.setXmlAttributes(Txdm2450, strSrc, strDest, "sqlMapConfig", "sqlMap", "url", xmls);

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
		
			context = new ClassPathXmlApplicationContext("config/Spring-db.xml");
			
			Txdm2450.setProcess("2450", "�κ�ī �����ڷ� �ڷῬ��", "thr_2450");  /* �������� ��� */
			
			Txdm2450.setContext(context);
			
			
			Thread tx2450Thread = new Thread(Txdm2450);
			
			tx2450Thread.setName("thr_2450");
			
			tx2450Thread.start();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/*
	 * ���� ���μ��� �����մϴ�.
	 * �ʱ���۽� �ѹ��� �����մϴ�.
	 * */
	@Override
	public void initProcess() throws Exception {
		// TODO Auto-generated method stub
		log.debug("=====================================================================");
		log.debug("== " + this.getClass().getName() +  " initProcess() ==");
		log.debug("=====================================================================");
		
		this.context = this.appContext;
		
		cyberService = (IbatisService) this.getService("ibatisService_cyber"); 
		/**
		 * �ְ��� DB���������� ����������
		 */
		UcContextHolder.setCustomerType("JUGEOJI");

		govService   = (IbatisService) this.getService("ibatisService");
		
	}
    /*
     * ����� 10�ʴ����� thick �մϴ�.
     * ���� ���⼭�� �� ���� ���ؼ� ������ ������ �����ϵ��� �մϴ�.
     * */

	public void mainProcess() throws Exception {
		// TODO Auto-generated method stub

		log.info("======== �κ�ī ���� ���� START ========");
		
		while (true) {			
			/**
			 * ���漼 �������� ó��
			 */

			int flag = 0;
			
			for(jobFlag = 1; jobFlag <= 3; jobFlag++) {

				switch (jobFlag) {
					case 1:  tblName = "TX1202"; break;
					case 2:  tblName = "TX2201"; break;
					case 3:  tblName = "ET2101"; break;
					default: break;
				}
				
				snList  = cyberService.queryForList("TXDM2450." + tblName + "_SELECT", null);
			    if (snList.size() > 0) {
			    	flag = 1;
			    	
			    	this.startJob();
			    }
				
			}
					    
			if(flag == 0) break;
		}
		
		log.info("======== �κ�ī ���� ���� END   ========");
	}

	@Override
	public void onInterrupt() throws Exception {
		// TODO Auto-generated method stub
		if(threadList == null) return;
		
		for(int i = 0; i<threadList.length; i++) {
			threadList[i].interrupt();
		}
	}

	/**
	 * ����Ʈ������ ó��
	 */
	public void transactionJob() {
		// TODO Auto-generated method stub
		
		log.info("TRANSACTION JOB FLAG==" + jobFlag + ", COUNT==" + snList.size()) ;

//		govService.queryForMultiInsert("TXDM2450.OFFRECIP_INSERT", snList);

		MapForm snRows = new MapForm();
		for(int i=0;i<snList.size();i++){
			snRows =  snList.get(i);
			if(govService.getOneFieldInteger("TXDM2450.OFFRECIP_SELECT_CNT", snRows)==0){
				govService.queryForUpdate("TXDM2450.OFFRECIP_INSERT", snRows);
			}
		}

		cyberService.queryForMultiInsert("TXDM2450." + tblName + "_UPDATE", snList);
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
			
			log.info("buvicar--run--sysMap"+sysMap);
			
			if(this.getService().queryForUpdate("CODMBASE.CODMSYSTUpdate", sysMap) < 1) {
				this.getService().queryForInsert("CODMBASE.CODMSYSTInsert", sysMap);
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
				
				Thread.sleep(this.loopTerm * 1000);
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
	
}
