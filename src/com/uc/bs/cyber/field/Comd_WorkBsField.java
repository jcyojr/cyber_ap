/**
 *  주시스템명 : 사이버지방세청 고도화
 *  업  무  명 : 공통
 *  기  능  명 : 사이버세청 부산은행연계 업무 공통전문
 *  클래스  ID : Comd_WorkBsField
 *  변경  이력 :
 * ------------------------------------------------------------------------
 *  작성자      	소속  		    일자            Tag           내용
 * ------------------------------------------------------------------------
 *  최유환       유채널(주)      2015.01.19         %01%         최초작성
 */

package com.uc.bs.cyber.field;

import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.uc.bs.cyber.CbUtil;
import com.uc.core.MapForm;
import com.uc.egtob.net.FieldList;

public class Comd_WorkBsField {

	
	protected FieldList headField;
	
	protected FieldList sendField;
	
	protected FieldList recvField;
	
	protected FieldList repetField;

	private MapForm headMap;
	
	private Log log = LogFactory.getLog(this.getClass());
	
	/**
	 * 생성자
	 */
	public Comd_WorkBsField() {

		headField = new FieldList();
		
		headField.add("LEN"               , 4  ,  "H"); //전문길이
		headField.add("TRAN_CODE"         , 9  ,  "C"); //식별코드 - TRAN CODE(CJVJGRDT)
		headField.add("BIZ_CODE"          , 8  ,  "C"); //업체코드 - 은행부여 업체코드
		headField.add("BNK_CODE"          , 2  ,  "H"); //은행코드 - 32
		headField.add("TX_GUBUN"          , 4  ,  "C"); //전문구분 
		headField.add("PROGRAM_ID"        , 3  ,  "H"); //업무구분
		headField.add("TRAN_CNT"          , 1  ,  "H"); //송신횟수
		headField.add("TRAN_NO"           , 6  ,  "H"); //전문번호
		headField.add("TRAN_DT"           , 6  ,  "C"); //전송일자
		headField.add("TRAN_TM"           , 6  ,  "C"); //전송시각
		headField.add("RS_CODE"           , 4  ,  "C"); //응답코드
		headField.add("FILLER"            , 45 ,  "C"); //공란
		headField.add("TRAN_B_NO"         , 6  ,  "H"); //원전문번호	
		
		sendField = new FieldList(); 
		recvField = new FieldList(); 
		repetField = new FieldList();
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
	
	/* 수신전문 필드정의
	 * 송수신전문이 동일한 경우
	 * @param seField :: 복사할 필드
	 * */
	@SuppressWarnings("unchecked")
    public void addRecvFieldAll(FieldList seField) {
		
		if(recvField == null) recvField = new FieldList();
		
		recvField.addAll(seField);
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
	 * @param recvBuff
	 * @return
	 * @throws Exception 
	 */
	public MapForm parseHeadBuffer(byte[] recvBuff) throws Exception {
		
		return headField.parseMessage(recvBuff, 0);
	}
	

    /**
     * 송신전문생성(일반)
     * @param headMap
     * @param dataMap
     * @return
     */
	public byte[] makeSendBuffer(MapForm headMap, MapForm dataMap) {
		
		return makeSendBuffer(headMap, dataMap, 0);
	}
	
	/**
	 * 송신전문생성(반복)
	 * @param headMap
	 * @param dataMap
	 * @param addLen
	 * @return
	 */
	public byte[] makeSendBuffer(MapForm headMap, MapForm dataMap, int addLen) {
	
		if(dataMap == null) dataMap = new MapForm();
		
		byte[] retBuff = new byte[headField.getFieldListLen() + (sendField == null?0:sendField.getFieldListLen())];
		
		/*헤드전문중 자동화 할 수 있는 것은 여기서 셋팅*/
		headMap.setMap("LEN"     , Integer.toString(retBuff.length + addLen - 4));  //전문길이
		
		headMap.setMap("TX_DATE" , CbUtil.getCurrent("yyMMddHHmmss"));     //시간셋팅
		
		byte[] headBuff =  headField.makeMessageByte(headMap);
		
		if(sendField == null) return headBuff;	// dataField 가 null 이면 headField만 생성하도록
		
		byte[] dataBuff =  sendField.makeMessageByte(dataMap);
		
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
	public MapForm parseRecvBuffer(byte[] recvBuff) throws Exception {
		
		MapForm recvMap = recvField.parseMessage(recvBuff, headField.getFieldListLen());
				
		return recvMap;
		
	}
	
	
	/**
	 * 송신전문 Parsing
	 * @param recvBuff
	 * @return
	 * @throws Exception
	 */
	public MapForm parseSendBuffer(byte[] recvBuff) throws Exception {
		
		log.debug("FIELD SIZE==" + (headField.getFieldListLen() + sendField.getFieldListLen()) + ", RECV SIZE=" + recvBuff.length);
		MapForm sendMap = sendField.parseMessage(recvBuff, headField.getFieldListLen());
		
		return sendMap;
		
	}
	
	
	/**
	 * 반복 포함 송신전문 생성
	 * @param dataMap
	 * @param reptList
	 * @return
	 * @throws Exception 
	 */
	public byte[] makeSendReptBuffer(MapForm headMap, MapForm dataMap, ArrayList<?> reptList) throws Exception {
		
		int reptSize = reptList.size() * repetField.getFieldListLen();
		
		byte[] retBuff = new byte[headField.getFieldListLen() + sendField.getFieldListLen() + reptSize];
				
		byte[] dataBuff = makeSendBuffer(headMap, dataMap, reptSize);
				
		byte[] reptBuff = repetField.makeRepeatedMessage(reptList);
		
		System.arraycopy(dataBuff, 0, retBuff, 0, dataBuff.length);
		
		System.arraycopy(reptBuff, 0, retBuff, dataBuff.length, reptBuff.length);
		
		return retBuff;
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
	public MapForm parseRecvReptBuffer(byte[] recvBuff, int cnt) throws Exception {
		
		MapForm retMap = this.parseRecvBuffer(recvBuff);
		
		int reptPos   =  headField.getFieldListLen() + recvField.getFieldListLen();

		ArrayList<MapForm> retList = repetField.parseRepeatedMessage(recvBuff, reptPos, cnt);
		
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
