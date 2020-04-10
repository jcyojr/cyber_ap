/**
 *  �ֽý��۸� : ���̹����漼û ����e���� ����
 *  ��  ��  �� : ����e����(���ϼ������) �ǽð����ΰ������
 *  ��  ��  �� : ���ý�->���ý� ���� �������� ����ó��
 *  Ŭ����  ID : We252001Service
 *  ����  �̷� :
 * ------------------------------------------------------------------------
 *  �ۼ���     �Ҽ�  		 ����      Tag   ����
 * ------------------------------------------------------------------------   
 * ��â��   ��ä��(��)  2014.11.20     %02%  ����e����3��(���ϼ���) �߰�
 */
package com.uc.bs.cyber.service.we252001;

import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;

import com.uc.core.MapForm;
import com.uc.core.spring.service.IbatisBaseService;


/**
 * @author Administrator
 */
public class We252001Service {

	protected Log log = LogFactory.getLog(this.getClass());
	
	private ApplicationContext appContext = null;
	
	private IbatisBaseService sqlService_cyber = null;
		
	/**
	 * ������
	 */
	public We252001Service(ApplicationContext context) {
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
	 * ���ϼ������ �ΰ��ڷḦ ��ȸ�ϰ� ����ó���Ѵ�...
	 * */
	public String chkweTax(MapForm mf) throws Exception {
		
		String retStr = "";
		
		try{
			
			ArrayList<MapForm>  weCmdLevyList  =  sqlService_cyber.queryForList("WE252001.SELECT_SHSD_LEVY_LIST", mf);
			
			MapForm mfLevyList = new MapForm();
						
			if (weCmdLevyList.size()  ==  1)   {
				
				if(log.isDebugEnabled()){
					log.info("============================================");
					log.info("== CYB252001 chkweTax �ΰ���ȸ ����!! ( �� 1��)   ");
					log.info("============================================");
				}
				
				/* �ΰ��ڷᰡ 1�� �����ϹǷ� ����
				 * 1. ���ڼ������̺� (���ý���)�ΰ��ڷḦ INSERT�Ѵ�.
				 * 2. ���� ���ϼ��� �ΰ� ���̺��� ����Flag�� ������Ʈ �Ѵ�.
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
				mf.setMap("PAY_CNT", sqlService_cyber.getOneFieldInteger("WE252001.TX3211_TB_MaxPayCnt", mfLevyList));
				
				log.debug(" ���ϼ������ MaxPayCnt = " + mf.getMap("PAY_CNT").toString());
				
				/*���������������� ���� ī���� ���� ��� ���α���ڵ�� ������ �� 3�ڸ��� ����� : 20110711*/
				/*20110816 �ٽ� ������ */
				//if(!((String) mf.getMap("CARD_REQ_NO")).trim().equals("")) { // ī����ι�ȣ�� ������ ī�����
		        //    mf.setMap("BNK_CD", ((String) mf.getMap("BANK_COD")).trim().substring(2, 5));
		        //} else { // �ƴϸ� ������ü ���� 
		        //    mf.setMap("BNK_CD", ((String) mf.getMap("BANK_COD")).trim().substring(0, 3));
		        //}
				/*20141125 ����*/
				mf.setMap("BNK_CD", ((String) mf.getMap("BANK_COD")).trim().substring(0, 3));
				
				/*���ೳ��ó������*/ //PAY_FORM -> R:���ೳ��, I:�����Ű� �� ����(��ó���), Q:��ȸ �� ����(��ó���)
				mf.setMap("RSN_YN", (mf.getMap("PAY_FORM").equals("R")) ? "Y" : "N"); 
				
				mf.setMap("TAX_YY", (mfLevyList.getMap("TAX_YY")));
				mf.setMap("TAX_MM", (mfLevyList.getMap("TAX_MM")));
				mf.setMap("PRT_NPNO", (mfLevyList.getMap("PRT_NPNO")));
				mf.setMap("GUBUN", (mfLevyList.getMap("GUBUN")));
				
				/*���ڼ������̺� INSERT : ���߼����� ����ϹǷ� ����ó�����θ� Ȯ������ �ʰ� INSERT */
				int intLog = 0;
                
                intLog = sqlService_cyber.queryForUpdate("WE252001.INSERT_TX3211_TB_EPAY", mf);
				
				if(!(mfLevyList.getMap("SNTG").equals("1") || mfLevyList.getMap("SNTG").equals("2"))) {  /*�̹̼����� �Ǿ� �ִ� �����...(�Ƹ� �λ����� ���ͳݹ�ŷ����) ����*/
					
					/*���ϼ������ �ΰ������� ������Ʈ*/
                    if(intLog > 0) {
                        
                        sqlService_cyber.queryForUpdate("WE252001.UPDATE_TX3111_TB_NAPBU_INFO", mf);
                        
                        log.info("���ϼ������ ���ڳ���ó�� �Ϸ�! [" + mf.getMap("EPAY_NO") + "]");
                    }
					
				} else {
					log.info("�� ����ó���� ���Դϴ�. ���ڼ������̺� ���߳��ο��� Ȯ���ϼ���!");
					log.info("���ڼ������̺� ����ó���Ǿ����ϴ�.");
				}
				
				
//				/*���ճ����̹Ƿ� ��ü�� ������...*/
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
					log.info("== CYB252001 chkweTax �ΰ���ȸ ����!! ���ڳ��ι�ȣ(" + mf.getMap("EPAY_NO") + ")( �� " + 0 + "��)   ");
					log.info("============================================");
				}
				
				retStr = "44100-101";
				
			} else {
				
				if(log.isDebugEnabled()){
					log.info("============================================");
					log.info("== CYB252001 chkweTax �ΰ���ȸ ����!! ���ڳ��ι�ȣ(" + mf.getMap("EPAY_NO") + ")( �� " + weCmdLevyList.size() + "��)   ");
					log.info("============================================");
				}
				
				retStr = "44100-201";
			}
			
			
		} catch (Exception e) {
			
			e.printStackTrace();
			
			log.error("============================================");
			log.error("== CYB252001 chkweTax Exception(�ý���) ");
			log.error("============================================");

			retStr = "44100-201";
			
		}
		
        return retStr;
	}

	
}
