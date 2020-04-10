/**
 *  주시스템명 : 부산시 사이버지방세청
 *  업  무  명 : 공통
 *  기  능  명 : DB를 읽어서 전문을 생성하고 멀티로 쏘는 샘플
 *  클래스  ID : Codm0001_01
 *  변경  이력 :
 * ------------------------------------------------------------------------
 *  작성자      	소속  		    일자            Tag           내용
 * ------------------------------------------------------------------------
 *  김대완       유채널(주)      2010.11.30         %01%         최초작성
 *  송동욱       유채널(주)      2011.04.27         %01%         보고배낌
 */

package com.uc.bs.cyber.daemon.sample;

import java.io.IOException;

import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.uc.core.misc.XMLFileReader;
import com.uc.core.misc.XmlUtils;
import com.uc.core.spring.service.UcContextHolder;
import com.uc.bs.cyber.CbUtil;
import com.uc.bs.cyber.daemon.Codm_BaseDaemon;

public class Codm0001 extends Codm_BaseDaemon {
	 
	private String dataSource  = null; 

	public Codm0001() throws IOException, Exception {
		
		super();
		// TODO Auto-generated constructor stub
		
		log = LogFactory.getLog(this.getClass());
		
		loopTerm = 60;	// 1  분마다 Thread 들을 감시해라
	}
	
	
	public String getDataSource() {
		return dataSource;
	}

	public void setDataSource(String dataSource) {
		this.dataSource = dataSource;
	}


	/**
	 * Main Thread
	 * 테스트시 구동시킨다.
	 * @param args
	 */
	public static void main(String[] args) {
		
		Codm0001 Codm0001;
		ApplicationContext context  = null;
		
		try {
			// Log
			Codm0001 = new Codm0001();
			
			CbUtil.setupLog4jConfig(Codm0001, "log4j.tomcat.xml");
			
			try {
				String[] xmls = XMLFileReader.getAllFilePathArray(CbUtil.getResourcePath(Codm0001, "/"), "SERVICE.XML");

				String strSrc = CbUtil.getResourcePath(Codm0001, "/") + "config/sqlmaps.xml";

				String strDest = CbUtil.getResourcePath(Codm0001, "/") + "config/sqlmapConfig.xml";

				XmlUtils.setXmlAttributes(Codm0001, strSrc, strDest, "sqlMapConfig", "sqlMap", "url", xmls);

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
		
			context = new ClassPathXmlApplicationContext("config/*-Spring-db.xml");
			
			Codm0001.setProcess("0001", "테스트_0001", "thr_0001");  /* 업무데몬 등록 */
			
			Codm0001.setContext(context);
			
			Codm0001.setDataSource("LT_etax");  /*자치단체 ID*/

			UcContextHolder.setCustomerType(Codm0001.getDataSource());
			
			Thread bs0001Thread = new Thread(Codm0001);
			
			bs0001Thread.setName("thr_0001");
			
			bs0001Thread.start();
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	/*메인데몬의 INTERUPT 발생시 내것도 INTERUPT 걸어서 아작내자... 
	 *Codm_BaseDaemon.class에서 INTERUPT걸리면 자동으로 걸린다...
	 * */
	@Override
	public void onInterrupt() {
		// TODO Auto-generated method stub
		
		if(threadList == null) return;
		
		for(int i = 0; i<threadList.length; i++) {
			threadList[i].interrupt();
		}
	}

    /*
     * 현재는 10초단위로 thick 합니다.
     * 따라서 여기서는 현 데몬에 의해서 생성된 데몬을 관리하도록 합니다.
     * */
	@Override
	public void mainProcess() {
		// TODO Auto-generated method stub
				
		for(int i = 0; i<threadList.length; i++) {
			

			if(!threadList[i].isAlive()) {	// 다시 살려라
				
				log.debug("=====================================================================");
				log.info("== " + this.getClass().getName()+ " mainProcess() ==");
				log.debug("=====================================================================");
				
				log.debug(" Thread(" + threadList[i].getName() + ") is " + threadList[i].isAlive());
				
				String govId = CbUtil.getResource("ApplicationResource", "cyber.lt.gov" + i);
				
				try {
					threadList[i] = new Thread(newProcess(govId), govId);
				
					threadList[i].start();
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}			
				
			}
			
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
		
		int procCnt  = CbUtil.getResourceInt("ApplicationResource", "cyber.lt.count");
		
		threadList = new Thread[procCnt];
		
		for(int i = 0; i<procCnt; i++) {

			String govId = CbUtil.getResource("ApplicationResource", "cyber.lt.gov" + i);
			
			threadList[i] = new Thread(newProcess(govId), govId);			
			threadList[i].start();
			
		}
		
	}
	
	/*트랜잭션이 필요한 경우 사용한다.
	 *interface 메서드 구현
	 * */
	@Override
	public void transactionJob() {
		// TODO Auto-generated method stub
		// ...Here is nothing ...
	}
	
	/**
	 * @param govId
	 * @return
	 * @throws Exception
	 * 메인 로직...을 호출한다.
	 */
	private Codm0001_process newProcess(String govId) throws Exception {
		
		Codm0001_process process = new  Codm0001_process();
		
		process.setContext(appContext);
		process.setGovId(govId);
		
		process.initProcess();		
		
		return process;
	}


}
