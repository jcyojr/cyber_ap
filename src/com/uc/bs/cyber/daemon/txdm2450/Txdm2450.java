/**
 *  주시스템명 : 부산시 사이버지방세청 
 *  업  무  명 : 연계
 *  기  능  명 : 부비카 수납자료 전송
 *               20 분단위로 동작...
 *               부비카수납자료는  별도의 구청서버에 접속하지 않고 주거지서버로만 연결한다
 *  클래스  ID : Txdm2450
 *  변경  이력 :
 * ------------------------------------------------------------------------
 *  작성자      	소속  		    일자            Tag           내용
 * ------------------------------------------------------------------------
 *  김대완       유채널(주)      2011.08.10         %01%         최초작성
 */
package com.uc.bs.cyber.daemon.txdm2450;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.uc.bs.cyber.CbUtil;
import com.uc.bs.cyber.daemon.Codm_BaseDaemon;
import com.uc.core.MapForm;
import com.uc.core.misc.XMLFileReader;
import com.uc.core.misc.XmlUtils;
import com.uc.core.spring.service.IbatisService;
import com.uc.core.spring.service.UcContextHolder;

/**
 * @author Administrator
 *
 */
public class Txdm2450 extends Codm_BaseDaemon {
	
	/**
	 * 생성자
	 */
	
	protected IbatisService cyberService = null;	 // 기본 DB 작업용 사이버세청 연결 
	
	protected IbatisService govService  = null;	 // 기본 DB 작업용 자치단체 연결
	
	private ArrayList<MapForm> snList = null;
	
	private String tblName  = "";
	
	private int jobFlag = 0;
	
	public Txdm2450() throws IOException, Exception {
		// TODO Auto-generated constructor stub
		super();
		
		log = LogFactory.getLog(this.getClass());

		this.loopTerm = 300;	/* 5분 마다 */
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Txdm2450 Txdm2450;
		ApplicationContext context  = null;
		
		try {
			// Log
			Txdm2450 = new Txdm2450();
			
			CbUtil.setupLog4jConfig(Txdm2450, "log4j.xml");
			
			try {
				String[] xmls = XMLFileReader.getAllFilePathArray(CbUtil.getResourcePath(Txdm2450, "/"), "SERVICE.XML");

				String strSrc = CbUtil.getResourcePath(Txdm2450, "/") + "config/sqlmaps.xml";

				String strDest = CbUtil.getResourcePath(Txdm2450, "/") + "config/sqlmapConfig.xml";

				XmlUtils.setXmlAttributes(Txdm2450, strSrc, strDest, "sqlMapConfig", "sqlMap", "url", xmls);

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
		
			context = new ClassPathXmlApplicationContext("config/Spring-db.xml");
			
			Txdm2450.setProcess("2450", "부비카 수납자료 자료연계", "thr_2450");  /* 업무데몬 등록 */
			
			Txdm2450.setContext(context);
			
			
			Thread tx2450Thread = new Thread(Txdm2450);
			
			tx2450Thread.setName("thr_2450");
			
			tx2450Thread.start();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/*
	 * 업무 프로세스 시작합니다.
	 * 초기시작시 한번만 동작합니다.
	 * */
	@Override
	public void initProcess() throws Exception {
		// TODO Auto-generated method stub
		log.debug("=====================================================================");
		log.debug("== " + this.getClass().getName() +  " initProcess() ==");
		log.debug("=====================================================================");
		
		this.context = this.appContext;
		
		cyberService = (IbatisService) this.getService("ibatisService_cyber"); 
		/**
		 * 주거지 DB연결정보를 가져오도록
		 */
		UcContextHolder.setCustomerType("JUGEOJI");

		govService   = (IbatisService) this.getService("ibatisService");
		
	}
    /*
     * 현재는 10초단위로 thick 합니다.
     * 따라서 여기서는 현 데몬에 의해서 생성된 데몬을 관리하도록 합니다.
     * */

	public void mainProcess() throws Exception {
		// TODO Auto-generated method stub

		log.info("======== 부비카 전송 데몬 START ========");
		
		while (true) {			
			/**
			 * 지방세 수납내역 처리
			 */

			int flag = 0;
			
			for(jobFlag = 1; jobFlag <= 3; jobFlag++) {

				switch (jobFlag) {
					case 1:  tblName = "TX1202"; break;
					case 2:  tblName = "TX2201"; break;
					case 3:  tblName = "ET2101"; break;
					default: break;
				}
				
				snList  = cyberService.queryForList("TXDM2450." + tblName + "_SELECT", null);
			    if (snList.size() > 0) {
			    	flag = 1;
			    	
			    	this.startJob();
			    }
				
			}
					    
			if(flag == 0) break;
		}
		
		log.info("======== 부비카 전송 데몬 END   ========");
	}

	@Override
	public void onInterrupt() throws Exception {
		// TODO Auto-generated method stub
		if(threadList == null) return;
		
		for(int i = 0; i<threadList.length; i++) {
			threadList[i].interrupt();
		}
	}

	/**
	 * 단일트랜젝션 처리
	 */
	public void transactionJob() {
		// TODO Auto-generated method stub
		
		log.info("TRANSACTION JOB FLAG==" + jobFlag + ", COUNT==" + snList.size()) ;

//		govService.queryForMultiInsert("TXDM2450.OFFRECIP_INSERT", snList);

		MapForm snRows = new MapForm();
		for(int i=0;i<snList.size();i++){
			snRows =  snList.get(i);
			if(govService.getOneFieldInteger("TXDM2450.OFFRECIP_SELECT_CNT", snRows)==0){
				govService.queryForUpdate("TXDM2450.OFFRECIP_INSERT", snRows);
			}
		}

		cyberService.queryForMultiInsert("TXDM2450." + tblName + "_UPDATE", snList);
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
			
			log.info("buvicar--run--sysMap"+sysMap);
			
			if(this.getService().queryForUpdate("CODMBASE.CODMSYSTUpdate", sysMap) < 1) {
				this.getService().queryForInsert("CODMBASE.CODMSYSTInsert", sysMap);
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
				
				Thread.sleep(this.loopTerm * 1000);
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
	
}
