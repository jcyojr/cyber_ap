/**
 *  주시스템명 : 사이버지방세청 고도화
 *  업  무  명 : 지방세 납부통지전문
 *  기  능  명 : 편의점(더존)-사이버세청 
 *               업무처리
 *               편의점(더존)에서 수신된 전문에 대해서 응답전문데이트를 생성하고 
 *               조회결과를 저장한다.
 *  클래스  ID : Df502001Service
 *  변경  이력 :
 * ------------------------------------------------------------------------
 *  작성자     소속  		 일자      Tag   내용
 * ------------------------------------------------------------------------   
 * 임창섭   유채널(주)    2013.08.13   %01%  신규작성
 *  
 */
package com.uc.bs.cyber.service.df502001;

import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;

import com.uc.core.MapForm;
import com.uc.core.spring.service.IbatisBaseService;
import com.uc.bs.cyber.CbUtil;
import com.uc.bs.cyber.service.df202001.Df202001FieldList;

/**
 * @author Administrator
 *
 */
public class Df502001Service {

	protected Log log = LogFactory.getLog(this.getClass());
	
	private ApplicationContext appContext = null;
	
	private IbatisBaseService sqlService_cyber = null;
		
	private Df202001FieldList dfField = null;
	
	private int[] svcTms = new int[2];
	
	CbUtil cUtil = null;
	
	/**
	 * 생성자
	 */
	public Df502001Service(ApplicationContext appContext) {
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
	 * 지방세 납부통보 처리...
	 * */
	public byte[] chk_df_502001(MapForm headMap, MapForm mf) throws Exception {
		
		/*
		 * 편의점 수납시간 : 07:00 ~ 22:00
		 * */
				
		MapForm sendMap = new MapForm();
		
		/*세외수입 고지내역조회 시 사용 */
		String resCode = "0000";       /*응답코드*/
		String tx_id = "SN2601";
		String sg_code = "26";        /*부산시기관코드*/
		int curTime = 0;
       
		try{
			
			/*서비스 시간*/
			svcTms[0] = 0700;
			svcTms[1] = 2200;
			
			cUtil = CbUtil.getInstance();
			
			log.info(cUtil.msgPrint("", "S", "DP502001 chk_df_502001()", resCode));
			
			/*전문체크*/
			/*기관*/
			if(!headMap.getMap("SIDO_CODE").equals(sg_code)){
				resCode = "1000";
			}
			/*발행기관코드*/
			if(!headMap.getMap("TX_ID").equals(tx_id)){
				resCode = "1000";
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
			
			if(resCode.equals("0000")){
				
				/* 조회조건 자르기(납부번호 잘라서 조회조건 만든다) */
			    mf.setMap("SGG_COD" , mf.getMap("TAX_NO").toString().substring(2, 5));    //구청코드
	            mf.setMap("ACCT_COD", mf.getMap("TAX_NO").toString().substring(6, 8));    //회계코드
	            mf.setMap("TAX_ITEM", mf.getMap("TAX_NO").toString().substring(8, 14));   //과목/세목코드
	            mf.setMap("TAX_YY"  , mf.getMap("TAX_NO").toString().substring(14, 18));  //과세년도
	            mf.setMap("TAX_MM"  , mf.getMap("TAX_NO").toString().substring(18, 20));  //과세월
	            mf.setMap("TAX_DIV" , mf.getMap("TAX_NO").toString().substring(20, 21));  //기분코드
	            mf.setMap("HACD"    , mf.getMap("TAX_NO").toString().substring(21, 24));  //행정동코드
	            mf.setMap("TAX_SNO" , mf.getMap("TAX_NO").toString().substring(24, 30));  //과세번호
				
				log.debug("TAX_NO   = [" + mf.getMap("TAX_NO") + "]");
				log.debug("EPAY_NO  = [" + mf.getMap("EPAY_NO") + "]");
				log.debug("SGG_COD  = [" + mf.getMap("SGG_COD") + "]");
				log.debug("ACCT_COD = [" + mf.getMap("ACCT_COD") + "]");
				log.debug("TAX_ITEM = [" + mf.getMap("TAX_ITEM") + "]");
				log.debug("TAX_YY   = [" + mf.getMap("TAX_YY") + "]");
				log.debug("TAX_MM   = [" + mf.getMap("TAX_MM") + "]");
				log.debug("TAX_DIV  = [" + mf.getMap("TAX_DIV") + "]");
				log.debug("HACD     = [" + mf.getMap("HACD") + "]");
				log.debug("TAX_SNO  = [" + mf.getMap("TAX_SNO") + "]");
				
				/*지방세 부과내역 조회*/
				ArrayList<MapForm>  dfCmd502001List  =  sqlService_cyber.queryForList("DF502001.SELECT_TAX_SEARCH_LIST", mf);
				
				log.info("dfCmd502001List.size() = [" + dfCmd502001List.size() +"]");
				
		        /* Error : 고지내역없음 */
				if(dfCmd502001List.size() <= 0){
					resCode = "5020";  
				} 
				/* Error : 고지내역 2건 이상 */
				else if (dfCmd502001List.size() > 1) {
					resCode = "5060";
				} else {
					
					/* 조회결과 담는 맵폼 */
					MapForm taxform = new MapForm();
					
					if ( dfCmd502001List.size() > 0 ) {
						
						taxform = dfCmd502001List.get(0);

						String napkiGubun = cUtil.getNapkiGubun(taxform.getMap("DUE_DT").toString());
						
						/* 버스, 주거지 */
						if(taxform.getMap("TAX_GBN").equals("2")){
							taxform.setMap("SUM_RCP", taxform.getMap("SUM_B_AMT"));
						}
		                /* 지방세 */
		                else {
		                	/* 체납(DLQ_DIV= 1:부과 2:체납)분말고 기분이 3(자납) 아닐때 납기내금액 및 납기후금액 등등 */
		                	if (taxform.getMap("DLQ_DIV").equals("1") && !taxform.getMap("TAX_DIV").equals("3")){
		                		
		                		/* 납부금액 */
		                		taxform.setMap("SUM_RCP", cUtil.getNapAmt(taxform.getMap("TAX_DT").toString()                       //부과일자
		        														, napkiGubun												//납기내후 구분
		        														, Long.parseLong(taxform.getMap("MNTX").toString())			//본세
																		, Long.parseLong(taxform.getMap("CPTX").toString())			//도시계회세
																		, Long.parseLong(taxform.getMap("CFTX").toString())			//공동시설세
																		, Long.parseLong(taxform.getMap("LETX").toString())			//교육세
																		, Long.parseLong(taxform.getMap("ASTX").toString())));		//농특세
		                		
		                	} else {
		                		/* 납부금액 */
		                		taxform.setMap("SUM_RCP", cUtil.getNapAmt(Long.parseLong(taxform.getMap("MNTX").toString())			//본세
		        														, Long.parseLong(taxform.getMap("MNTX_ADTX").toString())	//본세 가산금
																		, Long.parseLong(taxform.getMap("CPTX").toString())			//도시계회세
																		, Long.parseLong(taxform.getMap("CPTX_ADTX").toString())	//도시계회세 가산금
																		, Long.parseLong(taxform.getMap("CFTX").toString())			//공동시설세
																		, Long.parseLong(taxform.getMap("CFTX_ADTX").toString())	//공동시설세 가산금
																		, Long.parseLong(taxform.getMap("LETX").toString())			//교육세
																		, Long.parseLong(taxform.getMap("LETX_ADTX").toString())	//교육세 가산금
																		, Long.parseLong(taxform.getMap("ASTX").toString())			//농특세
		                												, Long.parseLong(taxform.getMap("ASTX_ADTX").toString())));	//농특세 가산금
		                		/* 나머지 세세목 금액 그대로 쓰면된다 */
		                	}
		                	
		                }

			            if (Long.parseLong(mf.getMap("NAPBU_AMT").toString()) == Long.parseLong(taxform.getMap("SUM_RCP").toString())) {
			            
    						taxform.setMap("SGG_COD" , taxform.getMap("SGG_COD"));  /*구청코드 */
    						taxform.setMap("ACCT_COD", taxform.getMap("ACCT_COD")); /*회계코드 */
    						taxform.setMap("TAX_ITEM", taxform.getMap("TAX_ITEM")); /*과세목   */
    					    taxform.setMap("TAX_YY"  , taxform.getMap("TAX_YY"));   /*부과년도 */
    					    taxform.setMap("TAX_MM"  , taxform.getMap("TAX_MM"));   /*부과월   */
    					    taxform.setMap("TAX_DIV" , taxform.getMap("TAX_DIV"));  /*기분     */
    					    taxform.setMap("HACD"    , taxform.getMap("HACD"));     /*행정동   */
    					    taxform.setMap("TAX_SNO" , taxform.getMap("TAX_SNO"));  /*과세번호 */
    					    taxform.setMap("RCP_CNT" , taxform.getMap("RCP_CNT"));  /*분납순번 */
    					    taxform.setMap("SNTG"    , "1");                        /*수납FLAG */
    					    taxform.setMap("SUM_RCP" , taxform.getMap("SUM_RCP"));  /*수납금액 */
    					    taxform.setMap("PAY_DT"  , mf.getMap("NAPBU_DATE"));    /*납부일자 */
    					    taxform.setMap("SNSU"    , mf.getMap("NAPBU_SNSU"));                        /*수납수단(금결원:'1', 부산은행:'3', 카드:'2') */
    					    //주의 납부기관이 전문상에 H로 되어 있기때문에 앞'0'은 삭제되므로 반드시 추가함...
    					    taxform.setMap("BANK_CD", headMap.getMap("BANK_CODE"));   /*납부기관 */
    					    taxform.setMap("BRC_NO" , CbUtil.rPadString(headMap.getMap("BANK_CODE").toString(), 7, '0'));    /*수납기관코드    */
    					    taxform.setMap("TMSG_NO", headMap.getMap("BCJ_NO"));    /*출금은행_전문관리번호 */   
    					    taxform.setMap("RSN_YN" , "N");
    					    
    					    int pay_cnt = 0, intLog = 0, pay_gbn = 0;
    					    
    					    /**
    						 * 20110726 : 자동이체 등록건은 수납처리불가
    						 */
    						if(!taxform.getMap("AUTO_TRNF_YN").equals("Y")){
    							
    							/* 지방세 수납처리 */
    						    if(taxform.getMap("TAX_GBN").equals("1")){
    						    	
    						    	/*모든 매체를 통한 납부건수가 있는지 확인(중복수납가능하므로 수납순번을 구함*/
    						    	pay_gbn = sqlService_cyber.getOneFieldInteger("DF502001.TX1201_TB_PAY", taxform);
    						    	
    						    	pay_cnt = pay_gbn;
    						    	
    						    	//if (pay_gbn == 0) {
    						    	//	pay_cnt = 0;
    						    	//} else {
    						    	//	pay_cnt = sqlService_cyber.getOneFieldInteger("DF502001.TX1201_TB_MaxPayCnt", taxform);
    						    	//}
    
    						    	taxform.setMap("PAY_CNT", pay_cnt);
    						    	
    						    	intLog = sqlService_cyber.queryForUpdate("DF502001.INSERT_TX1201_TB_EPAY", taxform);
    						    	
    								if(intLog > 0) {
    									
    									sqlService_cyber.queryForUpdate("DF502001.UPDATE_TX1102_TB_NAPBU_INFO", taxform);
    									
    									log.info("◆ 지방세 전자납부처리 완료! [" + mf.getMap("TAX_NO") + "]");
    									
//    									MapForm mpSMS = new MapForm();
//    									
//    									mpSMS.setMap("REG_NO"  , taxform.getMap("REG_NO"));
//    									mpSMS.setMap("TAX_ITEM", taxform.getMap("TAX_ITEM"));
//    									mpSMS.setMap("SUM_RCP" , taxform.getMap("SUM_RCP"));
//    									
//    									try {
//    										sqlService_cyber.procedure("CODMBASE.SMSCALL", mpSMS);
//    									}catch (Exception e) {
//    										log.info("SMS 등록데이터 = " + mpSMS.getMaps());
//    										log.info("SMS 등록오류 발생");
//    									}
//    									
    								}
    						    }
    						    /* 버스, 주거지 수납처리 */
    						    else if (taxform.getMap("TAX_GBN").equals("2")){
    						    	
    						    	pay_gbn = sqlService_cyber.getOneFieldInteger("DF502001.TX2221_TB_PAY", taxform);
    						    	
    						    	pay_cnt = pay_gbn;
    						    	
    						    	//if (pay_gbn == 0) {
    						    	//	pay_cnt = 0;
    						    	//} else {
    						    	//	pay_cnt = sqlService_cyber.getOneFieldInteger("DF502001.TX2221_TB_MaxPayCnt", taxform);
    						    	//}
    						    		
    						    	taxform.setMap("PAY_CNT", pay_cnt);
    						    	
    						    	intLog = sqlService_cyber.queryForUpdate("DF502001.INSERT_TX2221_TB_EPAY", taxform);
    						    	
    								if(intLog > 0) {
    									
    									sqlService_cyber.queryForUpdate("DF502001.UPDATE_TX2122_TB_NAPBU_INFO", taxform);
    									
    									log.info("◆ 버스. 주거지 전자납부처리 완료! [" + mf.getMap("TAX_NO") + "]");
    									
//    									MapForm mpSMS = new MapForm();	
//    									
//    									mpSMS.setMap("REG_NO"  , taxform.getMap("REG_NO"));
//    									mpSMS.setMap("TAX_ITEM", taxform.getMap("TAX_ITEM"));
//    									mpSMS.setMap("SUM_RCP" , taxform.getMap("SUM_RCP"));
//    									
//    									try {
//    //										sqlService_cyber.procedure("CODMBASE.SMSCALL", mpSMS);
//    									}catch (Exception e) {
//    										log.info("SMS 등록데이터 = " + mpSMS.getMaps());
//    										log.info("SMS 등록오류 발생");
//    									}
    								}
    						    }
    						} else {
    							
    							resCode = "7000";
    							//자동이체건으로 수납처리 불가...
    						}
			            } else {
                            resCode = "4000";        // 납부금액 틀림
                        }
                        
					} else { 
						/* 조회건수가 없는 경우 전문생성 */
						resCode = "5020";  /*조회내역없음*/
						
					} 
					
				}
				
			}

			log.info(cUtil.msgPrint("", "", "DP502001 chk_df_502001()", resCode));
			
        } catch (Exception e) {
			
        	//20110829 추가
        	resCode = "9090";
        	
			e.printStackTrace();
			log.error("============================================");
			log.error("== chk_df_502001 Exception(시스템) ");
			log.error("============================================");
		}
        
//        if(!resCode.equals("0000")) {
//        	sendMap = mf;
//        }
        
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

