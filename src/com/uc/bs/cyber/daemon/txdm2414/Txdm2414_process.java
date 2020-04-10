/**
 *  주시스템명 : 부산시청 사이버 지방세청
 *  업  무  명 : 연계
 *  기  능  명 : 표준세외수입 수납자료전송
 *  클래스  ID : Txdm2414_process : 실 처리로직을 작성하는 클래스
 *  변경  이력 :
 * ------------------------------------------------------------------------
 *  작성자      	소속  		    일자            Tag           내용
 * ------------------------------------------------------------------------
 *  표승한       다산시스템      2013.12.13         %01%         최초작성
 */
package com.uc.bs.cyber.daemon.txdm2414;

import java.util.ArrayList;

import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;

import com.uc.bs.cyber.CbUtil;
import com.uc.bs.cyber.daemon.Codm_BaseProcess;
import com.uc.bs.cyber.daemon.Codm_interface;
import com.uc.core.MapForm;
import com.uc.core.spring.service.UcContextHolder;

public class Txdm2414_process extends Codm_BaseProcess implements Codm_interface {

	private MapForm dataMap            = null;
	private int insert_cnt = 0, update_cnt = 0, delete_cnt = 0, remote_up_cnt = 0;
    private int p_del_cnt = 0;
	
	/*트랜잭션을 위하여 */
	MapForm gblNidLevyRows    = null;
	
	/**
	 * 
	 */
	public Txdm2414_process() {
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
		log.info("=[표준세외수입-[" + c_slf_org_nm + "] 수납자료전송] Start =");
		log.info("=====================================================================");
		
		try {
				
			setContext(appContext);
			setApp(appContext);

			UcContextHolder.setCustomerType(this.dataSource);
						
			dataMap = new MapForm();  /*초기화*/
			
			log.info("this.c_slf_org ======================= [" + this.c_slf_org + "]");
			dataMap.setMap("SGG_COD", this.c_slf_org);
			
			do {
				
				int page = cyberService.getOneFieldInteger("TXDM2414.SELECT_TX2211_CNT", dataMap);
				
				log.info("[표준세외수입-[" + c_slf_org_nm + "]] CNT = [" + page + "]");
				
				if(page == 0) break;
				
				this.startJob();               /*멀티 트랜잭션 호출*/
				
				if(cyberService.getOneFieldInteger("TXDM2414.SELECT_TX2211_CNT", dataMap) == 0) {
					break;
				}
				
			}while(true);
					
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
		/*1. 수납자료 전송 업무 처리.                               */
		/*---------------------------------------------------------*/
		txdm2414_JobProcess();
	
	}
	
	
    /*표준세외수입...연계*/
	private int txdm2414_JobProcess() {
		
		log.info("=====================================================================");
		log.info("=" + this.getClass().getName()+ " txdm2414_JobProcess()[표준세외수입-[" + c_slf_org_nm + "] 수납자료 전송] Start =");
		log.info("= govid["+this.govId+"], orgcd["+this.c_slf_org+"]");
		log.info("=====================================================================");
		
		/*초기화*/
		
		insert_cnt = 0;
		update_cnt = 0; 
		delete_cnt = 0;
		remote_up_cnt = 0;
		p_del_cnt     = 0;
		
		/*전역 초기화*/
		gblNidLevyRows = new MapForm();

		int rec_cnt = 0;
		int nul_cnt = 0;
		
		int tot_size = 0;
		
		long elapseTime1 = 0;
		long elapseTime2 = 0;
		
		elapseTime1 = System.currentTimeMillis();

		/*---------------------------------------------------------*/
		/*1. 수납자료 전송 업무 처리.                               */
		/*---------------------------------------------------------*/
		
		try {

			/*연계테이블 부과자료 SELECT 쿼리(NIS)*/
			ArrayList<MapForm> alNidLevyList =  cyberService.queryForList("TXDM2414.SELECT_TX2211_LIST", dataMap);
			
			tot_size = alNidLevyList.size();
			
		    log.info("[" + c_slf_org_nm + "]세외수입수납자료전송 건수 = [" + tot_size + "]");
			
			if (tot_size  >  0)   {
				
				/*전자납부자료 1건씩 fetch 처리 */
				for ( rec_cnt = 0;  rec_cnt < tot_size;  rec_cnt++)   {
					
					gblNidLevyRows =  alNidLevyList.get(rec_cnt);
					
					/*혹시나...because of testing */
					if (gblNidLevyRows == null  ||  gblNidLevyRows.isEmpty() )   {
						nul_cnt++;
						continue;
					}
					if(govService.getOneFieldInteger("TXDM2414.SELECT_RISTNACOCR_CNT", gblNidLevyRows)>0){
						gblNidLevyRows.setMap("TRTG","5");
					}else{
						try{
							govService.queryForUpdate("TXDM2414.INSERT_RISTNACOCR", gblNidLevyRows);
							insert_cnt++;
							gblNidLevyRows.setMap("TRTG","1");
						}catch (Exception sub_e){
							log.error("UPDATE_DEL_INFO_DETAIL 오류데이터 = " + gblNidLevyRows.getMaps());
							sub_e.printStackTrace();
						}						
					}
					
					try{
						/*원장 업데이트*/
						remote_up_cnt +=cyberService.queryForUpdate("TXDM2414.UPDATE_TX2211_TRTG", gblNidLevyRows);
					}catch (Exception sub_e){
						log.error("UPDATE_O_ORG_TBL 오류데이터 = " + gblNidLevyRows.getMaps());
						sub_e.printStackTrace();
					}
					
					if(rec_cnt % 500 == 0) {
						log.info("[H][" + this.c_slf_org_nm + "][" + this.c_slf_org + "]세외수입 수납자료 전송건수 = [" + rec_cnt + "]");
					}
				}
				
				log.info("[" + c_slf_org_nm + "]세외수입수납자료 전송 건수 [" + rec_cnt + "] 등록건수 [" + insert_cnt +"] 수정건수 [" + update_cnt + "] 삭제건수 [" + delete_cnt + "] 물리적삭제 [" + p_del_cnt + "]");
				log.info("원장업데이트 ["+remote_up_cnt+"], 미처리 부과건수 [" + nul_cnt + "]");
			}

			elapseTime2 = System.currentTimeMillis();
			
			log.info("[" + c_slf_org_nm + "]세외수입 수납자료전송 시간("+tot_size+") : " + CbUtil.formatTime(elapseTime2 - elapseTime1));
					
		} catch (Exception e) {
		    log.error("SELECT_O_LEVY_LIST 오류 데이터 = " + gblNidLevyRows.getMaps());
			throw (RuntimeException) e;
		}
		
		return tot_size;
	}
	
}
