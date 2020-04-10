/**
 *  �ֽý��۸� : �λ�� ���̹����漼û
 *  ��  ��  �� : ����
 *  ��  ��  �� : ����� ����� �ΰ� �ڷ� ���� ���� 
 *     
 *               5�д����� ����...
 *  Ŭ����  ID : Txdm4311
 *  ���  ���� : ����� �׽�Ʈ�� ����ϰ� ������ ���Ͽ� �� ���μ����� �⵿���� �ʴ� ��츦 ���.
 *  ����  �̷� :
 * ------------------------------------------------------------------------
 *  �ۼ���      	�Ҽ�  		    ����            Tag           ����
 * ------------------------------------------------------------------------
 *  �۵���       ��ä��(��)      2011.10.12         %01%         �����ۼ�
 */
package com.uc.bs.cyber.daemon.txdm4311;

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
public class Txdm4311 extends Codm_BaseDaemon{

	private static String govid = "";
	private static String orgcd = "";
	private static String orgdb = "";
	
	/**
	 * ������
	 */
	public Txdm4311() throws IOException, Exception {
		
		super();

		log = LogFactory.getLog(this.getClass());

		this.loopTerm = 300;	/* 300�ʸ��� */
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Txdm4311 Txdm4311;
		ApplicationContext context  = null;
		
		try {
			// Log
			Txdm4311 = new Txdm4311();
			
			CbUtil.setupLog4jConfig(Txdm4311, "log4j.xml");
			
			try {
				String[] xmls = XMLFileReader.getAllFilePathArray(CbUtil.getResourcePath(Txdm4311, "/"), "SERVICE.XML");

				String strSrc = CbUtil.getResourcePath(Txdm4311, "/") + "config/sqlmaps.xml";

				String strDest = CbUtil.getResourcePath(Txdm4311, "/") + "config/sqlmapConfig.xml";

				XmlUtils.setXmlAttributes(Txdm4311, strSrc, strDest, "sqlMapConfig", "sqlMap", "url", xmls);

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
		
			context = new ClassPathXmlApplicationContext("config/Tax-Spring-db.xml");
			
			Txdm4311.setProcess("4311", "����� ����� �ΰ��ڷ� �ڷῬ��(�õ�)", "thr_4311");  /* �������� ��� */
			
			Txdm4311.setContext(context);
			
			Thread tx4311Thread = new Thread(Txdm4311);
			
			tx4311Thread.setName("thr_4311");
			
			tx4311Thread.start();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void initProcess() throws Exception {
		// TODO Auto-generated method stub
		
		log.debug("=====================================================================");
		log.debug("== " + this.getClass().getName() +  " initProcess() ==");
		log.debug("=====================================================================");
		
		int procCnt  = CbUtil.getResourceInt("ApplicationResource", "cyber.ht.count");
		
		if (log.isDebugEnabled()){
			log.debug("�⵿���μ��� ���� :: procCnt = " + procCnt);
		}
		
		threadList = new Thread[procCnt];
		
		for(int i = 0; i<procCnt; i++) {
			
			/*
			 * 1. ����� ������� �ڵ�
			 * 2. DB��������
			 * 3. Context
			 * 4. �õ� ����� �����̹Ƿ� �õ������θ� �����Ͽ� �����ϰ�
			 *    ��û�� �����Ƿ� ���� ������� �����ϰ� sub thread ���� ��Ƽ�� �����Ѵ�.
			 *    
			 * */

			govid = CbUtil.getResource("ApplicationResource", "cyber.st.gov" + i);
			orgcd = CbUtil.getResource("ApplicationResource", "cyber." + govid + ".org_cd");
			orgdb = "SI_000";
			
			threadList[i] = new Thread(newProcess(govid, orgdb), "sub_4311thr_" + orgcd);			
			threadList[i].start();
			
		}
	}

	@Override
	public void mainProcess() throws Exception {
		// TODO Auto-generated method stub
		for(int i = 0; i<threadList.length; i++) {
			
			if(!threadList[i].isAlive()) {	// �ٽ� �����
				
				log.debug("=====================================================================");
				log.debug("== " + this.getClass().getName()+ " mainProcess(" + i + ") ==");
				log.debug("=====================================================================");
				
				log.debug(" Thread(" + threadList[i].getName() + ") is " + threadList[i].isAlive());
				
				govid = CbUtil.getResource("ApplicationResource", "cyber.st.gov" + i);
				orgcd = CbUtil.getResource("ApplicationResource", "cyber." + govid + ".org_cd");
				orgdb = "SI_000";
				
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
	 * @param govId
	 * @return
	 * @throws Exception
	 * ���� ����...�� ȣ���Ѵ�.
	 */
	private Txdm4311_process newProcess(String govId, String orgcd) throws Exception {
		
		Txdm4311_process process = new Txdm4311_process();
		
		process.setContext(appContext);
		process.setGovId(govId);
		process.setDataSource(orgcd);
		
		process.initProcess();		
		
		return process;
	}

}
