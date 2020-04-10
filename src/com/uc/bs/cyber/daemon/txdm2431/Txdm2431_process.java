/**
 *  �ֽý��۸� : �λ��û ���̹� ���漼û
 *  ��  ��  �� : ����
 *  ��  ��  �� : ���������������ݰ��·� �������ä�� ����
 *               �ְ����� ������� ä�� ����.
 *  Ŭ����  ID : Txdm2431_process : �� ó�������� �ۼ��ϴ� Ŭ����
 *  ����  �̷� : 
 * ------------------------------------------------------------------------
 *  �ۼ���      	�Ҽ�  		    ����            Tag           ����
 * ------------------------------------------------------------------------
 *  �۵���       ��ä��(��)      2011.05.31         %01%         �����ۼ�
 */
package com.uc.bs.cyber.daemon.txdm2431;

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
public class Txdm2431_process extends Codm_BaseProcess implements Codm_interface {

	@SuppressWarnings("unused")
	private MapForm dataMap            = null;
	private int loop_cnt = 0, update_cnt = 0, error_cnt = 0;
	
	/**
	 * 
	 */
	public Txdm2431_process() {
		// TODO Auto-generated constructor stub
		super();
		
		log = LogFactory.getLog(this.getClass());
		
		/**
		 * 10 �и��� ����
		 */
		loopTerm = 60 * 10;
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
			MapForm ststMap = cyberService.queryForMap("TXDM2420.GET_CO5102_MAXSEQ", null);
					
			ststMap.setMap("JOB_ID"    , "TXDM2431");
			ststMap.setMap("PARM_NM"   , this.c_slf_org + "||" + this.govId);
			ststMap.setMap("LOG_DESC"  , "== START ==");
			ststMap.setMap("RESULT_CD" , "S");
			
			/*�ΰ������ڷ� ������ */
			cyberService.queryForUpdate("TXDM2420.INSERT_LOG_JOBSTATE", ststMap);
			
			this.startJob();
						
			ststMap.setMap("LOG_DESC"  , "== END  �������ä������ ó���Ǽ�::" + loop_cnt + ", (����ó��)�����Ǽ�::" + update_cnt + ", �����Ǽ�::" + error_cnt);
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
		log.info("= govid["+this.govId+"], orgcd["+this.dataSource+"]");
		log.info("=====================================================================");
		
		/*Ʈ������� �����ϴ� �Ǿ����� ���⼭ �����Ѵ�...*/
		/*�Ǿ����� �����մϴ�.*/
		loop_cnt = 0;
		update_cnt = 0;
		error_cnt = 0;

		/*���̹��� ���������������ݰ��·� �ΰ������� �����´�.*/
		ArrayList<MapForm> alBusFineList =  cyberService.queryForList("TXDM2431.SELECT_VIRUAL_BUS_FINE_LIST", null);
		
		if (alBusFineList.size()  >  0)   {
			
			for (int rec_cnt = 0;  rec_cnt < alBusFineList.size();  rec_cnt++)   {
				
				MapForm mfBusFineList =  alBusFineList.get(rec_cnt);
				
				if (mfBusFineList == null  ||  mfBusFineList.isEmpty() )   {
					continue;
				}
				
				loop_cnt++;
				
				/*==========�������̺� ������Ʈ===========*/
				/*flag ó��*/

				try {
					
					if (govService.queryForUpdate("TXDM2431.UPDATE_NONTAX_TAB", mfBusFineList) > 0) {
						
						mfBusFineList.setMap("SGG_TR_TG", "1");       /*������*/
						if (cyberService.queryForUpdate("TXDM2431.UPDATE_TX2122_TB", mfBusFineList) > 0) {
							update_cnt++;
						}
						
					} else {
						mfBusFineList.setMap("SGG_TR_TG", "9");   /*���н�*/
						/*���������� ������Ʈ�� ���� ������ ���̹�DB�� ����� �����.*/
						cyberService.queryForUpdate("TXDM2431.UPDATE_TX2122_TB", mfBusFineList);
						
						error_cnt++;
					}
					
				}catch (Exception e){
					
					log.error("���������� = " + mfBusFineList.getMaps());
					e.printStackTrace();
					throw (RuntimeException) e;
					
				}

			}
			
			/***********************************************************************************/
			try{
				MapForm daemonMap = new MapForm();
				daemonMap.setMap("DAEMON"    , "TXDM2431");
				daemonMap.setMap("DAEMON_NM" , "��������(NONTAX_TAB)������¿���");
				daemonMap.setMap("SGG_COD"   , c_slf_org);
				daemonMap.setMap("TOTAL_CNT" , loop_cnt);
				daemonMap.setMap("INSERT_CNT", 0);
				daemonMap.setMap("UPDATE_CNT", update_cnt);
				daemonMap.setMap("DELETE_CNT", 0);
				daemonMap.setMap("ERROR_CNT" , error_cnt);
				cyberService.queryForInsert("CODMBASE.InsertDaemonProcessCheck", daemonMap);
			}catch(Exception ex){
				log.debug("��������(NONTAX_TAB)������¿��� �α� ��� ����");
			}				
			/***********************************************************************************/
			
			
		}else{
			
			/***********************************************************************************/
			try{
				MapForm daemonMap = new MapForm();
				daemonMap.setMap("DAEMON"    , "TXDM2431");
				daemonMap.setMap("DAEMON_NM" , "��������(NONTAX_TAB)������¿���");
				daemonMap.setMap("SGG_COD"   , c_slf_org);
				daemonMap.setMap("TOTAL_CNT" , 0);
				daemonMap.setMap("INSERT_CNT", 0);
				daemonMap.setMap("UPDATE_CNT", 0);
				daemonMap.setMap("DELETE_CNT", 0);
				daemonMap.setMap("ERROR_CNT" , 0);
				cyberService.queryForInsert("CODMBASE.InsertDaemonProcessCheck", daemonMap);
			}catch(Exception ex){
				log.debug("��������(NONTAX_TAB)������¿��� ����� �����ϴ�. ��� ����");
			}				
			/***********************************************************************************/
		}
		
		log.info("���������������ݰ��·� �������ä������ ó���Ǽ�::" + loop_cnt + ", (����ó��)�����Ǽ�::" + update_cnt + ", �����Ǽ�::" + error_cnt);
		

	}
	

}
