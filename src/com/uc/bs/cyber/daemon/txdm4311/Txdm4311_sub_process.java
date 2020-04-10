/**
 *  �ֽý��۸� : �λ��û ���̹� ���漼û
 *  ��  ��  �� : ����
 *  ��  ��  �� : ���ϼ��� ����� �ΰ���ǥ ����(�õ�)�� ��ó���ϴ� Ŭ����
 *               ���� ���������� ���踦 ó���ϴ� Ŭ������ 
 *               ���ϼ��� �����κ��� ����� �����͸� ���̹���û ������ INSERT�Ѵ�.
 *  Ŭ����  ID : Txdm4311_sub_process 
 *  ����  �̷� :
 * ------------------------------------------------------------------------
 *  �ۼ���      	�Ҽ�  		    ����            Tag           ����
 * ------------------------------------------------------------------------
 *  �۵���       ��ä��(��)      2011.10.12         %01%         �����ۼ�
 */
package com.uc.bs.cyber.daemon.txdm4311;

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
public class Txdm4311_sub_process extends Codm_BaseProcess implements Codm_interface {

	private MapForm dataMap  = null;
	private int loop_cnt = 0, insert_cnt = 0, update_cnt = 0, del_cnt = 0;
	private int seq = 0;
	/**
	 * 
	 */
	public Txdm4311_sub_process() {
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

	/*ó�������Դϴ�...*/
	public void setSeq(int seq) {
		this.seq = seq;
	}
	
	@Override
	public void transactionJob() {
		// TODO Auto-generated method stub
		txdm4311_JobProcess();
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
			dataMap.setMap("trn_yn"       , '0');             /*������ ��    */
			dataMap.setMap("PAGE"         , seq);             /*ó��������*/
			
			this.startJob();                                  /*��Ƽ Ʈ����� ȣ��*/
						
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}			
		
	}
	
	/*�ڷ� ����*/
	private int txdm4311_JobProcess() {
		
		log.info("=====================================================================");
		log.info("=" + this.getClass().getName()+ " txdm4311_JobProcess() [���ϼ��� ����� �����ڷῬ��] Start = PAGE[" + seq + "]");
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
		
		MapForm mpTaxFixSudoList = new MapForm();
		
		try {
			
			queryElapse1 = System.currentTimeMillis();
			
			/*���漼 ����п� �ΰ������� �����´�.*/
			ArrayList<MapForm> alFixedSudoList =  govService.queryForList("TXDM4311.sudo_select_fixed_list", dataMap);
			
			queryElapse2 = System.currentTimeMillis();
			
			LevyCnt = alFixedSudoList.size();
			
			log.info("[" + c_slf_org_nm + "]���ϼ��� ����� ������ȸ �ð� : " + CbUtil.formatTime(queryElapse2 - queryElapse1) + " (" + LevyCnt + ")");
			
			if (LevyCnt  >  0)   {

				
				elapseTime1 = System.currentTimeMillis();
				
				String state_flag = "";
				
				for (int i = 0;  i < LevyCnt;  i++)   {
					
					mpTaxFixSudoList =  alFixedSudoList.get(i);
					
					if (mpTaxFixSudoList == null  ||  mpTaxFixSudoList.isEmpty() )   {
						continue;
					}
					
					state_flag = (String) mpTaxFixSudoList.getMap("state_flag");  /*�����ļ�������*/
					
					loop_cnt++;

					try {
						
						/*1.�����ڷ� ���� ó��*/						
						if (state_flag.equals("D")) {

							/*flag ó��*/
							if (cyberService.queryForUpdate("TXDM4311.sudo_delete_levy", mpTaxFixSudoList) > 0) {
								del_cnt++;
							}
							
						} else {
							/*�ΰ��ڷ� �Է� ����*/
							try {
								
								cyberService.queryForInsert("TXDM4311.sudo_insert_levy", mpTaxFixSudoList);
								insert_cnt++;
								
							}catch (Exception e){
								
								if (e instanceof DuplicateKeyException){
									cyberService.queryForUpdate("TXDM4311.sudo_update_levy", mpTaxFixSudoList);	
									update_cnt++;
								} else {
									
									log.info("�����߻������� = " + mpTaxFixSudoList.getMaps());
									e.printStackTrace();							
									throw (RuntimeException) e;
								}
							}
							
						}
						
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
				
				elapseTime2 = System.currentTimeMillis();
				
				log.info("[" + c_slf_org_nm + "]���ϼ��� ����� �����ڷ� ����ð� : " + CbUtil.formatTime(elapseTime2 - elapseTime1));
				log.info("���ϼ��� ����� �����ڷῬ�� ó���Ǽ�::" + loop_cnt + ", �ΰ�ó��::" + insert_cnt
					   + ", ������Ʈ::" + update_cnt + ", ����ó��::" + del_cnt);
			}
			
		}catch (Exception e){
			e.printStackTrace();
		}
		
		return LevyCnt;
		
	}
	

}
