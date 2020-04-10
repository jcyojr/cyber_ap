/**
 *  주시스템명 : 사이버지방세청 고도화
 *  업  무  명 : 공통 납부내역 정정
 *  기  능  명 : 부산은행-공통 납부내역 정정
 *              
 *  클래스  ID : Bs502101FieldList
 *  변경  이력 :
 * ------------------------------------------------------------------------
 *  작성자     소속  		 일자      Tag   내용
 * ------------------------------------------------------------------------   
 * 소도우   유채널(주)  2011.06.24     %01%  신규작성
 *  
 */
package com.uc.bs.cyber.service.bs502101;

import com.uc.bs.cyber.field.Comd_WorkKfField;
/**
 * @author Administrator
 *
 */
public class Bs502101FieldList extends Comd_WorkKfField {
			
	
	/**
	 * 생성자
	 * 공통 납부내역 정정 전문생성
	 * (주의사항)
	 * 현재 전문상 지방세만 납부내역을 정정할 수 있는 것 같다.
	 * 
	 */
	public Bs502101FieldList() {
		// TODO Auto-generated constructor stub
		super();

		/*결제원 송수신 전문 여기서는 송수신이 동일하다.*/
		
		/*
		 * 송신전문(사이버)
		 * */	
 		addSendField("JUMIN_NO"             , 13 ,  "C"  );  /*주민(사업자,법인)등록번호 */
 		addSendField("JINGSU_ACCTNO"        , 6  ,  "C"  );  /*징수관계좌번호 / 예비정보 */
 		
 		/*납부번호 29 자리*/
 		addSendField("EPAY_NO"        		, 19 ,  "C"  );  /*전자납부번호              */
 		addSendField("NAPBU_SEQ"      		, 3  ,  "H"  );  /*납부순번 / 예비정보       */
 		addSendField("RESERV1"        		, 7  ,  "C"  );  /*예비정보 필드             */
 		
 		addSendField("NAPBU_AMT"        	, 15 ,  "H"  );  /*납부금액                  */
 		addSendField("NAPBU_DATE"        	, 8  ,  "H"  );  /*납부(예약)일자            */
 		addSendField("OUTBANK_CODE"        	, 7  ,  "H"  );  /*출금은행 점별코드         */
 		addSendField("OUTACCT_NO"        	, 16 ,  "C"  );  /*출금계좌번호              */
 		addSendField("NAP_TELNO"        	, 14 ,  "C"  );  /*연락전화번호              */
 		addSendField("NAPBU_JUMINNO"        , 13 ,  "C"  );  /*납부자 주민(사업자)번호   */
 		addSendField("NAPBU_NAME"        	, 10 ,  "C"  );  /*납부자 성명               */
 		addSendField("ACCT_OWNER"        	, 10 ,  "C"  );  /*예금주 성명               */
 		addSendField("NAPBU_SYS"        	, 1  ,  "C"  );  /*납부이용 시스템           */
 		addSendField("B_NAPBU_SYS"        	, 1  ,  "C"  );  /*기 납부이용 시스템        */
 		addSendField("NAPBU_GUBUN"        	, 1  ,  "C"  );  /*납부형태 구분             */
 		/*예비정보 필드 9*/
 		addSendField("RESERV2"        		, 1  ,  "C"  );  /*예비정보 필드             */
 		addSendField("RESERV3"        		, 8  ,  "C"  );  /*예비정보 필드             */
        
		/*
		 * 수신전문(결제원)
		 * 송수신전문이 동일하므로 송신전문을 수신전문에 대체시킴
		 * */
	    this.addRecvFieldAll(sendField);
        
	}
}
