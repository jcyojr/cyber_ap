/**
 *  �ֽý��۸� : ���̹����漼û ��ȭ
 *  ��  ��  �� : ����
 *  ��  ��  �� : ���̹����漼û ���� ��ġ ����(����ó����)
 *  Ŭ����  ID : Txdm_BatchProcess
 *  ����  �̷� :
 * ------------------------------------------------------------------------
 *  �ۼ���      	�Ҽ�  		    ����            Tag           ����
 * ------------------------------------------------------------------------
 *  �۵���         ��ä��(��)    2011.05.24         %01%         �����ۼ�
 */
package com.uc.bs.cyber.batch;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;

import com.uc.bs.cyber.CbUtil;
import com.uc.bs.cyber.daemon.Codm_interface;
import com.uc.core.MapForm;
import com.uc.core.spring.service.IbatisBaseService;
import com.uc.core.spring.service.TransactionJob;

/**
 * @author Administrator
 *
 */
public abstract class Txdm_BatchProcess extends TransactionJob implements Codm_interface, Runnable {
	
	protected Log log = LogFactory.getLog(this.getClass());
	
	protected ApplicationContext appContext = null;  // Spring Context
	
	protected IbatisBaseService cyberService = null;	 // �⺻ DB �۾��� ���̹���û ����
	
	protected IbatisBaseService govService  = null;	     // �⺻ DB �۾��� ��ġ��ü ����
	
	protected String dataSource = null;	    // ��ġ��ü�ڵ� 
	
	protected String c_slf_org = null;	    // ��ġ��ü�ڵ� 

	protected String c_slf_org_nm  = null;	// ��ġ��ü�� 
	
	protected String govId  = null;		    // ��ġ��ü ����ID
	
	protected MapForm sysMap = new MapForm();
	
	protected String scg_code    = "000";
	protected String procName    = "";
	protected String procId      = "";
	protected String threadName  = "";
	
	/**
	 * ������
	 */
	public Txdm_BatchProcess() {
		// TODO Auto-generated constructor stub
		super();
		log = LogFactory.getLog(this.getClass());
	}

	/**
	 * @param ��û�ڵ� the scg_code to set
	 */
	public void setScg_code(String scg_code) {
		this.scg_code = scg_code;
	}
	
	public void setProcess(String id, String name, String thr_name) {
		procId = id; procName  = name; threadName = thr_name;
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
		this.appContext = context;
	}
	
	/*interface ����*/
	@Override
	public Object getService(String beanName) {
		// TODO Auto-generated method stub
		return appContext.getBean(beanName);
	}
	/*interface ����*/
	@Override
	public abstract void initProcess() throws Exception;
	
	/*�߻�޼��� ���� : ��û�� ���������� ���ؼ�*/
	public abstract void setDatasrc(String datasrc);
	
	/* �߻�޼��� ����*/
	public abstract void runProcess() throws Exception;
	
	public IbatisBaseService getService(){
		return (IbatisBaseService) appContext.getBean("baseService_cyber");
	}
	
	/* ������ ����*/
	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			
			sysMap.setMap("SGG_COD"      , scg_code);                             /*��û�ڵ� : �λ��(626), ���̹���û(000)*/
			sysMap.setMap("PROCESS_ID"   , procId);                               /*���μ��� ID*/
			sysMap.setMap("PROCESS_NM"   , procName);                             /*���μ��� ��*/
			sysMap.setMap("THREAD_NM"    , threadName);                           /*������ ��*/
			sysMap.setMap("BIGO"         , CbUtil.getServerIp());                 /*��� : ���༭��IP�� �����Ѵ�. */
			sysMap.setMap("MANE_STAT"    , "1");                                  /*�⵿����*/
			sysMap.setMap("MANAGE_CD"    , "2");                                  /*�������� 1 : ���� 2: ��ġ*/
			sysMap.setMap("STT_DT"       , CbUtil.getCurrent("yyyyMMddHHmmss"));  /*�����Ͻ� */
			sysMap.setMap("END_DT"       , "");                                   /*�����Ͻ�*/
			sysMap.setMap("REG_DTM"      , CbUtil.getCurrent("yyyyMMddHHmmss"));  /*����Ͻ� : ó���ѹ���...*/
			sysMap.setMap("LAST_DTM"     , CbUtil.getCurrent("yyyyMMddHHmmss"));  /*���������Ͻ�*/
			sysMap.setMap("LAST_TIMEMIL" , System.currentTimeMillis());
			
			/**
			 * ����������� ���
			 * TEST�� ��츸 ����
			 */
			try{
				if(this.getService().queryForUpdate("CODMBASE.CODMSYSTUpdate", sysMap) < 1) {
					this.getService().queryForInsert("CODMBASE.CODMSYSTInsert", sysMap);
				}
			}catch (Exception e) {
				e.printStackTrace();
			}
			
			initProcess();
			
			runProcess();
			
			Thread.sleep(1000);
			
			sysMap.setMap("END_DT"        , CbUtil.getCurrent("yyyyMMddHHmmss"));  /*�����Ͻ�*/
			sysMap.setMap("LAST_DTM"      , CbUtil.getCurrent("yyyyMMddHHmmss"));
			sysMap.setMap("LAST_TIMEMIL"  , System.currentTimeMillis());
			
			/*���� ������¸� ������Ʈ �Ѵ�...*/
			this.getService().queryForUpdate("CODMBASE.CODMSYSTUpdate", sysMap);
				
		} catch (Exception e) {
			
			e.printStackTrace();
			
		}
		
		log.info("================================================");
		log.info("== " + Thread.currentThread().getName() + " PROCESS Terminated!!");
		log.info("================================================");
	}

}
