/**
 *  �ֽý��۸� : ���̹����漼û ��ȭ
 *  ��  ��  �� : ���� ���� �ۼ��� ����
 *  ��  ��  �� : ������-���ϼ��� ���γ��� ����ȸ
 *  Ŭ����  ID : Kf251001FieldList
 *  ����  �̷� :
 * ------------------------------------------------------------------------
 *  �ۼ���     �Ҽ�  		 ����      Tag   ����
 * ------------------------------------------------------------------------   
 * �ҵ���   ��ä��(��)  2011.06.08     %01%  �ű��ۼ�
 *  
 */
package com.uc.bs.cyber.service.kf253002;

import com.uc.bs.cyber.field.Comd_WorkKfField;
/**
 * @author Administrator
 *
 */
public class Kf253002FieldList extends Comd_WorkKfField {
			
	
	/**
	 * ������
	 * ���ϼ��� ���γ��� ������ȸ ��������
	 */
	public Kf253002FieldList() {
		// TODO Auto-generated constructor stub
		super();

		/*
		 * �۽�����(������)
		 * */
		this.addSendField("JUMINNO"            , 13      , "C");      /*�������ֹι�ȣ                                    */
		this.addSendField("CUSTNO"             , 30      , "C");      /*���밡��ȣ/���ڳ��ι�ȣ/������ȣ                  */
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
		this.addSendField("SNAP_BANK_CODE"     ,  7      , "H");      /*�������� ���� �ڵ�                                */
		this.addSendField("SNAP_SYMD"          , 14      , "H");      /*���� �Ͻ�                                         */
		this.addSendField("NAPGU_AMT"          , 10      , "H");      /*���� �ݾ�                                         */
		this.addSendField("OUT_ACCTNO"         , 16      , "C");      /*��ݰ��¹�ȣ                                      */
		this.addSendField("ETC1"               , 10      , "C");      /*���� ���� FIELD                                   */
		
		/*
		 * ��������(������) : �ۼ��������� �����ؼ�
		 * */
		this.addRecvFieldAll(sendField);

	}
	
	
}
