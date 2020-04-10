/**
 *  주시스템명 : 부산시청 사이버 지방세청
 *  업  무  명 : 연계
 *  기  능  명 : 상하수도 정기분 부가자표 연계(시도)를 실처리하는 클래스
 *               실제 페이지별로 연계를 처리하는 클래스로 
 *               상하수도 서버로부터 연계된 데이터를 사이버세청 서버로 INSERT한다.
 *  클래스  ID : Txdm4311_sub_process 
 *  변경  이력 :
 * ------------------------------------------------------------------------
 *  작성자      	소속  		    일자            Tag           내용
 * ------------------------------------------------------------------------
 *  송동욱       유채널(주)      2011.10.12         %01%         최초작성
 */
package com.uc.bs.cyber.daemon.txdm4311;

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
public class Txdm4311_sub_process extends Codm_BaseProcess implements Codm_interface {

	private MapForm dataMap  = null;
	private int loop_cnt = 0, insert_cnt = 0, update_cnt = 0, del_cnt = 0;
	private int seq = 0;
	/**
	 * 
	 */
	public Txdm4311_sub_process() {
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

	/*처리순서입니다...*/
	public void setSeq(int seq) {
		this.seq = seq;
	}
	
	@Override
	public void transactionJob() {
		// TODO Auto-generated method stub
		txdm4311_JobProcess();
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
			dataMap.setMap("PAGE"         , seq);             /*처리페이지*/
			
			this.startJob();                                  /*멀티 트랜잭션 호출*/
						
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}			
		
	}
	
	/*자료 연계*/
	private int txdm4311_JobProcess() {
		
		log.info("=====================================================================");
		log.info("=" + this.getClass().getName()+ " txdm4311_JobProcess() [상하수도 정기분 고지자료연계] Start = PAGE[" + seq + "]");
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
		
		MapForm mpTaxFixSudoList = new MapForm();
		
		try {
			
			queryElapse1 = System.currentTimeMillis();
			
			/*지방세 정기분외 부과내역을 가져온다.*/
			ArrayList<MapForm> alFixedSudoList =  govService.queryForList("TXDM4311.sudo_select_fixed_list", dataMap);
			
			queryElapse2 = System.currentTimeMillis();
			
			LevyCnt = alFixedSudoList.size();
			
			log.info("[" + c_slf_org_nm + "]상하수도 정기분 쿼리조회 시간 : " + CbUtil.formatTime(queryElapse2 - queryElapse1) + " (" + LevyCnt + ")");
			
			if (LevyCnt  >  0)   {

				
				elapseTime1 = System.currentTimeMillis();
				
				String state_flag = "";
				
				for (int i = 0;  i < LevyCnt;  i++)   {
					
					mpTaxFixSudoList =  alFixedSudoList.get(i);
					
					if (mpTaxFixSudoList == null  ||  mpTaxFixSudoList.isEmpty() )   {
						continue;
					}
					
					state_flag = (String) mpTaxFixSudoList.getMap("state_flag");  /*납기후수납여부*/
					
					loop_cnt++;

					try {
						
						/*1.삭제자료 삭제 처리*/						
						if (state_flag.equals("D")) {

							/*flag 처리*/
							if (cyberService.queryForUpdate("TXDM4311.sudo_delete_levy", mpTaxFixSudoList) > 0) {
								del_cnt++;
							}
							
						} else {
							/*부가자료 입력 시작*/
							try {
								
								cyberService.queryForInsert("TXDM4311.sudo_insert_levy", mpTaxFixSudoList);
								insert_cnt++;
								
							}catch (Exception e){
								
								if (e instanceof DuplicateKeyException){
									cyberService.queryForUpdate("TXDM4311.sudo_update_levy", mpTaxFixSudoList);	
									update_cnt++;
								} else {
									
									log.info("오류발생데이터 = " + mpTaxFixSudoList.getMaps());
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
				
				log.info("[" + c_slf_org_nm + "]상하수도 정기분 고지자료 연계시간 : " + CbUtil.formatTime(elapseTime2 - elapseTime1));
				log.info("상하수도 정기분 고지자료연계 처리건수::" + loop_cnt + ", 부과처리::" + insert_cnt
					   + ", 업데이트::" + update_cnt + ", 삭제처리::" + del_cnt);
			}
			
		}catch (Exception e){
			e.printStackTrace();
		}
		
		return LevyCnt;
		
	}
	

}
