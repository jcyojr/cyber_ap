/**
 *  �ֽý��۸� : ���̹����漼û ��ȭ
 *  ��  ��  �� : ��������� ���� 
 *  ��  ��  �� : �λ�����-���漼 ��������� 
 *              
 *  Ŭ����  ID : Bs992001FieldList
 *  ����  �̷� :
 * ------------------------------------------------------------------------
 *  �ۼ���     �Ҽ�  		 ����      Tag   ����
 * ------------------------------------------------------------------------   
 * �۵���   �ٻ�(��)  2011.06.24     %01%  �ű��ۼ�
 *  
 */
package com.uc.bs.cyber.service.bs992001;

import com.uc.bs.cyber.field.Comd_WorkKfField;
/**
 * @author Administrator
 *
 */
public class Bs992001FieldList extends Comd_WorkKfField {
			
	/**
	 * ������
	 * ���漼 ���� ����� ��������
	 */
	public Bs992001FieldList() {
		// TODO Auto-generated constructor stub
		super();

		/*������ �ۼ��� ���� ���⼭�� �ۼ����� �����ϴ�.*/
		
		/*
		 * �۽�����(���̹�)
		 * */	
		addSendField("OUTBANK_CODE"           , 7       ,"C");   /*������� ���� �ڵ�                                      */
		addSendField("NAPBU_JUMIN_NO"         ,13       ,"C");   /*������ �ֹ�(�����)��Ϲ�ȣ                             */
		addSendField("B_OUTBANKJNO"           ,12       ,"C");   /*���ŷ� ��� ���� ���� ���� ��ȣ / ���ŷ� �ŷ� ���� ��ȣ */
		addSendField("B_OUTBANKTXDATE"        ,12       ,"H");   /*���ŷ� ��� ���� ���� ���� �Ͻ�                         */
		addSendField("B_OUTACCT_NO"           ,16       ,"C");   /*���ŷ� ��� ���� ��ȣ                                   */
		addSendField("B_NAPBU_AMT"            ,15       ,"H");   /*���ŷ� ���� �ݾ�                                        */
		addSendField("CANCEL_GUBUN"           , 1       ,"C");   /*��� ����                                               */
		addSendField("ORI_TRADE"              , 1       ,"C");   /*���ŷ� ���� ���� ����                                   */
		addSendField("RESERV1"                , 9       ,"C");   /*���� ���� FIELD                                         */	

		/*
		 * ��������(������)
		 * �ۼ��������� �����ϹǷ� �۽������� ���������� ��ü��Ŵ
		 * */
	    this.addRecvFieldAll(sendField);
        
	}
	
}
