/**
 *  주시스템명 : 사이버지방세청 고도화
 *  업  무  명 : 내부 전문 송수신 연계
 *  기  능  명 : 결제원-환경개선부담금 납부내역 상세조회 
 *              
 *  클래스  ID : Kf203001FieldList
 *  변경  이력 :
 * ------------------------------------------------------------------------
 *  작성자     소속  		 일자      Tag   내용
 * ------------------------------------------------------------------------   
 * 소도우   유채널(주)  2011.06.08     %01%  신규작성
 *  
 */
package com.uc.bs.cyber.service.kf203002;

import com.uc.bs.cyber.field.Comd_WorkKfField;
/**
 * @author Administrator
 *
 */
public class Kf203002FieldList extends Comd_WorkKfField {
			
	/**
	 * 생성자
	 * 환경개선부담금 납부내역 상세조회 전문생성
	 */
	public Kf203002FieldList() {
		// TODO Auto-generated constructor stub
		super();
		
		/*
		 * 송신전문(결제원)
		 * */
		this.addSendField("ETAXNO"            ,29         ,"C");            
		this.addSendField("NAPBU_JUMIN_NO"    ,13         ,"C");
		this.addSendField("JUMINNO"           ,13         ,"C");
		this.addSendField("SIDO"              ,2          ,"H");
		this.addSendField("GU_CODE"           ,3          ,"H");
		this.addSendField("CONFIRM_NO1"       ,1          ,"H");
		this.addSendField("HCALVAL"           ,2          ,"H");
		this.addSendField("GWAMOK"            ,6          ,"H");
		this.addSendField("TAX_YYMM"          ,6          ,"H");
		this.addSendField("KIBUN"             ,1          ,"H");
		this.addSendField("DONG_CODE"         ,3          ,"H");
		this.addSendField("GWASE_NO"          ,6          ,"H");
		this.addSendField("CONFIRM_NO2"       ,1          ,"H");
		this.addSendField("NAPBU_NAME"        ,40         ,"C");
		this.addSendField("NAP_BFAMT"         ,15         ,"H");
		this.addSendField("NAP_AFAMT"         ,15         ,"H");
		this.addSendField("CONFIRM_NO3"       ,1          ,"H");
		this.addSendField("GWASE_POINT"       ,15         ,"H");
		this.addSendField("BONSE"             ,15         ,"H");
		this.addSendField("BONSE_ADD"         ,15         ,"H");
		this.addSendField("DOSISE"            ,15         ,"H");
		this.addSendField("DOSISE_ADD"        ,15         ,"H");
		this.addSendField("GONGDONGSE"        ,15         ,"H");
		this.addSendField("GONGDONGSE_ADD"    ,15         ,"H");
		this.addSendField("EDUSE"             ,15         ,"H");
		this.addSendField("EDUSE_ADD"         ,15         ,"H");
		this.addSendField("NAP_BFDATE"        ,8          ,"H");
		this.addSendField("NAP_AFDATE"        ,8          ,"H");
		this.addSendField("CONFIRM_NO4"       ,1          ,"H");
		this.addSendField("FILLER1"           ,1          ,"H");
		this.addSendField("CONFIRM_NO5"       ,1          ,"H");
		this.addSendField("GWASE_DESC"        ,80         ,"C");
		this.addSendField("GOJI_DATE"         ,8          ,"H");
		this.addSendField("NAPBU_SYS"         ,1          ,"C");
		this.addSendField("JIJUM_CODE"        ,7          ,"H");
		this.addSendField("NAPBU_DATE"        ,14         ,"H");
		this.addSendField("NAPBU_AMT"         ,15         ,"H");
		this.addSendField("OUTACCT_NO"        ,16         ,"C");
		this.addSendField("RESERVED2"         ,10         ,"C");

		/*
		 * 수신전문(결제원)
		 * 송수신전문이 동일하므로 송신전문을 수신전문에 대체시킴
		 * */
	    this.addRecvFieldAll(sendField);
	}
	
	
}
