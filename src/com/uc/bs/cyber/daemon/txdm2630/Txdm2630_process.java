/**
 *  �ֽý��۸� : �λ��û ���̹� ���漼û
 *  ��  ��  �� : ����
 *  ��  ��  �� : �ٷΰ��� �ΰ��ڷ� ����ó��
 *  Ŭ����  ID : Txdm2630_process : �� ó�������� �ۼ��ϴ� Ŭ����
 *  ����  �̷� :
 * ------------------------------------------------------------------------
 *  �ۼ���      	�Ҽ�  		    ����            Tag           ����
 * ------------------------------------------------------------------------
 *  ����ȯ       ��ä��(��)      2013.10.01         %01%         �����ۼ�
 */
package com.uc.bs.cyber.daemon.txdm2630;

import java.util.ArrayList;

import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;

import com.uc.bs.cyber.CbUtil;
import com.uc.bs.cyber.daemon.Codm_BaseProcess;
import com.uc.bs.cyber.daemon.Codm_interface;
import com.uc.core.MapForm;
import com.uc.core.spring.service.UcContextHolder;

/**
 * @author Administrator
 *
 */
public class Txdm2630_process extends Codm_BaseProcess implements Codm_interface {

private MapForm dataMap  = null;
	
	private int insert_cnt = 0, update_cnt = 0;
	
	/**
	 * 
	 */
	public Txdm2630_process() {
	
		super();
		
		log = LogFactory.getLog(this.getClass());
		
		/**
		 * 5 �и��� ����
		 */
		loopTerm = 300;
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
		
		txdm2630_JobProcess();
	}

	/*Ʈ������� �����ϱ� ���� �Լ�.*/
	private void mainTransProcess(){
		
		
		try {
			
			setContext(appContext);
			setApp(appContext);

			UcContextHolder.setCustomerType(this.dataSource);
			
			dataMap = new MapForm();  /*�ʱ�ȭ*/
			dataMap.setMap("PAGE_PER_CNT" , 1000);           	/*�������� ����*/
			dataMap.setMap("STATE"        , "1");             	/*������� ��    */
			
			
				
			int page = cyberService.getOneFieldInteger("TXDM2630.select_count_page", dataMap);
				
			log.info("�ٷΰ��� �ΰ��ڷ� ���� ���� PAGE_PER_CNT(" + dataMap.getMap("PAGE_PER_CNT") + ") �� Page���� =" + page);
				
			if(page == 0){
				log.info("�ٷΰ��� �ΰ��ڷ� ���� ��� �Ǽ��� �����ϴ�.");			
			}else{
				
				for(int i = 1 ; i <= page ; i ++) {

					dataMap.setMap("PAGE",  i);    /*ó��������*/
					this.startJob();               /*��Ƽ Ʈ����� ȣ��*/
				}
				
			}

						
		} catch (Exception e) {

			e.printStackTrace();
		}			
		
		
	}
	
	/*�ڷ� ����*/
	private int txdm2630_JobProcess() {
		
		log.info("=====================================================================");
		log.info("=" + this.getClass().getName()+ " txdm2630_JobProcess() [�ٷΰ��� �ΰ��ڷ� ����ó��] Start =");
		log.info("=====================================================================");

		
		//long elapseTime1 = 0;
		//long elapseTime2 = 0;
		
		int LevyCnt = 0;

		MapForm sunapData = new MapForm();
			
		try {
						
			/*���漼 ����п� �ΰ������� �����´�.*/
			ArrayList<MapForm> dataList =  cyberService.queryForList("TXDM2630.selectBaroSunapList", dataMap);
						
			LevyCnt = dataList.size();

			if (LevyCnt  >  0)   {
				
				log.info("���ڽŰ� �� ���� ���� : " + LevyCnt + " �� �ֽ��ϴ�.");

				//elapseTime1 = System.currentTimeMillis();
				
				for (int i = 0;  i < LevyCnt;  i++)   {
					
					sunapData =  dataList.get(i);
					
					log.info("sunapData :: " + sunapData);
	
					int bugaDataCheck = cyberService.getOneFieldInteger("TXDM2630.getBugaDataCheck", sunapData);
					
					log.info("bugaDataCheck  :: " + bugaDataCheck);
					
					
					if(bugaDataCheck > 0){
						  
						// OCR_BD ��ȸ
						String OCR_BD = null;
						try{						
							OCR_BD = cyberService.getOneFieldString("TXDM2630.getBugaOcrBd",sunapData);
							log.info("OCR_BD ��ȸ :: " + OCR_BD);
						}catch(Exception e){
							e.printStackTrace();
							log.info("OCR_BD ����");
							throw (RuntimeException) e;
						}

						sunapData.setMap("OCR_BD", OCR_BD);												
						
						//PAY_CNT ��ȸ
						int pay_cnt = 0;
						try{
							pay_cnt = cyberService.getOneFieldInteger("TXDM2630.getPayCnt", sunapData);
							log.info("PAY_CNT ��ȸ :: " + pay_cnt);
						}catch(Exception e){
							e.printStackTrace();
							log.info("PAY_CNT ��ȸ ����");
							throw (RuntimeException) e;
						}
						
						sunapData.setMap("PAY_CNT", pay_cnt);
						
						try{
							
							cyberService.queryForInsert("TXDM2630.insertSunapData", sunapData);
							log.info("�������̺� ���");
						}catch(Exception e){
							e.printStackTrace();
							log.info("�������̺� ��� ���� :: " + sunapData);
							throw (RuntimeException) e;
						}
						
						try{
							
							cyberService.queryForUpdate("TXDM2630.updateBugaData", sunapData);
							log.info("�ΰ������� ���� ���� �Ϸ�");
						}catch(Exception e){
							e.printStackTrace();
							log.info("�ΰ������� ���� ���� ���� ���� :: " + sunapData);
							throw (RuntimeException) e;
						}
						
						
						try{
							cyberService.queryForUpdate("TXDM2630.update_complete", sunapData);
							log.info("�����������̺� ���� �Ϸ� ����");
						}catch(Exception e){
							e.printStackTrace();
							log.info("�����������̺� �Ϸ� ���� ���� :: " + sunapData);
							throw (RuntimeException) e;
						}
						
						log.info("===== �ٷΰ��� �ΰ��ڷ� ����ó�� �Ϸ� ===== ");
					
					}else{
						
						log.info("�ΰ��ڷᰡ �������� �ʾҽ��ϴ�.");
						continue;
					}

				}
				
				//elapseTime2 = System.currentTimeMillis();
				
				//log.info("���� ó�� �ð� : " + CbUtil.formatTime(elapseTime2 - elapseTime1));
				
				log.info("==================== �ٷΰ��� ����ó����� ó�� �Ϸ� ======================");
				
			}else{
				log.info("���� ��� �Ǽ��� �����ϴ�.");
			}
			
		}catch (Exception e){
			e.printStackTrace();
		}
		
		return LevyCnt;
		
	}
	
}

