/**
 *  �ֽý��۸� : �λ�� ���̹����漼û
 *  ��  ��  �� : ����
 *  ��  ��  �� : ���������ݰ��·� �����ڷῬ��
 *               5�д����� ����...
 *               ���������ݰ��·�� ������ ��û������ �������� �ʰ� �õ������θ� �����Ѵ�
 *  Ŭ����  ID : Txdm2473
 *  ����  �̷� :
 * ------------------------------------------------------------------------
 *  �ۼ���      	�Ҽ�  		    ����            Tag           ����
 * ------------------------------------------------------------------------
 *  �۵���       ��ä��(��)      2011.06.03         %01%         �����ۼ�
 */
package com.uc.bs.cyber.daemon.txdm2473;

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
 */
public class Txdm2473 extends Codm_BaseDaemon {

	private static String govid = "";
	private static String orgcd = "";
	private static String orgdb = "";
	
	/**
	 * ������
	 */
	public Txdm2473() throws IOException, Exception {
		// TODO Auto-generated constructor stub
		super();
		
		log = LogFactory.getLog(this.getClass());

		this.loopTerm = 300;	/* 300���� */
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Txdm2473 Txdm2473;
		ApplicationContext context  = null;
		
		try {
			// Log
			Txdm2473 = new Txdm2473();
			
			CbUtil.setupLog4jConfig(Txdm2473, "log4j.xml");
			
			try {
				String[] xmls = XMLFileReader.getAllFilePathArray(CbUtil.getResourcePath(Txdm2473, "/"), "SERVICE.XML");

				String strSrc = CbUtil.getResourcePath(Txdm2473, "/") + "config/sqlmaps.xml";

				String strDest = CbUtil.getResourcePath(Txdm2473, "/") + "config/sqlmapConfig.xml";

				XmlUtils.setXmlAttributes(Txdm2473, strSrc, strDest, "sqlMapConfig", "sqlMap", "url", xmls);

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
		
			context = new ClassPathXmlApplicationContext("config/Spring-db.xml");
			
			Txdm2473.setProcess("2473", "���������ݰ��·� �����ڷῬ��", "thr_2473");  /* �������� ��� */
			
			Txdm2473.setContext(context);
			
			Thread tx2473Thread = new Thread(Txdm2473);
			
			tx2473Thread.setName("thr_2473");
			
			tx2473Thread.start();
			
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
		
		int procCnt  = CbUtil.getResourceInt("ApplicationResource", "cyber.se.count");
		
		if (log.isDebugEnabled()){
			log.debug("procCnt = " + procCnt);
		}
		
		threadList = new Thread[procCnt];
				
		for(int i = 0; i<procCnt; i++) {
			
			/*
			 * 1. ���������ݰ��·�
			 * 2. DB��������
			 * 3. Context
			 * 4. ��û�ڵ尡 ���ܼ��԰� ���������� ���ܼ��԰��� ���� �̿��Ѵ�.
			 *    DB�� ��û�� �����ϰ� ��û���� ��ϵǾ� �����Ƿ� DB ������ �����ϰ� ��û�ڵ常 �޸� ����
			 *    dataSource34 - Spring-db.xml ���� 
			 * */
			
			/*
			 * �۷ι� Ʈ����� �� ����δ��� ���̱� ���ؼ�
			 * 1���� Ʈ����ǿ� ���� ����� ���� �����
			 * */

			govid = CbUtil.getResource("ApplicationResource", "cyber.se.gov" + i); 
			orgcd = CbUtil.getResource("ApplicationResource", "cyber." + govid + ".org_cd");
			
			orgdb = "TI_000"; /*DB ��������*/
			
			//debug mode
			
			threadList[i] = new Thread(newProcess(govid, orgdb), "sub_2473thr_" + orgcd);			
			threadList[i].start();
			
		}
	}
    /*
     * ����� 10�ʴ����� thick �մϴ�.
     * ���� ���⼭�� �� ���� ���ؼ� ������ ������ �����ϵ��� �մϴ�.
     * */
	@Override
	public void mainProcess() throws Exception {
		// TODO Auto-generated method stub
		for(int i = 0; i<threadList.length; i++) {
			
			if(!threadList[i].isAlive()) {	// �ٽ� �����
				
				log.debug("=====================================================================");
				log.debug("== " + this.getClass().getName()+ " mainProcess(" + i + ") ==");
				log.debug("=====================================================================");
				
				log.debug(" Thread(" + threadList[i].getName() + ") is " + threadList[i].isAlive());
				
				govid = CbUtil.getResource("ApplicationResource", "cyber.se.gov" + i);
				orgcd = CbUtil.getResource("ApplicationResource", "cyber." + govid + ".org_cd");
				
				orgdb = "TI_000"; /*DB ��������*/
				
				
				try {
					threadList[i] = new Thread(newProcess(govid, orgdb), govid);
				
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
	 * @param govId ��������
	 * @param orgcd �������
	 * @return
	 * @throws Exception
	 * ���� ����...�� ȣ���Ѵ�.
	 */
	private Txdm2473_process newProcess(String govId, String orgcd) throws Exception {
		
		Txdm2473_process process = new Txdm2473_process();
		
		process.setContext(appContext);
		process.setGovId(govId);
		process.setDataSource(orgcd);
		
		process.initProcess();		
		
		
		return process;
	}

}
