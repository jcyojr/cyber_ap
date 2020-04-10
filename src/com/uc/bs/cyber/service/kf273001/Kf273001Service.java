/**
 *  주시스템명 : 사이버지방세청 고도화
 *  업  무  명 : 세외수입 납부내역 간략조회 전문
 *  기  능  명 : 부산은행-사이버세청 
 *               업무처리
 *               결재원에서 수신된 전문에 대해서 응답전문데이트를 생성하고 
 *               조회결과를 저장한다.
 *  클래스  ID : Kf273001Service
 *  변경  이력 :
 * ------------------------------------------------------------------------
 *  작성자     소속  		 일자      Tag   내용
 * ------------------------------------------------------------------------   
 * 황종훈   다산(주)    2011.06.13   %01%  신규작성
 * 임창섭   유채널(주)  2013.11.04     %02%  간단e납부관련(세외수입, 환경개선부담금) 추가
 */
package com.uc.bs.cyber.service.kf273001;

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
public class Kf273001Service {

	protected Log log = LogFactory.getLog(this.getClass());
	
	private ApplicationContext appContext = null;
	
	private IbatisBaseService sqlService_cyber = null;
		
	private Kf273001FieldList kfField = null;
	
	CbUtil cUtil = null;
	
	/**
	 * 생성자
	 */
	public Kf273001Service(ApplicationContext appContext) {
		// TODO Auto-generated constructor stub
		
		setAppContext(appContext);
		
		kfField = new Kf273001FieldList();
		
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
	 * 기타세입금 납부내역 간략조회 처리...
	 * */
	public byte[] chk_kf_273001(MapForm headMap, MapForm mf) throws Exception {
		
		MapForm sendMap = new MapForm();
		
		/*기타세입금 고지내역조회 시 사용 */
		String resCode = "000";       /*응답코드*/
//		String giro_no = "1000685";   /*부산시지로코드*/ // 실제 전문보고 바궈야댐 
		String giro_no = "1500172";    /*부산시지로코드- 간단e납부시행으로 기타세입금 지로코드 생김*/
		String sg_code = "26";        /*부산시기관코드*/
		
		String POINTNO = "";          /*지정번호*/
		String DATANUM = "";          /*데이터갯수*/
		int START_IDX  = 0;
		
		/*반복부*/
		ArrayList<MapForm> alRepeatData = new ArrayList<MapForm>();
		
		try{
			cUtil = CbUtil.getInstance();
			
			log.info(cUtil.msgPrint("8", "S","BP273001 chk_kf_273001()", resCode));
			
			/*전문체크*/
			
			/*기관*/
			if(!headMap.getMap("GPUB_CODE").equals(sg_code)){
				resCode = "122";
			}
			/*발행기관지로코드*/
			if(!headMap.getMap("GJIRO_NO").equals(giro_no)){
				resCode = "123";
			}
			
			/*주민번호*/
			if(mf.getMap("JUMINNO").toString().length() != 13){
				resCode = "340";
			}
			
			POINTNO = (String)mf.getMap("POINTNO"); // 지정번호
			DATANUM = (String)mf.getMap("DATANUM"); // 데이터건수

			if(resCode.equals("000")) {
				
				ArrayList<MapForm>  bsCmd273001List  =  sqlService_cyber.queryForList("KF273001.SELECT_RECIP_LIST", mf);
				
				if ( bsCmd273001List.size() > 0 ) {
					
				  //간단e납부 테스트시 pointno가 '0'으로 올 경우 에러 확인 수정
                    if (Integer.parseInt(POINTNO)<=0) POINTNO = "001";
					
					START_IDX = Integer.parseInt(POINTNO) -1;
					
					if (START_IDX >= bsCmd273001List.size()){
						resCode = "112";
					} else {
						resCode = "000";
						
						/*지정번호 처리로직*/
						if (Integer.parseInt(DATANUM) > 10 )
			                DATANUM = "10";
						else if ( bsCmd273001List.size() < (START_IDX+Integer.parseInt(DATANUM)) )
			                DATANUM = String.valueOf( bsCmd273001List.size() - START_IDX );
			              
						/*0210 응답전문 생성 */
						sendMap.setMap("JUMINNO" , mf.getMap("JUMINNO"));     /*주민번호   */
			            sendMap.setMap("DATATOT" , bsCmd273001List.size());   /*고지총건수 */
			  			sendMap.setMap("POINTNO" , POINTNO);                  /*지정번호   */
						
			  			/*1건이상 조회가 된 경우 전문생성*/
			  			sendMap.setMap("DATANUM", DATANUM);                  /*데이터건수 */

			  			//for ( int al_cnt = Integer.parseInt(POINTNO);  al_cnt < (Integer.parseInt(POINTNO) +Integer.parseInt(DATANUM));  al_cnt++)   {//2013-10-29 금결원 이보람 과장과 통화(전체전문길이 1Kbyte제한)후 아래로 수정 by 임창섭
		                for ( int al_cnt = START_IDX;  al_cnt < (START_IDX + Integer.parseInt(DATANUM));  al_cnt++)  {
							
							MapForm mfCmd273001List  =  bsCmd273001List.get(al_cnt);
							
							MapForm kfRepeatData = new MapForm();
							
							kfRepeatData.setMap("PUBGCODE"   , headMap.getMap("GPUB_CODE"));
							kfRepeatData.setMap("JIRONO"     , headMap.getMap("GIRO_NO"));
							kfRepeatData.setMap("ETAXNO"     , mfCmd273001List.getMap("TAX_NO"));
							kfRepeatData.setMap("ETAXNO"     , mfCmd273001List.getMap("EPAY_NO"));
							kfRepeatData.setMap("GWAMOK"     , mfCmd273001List.getMap("TAX_ITEM"));
							kfRepeatData.setMap("GWADATE"    , mfCmd273001List.getMap("TAX_YM"));
							kfRepeatData.setMap("KIBUN"      , mfCmd273001List.getMap("TAX_DIV"));
							kfRepeatData.setMap("NAPAMT"     , mfCmd273001List.getMap("SUTT"));
							kfRepeatData.setMap("NAPBU_DATE" , mfCmd273001List.getMap("PAY_DT"));
							kfRepeatData.setMap("NAPBU_SYS"  , mfCmd273001List.getMap("NAPBU_SYS"));
							kfRepeatData.setMap("JIJUM_CODE" , mfCmd273001List.getMap("BANK_CD"));
							
							alRepeatData.add(kfRepeatData);

						} 
					}
					
				}else{ 
					/* 조회건수가 없는 경우 전문생성
					 * */
					
					resCode = "112";  /*조회내역없음*/
				
				} 
				
			}
			
			log.info(cUtil.msgPrint("8", "","BP273001 chk_kf_273001()", resCode));
			
        } catch (Exception e) {
			
			e.printStackTrace();
			log.error("============================================");
			log.error("== chk_kf_273001 Exception(시스템) ");
			log.error("============================================");
		}
        
        if(!resCode.equals("000")) {
        	sendMap = mf;
        	sendMap.setMap("DATATOT"  , "000");  /*고지총건수 */
			sendMap.setMap("DATANUM"  , "00");   /*데이터건수 */
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
