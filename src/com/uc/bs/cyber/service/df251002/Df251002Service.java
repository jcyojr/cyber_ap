/**
 *  주시스템명 : 사이버지방세청 고도화
 *  업  무  명 : 상하수도고지내역 상세조회 
 *  기  능  명 : 편의점(더존)-사이버세청 
 *               업무처리
 *               편의점(더존)에서 수신된 전문에 대해서 응답전문데이트를 생성하고 
 *               조회결과를 저장한다.
 *  클래스  ID : Df251002Service
 *  변경  이력 :
 * ------------------------------------------------------------------------
 *  작성자     소속  		 일자      Tag   내용
 * ------------------------------------------------------------------------   
 * 임창섭   유채널(주)    2013.09.13   %01%  수정 
 */
package com.uc.bs.cyber.service.df251002;

import java.math.BigDecimal;
import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;

import com.uc.core.MapForm;
import com.uc.core.spring.service.IbatisBaseService;
import com.uc.bs.cyber.CbUtil;
import com.uc.bs.cyber.service.df201002.Df201002FieldList;

/**
 * @author Administrator
 *
 */
public class Df251002Service {

	protected Log log = LogFactory.getLog(this.getClass());
	
	private ApplicationContext appContext = null;
	
	private IbatisBaseService sqlService_cyber = null;
		
	private Df201002FieldList dfField = null;
	
	CbUtil cUtil = null;
	
	/**
	 * 생성자
	 */
	public Df251002Service(ApplicationContext appContext) {
		// TODO Auto-generated constructor stub
		
		setAppContext(appContext);
		
		dfField = new Df201002FieldList();
		
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
	 * 상하수도고지내역 고지내역 상세조회 처리...
	 * */
	public byte[] chk_df_251002(MapForm headMap, MapForm mf) throws Exception {
		
				
		MapForm sendMap = new MapForm();
		
		/*상하수도 고지내역조회 시 사용 */
		String resCode = "0000";      /*응답코드*/
		String tx_id = "SN2601";      /*부산시코드*/
		String sg_code = "26";        /*부산시기관코드*/
		String napkiGubun = "";
		
		try{
			cUtil = CbUtil.getInstance();
			
			log.info(cUtil.msgPrint("2", "S","DP251002 chk_df_251002()", resCode));
			
			/*전문체크*/
			
			/*발행기관분류코드 오류*/
			if(!headMap.getMap("SIDO_CODE").equals(sg_code)){
                resCode = "1000";
			}
			/*지로번호 오류*/
			if(!headMap.getMap("TX_ID").equals(tx_id)){
				resCode = "1000";
			}

            mf.setMap("PRT_NO"     , CbUtil.getCurrent("yyyyMMdd").substring(0, 2) + mf.getMap("TAX_NO").toString().substring(0, 12));

            log.debug("PRT_NO = [" + mf.getMap("PRT_NO") + "]");
			
            /* 납부내역 존재여부 확인 조회 */
            ArrayList<MapForm>  dfAl251002List  =  sqlService_cyber.queryForList("DF251002.SELECT_SHSD_PAY_LIST", mf);
            
            int payCnt = dfAl251002List.size(); /*납부총건수*/
            
            log.debug("payCnt = " + payCnt);
            
            if(payCnt == 0) {
                resCode = "0000";   /*납부내역없음*/
            } else {
                /*납부내역 있음*/
                MapForm mfCmdEpayList = dfAl251002List.get(0);
                
                if ( mfCmdEpayList == null || mfCmdEpayList.isEmpty() ) {
                    /*납부내역없음*/
                    resCode = "0000";
                } else {
                    /*납부내역이 있는 경우*/
                    if(mfCmdEpayList.getMap("SNTG").equals("9")) {  /*취소*/
                        resCode = "0000";
                    } else {
                        resCode = "5000";  /*납부기수신*/
                    }
                }
            }
            
			int levyCnt = 0;
		
			if(resCode.equals("0000")) {
			    
			    if(mf.getMap("EPAY_NO").toString().length() == 17) {
	                mf.setMap("EPAY_NO", mf.getMap("EPAY_NO").toString() + "00");
	            }
		
				if(mf.getMap("EPAY_NO").toString().length() == 19) {
					
					ArrayList<MapForm>  dfCmd251002List  =  sqlService_cyber.queryForList("DF251002.SELECT_SHSD_LEVY_LIST", mf);
					
					levyCnt = dfCmd251002List.size();   /*고지총건수*/
					
					if(levyCnt <= 0){
						resCode = "5020";   /*조회내역 없음*/
					} else if ( levyCnt > 1 ) {
						resCode = "5060";   /*고지건수2건이상*/
					} else {
						
						resCode = "0000";

						//전문처리 변수선언
					    String BNAPDATE             = "";       // 납기내 납기일
					    long BNAPAMT                = 0 ;       // 납기내 금액
					    long ANAPAMT                = 0 ;       // 납기후 금액
					    long NAPAMT                 = 0 ;       // 실제납부 해야할 금액
					    String ANAPDATE             = "";       // 납기후 납기일
					    String CNAPTERM             = "";       // 체납기간
					    String ADDR                 = "";       // 주소
					    String USETERM              = "";       // 사용기간
					    String AUTOREG              = "";       // 자동이체등록여부
//					    String SNAP_BANK_CODE       = "";       // 수납은행 코드
//					    String SNAP_SYMD            = "";       // 수납일자
					    String NAPGUBUN             = "";       // 납기내후구분코드
					    String SGG_NM               = "상수도본부";
					    
					    String CURDATE = (String) CbUtil.getCurrentDate().substring(0, 8);
					    log.debug("CURDATE = " + CURDATE );
						
					    MapForm mpCmd251002List = dfCmd251002List.get(0);

						//납기일자 SET
                        BNAPDATE = (String) mpCmd251002List.getMap("DUE_DT");         //납기내 납기일
                        ANAPDATE = (String) mpCmd251002List.getMap("DUE_F_DT");       //납기후 납기일
                        
                        //조회일자와 비교하여 납기후 납기일 이내 자료만 처리한다.
                        if (CbUtil.getInt(CURDATE) > CbUtil.getInt(ANAPDATE)) {
                            resCode = "5010";   //납기일자가 지난 고지분
                        } else {
                            
    						String dang   = (String) mpCmd251002List.getMap("GUBUN");    //당월구분
    						String prt_gb   = (String) mpCmd251002List.getMap("PRT_GB"); //고지구분
						
    //		                ADDR     = (String) mpCmd251002List.getMap("ADDRESS");        //주소  
    //		                OCR      = (String) mpCmd251002List.getMap("OCR_1") + (String) mpCmd251002List.getMap("OCR_2");
    		                
    		                //납기내(B), 납기후(A) 체크
    		                napkiGubun = cUtil.getNapGubun(BNAPDATE, ANAPDATE);
    		                
    		                log.debug("napkiGubun = " + napkiGubun );
    
    		                NAPGUBUN = napkiGubun;
    		                
    						if(dang.equals("1") || dang.equals("3")) {
    							
    							if(log.isDebugEnabled()){
    								log.debug("정기/수시 Starting");
    							}
    							
    			                //납부총액 SET
    			                BNAPAMT  = ((BigDecimal)mpCmd251002List.getMap("SUM_B_AMT")).longValue();         //납기내 금액
    			                ANAPAMT  = ((BigDecimal)mpCmd251002List.getMap("SUM_F_AMT")).longValue();         //납기후 금액
    							
    			                /* 실제 납부해야할 금액*/
                                if ("B".equals(NAPGUBUN)) {
                                    if ("2".equals(prt_gb)) {
                                        resCode = "7000";   //자동이체 납부대상 자료입니다.
                                        NAPAMT = BNAPAMT;
                                    } else {
                                        NAPAMT = BNAPAMT;
                                    }
                                } else {
                                    NAPAMT = ANAPAMT;
                                }
    
    						} else if (dang.equals("2")){
    							
    							if(log.isDebugEnabled()){
    								log.debug("체납 Starting");
    							}
    							
    							//납기일자 SET
    			                //납기내인경우 납기내 일자 SET, 납기후인 경우 납기후 일자 SET
    			                //납기내(B), 납기후(A) 체크
    							if(NAPGUBUN.equals("B"))		                {
    			                    BNAPDATE = (String) mpCmd251002List.getMap("DUE_DT");         //납기후 납기일
    			                    ANAPDATE = BNAPDATE;
    			                } else  {
    			                    BNAPDATE = (String) mpCmd251002List.getMap("DUE_F_DT");       //납기후 납기일
    			                    ANAPDATE = BNAPDATE;
    			                }
    							
    							//체납분인경우 무조건 납기내기한으로 납부구분 설정
    			                NAPGUBUN = "B";
    
    			                //납부총액 SET
    			                BNAPAMT  = ((BigDecimal)mpCmd251002List.getMap("SUM_B_AMT")).longValue();       //납기내 금액
    			                ANAPAMT  = 0;
    			                NAPAMT   = BNAPAMT;
    			                CNAPTERM = (String) mpCmd251002List.getMap("NOT_STT_DATE") + " ~ " + (String) mpCmd251002List.getMap("NOT_END_DATE");          //체납기간
    			                USETERM = CNAPTERM;          //사용기간
    
    			                AUTOREG = "N";      //자동이체 등록여부
    							
    						}
    						
    						String KIBUN = "";
    						
    						if(dang.equals("1")) {
                                KIBUN = "1";
                            }else if (dang.equals("2")) {
                                KIBUN = "0";
    						}else if (dang.equals("3")) {
    						    KIBUN = "2";
    						}
    						
    						String napName = (String) mpCmd251002List.getMap("REG_NM") +"  "+ (String) mpCmd251002List.getMap("ADDRESS");
    						/* 납세자명이 80바이트기 때문에 모든 글자가 한글이 경우 40자 이하로 자른다 */
                            if(napName.length() > 0)
                                sendMap.setMap("NAP_NAME"           , CbUtil.ksubstr(napName, 40));
                            else 
                                sendMap.setMap("NAP_NAME"           , napName);
                            
    						sendMap.setMap("TAX_GB"             , mf.getMap("TAX_GB"));  /*세금 구분(3:상수도)*/
    						sendMap.setMap("TAX_NO"             , mf.getMap("TAX_NO"));  /*납부번호             */
    						sendMap.setMap("EPAY_NO"            , mf.getMap("EPAY_NO")); /*수용가번호/전자납부번호 */
    //						sendMap.setMap("NAP_NAME"           , mpCmd251002List.getMap("REG_NM") +"  "+ mpCmd251002List.getMap("ADDRESS").toString());        /*성명 + 주소 */
    						sendMap.setMap("SGG_NAME"           , SGG_NM);  //CbUtil.k2u("상수도본부")
    						sendMap.setMap("TAX_NAME"           , mpCmd251002List.getMap("TAX_NOTICE_TITLE"));
    						sendMap.setMap("TAX_YM"             , (String)mpCmd251002List.getMap("TAX_YY") + (String)mpCmd251002List.getMap("TAX_MM")); /*부과 년월*/
    						sendMap.setMap("TAX_DIV"            , KIBUN);     /*기분*/
    						sendMap.setMap("NAP_BFDATE"         , BNAPDATE);  /*납기내 납기일   | 납부 마감일 */
    						sendMap.setMap("NAP_BFAMT"          , BNAPAMT);   /*납기내 금액     | 체납액      */
    						sendMap.setMap("NAP_AFDATE"         , ANAPDATE);  /*납기후 납기일*/
    						sendMap.setMap("NAP_AFAMT"          , ANAPAMT);   /*납기후 금액*/
    						sendMap.setMap("NAP_AMT"            , NAPAMT);    /*실제 납부해야할 금액*/
    						sendMap.setMap("TAX_DESC"           , USETERM);   /*과세 사항*/
    						sendMap.setMap("FILLER1"            , " ");       /*      */
		                }
						
					}
					
				} else {
					resCode = "2000";  /*전자납부번호, 수용가번호 오류*/
				}
				
			}

			log.info(cUtil.msgPrint("2", "","DP251002 chk_df_251002()", resCode));
			
        } catch (Exception e) {
			
        	resCode = "9090";  //Error : 전자납부번호, 수용가번호 오류
        	
			e.printStackTrace();
			log.error("============================================");
			log.error("== chk_df_251002 Exception(시스템) ");
			log.error("============================================");
		}
        
        /*여기서 공통헤드를 셋팅한다. 바뀌는 부분만 다시 셋팅해서 전문을 조립*/
        headMap.setMap("RESP_CODE" , resCode);     /*응답코드*/
        headMap.setMap("TX_GUBUN"  , "040");
        
        return dfField.makeSendBuffer(headMap, sendMap);
	}

	/*----------------------------------------------*/
	/* 일련번호 가져오기 테스트*/
	/*----------------------------------------------*/
	private int SeqNumber(){
    	return (Integer)sqlService_cyber.queryForBean("CODMBASE.CODM_GETSEQNO", "00");
    }
}
