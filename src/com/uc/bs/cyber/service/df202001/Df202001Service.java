/**
 *  주시스템명 : 사이버지방세청 고도화
 *  업  무  명 : 편의점(더존)-환경개선부담금 납부결과통지 / 예약신청 
 *  기  능  명 : 편의점(더존)-사이버세청 
 *               업무처리
 *               
 *  클래스  ID : Df202001Service
 *  변경  이력 :
 * ------------------------------------------------------------------------
 *  작성자     소속  		 일자      Tag   내용
 * ------------------------------------------------------------------------   
 * 임창섭   유채널(주)    2013.08.13   %01%  신규작성
 *  
 */
package com.uc.bs.cyber.service.df202001;

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
public class Df202001Service {

	protected Log log = LogFactory.getLog(this.getClass());
	
	private ApplicationContext appContext = null;
	
	private IbatisBaseService sqlService_cyber = null;
		
	private Df202001FieldList dfField = null;
	
	private int[] svcTms = new int[2];
	
	CbUtil cUtil = null;
	
	/**
	 * 생성자
	 */
	public Df202001Service(ApplicationContext appContext) {
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
	 * 환경개선부담금 납부결과 통지 / 예약납부
	 * */
	public byte[] chk_df_202001(MapForm headMap, MapForm mf) throws Exception {
		
		/*환경개선부담금 고지내역조회 시 사용 */
		String resCode = "0000";      /*응답코드*/
		String tx_id = "SN2601";   /*부산시지로코드*/
		String sg_code = "26";        /*부산시기관코드*/
		int curTime = 0;

		try{
			
			/*서비스 시간*/
			svcTms[0] = 0700;
			svcTms[1] = 2200;
			
			cUtil = CbUtil.getInstance();
			
			log.info(cUtil.msgPrint("3", "S","DP202001 chk_df_202001()", resCode));
			
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
            
			/*납부내역이 존재하지 않고 취소된 경우 납부처리*/
			if(resCode.equals("0000")){
				
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
	            
	            /*기납부 여부 확인*/
	            ArrayList<MapForm>  dfAl202001List = sqlService_cyber.queryForList("DF202001.SELECT_ENV_PAY_LIST", mf);
	            
	            log.info("환경개선부담금 기납부여부 조회 완료! [" + mf.getMap("ETAX_NO") + "]");
	            
	            int totCnt = dfAl202001List.size();
	            
	            if(totCnt == 0) {
	                resCode = "0000";   /*납부내역없음*/
	            } else {
	                
	                /*내역 있음*/
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
	            
				/*부과내역리스트 검색*/
				ArrayList<MapForm>  alDfLevyList = sqlService_cyber.queryForList("DF202001.SELECT_ENV_SEARCH_LIST", mf);
				
				log.info("환경개선부담금 부과내역 조회 완료! [" + mf.getMap("ETAX_NO") + "]");
				
				if(alDfLevyList.size() == 0) {
					
					resCode = "5020";  /*고지내역없음*/
					
				} else if(alDfLevyList.size() > 1) {
					
					resCode = "5060";  /*고지내역2건, 1건인것만 처리*/
					
				} else {
					
					MapForm mpDfLevyList = alDfLevyList.get(0);
					
					/*전문정보조립*/
					/*전자수납테이블에 수납순번을 구한다. 중복처리가 가능하므로...*/
					mpDfLevyList.setMap("PAY_CNT" , sqlService_cyber.getOneFieldInteger("DF202001.TX2231_TB_MaxPayCnt", mpDfLevyList));
					mpDfLevyList.setMap("SUM_RCP" , mf.getMap("NAPBU_AMT"));   /*납부금액*/
					mpDfLevyList.setMap("PAY_DT"  , mf.getMap("NAPBU_DATE"));  /*수납일자*/
					mpDfLevyList.setMap("SNTG"    , "1");                      /*납부상태*/
					mpDfLevyList.setMap("SNSU"    , mf.getMap("NAPBU_SNSU"));                      /*수납수단 1:금융결제원 2:카드 3:은행*/
                    mpDfLevyList.setMap("BANK_CD" , headMap.getMap("BANK_CODE"));    /*납부기관*/
                    mpDfLevyList.setMap("BRC_NO"  , CbUtil.rPadString(headMap.getMap("BANK_CODE").toString(), 7, '0'));   /*(수납)납부기관*/
                    mpDfLevyList.setMap("RSN_YN"  , "N");                      /*납부구분 Y:예약  N:조회*/
					mpDfLevyList.setMap("TRTG"    , "0");                      /*자료전송상태*/
					mpDfLevyList.setMap("TMSG_NO" , headMap.getMap("BCJ_NO")); /*출금은행_전문관리번호*/
					
					int intLog = sqlService_cyber.queryForUpdate("DF202001.INSERT_TX2231_TB_EPAY", mpDfLevyList);
					
					if(intLog > 0) {
						
						sqlService_cyber.queryForUpdate("DF202001.UPDATE_TX2132_TB_NAPBU_INFO", mpDfLevyList);
						
						log.info("◆환경개선부담금 전자납부처리 완료! [" + mf.getMap("ETAX_NO") + "]");
						
//						MapForm mpSMS = new MapForm();	
//						
//						mpSMS.setMap("REG_NO"  , mpDfLevyList.getMap("REG_NO"));
//						mpSMS.setMap("TAX_ITEM", mpDfLevyList.getMap("TAX_ITEM"));
//						mpSMS.setMap("SUM_RCP" , mf.getMap("NAPBU_AMT"));
//						
//						try {
//							sqlService_cyber.procedure("CODMBASE.SMSCALL", mpSMS);
//						}catch (Exception e) {
//							log.info("SMS 등록데이터 = " + mpSMS.getMaps());
//							log.info("SMS 등록오류 발생");
//						}
						
					}
				}
			}
			
			log.info(cUtil.msgPrint("3", "","DP202001 chk_df_202001()", resCode));
			
        } catch (Exception e) {
			
        	resCode = "9090";  //Error : 
        	
			e.printStackTrace();
			log.error("============================================");
			log.error("== chk_df_201002 Exception(시스템) ");
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
