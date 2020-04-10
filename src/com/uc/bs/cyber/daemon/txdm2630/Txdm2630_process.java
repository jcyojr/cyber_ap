/**
 *  주시스템명 : 부산시청 사이버 지방세청
 *  업  무  명 : 연계
 *  기  능  명 : 바로결제 부가자료 수납처리
 *  클래스  ID : Txdm2630_process : 실 처리로직을 작성하는 클래스
 *  변경  이력 :
 * ------------------------------------------------------------------------
 *  작성자      	소속  		    일자            Tag           내용
 * ------------------------------------------------------------------------
 *  최유환       유채널(주)      2013.10.01         %01%         최초작성
 */
package com.uc.bs.cyber.daemon.txdm2630;

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
public class Txdm2630_process extends Codm_BaseProcess implements Codm_interface {

private MapForm dataMap  = null;
	
	private int insert_cnt = 0, update_cnt = 0;
	
	/**
	 * 
	 */
	public Txdm2630_process() {
	
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
	public void runProcess() throws Exception {
			
		/*트랜잭션 시작*/
		mainTransProcess();
	}

	/*Context 주입용*/
	public void setDatasrc(String datasrc) {
	
		this.dataSource = datasrc;
	}


	public void transactionJob() {
		
		txdm2630_JobProcess();
	}

	/*트랜잭션을 실행하기 위한 함수.*/
	private void mainTransProcess(){
		
		
		try {
			
			setContext(appContext);
			setApp(appContext);

			UcContextHolder.setCustomerType(this.dataSource);
			
			dataMap = new MapForm();  /*초기화*/
			dataMap.setMap("PAGE_PER_CNT" , 1000);           	/*페이지당 갯수*/
			dataMap.setMap("STATE"        , "1");             	/*수납대기 분    */
			
			
				
			int page = cyberService.getOneFieldInteger("TXDM2630.select_count_page", dataMap);
				
			log.info("바로결제 부과자료 수납 대기건 PAGE_PER_CNT(" + dataMap.getMap("PAGE_PER_CNT") + ") 당 Page갯수 =" + page);
				
			if(page == 0){
				log.info("바로결제 부과자료 수납 대기 건수가 없습니다.");			
			}else{
				
				for(int i = 1 ; i <= page ; i ++) {

					dataMap.setMap("PAGE",  i);    /*처리페이지*/
					this.startJob();               /*멀티 트랜잭션 호출*/
				}
				
			}

						
		} catch (Exception e) {

			e.printStackTrace();
		}			
		
		
	}
	
	/*자료 연계*/
	private int txdm2630_JobProcess() {
		
		log.info("=====================================================================");
		log.info("=" + this.getClass().getName()+ " txdm2630_JobProcess() [바로결제 부과자료 수납처리] Start =");
		log.info("=====================================================================");

		
		//long elapseTime1 = 0;
		//long elapseTime2 = 0;
		
		int LevyCnt = 0;

		MapForm sunapData = new MapForm();
			
		try {
						
			/*지방세 정기분외 부과내역을 가져온다.*/
			ArrayList<MapForm> dataList =  cyberService.queryForList("TXDM2630.selectBaroSunapList", dataMap);
						
			LevyCnt = dataList.size();

			if (LevyCnt  >  0)   {
				
				log.info("전자신고 후 수납 대기건 : " + LevyCnt + " 건 있습니다.");

				//elapseTime1 = System.currentTimeMillis();
				
				for (int i = 0;  i < LevyCnt;  i++)   {
					
					sunapData =  dataList.get(i);
					
					log.info("sunapData :: " + sunapData);
	
					int bugaDataCheck = cyberService.getOneFieldInteger("TXDM2630.getBugaDataCheck", sunapData);
					
					log.info("bugaDataCheck  :: " + bugaDataCheck);
					
					
					if(bugaDataCheck > 0){
						  
						// OCR_BD 조회
						String OCR_BD = null;
						try{						
							OCR_BD = cyberService.getOneFieldString("TXDM2630.getBugaOcrBd",sunapData);
							log.info("OCR_BD 조회 :: " + OCR_BD);
						}catch(Exception e){
							e.printStackTrace();
							log.info("OCR_BD 오류");
							throw (RuntimeException) e;
						}

						sunapData.setMap("OCR_BD", OCR_BD);												
						
						//PAY_CNT 조회
						int pay_cnt = 0;
						try{
							pay_cnt = cyberService.getOneFieldInteger("TXDM2630.getPayCnt", sunapData);
							log.info("PAY_CNT 조회 :: " + pay_cnt);
						}catch(Exception e){
							e.printStackTrace();
							log.info("PAY_CNT 조회 오류");
							throw (RuntimeException) e;
						}
						
						sunapData.setMap("PAY_CNT", pay_cnt);
						
						try{
							
							cyberService.queryForInsert("TXDM2630.insertSunapData", sunapData);
							log.info("수납테이블 등록");
						}catch(Exception e){
							e.printStackTrace();
							log.info("수납테이블 등록 오류 :: " + sunapData);
							throw (RuntimeException) e;
						}
						
						try{
							
							cyberService.queryForUpdate("TXDM2630.updateBugaData", sunapData);
							log.info("부과데이터 수납 변경 완료");
						}catch(Exception e){
							e.printStackTrace();
							log.info("부과데이터 수납 변경 변경 오류 :: " + sunapData);
							throw (RuntimeException) e;
						}
						
						
						try{
							cyberService.queryForUpdate("TXDM2630.update_complete", sunapData);
							log.info("전문연계테이블 수납 완료 변경");
						}catch(Exception e){
							e.printStackTrace();
							log.info("전문연계테이블 완료 변경 오류 :: " + sunapData);
							throw (RuntimeException) e;
						}
						
						log.info("===== 바로결제 부과자료 수납처리 완료 ===== ");
					
					}else{
						
						log.info("부과자료가 생성되지 않았습니다.");
						continue;
					}

				}
				
				//elapseTime2 = System.currentTimeMillis();
				
				//log.info("수납 처리 시간 : " + CbUtil.formatTime(elapseTime2 - elapseTime1));
				
				log.info("==================== 바로결제 수납처리대몬 처리 완료 ======================");
				
			}else{
				log.info("수납 대기 건수가 없습니다.");
			}
			
		}catch (Exception e){
			e.printStackTrace();
		}
		
		return LevyCnt;
		
	}
	
}

