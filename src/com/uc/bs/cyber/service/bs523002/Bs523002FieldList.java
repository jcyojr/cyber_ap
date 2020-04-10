/**
 *  주시스템명 : 사이버지방세청 고도화
 *  업  무  명 : 내부 전문 송수신 연계
 *  기  능  명 : 부산은행-지방세 납부내역 간략조회 
 *              
 *  클래스  ID : Bs523002FieldList
 *  변경  이력 :
 * ------------------------------------------------------------------------
 *  작성자     소속  		 일자      Tag   내용
 * ------------------------------------------------------------------------   
 * 황종훈   다산(주)  2011.06.08     %01%  신규작성
 *  
 */
package com.uc.bs.cyber.service.bs523002;

import com.uc.bs.cyber.field.Comd_WorkKfField;
/**
 * @author Administrator
 *
 */
public class Bs523002FieldList extends Comd_WorkKfField {
			
	
	/**
	 * 생성자
	 * 지방세 납부내역 간략조회 전문생성
	 */
	public Bs523002FieldList() {
		// TODO Auto-generated constructor stub
		super();

		/*결제원 송수신 전문 여기서는 송수신이 동일하다.*/
		
		/*
		 * 송신전문(사이버)
		 * */	
	    addSendField("ETAXNO"             	, 29 ,  "C"  );  /*납부번호 */
	    addSendField("SEQNO"             	, 5  ,  "H"  );  /*분납순번					 */
	    addSendField("NAPBU_JUMIN_NO"       , 13 ,  "C"  );  /*주민(사업자,법인)등록번호 */
 		addSendField("JUMIN_NO"             , 13 ,  "C"  );  /*주민(사업자,법인)등록번호 */
 		addSendField("SIDO"                 , 2  ,  "H"  );  /*시도                      */
 		addSendField("GU_CODE"              , 3  ,  "H"  );  /*과세기관(시군구)          */
 		addSendField("CHK1"          		, 1  ,  "H"  );  /*검증번호 1                */
 		addSendField("HCALVAL"              , 2  ,  "H"  );  /*회계                      */
 		addSendField("GWA_MOK"              , 6  ,  "H"  );  /*과목/세목                 */
 		addSendField("TAX_YYMM"             , 6  ,  "H"  );  /*년도/기분                 */
 		addSendField("KIBUN"                , 1  ,  "H"  );  /*구분                      */
 		addSendField("DONG_CODE"            , 3  ,  "H"  );  /*행정동(읍면동)            */
 		addSendField("GWASE_NO"             , 6  ,  "H"  );  /*관리 번호                 */
 		addSendField("CHK2"          		, 1  ,  "H"  );  /*검증번호 2                */
 		addSendField("NAP_NAME"             , 40 ,  "C"  );  /*납부자 성명               */
 		addSendField("NAP_BFAMT"            , 15 ,  "H"  );  /*납기내 금액               */
 		addSendField("NAP_AFAMT"            , 15 ,  "H"  );  /*납기후 금액               */
 	    addSendField("CHK3"		            , 1  ,  "H"  );  /*검증번호 3                */
 	    addSendField("KWA_AMT"              , 15 ,  "H"  );  /*과세 표준                 */
	    addSendField("MNTX"                 , 15 ,  "H"  );  /*본세		                 */
	    addSendField("MNTX_ADTX"            , 15 ,  "H"  );  /*본세가산금	             */
	    addSendField("CPTX"                 , 15 ,  "H"  );  /*도시계획세                */
	    addSendField("CPTX_ADTX"            , 15 ,  "H"  );  /*도시계획세 가산금	     */
	    addSendField("CFTX"                 , 15 ,  "H"  );  /*공동시설세/농특세         */
	    addSendField("CFTX_ADTX"            , 15 ,  "H"  );  /*공동시설세/농특세 가산금	 */
	    addSendField("LETX"                 , 15 ,  "H"  );  /*교육세                    */
	    addSendField("LETX_ADTX"            , 15 ,  "H"  );  /*교육세가산금	             */	  
 	    addSendField("DUE_DT"           	, 8  ,  "H"  );  /*납기일 (납기내)           */
 	    addSendField("DUE_F_DT"           	, 8  ,  "H"  );  /*납기일 (납기후)           */
 	    addSendField("CHK4"          		, 1  ,  "H"  );  /*검증번호 4                */
 	    addSendField("FILLER"               , 1  ,  "H"  );  /*필러                      */
 	    addSendField("CHK5"          		, 1  ,  "H"  );  /*검증번호 5                */
 	    addSendField("MLGN"           		, 80 ,  "C"  );  /*과세 사항                 */
 	    addSendField("GOJICR_DATE"          , 8  ,  "H"  );  /*고지자료 발생일자         */
 	    addSendField("NAPBU_SYS"            , 1  ,  "C"  );  /*납부이용 시스템           */
 	    addSendField("BANK_CD"              , 7  ,  "H"  );  /*수납은행 점별 코드        */
 	    addSendField("RECIP_DATE"           , 14 ,  "H"  );  /*납부 일시                 */
 	    addSendField("RECIP_AMT"            , 15 ,  "H"  );  /*납부 금액                 */
 	    addSendField("OUTACCT_NO "          , 16 ,  "C"  );  /*출금계좌번호              */
 	    addSendField("FIELD2"               , 10 ,  "C"  );  /*예비 정보 FIELD 5         */	

 	    /*
 	   44063010600120110615400045002
 	   00000
 	   7508222122429
 	   7508222122429
 	   26
 	   440
 	   6
 	   30
 	   106001
 	   201106
 	   1
 	   540
 	   004500
 	   2
 	   김구연                                  
 	   000000000194800
 	   000000000200630
 	   3
 	   000000000299700
 	   000000000149850
 	   000000000000000
 	   000000000000000
 	   000000000000000
 	   000000000000000
 	   000000000000000
 	   000000000044950
 	   000000000000000
 	   20110630
 	   20110801
 	   8
 	   0
 	   0
 	   299700-(2011.01.01~2011.06.30)                                                  
 	   20110607
 	   B
 	   0329990
 	   20110622073302
 	   000000000194800 
 	   '                          ' ---> 빈칸 어쩔꺼임
 	   */
 	                                                      
		/*
		 * 수신전문(결제원)
		 * 송수신전문이 동일하므로 송신전문을 수신전문에 대체시킴
		 * */
	    this.addRecvFieldAll(sendField);
        
	}
	
	
}
