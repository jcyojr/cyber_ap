/**
 *  �ֽý��۸� : ���̹����漼û ��ȭ
 *  ��  ��  �� : ���漼 ���ΰ�� �뺸
 *  ��  ��  �� : ������-���ܼ��� �ϰ����� �뺸 ����
 *              
 *  Ŭ����  ID : Kf276001FieldList
 *  ����  �̷� :
 * ------------------------------------------------------------------------
 *  �ۼ���     �Ҽ�  		 ����      Tag   ����
 * ------------------------------------------------------------------------   
 * �ҵ���   ��ä��(��)  2011.06.08     %01%  �ű��ۼ�
 *  
 */
package com.uc.bs.cyber.service.kf276001;

import com.uc.bs.cyber.field.Comd_WorkKfField;
/**
 * @author Administrator
 *
 */
public class Kf276001FieldList extends Comd_WorkKfField {
			
	
	/**
	 * ������
	 * ���ܼ��� �ϰ����ΰ�� �뺸 ��������
	 */
	public Kf276001FieldList() {
		// TODO Auto-generated constructor stub
		super();
		
        /*
         * �۽�����(������)
         * */
        this.addSendField("JUMINNO"		,13  ,"C");     /*������ �ֹ�(�����)��Ϲ�ȣ	*/
        this.addSendField("PAY_DT"		,8   ,"H");     /*��������             			*/
        this.addSendField("BANK_CD"		,7   ,"H");     /*������������ڵ�              */
        this.addSendField("OUTACCT_NO"	,3   ,"C");     /*���� ��ȣ                  	*/
        this.addSendField("TEL_NO"		,14  ,"H");     /*������ �Ǽ�                	*/
        this.addSendField("REG_NO"		,13  ,"C");     /*������ �ֹ�(�����)��Ϲ�ȣ	*/
        this.addSendField("REG_NM"		,10  ,"C");     /*������ ��						*/
        this.addSendField("ACCT_HOL"	,10  ,"C");     /*�����ָ� 						*/
        this.addSendField("NAPBU_SYS"	,1   ,"C");     /*���� �̿� �ý���				*/
        this.addSendField("NAPBU_GB"	,1   ,"C");     /*���� ���� ����				*/
        this.addSendField("FIELD1"		,10  ,"C");     /*�������� FIELD 1				*/
        this.addSendField("REQ_CNT"		,2   ,"H");     /*��û�Ǽ�						*/
        
        /*
         * ��������(������)
         * */
        this.addRecvField("JUMINNO"		,13  ,"C");     /*������ �ֹ�(�����)��Ϲ�ȣ	*/
        this.addRecvField("PAY_DT"		,8   ,"H");     /*��������             			*/
        this.addRecvField("BANK_CD"		,7   ,"H");     /*������������ڵ�              */
        this.addRecvField("OUTACCT_NO"	,3   ,"C");     /*���� ��ȣ                  	*/
        this.addRecvField("TEL_NO"		,14  ,"H");     /*������ �Ǽ�                	*/
        this.addRecvField("REG_NO"		,13  ,"C");     /*������ �ֹ�(�����)��Ϲ�ȣ	*/
        this.addRecvField("REG_NM"		,10  ,"C");     /*������ ��						*/
        this.addRecvField("ACCT_HOL"	,10  ,"C");     /*�����ָ� 						*/
        this.addRecvField("NAPBU_SYS"	,1   ,"C");     /*���� �̿� �ý���				*/
        this.addRecvField("NAPBU_GB"	,1   ,"C");     /*���� ���� ����				*/
        this.addRecvField("FIELD1	"	,10  ,"C");     /*�������� FIELD 1				*/
        this.addRecvField("REQ_CNT"		,2   ,"H");     /*��û�Ǽ�						*/

        
        /*
         * �۽�����(�ݺ�����)
         * */
        this.addRepetField("PUBGCODE"   ,2   ,"H");     /*������ �з��ڵ�          */ 
        this.addRepetField("JIRONO"     ,7   ,"H");     /*���� ��ȣ                  */ 
        this.addRepetField("EPAYNO"     ,19  ,"C");     /*���ڳ��ι�ȣ               */
        this.addRepetField("FIELD2"		 ,13  ,"C");     /*�������� FIELD 2			 */
        this.addRepetField("NAPBU_AMT"   ,15  ,"H");     /*���� �ݾ�                  */ 
        this.addRepetField("SEQNO"    	 ,12  ,"C");     /*�ŷ� ���� ��ȣ             */ 
        this.addRepetField("TREAT_CODE"	 ,3   ,"C");     /*ó������ �ڵ�         	 */ 
        
	}
}
