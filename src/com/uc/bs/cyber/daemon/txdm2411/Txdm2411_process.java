/**
 *  �ֽý��۸� : �λ��û ���̹� ���漼û
 *  ��  ��  �� : ����
 *  ��  ��  �� : ǥ�ؼ��ܼ��� �ΰ��ڷῬ��
 *  Ŭ����  ID : Txdm2411_process : �� ó�������� �ۼ��ϴ� Ŭ����
 *  ����  �̷� :
 * ------------------------------------------------------------------------
 *  �ۼ���      	�Ҽ�  		    ����            Tag           ����
 * ------------------------------------------------------------------------
 *  �۵���       ��ä��(��)      2011.05.11         %01%         �����ۼ�
 *  ǥ����       �ٻ�ý���             2013.12.11         %02%         ���躯��
 */
package com.uc.bs.cyber.daemon.txdm2411;

import java.math.BigDecimal;
import java.util.ArrayList;

import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;

import com.uc.bs.cyber.CbUtil;
import com.uc.bs.cyber.daemon.Codm_BaseProcess;
import com.uc.bs.cyber.daemon.Codm_interface;
import com.uc.core.MapForm;
import com.uc.core.spring.service.UcContextHolder;

/**
 * @author Administrator
 * 20110919 ��
 * �ӵ��� �� ������ 
 * ����� �� �޸� �ؾ� ����...
 * �ϴ� ��üī���͸� ���ϰ� 
 * 100~1000������ �������� ������ 
 * �������� ������ŭ �Ѳ����� ó���ϵ��� Ʈ������� �����Ѵ�...
 */
public class Txdm2411_process extends Codm_BaseProcess implements Codm_interface {

	private MapForm dataMap            = null;
	private int insert_cnt = 0, update_cnt = 0, delete_cnt = 0, remote_up_cnt = 0;
    private int p_del_cnt = 0;
	
	/*Ʈ������� ���Ͽ� */
	MapForm gblNidLevyRows    = null;
	
	/**
	 * 
	 */
	public Txdm2411_process() {
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
		log.info("=[ǥ�ؼ��ܼ���-[" + c_slf_org_nm + "] �ΰ��ڷῬ��] Start =");
		log.info("=====================================================================");
		
		try {
				
			setContext(appContext);
			setApp(appContext);

			UcContextHolder.setCustomerType(this.dataSource);
						
			dataMap = new MapForm();  /*�ʱ�ȭ*/
			
			log.info("this.c_slf_org ======================= [" + this.c_slf_org + "]");

			dataMap.setMap("SGG",  this.c_slf_org);
			dataMap.setMap("BS_NOT_IN_SEMOK", not_in_semok());
			dataMap.setMap("BS_NOT_IN_SEQ", not_in_seq());

			long SEQ_MAX = 0L;
			try{
				SEQ_MAX = Long.parseLong(govService.getOneFieldString("TXDM2411.SELECT_SPNT200_SEQ_MAX", dataMap));
			}catch (Exception sub_e){
				log.error("["+this.c_slf_org+"] SELECT_SPNT200_SEQ_MAX ����");
				sub_e.printStackTrace();
			}						

			if(SEQ_MAX>0L){
				try{
					govService.queryForUpdate("TXDM2411.UPDATE_SPNT200_START", SEQ_MAX);
				}catch (Exception sub_e){
					log.error("["+this.c_slf_org+"] UPDATE_SPNT200_START ����");
					sub_e.printStackTrace();
				}						

				this.startJob();

				try{
					govService.queryForUpdate("TXDM2411.UPDATE_SPNT200_END", dataMap);
				}catch (Exception sub_e){
					log.error("["+this.c_slf_org+"]UPDATE_SPNT200_END ����");
					sub_e.printStackTrace();
				}						
			
			}else{
				
				/***********************************************************************************/
				try{
					MapForm daemonMap = new MapForm();
					daemonMap.setMap("DAEMON"    , "TXDM2411");
					daemonMap.setMap("DAEMON_NM" , "ǥ�ؼ��ܼ���(SPNT200)�ΰ��ڷῬ��");
					daemonMap.setMap("SGG_COD"   , c_slf_org);
					daemonMap.setMap("TOTAL_CNT" , 0);
					daemonMap.setMap("INSERT_CNT", 0);
					daemonMap.setMap("UPDATE_CNT", 0);
					daemonMap.setMap("DELETE_CNT", 0);
					daemonMap.setMap("ERROR_CNT" , 0);
					cyberService.queryForInsert("CODMBASE.InsertDaemonProcessCheck", daemonMap);
				}catch(Exception ex){
					log.debug("ǥ�ؼ��ܼ��� �ΰ��ڷῬ�� ����� �����ϴ�. ��� ����");
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
		txdm2411_JobProcess();
	
	}
	
	
    /*ǥ�ؼ��ܼ���...����*/
	private int txdm2411_JobProcess() {
		
		log.info("=====================================================================");
		log.info("=" + this.getClass().getName()+ " txdm2411_JobProcess()[ǥ�ؼ��ܼ���-[" + c_slf_org_nm + "] �ΰ��ڷῬ��] Start =");
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
		
		/*==============================================================*/
		/* ���ܼ��� �ΰ��ڷ� ��� (ó������ : 1 �ű�, 2 ����, 3 ����)
		 *  ó�������� 3�϶��� �����ϰ�
		 *  1 �̰ų� 2�϶��� INSERT�Ѵ�
		 *  INSERT �ϴ��߿� �ڷ� �ߺ��̵Ǹ� UPDATE
		 *  LIST EPAY_NO (���ڳ��ι�ȣ)�� ���ڳ��ι�ȣ�� ��´�.
		 */
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
				alNidLevyList =  govService.queryForList("TXDM2411.SELECT_SPNT200_LIST", dataMap);
			}catch (Exception sub_e){
				log.error("["+this.c_slf_org+"] SELECT_SPNT200_LIST ���� = " + gblNidLevyRows.getMaps());
				sub_e.printStackTrace();
			}						

			tot_size = alNidLevyList.size();
			
		    log.info("[" + c_slf_org_nm + "]���ܼ��Ժΰ������ڷ� �Ǽ� = [" + tot_size + "]");
			
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
/*
				    String REG_NO = (String) gblNidLevyRows.getMap("REG_NO");
				    String TAX_NO = (String) gblNidLevyRows.getMap("TAX_NO");
				    String EPAY_NO = (String) gblNidLevyRows.getMap("EPAY_NO");
				    String TAX_DIV = (String) gblNidLevyRows.getMap("TAX_DIV");
			        if(TRN_YN.equals("1")&&(REG_NO.trim().equals("")||TAX_NO.trim().equals("")||EPAY_NO.trim().equals("")||TAX_DIV.trim().equals("")))TRN_YN="X";
*/
				    String ACCT_COD = (String) gblNidLevyRows.getMap("ACCT_COD");
				    String TAX_ITEM = (String) gblNidLevyRows.getMap("TAX_ITEM");

			        long NATN_TAX = ((BigDecimal) gblNidLevyRows.getMap("NATN_TAX")).longValue();
			        long SDOTX = ((BigDecimal) gblNidLevyRows.getMap("SDOTX")).longValue();
			        long SGGTX = ((BigDecimal) gblNidLevyRows.getMap("SGGTX")).longValue();
			        long SDOTX_ADTX = ((BigDecimal) gblNidLevyRows.getMap("SDOTX_ADTX")).longValue();
			        long SGGTX_ADTX = ((BigDecimal) gblNidLevyRows.getMap("SGGTX_ADTX")).longValue();
					long VAT_AMT = ((BigDecimal) gblNidLevyRows.getMap("VAT_AMT")).longValue();
					long PAYMENT_DATE1 = ((BigDecimal) gblNidLevyRows.getMap("PAYMENT_DATE1")).longValue();
					long PAYMENT_DATE2 = ((BigDecimal) gblNidLevyRows.getMap("PAYMENT_DATE2")).longValue();
					long PAYMENT_DATE3 = ((BigDecimal) gblNidLevyRows.getMap("PAYMENT_DATE3")).longValue();
					long AFTPAYMENT_DATE1 = ((BigDecimal) gblNidLevyRows.getMap("AFTPAYMENT_DATE1")).longValue();
					long AFTPAYMENT_DATE2 = ((BigDecimal) gblNidLevyRows.getMap("AFTPAYMENT_DATE2")).longValue();
					long AFTPAYMENT_DATE3 = ((BigDecimal) gblNidLevyRows.getMap("AFTPAYMENT_DATE3")).longValue();
			        long OCR_NATN_TAX = ((BigDecimal) gblNidLevyRows.getMap("OCR_NATN_TAX")).longValue();
			        long OCR_SIDO_TAX = ((BigDecimal) gblNidLevyRows.getMap("OCR_SIDO_TAX")).longValue();
			        long OCR_SIGUNGU_TAX = ((BigDecimal) gblNidLevyRows.getMap("OCR_SIGUNGU_TAX")).longValue();
					long OCR_PAYMENT_DATE1 = ((BigDecimal) gblNidLevyRows.getMap("OCR_PAYMENT_DATE1")).longValue();
					long OCR_AFTPAYMENT_DATE1 = ((BigDecimal) gblNidLevyRows.getMap("OCR_AFTPAYMENT_DATE1")).longValue();
			        
					// ����Ǹ� �ȵǴ� �ڷ�
			        if(TRN_YN.equals("1")&&(ACCT_COD.equals("16")||NATN_TAX>0L||((ACCT_COD.equals("31")||ACCT_COD.equals("51"))&&(SGGTX+SGGTX_ADTX)>0L)||((ACCT_COD.equals("41")||ACCT_COD.equals("61"))&&(SDOTX+SDOTX_ADTX)>0L)))TRN_YN="X";
			        if(TRN_YN.equals("1")&&(not_in_semok_check(dataMap, (ACCT_COD+TAX_ITEM))))TRN_YN="X";
			        if(TRN_YN.equals("1")&&(
			        		NATN_TAX%10L>0L||SDOTX%10L>0L||SGGTX%10L>0L||SDOTX_ADTX%10L>0L||SGGTX_ADTX%10L>0L||VAT_AMT%10L>0L||
			        		PAYMENT_DATE1%10L>0L||PAYMENT_DATE2%10L>0L||PAYMENT_DATE3%10L>0L||AFTPAYMENT_DATE1%10L>0L||AFTPAYMENT_DATE2%10L>0L||AFTPAYMENT_DATE3%10L>0L||
			        		OCR_NATN_TAX%10L>0L||OCR_SIDO_TAX%10L>0L||OCR_SIGUNGU_TAX%10L>0L||OCR_PAYMENT_DATE1%10L>0L||OCR_AFTPAYMENT_DATE1%10L>0L
			        		))TRN_YN="X";
			        
					if(TRN_YN.equals("1")){
						
						// ���ڳ��ι�ȣ �ߺ��� Ȯ�� �� ����
						int TX2111_CNT_EPAY_NO = cyberService.getOneFieldInteger("TXDM2411.SELECT_TX2111_CNT_EPAY_NO", gblNidLevyRows);
						int TX2211_CNT_EPAY_NO = cyberService.getOneFieldInteger("TXDM2411.SELECT_TX2211_CNT_EPAY_NO", gblNidLevyRows);
						int TX2111_CNT_TAX_NO = cyberService.getOneFieldInteger("TXDM2411.SELECT_TX2111_CNT_TAX_NO", gblNidLevyRows);
						int TX2112_CNT_TAX_NO = cyberService.getOneFieldInteger("TXDM2411.SELECT_TX2112_CNT_TAX_NO", gblNidLevyRows);
						int TX2211_CNT_TAX_NO = cyberService.getOneFieldInteger("TXDM2411.SELECT_TX2211_CNT_TAX_NO", gblNidLevyRows);
						if(TX2111_CNT_EPAY_NO>1)TRN_YN="W";

						// �ű�, ����, ���� ���� ����
						String CUD_OPT = (String) gblNidLevyRows.getMap("CUD_OPT");
						String v_cud="0";

						if(v_cud.equals("0")&&TX2111_CNT_EPAY_NO>0){
							if(v_cud.equals("0")&&TX2211_CNT_EPAY_NO>0)v_cud="0";
							else v_cud="2";
						}
						else v_cud="1";
						if((!v_cud.equals("0"))&&(CUD_OPT.equals("3")||PAYMENT_DATE1+AFTPAYMENT_DATE1==0L))v_cud="3";

						// �ű�, ����, ���� ���п� ���� ó�� 
						if(TRN_YN.equals("1")&&v_cud.equals("1")){

							if(TX2211_CNT_TAX_NO>0){
								TRN_YN="X";
							}else{
								if(TX2111_CNT_TAX_NO>0){
									try{
										cyberService.queryForUpdate("TXDM2411.UPDATE_TX2111_TAX_NO", gblNidLevyRows);
									}catch (Exception sub_e){
										TRN_YN="E";
										log.error("["+this.c_slf_org+"] UPDATE_TX2111_TAX_NO ���� = " + gblNidLevyRows.getMaps());
										sub_e.printStackTrace();
									}

									try{
										cyberService.queryForUpdate("TXDM2411.UPDATE_TX2112_TAX_NO", gblNidLevyRows);
										update_cnt++;
									}catch (Exception sub_e){
										TRN_YN="E";
										log.error("["+this.c_slf_org+"] UPDATE_TX2112_TAX_NO ���� = " + gblNidLevyRows.getMaps());
										sub_e.printStackTrace();
									}
								}else{
									try{
										cyberService.queryForUpdate("TXDM2411.INSERT_TX2111", gblNidLevyRows);
									}catch (Exception sub_e){
										TRN_YN="E";
										log.error("["+this.c_slf_org+"] INSERT_TX2111 ���� = " + gblNidLevyRows.getMaps());
										sub_e.printStackTrace();
									}						

									if(TX2112_CNT_TAX_NO>0){
										try{
											cyberService.queryForUpdate("TXDM2411.UPDATE_TX2112_TAX_NO", gblNidLevyRows);
											update_cnt++;
										}catch (Exception sub_e){
											TRN_YN="E";
											log.error("["+this.c_slf_org+"] UPDATE_TX2112_TAX_NO ���� = " + gblNidLevyRows.getMaps());
											sub_e.printStackTrace();
										}
									}else{
										try{
											cyberService.queryForUpdate("TXDM2411.INSERT_TX2112", gblNidLevyRows);
											insert_cnt++;
										}catch (Exception sub_e){
											TRN_YN="E";
											log.error("["+this.c_slf_org+"] INSERT_TX2112 ���� = " + gblNidLevyRows.getMaps());
											sub_e.printStackTrace();
										}						
									}
									
								}
							}
						}

						if(TRN_YN.equals("1")&&(v_cud.equals("2")||v_cud.equals("3"))){
							gblNidLevyRows.setMap("V_TAX_NO",cyberService.getOneFieldString("TXDM2411.SELECT_TX2111_TAX_NO", gblNidLevyRows));
						}

						if(TRN_YN.equals("1")&&v_cud.equals("2")){
							try{
								cyberService.queryForUpdate("TXDM2411.UPDATE_TX2111_EPAY_NO", gblNidLevyRows);
							}catch (Exception sub_e){
								TRN_YN="E";
								log.error("["+this.c_slf_org+"] UPDATE_TX2111_EPAY_NO ���� = " + gblNidLevyRows.getMaps());
								sub_e.printStackTrace();
							}

							try{
								cyberService.queryForUpdate("TXDM2411.UPDATE_TX2112_EPAY_NO", gblNidLevyRows);
								update_cnt++;
							}catch (Exception sub_e){
								TRN_YN="E";
								log.error("["+this.c_slf_org+"] UPDATE_TX2112_EPAY_NO ���� = " + gblNidLevyRows.getMaps());
								sub_e.printStackTrace();
							}
						}

						if(TRN_YN.equals("1")&&v_cud.equals("3")&&TX2111_CNT_EPAY_NO>0){
							try{
								if (cyberService.queryForDelete("TXDM2411.UPDATE_TX2112_DEL_YN", gblNidLevyRows) > 0) {
									delete_cnt++;
								}
							}catch (Exception sub_e){
								TRN_YN="E";
								log.error("["+this.c_slf_org+"] UPDATE_TX2112_DEL_YN ���� = " + gblNidLevyRows.getMaps());
								sub_e.printStackTrace();
							}						
						}
					}
					gblNidLevyRows.setMap("TRN_YN",TRN_YN);
					
					try{
						/*�������� ������Ʈ*/
						remote_up_cnt +=govService.queryForUpdate("TXDM2411.UPDATE_SPNT200_TRN_YN", gblNidLevyRows);
					}catch (Exception sub_e){
						log.error("["+this.c_slf_org+"] UPDATE_SPNT200_TRN_YN ���� = " + gblNidLevyRows.getMaps());
						try{
							cyberService.queryForUpdate("TXDM2411.INSERT_SP_TX2111_NOT_SEQ", gblNidLevyRows);
						}catch (Exception sub_e2){
							log.error("["+this.c_slf_org+"] INSERT_SP_TX2111_NOT_SEQ ���� = " + gblNidLevyRows.getMaps());
							sub_e2.printStackTrace();
						}
					}	
					
					if(rec_cnt % 500 == 0) {
						log.info("[" + this.c_slf_org_nm + "][" + this.c_slf_org + "]���ܼ��Ժΰ��ڷ�ó���߰Ǽ� = [" + rec_cnt + "]");
					}
				}
				
				log.info("[" + c_slf_org_nm + "]���ܼ��Ժΰ��ڷῬ�� �Ǽ� [" + rec_cnt + "] ��ϰǼ� [" + insert_cnt +"] �����Ǽ� [" + update_cnt + "] �����Ǽ� [" + delete_cnt + "] ���������� [" + p_del_cnt + "]");
				log.info("�������������Ʈ ["+remote_up_cnt+"], ��ó�� �ΰ��Ǽ� [" + nul_cnt + "]");
				
				/***********************************************************************************/
				try{
					MapForm daemonMap = new MapForm();
					daemonMap.setMap("DAEMON"    , "TXDM2411");
					daemonMap.setMap("DAEMON_NM" , "ǥ�ؼ��ܼ���(SPNT200)�ΰ��ڷῬ��");
					daemonMap.setMap("SGG_COD"   , c_slf_org);
					daemonMap.setMap("TOTAL_CNT" , rec_cnt);
					daemonMap.setMap("INSERT_CNT", insert_cnt);
					daemonMap.setMap("UPDATE_CNT", update_cnt);
					daemonMap.setMap("DELETE_CNT", delete_cnt);
					daemonMap.setMap("ERROR_CNT" , 0);
					cyberService.queryForInsert("CODMBASE.InsertDaemonProcessCheck", daemonMap);
				}catch(Exception ex){
					log.debug("ǥ�ؼ��ܼ��� �ΰ��ڷῬ�� �α� ��� ����");
				}				
				/***********************************************************************************/
				
			}

			elapseTime2 = System.currentTimeMillis();
			
			log.info("[" + c_slf_org_nm + "]���ܼ��� �ΰ��ڷῬ�� �ð�("+tot_size+") : " + CbUtil.formatTime(elapseTime2 - elapseTime1));
					
		} catch (Exception e) {
		    log.error("["+this.c_slf_org+"] TXDM2411 ���� ���� = " + gblNidLevyRows.getMaps());
			throw (RuntimeException) e;
		}
		
		return tot_size;
	}

	
	/*���ܼ��Կ��� ���ܽ��Ѿ� �� ����*/
	private String not_in_semok() {
	
		StringBuffer sb = new StringBuffer();
		String Retval = "";
		try {
	
			ArrayList<MapForm> alNoSemokList =  cyberService.queryForList("CODMBASE.NOSEOI", null);
			
			if (alNoSemokList.size()  >  0)   {
				
				for ( int rec_cnt = 0;  rec_cnt < alNoSemokList.size();  rec_cnt++)   {
					
					MapForm mpNoSemokList =  alNoSemokList.get(rec_cnt);
					
					/*Ȥ�ó�...because of testing */
					if (mpNoSemokList == null  ||  mpNoSemokList.isEmpty() )   {
						continue;
					}
					
					sb.append("'").append(mpNoSemokList.getMap("SEMOK")).append("'").append(",");

				}
				
				Retval = sb.toString().substring(0, sb.toString().length() -1);
				
			} 
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	
		return Retval;
	}

	
	/*���ܼ��Կ��� ���ܽ��Ѿ� �� SEQ*/
	private String not_in_seq() {
	
		StringBuffer sb = new StringBuffer();
		String Retval = "";
		try {
	
			ArrayList<MapForm> alNoSemokList =  cyberService.queryForList("TXDM2411.SELECT_SP_TX2111_NOT_SEQ", dataMap);
			
			if (alNoSemokList.size()  >  0)   {
				
				for ( int rec_cnt = 0;  rec_cnt < alNoSemokList.size();  rec_cnt++)   {
					
					MapForm mpNoSemokList =  alNoSemokList.get(rec_cnt);
					
					/*Ȥ�ó�...because of testing */
					if (mpNoSemokList == null  ||  mpNoSemokList.isEmpty() )   {
						continue;
					}
					
					sb.append(mpNoSemokList.getMap("NOTSEQ")).append(",");

				}
				
				Retval = sb.toString().substring(0, sb.toString().length() -1);
				
			} 
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	
		return Retval;
	}

	
	/*���ܼ��Կ��� �����ϴ� ���� �˻�*/
	private boolean not_in_semok_check(MapForm dataMap, String taxItem) {

		boolean rtn=false;
		String semokList = (String)dataMap.getMap("BS_NOT_IN_SEMOK");
		semokList=semokList.replaceAll("'","");
		String semokArr[] = semokList.split(",");

		for(int i=0;i<semokArr.length;i++){
			if(semokArr[i].equals(taxItem.substring(2,5))||semokArr[i].equals(taxItem)){
				rtn=true;
				break;
			}
		}
		
		if(Integer.parseInt(taxItem.substring(2,5))<201||Integer.parseInt(taxItem.substring(2,5))>=300)rtn=true;
		if(taxItem.substring(2,8).equals("202099"))rtn=false;

		return rtn;
	}
}
