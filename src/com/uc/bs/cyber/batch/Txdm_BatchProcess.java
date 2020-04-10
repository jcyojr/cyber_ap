/**
 *  주시스템명 : 사이버지방세청 고도화
 *  업  무  명 : 공통
 *  기  능  명 : 사이버지방세청 연계 배치 공통(업무처리용)
 *  클래스  ID : Txdm_BatchProcess
 *  변경  이력 :
 * ------------------------------------------------------------------------
 *  작성자      	소속  		    일자            Tag           내용
 * ------------------------------------------------------------------------
 *  송동욱         유채널(주)    2011.05.24         %01%         최초작성
 */
package com.uc.bs.cyber.batch;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;

import com.uc.bs.cyber.CbUtil;
import com.uc.bs.cyber.daemon.Codm_interface;
import com.uc.core.MapForm;
import com.uc.core.spring.service.IbatisBaseService;
import com.uc.core.spring.service.TransactionJob;

/**
 * @author Administrator
 *
 */
public abstract class Txdm_BatchProcess extends TransactionJob implements Codm_interface, Runnable {
	
	protected Log log = LogFactory.getLog(this.getClass());
	
	protected ApplicationContext appContext = null;  // Spring Context
	
	protected IbatisBaseService cyberService = null;	 // 기본 DB 작업용 사이버세청 연결
	
	protected IbatisBaseService govService  = null;	     // 기본 DB 작업용 자치단체 연결
	
	protected String dataSource = null;	    // 자치단체코드 
	
	protected String c_slf_org = null;	    // 자치단체코드 

	protected String c_slf_org_nm  = null;	// 자치단체명 
	
	protected String govId  = null;		    // 자치단체 연결ID
	
	protected MapForm sysMap = new MapForm();
	
	protected String scg_code    = "000";
	protected String procName    = "";
	protected String procId      = "";
	protected String threadName  = "";
	
	/**
	 * 생성자
	 */
	public Txdm_BatchProcess() {
		// TODO Auto-generated constructor stub
		super();
		log = LogFactory.getLog(this.getClass());
	}

	/**
	 * @param 구청코드 the scg_code to set
	 */
	public void setScg_code(String scg_code) {
		this.scg_code = scg_code;
	}
	
	public void setProcess(String id, String name, String thr_name) {
		procId = id; procName  = name; threadName = thr_name;
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
		this.appContext = context;
	}
	
	/*interface 구현*/
	@Override
	public Object getService(String beanName) {
		// TODO Auto-generated method stub
		return appContext.getBean(beanName);
	}
	/*interface 구현*/
	@Override
	public abstract void initProcess() throws Exception;
	
	/*추상메서드 정의 : 구청별 연결정보를 위해서*/
	public abstract void setDatasrc(String datasrc);
	
	/* 추상메서드 정의*/
	public abstract void runProcess() throws Exception;
	
	public IbatisBaseService getService(){
		return (IbatisBaseService) appContext.getBean("baseService_cyber");
	}
	
	/* 스레드 구현*/
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
			sysMap.setMap("MANAGE_CD"    , "2");                                  /*관리구분 1 : 데몬 2: 배치*/
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
			
			initProcess();
			
			runProcess();
			
			Thread.sleep(1000);
			
			sysMap.setMap("END_DT"        , CbUtil.getCurrent("yyyyMMddHHmmss"));  /*종료일시*/
			sysMap.setMap("LAST_DTM"      , CbUtil.getCurrent("yyyyMMddHHmmss"));
			sysMap.setMap("LAST_TIMEMIL"  , System.currentTimeMillis());
			
			/*서버 데몬상태를 업데이트 한다...*/
			this.getService().queryForUpdate("CODMBASE.CODMSYSTUpdate", sysMap);
				
		} catch (Exception e) {
			
			e.printStackTrace();
			
		}
		
		log.info("================================================");
		log.info("== " + Thread.currentThread().getName() + " PROCESS Terminated!!");
		log.info("================================================");
	}

}
