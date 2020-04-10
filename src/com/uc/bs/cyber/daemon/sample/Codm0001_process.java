/**
 *  주시스템명 : 부산시청 사이버 지방세청
 *  업  무  명 : 공통
 *  기  능  명 : DB를 읽어서 전문을 생성하고 멀티로 쏘는 샘플
 *  클래스  ID : Codm0001_process : 실 처리로직을 작성하는 클래스
 *  변경  이력 :
 * ------------------------------------------------------------------------
 *  작성자      	소속  		    일자            Tag           내용
 * ------------------------------------------------------------------------
 *  김대완       유채널(주)      2010.11.30         %01%         최초작성
 *  송동욱       유채널(주)      2011.04.27         %01%         보고배낌
 */

package com.uc.bs.cyber.daemon.sample;

import java.io.IOException;
import java.util.ArrayList;

import org.springframework.context.ApplicationContext;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.CannotGetJdbcConnectionException;

import com.uc.core.MapForm;
import com.uc.core.spring.service.IbatisBaseService;
import com.uc.core.spring.service.UcContextHolder;

import com.uc.bs.cyber.daemon.Codm_interface;
import com.uc.bs.cyber.daemon.Codm_BaseProcess;

public class Codm0001_process extends Codm_BaseProcess implements Codm_interface {
	
	public Codm0001_process() throws IOException, Exception {
		
		super();
		
		/**
		 * 5 분마다 돈다
		 */
		loopTerm = 300;
		
		// TODO Auto-generated constructor stub
	}

	/* (Codm_BaseProcess)추상메서드 재구현*/
	@Override
	public void runProcess() throws Exception {
		// TODO Auto-generated method stub
		
		log.debug("=====================================================================");
		log.debug("== " + this.getClass().getName()+ "runProcess() ==");
		log.debug("=====================================================================");
		
		int insertCnt = 0, updateCnt = 0;
		
		try {
			ArrayList<MapForm> scybList = cyberService.queryForList("CODM0001.SELECT_TR_INCREASE", null);			
			
			long scybCnt = scybList.size();
			
			if(scybCnt == 0) {
				
				MapForm newList = null;
				
				newList = new MapForm();
				newList.setMap("SEQ" , "1");
				newList.setMap("COL1", "테스트");
				newList.setMap("COL2", "이것도테스트");
				newList.setMap("COL3", "저것도테스트");
				newList.setMap("CLO4", "이거리저거리깨거리");
				
				cyberService.queryForInsert("CODM0001.INSERT_TR_INCREASE", newList);
			}
			
			for(int i=0; i<scybCnt; i++) {
				
				try {
					
					MapForm max_value = cyberService.queryForMap("CODM0001.MAX_VALUE", null);
					
					MapForm sbList = scybList.get(i);
					
					sbList.setMap("SEQ", max_value.getMap("SEQ"));
					
					cyberService.queryForInsert("CODM0001.INSERT_TR_INCREASE", sbList);
					
					/**
					 * 아래의 변수들을 추가로 사용 할 수 있습니다.
					 */
					//  this.etaxSqlMap;
					//  this.govSqlMap;
					//  this.c_slf_org;
					//  this.c_slf_org_nm;
					
					insertCnt ++;
					
					log.debug("=====쭉쭉돌아라 = " + i);
					
				} catch (DataIntegrityViolationException de) {	// Duplicate 가 난 경우 UPDATE 를 해라
	
					updateCnt += cyberService.queryForUpdate("CODM0001.UPDATE_TR_INCREASE", scybList.get(i));
				}
				
			}
			
			//여기서 부터는 트랜잭션 테스트다...
			mainTransProcess();
			
			
		} catch (CannotGetJdbcConnectionException e) {	// DB 연결이 안되는 경우 Thread 를 종료하고 다시 생성하도록 한다
			// TODO Auto-generated catch block
			
			log.error("========= CONNECTION ERROR!!" );
			log.error(e.getMessage());
			
			throw new InterruptedException();
			
		}
		
		log.info("======================================================================");
		log.info("==   테스트 등록=" + insertCnt + ", 업데이트=" + updateCnt + "   ==");
		log.info("======================================================================");
				
	}

	public void setApp(ApplicationContext context) {
		
		this.context = context;

	}
	
	/*트랜잭션을 실행하기 위한 함수.*/
	private void mainTransProcess(){
		
		Codm0001_process Codm0001_process;

		try {
			
			Codm0001_process = new Codm0001_process();
				
			Codm0001_process.setContext(appContext);
			Codm0001_process.setApp(appContext);

			UcContextHolder.setCustomerType("LT_etax");
			
			Codm0001_process.startJob();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}			
		
	}
	
	/*
	 * 트랜잭션 구현시 사용하는 함수.
	 * TransactionJob.class의 interface 구현
	 * */
	@Override
	public void transactionJob() {
		// TODO Auto-generated method stub
		log.debug("=====================================================================");
		log.debug("=" + this.getClass().getName()+ " transactionJob() Start =");
		log.debug("=====================================================================");
		// 트랜잭션 테스트에 대한 예제입니다.
		// 트랜잭션은 반드시 여기서 구현하시기 바랍니다.
		
		try {
			
			IbatisBaseService  sqlService  = (IbatisBaseService) this.context.getBean("baseService");	
			IbatisBaseService  cyberService  = (IbatisBaseService) this.context.getBean("baseService_cyber");
			
			ArrayList<MapForm>  yearList  =  sqlService.queryForList("Transaction.etax_Yearlist",  null);
			
			for (int i = 0;  i < yearList.size(); i++) {
				
				MapForm  yearMap  =  yearList.get(i);
				
				if (yearMap  ==  null  ||  yearMap.isEmpty() )  {
					continue;
				}
				
				ArrayList<MapForm>  cmdList  =  sqlService.queryForList("Transaction.etaxlist",  yearMap);
				
				System.out.println("cmdPayList.size() = " + cmdList.size());
				
				for (int j = 0; j < cmdList.size(); j++) {
					
					MapForm  cmdMap  =  cmdList.get(j);
					
					if (cmdMap  ==  null  ||  cmdMap.isEmpty() )  {
						continue;
					}
					
					if (cyberService.queryForUpdate("Transaction.etaxupdate",  cmdMap) < 0){
					    cyberService.queryForInsert("Transaction.etaxinsert",  cmdMap);
					}
					//if (i > 3) throw new RuntimeException();
				}

			}

			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}

	@Override
	public void setDatasrc(String datasrc) {
		// TODO Auto-generated method stub
		this.dataSource = datasrc;
	}
}
