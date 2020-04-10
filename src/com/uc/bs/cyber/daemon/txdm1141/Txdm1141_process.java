/**
 *  주시스템명 : 부산시청 사이버 지방세청
 *  업  무  명 : 연계
 *  기  능  명 : 과오납자료 자료연계
 *  클래스  ID : txdm1141_process : 실 처리로직을 작성하는 클래스
 *  변경  이력 :
 * ------------------------------------------------------------------------
 *  작성자      	소속  		    일자            Tag           내용
 * ------------------------------------------------------------------------
 *  천혜정       유채널(주)      2011.11.14         %01%         최초작성
 */
package com.uc.bs.cyber.daemon.txdm1141;

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
 */
public class Txdm1141_process extends Codm_BaseProcess implements Codm_interface {

	private MapForm dataMap            = null;
	private int insert_cnt = 0, update_cnt = 0, delete_cnt = 0, remote_up_cnt = 0, rcp_cnt = 0;
    private int p_del_cnt = 0;
	
	/*트랜잭션을 위하여 (지방세 과오납자료 연계) */
	MapForm gblNidLevyRows    = null;
	
	/*트랜잭션을 위하여 (지방세 수납요약정보 연계) */
	MapForm rcpListRow = null;
	
	/**
	 * 
	 */
	public Txdm1141_process() {
		// TODO Auto-generated constructor stub
		super();
		
		log = LogFactory.getLog(this.getClass());
		
		/**
		 * 5 분마다 돈다
		 */
		loopTerm = 300;
	}

	/*Context 주입용*/
	public void setApp(ApplicationContext context) {
		this.context = context;
	}
	
	/* process starting */
	@Override
	public void runProcess() throws Exception {
		// TODO Auto-generated method stub
		//log.info("=====================================================================");
		//log.info("=" + this.getClass().getName()+ " runProcess() ==");
		//log.info("=====================================================================");

		/*트랜잭션 업무 구현*/
		mainTransProcess();

	}

	/*트랜잭션을 실행하기 위한 함수.*/
	private void mainTransProcess(){

		log.info("=====================================================================");
		log.info("=[과오납-구청 자료연계-[" + c_slf_org_nm + "] 자료송신] Start =");
		log.info("=====================================================================");		
		
		/* * 
		 * */
		
		try {
				
			setContext(appContext);
			setApp(appContext);

			UcContextHolder.setCustomerType(this.dataSource);
						
			dataMap = new MapForm();  /*초기화*/

			dataMap.setMap("TRN_YN",  "0");						//전송상태가 '0'인 것을 조회한다.
			dataMap.setMap("PAGE_PER_CNT"   ,  500);            /*페이지당 갯수*/
			
				
			log.info("시작");
				
			int page = govService.getOneFieldInteger("txdm1141.txdm3141_select_count_page", dataMap);
				
			log.debug("[과오납(" + c_slf_org_nm + ")] PAGE_PER_CNT(" + dataMap.getMap("PAGE_PER_CNT") + ") 당 Page갯수 =" + page);
				
				
			for(int i = 1 ; i <= page ; i ++) {
				log.debug(i);
				dataMap.setMap("JobGb", "1");
				dataMap.setMap("PAGE",  i);    /*처리페이지*/
				this.startJob();               /*멀티 트랜잭션 호출*/
			}
				
	
			
			/* 지방세수납요약정보 추가 */
			
			log.info("=====================================================================");
			log.info("=[지방세수납요약정보-[" + c_slf_org_nm + "] 자료송신] Start =");
			log.info("=====================================================================");
			
			dataMap = new MapForm();  /*초기화*/
			
			log.info("this.c_slf_org ======================= [" + this.c_slf_org + "]");

			dataMap.setMap("SGG_COD",  this.c_slf_org);
			
			int rcpCnt = govService.getOneFieldInteger("TXDM1151.SELECT_COUNT", dataMap);
				
			log.info("[지방세수납요약정보-[" + c_slf_org_nm + "]] 건수(" + rcpCnt + ")");
				
			if(rcpCnt == 0){
				log.info("[지방세수납요약정보가 없습니다.");
				
				/***********************************************************************************/
				try{
					MapForm daemonMap = new MapForm();
					daemonMap.setMap("DAEMON"    , "TXDM1141");
					daemonMap.setMap("DAEMON_NM" , "수납(SCON540)정보연계");
					daemonMap.setMap("SGG_COD"   , c_slf_org);
					daemonMap.setMap("TOTAL_CNT" , rcpCnt);
					daemonMap.setMap("INSERT_CNT", 0);
					daemonMap.setMap("UPDATE_CNT", 0);
					daemonMap.setMap("DELETE_CNT", 0);
					daemonMap.setMap("ERROR_CNT" , 0);
					cyberService.queryForInsert("CODMBASE.InsertDaemonProcessCheck", daemonMap);
				}catch(Exception ex){
					log.debug("지방세 수납요약정보 건수가 없습니다. 등록 오류");
				}				
				/***********************************************************************************/
				
			}else{
				dataMap.setMap("JobGb", "2");
				this.startJob();
			}
				
				
					
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
			
		/*---------------------------------------------------------*/
		/*1. 부가연계자료 업무 처리.                               */
		/*---------------------------------------------------------*/
		/*업무 구분 1: 지방세 과오납자료 연계  2:지방세 수납요약정보 연계*/
		String jb = (String) dataMap.getMap("JobGb");
		
		txdm1141_JobProcess(jb);
	
	}
	
	
    /*과오납...연계(구청)*/
	private int txdm1141_JobProcess(String jb) {
		
		int tot_size=0;
		
		if(jb.equals("1")){ /* 1. 지방세 과오납자료 연계 */
			log.info("=====================================================================");
			log.info("=" + this.getClass().getName()+ " txdm1141_JobProcess()[과오납-구청 자료연계] Start =");
			log.info("= govid["+this.govId+"], orgcd["+this.c_slf_org+"]");
			log.info("=====================================================================");
			
			/*초기화*/
			
			insert_cnt = 0;
			update_cnt = 0; 
			delete_cnt = 0;
			remote_up_cnt = 0;
			p_del_cnt     = 0;
			
			/*전역 초기화*/
			gblNidLevyRows = new MapForm();
	
			/*트랜잭션을 포함하는 실업무는 여기서 구현한다...*/
			/*실업무를 구현합니다.*/
			
			int rec_cnt = 0;
			int nul_cnt = 0;
			
			tot_size = 0;
			
			long elapseTime1 = 0;
			long elapseTime2 = 0;
			
			elapseTime1 = System.currentTimeMillis();
	
			/*---------------------------------------------------------*/
			/*1. 부과연계자료 업무 처리.                               */
			/*---------------------------------------------------------*/
			
			try {
	
				dataMap.setMap("SGG_COD",  this.c_slf_org);
				dataMap.setMap("TRN_YN",  "0");
	
				/*연계테이블 부과자료 SELECT 쿼리(TAX)*/
				ArrayList<MapForm> alNidLevyList =  govService.queryForList("txdm1141.txdm3141_select_list", dataMap);
				
				tot_size = alNidLevyList.size();
				
			    log.info("구청과오납 연계자료 건수 = [" + tot_size + "]");
				
				if (tot_size  >  0)   {
					
					/*전자납부자료 1건씩 fetch 처리 */
					for ( rec_cnt = 0;  rec_cnt < tot_size;  rec_cnt++)   {
	
						gblNidLevyRows =  alNidLevyList.get(rec_cnt);
						
						/*혹시나...because of testing */
						if (gblNidLevyRows == null  ||  gblNidLevyRows.isEmpty() )   {
							nul_cnt++;
							continue;
						}
						
						try{
							/* 사이버에 인서트*/
							cyberService.queryForInsert("txdm1141.txdm1141_insert_tx1401_tb", gblNidLevyRows);
							insert_cnt++;
	
						 } catch (Exception e) {
							
							 /*중복이 발생하거나 제약조건에 어긋나는 경우체크*/
							 if (e instanceof DuplicateKeyException){	// 기본키가 중복되서 예외처리된경우 
									
								 try {
									 /* 사이버에 업데이트*/
									 cyberService.queryForUpdate("txdm1141.txdm1141_update_tx1401_tb", gblNidLevyRows);
									 update_cnt++;
										
								 } catch (Exception be) {
									 // TODO Auto-generated catch block
									 log.info("ERROR:txdm1141_update_tx1401_tb");
									 be.printStackTrace();
									throw (RuntimeException) be;
								 }
								 
							} else{
								log.info("ERROR:txdm1141_insert_tx1401_tb");
								log.error("오류데이터 = " + gblNidLevyRows.getMaps());
								throw (RuntimeException) e;
							}
						
						 }
						 /* 완료되면 TRN_YN 9인 것을1로 변경*/
						 gblNidLevyRows.setMap("TRN_YN"  , "1" );                
						 /*구청에 업데이트*/
						 remote_up_cnt += govService.queryForUpdate("txdm1141.txdm3141_update_done", gblNidLevyRows);
					}
				}
					
				log.info("과오납 자료연계 건수 [" + rec_cnt + "] 등록건수 [" + insert_cnt +"] 수정건수 [" + update_cnt + "] 삭제건수 [" + delete_cnt + "] 물리적삭제 [" + p_del_cnt + "]");
				log.info("trn_yn 업데이트 ["+remote_up_cnt+"], 미처리 부과건수 [" + nul_cnt + "]");
	
				elapseTime2 = System.currentTimeMillis();
				
				log.info("과오납 연계자료 연계 시간("+tot_size+") : " + CbUtil.formatTime(elapseTime2 - elapseTime1));
						
			} catch (Exception e) {
				e.printStackTrace();
			    log.error("오류 데이터 = " + gblNidLevyRows.getMaps());
				throw (RuntimeException) e;
			}
			
			
			
		}else if(jb.equals("2")){   /* 2. 지방세 수납요약정보 연계 시작 */
			
			log.info("=====================================================================");
			log.info("=" + this.getClass().getName()+ " txdm1141_JobProcess()[지방세수납요약정보-[" + c_slf_org_nm + "] 자료전송] Start =");
			log.info("= govid["+this.govId+"], orgcd["+this.c_slf_org+"]");
			log.info("=====================================================================");
			
			/*초기화*/
			rcp_cnt = 0;
			remote_up_cnt = 0;
			delete_cnt = 0;
			update_cnt = 0;
		
			/*전역 초기화*/
			rcpListRow = new MapForm();

			int nul_cnt = 0;
			int cnt = 0;
			tot_size = 0;
			
			long elapseTime1 = 0;
			long elapseTime2 = 0;
			
			elapseTime1 = System.currentTimeMillis();

			/*---------------------------------------------------------*/
			/*1. 지방세수납요약정보 업무 처리.                               */
			/*---------------------------------------------------------*/
			
			try {

				dataMap.setMap("SGG_COD",  this.c_slf_org);

				/* 실시간 수납/취소 SELECT 쿼리 */
				ArrayList<MapForm> rcpList =  govService.queryForList("txdm1141.SELECT_LIST", dataMap);
				
				tot_size = rcpList.size();
				
			    log.info("[" + c_slf_org_nm + "]지방세수납요약정보 건수 = [" + tot_size + "]");
				
				if (tot_size  >  0)   {
					
					/* 실시간 수납/취소 자료를 해당 구청에 fetch */
					for (cnt = 0;  cnt < tot_size;  cnt++) {

						rcpListRow =  rcpList.get(cnt);
						
						/*혹시나...because of testing */
						if (rcpListRow == null  ||  rcpListRow.isEmpty()) {
							nul_cnt++;
							continue;
						}
						
						if(rcpListRow.getMap("CUD_OPT").equals("3")){
							/*
							try {
								
								rcpListRow.setMap("DEL_YN", "N");
								
								cyberService.queryForUpdate("TXDM1151.TXSV1151_UPDATE_TX1102_TB", rcpListRow);
								
							} catch (Exception e) {
								
								log.error("TX1102_TB 에러 = " + rcpListRow.getMaps());
								log.error(e.getMessage());
								
							}
							*/
							
							try {

								cyberService.queryForUpdate("txdm1141.TXSV1151_DELETE_TX1211_TB", rcpListRow);
								
								delete_cnt++;
								
							} catch (Exception e) {
								
								log.error("오류데이터 = " + rcpListRow.getMaps());
								e.printStackTrace();							
								throw (RuntimeException) e;
									
							}
							
						} else if(rcpListRow.getMap("CUD_OPT").equals("2")){
							
							int updateCnt = 0;
							/*
							try {
								
								rcpListRow.setMap("DEL_YN", "Y");
								
								cyberService.queryForUpdate("TXDM1151.TXSV1151_UPDATE_TX1102_TB", rcpListRow);
								
							} catch (Exception e) {
								
								log.error("TX1102_TB 에러 = " + rcpListRow.getMaps());
								log.error(e.getMessage());
								
							}	
							*/					
							
							try {						
								
								updateCnt = cyberService.queryForUpdate("txdm1141.TXSV1151_UPDATE_TX1211_TB", rcpListRow);
								
								update_cnt++;
								
							} catch (Exception e) {
								
								log.error("오류데이터 = " + rcpListRow.getMaps());
								e.printStackTrace();							
								throw (RuntimeException) e;
								
							}
							
							if(updateCnt == 0) {
								
								try {
									
									cyberService.queryForUpdate("txdm1141.TXSV1151_INSERT_TX1211_TB", rcpListRow);
									
									//update_cnt++;
									
								} catch (Exception e) {
									
									log.error("오류데이터 = " + rcpListRow.getMaps());
									e.printStackTrace();							
									throw (RuntimeException) e;
									
								}
								
							}
							
						} else if(rcpListRow.getMap("CUD_OPT").equals("1")){
							
							/*
							try {
								
								rcpListRow.setMap("DEL_YN", "Y");
								
								cyberService.queryForUpdate("TXDM1151.TXSV1151_UPDATE_TX1102_TB", rcpListRow);
								
							} catch (Exception e) {
								
								log.error("TX1102_TB 에러 = " + rcpListRow.getMaps());
								log.error(e.getMessage());
								
							}
							*/
							
							try {
								
								cyberService.queryForUpdate("txdm1141.TXSV1151_INSERT_TX1211_TB", rcpListRow);
								
								rcp_cnt++;
								
							} catch (Exception e) {
								
								/*중복이 발생하거나 제약조건에 어긋나는 경우체크*/
								if (e instanceof DuplicateKeyException){
									
									try {
										
										cyberService.queryForUpdate("txdm1141.TXSV1151_UPDATE_TX1211_TB", rcpListRow);
										    
									} catch (Exception sub_e) {
										log.error("오류데이터 = " + rcpListRow.getMaps());
										e.printStackTrace();
										throw (RuntimeException) sub_e;
									}
									
								} else {
									
									log.error("오류데이터 = " + rcpListRow.getMaps());
									e.printStackTrace();							
									throw (RuntimeException) e;
									
								}
								
							}
							
						}
						
						/*=========== UPDATE ===========*/
						try {
							
							/* 지방세수납요약정보 완료 업데이트 */
							remote_up_cnt += govService.queryForUpdate("txdm1141.TXSV3151_UPDATE_DONE", rcpListRow);
							
						} catch (Exception e) {
							
							log.error("오류데이터 = " + rcpListRow.getMaps());
							e.printStackTrace();							
							throw (RuntimeException) e;
								
						}

					}
					
					log.info("[" + c_slf_org_nm + "]지방세수납요약정보 [" + cnt + "] 신규 [" + rcp_cnt +"] 변경 [" + update_cnt + "] 삭제 [" + delete_cnt + "]");
					log.info("처리건수 ["+remote_up_cnt+"], 미처리 부과건수 [" + nul_cnt + "]");
					
					/***********************************************************************************/
					try{
						MapForm daemonMap = new MapForm();
						daemonMap.setMap("DAEMON"    , "TXDM1141");
						daemonMap.setMap("DAEMON_NM" , "수납(SCON540)정보연계");
						daemonMap.setMap("SGG_COD"   , c_slf_org);
						daemonMap.setMap("TOTAL_CNT" , cnt);
						daemonMap.setMap("INSERT_CNT", rcp_cnt);
						daemonMap.setMap("UPDATE_CNT", update_cnt);
						daemonMap.setMap("DELETE_CNT", delete_cnt);
						daemonMap.setMap("ERROR_CNT" , 0);
						cyberService.queryForInsert("CODMBASE.InsertDaemonProcessCheck", daemonMap);
					}catch(Exception ex){
						log.debug("지방세 수납요약정보 로그 등록 오류");
					}				
					/***********************************************************************************/
					
				}

				elapseTime2 = System.currentTimeMillis();
				
				log.info("[" + c_slf_org_nm + "]지방세수납요약정보 전송 시간("+tot_size+") : " + CbUtil.formatTime(elapseTime2 - elapseTime1));
						
			} catch (Exception e) {
				
			    log.error("오류 데이터 = " + rcpListRow.getMaps());
			    e.printStackTrace();
				throw (RuntimeException) e;
				
			}
			
			
		}
		
		return tot_size;
		
	}
	

}
