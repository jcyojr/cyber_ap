/**
 *  �ֽý��۸� : ���̹����漼û ����e���� ����
 *  ��  ��  �� : �ǽð������������
 *  ��  ��  �� : ���ý�-���̹���û 
 *               ����ó��
 *  Ŭ����  ID : We902001Service
 *  ����  �̷� :
 * ------------------------------------------------------------------------
 *  �ۼ���     �Ҽ�  		 ����      Tag   ����
 * ------------------------------------------------------------------------   
 * ��â��   ��ä��(��)  2013.11.24     %01%  �ű��ۼ�
 *                2014.04.18           ���� 
 *                ���� ó���ҽ��� �ҽ��� SNSU=1�κ��� �ּ�ó�� �Ǿ� �־���(���� �Ҹ�Ȯ)
 *                �׷��� ������ ���� ����e������ ��ҿ�û�� ���ؽ�����(������µ�)�� ��� �ϴ� ��찡 �߻�(�̶��� ���÷� ��Ұ��� ����.)
 *                ����e���η� ���ε� ��(SNSU=1)�� ��ȸ�Ͽ� ����� �� �ֵ��� �ּ��� �����ϰ� pay_cnt��  null�� ��� 9999�� �����Ͽ� ó��Ű�� ��.
 *                
 *  
 */
package com.uc.bs.cyber.service.we902001;
import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;

import com.uc.core.MapForm;
import com.uc.core.spring.service.IbatisBaseService;

import com.uc.bs.cyber.service.we532001.We532001FieldList;
import com.uc.bs.cyber.service.we532002.We532002FieldList;
/**
 * @author Administrator
 *
 */
public class We902001Service {

	protected Log log = LogFactory.getLog(this.getClass());
	
	private ApplicationContext appContext = null;
	
	private IbatisBaseService sqlService_cyber = null;
	
	private We532001FieldList FL_532001 = new We532001FieldList();
	private We532002FieldList FL_532002 = new We532002FieldList();
	
	/**
	 * ������
	 */
	public We902001Service(ApplicationContext context) {
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
	 * ��Ҹ� ���� �ΰ��ڷḦ ��ȸ�ϰ� ���ó���Ѵ�.
	 * */
	public String chkweTax(MapForm mf) throws Exception {
		
        String retStr = "";
		
		try{
			/*��������� �޾� ���ý����������α׸� ��ȸ�Ѵ�.*/
			ArrayList<MapForm>  weCmdLevyList  =  sqlService_cyber.queryForList("WE902001.CO4102_TB_Select", mf);
			
			if (weCmdLevyList.size()  >  0)   {
				
				for ( int rec_cnt = 0; rec_cnt < weCmdLevyList.size();  rec_cnt++ )   {
					
					MapForm mfTotalLevyList =  weCmdLevyList.get(rec_cnt);
					
					/*Ȥ�ó�...because of testing */
					if (mfTotalLevyList == null || mfTotalLevyList.isEmpty() )   {
						continue;
					}
					
					log.info("== CYB902001 �������" + (rec_cnt+1) +" ==(" + mfTotalLevyList.getMap("MSG") + ")");
					log.info("== CYB902001 �������" + (rec_cnt+1) +" ==(" + mfTotalLevyList.getMap("TRN_CD") + ")");
					
					String TRN_CD = mfTotalLevyList.getMap("TRN_CD").toString();
					
					//!.���ڼ������̺��� ����FLAG�� ����� 
					//!.ȯ�氳���δ�ݺΰ��� ���̺��� ����Flag�� ='0'���� �����Ѵ�...
					
					MapForm mfLog = new MapForm();
					
					MapForm mf_2132_list = new MapForm();
					
					if (TRN_CD.equals("532001")) { /* �ǽð����ΰ� ��� */
						
						/*1 ���ý� ���� ���ŷα� TABLE�� ���������� �Ľ��Ѵ�. (Codm_BaseService.xml) */
						mfLog = FL_532001.parseBuff(mfTotalLevyList.getMap("MSG").toString().getBytes());
						
						/*2 ���������� ã�´�. */
						mf_2132_list = sqlService_cyber.queryForMap("WE902001.TX2132_TB_532001_Select", mfLog);
						
						if(mf_2132_list == null || mf_2132_list.size() == 0) {
							
							retStr = "44120-101";
							
						} else {
							
							/*3 ���ڼ����������̺��� ���� e���� �����ǿ� ���� ���������� ���Ѵ�. ������ ���ؼ�...*/
							mf_2132_list.setMap("PAY_CNT", sqlService_cyber.getOneFieldInteger("WE902001.TX2231_TB_MaxValue", mf_2132_list));
							
							if (!((mf_2132_list.getMap("PAY_CNT").toString()).equals("9999"))) { //pay_cnt == 9999�� �ش� SNSU�ڵ��� �������� ���ٴ� �ǹ�
							    /*4 ȯ�氳���δ�ݺΰ������̺��� ����FLAG�� ���� */
	                            if(sqlService_cyber.getOneFieldInteger("WE902001.TX2231_TB_SU_CNT", mf_2132_list) == 1) {
	                                
	                                /**
	                                 * �����Ǽ��� 1�ΰ�츸...
	                                 * 1 �̻��� ���� �ߺ��̹Ƿ� �ΰ������̺��� ������Ʈ ���� �ʴ´�.  
	                                 */
	                                sqlService_cyber.queryForUpdate("WE902001.TX2132_TB_Cancle532001_Update", mfLog);
	                            }
	                            
	                            /*5 ���ڼ����������̺��� �������� �����Ѵ�. */
	                            sqlService_cyber.queryForDelete("WE902001.TX2231_TB_Delete", mf_2132_list);
	                            
	                            log.info("ȯ�氳���δ�� ���ó�� �Ϸ�! [" + mfLog.getMap("EPAY_NO") + "]");
							}
							log.info("ȯ�氳���δ�� �ش� ����e���� �������� �����Ƿ� �н�]");
							retStr = "44120-000";
						}

					} else if (mfTotalLevyList.getMap("TRN_CD").equals("532002")) { /* �ǽð� �Ѱ����ΰ� ��� */
						
						/*1 ���ý� ���� ���ŷα� TABLE�� ���������� �Ľ��Ѵ�. (Codm_BaseService.xml) */
						mfLog = FL_532002.parseBuff(mfTotalLevyList.getMap("MSG").toString().getBytes());
						
						/*2 ���������� ã�´�. */
						mf_2132_list = sqlService_cyber.queryForMap("WE902001.TX2132_TB_532002_Select", mfLog);
						
						if(mf_2132_list == null || mf_2132_list.size() == 0) {
							
							retStr = "44120-101";
							
						} else {
							
							/*3 ���ڼ����������̺��� ���������� ���Ѵ�. ������ ���ؼ�...*/
							mf_2132_list.setMap("PAY_CNT", sqlService_cyber.getOneFieldInteger("WE902001.TX2231_TB_MaxValue", mf_2132_list));
							
							if (!((mf_2132_list.getMap("PAY_CNT").toString()).equals("9999"))) { //pay_cnt == 9999�� �ش� SNSU�ڵ��� �������� ���ٴ� �ǹ�
							    
    							/*4 ȯ�氳���δ�ݺΰ������̺��� ����FLAG�� ���� */
    							if(sqlService_cyber.getOneFieldInteger("WE902001.TX2231_TB_SU_CNT", mf_2132_list) == 1) {
    								/*5 ȯ�氳���δ�ݺΰ������̺��� ����FLAG�� ���� */
    								sqlService_cyber.queryForUpdate("WE902001.TX2132_TB_Cancle532002_Update", mfLog);
    							}
    							
    							/*5 ���ڼ����������̺��� �������� �����Ѵ�. */
    							sqlService_cyber.queryForDelete("WE902001.TX2231_TB_Delete", mf_2132_list);

                            }
							
							retStr = "44120-000";
						}

					} else {
						
						log.error("============================================");
						log.error("== CYB902001 �����ڵ� ���� :  "+ mfTotalLevyList.getMap("TRN_CD"));
						log.error("============================================");		
						return "44120-201";
					}
					
				}
				
			} else {
				
				log.info("============================================");
				log.info("== CYB902001 chkweTax ������ȸ ����!! ( �� 0��)   ");
				log.info("============================================");
				
				retStr = "44120-101";
			}

        } catch (Exception e) {
			
			e.printStackTrace();
			log.error("============================================");
			log.error("== CYB902001 chkweTax Exception(�ý���) ");
			log.error("============================================");
			retStr =  "44120-201";
		}
		
        return retStr;	
	}
}
