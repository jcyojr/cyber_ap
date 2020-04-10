/**
 *  �ֽý��۸� : ���̹����漼û ����e���� ����
 *  ��  ��  �� : ���� ���� �ۼ��� ����
 *  ��  ��  �� : ���ý�-���̹���û ���� �ۼ��� ���
 *               �ǽð����ΰ������
 *  Ŭ����  ID : we202001FieldList
 *  ����  �̷� :
 * ------------------------------------------------------------------------
 *  �ۼ���     �Ҽ�  		 ����      Tag   ����
 * ------------------------------------------------------------------------   
 * �ҵ���   ��ä��(��)  2011.04.24     %01%  �ű��ۼ�
 *  
 */
package com.uc.bs.cyber.service.we202001;

import com.uc.egtob.net.FieldList;
import com.uc.bs.cyber.CbUtil;
import com.uc.core.MapForm;
/**
 * @author Administrator
 *
 */
public class We202001FieldList {
			
	private MapForm dataMap = null;
	
	private FieldList field ;
	
	private int     len =  0;
	
	/**
	 * ������
	 * �ǽð����ΰ������ ��������
	 * ����) ������������
	 */
	public We202001FieldList() {
		// TODO Auto-generated constructor stub
		
		field = new FieldList();

		field.add("LENGTH"	    	, 	6  , "H");  /*��������                                    */
		field.add("CO_TRAN"	    	, 	6  , "H");  /*�ŷ� ���� �ڵ�                              */
		field.add("CO_DT"     		,  12  , "H");  /*���� �Ͻ�                                   */
		field.add("CO_BKNUM"	    ,  12  , "C");  /*����/���� ���� ���� ��ȣ                    */
		field.add("CO_AGNUM"    	,  12  , "C");  /*�̿���/���� ���� ���� ��ȣ                */
		field.add("CO_AGCOD"        , 	2  , "H");  /*�̿��� ������ �з��ڵ�                  */
		field.add("CO_AGENUM"       ,   7  , "H");  /*�̿��� ���� ��ȣ                          */
		field.add("IH_REGNO"        ,  13  , "C");  /*�����ǹ��� �ֹ�(�����,����)��Ϲ�ȣ        */
		field.add("EPAY_NO"         ,  19  , "C");  /*���ڳ��ι�ȣ                                */
		field.add("SUM_RCP"        	,  15  , "H");  /*���� �ݾ�                                   */
		field.add("PAY_DT"        	,   8  , "C");  /*���� ����                                   */
		field.add("BANK_COD"        ,   7  , "C");  /*������� �� �� �ڵ� / ī��� �ڵ�           */
		field.add("PAY_REG_NO"      ,  13  , "C");  /*������ �ֹ�(�����)��Ϲ�ȣ                 */
		field.add("PAY_SYSTEM"      ,   1  , "C");  /*���� �̿� �ý���                            */
		field.add("PAY_FORM"        ,   1  , "C");  /*���� ���� ����                              */
		field.add("CARD_REQ_NO"     ,  12  , "C");  /*�ſ�ī�� ���ι�ȣ                           */
		field.add("CARD_MMS"        ,   2  , "C");  /*�ſ�ī�� �Һ� ���� ��                       */
		
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
	 * �����Ľ�...
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
