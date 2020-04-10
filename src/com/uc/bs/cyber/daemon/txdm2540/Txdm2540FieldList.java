/**
 *  �ֽý��۸� : ���̹����漼û ��ȭ
 *  ��  ��  �� : ���ý� ����
 *  ��  ��  �� : ���ý�-���̹���û ���� �ۼ��� ���
 *               ���ý� ȸ���������� ����
 *  Ŭ����  ID : Txdm2540FieldList
 *  ����  �̷� :
 * ------------------------------------------------------------------------
 *  �ۼ���     �Ҽ�  		 ����      Tag   ����
 * ------------------------------------------------------------------------   
 * �ҵ���   ��ä��(��)  2011.06.26     %01%  �ű��ۼ�
 *  
 */
package com.uc.bs.cyber.daemon.txdm2540;

import com.uc.egtob.net.FieldList;
import com.uc.core.MapForm;
/**
 * @author Administrator
 *
 */
public class Txdm2540FieldList {
			
	private MapForm dataMap = null;
	
	private FieldList field ;
	
	private int     len =  0;
	
	/**
	 * ������
	 * ���ý� ȸ���������� ����
	 * ����) ������������
	 */
	public Txdm2540FieldList() {
		// TODO Auto-generated constructor stub
		
		field = new FieldList();

		field.add("usr_div"  ,     32,    "C");    //1 ����ڱ��� [ ������(����,���λ����):01 ||||||||�ܱ���(����,���λ����):02||||||||����:21 ]
		field.add("usr_nm"   ,     80,    "C");    //2 ����/��ǥ��
		field.add("tpr_no"   ,     16,    "C");    //3 �ֹε�Ϲ�ȣ/���ι�ȣ
		field.add("bz_no"    ,     16,    "C");    //4 ����ڵ�Ϲ�ȣ
		field.add("zip_no"   ,      6,    "C");    //5 �����ȣ
		
		len = field.getFieldListLen();

		this.dataMap = new MapForm();

	}
	
	public void Txdm2540_Crp_FieldList() {
		// TODO Auto-generated constructor stub
		
		field = new FieldList();

		field.add("usr_div"  ,     32,    "C");    //1 ����ڱ��� [ ������(����,���λ����):01 ||||||||�ܱ���(����,���λ����):02||||||||����:21 ]
		field.add("usr_nm"   ,     80,    "C");    //2 ����/��ǥ��
		field.add("tpr_no"   ,     16,    "C");    //3 �ֹε�Ϲ�ȣ/���ι�ȣ
		field.add("bz_no"    ,     16,    "C");    //4 ����ڵ�Ϲ�ȣ
		field.add("zip_no"   ,      6,    "C");    //5 �����ȣ
		field.add("addr1"    ,    128,    "C");    //6 �ּ�
		field.add("addr2"    ,    128,    "C");    //7 ���ּ�
		field.add("sido_cod" ,      2,    "C");    //8 �õ��ڵ�
		field.add("sgg_cod"  ,      3,    "C");    //9 �ñ����ڵ�
		field.add("email"    ,    100,    "C");    //10 �̸����ּ�
		field.add("email_yn" ,      1,    "C");    //11 ���ϸ�����
		field.add("tel_no"   ,    128,    "C");    //12 ��ȭ��ȣ
		field.add("mo_tel"   ,    128,    "C");    //13 ����Ϲ�ȣ
		field.add("dn"       ,    256,    "C");    //14 ����������
		field.add("crp_nm"   ,     80,    "C");    //15 ���θ�
		field.add("remrk"    ,    512,    "C");    //16 ���
		field.add("comp_nm"  ,    100,    "C");    //17 ȸ���
		field.add("tpr_no2"  ,     16,    "C");    //18 ��ǥ���ֹι�ȣ
		
		len = field.getFieldListLen();

		this.dataMap = new MapForm();

	}
	
	public void Txdm2540_Crp_short_FieldList() {
		// TODO Auto-generated constructor stub
		
		field = new FieldList();

		field.add("usr_div"  ,     32,    "C");    //1 ����ڱ��� [ ������(����,���λ����):01 ||||||||�ܱ���(����,���λ����):02||||||||����:21 ]
		field.add("usr_nm"   ,     80,    "C");    //2 ����/��ǥ��
		field.add("tpr_no"   ,     16,    "C");    //3 �ֹε�Ϲ�ȣ/���ι�ȣ
		field.add("bz_no"    ,     16,    "C");    //4 ����ڵ�Ϲ�ȣ
		field.add("zip_no"   ,      6,    "C");    //5 �����ȣ
		field.add("addr1"    ,    128,    "C");    //6 �ּ�
		field.add("addr2"    ,    128,    "C");    //7 ���ּ�
		field.add("sido_cod" ,      2,    "C");    //8 �õ��ڵ�
		field.add("sgg_cod"  ,      3,    "C");    //9 �ñ����ڵ�
		field.add("email"    ,    100,    "C");    //10 �̸����ּ�
		field.add("email_yn" ,      1,    "C");    //11 ���ϸ�����
		field.add("tel_no"   ,    128,    "C");    //12 ��ȭ��ȣ
		field.add("mo_tel"   ,    128,    "C");    //13 ����Ϲ�ȣ
		field.add("dn"       ,    256,    "C");    //14 ����������
		field.add("crp_nm"   ,     80,    "C");    //15 ���θ�
		field.add("remrk"    ,    512,    "C");    //16 ���
		field.add("comp_nm"  ,    29,     "C");    //17 ȸ���

		
		len = field.getFieldListLen();

		this.dataMap = new MapForm();

	}	
	
	public void Txdm2540_Pri_FieldList() {
		// TODO Auto-generated constructor stub
		
		field = new FieldList();

		field.add("usr_div"  ,     32,    "C");    //1 ����ڱ��� [ ������(����,���λ����):01 ||||||||�ܱ���(����,���λ����):02||||||||����:21 ]
		field.add("usr_nm"   ,     80,    "C");    //2 ����/��ǥ��
		field.add("tpr_no"   ,     16,    "C");    //3 �ֹε�Ϲ�ȣ/���ι�ȣ
		field.add("bz_no"    ,     16,    "C");    //4 ����ڵ�Ϲ�ȣ
		field.add("zip_no"   ,      6,    "C");    //5 �����ȣ
		field.add("addr1"    ,    128,    "C");    //6 �ּ�
		field.add("addr2"    ,    128,    "C");    //7 ���ּ�
		field.add("sido_cod" ,      2,    "C");    //8 �õ��ڵ�
		field.add("sgg_cod"  ,      3,    "C");    //9 �ñ����ڵ�
		field.add("email"    ,    100,    "C");    //10 �̸����ּ�
		field.add("email_yn" ,      1,    "C");    //11 ���ϸ�����
		field.add("tel_no"   ,    128,    "C");    //12 ��ȭ��ȣ
		field.add("mo_tel"   ,    128,    "C");    //13 ����Ϲ�ȣ
		field.add("dn"       ,    256,    "C");    //14 ����������
		field.add("crp_nm"   ,     80,    "C");    //15 ���θ�
		

		
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
