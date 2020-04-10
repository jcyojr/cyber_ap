/**
 *  �ֽý��۸� : ���̹����漼û ��ȭ
 *  ��  ��  �� : ���� ���� �ۼ��� ����
 *  ��  ��  �� : �ٷΰ��� ����ȸ �������� 
 *              
 *  Ŭ����  ID : Df030002FieldList
 *  ����  �̷� :
 * ------------------------------------------------------------------------
 *  �ۼ���     �Ҽ�  		 ����      Tag   ����
 * ------------------------------------------------------------------------   
 * ����ȯ    ��ä��(��)   2013.09.04     %01%  �ű��ۼ�
 *  
 */
package com.uc.bs.cyber.service.df533001;

import com.uc.bs.cyber.field.Comd_WorkDfField;

/**
 * @author Administrator
 *
 */
public class Df533001FieldList extends Comd_WorkDfField {
				
	/**
	 * ������
	 * ����ȸ ��������
	 */
	public Df533001FieldList() {
		
		super();
		
		/*�ٷΰ��� ����ȸ �ۼ��� ����.*/
		
		/*
		 * ��������(�ٷΰ���)
		 * */
		
		addSendField("SIDO_COD"	            , 2   ,  "C"  );  /*�λ���ڵ� 26   */
		addSendField("SGG_COD"	 		    , 3   ,  "C"  );  /*����ڵ�        */
		addSendField("HACD"	 		        , 3   ,  "C"  );  /*������          */
		addSendField("ETC01"	 		    , 1   ,  "C"  );  /*������          */
		addSendField("ACCT_CD"	            , 2   ,  "C"  );  /*ȸ���ڵ�        */
		addSendField("TAX_ITEM"	            , 6   ,  "C"  );  /*�����ڵ�        */
		addSendField("TAX_YY"               , 4   ,  "C"  );  /*������          */
		addSendField("TAX_MM"               , 2   ,  "C"  );  /*������          */
		addSendField("TAX_DIV"              , 1   ,  "C"  );  /*�����������    */
		addSendField("TAX_DD"               , 2   ,  "C"  );  /*�Ű���          */
		addSendField("TAX_TIME"             , 6   ,  "C"  );  /*24HMISS         */
		
		addSendField("DUE_DT"               , 8   ,  "C"  );  /*���α���                */
		addSendField("SUM_RCP"              , 13  ,  "H"  );  /*�Ű��μ���     A + B  */
		addSendField("REG_NO"               , 13  ,  "C"  );  /*�ֹ�/���ι�ȣ           */
		addSendField("DPNM"                 , 40  ,  "C"  );  /*����                    */
		
		addSendField("CHECK"                , 244 ,  "C"  );  /*������         */
		
		/*�߰�*/
		addSendField("NAPBU_TAX"            , 11  ,  "H"  );  /*�� ���αݾ� - SUM_RCP ���Ͽ� ó��  */
		addSendField("SUNAP_DT"             , 8   ,  "C"  );  /*��������                      */
		addSendField("NAPBU_SUDAN"          , 1   ,  "C"  );  /*���μ��� 1:ī��, 2:������ü   */
		
		addSendField("TAX_NO"               , 31  ,  "C"  );  /*������ȣ   */
		addSendField("EPAY_NO"              , 19  ,  "C"  );  /*���ڳ��ι�ȣ   */
		addSendField("NAPBU_GB"             , 1   ,  "C"  );  /*���α���  1:�Ű�  2:����   */
		addSendField("NAPBU_JM_NO"          , 12  ,  "C"  );  /*�Ű�ŷ� �����Ϸù�ȣ   */
		
		addSendField("FILTER2"              , 27  ,  "C"  );  /*��ĭ                          */
		
		
		/*
		 * ��������(������(����))
		 * �ۼ��������� �����ϹǷ� �۽������� ���������� ��ü��Ŵ
		 * */
	    this.addRecvFieldAll(sendField);
		
		
	}
	
	
}
