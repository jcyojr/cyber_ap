/**
 *  �ֽý��۸� : �λ��û ���̹� ���漼û
 *  ��  ��  �� : ����
 *  ��  ��  �� : ���ϼ��� ����� �ΰ���ǥ ����(�õ�) ����
 *  Ŭ����  ID : Txdm4311_process : ���������� �����带 �Ҵ��Ѵ�.
 *  ����  �̷� :
 * ------------------------------------------------------------------------
 *  �ۼ���      	�Ҽ�  		    ����            Tag           ����
 * ------------------------------------------------------------------------
 *  �۵���       ��ä��(��)      2011.10.12         %01%         �����ۼ�
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

	/*Context ���Կ�*/
	public void setApp(ApplicationContext context) {
		this.context = context;
	}

	/*���μ��� ����*/
	@Override
	public void runProcess() throws Exception {
		// TODO Auto-generated method stub
		
		/*��Ƽ ������ ����*/
		subThreadProcess();
	}

	/*Context ���Կ�*/
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
	 * ��Ƽ ������ �Ҵ� ���μ���.
	 * 
	 * ó�� ����� ������ ���� ��ŭ ��Ƽ�����带 �Ҵ��Ѵ�...
	 * �� ���� 10�ʿ� �ѹ��� ��ȸ�ϰ� �̹� �Ҵ�� �����尡 ������
	 * �ٽ� ���������� �Ҵ��� �� �ֵ����Ͽ� ������ ������ �Ҵ��� �����Ѵ�.
	 * 
	 * */
	private void subThreadProcess(){
		
		try {
		
			int idx = 0;
			
			setContext(appContext);
			setApp(appContext);

			UcContextHolder.setCustomerType(this.dataSource);
			
			dataMap = new MapForm();  /*�ʱ�ȭ*/
			dataMap.setMap("PAGE_PER_CNT" , 1000);            /*�������� ����*/
			dataMap.setMap("SGG_COD"      , this.c_slf_org);  /*��û�ڵ� ����*/
			dataMap.setMap("trn_yn"       , '0');             /*������ ��    */
			
			/**
			 * ���ǻ����� THREAD�� 10�� �̻��� ���� �ʵ��� �������� �̸� �����Ѵ�.
			 */
			
			do {
								
				int page = govService.getOneFieldInteger("TXDM4311.select_count_page", dataMap);
				
				log.info("[���ϼ��� ����� �ΰ��ڷ� (" + c_slf_org_nm + ")] PAGE_PER_CNT(" + dataMap.getMap("PAGE_PER_CNT") + ") �� Page���� =" + page);
				
				if(page == 0) break;
				
				for(int i = 1 ; i <= page ; i ++) {

					dataMap.setMap("PAGE",  i);    /*ó��������*/
					
					if (sub_thr_yn) {
						
						if(!sub_threadList[i].isAlive()) {
							sub_threadList[i] = new Thread(newSubProcess(String.valueOf(jobId), dataSource, i), idx + "_sub_4311_plus_thr_" + i);			
							sub_threadList[i].start();
						}
						
					} else {
						/*������ ó���� �̱� Ŭ������ ����*/
						newSubProcess(String.valueOf(jobId), dataSource, i);
					}
					
				}
				
				idx++;

				if (!sub_thr_yn) {
				
					/*���� ������ 10�ʿ� �ѹ��� ��ȸ�ϰ�*/
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
	 * ���� ����...�� ȣ���Ѵ�.
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
