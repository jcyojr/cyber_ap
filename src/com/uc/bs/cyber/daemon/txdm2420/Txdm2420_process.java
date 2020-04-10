/**
 *  �ֽý��۸� : �λ��û ���̹� ���漼û
 *  ��  ��  �� : ����
 *  ��  ��  �� : �������ߺδ�� �ΰ��ڷ� ����
 *  Ŭ����  ID : Txdm2420_process : �� ó�������� �ۼ��ϴ� Ŭ����
 *  ����  �̷� :
 * ------------------------------------------------------------------------
 *  �ۼ���      	�Ҽ�  		    ����            Tag           ����
 * ------------------------------------------------------------------------
 *  �۵���       ��ä��(��)      2011.05.11         %01%         �����ۼ�
 */
package com.uc.bs.cyber.daemon.txdm2420;

import java.util.ArrayList;

import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.dao.DuplicateKeyException;

import com.uc.bs.cyber.daemon.Codm_BaseProcess;
import com.uc.bs.cyber.daemon.Codm_interface;
import com.uc.core.MapForm;
import com.uc.core.spring.service.UcContextHolder;

/**
 * @author Administrator
 * 
 * ����) 20111006 ��
 * 
 *  �������� �δ�� ���� ó�� ����
 *  1) ET3101_TB �� ���� ������ ������Ʈ �� ��¥�� ���ؿ´�.
 *  2) ���ؿ� ��¥�� �������� �������̺��� �����͸� �����ϰ�
 *  3) ���̹���û ���̺� �Է� �� ������Ʈ �Ѵ�.
 *  4) txdm2421 AP���� ������¸� �����ϸ�
 *  5) ���û���ܿ��� �ش� ���� ������Ʈ �ϰ� ������ ��¥�� ����ȴ�.
 *  6) ��¥�� ����Ǿ��� ������ �ٽ� ������ �ǿ� ���ؼ� ���谡 ���۵ȴ�.
 *  7) ����� �����ʹ� �̹� ���̹���û�� �ְ� ��¥ �� �ݾ� ������ ���⶧����
 *  8) ������Ʈ�� �ǰ� ������� ä�� �� ���ۿ��δ� �״�� �д�.
 *  9) ���� ������ �۾��� 2���ϰ� �ȴ�...����...
 */
public class Txdm2420_process extends Codm_BaseProcess implements Codm_interface {

	private MapForm dataMap            = null;
	private int insert_cnt = 0, update_cnt = 0, delete_cnt = 0;
	
	/**
	 * 
	 */
	public Txdm2420_process() {
		// TODO Auto-generated constructor stub
		super();
		
		log = LogFactory.getLog(this.getClass());
		
		/**
		 * 10 �и��� ����
		 */
		loopTerm = 60 * 15;
	}
	/*Context ���Կ�*/
	public void setApp(ApplicationContext context) {
		this.context = context;
	}
	
	/*Ʈ������� �����ϱ� ���� �Լ�.*/
	@SuppressWarnings("unchecked")
	private void mainTransProcess(){
		
		try {
				
			setContext(appContext);
			setApp(appContext);

			UcContextHolder.setCustomerType(this.dataSource);
			
			dataMap = new MapForm();  /*�ʱ�ȭ*/
			dataMap.setMap("PAGE_PER_CNT" , 1000);            /*�������� ����*/
			dataMap.setMap("SGG_COD"      , this.c_slf_org);  /*��û�ڵ� ����*/
			
			do {
				
				/*������Ʈ �ð� �����Ѵ�.*/
				dataMap.putAll(cyberService.queryForMap("TXDM2420.SELECT_C_LAST_SGG_WORKTIME", dataMap));
				
				int page = govService.getOneFieldInteger("TXDM2420.SELECT_C_TRAFFIC_LEVY_PER_CNT", dataMap);
				
				log.info("[�������ߺδ��(" + c_slf_org_nm + ")] PAGE_PER_CNT(" + dataMap.getMap("PAGE_PER_CNT") + ") �� Page���� =" + page);
				
				if(page == 0) break;
				
				MapForm ststMap = new MapForm();
				
				ststMap.setMap("JOB_ID"    , "TXDM2420");
				ststMap.setMap("PARM_NM"   , this.c_slf_org + "||" + this.govId);
				ststMap.setMap("LOG_DESC"  , "== START ==");
				ststMap.setMap("RESULT_CD" , "S");
				
				/*.1 �ΰ������ڷ� ������ */
				cyberService.queryForUpdate("TXDM2420.INSERT_LOG_JOBSTATE", ststMap);
				
				for(int i = 1 ; i <= page ; i ++) {

					dataMap.setMap("PAGE",  i);    /*ó��������*/
					this.startJob();               /*��Ƽ Ʈ����� ȣ��*/
				}
				
				ststMap.setMap("JOB_ID"    , "TXDM2420");
				ststMap.setMap("PARM_NM"   , this.c_slf_org + "||" + this.govId);
				ststMap.setMap("LOG_DESC"  , "== END  ��ϰǼ�::" + insert_cnt +", �����Ǽ�::" + update_cnt + ", �����Ǽ�::" + delete_cnt);
				ststMap.setMap("RESULT_CD" , "E");
				
				/*.�ΰ������ڷ� ������ */
				cyberService.queryForUpdate("TXDM2420.INSERT_LOG_JOBSTATE", ststMap);
				
				if(govService.getOneFieldInteger("TXDM2420.SELECT_C_TRAFFIC_LEVY_PER_CNT", dataMap) == 0) {
					break;
				}
				
			}while(true);
						
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}			
		
	}
	
	/* process starting */
	@Override
	public void runProcess() throws Exception {
		// TODO Auto-generated method stub
		//log.debug("=====================================================================");
		//log.debug("=" + this.getClass().getName()+ " runProcess() ==");
		//log.debug("=====================================================================");
		
		/*Ʈ����� ���� ����*/
		mainTransProcess();
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
		
		log.info("=====================================================================");
		log.info("=" + this.getClass().getName()+ " transactionJob() Start =");
		log.info("= govid["+this.govId+"], orgcd["+this.c_slf_org+"] ordnm[" + c_slf_org_nm + "]");
		log.info("=====================================================================");
		
		/*Ʈ������� �����ϴ� �Ǿ����� ���⼭ �����Ѵ�...*/
		/*�Ǿ����� �����մϴ�.*/
		insert_cnt = 0;
		update_cnt = 0;
		delete_cnt = 0;
		
		/*work time �ð����� (�������� �����ϱ� ����) */
		cyberService.queryForUpdate("TXDM2420.UPDATE_C_LAST_SGG_WORKTIME", dataMap);

		/* �������� �ΰ������ϱ�, TX2112_TB ->> SNTG = 0 �ΰ͸� ����� 
		 *                          ���ο��� 0:�̼���, 1:����ó����, 2:����
		 * */

		/*�������ߺδ���� �����ϱ� ���� ����DB�� ���� ������ȣ�� �����Ѵ�*/
		ArrayList<MapForm> alTaxNoList =  govService.queryForList("TXDM2420.SELECT_C_TRAFFIC_TAX_NO_LIST", dataMap);

		if (alTaxNoList.size()  >  0)   {
			
			for (int rec_cnt = 0;  rec_cnt < alTaxNoList.size();  rec_cnt++)   {
								
				MapForm mfTaxNoList =  alTaxNoList.get(rec_cnt);
				
				if (mfTaxNoList == null  ||  mfTaxNoList.isEmpty() )   {
					continue;
				}
				
				/*==========���� ��ƾ===========*/
				/*flag ó��*/
				if (cyberService.queryForDelete("TXDM2420.UPDATE_DEL_TRAFFIC_INFO_DETAIL", mfTaxNoList) > 0) {
					delete_cnt++;
				}
			}
			
		}
		
		/*
		 * 3. �������ߺδ�� �����ڷ� �Է�
		 * */
		
		/*�������ߺδ�� �ΰ����� ��������*/
		ArrayList<MapForm> alTrafficLevyList =  govService.queryForList("TXDM2420.SELECT_C_TRAFFIC_LEVY_LIST", dataMap);
		
		int rec_cnt = 0;
		
		if (alTrafficLevyList.size()  >  0)   {
			
			for (rec_cnt = 0;  rec_cnt < alTrafficLevyList.size();  rec_cnt++)   {
								
				MapForm mfTrafficLevyList =  alTrafficLevyList.get(rec_cnt);
				
				if (mfTrafficLevyList == null  ||  mfTrafficLevyList.isEmpty() )   {
					continue;
				}
				
				/*�⺻ Default �� ���� */
				mfTrafficLevyList.setMap("BU_ADD_YN"   , "N" );       /*�ΰ���ġ������ 'N'           */
				mfTrafficLevyList.setMap("TAX_CNT"     ,  0  );       /*�ΰ����� 0                   */
				mfTrafficLevyList.setMap("PROC_CLS"    , "1" );       /*�������ä������ default '1' */
				mfTrafficLevyList.setMap("DEL_YN"      , "N" );       /*��������         default 'N' */
				mfTrafficLevyList.setMap("CUD_OPT"     , "1" );       /*�ڷ��ϱ��� üũ�ٶ�. */
				mfTrafficLevyList.setMap("SGG_TR_TG"   , "0" );       /*��û����ó������*/
				
				/*�׿� �ʵ� ����*/
				mfTrafficLevyList.setMap("NATN_TAX"    , 0 );       /*����           */
				mfTrafficLevyList.setMap("NATN_RATE"   , 0 );       /*��������       */
				mfTrafficLevyList.setMap("SIDO_TAX"    , 0 );       /*�õ���         */
				mfTrafficLevyList.setMap("SIDO_RATE"   , 0 );       /*�õ�������     */
				
				mfTrafficLevyList.setMap("TAX_NOTICE_TITLE"   , "�������ߺδ��");
				mfTrafficLevyList.setMap("ORG_PART_CODE"      , "");
				mfTrafficLevyList.setMap("ACCOUNT_NAME"       , "�ñ�����");
				mfTrafficLevyList.setMap("TAX_NM"             , "�������ߺδ��");
				mfTrafficLevyList.setMap("REG_TEL"            , "");
				mfTrafficLevyList.setMap("REG_ZIPCD"          , "");
				mfTrafficLevyList.setMap("ADDRESS"            , "");
				mfTrafficLevyList.setMap("LEVY_DETAIL1"       , "");
				mfTrafficLevyList.setMap("LEVY_DETAIL2"       , "");
				mfTrafficLevyList.setMap("LEVY_DETAIL3"       , "");
				mfTrafficLevyList.setMap("LEVY_DETAIL4"       , "");
				mfTrafficLevyList.setMap("LEVY_DETAIL5"       , "");
				mfTrafficLevyList.setMap("LEVY_DETAIL6"       , "");
				mfTrafficLevyList.setMap("SUBJECT_NAME1"      , "");
				mfTrafficLevyList.setMap("SUBJECT_NAME2"      , "");
				mfTrafficLevyList.setMap("SUBJECT_NAME3"      , "");
				mfTrafficLevyList.setMap("PAYMENT_DATE2"      , 0 );
				mfTrafficLevyList.setMap("AFTPAYMENT_DATE2"   , 0 );				
				mfTrafficLevyList.setMap("PAYMENT_DATE3"      , 0 );
				mfTrafficLevyList.setMap("AFTPAYMENT_DATE3"   , 0 );
				mfTrafficLevyList.setMap("BUGWA_BUSEONAME"    , "");
				mfTrafficLevyList.setMap("SUNAP_BUSEONAME"    , "");
				mfTrafficLevyList.setMap("USER_NAME"          , "");
				mfTrafficLevyList.setMap("USER_TEL_NO"        , "");
				mfTrafficLevyList.setMap("SNTG"               , "0");
				mfTrafficLevyList.setMap("PAID_DATE"          , "");
				mfTrafficLevyList.setMap("EPAY_NO_OLD"        , "");	

				try {

					if (cyberService.queryForUpdate("TXDM2420.INSERT_PUB_C_LEVY_DETAIL", mfTrafficLevyList) > 0) {
						cyberService.queryForUpdate("TXDM2420.INSERT_PUB_C_LEVY", mfTrafficLevyList);
						
						insert_cnt++;
					} 
					
				}catch (Exception e){

					/*�ߺ��� �߻��ϰų� �������ǿ� ��߳��� ���üũ*/
					if (e instanceof DuplicateKeyException){
						
						try{
													
							if (cyberService.queryForUpdate("TXDM2420.UPDATE_PUB_C_LEVY_DETAIL", mfTrafficLevyList) > 0) {
								cyberService.queryForUpdate("TXDM2420.UPDATE_PUB_C_LEVY", mfTrafficLevyList);
							    update_cnt++;
							}
							
						}catch (Exception sub_e){
							log.error("���������� = " + mfTrafficLevyList.getMaps());
							e.printStackTrace();
							throw (RuntimeException) sub_e;
						}
						
					}else{
						log.error("���������� = " + mfTrafficLevyList.getMaps());
						e.printStackTrace();							
						throw (RuntimeException) e;
					}
					
				}

			}
			
		}
		
		log.info("�������ߺδ��(" + c_slf_org_nm + ") �ڷῬ�� �Ǽ�::" + rec_cnt + ", ��ϰǼ�::" + insert_cnt +", �����Ǽ�::" + update_cnt + ", �����Ǽ�::" + delete_cnt);
		
	}

}
