/**
 *  �ֽý��۸� : �λ��û ���̹� ���漼û
 *  ��  ��  �� : ����
 *  ��  ��  �� : �ְ������������� �ΰ��ڷ� ����(�õ�) ����
 *  Ŭ����  ID : Txdm2440_process : �� ó�������� �ۼ��ϴ� Ŭ����
 *  ����  �̷� :
 * ------------------------------------------------------------------------
 *  �ۼ���      	�Ҽ�  		    ����            Tag           ����
 * ------------------------------------------------------------------------
 *  �۵���       ��ä��(��)      2011.09.20         %01%         �����ۼ�
 */
package com.uc.bs.cyber.daemon.txdm2440;

import java.math.BigDecimal;
import java.util.ArrayList;

import org.apache.commons.logging.LogFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.context.ApplicationContext;

import com.uc.bs.cyber.CbUtil;
import com.uc.bs.cyber.daemon.Codm_BaseProcess;
import com.uc.bs.cyber.daemon.Codm_interface;
import com.uc.core.MapForm;
import com.uc.core.spring.service.UcContextHolder;

/**
 * @author Administrator
 *
 */
public class Txdm2440_process extends Codm_BaseProcess implements Codm_interface {

	private MapForm dataMap  = null;
	private int insert_cnt = 0, update_cnt = 0, delete_cnt = 0;
	
	/**
	 * 
	 */
	public Txdm2440_process() {
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
	
	public String getDataSource() {
		return this.dataSource;
	}	
	
	/*Ʈ������� �����ϱ� ���� �Լ�.*/
	private void mainTransProcess(){
		
		try {
			
			int page = 0;
			
			setApp(appContext);
			
			UcContextHolder.setCustomerType(getDataSource());
			
			dataMap = new MapForm();  /*�ʱ�ȭ*/
			dataMap.setMap("PAGE_PER_CNT",  500);           /*�������� ����*/
			
			if(jobId == 0) {
				
					page = govService.getOneFieldInteger("TXDM2440.SELECT_C_JUGEOJI_FINE_CNT", dataMap);

					
					log.info("[�ְ�������������] PAGE_PER_CNT(" + dataMap.getMap("PAGE_PER_CNT") + ") �� Page���� =" + page);
					
					dataMap.setMap("GUBUN",  "1");
					
					if(page > 0){
					
						for(int i = 1 ; i <= page ; i ++) {
							
							MapForm ststMap = new MapForm();
							
							ststMap.setMap("JOB_ID"    , "TXDM2440");
							ststMap.setMap("PARM_NM"   , this.c_slf_org + "||" + this.govId);
							ststMap.setMap("LOG_DESC"  , "== START ==");
							ststMap.setMap("RESULT_CD" , "S");
							/*�ΰ������ڷ� ������ */
							cyberService.queryForUpdate("TXDM2420.INSERT_LOG_JOBSTATE", ststMap);
							
							dataMap.setMap("PAGE",  i);    /*ó��������*/
							
							this.startJob();
							
							ststMap.setMap("LOG_DESC"  , "== END  �ְ������������� �ڷῬ�� ó���Ǽ�::" + insert_cnt + ", (����ó��)�����Ǽ�::" + update_cnt + ", �����Ǽ�::" + delete_cnt);
							ststMap.setMap("RESULT_CD" , "E");
							
							/*�ΰ������ڷ� ���Ϸ� */
							cyberService.queryForUpdate("TXDM2420.INSERT_LOG_JOBSTATE", ststMap);
						}
					
					}else{
						
						/***********************************************************************************/
						try{
							MapForm daemonMap = new MapForm();
							daemonMap.setMap("DAEMON"    , "TXDM2440");
							daemonMap.setMap("DAEMON_NM" , "�ְ���(NONTAX_TAB)�ΰ�����");
							daemonMap.setMap("SGG_COD"   , c_slf_org);
							daemonMap.setMap("TOTAL_CNT" , 0);
							daemonMap.setMap("INSERT_CNT", 0);
							daemonMap.setMap("UPDATE_CNT", 0);
							daemonMap.setMap("DELETE_CNT", 0);
							daemonMap.setMap("ERROR_CNT" , 0);
							cyberService.queryForInsert("CODMBASE.InsertDaemonProcessCheck", daemonMap);
						}catch(Exception ex){
							log.debug("�ְ���(NONTAX_TAB)�ΰ����� ����� �����ϴ�. ��� ����");
						}				
						/***********************************************************************************/
						
					}
					
				
			} else if(jobId == 1) {
				
					page = govService.getOneFieldInteger("TXDM2440.SELECT_C_JUGEOJI_FINE_DEL_CNT", dataMap);
					
					log.info("[�ְ������������� �����ڷ�] PAGE_PER_CNT(" + dataMap.getMap("PAGE_PER_CNT") + ") �� Page���� =" + page);
					
					dataMap.setMap("GUBUN",  "2");
					
					if(page > 0) {
					
						for(int i = 1 ; i <= page ; i ++) {
												
							dataMap.setMap("PAGE",  i);    /*ó��������*/					
							this.startJob();
						}
					
					}
				
			} else if(jobId == 2) {
					
					page = govService.getOneFieldInteger("TXDM2440.SELECT_DEL_NONSOIN_CNT", dataMap);
					
					log.info("[�ְ������������� ���λ����ڷ�] PAGE_PER_CNT(" + dataMap.getMap("PAGE_PER_CNT") + ") �� Page���� =" + page);
					
					dataMap.setMap("GUBUN",  "3");
					
					if(page > 0) {
					
						for(int i = 1 ; i <= page ; i ++) {
												
							dataMap.setMap("PAGE",  i);    /*ó��������*/					
							this.startJob();
						}
					
					}
				
			}
			

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}			
		
	}
	
	@Override
	public void runProcess() throws Exception {
		// TODO Auto-generated method stub
		
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
	
		if(dataMap.getMap("GUBUN").equals("1")) {
			txdm2440_JobProcess1();
		} else if(dataMap.getMap("GUBUN").equals("2")) {
			txdm2440_JobProcess2();
		} else if(dataMap.getMap("GUBUN").equals("3")) {
			txdm2440_JobProcess3();
		}

	}
	
	/**
	 * �ΰ��ڷ� ����
	 * @return
	 */
	private int txdm2440_JobProcess1() {
		
		log.info("=====================================================================");
		log.info("=" + this.getClass().getName()+ " txdm2440_JobProcess1()[�ְ������������� �ΰ��ڷῬ��] Start =");
		log.info("=====================================================================");
		
		/*Ʈ������� �����ϴ� �Ǿ����� ���⼭ �����Ѵ�...*/
		/*�Ǿ����� �����մϴ�.*/
		insert_cnt = 0;
		update_cnt = 0;
		delete_cnt = 0;
		
		long elapseTime1 = 0;
		long elapseTime2 = 0;
		
		int t_cnt = 0;
		ArrayList<MapForm> alDwellParkList = null;
		
		try{
			elapseTime1 = System.currentTimeMillis();
			
			/*���� �ְ������������� �ΰ������� �����´�.*/
			alDwellParkList =  govService.queryForList("TXDM2440.SELECT_C_JUGEOJI_FINE_LIST", dataMap);
			
			t_cnt = alDwellParkList.size();
			
			log.info("�ְ������������� �ΰ��Ǽ� = " + t_cnt);
			
			int rec_cnt = 0;
			
			if (t_cnt  >  0)   {
				
				for (rec_cnt = 0; rec_cnt < t_cnt; rec_cnt++)   {
					
					MapForm mfDwellParkList =  alDwellParkList.get(rec_cnt);
					
					if (mfDwellParkList == null  ||  mfDwellParkList.isEmpty() )   {
						continue;
					}
					/*�ʱⰪ ����*/
					mfDwellParkList.setMap("TAX_CNT"   ,  0 );
					mfDwellParkList.setMap("PROC_CLS"  , "1");
					mfDwellParkList.setMap("DEL_YN"    , "N");
					mfDwellParkList.setMap("SGG_TR_TG" , "0");
					
					try{
						
						mfDwellParkList.setMap("CUD_OPT" , "1");
						
						/*==========�������̺� �Է� ===========*/
						if (cyberService.queryForUpdate("TXDM2440.INSERT_BUS_FINE_LEVY", mfDwellParkList) > 0) {
							cyberService.queryForUpdate("TXDM2440.INSERT_BUS_FINE_DETAIL_LEVY", mfDwellParkList);
							
							insert_cnt++;
						}
						
					}catch (Exception e){

						/*�ߺ��� �߻��ϰų� �������ǿ� ��߳��� ���üũ*/
						if (e instanceof DuplicateKeyException){
							
							mfDwellParkList.setMap("CUD_OPT" , "2");
							
							/*��������̺��� �ݾ� �� ������ ���ڸ� �����´�.*/
							MapForm mapState = cyberService.queryForMap("TXDM2440.SELECT_GET_AMT_DATE", mfDwellParkList);
							
							/* �ݾ��̳� ������ �����϶��� �������ä�� �÷��� �����ؼ� ä�� */
							if ((mapState.getMap("V_DATE") != null) 
									&& ((((BigDecimal) mapState.getMap("V_AMT")).longValue() != ((BigDecimal) mfDwellParkList.getMap("BUTT")).longValue())
											|| (mfDwellParkList.getMap("DUE_F_DT") != mapState.getMap("V_DATE")))) {

								mfDwellParkList.setMap("UP_GB","01");
								cyberService.queryForUpdate("TXDM2440.UPDATE_TX2122_TB", mfDwellParkList);
							}
							
							try{

								if (cyberService.queryForUpdate("TXDM2440.UPDATE_BUS_FINE_DETAIL_LEVY", mfDwellParkList) > 0) {
									cyberService.queryForUpdate("TXDM2440.UPDATE_BUS_FINE_LEVY", mfDwellParkList);

								    update_cnt++;
								}
								
							}catch (Exception sub_e){
								if (sub_e instanceof DuplicateKeyException || sub_e instanceof DataIntegrityViolationException){
									log.error("���������� = " + mfDwellParkList.getMaps());
									sub_e.printStackTrace();
									throw (RuntimeException) sub_e;
								} else {
									log.error("���������� = " + mfDwellParkList.getMaps());
									sub_e.printStackTrace();
								}
							}
							
						} else {
							log.error("���������� = " + mfDwellParkList.getMaps());
							e.printStackTrace();
							throw (RuntimeException) e;
						}
						
					}
					
					govService.queryForUpdate("TXDM2440.UPDATE_NONTAX_TAB_LINK", mfDwellParkList);
				}
				
			}
			elapseTime2 = System.currentTimeMillis();
			
			log.info("�ְ������������� �ڷῬ�� �Ǽ�::(" + t_cnt + "), ��ϰǼ�::" + insert_cnt +", �����Ǽ�::" + update_cnt + " ::ó���ð� " + CbUtil.formatTime(elapseTime2 - elapseTime1));
			
			/***********************************************************************************/
			try{
				MapForm daemonMap = new MapForm();
				daemonMap.setMap("DAEMON"    , "TXDM2440");
				daemonMap.setMap("DAEMON_NM" , "�ְ���(NONTAX_TAB)�ΰ�����");
				daemonMap.setMap("SGG_COD"   , c_slf_org);
				daemonMap.setMap("TOTAL_CNT" , t_cnt);
				daemonMap.setMap("INSERT_CNT", insert_cnt);
				daemonMap.setMap("UPDATE_CNT", update_cnt);
				daemonMap.setMap("DELETE_CNT", 0);
				daemonMap.setMap("ERROR_CNT" , 0);
				cyberService.queryForInsert("CODMBASE.InsertDaemonProcessCheck", daemonMap);
			}catch(Exception ex){
				log.debug("�ְ���(NONTAX_TAB)�ΰ����� �α� ��� ����");
			}				
			/***********************************************************************************/
			
		}catch (Exception e){
			throw (RuntimeException) e;
		}
		
		return t_cnt;
		
	}
	
	/**
	 * �ΰ������ڷῬ��
	 * @return
	 */
    private int txdm2440_JobProcess2() {
    	
		log.info("=====================================================================");
		log.info("=" + this.getClass().getName()+ " txdm2440_JobProcess2()[�ְ������������� �����ڷῬ��] Start =");
		log.info("=====================================================================");
    	
		long elapseTime1 = 0;
		long elapseTime2 = 0;
		
		ArrayList<MapForm> alDwellPrkDelList = null;

		int d_cnt = 0, rec_cnt = 0;
		
		try{
			
			elapseTime1 = System.currentTimeMillis();
			
			/*�ְ������������� �����ڷ� ���� ���� */
			alDwellPrkDelList =  govService.queryForList("TXDM2440.SELECT_C_JUGEOJI_FINE_DEL_LIST", dataMap);
			
			if (alDwellPrkDelList.size()  >  0) {

				for (rec_cnt = 0;  rec_cnt < alDwellPrkDelList.size();  rec_cnt++)   {
					
					MapForm mfDwellPrkDelList =  alDwellPrkDelList.get(rec_cnt);
					
					if (mfDwellPrkDelList == null  ||  mfDwellPrkDelList.isEmpty() )   {
						continue;
					}
					
					try{

						/*���̹� ���忡 ���� FLAG�� �����.*/
						mfDwellPrkDelList.setMap("UP_GB","02");
						cyberService.queryForUpdate("TXDM2440.UPDATE_TX2122_TB", mfDwellPrkDelList);
						
						/*������忡 ������Ʈ*/
						govService.queryForUpdate("TXDM2440.UPDATE_NONTAX_TAB_LINK", mfDwellPrkDelList);
						
						d_cnt ++;
						
					}catch (Exception sub_e){
						
						if (sub_e instanceof DuplicateKeyException || sub_e instanceof DataIntegrityViolationException){
							log.error("���������� = " + mfDwellPrkDelList.getMaps());
							sub_e.printStackTrace();
							throw (RuntimeException) sub_e;
						} else {
							log.error("���������� = " + mfDwellPrkDelList.getMaps());
							sub_e.printStackTrace();
						}
						
					}

				}
				
			}

			elapseTime2 = System.currentTimeMillis();
			
			log.info("�ְ������������� ���� �ڷῬ�� �Ǽ�::" + alDwellPrkDelList.size() + ", �����Ǽ�::" + d_cnt + " ::ó���ð� " + CbUtil.formatTime(elapseTime2 - elapseTime1));
			
		}catch (Exception e){
			throw (RuntimeException) e;
		}

		return alDwellPrkDelList.size();
	}

    /**
     * ����ó���ڷ� ����
     * @return
     */
    private int txdm2440_JobProcess3() {
    	
		log.info("=====================================================================");
		log.info("=" + this.getClass().getName()+ " txdm2440_JobProcess3()[�ְ������������� ���λ����ڷῬ��] Start =");
		log.info("=====================================================================");
    	
		long elapseTime1 = 0;
		long elapseTime2 = 0;
		
		ArrayList<MapForm> alDwellPrkSoinList = null;

		int s_cnt = 0, rec_cnt = 0;
		
		try{
			
			elapseTime1 = System.currentTimeMillis();
			
			/*�ְ������������� �����ڷ� ���̹�������� ���� */
			alDwellPrkSoinList =  govService.queryForList("TXDM2440.SELECT_DEL_NONSOIN_LIST", dataMap);
			
			if (alDwellPrkSoinList.size()  >  0) {
				
				for (rec_cnt = 0;  rec_cnt < alDwellPrkSoinList.size();  rec_cnt++)   {
					
					MapForm mfDwellPrkSoinList =  alDwellPrkSoinList.get(rec_cnt);
					
					if (mfDwellPrkSoinList == null  ||  mfDwellPrkSoinList.isEmpty() )   {
						continue;
					}
					
					/*���̹� ���忡 ���� FLAG�� �����.*/
					mfDwellPrkSoinList.setMap("UP_GB"   , "02");
					
					mfDwellPrkSoinList.setMap("SGG_COD" , mfDwellPrkSoinList.getMap("NNS_SGCD"));
					mfDwellPrkSoinList.setMap("ACCT_COD", mfDwellPrkSoinList.getMap("NNS_ACKU"));
					mfDwellPrkSoinList.setMap("TAX_ITEM", mfDwellPrkSoinList.getMap("NNS_SMCD"));
					mfDwellPrkSoinList.setMap("TAX_YY"  , mfDwellPrkSoinList.getMap("NNS_BYYY"));
					mfDwellPrkSoinList.setMap("TAX_MM"  , mfDwellPrkSoinList.getMap("NNS_BNGI"));
					mfDwellPrkSoinList.setMap("TAX_DIV" , mfDwellPrkSoinList.getMap("NNS_GIBN"));
					mfDwellPrkSoinList.setMap("HACD"    , mfDwellPrkSoinList.getMap("NNS_HACD"));

					try{

						/*���̹� ���� ���� FLAG�� �����. */
						cyberService.queryForUpdate("TXDM2440.UPDATE_TX2122_TB", mfDwellPrkSoinList);
						
						/*������忡 ������Ʈ*/
						govService.queryForUpdate("TXDM2440.UPDATE_NNONSOIN_TAB_LINK", mfDwellPrkSoinList);
						
						s_cnt++;
						
					}catch (Exception sub_e){
						
						if (sub_e instanceof DuplicateKeyException || sub_e instanceof DataIntegrityViolationException){
							log.error("���������� = " + mfDwellPrkSoinList.getMaps());
							sub_e.printStackTrace();
							throw (RuntimeException) sub_e;
						} else {
							log.error("���������� = " + mfDwellPrkSoinList.getMaps());
							sub_e.printStackTrace();
						}
						
					}
					
				}
				
			}
			
			elapseTime2 = System.currentTimeMillis();

			log.info("�ְ������������� ���λ����ڷῬ�� �Ǽ�::" + alDwellPrkSoinList.size() + ", �����Ǽ�::" + s_cnt + " ::ó���ð� " + CbUtil.formatTime(elapseTime2 - elapseTime1));
			
		}catch (Exception e){
			throw (RuntimeException) e;
		}

		return alDwellPrkSoinList.size();
    }
    

}
