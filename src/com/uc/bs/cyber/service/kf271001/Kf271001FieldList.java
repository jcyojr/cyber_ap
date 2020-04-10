/**
 *  주시스템명 : 사이버지방세청 고도화
 *  업  무  명 : 내부 전문 송수신 연계
 *  기  능  명 : 결제원-세외수입 고지내역 간략조회 - 목록 전문셋팅
 *              
 *  클래스  ID : Kf271001FieldList
 *  변경  이력 :
 * ------------------------------------------------------------------------
 *  작성자     소속  		 일자      Tag   내용
 * ------------------------------------------------------------------------   
 * 소도우   유채널(주)  2011.06.08     %01%  신규작성
 *  
 */
package com.uc.bs.cyber.service.kf271001;

import com.uc.bs.cyber.field.Comd_WorkKfField;

/**
 * @author Administrator
 *
 */
public class Kf271001FieldList extends Comd_WorkKfField {
			
	
	/**
	 * 생성자
	 * 환경개선부담금 간략조회목록 전문생성
	 */
	public Kf271001FieldList() {
		// TODO Auto-generated constructor stub
		super();
				
		/*
		 * 참고사항) 결제원의 경우 송수신전문이 동일하다.
		 *           반복전문이 있는 경우 반복건수의 Field 는 'DATA_NUM'으로 동일시킨다.
		 * */
		
		/*
		 * 송신전문(결제원)
		 * */
		this.addSendField("JUMIN_NO"         , 13 ,  "C");  /*주민(사업자,법인)등록번호 */
		this.addSendField("DATA_TOT"         , 3  ,  "H");  /*고지 총 건수              */
		this.addSendField("POINT_NO"         , 3  ,  "H");  /*지정 번호                 */
		this.addSendField("DATA_NUM"         , 2  ,  "H");  /*데이터 건수               */
		
		/*
		 * 수신전문(결제원) : 송수신전문이 동일해서
		 * */
		this.addRecvFieldAll(sendField);
		
		/*
		 * 수신전문(반복전문)
		 * */
		this.addRepetField("PUBGCODE"        , 2  ,  "H");  /*발행기관 분류코드         */
		this.addRepetField("JIRONO"          , 7  ,  "H");  /*지로 번호                 */
		this.addRepetField("TAX_NO"          , 32 ,  "C");  /*납부번호                  */
		this.addRepetField("GRNO"         	 , 19 ,  "C");  /* 전자납부번호             */
		this.addRepetField("GWA_MOK"         , 6  ,  "H");  /*과목/세목                 */
		this.addRepetField("GWA_DATE"        , 6  ,  "H");  /*년도/기분                 */
		this.addRepetField("KIBUN"           , 1  ,  "H");  /*장표 고지 형태            */
		this.addRepetField("NAP_AMT"         , 15 ,  "H");  /*납부 금액                 */
		this.addRepetField("NAP_GUBUN"       , 1  ,  "C");  /*납기 내후 구분            */
		this.addRepetField("NAP_DATE"        , 8  ,  "H");  /*납부 기한                 */
		this.addRepetField("AUTO_REG"        , 1  ,  "C");  /*자동이체 등록 여부        */

	}
}
