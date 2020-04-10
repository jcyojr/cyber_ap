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
package com.uc.bs.cyber.daemon.txdm3110;

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
public class Txdm3110_process extends Codm_BaseProcess implements Codm_interface {

	private MapForm dataMap  = null;
	private int loop_cnt = 0, insert_cnt = 0, update_cnt = 0, del_cnt = 0, vir_del_cnt = 0, notupdate_cnt=0;
	
	
	/**
	 * 
	 */
	public Txdm3110_process() {
		// TODO Auto-generated constructor stub
		super();
		
		log = LogFactory.getLog(this.getClass());
		
		this.loopTerm = 300;	/* 300�ʸ��� */
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
		
		txdm3110_JobProcess();
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
			dataMap.setMap("TRN_YN"       , "0");             /*������ ��    */

			
			int page = govService.getOneFieldInteger("TXDM3110.select_count_page", dataMap);
			log.info("[���漼 �ΰ��ڷ� (" + c_slf_org_nm + ")] PAGE_PER_CNT(" + dataMap.getMap("PAGE_PER_CNT") + ") �� Page���� =" + page);
			if(page > 0){
				for(int i = 1 ; i <= page ; i ++) {
					dataMap.setMap("PAGE",  i);
					this.startJob();
				}
			}else{
				
				log.info("[���漼 �ΰ��ڷ� (" + c_slf_org_nm + ")] ��ϰǼ��� �����ϴ�.");
				
				/********************************* ��û�� ó�� �α� ��� **************************************************/
				try{
					MapForm daemonMap = new MapForm();
					daemonMap.setMap("DAEMON"    , "TXDM3110");
					daemonMap.setMap("DAEMON_NM" , "���漼 �����ڷῬ��");
					daemonMap.setMap("SGG_COD"   , c_slf_org);
					daemonMap.setMap("TOTAL_CNT" , page);
					daemonMap.setMap("INSERT_CNT", 0);
					daemonMap.setMap("UPDATE_CNT", 0);
					daemonMap.setMap("DELETE_CNT", 0);
					daemonMap.setMap("ERROR_CNT" , 0);
					cyberService.queryForInsert("CODMBASE.InsertDaemonProcessCheck", daemonMap);
				}catch(Exception ex){
					log.info("��û�� ó�� �α� ��� ����");
				}				
				/******************************************************************************************************/
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}			
	}
	
	/*�ڷ� ����*/
	private int txdm3110_JobProcess() {

		long queryElapse1 = 0;
		long queryElapse2 = 0;
		
		long elapseTime1 = 0;
		long elapseTime2 = 0;
		
		int LevyCnt = 0;
		int kbcnt =0;
		
		loop_cnt = 0; 
		insert_cnt = 0;
		update_cnt = 0;
		notupdate_cnt=0;
		del_cnt = 0;
		vir_del_cnt = 0;
		
		MapForm mpTaxFixLevyList = null;
		MapForm mapkbviruplist = null;
		try {
			
			queryElapse1 = System.currentTimeMillis();
			
			log.info("=====================================================================");
			log.info("=" + this.getClass().getName()+ " txdm3110_JobProcess() [���漼 ����� �����ڷῬ��] Start =");
			log.info("= govid["+this.govId+"], orgcd["+this.c_slf_org+"], orgcd["+this.c_slf_org_nm+"]");
			log.info("=====================================================================");
			 
/*			ArrayList<MapForm> kb_vir_acc_List = govService.queryForList("TXDM3110.KB_VIR_ACC", dataMap);
			kbcnt = kb_vir_acc_List.size();
			String gubn = "";
			if (kbcnt > 0) {
				log.info("=========[�������డ����¼����� �ΰ��ڷ� ���� �Ǽ�]== : "+kbcnt);
				for (int i = 0; i < kbcnt; i++) {
					mapkbviruplist = kb_vir_acc_List.get(i);
					gubn = (String) mapkbviruplist.getMap("gb");
					if (gubn == "602") {
				    log.info("=========[�������డ����¼����� �ΰ��ڷ� ���� �翬��(scon602)]=========");
						cyberService.queryForUpdate("TXDM3110.scon602_kbupdate", mapkbviruplist);
					}else if(gubn == "743"){
					log.info("=========[�������డ����¼����� �ΰ��ڷ� ���� �翬��(scon743)]=========");
						cyberService.queryForUpdate("TXDM3110.scon743_kbupdate", mapkbviruplist);
					}					
				}
			}*/
			
			
			/*���漼 ����� �ΰ������� �����´�.*/
			ArrayList<MapForm> alFixedLevyList =  govService.queryForList("TXDM3110.select_list", dataMap);

			queryElapse2 = System.currentTimeMillis();
			
			LevyCnt = alFixedLevyList.size();
			
			log.info("[" + c_slf_org_nm + "]���漼 ������ȸ �ð� : " + CbUtil.formatTime(queryElapse2 - queryElapse1) + " (" + LevyCnt + ")");
			
			if (LevyCnt  >  0)   {

				String notice_cls = "";
				String due_f_dt   = "";
				
				elapseTime1 = System.currentTimeMillis();
				
				for (int i = 0;  i < LevyCnt;  i++)   {
					
					mpTaxFixLevyList = new MapForm();
					
					mpTaxFixLevyList =  alFixedLevyList.get(i);
					
					mpTaxFixLevyList.setMap("JAKU", "");           /* ��ǥ�ٰ� */
					mpTaxFixLevyList.setMap("ABKU", "");           /* �з����� */
					mpTaxFixLevyList.setMap("NPJA", "");           /* �����ǹ��� */
					mpTaxFixLevyList.setMap("ADD_CNT", 0);         /* ����Ƚ�� */
					mpTaxFixLevyList.setMap("CHG_CNT", "");        /* ����Ƚ�� */
					mpTaxFixLevyList.setMap("WDATE", "");          /* �ڷ��������� */
					mpTaxFixLevyList.setMap("INPUT_ID", "MUSER");  /* �Է��� */
					mpTaxFixLevyList.setMap("SNTG", "0");  		   /* �������� */
					mpTaxFixLevyList.setMap("SAYU_CD", "");  	   /* ���� */
					mpTaxFixLevyList.setMap("DL_TAX_DT", "");  	   /* ü���ΰ����� */


					if (mpTaxFixLevyList == null  ||  mpTaxFixLevyList.isEmpty() )   {
						continue;
					}			

					/*========================*/
					/*Ʈ�����ó�� 1000�Ǵ��� �⺻*/
					/*1. �ΰ��� �����п� ���� ������� ���� ����*/
					/*2. �ΰ��ڷ� ��� �� ����*/
					/*========================*/
					
					try {
						loop_cnt++;

						if(govService.getOneFieldInteger("TXDM3110.select_count_exists", mpTaxFixLevyList)==0){
							
							notice_cls = (String) mpTaxFixLevyList.getMap("NOTICE_CLS");  /*�����ļ�������*/
	
							/*1.�����кΰ��̹Ƿ� ������¹�ȣ�� ���� ó�� ���屺�� ����*/
							if (mpTaxFixLevyList.getMap("CUD_OPT").equals("2") && !dataMap.getMap("SGG_COD").equals("710")) {
	
								if((mpTaxFixLevyList.getMap("DLQ_DIV").equals("1") || (mpTaxFixLevyList.getMap("DLQ_DIV").equals("2"))
										&& ((mpTaxFixLevyList.getMap("DLQ_CHG_DIV").equals("20") || mpTaxFixLevyList.getMap("DLQ_CHG_DIV").equals("31"))))) {
									
									/* ������»��� */ 
									//if (govService.queryForDelete("TXDM3110.update_scon604_clean", mpTaxFixLevyList) > 0) {
									//	vir_del_cnt++;
									//}
									
								}
									
							}
							
							/* 2. �ΰ��ڷ� ��� �� ���� */
							
							if (notice_cls.equals("C")) due_f_dt = "00000000"; 
							
							else if (notice_cls.equals("D")) {
															
								long mntx = ((BigDecimal)mpTaxFixLevyList.getMap("MNTX")).longValue(); // �����ݾ� 300000�� �̸��̸� ���⹫��
								
								if (mntx < 300000) due_f_dt = "00000000"; // ���⹫��
								else due_f_dt = (String) mpTaxFixLevyList.getMap("GGYM"); 	// ������»������ DEADLINE_DT
								
							} else due_f_dt = (String) mpTaxFixLevyList.getMap("GGYM");	    // ������»������ DEADLINE_DT
							
							mpTaxFixLevyList.setMap("DEADLINE_DT", due_f_dt);
													
							long b_amt = ((BigDecimal)mpTaxFixLevyList.getMap("SUM_B_AMT")).longValue(); // ���⳻�ݾ�
							long f_amt = ((BigDecimal)mpTaxFixLevyList.getMap("SUM_F_AMT")).longValue(); // �����ıݾ�
							
							String tax_item = (String) mpTaxFixLevyList.getMap("TAX_ITEM");  //�����ڵ�
							
							/* �����ڷ� ���� ó�� */
							if (mpTaxFixLevyList.getMap("CUD_OPT").equals("3") || (b_amt + f_amt) == 0) {
								
								/*flag ó��*/
								if (cyberService.queryForUpdate("TXDM3110.update_tx1102_tb2", mpTaxFixLevyList) > 0) {
									
									del_cnt++;	
								}
								
								if(mpTaxFixLevyList.getMap("CUD_OPT").equals("3") && tax_item.equals("140003")){     // ��������ҵ漼 �̸�
									cyberService.queryForUpdate("TXDM3110.update_es5001_status", mpTaxFixLevyList);
								}
								
							} else {
								/*�ΰ��ڷ� �Է� ����*/
								
								/*�ΰ�*/
								try {
									
									cyberService.queryForInsert("TXDM3110.insert_tx1101_tb", mpTaxFixLevyList);
									
									insert_cnt++;
									
								} catch (Exception e){
									
									if (e instanceof DuplicateKeyException){
										
										cyberService.queryForUpdate("TXDM3110.update_tx1101_tb", mpTaxFixLevyList);
										
										update_cnt++;
										
									} else {
										
										log.info("�����߻������� = " + mpTaxFixLevyList.getMaps());
										
										e.printStackTrace();							
										throw (RuntimeException) e;
									}
								}
								
								/*�ΰ���*/
								try {
									
									mpTaxFixLevyList.setMap("PROC_CLS", "1");  		   /* �������ä�������ڵ� */
									mpTaxFixLevyList.setMap("SGG_TR_TG", "0");         /* ������� ��û���ۿ��� */
									
									cyberService.queryForInsert("TXDM3110.insert_tx1102_tb", mpTaxFixLevyList);
									
									insert_cnt++;
									
								}catch (Exception e){
									
									if (e instanceof DuplicateKeyException){
										
										cyberService.queryForUpdate("TXDM3110.update_tx1102_tb", mpTaxFixLevyList);
										
										update_cnt++;
										
									} else {
										
										log.info("�����߻������� = " + mpTaxFixLevyList.getMaps());
										
										e.printStackTrace();							
										throw (RuntimeException) e;
									}
								}
							}
						}
						
						if(mpTaxFixLevyList.getMap("TBL_CD").equals("SCON602")){
							govService.queryForUpdate("TXDM3110.update_complete_602", mpTaxFixLevyList);
						}else{
							govService.queryForUpdate("TXDM3110.update_complete_743", mpTaxFixLevyList);
						}

					} catch (Exception e) {
						// TODO Auto-generated catch block
						log.info("�����߻������� = " + mpTaxFixLevyList.getMaps());
						
						e.printStackTrace();
					}
				}
				
				elapseTime2 = System.currentTimeMillis();
				
				log.info("[" + c_slf_org_nm + "]���漼 �����ڷ� ����ð� : " + CbUtil.formatTime(elapseTime2 - elapseTime1));
				log.info("���漼 �����ڷῬ�� ó���Ǽ�::" + loop_cnt + ", �ΰ�ó��::" + ((insert_cnt > 0) ? insert_cnt / 2 : insert_cnt) 
					   + ", ������Ʈ::" + ((update_cnt > 0) ? update_cnt / 2 : update_cnt) + ", ����ó��::" + del_cnt + ", ���尡����� ���� ::" + vir_del_cnt);
				
				/********************************* ��û�� ó�� �α� ��� **************************************************/
				try{
					MapForm daemonMap = new MapForm();
					daemonMap.setMap("DAEMON"    , "TXDM3110");
					daemonMap.setMap("DAEMON_NM" , "���漼 �����ڷῬ��");
					daemonMap.setMap("SGG_COD"   , c_slf_org);
					daemonMap.setMap("TOTAL_CNT" , loop_cnt);
					daemonMap.setMap("INSERT_CNT", ((insert_cnt > 0) ? insert_cnt / 2 : insert_cnt));
					daemonMap.setMap("UPDATE_CNT", ((update_cnt > 0) ? update_cnt / 2 : update_cnt));
					daemonMap.setMap("DELETE_CNT", del_cnt);
					daemonMap.setMap("ERROR_CNT" , 0);
					cyberService.queryForInsert("CODMBASE.InsertDaemonProcessCheck", daemonMap);
				}catch(Exception ex){
					log.debug("��û�� ó�� �α� ��� ����");
				}				
				/******************************************************************************************************/
	
			}

		} catch (Exception e){
			e.printStackTrace();
		}
		
		return LevyCnt;
		
	}
}
