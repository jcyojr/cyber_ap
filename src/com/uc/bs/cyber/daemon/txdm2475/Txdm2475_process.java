/**
 *  �ֽý��۸� : �λ��û ���̹� ���漼û
 *  ��  ��  �� : ����
 *  ��  ��  �� : ���������ݰ��·� �ΰ��ڷῬ��
 *  Ŭ����  ID : Txdm2475_process : �� ó�������� �ۼ��ϴ� Ŭ����
 *  ����  �̷� :
 * ------------------------------------------------------------------------
 *  �ۼ���      	�Ҽ�  		    ����            Tag           ����
 * ------------------------------------------------------------------------
 *  �۵���       ��ä��(��)      2011.05.11         %01%         �����ۼ�
 */
package com.uc.bs.cyber.daemon.txdm2475;

import java.math.BigDecimal;
import java.util.ArrayList;

import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;

import com.uc.bs.cyber.CbUtil;
import com.uc.bs.cyber.daemon.Codm_BaseProcess;
import com.uc.bs.cyber.daemon.Codm_interface;
import com.uc.core.MapForm;
import com.uc.core.spring.service.UcContextHolder;

public class Txdm2475_process extends Codm_BaseProcess implements Codm_interface {

	private MapForm dataMap            = null;
	private int insert_cnt = 0, update_cnt = 0, delete_cnt = 0, remote_up_cnt = 0;
    private int p_del_cnt = 0;
	
	/*Ʈ������� ���Ͽ� */
	MapForm gblNidLevyRows    = null;
	
	/**
	 * 
	 */
	public Txdm2475_process() {
		// TODO Auto-generated constructor stub
		super();
		
		log = LogFactory.getLog(this.getClass());
		
		/**
		 * 3 �и��� ����
		 */
		loopTerm = 180;
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
	private void mainTransProcess() {

		log.info("=====================================================================");
		log.info("=[���������ݰ��·�-[" + c_slf_org_nm + "] �ΰ��ڷῬ��] Start =");
		log.info("=====================================================================");

		try {

			setContext(appContext);
			setApp(appContext);

			UcContextHolder.setCustomerType(this.dataSource);

			dataMap = new MapForm(); /* �ʱ�ȭ */

			log.info("this.c_slf_org ======================= ["+ this.c_slf_org +"]");

			dataMap.setMap("SGG_COD", this.c_slf_org);
			dataMap.setMap("BS_NOT_IN_SEQ", not_in_seq());

			int CNT = 0;
			try {
				CNT = govService.getOneFieldInteger("TXDM2475.SELECT_ROAD_CNT",	dataMap);
			} catch (Exception sub_e) {
				log.error("[" + this.c_slf_org + "] SELECT_ROAD_CNT ����");
				CNT = -1;
				sub_e.printStackTrace();
			}

			if (CNT > 0) {
				try {
					govService.queryForUpdate("TXDM2475.UPDATE_ROAD_START",	dataMap);
				} catch (Exception sub_e) {
					log.error("[" + this.c_slf_org + "] UPDATE_ROAD_START ����");
					sub_e.printStackTrace();
				}

				this.startJob();

				try {
					govService.queryForUpdate("TXDM2475.UPDATE_ROAD_END", dataMap);
				} catch (Exception sub_e) {
					log.error("[" + this.c_slf_org + "] UPDATE_ROAD_END ����");
					sub_e.printStackTrace();
				}
			
			}else if(CNT == 0){
				
				/********************************* ��û�� ó�� �α� ��� **************************************************/
				try{
					MapForm daemonMap = new MapForm();
					daemonMap.setMap("DAEMON"    , "TXDM2475");
					daemonMap.setMap("DAEMON_NM" , "���������ݰ��·� ����");
					daemonMap.setMap("SGG_COD"   , c_slf_org);
					daemonMap.setMap("TOTAL_CNT" , CNT);
					daemonMap.setMap("INSERT_CNT", 0);
					daemonMap.setMap("UPDATE_CNT", 0);
					daemonMap.setMap("DELETE_CNT", 0);
					daemonMap.setMap("ERROR_CNT" , 0);
					cyberService.queryForInsert("CODMBASE.InsertDaemonProcessCheck", daemonMap);
				}catch(Exception ex){
					log.info("��û�� ó�� �α� ��� ����");
				}				
				/**********************************************************************************************************/
			
			}else{
				log.info("[" + c_slf_org_nm + "]���������ݰ��·� �����ڷ� ��ȸ ���� !!!!!!!!!!!!!");
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
		txdm2475_JobProcess();
	
	}
	
    /*���������ݰ��·�...����*/
	private int txdm2475_JobProcess() {
		
		log.info("=====================================================================");
		log.info("=" + this.getClass().getName()+ " txdm2475_JobProcess()[���������ݰ��·�-[" + c_slf_org_nm + "] �ΰ��ڷῬ��] Start =");
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

			try {
				alNidLevyList = govService.queryForList("TXDM2475.SELECT_ROAD_LIST", dataMap);
			} catch (Exception sub_e) {
				log.error("[" + this.c_slf_org + "] SELECT_ROAD_LIST ���������� = " + gblNidLevyRows.getMaps());
				sub_e.printStackTrace();
			}	
			
			try{				
				cyberService.queryForUpdate("TXDM2475.DEL_EMPTY_DONE", dataMap);
				log.debug("[" + this.c_slf_org + "]  DEL_EMPTY_DONE = " + alNidLevyList );
			} catch (Exception sub_e) {
				sub_e.printStackTrace();
			}

			tot_size = alNidLevyList.size();
			
		    log.info("[" + c_slf_org_nm + "]���������ݰ��·� �����ڷ� �Ǽ� = [" + tot_size + "]");
			
			if (tot_size > 0) {
				
				/*���ڳ����ڷ� 1�Ǿ� fetch ó�� */
				for ( rec_cnt = 0;  rec_cnt < tot_size;  rec_cnt++) {
					
					gblNidLevyRows =  alNidLevyList.get(rec_cnt);
					
					/*Ȥ�ó�...because of testing */
					if (gblNidLevyRows == null || gblNidLevyRows.isEmpty()) {
						nul_cnt++;
						continue;
					}
					
				    String TRN_YN = "1";
				    String WETAX_YN = (String) gblNidLevyRows.getMap("WETAX_YN");
				    String THAP_GBN = (String) gblNidLevyRows.getMap("THAP_GBN");
				    String DIVIDED_PAYMENT_SEQNUM = (String) gblNidLevyRows.getMap("DIVIDED_PAYMENT_SEQNUM");
				    
					long SIGUNGU_TAX = ((BigDecimal) gblNidLevyRows.getMap("SIGUNGU_TAX")).longValue();
					long PAYMENT_DATE1 = ((BigDecimal) gblNidLevyRows.getMap("PAYMENT_DATE1")).longValue();
					long AFTPAYMENT_DATE1 = ((BigDecimal) gblNidLevyRows.getMap("AFTPAYMENT_DATE1")).longValue();
			        
					// ����Ǹ� �ȵǴ� �ڷ�
					if (TRN_YN.equals("1") && !THAP_GBN.equals("02")) {
						TRN_YN = "X";
					}
					if (TRN_YN.equals("1") && !(DIVIDED_PAYMENT_SEQNUM.equals("00") || DIVIDED_PAYMENT_SEQNUM.equals("98"))) {
						TRN_YN = "X";
					}
					if (TRN_YN.equals("1") && (SIGUNGU_TAX % 10L > 0L || PAYMENT_DATE1 % 10L > 0L || AFTPAYMENT_DATE1 % 10L > 0L)) {
						TRN_YN = "E";
					}

					// �ű�, ����, ���� ���� ����
					String V_DEL_YN="N";
				    String DPOSL_STAT = (String) gblNidLevyRows.getMap("DPOSL_STAT");
				    String BNON_SNTG = (String) gblNidLevyRows.getMap("BNON_SNTG");
				    String CUD_OPT = (String) gblNidLevyRows.getMap("CUD_OPT");
				    String EPAY_NO = (String) gblNidLevyRows.getMap("EPAY_NO");
				    String DUE_DT = (String) gblNidLevyRows.getMap("DUE_DT");
				    String REG_NO = (String) gblNidLevyRows.getMap("REG_NO");
				    
				    if (TRN_YN.equals("1") 
				    		&& (WETAX_YN.equals("N")) 
				    		&& (DPOSL_STAT.equals("2") 
				    				|| BNON_SNTG.equals("D") 
				    				|| BNON_SNTG.equals("S") 
				    				|| CUD_OPT.equals("3") 
				    				|| SIGUNGU_TAX == 0L 
				    				|| (PAYMENT_DATE1 + AFTPAYMENT_DATE1) == 0L)) {
						V_DEL_YN = "Y";
					
						try {
							if (cyberService.getOneFieldInteger("TXDM2475.SELECT_TX2112_DEL_YN_EPAY_NO_CNT", gblNidLevyRows) > 0) {
								if (cyberService.queryForUpdate("TXDM2475.UPDATE_TX2112_DEL_YN_EPAY_NO", gblNidLevyRows) > 0) {
									delete_cnt++;
								}
							}
						} catch (Exception sub_e) {
							TRN_YN = "E";
							log.error("[" + this.c_slf_org + "] UPDATE_TX2112_DEL_YN_EPAY_NO ���� = " + gblNidLevyRows.getMaps());
							sub_e.printStackTrace();
						}
					    
						gblNidLevyRows.setMap("TRN_YN",TRN_YN);				    
				    
				    }
				    
				    

					
					if (TRN_YN.equals("1")
							&& WETAX_YN.equals("N")
							&& V_DEL_YN.equals("N")
							&& (EPAY_NO.equals(" ")
									|| EPAY_NO.equals("0000000000000000000")
									|| DUE_DT.equals(" ") || REG_NO.equals(" "))) {
						TRN_YN = "E";
					}
					
					if (TRN_YN.equals("1")) {
						gblNidLevyRows.setMap("PROC_CLS", "1");
						gblNidLevyRows.setMap("VIR_ACC_NO", "");
						gblNidLevyRows.setMap("SGG_TR_TG", "0");
						gblNidLevyRows.setMap("DEL_YN", V_DEL_YN);
						if (WETAX_YN.equals("Y")) {
							gblNidLevyRows.setMap("DEL_YN", "Y");
							gblNidLevyRows.setMap("SNTG", "2");
						}

						String V_CUD = "";

						if (WETAX_YN.equals("Y")) {
							int TX2211_CNT_TAX_NO_WETAX = cyberService.getOneFieldInteger("TXDM2475.SELECT_TX2211_CNT_TAX_NO_WETAX", gblNidLevyRows);
							if (TX2211_CNT_TAX_NO_WETAX == 0) {
								int TX2211_MAX_PAY_CNT = cyberService.getOneFieldInteger("TXDM2475.SELECT_TX2211_MAX_PAY_CNT", gblNidLevyRows);
								gblNidLevyRows.setMap("PAY_CNT",TX2211_MAX_PAY_CNT);
								String TX2112_OCR_BD = cyberService.getOneFieldString("TXDM2475.SELECT_TX2112_OCR_BD", gblNidLevyRows);
								gblNidLevyRows.setMap("OCR_BD", TX2112_OCR_BD);
								try {
									cyberService.queryForUpdate("TXDM2475.INSERT_TX2211", gblNidLevyRows);
								} catch (Exception sub_e) {
									TRN_YN = "E";
									log.error("[" + this.c_slf_org + "] INSERT_TX2211 ���� = " + gblNidLevyRows.getMaps());
									sub_e.printStackTrace();
								}

								if (TRN_YN.equals("1")) {
									try {
										cyberService.queryForUpdate("TXDM2475.UPDATE_TX2112_DEL_YN_WETAX", gblNidLevyRows);
									} catch (Exception sub_e) {
										TRN_YN = "E";
										log.error("[" + this.c_slf_org + "] UPDATE_TX2112_DEL_YN_WETAX ���� = " + gblNidLevyRows.getMaps());
										sub_e.printStackTrace();
									}
								}
							}
						} else {
							if (WETAX_YN.equals("C")){
								if (cyberService.getOneFieldInteger("TXDM2475.SELECT_TX2211_CNT_TAX_NO_EPAY_NO", gblNidLevyRows)>0){
									gblNidLevyRows.setMap("MAX_PAY_CNT_EPAY_NO", cyberService.getOneFieldInteger("TXDM2475.SELECT_TX2211_MAX_PAY_CNT_EPAY_NO", gblNidLevyRows));

									try {
										cyberService.queryForUpdate("TXDM2475.UPDATE_TX2211_SNTG", gblNidLevyRows);
									} catch (Exception sub_e) {
										TRN_YN = "E";
										log.error("[" + this.c_slf_org + "] UPDATE_TX2211_SNTG ���� = " + gblNidLevyRows.getMaps());
										sub_e.printStackTrace();
									}

									if (cyberService.getOneFieldInteger("TXDM2475.SELECT_TX2211_CNT_TAX_NO", gblNidLevyRows)==0){
										try {
											cyberService.queryForUpdate("TXDM2475.UPDATE_TX2112_SNTG", gblNidLevyRows);
										} catch (Exception sub_e) {
											TRN_YN = "E";
											log.error("[" + this.c_slf_org + "] UPDATE_TX2112_SNTG ���� = " + gblNidLevyRows.getMaps());
											sub_e.printStackTrace();
										}
									}
								} 
							}
							
							int TX2111_CNT_TAX_NO = cyberService.getOneFieldInteger("TXDM2475.SELECT_TX2111_CNT_TAX_NO", gblNidLevyRows);
							int TX2211_CNT_TAX_NO = cyberService.getOneFieldInteger("TXDM2475.SELECT_TX2211_CNT_TAX_NO", gblNidLevyRows);

							if (TX2111_CNT_TAX_NO == 0) {
								if (V_DEL_YN.equals("Y")) {
									if (cyberService.getOneFieldInteger("TXDM2475.SELECT_TX2112_CNT_LEVY_DETAIL", gblNidLevyRows) > 0) {
										V_CUD = "L";
									}
								} else {
									V_CUD = "I";
								}
							} else {
								if (V_DEL_YN.equals("Y")) {
									if (TX2211_CNT_TAX_NO == 0) {
										V_CUD = "D";
									}
								} else {
									if (TX2211_CNT_TAX_NO == 0) {
										V_CUD = "U";
									} else {
										TRN_YN = "E";
									}
								}
							}

							if (V_CUD.equals("I")) {
								try {
									cyberService.queryForUpdate("TXDM2475.INSERT_TX2111", gblNidLevyRows);
								} catch (Exception sub_e) {
									TRN_YN = "E";
									log.error("[" + this.c_slf_org + "] INSERT_TX2111 ���� = " + gblNidLevyRows.getMaps());
									sub_e.printStackTrace();
								}

								try {
									cyberService.queryForUpdate("TXDM2475.INSERT_TX2112", gblNidLevyRows);
								} catch (Exception sub_e) {
									TRN_YN = "E";
									log.error("[" + this.c_slf_org + "] INSERT_TX2112 ���� = " + gblNidLevyRows.getMaps());
									sub_e.printStackTrace();
								}
								insert_cnt++;
							}

							if (V_CUD.equals("U")) {

								String vir_no = "";

								try {
									cyberService.queryForInsert("TXDM2475.INSERT_TX2115", gblNidLevyRows);
								} catch (Exception sub_e) {
									TRN_YN = "E";
									log.error("[" + this.c_slf_org + "] INSERT_TX2115 ���� = " + gblNidLevyRows.getMaps());
									sub_e.printStackTrace();
								}

								// ������ȣ,�����Ͻ� ����� ������¹�ȣ ������ ä���Ͽ� ������
								try {
									vir_no = cyberService.getOneFieldString("TXDM2475.getVirAccNoByTaxNoKey", gblNidLevyRows);
								} catch (Exception sub_e) {
									TRN_YN = "E";
									log.error("getVirAccNoByTaxNoKey ���� = " + gblNidLevyRows.getMaps());
									sub_e.printStackTrace();
								}
								gblNidLevyRows.setMap("VIR_NO", vir_no);

								try {
									cyberService.queryForUpdate("TXDM2475.UPDATE_TX2111_TAX_NO", gblNidLevyRows);
								} catch (Exception sub_e) {
									TRN_YN = "E";
									log.error("[" + this.c_slf_org + "] UPDATE_TX2111_TAX_NO ���� = " + gblNidLevyRows.getMaps());
									sub_e.printStackTrace();
								}

								try {
									cyberService.queryForUpdate("TXDM2475.UPDATE_TX2112_TAX_NO", gblNidLevyRows);
								} catch (Exception sub_e) {
									TRN_YN = "E";
									log.error("[" + this.c_slf_org + "] UPDATE_TX2112_TAX_NO ���� = " + gblNidLevyRows.getMaps());
									sub_e.printStackTrace();
								}
								update_cnt++;
							}

							if (V_CUD.equals("D")) {
								try {
									if (cyberService.queryForUpdate("TXDM2475.UPDATE_TX2112_DEL_YN", gblNidLevyRows) > 0) {
										delete_cnt++;
									}
								} catch (Exception sub_e) {
									TRN_YN = "E";
									log.error("[" + this.c_slf_org + "] UPDATE_TX2112_DEL_YN ���� = " + gblNidLevyRows.getMaps());
									sub_e.printStackTrace();
								}
							}

							if (V_CUD.equals("L")) {
								try {
									if (cyberService.getOneFieldInteger("TXDM2475.SELECT_TX2112_DEL_YN_LEVY_DETAIL_CNT", gblNidLevyRows) > 0) {
										if (cyberService.queryForUpdate("TXDM2475.UPDATE_TX2112_DEL_YN_LEVY_DETAIL", gblNidLevyRows) > 0) {
											delete_cnt++;
										}
									}
								} catch (Exception sub_e) {
									TRN_YN = "E";
									log.error("[" + this.c_slf_org + "] UPDATE_TX2112_DEL_YN_LEVY_DETAIL ���� = " + gblNidLevyRows.getMaps());
									sub_e.printStackTrace();
								}
							}
						}
					}

					gblNidLevyRows.setMap("TRN_YN",TRN_YN);
					
					try {
						/* �������� ������Ʈ */
						remote_up_cnt += govService.queryForUpdate("TXDM2475.UPDATE_ROAD_TRN_YN", gblNidLevyRows);
					} catch (Exception sub_e) {
						log.error("[" + this.c_slf_org + "] UPDATE_ROAD_TRN_YN ���� = " + gblNidLevyRows.getMaps());
						sub_e.printStackTrace();
					}
					
					if (rec_cnt % 500 == 0) {
						log.info("[" + this.c_slf_org_nm + "][" + this.c_slf_org + "]���������ݰ��·� �ڷ�ó���߰Ǽ� = [" + rec_cnt + "]");
					}
				}
				
				log.info("[" + c_slf_org_nm + "]���������ݰ��·� �ΰ��ڷῬ�� �Ǽ� [" + rec_cnt + "] ��ϰǼ� [" + insert_cnt +"] �����Ǽ� [" + update_cnt + "] �����Ǽ� [" + delete_cnt + "] ���������� [" + p_del_cnt + "]");
				log.info("�������������Ʈ ["+remote_up_cnt+"], ��ó�� �ΰ��Ǽ� [" + nul_cnt + "]");
				
				
				/********************************* ��û�� ó�� �α� ��� **************************************************/
				try{
					MapForm daemonMap = new MapForm();
					daemonMap.setMap("DAEMON"    , "TXDM2475");
					daemonMap.setMap("DAEMON_NM" , "���������ݰ��·� ����");
					daemonMap.setMap("SGG_COD"   , c_slf_org);
					daemonMap.setMap("TOTAL_CNT" , rec_cnt);
					daemonMap.setMap("INSERT_CNT", insert_cnt);
					daemonMap.setMap("UPDATE_CNT", update_cnt);
					daemonMap.setMap("DELETE_CNT", delete_cnt);
					daemonMap.setMap("ERROR_CNT" , 0);
					cyberService.queryForInsert("CODMBASE.InsertDaemonProcessCheck", daemonMap);
				}catch(Exception ex){
					log.info("��û�� ó�� �α� ��� ����");
				}				
				/**********************************************************************************************************/
				
			}

			elapseTime2 = System.currentTimeMillis();
			
			log.info("[" + c_slf_org_nm + "]���������ݰ��·� �ΰ��ڷῬ�� �ð�(" + tot_size + ") : " + CbUtil.formatTime(elapseTime2 - elapseTime1));
					
		} catch (Exception e) {
		    log.error("["+this.c_slf_org+"] TXDM2475 ���� ���� = " + gblNidLevyRows.getMaps());
			throw (RuntimeException) e;
		}
		
		return tot_size;
	}

	/*���ܼ��Կ��� ���ܽ��Ѿ� �� SEQ*/
	private String not_in_seq(){
	
		StringBuffer sb = new StringBuffer();
		String rt = "";
		try {
	
			ArrayList<MapForm> al =  cyberService.queryForList("TXDM2475.SELECT_SP_TX2111_NOT_SEQ", dataMap);
			
			if (al.size()>0){
				sb.append("'");
				for (int i=0;i<al.size();i++){
					
					MapForm mf =  al.get(i);
					if(mf==null||mf.isEmpty())continue;
					
					sb.append(mf.getMap("NOTSEQ")).append("','");
				}
				
				rt = sb.toString().substring(0, sb.toString().length() -2);
			} 
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	
		return rt;
	}

}
