/**
 *  �ֽý��۸� : ���̹����漼û ��ȭ
 *  ��  ��  �� : ���ܼ��� ���γ��� ����ȸ ����
 *  ��  ��  �� : �λ�����-���̹���û 
 *               ����ó��
 *               ��������� ���ŵ� ������ ���ؼ� ������������Ʈ�� �����ϰ� 
 *               ��ȸ����� �����Ѵ�.
 *  Ŭ����  ID : Bs273002Service
 *  ����  �̷� :
 * ------------------------------------------------------------------------
 *  �ۼ���     �Ҽ�  		 ����      Tag   ����
 * ------------------------------------------------------------------------   
 * Ȳ����   �ٻ�(��)    2011.06.13   %01%  �ű��ۼ�
 *  
 */
package com.uc.bs.cyber.service.kf273002;

import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;

import com.uc.core.MapForm;
import com.uc.core.spring.service.IbatisBaseService;
import com.uc.bs.cyber.CbUtil;

/**
 * @author Administrator
 *
 */
public class Kf273002Service {

	protected Log log = LogFactory.getLog(this.getClass());
	
	private ApplicationContext appContext = null;
	
	private IbatisBaseService sqlService_cyber = null;
		
	private Kf273002FieldList bsField = null;
	
	CbUtil cUtil = null;
	
	/**
	 * ������
	 */
	public Kf273002Service(ApplicationContext appContext) {
		// TODO Auto-generated constructor stub
		
		setAppContext(appContext);
		
		bsField = new Kf273002FieldList();
		
	}

	/* appContext property ���� */
	public void setAppContext(ApplicationContext appContext) {
		
		this.appContext = appContext;
		
		sqlService_cyber = (IbatisBaseService) this.appContext.getBean("baseService_cyber");

	}

	public ApplicationContext getAppContext() {
		return appContext;
	}
	
	/**
	 * ��Ÿ���Ա� ���γ��� ����ȸ ó��...
	 * */
	public byte[] chk_kf_273002(MapForm headMap, MapForm mf) throws Exception {
		
				
		MapForm sendMap = new MapForm();
		
		/*��Ÿ���Ա� ����������ȸ �� ��� */
		String resCode = "000";       /*�����ڵ�*/
//		String giro_no = "1000685";   /*�λ�������ڵ�*/
		String giro_no = "1500172";    /*�λ�������ڵ�- ����e���ν������� ��Ÿ���Ա� �����ڵ� ����*/
		String sg_code = "26";        /*�λ�ñ���ڵ�*/
		
		try{
			cUtil = CbUtil.getInstance();
			
			log.info(cUtil.msgPrint("9", "S","BP273002 chk_kf_273002()", resCode));
			
			/*����üũ*/
			/*���*/
			if(!headMap.getMap("GPUB_CODE").equals(sg_code)){
				resCode = "122";
			}
			/*�����������ڵ�*/
			if(!headMap.getMap("GJIRO_NO").equals(giro_no)){
				resCode = "123";
			}
			
			/* ��ȸ���� �ڸ���(���ι�ȣ �߶� ��ȸ���� �����) */
			mf.setMap("SGG_COD", mf.getMap("ETAXNO").toString().substring(0, 3)); 	//��û�ڵ�
			mf.setMap("ACCT_COD", mf.getMap("ETAXNO").toString().substring(4, 6));  //ȸ���ڵ�
			mf.setMap("TAX_ITEM", mf.getMap("ETAXNO").toString().substring(6, 12)); //����/�����ڵ�
			mf.setMap("TAX_YY", mf.getMap("ETAXNO").toString().substring(12, 16));  //�����⵵
			mf.setMap("TAX_MM", mf.getMap("ETAXNO").toString().substring(16, 18));	//������
			mf.setMap("TAX_DIV", mf.getMap("ETAXNO").toString().substring(18, 19));	//����ڵ�
			mf.setMap("HACD", mf.getMap("ETAXNO").toString().substring(19, 22));	//�������ڵ�
			mf.setMap("TAX_SNO", mf.getMap("ETAXNO").toString().substring(22, 28));	//������ȣ
			
			ArrayList<MapForm>  bsCmd273002List  =  sqlService_cyber.queryForList("BS273002.SELECT_RECIP_LIST", mf);
			
			log.debug("TAXNO = [" + mf.getMap("ETAXNO") + "]");
			log.debug("JUMIN_NO = [" + mf.getMap("JUMIN_NO") + "]");
			
			if(bsCmd273002List.size() <= 0){
				resCode = "112";  // Error : ���γ�������
			} else if(bsCmd273002List.size() > 1){
				resCode = "094";  // Error : ��ȸ 2�� �̻�
			} else {
				
				if ( bsCmd273002List.size() > 0 ) {
					
					MapForm recipform = new MapForm();
					
					recipform = bsCmd273002List.get(0);
					
					sendMap.setMap("GB", mf.getMap("GB"));  						/*��ȸ���� */
					sendMap.setMap("ETAXNO", mf.getMap("ETAXNO"));  				/*���ι�ȣ */
					sendMap.setMap("EPAYNO", mf.getMap("EPAYNO"));  				/*���ڳ��ι�ȣ              */
					sendMap.setMap("NAPBU_JUMIN_NO", mf.getMap("NAPBU_JUMIN_NO"));  /*�ֹ�(�����,����)��Ϲ�ȣ */
					sendMap.setMap("JUMIN_NO", mf.getMap("NAPBU_JUMIN_NO"));  		/*�ֹ�(�����,����)��Ϲ�ȣ */
					sendMap.setMap("FIELD1", "");  									/*�������� FIELD 1          */
					sendMap.setMap("BUGWA_STAT", recipform.getMap("BUGWA_STAT"));  	/*��������                  */
					sendMap.setMap("GWA_MOK", recipform.getMap("TAX_ITEM"));  		/*����/����                 */
					sendMap.setMap("GWA_NM", recipform.getMap("TAX_NM"));  			/*����/�����               */
					sendMap.setMap("FIELD2", "");  									/*�������� FIELD 2          */
					sendMap.setMap("OCR_BD", recipform.getMap("OCR_BD"));  			/*OCR���                   */
					sendMap.setMap("NAP_NAME", recipform.getMap("REG_NM"));  		/*������ ����               */
					sendMap.setMap("NAP_BFAMT", recipform.getMap("PAYMENT_DATE1")); /*���⳻ �ݾ�               */
					sendMap.setMap("NAP_AFAMT", Long.parseLong(recipform.getMap("PAYMENT_DATE1").toString()) + Long.parseLong(recipform.getMap("AFTPAYMENT_DATE1").toString()));  /*������ �ݾ�               */
					sendMap.setMap("NATN_TAX", recipform.getMap("NATN_TAX"));  		/*����                      */
					sendMap.setMap("NATN_TAX_ADD", recipform.getMap("NATN_TAX_ADD"));/*���������               */
					sendMap.setMap("SIDO_TAX", recipform.getMap("SIDO_TAX"));  		/*�õ���                    */
					sendMap.setMap("SIDO_TAX_ADD", recipform.getMap("SIDO_TAX_ADD"));/*�õ��� �����            */
					sendMap.setMap("SIGUNGU_TAX", recipform.getMap("SIGUNGU_TAX"));  /*�ñ�����                 */
					sendMap.setMap("SIGUNGU_TAX_ADD", recipform.getMap("SIGUNGU_TAX_ADD"));  /*�ñ����� �����  */
					sendMap.setMap("BUN_AMT", "");  								/*�г�����/���             */
					sendMap.setMap("BUN_AMT_ADD", "");  							/*�г�����/��� �����      */      
					sendMap.setMap("FIELD3", "");  									/*�������� FIELD 3          */
					sendMap.setMap("DUE_DT", recipform.getMap("DUE_DT"));  			/*������ (���⳻)           */
					sendMap.setMap("DUE_F_DT", recipform.getMap("DUE_F_DT"));  		/*������ (������)           */
					sendMap.setMap("TAX_GDS", recipform.getMap("TAX_GDS"));  		/*���� ���                 */
					sendMap.setMap("TAX_NOTICE_TITLE", recipform.getMap("TAX_NOTICE_TITLE"));  /*�ΰ� ����      */
					sendMap.setMap("LEVY_DETAIL1", recipform.getMap("LEVY_DETAIL6"));/*�����ڷ� �߻�����        */
					sendMap.setMap("NAPBU_SYS", recipform.getMap("NAPBU_SYS"));  	/*�����̿� �ý���           */
					sendMap.setMap("BANK_CD", recipform.getMap("BANK_CD"));  		/*�������� ���� �ڵ�        */
					sendMap.setMap("RECIP_DATE", recipform.getMap("PAY_DT") + "000000");/*���� �Ͻ�             */
					sendMap.setMap("RECIP_AMT", recipform.getMap("SUM_RCP"));  		/*���� �ݾ�                 */
					sendMap.setMap("OUTACCT_NO", "");  								/*��ݰ��¹�ȣ              */
					sendMap.setMap("FIELD4", "");  									/*���� ���� FIELD 4         */
					
				}else{ 
					/* ��ȸ�Ǽ��� ���� ��� ��������
					 * */
					
					resCode = "112";  /*��ȸ��������*/
					
				} 
				
			}

			log.info(cUtil.msgPrint("9", "","KF273002 chk_kf_273002()", resCode));
			
        } catch (Exception e) {
        	
			resCode = "093";
			
			e.printStackTrace();
			log.error("============================================");
			log.error("== chk_bs_273002 Exception(�ý���) ");
			log.error("============================================");
		}
        
        if(!resCode.equals("000")){
        	sendMap = mf;
        }
        
        /*���⼭ ������带 �����Ѵ�. �ٲ�� �κи� �ٽ� �����ؼ� ������ ����*/
        headMap.setMap("RS_FLAG"   , "G");         /*�����̿���(G) */
        headMap.setMap("RESP_CODE" , resCode);     /*�����ڵ�*/
        headMap.setMap("GCJ_NO"    , "0" + sg_code + "0" + CbUtil.lPadString(String.valueOf(SeqNumber()), 8, '0'));     /*�̿��� �Ϸù�ȣ*/
        headMap.setMap("TX_GUBUN"  , "0210");
        
        return bsField.makeSendBuffer(headMap, sendMap);
	}

	/*----------------------------------------------*/
	/* �Ϸù�ȣ �������� �׽�Ʈ*/
	/*----------------------------------------------*/
	private int SeqNumber(){
    	return (Integer)sqlService_cyber.queryForBean("CODMBASE.CODM_GETSEQNO", "00");
    }


}

