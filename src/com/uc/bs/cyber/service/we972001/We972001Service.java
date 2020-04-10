/**
 *  �ֽý��۸� : ���̹����漼û ����e���� ����
 *  ��  ��  �� : �ǽð������������
 *  ��  ��  �� : ���ý�-���̹���û 
 *               ����ó��
 *  Ŭ����  ID : We972001Service
 *  ����  �̷� :
 * ------------------------------------------------------------------------
 *  �ۼ���     �Ҽ�  		 ����      Tag   ����
 * ------------------------------------------------------------------------   
 * ��â��   ��ä��(��)  2013.11.24     %01%  �ű��ۼ�
 *  
 */
package com.uc.bs.cyber.service.we972001;
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
public class We972001Service {

	protected Log log = LogFactory.getLog(this.getClass());
	
	private ApplicationContext appContext = null;
	
	private IbatisBaseService sqlService_cyber = null;
	
	private We532001FieldList FL_532001 = new We532001FieldList();
	private We532002FieldList FL_532002 = new We532002FieldList();
	
	/**
	 * ������
	 */
	public We972001Service(ApplicationContext context) {
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
			ArrayList<MapForm>  weCmdLevyList  =  sqlService_cyber.queryForList("WE972001.CO4102_TB_Select", mf);
			
			if (weCmdLevyList.size()  >  0)   {
				
				for ( int rec_cnt = 0; rec_cnt < weCmdLevyList.size();  rec_cnt++ )   {
					
					MapForm mfTotalLevyList =  weCmdLevyList.get(rec_cnt);
					
					/*Ȥ�ó�...because of testing */
					if (mfTotalLevyList == null || mfTotalLevyList.isEmpty() )   {
						continue;
					}
					
					log.info("== CYB972001 �������" + (rec_cnt+1) +" ==(" + mfTotalLevyList.getMap("MSG") + ")");
					log.info("== CYB972001 �������" + (rec_cnt+1) +" ==(" + mfTotalLevyList.getMap("TRN_CD") + ")");
					
					String TRN_CD = mfTotalLevyList.getMap("TRN_CD").toString();
					
					//!.���ڼ������̺��� ����FLAG�� ����� 
					//!.���ܼ��Ժΰ��� ���̺��� ����Flag�� ='0'���� �����Ѵ�...
					
					MapForm mfLog = new MapForm();
					
					MapForm mf_2112_list = new MapForm();
					
					if (TRN_CD.equals("532001")) { /* �ǽð����ΰ� ��� */
						
						/*1 ���ý� ���� ���ŷα� TABLE�� ���������� �Ľ��Ѵ�. (Codm_BaseService.xml) */
						mfLog = FL_532001.parseBuff(mfTotalLevyList.getMap("MSG").toString().getBytes());
						log.info("== CYB972001 ������� EPAY_NO ==(" + mfLog.getMap("EPAY_NO").toString() + ")");
						
						/*2 ���������� ã�´�. */
						mf_2112_list = sqlService_cyber.queryForMap("WE972001.TX2112_TB_532001_Select", mfLog);
						
						if(mf_2112_list == null || mf_2112_list.size() == 0) {
							
							retStr = "44120-101";
							
						} else {
							
						    String TAX_GBN = mf_2112_list.getMap("TAX_GBN").toString();
						    
						    log.info("== CYB972001 ������� tax_gbn ==(" + TAX_GBN + ")");
						    
//						    if(TAX_GBN.equals("2")) {//�� ���ܼ����� ���
//    							/*3 ���ڼ����������̺��� ���������� ���Ѵ�. ������ ���ؼ�...*/
//    							mf_2112_list.setMap("PAY_CNT", sqlService_cyber.getOneFieldInteger("WE972001.TX2221_TB_MaxValue", mf_2112_list));
//    							
//    							/*4 ���ܼ��Ժΰ������̺��� ����FLAG�� ���� */
//    							if(sqlService_cyber.getOneFieldInteger("WE972001.TX2221_TB_SU_CNT", mf_2112_list) == 1) {
//    								
//    								/**
//    								 * �����Ǽ��� 1�ΰ�츸...1 �̻��� ���� �ߺ��̹Ƿ� �ΰ������̺��� ������Ʈ ���� �ʴ´�.  
//    								 */
//    								sqlService_cyber.queryForUpdate("WE972001.TX2122_TB_Cancle532001_Update", mfLog);
//    							}
//    							
//    							/*5 ���ڼ����������̺��� �������� �����Ѵ�. */
//    							sqlService_cyber.queryForDelete("WE972001.TX2221_TB_Delete", mf_2112_list);
//    							
//						    } else 
						    if(TAX_GBN.equals("1")) { //ǥ�ؼ��ܼ����� ���
						        
						        /*3 ���ڼ����������̺��� ���� e���� �����ǿ� ���� ���������� ���Ѵ�. ������ ���ؼ�...*/
                                mf_2112_list.setMap("PAY_CNT", sqlService_cyber.getOneFieldInteger("WE972001.TX2211_TB_MaxValue", mf_2112_list));
                                log.info("== CYB972001 ������� MaxPAY_CNT ==(" + mf_2112_list.getMap("PAY_CNT") + ")");
                                
                                if (!((mf_2112_list.getMap("PAY_CNT").toString()).equals("9999"))) { //pay_cnt == 9999�� �ش� SNSU�ڵ��� �������� ���ٴ� �ǹ�
                                    /*4 ���ܼ��Ժΰ������̺��� ����FLAG�� ���� */
                                    if(sqlService_cyber.getOneFieldInteger("WE972001.TX2211_TB_SU_CNT", mf_2112_list) == 1) {
                                        log.info("== CYB972001 ������� SU_CNT ==(1)");
                                        /**
                                         * �����Ǽ��� 1�ΰ�츸... 1 �̻��� ���� �ߺ��̹Ƿ� �ΰ������̺��� ������Ʈ ���� �ʴ´�.  
                                         */
                                        sqlService_cyber.queryForUpdate("WE972001.TX2112_TB_Cancle532001_Update",  mf_2112_list);
                                        log.info("== CYB972001 ������� Cancle532001_Update ==(" + mf_2112_list.getMap("TAX_NO") + ")");
                                    }
                                    
                                    /*5 ���ڼ����������̺��� �������� �����Ѵ�. */
                                    log.info("== CYB972001 ������� Delete ==(" + mf_2112_list.getMap("TAX_NO") + ")");
                                    sqlService_cyber.queryForDelete("WE972001.TX2211_TB_Delete", mf_2112_list);
                                                                    
                                    log.info("ǥ�ؼ��ܼ��� ���ó�� �Ϸ�! [" +  mf_2112_list.getMap("TAX_NO") + "]");
                                    retStr = "44120-000";
                                }
                                
                                log.info("ǥ�ؼ��ܼ��� �ش� ����s���� �������� �����Ƿ� �н�]");
                                retStr = "44120-000";
						    }
						}

					} else if (mfTotalLevyList.getMap("TRN_CD").equals("532002")) { /* �ǽð� �Ѱ����ΰ� ��� */
						
						/*1 ���ý� ���� ���ŷα� TABLE�� ���������� �Ľ��Ѵ�. (Codm_BaseService.xml) */
						mfLog = FL_532002.parseBuff(mfTotalLevyList.getMap("MSG").toString().getBytes());
						
						/*2 ǥ�ؼ��ܼ��� ���������� ã�´�. */
						mf_2112_list = sqlService_cyber.queryForMap("WE972001.TX2112_TB_532002_Select", mfLog);
						
						if(mf_2112_list == null || mf_2112_list.size() == 0) {
							
							retStr = "44120-101";
							
						} else {
							
							/*3 ǥ�ؼ��ܼ��� ���ڼ��� �������̺��� ���������� ���Ѵ�. ������ ���ؼ�...*/
							mf_2112_list.setMap("PAY_CNT", sqlService_cyber.getOneFieldInteger("WE972001.TX2211_TB_MaxValue", mf_2112_list));
							log.info("== CYB972002 ������� MaxPAY_CNT ==(" + mf_2112_list.getMap("PAY_CNT") + ")");
							/*4 ǥ�ؼ��ܼ��� �ΰ������̺��� ����FLAG�� ���� */
							if(sqlService_cyber.getOneFieldInteger("WE972001.TX2211_TB_SU_CNT", mf_2112_list) == 1) {
							    log.info("== CYB972002 �ϰ�������� SU_CNT ==(1)");
                                /**
                                 * �����Ǽ��� 1�ΰ�츸... 1 �̻��� ���� �ߺ��̹Ƿ� �ΰ������̺��� ������Ʈ ���� �ʴ´�.  
                                 */
								sqlService_cyber.queryForUpdate("WE972001.TX2112_TB_Cancle532002_Update", mf_2112_list);
								log.info("== CYB972002 �ϰ�������� Cancle532001_Update ==(" + mf_2112_list.getMap("TAX_NO") + ")");
							}
							
							/*5 ǥ�ؼ��ܼ��� ���ڼ��� �������̺��� �������� �����Ѵ�. */
							log.info("== CYB972002 �ϰ�������� Delete ==(" + mf_2112_list.getMap("TAX_NO") + ")");
							sqlService_cyber.queryForDelete("WE972001.TX2211_TB_Delete", mf_2112_list);
							
							log.info("ǥ�ؼ��ܼ��� �ϰ����� ���ó�� �Ϸ�! [" +  mf_2112_list.getMap("TAX_NO") + "]");
							
							retStr = "44120-000";
						}

					} else {
						
						log.error("============================================");
						log.error("== CYB972001 �����ڵ� ���� :  "+ mfTotalLevyList.getMap("TRN_CD"));
						log.error("============================================");		
						return "44120-201";
					}
					
				}
				
			} else {
				
				log.info("============================================");
				log.info("== CYB972001 chkweTax ������ȸ ����!! ( �� 0��)   ");
				log.info("============================================");
				
				retStr = "44120-101";
			}

        } catch (Exception e) {
			
			e.printStackTrace();
			log.error("============================================");
			log.error("== CYB972001 chkweTax Exception(�ý���) ");
			log.error("============================================");
			retStr =  "44120-201";
		}
		
        return retStr;	
	}
}
