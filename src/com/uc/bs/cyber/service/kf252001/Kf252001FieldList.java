/**
 *  �ֽý��۸� : ���̹����漼û ��ȭ
 *  ��  ��  �� : ���� ���� �ۼ��� ����
 *  ��  ��  �� : ������-���ϼ������ ���ΰ�� ����

 *  Ŭ����  ID : Kf252001FieldList
 *  ����  �̷� :
 * ------------------------------------------------------------------------
 *  �ۼ���     �Ҽ�  		 ����      Tag   ����
 * ------------------------------------------------------------------------   
 * �ҵ���   ��ä��(��)  2011.06.08     %01%  �ű��ۼ�
 *  
 */
package com.uc.bs.cyber.service.kf252001;

import com.uc.bs.cyber.field.Comd_WorkKfField;
/**
 * @author Administrator
 *
 */
public class Kf252001FieldList extends Comd_WorkKfField {
			
	
	/**
	 * ������
	 * ���ϼ��� ���ΰ�� ���� �ۼ��� ��������
	 */
	public Kf252001FieldList() {
		// TODO Auto-generated constructor stub
		super();

		/*
		 * �۽�����(������)
		 * */
		this.addSendField("SEARCHKEY"           ,30       ,"C");   /*���밡��ȣ/���ڳ��ι�ȣ/������ȣ  */
		this.addSendField("BYYYMM"              , 6       ,"H");   /*�ΰ� ���                         */
		this.addSendField("NAPBU_AMT"           ,15       ,"H");   /*���� �ݾ�                         */
		this.addSendField("NAPBU_DATE"          , 8       ,"H");   /*���� ���� (�Ǽ�����)              */
		this.addSendField("OUTBANK_CODE"        , 7       ,"H");   /*������� ���� �ڵ�                */
		this.addSendField("OUTACCT_NO"          ,16       ,"C");   /*���� ���� FIELD 1                 */
		this.addSendField("NAP_TELNO"           ,14       ,"C");   /*���� ���� FIELD 2                 */
		this.addSendField("NAPBU_JUMINNO"       ,13       ,"C");   /*������ �ֹ�(�����)��Ϲ�ȣ       */
		this.addSendField("NAPBU_NAME"          ,10       ,"C");   /*���� ���� FIELD 3                 */
		this.addSendField("ACCT_OWNER"          ,10       ,"C");   /*���� ���� FIELD 4                 */
		this.addSendField("NAPBU_SYS"           , 1       ,"C");   /*���� �̿� �ý���                  */
		this.addSendField("NAPBU_GUBUN"         , 1       ,"C");   /*���� ���� ����                    */
		this.addSendField("RESERV2"             , 9       ,"C");   /*���� ���� FIELD 5               
		 */

		/*
		 * ��������(������) : �ۼ��������� �����ؼ�
		 * */
		this.addRecvFieldAll(sendField);
		
	}
	
	
}
