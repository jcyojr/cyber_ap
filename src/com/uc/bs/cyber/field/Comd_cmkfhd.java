/**
 *  �ֽý��۸� : ���̹����漼û ��ȭ
 *  ��  ��  �� : ����
 *  ��  ��  �� : ���̹� - ������ ��������
 *  Ŭ����  ID : Comd_cmkfhd (������)
 *  ����  �̷� :
 * ------------------------------------------------------------------------
 *  �ۼ���      	�Ҽ�  		    ����            Tag           ����
 * ------------------------------------------------------------------------
 *  �۵���       ��ä��(��)      2011.06.09         %01%         �����ۼ�
 *
 */

package com.uc.bs.cyber.field;

import com.uc.core.MapForm;
import com.uc.egtob.net.FieldList;
import com.uc.bs.cyber.CbUtil;
/**
 * @author Administrator
 *
 */
public class Comd_cmkfhd {
	
	private FieldList fieldList ;
	
	private int      len   = 0;
	/**
	 * ������ : �������� ���� : �Ľ��ϱ� ����...
	 */
	public Comd_cmkfhd() {
		// TODO Auto-generated constructor stub
		
		/*�������� ��*/
		fieldList = new FieldList();
				
		fieldList.add("LEN"              , 4  ,  "H"); //��������
		fieldList.add("TX_ID"            , 3  ,  "C"); //"IGN" ���� :��������
		fieldList.add("BNK_CODE"         , 3  ,  "H"); //����/�����ڵ�
		fieldList.add("TX_GUBUN"         , 4  ,  "H"); //�������������ڵ�
		fieldList.add("PROGRAM_ID"       , 6  ,  "H"); //"������ �ŷ� ������ �����Ѵ�.��� ������ �ݵ�� SET�� �־�� �Ѵ�." 
		fieldList.add("STS_CODE"         , 3  ,  "H"); //���� Format Error �߻��� ������ �߻��� ������ �׸��ȣ
		fieldList.add("RS_FLAG"          , 1  ,  "C"); //��� ����(B), ����(C), ���ͳ������̿���(G)
		fieldList.add("RESP_CODE"        , 3  ,  "C"); //"BLANK"
		fieldList.add("TX_DATE"          , 12 ,  "H"); //"YYMMDDhhmmss"
		fieldList.add("BCJ_NO"           , 12 ,  "C"); //[����:������������ڵ�(3) / ����:��0CT��] + ��0�� + �Ϸù�ȣ(8�ڸ�)
		fieldList.add("GCJ_NO"           , 12 ,  "C"); //[�̿���:��0��+�������з��ڵ�(2) / ����:��0CT��] + ��0�� + �Ϸù�ȣ(8)
		fieldList.add("GPUB_CODE"        , 2  ,  "H"); //89:�������漼�Ա� (�뷮���� �� �ϰ����)
		fieldList.add("GJIRO_NO"         , 7  ,  "H"); //"0000000"
		fieldList.add("FILLER"           , 2  ,  "H"); //���񿵿�
		
		len = fieldList.getFieldListLen();
		
	}

	/**
	 * �����۽� ���� ���� : �۽Ž� ���������� �����Ѵ�.
	 * @param taxgb
	 * @param giroId
	 * @param trSeq
	 * @param procId
	 * @param sts_cd
	 * @param buffSize
	 * @return
	 * @throws Exception
	 */
	public byte[] getHeadBuffer(String taxgb, String giroId, String trRes, String procId, String sts_cd, String bcj_no, String gcj_no, int buffSize) throws Exception{
		
		MapForm headMap = new MapForm();
		                                                              
		headMap.setMap("LEN"              , Integer.toString(buffSize + fieldList.getFieldListLen()- 4));  //��������
		headMap.setMap("TX_ID"            , "IGN"      );  //"IGN" ����                                                                
		headMap.setMap("BNK_CODE"         , "099"      );  //3 (����ü�� "099")                                                                          
		headMap.setMap("TX_GUBUN"         , taxgb      );  //4  
		headMap.setMap("STS_CODE"         , sts_cd     );  //3  ���� Format Error �߻��� ������ �߻��� ������ �׸��ȣ        
		headMap.setMap("PROGRAM_ID"       , procId     );  //6  ������ �ŷ� ������ �����Ѵ�.��� ������ �ݵ�� SET�� �־�� �Ѵ�.  
		headMap.setMap("RS_FLAG"          , "G"        );  //1  ��� ����(B), ����(C), ���ͳ������̿���(G)                              
		headMap.setMap("RESP_CODE"        , trRes      );  //3  BLANK                                                                   
		headMap.setMap("TX_DATE"          , CbUtil.getCurrent("yyMMddHHmmss"));  //12 "YYMMDDhhmmss"                                                            
		headMap.setMap("BCJ_NO"           , bcj_no     );  //12 [����:������������ڵ�(3) / ����:��0CT��] + ��0�� + �Ϸù�ȣ(8�ڸ�)       
		headMap.setMap("GCJ_NO"           , gcj_no     );  //12 [�̿���:��0��+�������з��ڵ�(2) / ����:��0CT��] + ��0�� + �Ϸù�ȣ(8) 
		headMap.setMap("GPUB_CODE"        , "26"       );  //2  89:�������漼�Ա� (�뷮���� �� �ϰ����)                                  
		headMap.setMap("GJIRO_NO"         , "1000685"  );  //7 "0000000"                                                                 
		headMap.setMap("FILLER"           , "00"       );  //2 ���񿵿�  

		return fieldList.makeMessageByte(headMap);
		
	}
		
	/**
	 * @param taxgb :: ���� ����
	 * @param giroId :: ���� �̿���
	 * @param trSeq :: �����Ϸù�ȣ 
	 * @param procId :: ������� ���μ���ID (����ڿ���)
	 * @param sts_cd ::
	 * @param sendBuff :: �۽� ����
	 * @return
	 * @throws Exception
	 */
	public byte[] getBuffer(String taxgb, String giroId, String trSeq, String procId, String sts_cd, String bcj_no, String gcj_no, byte[] buff) throws Exception{
		
		byte[] headBuff = getHeadBuffer(taxgb, giroId, trSeq, procId, sts_cd, bcj_no, gcj_no, buff.length);
		
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
		
		headMap.setMap("LEN"		    , Integer.toString(realLen + fieldList.getFieldListLen()- 4));  // �������� Length 4�� ���ش� 
		headMap.setMap("TX_DATE"		, CbUtil.getCurrent("yyyyMMddHHmmss") );                        // �����߻��ð�  
		headMap.setMap("RESP_CODE"		, resCd);                                                       // �����ڵ� 

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
