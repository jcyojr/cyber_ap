/**
 *  주시스템명 : 사이버지방세청 고도화
 *  업  무  명 : 상하수도고지내역 상세조회 
 *  기  능  명 : 결제원-사이버세청 
 *               업무처리
 *               결재원에서 수신된 전문에 대해서 응답전문데이트를 생성하고 
 *               조회결과를 저장한다.
 *  클래스  ID : Kf251002Service
 *  변경  이력 :
 * ------------------------------------------------------------------------
 *  작성자     소속  		 일자      Tag   내용
 * ------------------------------------------------------------------------   
 * 소도우   유채널(주)    2011.06.13   %01%  신규작성
 *  
 */
package com.uc.bs.cyber.service.kf251002;

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
public class Kf251002Service {

	protected Log log = LogFactory.getLog(this.getClass());
	
	private ApplicationContext appContext = null;
	
	private IbatisBaseService sqlService_cyber = null;
		
	private Kf251002FieldList kfField = null;
	
	CbUtil cUtil = null;
	
	/**
	 * 생성자
	 */
	public Kf251002Service(ApplicationContext appContext) {
		// TODO Auto-generated constructor stub
		
		setAppContext(appContext);
		
		kfField = new Kf251002FieldList();
		
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
	public byte[] chk_kf_251002(MapForm headMap, MapForm mf) throws Exception {
		
				
		MapForm sendMap = new MapForm();
		
		/*상하수도 고지내역조회 시 사용 */
		String resCode = "000";       /*응답코드*/
		String giro_no = "1004102";   /*부산시지로코드*/
		String sg_code = "26";        /*부산시기관코드*/
		String napkiGubun = "";

		/*필요변수들*/
		String icheno = "";
		String custno = "";
		
		try{
			cUtil = CbUtil.getInstance();
			
			log.info(cUtil.msgPrint("2", "S","KP251002 chk_kf_251002()", resCode));
			
			/*전문체크*/
			
			/*발행기관분류코드 오류*/
			if(!headMap.getMap("GPUB_CODE").equals(sg_code)){
				resCode = "339";  
			}
			/*지로번호 오류*/
			if(!headMap.getMap("GJIRO_NO").equals(giro_no)){
				resCode = "324";
			}
						
			/*조회구분값에 따른 조회설정값 입력*/
			if (mf.getMap("SEARCHGUBUN").equals("S")) {  /*수용가번호를 사용해서 조회함...*/
				
				if(mf.getMap("SEARCHKEY").toString().length() != 30) {
					
					resCode = "311";   /*고지내역 없음*/
				} else {
					
					String searchky = cUtil.waterChgCustno("S", mf.getMap("SEARCHKEY").toString().substring(20,29));
					
					if(searchky.equals(mf.getMap("SEARCHKEY").toString())) {
						
						custno = mf.getMap("SEARCHKEY").toString().substring(20, 29); /*전자납부번호를 셋팅해야 함.. : 전문이 그래요*/
						
						log.info("SEARCHGUBUN = [" + mf.getMap("SEARCHGUBUN") + "]");
						log.info("custno = [" + custno + "] - 수용가번호");
						
					} else {
						
						resCode = "311";   /*고지내역 없음*/
					}
					
				}
				
				/*전자납부번호인 경우만 : 수용가번호인 경우는 필요없다...*/
				mf.setMap("TAX_YY" , mf.getMap("BYYYMM").toString().substring(0, 4));
				mf.setMap("TAX_MM" , mf.getMap("BYYYMM").toString().substring(4));
				
				
			} else if (mf.getMap("SEARCHGUBUN").equals("E")) { /*전자납부번호를 사용해서 조회함...*/
				
				icheno = mf.getMap("SEARCHKEY").toString();   /*수용가번호를 셋팅*/
				
				log.info("SEARCHGUBUN = [" + mf.getMap("SEARCHGUBUN") + "]");
				log.info("icheno = [" + icheno + "] - 전자납부번호");
								
			} else {
				
				resCode = "341";   /*[341] Χ전자(인터넷)납부번호 틀림*/
			}
			
			log.debug("custno = " + custno);
			
			/*전문으로 받은 조건 정의*/
			mf.setMap("CUST_NO", custno);
			mf.setMap("EPAY_NO", icheno);
			
			int levyCnt = 0;
		
			if(resCode.equals("000")) {
		
				if(custno.length() == 9 || icheno.length() == 17) {
					
					ArrayList<MapForm>  kfCmd251002List  =  sqlService_cyber.queryForList("KF251001.SELECT_SHSD_LEVY_LIST", mf);
					
					levyCnt = kfCmd251002List.size();   /*고지총건수*/
					
					if(levyCnt <= 0){
						resCode = "311";   /*조회내역 없음*/
					} else if ( levyCnt > 1 ) {
						resCode = "094";   /*고지건수2건이상*/
					} else {
						
						resCode = "000";
						
						//전문처리 변수선언
					    String BNAPDATE             = "";       // 납기내 납기일
					    long BNAPAMT                = 0 ;       // 납기내 금액
					    long ANAPAMT                = 0 ;       // 납기후 금액
					    String GUM2                 = "";       // 검증번호2
					    long BSAMT                  = 0 ;       // 상수도 납기내 금액
					    long BHAMT                  = 0 ;       // 하수도 납기내 금액
					    long BGAMT                  = 0 ;       // 지하수도 납기내 금액
					    long BMAMT                  = 0 ;       // 물이용부담금 납기내 금액
					    long ASAMT                  = 0 ;       // 상수도 납기후 금액      
					    long AHAMT                  = 0 ;       // 하수도 납기후 금액      
					    long AGAMT                  = 0 ;       // 지하수도 납기후 금액    
					    long AMAMT                  = 0 ;       // 물이용부담금 납기후 금액
					    String ANAPDATE             = "";       // 납기후 납기일
					    String GUM3                 = "";       // 검증번호3
					    String CNAPTERM             = "";       // 체납기간
					    String ADDR                 = "";       // 주소
					    String USETERM              = "";       // 사용기간
					    String AUTOREG              = "";       // 자동이체등록여부
					    String SNAP_BANK_CODE       = "";       // 수납은행 코드
					    String SNAP_SYMD            = "";       // 수납일자
					    String NAPGUBUN             = "";       // 납기내후구분코드
					    String ETC1                 = "";       // 예비필드1
					    String CUST_ADMIN_NUM       = "";       // 고객관리번호
					    String OCR                  = "";       // OCR

						MapForm mpCmd251002List = kfCmd251002List.get(0);

						String Epay_no = (String) mpCmd251002List.getMap("EPAY_NO");  /*전자납부번호*/
						String Cust_no = (String) mpCmd251002List.getMap("CUST_NO");  /*수용가번호*/
						
						String dang = mf.getMap("DANGGUBUN").toString();
						
						if (dang.equals("")) {
							dang   = (String) mpCmd251002List.getMap("GUBUN");
						}
						
		                //납기일자 SET
		                BNAPDATE = (String) mpCmd251002List.getMap("DUE_DT");         //납기내 납기일
		                ANAPDATE = (String) mpCmd251002List.getMap("DUE_F_DT");       //납기후 납기일
						
		                ADDR     = (String) mpCmd251002List.getMap("ADDRESS");        //주소  
		                OCR      = (String) mpCmd251002List.getMap("OCR_1") + (String) mpCmd251002List.getMap("OCR_2");
		                
		                //납기내(B), 납기후(A) 체크
		                napkiGubun = cUtil.getNapGubun(BNAPDATE, ANAPDATE);

		                NAPGUBUN = napkiGubun;
		                
						if(dang.equals("1") || dang.equals("3")) {
							
							if(log.isDebugEnabled()){
								log.debug("정기/수시 Starting");
							}

							
			                //납부총액 SET
			                BNAPAMT  = ((BigDecimal)mpCmd251002List.getMap("SUM_B_AMT")).longValue();         //납기내 금액
			                ANAPAMT  = ((BigDecimal)mpCmd251002List.getMap("SUM_F_AMT")).longValue();         //납기후 금액
							
			                GUM2 = "";
			                
			                //납부세부금액 SET
			                BSAMT    = ((BigDecimal)mpCmd251002List.getMap("FEE_AMT1")).longValue();         //납기내 상수도금액
			                BHAMT    = ((BigDecimal)mpCmd251002List.getMap("FEE_AMT2")).longValue();         //납기내 하수도금액
			                BGAMT    = ((BigDecimal)mpCmd251002List.getMap("FEE_AMT4")).longValue();         //납기내 지하수금액
			                BMAMT    = ((BigDecimal)mpCmd251002List.getMap("FEE_AMT3")).longValue();         //납기내 물이용금액
			                ASAMT    = ((BigDecimal)mpCmd251002List.getMap("FEE_AMT1_H")).longValue();         //납기후 상수도금액
			                AHAMT    = ((BigDecimal)mpCmd251002List.getMap("FEE_AMT2_H")).longValue();         //납기후 하수도금액
			                AGAMT    = ((BigDecimal)mpCmd251002List.getMap("FEE_AMT4_H")).longValue();         //납기후 지하수금액
			                AMAMT    = ((BigDecimal)mpCmd251002List.getMap("FEE_AMT3_H")).longValue();         //납기후 물이용금액

			                GUM3 = "";

			                CNAPTERM = "0";  //체납기간

			                USETERM  = (String) mpCmd251002List.getMap("USE_STT_DATE") + (String) mpCmd251002List.getMap("USE_END_DATE");          //사용기간

			                AUTOREG  = "N";      //자동이체 등록여부


			                /*
			                 * 송신전문에 고객관리번호 셋팅 시
			                 * S 수용가번호로 조회한 경우 : 전자납부번호
			                 * E 전자납부번호로 조회한 경우 : 수용가번호를 셋팅해서 돌려준다.
			                 * */
			                if (mf.getMap("SEARCHGUBUN").equals("S")) {
			                	
			                	/*고객관리번호 : S : 전자납부번호 셋팅 */
			                	CUST_ADMIN_NUM = Epay_no;
			                	
			                } else if (mf.getMap("SEARCHGUBUN").equals("E")) {
			                	
			                	/*고객관리번호 : E : 수용가번호 */
			                	CUST_ADMIN_NUM = cUtil.waterChgCustno("S", Cust_no); 
			                    
			                } else {
			                	
			                	resCode = "094";
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
			                BNAPAMT  = ((BigDecimal)mpCmd251002List.getMap("SUM_B_AMT")).longValue();          //납기내 금액
			                ANAPAMT  = 0;

			                GUM2 = "";

			                //납부세부금액 SET
			                BSAMT    = ((BigDecimal)mpCmd251002List.getMap("FEE_AMT1")).longValue();         //납기내 상수도금액
			                BHAMT    = ((BigDecimal)mpCmd251002List.getMap("FEE_AMT2")).longValue();         //납기내 하수도금액
			                BGAMT    = ((BigDecimal)mpCmd251002List.getMap("FEE_AMT4")).longValue();         //납기내 지하수금액
			                BMAMT    = ((BigDecimal)mpCmd251002List.getMap("FEE_AMT3")).longValue();         //납기내 물이용금액
			                ASAMT    = 0;
			                AHAMT    = 0;
			                AGAMT    = 0;
			                AMAMT    = 0;

			                GUM3 = "";

			                CNAPTERM = (String) mpCmd251002List.getMap("NOT_STT_DATE") + (String) mpCmd251002List.getMap("NOT_END_DATE");          //체납기간
			                USETERM = CNAPTERM;          //사용기간

			                  

			                AUTOREG = "N";      //자동이체 등록여부
							
			                if(mf.getMap("SEARCHGUBUN").equals("S")) 
			                {
			                    CUST_ADMIN_NUM = Epay_no;
			                }
			                else if(mf.getMap("SEARCHGUBUN").equals("E")) 
			                {
			                    CUST_ADMIN_NUM = cUtil.waterChgCustno("S", Cust_no); 
			                }
			                else
			                {
			                	resCode = "094";        // Error : 납기내후값 구분 오류
			                }
							
						}
		
						sendMap.setMap("SEARCHGUBUN"        , mf.getMap("SEARCHGUBUN"));                /*조회 구분('S'/'E')                                */
						sendMap.setMap("SEARCHKEY"          , mf.getMap("SEARCHKEY"));                  /*수용가번호/전자납부번호                           */
						sendMap.setMap("BYYYMM"             , (String)mpCmd251002List.getMap("TAX_YY") + (String)mpCmd251002List.getMap("TAX_MM")); /*부과 년월*/
						sendMap.setMap("DANGGUBUN"          , dang);                                    /*당월 구분                                         */
						sendMap.setMap("NPNO"               , mpCmd251002List.getMap("PRT_NPNO").toString());      /*관리 번호                              */
						sendMap.setMap("NAME"               , mpCmd251002List.getMap("REG_NM"));        /*성명                                              */
						sendMap.setMap("BNAPDATE"           , BNAPDATE);                                /*납기내 납기일              | 납부 마감일          */
						sendMap.setMap("BNAPAMT"            , BNAPAMT);                                 /*납기내 금액                | 체납액               */
						sendMap.setMap("ANAPAMT"            , ANAPAMT);                                 /*납기후 금액                                       */
						sendMap.setMap("GUM2"               , GUM2);                                    /*검증번호 2                                        */
						sendMap.setMap("BSAMT"              , BSAMT);                                   /*상수도납기내금액           |  상수도 체납액       */
						sendMap.setMap("BHAMT"              , BHAMT);                                   /*하수도납기내금액           |  하수도 체납액       */
						sendMap.setMap("BGAMT"              , BGAMT);                                   /*지하수납기내금액           |  지하수 체납액       */
						sendMap.setMap("BMAMT"              , BMAMT);                                   /*물이용부담금납기내금액     |  물이용부담금체납액  */
						sendMap.setMap("ASAMT"              , ASAMT);                                   /*상수도납기후금액                                  */
						sendMap.setMap("AHAMT"              , AHAMT);                                   /*하수도납기후금액                                  */
						sendMap.setMap("AGAMT"              , AGAMT);                                   /*지하수납기후금액                                  */
						sendMap.setMap("AMAMT"              , AMAMT);                                   /*물이용부담금납기후금액                            */
						sendMap.setMap("ANAPDATE"           , ANAPDATE);                                /*납기후 납기일                                     */
						sendMap.setMap("GUM3"               , GUM3);                                    /*검증번호 3                                        */
						sendMap.setMap("CNAPTERM"           , CNAPTERM);                                /*체납 기간                                         */
						sendMap.setMap("ADDR"               , ADDR);                                    /*주소                                              */
						sendMap.setMap("USETERM"            , USETERM);                                 /*사용 기간                                         */
						sendMap.setMap("AUTOREG"            , AUTOREG);                                 /*자동이체 등록 여부                                */
						sendMap.setMap("SNAP_BANK_CODE"     , SNAP_BANK_CODE);                          /*수납은행 점별 코드                                */
						sendMap.setMap("SNAP_SYMD"          , SNAP_SYMD);                               /*납부 일시                                         */
						sendMap.setMap("NAPGUBUN"           , NAPGUBUN);                                /*납기 내후 구분                                    */
						sendMap.setMap("ETC1"               , ETC1);                                    /*예비 정보 FIELD                                   */
						sendMap.setMap("CUST_ADMIN_NUM"     , CUST_ADMIN_NUM);                          /*고객관리번호                                      */
						sendMap.setMap("OCR"                , OCR);                                     /*OCR BAND                                          */
						
					}
					
					
				} else {
					resCode = "341";  /*전자납부번호, 수용가번호 오류*/
				}
				
			}

			log.info(cUtil.msgPrint("2", "","KP251002 chk_kf_251002()", resCode));
			
        } catch (Exception e) {
			
        	resCode = "093";  //Error : 전자납부번호, 수용가번호 오류
        	
			e.printStackTrace();
			log.error("============================================");
			log.error("== chk_kf_251002 Exception(시스템) ");
			log.error("============================================");
		}
        
        /*여기서 공통헤드를 셋팅한다. 바뀌는 부분만 다시 셋팅해서 전문을 조립*/
        headMap.setMap("RS_FLAG"   , "G");         /*지로이용기관(G) */
        headMap.setMap("RESP_CODE" , resCode);     /*응답코드*/
        headMap.setMap("GCJ_NO"    , "0" + sg_code + "0" + CbUtil.lPadString(String.valueOf(SeqNumber()), 8, '0'));     /*이용기관 일련번호*/
        headMap.setMap("TX_GUBUN"  , "0210");
        
        return kfField.makeSendBuffer(headMap, sendMap);
	}

	/*----------------------------------------------*/
	/* 일련번호 가져오기 테스트*/
	/*----------------------------------------------*/
	private int SeqNumber(){
    	return (Integer)sqlService_cyber.queryForBean("CODMBASE.CODM_GETSEQNO", "00");
    }
}
