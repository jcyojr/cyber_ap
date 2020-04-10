/**
 *  �ֽý��۸� : ���̹����漼û ��ȭ
 *  ��  ��  �� : ���� ���� �ۼ��� ����
 *  ��  ��  �� : �λ�����-����� �� Ư��ȸ���Ϻ� ���� ������ȸ
 *              
 *  Ŭ����  ID : Bs531002FieldList
 *  ����  �̷� :
 * ------------------------------------------------------------------------
 *  �ۼ���     �Ҽ�  		 ����      Tag   ����
 * ------------------------------------------------------------------------   
 * ����ȯ     ��ä��     2015.01.19     %01%  �ű��ۼ�
 *  
 */
package com.uc.bs.cyber.service.bs531002;

import com.uc.bs.cyber.field.Comd_WorkBsField;
/**
 * @author Administrator
 *
 */
public class Bs531002FieldList extends Comd_WorkBsField {
			
	
	/**
	 * ������
	 * BS ������� �������� ��ȸ ��������
	 */
	public Bs531002FieldList() {
		// TODO Auto-generated constructor stub
		super();

		/*������ �ۼ��� ���� ���⼭�� �ۼ����� �����ϴ�.*/
		
		/*
		 * �۽�����(���̹�)
		 * */	
	    addSendField("TRN_DATE"               , 8   ,  "C"  );  /*�ŷ�����        */
	    addSendField("TRN_TIME"               , 6   ,  "C"  );  /*�ŷ��ð�	    */
	    addSendField("VIR_ACC_NO"             , 12  ,  "C"  );  /*������¹�ȣ    */
 		addSendField("TRN_AMT"                , 13  ,  "H"  );  /*�ŷ��ݾ�        */
 		addSendField("MEDA_GBN"               , 2   ,  "C"  );  /*��ü����        */
 		addSendField("TRN_UNIQ_NO"            , 12  ,  "C"  );  /*�ŷ�������ȣ    */
 		addSendField("REG_NM"                 , 20  ,  "C"  );  /*����            */
 		addSendField("TRN_UNIQ_NO1"           , 13  ,  "C"  );  /*�ŷ�������ȣ1   */
 		addSendField("NEW_VIR_ACC_NO"         , 14  ,  "C"  );  /*�Ű�����¹�ȣ  */
 		addSendField("FILLER2"                , 50  ,  "C"  );  /*����            */
 		
 		/*
		 * ��������(������)
		 * �ۼ��������� �����ϹǷ� �۽������� ���������� ��ü��Ŵ
		 * */
	    this.addRecvFieldAll(sendField);
        
	}
	
	
}
