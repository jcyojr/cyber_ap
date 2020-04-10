/**
 *  주시스템명 : 사이버지방세청 고도화
 *  업  무  명 : 내부 전문 송수신 연계
 *  기  능  명 : 부산은행- 지방세  고지내역 상세조회 - 목록 전문셋팅
 *              
 *  클래스  ID : Bs521002FieldList
 *  변경  이력 :
 * ------------------------------------------------------------------------
 *  작성자     소속  		 일자      Tag   내용
 * ------------------------------------------------------------------------   
 * 황종훈   다산(주)  2011.06.08     %01%  신규작성
 *  
 */
package com.uc.bs.cyber.service.bs521002;

import com.uc.bs.cyber.field.Comd_WorkKfField;

/**
 * @author Administrator
 *
 */
public class Bs521002FieldList extends Comd_WorkKfField {
			
	
	/**
	 * 생성자
	 * 지방세 간략조회목록 전문생성
	 */
	public Bs521002FieldList() {
		// TODO Auto-generated constructor stub
		super();
				
		/*
		 * 참고사항) 결제원의 경우 송수신전문이 동일하다.
		 *           반복전문이 있는 경우 반복건수의 Field 는 'DATA_NUM'으로 동일시킨다.
		 * */
		
		/*
		 * 송신전문(사이버)
		 * */
	    	
		addSendField("TAXNO"              	, 29 ,  "C"  );  /*납부번호                  */
		addSendField("SEQNO"             	, 5  ,  "H"  );  /*분납순번					 */	
		addSendField("JUMIN_NO"             , 13 ,  "C"  );  /*주민(사업자,법인)등록번호 */
		addSendField("SIDO"               	, 2  ,  "H"  );  /*시도				         */
		addSendField("BUGWA_GIGWAN"         , 3  ,  "H"  );  /*부과기관 		         */
		addSendField("CHK1"		            , 1  ,  "H"  );  /*검증번호1 		         */
		addSendField("ACC_CD"	            , 2  ,  "H"  );  /*회계 			         */
		addSendField("SEMOK_CD"          	, 6  ,  "H"  );  /*과목/세목                 */
		addSendField("ACC_YM"              	, 6  ,  "H"  );  /*과세년월                  */
		addSendField("GIBN"              	, 1  ,  "H"  );  /*기분	                     */
		addSendField("DONG_CD"             	, 3  ,  "H"  );  /*행정동                    */
		addSendField("TAX_SNO"             	, 6  ,  "H"  );  /*과세번호                  */
		addSendField("CHK2"	                , 1  ,  "H"  );  /*검증번호2                 */
		addSendField("REG_NM"	            , 40 ,  "C"  );  /*납부자성명                */
		addSendField("NAP_BFAMT"            , 15 ,  "H"  );  /*납기내 금액               */
		addSendField("NAP_AFAMT"            , 15 ,  "H"  );  /*납기후 금액               */
		addSendField("CHK3"	                , 1  ,  "H"  );  /*검증번호3                 */
	    addSendField("KWA_AMT"         		, 15 ,  "H"  );  /*과세표준                  */
	    addSendField("MNTX"                 , 15 ,  "H"  );  /*본세		                 */
	    addSendField("MNTX_ADTX"            , 15 ,  "H"  );  /*본세가산금	             */
	    addSendField("CPTX"                 , 15 ,  "H"  );  /*도시계획세                */
	    addSendField("CPTX_ADTX"            , 15 ,  "H"  );  /*도시계획세 가산금	     */
	    addSendField("CFTX"                 , 15 ,  "H"  );  /*공동시설세/농특세         */
	    addSendField("CFTX_ADTX"            , 15 ,  "H"  );  /*공동시설세/농특세 가산금	 */
	    addSendField("LETX"                 , 15 ,  "H"  );  /*교육세                    */
	    addSendField("LETX_ADTX"            , 15 ,  "H"  );  /*교육세가산금	             */	  
	    addSendField("DUE_DT"               , 8  ,  "H"  );  /*납기일(납기내)            */
	    addSendField("DUE_F_DT"             , 8  ,  "H"  );  /*납기일(납기후)            */
	    addSendField("CHK4"	                , 1  ,  "H"  );  /*검증번호4                 */
	    addSendField("FILLER"               , 1  ,  "H"  );  /*필러                      */
	    addSendField("CHK5"                 , 1  ,  "H"  );  /*검증번호5                 */
	    addSendField("MLGN"                 , 80 ,  "C"  );  /*과세사항                  */
	    addSendField("GIGI_DATE"            , 8  ,  "H"  );  /*고지일자 	             */
	    addSendField("AUTO_TRNF_YN"         , 1  ,  "C"  );  /*자동이체등록여부          */
	    addSendField("BANK_CD"         		, 7  ,  "H"  );  /*수납은행점별코드          */
	    addSendField("RECIP_DATE"           , 14 ,  "H"  );  /*납부일시		             */
	    addSendField("NABGI_BA_GBN"         , 1  ,  "C"  );  /*납기 내후 구분            */
	    addSendField("FIELD2"         		, 5  ,  "H"  );  /*예비정보 FIELD 2          */
	    
	    /*
	    35043010600120110616601081077
	    00000
	    8111271111320
	    26
	    350
	    4
	    30106001
	    201106
	    1
	    660
	    1081077
	    조성용                                  
	    000000000074810
	    000000000077040
	    8
	    000000000115115
	    000000000057550
	    000000000000000
	    000000000000000
	    000000000000000
	    000000000000000
	    000000000000000
	    000000000017260
	    000000000000000
	    20110630
	    20110801
	    900부산31너9560(비영업용,배기량:1495)[차령경감율:45%]
	                                20110608
	    N
	    0000000
	    00000000000000
	    B
	    00000
	    */

		/*
		 * 수신전문(결제원)
		 * 송수신전문이 동일하므로 송신전문을 수신전문에 대체시킴
		 * */
	    this.addRecvFieldAll(sendField);		

	}
}

