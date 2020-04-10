/**
 *  �ֽý��۸� : �λ��û ���̹� ���漼û
 *  ��  ��  �� : ����
 *  ��  ��  �� : ǥ�ؼ��ܼ��� �����ڷῬ��
 *  Ŭ����  ID : Txdm2413_process : �� ó�������� �ۼ��ϴ� Ŭ����
 *  ����  �̷� :
 * ------------------------------------------------------------------------
 *  �ۼ���      	�Ҽ�  		    ����            Tag           ����
 * ------------------------------------------------------------------------
 *  ǥ����       �ٻ�ý���      2013.12.12         %01%         �����ۼ�
 */
package com.uc.bs.cyber.daemon.txdm2413;

import java.util.ArrayList;

import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;

import com.uc.bs.cyber.CbUtil;
import com.uc.bs.cyber.daemon.Codm_BaseProcess;
import com.uc.bs.cyber.daemon.Codm_interface;
import com.uc.core.MapForm;
import com.uc.core.spring.service.UcContextHolder;

public class Txdm2413_process extends Codm_BaseProcess implements Codm_interface {

	private MapForm dataMap            = null;
	private int insert_cnt = 0, update_cnt = 0, delete_cnt = 0, remote_up_cnt = 0;
    private int p_del_cnt = 0;
	
	/*Ʈ������� ���Ͽ� */
	MapForm gblNidLevyRows    = null;
	
	/**
	 * 
	 */
	public Txdm2413_process() {
		// TODO Auto-generated constructor stub
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
	
	/* process starting */
	@Override
	public void runProcess() throws Exception {
		// TODO Auto-generated method stub
		//log.info("=====================================================================");
		//log.info("=" + this.getClass().getName()+ " runProcess() ==");
		//log.info("=====================================================================");

		/*Ʈ����� ���� ����*/
		mainTransProcess();

	}

	/*Ʈ������� �����ϱ� ���� �Լ�.*/
	private void mainTransProcess(){
		
		log.info("=====================================================================");
		log.info("=[ǥ�ؼ��ܼ���-[" + c_slf_org_nm + "] �����ڷῬ��] Start =");
		log.info("=====================================================================");
		
		try {
				
			setContext(appContext);
			setApp(appContext);

			UcContextHolder.setCustomerType(this.dataSource);
						
			dataMap = new MapForm();  /*�ʱ�ȭ*/
			
			log.info("this.c_slf_org ======================= [" + this.c_slf_org + "]");
			
			do {
				
				int page = govService.getOneFieldInteger("TXDM2413.SELECT_SPNT300_CNT", dataMap);
				
				log.info("[ǥ�ؼ��ܼ���-[" + c_slf_org_nm + "]] CNT = [" + page + "]");
				
				if(page == 0) break;
				
				this.startJob();               /*��Ƽ Ʈ����� ȣ��*/
				
				if(govService.getOneFieldInteger("TXDM2413.SELECT_SPNT300_CNT", dataMap) == 0) {
					break;
				}
				
			}while(true);
					
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}			
		
	}
	
	/*�� ����� ���� ����...*/
	@Override
	public void setDatasrc(String datasrc) {
		// TODO Auto-generated method stub
		this.dataSource = datasrc;
	}
	
	/*Ʈ����� ����*/
	@Override
	public void transactionJob() {
		// TODO Auto-generated method stub
			
		/*---------------------------------------------------------*/
		/*1. �ΰ������ڷ� ���� ó��.                               */
		/*---------------------------------------------------------*/
		txdm2413_JobProcess();
	
	}
	
	
    /*ǥ�ؼ��ܼ���...����*/
	private int txdm2413_JobProcess() {
		
		log.info("=====================================================================");
		log.info("=" + this.getClass().getName()+ " txdm2413_JobProcess()[ǥ�ؼ��ܼ���-[" + c_slf_org_nm + "] �����ڷῬ��] Start =");
		log.info("= govid["+this.govId+"], orgcd["+this.c_slf_org+"]");
		log.info("=====================================================================");
		
		/*�ʱ�ȭ*/
		
		insert_cnt = 0;
		update_cnt = 0; 
		delete_cnt = 0;
		remote_up_cnt = 0;
		p_del_cnt     = 0;
		
		/*���� �ʱ�ȭ*/
		gblNidLevyRows = new MapForm();

		/*Ʈ������� �����ϴ� �Ǿ����� ���⼭ �����Ѵ�...*/
		/*�Ǿ����� �����մϴ�.*/

		int rec_cnt = 0;
		int nul_cnt = 0;
		
		int tot_size = 0;
		
		long elapseTime1 = 0;
		long elapseTime2 = 0;
		
		elapseTime1 = System.currentTimeMillis();

		/*---------------------------------------------------------*/
		/*1. ���������ڷ� ���� ó��.                               */
		/*---------------------------------------------------------*/
		
		try {

			/*�������̺� �ΰ��ڷ� SELECT ����(NIS)*/
			ArrayList<MapForm> alNidLevyList =  govService.queryForList("TXDM2413.SELECT_SPNT300_LIST", dataMap);
			
			tot_size = alNidLevyList.size();
			
		    log.info("[" + c_slf_org_nm + "]���ܼ��Լ��������ڷ� �Ǽ� = [" + tot_size + "]");
			
			if (tot_size  >  0)   {
				
				/*���ڳ����ڷ� 1�Ǿ� fetch ó�� */
				for ( rec_cnt = 0;  rec_cnt < tot_size;  rec_cnt++)   {
					
					gblNidLevyRows =  alNidLevyList.get(rec_cnt);
					
					/*Ȥ�ó�...because of testing */
					if (gblNidLevyRows == null  ||  gblNidLevyRows.isEmpty() )   {
						nul_cnt++;
						continue;
					}

					if(cyberService.getOneFieldInteger("TXDM2413.SELECT_TX2112_CNT", gblNidLevyRows)>0)
					{
						if(cyberService.getOneFieldInteger("TXDM2413.SELECT_TX2211_CNT", gblNidLevyRows)==0){
							gblNidLevyRows.setMap("TAX_CNT",cyberService.getOneFieldInteger("TXDM2413.SELECT_TX2112_TAX_CNT", gblNidLevyRows));
							gblNidLevyRows.setMap("OCR_BD",cyberService.getOneFieldInteger("TXDM2413.SELECT_TX2112_OCR_BD", gblNidLevyRows));

							try{
								cyberService.queryForUpdate("TXDM2413.INSERT_TX2211", gblNidLevyRows);
								insert_cnt++;

							}catch (Exception sub_e){
								log.error("UPDATE_DEL_INFO_DETAIL ���������� = " + gblNidLevyRows.getMaps());
								sub_e.printStackTrace();
							}						
						}
					}

					try{
						/*�������� ������Ʈ*/
						remote_up_cnt +=govService.queryForUpdate("TXDM2413.UPDATE_SPNT300_TRN_YN", gblNidLevyRows);
					}catch (Exception sub_e){
						log.error("UPDATE_O_ORG_TBL ���������� = " + gblNidLevyRows.getMaps());
						sub_e.printStackTrace();
					}	
					
					if(rec_cnt % 500 == 0) {
						log.info("[H][" + this.c_slf_org_nm + "][" + this.c_slf_org + "]���ܼ��Լ����ڷ�ó���߰Ǽ� = [" + rec_cnt + "]");
					}
					
				}
				
				log.info("[" + c_slf_org_nm + "]���ܼ��Լ����ڷῬ�� �Ǽ� [" + rec_cnt + "] ��ϰǼ� [" + insert_cnt +"] �����Ǽ� [" + update_cnt + "] �����Ǽ� [" + delete_cnt + "] ���������� [" + p_del_cnt + "]");
				log.info("�������������Ʈ ["+remote_up_cnt+"], ��ó�� �ΰ��Ǽ� [" + nul_cnt + "]");
			}

			elapseTime2 = System.currentTimeMillis();
			
			log.info("[" + c_slf_org_nm + "]���ܼ��� �����ڷῬ�� �ð�("+tot_size+") : " + CbUtil.formatTime(elapseTime2 - elapseTime1));
					
		} catch (Exception e) {
		    log.error("SELECT_O_LEVY_LIST ���� ������ = " + gblNidLevyRows.getMaps());
			throw (RuntimeException) e;
		}
		
		return tot_size;
	}

}
