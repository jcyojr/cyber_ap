/**
 *  주시스템명 : 부산시청 사이버 지방세청
 *  업  무  명 : 연계
 *  기  능  명 : 수기입력용사업장자정보수신 
 *  클래스  ID : txdm1174_process : 실 처리로직을 작성하는 클래스
 *  변경  이력 :
 * ------------------------------------------------------------------------
 *  작성자      	소속  		    일자            Tag           내용
 * ------------------------------------------------------------------------
 *  천혜정       유채널(주)      2012.02.28         %01%         최초작성
 */
package com.uc.bs.cyber.daemon.txdm1174;

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
 */
public class Txdm1174_process extends Codm_BaseProcess implements Codm_interface {

	private MapForm dataMap            = null;
	private int insert_cnt = 0, update_cnt = 0, delete_cnt = 0, remote_up_cnt = 0;
    private int p_del_cnt = 0;
	
	/*트랜잭션을 위하여 */
	MapForm gblNidLevyRows    = null;
	
	/**
	 * 
	 */
	public Txdm1174_process() {
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
		log.info("=[수기 위택스종소세정보 구축-구청 자료연계] Start =");
		log.info("=====================================================================");
		
		/* * 
		 * */
		
		try {
				
			setContext(appContext);
			setApp(appContext);

			UcContextHolder.setCustomerType(this.dataSource);
						
			dataMap = new MapForm();  /*초기화*/

			dataMap.setMap("PAGE_PER_CNT"   ,  500);            /*페이지당 갯수*/
			
			do {
				
				int page = govService.getOneFieldInteger("txdm1174.txdm1174_select_count_page", dataMap);
				
				log.info("[수기입력용사업장자정보-구청] PAGE_PER_CNT(" + dataMap.getMap("PAGE_PER_CNT") + ") 당 Page갯수 =" + page);
				
				if(page == 0) break;
				
				for(int i = 1 ; i <= page ; i ++) {
					
					dataMap.setMap("PAGE",  i);    /*처리페이지*/
					this.startJob();               /*멀티 트랜잭션 호출*/
				}
				
				break;
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
		/*1. 부가연계자료 업무 처리.                               */
		/*---------------------------------------------------------*/
		txdm1174_JobProcess();
	
	}
	
	
    /*수기입력용사업자정보수신...연계(구청)*/
	private int txdm1174_JobProcess() {
		
		log.info("=====================================================================");
		log.info("=" + this.getClass().getName()+ " txdm1174_JobProcess()[수기 위택스종소세정보 구축-구청 자료연계] Start =");
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

		/*트랜잭션을 포함하는 실업무는 여기서 구현한다...*/
		/*실업무를 구현합니다.*/
		
		int rec_cnt = 0;
		int nul_cnt = 0;
		
		int tot_size = 0;
		
		long elapseTime1 = 0;
		long elapseTime2 = 0;
		
		elapseTime1 = System.currentTimeMillis();

		/*---------------------------------------------------------*/
		/*1. 부가연계자료 업무 처리.                               */
		/*---------------------------------------------------------*/
		
		try {

			dataMap.setMap("SGG_COD",  this.c_slf_org);

			/*연계테이블 부과자료 SELECT 쿼리(TAX)*/
			ArrayList<MapForm> alNidLevyList =  govService.queryForList("txdm1174.txdm1174_select_list", dataMap);
			
			tot_size = alNidLevyList.size();
			
		    log.info("수기 위택스종소세정보 구축 연계자료 건수 = [" + tot_size + "]");
			
			if (tot_size  >  0)   {
				
				/*전자납부자료 1건씩 fetch 처리 */
				for ( rec_cnt = 0;  rec_cnt < tot_size;  rec_cnt++)   {

					gblNidLevyRows =  alNidLevyList.get(rec_cnt);
					
					/*혹시나...because of testing */
					if (gblNidLevyRows == null  ||  gblNidLevyRows.isEmpty() )   {
						nul_cnt++;
						continue;
					}
					
					/*데이터가 없으면 인서트 잇으면 업데이트*/ 
					
					try{
						cyberService.queryForInsert("txdm1174.txdm1174_insert_tx1604_tb", gblNidLevyRows);
						insert_cnt++;

					 } catch (Exception e) {
						
							/*중복이 발생하거나 제약조건에 어긋나는 경우체크 : sgg_cod , reg_no 가 중복*/
							if (e instanceof DuplicateKeyException){	// 기본키가 중복되서 예외처리된경우 
								
								try {
									cyberService.queryForInsert("txdm1174.txdm1174_update_tx1604_tb", gblNidLevyRows);
									update_cnt++;
									
								} catch (Exception be) {

									// TODO Auto-generated catch block
									be.printStackTrace();
								}
						}else{
							log.error("오류데이터 = " + gblNidLevyRows.getMaps());
							log.info(e.getMessage());
							e.printStackTrace();							
							throw (RuntimeException) e;
						}

//						remote_up_cnt+=	cyberService.queryForUpdate("txdm1174.txdm3141_update_state1", gblNidLevyRows);
					
					 }
				}
			}
				
				log.info("수기위택스종소세정보 자료연계 건수 [" + rec_cnt + "] 등록건수 [" + insert_cnt +"] 수정건수 [" + update_cnt + "] 삭제건수 [" + delete_cnt + "] 물리적삭제 [" + p_del_cnt + "]");
				log.info("수기 위택스종소세정보 ["+remote_up_cnt+"], 미처리 부과건수 [" + nul_cnt + "]");

			elapseTime2 = System.currentTimeMillis();
			
			log.info("위택스종소세정보 부과자료연계 시간("+tot_size+") : " + CbUtil.formatTime(elapseTime2 - elapseTime1));
					
		} catch (Exception e) {
		    log.error("오류 데이터 = " + gblNidLevyRows.getMaps());
			throw (RuntimeException) e;
		}
		
		return tot_size;
	}
	

}
