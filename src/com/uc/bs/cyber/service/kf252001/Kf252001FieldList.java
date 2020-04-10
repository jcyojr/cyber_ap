/**
 *  주시스템명 : 사이버지방세청 고도화
 *  업  무  명 : 내부 전문 송수신 연계
 *  기  능  명 : 결제원-상하수도요금 납부결과 통지

 *  클래스  ID : Kf252001FieldList
 *  변경  이력 :
 * ------------------------------------------------------------------------
 *  작성자     소속  		 일자      Tag   내용
 * ------------------------------------------------------------------------   
 * 소도우   유채널(주)  2011.06.08     %01%  신규작성
 *  
 */
package com.uc.bs.cyber.service.kf252001;

import com.uc.bs.cyber.field.Comd_WorkKfField;
/**
 * @author Administrator
 *
 */
public class Kf252001FieldList extends Comd_WorkKfField {
			
	
	/**
	 * 생성자
	 * 상하수도 납부결과 통지 송수신 전문생성
	 */
	public Kf252001FieldList() {
		// TODO Auto-generated constructor stub
		super();

		/*
		 * 송신전문(결제원)
		 * */
		this.addSendField("SEARCHKEY"           ,30       ,"C");   /*수용가번호/전자납부번호/과세번호  */
		this.addSendField("BYYYMM"              , 6       ,"H");   /*부과 년월                         */
		this.addSendField("NAPBU_AMT"           ,15       ,"H");   /*납부 금액                         */
		this.addSendField("NAPBU_DATE"          , 8       ,"H");   /*납부 일자 (실수납일)              */
		this.addSendField("OUTBANK_CODE"        , 7       ,"H");   /*출금은행 점별 코드                */
		this.addSendField("OUTACCT_NO"          ,16       ,"C");   /*예비 정보 FIELD 1                 */
		this.addSendField("NAP_TELNO"           ,14       ,"C");   /*예비 정보 FIELD 2                 */
		this.addSendField("NAPBU_JUMINNO"       ,13       ,"C");   /*납부자 주민(사업자)등록번호       */
		this.addSendField("NAPBU_NAME"          ,10       ,"C");   /*예비 정보 FIELD 3                 */
		this.addSendField("ACCT_OWNER"          ,10       ,"C");   /*예비 정보 FIELD 4                 */
		this.addSendField("NAPBU_SYS"           , 1       ,"C");   /*납부 이용 시스템                  */
		this.addSendField("NAPBU_GUBUN"         , 1       ,"C");   /*납부 형태 구분                    */
		this.addSendField("RESERV2"             , 9       ,"C");   /*예비 정보 FIELD 5               
		 */

		/*
		 * 수신전문(결제원) : 송수신전문이 동일해서
		 * */
		this.addRecvFieldAll(sendField);
		
	}
	
	
}
