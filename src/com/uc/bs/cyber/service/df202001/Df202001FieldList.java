/**
 *  �ֽý��۸� : ���̹����漼û ��ȭ
 *  ��  ��  �� : ���� ���� �ۼ��� ����
 *  ��  ��  �� : ������(����)-ȯ�氳���δ�� ���ΰ������
 *              
 *  Ŭ����  ID : Df202001FieldList
 *  ����  �̷� :
 * ------------------------------------------------------------------------
 *  �ۼ���     �Ҽ�  		 ����      Tag   ����
 * ------------------------------------------------------------------------   
 * ��â��   ��ä��(��)  2013.08.13     %01%  �ű��ۼ�
 *  
 */
package com.uc.bs.cyber.service.df202001;

import com.uc.bs.cyber.field.Comd_WorkDfField;
/**
 * @author Administrator
 *
 */
public class Df202001FieldList extends Comd_WorkDfField {
			

	/**
	 * ������
	 * ȯ�氳���δ�� ���ΰ������ ��������
	 */
	public Df202001FieldList() {
		// TODO Auto-generated constructor stub
		
        super();
		
        addSendField("TAX_GB"            , 1  ,  "C"  );  /*���ݱ����ڵ�(1:���漼, 2:����, 3:���ϼ���)*/
        addSendField("TAX_NO"            , 31 ,  "C"  );  /*������ȣ       */
        addSendField("EPAY_NO"           , 19 ,  "C"  );  /*���ڳ��ι�ȣ   */
        addSendField("NAPBU_AMT"         , 11 ,  "H"  );  /*�����ݾ�  */
        addSendField("NAPBU_DATE"        , 8  ,  "C"  );  /*��������  */
        addSendField("NAPBU_SNSU"        , 1  ,  "C"  );  /*��������  */
        addSendField("FILLER1"           , 29 ,  "C"  );  /*SPACE     */

		/*
		 * ��������(������(����))
		 * �ۼ��������� �����ϹǷ� �۽������� ���������� ��ü��Ŵ
		 * */
	    this.addRecvFieldAll(sendField);
	    
	}
	

}
