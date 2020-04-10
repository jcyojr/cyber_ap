/**
 *  �ֽý��۸� : ���̹����漼û ��ȭ
 *  ��  ��  �� : ���� ���� �ۼ��� ����
 *  ��  ��  �� : ������-ȯ�氳���δ�� ����ȸ �������� 
 *              
 *  Ŭ����  ID : Kf201001FieldList
 *  ����  �̷� :
 * ------------------------------------------------------------------------
 *  �ۼ���     �Ҽ�  		 ����      Tag   ����
 * ------------------------------------------------------------------------   
 * �ҵ���   ��ä��(��)  2011.06.08     %01%  �ű��ۼ�
 *  
 */
package com.uc.bs.cyber.service.kf201002;

import com.uc.bs.cyber.field.Comd_WorkKfField;

/**
 * @author Administrator
 *
 */
public class Kf201002FieldList extends Comd_WorkKfField {
				
	/**
	 * ������
	 * ȯ�氳���δ�� ����ȸ ��������
	 */
	public Kf201002FieldList() {
		// TODO Auto-generated constructor stub
		super();
		
		/*������ �ۼ��� ���� ���⼭�� �ۼ����� �����ϴ�.*/
		
		/*
		 * �۽�����(���̹�)
		 * */
		addSendField("ETAX_NO"              , 29 ,  "C"  );  /*���ڳ��ι�ȣ              */
		addSendField("JUMIN_NO"             , 13 ,  "C"  );  /*�ֹ�(�����,����)��Ϲ�ȣ */
		addSendField("SIDO"                 , 2  ,  "H"  );  /*�õ�                      */
		addSendField("GU_CODE"              , 3  ,  "H"  );  /*�������(�ñ���)          */
		addSendField("CONFIRM_NO1"          , 1  ,  "H"  );  /*������ȣ 1                */
		addSendField("HCALVAL"              , 2  ,  "H"  );  /*ȸ��                      */
		addSendField("GWA_MOK"              , 6  ,  "H"  );  /*����/����                 */
		addSendField("TAX_YYMM"             , 6  ,  "H"  );  /*�⵵/���                 */
		addSendField("KIBUN"                , 1  ,  "H"  );  /*����                      */
		addSendField("DONG_CODE"            , 3  ,  "H"  );  /*������(���鵿)            */
		addSendField("GWASE_NO"             , 6  ,  "H"  );  /*���� ��ȣ                 */
		addSendField("CONFIRM_NO2"          , 1  ,  "H"  );  /*������ȣ 2                */
		addSendField("NAP_NAME"             , 40 ,  "C"  );  /*������ ����               */
		addSendField("NAP_BFAMT"            , 15 ,  "H"  );  /*���⳻ �ݾ�               */
		addSendField("NAP_AFAMT"            , 15 ,  "H"  );  /*������ �ݾ�               */
	    addSendField("CONFIRM_NO3"          , 1  ,  "H"  );  /*������ȣ 3                */
	    addSendField("GWASE_RULE"           , 15 ,  "H"  );  /*���� ǥ��                 */
	    addSendField("BONSE"                , 15 ,  "H"  );  /*�δ��                    */
	    addSendField("BONSE_ADD"            , 15 ,  "H"  );  /*�δ�� �����             */
	    addSendField("DOSISE"               , 15 ,  "H"  );  /*�̼� �δ��               */
	    addSendField("DOSISE_ADD"           , 15 ,  "H"  );  /*�̼� �δ�� �����        */
	    addSendField("GONGDONGSE"           , 15 ,  "H"  );  /*���� ���� FIELD 1         */
	    addSendField("GONGDONGSE_ADD"       , 15 ,  "H"  );  /*���� ���� FIELD 2         */
	    addSendField("EDUSE"                , 15 ,  "H"  );  /*���� ���� FIELD 3         */
	    addSendField("EDUSE_ADD"            , 15 ,  "H"  );  /*���� ���� FIELD 4         */
	    addSendField("NAP_BFDATE"           , 8  ,  "H"  );  /*������ (���⳻)           */
	    addSendField("NAP_AFDATE"           , 8  ,  "H"  );  /*������ (������)           */
	    addSendField("CONFIRM_NO4"          , 1  ,  "H"  );  /*������ȣ 4                */
	    addSendField("FILLER1"              , 1  ,  "H"  );  /*�ʷ�                      */
	    addSendField("CONFIRM_NO5"          , 1  ,  "H"  );  /*������ȣ 5                */
	    addSendField("GWASE_DESC"           , 60 ,  "C"  );  /*���� ����                 */
	    addSendField("GWASE_PUB_DESC"       , 20 ,  "C"  );  /*���� ǥ�� ����            */
	    addSendField("GOJICR_DATE"          , 8  ,  "H"  );  /*�����ڷ� �߻�����         */
	    addSendField("JADONG_YN"            , 1  ,  "C"  );  /*�ڵ���ü ��� ����        */
	    addSendField("JIJUM_CODE"           , 7  ,  "H"  );  /*�������� ���� �ڵ�        */
	    addSendField("NAPBU_DATE"           , 14 ,  "H"  );  /*���� �Ͻ�                 */
	    addSendField("NP_BAF_GUBUN"         , 1  ,  "C"  );  /*���� ���� ����            */
	    addSendField("TAX_GOGI_GUBUN"       , 1  ,  "C"  );  /*���� ���� ����            */
	    addSendField("JA_GOGI_GUBUN"        , 1  ,  "C"  );  /*��ǥ ���� ����            */
	    addSendField("RESERV2"              , 3  ,  "C"  );  /*���� ���� FIELD 5         */

		/*
		 * ��������(������)
		 * �ۼ��������� �����ϹǷ� �۽������� ���������� ��ü��Ŵ
		 * */
	    
	    addRecvField("ETAX_NO"              , 29 ,  "C"  );  /*���ڳ��ι�ȣ              */
	    addRecvField("JUMIN_NO"             , 13 ,  "C"  );  /*�ֹ�(�����,����)��Ϲ�ȣ */
	    
	    	    
	}
	
	
}
