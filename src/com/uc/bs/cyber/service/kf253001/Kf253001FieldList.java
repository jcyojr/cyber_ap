/**
 *  주시스템명 : 사이버지방세청 고도화
 *  업  무  명 : 내부 전문 송수신 연계
 *  기  능  명 : 결제원-상하수도 납부내역 간략조회 
 *              
 *  클래스  ID : Kf253001FieldList
 *  변경  이력 :
 * ------------------------------------------------------------------------
 *  작성자     소속  		 일자      Tag   내용
 * ------------------------------------------------------------------------   
 * 소도우   유채널(주)  2011.06.20     %01%  신규작성
 *  
 */
package com.uc.bs.cyber.service.kf253001;

import com.uc.bs.cyber.field.Comd_WorkKfField;
/**
 * @author Administrator
 *
 */
public class Kf253001FieldList extends Comd_WorkKfField {
			
	/**
	 * 생성자
	 * 상하수도 납부내역 간략조회 전문생성
	 */
	public Kf253001FieldList() {
		// TODO Auto-generated constructor stub
		super();

		/*
		 * 송신전문(결제원)
		 * */
		this.addSendField("JUMINNO"         ,13      ,"C");     /*납부자 주민(사업자)등록번호*/
		this.addSendField("IN_STDATE"       ,8       ,"H");     /*조회 시작 일자             */
		this.addSendField("IN_ENDATE"       ,8       ,"H");     /*조회 종료 일자             */
		this.addSendField("DATATOT"         ,3       ,"H");     /*납부 총 건수               */
		this.addSendField("POINTNO"         ,3       ,"H");     /*지정 번호                  */
		this.addSendField("DATANUM"         ,2       ,"H");     /*데이터 건수                */
				
		/*
		 * 수신전문(결제원) : 송수신전문이 동일해서
		 * */
		this.addRecvFieldAll(sendField);
		
		/*
		 * 수신전문(반복전문)
		 * */		
		this.addRepetField("DANGGUBUN"      , 1       ,"C");    /*당월 구분         */
		this.addRepetField("CUSTNO"         , 6       ,"H");    /*부과 년월         */
		this.addRepetField("BYYYMM"         , 6       ,"H");    /*부과 년월         */
		this.addRepetField("NAPBU_AMT"      ,10       ,"H");    /*납부 금액         */
		this.addRepetField("NAPBU_DATE"     , 8       ,"H");    /*납부 일자         */
		this.addRepetField("NAPBU_SYS"      , 1       ,"C");    /*납부 이용 시스템  */ 
		this.addRepetField("JIJUM_CODE"     , 7       ,"H");    /*수납은행 점별 코드*/ 
		
	}

}
