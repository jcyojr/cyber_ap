/**
 *  �ֽý��۸� : ���̹����漼û ��ȭ
 *  ��  ��  �� : ����
 *  ��  ��  �� : ���̹���û �λ����࿬�� ���� ��������
 *  Ŭ����  ID : Comd_WorkBsField
 *  ����  �̷� :
 * ------------------------------------------------------------------------
 *  �ۼ���      	�Ҽ�  		    ����            Tag           ����
 * ------------------------------------------------------------------------
 *  ����ȯ       ��ä��(��)      2015.01.19         %01%         �����ۼ�
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
	 * ������
	 */
	public Comd_WorkBsField() {

		headField = new FieldList();
		
		headField.add("LEN"               , 4  ,  "H"); //��������
		headField.add("TRAN_CODE"         , 9  ,  "C"); //�ĺ��ڵ� - TRAN CODE(CJVJGRDT)
		headField.add("BIZ_CODE"          , 8  ,  "C"); //��ü�ڵ� - ����ο� ��ü�ڵ�
		headField.add("BNK_CODE"          , 2  ,  "H"); //�����ڵ� - 32
		headField.add("TX_GUBUN"          , 4  ,  "C"); //�������� 
		headField.add("PROGRAM_ID"        , 3  ,  "H"); //��������
		headField.add("TRAN_CNT"          , 1  ,  "H"); //�۽�Ƚ��
		headField.add("TRAN_NO"           , 6  ,  "H"); //������ȣ
		headField.add("TRAN_DT"           , 6  ,  "C"); //��������
		headField.add("TRAN_TM"           , 6  ,  "C"); //���۽ð�
		headField.add("RS_CODE"           , 4  ,  "C"); //�����ڵ�
		headField.add("FILLER"            , 45 ,  "C"); //����
		headField.add("TRAN_B_NO"         , 6  ,  "H"); //��������ȣ	
		
		sendField = new FieldList(); 
		recvField = new FieldList(); 
		repetField = new FieldList();
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
	
	/* �������� �ʵ�����
	 * �ۼ��������� ������ ���
	 * @param seField :: ������ �ʵ�
	 * */
	@SuppressWarnings("unchecked")
    public void addRecvFieldAll(FieldList seField) {
		
		if(recvField == null) recvField = new FieldList();
		
		recvField.addAll(seField);
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
	 * @param recvBuff
	 * @return
	 * @throws Exception 
	 */
	public MapForm parseHeadBuffer(byte[] recvBuff) throws Exception {
		
		return headField.parseMessage(recvBuff, 0);
	}
	

    /**
     * �۽���������(�Ϲ�)
     * @param headMap
     * @param dataMap
     * @return
     */
	public byte[] makeSendBuffer(MapForm headMap, MapForm dataMap) {
		
		return makeSendBuffer(headMap, dataMap, 0);
	}
	
	/**
	 * �۽���������(�ݺ�)
	 * @param headMap
	 * @param dataMap
	 * @param addLen
	 * @return
	 */
	public byte[] makeSendBuffer(MapForm headMap, MapForm dataMap, int addLen) {
	
		if(dataMap == null) dataMap = new MapForm();
		
		byte[] retBuff = new byte[headField.getFieldListLen() + (sendField == null?0:sendField.getFieldListLen())];
		
		/*��������� �ڵ�ȭ �� �� �ִ� ���� ���⼭ ����*/
		headMap.setMap("LEN"     , Integer.toString(retBuff.length + addLen - 4));  //��������
		
		headMap.setMap("TX_DATE" , CbUtil.getCurrent("yyMMddHHmmss"));     //�ð�����
		
		byte[] headBuff =  headField.makeMessageByte(headMap);
		
		if(sendField == null) return headBuff;	// dataField �� null �̸� headField�� �����ϵ���
		
		byte[] dataBuff =  sendField.makeMessageByte(dataMap);
		
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
	public MapForm parseRecvBuffer(byte[] recvBuff) throws Exception {
		
		MapForm recvMap = recvField.parseMessage(recvBuff, headField.getFieldListLen());
				
		return recvMap;
		
	}
	
	
	/**
	 * �۽����� Parsing
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
	 * �ݺ� ���� �۽����� ����
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
	 * ��������� �������� Return
	 * @return
	 */
	public int getHeadLen() {
	
		return headField.getFieldListLen();
	}
	
}
