/**
 *  �ֽý��۸� : ���̹����漼û ��ȭ
 *  ��  ��  �� : ����
 *  ��  ��  �� : ���̹���û �ݰ������ ���� ��������
 *  Ŭ����  ID : Comd_WorkKfField
 *  ����  �̷� :
 * ------------------------------------------------------------------------
 *  �ۼ���      	�Ҽ�  		    ����            Tag           ����
 * ------------------------------------------------------------------------
 *  ����       ��ä��(��)      2010.12.22         %01%         �����ۼ�
 *  �۵���                       2011.06.13                      Copy
 */

package com.uc.bs.cyber.field;

import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.uc.bs.cyber.CbUtil;
import com.uc.core.MapForm;
import com.uc.egtob.net.FieldList;

public class Comd_WorkKfField {

	
	protected FieldList headField;
	
	protected FieldList sendField;
	
	protected FieldList recvField;
	
	protected FieldList repetField;

	private MapForm headMap;
	
	private Log log = LogFactory.getLog(this.getClass());
	
	/**
	 * ������
	 */
	public Comd_WorkKfField() {

		headField = new FieldList();
		
		headField.add("LEN"              , 4  ,  "H"); //��������
		headField.add("TX_ID"            , 3  ,  "C"); //"IGN" ���� :��������
		headField.add("BNK_CODE"         , 3  ,  "H"); //����/�����ڵ�
		headField.add("TX_GUBUN"         , 4  ,  "H"); //�������������ڵ�
		headField.add("PROGRAM_ID"       , 6  ,  "H"); //"������ �ŷ� ������ �����Ѵ�.��� ������ �ݵ�� SET�� �־�� �Ѵ�." 
		headField.add("STS_CODE"         , 3  ,  "H"); //���� Format Error �߻��� ������ �߻��� ������ �׸��ȣ
		headField.add("RS_FLAG"          , 1  ,  "C"); //��� ����(B), ����(����������)(C), ���ͳ������̿���(���̹����漼û)(G)
		headField.add("RESP_CODE"        , 3  ,  "C"); //"BLANK"
		headField.add("TX_DATE"          , 12 ,  "H"); //"YYMMDDhhmmss"
		headField.add("BCJ_NO"           , 12 ,  "C"); //[����:������������ڵ�(3) / ����:��0CT��] + ��0�� + �Ϸù�ȣ(8�ڸ�)
		headField.add("GCJ_NO"           , 12 ,  "C"); //[�̿���:��0��+�������з��ڵ�(2) / ����:��0CT��] + ��0�� + �Ϸù�ȣ(8)
		headField.add("GPUB_CODE"        , 2  ,  "H"); //89:�������漼�Ա� (�뷮���� �� �ϰ����) / 26: �λ��
		headField.add("GJIRO_NO"         , 7  ,  "H"); //"0000000"
		headField.add("FILLER"           , 2  ,  "H"); //���񿵿�		
		
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
