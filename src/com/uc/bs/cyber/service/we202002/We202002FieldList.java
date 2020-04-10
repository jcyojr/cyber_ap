/**
 *  �ֽý��۸� : ���̹����漼û ��ȭ
 *  ��  ��  �� : ���� ���� �ۼ��� ����
 *  ��  ��  �� : ���ý�-���̹���û ���� �ۼ��� ���
 *               �ǽð��Ѱ����ΰ������
 *  Ŭ����  ID : We202002FieldList
 *  ����  �̷� :
 * ------------------------------------------------------------------------
 *  �ۼ���     �Ҽ�  		 ����      Tag   ����
 * ------------------------------------------------------------------------   
 * ��â��   ��ä��(��)  2014.01.02     %01%  �ű��ۼ�
 *  
 */
package com.uc.bs.cyber.service.we202002;

import com.uc.egtob.net.FieldList;
import com.uc.bs.cyber.CbUtil;
import com.uc.core.MapForm;

/**
 * @author Administrator
 *
 */
public class We202002FieldList{

	private MapForm dataMap = null;
	
	private FieldList field ;
	
	private int     len =  0;
	
	/**
	 * ������
	 * �ǽð��Ѱ����ΰ������ ��������
	 * ����) ������������
	 */
	public We202002FieldList() {
		// TODO Auto-generated constructor stub
		field = new FieldList();
		
		field.add("LENGTH"	    	, 	6  , "H");  /*��������                             */
		field.add("CO_TRAN"	    	, 	6  , "H");  /*�ŷ� ���� �ڵ�                       */
		field.add("CO_DIV"	     	,   3  , "C");  /*���� ����                            */
		field.add("CO_DT"     		,  12  , "H");  /*���� �Ͻ�                            */
		field.add("CO_BKNUM"	    ,  12  , "C");  /*����/���� ���� ���� ��ȣ             */
		field.add("CO_AGNUM"    	,  12  , "C");  /*�̿���/���� ���� ���� ��ȣ         */
		field.add("GIRO_COD"        , 	2  , "H");  /*�̿��� ������ �з��ڵ�           */
		field.add("GIRO_NO"        	,   7  , "H");  /*�̿��� ���� ��ȣ                   */
		field.add("REGNO"          	,  13  , "C");  /*�����ǹ��� �ֹ�(�����,����)��Ϲ�ȣ */
		field.add("TOTPAY_NO"       ,  19  , "C");  /*�Ѱ����� ������ȣ                    */
		field.add("TOT_CNT"        	,   6  , "H");  /*�Ѱ����� �� ���� �Ǽ�                */
		field.add("TOT_SUM_B_AMT"   ,  15  , "H");  /*�Ѱ����� �� ���� �ݾ�                */
		field.add("PAY_DT"        	,   8  , "H");  /*���� ����                            */
		field.add("BANK_COD"        ,   7  , "C");  /*������� �� �� �ڵ�                  */
		field.add("PAY_REG_NO"      ,  13  , "C");  /*������ �ֹ�(�����)��Ϲ�ȣ          */
		field.add("PAY_SYSTEM"      ,   1  , "C");  /*���� �̿� �ý���                     */
		field.add("PAY_FSYSTEM"     ,   1  , "C");  /*�� ���� �̿� �ý���                  */
		field.add("PAY_FORM"        ,   1  , "C");  /*���� ���� ����                       */
		field.add("CH_INCLUDE"      ,   1  , "C");  /*��ǥ ���� ����                       */
		field.add("CARD_REQ_NO"     ,  12  , "C");  /*�ſ�ī�� ���ι�ȣ                    */
		field.add("CARD_MMS"        ,   2  , "C");  /*�ſ�ī�� �Һ� ���� ��                */
		
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
