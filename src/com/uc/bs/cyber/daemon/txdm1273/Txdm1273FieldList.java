/**
 *  주시스템명 : 사이버지방세청 고도화
 *  업  무  명 : 수기고지서 수납자료 등록, 소인파일 파싱용
 *  기  능  명 : 수기고지서 주민세 양도소득할 소인파일을 파싱하기 위한 필드정의
 *              
 *  클래스  ID : Txdm1273FieldList
 *  변경  이력 :
 * ------------------------------------------------------------------------
 *  작성자     소속  		 일자      Tag   내용
 * ------------------------------------------------------------------------   
 * 송동욱   유채널(주)  2011.10.17     %01%  신규작성
 *  
 */
package com.uc.bs.cyber.daemon.txdm1273;

import com.uc.core.MapForm;
import com.uc.egtob.net.FieldList;
/**
 * @author Administrator
 *
 */
public class Txdm1273FieldList {
	
	
	private MapForm dataMap = null;
	
	private FieldList field ;
	
	private int     len =  0;
	
	/**
	 * 생성자
	 * 소인파일 파싱용 전문작성
	 */
	public Txdm1273FieldList() {
		// TODO Auto-generated constructor stub
		
		field = new FieldList();
		
		/*
		 * 주민세 양도소득할 소인파일 포멧정보
		 * */	
		field.add("SIDO_COD"            ,2       ,"C");  /*"시도코드"                 */
		field.add("SGG_COD"             ,3       ,"C");  /*"시군구코드"               */
		field.add("ACCT_COD"            ,2       ,"C");  /*"시군구코드"               */
		field.add("TAX_ITEM"            ,6       ,"C");  /*"과세년월"                 */
		field.add("TAX_YYMM"            ,6       ,"C");  /*"과세년월"                 */
		field.add("TAX_DIV"             ,1       ,"C");  /*"과세구분"                 */
		field.add("HACD"                ,3       ,"C");  /*"과세행정동"               */
		field.add("TAX_SNO"             ,6       ,"C");  /*"과세번호"                 */
		field.add("TAX_DT"              ,8       ,"C");  /*"신고일자"                 */
		field.add("REG_NO"              ,13      ,"C");  /*"납세자 주민/법인번호"     */
		field.add("REG_NM"              ,30      ,"C");  /*"납세자 성명"              */
		field.add("TPR_COD"             ,2       ,"C");  /*"법인코드"                 */
		field.add("REG_BUCD"            ,5       ,"C");  /*"납세자 법정동"            */
		field.add("ZIP_NO"              ,6       ,"C");  /*"납세자 우편번호"          */
		field.add("ZIP_ADDR"            ,60      ,"C");  /*"납세자 우편주소"          */
		field.add("OTH_ADDR"            ,100     ,"C");  /*"납세자 상세주소"          */
		field.add("MO_TEL"              ,16      ,"C");  /*"납세자 핸드폰번호"        */
		field.add("TEL"                 ,16      ,"C");  /*"납세자 전화번호"          */
		field.add("BIZ_NO"              ,10      ,"C");  /*"납세자 사업자번호"        */
		field.add("CMP_NM"              ,50      ,"C");  /*"상호명"                   */
		field.add("RVSN_YY"             ,4       ,"C");  /*"귀속년도"                 */
		field.add("DUE_DT"              ,8       ,"C");  /*"납기일자"                 */
		field.add("REQ_DIV"             ,2       ,"C");  /*"신고구분"                 */
		field.add("RTN_INC_DT"          ,8       ,"C");  /*"양도소득일자"             */
		field.add("RTN_ZIP_NO"          ,6       ,"C");  /*"양도물건지 우편번호"      */
		field.add("RTN_ADDR"            ,100     ,"C");  /*"양도물건지 주소"          */
		field.add("RTNTX"               ,14      ,"H");  /*"양도소득세"               */
		field.add("RSTX_RTN"            ,14      ,"H");  /*"주민세 양도소득세할"      */
		field.add("RADTX"               ,14      ,"H");  /*"신고불성실 가산세"        */
		field.add("PADTX"               ,14      ,"H");  /*"납부불성실 가산세"        */
		field.add("TAX_RT"              ,4       ,"H");  /*"세율"                     */
		field.add("ADTX_YN"             ,1       ,"C");  /*"가산세유무"               */
		field.add("DLQ_CNT"             ,14      ,"H");  /*"납부지연일수"             */
		field.add("TOT_ADTX"            ,14      ,"H");  /*"가산세 합계"              */
		field.add("TOT_AMT"             ,14      ,"H");  /*"총납부세액"               */
		field.add("F_DUE_DT"            ,8       ,"C");  /*"당초납기"                 */
		field.add("PAY_YN"              ,1       ,"C");  /*"수납여부(수기수납처리시)" */
		field.add("BRC_NO"              ,7       ,"C");  /*"은행코드"                 */
		field.add("PAY_DT"              ,8       ,"C");  /*"수납일자"                 */
		field.add("TRS_DT"              ,8       ,"C");  /*"이체일자"                 */
		field.add("ACC_DT"              ,8       ,"C");  /*"회계일자"                 */
		field.add("MASTER_BANK_COD"     ,3       ,"C");  /*"회계일자"                 */

		
		len = field.getFieldListLen();

		this.dataMap = new MapForm();
        
	}
	
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
