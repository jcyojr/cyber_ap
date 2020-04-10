/**
 *  �ֽý��۸� : ���̹����漼û ��ȭ
 *  ��  ��  �� : ����
 *  ��  ��  �� : �λ����� ��ܰ迬�� �ý��� ��������
 *  Ŭ����  ID : Comd_cmhd
 *  ����  �̷� :
 * ------------------------------------------------------------------------
 *  �ۼ���      	�Ҽ�  		    ����            Tag           ����
 * ------------------------------------------------------------------------
 *  ����       ��ä��(��)      2010.11.30         %01%         �����ۼ�
 *
 */

package com.uc.bs.cyber.field;

import com.uc.core.MapForm;
import com.uc.egtob.net.FieldList;
import com.uc.bs.cyber.CbUtil;

public class Comd_cmhd {

	private FieldList fieldList ;
	
	private int      len   = 0;
	/**
	 * ������
	 */
	public Comd_cmhd() {

		fieldList = new FieldList();
		
		fieldList.add("LENGTH",   	6,    "H");    // ��������
		fieldList.add("SYS_ID",   	4,    "C");    // ����������
		fieldList.add("SR_FLAG",   	1,    "C");    // ��û/���� ����
		fieldList.add("TR_TIME",   	14,   "C");    // �����߻��ð�
		fieldList.add("SVC_ID",   	4,    "C");    // ����ID 
		fieldList.add("MSG_ID",  	4,    "C");    // �������й�ȣ
		fieldList.add("TR_SEQ",   	10,   "H");    // �ŷ�������ȣ
		fieldList.add("RESP_CD",   	6,    "C");    // �����ڵ�
		fieldList.add("RESP_MSG",   80,   "C");    // ����޽��� 
		fieldList.add("USER_AREA",  20,   "C");    // User_Area 
		fieldList.add("FILLER",   	11,   "C");    // ���񿵿�
   
		
		len = fieldList.getFieldListLen();
	}
	

	/**
	 * �����۽� ���� ����
	 * @param svcId
	 * @param msgId
	 * @param trSeq
	 * @param procId
	 * @param buffSize
	 * @return
	 * @throws Exception
	 */
	public byte[] getHeadBuffer(String svcId, String msgId, String trSeq, String procId,  int buffSize) throws Exception{
		
		
		MapForm headMap = new MapForm();
	
		headMap.setMap("LENGTH"		,  Integer.toString(buffSize + fieldList.getFieldListLen()- 6));  // �������� 
		headMap.setMap("SYS_ID"		,  "ESCH"    );  // ���������� 
		headMap.setMap("SR_FLAG"	,  "S"       );  // ��û/���� ���� 
		headMap.setMap("TR_TIME"	,  CbUtil.getCurrent("yyyyMMddHHmmss") );  // �����߻��ð� 
		headMap.setMap("SVC_ID"		,  svcId    );  // ����ID 
		headMap.setMap("MSG_ID"		,  msgId    );  // �޽���ID 
		headMap.setMap("TR_SEQ"		,  trSeq    );  // �ŷ�������ȣ 
		headMap.setMap("RESP_CD"	,  "000000");	// �����ڵ� 
		headMap.setMap("RESP_MSG"	,  " "); 		// ����޽���
		headMap.setMap("USER_AREA"	,  procId);  	// �������� 
		headMap.setMap("FILLER"		,  " "); 		// ���񿵿�

		
		return fieldList.makeMessageByte(headMap);
		
	}
	
	/**
	 * �����۽� ���� ����
	 * @param svcId
	 * @param msgId
	 * @param trSeq
	 * @param procId
	 * @param buffSize
	 * @return
	 * @throws Exception
	 */
	public byte[] getHeadBuffer(String svcId, String msgId, String trSeq, String procId,  int buffSize, String sys_id) throws Exception{
		
		
		MapForm headMap = new MapForm();
	
		headMap.setMap("LENGTH"		,  Integer.toString(buffSize + fieldList.getFieldListLen()- 6));  // �������� 
		headMap.setMap("SYS_ID"		,  sys_id    );  // ���������� 
		headMap.setMap("SR_FLAG"	,  "S"       );  // ��û/���� ���� 
		headMap.setMap("TR_TIME"	,  CbUtil.getCurrent("yyyyMMddHHmmss") );  // �����߻��ð� 
		headMap.setMap("SVC_ID"		,  svcId    );  // ����ID 
		headMap.setMap("MSG_ID"		,  msgId    );  // �޽���ID 
		headMap.setMap("TR_SEQ"		,  trSeq    );  // �ŷ�������ȣ 
		headMap.setMap("RESP_CD"	,  "000000");	// �����ڵ� 
		headMap.setMap("RESP_MSG"	,  " "); 		// ����޽���
		headMap.setMap("USER_AREA"	,  procId);  	// �������� 
		headMap.setMap("FILLER"		,  " "); 		// ���񿵿�

		
		return fieldList.makeMessageByte(headMap);
		
	}
	
	/**
	 * @param svcId :: ���� ����ID
	 * @param msgId :: ���� �޽���ID
	 * @param trSeq :: �����Ϸù�ȣ 
	 * @param procId :: ������� ���μ���ID (����ڿ���)
	 * @param sendBuff :: �۽� ����
	 * @return
	 * @throws Exception
	 */
	public byte[] getBuffer(String svcId, String msgId, String trSeq, String procId,  byte[] buff) throws Exception{
		
		byte[] headBuff = getHeadBuffer(svcId, msgId, trSeq, procId, buff.length);
		
		byte[] retBuff = new byte[getLen() + buff.length];
		
		System.arraycopy(headBuff, 0, retBuff, 0, headBuff.length);

		System.arraycopy(buff, 0, retBuff, headBuff.length, buff.length);
		
		return retBuff;

	}

	
	/**
	 * @param svcId :: ���� ����ID
	 * @param msgId :: ���� �޽���ID
	 * @param trSeq :: �����Ϸù�ȣ 
	 * @param procId :: ������� ���μ���ID (����ڿ���)
	 * @param sendBuff :: �۽� ����
	 * @return
	 * @throws Exception
	 */
	public byte[] getBuffer(String svcId, String msgId, String trSeq, String procId,  byte[] buff, String sys_id) throws Exception{
		
		byte[] headBuff = getHeadBuffer(svcId, msgId, trSeq, procId, buff.length, sys_id);
		
		byte[] retBuff = new byte[getLen() + buff.length];
		
		System.arraycopy(headBuff, 0, retBuff, 0, headBuff.length);

		System.arraycopy(buff, 0, retBuff, headBuff.length, buff.length);
		
		return retBuff;

	}
	
	/**
	 * �������� ����
	 * @param headMap  :: �ý��۰��� Header Map
	 * @param resCd    :: �����ڵ�
	 * @param resMsg   :: �����޽���
	 * @param resBuff  :: ������ ����
	 * @param buffLen  :: ������ ���� ����
	 * @return
	 * @throws Exception 
	 */
	public byte[] getResBuffer(MapForm headMap, String resCd, String resMsg, byte[] resBuff, int buffLen) throws Exception {
		
		int realLen = buffLen>resBuff.length?resBuff.length:buffLen;
		
		byte[] retBuff = new byte[fieldList.getFieldListLen() + realLen];
		
		/**
		 * 2011.02.07 -- ���� --
		 * �����ܿ��� �Ǽ��� �������� ������ ���ǿ� ���������� ������ ó���ϵ��� ����
		 */
		if(!headMap.getMap("SYS_ID").equals("JBBK") || !headMap.getMap("SR_FLAG").equals("S")) {
			
			throw new Exception("(" + headMap.getMap("SYS_ID") + "_" + headMap.getMap("SR_FLAG") + ") �� ���� ���������� ���� �� �����ϴ�");
		}
		
		headMap.setMap("LENGTH"		    , Integer.toString(realLen + fieldList.getFieldListLen()- 6));  // �������� Length 6�� ���ش� 
		headMap.setMap("SR_FLAG"		, "R"       );  // ��û/���� ���� 
		headMap.setMap("TR_TIME"		, CbUtil.getCurrent("yyyyMMddHHmmss") );  // �����߻��ð�  
		headMap.setMap("RESP_CD"		, resCd);  // �����ڵ� 
		headMap.setMap("RESP_MSG"		, resMsg); 		// ����޽���

		byte[] headBuff =  fieldList.makeMessageByte(headMap);
		
		System.arraycopy(headBuff, 0, retBuff, 0, headBuff.length);
		
		System.arraycopy(resBuff, 0, retBuff, headBuff.length, realLen);
		
		return retBuff;
	}	
	
	/**
	 * ����� ��������
	 * @param headMap
	 * @param resCd
	 * @param resBuff
	 * @return
	 * @throws Exception 
	 */
	public byte[] getResBuffer(MapForm headMap, String resCd, String resMsg, byte[] resBuff) throws Exception {
		
		return getResBuffer(headMap, resCd, resMsg, resBuff, resBuff.length);
	}
	
	/**
	 * �������۸� Parsing �Ͽ� MapForm�� �����
	 * @param buffer 
	 * @return
	 * @throws Exception
	 */
	public MapForm parseBuffer(byte[] buffer) throws Exception{
		

			MapForm headMap = null;

			try {
				headMap = fieldList.parseMessage(buffer, 0);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}

		return headMap;
	}


	/**
	 * 
	 * @param buffer
	 * @param position
	 * @return
	 * @throws Exception
	 */
	public MapForm parseBuffer(byte[] buffer, int position) throws Exception{
		

		MapForm headMap = null;

		try {
			headMap = fieldList.parseMessage(buffer, position);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

		return headMap;
	}	
	
	/**
	 * 
	 * @return
	 */
	public int getLen() {
		// TODO Auto-generated method stub
		return this.len;
		
	}


	/**
	 * 
	 * @return
	 */
	public FieldList getFieldList() {
		// TODO Auto-generated method stub
		return this.fieldList;
	}
	
		
}
