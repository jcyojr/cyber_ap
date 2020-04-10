/**
 *  �ֽý��۸� : �λ��û ���̹� ���漼û
 *  ��  ��  �� : ����
 *  ��  ��  �� : �ǽð������ڷ�(����,���) ���ճ���
 *  Ŭ����  ID : Txdm1161_process : �� ó�������� �ۼ��ϴ� Ŭ����
 *  ����  �̷� :
 * ------------------------------------------------------------------------
 *  �ۼ���      	�Ҽ�  		    ����            Tag           ����
 * ------------------------------------------------------------------------
 *  Ȳ����       ��ä��(��)      2012.03.08         %01%         �����ۼ�
 */
package com.uc.bs.cyber.daemon.txdm1161;

import java.util.ArrayList;

import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;

import com.uc.bs.cyber.CbUtil;
import com.uc.bs.cyber.daemon.Codm_BaseProcess;
import com.uc.bs.cyber.daemon.Codm_interface;
import com.uc.core.MapForm;
import com.uc.core.spring.service.UcContextHolder;

public class Txdm1161_process extends Codm_BaseProcess implements Codm_interface {

	private MapForm dataMap = null;
	private int rcp_cnt = 0, rcp_cancel_cnt = 0, remote_up_cnt = 0;
	
	/*Ʈ������� ���Ͽ� */
	MapForm dataListRow = null;
	
	/**
	 * 
	 */
	public Txdm1161_process() {
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
		log.info("=[�ǽð��ڷ�(����,���) ���ճ���-[" + c_slf_org_nm + "] �ڷ�۽�] Start =");
		log.info("=====================================================================");
		try {
				
			setContext(appContext);
			setApp(appContext);

			UcContextHolder.setCustomerType(this.dataSource);
						
			dataMap = new MapForm();  /*�ʱ�ȭ*/
			
			log.info("this.c_slf_org ======================= [" + this.c_slf_org + "]");

			dataMap.setMap("SGG_COD",  this.c_slf_org);
			
			do {
				
				int rcpCnt = govService.getOneFieldInteger("TXDM1161.SELECT_COUNT", dataMap);
				
				log.info("[�ǽð��ڷ�(����,���) ���ճ���-[" + c_slf_org_nm + "]] �Ǽ�(" + rcpCnt + ")");
				
				if(rcpCnt == 0){
				    
					/***********************************************************************************/
					try{
						MapForm daemonMap = new MapForm();
						daemonMap.setMap("DAEMON"    , "TXDM1161");
						daemonMap.setMap("DAEMON_NM" , "����(SCON745)������ҿ���");
						daemonMap.setMap("SGG_COD"   , c_slf_org);
						daemonMap.setMap("TOTAL_CNT" , rcpCnt);
						daemonMap.setMap("INSERT_CNT", 0);
						daemonMap.setMap("UPDATE_CNT", 0);
						daemonMap.setMap("DELETE_CNT", 0);
						daemonMap.setMap("ERROR_CNT" , 0);
						cyberService.queryForInsert("CODMBASE.InsertDaemonProcessCheck", daemonMap);
					}catch(Exception ex){
						log.debug("���漼 ���ճ�����ҿ��� �Ǽ��� �����ϴ�. ��� ����");
					}				
					/***********************************************************************************/
					
					break;
				
				} else {
					this.startJob();
					break;
				}
				
				//if(govService.getOneFieldInteger("TXDM1161.SELECT_COUNT", dataMap) == 0) break;
				
			} while(true);
					
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
			
		txdm1161_JobProcess();
	
	}

	private int txdm1161_JobProcess() {
		
		log.info("=====================================================================");
		log.info("=" + this.getClass().getName()+ " txdm1161_JobProcess()[�ǽð��ڷ�(����,���) ���ճ���-[" + c_slf_org_nm + "] �ڷ�����] Start =");
		log.info("= govid["+this.govId+"], orgcd["+this.c_slf_org+"]");
		log.info("=====================================================================");
		
		/*�ʱ�ȭ*/
		rcp_cancel_cnt = 0;
		rcp_cnt = 0;
		remote_up_cnt = 0;
	
		
		/*���� �ʱ�ȭ*/
		dataListRow = new MapForm();

		int nul_cnt = 0;
		int cnt = 0;
		int tot_size = 0;
		
		long elapseTime1 = 0;
		long elapseTime2 = 0;
		
		elapseTime1 = System.currentTimeMillis();

		/*---------------------------------------------------------*/
		/*1. �ǽð����� ���ճ��� ���� ó��.                        */
		/*---------------------------------------------------------*/
		
		try {

			dataMap.setMap("SGG_COD",  this.c_slf_org);

			/* �ǽð� ����/��� SELECT ���� */
			ArrayList<MapForm> AccList =  govService.queryForList("TXDM1161.SELECT_LIST", dataMap);
			
			tot_size = AccList.size();
			
		    log.info("[" + c_slf_org_nm + "]�ǽð��ڷ�(����,���) ���ճ��� �ڷ� �Ǽ� = [" + tot_size + "]");
			
			if (tot_size  >  0)   {
				
				/* �ǽð� ����/��� �ڷḦ �ش� ��û�� fetch */
				for (cnt = 0;  cnt < tot_size;  cnt++) {

					dataListRow =  AccList.get(cnt);
					
					/*Ȥ�ó�...because of testing */
					if (dataListRow == null  ||  dataListRow.isEmpty() )   {
						nul_cnt++;
						continue;
					}
					
					if(dataListRow.getMap("GBN").equals("1")) {
						
						MapForm rcpMap = new MapForm();
					
						rcpMap = dataListRow;
						
			            rcpMap.setMap("SGG_COD", rcpMap.getMap("TAX_NO").toString().substring(2, 3 + 2));
			            rcpMap.setMap("ACCT_COD", rcpMap.getMap("TAX_NO").toString().substring(6, 2 + 6));
			            rcpMap.setMap("TAX_ITEM", rcpMap.getMap("TAX_NO").toString().substring(8, 6 + 8));
			            rcpMap.setMap("TAX_YY", rcpMap.getMap("TAX_NO").toString().substring(14, 4 + 14));
			            rcpMap.setMap("TAX_MM", rcpMap.getMap("TAX_NO").toString().substring(18, 2 + 18));
			            rcpMap.setMap("TAX_DIV", rcpMap.getMap("TAX_NO").toString().substring(20, 1 + 20));
			            rcpMap.setMap("HACD", rcpMap.getMap("TAX_NO").toString().substring(21, 3 + 21));
			            rcpMap.setMap("TAX_SNO", rcpMap.getMap("TAX_NO").toString().substring(24, 6 + 24));
			            
						try {
							
							ArrayList<MapForm> WeList;
							
							if(dataMap.getMap("SGG_COD").equals("000")) WeList = govService.queryForList("TXDM1161.TXSV3161_SELECT_ALL_000", dataListRow);
							else WeList = govService.queryForList("TXDM1161.TXSV3161_SELECT_ALL", dataListRow); 
							
							rcpMap.setMap("RCP_CNT", 0);
							rcpMap.setMap("OCRBD", " ");
							
							if(WeList.size() == 0) {
								
								/* �ΰ����̺� SCON602, SCON743�� �ڷ������ '8' �� ������Ʈ�ϰ� ó������ */
								try {
									
									rcpMap.setMap("TRN_YN", "8");
									
									govService.queryForUpdate("TXDM1161.TXSV3161_UPDATE_DONE", rcpMap);
									
								} catch (Exception e) {
									
									log.error("���������� = " + dataListRow.getMaps());
									e.printStackTrace();
									continue;
										
								}	
								
								continue;
								
							} else {
							
								/* �г������̶�, ��� �������� */
								for(int t_rcp_cnt = 0; t_rcp_cnt < WeList.size(); t_rcp_cnt++){
									
									rcpMap.setMap("RCP_CNT", WeList.get(t_rcp_cnt).getMap("RCP_CNT"));
									rcpMap.setMap("OCRBD", WeList.get(t_rcp_cnt).getMap("OCRBD"));
									
									/*
									log.info("RCP_CNT = [" + rcpMap.getMap("RCP_CNT")+ "]");
									log.info("RCP_CNT = [" + WeList.get(t_rcp_cnt).getMap("RCP_CNT") + "]");
									log.info("OCRBD = [" + rcpMap.getMap("OCRBD")+ "]");
									log.info("OCRBD = [" + WeList.get(t_rcp_cnt).getMap("OCRBD") + "]");
									*/
								}
							}
							
						} catch (Exception e) {
							
							log.error("���������� = " + dataListRow.getMaps());
							e.printStackTrace();
							throw (RuntimeException) e;
								
						}	
						
						/* ==== ���� ó�� ���� === */
						/* ����ҵ漼 Ư��¡�� ���̺� ���� */
						if(rcpMap.getMap("TAX_ITEM").equals("140004")) {
							
							try {
								
								cyberService.queryForUpdate("TXDM1161.INSERT_140004_RECEIPT", rcpMap);
								
							} catch (Exception e) {
								
								log.error("���������� = " + rcpMap.getMaps());
								e.printStackTrace();							
								throw (RuntimeException) e;
									
							}
							
						} else if(rcpMap.getMap("TAX_ITEM").equals("140001")) {
							/* ����ҵ漼 ���ռҵ� ���̺� ���� */
							try {
								
								cyberService.queryForUpdate("TXDM1161.INSERT_140001_RECEIPT", rcpMap);
								
							} catch (Exception e) {
								
								log.error("���������� = " + rcpMap.getMaps());
								e.printStackTrace();							
								throw (RuntimeException) e;
									
							}
							
						} else if(rcpMap.getMap("TAX_ITEM").equals("140002")) {
							/* ����ҵ漼 �絵�ҵ� ���̺� ���� */
							try {
								
								cyberService.queryForUpdate("TXDM1161.INSERT_140002_RECEIPT", rcpMap);
								
							} catch (Exception e) {
								
								log.error("���������� = " + rcpMap.getMaps());
								e.printStackTrace();							
								throw (RuntimeException) e;
									
							}	
							
						} else if(rcpMap.getMap("TAX_ITEM").equals("140003")) {
							/* ����ҵ漼 ���μ��� ���̺� ���� */
							try {

                                //2015�� ���� �����Ű�ȭ������ �Ű� ���Ѵٸ�, ���� ���̺�� ������Ʈ �� �ʿ�� ���� ��..
                                cyberService.queryForUpdate("TXDM1161.INSERT_140003_RECEIPT", rcpMap);
                                
                                //2015.03.24 �ű����̺� ���� ����
                                int corpTaxRecpUpdCnt = cyberService.queryForUpdate("TXDM1161.INSERT_140003_RECEIPT_NEW", rcpMap);
                                if (corpTaxRecpUpdCnt > 0) {
                                    log.info("[���μ��Ű�������] EPAY_NO = [" + rcpMap.getMap("EPAY_NO")+ "]");
                                }
								
							} catch (Exception e) {
								
								log.error("���������� = " + rcpMap.getMaps());
								e.printStackTrace();							
								throw (RuntimeException) e;
									
							}
							
						} else if(rcpMap.getMap("TAX_ITEM").equals("140011")) {
							/* ����ҵ漼 �������� ���̺� ���� */
							try {
								
								cyberService.queryForUpdate("TXDM1161.INSERT_140011_RECEIPT", rcpMap);
								
							} catch (Exception e) {
								
								log.error("���������� = " + rcpMap.getMaps());
								e.printStackTrace();							
								throw (RuntimeException) e;
									
							}
							
						} else if(rcpMap.getMap("TAX_ITEM").equals("104009")) {
							/* ����ҵ漼 ���� ���̺� ���� */
							try {
								
								cyberService.queryForUpdate("TXDM1161.INSERT_104009_RECEIPT", rcpMap);
								
							} catch (Exception e) {
								
								log.error("���������� = " + rcpMap.getMaps());
								e.printStackTrace();							
								throw (RuntimeException) e;
									
							}
							
						} else if(rcpMap.getMap("TAX_ITEM").equals("114002")) {
							/* ����ҵ漼 ��ϸ��㼼 ��Ϻ� ���̺� ���� */
							try {
								
								cyberService.queryForUpdate("TXDM1161.INSERT_114002_RECEIPT", rcpMap);
								
							} catch (Exception e) {
								
								log.error("���������� = " + rcpMap.getMaps());
								e.printStackTrace();							
								throw (RuntimeException) e;
									
							}
							
						} else if(rcpMap.getMap("TAX_ITEM").equals("109000")) {
							/* ����ҵ漼 ������ ���̺� ���� */
							try {
								
								cyberService.queryForUpdate("TXDM1161.INSERT_109000_RECEIPT", rcpMap);
								
							} catch (Exception e) {
								
								log.error("���������� = " + rcpMap.getMaps());
								e.printStackTrace();							
								throw (RuntimeException) e;
									
							}
							
						} else if(rcpMap.getMap("TAX_ITEM").equals("135001")) {
							/* �������߼� Ư�� ���̺� ���� */
							try {
								
								cyberService.queryForUpdate("TXDM1161.INSERT_135001_RECEIPT", rcpMap);
								
							} catch (Exception e) {
								
								log.error("���������� = " + rcpMap.getMaps());
								e.printStackTrace();							
								throw (RuntimeException) e;
									
							}
							
						}
						
						try {
							
							cyberService.queryForUpdate("TXDM1161.TX1201_TB_INSERT_RECEIPT", rcpMap);
							
							rcp_cnt++;
							
						} catch (Exception e) {
							
							e.printStackTrace();
							
							try {
								
								rcpMap.setMap("TRN_YN", "1");
								
								govService.queryForUpdate("TXDM1161.TXSV3161_UPDATE_DONE", rcpMap);
								
								nul_cnt++;
								
							} catch (Exception e_sub) {
								
								log.error("���������� = " + rcpMap.getMaps());
								e_sub.printStackTrace();							
								throw (RuntimeException) e_sub;
									
							}
							
							continue;
							//throw (RuntimeException) e;
								
						}
						
						try {
							
							cyberService.queryForUpdate("TXDM1161.TX1102_TB_UPDATE_STATE", rcpMap);
							
						} catch (Exception e) {
							
							log.error("���������� = " + rcpMap.getMaps());
							e.printStackTrace();							
							throw (RuntimeException) e;
								
						}	
						
						try {
							
							rcpMap.setMap("TRN_YN", "1");
							
							govService.queryForUpdate("TXDM1161.TXSV3161_UPDATE_DONE", rcpMap);
							
						} catch (Exception e) {
							
							log.error("���������� = " + rcpMap.getMaps());
							e.printStackTrace();							
							throw (RuntimeException) e;
								
						}
						
					} else {
						
						/* ��� �κ��ε� ���ϳ�?? */
						
					}

				}
				
				log.info("[" + c_slf_org_nm + "]�ǽð��ڷ�(����,���) ���ճ��� �Ǽ� [" + cnt + "] �ǽð����� [" + rcp_cnt +"] ��ҰǼ� [" + rcp_cancel_cnt + "]");
				log.info("ó���Ǽ� ["+remote_up_cnt+"], ��ó�� �ΰ��Ǽ� [" + nul_cnt + "]");
				
				/***********************************************************************************/
				try{
					MapForm daemonMap = new MapForm();
					daemonMap.setMap("DAEMON"    , "TXDM1161");
					daemonMap.setMap("DAEMON_NM" , "����(SCON745)������ҿ���");
					daemonMap.setMap("SGG_COD"   , c_slf_org);
					daemonMap.setMap("TOTAL_CNT" , cnt);
					daemonMap.setMap("INSERT_CNT", rcp_cnt);
					daemonMap.setMap("UPDATE_CNT", rcp_cancel_cnt);
					daemonMap.setMap("DELETE_CNT", 0);
					daemonMap.setMap("ERROR_CNT" , 0);
					cyberService.queryForInsert("CODMBASE.InsertDaemonProcessCheck", daemonMap);
				}catch(Exception ex){
					log.debug("���漼 ���ճ�����ҿ��� ��� ����");
				}				
				/***********************************************************************************/
				
			}

			elapseTime2 = System.currentTimeMillis();
			
			log.info("[" + c_slf_org_nm + "]�ǽð��ڷ�(����,���) ���ճ��� ���� �ð�("+tot_size+") : " + CbUtil.formatTime(elapseTime2 - elapseTime1));
					
		} catch (Exception e) {
			
		    log.error("���� ������ = " + dataListRow.getMaps());
			throw (RuntimeException) e;
			
		}
		
		return tot_size;
	}
	
}
