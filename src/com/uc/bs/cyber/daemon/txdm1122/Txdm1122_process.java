/**
 *  주시스템명 : 부산시청 사이버 지방세청
 *  업  무  명 : 연계
 *  기  능  명 : 가상계좌채번자료 구청별 연계
 *  클래스  ID : Txdm1122_process : 실 처리로직을 작성하는 클래스
 *  변경  이력 :
 * ------------------------------------------------------------------------
 *  작성자      	소속  		    일자            Tag           내용 
 * ------------------------------------------------------------------------
 *  김천       유채널(주)      2013.11.28         %01%         최초작성
 */
package com.uc.bs.cyber.daemon.txdm1122;

import java.util.ArrayList;
import java.util.Date;

import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.dao.DuplicateKeyException;

import com.uc.bs.cyber.CbUtil;
import com.uc.bs.cyber.daemon.Codm_BaseProcess;
import com.uc.bs.cyber.daemon.Codm_interface;
import com.uc.core.MapForm;
import com.uc.core.spring.service.UcContextHolder;

public class Txdm1122_process extends Codm_BaseProcess implements Codm_interface {

	private MapForm dataMap = null;
	private MapForm dataMap_604 = null;
	private int insert_cnt = 0, update_cnt = 0, remote_up_cnt = 0;
	
	/*트랜잭션을 위하여 */
	MapForm virListRow = null;
	private MapForm virAccMapForm_604 = null;
	
	/**
	 * 
	 */
	public Txdm1122_process() {
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
		log.info("=[가상계좌채번(기장test)-[" + c_slf_org_nm + "] 자료송신] Start =");
		log.info("=====================================================================");
		try {
				
			setContext(appContext);
			setApp(appContext);

			UcContextHolder.setCustomerType(this.dataSource);
						
			dataMap = new MapForm();  /*초기화*/
			
			log.info("this.c_slf_org ======================= chuntest [" + this.c_slf_org + "]");

			dataMap.setMap("SGG_COD",  710);     //this.c_slf_org = 710      
			//dataMap.setMap("PAGE_PER_CNT"   ,  500);           /*페이지당 갯수*/
			
			do {
				
				int virCnt = cyberService.getOneFieldInteger("TXDM1122.SELECT_VIR_COUNT", dataMap);
				
				//log.info("[가상계좌채번-[" + c_slf_org_nm + "]] PAGE_PER_CNT(" + dataMap.getMap("PAGE_PER_CNT") + ") 당 Page갯수 =" + virCnt);
				log.info("[가상계좌채번(기장test)-[" + c_slf_org_nm + "]] 건수(" + virCnt + ")");
				
				if(virCnt == 0) break;
				
				else this.startJob(); //job을 실행
				
				if(cyberService.getOneFieldInteger("TXDM1122.SELECT_VIR_COUNT", dataMap) == 0) break;
				
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
			
		/*---------------------------------------------------------*/
		/*1. 부가연계자료 업무 처리.                               */
		/*---------------------------------------------------------*/
		txdm1122_JobProcess();
	
	}

	private int txdm1122_JobProcess() {
		
		log.info("=====================================================================");
		log.info("=" + this.getClass().getName()+ " txdm1122_JobProcess()[가상계좌채번(기장-잡프로세스 실행됨)-[" + c_slf_org_nm + "] 자료전송] Start =");
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
			//log.info("111=====================================================================");
			dataMap.setMap("SGG_COD",  710);
			//log.info("222====" + dataMap.getMaps() + "=================================================================");
			dataMap_604 = new MapForm();
			dataMap_604.setMap("C_SLF_ORG", 26710);
            //log.info("333=====================================================================");
			/*연계테이블 부과자료 SELECT 쿼리(NIS)*/
			ArrayList<MapForm> virAccList =  cyberService.queryForList("TXDM1122.SELECT_VIR_LIST", dataMap); //가상계좌번호 필요
			
			//int test =  cyberService.getOneFieldInteger("TXDM1122.SELECT_604_test", dataMap_604); //가상계좌번호 필요
			//ArrayList<MapForm> virAccList_604 =  cyberService.queryForList("TXDM1122.SELECT_VIR_LIST_604", dataMap_604); 
			//log.info("test(count)=====" + test);
			tot_size = virAccList.size();
			c_slf_org_nm = "기장군";
		    log.info("[" + c_slf_org_nm + "]가상계좌채번자료 건수(기장 C_SLF_ORG= 26710) = [" + tot_size + "]");
			
			if (tot_size  >  0)   { //tx1102_tb 테이블에 생성된 가상계좌번호가 있으면, 
				
				/* 가상계좌채번자료를 해당 구청에 fetch */
				for (cnt = 0;  cnt < tot_size;  cnt++)   {
					virListRow = new MapForm();
					virListRow =  (MapForm)virAccList.get(cnt);
					/*log.info("virListRow에 값 확인 C_SLF_ORG=" + virListRow.getMap("C_SLF_ORG"));
					log.info("virListRow에 값 확인 C_SSEMOK=" + virListRow.getMap("C_SSEMOK"));
					log.info("virListRow에 값 확인 YY_GWASE=" + virListRow.getMap("YY_GWASE"));
					log.info("virListRow에 값 확인 MM_GWASE=" + virListRow.getMap("MM_GWASE"));
					log.info("virListRow에 값 확인 V_GWASE=" + virListRow.getMap("V_GWASE"));
					log.info("virListRow에 값 확인 C_DONG=" + virListRow.getMap("C_DONG"));					
					log.info("virListRow에 값 확인 S_GWASE=" + virListRow.getMap("S_GWASE"));							
					log.info("virListRow에 값 확인 VIR_ACC=" + virListRow.getMap("VIR_ACC"));					
					*/
					virAccMapForm_604 = new MapForm();
					ArrayList<MapForm> virAccList_604_test = govService.queryForList("TXDM1122.SELECT_VIR_LIST_604", virListRow); 
					if(virAccList_604_test.size()>0){
					virAccMapForm_604 = (MapForm)virAccList_604_test.get(0);
					log.info("604에 부과자료 있음 : 과세번호 =" + virListRow.getMap("V_GWASE") +"가상계좌번호 =" + virListRow.getMap("VIR_ACC"));
					}
					else{
						//log.info("604에 부과자료 없음");	
						continue;
					}
					//log.info("V_GWASE ==============" + virListRow.getMap("V_GWASE"));
					log.info("SELECT_VIR_LIST_604 통과");
					/*혹시나...because of testing */
					if (virListRow == null  ||  virListRow.isEmpty() )   {
						nul_cnt++;
						continue;
					}
					
					
					if(virAccMapForm_604.size() > 0){ //tx1102_tb와 604테이블에 매치되는 값이 있을 때
						/*기본 Default 값 설정 */
						//Date date = new Date(); 
						virListRow.setMap("SGG_TR_TG"   , "1" );                       /*구청전송처리구분*/
						//virListRow.setMap("UPD_DT_2","");
						//virListRow.setMap("UPD_DT_2","");						
						//virAccMapForm_604.setMap("UPD_USR_2","수정자2");
						//virListRow.setMap("END_DT_2","");
						//virAccMapForm_604.setMap("VIR_COM_2","부산은행");						
						//log.info("virAccMapForm_604.setMap 통과");
						//log.info("virAccMapForm_604=" + virAccMapForm_604.getMaps());
						//log.info("virListRow=" + virListRow.getMaps());
						
						/*if(virAccMapForm_604.getMap("VIR_ACC") == null){ //완전 처음 가상계좌 생성시
							try {
								log.info("완전 처음 가상계좌 생성 통과");
								cyberService.queryForUpdate("TXDM1122.VIRACC_UPDATE_FIRST", virListRow);
								
							} catch (Exception e) {
								log.error("오류데이터(VIRACC_UPDATE_FIRST) = " + virListRow.getMaps());
								throw (RuntimeException) e;
							}
							
						}else{ //가상계좌 생성 처음이 안
*/							
						
						
					if(virAccMapForm_604.getMap("VIR_ACC_2")== null){ //생성일자가 없을 때 => 처음 가상계좌 이후로 생성된 가상계좌번호가 없을 시
							log.info("virAccMapForm_604.getMap(VIR_ACC_2)== null");							
							log.info("dataMap.getMap(sgg_cod)=" + dataMap.getMap("SGG_COD"));		  
					/*if(dataMap.getMap("SGG_COD")=="710"){  //기장군일 경우, 			
*/					
					
							try{
								log.info("업데이트 VIRACC2_UPDATE_FIRST 통과");
								govService.queryForUpdate("TXDM1122.VIRACC2_UPDATE_FIRST", virListRow);
								update_cnt++;
								    
							} catch (Exception sub_e) {
								log.error("오류데이터(VIRACC2_UPDATE_FIRST) = " + virListRow.getMaps());									
									sub_e.printStackTrace();
									throw (RuntimeException) sub_e;
							}							
					
					/*}*/
					
					}else{ //생성일자가 있을 때(가상계좌를 최초 업데이트 한게 아닐 때) 
						log.info("가상계좌를 최초 업데이트 한게 아닐 때 else문 통과");
						log.info("dataMap.getMap(sgg_cod)=" + dataMap.getMap("SGG_COD"));	
						/*if(dataMap.getMap("SGG_COD")=="710"){  //기장군일 경우, 	
*/							try{
								log.info("TXDM1122.VIRACC2_UPDATE_NOT_FIRST 통과");
								log.info("VIR_ACC=" + virListRow.getMap("VIR_ACC"));
								govService.queryForUpdate("TXDM1122.VIRACC2_UPDATE_NOT_FIRST", virListRow);
								update_cnt++;
								    
							} catch (Exception sub_e) {
								log.error("오류데이터(VIRACC2_UPDATE_NOT_FIRST) = " + virListRow.getMaps());									
									sub_e.printStackTrace();
									throw (RuntimeException) sub_e;
							}	
						/*}*/
					}
					
						/*} 완전 처음 가상계좌 생성일때 if문 닫기*/
					try{
						
						/* 지상세 상세테이블 가상계좌 전송 완료 업데이트 */
						log.info("SGG_TR_TG를 1로 업데이트");
						remote_up_cnt += cyberService.queryForUpdate("TXDM1122.UPDATE_COMPLETE", virListRow);
						    
					} catch (Exception e) {
						
						log.error("오류데이터 = " + virListRow.getMaps());
						log.error(e.getMessage());
						e.printStackTrace();
						throw (RuntimeException) e;
						
					}
					
					}else{ //tx1102_tb와 604테이블에 매치되는 값이 없을 때
						//scon604에 부과자료가 없다
						System.out.println("scon604에 부과자료가 없다");
						 log.info("scon604에 부과자료가 없다");
					}
					
				}
				
				log.info("[" + c_slf_org_nm + "]가상계좌채번 건수 [" + cnt + "] 등록건수 [" + insert_cnt +"] 수정건수 [" + update_cnt + "]");
				log.info("지방세상세테이블업데이트 ["+remote_up_cnt+"], 미처리 부과건수 [" + nul_cnt + "]");
			}

			elapseTime2 = System.currentTimeMillis();
			
			log.info("[" + c_slf_org_nm + "]가상계좌채번 전송 시간("+tot_size+") : " + CbUtil.formatTime(elapseTime2 - elapseTime1));
					
		} catch (Exception e) {
		    log.error("오류 데이터11 = " + virListRow.getMaps());
			throw (RuntimeException) e;
		}
		
		return tot_size;
	}
}
