/**
 *  주시스템명 : 부산시청 사이버 지방세청
 *  업  무  명 : 연계
 *  기  능  명 : 표준세외수입 가상계좌 체번 전송
 *  클래스  ID : Txdm2415_process : 실 처리로직을 작성하는 클래스
 *  변경  이력 :
 * ------------------------------------------------------------------------
 *  작성자      	소속  		    일자            Tag           내용
 * ------------------------------------------------------------------------
 *  최유환       유채널          2015.02.10      %01%         최초작성
 */
package com.uc.bs.cyber.daemon.txdm2417;

import java.util.ArrayList;

import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.dao.DuplicateKeyException;

import com.uc.bs.cyber.CbUtil;
import com.uc.bs.cyber.daemon.Codm_BaseProcess;
import com.uc.bs.cyber.daemon.Codm_interface;
import com.uc.core.MapForm;
import com.uc.core.spring.service.UcContextHolder;


public class Txdm2417_process extends Codm_BaseProcess implements Codm_interface {

	private MapForm dataMap = null;
	private ArrayList<MapForm> list = null;
	private int insert_cnt = 0, update_cnt=0;
	
	
	/**
	 * 
	 */
	public Txdm2417_process() {

		super();
		
		log = LogFactory.getLog(this.getClass());
		
		loopTerm = 250;
	}

	/*Context 주입용*/
	public void setApp(ApplicationContext context) {
		this.context = context;
	}
	
	/* process starting */
	public void runProcess() throws Exception {

		//log.info("=====================================================================");
		//log.info("=" + this.getClass().getName()+ " runProcess() ==");
		//log.info("=====================================================================");

		/*트랜잭션 업무 구현*/
		mainTransProcess();

	}

	
	/*트랜잭션을 실행하기 위한 함수.*/
	private void mainTransProcess(){
		
		log.info("=====================================================================");
		log.info("=[표준세외수입-[" + c_slf_org_nm + "] 가상계좌전송] Start =");
		log.info("=====================================================================");
		
		try {
				
			setContext(appContext);
			setApp(appContext);

			UcContextHolder.setCustomerType(this.dataSource);
							
			log.info("this.c_slf_org ======================= [" + this.c_slf_org + "]");			

			dataMap = new MapForm();
			dataMap.setMap("SGG_COD", this.c_slf_org);
			int SEQ = 0;
		    //long CNT = 0L;
			try{
				SEQ = cyberService.getOneFieldInteger("TXDM2417.getCyberVirAccNoCount", dataMap);
			}catch (Exception sub_e){
				log.error("SEQ, CNT 오류");
				sub_e.printStackTrace();
			}						

			if(SEQ > 0){				
				this.startJob();
			}else{
				log.info("[" + c_slf_org_nm + "] 전송할 채번된 가상계좌가 없습니다.");
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
		txdm2417_JobProcess();
	
	}
	
	
    /*표준세외수입...연계*/
	private int txdm2417_JobProcess() {
		
		log.info("=====================================================================");
		log.info("=" + this.getClass().getName()+ " txdm2417_JobProcess()[표준세외수입-[" + c_slf_org_nm + "] 가상계좌 전송] Start =");
		log.info("= govid["+this.govId+"], orgcd["+this.c_slf_org+"]");
		log.info("=====================================================================");
		
		/*초기화*/
		
		insert_cnt = 0;
		update_cnt = 0;
		
		/*전역 초기화*/
	    list = new ArrayList<MapForm>();

	    MapForm inMap = null;
		/*트랜잭션을 포함하는 실업무는 여기서 구현한다...*/
		/*실업무를 구현합니다.*/
		
		long elapseTime1 = 0;
		long elapseTime2 = 0;
		
		elapseTime1 = System.currentTimeMillis();

		try {

			try{
				list =  cyberService.queryForList("TXDM2417.getCyberVirAccNoTranDataList", dataMap);
			}catch (Exception e){
				e.printStackTrace();
				return 0;				
			}						

			if(list.size() < 1){
				log.debug("가상계좌 전송 대상이 없습니다.");
				return 0;
			}
		    
			for(int i = 0; i < list.size(); i++){
				inMap = new MapForm();
				inMap = list.get(i);
				
				//중복이면 패스
				try{
					/*==========가상계좌 연계 테이블 입력 ===========*/
					govService.queryForInsert("TXDM2417.insertSeoiVirAccNoData", inMap);
					insert_cnt++;
				}catch (Exception e){

					/*중복이 발생하거나 제약조건에 어긋나는 경우체크*/
					if(e instanceof DuplicateKeyException){
						//가상계좌 체번 중인 자료
						govService.queryForUpdate("TXDM2417.updateSeoiVirAccnoData", inMap);
						update_cnt++;
					}else{
						log.error("오류데이터 = " + inMap.getMaps());
						e.printStackTrace();
						throw (RuntimeException) e;
						
					}
						
				}
				
				try{
					/*==========가상계좌 연계 완료 ===========*/
					cyberService.queryForUpdate("TXDM2417.updateVirAccNoTransEnd", inMap);					
				}catch (Exception e){
					log.error("오류데이터 = " + inMap.getMaps());
					e.printStackTrace();
					throw (RuntimeException) e;					
				}
							
			}
			
			elapseTime2 = System.currentTimeMillis();
			log.info("전체 :: " + list.size() + " 건  등록 :: " + insert_cnt + " 건   수정 :: " + update_cnt + " 건 처리");
			log.info("[" + c_slf_org_nm + "]세외수입 가상계좌 전송 시간("+list.size()+") : " + CbUtil.formatTime(elapseTime2 - elapseTime1));
					
		} catch (Exception e) {
			e.printStackTrace();
			throw (RuntimeException) e;			
		}
		
		return 0;
	}

}
