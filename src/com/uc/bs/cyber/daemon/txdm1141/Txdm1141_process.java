/**
 *  �ֽý��۸� : �λ��û ���̹� ���漼û
 *  ��  ��  �� : ����
 *  ��  ��  �� : �������ڷ� �ڷῬ��
 *  Ŭ����  ID : txdm1141_process : �� ó�������� �ۼ��ϴ� Ŭ����
 *  ����  �̷� :
 * ------------------------------------------------------------------------
 *  �ۼ���      	�Ҽ�  		    ����            Tag           ����
 * ------------------------------------------------------------------------
 *  õ����       ��ä��(��)      2011.11.14         %01%         �����ۼ�
 */
package com.uc.bs.cyber.daemon.txdm1141;

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
public class Txdm1141_process extends Codm_BaseProcess implements Codm_interface {

	private MapForm dataMap            = null;
	private int insert_cnt = 0, update_cnt = 0, delete_cnt = 0, remote_up_cnt = 0, rcp_cnt = 0;
    private int p_del_cnt = 0;
	
	/*Ʈ������� ���Ͽ� (���漼 �������ڷ� ����) */
	MapForm gblNidLevyRows    = null;
	
	/*Ʈ������� ���Ͽ� (���漼 ����������� ����) */
	MapForm rcpListRow = null;
	
	/**
	 * 
	 */
	public Txdm1141_process() {
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
		log.info("=[������-��û �ڷῬ��-[" + c_slf_org_nm + "] �ڷ�۽�] Start =");
		log.info("=====================================================================");		
		
		/* * 
		 * */
		
		try {
				
			setContext(appContext);
			setApp(appContext);

			UcContextHolder.setCustomerType(this.dataSource);
						
			dataMap = new MapForm();  /*�ʱ�ȭ*/

			dataMap.setMap("TRN_YN",  "0");						//���ۻ��°� '0'�� ���� ��ȸ�Ѵ�.
			dataMap.setMap("PAGE_PER_CNT"   ,  500);            /*�������� ����*/
			
				
			log.info("����");
				
			int page = govService.getOneFieldInteger("txdm1141.txdm3141_select_count_page", dataMap);
				
			log.debug("[������(" + c_slf_org_nm + ")] PAGE_PER_CNT(" + dataMap.getMap("PAGE_PER_CNT") + ") �� Page���� =" + page);
				
				
			for(int i = 1 ; i <= page ; i ++) {
				log.debug(i);
				dataMap.setMap("JobGb", "1");
				dataMap.setMap("PAGE",  i);    /*ó��������*/
				this.startJob();               /*��Ƽ Ʈ����� ȣ��*/
			}
				
	
			
			/* ���漼����������� �߰� */
			
			log.info("=====================================================================");
			log.info("=[���漼�����������-[" + c_slf_org_nm + "] �ڷ�۽�] Start =");
			log.info("=====================================================================");
			
			dataMap = new MapForm();  /*�ʱ�ȭ*/
			
			log.info("this.c_slf_org ======================= [" + this.c_slf_org + "]");

			dataMap.setMap("SGG_COD",  this.c_slf_org);
			
			int rcpCnt = govService.getOneFieldInteger("TXDM1151.SELECT_COUNT", dataMap);
				
			log.info("[���漼�����������-[" + c_slf_org_nm + "]] �Ǽ�(" + rcpCnt + ")");
				
			if(rcpCnt == 0){
				log.info("[���漼������������� �����ϴ�.");
				
				/***********************************************************************************/
				try{
					MapForm daemonMap = new MapForm();
					daemonMap.setMap("DAEMON"    , "TXDM1141");
					daemonMap.setMap("DAEMON_NM" , "����(SCON540)��������");
					daemonMap.setMap("SGG_COD"   , c_slf_org);
					daemonMap.setMap("TOTAL_CNT" , rcpCnt);
					daemonMap.setMap("INSERT_CNT", 0);
					daemonMap.setMap("UPDATE_CNT", 0);
					daemonMap.setMap("DELETE_CNT", 0);
					daemonMap.setMap("ERROR_CNT" , 0);
					cyberService.queryForInsert("CODMBASE.InsertDaemonProcessCheck", daemonMap);
				}catch(Exception ex){
					log.debug("���漼 ����������� �Ǽ��� �����ϴ�. ��� ����");
				}				
				/***********************************************************************************/
				
			}else{
				dataMap.setMap("JobGb", "2");
				this.startJob();
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
		/*1. �ΰ������ڷ� ���� ó��.                               */
		/*---------------------------------------------------------*/
		/*���� ���� 1: ���漼 �������ڷ� ����  2:���漼 ����������� ����*/
		String jb = (String) dataMap.getMap("JobGb");
		
		txdm1141_JobProcess(jb);
	
	}
	
	
    /*������...����(��û)*/
	private int txdm1141_JobProcess(String jb) {
		
		int tot_size=0;
		
		if(jb.equals("1")){ /* 1. ���漼 �������ڷ� ���� */
			log.info("=====================================================================");
			log.info("=" + this.getClass().getName()+ " txdm1141_JobProcess()[������-��û �ڷῬ��] Start =");
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
			
			tot_size = 0;
			
			long elapseTime1 = 0;
			long elapseTime2 = 0;
			
			elapseTime1 = System.currentTimeMillis();
	
			/*---------------------------------------------------------*/
			/*1. �ΰ������ڷ� ���� ó��.                               */
			/*---------------------------------------------------------*/
			
			try {
	
				dataMap.setMap("SGG_COD",  this.c_slf_org);
				dataMap.setMap("TRN_YN",  "0");
	
				/*�������̺� �ΰ��ڷ� SELECT ����(TAX)*/
				ArrayList<MapForm> alNidLevyList =  govService.queryForList("txdm1141.txdm3141_select_list", dataMap);
				
				tot_size = alNidLevyList.size();
				
			    log.info("��û������ �����ڷ� �Ǽ� = [" + tot_size + "]");
				
				if (tot_size  >  0)   {
					
					/*���ڳ����ڷ� 1�Ǿ� fetch ó�� */
					for ( rec_cnt = 0;  rec_cnt < tot_size;  rec_cnt++)   {
	
						gblNidLevyRows =  alNidLevyList.get(rec_cnt);
						
						/*Ȥ�ó�...because of testing */
						if (gblNidLevyRows == null  ||  gblNidLevyRows.isEmpty() )   {
							nul_cnt++;
							continue;
						}
						
						try{
							/* ���̹��� �μ�Ʈ*/
							cyberService.queryForInsert("txdm1141.txdm1141_insert_tx1401_tb", gblNidLevyRows);
							insert_cnt++;
	
						 } catch (Exception e) {
							
							 /*�ߺ��� �߻��ϰų� �������ǿ� ��߳��� ���üũ*/
							 if (e instanceof DuplicateKeyException){	// �⺻Ű�� �ߺ��Ǽ� ����ó���Ȱ�� 
									
								 try {
									 /* ���̹��� ������Ʈ*/
									 cyberService.queryForUpdate("txdm1141.txdm1141_update_tx1401_tb", gblNidLevyRows);
									 update_cnt++;
										
								 } catch (Exception be) {
									 // TODO Auto-generated catch block
									 log.info("ERROR:txdm1141_update_tx1401_tb");
									 be.printStackTrace();
									throw (RuntimeException) be;
								 }
								 
							} else{
								log.info("ERROR:txdm1141_insert_tx1401_tb");
								log.error("���������� = " + gblNidLevyRows.getMaps());
								throw (RuntimeException) e;
							}
						
						 }
						 /* �Ϸ�Ǹ� TRN_YN 9�� ����1�� ����*/
						 gblNidLevyRows.setMap("TRN_YN"  , "1" );                
						 /*��û�� ������Ʈ*/
						 remote_up_cnt += govService.queryForUpdate("txdm1141.txdm3141_update_done", gblNidLevyRows);
					}
				}
					
				log.info("������ �ڷῬ�� �Ǽ� [" + rec_cnt + "] ��ϰǼ� [" + insert_cnt +"] �����Ǽ� [" + update_cnt + "] �����Ǽ� [" + delete_cnt + "] ���������� [" + p_del_cnt + "]");
				log.info("trn_yn ������Ʈ ["+remote_up_cnt+"], ��ó�� �ΰ��Ǽ� [" + nul_cnt + "]");
	
				elapseTime2 = System.currentTimeMillis();
				
				log.info("������ �����ڷ� ���� �ð�("+tot_size+") : " + CbUtil.formatTime(elapseTime2 - elapseTime1));
						
			} catch (Exception e) {
				e.printStackTrace();
			    log.error("���� ������ = " + gblNidLevyRows.getMaps());
				throw (RuntimeException) e;
			}
			
			
			
		}else if(jb.equals("2")){   /* 2. ���漼 ����������� ���� ���� */
			
			log.info("=====================================================================");
			log.info("=" + this.getClass().getName()+ " txdm1141_JobProcess()[���漼�����������-[" + c_slf_org_nm + "] �ڷ�����] Start =");
			log.info("= govid["+this.govId+"], orgcd["+this.c_slf_org+"]");
			log.info("=====================================================================");
			
			/*�ʱ�ȭ*/
			rcp_cnt = 0;
			remote_up_cnt = 0;
			delete_cnt = 0;
			update_cnt = 0;
		
			/*���� �ʱ�ȭ*/
			rcpListRow = new MapForm();

			int nul_cnt = 0;
			int cnt = 0;
			tot_size = 0;
			
			long elapseTime1 = 0;
			long elapseTime2 = 0;
			
			elapseTime1 = System.currentTimeMillis();

			/*---------------------------------------------------------*/
			/*1. ���漼����������� ���� ó��.                               */
			/*---------------------------------------------------------*/
			
			try {

				dataMap.setMap("SGG_COD",  this.c_slf_org);

				/* �ǽð� ����/��� SELECT ���� */
				ArrayList<MapForm> rcpList =  govService.queryForList("txdm1141.SELECT_LIST", dataMap);
				
				tot_size = rcpList.size();
				
			    log.info("[" + c_slf_org_nm + "]���漼����������� �Ǽ� = [" + tot_size + "]");
				
				if (tot_size  >  0)   {
					
					/* �ǽð� ����/��� �ڷḦ �ش� ��û�� fetch */
					for (cnt = 0;  cnt < tot_size;  cnt++) {

						rcpListRow =  rcpList.get(cnt);
						
						/*Ȥ�ó�...because of testing */
						if (rcpListRow == null  ||  rcpListRow.isEmpty()) {
							nul_cnt++;
							continue;
						}
						
						if(rcpListRow.getMap("CUD_OPT").equals("3")){
							/*
							try {
								
								rcpListRow.setMap("DEL_YN", "N");
								
								cyberService.queryForUpdate("TXDM1151.TXSV1151_UPDATE_TX1102_TB", rcpListRow);
								
							} catch (Exception e) {
								
								log.error("TX1102_TB ���� = " + rcpListRow.getMaps());
								log.error(e.getMessage());
								
							}
							*/
							
							try {

								cyberService.queryForUpdate("txdm1141.TXSV1151_DELETE_TX1211_TB", rcpListRow);
								
								delete_cnt++;
								
							} catch (Exception e) {
								
								log.error("���������� = " + rcpListRow.getMaps());
								e.printStackTrace();							
								throw (RuntimeException) e;
									
							}
							
						} else if(rcpListRow.getMap("CUD_OPT").equals("2")){
							
							int updateCnt = 0;
							/*
							try {
								
								rcpListRow.setMap("DEL_YN", "Y");
								
								cyberService.queryForUpdate("TXDM1151.TXSV1151_UPDATE_TX1102_TB", rcpListRow);
								
							} catch (Exception e) {
								
								log.error("TX1102_TB ���� = " + rcpListRow.getMaps());
								log.error(e.getMessage());
								
							}	
							*/					
							
							try {						
								
								updateCnt = cyberService.queryForUpdate("txdm1141.TXSV1151_UPDATE_TX1211_TB", rcpListRow);
								
								update_cnt++;
								
							} catch (Exception e) {
								
								log.error("���������� = " + rcpListRow.getMaps());
								e.printStackTrace();							
								throw (RuntimeException) e;
								
							}
							
							if(updateCnt == 0) {
								
								try {
									
									cyberService.queryForUpdate("txdm1141.TXSV1151_INSERT_TX1211_TB", rcpListRow);
									
									//update_cnt++;
									
								} catch (Exception e) {
									
									log.error("���������� = " + rcpListRow.getMaps());
									e.printStackTrace();							
									throw (RuntimeException) e;
									
								}
								
							}
							
						} else if(rcpListRow.getMap("CUD_OPT").equals("1")){
							
							/*
							try {
								
								rcpListRow.setMap("DEL_YN", "Y");
								
								cyberService.queryForUpdate("TXDM1151.TXSV1151_UPDATE_TX1102_TB", rcpListRow);
								
							} catch (Exception e) {
								
								log.error("TX1102_TB ���� = " + rcpListRow.getMaps());
								log.error(e.getMessage());
								
							}
							*/
							
							try {
								
								cyberService.queryForUpdate("txdm1141.TXSV1151_INSERT_TX1211_TB", rcpListRow);
								
								rcp_cnt++;
								
							} catch (Exception e) {
								
								/*�ߺ��� �߻��ϰų� �������ǿ� ��߳��� ���üũ*/
								if (e instanceof DuplicateKeyException){
									
									try {
										
										cyberService.queryForUpdate("txdm1141.TXSV1151_UPDATE_TX1211_TB", rcpListRow);
										    
									} catch (Exception sub_e) {
										log.error("���������� = " + rcpListRow.getMaps());
										e.printStackTrace();
										throw (RuntimeException) sub_e;
									}
									
								} else {
									
									log.error("���������� = " + rcpListRow.getMaps());
									e.printStackTrace();							
									throw (RuntimeException) e;
									
								}
								
							}
							
						}
						
						/*=========== UPDATE ===========*/
						try {
							
							/* ���漼����������� �Ϸ� ������Ʈ */
							remote_up_cnt += govService.queryForUpdate("txdm1141.TXSV3151_UPDATE_DONE", rcpListRow);
							
						} catch (Exception e) {
							
							log.error("���������� = " + rcpListRow.getMaps());
							e.printStackTrace();							
							throw (RuntimeException) e;
								
						}

					}
					
					log.info("[" + c_slf_org_nm + "]���漼����������� [" + cnt + "] �ű� [" + rcp_cnt +"] ���� [" + update_cnt + "] ���� [" + delete_cnt + "]");
					log.info("ó���Ǽ� ["+remote_up_cnt+"], ��ó�� �ΰ��Ǽ� [" + nul_cnt + "]");
					
					/***********************************************************************************/
					try{
						MapForm daemonMap = new MapForm();
						daemonMap.setMap("DAEMON"    , "TXDM1141");
						daemonMap.setMap("DAEMON_NM" , "����(SCON540)��������");
						daemonMap.setMap("SGG_COD"   , c_slf_org);
						daemonMap.setMap("TOTAL_CNT" , cnt);
						daemonMap.setMap("INSERT_CNT", rcp_cnt);
						daemonMap.setMap("UPDATE_CNT", update_cnt);
						daemonMap.setMap("DELETE_CNT", delete_cnt);
						daemonMap.setMap("ERROR_CNT" , 0);
						cyberService.queryForInsert("CODMBASE.InsertDaemonProcessCheck", daemonMap);
					}catch(Exception ex){
						log.debug("���漼 ����������� �α� ��� ����");
					}				
					/***********************************************************************************/
					
				}

				elapseTime2 = System.currentTimeMillis();
				
				log.info("[" + c_slf_org_nm + "]���漼����������� ���� �ð�("+tot_size+") : " + CbUtil.formatTime(elapseTime2 - elapseTime1));
						
			} catch (Exception e) {
				
			    log.error("���� ������ = " + rcpListRow.getMaps());
			    e.printStackTrace();
				throw (RuntimeException) e;
				
			}
			
			
		}
		
		return tot_size;
		
	}
	

}
