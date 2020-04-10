/**
 *  �ֽý��۸� : �λ��û ���̹� ���漼û
 *  ��  ��  �� : ����
 *  ��  ��  �� : ���������ݰ��·� �ΰ��ڷῬ��
 *  Ŭ����  ID : Txdm2473_process : �� ó�������� �ۼ��ϴ� Ŭ����
 *  ����  �̷� :
 * ------------------------------------------------------------------------
 *  �ۼ���      	�Ҽ�  		    ����            Tag           ����
 * ------------------------------------------------------------------------
 *  �۵���       ��ä��(��)      2011.05.11         %01%         �����ۼ�
 */
package com.uc.bs.cyber.daemon.txdm2473;

import java.math.BigDecimal;
import java.util.ArrayList;

import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;

import com.uc.bs.cyber.CbUtil;
import com.uc.bs.cyber.daemon.Codm_BaseProcess;
import com.uc.bs.cyber.daemon.Codm_interface;
import com.uc.core.MapForm;
import com.uc.core.spring.service.UcContextHolder;

public class Txdm2473_process extends Codm_BaseProcess implements Codm_interface {

	private MapForm dataMap            = null;
	private int insert_cnt = 0, update_cnt = 0, delete_cnt = 0, remote_up_cnt = 0;
    private int p_del_cnt = 0;
	
	/*Ʈ������� ���Ͽ� */
	MapForm gblNidLevyRows    = null;
	
	/**
	 * 
	 */
	public Txdm2473_process() {
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
		log.info("=[���������ݰ��·�-[" + c_slf_org_nm + "] �ΰ��ڷῬ��] Start =");
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
				CNT = govService.getOneFieldInteger("TXDM2473.SELECT_ROAD_CNT", dataMap);
			}catch (Exception sub_e){
				log.error("SELECT_ROAD_CNT ����");
				sub_e.printStackTrace();
			}						

			if(CNT>0){
				try{
					govService.queryForUpdate("TXDM2473.UPDATE_ROAD_START", dataMap);
				}catch (Exception sub_e){
					log.error("UPDATE_ROAD_START ����");
					sub_e.printStackTrace();
				}						

				this.startJob();

				try{
					govService.queryForUpdate("TXDM2473.UPDATE_ROAD_END", dataMap);
				}catch (Exception sub_e){
					log.error("UPDATE_ROAD_END ����");
					sub_e.printStackTrace();
				}						
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
		txdm2473_JobProcess();
	
	}
	
	
    /*���������ݰ��·�...����*/
	private int txdm2473_JobProcess() {
		
		log.info("=====================================================================");
		log.info("=" + this.getClass().getName()+ " txdm2473_JobProcess()[���������ݰ��·�-[" + c_slf_org_nm + "] �ΰ��ڷῬ��] Start =");
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
				alNidLevyList =  govService.queryForList("TXDM2473.SELECT_ROAD_LIST", dataMap);
			}catch (Exception sub_e){
				log.error("SELECT_ROAD_LIST ���������� = " + gblNidLevyRows.getMaps());
				sub_e.printStackTrace();
			}						

			tot_size = alNidLevyList.size();
			
		    log.info("[" + c_slf_org_nm + "]���������ݰ��·� �����ڷ� �Ǽ� = [" + tot_size + "]");
			
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
				    String WETAX_YN = (String) gblNidLevyRows.getMap("WETAX_YN");
				    String THAP_GBN = (String) gblNidLevyRows.getMap("THAP_GBN");
				    String DIVIDED_PAYMENT_SEQNUM = (String) gblNidLevyRows.getMap("DIVIDED_PAYMENT_SEQNUM");
				    
					long SIGUNGU_TAX = ((BigDecimal) gblNidLevyRows.getMap("SIGUNGU_TAX")).longValue();
					long PAYMENT_DATE1 = ((BigDecimal) gblNidLevyRows.getMap("PAYMENT_DATE1")).longValue();
					long AFTPAYMENT_DATE1 = ((BigDecimal) gblNidLevyRows.getMap("AFTPAYMENT_DATE1")).longValue();
			        
					// ����Ǹ� �ȵǴ� �ڷ�
					if(TRN_YN.equals("1")&&!THAP_GBN.equals("02"))TRN_YN="X";
					if(TRN_YN.equals("1")&&!(DIVIDED_PAYMENT_SEQNUM.equals("00")||DIVIDED_PAYMENT_SEQNUM.equals("98")))TRN_YN="X";
					if(TRN_YN.equals("1")&&(SIGUNGU_TAX%10L>0L||PAYMENT_DATE1%10L>0L||AFTPAYMENT_DATE1%10L>0L))TRN_YN="E";

					// �ű�, ����, ���� ���� ����
					String V_DEL_YN="N";
				    String DPOSL_STAT = (String) gblNidLevyRows.getMap("DPOSL_STAT");
				    String BNON_SNTG = (String) gblNidLevyRows.getMap("BNON_SNTG");
				    String CUD_OPT = (String) gblNidLevyRows.getMap("CUD_OPT");
				    String EPAY_NO = (String) gblNidLevyRows.getMap("EPAY_NO");
				    String DUE_DT = (String) gblNidLevyRows.getMap("DUE_DT");
				    String REG_NO = (String) gblNidLevyRows.getMap("REG_NO");
				    if(TRN_YN.equals("1")&&(WETAX_YN.equals("N"))&&(DPOSL_STAT.equals("2")||BNON_SNTG.equals("D")||BNON_SNTG.equals("S")||CUD_OPT.equals("3")||SIGUNGU_TAX==0L||(PAYMENT_DATE1+AFTPAYMENT_DATE1)==0L))V_DEL_YN="Y";
				    if(TRN_YN.equals("1")&&V_DEL_YN.equals("N")&&(EPAY_NO.equals(" ")||EPAY_NO.equals("0000000000000000000")||DUE_DT.equals(" ")||REG_NO.equals(" ")))TRN_YN="E";

					if(TRN_YN.equals("1"))
				    {
						gblNidLevyRows.setMap("PROC_CLS","1");
						gblNidLevyRows.setMap("VIR_ACC_NO","");
						gblNidLevyRows.setMap("SGG_TR_TG","0");
						gblNidLevyRows.setMap("DEL_YN",V_DEL_YN);
						if(WETAX_YN.equals("Y")){
							gblNidLevyRows.setMap("DEL_YN","Y");
							gblNidLevyRows.setMap("SNTG","2");
						}

						String V_CUD ="";
						//int TX2111_CNT_EPAY_NO = cyberService.getOneFieldInteger("TXDM2473.SELECT_TX2111_CNT_EPAY_NO", gblNidLevyRows);
						int TX2111_CNT_TAX_NO = cyberService.getOneFieldInteger("TXDM2473.SELECT_TX2111_CNT_TAX_NO", gblNidLevyRows);
						int TX2211_CNT_TAX_NO = cyberService.getOneFieldInteger("TXDM2473.SELECT_TX2211_CNT_TAX_NO", gblNidLevyRows);
						
						if(TX2111_CNT_TAX_NO==0){
							if(V_DEL_YN.equals("Y")){
								if(cyberService.getOneFieldInteger("TXDM2473.SELECT_TX2112_CNT_LEVY_DETAIL", gblNidLevyRows)>0){
									V_CUD ="L";
								}
							}else{
								V_CUD="I";
							}
						}else{
							if(V_DEL_YN.equals("Y")){
								if(TX2211_CNT_TAX_NO==0){
									V_CUD="D";
								}
							}else{
								if(TX2211_CNT_TAX_NO==0){
									V_CUD="U";
								}else{
									TRN_YN="E";
								}
							}
						}

						if(V_CUD.equals("I")){
							try{
								cyberService.queryForUpdate("TXDM2473.INSERT_TX2111", gblNidLevyRows);
							}catch (Exception sub_e){
								TRN_YN="E";
								log.error("INSERT_TX2111 ���� = " + gblNidLevyRows.getMaps());
								sub_e.printStackTrace();
							}
							
							try{
								cyberService.queryForUpdate("TXDM2473.INSERT_TX2112", gblNidLevyRows);
							}catch (Exception sub_e){
								TRN_YN="E";
								log.error("INSERT_TX2112 ���� = " + gblNidLevyRows.getMaps());
								sub_e.printStackTrace();
							}
							insert_cnt++;
						}
						
						if(V_CUD.equals("U")){
							
							String vir_no = "";
							
							try{	
								cyberService.queryForInsert("TXDM2473.INSERT_TX2115", gblNidLevyRows);
							}catch(Exception sub_e){
								TRN_YN="E";
								log.error("INSERT_TX2115 ���� = " + gblNidLevyRows.getMaps());
								sub_e.printStackTrace();
							}
							
							//������ȣ,�����Ͻ� ����� ������¹�ȣ ������ ä���Ͽ� ������
							try{
								vir_no = cyberService.getOneFieldString("TXDM2473.getVirAccNoByTaxNoKey", gblNidLevyRows);	
							}catch (Exception sub_e){
								TRN_YN="E";
								log.error("getVirAccNoByTaxNoKey ���� = " + gblNidLevyRows.getMaps());
								sub_e.printStackTrace();
							}
							gblNidLevyRows.setMap("VIR_NO", vir_no);
							
							try{
								cyberService.queryForUpdate("TXDM2473.UPDATE_TX2111_TAX_NO", gblNidLevyRows);
							}catch (Exception sub_e){
								TRN_YN="E";
								log.error("UPDATE_TX2111_TAX_NO ���� = " + gblNidLevyRows.getMaps());
								sub_e.printStackTrace();
							}
							
							try{
								cyberService.queryForUpdate("TXDM2473.UPDATE_TX2112_TAX_NO", gblNidLevyRows);
							}catch (Exception sub_e){
								TRN_YN="E";
								log.error("UPDATE_TX2112_TAX_NO ���� = " + gblNidLevyRows.getMaps());
								sub_e.printStackTrace();
							}						
							update_cnt++;
						}
						
						if(V_CUD.equals("D")){
							try{
								if (cyberService.queryForDelete("TXDM2473.UPDATE_TX2112_DEL_YN", gblNidLevyRows) > 0){
									delete_cnt++;
								}
							}catch (Exception sub_e){
								TRN_YN="E";
								log.error("UPDATE_TX2112_DEL_YN ���� = " + gblNidLevyRows.getMaps());
								sub_e.printStackTrace();
							}
						}
						
						if(V_CUD.equals("L")){
							try{
								if (cyberService.queryForDelete("TXDM2473.UPDATE_TX2112_DEL_YN_LEVY_DETAIL", gblNidLevyRows) > 0){
									delete_cnt++;
								}
							}catch (Exception sub_e){
								TRN_YN="E";
								log.error("UPDATE_TX2112_DEL_YN_LEVY_DETAIL ���� = " + gblNidLevyRows.getMaps());
								sub_e.printStackTrace();
							}
						}
						
						if(WETAX_YN.equals("Y")){
							int TX2211_CNT_TAX_NO_WETAX = cyberService.getOneFieldInteger("TXDM2473.SELECT_TX2211_CNT_TAX_NO_WETAX", gblNidLevyRows);
							if(TX2211_CNT_TAX_NO_WETAX==0)
							{
								int TX2211_MAX_PAY_CNT = cyberService.getOneFieldInteger("TXDM2473.SELECT_TX2211_MAX_PAY_CNT", gblNidLevyRows);
								gblNidLevyRows.setMap("PAY_CNT",TX2211_MAX_PAY_CNT);
								try{
									cyberService.queryForUpdate("TXDM2473.INSERT_TX2211", gblNidLevyRows);
								}catch (Exception sub_e){
									TRN_YN="E";
									log.error("INSERT_TX2211 ���� = " + gblNidLevyRows.getMaps());
									sub_e.printStackTrace();
								}
								
								try{
									cyberService.queryForDelete("TXDM2473.UPDATE_TX2112_DEL_YN_N", gblNidLevyRows);
								}catch (Exception sub_e){
									TRN_YN="E";
									log.error("UPDATE_TX2112_DEL_YN_N ���� = " + gblNidLevyRows.getMaps());
									sub_e.printStackTrace();
								}
							}
						}
				    }

					try{
						if (cyberService.queryForDelete("TXDM2473.UPDATE_TX2112_DEL_YN_EPAY_NO", gblNidLevyRows) > 0){
							delete_cnt++;
						}
					}catch (Exception sub_e){
						TRN_YN="E";
						log.error("UPDATE_TX2112_DEL_YN_EPAY_NO ���� = " + gblNidLevyRows.getMaps());
						sub_e.printStackTrace();
					}

					gblNidLevyRows.setMap("TRN_YN",TRN_YN);
					
					try{
						/*�������� ������Ʈ*/
						remote_up_cnt +=govService.queryForUpdate("TXDM2473.UPDATE_ROAD_TRN_YN", gblNidLevyRows);
					}catch (Exception sub_e){
						log.error("UPDATE_ROAD_TRN_YN ���� = " + gblNidLevyRows.getMaps());
						sub_e.printStackTrace();
					}	
					
					if(rec_cnt % 500 == 0) {
						log.info("[" + this.c_slf_org_nm + "][" + this.c_slf_org + "]���������ݰ��·� �ڷ�ó���߰Ǽ� = [" + rec_cnt + "]");
					}
				}
				
				log.info("[" + c_slf_org_nm + "]���������ݰ��·� �ΰ��ڷῬ�� �Ǽ� [" + rec_cnt + "] ��ϰǼ� [" + insert_cnt +"] �����Ǽ� [" + update_cnt + "] �����Ǽ� [" + delete_cnt + "] ���������� [" + p_del_cnt + "]");
				log.info("�������������Ʈ ["+remote_up_cnt+"], ��ó�� �ΰ��Ǽ� [" + nul_cnt + "]");
			}

			elapseTime2 = System.currentTimeMillis();
			
			log.info("[" + c_slf_org_nm + "]���������ݰ��·� �ΰ��ڷῬ�� �ð�("+tot_size+") : " + CbUtil.formatTime(elapseTime2 - elapseTime1));
					
		} catch (Exception e) {
		    log.error("TXDM2473 ���� ���� = " + gblNidLevyRows.getMaps());
			throw (RuntimeException) e;
		}
		
		return tot_size;
	}
}
