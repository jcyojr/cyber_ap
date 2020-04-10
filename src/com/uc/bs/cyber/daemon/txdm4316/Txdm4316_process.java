/**
 *  �ֽý��۸� : �λ��û ���̹� ���漼û
 *  ��  ��  �� : ����
 *  ��  ��  �� : ���ϼ��� �ǽð� �����ڷ� ����.
 *  Ŭ����  ID : Txdm4316_process : �� ó�������� �ۼ��ϴ� Ŭ����
 *  ����  �̷� :
 * ------------------------------------------------------------------------
 *  �ۼ���      	�Ҽ�  		    ����            Tag           ����
 * ------------------------------------------------------------------------
 *  YHCHOI      ��ä��        2015.04.18        %01%         �����ۼ�
 */
package com.uc.bs.cyber.daemon.txdm4316;

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
public class Txdm4316_process extends Codm_BaseProcess implements Codm_interface {

	private MapForm dataMap  = null;
	
	/**
	 * 
	 */
	public Txdm4316_process() {

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
	@Override
	public void runProcess() throws Exception {
		
		/*Ʈ����� ����*/
		mainTransProcess();
	}

	/*Context ���Կ�*/
	@Override
	public void setDatasrc(String datasrc) {
		this.dataSource = datasrc;
	}

	@Override
	public void transactionJob() {
		txsv4316_JobProcess();
	}


	/*Ʈ������� �����ϱ� ���� �Լ�.*/
	private void mainTransProcess(){
		
		
		try {
			
			setContext(appContext);
			setApp(appContext);

			UcContextHolder.setCustomerType(this.dataSource);
			
			dataMap = new MapForm();  /*�ʱ�ȭ*/
											
			int page = cyberService.getOneFieldInteger("TXDM4316.getSunapDataListCount", dataMap);
				
			log.info("[���ϼ��� ���� �ڷ� ���� = " + page + " ]");
					

			if(page > 0){
				this.startJob(); 
			}else{
				
				/***********************************************************************************/
				try{
					MapForm daemonMap = new MapForm();
					daemonMap.setMap("DAEMON"    , "TXDM4316");
					daemonMap.setMap("DAEMON_NM" , "���ϼ���(VIR_RCVFEE_BS)�����ڷῬ��");
					daemonMap.setMap("SGG_COD"   , c_slf_org);
					daemonMap.setMap("TOTAL_CNT" , 0);
					daemonMap.setMap("INSERT_CNT", 0);
					daemonMap.setMap("UPDATE_CNT", 0);
					daemonMap.setMap("DELETE_CNT", 0);
					daemonMap.setMap("ERROR_CNT" , 0);
					cyberService.queryForInsert("CODMBASE.InsertDaemonProcessCheck", daemonMap);
				}catch(Exception ex){
					log.debug("���ϼ���(VIR_RCVFEE_BS)�����ڷῬ�� ����� �����ϴ�. ��� ����");
				}				
				/***********************************************************************************/
				
				log.info("[���ϼ��� ���� �ڷᰡ �����ϴ�.");
			}
				
						
		} catch (Exception e) {
			e.printStackTrace();
		}			

	}
	
	
	/*�ڷ� ����*/
	private int txsv4316_JobProcess() {
		
		log.info("=====================================================================");
		log.info("=" + this.getClass().getName()+ " txsv4316_JobProcess() [���ϼ��� �ǽð� ���� �ڷ� ����] Start =");
		log.info("=====================================================================");
				
		int insert_cnt = 0;
		int dup_cnt = 0; 
		int err_cnt = 0;
		
		try {
				
			/*���ϼ��� �����ڷ� ����� �����´�.*/
			ArrayList<MapForm> sunapList =  cyberService.queryForList("TXDM4316.getSudoSunapDataList", dataMap);
			
			if(sunapList.size() > 0){
				
				MapForm sunapData = null;
				
				for(int i = 0; i < sunapList.size(); i++){
					
					sunapData = new MapForm();
					
					sunapData = sunapList.get(i);
					
					sunapData.setMap("SD_TRTG", "1");
					
					try{
						//����� �����ڷ� ���
						govService.queryForInsert("TXDM4316.insertSudoSunapData", sunapData);
						
						insert_cnt++;
						
						
						
					}catch(Exception ex){
						
						if(ex instanceof DuplicateKeyException){
							
							log.debug("[���ϼ��� ���� �ڷ� �ߺ� �߻� - ���ۿϷ�� ���� " + sunapData);	
							
							dup_cnt++;
							
						}else{
													
							ex.printStackTrace();
							log.info("[���ϼ��� ���� �ڷ� ���� �� �����߻� - ������ ���� " + sunapData);
							sunapData.setMap("SD_TRTG", "9");
							err_cnt++;
						}			
						
					}
					
					try{
						
						cyberService.queryForUpdate("TXDM4316.updateCyberTrtgData", sunapData);
					}catch(Exception exc){
						exc.printStackTrace();
						log.info("[���ϼ��� ���� ���ۿϷ� �����߻� !!!! " + sunapData);
					}
					
					
				}
				
			}else{
				log.info("[���ϼ��� ���� �ڷᰡ �����ϴ�.");
			}

			log.info("����� �����ڷ� ��ü�Ǽ� [ " + sunapList.size() + " ] �� ��ϿϷ� [ " + insert_cnt + " ] �� ���� [ " + err_cnt + " ] �� �ߺ� [ " + dup_cnt + " ] �� �Դϴ�.");
			
			/***********************************************************************************/
			try{
				MapForm daemonMap = new MapForm();
				daemonMap.setMap("DAEMON"    , "TXDM4316");
				daemonMap.setMap("DAEMON_NM" , "���ϼ���(VIR_RCVFEE_BS)�����ڷῬ��");
				daemonMap.setMap("SGG_COD"   , c_slf_org);
				daemonMap.setMap("TOTAL_CNT" , sunapList.size());
				daemonMap.setMap("INSERT_CNT", insert_cnt);
				daemonMap.setMap("UPDATE_CNT", dup_cnt);
				daemonMap.setMap("DELETE_CNT", 0);
				daemonMap.setMap("ERROR_CNT" , err_cnt);
				cyberService.queryForInsert("CODMBASE.InsertDaemonProcessCheck", daemonMap);
			}catch(Exception ex){
				log.debug("���ϼ���(VIR_RCVFEE_BS)�����ڷῬ�� ����� �����ϴ�. ��� ����");
			}				
			/***********************************************************************************/
			
		}catch (Exception e){
			e.printStackTrace();
			log.error(e.getMessage());
		}
		
		return 0;
		
	}
	

}
