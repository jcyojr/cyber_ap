/**
 *  �ֽý��۸� : ���̹����漼û ��ȭ
 *  ��  ��  �� : ��������� �����ڷ� ���, �������� �Ľ̿�
 *  ��  ��  �� : ��������� Ư��¡�� ���������� �Ľ��ϱ� ���� �ʵ�����
 *              
 *  Ŭ����  ID : Txdm1271FieldList
 *  ����  �̷� :
 * ------------------------------------------------------------------------
 *  �ۼ���     �Ҽ�  		 ����      Tag   ����
 * ------------------------------------------------------------------------   
 * �۵���   ��ä��(��)  2011.10.17     %01%  �ű��ۼ�
 *  
 */
package com.uc.bs.cyber.daemon.txdm1271;

import com.uc.core.MapForm;
import com.uc.egtob.net.FieldList;
/**
 * @author Administrator
 *
 */
public class Txdm1271FieldList {
	
	
	private MapForm dataMap = null;
	
	private FieldList field ;
	
	private int     len =  0;
	
	/**
	 * ������
	 * �������� �Ľ̿� �����ۼ�
	 */
	public Txdm1271FieldList() {
		// TODO Auto-generated constructor stub
		
		field = new FieldList();
		
		/*
		 * Ư��¡�� �������� ��������
		 * */	
		field.add("SIDO_COD"           ,2         ,"C");   /*�õ��ڵ�                                    */ 
		field.add("SGG_COD"            ,3         ,"C");   /*�ñ����ڵ�                                  */ 
		field.add("ACCT_COD"           ,2         ,"C");   /*ȸ���ڵ�                                    */ 
		field.add("TAX_ITEM"           ,6         ,"C");   /*�����ڵ�                                    */ 
		field.add("TAX_YYMM"           ,6         ,"C");   /*�������                                    */ 
		field.add("TAX_DIV"            ,1         ,"C");   /*��������                                    */ 
		field.add("HACD"               ,3         ,"C");   /*����������                                  */ 
		field.add("TAX_SNO"            ,6         ,"C");   /*������ȣ                                    */ 
		field.add("TAX_DT"             ,8         ,"C");   /*�Ű�����                                    */ 
		field.add("REG_NO"             ,13        ,"C");   /*������ �ֹ�/���ι�ȣ                        */ 
		field.add("REG_NM"             ,30        ,"C");   /*������ ����                                 */ 
		field.add("TPR_COD"            ,2         ,"C");   /*�����ڵ�                                    */ 
		field.add("REG_BUCD"           ,5         ,"C");   /*����� ������                               */ 
		field.add("ZIP_NO"             ,6         ,"C");   /*����� �����ȣ                             */ 
		field.add("BIZ_ZIP_ADDR"       ,60        ,"C");   /*����� �����ּ�                             */ 
		field.add("BIZ_ADDR"           ,100       ,"C");   /*����� ���ּ�                             */ 
		field.add("MO_TEL"             ,16        ,"C");   /*�ڵ�����ȣ                                  */ 
		field.add("BIZ_TEL"            ,16        ,"C");   /*����� ��ȭ��ȣ                             */ 
		field.add("SAUP_NO"            ,10        ,"C");   /*����ڹ�ȣ                                  */ 
		field.add("CMP_NM"             ,50        ,"C");   /*��ȣ��                                      */ 
		field.add("REQ_DIV"            ,1         ,"C");   /*�Ű���                                    */ 
		field.add("RVSN_YYMM"          ,6         ,"C");   /*�ͼӳ��                                    */ 
		field.add("SUP_YYMM"           ,6         ,"C");   /*���޳��                                    */ 
		field.add("DUE_DT"             ,8         ,"C");   /*��������                                    */ 
		field.add("YY_TRTN"            ,14        ,"H");   /*�������� ȯ���Ѿ�                           */ 
		field.add("YY_MRTN"            ,14        ,"H");   /*������� ȯ�޾�                             */ 
		field.add("YY_RRTN"            ,14        ,"H");   /*�������� ȯ���ܾ�                           */ 
		field.add("OUT_TAMT"           ,14        ,"H");   /*�ߵ���� �����Ѿ�                           */ 
		field.add("OUT_MAMT"           ,14        ,"H");   /*�ߵ���� ���������                         */ 
		field.add("OUT_RAMT"           ,14        ,"H");   /*�ߵ���� �����ܾ�                           */ 
		field.add("EMP_CNT_1"          ,14        ,"H");   /*�ο�_�����ٷμҵ�                           */ 
		field.add("INCOMTAX_1"         ,14        ,"H");   /*(�Ű�),�ҵ漼��_�����ٷμҵ�                */ 
		field.add("RSTX_1"             ,14        ,"H");   /*(�Ű�),�ֹμ�_�����ٷμҵ�                  */ 
		field.add("ADTX_1"             ,14        ,"H");   /*(�Ű�),���꼼_�����ٷμҵ�                  */ 
		field.add("EMP_CNT_2"          ,14        ,"H");   /*�ο�_���ڼҵ�                               */ 
		field.add("INCOMTAX_2"         ,14        ,"H");   /*(�Ű�),�ҵ漼��_���ڼҵ�                    */ 
		field.add("RSTX_2"             ,14        ,"H");   /*(�Ű�),�ֹμ�_���ڼҵ�                      */ 
		field.add("ADTX_2"             ,14        ,"H");   /*(�Ű�),���꼼_���ڼҵ�                      */ 
		field.add("EMP_CNT_3"          ,14        ,"H");   /*�ο�_���ҵ�                               */ 
		field.add("INCOMTAX_3"         ,14        ,"H");   /*(�Ű�),�ҵ漼��_���ҵ�                    */ 
		field.add("RSTX_3"             ,14        ,"H");   /*(�Ű�),�ֹμ�_���ҵ�                      */ 
		field.add("ADTX_3"             ,14        ,"H");   /*(�Ű�),���꼼_���ҵ�                      */ 
		field.add("EMP_CNT_4"          ,14        ,"H");   /*�ο�_���������ҵ�(����ҵ�)                 */ 
		field.add("INCOMTAX_4"         ,14        ,"H");   /*(�Ű�),�ҵ漼��_���������ҵ�(����ҵ�)      */
		field.add("RSTX_4"             ,14        ,"H");   /*(�Ű�),�ֹμ�_���������ҵ�(����ҵ�)        */ 
		field.add("ADTX_4"             ,14        ,"H");   /*(�Ű�),���꼼_���������ҵ�(����ҵ�)        */ 
		field.add("EMP_CNT_5"          ,14        ,"H");   /*�ο�_���������ҵ�                           */ 
		field.add("INCOMTAX_5"         ,14        ,"H");   /*(�Ű�),�ҵ漼��_���������ҵ�                */ 
		field.add("RSTX_5"             ,14        ,"H");   /*(�Ű�),�ֹμ�_���������ҵ�                  */ 
		field.add("ADTX_5"             ,14        ,"H");   /*(�Ű�),���꼼_���������ҵ�                  */ 
		field.add("EMP_CNT_6"          ,14        ,"H");   /*�ο�_��Ÿ�ҵ�                               */ 
		field.add("INCOMTAX_6"         ,14        ,"H");   /*(�Ű�),�ҵ漼��_��Ÿ�ҵ�                    */ 
		field.add("RSTX_6"             ,14        ,"H");   /*(�Ű�),�ֹμ�_��Ÿ�ҵ�                      */ 
		field.add("ADTX_6"             ,14        ,"H");   /*(�Ű�),���꼼_��Ÿ�ҵ�                      */ 
		field.add("EMP_CNT_7"          ,14        ,"H");   /*�ο�_���μ��� 98��...                       */ 
		field.add("INCOMTAX_7"         ,14        ,"H");   /*(�Ű�),�ҵ漼��_���μ��� 98��...            */ 
		field.add("RSTX_7"             ,14        ,"H");   /*(�Ű�),�ֹμ�_���μ��� 98��...              */ 
		field.add("ADTX_7"             ,14        ,"H");   /*(�Ű�),���꼼_���μ��� 98��...              */ 
		field.add("EMP_CNT_8"          ,14        ,"H");   /*�ο�_�ܱ������κ��� �����ҵ�                */ 
		field.add("INCOMTAX_8"         ,14        ,"H");   /*(�Ű�),�ҵ漼��_�ܱ������κ��� �����ҵ�     */
		field.add("RSTX_8"             ,14        ,"H");   /*(�Ű�),�ֹμ�_�ܱ������κ��� �����ҵ�       */ 
		field.add("ADTX_8"             ,14        ,"H");   /*(�Ű�),���꼼_�ܱ������κ��� �����ҵ�       */ 
		field.add("EMP_CNT_9"          ,14        ,"H");   /*�ο�_�ҵ漼�� ��119�� �絵�ҵ�              */ 
		field.add("INCOMTAX_9"         ,14        ,"H");   /*(�Ű�),�ҵ漼��_�ҵ漼�� ��119�� �絵�ҵ�   */
		field.add("RSTX_9"             ,14        ,"H");   /*(�Ű�),�ֹμ�_�ҵ漼�� ��119�� �絵�ҵ�     */ 
		field.add("ADTX_9"             ,14        ,"H");   /*(�Ű�),���꼼_�ҵ漼�� ��119�� �絵�ҵ�     */ 
		field.add("INCOMTAX_10"        ,14        ,"H");   /*(�Ű�),�ҵ漼��_�հ�                        */ 
		field.add("RSTX_10"            ,14        ,"H");   /*(�Ű�),�ֹμ�_�հ�                          */ 
		field.add("ADTX_10"            ,14        ,"H");   /*(�Ű�),���꼼_�հ�_�Ű�Ҽ��� ���꼼�� ���� */
		field.add("MM_RTN"             ,14        ,"H");   /*�������ȯ�޼����հ�                        */ 
		field.add("PAY_RSTX"           ,14        ,"H");   /*(����),�ֹμ�                               */ 
		field.add("PAY_ADTX"           ,14        ,"H");   /*(����),���꼼                               */ 
		field.add("TAX_RT"             ,4         ,"C");   /*����                                        */ 
		field.add("ADTX_YN"            ,1         ,"C");   /*���꼼����                                  */ 
		field.add("TOT_ADTX"           ,14        ,"H");   /*���꼼 �հ�                                 */ 
		field.add("TOT_AMT"            ,14        ,"H");   /*�ѳ��μ���                                  */ 
		field.add("F_DUE_DT"           ,8         ,"C");   /*���ʳ���                                    */ 
		field.add("EMP_CNT_11"         ,14        ,"H");   /*�ο�_���ݼҵ�                               */ 
		field.add("INCOMTAX_11"        ,14        ,"H");   /*�ҵ漼_���ݼҵ�                             */ 
		field.add("ADTX_11"            ,14        ,"H");   /*�ֹμ�_���ݼҵ�                             */ 
		field.add("RDT_RSTX"           ,14        ,"H");   /*���꼼_���ݼҵ�                             */ 
		field.add("RDT_ADTX"           ,14        ,"H");   /*�������� ���꼼 ���� ������                 */ 
		field.add("RSTX_11"            ,14        ,"H");   /*�������� ���꼼 ���� ������                 */ 
		field.add("PAY_YN"             ,1         ,"C");   /*��������(�������ó����)                    */ 
		field.add("BRC_NO"             ,7         ,"C");   /*��������ڵ�                                */ 
		field.add("PAY_DT"             ,8         ,"C");   /*��������                                    */ 
		field.add("TRS_DT"             ,8         ,"C");   /*��ü����                                    */ 
		field.add("ACC_DT"             ,8         ,"C");   /*ȸ������                                    */ 
		field.add("MASTER_BANK_COD"    ,3         ,"C");   /*�����ڵ�                                    */ 
		
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
