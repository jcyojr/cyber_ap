/**
 *  주시스템명 : 사이버지방세청 고도화
 *  업  무  명 : 수기고지서 수납자료 등록, 소인파일 파싱용
 *  기  능  명 : 수기고지서 주민세 법인세할 소인파일을 파싱하기 위한 필드정의
 *              
 *  클래스  ID : Txdm1274FieldList
 *  변경  이력 :
 * ------------------------------------------------------------------------
 *  작성자     소속  		 일자      Tag   내용
 * ------------------------------------------------------------------------   
 * 송동욱   유채널(주)  2011.10.17     %01%  신규작성
 *  
 */
package com.uc.bs.cyber.daemon.txdm1274;

import com.uc.core.MapForm;
import com.uc.egtob.net.FieldList;
/**
 * @author Administrator
 *
 */
public class Txdm1274FieldList {
	
	
	private MapForm dataMap = null;
	
	private FieldList field ;
	
	private int     len =  0;
	
	/**
	 * 생성자
	 * 소인파일 파싱용 전문작성
	 */
	public Txdm1274FieldList() {
		// TODO Auto-generated constructor stub
		
		field = new FieldList();
		
		/*
		 * 주민세 법인세할 소인파일 포멧정보
		 * */	
		field.add("SIDO_COD"           ,2       ,"C");  /*"시도코드"                   */
		field.add("SGG_COD"            ,3       ,"C");  /*"시군구코드"                 */
		field.add("ACCT_COD"           ,2       ,"C");  /*"시군구코드"                 */
		field.add("TAX_ITEM"           ,6       ,"C");  /*"과세년월"                   */
		field.add("TAX_YYMM"           ,6       ,"C");  /*"과세년월"                   */
		field.add("TAX_DIV"            ,1       ,"C");  /*"과세구분"                   */
		field.add("HACD"               ,3       ,"C");  /*"과세행정동"                 */
		field.add("TAX_SNO"            ,6       ,"C");  /*"신고일자"                   */
		field.add("TAX_DT"             ,8       ,"C");  /*"신고일자"                   */
		field.add("REG_NO"             ,13      ,"C");  /*"납세자 주민/법인번호"       */
		field.add("REG_NM"             ,30      ,"C");  /*"납세자 성명"                */
		field.add("TPR_COD"            ,2       ,"C");  /*"법인코드"                   */
		field.add("REG_BUCD"           ,5       ,"C");  /*"사업장 법정동"              */
		field.add("BIZ_ZIP_NO"         ,6       ,"C");  /*"사업장 우편번호"            */
		field.add("BIZ_ZIP_ADDR"       ,60      ,"C");  /*"사업장 우편주소"            */
		field.add("BIZ_ADDR"           ,100     ,"C");  /*"사업장 상세주소"            */
		field.add("MO_TEL"             ,16      ,"C");  /*"핸드폰번호"                 */
		field.add("BIZ_TEL"            ,16      ,"C");  /*"사업장 전화번호"            */
		field.add("BIZ_NO"             ,10      ,"C");  /*"사업자번호"                 */
		field.add("CMP_NM"             ,50      ,"C");  /*"상호명"                     */
		field.add("CMPTX_KD"           ,1       ,"C");  /*"법인세신고종류"             */
		field.add("REQ_KD_DT"          ,8       ,"C");  /*"신고종류별 일자"            */
		field.add("DUE_DT"             ,8       ,"C");  /*"납기일자"                   */
		field.add("REQ_DIV"            ,1       ,"C");  /*"신고구분"                   */
		field.add("RVSN_S_DT"          ,8       ,"C");  /*"귀속사업기간 시작년월일"    */
		field.add("RVSN_E_DT"          ,8       ,"C");  /*"귀속사업기간 종료년월일"    */
		field.add("TOT_EMP_CNT"        ,14      ,"H");  /*"법인전체 종업원수"          */
		field.add("TOT_B_AREA"         ,14      ,"H");  /*"법인전체 건축물 연면적"     */
		field.add("IN_EMP_CNT"         ,14      ,"H");  /*"시군구내 종업원수"          */
		field.add("IN_B_ADRE"          ,14      ,"H");  /*"시군구내 건축물 연면적"     */
		field.add("TOT_CMPTX"          ,14      ,"H");  /*"법인세 총액"                */
		field.add("PDIV_RT"            ,7       ,"H");  /*"안분비율"                   */
		field.add("CMPTX"              ,14      ,"H");  /*"법인세액"                   */
		field.add("RSTX_CMP"           ,14      ,"H");  /*"주민세 법인세할"            */
		field.add("RADTX"              ,14      ,"H");  /*"신고불성실 가산세"          */
		field.add("PADTX"              ,14      ,"H");  /*"납부불성실 가산세"          */
		field.add("TAX_RT"             ,4       ,"H");  /*"세율"                       */
		field.add("ADTX_YN"            ,1       ,"C");  /*"가산세유무"                 */
		field.add("DLQ_CNT"            ,14      ,"H");  /*"납부지연일수"               */
		field.add("TOT_ADTX"           ,14      ,"H");  /*"가산세 합계"                */
		field.add("TOT_AMT"            ,14      ,"H");  /*"총납부세액"                 */
		field.add("F_DUE_DT"           ,8       ,"C");  /*"당초납기"                   */
		field.add("PAY_YN"             ,1       ,"C");  /*"수납여부(수기수납처리시)"   */
		field.add("BANK_COD"           ,7       ,"C");  /*영업점 지로코드(수납기관코드)*/
		field.add("PAY_DT"             ,8       ,"C");  /*"수납일자"                   */
		field.add("TRS_DT"             ,8       ,"C");  /*"이체일자"                   */
		field.add("ACC_DT"             ,8       ,"C");  /*"회계일자"                   */
		field.add("TOT_RSTX"           ,14      ,"H");  /*"주민세총액"                 */
		field.add("MASTER_BANK_COD"    ,3       ,"C");  /*"회계일자"                   */

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
