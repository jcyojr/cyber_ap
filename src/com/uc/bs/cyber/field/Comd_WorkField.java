/**
 *  주시스템명 : 사이버지방세청 고도화
 *  업  무  명 : 공통
 *  기  능  명 : 부산은행 대외계연계 업무 공통전문
 *  클래스  ID : Comd_WorkField
 *  변경  이력 :
 * ------------------------------------------------------------------------
 *  작성자      	소속  		    일자            Tag           내용
 * ------------------------------------------------------------------------
 *  김대완       유채널(주)      2010.12.22         %01%         최초작성
 *
 */

package com.uc.bs.cyber.field;

import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.uc.core.MapForm;
import com.uc.egtob.net.FieldList;

public class Comd_WorkField {

	
	protected FieldList headField;
	
	protected FieldList sendField;
	
	protected FieldList recvField;
	
	protected FieldList repetField;

	private MapForm headMap;
	
	private Log log = LogFactory.getLog(this.getClass());
	
	/**
	 * 생성자
	 */
	public Comd_WorkField() {

		headField = new FieldList();
		headField.add("TX_KIND" 		,    2,    "C");    // 자료구분 (00:default, 01:header, 03:trailer, 09:취소)
		headField.add("USER_ID" 		,   20,    "C");    // 사용자ID
		headField.add("UNIQ_NO" 		,   15,    "C");    // 업무고유번호
		headField.add("PROC_SEQ"		,   3 ,    "H");    // 재처리회차
		headField.add("TOTAL_CNT"		,   10,    "H");    // 전체데이터건수
		headField.add("PAGE_NO"			,   10,    "H");    // 전문페이지번호
		headField.add("SKIP_CNT"		,   10,    "H");    // 생략건수
		headField.add("CURR_CNT"		,   10,    "H");    // 데이터건수
		headField.add("ST_SEQ"			,   10,    "H");    // 시작일련번호
		headField.add("ED_SEQ"			,   10,    "H");    // 종료일련번호
		
		sendField = new FieldList(); recvField = new FieldList(); repetField = new FieldList();
	}


	/**
	 * 송신전문 필드 정의
	 * @param name :: I/O정의서 내의 자료ID
	 * @param size :: 필드 길이
	 * @param type :: Char='C', Number='H', Byte='B'  
	 */
	public void addSendField(String name, float size, String type) {
		
		if(sendField == null) sendField = new FieldList();
		sendField.add(name, size, type);
	}
	
	
	/**
	 * SEND 필드의 항목을 RECV필드에 복사
	 */
	@SuppressWarnings("unchecked")
	public void sendToRecv() {
		
		if(recvField == null) recvField = new FieldList();
		
		recvField.addAll(sendField);
	}
	
	/**
	 * 수신전문 필드정의
	 * @param name :: I/O정의서 내의 자료ID
	 * @param size :: 필드 길이
	 * @param type :: Char='C', Number='H', Byte='B'
	 */
	public void addRecvField(String name, float size, String type) {
		
		if(recvField == null) recvField = new FieldList();
		recvField.add(name, size, type);
	}
	
	/**
	 * 반복전문 필드정의
	 * @param name
	 * @param size
	 * @param type
	 */
	public void addRepetField(String name, float size, String type) {
		
		if(repetField == null) repetField = new FieldList();
		repetField.add(name, size, type);
	}
	
	/**
	 * 
	 * @param recvBuff
	 * @return
	 * @throws Exception 
	 */
	public MapForm parseHeadBuffer(byte[] recvBuff) throws Exception {
		
		return headField.parseMessage(recvBuff, 0);
	}
	
	
	/**
	 * 송신전문 생성
	 * @param dataMap
	 * @return
	 */
	public byte[] makeSendBuffer(MapForm dataMap)  {
	
		if(dataMap == null) dataMap = new MapForm();
		
		byte[] retBuff = new byte[headField.getFieldListLen() + (sendField == null?0:sendField.getFieldListLen())];
		
		if(dataMap.getMap("TX_KIND") == null) dataMap.setMap("TX_KIND", "00");
		
		byte[] headBuff =  headField.makeMessageByte(dataMap);
		
		if(sendField == null) return headBuff;	// dataField 가 null 이면 headField만 생성하도록
		
		byte[] dataBuff =  sendField.makeMessageByte(dataMap);
		
		System.arraycopy(headBuff, 0, retBuff, 0, headBuff.length);
		
		System.arraycopy(dataBuff, 0, retBuff, headBuff.length, dataBuff.length);
		
		return retBuff;
	}

	
	/**
	 * 수신(응답)전문 생성 :: 전문을 변환해서 전달해야 하는경우 사용해라
	 * @param dataMap
	 * @return
	 */
	public byte[] makeRecvBuffer(MapForm dataMap)  {
	
		if(dataMap == null) dataMap = new MapForm();
		
		byte[] retBuff = new byte[headField.getFieldListLen() + (recvField==null?0:recvField.getFieldListLen())];
		
		if(dataMap.getMap("TX_KIND") == null) dataMap.setMap("TX_KIND", "00");
		byte[] headBuff =  headField.makeMessageByte(dataMap);
		
		if(recvField == null) return headBuff;	// dataField 가 null 이면 headField만 생성하도록
		
		byte[] dataBuff =  recvField.makeMessageByte(dataMap);
		
		System.arraycopy(headBuff, 0, retBuff, 0, headBuff.length);
		
		System.arraycopy(dataBuff, 0, retBuff, headBuff.length, dataBuff.length);
		
		return retBuff;
	}
	
	
	/**
	 * 수신전문 Parsing
	 * @param recvBuff
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public MapForm parseRecvBuffer(byte[] recvBuff) throws Exception {
		
		headMap = headField.parseMessage(recvBuff, 0); 
		MapForm recvMap = recvField.parseMessage(recvBuff, headField.getFieldListLen());
		
		recvMap.putAll(headMap);
		
		return recvMap;
		
	}
	
	
	/**
	 * 송신전문 Parsing
	 * @param recvBuff
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public MapForm parseSendBuffer(byte[] recvBuff) throws Exception {
		
		headMap = headField.parseMessage(recvBuff, 0); 
		
		log.debug("FIELD SIZE==" + (headField.getFieldListLen() + sendField.getFieldListLen()) + ", RECV SIZE=" + recvBuff.length);
		MapForm sendMap = sendField.parseMessage(recvBuff, headField.getFieldListLen());
		
		sendMap.putAll(headMap);
		
		return sendMap;
		
	}
	
	
	/**
	 * 반복 포함 송신전문 생성
	 * @param dataMap
	 * @param reptList
	 * @return
	 * @throws Exception 
	 */
	public byte[] makeSendReptBuffer(MapForm dataMap, ArrayList<?> reptList) throws Exception {
		
		int reptSize = reptList.size() * repetField.getFieldListLen();
		
		byte[] retBuff = new byte[headField.getFieldListLen() + sendField.getFieldListLen() + reptSize];
		
		byte[] dataBuff = makeSendBuffer(dataMap);
		
		byte[] reptBuff = repetField.makeRepeatedMessage(reptList);
		
		System.arraycopy(dataBuff, 0, retBuff, 0, dataBuff.length);
		
		System.arraycopy(reptBuff, 0, retBuff, dataBuff.length, reptBuff.length);
		
		return retBuff;
	}
	

	/**
	 * 반복 포함 수신전문 생성
	 * @param dataMap
	 * @param reptList
	 * @return
	 * @throws Exception 
	 */
	public byte[] makeRecvReptBuffer(MapForm dataMap, ArrayList<?> reptList) throws Exception {
		
		int reptSize = reptList.size() * repetField.getFieldListLen();
		
		byte[] retBuff = new byte[headField.getFieldListLen() + recvField.getFieldListLen() + reptSize];
		
		byte[] dataBuff = makeRecvBuffer(dataMap);
		
		byte[] reptBuff = repetField.makeRepeatedMessage(reptList);
		
		System.arraycopy(dataBuff, 0, retBuff, 0, dataBuff.length);
		
		System.arraycopy(reptBuff, 0, retBuff, dataBuff.length, reptBuff.length);
		
		return retBuff;
	}
	
	
	/**
	 * 반복부 포함하는 송신전문 Parsing
	 * @param recvBuff
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public MapForm parseSendReptBuffer(byte[] recvBuff) throws Exception {
		
		MapForm retMap = this.parseSendBuffer(recvBuff);
		
		int reptPos   =  headField.getFieldListLen() + sendField.getFieldListLen();

		log.debug("RECV COUNT==" + headMap.getMap("CURR_CNT"));
		
		ArrayList<MapForm> retList = repetField.parseRepeatedMessage(recvBuff, reptPos, Integer.parseInt(headMap.getMap("CURR_CNT").toString()));
		
		retMap.setMap("repetList", retList);
		
		return retMap;
	}
	

	/**
	 * 반복부만 있는 전문 Parsing
	 * @param recvBuff
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<MapForm> parseReptBuffer(byte[] recvBuff) throws Exception {
		
		headMap = this.parseHeadBuffer(recvBuff);
		
		int reptPos   =  headField.getFieldListLen() ;

		log.debug("RECV COUNT==" + headMap.getMap("CURR_CNT"));
		
		return repetField.parseRepeatedMessage(recvBuff, reptPos, Integer.parseInt(headMap.getMap("CURR_CNT").toString()));
		
	}

	/**
	 * 반복부만 있는 전문 Parsing
	 * @param recvBuff
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<MapForm> parseReptBuffer(byte[] recvBuff, int pos) throws Exception {
		
		return repetField.parseRepeatedMessage(recvBuff, pos);
		
	}	
	
	/**
	 * 반복부만 있는 전문 Parsing
	 * @param recvBuff
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<MapForm> parseReptBuffer(byte[] recvBuff, int pos, int cnt) throws Exception {
		

		return repetField.parseRepeatedMessage(recvBuff, pos, cnt);
		
	}	
	
	/**
	 * 반복부 포함하는 수신전문 Parsing
	 * @param recvBuff
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public MapForm parseRecvReptBuffer(byte[] recvBuff) throws Exception {
		
		MapForm retMap = this.parseRecvBuffer(recvBuff);
		
		int reptPos   =  headField.getFieldListLen() + recvField.getFieldListLen();

		ArrayList<MapForm> retList = repetField.parseRepeatedMessage(recvBuff, reptPos, Integer.parseInt(headMap.getMap("CURR_CNT").toString()));
		
		retMap.setMap("repetList", retList);
		
		return retMap;
	}

	
	/**
	 * @param headMap the headMap to set
	 */
	public void setHeadMap(MapForm headMap) {
		this.headMap = headMap;
	}

	/**
	 * @return the headMap
	 */
	public MapForm getHeadMap() {
		return headMap;
	}

	/**
	 * 업무공통부 전문길이 Return
	 * @return
	 */
	public int getHeadLen() {
	
		return headField.getFieldListLen();
	}
	
}
