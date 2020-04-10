/**
 *  �ֽý��۸� : �λ��û ���̹� ���漼û
 *  ��  ��  �� : ����
 *  ��  ��  �� : ǥ�ؼ��ܼ��� ������� ü�� ����
 *  Ŭ����  ID : Txdm2418_process : �� ó�������� �ۼ��ϴ� Ŭ����
 *  ����  �̷� :
 * ------------------------------------------------------------------------
 *  �ۼ���      	�Ҽ�  		    ����            Tag           ����
 * ------------------------------------------------------------------------
 *  ����ȯ       ��ä��          2015.02.10      %01%         �����ۼ�
 */
package com.uc.bs.cyber.daemon.txdm2418;

import java.util.ArrayList;

import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;

import com.uc.bs.cyber.CbUtil;
import com.uc.bs.cyber.daemon.Codm_BaseProcess;
import com.uc.bs.cyber.daemon.Codm_interface;
import com.uc.core.MapForm;
import com.uc.core.spring.service.UcContextHolder;


public class Txdm2418_process extends Codm_BaseProcess implements Codm_interface {

	private MapForm dataMap = null;
	private ArrayList<MapForm> list = null;
	
	
	/**
	 * 
	 */
	public Txdm2418_process() {

		super();
		
		log = LogFactory.getLog(this.getClass());
		
		/**
		 * 5 �и��� ����
		 */
		loopTerm = 310;
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
		log.info("=[ǥ�ؼ��ܼ���(TX2112_TB)- ������� ���] Start =");
		log.info("=====================================================================");
		
		try {
				
			setContext(appContext);
			setApp(appContext);

			UcContextHolder.setCustomerType(this.dataSource);
							
			log.info("this.c_slf_org ======================= [" + this.c_slf_org + "]");


			int SEQ = 0;
		    //long CNT = 0L;
			try{
				SEQ = cyberService.getOneFieldInteger("TXDM2418.getTx2112VirAccNoUpdateCount", null);
			}catch (Exception sub_e){
				log.error("SEQ, CNT ����");
				sub_e.printStackTrace();
			}						

			if(SEQ > 0){				
				this.startJob();
			}else{
				log.info("ǥ�ؼ��ܼ���(TX2112_TB)- ������� ��� ��� �Ǽ��� �����ϴ�.");
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
		txdm2418_JobProcess();
	
	}
	
	
    /*ǥ�ؼ��ܼ���...����*/
	private int txdm2418_JobProcess() {
		
		log.info("=====================================================================");
		log.info("=" + this.getClass().getName()+ " txdm2418_JobProcess()[ǥ�ؼ��ܼ���(TX2112_TB)- ������� ���] Start =");
		log.info("= govid["+this.govId+"], orgcd["+this.c_slf_org+"]");
		log.info("=====================================================================");
		
		/*�ʱ�ȭ*/
		int insert_cnt = 0;
		int wait_cnt = 0;
		
		/*���� �ʱ�ȭ*/
	    dataMap = new MapForm();
	    list = new ArrayList<MapForm>();

	    MapForm inMap = null;
		/*Ʈ������� �����ϴ� �Ǿ����� ���⼭ �����Ѵ�...*/
		/*�Ǿ����� �����մϴ�.*/
		
		long elapseTime1 = 0;
		long elapseTime2 = 0;
		
		elapseTime1 = System.currentTimeMillis();

		try {

			try{
				list =  cyberService.queryForList("TXDM2418.getTx2112VirAccNoUpdateList", dataMap);
			}catch (Exception e){
				e.printStackTrace();
				log.error("Exception getTx2112VirAccNoUpdateList");
				throw (RuntimeException) e;			
			}						

			if(list.size() < 1){
				log.debug("������� ���(TX2112) ��� ����� �����ϴ�.");
				return 0;
			}
		    
			for(int i = 0; i < list.size(); i++){
				inMap = new MapForm();
				inMap = list.get(i);
				String proc_cls = null;
				try{
					proc_cls = cyberService.getOneFieldString("TXDM2418.selectTx2112BugwaDataBonjanBigo", inMap);
				}catch(Exception ex){
					log.error("Exception ex �ݾ� �� ����");
					ex.printStackTrace();
					throw (RuntimeException) ex;
				}
				
				if(proc_cls == null || proc_cls.equals("")){
					wait_cnt++;
					continue;
				}else{
					inMap.setMap("PROC_CLS", proc_cls);
				}
				
				try{
					/*==========������� ���� ���̺� �Է� ===========*/
					if(cyberService.queryForUpdate("TXDM2418.updateTx2112VirAccNo", inMap) > 0){
						cyberService.queryForUpdate("TXDM2418.updateTx2114End", inMap);
						insert_cnt++;
					}else{
						wait_cnt++;
					}

				}catch (Exception e){
					log.error("���������� = " + inMap.getMaps());
					e.printStackTrace();
					throw (RuntimeException) e;
				}
												
			}
			
			elapseTime2 = System.currentTimeMillis();
			
			log.info("TX2112_TB ������� ��� �ð�("+list.size()+") : " + CbUtil.formatTime(elapseTime2 - elapseTime1) + " ����Ϸ� : " + insert_cnt + "��, ��� : " + wait_cnt + "��");
					
		} catch (Exception e) {
			e.printStackTrace();
			throw (RuntimeException) e;			
		}
		
		return 0;
	}

}
