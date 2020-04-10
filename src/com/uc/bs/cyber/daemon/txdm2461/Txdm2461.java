/**
 *  �ֽý��۸� : �λ�� ���̹����漼û
 *  ��  ��  �� : ����
 *  ��  ��  �� : ȯ�氳���δ�� �����������
 *              
 *  Ŭ����  ID : Txdm2461
 *  ����  �̷� :
 * ------------------------------------------------------------------------
 *  �ۼ���      	�Ҽ�  		    ����            Tag           ����
 * ------------------------------------------------------------------------
 *  �۵���       ��ä��(��)      2011.06.06         %01%         �����ۼ�
 */
package com.uc.bs.cyber.daemon.txdm2461;

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
public class Txdm2461 extends Codm_BaseDaemon {

	private static String govid = "";
	private static String orgcd = "";
	private static String orgdb = "";
	
	/**
	 * @throws Exception 
	 * @throws IOException 
	 * 
	 */
	public Txdm2461() throws IOException, Exception {
		// TODO Auto-generated constructor stub
		super();
		
		log = LogFactory.getLog(this.getClass());

		this.loopTerm = 600;	/* 600�ʸ��� */
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		Txdm2461 Txdm2461;
		ApplicationContext context  = null;
		
		try {
			// Log
			Txdm2461 = new Txdm2461();
			
			CbUtil.setupLog4jConfig(Txdm2461, "log4j.xml");
			
			try {
				String[] xmls = XMLFileReader.getAllFilePathArray(CbUtil.getResourcePath(Txdm2461, "/"), "SERVICE.XML");

				String strSrc = CbUtil.getResourcePath(Txdm2461, "/") + "config/sqlmaps.xml";

				String strDest = CbUtil.getResourcePath(Txdm2461, "/") + "config/sqlmapConfig.xml";

				XmlUtils.setXmlAttributes(Txdm2461, strSrc, strDest, "sqlMapConfig", "sqlMap", "url", xmls);

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
		
			context = new ClassPathXmlApplicationContext("config/Spring-db.xml");
			
			Txdm2461.setProcess("2461", "ȯ�氳���δ�� �����������", "thr_2461");  /* �������� ��� */
			
			Txdm2461.setContext(context);
			
			Thread tx2461Thread = new Thread(Txdm2461);
			
			tx2461Thread.setName("thr_2461");
			
			tx2461Thread.start();
			
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
			
			threadList[i] = new Thread(newProcess(govid, orgdb), "sub_2461thr_" + orgcd);			
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
				
				govid = CbUtil.getResource("ApplicationResource", "cyber.hv.gov" + i);
				orgcd = CbUtil.getResource("ApplicationResource", "cyber." + govid + ".org_cd");
				orgdb = "HI_000";
				
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
	private Txdm2461_process newProcess(String govId, String orgcd) throws Exception {
		
		Txdm2461_process process = new Txdm2461_process();
		
		process.setContext(appContext);
		process.setGovId(govId);
		process.setDataSource(orgcd);
		
		process.initProcess();		
		
		return process;
	}
}
