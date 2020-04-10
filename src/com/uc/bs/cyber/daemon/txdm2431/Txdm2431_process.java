/**
 *  주시스템명 : 부산시청 사이버 지방세청
 *  업  무  명 : 연계
 *  기  능  명 : 버스전용차로위반과태료 가상계좌채번 연계
 *               주거지는 가상계좌 채번 안함.
 *  클래스  ID : Txdm2431_process : 실 처리로직을 작성하는 클래스
 *  변경  이력 : 
 * ------------------------------------------------------------------------
 *  작성자      	소속  		    일자            Tag           내용
 * ------------------------------------------------------------------------
 *  송동욱       유채널(주)      2011.05.31         %01%         최초작성
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
		 * 10 분마다 돈다
		 */
		loopTerm = 60 * 10;
	}
	
	/*Context 주입용*/
	public void setApp(ApplicationContext context) {
		this.context = context;
	}
	
	/*트랜잭션을 실행하기 위한 함수.*/
	private void mainTransProcess(){
		
		try {
				
			setContext(appContext);
			setApp(appContext);

			UcContextHolder.setCustomerType(this.dataSource);
			
			/*SEQ구하기*/
			MapForm ststMap = cyberService.queryForMap("TXDM2420.GET_CO5102_MAXSEQ", null);
					
			ststMap.setMap("JOB_ID"    , "TXDM2431");
			ststMap.setMap("PARM_NM"   , this.c_slf_org + "||" + this.govId);
			ststMap.setMap("LOG_DESC"  , "== START ==");
			ststMap.setMap("RESULT_CD" , "S");
			
			/*부과전송자료 통계시작 */
			cyberService.queryForUpdate("TXDM2420.INSERT_LOG_JOBSTATE", ststMap);
			
			this.startJob();
						
			ststMap.setMap("LOG_DESC"  , "== END  가상계좌채번연계 처리건수::" + loop_cnt + ", (연계처리)수정건수::" + update_cnt + ", 오류건수::" + error_cnt);
			ststMap.setMap("RESULT_CD" , "E");
			
			/*부과전송자료 통계완료 */
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
		
		/*트랜잭션 업무 구현*/
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
		
		/*트랜잭션을 포함하는 실업무는 여기서 구현한다...*/
		/*실업무를 구현합니다.*/
		loop_cnt = 0;
		update_cnt = 0;
		error_cnt = 0;

		/*사이버의 버스전용차로위반과태료 부과내역을 가져온다.*/
		ArrayList<MapForm> alBusFineList =  cyberService.queryForList("TXDM2431.SELECT_VIRUAL_BUS_FINE_LIST", null);
		
		if (alBusFineList.size()  >  0)   {
			
			for (int rec_cnt = 0;  rec_cnt < alBusFineList.size();  rec_cnt++)   {
				
				MapForm mfBusFineList =  alBusFineList.get(rec_cnt);
				
				if (mfBusFineList == null  ||  mfBusFineList.isEmpty() )   {
					continue;
				}
				
				loop_cnt++;
				
				/*==========연계테이블 업데이트===========*/
				/*flag 처리*/

				try {
					
					if (govService.queryForUpdate("TXDM2431.UPDATE_NONTAX_TAB", mfBusFineList) > 0) {
						
						mfBusFineList.setMap("SGG_TR_TG", "1");       /*성공시*/
						if (cyberService.queryForUpdate("TXDM2431.UPDATE_TX2122_TB", mfBusFineList) > 0) {
							update_cnt++;
						}
						
					} else {
						mfBusFineList.setMap("SGG_TR_TG", "9");   /*실패시*/
						/*연계정보에 업데이트가 되지 않으면 사이버DB에 기록을 남긴다.*/
						cyberService.queryForUpdate("TXDM2431.UPDATE_TX2122_TB", mfBusFineList);
						
						error_cnt++;
					}
					
				}catch (Exception e){
					
					log.error("오류데이터 = " + mfBusFineList.getMaps());
					e.printStackTrace();
					throw (RuntimeException) e;
					
				}

			}
			
			/***********************************************************************************/
			try{
				MapForm daemonMap = new MapForm();
				daemonMap.setMap("DAEMON"    , "TXDM2431");
				daemonMap.setMap("DAEMON_NM" , "버스전용(NONTAX_TAB)가상계좌연계");
				daemonMap.setMap("SGG_COD"   , c_slf_org);
				daemonMap.setMap("TOTAL_CNT" , loop_cnt);
				daemonMap.setMap("INSERT_CNT", 0);
				daemonMap.setMap("UPDATE_CNT", update_cnt);
				daemonMap.setMap("DELETE_CNT", 0);
				daemonMap.setMap("ERROR_CNT" , error_cnt);
				cyberService.queryForInsert("CODMBASE.InsertDaemonProcessCheck", daemonMap);
			}catch(Exception ex){
				log.debug("버스전용(NONTAX_TAB)가상계좌연계 로그 등록 오류");
			}				
			/***********************************************************************************/
			
			
		}else{
			
			/***********************************************************************************/
			try{
				MapForm daemonMap = new MapForm();
				daemonMap.setMap("DAEMON"    , "TXDM2431");
				daemonMap.setMap("DAEMON_NM" , "버스전용(NONTAX_TAB)가상계좌연계");
				daemonMap.setMap("SGG_COD"   , c_slf_org);
				daemonMap.setMap("TOTAL_CNT" , 0);
				daemonMap.setMap("INSERT_CNT", 0);
				daemonMap.setMap("UPDATE_CNT", 0);
				daemonMap.setMap("DELETE_CNT", 0);
				daemonMap.setMap("ERROR_CNT" , 0);
				cyberService.queryForInsert("CODMBASE.InsertDaemonProcessCheck", daemonMap);
			}catch(Exception ex){
				log.debug("버스전용(NONTAX_TAB)가상계좌연계 대상이 없습니다. 등록 오류");
			}				
			/***********************************************************************************/
		}
		
		log.info("버스전용차로위반과태료 가상계좌채번연계 처리건수::" + loop_cnt + ", (연계처리)수정건수::" + update_cnt + ", 오류건수::" + error_cnt);
		

	}
	

}
