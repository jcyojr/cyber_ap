/**
 *  주시스템명 : 사이버지방세청 고도화
 *  업  무  명 : 내부 전문 송수신 연계
 *  기  능  명 : 결제원-상하수도고지내역 간략조회 2
 *               참고) 간략조회에서 전자납부번호가 추가됨.
 *  클래스  ID : Kf251001FieldList
 *  변경  이력 :
 * ------------------------------------------------------------------------
 *  작성자     소속  		 일자      Tag   내용
 * ------------------------------------------------------------------------   
 * 소도우   유채널(주)  2011.06.08     %01%  신규작성
 *  
 */
package com.uc.bs.cyber.service.kf251005;

import com.uc.bs.cyber.field.Comd_WorkKfField;
/**
 * @author Administrator
 *
 */
public class Kf251005FieldList extends Comd_WorkKfField {
			
	
	/**
	 * 생성자
	 * 상하수도 납부내역 간략조회 전문생성
	 */
	public Kf251005FieldList() {
		// TODO Auto-generated constructor stub
		super();

		/*
		 * 송신전문(결제원)
		 * */
		this.addSendField("SEARCHGUBUN"         , 1       ,"C");  /*조회 구분('S')*/  
		this.addSendField("SEARCHKEY"           ,30       ,"C");  /*조회 번호     */
		this.addSendField("CUSTNO"              ,30       ,"C");  /*수용가번호    */
		this.addSendField("DATATOT"             , 3       ,"H");  /*고지 총 건수  */
		this.addSendField("POINTNO"             , 3       ,"H");  /*지정 번호     */
		this.addSendField("DATANUM"             , 2       ,"H");  /*데이터 건수   */
		
		/*
		 * 수신전문(결제원) : 송수신전문이 동일해서
		 * */
		this.addRecvFieldAll(sendField);
		
		/*
		 * 수신전문(반복전문)
		 * */
		this.addRepetField("DANGGUBUN"          , 1       ,"C");  /*당월 구분         */
		this.addRepetField("BYYYMM"             , 6       ,"H");  /*부과 년월         */
		this.addRepetField("NAPAMT"             ,10       ,"H");  /*납부 금액         */
		this.addRepetField("NAPGUBUN"           , 1       ,"C");  /*납기 내후 구분    */
		this.addRepetField("NAPDATE"            , 8       ,"H");  /*납부 기한         */
		this.addRepetField("AUTOREG"            , 1       ,"C");  /*자동이체 등록 여부*/
		this.addRepetField("ICHENO"             ,29       ,"C");  /*전자납부번호/과세번호*/

	}
	
	
}
