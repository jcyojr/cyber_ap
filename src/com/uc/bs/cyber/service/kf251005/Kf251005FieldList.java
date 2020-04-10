/**
 *  �ֽý��۸� : ���̹����漼û ��ȭ
 *  ��  ��  �� : ���� ���� �ۼ��� ����
 *  ��  ��  �� : ������-���ϼ����������� ������ȸ 2
 *               ����) ������ȸ���� ���ڳ��ι�ȣ�� �߰���.
 *  Ŭ����  ID : Kf251001FieldList
 *  ����  �̷� :
 * ------------------------------------------------------------------------
 *  �ۼ���     �Ҽ�  		 ����      Tag   ����
 * ------------------------------------------------------------------------   
 * �ҵ���   ��ä��(��)  2011.06.08     %01%  �ű��ۼ�
 *  
 */
package com.uc.bs.cyber.service.kf251005;

import com.uc.bs.cyber.field.Comd_WorkKfField;
/**
 * @author Administrator
 *
 */
public class Kf251005FieldList extends Comd_WorkKfField {
			
	
	/**
	 * ������
	 * ���ϼ��� ���γ��� ������ȸ ��������
	 */
	public Kf251005FieldList() {
		// TODO Auto-generated constructor stub
		super();

		/*
		 * �۽�����(������)
		 * */
		this.addSendField("SEARCHGUBUN"         , 1       ,"C");  /*��ȸ ����('S')*/  
		this.addSendField("SEARCHKEY"           ,30       ,"C");  /*��ȸ ��ȣ     */
		this.addSendField("CUSTNO"              ,30       ,"C");  /*���밡��ȣ    */
		this.addSendField("DATATOT"             , 3       ,"H");  /*���� �� �Ǽ�  */
		this.addSendField("POINTNO"             , 3       ,"H");  /*���� ��ȣ     */
		this.addSendField("DATANUM"             , 2       ,"H");  /*������ �Ǽ�   */
		
		/*
		 * ��������(������) : �ۼ��������� �����ؼ�
		 * */
		this.addRecvFieldAll(sendField);
		
		/*
		 * ��������(�ݺ�����)
		 * */
		this.addRepetField("DANGGUBUN"          , 1       ,"C");  /*��� ����         */
		this.addRepetField("BYYYMM"             , 6       ,"H");  /*�ΰ� ���         */
		this.addRepetField("NAPAMT"             ,10       ,"H");  /*���� �ݾ�         */
		this.addRepetField("NAPGUBUN"           , 1       ,"C");  /*���� ���� ����    */
		this.addRepetField("NAPDATE"            , 8       ,"H");  /*���� ����         */
		this.addRepetField("AUTOREG"            , 1       ,"C");  /*�ڵ���ü ��� ����*/
		this.addRepetField("ICHENO"             ,29       ,"C");  /*���ڳ��ι�ȣ/������ȣ*/

	}
	
	
}
