/**
 *  �ֽý��۸� : �λ��û ���̹� ���漼û
 *  ��  ��  �� : ����
 *  ��  ��  �� : ���������������ݰ��·� �ΰ��ڷ� ����
 *  Ŭ����  ID : Txdm2430_process : �� ó�������� �ۼ��ϴ� Ŭ����
 *  ����  �̷� :
 * ------------------------------------------------------------------------
 *  �ۼ���      	�Ҽ�  		    ����            Tag           ����
 * ------------------------------------------------------------------------
 *  �۵���       ��ä��(��)      2011.05.31         %01%         �����ۼ�
 */
package com.uc.bs.cyber.daemon.txdm2430;

import java.math.BigDecimal;
import java.util.ArrayList;

import org.apache.commons.logging.LogFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.context.ApplicationContext;

import com.uc.bs.cyber.daemon.Codm_BaseProcess;
import com.uc.bs.cyber.daemon.Codm_interface;
import com.uc.core.MapForm;
import com.uc.core.spring.service.UcContextHolder;

/**
 * @author Administrator
 *
 */
public class Txdm2430_process extends Codm_BaseProcess implements Codm_interface {

	@SuppressWarnings("unused")
	private MapForm dataMap            = null;
	private int insert_cnt = 0, update_cnt = 0, delete_cnt = 0;
	
	/**
	 * 
	 */
	public Txdm2430_process() {
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
	
	public String getDataSource() {
		return this.dataSource;
	}
	
	/*Ʈ������� �����ϱ� ���� �Լ�.*/
	private void mainTransProcess(){
		
		try {
			
			setApp(appContext);
			
			UcContextHolder.setCustomerType(getDataSource());
			
			/*SEQ���ϱ�*/
			MapForm ststMap = new MapForm();
			
			ststMap.setMap("JOB_ID"    , "TXDM2430");
			ststMap.setMap("PARM_NM"   , this.c_slf_org + "||" + this.govId);
			ststMap.setMap("LOG_DESC"  , "== START ==");
			ststMap.setMap("RESULT_CD" , "S");
			
			/*�ΰ������ڷ� ������ */
			cyberService.queryForUpdate("TXDM2420.INSERT_LOG_JOBSTATE", ststMap);
			
			this.startJob();
			
			ststMap.setMap("LOG_DESC"  , "== END  ���������������� �ڷῬ�� ó���Ǽ�::" + insert_cnt + ", (����ó��)�����Ǽ�::" + update_cnt + ", �����Ǽ�::" + delete_cnt);
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
		log.info("= govid["+this.govId+"], orgcd["+this.c_slf_org+"]");
		log.info("=====================================================================");
		
		/*Ʈ������� �����ϴ� �Ǿ����� ���⼭ �����Ѵ�...*/
		/*�Ǿ����� �����մϴ�.*/
		insert_cnt = 0;
		update_cnt = 0;
		delete_cnt = 0;
		
		ArrayList<MapForm> alBusFineList = null;
		
		/*���� ���������������ݰ��·� �ΰ������� �����´�.*/
		alBusFineList =  govService.queryForList("TXDM2430.SELECT_C_BUS_FINE_LIST", null);

		int t_cnt = alBusFineList.size();
		
		log.info("���������������� �ΰ��Ǽ� = " + t_cnt);
		
		int rec_cnt = 0;
		
		if (t_cnt  >  0)   {
			log.info("!!!���������������� �������!!!");
			
			for (rec_cnt = 0;  rec_cnt < t_cnt;  rec_cnt++)   {
				
				MapForm mfBusFineList =  alBusFineList.get(rec_cnt);
				
				if (mfBusFineList == null  ||  mfBusFineList.isEmpty() )   {
					log.debug("song continue");
					continue;
				}
				/*�ʱⰪ ����*/
				mfBusFineList.setMap("PROC_CLS"  , "1");
				mfBusFineList.setMap("DEL_YN"    , "N");
				mfBusFineList.setMap("SGG_TR_TG" , "0");
				
				
				try{
										
					/*==========�������̺� �Է� ===========*/
					if (cyberService.queryForUpdate("TXDM2430.INSERT_BUS_FINE_LEVY", mfBusFineList) > 0) {
						cyberService.queryForUpdate("TXDM2430.INSERT_BUS_FINE_DETAIL_LEVY", mfBusFineList);
						//��ø���
						//cyberService.queryForUpdate("TXDM2430.UPDATE_BUS_FINE_LEVY_OCRBD", mfBusFineList);
						
						insert_cnt++;
					}
					
				}catch (Exception e){

					/*�ߺ��� �߻��ϰų� �������ǿ� ��߳��� ���üũ*/
					if (e instanceof DuplicateKeyException){
						
						/*��������̺��� �ݾ� �� ������ ���ڸ� �����´�.*/
						MapForm mapState = cyberService.queryForMap("TXDM2430.SELECT_GET_AMT_DATE", mfBusFineList);
						
						/* �ݾ��̳� ������ �����϶��� �������ä�� �÷��� �����ؼ� ä�� */
						if ((mapState.getMap("V_DATE") != null) 
								&& ((((BigDecimal) mapState.getMap("V_AMT")).longValue() != ((BigDecimal) mfBusFineList.getMap("BUTT")).longValue())
										|| (mfBusFineList.getMap("DUE_F_DT") != mapState.getMap("V_DATE")))) {
							

							
							mfBusFineList.setMap("UP_GB","01");
							cyberService.queryForUpdate("TXDM2430.UPDATE_TX2122_TB", mfBusFineList);
							
						}
						
						try{
							

						    
							if (cyberService.queryForUpdate("TXDM2430.UPDATE_BUS_FINE_DETAIL_LEVY", mfBusFineList) > 0) {
								cyberService.queryForUpdate("TXDM2430.UPDATE_BUS_FINE_LEVY", mfBusFineList);
								//cyberService.queryForUpdate("TXDM2430.UPDATE_BUS_FINE_LEVY_OCRBD", mfBusFineList);

							    update_cnt++;
							}
							
						}catch (Exception sub_e){
							if (sub_e instanceof DuplicateKeyException || sub_e instanceof DataIntegrityViolationException){
								log.error("���������� = " + mfBusFineList.getMaps());
								sub_e.printStackTrace();
								throw (RuntimeException) sub_e;
							} else {
								log.error("���������� = " + mfBusFineList.getMaps());
								sub_e.printStackTrace();
							}
						}
						
					} else {
						log.error("���������� = " + mfBusFineList.getMaps());
						e.printStackTrace();
						throw (RuntimeException) e;
					}
					
				}
				mfBusFineList.setMap("NON_UP_GB","01");
				govService.queryForUpdate("TXDM2430.UPDATE_NONTAX_TAB_LINK", mfBusFineList);
				
			}
			
			/***********************************************************************************/
			try{
				MapForm daemonMap = new MapForm();
				daemonMap.setMap("DAEMON"    , "TXDM2430");
				daemonMap.setMap("DAEMON_NM" , "��������(NONTAX_TAB)�ΰ�����");
				daemonMap.setMap("SGG_COD"   , c_slf_org);
				daemonMap.setMap("TOTAL_CNT" , t_cnt);
				daemonMap.setMap("INSERT_CNT", insert_cnt);
				daemonMap.setMap("UPDATE_CNT", update_cnt);
				daemonMap.setMap("DELETE_CNT", 0);
				daemonMap.setMap("ERROR_CNT" , 0);
				cyberService.queryForInsert("CODMBASE.InsertDaemonProcessCheck", daemonMap);
			}catch(Exception ex){
				log.debug("��������(NONTAX_TAB)�ΰ����� �α� ��� ����");
			}				
			/***********************************************************************************/
			
		}else{
			
			/***********************************************************************************/
			try{
				MapForm daemonMap = new MapForm();
				daemonMap.setMap("DAEMON"    , "TXDM2430");
				daemonMap.setMap("DAEMON_NM" , "��������(NONTAX_TAB)�ΰ�����");
				daemonMap.setMap("SGG_COD"   , c_slf_org);
				daemonMap.setMap("TOTAL_CNT" , 0);
				daemonMap.setMap("INSERT_CNT", 0);
				daemonMap.setMap("UPDATE_CNT", 0);
				daemonMap.setMap("DELETE_CNT", 0);
				daemonMap.setMap("ERROR_CNT" , 0);
				cyberService.queryForInsert("CODMBASE.InsertDaemonProcessCheck", daemonMap);
			}catch(Exception ex){
				log.debug("��������(NONTAX_TAB)�ΰ����� ����� �����ϴ�. ��� ����");
			}				
			/***********************************************************************************/
			
		}
		ArrayList<MapForm> alBusFineDelList = null;
		
		/*���������������ݰ��·�  �����ڷ� ���� ���� */
		alBusFineDelList =  govService.queryForList("TXDM2430.SELECT_C_BUS_FINE_GOJI_LIST", null);
		
		if (alBusFineDelList.size()  >  0) {
			
			for (rec_cnt = 0;  rec_cnt < alBusFineDelList.size();  rec_cnt++)   {
				
				MapForm mfBusFineDelList =  alBusFineDelList.get(rec_cnt);
				
				if (mfBusFineDelList == null  ||  mfBusFineDelList.isEmpty() )   {
					continue;
				}
				
				/*���̹� ���忡 ���� FLAG�� �����.*/
				mfBusFineDelList.setMap("UP_GB","02");
				cyberService.queryForUpdate("TXDM2430.UPDATE_TX2122_TB", mfBusFineDelList);
				
				/*������忡 ������Ʈ*/
				mfBusFineDelList.setMap("NON_UP_GB","02");
				govService.queryForUpdate("TXDM2430.UPDATE_NONTAX_TAB_LINK", mfBusFineDelList);
			}
			
		}
		
		log.info("���������������� �ڷῬ�� �Ǽ�::" + alBusFineList.size() + ", ��ϰǼ�::" + insert_cnt +", �����Ǽ�::" + update_cnt + ", �����Ǽ�::" + delete_cnt);
		
	}
	
	
}
