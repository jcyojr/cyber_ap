/**
 *  �ֽý��۸� : ���̹����漼û ��ȭ
 *  ��  ��  �� : ���� ���� �ۼ��� ����
 *  ��  ��  �� : ������-ȯ�氳���δ�� ���γ��� ����ȸ 
 *              
 *  Ŭ����  ID : Kf203001FieldList
 *  ����  �̷� :
 * ------------------------------------------------------------------------
 *  �ۼ���     �Ҽ�  		 ����      Tag   ����
 * ------------------------------------------------------------------------   
 * �ҵ���   ��ä��(��)  2011.06.08     %01%  �ű��ۼ�
 *  
 */
package com.uc.bs.cyber.service.kf203002;

import com.uc.bs.cyber.field.Comd_WorkKfField;
/**
 * @author Administrator
 *
 */
public class Kf203002FieldList extends Comd_WorkKfField {
			
	/**
	 * ������
	 * ȯ�氳���δ�� ���γ��� ����ȸ ��������
	 */
	public Kf203002FieldList() {
		// TODO Auto-generated constructor stub
		super();
		
		/*
		 * �۽�����(������)
		 * */
		this.addSendField("ETAXNO"            ,29         ,"C");            
		this.addSendField("NAPBU_JUMIN_NO"    ,13         ,"C");
		this.addSendField("JUMINNO"           ,13         ,"C");
		this.addSendField("SIDO"              ,2          ,"H");
		this.addSendField("GU_CODE"           ,3          ,"H");
		this.addSendField("CONFIRM_NO1"       ,1          ,"H");
		this.addSendField("HCALVAL"           ,2          ,"H");
		this.addSendField("GWAMOK"            ,6          ,"H");
		this.addSendField("TAX_YYMM"          ,6          ,"H");
		this.addSendField("KIBUN"             ,1          ,"H");
		this.addSendField("DONG_CODE"         ,3          ,"H");
		this.addSendField("GWASE_NO"          ,6          ,"H");
		this.addSendField("CONFIRM_NO2"       ,1          ,"H");
		this.addSendField("NAPBU_NAME"        ,40         ,"C");
		this.addSendField("NAP_BFAMT"         ,15         ,"H");
		this.addSendField("NAP_AFAMT"         ,15         ,"H");
		this.addSendField("CONFIRM_NO3"       ,1          ,"H");
		this.addSendField("GWASE_POINT"       ,15         ,"H");
		this.addSendField("BONSE"             ,15         ,"H");
		this.addSendField("BONSE_ADD"         ,15         ,"H");
		this.addSendField("DOSISE"            ,15         ,"H");
		this.addSendField("DOSISE_ADD"        ,15         ,"H");
		this.addSendField("GONGDONGSE"        ,15         ,"H");
		this.addSendField("GONGDONGSE_ADD"    ,15         ,"H");
		this.addSendField("EDUSE"             ,15         ,"H");
		this.addSendField("EDUSE_ADD"         ,15         ,"H");
		this.addSendField("NAP_BFDATE"        ,8          ,"H");
		this.addSendField("NAP_AFDATE"        ,8          ,"H");
		this.addSendField("CONFIRM_NO4"       ,1          ,"H");
		this.addSendField("FILLER1"           ,1          ,"H");
		this.addSendField("CONFIRM_NO5"       ,1          ,"H");
		this.addSendField("GWASE_DESC"        ,80         ,"C");
		this.addSendField("GOJI_DATE"         ,8          ,"H");
		this.addSendField("NAPBU_SYS"         ,1          ,"C");
		this.addSendField("JIJUM_CODE"        ,7          ,"H");
		this.addSendField("NAPBU_DATE"        ,14         ,"H");
		this.addSendField("NAPBU_AMT"         ,15         ,"H");
		this.addSendField("OUTACCT_NO"        ,16         ,"C");
		this.addSendField("RESERVED2"         ,10         ,"C");

		/*
		 * ��������(������)
		 * �ۼ��������� �����ϹǷ� �۽������� ���������� ��ü��Ŵ
		 * */
	    this.addRecvFieldAll(sendField);
	}
	
	
}
