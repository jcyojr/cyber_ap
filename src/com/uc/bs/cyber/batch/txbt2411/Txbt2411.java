/**
 *  주시스템명 : 부산시 사이버지방세청
 *  업  무  명 : 배치
 *  기  능  명 : 표준세외수입 부과자료연계(구청)
 *               배치로 구동되며 저녁 9시에 한번만 
 *  클래스  ID : Txbt2411
 *  변경  이력 :
 * ------------------------------------------------------------------------
 *  작성자      	소속  		    일자            Tag           내용
 * ------------------------------------------------------------------------
 *  송동욱       유채널(주)      2011.05.20         %01%         최초작성
 */
package com.uc.bs.cyber.batch.txbt2411;


import java.io.IOException;

import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.ibatis.sqlmap.client.SqlMapException;
import com.uc.bs.cyber.batch.Txdm_BatchProcess;
import com.uc.core.spring.service.UcContextHolder;
import com.uc.core.misc.XMLFileReader;
import com.uc.core.misc.XmlUtils;
import com.uc.bs.cyber.CbUtil;

/**
 * @author Administrator
 *
 */
public class Txbt2411 extends Txdm_BatchProcess {
	
	private static String govid = "";
	private static String orgcd = "";
		
	/**
	 * 생성자
	 */
	public Txbt2411() {
		// TODO Auto-generated constructor stub
		super(); /*생성자의 첫번째명령문*/
		
		log = LogFactory.getLog(this.getClass());

	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
	  
		System.out.println("=================================================");
		System.out.println("사이버지방세청 표준세외수입 부과자료연계 Started");
		System.out.println("=================================================");	
		
		Txbt2411 batch;
		
		batch = new Txbt2411();
		
		ApplicationContext context  = null;
		
		System.out.println("== OS Name = " + System.getProperty("os.name"));
		System.out.println("== OS Version = " + System.getProperty("os.version"));
		System.out.println("== FILE Separator = " + System.getProperty("file.separator"));
		
		try {
			//Log
			
			CbUtil.setupLog4jConfig(batch, "log4j.txbt2411.xml");
			
			try {
				String[] xmls = XMLFileReader.getAllFilePathArray(CbUtil.getResourcePath(batch, "/"), "SERVICE.XML");

				String strSrc = CbUtil.getResourcePath(batch, "/") + "config/sqlmaps.xml";

				String strDest = CbUtil.getResourcePath(batch, "/") + "config/sqlmapConfig.xml";

				XmlUtils.setXmlAttributes(batch, strSrc, strDest, "sqlMapConfig", "sqlMap", "url", xmls);

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
		
			context = new ClassPathXmlApplicationContext("config/Seoi-Spring-db.xml");
			
			batch.setProcess("2411", "표준세외수입 부과자료연계(구청별)", "thr_2411");  /* 업무데몬 등록 */
			
			batch.setContext(context);
			
			Thread Txbt2411Thread = new Thread(batch);
			
			Txbt2411Thread.setName("thr_2411");
			
			Txbt2411Thread.start();
			
			
		} catch (SqlMapException se) {
			se.printStackTrace();
		} catch (BeansException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public void setApp(ApplicationContext context) {
		this.context = context;
	}
	
	/*트랜잭션을 실행하기 위한 함수.
	private void mainTransProcess(){
		Txbt2411_01 batch;
		try {
			batch = new Txbt2411_01();
			batch.setContext(appContext);
			batch.setApp(appContext);
			UcContextHolder.setCustomerType("LT_etax");
			batch.startJob();
		} catch (Exception e) {
			e.printStackTrace();
		}			
	}*/
	
	@SuppressWarnings("unused")
	private void mainTransProcess(){

		try {
			
			this.context = appContext;
			
			UcContextHolder.setCustomerType(orgcd);
						
			this.startJob();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}			
		
	}	
	
	/*
	 * 업무 프로세스 시작합니다.
	 * 초기시작시 한번만 동작합니다.
	 * */
	public void initProcess() throws IOException, Exception {
		// TODO Auto-generated method stub
		log.debug("=====================================================================");
		log.debug("== " + this.getClass().getName() +  " initProcess() ==");
		log.debug("=====================================================================");
		
		
		try {
						
			int govCnt = CbUtil.getResourceInt("ApplicationResource", "cyber.so.count");
			
			Thread[] threadList = new Thread[govCnt];
			
			for (int i = 0; i < govCnt; i++) {
				
				try {
					
					govid = CbUtil.getResource("ApplicationResource", "cyber.so.gov" + i);
					orgcd = "NI_" + CbUtil.getResource("ApplicationResource", "cyber." + govid + ".org_cd");
					
					//if(!orgcd.equals("NI_326")) {
						// 현재 이구청은 안되거나 연결이 안됨
					//	continue;
					//}
					
					log.info("===============================           " + orgcd);
						
					threadList[i] = new Thread(newProcess(govid, orgcd), "sub_thr2411_" + orgcd.substring(3));		
					
					threadList[i].start();
						
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}			

			}
			
			
		} catch (SqlMapException se) {
			se.printStackTrace();
		} catch (BeansException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	/*구청별 DB연결정보를 셋팅하기 위함...*/
	@Override
	public void setDatasrc(String datasrc) {
		// TODO Auto-generated method stub
		this.dataSource = datasrc;
	}
	
	@Override
	public void runProcess() throws Exception {
		// TODO Auto-generated method stub

		// 구청을 연계하여 시작해야 한다...
		
	}
	

	/*[모든 업무를 여기에 구성한다...]
	 * 1. 부과자료 연계 처리
	 * 2. 부가가치세 업무처리
	 * 3. 수납자료삭제처리 업무(FLAG)
	 * 
	 * (참고사항)
	 *  try ~ catch 문을 사용하는 경우 TRANSACTION 처리시 반드시 throw 처리를 해야 함... 
	 * */
	@Override
	public void transactionJob() {
		// TODO Auto-generated method stub
		
		if(log.isDebugEnabled()){
			
			log.debug("====================트 랜 잭 션 시 작======================");
			log.debug(" govid["+govid+"], orgcd["+orgcd+"]");
			log.debug("===========================================================");
		}
		
	}
	
	/**
	 * @param govId
	 * @return
	 * @throws Exception
	 * 메인 로직...을 호출한다.
	 */
	private Txbt2411_process newProcess(String govId, String orgcd) throws Exception {
		
		Txbt2411_process process = new Txbt2411_process();

		process.setContext(appContext);
		process.setGovId(govId);
		process.setDataSource(orgcd);
		
		process.initProcess();		
		
		return process;
	}

}
