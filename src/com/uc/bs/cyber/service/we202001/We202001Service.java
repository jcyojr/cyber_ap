/**
 *  �ֽý��۸� : ���̹����漼û ����e���� ����
 *  ��  ��  �� : ����e����(ȯ�氳���δ��) �ǽð����ΰ������
 *  ��  ��  �� : ���ý�-���̹���û 
 *               ����ó��
 *  Ŭ����  ID : We202001Service
 *  ����  �̷� :
 * ------------------------------------------------------------------------
 *  �ۼ���     �Ҽ�  		 ����      Tag   ����
 * ------------------------------------------------------------------------   
 * �ҵ���   ��ä��(��)  2011.04.24     %01%  �ű��ۼ�
 * ��â��   ��ä��(��)  2013.11.04     %02%  ����e���ΰ���(���ܼ���, ȯ�氳���δ��) �߰�
 */
package com.uc.bs.cyber.service.we202001;

import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;

import com.uc.core.MapForm;
import com.uc.core.spring.service.IbatisBaseService;


/**
 * @author Administrator
 */
public class We202001Service {

	protected Log log = LogFactory.getLog(this.getClass());
	
	private ApplicationContext appContext = null;
	
	private IbatisBaseService sqlService_cyber = null;
		
	/**
	 * ������
	 */
	public We202001Service(ApplicationContext context) {
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
	 * ȯ�氳���δ�� �ΰ��ڷḦ ��ȸ�ϰ� ����ó���Ѵ�...
	 * */
	public String chkweTax(MapForm mf) throws Exception {
		
		String retStr = "";
		
		try{
			
			ArrayList<MapForm>  weCmdLevyList  =  sqlService_cyber.queryForList("WE202001.SELECT_ENV_SEARCH_LIST", mf);
			
			MapForm mfLevyList = new MapForm();
						
			if (weCmdLevyList.size()  ==  1)   {
				
				if(log.isDebugEnabled()){
					log.info("============================================");
					log.info("== CYB202001 chkweTax �ΰ���ȸ ����!! ( �� 1��)   ");
					log.info("============================================");
				}
				
				/* �ΰ��ڷᰡ 1�� �����ϹǷ� ����
				 * 1. ���ڼ������̺� (���ý���)�ΰ��ڷḦ INSERT�Ѵ�.
				 * 2. ���� ���漼 �ΰ� �� ���̺��� ����Flag�� ������Ʈ �Ѵ�.
				 * */
				
				/**
				 * (����)
				 * 20110830 
				 * �񵿱��Ŀ� ���� ���߳���ó���� ���ؼ� ���ý������� ����ó����(����ó��) �뺸�� �ޱ� ������
				 * ���ý��� ó�������� �ִ� ������ ���̹� ���ڼ��� ���̺� ����ó���Ѵ�..
				 * �� �Ŀ� ���ó�� ����. 
				 */

				mfLevyList = weCmdLevyList.get(0);
				
				/*���ڼ������̺� ���������� ���Ѵ�. �ߺ�ó���� �����ϹǷ�...*/
				mf.setMap("PAY_CNT", sqlService_cyber.getOneFieldInteger("WE202001.TX2231_TB_MaxPayCnt", mfLevyList));
				
				log.debug(" ȯ�氳���δ�� MaxPayCnt = " + mf.getMap("PAY_CNT").toString());
				
				/*���������������� ���� ī���� ���� ��� ���α���ڵ�� ������ �� 3�ڸ��� ����� : 20110711*/
				/*20110816 �ٽ� ������ */
				//if(!((String) mf.getMap("CARD_REQ_NO")).trim().equals("")) { // ī����ι�ȣ�� ������ ī�����
		        //    mf.setMap("BNK_CD", ((String) mf.getMap("BANK_COD")).trim().substring(2, 5));
		        //} else { // �ƴϸ� ������ü ���� 
		        //    mf.setMap("BNK_CD", ((String) mf.getMap("BANK_COD")).trim().substring(0, 3));
		        //}
				/*20110830 ����*/
				mf.setMap("BNK_CD", ((String) mf.getMap("BANK_COD")).trim().substring(0, 3));
				
				/*���ೳ��ó������*/ //PAY_FORM -> R:���ೳ��, I:�����Ű� �� ����(��ó���), Q:��ȸ �� ����(��ó���)
				mf.setMap("RSN_YN", (mf.getMap("PAY_FORM").equals("R")) ? "Y" : "N"); 
				
				/*���ڼ������̺� INSERT : ���߼����� ����ϹǷ� ����ó�����θ� Ȯ������ �ʰ� INSERT */
				int intLog = 0;
                
                intLog = sqlService_cyber.queryForUpdate("WE202001.INSERT_TX2231_TB_EPAY", mf);
				
				if(!(mfLevyList.getMap("SNTG").equals("1") || mfLevyList.getMap("SNTG").equals("2"))) {  /*�̹̼����� �Ǿ� �ִ� �����...(�Ƹ� �λ����� ���ͳݹ�ŷ����) ����*/
					
					/*ȯ�氳���δ�� �ΰ������� ������Ʈ*/
                    if(intLog > 0) {
                        
                        sqlService_cyber.queryForUpdate("WE202001.UPDATE_TX2132_TB_NAPBU_INFO", mf);
                        
                        log.info("ȯ�氳���δ�� ���ڳ���ó�� �Ϸ�! [" + mf.getMap("EPAY_NO") + "]");
                    }
					
				} else {
					log.info("�� ����ó���� ���Դϴ�. ���ڼ������̺� ���߳��ο��� Ȯ���ϼ���!");
					log.info("���ڼ������̺� ����ó���Ǿ����ϴ�.");
				}
				
				
				/*���ճ����̹Ƿ� ��ü�� ������...*/
//				MapForm mpSMS = new MapForm();	
//				
//				mpSMS.setMap("REG_NO"  , mf.getMap("PAY_REG_NO"));
//				mpSMS.setMap("TAX_ITEM", "888888"); /*�ϰ�����*/
//				mpSMS.setMap("SUM_RCP" , mf.getMap("SUM_RCP"));
//				
//				try {
//					sqlService_cyber.procedure("CODMBASE.SMSCALL", mpSMS);
//				}catch (Exception e) {
//					log.info("SMS ��ϵ����� = " + mpSMS.getMaps());
//					log.info("SMS ��Ͽ��� �߻�");
//				}
				
				retStr = "44100-000";
				
			} else if (weCmdLevyList.size()  ==  0)   {
				
				if(log.isDebugEnabled()){
					log.info("============================================");
					log.info("== CYB202001 chkweTax �ΰ���ȸ ����!! ���ڳ��ι�ȣ(" + mf.getMap("EPAY_NO") + ")( �� " + 0 + "��)   ");
					log.info("============================================");
				}
				
				retStr = "44100-101";
				
			} else {
				
				if(log.isDebugEnabled()){
					log.info("============================================");
					log.info("== CYB202001 chkweTax �ΰ���ȸ ����!! ���ڳ��ι�ȣ(" + mf.getMap("EPAY_NO") + ")( �� " + weCmdLevyList.size() + "��)   ");
					log.info("============================================");
				}
				
				retStr = "44100-201";
			}
			
			
		} catch (Exception e) {
			
			e.printStackTrace();
			
			log.error("============================================");
			log.error("== CYB202001 chkweTax Exception(�ý���) ");
			log.error("============================================");

			retStr = "44100-201";
			
		}
		
        return retStr;
	}

	
}
