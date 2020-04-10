/**
 *  �ֽý��۸� : �λ��û ���̹� ���漼û
 *  ��  ��  �� : ����
 *  ��  ��  �� : DB�� �о ������ �����ϰ� ��Ƽ�� ��� ����
 *  Ŭ����  ID : Codm0001_process : �� ó�������� �ۼ��ϴ� Ŭ����
 *  ����  �̷� :
 * ------------------------------------------------------------------------
 *  �ۼ���      	�Ҽ�  		    ����            Tag           ����
 * ------------------------------------------------------------------------
 *  ����       ��ä��(��)      2010.11.30         %01%         �����ۼ�
 *  �۵���       ��ä��(��)      2011.04.27         %01%         ����賦
 */

package com.uc.bs.cyber.daemon.sample;

import java.io.IOException;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.uc.bs.cyber.CbUtil;
import com.uc.bs.cyber.daemon.Codm_BaseProcess;
import com.uc.core.misc.XMLFileReader;
import com.uc.core.misc.XmlUtils;
import com.uc.core.spring.service.IbatisBaseService;
import com.uc.core.spring.service.IbatisService;

public class CodmTest extends Codm_BaseProcess {
	
	private IbatisBaseService sqlService ;
	
	public CodmTest() throws IOException, Exception {
		
		super();
		
		/**
		 * 5 �и��� ����
		 */
		loopTerm = 300;
		
		// TODO Auto-generated constructor stub
	}



	public void setApp(ApplicationContext context) {
		
		this.context = context;

	}
	
	
	public static void main(String[] args) {
		
		try {
			CodmTest process = new CodmTest();
			
			
			CbUtil.setupLog4jConfig(process, "log4j.tomcat.xml");
			
			/**
			 * ���ؽ�Ʈ ���� *Service.xml ������ ã�Ƽ� sqlmapConfig.xml ���Ͽ� ����Ѵ�
			 */
			try {
				String[] xmls = XMLFileReader.getAllFilePathArray(CbUtil.getResourcePath(process, "/"), "SERVICE.XML");

				String strSrc = CbUtil.getResourcePath(process, "/") + "config/sqlmaps.xml";

				String strDest = CbUtil.getResourcePath(process, "/") + "config/sqlmapConfig.xml";

				XmlUtils.setXmlAttributes(process, strSrc, strDest, "sqlMapConfig", "sqlMap", "url", xmls);

				System.out.println("2=========================================================");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
		
			process.setApp(new ClassPathXmlApplicationContext("config/Test-Spring-db.xml"));


			process.mainTransProcess();						
						
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		
	}
	
	
	/*Ʈ������� �����ϱ� ���� �Լ�.*/
	private void mainTransProcess(){
		
		sqlService = (IbatisBaseService) context.getBean("baseService");
		
		log.info("DUAL==" + sqlService.getOneFieldString("select sysdate from dual"));
		/*
		sqlService.queryForInsert("insert into stemp values('Hello out transaction'||to_char(sysdate, 'YYYYMMDDHH24MISS'))");
		
		if(true) throw new RuntimeException("Out");
		sqlService.queryForInsert("insert into stemp values('Hello out transaction'||to_char(sysdate, 'YYYYMMDDHH24MISS'))");
		

		try {
			this.startJob();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}			
		
		sqlService.queryForInsert("insert into stemp values('Hello out transaction'||to_char(sysdate, 'YYYYMMDDHH24MISS'))");
		sqlService.queryForInsert("insert into stemp values('Hello out transaction'||to_char(sysdate, 'YYYYMMDDHH24MISS'))");
		
		*/
	}
	
	/*
	 * Ʈ����� ������ ����ϴ� �Լ�.
	 * TransactionJob.class�� interface ����
	 * */
	@Override
	public void transactionJob() {
		// TODO Auto-generated method stub
		log.debug("=====================================================================");
		log.debug("=" + this.getClass().getName()+ " transactionJob() Start =");
		log.debug("=====================================================================");
		// Ʈ����� �׽�Ʈ�� ���� �����Դϴ�.
		// Ʈ������� �ݵ�� ���⼭ �����Ͻñ� �ٶ��ϴ�.

		
		
		try {
			Thread.sleep(1000);
			
			sqlService.queryForInsert("insert into stemp values('Hello in transaction'||to_char(sysdate, 'YYYYMMDDHH24MISS'))");
			sqlService.queryForInsert("insert into stemp values('Hello in transaction'||to_char(sysdate, 'YYYYMMDDHH24MISS'))");
			sqlService.queryForInsert("insert into stemp values('Hello in transaction'||to_char(sysdate, 'YYYYMMDDHH24MISS'))");
			sqlService.queryForInsert("insert into stemp values('Hello in transaction'||to_char(sysdate, 'YYYYMMDDHH24MISS'))");			
			
			
			Thread.sleep(1000);

			throw new RuntimeException("Test");
			
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
	}

	@Override
	public void setDatasrc(String datasrc) {
		// TODO Auto-generated method stub
		this.dataSource = datasrc;
	}



	@Override
	public void runProcess() throws Exception {
		// TODO Auto-generated method stub
		
	}
}
