/**
 *  주시스템명 : 사이버지방세청 고도화
 *  업  무  명 : 내부 전문 송수신 연계
 *  기  능  명 : 부산은행-세외수입 납부내역 간략조회 
 *              
 *  클래스  ID : Bs273001FieldList
 *  변경  이력 :
 * ------------------------------------------------------------------------
 *  작성자     소속  		 일자      Tag   내용
 * ------------------------------------------------------------------------   
 * 황종훈   다산(주)  2011.06.08     %01%  신규작성
 *  
 */
package com.uc.bs.cyber.service.kf273001;

import com.uc.bs.cyber.field.Comd_WorkKfField;
/**
 * @author Administrator
 *
 */
public class Kf273001FieldList extends Comd_WorkKfField {
			
	
	/**
	 * 생성자
	 * 세외수입 납부내역 간략조회 전문생성
	 */
	public Kf273001FieldList() {
		// TODO Auto-generated constructor stub
		super();
		
        /*
         * 송신전문(결제원)
         * */
        this.addSendField("JUMINNO"       ,13  ,"C");     /*납부자 주민(사업자)등록번호*/
        this.addSendField("IN_STDATE"     ,8   ,"H");     /*조회 시작 일자             */
        this.addSendField("IN_ENDATE"     ,8   ,"H");     /*조회 종료 일자             */
        this.addSendField("DATATOT"       ,3   ,"H");     /*납부 총 건수               */
        this.addSendField("POINTNO"       ,3   ,"H");     /*지정 번호                  */
        this.addSendField("DATANUM"       ,2   ,"H");     /*데이터 건수                */

        
        /*
         * 수신전문(결제원)
         * */
        this.addRecvField("JUMINNO"       ,13  ,"C");     /*납부자 주민(사업자)등록번호*/ 
        this.addRecvField("IN_STDATE"     ,8   ,"H");     /*조회 시작 일자             */ 
        this.addRecvField("IN_ENDATE"     ,8   ,"H");     /*조회 종료 일자             */ 
        this.addRecvField("DATATOT"       ,3   ,"H");     /*납부 총 건수               */ 
        this.addRecvField("POINTNO"       ,3   ,"H");     /*지정 번호                  */ 
        this.addRecvField("DATANUM"       ,2   ,"H");     /*데이터 건수                */ 

        
        /*
         * 수신전문(반복전문)
         * */
        this.addRepetField("PUBGCODE"     ,2   ,"H");     /*발행기관 분류코드          */ 
        this.addRepetField("JIRONO"       ,7   ,"H");     /*지로 번호                  */ 
        this.addRepetField("ETAXNO"       ,32  ,"C");     /*납부번호                   */
        this.addRepetField("EPAYNO"       ,19  ,"C");     /*전자납부번호               */
        this.addRepetField("SEQNO"        ,5   ,"H");     /*분납순번                   */
        this.addRepetField("GWAMOK"       ,6   ,"H");     /*과목/세목                  */ 
        this.addRepetField("GWADATE"      ,6   ,"H");     /*년도/기분                  */ 
        this.addRepetField("KIBUN"        ,1   ,"H");     /*장표 고지 형태             */ 
        this.addRepetField("NAPAMT"       ,15  ,"H");     /*납부 금액                  */ 
        this.addRepetField("NAPBU_DATE"   ,8   ,"H");     /*납부 일자                  */ 
        this.addRepetField("NAPBU_SYS"    ,1   ,"C");     /*납부 이용 시스템           */ 
        this.addRepetField("JIJUM_CODE"   ,7   ,"H");     /*수납은행 점별 코드         */ 

        /* 전문 예
	        0194IGN0320210273001000G0001106210759210320I0000082026000000537261000685006109151090421201106212011062100100101
	        26
	        1000685
	        38053010600120110615500026895
	        00000
	        106001
	        201106
	        1
	        000000000207790
	        20110621
	        B
	        0329990
        */
        
	}
	
	
}
