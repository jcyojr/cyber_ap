/**
 *  주시스템명 : 부산시청 사이버 지방세청
 *  업  무  명 : 연계
 *  기  능  명 : 수기입력용납세자정보수신, 수기입력용 사업장정보연계, 수기 위택스종소세정보연계
 *  클래스  ID : Txdm1175_process : 실 처리로직을 작성하는 클래스
 *  변경  이력 :
 * ------------------------------------------------------------------------
 *  작성자      	소속  		    일자            Tag           내용
 * ------------------------------------------------------------------------
 *  천혜정       유채널(주)      2012.02.10         %01%         최초작성
 */
package com.uc.bs.cyber.daemon.txdm1175;

import java.util.ArrayList;

import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.dao.DuplicateKeyException;

import com.uc.bs.cyber.CbUtil;
import com.uc.bs.cyber.daemon.Codm_BaseProcess;
import com.uc.bs.cyber.daemon.Codm_interface;
import com.uc.core.MapForm;
import com.uc.core.spring.service.UcContextHolder;

/**
 * @author Administrator
 */
public class Txdm1175_process extends Codm_BaseProcess implements Codm_interface {

	private MapForm dataMap            = null;
	private int insert_cnt = 0, update_cnt = 0;
	
	/*트랜잭션을 위하여 */
	MapForm gblNidLevyRows    = null;
	
	/**
	 * 
	 */
	public Txdm1175_process() {

		super();
		
		log = LogFactory.getLog(this.getClass());
		
		/**
		 * 30 분마다 돈다
		 */
		loopTerm = 60 * 30;
	}

	/*Context 주입용*/
	public void setApp(ApplicationContext context) {
		this.context = context;
	}
	
	/* process starting */
	public void runProcess() throws Exception {

		/*트랜잭션 업무 구현*/
		mainTransProcess();

	}

	
	/*트랜잭션을 실행하기 위한 함수.*/
	private void mainTransProcess(){
		
		log.info("=====================================================================");
		log.info("=[수기입력용납세자정보-구청 자료연계] Start =" + this.c_slf_org);
		log.info("=====================================================================");
		

		try {
				
			setContext(appContext);
			setApp(appContext);

			UcContextHolder.setCustomerType(this.dataSource);
						
			dataMap = new MapForm();  /*초기화*/
			
			//수기입력용납세자정보
			int page1 = govService.getOneFieldInteger("txdm1175.txdm1171_select_count_page", dataMap);
			//수기입력용사업장자정보	
			int page2 = govService.getOneFieldInteger("txdm1175.txdm1172_select_count_page", dataMap);
			//수기 위택스종소세정보 구축
			int page4 = govService.getOneFieldInteger("txdm1175.txdm1174_select_count_page", dataMap);
				
											
			if(page1 > 0){
				dataMap.setMap("JOB_GBN", "1");			  
				this.startJob();             /*멀티 트랜잭션 호출*/
					
			}else{
				log.info("[수기입력용납세자정보 TX1171]-구청 "+ this.c_slf_org +" 자료가 없습니다.");
				
				/***********************************************************************************/
				try{
					MapForm daemonMap = new MapForm();
					daemonMap.setMap("DAEMON"    , "TXDM1175");
					daemonMap.setMap("DAEMON_NM" , "수기입력용(V_TTPRCON1)정보");
					daemonMap.setMap("SGG_COD"   , c_slf_org);
					daemonMap.setMap("TOTAL_CNT" , page1);
					daemonMap.setMap("INSERT_CNT", 0);
					daemonMap.setMap("UPDATE_CNT", 0);
					daemonMap.setMap("DELETE_CNT", 0);
					daemonMap.setMap("ERROR_CNT" , 0);
					cyberService.queryForInsert("CODMBASE.InsertDaemonProcessCheck", daemonMap);
				}catch(Exception ex){
					log.debug("지방세 수납요약정보 건수가 없습니다. 등록 오류");
				}				
				/***********************************************************************************/
				
			}
			
			if(page2 > 0){
				dataMap.setMap("JOB_GBN", "2");			  
				this.startJob();             /*멀티 트랜잭션 호출*/
					
			}else{
				log.info("[수기입력용사업장자정보 TX1172] -구청 "+ this.c_slf_org +" 자료가 없습니다.");
			}
			
			if(page4 > 0){
				dataMap.setMap("JOB_GBN", "4");			  
				this.startJob();             /*멀티 트랜잭션 호출*/
					
			}else{
				log.info("[수기 위택스종소세정보 구축 TX1174] -구청 "+ this.c_slf_org +" 자료가 없습니다.");
			}
			
				
		
		} catch (Exception e) {
			e.printStackTrace();
		}			
		
	}
	
	/*현 사용할 일이 없넹...*/
	public void setDatasrc(String datasrc) {
		this.dataSource = datasrc;
	}
	
	/*트랜잭션 구성*/
	public void transactionJob() {
			
		if(dataMap.getMap("JOB_GBN").equals("1")){
			txdm1171_JobProcess();
		}else if(dataMap.getMap("JOB_GBN").equals("2")){
			txdm1172_JobProcess();
		}else if(dataMap.getMap("JOB_GBN").equals("4")){
			txdm1174_JobProcess();
		}else{
			log.debug("JOB_GBN 오류 입니다. " + dataMap.getMap("JOB_GBN"));
		}
			
	}
    	
	/*수기입력용납세자정보수신...연계(구청)*/
	private int txdm1171_JobProcess() {
			
		log.info("=====================================================================");
		log.info("=" + this.getClass().getName()+ " txdm1175_JobProcess()[수기입력용납세자정보-구청 자료연계] Start =");
		log.info("= govid["+this.govId+"], orgcd["+this.c_slf_org+"]");
		log.info("=====================================================================");
			
		/*초기화*/			
		insert_cnt = 0;
		update_cnt = 0; 
			
		/*전역 초기화*/
		gblNidLevyRows = new MapForm();

		/*트랜잭션을 포함하는 실업무는 여기서 구현한다...*/
		/*실업무를 구현합니다.*/
			
		int rec_cnt = 0;			
		int tot_size = 0;
			
		long elapseTime1 = 0;
		long elapseTime2 = 0;
			
		elapseTime1 = System.currentTimeMillis();

		/*---------------------------------------------------------*/
		/*1. 부가연계자료 업무 처리.                               */
		/*---------------------------------------------------------*/
			
		try {
            
			dataMap.setMap("SGG_COD",  this.c_slf_org);


			/*연계테이블 부과자료 SELECT 쿼리(TAX)*/
			ArrayList<MapForm> alNidLevyList =  govService.queryForList("txdm1175.txdm1171_select_list", dataMap);
				
			tot_size = alNidLevyList.size();
				
			   log.info("수기입력용납세자정보 연계자료 건수 = [" + tot_size + "]");
				
			if (tot_size  >  0)   {
			    		
				/*전자납부자료 1건씩 fetch 처리 */
				for ( rec_cnt = 0;  rec_cnt < tot_size;  rec_cnt++)   {

					gblNidLevyRows =  alNidLevyList.get(rec_cnt);
						
					/*혹시나...because of testing */
					if(gblNidLevyRows == null || gblNidLevyRows.isEmpty() || gblNidLevyRows.getMap("REG_NO_CHECK").equals("99999")){
						continue;
					}
						
					/*데이터가 없으면 인서트 잇으면 업데이트*/ 
					gblNidLevyRows.setMap("SGG_COD", dataMap.getMap("SGG_COD"));
						
					try{
						cyberService.queryForInsert("txdm1175.txdm1171_insert_tx1601_tb", gblNidLevyRows);
						insert_cnt++;

					}catch(Exception e){
							
						/*중복이 발생하거나 제약조건에 어긋나는 경우체크 : sgg_cod , reg_no 가 중복*/
						if(e instanceof DuplicateKeyException){	// 기본키가 중복되서 예외처리된경우 
									
							try{
								cyberService.queryForInsert("txdm1175.txdm1171_update_tx1601_tb", gblNidLevyRows);
								update_cnt++;
										
							}catch(Exception be){

								be.printStackTrace();
							}
							
						}else{
							log.error("오류데이터 = " + gblNidLevyRows.getMaps());
							e.printStackTrace();							
							throw (RuntimeException) e;
						}										
					}
				}
			}
					
			log.info("수기입력용납세자정보수신 TX1171 자료연계 건수 [" + rec_cnt + "] 등록건수 [" + insert_cnt +"] 수정건수 [" + update_cnt + "]");
			
			/***********************************************************************************/
			try{
				MapForm daemonMap = new MapForm();
				daemonMap.setMap("DAEMON"    , "TXDM1175");
				daemonMap.setMap("DAEMON_NM" , "수기입력용(V_TTPRCON1)정보");
				daemonMap.setMap("SGG_COD"   , c_slf_org);
				daemonMap.setMap("TOTAL_CNT" , rec_cnt);
				daemonMap.setMap("INSERT_CNT", insert_cnt);
				daemonMap.setMap("UPDATE_CNT", update_cnt);
				daemonMap.setMap("DELETE_CNT", 0);
				daemonMap.setMap("ERROR_CNT" , 0);
				cyberService.queryForInsert("CODMBASE.InsertDaemonProcessCheck", daemonMap);
			}catch(Exception ex){
				log.debug("지방세 수납요약정보 건수 로그 등록 오류");
			}				
			/***********************************************************************************/

			elapseTime2 = System.currentTimeMillis();
				
			log.info("수기입력용납세자정보수신 자료연계 시간("+tot_size+") : " + CbUtil.formatTime(elapseTime2 - elapseTime1));
						
		}catch (Exception e){
			log.error("오류 데이터 = " + gblNidLevyRows.getMaps());
			throw (RuntimeException) e;
		}
			
		return tot_size;
	}
		
		
	/*수기입력용사업자정보수신...연계(구청)*/
	private int txdm1172_JobProcess() {
			
		log.info("=====================================================================");
		log.info("=" + this.getClass().getName()+ " txdm1175_JobProcess()[수기입력용사업장자정보-구청 자료연계] Start =");
		log.info("= govid["+this.govId+"], orgcd["+this.c_slf_org+"]");
		log.info("=====================================================================");
			
		/*초기화*/
			
		insert_cnt = 0;
		update_cnt = 0; 
			
		/*전역 초기화*/
		gblNidLevyRows = new MapForm();

		/*트랜잭션을 포함하는 실업무는 여기서 구현한다...*/
		/*실업무를 구현합니다.*/
			
		int rec_cnt = 0;			
		int tot_size = 0;
			
		long elapseTime1 = 0;
		long elapseTime2 = 0;
			
		elapseTime1 = System.currentTimeMillis();

		/*---------------------------------------------------------*/
		/*1. 부가연계자료 업무 처리.                               */
		/*---------------------------------------------------------*/
			
		try {

			dataMap.setMap("SGG_COD",  this.c_slf_org);

			/*연계테이블 부과자료 SELECT 쿼리(TAX)*/
			ArrayList<MapForm> alNidLevyList =  govService.queryForList("txdm1175.txdm1172_select_list", dataMap);
				
			tot_size = alNidLevyList.size();
				
			log.info("수기입력용사업장자정보 연계자료 건수 = [" + tot_size + "]");
				
		    if(tot_size > 0){
					
				/*전자납부자료 1건씩 fetch 처리 */
				for(rec_cnt = 0; rec_cnt < tot_size; rec_cnt++){

					gblNidLevyRows =  alNidLevyList.get(rec_cnt);
						
					/*혹시나...because of testing */
					if(gblNidLevyRows == null || gblNidLevyRows.isEmpty()){
						continue;
					}
						
					/*데이터가 없으면 인서트 잇으면 업데이트*/ 
					gblNidLevyRows.setMap("SGG_COD", dataMap.getMap("SGG_COD"));
						
					try{
						cyberService.queryForInsert("txdm1175.txdm1172_insert_tx1602_tb", gblNidLevyRows);
						insert_cnt++;

					}catch(Exception e){
							
						/*중복이 발생하거나 제약조건에 어긋나는 경우체크 : sgg_cod , reg_no 가 중복*/
						if(e instanceof DuplicateKeyException){	// 기본키가 중복되서 예외처리된경우 
									
							try{
								cyberService.queryForInsert("txdm1175.txdm1172_update_tx1602_tb", gblNidLevyRows);
								update_cnt++;
										
							}catch(Exception be){
								be.printStackTrace();
							}
							
						}else{
							log.error("오류데이터 = " + gblNidLevyRows.getMaps());
							e.printStackTrace();							
							throw (RuntimeException) e;
						}					
					}
				}
			}
					
			log.info("수기입력용사업장자정보수신 TX1172 자료연계 건수 [" + rec_cnt + "] 등록건수 [" + insert_cnt +"] 수정건수 [" + update_cnt + "]");
			elapseTime2 = System.currentTimeMillis();				
			log.info("시청세외수입 부과자료연계 시간("+tot_size+") : " + CbUtil.formatTime(elapseTime2 - elapseTime1));
						
		}catch(Exception e){
			log.error("오류 데이터 = " + gblNidLevyRows.getMaps());
			throw (RuntimeException) e;
		}
			
		return tot_size;
	}
		
	
	/*수기입력용사업자정보수신...연계(구청)*/
	private int txdm1174_JobProcess() {
			
		log.info("=====================================================================");
		log.info("=" + this.getClass().getName()+ " txdm1175_JobProcess()[수기 위택스종소세정보 구축-구청 자료연계] Start =");
		log.info("= govid["+this.govId+"], orgcd["+this.c_slf_org+"]");
		log.info("=====================================================================");
			
		/*초기화*/
			
		insert_cnt = 0;
		update_cnt = 0; 
			
		/*전역 초기화*/
		gblNidLevyRows = new MapForm();

		/*트랜잭션을 포함하는 실업무는 여기서 구현한다...*/
		/*실업무를 구현합니다.*/
			
		int rec_cnt = 0;
			
		int tot_size = 0;
			
		long elapseTime1 = 0;
		long elapseTime2 = 0;
			
		elapseTime1 = System.currentTimeMillis();

		/*---------------------------------------------------------*/
		/*1. 부가연계자료 업무 처리.                               */
		/*---------------------------------------------------------*/
			
		try{

			dataMap.setMap("SGG_COD",  this.c_slf_org);

			/*연계테이블 부과자료 SELECT 쿼리(TAX)*/
			ArrayList<MapForm> alNidLevyList =  govService.queryForList("txdm1175.txdm1174_select_list", dataMap);
				
			tot_size = alNidLevyList.size();
				
			log.info("수기 위택스종소세정보 구축 연계자료 건수 = [" + tot_size + "]");
				
			if(tot_size > 0){
					
				/*전자납부자료 1건씩 fetch 처리 */
				for(rec_cnt = 0; rec_cnt < tot_size; rec_cnt++){

					gblNidLevyRows =  alNidLevyList.get(rec_cnt);
						
					/*혹시나...because of testing */
					if(gblNidLevyRows == null || gblNidLevyRows.isEmpty()){
							continue;
					}						
						
					try{
						cyberService.queryForInsert("txdm1175.txdm1174_insert_tx1604_tb", gblNidLevyRows);
						insert_cnt++;

					}catch(Exception e){
							
						/*중복이 발생하거나 제약조건에 어긋나는 경우체크 : sgg_cod , reg_no 가 중복*/
						if(e instanceof DuplicateKeyException){	// 기본키가 중복되서 예외처리된경우 
									
							try{
								cyberService.queryForInsert("txdm1175.txdm1174_update_tx1604_tb", gblNidLevyRows);
								update_cnt++;
										
							}catch(Exception be){
								be.printStackTrace();
							}
						
						}else{
							log.error("오류데이터 = " + gblNidLevyRows.getMaps());
							e.printStackTrace();							
							throw (RuntimeException) e;
						}
					}
				}
			}
					
			log.info("수기위택스종소세정보 TX1174 자료연계 건수 [" + rec_cnt + "] 등록건수 [" + insert_cnt +"] 수정건수 [" + update_cnt + "]");
		    elapseTime2 = System.currentTimeMillis();		
			log.info("위택스종소세정보 부과자료연계 시간("+tot_size+") : " + CbUtil.formatTime(elapseTime2 - elapseTime1));
						
		}catch(Exception e){
			log.error("오류 데이터 = " + gblNidLevyRows.getMaps());
			throw (RuntimeException) e;
		}
			
		return tot_size;
	}
		

}
