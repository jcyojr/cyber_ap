/**
 *  주시스템명 : 사이버지방세청 고도화
 *  업  무  명 : 공통
 *  기  능  명 : 부산시(구) 대외연계 서버
 *  클래스  ID : Comd_bsServer
 *  변경  이력 :
 * ------------------------------------------------------------------------
 *  작성자      	소속  		    일자            Tag           내용
 * ------------------------------------------------------------------------
 *  김대완       유채널(주)      2010.11.30         %01%         최초작성
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
	 * 생성자...
	 * @param port
	 */
	public Comd_bsServer(int port) {
		this.port = port;
		
		try {
						
			workProcId = CbUtil.getResource("ApplicationResource",     "etax.worker.id");
			
			bsSendPort = CbUtil.getResourceInt("ApplicationResource", "jbbk.send.port");
			
			sysMap.setMap("SGG_COD"      , "626");                                     /*구청코드 : 부산시(626), 사이버세청(000)*/
			sysMap.setMap("PROCESS_ID"   , String.valueOf(this.port));                 /*프로세스 ID*/
			sysMap.setMap("PROCESS_NM"   , this.port== this.bsSendPort?"대외송신연결":"대외수신연결"); /*프로세스 명*/
			sysMap.setMap("THREAD_NM"    , this.port== this.bsSendPort?"대외송신연결":"대외수신연결"); /*쓰레드 명*/

			sysMap.setMap("MANE_STAT"    , "9");                                       /*기동상태*/
			sysMap.setMap("MANAGE_CD"    , "2");                                       /*관리구분 1 : 데몬 */
			sysMap.setMap("STT_DT"       , CbUtil.getCurrent("yyyyMMddHHmmss"));       /*시작일시 */
			sysMap.setMap("END_DT"       , "");                                        /*종료일시*/
			sysMap.setMap("LAST_DTM"     , CbUtil.getCurrent("yyyy-MM-dd HH:mm:ss"));  /*최종수정일시*/
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
	 * Runnable interface 구현부
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
	 * SocketWorker interface 구현부
	 */
	@Override
	public void onAccept(SocketChannel socket) {
		// TODO Auto-generated method stub
		String nm = socket.socket().getInetAddress() + ":" + socket.socket().getPort();
		
		log.info("");
		log.info("=================================================");
		log.info("클라이언트가 연결되었습니다 FROM::" + nm);
		log.info("=================================================");
		
		/**
		 * 새로운 클라이언트가 연결되면 기존 연결을 죽인다.
		 */
		if(clientSocket != null) {
			log.info("기존연결을 종료함 :: " + clientSocket.socket().getRemoteSocketAddress());
			
			try {
				serverSocket.sendNClose(clientSocket.socket().getRemoteSocketAddress(), "새로운 연결이 생성되었습니다".getBytes());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		/**
		 * 최종 클라이언트를 기억해준다
		 */
		clientSocket  = socket;
		
		if(this.port == this.bsSendPort) { // 송신포트이면 송신 아니면 수신
			
		}

		sysMap.setMap("SGG_COD"      , "626");                                     /*구청코드 : 부산시(626), 사이버세청(000)*/
		sysMap.setMap("PROCESS_ID"   , String.valueOf(this.port));                 /*프로세스 ID*/
		sysMap.setMap("PROCESS_NM"   , this.port== this.bsSendPort?"대외송신연결":"대외수신연결"); /*프로세스 명*/
		sysMap.setMap("THREAD_NM"    , this.port== this.bsSendPort?"대외송신연결":"대외수신연결"); /*쓰레드 명*/
		sysMap.setMap("BIGO"         , this.clientSocket.socket().getRemoteSocketAddress().toString());  /*비고 : 실행서버IP를 셋팅한다. */
		sysMap.setMap("MANE_STAT"    , "1");                                       /*기동상태*/
		sysMap.setMap("MANAGE_CD"    , "2");                                       /*관리구분 1 : 데몬 */
		sysMap.setMap("STT_DT"       , CbUtil.getCurrent("yyyyMMddHHmmss"));       /*시작일시 */
		sysMap.setMap("LAST_DTM"     , CbUtil.getCurrent("yyyy-MM-dd HH:mm:ss"));  /*최종수정일시*/
		sysMap.setMap("LAST_TIMEMIL" , System.currentTimeMillis());
		
		/**
		 * 데몬상태정보 등록
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
	 * SocketWorker interface 구현부
	 */
	@Override
	public void onClose(SocketChannel socket) {
		// TODO Auto-generated method stub
		int port = socket.socket().getPort();
		
		log.info("======================================================");
		log.info("연결이 종료되었습니다 IP=" + socket.socket().getRemoteSocketAddress() +", PORT=" + port);
		log.info("======================================================");
		
		clientSocket = null;
		
		sysMap.setMap("END_DT"       , CbUtil.getCurrent("yyyyMMddHHmmss"));       /*종료일시*/
		sysMap.setMap("LAST_DTM"     , CbUtil.getCurrent("yyyy-MM-dd HH:mm:ss"));  /*최종수정일시*/
		sysMap.setMap("MANE_STAT"    , "9");                                       /*기동상태*/
		sysMap.setMap("LAST_TIMEMIL" ,System.currentTimeMillis());
		
		this.sqlService.queryForUpdate("CODMBASE.CODMSYSTUpdate", sysMap);
		
	}


	/**
	 * SocketWorker interface 구현부
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
			throw new Exception("부산시(구) 호스트가 연결되지 않았습니다.");
		}

		log.info("대외 송신::" + clientSocket.socket().getRemoteSocketAddress() + ", (" + array.length + ")= " + new String(array, 0, array.length < 260? array.length:260));

		this.serverSocket.send(clientSocket.socket().getRemoteSocketAddress(), array);
	}
	

	/**
	 * SocketWorker interface 구현부
	 * Flag 를 구분하여 수신한 전문을 분기 및 처리한다.
	 */
	@Override
	public void processData(SocketChannel socket, byte[] array, int length) {
		// TODO Auto-generated method stub
		
		MapForm cmMap;
		try {
			
			log.info("대외 전문수신(" + array.length + ") = " + new String(array, 0, array.length < 260? array.length:260));
			
			cmMap = headField.parseBuffer(array);
			
			// 16진수로 로그확인
			// Utils.dumpAsHex(log, array, "호스트 수신 전문", length);
			
			String sCmd = (String) cmMap.getMap("SR_FLAG");
			String procId = null;
			
			if(cmMap.getMap("SYS_ID").equals("JBBK") && sCmd.equals("S")) {
				procId = workProcId;				
			} else if(cmMap.getMap("SYS_ID").equals("ESCH")&& sCmd.equals("R")) {
				procId = ((String) cmMap.getMap("USER_AREA")).substring(0,4);
			} else {
				log.error("이상한 전문수신(" + array.length + ") = " + new String(array, 0, array.length < 260? array.length:260));
				return;
			}

			log.info("대외계 수신Head = " + cmMap +", 타겟ID=" + procId);			
			commClient.sendClientData("3", "1", procId , array);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

			log.error("전문처리중 오류발생 ::" + e.getMessage());

		}
				
	}


	/**
	 * @param commClient the commClient to set
	 */
	public void setCommClient(UcCommClient commClient) {
		this.commClient = commClient;
	}

	/**
	 * SocketWorker interface 구현부
	 */
	@Override
	public void sendPoll() {
		// TODO Auto-generated method stub
		
	}

	/**
	 * SocketWorker interface 구현부
	 * */
	@Override
	public void onTimeout() {
		// TODO Auto-generated method stub
		
		if(System.currentTimeMillis() - (Long) sysMap.getMap("LAST_TIMEMIL") >= 1000*60*10) { // 10분이상 경과됐다면 상태를 UPDATE 한다

			
			sysMap.setMap("LAST_DTM" , CbUtil.getCurrent("yyyy-MM-dd HH:mm:ss"));

			if(this.clientSocket != null) {
				sysMap.setMap("MANE_STAT"    , "1");                                                             /*기동상태*/
				sysMap.setMap("BIGO"         , this.clientSocket.socket().getRemoteSocketAddress().toString());  /*비고 : 실행서버IP를 셋팅한다. */
			}

			sysMap.setMap("LAST_TIMEMIL", System.currentTimeMillis());
			
			/**
			 * 데몬상태정보 등록
			 */
			if(this.sqlService.queryForUpdate("CODMBASE.CODMSYSTUpdate", sysMap) < 1) {
				this.sqlService.queryForInsert("CODMBASE.ETTCSYSTInsert", sysMap);
			}
			
			log.debug("=========================================================");
			if(this.clientSocket == null) log.info("== 부산시(구) 대외연결:" + this.port + "::NULL,"  + this.serverSocket.getConnectionList().size()); 
			else log.info("== 부산시(구) 대외연결:" + this.port + "::" + this.clientSocket.socket().getRemoteSocketAddress() + ","  + this.serverSocket.getConnectionList().size());
			log.debug("=========================================================");
			
		}
		
	}

	/**
	 * 컨텍스트를 주입받고 DB연결 결정...
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
