/**
 *  �ֽý��۸� : �λ��û ���̹� ���漼û
 *  ��  ��  �� : ����
 *  ��  ��  �� : ���ϼ��� ���� �ΰ��ڷ� ��������.
 *  Ŭ����  ID : Txdm4314_process : �� ó�������� �ۼ��ϴ� Ŭ����
 *  ����  �̷� :
 * ------------------------------------------------------------------------
 *  �ۼ���      	�Ҽ�  		    ����            Tag           ����
 * ------------------------------------------------------------------------
 *  �ڻ��          �ٻ�ý���      2011.11.18         %01%         �����ۼ�
 */
package com.uc.bs.cyber.daemon.txdm4314;

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
public class Txdm4314_process extends Codm_BaseProcess implements Codm_interface {

	private MapForm dataMap  = null;
	
	private int insert_cnt = 0, update_cnt = 0, delete_cnt = 0, tot_cnt = 0;
	
	/**
	 * 
	 */
	public Txdm4314_process() {
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

	/*���μ��� ����*/
	@Override
	public void runProcess() throws Exception {
		// TODO Auto-generated method stub
		
		/*Ʈ����� ����*/
		mainTransProcess();
	}

	/*Context ���Կ�*/
	@Override
	public void setDatasrc(String datasrc) {
		// TODO Auto-generated method stub
		this.dataSource = datasrc;
	}

	@Override
	public void transactionJob() {
		// TODO Auto-generated method stub
		txsv4314_JobProcess();
	}


	/*Ʈ������� �����ϱ� ���� �Լ�.*/
	private void mainTransProcess(){
		
		
		try {
			
			setContext(appContext);
			setApp(appContext);

			UcContextHolder.setCustomerType(this.dataSource);
			
			dataMap = new MapForm();  /*�ʱ�ȭ*/
			dataMap.setMap("PAGE_PER_CNT" , 1000);            /*�������� ����*/
			dataMap.setMap("SR_FLAG"      , "0");             /*������ ��*/
			
			do {
				
				int page = govService.getOneFieldInteger("TXSV4314.select_count_page", dataMap);
				
				log.info("[���ϼ��� ����� �ΰ��ڷ� (" + c_slf_org_nm + ")] PAGE_PER_CNT(" + dataMap.getMap("PAGE_PER_CNT") + ") �� Page���� =" + page);
				
				if(page == 0){
					
					/***********************************************************************************/
					try{
						MapForm daemonMap = new MapForm();
						daemonMap.setMap("DAEMON"    , "TXDM4314");
						daemonMap.setMap("DAEMON_NM" , "���ϼ��������(CYBER_PRTFEE)�ΰ��ڷῬ��");
						daemonMap.setMap("SGG_COD"   , c_slf_org);
						daemonMap.setMap("TOTAL_CNT" , 0);
						daemonMap.setMap("INSERT_CNT", 0);
						daemonMap.setMap("UPDATE_CNT", 0);
						daemonMap.setMap("DELETE_CNT", 0);
						daemonMap.setMap("ERROR_CNT" , 0);
						cyberService.queryForInsert("CODMBASE.InsertDaemonProcessCheck", daemonMap);
					}catch(Exception ex){
						log.debug("���ϼ��������(CYBER_PRTFEE)�ΰ��ڷῬ�� ����� �����ϴ�. ��� ����");
					}				
					/***********************************************************************************/
					
					break;
				}

				for(int i = 1 ; i <= page ; i ++) {
					dataMap.setMap("PAGE",  i);    /*ó��������*/
					this.startJob();               /*��Ƽ Ʈ����� ȣ��*/
				}
								
				if(govService.getOneFieldInteger("TXSV4314.select_count_page", dataMap) == 0) {
					break;
				}
				
			}while(true);
						
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}			

	}
	
	/*�ڷ� ����*/
	private int txsv4314_JobProcess() {
		
		log.info("=====================================================================");
		log.info("=" + this.getClass().getName()+ " txsv4314_JobProcess() [���ϼ��� ���� �ڷ� ����] Start =");
		log.info("= govid["+this.govId+"], orgcd["+this.c_slf_org+"], orgcd["+this.c_slf_org_nm+"]");
		log.info("=====================================================================");
		
		long queryElapse1 = 0;
		long queryElapse2 = 0;
		
		long elapseTime1 = 0;
		long elapseTime2 = 0;
		
		int sudoCnt = 0;
		
		insert_cnt = 0;
		update_cnt = 0; 
		delete_cnt = 0;
		tot_cnt = 0;
		
		try {
			elapseTime1 = System.currentTimeMillis();
			
			queryElapse1 = System.currentTimeMillis();
			
			/*���ϼ��� ����п� �ΰ������� �����´�.*/
			ArrayList<MapForm> alFixedLevyList =  govService.queryForList("TXSV4314.sudo_select_data", dataMap);
			
			queryElapse2 = System.currentTimeMillis();
			
			sudoCnt = alFixedLevyList.size();

			MapForm mpTaxFixLevyList = new MapForm();
			
			log.info("[" + c_slf_org_nm + "]���ϼ��� ����� ������ȸ �ð� : " + CbUtil.formatTime(queryElapse2 - queryElapse1) + " (" + sudoCnt + ")");
			
			if (sudoCnt  >  0)   {

				for ( int rec_cnt = 0;  rec_cnt < sudoCnt;  rec_cnt++)   {

					mpTaxFixLevyList =  alFixedLevyList.get(rec_cnt);
					
					if (mpTaxFixLevyList == null  ||  mpTaxFixLevyList.isEmpty()) continue;
					
					String trt_sp = (String) mpTaxFixLevyList.getMap("STATE_FLAG");   /*ó������ I:Insert, D : ���� */
					
					log.info("=============================" + trt_sp);
					log.info("=============================" + mpTaxFixLevyList.getMap("STATE_FLAG"));
					
					
					if(trt_sp.equals("I")) {
						
						try{// STATE_FLAG �� 'I'�ϰ�� insert
							cyberService.queryForList("TXSV4314.sudo_insert_data", mpTaxFixLevyList);
							insert_cnt++;
						}catch(Exception e){
							
							if (e instanceof DuplicateKeyException){
								//insert �Ҷ� Ű ���������� �ɸ� ��( ����Ÿ�� �����Ƿ� ������Ʈ)
								try{
									cyberService.queryForList("TXSV4314.sudo_update_data", mpTaxFixLevyList);
									update_cnt++;
								}catch(Exception e1){
									log.error("���������� = " + mpTaxFixLevyList.getMaps());
									log.error(e.getMessage());
									e.printStackTrace();
									throw (RuntimeException) e1;
								}
							} else {
								
								log.error("���������� = " + mpTaxFixLevyList.getMaps());
								log.error(e.getMessage());
								e.printStackTrace();
								throw (RuntimeException) e;
								
							}
						}
					} else if(trt_sp.equals("D") ){
						
						try{//STATE_FLAG�� 'D'�� ��� ����
							cyberService.queryForList("TXSV4314.new_sudo_delete_data", mpTaxFixLevyList);
							delete_cnt++;
						}catch(Exception e){
							log.error("���������� = " + mpTaxFixLevyList.getMaps());
							log.error(e.getMessage());
							e.printStackTrace();
							throw (RuntimeException) e;
						}
						
					}
					
					try {
						
						govService.queryForList("TXSV4314.sudo_update_srflag", mpTaxFixLevyList);
						
						tot_cnt++;
						
					} catch (Exception b){
						
						log.error("���������� = " + mpTaxFixLevyList.getMaps());
						log.error(b.getMessage());
						b.printStackTrace();
						throw (RuntimeException) b;
					}
				}
			}
			elapseTime2 = System.currentTimeMillis();
			
			log.info("���ϼ��� ����� �����ڷ� ����ð� : " + CbUtil.formatTime(elapseTime2 - elapseTime1));
			log.info("�ѿ���[" + tot_cnt + "]" + "INSERT �Ǽ�" + "["+insert_cnt+"]" +"UPDATE �Ǽ�" + "["+update_cnt+"]"+"delete �Ǽ�" + "["+delete_cnt+"]" );
			
			/***********************************************************************************/
			try{
				MapForm daemonMap = new MapForm();
				daemonMap.setMap("DAEMON"    , "TXDM4314");
				daemonMap.setMap("DAEMON_NM" , "���ϼ��������(CYBER_PRTFEE)�ΰ��ڷῬ��");
				daemonMap.setMap("SGG_COD"   , c_slf_org);
				daemonMap.setMap("TOTAL_CNT" , tot_cnt);
				daemonMap.setMap("INSERT_CNT", insert_cnt);
				daemonMap.setMap("UPDATE_CNT", update_cnt);
				daemonMap.setMap("DELETE_CNT", delete_cnt);
				daemonMap.setMap("ERROR_CNT" , 0);
				cyberService.queryForInsert("CODMBASE.InsertDaemonProcessCheck", daemonMap);
			}catch(Exception ex){
				log.debug("���ϼ��������(CYBER_PRTFEE)�ΰ��ڷῬ�� �α� ��� ����");
			}				
			/***********************************************************************************/
			
			
		}catch (Exception e){
			e.printStackTrace();
			log.error(e.getMessage());
		}
		
		return sudoCnt;
		
	}
	

}
