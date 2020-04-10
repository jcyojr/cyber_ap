/**
 *  주시스템명 : 부산시청 사이버 지방세청
 *  업  무  명 : 연계
 *  기  능  명 : 표준세외수입 부과자료연계
 *  클래스  ID : Txdm2411_process : 실 처리로직을 작성하는 클래스
 *  변경  이력 :
 * ------------------------------------------------------------------------
 *  작성자      	소속  		    일자            Tag           내용
 * ------------------------------------------------------------------------
 *  송동욱       유채널(주)      2011.05.11         %01%         최초작성
 *  표승한       다산시스템             2013.12.11         %02%         연계변경
 */
package com.uc.bs.cyber.daemon.txdm2411;

import java.math.BigDecimal;
import java.util.ArrayList;

import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;

import com.uc.bs.cyber.CbUtil;
import com.uc.bs.cyber.daemon.Codm_BaseProcess;
import com.uc.bs.cyber.daemon.Codm_interface;
import com.uc.core.MapForm;
import com.uc.core.spring.service.UcContextHolder;

/**
 * @author Administrator
 * 20110919 송
 * 속도가 넘 느리다 
 * 방식을 좀 달리 해야 겠음...
 * 일단 전체카운터를 구하고 
 * 100~1000단위로 페이지를 나누고 
 * 페이지별 갯수만큼 한꺼번에 처리하도록 트랜잭션을 구성한다...
 */
public class Txdm2411_process extends Codm_BaseProcess implements Codm_interface {

	private MapForm dataMap            = null;
	private int insert_cnt = 0, update_cnt = 0, delete_cnt = 0, remote_up_cnt = 0;
    private int p_del_cnt = 0;
	
	/*트랜잭션을 위하여 */
	MapForm gblNidLevyRows    = null;
	
	/**
	 * 
	 */
	public Txdm2411_process() {
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
		log.info("=[표준세외수입-[" + c_slf_org_nm + "] 부과자료연계] Start =");
		log.info("=====================================================================");
		
		try {
				
			setContext(appContext);
			setApp(appContext);

			UcContextHolder.setCustomerType(this.dataSource);
						
			dataMap = new MapForm();  /*초기화*/
			
			log.info("this.c_slf_org ======================= [" + this.c_slf_org + "]");

			dataMap.setMap("SGG",  this.c_slf_org);
			dataMap.setMap("BS_NOT_IN_SEMOK", not_in_semok());
			dataMap.setMap("BS_NOT_IN_SEQ", not_in_seq());

			long SEQ_MAX = 0L;
			try{
				SEQ_MAX = Long.parseLong(govService.getOneFieldString("TXDM2411.SELECT_SPNT200_SEQ_MAX", dataMap));
			}catch (Exception sub_e){
				log.error("["+this.c_slf_org+"] SELECT_SPNT200_SEQ_MAX 오류");
				sub_e.printStackTrace();
			}						

			if(SEQ_MAX>0L){
				try{
					govService.queryForUpdate("TXDM2411.UPDATE_SPNT200_START", SEQ_MAX);
				}catch (Exception sub_e){
					log.error("["+this.c_slf_org+"] UPDATE_SPNT200_START 오류");
					sub_e.printStackTrace();
				}						

				this.startJob();

				try{
					govService.queryForUpdate("TXDM2411.UPDATE_SPNT200_END", dataMap);
				}catch (Exception sub_e){
					log.error("["+this.c_slf_org+"]UPDATE_SPNT200_END 오류");
					sub_e.printStackTrace();
				}						
			
			}else{
				
				/***********************************************************************************/
				try{
					MapForm daemonMap = new MapForm();
					daemonMap.setMap("DAEMON"    , "TXDM2411");
					daemonMap.setMap("DAEMON_NM" , "표준세외수입(SPNT200)부과자료연계");
					daemonMap.setMap("SGG_COD"   , c_slf_org);
					daemonMap.setMap("TOTAL_CNT" , 0);
					daemonMap.setMap("INSERT_CNT", 0);
					daemonMap.setMap("UPDATE_CNT", 0);
					daemonMap.setMap("DELETE_CNT", 0);
					daemonMap.setMap("ERROR_CNT" , 0);
					cyberService.queryForInsert("CODMBASE.InsertDaemonProcessCheck", daemonMap);
				}catch(Exception ex){
					log.debug("표준세외수입 부과자료연계 대상이 없습니다. 등록 오류");
				}				
				/***********************************************************************************/
				
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
		txdm2411_JobProcess();
	
	}
	
	
    /*표준세외수입...연계*/
	private int txdm2411_JobProcess() {
		
		log.info("=====================================================================");
		log.info("=" + this.getClass().getName()+ " txdm2411_JobProcess()[표준세외수입-[" + c_slf_org_nm + "] 부과자료연계] Start =");
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
		
		/*==============================================================*/
		/* 세외수입 부과자료 등록 (처리구분 : 1 신규, 2 수정, 3 삭제)
		 *  처리구분이 3일때는 삭제하고
		 *  1 이거나 2일때는 INSERT한다
		 *  INSERT 하는중에 자료 중복이되면 UPDATE
		 *  LIST EPAY_NO (전자납부번호)에 전자납부번호를 담는다.
		 */
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

			try{
				alNidLevyList =  govService.queryForList("TXDM2411.SELECT_SPNT200_LIST", dataMap);
			}catch (Exception sub_e){
				log.error("["+this.c_slf_org+"] SELECT_SPNT200_LIST 오류 = " + gblNidLevyRows.getMaps());
				sub_e.printStackTrace();
			}						

			tot_size = alNidLevyList.size();
			
		    log.info("[" + c_slf_org_nm + "]세외수입부과연계자료 건수 = [" + tot_size + "]");
			
			if (tot_size  >  0)   {
				
				/*전자납부자료 1건씩 fetch 처리 */
				for ( rec_cnt = 0;  rec_cnt < tot_size;  rec_cnt++)   {
					
					gblNidLevyRows =  alNidLevyList.get(rec_cnt);
					
					/*혹시나...because of testing */
					if (gblNidLevyRows == null  ||  gblNidLevyRows.isEmpty() ){
						nul_cnt++;
						continue;
					}

				    String TRN_YN = "1";
/*
				    String REG_NO = (String) gblNidLevyRows.getMap("REG_NO");
				    String TAX_NO = (String) gblNidLevyRows.getMap("TAX_NO");
				    String EPAY_NO = (String) gblNidLevyRows.getMap("EPAY_NO");
				    String TAX_DIV = (String) gblNidLevyRows.getMap("TAX_DIV");
			        if(TRN_YN.equals("1")&&(REG_NO.trim().equals("")||TAX_NO.trim().equals("")||EPAY_NO.trim().equals("")||TAX_DIV.trim().equals("")))TRN_YN="X";
*/
				    String ACCT_COD = (String) gblNidLevyRows.getMap("ACCT_COD");
				    String TAX_ITEM = (String) gblNidLevyRows.getMap("TAX_ITEM");

			        long NATN_TAX = ((BigDecimal) gblNidLevyRows.getMap("NATN_TAX")).longValue();
			        long SDOTX = ((BigDecimal) gblNidLevyRows.getMap("SDOTX")).longValue();
			        long SGGTX = ((BigDecimal) gblNidLevyRows.getMap("SGGTX")).longValue();
			        long SDOTX_ADTX = ((BigDecimal) gblNidLevyRows.getMap("SDOTX_ADTX")).longValue();
			        long SGGTX_ADTX = ((BigDecimal) gblNidLevyRows.getMap("SGGTX_ADTX")).longValue();
					long VAT_AMT = ((BigDecimal) gblNidLevyRows.getMap("VAT_AMT")).longValue();
					long PAYMENT_DATE1 = ((BigDecimal) gblNidLevyRows.getMap("PAYMENT_DATE1")).longValue();
					long PAYMENT_DATE2 = ((BigDecimal) gblNidLevyRows.getMap("PAYMENT_DATE2")).longValue();
					long PAYMENT_DATE3 = ((BigDecimal) gblNidLevyRows.getMap("PAYMENT_DATE3")).longValue();
					long AFTPAYMENT_DATE1 = ((BigDecimal) gblNidLevyRows.getMap("AFTPAYMENT_DATE1")).longValue();
					long AFTPAYMENT_DATE2 = ((BigDecimal) gblNidLevyRows.getMap("AFTPAYMENT_DATE2")).longValue();
					long AFTPAYMENT_DATE3 = ((BigDecimal) gblNidLevyRows.getMap("AFTPAYMENT_DATE3")).longValue();
			        long OCR_NATN_TAX = ((BigDecimal) gblNidLevyRows.getMap("OCR_NATN_TAX")).longValue();
			        long OCR_SIDO_TAX = ((BigDecimal) gblNidLevyRows.getMap("OCR_SIDO_TAX")).longValue();
			        long OCR_SIGUNGU_TAX = ((BigDecimal) gblNidLevyRows.getMap("OCR_SIGUNGU_TAX")).longValue();
					long OCR_PAYMENT_DATE1 = ((BigDecimal) gblNidLevyRows.getMap("OCR_PAYMENT_DATE1")).longValue();
					long OCR_AFTPAYMENT_DATE1 = ((BigDecimal) gblNidLevyRows.getMap("OCR_AFTPAYMENT_DATE1")).longValue();
			        
					// 연계되면 안되는 자료
			        if(TRN_YN.equals("1")&&(ACCT_COD.equals("16")||NATN_TAX>0L||((ACCT_COD.equals("31")||ACCT_COD.equals("51"))&&(SGGTX+SGGTX_ADTX)>0L)||((ACCT_COD.equals("41")||ACCT_COD.equals("61"))&&(SDOTX+SDOTX_ADTX)>0L)))TRN_YN="X";
			        if(TRN_YN.equals("1")&&(not_in_semok_check(dataMap, (ACCT_COD+TAX_ITEM))))TRN_YN="X";
			        if(TRN_YN.equals("1")&&(
			        		NATN_TAX%10L>0L||SDOTX%10L>0L||SGGTX%10L>0L||SDOTX_ADTX%10L>0L||SGGTX_ADTX%10L>0L||VAT_AMT%10L>0L||
			        		PAYMENT_DATE1%10L>0L||PAYMENT_DATE2%10L>0L||PAYMENT_DATE3%10L>0L||AFTPAYMENT_DATE1%10L>0L||AFTPAYMENT_DATE2%10L>0L||AFTPAYMENT_DATE3%10L>0L||
			        		OCR_NATN_TAX%10L>0L||OCR_SIDO_TAX%10L>0L||OCR_SIGUNGU_TAX%10L>0L||OCR_PAYMENT_DATE1%10L>0L||OCR_AFTPAYMENT_DATE1%10L>0L
			        		))TRN_YN="X";
			        
					if(TRN_YN.equals("1")){
						
						// 전자납부번호 중복은 확인 후 연계
						int TX2111_CNT_EPAY_NO = cyberService.getOneFieldInteger("TXDM2411.SELECT_TX2111_CNT_EPAY_NO", gblNidLevyRows);
						int TX2211_CNT_EPAY_NO = cyberService.getOneFieldInteger("TXDM2411.SELECT_TX2211_CNT_EPAY_NO", gblNidLevyRows);
						int TX2111_CNT_TAX_NO = cyberService.getOneFieldInteger("TXDM2411.SELECT_TX2111_CNT_TAX_NO", gblNidLevyRows);
						int TX2112_CNT_TAX_NO = cyberService.getOneFieldInteger("TXDM2411.SELECT_TX2112_CNT_TAX_NO", gblNidLevyRows);
						int TX2211_CNT_TAX_NO = cyberService.getOneFieldInteger("TXDM2411.SELECT_TX2211_CNT_TAX_NO", gblNidLevyRows);
						if(TX2111_CNT_EPAY_NO>1)TRN_YN="W";

						// 신규, 수정, 삭제 구분 조건
						String CUD_OPT = (String) gblNidLevyRows.getMap("CUD_OPT");
						String v_cud="0";

						if(v_cud.equals("0")&&TX2111_CNT_EPAY_NO>0){
							if(v_cud.equals("0")&&TX2211_CNT_EPAY_NO>0)v_cud="0";
							else v_cud="2";
						}
						else v_cud="1";
						if((!v_cud.equals("0"))&&(CUD_OPT.equals("3")||PAYMENT_DATE1+AFTPAYMENT_DATE1==0L))v_cud="3";

						// 신규, 수정, 삭제 구분에 따른 처리 
						if(TRN_YN.equals("1")&&v_cud.equals("1")){

							if(TX2211_CNT_TAX_NO>0){
								TRN_YN="X";
							}else{
								if(TX2111_CNT_TAX_NO>0){
									try{
										cyberService.queryForUpdate("TXDM2411.UPDATE_TX2111_TAX_NO", gblNidLevyRows);
									}catch (Exception sub_e){
										TRN_YN="E";
										log.error("["+this.c_slf_org+"] UPDATE_TX2111_TAX_NO 오류 = " + gblNidLevyRows.getMaps());
										sub_e.printStackTrace();
									}

									try{
										cyberService.queryForUpdate("TXDM2411.UPDATE_TX2112_TAX_NO", gblNidLevyRows);
										update_cnt++;
									}catch (Exception sub_e){
										TRN_YN="E";
										log.error("["+this.c_slf_org+"] UPDATE_TX2112_TAX_NO 오류 = " + gblNidLevyRows.getMaps());
										sub_e.printStackTrace();
									}
								}else{
									try{
										cyberService.queryForUpdate("TXDM2411.INSERT_TX2111", gblNidLevyRows);
									}catch (Exception sub_e){
										TRN_YN="E";
										log.error("["+this.c_slf_org+"] INSERT_TX2111 오류 = " + gblNidLevyRows.getMaps());
										sub_e.printStackTrace();
									}						

									if(TX2112_CNT_TAX_NO>0){
										try{
											cyberService.queryForUpdate("TXDM2411.UPDATE_TX2112_TAX_NO", gblNidLevyRows);
											update_cnt++;
										}catch (Exception sub_e){
											TRN_YN="E";
											log.error("["+this.c_slf_org+"] UPDATE_TX2112_TAX_NO 오류 = " + gblNidLevyRows.getMaps());
											sub_e.printStackTrace();
										}
									}else{
										try{
											cyberService.queryForUpdate("TXDM2411.INSERT_TX2112", gblNidLevyRows);
											insert_cnt++;
										}catch (Exception sub_e){
											TRN_YN="E";
											log.error("["+this.c_slf_org+"] INSERT_TX2112 오류 = " + gblNidLevyRows.getMaps());
											sub_e.printStackTrace();
										}						
									}
									
								}
							}
						}

						if(TRN_YN.equals("1")&&(v_cud.equals("2")||v_cud.equals("3"))){
							gblNidLevyRows.setMap("V_TAX_NO",cyberService.getOneFieldString("TXDM2411.SELECT_TX2111_TAX_NO", gblNidLevyRows));
						}

						if(TRN_YN.equals("1")&&v_cud.equals("2")){
							try{
								cyberService.queryForUpdate("TXDM2411.UPDATE_TX2111_EPAY_NO", gblNidLevyRows);
							}catch (Exception sub_e){
								TRN_YN="E";
								log.error("["+this.c_slf_org+"] UPDATE_TX2111_EPAY_NO 오류 = " + gblNidLevyRows.getMaps());
								sub_e.printStackTrace();
							}

							try{
								cyberService.queryForUpdate("TXDM2411.UPDATE_TX2112_EPAY_NO", gblNidLevyRows);
								update_cnt++;
							}catch (Exception sub_e){
								TRN_YN="E";
								log.error("["+this.c_slf_org+"] UPDATE_TX2112_EPAY_NO 오류 = " + gblNidLevyRows.getMaps());
								sub_e.printStackTrace();
							}
						}

						if(TRN_YN.equals("1")&&v_cud.equals("3")&&TX2111_CNT_EPAY_NO>0){
							try{
								if (cyberService.queryForDelete("TXDM2411.UPDATE_TX2112_DEL_YN", gblNidLevyRows) > 0) {
									delete_cnt++;
								}
							}catch (Exception sub_e){
								TRN_YN="E";
								log.error("["+this.c_slf_org+"] UPDATE_TX2112_DEL_YN 오류 = " + gblNidLevyRows.getMaps());
								sub_e.printStackTrace();
							}						
						}
					}
					gblNidLevyRows.setMap("TRN_YN",TRN_YN);
					
					try{
						/*고지원장 업데이트*/
						remote_up_cnt +=govService.queryForUpdate("TXDM2411.UPDATE_SPNT200_TRN_YN", gblNidLevyRows);
					}catch (Exception sub_e){
						log.error("["+this.c_slf_org+"] UPDATE_SPNT200_TRN_YN 오류 = " + gblNidLevyRows.getMaps());
						try{
							cyberService.queryForUpdate("TXDM2411.INSERT_SP_TX2111_NOT_SEQ", gblNidLevyRows);
						}catch (Exception sub_e2){
							log.error("["+this.c_slf_org+"] INSERT_SP_TX2111_NOT_SEQ 오류 = " + gblNidLevyRows.getMaps());
							sub_e2.printStackTrace();
						}
					}	
					
					if(rec_cnt % 500 == 0) {
						log.info("[" + this.c_slf_org_nm + "][" + this.c_slf_org + "]세외수입부과자료처리중건수 = [" + rec_cnt + "]");
					}
				}
				
				log.info("[" + c_slf_org_nm + "]세외수입부과자료연계 건수 [" + rec_cnt + "] 등록건수 [" + insert_cnt +"] 수정건수 [" + update_cnt + "] 삭제건수 [" + delete_cnt + "] 물리적삭제 [" + p_del_cnt + "]");
				log.info("고지원장업데이트 ["+remote_up_cnt+"], 미처리 부과건수 [" + nul_cnt + "]");
				
				/***********************************************************************************/
				try{
					MapForm daemonMap = new MapForm();
					daemonMap.setMap("DAEMON"    , "TXDM2411");
					daemonMap.setMap("DAEMON_NM" , "표준세외수입(SPNT200)부과자료연계");
					daemonMap.setMap("SGG_COD"   , c_slf_org);
					daemonMap.setMap("TOTAL_CNT" , rec_cnt);
					daemonMap.setMap("INSERT_CNT", insert_cnt);
					daemonMap.setMap("UPDATE_CNT", update_cnt);
					daemonMap.setMap("DELETE_CNT", delete_cnt);
					daemonMap.setMap("ERROR_CNT" , 0);
					cyberService.queryForInsert("CODMBASE.InsertDaemonProcessCheck", daemonMap);
				}catch(Exception ex){
					log.debug("표준세외수입 부과자료연계 로그 등록 오류");
				}				
				/***********************************************************************************/
				
			}

			elapseTime2 = System.currentTimeMillis();
			
			log.info("[" + c_slf_org_nm + "]세외수입 부과자료연계 시간("+tot_size+") : " + CbUtil.formatTime(elapseTime2 - elapseTime1));
					
		} catch (Exception e) {
		    log.error("["+this.c_slf_org+"] TXDM2411 연계 오류 = " + gblNidLevyRows.getMaps());
			throw (RuntimeException) e;
		}
		
		return tot_size;
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

	
	/*세외수입에서 제외시켜야 할 SEQ*/
	private String not_in_seq() {
	
		StringBuffer sb = new StringBuffer();
		String Retval = "";
		try {
	
			ArrayList<MapForm> alNoSemokList =  cyberService.queryForList("TXDM2411.SELECT_SP_TX2111_NOT_SEQ", dataMap);
			
			if (alNoSemokList.size()  >  0)   {
				
				for ( int rec_cnt = 0;  rec_cnt < alNoSemokList.size();  rec_cnt++)   {
					
					MapForm mpNoSemokList =  alNoSemokList.get(rec_cnt);
					
					/*혹시나...because of testing */
					if (mpNoSemokList == null  ||  mpNoSemokList.isEmpty() )   {
						continue;
					}
					
					sb.append(mpNoSemokList.getMap("NOTSEQ")).append(",");

				}
				
				Retval = sb.toString().substring(0, sb.toString().length() -1);
				
			} 
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	
		return Retval;
	}

	
	/*세외수입에서 제외하는 세목 검사*/
	private boolean not_in_semok_check(MapForm dataMap, String taxItem) {

		boolean rtn=false;
		String semokList = (String)dataMap.getMap("BS_NOT_IN_SEMOK");
		semokList=semokList.replaceAll("'","");
		String semokArr[] = semokList.split(",");

		for(int i=0;i<semokArr.length;i++){
			if(semokArr[i].equals(taxItem.substring(2,5))||semokArr[i].equals(taxItem)){
				rtn=true;
				break;
			}
		}
		
		if(Integer.parseInt(taxItem.substring(2,5))<201||Integer.parseInt(taxItem.substring(2,5))>=300)rtn=true;
		if(taxItem.substring(2,8).equals("202099"))rtn=false;

		return rtn;
	}
}
