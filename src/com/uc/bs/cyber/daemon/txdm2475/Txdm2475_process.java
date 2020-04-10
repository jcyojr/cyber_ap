/**
 *  주시스템명 : 부산시청 사이버 지방세청
 *  업  무  명 : 연계
 *  기  능  명 : 주정차위반과태료 부과자료연계
 *  클래스  ID : Txdm2475_process : 실 처리로직을 작성하는 클래스
 *  변경  이력 :
 * ------------------------------------------------------------------------
 *  작성자      	소속  		    일자            Tag           내용
 * ------------------------------------------------------------------------
 *  송동욱       유채널(주)      2011.05.11         %01%         최초작성
 */
package com.uc.bs.cyber.daemon.txdm2475;

import java.math.BigDecimal;
import java.util.ArrayList;

import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;

import com.uc.bs.cyber.CbUtil;
import com.uc.bs.cyber.daemon.Codm_BaseProcess;
import com.uc.bs.cyber.daemon.Codm_interface;
import com.uc.core.MapForm;
import com.uc.core.spring.service.UcContextHolder;

public class Txdm2475_process extends Codm_BaseProcess implements Codm_interface {

	private MapForm dataMap            = null;
	private int insert_cnt = 0, update_cnt = 0, delete_cnt = 0, remote_up_cnt = 0;
    private int p_del_cnt = 0;
	
	/*트랜잭션을 위하여 */
	MapForm gblNidLevyRows    = null;
	
	/**
	 * 
	 */
	public Txdm2475_process() {
		// TODO Auto-generated constructor stub
		super();
		
		log = LogFactory.getLog(this.getClass());
		
		/**
		 * 3 분마다 돈다
		 */
		loopTerm = 180;
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
	private void mainTransProcess() {

		log.info("=====================================================================");
		log.info("=[주정차위반과태료-[" + c_slf_org_nm + "] 부과자료연계] Start =");
		log.info("=====================================================================");

		try {

			setContext(appContext);
			setApp(appContext);

			UcContextHolder.setCustomerType(this.dataSource);

			dataMap = new MapForm(); /* 초기화 */

			log.info("this.c_slf_org ======================= ["+ this.c_slf_org +"]");

			dataMap.setMap("SGG_COD", this.c_slf_org);
			dataMap.setMap("BS_NOT_IN_SEQ", not_in_seq());

			int CNT = 0;
			try {
				CNT = govService.getOneFieldInteger("TXDM2475.SELECT_ROAD_CNT",	dataMap);
			} catch (Exception sub_e) {
				log.error("[" + this.c_slf_org + "] SELECT_ROAD_CNT 오류");
				CNT = -1;
				sub_e.printStackTrace();
			}

			if (CNT > 0) {
				try {
					govService.queryForUpdate("TXDM2475.UPDATE_ROAD_START",	dataMap);
				} catch (Exception sub_e) {
					log.error("[" + this.c_slf_org + "] UPDATE_ROAD_START 오류");
					sub_e.printStackTrace();
				}

				this.startJob();

				try {
					govService.queryForUpdate("TXDM2475.UPDATE_ROAD_END", dataMap);
				} catch (Exception sub_e) {
					log.error("[" + this.c_slf_org + "] UPDATE_ROAD_END 오류");
					sub_e.printStackTrace();
				}
			
			}else if(CNT == 0){
				
				/********************************* 구청별 처리 로그 등록 **************************************************/
				try{
					MapForm daemonMap = new MapForm();
					daemonMap.setMap("DAEMON"    , "TXDM2475");
					daemonMap.setMap("DAEMON_NM" , "주정차위반과태료 연계");
					daemonMap.setMap("SGG_COD"   , c_slf_org);
					daemonMap.setMap("TOTAL_CNT" , CNT);
					daemonMap.setMap("INSERT_CNT", 0);
					daemonMap.setMap("UPDATE_CNT", 0);
					daemonMap.setMap("DELETE_CNT", 0);
					daemonMap.setMap("ERROR_CNT" , 0);
					cyberService.queryForInsert("CODMBASE.InsertDaemonProcessCheck", daemonMap);
				}catch(Exception ex){
					log.info("구청별 처리 로그 등록 오류");
				}				
				/**********************************************************************************************************/
			
			}else{
				log.info("[" + c_slf_org_nm + "]주정차위반과태료 연계자료 조회 오류 !!!!!!!!!!!!!");
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
		txdm2475_JobProcess();
	
	}
	
    /*주정차위반과태료...연계*/
	private int txdm2475_JobProcess() {
		
		log.info("=====================================================================");
		log.info("=" + this.getClass().getName()+ " txdm2475_JobProcess()[주정차위반과태료-[" + c_slf_org_nm + "] 부과자료연계] Start =");
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
		int tot_size = 0;		
		long elapseTime1 = 0;
		long elapseTime2 = 0;
		
		elapseTime1 = System.currentTimeMillis();

		/*---------------------------------------------------------*/
		/*1. 부가연계자료 업무 처리.                               */
		/*---------------------------------------------------------*/
		log.info("------try문 들어가기 전 -----------");
		try {

			/*연계테이블 부과자료 SELECT 쿼리(NIS)*/
			ArrayList<MapForm> alNidLevyList = null;

			try {
				alNidLevyList = govService.queryForList("TXDM2475.SELECT_ROAD_LIST", dataMap);
			} catch (Exception sub_e) {
				log.error("[" + this.c_slf_org + "] SELECT_ROAD_LIST 오류데이터 = " + gblNidLevyRows.getMaps());
				sub_e.printStackTrace();
			}	
			
			try{				
				cyberService.queryForUpdate("TXDM2475.DEL_EMPTY_DONE", dataMap);
				log.debug("[" + this.c_slf_org + "]  DEL_EMPTY_DONE = " + alNidLevyList );
			} catch (Exception sub_e) {
				sub_e.printStackTrace();
			}

			tot_size = alNidLevyList.size();
			
		    log.info("[" + c_slf_org_nm + "]주정차위반과태료 연계자료 건수 = [" + tot_size + "]");
			
			if (tot_size > 0) {
				
				/*전자납부자료 1건씩 fetch 처리 */
				for ( rec_cnt = 0;  rec_cnt < tot_size;  rec_cnt++) {
					
					gblNidLevyRows =  alNidLevyList.get(rec_cnt);
					
					/*혹시나...because of testing */
					if (gblNidLevyRows == null || gblNidLevyRows.isEmpty()) {
						nul_cnt++;
						continue;
					}
					
				    String TRN_YN = "1";
				    String WETAX_YN = (String) gblNidLevyRows.getMap("WETAX_YN");
				    String THAP_GBN = (String) gblNidLevyRows.getMap("THAP_GBN");
				    String DIVIDED_PAYMENT_SEQNUM = (String) gblNidLevyRows.getMap("DIVIDED_PAYMENT_SEQNUM");
				    
					long SIGUNGU_TAX = ((BigDecimal) gblNidLevyRows.getMap("SIGUNGU_TAX")).longValue();
					long PAYMENT_DATE1 = ((BigDecimal) gblNidLevyRows.getMap("PAYMENT_DATE1")).longValue();
					long AFTPAYMENT_DATE1 = ((BigDecimal) gblNidLevyRows.getMap("AFTPAYMENT_DATE1")).longValue();
			        
					// 연계되면 안되는 자료
					if (TRN_YN.equals("1") && !THAP_GBN.equals("02")) {
						TRN_YN = "X";
					}
					if (TRN_YN.equals("1") && !(DIVIDED_PAYMENT_SEQNUM.equals("00") || DIVIDED_PAYMENT_SEQNUM.equals("98"))) {
						TRN_YN = "X";
					}
					if (TRN_YN.equals("1") && (SIGUNGU_TAX % 10L > 0L || PAYMENT_DATE1 % 10L > 0L || AFTPAYMENT_DATE1 % 10L > 0L)) {
						TRN_YN = "E";
					}

					// 신규, 수정, 삭제 구분 조건
					String V_DEL_YN="N";
				    String DPOSL_STAT = (String) gblNidLevyRows.getMap("DPOSL_STAT");
				    String BNON_SNTG = (String) gblNidLevyRows.getMap("BNON_SNTG");
				    String CUD_OPT = (String) gblNidLevyRows.getMap("CUD_OPT");
				    String EPAY_NO = (String) gblNidLevyRows.getMap("EPAY_NO");
				    String DUE_DT = (String) gblNidLevyRows.getMap("DUE_DT");
				    String REG_NO = (String) gblNidLevyRows.getMap("REG_NO");
				    
				    if (TRN_YN.equals("1") 
				    		&& (WETAX_YN.equals("N")) 
				    		&& (DPOSL_STAT.equals("2") 
				    				|| BNON_SNTG.equals("D") 
				    				|| BNON_SNTG.equals("S") 
				    				|| CUD_OPT.equals("3") 
				    				|| SIGUNGU_TAX == 0L 
				    				|| (PAYMENT_DATE1 + AFTPAYMENT_DATE1) == 0L)) {
						V_DEL_YN = "Y";
					
						try {
							if (cyberService.getOneFieldInteger("TXDM2475.SELECT_TX2112_DEL_YN_EPAY_NO_CNT", gblNidLevyRows) > 0) {
								if (cyberService.queryForUpdate("TXDM2475.UPDATE_TX2112_DEL_YN_EPAY_NO", gblNidLevyRows) > 0) {
									delete_cnt++;
								}
							}
						} catch (Exception sub_e) {
							TRN_YN = "E";
							log.error("[" + this.c_slf_org + "] UPDATE_TX2112_DEL_YN_EPAY_NO 오류 = " + gblNidLevyRows.getMaps());
							sub_e.printStackTrace();
						}
					    
						gblNidLevyRows.setMap("TRN_YN",TRN_YN);				    
				    
				    }
				    
				    

					
					if (TRN_YN.equals("1")
							&& WETAX_YN.equals("N")
							&& V_DEL_YN.equals("N")
							&& (EPAY_NO.equals(" ")
									|| EPAY_NO.equals("0000000000000000000")
									|| DUE_DT.equals(" ") || REG_NO.equals(" "))) {
						TRN_YN = "E";
					}
					
					if (TRN_YN.equals("1")) {
						gblNidLevyRows.setMap("PROC_CLS", "1");
						gblNidLevyRows.setMap("VIR_ACC_NO", "");
						gblNidLevyRows.setMap("SGG_TR_TG", "0");
						gblNidLevyRows.setMap("DEL_YN", V_DEL_YN);
						if (WETAX_YN.equals("Y")) {
							gblNidLevyRows.setMap("DEL_YN", "Y");
							gblNidLevyRows.setMap("SNTG", "2");
						}

						String V_CUD = "";

						if (WETAX_YN.equals("Y")) {
							int TX2211_CNT_TAX_NO_WETAX = cyberService.getOneFieldInteger("TXDM2475.SELECT_TX2211_CNT_TAX_NO_WETAX", gblNidLevyRows);
							if (TX2211_CNT_TAX_NO_WETAX == 0) {
								int TX2211_MAX_PAY_CNT = cyberService.getOneFieldInteger("TXDM2475.SELECT_TX2211_MAX_PAY_CNT", gblNidLevyRows);
								gblNidLevyRows.setMap("PAY_CNT",TX2211_MAX_PAY_CNT);
								String TX2112_OCR_BD = cyberService.getOneFieldString("TXDM2475.SELECT_TX2112_OCR_BD", gblNidLevyRows);
								gblNidLevyRows.setMap("OCR_BD", TX2112_OCR_BD);
								try {
									cyberService.queryForUpdate("TXDM2475.INSERT_TX2211", gblNidLevyRows);
								} catch (Exception sub_e) {
									TRN_YN = "E";
									log.error("[" + this.c_slf_org + "] INSERT_TX2211 오류 = " + gblNidLevyRows.getMaps());
									sub_e.printStackTrace();
								}

								if (TRN_YN.equals("1")) {
									try {
										cyberService.queryForUpdate("TXDM2475.UPDATE_TX2112_DEL_YN_WETAX", gblNidLevyRows);
									} catch (Exception sub_e) {
										TRN_YN = "E";
										log.error("[" + this.c_slf_org + "] UPDATE_TX2112_DEL_YN_WETAX 오류 = " + gblNidLevyRows.getMaps());
										sub_e.printStackTrace();
									}
								}
							}
						} else {
							if (WETAX_YN.equals("C")){
								if (cyberService.getOneFieldInteger("TXDM2475.SELECT_TX2211_CNT_TAX_NO_EPAY_NO", gblNidLevyRows)>0){
									gblNidLevyRows.setMap("MAX_PAY_CNT_EPAY_NO", cyberService.getOneFieldInteger("TXDM2475.SELECT_TX2211_MAX_PAY_CNT_EPAY_NO", gblNidLevyRows));

									try {
										cyberService.queryForUpdate("TXDM2475.UPDATE_TX2211_SNTG", gblNidLevyRows);
									} catch (Exception sub_e) {
										TRN_YN = "E";
										log.error("[" + this.c_slf_org + "] UPDATE_TX2211_SNTG 오류 = " + gblNidLevyRows.getMaps());
										sub_e.printStackTrace();
									}

									if (cyberService.getOneFieldInteger("TXDM2475.SELECT_TX2211_CNT_TAX_NO", gblNidLevyRows)==0){
										try {
											cyberService.queryForUpdate("TXDM2475.UPDATE_TX2112_SNTG", gblNidLevyRows);
										} catch (Exception sub_e) {
											TRN_YN = "E";
											log.error("[" + this.c_slf_org + "] UPDATE_TX2112_SNTG 오류 = " + gblNidLevyRows.getMaps());
											sub_e.printStackTrace();
										}
									}
								} 
							}
							
							int TX2111_CNT_TAX_NO = cyberService.getOneFieldInteger("TXDM2475.SELECT_TX2111_CNT_TAX_NO", gblNidLevyRows);
							int TX2211_CNT_TAX_NO = cyberService.getOneFieldInteger("TXDM2475.SELECT_TX2211_CNT_TAX_NO", gblNidLevyRows);

							if (TX2111_CNT_TAX_NO == 0) {
								if (V_DEL_YN.equals("Y")) {
									if (cyberService.getOneFieldInteger("TXDM2475.SELECT_TX2112_CNT_LEVY_DETAIL", gblNidLevyRows) > 0) {
										V_CUD = "L";
									}
								} else {
									V_CUD = "I";
								}
							} else {
								if (V_DEL_YN.equals("Y")) {
									if (TX2211_CNT_TAX_NO == 0) {
										V_CUD = "D";
									}
								} else {
									if (TX2211_CNT_TAX_NO == 0) {
										V_CUD = "U";
									} else {
										TRN_YN = "E";
									}
								}
							}

							if (V_CUD.equals("I")) {
								try {
									cyberService.queryForUpdate("TXDM2475.INSERT_TX2111", gblNidLevyRows);
								} catch (Exception sub_e) {
									TRN_YN = "E";
									log.error("[" + this.c_slf_org + "] INSERT_TX2111 오류 = " + gblNidLevyRows.getMaps());
									sub_e.printStackTrace();
								}

								try {
									cyberService.queryForUpdate("TXDM2475.INSERT_TX2112", gblNidLevyRows);
								} catch (Exception sub_e) {
									TRN_YN = "E";
									log.error("[" + this.c_slf_org + "] INSERT_TX2112 오류 = " + gblNidLevyRows.getMaps());
									sub_e.printStackTrace();
								}
								insert_cnt++;
							}

							if (V_CUD.equals("U")) {

								String vir_no = "";

								try {
									cyberService.queryForInsert("TXDM2475.INSERT_TX2115", gblNidLevyRows);
								} catch (Exception sub_e) {
									TRN_YN = "E";
									log.error("[" + this.c_slf_org + "] INSERT_TX2115 오류 = " + gblNidLevyRows.getMaps());
									sub_e.printStackTrace();
								}

								// 차량번호,적발일시 변경시 가상계좌번호 삭제후 채번하여 재전송
								try {
									vir_no = cyberService.getOneFieldString("TXDM2475.getVirAccNoByTaxNoKey", gblNidLevyRows);
								} catch (Exception sub_e) {
									TRN_YN = "E";
									log.error("getVirAccNoByTaxNoKey 오류 = " + gblNidLevyRows.getMaps());
									sub_e.printStackTrace();
								}
								gblNidLevyRows.setMap("VIR_NO", vir_no);

								try {
									cyberService.queryForUpdate("TXDM2475.UPDATE_TX2111_TAX_NO", gblNidLevyRows);
								} catch (Exception sub_e) {
									TRN_YN = "E";
									log.error("[" + this.c_slf_org + "] UPDATE_TX2111_TAX_NO 오류 = " + gblNidLevyRows.getMaps());
									sub_e.printStackTrace();
								}

								try {
									cyberService.queryForUpdate("TXDM2475.UPDATE_TX2112_TAX_NO", gblNidLevyRows);
								} catch (Exception sub_e) {
									TRN_YN = "E";
									log.error("[" + this.c_slf_org + "] UPDATE_TX2112_TAX_NO 오류 = " + gblNidLevyRows.getMaps());
									sub_e.printStackTrace();
								}
								update_cnt++;
							}

							if (V_CUD.equals("D")) {
								try {
									if (cyberService.queryForUpdate("TXDM2475.UPDATE_TX2112_DEL_YN", gblNidLevyRows) > 0) {
										delete_cnt++;
									}
								} catch (Exception sub_e) {
									TRN_YN = "E";
									log.error("[" + this.c_slf_org + "] UPDATE_TX2112_DEL_YN 오류 = " + gblNidLevyRows.getMaps());
									sub_e.printStackTrace();
								}
							}

							if (V_CUD.equals("L")) {
								try {
									if (cyberService.getOneFieldInteger("TXDM2475.SELECT_TX2112_DEL_YN_LEVY_DETAIL_CNT", gblNidLevyRows) > 0) {
										if (cyberService.queryForUpdate("TXDM2475.UPDATE_TX2112_DEL_YN_LEVY_DETAIL", gblNidLevyRows) > 0) {
											delete_cnt++;
										}
									}
								} catch (Exception sub_e) {
									TRN_YN = "E";
									log.error("[" + this.c_slf_org + "] UPDATE_TX2112_DEL_YN_LEVY_DETAIL 오류 = " + gblNidLevyRows.getMaps());
									sub_e.printStackTrace();
								}
							}
						}
					}

					gblNidLevyRows.setMap("TRN_YN",TRN_YN);
					
					try {
						/* 고지원장 업데이트 */
						remote_up_cnt += govService.queryForUpdate("TXDM2475.UPDATE_ROAD_TRN_YN", gblNidLevyRows);
					} catch (Exception sub_e) {
						log.error("[" + this.c_slf_org + "] UPDATE_ROAD_TRN_YN 오류 = " + gblNidLevyRows.getMaps());
						sub_e.printStackTrace();
					}
					
					if (rec_cnt % 500 == 0) {
						log.info("[" + this.c_slf_org_nm + "][" + this.c_slf_org + "]주정차위반과태료 자료처리중건수 = [" + rec_cnt + "]");
					}
				}
				
				log.info("[" + c_slf_org_nm + "]주정차위반과태료 부과자료연계 건수 [" + rec_cnt + "] 등록건수 [" + insert_cnt +"] 수정건수 [" + update_cnt + "] 삭제건수 [" + delete_cnt + "] 물리적삭제 [" + p_del_cnt + "]");
				log.info("고지원장업데이트 ["+remote_up_cnt+"], 미처리 부과건수 [" + nul_cnt + "]");
				
				
				/********************************* 구청별 처리 로그 등록 **************************************************/
				try{
					MapForm daemonMap = new MapForm();
					daemonMap.setMap("DAEMON"    , "TXDM2475");
					daemonMap.setMap("DAEMON_NM" , "주정차위반과태료 연계");
					daemonMap.setMap("SGG_COD"   , c_slf_org);
					daemonMap.setMap("TOTAL_CNT" , rec_cnt);
					daemonMap.setMap("INSERT_CNT", insert_cnt);
					daemonMap.setMap("UPDATE_CNT", update_cnt);
					daemonMap.setMap("DELETE_CNT", delete_cnt);
					daemonMap.setMap("ERROR_CNT" , 0);
					cyberService.queryForInsert("CODMBASE.InsertDaemonProcessCheck", daemonMap);
				}catch(Exception ex){
					log.info("구청별 처리 로그 등록 오류");
				}				
				/**********************************************************************************************************/
				
			}

			elapseTime2 = System.currentTimeMillis();
			
			log.info("[" + c_slf_org_nm + "]주정차위반과태료 부과자료연계 시간(" + tot_size + ") : " + CbUtil.formatTime(elapseTime2 - elapseTime1));
					
		} catch (Exception e) {
		    log.error("["+this.c_slf_org+"] TXDM2475 연계 오류 = " + gblNidLevyRows.getMaps());
			throw (RuntimeException) e;
		}
		
		return tot_size;
	}

	/*세외수입에서 제외시켜야 할 SEQ*/
	private String not_in_seq(){
	
		StringBuffer sb = new StringBuffer();
		String rt = "";
		try {
	
			ArrayList<MapForm> al =  cyberService.queryForList("TXDM2475.SELECT_SP_TX2111_NOT_SEQ", dataMap);
			
			if (al.size()>0){
				sb.append("'");
				for (int i=0;i<al.size();i++){
					
					MapForm mf =  al.get(i);
					if(mf==null||mf.isEmpty())continue;
					
					sb.append(mf.getMap("NOTSEQ")).append("','");
				}
				
				rt = sb.toString().substring(0, sb.toString().length() -2);
			} 
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	
		return rt;
	}

}
