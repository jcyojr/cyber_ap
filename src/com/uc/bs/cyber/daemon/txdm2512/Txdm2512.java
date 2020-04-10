/**
 *  �ֽý��۸� : ���̹����漼û ��ȭ
 *  ��  ��  �� : ����
 *  ��  ��  �� : ������ ���輭���� �⵿�ϰ� �ۼ��ŵ����͸� ó���Ѵ�...
 *               ������ ��û �� �����ڷḦ �����Ѵ�.
 *               
 *               ���� ������� �λ����� ��δ� ó�� �� �� ������
 *               ����PORT�� �����Ͽ� �λ����� / ���������� �����Ѵ�..
 *               ����� �λ�����
 *               
 *               com.uc.bs.cyber.etax.* �ڿ����
 *  Ŭ����  ID : Txdm2512 (�λ�����) 
 *  ����  �̷� :
 * ------------------------------------------------------------------------
 *  �ۼ���      	�Ҽ�  		    ����            Tag           ����
 * ------------------------------------------------------------------------
 *  �۵���      ��ä��(��)         2011.06.07         %01%         �����ۼ�
 */
package com.uc.bs.cyber.daemon.txdm2512;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.uc.core.MapForm;
import com.uc.core.misc.Utils;
import com.uc.core.misc.XMLFileReader;
import com.uc.core.misc.XmlUtils;
import com.uc.core.spring.service.IbatisBaseService;
import com.uc.core.spring.service.UcContextHolder;

import java.nio.channels.SocketChannel;

//TEST!!!
import com.uc.bs.cyber.CbUtil;
import com.uc.bs.cyber.etax.net.RcvWorker;
import com.uc.bs.cyber.etax.net.RcvServer;

import com.uc.bs.cyber.field.Comd_WorkBsField;

import com.uc.bs.cyber.service.bs531001.Bs531001FieldList;
import com.uc.bs.cyber.service.bs531001.Bs531001Service;
import com.uc.bs.cyber.service.bs531002.Bs531002FieldList;
import com.uc.bs.cyber.service.bs531002.Bs531002Service;


//EGTOB FRAME �� �̿��ϱ� ����.
//import com.uc.egtob.net.server.RcvWorker;
//import com.uc.egtob.net.server.RcvServer;

public class Txdm2512 extends RcvWorker{
	
	private IbatisBaseService sqlService_cyber = null;
	private ApplicationContext appContext = null;
	private Thread comdThread = null;
	

	private Comd_WorkBsField MSG_HEAD = new Comd_WorkBsField();
	
	
	private Bs531001FieldList BSL_531001 = new Bs531001FieldList();
	private Bs531002FieldList BSL_531002 = new Bs531002FieldList();
	
	
	private MapForm dataMap    = null;
	@SuppressWarnings("unused")
	private String dataSource  = null;
	@SuppressWarnings("unused")
	private byte[] recvBuffer;
	
	/* ������ 
	 * QUEUE ��
	 * */
	public Txdm2512() throws Exception {
		super();
		// TODO Auto-generated constructor stub

		log.info("============================================");
		log.info("== new RcvWorker( )                 Start ==");
		log.info("============================================");

		
	}
	
	/* ������ 
	 * �ʱ�ȭ��
	 * */
	public Txdm2512(int annotation) throws Exception {
		// TODO Auto-generated constructor stub
        // nothing...
		
	}	
	
	/*
	 * Ʈ����ǿ�(������)
	 * */
	public Txdm2512(ApplicationContext context, String dataSource) throws Exception {
		
		/*Context ����...*/
		this.context = context;
		this.dataSource = dataSource;
		
	}
			
	/*������*/
	public Txdm2512(ApplicationContext context) throws Exception {
		
		/*Context ����...*/
		setAppContext(context);
		
		sqlService_cyber = (IbatisBaseService) appContext.getBean("baseService_cyber");
			
		startServer();
		
	}

	/*=====================================================*/
	/*������ �����Ͻÿ�....................................*/
	/*=====================================================*/
	/* RcvWorker ���� ���ŵ� ����, ���ŵ����͸� ó���Ѵ�...
	 * �߻�޼��� ���� */
	@Override
	public void doDataRecv(SocketChannel socket, byte[] buffer)	throws Exception {
		// TODO Auto-generated method stub
		
		log.info("============================================");
		log.info("== doDataRecv(" + socket.socket().getInetAddress() + ") Start ==");
		log.info(" RECV[" + new String(buffer) + "]");
		log.info("============================================");
		
		this.isResponse = true;
		
		CbUtil cUtil = CbUtil.getInstance();
		
		MapForm cmMap;
		MapForm logMap = new MapForm();
		recvBuffer = null;

		try {
			
			/*
			 * ����ó��...(Ʈ�����ó��)
			 * */
			recvBuffer =  buffer;
						
			/* �������м� */
			
			cmMap = MSG_HEAD.parseHeadBuffer(buffer);
			
			cmMap.setMap("PRC_GB", "BV");   /*�λ����� ����� ��ȸ���� ó�� ���� ����*/
			
			log.debug("cmMap = " + cmMap.getMaps());
			
			/* �ۼ��� ���� �����*/
			logMap.setMap("SYS_DSC"  , "05"); /*�λ����� �������*/
			logMap.setMap("MSG_ID"   , cmMap.getMap("PROGRAM_ID")); /*��������*/
			logMap.setMap("RCV_MSG"  , new String(buffer)); /*��������*/
			logMap.setMap("REG_DTM"  , CbUtil.getCurrent("yyyyMMddHHmmss"));
			
			/* �м��� ������ ���� ���α׷��� �б��Ѵ�. */
			if(cmMap.getMap("TX_GUBUN").equals("0200")) {  /*������� ��û*/
				
				if(cmMap.getMap("PROGRAM_ID").equals("300")){ //�Ա� �� �Ա���� �ŷ����� ����
											
					log.debug("�Ա� �� �Ա���� �ŷ�   PROGRAM_ID : " + cmMap.getMap("PROGRAM_ID"));
					dataMap = BSL_531001.parseRecvBuffer(buffer);  /*���������Ľ�...*/
					
					log.debug("dataMap = " + dataMap.getMaps());
					
					Bs531001Service BS_531001 = new Bs531001Service(appContext);  /* 531001 ����ó�� */
					
					/*���������� ó���ϰ� ����������  �����Ѵ�.*/
					retBuffer = BS_531001.chk_bs_531001(cmMap, dataMap);
					
					
					
				}else if(cmMap.getMap("PROGRAM_ID").equals("400")){ // �Աݰ��� ���� ������ȸ
					
					log.debug("�Աݰ��� ���� ������ȸ   PROGRAM_ID : " + cmMap.getMap("PROGRAM_ID"));
					dataMap = BSL_531002.parseRecvBuffer(buffer);  /*���������Ľ�...*/
					
					log.debug("dataMap = " + dataMap.getMaps());
					
					Bs531002Service bs_531002 = new Bs531002Service(appContext);  /* 531002 ����ó�� */
					
					retBuffer = bs_531002.chk_bs_531002(cmMap, dataMap);
					
					
				}
				
				else { //�λ�����
						
					log.error("�̻��� ��������(" + buffer.length + ") = " + new String(buffer, 0, buffer.length < 260? buffer.length:260));
					
					retBuffer = buffer;
					
				}
														 	
				
			} else if(cmMap.getMap("TX_GUBUN").equals("210")) { /*������� ��û����*/
				
			}else {
				log.error("�̻��� ��������(" + buffer.length + ") = " + new String(buffer, 0, buffer.length < 260? buffer.length:260));
				
				retBuffer = buffer;
			}
			
			/*Ʈ����� ����...*/
			//DB�Է°��� �ִ� ���...
			//mainTransProcess();
			
			/*���������� �ۼ��������� �����Ѵ�...*/
			if(!cmMap.getMap("TX_GUBUN").equals("0810")) {
				
				if(retBuffer.length >= 2000) {
					log.info("������������� 2000bytes �̻�: 2000bytes �� ��ϳ���");
					logMap.setMap("RTN_MSG" , new String(retBuffer).substring(0, 2000)); /*��������*/
				} else {
					logMap.setMap("RTN_MSG" , new String(retBuffer)); /*��������*/
				}

				logMap.setMap("RES_CD"  , new String(retBuffer).substring(49, 53)); /*ó�����*/
				logMap.setMap("ERR_MSG" , ""); /*�����޽���*/
				logMap.setMap("REG_DTM" , CbUtil.getCurrent("yyyyMMddHHmmss"));
				logMap.setMap("LAST_DTM", CbUtil.getCurrent("yyyyMMddHHmmss"));
				
				if(sqlService_cyber.queryForUpdate("CODMBASE.CODM_JUNMUN_TXRX_INSERT_CO5108", logMap) > 0) {
					log.info("�ۼ��� ���� ����Ϸ�!!!");
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			
			/*������ �߻��� ���...Ư�� ������ �̻��� ��� ���� �����ش�.*/
			retBuffer = buffer;
		} finally {
			
			/*���������� ������ ���� ���� */
			//System.gc();
			
			//System.runFinalization();
			
		}

	}
	
	/* Main Thread 
	 * �׽�Ʈ ����ø� ����մϴ�...
	 * */
	public static void main(String args[]) {

		ApplicationContext context  = null;
		
		try {

			Txdm2512 svr = new Txdm2512(0);
			
			/*Log4j ����*/
			CbUtil.setupLog4jConfig(svr, "log4j.tomcat.xml");
			
			String[] xmls = XMLFileReader.getAllFilePathArray(Utils.getResourcePath(svr, "/"), "SERVICE.XML");

			String strSrc = Utils.getResourcePath(svr, "/") + "config/sqlmaps.xml";

			String strDest = Utils.getResourcePath(svr, "/") + "config/sqlmapConfig.xml";

			XmlUtils.setXmlAttributes(svr, strSrc, strDest, "sqlMapConfig", "sqlMap", "url", xmls);

			context = new ClassPathXmlApplicationContext("config/Tomcat-Spring-db.xml");
			
			svr.setAppContext(context);
			
			svr.sqlService_cyber = (IbatisBaseService) svr.getAppContext().getBean("baseService_cyber");
			
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
		
		int connPort =  Utils.getResourceInt("ApplicationResource", "bvmc.recv.port");  /*41052*/
		//int connPort =  41052;
		
		RcvServer server = new RcvServer(connPort, "com.uc.bs.cyber.daemon.txdm2512.Txdm2512", 20, 10, "log4j.xml", 4 ,com.uc.core.Constants.TYPEFIELDLEN);

		server.setContext(appContext);
		server.setProcId("2512");
		server.setProcName("�λ����డ�������ȸ���ο��輭��");
		server.setThreadName("thr_2512");
		
		server.initial();
		
		comdThread = new Thread(server);
		
		/**
		 * ������ �ۼ����� ���ؼ� ������ ������ش�
		 * (������������)�� ���ؼ� Threading...
		 */
		comdThread.setName("2512");
		
		comdThread.start();
		
	}
	
	/*----------------------------------------------*/
	/* �Ϸù�ȣ �������� �׽�Ʈ*/
	/*----------------------------------------------*/
	private int SeqNumber(){
    	return (Integer)sqlService_cyber.queryForBean("CODMBASE.CODM_GETSEQNO", "00");
    }
	
	/*--------------------------------------------------------------*/
	/*--------------------property set -----------------------------*/
	/*--------------------------------------------------------------*/
	public void setAppContext(ApplicationContext appContext) {
		 this.appContext = appContext;
	}

	public ApplicationContext getAppContext() {
		return appContext;
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

	@Override
	public void setContext(Object obj) {
		// TODO Auto-generated method stub
		this.appContext = (ApplicationContext) obj;
		
		sqlService_cyber = (IbatisBaseService) appContext.getBean("baseService_cyber");
	}

	

}
