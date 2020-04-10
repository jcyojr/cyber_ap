/**
 *  �ֽý��۸� : ���̹����漼û ��ȭ
 *  ��  ��  �� : ���� ���� �ۼ��� ����
 *  ��  ��  �� : BS ������ �Ա� �������� 
 *              
 *  Ŭ����  ID : Bs531001FieldList
 *  ����  �̷� :
 * ------------------------------------------------------------------------
 *  �ۼ���     �Ҽ�  		 ����      Tag   ����
 * ------------------------------------------------------------------------   
 * ����ȯ     ��ä��     2015.01.19     %01%  �ű��ۼ�
 *  
 */
package com.uc.bs.cyber.service.bs531001;

import com.uc.bs.cyber.field.Comd_WorkBsField;
/**
 * @author Administrator
 *
 */
public class Bs531001FieldList extends Comd_WorkBsField {
			
	
	/**
	 * ������
	 * BS ������� �Ա� ��������
	 */
	public Bs531001FieldList() {
		// TODO Auto-generated constructor stub
		super();

		/*BS �ۼ��� ���� ���⼭�� �ۼ����� �����ϴ�.*/
		
		/*
		 * �۽�����(���̹�)
		 * */	
	    addSendField("ACC_NO"             	, 15  ,  "C"  );  /*���¹�ȣ  �Աݰ��¹�ȣ */
	    addSendField("FILLER2"             	, 2   ,  "C"  );  /*����					 */
	    addSendField("JOB_GBN"              , 2   ,  "C"  );  /*�ŷ�����  �Ա� 20  �Ա���� 51  */
 		addSendField("BANK_CODE"            , 3   ,  "C"  );  /*�����ڵ�    */
 		addSendField("TRN_AMT"              , 13  ,  "H"  );  /*�ŷ��ݾ�                         */
 		addSendField("TRN_F_AMT"            , 13  ,  "H"  );  /*�Ա��� �ܾ�          */
 		addSendField("GIRO_CODE"            , 7   ,  "C"  );  /*�Ա��� �����ڵ�                */
 		addSendField("REG_NM"               , 14  ,  "C"  );  /*����                      */
 		addSendField("SUPYO_NO"             , 10  ,  "C"  );  /*��ǥ��ȣ                 */
 		addSendField("CASH_AMT"             , 13  ,  "H"  );  /*����                 */
 		addSendField("TA_SUPYO_AMT"         , 13  ,  "H"  );  /*Ÿ�� ��ǥ�ݾ�                      */
 		addSendField("ETC_SUPYO_AMT"        , 13  ,  "H"  );  /*���Լ�ǥ, ��Ÿ            */
 		addSendField("VIR_ACC_NO"           , 14  ,  "C"  );  /*������¹�ȣ                 */
 		addSendField("TRN_DATE"          	, 6   ,  "C"  );  /*�ŷ�����                */
 		addSendField("TRN_TIME"             , 6   ,  "C"  );  /*�ŷ��ð�               */
 		addSendField("FILLER3"              , 6   ,  "C"  );  /*����               */
 	                                            
		/*
		 * ��������(������)
		 * �ۼ��������� �����ϹǷ� �۽������� ���������� ��ü��Ŵ
		 * */
	    this.addRecvFieldAll(sendField);
        
	}
	
	
}
