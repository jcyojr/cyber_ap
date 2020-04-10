/**
 *  �ֽý��۸� : �λ�� ���̹����漼û
 *  ��  ��  �� : ����
 *  ��  ��  �� : �ְ������������� ���� ���� 
 *     
 *               5�д����� ����...
 *  Ŭ����  ID : Txdm2440
 *  ����  �̷� :
 * ------------------------------------------------------------------------
 *  �ۼ���      	�Ҽ�  		    ����            Tag           ����
 * ------------------------------------------------------------------------
 *  �۵���       ��ä��(��)      2011.09.20         %01%         �����ۼ�
 */
package com.uc.bs.cyber.daemon.txdm2440;

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
public class Txdm2440 extends Codm_BaseDaemon{

	private static String govid = "";
	private static String orgcd = "";
	private static String orgdb = "";
	
	/**
	 * ������
	 */
	public Txdm2440() throws IOException, Exception {
		
		super();

		log = LogFactory.getLog(this.getClass());

		this.loopTerm = 300;	/* 300�ʸ��� */
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Txdm2440 Txdm2440;
		ApplicationContext context  = null;
		
		try {
			// Log
			Txdm2440 = new Txdm2440();
			
			CbUtil.setupLog4jConfig(Txdm2440, "log4j.xml");
			
			try {
				String[] xmls = XMLFileReader.getAllFilePathArray(CbUtil.getResourcePath(Txdm2440, "/"), "SERVICE.XML");

				String strSrc = CbUtil.getResourcePath(Txdm2440, "/") + "config/sqlmaps.xml";

				String strDest = CbUtil.getResourcePath(Txdm2440, "/") + "config/sqlmapConfig.xml";

				XmlUtils.setXmlAttributes(Txdm2440, strSrc, strDest, "sqlMapConfig", "sqlMap", "url", xmls);

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
		
			context = new ClassPathXmlApplicationContext("config/Spring-db.xml");
			
			Txdm2440.setProcess("2440", "�ְ������������� �ڷῬ��(�õ�)", "thr_2440");  /* �������� ��� */
			
			Txdm2440.setContext(context);
			
			Thread tx2440Thread = new Thread(Txdm2440);
			
			tx2440Thread.setName("thr_2440");
			
			tx2440Thread.start();
			
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
		
		/**
		 * ������ ��İ� Ʋ��...
		 * ����, ����, ���λ��� �� ������ ������� �����Ѵ�...
		 */
		
		int procCnt  = 3;
		
		if (log.isDebugEnabled()){
			log.debug("�⵿���μ��� ���� :: procCnt = " + procCnt);
		}
		
		threadList = new Thread[procCnt];
		
		for(int i = 0; i<procCnt; i++) {
			
			/*
			 * 1. ��û(��û)�ڵ�
			 * 2. DB��������
			 * 3. Context
			 * */
			govid = CbUtil.getResource("ApplicationResource", "cyber.bt.gov0");
			orgcd = CbUtil.getResource("ApplicationResource", "cyber." + govid + ".org_cd");
			orgdb = "JUGEOJI";  /*�ְ��� ���������� DB����*/
			
			threadList[i] = new Thread(newProcess(govid, orgdb, i), "sub_2440thr_" + orgcd);			
			threadList[i].start();
			
		}
	}

	@Override
	public void mainProcess() throws Exception {
		// TODO Auto-generated method stub
		for(int i = 0; i<threadList.length; i++) {
			
			if(!threadList[i].isAlive()) {	// �ٽ� �����
				
				log.debug("=====================================================================");
				log.debug("== " + this.getClass().getName() + " mainProcess(" + i + ") ==");
				log.debug("=====================================================================");
				
				log.debug(" Thread(" + threadList[i].getName() + ") is " + threadList[i].isAlive());
				
				govid = CbUtil.getResource("ApplicationResource", "cyber.bt.gov" + i);
				orgcd = CbUtil.getResource("ApplicationResource", "cyber." + govid + ".org_cd");
				orgdb = "JUGEOJI";
				
				try {
					threadList[i] = new Thread(newProcess(govid, orgdb, i), govid);
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
	 * ���� ����...�� ȣ���Ѵ�.
	 */
	private Txdm2440_process newProcess(String govId, String orgcd, int jobid) throws Exception {
		
		Txdm2440_process process = new Txdm2440_process();
		
		process.setContext(appContext);
		process.setGovId(govId);
		process.setUpId(jobid);
		process.setDataSource(orgcd);
		
		process.initProcess();		
		
		return process;
	}

}
