/**
 *  �ֽý��۸� : ���̹����漼û ��ȭ
 *  ��  ��  �� : ���� ���� �ۼ��� ����
 *  ��  ��  �� : ���� �ۼ��� ���
 *               ���ý� - ���̹���û : �������� ó��
 *  Ŭ����  ID : RtnFieldList
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

public class RtnFieldList {

	private MapForm dataMap = null;
	private MapForm listMap = null;
	
	private FieldList field ;
	
	private int     len =  0;
	/**
	 * ������
	 * �������� ����
	 */
	public RtnFieldList() {

		field = new FieldList();
		
		field.add("RSLT_COD"	    , 	9  , "C");  /*����ڵ�    */
		field.add("RSLT_MESSAGE"    ,  50  , "C");  /*��� �޽��� */
		
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
	 * ���� �Ϻ� ���� (�߻��ð�, IP)
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
	 * Raw ���� �Ľ�...
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
				return null;
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
	
	
	/*
	 * ������� �� �����޼��� ����
	 * */
	public byte[] CYB532001(String key) throws Exception{
		
		listMap = new MapForm();
		
		listMap.setMap("44100-000", "���� ���� �Ϸ�Ǿ����ϴ�");
		listMap.setMap("44100-101", "���ڳ��ι�ȣ�� ���������� �����ϴ�");
		listMap.setMap("44100-201", "���� DB �ý��� ��ַ� ó���Ұ��մϴ�");
		listMap.setMap("44100-301", "������ ���̰� ���ǵ� ������ Ů�ϴ�");
		
		dataMap.setMap("RSLT_COD", key);
		dataMap.setMap("RSLT_MESSAGE", listMap.get(key));
		
		return this.getBuff(dataMap);
		
		
	}

	public byte[] CYB532002(String key) throws Exception{
		
		listMap = new MapForm();
		
		listMap.setMap("44110-000", "���� ���� �Ϸ�Ǿ����ϴ�");
		listMap.setMap("44110-101", "���ڳ��ι�ȣ�� ���������� �����ϴ�");
		listMap.setMap("44110-201", "���� DB �ý��� ��ַ� ó���Ұ��մϴ�");
		listMap.setMap("44110-301", "������ ���̰� ���ǵ� ������ Ů�ϴ�");
		
		dataMap.setMap("RSLT_COD", key);
		dataMap.setMap("RSLT_MESSAGE", listMap.get(key));
		
		return this.getBuff(dataMap);
	}

	public byte[] CYB992001(String key) throws Exception{
		
		listMap = new MapForm();
		
		listMap.setMap("44120-000", "���� ���� �Ϸ�Ǿ����ϴ�");
		listMap.setMap("44120-101", "���ڳ��ι�ȣ�� ���������� �����ϴ�");
		listMap.setMap("44120-201", "���� DB �ý��� ��ַ� ó���Ұ��մϴ�");
		listMap.setMap("44120-301", "������ ���̰� ���ǵ� ������ Ů�ϴ�");
		
		dataMap.setMap("RSLT_COD", key);
		dataMap.setMap("RSLT_MESSAGE", listMap.get(key));
		
		return this.getBuff(dataMap);
	}

}
