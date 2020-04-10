/**
 *  �ֽý��۸� : ���̹����漼û ��ȭ
 *  ��  ��  �� : ���� ���� �ۼ��� ����
 *  ��  ��  �� : ������(����)-ȯ�氳���δ�� ����ȸ �������� 
 *              
 *  Ŭ����  ID : Df201002FieldList
 *  ����  �̷� :
 * ------------------------------------------------------------------------
 *  �ۼ���     �Ҽ�  		 ����      Tag   ����
 * ------------------------------------------------------------------------   
 * ��â��   ��ä��(��)  2013.08.12     %01%  �ű��ۼ�
 *  
 */
package com.uc.bs.cyber.service.df201002;

import com.uc.bs.cyber.field.Comd_WorkDfField;

/**
 * @author Administrator
 *
 */
public class Df201002FieldList extends Comd_WorkDfField {
				
	/**
	 * ������
	 * ȯ�氳���δ�� ����ȸ ��������
	 */
	public Df201002FieldList() {
		// TODO Auto-generated constructor stub
		super();
		
		/*������(����) ����ȸ �ۼ��� ����.*/
		
		/*
		 * �۽�����(���̹�)
		 * */
        addSendField("TAX_GB"             , 1   ,  "C"  );  /*���ݱ����ڵ�(1:���漼, 2:����, 3:���ϼ���)*/
        addSendField("TAX_NO"             , 31  ,  "C"  );  /*������ȣ       */
        addSendField("EPAY_NO"            , 19  ,  "C"  );  /*���ڳ��ι�ȣ   */   
        addSendField("NAP_NAME"           , 80  ,  "C"  );  /*�����ڸ�       */
        addSendField("SGG_NAME"           , 10  ,  "C"  );  /*�ΰ������     */
        addSendField("TAX_NAME"           , 50  ,  "C"  );  /*���񼼸��     */
        addSendField("TAX_YM"             , 6   ,  "C"  );  /*�������       */
        addSendField("TAX_DIV"            , 1   ,  "C"  );  /*���         */
        addSendField("NAP_BFDATE"         , 8   ,  "C"  );  /*������(���⳻)*/
        addSendField("NAP_BFAMT"          , 11  ,  "H"  );  /*���⳻ �ݾ�    */
        addSendField("NAP_AFDATE"         , 8   ,  "C"  );  /*������(������)*/
        addSendField("NAP_AFAMT"          , 11  ,  "H"  );  /*������ �ݾ�    */
        addSendField("NAP_AMT"            , 11  ,  "H"  );  /*���� �����ؾ� �� �ݾ�   */
        addSendField("TAX_DESC"           , 130 ,  "C"  );  /*�ΰ�����       */
        addSendField("FILLER1"            , 23  ,  "C"  );  /*�������� FIELD 2 */

		/*
		 * ��������(������(����))
		 * 
		 * */
        addRecvField("TAX_GB"               , 1  ,  "C"); /*���ݱ����ڵ�(1:���漼, 2:����, 3:���ϼ���) */
        addRecvField("TAX_NO"               , 31 ,  "C"); /*������ȣ        */
        addRecvField("EPAY_NO"              , 19 ,  "C"); /*���ڳ��ι�ȣ    */
        addRecvField("FILLER2"              , 29 ,  "C"); /*SPACE      */
        
	}
	
	
}
