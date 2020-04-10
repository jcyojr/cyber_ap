/**
 *  주시스템명 : 사이버지방세청 고도화
 *  업  무  명 : 지방세 고지내역 간략조회 전문
 *  기  능  명 : 부산은행-사이버세청 
 *               업무처리
 *               결재원에서 수신된 전문에 대해서 응답전문데이트를 생성하고 
 *               조회결과를 저장한다.
 *  클래스  ID : Bs521001Service
 *  변경  이력 :
 * ------------------------------------------------------------------------
 *  작성자     소속  		 일자      Tag   내용
 * ------------------------------------------------------------------------   
 * 황종훈   다산(주)    2011.06.13   %01%  신규작성
 * 임창섭   유채널(주)  2013.11.04     %02%  간단e납부관련(세외수입, 환경개선부담금) 추가
 */
package com.uc.bs.cyber.service.bs521001;

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
public class Bs521001Service {

	protected Log log = LogFactory.getLog(this.getClass());
	
	private ApplicationContext appContext = null;
	
	private IbatisBaseService sqlService_cyber = null;
		
	private Bs521001FieldList kfField = null;
	
	CbUtil cUtil = null;
	
	/**
	 * 생성자
	 */
	public Bs521001Service(ApplicationContext appContext) {
		// TODO Auto-generated constructor stub
		
		setAppContext(appContext);
		
		kfField = new Bs521001FieldList();
		
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
	 * 지방세 고지내역 간략조회 처리...
	 * */
	public byte[] chk_bs_521001(MapForm headMap, MapForm mf) throws Exception {
		
				
		MapForm sendMap = new MapForm();
		
		/*지방세 고지내역조회 시 사용 */
		String resCode = "000";       /*응답코드*/
//		String giro_no = "1000685";   /*부산시지로코드 : 시청*/
		String strTaxGubun = "";
		String sg_code = "26";        /*부산시기관코드*/
		
		String POINTNO = "";          /*지정번호*/
		String DATANUM = "";          /*데이터갯수*/
		int START_IDX = 0;
		
		/*반복부*/
		ArrayList<MapForm> alRepeatData = new ArrayList<MapForm>();
		
		try{
			cUtil = CbUtil.getInstance();
			
			log.info(cUtil.msgPrint("6", "S","BP521001 chk_bs_521001()", resCode));
			
			/*전문체크*/
			/*기관*/
			if(!headMap.getMap("GPUB_CODE").equals(sg_code)){
				resCode = "122";
			}
			/*발행기관지로코드*/
			strTaxGubun = (String) sqlService_cyber.queryForBean("CODMBASE.GIRO_FOR_TAX", headMap);
//          if(!headMap.getMap("GJIRO_NO").equals(giro_no)){
			log.debug("--------strTaxGubun = " + strTaxGubun + " -------------확인 20140721");
            
			if(!("1".equals(strTaxGubun))){
              resCode = "123";
            }
            
			POINTNO = (String)mf.getMap("POINT_NO"); // 지정번호
			DATANUM = (String)mf.getMap("DATA_NUM"); // 데이터건수

			if (resCode.equals("000")) {
				
				/*지방세 상세내역의 쿼리를 사용한다...*/
				ArrayList<MapForm>  bsCmd521001List  =  sqlService_cyber.queryForList("BS521001.SELECT_TAX_SEARCH_LIST", mf);
				log.debug("-------bsCmd521001List.size() = " +  String.valueOf(bsCmd521001List.size()) + " ---------------확인 20140721");				
				
				if ( bsCmd521001List.size() > 0 ) {
				    
					//간단e납부 테스트시 pointno가 '0'으로 올 경우 에러 확인 수정
                    if (Integer.parseInt(POINTNO)<=0) POINTNO = "001";
						START_IDX = Integer.parseInt(POINTNO) -1;
					if (START_IDX >= bsCmd521001List.size()){
						resCode = "111";
					} else {
						resCode = "000";
						
						/*지정번호 처리로직*/
						if (Integer.parseInt(DATANUM) >= 10 ){
							if(Integer.parseInt(DATANUM) > bsCmd521001List.size())
								DATANUM = String.valueOf(bsCmd521001List.size());
							else
								DATANUM = "10";
			                log.debug("-------DATANUM" + Integer.parseInt(DATANUM) + "---------------ENDTERED");
			                	
						}
						else
			                DATANUM = String.valueOf( bsCmd521001List.size() - START_IDX );
			              
						/*0210 응답전문 생성 */
						int DATA_TOT=(bsCmd521001List.size()>999)?999:bsCmd521001List.size();
						sendMap.setMap("JUMIN_NO", mf.getMap("JUMIN_NO"));    /*주민번호   */
			            sendMap.setMap("DATA_TOT", DATA_TOT);   /*고지총건수 */
			  			sendMap.setMap("POINT_NO", POINTNO);                  /*지정번호   */
			  			sendMap.setMap("DATA_NUM", DATANUM);                  /*데이터건수 */
			  			
//                      for ( int al_cnt = 0;  al_cnt < bsCmd521001List.size();  al_cnt++)   {
//                      for ( int al_cnt = 0;  al_cnt < Integer.parseInt(DATANUM);  al_cnt++)   {//2013-10-29 금결원 이보람 과장과 통화(전체전문길이 1Kbyte제한)후 아래로 수정 by 임창섭
                        for ( int al_cnt = START_IDX;  al_cnt < (START_IDX + Integer.parseInt(DATANUM));  al_cnt++)  {
							
							MapForm mfCmd521001List  =  bsCmd521001List.get(al_cnt);
							
							MapForm mfRepeatData = new MapForm();
							
							/* 납기구분 */
							String napGubun = cUtil.getNapkiGubun((String)mfCmd521001List.getMap("DUE_DT"));
							
							mfRepeatData.setMap("PUBGCODE"   , headMap.getMap("GPUB_CODE"));
							mfRepeatData.setMap("JIRONO"     , headMap.getMap("GJIRO_NO"));
							mfRepeatData.setMap("TAXNO"    	 , mfCmd521001List.getMap("TAXNO"));
							mfRepeatData.setMap("SEQNO"    	 , Long.parseLong(mfCmd521001List.getMap("SEQNO").toString()));
							mfRepeatData.setMap("GWA_MOK"    , mfCmd521001List.getMap("TAX_ITEM"));
							mfRepeatData.setMap("GWA_YM"   	 , mfCmd521001List.getMap("TAX_YM"));
							mfRepeatData.setMap("KIBUN"      , mfCmd521001List.getMap("TAX_DIV"));
							
							/* 버스 주거지 */
							if (mfCmd521001List.getMap("TAX_GBN").equals("2")){
								mfRepeatData.setMap("NAP_AMT"    , mfCmd521001List.getMap("SUM_B_AMT"));
								//mfRepeatData.setMap("NAP_AMT"    , mfCmd521001List.getMap("AMT"));
								mfRepeatData.setMap("NAP_GUBUN"  , "B");
								mfRepeatData.setMap("NAP_DATE"   , mfCmd521001List.getMap("DUE_DT"));
							}
							/* 지방세 */
							else {
								if (mfCmd521001List.getMap("DLQ_DIV").equals("1") && !mfCmd521001List.getMap("TAX_DIV").equals("3")){
									/* 납부금액 */
									mfRepeatData.setMap("NAP_AMT", cUtil.getNapAmt(mfCmd521001List.getMap("TAX_DT").toString(), napGubun
												                				, Long.parseLong(mfCmd521001List.getMap("MNTX").toString())  	//본세
																				, Long.parseLong(mfCmd521001List.getMap("CPTX").toString())		//도시계회
																				, Long.parseLong(mfCmd521001List.getMap("CFTX").toString())		//공동시설세
																				, Long.parseLong(mfCmd521001List.getMap("LETX").toString())		//교육세
																				, Long.parseLong(mfCmd521001List.getMap("ASTX").toString())));	//농특세
									
									/* 납기 내후 구분 */
									mfRepeatData.setMap("NAP_GUBUN"  , napGubun);
									
									/* 납기일자 */
									if(napGubun.equals("B")){
										mfRepeatData.setMap("NAP_DATE"   , mfCmd521001List.getMap("DUE_DT"));
									} else if (napGubun.equals("A")){
										mfRepeatData.setMap("NAP_DATE"   , mfCmd521001List.getMap("DUE_F_DT"));
									}
									
								} else {
			                		/* 납부금액 */
			                		mfRepeatData.setMap("NAP_AMT", cUtil.getNapAmt(Long.parseLong(mfCmd521001List.getMap("MNTX").toString())			//본세
													                				, Long.parseLong(mfCmd521001List.getMap("MNTX_ADTX").toString())  	//본세 가산금
																					, Long.parseLong(mfCmd521001List.getMap("CPTX").toString())			//도시계회세
																					, Long.parseLong(mfCmd521001List.getMap("CPTX_ADTX").toString())	//도시계회세 가산금
																					, Long.parseLong(mfCmd521001List.getMap("CFTX").toString())			//공동시설세
																					, Long.parseLong(mfCmd521001List.getMap("CFTX_ADTX").toString())	//공동시설세 가산금
																					, Long.parseLong(mfCmd521001List.getMap("LETX").toString())			//교육세
																					, Long.parseLong(mfCmd521001List.getMap("LETX_ADTX").toString())	//교육세 가산금
																					, Long.parseLong(mfCmd521001List.getMap("ASTX").toString())			//농특세
																				    , Long.parseLong(mfCmd521001List.getMap("ASTX_ADTX").toString())));	//농특세 가산금
			                		/* 납기내후구분 */
			                		mfRepeatData.setMap("NAP_GUBUN"  , "B");
			                		/* 납기일자 */
			                		mfRepeatData.setMap("NAP_DATE"   , mfCmd521001List.getMap("DUE_DT"));
								}
							
							}
							
							/*자동이체 등록 여부 */
							mfRepeatData.setMap("AUTO_REG"   , "N");			
							
							alRepeatData.add(mfRepeatData);
														
						} 
					}
					
				}else{ 
					/* 조회건수가 없는 경우 전문생성
					 * */
					
					resCode = "111";  /*조회내역없음*/
					
					sendMap.setMap("DATA_TOT" , "000");  /*고지총건수 */
					sendMap.setMap("DATA_NUM" , "00");   /*데이터건수 */
				} 
				
			}

			log.info(cUtil.msgPrint("6", "","BP521001 chk_bs_521001()", resCode));
			
        } catch (Exception e) {
			
        	resCode = "093";
        	
			e.printStackTrace();
			log.error("============================================");
			log.error("== chk_bs_521001 Exception(시스템) ");
			log.error("============================================");
		}
        
        /*여기서 공통헤드를 셋팅한다. 바뀌는 부분만 다시 셋팅해서 전문을 조립*/
        headMap.setMap("RS_FLAG"   , "G");         /*지로이용기관(G) */
        headMap.setMap("RESP_CODE" , resCode);     /*응답코드*/
        headMap.setMap("GCJ_NO"    , "0" + sg_code + "0" + CbUtil.lPadString(String.valueOf(SeqNumber()), 8, '0'));     /*이용기관 일련번호*/
        headMap.setMap("TX_GUBUN"  , "0210");
        
        return kfField.makeSendReptBuffer(headMap, sendMap, alRepeatData);
	}

	/*----------------------------------------------*/
	/* 일련번호 가져오기 테스트*/
	/*----------------------------------------------*/
	private int SeqNumber(){
    	return (Integer)sqlService_cyber.queryForBean("CODMBASE.CODM_GETSEQNO", "00");
    }
}

