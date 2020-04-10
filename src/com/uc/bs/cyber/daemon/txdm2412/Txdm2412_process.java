/**
 *  �ֽý��۸� : �λ��û ���̹� ���漼û
 *  ��  ��  �� : ����
 *  ��  ��  �� : ǥ�ؼ��ܼ��� �����ڷ�����
 *  Ŭ����  ID : Txdm2412_process : �� ó�������� �ۼ��ϴ� Ŭ����
 *  ����  �̷� :
 * ------------------------------------------------------------------------
 *  �ۼ���      	�Ҽ�  		    ����            Tag           ����
 * ------------------------------------------------------------------------
 *  ǥ����       �ٻ�ý���      2013.12.13         %01%         �����ۼ�
 *  ǥ����       �ٻ�ý���      2015.05.29         %01%         ����
 */
package com.uc.bs.cyber.daemon.txdm2412;

import java.util.ArrayList;

import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;

import com.uc.bs.cyber.CbUtil;
import com.uc.bs.cyber.daemon.Codm_BaseProcess;
import com.uc.bs.cyber.daemon.Codm_interface;
import com.uc.core.MapForm;
import com.uc.core.spring.service.UcContextHolder;

public class Txdm2412_process extends Codm_BaseProcess implements Codm_interface {

	private MapForm dataMap            = null;
	private int insert_cnt = 0, update_cnt = 0, delete_cnt = 0, remote_up_cnt = 0, error_cnt = 0;
    private int p_del_cnt = 0;
	
	/*Ʈ������� ���Ͽ� */
	MapForm gblNidLevyRows    = null;
	
	/**
	 * 
	 */
	public Txdm2412_process() {
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
				
				int page = cyberService.getOneFieldInteger("TXDM2412.SELECT_TX2211_CNT", dataMap);
				
				log.info("[ǥ�ؼ��ܼ���-[" + c_slf_org_nm + "]] CNT = [" + page + "]");
				
				if(page == 0){
					
					/***********************************************************************************/
					try{
						MapForm daemonMap = new MapForm();
						daemonMap.setMap("DAEMON"    , "TXDM2412");
						daemonMap.setMap("DAEMON_NM" , "ǥ�ؼ��ܼ���(RISTNACOCR)��������");
						daemonMap.setMap("SGG_COD"   , c_slf_org);
						daemonMap.setMap("TOTAL_CNT" , page);
						daemonMap.setMap("INSERT_CNT", 0);
						daemonMap.setMap("UPDATE_CNT", 0);
						daemonMap.setMap("DELETE_CNT", 0);
						daemonMap.setMap("ERROR_CNT" , 0);
						cyberService.queryForInsert("CODMBASE.InsertDaemonProcessCheck", daemonMap);
					}catch(Exception ex){
						log.debug("ǥ�ؼ��ܼ��� �����ڷ����� ����� �����ϴ�. ��� ����");
					}				
					/***********************************************************************************/
					
					break;
				}

				
				dataMap.setMap("TRN_SNO", Long.parseLong(cyberService.getOneFieldString("TXDM2412.SELECT_TRN_SNO", dataMap)));
				if(cyberService.queryForUpdate("TXDM2412.UPDATE_TX2211_CNT", dataMap)==0) break;
				
				this.startJob();               /*��Ƽ Ʈ����� ȣ��*/
				
				if(cyberService.getOneFieldInteger("TXDM2412.SELECT_TX2211_CNT", dataMap) == 0) {
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
		txdm2412_JobProcess();
	
	}
	
	
    /*ǥ�ؼ��ܼ���...����*/
	private int txdm2412_JobProcess() {
		
		log.info("=====================================================================");
		log.info("=" + this.getClass().getName()+ " txdm2412_JobProcess()[ǥ�ؼ��ܼ���-[" + c_slf_org_nm + "] �����ڷ� ����] Start =");
		log.info("= govid["+this.govId+"], orgcd["+this.c_slf_org+"]");
		log.info("=====================================================================");
		
		/*�ʱ�ȭ*/
		
		insert_cnt = 0;
		update_cnt = 0; 
		delete_cnt = 0;
		error_cnt  = 0;
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
			ArrayList<MapForm> alNidLevyList =  cyberService.queryForList("TXDM2412.SELECT_TX2211_LIST", dataMap);
			
			tot_size = alNidLevyList.size();
			
		    log.info("[" + c_slf_org_nm + "]���ܼ��Լ����ڷ����� �Ǽ� = [" + tot_size + "]");
			
			if (tot_size  >  0)   {
				
				/*���ڳ����ڷ� 1�Ǿ� fetch ó�� */
				for ( rec_cnt = 0;  rec_cnt < tot_size;  rec_cnt++)   {
					
					gblNidLevyRows =  alNidLevyList.get(rec_cnt);
					
					if (gblNidLevyRows == null  ||  gblNidLevyRows.isEmpty() )   {
						nul_cnt++;
						continue;
					}
					if(govService.getOneFieldInteger("TXDM2412.SELECT_RISTNACOCR_CNT", gblNidLevyRows)>0){
//�׽�Ʈ��			if(cyberService.getOneFieldInteger("TXDM2412.SELECT_RISTNACOCR_CNT", gblNidLevyRows)>0){
						gblNidLevyRows.setMap("TRTG","5");
					}else{

						String PAY_YMD = (String) gblNidLevyRows.getMap("PAY_YMD");
						String CURR_DATE = (String) gblNidLevyRows.getMap("CURR_DATE");
						String SGG_COD = (String) gblNidLevyRows.getMap("SGG_COD");
						if(!PAY_YMD.equals(CURR_DATE)||SGG_COD.equals("340")){
							gblNidLevyRows.setMap("SNO",govService.getOneFieldString("TXDM2412.SELECT_RISTNACOCR_MAX_SNO", gblNidLevyRows));
						}
						
						try{
							govService.queryForUpdate("TXDM2412.INSERT_RISTNACOCR", gblNidLevyRows);
//�׽�Ʈ��					cyberService.queryForUpdate("TXDM2412.INSERT_RISTNACOCR", gblNidLevyRows);
							insert_cnt++;
							gblNidLevyRows.setMap("TRTG","1");
						}catch (Exception sub_e){
							log.error("INSERT_RISTNACOCR ���������� = " + gblNidLevyRows.getMaps());
							error_cnt++;
							sub_e.printStackTrace();
						}						
					}
					
					try{
						/*���� ������Ʈ*/
						remote_up_cnt +=cyberService.queryForUpdate("TXDM2412.UPDATE_TX2211_TRTG", gblNidLevyRows);
					}catch (Exception sub_e){
						log.error("UPDATE_O_ORG_TBL ���������� = " + gblNidLevyRows.getMaps());
						error_cnt++;
						sub_e.printStackTrace();
					}

					if(rec_cnt % 500 == 0) {
						log.info("[H][" + this.c_slf_org_nm + "][" + this.c_slf_org + "]���ܼ��� �����ڷ� ���۰Ǽ� = [" + rec_cnt + "]");
					}
				}
				log.info("[" + c_slf_org_nm + "]���ܼ��Լ����ڷ� ���� �Ǽ� [" + rec_cnt + "] ��ϰǼ� [" + insert_cnt +"] �����Ǽ� [" + update_cnt + "] �����Ǽ� [" + delete_cnt + "] ���������� [" + p_del_cnt + "]");
				log.info("���������Ʈ ["+remote_up_cnt+"], ��ó�� �ΰ��Ǽ� [" + nul_cnt + "]");
				
				/***********************************************************************************/
				try{
					MapForm daemonMap = new MapForm();
					daemonMap.setMap("DAEMON"    , "TXDM2412");
					daemonMap.setMap("DAEMON_NM" , "ǥ�ؼ��ܼ���(RISTNACOCR)��������");
					daemonMap.setMap("SGG_COD"   , c_slf_org);
					daemonMap.setMap("TOTAL_CNT" , rec_cnt);
					daemonMap.setMap("INSERT_CNT", insert_cnt);
					daemonMap.setMap("UPDATE_CNT", update_cnt);
					daemonMap.setMap("DELETE_CNT", delete_cnt);
					daemonMap.setMap("ERROR_CNT" , error_cnt);
					cyberService.queryForInsert("CODMBASE.InsertDaemonProcessCheck", daemonMap);
				}catch(Exception ex){
					log.debug("ǥ�ؼ��ܼ��� �����ڷ����� ����� �����ϴ�. ��� ����");
				}				
				/***********************************************************************************/
				
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
