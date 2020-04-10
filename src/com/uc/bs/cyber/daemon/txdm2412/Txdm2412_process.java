/**
 *  주시스템명 : 부산시청 사이버 지방세청
 *  업  무  명 : 연계
 *  기  능  명 : 표준세외수입 수납자료전송
 *  클래스  ID : Txdm2412_process : 실 처리로직을 작성하는 클래스
 *  변경  이력 :
 * ------------------------------------------------------------------------
 *  작성자      	소속  		    일자            Tag           내용
 * ------------------------------------------------------------------------
 *  표승한       다산시스템      2013.12.13         %01%         최초작성
 *  표승한       다산시스템      2015.05.29         %01%         변경
 */
package com.uc.bs.cyber.daemon.txdm2412;

import java.util.ArrayList;

import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;

import com.uc.bs.cyber.CbUtil;
import com.uc.bs.cyber.daemon.Codm_BaseProcess;
import com.uc.bs.cyber.daemon.Codm_interface;
import com.uc.core.MapForm;
import com.uc.core.spring.service.UcContextHolder;

public class Txdm2412_process extends Codm_BaseProcess implements Codm_interface {

	private MapForm dataMap            = null;
	private int insert_cnt = 0, update_cnt = 0, delete_cnt = 0, remote_up_cnt = 0, error_cnt = 0;
    private int p_del_cnt = 0;
	
	/*트랜잭션을 위하여 */
	MapForm gblNidLevyRows    = null;
	
	/**
	 * 
	 */
	public Txdm2412_process() {
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
	
	/* process starting */
	@Override
	public void runProcess() throws Exception {
		// TODO Auto-generated method stub
		//log.info("=====================================================================");
		//log.info("=" + this.getClass().getName()+ " runProcess() ==");
		//log.info("=====================================================================");

		/*트랜잭션 업무 구현*/
		mainTransProcess();

	}

	/*트랜잭션을 실행하기 위한 함수.*/
	private void mainTransProcess(){
		
		log.info("=====================================================================");
		log.info("=[표준세외수입-[" + c_slf_org_nm + "] 수납자료전송] Start =");
		log.info("=====================================================================");
		
		try {
				
			setContext(appContext);
			setApp(appContext);

			UcContextHolder.setCustomerType(this.dataSource);
						
			dataMap = new MapForm();  /*초기화*/
			
			log.info("this.c_slf_org ======================= [" + this.c_slf_org + "]");
			dataMap.setMap("SGG_COD", this.c_slf_org);
			
			do {
				
				int page = cyberService.getOneFieldInteger("TXDM2412.SELECT_TX2211_CNT", dataMap);
				
				log.info("[표준세외수입-[" + c_slf_org_nm + "]] CNT = [" + page + "]");
				
				if(page == 0){
					
					/***********************************************************************************/
					try{
						MapForm daemonMap = new MapForm();
						daemonMap.setMap("DAEMON"    , "TXDM2412");
						daemonMap.setMap("DAEMON_NM" , "표준세외수입(RISTNACOCR)수납전송");
						daemonMap.setMap("SGG_COD"   , c_slf_org);
						daemonMap.setMap("TOTAL_CNT" , page);
						daemonMap.setMap("INSERT_CNT", 0);
						daemonMap.setMap("UPDATE_CNT", 0);
						daemonMap.setMap("DELETE_CNT", 0);
						daemonMap.setMap("ERROR_CNT" , 0);
						cyberService.queryForInsert("CODMBASE.InsertDaemonProcessCheck", daemonMap);
					}catch(Exception ex){
						log.debug("표준세외수입 수납자료전송 대상이 없습니다. 등록 오류");
					}				
					/***********************************************************************************/
					
					break;
				}

				
				dataMap.setMap("TRN_SNO", Long.parseLong(cyberService.getOneFieldString("TXDM2412.SELECT_TRN_SNO", dataMap)));
				if(cyberService.queryForUpdate("TXDM2412.UPDATE_TX2211_CNT", dataMap)==0) break;
				
				this.startJob();               /*멀티 트랜잭션 호출*/
				
				if(cyberService.getOneFieldInteger("TXDM2412.SELECT_TX2211_CNT", dataMap) == 0) {
					break;
				}
				
			}while(true);
					
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}			
		
	}
	
	/*현 사용할 일이 없넹...*/
	@Override
	public void setDatasrc(String datasrc) {
		// TODO Auto-generated method stub
		this.dataSource = datasrc;
	}
	
	/*트랜잭션 구성*/
	@Override
	public void transactionJob() {
		// TODO Auto-generated method stub
			
		/*---------------------------------------------------------*/
		/*1. 수납자료 전송 업무 처리.                               */
		/*---------------------------------------------------------*/
		txdm2412_JobProcess();
	
	}
	
	
    /*표준세외수입...연계*/
	private int txdm2412_JobProcess() {
		
		log.info("=====================================================================");
		log.info("=" + this.getClass().getName()+ " txdm2412_JobProcess()[표준세외수입-[" + c_slf_org_nm + "] 수납자료 전송] Start =");
		log.info("= govid["+this.govId+"], orgcd["+this.c_slf_org+"]");
		log.info("=====================================================================");
		
		/*초기화*/
		
		insert_cnt = 0;
		update_cnt = 0; 
		delete_cnt = 0;
		error_cnt  = 0;
		remote_up_cnt = 0;
		p_del_cnt     = 0;
		
		/*전역 초기화*/
		gblNidLevyRows = new MapForm();

		int rec_cnt = 0;
		int nul_cnt = 0;
		
		int tot_size = 0;
		
		long elapseTime1 = 0;
		long elapseTime2 = 0;
		
		elapseTime1 = System.currentTimeMillis();

		/*---------------------------------------------------------*/
		/*1. 수납자료 전송 업무 처리.                               */
		/*---------------------------------------------------------*/
		
		try {

			/*연계테이블 부과자료 SELECT 쿼리(NIS)*/
			ArrayList<MapForm> alNidLevyList =  cyberService.queryForList("TXDM2412.SELECT_TX2211_LIST", dataMap);
			
			tot_size = alNidLevyList.size();
			
		    log.info("[" + c_slf_org_nm + "]세외수입수납자료전송 건수 = [" + tot_size + "]");
			
			if (tot_size  >  0)   {
				
				/*전자납부자료 1건씩 fetch 처리 */
				for ( rec_cnt = 0;  rec_cnt < tot_size;  rec_cnt++)   {
					
					gblNidLevyRows =  alNidLevyList.get(rec_cnt);
					
					if (gblNidLevyRows == null  ||  gblNidLevyRows.isEmpty() )   {
						nul_cnt++;
						continue;
					}
					if(govService.getOneFieldInteger("TXDM2412.SELECT_RISTNACOCR_CNT", gblNidLevyRows)>0){
//테스트용			if(cyberService.getOneFieldInteger("TXDM2412.SELECT_RISTNACOCR_CNT", gblNidLevyRows)>0){
						gblNidLevyRows.setMap("TRTG","5");
					}else{

						String PAY_YMD = (String) gblNidLevyRows.getMap("PAY_YMD");
						String CURR_DATE = (String) gblNidLevyRows.getMap("CURR_DATE");
						String SGG_COD = (String) gblNidLevyRows.getMap("SGG_COD");
						if(!PAY_YMD.equals(CURR_DATE)||SGG_COD.equals("340")){
							gblNidLevyRows.setMap("SNO",govService.getOneFieldString("TXDM2412.SELECT_RISTNACOCR_MAX_SNO", gblNidLevyRows));
						}
						
						try{
							govService.queryForUpdate("TXDM2412.INSERT_RISTNACOCR", gblNidLevyRows);
//테스트용					cyberService.queryForUpdate("TXDM2412.INSERT_RISTNACOCR", gblNidLevyRows);
							insert_cnt++;
							gblNidLevyRows.setMap("TRTG","1");
						}catch (Exception sub_e){
							log.error("INSERT_RISTNACOCR 오류데이터 = " + gblNidLevyRows.getMaps());
							error_cnt++;
							sub_e.printStackTrace();
						}						
					}
					
					try{
						/*원장 업데이트*/
						remote_up_cnt +=cyberService.queryForUpdate("TXDM2412.UPDATE_TX2211_TRTG", gblNidLevyRows);
					}catch (Exception sub_e){
						log.error("UPDATE_O_ORG_TBL 오류데이터 = " + gblNidLevyRows.getMaps());
						error_cnt++;
						sub_e.printStackTrace();
					}

					if(rec_cnt % 500 == 0) {
						log.info("[H][" + this.c_slf_org_nm + "][" + this.c_slf_org + "]세외수입 수납자료 전송건수 = [" + rec_cnt + "]");
					}
				}
				log.info("[" + c_slf_org_nm + "]세외수입수납자료 전송 건수 [" + rec_cnt + "] 등록건수 [" + insert_cnt +"] 수정건수 [" + update_cnt + "] 삭제건수 [" + delete_cnt + "] 물리적삭제 [" + p_del_cnt + "]");
				log.info("원장업데이트 ["+remote_up_cnt+"], 미처리 부과건수 [" + nul_cnt + "]");
				
				/***********************************************************************************/
				try{
					MapForm daemonMap = new MapForm();
					daemonMap.setMap("DAEMON"    , "TXDM2412");
					daemonMap.setMap("DAEMON_NM" , "표준세외수입(RISTNACOCR)수납전송");
					daemonMap.setMap("SGG_COD"   , c_slf_org);
					daemonMap.setMap("TOTAL_CNT" , rec_cnt);
					daemonMap.setMap("INSERT_CNT", insert_cnt);
					daemonMap.setMap("UPDATE_CNT", update_cnt);
					daemonMap.setMap("DELETE_CNT", delete_cnt);
					daemonMap.setMap("ERROR_CNT" , error_cnt);
					cyberService.queryForInsert("CODMBASE.InsertDaemonProcessCheck", daemonMap);
				}catch(Exception ex){
					log.debug("표준세외수입 수납자료전송 대상이 없습니다. 등록 오류");
				}				
				/***********************************************************************************/
				
			}
			elapseTime2 = System.currentTimeMillis();
			log.info("[" + c_slf_org_nm + "]세외수입 수납자료전송 시간("+tot_size+") : " + CbUtil.formatTime(elapseTime2 - elapseTime1));
					
		} catch (Exception e) {
		    log.error("SELECT_O_LEVY_LIST 오류 데이터 = " + gblNidLevyRows.getMaps());
			throw (RuntimeException) e;
		}
		return tot_size;
	}
}
