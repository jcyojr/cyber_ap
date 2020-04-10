/**
 *  �ֽý��۸� : �λ�� ���̹����漼û
 *  ��  ��  �� : ����
 *  ��  ��  �� : DB�� �о ������ �����ϰ� ��Ƽ�� ��� ����
 *  Ŭ����  ID : Codm0001_01
 *  ����  �̷� :
 * ------------------------------------------------------------------------
 *  �ۼ���      	�Ҽ�  		    ����            Tag           ����
 * ------------------------------------------------------------------------
 *  ����       ��ä��(��)      2010.11.30         %01%         �����ۼ�
 *  �۵���       ��ä��(��)      2011.04.27         %01%         ����賦
 */

package com.uc.bs.cyber.daemon.sample;

import java.io.IOException;

import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.uc.core.misc.XMLFileReader;
import com.uc.core.misc.XmlUtils;
import com.uc.core.spring.service.UcContextHolder;
import com.uc.bs.cyber.CbUtil;
import com.uc.bs.cyber.daemon.Codm_BaseDaemon;

public class Codm0001 extends Codm_BaseDaemon {
	 
	private String dataSource  = null; 

	public Codm0001() throws IOException, Exception {
		
		super();
		// TODO Auto-generated constructor stub
		
		log = LogFactory.getLog(this.getClass());
		
		loopTerm = 60;	// 1  �и��� Thread ���� �����ض�
	}
	
	
	public String getDataSource() {
		return dataSource;
	}

	public void setDataSource(String dataSource) {
		this.dataSource = dataSource;
	}


	/**
	 * Main Thread
	 * �׽�Ʈ�� ������Ų��.
	 * @param args
	 */
	public static void main(String[] args) {
		
		Codm0001 Codm0001;
		ApplicationContext context  = null;
		
		try {
			// Log
			Codm0001 = new Codm0001();
			
			CbUtil.setupLog4jConfig(Codm0001, "log4j.tomcat.xml");
			
			try {
				String[] xmls = XMLFileReader.getAllFilePathArray(CbUtil.getResourcePath(Codm0001, "/"), "SERVICE.XML");

				String strSrc = CbUtil.getResourcePath(Codm0001, "/") + "config/sqlmaps.xml";

				String strDest = CbUtil.getResourcePath(Codm0001, "/") + "config/sqlmapConfig.xml";

				XmlUtils.setXmlAttributes(Codm0001, strSrc, strDest, "sqlMapConfig", "sqlMap", "url", xmls);

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
		
			context = new ClassPathXmlApplicationContext("config/*-Spring-db.xml");
			
			Codm0001.setProcess("0001", "�׽�Ʈ_0001", "thr_0001");  /* �������� ��� */
			
			Codm0001.setContext(context);
			
			Codm0001.setDataSource("LT_etax");  /*��ġ��ü ID*/

			UcContextHolder.setCustomerType(Codm0001.getDataSource());
			
			Thread bs0001Thread = new Thread(Codm0001);
			
			bs0001Thread.setName("thr_0001");
			
			bs0001Thread.start();
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	/*���ε����� INTERUPT �߻��� ���͵� INTERUPT �ɾ ���۳���... 
	 *Codm_BaseDaemon.class���� INTERUPT�ɸ��� �ڵ����� �ɸ���...
	 * */
	@Override
	public void onInterrupt() {
		// TODO Auto-generated method stub
		
		if(threadList == null) return;
		
		for(int i = 0; i<threadList.length; i++) {
			threadList[i].interrupt();
		}
	}

    /*
     * ����� 10�ʴ����� thick �մϴ�.
     * ���� ���⼭�� �� ���� ���ؼ� ������ ������ �����ϵ��� �մϴ�.
     * */
	@Override
	public void mainProcess() {
		// TODO Auto-generated method stub
				
		for(int i = 0; i<threadList.length; i++) {
			

			if(!threadList[i].isAlive()) {	// �ٽ� �����
				
				log.debug("=====================================================================");
				log.info("== " + this.getClass().getName()+ " mainProcess() ==");
				log.debug("=====================================================================");
				
				log.debug(" Thread(" + threadList[i].getName() + ") is " + threadList[i].isAlive());
				
				String govId = CbUtil.getResource("ApplicationResource", "cyber.lt.gov" + i);
				
				try {
					threadList[i] = new Thread(newProcess(govId), govId);
				
					threadList[i].start();
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
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
		// TODO Auto-generated method stub
		log.debug("=====================================================================");
		log.debug("== " + this.getClass().getName() +  " initProcess() ==");
		log.debug("=====================================================================");
		
		int procCnt  = CbUtil.getResourceInt("ApplicationResource", "cyber.lt.count");
		
		threadList = new Thread[procCnt];
		
		for(int i = 0; i<procCnt; i++) {

			String govId = CbUtil.getResource("ApplicationResource", "cyber.lt.gov" + i);
			
			threadList[i] = new Thread(newProcess(govId), govId);			
			threadList[i].start();
			
		}
		
	}
	
	/*Ʈ������� �ʿ��� ��� ����Ѵ�.
	 *interface �޼��� ����
	 * */
	@Override
	public void transactionJob() {
		// TODO Auto-generated method stub
		// ...Here is nothing ...
	}
	
	/**
	 * @param govId
	 * @return
	 * @throws Exception
	 * ���� ����...�� ȣ���Ѵ�.
	 */
	private Codm0001_process newProcess(String govId) throws Exception {
		
		Codm0001_process process = new  Codm0001_process();
		
		process.setContext(appContext);
		process.setGovId(govId);
		
		process.initProcess();		
		
		return process;
	}


}
