/**
 *  �ֽý��۸� : �λ��û ���̹� ���漼û
 *  ��  ��  �� : ����
 *  ��  ��  �� : �������ä���ڷ� ��û�� ����
 *  Ŭ����  ID : Txdm1121_process : �� ó�������� �ۼ��ϴ� Ŭ����
 *  ����  �̷� :
 * ------------------------------------------------------------------------
 *  �ۼ���      	�Ҽ�  		    ����            Tag           ����
 * ------------------------------------------------------------------------
 *  Ȳ����       ��ä��(��)      2012.03.08         %01%         �����ۼ�
 */
package com.uc.bs.cyber.daemon.txdm1121;

import java.util.ArrayList;

import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.dao.DuplicateKeyException;

import com.uc.bs.cyber.CbUtil;
import com.uc.bs.cyber.daemon.Codm_BaseProcess;
import com.uc.bs.cyber.daemon.Codm_interface;
import com.uc.core.MapForm;
import com.uc.core.spring.service.UcContextHolder;

public class Txdm1121_process extends Codm_BaseProcess implements Codm_interface {

	private MapForm dataMap = null;
	private int insert_cnt = 0, update_cnt = 0, remote_up_cnt = 0;
	
	/*Ʈ������� ���Ͽ� */
	MapForm virListRow = null;
	
	/**
	 * 
	 */
	public Txdm1121_process() {
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
		log.info("=[�������ä��-[" + c_slf_org_nm + "] �ڷ�۽�] Start =");
		log.info("=====================================================================");
		try {
				
			setContext(appContext);
			setApp(appContext);

			UcContextHolder.setCustomerType(this.dataSource);
						
			dataMap = new MapForm();  /*�ʱ�ȭ*/
			
			log.info("this.c_slf_org ======================= [" + this.c_slf_org + "]");

			dataMap.setMap("SGG_COD",  this.c_slf_org);
			//dataMap.setMap("PAGE_PER_CNT"   ,  500);           /*�������� ����*/
            if(dataMap.getMap("SGG_COD").equals("000")){
            	int virCnt = cyberService.getOneFieldInteger("TXDM1121.SELECT_VIR_COUNT_SIDO", dataMap);
    			log.info("[�������ä��-[" + c_slf_org_nm + "]] �Ǽ�(" + virCnt + ")");
    			if(virCnt > 0){
    				this.startJob();
    			}else{
    				
    				/***********************************************************************************/
    				try{
    					MapForm daemonMap = new MapForm();
    					daemonMap.setMap("DAEMON"    , "TXDM1121");
    					daemonMap.setMap("DAEMON_NM" , "�������ä��(SCON604)����");
    					daemonMap.setMap("SGG_COD"   , c_slf_org);
    					daemonMap.setMap("TOTAL_CNT" , virCnt);
    					daemonMap.setMap("INSERT_CNT", 0);
    					daemonMap.setMap("UPDATE_CNT", 0);
    					daemonMap.setMap("DELETE_CNT", 0);
    					daemonMap.setMap("ERROR_CNT" , 0);
    					cyberService.queryForInsert("CODMBASE.InsertDaemonProcessCheck", daemonMap);
    				}catch(Exception ex){
    					log.debug("���漼 �������ä������ ����� �����ϴ�. ��� ����");
    				}				
    				/***********************************************************************************/
    				
    			}
            }else{
            	int virCnt = cyberService.getOneFieldInteger("TXDM1121.SELECT_VIR_COUNT", dataMap);
    			log.info("[�������ä��-[" + c_slf_org_nm + "]] �Ǽ�(" + virCnt + ")");
    			if(virCnt > 0){
    				this.startJob();
    			}else{
    				
    				/***********************************************************************************/
    				try{
    					MapForm daemonMap = new MapForm();
    					daemonMap.setMap("DAEMON"    , "TXDM1121");
    					daemonMap.setMap("DAEMON_NM" , "�������ä��(SCON604)����");
    					daemonMap.setMap("SGG_COD"   , c_slf_org);
    					daemonMap.setMap("TOTAL_CNT" , virCnt);
    					daemonMap.setMap("INSERT_CNT", 0);
    					daemonMap.setMap("UPDATE_CNT", 0);
    					daemonMap.setMap("DELETE_CNT", 0);
    					daemonMap.setMap("ERROR_CNT" , 0);
    					cyberService.queryForInsert("CODMBASE.InsertDaemonProcessCheck", daemonMap);
    				}catch(Exception ex){
    					log.debug("���漼 �������ä������ ����� �����ϴ�. ��� ����");
    				}				
    				/***********************************************************************************/
    				
    			}
            }
            
            
            
			//int virCnt = cyberService.getOneFieldInteger("TXDM1121.SELECT_VIR_COUNT", dataMap);
			//log.info("[�������ä��-[" + c_slf_org_nm + "]] �Ǽ�(" + virCnt + ")");
			//if(virCnt > 0) this.startJob();
					
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
		/*1. �ڷῬ�� ����ó��		                               */
		/*---------------------------------------------------------*/
		txdm1121_JobProcess();
	
	}

	private int txdm1121_JobProcess() {
		
		log.info("=====================================================================");
		log.info("=" + this.getClass().getName()+ " txdm1121_JobProcess()[�������ä��-[" + c_slf_org_nm + "] �ڷ�����] Start =");
		log.info("= govid["+this.govId+"], orgcd["+this.c_slf_org+"]");
		log.info("=====================================================================");
		
		/*�ʱ�ȭ*/
		insert_cnt = 0;
		update_cnt = 0; 
		remote_up_cnt = 0;
	
		/*���� �ʱ�ȭ*/
		virListRow = new MapForm();

		int nul_cnt = 0;
		int cnt = 0;
		int tot_size = 0;
		
		long elapseTime1 = 0;
		long elapseTime2 = 0;
		
		elapseTime1 = System.currentTimeMillis();

		/*---------------------------------------------------------*/
		/*1. �ΰ������ڷ� ���� ó��.                               */
		/*---------------------------------------------------------*/
		
		try {

			dataMap.setMap("SGG_COD",  this.c_slf_org);

			/*�������̺� �ΰ��ڷ� SELECT ����(NIS)*/
			ArrayList<MapForm> virAccList = new ArrayList<MapForm>();
			if(dataMap.getMap("SGG_COD").equals("000")){
				virAccList =  cyberService.queryForList("TXDM1121.SELECT_VIR_LIST_SIDO", dataMap);
			}else{
				virAccList =  cyberService.queryForList("TXDM1121.SELECT_VIR_LIST", dataMap);
			}
			
			//ArrayList<MapForm> virAccList =  cyberService.queryForList("TXDM1121.SELECT_VIR_LIST", dataMap);
			
			tot_size = virAccList.size();
			
		    log.info("[" + c_slf_org_nm + "]�������ä���ڷ� �Ǽ� = [" + tot_size + "]");
			
			if (tot_size  >  0)   {
				  log.info(" (((((( �۾��ϴ� ��û )))))) == >"+dataMap.getMap("SGG_COD"));
				/* �������ä���ڷḦ �ش� ��û�� fetch */
				for (cnt = 0;  cnt < tot_size;  cnt++)   {

					virListRow =  virAccList.get(cnt);
					
					/*Ȥ�ó�...because of testing */
					if (virListRow == null  ||  virListRow.isEmpty() )   {
						nul_cnt++;
						continue;
					}
					
					/*�⺻ Default �� ���� */
					virListRow.setMap("SGG_TR_TG"   , "1" );                       /*��û����ó������*/
					
					/* �õ������� ��û������ scon604 ���̺� �÷��� �޶� �б� */
					if(dataMap.getMap("SGG_COD").equals("000") || virListRow.getMap("ACCT_COD").equals("39")){
						
						/*=========== INSERT ===========*/
						try {
							if(govService.getOneFieldInteger("TXDM1121.SELECT_SEND_VIR_COUNT", virListRow)>0){
								govService.queryForUpdate("TXDM1121.VIRACC_UPDATE_SIDO", virListRow);
								update_cnt++;

							}else{
								govService.queryForUpdate("TXDM1121.VIRACC_INSERT_SIDO", virListRow);
								insert_cnt++;
							}
						} catch (Exception e) {
							log.info(e.getMessage());
							log.error("���������� = " + virListRow.getMaps());
							log.error(e.getMessage());
							e.printStackTrace();							
							throw (RuntimeException) e;
						}						
						
					} else if(dataMap.getMap("SGG_COD").equals("710")){    /* ���屺 �߰�*/
						
						/*=========== INSERT ===========*/
						try {
							
							MapForm virAccMapForm_604 = new MapForm();
							
							ArrayList<MapForm> virAccList_604_test = govService.queryForList("TXDM1121.SELECT_VIR_LIST_604", virListRow);
							
							if(virAccList_604_test.size()>0){
								virAccMapForm_604 = (MapForm)virAccList_604_test.get(0);
							}else{	
								continue;
							}
							
							
							if(virAccMapForm_604.getMap("VIR_ACC_2")== null){ //������� ���� ��	
							
									try{
										govService.queryForUpdate("TXDM1121.VIRACC2_UPDATE_FIRST", virListRow);
										update_cnt++;
										    
									} catch (Exception sub_e) {
										log.error("����������(VIRACC2_UPDATE_FIRST) = " + virListRow.getMaps());									
											sub_e.printStackTrace();
											throw (RuntimeException) sub_e;
									}							

							
							}else{ //������°� ���� ��(������¸� ���� ������Ʈ �Ѱ� �ƴ� ��) 
	
									try{
										govService.queryForUpdate("TXDM1121.VIRACC2_UPDATE_NOT_FIRST", virListRow);
										update_cnt++;
										    
									} catch (Exception sub_e) {
										log.error("����������(VIRACC2_UPDATE_NOT_FIRST) = " + virListRow.getMaps());									
											sub_e.printStackTrace();
											throw (RuntimeException) sub_e;
									}	
							}
							
						} catch (Exception e) {
							log.error("���������� = " + virListRow.getMaps());
							log.error(e.getMessage());
							e.printStackTrace();							
							throw (RuntimeException) e;
						}
						
					} else {
					
						/*=========== INSERT ===========*/
						try {
							if(govService.getOneFieldInteger("TXDM1121.SELECT_SEND_VIR_COUNT", virListRow)>0){
								govService.queryForUpdate("TXDM1121.VIRACC_UPDATE", virListRow);
								update_cnt++;
							}else{
								govService.queryForUpdate("TXDM1121.VIRACC_INSERT", virListRow);
								insert_cnt++;
							}
						} catch (Exception e) {
							log.error("���������� = " + virListRow.getMaps());
							log.error(e.getMessage());
							e.printStackTrace();							
							throw (RuntimeException) e;
						}
					}
					
					try{
						
						/* ���漼 �����̺� ������� ���� �Ϸ� ������Ʈ */
						remote_up_cnt += cyberService.queryForUpdate("TXDM1121.UPDATE_COMPLETE", virListRow);
						    
					} catch (Exception e) {
						
						log.error("���������� = " + virListRow.getMaps());
						log.error(e.getMessage());
						e.printStackTrace();
						throw (RuntimeException) e;
						
					}

				}
				
				log.info("[" + c_slf_org_nm + "]�������ä�� �Ǽ� [" + cnt + "] ��ϰǼ� [" + insert_cnt +"] �����Ǽ� [" + update_cnt + "]");
				log.info("���漼�����̺������Ʈ ["+remote_up_cnt+"], ��ó�� �ΰ��Ǽ� [" + nul_cnt + "]");
				
				/***********************************************************************************/
				try{
					MapForm daemonMap = new MapForm();
					daemonMap.setMap("DAEMON"    , "TXDM1121");
					daemonMap.setMap("DAEMON_NM" , "�������ä��(SCON604)����");
					daemonMap.setMap("SGG_COD"   , c_slf_org);
					daemonMap.setMap("TOTAL_CNT" , cnt);
					daemonMap.setMap("INSERT_CNT", insert_cnt);
					daemonMap.setMap("UPDATE_CNT", update_cnt);
					daemonMap.setMap("DELETE_CNT", 0);
					daemonMap.setMap("ERROR_CNT" , 0);
					cyberService.queryForInsert("CODMBASE.InsertDaemonProcessCheck", daemonMap);
				}catch(Exception ex){
					log.debug("���漼 �������ä������ ����� �����ϴ�. ��� ����");
				}				
				/***********************************************************************************/
				
			}

			elapseTime2 = System.currentTimeMillis();
			
			log.info("[" + c_slf_org_nm + "]�������ä�� ���� �ð�("+tot_size+") : " + CbUtil.formatTime(elapseTime2 - elapseTime1));
					
		} catch (Exception e) {
		    log.error("���� ������ = " + virListRow.getMaps());
		    log.error(e.getMessage());
			throw (RuntimeException) e;
		}
		
		return tot_size;
	}
}
