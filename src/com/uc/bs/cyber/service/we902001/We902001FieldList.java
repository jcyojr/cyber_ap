/**
 *  �ֽý��۸� : ���̹����漼û ����e���� ����
 *  ��  ��  �� : ���� ���� �ۼ��� ����
 *  ��  ��  �� : ���ý�-���̹���û ���� �ۼ��� ���
 *               �ǽð������������
 *  Ŭ����  ID : We902001FieldList
 *  ����  �̷� :
 * ------------------------------------------------------------------------
 *  �ۼ���     �Ҽ�  		 ����      Tag   ����
 * ------------------------------------------------------------------------   
 * ��â��   ��ä��(��)  2013.11.24     %01%  �ű��ۼ�
 *  
 */
package com.uc.bs.cyber.service.we902001;

import com.uc.core.MapForm;
import com.uc.egtob.net.FieldList;
import com.uc.bs.cyber.CbUtil;

/**
 * @author Administrator
 *
 */
public class We902001FieldList {
	
	private MapForm dataMap = null;
	
	private FieldList field ;
	
	private int     len =  0;
	/**
	 * ������
	 * �ǽð������������ ��������
	 * ����) ������������
	 */
	public We902001FieldList() {
		// TODO Auto-generated constructor stub
		field = new FieldList();
		
		field.add("LENGTH"	    	, 	6  , "H");  /*��������*/
		field.add("CO_TRAN"	    	, 	6  , "H");  /*�ŷ� ���� �ڵ�                                         */
		field.add("CO_DIV"	     	,   3  , "C");  /*���� ����                                              */
		field.add("CO_DT"     		,  12  , "H");  /*���� �Ͻ�                                              */
		field.add("CO_BKNUM"	    ,  12  , "C");  /*����/���� ���� ���� ��ȣ                               */
		field.add("CO_AGNUM"    	,  12  , "C");  /*�̿���/���� ���� ���� ��ȣ                           */
		field.add("GIRO_COD"        , 	2  , "H");  /*�̿��� ������ �з��ڵ�                             */
		field.add("GIRO_NO"        	,   7  , "H");  /*�̿��� ���� ��ȣ                                     */
		field.add("BANK_COD"        ,   7  , "C");  /*������� ���� �ڵ� / ī��� �ڵ�                       */
		field.add("PAY_REG_NO"      ,  13  , "C");  /*������ �ֹ�(�����)��Ϲ�ȣ                            */
		field.add("O_AGNUM"         ,  12  , "C");  /*���ŷ� ��� ���� ���� ���� ��ȣ /���ŷ� �ŷ� ���� ��ȣ */
		field.add("O_DT"          	,  12  , "H");  /*���ŷ� ��� ���� ���� ���� �Ͻ�                        */
		field.add("SUM_RCP"        	,  15  , "H");  /*���ŷ� ���� �ݾ�                                       */
		field.add("CAN_RE"       	,   1  , "C");  /*��� ����                                              */
		field.add("PAY_FORM"        ,   1  , "C");  /*���ŷ� ���� ���� ����                                  */
		
		len = field.getFieldListLen();

		this.dataMap = new MapForm();
	}

	/**
	 * ���� ������ �۽��� ��� ����
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
		
		dataMap.setMap("DATETIME"     , CbUtil.getCurrent("yyyyMMddHHmmss"));
		dataMap.setMap("IPADDR"       , new String(java.net.InetAddress.getLocalHost().getHostAddress()));
		
		headBuf =  field.makeMessageByte(dataMap);
		
		return headBuf;		
		
	}
	
	/**
	 * 
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
