/**
 *  주시스템명 : 사이버지방세청 고도화
 *  업  무  명 : 결제원-상하수도고지내역 간략조회 
 *  기  능  명 : 결제원-사이버세청 
 *               업무처리
 *               
 *  클래스  ID : Kf251001Service
 *  변경  이력 :
 * ------------------------------------------------------------------------
 *  작성자     소속  		 일자      Tag   내용
 * ------------------------------------------------------------------------   
 * 소도우   유채널(주)    2011.06.13   %01%  신규작성
 * 임창섭   유채널(주)  2013.11.04     %02%  간단e납부관련(세외수입, 환경개선부담금) 추가
 */
package com.uc.bs.cyber.service.kf251001;

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
public class Kf251001Service {

	protected Log log = LogFactory.getLog(this.getClass());
	
	private ApplicationContext appContext = null;
	
	private IbatisBaseService sqlService_cyber = null;
		
	private Kf251001FieldList kfField = null;
	
	CbUtil cUtil = null;
	
	/**
	 * 생성자
	 */
	public Kf251001Service(ApplicationContext appContext) {
		// TODO Auto-generated constructor stub
		
		setAppContext(appContext);
		
		kfField = new Kf251001FieldList();
		
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
	 * 상하수도 고지내역 간략조회 시작
	 * */
	public byte[] chk_kf_251001(MapForm headMap, MapForm mf) throws Exception {
		
		MapForm sendMap = new MapForm();
		
		CbUtil cUtil = null;
		
		/*상하수도요금 고지내역조회 시 사용 */
		String resCode = "000";       /*응답코드*/
		String giro_no = "1004102";   /*부산시지로코드*/
		String sg_code = "26";        /*부산시기관코드*/
		
		String POINTNO = "";          /*지정번호*/
		String DATANUM = "";          /*데이터갯수*/
		int START_IDX = 0;
		
		/*필요변수들*/
		String icheno = "";
		String custno = "";
		
		
		/*반복부*/
		ArrayList<MapForm> alRepeatData = new ArrayList<MapForm>();
		
		try{
			cUtil = CbUtil.getInstance();
			
			log.info(cUtil.msgPrint("1", "S","KP251001 chk_kf_251001()", resCode));
			
			/*전문체크*/
			
			/*기관*/
			if(!headMap.getMap("GPUB_CODE").equals(sg_code)){
				resCode = "339";
			}
			/*발행기관지로코드*/
			if(!headMap.getMap("GJIRO_NO").equals(giro_no)){
				resCode = "324";
			}
			
			POINTNO =  (String)mf.getMap("POINTNO");  /*지정번호*/
			DATANUM =  (String)mf.getMap("DATANUM");  /*건수*/
			
			/*조회구분값에 따른 조회설정값 입력*/
			if (mf.getMap("SEARCHGUBUN").equals("S")) {
				
				String searchky = cUtil.waterChgCustno("S", mf.getMap("SEARCHKEY").toString().substring(20,29));
				
				if(searchky.equals(mf.getMap("SEARCHKEY").toString())) {
					
					custno = mf.getMap("SEARCHKEY").toString().substring(20,29);
					
				} else {
					
					resCode = "311";   /*고지내역 없음*/
 				
				}
				
			} else if (mf.getMap("SEARCHGUBUN").equals("E")) {
				
				icheno = mf.getMap("SEARCHKEY").toString();   /*전자납부번호*/
				
			} else {
				
				resCode = "324";   /*조회내역없음*/
			}
			
			mf.setMap("CUST_NO", custno);
			mf.setMap("EPAY_NO", icheno);
			
			int levyCnt = 0;
			
			/*여기까지 정상이면....*/
			if(resCode.equals("000")){
				/*사이버 DB와 비교하여 고지내역을 확인한다..*/
				
				ArrayList<MapForm>  kfCmd251001List  =  sqlService_cyber.queryForList("KF251001.SELECT_SHSD_LEVY_LIST", mf);
				
				levyCnt = kfCmd251001List.size();   /*고지총건수*/
				
				if(levyCnt <= 0){
				   resCode = "311";  /*부과내역 없음*/

				} else {
							            
				  //간단e납부 테스트시 pointno가 '0'으로 올 경우 에러 확인 수정
                    if (Integer.parseInt(POINTNO)<=0) POINTNO = "001";

		            START_IDX = Integer.parseInt(POINTNO) -1;
		            
		            if (START_IDX >= levyCnt) {
		            	
		            	resCode = "311";  /*부과내역 없음*/  	

		            } else {
		            	
		            	resCode = "000"; 
		            	
		            	if (Integer.parseInt(DATANUM) > 10 && Integer.parseInt(DATANUM) < 99 )
		                    DATANUM = "10";

		            	if ( levyCnt < (START_IDX+Integer.parseInt(DATANUM)) )
		                    DATANUM = String.valueOf( levyCnt - START_IDX );

		            	if (Integer.parseInt(DATANUM) > 10)
		                    DATANUM = "10";    
		            	
		            	/*0210 응답전문 생성 */
		            	sendMap.setMap("SEARCHGUBUN", mf.getMap("SEARCHGUBUN")); /*조회구분*/
		            	sendMap.setMap("SEARCHKEY"  , mf.getMap("SEARCHKEY"));   /*조회번호*/
		    			sendMap.setMap("CUSTNO"     , mf.getMap("SEARCHKEY"));   /*수용가번호  */
		            	sendMap.setMap("DATATOT"    , levyCnt);                  /*고지총 건수 */ 
		            	sendMap.setMap("POINTNO"    , POINTNO);                  /*지정번호    */
		            	sendMap.setMap("DATANUM"    , DATANUM);                  /*데이터 건수 */

		            	String epay_no    = "";
		            	String cust_no    = "";
		            	String gubun      = "";
		            	String napkiGubun = "";
		            	
		            	String NAPDATE    = "";
		            	String NAPGUBUN   = "";
		            	long NAPAMT       = 0;
		            	
		            	//for ( int al_cnt = 0;  al_cnt < kfCmd251001List.size();  al_cnt++)   {//2013-10-29 금결원 이보람 과장과 통화(전체전문길이 1Kbyte제한)후 아래로 수정 by 임창섭
	                    for ( int al_cnt = START_IDX;  al_cnt < (START_IDX + Integer.parseInt(DATANUM));  al_cnt++)  {
		            		
		            		MapForm mfCmd251001List  =  kfCmd251001List.get(al_cnt);
		            		
		            		log.debug("mfCmd251001List = " + mfCmd251001List.getMaps());
		            		
		            		MapForm mfRepeatData = new MapForm();
							
							alRepeatData.add(mfRepeatData);
		            		
							epay_no = (String) mfCmd251001List.getMap("EPAY_NO");
							cust_no = (String) mfCmd251001List.getMap("CUST_NO");
							gubun   = (String) mfCmd251001List.getMap("GUBUN");
							
							if (mfCmd251001List.getMap("GUBUN").equals("3")) { /*수시*/
								gubun = "1";
							}
							
							//납기내(B), 납기후(A) 체크
			                napkiGubun = cUtil.getNapGubun((String)mfCmd251001List.getMap("DUE_DT"), (String)mfCmd251001List.getMap("DUE_F_DT"));
							
			                if(napkiGubun.equals("B")){
			                	
			                	NAPDATE  = (String)mfCmd251001List.getMap("DUE_DT");
			                    NAPGUBUN = napkiGubun;                  //납기내후구분
			                    NAPAMT   = ((BigDecimal)mfCmd251001List.getMap("SUM_B_AMT")).longValue();  //체납분인경우 체납액
			                    
			                } else if (napkiGubun.equals("A")){
			                	
			                	NAPDATE  = (String)mfCmd251001List.getMap("DUE_F_DT");
			                    NAPGUBUN = napkiGubun;                 //납기내후구분
			                    NAPAMT   = ((BigDecimal)mfCmd251001List.getMap("SUM_F_AMT")).longValue();
			                    
			                } else {
			                	resCode = "093"; 
			                }
			                
							mfRepeatData.setMap("DANGGUBUN"  , gubun);
							mfRepeatData.setMap("BYYYMM"     , (String)mfCmd251001List.getMap("TAX_YY") + (String)mfCmd251001List.getMap("TAX_MM"));
							mfRepeatData.setMap("NAPAMT"     , NAPAMT);
							mfRepeatData.setMap("NAPGUBUN"   , NAPGUBUN);
							mfRepeatData.setMap("NAPDATE"    , NAPDATE);
							mfRepeatData.setMap("AUTOREG"    , "N");
							
		            	}
   	
		            }		            	
					
				}
				
			} 
			

			log.info(cUtil.msgPrint("1", "","KP251001 chk_kf_251001()", resCode));
			
        } catch (Exception e) {
			
			e.printStackTrace();
			log.error("============================================");
			log.error("== chk_kf_251001 Exception(시스템) ");
			log.error("============================================");
		}
        
        if(resCode.equals("311")) {
        	sendMap = mf;     /*부과내역이 없는 경우 온데로...*/
        	
        	sendMap.setMap("DATATOT" , "000");                  /*고지총 건수 */ 
        	sendMap.setMap("DATANUM" , "00" );                  /*데이터 건수 */
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
