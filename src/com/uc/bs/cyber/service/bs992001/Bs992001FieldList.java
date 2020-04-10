/**
 *  주시스템명 : 사이버지방세청 고도화
 *  업  무  명 : 납부재취소 전문 
 *  기  능  명 : 부산은행-지방세 납부재취소 
 *              
 *  클래스  ID : Bs992001FieldList
 *  변경  이력 :
 * ------------------------------------------------------------------------
 *  작성자     소속  		 일자      Tag   내용
 * ------------------------------------------------------------------------   
 * 송동욱   다산(주)  2011.06.24     %01%  신규작성
 *  
 */
package com.uc.bs.cyber.service.bs992001;

import com.uc.bs.cyber.field.Comd_WorkKfField;
/**
 * @author Administrator
 *
 */
public class Bs992001FieldList extends Comd_WorkKfField {
			
	/**
	 * 생성자
	 * 지방세 납부 재취소 전문생성
	 */
	public Bs992001FieldList() {
		// TODO Auto-generated constructor stub
		super();

		/*결제원 송수신 전문 여기서는 송수신이 동일하다.*/
		
		/*
		 * 송신전문(사이버)
		 * */	
		addSendField("OUTBANK_CODE"           , 7       ,"C");   /*출금은행 점별 코드                                      */
		addSendField("NAPBU_JUMIN_NO"         ,13       ,"C");   /*납부자 주민(사업자)등록번호                             */
		addSendField("B_OUTBANKJNO"           ,12       ,"C");   /*원거래 출금 은행 전문 관리 번호 / 원거래 거래 고유 번호 */
		addSendField("B_OUTBANKTXDATE"        ,12       ,"H");   /*원거래 출금 은행 전문 전송 일시                         */
		addSendField("B_OUTACCT_NO"           ,16       ,"C");   /*원거래 출금 계좌 번호                                   */
		addSendField("B_NAPBU_AMT"            ,15       ,"H");   /*원거래 납부 금액                                        */
		addSendField("CANCEL_GUBUN"           , 1       ,"C");   /*취소 사유                                               */
		addSendField("ORI_TRADE"              , 1       ,"C");   /*원거래 납부 형태 구분                                   */
		addSendField("RESERV1"                , 9       ,"C");   /*예비 정보 FIELD                                         */	

		/*
		 * 수신전문(결제원)
		 * 송수신전문이 동일하므로 송신전문을 수신전문에 대체시킴
		 * */
	    this.addRecvFieldAll(sendField);
        
	}
	
}
