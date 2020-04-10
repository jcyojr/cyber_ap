/**
 *  �ֽý��۸� : �λ�� ���̹����漼û
 *  ��  ��  �� : ����
 *  ��  ��  �� : �ٷΰ��� ����ó��
 *               5�д����� ����...
 *  Ŭ����  ID : Txdm2630
 *  ����  �̷� :
 * ------------------------------------------------------------------------
 *  �ۼ���      	�Ҽ�  		    ����            Tag           ����
 * ------------------------------------------------------------------------
 *  ����ȯ         ��ä��        2013.09.11         %01%         �����ۼ�
 */
package com.uc.bs.cyber.daemon.txdm2630;

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
public class Txdm2630 extends Codm_BaseDaemon {

	private static String govid = "";
	private static String orgcd = "";
	private static String orgdb = "";

    /*������*/
	public Txdm2630() throws IOException, Exception {
		
		super();
		
		log = LogFactory.getLog(this.getClass());

		this.loopTerm = 300;	/* 300�ʸ��� */
		
	}
		
	/**
	 * @param args
	 */
	public static void main(String[] args) {
			
		Txdm2630 Txdm2630;
		ApplicationContext context  = null;
		
		try {
			// Log
			Txdm2630  = new Txdm2630();
			
			CbUtil.setupLog4jConfig(Txdm2630 , "log4j.xml");
			
			try {
				String[] xmls = XMLFileReader.getAllFilePathArray(CbUtil.getResourcePath(Txdm2630 , "/"), "SERVICE.XML");

				String strSrc = CbUtil.getResourcePath(Txdm2630 , "/") + "config/sqlmaps.xml";

				String strDest = CbUtil.getResourcePath(Txdm2630 , "/") + "config/sqlmapConfig.xml";

				XmlUtils.setXmlAttributes(Txdm2630 , strSrc, strDest, "sqlMapConfig", "sqlMap", "url", xmls);

			} catch (Exception e) {
				
				e.printStackTrace();
			}			
		
			context = new ClassPathXmlApplicationContext("config/Sudo-Spring-db.xml");
			
			Txdm2630.setProcess("2630", "�ٷΰ��� ����ó��", "thr_2630");  /* �������� ��� */
			
			Txdm2630.setContext(context);
			
			Thread tx2630Thread = new Thread(Txdm2630);
			
			tx2630Thread.setName("thr_2630");
			
			tx2630Thread.start();
			
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
				
				govid = CbUtil.getResource("ApplicationResource", "cyber.br.gov" + i);
				orgcd = CbUtil.getResource("ApplicationResource", "cyber." + govid + ".org_cd");
				orgdb = "SU_" + orgcd;
				
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
		
		int procCnt  = CbUtil.getResourceInt("ApplicationResource", "cyber.br.count");
		
		if (log.isDebugEnabled()){
			log.debug("�ٷΰ��� ����ó�� :: procCnt = " + procCnt);
		}
		
		threadList = new Thread[procCnt];
		
		for(int i = 0; i<procCnt; i++) {
			
			/*
			 * 1. ��û(��û)�ڵ�
			 * 2. DB��������
			 * 3. Context
			*/

			govid = CbUtil.getResource("ApplicationResource", "cyber.br.gov" + i);
			orgcd = CbUtil.getResource("ApplicationResource", "cyber." + govid + ".org_cd");
			orgdb = "SU_" + orgcd;
			
			threadList[i] = new Thread(newProcess(govid, orgdb), "sub_2630thr_" + orgcd);			
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
	private Txdm2630_process newProcess(String govId, String orgcd) throws Exception {
		
		Txdm2630_process process = new Txdm2630_process();
		
		process.setContext(appContext);
		process.setGovId(govId);
		process.setDataSource(orgcd);
		
		process.initProcess();		
		
		return process;
	}

}
