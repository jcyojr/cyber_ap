/**
 *  주시스템명 : 사이버지방세청 고도화
 *  업  무  명 : 내부 전문 송수신 연계
 *  기  능  명 : 결제원-환경개선부담금 납부결과통지/ 예약신청 
 *              
 *  클래스  ID : Kf202001FieldList
 *  변경  이력 :
 * ------------------------------------------------------------------------
 *  작성자     소속  		 일자      Tag   내용
 * ------------------------------------------------------------------------   
 * 소도우   유채널(주)  2011.06.08     %01%  신규작성
 *  
 */
package com.uc.bs.cyber.service.kf202001;

import com.uc.bs.cyber.field.Comd_WorkKfField;
/**
 * @author Administrator
 *
 */
public class Kf202001FieldList extends Comd_WorkKfField {
			

	/**
	 * 생성자
	 * 환경개선부담금 납부결과통지 전문생성
	 */
	public Kf202001FieldList() {
		// TODO Auto-generated constructor stub
		
        super();
		
        addSendField("JUMIN_NO"       , 13 ,  "C" );  /*주민(사업자,법인)등록번호  */
		addSendField("RESERV1"        , 6  ,  "C" );  /*예비 정보 FIELD 1          */
		addSendField("ETAX_NO"        , 29 ,  "C" );  /*전자납부번호               */
		addSendField("NAPBU_AMT"      , 15 ,  "H" );  /*납부 금액                  */
		addSendField("NAPBU_DATE"     , 8  ,  "H" );  /*납부(예약) 일자            */
		addSendField("OUTBANK_CODE"   , 7  ,  "H" );  /*출금은행 점별 코드         */
		addSendField("OUTACCT_NO"     , 16 ,  "C" );  /*출금 계좌 번호             */
		addSendField("NAP_TELNO"      , 14 ,  "C" );  /*연락 전화 번호             */
		addSendField("NAPBU_JUMINNO"  , 13 ,  "C" );  /*납부자 주민(사업자)등록번호*/
		addSendField("NAPBU_NAME"     , 10 ,  "C" );  /*납부자 성명                */
		addSendField("ACCT_OWNER"     , 10 ,  "C" );  /*예금주 성명                */
		addSendField("NAPBU_SYS"      , 1  ,  "C" );  /*납부 이용 시스템           */
		addSendField("NAPBU_GUBUN"    , 1  ,  "C" );  /*납부 형태 구분             */
		addSendField("RESERV2"        , 9  ,  "C" );  /*예비 정보 FIELD 2          */

		/*
		 * 수신전문(결제원)
		 * 송수신전문이 동일하므로 송신전문을 수신전문에 대체시킴
		 * */
	    this.addRecvFieldAll(sendField);
	    
	}
	

}
