/**
 * 
 */
package com.uc.bs.cyber;

/**
 * @author Administrator
 *
 */
public class CbConst extends com.uc.core.Constants{
	
	/**
	 * ������Ʈ ���۽� �Ʒ��� 3�� ���� �����Ͽ� ����ϼ���
	 */
	public static final int SERVICE_MODE = 1;		// ���� ��� 0:�⺻, 1:������ ����, 2: �׽�Ʈ������ ��ȸ
	
	/***************************************************
	 * �ý��� �ڵ�
	 */
	public static final String SYS_DSC_CORE   = "01";		// �ھ�ý���
	public static final String SYS_DSC_CTAG   = "02";		// ��ûAgent
	public static final String SYS_DSC_KGIS   = "03";		// �ݰ�ý���
	public static final String SYS_DSC_KGCF   = "04";		// CashFlow

	/**
	 * �ۼ��� �����ڵ�
	 */
	public static final String TRS_DSC_SEND   = "01";		// �۽�
	public static final String TRS_DSC_RECV   = "02";		// ����
	
	/**
	 * ���� ó�������ڵ� 
	 */
	public static final String FILE_STS_DSC_REQ   = "00";		// �۽ſ�û
	public static final String FILE_STS_DSC_RCV   = "01";		// �۽ſ�û
	public static final String FILE_STS_DSC_ING   = "02";		// �۽ſ�û
	public static final String FILE_STS_DSC_DON   = "03";		// �۽ſ�û
	public static final String FILE_STS_DSC_ERR   = "09";		// �۽ſ�û
	
	/**
	 * �������� �ڵ� 
	 */
	public static final String FILE_KIND_DSC01	= "01";		// ���漼��������
	public static final String FILE_KIND_DSC03 	= "03";		// ���ϼ�����������
	public static final String FILE_KIND_DSC04   = "04";	// ���漼 �����������
	public static final String FILE_KIND_DSC05   = "05";	// ���ϼ��� ��������
	public static final String FILE_KIND_DSC06   = "06";	// ���漼 �ϰ���������
	public static final String FILE_KIND_DSC07   = "07";	// ���ϼ��� ������������	
	public static final String FILE_KIND_DSC99   = "99";    // ������ ����
	
	public static final String FILE_KIND_DSC11   = "11";    // �ϰ�/���� �ϰ�ó��
	public static final String FILE_KIND_DSC12   = "12";    // �ڱݿ����³��� �ϰ�ó��
	public static final String FILE_KIND_DSC13   = "13";    // ��������������
	public static final String FILE_KIND_DSC14   = "14";    // �ݰ��ڵ���������
	public static final String FILE_KIND_DSC15   = "15";    // �ŷ���������
	public static final String FILE_KIND_DSC16   = "16";    // ����ϰ�������
	public static final String FILE_KIND_DSC17   = "17";    // �䱸�Ұ��� �ŷ�����
	
	/* OCR */
	public static final String FILE_KIND_DSC20   = "20";    // ���ڼ���������
	public static final String FILE_KIND_DSC21   = "21";    // ��ü����
	
	/**
	 * Login ID 
	 */
    public static final String SAVE_USER_ID_KEY       = "user_id";
	public static final String SAVE_USER_ID_CHECK_KEY = "save_id";
    public static final String LOGIN_USER_ID          = "login_user_id";
    
    /**
     *  ���� �ŷ������ڵ�
     */
	public static final String TR_KIND_REQ = "0200";	// �䱸
	public static final String TR_KIND_RES = "0210";	// ����
	public static final String TR_KIND_RREQ = "0400";	// ��ó��Ȯ�ο䱸
	public static final String TR_KIND_RRES = "0410";	// ��ó��Ȯ������	
	
	
	/* �����ڵ� */
	public static final String BANK_CD = "032";
	
	public static final int   TRS_STS_CD_OK      = 10;
	public static final int   TRS_STS_CD_ER      = 20;
	
	public static final Object SHPROC_DSC_REG     = "01";
	public static final Object SHPROC_DSC_FORMAT  = "02";
	public static final Object SHPROC_DSC_GJERR   = "03";
	public static final Object SHPROC_DSC_GJEND   = "04";
	public static final Object SHPROC_DSC_SEND    = "05";
	
	/**
	 * ������� ���������ڵ� ���漼:01, ���ܼ���:02
	 */
	public static final String VAJOB_DSC_LT       = "01";
	public static final String VAJOB_DSC_SO       = "02";
}

