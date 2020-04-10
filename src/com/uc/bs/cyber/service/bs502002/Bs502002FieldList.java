/**
 *  주시스템명 : 사이버지방세청 고도화
 *  업  무  명 : 지방세 납부내역 무통장입금 통보조회
 *  기  능  명 : 결제원-지방세 납부내역(무통장) 입금통보전문정의 
 *              
 *  클래스  ID : Bs502002FieldList
 *  변경  이력 :
 * ------------------------------------------------------------------------
 *  작성자     소속  		 일자      Tag   내용
 * ------------------------------------------------------------------------   
 * 소도우   유채널(주)  2011.06.08     %01%  신규작성
 *  
 */
package com.uc.bs.cyber.service.bs502002;

import com.uc.bs.cyber.field.Comd_WorkKfField;
/**
 * @author Administrator
 *
 */
public class Bs502002FieldList extends Comd_WorkKfField {
			
	
	/**
	 * 생성자
	 * 지방세 납부결과 통보 전문생성
	 */
	public Bs502002FieldList() {
		// TODO Auto-generated constructor stub
		super();

		/*결제원 송수신 전문 여기서는 송수신이 동일하다.*/
		
		/*
		 * 송신전문(사이버)
		 * */	
 		addSendField("NAPBU_AMT"        , 15 ,  "H"  );  	/*납부금액 			*/
 		addSendField("NAPBU_DATE"      	, 8  ,  "H"  );  	/*납부일자          */
 		addSendField("OUTBANK_CODE"     , 7  ,  "H"  );  	/*출금은행 점별코드 */
 		addSendField("NAPBU_NAME"       , 20 ,  "C"  );  	/*납부자 성명       */
 		addSendField("OUTACCT_NO"       , 16 ,  "C"  );  	/*출금계좌번호      */
 		addSendField("GUBUN"        	, 2  ,  "H"  );  	/*매체구분          */
 		addSendField("INACCT_NO"        , 12 ,  "C"  );  	/*입금계좌번호      */
 		addSendField("RESERV2"        	, 5  ,  "C"  );  	/*예비정보 필드     */  
        
		/*
		 * 수신전문(결제원)
		 * 송수신전문이 동일하므로 송신전문을 수신전문에 대체시킴
		 * */
	    this.addRecvFieldAll(sendField);
        
	}
}
