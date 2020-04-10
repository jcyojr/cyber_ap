/**
 *  �ֽý��۸� : �λ�� ���̹����漼û
 *  ��  ��  �� : ����
 *  ��  ��  �� : ���漼 ���볳���� �ΰ��ڷ�(��û��) ���� ���� 
 *               ���� ��û�� �� �Ǻ� �����带 �����ϰ� �������μ��� �⵿ �� ��⵿.
 *     
 *               5�д����� ����...
 *  Ŭ����  ID : Txdm3112
 *  ���  ���� : ����� �׽�Ʈ�� ����ϰ� ������ ���Ͽ� �� ���μ����� �⵿���� �ʴ� ��츦 ���.
 *  ����  �̷� :
 * ------------------------------------------------------------------------
 *  �ۼ���      	�Ҽ�  		    ����            Tag           ����
 * ------------------------------------------------------------------------
 *  ������       ��ä��(��)      2014.10.02         %01%         �����ۼ�
 */
package com.uc.bs.cyber.daemon.txdm3112;

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
public class Txdm3112 extends Codm_BaseDaemon{

	private static String govid = "";
	private static String orgcd = "";
	private static String orgdb = "";
	
	/**
	 * ������
	 */
	public Txdm3112() throws IOException, Exception {
		
		super();

		log = LogFactory.getLog(this.getClass());

		this.loopTerm = 300;	/* 300�ʸ��� */
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		Txdm3112 Txdm3112;
		ApplicationContext context  = null;
		
		try {
			//Log
			Txdm3112 = new Txdm3112();
			
			CbUtil.setupLog4jConfig(Txdm3112, "log4j.xml");
			
			try {
				String[] xmls = XMLFileReader.getAllFilePathArray(CbUtil.getResourcePath(Txdm3112, "/"), "SERVICE.XML");

				String strSrc = CbUtil.getResourcePath(Txdm3112, "/") + "config/sqlmaps.xml";

				String strDest = CbUtil.getResourcePath(Txdm3112, "/") + "config/sqlmapConfig.xml";

				XmlUtils.setXmlAttributes(Txdm3112, strSrc, strDest, "sqlMapConfig", "sqlMap", "url", xmls);

			} catch (Exception e) {

				e.printStackTrace();
			}			
		
			context = new ClassPathXmlApplicationContext("config/Tax-Spring-db.xml"); 
			
			Txdm3112.setProcess("3112", "���漼 ���볳���� �ڷῬ��(��û��)", "thr_3112");  /* �������� ��� */
			
			Txdm3112.setContext(context);
			
			Thread tx3112Thread = new Thread(Txdm3112);
			
			tx3112Thread.setName("thr_3112");
			
			tx3112Thread.start();
			
		} catch (IOException e) {

			e.printStackTrace();
		} catch (Exception e) {

			e.printStackTrace();
		}
	}

	
	public void initProcess() throws Exception {

		
		log.debug("=====================================================================");
		log.debug("== " + this.getClass().getName() +  " initProcess() ==");
		log.debug("=====================================================================");
		
		int procCnt  = CbUtil.getResourceInt("ApplicationResource", "cyber.tax.count");  
		
		if (log.isDebugEnabled()){
			log.debug("�⵿���μ��� ���� :: procCnt = " + procCnt);
		}
		
		//procCnt = 1;
		
		threadList = new Thread[procCnt];
		
		for(int i = 0; i<procCnt; i++) {
			
			/*
			 * 1. ��û(��û)�ڵ�
			 * 2. DB��������
			 * 3. Context
			 * */

			govid = CbUtil.getResource("ApplicationResource", "cyber.tax.gov" + i);
			orgcd = CbUtil.getResource("ApplicationResource", "cyber." + govid + ".org_cd");
			orgdb = "JI_"+ orgcd;
			
			log.info("orgdb = [" + orgdb + "]");
			
			threadList[i] = new Thread(newProcess(govid, orgdb), "sub_3112thr_" + orgcd);			
			threadList[i].start();
			
		}
	}

	@Override
	public void mainProcess() throws Exception {

		for(int i = 0; i<threadList.length; i++) {
			
			if(!threadList[i].isAlive()) {	// �ٽ� �����
				
				log.debug("=====================================================================");
				log.debug("== " + this.getClass().getName()+ " mainProcess(" + i + ") ==");
				log.debug("=====================================================================");
				
				log.debug(" Thread(" + threadList[i].getName() + ") is " + threadList[i].isAlive());
				
				govid = CbUtil.getResource("ApplicationResource", "cyber.tax.gov" + i);
				orgcd = CbUtil.getResource("ApplicationResource", "cyber." + govid + ".org_cd");
				orgdb = "JI_"+ orgcd;
				
				try {
					threadList[i] = new Thread(newProcess(govid, orgdb), govid);
					threadList[i].start();
					
				} catch (Exception e) {

					e.printStackTrace();
				}			
				
			}
			
		}
		
	}

	@Override
	public void onInterrupt() throws Exception {

		if(threadList == null) return;
		
		for(int i = 0; i<threadList.length; i++) {
			threadList[i].interrupt();
		}
	}

	@Override
	public void transactionJob() {

		
	}
	
	/**
	 * @param govId
	 * @return
	 * @throws Exception
	 * ���� ����...�� ȣ���Ѵ�.
	 */
	private Txdm3112_process newProcess(String govId, String orgcd) throws Exception {
		
		Txdm3112_process process = new Txdm3112_process();
		
		process.setContext(appContext);
		process.setGovId(govId);
		process.setDataSource(orgcd);
		
		process.initProcess();		
		
		return process;
	}
}
