/**
 *  �ֽý��۸� : �λ�� ���̹����漼û
 *  ��  ��  �� : ����
 *  ��  ��  �� : ���� e ���� - ȯ�氳���δ�� �ǽð� ���� ����              
 *  Ŭ����  ID : Txdm2462
 *  ����  �̷� :
 * ------------------------------------------------------------------------
 *  �ۼ���      	�Ҽ�  		    ����            Tag           ����
 * ------------------------------------------------------------------------
 *  ����ȯ       ��ä��(��)      2014.01.14         %01%         �����ۼ�
 *  
 */
package com.uc.bs.cyber.daemon.txdm2462;

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
public class Txdm2462 extends Codm_BaseDaemon {

	private static String govid = "";
	private static String orgcd = "";
	private static String orgdb = "";
	
	/**
	 * @throws Exception 
	 * @throws IOException 
	 * 
	 */
	public Txdm2462() throws IOException, Exception {
		
		super();
		
		log = LogFactory.getLog(this.getClass());

		this.loopTerm = 30;	/* 30�ʸ��� */
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		Txdm2462 Txdm2462;
		ApplicationContext context  = null;
		
		try {
			// Log
			Txdm2462 = new Txdm2462();
			
			CbUtil.setupLog4jConfig(Txdm2462, "log4j.xml");
			
			try {
				String[] xmls = XMLFileReader.getAllFilePathArray(CbUtil.getResourcePath(Txdm2462, "/"), "SERVICE.XML");

				String strSrc = CbUtil.getResourcePath(Txdm2462, "/") + "config/sqlmaps.xml";

				String strDest = CbUtil.getResourcePath(Txdm2462, "/") + "config/sqlmapConfig.xml";

				XmlUtils.setXmlAttributes(Txdm2462, strSrc, strDest, "sqlMapConfig", "sqlMap", "url", xmls);

			} catch (Exception e) {
			
				e.printStackTrace();
			}			
		
			context = new ClassPathXmlApplicationContext("config/Spring-db.xml");
			
			Txdm2462.setProcess("2462", "ȯ�氳���δ�� �ǽð� ��������", "thr_2462");  /* �������� ��� */
			
			Txdm2462.setContext(context);
			
			Thread tx2462Thread = new Thread(Txdm2462);
			
			tx2462Thread.setName("thr_2462");
			
			tx2462Thread.start();
			
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
	@Override
	public void initProcess() throws Exception {
	
		log.debug("=====================================================================");
		log.debug("== " + this.getClass().getName() +  " initProcess() ==");
		log.debug("=====================================================================");
		
		int procCnt  = CbUtil.getResourceInt("ApplicationResource", "cyber.hv.count");
		
		if (log.isDebugEnabled()){
			log.debug("procCnt = " + procCnt);
		}
		
		threadList = new Thread[procCnt];
		
		for(int i = 0; i<procCnt; i++) {
			
			/*
			 * 1. ȯ�氳���δ�� �����������
			 * 2. DB��������
			 * 3. Context
			 * 4. ��û�ڵ尡 ���漼�Ͱ� ����.
			 *    DB�� ��û�� �����ϰ� ��û���� ��ϵǾ� �����Ƿ� DB ������ �����ϰ� ��û�ڵ常 �޸� ����
			 *    dataSource34 - Spring-db.xml ���� 
			 *    ����) ������� ������ ��û���� �Ѳ����� �����Ѵ�.
			 * */

			govid = CbUtil.getResource("ApplicationResource", "cyber.hv.gov" + i);
			orgcd = CbUtil.getResource("ApplicationResource", "cyber." + govid + ".org_cd");
			orgdb = "HI_000";
			
			threadList[i] = new Thread(newProcess(govid, orgdb), "sub_2462thr_" + orgcd);			
			threadList[i].start();
			
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
				
				govid = CbUtil.getResource("ApplicationResource", "cyber.hv.gov" + i);
				orgcd = CbUtil.getResource("ApplicationResource", "cyber." + govid + ".org_cd");
				orgdb = "HI_000";
				
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
	 * @param govId ��������
	 * @param orgcd �������
	 * @return
	 * @throws Exception
	 * ���� ����...�� ȣ���Ѵ�.
	 */
	private Txdm2462_process newProcess(String govId, String orgcd) throws Exception {
		
		Txdm2462_process process = new Txdm2462_process();
		
		process.setContext(appContext);
		process.setGovId(govId);
		process.setDataSource(orgcd);
		
		process.initProcess();		
		
		return process;
	}
}
