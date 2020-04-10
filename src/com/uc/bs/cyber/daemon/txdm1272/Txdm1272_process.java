/**
 *  �ֽý��۸� : �λ��û ���̹� ���漼û
 *  ��  ��  �� : ����
 *  ��  ��  �� : ��������� �ֹμ� ���ռҵ��� ������������ (��û��) ó�� ����
 *  Ŭ����  ID : Txdm1272_process : �� ó�������� �ۼ��ϴ� Ŭ����
 *  ����  �̷� :
 * ------------------------------------------------------------------------
 *  �ۼ���      	�Ҽ�  		    ����            Tag           ����
 * ------------------------------------------------------------------------
 *  �۵���       ��ä��(��)      2011.10.17         %01%         �����ۼ�
 */
package com.uc.bs.cyber.daemon.txdm1272;

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
 *
 */
public class Txdm1272_process extends Codm_BaseProcess implements Codm_interface {

	private MapForm dataMap  = null;
	private int loop_cnt = 0, insert_cnt = 0, update_cnt = 0;
	
	/**
	 * 
	 */
	public Txdm1272_process() {
		// TODO Auto-generated constructor stub
		super();
		
		log = LogFactory.getLog(this.getClass());
		
		loopTerm = 300;    /*5�д�����...*/
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
		
		/*��������� �������� �Է� �� ��û�� ���*/
		txdm1272_JobProcess();
	}


	/*Ʈ������� �����ϱ� ���� �Լ�.*/
	private void mainTransProcess(){

		setContext(appContext);
		setApp(appContext);

		UcContextHolder.setCustomerType(this.dataSource);
		
		try {
						
			dataMap = new MapForm();  /*�ʱ�ȭ*/

			dataMap.setMap("PAGE_PER_CNT"   ,  1000);           /*�������� ����*/
			dataMap.setMap("rowcnt"         ,  10000);
			dataMap.setMap("sido_cod"       ,  "26");
			dataMap.setMap("sgg_cod"        ,  c_slf_org);
			
			do {
				
				int page = cyberService.getOneFieldInteger("TXDM1272.tx1612_tb_select_page_cnt", dataMap);
				
				log.info("[�ֹμ� ���ռҵ���] PAGE_PER_CNT(" + dataMap.getMap("PAGE_PER_CNT") + ") �� Page���� =" + page);
				
				if(page == 0) break;
				
				for(int i = 1 ; i <= page ; i ++) {
					
					dataMap.setMap("PAGE",  i);    /*ó��������*/
					this.startJob();               /*��Ƽ Ʈ����� ȣ��*/
				}
				
				if(cyberService.getOneFieldInteger("TXDM1272.tx1612_tb_select_page_cnt", dataMap) == 0) {
					break;
				}
				
			}while(true);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}			
		
	}
	
	/*�Ľ��� �ڷḦ ���̹�DB�� �Է�*/
	private void txdm1272_JobProcess() {
		
		log.info("=====================================================================");
		log.info("=" + this.getClass().getName()+ " txdm1272_JobProcess() [��������� �ֹμ� ���ռҵ��� ������������] Start =");
		log.info("= govid["+this.govId+"], orgcd["+this.c_slf_org+"], orgcd["+this.c_slf_org_nm+"]");
		log.info("=====================================================================");
				
		long elapseTime1 = 0;
		long elapseTime2 = 0;
		
		int LevyCnt = 0;
		int rec_cnt = 0;
		
		loop_cnt = 0; 
		insert_cnt = 0;
		update_cnt = 0;
		
		MapForm mp_tx1613_tb_List = new MapForm();
		
		try {
		
			dataMap.setMap("sgg_cod"  ,  this.c_slf_org);
			dataMap.setMap("sido_cod" ,  "26");
			dataMap.setMap("tr_tg"    ,  "0");
			
			
			elapseTime1 = System.currentTimeMillis();
			
			/*�������̺� �ΰ��ڷ� SELECT ����(NIS)*/
			ArrayList<MapForm> alSpc_tx1613_List =  cyberService.queryForList("TXDM1272.tx1613_tb_select_list", dataMap);
			
			LevyCnt = alSpc_tx1613_List.size();

			if (LevyCnt  >  0)   {
				
				for ( rec_cnt = 0;  rec_cnt < LevyCnt;  rec_cnt++)   {
					
					mp_tx1613_tb_List = alSpc_tx1613_List.get(rec_cnt);
					
					/*Ȥ�ó�...because of testing */
					if (mp_tx1613_tb_List == null  ||  mp_tx1613_tb_List.isEmpty() )   {
						continue;
					}
					
					try {

						if(govService.getOneFieldInteger("TXDM1272.scyb503_select_cnt", mp_tx1613_tb_List) == 0 ) {
						
							mp_tx1613_tb_List.setMap("REG_YY",     mp_tx1613_tb_List.getMap("PAY_DT").toString().substring(0, 4));
							mp_tx1613_tb_List.setMap("SND_IP",     "127.0.0.1");
							mp_tx1613_tb_List.setMap("TRN_YN",     "0");
							mp_tx1613_tb_List.setMap("RPT_REG_NO", "");
							mp_tx1613_tb_List.setMap("RPT_NM",     "");
							mp_tx1613_tb_List.setMap("RPT_TEL",    "");
							mp_tx1613_tb_List.setMap("RPT_ID",     "");
							
							govService.queryForUpdate("TXDM1272.scyb503_insert_receipt", mp_tx1613_tb_List);
							
							insert_cnt++;
							
						} else {
							update_cnt++;
						}
						
					}catch (Exception e){
						
						/*�ߺ��� �߻��ϰų� �������ǿ� ��߳��� ���üũ*/
						if (e instanceof DuplicateKeyException){

						}else{						
							loop_cnt++;
							throw (RuntimeException) e;
						}
						
					}
					
				}
				
			}
			
			elapseTime2 = System.currentTimeMillis();
			
			log.info("�ֹμ� ���ռҵ��� ������������(" + c_slf_org_nm + ") �ڷῬ�� �Ǽ�::" + rec_cnt + ", ��ϰǼ�::" + insert_cnt +", ���ϰǼ�::" + update_cnt);
			log.info("�ֹμ� ���ռҵ��� ������������(" + c_slf_org_nm + ") �ڷῬ�� �ð�::" + CbUtil.formatTime(elapseTime2 - elapseTime1));
			
		}catch (Exception e){
			e.printStackTrace();
			throw (RuntimeException) e;
		}
				
	}

}
