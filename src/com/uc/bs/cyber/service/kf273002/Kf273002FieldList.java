/**
 *  주시스템명 : 사이버지방세청 고도화
 *  업  무  명 : 내부 전문 송수신 연계
 *  기  능  명 : 부산은행-세외수입 납부내역 상세조회 
 *              
 *  클래스  ID : kf273002FieldList
 *  변경  이력 :
 * ------------------------------------------------------------------------
 *  작성자     소속  		 일자      Tag   내용
 * ------------------------------------------------------------------------   
 * 황종훈   다산(주)  2011.06.08     %01%  신규작성
 *  
 */
package com.uc.bs.cyber.service.kf273002;

import com.uc.bs.cyber.field.Comd_WorkKfField;
/**
 * @author Administrator
 *
 */
public class Kf273002FieldList extends Comd_WorkKfField {
			
	
	/**
	 * 생성자
	 * 세외수입 납부내역 상세조회 전문생성
	 */
	public Kf273002FieldList() {
		// TODO Auto-generated constructor stub
		super();

		/*결제원 송수신 전문 여기서는 송수신이 동일하다.*/
		
		/*
		 * 송신전문(사이버)
		 * */	
		addSendField("GB"  		           	, 1  ,  "C"  );  /*조회구분 */
	    addSendField("ETAXNO"             	, 32 ,  "C"  );  /*납부번호 */
	    addSendField("EPAYNO"             	, 19 ,  "C"  );  /*전자납부번호				 */
	    addSendField("NAPBU_JUMIN_NO"       , 13 ,  "C"  );  /*주민(사업자,법인)등록번호 */
 		addSendField("JUMIN_NO"             , 13 ,  "C"  );  /*주민(사업자,법인)등록번호 */
 		addSendField("FIELD1"               , 2  ,  "C"  );  /*예비정보 FIELD 1          */
 		addSendField("BUGWA_STAT"          	, 1  ,  "H"  );  /*고지구분        	         */
 		addSendField("GWA_MOK"              , 6  ,  "H"  );  /*과목/세목                 */
 		addSendField("GWA_NM"             	, 50 ,  "C"  );  /*과목/세목명               */
 		addSendField("FIELD2"               , 1  ,  "C"  );  /*예비정보 FIELD 2          */
 		addSendField("OCR_BD"             	, 108,  "C"  );  /*OCR밴드                   */
 		addSendField("NAP_NAME"             , 40 ,  "C"  );  /*납부자 성명               */
 		addSendField("NAP_BFAMT"            , 15 ,  "H"  );  /*납기내 금액               */
 		addSendField("NAP_AFAMT"            , 15 ,  "H"  );  /*납기후 금액               */
	    addSendField("NATN_TAX"             , 11 ,  "H"  );  /*국세		                 */
	    addSendField("NATN_TAX_ADD"         , 11 ,  "H"  );  /*국세가산금	             */
	    addSendField("SIDO_TAX"             , 11 ,  "H"  );  /*시도세  	                 */
	    addSendField("SIDO_TAX_ADD"         , 11 ,  "H"  );  /*시도세 가산금		     */
	    addSendField("SIGUNGU_TAX"          , 11 ,  "H"  );  /*시군구세         		 */
	    addSendField("SIGUNGU_TAX_ADD"      , 11 ,  "H"  );  /*시군구세 가산금	 		 */
	    addSendField("BUN_AMT"              , 11 ,  "H"  );  /*분납이자/기금             */
	    addSendField("BUN_AMT_ADD"          , 15 ,  "H"  );  /*분납이자/기금 가산금      */	  
	    addSendField("FIELD3"               , 11 ,  "C"  );  /*예비정보 FIELD 3          */
 	    addSendField("DUE_DT"           	, 8  ,  "H"  );  /*납기일 (납기내)           */
 	    addSendField("DUE_F_DT"           	, 8  ,  "H"  );  /*납기일 (납기후)           */
 	    addSendField("TAX_GDS"         		, 100,  "C"  );  /*과세 대상                 */
 	    addSendField("TAX_NOTICE_TITLE" 	, 150,  "C"  );  /*부과 내역                 */
 	    addSendField("LEVY_DETAIL1"         , 8  ,  "H"  );  /*고지자료 발생일자         */
 	    addSendField("NAPBU_SYS"            , 1  ,  "C"  );  /*납부이용 시스템           */
 	    addSendField("BANK_CD"              , 7  ,  "H"  );  /*수납은행 점별 코드        */
 	    addSendField("RECIP_DATE"           , 14 ,  "H"  );  /*납부 일시                 */
 	    addSendField("RECIP_AMT"            , 15 ,  "H"  );  /*납부 금액                 */
 	    addSendField("OUTACCT_NO "          , 16 ,  "C"  );  /*출금계좌번호              ?*/
 	    addSendField("FIELD4"               , 15 ,  "C"  );  /*예비 정보 FIELD 4         ?*/	


 	                                                      
		/*
		 * 수신전문(결제원)
		 * 송수신전문이 동일하므로 송신전문을 수신전문에 대체시킴
		 * */
	    this.addRecvFieldAll(sendField);
        
	}
	
	
}
