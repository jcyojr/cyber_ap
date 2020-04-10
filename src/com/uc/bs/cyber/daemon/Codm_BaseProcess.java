/**
 *  주시스템명 : 사이버지방세청 고도화
 *  업  무  명 : 공통
 *  기  능  명 : 사이버지방세청 연계 데몬 공통(업무처리용)
 *  클래스  ID : Codm_BaseProcess
 *  변경  이력 :
 * ------------------------------------------------------------------------
 *  작성자      	소속  		    일자            Tag           내용
 * ------------------------------------------------------------------------
 *  Administrator   u c         2011.04.24         %01%         최초작성
 */
package com.uc.bs.cyber.daemon;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;

import com.uc.core.spring.service.IbatisBaseService;
import com.uc.core.spring.service.TransactionJob;
import com.uc.bs.cyber.CbUtil;

/**
 * @author Administrator
 *
 */
public abstract class Codm_BaseProcess extends TransactionJob implements Codm_interface, Runnable {
	
	protected Log log = LogFactory.getLog(this.getClass());
	
	protected ApplicationContext appContext = null;  // Spring Context
	
	protected int loopTerm = 60;				     // Thread 반복시간 기본 1분
	
	protected IbatisBaseService cyberService = null;	 // 기본 DB 작업용 사이버세청 연결
	
	protected IbatisBaseService govService  = null;	     // 기본 DB 작업용 자치단체 연결
	
	protected String dataSource = null;	    // 자치단체코드 
	
	protected String c_slf_org = null;	    // 자치단체코드 

	protected String c_slf_org_nm  = null;	// 자치단체명 
	
	protected String govId  = null;		    // 자치단체 연결ID
	
	protected int jobId  = 0;		        // 프로세스 순서
	
	/**
	 * 생성자
	 */
	public Codm_BaseProcess() {
		// TODO Auto-generated constructor stub
		super();
		log = LogFactory.getLog(this.getClass());
	}
	
	
	/*구청을 DB연결을 구별하기 위함...*/
	public void setDataSource(String dataSource) {
		this.dataSource = dataSource;
	}
	
	
	/**
	 * @return the govId
	 */
	public String getGovId() {
		return govId;
	}

	/**
	 * @param govId the govId to set
	 */
	public void setGovId(String govId) {
		this.govId = govId;
	}
	
	public void setUpId(int jobId) {
		this.jobId = jobId;
	}
	
	/*interface 구현*/
	@Override 
	public ApplicationContext getContext() {
		// TODO Auto-generated method stub
		return appContext ;
	}
	
	/*interface 매서드 구현*/
	@Override
	public void setContext(ApplicationContext context) {
		// TODO Auto-generated method stub
		appContext = context;
	}
	
	/*interface 구현*/
	@Override
	public Object getService(String beanName) {
		// TODO Auto-generated method stub
		return appContext.getBean(beanName);
	}
	/*interface 구현*/
	@Override
	public void initProcess() throws Exception {
		// TODO Auto-generated method stub
		
		log.debug("=====================================================================");
		log.debug("== " + this.getClass().getName() +  " initProcess() ==");
		log.debug("=====================================================================");
		
        c_slf_org = CbUtil.getResource("ApplicationResource", "cyber."  + govId + ".org_cd");	  // 자치단체코드
		
		c_slf_org_nm  = CbUtil.getResource("ApplicationResource", "cyber."  + govId + ".org_nm"); // 자치단체명
				
		cyberService = (IbatisBaseService) getService("baseService_cyber");	// IBATIS Service 사용 (기본 업무에서 사용)
		
		govService = (IbatisBaseService) getService("baseService");	        // 지자체 연결용 IbatisService Service
		
	}
	

	/*추상메서드 정의 : 구청별 연결정보를 위해서*/
	public abstract void setDatasrc(String datasrc);
	
	/* 추상메서드 정의*/
	public abstract void runProcess() throws Exception;

	/* 스레드 구현*/
	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			while(!Thread.currentThread().isInterrupted()) {
				
				runProcess();
			
				Thread.sleep(loopTerm * 1000);
				
			}		
		} catch (InterruptedException e1) {
			// e1.printStackTrace();
			
			log.info("================================================");
			log.info("== " + Thread.currentThread().getName() + " PROCESS Interrupte end!!");
			log.info("================================================");			
			
		} catch (Exception e) {
			
			e.printStackTrace();
			
		}
		
		log.info("================================================");
		log.info("== " + Thread.currentThread().getName() + " PROCESS Terminated!!");
		log.info("================================================");
	}
	

}
