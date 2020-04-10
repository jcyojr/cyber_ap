/**
 *  �ֽý��۸� : �λ��û ���̹� ���漼û
 *  ��  ��  �� : ����
 *  ��  ��  �� : ���������ݰ��·� �����ڷῬ��
 *  Ŭ����  ID : Txdm2470_process : �� ó�������� �ۼ��ϴ� Ŭ����
 *  ����  �̷� :
 * ------------------------------------------------------------------------
 *  �ۼ���      	�Ҽ�  		    ����            Tag           ����
 * ------------------------------------------------------------------------
 *  �۵���       ��ä��(��)      2011.06.03         %01%         �����ۼ�
 */
package com.uc.bs.cyber.daemon.txdm2470;

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
public class Txdm2470_process extends Codm_BaseProcess implements Codm_interface {

	private MapForm dataMap            = null;
	private int loop_cnt = 0, insert_cnt = 0, update_cnt = 0, del_cnt = 0;
	
	MapForm gblParkingLevyList = new MapForm();
	
	/**
	 * 
	 */
	public Txdm2470_process() {
		// TODO Auto-generated constructor stub
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

			setContext(appContext);
			setApp(appContext);

			UcContextHolder.setCustomerType(this.dataSource);

			MapForm ststMap = new MapForm();

			log.info("=======[" + c_slf_org_nm + "] ���������ݰ��·� �ΰ��ڷ� ���� ����=======");
			
			do {
				
				ststMap.setMap("JOB_ID"    , "TXDM2470");
				ststMap.setMap("PARM_NM"   , this.c_slf_org + "||" + this.govId);
				ststMap.setMap("LOG_DESC"  , "== START ==");
				ststMap.setMap("RESULT_CD" , "S");
				
				/*���������ݰ��·� �����ڷῬ�� ����... */
				cyberService.queryForUpdate("TXDM2420.INSERT_LOG_JOBSTATE", ststMap);
				
				int procCnt = txdm2470_JobProcess();
				
				/*�Ϸ��ڷ� ������Ʈ */
				ststMap.setMap("LOG_DESC"  , "== END  ���������ݰ��·� �����ڷῬ�� ó���Ǽ�::" + loop_cnt + ", �ΰ�ó��::" + insert_cnt + ", ������Ʈ::" + update_cnt + ", ����ó��::" + del_cnt);
				ststMap.setMap("RESULT_CD" , "E");
				
				/*�ΰ������ڷ� ���Ϸ� */
				cyberService.queryForUpdate("TXDM2420.INSERT_LOG_JOBSTATE", ststMap);		
				
				if(procCnt == 0) {
					break;	
				}

			} while(true);
		
			log.info("=======[" + c_slf_org_nm + "] ���������ݰ��·� �ΰ��ڷ� ���� �� =======");
			

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

	@Override
	public void setDatasrc(String datasrc) {
		// TODO Auto-generated method stub
		this.dataSource = datasrc;
	}

	@Override
	public void transactionJob() {
		// TODO Auto-generated method stub
		
		nonTransactionJob();
	}
	
	public void nonTransactionJob() {
		
		if(gblParkingLevyList.getMap("BNON_SNTG").equals("I") || gblParkingLevyList.getMap("BNON_SNTG").equals("U") || gblParkingLevyList.getMap("BNON_SNTG").equals("M") ) {
			
			/*==========�ΰ��ڷ� ����...===========*/
			long natn_tax = Long.parseLong(gblParkingLevyList.getMap("NATN_TAX").toString());
			long sido_tax = Long.parseLong(gblParkingLevyList.getMap("SIDO_TAX").toString());
			long sigingu_tax = Long.parseLong(gblParkingLevyList.getMap("SIGUNGU_TAX").toString());
			
			/*�ʱⰪ ����*/
			gblParkingLevyList.setMap("TAX_CNT"    , 0 );  /*�ΰ��ô� 0 : �����ô� ����ؾ� ��...(���߼���)*/
			gblParkingLevyList.setMap("DEL_YN"     ,"N"); 
			gblParkingLevyList.setMap("PROC_CLS"   ,"1");  /*�������ó������ �ڵ� Default */
			gblParkingLevyList.setMap("BU_ADD_YN"  ,"N");  /*�ΰ���ġ���� */
			gblParkingLevyList.setMap("CUD_OPT"    ,"1");  /*�ڷ��ϱ��� */
			gblParkingLevyList.setMap("SGG_TR_TG"  ,"0");  /*��û����ó������*/
			
			
			try {
				
				/*�ݾ��� 0 �� �������*/
				if((natn_tax + sido_tax + sigingu_tax) == 0) {
					
					/*���� FLAG */
					if (cyberService.queryForUpdate("TXDM2470.UPDATE_DEL_PARKING_LEVY", gblParkingLevyList) > 0) {
						del_cnt++;
					}
					
				} else {
					
					/*�ΰ�*/
					try {
						
						cyberService.queryForUpdate("TXDM2470.INSERT_PARKING_LEVY", gblParkingLevyList);
						
					}catch (Exception e){
						
						if (e instanceof DuplicateKeyException){
							cyberService.queryForUpdate("TXDM2470.UPDATE_PARKING_LEVY", gblParkingLevyList);	
						} else {
							
							log.info("�����߻������� = " + gblParkingLevyList.getMaps());
							e.printStackTrace();							
							throw (RuntimeException) e;
						}
					}
					
					/*�ΰ���*/
					try {
						
						cyberService.queryForUpdate("TXDM2470.INSERT_PARKING_DETAIL_LEVY", gblParkingLevyList);
						
					}catch (Exception e){
						
						if (e instanceof DuplicateKeyException){
							cyberService.queryForUpdate("TXDM2470.UPDATE_PARKING_DETAIL_LEVY", gblParkingLevyList);
						} else {
							
							log.info("�����߻������� = " + gblParkingLevyList.getMaps());
							e.printStackTrace();							
							throw (RuntimeException) e;
						}
					}
					
					insert_cnt++;
				}
				
			}catch (Exception e){
				
				log.info("�����߻������� = " + gblParkingLevyList.getMaps());
				e.printStackTrace();						
			}

		} else {
			
			gblParkingLevyList.setMap("TAX_CNT"    , 0 );
			
			/*���� FLAG */
			if (cyberService.queryForUpdate("TXDM2470.UPDATE_DEL_PARKING_LEVY", gblParkingLevyList) > 0) {
				del_cnt++;
			}
			
		}
		
		/* ����ó������ ������Ʈ*/
		govService.queryForUpdate("TXDM2470.UPDATE_PARKING_LEVY_RESULT", gblParkingLevyList);

	}
	
	/**
	 * ���������ݰ��·� �����ڷῬ�� ���μ���
	 */
	private int txdm2470_JobProcess() {
		
		log.info("=====================================================================");
		log.info("=" + this.getClass().getName()+ " txdm2470_JobProcess() [���������ݰ��·� �����ڷῬ��] Start =");
		log.info("= govid["+this.govId+"], orgcd["+this.c_slf_org+"], orgcd["+this.c_slf_org_nm+"]");
		log.info("=====================================================================");
		
		loop_cnt = 0; 
		insert_cnt = 0;
		update_cnt = 0;
		del_cnt = 0;
		
		long elapseTime1 = 0;
		long elapseTime2 = 0;
		
		/*Ʈ������� �����ϴ� �Ǿ����� ���⼭ �����Ѵ�...*/
		/*�Ǿ����� �����մϴ�.*/
		
		dataMap = new MapForm();
		dataMap.setMap("TRANS_SGCD", this.c_slf_org);
						
		/*���������ݰ��·� �ΰ������� �����´�.*/
		ArrayList<MapForm> alParkingLevyList =  govService.queryForList("TXDM2470.SELECT_PARKING_LEVY_LIST", dataMap);
		
		log.info("[C][" + this.c_slf_org_nm + "]�ΰ����� �Ǽ� = " + alParkingLevyList.size());
		
		if (alParkingLevyList.size()  >  0)   {
			
			elapseTime1 = System.currentTimeMillis();
			
			for (int rec_cnt = 0;  rec_cnt < alParkingLevyList.size();  rec_cnt++)   {
				
				gblParkingLevyList =  alParkingLevyList.get(rec_cnt);
				
				if (gblParkingLevyList == null  ||  gblParkingLevyList.isEmpty() )   {
					continue;
				}
				
				loop_cnt++;

				/*========================*/
				/*Ʈ�����ó�� �Ǻ�*/
				/*========================*/
				try {
					//this.startJob();
					this.nonTransactionJob();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
			
			elapseTime2 = System.currentTimeMillis();
			
		}
		
		log.info("[" + c_slf_org_nm + "]���������ݰ��·� ����ð� : " + CbUtil.formatTime(elapseTime2 - elapseTime1));
		log.info("���������ݰ��·� �����ڷῬ�� ó���Ǽ�::" + loop_cnt + ", �ΰ�ó��::" + insert_cnt + ", ������Ʈ::" + update_cnt + ", ����ó��::" + del_cnt);
		
		return alParkingLevyList.size();
	}
	
}
