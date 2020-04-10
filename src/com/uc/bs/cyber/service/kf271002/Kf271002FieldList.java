/**
 *  �ֽý��۸� : ���̹����漼û ��ȭ
 *  ��  ��  �� : ���� ���� �ۼ��� ����
 *  ��  ��  �� : ������-���ܼ��� ����ȸ �������� 
 *              
 *  Ŭ����  ID : Kf271002FieldList
 *  ����  �̷� :
 * ------------------------------------------------------------------------
 *  �ۼ���     �Ҽ�  		 ����      Tag   ����
 * ------------------------------------------------------------------------   
 * �ҵ���   ��ä��(��)  2011.06.08     %01%  �ű��ۼ�
 *  
 */
package com.uc.bs.cyber.service.kf271002;

import com.uc.bs.cyber.field.Comd_WorkKfField;

/**
 * @author Administrator
 *
 */
public class Kf271002FieldList extends Comd_WorkKfField {
				
	/**
	 * ������
	 * ���ܼ��� ����ȸ ��������
	 */
	public Kf271002FieldList() {
		// TODO Auto-generated constructor stub
		super();
		
		/*������ �ۼ��� ���� ���⼭�� �ۼ����� �����ϴ�.*/
		
		/*
		 * ��������(������)
		 * */
		addRecvField("S_GBN"                , 1  ,  "C"  );  /*��ȸ���� (G/E)            */
		addRecvField("TAX_NO"               , 32 ,  "C"  );  /*���ι�ȣ	                 */
		addRecvField("GRNO"                 , 19 ,  "C"  );  /*���ڳ��ι�ȣ              */
		addRecvField("JUMIN_NO"             , 13 ,  "C"  );  /*�ֹ�(�����,����)��Ϲ�ȣ */		
		
		/*
		 * �۽�����(���̹�)
		 * */
		addSendField("S_GBN"              	, 1  ,  "C"  );  /*��ȸ���� (G/E)            */
		addSendField("TAX_NO"               , 32 ,  "C"  );  /*���ι�ȣ	                 */
		addSendField("GRNO"              	, 19 ,  "C"  );  /*���ڳ��ι�ȣ              */
		addSendField("JUMIN_NO"             , 13 ,  "C"  );  /*�ֹ�(�����,����)��Ϲ�ȣ */
		addSendField("FIELD1"               , 2  ,  "C"  );  /*���� ���� FIELD 1         */
		addSendField("BUGWA_GB"             , 1  ,  "H"  );  /*�������� 		         */
		addSendField("SEMOK_CD"          	, 6  ,  "C"  );  /*����/����                 */
		addSendField("SEMOK_NM"             , 50 ,  "C"  );  /*����/�����               */
		addSendField("GBN"              	, 1  ,  "C"  );  /*����/����                 */
		addSendField("OCR_BD"             	, 108,  "C"  );  /*�⵵/���                 */
		addSendField("NAP_NAME"             , 40 ,  "C"  );  /*������ ����               */
		addSendField("NAP_BFAMT"            , 15 ,  "H"  );  /*���⳻ �ݾ�               */
		addSendField("NAP_AFAMT"            , 15 ,  "H"  );  /*������ �ݾ�               */
	    addSendField("GUKAMT"          		, 11 ,  "H"  );  /*���� 	                 */
	    addSendField("GUKAMT_ADD"           , 11 ,  "H"  );  /*���� �����               */
	    addSendField("SIDO_AMT"             , 11 ,  "H"  );  /*�õ���                    */
	    addSendField("SIDO_AMT_ADD"         , 11 ,  "H"  );  /*�õ��� �����             */
	    addSendField("SIGUNGU_AMT"          , 11 ,  "H"  );  /*�ñ�����      	         */
	    addSendField("SIGUNGU_AMT_ADD"      , 11 ,  "H"  );  /*�ñ����� �����   	     */
	    addSendField("BUNAP_AMT"            , 11 ,  "H"  );  /*�г�����/���             */
	    addSendField("BUNAP_AMT_ADD"        , 11 ,  "H"  );  /*�г�����/��� �����      */
	    addSendField("FIELD2"               , 11 ,  "C"  );  /*���� ���� FIELD 2         */
	    addSendField("NAP_BFDATE"           , 8  ,  "H"  );  /*������ (���⳻)           */
	    addSendField("NAP_AFDATE"           , 8  ,  "H"  );  /*������ (������)           */
	    addSendField("GWASE_ITEM"           , 100,  "C"  );  /*�������                  */
	    addSendField("BUGWA_TAB"          	, 150,  "C"  );  /*�ΰ�����                  */
	    addSendField("GOJI_DATE"   		    , 8  ,  "H"  );  /*�����ڷ� �߻�����         */
	    addSendField("OUTO_ICHE_GB"         , 1  ,  "C"  );  /*�ڵ���ü��Ͽ���          */
	    addSendField("SUNAB_BANK_CD"        , 7  ,  "H"  );  /*�������� ���� �ڵ�        */
	    addSendField("RECIP_DATE"           , 14 ,  "H"  );  /*�����Ͻ�			         */
	    addSendField("NAPGI_BA_GB"          , 1  ,  "C"  );  /*���⳻�ı���              */
	    addSendField("FILED3"          		, 15 ,  "C"  );  /*�������� FIELD 3          */

  
	}
	
	
}
