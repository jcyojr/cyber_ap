/**
 *  주시스템명 : 사이버지방세청 간단e납부 대응
 *  업  무  명 : 내부 전문 송수신 연계
 *  기  능  명 : 위택스-사이버세청 전문 송수신 헤더
 *               실시간납부결과통지
 *  클래스  ID : we202001FieldList
 *  변경  이력 :
 * ------------------------------------------------------------------------
 *  작성자     소속  		 일자      Tag   내용
 * ------------------------------------------------------------------------   
 * 소도우   유채널(주)  2011.04.24     %01%  신규작성
 *  
 */
package com.uc.bs.cyber.service.we202001;

import com.uc.egtob.net.FieldList;
import com.uc.bs.cyber.CbUtil;
import com.uc.core.MapForm;
/**
 * @author Administrator
 *
 */
public class We202001FieldList {
			
	private MapForm dataMap = null;
	
	private FieldList field ;
	
	private int     len =  0;
	
	/**
	 * 생성자
	 * 실시간납부결과통지 전문생성
	 * 참고) 공통전문없음
	 */
	public We202001FieldList() {
		// TODO Auto-generated constructor stub
		
		field = new FieldList();

		field.add("LENGTH"	    	, 	6  , "H");  /*전문길이                                    */
		field.add("CO_TRAN"	    	, 	6  , "H");  /*거래 구분 코드                              */
		field.add("CO_DT"     		,  12  , "H");  /*전송 일시                                   */
		field.add("CO_BKNUM"	    ,  12  , "C");  /*은행/센터 전문 관리 번호                    */
		field.add("CO_AGNUM"    	,  12  , "C");  /*이용기관/센터 전문 관리 번호                */
		field.add("CO_AGCOD"        , 	2  , "H");  /*이용기관 발행기관 분류코드                  */
		field.add("CO_AGENUM"       ,   7  , "H");  /*이용기관 지로 번호                          */
		field.add("IH_REGNO"        ,  13  , "C");  /*납부의무자 주민(사업자,법인)등록번호        */
		field.add("EPAY_NO"         ,  19  , "C");  /*전자납부번호                                */
		field.add("SUM_RCP"        	,  15  , "H");  /*납부 금액                                   */
		field.add("PAY_DT"        	,   8  , "C");  /*납부 일자                                   */
		field.add("BANK_COD"        ,   7  , "C");  /*출금은행 점 별 코드 / 카드사 코드           */
		field.add("PAY_REG_NO"      ,  13  , "C");  /*납부자 주민(사업자)등록번호                 */
		field.add("PAY_SYSTEM"      ,   1  , "C");  /*납부 이용 시스템                            */
		field.add("PAY_FORM"        ,   1  , "C");  /*납부 형태 구분                              */
		field.add("CARD_REQ_NO"     ,  12  , "C");  /*신용카드 승인번호                           */
		field.add("CARD_MMS"        ,   2  , "C");  /*신용카드 할부 개월 수                       */
		
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
		
		dataMap.setMap("DATETIME"     , CbUtil.getCurrent("yyyyMMddHHmmss"));
		dataMap.setMap("IPADDR"       , new String(java.net.InetAddress.getLocalHost().getHostAddress()));
		
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
