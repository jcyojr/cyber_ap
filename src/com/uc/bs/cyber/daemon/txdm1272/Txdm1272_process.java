/**
 *  주시스템명 : 부산시청 사이버 지방세청
 *  업  무  명 : 연계
 *  기  능  명 : 수기고지서 주민세 종합소득할 수납내역전송 (구청별) 처리 업무
 *  클래스  ID : Txdm1272_process : 실 처리로직을 작성하는 클래스
 *  변경  이력 :
 * ------------------------------------------------------------------------
 *  작성자      	소속  		    일자            Tag           내용
 * ------------------------------------------------------------------------
 *  송동욱       유채널(주)      2011.10.17         %01%         최초작성
 */
package com.uc.bs.cyber.daemon.txdm1272;

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
public class Txdm1272_process extends Codm_BaseProcess implements Codm_interface {

	private MapForm dataMap  = null;
	private int loop_cnt = 0, insert_cnt = 0, update_cnt = 0;
	
	/**
	 * 
	 */
	public Txdm1272_process() {
		// TODO Auto-generated constructor stub
		super();
		
		log = LogFactory.getLog(this.getClass());
		
		loopTerm = 300;    /*5분단위로...*/
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
		
		/*수기고지서 소인파일 입력 및 구청별 등록*/
		txdm1272_JobProcess();
	}


	/*트랜잭션을 실행하기 위한 함수.*/
	private void mainTransProcess(){

		setContext(appContext);
		setApp(appContext);

		UcContextHolder.setCustomerType(this.dataSource);
		
		try {
						
			dataMap = new MapForm();  /*초기화*/

			dataMap.setMap("PAGE_PER_CNT"   ,  1000);           /*페이지당 갯수*/
			dataMap.setMap("rowcnt"         ,  10000);
			dataMap.setMap("sido_cod"       ,  "26");
			dataMap.setMap("sgg_cod"        ,  c_slf_org);
			
			do {
				
				int page = cyberService.getOneFieldInteger("TXDM1272.tx1612_tb_select_page_cnt", dataMap);
				
				log.info("[주민세 종합소득할] PAGE_PER_CNT(" + dataMap.getMap("PAGE_PER_CNT") + ") 당 Page갯수 =" + page);
				
				if(page == 0) break;
				
				for(int i = 1 ; i <= page ; i ++) {
					
					dataMap.setMap("PAGE",  i);    /*처리페이지*/
					this.startJob();               /*멀티 트랜잭션 호출*/
				}
				
				if(cyberService.getOneFieldInteger("TXDM1272.tx1612_tb_select_page_cnt", dataMap) == 0) {
					break;
				}
				
			}while(true);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}			
		
	}
	
	/*파싱한 자료를 사이버DB에 입력*/
	private void txdm1272_JobProcess() {
		
		log.info("=====================================================================");
		log.info("=" + this.getClass().getName()+ " txdm1272_JobProcess() [수기고지서 주민세 종합소득할 수납내역전송] Start =");
		log.info("= govid["+this.govId+"], orgcd["+this.c_slf_org+"], orgcd["+this.c_slf_org_nm+"]");
		log.info("=====================================================================");
				
		long elapseTime1 = 0;
		long elapseTime2 = 0;
		
		int LevyCnt = 0;
		int rec_cnt = 0;
		
		loop_cnt = 0; 
		insert_cnt = 0;
		update_cnt = 0;
		
		MapForm mp_tx1613_tb_List = new MapForm();
		
		try {
		
			dataMap.setMap("sgg_cod"  ,  this.c_slf_org);
			dataMap.setMap("sido_cod" ,  "26");
			dataMap.setMap("tr_tg"    ,  "0");
			
			
			elapseTime1 = System.currentTimeMillis();
			
			/*연계테이블 부과자료 SELECT 쿼리(NIS)*/
			ArrayList<MapForm> alSpc_tx1613_List =  cyberService.queryForList("TXDM1272.tx1613_tb_select_list", dataMap);
			
			LevyCnt = alSpc_tx1613_List.size();

			if (LevyCnt  >  0)   {
				
				for ( rec_cnt = 0;  rec_cnt < LevyCnt;  rec_cnt++)   {
					
					mp_tx1613_tb_List = alSpc_tx1613_List.get(rec_cnt);
					
					/*혹시나...because of testing */
					if (mp_tx1613_tb_List == null  ||  mp_tx1613_tb_List.isEmpty() )   {
						continue;
					}
					
					try {

						if(govService.getOneFieldInteger("TXDM1272.scyb503_select_cnt", mp_tx1613_tb_List) == 0 ) {
						
							mp_tx1613_tb_List.setMap("REG_YY",     mp_tx1613_tb_List.getMap("PAY_DT").toString().substring(0, 4));
							mp_tx1613_tb_List.setMap("SND_IP",     "127.0.0.1");
							mp_tx1613_tb_List.setMap("TRN_YN",     "0");
							mp_tx1613_tb_List.setMap("RPT_REG_NO", "");
							mp_tx1613_tb_List.setMap("RPT_NM",     "");
							mp_tx1613_tb_List.setMap("RPT_TEL",    "");
							mp_tx1613_tb_List.setMap("RPT_ID",     "");
							
							govService.queryForUpdate("TXDM1272.scyb503_insert_receipt", mp_tx1613_tb_List);
							
							insert_cnt++;
							
						} else {
							update_cnt++;
						}
						
					}catch (Exception e){
						
						/*중복이 발생하거나 제약조건에 어긋나는 경우체크*/
						if (e instanceof DuplicateKeyException){

						}else{						
							loop_cnt++;
							throw (RuntimeException) e;
						}
						
					}
					
				}
				
			}
			
			elapseTime2 = System.currentTimeMillis();
			
			log.info("주민세 종합소득할 수납내역전송(" + c_slf_org_nm + ") 자료연계 건수::" + rec_cnt + ", 등록건수::" + insert_cnt +", 기등록건수::" + update_cnt);
			log.info("주민세 종합소득할 수납내역전송(" + c_slf_org_nm + ") 자료연계 시간::" + CbUtil.formatTime(elapseTime2 - elapseTime1));
			
		}catch (Exception e){
			e.printStackTrace();
			throw (RuntimeException) e;
		}
				
	}

}
