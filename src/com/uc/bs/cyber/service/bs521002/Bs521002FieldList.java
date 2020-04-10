/**
 *  �ֽý��۸� : ���̹����漼û ��ȭ
 *  ��  ��  �� : ���� ���� �ۼ��� ����
 *  ��  ��  �� : �λ�����- ���漼  �������� ����ȸ - ��� ��������
 *              
 *  Ŭ����  ID : Bs521002FieldList
 *  ����  �̷� :
 * ------------------------------------------------------------------------
 *  �ۼ���     �Ҽ�  		 ����      Tag   ����
 * ------------------------------------------------------------------------   
 * Ȳ����   �ٻ�(��)  2011.06.08     %01%  �ű��ۼ�
 *  
 */
package com.uc.bs.cyber.service.bs521002;

import com.uc.bs.cyber.field.Comd_WorkKfField;

/**
 * @author Administrator
 *
 */
public class Bs521002FieldList extends Comd_WorkKfField {
			
	
	/**
	 * ������
	 * ���漼 ������ȸ��� ��������
	 */
	public Bs521002FieldList() {
		// TODO Auto-generated constructor stub
		super();
				
		/*
		 * �������) �������� ��� �ۼ��������� �����ϴ�.
		 *           �ݺ������� �ִ� ��� �ݺ��Ǽ��� Field �� 'DATA_NUM'���� ���Ͻ�Ų��.
		 * */
		
		/*
		 * �۽�����(���̹�)
		 * */
	    	
		addSendField("TAXNO"              	, 29 ,  "C"  );  /*���ι�ȣ                  */
		addSendField("SEQNO"             	, 5  ,  "H"  );  /*�г�����					 */	
		addSendField("JUMIN_NO"             , 13 ,  "C"  );  /*�ֹ�(�����,����)��Ϲ�ȣ */
		addSendField("SIDO"               	, 2  ,  "H"  );  /*�õ�				         */
		addSendField("BUGWA_GIGWAN"         , 3  ,  "H"  );  /*�ΰ���� 		         */
		addSendField("CHK1"		            , 1  ,  "H"  );  /*������ȣ1 		         */
		addSendField("ACC_CD"	            , 2  ,  "H"  );  /*ȸ�� 			         */
		addSendField("SEMOK_CD"          	, 6  ,  "H"  );  /*����/����                 */
		addSendField("ACC_YM"              	, 6  ,  "H"  );  /*�������                  */
		addSendField("GIBN"              	, 1  ,  "H"  );  /*���	                     */
		addSendField("DONG_CD"             	, 3  ,  "H"  );  /*������                    */
		addSendField("TAX_SNO"             	, 6  ,  "H"  );  /*������ȣ                  */
		addSendField("CHK2"	                , 1  ,  "H"  );  /*������ȣ2                 */
		addSendField("REG_NM"	            , 40 ,  "C"  );  /*�����ڼ���                */
		addSendField("NAP_BFAMT"            , 15 ,  "H"  );  /*���⳻ �ݾ�               */
		addSendField("NAP_AFAMT"            , 15 ,  "H"  );  /*������ �ݾ�               */
		addSendField("CHK3"	                , 1  ,  "H"  );  /*������ȣ3                 */
	    addSendField("KWA_AMT"         		, 15 ,  "H"  );  /*����ǥ��                  */
	    addSendField("MNTX"                 , 15 ,  "H"  );  /*����		                 */
	    addSendField("MNTX_ADTX"            , 15 ,  "H"  );  /*���������	             */
	    addSendField("CPTX"                 , 15 ,  "H"  );  /*���ð�ȹ��                */
	    addSendField("CPTX_ADTX"            , 15 ,  "H"  );  /*���ð�ȹ�� �����	     */
	    addSendField("CFTX"                 , 15 ,  "H"  );  /*�����ü���/��Ư��         */
	    addSendField("CFTX_ADTX"            , 15 ,  "H"  );  /*�����ü���/��Ư�� �����	 */
	    addSendField("LETX"                 , 15 ,  "H"  );  /*������                    */
	    addSendField("LETX_ADTX"            , 15 ,  "H"  );  /*�����������	             */	  
	    addSendField("DUE_DT"               , 8  ,  "H"  );  /*������(���⳻)            */
	    addSendField("DUE_F_DT"             , 8  ,  "H"  );  /*������(������)            */
	    addSendField("CHK4"	                , 1  ,  "H"  );  /*������ȣ4                 */
	    addSendField("FILLER"               , 1  ,  "H"  );  /*�ʷ�                      */
	    addSendField("CHK5"                 , 1  ,  "H"  );  /*������ȣ5                 */
	    addSendField("MLGN"                 , 80 ,  "C"  );  /*��������                  */
	    addSendField("GIGI_DATE"            , 8  ,  "H"  );  /*�������� 	             */
	    addSendField("AUTO_TRNF_YN"         , 1  ,  "C"  );  /*�ڵ���ü��Ͽ���          */
	    addSendField("BANK_CD"         		, 7  ,  "H"  );  /*�������������ڵ�          */
	    addSendField("RECIP_DATE"           , 14 ,  "H"  );  /*�����Ͻ�		             */
	    addSendField("NABGI_BA_GBN"         , 1  ,  "C"  );  /*���� ���� ����            */
	    addSendField("FIELD2"         		, 5  ,  "H"  );  /*�������� FIELD 2          */
	    
	    /*
	    35043010600120110616601081077
	    00000
	    8111271111320
	    26
	    350
	    4
	    30106001
	    201106
	    1
	    660
	    1081077
	    ������                                  
	    000000000074810
	    000000000077040
	    8
	    000000000115115
	    000000000057550
	    000000000000000
	    000000000000000
	    000000000000000
	    000000000000000
	    000000000000000
	    000000000017260
	    000000000000000
	    20110630
	    20110801
	    900�λ�31��9560(�񿵾���,��ⷮ:1495)[���ɰ氨��:45%]
	                                20110608
	    N
	    0000000
	    00000000000000
	    B
	    00000
	    */

		/*
		 * ��������(������)
		 * �ۼ��������� �����ϹǷ� �۽������� ���������� ��ü��Ŵ
		 * */
	    this.addRecvFieldAll(sendField);		

	}
}

