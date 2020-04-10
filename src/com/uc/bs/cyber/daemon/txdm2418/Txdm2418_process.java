/**
 *  주시스템명 : 부산시청 사이버 지방세청
 *  업  무  명 : 연계
 *  기  능  명 : 표준세외수입 가상계좌 체번 전송
 *  클래스  ID : Txdm2418_process : 실 처리로직을 작성하는 클래스
 *  변경  이력 :
 * ------------------------------------------------------------------------
 *  작성자      	소속  		    일자            Tag           내용
 * ------------------------------------------------------------------------
 *  최유환       유채널          2015.02.10      %01%         최초작성
 */
package com.uc.bs.cyber.daemon.txdm2418;

import java.util.ArrayList;

import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;

import com.uc.bs.cyber.CbUtil;
import com.uc.bs.cyber.daemon.Codm_BaseProcess;
import com.uc.bs.cyber.daemon.Codm_interface;
import com.uc.core.MapForm;
import com.uc.core.spring.service.UcContextHolder;


public class Txdm2418_process extends Codm_BaseProcess implements Codm_interface {

	private MapForm dataMap = null;
	private ArrayList<MapForm> list = null;
	
	
	/**
	 * 
	 */
	public Txdm2418_process() {

		super();
		
		log = LogFactory.getLog(this.getClass());
		
		/**
		 * 5 분마다 돈다
		 */
		loopTerm = 310;
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
		log.info("=[표준세외수입(TX2112_TB)- 가상계좌 등록] Start =");
		log.info("=====================================================================");
		
		try {
				
			setContext(appContext);
			setApp(appContext);

			UcContextHolder.setCustomerType(this.dataSource);
							
			log.info("this.c_slf_org ======================= [" + this.c_slf_org + "]");


			int SEQ = 0;
		    //long CNT = 0L;
			try{
				SEQ = cyberService.getOneFieldInteger("TXDM2418.getTx2112VirAccNoUpdateCount", null);
			}catch (Exception sub_e){
				log.error("SEQ, CNT 오류");
				sub_e.printStackTrace();
			}						

			if(SEQ > 0){				
				this.startJob();
			}else{
				log.info("표준세외수입(TX2112_TB)- 가상계좌 등록 대기 건수가 없습니다.");
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
		txdm2418_JobProcess();
	
	}
	
	
    /*표준세외수입...연계*/
	private int txdm2418_JobProcess() {
		
		log.info("=====================================================================");
		log.info("=" + this.getClass().getName()+ " txdm2418_JobProcess()[표준세외수입(TX2112_TB)- 가상계좌 등록] Start =");
		log.info("= govid["+this.govId+"], orgcd["+this.c_slf_org+"]");
		log.info("=====================================================================");
		
		/*초기화*/
		int insert_cnt = 0;
		int wait_cnt = 0;
		
		/*전역 초기화*/
	    dataMap = new MapForm();
	    list = new ArrayList<MapForm>();

	    MapForm inMap = null;
		/*트랜잭션을 포함하는 실업무는 여기서 구현한다...*/
		/*실업무를 구현합니다.*/
		
		long elapseTime1 = 0;
		long elapseTime2 = 0;
		
		elapseTime1 = System.currentTimeMillis();

		try {

			try{
				list =  cyberService.queryForList("TXDM2418.getTx2112VirAccNoUpdateList", dataMap);
			}catch (Exception e){
				e.printStackTrace();
				log.error("Exception getTx2112VirAccNoUpdateList");
				throw (RuntimeException) e;			
			}						

			if(list.size() < 1){
				log.debug("가상계좌 등록(TX2112) 대기 대상이 없습니다.");
				return 0;
			}
		    
			for(int i = 0; i < list.size(); i++){
				inMap = new MapForm();
				inMap = list.get(i);
				String proc_cls = null;
				try{
					proc_cls = cyberService.getOneFieldString("TXDM2418.selectTx2112BugwaDataBonjanBigo", inMap);
				}catch(Exception ex){
					log.error("Exception ex 금액 비교 에러");
					ex.printStackTrace();
					throw (RuntimeException) ex;
				}
				
				if(proc_cls == null || proc_cls.equals("")){
					wait_cnt++;
					continue;
				}else{
					inMap.setMap("PROC_CLS", proc_cls);
				}
				
				try{
					/*==========가상계좌 연계 테이블 입력 ===========*/
					if(cyberService.queryForUpdate("TXDM2418.updateTx2112VirAccNo", inMap) > 0){
						cyberService.queryForUpdate("TXDM2418.updateTx2114End", inMap);
						insert_cnt++;
					}else{
						wait_cnt++;
					}

				}catch (Exception e){
					log.error("오류데이터 = " + inMap.getMaps());
					e.printStackTrace();
					throw (RuntimeException) e;
				}
												
			}
			
			elapseTime2 = System.currentTimeMillis();
			
			log.info("TX2112_TB 가상계좌 등록 시간("+list.size()+") : " + CbUtil.formatTime(elapseTime2 - elapseTime1) + " 변경완료 : " + insert_cnt + "건, 대기 : " + wait_cnt + "건");
					
		} catch (Exception e) {
			e.printStackTrace();
			throw (RuntimeException) e;			
		}
		
		return 0;
	}

}
