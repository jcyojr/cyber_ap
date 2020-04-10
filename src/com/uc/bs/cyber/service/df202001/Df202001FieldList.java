/**
 *  주시스템명 : 사이버지방세청 고도화
 *  업  무  명 : 내부 전문 송수신 연계
 *  기  능  명 : 편의점(더존)-환경개선부담금 납부결과통지
 *              
 *  클래스  ID : Df202001FieldList
 *  변경  이력 :
 * ------------------------------------------------------------------------
 *  작성자     소속  		 일자      Tag   내용
 * ------------------------------------------------------------------------   
 * 임창섭   유채널(주)  2013.08.13     %01%  신규작성
 *  
 */
package com.uc.bs.cyber.service.df202001;

import com.uc.bs.cyber.field.Comd_WorkDfField;
/**
 * @author Administrator
 *
 */
public class Df202001FieldList extends Comd_WorkDfField {
			

	/**
	 * 생성자
	 * 환경개선부담금 납부결과통지 전문생성
	 */
	public Df202001FieldList() {
		// TODO Auto-generated constructor stub
		
        super();
		
        addSendField("TAX_GB"            , 1  ,  "C"  );  /*세금구분코드(1:지방세, 2:세외, 3:상하수도)*/
        addSendField("TAX_NO"            , 31 ,  "C"  );  /*납세번호       */
        addSendField("EPAY_NO"           , 19 ,  "C"  );  /*전자납부번호   */
        addSendField("NAPBU_AMT"         , 11 ,  "H"  );  /*수납금액  */
        addSendField("NAPBU_DATE"        , 8  ,  "C"  );  /*수납일자  */
        addSendField("NAPBU_SNSU"        , 1  ,  "C"  );  /*수납수단  */
        addSendField("FILLER1"           , 29 ,  "C"  );  /*SPACE     */

		/*
		 * 수신전문(편의점(더존))
		 * 송수신전문이 동일하므로 송신전문을 수신전문에 대체시킴
		 * */
	    this.addRecvFieldAll(sendField);
	    
	}
	

}
