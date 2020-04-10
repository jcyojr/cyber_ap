/**
 *  주시스템명 : 부산시청 사이버 지방세청
 *  업  무  명 : 연계
 *  기  능  명 : 표준세외수입 가상계좌 체번대상 연계
 *  클래스  ID : Txdm2416_process : 실 처리로직을 작성하는 클래스
 *  변경  이력 :
 * ------------------------------------------------------------------------
 *  작성자      	소속  		    일자            Tag           내용
 * ------------------------------------------------------------------------
 *  최유환       유채널(주)      2015.02.11         %01%         최초작성
 */
package com.uc.bs.cyber.daemon.txdm2416;

import java.util.ArrayList;

import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.dao.DuplicateKeyException;

import com.uc.bs.cyber.daemon.Codm_BaseProcess;
import com.uc.bs.cyber.daemon.Codm_interface;
import com.uc.core.MapForm;
import com.uc.core.spring.service.UcContextHolder;


public class Txdm2416_process extends Codm_BaseProcess implements Codm_interface {

	private MapForm dataMap = null;
	private int insert_cnt = 0;
	private int update_cnt = 0;
	private int err_cnt = 0;
	
	
	/**
	 * 
	 */
	public Txdm2416_process() {

		super();
		
		log = LogFactory.getLog(this.getClass());
		
		/**
		 * 8 분마다 돈다
		 */
		loopTerm = 60 * 8;
	}

	/*Context 주입용*/
	public void setApp(ApplicationContext context) {
		this.context = context;
	}
	

	public void runProcess() throws Exception {

		/*트랜잭션 업무 구현*/
		mainTransProcess();

	}

	/*트랜잭션을 실행하기 위한 함수.*/
	private void mainTransProcess(){
		
		log.info("=====================================================================");
		log.info("=[표준세외수입-[" + c_slf_org_nm + "] 가상계좌연계] Start =");
		log.info("=====================================================================");
		
		try {
				
			setContext(appContext);
			setApp(appContext);

			UcContextHolder.setCustomerType(this.dataSource);
							
			log.info("this.c_slf_org ======================= [" + this.c_slf_org + "]");


			dataMap = new MapForm();
			dataMap.setMap("SGG_COD"      , this.c_slf_org);  /*구청코드 맵핑*/
			int SEQ = 0;

			try{
				SEQ = govService.getOneFieldInteger("TXDM2416.getVirAccNoLinkCount", dataMap);
				log.debug("SEQ :: " + SEQ);
			}catch (Exception sub_e){
				log.error("SEQ, CNT 오류");
				SEQ = -1;
				sub_e.printStackTrace();
			}						

			if(SEQ > 0){				
				this.startJob();
			}else if(SEQ == 0){
				log.info("["+ c_slf_org_nm + "] 의 가상계좌 체번 대상이 없습니다.");
				
				/***********************************************************************************/
				try{
					MapForm daemonMap = new MapForm();
					daemonMap.setMap("DAEMON"    , "TXDM2416");
					daemonMap.setMap("DAEMON_NM" , "세외수입 가상계좌 채번연계");
					daemonMap.setMap("SGG_COD"   , c_slf_org);
					daemonMap.setMap("TOTAL_CNT" , SEQ);
					daemonMap.setMap("INSERT_CNT", 0);
					daemonMap.setMap("UPDATE_CNT", 0);
					daemonMap.setMap("DELETE_CNT", 0);
					daemonMap.setMap("ERROR_CNT" , 0);
					cyberService.queryForInsert("CODMBASE.InsertDaemonProcessCheck", daemonMap);
				}catch(Exception ex){
					log.debug("대몬 가상계좌 체번 대상이 없습니다. 등록 오류");
				}				
				/***********************************************************************************/
				
			}else{
				log.info("["+ c_slf_org_nm + "] 의 연계데이터 조회 오류");
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
			
		/*---------------------------------------------------------*/
		/*1. 부가연계자료 업무 처리.                               */
		/*---------------------------------------------------------*/
		txdm2416_JobProcess();
	
	}
	
	
    /*표준세외수입...연계*/
	private int txdm2416_JobProcess() {
		
		log.info("=====================================================================");
		log.info("=" + this.getClass().getName()+ " txdm2416_JobProcess()[표준세외수입-[" + c_slf_org_nm + "] 가상계좌연계] Start =");
		log.info("= govid["+this.govId+"], orgcd["+this.c_slf_org+"]");
		log.info("=====================================================================");
		
		/*초기화*/
		
		insert_cnt = 0;
		update_cnt = 0;
		err_cnt = 0;
		int dup_cnt = 0;
	
	    ArrayList<MapForm> list  = new ArrayList<MapForm>();

	    MapForm inMap = null;
		/*트랜잭션을 포함하는 실업무는 여기서 구현한다...*/
		/*실업무를 구현합니다.*/
		

		try {

			try{
		
				list = govService.queryForList("TXDM2416.getVirAccNoDataList", dataMap);
	
			}catch (Exception e){
				e.printStackTrace();
				return 0;				
			}						

			if(list.size() < 1){
				log.info("가상계좌 체번 대상이 없습니다.");
				return 0;
			}
		    
			for(int i = 0; i < list.size(); i++){
				inMap = new MapForm();
				inMap = list.get(i);
				
				//log.debug("inMap  :: " + inMap);
				//중복이면 패스
				try{
					
					/*==========가상계좌 체번 테이블 입력 ===========*/
					cyberService.queryForInsert("TXDM2416.insertTx2114VirAccNoData", inMap);
					insert_cnt++;
				}catch (Exception e){

					/*중복이 발생하거나 제약조건에 어긋나는 경우체크*/
					if(e instanceof DuplicateKeyException){
						//가상계좌 체번 중인 자료
						//log.debug("중복건 패스 :: " + inMap.getMap("TAX_NO"));
						//중복건중 오류건인 경우 OCR밴드 비교하여 업데이트 처리 추가
						String ocr = null;
						try{
						    ocr = cyberService.getOneFieldString("TXDM2416.getDuplicatekeyOcrBd", inMap);
						}catch(Exception ex){
							log.debug("ocr 체크 에러");
							ex.printStackTrace();
							throw (RuntimeException) ex;
						}
						//채번중인 자료
						if(ocr == null || ocr.equals("")){
							dup_cnt++;
							continue;
						}
						//오류자료
						else if(ocr.equals(inMap.getMap("OCR_BD"))){
							err_cnt++;
							continue;
						}
						//오류변경건
						else{
						    cyberService.queryForUpdate("TXDM2416.updateOCRBDandETC", inMap);
							update_cnt++;
							continue;
						}
						
					}else{
						log.error("오류데이터 = " + inMap.getMaps());
						e.printStackTrace();
						throw (RuntimeException) e;
						
					}
					
				}
				
			}
						
			log.info("[" + c_slf_org_nm + "]세외수입 가상계좌연계 ("+list.size()+") 중 등록 : " + insert_cnt + "건  중복 : " + dup_cnt + "건  변경 : " + update_cnt + "건  계속에러 : " + err_cnt + "건");
			
			/***********************************************************************************/
			try{
				MapForm daemonMap = new MapForm();
				daemonMap.setMap("DAEMON"    , "TXDM2416");
				daemonMap.setMap("DAEMON_NM" , "세외수입 가상계좌 채번연계");
				daemonMap.setMap("SGG_COD"   , c_slf_org);
				daemonMap.setMap("TOTAL_CNT" , list.size());
				daemonMap.setMap("INSERT_CNT", insert_cnt);
				daemonMap.setMap("UPDATE_CNT", dup_cnt);
				daemonMap.setMap("DELETE_CNT", 0);
				daemonMap.setMap("ERROR_CNT" , err_cnt);
				cyberService.queryForInsert("CODMBASE.InsertDaemonProcessCheck", daemonMap);
			}catch(Exception ex){
				log.debug("대몬 처리로그 등록 오류");
			}				
			/***********************************************************************************/
			
		} catch (Exception e) {
			e.printStackTrace();
			throw (RuntimeException) e;			
		}
		
		return 0;
	}

}
