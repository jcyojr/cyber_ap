/**
                                                               *  주시스템명 : 사이버지방세청 고도화
 *  업  무  명 : 공통
 *  기  능  명 : 편의점(더존) 연계서버를 기동하고 송수신데이터를 처리한다...
 *            편의점(더존) 요청 시 수납자료를 전송한다.
 *            com.uc.bs.cyber.etax.* 자원사용
 *  클래스  ID : Txdm2610 
 *  변경  이력 :
 * ------------------------------------------------------------------------
 *  작성자      	소속  		    일자            Tag           내용
 * ------------------------------------------------------------------------
 *  임창섭      유채널(주)         2013.08.12         %01%         최초작성
 */
package com.uc.bs.cyber.daemon.txdm2610;

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
import com.uc.bs.cyber.field.Comd_WorkDfField;
import com.uc.bs.cyber.service.df533001.Df533001FieldList;
import com.uc.bs.cyber.service.df533001.Df533001Tax004FieldList;
import com.uc.bs.cyber.service.df533001.Df533001Tax005FieldList;
import com.uc.bs.cyber.service.df533001.Df533001Tax008FieldList;
import com.uc.bs.cyber.service.df533001.Df533001Tax009FieldList;
import com.uc.bs.cyber.service.df533001.Df533001Tax011FieldList;
import com.uc.bs.cyber.service.df201002.Df201002FieldList;
import com.uc.bs.cyber.service.df202001.Df202001FieldList;
import com.uc.bs.cyber.service.df201002.Df201002Service;
import com.uc.bs.cyber.service.df202001.Df202001Service;
import com.uc.bs.cyber.service.df251002.Df251002Service;
import com.uc.bs.cyber.service.df252001.Df252001Service;
import com.uc.bs.cyber.service.df271002.Df271002Service;
import com.uc.bs.cyber.service.df272001.Df272001Service;
import com.uc.bs.cyber.service.df502001.Df502001Service;
import com.uc.bs.cyber.service.df521002.Df521002Service;

//EGTOB FRAME 을 이용하기 위함.
//import com.uc.egtob.net.server.RcvWorker;
//import com.uc.egtob.net.server.RcvServer;

public class Txdm2610 extends RcvWorker{
	
	private IbatisBaseService sqlService_cyber = null;
	private ApplicationContext appContext = null;
	private Thread comdThread = null;
	
	private Comd_WorkDfField MSG_HEAD = new Comd_WorkDfField(); //전문공통부 --
	
	private Df201002FieldList DFL_201002 = new Df201002FieldList();	//환경개선부담금 고지내역 상세조회 --
	private Df202001FieldList DFL_202001 = new Df202001FieldList();	//환경개선부담금 납부결과 통지     --

	private MapForm dataMap    = null;
	@SuppressWarnings("unused")
	private String dataSource  = null;

	private byte[] recvBuffer;
	
	
	/*
	 * 20141001 수기고지서 신고 납부 추가
	 * */
	// 전문데이터 저장시 처리 상태
    // 0 : 전자신고 완료  5 : 테이터 처리 에러   9 : 전문에러
	private String state = "0";
	//전문응답 성공코드
	private String RE_CO = "0000";			
	// 전문 에러 내용
	private String err_msg;
    //전문 처리 
    private MapForm taxMap    = null;
			
	//고지내역 상세조회 --
	private Df533001FieldList GBN_LIST =   new Df533001FieldList();	
	//지방소득세 종소 상세조회 --
	private Df533001Tax004FieldList DFL_004 =   new Df533001Tax004FieldList();
	//지방소득세 양소 상세조회 --
	private Df533001Tax005FieldList DFL_005 =   new Df533001Tax005FieldList();	
	//지방소득세 특별징수 상세조회 --
	private Df533001Tax008FieldList DFL_008 =   new Df533001Tax008FieldList();	
	//주민세 재산분 상세조회
	private Df533001Tax009FieldList DFL_009 =   new Df533001Tax009FieldList();
	//주민세 종업원분 상세조회
	private Df533001Tax011FieldList DFL_011 =   new Df533001Tax011FieldList();
	
	
	/* 생성자 
	 * QUEUE 용
	 * */
	public Txdm2610() throws Exception {
		super();

		log.info("============================================");
		log.info("== new RcvWorker( )                 Start ==");
		log.info("============================================");

	}
	
	/* 생성자 
	 * 초기화용
	 * */
	public Txdm2610(int annotation) throws Exception {
        // nothing...
		
	}	
	
	/*
	 * 트랜잭션용(사용안함)
	 * */
	public Txdm2610(ApplicationContext context, String dataSource) throws Exception {
		
		/*Context 주입...*/
		this.context = context;
		this.dataSource = dataSource;
		
	}
			
	/*생성자*/
	public Txdm2610(ApplicationContext context) throws Exception {
		
		/*Context 주입...*/
		setAppContext(context);
		
		sqlService_cyber = (IbatisBaseService) this.appContext.getBean("baseService_cyber");
			
		startServer();
		
	}

	
	/*=====================================================*/
	/*업무를 구현하시오....................................*/
	/*=====================================================*/
	/* RcvWorker 에서 수신된 소켓, 수신데이터를 처리한다...
	 * 추상메서드 구현 */
	@Override
	public void doDataRecv(SocketChannel socket, byte[] buffer)	throws Exception {
		
		log.info("============================================");
		log.info("== doDataRecv(" + socket.socket().getInetAddress() + ") Start ==");
		log.info(" RECV====[" + new String(buffer) + "]");
		log.info("============================================");
		
		CbUtil cUtil = CbUtil.getInstance();
		
		MapForm cmMap;
		MapForm logMap = new MapForm();
		recvBuffer = null;
		
		byte[] chkJummun = new byte[70];  //체크용 전문(공통전문크기만큼)

		try {
			
			/*
			 * 전문처리...(트랜잭션처리)
			 * */
			recvBuffer =  buffer;
			
			if(recvBuffer.length < 70) {
				//70byte 만큼 채워서 오류를 방지한다...				
				StringBuffer sb = new StringBuffer();
				
				for(int i = 0 ; i < 70 - recvBuffer.length ; i++){
					sb.append("0");
				}
				System.arraycopy(sb.toString().getBytes(), 0, chkJummun, buffer.length, 70 - buffer.length);
				
			} else {
				System.arraycopy(buffer, 0, chkJummun, 0, 70);
			}
						
			/* 수신헤드분석 */
			cmMap = MSG_HEAD.parseHeadBuffer(chkJummun);
			
			cmMap.setMap("PRC_GB", "DP");   /*편의점(더존) 처리 데몬 구분*/
						
			//log.debug("cmMap = " + cmMap.getMaps());
			
			/* 송수신 전문 저장용*/
			logMap.setMap("SYS_DSC"  , "04"); /*편의점(더존)-카드*/
			logMap.setMap("MSG_ID"   , cmMap.getMap("MEDIA_CODE").toString() + cmMap.getMap("TX_GUBUN").toString() + cmMap.getMap("BANK_CODE").toString()); /*전문종별*/
			logMap.setMap("RCV_MSG"  , new String(buffer)); /*수신전문*/
			logMap.setMap("REG_DTM"  , CbUtil.getCurrent("yyyyMMddHHmmss"));
			
			/* 분석된 전문에 따라서 프로그램을 분기한다. */
			if(cmMap.getMap("TX_GUBUN").equals("030")) {  /*상세조회 요청전문*/
			
				dataMap = DFL_201002.parseRecvBuffer(buffer);  /*수신전문파싱...*/
				
				if (dataMap.getMap("TAX_GB").equals("2")) {// 세외수입 고지내역 상세조회 전문 :: 편의점(더존)-->사이버(지자체)
				
					dataMap.setMap("SEMOK_GB", dataMap.getMap("EPAY_NO").toString().substring(5, 6));//전자납부번호요금구분 첫숫자로 세목을 구분한다.
					
					log.debug("SEMOK_GB = " + dataMap.getMap("SEMOK_GB"));
					
					if(dataMap.getMap("SEMOK_GB").equals("9")){ // 환경개선부담금 고지내역상세조회 :: 편의점(더존)-->사이버(지자체)
						
						if(cmMap.getMap("RS_FLAG").equals("DZ")){ //편의점(더존)
							
							log.debug("◆  환경개선부담금 상세조회 dataMap = " + dataMap.getMaps());
							
							Df201002Service df_201002 = new Df201002Service(appContext);  /* 201002 업무처리 */
							
							retBuffer = df_201002.chk_df_201002(cmMap, dataMap);
							
							if(log.isDebugEnabled()){
								log.debug("retBuffer = " + new String(retBuffer));
							}
							
						} else { 
						    retBuffer = mkErrJunmun(cmMap, buffer);
						}
						
					} else { // 환경개선부담금외 세외수입 고지내역 상세조회 전문 :: 편의점(더존)-->사이버(지자체)
						
						if(cmMap.getMap("RS_FLAG").equals("DZ")){ //편의점(더존)
							
							log.debug("◆  세외수입 상세조회 dataMap = " + dataMap.getMaps());
							
							Df271002Service df_271002 = new Df271002Service(appContext);  /* 271002 업무처리 */
							
							retBuffer = df_271002.chk_df_271002(cmMap, dataMap);
							
						} else { 
						    retBuffer = mkErrJunmun(cmMap, buffer);
						}
					}
					
				}else if(dataMap.getMap("TAX_GB").equals("3")){ // 상하수도 고지내역상세조회 :: 편의점(더존)-->사이버(지자체)

					if(cmMap.getMap("RS_FLAG").equals("DZ")){ //편의점(더존)
						
						log.debug("◆  상하수도 상세조회 dataMap = " + dataMap.getMaps());
						
						Df251002Service df_251002 = new Df251002Service(appContext);  /* 251002 업무처리 */
						
						retBuffer = df_251002.chk_df_251002(cmMap, dataMap);
						
					} else { 
						
						retBuffer = mkErrJunmun(cmMap, buffer);
					}
					
				}else if(dataMap.getMap("TAX_GB").equals("1")){ // 지방세 고지내역상세조회  :: 편의점(더존)-->사이버(지자체)
					
					if(cmMap.getMap("RS_FLAG").equals("DZ")){ //편의점(더존)
						
						log.debug("◆  지방세 상세조회 dataMap = " + dataMap.getMaps());
						
						Df521002Service df_521002 = new Df521002Service(appContext);  /* 521002 업무처리 */
						
						retBuffer = df_521002.chk_df_521002(cmMap, dataMap);						
										
					} else { 
                        retBuffer = mkErrJunmun(cmMap, buffer);
                    }

				} 	
					
			} else if(cmMap.getMap("TX_GUBUN").equals("040")) { /*상세조회 요청응답*/
				
					
			} else if(cmMap.getMap("TX_GUBUN").equals("050")) { /*납부처리 요청*/

				dataMap = DFL_202001.parseRecvBuffer(buffer);  /*수신전문파싱...*/
				
				if (dataMap.getMap("TAX_GB").equals("2")) {// 세외수입 납부처리 요청전문 :: 편의점(더존)-->사이버(지자체)
					
					dataMap.setMap("SEMOK_GB", dataMap.getMap("EPAY_NO").toString().substring(5, 6));//전자납부번호요금구분 첫숫자로 세목을 구분한다.
					
					log.debug("SEMOK_GB = " + dataMap.getMap("SEMOK_GB"));					
					
					if(dataMap.getMap("SEMOK_GB").equals("9")){ // 환경개선부담금 납부결과통지:: 편의점(더존)-->사이버(지자체)
						
						if(cmMap.getMap("RS_FLAG").equals("DZ")){ //편의점(더존)
							
							log.debug("◆  환경개선부담금 납부요청 dataMap = " + dataMap.getMaps());
							
							Df202001Service df_202001 = new Df202001Service(appContext);  /* 202001 업무처리 */
							
							retBuffer = df_202001.chk_df_202001(cmMap, dataMap);
							
						} else { 
							
							retBuffer = mkErrJunmun(cmMap, buffer);
						}
					}else {
						
						if(cmMap.getMap("RS_FLAG").equals("DZ")){ //편의점(더존)
							
							log.debug("◆  세외수입 납부요청 dataMap = " + dataMap.getMaps());
							
							Df272001Service df_272001 = new Df272001Service(appContext);  /* 272001 업무처리 */
							
							retBuffer = df_272001.chk_df_272001(cmMap, dataMap);
							
						} else { //잘못수신된 전문
							
							/* 여기선 편의점(더존)으부터 수신된 전문을 수신하면 안됨
							 * 따라서 오류를 셋팅하여 주던지..아님..그냥 생까던지 해야함.*/
							
							retBuffer = mkErrJunmun(cmMap, buffer);
							
						}
					}
								
				}else if(dataMap.getMap("TAX_GB").equals("3")){ // 상하수도요금 납부결과 통지 :: 편의점(더존)-->사이버(지자체)
					
					if(cmMap.getMap("RS_FLAG").equals("DZ")){ //편의점(더존)
						
						log.debug("◆  상하수도 납부요청 dataMap = " + dataMap.getMaps());
						
						Df252001Service df_252001 = new Df252001Service(appContext);  /* 252001 업무처리 */
						
						retBuffer = df_252001.chk_df_252001(cmMap, dataMap);
						
					} else { 
						
						retBuffer = mkErrJunmun(cmMap, buffer);
					}
					
				}else if(dataMap.getMap("TAX_GB").equals("1")){ // 지방세 납부결과 통지 전문 :: 편의점(더존)-->사이버(지자체)

				    if(cmMap.getMap("RS_FLAG").equals("DZ")){ //편의점(더존)

						log.debug("◆  지방세 납부요청 dataMap = " + dataMap.getMaps());
						
						Df502001Service df_502001 = new Df502001Service(appContext);  /* 272001 업무처리 */
						
						retBuffer = df_502001.chk_df_502001(cmMap, dataMap);
									
					} else { //잘못수신된 전문
						
						/* 여기선 편의점(더존)으부터 수신된 전문을 수신하면 안됨
						 * 따라서 오류를 셋팅하여 주던지..아님..그냥 생까던지 해야함.*/
						
						retBuffer = mkErrJunmun(cmMap, buffer);	
					}
				}
							
//			} else if(cmMap.getMap("TX_GUBUN").equals("060")) { /*납부처리 요청응답*/

//			} else if(cmMap.getMap("TX_GUBUN").equals("700")) { /*집계전문 지시*/
//
//			} else if(cmMap.getMap("TX_GUBUN").equals("710")) { /*집계전문 보고*/

			} else if(cmMap.getMap("TX_GUBUN").equals("800")) { /*통신망전문 지시(요청)*/
				
				cmMap.setMap("RESP_CODE" , "0000");     /*응답코드*/
		        cmMap.setMap("TX_GUBUN"  , "090");
				
				retBuffer = MSG_HEAD.makeSendBuffer(cmMap, dataMap);
				
			} else if(cmMap.getMap("TX_GUBUN").equals("090")) { /*통신망전문 보고(통지)*/

			} 
			
			/*
			 * 바로결제 송수신 전문 처리
			 * 정상 분만 신고 납부 처리한다. 
			 * 납부지연일수 = 0, 가산금 = 0
			 */
			else if(cmMap.getMap("TX_GUBUN").equals("150")) { 

				//log.debug("buffer :: "+buffer);
				/*수신전문파싱...*/
				dataMap = GBN_LIST.parseRecvBuffer(buffer);  
				
				//log.debug("전문확인 dataMap :: "+dataMap);
				
					
				// 주민세 종소
				if(dataMap.getMap("TAX_ITEM").equals("140001")){
						
					/*수신전문파싱...*/
					taxMap = DFL_004.parseRecvBuffer(buffer);  
					
					//전문관리번호
					taxMap.setMap("JM_NO", cmMap.getMap("BCJ_NO").toString());
					//은행(카드)코드, 조회시엔 "000"
					taxMap.setMap("BANK_CD", cmMap.getMap("BANK_CODE").toString());
					//전송일시 - "YYYYMMDDhhmmss"
					taxMap.setMap("TX_DATETIME", cmMap.getMap("TX_DATETIME").toString());
						
					//log.debug("전문파싱확인 taxMap :: "+taxMap);
						
					if(taxMap.getMap("NAPBU_GB").equals("1")){
						
						log.info("종합소득 신고 시작");
						//납부지연 일수가 0보다 크면 오류 처리.20141001 - 표준에 신고 할수 없다.
						if(Integer.parseInt(taxMap.getMap("DLQ_CNT").toString()) > 0 || Integer.parseInt(taxMap.getMap("GAST").toString()) > 0
								|| CbUtil.checkNull(taxMap.getMap("RVSN_YY").toString()) == null || CbUtil.checkNull(taxMap.getMap("RVSN_YY").toString()).equals("")){
							log.debug("DLQ_CNT 또는 GAST 가 0 보다 크면 오류 처리");
							cmMap.setMap("RESP_CODE" , "5010");     /*응답코드 5010 납부기한이 지난 고지분입니다.*/
							cmMap.setMap("TX_GUBUN"  , "160");
							retBuffer = mkBaroSunapErrJunmun(cmMap, buffer);
						}else{
							// 표준지방세 연계					
							retBuffer = sendJobTax004(cmMap, taxMap);
						}
												
					}else if(taxMap.getMap("NAPBU_GB").equals("2")){
						log.info("종합소득 납부전문 시작");
						//납부처리
						retBuffer = tax004SunapProcess(cmMap, taxMap, dataMap);
						
						log.info("send buffer :: " + retBuffer);
					}else{
						//납부구분 오류
						log.debug("납부구분 " + taxMap.getMap("NAPBU_GB").toString() +" 오류 입니다. " );
						cmMap.setMap("RESP_CODE" , "5510");     /*응답코드 5510 고지구분필드오류 입니다.*/
						cmMap.setMap("TX_GUBUN"  , "160");
						retBuffer = mkBaroSunapErrJunmun(cmMap, buffer);
					}
						
					
				}
				// 주민세 양소
				else if(dataMap.getMap("TAX_ITEM").equals("140002")){
						
					taxMap = DFL_005.parseRecvBuffer(buffer);  /*수신전문파싱...*/
						
					taxMap.setMap("JM_NO", cmMap.getMap("BCJ_NO").toString());
					taxMap.setMap("BANK_CD", cmMap.getMap("BANK_CODE").toString());
					taxMap.setMap("TX_DATETIME", cmMap.getMap("TX_DATETIME").toString());
					    
					//log.debug("전문파싱확인 taxMap :: "+taxMap);
						
						
					if(taxMap.getMap("NAPBU_GB").equals("1")){
						
						//납부지연일수, 가산금이 0보다 크면 오류 처리
						if(Integer.parseInt(taxMap.getMap("DLQ_CNT").toString()) > 0 || Integer.parseInt(taxMap.getMap("GAST").toString()) > 0
								|| CbUtil.checkNull(taxMap.getMap("RVSN_YY").toString()) == null || CbUtil.checkNull(taxMap.getMap("RVSN_YY").toString()).equals("")){
							log.debug("DLQ_CNT 또는 GAST 가 0 보다 크면 오류 처리");
							cmMap.setMap("RESP_CODE" , "1000");     /*응답코드 3010 총금액이 틀립니다.*/
							cmMap.setMap("TX_GUBUN"  , "160");
							retBuffer = mkBaroSunapErrJunmun(cmMap, buffer);
						}else{
							// 표준지방세 연계					
							retBuffer = sendJobTax005(cmMap, taxMap);
						}
						
						
					}else if(taxMap.getMap("NAPBU_GB").equals("2")){
						//납부처리
						log.info("양도소득 납부전문 시작");
						retBuffer = tax005SunapProcess(cmMap, taxMap, dataMap);
						log.info("send buffer :: " + retBuffer);
					}else{
						//납부구분 오류
						log.debug("납부구분 " + taxMap.getMap("NAPBU_GB").toString() +" 오류 입니다. " );
						cmMap.setMap("RESP_CODE" , "5510");     /*응답코드 5510 고지구분필드오류 입니다.*/
						cmMap.setMap("TX_GUBUN"  , "160");
						retBuffer = mkBaroSunapErrJunmun(cmMap, buffer);
					}		
						
	                    
				}
				//주민세 재산분
				else if(dataMap.getMap("TAX_ITEM").equals("104009")){
						
					taxMap = DFL_009.parseRecvBuffer(buffer);  /*수신전문파싱...*/
					
					taxMap.setMap("JM_NO", cmMap.getMap("BCJ_NO").toString());
					taxMap.setMap("BANK_CD", cmMap.getMap("BANK_CODE").toString());
					taxMap.setMap("TX_DATETIME", cmMap.getMap("TX_DATETIME").toString());
					    
					//log.debug("전문파싱확인 taxMap :: "+taxMap);
											
					if(taxMap.getMap("NAPBU_GB").equals("1")){
						
						//납부지연일수, 가산금이 0보다 크면 오류 처리
						if(Integer.parseInt(taxMap.getMap("DLQ_CNT").toString()) > 0 || Integer.parseInt(taxMap.getMap("NGAS").toString()) > 0 || Integer.parseInt(taxMap.getMap("GSEK").toString()) > 0
								|| CbUtil.checkNull(taxMap.getMap("RVSN_YY").toString()) == null || CbUtil.checkNull(taxMap.getMap("RVSN_YY").toString()).equals("")){
							log.debug("DLQ_CNT 또는 NGAS, GSEK 가 0 보다 크면 오류 처리");
							cmMap.setMap("RESP_CODE" , "1000");     /*응답코드 3010 총금액이 틀립니다.*/
							cmMap.setMap("TX_GUBUN"  , "160");
							retBuffer = mkBaroSunapErrJunmun(cmMap, buffer);
						}else{
							// 표준지방세 연계					
							retBuffer = sendJobTax009(cmMap, taxMap);
						}
						
						
					}else if(taxMap.getMap("NAPBU_GB").equals("2")){
						//납부처리
						log.info("주민세 재산분 납부전문 시작");
						retBuffer = tax009SunapProcess(cmMap, taxMap, dataMap);
						log.info("send buffer :: " + retBuffer);
					}else{
						//납부구분 오류
						log.debug("납부구분 " + taxMap.getMap("NAPBU_GB").toString() +" 오류 입니다. " );
						cmMap.setMap("RESP_CODE" , "5510");     /*응답코드 5510 고지구분필드오류 입니다.*/
						cmMap.setMap("TX_GUBUN"  , "160");
						retBuffer = mkBaroSunapErrJunmun(cmMap, buffer);
					}		
					
				}
				//주민세 종업원분
                else if(dataMap.getMap("TAX_ITEM").equals("140011")){
						
                	taxMap = DFL_011.parseRecvBuffer(buffer);  /*수신전문파싱...*/
					
					taxMap.setMap("JM_NO", cmMap.getMap("BCJ_NO").toString());
					taxMap.setMap("BANK_CD", cmMap.getMap("BANK_CODE").toString());
					taxMap.setMap("TX_DATETIME", cmMap.getMap("TX_DATETIME").toString());
					    
					//log.debug("전문파싱확인 taxMap :: "+taxMap);
						
					if(taxMap.getMap("NAPBU_GB").equals("1")){
						
						//납부지연 일수, 가산금이 0보다 크면 오류 처리
						if(Integer.parseInt(taxMap.getMap("DLQ_CNT").toString()) > 0 || Integer.parseInt(taxMap.getMap("NGAS").toString()) > 0 || Integer.parseInt(taxMap.getMap("GSEK").toString()) > 0
								|| CbUtil.checkNull(taxMap.getMap("RVSN_YY").toString()) == null || CbUtil.checkNull(taxMap.getMap("RVSN_YY").toString()).equals("")){
							log.debug("DLQ_CNT 또는 NGAS, GSEK 가 0 보다 크면 오류 처리");
							cmMap.setMap("RESP_CODE" , "1000");     /*응답코드 3010 총금액이 틀립니다.*/
							cmMap.setMap("TX_GUBUN"  , "160");
							retBuffer = mkBaroSunapErrJunmun(cmMap, buffer);
						}else{
							// 표준지방세 연계					
							retBuffer = sendJobTax011(cmMap, taxMap);
						}
												
					}else if(taxMap.getMap("NAPBU_GB").equals("2")){
						//납부처리
						log.info("주민세 종업원분 납부전문 시작");
						retBuffer = tax011SunapProcess(cmMap, taxMap, dataMap);
						log.info("send buffer :: " + retBuffer);
					}else{
						//납부구분 오류
						log.debug("납부구분 " + taxMap.getMap("NAPBU_GB").toString() +" 오류 입니다. " );
						cmMap.setMap("RESP_CODE" , "5510");     /*응답코드 5510 고지구분필드오류 입니다.*/
						cmMap.setMap("TX_GUBUN"  , "160");
						retBuffer = mkBaroSunapErrJunmun(cmMap, buffer);
					}		
					
				}else{
						
					// 회계코드 불일치
					log.error("Invalid format 전문수신(" + buffer.length + ") = [" + new String(buffer, 0, buffer.length < 260? buffer.length:260) + "]");
						
					cmMap.setMap("RESP_CODE" , "1010");     /*응답코드 1010 세목코드 오류.*/
					cmMap.setMap("TX_GUBUN"  , "160");
					    
					retBuffer = mkBaroSunapErrJunmun(cmMap, buffer);
						
				}						

			} 
			// 특별징수 전자신고 및 수납 처리
			else if(cmMap.getMap("TX_GUBUN").equals("250")){
				
				/*수신전문파싱...*/
				taxMap = DFL_008.parseRecvBuffer(buffer);  
				
				//전문관리번호
				taxMap.setMap("JM_NO", cmMap.getMap("BCJ_NO").toString());
				//은행(카드)코드, 조회시엔 "000"
				taxMap.setMap("BANK_CD", cmMap.getMap("BANK_CODE").toString());
				//전송일시 - "YYYYMMDDhhmmss"
				taxMap.setMap("TX_DATETIME", cmMap.getMap("TX_DATETIME").toString());
					
				//log.debug("전문파싱확인 taxMap :: "+taxMap);
				
				//특별징수
				if(taxMap.getMap("TAX_ITEM").equals("140004")){
					
					if(taxMap.getMap("NAPBU_GB").equals("1")){
						
						log.info("특별징수 신고 시작");
						//납부지연 일수가 0보다 크면 오류 처리.20141001 - 표준에 신고 할수 없다.
						//if(Integer.parseInt(taxMap.getMap("DLQ_CNT").toString()) > 0 || Integer.parseInt(taxMap.getMap("GAST").toString()) > 0
						//		|| CbUtil.checkNull(taxMap.getMap("RVSN_YY").toString()) == null || CbUtil.checkNull(taxMap.getMap("RVSN_YY").toString()).equals("")){
						//	log.debug("DLQ_CNT 또는 GAST 가 0 보다 크면 오류 처리");
						//	cmMap.setMap("RESP_CODE" , "5010");     /*응답코드 5010 납부기한이 지난 고지분입니다.*/
						//	cmMap.setMap("TX_GUBUN"  , "260");
						//	retBuffer = mkBaroSunapErrJunmun(cmMap, buffer);
						//}else{
							
						//}
						
						// 표준지방세 연계					
						retBuffer = sendJobTax008(cmMap, taxMap);
												
					}else if(taxMap.getMap("NAPBU_GB").equals("2")){
						log.info("특별징수 납부전문 시작");
						//납부처리
						retBuffer = tax008SunapProcess(cmMap, taxMap, dataMap);
						
						//log.info("send buffer :: " + retBuffer);
					}else{
						//납부구분 오류
						log.debug("납부구분 " + taxMap.getMap("NAPBU_GB").toString() +" 오류 입니다. " );
						cmMap.setMap("RESP_CODE" , "5510");     /*응답코드 5510 고지구분필드오류 입니다.*/
						cmMap.setMap("TX_GUBUN"  , "260");
						retBuffer = mkBaroSunapErrJunmun(cmMap, buffer);
					}
					
				}else{
					
					//세목코드 불일치
					log.debug("TAX_ITEM " + taxMap.getMap("TAX_ITEM").toString() +" 오류 입니다. " );
					cmMap.setMap("RESP_CODE" , "1010");     /*응답코드 1010 세목코드 오류.*/
					cmMap.setMap("TX_GUBUN"  , "260");
					retBuffer = mkBaroSunapErrJunmun(cmMap, buffer);					
				}
				
			}
			
			else {
				log.error("Invalid format 전문수신(" + buffer.length + ") = [" + new String(buffer, 0, buffer.length < 260? buffer.length:260) + "]");
				
				retBuffer = buffer;
			}
			
			
			/*트랜잭션 시작...*/
			//DB입력값이 있는 경우...
			//mainTransProcess();
			
			/*마지막으로 송수신전문을 저장한다...*/
			if(!cmMap.getMap("TX_GUBUN").equals("090")) {
				
				if(retBuffer.length >= 2000) {
					log.info("응답수신전문이 2000bytes 이상: 2000bytes 만 기록남김");
					logMap.setMap("RTN_MSG" , new String(retBuffer).substring(0, 2000)); /*응답전문*/
				} else {
					logMap.setMap("RTN_MSG" , new String(retBuffer)); /*응답전문*/
				}
				
				logMap.setMap("RES_CD"  , new String(retBuffer).substring(29, 33)); /*처리결과*/
				logMap.setMap("ERR_MSG" , cUtil.msgPrint("", "", "", new String(retBuffer).substring(29, 33))); /*오류메시지*/
				logMap.setMap("REG_DTM" , CbUtil.getCurrent("yyyyMMddHHmmss"));
				logMap.setMap("LAST_DTM", CbUtil.getCurrent("yyyyMMddHHmmss"));
				
				if(sqlService_cyber.queryForUpdate("CODMBASE.CODM_JUNMUN_TXRX_INSERT_CO5111", logMap) > 0) {
					log.info("송수신 전문 저장완료!!!");
				}
			}
				
		} catch (Exception e) {
			e.printStackTrace();
			
			cmMap = MSG_HEAD.parseHeadBuffer(chkJummun);

			log.debug("........전문이 이상한 경우 고대로 돌려준다.");
			/*오류가 발생한 경우...특히 전문이 이상한 경우 고대로 돌려준다.*/
			retBuffer = mkErrJunmun(cmMap, buffer);
		} finally {
			
			/*마지막에는 스레기 수거 수행 */
			//System.gc();
		}

	}
	
	/*에러전문을 만들어 셋팅해 준다.*/
    private byte[] mkBaroSunapErrJunmun(MapForm mpHead, byte[] rv_data){
    
    	try {
    		
    		byte[] b_head = GBN_LIST.makeSendBuffer(mpHead, dataMap);
    		
    		System.arraycopy(b_head, 0, rv_data, 0, 70);
    		
    		
    	}catch (Exception e) {
			e.printStackTrace();
		}

    	return rv_data;
    }
    
	
	/*에러전문을 만들어 셋팅해 준다.*/
    private byte[] mkErrJunmun(MapForm mpHead, byte[] rv_data){
    
    	try {
    		
    		mpHead.setMap("RESP_CODE" , "1000");     /*응답코드 : 전문에러*/

    		byte[] b_head = MSG_HEAD.makeSendBuffer(mpHead, dataMap);
    		
    		System.arraycopy(b_head, 0, rv_data, 0, 70);
    		
    		
    	}catch (Exception e) {
			e.printStackTrace();
		}

    	return rv_data;
    }
	
	/* Main Thread 
	 * 테스트 실행시만 사용합니다...
	 * */
	public static void main(String args[]) {

		ApplicationContext context  = null;
		
		try {

			Txdm2610 svr = new Txdm2610(0);
			
			/*Log4j 설정*/
			CbUtil.setupLog4jConfig(svr, "log4j.tomcat.xml");
			
			String[] xmls = XMLFileReader.getAllFilePathArray(Utils.getResourcePath(svr, "/"), "SERVICE.XML");

			String strSrc = Utils.getResourcePath(svr, "/") + "config/sqlmaps.xml";

			String strDest = Utils.getResourcePath(svr, "/") + "config/sqlmapConfig.xml";

			XmlUtils.setXmlAttributes(svr, strSrc, strDest, "sqlMapConfig", "sqlMap", "url", xmls);

			context = new ClassPathXmlApplicationContext("config/Tomcat-Spring-*.xml");
			
			svr.setAppContext(context);
			
			svr.sqlService_cyber = (IbatisBaseService) svr.getAppContext().getBean("baseService_cyber");
			
			svr.startServer();

		} catch (Exception e) {
			
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
		
		int connPort =  Utils.getResourceInt("ApplicationResource", "dftc.recv.port");  /*54001*/
		
		RcvServer server = new RcvServer(connPort, "com.uc.bs.cyber.daemon.txdm2610.Txdm2610", 30, 10, "log4j.xml", 4 ,com.uc.core.Constants.TYPEFIELDLEN);

		server.setContext(appContext);
		server.setProcId("2610");
		server.setProcName("편의점(더존)연계서버");
		server.setThreadName("thr_2610");
		
		server.initial();
		
		comdThread = new Thread(server);
		
		/**
		 * 데이터 송수신을 위해서 서버를 등록해준다
		 * (서버관리데몬)을 위해서 Threading...
		 */
		comdThread.setName("2610");
		
		comdThread.start();
		
	}
	
	
	/*----------------------------------------------*/
	/* 일련번호 가져오기 테스트*/
	/*----------------------------------------------*/
	private int SeqNumber(){
		
		int retVal = 0;
		
		try {
			retVal = (Integer)sqlService_cyber.queryForBean("CODMBASE.CODM_GETSEQNO", "00");
		} catch (Exception e) {
			e.printStackTrace();
		}
    	
		return retVal;
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
		
			e.printStackTrace();
		}			
		
	}
	

	/*트랜잭션 처리*/
	@Override
	public void transactionJob() {

		try {
			
			log.info("[=========== 여긴 transactionJob() :: 트랜잭션처리영역 입니다. ========== ]");
			
		} catch (Exception e) {
		
			e.printStackTrace();
		}
	
	}

	@Override
	public void setContext(Object obj) {
		
		this.appContext = (ApplicationContext) obj;
		sqlService_cyber = (IbatisBaseService) appContext.getBean("baseService_cyber");

	}
	
	
	// taxMap 초기화
	private void taxMapFormInit(MapForm cmMap) {
				
		taxMap = new MapForm();
				
		// 추가
	    taxMap.setMap("RTN_ZIP_CD", "");  // 양도물건주소 우편번호 - 양도소득
	    taxMap.setMap("RTN_ADDR",   "");  // 양도물건주소 상세     - 양도소득
		taxMap.setMap("JSDT",       "");  // 귀속사업기간 시작     - 법인세
		taxMap.setMap("JEDT",       "");  // 귀속사업기간 종료     - 법인세
		taxMap.setMap("TJUS",        0);  // 종업원(전체)          - 법인세
		taxMap.setMap("JUWS",        0);  // 종업원(시구군)        - 법인세
		taxMap.setMap("TCNT",        0);  // 건축물(전체)          - 법인세
		taxMap.setMap("SASU",        0);  // 건축물(시구군)        - 법인세
	    taxMap.setMap("RADTX",       0);  // 신고불성실가산세      - 법인세
		taxMap.setMap("NGAS",        0);  // 납부불성실가산세      - 법인세
		taxMap.setMap("JYMD",       "");  // 급여지급일            - 특별징수
		taxMap.setMap("RVSN_YY",    "");  // 귀속년도              - 종합소득
		taxMap.setMap("RVSN_MM",    "");  // 귀속월                - 특별징수
		taxMap.setMap("WANT",        0);  // 계(인원)              - 특별징수
		taxMap.setMap("GWAT",        0);  // 계(과세표준)          - 특별징수
	    taxMap.setMap("TAXT",        0);  // 계(주민세)            - 특별징수
		taxMap.setMap("HAMT",        0);  // 환불총액              - 특별징수
		taxMap.setMap("DLQ_CNT",     0);  // 납부지연일수          - 종,양,법
		taxMap.setMap("F_DUE_DT",   "");  // 당초 납부기한         - 종,양,법
		taxMap.setMap("BCJ_NO", cmMap.getMap("BCJ_NO").toString());
	    taxMap.setMap("BANK_CODE", cmMap.getMap("BANK_CODE").toString());
		taxMap.setMap("TX_DATETIME", cmMap.getMap("TX_DATETIME").toString());
			    
		//종업원 추가
		taxMap.setMap("SUP_DT",     "");  // 급여지급일          - 종업원
			    
		log.debug("BCJ_NO" + cmMap.getMap("BCJ_NO").toString());
		/*
		headField.add("LEN"              , 4  ,  "H"); //전문길이
	    headField.add("TX_ID"            , 6  ,  "C"); //업무구분 - SN2601:부산시
		headField.add("TX_DATETIME"      , 14 ,  "C"); //전송일시 - "YYYYMMDDhhmmss"
		headField.add("TX_GUBUN"         , 3  ,  "C"); //전문종별구분코드 "030:상세조회요청, 040:상세조회요청 응답, 050:납부처리요청, 060:납부처리요청 응답
		headField.add("RS_FLAG"          , 2  ,  "C"); //접속경로구분코드 - 더존(DZ)
		headField.add("RESP_CODE"        , 4  ,  "C"); //결과코드 - 조회시엔 "0000"
		headField.add("SIDO_CODE"        , 2  ,  "C"); //시도구분코드 - 26:부산시
		headField.add("BANK_CODE"        , 3  ,  "C"); //은행(카드)코드, 조회시엔 "000"
		headField.add("BCJ_NO"           , 12 ,  "C"); //전문관리번호(일자별 전문관리번호)
		headField.add("MEDIA_CODE"       , 3  ,  "C"); //매체구분코드 - 270: 편의점수납
		headField.add("RESERVE1"         , 17 ,  "C"); //예비영역
		*/	
	}
			
	// 지방소득세 종합소득분 전자신고 표준지방세 연계  cmMap, dataMap, taxMap
	private byte[] sendJobTax004(MapForm cmMap, MapForm taxMap) {
			
		/******************************************************
		* 표준 사업단 자료 연계 처리  START
		* *****************************************************/
	    MapForm   sndForm  = new  MapForm();
	    	
	    try{
	    	sndForm.setMap("tax_gubun"	   ,   "140001"); //택스구분
	        sndForm.setMap("SIDO_COD"      ,   "26"); // (2)  시도코드   -   지방자치단체 필수구분값으로 사용
	        sndForm.setMap("SGG_COD"       ,   CbUtil.checkNull(taxMap.getMap("SGG_COD").toString())); // (3)  시군구코드   -   지방자치단체 필수구분값으로 사용
	        sndForm.setMap("TAX_ITEM"      ,   "140001"); // (6)  세목코드
	        sndForm.setMap("TAX_YYMM"      ,   taxMap.getMap("TAX_YY").toString()+taxMap.getMap("TAX_MM").toString()); // (6)  과세년월
	        sndForm.setMap("TAX_DIV"       ,   "3"); // (1)  과세구분   -   3:신고분
	           
	        //log.debug("=========행정동=============="+taxMap.getMap("HACD").toString());
	            
	        String bucd = getLdongCode(taxMap.getMap("SGG_COD").toString(), taxMap.getMap("HACD").toString());
	        //log.debug("=========법정동=============="+bucd);
	        	
	        //String address = getAddress(taxMap.getMap("ZIP_CD").toString());	
	        //taxMap.setMap("ADDRESS", address);
	        //String address = "busansi donggu test";
	        //log.debug("=========address=============="+address);
	        
	        sndForm.setMap("ADONG_COD"     ,   CbUtil.checkNull(taxMap.getMap("HACD").toString())); // (3)  과세행정동    
	        String TAX_DT = taxMap.getMap("TAX_YY").toString()+taxMap.getMap("TAX_MM").toString()+taxMap.getMap("TAX_DD").toString();   
	        sndForm.setMap("TAX_DT"        ,   TAX_DT); // (8)  신고일자
	        sndForm.setMap("REG_NO"        ,   CbUtil.checkNull(taxMap.getMap("REG_NO").toString())); // (13) 납세자 주민/법인번호    
	        sndForm.setMap("REG_NM"        ,   CbUtil.checkNull(taxMap.getMap("DPNM").toString())); // (80) 납세자 성명
	        sndForm.setMap("TPR_COD"       ,   (taxMap.getMap("DP_GBN").equals("0"))?"00":"20"); // (2)  법인코드   -   행자부 표준코드- 납세의무자(법인.개인)코드
	        sndForm.setMap("LDONG_COD"     ,   CbUtil.checkNull(bucd) + "00"); // (5)  사업장 법정동
	        sndForm.setMap("BIZ_ZIP_NO"    ,   CbUtil.checkNull(taxMap.getMap("ZIP_CD").toString())); // (6)  사업장 우편번호
	        sndForm.setMap("BIZ_ZIP_ADDR"  ,   CbUtil.checkNull(taxMap.getMap("ADDR").toString()));    // (60) 사업장 우편주소
	        String  addrdt = " ";
	        if(taxMap.getMap("ADDR_DT").toString()==null || taxMap.getMap("ADDR_DT").toString().equals("")){
	            addrdt = " ";
	        }else{
	            addrdt = taxMap.getMap("ADDR_DT").toString();
	        }
	        sndForm.setMap("BIZ_ADDR"      ,   addrdt);    // (100)사업장 상세주소
	        sndForm.setMap("MO_TEL"        ,   CbUtil.checkNull(taxMap.getMap("PHONE_NO").toString()));    // (16) 핸드폰번호
	        sndForm.setMap("BIZ_TEL"       ,   CbUtil.checkNull(taxMap.getMap("TEL_NO").toString()));    // (16) 사업장 전화번호
	       
	        //사업자번호
	    	sndForm.setMap("BIZ_NO"        ,   CbUtil.checkNull(taxMap.getMap("SAUP_NO").toString()));  // N(10) 사업자번호	 
	        sndForm.setMap("CMP_NM"        ,   CbUtil.checkNull(taxMap.getMap("CMP_NM").toString()));    // (100)상호명
	        sndForm.setMap("RVSN_YY"       ,   CbUtil.checkNull(taxMap.getMap("RVSN_YY").toString()));    // (4)  귀속년도
	        sndForm.setMap("C_REQ_DT"      ,   "");    // (8)  수정신고일자   -   신고 구분이 "수정신고" 일 경우만 값이 존재함
	        sndForm.setMap("DUE_DT"        ,   taxMap.getMap("DUE_DT").toString());    // (8)  납기일자
	        sndForm.setMap("REQ_DIV"       ,   "1");    // (1)  신고구분   -    1:확정신고,2:수정신고
	                    
	        sndForm.setMap("INCTX"         ,   CbUtil.checkNull(taxMap.getMap("KJSA").toString()));    // (14) 종합소득세
	        sndForm.setMap("RSTX_INC"      ,   CbUtil.checkNull(taxMap.getMap("JMSA").toString()));    // (14) 지방소득세 종합소득세분
	        sndForm.setMap("RADTX"         ,   "0");    // (14) 신고불성실 가산세
	        sndForm.setMap("PADTX"         ,   "0");    // (14) 납부불성실 가산세
	        sndForm.setMap("TAX_RT"        ,   "10");    // (4)  세율   -   천분율
	        sndForm.setMap("ADTX_YN"       ,   "2");    // (1)  가산세유무   -    1:유 2:무
	        sndForm.setMap("DLQ_CNT"       ,   "0");    // (14) 납부지연일수
	        sndForm.setMap("TOT_ADTX"      ,   "0");    // (14) 가산세 합계
	        sndForm.setMap("TOT_AMT"       ,   CbUtil.checkNull(taxMap.getMap("SUM_RCP").toString()));    // (14) 총납부세액   -   지방소득세 종합소득세분 + 가산세 합계
	        	
	        sndForm.setMap("F_DUE_DT"      ,   CbUtil.checkNull(taxMap.getMap("DUE_DT").toString()));   // (8)  당초납기
	        sndForm.setMap("RPT_REG_NO"    ,   taxMap.getMap("REG_NO").toString());    // (13) 신고자 주민/법인번호
	        sndForm.setMap("RPT_NM"        ,   taxMap.getMap("DPNM").toString());    // (80) 신고자 성명/법인명
	        sndForm.setMap("RPT_TEL"       ,   taxMap.getMap("PHONE_NO").toString());    // (16) 신고자 전화번호   -   핸드폰 또는 전화번호
	        sndForm.setMap("RPT_ID"        ,   "ETAX9"  																														);    // (20) 신고자ID
	        sndForm.setMap("RPT_ADMIN"     ,   taxMap.getMap("DPNM").toString());    // (60) 신고자 기관/부서명
	        sndForm.setMap("RPT_SYSTEM"    ,   "ETAX");    // (5)  신고 시스템 구분   -   WETAX : WETAX, ETAX: ETAX
	           
	        sndForm.setMap("SINGO_DIV"     ,   "1");    // (1)  신고구분   -   최초신고:1 , 수정신고:2
	        sndForm.setMap("B_TAX_NO"      ,   "");    // (31) 최초신고분 과세번호   -   수정신고시 필수
	        sndForm.setMap("B_EPAY_NO"     ,   "");    // (19) 최초신고분 전자납부번호   -   수정신고시 필수
	        sndForm.setMap("CHG_MEMO"      ,   "");    // (2)  수정신고내용   -   수정신고시필수
	    								//                      - 과소신고 : 11
	    								//                      - 환급세액 초과 : 12
	    							    //                      - 정산누락에 따른 과소신고 : 13
	    								//                      - 기타 : 91"
	        sndForm.setMap("CHG_REASON"    ,   "");    // (2)  수정신고사유   -   수정신고시필수
	    							    //                      - 과세표준의 과소신고 : 21
	    								//                      - 산출세액의 과소신고 : 22
	    							    //                      - 환급세액 초과 : 23
	    								//                      - 특별징수의무자의 정산과정에서 누락 :24
	    								//                      - 기타 : 92"
	        sndForm.setMap("REASON_DT"     ,   "");    // (8)  사유발생일   -   수정신고시필수
	        sndForm.setMap("ETC_MEMO"      ,   "");    // (200)비고   -   수정신고시 사용
	        sndForm.setMap("SUNAP_DT"      ,   "");    // (8)  수납일자   -   수정신고시 사용
	        sndForm.setMap("EVI_DOC1"      ,   "" );    // (100)서류명
	        sndForm.setMap("EVI_DOC_URL1"  ,   "" );    // (100)서류조회URL
	        sndForm.setMap("EVI_DOC2"      ,   "" );    // (100)서류명2
	        sndForm.setMap("EVI_DOC_URL2"  ,   "" );    // (100)서류조회URL2
	        sndForm.setMap("EVI_DOC3"      ,   "" );    // (100)서류명2
	        sndForm.setMap("EVI_DOC_URL3"  ,   "" );    // (100)서류조회URL2
	    	    
	    	//log.debug("sndForm :: " + sndForm);
	    	    
	    	    
	    	try {
	    	    	
	    	    // 지방소득세 종합소득분 신고 송수신 서비스 호출
	    	    Tax004Service  soapservice = new  Tax004Service();
	    	    	
	    	    MapForm recvForm = null;
	    	    	
	    	    try{
	    	    	recvForm = soapservice.sndService(sndForm);
	    	    }catch(Exception e){
	    	    	e.printStackTrace();
	    	    	log.debug(" soapservice 지방소득세 종합소득분 전자신고 에러 ");	   
	    	    	//RE_CO = "1000";
	    	    	cmMap.setMap("RESP_CODE" , "1000");     /*응답코드*/
		    		cmMap.setMap("TX_GUBUN"  , "160");
		    		return DFL_005.makeSendBuffer(cmMap, taxMap);
	    	    }
	    	    	
	    	    //log.debug("****recvForm*********"+recvForm+"******************* ");
	    	    taxMap.putAll(recvForm);
	    	    	
	    	    //log.debug("***************************************************"+taxMap.getMap("MESSAGE").toString()+"*값이 SVR01이면 등록함 *******************************************");
	    	    if(taxMap.getMap("MESSAGE").equals("SVR01")){
	    	    		
	    	    	log.debug("************ 종합소득 신고 등록하기 *********");
	    	    		 
	    	    	if(taxMap.getMap("EPAY_NO").equals("") || taxMap.getMap("EPAY_NO").toString() == null){
	    	    		cmMap.setMap("RESP_CODE" , "3010");     /*응답코드 */
			    		cmMap.setMap("TX_GUBUN"  , "160");
			    		return DFL_005.makeSendBuffer(cmMap, taxMap);
	    	    	}
	    	    	taxMap.setMap("HWAN", 0);
	    	    	taxMap.setMap("GGYM", taxMap.getMap("TAX_YY").toString()+taxMap.getMap("TAX_MM").toString()+taxMap.getMap("TAX_DD").toString());
	    	    	taxMap.setMap("REQ_SNMH", taxMap.getMap("DPNM").toString());	    	
	    	    		
	    	    	taxMap.setMap("BUCD", bucd);
	    	    	taxMap.setMap("TAX_RATE", "10%");
	    	    	taxMap.setMap("SGGB", "1");
	    	    	taxMap.setMap("REQ_DIV", "1");
	    	    		 		
	    	    	//log.debug("**** taxMap ****" + taxMap + "******************* ");
	    	    		
	    	    	//try{
	    	    	//	sqlService_cyber.queryForInsert("TXDM2610.insertEs1121Tax004", taxMap);//인서트
	    	    	//}catch(Exception e){
	    	    	//	e.printStackTrace();
	    	    	//	log.debug("등록 에러 : " + taxMap);   	    			
	    	    	//	cmMap.setMap("RESP_CODE" , "8000");     /*응답코드*/
	        		//   cmMap.setMap("TX_GUBUN"  , "160");
	        		//    return DFL_004.makeSendBuffer(cmMap, taxMap);    	    			
	    	    	//}	    		
	    	    	
	    	    }else{	    		
	    	    	log.debug("************ 지방소득세 종합소득분 웹서비스 실패 *********");
	    	    	log.debug("************ MESSAGE ****** : " + taxMap.getMap("MESSAGE"));
	    	    		
	    	    	cmMap.setMap("RESP_CODE" , "9000");     /*응답코드*/
	    			cmMap.setMap("TX_GUBUN"  , "160");
	    			return DFL_004.makeSendBuffer(cmMap, taxMap);
	    	    }
	    	    		    	
	    	    	
	    	} catch(Exception e) {
	    	    log.debug("지방소득세 종합소득분 전자신고 전문 송수신오류 ");
	    	    cmMap.setMap("RESP_CODE" , "9000");     /*응답코드*/
				cmMap.setMap("TX_GUBUN"  , "160");
				return DFL_004.makeSendBuffer(cmMap, taxMap);  
	    	} finally {
	    	    
	        }
	    
	    	/******************************************************
	        * 표준 사업단 자료 연계 처리  END
	        * *****************************************************/
	    		
	        // 받아온 전문 저장
	    	taxMap.setMap("STATE", state);
	    	taxMap.setMap("ERR_MSG", err_msg);
	        
	    	try{
	    	    //전문 저장
	    	    sqlService_cyber.queryForInsert("TXDM2610.insertBaroPayData", taxMap);
	    	}catch(Exception e){
	    	    e.printStackTrace();
	    	    log.debug("연계테이블 등록 오류");   	    	
	    	    cmMap.setMap("RESP_CODE" , "8000");     /*응답코드*/
				cmMap.setMap("TX_GUBUN"  , "160");
				return DFL_004.makeSendBuffer(cmMap, taxMap);  
	    	}
	    	    
	    	//TAX_SNO,EPAY_NO,TAX_NO 최종 확인
	    	if(taxMap.getMap("EPAY_NO") == "" || taxMap.getMap("EPAY_NO") == null){
	    	    //RE_CO = "1000";
	    	    cmMap.setMap("RESP_CODE" , "1000");     /*응답코드*/
				cmMap.setMap("TX_GUBUN"  , "160");
				return DFL_004.makeSendBuffer(cmMap, taxMap);
	    	}
			
	    	
	    }catch(Exception e){
			e.printStackTrace();
			//RE_CO = "1000";
			log.debug("전문 세팅 오류 ");   	    	
    	    cmMap.setMap("RESP_CODE" , "1000");     /*응답코드*/
			cmMap.setMap("TX_GUBUN"  , "160");
			return DFL_004.makeSendBuffer(cmMap, taxMap);  
		}
	    		    
		// 리턴 값 정리
		cmMap.setMap("RESP_CODE" , "0000");     /*응답코드*/
		cmMap.setMap("TX_GUBUN"  , "160");
		return DFL_004.makeSendBuffer(cmMap, taxMap);		
	}
	    
	    

	// 주민세 양소 전자신고 표준지방세 연계
	private byte[] sendJobTax005(MapForm cmMap, MapForm taxMap) {
			
		/******************************************************
		* 표준 사업단 자료 연계 처리  START
		* *****************************************************/
		MapForm   sndForm  = new  MapForm();
			
		try{
			sndForm.setMap("tax_gubun"	  ,   "140002");	  //택스구분
			sndForm.setMap("SIDO_COD"     ,   "26");    // 시도코드
			sndForm.setMap("SGG_COD"      ,   CbUtil.checkNull(taxMap.getMap("SGG_COD").toString()));    // 시군구코드
			sndForm.setMap("TAX_ITEM"     ,   "140002");    // 세목코드
			sndForm.setMap("TAX_YYMM"     ,   CbUtil.checkNull(taxMap.getMap("TAX_YY").toString()+taxMap.getMap("TAX_MM").toString()));    // 과세년월
			sndForm.setMap("TAX_DIV"      ,   "3");    // 과세구분
		    	
		    String bucd = getLdongCode(taxMap.getMap("SGG_COD").toString(), taxMap.getMap("HACD").toString());   	
		
		    //String address = getAddress(taxMap.getMap("ZIP_CD").toString());	    	
		    //taxMap.setMap("ADDRESS", address);
		    	
			sndForm.setMap("ADONG_COD"    ,   CbUtil.checkNull(taxMap.getMap("HACD").toString()));    // 과세행정동
			      
			sndForm.setMap("TAX_DT"       ,   taxMap.getMap("TAX_YY").toString()+taxMap.getMap("TAX_MM").toString()+taxMap.getMap("TAX_DD").toString());    // 신고일자
			sndForm.setMap("REG_NO"       ,   CbUtil.checkNull(taxMap.getMap("REG_NO").toString()));  // 납세자 주민/법인번호
			sndForm.setMap("REG_NM"       ,   CbUtil.checkNull(taxMap.getMap("DPNM").toString()));    // 납세자 성명
			sndForm.setMap("TPR_COD"      ,   (taxMap.getMap("DP_GBN").equals("0"))?"00":"20");    // 법인코드
			sndForm.setMap("LDONG_COD"    ,   CbUtil.checkNull(bucd) + "00");    // 납세자 법정동
			sndForm.setMap("ZIP_NO"       ,   CbUtil.checkNull(taxMap.getMap("ZIP_CD").toString()));    // 납세자 우편번호
			sndForm.setMap("ZIP_ADDR"     ,   CbUtil.checkNull(taxMap.getMap("ADDR").toString()));    // 납세자 우편주소
			sndForm.setMap("OTH_ADDR"     ,   " " + CbUtil.checkNull(taxMap.getMap("ADDR_DT").toString()));    // 납세자 상세주소
			sndForm.setMap("MO_TEL"       ,   CbUtil.checkNull(taxMap.getMap("PHONE_NO").toString()));    // 납세자 핸드폰번호
			sndForm.setMap("TEL"          ,   CbUtil.checkNull(taxMap.getMap("TEL_NO").toString()));    // 납세자 전화번호
			sndForm.setMap("BIZ_NO"       ,   CbUtil.checkNull(taxMap.getMap("SAUP_NO").toString()));    // 납세자 사업자번호
			sndForm.setMap("CMP_NM"       ,   CbUtil.checkNull(taxMap.getMap("DPNM").toString()));    // 상호명
			sndForm.setMap("RVSN_YY"      ,   CbUtil.checkNull(taxMap.getMap("RVSN_YY").toString()));    // 귀속년도
			sndForm.setMap("DUE_DT"       ,   CbUtil.checkNull(taxMap.getMap("DUE_DT").toString()));    // 납기일자
			sndForm.setMap("REQ_DIV"      ,   "11");    // 신고구분 11 확정신고 13 예정신고 14 수정신고
			sndForm.setMap("RTN_INC_DT"   ,   taxMap.getMap("TAX_YY").toString()+taxMap.getMap("TAX_MM").toString()+taxMap.getMap("TAX_DD").toString());    // 양도소득일자
			sndForm.setMap("RTN_ZIP_NO"   ,   CbUtil.checkNull(taxMap.getMap("RTN_ZIP_CD").toString()));    // 양도물건지 우편번호
			sndForm.setMap("RTN_ADDR"     ,   CbUtil.checkNull(taxMap.getMap("RTN_ADDR").toString()));    // 양도물건지 주소
			sndForm.setMap("RTNTX"        ,   CbUtil.checkNull(taxMap.getMap("KJSA").toString()));    // 양도소득세
			sndForm.setMap("RSTX_RTN"     ,   CbUtil.checkNull(taxMap.getMap("JMSA").toString()));    // 지방소득세 양도소득세분
			sndForm.setMap("RADTX"        ,   "0");    // 신고불성실 가산세
			sndForm.setMap("PADTX"        ,   "0");    // 납부불성실 가산세
			sndForm.setMap("TAX_RT"       ,   "10");    // 세율
			sndForm.setMap("ADTX_YN"      ,   "2");    // 가산세유무
			sndForm.setMap("DLQ_CNT"      ,   "0");    // 납부지연일수
			sndForm.setMap("TOT_ADTX"     ,   "0");    // 가산세 합계
			sndForm.setMap("TOT_AMT"      ,   CbUtil.checkNull(taxMap.getMap("SUM_RCP").toString()));    // 총납부세액
			sndForm.setMap("F_DUE_DT"     ,   CbUtil.checkNull(taxMap.getMap("DUE_DT").toString()));    // 당초납기
			sndForm.setMap("RPT_REG_NO"   ,   taxMap.getMap("REG_NO").toString());    // 신고자 주민/법인번호
			sndForm.setMap("RPT_NM"       ,   CbUtil.checkNull(taxMap.getMap("DPNM").toString()));    // 신고자 성명/법인명
			sndForm.setMap("RPT_TEL"      ,   CbUtil.checkNull(taxMap.getMap("PHONE_NO").toString()));    // 신고자 전화번호
			sndForm.setMap("RPT_ID"       ,   "ETAX9");    // 신고자ID
			sndForm.setMap("RPT_ADMIN"    ,   CbUtil.checkNull(taxMap.getMap("DPNM").toString()));    // 신고자 기관/부서명
			sndForm.setMap("RPT_SYSTEM"   ,   "ETAX");    // 신고 시스템 구분
			sndForm.setMap("SINGO_DIV"    ,   "");    // 신고구분
			sndForm.setMap("B_TAX_NO"     ,   "");    // 최초신고분 과세번호
			sndForm.setMap("B_EPAY_NO"    ,   "");    // 최초신고분 전자납부번호
			sndForm.setMap("CHG_MEMO"     ,   "");    // 수정신고내용
			sndForm.setMap("CHG_REASON"   ,   "");    // 수정신고사유
			sndForm.setMap("REASON_DT"    ,   "");    // 사유발생일
			sndForm.setMap("ETC_MEMO"     ,   "");    // 비고
			sndForm.setMap("SUNAP_DT"     ,   "");    // 수납일자
			sndForm.setMap("EVI_DOC1"     ,   "");    // 서류명
			sndForm.setMap("EVI_DOC_URL1" ,   "");    // 서류조회URL
			sndForm.setMap("EVI_DOC2"     ,   "");    // 서류명2
			sndForm.setMap("EVI_DOC_URL2" ,   "");    // 서류조회URL2
			sndForm.setMap("EVI_DOC3"     ,   "");    // 서류명2
			sndForm.setMap("EVI_DOC_URL3" ,   "");    // 서류조회URL2
			    
			//log.debug(sndForm);
			    
			//신고구분
			if(sndForm.getMap("REQ_DIV").equals("13")){	//예정신고
				taxMap.setMap("SGGB", "1");
			}
			if(sndForm.getMap("REQ_DIV").equals("11")){	//확정신고
				taxMap.setMap("SGGB", "2");
			}
				
			try {
			    // 지방세 양도소득분 신고 송수신 서비스 호출
			    Tax005Service  soapservice = new  Tax005Service();
			    	
			    //log.debug("****sndForm*********"+sndForm+"******************* ");
			    	
			    MapForm recvForm = null;
			    	
			    try{
			    	recvForm = soapservice.sndService(sndForm);
			    }catch(Exception e){
			    	e.printStackTrace();
			    	//RE_CO = "1000";
			    	cmMap.setMap("RESP_CODE" , "1000");     /*응답코드*/
		    		cmMap.setMap("TX_GUBUN"  , "160");
		    		return DFL_005.makeSendBuffer(cmMap, taxMap);	
			    }
			    	
			    //log.debug("****recvForm*********"+recvForm+"******************* ");
			    	
			    taxMap.putAll(recvForm);
			    	
			    if(taxMap.getMap("MESSAGE").equals("SVR01")){		
			    		
			    	if(taxMap.getMap("EPAY_NO").equals("") || taxMap.getMap("EPAY_NO").toString() == null){
	    	    		//납부금액이 소액일 경우 전자납부번호가 빈값으로 넙어온다.--에러처리
			    		cmMap.setMap("RESP_CODE" , "3010");     /*응답코드 */
			    		cmMap.setMap("TX_GUBUN"  , "160");
			    		return DFL_005.makeSendBuffer(cmMap, taxMap);
	    	    	}
			    	taxMap.setMap("BUCD", bucd);
			    	taxMap.setMap("YYMD", sndForm.getMap("TAX_DT").toString());
			    	taxMap.setMap("REQ_SNMH", taxMap.getMap("DPNM").toString());
			    	taxMap.setMap("REQ_NM", taxMap.getMap("DPNM").toString());
			    	taxMap.setMap("F_DUE_DT", taxMap.getMap("DUE_DT").toString());
			    			
			    	log.debug("************ 양도소득분 신고 등록하기 *********");
			    	//log.debug("**** taxMap *" + taxMap + "******************* ");
			    		

			    }else{

			    	log.debug("************ 웹서비스 에러 *********");
			    	cmMap.setMap("RESP_CODE" , "9000");     /*응답코드*/
		    		cmMap.setMap("TX_GUBUN"  , "160");
		    		return DFL_005.makeSendBuffer(cmMap, taxMap);
			    }    
			    	
			} catch(Exception e) {
			    	
			    log.debug("양도소득분 신고 전문 송수신오류 ");
			    cmMap.setMap("RESP_CODE" , "9000");     /*응답코드*/
	    		cmMap.setMap("TX_GUBUN"  , "160");
	    		return DFL_005.makeSendBuffer(cmMap, taxMap);
			} finally {
			    	//clear();
			}
				
		    /******************************************************
		    * 표준 사업단 자료 연계 처리  END
			* *****************************************************/
			    
			// 받아온 전문 저장
			taxMap.setMap("STATE", state);
			    		        
			try{
			    sqlService_cyber.queryForInsert("TXDM2610.insertBaroPayData", taxMap);
			}catch(Exception e){
			    e.printStackTrace();
			    log.debug("연계테이블 등록 오류");
			    cmMap.setMap("RESP_CODE" , "8000");     /*응답코드*/
	    		cmMap.setMap("TX_GUBUN"  , "160");
	    		return DFL_005.makeSendBuffer(cmMap, taxMap);
			}
			    
		}catch(Exception e){
				
			e.printStackTrace();
			cmMap.setMap("RESP_CODE" , "1000");     /*응답코드*/
			cmMap.setMap("TX_GUBUN"  , "160");
			return DFL_005.makeSendBuffer(cmMap, taxMap);
		}
		    
		// 리턴 값 정리
		cmMap.setMap("RESP_CODE" , "0000");     /*응답코드*/
		cmMap.setMap("TX_GUBUN"  , "160");
		return DFL_005.makeSendBuffer(cmMap, taxMap);
	}

	
	// 주민세 특별징수 전자신고 표준지방세 연계
	private byte[] sendJobTax008(MapForm cmMap, MapForm taxMap) {

		
		log.debug("******************************************************");
		log.debug("* 표준 사업단 특별징수 신고 연계 처리  START");
		log.debug("******************************************************");
		//log.debug("taxMap :: " + taxMap);
		
		/*데이터 검증*/
		//총 인원
		
		String ADONG_COD = "";
		String BUCD = "";
		long tot_emp = 0L;
		long tot_std = 0L;
		long tot_tax = 0L;
		
		String INTX = "";
		String TOT_ADTX = "";
		
		try{
		
			//log.debug("총 인원  체크 시작");
			tot_emp = Long.parseLong(CbUtil.checkNullZero(taxMap.getMap("TXTP_EMP11").toString())) + Long.parseLong(CbUtil.checkNullZero(taxMap.getMap("TXTP_EMP12").toString()))
		             + Long.parseLong(CbUtil.checkNullZero(taxMap.getMap("TXTP_EMP13").toString())) + Long.parseLong(CbUtil.checkNullZero(taxMap.getMap("TXTP_EMP14").toString()))
		             + Long.parseLong(CbUtil.checkNullZero(taxMap.getMap("TXTP_EMP16").toString())) + Long.parseLong(CbUtil.checkNullZero(taxMap.getMap("TXTP_EMP17").toString()))
		             + Long.parseLong(CbUtil.checkNullZero(taxMap.getMap("TXTP_EMP21").toString())) + Long.parseLong(CbUtil.checkNullZero(taxMap.getMap("TXTP_EMP22").toString()))
		             + Long.parseLong(CbUtil.checkNullZero(taxMap.getMap("TXTP_EMP31").toString())) + Long.parseLong(CbUtil.checkNullZero(taxMap.getMap("TXTP_EMP32").toString())) + Long.parseLong(CbUtil.checkNullZero(taxMap.getMap("TXTP_EMP34").toString()))
		             + Long.parseLong(CbUtil.checkNullZero(taxMap.getMap("TXTP_EMP33").toString())) + Long.parseLong(CbUtil.checkNullZero(taxMap.getMap("TXTP_EMP91").toString()));
		
			//log.debug("총 인원  체크 :: "  + tot_emp);
			//총 과세표준
			//log.debug("총 과세표준  체크 시작");
			tot_std = Long.parseLong(CbUtil.checkNullZero(taxMap.getMap("TXTP_STD11").toString())) + Long.parseLong(CbUtil.checkNullZero(taxMap.getMap("TXTP_STD12").toString()))
	                 + Long.parseLong(CbUtil.checkNullZero(taxMap.getMap("TXTP_STD13").toString())) + Long.parseLong(CbUtil.checkNullZero(taxMap.getMap("TXTP_STD14").toString()))
	                 + Long.parseLong(CbUtil.checkNullZero(taxMap.getMap("TXTP_STD16").toString())) + Long.parseLong(CbUtil.checkNullZero(taxMap.getMap("TXTP_STD17").toString()))
	                 + Long.parseLong(CbUtil.checkNullZero(taxMap.getMap("TXTP_STD21").toString())) + Long.parseLong(CbUtil.checkNullZero(taxMap.getMap("TXTP_STD22").toString()))
	                 + Long.parseLong(CbUtil.checkNullZero(taxMap.getMap("TXTP_STD31").toString())) + Long.parseLong(CbUtil.checkNullZero(taxMap.getMap("TXTP_STD32").toString())) + Long.parseLong(CbUtil.checkNullZero(taxMap.getMap("TXTP_STD34").toString()))
	                 + Long.parseLong(CbUtil.checkNullZero(taxMap.getMap("TXTP_STD33").toString())) + Long.parseLong(CbUtil.checkNullZero(taxMap.getMap("TXTP_STD91").toString()));
			//log.debug("총 과세표준  체크 ::  " + tot_std);
		
			//총 지방소득세             
			//log.debug("총 지방소득세  체크 시작");
			tot_tax = Long.parseLong(CbUtil.checkNullZero(taxMap.getMap("TXTP_INTX11").toString())) + Long.parseLong(CbUtil.checkNullZero(taxMap.getMap("TXTP_INTX12").toString()))
                     + Long.parseLong(CbUtil.checkNullZero(taxMap.getMap("TXTP_INTX13").toString())) + Long.parseLong(CbUtil.checkNullZero(taxMap.getMap("TXTP_INTX14").toString()))
                     + Long.parseLong(CbUtil.checkNullZero(taxMap.getMap("TXTP_INTX16").toString())) + Long.parseLong(CbUtil.checkNullZero(taxMap.getMap("TXTP_INTX17").toString()))
                     + Long.parseLong(CbUtil.checkNullZero(taxMap.getMap("TXTP_INTX21").toString())) + Long.parseLong(CbUtil.checkNullZero(taxMap.getMap("TXTP_INTX22").toString()))
                     + Long.parseLong(CbUtil.checkNullZero(taxMap.getMap("TXTP_INTX31").toString())) + Long.parseLong(CbUtil.checkNullZero(taxMap.getMap("TXTP_INTX32").toString())) + Long.parseLong(CbUtil.checkNullZero(taxMap.getMap("TXTP_INTX34").toString()))
                     + Long.parseLong(CbUtil.checkNullZero(taxMap.getMap("TXTP_INTX33").toString())) + Long.parseLong(CbUtil.checkNullZero(taxMap.getMap("TXTP_INTX91").toString()));
	       
			//log.debug("총 지방소득세  체크 :: " + tot_tax);
		
		
//			if(tot_emp < 1){
//				log.debug("총 인원 오류 :: " + tot_emp);
//				cmMap.setMap("RESP_CODE" , "1000");     /*응답코드*/
//				cmMap.setMap("TX_GUBUN"  , "260");
//				return DFL_008.makeSendBuffer(cmMap, taxMap);
//			}
//		
//			if(tot_std < 1){
//				log.debug("총 과세표준 오류 :: " + tot_std);
//				cmMap.setMap("RESP_CODE" , "1000");     /*응답코드*/
//				cmMap.setMap("TX_GUBUN"  , "260");
//				return DFL_008.makeSendBuffer(cmMap, taxMap);
//			}
//		
//			if(tot_tax < 1){
//				log.debug("총 지방소득세 오류 :: " + tot_tax);
//				cmMap.setMap("RESP_CODE" , "1000");     /*응답코드*/
//				cmMap.setMap("TX_GUBUN"  , "260");
//				return DFL_008.makeSendBuffer(cmMap, taxMap);
//			}
		
			// 전화번호 확인 - 필수
			if(CbUtil.nullChk(taxMap.getMap("BIZ_TEL").toString()).length() < 6){
				log.debug("전화번호(BIZ_TEL) 필수값 오류 :: " + CbUtil.nullChk(taxMap.getMap("BIZ_TEL").toString()));
				cmMap.setMap("RESP_CODE" , "1000");     /*응답코드*/
				cmMap.setMap("TX_GUBUN"  , "260");
				return DFL_008.makeSendBuffer(cmMap, taxMap);
			}
			
			//반기 확인
			if(CbUtil.nullChk(taxMap.getMap("REQ_DIV")).equals("2")){
				String sup_mm = CbUtil.nullChk(taxMap.getMap("SUP_YYMM").toString()).substring(4);
				if(sup_mm.equals("06")||sup_mm.equals("12")){
					log.debug("반기선택");
				}else{
					log.debug("반기선택 오류 :: " + CbUtil.nullChk(taxMap.getMap("SUP_YYMM").toString()));
				}
			}
			//주민번호 길이 체크
			if(CbUtil.nullChk(taxMap.getMap("REG_NO").toString()).length() < 13){
				log.debug("주민번호 오류 :: " + CbUtil.nullChk(taxMap.getMap("REG_NO").toString()));
				cmMap.setMap("RESP_CODE" , "1000");     /*응답코드*/
				cmMap.setMap("TX_GUBUN"  , "260");
				return DFL_008.makeSendBuffer(cmMap, taxMap);
			}
			//귀속년월 체크
			if(CbUtil.nullChk(taxMap.getMap("RVSN_YYMM").toString()).length() < 6){
				log.debug("귀속년월 오류 :: " + CbUtil.nullChk(taxMap.getMap("RVSN_YYMM").toString()));
				cmMap.setMap("RESP_CODE" , "1000");     /*응답코드*/
				cmMap.setMap("TX_GUBUN"  , "260");
				return DFL_008.makeSendBuffer(cmMap, taxMap);
			}
				
			//행정동코드 구하기
			ADONG_COD = getHacdCode(CbUtil.nullChk(taxMap.getMap("SGG_COD").toString()),CbUtil.nullChk(taxMap.getMap("LDONG_COD").toString()));   	
			if(ADONG_COD == null || ADONG_COD.equals("")){
				ADONG_COD = "000";
			}
			//법정동코드
			BUCD = CbUtil.nullChk(taxMap.getMap("LDONG_COD").toString()) + "00";
		
			log.debug("행정동코드  :: " + ADONG_COD + "법정동코드  :: " + BUCD);
		
			//납부할지방소득세 INTX, 납부할가산세 TOT_ADTX  구하기
			//납부할지방소득세 = 납부총금액 - 가산세    :  INTX = ADD_TOT_AMT - PAY_ADTX
			//납부할가산세 = 납부총금액 - 납부할지방소득세    : TOT_ADTX = ADD_TOT_AMT - INTX			
			long tempINTX = Long.parseLong(taxMap.getMap("ADD_TOT_AMT").toString()) - Long.parseLong(taxMap.getMap("PAY_ADTX").toString());
			long tempTOT_ADTX = Long.parseLong(taxMap.getMap("ADD_TOT_AMT").toString()) - tempINTX;
			INTX = Long.toString(tempINTX);
			TOT_ADTX = Long.toString(tempTOT_ADTX);
			log.debug("납부할지방소득세 :: " + INTX + "  납부할가산세 :: " + TOT_ADTX);
		
		}catch(Exception e){
			e.printStackTrace();
			cmMap.setMap("RESP_CODE" , "1000");     /*응답코드*/
    		cmMap.setMap("TX_GUBUN"  , "260");
    		return DFL_008.makeSendBuffer(cmMap, taxMap);
		}
		
		try{
				
			StringBuffer reqXml= new StringBuffer("<?xml version='1.0' encoding = 'euc-kr' ?>");
		    	
			reqXml.append("<LTIS>");
		    reqXml.append("<COMMON name='ComModel'></COMMON>");
		    reqXml.append("<CONTENT>");
		    reqXml.append("<DATA name='ThrJ2PInfo'>");
		    reqXml.append("<SIDO_COD><![CDATA[26]]></SIDO_COD>");
	        reqXml.append("<SGG_COD><![CDATA["+CbUtil.nullChk(taxMap.getMap("SGG_COD").toString())+"]]></SGG_COD>");
	        reqXml.append("<TAX_ITEM><![CDATA["+CbUtil.nullChk(taxMap.getMap("TAX_ITEM").toString())+"]]></TAX_ITEM>");
	        reqXml.append("<TAX_YYMM><![CDATA["+CbUtil.nullChk(taxMap.getMap("TAX_YY").toString())+CbUtil.nullChk(taxMap.getMap("TAX_MM").toString())+"]]></TAX_YYMM>");
	        reqXml.append("<TAX_DIV><![CDATA[3]]></TAX_DIV>");	        
	        
	        reqXml.append("<ADONG_COD><![CDATA["+ADONG_COD+"]]></ADONG_COD>");
	        reqXml.append("<TAX_DT><![CDATA["+CbUtil.nullChk(taxMap.getMap("TAX_DT").toString())+"]]></TAX_DT>");
	        reqXml.append("<REG_NO><![CDATA["+taxMap.getMap("REG_NO").toString()+"]]></REG_NO>");
	        reqXml.append("<REG_NM><![CDATA["+CbUtil.nullChk(taxMap.getMap("REG_NM").toString())+"]]></REG_NM>");
	        reqXml.append("<TPR_COD><![CDATA["+CbUtil.nullChk(taxMap.getMap("TPR_COD").toString())+"]]></TPR_COD>");
	        reqXml.append("<BIZ_NO><![CDATA["+CbUtil.nullChk(taxMap.getMap("BIZ_NO").toString())+"]]></BIZ_NO>");
	        reqXml.append("<CMP_NM><![CDATA["+CbUtil.nullChk(taxMap.getMap("CMP_NM").toString())+"]]></CMP_NM>");
	        reqXml.append("<LDONG_COD><![CDATA["+BUCD+"]]></LDONG_COD>");
	        reqXml.append("<BIZ_ZIP_NO><![CDATA["+CbUtil.nullChk(taxMap.getMap("BIZ_ZIP_NO").toString())+"]]></BIZ_ZIP_NO>");
	        reqXml.append("<BIZ_ADDR><![CDATA["+CbUtil.nullChk(taxMap.getMap("BIZ_ADDR").toString())+"]]></BIZ_ADDR>");
	        reqXml.append("<MO_TEL><![CDATA["+CbUtil.nullChk(taxMap.getMap("MO_TEL").toString())+"]]></MO_TEL>");
	        reqXml.append("<BIZ_TEL><![CDATA["+CbUtil.nullChk(taxMap.getMap("BIZ_TEL").toString())+"]]></BIZ_TEL>");
	        reqXml.append("<REQ_DIV><![CDATA["+CbUtil.nullChk(taxMap.getMap("REQ_DIV").toString())+"]]></REQ_DIV>");
	        	        
	        reqXml.append("<RVSN_YYMM><![CDATA["+CbUtil.nullChk(taxMap.getMap("RVSN_YYMM").toString())+"]]></RVSN_YYMM>");
	        reqXml.append("<SUP_YYMM><![CDATA["+CbUtil.nullChk(taxMap.getMap("SUP_YYMM").toString())+"]]></SUP_YYMM>"); 
	        reqXml.append("<F_DUE_DT><![CDATA["+CbUtil.nullChk(taxMap.getMap("F_DUE_DT").toString())+"]]></F_DUE_DT>");
	        reqXml.append("<DUE_DT><![CDATA["+CbUtil.nullChk(taxMap.getMap("DUE_DT").toString())+"]]></DUE_DT>");
	        reqXml.append("<TAX_RT><![CDATA[10]]></TAX_RT>");
	        reqXml.append("<TOT_STD_AMT><![CDATA["+CbUtil.nullChk(taxMap.getMap("TOT_STD_AMT").toString())+"]]></TOT_STD_AMT>");
	        reqXml.append("<PAY_RSTX><![CDATA["+CbUtil.nullChk(taxMap.getMap("PAY_RSTX").toString())+"]]></PAY_RSTX>");
	        reqXml.append("<ADTX_YN><![CDATA["+CbUtil.nullChk(taxMap.getMap("ADTX_YN").toString())+"]]></ADTX_YN>");
	        reqXml.append("<ADTX_AM><![CDATA["+CbUtil.nullChk(taxMap.getMap("ADTX_AM").toString())+"]]></ADTX_AM>");
	        reqXml.append("<DLQ_ADTX><![CDATA["+CbUtil.nullChk(taxMap.getMap("DLQ_ADTX").toString())+"]]></DLQ_ADTX>");
	        reqXml.append("<DLQ_CNT><![CDATA["+CbUtil.nullChk(taxMap.getMap("DLQ_CNT").toString())+"]]></DLQ_CNT>");
	        reqXml.append("<PAY_ADTX><![CDATA["+CbUtil.nullChk(taxMap.getMap("PAY_ADTX").toString())+"]]></PAY_ADTX>");
	        reqXml.append("<MEMO><![CDATA["+CbUtil.nullChk(taxMap.getMap("MEMO").toString())+"]]></MEMO>");
	        reqXml.append("<ADD_MM_RTN><![CDATA["+CbUtil.nullChk(taxMap.getMap("ADD_MM_RTN").toString())+"]]></ADD_MM_RTN>");
	        reqXml.append("<ADD_MM_AAMT><![CDATA["+CbUtil.nullChk(taxMap.getMap("ADD_MM_AAMT").toString())+"]]></ADD_MM_AAMT>");	        
	        
	        reqXml.append("<ADD_YY_TRTN><![CDATA["+CbUtil.nullChk(taxMap.getMap("ADD_YY_TRTN").toString())+"]]></ADD_YY_TRTN>");
	        reqXml.append("<ADD_YY_TAMT><![CDATA["+CbUtil.nullChk(taxMap.getMap("ADD_YY_TAMT").toString())+"]]></ADD_YY_TAMT>");
	        reqXml.append("<ADD_ETC_RTN><![CDATA["+CbUtil.nullChk(taxMap.getMap("ADD_ETC_RTN").toString())+"]]></ADD_ETC_RTN>");
	        reqXml.append("<ADD_RDT_ADTX><![CDATA["+CbUtil.nullChk(taxMap.getMap("ADD_RDT_ADTX").toString())+"]]></ADD_RDT_ADTX>");
	        reqXml.append("<ADD_RDT_AADD><![CDATA["+CbUtil.nullChk(taxMap.getMap("ADD_RDT_AADD").toString())+"]]></ADD_RDT_AADD>");
	        reqXml.append("<ADD_SUM_RTN><![CDATA["+CbUtil.nullChk(taxMap.getMap("ADD_SUM_RTN").toString())+"]]></ADD_SUM_RTN>");
	        reqXml.append("<ADD_SUM_AAMT><![CDATA["+CbUtil.nullChk(taxMap.getMap("ADD_SUM_AAMT").toString())+"]]></ADD_SUM_AAMT>");
	        reqXml.append("<ADD_OUT_AMT><![CDATA["+CbUtil.nullChk(taxMap.getMap("ADD_OUT_AMT").toString())+"]]></ADD_OUT_AMT>");
	        reqXml.append("<ADD_TOT_AMT><![CDATA["+CbUtil.nullChk(taxMap.getMap("ADD_TOT_AMT").toString())+"]]></ADD_TOT_AMT>");
	        reqXml.append("<INTX><![CDATA[" + INTX + "]]></INTX>");
	        reqXml.append("<TOT_ADTX><![CDATA[" + TOT_ADTX + "]]></TOT_ADTX>");
	       		        
	        reqXml.append("<ADD_OUT_SAMT><![CDATA["+CbUtil.nullChk(taxMap.getMap("ADD_OUT_SAMT").toString())+"]]></ADD_OUT_SAMT>");
	        reqXml.append("<MINU_YN><![CDATA["+CbUtil.nullChk(taxMap.getMap("MINU_YN").toString())+"]]></MINU_YN>");
	        reqXml.append("<SGG_COD2><![CDATA[]]></SGG_COD2>");
	        reqXml.append("<RPT_REG_NO><![CDATA["+CbUtil.nullChk(taxMap.getMap("REG_NO").toString())+"]]></RPT_REG_NO>");
	        reqXml.append("<RPT_NM><![CDATA["+CbUtil.nullChk(taxMap.getMap("REG_NM").toString())+"]]></RPT_NM>");
	        reqXml.append("<RPT_TEL><![CDATA["+CbUtil.nullChk(taxMap.getMap("BIZ_TEL").toString())+"]]></RPT_TEL>");
	        reqXml.append("<RPT_ID><![CDATA[ETAX9]]></RPT_ID>");
	        reqXml.append("<RPT_SYSTEM><![CDATA[ETAX]]></RPT_SYSTEM>");	        
	        reqXml.append("</DATA>");
	              	        
	        reqXml.append("<LIST size='12' name='ThrJ2PInfo'>");
	        reqXml.append("<RECORD name='ThrJ2PInfo'>");
	        reqXml.append("<TXTP_CD><![CDATA[11]]></TXTP_CD>");  //이자소득
	        reqXml.append("<TXTP_EMP><![CDATA[" + CbUtil.nullChk(taxMap.getMap("TXTP_EMP11").toString())  + "]]></TXTP_EMP>"); //인원
	        reqXml.append("<TXTP_STD><![CDATA[" + CbUtil.nullChk(taxMap.getMap("TXTP_STD11").toString())  + "]]></TXTP_STD>"); //과세표준
	        reqXml.append("<TXTP_INTX><![CDATA["+ CbUtil.nullChk(taxMap.getMap("TXTP_INTX11").toString()) + "]]></TXTP_INTX>");//지방소득세	        	        
	        reqXml.append("</RECORD>");
	        reqXml.append("<RECORD name='ThrJ2PInfo'>");
	        reqXml.append("<TXTP_CD><![CDATA[12]]></TXTP_CD>");  //배당소득
	        reqXml.append("<TXTP_EMP><![CDATA[" + CbUtil.nullChk(taxMap.getMap("TXTP_EMP12").toString())  + "]]></TXTP_EMP>"); //인원
	        reqXml.append("<TXTP_STD><![CDATA[" + CbUtil.nullChk(taxMap.getMap("TXTP_STD12").toString())  + "]]></TXTP_STD>"); //과세표준
	        reqXml.append("<TXTP_INTX><![CDATA["+ CbUtil.nullChk(taxMap.getMap("TXTP_INTX12").toString()) + "]]></TXTP_INTX>");//지방소득세
	        reqXml.append("</RECORD>");
	        reqXml.append("<RECORD name='ThrJ2PInfo'>");
	        reqXml.append("<TXTP_CD><![CDATA[13]]></TXTP_CD>");  //사업소득
	        reqXml.append("<TXTP_EMP><![CDATA[" + CbUtil.nullChk(taxMap.getMap("TXTP_EMP13").toString())  + "]]></TXTP_EMP>"); //인원
	        reqXml.append("<TXTP_STD><![CDATA[" + CbUtil.nullChk(taxMap.getMap("TXTP_STD13").toString())  + "]]></TXTP_STD>"); //과세표준
	        reqXml.append("<TXTP_INTX><![CDATA["+ CbUtil.nullChk(taxMap.getMap("TXTP_INTX13").toString()) + "]]></TXTP_INTX>");//지방소득세
	        reqXml.append("</RECORD>");
	        reqXml.append("<RECORD name='ThrJ2PInfo'>");
	        reqXml.append("<TXTP_CD><![CDATA[14]]></TXTP_CD>");  //근로소득
	        reqXml.append("<TXTP_EMP><![CDATA[" + CbUtil.nullChk(taxMap.getMap("TXTP_EMP14").toString())  + "]]></TXTP_EMP>"); //인원
	        reqXml.append("<TXTP_STD><![CDATA[" + CbUtil.nullChk(taxMap.getMap("TXTP_STD14").toString())  + "]]></TXTP_STD>"); //과세표준
	        reqXml.append("<TXTP_INTX><![CDATA["+ CbUtil.nullChk(taxMap.getMap("TXTP_INTX14").toString()) + "]]></TXTP_INTX>");//지방소득세
	        reqXml.append("</RECORD>");
	        reqXml.append("<RECORD name='ThrJ2PInfo'>");
	        reqXml.append("<TXTP_CD><![CDATA[16]]></TXTP_CD>");  //기타소득
	        reqXml.append("<TXTP_EMP><![CDATA[" + CbUtil.nullChk(taxMap.getMap("TXTP_EMP16").toString())  + "]]></TXTP_EMP>"); //인원
	        reqXml.append("<TXTP_STD><![CDATA[" + CbUtil.nullChk(taxMap.getMap("TXTP_STD16").toString())  + "]]></TXTP_STD>"); //과세표준
	        reqXml.append("<TXTP_INTX><![CDATA["+ CbUtil.nullChk(taxMap.getMap("TXTP_INTX16").toString()) + "]]></TXTP_INTX>");//지방소득세
	        reqXml.append("</RECORD>");
	        reqXml.append("<RECORD name='ThrJ2PInfo'>");
	        reqXml.append("<TXTP_CD><![CDATA[17]]></TXTP_CD>");  //연금소득
	        reqXml.append("<TXTP_EMP><![CDATA[" + CbUtil.nullChk(taxMap.getMap("TXTP_EMP17").toString())  + "]]></TXTP_EMP>"); //인원
	        reqXml.append("<TXTP_STD><![CDATA[" + CbUtil.nullChk(taxMap.getMap("TXTP_STD17").toString())  + "]]></TXTP_STD>"); //과세표준
	        reqXml.append("<TXTP_INTX><![CDATA["+ CbUtil.nullChk(taxMap.getMap("TXTP_INTX17").toString()) + "]]></TXTP_INTX>");//지방소득세
	        reqXml.append("</RECORD>");
	        reqXml.append("<RECORD name='ThrJ2PInfo'>");
	        reqXml.append("<TXTP_CD><![CDATA[21]]></TXTP_CD>");  //퇴직소득
	        reqXml.append("<TXTP_EMP><![CDATA[" + CbUtil.nullChk(taxMap.getMap("TXTP_EMP21").toString())  + "]]></TXTP_EMP>"); //인원
	        reqXml.append("<TXTP_STD><![CDATA[" + CbUtil.nullChk(taxMap.getMap("TXTP_STD21").toString())  + "]]></TXTP_STD>"); //과세표준
	        reqXml.append("<TXTP_INTX><![CDATA["+ CbUtil.nullChk(taxMap.getMap("TXTP_INTX21").toString()) + "]]></TXTP_INTX>");//지방소득세
	        reqXml.append("</RECORD>");
	        reqXml.append("<RECORD name='ThrJ2PInfo'>");
	        reqXml.append("<TXTP_CD><![CDATA[22]]></TXTP_CD>");  //양도소득 소득세법 제119조
	        reqXml.append("<TXTP_EMP><![CDATA[" + CbUtil.nullChk(taxMap.getMap("TXTP_EMP22").toString())  + "]]></TXTP_EMP>"); //인원
	        reqXml.append("<TXTP_STD><![CDATA[" + CbUtil.nullChk(taxMap.getMap("TXTP_STD22").toString())  + "]]></TXTP_STD>"); //과세표준
	        reqXml.append("<TXTP_INTX><![CDATA["+ CbUtil.nullChk(taxMap.getMap("TXTP_INTX22").toString()) + "]]></TXTP_INTX>");//지방소득세
	        reqXml.append("</RECORD>");
	        reqXml.append("<RECORD name='ThrJ2PInfo'>");
	        reqXml.append("<TXTP_CD><![CDATA[31]]></TXTP_CD>");  //외국법인 법인세법 제98조
	        reqXml.append("<TXTP_EMP><![CDATA[" + CbUtil.nullChk(taxMap.getMap("TXTP_EMP31").toString())  + "]]></TXTP_EMP>"); //인원
	        reqXml.append("<TXTP_STD><![CDATA[" + CbUtil.nullChk(taxMap.getMap("TXTP_STD31").toString())  + "]]></TXTP_STD>"); //과세표준
	        reqXml.append("<TXTP_INTX><![CDATA["+ CbUtil.nullChk(taxMap.getMap("TXTP_INTX31").toString()) + "]]></TXTP_INTX>");//지방소득세
	        reqXml.append("</RECORD>");
	        reqXml.append("<RECORD name='ThrJ2PInfo'>");
	        reqXml.append("<TXTP_CD><![CDATA[32]]></TXTP_CD>");  //저축해지
	        reqXml.append("<TXTP_EMP><![CDATA[" + CbUtil.nullChk(taxMap.getMap("TXTP_EMP32").toString())  + "]]></TXTP_EMP>"); //인원
	        reqXml.append("<TXTP_STD><![CDATA[" + CbUtil.nullChk(taxMap.getMap("TXTP_STD32").toString())  + "]]></TXTP_STD>"); //과세표준
	        reqXml.append("<TXTP_INTX><![CDATA["+ CbUtil.nullChk(taxMap.getMap("TXTP_INTX32").toString()) + "]]></TXTP_INTX>");//지방소득세
	        reqXml.append("</RECORD>");
	        reqXml.append("<RECORD name='ThrJ2PInfo'>");
	        reqXml.append("<TXTP_CD><![CDATA[33]]></TXTP_CD>");  //내국법인 법인세법 제73조
	        reqXml.append("<TXTP_EMP><![CDATA[" + CbUtil.nullChk(taxMap.getMap("TXTP_EMP33").toString())  + "]]></TXTP_EMP>"); //인원
	        reqXml.append("<TXTP_STD><![CDATA[" + CbUtil.nullChk(taxMap.getMap("TXTP_STD33").toString())  + "]]></TXTP_STD>"); //과세표준
	        reqXml.append("<TXTP_INTX><![CDATA["+ CbUtil.nullChk(taxMap.getMap("TXTP_INTX33").toString()) + "]]></TXTP_INTX>");//지방소득세
	        reqXml.append("</RECORD>");
	        reqXml.append("<RECORD name='ThrJ2PInfo'>");
	        reqXml.append("<TXTP_CD><![CDATA[91]]></TXTP_CD>");  //외국인으로부터 받은 소득(구서식)
	        reqXml.append("<TXTP_EMP><![CDATA[" + CbUtil.nullChk(taxMap.getMap("TXTP_EMP91").toString())  + "]]></TXTP_EMP>"); //인원
	        reqXml.append("<TXTP_STD><![CDATA[" + CbUtil.nullChk(taxMap.getMap("TXTP_STD91").toString())  + "]]></TXTP_STD>"); //과세표준
	        reqXml.append("<TXTP_INTX><![CDATA["+ CbUtil.nullChk(taxMap.getMap("TXTP_INTX91").toString()) + "]]></TXTP_INTX>");//지방소득세
	        reqXml.append("</RECORD>");
	        reqXml.append("</LIST>");
		    reqXml.append("</CONTENT>");
	        reqXml.append("</LTIS>");
		    
	        log.debug("reqXml 666 :: " + reqXml);
	        
		    try{    			    
			    // 지방세 양도소득분 신고 송수신 서비스 호출
			    Tax008Service  soapservice = new  Tax008Service();
		    	
			    MapForm recvForm = null;
			    	
			    try{
			    	recvForm = soapservice.sndService(reqXml, taxMap.getMap("SGG_COD").toString());;
			    }catch(Exception e){
			    	e.printStackTrace();
			    	//RE_CO = "1000";	    
			    	cmMap.setMap("RESP_CODE" , "1000");     /*응답코드*/
		    		cmMap.setMap("TX_GUBUN"  , "260");
		    		return DFL_008.makeSendBuffer(cmMap, taxMap);
			    }
			    	
			    //log.debug("****recvForm*********"+recvForm+"******************* ");
			    	
			    taxMap.putAll(recvForm);
			    			 
			    if(taxMap.getMap("MESSAGE").equals("SVR01")){
			    		
			    	if(taxMap.getMap("EPAY_NO").equals("") || taxMap.getMap("EPAY_NO").toString() == null){
	    	    		//납부금액이 소액일 경우 전자납부번호가 빈값으로 넙어온다.--에러처리
			    		cmMap.setMap("RESP_CODE" , "3010");     /*응답코드 */
			    		cmMap.setMap("TX_GUBUN"  , "260");
			    		return DFL_008.makeSendBuffer(cmMap, taxMap);
	    	    	}
			    	//taxMap.setMap("BUCD", bucd);	    		
			    	taxMap.setMap("YYMD", taxMap.getMap("TAX_DT").toString());
			    	taxMap.setMap("REQ_SNMH", taxMap.getMap("DPNM").toString());
			    	taxMap.setMap("REQ_NM", taxMap.getMap("DPNM").toString());
			    	taxMap.setMap("F_DUE_DT", taxMap.getMap("DUE_DT").toString());
			    			    		
			    	log.debug("************ 특별징수 신고 완료 *********");
			    		
			    }else{

			    	log.debug("************ 특별징수 웹서비스 에러 *********");
			    	cmMap.setMap("RESP_CODE" , "9000");     /*응답코드*/
		    		cmMap.setMap("TX_GUBUN"  , "260");
		    		return DFL_008.makeSendBuffer(cmMap, taxMap);
			    }    
			    	
			}catch(Exception e){
			    	
			    log.debug("특별징수 신고 전문 송수신오류 ");
			    cmMap.setMap("RESP_CODE" , "9000");     /*응답코드*/
	    		cmMap.setMap("TX_GUBUN"  , "260");
	    		return DFL_008.makeSendBuffer(cmMap, taxMap);
			   
			}finally{
			    //clear();
			}
		    
					
			/******************************************************
			* 표준 사업단 자료 연계 처리  END
			* *****************************************************/
				    
			// 받아온 전문 저장
			taxMap.setMap("STATE", state);		    
			
			taxMap.setMap("JYMD", taxMap.getMap("SUP_YYMM"));   //지급년월
			
			taxMap.setMap("WANT", tot_emp);   //총 인원
			taxMap.setMap("GWAT", tot_std);   //총 과세표준
			taxMap.setMap("TAXT", tot_tax);   //총 지방소득세
			
			taxMap.setMap("HAMT", taxMap.getMap("ADD_SUM_RTN")); //환급금합계
			
			taxMap.setMap("SUM_RCP", taxMap.getMap("NAPBU_TAX")); //납부총금액
			
			taxMap.setMap("TAX_DD", taxMap.getMap("TX_DATETIME").toString().substring(6, 8));
			taxMap.setMap("TAX_TIME",taxMap.getMap("TX_DATETIME").toString().substring(8));
			
			try{
				sqlService_cyber.queryForInsert("TXDM2610.insertBaroPayData", taxMap);
			}catch(Exception e){
				e.printStackTrace();
				log.debug("연계테이블 등록 오류");
				cmMap.setMap("RESP_CODE" , "8000");     /*응답코드*/
				cmMap.setMap("TX_GUBUN"  , "260");
		    	return DFL_008.makeSendBuffer(cmMap, taxMap);
			}
		    
		    
				    
		}catch(Exception e){
					
			e.printStackTrace();
			cmMap.setMap("RESP_CODE" , "1000");     /*응답코드*/
			cmMap.setMap("TX_GUBUN"  , "260");
			return DFL_008.makeSendBuffer(cmMap, taxMap);
		}
			
	
		// 리턴 값 정리
		cmMap.setMap("RESP_CODE" , "0000");     /*응답코드*/
		cmMap.setMap("TX_GUBUN"  , "260");
		return DFL_008.makeSendBuffer(cmMap, taxMap);
	} 
		
	
	// 주민세 재산분 전자신고 표준지방세 연계
	private byte[] sendJobTax009(MapForm cmMap, MapForm taxMap) {
			    	
		/******************************************************
	    * 표준 사업단 자료 연계 처리  START
		* *****************************************************/
		MapForm sndForm = new MapForm();
			
		try{
			sndForm.setMap("tax_gubun"	       ,   "104009"); // 택스구분
			sndForm.setMap("SIDO_COD"          ,   "26");     // 시도코드
			sndForm.setMap("SGG_COD"           ,   CbUtil.checkNull(taxMap.getMap("SGG_COD").toString())); // 시군구코드
			sndForm.setMap("TAX_ITEM"          ,   "104009");    // 세목코드
			sndForm.setMap("TAX_YYMM"          ,   CbUtil.checkNull(taxMap.getMap("TAX_YY").toString()+taxMap.getMap("TAX_MM").toString()));    // 과세년월
			sndForm.setMap("TAX_DIV"           ,   "3");         // 과세구분
		    	
		    String bucd = getLdongCode(taxMap.getMap("SGG_COD").toString(), taxMap.getMap("HACD").toString());   	
		    	
		    //String address = getAddress(taxMap.getMap("ZIP_CD").toString());
		    	
		    //taxMap.setMap("ADDRESS", address);
		    	
			sndForm.setMap("ADONG_COD"         ,   CbUtil.checkNull(taxMap.getMap("HACD").toString()));    // 과세행정동
			  
			sndForm.setMap("TAX_DT"            ,   taxMap.getMap("TAX_YY").toString()+taxMap.getMap("TAX_MM").toString()+taxMap.getMap("TAX_DD").toString());    // 신고일자
			sndForm.setMap("REG_NO"            ,   CbUtil.checkNull(taxMap.getMap("REG_NO").toString()));  // 납세자 주민/법인번호
			sndForm.setMap("REG_NM"            ,   CbUtil.checkNull(taxMap.getMap("DPNM").toString()));    // 납세자 성명
			sndForm.setMap("TPR_COD"           ,   (taxMap.getMap("DP_GBN").equals("0"))?"00":"20");    // 법인코드 DP_GBN
			sndForm.setMap("LDONG_COD"         ,   CbUtil.checkNull(bucd) + "00");    // 납세자 법정동
			      
			sndForm.setMap("ZIP_NO"            ,   CbUtil.checkNull(taxMap.getMap("ZIP_CD").toString()));    // 납세자 우편번호
			sndForm.setMap("ZIP_ADDR"          ,   CbUtil.checkNull(taxMap.getMap("ADDR").toString()));    // 납세자 우편주소
			sndForm.setMap("OTH_ADDR"          ,   " " + CbUtil.checkNull(taxMap.getMap("ADDR_DT").toString()));     // (100)  납세자 상세주소
			sndForm.setMap("MO_TEL"            ,   CbUtil.checkNull(taxMap.getMap("PHONE_NO").toString()));    // 납세자 핸드폰번호
			sndForm.setMap("TEL"               ,   CbUtil.checkNull(taxMap.getMap("TEL_NO").toString()));    // 납세자 전화번호
			sndForm.setMap("BIZ_NO"            ,   CbUtil.checkNull(taxMap.getMap("SAUP_NO").toString()));    // 납세자 사업자번호
			sndForm.setMap("CMP_NM"            ,   CbUtil.checkNull(taxMap.getMap("CMP_NM").toString()));     // (100)  상호명
		    sndForm.setMap("BIZ_ZIP_NO"        ,   CbUtil.checkNull(taxMap.getMap("ZIP_CD").toString()));    // (6)    사업장우편번호
		    sndForm.setMap("BIZ_ZIP_ADDR"      ,   CbUtil.checkNull(taxMap.getMap("ADDR").toString()));                  // (60)   사업장우편주소
		    sndForm.setMap("BIZ_ADDR"          ,   CbUtil.checkNull(taxMap.getMap("ADDR_DT").toString()));   // (100)  사업장상세주소        
		    sndForm.setMap("RVSN_YY"           ,   CbUtil.checkNull(taxMap.getMap("RVSN_YY").toString()));    // (6)    귀속년도
		        
		    sndForm.setMap("DUE_DT"            ,   CbUtil.checkNull(taxMap.getMap("DUE_DT").toString())); 
		    sndForm.setMap("F_DUE_DT"          ,   CbUtil.checkNull(taxMap.getMap("F_DUE_DT").toString())); 
		    sndForm.setMap("US_AREA"           ,   CbUtil.checkNull(taxMap.getMap("GSGY").toString()));  //총사용면적 

		    sndForm.setMap("TXE_AREA"          ,   "0"); //비과세면적
		    sndForm.setMap("TXE_AREA_R"        ,   ""); //varchar(100)비과세면적내역
		    sndForm.setMap("TAX_AREA"          ,   CbUtil.checkNull(taxMap.getMap("GSGY").toString())); //과세면적 
		    sndForm.setMap("TAX_STD"           ,   CbUtil.checkNull(taxMap.getMap("GSGY").toString())); //과세표준
		    sndForm.setMap("POL_YN"            ,   CbUtil.checkNull(taxMap.getMap("BECH").toString())); //공해유발유무(2:무 ,1:유)
		    sndForm.setMap("RDX_COD"           ,   ""); //(중과)감면코드
		    sndForm.setMap("RDX_R"             ,   ""); //감면사유
		    sndForm.setMap("RDX_AMT"           ,   "0"); //비과세세액
		    sndForm.setMap("TAX_RT"            ,   (taxMap.getMap("BECH").equals("0"))?"250":"500"); //세율
		    sndForm.setMap("R_P_AMT"           ,   CbUtil.checkNull(taxMap.getMap("SGEK").toString())); //신고납부세액
		    sndForm.setMap("ADTX_YN"           ,   "2"); //가산세(2:무,1:유)
		    sndForm.setMap("DLQ_CNT"           ,   "0"); //납부지연일수
		    sndForm.setMap("RADTX"             ,   "0"); //신고불성실가산세
		    sndForm.setMap("PADTX"             ,   "0"); //납부불성실가산세
		    sndForm.setMap("TOT_ADTX"          ,   "0"); //가산세액계
		    sndForm.setMap("TOT_AMT"           ,   CbUtil.checkNull(taxMap.getMap("SUM_RCP").toString())); //총납부금액
		    sndForm.setMap("RPT_REG_NO"        ,   CbUtil.checkNull(taxMap.getMap("REG_NO").toString())); //신고자주민/법인번호
		    sndForm.setMap("RPT_NM"            ,   CbUtil.checkNull(taxMap.getMap("DPNM").toString())); //신고자성명/법인명
		    sndForm.setMap("RPT_TEL"           ,   CbUtil.checkNull(taxMap.getMap("TEL_NO").toString())); //신고자전화번호
		    sndForm.setMap("RPT_ID"            ,   "ETAX9"); //신고자ID
		    sndForm.setMap("RPT_ADMIN"         ,   ""); //신고자기관/부서명
		    sndForm.setMap("RPT_SYSTEM"        ,   "ETAX"); //신고시스템구분
		
		    sndForm.setMap("SINGO_DIV"         ,   "0");    // (1)      신고구분   -  최초신고:1 , 수정신고:2
		    sndForm.setMap("B_TAX_NO"          ,   "");    // (31)     최초신고분 과세번호   -  수정신고시필수
		    sndForm.setMap("B_EPAY_NO"         ,   "");    // (19)     최초신고분 전자납부번호   -  수정신고시필수
		    sndForm.setMap("CHG_MEMO"          ,   "");    // (2)      수정신고내용   -  수정신고시필수   - 과소신고 : 11   - 환급세액 초과 : 12   - 정산누락에 따른 과소신고 : 13   - 기타 : 91
		    sndForm.setMap("CHG_REASON"        ,   "");    // (2)      수정신고사유   -  수정신고시필수 - 과세표준의 과소신고 : 21 - 산출세액의 과소신고 : 22 - 환급세액 초과 : 23 - 특별징수의무자의 정산과정에서 누락 :24 - 기타 : 92
		    sndForm.setMap("REASON_DT"         ,   "");    // (8)      사유발생일   -  수정신고시필수
		    sndForm.setMap("ETC_MEMO"          ,   "");    // (200)    비고   -  수정신고시 사용
		    sndForm.setMap("SUNAP_DT"          ,   "");    // (8)      수납일자   -  수정신고시 사용
		    sndForm.setMap("EVI_DOC1"          ,   "");    // (100)    서류명
		    sndForm.setMap("EVI_DOC_URL1"      ,   "");    // (100)    서류조회URL
		    sndForm.setMap("EVI_DOC2"          ,   "");    // (100)    서류명2
		    sndForm.setMap("EVI_DOC_URL2"      ,   "");    // (100)    서류조회URL2
		    sndForm.setMap("EVI_DOC3"          ,   "");    // (100)    서류명3
		    sndForm.setMap("EVI_DOC_URL3"      ,   "");    // (100)    서류조회URL3																										);    // (100)    서류조회URL2
		        
		    sndForm.setMap("C_TAXADD_R"        ,   "");    // (CHAR2)  신고불성실가산세구분   -  <2013신규>
			sndForm.setMap("C_TAXADD_GUBUN"    ,   "");    // (CHAR2)  부정신고사유   -  <2013신규>
			    
			    
			sndForm.setMap("RADTX_RT"          ,   "");    // (NUMBER5.4)  신고불성실가산적용세율   -  <2013신규>
			//부정신고일때만 과세대상 급여액값 넘겨줌
			sndForm.setMap("F_SINGO_STD"       ,   "");    // (NUMBER14)  부정신고적용과표   -  <2013신규>
			    	    
			try{    			    
			    // 지방세 양도소득분 신고 송수신 서비스 호출
			    Tax009Service  soapservice = new  Tax009Service();
			    	
			    //log.debug("****sndForm*********"+sndForm+"******************* ");
			    	
			    MapForm recvForm = null;
			    	
			    try{
			    	recvForm = soapservice.sndService(sndForm);
			    }catch(Exception e){
			    	e.printStackTrace();
			    	//RE_CO = "1000";	    
			    	cmMap.setMap("RESP_CODE" , "1000");     /*응답코드*/
		    		cmMap.setMap("TX_GUBUN"  , "160");
		    		return DFL_009.makeSendBuffer(cmMap, taxMap);
			    }
			    	
			    //log.debug("****recvForm*********"+recvForm+"******************* ");
			    	
			    taxMap.putAll(recvForm);
			    			 
			    if(taxMap.getMap("MESSAGE").equals("SVR01")){
			    		
			    	if(taxMap.getMap("EPAY_NO").equals("") || taxMap.getMap("EPAY_NO").toString() == null){
	    	    		//납부금액이 소액일 경우 전자납부번호가 빈값으로 넙어온다.--에러처리
			    		cmMap.setMap("RESP_CODE" , "3010");     /*응답코드 */
			    		cmMap.setMap("TX_GUBUN"  , "160");
			    		return DFL_009.makeSendBuffer(cmMap, taxMap);
	    	    	}
			    	taxMap.setMap("BUCD", bucd);	    		
			    	taxMap.setMap("YYMD", sndForm.getMap("TAX_DT").toString());
			    	taxMap.setMap("REQ_SNMH", taxMap.getMap("DPNM").toString());
			    	taxMap.setMap("REQ_NM", taxMap.getMap("DPNM").toString());
			    	taxMap.setMap("F_DUE_DT", taxMap.getMap("DUE_DT").toString());
			    			    		
			    	log.debug("************ 주민세 재산분 신고 완료 *********");
			    		
			    }else{

			    	log.debug("************ 주민세 재산분 웹서비스 에러 *********");
			    	cmMap.setMap("RESP_CODE" , "9000");     /*응답코드*/
		    		cmMap.setMap("TX_GUBUN"  , "160");
		    		return DFL_009.makeSendBuffer(cmMap, taxMap);
			    }    
			    	
			}catch(Exception e){
			    	
			    log.debug("주민세 재산분 신고 전문 송수신오류 ");
			    cmMap.setMap("RESP_CODE" , "9000");     /*응답코드*/
	    		cmMap.setMap("TX_GUBUN"  , "160");
	    		return DFL_009.makeSendBuffer(cmMap, taxMap);
			   
			}finally{
			    //clear();
			}
				
			/******************************************************
		    * 표준 사업단 자료 연계 처리  END
			* *****************************************************/
			    
			// 받아온 전문 저장
			taxMap.setMap("STATE", state);		    
			        
			try{
			    sqlService_cyber.queryForInsert("TXDM2610.insertBaroPayData", taxMap);
			}catch(Exception e){
			    e.printStackTrace();
			    log.debug("연계테이블 등록 오류");
			    cmMap.setMap("RESP_CODE" , "8000");     /*응답코드*/
	    		cmMap.setMap("TX_GUBUN"  , "160");
	    		return DFL_009.makeSendBuffer(cmMap, taxMap);
			}
			    
		}catch(Exception e){
				
			e.printStackTrace();
			cmMap.setMap("RESP_CODE" , "1000");     /*응답코드*/
			cmMap.setMap("TX_GUBUN"  , "160");
			return DFL_009.makeSendBuffer(cmMap, taxMap);
		}
		
		    
		// 리턴 값 정리
		cmMap.setMap("RESP_CODE" , "0000");     /*응답코드*/
		cmMap.setMap("TX_GUBUN"  , "160");
		return DFL_005.makeSendBuffer(cmMap, taxMap);
	}
	    
		
	// 주민세 종업원분 전자신고 표준지방세 연계
	private byte[] sendJobTax011(MapForm cmMap, MapForm taxMap) {
			    	
		/******************************************************
		* 표준 사업단 자료 연계 처리  START
		* *****************************************************/
		MapForm sndForm  = new MapForm();
			
		try{
			sndForm.setMap("tax_gubun"	       ,   "104011");	  //택스구분
			sndForm.setMap("SIDO_COD"          ,   "26");    // 시도코드
			sndForm.setMap("SGG_COD"           ,   CbUtil.checkNull(taxMap.getMap("SGG_COD").toString()));    // 시군구코드
			sndForm.setMap("TAX_ITEM"          ,   "104011");    // 세목코드
			sndForm.setMap("TAX_YYMM"          ,   CbUtil.checkNull(taxMap.getMap("TAX_YY").toString()+taxMap.getMap("TAX_MM").toString()));    // 과세년월
			sndForm.setMap("TAX_DIV"           ,   "3");    // 과세구분
		    	
		    String bucd = getLdongCode(taxMap.getMap("SGG_COD").toString(), taxMap.getMap("HACD").toString());   		    	
		    //String address = getAddress(taxMap.getMap("ZIP_CD").toString());	    	
		    //taxMap.setMap("ADDRESS", address);	    	
			sndForm.setMap("ADONG_COD"         ,   CbUtil.checkNull(taxMap.getMap("HACD").toString()));    // 과세행정동
			  		    
			sndForm.setMap("TAX_DT"            ,   CbUtil.checkNull(taxMap.getMap("TAX_YY").toString()+taxMap.getMap("TAX_MM").toString()+taxMap.getMap("TAX_DD").toString()));    // (8)      신고일자
		    sndForm.setMap("TPR_COD"           ,   (taxMap.getMap("DP_GBN").equals("0"))?"00":"20");   // (2)      법인코드   -  행자부표준코드(납세의무자(개인.법인)코드)
		    sndForm.setMap("REG_NO"            ,   CbUtil.checkNull(taxMap.getMap("REG_NO").toString()));    // (13)     주민등록번호
		    sndForm.setMap("REG_NM"            ,   CbUtil.checkNull(taxMap.getMap("DPNM").toString()));    // (80)     성명
		    sndForm.setMap("LDONG_COD"         ,   CbUtil.checkNull(bucd) + "00");    // (5)      물건지법정동
		    sndForm.setMap("ZIP_NO"            ,   CbUtil.checkNull(taxMap.getMap("ZIP_CD").toString()));   // (6)      납세자우편번호
		    sndForm.setMap("ZIP_ADDR"          ,   CbUtil.checkNull(taxMap.getMap("ADDR").toString()));    // (60)     납세자우편주소
		    sndForm.setMap("OTH_ADDR"          ,   " " + CbUtil.checkNull(taxMap.getMap("ADDR_DT").toString()));    // (100)    납세자상세주소
		    sndForm.setMap("MO_TEL"            ,   CbUtil.checkNull(taxMap.getMap("PHONE_NO").toString()));    // (16)     납세자핸드폰번호
		    sndForm.setMap("TEL"               ,   CbUtil.checkNull(taxMap.getMap("TEL_NO").toString()));    // (16)     납세자전화번호
		    sndForm.setMap("BIZ_NO"            ,   CbUtil.checkNull(taxMap.getMap("SAUP_NO").toString()));    // (10)     사업자번호   -  개인사업자,법인인경우 필수
		    sndForm.setMap("CMP_NM"            ,   CbUtil.checkNull(taxMap.getMap("CMP_NM").toString()));    // (100)    상호   -  개인사업자,법인인경우 필수
		    sndForm.setMap("BIZ_ZIP_NO"        ,   CbUtil.checkNull(taxMap.getMap("ZIP_CD").toString())); 
		    sndForm.setMap("BIZ_ZIP_ADDR"      ,   CbUtil.checkNull(taxMap.getMap("ADDR").toString())); 
		    sndForm.setMap("BIZ_ADDR"          ,   " ");
		        
		    sndForm.setMap("RVSN_YYMM"         ,   CbUtil.checkNull(taxMap.getMap("RVSN_YY").toString())+CbUtil.checkNull(taxMap.getMap("RVSN_MM").toString()));    // (6)    귀속년월
		    sndForm.setMap("SAL_DT"            ,   CbUtil.checkNull(taxMap.getMap("SUP_DT").toString()));    // (8)    급여지급일자
		    sndForm.setMap("DUE_DT"            ,   CbUtil.checkNull(taxMap.getMap("DUE_DT").toString()));    // (8)    납기일자
		    sndForm.setMap("F_DUE_DT"          ,   CbUtil.checkNull(taxMap.getMap("F_DUE_DT").toString()));    // (8)    당초납기   -  12월 8일 추가
		    sndForm.setMap("EMP_CNT"           ,   CbUtil.checkNull(taxMap.getMap("GSJS").toString()));    // (6)    종업원수(과세대상)
		    sndForm.setMap("TXE_EMP_CNT"       ,   "0");    // (6)    비과세대상종업원수   -  신규추가
		        
		    //log.info("sal dd");
		      
		    //log.info("sal :: " + Integer.parseInt(taxMap.getMap("GSGY").toString())+Integer.parseInt(taxMap.getMap("BGGY").toString()));
		        
		    sndForm.setMap("SAL"               ,   Integer.parseInt(taxMap.getMap("GSGY").toString())+Integer.parseInt(taxMap.getMap("BGGY").toString()));    // (14)   총급여액
		        
		    //log.info("sal :: " + Integer.parseInt(taxMap.getMap("GSGY").toString())+Integer.parseInt(taxMap.getMap("BGGY").toString()));
		        
		    sndForm.setMap("TXE_SAL"           ,   CbUtil.checkNull(taxMap.getMap("BGGY").toString()));    // (14)   비과세대상 총급여액
		    sndForm.setMap("TAX_STD"           ,   CbUtil.checkNull(taxMap.getMap("GSGY").toString()));    // (14)   과세표준액   -  총급여액 - 비과세대상 총급여액
		    sndForm.setMap("RDX_C0D"           ,   "");    // (10)   (중과)감면코드   -  행자부표준코드(중과감면코드)
		    sndForm.setMap("RDX_R"             ,   "");    // (100)  감면사유   -  size 변경
		    sndForm.setMap("RDX_AMT"           ,   "0");    // (14)   비과세세액   -  감면세액
		    sndForm.setMap("R_P_AMT"           ,   CbUtil.checkNull(taxMap.getMap("SGEK").toString()));    // (14)   신고납부세액   -  과세표준액 * (0.5/100)
		    sndForm.setMap("TAX_RT"            ,   "0.005");    // (10.4) 세율   -  0.005
		    sndForm.setMap("ADTX_YN"           ,   "2");    // (1)    가산세(2=무,1=유)   -  납부지연일자가 0 보다 크면 1, 0 이면 2
		    sndForm.setMap("DLQ_CNT"           ,   "0");    // (4)    납부지연일수   -  현재일자 - 당초납기일자, 휴일은 일수에서 제외
		    sndForm.setMap("RADTX"             ,   "0");    // (14)   신고불성실가산세액
		    sndForm.setMap("PADTX"             ,   "0");    // (14)   납부불성실가산세액
		    sndForm.setMap("TOT_ADTX"          ,   "0");    // (14)   가산세액계   -  신고불성실가산세액 + 납부불성실가산세액
		    sndForm.setMap("TOT_AMT"           ,   CbUtil.checkNull(taxMap.getMap("SUM_RCP").toString()));    // (14)   총납부금액   -  신고납부세액 + 가산세액계
		    sndForm.setMap("RPT_REG_NO"        ,   CbUtil.checkNull(taxMap.getMap("REG_NO").toString()));    // (13)   신고자 주민/법인번호
		    sndForm.setMap("RPT_NM"            ,   CbUtil.checkNull(taxMap.getMap("DPNM").toString()));    // (80)   신고자 성명/법인명
		    sndForm.setMap("RPT_TEL"           ,   CbUtil.checkNull(taxMap.getMap("PHONE_NO").toString()));    // (16)   신고자 전화번호   -  핸드폰 또는 전화번호
		    sndForm.setMap("RPT_ID"            ,   "ETAX9");    // (20)   신고자ID
		    sndForm.setMap("RPT_ADMIN"         ,   "");    // (60)   신고자 기관/부서명
		    sndForm.setMap("RPT_SYSTEM"        ,   "ETAX");    // (5)    신고 시스템 구분   -  WETAX : WETAX, ETAX: ETAX
		    sndForm.setMap("SGG_COD2"          ,   "");    // (3)    시군구코드(서울시 용)   -  서울시 연계시 실제 시도/시군구코드를 필수입력
		        
		    sndForm.setMap("SINGO_DIV"         ,   "0");    // (1)    신고구분   -  최초신고:1 , 수정신고:2
		    sndForm.setMap("B_TAX_NO"          ,   "");    // (31)   최초신고분 과세번호   -  수정신고시필수
		    sndForm.setMap("B_EPAY_NO"         ,   "");    // (19)   최초신고분 전자납부번호   -  수정신고시필수
		    sndForm.setMap("CHG_MEMO"          ,   "");    // (2)    수정신고내용   -  수정신고시필수
																										
		    sndForm.setMap("CHG_REASON"        ,   "");    // (2)    수정신고사유   -  수정신고시필수
																												                                                    
		    sndForm.setMap("REASON_DT"         ,   "");    // (8)    사유발생일   -  수정신고시필수
		    sndForm.setMap("ETC_MEMO"          ,   "");    // (200)  비고   -  수정신고시 사용
		    sndForm.setMap("SUNAP_DT"          ,   "");    // (8)    수납일자   -  수정신고시 사용
		    sndForm.setMap("EVI_DOC1"          ,   "");    // (100)  서류명   -  <신규>
		    sndForm.setMap("EVI_DOC_URL1"      ,   "");    // (100)  서류조회URL   -  <신규>
		    sndForm.setMap("EVI_DOC2"          ,   "");    // (100)  서류명2   -  <신규>
		    sndForm.setMap("EVI_DOC_URL2"      ,   "");    // (100)  서류조회URL2   -  <신규>
		    sndForm.setMap("EVI_DOC3"          ,   "");    // (100)  서류명2   -  <신규>
		    sndForm.setMap("EVI_DOC_URL3"      ,   "");    // (100)  서류조회URL2   -  <신규>
		       
		    /*201301추가분*/
		    sndForm.setMap("BFE_EMP_CNT"       ,   CbUtil.checkNull(taxMap.getMap("GSJS").toString()));    // (6)  직전사업연도 월 평균종업원수   -  <신규>
		        
		    //int mon_sal = ((Integer.parseInt(taxMap.getMap("GSGY").toString()) / Integer.parseInt(taxMap.getMap("GSJS").toString()))/10)*10;
		    //log.debug("mon_sal :: " + mon_sal);
		    sndForm.setMap("MON_SAL"           ,   "0");    // (14)  월 적용급여액   -  <신규>
		    sndForm.setMap("RDX_SAL"           ,   "0");    // (14)  공제액  -  <신규>   
		    //신고불성실가산세가 없다 ?    
		    sndForm.setMap("C_TAXADD_R"        ,   "");    // (CHAR2)  신고불성실가산세구분   -  <2013신규>
		    sndForm.setMap("C_TAXADD_GUBUN"    ,   "");    // (CHAR2)  부정신고사유   -  <2013신규>
		        
		    sndForm.setMap("RADTX_RT"          ,   "");    // (NUMBER5.4)  신고불성실가산적용세율   -  <2013신규>
		    //부정신고일때만 과세대상 급여액값 넘겨줌
		    sndForm.setMap("F_SINGO_STD"       ,   "");    // (NUMBER14)  부정신고적용과표   -  <2013신규>
		      
		    sndForm.setMap("RDX_EMP_CNT"       ,   "0");    // (NUMBER7.1)  공제인원수(신설법인인경우만 적용 )  -  <2013신규>
		    sndForm.setMap("RDX_COD_GBN"       ,   "");    // (CHAR 2)  공제사유구분코드  -  <2013.3.8신규>
			       
			//log.debug(sndForm);
   
			try {
			    	    
			    // 지방세 양도소득분 신고 송수신 서비스 호출
			    Tax011Service  soapservice = new  Tax011Service();
			    	
			    //log.debug("****sndForm*********"+sndForm+"******************* ");
			    	
			    MapForm recvForm = null;
			    	
			    try{
			    	recvForm = soapservice.sndService(sndForm);
			    }catch(Exception e){
			    	e.printStackTrace();
			    	//RE_CO = "1000";
			    	cmMap.setMap("RESP_CODE" , "1000");     /*응답코드*/
		    		cmMap.setMap("TX_GUBUN"  , "160");
		    		return DFL_011.makeSendBuffer(cmMap, taxMap);
			    }
			    	
			    //log.debug("****recvForm*********"+recvForm+"******************* ");
			    	
			    taxMap.putAll(recvForm);
			    	
			    if(taxMap.getMap("MESSAGE").equals("SVR01")){		
			    		
			    	if(taxMap.getMap("EPAY_NO").equals("") || taxMap.getMap("EPAY_NO").toString() == null){
	    	    		//납부금액이 소액일 경우 전자납부번호가 빈값으로 넙어온다.--에러처리
			    		cmMap.setMap("RESP_CODE" , "3010");     /*응답코드 */
			    		cmMap.setMap("TX_GUBUN"  , "160");
			    		return DFL_011.makeSendBuffer(cmMap, taxMap);
	    	    	}
			    	
			    	taxMap.setMap("BUCD", bucd);	
			    	taxMap.setMap("YYMD", sndForm.getMap("TAX_DT").toString());
			    	taxMap.setMap("REQ_SNMH", taxMap.getMap("DPNM").toString());
			    	taxMap.setMap("REQ_NM", taxMap.getMap("DPNM").toString());
			    	taxMap.setMap("F_DUE_DT", taxMap.getMap("DUE_DT").toString());	
			    		
			    	log.debug("************ 주민세 종업원분 신고 등록하기 *********");
			    		
			    	log.debug("**** taxMap *" + taxMap + "******************* ");

			    }else{

			    	log.debug("************ 웹서비스 에러 *********");
			    	cmMap.setMap("RESP_CODE" , "9000");     /*응답코드*/
		    		cmMap.setMap("TX_GUBUN"  , "160");
		    		return DFL_011.makeSendBuffer(cmMap, taxMap);
			    }    
			    	
			}catch(Exception e){
			    	
			    log.debug("주민세 종업원분 신고 전문 송수신오류 ");
			    cmMap.setMap("RESP_CODE" , "9000");     /*응답코드*/
	    		cmMap.setMap("TX_GUBUN"  , "160");
	    		return DFL_011.makeSendBuffer(cmMap, taxMap);
			   
			}finally{
			    //clear();
			}
				
			/******************************************************
		    * 표준 사업단 자료 연계 처리  END
			******************************************************/
			    
			// 받아온 전문 저장
			taxMap.setMap("STATE", state);
			            
			try{
			    sqlService_cyber.queryForInsert("TXDM2610.insertBaroPayData", taxMap);
			}catch(Exception e){
			    e.printStackTrace();
			    log.debug("연계테이블 등록 오류");
			    cmMap.setMap("RESP_CODE" , "8000");     /*응답코드*/
	    		cmMap.setMap("TX_GUBUN"  , "160");
	    		return DFL_011.makeSendBuffer(cmMap, taxMap);
			}
			    
		}catch(Exception e){		
			e.printStackTrace();
			cmMap.setMap("RESP_CODE" , "1000");     /*응답코드*/
			cmMap.setMap("TX_GUBUN"  , "160");
			return DFL_011.makeSendBuffer(cmMap, taxMap);
		}
		    
		// 리턴 값 정리
		cmMap.setMap("RESP_CODE" , "0000");     /*응답코드*/
		cmMap.setMap("TX_GUBUN"  , "160");
		return DFL_011.makeSendBuffer(cmMap, taxMap);
	}
	    
	    
	
	    
	    
	// 수납전문을 받아서 전자신고 데이터를 수납대기로 변경처리
	private byte[] tax004SunapProcess(MapForm cmMap, MapForm taxMap, MapForm sendMap) {
			
	    MapForm singoData = new MapForm();
	    	
	    try{		  		
	    	int row_cnt = sqlService_cyber.getOneFieldInteger("TXDM2610.getSingoDataCheckCount", taxMap);	
	    	if(row_cnt == 1){		
	    		singoData = sqlService_cyber.queryForMap("TXDM2610.getSingoData", taxMap);
	    	}else{
	    		//전자신고 전문 
	    		//RE_CO = "5020";   //고지자료가 없습니다.
	    		cmMap.setMap("RESP_CODE" , "5020");     /*응답코드*/
	    		cmMap.setMap("TX_GUBUN"  , "160");
	    		return DFL_004.makeSendBuffer(cmMap, sendMap);
	    	}
	    			
	    	long singoSum = Long.parseLong(singoData.getMap("SUM_RCP").toString());
	    	long sumrcp = Long.parseLong(taxMap.getMap("SUM_RCP").toString());
	    		
	    	if(singoSum != sumrcp){
	    		//RE_CO = "3010";  // 총급액이 틀립니다.
	    		cmMap.setMap("RESP_CODE" , "3010");
	    		cmMap.setMap("TX_GUBUN"  , "160");
	    		return DFL_004.makeSendBuffer(cmMap, sendMap);
	    	}
	    		
	    	if(!singoData.getMap("TAX_NO").equals(taxMap.getMap("TAX_NO"))){
	    		//RE_CO = "3010";  // 납부번호가 틀립니다.
	    		cmMap.setMap("RESP_CODE" , "5020");
	    		cmMap.setMap("TX_GUBUN"  , "160");
	    		return DFL_004.makeSendBuffer(cmMap, sendMap);
	    	}
	    		
	    	if(!singoData.getMap("EPAY_NO").equals(taxMap.getMap("EPAY_NO"))){
	    		//RE_CO = "3010";  // 전자납부번호가 틀립니다.
	    		cmMap.setMap("RESP_CODE" , "5020");
	    		cmMap.setMap("TX_GUBUN"  , "160");
	    		return DFL_004.makeSendBuffer(cmMap, sendMap);
	    	}
	    		
	    	if(!singoData.getMap("REG_NO").equals(taxMap.getMap("REG_NO"))){
	    		//RE_CO = "3010";  // 납세자번호가 틀립니다.
	    		cmMap.setMap("RESP_CODE" , "5020");
	    		cmMap.setMap("TX_GUBUN"  , "160");
	    		return DFL_004.makeSendBuffer(cmMap, sendMap);
	    	}
	    		
	    	String TAX_DT = taxMap.getMap("TAX_YY").toString()+taxMap.getMap("TAX_MM").toString()+taxMap.getMap("TAX_DD").toString(); 
	    	if(!singoData.getMap("TAX_DT").equals(TAX_DT)){
	    		//RE_CO = "3010";  // 년월일가 틀립니다.
	    		cmMap.setMap("RESP_CODE" , "5020");
	    		cmMap.setMap("TX_GUBUN"  , "160");
	    		return DFL_004.makeSendBuffer(cmMap, sendMap);
	    	}
	    			
	    	int updateCount = 0;
	    	try{
	    		updateCount = sqlService_cyber.queryForUpdate("TXDM2610.updateSinGoDataForSunap", taxMap);
	    	}catch(Exception e){
	    		e.printStackTrace();
	    		cmMap.setMap("RESP_CODE" , "8000");     /*응답코드*/
	    		cmMap.setMap("TX_GUBUN"  , "160");
	    		return DFL_004.makeSendBuffer(cmMap, sendMap);
	    	}
	    		
	    	if(updateCount == 0){
	    		//RE_CO = "8000";  //디비에러
	    		cmMap.setMap("RESP_CODE" , "8000");     /*응답코드*/
	    		cmMap.setMap("TX_GUBUN"  , "160");
	    		return DFL_004.makeSendBuffer(cmMap, sendMap);
	    	}
	    			
	    }catch(Exception e){
	    	e.printStackTrace();
	    	cmMap.setMap("RESP_CODE" , "9000");     /*응답코드*/
			cmMap.setMap("TX_GUBUN"  , "160");
			return DFL_004.makeSendBuffer(cmMap, sendMap);
	    }
	    	
	    // 리턴 값 정리
		cmMap.setMap("RESP_CODE" , "0000");     /*응답코드*/
		cmMap.setMap("TX_GUBUN"  , "160");
		    		
		//log.debug("cmMap " + cmMap);
		//log.debug("     ");
		//log.debug("  sendMap   " + sendMap);
		    	    
		return DFL_004.makeSendBuffer(cmMap, sendMap);
	}
	        
	// 수납전문을 받아서 전자신고 데이터를 수납대기로 변경처리
	private byte[] tax005SunapProcess(MapForm cmMap, MapForm taxMap, MapForm sendMap) {
			
	    MapForm singoData = new MapForm();
	    	
	    try{	  		
	    	int row_cnt = sqlService_cyber.getOneFieldInteger("TXDM2610.getSingoDataCheckCount", taxMap);
	    		
	    	if(row_cnt == 1){
		
	    		singoData = sqlService_cyber.queryForMap("TXDM2610.getSingoData", taxMap);
	    		
	    	}else{
	    		//전자신고 전문 
	    		//RE_CO = "5020";   //고지자료가 없습니다.
	    		cmMap.setMap("RESP_CODE" , "5020");     /*응답코드*/
	    		cmMap.setMap("TX_GUBUN"  , "160");
	    		return DFL_005.makeSendBuffer(cmMap, sendMap);
	    	}
	    		
	    		
	    	long singoSum = Long.parseLong(singoData.getMap("SUM_RCP").toString());
	    	long sumrcp = Long.parseLong(taxMap.getMap("SUM_RCP").toString());
	    		
	    	if(singoSum != sumrcp){
	    		//RE_CO = "3010";  // 총급액이 틀립니다.
	    		cmMap.setMap("RESP_CODE" , "3010");
	    		cmMap.setMap("TX_GUBUN"  , "160");
	    		return DFL_005.makeSendBuffer(cmMap, sendMap);
	    	}
	    		
	    	if(!singoData.getMap("TAX_NO").equals(taxMap.getMap("TAX_NO"))){
	    		//RE_CO = "3010";  // 납부번호가 틀립니다.
	    		cmMap.setMap("RESP_CODE" , "5020");
	    		cmMap.setMap("TX_GUBUN"  , "160");
	    		return DFL_005.makeSendBuffer(cmMap, sendMap);
	    	}
	    		
	    	if(!singoData.getMap("EPAY_NO").equals(taxMap.getMap("EPAY_NO"))){
	    		//RE_CO = "3010";  // 전자납부번호가 틀립니다.
	    		cmMap.setMap("RESP_CODE" , "5020");
	    		cmMap.setMap("TX_GUBUN"  , "160");
	    		return DFL_005.makeSendBuffer(cmMap, sendMap);
	    	}
	    		
	    	if(!singoData.getMap("REG_NO").equals(taxMap.getMap("REG_NO"))){
	    		//RE_CO = "3010";  // 주민번호가 틀립니다.
	    		cmMap.setMap("RESP_CODE" , "5020");
	    		cmMap.setMap("TX_GUBUN"  , "160");
	    		return DFL_005.makeSendBuffer(cmMap, sendMap);
	    	}
	    		
	    	String TAX_DT = taxMap.getMap("TAX_YY").toString()+taxMap.getMap("TAX_MM").toString()+taxMap.getMap("TAX_DD").toString(); 
	    	if(!singoData.getMap("TAX_DT").equals(TAX_DT)){
	    		//RE_CO = "3010";  // 년월일가 틀립니다.
	    		cmMap.setMap("RESP_CODE" , "5020");
	    		cmMap.setMap("TX_GUBUN"  , "160");
	    		return DFL_005.makeSendBuffer(cmMap, sendMap);
	    	}
	    			
	    	int updateCount = 0;
	    	try{
	    		updateCount = sqlService_cyber.queryForUpdate("TXDM2610.updateSinGoDataForSunap", taxMap);
	    	}catch(Exception e){
	    		e.printStackTrace();
	    	}
	    		
	    	if(updateCount == 0){
	    		//RE_CO = "8000";  //디비에러
	    		cmMap.setMap("RESP_CODE" , "8000");     /*응답코드*/
	    		cmMap.setMap("TX_GUBUN"  , "160");
	    		return DFL_005.makeSendBuffer(cmMap, sendMap);
	    	}
	    			
	    }catch(Exception e){
	    	e.printStackTrace();
	    	cmMap.setMap("RESP_CODE" , "9000");     /*응답코드*/
    		cmMap.setMap("TX_GUBUN"  , "160");
    		return DFL_005.makeSendBuffer(cmMap, sendMap);
	    }
	    	
	    // 리턴 값 정리
		cmMap.setMap("RESP_CODE" , "0000");     /*응답코드*/
		cmMap.setMap("TX_GUBUN"  , "160");
		return DFL_005.makeSendBuffer(cmMap, sendMap);
	}
	
	
	// 수납전문을 받아서 전자신고 데이터를 수납대기로 변경처리
	private byte[] tax008SunapProcess(MapForm cmMap, MapForm taxMap, MapForm sendMap) {
				
		MapForm singoData = new MapForm();
		    	
		try{	  		
		    	
			int row_cnt = sqlService_cyber.getOneFieldInteger("TXDM2610.getSingoDataCheckCount", taxMap);
		    		
		    if(row_cnt == 1){
		    		
		    	singoData = sqlService_cyber.queryForMap("TXDM2610.getSingoData", taxMap);
		    		
		    }else{
		    	//전자신고 전문 
		    	//RE_CO = "5020";   //고지자료가 없습니다.
		    	cmMap.setMap("RESP_CODE" , "5020");     /*응답코드*/
		    	cmMap.setMap("TX_GUBUN"  , "260");
		    	return DFL_009.makeSendBuffer(cmMap, sendMap);
		    }
		    			
		    long singoSum = Long.parseLong(singoData.getMap("NAPBU_TAX").toString());
		    long sumrcp = Long.parseLong(taxMap.getMap("NAPBU_TAX").toString());
		    		
		    if(singoSum != sumrcp){
		    	//RE_CO = "3010";  // 총급액이 틀립니다.
		    	cmMap.setMap("RESP_CODE" , "3010");
		    	cmMap.setMap("TX_GUBUN"  , "260");
		    	return DFL_008.makeSendBuffer(cmMap, sendMap);
		    }
		    		
		    if(!singoData.getMap("TAX_NO").equals(taxMap.getMap("TAX_NO"))){
		    	//RE_CO = "3010";  // 납부번호가 틀립니다.
		    	cmMap.setMap("RESP_CODE" , "5020");
		    	cmMap.setMap("TX_GUBUN"  , "260");
		    	return DFL_008.makeSendBuffer(cmMap, sendMap);
		    }
		    		
		    if(!singoData.getMap("EPAY_NO").equals(taxMap.getMap("EPAY_NO"))){
		    	//RE_CO = "3010";  // 전자납부번호가 틀립니다.
		    	cmMap.setMap("RESP_CODE" , "5020");
		    	cmMap.setMap("TX_GUBUN"  , "260");
		    	return DFL_008.makeSendBuffer(cmMap, sendMap);
		    }
		    		
		    if(!singoData.getMap("REG_NO").equals(taxMap.getMap("REG_NO"))){
		    	//RE_CO = "3010";  // 주민번호가 틀립니다.
		    	cmMap.setMap("RESP_CODE" , "5020");
		    	cmMap.setMap("TX_GUBUN"  , "260");
		    	return DFL_008.makeSendBuffer(cmMap, sendMap);
		    }
		    		
		    //String TAX_DT = taxMap.getMap("TAX_YY").toString()+taxMap.getMap("TAX_MM").toString()+taxMap.getMap("TAX_DD").toString(); 
		    //if(!singoData.getMap("TAX_DT").equals(TAX_DT)){
		    	//RE_CO = "3010";  // 년월일가 틀립니다.
		    //	cmMap.setMap("RESP_CODE" , "5020");
		    //	cmMap.setMap("TX_GUBUN"  , "260");
		    //	return DFL_008.makeSendBuffer(cmMap, sendMap);
		    //}
		    		    			
		    try{
		    	sqlService_cyber.queryForUpdate("TXDM2610.updateSinGoDataForSunap", taxMap);
		    }catch(Exception e){
		    	e.printStackTrace();
		    	//RE_CO = "8000";
		    	// 리턴 값 정리
		    	cmMap.setMap("RESP_CODE" , "8000");     /*응답코드*/
		    	cmMap.setMap("TX_GUBUN"  , "260");
		    	return DFL_008.makeSendBuffer(cmMap, sendMap);
		    }
		    		
		    			
	
		}catch(Exception e){
		    e.printStackTrace();
		    // 리턴 값 정리
			cmMap.setMap("RESP_CODE" , "9000");     /*응답코드*/
			cmMap.setMap("TX_GUBUN"  , "260");
			return DFL_008.makeSendBuffer(cmMap, sendMap);
	
		}
		    	
		// 리턴 값 정리
		cmMap.setMap("RESP_CODE" , "0000");     /*응답코드*/
		cmMap.setMap("TX_GUBUN"  , "260");
		return DFL_008.makeSendBuffer(cmMap, sendMap);
	}
	    
	    
	// 수납전문을 받아서 전자신고 데이터를 수납대기로 변경처리
	private byte[] tax009SunapProcess(MapForm cmMap, MapForm taxMap, MapForm sendMap) {
			
	    MapForm singoData = new MapForm();
	    	
	    try{	  		
	    	int row_cnt = sqlService_cyber.getOneFieldInteger("TXDM2610.getSingoDataCheckCount", taxMap);
	    		
	    	if(row_cnt == 1){
	    		
	    		singoData = sqlService_cyber.queryForMap("TXDM2610.getSingoData", taxMap);
	    		
	    	}else{
	    		//전자신고 전문 
	    		//RE_CO = "5020";   //고지자료가 없습니다.
	    		cmMap.setMap("RESP_CODE" , "5020");     /*응답코드*/
	    		cmMap.setMap("TX_GUBUN"  , "160");
	    		return DFL_009.makeSendBuffer(cmMap, sendMap);
	    	}
	    			
	    	long singoSum = Long.parseLong(singoData.getMap("SUM_RCP").toString());
	    	long sumrcp = Long.parseLong(taxMap.getMap("SUM_RCP").toString());
	    		
	    	if(singoSum != sumrcp){
	    		//RE_CO = "3010";  // 총급액이 틀립니다.
	    		cmMap.setMap("RESP_CODE" , "3010");
	    		cmMap.setMap("TX_GUBUN"  , "160");
	    		return DFL_009.makeSendBuffer(cmMap, sendMap);
	    	}
	    		
	    	if(!singoData.getMap("TAX_NO").equals(taxMap.getMap("TAX_NO"))){
	    		//RE_CO = "3010";  // 납부번호가 틀립니다.
	    		cmMap.setMap("RESP_CODE" , "5020");
	    		cmMap.setMap("TX_GUBUN"  , "160");
	    		return DFL_009.makeSendBuffer(cmMap, sendMap);
	    	}
	    		
	    	if(!singoData.getMap("EPAY_NO").equals(taxMap.getMap("EPAY_NO"))){
	    		//RE_CO = "3010";  // 전자납부번호가 틀립니다.
	    		cmMap.setMap("RESP_CODE" , "5020");
	    		cmMap.setMap("TX_GUBUN"  , "160");
	    		return DFL_009.makeSendBuffer(cmMap, sendMap);
	    	}
	    		
	    	if(!singoData.getMap("REG_NO").equals(taxMap.getMap("REG_NO"))){
	    		//RE_CO = "3010";  // 주민번호가 틀립니다.
	    		cmMap.setMap("RESP_CODE" , "5020");
	    		cmMap.setMap("TX_GUBUN"  , "160");
	    		return DFL_009.makeSendBuffer(cmMap, sendMap);
	    	}
	    		
	    	String TAX_DT = taxMap.getMap("TAX_YY").toString()+taxMap.getMap("TAX_MM").toString()+taxMap.getMap("TAX_DD").toString(); 
	    	if(!singoData.getMap("TAX_DT").equals(TAX_DT)){
	    		//RE_CO = "3010";  // 년월일가 틀립니다.
	    		cmMap.setMap("RESP_CODE" , "5020");
	    		cmMap.setMap("TX_GUBUN"  , "160");
	    		return DFL_009.makeSendBuffer(cmMap, sendMap);
	    	}
	    		    			
	    	try{
	    		sqlService_cyber.queryForUpdate("TXDM2610.updateSinGoDataForSunap", taxMap);
	    	}catch(Exception e){
	    		e.printStackTrace();
	    		//RE_CO = "8000";
	    		// 리턴 값 정리
	    		cmMap.setMap("RESP_CODE" , "8000");     /*응답코드*/
	    		cmMap.setMap("TX_GUBUN"  , "160");
	    		return DFL_009.makeSendBuffer(cmMap, sendMap);
	    	}
	    		
	    			
	    }catch(Exception e){
	    	e.printStackTrace();
	    	// 리턴 값 정리
			cmMap.setMap("RESP_CODE" , "9000");     /*응답코드*/
			cmMap.setMap("TX_GUBUN"  , "160");
			return DFL_009.makeSendBuffer(cmMap, sendMap);
	    }
	    	
	    // 리턴 값 정리
		cmMap.setMap("RESP_CODE" , "0000");     /*응답코드*/
		cmMap.setMap("TX_GUBUN"  , "160");
		return DFL_009.makeSendBuffer(cmMap, sendMap);
	}
	    
	    
	// 수납전문을 받아서 전자신고 데이터를 수납대기로 변경처리
	private byte[] tax011SunapProcess(MapForm cmMap, MapForm taxMap, MapForm sendMap) {
			
	    MapForm singoData = new MapForm();
	    	
	    try{	  		
	    	int row_cnt = sqlService_cyber.getOneFieldInteger("TXDM2610.getSingoDataCheckCount", taxMap);
	    		
	    	if(row_cnt == 1){
	    			
	    		singoData = sqlService_cyber.queryForMap("TXDM2610.getSingoData", taxMap);
	    		
	    	}else{
	    		//전자신고 전문 
	    		//RE_CO = "5020";   //고지자료가 없습니다.
	    		cmMap.setMap("RESP_CODE" , "5020");     /*응답코드*/
	    		cmMap.setMap("TX_GUBUN"  , "160");
	    		return DFL_011.makeSendBuffer(cmMap, sendMap);
	    	}
	    		
	    	long singoSum = Long.parseLong(singoData.getMap("SUM_RCP").toString());
	    	long sumrcp = Long.parseLong(taxMap.getMap("SUM_RCP").toString());
	    		
	    	if(singoSum != sumrcp){
	    		//RE_CO = "3010";  // 총급액이 틀립니다.
	    		cmMap.setMap("RESP_CODE" , "3010");
	    		cmMap.setMap("TX_GUBUN"  , "160");
	    		return DFL_011.makeSendBuffer(cmMap, sendMap);
	    	}
	    		
	    	if(!singoData.getMap("TAX_NO").equals(taxMap.getMap("TAX_NO"))){
	    		//RE_CO = "3010";  // 납부번호 틀립니다.
	    		cmMap.setMap("RESP_CODE" , "5020");
	    		cmMap.setMap("TX_GUBUN"  , "160");
	    		return DFL_011.makeSendBuffer(cmMap, sendMap);
	    	}
	    		
	    	if(!singoData.getMap("EPAY_NO").equals(taxMap.getMap("EPAY_NO"))){
	    		//RE_CO = "3010";  // 전자납부번호가 틀립니다.
	    		cmMap.setMap("RESP_CODE" , "5020");
	    		cmMap.setMap("TX_GUBUN"  , "160");
	    		return DFL_011.makeSendBuffer(cmMap, sendMap);
	    	}
	    		
	    	if(!singoData.getMap("REG_NO").equals(taxMap.getMap("REG_NO"))){
	    		//RE_CO = "3010";  // 주민번호가 틀립니다.
	    		cmMap.setMap("RESP_CODE" , "5020");
	    		cmMap.setMap("TX_GUBUN"  , "160");
	    		return DFL_011.makeSendBuffer(cmMap, sendMap);
	    	}
	    		
	    	String TAX_DT = taxMap.getMap("TAX_YY").toString()+taxMap.getMap("TAX_MM").toString()+taxMap.getMap("TAX_DD").toString(); 
	    	if(!singoData.getMap("TAX_DT").equals(TAX_DT)){
	    		//RE_CO = "3010";  // 년월일이 틀립니다.
	    		cmMap.setMap("RESP_CODE" , "5020");
	    		cmMap.setMap("TX_GUBUN"  , "160");
	    		return DFL_011.makeSendBuffer(cmMap, sendMap);
	    	}
	      		
	    	try{
	    		sqlService_cyber.queryForUpdate("TXDM2610.updateSinGoDataForSunap", taxMap);
	    	}catch(Exception e){
	    		e.printStackTrace();	
	    		//RE_CO = "8000";
	    		// 리턴 값 정리
	    		cmMap.setMap("RESP_CODE" , "8000");     /*응답코드*/
	    		cmMap.setMap("TX_GUBUN"  , "160");
	    		return DFL_011.makeSendBuffer(cmMap, sendMap);
	    	}
	    		
	    		    	
	    }catch(Exception e){
	    	e.printStackTrace();
	    	// 리턴 값 정리
			cmMap.setMap("RESP_CODE" , "9000");     /*응답코드*/
			cmMap.setMap("TX_GUBUN"  , "160");
			return DFL_011.makeSendBuffer(cmMap, sendMap);
	    }
	    	
	    // 리턴 값 정리
		cmMap.setMap("RESP_CODE" , "0000");     /*응답코드*/
		cmMap.setMap("TX_GUBUN"  , "160");
		return DFL_011.makeSendBuffer(cmMap, sendMap);
	}

	// 법정동코드
	private String getLdongCode(String sgg_cod, String hacd){		    	
		String ldong_cd = null;	
		MapForm mf = new MapForm();
		mf.setMap("SIDO_CD", "26");
		mf.setMap("SGG_CD", sgg_cod);
		mf.setMap("HACD", hacd);   	
		try{    		
		    ldong_cd = sqlService_cyber.getOneFieldString("TXDM2610.getLdongCode", mf);
		}catch(Exception e){
		    log.debug("법정동코드 오류");
		    e.printStackTrace();
		}	    	
		return ldong_cd;
	}
	
	//특별징수 행정동코드 구하기
	private String getHacdCode(String sgg_cod, String bucd) {
		String hacd = null;
		MapForm mf = new MapForm();
		mf.setMap("SIDO_CD", "26");
		mf.setMap("SGG_CD", sgg_cod);
		mf.setMap("BUCD", bucd);
		try{
			hacd = sqlService_cyber.getOneFieldString("TXDM2610.getHacdCode", mf);
		}catch(Exception e){
			log.debug("행정동코드 오류");
			e.printStackTrace();
		}
		return hacd;
	}
		    
	//주소상세 검색 - 사용안함	    
	private String getAddress(String post) {
				
		String address = null;
		    	
		MapForm addr = new MapForm();
		addr.setMap("POST", post);
		    	
		try{    		
		    address = sqlService_cyber.getOneFieldString("TXDM2610.getAddress", addr);
		    //address = CbUtil.strEncod(address, "MS949", "KSC5601");
		}catch(Exception e){
		    log.debug("주소검색 오류");
		}
		    	
		return address;
	}
    
	


}
