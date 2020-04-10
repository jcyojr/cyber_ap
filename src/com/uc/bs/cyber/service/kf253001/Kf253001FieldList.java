/**
 *  �ֽý��۸� : ���̹����漼û ��ȭ
 *  ��  ��  �� : ���� ���� �ۼ��� ����
 *  ��  ��  �� : ������-���ϼ��� ���γ��� ������ȸ 
 *              
 *  Ŭ����  ID : Kf253001FieldList
 *  ����  �̷� :
 * ------------------------------------------------------------------------
 *  �ۼ���     �Ҽ�  		 ����      Tag   ����
 * ------------------------------------------------------------------------   
 * �ҵ���   ��ä��(��)  2011.06.20     %01%  �ű��ۼ�
 *  
 */
package com.uc.bs.cyber.service.kf253001;

import com.uc.bs.cyber.field.Comd_WorkKfField;
/**
 * @author Administrator
 *
 */
public class Kf253001FieldList extends Comd_WorkKfField {
			
	/**
	 * ������
	 * ���ϼ��� ���γ��� ������ȸ ��������
	 */
	public Kf253001FieldList() {
		// TODO Auto-generated constructor stub
		super();

		/*
		 * �۽�����(������)
		 * */
		this.addSendField("JUMINNO"         ,13      ,"C");     /*������ �ֹ�(�����)��Ϲ�ȣ*/
		this.addSendField("IN_STDATE"       ,8       ,"H");     /*��ȸ ���� ����             */
		this.addSendField("IN_ENDATE"       ,8       ,"H");     /*��ȸ ���� ����             */
		this.addSendField("DATATOT"         ,3       ,"H");     /*���� �� �Ǽ�               */
		this.addSendField("POINTNO"         ,3       ,"H");     /*���� ��ȣ                  */
		this.addSendField("DATANUM"         ,2       ,"H");     /*������ �Ǽ�                */
				
		/*
		 * ��������(������) : �ۼ��������� �����ؼ�
		 * */
		this.addRecvFieldAll(sendField);
		
		/*
		 * ��������(�ݺ�����)
		 * */		
		this.addRepetField("DANGGUBUN"      , 1       ,"C");    /*��� ����         */
		this.addRepetField("CUSTNO"         , 6       ,"H");    /*�ΰ� ���         */
		this.addRepetField("BYYYMM"         , 6       ,"H");    /*�ΰ� ���         */
		this.addRepetField("NAPBU_AMT"      ,10       ,"H");    /*���� �ݾ�         */
		this.addRepetField("NAPBU_DATE"     , 8       ,"H");    /*���� ����         */
		this.addRepetField("NAPBU_SYS"      , 1       ,"C");    /*���� �̿� �ý���  */ 
		this.addRepetField("JIJUM_CODE"     , 7       ,"H");    /*�������� ���� �ڵ�*/ 
		
	}

}
