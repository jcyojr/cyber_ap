/**
 *  주시스템명 : 사이버지방세청 고도화
 *  업  무  명 : 지방세 고지내역 상세조회 전문
 *  기  능  명 : 편의점(더존)-사이버세청 
 *  업무처리   :
 *               편의점(더존)에서 수신된 전문에 대해서 응답전문데이트를 생성하고 
 *               조회결과를 저장한다.
 *  클래스  ID : Df521001Service
 *  변경  이력 :
 * ------------------------------------------------------------------------
 *  작성자     소속  		 일자      Tag   내용
 * ------------------------------------------------------------------------   
 * 임창섭  유채널(주)    2013.08.13   %01%  신규작성
 *  
 */
package com.uc.bs.cyber.service.df521002;

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
public class Df521002Service {

	protected Log log = LogFactory.getLog(this.getClass());
	
	private ApplicationContext appContext = null;
	
	private IbatisBaseService sqlService_cyber = null;
		
	private Df201002FieldList dfField = null;
	
	CbUtil cUtil = null;
	
	/**
	 * 생성자
	 */
	public Df521002Service(ApplicationContext appContext) {
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
	 * 지방세 고지내역 상세조회 처리...
	 * */
	public byte[] chk_df_521002(MapForm headMap, MapForm mf) throws Exception {
		
				
		MapForm sendMap = new MapForm();
		
		/*지방세 고지내역조회 시 사용 */
		String resCode = "0000";      /*응답코드*/
//		String giro_no = "1000685";   /*부산시지로코드*/
		String tx_id = "SN2601";
		String sg_code = "26";        /*부산시기관코드*/
		
		try{
			cUtil = CbUtil.getInstance();
			
			log.info(cUtil.msgPrint("7", "S","DP521002 chk_df_521002()", resCode));
			
			/*전문체크*/
			/*기관*/
			if(!headMap.getMap("SIDO_CODE").equals(sg_code)){
				resCode = "1000";
			}
			/*발행기관지로코드*/
			if(!headMap.getMap("TX_ID").equals(tx_id)){
				resCode = "1000";
			}
			
			if(mf.getMap("EPAY_NO").toString().length() != 19) {
                resCode = "2000";
            }
            
            if(mf.getMap("TAX_NO").toString().length() != 31) {
                resCode = "2000";
            }
            
			if (resCode.equals("0000")) {

			    mf.setMap("SGG_COD" , mf.getMap("TAX_NO").toString().substring(2, 5));    //구청코드
			    mf.setMap("ACCT_COD", mf.getMap("TAX_NO").toString().substring(6, 8));    //회계코드
			    mf.setMap("TAX_ITEM", mf.getMap("TAX_NO").toString().substring(8, 14));   //과목/세목코드
			    mf.setMap("TAX_YY"  , mf.getMap("TAX_NO").toString().substring(14, 18));  //과세년도
			    mf.setMap("TAX_MM"  , mf.getMap("TAX_NO").toString().substring(18, 20));  //과세월
			    mf.setMap("TAX_DIV" , mf.getMap("TAX_NO").toString().substring(20, 21));  //기분코드
			    mf.setMap("HACD"    , mf.getMap("TAX_NO").toString().substring(21, 24));  //행정동코드
			    mf.setMap("TAX_SNO" , mf.getMap("TAX_NO").toString().substring(24, 30));  //과세번호
			    
	            log.debug("SGG_COD  = [" + mf.getMap("SGG_COD") + "]");
	            log.debug("ACCT_COD = [" + mf.getMap("ACCT_COD") + "]");
	            log.debug("TAX_ITEM = [" + mf.getMap("TAX_ITEM") + "]");
	            log.debug("TAX_YY   = [" + mf.getMap("TAX_YY") + "]");
	            log.debug("TAX_MM   = [" + mf.getMap("TAX_MM") + "]");
	            log.debug("TAX_DIV  = [" + mf.getMap("TAX_DIV") + "]");
	            log.debug("HACD     = [" + mf.getMap("HACD") + "]");
	            log.debug("TAX_SNO  = [" + mf.getMap("TAX_SNO") + "]");
                
	            
	            /*지방세 전자수납을 조회하여 기납부여부를 확인한다.*/
                ArrayList<MapForm>  dfCmdEpayList  =  sqlService_cyber.queryForList("DF521002.SELECT_TAX_PAY_LIST", mf);
                
                int payCnt = dfCmdEpayList.size();
                
                log.debug("payCnt = " + payCnt);
                
                if(payCnt == 0) {
                    resCode = "0000";   /*납부내역없음*/
                } else {
                    /*납부내역 있음*/
                    MapForm mfCmdEpayList = dfCmdEpayList.get(0);
                    
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
                
                /*지방세 부과내역을 조회한다.*/
	            ArrayList<MapForm>  dfCmd521002List  =  sqlService_cyber.queryForList("DF521002.SELECT_TAX_SEARCH_LIST", mf);
                
                log.debug("TAX_NO = [" + mf.getMap("TAX_NO") + "]");
                log.debug("EPAY_NO = [" + mf.getMap("EPAY_NO") + "]");
                
                if(dfCmd521002List.size() <= 0){
                    resCode = "5020";  // Error : 고지내역없음
                } else if (dfCmd521002List.size() > 1){
                    resCode = "5060";  // Error : 고지내역 2건 이상
                }
                
				if ( dfCmd521002List.size() > 0 && resCode.equals("0000")) {
					
					resCode = "0000";
					
					MapForm mfCmd521002List  =  dfCmd521002List.get(0);
					
					/*
					 *  앞부분 공통 
					 */
					
					/**
					 * 20110727 : 자동이체 등록건은 수납처리 할 수 없게 함...
					 */
					if(mfCmd521002List.getMap("AUTO_TRNF_YN").equals("Y")) {
						log.info("==============자동이체등록건(수납처리불가)================");
//						resCode = "7000";
					}
					
					/* 자동이체 등록여부 */
//					sendMap.setMap("AUTO_TRNF_YN", mfCmd521002List.getMap("AUTO_TRNF_YN"));
					/* 납부번호 */
					sendMap.setMap("TAX_GB", mf.getMap("TAX_GB"));
					/* 일련번호 */
					sendMap.setMap("TAX_NO", mf.getMap("TAX_NO"));
					/* 주민(법인,사업자) 번호 */
					sendMap.setMap("EPAY_NO", mf.getMap("EPAY_NO"));
	                /* 납부자성명 */
                    sendMap.setMap("NAP_NAME", mfCmd521002List.getMap("REG_NM"));
					/* 부과기관 명*/
					sendMap.setMap("SGG_NAME", mfCmd521002List.getMap("SGNM"));
//					/* 과목세목명 */
//					sendMap.setMap("SEMOK_CD", mfCmd521002List.getMap("TAX_ITEM"));
					sendMap.setMap("TAX_NAME", mfCmd521002List.getMap("TAX_NM"));
					/* 과세년월 */
					sendMap.setMap("TAX_YM", mfCmd521002List.getMap("TAX_YM"));
					/* 기분 */
					sendMap.setMap("TAX_DIV", mfCmd521002List.getMap("TAX_DIV"));
					
					/*
					 * 앞부분 공통끝
					 */
					
					/* 납기구분 */
					String napGubun = cUtil.getNapkiGubun((String)mfCmd521002List.getMap("DUE_DT"));
					
					/* 버스, 주거지 전용 */
	                if(mfCmd521002List.getMap("TAX_GBN").equals("2")){
	                	/* 납기내금액 (납기내금액 납기후금액 일치) */
	                	sendMap.setMap("NAP_BFAMT", mfCmd521002List.getMap("AMT"));
	                	/* 납기후금액 */
	                	sendMap.setMap("NAP_AFAMT", sendMap.getMap("NAP_BFAMT"));
	                	/* 실제 납부해야할 금액 (납기후 금액과 납기내 금액은 같다) */
	                	sendMap.setMap("NAP_AMT", sendMap.getMap("NAP_BFAMT"));
	                	
	                	/* 납기구분은 무조건 B */
	                	napGubun = "B";
	                	
	                	/* 납기내일자 */
	                	sendMap.setMap("NAP_BFDATE", mfCmd521002List.getMap("DUE_DT"));
	                	/* 납기후일자 */
	                	sendMap.setMap("NAP_AFDATE", mfCmd521002List.getMap("DUE_F_DT"));
	                	/*부과내역*/
	                	sendMap.setMap("TAX_DESC", mfCmd521002List.getMap("MLGN"));
	                	/* 예비정보 2 */
		                sendMap.setMap("FILLER1", "");	
	                	
	                }
	                /* 지방세 */
	                else {
	                	/* 체납(DLQ_DIV= 1:부과 2:체납)분말고 기분이 3(자납) 아닐때 납기내금액 및 납기후금액 등등 */
	                	if (mfCmd521002List.getMap("DLQ_DIV").equals("1") && !mfCmd521002List.getMap("TAX_DIV").equals("3")){
	                		
	                	    
	                		/* 납기내금액 */
	                		sendMap.setMap("NAP_BFAMT", cUtil.getNapBfAmt(Long.parseLong(mfCmd521002List.getMap("MNTX").toString())  	//본세
	                													, Long.parseLong(mfCmd521002List.getMap("CPTX").toString())		//도시계회
	                													, Long.parseLong(mfCmd521002List.getMap("CFTX").toString())		//공동시설세
	                													, Long.parseLong(mfCmd521002List.getMap("LETX").toString())		//교육세
	                													, Long.parseLong(mfCmd521002List.getMap("ASTX").toString())));	//농특세
	                		/* 납기후금액 */
	                		sendMap.setMap("NAP_AFAMT", cUtil.getNapAfAmt(mfCmd521002List.getMap("TAX_DT").toString()					//부과일자
											                			, Long.parseLong(mfCmd521002List.getMap("MNTX").toString())  	//본세
																		, Long.parseLong(mfCmd521002List.getMap("CPTX").toString())		//도시계회
																		, Long.parseLong(mfCmd521002List.getMap("CFTX").toString())		//공동시설세
																		, Long.parseLong(mfCmd521002List.getMap("LETX").toString())		//교육세
																		, Long.parseLong(mfCmd521002List.getMap("ASTX").toString())));	//농특세
                            /* 실제 납부해야할 금액*/
                            if ("B".equals(napGubun)) {
                                sendMap.setMap("NAP_AMT", sendMap.getMap("NAP_BFAMT"));
                            } else {
                                sendMap.setMap("NAP_AMT", sendMap.getMap("NAP_AFAMT"));
                            }
	                		
	                		/* 납기내일자 */
	                		sendMap.setMap("NAP_BFDATE", mfCmd521002List.getMap("DUE_DT"));
	                		/* 납기후일자 */
	                		sendMap.setMap("NAP_AFDATE", mfCmd521002List.getMap("DUE_F_DT"));
	                		
	                	} else {
	                		/* 납기내금액 */
	                		sendMap.setMap("NAP_BFAMT", cUtil.getNapBfAmt(Long.parseLong(mfCmd521002List.getMap("MNTX").toString())			//본세
											                		    , Long.parseLong(mfCmd521002List.getMap("MNTX_ADTX").toString())  	//본세 가산금
																		, Long.parseLong(mfCmd521002List.getMap("CPTX").toString())			//도시계회세
																		, Long.parseLong(mfCmd521002List.getMap("CPTX_ADTX").toString())	//도시계회세 가산금
																		, Long.parseLong(mfCmd521002List.getMap("CFTX").toString())			//공동시설세
																		, Long.parseLong(mfCmd521002List.getMap("CFTX_ADTX").toString())	//공동시설세 가산금
																		, Long.parseLong(mfCmd521002List.getMap("LETX").toString())			//교육세
																		, Long.parseLong(mfCmd521002List.getMap("LETX_ADTX").toString())	//교육세 가산금
																		, Long.parseLong(mfCmd521002List.getMap("ASTX").toString())			//농특세
																		, Long.parseLong(mfCmd521002List.getMap("ASTX_ADTX").toString())));	//농특세 가산금
	                		napGubun = "B";
	                		
	                		/* 납기후금액 (납기후 금액과 납기내 금액은 같다) */
	                		sendMap.setMap("NAP_AFAMT", sendMap.getMap("NAP_BFAMT"));
                            /* 실제 납부해야할 금액 (납기후 금액과 납기내 금액은 같다) */
                            sendMap.setMap("NAP_AMT", sendMap.getMap("NAP_BFAMT"));
	                		/* 납기내일자 */
	                		sendMap.setMap("NAP_BFDATE", mfCmd521002List.getMap("DUE_DT"));
	                		/* 납기후일자 */
	                		// sendMap.setMap("DUE_F_DT", mfCmd521002List.getMap("DUE_DT")); -- 납기후일자 처리오류 수정--2011.09.14 --김대완---
	                		sendMap.setMap("NAP_AFDATE", mfCmd521002List.getMap("DUE_F_DT"));
	                		
	                	}
		                /*
		                 * 지방세 뒷부분 공통
		                 */
	                	if (mfCmd521002List.getMap("DLQ_DIV").equals("2")) {
	                	    sendMap.setMap("TAX_DIV", "0");   //체납분일때
	                	}
		                /* 과세사항 + 과세표준설명  */
		                sendMap.setMap("TAX_DESC", mfCmd521002List.getMap("MLGN"));
		                /* 예비정보 2 */
		                sendMap.setMap("FILLER1", "");
		                /*
		                 * 지방세 끝
		                 */
	                }
					
				} else { 
					/* 조회건수가 없는 경우 전문생성
					 * */
					resCode = "5020";  /*조회내역없음*/
					
				} 
			}
			
			log.info(cUtil.msgPrint("7", "","DP521002 chk_df_521002()", resCode));
			
        } catch (Exception e) {
        	
        	resCode = "9090";  
        	
			e.printStackTrace();
			log.error("============================================");
			log.error("== chk_df_521002 Exception(시스템) ");
			log.error("============================================");
		}
        
        /*오류인경우 고대로 돌려줌*/
        if(!resCode.equals("0000")) {
        	sendMap = mf;
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

