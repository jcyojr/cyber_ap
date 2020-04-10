/**
 *  주시스템명 : 사이버지방세청 고도화
 *  업  무  명 : 공통
 *  기  능  명 : 구청별 세외수입 건별 고지자료 연계
 *               사이버세청 관리자페이지에서 당일 부과된 건에 대해서
 *               고객이 납부를 요청하는 경우 건별로 대처하기 위함...
 *  클래스  ID : Txdm2560FieldList
 *  변경  이력 :
 * ------------------------------------------------------------------------
 *  작성자     소속  		 일자      Tag   내용
 * ------------------------------------------------------------------------   
 * 소도우   유채널(주)  2011.08.06     %01%  신규작성
 *  
 */
package com.uc.bs.cyber.daemon.txdm2560;

import com.uc.egtob.net.FieldList;
import com.uc.core.MapForm;
/**
 * @author Administrator
 *
 */
public class Txdm2560FieldList {
			
	private MapForm dataMap = null;
	
	private FieldList field ;
	
	private int     len =  0;
	
	/**
	 * 생성자
	 * 위택스 회원정보수신 전문
	 * 참고) 공통전문없음
	 */
	public Txdm2560FieldList() {
		// TODO Auto-generated constructor stub
		
		field = new FieldList();

		field.add("LEN"      ,      4,    "H");    //1 길이
		field.add("SGG_COD"  ,      3,    "C");    //2 구청코드
		field.add("REG_NO"   ,     13,    "C");    //3 주민번호
		field.add("TAX_SNO"  ,      6,    "C");    //4 과세번호
		field.add("DIL_GB"   ,      1,    "C");    //5 체납구분

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
