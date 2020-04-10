/**
 *  �ֽý��۸� : �λ��û ���̹� ���漼û
 *  ��  ��  �� : ����
 *  ��  ��  �� : �����Է¿볳������������, �����Է¿� �������������, ���� ���ý����Ҽ���������
 *  Ŭ����  ID : Txdm1175_process : �� ó�������� �ۼ��ϴ� Ŭ����
 *  ����  �̷� :
 * ------------------------------------------------------------------------
 *  �ۼ���      	�Ҽ�  		    ����            Tag           ����
 * ------------------------------------------------------------------------
 *  õ����       ��ä��(��)      2012.02.10         %01%         �����ۼ�
 */
package com.uc.bs.cyber.daemon.txdm1175;

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
 */
public class Txdm1175_process extends Codm_BaseProcess implements Codm_interface {

	private MapForm dataMap            = null;
	private int insert_cnt = 0, update_cnt = 0;
	
	/*Ʈ������� ���Ͽ� */
	MapForm gblNidLevyRows    = null;
	
	/**
	 * 
	 */
	public Txdm1175_process() {

		super();
		
		log = LogFactory.getLog(this.getClass());
		
		/**
		 * 30 �и��� ����
		 */
		loopTerm = 60 * 30;
	}

	/*Context ���Կ�*/
	public void setApp(ApplicationContext context) {
		this.context = context;
	}
	
	/* process starting */
	public void runProcess() throws Exception {

		/*Ʈ����� ���� ����*/
		mainTransProcess();

	}

	
	/*Ʈ������� �����ϱ� ���� �Լ�.*/
	private void mainTransProcess(){
		
		log.info("=====================================================================");
		log.info("=[�����Է¿볳��������-��û �ڷῬ��] Start =" + this.c_slf_org);
		log.info("=====================================================================");
		

		try {
				
			setContext(appContext);
			setApp(appContext);

			UcContextHolder.setCustomerType(this.dataSource);
						
			dataMap = new MapForm();  /*�ʱ�ȭ*/
			
			//�����Է¿볳��������
			int page1 = govService.getOneFieldInteger("txdm1175.txdm1171_select_count_page", dataMap);
			//�����Է¿�����������	
			int page2 = govService.getOneFieldInteger("txdm1175.txdm1172_select_count_page", dataMap);
			//���� ���ý����Ҽ����� ����
			int page4 = govService.getOneFieldInteger("txdm1175.txdm1174_select_count_page", dataMap);
				
											
			if(page1 > 0){
				dataMap.setMap("JOB_GBN", "1");			  
				this.startJob();             /*��Ƽ Ʈ����� ȣ��*/
					
			}else{
				log.info("[�����Է¿볳�������� TX1171]-��û "+ this.c_slf_org +" �ڷᰡ �����ϴ�.");
				
				/***********************************************************************************/
				try{
					MapForm daemonMap = new MapForm();
					daemonMap.setMap("DAEMON"    , "TXDM1175");
					daemonMap.setMap("DAEMON_NM" , "�����Է¿�(V_TTPRCON1)����");
					daemonMap.setMap("SGG_COD"   , c_slf_org);
					daemonMap.setMap("TOTAL_CNT" , page1);
					daemonMap.setMap("INSERT_CNT", 0);
					daemonMap.setMap("UPDATE_CNT", 0);
					daemonMap.setMap("DELETE_CNT", 0);
					daemonMap.setMap("ERROR_CNT" , 0);
					cyberService.queryForInsert("CODMBASE.InsertDaemonProcessCheck", daemonMap);
				}catch(Exception ex){
					log.debug("���漼 ����������� �Ǽ��� �����ϴ�. ��� ����");
				}				
				/***********************************************************************************/
				
			}
			
			if(page2 > 0){
				dataMap.setMap("JOB_GBN", "2");			  
				this.startJob();             /*��Ƽ Ʈ����� ȣ��*/
					
			}else{
				log.info("[�����Է¿����������� TX1172] -��û "+ this.c_slf_org +" �ڷᰡ �����ϴ�.");
			}
			
			if(page4 > 0){
				dataMap.setMap("JOB_GBN", "4");			  
				this.startJob();             /*��Ƽ Ʈ����� ȣ��*/
					
			}else{
				log.info("[���� ���ý����Ҽ����� ���� TX1174] -��û "+ this.c_slf_org +" �ڷᰡ �����ϴ�.");
			}
			
				
		
		} catch (Exception e) {
			e.printStackTrace();
		}			
		
	}
	
	/*�� ����� ���� ����...*/
	public void setDatasrc(String datasrc) {
		this.dataSource = datasrc;
	}
	
	/*Ʈ����� ����*/
	public void transactionJob() {
			
		if(dataMap.getMap("JOB_GBN").equals("1")){
			txdm1171_JobProcess();
		}else if(dataMap.getMap("JOB_GBN").equals("2")){
			txdm1172_JobProcess();
		}else if(dataMap.getMap("JOB_GBN").equals("4")){
			txdm1174_JobProcess();
		}else{
			log.debug("JOB_GBN ���� �Դϴ�. " + dataMap.getMap("JOB_GBN"));
		}
			
	}
    	
	/*�����Է¿볳������������...����(��û)*/
	private int txdm1171_JobProcess() {
			
		log.info("=====================================================================");
		log.info("=" + this.getClass().getName()+ " txdm1175_JobProcess()[�����Է¿볳��������-��û �ڷῬ��] Start =");
		log.info("= govid["+this.govId+"], orgcd["+this.c_slf_org+"]");
		log.info("=====================================================================");
			
		/*�ʱ�ȭ*/			
		insert_cnt = 0;
		update_cnt = 0; 
			
		/*���� �ʱ�ȭ*/
		gblNidLevyRows = new MapForm();

		/*Ʈ������� �����ϴ� �Ǿ����� ���⼭ �����Ѵ�...*/
		/*�Ǿ����� �����մϴ�.*/
			
		int rec_cnt = 0;			
		int tot_size = 0;
			
		long elapseTime1 = 0;
		long elapseTime2 = 0;
			
		elapseTime1 = System.currentTimeMillis();

		/*---------------------------------------------------------*/
		/*1. �ΰ������ڷ� ���� ó��.                               */
		/*---------------------------------------------------------*/
			
		try {
            
			dataMap.setMap("SGG_COD",  this.c_slf_org);


			/*�������̺� �ΰ��ڷ� SELECT ����(TAX)*/
			ArrayList<MapForm> alNidLevyList =  govService.queryForList("txdm1175.txdm1171_select_list", dataMap);
				
			tot_size = alNidLevyList.size();
				
			   log.info("�����Է¿볳�������� �����ڷ� �Ǽ� = [" + tot_size + "]");
				
			if (tot_size  >  0)   {
			    		
				/*���ڳ����ڷ� 1�Ǿ� fetch ó�� */
				for ( rec_cnt = 0;  rec_cnt < tot_size;  rec_cnt++)   {

					gblNidLevyRows =  alNidLevyList.get(rec_cnt);
						
					/*Ȥ�ó�...because of testing */
					if(gblNidLevyRows == null || gblNidLevyRows.isEmpty() || gblNidLevyRows.getMap("REG_NO_CHECK").equals("99999")){
						continue;
					}
						
					/*�����Ͱ� ������ �μ�Ʈ ������ ������Ʈ*/ 
					gblNidLevyRows.setMap("SGG_COD", dataMap.getMap("SGG_COD"));
						
					try{
						cyberService.queryForInsert("txdm1175.txdm1171_insert_tx1601_tb", gblNidLevyRows);
						insert_cnt++;

					}catch(Exception e){
							
						/*�ߺ��� �߻��ϰų� �������ǿ� ��߳��� ���üũ : sgg_cod , reg_no �� �ߺ�*/
						if(e instanceof DuplicateKeyException){	// �⺻Ű�� �ߺ��Ǽ� ����ó���Ȱ�� 
									
							try{
								cyberService.queryForInsert("txdm1175.txdm1171_update_tx1601_tb", gblNidLevyRows);
								update_cnt++;
										
							}catch(Exception be){

								be.printStackTrace();
							}
							
						}else{
							log.error("���������� = " + gblNidLevyRows.getMaps());
							e.printStackTrace();							
							throw (RuntimeException) e;
						}										
					}
				}
			}
					
			log.info("�����Է¿볳������������ TX1171 �ڷῬ�� �Ǽ� [" + rec_cnt + "] ��ϰǼ� [" + insert_cnt +"] �����Ǽ� [" + update_cnt + "]");
			
			/***********************************************************************************/
			try{
				MapForm daemonMap = new MapForm();
				daemonMap.setMap("DAEMON"    , "TXDM1175");
				daemonMap.setMap("DAEMON_NM" , "�����Է¿�(V_TTPRCON1)����");
				daemonMap.setMap("SGG_COD"   , c_slf_org);
				daemonMap.setMap("TOTAL_CNT" , rec_cnt);
				daemonMap.setMap("INSERT_CNT", insert_cnt);
				daemonMap.setMap("UPDATE_CNT", update_cnt);
				daemonMap.setMap("DELETE_CNT", 0);
				daemonMap.setMap("ERROR_CNT" , 0);
				cyberService.queryForInsert("CODMBASE.InsertDaemonProcessCheck", daemonMap);
			}catch(Exception ex){
				log.debug("���漼 ����������� �Ǽ� �α� ��� ����");
			}				
			/***********************************************************************************/

			elapseTime2 = System.currentTimeMillis();
				
			log.info("�����Է¿볳������������ �ڷῬ�� �ð�("+tot_size+") : " + CbUtil.formatTime(elapseTime2 - elapseTime1));
						
		}catch (Exception e){
			log.error("���� ������ = " + gblNidLevyRows.getMaps());
			throw (RuntimeException) e;
		}
			
		return tot_size;
	}
		
		
	/*�����Է¿�������������...����(��û)*/
	private int txdm1172_JobProcess() {
			
		log.info("=====================================================================");
		log.info("=" + this.getClass().getName()+ " txdm1175_JobProcess()[�����Է¿�����������-��û �ڷῬ��] Start =");
		log.info("= govid["+this.govId+"], orgcd["+this.c_slf_org+"]");
		log.info("=====================================================================");
			
		/*�ʱ�ȭ*/
			
		insert_cnt = 0;
		update_cnt = 0; 
			
		/*���� �ʱ�ȭ*/
		gblNidLevyRows = new MapForm();

		/*Ʈ������� �����ϴ� �Ǿ����� ���⼭ �����Ѵ�...*/
		/*�Ǿ����� �����մϴ�.*/
			
		int rec_cnt = 0;			
		int tot_size = 0;
			
		long elapseTime1 = 0;
		long elapseTime2 = 0;
			
		elapseTime1 = System.currentTimeMillis();

		/*---------------------------------------------------------*/
		/*1. �ΰ������ڷ� ���� ó��.                               */
		/*---------------------------------------------------------*/
			
		try {

			dataMap.setMap("SGG_COD",  this.c_slf_org);

			/*�������̺� �ΰ��ڷ� SELECT ����(TAX)*/
			ArrayList<MapForm> alNidLevyList =  govService.queryForList("txdm1175.txdm1172_select_list", dataMap);
				
			tot_size = alNidLevyList.size();
				
			log.info("�����Է¿����������� �����ڷ� �Ǽ� = [" + tot_size + "]");
				
		    if(tot_size > 0){
					
				/*���ڳ����ڷ� 1�Ǿ� fetch ó�� */
				for(rec_cnt = 0; rec_cnt < tot_size; rec_cnt++){

					gblNidLevyRows =  alNidLevyList.get(rec_cnt);
						
					/*Ȥ�ó�...because of testing */
					if(gblNidLevyRows == null || gblNidLevyRows.isEmpty()){
						continue;
					}
						
					/*�����Ͱ� ������ �μ�Ʈ ������ ������Ʈ*/ 
					gblNidLevyRows.setMap("SGG_COD", dataMap.getMap("SGG_COD"));
						
					try{
						cyberService.queryForInsert("txdm1175.txdm1172_insert_tx1602_tb", gblNidLevyRows);
						insert_cnt++;

					}catch(Exception e){
							
						/*�ߺ��� �߻��ϰų� �������ǿ� ��߳��� ���üũ : sgg_cod , reg_no �� �ߺ�*/
						if(e instanceof DuplicateKeyException){	// �⺻Ű�� �ߺ��Ǽ� ����ó���Ȱ�� 
									
							try{
								cyberService.queryForInsert("txdm1175.txdm1172_update_tx1602_tb", gblNidLevyRows);
								update_cnt++;
										
							}catch(Exception be){
								be.printStackTrace();
							}
							
						}else{
							log.error("���������� = " + gblNidLevyRows.getMaps());
							e.printStackTrace();							
							throw (RuntimeException) e;
						}					
					}
				}
			}
					
			log.info("�����Է¿��������������� TX1172 �ڷῬ�� �Ǽ� [" + rec_cnt + "] ��ϰǼ� [" + insert_cnt +"] �����Ǽ� [" + update_cnt + "]");
			elapseTime2 = System.currentTimeMillis();				
			log.info("��û���ܼ��� �ΰ��ڷῬ�� �ð�("+tot_size+") : " + CbUtil.formatTime(elapseTime2 - elapseTime1));
						
		}catch(Exception e){
			log.error("���� ������ = " + gblNidLevyRows.getMaps());
			throw (RuntimeException) e;
		}
			
		return tot_size;
	}
		
	
	/*�����Է¿�������������...����(��û)*/
	private int txdm1174_JobProcess() {
			
		log.info("=====================================================================");
		log.info("=" + this.getClass().getName()+ " txdm1175_JobProcess()[���� ���ý����Ҽ����� ����-��û �ڷῬ��] Start =");
		log.info("= govid["+this.govId+"], orgcd["+this.c_slf_org+"]");
		log.info("=====================================================================");
			
		/*�ʱ�ȭ*/
			
		insert_cnt = 0;
		update_cnt = 0; 
			
		/*���� �ʱ�ȭ*/
		gblNidLevyRows = new MapForm();

		/*Ʈ������� �����ϴ� �Ǿ����� ���⼭ �����Ѵ�...*/
		/*�Ǿ����� �����մϴ�.*/
			
		int rec_cnt = 0;
			
		int tot_size = 0;
			
		long elapseTime1 = 0;
		long elapseTime2 = 0;
			
		elapseTime1 = System.currentTimeMillis();

		/*---------------------------------------------------------*/
		/*1. �ΰ������ڷ� ���� ó��.                               */
		/*---------------------------------------------------------*/
			
		try{

			dataMap.setMap("SGG_COD",  this.c_slf_org);

			/*�������̺� �ΰ��ڷ� SELECT ����(TAX)*/
			ArrayList<MapForm> alNidLevyList =  govService.queryForList("txdm1175.txdm1174_select_list", dataMap);
				
			tot_size = alNidLevyList.size();
				
			log.info("���� ���ý����Ҽ����� ���� �����ڷ� �Ǽ� = [" + tot_size + "]");
				
			if(tot_size > 0){
					
				/*���ڳ����ڷ� 1�Ǿ� fetch ó�� */
				for(rec_cnt = 0; rec_cnt < tot_size; rec_cnt++){

					gblNidLevyRows =  alNidLevyList.get(rec_cnt);
						
					/*Ȥ�ó�...because of testing */
					if(gblNidLevyRows == null || gblNidLevyRows.isEmpty()){
							continue;
					}						
						
					try{
						cyberService.queryForInsert("txdm1175.txdm1174_insert_tx1604_tb", gblNidLevyRows);
						insert_cnt++;

					}catch(Exception e){
							
						/*�ߺ��� �߻��ϰų� �������ǿ� ��߳��� ���üũ : sgg_cod , reg_no �� �ߺ�*/
						if(e instanceof DuplicateKeyException){	// �⺻Ű�� �ߺ��Ǽ� ����ó���Ȱ�� 
									
							try{
								cyberService.queryForInsert("txdm1175.txdm1174_update_tx1604_tb", gblNidLevyRows);
								update_cnt++;
										
							}catch(Exception be){
								be.printStackTrace();
							}
						
						}else{
							log.error("���������� = " + gblNidLevyRows.getMaps());
							e.printStackTrace();							
							throw (RuntimeException) e;
						}
					}
				}
			}
					
			log.info("�������ý����Ҽ����� TX1174 �ڷῬ�� �Ǽ� [" + rec_cnt + "] ��ϰǼ� [" + insert_cnt +"] �����Ǽ� [" + update_cnt + "]");
		    elapseTime2 = System.currentTimeMillis();		
			log.info("���ý����Ҽ����� �ΰ��ڷῬ�� �ð�("+tot_size+") : " + CbUtil.formatTime(elapseTime2 - elapseTime1));
						
		}catch(Exception e){
			log.error("���� ������ = " + gblNidLevyRows.getMaps());
			throw (RuntimeException) e;
		}
			
		return tot_size;
	}
		

}
