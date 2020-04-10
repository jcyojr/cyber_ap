/**
 *  �ֽý��۸� : ���̹����漼û ��ȭ
 *  ��  ��  �� : ���� ���� �ۼ��� ����
 *  ��  ��  �� : ������-���ܼ��� �������� ������ȸ - ��� ��������
 *              
 *  Ŭ����  ID : Kf271001FieldList
 *  ����  �̷� :
 * ------------------------------------------------------------------------
 *  �ۼ���     �Ҽ�  		 ����      Tag   ����
 * ------------------------------------------------------------------------   
 * �ҵ���   ��ä��(��)  2011.06.08     %01%  �ű��ۼ�
 *  
 */
package com.uc.bs.cyber.service.kf271001;

import com.uc.bs.cyber.field.Comd_WorkKfField;

/**
 * @author Administrator
 *
 */
public class Kf271001FieldList extends Comd_WorkKfField {
			
	
	/**
	 * ������
	 * ȯ�氳���δ�� ������ȸ��� ��������
	 */
	public Kf271001FieldList() {
		// TODO Auto-generated constructor stub
		super();
				
		/*
		 * �������) �������� ��� �ۼ��������� �����ϴ�.
		 *           �ݺ������� �ִ� ��� �ݺ��Ǽ��� Field �� 'DATA_NUM'���� ���Ͻ�Ų��.
		 * */
		
		/*
		 * �۽�����(������)
		 * */
		this.addSendField("JUMIN_NO"         , 13 ,  "C");  /*�ֹ�(�����,����)��Ϲ�ȣ */
		this.addSendField("DATA_TOT"         , 3  ,  "H");  /*���� �� �Ǽ�              */
		this.addSendField("POINT_NO"         , 3  ,  "H");  /*���� ��ȣ                 */
		this.addSendField("DATA_NUM"         , 2  ,  "H");  /*������ �Ǽ�               */
		
		/*
		 * ��������(������) : �ۼ��������� �����ؼ�
		 * */
		this.addRecvFieldAll(sendField);
		
		/*
		 * ��������(�ݺ�����)
		 * */
		this.addRepetField("PUBGCODE"        , 2  ,  "H");  /*������ �з��ڵ�         */
		this.addRepetField("JIRONO"          , 7  ,  "H");  /*���� ��ȣ                 */
		this.addRepetField("TAX_NO"          , 32 ,  "C");  /*���ι�ȣ                  */
		this.addRepetField("GRNO"         	 , 19 ,  "C");  /* ���ڳ��ι�ȣ             */
		this.addRepetField("GWA_MOK"         , 6  ,  "H");  /*����/����                 */
		this.addRepetField("GWA_DATE"        , 6  ,  "H");  /*�⵵/���                 */
		this.addRepetField("KIBUN"           , 1  ,  "H");  /*��ǥ ���� ����            */
		this.addRepetField("NAP_AMT"         , 15 ,  "H");  /*���� �ݾ�                 */
		this.addRepetField("NAP_GUBUN"       , 1  ,  "C");  /*���� ���� ����            */
		this.addRepetField("NAP_DATE"        , 8  ,  "H");  /*���� ����                 */
		this.addRepetField("AUTO_REG"        , 1  ,  "C");  /*�ڵ���ü ��� ����        */

	}
}
