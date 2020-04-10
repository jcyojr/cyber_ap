/**
 *  주시스템명 : 사이버지방세청 고도화
 *  업  무  명 : 내부 전문 송수신 연계
 *  기  능  명 : 결제원-세외수입 상세조회 전문셋팅 
 *              
 *  클래스  ID : Kf271002FieldList
 *  변경  이력 :
 * ------------------------------------------------------------------------
 *  작성자     소속  		 일자      Tag   내용
 * ------------------------------------------------------------------------   
 * 소도우   유채널(주)  2011.06.08     %01%  신규작성
 *  
 */
package com.uc.bs.cyber.service.kf271002;

import com.uc.bs.cyber.field.Comd_WorkKfField;

/**
 * @author Administrator
 *
 */
public class Kf271002FieldList extends Comd_WorkKfField {
				
	/**
	 * 생성자
	 * 세외수입 상세조회 전문생성
	 */
	public Kf271002FieldList() {
		// TODO Auto-generated constructor stub
		super();
		
		/*결제원 송수신 전문 여기서는 송수신이 동일하다.*/
		
		/*
		 * 수신전문(결제원)
		 * */
		addRecvField("S_GBN"                , 1  ,  "C"  );  /*조회구분 (G/E)            */
		addRecvField("TAX_NO"               , 32 ,  "C"  );  /*납부번호	                 */
		addRecvField("GRNO"                 , 19 ,  "C"  );  /*전자납부번호              */
		addRecvField("JUMIN_NO"             , 13 ,  "C"  );  /*주민(사업자,법인)등록번호 */		
		
		/*
		 * 송신전문(사이버)
		 * */
		addSendField("S_GBN"              	, 1  ,  "C"  );  /*조회구분 (G/E)            */
		addSendField("TAX_NO"               , 32 ,  "C"  );  /*납부번호	                 */
		addSendField("GRNO"              	, 19 ,  "C"  );  /*전자납부번호              */
		addSendField("JUMIN_NO"             , 13 ,  "C"  );  /*주민(사업자,법인)등록번호 */
		addSendField("FIELD1"               , 2  ,  "C"  );  /*예비 정보 FIELD 1         */
		addSendField("BUGWA_GB"             , 1  ,  "H"  );  /*고지구분 		         */
		addSendField("SEMOK_CD"          	, 6  ,  "C"  );  /*과목/세목                 */
		addSendField("SEMOK_NM"             , 50 ,  "C"  );  /*과목/세목명               */
		addSendField("GBN"              	, 1  ,  "C"  );  /*과목/세목                 */
		addSendField("OCR_BD"             	, 108,  "C"  );  /*년도/기분                 */
		addSendField("NAP_NAME"             , 40 ,  "C"  );  /*납부자 성명               */
		addSendField("NAP_BFAMT"            , 15 ,  "H"  );  /*납기내 금액               */
		addSendField("NAP_AFAMT"            , 15 ,  "H"  );  /*납기후 금액               */
	    addSendField("GUKAMT"          		, 11 ,  "H"  );  /*국세 	                 */
	    addSendField("GUKAMT_ADD"           , 11 ,  "H"  );  /*국세 가산금               */
	    addSendField("SIDO_AMT"             , 11 ,  "H"  );  /*시도세                    */
	    addSendField("SIDO_AMT_ADD"         , 11 ,  "H"  );  /*시도세 가산금             */
	    addSendField("SIGUNGU_AMT"          , 11 ,  "H"  );  /*시군구세      	         */
	    addSendField("SIGUNGU_AMT_ADD"      , 11 ,  "H"  );  /*시군구세 가산금   	     */
	    addSendField("BUNAP_AMT"            , 11 ,  "H"  );  /*분납이자/기금             */
	    addSendField("BUNAP_AMT_ADD"        , 11 ,  "H"  );  /*분납이자/기금 가산금      */
	    addSendField("FIELD2"               , 11 ,  "C"  );  /*예비 정보 FIELD 2         */
	    addSendField("NAP_BFDATE"           , 8  ,  "H"  );  /*납기일 (납기내)           */
	    addSendField("NAP_AFDATE"           , 8  ,  "H"  );  /*납기일 (납기후)           */
	    addSendField("GWASE_ITEM"           , 100,  "C"  );  /*과세대상                  */
	    addSendField("BUGWA_TAB"          	, 150,  "C"  );  /*부과내역                  */
	    addSendField("GOJI_DATE"   		    , 8  ,  "H"  );  /*고지자료 발생일자         */
	    addSendField("OUTO_ICHE_GB"         , 1  ,  "C"  );  /*자동이체등록여부          */
	    addSendField("SUNAB_BANK_CD"        , 7  ,  "H"  );  /*수납은행 점별 코드        */
	    addSendField("RECIP_DATE"           , 14 ,  "H"  );  /*납부일시			         */
	    addSendField("NAPGI_BA_GB"          , 1  ,  "C"  );  /*납기내후구분              */
	    addSendField("FILED3"          		, 15 ,  "C"  );  /*예비정보 FIELD 3          */

  
	}
	
	
}
