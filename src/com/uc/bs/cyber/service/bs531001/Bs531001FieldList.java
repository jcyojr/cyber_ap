/**
 *  주시스템명 : 사이버지방세청 고도화
 *  업  무  명 : 내부 전문 송수신 연계
 *  기  능  명 : BS 계상계좌 입금 전문생성 
 *              
 *  클래스  ID : Bs531001FieldList
 *  변경  이력 :
 * ------------------------------------------------------------------------
 *  작성자     소속  		 일자      Tag   내용
 * ------------------------------------------------------------------------   
 * 최유환     유채널     2015.01.19     %01%  신규작성
 *  
 */
package com.uc.bs.cyber.service.bs531001;

import com.uc.bs.cyber.field.Comd_WorkBsField;
/**
 * @author Administrator
 *
 */
public class Bs531001FieldList extends Comd_WorkBsField {
			
	
	/**
	 * 생성자
	 * BS 가상계좌 입금 전문생성
	 */
	public Bs531001FieldList() {
		// TODO Auto-generated constructor stub
		super();

		/*BS 송수신 전문 여기서는 송수신이 동일하다.*/
		
		/*
		 * 송신전문(사이버)
		 * */	
	    addSendField("ACC_NO"             	, 15  ,  "C"  );  /*계좌번호  입금계좌번호 */
	    addSendField("FILLER2"             	, 2   ,  "C"  );  /*공란					 */
	    addSendField("JOB_GBN"              , 2   ,  "C"  );  /*거래구분  입금 20  입금취소 51  */
 		addSendField("BANK_CODE"            , 3   ,  "C"  );  /*은행코드    */
 		addSendField("TRN_AMT"              , 13  ,  "H"  );  /*거래금액                         */
 		addSendField("TRN_F_AMT"            , 13  ,  "H"  );  /*입금후 잔액          */
 		addSendField("GIRO_CODE"            , 7   ,  "C"  );  /*입금점 지로코드                */
 		addSendField("REG_NM"               , 14  ,  "C"  );  /*성명                      */
 		addSendField("SUPYO_NO"             , 10  ,  "C"  );  /*수표번호                 */
 		addSendField("CASH_AMT"             , 13  ,  "H"  );  /*현금                 */
 		addSendField("TA_SUPYO_AMT"         , 13  ,  "H"  );  /*타행 수표금액                      */
 		addSendField("ETC_SUPYO_AMT"        , 13  ,  "H"  );  /*가게수표, 기타            */
 		addSendField("VIR_ACC_NO"           , 14  ,  "C"  );  /*가상계좌번호                 */
 		addSendField("TRN_DATE"          	, 6   ,  "C"  );  /*거래일자                */
 		addSendField("TRN_TIME"             , 6   ,  "C"  );  /*거래시간               */
 		addSendField("FILLER3"              , 6   ,  "C"  );  /*공란               */
 	                                            
		/*
		 * 수신전문(결제원)
		 * 송수신전문이 동일하므로 송신전문을 수신전문에 대체시킴
		 * */
	    this.addRecvFieldAll(sendField);
        
	}
	
	
}
