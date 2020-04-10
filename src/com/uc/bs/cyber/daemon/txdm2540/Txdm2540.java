/**
 *  주시스템명 : 사이버지방세청 고도화
 *  업  무  명 : 공통
 *  기  능  명 : 위택스 회원정보를 수신하고 DB에 저장한다...
 *               com.uc.bs.cyber.etax.* 자원사용
 *  클래스  ID : Txdm2540 
 *  변경  이력 :
 * ------------------------------------------------------------------------
 *  작성자      	소속  		    일자            Tag           내용
 * ------------------------------------------------------------------------
 *  송동욱        유채널(주)      2011.06.26         %01%         최초작성
 */
package com.uc.bs.cyber.daemon.txdm2540;

import java.nio.channels.SocketChannel;

import gov.mogaha.ilts.com.transport.request.ParamVO;
import gov.mogaha.ilts.sido.transport.response.ResultVO2;
import gov.mogaha.ilts.sido.ConnectionManager2;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.dao.DuplicateKeyException;

import com.uc.bs.cyber.CbUtil;
import com.uc.bs.cyber.daemon.txdm2540.Txdm2540;
import com.uc.bs.cyber.etax.net.RcvServer;
import com.uc.bs.cyber.etax.net.RcvWorker;
import com.uc.core.MapForm;
import com.uc.core.misc.Utils;
import com.uc.core.misc.XMLFileReader;
import com.uc.core.misc.XmlUtils;
import com.uc.core.spring.service.IbatisBaseService;
import com.uc.core.spring.service.UcContextHolder;
import com.uc.egtob.net.FieldList;

/**
 * @author Administrator
 *
 */
public class Txdm2540 extends RcvWorker{

	private IbatisBaseService sqlService_cyber = null;
	private ApplicationContext appContext = null;
	private Thread comdThread = null;
			
	@SuppressWarnings("unused")
	private String dataSource  = null;
	
	private FieldList msgField;
	
	private Txdm2540FieldList TD_2540 = new Txdm2540FieldList();
	
	
	/*
	 * 생성자
	 */
	public Txdm2540() {
		super();
		// TODO Auto-generated constructor stub

		log.info("============================================");
		log.info("== new RcvWorker( )                 Start ==");
		log.info("============================================");
	}
	
	/* 
	 * 생성자 초기화용
	 * */
	public Txdm2540(int annotation) throws Exception {
		// TODO Auto-generated constructor stub
        // nothing...
	}	

	/*
	 * 트랜잭션용(사용안함)
	 * */
	public Txdm2540(ApplicationContext context, String dataSource) throws Exception {
		
		/*Context 주입...*/
		this.context = context;
		this.dataSource = dataSource;
		
	}
	
	/*
	 * 생성자
	 * */
	public Txdm2540(ApplicationContext context) throws Exception {
		
		/*Context 주입...*/
		setAppContext(context);
		
		startServer();

	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		ApplicationContext context  = null;
		
		try {

			Txdm2540 svr = new Txdm2540(0);
			
			/*Log4j 설정*/
			CbUtil.setupLog4jConfig(svr, "log4j.tomcat.xml");
			
			String[] xmls = XMLFileReader.getAllFilePathArray(Utils.getResourcePath(svr, "/"), "SERVICE.XML");

			String strSrc = Utils.getResourcePath(svr, "/") + "config/sqlmaps.xml";

			String strDest = Utils.getResourcePath(svr, "/") + "config/sqlmapConfig.xml";

			XmlUtils.setXmlAttributes(svr, strSrc, strDest, "sqlMapConfig", "sqlMap", "url", xmls);

			context = new ClassPathXmlApplicationContext("config/Tomcat-Spring-*.xml");
		
			svr.setAppContext(context);
			
			//svr.sqlService_cyber = (IbatisBaseService) svr.getAppContext().getBean("baseService_cyber");
			
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
		
		int connPort =  Utils.getResourceInt("ApplicationResource", "wetax.recv_mem.port");  /*9831*/
		
		RcvServer server = new RcvServer(connPort, "com.uc.bs.cyber.daemon.txdm2540.Txdm2540", 10, 2, "log4j.xml", 0, com.uc.core.Constants.TYPENOHEAD);
		
		server.setContext(appContext);
		server.setProcId("2540");
		server.setProcName("위택스 회원정보수신");
		server.setThreadName("thr_2540");
		server.initial();
		
		comdThread = new Thread(server);
		
		/**
		 * 데이터 송수신을 위해서 서버를 등록해준다
		 * (서버관리데몬)을 위해서 Threading...
		 */
		comdThread.setName("2540");
		
		comdThread.start();
		
	}	

	@Override
	public void doDataRecv(SocketChannel socket, byte[] buffer)	throws Exception {
		// TODO Auto-generated method stub
		
		int recvlen = buffer.length;
		
		log.info("============================================");
		log.info("== doDataRecv(" + socket.socket().getInetAddress() + ") Start ==");
		log.info(" SIZE[" + recvlen + "]");
		log.info(" RECV[" + new String(buffer) + "]");
		log.info("============================================");
		
		MapForm dataMap = null;
		MapForm logMap = new MapForm();
		
		
		
		/**
		 * 응답전문 필드 선언
		 */
		this.msgField = new FieldList();
		
		msgField.add("RSLT_COD"  ,   	9,    "C");    // 결과코드
		msgField.add("RSLT_MSG"  ,     50,    "C");    // 결과메시지
		
		if(buffer.length >= 1500) {
			
			try {
				
				TD_2540 = new Txdm2540FieldList();        /*초기화*/        
				
				dataMap = TD_2540.parseBuff(buffer);      /*수신전문 파싱(부분)*/
				
				/* 송수신 전문 저장용*/
				logMap.setMap("SYS_DSC"  , "03"); /*위택스*/
				logMap.setMap("MSG_ID"   , "Txdm2540"); /*전문종별*/
				logMap.setMap("RCV_MSG"  , new String(buffer)); /*수신전문*/
				logMap.setMap("REG_DTM"  , CbUtil.getCurrent("yyyyMMddHHmmss"));
				
				if (log.isDebugEnabled()) {
					log.debug("dataMap = " + dataMap.getMaps());
				}
				
                //usr_div값이 정의되지 않은 값('90')이 들어와서 실명인증 부분에서 NullPointerException 발생해서 수정.
				//'90'은 예전에 구분값으로 존재한 값으로 수정하지 않는 한 현재도 보내질 수 있다고 하여 처리 없이 되돌려 보내는 응답 처리함.
				//2013.12.05 by 임창섭(위텍스 박종표 과장과 통화후)
                String[] usrDiv = new String[2];
                
				if (dataMap.getMap("usr_div").equals("01")) { //내국인

					TD_2540.Txdm2540_Pri_FieldList();
					
					dataMap = TD_2540.parseBuff(buffer); /*다시한번다 파생해서 법인명을 셋팅한다.*/
					
					dataMap.setMap("gbn"  , "Kor");
					dataMap.setMap("sname", dataMap.getMap("usr_nm"));
					dataMap.setMap("plgn" , "1");
					
					usrDiv[0] = "24140-000";
					usrDiv[1] = "정상";
					
				}else if (dataMap.getMap("usr_div").equals("02")) { //외국인
					
					TD_2540.Txdm2540_Pri_FieldList();
					
					dataMap = TD_2540.parseBuff(buffer); /*다시한번다 파생해서 법인명을 셋팅한다.*/
					
					dataMap.setMap("gbn"  , "Frn");
					dataMap.setMap("sname", dataMap.getMap("usr_nm"));
					dataMap.setMap("plgn" , "1");
					
					usrDiv[0] = "24140-000";
					usrDiv[1] = "정상";

				}else if (dataMap.getMap("usr_div").equals("21")) { //법인
					
					if(recvlen == 1732) {
						TD_2540.Txdm2540_Crp_FieldList();
					}else{
						TD_2540.Txdm2540_Crp_short_FieldList();
					}

					dataMap = TD_2540.parseBuff(buffer); /*다시한번다 파생해서 법인명을 셋팅한다.*/
					
					dataMap.setMap("gbn"  , "Crp");
					dataMap.setMap("sname", dataMap.getMap("comp_nm"));
					dataMap.setMap("plgn" , "2");
					
					usrDiv[0] = "24140-000";
					usrDiv[1] = "정상";

				} else {
				    dataMap = new MapForm();
                    dataMap.setMap("RSLT_COD", "24130-301");
                    dataMap.setMap("RSLT_MSG", "사용자 구분입력값이 정의되지 않은 값입니다.");
                    
                    usrDiv[0] = "24130-301";
                    usrDiv[1] = "사용자 구분입력값이 정의되지 않은 값입니다.";
				}
				
				if(usrDiv[0].equals("24140-000")){
    				/**
    				 * 회원정보 실명인증 서비스 
    				 */
    				ParamVO param = new ParamVO();
    				
    				param.put("REQ_TYPE", dataMap.getMap("gbn"));     //국가
    				param.put("CHK_NAME", dataMap.getMap("sname"));   //이름/법인
    				param.put("CHK_NO"  , dataMap.getMap("tpr_no"));  //주민번호/법인번호
				
				    //String[] weTaxChk = toWetaxSend(param);   //adaptor 를 통해서 위택스로 전송
				
                    //DEBUGGING
                    String[] weTaxChk = {"24140-000", "정상"};
    				
    				if(weTaxChk[0].equals("24140-000")){
    					
    					try{
    						if(dataMap.getMap("tel_no").equals("") || dataMap.getMap("tel_no").toString().length() < 9) {
    							dataMap.setMap("tel_no", "--");
    						}else{
    							
    							if(dataMap.getMap("tel_no").toString().substring(0,2).equals("02")) {
    								dataMap.setMap("tel_no", dataMap.getMap("tel_no").toString().substring(0, 2) + "-" + 
    								                         dataMap.getMap("tel_no").toString().substring(2, dataMap.getMap("tel_no").toString().length() - 4) + "-" +
    								                         dataMap.getMap("tel_no").toString().substring(dataMap.getMap("tel_no").toString().length() - 4));
    							}else{
    								dataMap.setMap("tel_no", dataMap.getMap("tel_no").toString().substring(0, 3) + "-" + 
    				                                         dataMap.getMap("tel_no").toString().substring(3, dataMap.getMap("tel_no").toString().length() - 4) + "-" +
    				                                         dataMap.getMap("tel_no").toString().substring(dataMap.getMap("tel_no").toString().length() - 4));
    							}
    							
    							
    						}
    						
    
    						if(dataMap.getMap("mo_tel").equals("") || dataMap.getMap("mo_tel").toString().length() < 10) {
    							dataMap.setMap("mo_tel", "--");
    						}else{
    							dataMap.setMap("mo_tel", dataMap.getMap("mo_tel").toString().substring(0, 3) + "-" + 
                                                         dataMap.getMap("mo_tel").toString().substring(3, dataMap.getMap("mo_tel").toString().length() - 4) + "-" +
                                                         dataMap.getMap("mo_tel").toString().substring(dataMap.getMap("mo_tel").toString().length() - 4));
    							
    						}
    						
    						/*등록된 사용자가 없다면*/
    						if (RegisterYn(dataMap.getMap("tpr_no").toString()) == 0) {
    							
    							/*초기값 세팅*/
    							if(!(dataMap.getMap("email_yn").equals("1") || dataMap.getMap("email_yn").equals("0"))) {
    								dataMap.setMap("email_yn", dataMap.getMap("email_yn").equals("Y") ? "1" : "0");
    							}
    						
    							dataMap.setMap("comp_nm", "");
    							dataMap.setMap("tpr_no2", "");
    
    							log.info("----------수신자료----------");
    							log.info("usr_div  = " + dataMap.getMap("usr_div").toString());
    							log.info("usr_nm   = " + dataMap.getMap("usr_nm").toString());
    							log.info("tpr_no   = " + dataMap.getMap("tpr_no").toString());
    							log.info("bz_no    = " + dataMap.getMap("bz_no").toString());
    							log.info("zip_no   = " + dataMap.getMap("zip_no").toString());
    							log.info("addr1    = " + dataMap.getMap("addr1").toString());
    							log.info("addr2    = " + dataMap.getMap("addr2").toString());
    							log.info("sido_cod = " + dataMap.getMap("sido_cod").toString());
    							log.info("sgg_cod  = " + dataMap.getMap("sgg_cod").toString());
    							log.info("email    = " + dataMap.getMap("email").toString());
    							log.info("email_yn = " + dataMap.getMap("email_yn").toString());
    							log.info("tel_no   = " + dataMap.getMap("tel_no").toString());
    							log.info("mo_tel   = " + dataMap.getMap("mo_tel").toString());			
    							log.info("dn       = " + dataMap.getMap("dn").toString());
    							log.info("crp_nm   = " + dataMap.getMap("crp_nm").toString());
    							
    							if (dataMap.getMap("usr_div").equals("21")) { //법인
    								log.info("------------법인------------");
    								log.info("remrk    = " + dataMap.getMap("remrk").toString());
    								log.info("comp_nm  = " + dataMap.getMap("comp_nm").toString());
    								log.info("tpr_no2  = " + dataMap.getMap("tpr_no2").toString());
    							}
    							log.info("----------입력시작----------");
    						
    							if (sqlService_cyber.queryForUpdate("TXDM2540.INSERT_ME1101_TB_FOR_WETAX_MEMBER", dataMap) > 0) {
    								dataMap = new MapForm();
    								dataMap.setMap("RSLT_COD", "24130-000");
    								dataMap.setMap("RSLT_MSG", "회원정보등록성공");
    							} else {
    								dataMap = new MapForm();
    								dataMap.setMap("RSLT_COD", "24130-201");
    								dataMap.setMap("RSLT_MSG", "내부 시스템 DB 장애로 서비스 불가");
    							}
    							
    						} else {
    							
    							if (sqlService_cyber.queryForUpdate("TXDM2540.UPDATE_ME1101_TB_FOR_WETAX_MEMBER", dataMap) > 0) {
    								log.info("위택스 회원정보 수정완료!");
    								
    								dataMap = new MapForm();
    								dataMap.setMap("RSLT_COD", "24130-000");
    								dataMap.setMap("RSLT_MSG", "회원정보등록성공");
    								
    							} else {
    								dataMap = new MapForm();
    								dataMap.setMap("RSLT_COD", "24130-201");
    								dataMap.setMap("RSLT_MSG", "내부 시스템 DB 장애로 서비스 불가");
    								
    								//dataMap = new MapForm();
    								//dataMap.setMap("RSLT_COD", "24130-001");
    								//dataMap.setMap("RSLT_MSG", "기존에 등록된 회원입니다");
    							}
    
    						}
    
    						
    					}catch (Exception e){
    						
    						e.printStackTrace();
    						
    						/*중복이 발생하거나 제약조건에 어긋나는 경우체크*/
    						if (e instanceof DuplicateKeyException){
    							
    							if (sqlService_cyber.queryForUpdate("TXDM2540.UPDATE_ME1101_TB_FOR_WETAX_MEMBER", dataMap) > 0) {
    								log.info("위택스 회원정보 수정완료!");
    							} 
    							
    						}else{
    							throw e;
    						}
    						
    					}
    					
    				} else if(weTaxChk[0].equals("24140-101")){
    					
    					dataMap = new MapForm();
    					dataMap.setMap("RSLT_COD", "24140-101");
    					dataMap.setMap("RSLT_MSG", "실명 인증에 실패했습니다");
    
    				} else if(weTaxChk[0].equals("24140-102")){
    					
    					dataMap = new MapForm();
    					dataMap.setMap("RSLT_COD", "24140-102");
    					dataMap.setMap("RSLT_MSG", "실명 인증서버 이상으로 실명인증 확인이 불가합니다");
    					
    				} else if(weTaxChk[0].equals("24140-301")){
    					
    					dataMap = new MapForm();
    					dataMap.setMap("RSLT_COD", "24140-301");
    					dataMap.setMap("RSLT_MSG", "사용자 구분입력값이 정의되지 않은 값입니다");
    					
    				} else if(weTaxChk[0].equals("24140-302")){
    					
    					dataMap = new MapForm();
    					dataMap.setMap("RSLT_COD", "24140-302");
    					dataMap.setMap("RSLT_MSG", "필수 입력값이 비어있습니다");
    				}
				} else {
                    
                    dataMap = new MapForm();
                    dataMap.setMap("RSLT_COD", "24140-301");
                    dataMap.setMap("RSLT_MSG", "사용자 구분입력값이 정의되지 않은 값입니다");
				}
				
			} catch (Exception e) {
				// 시스템 오류입니다
				e.printStackTrace();
				
				dataMap = new MapForm();
				
				dataMap.setMap("RSLT_COD", "24110-201");
				dataMap.setMap("RSLT_MSG", "내부시스템 오류가 발생했습니다");
			}
			
			
		} else {
			
			dataMap = new MapForm();
			
			dataMap.setMap("RSLT_COD", "24110-102");
			dataMap.setMap("RSLT_MSG", "요청한 회원정보값이 비정상 포맷입니다");
			
		}
		
		this.retBuffer = msgField.makeMessageByte(dataMap)	;
		
		logMap.setMap("RTN_MSG" , new String(retBuffer)); /*응답전문*/
		logMap.setMap("RES_CD"  , ""); /*처리결과*/
		logMap.setMap("ERR_MSG" , ""); /*오류메시지*/
		logMap.setMap("REG_DTM" , CbUtil.getCurrent("yyyyMMddHHmmss"));
		logMap.setMap("LAST_DTM", CbUtil.getCurrent("yyyyMMddHHmmss"));
		
		if(sqlService_cyber.queryForUpdate("CODMBASE.CODM_JUNMUN_TXRX_INSERT", logMap) > 0) {
			log.info("송수신 전문 저장완료!!!");
		}

	}

	@Override
	public void setContext(Object obj) {
		// TODO Auto-generated method stub
		this.setAppContext((ApplicationContext) obj);
	}

	/*--------------------------------------------------------------*/
	/*--------------------property set -----------------------------*/
	/*--------------------------------------------------------------*/
	public void setAppContext(ApplicationContext appContext) {
		this.appContext = appContext;
		
		sqlService_cyber = (IbatisBaseService) appContext.getBean("baseService_cyber");
	}

	public ApplicationContext getAppContext() {
		return appContext;
	}
	
	
	/**
	 * 위택스로 부터 데이터를 전송하는 아답터
	 * @param paramVO
	 * @throws Exception
	 */
	public String[] toWetaxSend(ParamVO paramVO) throws Exception{

		// 초기화 과정에서 다음과 같이 한번만 설정을 해주시면 됩니다.
		System.setProperty("ITLS.DIR", CbUtil.getResourcePath(this, ""));
		
		log.info("ITLS.DIR = " + CbUtil.getResourcePath(this, ""));
		
		// 위택스에서 응답할 데이터가 저장될 변수 생성
		ResultVO2 resultVO = null;
		String sRetCode    = "";
		String sRetMsg     = "";
		String rt_msg[]    = new String[2];
		
		try {

			// 위택스의 ‘이택스 회원정보수신’ 서비스(SIDO.MEMBER01) 호출
			log.info("위택스 서비스(LCIJ.LCIJR04) 호출...");
			resultVO = (ResultVO2) ConnectionManager2.execute("LCIJ.LCIJR04", paramVO);

			// 위택스 서비스 접속 성공/실패 여부 확인
			if (!resultVO.isSuccess()) {
				//위택스 서비스 접속 실패 시 실패 메시지를 출력하는 예시
				System.out.println("메세지 : " + resultVO.getMessage());
				log.error("메세지 : " + resultVO.getMessage());

				rt_msg[0] = "24100-401";
				rt_msg[1] = "위택스 서비스접속 실패";
			}
			
			// 위택스 서비스 응답 정상 여부 확인
			if (resultVO.isOk()) {
				sRetCode = (String)(resultVO.getObject("RSLT_COD"));
				sRetMsg  = (String)(resultVO.getObject("RSLT_MESSAGE"));
				//납부결과 전송이 성공했을 경우 처리 로직 구현

				log.info("위택스 서비스호출(" + sRetCode + ") " + sRetMsg);

				rt_msg[0] = sRetCode;
				rt_msg[1] = sRetMsg;
				
				if(!sRetCode.equals("44130-000")) {

					System.out.println("위택스 서비스호출 오류[" + sRetCode + "] " + sRetMsg);
					log.error("위택스 서비스호출 오류[" + sRetCode + "] " + sRetMsg);
					
					rt_msg[0] = "44130-000";
					rt_msg[1] = "위택스 서비스호출 오류";
					
				}

			}// 위택스 서비스 오류
			else {
				System.out.println("Fail");

				System.out.println("[441030-402] 위택스 서비스 오류");
				log.error("[441030-402] 위택스 서비스 오류");
				
				rt_msg[0] = "441030-402";
				rt_msg[1] = "위택스 서비스 오류";
				
			}
			
		} catch (Exception e) {
			e.printStackTrace();

			log.error("WeTax 서비스 오류 ::: " + e.getMessage());

			rt_msg[0] = "441030-402";
			rt_msg[1] = "위택스 서비스 오류";
			
		}
		
		return rt_msg;
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
    
    /*등록여부확인*/
    private int RegisterYn(String reg_no){
    	return (Integer)sqlService_cyber.queryForBean("TXDM2540.SELECT_MEMBER_INFO", reg_no);
    }
    
    
}
