/**
 *  주시스템명 : 사이버지방세청 고도화
 *  업  무  명 : 결제원-상하수도요금 납부결과 통지 / 납부예약
 *  기  능  명 : 결제원-사이버세청 
 *               업무처리
 *               
 *  클래스  ID : Kf252001Service
 *  변경  이력 :
 * ------------------------------------------------------------------------
 *  작성자     소속  		 일자      Tag   내용
 * ------------------------------------------------------------------------   
 * 송동욱   유채널(주)    2011.06.21   %01%  신규작성
 *  
 */
package com.uc.bs.cyber.service.kf252001;

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
public class Kf252001Service {

	protected Log log = LogFactory.getLog(this.getClass());
	
	private ApplicationContext appContext = null;
	
	private IbatisBaseService sqlService_cyber = null;
		
	private Kf252001FieldList kfField = null;
	
	private int[] svcTms = new int[2];
	
	CbUtil cUtil = null;
	
	/**
	 * 생성자
	 */
	public Kf252001Service(ApplicationContext appContext) {
		// TODO Auto-generated constructor stub
		
		setAppContext(appContext);
		
		kfField = new Kf252001FieldList();
		
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
	public byte[] chk_kf_252001(MapForm headMap, MapForm mf) throws Exception {
				
		CbUtil cUtil = null;
		
		/*상하수도요금 고지내역조회 시 사용 */
		String resCode = "000";       /*응답코드*/
		String giro_no = "1004102";   /*부산시 상하수도 지로코드*/
		String sg_code = "26";        /*부산시기관코드*/
		
		int curTime = 0;

		try{
			
			/*서비스 시간*/
			svcTms[0] = 0030;
			svcTms[1] = 2200;
			
			cUtil = CbUtil.getInstance();

			log.info(cUtil.msgPrint("3", "S","KP252001 chk_kf_252001()", resCode));
			
			/*전문체크*/
			
			/*기관*/
			if(!headMap.getMap("GPUB_CODE").equals(sg_code)){
				resCode = "339";
			}
			/*발행기관지로코드*/
			if(!headMap.getMap("GJIRO_NO").equals(giro_no)){
				resCode = "324";
			}
			
			curTime = Integer.parseInt(headMap.getMap("TX_DATE").toString().substring(6, 10));
			
			if(log.isDebugEnabled()) {
				log.debug("-------------------------------------------------------------");
				log.debug("   □ 조회키 : " + mf.getMap("SEARCHKEY").toString());
				log.debug("   □ 부과년월 : " + mf.getMap("BYYYMM").toString());
				log.debug("   □ 수납기관 분류 : " + headMap.getMap("GJIRO_NO").toString());
				log.debug("   □ 납부금액 : " + mf.getMap("NAPBU_AMT").toString());
				log.debug("-------------------------------------------------------------");
			}
		
			/*변수선언*/
			String ICHENO      = "";
			String CUSTNO      = "";
			@SuppressWarnings("unused")
			String searchgubun = "";
			int rcpCount       = 0;
			int totalCount     = 0;
			
			if (mf.getMap("SEARCHKEY").toString().length() == 17) { /*전납납부번호*/
				
				ICHENO = mf.getMap("SEARCHKEY").toString();
	            searchgubun = "E";
			
			} else { /*수용가번호*/
				
				if (cUtil.waterChgCustno("S", mf.getMap("SEARCHKEY").toString().substring(20,29)).equals(mf.getMap("SEARCHKEY").toString())){
					
					CUSTNO = mf.getMap("SEARCHKEY").toString().substring(20,29);
	                searchgubun = "S";
					
				} else {
					
					resCode = "311";       //고지내용 없음
				}
				/*여기서만 필요함...*/
				mf.setMap("TAX_YY" , mf.getMap("BYYYMM").toString().substring(0, 4));
				mf.setMap("TAX_MM" , mf.getMap("BYYYMM").toString().substring(4));
			}
			
			if (CUSTNO.length() == 9 || ICHENO.length() == 17) {
				
				String napkiGubun = "";    // 납기내후 구분 (B:납기내, A:납기후)
		        String NAPAMT = "";        // 납부금액
		        
		        /*결제원에서 수신한 전문을 받아 조회결과를 확인한다.*/
				
		        /*결제원에서 수신한 전문을 DB 에 맵핑하기 위함.*/
		        mf.setMap("CUST_NO", CUSTNO);
				mf.setMap("EPAY_NO", ICHENO);
				
		        
				ArrayList<MapForm>  kfAl252001List = sqlService_cyber.queryForList("KF251001.SELECT_SHSD_PAY_LIST", mf);
		        
				rcpCount = kfAl252001List.size();
				
				
		        if (rcpCount < 1){
		        	resCode = "000"; //납부내역없음
		        } else {

		        	/*납부내역 있음*/
					MapForm mfCmd252001List = kfAl252001List.get(0);
					
					if ( mfCmd252001List == null  ||  mfCmd252001List.isEmpty() )   {
						/*납부내역없음*/
						resCode = "000";
					} else {
						/*납부내역이 있는 경우*/
						if(mfCmd252001List.getMap("SNTG").equals("9")) {  /*취소*/
							
							resCode = "000";
							
						} else {
							
							resCode = "331";  /*납부기수신*/

						}
					}

		        }
		        
		        if (curTime >= svcTms[0] && curTime <= svcTms[1]) {
					
				} else {
					resCode = "092";  /*납부시간 아님...*/
				}

		        /*납부내역이 존재하지 않거나, 납부취소된 경우에 대한 납부처리*/
		        if(resCode.equals("000")) {
		        	
		        	/*부과리스트 검색*/
					ArrayList<MapForm>  alKfLevyList = sqlService_cyber.queryForList("KF251001.SELECT_SHSD_LEVY_LIST", mf);
		        	
					totalCount = alKfLevyList.size();
					
					if (totalCount < 1)
						resCode = "311";        // Error : 고지내역없음
			        else if (totalCount > 1)
			        	resCode = "093";        // Error : 고지내역 2건 이상
			        else {                      // 고지내역이 1건인 경우에만 처리
			        	
			        	MapForm mpKfLevyList = alKfLevyList.get(0);
			        	
				        String prt_del1 = (String)mpKfLevyList.getMap("DUE_DT");         //납기내 납기일;
				        String prt_del2 = (String)mpKfLevyList.getMap("DUE_F_DT");       //납기후 납기일;

		                //납기내(B), 납기후(A) 체크
		                napkiGubun = cUtil.getNapGubun(prt_del1, prt_del2);

		                //업무처리변수 Set
		                long prt_amt         = ((BigDecimal)mpKfLevyList.getMap("SUM_B_AMT")).longValue();    /*납기내*/
		                long prt_amt2        = ((BigDecimal)mpKfLevyList.getMap("SUM_F_AMT")).longValue();    /*납기후*/
		                
		                if(napkiGubun.equals("B"))
		                {
		                    NAPAMT = Long.toString(prt_amt);   //납기내금액
		                }
		                else if(napkiGubun.equals("A"))
		                {
		                    NAPAMT = Long.toString(prt_amt2);  //납기후금액
		                }

		                
		                if (NAPAMT.equals((String) mf.getMap("NAPBU_AMT")))
		            	    resCode = "000";        // 정상
		                else
		            	    resCode = "343";        // 납부금액 틀림
		                
		                
		    	        // 수납업무처리
		    	        // 1. 부과내역 update
		    	        // 2. 수납테이블 insert
		    			if (resCode.equals("000")) {
		    				
		    				/*전문정보조립*/
		    				/*전자수납테이블에 수납순번을 구한다. 중복처리가 가능하므로...*/
		    				mpKfLevyList.setMap("PAY_CNT" , sqlService_cyber.getOneFieldInteger("KF251001.TX3211_TB_MaxPayCnt", mpKfLevyList));
		    				mpKfLevyList.setMap("SUM_RCP" , NAPAMT);                                                   /*납부금액*/
		    				mpKfLevyList.setMap("PAY_DT"  , mf.getMap("NAPBU_DATE"));                                  /*수납일자*/
		    				mpKfLevyList.setMap("SNTG"    , "1");                                                      /*납부상태*/
		    				mpKfLevyList.setMap("SNSU"    , "1");                                                      /*수납매체 1 금융결제원*/
		    				mpKfLevyList.setMap("BANK_CD" , CbUtil.lPadString(mf.getMap("OUTBANK_CODE").toString(), 7, '0').substring(0,3));      /*납부기관*/
		    				mpKfLevyList.setMap("RSN_YN"  , (mf.getMap("NAPBU_GUBUN").equals("R")) ? "Y" : "N");       /*납부구분 Q예약  R조회*/
		    				mpKfLevyList.setMap("TRTG"    , "0");                                                      /*자료전송상태*/
		    				mpKfLevyList.setMap("BRC_NO"  , CbUtil.lPadString(mf.getMap("OUTBANK_CODE").toString(), 7, '0'));  /*수납기관코드*/
							mpKfLevyList.setMap("TMSG_NO" , headMap.getMap("BCJ_NO"));   /*출금은행_전문관리번호*/
		    				
		    				int intLog = sqlService_cyber.queryForUpdate("KF251001.INSERT_TX3211_TB_EPAY", mpKfLevyList);
		    				
		    				if(intLog > 0) {
		    					
		    					sqlService_cyber.queryForUpdate("KF251001.UPDATE_TX3111_TB_NAPBU_INFO", mpKfLevyList);
		    					
		    					log.info("상하수도요금 전자납부처리 완료! [" + mf.getMap("SEARCHKEY") + "]");
		    					
		    					//MapForm mpSMS = new MapForm();	
								
		    					/*상하수도는 주민번호가 없으니..Select 한다.*/
								//mpSMS.setMap("REG_NO"  , "");
								//mpSMS.setMap("TAX_ITEM", mpKfLevyList.getMap("TAX_ITEM"));
								//mpSMS.setMap("SUM_RCP" , NAPAMT);
								
								//sqlService_cyber.procedure("CODMBASE.SMSCALL", mpSMS);
		    				}

		    			}
			            
			        }
		        	
		        }

			}
			
			log.info(cUtil.msgPrint("3","","KP252001", resCode));
			log.info("=============================================");
		
        } catch (Exception e) {
			
			e.printStackTrace();
			log.error("============================================");
			log.error("== chk_kf_252001 Exception(시스템) ");
			log.error("============================================");
		}
        
        /*수신전문을 같이 보낸다.*/
        
        /*여기서 공통헤드를 셋팅한다. 바뀌는 부분만 다시 셋팅해서 전문을 조립*/
        headMap.setMap("RS_FLAG"   , "G");         /*지로이용기관(G) */
        headMap.setMap("RESP_CODE" , resCode);     /*응답코드*/
        headMap.setMap("GCJ_NO"    , "0" + sg_code + "0" + CbUtil.lPadString(String.valueOf(SeqNumber()), 8, '0'));     /*이용기관 일련번호*/
        headMap.setMap("TX_GUBUN"  , "0210");
        
        return kfField.makeSendBuffer(headMap, mf);
        
	}

	/*----------------------------------------------*/
	/* 일련번호 가져오기 테스트*/
	/*----------------------------------------------*/
	private int SeqNumber(){
    	return (Integer)sqlService_cyber.queryForBean("CODMBASE.CODM_GETSEQNO", "00");
    }
}
