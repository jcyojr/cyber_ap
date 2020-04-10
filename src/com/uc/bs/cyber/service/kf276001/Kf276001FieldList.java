/**
 *  주시스템명 : 사이버지방세청 고도화
 *  업  무  명 : 지방세 납부결과 통보
 *  기  능  명 : 결제원-세외수입 일괄납부 통보 전문
 *              
 *  클래스  ID : Kf276001FieldList
 *  변경  이력 :
 * ------------------------------------------------------------------------
 *  작성자     소속  		 일자      Tag   내용
 * ------------------------------------------------------------------------   
 * 소도우   유채널(주)  2011.06.08     %01%  신규작성
 *  
 */
package com.uc.bs.cyber.service.kf276001;

import com.uc.bs.cyber.field.Comd_WorkKfField;
/**
 * @author Administrator
 *
 */
public class Kf276001FieldList extends Comd_WorkKfField {
			
	
	/**
	 * 생성자
	 * 세외수입 일괄납부결과 통보 전문생성
	 */
	public Kf276001FieldList() {
		// TODO Auto-generated constructor stub
		super();
		
        /*
         * 송신전문(결제원)
         * */
        this.addSendField("JUMINNO"		,13  ,"C");     /*납부자 주민(사업자)등록번호	*/
        this.addSendField("PAY_DT"		,8   ,"H");     /*납부일자             			*/
        this.addSendField("BANK_CD"		,7   ,"H");     /*출근은행점별코드              */
        this.addSendField("OUTACCT_NO"	,3   ,"C");     /*지정 번호                  	*/
        this.addSendField("TEL_NO"		,14  ,"H");     /*데이터 건수                	*/
        this.addSendField("REG_NO"		,13  ,"C");     /*납부자 주민(사업자)등록번호	*/
        this.addSendField("REG_NM"		,10  ,"C");     /*납부자 명						*/
        this.addSendField("ACCT_HOL"	,10  ,"C");     /*예금주명 						*/
        this.addSendField("NAPBU_SYS"	,1   ,"C");     /*납부 이용 시스템				*/
        this.addSendField("NAPBU_GB"	,1   ,"C");     /*납부 형태 구분				*/
        this.addSendField("FIELD1"		,10  ,"C");     /*예비정보 FIELD 1				*/
        this.addSendField("REQ_CNT"		,2   ,"H");     /*요청건수						*/
        
        /*
         * 수신전문(결제원)
         * */
        this.addRecvField("JUMINNO"		,13  ,"C");     /*납부자 주민(사업자)등록번호	*/
        this.addRecvField("PAY_DT"		,8   ,"H");     /*납부일자             			*/
        this.addRecvField("BANK_CD"		,7   ,"H");     /*출근은행점별코드              */
        this.addRecvField("OUTACCT_NO"	,3   ,"C");     /*지정 번호                  	*/
        this.addRecvField("TEL_NO"		,14  ,"H");     /*데이터 건수                	*/
        this.addRecvField("REG_NO"		,13  ,"C");     /*납부자 주민(사업자)등록번호	*/
        this.addRecvField("REG_NM"		,10  ,"C");     /*납부자 명						*/
        this.addRecvField("ACCT_HOL"	,10  ,"C");     /*예금주명 						*/
        this.addRecvField("NAPBU_SYS"	,1   ,"C");     /*납부 이용 시스템				*/
        this.addRecvField("NAPBU_GB"	,1   ,"C");     /*납부 형태 구분				*/
        this.addRecvField("FIELD1	"	,10  ,"C");     /*예비정보 FIELD 1				*/
        this.addRecvField("REQ_CNT"		,2   ,"H");     /*요청건수						*/

        
        /*
         * 송신전문(반복전문)
         * */
        this.addRepetField("PUBGCODE"   ,2   ,"H");     /*발행기관 분류코드          */ 
        this.addRepetField("JIRONO"     ,7   ,"H");     /*지로 번호                  */ 
        this.addRepetField("EPAYNO"     ,19  ,"C");     /*전자납부번호               */
        this.addRepetField("FIELD2"		 ,13  ,"C");     /*예비정보 FIELD 2			 */
        this.addRepetField("NAPBU_AMT"   ,15  ,"H");     /*납부 금액                  */ 
        this.addRepetField("SEQNO"    	 ,12  ,"C");     /*거래 고유 번호             */ 
        this.addRepetField("TREAT_CODE"	 ,3   ,"C");     /*처리응답 코드         	 */ 
        
	}
}
