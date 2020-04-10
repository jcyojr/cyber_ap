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
public class Df533001Tax008FieldList extends Comd_WorkDfField {
				
	/**
	 * ������
	 * ����ȸ ��������
	 */
	public Df533001Tax008FieldList() {
		
		super();
		
		/*�ٷΰ��� ����ȸ �ۼ��� ����. - Ư��¡�� */
		
		/*
		 * ��������(�ٷΰ���)
		 * */


		addSendField("DATA_DIV"	            , 2   ,  "C"  );  /*�ڷᱸ��  21                                                            */
		addSendField("DOC_COD"	 		    , 7   ,  "C"  );  /*�����ڵ�  A103900                                                       */
		addSendField("SIDO_COD"	 		    , 2   ,  "C"  );  /*���������� �õ��ڵ� 26                                                 */
		addSendField("SGG_COD"	 		    , 3   ,  "C"  );  /*���������� �ñ����ڵ�                                                          */
		addSendField("LDONG_COD"	        , 3   ,  "C"  );  /*���������� �������ڵ�                                                         */
		addSendField("LDONG_COD_ETC"	    , 2   ,  "C"  );  /*���������� �������ڵ�_��,�� �ڵ�                                                         */
		addSendField("TAX_ITEM"	            , 6   ,  "C"  );  /*�����ڵ� 140004                                                         */
		addSendField("TAX_YY"               , 4   ,  "C"  );  /*������                                                                           */
		addSendField("TAX_MM"               , 2   ,  "C"  );  /*������                                                                           */
		addSendField("TAX_DIV"              , 1   ,  "C"  );  /*�������� 3                                                              */
		addSendField("REQ_DIV"              , 1   ,  "C"  );  /*���α��� 1, 2                                                           */
		addSendField("TAX_DT"               , 8   ,  "C"  );  /*�Ű�����                                                                         */
		
		addSendField("TPR_COD"              , 2   ,  "C"  );  /*����/���� ����  01,02,..90                                               */
		addSendField("REG_NO"               , 13  ,  "C"  );  /*�ֹ�(����)��ȣ                                                                   */
		addSendField("REG_NM"               , 80  ,  "C"  );  /*����/���θ�                                                                       */
		addSendField("BIZ_NO"               , 10  ,  "C"  );  /*����ڵ�Ϲ�ȣ                                                                    */
		addSendField("CMP_NM"               , 80  ,  "C"  );  /*��ȣ                                                                              */
		addSendField("BIZ_ZIP_NO"           , 6   ,  "C"  );  /*���������� �����ȣ                                                             */
		addSendField("BIZ_ADDR"             , 200 ,  "C"  );  /*����� �ּ�                                                                       */
		addSendField("BIZ_TEL"              , 30  ,  "C"  );  /*��ȭ��ȣ                                                                          */
		addSendField("MO_TEL"               , 30  ,  "C"  );  /*�ڵ��� ��ȣ                                                                       */
		
		addSendField("SUP_YYMM"             , 6   ,  "C"  );  /*���޳�� �ݱ��ǰ�� ���ݱ�6�Ĺݱ�12                                         */
		addSendField("RVSN_YYMM"            , 6   ,  "C"  );  /*�ͼӳ��                                                                           */
		addSendField("F_DUE_DT"             , 8   ,  "C"  );  /*���ʳ������� ���α��� ����üũ������                                               */
		addSendField("DUE_DT"               , 8   ,  "C"  );  /*��������                                                                           */
		addSendField("TAX_RT"               , 5   ,  "C"  );  /*����  10%                                                                */
		addSendField("TOT_STD_AMT"          , 15  ,  "H"  );  /*����ǥ���հ� ��������                                                              */	
		addSendField("PAY_RSTX"             , 15  ,  "H"  );  /*����ҵ漼 �հ� ��������                                                           */			
		addSendField("ADTX_YN"              , 1   ,  "C"  );  /*���꼼 ����   1,2                                                         */
		addSendField("ADTX_AM"              , 15  ,  "H"  );  /*���꼼1����   ���꼼 5% �Ǵ� 10%                                            */
		addSendField("DLQ_ADTX"             , 15  ,  "H"  );  /*���꼼2�����Ⱓ  �����ϼ��� ���� ���꼼                                            */
		addSendField("DLQ_CNT"              , 4   ,  "H"  );  /*�������� �ϼ�                                                                      */
		addSendField("PAY_ADTX"             , 15  ,  "H"  );  /*���꼼  ���꼼�հ�=���꼼1����+���꼼2�����Ⱓ                                     */
		
		addSendField("MEMO"                 , 100  ,  "C"  );  /*���  SPACE                                                              */
		addSendField("ADD_MM_RTN"           , 15   ,  "H"  );  /*��� ��Ÿ ȯ�ޱ�                                                                  */
		addSendField("ADD_MM_AAMT"          , 15   ,  "H"  );  /*��� �߰� ���ξ�                                                                  */
		addSendField("ADD_YY_TRTN"          , 15   ,  "H"  );  /*�������� ȯ�޾�                                                                   */
		addSendField("ADD_YY_TAMT"          , 15   ,  "H"  );  /*�������� �߰����ξ�                                                               */
		addSendField("ADD_ETC_RTN"          , 15   ,  "H"  );  /*�ߵ� ����� ȯ�޾�                                                                */
		addSendField("ADD_RDT_ADTX"         , 15   ,  "H"  );  /*���꼼��� �߰� ���ξ�                                                            */
		addSendField("ADD_RDT_AADD"         , 15   ,  "H"  );  /*���꼼��� �߰� ���꼼                                                            */
		addSendField("ADD_SUM_RTN"          , 15   ,  "H"  );  /*ȯ���հ�ݾ�                                                                      */
		addSendField("ADD_SUM_AAMT"         , 15   ,  "H"  );  /*�߰����� �հ�ݾ�                                                                 */
		
		addSendField("ADD_OUT_AMT"          , 15   ,  "H"  );  /*���������ݾ� ADD_SUM_AAAMT - ADD_SUM_RTN                                 */
		addSendField("ADD_TOT_AMT"          , 15   ,  "H"  );  /*�����ѱݾ� ����ҵ漼�հ� = PAY_ADTX(���꼼�հ�) + ADD_OUT_AMT(���������ݾ�)  */
		addSendField("INTX"                 , 15   ,  "H"  );  /*����������ҵ漼 ADD_TOT_AMT - (PAY_ADTX+ADD_RDT_AADD)                    */
		addSendField("TOT_ADTX"             , 15   ,  "H"  );  /*������ ���꼼  =  ADD_TOT_AMT - INTX                                      */
		addSendField("ADD_OUT_SAMT"         , 15   ,  "H"  );  /*������ȯ���ܾ�  ���������ݾ�(�̿�ȯ���ܾ�)                                   */
		addSendField("MINU_YN"              , 1    ,  "C"  );  /*���������ݾ� ������ ����  Y:����, N:���                                         */
		
		addSendField("RPT_REG_NO"           , 13   ,  "C"  );  /*�����븮�� �ֹε�Ϲ�ȣ                                                           */
		addSendField("RPT_NM"               , 80   ,  "C"  );  /*�����븮�� ����                                                                   */
		addSendField("RPT_BIZ_NO"           , 10   ,  "C"  );  /*�����븮�� ����ڵ�Ϲ�ȣ                                                         */
		addSendField("RPT_TEL"              , 30   ,  "C"  );  /*�����븮�� ��ȭ��ȣ                                                               */
		addSendField("TAX_PRO_CD"           , 4    ,  "C"  );  /*�������α׷� �ڵ�                                                                 */
		addSendField("A_SPACE"              , 27   ,  "C"  );  /*���� SPACE                                                              */
		/* ���μ� �� ������������ 1080byte  */
		
		addSendField("DATA_DIV11"           , 2    ,  "C"  );  /*�ڷᱸ��  22                                                             */
		addSendField("DOC_COD11"            , 7    ,  "C"  );  /*�����ڵ�  A103900                                                        */
		addSendField("TXTP_CD11"            , 2    ,  "C"  );  /*�ҵ汸��   11:���ڼҵ�                                                            */
		addSendField("TXTP_EMP11"           , 8    ,  "H"  );  /*�ο�                                                                              */
		addSendField("TXTP_STD11"           , 15   ,  "H"  );  /*����ǥ�ؾ�                                                                        */
		addSendField("TXTP_INTX11"          , 15   ,  "H"  );  /*����ҵ漼                                                                        */
		
		addSendField("DATA_DIV12"           , 2    ,  "C"  );  /*�ڷᱸ��  22                                                             */
		addSendField("DOC_COD12"            , 7    ,  "C"  );  /*�����ڵ�  A103900                                                        */
		addSendField("TXTP_CD12"            , 2    ,  "C"  );  /*�ҵ汸��   12:���ҵ�                                                            */
		addSendField("TXTP_EMP12"           , 8    ,  "H"  );  /*�ο�                                                                              */
		addSendField("TXTP_STD12"           , 15   ,  "H"  );  /*����ǥ�ؾ�                                                                        */
		addSendField("TXTP_INTX12"          , 15   ,  "H"  );  /*����ҵ漼                                                                        */
		
		addSendField("DATA_DIV13"           , 2    ,  "C"  );  /*�ڷᱸ��  22                                                             */
		addSendField("DOC_COD13"            , 7    ,  "C"  );  /*�����ڵ�  A103900                                                        */
		addSendField("TXTP_CD13"            , 2    ,  "C"  );  /*�ҵ汸��   13:����ҵ�                                                            */
		addSendField("TXTP_EMP13"           , 8    ,  "H"  );  /*�ο�                                                                              */
		addSendField("TXTP_STD13"           , 15   ,  "H"  );  /*����ǥ�ؾ�                                                                        */
		addSendField("TXTP_INTX13"          , 15   ,  "H"  );  /*����ҵ漼                                                                        */
		
		addSendField("DATA_DIV14"           , 2    ,  "C"  );  /*�ڷᱸ��  22                                                             */
		addSendField("DOC_COD14"            , 7    ,  "C"  );  /*�����ڵ�  A103900                                                        */
		addSendField("TXTP_CD14"            , 2    ,  "C"  );  /*�ҵ汸��   14:�ٷμҵ�                                                            */
		addSendField("TXTP_EMP14"           , 8    ,  "H"  );  /*�ο�                                                                              */
		addSendField("TXTP_STD14"           , 15   ,  "H"  );  /*����ǥ�ؾ�                                                                        */
		addSendField("TXTP_INTX14"          , 15   ,  "H"  );  /*����ҵ漼                                                                        */
		
		addSendField("DATA_DIV16"           , 2    ,  "C"  );  /*�ڷᱸ��  22                                                             */
		addSendField("DOC_COD16"            , 7    ,  "C"  );  /*�����ڵ�  A103900                                                        */
		addSendField("TXTP_CD16"            , 2    ,  "C"  );  /*�ҵ汸��   16:��Ÿ�ҵ�                                                            */
		addSendField("TXTP_EMP16"           , 8    ,  "H"  );  /*�ο�                                                                              */
		addSendField("TXTP_STD16"           , 15   ,  "H"  );  /*����ǥ�ؾ�                                                                        */
		addSendField("TXTP_INTX16"          , 15   ,  "H"  );  /*����ҵ漼                                                                        */
		
		addSendField("DATA_DIV17"           , 2    ,  "C"  );  /*�ڷᱸ��  22                                                             */
		addSendField("DOC_COD17"            , 7    ,  "C"  );  /*�����ڵ�  A103900                                                        */
		addSendField("TXTP_CD17"            , 2    ,  "C"  );  /*�ҵ汸��   17:���ݼҵ�                                                            */
		addSendField("TXTP_EMP17"           , 8    ,  "H"  );  /*�ο�                                                                              */
		addSendField("TXTP_STD17"           , 15   ,  "H"  );  /*����ǥ�ؾ�                                                                        */
		addSendField("TXTP_INTX17"          , 15   ,  "H"  );  /*����ҵ漼                                                                        */
		
		addSendField("DATA_DIV21"           , 2    ,  "C"  );  /*�ڷᱸ��  22                                                             */
		addSendField("DOC_COD21"            , 7    ,  "C"  );  /*�����ڵ�  A103900                                                        */
		addSendField("TXTP_CD21"            , 2    ,  "C"  );  /*�ҵ汸��   21:�����ҵ�                                                            */
		addSendField("TXTP_EMP21"           , 8    ,  "H"  );  /*�ο�                                                                              */
		addSendField("TXTP_STD21"           , 15   ,  "H"  );  /*����ǥ�ؾ�                                                                        */
		addSendField("TXTP_INTX21"          , 15   ,  "H"  );  /*����ҵ漼                                                                        */
		
		addSendField("DATA_DIV22"           , 2    ,  "C"  );  /*�ڷᱸ��  22                                                             */
		addSendField("DOC_COD22"            , 7    ,  "C"  );  /*�����ڵ�  A103900                                                        */
		addSendField("TXTP_CD22"            , 2    ,  "C"  );  /*�ҵ汸��   22:�絵�ҵ� �ҵ漼�� ��119��                                          */
		addSendField("TXTP_EMP22"           , 8    ,  "H"  );  /*�ο�                                                                              */
		addSendField("TXTP_STD22"           , 15   ,  "H"  );  /*����ǥ�ؾ�                                                                        */
		addSendField("TXTP_INTX22"          , 15   ,  "H"  );  /*����ҵ漼                                                                        */
		
		addSendField("DATA_DIV31"           , 2    ,  "C"  );  /*�ڷᱸ��  22                                                             */
		addSendField("DOC_COD31"            , 7    ,  "C"  );  /*�����ڵ�  A103900                                                        */
		addSendField("TXTP_CD31"            , 2    ,  "C"  );  /*�ҵ汸��   31:�ܱ����� ���μ��� ��98��                                           */
		addSendField("TXTP_EMP31"           , 8    ,  "H"  );  /*�ο�                                                                              */
		addSendField("TXTP_STD31"           , 15   ,  "H"  );  /*����ǥ�ؾ�                                                                        */
		addSendField("TXTP_INTX31"          , 15   ,  "H"  );  /*����ҵ漼                                                                        */
		
		addSendField("DATA_DIV32"           , 2    ,  "C"  );  /*�ڷᱸ��  22                                                             */
		addSendField("DOC_COD32"            , 7    ,  "C"  );  /*�����ڵ�  A103900                                                        */
		addSendField("TXTP_CD32"            , 2    ,  "C"  );  /*�ҵ汸��   32:��������                                                            */
		addSendField("TXTP_EMP32"           , 8    ,  "H"  );  /*�ο�                                                                              */
		addSendField("TXTP_STD32"           , 15   ,  "H"  );  /*����ǥ�ؾ�                                                                        */
		addSendField("TXTP_INTX32"          , 15   ,  "H"  );  /*����ҵ漼                                                                        */
		
		addSendField("DATA_DIV33"           , 2    ,  "C"  );  /*�ڷᱸ��  22                                                             */
		addSendField("DOC_COD33"            , 7    ,  "C"  );  /*�����ڵ�  A103900                                                        */
		addSendField("TXTP_CD33"            , 2    ,  "C"  );  /*�ҵ汸��   33:��������(���μ���-��37��)                                    */
		addSendField("TXTP_EMP33"           , 8    ,  "H"  );  /*�ο�                                                                              */
		addSendField("TXTP_STD33"           , 15   ,  "H"  );  /*����ǥ�ؾ�                                                                        */
		addSendField("TXTP_INTX33"          , 15   ,  "H"  );  /*����ҵ漼                                                                        */
		
		addSendField("DATA_DIV34"           , 2    ,  "C"  );  /*�ڷᱸ��  22                                                             */
		addSendField("DOC_COD34"            , 7    ,  "C"  );  /*�����ڵ�  A103900                                                        */
		addSendField("TXTP_CD34"            , 2    ,  "C"  );  /*�ҵ汸��   34:��������(������)                                             */
		addSendField("TXTP_EMP34"           , 8    ,  "H"  );  /*�ο�                                                                              */
		addSendField("TXTP_STD34"           , 15   ,  "H"  );  /*����ǥ�ؾ�                                                                        */
		addSendField("TXTP_INTX34"          , 15   ,  "H"  );  /*����ҵ漼                                                                        */
		
		addSendField("DATA_DIV91"           , 2    ,  "C"  );  /*�ڷᱸ��  22                                                             */
		addSendField("DOC_COD91"            , 7    ,  "C"  );  /*�����ڵ�  A103900                                                        */
		addSendField("TXTP_CD91"            , 2    ,  "C"  );  /*�ҵ汸��   91:�ܱ������κ��� ���� �ҵ�(������)                               */
		addSendField("TXTP_EMP91"           , 8    ,  "H"  );  /*�ο�                                                                              */
		addSendField("TXTP_STD91"           , 15   ,  "H"  );  /*����ǥ�ؾ�                                                                        */
		addSendField("TXTP_INTX91"          , 15   ,  "H"  );  /*����ҵ漼                                                                        */
		
		
		/*�߰�*/
		addSendField("NAPBU_TAX"            , 11  ,  "H"  );  /*�� ���αݾ� - SUM_RCP ���Ͽ� ó��                                               */
		addSendField("SUNAP_DT"             , 8   ,  "C"  );  /*��������                                                                           */
		addSendField("NAPBU_SUDAN"          , 1   ,  "C"  );  /*���μ��� 1:ī��, 2:������ü                                                        */
		
		addSendField("TAX_NO"               , 31  ,  "C"  );  /*������ȣ                                                                           */
		addSendField("EPAY_NO"              , 19  ,  "C"  );  /*���ڳ��ι�ȣ                                                                       */
		addSendField("NAPBU_GB"             , 1   ,  "C"  );  /*���α���  1:�Ű�  2:����                                                           */
		addSendField("NAPBU_JM_NO"          , 12  ,  "C"  );  /*�Ű�ŷ� �����Ϸù�ȣ                                                               */
		
		addSendField("FILTER2"              , 30  ,  "C"  );  /*��ĭ                                                                                */
		
	
		/*
		 * ��������(������(����))
		 * �ۼ��������� �����ϹǷ� �۽������� ���������� ��ü��Ŵ - 1080 + 49 * 13  1717 + 
		 * */
	    this.addRecvFieldAll(sendField);
	    
	}
	
	
}
