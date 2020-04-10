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
 *  클래스  ID : Txdm2511 (부산은행) 
 *  변경  이력 :
 * ------------------------------------------------------------------------
 *  작성자      	소속  		    일자            Tag           내용
 * ------------------------------------------------------------------------
 *  송동욱      유채널(주)         2011.06.07         %01%         최초작성
 */
package com.uc.bs.cyber.daemon.txdm2511;

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

import com.uc.bs.cyber.field.Comd_WorkKfField;

import com.uc.bs.cyber.service.kf201001.Kf201001FieldList;
import com.uc.bs.cyber.service.kf201002.Kf201002FieldList;
import com.uc.bs.cyber.service.kf202001.Kf202001FieldList;
import com.uc.bs.cyber.service.kf203001.Kf203001FieldList;
import com.uc.bs.cyber.service.kf203002.Kf203002FieldList;

import com.uc.bs.cyber.service.kf251001.Kf251001FieldList;
import com.uc.bs.cyber.service.kf251005.Kf251005FieldList;
import com.uc.bs.cyber.service.kf251002.Kf251002FieldList;
import com.uc.bs.cyber.service.kf252001.Kf252001FieldList;
import com.uc.bs.cyber.service.kf253001.Kf253001FieldList;
import com.uc.bs.cyber.service.kf253002.Kf253002FieldList;

import com.uc.bs.cyber.service.kf271001.Kf271001FieldList;
import com.uc.bs.cyber.service.kf271002.Kf271002FieldList;
import com.uc.bs.cyber.service.kf272001.Kf272001FieldList;
import com.uc.bs.cyber.service.kf273001.Kf273001FieldList;
import com.uc.bs.cyber.service.kf273002.Kf273002FieldList;
import com.uc.bs.cyber.service.kf276001.Kf276001FieldList;

import com.uc.bs.cyber.service.bs521001.Bs521001FieldList;
import com.uc.bs.cyber.service.bs521002.Bs521002FieldList;
import com.uc.bs.cyber.service.bs523001.Bs523001FieldList;
import com.uc.bs.cyber.service.bs523002.Bs523002FieldList;
import com.uc.bs.cyber.service.bs992001.Bs992001FieldList;
import com.uc.bs.cyber.service.bs502001.Bs502001FieldList;
import com.uc.bs.cyber.service.bs502002.Bs502002FieldList;
import com.uc.bs.cyber.service.bs502101.Bs502101FieldList;

import com.uc.bs.cyber.service.kf201001.Kf201001Service;
import com.uc.bs.cyber.service.kf201002.Kf201002Service;
import com.uc.bs.cyber.service.kf202001.Kf202001Service;
import com.uc.bs.cyber.service.kf203001.Kf203001Service;
import com.uc.bs.cyber.service.kf203002.Kf203002Service;

import com.uc.bs.cyber.service.kf251001.Kf251001Service;
import com.uc.bs.cyber.service.kf251002.Kf251002Service;
import com.uc.bs.cyber.service.kf251005.Kf251005Service;
import com.uc.bs.cyber.service.kf252001.Kf252001Service;
import com.uc.bs.cyber.service.kf253001.Kf253001Service;
import com.uc.bs.cyber.service.kf253002.Kf253002Service;

import com.uc.bs.cyber.service.kf271001.Kf271001Service;
import com.uc.bs.cyber.service.kf271002.Kf271002Service;
import com.uc.bs.cyber.service.kf272001.Kf272001Service;
import com.uc.bs.cyber.service.kf273001.Kf273001Service;
import com.uc.bs.cyber.service.kf273002.Kf273002Service;
import com.uc.bs.cyber.service.kf276001.Kf276001Service;

import com.uc.bs.cyber.service.bs502001.Bs502001Service;
import com.uc.bs.cyber.service.bs502002.Bs502002Service;
import com.uc.bs.cyber.service.bs502101.Bs502101Service;
import com.uc.bs.cyber.service.bs521001.Bs521001Service;
import com.uc.bs.cyber.service.bs521002.Bs521002Service;
import com.uc.bs.cyber.service.bs523001.Bs523001Service;
import com.uc.bs.cyber.service.bs523002.Bs523002Service;

import com.uc.bs.cyber.service.bs992001.Bs992001Service;



//EGTOB FRAME 을 이용하기 위함.
//import com.uc.egtob.net.server.RcvWorker;
//import com.uc.egtob.net.server.RcvServer;

public class Txdm2511 extends RcvWorker{
	
	private IbatisBaseService sqlService_cyber = null;
	private ApplicationContext appContext = null;
	private Thread comdThread = null;
	

	
	private Comd_WorkKfField MSG_HEAD = new Comd_WorkKfField();
	private Kf201001FieldList KFL_201001 = new Kf201001FieldList();
	private Kf201002FieldList KFL_201002 = new Kf201002FieldList();
	private Kf202001FieldList KFL_202001 = new Kf202001FieldList();
	private Kf203001FieldList KFL_203001 = new Kf203001FieldList();
	private Kf203002FieldList KFL_203002 = new Kf203002FieldList();
	
	private Kf251001FieldList KFL_251001 = new Kf251001FieldList();
	private Kf251005FieldList KFL_251005 = new Kf251005FieldList();
	private Kf251002FieldList KFL_251002 = new Kf251002FieldList();
	private Kf252001FieldList KFL_252001 = new Kf252001FieldList();
	private Kf253001FieldList KFL_253001 = new Kf253001FieldList();
	private Kf253002FieldList KFL_253002 = new Kf253002FieldList();
	
	private Kf271001FieldList KFL_271001 = new Kf271001FieldList();
	private Kf271002FieldList KFL_271002 = new Kf271002FieldList();
	private Kf272001FieldList KFL_272001 = new Kf272001FieldList();
	private Kf273001FieldList KFL_273001 = new Kf273001FieldList();
	private Kf273002FieldList KFL_273002 = new Kf273002FieldList();
	private Kf276001FieldList KFL_276001 = new Kf276001FieldList();
	
	private Bs521001FieldList BSL_521001 = new Bs521001FieldList();
	private Bs521002FieldList BSL_521002 = new Bs521002FieldList();
	private Bs523001FieldList BSL_523001 = new Bs523001FieldList();
	private Bs523002FieldList BSL_523002 = new Bs523002FieldList();
	private Bs992001FieldList BSL_992001 = new Bs992001FieldList();
	private Bs502001FieldList BSL_502001 = new Bs502001FieldList();
	private Bs502002FieldList BSL_502002 = new Bs502002FieldList();	
	private Bs502101FieldList BSL_502101 = new Bs502101FieldList();
	
	private MapForm dataMap    = null;
	@SuppressWarnings("unused")
	private String dataSource  = null;
	@SuppressWarnings("unused")
	private byte[] recvBuffer;
	
	/* 생성자 
	 * QUEUE 용
	 * */
	public Txdm2511() throws Exception {
		super();
		// TODO Auto-generated constructor stub

		log.info("============================================");
		log.info("== new RcvWorker( )                 Start ==");
		log.info("============================================");

		
	}
	
	/* 생성자 
	 * 초기화용
	 * */
	public Txdm2511(int annotation) throws Exception {
		// TODO Auto-generated constructor stub
        // nothing...
		
	}	
	
	/*
	 * 트랜잭션용(사용안함)
	 * */
	public Txdm2511(ApplicationContext context, String dataSource) throws Exception {
		
		/*Context 주입...*/
		this.context = context;
		this.dataSource = dataSource;
		
	}
			
	/*생성자*/
	public Txdm2511(ApplicationContext context) throws Exception {
		
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
			
			cmMap.setMap("PRC_GB", "BP");   /*부산은행 처리 데몬 구분*/
			
			log.debug("cmMap = " + cmMap.getMaps());
			
			/* 송수신 전문 저장용*/
			logMap.setMap("SYS_DSC"  , "02"); /*부산은행*/
			logMap.setMap("MSG_ID"   , cmMap.getMap("PROGRAM_ID")); /*전문종별*/
			logMap.setMap("RCV_MSG"  , new String(buffer)); /*수신전문*/
			logMap.setMap("REG_DTM"  , CbUtil.getCurrent("yyyyMMddHHmmss"));
			
			/* 분석된 전문에 따라서 프로그램을 분기한다. */
			if(cmMap.getMap("TX_GUBUN").equals("200")) {  /*통상전문 요청*/
				
				if(cmMap.getMap("PROGRAM_ID").equals("201001")){ // 환경개선부담금 고지내역간략조회 :: 금결원-->사이버(지자체) 
					
					if(cmMap.getMap("RS_FLAG").equals("C")){ //결제원
						
						dataMap = KFL_201001.parseRecvBuffer(buffer);  /*수신전문파싱...*/
						
						log.debug("dataMap = " + dataMap.getMaps());
						
						Kf201001Service kf_201001 = new Kf201001Service(appContext);  /* 201001 업무처리 */
						
						/*수신전문을 처리하고 응답전문을  생성한다.*/
						retBuffer = kf_201001.chk_kf_201001(cmMap, dataMap);
						
						if(log.isDebugEnabled()){
							log.debug("retBuffer = " + new String(retBuffer));
						}
						
					} else { //부산은행
						
						
					}
					
					
				}else if(cmMap.getMap("PROGRAM_ID").equals("201002")){ // 환경개선부담금 고지내역상세조회 :: 금결원-->사이버(지자체)
					
					if(cmMap.getMap("RS_FLAG").equals("C")){ //결제원
						
						dataMap = KFL_201002.parseRecvBuffer(buffer);  /*수신전문파싱...*/
						
						log.debug("dataMap = " + dataMap.getMaps());
						
						Kf201002Service kf_201002 = new Kf201002Service(appContext);  /* 201002 업무처리 */
						
						retBuffer = kf_201002.chk_kf_201002(cmMap, dataMap);
						
						if(log.isDebugEnabled()){
							log.debug("retBuffer = " + new String(retBuffer));
						}
						
					} else { //부산은행
						
						
					}
					
					
				}else if(cmMap.getMap("PROGRAM_ID").equals("202001")){ // 환경개선부담금 납부결과통지 / 예약신청 :: 금결원-->사이버(지자체)
					
					if(cmMap.getMap("RS_FLAG").equals("C")){ //결제원
						
						dataMap = KFL_202001.parseRecvBuffer(buffer);  /*수신전문파싱...*/
						
						log.debug("dataMap = " + dataMap.getMaps());
						
						Kf202001Service kf_202001 = new Kf202001Service(appContext);  /* 202001 업무처리 */
						
						retBuffer = kf_202001.chk_kf_202001(cmMap, dataMap);
						
					} else { //부산은행
						
						
					}
					
					
				}else if(cmMap.getMap("PROGRAM_ID").equals("203001")){ // 환경개선부담금 납부내역간략조회 :: 금결원-->사이버(지자체)
					
					if(cmMap.getMap("RS_FLAG").equals("C")){ //결제원
						
						dataMap = KFL_203001.parseRecvBuffer(buffer);  /*수신전문파싱...*/
						
						log.debug("dataMap = " + dataMap.getMaps());
						
						Kf203001Service kf_203001 = new Kf203001Service(appContext);  /* 203001 업무처리 */
						
						retBuffer = kf_203001.chk_kf_203001(cmMap, dataMap);
						
					} else { //부산은행
						
						
					}
					
				}else if(cmMap.getMap("PROGRAM_ID").equals("203002")){ // 환경개선부담금 납부내역상세조회 :: 금결원-->사이버(지자체)
					
					if(cmMap.getMap("RS_FLAG").equals("C")){ //결제원
						
						dataMap = KFL_203002.parseRecvBuffer(buffer);  /*수신전문파싱...*/
						
						log.debug("dataMap = " + dataMap.getMaps());
						
						Kf203002Service kf_203002 = new Kf203002Service(appContext);  /* 203002 업무처리 */
						
						retBuffer = kf_203002.chk_kf_203002(cmMap, dataMap);
						
					} else { //부산은행
						
						
					}

				}else if(cmMap.getMap("PROGRAM_ID").equals("251001")){ // 상하수도 고지내역간략조회 :: 금결원-->사이버(지자체)
					
					if(cmMap.getMap("RS_FLAG").equals("C")){ //결제원
						
						dataMap = KFL_251001.parseRecvBuffer(buffer);  /*수신전문파싱...*/
						
						log.debug("dataMap = " + dataMap.getMaps());
						
						Kf251001Service kf_251001 = new Kf251001Service(appContext);  /* 251001 업무처리 */
						
						retBuffer = kf_251001.chk_kf_251001(cmMap, dataMap);
						
					} else { //부산은행
						
						
					}
					
					
				}else if(cmMap.getMap("PROGRAM_ID").equals("251005")){ // 상하수도 고지내역간략조회 2 :: 금결원-->사이버(지자체)
					
					if(cmMap.getMap("RS_FLAG").equals("C")){ //결제원
						
						dataMap = KFL_251005.parseRecvBuffer(buffer);  /*수신전문파싱...*/
						
						log.debug("dataMap = " + dataMap.getMaps());
						
						Kf251005Service kf_251005 = new Kf251005Service(appContext);  /* 251005 업무처리 */
						
						retBuffer = kf_251005.chk_kf_251005(cmMap, dataMap);
						
					} else { //부산은행
						
						
					}
					
					
				}else if(cmMap.getMap("PROGRAM_ID").equals("251002")){ // 상하수도 고지내역상세조회 :: 금결원-->사이버(지자체)

					if(cmMap.getMap("RS_FLAG").equals("C")){ //결제원
						
						dataMap = KFL_251002.parseRecvBuffer(buffer);  /*수신전문파싱...*/
						
						log.debug("dataMap = " + dataMap.getMaps());
						
						Kf251002Service kf_251002 = new Kf251002Service(appContext);  /* 251002 업무처리 */
						
						retBuffer = kf_251002.chk_kf_251002(cmMap, dataMap);
						
					} else { //부산은행
						
						
					}
					
					
				}else if(cmMap.getMap("PROGRAM_ID").equals("252001")){ // 상하수도요금 납부결과 통지 :: 금결원-->사이버(지자체)
					
					if(cmMap.getMap("RS_FLAG").equals("C")){ //결제원
						
						dataMap = KFL_252001.parseRecvBuffer(buffer);  /*수신전문파싱...*/
						
						log.debug("dataMap = " + dataMap.getMaps());
						
						Kf252001Service kf_252001 = new Kf252001Service(appContext);  /* 252001 업무처리 */
						
						retBuffer = kf_252001.chk_kf_252001(cmMap, dataMap);
						
					} else { //부산은행
						
						
					}
					
				}else if(cmMap.getMap("PROGRAM_ID").equals("253001")){ // 상하수도 납부내역 간략조회 
				
					if(cmMap.getMap("RS_FLAG").equals("C")){ //결제원
						
						dataMap = KFL_253001.parseRecvBuffer(buffer);  /*수신전문파싱...*/
						
						log.debug("dataMap = " + dataMap.getMaps());
						
						Kf253001Service kf_253001 = new Kf253001Service(appContext);  /* 253001 업무처리 */
						
						retBuffer = kf_253001.chk_kf_253001(cmMap, dataMap);
						
					} else { //부산은행
						
						
					}
					
				}else if(cmMap.getMap("PROGRAM_ID").equals("253002")){ // 상하수도납부내역 상세조회 
					
					if(cmMap.getMap("RS_FLAG").equals("C")){ //결제원
						
						dataMap = KFL_253002.parseRecvBuffer(buffer);  /*수신전문파싱...*/
						
						log.debug("dataMap = " + dataMap.getMaps());
						
						Kf253002Service kf_253002 = new Kf253002Service(appContext);  /* 253001 업무처리 */
						
						retBuffer = kf_253002.chk_kf_253002(cmMap, dataMap);
						
					} else { //부산은행
						
						
					}
				
				}else if(cmMap.getMap("PROGRAM_ID").equals("271001")){ // 세외수입 고지내역 조회 전문 :: 금결원-->사이버(지자체)
					
					if(cmMap.getMap("RS_FLAG").equals("C")){ //결제원
						
						dataMap = KFL_271001.parseRecvBuffer(buffer);  /*수신전문파싱...*/
						
						log.debug("dataMap = " + dataMap.getMaps());
						
						Kf271001Service kf_271001 = new Kf271001Service(appContext);  /* 271001 업무처리 */
						
						retBuffer = kf_271001.chk_kf_271001(cmMap, dataMap);
						
					} else { //부산은행
						
						
					}
					
					
				}else if(cmMap.getMap("PROGRAM_ID").equals("271002")){ // 세외수입 고지내역 상세조회 전문 :: 금결원-->사이버(지자체)
					
					if(cmMap.getMap("RS_FLAG").equals("C")){ //결제원
						
						dataMap = KFL_271002.parseRecvBuffer(buffer);  /*수신전문파싱...*/
						
						log.debug("dataMap = " + dataMap.getMaps());
						
						Kf271002Service kf_271002 = new Kf271002Service(appContext);  /* 271002 업무처리 */
						
						retBuffer = kf_271002.chk_kf_271002(cmMap, dataMap);
						
					} else { //부산은행
						
						
					}
					
				}else if(cmMap.getMap("PROGRAM_ID").equals("272001")){ // 세외수입 납부결과 통지 전문 :: 금결원-->사이버(지자체)
					
					if(cmMap.getMap("RS_FLAG").equals("C")){ //결제원
						
						dataMap = KFL_272001.parseRecvBuffer(buffer);  /*수신전문파싱...*/
						
						log.debug("dataMap = " + dataMap.getMaps());
						
						Kf272001Service kf_272001 = new Kf272001Service(appContext);  /* 272001 업무처리 */
						
						retBuffer = kf_272001.chk_kf_272001(cmMap, dataMap);
						
					} else { //부산은행
						
						
					}
					
				}else if(cmMap.getMap("PROGRAM_ID").equals("273001")){ // 세외수입 납부내역 간략조회 전문
					
					if(cmMap.getMap("RS_FLAG").equals("C")){ //결제원
						
						dataMap = KFL_273001.parseRecvBuffer(buffer);  /*수신전문파싱...*/
						
						log.debug("dataMap = " + dataMap.getMaps());
						
						Kf273001Service kf_273001 = new Kf273001Service(appContext);  /* 272001 업무처리 */
						
						retBuffer = kf_273001.chk_kf_273001(cmMap, dataMap);
						
					} else { //부산은행
						
						
					}
					
				}else if(cmMap.getMap("PROGRAM_ID").equals("273002")){ // 세외수입 납부내역 상세조회 전문
					
					if(cmMap.getMap("RS_FLAG").equals("C")){ //결제원
						
						dataMap = KFL_273002.parseRecvBuffer(buffer);  /*수신전문파싱...*/
						
						log.debug("dataMap = " + dataMap.getMaps());
						
						Kf273002Service kf_273002 = new Kf273002Service(appContext);  /* 273002 업무처리 */
						
						retBuffer = kf_273002.chk_kf_273002(cmMap, dataMap);
						
					} else { //부산은행
						
						
					}
					
				}else if(cmMap.getMap("PROGRAM_ID").equals("276001")){ // 세외수입 일괄납부결과 통지 전문 :: 금결원-->사이버(지자체)
					
					if(cmMap.getMap("RS_FLAG").equals("C")){ //결제원
						
						MapForm mfMap = KFL_272001.parseRecvBuffer(buffer);  /*수신전문파싱...*/
						
						dataMap = KFL_276001.parseRecvReptBuffer(buffer, Integer.parseInt(mfMap.getMap("REQ_CNT").toString()));  /*수신전문파싱...*/
						
						log.debug("dataMap = " + dataMap.getMaps());
						
						Kf276001Service kf_276001 = new Kf276001Service(appContext);  /* 272001 업무처리 */
						
						retBuffer = kf_276001.chk_kf_276001(cmMap, mfMap, dataMap);
						
					} else { //부산은행
						
						
					}	
					
				}else if(cmMap.getMap("PROGRAM_ID").equals("502001")){ // 지방세 납부결과 통지 전문 :: 금결원-->사이버(지자체)
						
					if(cmMap.getMap("RS_FLAG").equals("C")){ //결제원
						
						
					} else { //부산은행
						
						dataMap = BSL_502001.parseRecvBuffer(buffer);  /*수신전문파싱...*/
						
						log.debug("dataMap = " + dataMap.getMaps());
						
						Bs502001Service bs_502001 = new Bs502001Service(appContext);  /* 272001 업무처리 */
						
						retBuffer = bs_502001.chk_bs_502001(cmMap, dataMap);
						
					}	
					
				}else if(cmMap.getMap("PROGRAM_ID").equals("502002")){ // 지방세 납부통지전문(서구청?) :: 어따 쓰는 전문이여....그냥 집어 넣으면 되는겨?(황대리 똑바로 해~라)
					
					if(cmMap.getMap("RS_FLAG").equals("C")){ //결제원
						
						
					} else { //부산은행
						
						dataMap = BSL_502002.parseRecvBuffer(buffer);  /*수신전문파싱...*/
						
						log.debug("dataMap = " + dataMap.getMaps());
						
						Bs502002Service bs_502002 = new Bs502002Service(appContext);  /* 502002 업무처리 */
						
						retBuffer = bs_502002.chk_bs_502002(cmMap, dataMap);
						
						this.isResponse = false;
					}	
					
				}else if(cmMap.getMap("PROGRAM_ID").equals("502101")){ // 공통 납부내역 정정
					
					if(cmMap.getMap("RS_FLAG").equals("C")){ //결제원
						
						
					} else { //부산은행
						
						dataMap = BSL_502101.parseRecvBuffer(buffer);  /*수신전문파싱...*/
						
						log.debug("dataMap = " + dataMap.getMaps());
						
						Bs502101Service bs_502101 = new Bs502101Service(appContext);  /* 502101 업무처리 */
						
						retBuffer = bs_502101.chk_bs_502101(cmMap, dataMap);
						
					}	
					
				}else if(cmMap.getMap("PROGRAM_ID").equals("521001")){ // 지방세 고지내역간략조회  :: 금결원-->사이버(지자체)
					
					if(cmMap.getMap("RS_FLAG").equals("C")){ //결제원
						
						
					} else { //부산은행
						
						dataMap = BSL_521001.parseRecvBuffer(buffer);  /*수신전문파싱...*/
						
						log.debug("dataMap = " + dataMap.getMaps());
						
						Bs521001Service bs_521001 = new Bs521001Service(appContext);  /* 521001 업무처리 */
						
						retBuffer = bs_521001.chk_bs_521001(cmMap, dataMap);
						
					}
					
				}else if(cmMap.getMap("PROGRAM_ID").equals("521002")){ // 지방세 고지내역상세조회  :: 금결원-->사이버(지자체)
					
					if(cmMap.getMap("RS_FLAG").equals("C")){ //결제원
						
						
					} else { //부산은행
						
						dataMap = BSL_521002.parseRecvBuffer(buffer);  /*수신전문파싱...*/
						
						log.debug("dataMap = " + dataMap.getMaps());
						
						Bs521002Service bs_521002 = new Bs521002Service(appContext);  /* 521002 업무처리 */
						
						retBuffer = bs_521002.chk_bs_521002(cmMap, dataMap);
						
					}
					
				}else if(cmMap.getMap("PROGRAM_ID").equals("523001")){ // 지방세 납부내역 간략조회 
					
					if(cmMap.getMap("RS_FLAG").equals("C")){ //결제원
						
						
					} else { //부산은행
						
						dataMap = BSL_523001.parseRecvBuffer(buffer);  /*수신전문파싱...*/
						
						log.debug("dataMap = " + dataMap.getMaps());
						
						Bs523001Service bs_523001 = new Bs523001Service(appContext);  /* 523001 업무처리 */
						
						retBuffer = bs_523001.chk_bs_523001(cmMap, dataMap);
						
					}
					
				}else if(cmMap.getMap("PROGRAM_ID").equals("523002")){ // 지방세 납부내역 상세조회 
					
					if(cmMap.getMap("RS_FLAG").equals("C")){ //결제원
						
						
					} else { //부산은행
						
						dataMap = BSL_523002.parseRecvBuffer(buffer);  /*수신전문파싱...*/
						
						log.debug("dataMap = " + dataMap.getMaps());
						
						Bs523002Service bs_523002 = new Bs523002Service(appContext);  /* 523002 업무처리 */
						
						retBuffer = bs_523002.chk_bs_523002(cmMap, dataMap);
						
					}
					
				} 	
				
			} else if(cmMap.getMap("TX_GUBUN").equals("210")) { /*통상전문 요청응답*/
				
			} else if(cmMap.getMap("TX_GUBUN").equals("420")) { /*취소전문 요청*/
				
				if(cmMap.getMap("PROGRAM_ID").equals("992001")){ /*납부 재(취소)*/
					
					if(cmMap.getMap("RS_FLAG").equals("C")){ /* 결제원으로 부터 응답*/
						
					} else {  /*부산은행으로 부터 응답*/
						
						dataMap = BSL_992001.parseRecvBuffer(buffer);  /*수신전문파싱...*/
						
						log.debug("dataMap = " + dataMap.getMaps());
						
						Bs992001Service bs_992001 = new Bs992001Service(appContext);  /* 523002 업무처리 */
						
						retBuffer = bs_992001.chk_bs_992001(cmMap, dataMap);
					}
					
				}

			} else if(cmMap.getMap("TX_GUBUN").equals("430")) { /*취소전문 요청응답*/

			} else if(cmMap.getMap("TX_GUBUN").equals("700")) { /*집계전문 지시*/

			} else if(cmMap.getMap("TX_GUBUN").equals("710")) { /*집계전문 보고*/

			} else if(cmMap.getMap("TX_GUBUN").equals("800")) { /*통신망전문 지시(요청)*/

			} else if(cmMap.getMap("TX_GUBUN").equals("810")) { /*통신망전문 보고(통지)*/
				
				cmMap.setMap("RS_FLAG"   , "G");       /*지로이용기관(G) */
				cmMap.setMap("RESP_CODE" , "000");     /*응답코드*/
		        cmMap.setMap("GCJ_NO"    , "0260" + CbUtil.lPadString(String.valueOf(SeqNumber()), 8, '0'));     /*이용기관 일련번호*/
		        cmMap.setMap("TX_GUBUN"  , "0810");
		        
		        retBuffer = MSG_HEAD.makeSendBuffer(cmMap, dataMap);
		        
			} else {
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

				logMap.setMap("RES_CD"  , new String(retBuffer).substring(24, 27)); /*처리결과*/
				logMap.setMap("ERR_MSG" , cUtil.msgPrint("", "", "", new String(retBuffer).substring(24, 27))); /*오류메시지*/
				logMap.setMap("REG_DTM" , CbUtil.getCurrent("yyyyMMddHHmmss"));
				logMap.setMap("LAST_DTM", CbUtil.getCurrent("yyyyMMddHHmmss"));
				
				if(sqlService_cyber.queryForUpdate("CODMBASE.CODM_JUNMUN_TXRX_INSERT", logMap) > 0) {
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

			Txdm2511 svr = new Txdm2511(0);
			
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
		
		int connPort =  Utils.getResourceInt("ApplicationResource", "bstc.recv.port");  /*53001*/
		
		RcvServer server = new RcvServer(connPort, "com.uc.bs.cyber.daemon.txdm2511.Txdm2511", 20, 10, "log4j.xml", 4 ,com.uc.core.Constants.TYPEFIELDLEN);

		server.setContext(appContext);
		server.setProcId("2511");
		server.setProcName("부산은행통합연계서버");
		server.setThreadName("thr_2511");
		
		server.initial();
		
		comdThread = new Thread(server);
		
		/**
		 * 데이터 송수신을 위해서 서버를 등록해준다
		 * (서버관리데몬)을 위해서 Threading...
		 */
		comdThread.setName("2511");
		
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
