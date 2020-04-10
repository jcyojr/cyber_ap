/**
 *  주시스템명 : 사이버지방세청 고도화
 *  업  무  명 : 환경개선부담금 고지내역 상세조회 전문
 *  기  능  명 : 편의점(더존)-사이버세청 
 *               업무처리
 *               편의점(더존)에서 수신된 전문에 대해서 응답전문데이트를 생성하고 
 *               조회결과를 저장한다.
 *  클래스  ID : Df201002Service
 *  변경  이력 :
 * ------------------------------------------------------------------------
 *  작성자     소속  		 일자      Tag   내용
 * ------------------------------------------------------------------------   
 * 임창섭   유채널(주)    2013.08.13   %01%  신규작성
 *  
 */
package com.uc.bs.cyber.service.df201002;

import java.math.BigDecimal;
import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;

import com.uc.core.MapForm;
import com.uc.core.spring.service.IbatisBaseService;
import com.uc.bs.cyber.CbUtil;

/**
 * @author Administrator
 *
 */
public class Df201002Service {

	protected Log log = LogFactory.getLog(this.getClass());
	
	private ApplicationContext appContext = null;
	
	private IbatisBaseService sqlService_cyber = null;
		
	private Df201002FieldList dfField = null;
	
	CbUtil cUtil = null;
	
	/**
	 * 생성자
	 */
	public Df201002Service(ApplicationContext appContext) {
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
	 * 환경개선부담금 고지내역 상세조회 처리...
	 * */
	public byte[] chk_df_201002(MapForm headMap, MapForm mf) throws Exception {
		
				
		MapForm sendMap = new MapForm();
		
		/*환경개선부담금 고지내역조회 시 사용 */
		String resCode = "0000";      /*응답코드*/
		String tx_id = "SN2601";      /*부산시코드*/
		String sg_code = "26";        /*부산시기관코드*/
		String napkiGubun = "";

		long NAP_BFAMT = 0;
		long NAP_AFAMT = 0;
		long NAP_AMT = 0;

		try{
			
			cUtil = CbUtil.getInstance();
			
			log.info(cUtil.msgPrint("2", "S","DP201002 chk_df_201002()", resCode));
			

			/*전문체크*/
			
			/*기관*/
			if(!headMap.getMap("SIDO_CODE").equals(sg_code)){
				resCode = "1000";
			}
			/*발행기관코드*/
			if(!headMap.getMap("TX_ID").equals(tx_id)){
				resCode = "1000";
			}
			
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
            
            ArrayList<MapForm>  dfAl202001List = sqlService_cyber.queryForList("DF202001.SELECT_ENV_PAY_LIST", mf);
            
            int payCnt = dfAl202001List.size();
            
            if(payCnt == 0) {
                resCode = "0000";   /*납부내역없음*/
            } else {
                
                /*납부내역 있음*/
                MapForm mfCmd202001List = dfAl202001List.get(0);
                
                if ( mfCmd202001List == null  ||  mfCmd202001List.isEmpty() )   {
                    /*납부내역없음*/
                    resCode = "0000";
                } else {
                    /*납부취소내역이 있는 경우*/
                    if(mfCmd202001List.getMap("SNTG").equals("9")) {  /*취소*/
                        resCode = "0000";
                    } else {
                        resCode = "5000";  /*납부기수신*/
                    }
                }
            }
            
			log.debug("EPAY_NO.length : " + mf.getMap("EPAY_NO").toString().length());
			
			if(mf.getMap("EPAY_NO").toString().length() == 19 && resCode.equals("0000")){
				
				ArrayList<MapForm>  dfCmd201002List  =  sqlService_cyber.queryForList("DF201002.SELECT_ENV_SEARCH_LIST", mf);
				
				int levyCnt = dfCmd201002List.size();
				
				if ( levyCnt <= 0) {
					resCode = "5020";  /*조회내역없음*/
				} else if (levyCnt > 1) {
					resCode = "5060";  /*고지서 2건 이상*/
				} else {
					
					MapForm mfCmd201002List  =  dfCmd201002List.get(0);
					
					MapForm mfSendData = new MapForm();
					
					/*조회 내역으로 전문세팅*/
					mfSendData.setMap("TAX_GB"               ,  mf.getMap("TAX_GB")  );        /*세금구분코드*/
					mfSendData.setMap("TAX_NO"               ,  mf.getMap("TAX_NO")  );        /*납세번호*/
					mfSendData.setMap("EPAY_NO"              ,  mf.getMap("EPAY_NO")  );       /*전자납부번호*/
					mfSendData.setMap("NAP_NAME"             ,  mfCmd201002List.getMap("REG_NM")  );  /*납부자 성명 */
                    mfSendData.setMap("SGG_NAME"             ,  mfCmd201002List.getMap("SGNM")  );    /*과세기관(시군구) 명    */
                    mfSendData.setMap("TAX_NAME"             ,  mfCmd201002List.getMap("TAX_NM")  );  /*과목/세목명             */
                    mfSendData.setMap("TAX_YM"               ,  (String)mfCmd201002List.getMap("TAX_YY")  +  (String)mfCmd201002List.getMap("TAX_MM")  );  /*부과년월*/ 
					
				    napkiGubun = cUtil.getNapGubun((String)mfCmd201002List.getMap("DUE_DT"), (String)mfCmd201002List.getMap("DUE_F_DT"));
				    
				    if(napkiGubun.equals("B")){//납기내
				    	
				    	if(mfCmd201002List.getMap("DUE_DT").equals(mfCmd201002List.getMap("DUE_F_DT"))){ 
				    		
				    		/*체납*/
				    		NAP_BFAMT = ((BigDecimal)mfCmd201002List.getMap("ENV_AMT")).longValue()+((BigDecimal)mfCmd201002List.getMap("ADD_AMT")).longValue();   // 납기내금액
		                    NAP_AFAMT = ((BigDecimal)mfCmd201002List.getMap("ENV_AMT")).longValue()+((BigDecimal)mfCmd201002List.getMap("ADD_AMT")).longValue();   // 납기후금액
		                    NAP_AMT = NAP_BFAMT;
		                    mfSendData.setMap("TAX_DIV"   ,  "0" );  /*기분                    */
				    	} else {
				    		/*납기내*/
				    		NAP_BFAMT = ((BigDecimal)mfCmd201002List.getMap("ENV_AMT")).longValue();   // 납기내금액
		                    NAP_AFAMT = ((BigDecimal)mfCmd201002List.getMap("ENV_AMT")).longValue()+((BigDecimal)mfCmd201002List.getMap("ADD_AMT")).longValue();  // 납기후금액
		                    NAP_AMT = NAP_BFAMT;
		                    mfSendData.setMap("TAX_DIV"   ,  mfCmd201002List.getMap("TAX_DIV") );  /*기분                    */
				    	}
				    	
				    } else if(napkiGubun.equals("A")) {
				    	/*납기후*/
				    	NAP_BFAMT = ((BigDecimal)mfCmd201002List.getMap("ENV_AMT")).longValue();   // 납기내금액
	                    NAP_AFAMT = ((BigDecimal)mfCmd201002List.getMap("ENV_AMT")).longValue()+((BigDecimal)mfCmd201002List.getMap("ADD_AMT")).longValue();  // 납기후금액
	                    NAP_AMT = NAP_AFAMT;
	                    mfSendData.setMap("TAX_DIV"    ,  mfCmd201002List.getMap("TAX_DIV") );  /*기분                    */
				    }
				    mfSendData.setMap("NAP_BFAMT"            ,  NAP_BFAMT   );
					mfSendData.setMap("NAP_AFAMT"            ,  NAP_AFAMT   );
					mfSendData.setMap("NAP_AMT"              ,  NAP_AMT     );
				    mfSendData.setMap("NAP_BFDATE"           ,  mfCmd201002List.getMap("DUE_DT")  );    /*납기내 기한*/
				    mfSendData.setMap("NAP_AFDATE"           ,  mfCmd201002List.getMap("DUE_F_DT")  );  /*납기후 기한*/
				    mfSendData.setMap("TAX_DESC"             , mfCmd201002List.getMap("MLGN_IF1") + " " + mfCmd201002List.getMap("MLGN_IF2")  );
				    mfSendData.setMap("FILLER1"              ,  " "  );  /*예비*/
				    
				    /*전문맵핑*/
				    sendMap = mfSendData;
				}
				
			} else {
				resCode = "2000";  /*전자납부번호, 수용가번호 오류*/
			}
			
			log.info(cUtil.msgPrint("2", "","DP201002 chk_df_201002()", resCode));
			
        } catch (Exception e) {
			
        	resCode = "2000";  //Error : 전자납부번호, 수용가번호 오류
        	
			e.printStackTrace();
			log.error("============================================");
			log.error("== chk_df_201002 Exception(시스템) ");
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
