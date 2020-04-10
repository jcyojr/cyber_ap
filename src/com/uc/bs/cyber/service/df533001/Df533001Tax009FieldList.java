/**
c *  �ֽý��۸� : ���̹����漼û ��ȭ
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
public class Df533001Tax009FieldList extends Comd_WorkDfField {
				
	/**
	 * ������
	 * ����ȸ ��������
	 */
	public Df533001Tax009FieldList() {
		
		super();
		
		/*�ٷΰ��� ����ȸ �ۼ��� ����.*/
		
		/*
		 * ��������(�ٷΰ���)
		 * */

		addSendField("SIDO_COD"	            , 2   ,  "C"  );  /*�λ���ڵ� 26                       */
		addSendField("SGG_COD"	 		    , 3   ,  "C"  );  /*����ڵ�                                */
		addSendField("HACD"	 		        , 3   ,  "C"  );  /*������                                  */
		addSendField("ETC01"	 		    , 1   ,  "C"  );  /*��ĭ                                    */
		addSendField("ACCT_CD"	            , 2   ,  "C"  );  /*ȸ���ڵ�                                */
		addSendField("TAX_ITEM"	            , 6   ,  "C"  );  /*�����ڵ�                                */
		addSendField("TAX_YY"               , 4   ,  "C"  );  /*������                                  */
		addSendField("TAX_MM"               , 2   ,  "C"  );  /*������                                  */
		addSendField("TAX_DIV"              , 1   ,  "C"  );  /*�����������                            */
		addSendField("TAX_DD"               , 2   ,  "C"  );  /*�Ű���                                  */
		addSendField("TAX_TIME"             , 6   ,  "C"  );  /*24HMISS                            */
		
		addSendField("DUE_DT"               , 8   ,  "C"  );  /*���α���                                */
		addSendField("SUM_RCP"              , 13  ,  "H"  );  /*�Ű��μ���  A+B+C                  */
		addSendField("REG_NO"               , 13  ,  "C"  );  /*�ֹ�/���ι�ȣ                           */
		addSendField("DPNM"                 , 40  ,  "C"  );  /*����                                    */
		
		addSendField("DP_GBN"               , 1   ,  "C"  );  /*���α��� 0:����, 1:����                */
		addSendField("TEL_NO"               , 12  ,  "C"  );  /*��ȭ��ȣ                                */
		addSendField("PHONE_NO"             , 12  ,  "C"  );  /*�޴���                                  */
		addSendField("ZIP_CD"               , 6   ,  "C"  );  /*�����ȣ                                */
		addSendField("ADDR"                 , 60  ,  "C"  );  /*�ּ�                    */	
		addSendField("ADDR_DT"              , 10  ,  "C"  );  /*�ּ�_��               */			
		addSendField("SAUP_NO"              , 10  ,  "C"  );  /*����ڹ�ȣ                              */
		addSendField("CMP_NM"               , 40  ,  "C"  );  /*��ȣ                                    */
		addSendField("RVSN_YY"              , 4   ,  "C"  );  /*�ͼӳ⵵                                */
		addSendField("RVSN_MM"              , 2   ,  "C"  );  /*�ͼӳ��                                */
		
		addSendField("GSGY"                 , 5   ,  "H"  );  /*���(����)����                          */
		addSendField("BECH"                 , 1   ,  "C"  );  /*�����������⿩��  1:����, 0:�̹���      */
		addSendField("SGEK"                 , 13  ,  "H"  );  /*����Ҽ� ���μ��� A                   */
		addSendField("F_DUE_DT"             , 8   ,  "C"  );  /*���� ���α���                            */
		addSendField("DLQ_CNT"              , 3   ,  "H"  );  /*���������ϼ�                             */
		addSendField("NGAS"                 , 11  ,  "H"  );  /*���κҼ��� ����� B                   */
		addSendField("GSEK"                 , 11  ,  "H"  );  /*�Ű�Ҽ��� ����� C                   */
		addSendField("BU_CD"                , 2   ,  "C"  );  /*�������� 2�ڸ� �߰�     */
		addSendField("FILTER"               , 33  ,  "H"  );  /*��ĭ                                     */
		
		/*�߰�*/
		addSendField("NAPBU_TAX"            , 11  ,  "H"  );  /*�� ���αݾ� - SUM_RCP ���Ͽ� ó��     */
		addSendField("SUNAP_DT"             , 8   ,  "C"  );  /*��������                                 */
		addSendField("NAPBU_SUDAN"          , 1   ,  "C"  );  /*���μ��� 1:ī��, 2:������ü             */
		
		addSendField("TAX_NO"               , 31  ,  "C"  );  /*������ȣ                                 */
		addSendField("EPAY_NO"              , 19  ,  "C"  );  /*���ڳ��ι�ȣ                             */
		addSendField("NAPBU_GB"             , 1   ,  "C"  );  /*���α���  1:�Ű�  2:����                */
		addSendField("NAPBU_JM_NO"          , 12  ,  "C"  );  /*�Ű�ŷ� �����Ϸù�ȣ                    */
		
		addSendField("FILTER2"              , 27  ,  "C"  );  /*��ĭ                                     */
		
		/*
		 * ��������(������(����))
		 * �ۼ��������� �����ϹǷ� �۽������� ���������� ��ü��Ŵ
		 * */
	    this.addRecvFieldAll(sendField);
			    	    
	}
	
	
}
