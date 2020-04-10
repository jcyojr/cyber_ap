/**
 *  �ֽý��۸� : �λ��û ���̹� ���漼û
 *  ��  ��  �� : ����
 *  ��  ��  �� : ���ϼ��� ���ڰ��� �����û ȸ�� ����
 *  Ŭ����  ID : Txdm4315_process : �� ó�������� �ۼ��ϴ� Ŭ����
 *  ����  �̷� :
 * ------------------------------------------------------------------------
 *  �ۼ���      	�Ҽ�  		    ����            Tag           ����
 * ------------------------------------------------------------------------
 *  ����ȯ          ��ä��      2013.09.11         %01%         �����ۼ�
 */
package com.uc.bs.cyber.daemon.txdm4315;

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
public class Txdm4315_process extends Codm_BaseProcess implements Codm_interface {

	private MapForm dataMap  = null;
	
	private int insert_cnt = 0, update_cnt = 0;
	
	/**
	 * 
	 */
	public Txdm4315_process() {
	
		super();
		
		log = LogFactory.getLog(this.getClass());
		
		/**
		 * 10 �и��� ����
		 */
		loopTerm = 600;
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
		
		txsv4314_JobProcess();
	}

	/*Ʈ������� �����ϱ� ���� �Լ�.*/
	private void mainTransProcess(){
		
		
		try {
			
			setContext(appContext);
			setApp(appContext);

			UcContextHolder.setCustomerType(this.dataSource);
			
			dataMap = new MapForm();  /*�ʱ�ȭ*/
			dataMap.setMap("PAGE_PER_CNT" , 1000);            /*�������� ����*/
			dataMap.setMap("STATE"        ,  "0");            /*�űԽ�û ��*/
			
			do {
				
				int page = cyberService.getOneFieldInteger("TXSV4315.ssd_enoti_count_page", dataMap);
				
				log.info("[���ϼ��� ���ڰ��� �����û ] PAGE_PER_CNT(" + dataMap.getMap("PAGE_PER_CNT") + ") �� Page���� =" + page);
				
				if(page == 0){ 
					log.info("����� ���ڰ��� �����û ������ �����ϴ�.");
					break;
				}

				for(int i = 1 ; i <= page ; i ++) {
					dataMap.setMap("PAGE",  i);    /*ó��������*/
					this.startJob();               /*��Ƽ Ʈ����� ȣ��*/
				}
								
				if(cyberService.getOneFieldInteger("TXSV4315.ssd_enoti_count_page", dataMap) == 0) {
					break;
				}
				
			}while(true);
						
		} catch (Exception e) {
			
			e.printStackTrace();
		}			

	}
	
	/*�ڷ� ����*/
	private int txsv4314_JobProcess() {
		
		log.info("=====================================================================");
		log.info("=" + this.getClass().getName()+ " txsv4315_JobProcess() [���ϼ��� ���ڰ��� �������� ����] Start =");
		log.info("=====================================================================");
		
		
		int sudoCnt = 0;
		
		insert_cnt = 0;
		update_cnt = 0; 
		
		int cy_member_check;
		
		try {
			
			/*���ϼ��� ���ڰ��� �����û ���� �����´�.*/
			ArrayList<MapForm> ssdenotiList =  cyberService.queryForList("TXSV4315.getSudoEnotiList", dataMap);
				
			sudoCnt = ssdenotiList.size();

			MapForm ssdEnotiData = new MapForm();
			
			if(sudoCnt  >  0){

				for(int rec_cnt = 0;  rec_cnt < sudoCnt;  rec_cnt++){

					ssdEnotiData =  ssdenotiList.get(rec_cnt);
					
					String enoti_gb = ssdEnotiData.getMap("ENOTI_GB").toString();
					
					if(enoti_gb.equals("")){
						log.info("���ڰ��� ������ �� �Դϴ�.");
						continue;
					}
					
					try{					
						// ����� ���ڰ��� ���
						cyberService.queryForInsert("TXSV4315.insertSudoEnotiMemberInfo", ssdEnotiData);
						
					}catch(Exception e){
						log.error("���������� = " + ssdEnotiData.getMaps());
						log.error(e.getMessage());
						e.printStackTrace();
						throw (RuntimeException) e;
					}
					
					try{					
						// ����ȣ ���
						cyberService.queryForInsert("TXSV4315.insertSudoCustNo", ssdEnotiData);
						
					}catch(Exception e){
						log.error("���������� = " + ssdEnotiData.getMaps());
						log.error(e.getMessage());
						e.printStackTrace();
						throw (RuntimeException) e;
					}
										
					try{
						// ���̹� ȸ������ Ȯ��
						cy_member_check = cyberService.getOneFieldInteger("TXSV4315.getCyberMemberInfoCheck", ssdEnotiData);
						
					}catch(Exception e){
						log.error("���������� = " + ssdEnotiData.getMaps());
						log.error(e.getMessage());
						e.printStackTrace();
						throw (RuntimeException) e;
					}
					
					
					// ���̹� ȸ���� ���
					if(cy_member_check > 0){
						
						
						// ȸ������ ����						
						try{
							if(enoti_gb.equals("1")){
								// ���漼 ����
								cyberService.queryForUpdate("TXSV4315.updateCyberMemberEnotiInfo", ssdEnotiData);
							}else if(enoti_gb.equals("2")){
								// ����� ����
								cyberService.queryForUpdate("TXSV4315.updateCyberMemberSSDInfo", ssdEnotiData);
							}else{
								// ���漼 ����� ����
								cyberService.queryForUpdate("TXSV4315.updateCyberMemberEnotiSsdEnotiInfo", ssdEnotiData);							
							}
							
						}catch(Exception e){
							log.error("���������� = " + ssdEnotiData.getMaps());
							log.error(e.getMessage());
							e.printStackTrace();
							throw (RuntimeException) e;
						}
						update_cnt += 1;
						
					}
					// ���̹� ȸ���� �ƴ� ��� 
					else{
						
						// ���̹� ��ȸ������ ����
						try{
							
							cyberService.queryForInsert("TXSV4315.insertCyberMemberInfo", ssdEnotiData);
														
						}catch(Exception e){
							log.error("���������� = " + ssdEnotiData.getMaps());
							log.error(e.getMessage());
							e.printStackTrace();
							throw (RuntimeException) e;
						}
						insert_cnt += 1;
						
						
					}
					
					// �������̺� ���� ����
					try{
						cyberService.queryForUpdate("TXSV4315.updateCyberMemberState", ssdEnotiData);
					}catch(Exception e){
						log.error("���������� = " + ssdEnotiData.getMaps());
						log.error(e.getMessage());
						e.printStackTrace();
						throw (RuntimeException) e;
					}
					
				}
				
			}else{
				log.info("����� ���ڰ��� ��û ȸ���� �����ϴ�.");
			}
		
			//log.info("���ϼ��� ����� �����ڷ� ����ð� : " + CbUtil.formatTime(elapseTime2 - elapseTime1));
			log.info("�ѿ���[" + sudoCnt + "]" + "INSERT �Ǽ�" + "["+insert_cnt+"]" +"UPDATE �Ǽ�" + "["+update_cnt+"]" );
			
			
		}catch (Exception e){
			e.printStackTrace();
			log.error(e.getMessage());
		}
		
		return sudoCnt;
		
	}
	

}
