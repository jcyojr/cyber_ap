/**
 *  주시스템명 : 사이버지방세청 고도화
 *  업  무  명 : 공통
 *  기  능  명 : 결제원 연계서버를 기동하고 송수신데이터를 처리한다...
 *               결제원 요청 시 수납자료를 전송한다.
 *               
 *               실제 결재원과 부산은행 모두다 처리 할 수 있으나
 *               수신PORT로 구분하여 부산은행 / 결제원으로 구분한다..
 *               여기는 부산은행
 *               
 *               com.uc.bs.cyber.etax.* 자원사용
 *  클래스  ID : Txdm2512 (부산은행) 
 *  변경  이력 :
 * ------------------------------------------------------------------------
 *  작성자      	소속  		    일자            Tag           내용
 * ------------------------------------------------------------------------
 *  송동욱      유채널(주)         2011.06.07         %01%         최초작성
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


//EGTOB FRAME 을 이용하기 위함.
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
	
	/* 생성자 
	 * QUEUE 용
	 * */
	public Txdm2512() throws Exception {
		super();
		// TODO Auto-generated constructor stub

		log.info("============================================");
		log.info("== new RcvWorker( )                 Start ==");
		log.info("============================================");

		
	}
	
	/* 생성자 
	 * 초기화용
	 * */
	public Txdm2512(int annotation) throws Exception {
		// TODO Auto-generated constructor stub
        // nothing...
		
	}	
	
	/*
	 * 트랜잭션용(사용안함)
	 * */
	public Txdm2512(ApplicationContext context, String dataSource) throws Exception {
		
		/*Context 주입...*/
		this.context = context;
		this.dataSource = dataSource;
		
	}
			
	/*생성자*/
	public Txdm2512(ApplicationContext context) throws Exception {
		
		/*Context 주입...*/
		setAppContext(context);
		
		sqlService_cyber = (IbatisBaseService) appContext.getBean("baseService_cyber");
			
		startServer();
		
	}

	/*=====================================================*/
	/*업무를 구현하시오....................................*/
	/*=====================================================*/
	/* RcvWorker 에서 수신된 소켓, 수신데이터를 처리한다...
	 * 추상메서드 구현 */
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
			 * 전문처리...(트랜잭션처리)
			 * */
			recvBuffer =  buffer;
						
			/* 수신헤드분석 */
			
			cmMap = MSG_HEAD.parseHeadBuffer(buffer);
			
			cmMap.setMap("PRC_GB", "BV");   /*부산은행 상수도 조회납부 처리 데몬 구분*/
			
			log.debug("cmMap = " + cmMap.getMaps());
			
			/* 송수신 전문 저장용*/
			logMap.setMap("SYS_DSC"  , "05"); /*부산은행 가상계좌*/
			logMap.setMap("MSG_ID"   , cmMap.getMap("PROGRAM_ID")); /*전문종별*/
			logMap.setMap("RCV_MSG"  , new String(buffer)); /*수신전문*/
			logMap.setMap("REG_DTM"  , CbUtil.getCurrent("yyyyMMddHHmmss"));
			
			/* 분석된 전문에 따라서 프로그램을 분기한다. */
			if(cmMap.getMap("TX_GUBUN").equals("0200")) {  /*통상전문 요청*/
				
				if(cmMap.getMap("PROGRAM_ID").equals("300")){ //입금 및 입금취소 거래내역 전송
											
					log.debug("입금 및 입금취소 거래   PROGRAM_ID : " + cmMap.getMap("PROGRAM_ID"));
					dataMap = BSL_531001.parseRecvBuffer(buffer);  /*수신전문파싱...*/
					
					log.debug("dataMap = " + dataMap.getMaps());
					
					Bs531001Service BS_531001 = new Bs531001Service(appContext);  /* 531001 업무처리 */
					
					/*수신전문을 처리하고 응답전문을  생성한다.*/
					retBuffer = BS_531001.chk_bs_531001(cmMap, dataMap);
					
					
					
				}else if(cmMap.getMap("PROGRAM_ID").equals("400")){ // 입금계좌 사전 수취조회
					
					log.debug("입금계좌 사전 수취조회   PROGRAM_ID : " + cmMap.getMap("PROGRAM_ID"));
					dataMap = BSL_531002.parseRecvBuffer(buffer);  /*수신전문파싱...*/
					
					log.debug("dataMap = " + dataMap.getMaps());
					
					Bs531002Service bs_531002 = new Bs531002Service(appContext);  /* 531002 업무처리 */
					
					retBuffer = bs_531002.chk_bs_531002(cmMap, dataMap);
					
					
				}
				
				else { //부산은행
						
					log.error("이상한 전문수신(" + buffer.length + ") = " + new String(buffer, 0, buffer.length < 260? buffer.length:260));
					
					retBuffer = buffer;
					
				}
														 	
				
			} else if(cmMap.getMap("TX_GUBUN").equals("210")) { /*통상전문 요청응답*/
				
			}else {
				log.error("이상한 전문수신(" + buffer.length + ") = " + new String(buffer, 0, buffer.length < 260? buffer.length:260));
				
				retBuffer = buffer;
			}
			
			/*트랜잭션 시작...*/
			//DB입력값이 있는 경우...
			//mainTransProcess();
			
			/*마지막으로 송수신전문을 저장한다...*/
			if(!cmMap.getMap("TX_GUBUN").equals("0810")) {
				
				if(retBuffer.length >= 2000) {
					log.info("응답수신전문이 2000bytes 이상: 2000bytes 만 기록남김");
					logMap.setMap("RTN_MSG" , new String(retBuffer).substring(0, 2000)); /*응답전문*/
				} else {
					logMap.setMap("RTN_MSG" , new String(retBuffer)); /*응답전문*/
				}

				logMap.setMap("RES_CD"  , new String(retBuffer).substring(49, 53)); /*처리결과*/
				logMap.setMap("ERR_MSG" , ""); /*오류메시지*/
				logMap.setMap("REG_DTM" , CbUtil.getCurrent("yyyyMMddHHmmss"));
				logMap.setMap("LAST_DTM", CbUtil.getCurrent("yyyyMMddHHmmss"));
				
				if(sqlService_cyber.queryForUpdate("CODMBASE.CODM_JUNMUN_TXRX_INSERT_CO5108", logMap) > 0) {
					log.info("송수신 전문 저장완료!!!");
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			
			/*오류가 발생한 경우...특히 전문이 이상한 경우 고대로 돌려준다.*/
			retBuffer = buffer;
		} finally {
			
			/*마지막에는 스레기 수거 수행 */
			//System.gc();
			
			//System.runFinalization();
			
		}

	}
	
	/* Main Thread 
	 * 테스트 실행시만 사용합니다...
	 * */
	public static void main(String args[]) {

		ApplicationContext context  = null;
		
		try {

			Txdm2512 svr = new Txdm2512(0);
			
			/*Log4j 설정*/
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
	
	/* 서버 시작...*/
	private void startServer() throws Exception {
		
		/**
		 * SERVER START
		 * port     : Listen Port
         * worker   : Worker 쓰레드(class)
         * maxQue   : 최대 Queue 크기
    	 * procCnt  : 프로세스(쓰레드) 갯수
		 */
		
		int connPort =  Utils.getResourceInt("ApplicationResource", "bvmc.recv.port");  /*41052*/
		//int connPort =  41052;
		
		RcvServer server = new RcvServer(connPort, "com.uc.bs.cyber.daemon.txdm2512.Txdm2512", 20, 10, "log4j.xml", 4 ,com.uc.core.Constants.TYPEFIELDLEN);

		server.setContext(appContext);
		server.setProcId("2512");
		server.setProcName("부산은행가상계좌조회납부연계서버");
		server.setThreadName("thr_2512");
		
		server.initial();
		
		comdThread = new Thread(server);
		
		/**
		 * 데이터 송수신을 위해서 서버를 등록해준다
		 * (서버관리데몬)을 위해서 Threading...
		 */
		comdThread.setName("2512");
		
		comdThread.start();
		
	}
	
	/*----------------------------------------------*/
	/* 일련번호 가져오기 테스트*/
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
	
	/*트랜잭션을 실행하기 위한 함수.
	 * -- 함수 실행시 : transactionJob() 함수 자동 실행...
	 * -- transactionJob 함수 영역에서만 트랜잭션이 동작한다.
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
	

	/*트랜잭션 처리*/
	@Override
	public void transactionJob() {
		// TODO Auto-generated method stub

		try {
			
			log.info("[=========== 여긴 transactionJob() :: 트랜잭션처리영역 입니다. ========== ]");
			
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
