/**
 *  �ֽý��۸� : ���̹����漼û ��ȭ
 *  ��  ��  �� : BS ������� �ŷ����� ����
 *  ��  ��  �� : �λ�����-���̹���û 
 *               �λ����࿡�� ���ŵ� ������ ���ؼ� ����� �� ��Ÿ���ܼ��� �Ա�ó�� 
 *  Ŭ����  ID : Bs531001Service
 *  ����  �̷� :
 * ------------------------------------------------------------------------
 *  �ۼ���     �Ҽ�  		 ����      Tag   ����
 * ------------------------------------------------------------------------   
 *  ����ȯ     ��ä��    2015.01.28   %01%  �ű��ۼ�
 *  
 */
package com.uc.bs.cyber.service.bs531001;

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
public class Bs531001Service {

	protected Log log = LogFactory.getLog(this.getClass());
	
	private ApplicationContext appContext = null;
	
	private IbatisBaseService sqlService_cyber = null;
		
	private Bs531001FieldList bsField = null;
	
	CbUtil cUtil = null;
	
	/**
	 * ������
	 */
	public Bs531001Service(ApplicationContext appContext) {
		// TODO Auto-generated constructor stub
		
		setAppContext(appContext);
		
		bsField = new Bs531001FieldList();
		
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
	public byte[] chk_bs_531001(MapForm headMap, MapForm mf) throws Exception {
		
		/*****************************************
		 * 0000 ����
		 * 7011 �ش���¹�ȣ ����
		 * 7012 �Աݱݾ� ����ġ
		 * 7013 �ش���� �ԱݺҰ�
		 * 7014 �ش��� ���
		 * 7015 ��Ÿ ����
		 * */
		
		//����ó������		
		boolean proc = false;
		ArrayList<MapForm> list = new ArrayList<MapForm>();
		MapForm amtCheck = null;
		//MapForm sendMap = new MapForm();
		
		//�Աݰŷ� �Ա���Ұŷ� ����
		String job_gbn = "";
		//String tax_gbn = "";
		
		try{
			cUtil = CbUtil.getInstance();
			
			//JOB_GBN  20 : �Ա�   51 : �Ա����
			job_gbn = mf.getMap("JOB_GBN").toString();
			
			long bAmt = Long.parseLong(mf.getMap("TRN_AMT").toString());
			
			String acc_no = mf.getMap("VIR_ACC_NO").toString();
			String vir_gbn = "";
			String vir_gbn2 = "";
			if(acc_no.length() != 13){
				//�ش� ������¹�ȣ ����
				log.debug("������� ���� ���� 13�ڸ��� �ƴմϴ�. :: " + acc_no.length());
				headMap.setMap("RS_CODE" , "7011");            
		        return bsField.makeSendBuffer(headMap, mf);
			}else{
				vir_gbn = acc_no.substring(4, 6);
				vir_gbn2 = acc_no.substring(4, 8);
				log.debug("������� ����  44 : ����� , 4500 : �����  :: " + vir_gbn + "  " + vir_gbn2);
			}
			
			//TAX_GBN   �����, ����� ����   ������� ������� �и�
			//tax_gbn = mf.getMap("VIR_ACC_NO").toString().substring(0,1);
			
			//�Աݰŷ�
			if(job_gbn.equals("20")){
						
				log.debug("�Աݰŷ�  :: " + job_gbn);
				//�Ա�ó�� ����� �ִ��� �ѹ��� Ȯ��
				
				// ������� ��ȣ�� �̳� �� ���� �� �Ǽ� Ȯ��
				if(vir_gbn.equals("44")){
					// ����� ****-44-*******
					log.debug("����� �ΰ��ڷ� ��ȸ �մϴ�.");
					// ����� �ΰ��ڷ� ��ȸ
					list = sqlService_cyber.queryForList("BS531001.getSudoSunapList", mf);
					
				}else if(vir_gbn2.equals("4500")){
					// ����� ****-4500-***** 1���� �ο� 
					log.debug("����� �ΰ��ڷḦ ��ȸ�մϴ�.");
					// ����� �ΰ��ڷ� ��ȸ
					list = sqlService_cyber.queryForList("BS531001.getEtcSunapList", mf);
					
				}else{
					log.debug("������� ����� ����� ���� ���� �Դϴ�. vir_gbn : " + vir_gbn + "  " + vir_gbn2);
					headMap.setMap("RS_CODE"   , "7011");     /*�����ڵ�*/  
			        headMap.setMap("TX_GUBUN"  , "0210");
			        
			        return bsField.makeSendBuffer(headMap, mf);
				}
				
								
				if(list.size() < 1 ){
					//7011 : �ش� ���¹�ȣ ����
					log.debug("7011 : �ش� ���¹�ȣ ����  list.size() < 1 �Դϴ�." + list.size());
					headMap.setMap("RS_CODE"   ,  "7011");     
			        headMap.setMap("TX_GUBUN"  ,  "0210");			        
			        return bsField.makeSendBuffer(headMap, mf);
				}
				
					
				log.debug("������·� ��ȸ�� �ΰ��ڷ�� ó���ݾ� ���Ͽ� ����ó�� �մϴ�.");
				for(int i = 0; i < list.size(); i++){
					amtCheck = new MapForm();
					amtCheck = list.get(i);
					//������·� ��ȸ�� �ΰ��ڷ� �� ó���ݾ� ���Ͽ� ����ó��
					long sumamt = Long.parseLong(amtCheck.getMap("SUM_AMT").toString());
					
					//�Ա�ó�� �ؾ��� ���� ������ ����ó�� �� for �� ��
					if(sumamt == bAmt){
					 	log.debug("�Աݱݾ� ��ġ  :: " + sumamt);
						/*
						 * GUBUN   1 : ���˺�   2 : �����   3 : �հ�     4 : ���ú�     5 : ����� �ܰ�   6: ����� �հ�
						 * */  
						
					 	//������¹�ȣ �����Ϸù�ȣ ����
						amtCheck.setMap("BS_TRANS_NO", headMap.getMap("TRAN_NO"));
						amtCheck.setMap("VIR_ACC_NO", mf.getMap("VIR_ACC_NO"));
						
						//����� ���� ����� ���� Ȯ��
						//����� �ܰ� ó��
						if(amtCheck.getMap("GUBUN").equals("5")){
							//����� �ܰǼ���ó��
							log.debug("����� �ܰǼ���ó��  :: " + amtCheck.getMap("GUBUN"));
							sqlService_cyber.queryForInsert("BS531001.insertEtcSunap", amtCheck);
							sqlService_cyber.queryForUpdate("BS531001.updateEtcBugwaSunap", amtCheck);
																			
							proc = true;
							break;
						}
						//����� �հ� ó��
						else if(amtCheck.getMap("GUBUN").equals("6")){
							//����� �հ����ó��
							log.debug("����� �հ����ó��  :: " + amtCheck.getMap("GUBUN"));				
						
							if(sqlService_cyber.queryForUpdate("BS531001.insertEtcAllSunap", amtCheck) > 0){
								sqlService_cyber.queryForUpdate("BS531001.updateEtcAllBugwaSunap", amtCheck);
							}
		
							proc = true;
							break;
						}
						else if(amtCheck.getMap("GUBUN").equals("3")){
							//����� ��ü ����
							log.debug("����� ��ü ����  :: " + amtCheck.getMap("GUBUN"));
							//�ΰ��ڷ� ����ó��
							sqlService_cyber.queryForInsert("BS531001.insertSudoAllSunapProc", amtCheck);
							sqlService_cyber.queryForUpdate("BS531001.updateSudoAllBugwaSunapProc", amtCheck);
							
							proc = true;
							break;
						}else if(amtCheck.getMap("GUBUN").equals("1") || amtCheck.getMap("GUBUN").equals("2") || amtCheck.getMap("GUBUN").equals("4")){
							//����� �Ϻ� ����
							log.debug("����� �Ϻ� ����  :: " + amtCheck.getMap("GUBUN"));
							//�ΰ��ڷ� ����ó��
							sqlService_cyber.queryForInsert("BS531001.insertSudoSunapData", amtCheck);
							sqlService_cyber.queryForUpdate("BS531001.updateSudoBugwaSunapData", amtCheck);			
							proc = true;
							break;
						}else{
							//7012 : �Աݱݾ� ����ġ 
							log.debug("GUBUN ����  :: " + amtCheck.getMap("GUBUN"));
							headMap.setMap("RS_CODE"   ,  "7012");     
					        headMap.setMap("TX_GUBUN"  ,  "0210");			        
					        return bsField.makeSendBuffer(headMap, mf);
						}
						
					}
					
				}
				
			}
			//�б���Ұŷ�
			else if(job_gbn.equals("51")){
				
				log.debug("�Ա���Ұŷ� 51 ::  " + job_gbn);
				
				MapForm smf = new MapForm();
				//���ŷ� ������ȣ
				smf.setMap("BS_TRANS_NO", headMap.getMap("TRAN_B_NO"));
				smf.setMap("TRANS_NO", headMap.getMap("TRAN_NO"));
				smf.setMap("SUM_RCP", mf.getMap("TRN_AMT"));
				
				//�Ա���� �ŷ� ���� Ȯ��
				//list = sqlService_cyber.queryForList("BS531001.getSudoSunapCancelList", smf);
				
				// ������� ��ȣ�� �̳� �� ���� �� �Ǽ� Ȯ��
				if(vir_gbn.equals("44")){
					// ����� ****-44-*******
					log.debug("����� �ΰ��ڷ� ��ȸ �մϴ�.");
					// ����� �ΰ��ڷ� ��ȸ
					list = sqlService_cyber.queryForList("BS531001.getSudoSunapCancelList", smf);
					
					//��Ұ��� ������� ���� 
					if(list.size() < 1){
						//7012 : �Ա� ��ұݾ� ����ġ 
						log.debug("����� �Ա���� list.size() < 1 �Դϴ�. ::  " + list.size());
						headMap.setMap("RS_CODE"   ,  "7012");     
				        headMap.setMap("TX_GUBUN"  ,  "0210");			        
				        return bsField.makeSendBuffer(headMap, mf);
					}
					
					//�Ա���� ó��
					log.debug("����� �������̺��� ���� ������� �ΰ� �� ���� ���̺� ������� ó��");
					for(int i = 0; i < list.size(); i++){
						amtCheck = new MapForm();
						amtCheck = list.get(i);
						
						amtCheck.setMap("BS_TRANS_NO", headMap.getMap("TRAN_NO"));
						
						log.debug("����� ������� �ڷ� ("+i+") ���� :: " + amtCheck);
						//������� �� �Ϸ� ���� ����
						if(sqlService_cyber.queryForUpdate("BS531001.updateSunapCancel", amtCheck) > 0){
							sqlService_cyber.queryForUpdate("BS531001.updateBugwaSunapCancel", amtCheck);
						}else{
							log.debug("����� ������� �ڷ� ("+i+") :: ���� " + amtCheck);
						}
						
						log.debug("����� ������� �ڷ� ("+i+") :: �Ϸ�");
					}
					
					proc = true;
					
				}else if(vir_gbn2.equals("4500")){
					// ����� ****-4500-***** 1���� �ο� 
					log.debug("����� �ΰ��ڷḦ ��ȸ�մϴ�.");
					// ����� �ΰ��ڷ� ��ȸ
					list = sqlService_cyber.queryForList("BS531001.getEtcSunapCancelList", smf);
					
					//��Ұ��� ������� ���� 
					if(list.size() < 1){
						//7012 : �Ա� ��ұݾ� ����ġ 
						log.debug("����� �Ա���� list.size() < 1 �Դϴ�. ::  " + list.size());
						headMap.setMap("RS_CODE"   ,  "7012");     
				        headMap.setMap("TX_GUBUN"  ,  "0210");			        
				        return bsField.makeSendBuffer(headMap, mf);
					}
					
					//�Ա���� ó��
					log.debug("����� �������̺��� ���� ������� �ΰ� �� ���� ���̺� ������� ó��");
					for(int i = 0; i < list.size(); i++){
						amtCheck = new MapForm();
						amtCheck = list.get(i);
						
						amtCheck.setMap("BS_TRANS_NO", headMap.getMap("TRAN_NO"));
						
						log.debug("����� ������� �ڷ� ("+i+") ���� :: " + amtCheck);
						//������� �� �Ϸ� ���� ����
						if(sqlService_cyber.queryForUpdate("BS531001.updateEtcSunapCancel", amtCheck) > 0){
							sqlService_cyber.queryForUpdate("BS531001.updateEtcBugwaSunapCancel", amtCheck);
						}else{
							log.debug("����� ������� �ڷ� ("+i+") :: ���� " + amtCheck);
						}
						
						log.debug("����� ������� �ڷ� ("+i+") :: �Ϸ�");
					}
					
					proc = true;
					
					
				}else{
					log.debug("������� ����� ����� ���� ���� �Դϴ�. vir_gbn : " + vir_gbn + "  " + vir_gbn2);
					headMap.setMap("RS_CODE"   , "7011");     /*�����ڵ�*/  
			        headMap.setMap("TX_GUBUN"  , "0210");
			        
			        return bsField.makeSendBuffer(headMap, mf);
				}
				

			}else{
				//�ŷ����� ����
				log.debug("�ŷ����� ���� �Դϴ�. :: " + job_gbn);
				/*�����ڵ�  7015 :: ��Ÿ ����*/
			    headMap.setMap("RS_CODE"   ,  "7015");     
		        headMap.setMap("TX_GUBUN"  ,  "0210");		        
		        return bsField.makeSendBuffer(headMap, mf);
			}
			
			
        } catch (Exception e) {
			
			e.printStackTrace();
			log.debug("===== Exception �߻� =======");
			
			headMap.setMap("RS_CODE"   ,  "7015");     /*�����ڵ�*/
		    headMap.setMap("TX_GUBUN"  ,  "0210");
		    return bsField.makeSendBuffer(headMap, mf);
		}
       
		if(proc){
			log.debug("proc = true  :: " + proc);
			headMap.setMap("RS_CODE" , "0000");
		}else{
			log.debug("proc = false  :: " + proc);
			headMap.setMap("RS_CODE" , "7012");
		}        
        headMap.setMap("TX_GUBUN"  , "0210");  
        log.debug("===== ó���Ϸ� =======");
        log.debug("===== headMap :: " + headMap);
        return bsField.makeSendBuffer(headMap, mf);
	}

	

}

