/**
 *  �ֽý��۸� : ���̹����漼û ��ȭ
 *  ��  ��  �� : ���� ���� �ۼ��� ����
 *  ��  ��  �� : �λ�����-���ܼ��� ���γ��� ����ȸ 
 *              
 *  Ŭ����  ID : kf273002FieldList
 *  ����  �̷� :
 * ------------------------------------------------------------------------
 *  �ۼ���     �Ҽ�  		 ����      Tag   ����
 * ------------------------------------------------------------------------   
 * Ȳ����   �ٻ�(��)  2011.06.08     %01%  �ű��ۼ�
 *  
 */
package com.uc.bs.cyber.service.kf273002;

import com.uc.bs.cyber.field.Comd_WorkKfField;
/**
 * @author Administrator
 *
 */
public class Kf273002FieldList extends Comd_WorkKfField {
			
	
	/**
	 * ������
	 * ���ܼ��� ���γ��� ����ȸ ��������
	 */
	public Kf273002FieldList() {
		// TODO Auto-generated constructor stub
		super();

		/*������ �ۼ��� ���� ���⼭�� �ۼ����� �����ϴ�.*/
		
		/*
		 * �۽�����(���̹�)
		 * */	
		addSendField("GB"  		           	, 1  ,  "C"  );  /*��ȸ���� */
	    addSendField("ETAXNO"             	, 32 ,  "C"  );  /*���ι�ȣ */
	    addSendField("EPAYNO"             	, 19 ,  "C"  );  /*���ڳ��ι�ȣ				 */
	    addSendField("NAPBU_JUMIN_NO"       , 13 ,  "C"  );  /*�ֹ�(�����,����)��Ϲ�ȣ */
 		addSendField("JUMIN_NO"             , 13 ,  "C"  );  /*�ֹ�(�����,����)��Ϲ�ȣ */
 		addSendField("FIELD1"               , 2  ,  "C"  );  /*�������� FIELD 1          */
 		addSendField("BUGWA_STAT"          	, 1  ,  "H"  );  /*��������        	         */
 		addSendField("GWA_MOK"              , 6  ,  "H"  );  /*����/����                 */
 		addSendField("GWA_NM"             	, 50 ,  "C"  );  /*����/�����               */
 		addSendField("FIELD2"               , 1  ,  "C"  );  /*�������� FIELD 2          */
 		addSendField("OCR_BD"             	, 108,  "C"  );  /*OCR���                   */
 		addSendField("NAP_NAME"             , 40 ,  "C"  );  /*������ ����               */
 		addSendField("NAP_BFAMT"            , 15 ,  "H"  );  /*���⳻ �ݾ�               */
 		addSendField("NAP_AFAMT"            , 15 ,  "H"  );  /*������ �ݾ�               */
	    addSendField("NATN_TAX"             , 11 ,  "H"  );  /*����		                 */
	    addSendField("NATN_TAX_ADD"         , 11 ,  "H"  );  /*���������	             */
	    addSendField("SIDO_TAX"             , 11 ,  "H"  );  /*�õ���  	                 */
	    addSendField("SIDO_TAX_ADD"         , 11 ,  "H"  );  /*�õ��� �����		     */
	    addSendField("SIGUNGU_TAX"          , 11 ,  "H"  );  /*�ñ�����         		 */
	    addSendField("SIGUNGU_TAX_ADD"      , 11 ,  "H"  );  /*�ñ����� �����	 		 */
	    addSendField("BUN_AMT"              , 11 ,  "H"  );  /*�г�����/���             */
	    addSendField("BUN_AMT_ADD"          , 15 ,  "H"  );  /*�г�����/��� �����      */	  
	    addSendField("FIELD3"               , 11 ,  "C"  );  /*�������� FIELD 3          */
 	    addSendField("DUE_DT"           	, 8  ,  "H"  );  /*������ (���⳻)           */
 	    addSendField("DUE_F_DT"           	, 8  ,  "H"  );  /*������ (������)           */
 	    addSendField("TAX_GDS"         		, 100,  "C"  );  /*���� ���                 */
 	    addSendField("TAX_NOTICE_TITLE" 	, 150,  "C"  );  /*�ΰ� ����                 */
 	    addSendField("LEVY_DETAIL1"         , 8  ,  "H"  );  /*�����ڷ� �߻�����         */
 	    addSendField("NAPBU_SYS"            , 1  ,  "C"  );  /*�����̿� �ý���           */
 	    addSendField("BANK_CD"              , 7  ,  "H"  );  /*�������� ���� �ڵ�        */
 	    addSendField("RECIP_DATE"           , 14 ,  "H"  );  /*���� �Ͻ�                 */
 	    addSendField("RECIP_AMT"            , 15 ,  "H"  );  /*���� �ݾ�                 */
 	    addSendField("OUTACCT_NO "          , 16 ,  "C"  );  /*��ݰ��¹�ȣ              ?*/
 	    addSendField("FIELD4"               , 15 ,  "C"  );  /*���� ���� FIELD 4         ?*/	


 	                                                      
		/*
		 * ��������(������)
		 * �ۼ��������� �����ϹǷ� �۽������� ���������� ��ü��Ŵ
		 * */
	    this.addRecvFieldAll(sendField);
        
	}
	
	
}
