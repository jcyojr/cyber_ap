/**
 *  �ֽý��۸� : ���̹����漼û ��ȭ
 *  ��  ��  �� : ���� ���� �ۼ��� ����
 *  ��  ��  �� : �λ�����-���ܼ��� ���γ��� ������ȸ 
 *              
 *  Ŭ����  ID : Bs273001FieldList
 *  ����  �̷� :
 * ------------------------------------------------------------------------
 *  �ۼ���     �Ҽ�  		 ����      Tag   ����
 * ------------------------------------------------------------------------   
 * Ȳ����   �ٻ�(��)  2011.06.08     %01%  �ű��ۼ�
 *  
 */
package com.uc.bs.cyber.service.kf273001;

import com.uc.bs.cyber.field.Comd_WorkKfField;
/**
 * @author Administrator
 *
 */
public class Kf273001FieldList extends Comd_WorkKfField {
			
	
	/**
	 * ������
	 * ���ܼ��� ���γ��� ������ȸ ��������
	 */
	public Kf273001FieldList() {
		// TODO Auto-generated constructor stub
		super();
		
        /*
         * �۽�����(������)
         * */
        this.addSendField("JUMINNO"       ,13  ,"C");     /*������ �ֹ�(�����)��Ϲ�ȣ*/
        this.addSendField("IN_STDATE"     ,8   ,"H");     /*��ȸ ���� ����             */
        this.addSendField("IN_ENDATE"     ,8   ,"H");     /*��ȸ ���� ����             */
        this.addSendField("DATATOT"       ,3   ,"H");     /*���� �� �Ǽ�               */
        this.addSendField("POINTNO"       ,3   ,"H");     /*���� ��ȣ                  */
        this.addSendField("DATANUM"       ,2   ,"H");     /*������ �Ǽ�                */

        
        /*
         * ��������(������)
         * */
        this.addRecvField("JUMINNO"       ,13  ,"C");     /*������ �ֹ�(�����)��Ϲ�ȣ*/ 
        this.addRecvField("IN_STDATE"     ,8   ,"H");     /*��ȸ ���� ����             */ 
        this.addRecvField("IN_ENDATE"     ,8   ,"H");     /*��ȸ ���� ����             */ 
        this.addRecvField("DATATOT"       ,3   ,"H");     /*���� �� �Ǽ�               */ 
        this.addRecvField("POINTNO"       ,3   ,"H");     /*���� ��ȣ                  */ 
        this.addRecvField("DATANUM"       ,2   ,"H");     /*������ �Ǽ�                */ 

        
        /*
         * ��������(�ݺ�����)
         * */
        this.addRepetField("PUBGCODE"     ,2   ,"H");     /*������ �з��ڵ�          */ 
        this.addRepetField("JIRONO"       ,7   ,"H");     /*���� ��ȣ                  */ 
        this.addRepetField("ETAXNO"       ,32  ,"C");     /*���ι�ȣ                   */
        this.addRepetField("EPAYNO"       ,19  ,"C");     /*���ڳ��ι�ȣ               */
        this.addRepetField("SEQNO"        ,5   ,"H");     /*�г�����                   */
        this.addRepetField("GWAMOK"       ,6   ,"H");     /*����/����                  */ 
        this.addRepetField("GWADATE"      ,6   ,"H");     /*�⵵/���                  */ 
        this.addRepetField("KIBUN"        ,1   ,"H");     /*��ǥ ���� ����             */ 
        this.addRepetField("NAPAMT"       ,15  ,"H");     /*���� �ݾ�                  */ 
        this.addRepetField("NAPBU_DATE"   ,8   ,"H");     /*���� ����                  */ 
        this.addRepetField("NAPBU_SYS"    ,1   ,"C");     /*���� �̿� �ý���           */ 
        this.addRepetField("JIJUM_CODE"   ,7   ,"H");     /*�������� ���� �ڵ�         */ 

        /* ���� ��
	        0194IGN0320210273001000G0001106210759210320I0000082026000000537261000685006109151090421201106212011062100100101
	        26
	        1000685
	        38053010600120110615500026895
	        00000
	        106001
	        201106
	        1
	        000000000207790
	        20110621
	        B
	        0329990
        */
        
	}
	
	
}
