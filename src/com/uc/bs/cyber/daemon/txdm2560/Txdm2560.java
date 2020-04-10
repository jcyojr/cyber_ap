/**
 *  주시스템명 : 사이버지방세청 고도화
 *  업  무  명 : 공통
 *  기  능  명 : 세외수입(구청) 고지자료조회 연계
 *               com.uc.bs.cyber.etax.* 자원사용
 *               
 *               사이버세청 관리자로 부터 구세외수입연계 전문을 수신받고  
 *               구청에 부과된 연계자료를 사이버세청에 부과한다.
 *               
 *  클래스  ID : Txdm2560 
 *  변경  이력 :
 * ------------------------------------------------------------------------
 *  작성자      	소속  		    일자            Tag           내용
 * ------------------------------------------------------------------------
 *  송동욱      유채널(주)         2011.08.05         %01%         최초작성
 */
package com.uc.bs.cyber.daemon.txdm2560;

import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Iterator;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.UncategorizedSQLException;

import com.uc.bs.cyber.CbUtil;
import com.uc.bs.cyber.etax.net.RcvServer;
import com.uc.bs.cyber.etax.net.RcvWorker;
import com.uc.core.MapForm;
import com.uc.core.misc.XMLFileReader;
import com.uc.core.misc.XmlUtils;
import com.uc.core.spring.service.IbatisBaseService;
import com.uc.core.spring.service.UcContextHolder;
import com.uc.egtob.net.FieldList;

/**
 * @author Administrator
 *
 */
public class Txdm2560 extends RcvWorker{

	private IbatisBaseService sqlService_cyber = null;
	private IbatisBaseService govService  = null;
	private ApplicationContext appContext = null;
	private Thread comdThread = null;
		
	private String dataSource  = null;
	
	private FieldList msgField;
	
	private Txdm2560FieldList TD_2560 = new Txdm2560FieldList();
	
	/*
	 * 생성자
	 */
	public Txdm2560() {
		super();
		// TODO Auto-generated constructor stub

		log.info("============================================");
		log.info("== new RcvWorker( )                 Start ==");
		log.info("============================================");
	}
	
	/* 
	 * 생성자 초기화용
	 * */
	public Txdm2560(int annotation) throws Exception {
		// TODO Auto-generated constructor stub
        // nothing...
	}	

	/*
	 * 트랜잭션용(사용안함)
	 * */
	public Txdm2560(ApplicationContext context, String dataSource) throws Exception {
		
		/*Context 주입...*/
		this.context = context;
		this.dataSource = dataSource;
		
	}
	
	/*
	 * 생성자
	 * */
	public Txdm2560(ApplicationContext context) throws Exception {
		
		/*Context 주입...*/
		setContext(context);
		
		startServer();

	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		ApplicationContext context  = null;
		
		try {

			Txdm2560 svr = new Txdm2560(0);
			
			/*Log4j 설정*/
			CbUtil.setupLog4jConfig(svr, "log4j.txdm2560.xml");
			
			try {
				String[] xmls = XMLFileReader.getAllFilePathArray(CbUtil.getResourcePath(svr, "/"), "SERVICE.XML");

				String strSrc = CbUtil.getResourcePath(svr, "/") + "config/sqlmaps.xml";

				String strDest = CbUtil.getResourcePath(svr, "/") + "config/sqlmapConfig.xml";

				XmlUtils.setXmlAttributes(svr, strSrc, strDest, "sqlMapConfig", "sqlMap", "url", xmls);

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
		
			context = new ClassPathXmlApplicationContext("config/Seoi-Spring-db.xml");
			
			svr.setContext(context);
			
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
		
		RcvServer server = new RcvServer(4642, "com.uc.bs.cyber.daemon.txdm2560.Txdm2560", 10, 5, "log4j.tomcat.xml", 0, com.uc.core.Constants.TYPENOHEAD);
		
		server.setContext(appContext);
		server.setProcId("2560");
		server.setProcName("구청별 세외수입연계(단건)");
		server.setThreadName("thr_2560");
		server.initial();
		
		comdThread = new Thread(server);
		
		/**
		 * 데이터 송수신을 위해서 서버를 등록해준다
		 * (서버관리데몬)을 위해서 Threading...
		 */
		comdThread.setName("2560");
		
		comdThread.start();
		
	}	

	@Override
	public void doDataRecv(SocketChannel socket, byte[] buffer)	throws Exception {
		// TODO Auto-generated method stub
		
		log.info("============================================");
		log.info("== doDataRecv(" + socket.socket().getInetAddress() + ") Start ==");
		log.info(" RECV[" + new String(buffer) + "]");
		log.info("============================================");
		
		MapForm dataMap = null;

		/**
		 * 응답전문 필드 선언
		 */
		this.msgField = new FieldList();
				
		msgField.add("LEN"      ,      4,    "H");    //1 길이
		msgField.add("SGG_COD"  ,      3,    "C");    //2 구청코드
		msgField.add("REG_NO"   ,     13,    "C");    //3 주민번호
		msgField.add("TAX_SNO"  ,      6,    "C");    //4 과세번호
		msgField.add("DIL_GB"   ,      1,    "C");    //5 체납구분
		msgField.add("RST_COD"  ,      3,    "C");    //6 결과코드
		msgField.add("RST_MSG"  ,    200,    "C");    //7 결과메세지
		
		String resMsg  = "";
		
		/*정상크기의 전문이라 생각함.*/
		if(buffer.length >= 25) {
						
			dataMap = TD_2560.parseBuff(buffer);      /*수신전문 파싱(전체)*/
			
			
			if(dataMap.getMap("SGG_COD").toString().length() != 3){
				dataMap.setMap("RST_COD", "110");
				dataMap.setMap("RST_MSG", "구청코드가 이상합니다.");
			} else {
				
				/*구청별 DB 연결정보 셋팅*/
				setDataSource("NI_" + dataMap.getMap("SGG_COD").toString());
				
				if (dataMap.getMap("DIL_GB").equals("0")) {
					log.info(" 건별 정기분 세외수입 연계");
					/*건별 정기분 세외수입 연계*/
					resMsg += txdm2560_Rglr_JobProcess(dataMap);
					log.info(" 건별 체납분 세외수입 연계");
					/*건별 체납분 세외수입 연계*/
					resMsg += txdm2560_fail_JobProcess(dataMap);
					
				} else if(dataMap.getMap("DIL_GB").equals("1")) {
					
					log.info(" 건별 정기분 세외수입 연계");
					/*건별 정기분 세외수입 연계*/
					resMsg += txdm2560_Rglr_JobProcess(dataMap);
					
				} else if(dataMap.getMap("DIL_GB").equals("2")) {
					
					log.info(" 건별 체납분 세외수입 연계");
					
					/*건별 체납분 세외수입 연계*/
					resMsg += txdm2560_fail_JobProcess(dataMap);
				}
				
				/*받고 처리했으니 받은만큼 돌려조야지.*/
				dataMap.setMap("LEN"    , msgField.getFieldListLen());
				dataMap.setMap("RST_MSG", resMsg);
				
				log.info("=============================================");
				log.info("resMsg = " + resMsg);
				log.info("정상글자포함위치 = " + resMsg.indexOf("정상"));
				log.info("=============================================");
				
				if(resMsg.indexOf("정상") >= 0) {
					dataMap.setMap("RST_COD", "000");
				}else{
					dataMap.setMap("RST_COD", "111");
				}

			}
			
		}else{

			dataMap = new MapForm();
			
			dataMap.setMap("LEN"    , msgField.getFieldListLen());
			dataMap.setMap("SGG_COD", "");
			dataMap.setMap("REG_NO" , "");
			dataMap.setMap("TAX_SNO", "");
			dataMap.setMap("DIL_GB" , "");
			
			dataMap.setMap("RST_COD", "094");
			dataMap.setMap("RST_MSG", "잘못된 전문을 수신하였습니다.");
			
		}

		this.retBuffer = msgField.makeMessageByte(dataMap);	
	}

	@Override
	public void setContext(Object obj) {
		// TODO Auto-generated method stub
		this.appContext = (ApplicationContext) obj;
		
		sqlService_cyber = (IbatisBaseService) appContext.getBean("baseService_cyber");
	}
	/*--------------------------------------------------------------*/
	/*--------------------property set -----------------------------*/
	/*--------------------------------------------------------------*/
	
	public ApplicationContext getAppContext() {
		return appContext;
	}
	
	/*구청을 DB연결을 구별하기 위함...*/
	public void setDataSource(String dataSource) {
		this.dataSource = dataSource;
		
		govService = (IbatisBaseService) appContext.getBean("baseService");
		
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
	
	/*----------------------------------------------*/
	/* 일련번호 가져오기 테스트*/
	/*----------------------------------------------*/
    @SuppressWarnings("unused")
	private int SeqNumber(){
    	
    	return (Integer)sqlService_cyber.queryForBean("CODMBASE.CODM_GETSEQNO", "00");

    }


	/*정기분 실연계업무 시작*/
	private String txdm2560_Rglr_JobProcess(MapForm dataMap){
		
		if(log.isInfoEnabled()){	
			log.info("====================건별 부과자료연계 시작======================");
			log.info(" govid["+dataMap.getMap("SGG_COD")+"], orgcd["+this.dataSource+"]");
			log.info("========================NON TRANSACTION=========================");
		}
		
		String Retvalue = "";
		
		/*-------------Context 주입 및 연계DB설정----------------*/
		this.context = appContext;
		UcContextHolder.setCustomerType(this.dataSource);
		/*-------------------------------------------------------*/
		
		long elapseTime1 = 0;
		long elapseTime2 = 0;
		long t_elpTime1 = 0;
		long t_elpTime2 = 0;
		
		try {
			
			/*---------------------------------------------------------*/
			/*1. 부가연계자료 업무 처리.                               */
			/*---------------------------------------------------------*/
			if(log.isInfoEnabled()){
				log.info("====================건별 부가연계자료 시작    ======================");
			}
			/*처음 기준시간 셋팅*/
			elapseTime1 = System.currentTimeMillis();
			t_elpTime1 = System.currentTimeMillis();
			
			dataMap.setMap("BS_NOT_IN_SEMOK",  not_in_semok());
			
			/*전자납부 고지내역을 조회한다.(NIS)*/
			ArrayList<MapForm> weNidLevyList = govService.queryForList("TXDM2560.SELECT_LEVY_INFO_LIST", dataMap);
			
			long ins = 0;
			
			if (weNidLevyList.size()  >  0)   {
				
				/*전자납부자료 1건씩 fetch 처리 */
				for ( int rec_cnt = 0;  rec_cnt < weNidLevyList.size();  rec_cnt++)   {
					
					MapForm mfNidLevyList =  weNidLevyList.get(rec_cnt);
					
					/*혹시나...because of testing */
					if (mfNidLevyList == null  ||  mfNidLevyList.isEmpty() )   {
						continue;
					}
					
					/*기본 Default 값 설정 */
					mfNidLevyList.setMap("BU_ADD_YN"   , "N" );       /*부가가치세구분 'N'           */
					mfNidLevyList.setMap("TAX_CNT"     ,  0  );       /*부과순번 0                   */
					
					mfNidLevyList.setMap("PROC_CLS"    , "1" );       /*가상계좌채번구번 default '1' */
					mfNidLevyList.setMap("DEL_YN"      , "N" );       /*삭제여부         default 'N' */

					/**
					 * 어떤문제인지...
					 * 값이나 key 가 null 인 경우 오류가 발생하므로
					 * 값이 null 이면 ''로 바꿔주도록 처리한다.....2011.08.01-FreeB------
					 */
					nullToStr(mfNidLevyList);
					
					try {

						/*신규 INSERT*/
						mfNidLevyList.setMap("CUD_OPT"     , "1" );       /*자료등록구분(신)           */
						/*표준세외수입부과 테이블 입력*/
						if (sqlService_cyber.queryForUpdate("TXDM2560.INSERT_PUB_SEOI_LEVY", mfNidLevyList) > 0 ){
							/*...*/							
						}
						
					}catch (Exception e){
						
						/*중복이 발생하거나 제약조건에 어긋나는 경우체크*/
						if (e instanceof DuplicateKeyException){
							
							try{
								
								/*기존정보 업데이트*/
								mfNidLevyList.setMap("CUD_OPT"     , "2" );       /*자료등록구분(업)           */
														
			                    if (sqlService_cyber.queryForUpdate("TXDM2560.UPDATE_PUB_SEOI_LEVY_DETAIL", mfNidLevyList) > 0 ){
                                   /*...*/
								}
								
							}catch (Exception sub_e){
								log.error("오류데이터 = " + mfNidLevyList.getMaps());
								e.printStackTrace();
								throw (RuntimeException) sub_e;
							}
							
						}else{
							log.error("오류데이터 = " + mfNidLevyList.getMaps());
							e.printStackTrace();							
							throw (RuntimeException) e;
						}
						
					}
					
					try {

						/*신규 INSERT*/
						mfNidLevyList.setMap("CUD_OPT"     , "1" );       /*자료등록구분(신)           */
						/*표준세외수입부과 상세테이블 입력 */
						if (sqlService_cyber.queryForUpdate("TXDM2560.INSERT_PUB_SEOI_LEVY_DETAIL", mfNidLevyList) > 0 ){
						   /*...*/	
						}
												
					}catch (Exception e){
						
						/*중복이 발생하거나 제약조건에 어긋나는 경우체크*/
						if (e instanceof DuplicateKeyException){
							
							try{
								
								/*기존정보 업데이트*/
								mfNidLevyList.setMap("CUD_OPT"     , "2" );       /*자료등록구분(업)           */
								
								/*표준세외수입부과 상세테이블 업데이트 */		
			                    if (sqlService_cyber.queryForUpdate("TXDM2560.UPDATE_PUB_SEOI_LEVY", mfNidLevyList) > 0 ){
								   /*...*/	
								}
								
							}catch (Exception sub_e){
								log.error("오류데이터 = " + mfNidLevyList.getMaps());
								e.printStackTrace();
								throw (RuntimeException) sub_e;
							}
							
						}else{
							log.error("오류데이터 = " + mfNidLevyList.getMaps());
							e.printStackTrace();							
							throw (RuntimeException) e;
						}
						
						ins++;
						mfNidLevyList.clear();
					
				    }
					
					if(ins == 0) {
						Retvalue += "오류 : 부과자료가 없습니다. ";
					}else{
						Retvalue += "정상 : 정기분 부과자료연계 건수 (" + ins + ") ";
					}

				}
				
			} else {
				Retvalue += "오류 : 부과자료가 없습니다. ";
			}
			
			
			/*마지막 기준시간 셋팅*/
			elapseTime2 = System.currentTimeMillis();
			
			log.info("부가자료 연계건수 : " + ins + " (EA)");
			log.info("부가자료 연계시간 : " + (elapseTime2 - elapseTime1) / 1000 + " (s)");
			
			/*---------------------------------------------------------*/
			/*2. 부가가치세 업무 처리.                                 */
			/*---------------------------------------------------------*/
			if(log.isInfoEnabled()){
				log.info("====================건별 부가가치세업무 시작  ======================");
			}
			
			elapseTime1 = System.currentTimeMillis();
			
			
			ins = 0;
			
			dataMap.setMap("BUGWA_STAT", "01");  /*정기수시*/
			
			/*부가가치세 고지내역을 조회한다.(NIS)*/
			ArrayList<MapForm> weNidVatLevyList = govService.queryForList("TXDM2560.SELECT_VAT_INFO_LIST", dataMap);
			
			if (weNidVatLevyList.size()  >  0)   {
				
				/*부가세 1건씩 fetch 처리 */
				for ( int rec_cnt = 0;  rec_cnt < weNidVatLevyList.size();  rec_cnt++)   {
					
					MapForm mfNidVatLevyList =  weNidVatLevyList.get(rec_cnt);
					
					mfNidVatLevyList.setMap("BUGWA_STAT", "01");  /*정기수시*/
					
					/*회계정보 31, 41, 61*/
					String cACCT = sqlService_cyber.getOneFieldString("TXDM2560.SELECT_VAT_ACCT_LIST", mfNidVatLevyList);
					
					if (cACCT != null && (cACCT.equals("31") || cACCT.equals("41") || cACCT.equals("61"))){   /*시세외(31) / 군군세외(41,61)만 처리한다.*/
						
						log.info("cACCT = " + cACCT);
						/*
						 * 부가가치세 기 부과자료 삭제
						 * (EX:일반고지로 부과처리되어 있는 부가세 자료를 찻아서 합산전 삭제)
						 * */
						
						if (sqlService_cyber.queryForDelete("TXDM2560.DELETE_EXT_VAT_DETAIL_INFO", mfNidVatLevyList) > 0 ){
							sqlService_cyber.queryForDelete("TXDM2560.DELETE_EXT_VAT_INFO", mfNidVatLevyList);
						}
						
						mfNidVatLevyList.setMap("SIGUNGU_TAX"  , mfNidVatLevyList.getMap("FST_AMT"));
						mfNidVatLevyList.setMap("CHK_ACC"      , cACCT);
						
						/*부과금액 및 OCR밴드를 업데이트 한다.*/
						if (sqlService_cyber.queryForUpdate("TXDM2560.UPDATE_VAT_AMT_DETAIL_SAVE", mfNidVatLevyList) > 0 ){
							sqlService_cyber.queryForUpdate("TXDM2560.UPDATE_VAT_AMT_SAVE", mfNidVatLevyList);
						}
						ins++;
					}
					
				}

				if(ins == 0){
					Retvalue += " : 부가가치세자료 연계없음. ";
				}else{
					Retvalue += " : 정기분 부가가치세자료연계 건수 (" + ins + ") ";
				}
				
			} else {
				Retvalue += " : 부가가치세자료 연계없음. ";
			}

			t_elpTime2 = System.currentTimeMillis();
			
			log.info("수납자료 삭제시간 : " + (elapseTime2 - elapseTime1) / 1000 + " (s)");
			
			log.info("====================건별 부과자료연계 끝  ======================");
			log.info(" govid["+dataMap.getMap("SGG_COD")+"], orgcd["+this.dataSource+"]");
			log.info(" 부과자료연계 시간 : " + CbUtil.formatTime(t_elpTime2 - t_elpTime1));
			log.info("================================================================");
			

		} catch (UncategorizedSQLException use) {
			
            use.printStackTrace();
            
            if(log.isErrorEnabled()){
            	
            	log.error("=========== SQL 오류 발생 ===========");
            	log.error("SQLERRCODE = " + use.getSQLException().getErrorCode());
            	log.error(use.getMessage());
            }
            
            Retvalue = "(1)" + dataMap.getMap("SGG_COD") + "_SQL 장애 ";
            
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Retvalue = "(1)시스템 장애 ";
		}		

		return Retvalue;
	}
	
	/*체납 실연계업무 시작*/
	private String txdm2560_fail_JobProcess(MapForm dataMap){
		
		if(log.isInfoEnabled()){	
			log.info("====================건별 체납자료연계 시작======================");
			log.info(" govid["+dataMap.getMap("SGG_COD")+"], orgcd["+this.dataSource+"]");
			log.info("========================NON TRANSACTION=========================");
		}
		
		String Retvalue = "";
		
		/*-------------Context 주입 및 연계DB설정----------------*/
		this.context = appContext;
		UcContextHolder.setCustomerType(this.dataSource);
		/*-------------------------------------------------------*/
		long elapseTime1 = 0;
		long elapseTime2 = 0;
		long t_elpTime1 = 0;
		long t_elpTime2 = 0;
		
	
		try {
			
			/*---------------------------------------------------------*/
			/*1. 부가연계자료 업무 처리.                               */
			/*---------------------------------------------------------*/
			if(log.isInfoEnabled()){
				log.info("====================건별 체납부가연계자료 시작    ======================");
			}
			/*처음 기준시간 셋팅*/
			elapseTime1 = System.currentTimeMillis();
			t_elpTime1 = System.currentTimeMillis();
			
			dataMap.setMap("BS_NOT_IN_SEMOK",  not_in_semok());

			/*전자납부 체납고지내역을 조회한다.(NIS)*/
			ArrayList<MapForm> weNidLevyList = govService.queryForList("TXDM2560.SELECT_FAIL_LEVY_INFO_LIST", dataMap);
			
			long ins = 0;
			
			if (weNidLevyList.size()  >  0)   {
				
				/*전자납부자료 1건씩 fetch 처리 */
				for ( int rec_cnt = 0;  rec_cnt < weNidLevyList.size();  rec_cnt++)   {
					
					MapForm mfNidLevyList =  weNidLevyList.get(rec_cnt);
					
					/*혹시나...because of testing */
					if (mfNidLevyList == null  ||  mfNidLevyList.isEmpty() )   {
						continue;
					}
					
					/*기본 Default 값 설정 */
					mfNidLevyList.setMap("BU_ADD_YN"   , "N" );       /*부가가치세구분 'N'           */
					mfNidLevyList.setMap("TAX_CNT"     ,  0  );       /*부과순번 0                   */
					mfNidLevyList.setMap("PROC_CLS"    , "1" );       /*가상계좌채번구번 default '1' */
					mfNidLevyList.setMap("DEL_YN"      , "N" );       /*삭제여부         default 'N' */
					
					/**
					 * 어떤문제인지...
					 * 값이나 key 가 null 인 경우 오류가 발생하므로
					 * 값이 null 이면 ''로 바꿔주도록 처리한다.....2011.08.01-FreeB------
					 */
					nullToStr(mfNidLevyList);
					

					try {

						/*신규 INSERT*/
						mfNidLevyList.setMap("CUD_OPT"     , "1" );       /*자료등록구분(신)           */
						/*표준세외수입부과 테이블 입력*/
						sqlService_cyber.queryForUpdate("TXDM2560.INSERT_PUB_SEOI_LEVY", mfNidLevyList);
						
					}catch (Exception e){
						
						/*중복이 발생하거나 제약조건에 어긋나는 경우체크*/
						if (e instanceof DuplicateKeyException){
							
							try{
								
								/*기존정보 업데이트*/
								mfNidLevyList.setMap("CUD_OPT"     , "2" );       /*자료등록구분(업)           */
														
								sqlService_cyber.queryForUpdate("TXDM2560.UPDATE_PUB_SEOI_LEVY", mfNidLevyList);
								
							}catch (Exception sub_e){
								log.error("오류데이터 = " + mfNidLevyList.getMaps());
								e.printStackTrace();
								throw (RuntimeException) sub_e;
							}
							
						}else{
							log.error("오류데이터 = " + mfNidLevyList.getMaps());
							e.printStackTrace();							
							throw (RuntimeException) e;
						}
						
					}
					

					try {

						/*신규 INSERT*/
						mfNidLevyList.setMap("CUD_OPT"     , "1" );       /*자료등록구분(신)           */
						/*표준세외수입부과 테이블 입력*/
						sqlService_cyber.queryForUpdate("TXDM2560.INSERT_PUB_SEOI_LEVY_DETAIL", mfNidLevyList);
						
					}catch (Exception e){
						
						/*중복이 발생하거나 제약조건에 어긋나는 경우체크*/
						if (e instanceof DuplicateKeyException){
							
							try{
								
								/*기존정보 업데이트*/
								mfNidLevyList.setMap("CUD_OPT"     , "2" );       /*자료등록구분(업)           */
														
								sqlService_cyber.queryForUpdate("TXDM2560.UPDATE_PUB_SEOI_LEVY_DETAIL", mfNidLevyList);
								
							}catch (Exception sub_e){
								log.error("INSERT 오류데이터 = " + mfNidLevyList.getMaps());
								e.printStackTrace();
								throw (RuntimeException) sub_e;
							}
							
						}else{
							log.error("시스템 오류데이터 = " + mfNidLevyList.getMaps());
							e.printStackTrace();							
							throw (RuntimeException) e;
						}
						
					}
					
					ins++;
					mfNidLevyList.clear();
					
				}
				
				if(ins == 0) {
					Retvalue += "오류 : 체납부과자료가 없습니다. ";
				}else{
					Retvalue += "정상 : 체납분 부과자료연계 건수 (" + ins + ") ";
				}
				
				
				
			} else {
				
				Retvalue += "오류 : 체납부과자료가 없습니다. ";
				
			}
			
			
			/*마지막 기준시간 셋팅*/
			elapseTime2 = System.currentTimeMillis();
			
			log.info("체납 부가자료 연계건수 : " + ins + " (EA)");
			log.info("체납 부가자료 연계시간 : " + (elapseTime2 - elapseTime1) / 1000 + " (s)");
			
			/*---------------------------------------------------------*/
			/*2. 체납부가가치세 업무 처리.                                 */
			/*---------------------------------------------------------*/
			if(log.isInfoEnabled()){
				log.info("====================건별 체납부가가치세업무 시작  ======================");
			}
						
			elapseTime1 = System.currentTimeMillis();
			
			dataMap.setMap("BUGWA_STAT", "01");  /*정기수시*/
			
			ins = 0;
			/*부가가치세 고지내역을 조회한다.(NIS)*/
			ArrayList<MapForm> weNidVatLevyList = govService.queryForList("TXDM2560.SELECT_FAIL_VAT_INFO_LIST", dataMap);
			
			if (weNidVatLevyList.size()  >  0)   {
				
				/*부가세 1건씩 fetch 처리 */
				for ( int rec_cnt = 0;  rec_cnt < weNidVatLevyList.size();  rec_cnt++)   {
					
					MapForm mfNidVatLevyList =  weNidVatLevyList.get(rec_cnt);
					
					mfNidVatLevyList.setMap("BUGWA_STAT", "02");  /*체납*/
					
					/*회계정보 31, 41, 61*/
					String cACCT = sqlService_cyber.getOneFieldString("TXDM2560.SELECT_VAT_ACCT_LIST", mfNidVatLevyList);
					
					if (cACCT != null && (cACCT.equals("31") || cACCT.equals("41") || cACCT.equals("61"))){   /*시세외(31) / 군군세외(41,61)만 처리한다.*/
						
						log.info("cACCT = " + cACCT);
						/*
						 * 부가가치세 기 부과자료 삭제
						 * (EX:일반고지로 부과처리되어 있는 부가세 자료를 찻아서 합산전 삭제)
						 * */

						if (sqlService_cyber.queryForDelete("TXDM2560.DELETE_EXT_VAT_DETAIL_INFO", mfNidVatLevyList) > 0 ){
							sqlService_cyber.queryForDelete("TXDM2560.DELETE_EXT_VAT_INFO", mfNidVatLevyList);
						}
						
						log.info("SIGUNGU_TAX = " + mfNidVatLevyList.getMap("FST_AMT"));
						log.info("THLVY_KEY = " + mfNidVatLevyList.getMap("THLVY_KEY"));
						
						mfNidVatLevyList.setMap("SIGUNGU_TAX"  , mfNidVatLevyList.getMap("FST_AMT"));
						mfNidVatLevyList.setMap("CHK_ACC"      , cACCT);
						
						/*부과금액 및 OCR밴드를 업데이트 한다.*/
						if (sqlService_cyber.queryForUpdate("TXDM2560.UPDATE_VAT_AMT_DETAIL_SAVE", mfNidVatLevyList) > 0 ){
							sqlService_cyber.queryForUpdate("TXDM2560.UPDATE_VAT_AMT_SAVE", mfNidVatLevyList);
						}
						ins++;
					}
					
				}
				
				if(ins == 0) {
					Retvalue += " : 체납부가가치세자료 연계없음. ";
				}else{
					Retvalue += " : 체납분 부가가치세자료연계 건수 (" + ins + ") ";
				}

			} else {
				Retvalue += " : 체납부가가치세자료 연계없음. ";
			}
			
			elapseTime2 = System.currentTimeMillis();
			
			log.debug("부가가치세자료 연계건수 : " + ins + " (EA)");
			log.debug("부가가치세자료 연계시간 : " + (elapseTime2 - elapseTime1) / 1000 + " (s)");
			

			elapseTime2 = System.currentTimeMillis();
			t_elpTime2 = System.currentTimeMillis();
			
			
			log.info("====================건별 부과자료연계 끝  ======================");
			log.info(" govid["+dataMap.getMap("SGG_COD")+"], orgcd["+this.dataSource+"]");
			log.info(" 부과자료연계 시간 : [" + CbUtil.formatTime(t_elpTime2 - t_elpTime1) + "]");
			log.info("================================================================");
			
		} catch (UncategorizedSQLException use) {
			
            use.printStackTrace();
            
            if(log.isErrorEnabled()){
            	
            	log.error("=========== SQL 오류 발생 ===========");
            	log.error("SQLERRCODE = " + use.getSQLException().getErrorCode());
            	log.error(use.getMessage());
            	
            }

            Retvalue = "(2)" + dataMap.getMap("SGG_COD") + "_SQL 장애 ";
            
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
			Retvalue = "(2)시스템 장애 ";
		}			

		return Retvalue;
	}
	

	/*오라클 8 :: 을 사용하는 JDBC 드라이버용*/
	@SuppressWarnings("unchecked")
	private void nullToStr(MapForm mapForm) {
		// TODO Auto-generated method stub
		Iterator<String> iterForm =  mapForm.getKeyList().iterator();
		
		while(iterForm.hasNext()) {
			String keyNm =  iterForm.next();
			if(mapForm.getMap(keyNm) == null) mapForm.setMap(keyNm, "");
		}
		
	}
	
	/*세외수입에서 제외시켜야 할 과목*/
	private String not_in_semok() {
	
		StringBuffer sb = new StringBuffer();
		String Retval = "";
		try {
	
			ArrayList<MapForm> alNoSemokList =  sqlService_cyber.queryForList("CODMBASE.NOSEOI", null);
			
			if (alNoSemokList.size()  >  0)   {
				
				for ( int rec_cnt = 0;  rec_cnt < alNoSemokList.size();  rec_cnt++)   {
					
					MapForm mpNoSemokList =  alNoSemokList.get(rec_cnt);
					
					/*혹시나...because of testing */
					if (mpNoSemokList == null  ||  mpNoSemokList.isEmpty() )   {
						continue;
					}
					
					sb.append("'").append(mpNoSemokList.getMap("SEMOK")).append("'").append(",");

				}
				
				Retval = sb.toString().substring(0, sb.toString().length() -1);
				
			} 
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	
		return Retval;
	}	
}
