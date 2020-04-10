/**
 *  �ֽý��۸� : ���̹����漼û ��ȭ
 *  ��  ��  �� : ���� ���� �ۼ��� ����
 *  ��  ��  �� : ���ý� ���������� �м��ϰ� ������ �б⸦ ���� Class
 *  Ŭ����  ID : ChkFieldList
 *  ����  �̷� :
 * ------------------------------------------------------------------------
 *  �ۼ���      	�Ҽ�  		    ����            Tag           ����
 * ------------------------------------------------------------------------
 *  Administrator   u c         2011.04.24         %01%         �����ۼ�
 */

package com.uc.bs.cyber.etax.net;

import com.uc.core.MapForm;
import com.uc.egtob.net.FieldList;
import com.uc.bs.cyber.CbUtil;

//verify
public class ChkFieldList {

	private MapForm dataMap = null;
	
	private FieldList field ;
	
	private int     len =  0;
	
	/**
	 * ������
	 */
	public ChkFieldList() {

		field = new FieldList();
		
		field.add("LENGTH"	    		, 	6  , "H");
		field.add("CO_TRAN"	    		, 	6  , "H");
		
		len = field.getFieldListLen();

		this.dataMap = new MapForm();
	}
	

	/**
	 * ���� ������ �۽��� ��� ����
	 * @param cmd      : 2:���, 3:������
	 * @param sbCmd    : 1:First, 2:Middle, 3:Last
	 * @param srcId    : ������� ���� ID
	 * @param destId   : ������ ��� ID
	 * @param buffSize : �����ͺ� ����
	 * @return
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
				// return null;
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
