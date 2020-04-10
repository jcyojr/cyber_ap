/**
 *  주시스템명 : 사이버지방세청 고도화
 *  업  무  명 : 내부 전문 송수신 연계
 *  기  능  명 : 편의점(더존)-환경개선부담금 상세조회 전문셋팅 
 *              
 *  클래스  ID : Df201002FieldList
 *  변경  이력 :
 * ------------------------------------------------------------------------
 *  작성자     소속  		 일자      Tag   내용
 * ------------------------------------------------------------------------   
 * 임창섭   유채널(주)  2013.08.12     %01%  신규작성
 *  
 */
package com.uc.bs.cyber.service.df201002;

import com.uc.bs.cyber.field.Comd_WorkDfField;

/**
 * @author Administrator
 *
 */
public class Df201002FieldList extends Comd_WorkDfField {
				
	/**
	 * 생성자
	 * 환경개선부담금 상세조회 전문생성
	 */
	public Df201002FieldList() {
		// TODO Auto-generated constructor stub
		super();
		
		/*편의점(더존) 상세조회 송수신 전문.*/
		
		/*
		 * 송신전문(사이버)
		 * */
        addSendField("TAX_GB"             , 1   ,  "C"  );  /*세금구분코드(1:지방세, 2:세외, 3:상하수도)*/
        addSendField("TAX_NO"             , 31  ,  "C"  );  /*납세번호       */
        addSendField("EPAY_NO"            , 19  ,  "C"  );  /*전자납부번호   */   
        addSendField("NAP_NAME"           , 80  ,  "C"  );  /*납세자명       */
        addSendField("SGG_NAME"           , 10  ,  "C"  );  /*부과기관명     */
        addSendField("TAX_NAME"           , 50  ,  "C"  );  /*과목세목명     */
        addSendField("TAX_YM"             , 6   ,  "C"  );  /*과세년월       */
        addSendField("TAX_DIV"            , 1   ,  "C"  );  /*기분         */
        addSendField("NAP_BFDATE"         , 8   ,  "C"  );  /*납기일(납기내)*/
        addSendField("NAP_BFAMT"          , 11  ,  "H"  );  /*납기내 금액    */
        addSendField("NAP_AFDATE"         , 8   ,  "C"  );  /*납기일(납기후)*/
        addSendField("NAP_AFAMT"          , 11  ,  "H"  );  /*납기후 금액    */
        addSendField("NAP_AMT"            , 11  ,  "H"  );  /*실제 납부해야 할 금액   */
        addSendField("TAX_DESC"           , 130 ,  "C"  );  /*부과내역       */
        addSendField("FILLER1"            , 23  ,  "C"  );  /*예비정보 FIELD 2 */

		/*
		 * 수신전문(편의점(더존))
		 * 
		 * */
        addRecvField("TAX_GB"               , 1  ,  "C"); /*세금구분코드(1:지방세, 2:세외, 3:상하수도) */
        addRecvField("TAX_NO"               , 31 ,  "C"); /*납세번호        */
        addRecvField("EPAY_NO"              , 19 ,  "C"); /*전자납부번호    */
        addRecvField("FILLER2"              , 29 ,  "C"); /*SPACE      */
        
	}
	
	
}
