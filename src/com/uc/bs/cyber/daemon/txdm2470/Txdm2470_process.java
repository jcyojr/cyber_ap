/**
 *  주시스템명 : 부산시청 사이버 지방세청
 *  업  무  명 : 연계
 *  기  능  명 : 주정차위반과태료 고지자료연계
 *  클래스  ID : Txdm2470_process : 실 처리로직을 작성하는 클래스
 *  변경  이력 :
 * ------------------------------------------------------------------------
 *  작성자      	소속  		    일자            Tag           내용
 * ------------------------------------------------------------------------
 *  송동욱       유채널(주)      2011.06.03         %01%         최초작성
 */
package com.uc.bs.cyber.daemon.txdm2470;

import java.util.ArrayList;

import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.dao.DuplicateKeyException;

import com.uc.bs.cyber.CbUtil;
import com.uc.bs.cyber.daemon.Codm_BaseProcess;
import com.uc.bs.cyber.daemon.Codm_interface;
import com.uc.core.MapForm;
import com.uc.core.spring.service.UcContextHolder;

/**
 * @author Administrator
 *
 */
public class Txdm2470_process extends Codm_BaseProcess implements Codm_interface {

	private MapForm dataMap            = null;
	private int loop_cnt = 0, insert_cnt = 0, update_cnt = 0, del_cnt = 0;
	
	MapForm gblParkingLevyList = new MapForm();
	
	/**
	 * 
	 */
	public Txdm2470_process() {
		// TODO Auto-generated constructor stub
		super();
		
		log = LogFactory.getLog(this.getClass());
		
		/**
		 * 5 분마다 돈다
		 */
		loopTerm = 60 * 5;
	}
	
	/*Context 주입용*/
	public void setApp(ApplicationContext context) {
		this.context = context;
	}
	
	/*트랜잭션을 실행하기 위한 함수.*/
	private void mainTransProcess(){
		
		try {

			setContext(appContext);
			setApp(appContext);

			UcContextHolder.setCustomerType(this.dataSource);

			MapForm ststMap = new MapForm();

			log.info("=======[" + c_slf_org_nm + "] 주정차위반과태료 부과자료 연계 시작=======");
			
			do {
				
				ststMap.setMap("JOB_ID"    , "TXDM2470");
				ststMap.setMap("PARM_NM"   , this.c_slf_org + "||" + this.govId);
				ststMap.setMap("LOG_DESC"  , "== START ==");
				ststMap.setMap("RESULT_CD" , "S");
				
				/*주정차위반과태료 고지자료연계 시작... */
				cyberService.queryForUpdate("TXDM2420.INSERT_LOG_JOBSTATE", ststMap);
				
				int procCnt = txdm2470_JobProcess();
				
				/*완료자료 업데이트 */
				ststMap.setMap("LOG_DESC"  , "== END  주정차위반과태료 고지자료연계 처리건수::" + loop_cnt + ", 부과처리::" + insert_cnt + ", 업데이트::" + update_cnt + ", 삭제처리::" + del_cnt);
				ststMap.setMap("RESULT_CD" , "E");
				
				/*부과전송자료 통계완료 */
				cyberService.queryForUpdate("TXDM2420.INSERT_LOG_JOBSTATE", ststMap);		
				
				if(procCnt == 0) {
					break;	
				}

			} while(true);
		
			log.info("=======[" + c_slf_org_nm + "] 주정차위반과태료 부과자료 연계 끝 =======");
			

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}			
		
	}
	
	/* process starting */
	@Override
	public void runProcess() throws Exception {
		// TODO Auto-generated method stub
		//log.debug("=====================================================================");
		//log.debug("=" + this.getClass().getName()+ " runProcess() ==");
		//log.debug("=====================================================================");
		
		/*트랜잭션 업무 구현*/
		mainTransProcess();
	}

	@Override
	public void setDatasrc(String datasrc) {
		// TODO Auto-generated method stub
		this.dataSource = datasrc;
	}

	@Override
	public void transactionJob() {
		// TODO Auto-generated method stub
		
		nonTransactionJob();
	}
	
	public void nonTransactionJob() {
		
		if(gblParkingLevyList.getMap("BNON_SNTG").equals("I") || gblParkingLevyList.getMap("BNON_SNTG").equals("U") || gblParkingLevyList.getMap("BNON_SNTG").equals("M") ) {
			
			/*==========부과자료 저장...===========*/
			long natn_tax = Long.parseLong(gblParkingLevyList.getMap("NATN_TAX").toString());
			long sido_tax = Long.parseLong(gblParkingLevyList.getMap("SIDO_TAX").toString());
			long sigingu_tax = Long.parseLong(gblParkingLevyList.getMap("SIGUNGU_TAX").toString());
			
			/*초기값 세팅*/
			gblParkingLevyList.setMap("TAX_CNT"    , 0 );  /*부과시는 0 : 수납시는 고려해야 함...(이중수납)*/
			gblParkingLevyList.setMap("DEL_YN"     ,"N"); 
			gblParkingLevyList.setMap("PROC_CLS"   ,"1");  /*가상계좌처리구분 코드 Default */
			gblParkingLevyList.setMap("BU_ADD_YN"  ,"N");  /*부가가치구분 */
			gblParkingLevyList.setMap("CUD_OPT"    ,"1");  /*자료등록구분 */
			gblParkingLevyList.setMap("SGG_TR_TG"  ,"0");  /*구청전송처리구분*/
			
			
			try {
				
				/*금액이 0 도 삭제대상*/
				if((natn_tax + sido_tax + sigingu_tax) == 0) {
					
					/*삭제 FLAG */
					if (cyberService.queryForUpdate("TXDM2470.UPDATE_DEL_PARKING_LEVY", gblParkingLevyList) > 0) {
						del_cnt++;
					}
					
				} else {
					
					/*부과*/
					try {
						
						cyberService.queryForUpdate("TXDM2470.INSERT_PARKING_LEVY", gblParkingLevyList);
						
					}catch (Exception e){
						
						if (e instanceof DuplicateKeyException){
							cyberService.queryForUpdate("TXDM2470.UPDATE_PARKING_LEVY", gblParkingLevyList);	
						} else {
							
							log.info("오류발생데이터 = " + gblParkingLevyList.getMaps());
							e.printStackTrace();							
							throw (RuntimeException) e;
						}
					}
					
					/*부과상세*/
					try {
						
						cyberService.queryForUpdate("TXDM2470.INSERT_PARKING_DETAIL_LEVY", gblParkingLevyList);
						
					}catch (Exception e){
						
						if (e instanceof DuplicateKeyException){
							cyberService.queryForUpdate("TXDM2470.UPDATE_PARKING_DETAIL_LEVY", gblParkingLevyList);
						} else {
							
							log.info("오류발생데이터 = " + gblParkingLevyList.getMaps());
							e.printStackTrace();							
							throw (RuntimeException) e;
						}
					}
					
					insert_cnt++;
				}
				
			}catch (Exception e){
				
				log.info("오류발생데이터 = " + gblParkingLevyList.getMaps());
				e.printStackTrace();						
			}

		} else {
			
			gblParkingLevyList.setMap("TAX_CNT"    , 0 );
			
			/*삭제 FLAG */
			if (cyberService.queryForUpdate("TXDM2470.UPDATE_DEL_PARKING_LEVY", gblParkingLevyList) > 0) {
				del_cnt++;
			}
			
		}
		
		/* 삭제처리여부 업데이트*/
		govService.queryForUpdate("TXDM2470.UPDATE_PARKING_LEVY_RESULT", gblParkingLevyList);

	}
	
	/**
	 * 주정차위반과태료 고지자료연계 프로세스
	 */
	private int txdm2470_JobProcess() {
		
		log.info("=====================================================================");
		log.info("=" + this.getClass().getName()+ " txdm2470_JobProcess() [주정차위반과태료 고지자료연계] Start =");
		log.info("= govid["+this.govId+"], orgcd["+this.c_slf_org+"], orgcd["+this.c_slf_org_nm+"]");
		log.info("=====================================================================");
		
		loop_cnt = 0; 
		insert_cnt = 0;
		update_cnt = 0;
		del_cnt = 0;
		
		long elapseTime1 = 0;
		long elapseTime2 = 0;
		
		/*트랜잭션을 포함하는 실업무는 여기서 구현한다...*/
		/*실업무를 구현합니다.*/
		
		dataMap = new MapForm();
		dataMap.setMap("TRANS_SGCD", this.c_slf_org);
						
		/*주정차위반과태료 부과내역을 가져온다.*/
		ArrayList<MapForm> alParkingLevyList =  govService.queryForList("TXDM2470.SELECT_PARKING_LEVY_LIST", dataMap);
		
		log.info("[C][" + this.c_slf_org_nm + "]부과내역 건수 = " + alParkingLevyList.size());
		
		if (alParkingLevyList.size()  >  0)   {
			
			elapseTime1 = System.currentTimeMillis();
			
			for (int rec_cnt = 0;  rec_cnt < alParkingLevyList.size();  rec_cnt++)   {
				
				gblParkingLevyList =  alParkingLevyList.get(rec_cnt);
				
				if (gblParkingLevyList == null  ||  gblParkingLevyList.isEmpty() )   {
					continue;
				}
				
				loop_cnt++;

				/*========================*/
				/*트랜잭션처리 건별*/
				/*========================*/
				try {
					//this.startJob();
					this.nonTransactionJob();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
			
			elapseTime2 = System.currentTimeMillis();
			
		}
		
		log.info("[" + c_slf_org_nm + "]주정차위반과태료 연계시간 : " + CbUtil.formatTime(elapseTime2 - elapseTime1));
		log.info("주정차위반과태료 고지자료연계 처리건수::" + loop_cnt + ", 부과처리::" + insert_cnt + ", 업데이트::" + update_cnt + ", 삭제처리::" + del_cnt);
		
		return alParkingLevyList.size();
	}
	
}
