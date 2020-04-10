/**
 *  �ֽý��۸� : ���̹����漼û ��ȭ
 *  ��  ��  �� : ����
 *  ��  ��  �� : ���̹���û ������(����)���� ���� ��������
 *  Ŭ����  ID : Comd_WorkDfField
 *  ����  �̷� :
 * ------------------------------------------------------------------------
 *  �ۼ���      	�Ҽ�  		    ����            Tag           ����
 * ------------------------------------------------------------------------
 *  ����       ��ä��(��)      2010.12.22         %01%         �����ۼ�
 *  �۵���                           2011.06.13                      Copy
 *  ��â��       ��ä��(��)      2013.08.13                      �������������� ����
 */

package com.uc.bs.cyber.field;

import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.uc.bs.cyber.CbUtil;
import com.uc.core.MapForm;
import com.uc.egtob.net.FieldList;

public class Comd_WorkDfField {

	
	protected FieldList headField;
	
	protected FieldList sendField;
	
	protected FieldList recvField;
	
	protected FieldList repetField;

	private MapForm headMap;
	
	private Log log = LogFactory.getLog(this.getClass());
	
	/**
	 * ������
	 */
	public Comd_WorkDfField() {

		headField = new FieldList();
		
		headField.add("LEN"              , 4  ,  "H"); //��������
		headField.add("TX_ID"            , 6  ,  "C"); //�������� - SN2601:�λ��
		headField.add("TX_DATETIME"      , 14 ,  "C"); //�����Ͻ� - "YYYYMMDDhhmmss"
		headField.add("TX_GUBUN"         , 3  ,  "C"); //�������������ڵ� "030:����ȸ��û, 040:����ȸ��û ����, 050:����ó����û, 060:����ó����û ����
		headField.add("RS_FLAG"          , 2  ,  "C"); //���Ӱ�α����ڵ� - ����(DZ)
		headField.add("RESP_CODE"        , 4  ,  "C"); //����ڵ� - ��ȸ�ÿ� "0000"
		headField.add("SIDO_CODE"        , 2  ,  "C"); //�õ������ڵ� - 26:�λ��
		headField.add("BANK_CODE"        , 3  ,  "C"); //����(ī��)�ڵ�, ��ȸ�ÿ� "000"
		headField.add("BCJ_NO"           , 12 ,  "C"); //����������ȣ(���ں� ����������ȣ)
		headField.add("MEDIA_CODE"       , 3  ,  "C"); //��ü�����ڵ� - 270: ����������
		headField.add("RESERVE1"         , 17 ,  "C"); //���񿵿�
		
//		headField.add("TAX_GB"           , 1  ,  "H"  );  /*���ݱ����ڵ�(1:���漼, 2:����, 3:���ϼ���) */ 
//		headField.add("TAX_NO"           , 31 ,  "C"  );  /*������ȣ                */
//		headField.add("EPAY_NO"          , 19 ,  "C"  );  /*���ڳ��ι�ȣ            */

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

	
	/** 
	 * �������� �ʵ�����
	 * �ۼ��������� ������ ���
     * @param seField :: ������ �ʵ�
	 */
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
		
		headMap.setMap("TX_DATETIME" , CbUtil.getCurrent("yyyyMMddHHmmss"));     //�ð�����
		
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
