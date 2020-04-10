/**
 *  �ֽý��۸� : ���̹����漼û ��ȭ
 *  ��  ��  �� : ���� ���� �ۼ��� ����
 *  ��  ��  �� : ������-���ܼ��� ���ΰ������/ �����û 
 *              
 *  Ŭ����  ID : Kf272001FieldList
 *  ����  �̷� :
 * ------------------------------------------------------------------------
 *  �ۼ���     �Ҽ�  		 ����      Tag   ����
 * ------------------------------------------------------------------------   
 * �ҵ���   ��ä��(��)  2011.06.08     %01%  �ű��ۼ�
 *  
 */
package com.uc.bs.cyber.service.kf272001;

import com.uc.bs.cyber.field.Comd_WorkKfField;
/**
 * @author Administrator
 *
 */
public class Kf272001FieldList extends Comd_WorkKfField {
			

	/**
	 * ������
	 * ȯ�氳���δ�� ���ΰ������ ��������
	 */
	public Kf272001FieldList() {
		// TODO Auto-generated constructor stub
		
        super();
		
        addSendField("JUMIN_NO"       , 13 ,  "C" );  /*�ֹ�(�����,����)��Ϲ�ȣ  */
        addSendField("TAX_NO"         , 32 ,  "C" );  /*���ι�ȣ 				   */
        addSendField("GRNO"        	  , 19 ,  "C" );  /*���ڳ��ι�ȣ               */
		addSendField("NAPBU_AMT"      , 15 ,  "H" );  /*���� �ݾ�                  */
		addSendField("NAPBU_DATE"     , 8  ,  "H" );  /*����(�Ǽ���) ����          */
		addSendField("OUTBANK_CODE"   , 7  ,  "H" );  /*������� ���� �ڵ�         */
		addSendField("FIELD1"         , 16 ,  "C" );  /*�������� FIELD 1           */
		addSendField("FIELD2"         , 14 ,  "C" );  /*�������� FIELD 2           */
		addSendField("NAPBU_JUMINNO"  , 13 ,  "C" );  /*������ �ֹ�(�����)��Ϲ�ȣ*/
		addSendField("FIELD3"         , 10 ,  "C" );  /*�������� FIELD 3           */
		addSendField("FIELD4"         , 10 ,  "C" );  /*�������� FIELD 4           */
		addSendField("NAPBU_SYS"      , 1  ,  "C" );  /*���� �̿� �ý���           */
		addSendField("NAPBU_GUBUN"    , 1  ,  "C" );  /*���� ���� ����             */
		addSendField("FIELD5"         , 10 ,  "C" );  /*���� ���� FIELD 5          */

		/*
		 * ��������(������)
		 * �ۼ��������� �����ϹǷ� �۽������� ���������� ��ü��Ŵ
		 * */
	    this.addRecvFieldAll(sendField);
	    
	}
}

