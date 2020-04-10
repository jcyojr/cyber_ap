/**
 *  �ֽý��۸� : ���̹����漼û ��ȭ
 *  ��  ��  �� : ����
 *  ��  ��  �� : �λ��(��) ��ܿ��� ����
 *  Ŭ����  ID : Comd_bsServer
 *  ����  �̷� :
 * ------------------------------------------------------------------------
 *  �ۼ���      	�Ҽ�  		    ����            Tag           ����
 * ------------------------------------------------------------------------
 *  ����       ��ä��(��)      2010.11.30         %01%         �����ۼ�
 *
 */

package com.uc.bs.cyber.net;

import java.io.IOException;
import java.nio.channels.SocketChannel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;

import com.ibatis.sqlmap.client.SqlMapException;

import com.uc.core.MapForm;
import com.uc.core.Constants;
import com.uc.egtob.net.SocketDesc;
import com.uc.egtob.net.SocketDescImpl;
import com.uc.egtob.net.SocketWorker;
import com.uc.egtob.net.UcServerSocket;
import com.uc.core.spring.service.IbatisService;
import com.uc.egtob.uccomm.UcCommClient;

import com.uc.bs.cyber.CbUtil;
import com.uc.bs.cyber.field.Comd_cmhd;

public class Comd_bsServer implements Runnable, SocketWorker{
	
	protected IbatisService sqlService = null;
	
	protected Log log = LogFactory.getLog(this.getClass());
	
	protected MapForm sysMap = new MapForm();
	
	private UcServerSocket serverSocket = null;
	
	private SocketDesc sockDesc         = null;
	
	private Comd_cmhd headField         = new Comd_cmhd();
	
	private UcCommClient commClient     = null;
	
	private String    workProcId        = "9903";
	
	private SocketChannel  clientSocket = null;
	
	private int port;
	
	private int bsSendPort = 0;
	
	private ApplicationContext appContext;
	

	/**
	 * ������...
	 * @param port
	 */
	public Comd_bsServer(int port) {
		this.port = port;
		
		try {
						
			workProcId = CbUtil.getResource("ApplicationResource",     "etax.worker.id");
			
			bsSendPort = CbUtil.getResourceInt("ApplicationResource", "jbbk.send.port");
			
			sysMap.setMap("SGG_COD"      , "626");                                     /*��û�ڵ� : �λ��(626), ���̹���û(000)*/
			sysMap.setMap("PROCESS_ID"   , String.valueOf(this.port));                 /*���μ��� ID*/
			sysMap.setMap("PROCESS_NM"   , this.port== this.bsSendPort?"��ܼ۽ſ���":"��ܼ��ſ���"); /*���μ��� ��*/
			sysMap.setMap("THREAD_NM"    , this.port== this.bsSendPort?"��ܼ۽ſ���":"��ܼ��ſ���"); /*������ ��*/

			sysMap.setMap("MANE_STAT"    , "9");                                       /*�⵿����*/
			sysMap.setMap("MANAGE_CD"    , "2");                                       /*�������� 1 : ���� */
			sysMap.setMap("STT_DT"       , CbUtil.getCurrent("yyyyMMddHHmmss"));       /*�����Ͻ� */
			sysMap.setMap("END_DT"       , "");                                        /*�����Ͻ�*/
			sysMap.setMap("LAST_DTM"     , CbUtil.getCurrent("yyyy-MM-dd HH:mm:ss"));  /*���������Ͻ�*/
			sysMap.setMap("LAST_TIMEMIL" , Long.parseLong("0"));
			
		} catch (SqlMapException se) {
			se.printStackTrace();
		} catch (BeansException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}			
		
	}
	
	/**
	 * Runnable interface ������
	 */
	@Override
	public void run() {
		// TODO Auto-generated method stub
		Comd_cmhd headField = new Comd_cmhd();
		
		sockDesc     = new SocketDescImpl(headField.getFieldList(), null, "LENGTH", Constants.TYPEFIELDLEN);

		serverSocket = new UcServerSocket(sockDesc, this, port);
		
		serverSocket.mainLoop();
		
	}

	/**
	 * SocketWorker interface ������
	 */
	@Override
	public void onAccept(SocketChannel socket) {
		// TODO Auto-generated method stub
		String nm = socket.socket().getInetAddress() + ":" + socket.socket().getPort();
		
		log.info("");
		log.info("=================================================");
		log.info("Ŭ���̾�Ʈ�� ����Ǿ����ϴ� FROM::" + nm);
		log.info("=================================================");
		
		/**
		 * ���ο� Ŭ���̾�Ʈ�� ����Ǹ� ���� ������ ���δ�.
		 */
		if(clientSocket != null) {
			log.info("���������� ������ :: " + clientSocket.socket().getRemoteSocketAddress());
			
			try {
				serverSocket.sendNClose(clientSocket.socket().getRemoteSocketAddress(), "���ο� ������ �����Ǿ����ϴ�".getBytes());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		/**
		 * ���� Ŭ���̾�Ʈ�� ������ش�
		 */
		clientSocket  = socket;
		
		if(this.port == this.bsSendPort) { // �۽���Ʈ�̸� �۽� �ƴϸ� ����
			
		}

		sysMap.setMap("SGG_COD"      , "626");                                     /*��û�ڵ� : �λ��(626), ���̹���û(000)*/
		sysMap.setMap("PROCESS_ID"   , String.valueOf(this.port));                 /*���μ��� ID*/
		sysMap.setMap("PROCESS_NM"   , this.port== this.bsSendPort?"��ܼ۽ſ���":"��ܼ��ſ���"); /*���μ��� ��*/
		sysMap.setMap("THREAD_NM"    , this.port== this.bsSendPort?"��ܼ۽ſ���":"��ܼ��ſ���"); /*������ ��*/
		sysMap.setMap("BIGO"         , this.clientSocket.socket().getRemoteSocketAddress().toString());  /*��� : ���༭��IP�� �����Ѵ�. */
		sysMap.setMap("MANE_STAT"    , "1");                                       /*�⵿����*/
		sysMap.setMap("MANAGE_CD"    , "2");                                       /*�������� 1 : ���� */
		sysMap.setMap("STT_DT"       , CbUtil.getCurrent("yyyyMMddHHmmss"));       /*�����Ͻ� */
		sysMap.setMap("LAST_DTM"     , CbUtil.getCurrent("yyyy-MM-dd HH:mm:ss"));  /*���������Ͻ�*/
		sysMap.setMap("LAST_TIMEMIL" , System.currentTimeMillis());
		
		/**
		 * ����������� ���
		 */
		try {
			if(this.sqlService.queryForUpdate("CODMBASE.CODMSYSTUpdate", sysMap) < 1) {
				this.sqlService.queryForInsert("CODMBASE.CODMSYSTInsert", sysMap);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		
	}
	
	/**
	 * SocketWorker interface ������
	 */
	@Override
	public void onClose(SocketChannel socket) {
		// TODO Auto-generated method stub
		int port = socket.socket().getPort();
		
		log.info("======================================================");
		log.info("������ ����Ǿ����ϴ� IP=" + socket.socket().getRemoteSocketAddress() +", PORT=" + port);
		log.info("======================================================");
		
		clientSocket = null;
		
		sysMap.setMap("END_DT"       , CbUtil.getCurrent("yyyyMMddHHmmss"));       /*�����Ͻ�*/
		sysMap.setMap("LAST_DTM"     , CbUtil.getCurrent("yyyy-MM-dd HH:mm:ss"));  /*���������Ͻ�*/
		sysMap.setMap("MANE_STAT"    , "9");                                       /*�⵿����*/
		sysMap.setMap("LAST_TIMEMIL" ,System.currentTimeMillis());
		
		this.sqlService.queryForUpdate("CODMBASE.CODMSYSTUpdate", sysMap);
		
	}


	/**
	 * SocketWorker interface ������
	 */
	@Override
	public void onConnect(SocketChannel socket) {
		// TODO Auto-generated method stub
		
	}

	
	/**
	 * 
	 * @param array
	 * @throws Exception 
	 * @throws IOException 
	 */
	public void sendToClient(byte[] array) throws IOException, Exception {
		
		if(clientSocket == null) {
			throw new Exception("�λ��(��) ȣ��Ʈ�� ������� �ʾҽ��ϴ�.");
		}

		log.info("��� �۽�::" + clientSocket.socket().getRemoteSocketAddress() + ", (" + array.length + ")= " + new String(array, 0, array.length < 260? array.length:260));

		this.serverSocket.send(clientSocket.socket().getRemoteSocketAddress(), array);
	}
	

	/**
	 * SocketWorker interface ������
	 * Flag �� �����Ͽ� ������ ������ �б� �� ó���Ѵ�.
	 */
	@Override
	public void processData(SocketChannel socket, byte[] array, int length) {
		// TODO Auto-generated method stub
		
		MapForm cmMap;
		try {
			
			log.info("��� ��������(" + array.length + ") = " + new String(array, 0, array.length < 260? array.length:260));
			
			cmMap = headField.parseBuffer(array);
			
			// 16������ �α�Ȯ��
			// Utils.dumpAsHex(log, array, "ȣ��Ʈ ���� ����", length);
			
			String sCmd = (String) cmMap.getMap("SR_FLAG");
			String procId = null;
			
			if(cmMap.getMap("SYS_ID").equals("JBBK") && sCmd.equals("S")) {
				procId = workProcId;				
			} else if(cmMap.getMap("SYS_ID").equals("ESCH")&& sCmd.equals("R")) {
				procId = ((String) cmMap.getMap("USER_AREA")).substring(0,4);
			} else {
				log.error("�̻��� ��������(" + array.length + ") = " + new String(array, 0, array.length < 260? array.length:260));
				return;
			}

			log.info("��ܰ� ����Head = " + cmMap +", Ÿ��ID=" + procId);			
			commClient.sendClientData("3", "1", procId , array);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

			log.error("����ó���� �����߻� ::" + e.getMessage());

		}
				
	}


	/**
	 * @param commClient the commClient to set
	 */
	public void setCommClient(UcCommClient commClient) {
		this.commClient = commClient;
	}

	/**
	 * SocketWorker interface ������
	 */
	@Override
	public void sendPoll() {
		// TODO Auto-generated method stub
		
	}

	/**
	 * SocketWorker interface ������
	 * */
	@Override
	public void onTimeout() {
		// TODO Auto-generated method stub
		
		if(System.currentTimeMillis() - (Long) sysMap.getMap("LAST_TIMEMIL") >= 1000*60*10) { // 10���̻� ����ƴٸ� ���¸� UPDATE �Ѵ�

			
			sysMap.setMap("LAST_DTM" , CbUtil.getCurrent("yyyy-MM-dd HH:mm:ss"));

			if(this.clientSocket != null) {
				sysMap.setMap("MANE_STAT"    , "1");                                                             /*�⵿����*/
				sysMap.setMap("BIGO"         , this.clientSocket.socket().getRemoteSocketAddress().toString());  /*��� : ���༭��IP�� �����Ѵ�. */
			}

			sysMap.setMap("LAST_TIMEMIL", System.currentTimeMillis());
			
			/**
			 * ����������� ���
			 */
			if(this.sqlService.queryForUpdate("CODMBASE.CODMSYSTUpdate", sysMap) < 1) {
				this.sqlService.queryForInsert("CODMBASE.ETTCSYSTInsert", sysMap);
			}
			
			log.debug("=========================================================");
			if(this.clientSocket == null) log.info("== �λ��(��) ��ܿ���:" + this.port + "::NULL,"  + this.serverSocket.getConnectionList().size()); 
			else log.info("== �λ��(��) ��ܿ���:" + this.port + "::" + this.clientSocket.socket().getRemoteSocketAddress() + ","  + this.serverSocket.getConnectionList().size());
			log.debug("=========================================================");
			
		}
		
	}

	/**
	 * ���ؽ�Ʈ�� ���Թް� DB���� ����...
	 * @param context
	 */
	public void setContext(ApplicationContext context) {
		// TODO Auto-generated method stub
		this.setAppContext(context);
		this.sqlService = (IbatisService) getAppContext().getBean("baseService_cyber");
	}

	/* --- */
	public void setAppContext(ApplicationContext appContext) {
		this.appContext = appContext;
	}

	public ApplicationContext getAppContext() {
		return appContext;
	}

}
