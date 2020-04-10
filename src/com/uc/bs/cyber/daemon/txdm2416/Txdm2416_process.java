/**
 *  �ֽý��۸� : �λ��û ���̹� ���漼û
 *  ��  ��  �� : ����
 *  ��  ��  �� : ǥ�ؼ��ܼ��� ������� ü����� ����
 *  Ŭ����  ID : Txdm2416_process : �� ó�������� �ۼ��ϴ� Ŭ����
 *  ����  �̷� :
 * ------------------------------------------------------------------------
 *  �ۼ���      	�Ҽ�  		    ����            Tag           ����
 * ------------------------------------------------------------------------
 *  ����ȯ       ��ä��(��)      2015.02.11         %01%         �����ۼ�
 */
package com.uc.bs.cyber.daemon.txdm2416;

import java.util.ArrayList;

import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.dao.DuplicateKeyException;

import com.uc.bs.cyber.daemon.Codm_BaseProcess;
import com.uc.bs.cyber.daemon.Codm_interface;
import com.uc.core.MapForm;
import com.uc.core.spring.service.UcContextHolder;


public class Txdm2416_process extends Codm_BaseProcess implements Codm_interface {

	private MapForm dataMap = null;
	private int insert_cnt = 0;
	private int update_cnt = 0;
	private int err_cnt = 0;
	
	
	/**
	 * 
	 */
	public Txdm2416_process() {

		super();
		
		log = LogFactory.getLog(this.getClass());
		
		/**
		 * 8 �и��� ����
		 */
		loopTerm = 60 * 8;
	}

	/*Context ���Կ�*/
	public void setApp(ApplicationContext context) {
		this.context = context;
	}
	

	public void runProcess() throws Exception {

		/*Ʈ����� ���� ����*/
		mainTransProcess();

	}

	/*Ʈ������� �����ϱ� ���� �Լ�.*/
	private void mainTransProcess(){
		
		log.info("=====================================================================");
		log.info("=[ǥ�ؼ��ܼ���-[" + c_slf_org_nm + "] ������¿���] Start =");
		log.info("=====================================================================");
		
		try {
				
			setContext(appContext);
			setApp(appContext);

			UcContextHolder.setCustomerType(this.dataSource);
							
			log.info("this.c_slf_org ======================= [" + this.c_slf_org + "]");


			dataMap = new MapForm();
			dataMap.setMap("SGG_COD"      , this.c_slf_org);  /*��û�ڵ� ����*/
			int SEQ = 0;

			try{
				SEQ = govService.getOneFieldInteger("TXDM2416.getVirAccNoLinkCount", dataMap);
				log.debug("SEQ :: " + SEQ);
			}catch (Exception sub_e){
				log.error("SEQ, CNT ����");
				SEQ = -1;
				sub_e.printStackTrace();
			}						

			if(SEQ > 0){				
				this.startJob();
			}else if(SEQ == 0){
				log.info("["+ c_slf_org_nm + "] �� ������� ü�� ����� �����ϴ�.");
				
				/***********************************************************************************/
				try{
					MapForm daemonMap = new MapForm();
					daemonMap.setMap("DAEMON"    , "TXDM2416");
					daemonMap.setMap("DAEMON_NM" , "���ܼ��� ������� ä������");
					daemonMap.setMap("SGG_COD"   , c_slf_org);
					daemonMap.setMap("TOTAL_CNT" , SEQ);
					daemonMap.setMap("INSERT_CNT", 0);
					daemonMap.setMap("UPDATE_CNT", 0);
					daemonMap.setMap("DELETE_CNT", 0);
					daemonMap.setMap("ERROR_CNT" , 0);
					cyberService.queryForInsert("CODMBASE.InsertDaemonProcessCheck", daemonMap);
				}catch(Exception ex){
					log.debug("��� ������� ü�� ����� �����ϴ�. ��� ����");
				}				
				/***********************************************************************************/
				
			}else{
				log.info("["+ c_slf_org_nm + "] �� ���赥���� ��ȸ ����");
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
			
		/*---------------------------------------------------------*/
		/*1. �ΰ������ڷ� ���� ó��.                               */
		/*---------------------------------------------------------*/
		txdm2416_JobProcess();
	
	}
	
	
    /*ǥ�ؼ��ܼ���...����*/
	private int txdm2416_JobProcess() {
		
		log.info("=====================================================================");
		log.info("=" + this.getClass().getName()+ " txdm2416_JobProcess()[ǥ�ؼ��ܼ���-[" + c_slf_org_nm + "] ������¿���] Start =");
		log.info("= govid["+this.govId+"], orgcd["+this.c_slf_org+"]");
		log.info("=====================================================================");
		
		/*�ʱ�ȭ*/
		
		insert_cnt = 0;
		update_cnt = 0;
		err_cnt = 0;
		int dup_cnt = 0;
	
	    ArrayList<MapForm> list  = new ArrayList<MapForm>();

	    MapForm inMap = null;
		/*Ʈ������� �����ϴ� �Ǿ����� ���⼭ �����Ѵ�...*/
		/*�Ǿ����� �����մϴ�.*/
		

		try {

			try{
		
				list = govService.queryForList("TXDM2416.getVirAccNoDataList", dataMap);
	
			}catch (Exception e){
				e.printStackTrace();
				return 0;				
			}						

			if(list.size() < 1){
				log.info("������� ü�� ����� �����ϴ�.");
				return 0;
			}
		    
			for(int i = 0; i < list.size(); i++){
				inMap = new MapForm();
				inMap = list.get(i);
				
				//log.debug("inMap  :: " + inMap);
				//�ߺ��̸� �н�
				try{
					
					/*==========������� ü�� ���̺� �Է� ===========*/
					cyberService.queryForInsert("TXDM2416.insertTx2114VirAccNoData", inMap);
					insert_cnt++;
				}catch (Exception e){

					/*�ߺ��� �߻��ϰų� �������ǿ� ��߳��� ���üũ*/
					if(e instanceof DuplicateKeyException){
						//������� ü�� ���� �ڷ�
						//log.debug("�ߺ��� �н� :: " + inMap.getMap("TAX_NO"));
						//�ߺ����� �������� ��� OCR��� ���Ͽ� ������Ʈ ó�� �߰�
						String ocr = null;
						try{
						    ocr = cyberService.getOneFieldString("TXDM2416.getDuplicatekeyOcrBd", inMap);
						}catch(Exception ex){
							log.debug("ocr üũ ����");
							ex.printStackTrace();
							throw (RuntimeException) ex;
						}
						//ä������ �ڷ�
						if(ocr == null || ocr.equals("")){
							dup_cnt++;
							continue;
						}
						//�����ڷ�
						else if(ocr.equals(inMap.getMap("OCR_BD"))){
							err_cnt++;
							continue;
						}
						//���������
						else{
						    cyberService.queryForUpdate("TXDM2416.updateOCRBDandETC", inMap);
							update_cnt++;
							continue;
						}
						
					}else{
						log.error("���������� = " + inMap.getMaps());
						e.printStackTrace();
						throw (RuntimeException) e;
						
					}
					
				}
				
			}
						
			log.info("[" + c_slf_org_nm + "]���ܼ��� ������¿��� ("+list.size()+") �� ��� : " + insert_cnt + "��  �ߺ� : " + dup_cnt + "��  ���� : " + update_cnt + "��  ��ӿ��� : " + err_cnt + "��");
			
			/***********************************************************************************/
			try{
				MapForm daemonMap = new MapForm();
				daemonMap.setMap("DAEMON"    , "TXDM2416");
				daemonMap.setMap("DAEMON_NM" , "���ܼ��� ������� ä������");
				daemonMap.setMap("SGG_COD"   , c_slf_org);
				daemonMap.setMap("TOTAL_CNT" , list.size());
				daemonMap.setMap("INSERT_CNT", insert_cnt);
				daemonMap.setMap("UPDATE_CNT", dup_cnt);
				daemonMap.setMap("DELETE_CNT", 0);
				daemonMap.setMap("ERROR_CNT" , err_cnt);
				cyberService.queryForInsert("CODMBASE.InsertDaemonProcessCheck", daemonMap);
			}catch(Exception ex){
				log.debug("��� ó���α� ��� ����");
			}				
			/***********************************************************************************/
			
		} catch (Exception e) {
			e.printStackTrace();
			throw (RuntimeException) e;			
		}
		
		return 0;
	}

}
