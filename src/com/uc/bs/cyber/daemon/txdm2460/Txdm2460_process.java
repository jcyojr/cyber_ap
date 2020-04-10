/**
 *  주시스템명 : 부산시청 사이버 지방세청
 *  업  무  명 : 연계
 *  기  능  명 : 환경개선부담금 고지자료연계
 *  클래스  ID : Txdm2460_process : 실 처리로직을 작성하는 클래스
 *  변경  이력 : 2014.01.23 간단 e-납부 실시간 수납처리 추가 및 필드 추가 
 *               ENV_RAMT-수납금액, ENV_RGBN-수납구분, ENV_SYSDATE-등록일자
 * ------------------------------------------------------------------------
 *  작성자      	소속  		    일자            Tag           내용
 * ------------------------------------------------------------------------
 *  송동욱       유채널(주)      2011.06.03         %01%         최초작성
 */
package com.uc.bs.cyber.daemon.txdm2460;

import java.math.BigDecimal;
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
public class Txdm2460_process extends Codm_BaseProcess implements Codm_interface {

	private MapForm dataMap            = null;
	private int loop_cnt = 0, dloop_cnt = 0, insert_cnt = 0, update_cnt = 0, del_cnt = 0;
	private int non_dt_cnt = 0;
	private int org_cnt, org_del_cnt = 0;
	// 간단 e 납부 실시간 수납 처리건수
	private int sunap_cnt = 0;
	
	/**
	  * 
	 */
	public Txdm2460_process() {
		
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
			
			int page = 0;
			
			setContext(appContext);
			setApp(appContext);

			UcContextHolder.setCustomerType(this.dataSource);
			
			dataMap = new MapForm();  /*초기화*/
			dataMap.setMap("PAGE_PER_CNT",  500);           /*페이지당 갯수*/
			
			if(jobId == 0) {
			
				do {
					
					page = govService.getOneFieldInteger("TXDM2460.SELECT_ENV_LEVY_PAGE_CNT", dataMap);
					
					log.info("[환경개선부담금부과] PAGE_PER_CNT(" + dataMap.getMap("PAGE_PER_CNT") + ") 당 Page갯수 =" + page);
					
					dataMap.setMap("GUBUN",  "1");
					
					if(page == 0){
						
						/***********************************************************************************/
						try{
							MapForm daemonMap = new MapForm();
							daemonMap.setMap("DAEMON"    , "TXDM2460");
							daemonMap.setMap("DAEMON_NM" , "환경개선(ENVTNA2BCR_ETAX)부과연계");
							daemonMap.setMap("SGG_COD"   , c_slf_org);
							daemonMap.setMap("TOTAL_CNT" , 0);
							daemonMap.setMap("INSERT_CNT", 0);
							daemonMap.setMap("UPDATE_CNT", 0);
							daemonMap.setMap("DELETE_CNT", 0);
							daemonMap.setMap("ERROR_CNT" , 0);
							cyberService.queryForInsert("CODMBASE.InsertDaemonProcessCheck", daemonMap);
						}catch(Exception ex){
							log.debug("환경개선(ENVTNA2BCR_ETAX)부과연계 대상이 없습니다. 등록 오류");
						}				
						/***********************************************************************************/
						
						break;
					}
					
					for(int i = 1 ; i <= page ; i ++) {
						
						MapForm ststMap = new MapForm();
						
						ststMap.setMap("JOB_ID"    , "TXDM2460");
						ststMap.setMap("PARM_NM"   , this.c_slf_org + "||" + this.govId);
						ststMap.setMap("LOG_DESC"  , "== START ==");
						ststMap.setMap("RESULT_CD" , "S");
						
						/*환경개선부담금 고지자료연계 시작... */
						cyberService.queryForUpdate("TXDM2420.INSERT_LOG_JOBSTATE", ststMap);
						
						dataMap.setMap("PAGE",  i);    /*처리페이지*/
						
						this.startJob();
						
						ststMap.setMap("LOG_DESC"  , "== END  환경개선부담금 고지자료연계 처리건수::" + loop_cnt + ", 부과처리::" + insert_cnt + ", 업데이트::" + update_cnt + ", 삭제처리::" + del_cnt);
						ststMap.setMap("RESULT_CD" , "E");
						
						/*부과전송자료 통계완료 */
						cyberService.queryForUpdate("TXDM2420.INSERT_LOG_JOBSTATE", ststMap);
						
					}
					
					if(govService.getOneFieldInteger("TXDM2460.SELECT_ENV_LEVY_PAGE_CNT", dataMap) == 0) {
						break;
					}

				} while(true);
				
			}else if(jobId == 1) {
				
				do {
					
					//page = govService.getOneFieldInteger("TXDM2460.SELECT_ENV_LEVY_DEL_PAGE_CNT", dataMap);
					
					page = govService.getOneFieldInteger("TXDM2460.getEnvTaxSunapAndDeleteDataCount", dataMap);
					
					log.info("[환경개선부담금 간단e납부수납 및 삭제] PAGE_PER_CNT(" + dataMap.getMap("PAGE_PER_CNT") + ") 당 Page갯수 =" + page);
					
					dataMap.setMap("GUBUN",  "2");
					
					if(page == 0) break;
					
					for(int i = 1 ; i <= page ; i ++) {

						dataMap.setMap("PAGE",  i);    /*처리페이지*/
						
						this.startJob();
					}
					
					//if(govService.getOneFieldInteger("TXDM2460.SELECT_ENV_LEVY_PAGE_CNT", dataMap) == 0) {
					//	break;
					//}
					
					if(govService.getOneFieldInteger("TXDM2460.getEnvTaxSunapAndDeleteDataCount", dataMap) == 0) {
						break;
					}

				} while(true);
				
			}

		} catch (Exception e) {
			
			e.printStackTrace();
		}			
		
	}
	
	/* process starting */
	public void runProcess() throws Exception {
		
		//log.debug("=====================================================================");
		//log.debug("=" + this.getClass().getName()+ " runProcess() ==");
		//log.debug("=====================================================================");
		
		/*트랜잭션 업무 구현*/
		mainTransProcess();
	}

	
	public void setDatasrc(String datasrc) {
		
		this.dataSource = datasrc;
	}

	
	public void transactionJob() {
		
		if(dataMap.getMap("GUBUN").equals("1")) {
			txdm2460_JobProcess1();
		} else if(dataMap.getMap("GUBUN").equals("2")) {
			txdm2460_JobProcess2();
		}
		
	}
	
	/**
	 * 부가 연계
	 * @return
	 */
	private int txdm2460_JobProcess1() {
		
		log.info("=====================================================================");
		log.info("=" + this.getClass().getName()+ " txdm2460_JobProcess1()[환경개선부담금 부과자료연계] Start =");
		log.info("=====================================================================");
		
		/*트랜잭션을 포함하는 실업무는 여기서 구현한다...*/
		/*실업무를 구현합니다.*/
		loop_cnt = 0; 
		insert_cnt = 0;
		update_cnt = 0;
		non_dt_cnt = 0;
		org_cnt = 0;

		long elapseTime1 = 0;
		long elapseTime2 = 0;
		
		int levyCnt = 0;
		
		/*구청코드셋팅*/
		dataMap.setMap("SGG_COD" , c_slf_org);
		
		try{
			/*환경개선부담금 부과내역을 가져온다.*/
			ArrayList<MapForm> alEnvLevyList =  govService.queryForList("TXDM2460.SELECT_ENV_LEVY_LIST", dataMap);
			
			levyCnt = alEnvLevyList.size();
			
			log.info("[" + this.c_slf_org_nm + "]환경개선부담금 부과내역 건수 = " + levyCnt);
			
			if (levyCnt  >  0)   {
			
				elapseTime1 = System.currentTimeMillis();
				
				for (int rec_cnt = 0;  rec_cnt < alEnvLevyList.size();  rec_cnt++)   {
					
					MapForm mpEnvLevyList =  alEnvLevyList.get(rec_cnt);
					
					if (mpEnvLevyList == null  ||  mpEnvLevyList.isEmpty() )   {
						continue;
					}
					
					loop_cnt++;
					
					String sntg = (String) mpEnvLevyList.getMap("ENV_SNTG");
					String trtg = (String) mpEnvLevyList.getMap("ENV_TRTG");
					
					if(sntg.equals("0") && trtg.equals("0")){
						
						/*==========부과자료 저장...===========*/
						/*초기값 세팅*/
						mpEnvLevyList.setMap("CUD_OPT"   ,"1");  /*처리구분코드*/
						mpEnvLevyList.setMap("PROC_CLS"  ,"1");  /*가상계좌체번구분*/
						mpEnvLevyList.setMap("DEL_YN"    ,"N"); 
						mpEnvLevyList.setMap("SGG_TR_TG", "0");  /*구청전송처리구분*/
						
						try{
							
							
							if (cyberService.queryForUpdate("TXDM2460.INSERT_ENV_H_LEVY_DESC", mpEnvLevyList) > 0) {
								
				
								cyberService.queryForUpdate("TXDM2460.INSERT_ENV_H_LEVY", mpEnvLevyList);
								//잠시막음
								//cyberService.queryForUpdate("TXDM2460.UPDATE_ENV_H_LEVY_OCRBD", mpEnvLevyList);						
								insert_cnt++;
							} 
							
						}catch (Exception e){
							
							/*중복이 발생하거나 제약조건에 어긋나는 경우체크*/
							if (e instanceof DuplicateKeyException){
								
								MapForm mapPLTInfo = cyberService.queryForMap("TXDM2460.SELECT_ENV_PENALTY_INFO", mpEnvLevyList);
								
								if (mapPLTInfo == null  ||  mapPLTInfo.isEmpty() )   {
									continue;
								}
								
								long envamt2 = ((BigDecimal)(mpEnvLevyList.getMap("BUTT"))).longValue() + ((BigDecimal)(mpEnvLevyList.getMap("ADD_AMT"))).longValue();
								long envamt1 = ((BigDecimal)(mpEnvLevyList.getMap("BUTT"))).longValue();
								
								long pltamt = ((BigDecimal)(mapPLTInfo.getMap("ENV_AMT"))).longValue();
								
								try{
									
									boolean virRemake=false;
									
									/*부가시 가상계좌에 표시할 이름이 같으면...*/
									if (!virRemake&&!mpEnvLevyList.getMap("REG_NM").equals(mapPLTInfo.getMap("REG_NM")))virRemake=true;
									if (!virRemake&&!mpEnvLevyList.getMap("ENV_CNAPG").equals(mapPLTInfo.getMap("ENV_CNAPG")))virRemake=true;
									if (!virRemake&&!mpEnvLevyList.getMap("DEADLINE_DT").equals(mapPLTInfo.getMap("DEADLINE_DT")))virRemake=true;
									if (!virRemake&&!mpEnvLevyList.getMap("DUE_DT").equals(mapPLTInfo.getMap("DUE_DT")))virRemake=true;
									if (!virRemake&&!mpEnvLevyList.getMap("DUE_F_DT").equals(mapPLTInfo.getMap("DUE_F_DT")))virRemake=true;
									if (!virRemake&&pltamt != envamt2)virRemake=true;
									
									if(virRemake){
										mpEnvLevyList.setMap("AMTEQ"      ,"N");   /*업데이트 쿼리조건*/
										mpEnvLevyList.setMap("PROC_CLS"   ,"1");   /*처리구분*/
										//mpEnvLevyList.setMap("VIR_ACC_NO" ,CbUtil.nullChk(mpEnvLevyList.getMap("ENV_ACC_NO")));    /*세올사업단에서 가지고 있는 가상계좌*/
										//mpEnvLevyList.setMap("SGG_TR_TG"  ,"0");   /*전송처리구분*/
									}else{
										mpEnvLevyList.setMap("AMTEQ"      ,"Y");   /*업데이트 쿼리조건*/
										//mpEnvLevyList.setMap("SGG_TR_TG"  ,"0");   /*전송처리구분*/
									}
									//if(mpEnvLevyList.getMap("ENV_ACC_NO").equals("") || mpEnvLevyList.getMap("ENV_ACC_NO") == null ){
									//	mpEnvLevyList.setMap("SGG_TR_TG"  ,"0");   /*전송처리구분*/
									//}else{
									//	mpEnvLevyList.setMap("SGG_TR_TG"  ,"1");   /*전송처리구분*/
									//}

									if (cyberService.queryForUpdate("TXDM2460.UPDATE_ENV_H_LEVY_DESC", mpEnvLevyList) > 0) {
				
										cyberService.queryForUpdate("TXDM2460.UPDATE_ENV_H_LEVY", mpEnvLevyList);
										//cyberService.queryForUpdate("TXDM2460.UPDATE_ENV_H_LEVY_OCRBD", mpEnvLevyList);
									    update_cnt++;
									}

								}catch (Exception ex){
									log.info("오류발생데이터 = " + mpEnvLevyList.getMaps());
									ex.printStackTrace();
									log.info("Exception ex " + ex.getMessage());
									throw (RuntimeException) ex;
								}

							}else{
								log.info("오류발생데이터 = " + mpEnvLevyList.getMaps());
								e.printStackTrace();
								log.info("Exception e " + e.getMessage());
								throw (RuntimeException) e;
							}
							
						}
						
						/*부가처리여부 업데이트*/
						if(govService.queryForUpdate("TXDM2460.UPDATE_TR_ENVTAX_TAB", mpEnvLevyList) > 0) {
							org_cnt++;
						}
				
						
					}else{
						non_dt_cnt++;
					}
					
				}
				
				elapseTime2 = System.currentTimeMillis();
				
				log.info("[H][" + c_slf_org_nm + "]부가자료 연계건수 : " + levyCnt + " (EA)");
				log.info("[H][" + c_slf_org_nm + "]부가자료 연계시간 : " + CbUtil.formatTime(elapseTime2 - elapseTime1));
				
			}
		
			log.info("환경개선부담금 고지자료연계 처리건수::" + loop_cnt + ", 부과처리::" + insert_cnt + ", 업데이트::" + update_cnt + ", 원장업데이트 = " + org_cnt); 

			/***********************************************************************************/
			try{
				MapForm daemonMap = new MapForm();
				daemonMap.setMap("DAEMON"    , "TXDM2460");
				daemonMap.setMap("DAEMON_NM" , "환경개선(ENVTNA2BCR_ETAX)부과연계");
				daemonMap.setMap("SGG_COD"   , c_slf_org);
				daemonMap.setMap("TOTAL_CNT" , loop_cnt);
				daemonMap.setMap("INSERT_CNT", insert_cnt);
				daemonMap.setMap("UPDATE_CNT", update_cnt);
				daemonMap.setMap("DELETE_CNT", 0);
				daemonMap.setMap("ERROR_CNT" , non_dt_cnt);
				cyberService.queryForInsert("CODMBASE.InsertDaemonProcessCheck", daemonMap);
			}catch(Exception ex){
				log.debug("환경개선(ENVTNA2BCR_ETAX)부과연계 로그 등록 오류");
			}				
			/***********************************************************************************/
			
			
		}catch (Exception e){
			e.printStackTrace();
		}
		
		return levyCnt;
	}
	
	/**
	 * 수납 및 삭제 연계
	 * @return
	 */
	private int txdm2460_JobProcess2() {
		
		log.info("=====================================================================");
		log.info("=" + this.getClass().getName()+ " txdm2460_JobProcess1()[환경개선부담금 삭제자료연계] Start =");
		log.info("=====================================================================");
		
		/*트랜잭션을 포함하는 실업무는 여기서 구현한다...*/
		/*실업무를 구현합니다.*/
		dloop_cnt = 0; 
		del_cnt = 0;
		org_del_cnt = 0;
		sunap_cnt = 0;
		
		int bugaInsert_cnt = 0;
		int bugaDelyn_cnt = 0;
		int pass_cnt = 0;
		int gandan_cnt = 0;
		
		long elapseTime1 = 0;
		long elapseTime2 = 0;
		
		int levyCnt = 0;
		
		/*구청코드셋팅*/
		dataMap.setMap("SGG_COD" , c_slf_org);
		
		try{

			/*환경개선부담금 부과내역중 삭제처리해야 할 DATA를 가져온다.*/
			//ArrayList<MapForm> alEnvLevyDelList =  govService.queryForList("TXDM2460.SELECT_ENV_LEVY_DEL_INFOKEY", dataMap);
			
			//연계테이블에서 간단 e 납부 수납처리 및 삭제처리 해야할 DATA를 가져온다.
			ArrayList<MapForm> alEnvLevyDelList =  govService.queryForList("TXDM2460.getEnvTaxSunapDataAndDeleteData", dataMap);
			
			levyCnt = alEnvLevyDelList.size();
			
			log.info("[" + this.c_slf_org_nm + "]환경개선부담금 간단e납부 및 삭제부과 건수 = " + levyCnt);
			
			if(levyCnt > 0){
				
				elapseTime1 = System.currentTimeMillis();
				
				// 부과자료, 수납자료, 수납순번 확인  
				MapForm bugaCheck = new MapForm();
				
				for(int rec_cnt = 0;  rec_cnt < alEnvLevyDelList.size();  rec_cnt++){
					
					MapForm mpEnvLevyDelList =  alEnvLevyDelList.get(rec_cnt);
					
					if(mpEnvLevyDelList == null || mpEnvLevyDelList.isEmpty()){
						continue;
					}				
											
					
					// 간단 e 납부 실시간 수납 건
					if(mpEnvLevyDelList.getMap("SNSU").equals("1")){
						
						sunap_cnt++;
						
						bugaCheck = new MapForm();
						
						bugaCheck = cyberService.queryForMap("TXDM2460.getBugaDataAndSunapResult", mpEnvLevyDelList);
						
						// 수납순번
						mpEnvLevyDelList.setMap("PAY_CNT", bugaCheck.getMap("PAY_CNT"));
						
						// 수납처리 세팅
						mpEnvLevyDelList.setMap("SNTG"   , "2");
						mpEnvLevyDelList.setMap("SNSU"   , "1");
						mpEnvLevyDelList.setMap("BANK_CD", "100");
						mpEnvLevyDelList.setMap("BRC_NO" , "1000000");
						mpEnvLevyDelList.setMap("TRTG"   , "1");
						
						/*초기값 세팅*/
						mpEnvLevyDelList.setMap("CUD_OPT"   , "1");  /* 처리구분코드     1:등록,2:수정,3:삭제  */
						mpEnvLevyDelList.setMap("PROC_CLS"  , "3");  /* 가상계좌체번구분 1:대기,2:생성중,3:완료*/
						mpEnvLevyDelList.setMap("DEL_YN"    , "N"); 
						mpEnvLevyDelList.setMap("SGG_TR_TG" , "1");  /* 구청전송처리구분 0:미전송 1:전송       */
																
						// 전문에 의한 수납처리 완료
						if(bugaCheck.getMap("SNSU").equals("1")){
							
							pass_cnt++;
							log.info("전문에 의해 이미 수납 처리 되었습니다.");
							
						}
						// 부과자료 미생성
						else if(bugaCheck.getMap("SNSU").equals("NOT") && bugaCheck.getMap("DEL_YN").equals("NOT")){
							
							bugaInsert_cnt++;
							
							/*==========부과자료 등록 후 수납처리===========*/
							log.info("부과자료가 없어서 생성 후 수납처리 합니다.");						
							
							// 부과자료 등록
							try{														
								if(cyberService.queryForUpdate("TXDM2460.INSERT_ENV_H_LEVY_DESC", mpEnvLevyDelList) > 0){									
									cyberService.queryForUpdate("TXDM2460.INSERT_ENV_H_LEVY", mpEnvLevyDelList);									
								} 								
							}catch (Exception e){
								e.printStackTrace();
								log.info("부과등록 오류 "+ mpEnvLevyDelList.getMaps());
							}
							
							// 수납자료 등록
							try{
								cyberService.queryForInsert("TXDM2460.insertGandanENapbuSunap", mpEnvLevyDelList);
							}catch(Exception e){
								e.printStackTrace();
								log.info("수납처리 오류" + mpEnvLevyDelList.getMaps());
							}
																			
						}
						// 간단 e 납부 수납처리
						else if(bugaCheck.getMap("SNSU").equals("NOT") && !bugaCheck.getMap("DEL_YN").equals("NOT")){
							
							bugaDelyn_cnt++;	
							
							log.info("간단 e 납부 수납처리");
							
							// 부과자료 TX2132_TB 수납처리
							try{
								cyberService.queryForUpdate("TXDM2460.updateBugaDataSntgAndDelyn", mpEnvLevyDelList);
							}catch(Exception e){
								e.printStackTrace();
								log.info("부과자료 SNTG, DELYN 변경 오류 " + mpEnvLevyDelList.getMaps());
							}
							// 수납자료 등록
							try{
								cyberService.queryForInsert("TXDM2460.insertGandanENapbuSunap", mpEnvLevyDelList);
							}catch(Exception e){
								e.printStackTrace();
								log.info("수납처리 오류 " + mpEnvLevyDelList.getMaps());
							}
							
						}
						
						
					}
					// 기존 삭제 처리 건
					else if(mpEnvLevyDelList.getMap("SNSU").equals("DEL")){
											
						String sntg = (String) mpEnvLevyDelList.getMap("SNTG");
						String trtg = (String) mpEnvLevyDelList.getMap("SGG_TR_TG");
						
						long amt = ((BigDecimal)(mpEnvLevyDelList.getMap("BUTT"))).longValue() + ((BigDecimal)(mpEnvLevyDelList.getMap("ADD_AMT"))).longValue();
						
						if(((sntg.equals("8")||sntg.equals("9")) && trtg.equals("0")) || (amt == 0 && trtg.equals("0"))){
							
							if(cyberService.queryForUpdate("TXDM2460.UPDATE_DEL_H_LEVY_DESC", mpEnvLevyDelList) > 0) {
								del_cnt++;
							}
						} else {
							log.debug("삭제조건에 해당안됨 [" + mpEnvLevyDelList.getMaps() + "]");
						}
						
					}else{
						// 오류
						log.debug(" 오류 [" + mpEnvLevyDelList.getMaps() + "]");
					}
										
					
					/*원장 업데이트*/
					if(govService.queryForUpdate("TXDM2460.UPDATE_TR_DEL_ENVTAX_TAB", mpEnvLevyDelList) > 0){
						org_del_cnt++;
					}

				}
				
				elapseTime2 = System.currentTimeMillis();
				log.info("[H][" + c_slf_org_nm + "]환경개선 간단 e 납부 및 삭제자료 연계시간 : " + CbUtil.formatTime(elapseTime2 - elapseTime1));
				log.info("전체건수::" + alEnvLevyDelList.size() +", 간단 e 납부 수납건수 :: "+sunap_cnt+ " 중 부과생성"+bugaInsert_cnt+" 간단처리 "+bugaDelyn_cnt+" 건 , 기존삭제처리::" + del_cnt + ", 원장업데이트 ::" + org_del_cnt);
				
			}else{
				log.info("환경개선부담금 간단e납부 수납처리 및 삭제처리 건수가 없습니다.");
			}
						
			
		}catch (Exception e){
			e.printStackTrace();
		}
		
		return levyCnt;
	}
	
}
