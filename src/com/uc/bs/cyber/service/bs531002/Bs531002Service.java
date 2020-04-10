/**
 *  �ֽý��۸� : ���̹����漼û ��ȭ
 *  ��  ��  �� : �Աݰ��� ���� ������ȸ ����
 *  ��  ��  �� : �λ�����-���̹���û 
 *              ���� ������ �м��ϰ� ��������� �Ա� ���� ���θ� Ȯ�� �Ѵ�. 
 *  Ŭ����  ID : Bs531002Service
 *  ����  �̷� :
 * ------------------------------------------------------------------------
 *  �ۼ���     �Ҽ�  		 ����      Tag   ����
 * ------------------------------------------------------------------------   
 *  ����ȯ   ��ä��      2015.01.19   %01%  �ű��ۼ�
 *  
 */
package com.uc.bs.cyber.service.bs531002;

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
public class Bs531002Service {

	protected Log log = LogFactory.getLog(this.getClass());
	
	private ApplicationContext appContext = null;
	
	private IbatisBaseService sqlService_cyber = null;
		
	private Bs531002FieldList bsField = null;
	
	CbUtil cUtil = null;
	
	/**
	 * ������
	 */
	public Bs531002Service(ApplicationContext appContext) {
		// TODO Auto-generated constructor stub
		
		setAppContext(appContext);
		
		bsField = new Bs531002FieldList();
		
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
	 * ���漼 ���γ��� ����ȸ ó��...
	 * */
	public byte[] chk_bs_531002(MapForm headMap, MapForm mf) throws Exception {
		
		
		/*****************************************
		 * 0000 ����
		 * 7011 �ش���¹�ȣ ����
		 * 7012 �Աݱݾ� ����ġ
		 * 7013 �ش���� �ԱݺҰ�
		 * 7014 �ش��� ���
		 * 7015 ��Ÿ ����
		 * */
		ArrayList<MapForm> list = new ArrayList<MapForm>();		
		MapForm sendMap = new MapForm();
		MapForm checkMap = null;
		boolean rsCheck = false;
		/*����� �� ����� ������ȸ */                                                                 
		try{
			
			/*����üũ*/
			String vir_acc_no = mf.getMap("NEW_VIR_ACC_NO").toString();
			log.debug("�� ������¹�ȣ Ȯ�� : " + vir_acc_no);
			long trn_amt = Long.parseLong(mf.getMap("TRN_AMT").toString());
			log.debug("�ŷ��ݾ� Ȯ�� : " + trn_amt);
			
			if(trn_amt < 10){
				log.debug("�ݾ��� 10�� �̸��Դϴ�.");
				headMap.setMap("RS_CODE" , "7012");            
		        return bsField.makeSendBuffer(headMap, mf);
			}
			
			// ����� �� Ư��ȸ�� ���� - ������� ������� ��  ����� : 5,  Ư�� : 4
			String vir_gbn = "";
			String vir_gbn2 = "";
						
			if(vir_acc_no.length() != 13){
				//�ش� ������¹�ȣ ����
				log.debug("������� ���� ���� 13�ڸ��� �ƴմϴ�. :: " + vir_acc_no.length());
				headMap.setMap("RS_CODE" , "7011");            
		        return bsField.makeSendBuffer(headMap, mf);
			}else{
				vir_gbn = vir_acc_no.substring(4, 6);
				vir_gbn2 = vir_acc_no.substring(4, 8);
				log.debug("������� ����  44 : ����� , 4500 : �����  :: " + vir_gbn + "  " + vir_gbn2);
			}
			
			//�����, ����� 
			
			// ������� ��ȣ�� �̳� �� ���� �� �Ǽ� Ȯ��
			if(vir_gbn.equals("44")){
				// ����� ****-44-*******
				log.debug("����� �ΰ��ڷ� ��ȸ �մϴ�.");
				// ����� �ΰ��ڷ� ��ȸ
				list = sqlService_cyber.queryForList("BS531002.getSudoBugwaList", mf);
				
			}else if(vir_gbn2.equals("4500")){
				// ����� ****-4500-***** 1���� �ο� 
				log.debug("����� �ΰ��ڷḦ ��ȸ�մϴ�.");
				// ���ܼ��� �ΰ��ڷ� ��ȸ
				list = sqlService_cyber.queryForList("BS531002.getEtcBugwaList", mf);
				
			}else{
				log.debug("������� ����� ����� ���� ���� �Դϴ�. vir_gbn : " + vir_gbn + "  " + vir_gbn2);
				headMap.setMap("RS_CODE"   , "7011");     /*�����ڵ�*/  
		        headMap.setMap("TX_GUBUN"  , "0210");
		        
		        return bsField.makeSendBuffer(headMap, mf);
			}
			
			//������� ��ȸ 
			if(list.size() < 1){
				log.debug("�ش� ��������� list.size() < 1 �Դϴ�. ");
				headMap.setMap("RS_CODE"   , "7011");     /*�����ڵ�*/  
		        headMap.setMap("TX_GUBUN"  , "0210");
		        
		        return bsField.makeSendBuffer(headMap, mf);
			}
			
			for(int i = 0; i < list.size(); i++){
				checkMap = new MapForm();
				checkMap = list.get(i);
				
				log.debug("����� ���� Ȯ�� �մϴ�. " + checkMap);
				long checkAmt = Long.parseLong(checkMap.getMap("SUM_AMT").toString());
				
				if(checkAmt == trn_amt){
					log.debug("�ݾ��� ��ġ�մϴ�.");
					mf.setMap("REG_NM", checkMap.getMap("REG_NM"));
					rsCheck = true;
					break;
				}
				log.debug("�ݾ��� ��ġ���� �ʽ��ϴ�.");
				
			}
			
			
        } catch (Exception e) {
        			
			e.printStackTrace();
			log.error("============================================");
			log.error("== chk_bs_523002 Exception(�ý���) ");
			log.error("============================================");
		}
		
        if(rsCheck){
        	log.debug("����ó�� rsCheck = " + rsCheck);
        	headMap.setMap("RS_CODE" , "0000"); 
        	
        }else{
        	//�Աݱݾ� ����ġ
        	log.debug("�ݾ� ����ġ rsCheck = " + rsCheck);
        	headMap.setMap("RS_CODE" , "7012");      
        }     
        headMap.setMap("TX_GUBUN"  , "0210");
        
        return bsField.makeSendBuffer(headMap, mf);
	}


}

