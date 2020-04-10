/**
 *  �ֽý��۸� : �λ��û ���̹� ���漼û
 *  ��  ��  �� : ����
 *  ��  ��  �� : ���漼 ����� �ΰ���ǥ ����(��û��) ����
 *  Ŭ����  ID : Txdm3110_process : �� ó�������� �ۼ��ϴ� Ŭ����
 *  ����  �̷� :
 * ------------------------------------------------------------------------
 *  �ۼ���      	�Ҽ�  		    ����            Tag           ����
 * ------------------------------------------------------------------------
 *  �۵���       ��ä��(��)      2011.10.12         %01%         �����ۼ�
 */
package com.uc.bs.cyber.daemon.txdm1250;

import java.math.BigDecimal;
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
public class Txdm1250_process extends Codm_BaseProcess implements Codm_interface {

	private MapForm dataMap  = null;
	private int loop_cnt = 0, insert_cnt = 0, update_cnt = 0, del_cnt = 0, vir_del_cnt = 0;
	
	/**
	 * 
	 */
	public Txdm1250_process() {
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
		txdm1250_JobProcess();
	}


	/*Ʈ������� �����ϱ� ���� �Լ�.*/
	private void mainTransProcess(){
		
		
		try {
			
			setContext(appContext);
			setApp(appContext);

			UcContextHolder.setCustomerType(this.dataSource);
			
			dataMap = new MapForm();  /*�ʱ�ȭ*/
			dataMap.setMap("PAGE_PER_CNT" , 1000);            /*�������� ����*/
			dataMap.setMap("SGG_COD"      , this.c_slf_org);  /*��û�ڵ� ����*/
			dataMap.setMap("trn_yn"       , '0');             /*������ ��*/
			
			do {
				
				int page = govService.getOneFieldInteger("TXDM1250.select_count_page", dataMap);
				
				log.info("[���漼 ����� �ΰ��ڷ� (" + c_slf_org_nm + ")] PAGE_PER_CNT(" + dataMap.getMap("PAGE_PER_CNT") + ") �� Page���� =" + page);
				
				if(page == 0) break;

				for(int i = 1 ; i <= page ; i ++) {

					dataMap.setMap("PAGE",  i);    /*ó��������*/
					this.startJob();               /*��Ƽ Ʈ����� ȣ��*/
				}
								
				if(govService.getOneFieldInteger("TXDM1250.select_count_page", dataMap) == 0) {
					break;
				}
				
			}while(true);
						
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}			
		
		
	}
	
	/*�ڷ� ����*/
	private int txdm1250_JobProcess() {
		
		log.info("=====================================================================");
		log.info("=" + this.getClass().getName()+ " txdm1250_JobProcess() [���漼 ����� �����ڷῬ��] Start =");
		log.info("= govid["+this.govId+"], orgcd["+this.c_slf_org+"], orgcd["+this.c_slf_org_nm+"]");
		log.info("=====================================================================");
		
		long queryElapse1 = 0;
		long queryElapse2 = 0;
		
		long elapseTime1 = 0;
		long elapseTime2 = 0;
		
		int LevyCnt = 0;
		
		loop_cnt = 0; 
		insert_cnt = 0;
		update_cnt = 0;
		del_cnt = 0;
		vir_del_cnt = 0;
		
		MapForm mpTaxFixLevyList = new MapForm();
		
		try {
			
			queryElapse1 = System.currentTimeMillis();
			
			/*���漼 ����п� �ΰ������� �����´�.*/
			ArrayList<MapForm> alFixedLevyList =  govService.queryForList("TXDM1250.select_fixed_list", dataMap);
			
			queryElapse2 = System.currentTimeMillis();
			
			LevyCnt = alFixedLevyList.size();
			
			log.info("[" + c_slf_org_nm + "]���漼����� ������ȸ �ð� : " + CbUtil.formatTime(queryElapse2 - queryElapse1) + " (" + LevyCnt + ")");
			
			if (LevyCnt  >  0)   {

				String notice_cls = "";
				String due_f_dt   = "";
				
				elapseTime1 = System.currentTimeMillis();
				
				for (int i = 0;  i < LevyCnt;  i++)   {
					
					mpTaxFixLevyList =  alFixedLevyList.get(i);
					
					if (mpTaxFixLevyList == null  ||  mpTaxFixLevyList.isEmpty() )   {
						continue;
					}
					
				}
				
				log.info("[" + c_slf_org_nm + "]���漼����� �����ڷ� ����ð� : " + CbUtil.formatTime(elapseTime2 - elapseTime1));
				log.info("���漼����� �����ڷῬ�� ó���Ǽ�::" + loop_cnt + ", �ΰ�ó��::" + ((insert_cnt > 0) ? insert_cnt / 2 : insert_cnt) 
					   + ", ������Ʈ::" + ((update_cnt > 0) ? update_cnt / 2 : update_cnt) + ", ����ó��::" + del_cnt + ", ���尡����� ���� ::" + vir_del_cnt);
			}
			
		}catch (Exception e){
			e.printStackTrace();
		}
		
		return LevyCnt;
		
	}
	

}
