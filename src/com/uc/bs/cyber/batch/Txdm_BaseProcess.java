/**
 *  �ֽý��۸� : ���̹����漼û ��ȭ
 *  ��  ��  �� : ����
 *  ��  ��  �� : ���̹����漼û ���� ��ġ ����(����ó����)
 *  Ŭ����  ID : Txdm_BaseProcess
 *  ����  �̷� :
 * ------------------------------------------------------------------------
 *  �ۼ���      	�Ҽ�  		    ����            Tag           ����
 * ------------------------------------------------------------------------
 *  Administrator   u c         2011.04.24         %01%         �����ۼ�
 */
package com.uc.bs.cyber.batch;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;

import com.uc.core.spring.service.IbatisBaseService;
import com.uc.core.spring.service.TransactionJob;
import com.uc.bs.cyber.CbUtil;
import com.uc.bs.cyber.daemon.Codm_interface;

/**
 * @author Administrator
 *
 */
public abstract class Txdm_BaseProcess extends TransactionJob implements Codm_interface, Runnable {
	
	protected Log log = LogFactory.getLog(this.getClass());
	
	protected ApplicationContext appContext = null;  // Spring Context
	
	protected IbatisBaseService cyberService = null;	 // �⺻ DB �۾��� ���̹���û ����
	
	protected IbatisBaseService govService  = null;	     // �⺻ DB �۾��� ��ġ��ü ����
	
	protected String dataSource = null;	    // ��ġ��ü�ڵ� 
	
	protected String c_slf_org = null;	    // ��ġ��ü�ڵ� 

	protected String c_slf_org_nm  = null;	// ��ġ��ü�� 
	
	protected String govId  = null;		    // ��ġ��ü ����ID
	
	
	/**
	 * ������
	 */
	public Txdm_BaseProcess() {
		// TODO Auto-generated constructor stub
		super();
		log = LogFactory.getLog(this.getClass());
	}
	
	
	/*��û�� DB������ �����ϱ� ����...*/
	public void setDataSource(String dataSource) {
		this.dataSource = dataSource;
	}
	
	
	/**
	 * @return the govId
	 */
	public String getGovId() {
		return govId;
	}

	/**
	 * @param govId the govId to set
	 */
	public void setGovId(String govId) {
		this.govId = govId;
	}
	/*interface ����*/
	@Override 
	public ApplicationContext getContext() {
		// TODO Auto-generated method stub
		return appContext ;
	}
	
	/*interface �ż��� ����*/
	@Override
	public void setContext(ApplicationContext context) {
		// TODO Auto-generated method stub
		appContext = context;
	}
	
	/*interface ����*/
	@Override
	public Object getService(String beanName) {
		// TODO Auto-generated method stub
		return appContext.getBean(beanName);
	}
	
	
	/*interface ����*/
	@Override
	public void initProcess() throws Exception {
		// TODO Auto-generated method stub
		
		log.debug("=====================================================================");
		log.debug("== " + this.getClass().getName() +  " initProcess() ==");
		log.debug("=====================================================================");
		
        c_slf_org = CbUtil.getResource("ApplicationResource", "cyber."  + govId + ".org_cd");	  // ��ġ��ü�ڵ�
		
		c_slf_org_nm  = CbUtil.getResource("ApplicationResource", "cyber."  + govId + ".org_nm"); // ��ġ��ü��
		
		cyberService = (IbatisBaseService) getService("baseService_cyber");	// IBATIS Service ��� (�⺻ �������� ���)
		
		govService = (IbatisBaseService) getService("baseService");	        // ����ü ����� IbatisService Service
		
	}
	

	/*�߻�޼��� ���� : ��û�� ���������� ���ؼ�*/
	public abstract void setDatasrc(String datasrc);
	
	/* �߻�޼��� ����*/
	public abstract void runProcess() throws Exception;

	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			
			initProcess();
			
			runProcess();
			
		} catch (InterruptedException e1) {
			// e1.printStackTrace();

		} catch (Exception e) {
			
			e.printStackTrace();
			
		}
		
	}

	
}
