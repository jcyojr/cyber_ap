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
public class Df533001Tax008FieldList extends Comd_WorkDfField {
				
	/**
	 * 생성자
	 * 상세조회 전문생성
	 */
	public Df533001Tax008FieldList() {
		
		super();
		
		/*바로결제 상세조회 송수신 전문. - 특별징수 */
		
		/*
		 * 수신전문(바로결제)
		 * */


		addSendField("DATA_DIV"	            , 2   ,  "C"  );  /*자료구분  21                                                            */
		addSendField("DOC_COD"	 		    , 7   ,  "C"  );  /*서식코드  A103900                                                       */
		addSendField("SIDO_COD"	 		    , 2   ,  "C"  );  /*사업장소재지 시도코드 26                                                 */
		addSendField("SGG_COD"	 		    , 3   ,  "C"  );  /*사업장소재지 시군구코드                                                          */
		addSendField("LDONG_COD"	        , 3   ,  "C"  );  /*사업장소재지 법정동코드                                                         */
		addSendField("LDONG_COD_ETC"	    , 2   ,  "C"  );  /*사업장소재지 법정동코드_면,리 코드                                                         */
		addSendField("TAX_ITEM"	            , 6   ,  "C"  );  /*세목코드 140004                                                         */
		addSendField("TAX_YY"               , 4   ,  "C"  );  /*과세년                                                                           */
		addSendField("TAX_MM"               , 2   ,  "C"  );  /*과세월                                                                           */
		addSendField("TAX_DIV"              , 1   ,  "C"  );  /*과세구분 3                                                              */
		addSendField("REQ_DIV"              , 1   ,  "C"  );  /*납부구분 1, 2                                                           */
		addSendField("TAX_DT"               , 8   ,  "C"  );  /*신고일자                                                                         */
		
		addSendField("TPR_COD"              , 2   ,  "C"  );  /*개인/법인 구분  01,02,..90                                               */
		addSendField("REG_NO"               , 13  ,  "C"  );  /*주민(법인)번호                                                                   */
		addSendField("REG_NM"               , 80  ,  "C"  );  /*성명/법인명                                                                       */
		addSendField("BIZ_NO"               , 10  ,  "C"  );  /*사업자등록번호                                                                    */
		addSendField("CMP_NM"               , 80  ,  "C"  );  /*상호                                                                              */
		addSendField("BIZ_ZIP_NO"           , 6   ,  "C"  );  /*사업장소재지 우편번호                                                             */
		addSendField("BIZ_ADDR"             , 200 ,  "C"  );  /*사업장 주소                                                                       */
		addSendField("BIZ_TEL"              , 30  ,  "C"  );  /*전화번호                                                                          */
		addSendField("MO_TEL"               , 30  ,  "C"  );  /*핸드폰 번호                                                                       */
		
		addSendField("SUP_YYMM"             , 6   ,  "C"  );  /*지급년월 반기의경우 전반기6후반기12                                         */
		addSendField("RVSN_YYMM"            , 6   ,  "C"  );  /*귀속년월                                                                           */
		addSendField("F_DUE_DT"             , 8   ,  "C"  );  /*당초납기일자 납부기한 휴일체크한익일                                               */
		addSendField("DUE_DT"               , 8   ,  "C"  );  /*납기일자                                                                           */
		addSendField("TAX_RT"               , 5   ,  "C"  );  /*세율  10%                                                                */
		addSendField("TOT_STD_AMT"          , 15  ,  "H"  );  /*과세표준합계 양의정수                                                              */	
		addSendField("PAY_RSTX"             , 15  ,  "H"  );  /*지방소득세 합계 양의정수                                                           */			
		addSendField("ADTX_YN"              , 1   ,  "C"  );  /*가산세 유무   1,2                                                         */
		addSendField("ADTX_AM"              , 15  ,  "H"  );  /*가산세1정액   가산세 5% 또는 10%                                            */
		addSendField("DLQ_ADTX"             , 15  ,  "H"  );  /*가산세2지연기간  지연일수에 따른 가산세                                            */
		addSendField("DLQ_CNT"              , 4   ,  "H"  );  /*납부지연 일수                                                                      */
		addSendField("PAY_ADTX"             , 15  ,  "H"  );  /*가산세  가산세합계=가산세1정액+가산세2지연기간                                     */
		
		addSendField("MEMO"                 , 100  ,  "C"  );  /*비고  SPACE                                                              */
		addSendField("ADD_MM_RTN"           , 15   ,  "H"  );  /*당월 기타 환급금                                                                  */
		addSendField("ADD_MM_AAMT"          , 15   ,  "H"  );  /*당월 추가 납부액                                                                  */
		addSendField("ADD_YY_TRTN"          , 15   ,  "H"  );  /*연말정산 환급액                                                                   */
		addSendField("ADD_YY_TAMT"          , 15   ,  "H"  );  /*연말정산 추가납부액                                                               */
		addSendField("ADD_ETC_RTN"          , 15   ,  "H"  );  /*중도 퇴사자 환급액                                                                */
		addSendField("ADD_RDT_ADTX"         , 15   ,  "H"  );  /*가산세대상 추가 납부액                                                            */
		addSendField("ADD_RDT_AADD"         , 15   ,  "H"  );  /*가산세대상 추가 가산세                                                            */
		addSendField("ADD_SUM_RTN"          , 15   ,  "H"  );  /*환급합계금액                                                                      */
		addSendField("ADD_SUM_AAMT"         , 15   ,  "H"  );  /*추가납부 합계금액                                                                 */
		
		addSendField("ADD_OUT_AMT"          , 15   ,  "H"  );  /*가감조정금액 ADD_SUM_AAAMT - ADD_SUM_RTN                                 */
		addSendField("ADD_TOT_AMT"          , 15   ,  "H"  );  /*납부총금액 지방소득세합계 = PAY_ADTX(가산세합계) + ADD_OUT_AMT(가감조정금액)  */
		addSendField("INTX"                 , 15   ,  "H"  );  /*납부할지방소득세 ADD_TOT_AMT - (PAY_ADTX+ADD_RDT_AADD)                    */
		addSendField("TOT_ADTX"             , 15   ,  "H"  );  /*납부할 가산세  =  ADD_TOT_AMT - INTX                                      */
		addSendField("ADD_OUT_SAMT"         , 15   ,  "H"  );  /*차감후환급잔액  가감조정금액(이월환급잔액)                                   */
		addSendField("MINU_YN"              , 1    ,  "C"  );  /*가감조정금액 음수값 여부  Y:음수, N:양수                                         */
		
		addSendField("RPT_REG_NO"           , 13   ,  "C"  );  /*세무대리인 주민등록번호                                                           */
		addSendField("RPT_NM"               , 80   ,  "C"  );  /*세무대리인 성명                                                                   */
		addSendField("RPT_BIZ_NO"           , 10   ,  "C"  );  /*세무대리인 사업자등록번호                                                         */
		addSendField("RPT_TEL"              , 30   ,  "C"  );  /*세무대리인 전화번호                                                               */
		addSendField("TAX_PRO_CD"           , 4    ,  "C"  );  /*세무프로그램 코드                                                                 */
		addSendField("A_SPACE"              , 27   ,  "C"  );  /*공란 SPACE                                                              */
		/* 납부서 및 영수필통지서 1080byte  */
		
		addSendField("DATA_DIV11"           , 2    ,  "C"  );  /*자료구분  22                                                             */
		addSendField("DOC_COD11"            , 7    ,  "C"  );  /*서식코드  A103900                                                        */
		addSendField("TXTP_CD11"            , 2    ,  "C"  );  /*소득구분   11:이자소득                                                            */
		addSendField("TXTP_EMP11"           , 8    ,  "H"  );  /*인원                                                                              */
		addSendField("TXTP_STD11"           , 15   ,  "H"  );  /*과세표준액                                                                        */
		addSendField("TXTP_INTX11"          , 15   ,  "H"  );  /*지방소득세                                                                        */
		
		addSendField("DATA_DIV12"           , 2    ,  "C"  );  /*자료구분  22                                                             */
		addSendField("DOC_COD12"            , 7    ,  "C"  );  /*서식코드  A103900                                                        */
		addSendField("TXTP_CD12"            , 2    ,  "C"  );  /*소득구분   12:배당소득                                                            */
		addSendField("TXTP_EMP12"           , 8    ,  "H"  );  /*인원                                                                              */
		addSendField("TXTP_STD12"           , 15   ,  "H"  );  /*과세표준액                                                                        */
		addSendField("TXTP_INTX12"          , 15   ,  "H"  );  /*지방소득세                                                                        */
		
		addSendField("DATA_DIV13"           , 2    ,  "C"  );  /*자료구분  22                                                             */
		addSendField("DOC_COD13"            , 7    ,  "C"  );  /*서식코드  A103900                                                        */
		addSendField("TXTP_CD13"            , 2    ,  "C"  );  /*소득구분   13:사업소득                                                            */
		addSendField("TXTP_EMP13"           , 8    ,  "H"  );  /*인원                                                                              */
		addSendField("TXTP_STD13"           , 15   ,  "H"  );  /*과세표준액                                                                        */
		addSendField("TXTP_INTX13"          , 15   ,  "H"  );  /*지방소득세                                                                        */
		
		addSendField("DATA_DIV14"           , 2    ,  "C"  );  /*자료구분  22                                                             */
		addSendField("DOC_COD14"            , 7    ,  "C"  );  /*서식코드  A103900                                                        */
		addSendField("TXTP_CD14"            , 2    ,  "C"  );  /*소득구분   14:근로소득                                                            */
		addSendField("TXTP_EMP14"           , 8    ,  "H"  );  /*인원                                                                              */
		addSendField("TXTP_STD14"           , 15   ,  "H"  );  /*과세표준액                                                                        */
		addSendField("TXTP_INTX14"          , 15   ,  "H"  );  /*지방소득세                                                                        */
		
		addSendField("DATA_DIV16"           , 2    ,  "C"  );  /*자료구분  22                                                             */
		addSendField("DOC_COD16"            , 7    ,  "C"  );  /*서식코드  A103900                                                        */
		addSendField("TXTP_CD16"            , 2    ,  "C"  );  /*소득구분   16:기타소득                                                            */
		addSendField("TXTP_EMP16"           , 8    ,  "H"  );  /*인원                                                                              */
		addSendField("TXTP_STD16"           , 15   ,  "H"  );  /*과세표준액                                                                        */
		addSendField("TXTP_INTX16"          , 15   ,  "H"  );  /*지방소득세                                                                        */
		
		addSendField("DATA_DIV17"           , 2    ,  "C"  );  /*자료구분  22                                                             */
		addSendField("DOC_COD17"            , 7    ,  "C"  );  /*서식코드  A103900                                                        */
		addSendField("TXTP_CD17"            , 2    ,  "C"  );  /*소득구분   17:연금소득                                                            */
		addSendField("TXTP_EMP17"           , 8    ,  "H"  );  /*인원                                                                              */
		addSendField("TXTP_STD17"           , 15   ,  "H"  );  /*과세표준액                                                                        */
		addSendField("TXTP_INTX17"          , 15   ,  "H"  );  /*지방소득세                                                                        */
		
		addSendField("DATA_DIV21"           , 2    ,  "C"  );  /*자료구분  22                                                             */
		addSendField("DOC_COD21"            , 7    ,  "C"  );  /*서식코드  A103900                                                        */
		addSendField("TXTP_CD21"            , 2    ,  "C"  );  /*소득구분   21:퇴직소득                                                            */
		addSendField("TXTP_EMP21"           , 8    ,  "H"  );  /*인원                                                                              */
		addSendField("TXTP_STD21"           , 15   ,  "H"  );  /*과세표준액                                                                        */
		addSendField("TXTP_INTX21"          , 15   ,  "H"  );  /*지방소득세                                                                        */
		
		addSendField("DATA_DIV22"           , 2    ,  "C"  );  /*자료구분  22                                                             */
		addSendField("DOC_COD22"            , 7    ,  "C"  );  /*서식코드  A103900                                                        */
		addSendField("TXTP_CD22"            , 2    ,  "C"  );  /*소득구분   22:양도소득 소득세법 제119조                                          */
		addSendField("TXTP_EMP22"           , 8    ,  "H"  );  /*인원                                                                              */
		addSendField("TXTP_STD22"           , 15   ,  "H"  );  /*과세표준액                                                                        */
		addSendField("TXTP_INTX22"          , 15   ,  "H"  );  /*지방소득세                                                                        */
		
		addSendField("DATA_DIV31"           , 2    ,  "C"  );  /*자료구분  22                                                             */
		addSendField("DOC_COD31"            , 7    ,  "C"  );  /*서식코드  A103900                                                        */
		addSendField("TXTP_CD31"            , 2    ,  "C"  );  /*소득구분   31:외국법인 법인세법 제98조                                           */
		addSendField("TXTP_EMP31"           , 8    ,  "H"  );  /*인원                                                                              */
		addSendField("TXTP_STD31"           , 15   ,  "H"  );  /*과세표준액                                                                        */
		addSendField("TXTP_INTX31"          , 15   ,  "H"  );  /*지방소득세                                                                        */
		
		addSendField("DATA_DIV32"           , 2    ,  "C"  );  /*자료구분  22                                                             */
		addSendField("DOC_COD32"            , 7    ,  "C"  );  /*서식코드  A103900                                                        */
		addSendField("TXTP_CD32"            , 2    ,  "C"  );  /*소득구분   32:저축해지                                                            */
		addSendField("TXTP_EMP32"           , 8    ,  "H"  );  /*인원                                                                              */
		addSendField("TXTP_STD32"           , 15   ,  "H"  );  /*과세표준액                                                                        */
		addSendField("TXTP_INTX32"          , 15   ,  "H"  );  /*지방소득세                                                                        */
		
		addSendField("DATA_DIV33"           , 2    ,  "C"  );  /*자료구분  22                                                             */
		addSendField("DOC_COD33"            , 7    ,  "C"  );  /*서식코드  A103900                                                        */
		addSendField("TXTP_CD33"            , 2    ,  "C"  );  /*소득구분   33:내국법인(법인세법-제37조)                                    */
		addSendField("TXTP_EMP33"           , 8    ,  "H"  );  /*인원                                                                              */
		addSendField("TXTP_STD33"           , 15   ,  "H"  );  /*과세표준액                                                                        */
		addSendField("TXTP_INTX33"          , 15   ,  "H"  );  /*지방소득세                                                                        */
		
		addSendField("DATA_DIV34"           , 2    ,  "C"  );  /*자료구분  22                                                             */
		addSendField("DOC_COD34"            , 7    ,  "C"  );  /*서식코드  A103900                                                        */
		addSendField("TXTP_CD34"            , 2    ,  "C"  );  /*소득구분   34:가감세액(조정액)                                             */
		addSendField("TXTP_EMP34"           , 8    ,  "H"  );  /*인원                                                                              */
		addSendField("TXTP_STD34"           , 15   ,  "H"  );  /*과세표준액                                                                        */
		addSendField("TXTP_INTX34"          , 15   ,  "H"  );  /*지방소득세                                                                        */
		
		addSendField("DATA_DIV91"           , 2    ,  "C"  );  /*자료구분  22                                                             */
		addSendField("DOC_COD91"            , 7    ,  "C"  );  /*서식코드  A103900                                                        */
		addSendField("TXTP_CD91"            , 2    ,  "C"  );  /*소득구분   91:외국인으로부터 받은 소득(구서식)                               */
		addSendField("TXTP_EMP91"           , 8    ,  "H"  );  /*인원                                                                              */
		addSendField("TXTP_STD91"           , 15   ,  "H"  );  /*과세표준액                                                                        */
		addSendField("TXTP_INTX91"          , 15   ,  "H"  );  /*지방소득세                                                                        */
		
		
		/*추가*/
		addSendField("NAPBU_TAX"            , 11  ,  "H"  );  /*실 납부금액 - SUM_RCP 비교하여 처리                                               */
		addSendField("SUNAP_DT"             , 8   ,  "C"  );  /*수납일자                                                                           */
		addSendField("NAPBU_SUDAN"          , 1   ,  "C"  );  /*납부수단 1:카드, 2:계좌이체                                                        */
		
		addSendField("TAX_NO"               , 31  ,  "C"  );  /*납세번호                                                                           */
		addSendField("EPAY_NO"              , 19  ,  "C"  );  /*전자납부번호                                                                       */
		addSendField("NAPBU_GB"             , 1   ,  "C"  );  /*납부구분  1:신고  2:납부                                                           */
		addSendField("NAPBU_JM_NO"          , 12  ,  "C"  );  /*신고거래 전문일련번호                                                               */
		
		addSendField("FILTER2"              , 30  ,  "C"  );  /*빈칸                                                                                */
		
	
		/*
		 * 수신전문(편의점(더존))
		 * 송수신전문이 동일하므로 송신전문을 수신전문에 대체시킴 - 1080 + 49 * 13  1717 + 
		 * */
	    this.addRecvFieldAll(sendField);
	    
	}
	
	
}
