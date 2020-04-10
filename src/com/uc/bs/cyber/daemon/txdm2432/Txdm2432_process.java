/**
 *  �ֽý��۸� : �λ��û ���̹� ���漼û
 *  ��  ��  �� : ����
 *  ��  ��  �� : ���������������ݰ��·� ������¼��� �� �ð� ����
 *  Ŭ����  ID : Txdm2432_process : �� ó�������� �ۼ��ϴ� Ŭ����
 *  ����  �̷� :
 * ------------------------------------------------------------------------
 *  �ۼ���      	�Ҽ�  		    ����            Tag           ����
 * ------------------------------------------------------------------------
 *  �۵���       ��ä��(��)      2011.05.31         %01%         �����ۼ�
 */
package com.uc.bs.cyber.daemon.txdm2432;

import java.util.ArrayList;

import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;

import com.uc.bs.cyber.daemon.Codm_BaseProcess;
import com.uc.bs.cyber.daemon.Codm_interface;
import com.uc.core.MapForm;
import com.uc.core.spring.service.UcContextHolder;

/**
 * @author Administrator
 *
 */
public class Txdm2432_process extends Codm_BaseProcess implements Codm_interface {

	@SuppressWarnings("unused")
	private MapForm dataMap            = null;
	private int loop_cnt = 0, update_cnt = 0, error_cnt = 0;
	
	/**
	 * 
	 */
	public Txdm2432_process() {
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
			
			/*SEQ���ϱ�*/
			MapForm ststMap = new MapForm();
			
			ststMap.setMap("JOB_ID"    , "TXDM2432");
			ststMap.setMap("PARM_NM"   , this.c_slf_org + "||" + this.govId);
			ststMap.setMap("LOG_DESC"  , "== START ==");
			ststMap.setMap("RESULT_CD" , "S");
			
			/*���������������ݰ��·� �ǽð����� ������ */
			cyberService.queryForUpdate("TXDM2420.INSERT_LOG_JOBSTATE", ststMap);

			this.startJob();
		
			ststMap.setMap("LOG_DESC"  , "== END  ������¼��� �ǽð�ó���Ǽ�::" + loop_cnt + ", ����ó��::" + update_cnt + ", ��ó����::" + error_cnt);
			ststMap.setMap("RESULT_CD" , "E");
			
			/*�ΰ������ڷ� ���Ϸ� */
			cyberService.queryForUpdate("TXDM2420.INSERT_LOG_JOBSTATE", ststMap);
			
			
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
		
		log.info("=====================================================================");
		log.info("=" + this.getClass().getName()+ " transactionJob() Start =");
		log.info("= govid["+this.govId+"], orgcd["+this.dataSource+"], orgnm["+this.c_slf_org_nm+"]");
		log.info("=====================================================================");
		
		/*Ʈ������� �����ϴ� �Ǿ����� ���⼭ �����Ѵ�...*/
		/*�Ǿ����� �����մϴ�.*/
		loop_cnt = 0;
		update_cnt = 0;
		error_cnt = 0;
		
		/*���̹��� ���������������ݰ��·� ������¼����� �����Ѵ�.*/
		ArrayList<MapForm> alVSunapBusFineList =  cyberService.queryForList("TXDM2432.SELECT_VIRUAL_BUS_FINE_SUNAP_LIST", null);
		
		if (alVSunapBusFineList.size()  >  0)   {
			
			for (int rec_cnt = 0;  rec_cnt < alVSunapBusFineList.size();  rec_cnt++)   {
				
				MapForm mfVSunapBusFineList =  alVSunapBusFineList.get(rec_cnt);
				
				if (mfVSunapBusFineList == null  ||  mfVSunapBusFineList.isEmpty() )   {
					continue;
				}
				
				loop_cnt++;
				
				/*==========�������̺� ����ó�� ������Ʈ===========*/
				/*��ó���� ���� ���ܽ�Ų��.*/

				if (govService.queryForUpdate("TXDM2432.UPDATE_NONTAX_SUNAP_INFO_TAB", mfVSunapBusFineList) > 0) {
					
					cyberService.queryForUpdate("TXDM2432.UPDATE_TX1202_TB_TRTG_TAB", mfVSunapBusFineList);
					
					update_cnt++;
				} else {
					error_cnt++;
				}
				
			}
			
			/***********************************************************************************/
			try{
				MapForm daemonMap = new MapForm();
				daemonMap.setMap("DAEMON"    , "TXDM2432");
				daemonMap.setMap("DAEMON_NM" , "��������(NONTAX_TAB)������¼����ڷῬ��");
				daemonMap.setMap("SGG_COD"   , c_slf_org);
				daemonMap.setMap("TOTAL_CNT" , loop_cnt);
				daemonMap.setMap("INSERT_CNT", 0);
				daemonMap.setMap("UPDATE_CNT", update_cnt);
				daemonMap.setMap("DELETE_CNT", 0);
				daemonMap.setMap("ERROR_CNT" , error_cnt);
				cyberService.queryForInsert("CODMBASE.InsertDaemonProcessCheck", daemonMap);
			}catch(Exception ex){
				log.debug("��������(NONTAX_TAB)������¼����ڷῬ�� �α� ��� ����");
			}				
			/***********************************************************************************/
			
		}else{
			
			/***********************************************************************************/
			try{
				MapForm daemonMap = new MapForm();
				daemonMap.setMap("DAEMON"    , "TXDM2432");
				daemonMap.setMap("DAEMON_NM" , "��������(NONTAX_TAB)������¼����ڷῬ��");
				daemonMap.setMap("SGG_COD"   , c_slf_org);
				daemonMap.setMap("TOTAL_CNT" , 0);
				daemonMap.setMap("INSERT_CNT", 0);
				daemonMap.setMap("UPDATE_CNT", 0);
				daemonMap.setMap("DELETE_CNT", 0);
				daemonMap.setMap("ERROR_CNT" , 0);
				cyberService.queryForInsert("CODMBASE.InsertDaemonProcessCheck", daemonMap);
			}catch(Exception ex){
				log.debug("��������(NONTAX_TAB)������¼����ڷῬ�� ����� �����ϴ�. ��� ����");
			}				
			/***********************************************************************************/
			
		}
		
		
		log.info("���������������ݰ��·� ������¼��� �ǽð�ó���Ǽ�::" + loop_cnt + ", ����ó��::" + update_cnt + ", ��ó����::" + error_cnt);

	}

}
