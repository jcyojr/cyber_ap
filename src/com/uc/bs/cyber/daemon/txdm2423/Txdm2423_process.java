/**
 *  �ֽý��۸� : �λ��û ���̹� ���漼û
 *  ��  ��  �� : ����
 *  ��  ��  �� : �������ߺδ�� �ΰ��ڷῬ��
 *  Ŭ����  ID : Txdm2423_process : �� ó�������� �ۼ��ϴ� Ŭ����
 *  ����  �̷� :
 * ------------------------------------------------------------------------
 *  �ۼ���      	�Ҽ�  		    ����            Tag           ����
 * ------------------------------------------------------------------------
 *  �۵���       ��ä��(��)      2011.05.11         %01%         �����ۼ�
 */
package com.uc.bs.cyber.daemon.txdm2423;

import java.math.BigDecimal;
import java.util.ArrayList;

import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;

import com.uc.bs.cyber.CbUtil;
import com.uc.bs.cyber.daemon.Codm_BaseProcess;
import com.uc.bs.cyber.daemon.Codm_interface;
import com.uc.core.MapForm;
import com.uc.core.spring.service.UcContextHolder;

public class Txdm2423_process extends Codm_BaseProcess implements Codm_interface {

	private MapForm dataMap            = null;
	private int insert_cnt = 0, update_cnt = 0, delete_cnt = 0, remote_up_cnt = 0;
    private int p_del_cnt = 0;
	
	/*Ʈ������� ���Ͽ� */
	MapForm gblNidLevyRows    = null;
	
	/**
	 * 
	 */
	public Txdm2423_process() {
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
		log.info("=[�������ߺδ��-[" + c_slf_org_nm + "] �ΰ��ڷῬ��] Start =");
		log.info("=====================================================================");
		
		try {
				
			setContext(appContext);
			setApp(appContext);

			UcContextHolder.setCustomerType(this.dataSource);
						
			dataMap = new MapForm();  /*�ʱ�ȭ*/
			
			log.info("this.c_slf_org ======================= [" + this.c_slf_org + "]");

			dataMap.setMap("SGG_COD",  this.c_slf_org);

			int CNT = 0;
			try{
				CNT = govService.getOneFieldInteger("TXDM2423.SELECT_ROAD_CNT", dataMap);
			}catch (Exception sub_e){
				log.error("SELECT_ROAD_CNT ����");
				sub_e.printStackTrace();
			}						

			if(CNT>0){
				try{
					govService.queryForUpdate("TXDM2423.UPDATE_ROAD_START", dataMap);
				}catch (Exception sub_e){
					log.error("UPDATE_ROAD_START ����");
					sub_e.printStackTrace();
				}						

				this.startJob();

				try{
					govService.queryForUpdate("TXDM2423.UPDATE_ROAD_END", dataMap);
				}catch (Exception sub_e){
					log.error("UPDATE_ROAD_END ����");
					sub_e.printStackTrace();
				}						
			
			}else{
				
				/***********************************************************************************/
				try{
					MapForm daemonMap = new MapForm();
					daemonMap.setMap("DAEMON"    , "TXDM2423");
					daemonMap.setMap("DAEMON_NM" , "��������(ROTTNALVYINFO)�ΰ�����");
					daemonMap.setMap("SGG_COD"   , c_slf_org);
					daemonMap.setMap("TOTAL_CNT" , 0);
					daemonMap.setMap("INSERT_CNT", 0);
					daemonMap.setMap("UPDATE_CNT", 0);
					daemonMap.setMap("DELETE_CNT", 0);
					daemonMap.setMap("ERROR_CNT" , 0);
					cyberService.queryForInsert("CODMBASE.InsertDaemonProcessCheck", daemonMap);
				}catch(Exception ex){
					log.debug("��������(ROTTNALVYINFO)�ΰ����� ����� �����ϴ�. ��� ����");
				}				
				/***********************************************************************************/
				
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
		txdm2423_JobProcess();
	
	}
	
	
	private int txdm2423_JobProcess() {
		
		log.info("=====================================================================");
		log.info("=" + this.getClass().getName()+ " txdm2423_JobProcess()[�������ߺδ��-[" + c_slf_org_nm + "] �ΰ��ڷῬ��] Start =");
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
		log.info("------try�� ���� �� -----------");
		try {

			/*�������̺� �ΰ��ڷ� SELECT ����(NIS)*/
			ArrayList<MapForm> alNidLevyList = null;

			try{
				alNidLevyList =  govService.queryForList("TXDM2423.SELECT_ROAD_LIST", dataMap);
			}catch (Exception sub_e){
				log.error("SELECT_ROAD_LIST ���������� = " + gblNidLevyRows.getMaps());
				sub_e.printStackTrace();
			}						

			tot_size = alNidLevyList.size();
			
		    log.info("[" + c_slf_org_nm + "]�������ߺδ�� �ΰ� �����ڷ� �Ǽ� = [" + tot_size + "]");
			
			if (tot_size  >  0)   {
				
				/*���ڳ����ڷ� 1�Ǿ� fetch ó�� */
				for ( rec_cnt = 0;  rec_cnt < tot_size;  rec_cnt++)   {
					
					gblNidLevyRows =  alNidLevyList.get(rec_cnt);
					
					/*Ȥ�ó�...because of testing */
					if (gblNidLevyRows == null  ||  gblNidLevyRows.isEmpty() ){
						nul_cnt++;
						continue;
					}

				    String TRN_YN = "1";
			        
					// ����Ǹ� �ȵǴ� �ڷ�

					// �ű�, ����, ���� ���� ����
					String V_DEL_YN="N";
				    String LVY_STATE_SE_CODE = (String) gblNidLevyRows.getMap("LVY_STATE_SE_CODE");
				    String NAPGI_INSD_YMD = (String) gblNidLevyRows.getMap("NAPGI_INSD_YMD");
				    String EPAY_NO = (String) gblNidLevyRows.getMap("EPAY_NO");
				    String TAX_NO = (String) gblNidLevyRows.getMap("TAX_NO");
				    long PAYMENT_DATE1 = ((BigDecimal) gblNidLevyRows.getMap("PAYMENT_DATE1")).longValue();
				    
				    if(TRN_YN.equals("1")&&(LVY_STATE_SE_CODE.equals("140")||LVY_STATE_SE_CODE.equals("410")||PAYMENT_DATE1==0L)){
				    	V_DEL_YN="Y";
				    }else if(TRN_YN.equals("1")&&(LVY_STATE_SE_CODE.equals("120")||LVY_STATE_SE_CODE.equals("210"))){
				    	if(TRN_YN.equals("1")&&(NAPGI_INSD_YMD.equals(" ")||EPAY_NO.equals(" "))){
				    		log.info(c_slf_org_nm+"trn_yn~~~~");
				    		TRN_YN = "E";
				    		}
				    }else{ 
				    	log.info(c_slf_org_nm+"trn_yn111");
				    	TRN_YN = "E";
				    }
					if(TRN_YN.equals("1"))
				    {
						gblNidLevyRows.setMap("PROC_CLS","1");
						gblNidLevyRows.setMap("VIR_ACC_NO","");
						gblNidLevyRows.setMap("SGG_TR_TG","0");
						gblNidLevyRows.setMap("DEL_YN",V_DEL_YN);

						String V_CUD ="";
						int TX2111_CNT_EPAY_NO = cyberService.getOneFieldInteger("TXDM2423.SELECT_TX2111_CNT_EPAY_NO", gblNidLevyRows);
						int TX2111_CNT_TAX_NO = cyberService.getOneFieldInteger("TXDM2423.SELECT_TX2111_CNT_TAX_NO", gblNidLevyRows);
						int TX2211_CNT_TAX_NO = cyberService.getOneFieldInteger("TXDM2423.SELECT_TX2211_CNT_TAX_NO", gblNidLevyRows);

						if(TX2111_CNT_EPAY_NO==0){
							if(TX2111_CNT_TAX_NO==0){
								if(V_DEL_YN.equals("Y")){
									log.info(c_slf_org_nm+"trn_yn222");
									TRN_YN="E";
								}else{
									V_CUD="I";
								}
							}else{
								if(TX2211_CNT_TAX_NO==0){
									if(V_DEL_YN.equals("Y")){
										V_CUD="D";
									}else{
										V_CUD="U";
									}
								}else{
									log.info(c_slf_org_nm+"trn_yn333");
									TRN_YN="E";
								}
							}
						}else if(TX2111_CNT_EPAY_NO==1){
							String TAX_NO_OLD = cyberService.getOneFieldString("TXDM2423.SELECT_TX2111_TAX_NO", gblNidLevyRows);
							gblNidLevyRows.setMap("TAX_NO_KEY",TAX_NO_OLD);
							int TX2211_CNT_TAX_NO_OLD = cyberService.getOneFieldInteger("TXDM2423.SELECT_TX2211_CNT_TAX_NO", gblNidLevyRows);

							if(TX2111_CNT_TAX_NO==0){
								if(TX2211_CNT_TAX_NO_OLD==0){
									if(V_DEL_YN.equals("Y"))V_CUD="D";
									else V_CUD="U";
								}
							}else{
								if(TAX_NO.equals(TAX_NO_OLD)){
									if(TX2211_CNT_TAX_NO_OLD==0){
										if(V_DEL_YN.equals("Y"))V_CUD="D";
										else V_CUD="U";
									}
								}else{
									if(TX2211_CNT_TAX_NO==0){
										try{
											if (cyberService.queryForDelete("TXDM2423.UPDATE_TX2112_DEL_YN", gblNidLevyRows) > 0){
												delete_cnt++;
											}
										}catch (Exception sub_e){
											log.info(c_slf_org_nm+"trn_yn444");
											TRN_YN="E";
											log.error("UPDATE_TX2112_DEL_YN ���� = " + gblNidLevyRows.getMaps());
											sub_e.printStackTrace();
										}
										
										gblNidLevyRows.setMap("TAX_NO_KEY",TAX_NO);
										if(V_DEL_YN.equals("Y"))V_CUD="D";
										else V_CUD="U";
									}
								}
							}
						}else{
							log.info(c_slf_org_nm+"trn_yn555");
							TRN_YN="E";
						}

						if(V_CUD.equals("I")){
							try{
								cyberService.queryForUpdate("TXDM2423.INSERT_TX2111", gblNidLevyRows);
							}catch (Exception sub_e){
								log.info(c_slf_org_nm+"trn_yn666");
								TRN_YN="E";
								log.error("INSERT_TX2111 ���� = " + gblNidLevyRows.getMaps());
								sub_e.printStackTrace();
							}
							
							try{
								cyberService.queryForUpdate("TXDM2423.INSERT_TX2112", gblNidLevyRows);
							}catch (Exception sub_e){
								log.info(c_slf_org_nm+"trn_yn777");
								TRN_YN="E";
								log.error("INSERT_TX2112 ���� = " + gblNidLevyRows.getMaps());
								sub_e.printStackTrace();
							}
							insert_cnt++;
						}
						
						if(V_CUD.equals("U")){
							try{
								cyberService.queryForUpdate("TXDM2423.UPDATE_TX2111_TAX_NO", gblNidLevyRows);
							}catch (Exception sub_e){
								log.info(c_slf_org_nm+"trn_yn888");
								TRN_YN="E";
								log.error("UPDATE_TX2111_TAX_NO ���� = " + gblNidLevyRows.getMaps());
								sub_e.printStackTrace();
							}
							
							try{
								cyberService.queryForUpdate("TXDM2423.UPDATE_TX2112_TAX_NO", gblNidLevyRows);
							}catch (Exception sub_e){
								log.info(c_slf_org_nm+"trn_yn999");
								TRN_YN="E";
								log.error("UPDATE_TX2112_TAX_NO ���� = " + gblNidLevyRows.getMaps());
								sub_e.printStackTrace();
							}						
							update_cnt++;
						}
						
						if(V_CUD.equals("D")){
							try{
								if (cyberService.queryForDelete("TXDM2423.UPDATE_TX2112_DEL_YN", gblNidLevyRows) > 0){
									delete_cnt++;
								}
							}catch (Exception sub_e){
								log.info(c_slf_org_nm+"trn_yn000");
								TRN_YN="E";
								log.error("UPDATE_TX2112_DEL_YN ���� = " + gblNidLevyRows.getMaps());
								sub_e.printStackTrace();
							}
						}
				    }

					gblNidLevyRows.setMap("TRN_YN",TRN_YN);
					
					try{
						/*�������� ������Ʈ*/
						remote_up_cnt +=govService.queryForUpdate("TXDM2423.UPDATE_ROAD_TRN_YN", gblNidLevyRows);
					}catch (Exception sub_e){
						log.error("UPDATE_ROAD_TRN_YN ���� = " + gblNidLevyRows.getMaps());
						sub_e.printStackTrace();
					}	
					
					if(rec_cnt % 500 == 0) {
						log.info("[" + this.c_slf_org_nm + "][" + this.c_slf_org + "]�������ߺδ�� �ΰ����� �ڷ�ó���߰Ǽ� = [" + rec_cnt + "]");
					}
				}
				
				log.info("[" + c_slf_org_nm + "]�������ߺδ�� �ΰ��ڷῬ�� �Ǽ� [" + rec_cnt + "] ��ϰǼ� [" + insert_cnt +"] �����Ǽ� [" + update_cnt + "] �����Ǽ� [" + delete_cnt + "] ���������� [" + p_del_cnt + "]");
				log.info("�������������Ʈ ["+remote_up_cnt+"], ��ó�� �ΰ��Ǽ� [" + nul_cnt + "]");
				
				/***********************************************************************************/
				try{
					MapForm daemonMap = new MapForm();
					daemonMap.setMap("DAEMON"    , "TXDM2423");
					daemonMap.setMap("DAEMON_NM" , "��������(ROTTNALVYINFO)�ΰ�����");
					daemonMap.setMap("SGG_COD"   , c_slf_org);
					daemonMap.setMap("TOTAL_CNT" , rec_cnt);
					daemonMap.setMap("INSERT_CNT", insert_cnt);
					daemonMap.setMap("UPDATE_CNT", update_cnt);
					daemonMap.setMap("DELETE_CNT", delete_cnt);
					daemonMap.setMap("ERROR_CNT" , 0);
					cyberService.queryForInsert("CODMBASE.InsertDaemonProcessCheck", daemonMap);
				}catch(Exception ex){
					log.debug("��������(ROTTNALVYINFO)�ΰ����� ����� �����ϴ�. ��� ����");
				}				
				/***********************************************************************************/
				
			}

			elapseTime2 = System.currentTimeMillis();
			
			log.info("[" + c_slf_org_nm + "]�������ߺδ�� �ΰ��ڷῬ�� �ð�("+tot_size+") : " + CbUtil.formatTime(elapseTime2 - elapseTime1));
					
		} catch (Exception e) {
		    log.error("TXDM2423 ���� ���� = " + gblNidLevyRows.getMaps());
			throw (RuntimeException) e;
		}
		
		return tot_size;
	}
}
