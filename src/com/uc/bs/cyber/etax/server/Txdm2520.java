/**
 *  주시스템명 : 사이버지방세청 고도화
 *  업  무  명 : 공통
 *  기  능  명 : 위택스 통합수납연계서버를 기동하고 송수신데이터를 처리한다...
 *  클래스  ID : Txdm2520 :: TEST
 *  변경  이력 :
 * ------------------------------------------------------------------------
 *  작성자      	소속  		    일자            Tag           내용
 * ------------------------------------------------------------------------
 *  Administrator   u c         2011.04.24         %01%         최초작성
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

//EGTOB FRAME 을 이용하기 위함.
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
	
	/* 생성자 
	 * QUEUE 용
	 * */
	public Txdm2520() throws Exception {
		super();
		// TODO Auto-generated constructor stub

		log.info("============================================");
		log.info("== new RcvWorker( )                 Start ==");
		log.info("============================================");

		
	}
	
	/* 생성자 
	 * 초기화용
	 * */
	public Txdm2520(int annotation) throws Exception {
		// TODO Auto-generated constructor stub
        // nothing...
		
	}	
	
	/*
	 * 트랜잭션용(사용안함)
	 * */
	public Txdm2520(ApplicationContext context, String dataSource) throws Exception {
		
		/*Context 주입...*/
		this.context = context;
		this.dataSource = dataSource;
		
	}
			
	/*생성자*/
	public Txdm2520(ApplicationContext context) throws Exception {
		
		/*Context 주입...*/
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
		
		recvBuffer = null;

		try {
			
			/*
			 * 전문처리...(트랜잭션처리)
			 * */
			recvBuffer =  buffer;
						
			/*트랜잭션 시작...*/
			mainTransProcess();

		} catch (Exception e) {
			
			e.printStackTrace();
		}

	}
	
	
	/* Main Thread 
	 * 테스트 실행시만 사용합니다...
	 * */
	public static void main(String args[]) {

		ApplicationContext context  = null;
		
		try {

			Txdm2520 svr = new Txdm2520(0);
			
			/*Log4j 설정*/
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
	
	/* 서버 시작...*/
	private static void startServer() throws Exception {
		
		/**
		 * SERVER START
		 * port     : Listen Port
         * worker   : Worker 쓰레드(class)
         * maxQue   : 최대 Queue 크기
    	 * procCnt  : 프로세스(쓰레드) 갯수
		 */
		
		int connPort =  Utils.getResourceInt("ApplicationResource", "wetax.recv.port");  /*9983*/
		
		RcvServer server = new RcvServer(connPort, "com.uc.bs.cyber.etax.server.Txdm2520", 20, 20, "log4j.tomcat.xml", 6, com.uc.core.Constants.TYPEFIELDLEN);

		server.setContext(appContext);
		
		comdThread = new Thread(server);
		
		/**
		 * 데이터 송수신을 위해서 서버를 등록해준다
		 * (서버관리데몬)을 위해서 Threading...
		 */
		comdThread.setName("2520");
		
		comdThread.start();
		
	}
	
	
	/*----------------------------------------------*/
	/* 일련번호 가져오기 테스트*/
	/*----------------------------------------------*/
    @SuppressWarnings("unused")
	private int SeqNumber(){
    	return (Integer)sqlService_cyber.queryForBean("CODMBASE.CODM_GETSEQNO", "00");
    }
	

	/*
	 * 수신전문을 DB에 저장한다...
	 * @param type : 전문구분
	 * @param buffer : 송수신전문
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
			
			
			/*수신전문을 저장한다...*/
			sqlService_cyber.queryForInsert("CODMBASE.CODM_JUNMUN_LOG_SAVE", dataMap);
			
			
		} catch (DuplicateKeyException dke) { /* 중복이 발생한 경우 */
			
			dke.printStackTrace();
			
			dataMap.setMap("RSLT_COD", "44100-100");
			dataMap.setMap("RSLT_MESSAGE", "전문오류!! 이미 등록된 전문입니다");
			
			retBuffer = rtnList.getBuff(dataMap);
			retStr = "44100-100";
			
		} catch (SQLException se) {
			se.printStackTrace();
			
			try {

				if(se.getErrorCode() == 1) {
					
					dataMap.setMap("RSLT_COD", "44100-100");
					dataMap.setMap("RSLT_MESSAGE", "전문오류!! 이미 등록된 전문입니다");
					
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
	
	/*트랜잭션을 실행하기 위한 함수.
	 * -- 함수 실행시 : transactionJob() 함수 자동 실행...
	 * -- transactionJob 함수 영역에서만 트랜잭션이 동작한다.
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
	
	/*트랜잭션 처리*/
	@Override
	public void transactionJob() {
		// TODO Auto-generated method stub
		
		String saveRet = "";
		
		try {
			
			log.info("[=========== 여긴 transactionJob() :: 트랜잭션처리영역 입니다. ========== ]");
			
			/*거래구분만 파싱한다...(거래종류를 알기 위함)*/
			dataMap = chkList.parseBuff(recvBuffer);
			
			if (((String) dataMap.getMap("CO_TRAN")).equals("532001")) { 
				/*
				 * 개별수납건에 대한 전문 처리
				 * */
				
				saveRet = saveTeleGram((String) dataMap.getMap("CO_TRAN"), recvBuffer);  /*수신전문 저장*/
				
				if(saveRet.equals("")) {   /*정상적인 전문인경우*/
					
					dataMap = FL_532001.parseBuff(recvBuffer);      /*수신전문 파싱(전체)*/
	
					if(log.isDebugEnabled()){
						log.debug(dataMap); 
					}
					
					// 카드수납인지 계좌이체인지 구분 기능 추가
					// 현재 전문에 판단할 수 있는 근거는 카드승인번호에 값이 있는지 없는지 이므로 고걸로만 판단한다
					if(!((String) dataMap.getMap("CARD_REQ_NO")).trim().equals("")) { /* 카드승인번호가 있으면 카드수납 */
						
						dataMap.setMap("BNK_CD", ((String) dataMap.getMap("BANK_COD")).trim().substring(2, 5));
						
					} else { /* 아니면 계좌이체 수납 */
						
						dataMap.setMap("BNK_CD", ((String) dataMap.getMap("BANK_COD")).trim().substring(0, 3));
				
					}
	
					We532001Service chkweTax = new We532001Service(appContext);  /* 532001 업무처리 */
	
					saveRet = chkweTax.chkweTax(dataMap);
	
					retBuffer = rtnList.CYB532001(saveRet);
				}
				
			} else if (((String) dataMap.getMap("CO_TRAN")).equals("532002")) {
				/*
				 * 총괄수납건에 대한 전문 처리
				 * */
				
				saveRet = saveTeleGram((String) dataMap.getMap("CO_TRAN"), recvBuffer);  /*수신전문 저장*/
				
				if(saveRet.equals("")) {   /*정상적인 전문인경우*/
					
					dataMap = FL_532002.parseBuff(recvBuffer);
					
					if(log.isDebugEnabled()){
						log.debug(dataMap); 
					}
					
                    if(!((String) dataMap.getMap("CARD_REQ_NO")).trim().equals("")) { /* 카드승인번호가 있으면 카드수납 */
						
						dataMap.setMap("BNK_CD", ((String) dataMap.getMap("BANK_COD")).trim().substring(2, 5));
						
					} else { /* 아니면 계좌이체 수납 */
						
						dataMap.setMap("BNK_CD", ((String) dataMap.getMap("BANK_COD")).trim().substring(0, 3));
				
					}
					
                    We532002Service chkweTax = new We532002Service(appContext);  /* 532002 업무처리 */
                    
                    saveRet = chkweTax.chkweTax(dataMap);
                	
					retBuffer = rtnList.CYB532002(saveRet);
					
				}
				
				
				
			} else if (((String) dataMap.getMap("CO_TRAN")).equals("992001")) {
				/*
				 * 취소 전문 처리
				 * */
				
				saveRet = saveTeleGram((String) dataMap.getMap("CO_TRAN"), recvBuffer);  /*수신전문 저장*/
				
				if(saveRet.equals("")) {   /*정상적인 전문인경우*/
					
					dataMap = FL_992001.parseBuff(recvBuffer);
					
					if(log.isDebugEnabled()){
						log.debug(dataMap); 
					}
					
					We992001Service chkweTax = new We992001Service(appContext);  /* 532002 업무처리 */
					
					saveRet = chkweTax.chkweTax(dataMap);
					
					retBuffer = rtnList.CYB532002(saveRet);
					
				}
				
				
			} else {
				/*
				 * 그외 다른 업무구분이 온 경우 오류처리...
				 * */
				
				rtnList.setField("RSLT_COD"     , "44000-000");
				rtnList.setField("RSLT_MESSAGE" , "거래구분코드 오류입니다");
				retBuffer = (byte[]) rtnList.getBuff();
				 
				log.error("============================================");
				log.error("== doDataRecv() Error End 거래구분코드 오류 ==");
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
