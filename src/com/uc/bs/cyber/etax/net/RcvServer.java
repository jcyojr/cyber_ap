/**
 *  주시스템명 : 사이버지방세청 고도화
 *  업  무  명 : 공통
 *  기  능  명 : 
 *  클래스  ID : RcvServer
 *  변경  이력 :
 * ------------------------------------------------------------------------
 *  작성자      	소속  		    일자            Tag           내용
 * ------------------------------------------------------------------------
 *  Administrator   u c         2011.04.24         %01%         최초작성
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

	private ArrayList<RcvWorker> workerList = null;  // Thread 생성갯수(20개)
	private ArrayList<Thread> threadList    = null;

	/* property set */
	public void setLogFile(String logFile) {
		this.logFile = logFile;
	}

	public String getLogFile() {
		return logFile;
	}
	
	/**
	 * 생성자
	 * */
	public RcvServer(int port, String worker, String logName) throws Exception {
		
		setLogFile(logName);
		
		this.port = port;
		
		rcvWorker = worker;
		
		initial();
	}
	
	/**
	 * 생성자
	 * */
	public RcvServer(int port, String worker, int maxQue, int procCnt, String logName, int dataLen, int nType) throws Exception {
		// TODO Auto-generated constructor stub
		
		this.setLogFile(logName);

		this.port = port;
		
		rcvWorker = worker;   /* ["com.uc.bs.cyber.etax.server.Server"] Server.class를 정해진 Q 만큼 생성시킨다...*/
		
		maxQuLen  = maxQue; 
		
		this.procCnt = procCnt;
		
		if(dataLen == 0) {
			sockDesc = new SocketDescImpl(0);
		} else {
			FieldList fieldList = new FieldList();
			fieldList.add("LENGTH", dataLen, "H");  //head 길이를 업무에 따라구분한다...
					
			//전문길이를 정하여 수신하는 경우
			sockDesc = new SocketDescImpl(fieldList, "", "LENGTH", nType);
		}

	}
	
	public RcvServer(int port, String worker, int maxQue, int procCnt, String logName, SocketDesc sockDesc) throws Exception {
		// TODO Auto-generated constructor stub
		
		this.setLogFile(logName);

		this.port = port;
		
		rcvWorker = worker;   /* ["com.uc.bs.cyber.etax.server.Server"] Server.class를 정해진 Q 만큼 생성시킨다...*/
		
		maxQuLen  = maxQue; 
		
		this.procCnt = procCnt;
		this.sockDesc = sockDesc;
	}
	

	/**
	 *  환경 초기화
	 * */
	public void initial() throws Exception{

		if(rcvWorker == null) throw new Exception("Worker Class가 정의되지 않았습니다");

		if(this.port <= 0) throw new Exception("Server Port가 정의되지 않았습니다");
				
		log.info("========[" + this.getClass().getName() + " STARTED]========");
		
		sysMap.setMap("SGG_COD"      , this.scg_code);                             /*구청코드 : 부산시(626), 사이버세청(000)*/
		sysMap.setMap("PROCESS_ID"   , this.procId);                               /*프로세스 ID : String.valueOf(this.port) */
		sysMap.setMap("PROCESS_NM"   , this.procName);                             /*프로세스 명*/
		sysMap.setMap("THREAD_NM"    , this.threadName);                           /*쓰레드 명*/

		sysMap.setMap("MANE_STAT"    , "9");                                       /*기동상태*/
		sysMap.setMap("MANAGE_CD"    , "1");                                       /*관리구분 1 : 데몬 */
		sysMap.setMap("STT_DT"       , CbUtil.getCurrent("yyyyMMddHHmmss"));       /*시작일시 */
		sysMap.setMap("END_DT"       , "");                                        /*종료일시*/
		sysMap.setMap("LAST_DTM"     , CbUtil.getCurrent("yyyyMMddHHmmss"));       /*최종수정일시*/
		sysMap.setMap("LAST_TIMEMIL" , Long.parseLong("0"));
		
		/*Server 를 생성하고 Queue 에 할당한다.*/
		this.setWorker(rcvWorker);
		
	}
	
	@Override /* Runnable 추상메서드 구현*/
	public void run() {
		// TODO Auto-generated method stub
		
		if(log.isDebugEnabled()){
			log.debug("=================================================");
			log.debug("sockDesc = " + sockDesc + " port " + port);
			log.debug("=================================================");
		}

		serverSocket = new UcServerSocket(sockDesc, this, port);
				
		// Socket 등록 해준다
		serverSocket.mainLoop();
	}

	@Override /* SocketWorker 추상메서드 구현*/
	public void onAccept(SocketChannel socket) {
		// TODO Auto-generated method stub
		
		String nm = socket.socket().getInetAddress() + ":" + socket.socket().getPort();
	
		/**
		 * 최종 클라이언트를 기억해준다
		 */
		clientSocket  = socket;
		
		if(log.isDebugEnabled()){
			log.debug("");
			log.debug("=================================================");
			log.debug("클라이언트가 연결되었습니다 FROM::" + nm);
			log.debug("=================================================");
		}
		
		sysMap.setMap("BIGO"         , socket.socket().getRemoteSocketAddress().toString());  /*비고 : 클라이언트 접속서버IP를 셋팅한다. */
		sysMap.setMap("MANE_STAT"    , "1");                                       /*기동상태*/
		sysMap.setMap("MANAGE_CD"    , "1");                                       /*관리구분 1 : 데몬 */
		sysMap.setMap("STT_DT"       , CbUtil.getCurrent("yyyyMMddHHmmss"));       /*시작일시 */
		sysMap.setMap("LAST_DTM"     , CbUtil.getCurrent("yyyyMMddHHmmss"));       /*최종수정일시*/
		sysMap.setMap("LAST_TIMEMIL" , System.currentTimeMillis());
		
		/**
		 * 데몬상태정보 등록
		 */
		try {
			//2016.06.20 주석처리
			//if(this.sqlService.queryForUpdate("CODMBASE.CODMSYSTUpdate", sysMap) < 1) {
			//	this.sqlService.queryForInsert("CODMBASE.CODMSYSTInsert", sysMap);
			//}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		
	}

	@Override /* SocketWorker 추상메서드 구현*/
	public void onClose(SocketChannel socket) {
		// TODO Auto-generated method stub
		int port = socket.socket().getPort();
		
		clientSocket = null;
		
		if(log.isDebugEnabled()){
			log.debug("======================================================");
			log.debug("연결이 종료되었습니다 IP=" + socket.socket().getRemoteSocketAddress() +", PORT=" + port);
			log.debug("======================================================");
		}
		
		sysMap.setMap("END_DT"       , CbUtil.getCurrent("yyyyMMddHHmmss"));       /*종료일시*/
		sysMap.setMap("LAST_DTM"     , CbUtil.getCurrent("yyyyMMddHHmmss"));       /*최종수정일시*/
		sysMap.setMap("MANE_STAT"    , "9");                                       /*기동상태*/
		sysMap.setMap("LAST_TIMEMIL" , System.currentTimeMillis());
		
		try {
			//this.sqlService.queryForUpdate("CODMBASE.CODMSYSTUpdate", sysMap);
		} catch (CannotGetJdbcConnectionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	@Override /* SocketWorker 추상메서드 구현*/
	public void onConnect(SocketChannel socket) {
		// TODO Auto-generated method stub
		log.info("================= onConnect() thick raised! ===================");
	}

	@Override /* SocketWorker 추상메서드 구현*/
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
			
			//if(w.getStat() == 1 && blockTime >= 120) {	// 최종 작업시간이 2분이상이면 그냥 죽이자 어차피 못할거같다
			
			if(w.getStat() == 1 && blockTime >= 30) {	// 임시로 8초
				log.fatal("====================================");
				log.fatal("프로세스가 블록됨("+ blockTime + ")");
				log.fatal("====================================");
				
				/* caution warning
				 * 블록되었을 경우 여기서 interrupt 을 걸면 이 thread 에 걸려 있는 잡은 어케 되는지가문제로다.
				 * */

				t.interrupt();
			}
						
			if(t.isAlive() == false || t.isInterrupted()) { // Thread 가 destroy 됐다면
				
				log.fatal("====================================");
				log.fatal("프로세스가 종료됨("+ t + ")");
				log.fatal("====================================");
				
				try {
					//다시 새로운쓰레드를 재설정한다....
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
		
        if(System.currentTimeMillis() - (Long) sysMap.getMap("LAST_TIMEMIL") >= 1000*60*10) { // 10분이상 경과됐다면 상태를 UPDATE 한다
        	
			sysMap.setMap("LAST_DTM" , CbUtil.getCurrent("yyyyMMddHHmmss"));

			if(this.clientSocket != null) {
				sysMap.setMap("MANE_STAT"    , "1");                                                             /*기동상태*/
				sysMap.setMap("BIGO"         , this.clientSocket.socket().getRemoteSocketAddress().toString());  /*비고 : 실행서버IP를 셋팅한다. */
			}
        	
			/*오라클 JDBC 8 버젼에는 오류가 날수 있으므로...*/
			if(sysMap.getMap("BIGO") == null) {
				sysMap.setMap("BIGO"         , "");
			}
			
			sysMap.setMap("LAST_TIMEMIL", System.currentTimeMillis());
			
			/**
			 * 데몬상태정보 등록
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

	@Override /* SocketWorker 추상메서드 구현*/
	public void processData(SocketChannel socket, byte[] array, int length) {
		// TODO Auto-generated method stub
		
		MapForm dataMap       = new MapForm();

		//if(log.isDebugEnabled()){
			//16진수 로그를 표시한다.
		//	Utils.dumpAsHex(log, array, "호스트 수신 전문", length);
		//}
		
		/**
		 * 모니터링 요청시 처리
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
		 * 2010.09.30 --FREEB-- Worker 를 못찾으면 오류메시지를 Return 한다
		 */
		if(worker == null) {
			try {
				
				log.error("=======================================================================");
				log.error(" 클라이언트(" + socket + ") 데이터(" + new String(array) + ") 처리실패!!");
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
		
		//저장된 socket, 수신 buffer 를 Map 에 담아 Queue 에 세팅하면
		//RcvWorker class --> run() 의 queue wait 를 깨운다...
		worker.setQueue(dataMap);
	}

	@Override /* SocketWorker 추상메서드 구현*/
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
			
			/** 2010.09.30 -김대완 ---------------------
			 *  큐 최대값이 넘으면 그냥 Return 시킨다 
			 */			
			if(worker.queueSize() >= maxQuLen) continue;
			
			if(worker.queueSize() < remainSize  ) {
				remainSize = worker.queueSize();
				
				remainIdx = i;
			}			
			
		}
		
		/**
		 * Worker 를 못찾으면 그냥 돌려보낸다
		 */
		if(remainIdx < 0) {
			log.error("=======================================================================");
			log.error("    Queue의 최대값(" + maxQuLen + ")을 초과함");
			log.error("=======================================================================");
			
			return null;
		}
		
		log.debug("remainIdx==" + remainIdx + ", size = " + ((RcvWorker)workerList.get(remainIdx)).queueSize());
		
		return (RcvWorker) workerList.get(remainIdx);
	}
	
	/**
	 * 모니터링 응답건
	 * */
	private byte[] getStatus() {
		// TODO Auto-generated method stub
		StringBuffer sb = new StringBuffer();
		
		sb.append(" ============== Server(" + this.port + ") Status =============== \n");
		for(int i = 0; i< workerList.size(); i++) {
			sb.append(" Worker_" + i + " STAT=" + ((RcvWorker)workerList.get(i)).getStat() + " Queue Size==" + ((RcvWorker)workerList.get(i)).queueSize() + " 처리건수==" + ((RcvWorker)workerList.get(i)).getqCnt() + " 재시작=" +((RcvWorker)workerList.get(i)).getrCnt() + "\n");
		}
		sb.append(" ================================================== \n");
		
		return sb.toString().getBytes();
	}
	
	/**
	 * Worker 스레드 셋팅
	 * Server.class를 Queue 갯수만큼 생성시키고 Queue 에 대입한다.
	 * worker : Server.class 
	 * */
	public void setWorker(String worker) throws InstantiationException, IllegalAccessException, ClassNotFoundException, Exception {

		workerList = new ArrayList<RcvWorker>();
		threadList = new ArrayList<Thread>();
		 
		for(int i = 0; i<procCnt; i++) {

			RcvWorker w = (RcvWorker)Class.forName(worker).newInstance();
			
			w.setContext(this.appContext);  /*Context 연결*/
			
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
	 * 새로운 Worker 스레드 셋팅
	 * 이상하게 된 쓰레드를 제거하고, 새로운 쓰레드로 대체한다.
	 * */
	private void resetWorker(int i, String worker) throws InstantiationException, IllegalAccessException, Exception {
		// TODO Auto-generated method stub
		
		List<?> queue = ((RcvWorker)workerList.get(i)).getQueue();
		
		RcvWorker old = (RcvWorker)workerList.get(i);
		
		RcvWorker w = (RcvWorker)Class.forName(worker).newInstance();
		 
		
		w.setrCnt(old.getrCnt() + 1);
		w.setqCnt(old.getqCnt());
		w.setContext(this.appContext);  /*반드시 다시연결한다. Context 연결*/
		
		w.setServer(serverSocket);
		
		Thread t = new Thread(w, "WORK_" + i);

		if(log.isDebugEnabled()){
			log.debug(worker + " WORK_" + i);
		}
		
		t.start();
		
		log.info("미처리 QUEUE==" + queue.size());
		
		while(!queue.isEmpty()) w.setQueue((MapForm) queue.remove(0));
		
		workerList.set(i, w); threadList.set(i, t);

		old.removeQueue();
	}
	
	/**
	 * 컨텍스트를 주입받고 DB연결 결정...
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
