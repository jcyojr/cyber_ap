/**
 *  �ֽý��۸� : �λ��û ���̹� ���漼û
 *  ��  ��  �� : ����
 *  ��  ��  �� : ���漼�����������
 *  Ŭ����  ID : Txdm1151_process : �� ó�������� �ۼ��ϴ� Ŭ����
 *  ����  �̷� :
 * ------------------------------------------------------------------------
 *  �ۼ���      	�Ҽ�  		    ����            Tag           ����
 * ------------------------------------------------------------------------
 *  Ȳ����       ��ä��(��)      2012.03.08         %01%         �����ۼ�
 */
package com.uc.bs.cyber.daemon.txdm1151;

import java.util.ArrayList;

import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.dao.DuplicateKeyException;

import com.uc.bs.cyber.CbUtil;
import com.uc.bs.cyber.daemon.Codm_BaseProcess;
import com.uc.bs.cyber.daemon.Codm_interface;
import com.uc.core.MapForm;
import com.uc.core.spring.service.UcContextHolder;

public class Txdm1151_process extends Codm_BaseProcess implements Codm_interface {

	private MapForm dataMap = null;
	private int rcp_cnt = 0, remote_up_cnt = 0, delete_cnt = 0, update_cnt = 0;
	
	/*Ʈ������� ���Ͽ� */
	MapForm rcpListRow = null;
	
	/**
	 * 
	 */
	public Txdm1151_process() {
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
		log.info("=[���漼�����������-[" + c_slf_org_nm + "] �ڷ�۽�] Start =");
		log.info("=====================================================================");
		try {
				
			setContext(appContext);
			setApp(appContext);

			UcContextHolder.setCustomerType(this.dataSource);
						
			dataMap = new MapForm();  /*�ʱ�ȭ*/
			
			log.info("this.c_slf_org ======================= [" + this.c_slf_org + "]");

			dataMap.setMap("SGG_COD",  this.c_slf_org);
			
			do {
				
				int rcpCnt = govService.getOneFieldInteger("TXDM1151.SELECT_COUNT", dataMap);
				
				log.info("[���漼�����������-[" + c_slf_org_nm + "]] �Ǽ�(" + rcpCnt + ")");
				
				if(rcpCnt == 0){
					break;
				}else{
					this.startJob();
				}
				break;
				//if(govService.getOneFieldInteger("TXDM1151.SELECT_COUNT", dataMap) == 0) break;
				
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
			
		txdm1151_JobProcess();
	
	}

	private int txdm1151_JobProcess() {
		
		log.info("=====================================================================");
		log.info("=" + this.getClass().getName()+ " txdm1151_JobProcess()[���漼�����������-[" + c_slf_org_nm + "] �ڷ�����] Start =");
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
		int tot_size = 0;
		
		long elapseTime1 = 0;
		long elapseTime2 = 0;
		
		elapseTime1 = System.currentTimeMillis();

		/*---------------------------------------------------------*/
		/*1. ���漼����������� ���� ó��.                               */
		/*---------------------------------------------------------*/
		
		try {

			dataMap.setMap("SGG_COD",  this.c_slf_org);

			/* �ǽð� ����/��� SELECT ���� */
			ArrayList<MapForm> rcpList =  govService.queryForList("TXDM1151.SELECT_LIST", dataMap);
			
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

							cyberService.queryForUpdate("TXDM1151.TXSV1151_DELETE_TX1211_TB", rcpListRow);
							
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
							
							updateCnt = cyberService.queryForUpdate("TXDM1151.TXSV1151_UPDATE_TX1211_TB", rcpListRow);
							
							update_cnt++;
							
						} catch (Exception e) {
							
							log.error("���������� = " + rcpListRow.getMaps());
							e.printStackTrace();							
							throw (RuntimeException) e;
							
						}
						
						if(updateCnt == 0) {
							
							try {
								
								cyberService.queryForUpdate("TXDM1151.TXSV1151_INSERT_TX1211_TB", rcpListRow);
								
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
							
							cyberService.queryForUpdate("TXDM1151.TXSV1151_INSERT_TX1211_TB", rcpListRow);
							
							rcp_cnt++;
							
						} catch (Exception e) {
							
							/*�ߺ��� �߻��ϰų� �������ǿ� ��߳��� ���üũ*/
							if (e instanceof DuplicateKeyException){
								
								try {
									
									cyberService.queryForUpdate("TXDM1151.TXSV1151_UPDATE_TX1211_TB", rcpListRow);
									    
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
						remote_up_cnt += govService.queryForUpdate("TXDM1151.TXSV3151_UPDATE_DONE", rcpListRow);
						
					} catch (Exception e) {
						
						log.error("���������� = " + rcpListRow.getMaps());
						e.printStackTrace();							
						throw (RuntimeException) e;
							
					}

				}
				
				log.info("[" + c_slf_org_nm + "]���漼����������� [" + cnt + "] �ű� [" + rcp_cnt +"] ���� [" + update_cnt + "] ���� [" + delete_cnt + "]");
				log.info("ó���Ǽ� ["+remote_up_cnt+"], ��ó�� �ΰ��Ǽ� [" + nul_cnt + "]");
			}

			elapseTime2 = System.currentTimeMillis();
			
			log.info("[" + c_slf_org_nm + "]���漼����������� ���� �ð�("+tot_size+") : " + CbUtil.formatTime(elapseTime2 - elapseTime1));
					
		} catch (Exception e) {
			
		    log.error("���� ������ = " + rcpListRow.getMaps());
		    e.printStackTrace();
			throw (RuntimeException) e;
			
		}
		
		return tot_size;
	}
	
}
