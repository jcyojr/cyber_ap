/**
 *  주시스템명 : 부산시청 사이버 지방세청
 *  업  무  명 : 연계
 *  기  능  명 : 지방세 연대납세자 부과자료연계(구청별) 처리 업무
 *  클래스  ID : Txdm3112_process : 실 처리로직을 작성하는 클래스
 *  변경  이력 :
 * ------------------------------------------------------------------------
 *  작성자      	소속  		    일자            Tag           내용
 * ------------------------------------------------------------------------
 *  이윤섭       유채널(주)      2014.10.02         %01%         최초작성
 *  변성호       유채널              2015.03.22                      세무대리인 업무 추가
 *  변성호       유채널              2015.03.22                      특별징수 명세 업무 추가
 */
package com.uc.bs.cyber.daemon.txdm3112;

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
 *
 */

public class Txdm3112_process extends Codm_BaseProcess implements Codm_interface {

	private MapForm dataMap  = null;
	private int insert_cnt = 0, update_cnt = 0, del_cnt = 0;
	
	/**
	 * 
	 */
	public Txdm3112_process() {
		
		super();
		
		log = LogFactory.getLog(this.getClass());
		
		/* 300초마다 */
		this.loopTerm = 300;	
	}
	
	/*Context 주입용*/
	public void setApp(ApplicationContext context) {
		
		this.context = context;
	}

	/*프로세스 시작*/
	public void runProcess() throws Exception {
		
		/*트랜잭션 시작*/
		mainTransProcess();
	}

	/*Context 주입용*/
	public void setDatasrc(String datasrc) {
		
		this.dataSource = datasrc;
	}

	public void transactionJob() {
		
		//작업구분 세무대리인업무 추가
		String jb = (String) dataMap.getMap("JobGb");
		
		/*정기분 외 부가처리..*/
		txdm3112_JobProcess(jb);
	}

	/*트랜잭션을 실행하기 위한 함수.*/
	private void mainTransProcess(){
		
		try {
			
			setContext(appContext);
			setApp(appContext);

			UcContextHolder.setCustomerType(this.dataSource);
			
			/*초기화*/
			dataMap = new MapForm();
			/*페이지당 갯수*/
			dataMap.setMap("PAGE_PER_CNT" , 1000);
			/*구청코드 맵핑*/
			dataMap.setMap("SGG_COD"      , this.c_slf_org);
			/*미전송 분*/
			dataMap.setMap("TRN_YN"       , "0");           
			
			int page = govService.getOneFieldInteger("TXDM3112.select_count_page", dataMap);
			
			log.info("[지방세 연대납세자 자료 (" + c_slf_org_nm + ")] PAGE_PER_CNT(" + dataMap.getMap("PAGE_PER_CNT") + ") 당 Page갯수 =" + page);
				
			if(page == 0){
				log.info("지방세 연대납세자 자료가 없습니다.");
			}else{
				/*멀티 트랜잭션 호출*/
				dataMap.setMap("JobGb", "1");
				this.startJob(); 
			}
			
			/*세무대리인 신청정보를 표준지방세에 넣는 데몬  */
			int page2 = cyberService.getOneFieldInteger("TXDM3112.select_count_page2", dataMap);
			
			if(page2 == 0){
				log.info("세무대리인 신청자료가 없습니다.");
			}else{
				/*멀티 트랜잭션 호출*/
				dataMap.setMap("JobGb", "2");
				this.startJob(); 
			}			
			
			/*세무대리인 신청결과 처리 업무 추가 2015.03.19 변성호 여기가 표준지방세꺼 긁어오는곳*/
			/*사이버 세청에서 신청한 데이터만 처리*/
			int page3 = govService.getOneFieldInteger("TXDM3112.select_count_page3", dataMap);
			int page4 = cyberService.getOneFieldInteger("TXDM3112.select_count_page4", dataMap);
			
			if(page3 == 0 || page4 == 0){
				log.info("세무대리인 신청결과 및 신철결과를 처리할 자료가 없습니다.");
			}else{
				/*멀티 트랜잭션 호출*/
				dataMap.setMap("JobGb", "3");
				this.startJob(); 
			}
			
			/*특별징수 명세서*/
			int page5 = cyberService.getOneFieldInteger("TXDM3112.select_count_page5", dataMap);
			
			if(page5 == 0){
				log.info("처리할 특별징수 명세서가 없습니다.!");
			}else{
				/*멀티 트랜잭션 호출*/
				dataMap.setMap("JobGb", "4");
				this.startJob(); 
			}
			
			/* 세무대리인 위텍스 자료 동기화 데몬
			 * 사이버 지방세청 자료가 우선시 된다.
			 * 사이버 지방세청에 없고 위텍스에 승인된자료는 인서트
			 * 사이버 지방세청에 있고 위텍스에도 있는자료는 사이버 지방세청 우선임.
			 */

			/*위텍스 동기화 대상검색*/
			int page6 = govService.getOneFieldInteger("TXDM3112.select_count_page6", dataMap);
			if(page6 < 1){
				log.info("세무대리인 위텍스정보 동기화 자료가 없습니다.");
			}else{
				/*멀티 트랜잭션 호출*/
				dataMap.setMap("JobGb", "5");
				this.startJob(); 
			}
			
			
		} catch (Exception e) {

			e.printStackTrace();
		}			
		
		
	}
	
	/*자료 연계*/
	private int txdm3112_JobProcess(String jb) {
		
		
		long queryElapse1 = 0;
		long queryElapse2 = 0;
		
		int LevyCnt = 0;
		
		insert_cnt = 0;
		update_cnt = 0;
		del_cnt = 0;
	
		
		MapForm etaxJointTaxPaymentList = null;
		MapForm etaxJointSemuList = null;    //세무대리인 신청요청
		MapForm etaxJointSemuRltList = null; //세무대리인 신청결과
		MapForm etaxJointTgRltList = null; //특별징수명세서
		MapForm etaxJointWtRltList = null; //위텍스 자료동기화
		
		try {
			
			if("1".equals(jb)){
			
			queryElapse1 = System.currentTimeMillis();
			
			/*지방세 연대납세자 부과내역을 가져온다.*/
			ArrayList<MapForm> jointTaxPaymentList =  govService.queryForList("TXDM3112.select_jointTaxPayment_list", dataMap);
			
			queryElapse2 = System.currentTimeMillis();
			
			LevyCnt = jointTaxPaymentList.size();
			
			log.info("[" + c_slf_org_nm + "]연대납세자 쿼리조회 시간 : " + CbUtil.formatTime(queryElapse2 - queryElapse1) + " (" + LevyCnt + ")");
			
			if (LevyCnt  >  0)   {
				
				for (int i = 0;  i < LevyCnt;  i++)   {
					
					etaxJointTaxPaymentList = new MapForm();
					
					etaxJointTaxPaymentList =  jointTaxPaymentList.get(i);

					if (etaxJointTaxPaymentList == null  ||  etaxJointTaxPaymentList.isEmpty() )   {
						
						continue;
					}
					

					/*========================*/
					/*트랜잭션처리 1000건단위 기본*/
					/*1. 부과자료 등록, 수정, 삭제 */
					/*2. 연계테이블 업데이트*/
					/*========================*/
					
					try {
						
						/*신규 및 수정*/	
						if(etaxJointTaxPaymentList.getMap("CUD_OPT").equals("1")||etaxJointTaxPaymentList.getMap("CUD_OPT").equals("2")){  
							
							try {
								
								    //우리쪽 인설트
									cyberService.queryForInsert("TXDM3112.insert_tx1104_tb", etaxJointTaxPaymentList);  
									
									//연계테이블 업데이트(1=성공) 
									etaxJointTaxPaymentList.setMap("TRN_YN", "1");   
									govService.queryForUpdate("TXDM3112.update_complete",etaxJointTaxPaymentList);
							
									insert_cnt++;
							
							}catch (Exception e){
							
									if (e instanceof DuplicateKeyException){
										
										//우리쪽 업데이트
										cyberService.queryForUpdate("TXDM3112.update_tx1104_tb", etaxJointTaxPaymentList);
										
										//연계테이블 업데이트(1=성공)  
										etaxJointTaxPaymentList.setMap("TRN_YN", "1");
										govService.queryForUpdate("TXDM3112.update_complete",etaxJointTaxPaymentList);
										
										update_cnt++;
										
									} else {
										
										log.info("오류발생데이터 = " + etaxJointTaxPaymentList.getMaps());
										
										//연계테이블 업데이트 (8=인설트, 업데이트 오류)
										etaxJointTaxPaymentList.setMap("TRN_YN", "8"); 
										govService.queryForUpdate("TXDM3112.update_complete",etaxJointTaxPaymentList);
										
										e.printStackTrace();
										
										throw (RuntimeException) e;
									}
							}
							
					    /*삭제*/	
						} else if (etaxJointTaxPaymentList.getMap("CUD_OPT").equals("3")){  
							
							try{
								
								//DEL_YN=Y로 업데이트
								cyberService.queryForUpdate("TXDM3112.delete_tx1104_tb", etaxJointTaxPaymentList);
								
								//연계테이블 업데이트 (1= 성공) 
								etaxJointTaxPaymentList.setMap("TRN_YN", "1");   
								govService.queryForUpdate("TXDM3112.update_complete",etaxJointTaxPaymentList);
								
								del_cnt++;
								
							} catch (Exception e) {
								
								log.info("오류발생데이터 = " + etaxJointTaxPaymentList.getMaps());
								
								//연계테이블 업데이트 (8=인설트, 업데이트 오류)
								etaxJointTaxPaymentList.setMap("TRN_YN", "8"); 
								govService.queryForUpdate("TXDM3112.update_complete",etaxJointTaxPaymentList);

								e.printStackTrace();
							}
							
						}else{
						
							log.info("오류발생데이터 = " + etaxJointTaxPaymentList.getMaps());
							
							//연계테이블 업데이트 (9=연계테이블 처리구분 오류)
							etaxJointTaxPaymentList.setMap("TRN_YN", "9");    
							govService.queryForUpdate("TXDM3112.update_complete",etaxJointTaxPaymentList);

							
						}
						
							

					} catch (Exception e) {

						log.info("오류발생데이터 = " + etaxJointTaxPaymentList.getMaps());

						e.printStackTrace();
					}

				}
				
			    log.info("연계건수 : " + LevyCnt + "건 중 신규 " + insert_cnt + "건 수정 " +  update_cnt + "건 삭제 " + del_cnt + "건 처리완료");
				
			}else{
				log.debug("연계자료가 없습니다.");
			}

			//세무대리인 업무
			}else if("2".equals(jb)){
				queryElapse1 = System.currentTimeMillis();
				
				/*세무대리인 신청자료를 가져온다.*/
				ArrayList<MapForm> jointSemuList = cyberService.queryForList("TXDM3112.select_jointSemu_list", dataMap);
				
				queryElapse2 = System.currentTimeMillis();
				
				LevyCnt = jointSemuList.size();
				
				log.info("[" + c_slf_org_nm + "]세무대리인 신청결과 쿼리조회 시간 : " + CbUtil.formatTime(queryElapse2 - queryElapse1) + " (" + LevyCnt + ")");
				
				if (LevyCnt  >  0)   {
					for (int i = 0;  i < LevyCnt;  i++)   {
						etaxJointSemuList = new MapForm();
						etaxJointSemuList =  jointSemuList.get(i);
						if (etaxJointSemuList == null  ||  etaxJointSemuList.isEmpty() )   {
							continue;
						}
						
							try {
									//세무대리인 신청자료 표준지방세쪽에 인서트
								    //150403 인서트만 존재 SEQ 매번 채번으로인한 동일데이터가 037테이블에는 게속쌓임 선처리 불필요.
									//int cnt = govService.getOneFieldInteger("TXDM3112.f_search",etaxJointSemuList);
								
										if("6".equals(etaxJointSemuList.getMap("REQ_PROC"))){
											etaxJointSemuList.setMap("REQ_PROC", "2");
										}
										
										//세무대리인 신청자료 표준지방세쪽에 인서트
										int iCnt = govService.queryForUpdate("TXDM3112.semu_insert",etaxJointSemuList);  
										
										if(iCnt > 0){
										//전송한데이터 전송여부 Y로 변경
										cyberService.queryForUpdate("TXDM3112.semu_cyber_update",etaxJointSemuList);
										insert_cnt++;
										}
										
							}catch (Exception e){
							
										log.info("오류발생데이터 = " + etaxJointSemuRltList.getMaps());
										e.printStackTrace();
										throw (RuntimeException) e;
							}

					}
					
				    log.info("처리결과 건수 : [" + LevyCnt + "] 건 중 insert : [" + insert_cnt + "] 건  처리완료");
				    
				}else{
					log.debug("세무대리인 신청 결과가 없습니다.");
				}
				
			}else if("3".equals(jb)){
				
				String msg = "";
				queryElapse1 = System.currentTimeMillis();
				
				/*세무대리인 신청결과를 가져온다.*/
				ArrayList<MapForm> jointSemuRltList = govService.queryForList("TXDM3112.select_jointSemuRlt_list", dataMap);
				
				queryElapse2 = System.currentTimeMillis();
				
				LevyCnt = jointSemuRltList.size();
				
				log.info("[" + c_slf_org_nm + "]세무대리인 신청결과 쿼리조회 시간 : " + CbUtil.formatTime(queryElapse2 - queryElapse1) + " (" + LevyCnt + ")");
				
				if (LevyCnt  >  0)   {
					
					for (int i = 0;  i < LevyCnt;  i++)   {
						etaxJointSemuRltList = new MapForm();
						etaxJointSemuRltList =  jointSemuRltList.get(i);
						if (etaxJointSemuRltList == null  ||  etaxJointSemuRltList.isEmpty() )   {
							continue;
						}

							try {
								
								//처리할대상이 있을경우에 결과처리
								int chkCnt = cyberService.getOneFieldInteger("TXDM3112.rltSelect_count", etaxJointSemuRltList);
								
								if(chkCnt > 0){
									//사이버세청 REQ_PROC 상태값 조회
									String cy_proc = cyberService.getOneFieldString("TXDM3112.cyber_Proc_Select", etaxJointSemuRltList);
									
									//취소건이면 표준지방세가 취소승인인 REQ_PROC가 2번일때만 가져와서 업데이트한다.
									//접수건은 4번인데 승인처리되기전에 사이버세청에 업데이트 되면 안되기때문에..
									if(!"2".equals(etaxJointSemuRltList.getMap("REQ_PROC")) && "6".equals(cy_proc)){
										
									}else{
									//우리쪽 업데이트
									int uCnt = cyberService.queryForUpdate("TXDM3112.update_tx5111_tb", etaxJointSemuRltList);  
										if(uCnt > 0){
											update_cnt++;
										}
									}	
								}
							}catch (Exception e){
							
										log.info("오류발생데이터 = " + etaxJointSemuRltList.getMaps());
										e.printStackTrace();
										throw (RuntimeException) e;
							}

					}
				    log.info("처리결과 건수 : " + update_cnt + "건 처리완료");
				}else{
					log.debug("세무대리인 신청처리 결과가 없습니다.");
				}
				
			}else if("4".equals(jb)){
				queryElapse1 = System.currentTimeMillis();
				
				/*특별징수 명세서 처리대상 조회*/
				ArrayList<MapForm> jointTgList = cyberService.queryForList("TXDM3112.select_jointTG_list", dataMap);
				
				queryElapse2 = System.currentTimeMillis();
				//etaxJointTgRltList
				LevyCnt = jointTgList.size();
				
				log.info("[" + c_slf_org_nm + "]특별징수 명세서 쿼리조회 시간 : " + CbUtil.formatTime(queryElapse2 - queryElapse1) + " (" + LevyCnt + ")");
				
				if (LevyCnt  >  0)   {
					for (int i = 0;  i < LevyCnt;  i++)   {
						etaxJointTgRltList = new MapForm();
						etaxJointTgRltList =  jointTgList.get(i);
						if (etaxJointTgRltList == null  ||  etaxJointTgRltList.isEmpty() )   {
							continue;
						}
						
							try {
								//구청코드
								etaxJointTgRltList.setMap("SGG_COD", dataMap.get("SGG_COD"));	
								
								//특별징수 명세서 처리자료를 해당구청에 인서트
								int iCnt = govService.queryForUpdate("TXDM3112.tg_insert",etaxJointTgRltList);  
								
								if(iCnt > 0){
								//구청인서트 완료후 사이버세청업데이투
								cyberService.queryForUpdate("TXDM3112.tg_update",etaxJointTgRltList);
								insert_cnt++;
								}	
							}catch (Exception e){
							
								log.info("오류발생데이터 = " + etaxJointTgRltList.getMaps());
								e.printStackTrace();
								throw (RuntimeException) e;
							}

					}
					
				    log.info("처리결과 건수 : [" + LevyCnt + "] 건 중 insert : [" + insert_cnt + "] 건 처리완료");
				    
				}else{
					log.debug("특별징수 명세서 처리결과가 없습니다.");
				}
				
			}else if("5".equals(jb)){
				String msg = "";
				queryElapse1 = System.currentTimeMillis();
				
				/*세무대리인 위텍스 승인내역을 가져온다.*/
				ArrayList<MapForm> jointWtRltList = govService.queryForList("TXDM3112.select_jointWtRlt_list", dataMap);
				
				queryElapse2 = System.currentTimeMillis();
				
				LevyCnt = jointWtRltList.size();
				
				log.info("[" + c_slf_org_nm + "]세무대리인 위텍스자료 쿼리조회 시간 : " + CbUtil.formatTime(queryElapse2 - queryElapse1) + " (" + LevyCnt + ")");
				
				if (LevyCnt  >  0)   {
					
					for (int i = 0;  i < LevyCnt;  i++)   {
						etaxJointWtRltList = new MapForm();
						etaxJointWtRltList =  jointWtRltList.get(i);
						if (etaxJointWtRltList == null  ||  etaxJointWtRltList.isEmpty() )   {
							continue;
						}

							try {
								
								//처리할대상이 있을경우에 결과처리
								int chkCnt = cyberService.getOneFieldInteger("TXDM3112.wtRltSelect_count", etaxJointWtRltList);
								
								//사이버 세청에 데이터가 없고
								if(chkCnt == 0){
									//위텍스 승인된건
								    if("4".equals(jointWtRltList.get(i).getMap("REQ_PROC"))){
								    	//우리쪽 인서트
								    	int uCnt = cyberService.queryForUpdate("TXDM3112.insertWt_tx5111_tb", etaxJointWtRltList);  
										if(uCnt > 0){
											insert_cnt++;
										}
								    }
								}else{
									//해당 데이터가 있고 데이터가 위텍스 자료이이고 사이버세청 DB와 표준지방세DB의 상태값이 다르면 업데이트
									int statCnt = cyberService.getOneFieldInteger("TXDM3112.wtStatSelect_count", etaxJointWtRltList);
										if(statCnt > 0){
											int upCnt = cyberService.queryForUpdate("TXDM3112.updateWt_tx5111_tb", etaxJointWtRltList);
											if(upCnt > 0){
												update_cnt++;
											}
										}
								}
								
							}catch (Exception e){
							
										log.info("오류발생데이터 = " + etaxJointWtRltList.getMaps());
										e.printStackTrace();
										//throw (RuntimeException) e;
							}

					}
				    log.info("처리결과 건수 : 등록 [" + insert_cnt + "]건 변경 [" + update_cnt + "]건 처리완료");
				}else{
					log.debug("세무대리인 위텍스 동기화 자료가 없습니다.");
				}
				
			}
			
		} catch (Exception e) {
			log.info("오류발생데이터 = " + etaxJointTaxPaymentList.getMaps());

			e.printStackTrace();
		}

		return LevyCnt;

	}

}
