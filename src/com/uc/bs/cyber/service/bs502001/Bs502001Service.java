/**
 *  주시스템명 : 사이버지방세청 고도화
 *  업  무  명 : 지방세 납부통지전문
 *  기  능  명 : 결재원-사이버세청 
 *               업무처리
 *               결재원에서 수신된 전문에 대해서 응답전문데이트를 생성하고 
 *               조회결과를 저장한다.
 *  클래스  ID : Bs502001Service
 *  변경  이력 :
 * ------------------------------------------------------------------------
 *  작성자     소속  		 일자      Tag   내용
 * ------------------------------------------------------------------------   
 * 황종훈   유채널(주)    2011.06.13   %01%  신규작성
 *  
 */
package com.uc.bs.cyber.service.bs502001;

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
public class Bs502001Service {

	protected Log log = LogFactory.getLog(this.getClass());
	
	private ApplicationContext appContext = null;
	
	private IbatisBaseService sqlService_cyber = null;
		
	private Bs502001FieldList bsField = null;
	
	private int[] svcTms = new int[2];
	
	CbUtil cUtil = null;
	
	/**
	 * 생성자
	 */
	public Bs502001Service(ApplicationContext appContext) {
		// TODO Auto-generated constructor stub
		
		setAppContext(appContext);
		
		bsField = new Bs502001FieldList();
		
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
	public byte[] chk_bs_502001(MapForm headMap, MapForm mf) throws Exception {
		
		/**
		 * (참고사항)
		 * 비택스(부산은행)와 위택스의 수납처리 방법의 차이로 인해
		 * 중복수납이 발생할 여지가 있음.
		 * 따라서 비택스에서는 수납건이 있으면 해당 수납건에 대해 기수납오류를 송신하고
		 * 수납을 불가한다.
		 * 단) 위택스에서 수납이 오는 경우 부산은행에서 수납이 이루어졌다 할지라도 이중으로 
		 * 수납을 처리한다. 
		 * 추후에 삭제시  부과자료의 변경을 방지하기 위함.
		 * 위택스 -- 수납(계정처리포함) 후 일방적으 통보(결과전문에 영향을 받지 않음)
		 * 비택스 -- 송신 후 송신전문에 따라 계정처리 함
		 * 
		 */
		
		/*
		 * 비택스 수납시간 : 07:00 ~ 22:00
		 * */
				
		MapForm sendMap = new MapForm();
		
		/*세외수입 고지내역조회 시 사용 */
		String resCode = "000";       /*응답코드*/
//		String giro_no = "1000685";   /*부산시지로코드*/
		String strTaxGubun = "";
		String sg_code = "26";        /*부산시기관코드*/
		int curTime = 0;
       
		try{
			
			/*서비스 시간*/
			svcTms[0] = 0700;
			svcTms[1] = 2200;
			
			cUtil = CbUtil.getInstance();
			
			log.info(cUtil.msgPrint("", "S", "BP502001 chk_bs_502001()", resCode));
			
			
			/*전문체크*/
			/*기관*/
			if(!headMap.getMap("GPUB_CODE").equals(sg_code)){
				resCode = "122";
			}
			/*발행기관지로코드*/
			strTaxGubun = (String) sqlService_cyber.queryForBean("CODMBASE.GIRO_FOR_TAX", headMap);
//			if(!headMap.getMap("GJIRO_NO").equals(giro_no)){
			if(!("1".equals(strTaxGubun))){
				resCode = "123";
			}
			/* 이체통보에 납부시간 체크는 의미가 없다.
			curTime = Integer.parseInt(headMap.getMap("TX_DATE").toString().substring(6, 10));
			
			if (curTime >= svcTms[0] && curTime <= svcTms[1]) {
				
			} else {
				resCode = "092";
			}
			 */			
			if(resCode.equals("000")){
				
				/* 조회조건 자르기(납부번호 잘라서 조회조건 만든다) */
				mf.setMap("SGG_COD", mf.getMap("ETAXNO").toString().substring(0, 3)); 	//구청코드
				mf.setMap("ACCT_COD", mf.getMap("ETAXNO").toString().substring(4, 6));  //회계코드
				mf.setMap("TAX_ITEM", mf.getMap("ETAXNO").toString().substring(6, 12)); //과목/세목코드
				mf.setMap("TAX_YY", mf.getMap("ETAXNO").toString().substring(12, 16));  //과세년도
				mf.setMap("TAX_MM", mf.getMap("ETAXNO").toString().substring(16, 18));	//과세월
				mf.setMap("TAX_DIV", mf.getMap("ETAXNO").toString().substring(18, 19));	//기분코드
				mf.setMap("HACD", mf.getMap("ETAXNO").toString().substring(19, 22));	//행정동코드
				mf.setMap("TAX_SNO", mf.getMap("ETAXNO").toString().substring(22, 28));	//과세번호
				
				log.debug("TAXNO    = [" + mf.getMap("ETAXNO") + "]");
				log.debug("JUMIN_NO = [" + mf.getMap("JUMIN_NO") + "]");
				log.debug("SGG_COD  = [" + mf.getMap("SGG_COD") + "]");
				log.debug("ACCT_COD = [" + mf.getMap("ACCT_COD") + "]");
				log.debug("TAX_ITEM = [" + mf.getMap("TAX_ITEM") + "]");
				log.debug("TAX_YY   = [" + mf.getMap("TAX_YY") + "]");
				log.debug("TAX_MM   = [" + mf.getMap("TAX_MM") + "]");
				log.debug("TAX_DIV  = [" + mf.getMap("TAX_DIV") + "]");
				log.debug("HACD     = [" + mf.getMap("HACD") + "]");
				log.debug("TAX_SNO  = [" + mf.getMap("TAX_SNO") + "]");
				
				ArrayList<MapForm>  bsCmd502001List  =  sqlService_cyber.queryForList("BS502001.SELECT_TAX_SEARCH_LIST", mf);
				
				log.info("bsCmd502001List.size() = [" + bsCmd502001List.size() +"]");
				
		        /* Error : 고지내역없음 */
				if(bsCmd502001List.size() <= 0){
					resCode = "111";  
				} 
				/* Error : 고지내역 2건 이상 */
				else if (bsCmd502001List.size() > 1) {
					resCode = "201";  
				} else {
					
					/* 조회결과 담는 맵폼 */
					MapForm taxform = new MapForm();
					
					if ( bsCmd502001List.size() > 0 ) {
						
						taxform = bsCmd502001List.get(0);

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
		                		/*
		                		/* 본세 가산금 
		                		taxform.setMap("MNTX_ADTX", cUtil.getGasanAmt(napkiGubun
		                													, taxform.getMap("TAX_YY").toString() + taxform.getMap("TAX_MM").toString() 
		                													, Long.parseLong(taxform.getMap("MNTX").toString())));
		                		/* 도시계획세 가산금 
		                		taxform.setMap("CPTX_ADTX", cUtil.getGasanAmt(napkiGubun
																			, taxform.getMap("TAX_YY").toString() + taxform.getMap("TAX_MM").toString() 
																			, Long.parseLong(taxform.getMap("CPTX").toString())));
		                		/* 공동시설세 가산금 
		                		taxform.setMap("CFTX_ADTX", cUtil.getGasanAmt(napkiGubun
																			, taxform.getMap("TAX_YY").toString() + taxform.getMap("TAX_MM").toString() 
																			, Long.parseLong(taxform.getMap("CFTX").toString())));
		                		/* 교육세 가산금 
		                		taxform.setMap("LETX_ADTX", cUtil.getGasanAmt(napkiGubun
																			, taxform.getMap("TAX_YY").toString() + taxform.getMap("TAX_MM").toString() 
																			, Long.parseLong(taxform.getMap("LETX").toString())));
		                		/* 농특세 가산금 
		                		taxform.setMap("ASTX_ADTX", cUtil.getGasanAmt(napkiGubun
																		, taxform.getMap("TAX_YY").toString() + taxform.getMap("TAX_MM").toString() 
																			, Long.parseLong(taxform.getMap("ASTX").toString())));  
								*/						
		                		
		                	} else {
		                		/* 납부금액 */
		                		taxform.setMap("SUM_RCP", cUtil.getNapAmt(Long.parseLong(taxform.getMap("MNTX").toString())				//본세
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
						
						
						/*
			            if (Long.parseLong(mf.getMap("NAPBU_AMT").toString()) == Long.parseLong(taxform.getMap("SUM_RCP").toString()))
			            	resCode = "000";        // 정상
			            else
			            	resCode = "126";        // 납부금액 틀림
			           	*/
			            
						taxform.setMap("JUMIN_NO", taxform.getMap("REG_NO"));   /*주민법인번호 */
						taxform.setMap("SGG_COD" , taxform.getMap("SGG_COD"));  /*구청코드 */     
						taxform.setMap("ACCT_COD", taxform.getMap("ACCT_COD")); /*회계코드 */   
						taxform.setMap("TAX_ITEM", taxform.getMap("TAX_ITEM")); /*과세목   */  
					    taxform.setMap("TAX_YY", taxform.getMap("TAX_YY"));     /*부과년도 */    
					    taxform.setMap("TAX_MM", taxform.getMap("TAX_MM"));     /*부과월   */  
					    taxform.setMap("TAX_DIV", taxform.getMap("TAX_DIV"));   /*기분     */ 
					    taxform.setMap("HACD", taxform.getMap("HACD"));       	/*행정동   */ 
					    taxform.setMap("TAX_SNO", taxform.getMap("TAX_SNO"));   /*과세번호 */   
					    taxform.setMap("RCP_CNT", taxform.getMap("RCP_CNT"));   /*분납순번 */   
					    taxform.setMap("SNTG", "1");       						/*수납FLAG */    
					    taxform.setMap("SUM_RCP", taxform.getMap("SUM_RCP"));  	/*수납금액 */   
					    taxform.setMap("PAY_DT" , mf.getMap("NAPBU_DATE"));     /*납부일자 */     
					    taxform.setMap("SNSU", "3");       						/*수납수단(부산은행 = '3') */    
					    //주의 납부기관이 전문상에 H로 되어 있기때문에 앞'0'은 삭제되므로 반드시 추가함...
					    taxform.setMap("BANK_CD", CbUtil.lPadString(mf.getMap("OUTBANK_CODE").toString(), 7, '0').substring(0, 3));   /*납부기관 */     
					    taxform.setMap("BRC_NO" , CbUtil.lPadString(mf.getMap("OUTBANK_CODE").toString(), 7, '0'));    /*수납기관코드    */  
					    taxform.setMap("TMSG_NO", headMap.getMap("BCJ_NO"));    /*출금은행_전문관리번호 */   
					    taxform.setMap("RSN_YN" , (mf.getMap("NAPBU_GUBUN").equals("R")) ? "Y" : "N");       
					    
					    int pay_cnt = 0, intLog = 0, pay_gbn = 0;
					    
					    /**
						 * 20110726 : 자동이체 등록건은 수납처리불가
						 */
						if(!taxform.getMap("AUTO_TRNF_YN").equals("Y")) {
							
							/* 지방세 수납처리 */
						    if(taxform.getMap("TAX_GBN").equals("1")){
						    	
						    	/*모든 매체를 통한 납부건수가 있는지 확인*/
						    	pay_gbn = sqlService_cyber.getOneFieldInteger("BS502001.TX1201_TB_PAY", taxform);
						    	
						    	pay_cnt = pay_gbn;
						    	
						    	//if (pay_gbn == 0) {
						    	//	pay_cnt = 0;
						    	//} else {
						    	//	pay_cnt = sqlService_cyber.getOneFieldInteger("BS502001.TX1201_TB_MaxPayCnt", taxform);
						    	//}

						    	taxform.setMap("PAY_CNT", pay_cnt);
						    	
						    	intLog = sqlService_cyber.queryForUpdate("BS502001.INSERT_TX1201_TB_EPAY", taxform);
						    	
								if(intLog > 0) {
									
									sqlService_cyber.queryForUpdate("BS502001.UPDATE_TX1102_TB_NAPBU_INFO", taxform);
									
									log.info("지방세 전자납부처리 완료! [" + mf.getMap("ETAXNO") + "]");
									
									
									MapForm mpSMS = new MapForm();	
									
									mpSMS.setMap("REG_NO"  , taxform.getMap("REG_NO"));
									mpSMS.setMap("TAX_ITEM", taxform.getMap("TAX_ITEM"));
									mpSMS.setMap("SUM_RCP" , taxform.getMap("SUM_RCP"));
									
									try {
										sqlService_cyber.procedure("CODMBASE.SMSCALL", mpSMS);
									}catch (Exception e) {
										log.info("SMS 등록데이터 = " + mpSMS.getMaps());
										log.info("SMS 등록오류 발생");
									}
									
								}
						    }
						    /* 버스, 주거지 수납처리 */
						    else if (taxform.getMap("TAX_GBN").equals("2")){
						    	
						    	pay_gbn = sqlService_cyber.getOneFieldInteger("BS502001.TX2221_TB_PAY", taxform);
						    	
						    	pay_cnt = pay_gbn;
						    	
						    	//if (pay_gbn == 0) {
						    	//	pay_cnt = 0;
						    	//} else {
						    	//	pay_cnt = sqlService_cyber.getOneFieldInteger("BS502001.TX2221_TB_MaxPayCnt", taxform);
						    	//}
						    		
						    	taxform.setMap("PAY_CNT", pay_cnt);
						    	
						    	intLog = sqlService_cyber.queryForUpdate("BS502001.INSERT_TX2221_TB_EPAY", taxform);
						    	
								if(intLog > 0) {
									
									sqlService_cyber.queryForUpdate("BS502001.UPDATE_TX2122_TB_NAPBU_INFO", taxform);
									
									log.info("버스. 주거지 전자납부처리 완료! [" + mf.getMap("ETAXNO") + "]");
									
									MapForm mpSMS = new MapForm();	
									
									mpSMS.setMap("REG_NO"  , taxform.getMap("REG_NO"));
									mpSMS.setMap("TAX_ITEM", taxform.getMap("TAX_ITEM"));
									mpSMS.setMap("SUM_RCP" , taxform.getMap("SUM_RCP"));
									
									try {
										sqlService_cyber.procedure("CODMBASE.SMSCALL", mpSMS);
									}catch (Exception e) {
										log.info("SMS 등록데이터 = " + mpSMS.getMaps());
										log.info("SMS 등록오류 발생");
									}
								}    	
						    	
						    }	    
						    
						} else {
							
							resCode = "336";
							//자동이체건으로 수납처리 불가...
						}
					           
			            
					}else{ 
						/* 조회건수가 없는 경우 전문생성
						 * */
						resCode = "111";  /*조회내역없음*/
						
					} 
					
				}
				
			}

			log.info(cUtil.msgPrint("", "", "BP502001 chk_bs_502001()", resCode));
			
        } catch (Exception e) {
			
        	//20110829 추가
        	resCode = "093";
        	
			e.printStackTrace();
			log.error("============================================");
			log.error("== chk_bs_502001 Exception(시스템) ");
			log.error("============================================");
		}
        
        if(!resCode.equals("000")) {
        	sendMap = mf;
        }
        
        /*여기서 공통헤드를 셋팅한다. 바뀌는 부분만 다시 셋팅해서 전문을 조립*/
        headMap.setMap("RS_FLAG"   , "G");         /*지로이용기관(G) */
        headMap.setMap("RESP_CODE" , resCode);     /*응답코드*/
        headMap.setMap("GCJ_NO"    , "0" + sg_code + "0" + CbUtil.lPadString(String.valueOf(SeqNumber()), 8, '0'));     /*이용기관 일련번호*/
        headMap.setMap("TX_GUBUN"  , "0210");
        
        return bsField.makeSendBuffer(headMap, sendMap);
	}

	/*----------------------------------------------*/
	/* 일련번호 가져오기 테스트*/
	/*----------------------------------------------*/
	private int SeqNumber(){
    	return (Integer)sqlService_cyber.queryForBean("CODMBASE.CODM_GETSEQNO", "00");
    }

}

