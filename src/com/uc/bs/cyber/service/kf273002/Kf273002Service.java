/**
 *  주시스템명 : 사이버지방세청 고도화
 *  업  무  명 : 세외수입 납부내역 상세조회 전문
 *  기  능  명 : 부산은행-사이버세청 
 *               업무처리
 *               결재원에서 수신된 전문에 대해서 응답전문데이트를 생성하고 
 *               조회결과를 저장한다.
 *  클래스  ID : Bs273002Service
 *  변경  이력 :
 * ------------------------------------------------------------------------
 *  작성자     소속  		 일자      Tag   내용
 * ------------------------------------------------------------------------   
 * 황종훈   다산(주)    2011.06.13   %01%  신규작성
 *  
 */
package com.uc.bs.cyber.service.kf273002;

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
public class Kf273002Service {

	protected Log log = LogFactory.getLog(this.getClass());
	
	private ApplicationContext appContext = null;
	
	private IbatisBaseService sqlService_cyber = null;
		
	private Kf273002FieldList bsField = null;
	
	CbUtil cUtil = null;
	
	/**
	 * 생성자
	 */
	public Kf273002Service(ApplicationContext appContext) {
		// TODO Auto-generated constructor stub
		
		setAppContext(appContext);
		
		bsField = new Kf273002FieldList();
		
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
	 * 기타세입금 납부내역 상세조회 처리...
	 * */
	public byte[] chk_kf_273002(MapForm headMap, MapForm mf) throws Exception {
		
				
		MapForm sendMap = new MapForm();
		
		/*기타세입금 고지내역조회 시 사용 */
		String resCode = "000";       /*응답코드*/
//		String giro_no = "1000685";   /*부산시지로코드*/
		String giro_no = "1500172";    /*부산시지로코드- 간단e납부시행으로 기타세입금 지로코드 생김*/
		String sg_code = "26";        /*부산시기관코드*/
		
		try{
			cUtil = CbUtil.getInstance();
			
			log.info(cUtil.msgPrint("9", "S","BP273002 chk_kf_273002()", resCode));
			
			/*전문체크*/
			/*기관*/
			if(!headMap.getMap("GPUB_CODE").equals(sg_code)){
				resCode = "122";
			}
			/*발행기관지로코드*/
			if(!headMap.getMap("GJIRO_NO").equals(giro_no)){
				resCode = "123";
			}
			
			/* 조회조건 자르기(납부번호 잘라서 조회조건 만든다) */
			mf.setMap("SGG_COD", mf.getMap("ETAXNO").toString().substring(0, 3)); 	//구청코드
			mf.setMap("ACCT_COD", mf.getMap("ETAXNO").toString().substring(4, 6));  //회계코드
			mf.setMap("TAX_ITEM", mf.getMap("ETAXNO").toString().substring(6, 12)); //과목/세목코드
			mf.setMap("TAX_YY", mf.getMap("ETAXNO").toString().substring(12, 16));  //과세년도
			mf.setMap("TAX_MM", mf.getMap("ETAXNO").toString().substring(16, 18));	//과세월
			mf.setMap("TAX_DIV", mf.getMap("ETAXNO").toString().substring(18, 19));	//기분코드
			mf.setMap("HACD", mf.getMap("ETAXNO").toString().substring(19, 22));	//행정동코드
			mf.setMap("TAX_SNO", mf.getMap("ETAXNO").toString().substring(22, 28));	//과세번호
			
			ArrayList<MapForm>  bsCmd273002List  =  sqlService_cyber.queryForList("BS273002.SELECT_RECIP_LIST", mf);
			
			log.debug("TAXNO = [" + mf.getMap("ETAXNO") + "]");
			log.debug("JUMIN_NO = [" + mf.getMap("JUMIN_NO") + "]");
			
			if(bsCmd273002List.size() <= 0){
				resCode = "112";  // Error : 납부내역없음
			} else if(bsCmd273002List.size() > 1){
				resCode = "094";  // Error : 조회 2건 이상
			} else {
				
				if ( bsCmd273002List.size() > 0 ) {
					
					MapForm recipform = new MapForm();
					
					recipform = bsCmd273002List.get(0);
					
					sendMap.setMap("GB", mf.getMap("GB"));  						/*조회구분 */
					sendMap.setMap("ETAXNO", mf.getMap("ETAXNO"));  				/*납부번호 */
					sendMap.setMap("EPAYNO", mf.getMap("EPAYNO"));  				/*전자납부번호              */
					sendMap.setMap("NAPBU_JUMIN_NO", mf.getMap("NAPBU_JUMIN_NO"));  /*주민(사업자,법인)등록번호 */
					sendMap.setMap("JUMIN_NO", mf.getMap("NAPBU_JUMIN_NO"));  		/*주민(사업자,법인)등록번호 */
					sendMap.setMap("FIELD1", "");  									/*예비정보 FIELD 1          */
					sendMap.setMap("BUGWA_STAT", recipform.getMap("BUGWA_STAT"));  	/*고지구분                  */
					sendMap.setMap("GWA_MOK", recipform.getMap("TAX_ITEM"));  		/*과목/세목                 */
					sendMap.setMap("GWA_NM", recipform.getMap("TAX_NM"));  			/*과목/세목명               */
					sendMap.setMap("FIELD2", "");  									/*예비정보 FIELD 2          */
					sendMap.setMap("OCR_BD", recipform.getMap("OCR_BD"));  			/*OCR밴드                   */
					sendMap.setMap("NAP_NAME", recipform.getMap("REG_NM"));  		/*납부자 성명               */
					sendMap.setMap("NAP_BFAMT", recipform.getMap("PAYMENT_DATE1")); /*납기내 금액               */
					sendMap.setMap("NAP_AFAMT", Long.parseLong(recipform.getMap("PAYMENT_DATE1").toString()) + Long.parseLong(recipform.getMap("AFTPAYMENT_DATE1").toString()));  /*납기후 금액               */
					sendMap.setMap("NATN_TAX", recipform.getMap("NATN_TAX"));  		/*국세                      */
					sendMap.setMap("NATN_TAX_ADD", recipform.getMap("NATN_TAX_ADD"));/*국세가산금               */
					sendMap.setMap("SIDO_TAX", recipform.getMap("SIDO_TAX"));  		/*시도세                    */
					sendMap.setMap("SIDO_TAX_ADD", recipform.getMap("SIDO_TAX_ADD"));/*시도세 가산금            */
					sendMap.setMap("SIGUNGU_TAX", recipform.getMap("SIGUNGU_TAX"));  /*시군구세                 */
					sendMap.setMap("SIGUNGU_TAX_ADD", recipform.getMap("SIGUNGU_TAX_ADD"));  /*시군구세 가산금  */
					sendMap.setMap("BUN_AMT", "");  								/*분납이자/기금             */
					sendMap.setMap("BUN_AMT_ADD", "");  							/*분납이자/기금 가산금      */      
					sendMap.setMap("FIELD3", "");  									/*예비정보 FIELD 3          */
					sendMap.setMap("DUE_DT", recipform.getMap("DUE_DT"));  			/*납기일 (납기내)           */
					sendMap.setMap("DUE_F_DT", recipform.getMap("DUE_F_DT"));  		/*납기일 (납기후)           */
					sendMap.setMap("TAX_GDS", recipform.getMap("TAX_GDS"));  		/*과세 대상                 */
					sendMap.setMap("TAX_NOTICE_TITLE", recipform.getMap("TAX_NOTICE_TITLE"));  /*부과 내역      */
					sendMap.setMap("LEVY_DETAIL1", recipform.getMap("LEVY_DETAIL6"));/*고지자료 발생일자        */
					sendMap.setMap("NAPBU_SYS", recipform.getMap("NAPBU_SYS"));  	/*납부이용 시스템           */
					sendMap.setMap("BANK_CD", recipform.getMap("BANK_CD"));  		/*수납은행 점별 코드        */
					sendMap.setMap("RECIP_DATE", recipform.getMap("PAY_DT") + "000000");/*납부 일시             */
					sendMap.setMap("RECIP_AMT", recipform.getMap("SUM_RCP"));  		/*납부 금액                 */
					sendMap.setMap("OUTACCT_NO", "");  								/*출금계좌번호              */
					sendMap.setMap("FIELD4", "");  									/*예비 정보 FIELD 4         */
					
				}else{ 
					/* 조회건수가 없는 경우 전문생성
					 * */
					
					resCode = "112";  /*조회내역없음*/
					
				} 
				
			}

			log.info(cUtil.msgPrint("9", "","KF273002 chk_kf_273002()", resCode));
			
        } catch (Exception e) {
        	
			resCode = "093";
			
			e.printStackTrace();
			log.error("============================================");
			log.error("== chk_bs_273002 Exception(시스템) ");
			log.error("============================================");
		}
        
        if(!resCode.equals("000")){
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

