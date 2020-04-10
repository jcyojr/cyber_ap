/**
 *  �ֽý��۸� : �λ�� ���̹����漼û
 *  ��  ��  �� : ����
 *  ��  ��  �� : ǥ�����漼 �ڵ��� ����� ����/���� ����
 *               5�д����� ����...
 *  Ŭ����  ID : Txdm6000
 *  ����  �̷� :
 * ------------------------------------------------------------------------
 *  �ۼ���      	�Ҽ�  		    ����            Tag           ����
 * ------------------------------------------------------------------------
 *  ����ȯ       ��ä��(��)      2014.04.28         %01%         �����ۼ�
 */
package com.uc.bs.cyber.daemon.txdm6000;

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
public class Txdm6000 extends Codm_BaseDaemon {

	private static String govid = "";
	private static String orgcd = "";
	private static String orgdb = "";

    /*������*/
	public Txdm6000() throws IOException, Exception {
		super();
		
		
		log = LogFactory.getLog(this.getClass());

		this.loopTerm = 60;	/*   120�� -- 2�� */
		
	}
		
	/**
	 * @param args
	 */
	public static void main(String[] args) {
			
		Txdm6000 Txdm6000;
		ApplicationContext context  = null;
		
		try {
			// Log
			Txdm6000 = new Txdm6000();
			
			CbUtil.setupLog4jConfig(Txdm6000, "log4j.xml");
			
			try {
				String[] xmls = XMLFileReader.getAllFilePathArray(CbUtil.getResourcePath(Txdm6000, "/"), "SERVICE.XML");

				String strSrc = CbUtil.getResourcePath(Txdm6000, "/") + "config/sqlmaps.xml";

				String strDest = CbUtil.getResourcePath(Txdm6000, "/") + "config/sqlmapConfig.xml";

				XmlUtils.setXmlAttributes(Txdm6000, strSrc, strDest, "sqlMapConfig", "sqlMap", "url", xmls);

			} catch (Exception e) {
				
				e.printStackTrace();
			}			
		
			context = new ClassPathXmlApplicationContext("config/Spring-db.xml");
			
			Txdm6000.setProcess("6000", "�ڵ��� ����� ����/���� ����", "thr_6000");  /* �������� ��� */
			
			Txdm6000.setContext(context);
			
			Thread tx6000Thread = new Thread(Txdm6000);
			
			tx6000Thread.setName("thr_6000");
			
			tx6000Thread.start();
			
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
	@Override
	public void mainProcess() throws Exception {
		
        for(int i = 0; i<threadList.length; i++) {
			
			if(!threadList[i].isAlive()) {	// �ٽ� �����
				
				log.debug("=====================================================================");
				log.debug("== " + this.getClass().getName()+ " mainProcess(" + i + ") ==");
				log.debug("=====================================================================");
				
				log.debug(" Thread(" + threadList[i].getName() + ") is " + threadList[i].isAlive());
				
				govid = CbUtil.getResource("ApplicationResource", "cyber.tbl.gov" + i);
				orgcd = CbUtil.getResource("ApplicationResource", "cyber." + govid + ".org_cd");
				orgdb = "LT_etax";
				
								
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
		
		int procCnt  = CbUtil.getResourceInt("ApplicationResource", "cyber.tbl.count");
		
		if (log.isDebugEnabled()){
			log.debug("TBL ���� :: procCnt = " + procCnt);
		}
		
		threadList = new Thread[procCnt];
		
		for(int i = 0; i<procCnt; i++) {
			
			/*
			 * 1. ��û(��û)�ڵ�
			 * 2. DB��������
			 * 3. Context
			 * */

			govid = CbUtil.getResource("ApplicationResource", "cyber.tbl.gov" + i);
			orgcd = CbUtil.getResource("ApplicationResource", "cyber." + govid + ".org_cd");
			orgdb = "LT_etax";
			
			
			threadList[i] = new Thread(newProcess(govid, orgdb), "sub_6000thr_" + orgcd);			
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
	private Txdm6000_process newProcess(String govId, String orgcd) throws Exception {
		
		Txdm6000_process process = new Txdm6000_process();
		
		process.setContext(appContext);
		process.setGovId(govId);
		process.setDataSource(orgcd);
		
		process.initProcess();		
		
		return process;
	}

}
