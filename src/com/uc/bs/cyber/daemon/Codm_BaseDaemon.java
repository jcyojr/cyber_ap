/**
 *  주시스템명 : 사이버지방세청 고도화
 *  업  무  명 : 공통
 *  기  능  명 : 사이버지방세청 연계 데몬 공통
 *  클래스  ID : Codm_BaseDaemon
 *  변경  이력 :
 * ------------------------------------------------------------------------
 *  작성자      	소속  		    일자            Tag           내용
 * ------------------------------------------------------------------------
 *  Administrator   u c         2011.04.24         %01%         최초작성
 */
package com.uc.bs.cyber.daemon;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;

import com.uc.core.MapForm;
import com.uc.core.spring.service.IbatisBaseService;
import com.uc.core.spring.service.TransactionJob;

import com.uc.bs.cyber.CbUtil;

/**
 * @author Administrator
 *
 */
public abstract class Codm_BaseDaemon extends TransactionJob implements Codm_interface, Runnable{
	
	protected Log log = LogFactory.getLog(this.getClass());
	
	protected int loopTerm = 60;
	
	protected MapForm sysMap = new MapForm();
	
	protected String scg_code    = "000";
	protected String procName    = "";
	protected String procId      = "";
	protected String threadName  = "";
	
	/**
	 * 생성자
	 * @throws IOException
	 * @throws Exception
	 */
	public Codm_BaseDaemon() throws IOException, Exception {
		super();
		// TODO Auto-generated constructor stub
		
	}
	
	protected ApplicationContext  appContext = null;
	
	protected Thread[] threadList    = null;
	

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	/**
	 * @param 구청코드 the scg_code to set
	 */
	public void setScg_code(String scg_code) {
		this.scg_code = scg_code;
	}

	/**
	 * 
	 * @param id
	 * @param name
	 */
	public void setProcess(String id, String name, String thr_name) {
		procId = id; procName  = name; threadName = thr_name;
	}

	/**
	 * Thread 구현
	 */
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
		try {
						
			sysMap.setMap("SGG_COD"      , scg_code);                             /*구청코드 : 부산시(626), 사이버세청(000)*/
			sysMap.setMap("PROCESS_ID"   , procId);                               /*프로세스 ID*/
			sysMap.setMap("PROCESS_NM"   , procName);                             /*프로세스 명*/
			sysMap.setMap("THREAD_NM"    , threadName);                           /*쓰레드 명*/
			sysMap.setMap("BIGO"         , CbUtil.getServerIp());                 /*비고 : 실행서버IP를 셋팅한다. */
			sysMap.setMap("MANE_STAT"    , "1");                                  /*기동상태*/
			sysMap.setMap("MANAGE_CD"    , "1");                                  /*관리구분 1 : 데몬 */
			sysMap.setMap("STT_DT"       , CbUtil.getCurrent("yyyyMMddHHmmss"));  /*시작일시 */
			sysMap.setMap("END_DT"       , "");                                   /*종료일시*/
			sysMap.setMap("REG_DTM"      , CbUtil.getCurrent("yyyyMMddHHmmss"));  /*등록일시 : 처음한번만...*/
			sysMap.setMap("LAST_DTM"     , CbUtil.getCurrent("yyyyMMddHHmmss"));  /*최종수정일시*/
			sysMap.setMap("LAST_TIMEMIL" , System.currentTimeMillis());
			
			/**
			 * 데몬상태정보 등록
			 * TEST의 경우만 막음
			 */
			try{
				if(this.getService().queryForUpdate("CODMBASE.CODMSYSTUpdate", sysMap) < 1) {
					this.getService().queryForInsert("CODMBASE.CODMSYSTInsert", sysMap);
				}
			}catch (Exception e) {
				e.printStackTrace();
			}
	
			/**
			 * 초기 쓰레드 기동
			 */
			this.initProcess();
			
			/**
			 * 쓰레드가 정상인 경우
			 */
			while(!Thread.currentThread().isInterrupted()) {
				
				/*메인함수처리 현재 10초단위로 Tick 한다.*/
				mainProcess();
				
				/*10분 단위로 상태 업데이트*/
                if(System.currentTimeMillis() - (Long)sysMap.getMap("LAST_TIMEMIL") >= 1000*60*10) {
					
					sysMap.setMap("LAST_DTM"      , CbUtil.getCurrent("yyyyMMddHHmmss"));
					sysMap.setMap("MANAGE_CD"     , "1");
					sysMap.setMap("LAST_TIMEMIL"  , System.currentTimeMillis());
					
					/*서버 데몬상태를 업데이트 한다...*/
					this.getService().queryForUpdate("CODMBASE.CODMSYSTUpdate", sysMap);
				}
				
				Thread.sleep(1000 * 10);
			}
			
			/*THREAD가 끝나면 이 데몬은 모두 삭제되도록 강제 Interrupting 한다.*/
			log.info("[" + this.procId + "] onInterrupt() called ...");
			
			sysMap.setMap("END_DT", CbUtil.getCurrent("yyyyMMddHHmmss"));
			/*서버 종료 데몬상태를 업데이트 한다...*/
			this.getService().queryForUpdate("CODMBASE.CODMSYSTUpdate", sysMap);
			
			this.onInterrupt();
			
		} catch (InterruptedException ie) {
			
			ie.printStackTrace();
			log.info("Thread Interrupted : [" + this.procId + "] 쓰레드 종료 중...");
			
			try {
				this.onInterrupt();
			} catch (Exception e) {
				e.printStackTrace();
				
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
    /*Codm_interface class 의 인터페이스 메서드 구현*/
	@Override
	public ApplicationContext getContext() {
		// TODO Auto-generated method stub
		return appContext;
	}
	/*Codm_interface class 의 인터페이스 메서드 구현*/
	@Override
	public Object getService(String beanName) {
		// TODO Auto-generated method stub
		return appContext.getBean(beanName);
	}

	/*Codm_interface class 의 인터페이스 메서드 구현*/
	@Override
	public void setContext(ApplicationContext context) {
		// TODO Auto-generated method stub
		this.appContext = context;
	}	
	
	public IbatisBaseService getService(){
		return (IbatisBaseService) appContext.getBean("baseService_cyber");
	}
	
	
	/**
	 * 추상메서드 정의
	 * @throws Exception
	 */
	public abstract void mainProcess() throws  Exception;
	
	/**
	 * 추상메서드 정의
	 * @throws Exception
	 */
	public abstract void onInterrupt() throws Exception;
	
	
	/*Codm_interface class 의 인터페이스 메서드 구현*/
	public abstract void initProcess() throws Exception;



}
