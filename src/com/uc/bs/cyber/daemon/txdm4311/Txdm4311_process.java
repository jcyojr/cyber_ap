/**
 *  주시스템명 : 부산시청 사이버 지방세청
 *  업  무  명 : 연계
 *  기  능  명 : 상하수도 정기분 부가자표 연계(시도) 업무
 *  클래스  ID : Txdm4311_process : 페이지별로 쓰레드를 할당한다.
 *  변경  이력 :
 * ------------------------------------------------------------------------
 *  작성자      	소속  		    일자            Tag           내용
 * ------------------------------------------------------------------------
 *  송동욱       유채널(주)      2011.10.12         %01%         최초작성
 */
package com.uc.bs.cyber.daemon.txdm4311;

import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;

import com.uc.bs.cyber.daemon.Codm_BaseProcess;
import com.uc.bs.cyber.daemon.Codm_interface;
import com.uc.core.MapForm;
import com.uc.core.spring.service.UcContextHolder;

/**
 * @author Administrator
 *
 */
public class Txdm4311_process extends Codm_BaseProcess implements Codm_interface {

	protected Thread[] sub_threadList = null;
	
	private MapForm dataMap  = null;
	
	private boolean sub_thr_yn = false;
	
	/**
	 * 
	 */
	public Txdm4311_process() {
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
		
		/*멀티 스레드 시작*/
		subThreadProcess();
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

	}

	/*
	 * 멀티 쓰레드 할당 프로세스.
	 * 
	 * 처음 실행시 페이지 갯수 만큼 멀티쓰레드를 할당한다...
	 * 그 다음 10초에 한번씩 조회하고 이미 할당된 쓰레드가 없으면
	 * 다시 순차적으로 할당할 수 있도록하여 과도한 쓰레드 할당을 방지한다.
	 * 
	 * */
	private void subThreadProcess(){
		
		try {
		
			int idx = 0;
			
			setContext(appContext);
			setApp(appContext);

			UcContextHolder.setCustomerType(this.dataSource);
			
			dataMap = new MapForm();  /*초기화*/
			dataMap.setMap("PAGE_PER_CNT" , 1000);            /*페이지당 갯수*/
			dataMap.setMap("SGG_COD"      , this.c_slf_org);  /*구청코드 맵핑*/
			dataMap.setMap("trn_yn"       , '0');             /*미전송 분    */
			
			/**
			 * 주의사항은 THREAD를 10개 이상이 넘지 않도록 쿼리에서 미리 방지한다.
			 */
			
			do {
								
				int page = govService.getOneFieldInteger("TXDM4311.select_count_page", dataMap);
				
				log.info("[상하수도 정기분 부가자료 (" + c_slf_org_nm + ")] PAGE_PER_CNT(" + dataMap.getMap("PAGE_PER_CNT") + ") 당 Page갯수 =" + page);
				
				if(page == 0) break;
				
				for(int i = 1 ; i <= page ; i ++) {

					dataMap.setMap("PAGE",  i);    /*처리페이지*/
					
					if (sub_thr_yn) {
						
						if(!sub_threadList[i].isAlive()) {
							sub_threadList[i] = new Thread(newSubProcess(String.valueOf(jobId), dataSource, i), idx + "_sub_4311_plus_thr_" + i);			
							sub_threadList[i].start();
						}
						
					} else {
						/*페이지 처리는 싱글 클래스로 실행*/
						newSubProcess(String.valueOf(jobId), dataSource, i);
					}
					
				}
				
				idx++;

				if (!sub_thr_yn) {
				
					/*부하 때문에 10초에 한번씩 조회하고*/
					if(govService.getOneFieldInteger("TXDM4311.select_count_page", dataMap) == 0) {
						break;
					}
				} else {
					break;
				}
				

			}while(true);
						
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}			

	}
	
	/**
	 * @param govId
	 * @return
	 * @throws Exception
	 * 메인 로직...을 호출한다.
	 */
	private Txdm4311_sub_process newSubProcess(String govId, String orgcd, int seq) throws Exception {
		
		Txdm4311_sub_process process = new Txdm4311_sub_process();
		
		process.setContext(appContext);
		process.setGovId(govId);
		process.setDataSource(orgcd);
		process.setSeq(seq);
		
		process.initProcess();		
		
		return process;
	}

}
