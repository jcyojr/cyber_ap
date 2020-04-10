/**
 *  주시스템명 : 부산시청 사이버 지방세청
 *  업  무  명 : 연계
 *  기  능  명 : 버스전용차로위반과태료 부과자료 연계
 *  클래스  ID : Txdm2430_process : 실 처리로직을 작성하는 클래스
 *  변경  이력 :
 * ------------------------------------------------------------------------
 *  작성자      	소속  		    일자            Tag           내용
 * ------------------------------------------------------------------------
 *  송동욱       유채널(주)      2011.05.31         %01%         최초작성
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
		 * 5 분마다 돈다
		 */
		loopTerm = 60 * 5;
	}
	
	/*Context 주입용*/
	public void setApp(ApplicationContext context) {
		this.context = context;
	}
	
	public String getDataSource() {
		return this.dataSource;
	}
	
	/*트랜잭션을 실행하기 위한 함수.*/
	private void mainTransProcess(){
		
		try {
			
			setApp(appContext);
			
			UcContextHolder.setCustomerType(getDataSource());
			
			/*SEQ구하기*/
			MapForm ststMap = new MapForm();
			
			ststMap.setMap("JOB_ID"    , "TXDM2430");
			ststMap.setMap("PARM_NM"   , this.c_slf_org + "||" + this.govId);
			ststMap.setMap("LOG_DESC"  , "== START ==");
			ststMap.setMap("RESULT_CD" , "S");
			
			/*부과전송자료 통계시작 */
			cyberService.queryForUpdate("TXDM2420.INSERT_LOG_JOBSTATE", ststMap);
			
			this.startJob();
			
			ststMap.setMap("LOG_DESC"  , "== END  버스전용차로위반 자료연계 처리건수::" + insert_cnt + ", (연계처리)수정건수::" + update_cnt + ", 삭제건수::" + delete_cnt);
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
		log.info("= govid["+this.govId+"], orgcd["+this.c_slf_org+"]");
		log.info("=====================================================================");
		
		/*트랜잭션을 포함하는 실업무는 여기서 구현한다...*/
		/*실업무를 구현합니다.*/
		insert_cnt = 0;
		update_cnt = 0;
		delete_cnt = 0;
		
		ArrayList<MapForm> alBusFineList = null;
		
		/*연계 버스전용차로위반과태료 부과내역을 가져온다.*/
		alBusFineList =  govService.queryForList("TXDM2430.SELECT_C_BUS_FINE_LIST", null);

		int t_cnt = alBusFineList.size();
		
		log.info("버스전용차로위반 부과건수 = " + t_cnt);
		
		int rec_cnt = 0;
		
		if (t_cnt  >  0)   {
			log.info("!!!버스전용차로위반 연계시작!!!");
			
			for (rec_cnt = 0;  rec_cnt < t_cnt;  rec_cnt++)   {
				
				MapForm mfBusFineList =  alBusFineList.get(rec_cnt);
				
				if (mfBusFineList == null  ||  mfBusFineList.isEmpty() )   {
					log.debug("song continue");
					continue;
				}
				/*초기값 셋팅*/
				mfBusFineList.setMap("PROC_CLS"  , "1");
				mfBusFineList.setMap("DEL_YN"    , "N");
				mfBusFineList.setMap("SGG_TR_TG" , "0");
				
				
				try{
										
					/*==========원장테이블 입력 ===========*/
					if (cyberService.queryForUpdate("TXDM2430.INSERT_BUS_FINE_LEVY", mfBusFineList) > 0) {
						cyberService.queryForUpdate("TXDM2430.INSERT_BUS_FINE_DETAIL_LEVY", mfBusFineList);
						//잠시막음
						//cyberService.queryForUpdate("TXDM2430.UPDATE_BUS_FINE_LEVY_OCRBD", mfBusFineList);
						
						insert_cnt++;
					}
					
				}catch (Exception e){

					/*중복이 발생하거나 제약조건에 어긋나는 경우체크*/
					if (e instanceof DuplicateKeyException){
						
						/*기원장테이블에서 금액 및 납기후 일자를 가져온다.*/
						MapForm mapState = cyberService.queryForMap("TXDM2430.SELECT_GET_AMT_DATE", mfBusFineList);
						
						/* 금액이나 납기일 변경일때만 가상계좌채번 플레그 변경해서 채번 */
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
								log.error("오류데이터 = " + mfBusFineList.getMaps());
								sub_e.printStackTrace();
								throw (RuntimeException) sub_e;
							} else {
								log.error("오류데이터 = " + mfBusFineList.getMaps());
								sub_e.printStackTrace();
							}
						}
						
					} else {
						log.error("오류데이터 = " + mfBusFineList.getMaps());
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
				daemonMap.setMap("DAEMON_NM" , "버스전용(NONTAX_TAB)부과연계");
				daemonMap.setMap("SGG_COD"   , c_slf_org);
				daemonMap.setMap("TOTAL_CNT" , t_cnt);
				daemonMap.setMap("INSERT_CNT", insert_cnt);
				daemonMap.setMap("UPDATE_CNT", update_cnt);
				daemonMap.setMap("DELETE_CNT", 0);
				daemonMap.setMap("ERROR_CNT" , 0);
				cyberService.queryForInsert("CODMBASE.InsertDaemonProcessCheck", daemonMap);
			}catch(Exception ex){
				log.debug("버스전용(NONTAX_TAB)부과연계 로그 등록 오류");
			}				
			/***********************************************************************************/
			
		}else{
			
			/***********************************************************************************/
			try{
				MapForm daemonMap = new MapForm();
				daemonMap.setMap("DAEMON"    , "TXDM2430");
				daemonMap.setMap("DAEMON_NM" , "버스전용(NONTAX_TAB)부과연계");
				daemonMap.setMap("SGG_COD"   , c_slf_org);
				daemonMap.setMap("TOTAL_CNT" , 0);
				daemonMap.setMap("INSERT_CNT", 0);
				daemonMap.setMap("UPDATE_CNT", 0);
				daemonMap.setMap("DELETE_CNT", 0);
				daemonMap.setMap("ERROR_CNT" , 0);
				cyberService.queryForInsert("CODMBASE.InsertDaemonProcessCheck", daemonMap);
			}catch(Exception ex){
				log.debug("버스전용(NONTAX_TAB)부과연계 대상이 없습니다. 등록 오류");
			}				
			/***********************************************************************************/
			
		}
		ArrayList<MapForm> alBusFineDelList = null;
		
		/*버스전용차로위반과태료  고지자료 삭제 연계 */
		alBusFineDelList =  govService.queryForList("TXDM2430.SELECT_C_BUS_FINE_GOJI_LIST", null);
		
		if (alBusFineDelList.size()  >  0) {
			
			for (rec_cnt = 0;  rec_cnt < alBusFineDelList.size();  rec_cnt++)   {
				
				MapForm mfBusFineDelList =  alBusFineDelList.get(rec_cnt);
				
				if (mfBusFineDelList == null  ||  mfBusFineDelList.isEmpty() )   {
					continue;
				}
				
				/*사이버 원장에 삭제 FLAG를 세운다.*/
				mfBusFineDelList.setMap("UP_GB","02");
				cyberService.queryForUpdate("TXDM2430.UPDATE_TX2122_TB", mfBusFineDelList);
				
				/*연계원장에 업데이트*/
				mfBusFineDelList.setMap("NON_UP_GB","02");
				govService.queryForUpdate("TXDM2430.UPDATE_NONTAX_TAB_LINK", mfBusFineDelList);
			}
			
		}
		
		log.info("버스전용차로위반 자료연계 건수::" + alBusFineList.size() + ", 등록건수::" + insert_cnt +", 수정건수::" + update_cnt + ", 삭제건수::" + delete_cnt);
		
	}
	
	
}
