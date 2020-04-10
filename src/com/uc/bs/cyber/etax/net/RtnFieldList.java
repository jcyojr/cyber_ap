/**
 *  주시스템명 : 사이버지방세청 고도화
 *  업  무  명 : 내부 전문 송수신 연계
 *  기  능  명 : 전문 송수신 헤더
 *               위택스 - 사이버세청 : 응답전문 처리
 *  클래스  ID : RtnFieldList
 *  변경  이력 :
 * ------------------------------------------------------------------------
 *  작성자      	소속  		    일자            Tag           내용
 * ------------------------------------------------------------------------
 *  Administrator   u c         2011.04.24         %01%         최초작성
 */

package com.uc.bs.cyber.etax.net;

import com.uc.core.MapForm;
import com.uc.egtob.net.FieldList;
import com.uc.bs.cyber.CbUtil;

public class RtnFieldList {

	private MapForm dataMap = null;
	private MapForm listMap = null;
	
	private FieldList field ;
	
	private int     len =  0;
	/**
	 * 생성자
	 * 응답전문 생성
	 */
	public RtnFieldList() {

		field = new FieldList();
		
		field.add("RSLT_COD"	    , 	9  , "C");  /*결과코드    */
		field.add("RSLT_MESSAGE"    ,  50  , "C");  /*결과 메시지 */
		
		len = field.getFieldListLen();

		this.dataMap = new MapForm();
	}
	

	/**
	 * 전문 엔진에 송신할 헤더 생성
	 * @param cmd      : 2:등록, 3:데이터
	 * @param sbCmd    : 1:First, 2:Middle, 3:Last
	 * @param srcId    : 응답받을 서버 ID
	 * @param destId   : 전송할 대상 ID
	 * @param buffSize : 데이터부 길이
	 * @return
	 */
	public byte[] getBuff(MapForm mapForm) throws Exception{
		
		dataMap = mapForm;
		
		return getBuff();
	}

	
	/**
	 * 전문 일부 셋팅 (발생시간, IP)
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
	 * Raw 전문 파싱...
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
				return null;
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
	
	
	/*
	 * 결과전문 및 오류메세지 셋팅
	 * */
	public byte[] CYB532001(String key) throws Exception{
		
		listMap = new MapForm();
		
		listMap.setMap("44100-000", "정상 수신 완료되었습니다");
		listMap.setMap("44100-101", "전자납부번호가 고지정보에 없습니다");
		listMap.setMap("44100-201", "내부 DB 시스템 장애로 처리불가합니다");
		listMap.setMap("44100-301", "데이터 길이가 정의된 값보다 큽니다");
		
		dataMap.setMap("RSLT_COD", key);
		dataMap.setMap("RSLT_MESSAGE", listMap.get(key));
		
		return this.getBuff(dataMap);
		
		
	}

	public byte[] CYB532002(String key) throws Exception{
		
		listMap = new MapForm();
		
		listMap.setMap("44110-000", "정상 수신 완료되었습니다");
		listMap.setMap("44110-101", "전자납부번호가 고지정보에 없습니다");
		listMap.setMap("44110-201", "내부 DB 시스템 장애로 처리불가합니다");
		listMap.setMap("44110-301", "데이터 길이가 정의된 값보다 큽니다");
		
		dataMap.setMap("RSLT_COD", key);
		dataMap.setMap("RSLT_MESSAGE", listMap.get(key));
		
		return this.getBuff(dataMap);
	}

	public byte[] CYB992001(String key) throws Exception{
		
		listMap = new MapForm();
		
		listMap.setMap("44120-000", "정상 수신 완료되었습니다");
		listMap.setMap("44120-101", "전자납부번호가 수납정보에 없습니다");
		listMap.setMap("44120-201", "내부 DB 시스템 장애로 처리불가합니다");
		listMap.setMap("44120-301", "데이터 길이가 정의된 값보다 큽니다");
		
		dataMap.setMap("RSLT_COD", key);
		dataMap.setMap("RSLT_MESSAGE", listMap.get(key));
		
		return this.getBuff(dataMap);
	}

}
