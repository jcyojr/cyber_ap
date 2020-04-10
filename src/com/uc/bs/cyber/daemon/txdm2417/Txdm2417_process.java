/**
 *  �ֽý��۸� : �λ��û ���̹� ���漼û
 *  ��  ��  �� : ����
 *  ��  ��  �� : ǥ�ؼ��ܼ��� ������� ü�� ����
 *  Ŭ����  ID : Txdm2415_process : �� ó�������� �ۼ��ϴ� Ŭ����
 *  ����  �̷� :
 * ------------------------------------------------------------------------
 *  �ۼ���      	�Ҽ�  		    ����            Tag           ����
 * ------------------------------------------------------------------------
 *  ����ȯ       ��ä��          2015.02.10      %01%         �����ۼ�
 */
package com.uc.bs.cyber.daemon.txdm2417;

import java.util.ArrayList;

import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.dao.DuplicateKeyException;

import com.uc.bs.cyber.CbUtil;
import com.uc.bs.cyber.daemon.Codm_BaseProcess;
import com.uc.bs.cyber.daemon.Codm_interface;
import com.uc.core.MapForm;
import com.uc.core.spring.service.UcContextHolder;


public class Txdm2417_process extends Codm_BaseProcess implements Codm_interface {

	private MapForm dataMap = null;
	private ArrayList<MapForm> list = null;
	private int insert_cnt = 0, update_cnt=0;
	
	
	/**
	 * 
	 */
	public Txdm2417_process() {

		super();
		
		log = LogFactory.getLog(this.getClass());
		
		loopTerm = 250;
	}

	/*Context ���Կ�*/
	public void setApp(ApplicationContext context) {
		this.context = context;
	}
	
	/* process starting */
	public void runProcess() throws Exception {

		//log.info("=====================================================================");
		//log.info("=" + this.getClass().getName()+ " runProcess() ==");
		//log.info("=====================================================================");

		/*Ʈ����� ���� ����*/
		mainTransProcess();

	}

	
	/*Ʈ������� �����ϱ� ���� �Լ�.*/
	private void mainTransProcess(){
		
		log.info("=====================================================================");
		log.info("=[ǥ�ؼ��ܼ���-[" + c_slf_org_nm + "] �����������] Start =");
		log.info("=====================================================================");
		
		try {
				
			setContext(appContext);
			setApp(appContext);

			UcContextHolder.setCustomerType(this.dataSource);
							
			log.info("this.c_slf_org ======================= [" + this.c_slf_org + "]");			

			dataMap = new MapForm();
			dataMap.setMap("SGG_COD", this.c_slf_org);
			int SEQ = 0;
		    //long CNT = 0L;
			try{
				SEQ = cyberService.getOneFieldInteger("TXDM2417.getCyberVirAccNoCount", dataMap);
			}catch (Exception sub_e){
				log.error("SEQ, CNT ����");
				sub_e.printStackTrace();
			}						

			if(SEQ > 0){				
				this.startJob();
			}else{
				log.info("[" + c_slf_org_nm + "] ������ ä���� ������°� �����ϴ�.");
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
		txdm2417_JobProcess();
	
	}
	
	
    /*ǥ�ؼ��ܼ���...����*/
	private int txdm2417_JobProcess() {
		
		log.info("=====================================================================");
		log.info("=" + this.getClass().getName()+ " txdm2417_JobProcess()[ǥ�ؼ��ܼ���-[" + c_slf_org_nm + "] ������� ����] Start =");
		log.info("= govid["+this.govId+"], orgcd["+this.c_slf_org+"]");
		log.info("=====================================================================");
		
		/*�ʱ�ȭ*/
		
		insert_cnt = 0;
		update_cnt = 0;
		
		/*���� �ʱ�ȭ*/
	    list = new ArrayList<MapForm>();

	    MapForm inMap = null;
		/*Ʈ������� �����ϴ� �Ǿ����� ���⼭ �����Ѵ�...*/
		/*�Ǿ����� �����մϴ�.*/
		
		long elapseTime1 = 0;
		long elapseTime2 = 0;
		
		elapseTime1 = System.currentTimeMillis();

		try {

			try{
				list =  cyberService.queryForList("TXDM2417.getCyberVirAccNoTranDataList", dataMap);
			}catch (Exception e){
				e.printStackTrace();
				return 0;				
			}						

			if(list.size() < 1){
				log.debug("������� ���� ����� �����ϴ�.");
				return 0;
			}
		    
			for(int i = 0; i < list.size(); i++){
				inMap = new MapForm();
				inMap = list.get(i);
				
				//�ߺ��̸� �н�
				try{
					/*==========������� ���� ���̺� �Է� ===========*/
					govService.queryForInsert("TXDM2417.insertSeoiVirAccNoData", inMap);
					insert_cnt++;
				}catch (Exception e){

					/*�ߺ��� �߻��ϰų� �������ǿ� ��߳��� ���üũ*/
					if(e instanceof DuplicateKeyException){
						//������� ü�� ���� �ڷ�
						govService.queryForUpdate("TXDM2417.updateSeoiVirAccnoData", inMap);
						update_cnt++;
					}else{
						log.error("���������� = " + inMap.getMaps());
						e.printStackTrace();
						throw (RuntimeException) e;
						
					}
						
				}
				
				try{
					/*==========������� ���� �Ϸ� ===========*/
					cyberService.queryForUpdate("TXDM2417.updateVirAccNoTransEnd", inMap);					
				}catch (Exception e){
					log.error("���������� = " + inMap.getMaps());
					e.printStackTrace();
					throw (RuntimeException) e;					
				}
							
			}
			
			elapseTime2 = System.currentTimeMillis();
			log.info("��ü :: " + list.size() + " ��  ��� :: " + insert_cnt + " ��   ���� :: " + update_cnt + " �� ó��");
			log.info("[" + c_slf_org_nm + "]���ܼ��� ������� ���� �ð�("+list.size()+") : " + CbUtil.formatTime(elapseTime2 - elapseTime1));
					
		} catch (Exception e) {
			e.printStackTrace();
			throw (RuntimeException) e;			
		}
		
		return 0;
	}

}
