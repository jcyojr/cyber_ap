/**
 *  주시스템명 : 사이버지방세청 고도화
 *  업  무  명 : 세외수입 고지내역 상세조회 전문
 *  기  능  명 : 편의점(더존)-사이버세청 
 *               업무처리
 *               편의점(더존)에서 수신된 전문에 대해서 응답전문데이트를 생성하고 
 *               조회결과를 저장한다.
 *  클래스  ID : Df271002Service
 *  변경  이력 :
 * ------------------------------------------------------------------------
 *  작성자     소속  		 일자      Tag   내용
 * ------------------------------------------------------------------------   
 * 임창섭   유채널(주)    2013.08.13   %01%  신규작성
 *  
 */
package com.uc.bs.cyber.service.df271002;

import java.math.BigDecimal;
import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.dao.DuplicateKeyException;

import com.uc.core.MapForm;
import com.uc.core.spring.service.IbatisBaseService;
import com.uc.bs.cyber.CbUtil;
import com.uc.bs.cyber.service.df201002.Df201002FieldList;

/**
 * @author Administrator
 *
 */
public class Df271002Service {

	protected Log log = LogFactory.getLog(this.getClass());
	
	private ApplicationContext appContext = null;
	
	private IbatisBaseService sqlService_cyber = null;
	
	private Df201002FieldList dfField = null;
	
	CbUtil cUtil = null;
	
	/**
	 * 생성자
	 */
	public Df271002Service(ApplicationContext appContext) {
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
	 * 세외수입 고지내역 상세조회 처리...
	 * 상세조회 화면 즉 편의점(더존)에서 5000이라는 기납부 라고 에러가 
	 * 발생하면 편의점(더존)에 문의하여 자료를 삭제하도록 한다.
	 * */
	public byte[] chk_df_271002(MapForm headMap, MapForm mf) throws Exception {
		
		MapForm sendMap = new MapForm();
		
		/*세외수입 고지내역조회 시 사용 */
		String resCode = "0000";      /*응답코드*/
		String tx_id = "SN2601";      /*부산시업무구분*/
		String sg_code = "26";        /*부산시기관코드*/
		
		try{

			cUtil = CbUtil.getInstance();
			
			log.info(cUtil.msgPrint("2", "S","DP271002 chk_df_271002()", resCode));
						
			/*전문체크*/
			
			/*기관*/
			if(!headMap.getMap("SIDO_CODE").equals(sg_code)){
				resCode = "1000";
			}
			/*발행기관코드*/
			if(!headMap.getMap("TX_ID").equals(tx_id)){
				resCode = "1000";
			}
			
			/**
			 * 전자납부번호가 19 임..17인경우 17 + "00" 붙어서 전문이 수신됨...
			 * 따라서 이런 오류를 방지하기 위함
			 */
			if(mf.getMap("EPAY_NO").toString().length() == 17) {
				mf.setMap("EPAY_NO", mf.getMap("EPAY_NO").toString() + "00");
			}
            
            if(mf.getMap("EPAY_NO").toString().length() != 19) {
                resCode = "2000";
            }
            
            if(mf.getMap("TAX_NO").toString().length() != 31) {
                resCode = "2000";
            }
            
            mf.setMap("SGG_COD" , mf.getMap("TAX_NO").toString().substring(2, 5));    //구청코드
            mf.setMap("ACCT_COD", mf.getMap("TAX_NO").toString().substring(6, 8));    //회계코드
            mf.setMap("TAX_ITEM", mf.getMap("TAX_NO").toString().substring(8, 14));   //과목/세목코드
            mf.setMap("TAX_YY"  , mf.getMap("TAX_NO").toString().substring(14, 18));  //과세년도
            mf.setMap("TAX_MM"  , mf.getMap("TAX_NO").toString().substring(18, 20));  //과세월
            mf.setMap("TAX_DIV" , mf.getMap("TAX_NO").toString().substring(20, 21));  //기분코드
            mf.setMap("HACD"    , mf.getMap("TAX_NO").toString().substring(21, 24));  //행정동코드
            mf.setMap("TAX_SNO" , mf.getMap("TAX_NO").toString().substring(24, 30));  //과세번호
            
            /*세외수입 전자수납의 기납부내역의 존재여부를 확인한다.*/
            ArrayList<MapForm>  dfAl272001List = sqlService_cyber.queryForList("DF272001.SELECT_BNON_PAY_LIST", mf);
            
            int payCnt = dfAl272001List.size();
            
            log.debug("totCnt = " + payCnt);
            
            if(payCnt == 0) {
                resCode = "0000";   /*납부내역없음*/
            } else {
                /*납부내역 있음*/
                MapForm mfCmd272001List = dfAl272001List.get(0);
                
                if ( mfCmd272001List == null || mfCmd272001List.isEmpty() ) {
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
            
			if(mf.getMap("EPAY_NO").toString().length() == 19 && resCode.equals("0000")){
				
				//로깅작업용 : 조회
//				mf.setMap("TX_GB"  , "1");
//				mf.setMap("PAY_DT" , CbUtil.getCurrentDate());
				
				ArrayList<MapForm>  dfCmd271002List  =  sqlService_cyber.queryForList("DF271002.SELECT_BNON_SEARCH_LIST", mf);
				
				log.debug("EPAY_NO = [" + mf.getMap("EPAY_NO") + "]");
//				log.debug("JUMIN_NO = [" + mf.getMap("JUMIN_NO") + "]");
				log.debug("TAX_NO = [" + mf.getMap("TAX_NO") + "]");
				log.debug("dfCmd271002List.size() = [" + dfCmd271002List.size() + "]");
				log.debug("resCode = [" + resCode + "]");
				
				int levy_cnt = dfCmd271002List.size();
				
				if (levy_cnt <= 0) {
					resCode = "5020"; /*조회내역없음*/
				} else if (levy_cnt > 1) {
					resCode = "5060"; /*고지서 2건 이상*/
				} else {
					
					for ( int al_cnt = 0;  al_cnt < levy_cnt;  al_cnt++)   {
						
						MapForm mfCmd271002List  =  dfCmd271002List.get(al_cnt);
												
						if ("2".equals(mfCmd271002List.getMap("TAX_GBN"))) {
			                log.debug("SGG_COD  = [" + mf.getMap("SGG_COD") + "]");
			                log.debug("ACCT_COD = [" + mf.getMap("ACCT_COD") + "]");
			                log.debug("TAX_ITEM = [" + mf.getMap("TAX_ITEM") + "]");
			                log.debug("TAX_YY   = [" + mf.getMap("TAX_YY") + "]");
			                log.debug("TAX_MM   = [" + mf.getMap("TAX_MM") + "]");
			                log.debug("TAX_DIV  = [" + mf.getMap("TAX_DIV") + "]");
			                log.debug("HACD     = [" + mf.getMap("HACD") + "]");
			                log.debug("TAX_SNO  = [" + mf.getMap("TAX_SNO") + "]");
			            }
						
						String napGubun = cUtil.getNapkiGubun((String)mfCmd271002List.getMap("DUE_DT"));
					
						/* 납세자명이 80바이트기 때문에 모든 글자가 한글이 경우 40자 이하로 자른다 */
						if(mfCmd271002List.getMap("REG_NM").toString().length() > 0)
						    mfCmd271002List.setMap("REG_NM", CbUtil.ksubstr(mfCmd271002List.getMap("REG_NM").toString(), 40));
						
						/* 부과내역이 130바이트기 때문에 모든 글자가 한글이 경우 65자 이하로 자른다 */
						if(mfCmd271002List.getMap("TAX_DESC").toString().length() > 0)
						    mfCmd271002List.setMap("TAX_DESC", CbUtil.ksubstr(mfCmd271002List.getMap("TAX_DESC").toString(), 65));
						
						/*조회후 전문 세팅*/
						sendMap.setMap("TAX_GB"                 ,  mf.getMap("TAX_GB"));
						sendMap.setMap("TAX_NO"                 ,  mf.getMap("TAX_NO"));
						sendMap.setMap("EPAY_NO"                ,  mf.getMap("EPAY_NO"));
						sendMap.setMap("NAP_NAME"               ,  mfCmd271002List.getMap("REG_NM"));
						sendMap.setMap("SGG_NAME"               ,  mfCmd271002List.getMap("SGNM"));
						sendMap.setMap("TAX_NAME"               ,  CbUtil.ksubstr(mfCmd271002List.getMap("TAX_NM").toString(), 50)); /*50자를 넘지 않게*/
						sendMap.setMap("TAX_YM"                 ,  mfCmd271002List.getMap("TAX_YM"));
						
						if("1".equals(mfCmd271002List.getMap("BUGWA_STAT"))) {
						    sendMap.setMap("TAX_DIV"                ,  mfCmd271002List.getMap("TAX_DIV"));
						} else {
						    sendMap.setMap("TAX_DIV"                , "0") ;
						}
						sendMap.setMap("NAP_BFAMT"              ,  mfCmd271002List.getMap("AMT"));
						sendMap.setMap("NAP_AFAMT"              ,  mfCmd271002List.getMap("AFT_AMT"));
						
						if (napGubun.equals("B")) {
						    sendMap.setMap("NAP_AMT"              ,  mfCmd271002List.getMap("AMT"));
						} else {
						    sendMap.setMap("NAP_AMT"              ,  mfCmd271002List.getMap("AFT_AMT"));
						}
						sendMap.setMap("NAP_BFDATE"             ,  mfCmd271002List.getMap("DUE_DT"));
						sendMap.setMap("NAP_AFDATE"             ,  mfCmd271002List.getMap("DUE_F_DT"));
						sendMap.setMap("TAX_DESC"               ,  mfCmd271002List.getMap("TAX_DESC"));
						sendMap.setMap("FILLER1"                 ,  " ");
						
						//logging...
//						try{
//							//로깅작업용 : 수납
//							mf.setMap("TAX_NO"      ,  mfCmd271002List.getMap("TAX_NO"));
//							mf.setMap("JUMIN_NO"    ,  mfCmd271002List.getMap("REG_NO"));
//							mf.setMap("PAY_DT"      ,  CbUtil.getCurrentDate());
//							mf.setMap("NAPBU_AMT"   ,  mfCmd271002List.getMap("AMT"));
//							mf.setMap("TX_GB"       ,  mfCmd271002List.getMap("TAX_GB"));
//							
//							sqlService_cyber.queryForUpdate("DF271002.INSERT_TX2421_TB_LOG", mf); //버스전용 결제원수납 로그
//							
//						}catch (Exception e) {
//							
//							if (e instanceof DuplicateKeyException){
//								log.info("조회 TX2421_TB 테이블에 이미 기록된 데이터");
//							} else {
//								log.error("오류 데이터 = " + mf.getMaps());
//							    log.error("Logging  failed!!!");	
//							}
//						}
					}
				}
			
			} else {
				resCode = "2000";  /*전자납부번호, 수용가번호 오류*/
			}
			
			log.info(cUtil.msgPrint("2", "","DP271002 chk_df_271002()", resCode));
			
        } catch (Exception e) {
			
        	resCode = "2000";  //Error : 전자납부번호, 수용가번호 오류
        	
			e.printStackTrace();
			log.error("============================================");
			log.error("== chk_df_271002 Exception(시스템) ");
			log.error("============================================");
		}
        
        /*정상적인 처리가 안되었다면 온 전문 그대로 돌려준다.*/
        if (!resCode.equals("0000")) {
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
