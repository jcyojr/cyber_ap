/**
 *  주시스템명 : 부산시청 사이버 지방세청
 *  업  무  명 : 연계
 *  기  능  명 : 상하수도 실시간 수납자료 전송.
 *  클래스  ID : Txdm4316_process : 실 처리로직을 작성하는 클래스
 *  변경  이력 :
 * ------------------------------------------------------------------------
 *  작성자      	소속  		    일자            Tag           내용
 * ------------------------------------------------------------------------
 *  YHCHOI      유채널        2015.04.18        %01%         최초작성
 */
package com.uc.bs.cyber.daemon.txdm4316;

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
public class Txdm4316_process extends Codm_BaseProcess implements Codm_interface {

	private MapForm dataMap  = null;
	
	/**
	 * 
	 */
	public Txdm4316_process() {

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
		
		/*트랜잭션 시작*/
		mainTransProcess();
	}

	/*Context 주입용*/
	@Override
	public void setDatasrc(String datasrc) {
		this.dataSource = datasrc;
	}

	@Override
	public void transactionJob() {
		txsv4316_JobProcess();
	}


	/*트랜잭션을 실행하기 위한 함수.*/
	private void mainTransProcess(){
		
		
		try {
			
			setContext(appContext);
			setApp(appContext);

			UcContextHolder.setCustomerType(this.dataSource);
			
			dataMap = new MapForm();  /*초기화*/
											
			int page = cyberService.getOneFieldInteger("TXDM4316.getSunapDataListCount", dataMap);
				
			log.info("[상하수도 수납 자료 갯수 = " + page + " ]");
					

			if(page > 0){
				this.startJob(); 
			}else{
				
				/***********************************************************************************/
				try{
					MapForm daemonMap = new MapForm();
					daemonMap.setMap("DAEMON"    , "TXDM4316");
					daemonMap.setMap("DAEMON_NM" , "상하수도(VIR_RCVFEE_BS)수납자료연계");
					daemonMap.setMap("SGG_COD"   , c_slf_org);
					daemonMap.setMap("TOTAL_CNT" , 0);
					daemonMap.setMap("INSERT_CNT", 0);
					daemonMap.setMap("UPDATE_CNT", 0);
					daemonMap.setMap("DELETE_CNT", 0);
					daemonMap.setMap("ERROR_CNT" , 0);
					cyberService.queryForInsert("CODMBASE.InsertDaemonProcessCheck", daemonMap);
				}catch(Exception ex){
					log.debug("상하수도(VIR_RCVFEE_BS)수납자료연계 대상이 없습니다. 등록 오류");
				}				
				/***********************************************************************************/
				
				log.info("[상하수도 수납 자료가 없습니다.");
			}
				
						
		} catch (Exception e) {
			e.printStackTrace();
		}			

	}
	
	
	/*자료 연계*/
	private int txsv4316_JobProcess() {
		
		log.info("=====================================================================");
		log.info("=" + this.getClass().getName()+ " txsv4316_JobProcess() [상하수도 실시간 수납 자료 연계] Start =");
		log.info("=====================================================================");
				
		int insert_cnt = 0;
		int dup_cnt = 0; 
		int err_cnt = 0;
		
		try {
				
			/*상하수도 수납자료 목록을 가져온다.*/
			ArrayList<MapForm> sunapList =  cyberService.queryForList("TXDM4316.getSudoSunapDataList", dataMap);
			
			if(sunapList.size() > 0){
				
				MapForm sunapData = null;
				
				for(int i = 0; i < sunapList.size(); i++){
					
					sunapData = new MapForm();
					
					sunapData = sunapList.get(i);
					
					sunapData.setMap("SD_TRTG", "1");
					
					try{
						//상수도 수납자료 등록
						govService.queryForInsert("TXDM4316.insertSudoSunapData", sunapData);
						
						insert_cnt++;
						
						
						
					}catch(Exception ex){
						
						if(ex instanceof DuplicateKeyException){
							
							log.debug("[상하수도 수납 자료 중복 발생 - 전송완료로 변경 " + sunapData);	
							
							dup_cnt++;
							
						}else{
													
							ex.printStackTrace();
							log.info("[상하수도 수납 자료 전송 중 오류발생 - 오류로 변경 " + sunapData);
							sunapData.setMap("SD_TRTG", "9");
							err_cnt++;
						}			
						
					}
					
					try{
						
						cyberService.queryForUpdate("TXDM4316.updateCyberTrtgData", sunapData);
					}catch(Exception exc){
						exc.printStackTrace();
						log.info("[상하수도 수납 전송완료 오류발생 !!!! " + sunapData);
					}
					
					
				}
				
			}else{
				log.info("[상하수도 수납 자료가 없습니다.");
			}

			log.info("상수도 수납자료 전체건수 [ " + sunapList.size() + " ] 중 등록완료 [ " + insert_cnt + " ] 건 실패 [ " + err_cnt + " ] 건 중복 [ " + dup_cnt + " ] 건 입니다.");
			
			/***********************************************************************************/
			try{
				MapForm daemonMap = new MapForm();
				daemonMap.setMap("DAEMON"    , "TXDM4316");
				daemonMap.setMap("DAEMON_NM" , "상하수도(VIR_RCVFEE_BS)수납자료연계");
				daemonMap.setMap("SGG_COD"   , c_slf_org);
				daemonMap.setMap("TOTAL_CNT" , sunapList.size());
				daemonMap.setMap("INSERT_CNT", insert_cnt);
				daemonMap.setMap("UPDATE_CNT", dup_cnt);
				daemonMap.setMap("DELETE_CNT", 0);
				daemonMap.setMap("ERROR_CNT" , err_cnt);
				cyberService.queryForInsert("CODMBASE.InsertDaemonProcessCheck", daemonMap);
			}catch(Exception ex){
				log.debug("상하수도(VIR_RCVFEE_BS)수납자료연계 대상이 없습니다. 등록 오류");
			}				
			/***********************************************************************************/
			
		}catch (Exception e){
			e.printStackTrace();
			log.error(e.getMessage());
		}
		
		return 0;
		
	}
	

}
