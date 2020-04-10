/**
 *  �ֽý��۸� : �λ�� ���̹����漼û
 *  ��  ��  �� : ����
 *  ��  ��  �� : ��������� �ֹμ� ���μ��� �������� ���(��û��) ���� ���� 
 *     
 *               5�д����� ����...
 *  Ŭ����  ID : Txdm1274
 *  ���  ���� : 
 *  ����  �̷� :
 * ------------------------------------------------------------------------
 *  �ۼ���      	�Ҽ�  		    ����            Tag           ����
 * ------------------------------------------------------------------------
 *  �۵���       ��ä��(��)      2011.12.07         %01%         �����ۼ�
 */
package com.uc.bs.cyber.daemon.txdm1291;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.dao.DuplicateKeyException;

import com.uc.bs.cyber.CbUtil;
import com.uc.bs.cyber.daemon.Codm_BaseDaemon;
import com.uc.core.MapForm;
import com.uc.core.misc.XMLFileReader;
import com.uc.core.misc.XmlUtils;

/**
 * @author Administrator
 *
 */
public class Txdm1291 extends Codm_BaseDaemon{

	private static String govid = "";
	private static String orgcd = "";
	private static String orgdb = "";
	
	private int mainloop = 0;
	
	private int loop_cnt = 0, insert_cnt = 0, update_cnt = 0;
	
	private String soinFilePath = "";
	private String moveFilePath = "";
	
	ArrayList<MapForm> alSoinList;
	
	/**
	 * ������
	 */
	public Txdm1291() throws IOException, Exception {
		
		super();

		log = LogFactory.getLog(this.getClass());

		this.loopTerm = 250;	
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Txdm1291 Txdm1291;
		ApplicationContext context  = null;
		
		System.out.println("== OS Name = " + System.getProperty("os.name"));
		System.out.println("== OS Version = " + System.getProperty("os.version"));
		
		try {
			
			// Log
			Txdm1291 = new Txdm1291();
			
			CbUtil.setupLog4jConfig(Txdm1291, "log4j.xml");
			
			try {
				
				String[] xmls = XMLFileReader.getAllFilePathArray(CbUtil.getResourcePath(Txdm1291, "/"), "SERVICE.XML");

				String strSrc = CbUtil.getResourcePath(Txdm1291, "/") + "config/sqlmaps.xml";

				String strDest = CbUtil.getResourcePath(Txdm1291, "/") + "config/sqlmapConfig.xml";

				XmlUtils.setXmlAttributes(Txdm1291, strSrc, strDest, "sqlMapConfig", "sqlMap", "url", xmls);

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
		
			context = new ClassPathXmlApplicationContext("config/Tax-Spring-db.xml");
			
			Txdm1291.setProcess("1274", "��������� �ֹμ� ���μ��� �����������(��û��)", "thr_1274");  /* �������� ��� */
			
			Txdm1291.setContext(context);
			
			Thread tx1274Thread = new Thread(Txdm1291);
			
			tx1274Thread.setName("thr_1274");
			
			tx1274Thread.start();
			
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
		
		/*�ʱ� ����� 
		 * 1. Ư��¡�� ���������� �о DB�� ����Ѵ�.
		 * 2. Ư��¡�� ������ ��û���� ����Ѵ�.(��Ƽ������)
		 * */
	
		
		/*2. �������� ����(��û��)*/
		int procCnt  = CbUtil.getResourceInt("ApplicationResource", "cyber.hv.count");
		
		if (log.isDebugEnabled()){
			log.debug("�⵿���μ��� ���� :: procCnt = " + procCnt);
		}
		
		threadList = new Thread[procCnt];
		
		for(int i = 0; i<procCnt; i++) {
			
			/*
			 * 1. ��û(��û)�ڵ�
			 * 2. DB��������
			 * 3. Context
			 * 4. ����
			 *    ���������� �о ��û���� �Է��Ѵ�.
			 *    ���������� ��û���� �������� �ʰ� ���� ���� �ϳ��� ���ϸ� ������
			 *    ��������� Ư��¡�� ��������
			 * */

			govid = CbUtil.getResource("ApplicationResource", "cyber.hv.gov" + i);
			orgcd = CbUtil.getResource("ApplicationResource", "cyber." + govid + ".org_cd");
			orgdb = "JI_" + orgcd;
			
			threadList[i] = new Thread(newProcess(govid, orgdb), "sub_1272thr_" + orgcd);			
			threadList[i].start();
			
		}
	}

	/*10 �� ������ thick */
	@Override
	public void mainProcess() throws Exception {
		// TODO Auto-generated method stub
		
		mainloop++;
		
		for(int i = 0; i < threadList.length; i++) {
			
			if(!threadList[i].isAlive()) {	// �ٽ� �����
				
				log.debug("=====================================================================");
				log.debug("== " + this.getClass().getName()+ " mainProcess(" + i + ") ==");
				log.debug("=====================================================================");
				
				log.debug(" Thread(" + threadList[i].getName() + ") is " + threadList[i].isAlive());
				
				govid = CbUtil.getResource("ApplicationResource", "cyber.hv.gov" + i);
				orgcd = CbUtil.getResource("ApplicationResource", "cyber." + govid + ".org_cd");
				orgdb = "JI_" + orgcd;
				
				try {
					threadList[i] = new Thread(newProcess(govid, orgdb), "sub_1271thr_" + orgcd);		
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
	private Txdm1291_process newProcess(String govId, String orgcd) throws Exception {
		
		Txdm1291_process process = new Txdm1291_process();
		
		process.setContext(appContext);
		process.setGovId(govId);
		process.setDataSource(orgcd);
		
		process.initProcess();		
		
		return process;
	}


}
