/**
 *  �ֽý��۸� : ���̹����漼û ����e���� ����
 *  ��  ��  �� : �ǽð������������
 *  ��  ��  �� : ���ý�-���̹���û 
 *               ����ó��
 *  Ŭ����  ID : We952001Service
 *  ����  �̷� :
 * ------------------------------------------------------------------------
 *  �ۼ���     �Ҽ�  		 ����      Tag   ����
 * ------------------------------------------------------------------------   
 * ��â��   ��ä��(��)  2014.11.24     %01%  �ű��ۼ�
 *  
 */
package com.uc.bs.cyber.service.we952001;
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
public class We952001Service {

	protected Log log = LogFactory.getLog(this.getClass());
	
	private ApplicationContext appContext = null;
	
	private IbatisBaseService sqlService_cyber = null;
	
	private We532001FieldList FL_532001 = new We532001FieldList();
	private We532002FieldList FL_532002 = new We532002FieldList();
	
	/**
	 * ������
	 */
	public We952001Service(ApplicationContext context) {
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
			ArrayList<MapForm>  weCmdLevyList  =  sqlService_cyber.queryForList("WE952001.CO4102_TB_Select", mf);
			
			if (weCmdLevyList.size()  >  0)   {
				
				for ( int rec_cnt = 0; rec_cnt < weCmdLevyList.size();  rec_cnt++ )   {
					
					MapForm mfTotalLevyList =  weCmdLevyList.get(rec_cnt);
					
					/*Ȥ�ó�...because of testing */
					if (mfTotalLevyList == null || mfTotalLevyList.isEmpty() )   {
						continue;
					}
					
					log.info("== CYB952001 �������" + (rec_cnt+1) +" ==(" + mfTotalLevyList.getMap("MSG") + ")");
					log.info("== CYB952001 �������" + (rec_cnt+1) +" ==(" + mfTotalLevyList.getMap("TRN_CD") + ")");
					
					String TRN_CD = mfTotalLevyList.getMap("TRN_CD").toString();
					
					//!.���ڼ������̺��� ����FLAG�� ����� 
					//!.���ϼ����ΰ��� ���̺��� ����Flag�� ='0'���� �����Ѵ�...
					
					MapForm mfLog = new MapForm();
					
					MapForm mf_3111_list = new MapForm();
					
					if (TRN_CD.equals("532001")) { /* �ǽð� �ܰǳ��� ��� */
						
						/*1 ���ý� ���� ���ŷα� TABLE�� ���������� �Ľ��Ѵ�. (Codm_BaseService.xml) */
						mfLog = FL_532001.parseBuff(mfTotalLevyList.getMap("MSG").toString().getBytes());
						
						/*2 ���������� ã�´�. */
						mf_3111_list = sqlService_cyber.queryForMap("WE952001.TX3111_TB_532001_Select", mfLog);
						
						if(mf_3111_list == null || mf_3111_list.size() == 0) {
							
							retStr = "44120-101";
							
						} else {
							
							/*3 ���ڼ����������̺��� ���� e���� �����ǿ� ���� ���������� ���Ѵ�. ������ ���ؼ�...*/
							mf_3111_list.setMap("PAY_CNT", sqlService_cyber.getOneFieldInteger("WE952001.TX3211_TB_MaxValue", mf_3111_list));
							
							if (!((mf_3111_list.getMap("PAY_CNT").toString()).equals("9999"))) { //pay_cnt == 9999�� �ش� SNSU�ڵ��� �������� ���ٴ� �ǹ�
							    /*4 ���ϼ����ΰ����̺��� �����Ǽ��� ���ؼ� 1���� ��쿡�� ����FLAG�� ���� */
	                            if(sqlService_cyber.getOneFieldInteger("WE952001.TX3211_TB_SU_CNT", mf_3111_list) == 1) {
	                                
	                                /**
	                                 * �����Ǽ��� 1�ΰ�츸...
	                                 * 1 �̻��� ���� �ߺ��̹Ƿ� �ΰ������̺��� ������Ʈ ���� �ʴ´�.  
	                                 */
	                                //sqlService_cyber.queryForUpdate("WE952001.TX3111_TB_Cancle532001_Update", mfLog);
	                                sqlService_cyber.queryForUpdate("WE952001.TX3111_TB_Cancle532001_Update", mf_3111_list);
	                            }
	                            
	                            /*5 ���ڼ����������̺��� �������� �����Ѵ�. */
	                            sqlService_cyber.queryForDelete("WE952001.TX3211_TB_Delete", mf_3111_list);
	                            
	                            log.info("���ϼ��� ���ó�� �Ϸ�! [" + mfLog.getMap("EPAY_NO") + "]");
							}
							log.info("���ϼ��� �ش� ����e���� �������� �����Ƿ� �н�]");
							retStr = "44120-000";
						}

					} else if (mfTotalLevyList.getMap("TRN_CD").equals("532002")) { /* �ǽð� �Ѱ����� ��� */
						
						/*1 ���ý� ���� ���ŷα� TABLE�� ���������� �Ľ��Ѵ�. (Codm_BaseService.xml) */
						mfLog = FL_532002.parseBuff(mfTotalLevyList.getMap("MSG").toString().getBytes());
						
						/*2 ���������� ã�´�. */
						mf_3111_list = sqlService_cyber.queryForMap("WE952001.TX3111_TB_532002_Select", mfLog);
						
						if(mf_3111_list == null || mf_3111_list.size() == 0) {
							
							retStr = "44120-101";
							
						} else {
							
							/*3 ���ڼ����������̺��� ���������� ���Ѵ�. ������ ���ؼ�...*/
							mf_3111_list.setMap("PAY_CNT", sqlService_cyber.getOneFieldInteger("WE952001.TX3211_TB_MaxValue", mf_3111_list));
							
							if (!((mf_3111_list.getMap("PAY_CNT").toString()).equals("9999"))) { //pay_cnt == 9999�� �ش� SNSU�ڵ��� �������� ���ٴ� �ǹ�
							    
    							/*4 ���ϼ����ΰ������̺��� ����FLAG�� ���� */
    							if(sqlService_cyber.getOneFieldInteger("WE952001.TX3211_TB_SU_CNT", mf_3111_list) == 1) {
    								/*5 ���ϼ����ΰ������̺��� ����FLAG�� ���� */
    								sqlService_cyber.queryForUpdate("WE952001.TX3111_TB_Cancle532002_Update", mfLog);
    							}
    							
    							/*5 ���ڼ��� ���̺��� �������� �����Ѵ�. */
    							sqlService_cyber.queryForDelete("WE952001.TX3211_TB_Delete", mf_3111_list);

                            }
							
							retStr = "44120-000";
						}

					} else {
						
						log.error("============================================");
						log.error("== CYB952001 �����ڵ� ���� :  "+ mfTotalLevyList.getMap("TRN_CD"));
						log.error("============================================");		
						return "44120-201";
					}
					
				}
				
			} else {
				
				log.info("============================================");
				log.info("== CYB952001 chkweTax ������ȸ ����!! ( �� 0��)   ");
				log.info("============================================");
				
				retStr = "44120-101";
			}

        } catch (Exception e) {
			
			e.printStackTrace();
			log.error("============================================");
			log.error("== CYB952001 chkweTax Exception(�ý���) ");
			log.error("============================================");
			retStr =  "44120-201";
		}
		
        return retStr;	
	}
}
