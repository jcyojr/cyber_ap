/**
 *  �ֽý��۸� : ���̹����漼û ��ȭ
 *  ��  ��  �� : ����
 *  ��  ��  �� : ��û�� ���ܼ��� �Ǻ� �����ڷ� ����
 *               ���̹���û ���������������� ���� �ΰ��� �ǿ� ���ؼ�
 *               ���� ���θ� ��û�ϴ� ��� �Ǻ��� ��ó�ϱ� ����...
 *  Ŭ����  ID : Txdm2560FieldList
 *  ����  �̷� :
 * ------------------------------------------------------------------------
 *  �ۼ���     �Ҽ�  		 ����      Tag   ����
 * ------------------------------------------------------------------------   
 * �ҵ���   ��ä��(��)  2011.08.06     %01%  �ű��ۼ�
 *  
 */
package com.uc.bs.cyber.daemon.txdm2560;

import com.uc.egtob.net.FieldList;
import com.uc.core.MapForm;
/**
 * @author Administrator
 *
 */
public class Txdm2560FieldList {
			
	private MapForm dataMap = null;
	
	private FieldList field ;
	
	private int     len =  0;
	
	/**
	 * ������
	 * ���ý� ȸ���������� ����
	 * ����) ������������
	 */
	public Txdm2560FieldList() {
		// TODO Auto-generated constructor stub
		
		field = new FieldList();

		field.add("LEN"      ,      4,    "H");    //1 ����
		field.add("SGG_COD"  ,      3,    "C");    //2 ��û�ڵ�
		field.add("REG_NO"   ,     13,    "C");    //3 �ֹι�ȣ
		field.add("TAX_SNO"  ,      6,    "C");    //4 ������ȣ
		field.add("DIL_GB"   ,      1,    "C");    //5 ü������

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
