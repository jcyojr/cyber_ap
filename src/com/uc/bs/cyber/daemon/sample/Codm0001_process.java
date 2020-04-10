/**
 *  �ֽý��۸� : �λ��û ���̹� ���漼û
 *  ��  ��  �� : ����
 *  ��  ��  �� : DB�� �о ������ �����ϰ� ��Ƽ�� ��� ����
 *  Ŭ����  ID : Codm0001_process : �� ó�������� �ۼ��ϴ� Ŭ����
 *  ����  �̷� :
 * ------------------------------------------------------------------------
 *  �ۼ���      	�Ҽ�  		    ����            Tag           ����
 * ------------------------------------------------------------------------
 *  ����       ��ä��(��)      2010.11.30         %01%         �����ۼ�
 *  �۵���       ��ä��(��)      2011.04.27         %01%         ����賦
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
		 * 5 �и��� ����
		 */
		loopTerm = 300;
		
		// TODO Auto-generated constructor stub
	}

	/* (Codm_BaseProcess)�߻�޼��� �籸��*/
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
				newList.setMap("COL1", "�׽�Ʈ");
				newList.setMap("COL2", "�̰͵��׽�Ʈ");
				newList.setMap("COL3", "���͵��׽�Ʈ");
				newList.setMap("CLO4", "�̰Ÿ����Ÿ����Ÿ�");
				
				cyberService.queryForInsert("CODM0001.INSERT_TR_INCREASE", newList);
			}
			
			for(int i=0; i<scybCnt; i++) {
				
				try {
					
					MapForm max_value = cyberService.queryForMap("CODM0001.MAX_VALUE", null);
					
					MapForm sbList = scybList.get(i);
					
					sbList.setMap("SEQ", max_value.getMap("SEQ"));
					
					cyberService.queryForInsert("CODM0001.INSERT_TR_INCREASE", sbList);
					
					/**
					 * �Ʒ��� �������� �߰��� ��� �� �� �ֽ��ϴ�.
					 */
					//  this.etaxSqlMap;
					//  this.govSqlMap;
					//  this.c_slf_org;
					//  this.c_slf_org_nm;
					
					insertCnt ++;
					
					log.debug("=====���ߵ��ƶ� = " + i);
					
				} catch (DataIntegrityViolationException de) {	// Duplicate �� �� ��� UPDATE �� �ض�
	
					updateCnt += cyberService.queryForUpdate("CODM0001.UPDATE_TR_INCREASE", scybList.get(i));
				}
				
			}
			
			//���⼭ ���ʹ� Ʈ����� �׽�Ʈ��...
			mainTransProcess();
			
			
		} catch (CannotGetJdbcConnectionException e) {	// DB ������ �ȵǴ� ��� Thread �� �����ϰ� �ٽ� �����ϵ��� �Ѵ�
			// TODO Auto-generated catch block
			
			log.error("========= CONNECTION ERROR!!" );
			log.error(e.getMessage());
			
			throw new InterruptedException();
			
		}
		
		log.info("======================================================================");
		log.info("==   �׽�Ʈ ���=" + insertCnt + ", ������Ʈ=" + updateCnt + "   ==");
		log.info("======================================================================");
				
	}

	public void setApp(ApplicationContext context) {
		
		this.context = context;

	}
	
	/*Ʈ������� �����ϱ� ���� �Լ�.*/
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
	 * Ʈ����� ������ ����ϴ� �Լ�.
	 * TransactionJob.class�� interface ����
	 * */
	@Override
	public void transactionJob() {
		// TODO Auto-generated method stub
		log.debug("=====================================================================");
		log.debug("=" + this.getClass().getName()+ " transactionJob() Start =");
		log.debug("=====================================================================");
		// Ʈ����� �׽�Ʈ�� ���� �����Դϴ�.
		// Ʈ������� �ݵ�� ���⼭ �����Ͻñ� �ٶ��ϴ�.
		
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
