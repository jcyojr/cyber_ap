/**
 *  �ֽý��۸� : ���̹����漼û ��ȭ
 *  ��  ��  �� : ����
 *  ��  ��  �� : ���ý� ���ռ������輭���� �⵿�ϰ� �ۼ��ŵ����͸� ó���Ѵ�...
 *  Ŭ����  ID : Txdm2520 :: TEST
 *  ����  �̷� :
 * ------------------------------------------------------------------------
 *  �ۼ���      	�Ҽ�  		    ����            Tag           ����
 * ------------------------------------------------------------------------
 *  Administrator   u c         2011.04.24         %01%         �����ۼ�
 */
package com.uc.bs.cyber.etax.server;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.dao.DuplicateKeyException;

import com.uc.core.MapForm;
import com.uc.core.misc.Utils;
import com.uc.core.misc.XMLFileReader;
import com.uc.core.misc.XmlUtils;
import com.uc.core.spring.service.IbatisBaseService;
import com.uc.core.spring.service.UcContextHolder;

import java.nio.channels.SocketChannel;
import java.sql.SQLException;

//TEST!!!
import com.uc.bs.cyber.CbUtil;
import com.uc.bs.cyber.etax.net.RcvWorker;
import com.uc.bs.cyber.etax.net.RcvServer;

import com.uc.bs.cyber.etax.net.ChkFieldList;
import com.uc.bs.cyber.etax.net.RtnFieldList;

import com.uc.bs.cyber.service.we532001.We532001FieldList;
import com.uc.bs.cyber.service.we532002.We532002FieldList;
import com.uc.bs.cyber.service.we992001.We992001FieldList;

import com.uc.bs.cyber.service.we532001.We532001Service;
import com.uc.bs.cyber.service.we532002.We532002Service;
import com.uc.bs.cyber.service.we992001.We992001Service;

//EGTOB FRAME �� �̿��ϱ� ����.
//import com.uc.egtob.net.server.RcvWorker;
//import com.uc.egtob.net.server.RcvServer;

public class Txdm2520 extends RcvWorker{
	
	private static IbatisBaseService sqlService_cyber = null;
	private static ApplicationContext appContext = null;
	private static Thread comdThread = null;
		
	private We532001FieldList FL_532001 = new We532001FieldList();
	private We532002FieldList FL_532002 = new We532002FieldList();
	private We992001FieldList FL_992001 = new We992001FieldList();
	
	private RtnFieldList rtnList = new RtnFieldList();
	private ChkFieldList chkList = new ChkFieldList();
	
	private MapForm dataMap    = null;
	@SuppressWarnings("unused")
	private String dataSource  = null;
	private byte[] recvBuffer;
	
	/* ������ 
	 * QUEUE ��
	 * */
	public Txdm2520() throws Exception {
		super();
		// TODO Auto-generated constructor stub

		log.info("============================================");
		log.info("== new RcvWorker( )                 Start ==");
		log.info("============================================");

		
	}
	
	/* ������ 
	 * �ʱ�ȭ��
	 * */
	public Txdm2520(int annotation) throws Exception {
		// TODO Auto-generated constructor stub
        // nothing...
		
	}	
	
	/*
	 * Ʈ����ǿ�(������)
	 * */
	public Txdm2520(ApplicationContext context, String dataSource) throws Exception {
		
		/*Context ����...*/
		this.context = context;
		this.dataSource = dataSource;
		
	}
			
	/*������*/
	public Txdm2520(ApplicationContext context) throws Exception {
		
		/*Context ����...*/
		setAppContext(context);
		
		sqlService_cyber = (IbatisBaseService) Txdm2520.appContext.getBean("baseService_cyber");
		

		try {
		
			startServer();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
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
		
		recvBuffer = null;

		try {
			
			/*
			 * ����ó��...(Ʈ�����ó��)
			 * */
			recvBuffer =  buffer;
						
			/*Ʈ����� ����...*/
			mainTransProcess();

		} catch (Exception e) {
			
			e.printStackTrace();
		}

	}
	
	
	/* Main Thread 
	 * �׽�Ʈ ����ø� ����մϴ�...
	 * */
	public static void main(String args[]) {

		ApplicationContext context  = null;
		
		try {

			Txdm2520 svr = new Txdm2520(0);
			
			/*Log4j ����*/
			CbUtil.setupLog4jConfig(svr, "log4j.tomcat.xml");
			
			String[] xmls = XMLFileReader.getAllFilePathArray(Utils.getResourcePath(svr, "/"), "SERVICE.XML");

			String strSrc = Utils.getResourcePath(svr, "/") + "config/sqlmaps.xml";

			String strDest = Utils.getResourcePath(svr, "/") + "config/sqlmapConfig.xml";

			XmlUtils.setXmlAttributes(svr, strSrc, strDest, "sqlMapConfig", "sqlMap", "url", xmls);

			context = new ClassPathXmlApplicationContext("config/Tomcat-Spring-*.xml");
		
			svr.setAppContext(context);
			
			sqlService_cyber = (IbatisBaseService) svr.getAppContext().getBean("baseService_cyber");
			
			startServer();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	/* ���� ����...*/
	private static void startServer() throws Exception {
		
		/**
		 * SERVER START
		 * port     : Listen Port
         * worker   : Worker ������(class)
         * maxQue   : �ִ� Queue ũ��
    	 * procCnt  : ���μ���(������) ����
		 */
		
		int connPort =  Utils.getResourceInt("ApplicationResource", "wetax.recv.port");  /*9983*/
		
		RcvServer server = new RcvServer(connPort, "com.uc.bs.cyber.etax.server.Txdm2520", 20, 20, "log4j.tomcat.xml", 6, com.uc.core.Constants.TYPEFIELDLEN);

		server.setContext(appContext);
		
		comdThread = new Thread(server);
		
		/**
		 * ������ �ۼ����� ���ؼ� ������ ������ش�
		 * (������������)�� ���ؼ� Threading...
		 */
		comdThread.setName("2520");
		
		comdThread.start();
		
	}
	
	
	/*----------------------------------------------*/
	/* �Ϸù�ȣ �������� �׽�Ʈ*/
	/*----------------------------------------------*/
    @SuppressWarnings("unused")
	private int SeqNumber(){
    	return (Integer)sqlService_cyber.queryForBean("CODMBASE.CODM_GETSEQNO", "00");
    }
	

	/*
	 * ���������� DB�� �����Ѵ�...
	 * @param type : ��������
	 * @param buffer : �ۼ�������
	 * */
	public String saveTeleGram(String type, byte[] buffer) throws Exception {
		
		log.debug("===============JUNMUN Save()================");
		
		String retStr = ""; 
		
		try {
			
			if (type.equals("532001")) {
				dataMap = FL_532001.parseBuff(buffer);

			} else if (type.equals("532002")) {
				dataMap = FL_532002.parseBuff(buffer);
                
				dataMap.setMap("EPAY_NO", dataMap.getMap("TOTPAY_NO"));
				
			} else if (type.equals("992001")) {
				dataMap = FL_992001.parseBuff(buffer);

			} else {
                
			}
			
			dataMap.setMap("MSG", new String(buffer));
			
			if (log.isDebugEnabled()){
				log.debug(dataMap);
			}
			
			
			/*���������� �����Ѵ�...*/
			sqlService_cyber.queryForInsert("CODMBASE.CODM_JUNMUN_LOG_SAVE", dataMap);
			
			
		} catch (DuplicateKeyException dke) { /* �ߺ��� �߻��� ��� */
			
			dke.printStackTrace();
			
			dataMap.setMap("RSLT_COD", "44100-100");
			dataMap.setMap("RSLT_MESSAGE", "��������!! �̹� ��ϵ� �����Դϴ�");
			
			retBuffer = rtnList.getBuff(dataMap);
			retStr = "44100-100";
			
		} catch (SQLException se) {
			se.printStackTrace();
			
			try {

				if(se.getErrorCode() == 1) {
					
					dataMap.setMap("RSLT_COD", "44100-100");
					dataMap.setMap("RSLT_MESSAGE", "��������!! �̹� ��ϵ� �����Դϴ�");
					
					retBuffer = rtnList.getBuff(dataMap);
					retStr = "44100-100";
					
				} else if (((String) dataMap.getMap("CO_TRAN")).equals("532001")) {
					retStr = "44100-201";
					retBuffer = rtnList.CYB532001(retStr);

				} else if (((String) dataMap.getMap("CO_TRAN")).equals("532002")) {
					retStr = "44110-201";
					retBuffer = rtnList.CYB532002(retStr);

				} else if (((String) dataMap.getMap("CO_TRAN")).equals("992001")) {
					retStr = "44120-201";
					retBuffer = rtnList.CYB992001(retStr);

				}

			} catch (Exception ee) {

			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
			try {
				if (((String) dataMap.getMap("CO_TRAN")).equals("532001")) {
					retStr = "44100-201";
					retBuffer = rtnList.CYB532001(retStr);

				} else if (((String) dataMap.getMap("CO_TRAN")).equals("532002")) {
					retStr = "44110-201";
					retBuffer = rtnList.CYB532002(retStr);

				} else if (((String) dataMap.getMap("CO_TRAN")).equals("992001")) {
					retStr = "44120-201";
					retBuffer = rtnList.CYB992001(retStr);

				}

			} catch (Exception ee) {

			}
			
		} finally{
						
		}	
		
		return retStr;
	}

	/*--------------------------------------------------------------*/
	/*--------------------property set -----------------------------*/
	/*--------------------------------------------------------------*/
	public void setAppContext(ApplicationContext appContext) {
		Txdm2520.appContext = appContext;
	}

	public ApplicationContext getAppContext() {
		return appContext;
	}
	
	/*Ʈ������� �����ϱ� ���� �Լ�.
	 * -- �Լ� ����� : transactionJob() �Լ� �ڵ� ����...
	 * -- transactionJob �Լ� ���������� Ʈ������� �����Ѵ�.
	 * */
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
		
		String saveRet = "";
		
		try {
			
			log.info("[=========== ���� transactionJob() :: Ʈ�����ó������ �Դϴ�. ========== ]");
			
			/*�ŷ����и� �Ľ��Ѵ�...(�ŷ������� �˱� ����)*/
			dataMap = chkList.parseBuff(recvBuffer);
			
			if (((String) dataMap.getMap("CO_TRAN")).equals("532001")) { 
				/*
				 * ���������ǿ� ���� ���� ó��
				 * */
				
				saveRet = saveTeleGram((String) dataMap.getMap("CO_TRAN"), recvBuffer);  /*�������� ����*/
				
				if(saveRet.equals("")) {   /*�������� �����ΰ��*/
					
					dataMap = FL_532001.parseBuff(recvBuffer);      /*�������� �Ľ�(��ü)*/
	
					if(log.isDebugEnabled()){
						log.debug(dataMap); 
					}
					
					// ī��������� ������ü���� ���� ��� �߰�
					// ���� ������ �Ǵ��� �� �ִ� �ٰŴ� ī����ι�ȣ�� ���� �ִ��� ������ �̹Ƿ� ��ɷθ� �Ǵ��Ѵ�
					if(!((String) dataMap.getMap("CARD_REQ_NO")).trim().equals("")) { /* ī����ι�ȣ�� ������ ī����� */
						
						dataMap.setMap("BNK_CD", ((String) dataMap.getMap("BANK_COD")).trim().substring(2, 5));
						
					} else { /* �ƴϸ� ������ü ���� */
						
						dataMap.setMap("BNK_CD", ((String) dataMap.getMap("BANK_COD")).trim().substring(0, 3));
				
					}
	
					We532001Service chkweTax = new We532001Service(appContext);  /* 532001 ����ó�� */
	
					saveRet = chkweTax.chkweTax(dataMap);
	
					retBuffer = rtnList.CYB532001(saveRet);
				}
				
			} else if (((String) dataMap.getMap("CO_TRAN")).equals("532002")) {
				/*
				 * �Ѱ������ǿ� ���� ���� ó��
				 * */
				
				saveRet = saveTeleGram((String) dataMap.getMap("CO_TRAN"), recvBuffer);  /*�������� ����*/
				
				if(saveRet.equals("")) {   /*�������� �����ΰ��*/
					
					dataMap = FL_532002.parseBuff(recvBuffer);
					
					if(log.isDebugEnabled()){
						log.debug(dataMap); 
					}
					
                    if(!((String) dataMap.getMap("CARD_REQ_NO")).trim().equals("")) { /* ī����ι�ȣ�� ������ ī����� */
						
						dataMap.setMap("BNK_CD", ((String) dataMap.getMap("BANK_COD")).trim().substring(2, 5));
						
					} else { /* �ƴϸ� ������ü ���� */
						
						dataMap.setMap("BNK_CD", ((String) dataMap.getMap("BANK_COD")).trim().substring(0, 3));
				
					}
					
                    We532002Service chkweTax = new We532002Service(appContext);  /* 532002 ����ó�� */
                    
                    saveRet = chkweTax.chkweTax(dataMap);
                	
					retBuffer = rtnList.CYB532002(saveRet);
					
				}
				
				
				
			} else if (((String) dataMap.getMap("CO_TRAN")).equals("992001")) {
				/*
				 * ��� ���� ó��
				 * */
				
				saveRet = saveTeleGram((String) dataMap.getMap("CO_TRAN"), recvBuffer);  /*�������� ����*/
				
				if(saveRet.equals("")) {   /*�������� �����ΰ��*/
					
					dataMap = FL_992001.parseBuff(recvBuffer);
					
					if(log.isDebugEnabled()){
						log.debug(dataMap); 
					}
					
					We992001Service chkweTax = new We992001Service(appContext);  /* 532002 ����ó�� */
					
					saveRet = chkweTax.chkweTax(dataMap);
					
					retBuffer = rtnList.CYB532002(saveRet);
					
				}
				
				
			} else {
				/*
				 * �׿� �ٸ� ���������� �� ��� ����ó��...
				 * */
				
				rtnList.setField("RSLT_COD"     , "44000-000");
				rtnList.setField("RSLT_MESSAGE" , "�ŷ������ڵ� �����Դϴ�");
				retBuffer = (byte[]) rtnList.getBuff();
				 
				log.error("============================================");
				log.error("== doDataRecv() Error End �ŷ������ڵ� ���� ==");
				log.error("============================================");
				
				return;

			}
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

	}

	@Override
	public void setContext(Object obj) {
		// TODO Auto-generated method stub
		this.setAppContext((ApplicationContext) obj);
	}



}
