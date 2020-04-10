/**
 *  �ֽý��۸� : �λ��û ���̹� ���漼û
 *  ��  ��  �� : ����
 *  ��  ��  �� : ȯ�氳���δ�� �����ڷῬ��
 *  Ŭ����  ID : Txdm2460_process : �� ó�������� �ۼ��ϴ� Ŭ����
 *  ����  �̷� : 2014.01.23 ���� e-���� �ǽð� ����ó�� �߰� �� �ʵ� �߰� 
 *               ENV_RAMT-�����ݾ�, ENV_RGBN-��������, ENV_SYSDATE-�������
 * ------------------------------------------------------------------------
 *  �ۼ���      	�Ҽ�  		    ����            Tag           ����
 * ------------------------------------------------------------------------
 *  �۵���       ��ä��(��)      2011.06.03         %01%         �����ۼ�
 */
package com.uc.bs.cyber.daemon.txdm2460;

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
public class Txdm2460_process extends Codm_BaseProcess implements Codm_interface {

	private MapForm dataMap            = null;
	private int loop_cnt = 0, dloop_cnt = 0, insert_cnt = 0, update_cnt = 0, del_cnt = 0;
	private int non_dt_cnt = 0;
	private int org_cnt, org_del_cnt = 0;
	// ���� e ���� �ǽð� ���� ó���Ǽ�
	private int sunap_cnt = 0;
	
	/**
	  * 
	 */
	public Txdm2460_process() {
		
		super();
		
		log = LogFactory.getLog(this.getClass());
		
		/**
		 * 5 �и��� ����
		 */
		loopTerm = 60 * 5;
	}
	
	/*Context ���Կ�*/
	public void setApp(ApplicationContext context) {
		this.context = context;
	}
	
	/*Ʈ������� �����ϱ� ���� �Լ�.*/
	private void mainTransProcess(){
		
		try {
			
			int page = 0;
			
			setContext(appContext);
			setApp(appContext);

			UcContextHolder.setCustomerType(this.dataSource);
			
			dataMap = new MapForm();  /*�ʱ�ȭ*/
			dataMap.setMap("PAGE_PER_CNT",  500);           /*�������� ����*/
			
			if(jobId == 0) {
			
				do {
					
					page = govService.getOneFieldInteger("TXDM2460.SELECT_ENV_LEVY_PAGE_CNT", dataMap);
					
					log.info("[ȯ�氳���δ�ݺΰ�] PAGE_PER_CNT(" + dataMap.getMap("PAGE_PER_CNT") + ") �� Page���� =" + page);
					
					dataMap.setMap("GUBUN",  "1");
					
					if(page == 0){
						
						/***********************************************************************************/
						try{
							MapForm daemonMap = new MapForm();
							daemonMap.setMap("DAEMON"    , "TXDM2460");
							daemonMap.setMap("DAEMON_NM" , "ȯ�氳��(ENVTNA2BCR_ETAX)�ΰ�����");
							daemonMap.setMap("SGG_COD"   , c_slf_org);
							daemonMap.setMap("TOTAL_CNT" , 0);
							daemonMap.setMap("INSERT_CNT", 0);
							daemonMap.setMap("UPDATE_CNT", 0);
							daemonMap.setMap("DELETE_CNT", 0);
							daemonMap.setMap("ERROR_CNT" , 0);
							cyberService.queryForInsert("CODMBASE.InsertDaemonProcessCheck", daemonMap);
						}catch(Exception ex){
							log.debug("ȯ�氳��(ENVTNA2BCR_ETAX)�ΰ����� ����� �����ϴ�. ��� ����");
						}				
						/***********************************************************************************/
						
						break;
					}
					
					for(int i = 1 ; i <= page ; i ++) {
						
						MapForm ststMap = new MapForm();
						
						ststMap.setMap("JOB_ID"    , "TXDM2460");
						ststMap.setMap("PARM_NM"   , this.c_slf_org + "||" + this.govId);
						ststMap.setMap("LOG_DESC"  , "== START ==");
						ststMap.setMap("RESULT_CD" , "S");
						
						/*ȯ�氳���δ�� �����ڷῬ�� ����... */
						cyberService.queryForUpdate("TXDM2420.INSERT_LOG_JOBSTATE", ststMap);
						
						dataMap.setMap("PAGE",  i);    /*ó��������*/
						
						this.startJob();
						
						ststMap.setMap("LOG_DESC"  , "== END  ȯ�氳���δ�� �����ڷῬ�� ó���Ǽ�::" + loop_cnt + ", �ΰ�ó��::" + insert_cnt + ", ������Ʈ::" + update_cnt + ", ����ó��::" + del_cnt);
						ststMap.setMap("RESULT_CD" , "E");
						
						/*�ΰ������ڷ� ���Ϸ� */
						cyberService.queryForUpdate("TXDM2420.INSERT_LOG_JOBSTATE", ststMap);
						
					}
					
					if(govService.getOneFieldInteger("TXDM2460.SELECT_ENV_LEVY_PAGE_CNT", dataMap) == 0) {
						break;
					}

				} while(true);
				
			}else if(jobId == 1) {
				
				do {
					
					//page = govService.getOneFieldInteger("TXDM2460.SELECT_ENV_LEVY_DEL_PAGE_CNT", dataMap);
					
					page = govService.getOneFieldInteger("TXDM2460.getEnvTaxSunapAndDeleteDataCount", dataMap);
					
					log.info("[ȯ�氳���δ�� ����e���μ��� �� ����] PAGE_PER_CNT(" + dataMap.getMap("PAGE_PER_CNT") + ") �� Page���� =" + page);
					
					dataMap.setMap("GUBUN",  "2");
					
					if(page == 0) break;
					
					for(int i = 1 ; i <= page ; i ++) {

						dataMap.setMap("PAGE",  i);    /*ó��������*/
						
						this.startJob();
					}
					
					//if(govService.getOneFieldInteger("TXDM2460.SELECT_ENV_LEVY_PAGE_CNT", dataMap) == 0) {
					//	break;
					//}
					
					if(govService.getOneFieldInteger("TXDM2460.getEnvTaxSunapAndDeleteDataCount", dataMap) == 0) {
						break;
					}

				} while(true);
				
			}

		} catch (Exception e) {
			
			e.printStackTrace();
		}			
		
	}
	
	/* process starting */
	public void runProcess() throws Exception {
		
		//log.debug("=====================================================================");
		//log.debug("=" + this.getClass().getName()+ " runProcess() ==");
		//log.debug("=====================================================================");
		
		/*Ʈ����� ���� ����*/
		mainTransProcess();
	}

	
	public void setDatasrc(String datasrc) {
		
		this.dataSource = datasrc;
	}

	
	public void transactionJob() {
		
		if(dataMap.getMap("GUBUN").equals("1")) {
			txdm2460_JobProcess1();
		} else if(dataMap.getMap("GUBUN").equals("2")) {
			txdm2460_JobProcess2();
		}
		
	}
	
	/**
	 * �ΰ� ����
	 * @return
	 */
	private int txdm2460_JobProcess1() {
		
		log.info("=====================================================================");
		log.info("=" + this.getClass().getName()+ " txdm2460_JobProcess1()[ȯ�氳���δ�� �ΰ��ڷῬ��] Start =");
		log.info("=====================================================================");
		
		/*Ʈ������� �����ϴ� �Ǿ����� ���⼭ �����Ѵ�...*/
		/*�Ǿ����� �����մϴ�.*/
		loop_cnt = 0; 
		insert_cnt = 0;
		update_cnt = 0;
		non_dt_cnt = 0;
		org_cnt = 0;

		long elapseTime1 = 0;
		long elapseTime2 = 0;
		
		int levyCnt = 0;
		
		/*��û�ڵ����*/
		dataMap.setMap("SGG_COD" , c_slf_org);
		
		try{
			/*ȯ�氳���δ�� �ΰ������� �����´�.*/
			ArrayList<MapForm> alEnvLevyList =  govService.queryForList("TXDM2460.SELECT_ENV_LEVY_LIST", dataMap);
			
			levyCnt = alEnvLevyList.size();
			
			log.info("[" + this.c_slf_org_nm + "]ȯ�氳���δ�� �ΰ����� �Ǽ� = " + levyCnt);
			
			if (levyCnt  >  0)   {
			
				elapseTime1 = System.currentTimeMillis();
				
				for (int rec_cnt = 0;  rec_cnt < alEnvLevyList.size();  rec_cnt++)   {
					
					MapForm mpEnvLevyList =  alEnvLevyList.get(rec_cnt);
					
					if (mpEnvLevyList == null  ||  mpEnvLevyList.isEmpty() )   {
						continue;
					}
					
					loop_cnt++;
					
					String sntg = (String) mpEnvLevyList.getMap("ENV_SNTG");
					String trtg = (String) mpEnvLevyList.getMap("ENV_TRTG");
					
					if(sntg.equals("0") && trtg.equals("0")){
						
						/*==========�ΰ��ڷ� ����...===========*/
						/*�ʱⰪ ����*/
						mpEnvLevyList.setMap("CUD_OPT"   ,"1");  /*ó�������ڵ�*/
						mpEnvLevyList.setMap("PROC_CLS"  ,"1");  /*�������ü������*/
						mpEnvLevyList.setMap("DEL_YN"    ,"N"); 
						mpEnvLevyList.setMap("SGG_TR_TG", "0");  /*��û����ó������*/
						
						try{
							
							
							if (cyberService.queryForUpdate("TXDM2460.INSERT_ENV_H_LEVY_DESC", mpEnvLevyList) > 0) {
								
				
								cyberService.queryForUpdate("TXDM2460.INSERT_ENV_H_LEVY", mpEnvLevyList);
								//��ø���
								//cyberService.queryForUpdate("TXDM2460.UPDATE_ENV_H_LEVY_OCRBD", mpEnvLevyList);						
								insert_cnt++;
							} 
							
						}catch (Exception e){
							
							/*�ߺ��� �߻��ϰų� �������ǿ� ��߳��� ���üũ*/
							if (e instanceof DuplicateKeyException){
								
								MapForm mapPLTInfo = cyberService.queryForMap("TXDM2460.SELECT_ENV_PENALTY_INFO", mpEnvLevyList);
								
								if (mapPLTInfo == null  ||  mapPLTInfo.isEmpty() )   {
									continue;
								}
								
								long envamt2 = ((BigDecimal)(mpEnvLevyList.getMap("BUTT"))).longValue() + ((BigDecimal)(mpEnvLevyList.getMap("ADD_AMT"))).longValue();
								long envamt1 = ((BigDecimal)(mpEnvLevyList.getMap("BUTT"))).longValue();
								
								long pltamt = ((BigDecimal)(mapPLTInfo.getMap("ENV_AMT"))).longValue();
								
								try{
									
									boolean virRemake=false;
									
									/*�ΰ��� ������¿� ǥ���� �̸��� ������...*/
									if (!virRemake&&!mpEnvLevyList.getMap("REG_NM").equals(mapPLTInfo.getMap("REG_NM")))virRemake=true;
									if (!virRemake&&!mpEnvLevyList.getMap("ENV_CNAPG").equals(mapPLTInfo.getMap("ENV_CNAPG")))virRemake=true;
									if (!virRemake&&!mpEnvLevyList.getMap("DEADLINE_DT").equals(mapPLTInfo.getMap("DEADLINE_DT")))virRemake=true;
									if (!virRemake&&!mpEnvLevyList.getMap("DUE_DT").equals(mapPLTInfo.getMap("DUE_DT")))virRemake=true;
									if (!virRemake&&!mpEnvLevyList.getMap("DUE_F_DT").equals(mapPLTInfo.getMap("DUE_F_DT")))virRemake=true;
									if (!virRemake&&pltamt != envamt2)virRemake=true;
									
									if(virRemake){
										mpEnvLevyList.setMap("AMTEQ"      ,"N");   /*������Ʈ ��������*/
										mpEnvLevyList.setMap("PROC_CLS"   ,"1");   /*ó������*/
										//mpEnvLevyList.setMap("VIR_ACC_NO" ,CbUtil.nullChk(mpEnvLevyList.getMap("ENV_ACC_NO")));    /*���û���ܿ��� ������ �ִ� �������*/
										//mpEnvLevyList.setMap("SGG_TR_TG"  ,"0");   /*����ó������*/
									}else{
										mpEnvLevyList.setMap("AMTEQ"      ,"Y");   /*������Ʈ ��������*/
										//mpEnvLevyList.setMap("SGG_TR_TG"  ,"0");   /*����ó������*/
									}
									//if(mpEnvLevyList.getMap("ENV_ACC_NO").equals("") || mpEnvLevyList.getMap("ENV_ACC_NO") == null ){
									//	mpEnvLevyList.setMap("SGG_TR_TG"  ,"0");   /*����ó������*/
									//}else{
									//	mpEnvLevyList.setMap("SGG_TR_TG"  ,"1");   /*����ó������*/
									//}

									if (cyberService.queryForUpdate("TXDM2460.UPDATE_ENV_H_LEVY_DESC", mpEnvLevyList) > 0) {
				
										cyberService.queryForUpdate("TXDM2460.UPDATE_ENV_H_LEVY", mpEnvLevyList);
										//cyberService.queryForUpdate("TXDM2460.UPDATE_ENV_H_LEVY_OCRBD", mpEnvLevyList);
									    update_cnt++;
									}

								}catch (Exception ex){
									log.info("�����߻������� = " + mpEnvLevyList.getMaps());
									ex.printStackTrace();
									log.info("Exception ex " + ex.getMessage());
									throw (RuntimeException) ex;
								}

							}else{
								log.info("�����߻������� = " + mpEnvLevyList.getMaps());
								e.printStackTrace();
								log.info("Exception e " + e.getMessage());
								throw (RuntimeException) e;
							}
							
						}
						
						/*�ΰ�ó������ ������Ʈ*/
						if(govService.queryForUpdate("TXDM2460.UPDATE_TR_ENVTAX_TAB", mpEnvLevyList) > 0) {
							org_cnt++;
						}
				
						
					}else{
						non_dt_cnt++;
					}
					
				}
				
				elapseTime2 = System.currentTimeMillis();
				
				log.info("[H][" + c_slf_org_nm + "]�ΰ��ڷ� ����Ǽ� : " + levyCnt + " (EA)");
				log.info("[H][" + c_slf_org_nm + "]�ΰ��ڷ� ����ð� : " + CbUtil.formatTime(elapseTime2 - elapseTime1));
				
			}
		
			log.info("ȯ�氳���δ�� �����ڷῬ�� ó���Ǽ�::" + loop_cnt + ", �ΰ�ó��::" + insert_cnt + ", ������Ʈ::" + update_cnt + ", ���������Ʈ = " + org_cnt); 

			/***********************************************************************************/
			try{
				MapForm daemonMap = new MapForm();
				daemonMap.setMap("DAEMON"    , "TXDM2460");
				daemonMap.setMap("DAEMON_NM" , "ȯ�氳��(ENVTNA2BCR_ETAX)�ΰ�����");
				daemonMap.setMap("SGG_COD"   , c_slf_org);
				daemonMap.setMap("TOTAL_CNT" , loop_cnt);
				daemonMap.setMap("INSERT_CNT", insert_cnt);
				daemonMap.setMap("UPDATE_CNT", update_cnt);
				daemonMap.setMap("DELETE_CNT", 0);
				daemonMap.setMap("ERROR_CNT" , non_dt_cnt);
				cyberService.queryForInsert("CODMBASE.InsertDaemonProcessCheck", daemonMap);
			}catch(Exception ex){
				log.debug("ȯ�氳��(ENVTNA2BCR_ETAX)�ΰ����� �α� ��� ����");
			}				
			/***********************************************************************************/
			
			
		}catch (Exception e){
			e.printStackTrace();
		}
		
		return levyCnt;
	}
	
	/**
	 * ���� �� ���� ����
	 * @return
	 */
	private int txdm2460_JobProcess2() {
		
		log.info("=====================================================================");
		log.info("=" + this.getClass().getName()+ " txdm2460_JobProcess1()[ȯ�氳���δ�� �����ڷῬ��] Start =");
		log.info("=====================================================================");
		
		/*Ʈ������� �����ϴ� �Ǿ����� ���⼭ �����Ѵ�...*/
		/*�Ǿ����� �����մϴ�.*/
		dloop_cnt = 0; 
		del_cnt = 0;
		org_del_cnt = 0;
		sunap_cnt = 0;
		
		int bugaInsert_cnt = 0;
		int bugaDelyn_cnt = 0;
		int pass_cnt = 0;
		int gandan_cnt = 0;
		
		long elapseTime1 = 0;
		long elapseTime2 = 0;
		
		int levyCnt = 0;
		
		/*��û�ڵ����*/
		dataMap.setMap("SGG_COD" , c_slf_org);
		
		try{

			/*ȯ�氳���δ�� �ΰ������� ����ó���ؾ� �� DATA�� �����´�.*/
			//ArrayList<MapForm> alEnvLevyDelList =  govService.queryForList("TXDM2460.SELECT_ENV_LEVY_DEL_INFOKEY", dataMap);
			
			//�������̺��� ���� e ���� ����ó�� �� ����ó�� �ؾ��� DATA�� �����´�.
			ArrayList<MapForm> alEnvLevyDelList =  govService.queryForList("TXDM2460.getEnvTaxSunapDataAndDeleteData", dataMap);
			
			levyCnt = alEnvLevyDelList.size();
			
			log.info("[" + this.c_slf_org_nm + "]ȯ�氳���δ�� ����e���� �� �����ΰ� �Ǽ� = " + levyCnt);
			
			if(levyCnt > 0){
				
				elapseTime1 = System.currentTimeMillis();
				
				// �ΰ��ڷ�, �����ڷ�, �������� Ȯ��  
				MapForm bugaCheck = new MapForm();
				
				for(int rec_cnt = 0;  rec_cnt < alEnvLevyDelList.size();  rec_cnt++){
					
					MapForm mpEnvLevyDelList =  alEnvLevyDelList.get(rec_cnt);
					
					if(mpEnvLevyDelList == null || mpEnvLevyDelList.isEmpty()){
						continue;
					}				
											
					
					// ���� e ���� �ǽð� ���� ��
					if(mpEnvLevyDelList.getMap("SNSU").equals("1")){
						
						sunap_cnt++;
						
						bugaCheck = new MapForm();
						
						bugaCheck = cyberService.queryForMap("TXDM2460.getBugaDataAndSunapResult", mpEnvLevyDelList);
						
						// ��������
						mpEnvLevyDelList.setMap("PAY_CNT", bugaCheck.getMap("PAY_CNT"));
						
						// ����ó�� ����
						mpEnvLevyDelList.setMap("SNTG"   , "2");
						mpEnvLevyDelList.setMap("SNSU"   , "1");
						mpEnvLevyDelList.setMap("BANK_CD", "100");
						mpEnvLevyDelList.setMap("BRC_NO" , "1000000");
						mpEnvLevyDelList.setMap("TRTG"   , "1");
						
						/*�ʱⰪ ����*/
						mpEnvLevyDelList.setMap("CUD_OPT"   , "1");  /* ó�������ڵ�     1:���,2:����,3:����  */
						mpEnvLevyDelList.setMap("PROC_CLS"  , "3");  /* �������ü������ 1:���,2:������,3:�Ϸ�*/
						mpEnvLevyDelList.setMap("DEL_YN"    , "N"); 
						mpEnvLevyDelList.setMap("SGG_TR_TG" , "1");  /* ��û����ó������ 0:������ 1:����       */
																
						// ������ ���� ����ó�� �Ϸ�
						if(bugaCheck.getMap("SNSU").equals("1")){
							
							pass_cnt++;
							log.info("������ ���� �̹� ���� ó�� �Ǿ����ϴ�.");
							
						}
						// �ΰ��ڷ� �̻���
						else if(bugaCheck.getMap("SNSU").equals("NOT") && bugaCheck.getMap("DEL_YN").equals("NOT")){
							
							bugaInsert_cnt++;
							
							/*==========�ΰ��ڷ� ��� �� ����ó��===========*/
							log.info("�ΰ��ڷᰡ ��� ���� �� ����ó�� �մϴ�.");						
							
							// �ΰ��ڷ� ���
							try{														
								if(cyberService.queryForUpdate("TXDM2460.INSERT_ENV_H_LEVY_DESC", mpEnvLevyDelList) > 0){									
									cyberService.queryForUpdate("TXDM2460.INSERT_ENV_H_LEVY", mpEnvLevyDelList);									
								} 								
							}catch (Exception e){
								e.printStackTrace();
								log.info("�ΰ���� ���� "+ mpEnvLevyDelList.getMaps());
							}
							
							// �����ڷ� ���
							try{
								cyberService.queryForInsert("TXDM2460.insertGandanENapbuSunap", mpEnvLevyDelList);
							}catch(Exception e){
								e.printStackTrace();
								log.info("����ó�� ����" + mpEnvLevyDelList.getMaps());
							}
																			
						}
						// ���� e ���� ����ó��
						else if(bugaCheck.getMap("SNSU").equals("NOT") && !bugaCheck.getMap("DEL_YN").equals("NOT")){
							
							bugaDelyn_cnt++;	
							
							log.info("���� e ���� ����ó��");
							
							// �ΰ��ڷ� TX2132_TB ����ó��
							try{
								cyberService.queryForUpdate("TXDM2460.updateBugaDataSntgAndDelyn", mpEnvLevyDelList);
							}catch(Exception e){
								e.printStackTrace();
								log.info("�ΰ��ڷ� SNTG, DELYN ���� ���� " + mpEnvLevyDelList.getMaps());
							}
							// �����ڷ� ���
							try{
								cyberService.queryForInsert("TXDM2460.insertGandanENapbuSunap", mpEnvLevyDelList);
							}catch(Exception e){
								e.printStackTrace();
								log.info("����ó�� ���� " + mpEnvLevyDelList.getMaps());
							}
							
						}
						
						
					}
					// ���� ���� ó�� ��
					else if(mpEnvLevyDelList.getMap("SNSU").equals("DEL")){
											
						String sntg = (String) mpEnvLevyDelList.getMap("SNTG");
						String trtg = (String) mpEnvLevyDelList.getMap("SGG_TR_TG");
						
						long amt = ((BigDecimal)(mpEnvLevyDelList.getMap("BUTT"))).longValue() + ((BigDecimal)(mpEnvLevyDelList.getMap("ADD_AMT"))).longValue();
						
						if(((sntg.equals("8")||sntg.equals("9")) && trtg.equals("0")) || (amt == 0 && trtg.equals("0"))){
							
							if(cyberService.queryForUpdate("TXDM2460.UPDATE_DEL_H_LEVY_DESC", mpEnvLevyDelList) > 0) {
								del_cnt++;
							}
						} else {
							log.debug("�������ǿ� �ش�ȵ� [" + mpEnvLevyDelList.getMaps() + "]");
						}
						
					}else{
						// ����
						log.debug(" ���� [" + mpEnvLevyDelList.getMaps() + "]");
					}
										
					
					/*���� ������Ʈ*/
					if(govService.queryForUpdate("TXDM2460.UPDATE_TR_DEL_ENVTAX_TAB", mpEnvLevyDelList) > 0){
						org_del_cnt++;
					}

				}
				
				elapseTime2 = System.currentTimeMillis();
				log.info("[H][" + c_slf_org_nm + "]ȯ�氳�� ���� e ���� �� �����ڷ� ����ð� : " + CbUtil.formatTime(elapseTime2 - elapseTime1));
				log.info("��ü�Ǽ�::" + alEnvLevyDelList.size() +", ���� e ���� �����Ǽ� :: "+sunap_cnt+ " �� �ΰ�����"+bugaInsert_cnt+" ����ó�� "+bugaDelyn_cnt+" �� , ��������ó��::" + del_cnt + ", ���������Ʈ ::" + org_del_cnt);
				
			}else{
				log.info("ȯ�氳���δ�� ����e���� ����ó�� �� ����ó�� �Ǽ��� �����ϴ�.");
			}
						
			
		}catch (Exception e){
			e.printStackTrace();
		}
		
		return levyCnt;
	}
	
}
