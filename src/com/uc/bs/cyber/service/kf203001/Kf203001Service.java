/**
 *  주시스템명 : 사이버지방세청 고도화
 *  업  무  명 : 환경개선부담금 납부내역 조회 전문
 *  기  능  명 : 결재원-사이버세청 
 *               업무처리
 *               결재원에서 수신된 전문에 대해서 응답전문데이트를 생성하고 
 *               송신한다.
 *  클래스  ID : Kf203001Service
 *  변경  이력 :
 * ------------------------------------------------------------------------
 *  작성자     소속  		 일자      Tag   내용
 * ------------------------------------------------------------------------   
 * 소도우   유채널(주)    2011.06.13   %01%  신규작성
 * 임창섭   유채널(주)  2013.11.04     %02%  간단e납부관련(세외수입, 환경개선부담금) 추가
 */
package com.uc.bs.cyber.service.kf203001;

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
public class Kf203001Service {

	protected Log log = LogFactory.getLog(this.getClass());
	
	private ApplicationContext appContext = null;
	
	private IbatisBaseService sqlService_cyber = null;
		
	private Kf203001FieldList kfField = null;
	
	CbUtil cUtil = null;
	
	/**
	 * 생성자
	 */
	public Kf203001Service(ApplicationContext appContext) {
		// TODO Auto-generated constructor stub
		
		setAppContext(appContext);
		
		kfField = new Kf203001FieldList();
		
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
	 * 환경개선부담금 고지내역 간략조회 처리...
	 * */
	public byte[] chk_kf_203001(MapForm headMap, MapForm mf) throws Exception {
		
				
		MapForm sendMap = new MapForm();
		
		/*환경개선부담금 고지내역조회 시 사용 */
		String resCode = "000";       /*응답코드*/
		String giro_no = "1004102";   /*부산시지로코드*/
		String sg_code = "26";        /*부산시기관코드*/
		
		String POINTNO = "";          /*지정번호*/
		String DATANUM = "";          /*데이터갯수*/
		int START_IDX = 0;
		
		/*반복부*/
		ArrayList<MapForm> alRepeatData = new ArrayList<MapForm>();
		
		try{
			cUtil = CbUtil.getInstance();
			
			log.info(cUtil.msgPrint("4", "S","KP203001 chk_kf_203001()", resCode));
			
			/*전문체크*/
			
			/*기관*/
			if(!headMap.getMap("GPUB_CODE").equals(sg_code)){
				resCode = "339";
			}
			/*발행기관지로코드*/
			if(!headMap.getMap("GJIRO_NO").equals(giro_no)){
				resCode = "324";
			}
			
			if(resCode.equals("000")) {
				
				POINTNO = (String)mf.getMap("POINTNO"); // 지정번호
				DATANUM = (String)mf.getMap("DATANUM"); // 데이터건수

				ArrayList<MapForm>  kfCmd203001List  =  sqlService_cyber.queryForList("KF201001.SELECT_ENV_KFPAY_LIST", mf);
				
				log.debug("EPAY_NO = [" + mf.getMap("EPAYNO") + "]");
				log.debug("JUMIN_NO = [" + mf.getMap("JUMINNO") + "]");
				log.debug("GPUB_CODE = [" + mf.getMap("GPUBCODE") + "]");
				log.debug("GJIRO_NO = [" + mf.getMap("JIRONO") + "]");
				log.debug("kfCmd203001List() = [" + kfCmd203001List.size() + "]");
				log.debug("resCode = [" + resCode + "]");
				
				
				if ( kfCmd203001List.size() > 0 && resCode.equals("000")) {
					
				  //간단e납부 테스트시 pointno가 '0'으로 올 경우 에러 확인 수정
                    if (Integer.parseInt(POINTNO)<=0) POINTNO = "001";
					
					START_IDX = Integer.parseInt(POINTNO) -1;
					
					if (START_IDX >= kfCmd203001List.size()){
						resCode = "311";
					} else {
						resCode = "000";
						
						/*지정번호 처리로직*/
						if (Integer.parseInt(DATANUM) > 10 && Integer.parseInt(DATANUM) < 99 )
			                DATANUM = "10";

			            if ( kfCmd203001List.size() < (START_IDX+Integer.parseInt(DATANUM)) )
			                DATANUM = String.valueOf( kfCmd203001List.size() - START_IDX );

			            if (Integer.parseInt(DATANUM) > 10)
			                DATANUM = "10";
			              
			            /*0210 응답전문 생성 */
						sendMap.setMap("JUMINNO" , mf.getMap("JUMINNO"));    /*주민번호   */
			            sendMap.setMap("DATATOT" , kfCmd203001List.size());   /*고지총건수 */
			  			sendMap.setMap("POINTNO" , POINTNO);                  /*지정번호   */
						sendMap.setMap("DATANUM" , DATANUM);                  /*지정번호의 데이터건수 */

//						for ( int al_cnt = Integer.parseInt(POINTNO);  al_cnt < (Integer.parseInt(POINTNO) +Integer.parseInt(DATANUM));  al_cnt++)   {//2013-10-29 금결원 이보람 과장과 통화(전체전문길이 1Kbyte제한)후 아래로 수정 by 임창섭
	                    for ( int al_cnt = START_IDX;  al_cnt < (START_IDX + Integer.parseInt(DATANUM));  al_cnt++)  {
							
							MapForm mfCmd203001List  =  kfCmd203001List.get(al_cnt);
							
							MapForm mfRepeatData = new MapForm();
							
							alRepeatData.add(mfRepeatData);
							
							/*발행기관 분류코드          1*/ 
							/*지로 번호                  2*/ 
							/*전자납부번호               3*/ 
							/*과목/세목                  4*/ 
							/*년도/기분(과세년월)        5*/ 
							/*장표 고지 형태             6*/ 
							/*납부 금액                  7*/ 
							/*납부 일자                  8*/ 
							/*납부 이용 시스템           9*/ 
							/*수납은행 점별 코드         10*/ 
							mfRepeatData.setMap("PUBGCODE"    , headMap.getMap("GPUB_CODE"));
							mfRepeatData.setMap("JIRONO"      , headMap.getMap("GJIRO_NO"));
							mfRepeatData.setMap("ETAXNO"      , mfCmd203001List.getMap("EPAY_NO"));
							mfRepeatData.setMap("GWAMOK"      , mfCmd203001List.getMap("TAX_ITEM"));
							mfRepeatData.setMap("GWADATE"     , (String)mfCmd203001List.getMap("TAX_YY") + (String)mfCmd203001List.getMap("TAX_MM"));
							mfRepeatData.setMap("KIBUN"       , mfCmd203001List.getMap("TAX_DIV"));
							mfRepeatData.setMap("NAPAMT"      , mfCmd203001List.getMap("SUM_RCP"));
							mfRepeatData.setMap("NAPBU_DATE"  , mfCmd203001List.getMap("PAY_DT"));
							mfRepeatData.setMap("NAPBU_SYS"   , mfCmd203001List.getMap("SNSU"));
							mfRepeatData.setMap("JIJUM_CODE"  , mfCmd203001List.getMap("BRC_NO"));

						} 
					}

				}else{ 
					/* 조회건수가 없는 경우 전문생성
					 * */
					
					resCode = "312";  /*조회내역없음*/
					sendMap.setMap("DATATOT" , "000");  /*납부총건수 */
					sendMap.setMap("DATANUM" , "00");   /*데이터건수 */
				} 
				
			}
			
			log.info(cUtil.msgPrint("4", "","KP203001 chk_kf_203001()", resCode));

        } catch (Exception e) {
			
			e.printStackTrace();
			log.error("============================================");
			log.error("== chk_kf_203001 Exception(시스템) ");
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
