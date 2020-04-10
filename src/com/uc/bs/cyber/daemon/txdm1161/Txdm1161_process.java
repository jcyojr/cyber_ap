/**
 *  주시스템명 : 부산시청 사이버 지방세청
 *  업  무  명 : 연계
 *  기  능  명 : 실시간수납자료(납부,취소) 통합납부
 *  클래스  ID : Txdm1161_process : 실 처리로직을 작성하는 클래스
 *  변경  이력 :
 * ------------------------------------------------------------------------
 *  작성자      	소속  		    일자            Tag           내용
 * ------------------------------------------------------------------------
 *  황종훈       유채널(주)      2012.03.08         %01%         최초작성
 */
package com.uc.bs.cyber.daemon.txdm1161;

import java.util.ArrayList;

import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;

import com.uc.bs.cyber.CbUtil;
import com.uc.bs.cyber.daemon.Codm_BaseProcess;
import com.uc.bs.cyber.daemon.Codm_interface;
import com.uc.core.MapForm;
import com.uc.core.spring.service.UcContextHolder;

public class Txdm1161_process extends Codm_BaseProcess implements Codm_interface {

	private MapForm dataMap = null;
	private int rcp_cnt = 0, rcp_cancel_cnt = 0, remote_up_cnt = 0;
	
	/*트랜잭션을 위하여 */
	MapForm dataListRow = null;
	
	/**
	 * 
	 */
	public Txdm1161_process() {
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
		log.info("=[실시간자료(납부,취소) 통합납부-[" + c_slf_org_nm + "] 자료송신] Start =");
		log.info("=====================================================================");
		try {
				
			setContext(appContext);
			setApp(appContext);

			UcContextHolder.setCustomerType(this.dataSource);
						
			dataMap = new MapForm();  /*초기화*/
			
			log.info("this.c_slf_org ======================= [" + this.c_slf_org + "]");

			dataMap.setMap("SGG_COD",  this.c_slf_org);
			
			do {
				
				int rcpCnt = govService.getOneFieldInteger("TXDM1161.SELECT_COUNT", dataMap);
				
				log.info("[실시간자료(납부,취소) 통합납부-[" + c_slf_org_nm + "]] 건수(" + rcpCnt + ")");
				
				if(rcpCnt == 0){
				    
					/***********************************************************************************/
					try{
						MapForm daemonMap = new MapForm();
						daemonMap.setMap("DAEMON"    , "TXDM1161");
						daemonMap.setMap("DAEMON_NM" , "통합(SCON745)납부취소연계");
						daemonMap.setMap("SGG_COD"   , c_slf_org);
						daemonMap.setMap("TOTAL_CNT" , rcpCnt);
						daemonMap.setMap("INSERT_CNT", 0);
						daemonMap.setMap("UPDATE_CNT", 0);
						daemonMap.setMap("DELETE_CNT", 0);
						daemonMap.setMap("ERROR_CNT" , 0);
						cyberService.queryForInsert("CODMBASE.InsertDaemonProcessCheck", daemonMap);
					}catch(Exception ex){
						log.debug("지방세 통합납부취소연계 건수가 없습니다. 등록 오류");
					}				
					/***********************************************************************************/
					
					break;
				
				} else {
					this.startJob();
					break;
				}
				
				//if(govService.getOneFieldInteger("TXDM1161.SELECT_COUNT", dataMap) == 0) break;
				
			} while(true);
					
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
			
		txdm1161_JobProcess();
	
	}

	private int txdm1161_JobProcess() {
		
		log.info("=====================================================================");
		log.info("=" + this.getClass().getName()+ " txdm1161_JobProcess()[실시간자료(납부,취소) 통합납부-[" + c_slf_org_nm + "] 자료전송] Start =");
		log.info("= govid["+this.govId+"], orgcd["+this.c_slf_org+"]");
		log.info("=====================================================================");
		
		/*초기화*/
		rcp_cancel_cnt = 0;
		rcp_cnt = 0;
		remote_up_cnt = 0;
	
		
		/*전역 초기화*/
		dataListRow = new MapForm();

		int nul_cnt = 0;
		int cnt = 0;
		int tot_size = 0;
		
		long elapseTime1 = 0;
		long elapseTime2 = 0;
		
		elapseTime1 = System.currentTimeMillis();

		/*---------------------------------------------------------*/
		/*1. 실시간수납 통합납부 업무 처리.                        */
		/*---------------------------------------------------------*/
		
		try {

			dataMap.setMap("SGG_COD",  this.c_slf_org);

			/* 실시간 수납/취소 SELECT 쿼리 */
			ArrayList<MapForm> AccList =  govService.queryForList("TXDM1161.SELECT_LIST", dataMap);
			
			tot_size = AccList.size();
			
		    log.info("[" + c_slf_org_nm + "]실시간자료(납부,취소) 통합납부 자료 건수 = [" + tot_size + "]");
			
			if (tot_size  >  0)   {
				
				/* 실시간 수납/취소 자료를 해당 구청에 fetch */
				for (cnt = 0;  cnt < tot_size;  cnt++) {

					dataListRow =  AccList.get(cnt);
					
					/*혹시나...because of testing */
					if (dataListRow == null  ||  dataListRow.isEmpty() )   {
						nul_cnt++;
						continue;
					}
					
					if(dataListRow.getMap("GBN").equals("1")) {
						
						MapForm rcpMap = new MapForm();
					
						rcpMap = dataListRow;
						
			            rcpMap.setMap("SGG_COD", rcpMap.getMap("TAX_NO").toString().substring(2, 3 + 2));
			            rcpMap.setMap("ACCT_COD", rcpMap.getMap("TAX_NO").toString().substring(6, 2 + 6));
			            rcpMap.setMap("TAX_ITEM", rcpMap.getMap("TAX_NO").toString().substring(8, 6 + 8));
			            rcpMap.setMap("TAX_YY", rcpMap.getMap("TAX_NO").toString().substring(14, 4 + 14));
			            rcpMap.setMap("TAX_MM", rcpMap.getMap("TAX_NO").toString().substring(18, 2 + 18));
			            rcpMap.setMap("TAX_DIV", rcpMap.getMap("TAX_NO").toString().substring(20, 1 + 20));
			            rcpMap.setMap("HACD", rcpMap.getMap("TAX_NO").toString().substring(21, 3 + 21));
			            rcpMap.setMap("TAX_SNO", rcpMap.getMap("TAX_NO").toString().substring(24, 6 + 24));
			            
						try {
							
							ArrayList<MapForm> WeList;
							
							if(dataMap.getMap("SGG_COD").equals("000")) WeList = govService.queryForList("TXDM1161.TXSV3161_SELECT_ALL_000", dataListRow);
							else WeList = govService.queryForList("TXDM1161.TXSV3161_SELECT_ALL", dataListRow); 
							
							rcpMap.setMap("RCP_CNT", 0);
							rcpMap.setMap("OCRBD", " ");
							
							if(WeList.size() == 0) {
								
								/* 부과테이블 SCON602, SCON743에 자료없으면 '8' 로 업데이트하고 처리안함 */
								try {
									
									rcpMap.setMap("TRN_YN", "8");
									
									govService.queryForUpdate("TXDM1161.TXSV3161_UPDATE_DONE", rcpMap);
									
								} catch (Exception e) {
									
									log.error("오류데이터 = " + dataListRow.getMaps());
									e.printStackTrace();
									continue;
										
								}	
								
								continue;
								
							} else {
							
								/* 분납순번이랑, 밴드 가져오기 */
								for(int t_rcp_cnt = 0; t_rcp_cnt < WeList.size(); t_rcp_cnt++){
									
									rcpMap.setMap("RCP_CNT", WeList.get(t_rcp_cnt).getMap("RCP_CNT"));
									rcpMap.setMap("OCRBD", WeList.get(t_rcp_cnt).getMap("OCRBD"));
									
									/*
									log.info("RCP_CNT = [" + rcpMap.getMap("RCP_CNT")+ "]");
									log.info("RCP_CNT = [" + WeList.get(t_rcp_cnt).getMap("RCP_CNT") + "]");
									log.info("OCRBD = [" + rcpMap.getMap("OCRBD")+ "]");
									log.info("OCRBD = [" + WeList.get(t_rcp_cnt).getMap("OCRBD") + "]");
									*/
								}
							}
							
						} catch (Exception e) {
							
							log.error("오류데이터 = " + dataListRow.getMaps());
							e.printStackTrace();
							throw (RuntimeException) e;
								
						}	
						
						/* ==== 수납 처리 시작 === */
						/* 지방소득세 특별징수 테이블 변경 */
						if(rcpMap.getMap("TAX_ITEM").equals("140004")) {
							
							try {
								
								cyberService.queryForUpdate("TXDM1161.INSERT_140004_RECEIPT", rcpMap);
								
							} catch (Exception e) {
								
								log.error("오류데이터 = " + rcpMap.getMaps());
								e.printStackTrace();							
								throw (RuntimeException) e;
									
							}
							
						} else if(rcpMap.getMap("TAX_ITEM").equals("140001")) {
							/* 지방소득세 종합소득 테이블 변경 */
							try {
								
								cyberService.queryForUpdate("TXDM1161.INSERT_140001_RECEIPT", rcpMap);
								
							} catch (Exception e) {
								
								log.error("오류데이터 = " + rcpMap.getMaps());
								e.printStackTrace();							
								throw (RuntimeException) e;
									
							}
							
						} else if(rcpMap.getMap("TAX_ITEM").equals("140002")) {
							/* 지방소득세 양도소득 테이블 변경 */
							try {
								
								cyberService.queryForUpdate("TXDM1161.INSERT_140002_RECEIPT", rcpMap);
								
							} catch (Exception e) {
								
								log.error("오류데이터 = " + rcpMap.getMaps());
								e.printStackTrace();							
								throw (RuntimeException) e;
									
							}	
							
						} else if(rcpMap.getMap("TAX_ITEM").equals("140003")) {
							/* 지방소득세 법인세분 테이블 변경 */
							try {

                                //2015년 부터 기존신고화면으로 신고를 안한다면, 기존 테이블로 업데이트 할 필요는 없을 듯..
                                cyberService.queryForUpdate("TXDM1161.INSERT_140003_RECEIPT", rcpMap);
                                
                                //2015.03.24 신규테이블 변경 적용
                                int corpTaxRecpUpdCnt = cyberService.queryForUpdate("TXDM1161.INSERT_140003_RECEIPT_NEW", rcpMap);
                                if (corpTaxRecpUpdCnt > 0) {
                                    log.info("[법인세신고내역수납] EPAY_NO = [" + rcpMap.getMap("EPAY_NO")+ "]");
                                }
								
							} catch (Exception e) {
								
								log.error("오류데이터 = " + rcpMap.getMaps());
								e.printStackTrace();							
								throw (RuntimeException) e;
									
							}
							
						} else if(rcpMap.getMap("TAX_ITEM").equals("140011")) {
							/* 지방소득세 종업원분 테이블 변경 */
							try {
								
								cyberService.queryForUpdate("TXDM1161.INSERT_140011_RECEIPT", rcpMap);
								
							} catch (Exception e) {
								
								log.error("오류데이터 = " + rcpMap.getMaps());
								e.printStackTrace();							
								throw (RuntimeException) e;
									
							}
							
						} else if(rcpMap.getMap("TAX_ITEM").equals("104009")) {
							/* 지방소득세 재산분 테이블 변경 */
							try {
								
								cyberService.queryForUpdate("TXDM1161.INSERT_104009_RECEIPT", rcpMap);
								
							} catch (Exception e) {
								
								log.error("오류데이터 = " + rcpMap.getMaps());
								e.printStackTrace();							
								throw (RuntimeException) e;
									
							}
							
						} else if(rcpMap.getMap("TAX_ITEM").equals("114002")) {
							/* 지방소득세 등록면허세 등록분 테이블 변경 */
							try {
								
								cyberService.queryForUpdate("TXDM1161.INSERT_114002_RECEIPT", rcpMap);
								
							} catch (Exception e) {
								
								log.error("오류데이터 = " + rcpMap.getMaps());
								e.printStackTrace();							
								throw (RuntimeException) e;
									
							}
							
						} else if(rcpMap.getMap("TAX_ITEM").equals("109000")) {
							/* 지방소득세 레저세 테이블 변경 */
							try {
								
								cyberService.queryForUpdate("TXDM1161.INSERT_109000_RECEIPT", rcpMap);
								
							} catch (Exception e) {
								
								log.error("오류데이터 = " + rcpMap.getMaps());
								e.printStackTrace();							
								throw (RuntimeException) e;
									
							}
							
						} else if(rcpMap.getMap("TAX_ITEM").equals("135001")) {
							/* 지역개발세 특자 테이블 변경 */
							try {
								
								cyberService.queryForUpdate("TXDM1161.INSERT_135001_RECEIPT", rcpMap);
								
							} catch (Exception e) {
								
								log.error("오류데이터 = " + rcpMap.getMaps());
								e.printStackTrace();							
								throw (RuntimeException) e;
									
							}
							
						}
						
						try {
							
							cyberService.queryForUpdate("TXDM1161.TX1201_TB_INSERT_RECEIPT", rcpMap);
							
							rcp_cnt++;
							
						} catch (Exception e) {
							
							e.printStackTrace();
							
							try {
								
								rcpMap.setMap("TRN_YN", "1");
								
								govService.queryForUpdate("TXDM1161.TXSV3161_UPDATE_DONE", rcpMap);
								
								nul_cnt++;
								
							} catch (Exception e_sub) {
								
								log.error("오류데이터 = " + rcpMap.getMaps());
								e_sub.printStackTrace();							
								throw (RuntimeException) e_sub;
									
							}
							
							continue;
							//throw (RuntimeException) e;
								
						}
						
						try {
							
							cyberService.queryForUpdate("TXDM1161.TX1102_TB_UPDATE_STATE", rcpMap);
							
						} catch (Exception e) {
							
							log.error("오류데이터 = " + rcpMap.getMaps());
							e.printStackTrace();							
							throw (RuntimeException) e;
								
						}	
						
						try {
							
							rcpMap.setMap("TRN_YN", "1");
							
							govService.queryForUpdate("TXDM1161.TXSV3161_UPDATE_DONE", rcpMap);
							
						} catch (Exception e) {
							
							log.error("오류데이터 = " + rcpMap.getMaps());
							e.printStackTrace();							
							throw (RuntimeException) e;
								
						}
						
					} else {
						
						/* 취소 부분인데 안하네?? */
						
					}

				}
				
				log.info("[" + c_slf_org_nm + "]실시간자료(납부,취소) 통합납부 건수 [" + cnt + "] 실시간수납 [" + rcp_cnt +"] 취소건수 [" + rcp_cancel_cnt + "]");
				log.info("처리건수 ["+remote_up_cnt+"], 미처리 부과건수 [" + nul_cnt + "]");
				
				/***********************************************************************************/
				try{
					MapForm daemonMap = new MapForm();
					daemonMap.setMap("DAEMON"    , "TXDM1161");
					daemonMap.setMap("DAEMON_NM" , "통합(SCON745)납부취소연계");
					daemonMap.setMap("SGG_COD"   , c_slf_org);
					daemonMap.setMap("TOTAL_CNT" , cnt);
					daemonMap.setMap("INSERT_CNT", rcp_cnt);
					daemonMap.setMap("UPDATE_CNT", rcp_cancel_cnt);
					daemonMap.setMap("DELETE_CNT", 0);
					daemonMap.setMap("ERROR_CNT" , 0);
					cyberService.queryForInsert("CODMBASE.InsertDaemonProcessCheck", daemonMap);
				}catch(Exception ex){
					log.debug("지방세 통합납부취소연계 등록 오류");
				}				
				/***********************************************************************************/
				
			}

			elapseTime2 = System.currentTimeMillis();
			
			log.info("[" + c_slf_org_nm + "]실시간자료(납부,취소) 통합납부 전송 시간("+tot_size+") : " + CbUtil.formatTime(elapseTime2 - elapseTime1));
					
		} catch (Exception e) {
			
		    log.error("오류 데이터 = " + dataListRow.getMaps());
			throw (RuntimeException) e;
			
		}
		
		return tot_size;
	}
	
}
