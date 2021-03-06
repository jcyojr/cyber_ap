/**
 *  주시스템명 : 부산시청 사이버 지방세청
 *  업  무  명 : 연계
 *  기  능  명 : 지방세수납요약정보
 *  클래스  ID : Txdm1151_process : 실 처리로직을 작성하는 클래스
 *  변경  이력 :
 * ------------------------------------------------------------------------
 *  작성자      	소속  		    일자            Tag           내용
 * ------------------------------------------------------------------------
 *  황종훈       유채널(주)      2012.03.08         %01%         최초작성
 */
package com.uc.bs.cyber.daemon.txdm1151;

import java.util.ArrayList;

import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.dao.DuplicateKeyException;

import com.uc.bs.cyber.CbUtil;
import com.uc.bs.cyber.daemon.Codm_BaseProcess;
import com.uc.bs.cyber.daemon.Codm_interface;
import com.uc.core.MapForm;
import com.uc.core.spring.service.UcContextHolder;

public class Txdm1151_process extends Codm_BaseProcess implements Codm_interface {

	private MapForm dataMap = null;
	private int rcp_cnt = 0, remote_up_cnt = 0, delete_cnt = 0, update_cnt = 0;
	
	/*트랜잭션을 위하여 */
	MapForm rcpListRow = null;
	
	/**
	 * 
	 */
	public Txdm1151_process() {
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
		log.info("=[지방세수납요약정보-[" + c_slf_org_nm + "] 자료송신] Start =");
		log.info("=====================================================================");
		try {
				
			setContext(appContext);
			setApp(appContext);

			UcContextHolder.setCustomerType(this.dataSource);
						
			dataMap = new MapForm();  /*초기화*/
			
			log.info("this.c_slf_org ======================= [" + this.c_slf_org + "]");

			dataMap.setMap("SGG_COD",  this.c_slf_org);
			
			do {
				
				int rcpCnt = govService.getOneFieldInteger("TXDM1151.SELECT_COUNT", dataMap);
				
				log.info("[지방세수납요약정보-[" + c_slf_org_nm + "]] 건수(" + rcpCnt + ")");
				
				if(rcpCnt == 0){
					break;
				}else{
					this.startJob();
				}
				break;
				//if(govService.getOneFieldInteger("TXDM1151.SELECT_COUNT", dataMap) == 0) break;
				
			} while(true);
					
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
			
		txdm1151_JobProcess();
	
	}

	private int txdm1151_JobProcess() {
		
		log.info("=====================================================================");
		log.info("=" + this.getClass().getName()+ " txdm1151_JobProcess()[지방세수납요약정보-[" + c_slf_org_nm + "] 자료전송] Start =");
		log.info("= govid["+this.govId+"], orgcd["+this.c_slf_org+"]");
		log.info("=====================================================================");
		
		/*초기화*/
		rcp_cnt = 0;
		remote_up_cnt = 0;
		delete_cnt = 0;
		update_cnt = 0;
	
		/*전역 초기화*/
		rcpListRow = new MapForm();

		int nul_cnt = 0;
		int cnt = 0;
		int tot_size = 0;
		
		long elapseTime1 = 0;
		long elapseTime2 = 0;
		
		elapseTime1 = System.currentTimeMillis();

		/*---------------------------------------------------------*/
		/*1. 지방세수납요약정보 업무 처리.                               */
		/*---------------------------------------------------------*/
		
		try {

			dataMap.setMap("SGG_COD",  this.c_slf_org);

			/* 실시간 수납/취소 SELECT 쿼리 */
			ArrayList<MapForm> rcpList =  govService.queryForList("TXDM1151.SELECT_LIST", dataMap);
			
			tot_size = rcpList.size();
			
		    log.info("[" + c_slf_org_nm + "]지방세수납요약정보 건수 = [" + tot_size + "]");
			
			if (tot_size  >  0)   {
				
				/* 실시간 수납/취소 자료를 해당 구청에 fetch */
				for (cnt = 0;  cnt < tot_size;  cnt++) {

					rcpListRow =  rcpList.get(cnt);
					
					/*혹시나...because of testing */
					if (rcpListRow == null  ||  rcpListRow.isEmpty()) {
						nul_cnt++;
						continue;
					}
					
					if(rcpListRow.getMap("CUD_OPT").equals("3")){
						/*
						try {
							
							rcpListRow.setMap("DEL_YN", "N");
							
							cyberService.queryForUpdate("TXDM1151.TXSV1151_UPDATE_TX1102_TB", rcpListRow);
							
						} catch (Exception e) {
							
							log.error("TX1102_TB 에러 = " + rcpListRow.getMaps());
							log.error(e.getMessage());
							
						}
						*/
						
						try {

							cyberService.queryForUpdate("TXDM1151.TXSV1151_DELETE_TX1211_TB", rcpListRow);
							
							delete_cnt++;
							
						} catch (Exception e) {
							
							log.error("오류데이터 = " + rcpListRow.getMaps());
							e.printStackTrace();							
							throw (RuntimeException) e;
								
						}
						
					} else if(rcpListRow.getMap("CUD_OPT").equals("2")){
						
						int updateCnt = 0;
						/*
						try {
							
							rcpListRow.setMap("DEL_YN", "Y");
							
							cyberService.queryForUpdate("TXDM1151.TXSV1151_UPDATE_TX1102_TB", rcpListRow);
							
						} catch (Exception e) {
							
							log.error("TX1102_TB 에러 = " + rcpListRow.getMaps());
							log.error(e.getMessage());
							
						}	
						*/					
						
						try {						
							
							updateCnt = cyberService.queryForUpdate("TXDM1151.TXSV1151_UPDATE_TX1211_TB", rcpListRow);
							
							update_cnt++;
							
						} catch (Exception e) {
							
							log.error("오류데이터 = " + rcpListRow.getMaps());
							e.printStackTrace();							
							throw (RuntimeException) e;
							
						}
						
						if(updateCnt == 0) {
							
							try {
								
								cyberService.queryForUpdate("TXDM1151.TXSV1151_INSERT_TX1211_TB", rcpListRow);
								
								//update_cnt++;
								
							} catch (Exception e) {
								
								log.error("오류데이터 = " + rcpListRow.getMaps());
								e.printStackTrace();							
								throw (RuntimeException) e;
								
							}
							
						}
						
					} else if(rcpListRow.getMap("CUD_OPT").equals("1")){
						
						/*
						try {
							
							rcpListRow.setMap("DEL_YN", "Y");
							
							cyberService.queryForUpdate("TXDM1151.TXSV1151_UPDATE_TX1102_TB", rcpListRow);
							
						} catch (Exception e) {
							
							log.error("TX1102_TB 에러 = " + rcpListRow.getMaps());
							log.error(e.getMessage());
							
						}
						*/
						
						try {
							
							cyberService.queryForUpdate("TXDM1151.TXSV1151_INSERT_TX1211_TB", rcpListRow);
							
							rcp_cnt++;
							
						} catch (Exception e) {
							
							/*중복이 발생하거나 제약조건에 어긋나는 경우체크*/
							if (e instanceof DuplicateKeyException){
								
								try {
									
									cyberService.queryForUpdate("TXDM1151.TXSV1151_UPDATE_TX1211_TB", rcpListRow);
									    
								} catch (Exception sub_e) {
									log.error("오류데이터 = " + rcpListRow.getMaps());
									e.printStackTrace();
									throw (RuntimeException) sub_e;
								}
								
							} else {
								
								log.error("오류데이터 = " + rcpListRow.getMaps());
								e.printStackTrace();							
								throw (RuntimeException) e;
								
							}
							
						}
						
					}
					
					/*=========== UPDATE ===========*/
					try {
						
						/* 지방세수납요약정보 완료 업데이트 */
						remote_up_cnt += govService.queryForUpdate("TXDM1151.TXSV3151_UPDATE_DONE", rcpListRow);
						
					} catch (Exception e) {
						
						log.error("오류데이터 = " + rcpListRow.getMaps());
						e.printStackTrace();							
						throw (RuntimeException) e;
							
					}

				}
				
				log.info("[" + c_slf_org_nm + "]지방세수납요약정보 [" + cnt + "] 신규 [" + rcp_cnt +"] 변경 [" + update_cnt + "] 삭제 [" + delete_cnt + "]");
				log.info("처리건수 ["+remote_up_cnt+"], 미처리 부과건수 [" + nul_cnt + "]");
			}

			elapseTime2 = System.currentTimeMillis();
			
			log.info("[" + c_slf_org_nm + "]지방세수납요약정보 전송 시간("+tot_size+") : " + CbUtil.formatTime(elapseTime2 - elapseTime1));
					
		} catch (Exception e) {
			
		    log.error("오류 데이터 = " + rcpListRow.getMaps());
		    e.printStackTrace();
			throw (RuntimeException) e;
			
		}
		
		return tot_size;
	}
	
}
