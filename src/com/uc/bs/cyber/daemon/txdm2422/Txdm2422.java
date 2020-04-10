/**
 *  �ֽý��۸� : �λ�� ���̹����漼û
 *  ��  ��  �� : ����
 *  ��  ��  �� : �������ߺδ�� �����ڷ�����
 *               5�д����� ����...
 *  Ŭ����  ID : Txdm2422
 *  ����  �̷� :
 * ------------------------------------------------------------------------
 *  �ۼ���      	�Ҽ�  		    ����            Tag           ����
 * ------------------------------------------------------------------------
 *  ǥ����       �ٻ�ý���      2013.12.13         %01%         �����ۼ�
 */
package com.uc.bs.cyber.daemon.txdm2422;

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
public class Txdm2422 extends Codm_BaseDaemon {

	private static String govid = "";
	private static String orgcd = "";
	private static String orgdb = "";

    /*������*/
	public Txdm2422() throws IOException, Exception {
		super();
		// TODO Auto-generated constructor stub
		
		log = LogFactory.getLog(this.getClass());

		this.loopTerm = 300;	/*   300�ʸ��� */
		
	}
		
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		Txdm2422 Txdm2422;
		ApplicationContext context  = null;
		
		try {
			// Log
			Txdm2422 = new Txdm2422();
			
			CbUtil.setupLog4jConfig(Txdm2422, "log4j.xml");
			
			try {
				String[] xmls = XMLFileReader.getAllFilePathArray(CbUtil.getResourcePath(Txdm2422, "/"), "SERVICE.XML");

				String strSrc = CbUtil.getResourcePath(Txdm2422, "/") + "config/sqlmaps.xml";

				String strDest = CbUtil.getResourcePath(Txdm2422, "/") + "config/sqlmapConfig.xml";

				XmlUtils.setXmlAttributes(Txdm2422, strSrc, strDest, "sqlMapConfig", "sqlMap", "url", xmls);

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
		
			context = new ClassPathXmlApplicationContext("config/Sw-Spring-db.xml");
			
			Txdm2422.setProcess("2422", "�������ߺδ�� �����ڷῬ��", "thr_2422");  /* �������� ��� */
			
			Txdm2422.setContext(context);
			
			Thread tx2422Thread = new Thread(Txdm2422);
			
			tx2422Thread.setName("thr_2422");
			
			tx2422Thread.start();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
    /*
     * ����� 10�ʴ����� thick �մϴ�.
     * ���� ���⼭�� �� ���� ���ؼ� ������ ������ �����ϵ��� �մϴ�.
     * */
	@Override
	public void mainProcess() throws Exception {
		// TODO Auto-generated method stub
		
		//i=1 ���� �����Ͽ� ��û�� �����Ѵ�.
        for(int i = 1; i<threadList.length; i++) {
			
			if(!threadList[i].isAlive()) {	// �ٽ� �����
				
				log.debug("=====================================================================");
				log.debug("== " + this.getClass().getName()+ " mainProcess(" + i + ") ==");
				log.debug("=====================================================================");
				
				log.debug(" Thread(" + threadList[i].getName() + ") is " + threadList[i].isAlive());
				
				govid = CbUtil.getResource("ApplicationResource", "cyber.seoi.gov" + i);
				orgcd = CbUtil.getResource("ApplicationResource", "cyber." + govid + ".org_cd");
				orgdb = "NI_" + orgcd;
				
				/* ��û ���� ���� */
				//if(orgcd.equals("626")) continue;
				
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
	
	/*
	 * ���� ���μ��� �����մϴ�.
	 * �ʱ���۽� �ѹ��� �����մϴ�.
	 * */
	public void initProcess() throws IOException, Exception {
		// TODO Auto-generated method stub
		log.debug("=====================================================================");
		log.debug("== " + this.getClass().getName() +  " initProcess() ==");
		log.debug("=====================================================================");
		
		int procCnt  = CbUtil.getResourceInt("ApplicationResource", "cyber.seoi.count");
		
		if (log.isDebugEnabled()){
			log.debug("��û���ܼ��� ���� :: procCnt = " + procCnt);
		}
		
		threadList = new Thread[procCnt];
		
		//i=1 ���� �����Ͽ� ��û�� �����Ѵ�.
		for(int i = 1; i<procCnt; i++) {
			
			/*
			 * 1. ��û(��û)�ڵ�
			 * 2. DB��������
			 * 3. Context
			 * */

			govid = CbUtil.getResource("ApplicationResource", "cyber.seoi.gov" + i);
			orgcd = CbUtil.getResource("ApplicationResource", "cyber." + govid + ".org_cd");
			orgdb = "NI_" + orgcd;
			
			/* ��û ���� ���� */
			//if(orgcd.equals("626")) continue;
			
			threadList[i] = new Thread(newProcess(govid, orgdb), "sub_2422thr_" + orgcd);			
			threadList[i].start();
			
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
	private Txdm2422_process newProcess(String govId, String orgcd) throws Exception {
		
		Txdm2422_process process = new Txdm2422_process();
		
		process.setContext(appContext);
		process.setGovId(govId);
		process.setDataSource(orgcd);
		
		process.initProcess();		
		
		return process;
	}

}
