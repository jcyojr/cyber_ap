/**
 *  주시스템명 : 부산시청 사이버 지방세청
 *  업  무  명 : 연계
 *  기  능  명 : 상하수도 전자고지 수기신청 회원 정보
 *  클래스  ID : Txdm4315_process : 실 처리로직을 작성하는 클래스
 *  변경  이력 :
 * ------------------------------------------------------------------------
 *  작성자      	소속  		    일자            Tag           내용
 * ------------------------------------------------------------------------
 *  최유환          유채널      2013.09.11         %01%         최초작성
 */
package com.uc.bs.cyber.daemon.txdm4315;

import java.util.ArrayList;

import org.apache.commons.logging.LogFactory;
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
public class Txdm4315_process extends Codm_BaseProcess implements Codm_interface {

	private MapForm dataMap  = null;
	
	private int insert_cnt = 0, update_cnt = 0;
	
	/**
	 * 
	 */
	public Txdm4315_process() {
	
		super();
		
		log = LogFactory.getLog(this.getClass());
		
		/**
		 * 10 분마다 돈다
		 */
		loopTerm = 600;
	}

	/*Context 주입용*/
	public void setApp(ApplicationContext context) {
		this.context = context;
	}

	/*프로세스 시작*/
	public void runProcess() throws Exception {
			
		/*트랜잭션 시작*/
		mainTransProcess();
	}

	/*Context 주입용*/
	public void setDatasrc(String datasrc) {
	
		this.dataSource = datasrc;
	}


	public void transactionJob() {
		
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
			dataMap.setMap("STATE"        ,  "0");            /*신규신청 분*/
			
			do {
				
				int page = cyberService.getOneFieldInteger("TXSV4315.ssd_enoti_count_page", dataMap);
				
				log.info("[상하수도 전자고지 수기신청 ] PAGE_PER_CNT(" + dataMap.getMap("PAGE_PER_CNT") + ") 당 Page갯수 =" + page);
				
				if(page == 0){ 
					log.info("상수도 전자고지 수기신청 정보가 없습니다.");
					break;
				}

				for(int i = 1 ; i <= page ; i ++) {
					dataMap.setMap("PAGE",  i);    /*처리페이지*/
					this.startJob();               /*멀티 트랜잭션 호출*/
				}
								
				if(cyberService.getOneFieldInteger("TXSV4315.ssd_enoti_count_page", dataMap) == 0) {
					break;
				}
				
			}while(true);
						
		} catch (Exception e) {
			
			e.printStackTrace();
		}			

	}
	
	/*자료 연계*/
	private int txsv4314_JobProcess() {
		
		log.info("=====================================================================");
		log.info("=" + this.getClass().getName()+ " txsv4315_JobProcess() [상하수도 전자고지 수기정보 연계] Start =");
		log.info("=====================================================================");
		
		
		int sudoCnt = 0;
		
		insert_cnt = 0;
		update_cnt = 0; 
		
		int cy_member_check;
		
		try {
			
			/*상하수도 전자고지 수기신청 정보 가져온다.*/
			ArrayList<MapForm> ssdenotiList =  cyberService.queryForList("TXSV4315.getSudoEnotiList", dataMap);
				
			sudoCnt = ssdenotiList.size();

			MapForm ssdEnotiData = new MapForm();
			
			if(sudoCnt  >  0){

				for(int rec_cnt = 0;  rec_cnt < sudoCnt;  rec_cnt++){

					ssdEnotiData =  ssdenotiList.get(rec_cnt);
					
					String enoti_gb = ssdEnotiData.getMap("ENOTI_GB").toString();
					
					if(enoti_gb.equals("")){
						log.info("전자고지 구분이 널 입니다.");
						continue;
					}
					
					try{					
						// 상수도 전자고지 등록
						cyberService.queryForInsert("TXSV4315.insertSudoEnotiMemberInfo", ssdEnotiData);
						
					}catch(Exception e){
						log.error("오류데이터 = " + ssdEnotiData.getMaps());
						log.error(e.getMessage());
						e.printStackTrace();
						throw (RuntimeException) e;
					}
					
					try{					
						// 고객번호 등록
						cyberService.queryForInsert("TXSV4315.insertSudoCustNo", ssdEnotiData);
						
					}catch(Exception e){
						log.error("오류데이터 = " + ssdEnotiData.getMaps());
						log.error(e.getMessage());
						e.printStackTrace();
						throw (RuntimeException) e;
					}
										
					try{
						// 사이버 회원정보 확인
						cy_member_check = cyberService.getOneFieldInteger("TXSV4315.getCyberMemberInfoCheck", ssdEnotiData);
						
					}catch(Exception e){
						log.error("오류데이터 = " + ssdEnotiData.getMaps());
						log.error(e.getMessage());
						e.printStackTrace();
						throw (RuntimeException) e;
					}
					
					
					// 사이버 회원일 경우
					if(cy_member_check > 0){
						
						
						// 회원정보 수정						
						try{
							if(enoti_gb.equals("1")){
								// 지방세 변경
								cyberService.queryForUpdate("TXSV4315.updateCyberMemberEnotiInfo", ssdEnotiData);
							}else if(enoti_gb.equals("2")){
								// 상수도 변경
								cyberService.queryForUpdate("TXSV4315.updateCyberMemberSSDInfo", ssdEnotiData);
							}else{
								// 지방세 상수도 변경
								cyberService.queryForUpdate("TXSV4315.updateCyberMemberEnotiSsdEnotiInfo", ssdEnotiData);							
							}
							
						}catch(Exception e){
							log.error("오류데이터 = " + ssdEnotiData.getMaps());
							log.error(e.getMessage());
							e.printStackTrace();
							throw (RuntimeException) e;
						}
						update_cnt += 1;
						
					}
					// 사이버 회원이 아닌 경우 
					else{
						
						// 사이버 준회원으로 가입
						try{
							
							cyberService.queryForInsert("TXSV4315.insertCyberMemberInfo", ssdEnotiData);
														
						}catch(Exception e){
							log.error("오류데이터 = " + ssdEnotiData.getMaps());
							log.error(e.getMessage());
							e.printStackTrace();
							throw (RuntimeException) e;
						}
						insert_cnt += 1;
						
						
					}
					
					// 연계테이블 상태 변경
					try{
						cyberService.queryForUpdate("TXSV4315.updateCyberMemberState", ssdEnotiData);
					}catch(Exception e){
						log.error("오류데이터 = " + ssdEnotiData.getMaps());
						log.error(e.getMessage());
						e.printStackTrace();
						throw (RuntimeException) e;
					}
					
				}
				
			}else{
				log.info("상수도 전자고지 신청 회원이 없습니다.");
			}
		
			//log.info("상하수도 정기분 고지자료 연계시간 : " + CbUtil.formatTime(elapseTime2 - elapseTime1));
			log.info("총연계[" + sudoCnt + "]" + "INSERT 건수" + "["+insert_cnt+"]" +"UPDATE 건수" + "["+update_cnt+"]" );
			
			
		}catch (Exception e){
			e.printStackTrace();
			log.error(e.getMessage());
		}
		
		return sudoCnt;
		
	}
	

}
