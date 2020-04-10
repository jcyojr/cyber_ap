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
 *  송동욱       유채널(주)      2011.05.11         %01%         최초작성
 */
package com.uc.bs.cyber.batch.txbt2412;

import java.util.ArrayList;
import java.util.Iterator;

import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.UncategorizedSQLException;

import com.uc.bs.cyber.CbUtil;
import com.uc.bs.cyber.batch.Txdm_BaseProcess;
import com.uc.bs.cyber.daemon.Codm_interface;
import com.uc.core.MapForm;
import com.uc.core.spring.service.UcContextHolder;

/**
 * @author Administrator
 *
 */
public class Txbt2412_process extends Txdm_BaseProcess implements Codm_interface {
	
	private MapForm dataMap  = null;
	
	/**
	 * 
	 */
	public Txbt2412_process() {
		// TODO Auto-generated constructor stub
		super();
		
		log = LogFactory.getLog(this.getClass());

	}

	/*Context 주입용*/
	public void setApp(ApplicationContext context) {
		this.context = context;
	}
	
	/* process starting */
	@Override
	public void runProcess() throws Exception {
		// TODO Auto-generated method stub
		
		//log.debug("=====================================================================");
		//log.debug("=" + this.getClass().getName()+ " runProcess() ==");
		//log.debug("=====================================================================");
		
		/*초기화*/
		
		/* 실업무 구현*/
		txbt2412_JobProcess();
		
		/*트랜잭션 업무 구현*/
		//mainTransProcess();
		
	}

	/*트랜잭션을 실행하기 위한 함수.*/
	@SuppressWarnings("unused")
	private void mainTransProcess(){
		
		try {
				
			setContext(appContext);
			setApp(appContext);

			UcContextHolder.setCustomerType(this.dataSource);
			
			this.startJob();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}			
		
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
		log.info("=====================================================================");
		
		/*트랜잭션을 포함하는 실업무는 여기서 구현한다...*/
		/*실업무를 구현합니다.*/
	}
	
	
	/*실연계업무 시작*/
	private void txbt2412_JobProcess(){
				
		/*-------------Context 주입 및 연계DB설정----------------*/
		this.context = appContext;
		UcContextHolder.setCustomerType(this.dataSource);
		/*-------------------------------------------------------*/
		long elapseTime1 = 0;
		long elapseTime2 = 0;
		long t_elpTime1 = 0;
		long t_elpTime2 = 0;
		
		int intLog = 0;
		
		dataMap = new MapForm();  /*초기화*/
		
		/*연계테이블 Link 셋팅*/
		dataMap.setMap("SGG_COD"        , this.c_slf_org);
		dataMap.setMap("BS_NOT_IN_SEMOK", not_in_semok());
		
		try {
			
			/*---------------------------------------------------------*/
			/*1. 부가연계자료 업무 처리.                               */
			/*---------------------------------------------------------*/
			if(log.isInfoEnabled()){
				log.info("["+c_slf_org_nm +"]====================체납부가연계자료 시작    ======================");
			}
			
			/*처음 기준시간 셋팅*/
			elapseTime1 = System.currentTimeMillis();
			t_elpTime1  = System.currentTimeMillis();
			
			dataMap.setMap("SEOI_GB", "01");   /*01:부과, 02:부가가치세*/
			
			/*체납부과정보 통계(NIS)*/
			MapForm maplvyInfo  = govService.queryForMap("TXBT2412.SELECT_FAIL_LEVY_INFO_STATIC", dataMap);
			
			if ( maplvyInfo == null  ||  maplvyInfo.isEmpty() )   {
				
				log.debug("체납부과정보 통계를 가져올 수 없습니다...");
				
				maplvyInfo.setMap("DEPT_CD", this.dataSource.substring(3));
				maplvyInfo.setMap("CNT", 0);
				
			} else {
				
				log.debug(" 부서코드(구청) = " + maplvyInfo.getMap("DEPT_CD"));
				log.debug(" 체납부과건수 = " + maplvyInfo.getMap("CNT"));
				
			}
			
			MapForm mapLog = new MapForm();
			
			mapLog.setMap("TRS_DT"      , CbUtil.getCurrent("yyyyMMddHHmmss"));
			mapLog.setMap("SGG_COD"     , c_slf_org);
			mapLog.setMap("TYPE"        , "I");
			mapLog.setMap("CNT"         , maplvyInfo.getMap("CNT"));
			mapLog.setMap("END_CNT"     , 0  );
			mapLog.setMap("GUBUN"       , "01");     /*체납 01, 부과 00*/
			
			/*전송시작 로깅작업 (insert 문 : 결과를 받기 위해 update 함수에 insert 문을 실행함*/
			intLog = cyberService.queryForUpdate("TXBT2412.INSERT_LOG_TX2191_TB", mapLog);
			
			log.debug("intLog = " + intLog);
			
			/*전자납부 체납고지내역을 조회한다.(NIS)*/
			ArrayList<MapForm> weNidLevyList = govService.queryForList("TXBT2412.SELECT_FAIL_LEVY_INFO_LIST", dataMap);
			
			long ins = 0;
			
			if (weNidLevyList.size()  >  0)   {
				
				/*전자납부자료 1건씩 fetch 처리 */
				for ( int rec_cnt = 0;  rec_cnt < weNidLevyList.size();  rec_cnt++)   {
					
					MapForm mfNidLevyList =  weNidLevyList.get(rec_cnt);
					
					/*혹시나...because of testing */
					if (mfNidLevyList == null  ||  mfNidLevyList.isEmpty() )   {
						continue;
					}
					
					/*기본 Default 값 설정 */
					mfNidLevyList.setMap("BU_ADD_YN"   , "N" );       /*부가가치세구분 'N'           */
					mfNidLevyList.setMap("TAX_CNT"     ,  0  );       /*부과순번 0                   */
					mfNidLevyList.setMap("PROC_CLS"    , "1" );       /*가상계좌채번구번 default '1' */
					mfNidLevyList.setMap("DEL_YN"      , "N" );       /*삭제여부         default 'N' */
					
					
					/**
					 * 어떤문제인지...
					 * 값이나 key 가 null 인 경우 오류가 발생하므로
					 * 값이 null 이면 ''로 바꿔주도록 처리한다.....2011.08.01-FreeB------
					 */
					nullToStr(mfNidLevyList);
					
					
					/*===============================*/
					/*부과테이블 입력*/
					/*===============================*/
					try {
						
						/*신규 INSERT*/
						mfNidLevyList.setMap("CUD_OPT"     , "1" );       /*자료등록구분(신)*/
						
						/*표준세외수입부과 테이블 입력*/
						cyberService.queryForUpdate("TXBT2412.INSERT_PUB_SEOI_LEVY", mfNidLevyList);
						

					}catch (Exception e){
						
						if (e instanceof DuplicateKeyException){
						
							/*기존정보 업데이트*/
							mfNidLevyList.setMap("CUD_OPT"     , "2" );       /*자료등록구분(업)*/
							
							/*표준세외수입부과 테이블 업데이트*/
	                    	cyberService.queryForUpdate("TXBT2412.UPDATE_PUB_SEOI_LEVY", mfNidLevyList);	

						}else{
							log.error("등록 시스템오류 = " + mfNidLevyList.getMaps());
							e.printStackTrace();							
							throw (RuntimeException) e;
						}
					}
					
					/*===============================*/
					/*부과상세테이블 입력*/
					/*===============================*/
					try {
						
						/*신규 INSERT*/
						mfNidLevyList.setMap("CUD_OPT"     , "1" );       /*자료등록구분(신) */
						
						/*표준세외수입부과 상세테이블 입력*/
						cyberService.queryForUpdate("TXBT2412.INSERT_PUB_SEOI_LEVY_DETAIL", mfNidLevyList);
						
					}catch (Exception e){
						
						if (e instanceof DuplicateKeyException){
							
							/*기존정보 업데이트*/
							mfNidLevyList.setMap("CUD_OPT"     , "2" );       /*자료등록구분(업) */
						
							/*표준세외수입부과 상세테이블 업데이트*/
							cyberService.queryForUpdate("TXBT2412.UPDATE_PUB_SEOI_LEVY_DETAIL", mfNidLevyList);
							
							
						}else{
							log.error("등록 시스템오류 = " + mfNidLevyList.getMaps());
							e.printStackTrace();							
							throw (RuntimeException) e;
						}
					}
					

					ins++;
					mfNidLevyList.clear();
					
				}
				
			}
			
			mapLog.setMap("END_DTM"      , CbUtil.getCurrent("yyyyMMddHHmmss"));
			mapLog.setMap("END_CNT"      , ins);
			
			/*전송결과 업데이트*/
			intLog = cyberService.queryForUpdate("TXBT2412.UPDATE_LOG_TX2191_TB", mapLog);
			
			/*마지막 기준시간 셋팅*/
			elapseTime2 = System.currentTimeMillis();
			
			log.info("["+c_slf_org_nm +"]부가자료 연계건수 : " + ins + " (EA)");
			log.info("["+c_slf_org_nm +"]부가자료 연계시간 : " + CbUtil.formatTime(elapseTime2 - elapseTime1));
			
			/*---------------------------------------------------------*/
			/*2. 체납부가가치세 업무 처리.                                 */
			/*---------------------------------------------------------*/
			if(log.isInfoEnabled()){
				log.info("["+c_slf_org_nm +"]====================체납부가가치세업무 시작  ======================");
			}
			
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
			}
			
			elapseTime1 = System.currentTimeMillis();
			
			dataMap.setMap("SEOI_GB", "02");   /*01:부과, 02:부가가치세*/
			
			/*부가가치세 전송로그를 구하기 위한 통계*/
			MapForm mapVatlvyInfo  = govService.queryForMap("TXBT2412.SELECT_FAIL_LEVY_INFO_STATIC", dataMap);
			
			ins = 0;
			
			if ( mapVatlvyInfo == null  ||  mapVatlvyInfo.isEmpty() )   {
				
				log.debug("체납부가가치세 통계를 가져올 수 없습니다...");
				
				mapVatlvyInfo.setMap("DEPT_CD", this.dataSource.substring(3));
				mapVatlvyInfo.setMap("CNT", 0);
								
			}
			mapLog = new MapForm();
			mapLog.setMap("TRS_DT"      , CbUtil.getCurrent("yyyyMMddHHmmss"));
			mapLog.setMap("SGG_COD"     , this.dataSource.substring(3));
			mapLog.setMap("TYPE"        , "B");
			mapLog.setMap("CNT"         , mapVatlvyInfo.getMap("CNT"));
			mapLog.setMap("END_CNT"     , 0  );
			mapLog.setMap("GUBUN"       , "01");
			
			/*전송시작 로깅작업 (insert 문 : 결과를 받기 위해 update 함수에 insert 문을 실행함*/
			intLog = cyberService.queryForUpdate("TXBT2412.INSERT_LOG_TX2191_TB", mapLog);
					
			/*부가가치세 고지내역을 조회한다.(NIS)*/
			ArrayList<MapForm> weNidVatLevyList = govService.queryForList("TXBT2412.SELECT_FAIL_VAT_INFO_LIST", dataMap);
			
			if (weNidVatLevyList.size()  >  0)   {
				
				/*부가세 1건씩 fetch 처리 */
				for ( int rec_cnt = 0;  rec_cnt < weNidVatLevyList.size();  rec_cnt++)   {
					
					MapForm mfNidVatLevyList =  weNidVatLevyList.get(rec_cnt);
					
					/*회계정보 31, 41, 61*/
					String cACCT = cyberService.getOneFieldString("TXBT2412.SELECT_VAT_ACCT_LIST", mfNidVatLevyList);
					
					if (cACCT != null && (cACCT.equals("31") || cACCT.equals("41") || cACCT.equals("61"))){   /*시세외(31) / 군군세외(41,61)만 처리한다.*/
						
						log.debug("cACCT = " + cACCT);
						/*
						 * 부가가치세 기 부과자료 삭제
						 * (EX:일반고지로 부과처리되어 있는 부가세 자료를 찻아서 합산전 삭제)
						 * */
						if (cyberService.queryForDelete("TXBT2412.DELETE_EXT_VAT_DETAIL_INFO", mfNidVatLevyList) > 0 ){
							cyberService.queryForDelete("TXBT2412.DELETE_EXT_VAT_INFO", mfNidVatLevyList);
						}
						
						mfNidVatLevyList.setMap("SIGUNGU_TAX"  , mfNidVatLevyList.getMap("FST_AMT"));
						mfNidVatLevyList.setMap("CHK_ACC"      , cACCT);
						
						/*부과금액 및 OCR밴드를 업데이트 한다.*/
						if (cyberService.queryForUpdate("TXBT2412.UPDATE_VAT_AMT_DETAIL_SAVE", mfNidVatLevyList) > 0 ){
							cyberService.queryForUpdate("TXBT2412.UPDATE_VAT_AMT_SAVE", mfNidVatLevyList);
						}
						ins++;
					}
					
				}

			}
			
			mapLog.setMap("END_DTM"      , CbUtil.getCurrent("yyyyMMddHHmmss"));
			mapLog.setMap("END_CNT"      , ins);
			
			/*전송결과 업데이트*/
			intLog = cyberService.queryForUpdate("TXBT2412.UPDATE_LOG_TX2191_TB", mapLog);
			
			elapseTime2 = System.currentTimeMillis();
			
			log.debug("["+c_slf_org_nm +"]부가가치세자료 연계건수 : " + ins + " (EA)");
			log.debug("["+c_slf_org_nm +"]부가가치세자료 연계시간 : " + CbUtil.formatTime(elapseTime2 - elapseTime1));
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
			}
			/*---------------------------------------------------------*/
			/*3. 수납자료 삭제업무 처리.                               */
			/*---------------------------------------------------------*/
			if(log.isInfoEnabled()){
				log.info("["+c_slf_org_nm +"]====================체납수납자료삭제업무 시작======================");
			}
			
			elapseTime1 = System.currentTimeMillis();
			
			mapLog = new MapForm();
			mapLog.setMap("TRS_DT"      , CbUtil.getCurrent("yyyyMMddHHmmss"));
			mapLog.setMap("SGG_COD"     , this.dataSource.substring(3));
			mapLog.setMap("TYPE"        , "D");
			mapLog.setMap("CNT"         , 0  );
			mapLog.setMap("END_CNT"     , 0  );
			mapLog.setMap("GUBUN"       , "01");
			
			/*전송시작 로깅작업 (insert 문 : 결과를 받기 위해 update 함수에 insert 문을 실행함*/
			intLog = cyberService.queryForUpdate("TXBT2411.INSERT_LOG_TX2191_TB", mapLog);
			
			ins = 0;
			
			/*표준세외수입 체납대장 테이블에서 회계년을 구한다.*/
			ArrayList<MapForm> alDelAcctInfo  = govService.queryForList("TXBT2412.SELECT_FAIL_KEY_FOR_DEL", null);
			
			if ( alDelAcctInfo.size()  >  0 )   {
				
				for ( int al_cnt = 0;  al_cnt < alDelAcctInfo.size();  al_cnt++)   {
					
					MapForm mfDelAcctInfo =  alDelAcctInfo.get(al_cnt);
					
					/*회계년을 기준으로 체납부과내역을 연계하여 가져온다. */
					ArrayList<MapForm> alDelInfoList  = govService.queryForList("TXBT2412.SELECT_FAIL_TAX_NO_FOR_DEL", mfDelAcctInfo);
					
					for ( int dl_cnt = 0;  dl_cnt < alDelInfoList.size();  dl_cnt++)   {
						
						MapForm mfDelInfoList =  alDelInfoList.get(dl_cnt);
						
						/*1건씩 비교하여 삭제필드에 업데이트 한다...*/
						
						if ( cyberService.queryForUpdate("TXBT2412.UPDATE_DEL_INFO_DETAIL", mfDelInfoList) > 0 ) {
							ins++;
						}

					}
					
				}
							
			}
			
			/*삭제*/
			//intLog = cyberService.queryForUpdate("TXBT2412.UPDATE_DEL_INFO_DETAIL_LINK", dataMap);
			
			log.info("["+c_slf_org_nm +"]수납자료 삭제건수 [" + this.dataSource.substring(3) + "] = [" + ins + "]");
			
			/*삭제는 마지막 시작건수 및 결과를 동일하게*/
			mapLog.setMap("CNT"         , ins);
			mapLog.setMap("END_DTM"     , CbUtil.getCurrent("yyyyMMddHHmmss"));
			mapLog.setMap("END_CNT"     , ins);
			
			
			/*전송시작 로깅작업 (insert 문 : 결과를 받기 위해 update 함수에 insert 문을 실행함*/
			intLog = cyberService.queryForUpdate("TXBT2411.UPDATE_LOG_TX2191_TB", mapLog);
			
			log.debug("intLog = " + intLog);
			
			elapseTime2 = System.currentTimeMillis();
			t_elpTime2 = System.currentTimeMillis();
			
			log.info("["+c_slf_org_nm +"]수납자료 삭제시간 : " + CbUtil.formatTime(elapseTime2 - elapseTime1));
			
			log.info("====================부과자료연계 끝  ======================");
			log.info("["+this.c_slf_org_nm+"]부과자료연계 시간 : " + CbUtil.formatTime(t_elpTime2 - t_elpTime1));
			log.info("===========================================================");
			
		} catch (UncategorizedSQLException use) {
			
            use.printStackTrace();
            
            if(log.isErrorEnabled()){
            	
            	log.error("=========== SQL 오류 발생 ===========");
            	log.error("SQLERRCODE = " + use.getSQLException().getErrorCode());
            	log.error(use.getMessage());
            	
            }
            
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}			

	}
	
	
	/*오라클 8 :: 을 사용하는 JDBC 드라이버용*/
	@SuppressWarnings("unchecked")
	private void nullToStr(MapForm mapForm) {
		// TODO Auto-generated method stub
		Iterator<String> iterForm = mapForm.getKeyList().iterator();
		
		while(iterForm.hasNext()) {
			String keyNm =  iterForm.next();
			if(mapForm.getMap(keyNm) == null) mapForm.setMap(keyNm, "");
		}
		
	}
	
	/*세외수입에서 제외시켜야 할 과목*/
	private String not_in_semok() {
	
		StringBuffer sb = new StringBuffer();
		String Retval = "";
		try {
	
			ArrayList<MapForm> alNoSemokList =  cyberService.queryForList("CODMBASE.NOSEOI", null);
			
			if (alNoSemokList.size()  >  0)   {
				
				for ( int rec_cnt = 0;  rec_cnt < alNoSemokList.size();  rec_cnt++)   {
					
					MapForm mpNoSemokList =  alNoSemokList.get(rec_cnt);
					
					/*혹시나...because of testing */
					if (mpNoSemokList == null  ||  mpNoSemokList.isEmpty() )   {
						continue;
					}
					
					sb.append("'").append(mpNoSemokList.getMap("SEMOK")).append("'").append(",");

				}
				
				Retval = sb.toString().substring(0, sb.toString().length() -1);
				
			} 
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	
		return Retval;
	}	
}
