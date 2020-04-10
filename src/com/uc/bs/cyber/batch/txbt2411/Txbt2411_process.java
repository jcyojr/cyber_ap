/**
 *  �ֽý��۸� : �λ�� ���̹����漼û
 *  ��  ��  �� : ��ġ
 *  ��  ��  �� : ǥ�ؼ��ܼ��� �ΰ��ڷῬ��(��û)
 *               ��ġ�� �����Ǹ� ���� 9�ÿ� �ѹ��� 
 *  Ŭ����  ID : Txbt2411
 *  ����  �̷� : �̰� JDBC �� ���������ؼ� �󾴴�... ��û ����Ŭ ������ 8i
 *               ���� DB LINK�� ����ؼ� �϶�ĭ��.
 * ------------------------------------------------------------------------
 *  �ۼ���      	�Ҽ�  		    ����            Tag           ����
 * ------------------------------------------------------------------------
 *  �۵���       ��ä��(��)      2011.05.11         %01%         �����ۼ�
 */
package com.uc.bs.cyber.batch.txbt2411;

import java.util.ArrayList;
import java.util.Iterator;

import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.UncategorizedSQLException;

import com.uc.bs.cyber.CbUtil;
import com.uc.bs.cyber.batch.Txdm_BaseProcess;
import com.uc.bs.cyber.daemon.Codm_interface;
import com.uc.core.MapForm;
import com.uc.core.spring.service.UcContextHolder;


/**
 * @author Administrator
 *
 * ����) 20110926 ���ó���� �߻�
 * ����̶�
 *   ��û���� ���� ���ܼ����� �ΰ��ϰ�, ���̹���û���� �����Ͽ�
 *   ������ ��������
 *   �׷��� ��������� ���� ���� �� ���� ������ �߻��ϸ� ��û������
 *   �̰ǿ� ���ؼ� ���ó���ϰ� �ȴ�.
 *   �̷��� �Ǹ� ���̹���û������ �̰ǿ� ���ؼ� �����Ͽ� �ΰ����̺���
 *   ���� �Ͽ� �̰��� ���̻� ������ �������� �ʵ��� ó���ؾ� �Ѵ�. - ��Ģ��
 *   
 * ����ܿ� ������ ���
 *   ��û�� ���ܼ����� �ǽð����� �ڷḦ �������� �����Ƿ�
 *   �Ǻ��� ������ �߻��ϴ� ��� ���۾����� ó���ϵ��� ���������ν�
 *   �켱 ������� ó���ϰ� �ִ�. ü���� �������� - ��
 */
public class Txbt2411_process extends Txdm_BaseProcess implements Codm_interface {
	
	private MapForm dataMap  = null;
	private int intLog = 0;
	
	/**
	 * 
	 */
	public Txbt2411_process() {
		// TODO Auto-generated constructor stub
		super();
		
		log = LogFactory.getLog(this.getClass());

	}

	/*Context ���Կ�*/
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
			
		/*��������*/
		mainTransProcess();
		
	}

	/*Ʈ������� �����ϱ� ���� �Լ�.*/
	private void mainTransProcess(){
		
		try {
				
			setContext(appContext);
			setApp(appContext);

			UcContextHolder.setCustomerType(this.dataSource);
			
			//this.startJob();
			txbt2411_JobProcess();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}			
		
	}
	
	/*�� ����� ���� ����...*/
	@Override
	public void setDatasrc(String datasrc) {
		// TODO Auto-generated method stub
		this.dataSource = datasrc;
	}
	
	
	/*Ʈ����� ����*/
	@Override
	public void transactionJob() {
		// TODO Auto-generated method stub
		log.debug("=====================================================================");
		log.debug("=" + this.getClass().getName()+ " transactionJob() Start =");
		log.debug("=====================================================================");
		
		/*Ʈ������� �����ϴ� �Ǿ����� ���⼭ �����Ѵ�...*/
		/*�Ǿ����� �����մϴ�.*/
	}
	
	
	/*�ǿ������ ����*/
	private void txbt2411_JobProcess(){
		
		
		/*-------------Context ���� �� ����DB����----------------*/
		this.context = appContext;
		UcContextHolder.setCustomerType(this.dataSource);
		/*-------------------------------------------------------*/
		long elapseTime1 = 0;
		long elapseTime2 = 0;
		long t_elpTime1 = 0;
		long t_elpTime2 = 0;
		
		intLog = 0;
		
		dataMap = new MapForm();  /*�ʱ�ȭ*/
		dataMap.setMap("SGG_COD"        , this.c_slf_org);
		dataMap.setMap("BS_NOT_IN_SEMOK", not_in_semok());
		
		try {
			
			/*---------------------------------------------------------*/
			/*1. �ΰ������ڷ� ���� ó��.                               */
			/*---------------------------------------------------------*/
			if(log.isInfoEnabled()){
				log.info("["+c_slf_org_nm +"]====================�ΰ������ڷ� ����    ======================");
			}
			/*ó�� ���ؽð� ����*/
			elapseTime1 = System.currentTimeMillis();
			t_elpTime1 = System.currentTimeMillis();
			
			dataMap.setMap("SEOI_GB", "01");   /*01:�ΰ�, 02:�ΰ���ġ��*/

			/*�ΰ����� ���(NIS)*/
			MapForm maplvyInfo  = govService.queryForMap("TXBT2411.SELECT_LEVY_INFO_STATIC", dataMap);
			
			if ( maplvyInfo == null  ||  maplvyInfo.isEmpty() )   {
				
				log.debug("�ΰ����� ��踦 ������ �� �����ϴ�...");
				
				maplvyInfo.setMap("DEPT_CD", this.c_slf_org);
				maplvyInfo.setMap("CNT"    , 0);
				
			} else {
				
				log.debug("�μ��ڵ�(" + this.c_slf_org_nm + ")(" + maplvyInfo.getMap("DEPT_CD") + ") �ΰ��Ǽ� = [" + maplvyInfo.getMap("CNT") + "]");
			}
			
			MapForm mapLog = new MapForm();
			
			mapLog.setMap("TRS_DT"      , CbUtil.getCurrent("yyyyMMddHHmmss"));
			mapLog.setMap("SGG_COD"     , this.dataSource.substring(3));
			mapLog.setMap("TYPE"        , "I");
			mapLog.setMap("CNT"         , maplvyInfo.getMap("CNT"));
			mapLog.setMap("END_CNT"     ,  0 );
			mapLog.setMap("GUBUN"       , "00");
			
			try {
				/*���۽��� �α��۾� (insert �� : ����� �ޱ� ���� update �Լ��� insert ���� ������*/
				intLog = cyberService.queryForUpdate("TXBT2411.INSERT_LOG_TX2191_TB", mapLog);

			}catch (Exception e){
				if (e instanceof DuplicateKeyException){
					log.error("�α� ���� = " + mapLog.getMaps());
					e.printStackTrace();

				}else{
					log.error("�α� �ý��ۿ��� = " + mapLog.getMaps());
					e.printStackTrace();							
					throw (RuntimeException) e;
				}
			}
			
			log.debug("[" + this.c_slf_org_nm + "] ������������ ���� ����..." );
			
			/*���ڳ��� ���������� ��ȸ�Ѵ�.(NIS)*/
			ArrayList<MapForm> weNidLevyList = govService.queryForList("TXBT2411.SELECT_LEVY_INFO_LIST", dataMap);
			
			log.debug("[" + this.c_slf_org_nm + "] ������������ ���� �Ϸ�(" + weNidLevyList.size() + ")");
			
			long ins = 0;
			
			if (weNidLevyList.size()  >  0)   {
				
				/*���ڳ����ڷ� 1�Ǿ� fetch ó�� */
				for ( int rec_cnt = 0;  rec_cnt < weNidLevyList.size();  rec_cnt++)   {
					
					MapForm mfNidLevyList =  weNidLevyList.get(rec_cnt);
					
					/*Ȥ�ó�...because of testing */
					if (mfNidLevyList == null  ||  mfNidLevyList.isEmpty() )   {
						continue;
					}
					
					/*�⺻ Default �� ���� */
					mfNidLevyList.setMap("BU_ADD_YN"   , "N" );       /*�ΰ���ġ������ 'N'           */
					mfNidLevyList.setMap("TAX_CNT"     ,  0  );       /*�ΰ����� 0                   */
					
					mfNidLevyList.setMap("PROC_CLS"    , "1" );       /*�������ä������ default '1' */
					mfNidLevyList.setMap("DEL_YN"      , "N" );       /*��������         default 'N' */

					/**
					 * ���������...
					 * ���̳� key �� null �� ��� ������ �߻��ϹǷ�
					 * ���� null �̸� ''�� �ٲ��ֵ��� ó���Ѵ�.....2011.08.01-FreeB------
					 */
					nullToStr(mfNidLevyList);

					/*===============================*/
					/*�ΰ����̺� �Է�*/
					/*===============================*/
					try {
						
						/*�ű� INSERT*/
						mfNidLevyList.setMap("CUD_OPT"     , "1" );       /*�ڷ��ϱ���(��)*/
						
						/*ǥ�ؼ��ܼ��Ժΰ� ���̺� �Է�*/
						intLog = cyberService.queryForUpdate("TXBT2411.INSERT_PUB_SEOI_LEVY", mfNidLevyList);
					
					}catch (Exception e){
						
						if (e instanceof DuplicateKeyException){
						
							/*�������� ������Ʈ*/
							mfNidLevyList.setMap("CUD_OPT"     , "2" );       /*�ڷ��ϱ���(��)*/
							
							try {
								/*ǥ�ؼ��ܼ��Ժΰ� ���̺� ������Ʈ*/
								intLog = cyberService.queryForUpdate("TXBT2411.UPDATE_PUB_SEOI_LEVY", mfNidLevyList);	

							}catch (Exception k){
								e.printStackTrace();
							}
							
						}else{
							log.error("��� �ý��ۿ��� = " + mfNidLevyList.getMaps());
							e.printStackTrace();							
							throw (RuntimeException) e;
						}
					}
					
					/*===============================*/
					/*�ΰ������̺� �Է�*/
					/*===============================*/
					try {
						
						/*�ű� INSERT*/
						mfNidLevyList.setMap("CUD_OPT"     , "1" );       /*�ڷ��ϱ���(��) */
						
						/*ǥ�ؼ��ܼ��Ժΰ� �����̺� �Է�*/
						cyberService.queryForUpdate("TXBT2411.INSERT_PUB_SEOI_LEVY_DETAIL", mfNidLevyList);
						
					}catch (Exception e){
						
						if (e instanceof DuplicateKeyException){
							
							/*�������� ������Ʈ*/
							mfNidLevyList.setMap("CUD_OPT"     , "2" );       /*�ڷ��ϱ���(��) */
						
							try {
								/*ǥ�ؼ��ܼ��Ժΰ� �����̺� ������Ʈ*/
								cyberService.queryForUpdate("TXBT2411.UPDATE_PUB_SEOI_LEVY_DETAIL", mfNidLevyList);

							}catch (Exception k){
								e.printStackTrace();
							}
							
						}else{
							log.error("��� �ý��ۿ��� = " + mfNidLevyList.getMaps());
							e.printStackTrace();							
							throw (RuntimeException) e;
						}
					}
					
					ins++;
					mfNidLevyList.clear();
					
				}
				
			}
			
			mapLog.setMap("END_DTM"      , CbUtil.getCurrent("yyyyMMddHHmmss"));
			mapLog.setMap("END_CNT"      , ins);
			
			try {
				/*���۰�� ������Ʈ*/
				intLog = cyberService.queryForUpdate("TXBT2411.UPDATE_LOG_TX2191_TB", mapLog);
				
			}catch (Exception e){
				if (e instanceof DuplicateKeyException){
					log.error("�α� ������Ʈ ���� = " + mapLog.getMaps());
					e.printStackTrace();

				}else{
					log.error("�α�. �ý��ۿ��� = " + mapLog.getMaps());
					e.printStackTrace();							
					throw (RuntimeException) e;
				}
			}
			
			/*������ ���ؽð� ����*/
			elapseTime2 = System.currentTimeMillis();
			
			log.info("["+c_slf_org_nm +"] �ΰ��ڷ� ����Ǽ� : " + ins + " (EA)");
			log.info("["+c_slf_org_nm +"] �ΰ��ڷ� ����ð� : " + CbUtil.formatTime(elapseTime2 - elapseTime1));
			
			/*---------------------------------------------------------*/
			/*2. �ΰ���ġ�� ���� ó��.                                 */
			/*---------------------------------------------------------*/
			if(log.isInfoEnabled()){
				log.info("["+c_slf_org_nm +"]====================�ΰ���ġ������ ����  ======================");
			}
			
			elapseTime1 = System.currentTimeMillis();
			
			dataMap.setMap("SEOI_GB", "02");   /*01:�ΰ�, 02:�ΰ���ġ��*/
			
			/*�ΰ���ġ�� ���۷α׸� ���ϱ� ���� ���*/
			MapForm mapVatlvyInfo  = govService.queryForMap("TXBT2411.SELECT_LEVY_INFO_STATIC", dataMap);
			
			ins = 0;
			
			if ( mapVatlvyInfo == null  ||  mapVatlvyInfo.isEmpty() )   {
				
				log.debug("�ΰ���ġ�� ��踦 ������ �� �����ϴ�...");
				
				mapVatlvyInfo.setMap("DEPT_CD", this.dataSource.substring(3));
				mapVatlvyInfo.setMap("CNT", 0);
								
			}

			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
			}
			
			mapLog = new MapForm();
			
			mapLog.setMap("TRS_DT"      , CbUtil.getCurrent("yyyyMMddHHmmss"));
			mapLog.setMap("SGG_COD"     , this.dataSource.substring(3));
			mapLog.setMap("TYPE"        , "B");
			mapLog.setMap("CNT"         , mapVatlvyInfo.getMap("CNT"));
			mapLog.setMap("END_CNT"     , 0);
			mapLog.setMap("GUBUN"       , "00");
			
			
			try {
				/*���۽��� �α��۾� (insert �� : ����� �ޱ� ���� update �Լ��� insert ���� ������*/
				intLog = cyberService.queryForUpdate("TXBT2411.INSERT_LOG_TX2191_TB", mapLog);
				
			}catch (Exception e){
				if (e instanceof DuplicateKeyException){
					log.error("�α����(�ΰ���ġ��) = " + mapLog.getMaps());
					e.printStackTrace();

				}else{
					log.error("�α� �ý���(�ΰ���ġ��) = " + mapLog.getMaps());
					e.printStackTrace();							
					throw (RuntimeException) e;
				}
			}
			

			/*�ΰ���ġ�� ���������� ��ȸ�Ѵ�.(NIS)*/
			ArrayList<MapForm> weNidVatLevyList = govService.queryForList("TXBT2411.SELECT_VAT_INFO_LIST", dataMap);
			
			if (weNidVatLevyList.size()  >  0)   {
				
				/*�ΰ��� 1�Ǿ� fetch ó�� */
				for ( int rec_cnt = 0;  rec_cnt < weNidVatLevyList.size();  rec_cnt++)   {
					
					MapForm mfNidVatLevyList =  weNidVatLevyList.get(rec_cnt);
					
					/*ȸ������ 31, 41, 61*/
					String cACCT = cyberService.getOneFieldString("TXBT2411.SELECT_VAT_ACCT_LIST", mfNidVatLevyList);
					
					if (cACCT != null && (cACCT.equals("31") || cACCT.equals("41") || cACCT.equals("61"))){   /*�ü���(31) / ��������(41,61)�� ó���Ѵ�.*/
						
						
						try {
							
							/*
							 * �ΰ���ġ�� �� �ΰ��ڷ� ����
							 * (EX:�Ϲݰ����� �ΰ�ó���Ǿ� �ִ� �ΰ��� �ڷḦ ���Ƽ� �ջ��� ����)
							 * */
							if (cyberService.queryForDelete("TXBT2411.DELETE_EXT_VAT_DETAIL_INFO", mfNidVatLevyList) > 0 ){
								cyberService.queryForDelete("TXBT2411.DELETE_EXT_VAT_INFO", mfNidVatLevyList);
							}
							
							mfNidVatLevyList.setMap("SIGUNGU_TAX"  , mfNidVatLevyList.getMap("FST_AMT"));
							mfNidVatLevyList.setMap("CHK_ACC"      , cACCT);
							
							/*�ΰ��ݾ� �� OCR��带 ������Ʈ �Ѵ�.*/
							if (cyberService.queryForUpdate("TXBT2411.UPDATE_VAT_AMT_DETAIL_SAVE", mfNidVatLevyList) > 0 ){
								cyberService.queryForUpdate("TXBT2411.UPDATE_VAT_AMT_SAVE", mfNidVatLevyList);
							}
							ins++;
							
						}catch (Exception e){
							if (e instanceof DuplicateKeyException){
								log.error("�ΰ���ġ�� ��Ͽ��� = " + mapLog.getMaps());
								e.printStackTrace();

							}else{
								log.error("�ΰ���ġ�� ��Ͻý��ۿ��� = " + mapLog.getMaps());
								e.printStackTrace();							
								throw (RuntimeException) e;
							}
							
						}
						
					}
					
				}

			}
			
			mapLog.setMap("END_DTM"      , CbUtil.getCurrent("yyyyMMddHHmmss"));
			mapLog.setMap("END_CNT"      , ins);
			
			try {
				/*���۰�� ������Ʈ*/
				intLog = cyberService.queryForUpdate("TXBT2411.UPDATE_LOG_TX2191_TB", mapLog);
				
			}catch (Exception e){
				if (e instanceof DuplicateKeyException){
					log.error("�α�(�ΰ���) ������Ʈ ���� = " + mapLog.getMaps());
					e.printStackTrace();

				}else{
					log.error("�α�(�ΰ���). �ý��ۿ��� = " + mapLog.getMaps());
					e.printStackTrace();							
					throw (RuntimeException) e;
				}
			}
			
			elapseTime2 = System.currentTimeMillis();
			
			log.info("["+c_slf_org_nm +"] �ΰ���ġ���ڷ� ����Ǽ� : " + ins + " (EA)");
			log.info("["+c_slf_org_nm +"] �ΰ���ġ���ڷ� ����ð� : " + CbUtil.formatTime(elapseTime2 - elapseTime1));
			
			/*---------------------------------------------------------*/
			/*3. �����ڷ� �������� ó��.                               */
			/*---------------------------------------------------------*/
			if(log.isInfoEnabled()){
				log.info("["+c_slf_org_nm +"]====================�����ڷ�������� ����======================");
			}
			
			elapseTime1 = System.currentTimeMillis();
			
			mapLog = new MapForm();
			
			mapLog.setMap("TRS_DT"      , CbUtil.getCurrent("yyyyMMddHHmmss"));
			mapLog.setMap("SGG_COD"     , this.dataSource.substring(3));
			mapLog.setMap("TYPE"        , "D" );
			mapLog.setMap("CNT"         ,  0  );
			mapLog.setMap("END_CNT"     ,  0  );
			mapLog.setMap("GUBUN"       , "00");
			
			try {
				/*���۽��� �α��۾� (insert �� : ����� �ޱ� ���� update �Լ��� insert ���� ������*/
				intLog = cyberService.queryForUpdate("TXBT2411.INSERT_LOG_TX2191_TB", mapLog);

			}catch (Exception e){
				if (e instanceof DuplicateKeyException){
					log.info("�ߺ����������� = " + mapLog.getMaps());
					e.printStackTrace();

				}else{
					log.info("���������� = " + mapLog.getMaps());
					e.printStackTrace();							
					throw (RuntimeException) e;
				}
			}

			ins = 0;
			
			/*ȸ����� ���Ͽ� ȸ��⵵�� �����͸� ���κ��Ѵ�.*/

			ArrayList<MapForm> alDelAcctInfo  = govService.queryForList("TXBT2411.SELECT_KEY_FOR_DEL", dataMap);
			
			if ( alDelAcctInfo.size()  >  0 )   {
				
				for ( int al_cnt = 0;  al_cnt < alDelAcctInfo.size();  al_cnt++)   {
					
					MapForm mfDelAcctInfo =  alDelAcctInfo.get(al_cnt);

					ArrayList<MapForm> alDelInfoList  = govService.queryForList("TXBT2411.SELECT_TAX_NO_FOR_DEL", mfDelAcctInfo);
					
					for ( int dl_cnt = 0;  dl_cnt < alDelInfoList.size();  dl_cnt++)   {
						
						MapForm mfDelInfoList =  alDelInfoList.get(dl_cnt);
						
						//1�Ǿ� ���Ͽ� �����Ѵ�...
						
						try {
							
							if ( cyberService.queryForUpdate("TXBT2411.UPDATE_DEL_INFO_DETAIL", mfDelInfoList) > 0 ) {
								ins++;
							}
							
						}catch (Exception e){
							if (e instanceof DuplicateKeyException){
								log.info("���� ���������� = " + mapLog.getMaps());
								e.printStackTrace();

							}else{
								log.info("���� �ý��ۿ��� = " + mapLog.getMaps());
								e.printStackTrace();							
								throw (RuntimeException) e;
							}
						}
						
						

					}
					
				}
							
			}
		
			log.info("["+c_slf_org_nm +"] �����ڷ� �����Ǽ� = [" + ins + "]");
			
			
			/*���� ��踦 ���Ѵ�. �α��*/
			//MapForm mapDelInfo  = govService.queryForMap("TXBT2411.SELECT_TAX_NO_FOR_DEL_LINK", dataMap);
			
			/*������ ������ ���۰Ǽ� �� ����� �����ϰ�*/
			mapLog.setMap("CNT"         , ins);
			mapLog.setMap("END_DTM"     , CbUtil.getCurrent("yyyyMMddHHmmss"));
			mapLog.setMap("END_CNT"     , ins);

			try {
				/*���۽��� �α��۾� (insert �� : ����� �ޱ� ���� update �Լ��� insert ���� ������*/
				intLog = cyberService.queryForUpdate("TXBT2411.UPDATE_LOG_TX2191_TB", mapLog);
				
			}catch (Exception e){
				if (e instanceof DuplicateKeyException){
					log.error("�α�(����) ������Ʈ ���� = " + mapLog.getMaps());
					e.printStackTrace();

				}else{
					log.error("�α�(����). �ý��ۿ��� = " + mapLog.getMaps());
					e.printStackTrace();							
					throw (RuntimeException) e;
				}
			}
			
			elapseTime2 = System.currentTimeMillis();
			t_elpTime2 = System.currentTimeMillis();
			
			log.info("["+c_slf_org_nm +"] �����ڷ� �����ð� : " + CbUtil.formatTime(elapseTime2 - elapseTime1));
			
			log.info("====================�ΰ��ڷῬ�� ��  ======================");
			log.info("["+this.c_slf_org_nm+"]�ΰ��ڷῬ�� �ð� : " + CbUtil.formatTime(t_elpTime2 - t_elpTime1));
			log.info("===========================================================");
			
		} catch (UncategorizedSQLException use) {
			
            use.printStackTrace();
            
            log.error("=========== SQL ���� �߻� ===========");
        	log.error("SQLERRCODE = " + use.getSQLException().getErrorCode());
        	log.error(use.getMessage());
            	
		} catch (Exception e) {
			// TODO Auto-generated catch block
			
			log.info("==================ERROR================");

			e.printStackTrace();
		}		

	}

	/*����Ŭ 8 :: �� ����ϴ� JDBC ����̹���*/
	@SuppressWarnings("unchecked")
	private void nullToStr(MapForm mapForm) {
		// TODO Auto-generated method stub
		Iterator<String> iterForm = mapForm.getKeyList().iterator();
		
		while(iterForm.hasNext()) {
			String keyNm =  iterForm.next();
			if(mapForm.getMap(keyNm) == null) mapForm.setMap(keyNm, "");
		}
		
	}

	/*���ܼ��Կ��� ���ܽ��Ѿ� �� ����*/
	private String not_in_semok() {
	
		StringBuffer sb = new StringBuffer();
		String Retval = "";
		try {
	
			ArrayList<MapForm> alNoSemokList =  cyberService.queryForList("CODMBASE.NOSEOI", null);
			
			if (alNoSemokList.size()  >  0)   {
				
				for ( int rec_cnt = 0;  rec_cnt < alNoSemokList.size();  rec_cnt++)   {
					
					MapForm mpNoSemokList =  alNoSemokList.get(rec_cnt);
					
					/*Ȥ�ó�...because of testing */
					if (mpNoSemokList == null  ||  mpNoSemokList.isEmpty() )   {
						continue;
					}
					
					sb.append("'").append(mpNoSemokList.getMap("SEMOK")).append("'").append(",");

				}
				
				Retval = sb.toString().substring(0, sb.toString().length() -1);
				
			} 
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	
		return Retval;
	}

}
