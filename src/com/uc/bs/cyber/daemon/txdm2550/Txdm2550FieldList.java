/**
 *  �ֽý��۸� : ���̹����漼û ��ȭ
 *  ��  ��  �� : �ݰ�� ���� ���ೳ��(���漼����)
 *  ��  ��  �� : ������- ���ೳ��(���ϼۼ���)
 *              
 *  Ŭ����  ID : Txdm2550FieldList
 *  ����  �̷� :
 * ------------------------------------------------------------------------
 *  �ۼ���     �Ҽ�  		 ����      Tag   ����
 * ------------------------------------------------------------------------   
 * �ҵ���   ��ä��(��)  2011.07.07     %01%  �ű��ۼ�
 *  
 */
package com.uc.bs.cyber.daemon.txdm2550;

import com.uc.core.MapForm;
import com.uc.egtob.net.FieldList;
/**
 * @author Administrator
 *
 */
public class Txdm2550FieldList {
	
	
	private MapForm dataMap = null;
	
	private FieldList field ;
	
	private int     len =  0;
	
	/**
	 * ������
	 * ���� �ۼ��� ��������
	 */
	public Txdm2550FieldList() {
		// TODO Auto-generated constructor stub
		
		field = new FieldList();

		/*�޼������� 4byte������ ����*/
		
		
		/*
		 * �۽�����(���̹�)
		 * */	
		field.add("MSG_LEN"        , 4       ,  "C"  );  	/*���Ÿ޼��� ����    */
		field.add("MSG_CODE"       , 1       ,  "C"  );  	/*���Ÿ޼��� CODE    */
		field.add("MSG_RSV"        , 3       ,  "C"  );  	/*���Ÿ޼��� �����ʵ�*/
		/*���*/
		field.add("BLK_TYPE"       , 1       ,  "C"  );  	/*���Ÿ޼��� TYPE    */
		field.add("BLK_FLAG"       , 3       ,  "C"  );  	/*���Ÿ޼��� Flag    */
		
		len = field.getFieldListLen();

		this.dataMap = new MapForm();
        
	}
	
	/**
	 * �������� ����(ǥ��)
	 */
    public void Msg_Pub_FieldList() {
    	
    	field = new FieldList();

		/*������ �ۼ��� ����.*/
		
		/*
		 * �۽�����(���̹�)
		 * */	
		field.add("MSG_LEN"        , 4       ,  "C"  );  	/*���Ÿ޼��� ����    */
		field.add("MSG_CODE"       , 1       ,  "C"  );  	/*���Ÿ޼��� CODE    */
		field.add("MSG_RSV"        , 3       ,  "C"  );  	/*���Ÿ޼��� �����ʵ�*/
		/*���*/
		field.add("BLK_DATA"       , 1032    ,  "C"  );  	/*���Ÿ޼��� DATA    */
		field.add("MSG_MD5"        , 16      ,  "B"  );  	/*���Ÿ޼��� md5     */
        
		len = field.getFieldListLen();

		this.dataMap = new MapForm();
    	
    }
	
    /**
     * ��ҽ� �� ���� �� ����
     */
    public void Msg_Can_FieldList() {
    	
    	field = new FieldList();

		/*
		 * �۽�����(���̹�)
		 * */	
		field.add("MSG_LEN"         , 4       ,  "C"  );  	/*�۽Ÿ޼��� ����    */
		field.add("MSG_CODE"        , 1       ,  "C"  );  	/*�۽Ÿ޼��� CODE    */
		field.add("MSG_RSV"         , 3       ,  "C"  );  	/*�۽Ÿ޼��� �����ʵ�*/
		/*���*/
		field.add("BLK_TYPE"        , 1       ,  "C"  );  	/*�۽Ÿ޼��� TYPE    */
		field.add("BLK_FLAG_GB"     , 1       ,  "C"  );  	/*�۽Ÿ޼��� �޼������� */
		field.add("BLK_CAN_CD"      , 2       ,  "C"  );  	/*�۽Ÿ޼��� ����ڵ�   */
		
		field.add("BLK_CAN_MSG_LEN" , 4       ,  "B"  );  	/*�۽Ÿ޼��� ��Ҹ޼�������   */
		field.add("BLK_CAN_MSG_DAT" , 1024    ,  "C"  );  	/*�۽Ÿ޼��� ��Ҹ޼���       */
		
		field.add("MSG_MD5"         , 16      ,  "B"  );  	/*�۽Ÿ޼��� md5 ��ȣȭ */
        
		len = field.getFieldListLen();

		this.dataMap = new MapForm();
    	
    }
    
    /**
     * OFFSET ��������...
     * �������� ������ ���������� �ްų� �Ǵ� �̾������ ��������
     */
    public void Msg_Pos_FieldList() {
    	
    	field = new FieldList();

		/*
		 * �۽�����(���̹�)
		 * */	
		field.add("MSG_LEN"         , 4       ,  "C"  );  	/*�۽Ÿ޼��� ����    */
		field.add("MSG_CODE"        , 1       ,  "C"  );  	/*�۽Ÿ޼��� CODE    */
		field.add("MSG_RSV"         , 3       ,  "C"  );  	/*�۽Ÿ޼��� �����ʵ�*/
		/*���*/
		field.add("BLK_TYPE"        , 1       ,  "C"  );  	/*�۽Ÿ޼��� TYPE    */
		field.add("BLK_FLAG"        , 3       ,  "B"  );  	/*�۽Ÿ޼��� FLAG    */
		
		field.add("BLK_OFFSET"      , 4       ,  "B"  );  	/*���ſϷ�� DATA�� Offset*/
		field.add("BLK_DATA"        , 1024    ,  "B"  );  	/*�̻��             */
		
		field.add("MSG_MD5"         , 16      ,  "B"  );  	/*�۽Ÿ޼��� md5 ��ȣȭ */
        
		len = field.getFieldListLen();

		this.dataMap = new MapForm();
    	
    }
    
    /**
     * ACK ��������
     */
    public void Msg_Ack_FieldList() {
    	
    	field = new FieldList();

		/*
		 * �۽�����(���̹�)
		 * */	
		field.add("MSG_LEN"         , 4       ,  "C"  );  	/*�۽Ÿ޼��� ����    */
		field.add("MSG_CODE"        , 1       ,  "C"  );  	/*�۽Ÿ޼��� CODE    */
		field.add("MSG_RSV"         , 3       ,  "C"  );  	/*�۽Ÿ޼��� �����ʵ�*/
		/*���*/
		field.add("BLK_TYPE"        , 1       ,  "C"  );  	/*�۽Ÿ޼��� TYPE    */
		field.add("BLK_FLAG1"       , 1       ,  "B"  );  	/*�۽Ÿ޼��� FLAG1   */
		field.add("BLK_FLAG2"       , 1       ,  "C"  );  	/*�۽Ÿ޼��� FLAG2   */
		field.add("BLK_FLAG3"       , 1       ,  "B"  );  	/*�۽Ÿ޼��� FLAG3   */
		
		field.add("BLK_OFFSET"      , 4       ,  "B"  );  	/*���ſϷ�� DATA�� Offset*/
		field.add("BLK_DATA"        , 1024    ,  "C"  );  	/*�̻��             */
		
		field.add("MSG_MD5"         , 16      ,  "B"  );  	/*�۽Ÿ޼��� md5 ��ȣȭ */
        
		len = field.getFieldListLen();

		this.dataMap = new MapForm();
    	
    }

    /**
     *  ���ϼ��� �� �Ľ̿� ����
     */
    public void Msg_Data_FieldList() {
    	
    	field = new FieldList();

		/*
		 * ��������(���̹�)
		 * */	
		field.add("MSG_LEN"         , 4       ,  "C"  );  	/*���Ÿ޼��� ����    */
		field.add("MSG_CODE"        , 1       ,  "C"  );  	/*���Ÿ޼��� CODE    */
		field.add("MSG_RSV"         , 3       ,  "C"  );  	/*���Ÿ޼��� �����ʵ�*/
		/*���*/
		field.add("BLK_TYPE"        , 1       ,  "C"  );  	/*���Ÿ޼��� TYPE    */
		field.add("BLK_FLAG_GB"     , 1       ,  "C"  );  	/*���Ÿ޼��� FLAG����*/
		field.add("BLK_RD_LEN"      , 2       ,  "B"  );  	/*���Ÿ޼��� ���������� ����  */
		
		field.add("BLK_OFFSET"      , 4       ,  "B"  );  	/*���ſϷ�� File �� Offset   */
		field.add("BLK_DATA"        , 1024    ,  "C"  );  	/*���� ���� ����              */
		
		field.add("MSG_MD5"         , 16      ,  "B"  );  	/*�۽Ÿ޼��� md5 ��ȣȭ */
        
		len = field.getFieldListLen();

		this.dataMap = new MapForm();
    	
    }

	
	/*����������*/
	public void Msg_fileinfo_FieldList() {
		// TODO Auto-generated constructor stub
		
		field = new FieldList();

		/*
		 * ��������(���̹�)
		 * */	
		field.add("filler1"    ,  8     ,"C");    /*Filler1      */
		/*���*/
		field.add("msg_type"   ,  1     ,"C");    /*���� type    */
		field.add("res_code"   ,  1     ,"C");    /*��û�ڵ�     */
		field.add("append_yn"  ,  1     ,"C");    /*append ����  */
		field.add("filler3"    ,  1     ,"B");    /*Filler3      */
		field.add("file_size"  ,  4     ,"B");    /*���ϻ�����   */
		field.add("userid"     , 64     ,"C");    /*�����ID     */
		field.add("password"   , 64     ,"C");    /*�н�����     */
		field.add("file_name"  ,128     ,"C");    /*���ϸ�       */
		field.add("reserved"   ,768     ,"C");    /*reserved     */
		
		len = field.getFieldListLen();

		this.dataMap = new MapForm();
        
	}
	
	
	/**
	 * ============================================================================
	 *        �� �� �� �� �� �� �� �� �� �� �� �� ��
	 * ============================================================================
	 */
	
	/**
	 * GR6653 HEAD : ���漼 ���ೳ�� ���� ����
	 */
	public void GR6653_Head_FieldList() {
		// TODO Auto-generated constructor stub
		
		field = new FieldList();

		/*
		 * �����Ľ̿�
		 * */	
		field.add("BUSINESSGUBUN"  ,  6    ,"C");    /*��������         */
		field.add("DATAGUBUN"      ,  2    ,"H");    /*�����ͱ���       */
		field.add("SEQNO"          ,  7    ,"H");    /*�Ϸù�ȣ         */
		field.add("BANKCODE"       ,  3    ,"C");    /*(����)�����ڵ�   */
		field.add("TOTALCOUNT"     ,  7    ,"H");    /*�� DATA RECORD ��*/
		field.add("TRANSDATE"      ,  8    ,"C");    /*��������         */
		field.add("FILLER"         ,197    ,"C");    /*FILLER           */

		len = field.getFieldListLen();

		this.dataMap = new MapForm();
        
	}
	/**
	 * GR6653 DATA : ���漼 ���ೳ�� ���� ����
	 */
	public void GR6653_Data_FieldList() {
		// TODO Auto-generated constructor stub
		
		field = new FieldList();

		/*
		 * �����Ľ̿�
		 * */	
		field.add("BUSINESSGUBUN"  , 6    ,"C");    /*��������            */
		field.add("DATAGUBUN"      , 2    ,"H");    /*�����ͱ���          */
		field.add("SEQNO"          , 7    ,"H");    /*�Ϸù�ȣ            */
		field.add("PUBGCODE"       , 2    ,"H");    /*�������з��ڵ�    */
		field.add("JIRONO"         , 7    ,"H");    /*������Ȯ�ι� ���� */
		field.add("JUMINNO"        ,13    ,"C");    /*�ֹι�ȣ            */
		field.add("ECHENO"         ,27    ,"C");    /*��ü��ȣ            */
		field.add("BANKCODE"       , 3    ,"H");    /*(����)�����ڵ�      */
		field.add("JANGPNO"        , 6    ,"H");    /*��ǥ��ȣ            */
		field.add("SUNAPJUMCODE"   , 7    ,"C");    /*������������ڵ�    */
		field.add("SUNAPDATE"      , 8    ,"C");    /*��������            */
		field.add("HGDATE"         , 8    ,"C");    /*ȸ������            */
		field.add("NAPBU_GUBUN"    , 1    ,"C");    /*���⳻�ı���        */
		field.add("NAPBU_AMT"      ,12    ,"H");    /*���αݾ�            */
		field.add("OCR_BAND1"      ,54    ,"C");    /*OCRBAND1            */
		field.add("OCR_BAND2"      ,54    ,"C");    /*OCRBAND2            */
		field.add("JANGPGRP"       , 3    ,"H");    /*��ǥ������ȣ        */
		field.add("PROCGUBUN"      , 1    ,"H");    /*ó������:���ͳ�����4*/
		field.add("SUSU_AMT"       , 4    ,"H");    /*������              */
		field.add("FILLER"         , 5    ,"H");    /*FILLER              */

		len = field.getFieldListLen();

		this.dataMap = new MapForm();
        
	}
	
	/**
	 * GR6653 TAILER : ���漼 ���ೳ�� ���� ����
	 */
	public void GR6653_Tailer_FieldList() {
		// TODO Auto-generated constructor stub
		
		field = new FieldList();

		/*
		 * �����Ľ̿�
		 * */	
		field.add("BUSINESSGUBUN"  ,  6    ,"C");    /*��������         */
		field.add("DATAGUBUN"      ,  2    ,"H");    /*�����ͱ���       */
		field.add("SEQNO"          ,  7    ,"H");    /*�Ϸù�ȣ         */
		field.add("BANKCODE"       ,  3    ,"C");    /*(����)�����ڵ�   */
		field.add("TOTALCOUNT"     ,  7    ,"H");    /*�� DATA RECORD ��*/
		field.add("TOTALPAYMONEY"  , 14    ,"H");    /*�հ�ݾ�         */
		field.add("FILLER"         ,191    ,"C");    /*FILLER           */

		len = field.getFieldListLen();

		this.dataMap = new MapForm();
        
	}
	
	/**
	 * GR6655 HEAD : ���漼 ���� ����
	 */
	public void GR6655_Head_FieldList() {
		// TODO Auto-generated constructor stub
		
		field = new FieldList();

		/*
		 * �����Ľ̿�
		 * */	
		field.add("BUSINESSGUBUN"  ,  6    ,"C");    /*��������         */
		field.add("DATAGUBUN"      ,  2    ,"H");    /*�����ͱ���       */
		field.add("SEQNO"          ,  7    ,"H");    /*�Ϸù�ȣ         */
		field.add("BANKCODE"       ,  3    ,"C");    /*(����)�����ڵ�   */
		field.add("TOTALCOUNT"     ,  7    ,"H");    /*�� DATA RECORD ��*/
		field.add("TRANSDATE"      ,  8    ,"C");    /*��������         */
		field.add("FILLER"         ,197    ,"C");    /*FILLER           */

		len = field.getFieldListLen();

		this.dataMap = new MapForm();
        
	}
	/**
	 * GR6655 DATA : ���漼 ���� ����
	 */
	public void GR6655_Data_FieldList() {
		// TODO Auto-generated constructor stub
		
		field = new FieldList();

		/*
		 * �����Ľ̿�
		 * */	
		field.add("BUSINESSGUBUN"  , 6    ,"C");    /*��������            */
		field.add("DATAGUBUN"      , 2    ,"H");    /*�����ͱ���          */
		field.add("SEQNO"          , 7    ,"H");    /*�Ϸù�ȣ            */
		field.add("PUBGCODE"       , 2    ,"H");    /*�������з��ڵ�    */
		field.add("JIRONO"         , 7    ,"H");    /*������Ȯ�ι� ���� */
		field.add("JUMINNO"        ,13    ,"C");    /*�ֹι�ȣ            */
		field.add("ECHENO"         ,27    ,"C");    /*��ü��ȣ            */
		field.add("BANKCODE"       , 3    ,"H");    /*(����)�����ڵ�      */
		field.add("JANGPNO"        , 6    ,"H");    /*��ǥ��ȣ            */
		field.add("SUNAPJUMCODE"   , 7    ,"C");    /*������������ڵ�    */
		field.add("SUNAPDATE"      , 8    ,"C");    /*��������            */
		field.add("HGDATE"         , 8    ,"C");    /*ȸ������            */
		field.add("NAPBU_GUBUN"    , 1    ,"C");    /*���⳻�ı���        */
		field.add("NAPBU_AMT"      ,12    ,"H");    /*���αݾ�            */
		field.add("OCR_BAND1"      ,54    ,"C");    /*OCRBAND1            */
		field.add("OCR_BAND2"      ,54    ,"C");    /*OCRBAND2            */
		field.add("JANGPGRP"       , 3    ,"H");    /*��ǥ������ȣ        */
		field.add("PROCGUBUN"      , 1    ,"H");    /*ó������:���ͳ�����4*/
		field.add("SUSU_AMT"       , 4    ,"H");    /*������              */
		field.add("FILLER"         , 5    ,"H");    /*FILLER              */

		len = field.getFieldListLen();

		this.dataMap = new MapForm();
        
	}
	/**
	 * GR6655 TAILER : ���漼 ���� ����
	 */
	public void GR6655_Tailer_FieldList() {
		// TODO Auto-generated constructor stub
		
		field = new FieldList();

		/*
		 * �����Ľ̿�
		 * */	
		field.add("BUSINESSGUBUN"  ,  6    ,"C");    /*��������         */
		field.add("DATAGUBUN"      ,  2    ,"H");    /*�����ͱ���       */
		field.add("SEQNO"          ,  7    ,"H");    /*�Ϸù�ȣ         */
		field.add("BANKCODE"       ,  3    ,"C");    /*(����)�����ڵ�   */
		field.add("TOTALCOUNT"     ,  7    ,"H");    /*�� DATA RECORD ��*/
		field.add("TOTALPAYMONEY"  , 14    ,"H");    /*�հ�ݾ�         */
		field.add("FILLER"         ,417    ,"C");    /*FILLER           */

		len = field.getFieldListLen();

		this.dataMap = new MapForm();
        
	}
	
	/**
	 * GR6654 HEAD : ���漼 ���ೳ�� ���� ����
	 */
	public void GR6654_Head_FieldList() {
		// TODO Auto-generated constructor stub
		
		field = new FieldList();

		/*
		 * �����Ľ̿�
		 * */	
		field.add("BUSINESSGUBUN"  ,  6    ,"C");    /*��������         */
		field.add("DATAGUBUN"      ,  2    ,"H");    /*�����ͱ���       */
		field.add("SEQNO"          ,  7    ,"H");    /*�Ϸù�ȣ         */
		field.add("BANKCODE"       ,  3    ,"C");    /*(����)�����ڵ�   */
		field.add("TOTALCOUNT"     ,  7    ,"H");    /*�� DATA RECORD ��*/
		field.add("TRANSDATE"      ,  8    ,"C");    /*��������         */
		field.add("FILLER"         ,417    ,"C");    /*FILLER           */

		len = field.getFieldListLen();

		this.dataMap = new MapForm();
        
	}
	
	
	
	/**
	 * GR6654 DATA : ���漼 ���ೳ�� ���� ����
	 */
	public void GR6654_Data_FieldList() {
		// TODO Auto-generated constructor stub
		
		field = new FieldList();

		/*
		 * �����Ľ̿�
		 * */	
		field.add("BUSINESSGUBUN"  , 6    ,"C");    /*��������            */
		field.add("DATAGUBUN"      , 2    ,"H");    /*�����ͱ���          */
		field.add("SEQNO"          , 7    ,"H");    /*�Ϸù�ȣ            */
		field.add("PUBGCODE"       , 2    ,"H");    /*�������з��ڵ�    */
		field.add("JIRONO"         , 7    ,"H");    /*������Ȯ�ι� ���� */
		field.add("EPAY_NO"        ,17    ,"C");    /*���ڳ��ι�ȣ        */
		field.add("FILLER1"        ,12    ,"C");    /*���� ���� FIELD 1   */
		field.add("REGNO"          ,13    ,"C");    /*�ֹ�(�����,����)��Ϲ�ȣ*/
		field.add("SIDO"           , 2    ,"H");    /*�õ�����            */
		field.add("PUB_ORG"        , 3    ,"H");    /*�������(�ñ���)    */
		field.add("CHK1"           , 1    ,"H");    /*������ȣ 1          */
		field.add("ACCT"           , 2    ,"H");    /*ȸ��                */
		field.add("GWAMOK"         , 6    ,"H");    /*����                */
		field.add("GWAYM"          , 6    ,"H");    /*�������            */
		field.add("DIV"            , 1    ,"H");    /*���                */
		field.add("HACD"           , 3    ,"H");    /*������(���鵿)      */
		field.add("TAX_SNO"        , 6    ,"H");    /*���� ��ȣ           */
		field.add("CHK2"           , 1    ,"H");    /*������ȣ 2          */
		field.add("REGNM"          ,40    ,"C");    /*������ ����         */
		field.add("SUM_B_AMT"      ,15    ,"H");    /*���⳻�ݾ�          */
		field.add("SUM_F_AMT"      ,15    ,"H");    /*�����ıݾ�          */
		field.add("CHK3"           , 1    ,"H");    /*������ȣ 3          */
		field.add("TAX_STD"        ,15    ,"H");    /*����ǥ��            */
		field.add("MNTX"           ,15    ,"H");    /*����                */
		field.add("MNTX_ADTX"      ,15    ,"H");    /*��������            */
		field.add("CPTX"           ,15    ,"H");    /*���ð�ȹ��          */
		field.add("CPTX_ADTX"      ,15    ,"H");    /*���ð�ȹ������      */
		field.add("CFTX"           ,15    ,"H");    /*�����ü���(��Ư)    */
		field.add("CFTX_ADTX"      ,15    ,"H");    /*�����ü���(��Ư)�����*/
		field.add("LETX"           ,15    ,"H");    /*������              */
		field.add("LETX_ADTX"      ,15    ,"H");    /*�����������        */
		field.add("DUE_DT"         , 8    ,"H");    /*���⳻����          */
		field.add("DUE_F_DT"       , 8    ,"H");    /*����������          */
		field.add("CHK4"           , 1    ,"H");    /*������ȣ 4          */
		field.add("FILLER2"        , 1    ,"H");    /*���� ���� FIELD 2   */
		field.add("CHK5"           , 1    ,"H");    /*������ȣ 5          */
		field.add("GWASESTATE"     ,60    ,"C");    /*��������            */
		field.add("GWASEDESC"      ,20    ,"C");    /*��������            */
		field.add("TAX_DT"         , 8    ,"C");    /*�ΰ�����            */
		field.add("ATUOREG"        , 1    ,"C");    /*�ڵ���ü��Ͽ���    */
		field.add("SUNAP_BRC"      , 7    ,"H");    /*���������ڵ�        */
		field.add("SUNAP_DTM"      , 14   ,"H");    /*�����Ͻ�(�Ǽ�����)  */
		field.add("NAPGB"          , 1    ,"C");    /*���ⱸ��            */
		field.add("PRCGB"          , 1    ,"C");    /*ó������            */
		field.add("FILLER"         ,16    ,"C");    /*FILLER              */

		len = field.getFieldListLen();

		this.dataMap = new MapForm();
        
	}
	
	/**
	 * GR6654 HEAD : ���漼 ���ೳ�� ���� ����
	 */
	public void GR6654_Tailer_FieldList() {
		// TODO Auto-generated constructor stub
		
		field = new FieldList();

		/*
		 * �����Ľ̿�
		 * */	
		field.add("BUSINESSGUBUN"  ,  6    ,"C");    /*��������         */
		field.add("DATAGUBUN"      ,  2    ,"H");    /*�����ͱ���       */
		field.add("SEQNO"          ,  7    ,"H");    /*�Ϸù�ȣ         */
		field.add("BANKCODE"       ,  3    ,"C");    /*(����)�����ڵ�   */
		field.add("TOTALCOUNT"     ,  7    ,"H");    /*�� DATA RECORD ��*/
		field.add("TOTALPAYMONEY"  , 14    ,"H");    /*�հ�ݾ�         */
		field.add("FILLER"         ,411    ,"C");    /*FILLER           */

		len = field.getFieldListLen();

		this.dataMap = new MapForm();
        
	}

	/**
	 * GR6677 HEAD : ���ϼ��� ��� ��������
	 */
	public void GR6677_Head_FieldList() {
		// TODO Auto-generated constructor stub
		
		field = new FieldList();

		/*
		 * �����Ľ̿�
		 * */	
		field.add("BUSINESSGUBUN"  ,  6    ,"C");    /*��������         */
		field.add("DATAGUBUN"      ,  2    ,"H");    /*�����ͱ���       */
		field.add("SEQNO"          ,  7    ,"H");    /*�Ϸù�ȣ         */
		field.add("BANKCODE"       ,  3    ,"C");    /*(����)�����ڵ�   */
		field.add("TOTALCOUNT"     ,  7    ,"H");    /*�� DATA RECORD ��*/
		field.add("TRANSDATE"      ,  8    ,"C");    /*��������         */
		field.add("FILLER"         ,267    ,"C");    /*FILLER           */
		
		len = field.getFieldListLen();

		this.dataMap = new MapForm();
        
	}
	/**
	 * GR6677 DATA : ���ϼ��� ��� ��������
	 */
	public void GR6677_Data_FieldList() {
		// TODO Auto-generated constructor stub
		
		field = new FieldList();

		/*
		 * �����Ľ̿�
		 * */	
		field.add("BUSINESSGUBUN"  , 6    ,"C");    /*��������            */
		field.add("DATAGUBUN"      , 2    ,"H");    /*�����ͱ���          */
		field.add("SEQNO"          , 7    ,"H");    /*�Ϸù�ȣ            */
		field.add("PUBGCODE"       , 2    ,"H");    /*�������з��ڵ�    */
		field.add("JIRONO"         , 7    ,"H");    /*������Ȯ�ι� ���� */
		field.add("CUSTNO"         ,30    ,"H");    /*���밡��ȣ          */
		field.add("KUMGOCD"        , 3    ,"H");    /*�ݰ������ڵ�        */
		field.add("FILLER1"        , 6    ,"H");    /*��ǥó������ �ǵ���ȣ*/
		field.add("SUNAPJUMCODE"   , 7    ,"H");    /*������������ڵ�    */
		field.add("NAPDT"          , 8    ,"H");    /*��������            */
		field.add("ACCTDT"         , 8    ,"H");    /*ȸ������(��ü����)  */
		field.add("NAPGB"          , 1    ,"H");    /*1:���⳻2:������3:����*/
		field.add("NAPBU_AMT"      ,12    ,"H");    /*���αݾ�            */
		field.add("SANG_AMT"       ,10    ,"H");    /*������ݾ�           */
		field.add("HA_AMT"         ,10    ,"H");    /*�ϼ����ݾ�           */
		field.add("JIHA_AMT"       ,10    ,"H");    /*���ϼ��ݾ�           */
		field.add("WATER_AMT"      ,10    ,"H");    /*���̿�ݾ�           */
		field.add("OCR_BAND"       ,108   ,"H");    /*OCRBAND              */
		field.add("FILLER2"        , 9    ,"H");    /*��ǥó������ ������ȣ*/
		field.add("PROCGUBUN"      , 1    ,"H");    /*ó������:���ͳ�����4*/
		field.add("SUSU_AMT"       , 4    ,"H");    /*������              */
		field.add("DANGMON"        , 2    ,"H");    /*11 �����, 22ü���� */
		field.add("TAX_YM"         , 6    ,"H");    /*�ΰ����            */
		field.add("ADMIN_NO"       , 8    ,"H");    /*������ȣ            */
		field.add("EPAY_NO"        ,17    ,"H");    /*���ڳ��ι�ȣ        */
		field.add("FILLER3"        , 6    ,"H");    /*FILLER              */

		len = field.getFieldListLen();

		this.dataMap = new MapForm();
        
	}

	/**
	 * GR6677 TAILER : ���ϼ��� ��� ��������
	 */
	public void GR6677_Tailer_FieldList() {
		// TODO Auto-generated constructor stub
		
		field = new FieldList();

		/*
		 * �����Ľ̿�
		 * */	
		field.add("BUSINESSGUBUN"  ,  6    ,"C");    /*��������         */
		field.add("DATAGUBUN"      ,  2    ,"H");    /*�����ͱ���       */
		field.add("SEQNO"          ,  7    ,"H");    /*�Ϸù�ȣ         */
		field.add("BANKCODE"       ,  3    ,"C");    /*(����)�����ڵ�   */
		field.add("TOTALCOUNT"     ,  7    ,"H");    /*�� DATA RECORD ��*/
		field.add("TOTALPAYMONEY"  , 14    ,"H");    /*�հ�ݾ�         */
		field.add("FILLER"         ,261    ,"C");    /*FILLER           */

		len = field.getFieldListLen();

		this.dataMap = new MapForm();
        
	}
	
	/**
	 * GR6675 HEAD : ���ϼ��� ��� ���ೳ����������
	 */
	public void GR6675_Head_FieldList() {
		// TODO Auto-generated constructor stub
		
		field = new FieldList();

		/*
		 * �����Ľ̿�
		 * */	
		field.add("BUSINESSGUBUN"  ,  6    ,"C");    /*��������         */
		field.add("DATAGUBUN"      ,  2    ,"H");    /*�����ͱ���       */
		field.add("SEQNO"          ,  7    ,"H");    /*�Ϸù�ȣ         */
		field.add("BANKCODE"       ,  3    ,"C");    /*(����)�����ڵ�   */
		field.add("TOTALCOUNT"     ,  7    ,"H");    /*�� DATA RECORD ��*/
		field.add("TRANSDATE"      ,  8    ,"C");    /*��������         */
		field.add("FILLER"         ,267    ,"C");    /*FILLER           */
		
		len = field.getFieldListLen();

		this.dataMap = new MapForm();
        
	}
	/**
	 * GR6675 DATA : ���ϼ��� ��� ���ೳ����������
	 */
	public void GR6675_Data_FieldList() {
		// TODO Auto-generated constructor stub
		
		field = new FieldList();

		/*
		 * �����Ľ̿�
		 * */	
		field.add("BUSINESSGUBUN"  , 6    ,"C");    /*��������              */
		field.add("DATAGUBUN"      , 2    ,"H");    /*�����ͱ���            */
		field.add("SEQNO"          , 7    ,"H");    /*�Ϸù�ȣ              */
		field.add("PUBGCODE"       , 2    ,"H");    /*�������з��ڵ�      */
		field.add("JIRONO"         , 7    ,"H");    /*������Ȯ�ι� ����   */
		field.add("CUSTNO"         ,30    ,"H");    /*���밡��ȣ            */
		field.add("KUMGOCD"        , 3    ,"H");    /*�ݰ������ڵ�          */
		field.add("FILLER1"        , 6    ,"H");    /*��ǥó������ �ǵ���ȣ */
		field.add("SUNAPJUMCODE"   , 7    ,"H");    /*������������ڵ�      */
		field.add("NAPDT"          , 8    ,"H");    /*��������              */
		field.add("ACCTDT"         , 8    ,"H");    /*ȸ������(��ü����)    */
		field.add("NAPGB"          , 1    ,"H");    /*1:���⳻2:������3:����*/
		field.add("NAPBU_AMT"      ,12    ,"H");    /*���αݾ�              */
		field.add("SANG_AMT"       ,10    ,"H");    /*������ݾ�            */
		field.add("HA_AMT"         ,10    ,"H");    /*�ϼ����ݾ�            */
		field.add("JIHA_AMT"       ,10    ,"H");    /*���ϼ��ݾ�            */
		field.add("WATER_AMT"      ,10    ,"H");    /*���̿�ݾ�            */
		field.add("OCR_BAND"       ,108   ,"H");    /*OCRBAND               */
		field.add("FILLER2"        , 9    ,"H");    /*��ǥó������ ������ȣ */
		field.add("PROCGUBUN"      , 1    ,"H");    /*ó������:���ͳ�����4  */
		field.add("SUSU_AMT"       , 4    ,"H");    /*������                */
		field.add("DANGMON"        , 2    ,"H");    /*11 �����, 22ü����   */
		field.add("TAX_YM"         , 6    ,"H");    /*�ΰ����              */
		field.add("ADMIN_NO"       , 8    ,"H");    /*������ȣ              */
		field.add("EPAY_NO"        ,17    ,"H");    /*���ڳ��ι�ȣ          */
		field.add("FILLER3"        , 6    ,"H");    /*FILLER                */

		len = field.getFieldListLen();

		this.dataMap = new MapForm();
        
	}

	/**
	 * GR6675 TAILER : ���ϼ��� ��� ���ೳ����������
	 */
	public void GR6675_Tailer_FieldList() {
		// TODO Auto-generated constructor stub
		
		field = new FieldList();

		/*
		 * �����Ľ̿�
		 * */	
		field.add("BUSINESSGUBUN"  ,  6    ,"C");    /*��������         */
		field.add("DATAGUBUN"      ,  2    ,"H");    /*�����ͱ���       */
		field.add("SEQNO"          ,  7    ,"H");    /*�Ϸù�ȣ         */
		field.add("BANKCODE"       ,  3    ,"C");    /*(����)�����ڵ�   */
		field.add("TOTALCOUNT"     ,  7    ,"H");    /*�� DATA RECORD ��*/
		field.add("TOTALPAYMONEY"  , 14    ,"H");    /*�հ�ݾ�         */
		field.add("FILLER"         ,261    ,"C");    /*FILLER           */

		len = field.getFieldListLen();

		this.dataMap = new MapForm();
        
	}
	
	/**
	 * GR6676 HEAD : ���ϼ��� ��� ���ೳ�κ������
	 */
	public void GR6676_Head_FieldList() {
		// TODO Auto-generated constructor stub
		
		field = new FieldList();

		/*
		 * �����Ľ̿�
		 * */	
		field.add("BUSINESSGUBUN"  ,  6    ,"C");    /*��������         */
		field.add("DATAGUBUN"      ,  2    ,"H");    /*�����ͱ���       */
		field.add("SEQNO"          ,  7    ,"H");    /*�Ϸù�ȣ         */
		field.add("BANKCODE"       ,  3    ,"H");    /*(����)�����ڵ�   */
		field.add("TOTALCOUNT"     ,  7    ,"H");    /*�� DATA RECORD ��*/
		field.add("TRANSDATE"      ,  8    ,"C");    /*��������         */
		field.add("FILLER"         ,447    ,"C");    /*FILLER           */
		
		len = field.getFieldListLen();

		this.dataMap = new MapForm();
        
	}
	/**
	 * GR6676 DATA : ���ϼ��� ��� ���ೳ�κ������
	 */
	public void GR6676_Data_FieldList() {
		// TODO Auto-generated constructor stub
		
		field = new FieldList();

		/*
		 * �����Ľ̿�
		 * */	
		field.add("BUSINESSGUBUN"      , 6       , "C");      /*��������                                          */
		field.add("DATAGUBUN"          , 2       , "H");      /*�����ͱ���                                        */
		field.add("SEQNO"              , 7       , "H");      /*�Ϸù�ȣ                                          */
		field.add("PUBGCODE"           , 2       , "H");      /*�������з��ڵ�                                  */
		field.add("JIRONO"             , 7       , "H");      /*������Ȯ�ι� ����                               */
		field.add("CUSTNO"             ,30       , "C");      /*���밡��ȣ                                        */
		field.add("TAX_YM"             , 6       , "H");      /*�ΰ����                                          */
		field.add("DANGGUBUN"          , 1       , "C");      /*��� ����                                         */
		field.add("NPNO"               , 10      , "C");      /*���� ��ȣ                                         */
		field.add("REGNM"              ,20       , "C");      /*����                                              */
    	field.add("BNAPDATE"           ,  8      , "H");      /*���⳻ ������              | ���� ������          */
		field.add("BNAPAMT"            , 10      , "H");      /*���⳻ �ݾ�                | ü����               */
		field.add("ANAPAMT"            , 10      , "H");      /*������ �ݾ�                                       */
		field.add("GUM2"               ,  1      , "H");      /*������ȣ 2                                        */
		field.add("BSAMT"              , 10      , "H");      /*��������⳻�ݾ�           |  ����� ü����       */
		field.add("BHAMT"              , 10      , "H");      /*�ϼ������⳻�ݾ�           |  �ϼ��� ü����       */
		field.add("BGAMT"              , 10      , "H");      /*���ϼ����⳻�ݾ�           |  ���ϼ� ü����       */
		field.add("BMAMT"              , 10      , "H");      /*���̿�δ�ݳ��⳻�ݾ�     |  ���̿�δ��ü����  */
		field.add("ASAMT"              , 10      , "H");      /*����������ıݾ�                                  */
		field.add("AHAMT"              , 10      , "H");      /*�ϼ��������ıݾ�                                  */
		field.add("AGAMT"              , 10      , "H");      /*���ϼ������ıݾ�                                  */
		field.add("AMAMT"              , 10      , "H");      /*���̿�δ�ݳ����ıݾ�                            */
		field.add("ANAPDATE"           ,  8      , "H");      /*������ ������                                     */
		field.add("GUM3"               ,  1      , "H");      /*������ȣ 3                                        */
		field.add("CNAPTERM"           , 16      , "H");      /*ü�� �Ⱓ                                         */
		field.add("ADDR"               , 60      , "C");      /*�ּ�                                              */
		field.add("USETERM"            , 16      , "H");      /*��� �Ⱓ                                         */
		field.add("AUTOREG"            ,  1      , "C");      /*�ڵ���ü ��� ����                                */
		field.add("SNAP_BANK_CODE"     ,  7      , "H");      /*�������� ���� �ڵ�                                */
		field.add("SNAP_SYMD"          , 14      , "H");      /*���� �Ͻ�                                         */
		field.add("NAPGUBUN"           ,  1      , "C");      /*���� ���� ����                                    */
		field.add("ETC1"               ,  9      , "C");      /*���� ���� FIELD                                   */
		field.add("CUST_ADMIN_NUM"     , 30      , "C");      /*��������ȣ                                      */
		field.add("OCR"                ,108      , "C");      /*OCR BAND                                          */
		field.add("PRCGB"              , 1       , "C");      /*ó������                                          */
		field.add("FILLER"             , 8       , "C");      /*FILLER                                            */


		len = field.getFieldListLen();

		this.dataMap = new MapForm();
        
	}

	/**
	 * GR6676 TAILER : ���ϼ��� ��� ���ೳ�κ������
	 */
	public void GR6676_Tailer_FieldList() {
		// TODO Auto-generated constructor stub
		
		field = new FieldList();

		/*
		 * �����Ľ̿�
		 * */	
		field.add("BUSINESSGUBUN"  ,  6    ,"C");    /*��������         */
		field.add("DATAGUBUN"      ,  2    ,"H");    /*�����ͱ���       */
		field.add("SEQNO"          ,  7    ,"H");    /*�Ϸù�ȣ         */
		field.add("BANKCODE"       ,  3    ,"H");    /*(����)�����ڵ�   */
		field.add("TOTALCOUNT"     ,  7    ,"H");    /*�� DATA RECORD ��*/
		field.add("TOTALPAYMONEY"  , 14    ,"H");    /*�հ�ݾ�         */
		field.add("FILLER"         ,441    ,"C");    /*FILLER           */

		len = field.getFieldListLen();

		this.dataMap = new MapForm();
        
	}
	
	

	/**
	 * GR6681 HEAD : ���ܼ��� ��������
	 */
	public void GR6681_Head_FieldList() {
		// TODO Auto-generated constructor stub
		
		field = new FieldList();

		/*
		 * �����Ľ̿�
		 * */	
		field.add("BUSINESSGUBUN"  ,  6    ,"C");    /*��������         */
		field.add("DATAGUBUN"      ,  2    ,"H");    /*�����ͱ���       */
		field.add("SEQNO"          ,  7    ,"H");    /*�Ϸù�ȣ         */
		field.add("BANKCODE"       ,  3    ,"C");    /*(����)�����ڵ�   */
		field.add("TOTALCOUNT"     ,  7    ,"H");    /*�� DATA RECORD ��*/
		field.add("TRANSDATE"      ,  8    ,"C");    /*��������         */
		field.add("FILLER"         ,197    ,"C");    /*FILLER           */
		
		len = field.getFieldListLen();

		this.dataMap = new MapForm();
        
	}
	/**
	 * GR6681 DATA : ���ܼ��� ��������
	 */
	public void GR6681_Data_FieldList() {
		// TODO Auto-generated constructor stub
		
		field = new FieldList();

		/*
		 * �����Ľ̿�
		 * */	
		field.add("BUSINESSGUBUN"  , 6    ,"C");    /*��������            */
		field.add("DATAGUBUN"      , 2    ,"H");    /*�����ͱ���          */
		field.add("SEQNO"          , 7    ,"H");    /*�Ϸù�ȣ            */
		field.add("PUBGCODE"       , 2    ,"H");    /*�������з��ڵ�    */
		field.add("JIRONO"         , 7    ,"H");    /*������Ȯ�ι� ���� */
		field.add("REG_NO"         ,13    ,"C");    /*�ֹι�ȣ            */
		field.add("TAX_NO"         ,27    ,"C");    /*��ü��ȣ            */
		field.add("KUMGOCD"        , 3    ,"C");    /*�ݰ������ڵ�        */
		field.add("FILLER1"        , 6    ,"H");    /*��ǥó������ �ǵ���ȣ*/
		field.add("SUNAPJUMCODE"   , 7    ,"H");    /*������������ڵ�    */
		field.add("NAPDT"          , 8    ,"H");    /*��������            */
		field.add("ACCTDT"         , 8    ,"H");    /*ȸ������(��ü����)  */
		field.add("NAPGB"          , 1    ,"H");    /*1:���⳻2:������3:����*/
		field.add("NAPBU_AMT"      ,12    ,"H");    /*���αݾ�            */
		field.add("OCR_BAND"       ,108   ,"H");    /*OCRBAND              */
		field.add("FILLER2"        , 3    ,"H");    /*��ǥó������ ������ȣ*/
		field.add("PROCGUBUN"      , 1    ,"H");    /*ó������:���ͳ�����4*/
		field.add("SUSU_AMT"       , 4    ,"H");    /*������              */
		field.add("FILLER3"        , 5    ,"H");    /*FILLER              */

		len = field.getFieldListLen();

		this.dataMap = new MapForm();
        
	}

	/**
	 * GR6681 TAILER : ���ܼ��� ��������
	 */
	public void GR6681_Tailer_FieldList() {
		// TODO Auto-generated constructor stub
		
		field = new FieldList();

		/*
		 * �����Ľ̿�
		 * */	
		field.add("BUSINESSGUBUN"  ,  6    ,"C");    /*��������         */
		field.add("DATAGUBUN"      ,  2    ,"H");    /*�����ͱ���       */
		field.add("SEQNO"          ,  7    ,"H");    /*�Ϸù�ȣ         */
		field.add("BANKCODE"       ,  3    ,"C");    /*(����)�����ڵ�   */
		field.add("TOTALCOUNT"     ,  7    ,"H");    /*�� DATA RECORD ��*/
		field.add("TOTALPAYMONEY"  , 14    ,"H");    /*�հ�ݾ�         */
		field.add("FILLER"         ,191    ,"C");    /*FILLER           */

		len = field.getFieldListLen();

		this.dataMap = new MapForm();
        
	}
	
	/**
	 * GR6694 HEAD : ���ܼ��� ���ೳ����������
	 */
	public void GR6694_Head_FieldList() {
		// TODO Auto-generated constructor stub
		
		field = new FieldList();

		/*
		 * �����Ľ̿�
		 * */	
		field.add("BUSINESSGUBUN"  ,  6    ,"C");    /*��������         */
		field.add("DATAGUBUN"      ,  2    ,"H");    /*�����ͱ���       */
		field.add("SEQNO"          ,  7    ,"H");    /*�Ϸù�ȣ         */
		field.add("BANKCODE"       ,  3    ,"C");    /*(����)�����ڵ�   */
		field.add("TOTALCOUNT"     ,  7    ,"H");    /*�� DATA RECORD ��*/
		field.add("TRANSDATE"      ,  8    ,"C");    /*��������         */
		field.add("FILLER"         ,197    ,"C");    /*FILLER           */
		
		len = field.getFieldListLen();

		this.dataMap = new MapForm();
        
	}
	/**
	 * GR6694 DATA : ���ܼ��� ���ೳ����������
	 */
	public void GR6694_Data_FieldList() {
		// TODO Auto-generated constructor stub
		
		field = new FieldList();

		/*
		 * �����Ľ̿�
		 * */	
		field.add("BUSINESSGUBUN"  , 6    ,"C");    /*��������              */
		field.add("DATAGUBUN"      , 2    ,"H");    /*�����ͱ���            */
		field.add("SEQNO"          , 7    ,"H");    /*�Ϸù�ȣ              */
		field.add("PUBGCODE"       , 2    ,"H");    /*�������з��ڵ�      */
		field.add("JIRONO"         , 7    ,"H");    /*������Ȯ�ι� ����   */
		field.add("REG_NO"         ,13    ,"C");    /*�ֹι�ȣ              */
		field.add("EPAY_NO"        ,27    ,"C");    /*���ڳ��ι�ȣ          */
		field.add("KUMGOCD"        , 3    ,"C");    /*�ݰ������ڵ�          */
		field.add("FILLER1"        , 6    ,"H");    /*��ǥó������ �ǵ���ȣ */
		field.add("SUNAPJUMCODE"   , 7    ,"H");    /*������������ڵ�      */
		field.add("NAPDT"          , 8    ,"H");    /*����������              */
		field.add("ACCTDT"         , 8    ,"H");    /*���ೳ�� �������(��ü����)*/
		field.add("NAPGB"          , 1    ,"H");    /*1:���⳻2:������3:����*/
		field.add("NAPBU_AMT"      ,12    ,"H");    /*���αݾ�              */
		field.add("OCR_BAND"       ,108   ,"H");    /*OCRBAND               */
		field.add("FILLER2"        , 3    ,"H");    /*��ǥó������ ������ȣ */
		field.add("PROCGUBUN"      , 1    ,"H");    /*ó������:���ͳ�����4  */
		field.add("SUSU_AMT"       , 4    ,"H");    /*������                */
		field.add("FILLER3"        , 5    ,"H");    /*FILLER                */

		len = field.getFieldListLen();

		this.dataMap = new MapForm();
        
	}

	/**
	 * GR6694 TAILER : ���ܼ��� ���ೳ����������
	 */
	public void GR6694_Tailer_FieldList() {
		// TODO Auto-generated constructor stub
		
		field = new FieldList();

		/*
		 * �����Ľ̿�
		 * */	
		field.add("BUSINESSGUBUN"  ,  6    ,"C");    /*��������         */
		field.add("DATAGUBUN"      ,  2    ,"H");    /*�����ͱ���       */
		field.add("SEQNO"          ,  7    ,"H");    /*�Ϸù�ȣ         */
		field.add("BANKCODE"       ,  3    ,"C");    /*(����)�����ڵ�   */
		field.add("TOTALCOUNT"     ,  7    ,"H");    /*�� DATA RECORD ��*/
		field.add("TOTALPAYMONEY"  , 14    ,"H");    /*�հ�ݾ�         */
		field.add("FILLER"         ,191    ,"C");    /*FILLER           */

		len = field.getFieldListLen();

		this.dataMap = new MapForm();
        
	}
	
	
	
	/**
	 * GR6695 HEAD : ���ܼ��� ���ೳ�κ������
	 */
	public void GR6695_Head_FieldList() {
		// TODO Auto-generated constructor stub
		
		field = new FieldList();

		/*
		 * �����Ľ̿�
		 * */	
		field.add("BUSINESSGUBUN"  ,  6    ,"C");    /*��������         */
		field.add("DATAGUBUN"      ,  2    ,"H");    /*�����ͱ���       */
		field.add("SEQNO"          ,  7    ,"H");    /*�Ϸù�ȣ         */
		field.add("BANKCODE"       ,  3    ,"H");    /*(����)�����ڵ�   */
		field.add("TOTALCOUNT"     ,  7    ,"H");    /*�� DATA RECORD ��*/
		field.add("TRANSDATE"      ,  8    ,"H");    /*��������         */
		field.add("FILLER"         ,667    ,"C");    /*FILLER           */
		
		len = field.getFieldListLen();

		this.dataMap = new MapForm();
        
	}
	/**
	 * GR6695 DATA : ���ܼ��� ���ೳ�κ������
	 */
	public void GR6695_Data_FieldList() {
		// TODO Auto-generated constructor stub
		
		field = new FieldList();

		/*
		 * �����Ľ̿�
		 * */			
		field.add("BUSINESSGUBUN"       , 6  ,  "C"  );  /*��������                  */
		field.add("DATAGUBUN"           , 2  ,  "H"  );  /*�����ͱ���                */
		field.add("SEQNO"               , 7  ,  "H"  );  /*�Ϸù�ȣ                  */
		field.add("PUBGCODE"            , 2  ,  "H"  );  /*�������з��ڵ�          */
		field.add("JIRONO"              , 7  ,  "H"  );  /*������Ȯ�ι� ����       */
		field.add("EPAY_NO"            	, 19 ,  "C"  );  /*���ڳ��ι�ȣ              */
		field.add("REG_NO"              , 13 ,  "C"  );  /*�ֹι�ȣ                  */
		field.add("FIELD1"              , 2  ,  "C"  );  /*���� ���� FIELD 1         */
		field.add("BUGWA_GB"            , 1  ,  "H"  );  /*�������� 		         */
		field.add("SEMOK_CD"          	, 6  ,  "H"  );  /*����/����                 */
		field.add("SEMOK_NM"            , 50 ,  "C"  );  /*����/�����               */
		field.add("GBN"              	, 1  ,  "C"  );  /*����                      */
		field.add("OCR_BD"             	, 108,  "C"  );  /*OCR���                   */
		field.add("NAP_NAME"            , 40 ,  "C"  );  /*������ ����               */
		field.add("NAP_BFAMT"           , 15 ,  "H"  );  /*���⳻ �ݾ�               */
		field.add("NAP_AFAMT"           , 15 ,  "H"  );  /*������ �ݾ�               */
	    field.add("GUKAMT"          	, 11 ,  "H"  );  /*���� 	                 */
	    field.add("GUKAMT_ADD"          , 11 ,  "H"  );  /*���� �����               */
	    field.add("SIDO_AMT"            , 11 ,  "H"  );  /*�õ���                    */
	    field.add("SIDO_AMT_ADD"        , 11 ,  "H"  );  /*�õ��� �����             */
	    field.add("SIGUNGU_AMT"         , 11 ,  "H"  );  /*�ñ�����      	         */
	    field.add("SIGUNGU_AMT_ADD"     , 11 ,  "H"  );  /*�ñ����� �����   	     */
	    field.add("BUNAP_AMT"           , 11 ,  "H"  );  /*�г�����/���             */
	    field.add("BUNAP_AMT_ADD"       , 11 ,  "H"  );  /*�г�����/��� �����      */
	    field.add("FIELD2"              , 11 ,  "C"  );  /*���� ���� FIELD 2         */
	    field.add("NAP_BFDATE"          , 8  ,  "H"  );  /*������ (���⳻)           */
	    field.add("NAP_AFDATE"          , 8  ,  "H"  );  /*������ (������)           */
	    field.add("GWASE_ITEM"          , 100,  "C"  );  /*�������                  */
	    field.add("BUGWA_TAB"          	, 150,  "C"  );  /*�ΰ�����                  */
	    field.add("GOJI_DATE"   		, 8  ,  "H"  );  /*�����ڷ� �߻�����         */
	    field.add("OUTO_ICHE_GB"        , 1  ,  "C"  );  /*�ڵ���ü��Ͽ���          */
	    field.add("SUNAB_BANK_CD"       , 7  ,  "H"  );  /*�������� ���� �ڵ�        */
	    field.add("RECIP_DATE"          , 14 ,  "H"  );  /*�����Ͻ�			         */
	    field.add("NAPGI_BA_GB"         , 1  ,  "C"  );  /*���⳻�ı���              */
	    field.add("PRCGB"               , 1  ,  "C"  );  /*ó������                  */
	    field.add("FILED3"          	, 9  ,  "C"  );  /*�������� FIELD 3          */		

		len = field.getFieldListLen();

		this.dataMap = new MapForm();
        
	}

	/**
	 * GR6695 TAILER : ���ܼ��� ���ೳ�κ������
	 */
	public void GR6695_Tailer_FieldList() {
		// TODO Auto-generated constructor stub
		
		field = new FieldList();

		/*
		 * �����Ľ̿�
		 * */	
		field.add("BUSINESSGUBUN"  ,  6    ,"C");    /*��������         */
		field.add("DATAGUBUN"      ,  2    ,"H");    /*�����ͱ���       */
		field.add("SEQNO"          ,  7    ,"H");    /*�Ϸù�ȣ         */
		field.add("BANKCODE"       ,  3    ,"H");    /*(����)�����ڵ�   */
		field.add("TOTALCOUNT"     ,  7    ,"H");    /*�� DATA RECORD ��*/
		field.add("TOTALPAYMONEY"  , 14    ,"H");    /*�հ�ݾ�         */
		field.add("FILLER"         ,661    ,"C");    /*FILLER           */

		len = field.getFieldListLen();

		this.dataMap = new MapForm();
        
	}
	

	/**
	 * GR6685 HEAD : ȯ�氳���δ�� ��������
	 */
	public void GR6685_Head_FieldList() {
		// TODO Auto-generated constructor stub
		
		field = new FieldList();

		/*
		 * �����Ľ̿�
		 * */	
		field.add("BUSINESSGUBUN"  ,  6    ,"C");    /*��������         */
		field.add("DATAGUBUN"      ,  2    ,"H");    /*�����ͱ���       */
		field.add("SEQNO"          ,  7    ,"H");    /*�Ϸù�ȣ         */
		field.add("BANKCODE"       ,  3    ,"C");    /*(����)�����ڵ�   */
		field.add("TOTALCOUNT"     ,  7    ,"H");    /*�� DATA RECORD ��*/
		field.add("TRANSDATE"      ,  8    ,"C");    /*��������         */
		field.add("FILLER"         ,197    ,"C");    /*FILLER           */
		
		len = field.getFieldListLen();

		this.dataMap = new MapForm();
        
	}
	/**
	 * GR6685 DATA : ȯ�氳���δ�� ��������
	 */
	public void GR6685_Data_FieldList() {
		// TODO Auto-generated constructor stub
		
		field = new FieldList();

		/*
		 * �����Ľ̿�
		 * */	
		field.add("BUSINESSGUBUN"  , 6    ,"C");    /*��������            */
		field.add("DATAGUBUN"      , 2    ,"H");    /*�����ͱ���          */
		field.add("SEQNO"          , 7    ,"H");    /*�Ϸù�ȣ            */
		field.add("PUBGCODE"       , 2    ,"H");    /*�������з��ڵ�    */
		field.add("JIRONO"         , 7    ,"H");    /*������Ȯ�ι� ���� */
		field.add("REG_NO"         ,13    ,"C");    /*�ֹι�ȣ            */
		field.add("TAX_NO"         ,27    ,"C");    /*��ü��ȣ            */
		field.add("KUMGOCD"        , 3    ,"C");    /*�ݰ������ڵ�        */
		field.add("FILLER1"        , 6    ,"H");    /*��ǥó������ �ǵ���ȣ*/
		field.add("SUNAPJUMCODE"   , 7    ,"H");    /*������������ڵ�    */
		field.add("NAPDT"          , 8    ,"H");    /*��������            */
		field.add("ACCTDT"         , 8    ,"H");    /*ȸ������(��ü����)  */
		field.add("NAPGB"          , 1    ,"H");    /*1:���⳻2:������3:����*/
		field.add("NAPBU_AMT"      ,12    ,"H");    /*���αݾ�            */
		field.add("OCR_BAND"       ,108   ,"H");    /*OCRBAND              */
		field.add("FILLER2"        , 3    ,"H");    /*��ǥó������ ������ȣ*/
		field.add("PROCGUBUN"      , 1    ,"H");    /*ó������:���ͳ�����4*/
		field.add("SUSU_AMT"       , 4    ,"H");    /*������              */
		field.add("FILLER3"        , 5    ,"H");    /*FILLER              */

		len = field.getFieldListLen();

		this.dataMap = new MapForm();
        
	}

	/**
	 * GR6685 TAILER : ȯ�氳���δ�� ��������
	 */
	public void GR6685_Tailer_FieldList() {
		// TODO Auto-generated constructor stub
		
		field = new FieldList();

		/*
		 * �����Ľ̿�
		 * */	
		field.add("BUSINESSGUBUN"  ,  6    ,"C");    /*��������         */
		field.add("DATAGUBUN"      ,  2    ,"H");    /*�����ͱ���       */
		field.add("SEQNO"          ,  7    ,"H");    /*�Ϸù�ȣ         */
		field.add("BANKCODE"       ,  3    ,"C");    /*(����)�����ڵ�   */
		field.add("TOTALCOUNT"     ,  7    ,"H");    /*�� DATA RECORD ��*/
		field.add("TOTALPAYMONEY"  , 14    ,"H");    /*�հ�ݾ�         */
		field.add("FILLER"         ,191    ,"C");    /*FILLER           */

		len = field.getFieldListLen();

		this.dataMap = new MapForm();
        
	}
	
	/**
	 * GR6696 HEAD : ȯ�氳���δ�� ���ೳ����������
	 */
	public void GR6696_Head_FieldList() {
		// TODO Auto-generated constructor stub
		
		field = new FieldList();

		/*
		 * �����Ľ̿�
		 * */	
		field.add("BUSINESSGUBUN"  ,  6    ,"C");    /*��������         */
		field.add("DATAGUBUN"      ,  2    ,"H");    /*�����ͱ���       */
		field.add("SEQNO"          ,  7    ,"H");    /*�Ϸù�ȣ         */
		field.add("BANKCODE"       ,  3    ,"C");    /*(����)�����ڵ�   */
		field.add("TOTALCOUNT"     ,  7    ,"H");    /*�� DATA RECORD ��*/
		field.add("TRANSDATE"      ,  8    ,"C");    /*��������         */
		field.add("FILLER"         ,197    ,"C");    /*FILLER           */
		
		len = field.getFieldListLen();

		this.dataMap = new MapForm();
        
	}
	/**
	 * GR6696 DATA : ȯ�氳���δ�� ���ೳ����������
	 */
	public void GR6696_Data_FieldList() {
		// TODO Auto-generated constructor stub
		
		field = new FieldList();

		/*
		 * �����Ľ̿�
		 * */	
		field.add("BUSINESSGUBUN"  , 6    ,"C");    /*��������              */
		field.add("DATAGUBUN"      , 2    ,"H");    /*�����ͱ���            */
		field.add("SEQNO"          , 7    ,"H");    /*�Ϸù�ȣ              */
		field.add("PUBGCODE"       , 2    ,"H");    /*�������з��ڵ�      */
		field.add("JIRONO"         , 7    ,"H");    /*������Ȯ�ι� ����   */
		field.add("REG_NO"         ,13    ,"C");    /*�ֹι�ȣ              */
		field.add("TAX_NO"         ,27    ,"C");    /*��ü��ȣ              */
		field.add("KUMGOCD"        , 3    ,"C");    /*�ݰ������ڵ�          */
		field.add("FILLER1"        , 6    ,"H");    /*��ǥó������ �ǵ���ȣ */
		field.add("SUNAPJUMCODE"   , 7    ,"H");    /*������������ڵ�      */
		field.add("NAPDT"          , 8    ,"H");    /*��������              */
		field.add("ACCTDT"         , 8    ,"H");    /*ȸ������(��ü����)    */
		field.add("NAPGB"          , 1    ,"H");    /*1:���⳻2:������3:����*/
		field.add("NAPBU_AMT"      ,12    ,"H");    /*���αݾ�              */
		field.add("OCR_BAND"       ,108   ,"H");    /*OCRBAND               */
		field.add("FILLER2"        , 3    ,"H");    /*��ǥó������ ������ȣ */
		field.add("PROCGUBUN"      , 1    ,"H");    /*ó������:���ͳ�����4  */
		field.add("SUSU_AMT"       , 4    ,"H");    /*������                */
		field.add("FILLER3"        , 5    ,"H");    /*FILLER                */

		len = field.getFieldListLen();

		this.dataMap = new MapForm();
        
	}

	/**
	 * GR6696 TAILER : ȯ�氳���δ�� ���ೳ����������
	 */
	public void GR6696_Tailer_FieldList() {
		// TODO Auto-generated constructor stub
		
		field = new FieldList();

		/*
		 * �����Ľ̿�
		 * */	
		field.add("BUSINESSGUBUN"  ,  6    ,"C");    /*��������         */
		field.add("DATAGUBUN"      ,  2    ,"H");    /*�����ͱ���       */
		field.add("SEQNO"          ,  7    ,"H");    /*�Ϸù�ȣ         */
		field.add("BANKCODE"       ,  3    ,"C");    /*(����)�����ڵ�   */
		field.add("TOTALCOUNT"     ,  7    ,"H");    /*�� DATA RECORD ��*/
		field.add("TOTALPAYMONEY"  , 14    ,"H");    /*�հ�ݾ�         */
		field.add("FILLER"         ,191    ,"C");    /*FILLER           */

		len = field.getFieldListLen();

		this.dataMap = new MapForm();
        
	}
	
	
	/**
	 * GR6697 HEAD : ȯ�氳���δ�� ���ೳ�κ������
	 */
	public void GR6697_Head_FieldList() {
		// TODO Auto-generated constructor stub
		
		field = new FieldList();

		/*
		 * �����Ľ̿�
		 * */	
		field.add("BUSINESSGUBUN"  ,  6    ,"C");    /*��������         */
		field.add("DATAGUBUN"      ,  2    ,"H");    /*�����ͱ���       */
		field.add("SEQNO"          ,  7    ,"H");    /*�Ϸù�ȣ         */
		field.add("BANKCODE"       ,  3    ,"C");    /*(����)�����ڵ�   */
		field.add("TOTALCOUNT"     ,  7    ,"H");    /*�� DATA RECORD ��*/
		field.add("TRANSDATE"      ,  8    ,"H");    /*��������         */
		field.add("FILLER"         ,417    ,"C");    /*FILLER           */
		
		len = field.getFieldListLen();

		this.dataMap = new MapForm();
        
	}
	/**
	 * GR6697 DATA : ȯ�氳���δ�� ���ೳ�κ������
	 */
	public void GR6697_Data_FieldList() {
		// TODO Auto-generated constructor stub
		
		field = new FieldList();

		/*
		 * �����Ľ̿�
		 * */	
		field.add("BUSINESSGUBUN"        , 6  ,  "C"  );  /*��������                  */
		field.add("DATAGUBUN"            , 2  ,  "H"  );  /*�����ͱ���                */
		field.add("SEQNO"                , 7  ,  "H"  );  /*�Ϸù�ȣ                  */
		field.add("PUBGCODE"             , 2  ,  "H"  );  /*�������з��ڵ�          */
		field.add("JIRONO"               , 7  ,  "H"  );  /*������Ȯ�ι� ����       */
		field.add("ETAX_NO"              , 29 ,  "C"  );  /*���ڳ��ι�ȣ              */
		field.add("JUMIN_NO"             , 13 ,  "C"  );  /*�ֹ�(�����,����)��Ϲ�ȣ */
		field.add("SIDO"                 , 2  ,  "H"  );  /*�õ�                      */
		field.add("GU_CODE"              , 3  ,  "H"  );  /*�������(�ñ���)          */
		field.add("CONFIRM_NO1"          , 1  ,  "H"  );  /*������ȣ 1                */
		field.add("HCALVAL"              , 2  ,  "H"  );  /*ȸ��                      */
		field.add("GWA_MOK"              , 6  ,  "H"  );  /*����/����                 */
		field.add("TAX_YYMM"             , 6  ,  "H"  );  /*�⵵/���                 */
		field.add("KIBUN"                , 1  ,  "H"  );  /*����                      */
		field.add("DONG_CODE"            , 3  ,  "H"  );  /*������(���鵿)            */
		field.add("GWASE_NO"             , 6  ,  "H"  );  /*���� ��ȣ                 */
		field.add("CONFIRM_NO2"          , 1  ,  "H"  );  /*������ȣ 2                */
		field.add("NAP_NAME"             , 40 ,  "C"  );  /*������ ����               */
		field.add("NAP_BFAMT"            , 15 ,  "H"  );  /*���⳻ �ݾ�               */
		field.add("NAP_AFAMT"            , 15 ,  "H"  );  /*������ �ݾ�               */
	    field.add("CONFIRM_NO3"          , 1  ,  "H"  );  /*������ȣ 3                */
	    field.add("GWASE_RULE"           , 15 ,  "H"  );  /*���� ǥ��                 */
	    field.add("BONSE"                , 15 ,  "H"  );  /*�δ��                    */
	    field.add("BONSE_ADD"            , 15 ,  "H"  );  /*�δ�� �����             */
	    field.add("DOSISE"               , 15 ,  "H"  );  /*�̼� �δ��               */
	    field.add("DOSISE_ADD"           , 15 ,  "H"  );  /*�̼� �δ�� �����        */
	    field.add("GONGDONGSE"           , 15 ,  "H"  );  /*���� ���� FIELD 1         */
	    field.add("GONGDONGSE_ADD"       , 15 ,  "H"  );  /*���� ���� FIELD 2         */
	    field.add("EDUSE"                , 15 ,  "H"  );  /*���� ���� FIELD 3         */
	    field.add("EDUSE_ADD"            , 15 ,  "H"  );  /*���� ���� FIELD 4         */
	    field.add("NAP_BFDATE"           , 8  ,  "H"  );  /*������ (���⳻)           */
	    field.add("NAP_AFDATE"           , 8  ,  "H"  );  /*������ (������)           */
	    field.add("CONFIRM_NO4"          , 1  ,  "H"  );  /*������ȣ 4                */
	    field.add("FILLER1"              , 1  ,  "H"  );  /*�ʷ�                      */
	    field.add("CONFIRM_NO5"          , 1  ,  "H"  );  /*������ȣ 5                */
	    field.add("GWASE_DESC"           , 60 ,  "C"  );  /*���� ����                 */
	    field.add("GWASE_PUB_DESC"       , 20 ,  "C"  );  /*���� ǥ�� ����            */
	    field.add("GOJICR_DATE"          , 8  ,  "H"  );  /*�����ڷ� �߻�����         */
	    field.add("JADONG_YN"            , 1  ,  "C"  );  /*�ڵ���ü ��� ����        */
	    field.add("JIJUM_CODE"           , 7  ,  "H"  );  /*�������� ���� �ڵ�        */
	    field.add("NAPBU_DATE"           , 14 ,  "H"  );  /*���� �Ͻ�                 */
	    field.add("NP_BAF_GUBUN"         , 1  ,  "C"  );  /*���� ���� ����            */
	    field.add("TAX_GOGI_GUBUN"       , 1  ,  "C"  );  /*���� ���� ����            */
	    field.add("JA_GOGI_GUBUN"        , 1  ,  "C"  );  /*��ǥ ���� ����            */
	    field.add("PRCGB"                , 1  ,  "C"  );  /*ó������                  */
	    field.add("RESERV2"              , 14 ,  "C"  );  /*���� ���� FIELD 5         */	  

		len = field.getFieldListLen();

		this.dataMap = new MapForm();
        
	}

	/**
	 * GR6697 TAILER : ȯ�氳���δ�� ���ೳ�κ������
	 */
	public void GR6697_Tailer_FieldList() {
		// TODO Auto-generated constructor stub
		
		field = new FieldList();

		/*
		 * �����Ľ̿�
		 * */	
		field.add("BUSINESSGUBUN"  ,  6    ,"C");    /*��������         */
		field.add("DATAGUBUN"      ,  2    ,"H");    /*�����ͱ���       */
		field.add("SEQNO"          ,  7    ,"H");    /*�Ϸù�ȣ         */
		field.add("BANKCODE"       ,  3    ,"C");    /*(����)�����ڵ�   */
		field.add("TOTALCOUNT"     ,  7    ,"H");    /*�� DATA RECORD ��*/
		field.add("TOTALPAYMONEY"  , 14    ,"H");    /*�հ�ݾ�         */
		field.add("FILLER"         ,411    ,"C");    /*FILLER           */

		len = field.getFieldListLen();

		this.dataMap = new MapForm();
        
	}
	

	/**
	 * ============================================================================
	 *        �� �� �� �� ��
	 * ============================================================================
	 */
	
	/**
	 * ���� ������ �۽��� ��� ����
	 */
	public byte[] getBuff(MapForm mapForm) throws Exception{
		
		dataMap = mapForm;

		return getBuff();
	}
	
	/**
	 * 
	 * @param msgType
	 * @param srcId
	 * @return
	 * @throws Exception
	 */
	public byte[] getBuff() throws Exception{

		byte[] headBuf ;
				
		headBuf =  field.makeMessageByte(dataMap);
		
		return headBuf;		
		
	}
	
	/**
	 * �����Ľ�...
	 * @param buffer
	 * @return
	 * @throws Exception
	 */
	public MapForm parseBuff(byte[] buffer) throws Exception{

		try {
			dataMap = field.parseMessage(buffer, 0);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw e;
		}

		return dataMap;
	}

	public int getLen() {
		// TODO Auto-generated method stub
		return len;
	}


	public FieldList getFieldList() {
		// TODO Auto-generated method stub
		return field;
	}
	
	/**
	 * 
	 * @param fldName
	 * @return
	 */
	public String getField(String fldName) {
		
		return (String) dataMap.getMap(fldName);
	}

	/**
	 * 
	 * @param key
	 * @param val
	 */
	public void setField(String key, String val) {
		// TODO Auto-generated method stub
		this.dataMap.setMap(key, val);
	}

	public MapForm getDataMap() {
		// TODO Auto-generated method stub
		return this.dataMap;
	}

	/**
	 * 
	 * @param mapForm
	 */
	public void setDataMap(MapForm mapForm) {
		// TODO Auto-generated method stub
		this.dataMap = mapForm;
	}
	
}
