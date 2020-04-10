/**
 *  주시스템명 : 사이버지방세청 고도화
 *  업  무  명 : 내부 전문 송수신 연계
 *  기  능  명 : 결제원-상하수도 납부내역 상세조회
 *  클래스  ID : Kf251001FieldList
 *  변경  이력 :
 * ------------------------------------------------------------------------
 *  작성자     소속  		 일자      Tag   내용
 * ------------------------------------------------------------------------   
 * 소도우   유채널(주)  2011.06.08     %01%  신규작성
 *  
 */
package com.uc.bs.cyber.service.kf253002;

import com.uc.bs.cyber.field.Comd_WorkKfField;
/**
 * @author Administrator
 *
 */
public class Kf253002FieldList extends Comd_WorkKfField {
			
	
	/**
	 * 생성자
	 * 상하수도 납부내역 간략조회 전문생성
	 */
	public Kf253002FieldList() {
		// TODO Auto-generated constructor stub
		super();

		/*
		 * 송신전문(결제원)
		 * */
		this.addSendField("JUMINNO"            , 13      , "C");      /*납부자주민번호                                    */
		this.addSendField("CUSTNO"             , 30      , "C");      /*수용가번호/전자납부번호/과세번호                  */
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
		this.addSendField("SNAP_BANK_CODE"     ,  7      , "H");      /*수납은행 점별 코드                                */
		this.addSendField("SNAP_SYMD"          , 14      , "H");      /*납부 일시                                         */
		this.addSendField("NAPGU_AMT"          , 10      , "H");      /*납부 금액                                         */
		this.addSendField("OUT_ACCTNO"         , 16      , "C");      /*출금계좌번호                                      */
		this.addSendField("ETC1"               , 10      , "C");      /*예비 정보 FIELD                                   */
		
		/*
		 * 수신전문(결제원) : 송수신전문이 동일해서
		 * */
		this.addRecvFieldAll(sendField);

	}
	
	
}
