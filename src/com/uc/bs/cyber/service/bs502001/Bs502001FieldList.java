/**
 *  �ֽý��۸� : ���̹����漼û ��ȭ
 *  ��  ��  �� : ���漼 ���ΰ�� �뺸
 *  ��  ��  �� : ������-���漼 ���γ��� ������ȸ 
 *              
 *  Ŭ����  ID : Bs502001FieldList
 *  ����  �̷� :
 * ------------------------------------------------------------------------
 *  �ۼ���     �Ҽ�  		 ����      Tag   ����
 * ------------------------------------------------------------------------   
 * �ҵ���   ��ä��(��)  2011.06.08     %01%  �ű��ۼ�
 *  
 */
package com.uc.bs.cyber.service.bs502001;

import com.uc.bs.cyber.field.Comd_WorkKfField;
/**
 * @author Administrator
 *
 */
public class Bs502001FieldList extends Comd_WorkKfField {
			
	
	/**
	 * ������
	 * ���漼 ���ΰ���뺸 ��������
	 */
	public Bs502001FieldList() {
		// TODO Auto-generated constructor stub
		super();

		/*������ �ۼ��� ���� ���⼭�� �ۼ����� �����ϴ�.*/
		
		/*
		 * �۽�����(���̹�)
		 * */	
 		addSendField("JUMIN_NO"             , 13 ,  "C"  );  /*�ֹ�(�����,����)��Ϲ�ȣ */
 		addSendField("JINGSU_ACCTNO"        , 6  ,  "C"  );  /*¡�������¹�ȣ            */
 		addSendField("ETAXNO"        		, 29 ,  "C"  );  /*���ι�ȣ                  */
 		addSendField("SEQNO"        		, 5  ,  "H"  );  /*�г� �Ϸù�ȣ             */
 		addSendField("NAPBU_AMT"        	, 15 ,  "H"  );  /*���αݾ�                  */
 		addSendField("NAPBU_DATE"        	, 8  ,  "H"  );  /*��������                  */
 		addSendField("OUTBANK_CODE"        	, 7  ,  "H"  );  /*������� �����ڵ�         */
 		addSendField("OUTACCT_NO"        	, 16 ,  "C"  );  /*��ݰ��¹�ȣ              */
 		addSendField("NAP_TELNO"        	, 14 ,  "C"  );  /*������ȭ��ȣ              */
 		addSendField("NAPBU_JUMINNO"        , 13 ,  "C"  );  /*������ �ֹ�(�����)��ȣ   */
 		addSendField("NAPBU_NAME"        	, 10 ,  "C"  );  /*������ ����               */
 		addSendField("ACCT_OWNER"        	, 10 ,  "C"  );  /*������ ����               */
 		addSendField("NAPBU_SYS"        	, 1  ,  "C"  );  /*�����̿� �ý���           */
 		addSendField("B_NAPBU_SYS"        	, 1  ,  "C"  );  /*�����̿� �ý���           */
 		addSendField("NAPBU_GUBUN"        	, 1  ,  "C"  );  /*�������� ����             */
 		addSendField("RESERV2"        		, 9  ,  "C"  );  /*�������� �ʵ�             */
        /*  
 		26100068500
 		6601172120616
 		000000
 		35043010600120110615700950844
 		00000
 		000000000039410
 		20110622
 		0329990                01034578330   6601172120616����ȫ    ����ȫ    B Q          		
 		*/
 		
		/*
		 * ��������(������)
		 * �ۼ��������� �����ϹǷ� �۽������� ���������� ��ü��Ŵ
		 * */
	    this.addRecvFieldAll(sendField);
        
	}
	
	
}
