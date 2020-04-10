/**
 *  �ֽý��۸� : ���̹����漼û ��ȭ
 *  ��  ��  �� : ���� ���� �ۼ��� ����
 *  ��  ��  �� : ������-���ϼ����������� ����ȸ 
 *              
 *  Ŭ����  ID : Kf251002FieldList
 *  ����  �̷� :
 * ------------------------------------------------------------------------
 *  �ۼ���     �Ҽ�  		 ����      Tag   ����
 * ------------------------------------------------------------------------   
 * �ҵ���   ��ä��(��)  2011.06.08     %01%  �ű��ۼ�
 *  
 */
package com.uc.bs.cyber.service.kf251002;

import com.uc.bs.cyber.field.Comd_WorkKfField;
/**
 * @author Administrator
 *
 */
public class Kf251002FieldList extends Comd_WorkKfField {
			
	
	/**
	 * ������
	 * ���ϼ��� ���γ��� ����ȸ ��������
	 */
	public Kf251002FieldList() {
		// TODO Auto-generated constructor stub
		super();

		/*
		 * �۽�����(������)
		 * */
		this.addSendField("SEARCHGUBUN"        ,  1      , "C");      /*��ȸ ����('S'/'E')                                */
		this.addSendField("SEARCHKEY"          , 30      , "C");      /*���밡��ȣ/���ڳ��ι�ȣ                           */
		this.addSendField("BYYYMM"             ,  6      , "H");      /*�ΰ� ���                                         */
		this.addSendField("DANGGUBUN"          ,  1      , "C");      /*��� ����                                         */
		this.addSendField("NPNO"               , 10      , "C");      /*���� ��ȣ                                         */
		this.addSendField("NAME"               , 20      , "C");      /*����                                              */
		this.addSendField("BNAPDATE"           ,  8      , "H");      /*���⳻ ������              | ���� ������          */
		this.addSendField("BNAPAMT"            , 10      , "H");      /*���⳻ �ݾ�                | ü����               */
		this.addSendField("ANAPAMT"            , 10      , "H");      /*������ �ݾ�                                       */
		this.addSendField("GUM2"               ,  1      , "H");      /*������ȣ 2                                        */
		this.addSendField("BSAMT"              , 10      , "H");      /*��������⳻�ݾ�           |  ����� ü����       */
		this.addSendField("BHAMT"              , 10      , "H");      /*�ϼ������⳻�ݾ�           |  �ϼ��� ü����       */
		this.addSendField("BGAMT"              , 10      , "H");      /*���ϼ����⳻�ݾ�           |  ���ϼ� ü����       */
		this.addSendField("BMAMT"              , 10      , "H");      /*���̿�δ�ݳ��⳻�ݾ�     |  ���̿�δ��ü����  */
		this.addSendField("ASAMT"              , 10      , "H");      /*����������ıݾ�                                  */
		this.addSendField("AHAMT"              , 10      , "H");      /*�ϼ��������ıݾ�                                  */
		this.addSendField("AGAMT"              , 10      , "H");      /*���ϼ������ıݾ�                                  */
		this.addSendField("AMAMT"              , 10      , "H");      /*���̿�δ�ݳ����ıݾ�                            */
		this.addSendField("ANAPDATE"           ,  8      , "H");      /*������ ������                                     */
		this.addSendField("GUM3"               ,  1      , "H");      /*������ȣ 3                                        */
		this.addSendField("CNAPTERM"           , 16      , "H");      /*ü�� �Ⱓ                                         */
		this.addSendField("ADDR"               , 60      , "C");      /*�ּ�                                              */
		this.addSendField("USETERM"            , 16      , "H");      /*��� �Ⱓ                                         */
		this.addSendField("AUTOREG"            ,  1      , "C");      /*�ڵ���ü ��� ����                                */
		this.addSendField("SNAP_BANK_CODE"     ,  7      , "H");      /*�������� ���� �ڵ�                                */
		this.addSendField("SNAP_SYMD"          , 14      , "H");      /*���� �Ͻ�                                         */
		this.addSendField("NAPGUBUN"           ,  1      , "C");      /*���� ���� ����                                    */
		this.addSendField("ETC1"               ,  9      , "C");      /*���� ���� FIELD                                   */
		this.addSendField("CUST_ADMIN_NUM"     , 30      , "C");      /*��������ȣ                                      */
		this.addSendField("OCR"                ,108      , "C");      /*OCR BAND                                          */
		
		/*
		 * ��������(������) : �۽������� 138����Ʈ�� ������ �´�...������ �̳� Ʋ����.
		 * */
		
		this.addRecvField("SEARCHGUBUN"        ,  1      , "C");      /*��ȸ ����('S'/'E')                                */
		this.addRecvField("SEARCHKEY"          , 30      , "C");      /*���밡��ȣ/���ڳ��ι�ȣ                           */
		this.addRecvField("BYYYMM"             ,  6      , "H");      /*�ΰ� ���                                         */
		this.addRecvField("DANGGUBUN"          ,  1      , "C");      /*��� ����                                         */


	}
	
	
}
