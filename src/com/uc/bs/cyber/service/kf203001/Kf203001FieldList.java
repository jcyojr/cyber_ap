/**
 *  �ֽý��۸� : ���̹����漼û ��ȭ
 *  ��  ��  �� : ���� ���� �ۼ��� ����
 *  ��  ��  �� : ������-ȯ�氳���δ�� ���γ��� ������ȸ 
 *              
 *  Ŭ����  ID : Kf203001FieldList
 *  ����  �̷� :
 * ------------------------------------------------------------------------
 *  �ۼ���     �Ҽ�  		 ����      Tag   ����
 * ------------------------------------------------------------------------   
 * �ҵ���   ��ä��(��)  2011.06.08     %01%  �ű��ۼ�
 *  
 */
package com.uc.bs.cyber.service.kf203001;

import com.uc.bs.cyber.field.Comd_WorkKfField;
/**
 * @author Administrator
 *
 */
public class Kf203001FieldList extends Comd_WorkKfField {
			
	
	/**
	 * ������
	 * ȯ�氳���δ�� ���γ��� ������ȸ ��������
	 */
	public Kf203001FieldList() {
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
		 * ��������(������)
		 * */
		this.addRecvField("JUMINNO"         ,13      ,"C");     /*������ �ֹ�(�����)��Ϲ�ȣ*/ 
		this.addRecvField("IN_STDATE"       ,8       ,"H");     /*��ȸ ���� ����             */ 
		this.addRecvField("IN_ENDATE"       ,8       ,"H");     /*��ȸ ���� ����             */ 
		this.addRecvField("DATATOT"         ,3       ,"H");     /*���� �� �Ǽ�               */ 
		this.addRecvField("POINTNO"         ,3       ,"H");     /*���� ��ȣ                  */ 
		this.addRecvField("DATANUM"         ,2       ,"H");     /*������ �Ǽ�                */ 

		
		/*
		 * ��������(�ݺ�����)
		 * */
		this.addRepetField("PUBGCODE"       ,2       ,"H");     /*������ �з��ڵ�          */ 
		this.addRepetField("JIRONO"         ,7       ,"H");     /*���� ��ȣ                  */ 
		this.addRepetField("ETAXNO"         ,29      ,"C");     /*���ڳ��ι�ȣ               */ 
		this.addRepetField("GWAMOK"         ,6       ,"H");     /*����/����                  */ 
		this.addRepetField("GWADATE"        ,6       ,"H");     /*�⵵/���                  */ 
		this.addRepetField("KIBUN"          ,1       ,"H");     /*��ǥ ���� ����             */ 
		this.addRepetField("NAPAMT"         ,15      ,"H");     /*���� �ݾ�                  */ 
		this.addRepetField("NAPBU_DATE"     ,8       ,"H");     /*���� ����                  */ 
		this.addRepetField("NAPBU_SYS"      ,1       ,"C");     /*���� �̿� �ý���           */ 
		this.addRepetField("JIJUM_CODE"     ,7       ,"H");     /*�������� ���� �ڵ�         */ 


	}
	
	
}
