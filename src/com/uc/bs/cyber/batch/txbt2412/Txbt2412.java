/**
 *  주시스템명 : 부산시 사이버지방세청
 *  업  무  명 : 배치
 *  기  능  명 : 표준세외수입 체납자료연계(구청)
 *               배치로 구동되며 저녁 9시에 한번만 
 *  클래스  ID : Txbt2412
 *  변경  이력 :
 * ------------------------------------------------------------------------
 *  작성자      	소속  		    일자            Tag           내용
 * ------------------------------------------------------------------------
 *  송동욱       유채널(주)      2011.05.20         %01%         최초작성
 */
package com.uc.bs.cyber.batch.txbt2412;

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
public class Txbt2412 extends Txdm_BatchProcess {

	private static String govid = "";
	private static String orgcd = "";
	
	private int INT_TRANSACTION_ST  = 0; 
	
	/**
	 * 
	 */
	public Txbt2412() {
		// TODO Auto-generated constructor stub
		super();
		
		log = LogFactory.getLog(this.getClass());
		
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		System.out.println("=================================================");
		System.out.println("사이버지방세청 표준세외수입 체납자료연계 Started");
		System.out.println("=================================================");	
		
		Txbt2412 batch;
		
		batch = new Txbt2412();
		
		ApplicationContext context  = null;
		
		System.out.println("== OS Name = " + System.getProperty("os.name"));
		System.out.println("== OS Version = " + System.getProperty("os.version"));
		System.out.println("== FILE Separator = " + System.getProperty("file.separator"));
		
		try {
			//Log
			
			CbUtil.setupLog4jConfig(batch, "log4j.txbt2412.xml");
			
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
			
			batch.setProcess("2412", "표준세외수입 체납자료연계(구청별)", "thr_2412");  /* 업무데몬 등록 */
			
			batch.setContext(context);
			
			Thread Txbt2412Thread = new Thread(batch);
			
			Txbt2412Thread.setName("thr_2412");
			
			Txbt2412Thread.start();
			
			
		} catch (SqlMapException se) {
			se.printStackTrace();
		} catch (BeansException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}		

	}
	@Override
	public void initProcess() throws Exception {
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
					
					/*
					if(orgcd.equals("NI_330") || orgcd.equals("NI_325") || orgcd.equals("NI_332") ||
							orgcd.equals("NI_335") || orgcd.equals("NI_337") || orgcd.equals("NI_339") ) {
						// 현재 이구청은 안되거나 연결이 안됨
						continue;
					}
					*/	
					
					threadList[i] = new Thread(newProcess(govid, orgcd), "sub_thr2412_" + orgcd.substring(3));		
					
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
	
	@Override
	public void runProcess() throws Exception {
		// TODO Auto-generated method stub
		// Thread 처리 안함...
		
	}


	@Override
	public void setDatasrc(String datasrc) {
		// TODO Auto-generated method stub
		this.dataSource = datasrc;
	}

	
	public void setApp(ApplicationContext context) {
		this.context = context;
	}
	
	/*트랜잭션을 실행하기 위한 함수.*/
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
	
	/*[모든 업무를 여기에 구성한다...]
	 * 1. 체납부과자료 연계 처리
	 * 2. 체납부가가치세 업무처리
	 * 3. 수납자료삭제처리 업무(FLAG)
	 * 
	 * (주의)  과도한 건의 트랜잭션은 삼가한다.
	 * 
	 * */
	@Override
	public void transactionJob() {
		// TODO Auto-generated method stub
		
		if(log.isDebugEnabled()){
			
			log.debug("====================트 랜 잭 션 시 작======================");
			log.debug(" govid["+govid+"], orgcd["+orgcd+"]");
			log.debug("===========================================================");
		}
		
		switch (INT_TRANSACTION_ST){
		
		case 1:
			break;
		case 2:
			break;
		case 3:
			break;
			default:
				
		}

	}
	
	/**
	 * @param govId
	 * @return
	 * @throws Exception
	 * 메인 로직...을 호출한다.
	 */
	private Txbt2412_process newProcess(String govId, String orgcd) throws Exception {
		
		Txbt2412_process process = new Txbt2412_process();
		
		process.setContext(appContext);
		process.setGovId(govId);
		process.setDataSource(orgcd);
		
		process.initProcess();		
		
		return process;
	}


	
	
	
}
