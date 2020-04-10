/**
 *  �ֽý��۸� : ���̹����漼û ��ȭ
 *  ��  ��  �� : ��������� �����ڷ� ���, �������� �Ľ̿�
 *  ��  ��  �� : ��������� �ֹμ� ���μ��� ���������� �Ľ��ϱ� ���� �ʵ�����
 *              
 *  Ŭ����  ID : Txdm1274FieldList
 *  ����  �̷� :
 * ------------------------------------------------------------------------
 *  �ۼ���     �Ҽ�  		 ����      Tag   ����
 * ------------------------------------------------------------------------   
 * �۵���   ��ä��(��)  2011.10.17     %01%  �ű��ۼ�
 *  
 */
package com.uc.bs.cyber.daemon.txdm1274;

import com.uc.core.MapForm;
import com.uc.egtob.net.FieldList;
/**
 * @author Administrator
 *
 */
public class Txdm1274FieldList {
	
	
	private MapForm dataMap = null;
	
	private FieldList field ;
	
	private int     len =  0;
	
	/**
	 * ������
	 * �������� �Ľ̿� �����ۼ�
	 */
	public Txdm1274FieldList() {
		// TODO Auto-generated constructor stub
		
		field = new FieldList();
		
		/*
		 * �ֹμ� ���μ��� �������� ��������
		 * */	
		field.add("SIDO_COD"           ,2       ,"C");  /*"�õ��ڵ�"                   */
		field.add("SGG_COD"            ,3       ,"C");  /*"�ñ����ڵ�"                 */
		field.add("ACCT_COD"           ,2       ,"C");  /*"�ñ����ڵ�"                 */
		field.add("TAX_ITEM"           ,6       ,"C");  /*"�������"                   */
		field.add("TAX_YYMM"           ,6       ,"C");  /*"�������"                   */
		field.add("TAX_DIV"            ,1       ,"C");  /*"��������"                   */
		field.add("HACD"               ,3       ,"C");  /*"����������"                 */
		field.add("TAX_SNO"            ,6       ,"C");  /*"�Ű�����"                   */
		field.add("TAX_DT"             ,8       ,"C");  /*"�Ű�����"                   */
		field.add("REG_NO"             ,13      ,"C");  /*"������ �ֹ�/���ι�ȣ"       */
		field.add("REG_NM"             ,30      ,"C");  /*"������ ����"                */
		field.add("TPR_COD"            ,2       ,"C");  /*"�����ڵ�"                   */
		field.add("REG_BUCD"           ,5       ,"C");  /*"����� ������"              */
		field.add("BIZ_ZIP_NO"         ,6       ,"C");  /*"����� �����ȣ"            */
		field.add("BIZ_ZIP_ADDR"       ,60      ,"C");  /*"����� �����ּ�"            */
		field.add("BIZ_ADDR"           ,100     ,"C");  /*"����� ���ּ�"            */
		field.add("MO_TEL"             ,16      ,"C");  /*"�ڵ�����ȣ"                 */
		field.add("BIZ_TEL"            ,16      ,"C");  /*"����� ��ȭ��ȣ"            */
		field.add("BIZ_NO"             ,10      ,"C");  /*"����ڹ�ȣ"                 */
		field.add("CMP_NM"             ,50      ,"C");  /*"��ȣ��"                     */
		field.add("CMPTX_KD"           ,1       ,"C");  /*"���μ��Ű�����"             */
		field.add("REQ_KD_DT"          ,8       ,"C");  /*"�Ű������� ����"            */
		field.add("DUE_DT"             ,8       ,"C");  /*"��������"                   */
		field.add("REQ_DIV"            ,1       ,"C");  /*"�Ű���"                   */
		field.add("RVSN_S_DT"          ,8       ,"C");  /*"�ͼӻ���Ⱓ ���۳����"    */
		field.add("RVSN_E_DT"          ,8       ,"C");  /*"�ͼӻ���Ⱓ ��������"    */
		field.add("TOT_EMP_CNT"        ,14      ,"H");  /*"������ü ��������"          */
		field.add("TOT_B_AREA"         ,14      ,"H");  /*"������ü ���๰ ������"     */
		field.add("IN_EMP_CNT"         ,14      ,"H");  /*"�ñ����� ��������"          */
		field.add("IN_B_ADRE"          ,14      ,"H");  /*"�ñ����� ���๰ ������"     */
		field.add("TOT_CMPTX"          ,14      ,"H");  /*"���μ� �Ѿ�"                */
		field.add("PDIV_RT"            ,7       ,"H");  /*"�Ⱥк���"                   */
		field.add("CMPTX"              ,14      ,"H");  /*"���μ���"                   */
		field.add("RSTX_CMP"           ,14      ,"H");  /*"�ֹμ� ���μ���"            */
		field.add("RADTX"              ,14      ,"H");  /*"�Ű�Ҽ��� ���꼼"          */
		field.add("PADTX"              ,14      ,"H");  /*"���κҼ��� ���꼼"          */
		field.add("TAX_RT"             ,4       ,"H");  /*"����"                       */
		field.add("ADTX_YN"            ,1       ,"C");  /*"���꼼����"                 */
		field.add("DLQ_CNT"            ,14      ,"H");  /*"���������ϼ�"               */
		field.add("TOT_ADTX"           ,14      ,"H");  /*"���꼼 �հ�"                */
		field.add("TOT_AMT"            ,14      ,"H");  /*"�ѳ��μ���"                 */
		field.add("F_DUE_DT"           ,8       ,"C");  /*"���ʳ���"                   */
		field.add("PAY_YN"             ,1       ,"C");  /*"��������(�������ó����)"   */
		field.add("BANK_COD"           ,7       ,"C");  /*������ �����ڵ�(��������ڵ�)*/
		field.add("PAY_DT"             ,8       ,"C");  /*"��������"                   */
		field.add("TRS_DT"             ,8       ,"C");  /*"��ü����"                   */
		field.add("ACC_DT"             ,8       ,"C");  /*"ȸ������"                   */
		field.add("TOT_RSTX"           ,14      ,"H");  /*"�ֹμ��Ѿ�"                 */
		field.add("MASTER_BANK_COD"    ,3       ,"C");  /*"ȸ������"                   */

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
