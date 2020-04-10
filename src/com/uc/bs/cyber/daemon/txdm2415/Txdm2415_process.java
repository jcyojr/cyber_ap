/**
 *  주시스템명 : 부산시청 사이버 지방세청
 *  업  무  명 : 연계
 *  기  능  명 : 표준세외수입 과오납자료연계
 *  클래스  ID : Txdm2415_process : 실 처리로직을 작성하는 클래스
 *  변경  이력 :
 * ------------------------------------------------------------------------
 *  작성자      	소속  		    일자            Tag           내용
 * ------------------------------------------------------------------------
 *  표승한       다산시스템             2014.04.10         %01%         최초작성
 */
package com.uc.bs.cyber.daemon.txdm2415;

import java.util.ArrayList;

import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;

import com.uc.bs.cyber.CbUtil;
import com.uc.bs.cyber.daemon.Codm_BaseProcess;
import com.uc.bs.cyber.daemon.Codm_interface;
import com.uc.core.MapForm;
import com.uc.core.spring.service.UcContextHolder;

public class Txdm2415_process extends Codm_BaseProcess implements Codm_interface {

	private MapForm dataMap            = null;
	private int insert_cnt = 0, update_cnt = 0, delete_cnt = 0, remote_up_cnt = 0;
    private int p_del_cnt = 0;
	
	/*트랜잭션을 위하여 */
	MapForm gblNidLevyRows    = null;
	
	/**
	 * 
	 */
	public Txdm2415_process() {
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
		log.info("=[표준세외수입-[" + c_slf_org_nm + "] 과오납자료연계] Start =");
		log.info("=====================================================================");
		
		try {
				
			setContext(appContext);
			setApp(appContext);

			UcContextHolder.setCustomerType(this.dataSource);
						
			dataMap = new MapForm();  /*초기화*/
			
			log.info("this.c_slf_org ======================= [" + this.c_slf_org + "]");

			long SEQ = 0L;
			try{
				SEQ = Long.parseLong(govService.getOneFieldString("TXDM2415.SELECT_SPNT410_SEQ", null));
			}catch (Exception sub_e){
				log.error("SELECT_SPNT410_SEQ 오류");
				sub_e.printStackTrace();
			}						

			if(SEQ>0L){
				try{
					govService.queryForUpdate("TXDM2415.UPDATE_SPNT410_START", SEQ);
				}catch (Exception sub_e){
					log.error("UPDATE_SPNT410_START 오류");
					sub_e.printStackTrace();
				}						

				this.startJob();

				try{
					govService.queryForUpdate("TXDM2415.UPDATE_SPNT410_END", null);
				}catch (Exception sub_e){
					log.error("UPDATE_SPNT410_END 오류");
					sub_e.printStackTrace();
				}						
			
			}else{
				
				/***********************************************************************************/
				try{
					MapForm daemonMap = new MapForm();
					daemonMap.setMap("DAEMON"    , "TXDM2415");
					daemonMap.setMap("DAEMON_NM" , "표준세외수입(SPNT410)과오납자료연계");
					daemonMap.setMap("SGG_COD"   , c_slf_org);
					daemonMap.setMap("TOTAL_CNT" , 0);
					daemonMap.setMap("INSERT_CNT", 0);
					daemonMap.setMap("UPDATE_CNT", 0);
					daemonMap.setMap("DELETE_CNT", 0);
					daemonMap.setMap("ERROR_CNT" , 0);
					cyberService.queryForInsert("CODMBASE.InsertDaemonProcessCheck", daemonMap);
				}catch(Exception ex){
					log.debug("표준세외수입 과오납자료연계 대상이 없습니다. 등록 오류");
				}				
				/***********************************************************************************/
				
			}

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
		/*1. 과오납연계자료 업무 처리.                               */
		/*---------------------------------------------------------*/
		txdm2415_JobProcess();
	
	}
	
	
    /*표준세외수입...과오납연계*/
	private int txdm2415_JobProcess() {
		
		log.info("=====================================================================");
		log.info("=" + this.getClass().getName()+ " txdm2415_JobProcess()[표준세외수입-[" + c_slf_org_nm + "] 과오납자료연계] Start =");
		log.info("= govid["+this.govId+"], orgcd["+this.c_slf_org+"]");
		log.info("=====================================================================");
		
		/*초기화*/
		
		insert_cnt = 0;
		update_cnt = 0; 
		delete_cnt = 0;
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
		/*1. 과오납연계자료 업무 처리.                               */
		/*---------------------------------------------------------*/
		log.info("------try문 들어가기 전 -----------");
		try {

			/*연계테이블 과오납자료 SELECT 쿼리(NIS)*/
			ArrayList<MapForm> alNidLevyList = null;

			try{
				alNidLevyList =  govService.queryForList("TXDM2415.SELECT_SPNT410_LIST", dataMap);
			}catch (Exception sub_e){
				log.error("SELECT_SPNT410_LIST 오류데이터 = " + gblNidLevyRows.getMaps());
				sub_e.printStackTrace();
			}						

			tot_size = alNidLevyList.size();
			
		    log.info("[" + c_slf_org_nm + "]세외수입과오납연계자료 건수 = [" + tot_size + "]");
			
			if (tot_size  >  0)   {
				
				/*전자납부자료 1건씩 fetch 처리 */
				for ( rec_cnt = 0;  rec_cnt < tot_size;  rec_cnt++)   {
					
					gblNidLevyRows =  alNidLevyList.get(rec_cnt);
					
					/*혹시나...because of testing */
					if (gblNidLevyRows == null  ||  gblNidLevyRows.isEmpty() ){
						nul_cnt++;
						continue;
					}

				    String TRN_YN = "1";
				    
				    String TRNN_ID = (String) gblNidLevyRows.getMap("TRNN_ID");
				    String DEP_COD = (String) gblNidLevyRows.getMap("DEP_COD");
				    if(!TRNN_ID.substring(0,3).equals(DEP_COD.substring(0,3)))TRN_YN = "X";
				    
					if(TRN_YN.equals("1")){
						
						// 기연계자료
						int TX2401_CNT = cyberService.getOneFieldInteger("TXDM2415.SELECT_TX2401_CNT", gblNidLevyRows);

						if(TX2401_CNT>0){
							try{
								cyberService.queryForUpdate("TXDM2415.UPDATE_TX2401", gblNidLevyRows);
								update_cnt++;
							}catch (Exception sub_e){
								TRN_YN="E";
								log.error("UPDATE_TX2401 오류 = " + gblNidLevyRows.getMaps());
								sub_e.printStackTrace();
							}
						}else{
							try{
								cyberService.queryForUpdate("TXDM2415.INSERT_TX2401", gblNidLevyRows);
								insert_cnt++;
							}catch (Exception sub_e){
								TRN_YN="E";
								log.error("INSERT_TX2401 오류 = " + gblNidLevyRows.getMaps());
								sub_e.printStackTrace();
							}						
						}
					}
					gblNidLevyRows.setMap("TRN_YN",TRN_YN);
					
					try{
						/*고지원장 업데이트*/
						remote_up_cnt +=govService.queryForUpdate("TXDM2415.UPDATE_SPNT410_TRN_YN", gblNidLevyRows);
					}catch (Exception sub_e){
						log.error("UPDATE_SPNT410_TRN_YN 오류 = " + gblNidLevyRows.getMaps());
						sub_e.printStackTrace();
					}	
					
					if(rec_cnt % 500 == 0) {
						log.info("[" + this.c_slf_org_nm + "][" + this.c_slf_org + "]세외수입과오납자료처리중건수 = [" + rec_cnt + "]");
					}
				}
				
				log.info("[" + c_slf_org_nm + "]세외수입과오납자료연계 건수 [" + rec_cnt + "] 등록건수 [" + insert_cnt +"] 수정건수 [" + update_cnt + "] 삭제건수 [" + delete_cnt + "] 물리적삭제 [" + p_del_cnt + "]");
				log.info("고지원장업데이트 ["+remote_up_cnt+"], 미처리 부과건수 [" + nul_cnt + "]");
				
				/***********************************************************************************/
				try{
					MapForm daemonMap = new MapForm();
					daemonMap.setMap("DAEMON"    , "TXDM2415");
					daemonMap.setMap("DAEMON_NM" , "표준세외수입(SPNT410)과오납자료연계");
					daemonMap.setMap("SGG_COD"   , c_slf_org);
					daemonMap.setMap("TOTAL_CNT" , rec_cnt);
					daemonMap.setMap("INSERT_CNT", insert_cnt);
					daemonMap.setMap("UPDATE_CNT", update_cnt);
					daemonMap.setMap("DELETE_CNT", delete_cnt);
					daemonMap.setMap("ERROR_CNT" , 0);
					cyberService.queryForInsert("CODMBASE.InsertDaemonProcessCheck", daemonMap);
				}catch(Exception ex){
					log.debug("표준세외수입 과오납자료연계 로그 등록 오류");
				}				
				/***********************************************************************************/
				
			}

			elapseTime2 = System.currentTimeMillis();
			
			log.info("[" + c_slf_org_nm + "]세외수입 과오납자료연계 시간("+tot_size+") : " + CbUtil.formatTime(elapseTime2 - elapseTime1));
					
		} catch (Exception e) {
		    log.error("TXDM2415 연계 오류 = " + gblNidLevyRows.getMaps());
			throw (RuntimeException) e;
		}
		
		return tot_size;
	}
}
