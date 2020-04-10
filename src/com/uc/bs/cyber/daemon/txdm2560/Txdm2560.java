/**
 *  �ֽý��۸� : ���̹����漼û ��ȭ
 *  ��  ��  �� : ����
 *  ��  ��  �� : ���ܼ���(��û) �����ڷ���ȸ ����
 *               com.uc.bs.cyber.etax.* �ڿ����
 *               
 *               ���̹���û �����ڷ� ���� �����ܼ��Կ��� ������ ���Źް�  
 *               ��û�� �ΰ��� �����ڷḦ ���̹���û�� �ΰ��Ѵ�.
 *               
 *  Ŭ����  ID : Txdm2560 
 *  ����  �̷� :
 * ------------------------------------------------------------------------
 *  �ۼ���      	�Ҽ�  		    ����            Tag           ����
 * ------------------------------------------------------------------------
 *  �۵���      ��ä��(��)         2011.08.05         %01%         �����ۼ�
 */
package com.uc.bs.cyber.daemon.txdm2560;

import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Iterator;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.UncategorizedSQLException;

import com.uc.bs.cyber.CbUtil;
import com.uc.bs.cyber.etax.net.RcvServer;
import com.uc.bs.cyber.etax.net.RcvWorker;
import com.uc.core.MapForm;
import com.uc.core.misc.XMLFileReader;
import com.uc.core.misc.XmlUtils;
import com.uc.core.spring.service.IbatisBaseService;
import com.uc.core.spring.service.UcContextHolder;
import com.uc.egtob.net.FieldList;

/**
 * @author Administrator
 *
 */
public class Txdm2560 extends RcvWorker{

	private IbatisBaseService sqlService_cyber = null;
	private IbatisBaseService govService  = null;
	private ApplicationContext appContext = null;
	private Thread comdThread = null;
		
	private String dataSource  = null;
	
	private FieldList msgField;
	
	private Txdm2560FieldList TD_2560 = new Txdm2560FieldList();
	
	/*
	 * ������
	 */
	public Txdm2560() {
		super();
		// TODO Auto-generated constructor stub

		log.info("============================================");
		log.info("== new RcvWorker( )                 Start ==");
		log.info("============================================");
	}
	
	/* 
	 * ������ �ʱ�ȭ��
	 * */
	public Txdm2560(int annotation) throws Exception {
		// TODO Auto-generated constructor stub
        // nothing...
	}	

	/*
	 * Ʈ����ǿ�(������)
	 * */
	public Txdm2560(ApplicationContext context, String dataSource) throws Exception {
		
		/*Context ����...*/
		this.context = context;
		this.dataSource = dataSource;
		
	}
	
	/*
	 * ������
	 * */
	public Txdm2560(ApplicationContext context) throws Exception {
		
		/*Context ����...*/
		setContext(context);
		
		startServer();

	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		ApplicationContext context  = null;
		
		try {

			Txdm2560 svr = new Txdm2560(0);
			
			/*Log4j ����*/
			CbUtil.setupLog4jConfig(svr, "log4j.txdm2560.xml");
			
			try {
				String[] xmls = XMLFileReader.getAllFilePathArray(CbUtil.getResourcePath(svr, "/"), "SERVICE.XML");

				String strSrc = CbUtil.getResourcePath(svr, "/") + "config/sqlmaps.xml";

				String strDest = CbUtil.getResourcePath(svr, "/") + "config/sqlmapConfig.xml";

				XmlUtils.setXmlAttributes(svr, strSrc, strDest, "sqlMapConfig", "sqlMap", "url", xmls);

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
		
			context = new ClassPathXmlApplicationContext("config/Seoi-Spring-db.xml");
			
			svr.setContext(context);
			
			svr.startServer();
			

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	/* ���� ����...*/
	private void startServer() throws Exception {
		
		/**
		 * SERVER START
		 * port     : Listen Port
         * worker   : Worker ������(class)
         * maxQue   : �ִ� Queue ũ��
    	 * procCnt  : ���μ���(������) ����
		 */
		
		RcvServer server = new RcvServer(4642, "com.uc.bs.cyber.daemon.txdm2560.Txdm2560", 10, 5, "log4j.tomcat.xml", 0, com.uc.core.Constants.TYPENOHEAD);
		
		server.setContext(appContext);
		server.setProcId("2560");
		server.setProcName("��û�� ���ܼ��Կ���(�ܰ�)");
		server.setThreadName("thr_2560");
		server.initial();
		
		comdThread = new Thread(server);
		
		/**
		 * ������ �ۼ����� ���ؼ� ������ ������ش�
		 * (������������)�� ���ؼ� Threading...
		 */
		comdThread.setName("2560");
		
		comdThread.start();
		
	}	

	@Override
	public void doDataRecv(SocketChannel socket, byte[] buffer)	throws Exception {
		// TODO Auto-generated method stub
		
		log.info("============================================");
		log.info("== doDataRecv(" + socket.socket().getInetAddress() + ") Start ==");
		log.info(" RECV[" + new String(buffer) + "]");
		log.info("============================================");
		
		MapForm dataMap = null;

		/**
		 * �������� �ʵ� ����
		 */
		this.msgField = new FieldList();
				
		msgField.add("LEN"      ,      4,    "H");    //1 ����
		msgField.add("SGG_COD"  ,      3,    "C");    //2 ��û�ڵ�
		msgField.add("REG_NO"   ,     13,    "C");    //3 �ֹι�ȣ
		msgField.add("TAX_SNO"  ,      6,    "C");    //4 ������ȣ
		msgField.add("DIL_GB"   ,      1,    "C");    //5 ü������
		msgField.add("RST_COD"  ,      3,    "C");    //6 ����ڵ�
		msgField.add("RST_MSG"  ,    200,    "C");    //7 ����޼���
		
		String resMsg  = "";
		
		/*����ũ���� �����̶� ������.*/
		if(buffer.length >= 25) {
						
			dataMap = TD_2560.parseBuff(buffer);      /*�������� �Ľ�(��ü)*/
			
			
			if(dataMap.getMap("SGG_COD").toString().length() != 3){
				dataMap.setMap("RST_COD", "110");
				dataMap.setMap("RST_MSG", "��û�ڵ尡 �̻��մϴ�.");
			} else {
				
				/*��û�� DB �������� ����*/
				setDataSource("NI_" + dataMap.getMap("SGG_COD").toString());
				
				if (dataMap.getMap("DIL_GB").equals("0")) {
					log.info(" �Ǻ� ����� ���ܼ��� ����");
					/*�Ǻ� ����� ���ܼ��� ����*/
					resMsg += txdm2560_Rglr_JobProcess(dataMap);
					log.info(" �Ǻ� ü���� ���ܼ��� ����");
					/*�Ǻ� ü���� ���ܼ��� ����*/
					resMsg += txdm2560_fail_JobProcess(dataMap);
					
				} else if(dataMap.getMap("DIL_GB").equals("1")) {
					
					log.info(" �Ǻ� ����� ���ܼ��� ����");
					/*�Ǻ� ����� ���ܼ��� ����*/
					resMsg += txdm2560_Rglr_JobProcess(dataMap);
					
				} else if(dataMap.getMap("DIL_GB").equals("2")) {
					
					log.info(" �Ǻ� ü���� ���ܼ��� ����");
					
					/*�Ǻ� ü���� ���ܼ��� ����*/
					resMsg += txdm2560_fail_JobProcess(dataMap);
				}
				
				/*�ް� ó�������� ������ŭ ����������.*/
				dataMap.setMap("LEN"    , msgField.getFieldListLen());
				dataMap.setMap("RST_MSG", resMsg);
				
				log.info("=============================================");
				log.info("resMsg = " + resMsg);
				log.info("�������������ġ = " + resMsg.indexOf("����"));
				log.info("=============================================");
				
				if(resMsg.indexOf("����") >= 0) {
					dataMap.setMap("RST_COD", "000");
				}else{
					dataMap.setMap("RST_COD", "111");
				}

			}
			
		}else{

			dataMap = new MapForm();
			
			dataMap.setMap("LEN"    , msgField.getFieldListLen());
			dataMap.setMap("SGG_COD", "");
			dataMap.setMap("REG_NO" , "");
			dataMap.setMap("TAX_SNO", "");
			dataMap.setMap("DIL_GB" , "");
			
			dataMap.setMap("RST_COD", "094");
			dataMap.setMap("RST_MSG", "�߸��� ������ �����Ͽ����ϴ�.");
			
		}

		this.retBuffer = msgField.makeMessageByte(dataMap);	
	}

	@Override
	public void setContext(Object obj) {
		// TODO Auto-generated method stub
		this.appContext = (ApplicationContext) obj;
		
		sqlService_cyber = (IbatisBaseService) appContext.getBean("baseService_cyber");
	}
	/*--------------------------------------------------------------*/
	/*--------------------property set -----------------------------*/
	/*--------------------------------------------------------------*/
	
	public ApplicationContext getAppContext() {
		return appContext;
	}
	
	/*��û�� DB������ �����ϱ� ����...*/
	public void setDataSource(String dataSource) {
		this.dataSource = dataSource;
		
		govService = (IbatisBaseService) appContext.getBean("baseService");
		
	}
	

	/*Ʈ������� �����ϱ� ���� �Լ�.
	 * -- �Լ� ����� : transactionJob() �Լ� �ڵ� ����...
	 * -- transactionJob �Լ� ���������� Ʈ������� �����Ѵ�.
	 * */
	@SuppressWarnings("unused")
	private void mainTransProcess(){

		try {
			
			this.context = appContext;
			
			UcContextHolder.setCustomerType("LT_etax");
						
			this.startJob();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}			
		
	}
	

	/*Ʈ����� ó��*/
	@Override
	public void transactionJob() {
		// TODO Auto-generated method stub

		try {
			
			log.info("[=========== ���� transactionJob() :: Ʈ�����ó������ �Դϴ�. ========== ]");
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	}
	
	/*----------------------------------------------*/
	/* �Ϸù�ȣ �������� �׽�Ʈ*/
	/*----------------------------------------------*/
    @SuppressWarnings("unused")
	private int SeqNumber(){
    	
    	return (Integer)sqlService_cyber.queryForBean("CODMBASE.CODM_GETSEQNO", "00");

    }


	/*����� �ǿ������ ����*/
	private String txdm2560_Rglr_JobProcess(MapForm dataMap){
		
		if(log.isInfoEnabled()){	
			log.info("====================�Ǻ� �ΰ��ڷῬ�� ����======================");
			log.info(" govid["+dataMap.getMap("SGG_COD")+"], orgcd["+this.dataSource+"]");
			log.info("========================NON TRANSACTION=========================");
		}
		
		String Retvalue = "";
		
		/*-------------Context ���� �� ����DB����----------------*/
		this.context = appContext;
		UcContextHolder.setCustomerType(this.dataSource);
		/*-------------------------------------------------------*/
		
		long elapseTime1 = 0;
		long elapseTime2 = 0;
		long t_elpTime1 = 0;
		long t_elpTime2 = 0;
		
		try {
			
			/*---------------------------------------------------------*/
			/*1. �ΰ������ڷ� ���� ó��.                               */
			/*---------------------------------------------------------*/
			if(log.isInfoEnabled()){
				log.info("====================�Ǻ� �ΰ������ڷ� ����    ======================");
			}
			/*ó�� ���ؽð� ����*/
			elapseTime1 = System.currentTimeMillis();
			t_elpTime1 = System.currentTimeMillis();
			
			dataMap.setMap("BS_NOT_IN_SEMOK",  not_in_semok());
			
			/*���ڳ��� ���������� ��ȸ�Ѵ�.(NIS)*/
			ArrayList<MapForm> weNidLevyList = govService.queryForList("TXDM2560.SELECT_LEVY_INFO_LIST", dataMap);
			
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
					
					try {

						/*�ű� INSERT*/
						mfNidLevyList.setMap("CUD_OPT"     , "1" );       /*�ڷ��ϱ���(��)           */
						/*ǥ�ؼ��ܼ��Ժΰ� ���̺� �Է�*/
						if (sqlService_cyber.queryForUpdate("TXDM2560.INSERT_PUB_SEOI_LEVY", mfNidLevyList) > 0 ){
							/*...*/							
						}
						
					}catch (Exception e){
						
						/*�ߺ��� �߻��ϰų� �������ǿ� ��߳��� ���üũ*/
						if (e instanceof DuplicateKeyException){
							
							try{
								
								/*�������� ������Ʈ*/
								mfNidLevyList.setMap("CUD_OPT"     , "2" );       /*�ڷ��ϱ���(��)           */
														
			                    if (sqlService_cyber.queryForUpdate("TXDM2560.UPDATE_PUB_SEOI_LEVY_DETAIL", mfNidLevyList) > 0 ){
                                   /*...*/
								}
								
							}catch (Exception sub_e){
								log.error("���������� = " + mfNidLevyList.getMaps());
								e.printStackTrace();
								throw (RuntimeException) sub_e;
							}
							
						}else{
							log.error("���������� = " + mfNidLevyList.getMaps());
							e.printStackTrace();							
							throw (RuntimeException) e;
						}
						
					}
					
					try {

						/*�ű� INSERT*/
						mfNidLevyList.setMap("CUD_OPT"     , "1" );       /*�ڷ��ϱ���(��)           */
						/*ǥ�ؼ��ܼ��Ժΰ� �����̺� �Է� */
						if (sqlService_cyber.queryForUpdate("TXDM2560.INSERT_PUB_SEOI_LEVY_DETAIL", mfNidLevyList) > 0 ){
						   /*...*/	
						}
												
					}catch (Exception e){
						
						/*�ߺ��� �߻��ϰų� �������ǿ� ��߳��� ���üũ*/
						if (e instanceof DuplicateKeyException){
							
							try{
								
								/*�������� ������Ʈ*/
								mfNidLevyList.setMap("CUD_OPT"     , "2" );       /*�ڷ��ϱ���(��)           */
								
								/*ǥ�ؼ��ܼ��Ժΰ� �����̺� ������Ʈ */		
			                    if (sqlService_cyber.queryForUpdate("TXDM2560.UPDATE_PUB_SEOI_LEVY", mfNidLevyList) > 0 ){
								   /*...*/	
								}
								
							}catch (Exception sub_e){
								log.error("���������� = " + mfNidLevyList.getMaps());
								e.printStackTrace();
								throw (RuntimeException) sub_e;
							}
							
						}else{
							log.error("���������� = " + mfNidLevyList.getMaps());
							e.printStackTrace();							
							throw (RuntimeException) e;
						}
						
						ins++;
						mfNidLevyList.clear();
					
				    }
					
					if(ins == 0) {
						Retvalue += "���� : �ΰ��ڷᰡ �����ϴ�. ";
					}else{
						Retvalue += "���� : ����� �ΰ��ڷῬ�� �Ǽ� (" + ins + ") ";
					}

				}
				
			} else {
				Retvalue += "���� : �ΰ��ڷᰡ �����ϴ�. ";
			}
			
			
			/*������ ���ؽð� ����*/
			elapseTime2 = System.currentTimeMillis();
			
			log.info("�ΰ��ڷ� ����Ǽ� : " + ins + " (EA)");
			log.info("�ΰ��ڷ� ����ð� : " + (elapseTime2 - elapseTime1) / 1000 + " (s)");
			
			/*---------------------------------------------------------*/
			/*2. �ΰ���ġ�� ���� ó��.                                 */
			/*---------------------------------------------------------*/
			if(log.isInfoEnabled()){
				log.info("====================�Ǻ� �ΰ���ġ������ ����  ======================");
			}
			
			elapseTime1 = System.currentTimeMillis();
			
			
			ins = 0;
			
			dataMap.setMap("BUGWA_STAT", "01");  /*�������*/
			
			/*�ΰ���ġ�� ���������� ��ȸ�Ѵ�.(NIS)*/
			ArrayList<MapForm> weNidVatLevyList = govService.queryForList("TXDM2560.SELECT_VAT_INFO_LIST", dataMap);
			
			if (weNidVatLevyList.size()  >  0)   {
				
				/*�ΰ��� 1�Ǿ� fetch ó�� */
				for ( int rec_cnt = 0;  rec_cnt < weNidVatLevyList.size();  rec_cnt++)   {
					
					MapForm mfNidVatLevyList =  weNidVatLevyList.get(rec_cnt);
					
					mfNidVatLevyList.setMap("BUGWA_STAT", "01");  /*�������*/
					
					/*ȸ������ 31, 41, 61*/
					String cACCT = sqlService_cyber.getOneFieldString("TXDM2560.SELECT_VAT_ACCT_LIST", mfNidVatLevyList);
					
					if (cACCT != null && (cACCT.equals("31") || cACCT.equals("41") || cACCT.equals("61"))){   /*�ü���(31) / ��������(41,61)�� ó���Ѵ�.*/
						
						log.info("cACCT = " + cACCT);
						/*
						 * �ΰ���ġ�� �� �ΰ��ڷ� ����
						 * (EX:�Ϲݰ����� �ΰ�ó���Ǿ� �ִ� �ΰ��� �ڷḦ ���Ƽ� �ջ��� ����)
						 * */
						
						if (sqlService_cyber.queryForDelete("TXDM2560.DELETE_EXT_VAT_DETAIL_INFO", mfNidVatLevyList) > 0 ){
							sqlService_cyber.queryForDelete("TXDM2560.DELETE_EXT_VAT_INFO", mfNidVatLevyList);
						}
						
						mfNidVatLevyList.setMap("SIGUNGU_TAX"  , mfNidVatLevyList.getMap("FST_AMT"));
						mfNidVatLevyList.setMap("CHK_ACC"      , cACCT);
						
						/*�ΰ��ݾ� �� OCR��带 ������Ʈ �Ѵ�.*/
						if (sqlService_cyber.queryForUpdate("TXDM2560.UPDATE_VAT_AMT_DETAIL_SAVE", mfNidVatLevyList) > 0 ){
							sqlService_cyber.queryForUpdate("TXDM2560.UPDATE_VAT_AMT_SAVE", mfNidVatLevyList);
						}
						ins++;
					}
					
				}

				if(ins == 0){
					Retvalue += " : �ΰ���ġ���ڷ� �������. ";
				}else{
					Retvalue += " : ����� �ΰ���ġ���ڷῬ�� �Ǽ� (" + ins + ") ";
				}
				
			} else {
				Retvalue += " : �ΰ���ġ���ڷ� �������. ";
			}

			t_elpTime2 = System.currentTimeMillis();
			
			log.info("�����ڷ� �����ð� : " + (elapseTime2 - elapseTime1) / 1000 + " (s)");
			
			log.info("====================�Ǻ� �ΰ��ڷῬ�� ��  ======================");
			log.info(" govid["+dataMap.getMap("SGG_COD")+"], orgcd["+this.dataSource+"]");
			log.info(" �ΰ��ڷῬ�� �ð� : " + CbUtil.formatTime(t_elpTime2 - t_elpTime1));
			log.info("================================================================");
			

		} catch (UncategorizedSQLException use) {
			
            use.printStackTrace();
            
            if(log.isErrorEnabled()){
            	
            	log.error("=========== SQL ���� �߻� ===========");
            	log.error("SQLERRCODE = " + use.getSQLException().getErrorCode());
            	log.error(use.getMessage());
            }
            
            Retvalue = "(1)" + dataMap.getMap("SGG_COD") + "_SQL ��� ";
            
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Retvalue = "(1)�ý��� ��� ";
		}		

		return Retvalue;
	}
	
	/*ü�� �ǿ������ ����*/
	private String txdm2560_fail_JobProcess(MapForm dataMap){
		
		if(log.isInfoEnabled()){	
			log.info("====================�Ǻ� ü���ڷῬ�� ����======================");
			log.info(" govid["+dataMap.getMap("SGG_COD")+"], orgcd["+this.dataSource+"]");
			log.info("========================NON TRANSACTION=========================");
		}
		
		String Retvalue = "";
		
		/*-------------Context ���� �� ����DB����----------------*/
		this.context = appContext;
		UcContextHolder.setCustomerType(this.dataSource);
		/*-------------------------------------------------------*/
		long elapseTime1 = 0;
		long elapseTime2 = 0;
		long t_elpTime1 = 0;
		long t_elpTime2 = 0;
		
	
		try {
			
			/*---------------------------------------------------------*/
			/*1. �ΰ������ڷ� ���� ó��.                               */
			/*---------------------------------------------------------*/
			if(log.isInfoEnabled()){
				log.info("====================�Ǻ� ü���ΰ������ڷ� ����    ======================");
			}
			/*ó�� ���ؽð� ����*/
			elapseTime1 = System.currentTimeMillis();
			t_elpTime1 = System.currentTimeMillis();
			
			dataMap.setMap("BS_NOT_IN_SEMOK",  not_in_semok());

			/*���ڳ��� ü������������ ��ȸ�Ѵ�.(NIS)*/
			ArrayList<MapForm> weNidLevyList = govService.queryForList("TXDM2560.SELECT_FAIL_LEVY_INFO_LIST", dataMap);
			
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
					

					try {

						/*�ű� INSERT*/
						mfNidLevyList.setMap("CUD_OPT"     , "1" );       /*�ڷ��ϱ���(��)           */
						/*ǥ�ؼ��ܼ��Ժΰ� ���̺� �Է�*/
						sqlService_cyber.queryForUpdate("TXDM2560.INSERT_PUB_SEOI_LEVY", mfNidLevyList);
						
					}catch (Exception e){
						
						/*�ߺ��� �߻��ϰų� �������ǿ� ��߳��� ���üũ*/
						if (e instanceof DuplicateKeyException){
							
							try{
								
								/*�������� ������Ʈ*/
								mfNidLevyList.setMap("CUD_OPT"     , "2" );       /*�ڷ��ϱ���(��)           */
														
								sqlService_cyber.queryForUpdate("TXDM2560.UPDATE_PUB_SEOI_LEVY", mfNidLevyList);
								
							}catch (Exception sub_e){
								log.error("���������� = " + mfNidLevyList.getMaps());
								e.printStackTrace();
								throw (RuntimeException) sub_e;
							}
							
						}else{
							log.error("���������� = " + mfNidLevyList.getMaps());
							e.printStackTrace();							
							throw (RuntimeException) e;
						}
						
					}
					

					try {

						/*�ű� INSERT*/
						mfNidLevyList.setMap("CUD_OPT"     , "1" );       /*�ڷ��ϱ���(��)           */
						/*ǥ�ؼ��ܼ��Ժΰ� ���̺� �Է�*/
						sqlService_cyber.queryForUpdate("TXDM2560.INSERT_PUB_SEOI_LEVY_DETAIL", mfNidLevyList);
						
					}catch (Exception e){
						
						/*�ߺ��� �߻��ϰų� �������ǿ� ��߳��� ���üũ*/
						if (e instanceof DuplicateKeyException){
							
							try{
								
								/*�������� ������Ʈ*/
								mfNidLevyList.setMap("CUD_OPT"     , "2" );       /*�ڷ��ϱ���(��)           */
														
								sqlService_cyber.queryForUpdate("TXDM2560.UPDATE_PUB_SEOI_LEVY_DETAIL", mfNidLevyList);
								
							}catch (Exception sub_e){
								log.error("INSERT ���������� = " + mfNidLevyList.getMaps());
								e.printStackTrace();
								throw (RuntimeException) sub_e;
							}
							
						}else{
							log.error("�ý��� ���������� = " + mfNidLevyList.getMaps());
							e.printStackTrace();							
							throw (RuntimeException) e;
						}
						
					}
					
					ins++;
					mfNidLevyList.clear();
					
				}
				
				if(ins == 0) {
					Retvalue += "���� : ü���ΰ��ڷᰡ �����ϴ�. ";
				}else{
					Retvalue += "���� : ü���� �ΰ��ڷῬ�� �Ǽ� (" + ins + ") ";
				}
				
				
				
			} else {
				
				Retvalue += "���� : ü���ΰ��ڷᰡ �����ϴ�. ";
				
			}
			
			
			/*������ ���ؽð� ����*/
			elapseTime2 = System.currentTimeMillis();
			
			log.info("ü�� �ΰ��ڷ� ����Ǽ� : " + ins + " (EA)");
			log.info("ü�� �ΰ��ڷ� ����ð� : " + (elapseTime2 - elapseTime1) / 1000 + " (s)");
			
			/*---------------------------------------------------------*/
			/*2. ü���ΰ���ġ�� ���� ó��.                                 */
			/*---------------------------------------------------------*/
			if(log.isInfoEnabled()){
				log.info("====================�Ǻ� ü���ΰ���ġ������ ����  ======================");
			}
						
			elapseTime1 = System.currentTimeMillis();
			
			dataMap.setMap("BUGWA_STAT", "01");  /*�������*/
			
			ins = 0;
			/*�ΰ���ġ�� ���������� ��ȸ�Ѵ�.(NIS)*/
			ArrayList<MapForm> weNidVatLevyList = govService.queryForList("TXDM2560.SELECT_FAIL_VAT_INFO_LIST", dataMap);
			
			if (weNidVatLevyList.size()  >  0)   {
				
				/*�ΰ��� 1�Ǿ� fetch ó�� */
				for ( int rec_cnt = 0;  rec_cnt < weNidVatLevyList.size();  rec_cnt++)   {
					
					MapForm mfNidVatLevyList =  weNidVatLevyList.get(rec_cnt);
					
					mfNidVatLevyList.setMap("BUGWA_STAT", "02");  /*ü��*/
					
					/*ȸ������ 31, 41, 61*/
					String cACCT = sqlService_cyber.getOneFieldString("TXDM2560.SELECT_VAT_ACCT_LIST", mfNidVatLevyList);
					
					if (cACCT != null && (cACCT.equals("31") || cACCT.equals("41") || cACCT.equals("61"))){   /*�ü���(31) / ��������(41,61)�� ó���Ѵ�.*/
						
						log.info("cACCT = " + cACCT);
						/*
						 * �ΰ���ġ�� �� �ΰ��ڷ� ����
						 * (EX:�Ϲݰ����� �ΰ�ó���Ǿ� �ִ� �ΰ��� �ڷḦ ���Ƽ� �ջ��� ����)
						 * */

						if (sqlService_cyber.queryForDelete("TXDM2560.DELETE_EXT_VAT_DETAIL_INFO", mfNidVatLevyList) > 0 ){
							sqlService_cyber.queryForDelete("TXDM2560.DELETE_EXT_VAT_INFO", mfNidVatLevyList);
						}
						
						log.info("SIGUNGU_TAX = " + mfNidVatLevyList.getMap("FST_AMT"));
						log.info("THLVY_KEY = " + mfNidVatLevyList.getMap("THLVY_KEY"));
						
						mfNidVatLevyList.setMap("SIGUNGU_TAX"  , mfNidVatLevyList.getMap("FST_AMT"));
						mfNidVatLevyList.setMap("CHK_ACC"      , cACCT);
						
						/*�ΰ��ݾ� �� OCR��带 ������Ʈ �Ѵ�.*/
						if (sqlService_cyber.queryForUpdate("TXDM2560.UPDATE_VAT_AMT_DETAIL_SAVE", mfNidVatLevyList) > 0 ){
							sqlService_cyber.queryForUpdate("TXDM2560.UPDATE_VAT_AMT_SAVE", mfNidVatLevyList);
						}
						ins++;
					}
					
				}
				
				if(ins == 0) {
					Retvalue += " : ü���ΰ���ġ���ڷ� �������. ";
				}else{
					Retvalue += " : ü���� �ΰ���ġ���ڷῬ�� �Ǽ� (" + ins + ") ";
				}

			} else {
				Retvalue += " : ü���ΰ���ġ���ڷ� �������. ";
			}
			
			elapseTime2 = System.currentTimeMillis();
			
			log.debug("�ΰ���ġ���ڷ� ����Ǽ� : " + ins + " (EA)");
			log.debug("�ΰ���ġ���ڷ� ����ð� : " + (elapseTime2 - elapseTime1) / 1000 + " (s)");
			

			elapseTime2 = System.currentTimeMillis();
			t_elpTime2 = System.currentTimeMillis();
			
			
			log.info("====================�Ǻ� �ΰ��ڷῬ�� ��  ======================");
			log.info(" govid["+dataMap.getMap("SGG_COD")+"], orgcd["+this.dataSource+"]");
			log.info(" �ΰ��ڷῬ�� �ð� : [" + CbUtil.formatTime(t_elpTime2 - t_elpTime1) + "]");
			log.info("================================================================");
			
		} catch (UncategorizedSQLException use) {
			
            use.printStackTrace();
            
            if(log.isErrorEnabled()){
            	
            	log.error("=========== SQL ���� �߻� ===========");
            	log.error("SQLERRCODE = " + use.getSQLException().getErrorCode());
            	log.error(use.getMessage());
            	
            }

            Retvalue = "(2)" + dataMap.getMap("SGG_COD") + "_SQL ��� ";
            
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
			Retvalue = "(2)�ý��� ��� ";
		}			

		return Retvalue;
	}
	

	/*����Ŭ 8 :: �� ����ϴ� JDBC ����̹���*/
	@SuppressWarnings("unchecked")
	private void nullToStr(MapForm mapForm) {
		// TODO Auto-generated method stub
		Iterator<String> iterForm =  mapForm.getKeyList().iterator();
		
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
	
			ArrayList<MapForm> alNoSemokList =  sqlService_cyber.queryForList("CODMBASE.NOSEOI", null);
			
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
