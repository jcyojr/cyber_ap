/**
 *  주시스템명 : 사이버지방세청 고도화
 *  업  무  명 : 내부 전문 송수신 연계
 *  기  능  명 : 결제원-환경개선부담금 상세조회 전문셋팅 
 *              
 *  클래스  ID : Kf201001FieldList
 *  변경  이력 :
 * ------------------------------------------------------------------------
 *  작성자     소속  		 일자      Tag   내용
 * ------------------------------------------------------------------------   
 * 소도우   유채널(주)  2011.06.08     %01%  신규작성
 *  
 */
package com.uc.bs.cyber.service.kf201002;

import com.uc.bs.cyber.field.Comd_WorkKfField;

/**
 * @author Administrator
 *
 */
public class Kf201002FieldList extends Comd_WorkKfField {
				
	/**
	 * 생성자
	 * 환경개선부담금 상세조회 전문생성
	 */
	public Kf201002FieldList() {
		// TODO Auto-generated constructor stub
		super();
		
		/*결제원 송수신 전문 여기서는 송수신이 동일하다.*/
		
		/*
		 * 송신전문(사이버)
		 * */
		addSendField("ETAX_NO"              , 29 ,  "C"  );  /*전자납부번호              */
		addSendField("JUMIN_NO"             , 13 ,  "C"  );  /*주민(사업자,법인)등록번호 */
		addSendField("SIDO"                 , 2  ,  "H"  );  /*시도                      */
		addSendField("GU_CODE"              , 3  ,  "H"  );  /*과세기관(시군구)          */
		addSendField("CONFIRM_NO1"          , 1  ,  "H"  );  /*검증번호 1                */
		addSendField("HCALVAL"              , 2  ,  "H"  );  /*회계                      */
		addSendField("GWA_MOK"              , 6  ,  "H"  );  /*과목/세목                 */
		addSendField("TAX_YYMM"             , 6  ,  "H"  );  /*년도/기분                 */
		addSendField("KIBUN"                , 1  ,  "H"  );  /*구분                      */
		addSendField("DONG_CODE"            , 3  ,  "H"  );  /*행정동(읍면동)            */
		addSendField("GWASE_NO"             , 6  ,  "H"  );  /*관리 번호                 */
		addSendField("CONFIRM_NO2"          , 1  ,  "H"  );  /*검증번호 2                */
		addSendField("NAP_NAME"             , 40 ,  "C"  );  /*납부자 성명               */
		addSendField("NAP_BFAMT"            , 15 ,  "H"  );  /*납기내 금액               */
		addSendField("NAP_AFAMT"            , 15 ,  "H"  );  /*납기후 금액               */
	    addSendField("CONFIRM_NO3"          , 1  ,  "H"  );  /*검증번호 3                */
	    addSendField("GWASE_RULE"           , 15 ,  "H"  );  /*과세 표준                 */
	    addSendField("BONSE"                , 15 ,  "H"  );  /*부담금                    */
	    addSendField("BONSE_ADD"            , 15 ,  "H"  );  /*부담금 가산금             */
	    addSendField("DOSISE"               , 15 ,  "H"  );  /*미수 부담금               */
	    addSendField("DOSISE_ADD"           , 15 ,  "H"  );  /*미수 부담금 가산금        */
	    addSendField("GONGDONGSE"           , 15 ,  "H"  );  /*예비 정보 FIELD 1         */
	    addSendField("GONGDONGSE_ADD"       , 15 ,  "H"  );  /*예비 정보 FIELD 2         */
	    addSendField("EDUSE"                , 15 ,  "H"  );  /*예비 정보 FIELD 3         */
	    addSendField("EDUSE_ADD"            , 15 ,  "H"  );  /*예비 정보 FIELD 4         */
	    addSendField("NAP_BFDATE"           , 8  ,  "H"  );  /*납기일 (납기내)           */
	    addSendField("NAP_AFDATE"           , 8  ,  "H"  );  /*납기일 (납기후)           */
	    addSendField("CONFIRM_NO4"          , 1  ,  "H"  );  /*검증번호 4                */
	    addSendField("FILLER1"              , 1  ,  "H"  );  /*필러                      */
	    addSendField("CONFIRM_NO5"          , 1  ,  "H"  );  /*검증번호 5                */
	    addSendField("GWASE_DESC"           , 60 ,  "C"  );  /*과세 사항                 */
	    addSendField("GWASE_PUB_DESC"       , 20 ,  "C"  );  /*과세 표준 설명            */
	    addSendField("GOJICR_DATE"          , 8  ,  "H"  );  /*고지자료 발생일자         */
	    addSendField("JADONG_YN"            , 1  ,  "C"  );  /*자동이체 등록 여부        */
	    addSendField("JIJUM_CODE"           , 7  ,  "H"  );  /*수납은행 점별 코드        */
	    addSendField("NAPBU_DATE"           , 14 ,  "H"  );  /*납부 일시                 */
	    addSendField("NP_BAF_GUBUN"         , 1  ,  "C"  );  /*납기 내후 구분            */
	    addSendField("TAX_GOGI_GUBUN"       , 1  ,  "C"  );  /*세금 종류 구분            */
	    addSendField("JA_GOGI_GUBUN"        , 1  ,  "C"  );  /*장표 고지 형태            */
	    addSendField("RESERV2"              , 3  ,  "C"  );  /*예비 정보 FIELD 5         */

		/*
		 * 수신전문(결제원)
		 * 송수신전문이 동일하므로 송신전문을 수신전문에 대체시킴
		 * */
	    
	    addRecvField("ETAX_NO"              , 29 ,  "C"  );  /*전자납부번호              */
	    addRecvField("JUMIN_NO"             , 13 ,  "C"  );  /*주민(사업자,법인)등록번호 */
	    
	    	    
	}
	
	
}
