/**
 *  주시스템명 : 부산시청 사이버 지방세청
 *  업  무  명 : 
 *  기  능  명 : 충당수납
 *  클래스  ID : Txbt5000.process
 *  변경  이력 :
 * ------------------------------------------------------------------------
 *  작성자      	소속  		    일자            Tag           내용
 * ------------------------------------------------------------------------
 * 김범준        유채널       2012.04.27    %01%      최초작성
 */


package com.uc.bs.cyber.batch.txbt5000;

import java.util.ArrayList;

import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.dao.DuplicateKeyException;

import com.uc.bs.cyber.daemon.Codm_BaseProcess;
import com.uc.bs.cyber.daemon.Codm_interface;
import com.uc.core.MapForm;
import com.uc.core.spring.service.UcContextHolder;

/**
 * @author Administrator
 *
 */
public class Txbt5000_process extends Codm_BaseProcess implements Codm_interface {

	private MapForm dataMap  = null;
	private int loop_cnt = 0, insert_cnt = 0, update_cnt = 0, del_cnt = 0;
	
	/**
	 * 
	 */
	public Txbt5000_process() {
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
		txbt5000_JobProcess();
	}


	/*트랜잭션을 실행하기 위한 함수.*/
	private void mainTransProcess() {
		
		try {
			
			setContext(appContext);
			setApp(appContext);

			UcContextHolder.setCustomerType(this.dataSource);
			
			dataMap = new MapForm();  								// 초기화
			dataMap.setMap("SGG_COD",  this.c_slf_org);  	// 구청코드 맵핑
			dataMap.setMap("TRN_YN", "0");            		   	// 미전송 분

			this.startJob();
						
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}			
	}
	
	/*자료 연계*/
	private int txbt5000_JobProcess() {
		
		/* Context 주입 및 연계DB설정 */
		this.context = appContext;
		UcContextHolder.setCustomerType(this.dataSource);
		
		log.info("=====================================================================");
		log.info("=" + this.getClass().getName()+ " txbt5000_JobProcess() [지방세 환급금 직권충당 수납처리 LOG] Start =");
		log.info("= govid["+this.govId+"], orgcd["+this.c_slf_org+"], orgcd["+this.c_slf_org_nm+"]");
		log.info("=====================================================================");
		
		int LevyCnt = 0;
		
		loop_cnt = 0; 
		insert_cnt = 0;
		update_cnt = 0;
		del_cnt = 0;
		
		
		MapForm mpSupplyingList = new MapForm();
		
		try {
			
			
			ArrayList<MapForm> alFixedLevyList =  govService.queryForList("TXBT5000.select_supplying_list", dataMap);
			
			LevyCnt = alFixedLevyList.size();
			
			if (LevyCnt  >  0) {
				
				
				for (int i = 0;  i < LevyCnt;  i++) {
					
					mpSupplyingList =  alFixedLevyList.get(i);

					if (mpSupplyingList == null  ||  mpSupplyingList.isEmpty()) {
						continue;
					}
					
					loop_cnt++;

					/* 삭제 처리 */
					if (mpSupplyingList.getMap("CHG_TYPE").equals("3")) {
						
						if (cyberService.queryForUpdate("TXBT5000.delete_tx4111_tb", mpSupplyingList) > 0) {
							
							del_cnt++;
							
						}
						
					} else {	
							
						try {
							cyberService.queryForInsert("TXBT5000.insert_tx4111_tb", mpSupplyingList);
								
								insert_cnt++;
								
							} catch (Exception e) {
								
								if (e instanceof DuplicateKeyException) {
									
									cyberService.queryForUpdate("TXBT5000.update_tx4111_tb", mpSupplyingList);
									
									update_cnt++;
									
								} else {
									
									log.info("오류발생데이터 = " + mpSupplyingList.getMaps());
									
									//e.printStackTrace();							
									//throw (RuntimeException) e;
									continue;
								}
							}
						}
						govService.queryForUpdate("TXBT5000.update_complete", mpSupplyingList);
						
				}
			}
				
		log.info("총건수= [" + loop_cnt + "]");
		
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return LevyCnt;
	}

}
