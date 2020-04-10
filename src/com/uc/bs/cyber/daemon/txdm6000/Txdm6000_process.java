/**
 *  주시스템명 : 부산시청 사이버 지방세청
 *  업  무  명 : 연계/웹서비스 전자신고 전송
 *  기  능  명 : 표준지방세 자동차 저당권 설정/말소 전자신고 전송
 *  클래스  ID : Txdm6000_process : 실 처리로직을 작성하는 클래스
 *  변경  이력 :
 * ------------------------------------------------------------------------
 *  작성자      	소속  		    일자            Tag           내용
 * ------------------------------------------------------------------------
 *  최유환       유채널(주)      2014.04.28         %01%         최초작성
 */
package com.uc.bs.cyber.daemon.txdm6000;

import java.util.ArrayList;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import com.uc.bs.cyber.daemon.Codm_BaseProcess;
import com.uc.bs.cyber.daemon.Codm_interface;
import com.uc.core.MapForm;
import com.uc.core.spring.service.UcContextHolder;


public class Txdm6000_process extends Codm_BaseProcess implements Codm_interface {

	private MapForm dataMap            = null;
	private MapForm sendMap            = null;
	
	
	
	/**
	 * 
	 */
	public Txdm6000_process() {
		
		super();
		
		log = LogFactory.getLog(this.getClass());
		
		/**
		 * 2 분마다 돈다
		 */
		loopTerm = 60;
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
		
		log.info("===================================================");
		log.info("====    표준지방세 자동차 저당권 설정/말소 전송     =====");
		log.info("====================================================");
		
		try {
				
			setContext(appContext);
			setApp(appContext);

			UcContextHolder.setCustomerType(this.dataSource);
						
			dataMap = new MapForm();  /*초기화*/
			
			//log.info("this.c_slf_org ======================= [" + this.c_slf_org + "]");
		
			int SEQ = 0;
			
			dataMap.setMap("TRTG", "0");
			
			try{
				SEQ = cyberService.getOneFieldInteger("TXDM6000.getTblSingoDataCount", dataMap);
			}catch (Exception e){
				log.info("getTblSingoDataCount 오류");
				e.printStackTrace();
			}						

			if(SEQ>0){				
				this.startJob();
			}else{
				log.info("자동차 저당권 설정/말소 건수가 없습니다.");
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
		/*1. 자동차 저당권 설정/말소 처리 시작                               */
		/*---------------------------------------------------------*/
		txdm6000_JobProcess();
	
	}
	
	
	private int txdm6000_JobProcess() {
		
		//전자신고 대기 목록
		ArrayList<MapForm> sendList = new ArrayList<MapForm>(); 
	
		//전자신고 서비스
		Txdm6000Service service = new Txdm6000Service();

		try {

			try{
				//전자신고 대상건 목록 조회
				sendList = cyberService.queryForList("TXDM6000.getCarSingoList", dataMap);
			}catch (Exception e){
				log.info("전자신고 대상건 목록조회 오류");
				e.printStackTrace();
			}		
			
			if(sendList.size() == 0){
				log.info("자동차 저당권 설정/말소 대상건이 없습니다.");
				return 0;
			}
			
			
			MapForm seqMap = null;
			MapForm taxMap = null;
	
			for(int i=0; i<sendList.size(); i++){
				
				seqMap = new MapForm();				
				
				//전자신고 데이터 초기화
				sendMap = new MapForm();
				//전자신고 데이터 저장
				sendMap = sendList.get(i);
				
				try{
					seqMap = cyberService.queryForMap("TXDM6000.getTblSingoSeq", sendMap);
				}catch(Exception e){
					e.printStackTrace();
					log.info("getTblSingoSeq 오류");
				}
				
				sendMap.setMap("SNO", seqMap.getMap("SNO"));
				
				taxMap = new MapForm();
				
				try{
					taxMap = service.sndService(sendMap);
				}catch(Exception e){
					e.printStackTrace();
					log.info("sndService 오류");
				}
								
				log.info("taxMap :: " + taxMap.getMaps());
				log.info("taxMap.getMap(MESSAGE)  :: "+taxMap.getMap("MESSAGE"));
				//전송 성공
				if(taxMap.getMap("MESSAGE").equals("SVR01")){		    		
					log.info("************ 자동차 저당 살정/말소 신고 완료 *********");		    		
		    		sendMap.setMap("TRTG", "1");
		    		sendMap.setMap("WS_MSG", "정상처리");
		    		sendMap.setMap("CARSTATE", "M8");		    		    		
		    	}else if(taxMap.getMap("MESSAGE").equals("ERR12")){		    		
		    		log.info("************ 자동차 저당 살정/말소 신고 등록 실패 *********");
		    		sendMap.setMap("TRTG", "8");
		    		sendMap.setMap("WS_MSG", "수신 자료 처리 오류");
		    		sendMap.setMap("CARSTATE", "M9");
		    	}else if(taxMap.getMap("MESSAGE").equals("SVR99")){		    		
		    		log.info("************ 자동차 저당 살정/말소 신고 등록 실패 *********");
		    		sendMap.setMap("TRTG", "8");
		    		sendMap.setMap("WS_MSG", "기타오류");
		    		sendMap.setMap("CARSTATE", "M9");
		    	}else{
		    		log.info("************ 자동차 저당 살정/말소 신고 등록 실패 *********");
		    		sendMap.setMap("TRTG", "8");
		    		sendMap.setMap("WS_MSG", "기타 오류 입니다.");
		    		sendMap.setMap("CARSTATE", "M9");
		    	}
				
				try{
					cyberService.queryForUpdate("TXDM6000.updateOnlineTapSingoTrtg", sendMap);
				}catch(Exception e){
					e.printStackTrace();
					log.info("****** 전자신고 테이블 변경 실패 *****");
				}
				
				if(sendMap.getMap("SINGO_GBN").equals("10")){
					try{
						cyberService.queryForUpdate("TXDM6000.updateAutoInsertCarstate", sendMap);
					}catch(Exception e){
						e.printStackTrace();
						log.info("****** 설정 테이블 변경 실패 *****");
					}
				}else if(sendMap.getMap("SINGO_GBN").equals("20")){
					try{
						cyberService.queryForUpdate("TXDM6000.updateAutoErasureCarstate", sendMap);
					}catch(Exception e){
						e.printStackTrace();
						log.info("****** 말소 테이블 변경 실패 *****");
					}
					
				}
				
		    	
			}

					
		} catch (Exception e) {
		    
			throw (RuntimeException) e;
		}
		
		return 0;
	}

	
}
