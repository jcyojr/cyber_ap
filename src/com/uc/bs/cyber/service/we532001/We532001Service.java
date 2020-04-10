/**
 *  �ֽý��۸� : ���̹����漼û ��ȭ
 *  ��  ��  �� : �ǽð����ΰ������
 *  ��  ��  �� : ���ý�-���̹���û 
 *               ����ó��
 *  Ŭ����  ID : We532001Service
 *  ����  �̷� :
 * ------------------------------------------------------------------------
 *  �ۼ���     �Ҽ�  		 ����      Tag   ����
 * ------------------------------------------------------------------------   
 * �ҵ���   ��ä��(��)  2011.04.24     %01%  �ű��ۼ�
 *  
 */
package com.uc.bs.cyber.service.we532001;

import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;

import com.uc.core.MapForm;
import com.uc.core.spring.service.IbatisBaseService;


/**
 * @author Administrator
 */
public class We532001Service {

	protected Log log = LogFactory.getLog(this.getClass());
	
	private ApplicationContext appContext = null;
	
	private IbatisBaseService sqlService_cyber = null;
		
	/**
	 * ������
	 */
	public We532001Service(ApplicationContext context) {
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
	 * �ΰ��ڷḦ ��ȸ�ϰ� ����ó���Ѵ�...
	 * */
	public String chkweTax(MapForm mf) throws Exception {
		
		String retStr = "";
		
		try{
			
			ArrayList<MapForm>  weCmdLevyList  =  sqlService_cyber.queryForList("WE532001.TX1102_TB_Only_Select", mf);
			
			MapForm mfLevyList = new MapForm();
						
			if (weCmdLevyList.size()  ==  1)   {
				
				if(log.isDebugEnabled()){
					log.info("============================================");
					log.info("== CYB532001 chkweTax �ΰ���ȸ ����!! ( �� 1��)   ");
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
				mf.setMap("PAY_CNT", sqlService_cyber.getOneFieldInteger("WE532001.TX1201_TB_MaxPayCnt", mfLevyList));
				
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
				
				/*���ڼ������̺� INSERT : ���߼����� ����ϹǷ� ����ó�����θ� Ȯ������ �ʰ� INSERT */
				sqlService_cyber.queryForInsert("WE532001.TX1201_TB_Insert", mf);
				
				if(!(mfLevyList.getMap("SNTG").equals("1") || mfLevyList.getMap("SNTG").equals("2"))) {  /*�̹̼����� �Ǿ� �ִ� �����...(�Ƹ� �λ����� ���ͳݹ�ŷ����) ����*/
					
					/*���漼 �ΰ������� ������Ʈ*/
					sqlService_cyber.queryForUpdate("WE532001.TX1102_TB_Update", mf);
					
					/*���� ���ڽŰ� ���̺� �������� ������Ʈ*/
					if(mfLevyList.getMap("TAX_ITEM").equals("140004")){ /*����ҵ漼 Ư��¡��*/
						
						sqlService_cyber.queryForUpdate("WE532001.ES1111_TB_Update", mfLevyList);
						
					} else if(mfLevyList.getMap("TAX_ITEM").equals("140001")){ /*����ҵ漼 ���ռҵ�*/
						
						sqlService_cyber.queryForUpdate("WE532001.ES1121_TB_Update", mfLevyList);
						
					} else if(mfLevyList.getMap("TAX_ITEM").equals("140002")){ /*����ҵ漼 �絵�ҵ�*/
						
						sqlService_cyber.queryForUpdate("WE532001.ES1131_TB_Update", mfLevyList);
						
					} else if(mfLevyList.getMap("TAX_ITEM").equals("140003")){ /*����ҵ漼 ���μ���*/
						
					    //2015�� ���� �����Ű�ȭ������ �Ű� ���Ѵٸ�, ���� ���̺�� ������Ʈ �� �ʿ�� ���� ��
                        sqlService_cyber.queryForUpdate("WE532001.ES1141_TB_Update", mfLevyList);
                        
                        //2015.03.24 �ű����̺� ���� ����
                        sqlService_cyber.queryForUpdate("WE532001.ES5001_TB_Update", mfLevyList);
						
					} else if(mfLevyList.getMap("TAX_ITEM").equals("140011") || 
							  mfLevyList.getMap("TAX_ITEM").equals("104009")){ /*����ҵ漼 ��������,�ֹμ� ����*/
						
						sqlService_cyber.queryForUpdate("WE532001.ES1151_TB_Update", mfLevyList);
						
					} else if(mfLevyList.getMap("TAX_ITEM").equals("114002")){ /*��ϼ����㼼*/
						
						sqlService_cyber.queryForUpdate("WE532001.ES1161_TB_Update", mfLevyList);
						
					} else if(mfLevyList.getMap("TAX_ITEM").equals("109000")){ /*������*/
						
						sqlService_cyber.queryForUpdate("WE532001.ES1171_TB_Update", mfLevyList);
						
					} else if(mfLevyList.getMap("TAX_ITEM").equals("135001")){ /*����ҵ漼 �����ڿ����߼� ���ڽŰ��*/
						
						sqlService_cyber.queryForUpdate("WE532001.ES1181_TB_Update", mfLevyList);
						
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
					log.info("== CYB532001 chkweTax �ΰ���ȸ ����!! ���ڳ��ι�ȣ(" + mf.getMap("EPAY_NO") + ")( �� " + 0 + "��)   ");
					log.info("============================================");
				}
				
				retStr = "44100-101";
				
			} else {
				
				//if(log.isDebugEnabled()){
					log.info("============================================");
					log.info("== CYB532001 chkweTax �ΰ���ȸ ����!! ���ڳ��ι�ȣ(" + mf.getMap("EPAY_NO") + ")( �� " + weCmdLevyList.size() + "��)   ");
					log.info("============================================");
				//}
				
				retStr = "44100-201";
			}
			
			
		} catch (Exception e) {
			
			e.printStackTrace();
			
			log.info("============================================");
			log.info("== CYB532001 chkweTax Exception(�ý���) ");
			log.info("============================================");

			retStr = "44100-201";
			
		}
		
        return retStr;
	}

	
}
