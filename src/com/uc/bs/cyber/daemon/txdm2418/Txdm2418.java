/**
 *  �ֽý��۸� : �λ�� ���̹����漼û
 *  ��  ��  �� : ����
 *  ��  ��  �� : ǥ�ؼ��ܼ��� ������¿���
 *               60�д����� ����...
 *  Ŭ����  ID : Txdm2414
 *  ����  �̷� :
 * ------------------------------------------------------------------------
 *  �ۼ���      	�Ҽ�  		    ����            Tag           ����
 * ------------------------------------------------------------------------
 *  YHCHOI       ��ä��(��)      2015.02.12         %01%         �����ۼ�
 */
package com.uc.bs.cyber.daemon.txdm2418;

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
public class Txdm2418 extends Codm_BaseDaemon {

	private static String govid = "";
	private static String orgcd = "";
	private static String orgdb = "";

    /*������*/
	public Txdm2418() throws IOException, Exception {
		super();
		
		log = LogFactory.getLog(this.getClass());

		this.loopTerm = 310;	/*   310�ʸ��� */
		
	}
		
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		Txdm2418 Txdm2418;
		ApplicationContext context  = null;
		
		try {
			// Log
			Txdm2418 = new Txdm2418();
			
			CbUtil.setupLog4jConfig(Txdm2418, "log4j.xml");
			
			try {
				String[] xmls = XMLFileReader.getAllFilePathArray(CbUtil.getResourcePath(Txdm2418, "/"), "SERVICE.XML");

				String strSrc = CbUtil.getResourcePath(Txdm2418, "/") + "config/sqlmaps.xml";

				String strDest = CbUtil.getResourcePath(Txdm2418, "/") + "config/sqlmapConfig.xml";

				XmlUtils.setXmlAttributes(Txdm2418, strSrc, strDest, "sqlMapConfig", "sqlMap", "url", xmls);

			} catch (Exception e) {

				e.printStackTrace();
			}			
		
			context = new ClassPathXmlApplicationContext("config/Sw-Spring-db.xml");
			
			Txdm2418.setProcess("2418", "ǥ�ؼ��ܼ���(TX2112_TB) ������µ��", "thr_2418");  /* �������� ��� */
			
			Txdm2418.setContext(context);
			
			Thread tx2418Thread = new Thread(Txdm2418);
			
			tx2418Thread.setName("thr_2418");
			
			tx2418Thread.start();
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
    /*
     * ����� 10�ʴ����� thick �մϴ�.
     * ���� ���⼭�� �� ���� ���ؼ� ������ ������ �����ϵ��� �մϴ�.
     * */
	public void mainProcess() throws Exception {
		
        for(int i = 0; i<threadList.length; i++) {
			
			if(!threadList[i].isAlive()) {	// �ٽ� �����
				
				log.debug("=====================================================================");
				log.debug("== " + this.getClass().getName()+ " mainProcess(" + i + ") ==");
				log.debug("=====================================================================");
				
				log.debug(" Thread(" + threadList[i].getName() + ") is " + threadList[i].isAlive());
				
				govid = CbUtil.getResource("ApplicationResource", "cyber.seoi.gov" + i);
				orgcd = CbUtil.getResource("ApplicationResource", "cyber." + govid + ".org_cd");
				orgdb = "NI_" + orgcd;
				
				/* �������� ������ */
				//if(!(orgcd.equals("626")||orgcd.equals("337"))) continue;
								
				try {
					threadList[i] = new Thread(newProcess(govid, orgdb), govid);
				
					threadList[i].start();
					
				} catch (Exception e) {

					e.printStackTrace();
				}			
				
			}
			
		}
	}
	
	/*
	 * ���� ���μ��� �����մϴ�.
	 * �ʱ���۽� �ѹ��� �����մϴ�.
	 * */
	public void initProcess() throws IOException, Exception {

		log.debug("=====================================================================");
		log.debug("== " + this.getClass().getName() +  " initProcess() ==");
		log.debug("=====================================================================");
		
		//int procCnt  = CbUtil.getResourceInt("ApplicationResource", "cyber.TestSeoi.count");
		//�ѹ���
		int procCnt = 1;
		
		if(log.isDebugEnabled()){
			log.debug("��û���ܼ��� ���� :: procCnt = " + procCnt);
		}
	
		threadList = new Thread[procCnt];
		
		for(int i = 0; i<procCnt; i++) {
			
			/*
			 * 1. ��û(��û)�ڵ�
			 * 2. DB��������
			 * 3. Context
			 * */

			govid = CbUtil.getResource("ApplicationResource", "cyber.seoi.gov" + i);
			orgcd = CbUtil.getResource("ApplicationResource", "cyber." + govid + ".org_cd");
			orgdb = "NI_" + orgcd;
			
			
			threadList[i] = new Thread(newProcess(govid, orgdb), "sub_2418thr_" + orgcd);			
			threadList[i].start();
			
		}
		
	}


	public void onInterrupt() throws Exception {
		
		if(threadList == null) return;
		
		for(int i = 0; i<threadList.length; i++) {
			threadList[i].interrupt();
		}
	}


	public void transactionJob() {
		
	}

	/**
	 * @param govId
	 * @return
	 * @throws Exception
	 * ���� ����...�� ȣ���Ѵ�.
	 */
	private Txdm2418_process newProcess(String govId, String orgcd) throws Exception {
		
		Txdm2418_process process = new Txdm2418_process();
		
		process.setContext(appContext);
		process.setGovId(govId);
		process.setDataSource(orgcd);
		
		process.initProcess();		
		
		return process;
	}

}
