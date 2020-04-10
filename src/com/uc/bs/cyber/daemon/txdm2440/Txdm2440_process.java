/**
 *  주시스템명 : 부산시청 사이버 지방세청
 *  업  무  명 : 연계
 *  기  능  명 : 주거지전용주차료 부과자료 연계(시도) 업무
 *  클래스  ID : Txdm2440_process : 실 처리로직을 작성하는 클래스
 *  변경  이력 :
 * ------------------------------------------------------------------------
 *  작성자      	소속  		    일자            Tag           내용
 * ------------------------------------------------------------------------
 *  송동욱       유채널(주)      2011.09.20         %01%         최초작성
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
		 * 10 분마다 돈다
		 */
		loopTerm = 60 * 10;
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
			
			int page = 0;
			
			setApp(appContext);
			
			UcContextHolder.setCustomerType(getDataSource());
			
			dataMap = new MapForm();  /*초기화*/
			dataMap.setMap("PAGE_PER_CNT",  500);           /*페이지당 갯수*/
			
			if(jobId == 0) {
				
					page = govService.getOneFieldInteger("TXDM2440.SELECT_C_JUGEOJI_FINE_CNT", dataMap);

					
					log.info("[주거지전용주차료] PAGE_PER_CNT(" + dataMap.getMap("PAGE_PER_CNT") + ") 당 Page갯수 =" + page);
					
					dataMap.setMap("GUBUN",  "1");
					
					if(page > 0){
					
						for(int i = 1 ; i <= page ; i ++) {
							
							MapForm ststMap = new MapForm();
							
							ststMap.setMap("JOB_ID"    , "TXDM2440");
							ststMap.setMap("PARM_NM"   , this.c_slf_org + "||" + this.govId);
							ststMap.setMap("LOG_DESC"  , "== START ==");
							ststMap.setMap("RESULT_CD" , "S");
							/*부과전송자료 통계시작 */
							cyberService.queryForUpdate("TXDM2420.INSERT_LOG_JOBSTATE", ststMap);
							
							dataMap.setMap("PAGE",  i);    /*처리페이지*/
							
							this.startJob();
							
							ststMap.setMap("LOG_DESC"  , "== END  주거지전용주차료 자료연계 처리건수::" + insert_cnt + ", (연계처리)수정건수::" + update_cnt + ", 삭제건수::" + delete_cnt);
							ststMap.setMap("RESULT_CD" , "E");
							
							/*부과전송자료 통계완료 */
							cyberService.queryForUpdate("TXDM2420.INSERT_LOG_JOBSTATE", ststMap);
						}
					
					}else{
						
						/***********************************************************************************/
						try{
							MapForm daemonMap = new MapForm();
							daemonMap.setMap("DAEMON"    , "TXDM2440");
							daemonMap.setMap("DAEMON_NM" , "주거지(NONTAX_TAB)부과연계");
							daemonMap.setMap("SGG_COD"   , c_slf_org);
							daemonMap.setMap("TOTAL_CNT" , 0);
							daemonMap.setMap("INSERT_CNT", 0);
							daemonMap.setMap("UPDATE_CNT", 0);
							daemonMap.setMap("DELETE_CNT", 0);
							daemonMap.setMap("ERROR_CNT" , 0);
							cyberService.queryForInsert("CODMBASE.InsertDaemonProcessCheck", daemonMap);
						}catch(Exception ex){
							log.debug("주거지(NONTAX_TAB)부과연계 대상이 없습니다. 등록 오류");
						}				
						/***********************************************************************************/
						
					}
					
				
			} else if(jobId == 1) {
				
					page = govService.getOneFieldInteger("TXDM2440.SELECT_C_JUGEOJI_FINE_DEL_CNT", dataMap);
					
					log.info("[주거지전용주차료 삭제자료] PAGE_PER_CNT(" + dataMap.getMap("PAGE_PER_CNT") + ") 당 Page갯수 =" + page);
					
					dataMap.setMap("GUBUN",  "2");
					
					if(page > 0) {
					
						for(int i = 1 ; i <= page ; i ++) {
												
							dataMap.setMap("PAGE",  i);    /*처리페이지*/					
							this.startJob();
						}
					
					}
				
			} else if(jobId == 2) {
					
					page = govService.getOneFieldInteger("TXDM2440.SELECT_DEL_NONSOIN_CNT", dataMap);
					
					log.info("[주거지전용주차료 소인삭제자료] PAGE_PER_CNT(" + dataMap.getMap("PAGE_PER_CNT") + ") 당 Page갯수 =" + page);
					
					dataMap.setMap("GUBUN",  "3");
					
					if(page > 0) {
					
						for(int i = 1 ; i <= page ; i ++) {
												
							dataMap.setMap("PAGE",  i);    /*처리페이지*/					
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
	
		if(dataMap.getMap("GUBUN").equals("1")) {
			txdm2440_JobProcess1();
		} else if(dataMap.getMap("GUBUN").equals("2")) {
			txdm2440_JobProcess2();
		} else if(dataMap.getMap("GUBUN").equals("3")) {
			txdm2440_JobProcess3();
		}

	}
	
	/**
	 * 부가자료 연계
	 * @return
	 */
	private int txdm2440_JobProcess1() {
		
		log.info("=====================================================================");
		log.info("=" + this.getClass().getName()+ " txdm2440_JobProcess1()[주거지전용주차료 부과자료연계] Start =");
		log.info("=====================================================================");
		
		/*트랜잭션을 포함하는 실업무는 여기서 구현한다...*/
		/*실업무를 구현합니다.*/
		insert_cnt = 0;
		update_cnt = 0;
		delete_cnt = 0;
		
		long elapseTime1 = 0;
		long elapseTime2 = 0;
		
		int t_cnt = 0;
		ArrayList<MapForm> alDwellParkList = null;
		
		try{
			elapseTime1 = System.currentTimeMillis();
			
			/*연계 주거지전용주차료 부과내역을 가져온다.*/
			alDwellParkList =  govService.queryForList("TXDM2440.SELECT_C_JUGEOJI_FINE_LIST", dataMap);
			
			t_cnt = alDwellParkList.size();
			
			log.info("주거지전용주차료 부과건수 = " + t_cnt);
			
			int rec_cnt = 0;
			
			if (t_cnt  >  0)   {
				
				for (rec_cnt = 0; rec_cnt < t_cnt; rec_cnt++)   {
					
					MapForm mfDwellParkList =  alDwellParkList.get(rec_cnt);
					
					if (mfDwellParkList == null  ||  mfDwellParkList.isEmpty() )   {
						continue;
					}
					/*초기값 셋팅*/
					mfDwellParkList.setMap("TAX_CNT"   ,  0 );
					mfDwellParkList.setMap("PROC_CLS"  , "1");
					mfDwellParkList.setMap("DEL_YN"    , "N");
					mfDwellParkList.setMap("SGG_TR_TG" , "0");
					
					try{
						
						mfDwellParkList.setMap("CUD_OPT" , "1");
						
						/*==========원장테이블 입력 ===========*/
						if (cyberService.queryForUpdate("TXDM2440.INSERT_BUS_FINE_LEVY", mfDwellParkList) > 0) {
							cyberService.queryForUpdate("TXDM2440.INSERT_BUS_FINE_DETAIL_LEVY", mfDwellParkList);
							
							insert_cnt++;
						}
						
					}catch (Exception e){

						/*중복이 발생하거나 제약조건에 어긋나는 경우체크*/
						if (e instanceof DuplicateKeyException){
							
							mfDwellParkList.setMap("CUD_OPT" , "2");
							
							/*기원장테이블에서 금액 및 납기후 일자를 가져온다.*/
							MapForm mapState = cyberService.queryForMap("TXDM2440.SELECT_GET_AMT_DATE", mfDwellParkList);
							
							/* 금액이나 납기일 변경일때만 가상계좌채번 플레그 변경해서 채번 */
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
									log.error("오류데이터 = " + mfDwellParkList.getMaps());
									sub_e.printStackTrace();
									throw (RuntimeException) sub_e;
								} else {
									log.error("오류데이터 = " + mfDwellParkList.getMaps());
									sub_e.printStackTrace();
								}
							}
							
						} else {
							log.error("오류데이터 = " + mfDwellParkList.getMaps());
							e.printStackTrace();
							throw (RuntimeException) e;
						}
						
					}
					
					govService.queryForUpdate("TXDM2440.UPDATE_NONTAX_TAB_LINK", mfDwellParkList);
				}
				
			}
			elapseTime2 = System.currentTimeMillis();
			
			log.info("주거지전용주차료 자료연계 건수::(" + t_cnt + "), 등록건수::" + insert_cnt +", 수정건수::" + update_cnt + " ::처리시간 " + CbUtil.formatTime(elapseTime2 - elapseTime1));
			
			/***********************************************************************************/
			try{
				MapForm daemonMap = new MapForm();
				daemonMap.setMap("DAEMON"    , "TXDM2440");
				daemonMap.setMap("DAEMON_NM" , "주거지(NONTAX_TAB)부과연계");
				daemonMap.setMap("SGG_COD"   , c_slf_org);
				daemonMap.setMap("TOTAL_CNT" , t_cnt);
				daemonMap.setMap("INSERT_CNT", insert_cnt);
				daemonMap.setMap("UPDATE_CNT", update_cnt);
				daemonMap.setMap("DELETE_CNT", 0);
				daemonMap.setMap("ERROR_CNT" , 0);
				cyberService.queryForInsert("CODMBASE.InsertDaemonProcessCheck", daemonMap);
			}catch(Exception ex){
				log.debug("주거지(NONTAX_TAB)부과연계 로그 등록 오류");
			}				
			/***********************************************************************************/
			
		}catch (Exception e){
			throw (RuntimeException) e;
		}
		
		return t_cnt;
		
	}
	
	/**
	 * 부가삭제자료연계
	 * @return
	 */
    private int txdm2440_JobProcess2() {
    	
		log.info("=====================================================================");
		log.info("=" + this.getClass().getName()+ " txdm2440_JobProcess2()[주거지전용주차료 삭제자료연계] Start =");
		log.info("=====================================================================");
    	
		long elapseTime1 = 0;
		long elapseTime2 = 0;
		
		ArrayList<MapForm> alDwellPrkDelList = null;

		int d_cnt = 0, rec_cnt = 0;
		
		try{
			
			elapseTime1 = System.currentTimeMillis();
			
			/*주거지전용주차료 고지자료 삭제 연계 */
			alDwellPrkDelList =  govService.queryForList("TXDM2440.SELECT_C_JUGEOJI_FINE_DEL_LIST", dataMap);
			
			if (alDwellPrkDelList.size()  >  0) {

				for (rec_cnt = 0;  rec_cnt < alDwellPrkDelList.size();  rec_cnt++)   {
					
					MapForm mfDwellPrkDelList =  alDwellPrkDelList.get(rec_cnt);
					
					if (mfDwellPrkDelList == null  ||  mfDwellPrkDelList.isEmpty() )   {
						continue;
					}
					
					try{

						/*사이버 원장에 삭제 FLAG를 세운다.*/
						mfDwellPrkDelList.setMap("UP_GB","02");
						cyberService.queryForUpdate("TXDM2440.UPDATE_TX2122_TB", mfDwellPrkDelList);
						
						/*연계원장에 업데이트*/
						govService.queryForUpdate("TXDM2440.UPDATE_NONTAX_TAB_LINK", mfDwellPrkDelList);
						
						d_cnt ++;
						
					}catch (Exception sub_e){
						
						if (sub_e instanceof DuplicateKeyException || sub_e instanceof DataIntegrityViolationException){
							log.error("오류데이터 = " + mfDwellPrkDelList.getMaps());
							sub_e.printStackTrace();
							throw (RuntimeException) sub_e;
						} else {
							log.error("오류데이터 = " + mfDwellPrkDelList.getMaps());
							sub_e.printStackTrace();
						}
						
					}

				}
				
			}

			elapseTime2 = System.currentTimeMillis();
			
			log.info("주거지전용주차료 삭제 자료연계 건수::" + alDwellPrkDelList.size() + ", 삭제건수::" + d_cnt + " ::처리시간 " + CbUtil.formatTime(elapseTime2 - elapseTime1));
			
		}catch (Exception e){
			throw (RuntimeException) e;
		}

		return alDwellPrkDelList.size();
	}

    /**
     * 소인처리자료 연계
     * @return
     */
    private int txdm2440_JobProcess3() {
    	
		log.info("=====================================================================");
		log.info("=" + this.getClass().getName()+ " txdm2440_JobProcess3()[주거지전용주차료 소인삭제자료연계] Start =");
		log.info("=====================================================================");
    	
		long elapseTime1 = 0;
		long elapseTime2 = 0;
		
		ArrayList<MapForm> alDwellPrkSoinList = null;

		int s_cnt = 0, rec_cnt = 0;
		
		try{
			
			elapseTime1 = System.currentTimeMillis();
			
			/*주거지전용주차료 소인자료 사이버원장삭제 연계 */
			alDwellPrkSoinList =  govService.queryForList("TXDM2440.SELECT_DEL_NONSOIN_LIST", dataMap);
			
			if (alDwellPrkSoinList.size()  >  0) {
				
				for (rec_cnt = 0;  rec_cnt < alDwellPrkSoinList.size();  rec_cnt++)   {
					
					MapForm mfDwellPrkSoinList =  alDwellPrkSoinList.get(rec_cnt);
					
					if (mfDwellPrkSoinList == null  ||  mfDwellPrkSoinList.isEmpty() )   {
						continue;
					}
					
					/*사이버 원장에 삭제 FLAG를 세운다.*/
					mfDwellPrkSoinList.setMap("UP_GB"   , "02");
					
					mfDwellPrkSoinList.setMap("SGG_COD" , mfDwellPrkSoinList.getMap("NNS_SGCD"));
					mfDwellPrkSoinList.setMap("ACCT_COD", mfDwellPrkSoinList.getMap("NNS_ACKU"));
					mfDwellPrkSoinList.setMap("TAX_ITEM", mfDwellPrkSoinList.getMap("NNS_SMCD"));
					mfDwellPrkSoinList.setMap("TAX_YY"  , mfDwellPrkSoinList.getMap("NNS_BYYY"));
					mfDwellPrkSoinList.setMap("TAX_MM"  , mfDwellPrkSoinList.getMap("NNS_BNGI"));
					mfDwellPrkSoinList.setMap("TAX_DIV" , mfDwellPrkSoinList.getMap("NNS_GIBN"));
					mfDwellPrkSoinList.setMap("HACD"    , mfDwellPrkSoinList.getMap("NNS_HACD"));

					try{

						/*사이버 원장 삭제 FLAG를 세운다. */
						cyberService.queryForUpdate("TXDM2440.UPDATE_TX2122_TB", mfDwellPrkSoinList);
						
						/*연계원장에 업데이트*/
						govService.queryForUpdate("TXDM2440.UPDATE_NNONSOIN_TAB_LINK", mfDwellPrkSoinList);
						
						s_cnt++;
						
					}catch (Exception sub_e){
						
						if (sub_e instanceof DuplicateKeyException || sub_e instanceof DataIntegrityViolationException){
							log.error("오류데이터 = " + mfDwellPrkSoinList.getMaps());
							sub_e.printStackTrace();
							throw (RuntimeException) sub_e;
						} else {
							log.error("오류데이터 = " + mfDwellPrkSoinList.getMaps());
							sub_e.printStackTrace();
						}
						
					}
					
				}
				
			}
			
			elapseTime2 = System.currentTimeMillis();

			log.info("주거지전용주차료 소인삭제자료연계 건수::" + alDwellPrkSoinList.size() + ", 삭제건수::" + s_cnt + " ::처리시간 " + CbUtil.formatTime(elapseTime2 - elapseTime1));
			
		}catch (Exception e){
			throw (RuntimeException) e;
		}

		return alDwellPrkSoinList.size();
    }
    

}
