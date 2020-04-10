/**
 *  �ֽý��۸� : ���̹����漼û ��ȭ
 *  ��  ��  �� : ���� ���� �ۼ��� ����
 *  ��  ��  �� : �λ�����-���漼 ���γ��� ������ȸ 
 *              
 *  Ŭ����  ID : Bs523002FieldList
 *  ����  �̷� :
 * ------------------------------------------------------------------------
 *  �ۼ���     �Ҽ�  		 ����      Tag   ����
 * ------------------------------------------------------------------------   
 * Ȳ����   �ٻ�(��)  2011.06.08     %01%  �ű��ۼ�
 *  
 */
package com.uc.bs.cyber.service.bs523002;

import com.uc.bs.cyber.field.Comd_WorkKfField;
/**
 * @author Administrator
 *
 */
public class Bs523002FieldList extends Comd_WorkKfField {
			
	
	/**
	 * ������
	 * ���漼 ���γ��� ������ȸ ��������
	 */
	public Bs523002FieldList() {
		// TODO Auto-generated constructor stub
		super();

		/*������ �ۼ��� ���� ���⼭�� �ۼ����� �����ϴ�.*/
		
		/*
		 * �۽�����(���̹�)
		 * */	
	    addSendField("ETAXNO"             	, 29 ,  "C"  );  /*���ι�ȣ */
	    addSendField("SEQNO"             	, 5  ,  "H"  );  /*�г�����					 */
	    addSendField("NAPBU_JUMIN_NO"       , 13 ,  "C"  );  /*�ֹ�(�����,����)��Ϲ�ȣ */
 		addSendField("JUMIN_NO"             , 13 ,  "C"  );  /*�ֹ�(�����,����)��Ϲ�ȣ */
 		addSendField("SIDO"                 , 2  ,  "H"  );  /*�õ�                      */
 		addSendField("GU_CODE"              , 3  ,  "H"  );  /*�������(�ñ���)          */
 		addSendField("CHK1"          		, 1  ,  "H"  );  /*������ȣ 1                */
 		addSendField("HCALVAL"              , 2  ,  "H"  );  /*ȸ��                      */
 		addSendField("GWA_MOK"              , 6  ,  "H"  );  /*����/����                 */
 		addSendField("TAX_YYMM"             , 6  ,  "H"  );  /*�⵵/���                 */
 		addSendField("KIBUN"                , 1  ,  "H"  );  /*����                      */
 		addSendField("DONG_CODE"            , 3  ,  "H"  );  /*������(���鵿)            */
 		addSendField("GWASE_NO"             , 6  ,  "H"  );  /*���� ��ȣ                 */
 		addSendField("CHK2"          		, 1  ,  "H"  );  /*������ȣ 2                */
 		addSendField("NAP_NAME"             , 40 ,  "C"  );  /*������ ����               */
 		addSendField("NAP_BFAMT"            , 15 ,  "H"  );  /*���⳻ �ݾ�               */
 		addSendField("NAP_AFAMT"            , 15 ,  "H"  );  /*������ �ݾ�               */
 	    addSendField("CHK3"		            , 1  ,  "H"  );  /*������ȣ 3                */
 	    addSendField("KWA_AMT"              , 15 ,  "H"  );  /*���� ǥ��                 */
	    addSendField("MNTX"                 , 15 ,  "H"  );  /*����		                 */
	    addSendField("MNTX_ADTX"            , 15 ,  "H"  );  /*���������	             */
	    addSendField("CPTX"                 , 15 ,  "H"  );  /*���ð�ȹ��                */
	    addSendField("CPTX_ADTX"            , 15 ,  "H"  );  /*���ð�ȹ�� �����	     */
	    addSendField("CFTX"                 , 15 ,  "H"  );  /*�����ü���/��Ư��         */
	    addSendField("CFTX_ADTX"            , 15 ,  "H"  );  /*�����ü���/��Ư�� �����	 */
	    addSendField("LETX"                 , 15 ,  "H"  );  /*������                    */
	    addSendField("LETX_ADTX"            , 15 ,  "H"  );  /*�����������	             */	  
 	    addSendField("DUE_DT"           	, 8  ,  "H"  );  /*������ (���⳻)           */
 	    addSendField("DUE_F_DT"           	, 8  ,  "H"  );  /*������ (������)           */
 	    addSendField("CHK4"          		, 1  ,  "H"  );  /*������ȣ 4                */
 	    addSendField("FILLER"               , 1  ,  "H"  );  /*�ʷ�                      */
 	    addSendField("CHK5"          		, 1  ,  "H"  );  /*������ȣ 5                */
 	    addSendField("MLGN"           		, 80 ,  "C"  );  /*���� ����                 */
 	    addSendField("GOJICR_DATE"          , 8  ,  "H"  );  /*�����ڷ� �߻�����         */
 	    addSendField("NAPBU_SYS"            , 1  ,  "C"  );  /*�����̿� �ý���           */
 	    addSendField("BANK_CD"              , 7  ,  "H"  );  /*�������� ���� �ڵ�        */
 	    addSendField("RECIP_DATE"           , 14 ,  "H"  );  /*���� �Ͻ�                 */
 	    addSendField("RECIP_AMT"            , 15 ,  "H"  );  /*���� �ݾ�                 */
 	    addSendField("OUTACCT_NO "          , 16 ,  "C"  );  /*��ݰ��¹�ȣ              */
 	    addSendField("FIELD2"               , 10 ,  "C"  );  /*���� ���� FIELD 5         */	

 	    /*
 	   44063010600120110615400045002
 	   00000
 	   7508222122429
 	   7508222122429
 	   26
 	   440
 	   6
 	   30
 	   106001
 	   201106
 	   1
 	   540
 	   004500
 	   2
 	   �豸��                                  
 	   000000000194800
 	   000000000200630
 	   3
 	   000000000299700
 	   000000000149850
 	   000000000000000
 	   000000000000000
 	   000000000000000
 	   000000000000000
 	   000000000000000
 	   000000000044950
 	   000000000000000
 	   20110630
 	   20110801
 	   8
 	   0
 	   0
 	   299700-(2011.01.01~2011.06.30)                                                  
 	   20110607
 	   B
 	   0329990
 	   20110622073302
 	   000000000194800 
 	   '                          ' ---> ��ĭ ��¿����
 	   */
 	                                                      
		/*
		 * ��������(������)
		 * �ۼ��������� �����ϹǷ� �۽������� ���������� ��ü��Ŵ
		 * */
	    this.addRecvFieldAll(sendField);
        
	}
	
	
}
