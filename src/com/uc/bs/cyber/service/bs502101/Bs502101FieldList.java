/**
 *  �ֽý��۸� : ���̹����漼û ��ȭ
 *  ��  ��  �� : ���� ���γ��� ����
 *  ��  ��  �� : �λ�����-���� ���γ��� ����
 *              
 *  Ŭ����  ID : Bs502101FieldList
 *  ����  �̷� :
 * ------------------------------------------------------------------------
 *  �ۼ���     �Ҽ�  		 ����      Tag   ����
 * ------------------------------------------------------------------------   
 * �ҵ���   ��ä��(��)  2011.06.24     %01%  �ű��ۼ�
 *  
 */
package com.uc.bs.cyber.service.bs502101;

import com.uc.bs.cyber.field.Comd_WorkKfField;
/**
 * @author Administrator
 *
 */
public class Bs502101FieldList extends Comd_WorkKfField {
			
	
	/**
	 * ������
	 * ���� ���γ��� ���� ��������
	 * (���ǻ���)
	 * ���� ������ ���漼�� ���γ����� ������ �� �ִ� �� ����.
	 * 
	 */
	public Bs502101FieldList() {
		// TODO Auto-generated constructor stub
		super();

		/*������ �ۼ��� ���� ���⼭�� �ۼ����� �����ϴ�.*/
		
		/*
		 * �۽�����(���̹�)
		 * */	
 		addSendField("JUMIN_NO"             , 13 ,  "C"  );  /*�ֹ�(�����,����)��Ϲ�ȣ */
 		addSendField("JINGSU_ACCTNO"        , 6  ,  "C"  );  /*¡�������¹�ȣ / �������� */
 		
 		/*���ι�ȣ 29 �ڸ�*/
 		addSendField("EPAY_NO"        		, 19 ,  "C"  );  /*���ڳ��ι�ȣ              */
 		addSendField("NAPBU_SEQ"      		, 3  ,  "H"  );  /*���μ��� / ��������       */
 		addSendField("RESERV1"        		, 7  ,  "C"  );  /*�������� �ʵ�             */
 		
 		addSendField("NAPBU_AMT"        	, 15 ,  "H"  );  /*���αݾ�                  */
 		addSendField("NAPBU_DATE"        	, 8  ,  "H"  );  /*����(����)����            */
 		addSendField("OUTBANK_CODE"        	, 7  ,  "H"  );  /*������� �����ڵ�         */
 		addSendField("OUTACCT_NO"        	, 16 ,  "C"  );  /*��ݰ��¹�ȣ              */
 		addSendField("NAP_TELNO"        	, 14 ,  "C"  );  /*������ȭ��ȣ              */
 		addSendField("NAPBU_JUMINNO"        , 13 ,  "C"  );  /*������ �ֹ�(�����)��ȣ   */
 		addSendField("NAPBU_NAME"        	, 10 ,  "C"  );  /*������ ����               */
 		addSendField("ACCT_OWNER"        	, 10 ,  "C"  );  /*������ ����               */
 		addSendField("NAPBU_SYS"        	, 1  ,  "C"  );  /*�����̿� �ý���           */
 		addSendField("B_NAPBU_SYS"        	, 1  ,  "C"  );  /*�� �����̿� �ý���        */
 		addSendField("NAPBU_GUBUN"        	, 1  ,  "C"  );  /*�������� ����             */
 		/*�������� �ʵ� 9*/
 		addSendField("RESERV2"        		, 1  ,  "C"  );  /*�������� �ʵ�             */
 		addSendField("RESERV3"        		, 8  ,  "C"  );  /*�������� �ʵ�             */
        
		/*
		 * ��������(������)
		 * �ۼ��������� �����ϹǷ� �۽������� ���������� ��ü��Ŵ
		 * */
	    this.addRecvFieldAll(sendField);
        
	}
}
