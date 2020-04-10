/**
 *  주시스템명 : 부산시청 사이버 지방세청
 *  업  무  명 : 연계
 *  기  능  명 : 가상계좌채번자료 구청별 연계
 *  클래스  ID : Txdm1121_process : 실 처리로직을 작성하는 클래스
 *  변경  이력 :
 * ------------------------------------------------------------------------
 *  작성자      	소속  		    일자            Tag           내용
 * ------------------------------------------------------------------------
 *  황종훈       유채널(주)      2012.03.08         %01%         최초작성
 */
package com.uc.bs.cyber.daemon.txdm1121;

import java.util.ArrayList;

import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.dao.DuplicateKeyException;

import com.uc.bs.cyber.CbUtil;
import com.uc.bs.cyber.daemon.Codm_BaseProcess;
import com.uc.bs.cyber.daemon.Codm_interface;
import com.uc.core.MapForm;
import com.uc.core.spring.service.UcContextHolder;

public class Txdm1121_process extends Codm_BaseProcess implements Codm_interface {

	private MapForm dataMap = null;
	private int insert_cnt = 0, update_cnt = 0, remote_up_cnt = 0;
	
	/*트랜잭션을 위하여 */
	MapForm virListRow = null;
	
	/**
	 * 
	 */
	public Txdm1121_process() {
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
		log.info("=[가상계좌채번-[" + c_slf_org_nm + "] 자료송신] Start =");
		log.info("=====================================================================");
		try {
				
			setContext(appContext);
			setApp(appContext);

			UcContextHolder.setCustomerType(this.dataSource);
						
			dataMap = new MapForm();  /*초기화*/
			
			log.info("this.c_slf_org ======================= [" + this.c_slf_org + "]");

			dataMap.setMap("SGG_COD",  this.c_slf_org);
			//dataMap.setMap("PAGE_PER_CNT"   ,  500);           /*페이지당 갯수*/
            if(dataMap.getMap("SGG_COD").equals("000")){
            	int virCnt = cyberService.getOneFieldInteger("TXDM1121.SELECT_VIR_COUNT_SIDO", dataMap);
    			log.info("[가상계좌채번-[" + c_slf_org_nm + "]] 건수(" + virCnt + ")");
    			if(virCnt > 0){
    				this.startJob();
    			}else{
    				
    				/***********************************************************************************/
    				try{
    					MapForm daemonMap = new MapForm();
    					daemonMap.setMap("DAEMON"    , "TXDM1121");
    					daemonMap.setMap("DAEMON_NM" , "가상계좌채번(SCON604)전송");
    					daemonMap.setMap("SGG_COD"   , c_slf_org);
    					daemonMap.setMap("TOTAL_CNT" , virCnt);
    					daemonMap.setMap("INSERT_CNT", 0);
    					daemonMap.setMap("UPDATE_CNT", 0);
    					daemonMap.setMap("DELETE_CNT", 0);
    					daemonMap.setMap("ERROR_CNT" , 0);
    					cyberService.queryForInsert("CODMBASE.InsertDaemonProcessCheck", daemonMap);
    				}catch(Exception ex){
    					log.debug("지방세 가상계좌채번전송 대상이 없습니다. 등록 오류");
    				}				
    				/***********************************************************************************/
    				
    			}
            }else{
            	int virCnt = cyberService.getOneFieldInteger("TXDM1121.SELECT_VIR_COUNT", dataMap);
    			log.info("[가상계좌채번-[" + c_slf_org_nm + "]] 건수(" + virCnt + ")");
    			if(virCnt > 0){
    				this.startJob();
    			}else{
    				
    				/***********************************************************************************/
    				try{
    					MapForm daemonMap = new MapForm();
    					daemonMap.setMap("DAEMON"    , "TXDM1121");
    					daemonMap.setMap("DAEMON_NM" , "가상계좌채번(SCON604)전송");
    					daemonMap.setMap("SGG_COD"   , c_slf_org);
    					daemonMap.setMap("TOTAL_CNT" , virCnt);
    					daemonMap.setMap("INSERT_CNT", 0);
    					daemonMap.setMap("UPDATE_CNT", 0);
    					daemonMap.setMap("DELETE_CNT", 0);
    					daemonMap.setMap("ERROR_CNT" , 0);
    					cyberService.queryForInsert("CODMBASE.InsertDaemonProcessCheck", daemonMap);
    				}catch(Exception ex){
    					log.debug("지방세 가상계좌채번전송 대상이 없습니다. 등록 오류");
    				}				
    				/***********************************************************************************/
    				
    			}
            }
            
            
            
			//int virCnt = cyberService.getOneFieldInteger("TXDM1121.SELECT_VIR_COUNT", dataMap);
			//log.info("[가상계좌채번-[" + c_slf_org_nm + "]] 건수(" + virCnt + ")");
			//if(virCnt > 0) this.startJob();
					
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
		/*1. 자료연계 업무처리		                               */
		/*---------------------------------------------------------*/
		txdm1121_JobProcess();
	
	}

	private int txdm1121_JobProcess() {
		
		log.info("=====================================================================");
		log.info("=" + this.getClass().getName()+ " txdm1121_JobProcess()[가상계좌채번-[" + c_slf_org_nm + "] 자료전송] Start =");
		log.info("= govid["+this.govId+"], orgcd["+this.c_slf_org+"]");
		log.info("=====================================================================");
		
		/*초기화*/
		insert_cnt = 0;
		update_cnt = 0; 
		remote_up_cnt = 0;
	
		/*전역 초기화*/
		virListRow = new MapForm();

		int nul_cnt = 0;
		int cnt = 0;
		int tot_size = 0;
		
		long elapseTime1 = 0;
		long elapseTime2 = 0;
		
		elapseTime1 = System.currentTimeMillis();

		/*---------------------------------------------------------*/
		/*1. 부가연계자료 업무 처리.                               */
		/*---------------------------------------------------------*/
		
		try {

			dataMap.setMap("SGG_COD",  this.c_slf_org);

			/*연계테이블 부과자료 SELECT 쿼리(NIS)*/
			ArrayList<MapForm> virAccList = new ArrayList<MapForm>();
			if(dataMap.getMap("SGG_COD").equals("000")){
				virAccList =  cyberService.queryForList("TXDM1121.SELECT_VIR_LIST_SIDO", dataMap);
			}else{
				virAccList =  cyberService.queryForList("TXDM1121.SELECT_VIR_LIST", dataMap);
			}
			
			//ArrayList<MapForm> virAccList =  cyberService.queryForList("TXDM1121.SELECT_VIR_LIST", dataMap);
			
			tot_size = virAccList.size();
			
		    log.info("[" + c_slf_org_nm + "]가상계좌채번자료 건수 = [" + tot_size + "]");
			
			if (tot_size  >  0)   {
				  log.info(" (((((( 작업하는 구청 )))))) == >"+dataMap.getMap("SGG_COD"));
				/* 가상계좌채번자료를 해당 구청에 fetch */
				for (cnt = 0;  cnt < tot_size;  cnt++)   {

					virListRow =  virAccList.get(cnt);
					
					/*혹시나...because of testing */
					if (virListRow == null  ||  virListRow.isEmpty() )   {
						nul_cnt++;
						continue;
					}
					
					/*기본 Default 값 설정 */
					virListRow.setMap("SGG_TR_TG"   , "1" );                       /*구청전송처리구분*/
					
					/* 시도서버와 구청서버에 scon604 테이블 컬럼이 달라서 분기 */
					if(dataMap.getMap("SGG_COD").equals("000") || virListRow.getMap("ACCT_COD").equals("39")){
						
						/*=========== INSERT ===========*/
						try {
							if(govService.getOneFieldInteger("TXDM1121.SELECT_SEND_VIR_COUNT", virListRow)>0){
								govService.queryForUpdate("TXDM1121.VIRACC_UPDATE_SIDO", virListRow);
								update_cnt++;

							}else{
								govService.queryForUpdate("TXDM1121.VIRACC_INSERT_SIDO", virListRow);
								insert_cnt++;
							}
						} catch (Exception e) {
							log.info(e.getMessage());
							log.error("오류데이터 = " + virListRow.getMaps());
							log.error(e.getMessage());
							e.printStackTrace();							
							throw (RuntimeException) e;
						}						
						
					} else if(dataMap.getMap("SGG_COD").equals("710")){    /* 기장군 추가*/
						
						/*=========== INSERT ===========*/
						try {
							
							MapForm virAccMapForm_604 = new MapForm();
							
							ArrayList<MapForm> virAccList_604_test = govService.queryForList("TXDM1121.SELECT_VIR_LIST_604", virListRow);
							
							if(virAccList_604_test.size()>0){
								virAccMapForm_604 = (MapForm)virAccList_604_test.get(0);
							}else{	
								continue;
							}
							
							
							if(virAccMapForm_604.getMap("VIR_ACC_2")== null){ //가상계좌 없을 때	
							
									try{
										govService.queryForUpdate("TXDM1121.VIRACC2_UPDATE_FIRST", virListRow);
										update_cnt++;
										    
									} catch (Exception sub_e) {
										log.error("오류데이터(VIRACC2_UPDATE_FIRST) = " + virListRow.getMaps());									
											sub_e.printStackTrace();
											throw (RuntimeException) sub_e;
									}							

							
							}else{ //가상계좌가 있을 때(가상계좌를 최초 업데이트 한게 아닐 때) 
	
									try{
										govService.queryForUpdate("TXDM1121.VIRACC2_UPDATE_NOT_FIRST", virListRow);
										update_cnt++;
										    
									} catch (Exception sub_e) {
										log.error("오류데이터(VIRACC2_UPDATE_NOT_FIRST) = " + virListRow.getMaps());									
											sub_e.printStackTrace();
											throw (RuntimeException) sub_e;
									}	
							}
							
						} catch (Exception e) {
							log.error("오류데이터 = " + virListRow.getMaps());
							log.error(e.getMessage());
							e.printStackTrace();							
							throw (RuntimeException) e;
						}
						
					} else {
					
						/*=========== INSERT ===========*/
						try {
							if(govService.getOneFieldInteger("TXDM1121.SELECT_SEND_VIR_COUNT", virListRow)>0){
								govService.queryForUpdate("TXDM1121.VIRACC_UPDATE", virListRow);
								update_cnt++;
							}else{
								govService.queryForUpdate("TXDM1121.VIRACC_INSERT", virListRow);
								insert_cnt++;
							}
						} catch (Exception e) {
							log.error("오류데이터 = " + virListRow.getMaps());
							log.error(e.getMessage());
							e.printStackTrace();							
							throw (RuntimeException) e;
						}
					}
					
					try{
						
						/* 지방세 상세테이블 가상계좌 전송 완료 업데이트 */
						remote_up_cnt += cyberService.queryForUpdate("TXDM1121.UPDATE_COMPLETE", virListRow);
						    
					} catch (Exception e) {
						
						log.error("오류데이터 = " + virListRow.getMaps());
						log.error(e.getMessage());
						e.printStackTrace();
						throw (RuntimeException) e;
						
					}

				}
				
				log.info("[" + c_slf_org_nm + "]가상계좌채번 건수 [" + cnt + "] 등록건수 [" + insert_cnt +"] 수정건수 [" + update_cnt + "]");
				log.info("지방세상세테이블업데이트 ["+remote_up_cnt+"], 미처리 부과건수 [" + nul_cnt + "]");
				
				/***********************************************************************************/
				try{
					MapForm daemonMap = new MapForm();
					daemonMap.setMap("DAEMON"    , "TXDM1121");
					daemonMap.setMap("DAEMON_NM" , "가상계좌채번(SCON604)전송");
					daemonMap.setMap("SGG_COD"   , c_slf_org);
					daemonMap.setMap("TOTAL_CNT" , cnt);
					daemonMap.setMap("INSERT_CNT", insert_cnt);
					daemonMap.setMap("UPDATE_CNT", update_cnt);
					daemonMap.setMap("DELETE_CNT", 0);
					daemonMap.setMap("ERROR_CNT" , 0);
					cyberService.queryForInsert("CODMBASE.InsertDaemonProcessCheck", daemonMap);
				}catch(Exception ex){
					log.debug("지방세 가상계좌채번전송 대상이 없습니다. 등록 오류");
				}				
				/***********************************************************************************/
				
			}

			elapseTime2 = System.currentTimeMillis();
			
			log.info("[" + c_slf_org_nm + "]가상계좌채번 전송 시간("+tot_size+") : " + CbUtil.formatTime(elapseTime2 - elapseTime1));
					
		} catch (Exception e) {
		    log.error("오류 데이터 = " + virListRow.getMaps());
		    log.error(e.getMessage());
			throw (RuntimeException) e;
		}
		
		return tot_size;
	}
}
