/**
 *  주시스템명 : 사이버지방세청 고도화
 *  업  무  명 : 수기고지서 수납자료 등록, 소인파일 파싱용
 *  기  능  명 : 수기고지서 특별징수 소인파일을 파싱하기 위한 필드정의
 *              
 *  클래스  ID : Txdm1271FieldList
 *  변경  이력 :
 * ------------------------------------------------------------------------
 *  작성자     소속  		 일자      Tag   내용
 * ------------------------------------------------------------------------   
 * 송동욱   유채널(주)  2011.10.17     %01%  신규작성
 *  
 */
package com.uc.bs.cyber.daemon.txdm1271;

import com.uc.core.MapForm;
import com.uc.egtob.net.FieldList;
/**
 * @author Administrator
 *
 */
public class Txdm1271FieldList {
	
	
	private MapForm dataMap = null;
	
	private FieldList field ;
	
	private int     len =  0;
	
	/**
	 * 생성자
	 * 소인파일 파싱용 전문작성
	 */
	public Txdm1271FieldList() {
		// TODO Auto-generated constructor stub
		
		field = new FieldList();
		
		/*
		 * 특별징수 소인파일 포멧정보
		 * */	
		field.add("SIDO_COD"           ,2         ,"C");   /*시도코드                                    */ 
		field.add("SGG_COD"            ,3         ,"C");   /*시군구코드                                  */ 
		field.add("ACCT_COD"           ,2         ,"C");   /*회계코드                                    */ 
		field.add("TAX_ITEM"           ,6         ,"C");   /*과세코드                                    */ 
		field.add("TAX_YYMM"           ,6         ,"C");   /*과세년월                                    */ 
		field.add("TAX_DIV"            ,1         ,"C");   /*과세구분                                    */ 
		field.add("HACD"               ,3         ,"C");   /*과세행정동                                  */ 
		field.add("TAX_SNO"            ,6         ,"C");   /*과세번호                                    */ 
		field.add("TAX_DT"             ,8         ,"C");   /*신고일자                                    */ 
		field.add("REG_NO"             ,13        ,"C");   /*납세자 주민/법인번호                        */ 
		field.add("REG_NM"             ,30        ,"C");   /*납세자 성명                                 */ 
		field.add("TPR_COD"            ,2         ,"C");   /*법인코드                                    */ 
		field.add("REG_BUCD"           ,5         ,"C");   /*사업장 법정동                               */ 
		field.add("ZIP_NO"             ,6         ,"C");   /*사업장 우편번호                             */ 
		field.add("BIZ_ZIP_ADDR"       ,60        ,"C");   /*사업장 우편주소                             */ 
		field.add("BIZ_ADDR"           ,100       ,"C");   /*사업장 상세주소                             */ 
		field.add("MO_TEL"             ,16        ,"C");   /*핸드폰번호                                  */ 
		field.add("BIZ_TEL"            ,16        ,"C");   /*사업장 전화번호                             */ 
		field.add("SAUP_NO"            ,10        ,"C");   /*사업자번호                                  */ 
		field.add("CMP_NM"             ,50        ,"C");   /*상호명                                      */ 
		field.add("REQ_DIV"            ,1         ,"C");   /*신고구분                                    */ 
		field.add("RVSN_YYMM"          ,6         ,"C");   /*귀속년월                                    */ 
		field.add("SUP_YYMM"           ,6         ,"C");   /*지급년월                                    */ 
		field.add("DUE_DT"             ,8         ,"C");   /*납기일자                                    */ 
		field.add("YY_TRTN"            ,14        ,"H");   /*연말정산 환급총액                           */ 
		field.add("YY_MRTN"            ,14        ,"H");   /*당월조정 환급액                             */ 
		field.add("YY_RRTN"            ,14        ,"H");   /*연말정산 환급잔액                           */ 
		field.add("OUT_TAMT"           ,14        ,"H");   /*중도퇴사 차감총액                           */ 
		field.add("OUT_MAMT"           ,14        ,"H");   /*중도퇴사 당월조정액                         */ 
		field.add("OUT_RAMT"           ,14        ,"H");   /*중도퇴사 차감잔액                           */ 
		field.add("EMP_CNT_1"          ,14        ,"H");   /*인원_갑종근로소득                           */ 
		field.add("INCOMTAX_1"         ,14        ,"H");   /*(신고),소득세액_갑종근로소득                */ 
		field.add("RSTX_1"             ,14        ,"H");   /*(신고),주민세_갑종근로소득                  */ 
		field.add("ADTX_1"             ,14        ,"H");   /*(신고),가산세_갑종근로소득                  */ 
		field.add("EMP_CNT_2"          ,14        ,"H");   /*인원_이자소득                               */ 
		field.add("INCOMTAX_2"         ,14        ,"H");   /*(신고),소득세액_이자소득                    */ 
		field.add("RSTX_2"             ,14        ,"H");   /*(신고),주민세_이자소득                      */ 
		field.add("ADTX_2"             ,14        ,"H");   /*(신고),가산세_이자소득                      */ 
		field.add("EMP_CNT_3"          ,14        ,"H");   /*인원_배당소득                               */ 
		field.add("INCOMTAX_3"         ,14        ,"H");   /*(신고),소득세액_배당소득                    */ 
		field.add("RSTX_3"             ,14        ,"H");   /*(신고),주민세_배당소득                      */ 
		field.add("ADTX_3"             ,14        ,"H");   /*(신고),가산세_배당소득                      */ 
		field.add("EMP_CNT_4"          ,14        ,"H");   /*인원_자유직업소득(사업소득)                 */ 
		field.add("INCOMTAX_4"         ,14        ,"H");   /*(신고),소득세액_자유직업소득(사업소득)      */
		field.add("RSTX_4"             ,14        ,"H");   /*(신고),주민세_자유직업소득(사업소득)        */ 
		field.add("ADTX_4"             ,14        ,"H");   /*(신고),가산세_자유직업소득(사업소득)        */ 
		field.add("EMP_CNT_5"          ,14        ,"H");   /*인원_갑종퇴직소득                           */ 
		field.add("INCOMTAX_5"         ,14        ,"H");   /*(신고),소득세액_갑종퇴직소득                */ 
		field.add("RSTX_5"             ,14        ,"H");   /*(신고),주민세_갑종퇴직소득                  */ 
		field.add("ADTX_5"             ,14        ,"H");   /*(신고),가산세_갑종퇴직소득                  */ 
		field.add("EMP_CNT_6"          ,14        ,"H");   /*인원_기타소득                               */ 
		field.add("INCOMTAX_6"         ,14        ,"H");   /*(신고),소득세액_기타소득                    */ 
		field.add("RSTX_6"             ,14        ,"H");   /*(신고),주민세_기타소득                      */ 
		field.add("ADTX_6"             ,14        ,"H");   /*(신고),가산세_기타소득                      */ 
		field.add("EMP_CNT_7"          ,14        ,"H");   /*인원_법인세법 98조...                       */ 
		field.add("INCOMTAX_7"         ,14        ,"H");   /*(신고),소득세액_법인세법 98조...            */ 
		field.add("RSTX_7"             ,14        ,"H");   /*(신고),주민세_법인세법 98조...              */ 
		field.add("ADTX_7"             ,14        ,"H");   /*(신고),가산세_법인세법 98조...              */ 
		field.add("EMP_CNT_8"          ,14        ,"H");   /*인원_외국인으로부터 받은소득                */ 
		field.add("INCOMTAX_8"         ,14        ,"H");   /*(신고),소득세액_외국인으로부터 받은소득     */
		field.add("RSTX_8"             ,14        ,"H");   /*(신고),주민세_외국인으로부터 받은소득       */ 
		field.add("ADTX_8"             ,14        ,"H");   /*(신고),가산세_외국인으로부터 받은소득       */ 
		field.add("EMP_CNT_9"          ,14        ,"H");   /*인원_소득세법 제119조 양도소득              */ 
		field.add("INCOMTAX_9"         ,14        ,"H");   /*(신고),소득세액_소득세법 제119조 양도소득   */
		field.add("RSTX_9"             ,14        ,"H");   /*(신고),주민세_소득세법 제119조 양도소득     */ 
		field.add("ADTX_9"             ,14        ,"H");   /*(신고),가산세_소득세법 제119조 양도소득     */ 
		field.add("INCOMTAX_10"        ,14        ,"H");   /*(신고),소득세액_합계                        */ 
		field.add("RSTX_10"            ,14        ,"H");   /*(신고),주민세_합계                          */ 
		field.add("ADTX_10"            ,14        ,"H");   /*(신고),가산세_합계_신고불성실 가산세와 동일 */
		field.add("MM_RTN"             ,14        ,"H");   /*당월조정환급세액합계                        */ 
		field.add("PAY_RSTX"           ,14        ,"H");   /*(납부),주민세                               */ 
		field.add("PAY_ADTX"           ,14        ,"H");   /*(납부),가산세                               */ 
		field.add("TAX_RT"             ,4         ,"C");   /*세율                                        */ 
		field.add("ADTX_YN"            ,1         ,"C");   /*가산세유무                                  */ 
		field.add("TOT_ADTX"           ,14        ,"H");   /*가산세 합계                                 */ 
		field.add("TOT_AMT"            ,14        ,"H");   /*총납부세액                                  */ 
		field.add("F_DUE_DT"           ,8         ,"C");   /*당초납기                                    */ 
		field.add("EMP_CNT_11"         ,14        ,"H");   /*인원_연금소득                               */ 
		field.add("INCOMTAX_11"        ,14        ,"H");   /*소득세_연금소득                             */ 
		field.add("ADTX_11"            ,14        ,"H");   /*주민세_연금소득                             */ 
		field.add("RDT_RSTX"           ,14        ,"H");   /*가산세_연금소득                             */ 
		field.add("RDT_ADTX"           ,14        ,"H");   /*조정액은 가산세 부터 차감함                 */ 
		field.add("RSTX_11"            ,14        ,"H");   /*조정액은 가산세 부터 차감함                 */ 
		field.add("PAY_YN"             ,1         ,"C");   /*수납여부(수기수납처리시)                    */ 
		field.add("BRC_NO"             ,7         ,"C");   /*수납기관코드                                */ 
		field.add("PAY_DT"             ,8         ,"C");   /*수납일자                                    */ 
		field.add("TRS_DT"             ,8         ,"C");   /*이체일자                                    */ 
		field.add("ACC_DT"             ,8         ,"C");   /*회계일자                                    */ 
		field.add("MASTER_BANK_COD"    ,3         ,"C");   /*은행코드                                    */ 
		
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
