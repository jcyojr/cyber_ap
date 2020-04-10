/**
 *  �ֽý��۸� : ���̹����漼û ��ȭ
 *  ��  ��  �� : �ǽð��Ѱ����ΰ������
 *  ��  ��  �� : ���ý�-���̹���û 
 *               ����ó��
 *  Ŭ����  ID : We202002Service
 *  ����  �̷� :
 * ------------------------------------------------------------------------
 *  �ۼ���     �Ҽ�  		 ����      Tag   ����
 * ------------------------------------------------------------------------   
 * ��â��   ��ä��(��)  2014.01.02     %01%  �ű��ۼ�
 *  
 */
package com.uc.bs.cyber.service.we202002;
import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;

import com.uc.core.MapForm;
import com.uc.core.spring.service.IbatisBaseService;
/**
 * @author Administrator
 *
 */
public class We202002Service {
	
	protected Log log = LogFactory.getLog(this.getClass());
	
	private ApplicationContext appContext = null;
	
	private IbatisBaseService sqlService_cyber = null;
	/**
	 * 
	 */
	public We202002Service(ApplicationContext context) {
		// TODO Auto-generated constructor stub
		setAppContext(context);
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
	 * ���� �ΰ��ڷḦ ��ȸ�ϰ� ����ó���Ѵ�...
	 * */
	public String chkweTax(MapForm mf) throws Exception {
		
        String retStr = "";
		int insRet = 0;
		try{
			
            ArrayList<MapForm>  weCmdLevyList  =  sqlService_cyber.queryForList("WE202002.TX1302_TB_Select", mf);
			
			if (weCmdLevyList.size()  >  0)   {
				
				if(log.isDebugEnabled()){
					log.info("============================================");
					log.info("== CYB202002 chkweTax �ΰ���ȸ ����!! ( �� " + weCmdLevyList.size() + "��)   ");
					log.info("============================================");
				}
				
				/* ���� �ΰ��ڷᰡ 1�� �̻� �����ϹǷ� ����
				 * 1. ���ڼ������̺� (���ý���)���� �ΰ��ڷḦ INSERT�Ѵ�.
				 * 2. ���� ȯ�氳���δ�ݺΰ� �� ���̺��� ����Flag�� ������Ʈ �Ѵ�.
				 * */
				
				/*���������������� ���� ī���� ���� ��� ���α���ڵ�� ������ �� 3�ڸ��� ����� : 20110711*/
				/*20110816 �ٽ� ������ */
				//if(!((String) mf.getMap("CARD_REQ_NO")).trim().equals("")) { // ī����ι�ȣ�� ������ ī�����
	            //    mf.setMap("BNK_CD", ((String) mf.getMap("BANK_COD")).trim().substring(2, 5));
	            //} else { // �ƴϸ� ������ü ���� 
	            //    mf.setMap("BNK_CD", ((String) mf.getMap("BANK_COD")).trim().substring(0, 3));
	            //}
				/*20110830 ����*/
				mf.setMap("BNK_CD", ((String) mf.getMap("BANK_COD")).trim().substring(0, 3));
				
				/*���ೳ��ó������*/
				mf.setMap("RSN_YN", (mf.getMap("PAY_FORM").equals("R")) ? "Y" : "N"); 
				
				/*���ڼ������̺� INSERT*/
				insRet = sqlService_cyber.queryForUpdate("WE202002.TX1201_TB_Insert", mf);
				log.info("TX1201 INSERT = " + insRet);
				
				/*ȯ�氳���δ�� �ΰ������� ������Ʈ (����ó��: ��Ƽó��(����������))*/
				insRet = sqlService_cyber.queryForMultiUpdate("WE202002.TX1102_TB_Update", weCmdLevyList);
				log.info("TX1102 UPDATE = " + insRet);
				
				/*�ϰ����� ���̺� ������Ʈ*/
				insRet = sqlService_cyber.queryForUpdate("WE202002.TX1301_TB_Update", mf);
				log.info("TX1301 UPDATE = " + insRet);
				
				
				/*���ճ����̹Ƿ� ��ü�� ������...*/
//				MapForm mpSMS = new MapForm();	
//				
//				mpSMS.setMap("REG_NO"  , mf.getMap("REGNO"));
//				mpSMS.setMap("TAX_ITEM", "888888"); /*�ϰ�����*/
//				mpSMS.setMap("SUM_RCP" , mf.getMap("TOT_SUM_B_AMT"));
//				
//				try {
//					sqlService_cyber.procedure("CODMBASE.SMSCALL", mpSMS);
//				}catch (Exception e) {
//					log.info("SMS ��ϵ����� = " + mpSMS.getMaps());
//					log.info("SMS ��Ͽ��� �߻�");
//				}

				retStr = "44110-000";
				
				if(log.isInfoEnabled()){
					log.info("============================================");
					log.info("== CYB202002 chkweTax ����ó��!! ���ճ��ι�ȣ [" + mf.getMap("TOTPAY_NO") + "]");
					log.info("============================================");
				}
				
			} else {
				
				if(log.isInfoEnabled()){
					log.info("============================================");
					log.info("== CYB202002 chkweTax �ΰ���ȸ ����!! ���ճ��ι�ȣ (" + mf.getMap("TOTPAY_NO") + ")( �� " + 0 + "��)   ");
					log.info("============================================");
				}
				
				retStr = "44110-101";
			}
			
        } catch (Exception e) {
			
			e.printStackTrace();
			
			log.error("============================================");
			log.error("== CYB202002 chkweTax Exception(�ý���) ");
			log.error("============================================");
			retStr = "44110-201";
			
		}
		
        return retStr;	
	}
}
