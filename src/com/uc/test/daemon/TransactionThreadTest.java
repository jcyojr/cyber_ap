package com.uc.test.daemon;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.uc.bs.cyber.daemon.Codm_BaseDaemon;
import com.uc.core.misc.Utils;
import com.uc.core.misc.XMLFileReader;
import com.uc.core.misc.XmlUtils;
import com.uc.core.spring.service.IbatisBaseService;
import com.uc.core.spring.service.UcContextHolder;

import java.io.IOException;
import java.util.ArrayList;
import com.uc.core.MapForm;

/*
 * 트랜잭션 예제...
 */
public class TransactionThreadTest extends Codm_BaseDaemon {

	private static IbatisBaseService  sqlService = null;
	
	private static IbatisBaseService  sqlService_cyber = null;
	
	private static String dataSource  = null;
	
	private int runTime = 0;
	
    public TransactionThreadTest() throws IOException, Exception {
		
		super();
		// TODO Auto-generated constructor stub
		
		
		
	}
	
	
	/**
	 * 
	 * @param context
	 * @param dataSrc
	 */
	public void setApp(ApplicationContext context, String dataSrc) {
		
		this.context = context;
		
		this.dataSource = dataSrc;

	}
	
	/*
	 * @see com.uc.core.spring.service.TransactionJob#transactionJob()
	 * 일련의 TRANSACTION 발생할 수 있는 여러개의 JOB을 동시에 처리한다.
	 */
	@Override
	public void transactionJob() {
		//TODO Auto-generated method stub
		
		//sqlService.queryForInsert("insert into TR_TEST values('1', '" + Thread.currentThread().getName() + "', '" + runTime+ "', '3')");
		//sqlService.queryForInsert("insert into TR_TEST values('2', '" + Thread.currentThread().getName() + "', '" + runTime+ "', '3')");
		//if(runTime > 0 && (runTime ++ % 100) == 0) throw new RuntimeException();
		
		log.debug("=========== transactionJob() Starting ============");
		
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

	}

	
	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		
		TransactionThreadTest testJob;
		
		try {
			// Log
			ApplicationContext context  = null;
			
			testJob = new TransactionThreadTest();
			
			Utils.setupLog4jConfig(testJob, "log4j.tomcat.xml");
			
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

			//testJob.setApp(context, "LT_470");
			//new Thread(testJob, "470").start();
			
			testJob.setApp(context, "LT_etax");
			
			UcContextHolder.setCustomerType(dataSource);
			
			sqlService = (IbatisBaseService) context.getBean("baseService");	
			
			sqlService_cyber = (IbatisBaseService) context.getBean("baseService_cyber");
						
			testJob.startJob();
			
          
			
			//new Thread(testJob, "etax").start();
			
			//testJob = new TransactionThreadTest();
			
			//testJob.setApp(context, "LT_110");
			//new Thread(testJob, "110").start();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}			
	}


	@Override
	public void mainProcess() throws Exception {
		// TODO Auto-generated method stub
		
		log.debug("===========함수시작2============");
		
		
		
		
		
		
	}


	@Override
	public void onInterrupt() throws Exception {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void initProcess() throws Exception {
		// TODO Auto-generated method stub
		
	}

  

	


}
