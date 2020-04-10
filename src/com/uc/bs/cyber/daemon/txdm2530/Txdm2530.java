/**
 *  주시스템명 : 사이버지방세청 고도화
 *  업  무  명 : 공통
 *  기  능  명 : 위택스 회원정보 전송 및 총괄납부 고지자료 전송시 난수정보 연계를 위한 데몬
 *               com.uc.bs.cyber.etax.* 자원사용
 *  클래스  ID : Txdm2530 
 *  변경  이력 :
 * ------------------------------------------------------------------------
 *  작성자      	소속  		    일자            Tag           내용
 * ------------------------------------------------------------------------
 *  김대완        유채널(주)      2011.06.02         %01%         최초작성
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

//EGTOB FRAME 을 이용하기 위함.
//import com.uc.egtob.net.server.RcvWorker;
//import com.uc.egtob.net.server.RcvServer;

public class Txdm2530 extends RcvWorker{
	
	private IbatisService sqlService = null;
	private ApplicationContext appContext = null;
	private Thread comdThread = null;
		
	
	@SuppressWarnings("unused")
	private String dataSource  = null;
	
	private FieldList msgField;
	

	
	/* 생성자 
	 * QUEUE 용
	 * */
	public Txdm2530() throws Exception {
		super();
		// TODO Auto-generated constructor stub

		log.info("============================================");
		log.info("== new RcvWorker( )                 Start ==");
		log.info("============================================");

		
	}
	
	/* 생성자 
	 * 초기화용
	 * */
	public Txdm2530(int annotation) throws Exception {
		// TODO Auto-generated constructor stub
        // nothing...
		
	}	
	
	/*
	 * 트랜잭션용(사용안함)
	 * */
	public Txdm2530(ApplicationContext context, String dataSource) throws Exception {
		
		/*Context 주입...*/
		this.context = context;
		this.dataSource = dataSource;
		
	}
			
	/*생성자*/
	public Txdm2530(ApplicationContext context) throws Exception {
		
		/*Context 주입...*/
		setAppContext(context);
		//msgField.add("CHALLENGE" ,    100,    "C");    // 전자서명값 원문
		
		startServer();
	}

	/*=====================================================*/
	/*업무를 구현하시오....................................*/
	/*=====================================================*/
	/* RcvWorker 에서 수신된 소켓, 수신데이터를 처리한다...
	 * 추상메서드 구현*/
	@Override
	public void doDataRecv(SocketChannel socket, byte[] buffer)	throws Exception {
		// TODO Auto-generated method stub
		
		log.info("============================================");
		log.info("== doDataRecv(" + socket.socket().getInetAddress() + ") Start ==");
		log.info(" RECV[" + new String(buffer) + "]");
		log.info("============================================");
		
		MapForm logMap = new MapForm();
		
		/**
		 * 응답전문 필드 선언
		 */
		this.msgField = new FieldList();
		
		msgField.add("RSLT_COD"  ,   	9,    "C");    // 결과코드
		msgField.add("RSLT_MSG"  ,     50,    "C");    // 결과메시지
		
		MapForm dataMap = new MapForm();
		
		if(buffer.length >= 32) {
		
			/* 송수신 전문 저장용*/
			logMap.setMap("SYS_DSC"  , "03"); /*위택스*/
			logMap.setMap("MSG_ID"   , "Txdm2530"); /*전문종별*/
			logMap.setMap("RCV_MSG"  , new String(buffer)); /*수신전문*/
			logMap.setMap("REG_DTM"  , CbUtil.getCurrent("yyyyMMddHHmmss"));
			
			try {
				
				dataMap = sqlService.queryForMap
									("SELECT PLGN USR_DIV, TRIM(CALL) CALL, LENGTH(TRIM(PLGN)) + 2 MSG_SIZE FROM CO4101_TB WHERE NANSU='" + new String(buffer) + "'");

				if(dataMap == null || dataMap.size() == 0) {
					// 일치하는 정보가 없음
					dataMap = new MapForm();

					dataMap.setMap("RSLT_COD", "24110-101");
					dataMap.setMap("RSLT_MSG", "요청한 난수값이 조회되지 않았습니다.");
					
				} else {
					msgField.add("MSG_SIZE"  ,   	9,    "H");    // 데이터부길이
					msgField.add("USR_DIV"   ,   	2,    "H");    // 인증서구분(01:개인, 02:개인사업자
					msgField.add("CALL" ,    dataMap.getMap("CALL").toString().length(),    "C");    // 전자서명값 원문
					
					dataMap.setMap("RSLT_COD", "24110-000");
					dataMap.setMap("RSLT_MSG", "정상 처리되었습니다");
				}
				
			} catch (Exception e) {
				
				dataMap = new MapForm();
				
				dataMap.setMap("RSLT_COD", "24110-201");
				dataMap.setMap("RSLT_MSG", "내부시스템 오류가 발생했습니다");
				// 시스템 오류입니다
				e.printStackTrace();
			}
			
		} else {
			// 난수 포맷이 맞지않습니다
			dataMap = new MapForm();
			
			dataMap.setMap("RSLT_COD", "24110-102");
			dataMap.setMap("RSLT_MSG", "요청한 난수값이 비정상 포맷입니다");
		}

		this.retBuffer = msgField.makeMessageByte(dataMap);	

		logMap.setMap("RTN_MSG" , new String(retBuffer)); /*응답전문*/
		logMap.setMap("RES_CD"  , ""); /*처리결과*/
		logMap.setMap("ERR_MSG" , ""); /*오류메시지*/
		logMap.setMap("REG_DTM" , CbUtil.getCurrent("yyyyMMddHHmmss"));
		logMap.setMap("LAST_DTM", CbUtil.getCurrent("yyyyMMddHHmmss"));
		
		if(sqlService.queryForUpdate("CODMBASE.CODM_JUNMUN_TXRX_INSERT", logMap) > 0) {
			log.info("송수신 전문 저장완료!!!");
		}
		
		
	}
	
	
	/* Main Thread 
	 * 테스트 실행시만 사용합니다...
	 * */
	public static void main(String args[]) {

		ApplicationContext context  = null;
		
		try {

			Txdm2530 svr = new Txdm2530(0);
			
			/*Log4j 설정*/
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
	
	/* 서버 시작...*/
	private void startServer() throws Exception {
		
		/**
		 * SERVER START
		 * port     : Listen Port
         * worker   : Worker 쓰레드(class)
         * maxQue   : 최대 Queue 크기
    	 * procCnt  : 프로세스(쓰레드) 갯수
		 */
		
		int connPort =  Utils.getResourceInt("ApplicationResource", "wetax.recv_nan.port");  /*9382*/
				
		RcvServer server = new RcvServer(connPort, "com.uc.bs.cyber.daemon.txdm2530.Txdm2530", 10, 5, "log4j.tomcat.xml", 0, com.uc.core.Constants.TYPENOHEAD);

		// server.setDataLen(32);
		
		server.setContext(appContext);
		server.setProcId("2530");
		server.setProcName("난수정보수신서버");
		server.setThreadName("thr_2530");
		
		server.initial();
		
		comdThread = new Thread(server);
		
		/**
		 * 데이터 송수신을 위해서 서버를 등록해준다
		 * (서버관리데몬)을 위해서 Threading...
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
