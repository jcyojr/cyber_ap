/**
 *  �ֽý��۸� : �λ��û ���̹� ���漼û
 *  ��  ��  �� : ����
 *  ��  ��  �� : ǥ�ؼ��ܼ��� �������ڷῬ��
 *  Ŭ����  ID : Txdm2415_process : �� ó�������� �ۼ��ϴ� Ŭ����
 *  ����  �̷� :
 * ------------------------------------------------------------------------
 *  �ۼ���      	�Ҽ�  		    ����            Tag           ����
 * ------------------------------------------------------------------------
 *  ǥ����       �ٻ�ý���             2014.04.10         %01%         �����ۼ�
 */
package com.uc.bs.cyber.daemon.txdm2415;

import java.util.ArrayList;

import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;

import com.uc.bs.cyber.CbUtil;
import com.uc.bs.cyber.daemon.Codm_BaseProcess;
import com.uc.bs.cyber.daemon.Codm_interface;
import com.uc.core.MapForm;
import com.uc.core.spring.service.UcContextHolder;

public class Txdm2415_process extends Codm_BaseProcess implements Codm_interface {

	private MapForm dataMap            = null;
	private int insert_cnt = 0, update_cnt = 0, delete_cnt = 0, remote_up_cnt = 0;
    private int p_del_cnt = 0;
	
	/*Ʈ������� ���Ͽ� */
	MapForm gblNidLevyRows    = null;
	
	/**
	 * 
	 */
	public Txdm2415_process() {
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
		log.info("=[ǥ�ؼ��ܼ���-[" + c_slf_org_nm + "] �������ڷῬ��] Start =");
		log.info("=====================================================================");
		
		try {
				
			setContext(appContext);
			setApp(appContext);

			UcContextHolder.setCustomerType(this.dataSource);
						
			dataMap = new MapForm();  /*�ʱ�ȭ*/
			
			log.info("this.c_slf_org ======================= [" + this.c_slf_org + "]");

			long SEQ = 0L;
			try{
				SEQ = Long.parseLong(govService.getOneFieldString("TXDM2415.SELECT_SPNT410_SEQ", null));
			}catch (Exception sub_e){
				log.error("SELECT_SPNT410_SEQ ����");
				sub_e.printStackTrace();
			}						

			if(SEQ>0L){
				try{
					govService.queryForUpdate("TXDM2415.UPDATE_SPNT410_START", SEQ);
				}catch (Exception sub_e){
					log.error("UPDATE_SPNT410_START ����");
					sub_e.printStackTrace();
				}						

				this.startJob();

				try{
					govService.queryForUpdate("TXDM2415.UPDATE_SPNT410_END", null);
				}catch (Exception sub_e){
					log.error("UPDATE_SPNT410_END ����");
					sub_e.printStackTrace();
				}						
			
			}else{
				
				/***********************************************************************************/
				try{
					MapForm daemonMap = new MapForm();
					daemonMap.setMap("DAEMON"    , "TXDM2415");
					daemonMap.setMap("DAEMON_NM" , "ǥ�ؼ��ܼ���(SPNT410)�������ڷῬ��");
					daemonMap.setMap("SGG_COD"   , c_slf_org);
					daemonMap.setMap("TOTAL_CNT" , 0);
					daemonMap.setMap("INSERT_CNT", 0);
					daemonMap.setMap("UPDATE_CNT", 0);
					daemonMap.setMap("DELETE_CNT", 0);
					daemonMap.setMap("ERROR_CNT" , 0);
					cyberService.queryForInsert("CODMBASE.InsertDaemonProcessCheck", daemonMap);
				}catch(Exception ex){
					log.debug("ǥ�ؼ��ܼ��� �������ڷῬ�� ����� �����ϴ�. ��� ����");
				}				
				/***********************************************************************************/
				
			}

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
		/*1. �����������ڷ� ���� ó��.                               */
		/*---------------------------------------------------------*/
		txdm2415_JobProcess();
	
	}
	
	
    /*ǥ�ؼ��ܼ���...����������*/
	private int txdm2415_JobProcess() {
		
		log.info("=====================================================================");
		log.info("=" + this.getClass().getName()+ " txdm2415_JobProcess()[ǥ�ؼ��ܼ���-[" + c_slf_org_nm + "] �������ڷῬ��] Start =");
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
		/*1. �����������ڷ� ���� ó��.                               */
		/*---------------------------------------------------------*/
		log.info("------try�� ���� �� -----------");
		try {

			/*�������̺� �������ڷ� SELECT ����(NIS)*/
			ArrayList<MapForm> alNidLevyList = null;

			try{
				alNidLevyList =  govService.queryForList("TXDM2415.SELECT_SPNT410_LIST", dataMap);
			}catch (Exception sub_e){
				log.error("SELECT_SPNT410_LIST ���������� = " + gblNidLevyRows.getMaps());
				sub_e.printStackTrace();
			}						

			tot_size = alNidLevyList.size();
			
		    log.info("[" + c_slf_org_nm + "]���ܼ��԰����������ڷ� �Ǽ� = [" + tot_size + "]");
			
			if (tot_size  >  0)   {
				
				/*���ڳ����ڷ� 1�Ǿ� fetch ó�� */
				for ( rec_cnt = 0;  rec_cnt < tot_size;  rec_cnt++)   {
					
					gblNidLevyRows =  alNidLevyList.get(rec_cnt);
					
					/*Ȥ�ó�...because of testing */
					if (gblNidLevyRows == null  ||  gblNidLevyRows.isEmpty() ){
						nul_cnt++;
						continue;
					}

				    String TRN_YN = "1";
				    
				    String TRNN_ID = (String) gblNidLevyRows.getMap("TRNN_ID");
				    String DEP_COD = (String) gblNidLevyRows.getMap("DEP_COD");
				    if(!TRNN_ID.substring(0,3).equals(DEP_COD.substring(0,3)))TRN_YN = "X";
				    
					if(TRN_YN.equals("1")){
						
						// �⿬���ڷ�
						int TX2401_CNT = cyberService.getOneFieldInteger("TXDM2415.SELECT_TX2401_CNT", gblNidLevyRows);

						if(TX2401_CNT>0){
							try{
								cyberService.queryForUpdate("TXDM2415.UPDATE_TX2401", gblNidLevyRows);
								update_cnt++;
							}catch (Exception sub_e){
								TRN_YN="E";
								log.error("UPDATE_TX2401 ���� = " + gblNidLevyRows.getMaps());
								sub_e.printStackTrace();
							}
						}else{
							try{
								cyberService.queryForUpdate("TXDM2415.INSERT_TX2401", gblNidLevyRows);
								insert_cnt++;
							}catch (Exception sub_e){
								TRN_YN="E";
								log.error("INSERT_TX2401 ���� = " + gblNidLevyRows.getMaps());
								sub_e.printStackTrace();
							}						
						}
					}
					gblNidLevyRows.setMap("TRN_YN",TRN_YN);
					
					try{
						/*�������� ������Ʈ*/
						remote_up_cnt +=govService.queryForUpdate("TXDM2415.UPDATE_SPNT410_TRN_YN", gblNidLevyRows);
					}catch (Exception sub_e){
						log.error("UPDATE_SPNT410_TRN_YN ���� = " + gblNidLevyRows.getMaps());
						sub_e.printStackTrace();
					}	
					
					if(rec_cnt % 500 == 0) {
						log.info("[" + this.c_slf_org_nm + "][" + this.c_slf_org + "]���ܼ��԰������ڷ�ó���߰Ǽ� = [" + rec_cnt + "]");
					}
				}
				
				log.info("[" + c_slf_org_nm + "]���ܼ��԰������ڷῬ�� �Ǽ� [" + rec_cnt + "] ��ϰǼ� [" + insert_cnt +"] �����Ǽ� [" + update_cnt + "] �����Ǽ� [" + delete_cnt + "] ���������� [" + p_del_cnt + "]");
				log.info("�������������Ʈ ["+remote_up_cnt+"], ��ó�� �ΰ��Ǽ� [" + nul_cnt + "]");
				
				/***********************************************************************************/
				try{
					MapForm daemonMap = new MapForm();
					daemonMap.setMap("DAEMON"    , "TXDM2415");
					daemonMap.setMap("DAEMON_NM" , "ǥ�ؼ��ܼ���(SPNT410)�������ڷῬ��");
					daemonMap.setMap("SGG_COD"   , c_slf_org);
					daemonMap.setMap("TOTAL_CNT" , rec_cnt);
					daemonMap.setMap("INSERT_CNT", insert_cnt);
					daemonMap.setMap("UPDATE_CNT", update_cnt);
					daemonMap.setMap("DELETE_CNT", delete_cnt);
					daemonMap.setMap("ERROR_CNT" , 0);
					cyberService.queryForInsert("CODMBASE.InsertDaemonProcessCheck", daemonMap);
				}catch(Exception ex){
					log.debug("ǥ�ؼ��ܼ��� �������ڷῬ�� �α� ��� ����");
				}				
				/***********************************************************************************/
				
			}

			elapseTime2 = System.currentTimeMillis();
			
			log.info("[" + c_slf_org_nm + "]���ܼ��� �������ڷῬ�� �ð�("+tot_size+") : " + CbUtil.formatTime(elapseTime2 - elapseTime1));
					
		} catch (Exception e) {
		    log.error("TXDM2415 ���� ���� = " + gblNidLevyRows.getMaps());
			throw (RuntimeException) e;
		}
		
		return tot_size;
	}
}
