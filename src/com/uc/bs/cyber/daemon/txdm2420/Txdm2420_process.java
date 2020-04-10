/**
 *  주시스템명 : 부산시청 사이버 지방세청
 *  업  무  명 : 연계
 *  기  능  명 : 교통유발부담금 부과자료 연계
 *  클래스  ID : Txdm2420_process : 실 처리로직을 작성하는 클래스
 *  변경  이력 :
 * ------------------------------------------------------------------------
 *  작성자      	소속  		    일자            Tag           내용
 * ------------------------------------------------------------------------
 *  송동욱       유채널(주)      2011.05.11         %01%         최초작성
 */
package com.uc.bs.cyber.daemon.txdm2420;

import java.util.ArrayList;

import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.dao.DuplicateKeyException;

import com.uc.bs.cyber.daemon.Codm_BaseProcess;
import com.uc.bs.cyber.daemon.Codm_interface;
import com.uc.core.MapForm;
import com.uc.core.spring.service.UcContextHolder;

/**
 * @author Administrator
 * 
 * 참고) 20111006 송
 * 
 *  교통유발 부담금 연계 처리 절차
 *  1) ET3101_TB 로 부터 마지막 업데이트 한 날짜를 구해온다.
 *  2) 구해온 날짜를 기준으로 연계테이블에서 데이터를 수집하고
 *  3) 사이버세청 테이블에 입력 및 업데이트 한다.
 *  4) txdm2421 AP에서 가상계좌를 전송하면
 *  5) 세올사업단에서 해당 건을 업데이트 하고 원장의 날짜가 변경된다.
 *  6) 날짜가 변경되었기 때문에 다시 연계한 건에 대해서 연계가 시작된다.
 *  7) 연계된 데이터는 이미 사이버세청에 있고 날짜 및 금액 변동이 없기때문에
 *  8) 업데이트만 되고 가상계좌 채번 및 전송여부는 그대로 둔다.
 *  9) 따라서 동일한 작업을 2번하게 된다...쓰벌...
 */
public class Txdm2420_process extends Codm_BaseProcess implements Codm_interface {

	private MapForm dataMap            = null;
	private int insert_cnt = 0, update_cnt = 0, delete_cnt = 0;
	
	/**
	 * 
	 */
	public Txdm2420_process() {
		// TODO Auto-generated constructor stub
		super();
		
		log = LogFactory.getLog(this.getClass());
		
		/**
		 * 10 분마다 돈다
		 */
		loopTerm = 60 * 15;
	}
	/*Context 주입용*/
	public void setApp(ApplicationContext context) {
		this.context = context;
	}
	
	/*트랜잭션을 실행하기 위한 함수.*/
	@SuppressWarnings("unchecked")
	private void mainTransProcess(){
		
		try {
				
			setContext(appContext);
			setApp(appContext);

			UcContextHolder.setCustomerType(this.dataSource);
			
			dataMap = new MapForm();  /*초기화*/
			dataMap.setMap("PAGE_PER_CNT" , 1000);            /*페이지당 갯수*/
			dataMap.setMap("SGG_COD"      , this.c_slf_org);  /*구청코드 맵핑*/
			
			do {
				
				/*업데이트 시간 설정한다.*/
				dataMap.putAll(cyberService.queryForMap("TXDM2420.SELECT_C_LAST_SGG_WORKTIME", dataMap));
				
				int page = govService.getOneFieldInteger("TXDM2420.SELECT_C_TRAFFIC_LEVY_PER_CNT", dataMap);
				
				log.info("[교통유발부담금(" + c_slf_org_nm + ")] PAGE_PER_CNT(" + dataMap.getMap("PAGE_PER_CNT") + ") 당 Page갯수 =" + page);
				
				if(page == 0) break;
				
				MapForm ststMap = new MapForm();
				
				ststMap.setMap("JOB_ID"    , "TXDM2420");
				ststMap.setMap("PARM_NM"   , this.c_slf_org + "||" + this.govId);
				ststMap.setMap("LOG_DESC"  , "== START ==");
				ststMap.setMap("RESULT_CD" , "S");
				
				/*.1 부과전송자료 통계시작 */
				cyberService.queryForUpdate("TXDM2420.INSERT_LOG_JOBSTATE", ststMap);
				
				for(int i = 1 ; i <= page ; i ++) {

					dataMap.setMap("PAGE",  i);    /*처리페이지*/
					this.startJob();               /*멀티 트랜잭션 호출*/
				}
				
				ststMap.setMap("JOB_ID"    , "TXDM2420");
				ststMap.setMap("PARM_NM"   , this.c_slf_org + "||" + this.govId);
				ststMap.setMap("LOG_DESC"  , "== END  등록건수::" + insert_cnt +", 수정건수::" + update_cnt + ", 삭제건수::" + delete_cnt);
				ststMap.setMap("RESULT_CD" , "E");
				
				/*.부과전송자료 통계시작 */
				cyberService.queryForUpdate("TXDM2420.INSERT_LOG_JOBSTATE", ststMap);
				
				if(govService.getOneFieldInteger("TXDM2420.SELECT_C_TRAFFIC_LEVY_PER_CNT", dataMap) == 0) {
					break;
				}
				
			}while(true);
						
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

	/*현 사용할 일이 없넹...*/
	@Override
	public void setDatasrc(String datasrc) {
		// TODO Auto-generated method stub
		this.dataSource = datasrc;
	}

	/*트랜잭션 구성*/
	@Override
	public void transactionJob() {
		// TODO Auto-generated method stub
		
		log.info("=====================================================================");
		log.info("=" + this.getClass().getName()+ " transactionJob() Start =");
		log.info("= govid["+this.govId+"], orgcd["+this.c_slf_org+"] ordnm[" + c_slf_org_nm + "]");
		log.info("=====================================================================");
		
		/*트랜잭션을 포함하는 실업무는 여기서 구현한다...*/
		/*실업무를 구현합니다.*/
		insert_cnt = 0;
		update_cnt = 0;
		delete_cnt = 0;
		
		/*work time 시간갱신 (누락분을 방지하기 위함) */
		cyberService.queryForUpdate("TXDM2420.UPDATE_C_LAST_SGG_WORKTIME", dataMap);

		/* 교통유발 부과삭제하기, TX2112_TB ->> SNTG = 0 인것만 지운다 
		 *                          납부여부 0:미수납, 1:수납처리중, 2:수납
		 * */

		/*교통유발부담금을 삭제하기 위해 연계DB로 부터 납세번호를 취합한다*/
		ArrayList<MapForm> alTaxNoList =  govService.queryForList("TXDM2420.SELECT_C_TRAFFIC_TAX_NO_LIST", dataMap);

		if (alTaxNoList.size()  >  0)   {
			
			for (int rec_cnt = 0;  rec_cnt < alTaxNoList.size();  rec_cnt++)   {
								
				MapForm mfTaxNoList =  alTaxNoList.get(rec_cnt);
				
				if (mfTaxNoList == null  ||  mfTaxNoList.isEmpty() )   {
					continue;
				}
				
				/*==========삭제 루틴===========*/
				/*flag 처리*/
				if (cyberService.queryForDelete("TXDM2420.UPDATE_DEL_TRAFFIC_INFO_DETAIL", mfTaxNoList) > 0) {
					delete_cnt++;
				}
			}
			
		}
		
		/*
		 * 3. 교통유발부담금 연계자료 입력
		 * */
		
		/*교통유발부담금 부과내역 가져오기*/
		ArrayList<MapForm> alTrafficLevyList =  govService.queryForList("TXDM2420.SELECT_C_TRAFFIC_LEVY_LIST", dataMap);
		
		int rec_cnt = 0;
		
		if (alTrafficLevyList.size()  >  0)   {
			
			for (rec_cnt = 0;  rec_cnt < alTrafficLevyList.size();  rec_cnt++)   {
								
				MapForm mfTrafficLevyList =  alTrafficLevyList.get(rec_cnt);
				
				if (mfTrafficLevyList == null  ||  mfTrafficLevyList.isEmpty() )   {
					continue;
				}
				
				/*기본 Default 값 설정 */
				mfTrafficLevyList.setMap("BU_ADD_YN"   , "N" );       /*부가가치세구분 'N'           */
				mfTrafficLevyList.setMap("TAX_CNT"     ,  0  );       /*부과순번 0                   */
				mfTrafficLevyList.setMap("PROC_CLS"    , "1" );       /*가상계좌채번구번 default '1' */
				mfTrafficLevyList.setMap("DEL_YN"      , "N" );       /*삭제여부         default 'N' */
				mfTrafficLevyList.setMap("CUD_OPT"     , "1" );       /*자료등록구분 체크바람. */
				mfTrafficLevyList.setMap("SGG_TR_TG"   , "0" );       /*구청전송처리구분*/
				
				/*그외 필드 맵핑*/
				mfTrafficLevyList.setMap("NATN_TAX"    , 0 );       /*국세           */
				mfTrafficLevyList.setMap("NATN_RATE"   , 0 );       /*국세요율       */
				mfTrafficLevyList.setMap("SIDO_TAX"    , 0 );       /*시도세         */
				mfTrafficLevyList.setMap("SIDO_RATE"   , 0 );       /*시도세요율     */
				
				mfTrafficLevyList.setMap("TAX_NOTICE_TITLE"   , "교통유발부담금");
				mfTrafficLevyList.setMap("ORG_PART_CODE"      , "");
				mfTrafficLevyList.setMap("ACCOUNT_NAME"       , "시군구세");
				mfTrafficLevyList.setMap("TAX_NM"             , "교통유발부담금");
				mfTrafficLevyList.setMap("REG_TEL"            , "");
				mfTrafficLevyList.setMap("REG_ZIPCD"          , "");
				mfTrafficLevyList.setMap("ADDRESS"            , "");
				mfTrafficLevyList.setMap("LEVY_DETAIL1"       , "");
				mfTrafficLevyList.setMap("LEVY_DETAIL2"       , "");
				mfTrafficLevyList.setMap("LEVY_DETAIL3"       , "");
				mfTrafficLevyList.setMap("LEVY_DETAIL4"       , "");
				mfTrafficLevyList.setMap("LEVY_DETAIL5"       , "");
				mfTrafficLevyList.setMap("LEVY_DETAIL6"       , "");
				mfTrafficLevyList.setMap("SUBJECT_NAME1"      , "");
				mfTrafficLevyList.setMap("SUBJECT_NAME2"      , "");
				mfTrafficLevyList.setMap("SUBJECT_NAME3"      , "");
				mfTrafficLevyList.setMap("PAYMENT_DATE2"      , 0 );
				mfTrafficLevyList.setMap("AFTPAYMENT_DATE2"   , 0 );				
				mfTrafficLevyList.setMap("PAYMENT_DATE3"      , 0 );
				mfTrafficLevyList.setMap("AFTPAYMENT_DATE3"   , 0 );
				mfTrafficLevyList.setMap("BUGWA_BUSEONAME"    , "");
				mfTrafficLevyList.setMap("SUNAP_BUSEONAME"    , "");
				mfTrafficLevyList.setMap("USER_NAME"          , "");
				mfTrafficLevyList.setMap("USER_TEL_NO"        , "");
				mfTrafficLevyList.setMap("SNTG"               , "0");
				mfTrafficLevyList.setMap("PAID_DATE"          , "");
				mfTrafficLevyList.setMap("EPAY_NO_OLD"        , "");	

				try {

					if (cyberService.queryForUpdate("TXDM2420.INSERT_PUB_C_LEVY_DETAIL", mfTrafficLevyList) > 0) {
						cyberService.queryForUpdate("TXDM2420.INSERT_PUB_C_LEVY", mfTrafficLevyList);
						
						insert_cnt++;
					} 
					
				}catch (Exception e){

					/*중복이 발생하거나 제약조건에 어긋나는 경우체크*/
					if (e instanceof DuplicateKeyException){
						
						try{
													
							if (cyberService.queryForUpdate("TXDM2420.UPDATE_PUB_C_LEVY_DETAIL", mfTrafficLevyList) > 0) {
								cyberService.queryForUpdate("TXDM2420.UPDATE_PUB_C_LEVY", mfTrafficLevyList);
							    update_cnt++;
							}
							
						}catch (Exception sub_e){
							log.error("오류데이터 = " + mfTrafficLevyList.getMaps());
							e.printStackTrace();
							throw (RuntimeException) sub_e;
						}
						
					}else{
						log.error("오류데이터 = " + mfTrafficLevyList.getMaps());
						e.printStackTrace();							
						throw (RuntimeException) e;
					}
					
				}

			}
			
		}
		
		log.info("교통유발부담금(" + c_slf_org_nm + ") 자료연계 건수::" + rec_cnt + ", 등록건수::" + insert_cnt +", 수정건수::" + update_cnt + ", 삭제건수::" + delete_cnt);
		
	}

}
