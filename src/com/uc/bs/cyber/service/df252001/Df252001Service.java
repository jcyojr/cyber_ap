/**
 *  주시스템명 : 사이버지방세청 고도화
 *  업  무  명 : 편의점(더존)-상하수도요금 납부결과 통지 / 납부예약
 *  기  능  명 : 편의점(더존)-사이버세청 
 *               업무처리
 *               
 *  클래스  ID : Df252001Service
 *  변경  이력 :
 * ------------------------------------------------------------------------
 *  작성자     소속  		 일자      Tag   내용
 * ------------------------------------------------------------------------   
 * 임창섭   유채널(주)    2013.08.13   %01%  신규작성
 *  
 */
package com.uc.bs.cyber.service.df252001;

import java.math.BigDecimal;
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
public class Df252001Service {

	protected Log log = LogFactory.getLog(this.getClass());
	
	private ApplicationContext appContext = null;
	
	private IbatisBaseService sqlService_cyber = null;
		
	private Df202001FieldList dfField = null;
	
	private int[] svcTms = new int[2];
	
	CbUtil cUtil = null;
	
	/**
	 * 생성자
	 */
	public Df252001Service(ApplicationContext appContext) {
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
	 * 상하수도요금 납부결과 통지
	 * */
	public byte[] chk_df_252001(MapForm headMap, MapForm mf) throws Exception {
				
		CbUtil cUtil = null;
		
		/*상하수도요금 고지내역조회 시 사용 */
		String resCode = "0000";      /*응답코드*/
		String tx_id = "SN2601";   /*부산시업무구군코드*/
		String sg_code = "26";        /*부산시기관코드*/
		
		int curTime = 0;

		try{
			
			/*서비스 시간*/
			svcTms[0] = 0700;
			svcTms[1] = 2200;
			
			cUtil = CbUtil.getInstance();

			log.info(cUtil.msgPrint("3", "S","DP252001 chk_df_252001()", resCode));
			
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
	        } else if (mf.getMap("EPAY_NO").toString().length() == 19) {
	            
	        } else {
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

			if(log.isDebugEnabled()) {
				log.debug("-------------------------------------------------------------");
				log.debug("   □ 납세번호 : " + mf.getMap("TAX_NO").toString());
				log.debug("   □ 전자납부번호 : " + mf.getMap("EPAY_NO").toString());
				log.debug("   □ 수납기관 분류 : " + headMap.getMap("BANK_CODE").toString());
				log.debug("   □ 납부금액 : " + mf.getMap("NAPBU_AMT").toString());
				log.debug("   □ 납부일자 : " + mf.getMap("NAPBU_DATE").toString());
				log.debug("-------------------------------------------------------------");
			}
			
			@SuppressWarnings("unused")
			String searchgubun = "";
			int rcpCount       = 0;
			int totalCount     = 0;
			
			/*편의점(더존)에서 수신한 전문을 DB 에 맵핑하기 위함.*/
	        mf.setMap("PRT_NO"     , CbUtil.getCurrent("yyyyMMdd").substring(0, 2) + mf.getMap("TAX_NO").toString().substring(0, 12));

			String napkiGubun = "";    // 납기내후 구분 (B:납기내, A:납기후)
	        String NAPAMT = "";        // 납부금액
			
	        /*기납부내역 존재여부 확인*/
	        try {
    			ArrayList<MapForm>  dfAl252001List = sqlService_cyber.queryForList("DF251002.SELECT_SHSD_PAY_LIST", mf);
    	        
    			rcpCount = dfAl252001List.size();
    			
    	        if (rcpCount < 1){
    	        	resCode = "0000"; //납부내역없음
    	        } else {
    	        	/*납부내역 있음*/
    				MapForm mfCmd252001List = dfAl252001List.get(0);
    				
    				if ( mfCmd252001List == null || mfCmd252001List.isEmpty() ) {
    					/*납부내역없음*/
    					resCode = "0000";
    				} else {
    					/*납부내역이 있는 경우*/
    					if(mfCmd252001List.getMap("SNTG").equals("9")) {  /*취소*/
    						resCode = "0000";
    					} else {
    						resCode = "5000";  /*이미수납된 고지분*/
    					}
    				}
    	        }
	        } catch (Exception e) {
	            resCode = "8000";
                log.info("상하수도요금 기납부여부 조회 Exception! [" + mf.getMap("ETAX_NO") + "]");
	        }
		        
	        /*납부내역이 존재하지 않거나, 납부취소된 경우에 대한 납부처리*/
	        if(resCode.equals("0000")) {
	        	
	        	/*부과내역 조회*/
	            ArrayList<MapForm>  alDfLevyList = null;
	            try {
	                alDfLevyList = sqlService_cyber.queryForList("DF251002.SELECT_SHSD_LEVY_LIST", mf);
	            } catch(Exception e) {
	                resCode = "8000";
	                log.info("상하수도요금 부과내역 조회 Exception! [" + mf.getMap("ETAX_NO") + "]");
                }
	            
				totalCount = alDfLevyList.size();
				
				if (totalCount < 1)
					resCode = "5020";        // Error : 고지내역없음
		        else if (totalCount > 1)
		        	resCode = "3000";        // Error : 고지내역 2건 이상
		        else {                       // 고지내역이 1건인 경우에만 처리
		        	
		            String CURDATE = (String) CbUtil.getCurrentDate().substring(0, 8);
		            log.debug("CURDATE = " + CURDATE );
		            
		        	MapForm mpDfLevyList = alDfLevyList.get(0);
		        	
			        String prt_del1 = (String)mpDfLevyList.getMap("DUE_DT");         //납기내 납기일;
			        String prt_del2 = (String)mpDfLevyList.getMap("DUE_F_DT");       //납기후 납기일;
			        
			        if (CbUtil.getInt(CURDATE) > CbUtil.getInt(prt_del2)) {
                        resCode = "5010";   //납기일자가 지난 고지분
                    } else {

    	                //납기내(B), 납기후(A) 체크
    	                napkiGubun = cUtil.getNapGubun(prt_del1, prt_del2);
    
    	                //업무처리변수 Set
    	                long prt_amt         = ((BigDecimal)mpDfLevyList.getMap("SUM_B_AMT")).longValue();    /*납기내*/
    	                long prt_amt2        = ((BigDecimal)mpDfLevyList.getMap("SUM_F_AMT")).longValue();    /*납기후*/
    	                
    	                if(napkiGubun.equals("B"))
    	                {
    	                    NAPAMT = Long.toString(prt_amt);   //납기내금액
    	                }
    	                else if(napkiGubun.equals("A"))
    	                {
    	                    NAPAMT = Long.toString(prt_amt2);  //납기후금액
    	                }
    	                
    	                if (NAPAMT.equals((String) mf.getMap("NAPBU_AMT")))
    	            	    resCode = "0000";        // 정상
    	                else
    	            	    resCode = "4000";        // 납부금액 틀림
    	                
    	    	        // 수납업무처리
    	    	        // 1. 부과내역 update
    	    	        // 2. 수납테이블 insert
    	    			if (resCode.equals("0000")) {
    	    				
    	    				/*전문정보조립*/
    	    				/*전자수납테이블에 수납순번을 구한다. 중복처리가 가능하므로...*/
    	    				mpDfLevyList.setMap("PAY_CNT"     , sqlService_cyber.getOneFieldInteger("DF251002.TX3211_TB_MaxPayCnt", mpDfLevyList));
    	    				mpDfLevyList.setMap("SUM_RCP"     , NAPAMT);                             /*납부금액*/
    	    				mpDfLevyList.setMap("PAY_DT"      , mf.getMap("NAPBU_DATE"));            /*수납일자*/
    	    				mpDfLevyList.setMap("SNTG"        , "1");                                /*납부상태*/
    	    				mpDfLevyList.setMap("SNSU"        , mf.getMap("NAPBU_SNSU"));                                /*수납매체 1 금융결제원, 2 카드, 3:은행, 7:이체*/
    	    				mpDfLevyList.setMap("RSN_YN"      , "N");                                /*납부구분 Q예약  R조회*/
    	    				/*mpDfLevyList.setMap("TRTG"        , "0");                                자료전송상태*/
    	    				mpDfLevyList.setMap("BANK_CD"     , headMap.getMap("BANK_CODE"));             /*납부기관 -카드*/
    	    				mpDfLevyList.setMap("BRC_NO"      , CbUtil.rPadString(headMap.getMap("BANK_CODE").toString(), 7, '0'));  /*수납기관코드*/
    						mpDfLevyList.setMap("TMSG_NO"     , headMap.getMap("BCJ_NO"));           /*출금은행_전문관리번호*/
    	    				
    						try {
    						    
    		    				int intLog = sqlService_cyber.queryForUpdate("DF251002.INSERT_TX3211_TB_EPAY", mpDfLevyList);
    		    				
    		    				if(intLog > 0) {
    		    					
    		    					sqlService_cyber.queryForUpdate("DF251002.UPDATE_TX3111_TB_NAPBU_INFO", mpDfLevyList);
    		    					
    		    					log.info("◆ 상하수도요금 전자납부처리 완료! [" + mf.getMap("EPAY_NO") + "]");
    		    					
    		    					//MapForm mpSMS = new MapForm();	
    								
    		    					/*상하수도는 주민번호가 없으니..Select 한다.*/
    								//mpSMS.setMap("REG_NO"  , "");
    								//mpSMS.setMap("TAX_ITEM", mpKfLevyList.getMap("TAX_ITEM"));
    								//mpSMS.setMap("SUM_RCP" , NAPAMT);
    								
    								//sqlService_cyber.procedure("CODMBASE.SMSCALL", mpSMS);
    		    				} 
    						}catch(Exception e) {
    						    resCode = "8000";
    						    log.info("상하수도요금 전자납부 처리 Exception! [" + mf.getMap("ETAX_NO") + "]");
    	    				}
    	    			}
                    }
		        }
	        }
			
			log.info(cUtil.msgPrint("3","","DP252001", resCode));
			log.info("=============================================");
		
        } catch (Exception e) {
            resCode = "9090";
            
			e.printStackTrace();
			log.error("============================================");
			log.error("== chk_df_252001 Exception(시스템) ");
			log.error("============================================");
		}
        
        /*수신전문을 같이 보낸다.*/
        
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
