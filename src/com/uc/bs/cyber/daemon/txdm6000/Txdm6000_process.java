/**
 *  �ֽý��۸� : �λ��û ���̹� ���漼û
 *  ��  ��  �� : ����/������ ���ڽŰ� ����
 *  ��  ��  �� : ǥ�����漼 �ڵ��� ����� ����/���� ���ڽŰ� ����
 *  Ŭ����  ID : Txdm6000_process : �� ó�������� �ۼ��ϴ� Ŭ����
 *  ����  �̷� :
 * ------------------------------------------------------------------------
 *  �ۼ���      	�Ҽ�  		    ����            Tag           ����
 * ------------------------------------------------------------------------
 *  ����ȯ       ��ä��(��)      2014.04.28         %01%         �����ۼ�
 */
package com.uc.bs.cyber.daemon.txdm6000;

import java.util.ArrayList;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import com.uc.bs.cyber.daemon.Codm_BaseProcess;
import com.uc.bs.cyber.daemon.Codm_interface;
import com.uc.core.MapForm;
import com.uc.core.spring.service.UcContextHolder;


public class Txdm6000_process extends Codm_BaseProcess implements Codm_interface {

	private MapForm dataMap            = null;
	private MapForm sendMap            = null;
	
	
	
	/**
	 * 
	 */
	public Txdm6000_process() {
		
		super();
		
		log = LogFactory.getLog(this.getClass());
		
		/**
		 * 2 �и��� ����
		 */
		loopTerm = 60;
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
		
		log.info("===================================================");
		log.info("====    ǥ�����漼 �ڵ��� ����� ����/���� ����     =====");
		log.info("====================================================");
		
		try {
				
			setContext(appContext);
			setApp(appContext);

			UcContextHolder.setCustomerType(this.dataSource);
						
			dataMap = new MapForm();  /*�ʱ�ȭ*/
			
			//log.info("this.c_slf_org ======================= [" + this.c_slf_org + "]");
		
			int SEQ = 0;
			
			dataMap.setMap("TRTG", "0");
			
			try{
				SEQ = cyberService.getOneFieldInteger("TXDM6000.getTblSingoDataCount", dataMap);
			}catch (Exception e){
				log.info("getTblSingoDataCount ����");
				e.printStackTrace();
			}						

			if(SEQ>0){				
				this.startJob();
			}else{
				log.info("�ڵ��� ����� ����/���� �Ǽ��� �����ϴ�.");
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
		/*1. �ڵ��� ����� ����/���� ó�� ����                               */
		/*---------------------------------------------------------*/
		txdm6000_JobProcess();
	
	}
	
	
	private int txdm6000_JobProcess() {
		
		//���ڽŰ� ��� ���
		ArrayList<MapForm> sendList = new ArrayList<MapForm>(); 
	
		//���ڽŰ� ����
		Txdm6000Service service = new Txdm6000Service();

		try {

			try{
				//���ڽŰ� ���� ��� ��ȸ
				sendList = cyberService.queryForList("TXDM6000.getCarSingoList", dataMap);
			}catch (Exception e){
				log.info("���ڽŰ� ���� �����ȸ ����");
				e.printStackTrace();
			}		
			
			if(sendList.size() == 0){
				log.info("�ڵ��� ����� ����/���� ������ �����ϴ�.");
				return 0;
			}
			
			
			MapForm seqMap = null;
			MapForm taxMap = null;
	
			for(int i=0; i<sendList.size(); i++){
				
				seqMap = new MapForm();				
				
				//���ڽŰ� ������ �ʱ�ȭ
				sendMap = new MapForm();
				//���ڽŰ� ������ ����
				sendMap = sendList.get(i);
				
				try{
					seqMap = cyberService.queryForMap("TXDM6000.getTblSingoSeq", sendMap);
				}catch(Exception e){
					e.printStackTrace();
					log.info("getTblSingoSeq ����");
				}
				
				sendMap.setMap("SNO", seqMap.getMap("SNO"));
				
				taxMap = new MapForm();
				
				try{
					taxMap = service.sndService(sendMap);
				}catch(Exception e){
					e.printStackTrace();
					log.info("sndService ����");
				}
								
				log.info("taxMap :: " + taxMap.getMaps());
				log.info("taxMap.getMap(MESSAGE)  :: "+taxMap.getMap("MESSAGE"));
				//���� ����
				if(taxMap.getMap("MESSAGE").equals("SVR01")){		    		
					log.info("************ �ڵ��� ���� ����/���� �Ű� �Ϸ� *********");		    		
		    		sendMap.setMap("TRTG", "1");
		    		sendMap.setMap("WS_MSG", "����ó��");
		    		sendMap.setMap("CARSTATE", "M8");		    		    		
		    	}else if(taxMap.getMap("MESSAGE").equals("ERR12")){		    		
		    		log.info("************ �ڵ��� ���� ����/���� �Ű� ��� ���� *********");
		    		sendMap.setMap("TRTG", "8");
		    		sendMap.setMap("WS_MSG", "���� �ڷ� ó�� ����");
		    		sendMap.setMap("CARSTATE", "M9");
		    	}else if(taxMap.getMap("MESSAGE").equals("SVR99")){		    		
		    		log.info("************ �ڵ��� ���� ����/���� �Ű� ��� ���� *********");
		    		sendMap.setMap("TRTG", "8");
		    		sendMap.setMap("WS_MSG", "��Ÿ����");
		    		sendMap.setMap("CARSTATE", "M9");
		    	}else{
		    		log.info("************ �ڵ��� ���� ����/���� �Ű� ��� ���� *********");
		    		sendMap.setMap("TRTG", "8");
		    		sendMap.setMap("WS_MSG", "��Ÿ ���� �Դϴ�.");
		    		sendMap.setMap("CARSTATE", "M9");
		    	}
				
				try{
					cyberService.queryForUpdate("TXDM6000.updateOnlineTapSingoTrtg", sendMap);
				}catch(Exception e){
					e.printStackTrace();
					log.info("****** ���ڽŰ� ���̺� ���� ���� *****");
				}
				
				if(sendMap.getMap("SINGO_GBN").equals("10")){
					try{
						cyberService.queryForUpdate("TXDM6000.updateAutoInsertCarstate", sendMap);
					}catch(Exception e){
						e.printStackTrace();
						log.info("****** ���� ���̺� ���� ���� *****");
					}
				}else if(sendMap.getMap("SINGO_GBN").equals("20")){
					try{
						cyberService.queryForUpdate("TXDM6000.updateAutoErasureCarstate", sendMap);
					}catch(Exception e){
						e.printStackTrace();
						log.info("****** ���� ���̺� ���� ���� *****");
					}
					
				}
				
		    	
			}

					
		} catch (Exception e) {
		    
			throw (RuntimeException) e;
		}
		
		return 0;
	}

	
}
