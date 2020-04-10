/**
*  주시스템명 : 부산시청 사이버 지방세청
 *  업  무  명 : 연계
 *  기  능  명 : 실시간수납자료(납부,취소) 송신(시청, 구청)
 *  클래스  ID : Txdm1131_process : 실 처리로직을 작성하는 클래스
 *  변경  이력 :
 * ------------------------------------------------------------------------
 *  작성자      	소속  		    일자            Tag           내용
 * ------------------------------------------------------------------------
 *  황종훈       유채널(주)      2012.03.08         %01%         최초작성
 */
package com.uc.bs.cyber.daemon.txdm1131;

import java.util.ArrayList;

import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;

import com.uc.bs.cyber.CbUtil;
import com.uc.bs.cyber.daemon.Codm_BaseProcess;
import com.uc.bs.cyber.daemon.Codm_interface;
import com.uc.core.MapForm;
import com.uc.core.spring.service.UcContextHolder;

public class Txdm1131_process extends Codm_BaseProcess implements Codm_interface {

	private MapForm dataMap = null;
	private int rcp_cnt = 0, rcp_cancel_cnt = 0, remote_up_cnt = 0;
	
	/*트랜잭션을 위하여 */
	MapForm rcpListRow = null;
	
	/**
	 * 
	 */
	public Txdm1131_process() {
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
		log.info("=[실시간 수납/취소-[" + c_slf_org_nm + "] 자료송신] Start =");
		log.info("=====================================================================");
		try {
				
			setContext(appContext);
			setApp(appContext);

			UcContextHolder.setCustomerType(this.dataSource);
						
			dataMap = new MapForm();  /*초기화*/
			
			log.info("this.c_slf_org ======================= [" + this.c_slf_org + "]");

			dataMap.setMap("SGG_COD",  this.c_slf_org);
			
			int rcpCnt = 0;
			if(dataMap.getMap("SGG_COD").equals("000")){  //고액체납자 추가
				rcpCnt = cyberService.getOneFieldInteger("TXDM1131.SELECT_COUNT_000", dataMap);
				log.info("[실시간 수납/취소(고액체납)-[" + c_slf_org_nm + "]] 건수(" + rcpCnt + ")");
				
			}else{
				rcpCnt = cyberService.getOneFieldInteger("TXDM1131.SELECT_COUNT", dataMap);
				log.info("[실시간 수납/취소-[" + c_slf_org_nm + "]] 건수(" + rcpCnt + ")");
				
			}
			
			if(rcpCnt > 0){
				this.startJob();
			}else{
				
				/***********************************************************************************/
				try{
					MapForm daemonMap = new MapForm();
					daemonMap.setMap("DAEMON"    , "TXDM1131");
					daemonMap.setMap("DAEMON_NM" , "수납(SCYB600)취소전송");
					daemonMap.setMap("SGG_COD"   , c_slf_org);
					daemonMap.setMap("TOTAL_CNT" , rcpCnt);
					daemonMap.setMap("INSERT_CNT", 0);
					daemonMap.setMap("UPDATE_CNT", 0);
					daemonMap.setMap("DELETE_CNT", 0);
					daemonMap.setMap("ERROR_CNT" , 0);
					cyberService.queryForInsert("CODMBASE.InsertDaemonProcessCheck", daemonMap);
				}catch(Exception ex){
					log.debug("지방세 실시간 수납/취소전송 대상이 없습니다. 등록 오류");
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
			
		txdm1131_JobProcess();
	
	}

	private int txdm1131_JobProcess() {
		
		log.info("=====================================================================");
		if(dataMap.getMap("SGG_COD").equals("000")){  //고액체납자 추가
			log.info("=" + this.getClass().getName()+ " txdm1132_JobProcess()[실시간 수납/취소(고액체납)-[" + c_slf_org_nm + "] 자료전송] Start =");
		}else{
			log.info("=" + this.getClass().getName()+ " txdm1131_JobProcess()[실시간 수납/취소-[" + c_slf_org_nm + "] 자료전송] Start =");
		}
		log.info("= govid["+this.govId+"], orgcd["+this.c_slf_org+"]");
		log.info("=====================================================================");
		
		/*초기화*/
		rcp_cancel_cnt = 0;
		rcp_cnt = 0;
		remote_up_cnt = 0;
	
		
		/*전역 초기화*/
		rcpListRow = new MapForm();

		int nul_cnt = 0;
		int cnt = 0;
		int tot_size = 0;
		
		long elapseTime1 = 0;
		long elapseTime2 = 0;
		
		elapseTime1 = System.currentTimeMillis();

		/*---------------------------------------------------------*/
		/*1. 실시간수납 업무 처리.                               */
		/*---------------------------------------------------------*/
		
		try {

			//dataMap.setMap("SGG_COD",  this.c_slf_org);

			ArrayList<MapForm> virAccList = new ArrayList<MapForm>();
			
			/* 실시간 수납/취소 SELECT 쿼리 */
			if(dataMap.getMap("SGG_COD").equals("000")){  //고액체납자 추가
				virAccList =  cyberService.queryForList("TXDM1131.SELECT_LIST_000", dataMap);
			}else{
				virAccList =  cyberService.queryForList("TXDM1131.SELECT_LIST", dataMap);
			}
			
			tot_size = virAccList.size();
			
			if(dataMap.getMap("SGG_COD").equals("000")){  //고액체납자 추가
				log.info("[" + c_slf_org_nm + "]실시간 수납/취소(고액체납) 자료 건수 = [" + tot_size + "]");
			}else{
				log.info("[" + c_slf_org_nm + "]실시간 수납/취소 자료 건수 = [" + tot_size + "]");
			}
			
			if (tot_size  >  0)   {
				
				/* 실시간 수납/취소 자료를 해당 구청에 fetch */
				for (cnt = 0;  cnt < tot_size;  cnt++) {

					rcpListRow =  virAccList.get(cnt);
					
					/*혹시나...because of testing */
					if (rcpListRow == null  ||  rcpListRow.isEmpty() )   {
						nul_cnt++;
						continue;
					}

					/* 납부일자 성정 */
					if(rcpListRow.getMap("PAY_DT") == null || rcpListRow.getMap("PAY_DT") == "" || rcpListRow.getMap("PAY_DT").equals(""))
						
						rcpListRow.setMap("CRE_DT", rcpListRow.getMap("TAX_YY").toString() + rcpListRow.getMap("TAX_MM").toString() + "01");
					
					else rcpListRow.setMap("CRE_DT", rcpListRow.getMap("TAX_DT"));

					/* 가상회수 설정 */
					if(rcpListRow.getMap("ADD_CNT").equals("0")) rcpListRow.setMap("ADD_CNT", "1");

					else rcpListRow.setMap("ADD_CNT", "2");
					
					/* 수납기관 설정 */
					if(rcpListRow.getMap("BANK_COD").equals("99")) rcpListRow.setMap("PAY_SYSTEM", "K");
					
					else if(rcpListRow.getMap("BANK_COD").equals("032")) rcpListRow.setMap("PAY_SYSTEM", "B");
					
					else if(rcpListRow.getMap("BANK_COD").equals("935")) rcpListRow.setMap("PAY_SYSTEM", "J");
					
					else rcpListRow.setMap("PAY_SYSTEM", "R");
					
					rcpListRow.setMap("IH_REGNO", rcpListRow.getMap("PAY_REG_NO"));
					
					rcpListRow.setMap("CHK1", rcpListRow.getMap("OCR_BD").toString().substring(5,1 + 5));      		// chk2                                     
					rcpListRow.setMap("CHK2", rcpListRow.getMap("OCR_BD").toString().substring(30,1 + 30));     	// chk2                                     
					rcpListRow.setMap("CHK3", rcpListRow.getMap("OCR_BD").toString().substring(53,1 + 53));     	// chk3                                     
					rcpListRow.setMap("CHK4", rcpListRow.getMap("OCR_BD").toString().substring(54 + 50, 1 + 54 + 50));  // chk4                                 
					rcpListRow.setMap("FILLER", "7");               													// filler                                                              
					rcpListRow.setMap("CHK5", rcpListRow.getMap("OCR_BD").toString().substring(54 + 53, 1 + 54 + 53)); 	// chk5
					rcpListRow.setMap("DEAL_STATE", "N");
					
					if(rcpListRow.getMap("SNTG").equals("1") || rcpListRow.getMap("SNTG").equals("2")) {

						/*=========== 실시간 수납 ===========*/
						try {
							int TX1201_CNT = cyberService.getOneFieldInteger("TXDM1131.SELECT_TX1201_CNT", rcpListRow);
							int SCYB600_CNT = govService.getOneFieldInteger("TXDM1131.SELECT_SCYB600_CNT", rcpListRow);
							if(SCYB600_CNT<TX1201_CNT){
								govService.queryForUpdate("TXDM1131.TXSV1131_INSERT_RECEIPT", rcpListRow);
								rcp_cnt++;
							}
							
						} catch (Exception e) {
							
							log.error("오류데이터1 = " + rcpListRow.getMaps());
							log.error(e.getMessage());
							e.printStackTrace();							
							throw (RuntimeException) e;
								
						}
					
					} else if (rcpListRow.getMap("SNTG").equals("9")) {
						
						/*=========== 취소 ===========*/
						try {
							
							govService.queryForUpdate("TXDM1131.TXSV1131_INSERT_RECEIPT_CANCEL", rcpListRow);
							
							rcp_cancel_cnt++;
							
						} catch (Exception e) {
							
							log.error("오류데이터2 = " + rcpListRow.getMaps());
							log.error(e.getMessage());
							e.printStackTrace();							
							throw (RuntimeException) e;
								
						}
						
					}
					
					
					/* 전송완료 플래그 설정 */
					rcpListRow.setMap("TRTG", "1");
					
					/*=========== UPDATE ===========*/
					try {						
	                /* TX1201_TB 전송 완료 업데이트 */
						remote_up_cnt += cyberService.queryForUpdate("TXDM1131.TXSV1131_UPDATE_STATE", rcpListRow);
						
					} catch (Exception e) {
						
						log.error("오류데이터3 = " + rcpListRow.getMaps());
						log.error(e.getMessage());
						e.printStackTrace();							
						throw (RuntimeException) e;
							
					}

				}
				
				if(dataMap.getMap("SGG_COD").equals("000")){  //고액체납자 추가
					log.info("[" + c_slf_org_nm + "]실시간 수납/취소(고액체납) 건수 [" + cnt + "] 실시간수납 [" + rcp_cnt +"] 취소건수 [" + rcp_cancel_cnt + "]");
				}else{
					log.info("[" + c_slf_org_nm + "]실시간 수납/취소 건수 [" + cnt + "] 실시간수납 [" + rcp_cnt +"] 취소건수 [" + rcp_cancel_cnt + "]");
				}
				
				log.info("처리건수 ["+remote_up_cnt+"], 미처리 부과건수 [" + nul_cnt + "]");
				
				
				/***********************************************************************************/
				try{
					MapForm daemonMap = new MapForm();
					daemonMap.setMap("DAEMON"    , "TXDM1131");
					daemonMap.setMap("DAEMON_NM" , "수납(SCYB600)취소전송");
					daemonMap.setMap("SGG_COD"   , c_slf_org);
					daemonMap.setMap("TOTAL_CNT" , cnt);
					daemonMap.setMap("INSERT_CNT", rcp_cnt);
					daemonMap.setMap("UPDATE_CNT", rcp_cancel_cnt);
					daemonMap.setMap("DELETE_CNT", 0);
					daemonMap.setMap("ERROR_CNT" , 0);
					cyberService.queryForInsert("CODMBASE.InsertDaemonProcessCheck", daemonMap);
				}catch(Exception ex){
					log.debug("지방세 실시간 수납/취소전송 대상이 없습니다. 등록 오류");
				}				
				/***********************************************************************************/
				
			}

			elapseTime2 = System.currentTimeMillis();
			
			if(dataMap.getMap("SGG_COD").equals("000")){  //고액체납자 추가
				log.info("[" + c_slf_org_nm + "]실시간 수납/취소(고액체납) 전송 시간("+tot_size+") : " + CbUtil.formatTime(elapseTime2 - elapseTime1));
			}else{
				log.info("[" + c_slf_org_nm + "]실시간 수납/취소 전송 시간("+tot_size+") : " + CbUtil.formatTime(elapseTime2 - elapseTime1));
			}
					
		} catch (Exception e) {
			
		    log.error("오류 데이터4 = " + rcpListRow.getMaps());
		    log.error(e.getMessage());
			throw (RuntimeException) e;
			
		}
		
		return tot_size;
	}
	
}
