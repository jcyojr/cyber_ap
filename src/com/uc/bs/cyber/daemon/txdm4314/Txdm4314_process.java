/**
 *  주시스템명 : 부산시청 사이버 지방세청
 *  업  무  명 : 연계
 *  기  능  명 : 상하수도 정기 부과자료 가져오기.
 *  클래스  ID : Txdm4314_process : 실 처리로직을 작성하는 클래스
 *  변경  이력 :
 * ------------------------------------------------------------------------
 *  작성자      	소속  		    일자            Tag           내용
 * ------------------------------------------------------------------------
 *  박상규          다산시스템      2011.11.18         %01%         최초작성
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
		 * 5 분마다 돈다
		 */
		loopTerm = 300;
	}

	/*Context 주입용*/
	public void setApp(ApplicationContext context) {
		this.context = context;
	}

	/*프로세스 시작*/
	@Override
	public void runProcess() throws Exception {
		// TODO Auto-generated method stub
		
		/*트랜잭션 시작*/
		mainTransProcess();
	}

	/*Context 주입용*/
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


	/*트랜잭션을 실행하기 위한 함수.*/
	private void mainTransProcess(){
		
		
		try {
			
			setContext(appContext);
			setApp(appContext);

			UcContextHolder.setCustomerType(this.dataSource);
			
			dataMap = new MapForm();  /*초기화*/
			dataMap.setMap("PAGE_PER_CNT" , 1000);            /*페이지당 갯수*/
			dataMap.setMap("SR_FLAG"      , "0");             /*미전송 분*/
			
			do {
				
				int page = govService.getOneFieldInteger("TXSV4314.select_count_page", dataMap);
				
				log.info("[상하수도 정기분 부과자료 (" + c_slf_org_nm + ")] PAGE_PER_CNT(" + dataMap.getMap("PAGE_PER_CNT") + ") 당 Page갯수 =" + page);
				
				if(page == 0){
					
					/***********************************************************************************/
					try{
						MapForm daemonMap = new MapForm();
						daemonMap.setMap("DAEMON"    , "TXDM4314");
						daemonMap.setMap("DAEMON_NM" , "상하수도정기분(CYBER_PRTFEE)부과자료연계");
						daemonMap.setMap("SGG_COD"   , c_slf_org);
						daemonMap.setMap("TOTAL_CNT" , 0);
						daemonMap.setMap("INSERT_CNT", 0);
						daemonMap.setMap("UPDATE_CNT", 0);
						daemonMap.setMap("DELETE_CNT", 0);
						daemonMap.setMap("ERROR_CNT" , 0);
						cyberService.queryForInsert("CODMBASE.InsertDaemonProcessCheck", daemonMap);
					}catch(Exception ex){
						log.debug("상하수도정기분(CYBER_PRTFEE)부과자료연계 대상이 없습니다. 등록 오류");
					}				
					/***********************************************************************************/
					
					break;
				}

				for(int i = 1 ; i <= page ; i ++) {
					dataMap.setMap("PAGE",  i);    /*처리페이지*/
					this.startJob();               /*멀티 트랜잭션 호출*/
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
	
	/*자료 연계*/
	private int txsv4314_JobProcess() {
		
		log.info("=====================================================================");
		log.info("=" + this.getClass().getName()+ " txsv4314_JobProcess() [상하수도 고지 자료 연계] Start =");
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
			
			/*상하수도 정기분외 부과내역을 가져온다.*/
			ArrayList<MapForm> alFixedLevyList =  govService.queryForList("TXSV4314.sudo_select_data", dataMap);
			
			queryElapse2 = System.currentTimeMillis();
			
			sudoCnt = alFixedLevyList.size();

			MapForm mpTaxFixLevyList = new MapForm();
			
			log.info("[" + c_slf_org_nm + "]상하수도 정기분 쿼리조회 시간 : " + CbUtil.formatTime(queryElapse2 - queryElapse1) + " (" + sudoCnt + ")");
			
			if (sudoCnt  >  0)   {

				for ( int rec_cnt = 0;  rec_cnt < sudoCnt;  rec_cnt++)   {

					mpTaxFixLevyList =  alFixedLevyList.get(rec_cnt);
					
					if (mpTaxFixLevyList == null  ||  mpTaxFixLevyList.isEmpty()) continue;
					
					String trt_sp = (String) mpTaxFixLevyList.getMap("STATE_FLAG");   /*처리구분 I:Insert, D : 삭제 */
					
					log.info("=============================" + trt_sp);
					log.info("=============================" + mpTaxFixLevyList.getMap("STATE_FLAG"));
					
					
					if(trt_sp.equals("I")) {
						
						try{// STATE_FLAG 가 'I'일경우 insert
							cyberService.queryForList("TXSV4314.sudo_insert_data", mpTaxFixLevyList);
							insert_cnt++;
						}catch(Exception e){
							
							if (e instanceof DuplicateKeyException){
								//insert 할때 키 제약조건이 걸릴 때( 데이타가 있으므로 업데이트)
								try{
									cyberService.queryForList("TXSV4314.sudo_update_data", mpTaxFixLevyList);
									update_cnt++;
								}catch(Exception e1){
									log.error("오류데이터 = " + mpTaxFixLevyList.getMaps());
									log.error(e.getMessage());
									e.printStackTrace();
									throw (RuntimeException) e1;
								}
							} else {
								
								log.error("오류데이터 = " + mpTaxFixLevyList.getMaps());
								log.error(e.getMessage());
								e.printStackTrace();
								throw (RuntimeException) e;
								
							}
						}
					} else if(trt_sp.equals("D") ){
						
						try{//STATE_FLAG가 'D'일 경우 삭제
							cyberService.queryForList("TXSV4314.new_sudo_delete_data", mpTaxFixLevyList);
							delete_cnt++;
						}catch(Exception e){
							log.error("오류데이터 = " + mpTaxFixLevyList.getMaps());
							log.error(e.getMessage());
							e.printStackTrace();
							throw (RuntimeException) e;
						}
						
					}
					
					try {
						
						govService.queryForList("TXSV4314.sudo_update_srflag", mpTaxFixLevyList);
						
						tot_cnt++;
						
					} catch (Exception b){
						
						log.error("오류데이터 = " + mpTaxFixLevyList.getMaps());
						log.error(b.getMessage());
						b.printStackTrace();
						throw (RuntimeException) b;
					}
				}
			}
			elapseTime2 = System.currentTimeMillis();
			
			log.info("상하수도 정기분 고지자료 연계시간 : " + CbUtil.formatTime(elapseTime2 - elapseTime1));
			log.info("총연계[" + tot_cnt + "]" + "INSERT 건수" + "["+insert_cnt+"]" +"UPDATE 건수" + "["+update_cnt+"]"+"delete 건수" + "["+delete_cnt+"]" );
			
			/***********************************************************************************/
			try{
				MapForm daemonMap = new MapForm();
				daemonMap.setMap("DAEMON"    , "TXDM4314");
				daemonMap.setMap("DAEMON_NM" , "상하수도정기분(CYBER_PRTFEE)부과자료연계");
				daemonMap.setMap("SGG_COD"   , c_slf_org);
				daemonMap.setMap("TOTAL_CNT" , tot_cnt);
				daemonMap.setMap("INSERT_CNT", insert_cnt);
				daemonMap.setMap("UPDATE_CNT", update_cnt);
				daemonMap.setMap("DELETE_CNT", delete_cnt);
				daemonMap.setMap("ERROR_CNT" , 0);
				cyberService.queryForInsert("CODMBASE.InsertDaemonProcessCheck", daemonMap);
			}catch(Exception ex){
				log.debug("상하수도정기분(CYBER_PRTFEE)부과자료연계 로그 등록 오류");
			}				
			/***********************************************************************************/
			
			
		}catch (Exception e){
			e.printStackTrace();
			log.error(e.getMessage());
		}
		
		return sudoCnt;
		
	}
	

}
