/**
 *  �ֽý��۸� : ���̹����漼û ��ȭ
 *  ��  ��  �� : ���� ���� �ۼ��� ����
 *  ��  ��  �� : ������-ȯ�氳���δ�� ���ΰ������/ �����û 
 *              
 *  Ŭ����  ID : Kf202001FieldList
 *  ����  �̷� :
 * ------------------------------------------------------------------------
 *  �ۼ���     �Ҽ�  		 ����      Tag   ����
 * ------------------------------------------------------------------------   
 * �ҵ���   ��ä��(��)  2011.06.08     %01%  �ű��ۼ�
 *  
 */
package com.uc.bs.cyber.service.kf202001;

import com.uc.bs.cyber.field.Comd_WorkKfField;
/**
 * @author Administrator
 *
 */
public class Kf202001FieldList extends Comd_WorkKfField {
			

	/**
	 * ������
	 * ȯ�氳���δ�� ���ΰ������ ��������
	 */
	public Kf202001FieldList() {
		// TODO Auto-generated constructor stub
		
        super();
		
        addSendField("JUMIN_NO"       , 13 ,  "C" );  /*�ֹ�(�����,����)��Ϲ�ȣ  */
		addSendField("RESERV1"        , 6  ,  "C" );  /*���� ���� FIELD 1          */
		addSendField("ETAX_NO"        , 29 ,  "C" );  /*���ڳ��ι�ȣ               */
		addSendField("NAPBU_AMT"      , 15 ,  "H" );  /*���� �ݾ�                  */
		addSendField("NAPBU_DATE"     , 8  ,  "H" );  /*����(����) ����            */
		addSendField("OUTBANK_CODE"   , 7  ,  "H" );  /*������� ���� �ڵ�         */
		addSendField("OUTACCT_NO"     , 16 ,  "C" );  /*��� ���� ��ȣ             */
		addSendField("NAP_TELNO"      , 14 ,  "C" );  /*���� ��ȭ ��ȣ             */
		addSendField("NAPBU_JUMINNO"  , 13 ,  "C" );  /*������ �ֹ�(�����)��Ϲ�ȣ*/
		addSendField("NAPBU_NAME"     , 10 ,  "C" );  /*������ ����                */
		addSendField("ACCT_OWNER"     , 10 ,  "C" );  /*������ ����                */
		addSendField("NAPBU_SYS"      , 1  ,  "C" );  /*���� �̿� �ý���           */
		addSendField("NAPBU_GUBUN"    , 1  ,  "C" );  /*���� ���� ����             */
		addSendField("RESERV2"        , 9  ,  "C" );  /*���� ���� FIELD 2          */

		/*
		 * ��������(������)
		 * �ۼ��������� �����ϹǷ� �۽������� ���������� ��ü��Ŵ
		 * */
	    this.addRecvFieldAll(sendField);
	    
	}
	

}
