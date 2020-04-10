/**
 *  �ֽý��۸� : �λ��û ���̹� ���漼û
 *  ��  ��  �� : 
 *  ��  ��  �� : ������
 *  Ŭ����  ID : Txbt5000.process
 *  ����  �̷� :
 * ------------------------------------------------------------------------
 *  �ۼ���      	�Ҽ�  		    ����            Tag           ����
 * ------------------------------------------------------------------------
 * �����        ��ä��       2012.04.27    %01%      �����ۼ�
 */


package com.uc.bs.cyber.batch.txbt5000;

import java.util.ArrayList;

import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.dao.DuplicateKeyException;

import com.uc.bs.cyber.daemon.Codm_BaseProcess;
import com.uc.bs.cyber.daemon.Codm_interface;
import com.uc.core.MapForm;
import com.uc.core.spring.service.UcContextHolder;

/**
 * @author Administrator
 *
 */
public class Txbt5000_process extends Codm_BaseProcess implements Codm_interface {

	private MapForm dataMap  = null;
	private int loop_cnt = 0, insert_cnt = 0, update_cnt = 0, del_cnt = 0;
	
	/**
	 * 
	 */
	public Txbt5000_process() {
		// TODO Auto-generated constructor stub
		super();
		
		log = LogFactory.getLog(this.getClass());
	}

	/*Context ���Կ�*/
	public void setApp(ApplicationContext context) {
		this.context = context;
	}

	/*���μ��� ����*/
	@Override
	public void runProcess() throws Exception {
		// TODO Auto-generated method stub
		
		/*Ʈ����� ����*/
		mainTransProcess();
	}

	/*Context ���Կ�*/
	@Override
	public void setDatasrc(String datasrc) {
		// TODO Auto-generated method stub
		this.dataSource = datasrc;
	}

	@Override
	public void transactionJob() {
		// TODO Auto-generated method stub
		txbt5000_JobProcess();
	}


	/*Ʈ������� �����ϱ� ���� �Լ�.*/
	private void mainTransProcess() {
		
		try {
			
			setContext(appContext);
			setApp(appContext);

			UcContextHolder.setCustomerType(this.dataSource);
			
			dataMap = new MapForm();  								// �ʱ�ȭ
			dataMap.setMap("SGG_COD",  this.c_slf_org);  	// ��û�ڵ� ����
			dataMap.setMap("TRN_YN", "0");            		   	// ������ ��

			this.startJob();
						
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}			
	}
	
	/*�ڷ� ����*/
	private int txbt5000_JobProcess() {
		
		/* Context ���� �� ����DB���� */
		this.context = appContext;
		UcContextHolder.setCustomerType(this.dataSource);
		
		log.info("=====================================================================");
		log.info("=" + this.getClass().getName()+ " txbt5000_JobProcess() [���漼 ȯ�ޱ� ������� ����ó�� LOG] Start =");
		log.info("= govid["+this.govId+"], orgcd["+this.c_slf_org+"], orgcd["+this.c_slf_org_nm+"]");
		log.info("=====================================================================");
		
		int LevyCnt = 0;
		
		loop_cnt = 0; 
		insert_cnt = 0;
		update_cnt = 0;
		del_cnt = 0;
		
		
		MapForm mpSupplyingList = new MapForm();
		
		try {
			
			
			ArrayList<MapForm> alFixedLevyList =  govService.queryForList("TXBT5000.select_supplying_list", dataMap);
			
			LevyCnt = alFixedLevyList.size();
			
			if (LevyCnt  >  0) {
				
				
				for (int i = 0;  i < LevyCnt;  i++) {
					
					mpSupplyingList =  alFixedLevyList.get(i);

					if (mpSupplyingList == null  ||  mpSupplyingList.isEmpty()) {
						continue;
					}
					
					loop_cnt++;

					/* ���� ó�� */
					if (mpSupplyingList.getMap("CHG_TYPE").equals("3")) {
						
						if (cyberService.queryForUpdate("TXBT5000.delete_tx4111_tb", mpSupplyingList) > 0) {
							
							del_cnt++;
							
						}
						
					} else {	
							
						try {
							cyberService.queryForInsert("TXBT5000.insert_tx4111_tb", mpSupplyingList);
								
								insert_cnt++;
								
							} catch (Exception e) {
								
								if (e instanceof DuplicateKeyException) {
									
									cyberService.queryForUpdate("TXBT5000.update_tx4111_tb", mpSupplyingList);
									
									update_cnt++;
									
								} else {
									
									log.info("�����߻������� = " + mpSupplyingList.getMaps());
									
									//e.printStackTrace();							
									//throw (RuntimeException) e;
									continue;
								}
							}
						}
						govService.queryForUpdate("TXBT5000.update_complete", mpSupplyingList);
						
				}
			}
				
		log.info("�ѰǼ�= [" + loop_cnt + "]");
		
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return LevyCnt;
	}

}
