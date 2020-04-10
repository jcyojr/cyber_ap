/**
 *  �ֽý��۸� : ���̹����漼û ��ȭ
 *  ��  ��  �� : ����
 *  ��  ��  �� : �λ����� ��ܰ迬�� ���� ��������
 *  Ŭ����  ID : Comd_WorkField
 *  ����  �̷� :
 * ------------------------------------------------------------------------
 *  �ۼ���      	�Ҽ�  		    ����            Tag           ����
 * ------------------------------------------------------------------------
 *  ����       ��ä��(��)      2010.12.22         %01%         �����ۼ�
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
	 * ������
	 */
	public Comd_WorkField() {

		headField = new FieldList();
		headField.add("TX_KIND" 		,    2,    "C");    // �ڷᱸ�� (00:default, 01:header, 03:trailer, 09:���)
		headField.add("USER_ID" 		,   20,    "C");    // �����ID
		headField.add("UNIQ_NO" 		,   15,    "C");    // ����������ȣ
		headField.add("PROC_SEQ"		,   3 ,    "H");    // ��ó��ȸ��
		headField.add("TOTAL_CNT"		,   10,    "H");    // ��ü�����ͰǼ�
		headField.add("PAGE_NO"			,   10,    "H");    // ������������ȣ
		headField.add("SKIP_CNT"		,   10,    "H");    // �����Ǽ�
		headField.add("CURR_CNT"		,   10,    "H");    // �����ͰǼ�
		headField.add("ST_SEQ"			,   10,    "H");    // �����Ϸù�ȣ
		headField.add("ED_SEQ"			,   10,    "H");    // �����Ϸù�ȣ
		
		sendField = new FieldList(); recvField = new FieldList(); repetField = new FieldList();
	}


	/**
	 * �۽����� �ʵ� ����
	 * @param name :: I/O���Ǽ� ���� �ڷ�ID
	 * @param size :: �ʵ� ����
	 * @param type :: Char='C', Number='H', Byte='B'  
	 */
	public void addSendField(String name, float size, String type) {
		
		if(sendField == null) sendField = new FieldList();
		sendField.add(name, size, type);
	}
	
	
	/**
	 * SEND �ʵ��� �׸��� RECV�ʵ忡 ����
	 */
	@SuppressWarnings("unchecked")
	public void sendToRecv() {
		
		if(recvField == null) recvField = new FieldList();
		
		recvField.addAll(sendField);
	}
	
	/**
	 * �������� �ʵ�����
	 * @param name :: I/O���Ǽ� ���� �ڷ�ID
	 * @param size :: �ʵ� ����
	 * @param type :: Char='C', Number='H', Byte='B'
	 */
	public void addRecvField(String name, float size, String type) {
		
		if(recvField == null) recvField = new FieldList();
		recvField.add(name, size, type);
	}
	
	/**
	 * �ݺ����� �ʵ�����
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
	 * �۽����� ����
	 * @param dataMap
	 * @return
	 */
	public byte[] makeSendBuffer(MapForm dataMap)  {
	
		if(dataMap == null) dataMap = new MapForm();
		
		byte[] retBuff = new byte[headField.getFieldListLen() + (sendField == null?0:sendField.getFieldListLen())];
		
		if(dataMap.getMap("TX_KIND") == null) dataMap.setMap("TX_KIND", "00");
		
		byte[] headBuff =  headField.makeMessageByte(dataMap);
		
		if(sendField == null) return headBuff;	// dataField �� null �̸� headField�� �����ϵ���
		
		byte[] dataBuff =  sendField.makeMessageByte(dataMap);
		
		System.arraycopy(headBuff, 0, retBuff, 0, headBuff.length);
		
		System.arraycopy(dataBuff, 0, retBuff, headBuff.length, dataBuff.length);
		
		return retBuff;
	}

	
	/**
	 * ����(����)���� ���� :: ������ ��ȯ�ؼ� �����ؾ� �ϴ°�� ����ض�
	 * @param dataMap
	 * @return
	 */
	public byte[] makeRecvBuffer(MapForm dataMap)  {
	
		if(dataMap == null) dataMap = new MapForm();
		
		byte[] retBuff = new byte[headField.getFieldListLen() + (recvField==null?0:recvField.getFieldListLen())];
		
		if(dataMap.getMap("TX_KIND") == null) dataMap.setMap("TX_KIND", "00");
		byte[] headBuff =  headField.makeMessageByte(dataMap);
		
		if(recvField == null) return headBuff;	// dataField �� null �̸� headField�� �����ϵ���
		
		byte[] dataBuff =  recvField.makeMessageByte(dataMap);
		
		System.arraycopy(headBuff, 0, retBuff, 0, headBuff.length);
		
		System.arraycopy(dataBuff, 0, retBuff, headBuff.length, dataBuff.length);
		
		return retBuff;
	}
	
	
	/**
	 * �������� Parsing
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
	 * �۽����� Parsing
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
	 * �ݺ� ���� �۽����� ����
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
	 * �ݺ� ���� �������� ����
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
	 * �ݺ��� �����ϴ� �۽����� Parsing
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
	 * �ݺ��θ� �ִ� ���� Parsing
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
	 * �ݺ��θ� �ִ� ���� Parsing
	 * @param recvBuff
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<MapForm> parseReptBuffer(byte[] recvBuff, int pos) throws Exception {
		
		return repetField.parseRepeatedMessage(recvBuff, pos);
		
	}	
	
	/**
	 * �ݺ��θ� �ִ� ���� Parsing
	 * @param recvBuff
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<MapForm> parseReptBuffer(byte[] recvBuff, int pos, int cnt) throws Exception {
		

		return repetField.parseRepeatedMessage(recvBuff, pos, cnt);
		
	}	
	
	/**
	 * �ݺ��� �����ϴ� �������� Parsing
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
	 * ��������� �������� Return
	 * @return
	 */
	public int getHeadLen() {
	
		return headField.getFieldListLen();
	}
	
}
