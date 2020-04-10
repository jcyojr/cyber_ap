/**
 *  주시스템명 : 사이버지방세청 고도화
 *  업  무  명 : 결제원-세외수입 납부결과통지/ 예약신청 
 *  기  능  명 : 결제원-사이버세청 
 *               업무처리
 *               
 *  클래스  ID : Df272001Service
 *  변경  이력 :
 * ------------------------------------------------------------------------
 *  작성자     소속  		 일자      Tag   내용
 * ------------------------------------------------------------------------   
 * 임창섭   유채널(주)    2013.08.13   %01%  신규작성
 *  
 */
package com.uc.bs.cyber.service.df272001;

import java.math.BigDecimal;
import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.dao.DuplicateKeyException;

import com.uc.core.MapForm;
import com.uc.core.spring.service.IbatisBaseService;
import com.uc.bs.cyber.CbUtil;
import com.uc.bs.cyber.service.df202001.Df202001FieldList;

/**
 * @author Administrator
 *
 */
public class Df272001Service {

	protected Log log = LogFactory.getLog(this.getClass());
	
	private ApplicationContext appContext = null;
	
	private IbatisBaseService sqlService_cyber = null;
		
	private Df202001FieldList dfField = null;
	
	private int[] svcTms = new int[2];
	
	CbUtil cUtil = null;
	
	/**
	 * 생성자
	 */
	public Df272001Service(ApplicationContext appContext) {
		// TODO Auto-generated constructor stub
		
		setAppContext(appContext);
		
		dfField = new Df202001FieldList();
		
	}

	/* appContext property 생성 */
	public void setAppContext(ApplicationContext appContext) {
		
		this.appContext = appContext;
		
		sqlService_cyber = (IbatisBaseService) this.appContext.getBean("baseService_cyber");

	}

	public ApplicationContext getAppContext() {
		return appContext;
	}
	
	/**
	 * 세외수입 납부결과 통지 
	 * */
	public byte[] chk_df_272001(MapForm headMap, MapForm mf) throws Exception {
		
		/*세외수입 고지내역조회 시 사용 */
		String resCode = "0000";        /*응답코드*/
		String tx_id = "SN2601";    /*부산시업무구분코드*/
		String sg_code = "26";         /*부산시기관코드*/
		int curTime = 0;

		try{
			/*서비스 시간*/
			svcTms[0] = 7000;
			svcTms[1] = 2200;
			
			cUtil = CbUtil.getInstance();
			
			log.info(cUtil.msgPrint("3", "S","DP272001 chk_df_272001()", resCode));

			/*전문체크*/
			
			/*기관*/
			if(!headMap.getMap("SIDO_CODE").equals(sg_code)){
				resCode = "1000";
			}
			/*발행기관지로코드*/
			if(!headMap.getMap("TX_ID").equals(tx_id)){
				resCode = "1000";
			}
			
			if(mf.getMap("EPAY_NO").toString().length() == 17) {
				mf.setMap("EPAY_NO", mf.getMap("EPAY_NO").toString() + "00");
			}
			
			if(mf.getMap("EPAY_NO").toString().length() != 19) {
				resCode = "2000";
			}
			
			if(mf.getMap("TAX_NO").toString().length() != 31) {
                resCode = "2000";
			}
			
            try {
                curTime = Integer.parseInt(headMap.getMap("TX_DATETIME").toString().substring(8, 12));
                
                if (curTime >= svcTms[0] && curTime <= svcTms[1]) {
                    
                } else {
                    resCode = "9080";  /*납부시간 아님...*/
                }
            } catch(Exception e) {
                resCode = "5520"; /*날짜 형식 오류*/
            }

			/*편의점(더존)에서 수신한 전문을 받아 기수납내역 여부를 확인한다.*/
            int totCnt = 0;
            try {
    			ArrayList<MapForm>  dfAl272001List = sqlService_cyber.queryForList("DF272001.SELECT_BNON_PAY_LIST", mf);
    			
    			totCnt = dfAl272001List.size();
    			
    			log.debug("totCnt = " + totCnt);
    			
    			if(totCnt == 0) {
    				resCode = "0000";   /*납부내역없음*/
    			} else {
    				/*납부내역 있음*/
    				MapForm mfCmd272001List = dfAl272001List.get(0);
    				
    				if ( mfCmd272001List == null  ||  mfCmd272001List.isEmpty() )   {
    					/*납부내역없음*/
    					resCode = "0000";
    				} else {
    					/*납부내역이 있는 경우*/
    					if(mfCmd272001List.getMap("SNTG").equals("9")) {  /*취소*/
    						resCode = "0000";
    					} else {
    						resCode = "5000";  /*납부기수신*/
    					}
    				}
    			}
            } catch (Exception e) {
                resCode = "8000";
                log.info("세외수입 기납부여부 조회 Exception! [" + mf.getMap("ETAX_NO") + "]");
            }
			
			/*납부내역이 존재하지 않거나 취소된 경우 납부처리*/
			if(resCode.equals("0000")){
				/*부과리스트 검색*/
				ArrayList<MapForm>  alDfLevyList = sqlService_cyber.queryForList("DF272001.SELECT_BNON_SEARCH_LIST", mf);
				
				if(alDfLevyList.size() == 0) {
					resCode = "5020";  /*고지내역없음*/
				} else if(alDfLevyList.size() > 1) {
					resCode = "5060";  /*고지내역2건 1건인것만 처리*/
				} else {
					MapForm mpDfLevyList = alDfLevyList.get(0);
					
					long rcp_amt = 0;
					
					/*  
					 * 수납자료와 부과자료 데이터비교(1)
					 */
					if(cUtil.getNapGubun((String)mpDfLevyList.getMap("DUE_DT"), (String)mpDfLevyList.getMap("DUE_F_DT")).toString().equals("B")){
					    rcp_amt = ((BigDecimal)mpDfLevyList.getMap("AMT")).longValue(); 
					} else {
						rcp_amt = ((BigDecimal)mpDfLevyList.getMap("AFTAMT")).longValue(); 
					}
					
					if(mf.getMap("NAPBU_AMT").equals(String.valueOf(rcp_amt))){ //납부대상 금액과 수납된 금액 비교
							resCode = "0000";
					/* 납부금액 틀림 */
					} else {
						resCode = "4000";
					}
					
					if (resCode.equals("0000")) {
						/*
						 * 비교 끝(1)
						 */
					    log.debug("mpDfLevyList  = "  + mpDfLevyList.getMaps());
						/*전문정보조립*/
						/*전자수납테이블에 수납순번을 구한다. 중복처리가 가능하므로...*/
						
						int rcp_cnt = 0;
						
						if(totCnt == 0){
							rcp_cnt = 0;
						} else {
							
							if(mpDfLevyList.getMap("TAX_GBN").equals("1")) { //표준세외수입 전자수납
							    log.debug("◆  표준세외수입 MaxPayCnt ");
								rcp_cnt = sqlService_cyber.getOneFieldInteger("DF272001.TX2211_TB_MaxPayCnt", mpDfLevyList);
							} else {
							    log.debug("◆  구세외수입 MaxPayCnt ");
							    mpDfLevyList.setMap("TAX_NO" , mf.getMap("TAX_NO"));
								rcp_cnt = sqlService_cyber.getOneFieldInteger("DF272001.TX2221_TB_MaxPayCnt", mpDfLevyList);
							}
							
							log.debug("◆  세외수입 MaxPayCnt : " + Integer.toString(rcp_cnt));
						}
						
						mpDfLevyList.setMap("PAY_CNT" , rcp_cnt);
						mpDfLevyList.setMap("SUM_RCP" , mf.getMap("NAPBU_AMT"));     /*납부금액*/
						mpDfLevyList.setMap("PAY_DT"  , mf.getMap("NAPBU_DATE"));    /*수납일자*/
						mpDfLevyList.setMap("SNTG"    , "1");                        /*납부상태*/
						mpDfLevyList.setMap("SNSU"    , mf.getMap("NAPBU_SNSU"));      /*수납수단 1:금융결제원 2:카드 3:은행*/
						mpDfLevyList.setMap("BANK_CD" , headMap.getMap("BANK_CODE"));    /*납부기관*/
	                    mpDfLevyList.setMap("BRC_NO"  , CbUtil.rPadString(headMap.getMap("BANK_CODE").toString(), 7, '0'));   /*(수납)납부기관지로코드*/
	                    mpDfLevyList.setMap("RSN_YN"  , "N");                      /*납부구분 Y:예약  N:조회*/
						mpDfLevyList.setMap("TRTG"    , "0");                        /*자료전송상태*/						
						mpDfLevyList.setMap("TMSG_NO" , headMap.getMap("BCJ_NO"));   /*출금은행_전문관리번호*/
						
						mpDfLevyList.setMap("EPAY_NO" , mf.getMap("EPAY_NO"));       /*전자납부*/
						mpDfLevyList.setMap("TAX_NO"  , mf.getMap("TAX_NO"));       /*납세번호*/
						
						/*구세외를 위한*/
						if(mpDfLevyList.getMap("TAX_GBN").equals("2")) {
    		                mpDfLevyList.setMap("SGG_COD" , mf.getMap("TAX_NO").toString().substring(2, 5));    //구청코드
    		                mpDfLevyList.setMap("ACCT_COD", mf.getMap("TAX_NO").toString().substring(6, 8));    //회계코드
    		                mpDfLevyList.setMap("TAX_ITEM", mf.getMap("TAX_NO").toString().substring(8, 14));   //과목/세목코드
    		                mpDfLevyList.setMap("TAX_YY"  , mf.getMap("TAX_NO").toString().substring(14, 18));  //과세년도
    		                mpDfLevyList.setMap("TAX_MM"  , mf.getMap("TAX_NO").toString().substring(18, 20));  //과세월
    		                mpDfLevyList.setMap("TAX_DIV" , mf.getMap("TAX_NO").toString().substring(20, 21));  //기분코드
    		                mpDfLevyList.setMap("HACD"    , mf.getMap("TAX_NO").toString().substring(21, 24));  //행정동코드
    		                mpDfLevyList.setMap("TAX_SNO" , mf.getMap("TAX_NO").toString().substring(24, 30));  //과세번호
    		                
    		                log.debug("SGG_COD  = [" + mpDfLevyList.getMap("SGG_COD") + "]");
    		                log.debug("ACCT_COD = [" + mpDfLevyList.getMap("ACCT_COD") + "]");
    		                log.debug("TAX_ITEM = [" + mpDfLevyList.getMap("TAX_ITEM") + "]");
    		                log.debug("TAX_YY   = [" + mpDfLevyList.getMap("TAX_YY") + "]");
    		                log.debug("TAX_MM   = [" + mpDfLevyList.getMap("TAX_MM") + "]");
    		                log.debug("TAX_DIV  = [" + mpDfLevyList.getMap("TAX_DIV") + "]");
    		                log.debug("HACD     = [" + mpDfLevyList.getMap("HACD") + "]");
    		                log.debug("TAX_SNO  = [" + mpDfLevyList.getMap("TAX_SNO") + "]");
						}
						
						int intLog = 0;
						
						try {
    						if(mpDfLevyList.getMap("TAX_GBN").equals("1")) {
    							intLog = sqlService_cyber.queryForUpdate("DF272001.INSERT_TX2211_TB_EPAY", mpDfLevyList);  /*표준세외수입*/
    						} else {
    							intLog = sqlService_cyber.queryForUpdate("DF272001.INSERT_TX2221_TB_EPAY", mpDfLevyList);  /*구세외수입 : 버스전용, 주거지*/
    						}
    
    						if(intLog > 0) {
    							
    							if(mpDfLevyList.getMap("TAX_GBN").equals("1")) {
    								sqlService_cyber.queryForUpdate("DF272001.UPDATE_TX2112_TB_NAPBU_INFO", mpDfLevyList);
    								
    								log.info("◆ 표준세외수입 전자납부처리 완료! [" + mf.getMap("EPAY_NO") + "]");
    							}else{
    								
    								sqlService_cyber.queryForUpdate("DF272001.UPDATE_TX2122_TB_NAPBU_INFO", mpDfLevyList);
    								
    								log.info("◆ 구세외수입 전자납부처리 완료! [" + mf.getMap("EPAY_NO") + "]");
    							}
    						}
						} catch (Exception e) {
						    resCode = "8000";
						    log.info(" 표준세외수입 전자납부처리 Exception! [" + mf.getMap("EPAY_NO") + "]");
						}

							//결제원 납세번호가 다름으로 인한 로깅작업용 : 수납
							//mf.setMap("TAX_NO"    , mf.getMap("TAX_NO"))
//							mf.setMap("TAX_NO"    , mpDfLevyList.getMap("TAX_NO"));	//로깅오류수정 by 임창섭
//							mf.setMap("JUMIN_NO"  , mpDfLevyList.getMap("JUMIN_NO"));
//							mf.setMap("NAPBU_AMT" , mf.getMap("NAPBU_AMT"));  
//							mf.setMap("PAY_DT"    , mf.getMap("NAPBU_DATE"));
//							mf.setMap("TX_GB"     , mpDfLevyList.getMap("TAX_GBN"));  
//							
//							try{
//								sqlService_cyber.queryForUpdate("DF271002.INSERT_TX2421_TB_LOG", mf);//버스전용결제원수납로그
//							}catch (Exception e) {
//								if (e instanceof DuplicateKeyException){
//									log.info("수납: TX2421_TB 테이블에 이미 기록된 데이터");
//								} else {
//									log.error("오류 데이터 = " + mf.getMaps());
//								    log.error("Logging  failed!!!");	
//								}
//							}
							
//							MapForm mpSMS = new MapForm();
//							
//							mpSMS.setMap("REG_NO"  , mpDfLevyList.getMap("REG_NO"));
//							mpSMS.setMap("TAX_ITEM", mpDfLevyList.getMap("TAX_ITEM"));
//							mpSMS.setMap("SUM_RCP" , mf.getMap("NAPBU_AMT"));
//							
//							try {
////								sqlService_cyber.procedure("CODMBASE.SMSCALL", mpSMS);
//							}catch (Exception e) {
//								log.info("SMS 등록데이터 = " + mpSMS.getMaps());
//								log.info("SMS 등록오류 발생");
//							}
//						}
					}
				}
			}
			
			log.info(cUtil.msgPrint("3", "","DP272001 chk_df_272001()", resCode));
			
        } catch (Exception e) {
			
        	resCode = "9090";  //Error : 알려지지 않은 에러
        	
			e.printStackTrace();
			log.error("============================================");
			log.error("== chk_df_272001 Exception(시스템) ");
			log.error("============================================");
		}
        
        /*여기서 공통헤드를 셋팅한다. 바뀌는 부분만 다시 셋팅해서 전문을 조립*/
        headMap.setMap("RESP_CODE" , resCode);     /*응답코드*/
        headMap.setMap("TX_GUBUN"  , "060");
        
        return dfField.makeSendBuffer(headMap, mf);
	}

	/*----------------------------------------------*/
	/* 일련번호 가져오기 테스트*/
	/*----------------------------------------------*/
	private int SeqNumber(){
    	return (Integer)sqlService_cyber.queryForBean("CODMBASE.CODM_GETSEQNO", "00");
    }
}
