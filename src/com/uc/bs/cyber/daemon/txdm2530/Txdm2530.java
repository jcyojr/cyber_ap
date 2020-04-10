/**
 *  �ֽý��۸� : ���̹����漼û ��ȭ
 *  ��  ��  �� : ����
 *  ��  ��  �� : ���ý� ȸ������ ���� �� �Ѱ����� �����ڷ� ���۽� �������� ���踦 ���� ����
 *               com.uc.bs.cyber.etax.* �ڿ����
 *  Ŭ����  ID : Txdm2530 
 *  ����  �̷� :
 * ------------------------------------------------------------------------
 *  �ۼ���      	�Ҽ�  		    ����            Tag           ����
 * ------------------------------------------------------------------------
 *  ����        ��ä��(��)      2011.06.02         %01%         �����ۼ�
 */
package com.uc.bs.cyber.daemon.txdm2530;

import java.nio.channels.SocketChannel;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.uc.bs.cyber.CbUtil;
import com.uc.bs.cyber.etax.net.RcvServer;
import com.uc.bs.cyber.etax.net.RcvWorker;
import com.uc.core.MapForm;
import com.uc.core.misc.Utils;
import com.uc.core.misc.XMLFileReader;
import com.uc.core.misc.XmlUtils;
import com.uc.core.spring.service.IbatisService;
import com.uc.egtob.net.FieldList;

//EGTOB FRAME �� �̿��ϱ� ����.
//import com.uc.egtob.net.server.RcvWorker;
//import com.uc.egtob.net.server.RcvServer;

public class Txdm2530 extends RcvWorker{
	
	private IbatisService sqlService = null;
	private ApplicationContext appContext = null;
	private Thread comdThread = null;
		
	
	@SuppressWarnings("unused")
	private String dataSource  = null;
	
	private FieldList msgField;
	

	
	/* ������ 
	 * QUEUE ��
	 * */
	public Txdm2530() throws Exception {
		super();
		// TODO Auto-generated constructor stub

		log.info("============================================");
		log.info("== new RcvWorker( )                 Start ==");
		log.info("============================================");

		
	}
	
	/* ������ 
	 * �ʱ�ȭ��
	 * */
	public Txdm2530(int annotation) throws Exception {
		// TODO Auto-generated constructor stub
        // nothing...
		
	}	
	
	/*
	 * Ʈ����ǿ�(������)
	 * */
	public Txdm2530(ApplicationContext context, String dataSource) throws Exception {
		
		/*Context ����...*/
		this.context = context;
		this.dataSource = dataSource;
		
	}
			
	/*������*/
	public Txdm2530(ApplicationContext context) throws Exception {
		
		/*Context ����...*/
		setAppContext(context);
		//msgField.add("CHALLENGE" ,    100,    "C");    // ���ڼ��� ����
		
		startServer();
	}

	/*=====================================================*/
	/*������ �����Ͻÿ�....................................*/
	/*=====================================================*/
	/* RcvWorker ���� ���ŵ� ����, ���ŵ����͸� ó���Ѵ�...
	 * �߻�޼��� ����*/
	@Override
	public void doDataRecv(SocketChannel socket, byte[] buffer)	throws Exception {
		// TODO Auto-generated method stub
		
		log.info("============================================");
		log.info("== doDataRecv(" + socket.socket().getInetAddress() + ") Start ==");
		log.info(" RECV[" + new String(buffer) + "]");
		log.info("============================================");
		
		MapForm logMap = new MapForm();
		
		/**
		 * �������� �ʵ� ����
		 */
		this.msgField = new FieldList();
		
		msgField.add("RSLT_COD"  ,   	9,    "C");    // ����ڵ�
		msgField.add("RSLT_MSG"  ,     50,    "C");    // ����޽���
		
		MapForm dataMap = new MapForm();
		
		if(buffer.length >= 32) {
		
			/* �ۼ��� ���� �����*/
			logMap.setMap("SYS_DSC"  , "03"); /*���ý�*/
			logMap.setMap("MSG_ID"   , "Txdm2530"); /*��������*/
			logMap.setMap("RCV_MSG"  , new String(buffer)); /*��������*/
			logMap.setMap("REG_DTM"  , CbUtil.getCurrent("yyyyMMddHHmmss"));
			
			try {
				
				dataMap = sqlService.queryForMap
									("SELECT PLGN USR_DIV, TRIM(CALL) CALL, LENGTH(TRIM(PLGN)) + 2 MSG_SIZE FROM CO4101_TB WHERE NANSU='" + new String(buffer) + "'");

				if(dataMap == null || dataMap.size() == 0) {
					// ��ġ�ϴ� ������ ����
					dataMap = new MapForm();

					dataMap.setMap("RSLT_COD", "24110-101");
					dataMap.setMap("RSLT_MSG", "��û�� �������� ��ȸ���� �ʾҽ��ϴ�.");
					
				} else {
					msgField.add("MSG_SIZE"  ,   	9,    "H");    // �����ͺα���
					msgField.add("USR_DIV"   ,   	2,    "H");    // ����������(01:����, 02:���λ����
					msgField.add("CALL" ,    dataMap.getMap("CALL").toString().length(),    "C");    // ���ڼ��� ����
					
					dataMap.setMap("RSLT_COD", "24110-000");
					dataMap.setMap("RSLT_MSG", "���� ó���Ǿ����ϴ�");
				}
				
			} catch (Exception e) {
				
				dataMap = new MapForm();
				
				dataMap.setMap("RSLT_COD", "24110-201");
				dataMap.setMap("RSLT_MSG", "���νý��� ������ �߻��߽��ϴ�");
				// �ý��� �����Դϴ�
				e.printStackTrace();
			}
			
		} else {
			// ���� ������ �����ʽ��ϴ�
			dataMap = new MapForm();
			
			dataMap.setMap("RSLT_COD", "24110-102");
			dataMap.setMap("RSLT_MSG", "��û�� �������� ������ �����Դϴ�");
		}

		this.retBuffer = msgField.makeMessageByte(dataMap);	

		logMap.setMap("RTN_MSG" , new String(retBuffer)); /*��������*/
		logMap.setMap("RES_CD"  , ""); /*ó�����*/
		logMap.setMap("ERR_MSG" , ""); /*�����޽���*/
		logMap.setMap("REG_DTM" , CbUtil.getCurrent("yyyyMMddHHmmss"));
		logMap.setMap("LAST_DTM", CbUtil.getCurrent("yyyyMMddHHmmss"));
		
		if(sqlService.queryForUpdate("CODMBASE.CODM_JUNMUN_TXRX_INSERT", logMap) > 0) {
			log.info("�ۼ��� ���� ����Ϸ�!!!");
		}
		
		
	}
	
	
	/* Main Thread 
	 * �׽�Ʈ ����ø� ����մϴ�...
	 * */
	public static void main(String args[]) {

		ApplicationContext context  = null;
		
		try {

			Txdm2530 svr = new Txdm2530(0);
			
			/*Log4j ����*/
			CbUtil.setupLog4jConfig(svr, "log4j.tomcat.xml");
			
			String[] xmls = XMLFileReader.getAllFilePathArray(Utils.getResourcePath(svr, "/"), "SERVICE.XML");

			String strSrc = Utils.getResourcePath(svr, "/") + "config/sqlmaps.xml";

			String strDest = Utils.getResourcePath(svr, "/") + "config/sqlmapConfig.xml";

			XmlUtils.setXmlAttributes(svr, strSrc, strDest, "sqlMapConfig", "sqlMap", "url", xmls);

			context = new ClassPathXmlApplicationContext("config/Tomcat-Spring-*.xml");
		
			svr.setAppContext(context);
			
			svr.sqlService = (IbatisService) svr.getAppContext().getBean("ibatisService_cyber");
			
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
		
		int connPort =  Utils.getResourceInt("ApplicationResource", "wetax.recv_nan.port");  /*9382*/
				
		RcvServer server = new RcvServer(connPort, "com.uc.bs.cyber.daemon.txdm2530.Txdm2530", 10, 5, "log4j.tomcat.xml", 0, com.uc.core.Constants.TYPENOHEAD);

		// server.setDataLen(32);
		
		server.setContext(appContext);
		server.setProcId("2530");
		server.setProcName("�����������ż���");
		server.setThreadName("thr_2530");
		
		server.initial();
		
		comdThread = new Thread(server);
		
		/**
		 * ������ �ۼ����� ���ؼ� ������ ������ش�
		 * (������������)�� ���ؼ� Threading...
		 */
		comdThread.setName("2530");
		
		comdThread.start();
		
	}
	

	/*--------------------------------------------------------------*/
	/*--------------------property set -----------------------------*/
	/*--------------------------------------------------------------*/
	public void setAppContext(ApplicationContext appContext) {
		this.appContext =appContext;
		
		sqlService= (IbatisService) appContext.getBean("ibatisService_cyber");
	}

	public ApplicationContext getAppContext() {
		return appContext;
	}

	@Override
	public void transactionJob() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setContext(Object obj) {
		// TODO Auto-generated method stub
		this.setAppContext((ApplicationContext) obj);
		sqlService= (IbatisService) appContext.getBean("ibatisService_cyber");
	}

	
	

}
