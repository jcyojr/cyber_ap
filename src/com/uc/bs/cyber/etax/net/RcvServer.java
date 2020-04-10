/**
 *  �ֽý��۸� : ���̹����漼û ��ȭ
 *  ��  ��  �� : ����
 *  ��  ��  �� : 
 *  Ŭ����  ID : RcvServer
 *  ����  �̷� :
 * ------------------------------------------------------------------------
 *  �ۼ���      	�Ҽ�  		    ����            Tag           ����
 * ------------------------------------------------------------------------
 *  Administrator   u c         2011.04.24         %01%         �����ۼ�
 */
package com.uc.bs.cyber.etax.net;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.CannotGetJdbcConnectionException;

import com.uc.egtob.net.SocketWorker;
import com.uc.egtob.net.SocketDesc;
import com.uc.egtob.net.UcServerSocket;
import com.uc.egtob.net.FieldList;
import com.uc.egtob.net.SocketDescImpl;
import com.uc.core.MapForm;
import com.uc.core.misc.Utils;
import com.uc.core.spring.service.IbatisBaseService;
import com.uc.bs.cyber.CbUtil;
import com.uc.bs.cyber.etax.net.RcvWorker;


public class RcvServer implements Runnable, SocketWorker{

	protected Log log = LogFactory.getLog(this.getClass());
	
	protected MapForm sysMap = new MapForm();
	
	protected IbatisBaseService sqlService  = null;
	
	private UcServerSocket serverSocket = null;
	
	private SocketDesc sockDesc         = null;
	
	private SocketChannel  clientSocket = null;
	
	private ApplicationContext appContext;

	private int dataLen     = 0;
	
	private int maxQuLen    = 10;
	
	private int procCnt     = 10;
	
	private int  port;
	
	private String  rcvWorker    = null;
	
	private String logFile       = null;
	
	private String scg_code      = "000";
	
	private String procName      = "";
	
	private String procId        = "";
	
	private String threadName    = "";
	
	public void setScg_code(String scgCode) {
		scg_code = scgCode;
	}
	public void setProcName(String procName) {
		this.procName = procName;
	}
	public void setProcId(String procId) {
		this.procId = procId;
	}
	public void setThreadName(String threadName) {
		this.threadName = threadName;
	}

	private ArrayList<RcvWorker> workerList = null;  // Thread ��������(20��)
	private ArrayList<Thread> threadList    = null;

	/* property set */
	public void setLogFile(String logFile) {
		this.logFile = logFile;
	}

	public String getLogFile() {
		return logFile;
	}
	
	/**
	 * ������
	 * */
	public RcvServer(int port, String worker, String logName) throws Exception {
		
		setLogFile(logName);
		
		this.port = port;
		
		rcvWorker = worker;
		
		initial();
	}
	
	/**
	 * ������
	 * */
	public RcvServer(int port, String worker, int maxQue, int procCnt, String logName, int dataLen, int nType) throws Exception {
		// TODO Auto-generated constructor stub
		
		this.setLogFile(logName);

		this.port = port;
		
		rcvWorker = worker;   /* ["com.uc.bs.cyber.etax.server.Server"] Server.class�� ������ Q ��ŭ ������Ų��...*/
		
		maxQuLen  = maxQue; 
		
		this.procCnt = procCnt;
		
		if(dataLen == 0) {
			sockDesc = new SocketDescImpl(0);
		} else {
			FieldList fieldList = new FieldList();
			fieldList.add("LENGTH", dataLen, "H");  //head ���̸� ������ ���󱸺��Ѵ�...
					
			//�������̸� ���Ͽ� �����ϴ� ���
			sockDesc = new SocketDescImpl(fieldList, "", "LENGTH", nType);
		}

	}
	
	public RcvServer(int port, String worker, int maxQue, int procCnt, String logName, SocketDesc sockDesc) throws Exception {
		// TODO Auto-generated constructor stub
		
		this.setLogFile(logName);

		this.port = port;
		
		rcvWorker = worker;   /* ["com.uc.bs.cyber.etax.server.Server"] Server.class�� ������ Q ��ŭ ������Ų��...*/
		
		maxQuLen  = maxQue; 
		
		this.procCnt = procCnt;
		this.sockDesc = sockDesc;
	}
	

	/**
	 *  ȯ�� �ʱ�ȭ
	 * */
	public void initial() throws Exception{

		if(rcvWorker == null) throw new Exception("Worker Class�� ���ǵ��� �ʾҽ��ϴ�");

		if(this.port <= 0) throw new Exception("Server Port�� ���ǵ��� �ʾҽ��ϴ�");
				
		log.info("========[" + this.getClass().getName() + " STARTED]========");
		
		sysMap.setMap("SGG_COD"      , this.scg_code);                             /*��û�ڵ� : �λ��(626), ���̹���û(000)*/
		sysMap.setMap("PROCESS_ID"   , this.procId);                               /*���μ��� ID : String.valueOf(this.port) */
		sysMap.setMap("PROCESS_NM"   , this.procName);                             /*���μ��� ��*/
		sysMap.setMap("THREAD_NM"    , this.threadName);                           /*������ ��*/

		sysMap.setMap("MANE_STAT"    , "9");                                       /*�⵿����*/
		sysMap.setMap("MANAGE_CD"    , "1");                                       /*�������� 1 : ���� */
		sysMap.setMap("STT_DT"       , CbUtil.getCurrent("yyyyMMddHHmmss"));       /*�����Ͻ� */
		sysMap.setMap("END_DT"       , "");                                        /*�����Ͻ�*/
		sysMap.setMap("LAST_DTM"     , CbUtil.getCurrent("yyyyMMddHHmmss"));       /*���������Ͻ�*/
		sysMap.setMap("LAST_TIMEMIL" , Long.parseLong("0"));
		
		/*Server �� �����ϰ� Queue �� �Ҵ��Ѵ�.*/
		this.setWorker(rcvWorker);
		
	}
	
	@Override /* Runnable �߻�޼��� ����*/
	public void run() {
		// TODO Auto-generated method stub
		
		if(log.isDebugEnabled()){
			log.debug("=================================================");
			log.debug("sockDesc = " + sockDesc + " port " + port);
			log.debug("=================================================");
		}

		serverSocket = new UcServerSocket(sockDesc, this, port);
				
		// Socket ��� ���ش�
		serverSocket.mainLoop();
	}

	@Override /* SocketWorker �߻�޼��� ����*/
	public void onAccept(SocketChannel socket) {
		// TODO Auto-generated method stub
		
		String nm = socket.socket().getInetAddress() + ":" + socket.socket().getPort();
	
		/**
		 * ���� Ŭ���̾�Ʈ�� ������ش�
		 */
		clientSocket  = socket;
		
		if(log.isDebugEnabled()){
			log.debug("");
			log.debug("=================================================");
			log.debug("Ŭ���̾�Ʈ�� ����Ǿ����ϴ� FROM::" + nm);
			log.debug("=================================================");
		}
		
		sysMap.setMap("BIGO"         , socket.socket().getRemoteSocketAddress().toString());  /*��� : Ŭ���̾�Ʈ ���Ӽ���IP�� �����Ѵ�. */
		sysMap.setMap("MANE_STAT"    , "1");                                       /*�⵿����*/
		sysMap.setMap("MANAGE_CD"    , "1");                                       /*�������� 1 : ���� */
		sysMap.setMap("STT_DT"       , CbUtil.getCurrent("yyyyMMddHHmmss"));       /*�����Ͻ� */
		sysMap.setMap("LAST_DTM"     , CbUtil.getCurrent("yyyyMMddHHmmss"));       /*���������Ͻ�*/
		sysMap.setMap("LAST_TIMEMIL" , System.currentTimeMillis());
		
		/**
		 * ����������� ���
		 */
		try {
			//2016.06.20 �ּ�ó��
			//if(this.sqlService.queryForUpdate("CODMBASE.CODMSYSTUpdate", sysMap) < 1) {
			//	this.sqlService.queryForInsert("CODMBASE.CODMSYSTInsert", sysMap);
			//}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		
	}

	@Override /* SocketWorker �߻�޼��� ����*/
	public void onClose(SocketChannel socket) {
		// TODO Auto-generated method stub
		int port = socket.socket().getPort();
		
		clientSocket = null;
		
		if(log.isDebugEnabled()){
			log.debug("======================================================");
			log.debug("������ ����Ǿ����ϴ� IP=" + socket.socket().getRemoteSocketAddress() +", PORT=" + port);
			log.debug("======================================================");
		}
		
		sysMap.setMap("END_DT"       , CbUtil.getCurrent("yyyyMMddHHmmss"));       /*�����Ͻ�*/
		sysMap.setMap("LAST_DTM"     , CbUtil.getCurrent("yyyyMMddHHmmss"));       /*���������Ͻ�*/
		sysMap.setMap("MANE_STAT"    , "9");                                       /*�⵿����*/
		sysMap.setMap("LAST_TIMEMIL" , System.currentTimeMillis());
		
		try {
			//this.sqlService.queryForUpdate("CODMBASE.CODMSYSTUpdate", sysMap);
		} catch (CannotGetJdbcConnectionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	@Override /* SocketWorker �߻�޼��� ����*/
	public void onConnect(SocketChannel socket) {
		// TODO Auto-generated method stub
		log.info("================= onConnect() thick raised! ===================");
	}

	@Override /* SocketWorker �߻�޼��� ����*/
	public void onTimeout() {
		// TODO Auto-generated method stub
		// log.debug("================== onTimeout() ===================");
				
		for(int i = 0; i< workerList.size(); i++) {
			
			Thread t = (Thread)threadList.get(i);
			
			RcvWorker w = (RcvWorker)workerList.get(i);
			
			if(log.isDebugEnabled()){
				//log.debug("Worker_" + i + " STAT=" + t.isAlive() + " Queue Size==" + ((RcvWorker)workerList.get(i)).queueSize());
			}
			
			long blockTime  = (System.currentTimeMillis() - w.getProcTime())/1000;
			
			//if(w.getStat() == 1 && blockTime >= 120) {	// ���� �۾��ð��� 2���̻��̸� �׳� ������ ������ ���ҰŰ���
			
			if(w.getStat() == 1 && blockTime >= 30) {	// �ӽ÷� 8��
				log.fatal("====================================");
				log.fatal("���μ����� ��ϵ�("+ blockTime + ")");
				log.fatal("====================================");
				
				/* caution warning
				 * ��ϵǾ��� ��� ���⼭ interrupt �� �ɸ� �� thread �� �ɷ� �ִ� ���� ���� �Ǵ����������δ�.
				 * */

				t.interrupt();
			}
						
			if(t.isAlive() == false || t.isInterrupted()) { // Thread �� destroy �ƴٸ�
				
				log.fatal("====================================");
				log.fatal("���μ����� �����("+ t + ")");
				log.fatal("====================================");
				
				try {
					//�ٽ� ���ο���带 �缳���Ѵ�....
					resetWorker(i, rcvWorker);
					
				} catch (InstantiationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
        if(System.currentTimeMillis() - (Long) sysMap.getMap("LAST_TIMEMIL") >= 1000*60*10) { // 10���̻� ����ƴٸ� ���¸� UPDATE �Ѵ�
        	
			sysMap.setMap("LAST_DTM" , CbUtil.getCurrent("yyyyMMddHHmmss"));

			if(this.clientSocket != null) {
				sysMap.setMap("MANE_STAT"    , "1");                                                             /*�⵿����*/
				sysMap.setMap("BIGO"         , this.clientSocket.socket().getRemoteSocketAddress().toString());  /*��� : ���༭��IP�� �����Ѵ�. */
			}
        	
			/*����Ŭ JDBC 8 �������� ������ ���� �����Ƿ�...*/
			if(sysMap.getMap("BIGO") == null) {
				sysMap.setMap("BIGO"         , "");
			}
			
			sysMap.setMap("LAST_TIMEMIL", System.currentTimeMillis());
			
			/**
			 * ����������� ���
			 */
						
			try{
				
				if(this.sqlService.queryForUpdate("CODMBASE.CODMSYSTUpdate", sysMap) < 1) {
					this.sqlService.queryForInsert("CODMBASE.CODMSYSTInsert", sysMap);
				}
			
			}catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override /* SocketWorker �߻�޼��� ����*/
	public void processData(SocketChannel socket, byte[] array, int length) {
		// TODO Auto-generated method stub
		
		MapForm dataMap       = new MapForm();

		//if(log.isDebugEnabled()){
			//16���� �α׸� ǥ���Ѵ�.
		//	Utils.dumpAsHex(log, array, "ȣ��Ʈ ���� ����", length);
		//}
		
		/**
		 * ����͸� ��û�� ó��
		 */
		if(length == 7 && (new String(array)).equals("MONITOR")) {
			
			try {
				serverSocket.send(socket.socket().getRemoteSocketAddress(), getStatus());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return ;
		}
	
		dataMap.setMap("socket", socket);
		dataMap.setMap("buffer", array);
		
		RcvWorker worker = getWorker();
		
		/**
		 * 2010.09.30 --FREEB-- Worker �� ��ã���� �����޽����� Return �Ѵ�
		 */
		if(worker == null) {
			try {
				
				log.error("=======================================================================");
				log.error(" Ŭ���̾�Ʈ(" + socket + ") ������(" + new String(array) + ") ó������!!");
				log.error("=======================================================================");
				serverSocket.send(socket.socket().getRemoteSocketAddress(), ((RcvWorker)workerList.get(0)).getErrMsg(0));
				
				return;
							
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		worker.setServer(serverSocket);
		
		log.debug("QUEUE SIZE== " + worker.queueSize());
		
		//����� socket, ���� buffer �� Map �� ��� Queue �� �����ϸ�
		//RcvWorker class --> run() �� queue wait �� �����...
		worker.setQueue(dataMap);
	}

	@Override /* SocketWorker �߻�޼��� ����*/
	public void sendPoll() {
		// TODO Auto-generated method stub
		
	}

	/**
	 * @return the dataLen
	 */
	public int getDataLen() {
		return dataLen;
	}

	/**
	 * @param dataLen the dataLen to set
	 */
	public void setDataLen(int dataLen) {
		this.dataLen = dataLen;
	}
	
	/**
	 * 
	 */
	private RcvWorker getWorker() {
		
		int remainSize = maxQuLen, remainIdx = -1 ;
		
		for (int i=0; i<workerList.size(); i++) {
			RcvWorker worker = (RcvWorker) workerList.get(i);

			if(((Thread)threadList.get(i)).isAlive() == false) continue;
			
			if(worker.queueSize() == 0 ) return worker;
			
			/** 2010.09.30 -���� ---------------------
			 *  ť �ִ밪�� ������ �׳� Return ��Ų�� 
			 */			
			if(worker.queueSize() >= maxQuLen) continue;
			
			if(worker.queueSize() < remainSize  ) {
				remainSize = worker.queueSize();
				
				remainIdx = i;
			}			
			
		}
		
		/**
		 * Worker �� ��ã���� �׳� ����������
		 */
		if(remainIdx < 0) {
			log.error("=======================================================================");
			log.error("    Queue�� �ִ밪(" + maxQuLen + ")�� �ʰ���");
			log.error("=======================================================================");
			
			return null;
		}
		
		log.debug("remainIdx==" + remainIdx + ", size = " + ((RcvWorker)workerList.get(remainIdx)).queueSize());
		
		return (RcvWorker) workerList.get(remainIdx);
	}
	
	/**
	 * ����͸� �����
	 * */
	private byte[] getStatus() {
		// TODO Auto-generated method stub
		StringBuffer sb = new StringBuffer();
		
		sb.append(" ============== Server(" + this.port + ") Status =============== \n");
		for(int i = 0; i< workerList.size(); i++) {
			sb.append(" Worker_" + i + " STAT=" + ((RcvWorker)workerList.get(i)).getStat() + " Queue Size==" + ((RcvWorker)workerList.get(i)).queueSize() + " ó���Ǽ�==" + ((RcvWorker)workerList.get(i)).getqCnt() + " �����=" +((RcvWorker)workerList.get(i)).getrCnt() + "\n");
		}
		sb.append(" ================================================== \n");
		
		return sb.toString().getBytes();
	}
	
	/**
	 * Worker ������ ����
	 * Server.class�� Queue ������ŭ ������Ű�� Queue �� �����Ѵ�.
	 * worker : Server.class 
	 * */
	public void setWorker(String worker) throws InstantiationException, IllegalAccessException, ClassNotFoundException, Exception {

		workerList = new ArrayList<RcvWorker>();
		threadList = new ArrayList<Thread>();
		 
		for(int i = 0; i<procCnt; i++) {

			RcvWorker w = (RcvWorker)Class.forName(worker).newInstance();
			
			w.setContext(this.appContext);  /*Context ����*/
			
			Thread t = new Thread(w, "WORK_" + i);
			
			if(log.isDebugEnabled()){
				log.debug(worker + " WORK_" + i);
			}
			
			t.start();
			
			workerList.add(w); 
			threadList.add(t);
			
		}

	}
	
	/**
	 * ���ο� Worker ������ ����
	 * �̻��ϰ� �� �����带 �����ϰ�, ���ο� ������� ��ü�Ѵ�.
	 * */
	private void resetWorker(int i, String worker) throws InstantiationException, IllegalAccessException, Exception {
		// TODO Auto-generated method stub
		
		List<?> queue = ((RcvWorker)workerList.get(i)).getQueue();
		
		RcvWorker old = (RcvWorker)workerList.get(i);
		
		RcvWorker w = (RcvWorker)Class.forName(worker).newInstance();
		 
		
		w.setrCnt(old.getrCnt() + 1);
		w.setqCnt(old.getqCnt());
		w.setContext(this.appContext);  /*�ݵ�� �ٽÿ����Ѵ�. Context ����*/
		
		w.setServer(serverSocket);
		
		Thread t = new Thread(w, "WORK_" + i);

		if(log.isDebugEnabled()){
			log.debug(worker + " WORK_" + i);
		}
		
		t.start();
		
		log.info("��ó�� QUEUE==" + queue.size());
		
		while(!queue.isEmpty()) w.setQueue((MapForm) queue.remove(0));
		
		workerList.set(i, w); threadList.set(i, t);

		old.removeQueue();
	}
	
	/**
	 * ���ؽ�Ʈ�� ���Թް� DB���� ����...
	 * @param context
	 */
	public void setContext(ApplicationContext context) {
		// TODO Auto-generated method stub
		this.setAppContext(context);
		this.sqlService = (IbatisBaseService) getAppContext().getBean("baseService_cyber");
	}

	/* --- */
	public void setAppContext(ApplicationContext appContext) {
		this.appContext = appContext;
	}

	public ApplicationContext getAppContext() {
		return appContext;
	}
	
}
