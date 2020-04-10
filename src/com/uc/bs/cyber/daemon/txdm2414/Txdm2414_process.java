/**
 *  �ֽý��۸� : �λ��û ���̹� ���漼û
 *  ��  ��  �� : ����
 *  ��  ��  �� : ǥ�ؼ��ܼ��� �����ڷ�����
 *  Ŭ����  ID : Txdm2414_process : �� ó�������� �ۼ��ϴ� Ŭ����
 *  ����  �̷� :
 * ------------------------------------------------------------------------
 *  �ۼ���      	�Ҽ�  		    ����            Tag           ����
 * ------------------------------------------------------------------------
 *  ǥ����       �ٻ�ý���      2013.12.13         %01%         �����ۼ�
 */
package com.uc.bs.cyber.daemon.txdm2414;

import java.util.ArrayList;

import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;

import com.uc.bs.cyber.CbUtil;
import com.uc.bs.cyber.daemon.Codm_BaseProcess;
import com.uc.bs.cyber.daemon.Codm_interface;
import com.uc.core.MapForm;
import com.uc.core.spring.service.UcContextHolder;

public class Txdm2414_process extends Codm_BaseProcess implements Codm_interface {

	private MapForm dataMap            = null;
	private int insert_cnt = 0, update_cnt = 0, delete_cnt = 0, remote_up_cnt = 0;
    private int p_del_cnt = 0;
	
	/*Ʈ������� ���Ͽ� */
	MapForm gblNidLevyRows    = null;
	
	/**
	 * 
	 */
	public Txdm2414_process() {
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
		log.info("=[ǥ�ؼ��ܼ���-[" + c_slf_org_nm + "] �����ڷ�����] Start =");
		log.info("=====================================================================");
		
		try {
				
			setContext(appContext);
			setApp(appContext);

			UcContextHolder.setCustomerType(this.dataSource);
						
			dataMap = new MapForm();  /*�ʱ�ȭ*/
			
			log.info("this.c_slf_org ======================= [" + this.c_slf_org + "]");
			dataMap.setMap("SGG_COD", this.c_slf_org);
			
			do {
				
				int page = cyberService.getOneFieldInteger("TXDM2414.SELECT_TX2211_CNT", dataMap);
				
				log.info("[ǥ�ؼ��ܼ���-[" + c_slf_org_nm + "]] CNT = [" + page + "]");
				
				if(page == 0) break;
				
				this.startJob();               /*��Ƽ Ʈ����� ȣ��*/
				
				if(cyberService.getOneFieldInteger("TXDM2414.SELECT_TX2211_CNT", dataMap) == 0) {
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
		/*1. �����ڷ� ���� ���� ó��.                               */
		/*---------------------------------------------------------*/
		txdm2414_JobProcess();
	
	}
	
	
    /*ǥ�ؼ��ܼ���...����*/
	private int txdm2414_JobProcess() {
		
		log.info("=====================================================================");
		log.info("=" + this.getClass().getName()+ " txdm2414_JobProcess()[ǥ�ؼ��ܼ���-[" + c_slf_org_nm + "] �����ڷ� ����] Start =");
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

		int rec_cnt = 0;
		int nul_cnt = 0;
		
		int tot_size = 0;
		
		long elapseTime1 = 0;
		long elapseTime2 = 0;
		
		elapseTime1 = System.currentTimeMillis();

		/*---------------------------------------------------------*/
		/*1. �����ڷ� ���� ���� ó��.                               */
		/*---------------------------------------------------------*/
		
		try {

			/*�������̺� �ΰ��ڷ� SELECT ����(NIS)*/
			ArrayList<MapForm> alNidLevyList =  cyberService.queryForList("TXDM2414.SELECT_TX2211_LIST", dataMap);
			
			tot_size = alNidLevyList.size();
			
		    log.info("[" + c_slf_org_nm + "]���ܼ��Լ����ڷ����� �Ǽ� = [" + tot_size + "]");
			
			if (tot_size  >  0)   {
				
				/*���ڳ����ڷ� 1�Ǿ� fetch ó�� */
				for ( rec_cnt = 0;  rec_cnt < tot_size;  rec_cnt++)   {
					
					gblNidLevyRows =  alNidLevyList.get(rec_cnt);
					
					/*Ȥ�ó�...because of testing */
					if (gblNidLevyRows == null  ||  gblNidLevyRows.isEmpty() )   {
						nul_cnt++;
						continue;
					}
					if(govService.getOneFieldInteger("TXDM2414.SELECT_RISTNACOCR_CNT", gblNidLevyRows)>0){
						gblNidLevyRows.setMap("TRTG","5");
					}else{
						try{
							govService.queryForUpdate("TXDM2414.INSERT_RISTNACOCR", gblNidLevyRows);
							insert_cnt++;
							gblNidLevyRows.setMap("TRTG","1");
						}catch (Exception sub_e){
							log.error("UPDATE_DEL_INFO_DETAIL ���������� = " + gblNidLevyRows.getMaps());
							sub_e.printStackTrace();
						}						
					}
					
					try{
						/*���� ������Ʈ*/
						remote_up_cnt +=cyberService.queryForUpdate("TXDM2414.UPDATE_TX2211_TRTG", gblNidLevyRows);
					}catch (Exception sub_e){
						log.error("UPDATE_O_ORG_TBL ���������� = " + gblNidLevyRows.getMaps());
						sub_e.printStackTrace();
					}
					
					if(rec_cnt % 500 == 0) {
						log.info("[H][" + this.c_slf_org_nm + "][" + this.c_slf_org + "]���ܼ��� �����ڷ� ���۰Ǽ� = [" + rec_cnt + "]");
					}
				}
				
				log.info("[" + c_slf_org_nm + "]���ܼ��Լ����ڷ� ���� �Ǽ� [" + rec_cnt + "] ��ϰǼ� [" + insert_cnt +"] �����Ǽ� [" + update_cnt + "] �����Ǽ� [" + delete_cnt + "] ���������� [" + p_del_cnt + "]");
				log.info("���������Ʈ ["+remote_up_cnt+"], ��ó�� �ΰ��Ǽ� [" + nul_cnt + "]");
			}

			elapseTime2 = System.currentTimeMillis();
			
			log.info("[" + c_slf_org_nm + "]���ܼ��� �����ڷ����� �ð�("+tot_size+") : " + CbUtil.formatTime(elapseTime2 - elapseTime1));
					
		} catch (Exception e) {
		    log.error("SELECT_O_LEVY_LIST ���� ������ = " + gblNidLevyRows.getMaps());
			throw (RuntimeException) e;
		}
		
		return tot_size;
	}
	
}
