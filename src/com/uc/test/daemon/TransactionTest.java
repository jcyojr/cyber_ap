
package com.uc.test.daemon;

import java.io.IOException;
import java.util.ArrayList;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.uc.bs.cyber.daemon.Codm_BaseDaemon;
import com.uc.core.MapForm;
import com.uc.core.misc.Utils;
import com.uc.core.misc.XMLFileReader;
import com.uc.core.misc.XmlUtils;
import com.uc.core.spring.service.IbatisBaseService;
import com.uc.core.spring.service.UcContextHolder;

import com.uc.bs.cyber.CbUtil;

public class TransactionTest extends Codm_BaseDaemon {

	private static IbatisBaseService  sqlService = null;
	
	private static IbatisBaseService  sqlService_cyber = null;
	
	private long elapseTime1 = 0;
	private long elapseTime2 = 0;
	
	private static String dataSource  = null;
		
	public TransactionTest() throws IOException, Exception {
		
		super();
		// TODO Auto-generated constructor stub
		
		loopTerm = 120;
	}
	
	public void setApp(ApplicationContext context, String dataSrc) {
		
		this.context = context;
		this.dataSource = dataSrc;
	}
	
	@Override
	public void transactionJob() {
		// TODO Auto-generated method stub
		
		log.info("======transactionJob Starting...=======");
		
		try {
			
			UcContextHolder.setCustomerType("LT_etax");
			
			sqlService = (IbatisBaseService) this.context.getBean("baseService");	 
			
			sqlService_cyber = (IbatisBaseService) this.context.getBean("baseService_cyber");
			
			ArrayList<MapForm>  yearList  =  sqlService.queryForList("Transaction.etax_Yearlist",  null);
			
			for (int i = 0;  i < yearList.size(); i++) {
				
				MapForm  yearMap  =  yearList.get(i);
				
				if (yearMap  ==  null  ||  yearMap.isEmpty() )  {
					continue;
				}
				
				ArrayList<MapForm>  cmdList  =  sqlService.queryForList("Transaction.etaxlist",  yearMap);
				
				System.out.println("cmdPayList.size() = " + cmdList.size());
				
				for (int j = 0; j < cmdList.size(); j++) {
					
					MapForm  cmdMap  =  cmdList.get(j);
					
					if (cmdMap  ==  null  ||  cmdMap.isEmpty() )  {
						continue;
					}
					
					
					sqlService_cyber.queryForInsert("Transaction.etaxinsert",  cmdMap);
					
					//if (i > 3) throw new RuntimeException();
				}

			}

			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	

	}

	
	/**
	 * ����Thread 
	 * @param args
	 */
	public static void main(String[] args) {
		
		mainSingleProcess();

	}
	
	/*�������μ����� Class �� ����� ������ ���⼭ �ڵ��Ѵ�.*/
	private static void mainSingleProcess(){
		
		TransactionTest testJob;
		
		ApplicationContext context  = null;

		try {
			// Log
			
			testJob = new TransactionTest();
			
			Utils.setupLog4jConfig (testJob, "log4j.tomcat.xml");
			
			try {
				String[] xmls = XMLFileReader.getAllFilePathArray(Utils.getResourcePath(testJob, "/"), "SERVICE.XML");

				String strSrc = Utils.getResourcePath(testJob, "/") + "config/sqlmaps.xml";

				String strDest = Utils.getResourcePath(testJob, "/") + "config/sqlmapConfig.xml";

				XmlUtils.setXmlAttributes(testJob, strSrc, strDest, "sqlMapConfig", "sqlMap", "url", xmls);

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
		
			context = new ClassPathXmlApplicationContext("config/Tomcat-Spring-*.xml");

			testJob.setApp(context, "LT_etax");
			testJob.setContext(context);
			
			UcContextHolder.setCustomerType(dataSource);
			
			sqlService = (IbatisBaseService) context.getBean("baseService");	
			
			sqlService_cyber = (IbatisBaseService) context.getBean("baseService_cyber");
			
			testJob.startJob();
			
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
		
		log.debug("===========mainProcess()============");
		
		elapseTime2 = System.currentTimeMillis();
		
		log.debug("Loop �ð� = " + (elapseTime2 - elapseTime1) /1000);
		
	}

	/*���ε����� INTERUPT �߻��� ���͵� INTERUPT �ɾ ���۳���... 
	 *Codm_BaseDaemon.class���� INTERUPT�ɸ��� �ڵ����� �ɸ���...
	 * */
	@Override
	public void onInterrupt() throws Exception {
		// TODO Auto-generated method stub
		
		log.debug("===========onInterrupt()============");
		
		if(threadList == null) return;
		
		for(int i = 0; i<threadList.length; i++) {
			threadList[i].interrupt();
		}
	}
	
	/*
	 * ���� ���μ��� �����մϴ�.
	 * �ʱ���۽� �ѹ��� �����մϴ�.
	 * */
	public void initProcess() throws IOException, Exception {
		
		log.debug("===========initProcess(s)============");

		elapseTime1 = System.currentTimeMillis();
		
		mainSingleProcess();
		
		log.debug("===========initProcess(e)============");
	}
	


}
