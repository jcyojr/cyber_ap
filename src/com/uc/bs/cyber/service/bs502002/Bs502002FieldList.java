/**
 *  �ֽý��۸� : ���̹����漼û ��ȭ
 *  ��  ��  �� : ���漼 ���γ��� �������Ա� �뺸��ȸ
 *  ��  ��  �� : ������-���漼 ���γ���(������) �Ա��뺸�������� 
 *              
 *  Ŭ����  ID : Bs502002FieldList
 *  ����  �̷� :
 * ------------------------------------------------------------------------
 *  �ۼ���     �Ҽ�  		 ����      Tag   ����
 * ------------------------------------------------------------------------   
 * �ҵ���   ��ä��(��)  2011.06.08     %01%  �ű��ۼ�
 *  
 */
package com.uc.bs.cyber.service.bs502002;

import com.uc.bs.cyber.field.Comd_WorkKfField;
/**
 * @author Administrator
 *
 */
public class Bs502002FieldList extends Comd_WorkKfField {
			
	
	/**
	 * ������
	 * ���漼 ���ΰ�� �뺸 ��������
	 */
	public Bs502002FieldList() {
		// TODO Auto-generated constructor stub
		super();

		/*������ �ۼ��� ���� ���⼭�� �ۼ����� �����ϴ�.*/
		
		/*
		 * �۽�����(���̹�)
		 * */	
 		addSendField("NAPBU_AMT"        , 15 ,  "H"  );  	/*���αݾ� 			*/
 		addSendField("NAPBU_DATE"      	, 8  ,  "H"  );  	/*��������          */
 		addSendField("OUTBANK_CODE"     , 7  ,  "H"  );  	/*������� �����ڵ� */
 		addSendField("NAPBU_NAME"       , 20 ,  "C"  );  	/*������ ����       */
 		addSendField("OUTACCT_NO"       , 16 ,  "C"  );  	/*��ݰ��¹�ȣ      */
 		addSendField("GUBUN"        	, 2  ,  "H"  );  	/*��ü����          */
 		addSendField("INACCT_NO"        , 12 ,  "C"  );  	/*�Աݰ��¹�ȣ      */
 		addSendField("RESERV2"        	, 5  ,  "C"  );  	/*�������� �ʵ�     */  
        
		/*
		 * ��������(������)
		 * �ۼ��������� �����ϹǷ� �۽������� ���������� ��ü��Ŵ
		 * */
	    this.addRecvFieldAll(sendField);
        
	}
}
