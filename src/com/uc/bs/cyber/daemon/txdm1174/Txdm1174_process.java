/**
 *  �ֽý��۸� : �λ��û ���̹� ���漼û
 *  ��  ��  �� : ����
 *  ��  ��  �� : �����Է¿��������������� 
 *  Ŭ����  ID : txdm1174_process : �� ó�������� �ۼ��ϴ� Ŭ����
 *  ����  �̷� :
 * ------------------------------------------------------------------------
 *  �ۼ���      	�Ҽ�  		    ����            Tag           ����
 * ------------------------------------------------------------------------
 *  õ����       ��ä��(��)      2012.02.28         %01%         �����ۼ�
 */
package com.uc.bs.cyber.daemon.txdm1174;

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
public class Txdm1174_process extends Codm_BaseProcess implements Codm_interface {

	private MapForm dataMap            = null;
	private int insert_cnt = 0, update_cnt = 0, delete_cnt = 0, remote_up_cnt = 0;
    private int p_del_cnt = 0;
	
	/*Ʈ������� ���Ͽ� */
	MapForm gblNidLevyRows    = null;
	
	/**
	 * 
	 */
	public Txdm1174_process() {
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
		log.info("=[���� ���ý����Ҽ����� ����-��û �ڷῬ��] Start =");
		log.info("=====================================================================");
		
		/* * 
		 * */
		
		try {
				
			setContext(appContext);
			setApp(appContext);

			UcContextHolder.setCustomerType(this.dataSource);
						
			dataMap = new MapForm();  /*�ʱ�ȭ*/

			dataMap.setMap("PAGE_PER_CNT"   ,  500);            /*�������� ����*/
			
			do {
				
				int page = govService.getOneFieldInteger("txdm1174.txdm1174_select_count_page", dataMap);
				
				log.info("[�����Է¿�����������-��û] PAGE_PER_CNT(" + dataMap.getMap("PAGE_PER_CNT") + ") �� Page���� =" + page);
				
				if(page == 0) break;
				
				for(int i = 1 ; i <= page ; i ++) {
					
					dataMap.setMap("PAGE",  i);    /*ó��������*/
					this.startJob();               /*��Ƽ Ʈ����� ȣ��*/
				}
				
				break;
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
		txdm1174_JobProcess();
	
	}
	
	
    /*�����Է¿�������������...����(��û)*/
	private int txdm1174_JobProcess() {
		
		log.info("=====================================================================");
		log.info("=" + this.getClass().getName()+ " txdm1174_JobProcess()[���� ���ý����Ҽ����� ����-��û �ڷῬ��] Start =");
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
		/*1. �ΰ������ڷ� ���� ó��.                               */
		/*---------------------------------------------------------*/
		
		try {

			dataMap.setMap("SGG_COD",  this.c_slf_org);

			/*�������̺� �ΰ��ڷ� SELECT ����(TAX)*/
			ArrayList<MapForm> alNidLevyList =  govService.queryForList("txdm1174.txdm1174_select_list", dataMap);
			
			tot_size = alNidLevyList.size();
			
		    log.info("���� ���ý����Ҽ����� ���� �����ڷ� �Ǽ� = [" + tot_size + "]");
			
			if (tot_size  >  0)   {
				
				/*���ڳ����ڷ� 1�Ǿ� fetch ó�� */
				for ( rec_cnt = 0;  rec_cnt < tot_size;  rec_cnt++)   {

					gblNidLevyRows =  alNidLevyList.get(rec_cnt);
					
					/*Ȥ�ó�...because of testing */
					if (gblNidLevyRows == null  ||  gblNidLevyRows.isEmpty() )   {
						nul_cnt++;
						continue;
					}
					
					/*�����Ͱ� ������ �μ�Ʈ ������ ������Ʈ*/ 
					
					try{
						cyberService.queryForInsert("txdm1174.txdm1174_insert_tx1604_tb", gblNidLevyRows);
						insert_cnt++;

					 } catch (Exception e) {
						
							/*�ߺ��� �߻��ϰų� �������ǿ� ��߳��� ���üũ : sgg_cod , reg_no �� �ߺ�*/
							if (e instanceof DuplicateKeyException){	// �⺻Ű�� �ߺ��Ǽ� ����ó���Ȱ�� 
								
								try {
									cyberService.queryForInsert("txdm1174.txdm1174_update_tx1604_tb", gblNidLevyRows);
									update_cnt++;
									
								} catch (Exception be) {

									// TODO Auto-generated catch block
									be.printStackTrace();
								}
						}else{
							log.error("���������� = " + gblNidLevyRows.getMaps());
							log.info(e.getMessage());
							e.printStackTrace();							
							throw (RuntimeException) e;
						}

//						remote_up_cnt+=	cyberService.queryForUpdate("txdm1174.txdm3141_update_state1", gblNidLevyRows);
					
					 }
				}
			}
				
				log.info("�������ý����Ҽ����� �ڷῬ�� �Ǽ� [" + rec_cnt + "] ��ϰǼ� [" + insert_cnt +"] �����Ǽ� [" + update_cnt + "] �����Ǽ� [" + delete_cnt + "] ���������� [" + p_del_cnt + "]");
				log.info("���� ���ý����Ҽ����� ["+remote_up_cnt+"], ��ó�� �ΰ��Ǽ� [" + nul_cnt + "]");

			elapseTime2 = System.currentTimeMillis();
			
			log.info("���ý����Ҽ����� �ΰ��ڷῬ�� �ð�("+tot_size+") : " + CbUtil.formatTime(elapseTime2 - elapseTime1));
					
		} catch (Exception e) {
		    log.error("���� ������ = " + gblNidLevyRows.getMaps());
			throw (RuntimeException) e;
		}
		
		return tot_size;
	}
	

}
