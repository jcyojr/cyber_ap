/**
 *  주시스템명 : 사이버지방세청 고도화
 *  업  무  명 : 내부 전문 송수신 연계
 *  기  능  명 : 바로결제 상세조회 전문셋팅 
 *              
 *  클래스  ID : Df030002FieldList
 *  변경  이력 :
 * ------------------------------------------------------------------------
 *  작성자     소속  		 일자      Tag   내용
 * ------------------------------------------------------------------------   
 * 최유환    유채널(주)   2013.09.04     %01%  신규작성
 *  
 */
package com.uc.bs.cyber.service.df533001;

import com.uc.bs.cyber.field.Comd_WorkDfField;

/**
 * @author Administrator
 *
 */
public class Df533001Tax005FieldList extends Comd_WorkDfField {
				
	/**
	 * 생성자
	 * 상세조회 전문생성
	 */
	public Df533001Tax005FieldList() {
		
		super();
		
		/*바로결제 상세조회 송수신 전문.*/
		
		/*
		 * 수신전문(바로결제)
		 * */

		addSendField("SIDO_COD"	            , 2   ,  "C"  );  /*부산시코드 26   */
		addSendField("SGG_COD"	 		    , 3   ,  "C"  );  /*기관코드        */
		addSendField("HACD"	 		        , 3   ,  "C"  );  /*행정동          */
		addSendField("ETC01"	 		    , 1   ,  "C"  );  /*행정동          */
		addSendField("ACCT_CD"	            , 2   ,  "C"  );  /*회계코드        */
		addSendField("TAX_ITEM"	            , 6   ,  "C"  );  /*세목코드        */
		addSendField("TAX_YY"               , 4   ,  "C"  );  /*과세년          */
		addSendField("TAX_MM"               , 2   ,  "C"  );  /*과세월          */
		addSendField("TAX_DIV"              , 1   ,  "C"  );  /*수기고지구분    */
		addSendField("TAX_DD"               , 2   ,  "C"  );  /*신고일          */
		addSendField("TAX_TIME"             , 6   ,  "C"  );  /*24HMISS         */
		
		addSendField("DUE_DT"               , 8   ,  "C"  );  /*납부기한            */
		addSendField("SUM_RCP"              , 13  ,  "H"  );  /*납부총액   A+B      */
		addSendField("REG_NO"               , 13  ,  "C"  );  /*주민/법인번호       */
		addSendField("DPNM"                 , 40  ,  "C"  );  /*성명                */
		
		addSendField("DP_GBN"               , 1   ,  "C"  );  /*개인구분 0:개인,1:법인 */	
		addSendField("TEL_NO"               , 12  ,  "C"  );  /*전화번호               */
		addSendField("PHONE_NO"             , 12  ,  "C"  );  /*휴대폰                 */
		addSendField("ZIP_CD"               , 6   ,  "C"  );  /*우편번호(납세자주소)   */
		addSendField("ADDR"                 , 60  ,  "C"  );  /*주소                    */	
		addSendField("ADDR_DT"              , 10  ,  "C"  );  /*주소_상세               */	
		addSendField("SAUP_NO"              , 10  ,  "C"  );  /*사업자번호             */
		addSendField("RVSN_MM"              , 2   ,  "C"  );  /*귀속년도               */
		addSendField("RVSN_YY"              , 4   ,  "C"  );  /*귀속 월                */		
		addSendField("KJSA"                 , 13  ,  "H"  );  /*양도소득세             */
		addSendField("JMSA"                 , 13  ,  "H"  );  /*주민세 납부세액  A     */
		addSendField("F_DUE_DT"             , 8   ,  "H"  );  /*당초 납부기한          */
		addSendField("DLQ_CNT"              , 3   ,  "H"  );  /*납부지연일수           */  		
		addSendField("GAST"                 , 11  ,  "H"  );  /*납부 불성실 가산세액 B */
		addSendField("RTN_ZIP_CD"           , 6   ,  "C"  );  /*양도물건주소-우편      */
		addSendField("RTN_ADDR"             , 70  ,  "C"  );  /*양도물건주소-상세      */
		addSendField("BU_CD"                , 2   ,  "C"  );  /*법정동리 2자리 추가     */
		addSendField("FILLTER"              , 1   ,  "C"  );  /*빈칸           */
		
		/*추가*/
		addSendField("NAPBU_TAX"            , 11  ,  "H"  );  /*실 납부금액 - SUM_RCP 비교하여 처리  */
		addSendField("SUNAP_DT"             , 8   ,  "C"  );  /*수납일자                      */
		addSendField("NAPBU_SUDAN"          , 1   ,  "C"  );  /*납부수단 1:카드, 2:계좌이체   */
		
		addSendField("TAX_NO"               , 31  ,  "C"  );  /*납세번호   */
		addSendField("EPAY_NO"              , 19  ,  "C"  );  /*전자납부번호   */
		addSendField("NAPBU_GB"             , 1   ,  "C"  );  /*납부구분  1:신고  2:납부   */
		addSendField("NAPBU_JM_NO"          , 12  ,  "C"  );  /*신고거래 전문일련번호   */
		
		addSendField("FILTER2"              , 27  ,  "C"  );  /*빈칸                          */
		
		/*
		 * 수신전문(편의점(더존))
		 * 송수신전문이 동일하므로 송신전문을 수신전문에 대체시킴
		 * */
	    this.addRecvFieldAll(sendField);
		
	}
	
	
}
