/**
 *  �ֽý��۸� : �λ��û ���̹� ���漼û
 *  ��  ��  �� : ����
 *  ��  ��  �� : ���漼 ���볳���� �ΰ��ڷῬ��(��û��) ó�� ����
 *  Ŭ����  ID : Txdm3112_process : �� ó�������� �ۼ��ϴ� Ŭ����
 *  ����  �̷� :
 * ------------------------------------------------------------------------
 *  �ۼ���      	�Ҽ�  		    ����            Tag           ����
 * ------------------------------------------------------------------------
 *  ������       ��ä��(��)      2014.10.02         %01%         �����ۼ�
 *  ����ȣ       ��ä��              2015.03.22                      �����븮�� ���� �߰�
 *  ����ȣ       ��ä��              2015.03.22                      Ư��¡�� �� ���� �߰�
 */
package com.uc.bs.cyber.daemon.txdm3112;

import java.util.ArrayList;

import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.dao.DuplicateKeyException;

import com.uc.bs.cyber.CbUtil;
import com.uc.bs.cyber.daemon.Codm_BaseProcess;
import com.uc.bs.cyber.daemon.Codm_interface;
import com.uc.core.MapForm;
import com.uc.core.spring.service.UcContextHolder;

/**
 * @author Administrator
 *
 */

public class Txdm3112_process extends Codm_BaseProcess implements Codm_interface {

	private MapForm dataMap  = null;
	private int insert_cnt = 0, update_cnt = 0, del_cnt = 0;
	
	/**
	 * 
	 */
	public Txdm3112_process() {
		
		super();
		
		log = LogFactory.getLog(this.getClass());
		
		/* 300�ʸ��� */
		this.loopTerm = 300;	
	}
	
	/*Context ���Կ�*/
	public void setApp(ApplicationContext context) {
		
		this.context = context;
	}

	/*���μ��� ����*/
	public void runProcess() throws Exception {
		
		/*Ʈ����� ����*/
		mainTransProcess();
	}

	/*Context ���Կ�*/
	public void setDatasrc(String datasrc) {
		
		this.dataSource = datasrc;
	}

	public void transactionJob() {
		
		//�۾����� �����븮�ξ��� �߰�
		String jb = (String) dataMap.getMap("JobGb");
		
		/*����� �� �ΰ�ó��..*/
		txdm3112_JobProcess(jb);
	}

	/*Ʈ������� �����ϱ� ���� �Լ�.*/
	private void mainTransProcess(){
		
		try {
			
			setContext(appContext);
			setApp(appContext);

			UcContextHolder.setCustomerType(this.dataSource);
			
			/*�ʱ�ȭ*/
			dataMap = new MapForm();
			/*�������� ����*/
			dataMap.setMap("PAGE_PER_CNT" , 1000);
			/*��û�ڵ� ����*/
			dataMap.setMap("SGG_COD"      , this.c_slf_org);
			/*������ ��*/
			dataMap.setMap("TRN_YN"       , "0");           
			
			int page = govService.getOneFieldInteger("TXDM3112.select_count_page", dataMap);
			
			log.info("[���漼 ���볳���� �ڷ� (" + c_slf_org_nm + ")] PAGE_PER_CNT(" + dataMap.getMap("PAGE_PER_CNT") + ") �� Page���� =" + page);
				
			if(page == 0){
				log.info("���漼 ���볳���� �ڷᰡ �����ϴ�.");
			}else{
				/*��Ƽ Ʈ����� ȣ��*/
				dataMap.setMap("JobGb", "1");
				this.startJob(); 
			}
			
			/*�����븮�� ��û������ ǥ�����漼�� �ִ� ����  */
			int page2 = cyberService.getOneFieldInteger("TXDM3112.select_count_page2", dataMap);
			
			if(page2 == 0){
				log.info("�����븮�� ��û�ڷᰡ �����ϴ�.");
			}else{
				/*��Ƽ Ʈ����� ȣ��*/
				dataMap.setMap("JobGb", "2");
				this.startJob(); 
			}			
			
			/*�����븮�� ��û��� ó�� ���� �߰� 2015.03.19 ����ȣ ���Ⱑ ǥ�����漼�� �ܾ���°�*/
			/*���̹� ��û���� ��û�� �����͸� ó��*/
			int page3 = govService.getOneFieldInteger("TXDM3112.select_count_page3", dataMap);
			int page4 = cyberService.getOneFieldInteger("TXDM3112.select_count_page4", dataMap);
			
			if(page3 == 0 || page4 == 0){
				log.info("�����븮�� ��û��� �� ��ö����� ó���� �ڷᰡ �����ϴ�.");
			}else{
				/*��Ƽ Ʈ����� ȣ��*/
				dataMap.setMap("JobGb", "3");
				this.startJob(); 
			}
			
			/*Ư��¡�� ����*/
			int page5 = cyberService.getOneFieldInteger("TXDM3112.select_count_page5", dataMap);
			
			if(page5 == 0){
				log.info("ó���� Ư��¡�� ������ �����ϴ�.!");
			}else{
				/*��Ƽ Ʈ����� ȣ��*/
				dataMap.setMap("JobGb", "4");
				this.startJob(); 
			}
			
			/* �����븮�� ���ؽ� �ڷ� ����ȭ ����
			 * ���̹� ���漼û �ڷᰡ �켱�� �ȴ�.
			 * ���̹� ���漼û�� ���� ���ؽ��� ���ε��ڷ�� �μ�Ʈ
			 * ���̹� ���漼û�� �ְ� ���ؽ����� �ִ��ڷ�� ���̹� ���漼û �켱��.
			 */

			/*���ؽ� ����ȭ ���˻�*/
			int page6 = govService.getOneFieldInteger("TXDM3112.select_count_page6", dataMap);
			if(page6 < 1){
				log.info("�����븮�� ���ؽ����� ����ȭ �ڷᰡ �����ϴ�.");
			}else{
				/*��Ƽ Ʈ����� ȣ��*/
				dataMap.setMap("JobGb", "5");
				this.startJob(); 
			}
			
			
		} catch (Exception e) {

			e.printStackTrace();
		}			
		
		
	}
	
	/*�ڷ� ����*/
	private int txdm3112_JobProcess(String jb) {
		
		
		long queryElapse1 = 0;
		long queryElapse2 = 0;
		
		int LevyCnt = 0;
		
		insert_cnt = 0;
		update_cnt = 0;
		del_cnt = 0;
	
		
		MapForm etaxJointTaxPaymentList = null;
		MapForm etaxJointSemuList = null;    //�����븮�� ��û��û
		MapForm etaxJointSemuRltList = null; //�����븮�� ��û���
		MapForm etaxJointTgRltList = null; //Ư��¡������
		MapForm etaxJointWtRltList = null; //���ؽ� �ڷᵿ��ȭ
		
		try {
			
			if("1".equals(jb)){
			
			queryElapse1 = System.currentTimeMillis();
			
			/*���漼 ���볳���� �ΰ������� �����´�.*/
			ArrayList<MapForm> jointTaxPaymentList =  govService.queryForList("TXDM3112.select_jointTaxPayment_list", dataMap);
			
			queryElapse2 = System.currentTimeMillis();
			
			LevyCnt = jointTaxPaymentList.size();
			
			log.info("[" + c_slf_org_nm + "]���볳���� ������ȸ �ð� : " + CbUtil.formatTime(queryElapse2 - queryElapse1) + " (" + LevyCnt + ")");
			
			if (LevyCnt  >  0)   {
				
				for (int i = 0;  i < LevyCnt;  i++)   {
					
					etaxJointTaxPaymentList = new MapForm();
					
					etaxJointTaxPaymentList =  jointTaxPaymentList.get(i);

					if (etaxJointTaxPaymentList == null  ||  etaxJointTaxPaymentList.isEmpty() )   {
						
						continue;
					}
					

					/*========================*/
					/*Ʈ�����ó�� 1000�Ǵ��� �⺻*/
					/*1. �ΰ��ڷ� ���, ����, ���� */
					/*2. �������̺� ������Ʈ*/
					/*========================*/
					
					try {
						
						/*�ű� �� ����*/	
						if(etaxJointTaxPaymentList.getMap("CUD_OPT").equals("1")||etaxJointTaxPaymentList.getMap("CUD_OPT").equals("2")){  
							
							try {
								
								    //�츮�� �μ�Ʈ
									cyberService.queryForInsert("TXDM3112.insert_tx1104_tb", etaxJointTaxPaymentList);  
									
									//�������̺� ������Ʈ(1=����) 
									etaxJointTaxPaymentList.setMap("TRN_YN", "1");   
									govService.queryForUpdate("TXDM3112.update_complete",etaxJointTaxPaymentList);
							
									insert_cnt++;
							
							}catch (Exception e){
							
									if (e instanceof DuplicateKeyException){
										
										//�츮�� ������Ʈ
										cyberService.queryForUpdate("TXDM3112.update_tx1104_tb", etaxJointTaxPaymentList);
										
										//�������̺� ������Ʈ(1=����)  
										etaxJointTaxPaymentList.setMap("TRN_YN", "1");
										govService.queryForUpdate("TXDM3112.update_complete",etaxJointTaxPaymentList);
										
										update_cnt++;
										
									} else {
										
										log.info("�����߻������� = " + etaxJointTaxPaymentList.getMaps());
										
										//�������̺� ������Ʈ (8=�μ�Ʈ, ������Ʈ ����)
										etaxJointTaxPaymentList.setMap("TRN_YN", "8"); 
										govService.queryForUpdate("TXDM3112.update_complete",etaxJointTaxPaymentList);
										
										e.printStackTrace();
										
										throw (RuntimeException) e;
									}
							}
							
					    /*����*/	
						} else if (etaxJointTaxPaymentList.getMap("CUD_OPT").equals("3")){  
							
							try{
								
								//DEL_YN=Y�� ������Ʈ
								cyberService.queryForUpdate("TXDM3112.delete_tx1104_tb", etaxJointTaxPaymentList);
								
								//�������̺� ������Ʈ (1= ����) 
								etaxJointTaxPaymentList.setMap("TRN_YN", "1");   
								govService.queryForUpdate("TXDM3112.update_complete",etaxJointTaxPaymentList);
								
								del_cnt++;
								
							} catch (Exception e) {
								
								log.info("�����߻������� = " + etaxJointTaxPaymentList.getMaps());
								
								//�������̺� ������Ʈ (8=�μ�Ʈ, ������Ʈ ����)
								etaxJointTaxPaymentList.setMap("TRN_YN", "8"); 
								govService.queryForUpdate("TXDM3112.update_complete",etaxJointTaxPaymentList);

								e.printStackTrace();
							}
							
						}else{
						
							log.info("�����߻������� = " + etaxJointTaxPaymentList.getMaps());
							
							//�������̺� ������Ʈ (9=�������̺� ó������ ����)
							etaxJointTaxPaymentList.setMap("TRN_YN", "9");    
							govService.queryForUpdate("TXDM3112.update_complete",etaxJointTaxPaymentList);

							
						}
						
							

					} catch (Exception e) {

						log.info("�����߻������� = " + etaxJointTaxPaymentList.getMaps());

						e.printStackTrace();
					}

				}
				
			    log.info("����Ǽ� : " + LevyCnt + "�� �� �ű� " + insert_cnt + "�� ���� " +  update_cnt + "�� ���� " + del_cnt + "�� ó���Ϸ�");
				
			}else{
				log.debug("�����ڷᰡ �����ϴ�.");
			}

			//�����븮�� ����
			}else if("2".equals(jb)){
				queryElapse1 = System.currentTimeMillis();
				
				/*�����븮�� ��û�ڷḦ �����´�.*/
				ArrayList<MapForm> jointSemuList = cyberService.queryForList("TXDM3112.select_jointSemu_list", dataMap);
				
				queryElapse2 = System.currentTimeMillis();
				
				LevyCnt = jointSemuList.size();
				
				log.info("[" + c_slf_org_nm + "]�����븮�� ��û��� ������ȸ �ð� : " + CbUtil.formatTime(queryElapse2 - queryElapse1) + " (" + LevyCnt + ")");
				
				if (LevyCnt  >  0)   {
					for (int i = 0;  i < LevyCnt;  i++)   {
						etaxJointSemuList = new MapForm();
						etaxJointSemuList =  jointSemuList.get(i);
						if (etaxJointSemuList == null  ||  etaxJointSemuList.isEmpty() )   {
							continue;
						}
						
							try {
									//�����븮�� ��û�ڷ� ǥ�����漼�ʿ� �μ�Ʈ
								    //150403 �μ�Ʈ�� ���� SEQ �Ź� ä���������� ���ϵ����Ͱ� 037���̺��� �Լӽ��� ��ó�� ���ʿ�.
									//int cnt = govService.getOneFieldInteger("TXDM3112.f_search",etaxJointSemuList);
								
										if("6".equals(etaxJointSemuList.getMap("REQ_PROC"))){
											etaxJointSemuList.setMap("REQ_PROC", "2");
										}
										
										//�����븮�� ��û�ڷ� ǥ�����漼�ʿ� �μ�Ʈ
										int iCnt = govService.queryForUpdate("TXDM3112.semu_insert",etaxJointSemuList);  
										
										if(iCnt > 0){
										//�����ѵ����� ���ۿ��� Y�� ����
										cyberService.queryForUpdate("TXDM3112.semu_cyber_update",etaxJointSemuList);
										insert_cnt++;
										}
										
							}catch (Exception e){
							
										log.info("�����߻������� = " + etaxJointSemuRltList.getMaps());
										e.printStackTrace();
										throw (RuntimeException) e;
							}

					}
					
				    log.info("ó����� �Ǽ� : [" + LevyCnt + "] �� �� insert : [" + insert_cnt + "] ��  ó���Ϸ�");
				    
				}else{
					log.debug("�����븮�� ��û ����� �����ϴ�.");
				}
				
			}else if("3".equals(jb)){
				
				String msg = "";
				queryElapse1 = System.currentTimeMillis();
				
				/*�����븮�� ��û����� �����´�.*/
				ArrayList<MapForm> jointSemuRltList = govService.queryForList("TXDM3112.select_jointSemuRlt_list", dataMap);
				
				queryElapse2 = System.currentTimeMillis();
				
				LevyCnt = jointSemuRltList.size();
				
				log.info("[" + c_slf_org_nm + "]�����븮�� ��û��� ������ȸ �ð� : " + CbUtil.formatTime(queryElapse2 - queryElapse1) + " (" + LevyCnt + ")");
				
				if (LevyCnt  >  0)   {
					
					for (int i = 0;  i < LevyCnt;  i++)   {
						etaxJointSemuRltList = new MapForm();
						etaxJointSemuRltList =  jointSemuRltList.get(i);
						if (etaxJointSemuRltList == null  ||  etaxJointSemuRltList.isEmpty() )   {
							continue;
						}

							try {
								
								//ó���Ҵ���� ������쿡 ���ó��
								int chkCnt = cyberService.getOneFieldInteger("TXDM3112.rltSelect_count", etaxJointSemuRltList);
								
								if(chkCnt > 0){
									//���̹���û REQ_PROC ���°� ��ȸ
									String cy_proc = cyberService.getOneFieldString("TXDM3112.cyber_Proc_Select", etaxJointSemuRltList);
									
									//��Ұ��̸� ǥ�����漼�� ��ҽ����� REQ_PROC�� 2���϶��� �����ͼ� ������Ʈ�Ѵ�.
									//�������� 4���ε� ����ó���Ǳ����� ���̹���û�� ������Ʈ �Ǹ� �ȵǱ⶧����..
									if(!"2".equals(etaxJointSemuRltList.getMap("REQ_PROC")) && "6".equals(cy_proc)){
										
									}else{
									//�츮�� ������Ʈ
									int uCnt = cyberService.queryForUpdate("TXDM3112.update_tx5111_tb", etaxJointSemuRltList);  
										if(uCnt > 0){
											update_cnt++;
										}
									}	
								}
							}catch (Exception e){
							
										log.info("�����߻������� = " + etaxJointSemuRltList.getMaps());
										e.printStackTrace();
										throw (RuntimeException) e;
							}

					}
				    log.info("ó����� �Ǽ� : " + update_cnt + "�� ó���Ϸ�");
				}else{
					log.debug("�����븮�� ��ûó�� ����� �����ϴ�.");
				}
				
			}else if("4".equals(jb)){
				queryElapse1 = System.currentTimeMillis();
				
				/*Ư��¡�� ���� ó����� ��ȸ*/
				ArrayList<MapForm> jointTgList = cyberService.queryForList("TXDM3112.select_jointTG_list", dataMap);
				
				queryElapse2 = System.currentTimeMillis();
				//etaxJointTgRltList
				LevyCnt = jointTgList.size();
				
				log.info("[" + c_slf_org_nm + "]Ư��¡�� ���� ������ȸ �ð� : " + CbUtil.formatTime(queryElapse2 - queryElapse1) + " (" + LevyCnt + ")");
				
				if (LevyCnt  >  0)   {
					for (int i = 0;  i < LevyCnt;  i++)   {
						etaxJointTgRltList = new MapForm();
						etaxJointTgRltList =  jointTgList.get(i);
						if (etaxJointTgRltList == null  ||  etaxJointTgRltList.isEmpty() )   {
							continue;
						}
						
							try {
								//��û�ڵ�
								etaxJointTgRltList.setMap("SGG_COD", dataMap.get("SGG_COD"));	
								
								//Ư��¡�� ���� ó���ڷḦ �ش籸û�� �μ�Ʈ
								int iCnt = govService.queryForUpdate("TXDM3112.tg_insert",etaxJointTgRltList);  
								
								if(iCnt > 0){
								//��û�μ�Ʈ �Ϸ��� ���̹���û��������
								cyberService.queryForUpdate("TXDM3112.tg_update",etaxJointTgRltList);
								insert_cnt++;
								}	
							}catch (Exception e){
							
								log.info("�����߻������� = " + etaxJointTgRltList.getMaps());
								e.printStackTrace();
								throw (RuntimeException) e;
							}

					}
					
				    log.info("ó����� �Ǽ� : [" + LevyCnt + "] �� �� insert : [" + insert_cnt + "] �� ó���Ϸ�");
				    
				}else{
					log.debug("Ư��¡�� ���� ó������� �����ϴ�.");
				}
				
			}else if("5".equals(jb)){
				String msg = "";
				queryElapse1 = System.currentTimeMillis();
				
				/*�����븮�� ���ؽ� ���γ����� �����´�.*/
				ArrayList<MapForm> jointWtRltList = govService.queryForList("TXDM3112.select_jointWtRlt_list", dataMap);
				
				queryElapse2 = System.currentTimeMillis();
				
				LevyCnt = jointWtRltList.size();
				
				log.info("[" + c_slf_org_nm + "]�����븮�� ���ؽ��ڷ� ������ȸ �ð� : " + CbUtil.formatTime(queryElapse2 - queryElapse1) + " (" + LevyCnt + ")");
				
				if (LevyCnt  >  0)   {
					
					for (int i = 0;  i < LevyCnt;  i++)   {
						etaxJointWtRltList = new MapForm();
						etaxJointWtRltList =  jointWtRltList.get(i);
						if (etaxJointWtRltList == null  ||  etaxJointWtRltList.isEmpty() )   {
							continue;
						}

							try {
								
								//ó���Ҵ���� ������쿡 ���ó��
								int chkCnt = cyberService.getOneFieldInteger("TXDM3112.wtRltSelect_count", etaxJointWtRltList);
								
								//���̹� ��û�� �����Ͱ� ����
								if(chkCnt == 0){
									//���ؽ� ���εȰ�
								    if("4".equals(jointWtRltList.get(i).getMap("REQ_PROC"))){
								    	//�츮�� �μ�Ʈ
								    	int uCnt = cyberService.queryForUpdate("TXDM3112.insertWt_tx5111_tb", etaxJointWtRltList);  
										if(uCnt > 0){
											insert_cnt++;
										}
								    }
								}else{
									//�ش� �����Ͱ� �ְ� �����Ͱ� ���ؽ� �ڷ����̰� ���̹���û DB�� ǥ�����漼DB�� ���°��� �ٸ��� ������Ʈ
									int statCnt = cyberService.getOneFieldInteger("TXDM3112.wtStatSelect_count", etaxJointWtRltList);
										if(statCnt > 0){
											int upCnt = cyberService.queryForUpdate("TXDM3112.updateWt_tx5111_tb", etaxJointWtRltList);
											if(upCnt > 0){
												update_cnt++;
											}
										}
								}
								
							}catch (Exception e){
							
										log.info("�����߻������� = " + etaxJointWtRltList.getMaps());
										e.printStackTrace();
										//throw (RuntimeException) e;
							}

					}
				    log.info("ó����� �Ǽ� : ��� [" + insert_cnt + "]�� ���� [" + update_cnt + "]�� ó���Ϸ�");
				}else{
					log.debug("�����븮�� ���ؽ� ����ȭ �ڷᰡ �����ϴ�.");
				}
				
			}
			
		} catch (Exception e) {
			log.info("�����߻������� = " + etaxJointTaxPaymentList.getMaps());

			e.printStackTrace();
		}

		return LevyCnt;

	}

}
