/**
 *  주시스템명 : 사이버지방세청 고도화
 *  업  무  명 : 내부 전문 송수신 연계
 *  기  능  명 : 결제원-상하수도고지내역 상세조회 
 *              
 *  클래스  ID : Kf251002FieldList
 *  변경  이력 :
 * ------------------------------------------------------------------------
 *  작성자     소속  		 일자      Tag   내용
 * ------------------------------------------------------------------------   
 * 소도우   유채널(주)  2011.06.08     %01%  신규작성
 *  
 */
package com.uc.bs.cyber.service.kf251002;

import com.uc.bs.cyber.field.Comd_WorkKfField;
/**
 * @author Administrator
 *
 */
public class Kf251002FieldList extends Comd_WorkKfField {
			
	
	/**
	 * 생성자
	 * 상하수도 납부내역 상세조회 전문생성
	 */
	public Kf251002FieldList() {
		// TODO Auto-generated constructor stub
		super();

		/*
		 * 송신전문(결제원)
		 * */
		this.addSendField("SEARCHGUBUN"        ,  1      , "C");      /*조회 구분('S'/'E')                                */
		this.addSendField("SEARCHKEY"          , 30      , "C");      /*수용가번호/전자납부번호                           */
		this.addSendField("BYYYMM"             ,  6      , "H");      /*부과 년월                                         */
		this.addSendField("DANGGUBUN"          ,  1      , "C");      /*당월 구분                                         */
		this.addSendField("NPNO"               , 10      , "C");      /*관리 번호                                         */
		this.addSendField("NAME"               , 20      , "C");      /*성명                                              */
		this.addSendField("BNAPDATE"           ,  8      , "H");      /*납기내 납기일              | 납부 마감일          */
		this.addSendField("BNAPAMT"            , 10      , "H");      /*납기내 금액                | 체납액               */
		this.addSendField("ANAPAMT"            , 10      , "H");      /*납기후 금액                                       */
		this.addSendField("GUM2"               ,  1      , "H");      /*검증번호 2                                        */
		this.addSendField("BSAMT"              , 10      , "H");      /*상수도납기내금액           |  상수도 체납액       */
		this.addSendField("BHAMT"              , 10      , "H");      /*하수도납기내금액           |  하수도 체납액       */
		this.addSendField("BGAMT"              , 10      , "H");      /*지하수납기내금액           |  지하수 체납액       */
		this.addSendField("BMAMT"              , 10      , "H");      /*물이용부담금납기내금액     |  물이용부담금체납액  */
		this.addSendField("ASAMT"              , 10      , "H");      /*상수도납기후금액                                  */
		this.addSendField("AHAMT"              , 10      , "H");      /*하수도납기후금액                                  */
		this.addSendField("AGAMT"              , 10      , "H");      /*지하수납기후금액                                  */
		this.addSendField("AMAMT"              , 10      , "H");      /*물이용부담금납기후금액                            */
		this.addSendField("ANAPDATE"           ,  8      , "H");      /*납기후 납기일                                     */
		this.addSendField("GUM3"               ,  1      , "H");      /*검증번호 3                                        */
		this.addSendField("CNAPTERM"           , 16      , "H");      /*체납 기간                                         */
		this.addSendField("ADDR"               , 60      , "C");      /*주소                                              */
		this.addSendField("USETERM"            , 16      , "H");      /*사용 기간                                         */
		this.addSendField("AUTOREG"            ,  1      , "C");      /*자동이체 등록 여부                                */
		this.addSendField("SNAP_BANK_CODE"     ,  7      , "H");      /*수납은행 점별 코드                                */
		this.addSendField("SNAP_SYMD"          , 14      , "H");      /*납부 일시                                         */
		this.addSendField("NAPGUBUN"           ,  1      , "C");      /*납기 내후 구분                                    */
		this.addSendField("ETC1"               ,  9      , "C");      /*예비 정보 FIELD                                   */
		this.addSendField("CUST_ADMIN_NUM"     , 30      , "C");      /*고객관리번호                                      */
		this.addSendField("OCR"                ,108      , "C");      /*OCR BAND                                          */
		
		/*
		 * 수신전문(결제원) : 송신전문이 138바이트가 빠져서 온다...ㅋㅋㅋ 이놈만 틀리다.
		 * */
		
		this.addRecvField("SEARCHGUBUN"        ,  1      , "C");      /*조회 구분('S'/'E')                                */
		this.addRecvField("SEARCHKEY"          , 30      , "C");      /*수용가번호/전자납부번호                           */
		this.addRecvField("BYYYMM"             ,  6      , "H");      /*부과 년월                                         */
		this.addRecvField("DANGGUBUN"          ,  1      , "C");      /*당월 구분                                         */


	}
	
	
}
