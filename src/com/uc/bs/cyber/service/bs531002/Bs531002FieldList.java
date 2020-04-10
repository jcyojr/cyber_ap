/**
 *  주시스템명 : 사이버지방세청 고도화
 *  업  무  명 : 내부 전문 송수신 연계
 *  기  능  명 : 부산은행-상수도 및 특별회계일부 사전 수취조회
 *              
 *  클래스  ID : Bs531002FieldList
 *  변경  이력 :
 * ------------------------------------------------------------------------
 *  작성자     소속  		 일자      Tag   내용
 * ------------------------------------------------------------------------   
 * 최유환     유채널     2015.01.19     %01%  신규작성
 *  
 */
package com.uc.bs.cyber.service.bs531002;

import com.uc.bs.cyber.field.Comd_WorkBsField;
/**
 * @author Administrator
 *
 */
public class Bs531002FieldList extends Comd_WorkBsField {
			
	
	/**
	 * 생성자
	 * BS 가상계좌 사전수취 조회 전문생성
	 */
	public Bs531002FieldList() {
		// TODO Auto-generated constructor stub
		super();

		/*결제원 송수신 전문 여기서는 송수신이 동일하다.*/
		
		/*
		 * 송신전문(사이버)
		 * */	
	    addSendField("TRN_DATE"               , 8   ,  "C"  );  /*거래일자        */
	    addSendField("TRN_TIME"               , 6   ,  "C"  );  /*거래시간	    */
	    addSendField("VIR_ACC_NO"             , 12  ,  "C"  );  /*가상계좌번호    */
 		addSendField("TRN_AMT"                , 13  ,  "H"  );  /*거래금액        */
 		addSendField("MEDA_GBN"               , 2   ,  "C"  );  /*매체구분        */
 		addSendField("TRN_UNIQ_NO"            , 12  ,  "C"  );  /*거래고유번호    */
 		addSendField("REG_NM"                 , 20  ,  "C"  );  /*성명            */
 		addSendField("TRN_UNIQ_NO1"           , 13  ,  "C"  );  /*거래고유번호1   */
 		addSendField("NEW_VIR_ACC_NO"         , 14  ,  "C"  );  /*신가상계좌번호  */
 		addSendField("FILLER2"                , 50  ,  "C"  );  /*공란            */
 		
 		/*
		 * 수신전문(결제원)
		 * 송수신전문이 동일하므로 송신전문을 수신전문에 대체시킴
		 * */
	    this.addRecvFieldAll(sendField);
        
	}
	
	
}
