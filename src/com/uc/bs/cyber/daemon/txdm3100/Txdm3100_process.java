/**
 *  주시스템명 : 부산시청 사이버 지방세청
 *  업  무  명 : 연계
 *  기  능  명 : 지방세 수시,자납분 부가자표 연계(시도) 업무
 *  클래스  ID : Txdm3100_process : 실 처리로직을 작성하는 클래스
 *  변경  이력 :
 * ------------------------------------------------------------------------
 *  작성자      	소속  		    일자            Tag           내용
 * ------------------------------------------------------------------------
 *  송동욱       유채널(주)      2011.10.12         %01%         최초작성
 */
package com.uc.bs.cyber.daemon.txdm3100;

import java.math.BigDecimal;
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
public class Txdm3100_process extends Codm_BaseProcess implements Codm_interface {

	private MapForm dataMap  = null;
	private int loop_cnt = 0, insert_cnt = 0, update_cnt = 0, del_cnt = 0, vir_del_cnt = 0;
	
	/**
	 * 
	 */
	public Txdm3100_process() {
		// TODO Auto-generated constructor stub
		super();
		
		log = LogFactory.getLog(this.getClass());
	}

	/*Context 주입용*/
	public void setApp(ApplicationContext context) {
		this.context = context;
	}

	/*프로세스 시작*/
	@Override
	public void runProcess() throws Exception {
		// TODO Auto-generated method stub
		
		/*트랜잭션 시작*/
		mainTransProcess();
	}

	/*Context 주입용*/
	@Override
	public void setDatasrc(String datasrc) {
		// TODO Auto-generated method stub
		this.dataSource = datasrc;
	}

	@Override
	public void transactionJob() {
		// TODO Auto-generated method stub
		txdm3100_JobProcess();
	}


	/*트랜잭션을 실행하기 위한 함수.*/
	private void mainTransProcess(){
		
		
		try {
			
			setContext(appContext);
			setApp(appContext);

			UcContextHolder.setCustomerType(this.dataSource);
			
			dataMap = new MapForm();  /*초기화*/
			dataMap.setMap("PAGE_PER_CNT" , 1000);            /*페이지당 갯수*/
			dataMap.setMap("SGG_COD"      , this.c_slf_org);  /*구청코드 맵핑*/
			dataMap.setMap("trn_yn"       , '0');             /*미전송 분    */
			
			do {
				
				int page = govService.getOneFieldInteger("TXDM3100.select_count_page", dataMap);
				
				log.info("[지방세 수시,자납분(시도) 부가자료 (" + c_slf_org_nm + ")] PAGE_PER_CNT(" + dataMap.getMap("PAGE_PER_CNT") + ") 당 Page갯수 =" + page);
				
				if(page == 0) break;

				for(int i = 1 ; i <= page ; i ++) {

					dataMap.setMap("PAGE",  i);    /*처리페이지*/
					this.startJob();               /*멀티 트랜잭션 호출*/
				}
								
				if(govService.getOneFieldInteger("TXDM3100.select_count_page", dataMap) == 0) {
					break;
				}
				
			}while(true);
						
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}			
		
		
	}
	
	/*자료 연계*/
	private int txdm3100_JobProcess() {
		
		log.info("=====================================================================");
		log.info("=" + this.getClass().getName()+ " txdm3100_JobProcess() [지방세(시도) 수시,자납분 고지자료연계] Start =");
		log.info("= govid["+this.govId+"], orgcd["+this.c_slf_org+"], orgcd["+this.c_slf_org_nm+"]");
		log.info("=====================================================================");
		
		long queryElapse1 = 0;
		long queryElapse2 = 0;
		
		long elapseTime1 = 0;
		long elapseTime2 = 0;
		
		int LevyCnt = 0;
		
		loop_cnt = 0; 
		insert_cnt = 0;
		update_cnt = 0;
		del_cnt = 0;
		vir_del_cnt = 0;
		
		MapForm mpTaxFixLevyList = new MapForm();
		
		try {
			
			queryElapse1 = System.currentTimeMillis();
			
			/*지방세 정기분외 부과내역을 가져온다.*/
			ArrayList<MapForm> alFixedLevyList =  govService.queryForList("TXDM3100.select_nonfixed_list", dataMap);
			
			queryElapse2 = System.currentTimeMillis();
			
			LevyCnt = alFixedLevyList.size();
			
			log.info("[" + c_slf_org_nm + "]지방세(시도)수시, 자납분 쿼리조회 시간 : " + CbUtil.formatTime(queryElapse2 - queryElapse1) + " (" + LevyCnt + ")");
			
			if (LevyCnt  >  0)   {

				String notice_cls = "";
				String due_f_dt   = "";
				
				elapseTime1 = System.currentTimeMillis();
				
				for (int i = 0;  i < LevyCnt;  i++)   {
					
					mpTaxFixLevyList =  alFixedLevyList.get(i);
					
					if (mpTaxFixLevyList == null  ||  mpTaxFixLevyList.isEmpty() )   {
						continue;
					}
					
					notice_cls = (String) mpTaxFixLevyList.getMap("notice_cls");  /*납기후수납여부*/
					
					loop_cnt++;

					/*========================*/
					/*트랜잭션처리 1000건단위 기본*/
					/*1. 부가자료 등록 및 수정*/
					/*========================*/
					
					try {
						
						/*1.삭제자료 삭제 처리*/
						if (notice_cls.equals("C")) {
							due_f_dt = "00000000";
						} else if (notice_cls.equals("D")) {
														
							long mntx = ((BigDecimal)mpTaxFixLevyList.getMap("mntx")).longValue(); // 본세금액 300000원 이하이면 납기무관
							
							if (mntx < 300000) 
								due_f_dt = "00000000";
							else 
								due_f_dt = (String) mpTaxFixLevyList.getMap("ggym"); // 납기후일자
							
						} else {
							due_f_dt = (String) mpTaxFixLevyList.getMap("ggym");	    // 납기후일자
						}
						
						mpTaxFixLevyList.setMap("due_f_dt", due_f_dt);
												
						long b_amt = ((BigDecimal)mpTaxFixLevyList.getMap("sum_b_amt")).longValue(); // 납기내금액
						long f_amt = ((BigDecimal)mpTaxFixLevyList.getMap("sum_f_amt")).longValue(); // 납기후금액
						
						if (mpTaxFixLevyList.getMap("cud_opt").equals("3") || (b_amt+f_amt) == 0) {
							
							mpTaxFixLevyList.setMap("del_yn", "Y");
							/*flag 처리*/
							if (cyberService.queryForUpdate("TXDM3111.update_tx1102_tb2", mpTaxFixLevyList) > 0) {
								del_cnt++;
							}
							
						} else {
							/*부가자료 입력 시작*/
							
							/*부과*/
							try {
								
								cyberService.queryForInsert("TXDM3111.insert_tx1101_tb", mpTaxFixLevyList);
								
								insert_cnt++;
								
							}catch (Exception e){
								
								if (e instanceof DuplicateKeyException){
									cyberService.queryForUpdate("TXDM3111.update_tx1101_tb", mpTaxFixLevyList);	
									update_cnt++;
								} else {
									
									log.info("오류발생데이터 = " + mpTaxFixLevyList.getMaps());
									e.printStackTrace();							
									throw (RuntimeException) e;
								}
							}
							
							/*부과상세*/
							try {
								
								cyberService.queryForInsert("TXDM3111.insert_tx1102_tb", mpTaxFixLevyList);
								
								insert_cnt++;
								
							}catch (Exception e){
								
								if (e instanceof DuplicateKeyException){
									cyberService.queryForUpdate("TXDM3111.update_tx1102_tb", mpTaxFixLevyList);	
									update_cnt++;
								} else { 
									
									log.info("오류발생데이터 = " + mpTaxFixLevyList.getMaps());
									e.printStackTrace();							
									throw (RuntimeException) e;
								}
							}
						}
						
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
				
				elapseTime2 = System.currentTimeMillis();
				
				log.info("[" + c_slf_org_nm + "]지방세(시도)수시,자납분 고지자료 연계시간 : " + CbUtil.formatTime(elapseTime2 - elapseTime1));
				log.info("지방세(시도)수시, 자납분 고지자료연계 처리건수::" + loop_cnt + ", 부과처리::" + ((insert_cnt > 0) ? insert_cnt / 2 : insert_cnt) 
					   + ", 업데이트::" + ((update_cnt > 0) ? update_cnt / 2 : update_cnt) + ", 삭제처리::" + del_cnt + ", 원장가상계좌 삭제 ::" + vir_del_cnt);
			}
			
		}catch (Exception e){
			e.printStackTrace();
		}
		
		return LevyCnt;
		
	}
	

}
