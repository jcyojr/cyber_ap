/**
 *  주시스템명 : 부산시청 사이버 지방세청
 *  업  무  명 : 공통
 *  기  능  명 : DB를 읽어서 전문을 생성하고 멀티로 쏘는 샘플
 *  클래스  ID : Codm0001_process : 실 처리로직을 작성하는 클래스
 *  변경  이력 :
 * ------------------------------------------------------------------------
 *  작성자      	소속  		    일자            Tag           내용
 * ------------------------------------------------------------------------
 *  김대완       유채널(주)      2010.11.30         %01%         최초작성
 *  송동욱       유채널(주)      2011.04.27         %01%         보고배낌
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
		 * 5 분마다 돈다
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
			 * 컨텍스트 내의 *Service.xml 파일을 찾아서 sqlmapConfig.xml 파일에 등록한다
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
	
	
	/*트랜잭션을 실행하기 위한 함수.*/
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
	 * 트랜잭션 구현시 사용하는 함수.
	 * TransactionJob.class의 interface 구현
	 * */
	@Override
	public void transactionJob() {
		// TODO Auto-generated method stub
		log.debug("=====================================================================");
		log.debug("=" + this.getClass().getName()+ " transactionJob() Start =");
		log.debug("=====================================================================");
		// 트랜잭션 테스트에 대한 예제입니다.
		// 트랜잭션은 반드시 여기서 구현하시기 바랍니다.

		
		
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
