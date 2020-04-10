/**
 *  주시스템명 : 사이버지방세청 고도화
 *  업  무  명 : 납부 (재)취소 전문
 *  기  능  명 : 부산은행-사이버세청 
 *               업무처리
 *               결재원에서 수신된 전문에 대해서 응답전문데이트를 생성하고 
 *               조회결과를 송신한다.
 *  클래스  ID : Bs992001Service
 *  변경  이력 :
 * ------------------------------------------------------------------------
 *  작성자     소속  		 일자      Tag   내용
 * ------------------------------------------------------------------------   
 * 송동욱   다산(주)    2011.06.13   %01%  신규작성
 * 임창섭      유채널(주)         2013.07.02         %02%         주석추가 납부취소  수정
 *  
 */
package com.uc.bs.cyber.service.bs992001;

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
public class Bs992001Service {

	protected Log log = LogFactory.getLog(this.getClass());
	
	private ApplicationContext appContext = null;
	
	private IbatisBaseService sqlService_cyber = null;
		
	private Bs992001FieldList kfField = null;
	
	CbUtil cUtil = null;
		  
	/**
	 * 생성자
	 */
	public Bs992001Service(ApplicationContext appContext) {
		// TODO Auto-generated constructor stub
		
		setAppContext(appContext);
		
		kfField = new Bs992001FieldList();
		
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
	 * 납부 재취소
	 * */
	public byte[] chk_bs_992001(MapForm headMap, MapForm mf) throws Exception {
		
		MapForm sendMap = new MapForm();
		
		/*지방세 고지내역조회 시 사용 */
		String resCode = "000";       /*응답코드*/
		String sg_code = "26" ;       /*부산시기관코드*/
		String strTaxGubun = "";
		
		ArrayList<MapForm> bsCmd992001List = new ArrayList<MapForm>();
		
		try{
			cUtil = CbUtil.getInstance();
			
			log.info(cUtil.msgPrint("11", "S","BP992001 chk_bs_992001() : 납부취소", resCode));
			
			/*기관*/
			if(!headMap.getMap("GPUB_CODE").equals(sg_code)){
				resCode = "122"; 
			}
			
			strTaxGubun = (String) sqlService_cyber.queryForBean("CODMBASE.GIRO_FOR_TAX", headMap);
			
			if(resCode.equals("000")){
				
				/*수신한 지로코드에 의해서 쿼리를 결정한다...*/
				if("3".equals(strTaxGubun)) { /*환경개선*/
					
    				log.info("통합환경개선 납부취소 전문 ...");
    				
    				bsCmd992001List = sqlService_cyber.queryForList("BS992001.SELECT_RECIP_H_LIST", mf);
					
				} else if("1".equals(strTaxGubun)) { /*지방세*/
					
					log.info("통합지방세 납부취소 전문 ...");
					
					bsCmd992001List = sqlService_cyber.queryForList("BS992001.SELECT_RECIP_J_LIST", mf);
					
				} else if ("2".equals(strTaxGubun)) { //통합세외수입-간단e납부
                 
				    log.info("통합세외수입 납부취소 전문 ...");
                    
                    bsCmd992001List = sqlService_cyber.queryForList("BS992001.SELECT_RECIP_O_LIST", mf);
                 
//				} else if(headMap.getMap("GJIRO_NO").equals("1500172")) { /*기타세입금*/
				} else if("4".equals(strTaxGubun)) { /*기타세입금*/
					
//					log.info("세외수입 납부취소 전문 ...");
					log.info("기타세입금 납부취소 전문 ...");
					
					bsCmd992001List = sqlService_cyber.queryForList("BS992001.SELECT_RECIP_O_LIST", mf);
					
//				} else if(headMap.getMap("GJIRO_NO").equals("1004102")) { /*상하수도*/
				} else if("5".equals(strTaxGubun)) { /*상하수도*/
					
					log.info("상하수도 납부취소 전문 ...");
					
					bsCmd992001List = sqlService_cyber.queryForList("BS992001.SELECT_RECIP_S_LIST", mf);
					
				} else {
					
					/*발행기관지로코드*/
					resCode = "123";
				}
								
				int payTotCnt = bsCmd992001List.size();
				
				if (payTotCnt <= 0) {
					
					/*조회내역없음*/
					resCode = "112";
					
				} else if (payTotCnt > 1) {
					
					/* 출금은행_전문관리번호로 조회하므로
					 * 이건에 대해서 절대 2건 조회가 될수 없다.
					 * */
					resCode = "094";
					
				} else {
					
					/*조회 1건
					 *전자수납테이블에 납부 취소 후 부과내역테이블에 취소 한다. 
					 * */
					MapForm mfCmd992001List  =  bsCmd992001List.get(0);

					/*정상인 경우*/
					if(resCode.equals("000")){
						
						if(!(headMap.getMap("RS_FLAG").equals("B")|| headMap.getMap("RS_FLAG").equals("C"))){
							resCode = "312"; //해당수납기관 아님.(비택스, 결제원)
						} else {
							
							if(mfCmd992001List.getMap("SNTG").equals("1") ){ //가수납상태이므로 납부 취소가 가능함...
								
								if(Long.parseLong(mfCmd992001List.getMap("SUM_RCP").toString()) != Long.parseLong(mf.getMap("B_NAPBU_AMT").toString())) {
									resCode = "417"; //원거래 지로대금납부금액 틀림
									
								} else {
									resCode = "000";
								}
								
							} else if(mfCmd992001List.getMap("SNTG").equals("9") ){
								resCode = "132"; // 납부내역 기취소하였음
								
							} else {
								resCode = "411"; // 취소대상 내역 아님 ( 이미 수납되고 구청에 통보 된 건이므로 취소불가)
							}
							
							/*정상적인 취소건인 경우*/
							if (resCode.equals("000")) {
								
								/* 먼저 수납테이블을 조회하여 다른매체를 통한 수납건이 있는 지 반드시 확인한다.
								 * 이중 수납건이 있는 경우는 부과테이블에 취소처리를 하지 않는다.
								 * */
								
								if (mfCmd992001List.getMap("TAX_GB").equals("J")){        //지방세
									
									ArrayList<MapForm> bsPayList = sqlService_cyber.queryForList("BS992001.SELECT_PAY_J_LIST", mfCmd992001List);
									
									if(bsPayList.size() > 0 ) {
										
										if(bsPayList.size() == 1 ) {
											//단건
											if (sqlService_cyber.queryForUpdate("BS992001.UPDATE_TX1102_TB_LEVY_DETAIL", mfCmd992001List) > 0 ) {
												
												if(sqlService_cyber.queryForUpdate("BS992001.DELETE_TX1201_TB_EPAY", mfCmd992001List) == 0 ) {
													resCode = "093"; //오류
												}
											} else {
												resCode = "093"; //오류
											}
											
										} else {
											//복수건인 경우 전자수납테이블만 취소한다.
											//취소건은 출금은행_전문관리번호로 조회한 데이터 이므로 해당 수납건에 대한 데이터만 취소처리한다.
											if(sqlService_cyber.queryForUpdate("BS992001.DELETE_TX1201_TB_EPAY", mfCmd992001List) == 0 ) {
												resCode = "093"; //오류
											}
										}
										
									} else {
										resCode = "093"; //오류
									}
									
									
								} else if (mfCmd992001List.getMap("TAX_GB").equals("O")){ //세외수입일반

									ArrayList<MapForm> bsPayList = sqlService_cyber.queryForList("BS992001.SELECT_PAY_O_LIST", mfCmd992001List);

									if(bsPayList.size() > 0 ) {

										if(bsPayList.size() == 1 ) {

											if (sqlService_cyber.queryForUpdate("BS992001.UPDATE_TX2221_TB_LEVY_DETAIL", mfCmd992001List) > 0 ) {

												if(sqlService_cyber.queryForUpdate("BS992001.DELETE_TX2122_TB_EPAY", mfCmd992001List) == 0 ) {

													resCode = "093"; //오류
												}
											} else {

												resCode = "093"; //오류
											}
											
										} else {

											//복수건인 경우 전자수납테이블만 취소한다.
											//취소건은 출금은행_전문관리번호로 조회한 데이터 이므로 해당 수납건에 대한 데이터만 취소처리한다.
											if (sqlService_cyber.queryForUpdate("BS992001.UPDATE_TX2221_TB_LEVY_DETAIL", mfCmd992001List) == 0 ) {

												resCode = "093"; //오류
											}
										}
										
									} else {

										resCode = "093"; //오류
									}

								} else if (mfCmd992001List.getMap("TAX_GB").equals("S")){ //상하수도

									ArrayList<MapForm> bsPayList = sqlService_cyber.queryForList("BS992001.SELECT_PAY_S_LIST", mfCmd992001List);

									if(bsPayList.size() > 0 ) {

										if(bsPayList.size() == 1 ) {

											if (sqlService_cyber.queryForUpdate("BS992001.UPDATE_TX3111_TB_LEVY_DETAIL", mfCmd992001List) > 0 ) {

												if(sqlService_cyber.queryForUpdate("BS992001.DELETE_TX3211_TB_EPAY", mfCmd992001List) == 0 ) {

													resCode = "093"; //오류
												}
											} else {

												resCode = "093"; //오류
											}
											
										} else {

											//복수건인 경우 전자수납테이블만 취소한다.
											//취소건은 출금은행_전문관리번호로 조회한 데이터 이므로 해당 수납건에 대한 데이터만 취소처리한다.
											if(sqlService_cyber.queryForUpdate("BS992001.DELETE_TX3211_TB_EPAY", mfCmd992001List) == 0 ) {

												resCode = "093"; //오류
											}
										}

									} else {

										resCode = "093"; //오류
									}

									
								} else if (mfCmd992001List.getMap("TAX_GB").equals("H")){ //환경개선

									ArrayList<MapForm> bsPayList = sqlService_cyber.queryForList("BS992001.SELECT_PAY_H_LIST", mfCmd992001List);

									if(bsPayList.size() > 0 ) {

										if(bsPayList.size() == 1 ) {

											if (sqlService_cyber.queryForUpdate("BS992001.UPDATE_TX2132_TB_LEVY_DETAIL", mfCmd992001List) > 0 ) {

												if(sqlService_cyber.queryForUpdate("BS992001.DELETE_TX2231_TB_EPAY", mfCmd992001List) == 0 ) {

													resCode = "093"; //오류
												}
											} else {

												resCode = "093"; //오류
											}
											
										} else {

											//복수건인 경우 전자수납테이블만 취소한다.
											//취소건은 출금은행_전문관리번호로 조회한 데이터 이므로 해당 수납건에 대한 데이터만 취소처리한다.
											if(sqlService_cyber.queryForUpdate("BS992001.DELETE_TX2231_TB_EPAY", mfCmd992001List) == 0 ) {

												resCode = "093"; //오류
											}
										}
										
									} else {

										resCode = "093"; //오류
									}
									
									

								} else if (mfCmd992001List.getMap("TAX_GB").equals("T")){ //표준세외수입

									ArrayList<MapForm> bsPayList = sqlService_cyber.queryForList("BS992001.SELECT_PAY_T_LIST", mfCmd992001List);

									if(bsPayList.size() > 0 ) {

										if(bsPayList.size() == 1 ) {

											if (sqlService_cyber.queryForUpdate("BS992001.UPDATE_TX2112_TB_LEVY_DETAIL", mfCmd992001List) > 0 ) {

												if(sqlService_cyber.queryForUpdate("BS992001.DELETE_TX2211_TB_EPAY", mfCmd992001List) == 0 ) {

													resCode = "093"; //오류
												}
											} else {

												resCode = "093"; //오류
											}
											
										} else {

											//복수건인 경우 전자수납테이블만 취소한다.
											//취소건은 출금은행_전문관리번호로 조회한 데이터 이므로 해당 수납건에 대한 데이터만 취소처리한다.
											if(sqlService_cyber.queryForUpdate("BS992001.DELETE_TX2211_TB_EPAY", mfCmd992001List) == 0 ) {

												resCode = "093"; //오류
											}
											
										}
										
									} else {

										resCode = "093"; //오류
									}
									
								}
								
							}
							
						}

					}
			
				}
				
			}
			
			log.info(cUtil.msgPrint("11", "","BP992001 chk_bs_992001()", resCode));
			
        } catch (Exception e) {
			
        	resCode = "093";
        	
			e.printStackTrace();
			log.error("============================================");
			log.error("== chk_bs_992001 Exception(시스템) ");
			log.error("============================================");
		}
        
        if(!resCode.equals("000")) {

        	sendMap = mf;
        } else {
        	if (headMap.getMap("TX_GUBUN").equals("420")) {
        		if(headMap.getMap("PROGRAM_ID").equals("992001")){ /*납부 재(취소)*/
					
					if(headMap.getMap("RS_FLAG").equals("C") || headMap.getMap("RS_FLAG").equals("B")){ /* 결제원 및 은행 으로 부터 응답*/

        				sendMap = mf; /*취소요청 전문은 처리성공시 왔던 전문 그대로 보냄 */
					}
        		}
        	}
        }
        
        /*여기서 공통헤드를 셋팅한다. 바뀌는 부분만 다시 셋팅해서 전문을 조립*/
        headMap.setMap("RS_FLAG"   , "G");         /*지로이용기관(G) */
        headMap.setMap("RESP_CODE" , resCode);     /*응답코드*/
        headMap.setMap("GCJ_NO"    , "0" + sg_code + "0" + CbUtil.lPadString(String.valueOf(SeqNumber()), 8, '0'));     /*이용기관 일련번호*/
        headMap.setMap("TX_GUBUN"  , "0430");

        return kfField.makeSendBuffer(headMap, sendMap);
	}

	/*----------------------------------------------*/
	/* 일련번호 가져오기 테스트*/
	/*----------------------------------------------*/
	private int SeqNumber(){
    	return (Integer)sqlService_cyber.queryForBean("CODMBASE.CODM_GETSEQNO", "00");
    }
}

