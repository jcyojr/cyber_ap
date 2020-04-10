/**
 *  �ֽý��۸� : �λ�� ���̹����漼û
 *  ��  ��  �� : ����
 *  ��  ��  �� : ȯ�氳���δ�� �����ڷῬ��
 *               5�д����� ����...
 *               ȯ�氳���δ����  ������ ��û������ �������� �ʰ� �õ������θ� �����Ѵ�
 *               ����E���� �ǽð� ���� ���� - 20131204
 *  Ŭ����  ID : Txdm2460
 *  ����  �̷� : 2014.01.23 ���� e-���� �ǽð� ���� ���� �߰�
 * ------------------------------------------------------------------------
 *  �ۼ���      	�Ҽ�  		    ����            Tag           ����
 * ------------------------------------------------------------------------
 *  �۵���       ��ä��(��)      2011.06.03         %01%         �����ۼ�
 */
package com.uc.bs.cyber.daemon.txdm2460;

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
public class Txdm2460 extends Codm_BaseDaemon {

	private static String govid = "";
	private static String orgcd = "";
	private static String orgdb = "";
	
	/**
	 * ������
	 */
	public Txdm2460() throws IOException, Exception {
	
		super();
		
		log = LogFactory.getLog(this.getClass());

		this.loopTerm = 30;	/* 30�ʸ��� */
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {

		Txdm2460 Txdm2460;
		ApplicationContext context  = null;
		
		try {
			// Log
			Txdm2460 = new Txdm2460();
			
			CbUtil.setupLog4jConfig(Txdm2460, "log4j.xml");
			
			try {
				String[] xmls = XMLFileReader.getAllFilePathArray(CbUtil.getResourcePath(Txdm2460, "/"), "SERVICE.XML");

				String strSrc = CbUtil.getResourcePath(Txdm2460, "/") + "config/sqlmaps.xml";

				String strDest = CbUtil.getResourcePath(Txdm2460, "/") + "config/sqlmapConfig.xml";

				XmlUtils.setXmlAttributes(Txdm2460, strSrc, strDest, "sqlMapConfig", "sqlMap", "url", xmls);

			} catch (Exception e) {
			
				e.printStackTrace();
				
			}			
		
			context = new ClassPathXmlApplicationContext("config/Tax-Spring-db.xml");
			
			Txdm2460.setProcess("2460", "ȯ�氳���δ�� �����ڷῬ��", "thr_2460");  /* �������� ��� */
			
			Txdm2460.setContext(context);
			
			Thread tx2460Thread = new Thread(Txdm2460);
			
			tx2460Thread.setName("thr_2460");
			
			tx2460Thread.start();
			
		} catch (IOException e) {
		
			e.printStackTrace();
		} catch (Exception e) {
			
			e.printStackTrace();
		}
	}
	
	/*
	 * ���� ���μ��� �����մϴ�.
	 * �ʱ���۽� �ѹ��� �����մϴ�.
	 * */
	public void initProcess() throws Exception {

		log.debug("=====================================================================");
		log.debug("== " + this.getClass().getName() +  " initProcess() ==");
		log.debug("=====================================================================");
		
		int procCnt  = CbUtil.getResourceInt("ApplicationResource", "cyber.ht.count");
		
		/*������ ������� */
		procCnt = 2;
		
		if (log.isDebugEnabled()){
			log.debug("procCnt = " + procCnt);
		}
		
		threadList = new Thread[procCnt];
		
		for(int i = 0; i<procCnt; i++) {
			
			/*
			 * 1. ȯ�氳���δ�� �����ڷῬ��
			 * 2. DB��������
			 * 3. Context
			 * 4. dataSource34 - Spring-db.xml ���� 
			 *    ����) ȯ�氳������� �ڷᰡ ���� �����Ƿ� ��û���� �����带 �����ϰ� �ʰ� 1������� �ֱ������� �����Ѵ�.
			 * 5. 20110928 �������� �������ϵ��� �߰���.   
			 * 6. 20140102 ����E���� ���� �����ڷ� ó�� �߰�
			 * */

			govid = CbUtil.getResource("ApplicationResource", "cyber.ht.gov0");    /*�ٸ� ���μ����� �����Ұ�*/
			orgcd = CbUtil.getResource("ApplicationResource", "cyber." + govid + ".org_cd");
			orgdb = "HI_" + orgcd;
			
			threadList[i] = new Thread(newProcess(govid, orgdb, i), "sub_2460thr_" + orgcd);			
			threadList[i].start();
			
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
				
				govid = CbUtil.getResource("ApplicationResource", "cyber.ht.gov" + i);
				orgcd = CbUtil.getResource("ApplicationResource", "cyber." + govid + ".org_cd");
				orgdb = "HI_" + orgcd;
				
				try {
					threadList[i] = new Thread(newProcess(govid, orgdb, i), govid);
				
					threadList[i].start();
					
				} catch (Exception e) {
				
					e.printStackTrace();
				}			
				
			}
			
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
	 * @param govId ��������
	 * @param orgcd �������
	 * @return
	 * @throws Exception
	 * ���� ����...�� ȣ���Ѵ�.
	 */
	private Txdm2460_process newProcess(String govId, String orgcd, int jobid) throws Exception {
		
		Txdm2460_process process = new Txdm2460_process();
		
		process.setContext(appContext);
		process.setGovId(govId);
		process.setUpId(jobid);
		process.setDataSource(orgcd);
		
		process.initProcess();		
		
		return process;
	}

}
