/**
 *  주시스템명 : 사이버지방세청 고도화
 *  업  무  명 : 금결원 파일 예약납부(지방세제외)
 *  기  능  명 : 결제원- 예약납부(파일송수신)
 *              
 *  클래스  ID : Txdm2550FieldList
 *  변경  이력 :
 * ------------------------------------------------------------------------
 *  작성자     소속  		 일자      Tag   내용
 * ------------------------------------------------------------------------   
 * 소도우   유채널(주)  2011.07.07     %01%  신규작성
 *  
 */
package com.uc.bs.cyber.daemon.txdm2550;

import com.uc.core.MapForm;
import com.uc.egtob.net.FieldList;
/**
 * @author Administrator
 *
 */
public class Txdm2550FieldList {
	
	
	private MapForm dataMap = null;
	
	private FieldList field ;
	
	private int     len =  0;
	
	/**
	 * 생성자
	 * 파일 송수신 전문생성
	 */
	public Txdm2550FieldList() {
		// TODO Auto-generated constructor stub
		
		field = new FieldList();

		/*메세지헤드와 4byte정보만 취합*/
		
		
		/*
		 * 송신전문(사이버)
		 * */	
		field.add("MSG_LEN"        , 4       ,  "C"  );  	/*수신메세지 길이    */
		field.add("MSG_CODE"       , 1       ,  "C"  );  	/*수신메세지 CODE    */
		field.add("MSG_RSV"        , 3       ,  "C"  );  	/*수신메세지 예비필드*/
		/*헤드*/
		field.add("BLK_TYPE"       , 1       ,  "C"  );  	/*수신메세지 TYPE    */
		field.add("BLK_FLAG"       , 3       ,  "C"  );  	/*수신메세지 Flag    */
		
		len = field.getFieldListLen();

		this.dataMap = new MapForm();
        
	}
	
	/**
	 * 파일전송 포멧(표준)
	 */
    public void Msg_Pub_FieldList() {
    	
    	field = new FieldList();

		/*결제원 송수신 전문.*/
		
		/*
		 * 송신전문(사이버)
		 * */	
		field.add("MSG_LEN"        , 4       ,  "C"  );  	/*수신메세지 길이    */
		field.add("MSG_CODE"       , 1       ,  "C"  );  	/*수신메세지 CODE    */
		field.add("MSG_RSV"        , 3       ,  "C"  );  	/*수신메세지 예비필드*/
		/*헤드*/
		field.add("BLK_DATA"       , 1032    ,  "C"  );  	/*수신메세지 DATA    */
		field.add("MSG_MD5"        , 16      ,  "B"  );  	/*수신메세지 md5     */
        
		len = field.getFieldListLen();

		this.dataMap = new MapForm();
    	
    }
	
    /**
     * 취소시 및 오류 시 전문
     */
    public void Msg_Can_FieldList() {
    	
    	field = new FieldList();

		/*
		 * 송신전문(사이버)
		 * */	
		field.add("MSG_LEN"         , 4       ,  "C"  );  	/*송신메세지 길이    */
		field.add("MSG_CODE"        , 1       ,  "C"  );  	/*송신메세지 CODE    */
		field.add("MSG_RSV"         , 3       ,  "C"  );  	/*송신메세지 예비필드*/
		/*헤드*/
		field.add("BLK_TYPE"        , 1       ,  "C"  );  	/*송신메세지 TYPE    */
		field.add("BLK_FLAG_GB"     , 1       ,  "C"  );  	/*송신메세지 메세지구분 */
		field.add("BLK_CAN_CD"      , 2       ,  "C"  );  	/*송신메세지 취소코드   */
		
		field.add("BLK_CAN_MSG_LEN" , 4       ,  "B"  );  	/*송신메세지 취소메세지길이   */
		field.add("BLK_CAN_MSG_DAT" , 1024    ,  "C"  );  	/*송신메세지 취소메세지       */
		
		field.add("MSG_MD5"         , 16      ,  "B"  );  	/*송신메세지 md5 암호화 */
        
		len = field.getFieldListLen();

		this.dataMap = new MapForm();
    	
    }
    
    /**
     * OFFSET 응답전문...
     * 파일정보 수신후 파일전송을 받거나 또는 이어받을때 응답전문
     */
    public void Msg_Pos_FieldList() {
    	
    	field = new FieldList();

		/*
		 * 송신전문(사이버)
		 * */	
		field.add("MSG_LEN"         , 4       ,  "C"  );  	/*송신메세지 길이    */
		field.add("MSG_CODE"        , 1       ,  "C"  );  	/*송신메세지 CODE    */
		field.add("MSG_RSV"         , 3       ,  "C"  );  	/*송신메세지 예비필드*/
		/*헤드*/
		field.add("BLK_TYPE"        , 1       ,  "C"  );  	/*송신메세지 TYPE    */
		field.add("BLK_FLAG"        , 3       ,  "B"  );  	/*송신메세지 FLAG    */
		
		field.add("BLK_OFFSET"      , 4       ,  "B"  );  	/*수신완료된 DATA의 Offset*/
		field.add("BLK_DATA"        , 1024    ,  "B"  );  	/*미사용             */
		
		field.add("MSG_MD5"         , 16      ,  "B"  );  	/*송신메세지 md5 암호화 */
        
		len = field.getFieldListLen();

		this.dataMap = new MapForm();
    	
    }
    
    /**
     * ACK 응답전문
     */
    public void Msg_Ack_FieldList() {
    	
    	field = new FieldList();

		/*
		 * 송신전문(사이버)
		 * */	
		field.add("MSG_LEN"         , 4       ,  "C"  );  	/*송신메세지 길이    */
		field.add("MSG_CODE"        , 1       ,  "C"  );  	/*송신메세지 CODE    */
		field.add("MSG_RSV"         , 3       ,  "C"  );  	/*송신메세지 예비필드*/
		/*헤드*/
		field.add("BLK_TYPE"        , 1       ,  "C"  );  	/*송신메세지 TYPE    */
		field.add("BLK_FLAG1"       , 1       ,  "B"  );  	/*송신메세지 FLAG1   */
		field.add("BLK_FLAG2"       , 1       ,  "C"  );  	/*송신메세지 FLAG2   */
		field.add("BLK_FLAG3"       , 1       ,  "B"  );  	/*송신메세지 FLAG3   */
		
		field.add("BLK_OFFSET"      , 4       ,  "B"  );  	/*수신완료된 DATA의 Offset*/
		field.add("BLK_DATA"        , 1024    ,  "C"  );  	/*미사용             */
		
		field.add("MSG_MD5"         , 16      ,  "B"  );  	/*송신메세지 md5 암호화 */
        
		len = field.getFieldListLen();

		this.dataMap = new MapForm();
    	
    }

    /**
     *  파일수신 시 파싱용 전문
     */
    public void Msg_Data_FieldList() {
    	
    	field = new FieldList();

		/*
		 * 수신전문(사이버)
		 * */	
		field.add("MSG_LEN"         , 4       ,  "C"  );  	/*수신메세지 길이    */
		field.add("MSG_CODE"        , 1       ,  "C"  );  	/*수신메세지 CODE    */
		field.add("MSG_RSV"         , 3       ,  "C"  );  	/*수신메세지 예비필드*/
		/*헤드*/
		field.add("BLK_TYPE"        , 1       ,  "C"  );  	/*수신메세지 TYPE    */
		field.add("BLK_FLAG_GB"     , 1       ,  "C"  );  	/*수신메세지 FLAG구분*/
		field.add("BLK_RD_LEN"      , 2       ,  "B"  );  	/*수신메세지 실제데이터 길이  */
		
		field.add("BLK_OFFSET"      , 4       ,  "B"  );  	/*수신완료된 File 내 Offset   */
		field.add("BLK_DATA"        , 1024    ,  "C"  );  	/*실제 파일 내용              */
		
		field.add("MSG_MD5"         , 16      ,  "B"  );  	/*송신메세지 md5 암호화 */
        
		len = field.getFieldListLen();

		this.dataMap = new MapForm();
    	
    }

	
	/*파일정보만*/
	public void Msg_fileinfo_FieldList() {
		// TODO Auto-generated constructor stub
		
		field = new FieldList();

		/*
		 * 수신전문(사이버)
		 * */	
		field.add("filler1"    ,  8     ,"C");    /*Filler1      */
		/*헤드*/
		field.add("msg_type"   ,  1     ,"C");    /*수신 type    */
		field.add("res_code"   ,  1     ,"C");    /*요청코드     */
		field.add("append_yn"  ,  1     ,"C");    /*append 여부  */
		field.add("filler3"    ,  1     ,"B");    /*Filler3      */
		field.add("file_size"  ,  4     ,"B");    /*파일사이즈   */
		field.add("userid"     , 64     ,"C");    /*사용자ID     */
		field.add("password"   , 64     ,"C");    /*패스워드     */
		field.add("file_name"  ,128     ,"C");    /*파일명       */
		field.add("reserved"   ,768     ,"C");    /*reserved     */
		
		len = field.getFieldListLen();

		this.dataMap = new MapForm();
        
	}
	
	
	/**
	 * ============================================================================
	 *        생 성 된 파 일 에 따 른 파 싱 용 전 문
	 * ============================================================================
	 */
	
	/**
	 * GR6653 HEAD : 지방세 예약납부 통지 내역
	 */
	public void GR6653_Head_FieldList() {
		// TODO Auto-generated constructor stub
		
		field = new FieldList();

		/*
		 * 파일파싱용
		 * */	
		field.add("BUSINESSGUBUN"  ,  6    ,"C");    /*업무구분         */
		field.add("DATAGUBUN"      ,  2    ,"H");    /*데이터구분       */
		field.add("SEQNO"          ,  7    ,"H");    /*일련번호         */
		field.add("BANKCODE"       ,  3    ,"C");    /*(저축)은행코드   */
		field.add("TOTALCOUNT"     ,  7    ,"H");    /*총 DATA RECORD 수*/
		field.add("TRANSDATE"      ,  8    ,"C");    /*수납일자         */
		field.add("FILLER"         ,197    ,"C");    /*FILLER           */

		len = field.getFieldListLen();

		this.dataMap = new MapForm();
        
	}
	/**
	 * GR6653 DATA : 지방세 예약납부 통지 내역
	 */
	public void GR6653_Data_FieldList() {
		// TODO Auto-generated constructor stub
		
		field = new FieldList();

		/*
		 * 파일파싱용
		 * */	
		field.add("BUSINESSGUBUN"  , 6    ,"C");    /*업무구분            */
		field.add("DATAGUBUN"      , 2    ,"H");    /*데이터구분          */
		field.add("SEQNO"          , 7    ,"H");    /*일련번호            */
		field.add("PUBGCODE"       , 2    ,"H");    /*발행기관분류코드    */
		field.add("JIRONO"         , 7    ,"H");    /*발행기관확인및 검증 */
		field.add("JUMINNO"        ,13    ,"C");    /*주민번호            */
		field.add("ECHENO"         ,27    ,"C");    /*이체번호            */
		field.add("BANKCODE"       , 3    ,"H");    /*(저축)은행코드      */
		field.add("JANGPNO"        , 6    ,"H");    /*장표번호            */
		field.add("SUNAPJUMCODE"   , 7    ,"C");    /*출금은행점별코드    */
		field.add("SUNAPDATE"      , 8    ,"C");    /*수납일자            */
		field.add("HGDATE"         , 8    ,"C");    /*회계일자            */
		field.add("NAPBU_GUBUN"    , 1    ,"C");    /*납기내후구분        */
		field.add("NAPBU_AMT"      ,12    ,"H");    /*납부금액            */
		field.add("OCR_BAND1"      ,54    ,"C");    /*OCRBAND1            */
		field.add("OCR_BAND2"      ,54    ,"C");    /*OCRBAND2            */
		field.add("JANGPGRP"       , 3    ,"H");    /*장표묶음번호        */
		field.add("PROCGUBUN"      , 1    ,"H");    /*처리구분:인터넷지로4*/
		field.add("SUSU_AMT"       , 4    ,"H");    /*수수료              */
		field.add("FILLER"         , 5    ,"H");    /*FILLER              */

		len = field.getFieldListLen();

		this.dataMap = new MapForm();
        
	}
	
	/**
	 * GR6653 TAILER : 지방세 예약납부 통지 내역
	 */
	public void GR6653_Tailer_FieldList() {
		// TODO Auto-generated constructor stub
		
		field = new FieldList();

		/*
		 * 파일파싱용
		 * */	
		field.add("BUSINESSGUBUN"  ,  6    ,"C");    /*업무구분         */
		field.add("DATAGUBUN"      ,  2    ,"H");    /*데이터구분       */
		field.add("SEQNO"          ,  7    ,"H");    /*일련번호         */
		field.add("BANKCODE"       ,  3    ,"C");    /*(저축)은행코드   */
		field.add("TOTALCOUNT"     ,  7    ,"H");    /*총 DATA RECORD 수*/
		field.add("TOTALPAYMONEY"  , 14    ,"H");    /*합계금액         */
		field.add("FILLER"         ,191    ,"C");    /*FILLER           */

		len = field.getFieldListLen();

		this.dataMap = new MapForm();
        
	}
	
	/**
	 * GR6655 HEAD : 지방세 수납 내역
	 */
	public void GR6655_Head_FieldList() {
		// TODO Auto-generated constructor stub
		
		field = new FieldList();

		/*
		 * 파일파싱용
		 * */	
		field.add("BUSINESSGUBUN"  ,  6    ,"C");    /*업무구분         */
		field.add("DATAGUBUN"      ,  2    ,"H");    /*데이터구분       */
		field.add("SEQNO"          ,  7    ,"H");    /*일련번호         */
		field.add("BANKCODE"       ,  3    ,"C");    /*(저축)은행코드   */
		field.add("TOTALCOUNT"     ,  7    ,"H");    /*총 DATA RECORD 수*/
		field.add("TRANSDATE"      ,  8    ,"C");    /*수납일자         */
		field.add("FILLER"         ,197    ,"C");    /*FILLER           */

		len = field.getFieldListLen();

		this.dataMap = new MapForm();
        
	}
	/**
	 * GR6655 DATA : 지방세 수납 내역
	 */
	public void GR6655_Data_FieldList() {
		// TODO Auto-generated constructor stub
		
		field = new FieldList();

		/*
		 * 파일파싱용
		 * */	
		field.add("BUSINESSGUBUN"  , 6    ,"C");    /*업무구분            */
		field.add("DATAGUBUN"      , 2    ,"H");    /*데이터구분          */
		field.add("SEQNO"          , 7    ,"H");    /*일련번호            */
		field.add("PUBGCODE"       , 2    ,"H");    /*발행기관분류코드    */
		field.add("JIRONO"         , 7    ,"H");    /*발행기관확인및 검증 */
		field.add("JUMINNO"        ,13    ,"C");    /*주민번호            */
		field.add("ECHENO"         ,27    ,"C");    /*이체번호            */
		field.add("BANKCODE"       , 3    ,"H");    /*(저축)은행코드      */
		field.add("JANGPNO"        , 6    ,"H");    /*장표번호            */
		field.add("SUNAPJUMCODE"   , 7    ,"C");    /*출금은행점별코드    */
		field.add("SUNAPDATE"      , 8    ,"C");    /*수납일자            */
		field.add("HGDATE"         , 8    ,"C");    /*회계일자            */
		field.add("NAPBU_GUBUN"    , 1    ,"C");    /*납기내후구분        */
		field.add("NAPBU_AMT"      ,12    ,"H");    /*납부금액            */
		field.add("OCR_BAND1"      ,54    ,"C");    /*OCRBAND1            */
		field.add("OCR_BAND2"      ,54    ,"C");    /*OCRBAND2            */
		field.add("JANGPGRP"       , 3    ,"H");    /*장표묶음번호        */
		field.add("PROCGUBUN"      , 1    ,"H");    /*처리구분:인터넷지로4*/
		field.add("SUSU_AMT"       , 4    ,"H");    /*수수료              */
		field.add("FILLER"         , 5    ,"H");    /*FILLER              */

		len = field.getFieldListLen();

		this.dataMap = new MapForm();
        
	}
	/**
	 * GR6655 TAILER : 지방세 수납 내역
	 */
	public void GR6655_Tailer_FieldList() {
		// TODO Auto-generated constructor stub
		
		field = new FieldList();

		/*
		 * 파일파싱용
		 * */	
		field.add("BUSINESSGUBUN"  ,  6    ,"C");    /*업무구분         */
		field.add("DATAGUBUN"      ,  2    ,"H");    /*데이터구분       */
		field.add("SEQNO"          ,  7    ,"H");    /*일련번호         */
		field.add("BANKCODE"       ,  3    ,"C");    /*(저축)은행코드   */
		field.add("TOTALCOUNT"     ,  7    ,"H");    /*총 DATA RECORD 수*/
		field.add("TOTALPAYMONEY"  , 14    ,"H");    /*합계금액         */
		field.add("FILLER"         ,417    ,"C");    /*FILLER           */

		len = field.getFieldListLen();

		this.dataMap = new MapForm();
        
	}
	
	/**
	 * GR6654 HEAD : 지방세 예약납부 변경 고지
	 */
	public void GR6654_Head_FieldList() {
		// TODO Auto-generated constructor stub
		
		field = new FieldList();

		/*
		 * 파일파싱용
		 * */	
		field.add("BUSINESSGUBUN"  ,  6    ,"C");    /*업무구분         */
		field.add("DATAGUBUN"      ,  2    ,"H");    /*데이터구분       */
		field.add("SEQNO"          ,  7    ,"H");    /*일련번호         */
		field.add("BANKCODE"       ,  3    ,"C");    /*(저축)은행코드   */
		field.add("TOTALCOUNT"     ,  7    ,"H");    /*총 DATA RECORD 수*/
		field.add("TRANSDATE"      ,  8    ,"C");    /*수납일자         */
		field.add("FILLER"         ,417    ,"C");    /*FILLER           */

		len = field.getFieldListLen();

		this.dataMap = new MapForm();
        
	}
	
	
	
	/**
	 * GR6654 DATA : 지방세 예약납부 변경 고지
	 */
	public void GR6654_Data_FieldList() {
		// TODO Auto-generated constructor stub
		
		field = new FieldList();

		/*
		 * 파일파싱용
		 * */	
		field.add("BUSINESSGUBUN"  , 6    ,"C");    /*업무구분            */
		field.add("DATAGUBUN"      , 2    ,"H");    /*데이터구분          */
		field.add("SEQNO"          , 7    ,"H");    /*일련번호            */
		field.add("PUBGCODE"       , 2    ,"H");    /*발행기관분류코드    */
		field.add("JIRONO"         , 7    ,"H");    /*발행기관확인및 검증 */
		field.add("EPAY_NO"        ,17    ,"C");    /*전자납부번호        */
		field.add("FILLER1"        ,12    ,"C");    /*예비 정보 FIELD 1   */
		field.add("REGNO"          ,13    ,"C");    /*주민(사업자,법인)등록번호*/
		field.add("SIDO"           , 2    ,"H");    /*시도구분            */
		field.add("PUB_ORG"        , 3    ,"H");    /*과세기관(시군구)    */
		field.add("CHK1"           , 1    ,"H");    /*검증번호 1          */
		field.add("ACCT"           , 2    ,"H");    /*회계                */
		field.add("GWAMOK"         , 6    ,"H");    /*과목                */
		field.add("GWAYM"          , 6    ,"H");    /*과세년월            */
		field.add("DIV"            , 1    ,"H");    /*기분                */
		field.add("HACD"           , 3    ,"H");    /*행정동(읍면동)      */
		field.add("TAX_SNO"        , 6    ,"H");    /*과세 번호           */
		field.add("CHK2"           , 1    ,"H");    /*검증번호 2          */
		field.add("REGNM"          ,40    ,"C");    /*납부자 성명         */
		field.add("SUM_B_AMT"      ,15    ,"H");    /*납기내금액          */
		field.add("SUM_F_AMT"      ,15    ,"H");    /*납기후금액          */
		field.add("CHK3"           , 1    ,"H");    /*검증번호 3          */
		field.add("TAX_STD"        ,15    ,"H");    /*과세표준            */
		field.add("MNTX"           ,15    ,"H");    /*본세                */
		field.add("MNTX_ADTX"      ,15    ,"H");    /*본세가산            */
		field.add("CPTX"           ,15    ,"H");    /*도시계획세          */
		field.add("CPTX_ADTX"      ,15    ,"H");    /*도시계획세가산      */
		field.add("CFTX"           ,15    ,"H");    /*공동시설세(농특)    */
		field.add("CFTX_ADTX"      ,15    ,"H");    /*공동시설세(농특)가산금*/
		field.add("LETX"           ,15    ,"H");    /*교육세              */
		field.add("LETX_ADTX"      ,15    ,"H");    /*교육세가산금        */
		field.add("DUE_DT"         , 8    ,"H");    /*납기내일자          */
		field.add("DUE_F_DT"       , 8    ,"H");    /*납기후일자          */
		field.add("CHK4"           , 1    ,"H");    /*검증번호 4          */
		field.add("FILLER2"        , 1    ,"H");    /*예비 정보 FIELD 2   */
		field.add("CHK5"           , 1    ,"H");    /*검증번호 5          */
		field.add("GWASESTATE"     ,60    ,"C");    /*과세사항            */
		field.add("GWASEDESC"      ,20    ,"C");    /*과세설명            */
		field.add("TAX_DT"         , 8    ,"C");    /*부과일자            */
		field.add("ATUOREG"        , 1    ,"C");    /*자동이체등록여부    */
		field.add("SUNAP_BRC"      , 7    ,"H");    /*수납점별코드        */
		field.add("SUNAP_DTM"      , 14   ,"H");    /*납부일시(실수납시)  */
		field.add("NAPGB"          , 1    ,"C");    /*납기구분            */
		field.add("PRCGB"          , 1    ,"C");    /*처리구분            */
		field.add("FILLER"         ,16    ,"C");    /*FILLER              */

		len = field.getFieldListLen();

		this.dataMap = new MapForm();
        
	}
	
	/**
	 * GR6654 HEAD : 지방세 예약납부 변경 고지
	 */
	public void GR6654_Tailer_FieldList() {
		// TODO Auto-generated constructor stub
		
		field = new FieldList();

		/*
		 * 파일파싱용
		 * */	
		field.add("BUSINESSGUBUN"  ,  6    ,"C");    /*업무구분         */
		field.add("DATAGUBUN"      ,  2    ,"H");    /*데이터구분       */
		field.add("SEQNO"          ,  7    ,"H");    /*일련번호         */
		field.add("BANKCODE"       ,  3    ,"C");    /*(저축)은행코드   */
		field.add("TOTALCOUNT"     ,  7    ,"H");    /*총 DATA RECORD 수*/
		field.add("TOTALPAYMONEY"  , 14    ,"H");    /*합계금액         */
		field.add("FILLER"         ,411    ,"C");    /*FILLER           */

		len = field.getFieldListLen();

		this.dataMap = new MapForm();
        
	}

	/**
	 * GR6677 HEAD : 상하수도 요금 수납내역
	 */
	public void GR6677_Head_FieldList() {
		// TODO Auto-generated constructor stub
		
		field = new FieldList();

		/*
		 * 파일파싱용
		 * */	
		field.add("BUSINESSGUBUN"  ,  6    ,"C");    /*업무구분         */
		field.add("DATAGUBUN"      ,  2    ,"H");    /*데이터구분       */
		field.add("SEQNO"          ,  7    ,"H");    /*일련번호         */
		field.add("BANKCODE"       ,  3    ,"C");    /*(저축)은행코드   */
		field.add("TOTALCOUNT"     ,  7    ,"H");    /*총 DATA RECORD 수*/
		field.add("TRANSDATE"      ,  8    ,"C");    /*수납일자         */
		field.add("FILLER"         ,267    ,"C");    /*FILLER           */
		
		len = field.getFieldListLen();

		this.dataMap = new MapForm();
        
	}
	/**
	 * GR6677 DATA : 상하수도 요금 수납내역
	 */
	public void GR6677_Data_FieldList() {
		// TODO Auto-generated constructor stub
		
		field = new FieldList();

		/*
		 * 파일파싱용
		 * */	
		field.add("BUSINESSGUBUN"  , 6    ,"C");    /*업무구분            */
		field.add("DATAGUBUN"      , 2    ,"H");    /*데이터구분          */
		field.add("SEQNO"          , 7    ,"H");    /*일련번호            */
		field.add("PUBGCODE"       , 2    ,"H");    /*발행기관분류코드    */
		field.add("JIRONO"         , 7    ,"H");    /*발행기관확인및 검증 */
		field.add("CUSTNO"         ,30    ,"H");    /*수용가번호          */
		field.add("KUMGOCD"        , 3    ,"H");    /*금고은행코드        */
		field.add("FILLER1"        , 6    ,"H");    /*장표처리시의 판독번호*/
		field.add("SUNAPJUMCODE"   , 7    ,"H");    /*출금은행점별코드    */
		field.add("NAPDT"          , 8    ,"H");    /*납부일자            */
		field.add("ACCTDT"         , 8    ,"H");    /*회계일자(이체일자)  */
		field.add("NAPGB"          , 1    ,"H");    /*1:납기내2:납기후3:무관*/
		field.add("NAPBU_AMT"      ,12    ,"H");    /*납부금액            */
		field.add("SANG_AMT"       ,10    ,"H");    /*상수도금액           */
		field.add("HA_AMT"         ,10    ,"H");    /*하수도금액           */
		field.add("JIHA_AMT"       ,10    ,"H");    /*지하수금액           */
		field.add("WATER_AMT"      ,10    ,"H");    /*물이용금액           */
		field.add("OCR_BAND"       ,108   ,"H");    /*OCRBAND              */
		field.add("FILLER2"        , 9    ,"H");    /*장표처리시의 묶음번호*/
		field.add("PROCGUBUN"      , 1    ,"H");    /*처리구분:인터넷지로4*/
		field.add("SUSU_AMT"       , 4    ,"H");    /*수수료              */
		field.add("DANGMON"        , 2    ,"H");    /*11 당월분, 22체납분 */
		field.add("TAX_YM"         , 6    ,"H");    /*부과년월            */
		field.add("ADMIN_NO"       , 8    ,"H");    /*관리번호            */
		field.add("EPAY_NO"        ,17    ,"H");    /*전자납부번호        */
		field.add("FILLER3"        , 6    ,"H");    /*FILLER              */

		len = field.getFieldListLen();

		this.dataMap = new MapForm();
        
	}

	/**
	 * GR6677 TAILER : 상하수도 요금 수납내역
	 */
	public void GR6677_Tailer_FieldList() {
		// TODO Auto-generated constructor stub
		
		field = new FieldList();

		/*
		 * 파일파싱용
		 * */	
		field.add("BUSINESSGUBUN"  ,  6    ,"C");    /*업무구분         */
		field.add("DATAGUBUN"      ,  2    ,"H");    /*데이터구분       */
		field.add("SEQNO"          ,  7    ,"H");    /*일련번호         */
		field.add("BANKCODE"       ,  3    ,"C");    /*(저축)은행코드   */
		field.add("TOTALCOUNT"     ,  7    ,"H");    /*총 DATA RECORD 수*/
		field.add("TOTALPAYMONEY"  , 14    ,"H");    /*합계금액         */
		field.add("FILLER"         ,261    ,"C");    /*FILLER           */

		len = field.getFieldListLen();

		this.dataMap = new MapForm();
        
	}
	
	/**
	 * GR6675 HEAD : 상하수도 요금 예약납부통지내역
	 */
	public void GR6675_Head_FieldList() {
		// TODO Auto-generated constructor stub
		
		field = new FieldList();

		/*
		 * 파일파싱용
		 * */	
		field.add("BUSINESSGUBUN"  ,  6    ,"C");    /*업무구분         */
		field.add("DATAGUBUN"      ,  2    ,"H");    /*데이터구분       */
		field.add("SEQNO"          ,  7    ,"H");    /*일련번호         */
		field.add("BANKCODE"       ,  3    ,"C");    /*(저축)은행코드   */
		field.add("TOTALCOUNT"     ,  7    ,"H");    /*총 DATA RECORD 수*/
		field.add("TRANSDATE"      ,  8    ,"C");    /*수납일자         */
		field.add("FILLER"         ,267    ,"C");    /*FILLER           */
		
		len = field.getFieldListLen();

		this.dataMap = new MapForm();
        
	}
	/**
	 * GR6675 DATA : 상하수도 요금 예약납부통지내역
	 */
	public void GR6675_Data_FieldList() {
		// TODO Auto-generated constructor stub
		
		field = new FieldList();

		/*
		 * 파일파싱용
		 * */	
		field.add("BUSINESSGUBUN"  , 6    ,"C");    /*업무구분              */
		field.add("DATAGUBUN"      , 2    ,"H");    /*데이터구분            */
		field.add("SEQNO"          , 7    ,"H");    /*일련번호              */
		field.add("PUBGCODE"       , 2    ,"H");    /*발행기관분류코드      */
		field.add("JIRONO"         , 7    ,"H");    /*발행기관확인및 검증   */
		field.add("CUSTNO"         ,30    ,"H");    /*수용가번호            */
		field.add("KUMGOCD"        , 3    ,"H");    /*금고은행코드          */
		field.add("FILLER1"        , 6    ,"H");    /*장표처리시의 판독번호 */
		field.add("SUNAPJUMCODE"   , 7    ,"H");    /*출금은행점별코드      */
		field.add("NAPDT"          , 8    ,"H");    /*납부일자              */
		field.add("ACCTDT"         , 8    ,"H");    /*회계일자(이체일자)    */
		field.add("NAPGB"          , 1    ,"H");    /*1:납기내2:납기후3:무관*/
		field.add("NAPBU_AMT"      ,12    ,"H");    /*납부금액              */
		field.add("SANG_AMT"       ,10    ,"H");    /*상수도금액            */
		field.add("HA_AMT"         ,10    ,"H");    /*하수도금액            */
		field.add("JIHA_AMT"       ,10    ,"H");    /*지하수금액            */
		field.add("WATER_AMT"      ,10    ,"H");    /*물이용금액            */
		field.add("OCR_BAND"       ,108   ,"H");    /*OCRBAND               */
		field.add("FILLER2"        , 9    ,"H");    /*장표처리시의 묶음번호 */
		field.add("PROCGUBUN"      , 1    ,"H");    /*처리구분:인터넷지로4  */
		field.add("SUSU_AMT"       , 4    ,"H");    /*수수료                */
		field.add("DANGMON"        , 2    ,"H");    /*11 당월분, 22체납분   */
		field.add("TAX_YM"         , 6    ,"H");    /*부과년월              */
		field.add("ADMIN_NO"       , 8    ,"H");    /*관리번호              */
		field.add("EPAY_NO"        ,17    ,"H");    /*전자납부번호          */
		field.add("FILLER3"        , 6    ,"H");    /*FILLER                */

		len = field.getFieldListLen();

		this.dataMap = new MapForm();
        
	}

	/**
	 * GR6675 TAILER : 상하수도 요금 예약납부통지내역
	 */
	public void GR6675_Tailer_FieldList() {
		// TODO Auto-generated constructor stub
		
		field = new FieldList();

		/*
		 * 파일파싱용
		 * */	
		field.add("BUSINESSGUBUN"  ,  6    ,"C");    /*업무구분         */
		field.add("DATAGUBUN"      ,  2    ,"H");    /*데이터구분       */
		field.add("SEQNO"          ,  7    ,"H");    /*일련번호         */
		field.add("BANKCODE"       ,  3    ,"C");    /*(저축)은행코드   */
		field.add("TOTALCOUNT"     ,  7    ,"H");    /*총 DATA RECORD 수*/
		field.add("TOTALPAYMONEY"  , 14    ,"H");    /*합계금액         */
		field.add("FILLER"         ,261    ,"C");    /*FILLER           */

		len = field.getFieldListLen();

		this.dataMap = new MapForm();
        
	}
	
	/**
	 * GR6676 HEAD : 상하수도 요금 예약납부변경고지
	 */
	public void GR6676_Head_FieldList() {
		// TODO Auto-generated constructor stub
		
		field = new FieldList();

		/*
		 * 파일파싱용
		 * */	
		field.add("BUSINESSGUBUN"  ,  6    ,"C");    /*업무구분         */
		field.add("DATAGUBUN"      ,  2    ,"H");    /*데이터구분       */
		field.add("SEQNO"          ,  7    ,"H");    /*일련번호         */
		field.add("BANKCODE"       ,  3    ,"H");    /*(저축)은행코드   */
		field.add("TOTALCOUNT"     ,  7    ,"H");    /*총 DATA RECORD 수*/
		field.add("TRANSDATE"      ,  8    ,"C");    /*수납일자         */
		field.add("FILLER"         ,447    ,"C");    /*FILLER           */
		
		len = field.getFieldListLen();

		this.dataMap = new MapForm();
        
	}
	/**
	 * GR6676 DATA : 상하수도 요금 예약납부변경고지
	 */
	public void GR6676_Data_FieldList() {
		// TODO Auto-generated constructor stub
		
		field = new FieldList();

		/*
		 * 파일파싱용
		 * */	
		field.add("BUSINESSGUBUN"      , 6       , "C");      /*업무구분                                          */
		field.add("DATAGUBUN"          , 2       , "H");      /*데이터구분                                        */
		field.add("SEQNO"              , 7       , "H");      /*일련번호                                          */
		field.add("PUBGCODE"           , 2       , "H");      /*발행기관분류코드                                  */
		field.add("JIRONO"             , 7       , "H");      /*발행기관확인및 검증                               */
		field.add("CUSTNO"             ,30       , "C");      /*수용가번호                                        */
		field.add("TAX_YM"             , 6       , "H");      /*부과년월                                          */
		field.add("DANGGUBUN"          , 1       , "C");      /*당월 구분                                         */
		field.add("NPNO"               , 10      , "C");      /*관리 번호                                         */
		field.add("REGNM"              ,20       , "C");      /*성명                                              */
    	field.add("BNAPDATE"           ,  8      , "H");      /*납기내 납기일              | 납부 마감일          */
		field.add("BNAPAMT"            , 10      , "H");      /*납기내 금액                | 체납액               */
		field.add("ANAPAMT"            , 10      , "H");      /*납기후 금액                                       */
		field.add("GUM2"               ,  1      , "H");      /*검증번호 2                                        */
		field.add("BSAMT"              , 10      , "H");      /*상수도납기내금액           |  상수도 체납액       */
		field.add("BHAMT"              , 10      , "H");      /*하수도납기내금액           |  하수도 체납액       */
		field.add("BGAMT"              , 10      , "H");      /*지하수납기내금액           |  지하수 체납액       */
		field.add("BMAMT"              , 10      , "H");      /*물이용부담금납기내금액     |  물이용부담금체납액  */
		field.add("ASAMT"              , 10      , "H");      /*상수도납기후금액                                  */
		field.add("AHAMT"              , 10      , "H");      /*하수도납기후금액                                  */
		field.add("AGAMT"              , 10      , "H");      /*지하수납기후금액                                  */
		field.add("AMAMT"              , 10      , "H");      /*물이용부담금납기후금액                            */
		field.add("ANAPDATE"           ,  8      , "H");      /*납기후 납기일                                     */
		field.add("GUM3"               ,  1      , "H");      /*검증번호 3                                        */
		field.add("CNAPTERM"           , 16      , "H");      /*체납 기간                                         */
		field.add("ADDR"               , 60      , "C");      /*주소                                              */
		field.add("USETERM"            , 16      , "H");      /*사용 기간                                         */
		field.add("AUTOREG"            ,  1      , "C");      /*자동이체 등록 여부                                */
		field.add("SNAP_BANK_CODE"     ,  7      , "H");      /*수납은행 점별 코드                                */
		field.add("SNAP_SYMD"          , 14      , "H");      /*납부 일시                                         */
		field.add("NAPGUBUN"           ,  1      , "C");      /*납기 내후 구분                                    */
		field.add("ETC1"               ,  9      , "C");      /*예비 정보 FIELD                                   */
		field.add("CUST_ADMIN_NUM"     , 30      , "C");      /*고객관리번호                                      */
		field.add("OCR"                ,108      , "C");      /*OCR BAND                                          */
		field.add("PRCGB"              , 1       , "C");      /*처리구분                                          */
		field.add("FILLER"             , 8       , "C");      /*FILLER                                            */


		len = field.getFieldListLen();

		this.dataMap = new MapForm();
        
	}

	/**
	 * GR6676 TAILER : 상하수도 요금 예약납부변경고지
	 */
	public void GR6676_Tailer_FieldList() {
		// TODO Auto-generated constructor stub
		
		field = new FieldList();

		/*
		 * 파일파싱용
		 * */	
		field.add("BUSINESSGUBUN"  ,  6    ,"C");    /*업무구분         */
		field.add("DATAGUBUN"      ,  2    ,"H");    /*데이터구분       */
		field.add("SEQNO"          ,  7    ,"H");    /*일련번호         */
		field.add("BANKCODE"       ,  3    ,"H");    /*(저축)은행코드   */
		field.add("TOTALCOUNT"     ,  7    ,"H");    /*총 DATA RECORD 수*/
		field.add("TOTALPAYMONEY"  , 14    ,"H");    /*합계금액         */
		field.add("FILLER"         ,441    ,"C");    /*FILLER           */

		len = field.getFieldListLen();

		this.dataMap = new MapForm();
        
	}
	
	

	/**
	 * GR6681 HEAD : 세외수입 수납내역
	 */
	public void GR6681_Head_FieldList() {
		// TODO Auto-generated constructor stub
		
		field = new FieldList();

		/*
		 * 파일파싱용
		 * */	
		field.add("BUSINESSGUBUN"  ,  6    ,"C");    /*업무구분         */
		field.add("DATAGUBUN"      ,  2    ,"H");    /*데이터구분       */
		field.add("SEQNO"          ,  7    ,"H");    /*일련번호         */
		field.add("BANKCODE"       ,  3    ,"C");    /*(저축)은행코드   */
		field.add("TOTALCOUNT"     ,  7    ,"H");    /*총 DATA RECORD 수*/
		field.add("TRANSDATE"      ,  8    ,"C");    /*수납일자         */
		field.add("FILLER"         ,197    ,"C");    /*FILLER           */
		
		len = field.getFieldListLen();

		this.dataMap = new MapForm();
        
	}
	/**
	 * GR6681 DATA : 세외수입 수납내역
	 */
	public void GR6681_Data_FieldList() {
		// TODO Auto-generated constructor stub
		
		field = new FieldList();

		/*
		 * 파일파싱용
		 * */	
		field.add("BUSINESSGUBUN"  , 6    ,"C");    /*업무구분            */
		field.add("DATAGUBUN"      , 2    ,"H");    /*데이터구분          */
		field.add("SEQNO"          , 7    ,"H");    /*일련번호            */
		field.add("PUBGCODE"       , 2    ,"H");    /*발행기관분류코드    */
		field.add("JIRONO"         , 7    ,"H");    /*발행기관확인및 검증 */
		field.add("REG_NO"         ,13    ,"C");    /*주민번호            */
		field.add("TAX_NO"         ,27    ,"C");    /*이체번호            */
		field.add("KUMGOCD"        , 3    ,"C");    /*금고은행코드        */
		field.add("FILLER1"        , 6    ,"H");    /*장표처리시의 판독번호*/
		field.add("SUNAPJUMCODE"   , 7    ,"H");    /*출금은행점별코드    */
		field.add("NAPDT"          , 8    ,"H");    /*납부일자            */
		field.add("ACCTDT"         , 8    ,"H");    /*회계일자(이체일자)  */
		field.add("NAPGB"          , 1    ,"H");    /*1:납기내2:납기후3:무관*/
		field.add("NAPBU_AMT"      ,12    ,"H");    /*납부금액            */
		field.add("OCR_BAND"       ,108   ,"H");    /*OCRBAND              */
		field.add("FILLER2"        , 3    ,"H");    /*장표처리시의 묶음번호*/
		field.add("PROCGUBUN"      , 1    ,"H");    /*처리구분:인터넷지로4*/
		field.add("SUSU_AMT"       , 4    ,"H");    /*수수료              */
		field.add("FILLER3"        , 5    ,"H");    /*FILLER              */

		len = field.getFieldListLen();

		this.dataMap = new MapForm();
        
	}

	/**
	 * GR6681 TAILER : 세외수입 수납내역
	 */
	public void GR6681_Tailer_FieldList() {
		// TODO Auto-generated constructor stub
		
		field = new FieldList();

		/*
		 * 파일파싱용
		 * */	
		field.add("BUSINESSGUBUN"  ,  6    ,"C");    /*업무구분         */
		field.add("DATAGUBUN"      ,  2    ,"H");    /*데이터구분       */
		field.add("SEQNO"          ,  7    ,"H");    /*일련번호         */
		field.add("BANKCODE"       ,  3    ,"C");    /*(저축)은행코드   */
		field.add("TOTALCOUNT"     ,  7    ,"H");    /*총 DATA RECORD 수*/
		field.add("TOTALPAYMONEY"  , 14    ,"H");    /*합계금액         */
		field.add("FILLER"         ,191    ,"C");    /*FILLER           */

		len = field.getFieldListLen();

		this.dataMap = new MapForm();
        
	}
	
	/**
	 * GR6694 HEAD : 세외수입 예약납부통지내역
	 */
	public void GR6694_Head_FieldList() {
		// TODO Auto-generated constructor stub
		
		field = new FieldList();

		/*
		 * 파일파싱용
		 * */	
		field.add("BUSINESSGUBUN"  ,  6    ,"C");    /*업무구분         */
		field.add("DATAGUBUN"      ,  2    ,"H");    /*데이터구분       */
		field.add("SEQNO"          ,  7    ,"H");    /*일련번호         */
		field.add("BANKCODE"       ,  3    ,"C");    /*(저축)은행코드   */
		field.add("TOTALCOUNT"     ,  7    ,"H");    /*총 DATA RECORD 수*/
		field.add("TRANSDATE"      ,  8    ,"C");    /*수납일자         */
		field.add("FILLER"         ,197    ,"C");    /*FILLER           */
		
		len = field.getFieldListLen();

		this.dataMap = new MapForm();
        
	}
	/**
	 * GR6694 DATA : 세외수입 예약납부통지내역
	 */
	public void GR6694_Data_FieldList() {
		// TODO Auto-generated constructor stub
		
		field = new FieldList();

		/*
		 * 파일파싱용
		 * */	
		field.add("BUSINESSGUBUN"  , 6    ,"C");    /*업무구분              */
		field.add("DATAGUBUN"      , 2    ,"H");    /*데이터구분            */
		field.add("SEQNO"          , 7    ,"H");    /*일련번호              */
		field.add("PUBGCODE"       , 2    ,"H");    /*발행기관분류코드      */
		field.add("JIRONO"         , 7    ,"H");    /*발행기관확인및 검증   */
		field.add("REG_NO"         ,13    ,"C");    /*주민번호              */
		field.add("EPAY_NO"        ,27    ,"C");    /*전자납부번호          */
		field.add("KUMGOCD"        , 3    ,"C");    /*금고은행코드          */
		field.add("FILLER1"        , 6    ,"H");    /*장표처리시의 판독번호 */
		field.add("SUNAPJUMCODE"   , 7    ,"H");    /*출금은행점별코드      */
		field.add("NAPDT"          , 8    ,"H");    /*예약접수일              */
		field.add("ACCTDT"         , 8    ,"H");    /*예약납부 희망일자(이체일자)*/
		field.add("NAPGB"          , 1    ,"H");    /*1:납기내2:납기후3:무관*/
		field.add("NAPBU_AMT"      ,12    ,"H");    /*납부금액              */
		field.add("OCR_BAND"       ,108   ,"H");    /*OCRBAND               */
		field.add("FILLER2"        , 3    ,"H");    /*장표처리시의 묶음번호 */
		field.add("PROCGUBUN"      , 1    ,"H");    /*처리구분:인터넷지로4  */
		field.add("SUSU_AMT"       , 4    ,"H");    /*수수료                */
		field.add("FILLER3"        , 5    ,"H");    /*FILLER                */

		len = field.getFieldListLen();

		this.dataMap = new MapForm();
        
	}

	/**
	 * GR6694 TAILER : 세외수입 예약납부통지내역
	 */
	public void GR6694_Tailer_FieldList() {
		// TODO Auto-generated constructor stub
		
		field = new FieldList();

		/*
		 * 파일파싱용
		 * */	
		field.add("BUSINESSGUBUN"  ,  6    ,"C");    /*업무구분         */
		field.add("DATAGUBUN"      ,  2    ,"H");    /*데이터구분       */
		field.add("SEQNO"          ,  7    ,"H");    /*일련번호         */
		field.add("BANKCODE"       ,  3    ,"C");    /*(저축)은행코드   */
		field.add("TOTALCOUNT"     ,  7    ,"H");    /*총 DATA RECORD 수*/
		field.add("TOTALPAYMONEY"  , 14    ,"H");    /*합계금액         */
		field.add("FILLER"         ,191    ,"C");    /*FILLER           */

		len = field.getFieldListLen();

		this.dataMap = new MapForm();
        
	}
	
	
	
	/**
	 * GR6695 HEAD : 세외수입 예약납부변경고지
	 */
	public void GR6695_Head_FieldList() {
		// TODO Auto-generated constructor stub
		
		field = new FieldList();

		/*
		 * 파일파싱용
		 * */	
		field.add("BUSINESSGUBUN"  ,  6    ,"C");    /*업무구분         */
		field.add("DATAGUBUN"      ,  2    ,"H");    /*데이터구분       */
		field.add("SEQNO"          ,  7    ,"H");    /*일련번호         */
		field.add("BANKCODE"       ,  3    ,"H");    /*(저축)은행코드   */
		field.add("TOTALCOUNT"     ,  7    ,"H");    /*총 DATA RECORD 수*/
		field.add("TRANSDATE"      ,  8    ,"H");    /*수납일자         */
		field.add("FILLER"         ,667    ,"C");    /*FILLER           */
		
		len = field.getFieldListLen();

		this.dataMap = new MapForm();
        
	}
	/**
	 * GR6695 DATA : 세외수입 예약납부변경고지
	 */
	public void GR6695_Data_FieldList() {
		// TODO Auto-generated constructor stub
		
		field = new FieldList();

		/*
		 * 파일파싱용
		 * */			
		field.add("BUSINESSGUBUN"       , 6  ,  "C"  );  /*업무구분                  */
		field.add("DATAGUBUN"           , 2  ,  "H"  );  /*데이터구분                */
		field.add("SEQNO"               , 7  ,  "H"  );  /*일련번호                  */
		field.add("PUBGCODE"            , 2  ,  "H"  );  /*발행기관분류코드          */
		field.add("JIRONO"              , 7  ,  "H"  );  /*발행기관확인및 검증       */
		field.add("EPAY_NO"            	, 19 ,  "C"  );  /*전자납부번호              */
		field.add("REG_NO"              , 13 ,  "C"  );  /*주민번호                  */
		field.add("FIELD1"              , 2  ,  "C"  );  /*예비 정보 FIELD 1         */
		field.add("BUGWA_GB"            , 1  ,  "H"  );  /*고지구분 		         */
		field.add("SEMOK_CD"          	, 6  ,  "H"  );  /*과목/세목                 */
		field.add("SEMOK_NM"            , 50 ,  "C"  );  /*과목/세목명               */
		field.add("GBN"              	, 1  ,  "C"  );  /*구분                      */
		field.add("OCR_BD"             	, 108,  "C"  );  /*OCR밴드                   */
		field.add("NAP_NAME"            , 40 ,  "C"  );  /*납부자 성명               */
		field.add("NAP_BFAMT"           , 15 ,  "H"  );  /*납기내 금액               */
		field.add("NAP_AFAMT"           , 15 ,  "H"  );  /*납기후 금액               */
	    field.add("GUKAMT"          	, 11 ,  "H"  );  /*국세 	                 */
	    field.add("GUKAMT_ADD"          , 11 ,  "H"  );  /*국세 가산금               */
	    field.add("SIDO_AMT"            , 11 ,  "H"  );  /*시도세                    */
	    field.add("SIDO_AMT_ADD"        , 11 ,  "H"  );  /*시도세 가산금             */
	    field.add("SIGUNGU_AMT"         , 11 ,  "H"  );  /*시군구세      	         */
	    field.add("SIGUNGU_AMT_ADD"     , 11 ,  "H"  );  /*시군구세 가산금   	     */
	    field.add("BUNAP_AMT"           , 11 ,  "H"  );  /*분납이자/기금             */
	    field.add("BUNAP_AMT_ADD"       , 11 ,  "H"  );  /*분납이자/기금 가산금      */
	    field.add("FIELD2"              , 11 ,  "C"  );  /*예비 정보 FIELD 2         */
	    field.add("NAP_BFDATE"          , 8  ,  "H"  );  /*납기일 (납기내)           */
	    field.add("NAP_AFDATE"          , 8  ,  "H"  );  /*납기일 (납기후)           */
	    field.add("GWASE_ITEM"          , 100,  "C"  );  /*과세대상                  */
	    field.add("BUGWA_TAB"          	, 150,  "C"  );  /*부과내역                  */
	    field.add("GOJI_DATE"   		, 8  ,  "H"  );  /*고지자료 발생일자         */
	    field.add("OUTO_ICHE_GB"        , 1  ,  "C"  );  /*자동이체등록여부          */
	    field.add("SUNAB_BANK_CD"       , 7  ,  "H"  );  /*수납은행 점별 코드        */
	    field.add("RECIP_DATE"          , 14 ,  "H"  );  /*납부일시			         */
	    field.add("NAPGI_BA_GB"         , 1  ,  "C"  );  /*납기내후구분              */
	    field.add("PRCGB"               , 1  ,  "C"  );  /*처리구분                  */
	    field.add("FILED3"          	, 9  ,  "C"  );  /*예비정보 FIELD 3          */		

		len = field.getFieldListLen();

		this.dataMap = new MapForm();
        
	}

	/**
	 * GR6695 TAILER : 세외수입 예약납부변경고지
	 */
	public void GR6695_Tailer_FieldList() {
		// TODO Auto-generated constructor stub
		
		field = new FieldList();

		/*
		 * 파일파싱용
		 * */	
		field.add("BUSINESSGUBUN"  ,  6    ,"C");    /*업무구분         */
		field.add("DATAGUBUN"      ,  2    ,"H");    /*데이터구분       */
		field.add("SEQNO"          ,  7    ,"H");    /*일련번호         */
		field.add("BANKCODE"       ,  3    ,"H");    /*(저축)은행코드   */
		field.add("TOTALCOUNT"     ,  7    ,"H");    /*총 DATA RECORD 수*/
		field.add("TOTALPAYMONEY"  , 14    ,"H");    /*합계금액         */
		field.add("FILLER"         ,661    ,"C");    /*FILLER           */

		len = field.getFieldListLen();

		this.dataMap = new MapForm();
        
	}
	

	/**
	 * GR6685 HEAD : 환경개선부담금 수납내역
	 */
	public void GR6685_Head_FieldList() {
		// TODO Auto-generated constructor stub
		
		field = new FieldList();

		/*
		 * 파일파싱용
		 * */	
		field.add("BUSINESSGUBUN"  ,  6    ,"C");    /*업무구분         */
		field.add("DATAGUBUN"      ,  2    ,"H");    /*데이터구분       */
		field.add("SEQNO"          ,  7    ,"H");    /*일련번호         */
		field.add("BANKCODE"       ,  3    ,"C");    /*(저축)은행코드   */
		field.add("TOTALCOUNT"     ,  7    ,"H");    /*총 DATA RECORD 수*/
		field.add("TRANSDATE"      ,  8    ,"C");    /*수납일자         */
		field.add("FILLER"         ,197    ,"C");    /*FILLER           */
		
		len = field.getFieldListLen();

		this.dataMap = new MapForm();
        
	}
	/**
	 * GR6685 DATA : 환경개선부담금 수납내역
	 */
	public void GR6685_Data_FieldList() {
		// TODO Auto-generated constructor stub
		
		field = new FieldList();

		/*
		 * 파일파싱용
		 * */	
		field.add("BUSINESSGUBUN"  , 6    ,"C");    /*업무구분            */
		field.add("DATAGUBUN"      , 2    ,"H");    /*데이터구분          */
		field.add("SEQNO"          , 7    ,"H");    /*일련번호            */
		field.add("PUBGCODE"       , 2    ,"H");    /*발행기관분류코드    */
		field.add("JIRONO"         , 7    ,"H");    /*발행기관확인및 검증 */
		field.add("REG_NO"         ,13    ,"C");    /*주민번호            */
		field.add("TAX_NO"         ,27    ,"C");    /*이체번호            */
		field.add("KUMGOCD"        , 3    ,"C");    /*금고은행코드        */
		field.add("FILLER1"        , 6    ,"H");    /*장표처리시의 판독번호*/
		field.add("SUNAPJUMCODE"   , 7    ,"H");    /*출금은행점별코드    */
		field.add("NAPDT"          , 8    ,"H");    /*납부일자            */
		field.add("ACCTDT"         , 8    ,"H");    /*회계일자(이체일자)  */
		field.add("NAPGB"          , 1    ,"H");    /*1:납기내2:납기후3:무관*/
		field.add("NAPBU_AMT"      ,12    ,"H");    /*납부금액            */
		field.add("OCR_BAND"       ,108   ,"H");    /*OCRBAND              */
		field.add("FILLER2"        , 3    ,"H");    /*장표처리시의 묶음번호*/
		field.add("PROCGUBUN"      , 1    ,"H");    /*처리구분:인터넷지로4*/
		field.add("SUSU_AMT"       , 4    ,"H");    /*수수료              */
		field.add("FILLER3"        , 5    ,"H");    /*FILLER              */

		len = field.getFieldListLen();

		this.dataMap = new MapForm();
        
	}

	/**
	 * GR6685 TAILER : 환경개선부담금 수납내역
	 */
	public void GR6685_Tailer_FieldList() {
		// TODO Auto-generated constructor stub
		
		field = new FieldList();

		/*
		 * 파일파싱용
		 * */	
		field.add("BUSINESSGUBUN"  ,  6    ,"C");    /*업무구분         */
		field.add("DATAGUBUN"      ,  2    ,"H");    /*데이터구분       */
		field.add("SEQNO"          ,  7    ,"H");    /*일련번호         */
		field.add("BANKCODE"       ,  3    ,"C");    /*(저축)은행코드   */
		field.add("TOTALCOUNT"     ,  7    ,"H");    /*총 DATA RECORD 수*/
		field.add("TOTALPAYMONEY"  , 14    ,"H");    /*합계금액         */
		field.add("FILLER"         ,191    ,"C");    /*FILLER           */

		len = field.getFieldListLen();

		this.dataMap = new MapForm();
        
	}
	
	/**
	 * GR6696 HEAD : 환경개선부담금 예약납부통지내역
	 */
	public void GR6696_Head_FieldList() {
		// TODO Auto-generated constructor stub
		
		field = new FieldList();

		/*
		 * 파일파싱용
		 * */	
		field.add("BUSINESSGUBUN"  ,  6    ,"C");    /*업무구분         */
		field.add("DATAGUBUN"      ,  2    ,"H");    /*데이터구분       */
		field.add("SEQNO"          ,  7    ,"H");    /*일련번호         */
		field.add("BANKCODE"       ,  3    ,"C");    /*(저축)은행코드   */
		field.add("TOTALCOUNT"     ,  7    ,"H");    /*총 DATA RECORD 수*/
		field.add("TRANSDATE"      ,  8    ,"C");    /*수납일자         */
		field.add("FILLER"         ,197    ,"C");    /*FILLER           */
		
		len = field.getFieldListLen();

		this.dataMap = new MapForm();
        
	}
	/**
	 * GR6696 DATA : 환경개선부담금 예약납부통지내역
	 */
	public void GR6696_Data_FieldList() {
		// TODO Auto-generated constructor stub
		
		field = new FieldList();

		/*
		 * 파일파싱용
		 * */	
		field.add("BUSINESSGUBUN"  , 6    ,"C");    /*업무구분              */
		field.add("DATAGUBUN"      , 2    ,"H");    /*데이터구분            */
		field.add("SEQNO"          , 7    ,"H");    /*일련번호              */
		field.add("PUBGCODE"       , 2    ,"H");    /*발행기관분류코드      */
		field.add("JIRONO"         , 7    ,"H");    /*발행기관확인및 검증   */
		field.add("REG_NO"         ,13    ,"C");    /*주민번호              */
		field.add("TAX_NO"         ,27    ,"C");    /*이체번호              */
		field.add("KUMGOCD"        , 3    ,"C");    /*금고은행코드          */
		field.add("FILLER1"        , 6    ,"H");    /*장표처리시의 판독번호 */
		field.add("SUNAPJUMCODE"   , 7    ,"H");    /*출금은행점별코드      */
		field.add("NAPDT"          , 8    ,"H");    /*납부일자              */
		field.add("ACCTDT"         , 8    ,"H");    /*회계일자(이체일자)    */
		field.add("NAPGB"          , 1    ,"H");    /*1:납기내2:납기후3:무관*/
		field.add("NAPBU_AMT"      ,12    ,"H");    /*납부금액              */
		field.add("OCR_BAND"       ,108   ,"H");    /*OCRBAND               */
		field.add("FILLER2"        , 3    ,"H");    /*장표처리시의 묶음번호 */
		field.add("PROCGUBUN"      , 1    ,"H");    /*처리구분:인터넷지로4  */
		field.add("SUSU_AMT"       , 4    ,"H");    /*수수료                */
		field.add("FILLER3"        , 5    ,"H");    /*FILLER                */

		len = field.getFieldListLen();

		this.dataMap = new MapForm();
        
	}

	/**
	 * GR6696 TAILER : 환경개선부담금 예약납부통지내역
	 */
	public void GR6696_Tailer_FieldList() {
		// TODO Auto-generated constructor stub
		
		field = new FieldList();

		/*
		 * 파일파싱용
		 * */	
		field.add("BUSINESSGUBUN"  ,  6    ,"C");    /*업무구분         */
		field.add("DATAGUBUN"      ,  2    ,"H");    /*데이터구분       */
		field.add("SEQNO"          ,  7    ,"H");    /*일련번호         */
		field.add("BANKCODE"       ,  3    ,"C");    /*(저축)은행코드   */
		field.add("TOTALCOUNT"     ,  7    ,"H");    /*총 DATA RECORD 수*/
		field.add("TOTALPAYMONEY"  , 14    ,"H");    /*합계금액         */
		field.add("FILLER"         ,191    ,"C");    /*FILLER           */

		len = field.getFieldListLen();

		this.dataMap = new MapForm();
        
	}
	
	
	/**
	 * GR6697 HEAD : 환경개선부담금 예약납부변경고지
	 */
	public void GR6697_Head_FieldList() {
		// TODO Auto-generated constructor stub
		
		field = new FieldList();

		/*
		 * 파일파싱용
		 * */	
		field.add("BUSINESSGUBUN"  ,  6    ,"C");    /*업무구분         */
		field.add("DATAGUBUN"      ,  2    ,"H");    /*데이터구분       */
		field.add("SEQNO"          ,  7    ,"H");    /*일련번호         */
		field.add("BANKCODE"       ,  3    ,"C");    /*(저축)은행코드   */
		field.add("TOTALCOUNT"     ,  7    ,"H");    /*총 DATA RECORD 수*/
		field.add("TRANSDATE"      ,  8    ,"H");    /*수납일자         */
		field.add("FILLER"         ,417    ,"C");    /*FILLER           */
		
		len = field.getFieldListLen();

		this.dataMap = new MapForm();
        
	}
	/**
	 * GR6697 DATA : 환경개선부담금 예약납부변경고지
	 */
	public void GR6697_Data_FieldList() {
		// TODO Auto-generated constructor stub
		
		field = new FieldList();

		/*
		 * 파일파싱용
		 * */	
		field.add("BUSINESSGUBUN"        , 6  ,  "C"  );  /*업무구분                  */
		field.add("DATAGUBUN"            , 2  ,  "H"  );  /*데이터구분                */
		field.add("SEQNO"                , 7  ,  "H"  );  /*일련번호                  */
		field.add("PUBGCODE"             , 2  ,  "H"  );  /*발행기관분류코드          */
		field.add("JIRONO"               , 7  ,  "H"  );  /*발행기관확인및 검증       */
		field.add("ETAX_NO"              , 29 ,  "C"  );  /*전자납부번호              */
		field.add("JUMIN_NO"             , 13 ,  "C"  );  /*주민(사업자,법인)등록번호 */
		field.add("SIDO"                 , 2  ,  "H"  );  /*시도                      */
		field.add("GU_CODE"              , 3  ,  "H"  );  /*과세기관(시군구)          */
		field.add("CONFIRM_NO1"          , 1  ,  "H"  );  /*검증번호 1                */
		field.add("HCALVAL"              , 2  ,  "H"  );  /*회계                      */
		field.add("GWA_MOK"              , 6  ,  "H"  );  /*과목/세목                 */
		field.add("TAX_YYMM"             , 6  ,  "H"  );  /*년도/기분                 */
		field.add("KIBUN"                , 1  ,  "H"  );  /*구분                      */
		field.add("DONG_CODE"            , 3  ,  "H"  );  /*행정동(읍면동)            */
		field.add("GWASE_NO"             , 6  ,  "H"  );  /*관리 번호                 */
		field.add("CONFIRM_NO2"          , 1  ,  "H"  );  /*검증번호 2                */
		field.add("NAP_NAME"             , 40 ,  "C"  );  /*납부자 성명               */
		field.add("NAP_BFAMT"            , 15 ,  "H"  );  /*납기내 금액               */
		field.add("NAP_AFAMT"            , 15 ,  "H"  );  /*납기후 금액               */
	    field.add("CONFIRM_NO3"          , 1  ,  "H"  );  /*검증번호 3                */
	    field.add("GWASE_RULE"           , 15 ,  "H"  );  /*과세 표준                 */
	    field.add("BONSE"                , 15 ,  "H"  );  /*부담금                    */
	    field.add("BONSE_ADD"            , 15 ,  "H"  );  /*부담금 가산금             */
	    field.add("DOSISE"               , 15 ,  "H"  );  /*미수 부담금               */
	    field.add("DOSISE_ADD"           , 15 ,  "H"  );  /*미수 부담금 가산금        */
	    field.add("GONGDONGSE"           , 15 ,  "H"  );  /*예비 정보 FIELD 1         */
	    field.add("GONGDONGSE_ADD"       , 15 ,  "H"  );  /*예비 정보 FIELD 2         */
	    field.add("EDUSE"                , 15 ,  "H"  );  /*예비 정보 FIELD 3         */
	    field.add("EDUSE_ADD"            , 15 ,  "H"  );  /*예비 정보 FIELD 4         */
	    field.add("NAP_BFDATE"           , 8  ,  "H"  );  /*납기일 (납기내)           */
	    field.add("NAP_AFDATE"           , 8  ,  "H"  );  /*납기일 (납기후)           */
	    field.add("CONFIRM_NO4"          , 1  ,  "H"  );  /*검증번호 4                */
	    field.add("FILLER1"              , 1  ,  "H"  );  /*필러                      */
	    field.add("CONFIRM_NO5"          , 1  ,  "H"  );  /*검증번호 5                */
	    field.add("GWASE_DESC"           , 60 ,  "C"  );  /*과세 사항                 */
	    field.add("GWASE_PUB_DESC"       , 20 ,  "C"  );  /*과세 표준 설명            */
	    field.add("GOJICR_DATE"          , 8  ,  "H"  );  /*고지자료 발생일자         */
	    field.add("JADONG_YN"            , 1  ,  "C"  );  /*자동이체 등록 여부        */
	    field.add("JIJUM_CODE"           , 7  ,  "H"  );  /*수납은행 점별 코드        */
	    field.add("NAPBU_DATE"           , 14 ,  "H"  );  /*납부 일시                 */
	    field.add("NP_BAF_GUBUN"         , 1  ,  "C"  );  /*납기 내후 구분            */
	    field.add("TAX_GOGI_GUBUN"       , 1  ,  "C"  );  /*세금 종류 구분            */
	    field.add("JA_GOGI_GUBUN"        , 1  ,  "C"  );  /*장표 고지 형태            */
	    field.add("PRCGB"                , 1  ,  "C"  );  /*처리구분                  */
	    field.add("RESERV2"              , 14 ,  "C"  );  /*예비 정보 FIELD 5         */	  

		len = field.getFieldListLen();

		this.dataMap = new MapForm();
        
	}

	/**
	 * GR6697 TAILER : 환경개선부담금 예약납부변경고지
	 */
	public void GR6697_Tailer_FieldList() {
		// TODO Auto-generated constructor stub
		
		field = new FieldList();

		/*
		 * 파일파싱용
		 * */	
		field.add("BUSINESSGUBUN"  ,  6    ,"C");    /*업무구분         */
		field.add("DATAGUBUN"      ,  2    ,"H");    /*데이터구분       */
		field.add("SEQNO"          ,  7    ,"H");    /*일련번호         */
		field.add("BANKCODE"       ,  3    ,"C");    /*(저축)은행코드   */
		field.add("TOTALCOUNT"     ,  7    ,"H");    /*총 DATA RECORD 수*/
		field.add("TOTALPAYMONEY"  , 14    ,"H");    /*합계금액         */
		field.add("FILLER"         ,411    ,"C");    /*FILLER           */

		len = field.getFieldListLen();

		this.dataMap = new MapForm();
        
	}
	

	/**
	 * ============================================================================
	 *        파 싱 함 수 들
	 * ============================================================================
	 */
	
	/**
	 * 전문 엔진에 송신할 헤더 생성
	 */
	public byte[] getBuff(MapForm mapForm) throws Exception{
		
		dataMap = mapForm;

		return getBuff();
	}
	
	/**
	 * 
	 * @param msgType
	 * @param srcId
	 * @return
	 * @throws Exception
	 */
	public byte[] getBuff() throws Exception{

		byte[] headBuf ;
				
		headBuf =  field.makeMessageByte(dataMap);
		
		return headBuf;		
		
	}
	
	/**
	 * 전문파싱...
	 * @param buffer
	 * @return
	 * @throws Exception
	 */
	public MapForm parseBuff(byte[] buffer) throws Exception{

		try {
			dataMap = field.parseMessage(buffer, 0);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw e;
		}

		return dataMap;
	}

	public int getLen() {
		// TODO Auto-generated method stub
		return len;
	}


	public FieldList getFieldList() {
		// TODO Auto-generated method stub
		return field;
	}
	
	/**
	 * 
	 * @param fldName
	 * @return
	 */
	public String getField(String fldName) {
		
		return (String) dataMap.getMap(fldName);
	}

	/**
	 * 
	 * @param key
	 * @param val
	 */
	public void setField(String key, String val) {
		// TODO Auto-generated method stub
		this.dataMap.setMap(key, val);
	}

	public MapForm getDataMap() {
		// TODO Auto-generated method stub
		return this.dataMap;
	}

	/**
	 * 
	 * @param mapForm
	 */
	public void setDataMap(MapForm mapForm) {
		// TODO Auto-generated method stub
		this.dataMap = mapForm;
	}
	
}
