/**
 *  주시스템명 : 사이버지방세청 고도화
 *  업  무  명 : 공통
 *  기  능  명 : 위택스 통합수납연계서버를 기동하고 송수신데이터를 처리한다...
 *               com.uc.bs.cyber.etax.* 자원사용
 *  클래스  ID : Txdm2520 
 *  변경  이력 :
 * ------------------------------------------------------------------------
 *  작성자         소속              일자            Tag           내용
 * ------------------------------------------------------------------------
 *  송동욱      유채널(주)         2011.04.24         %01%         최초작성
 *  임창섭      유채널(주)         2014.11.20         %02%         간단e납부 관련(세외수입, 환경개선부담금) 분기 추가 및 수정
 *  임창섭      유채널(주)         2014.11.20         %02%         간단e납부 관련(세외수입, 환경개선부담금) 분기 추가 및 수정
 */
package com.uc.bs.cyber.daemon.txdm2520;

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
//간단e납부 2차관련 추가
import com.uc.bs.cyber.service.we202001.We202001Service;
import com.uc.bs.cyber.service.we202002.We202002Service;
import com.uc.bs.cyber.service.we252001.We252001Service;    //간단e납부 3차관련 추가 상하수도
import com.uc.bs.cyber.service.we252002.We252002Service;
import com.uc.bs.cyber.service.we272001.We272001Service;
import com.uc.bs.cyber.service.we272002.We272002Service;
import com.uc.bs.cyber.service.we902001.We902001Service;
import com.uc.bs.cyber.service.we952001.We952001Service;
import com.uc.bs.cyber.service.we972001.We972001Service;


//EGTOB FRAME 을 이용하기 위함.
//import com.uc.egtob.net.server.RcvWorker;
//import com.uc.egtob.net.server.RcvServer;

public class Txdm2520 extends RcvWorker{
    
    private IbatisBaseService sqlService_cyber = null;
    private ApplicationContext appContext = null;
    private Thread comdThread = null;
        
    private We532001FieldList FL_532001 = new We532001FieldList();
    private We532002FieldList FL_532002 = new We532002FieldList();
    private We992001FieldList FL_992001 = new We992001FieldList();
    
    private RtnFieldList rtnList = new RtnFieldList();
    private ChkFieldList chkList = new ChkFieldList();
    
    
    private MapForm dataMap    = null;
    @SuppressWarnings("unused")
    private String dataSource  = null;
    private byte[] recvBuffer;
    
    private String strTaxGubun ="";
    
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
        
        sqlService_cyber = (IbatisBaseService) appContext.getBean("baseService_cyber");
        
        startServer();
        
    }

    /*=====================================================*/
    /*업무를 구현하시오....................................*/
    /*=====================================================*/
    /* RcvWorker 에서 수신된 소켓, 수신데이터를 처리한다...
     * 추상메서드 구현*/
    @Override
    public void doDataRecv(SocketChannel socket, byte[] buffer) throws Exception {
        // TODO Auto-generated method stub
    	log.info(" **** buffer convter => "+buffer.length);
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
            //mainTransProcess();
            
            /*NON트랜잭션*/
            txdm2520_JobProcess();
            
        } catch (Exception e) {
            
            e.printStackTrace();
        
            /*오류가 발생한 경우...특히 전문이 이상한 경우 고대로 돌려준다.*/
            retBuffer = buffer;
            
        } finally {
        
            /*마지막에는 스레기 수거 수행 */
            //System.runFinalization();
            
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
        
        int connPort =  Utils.getResourceInt("ApplicationResource", "wetax.recv.port");  /*9983*/
        
        RcvServer server = new RcvServer(connPort, "com.uc.bs.cyber.daemon.txdm2520.Txdm2520", 30, 20, "log4j.xml", 6, com.uc.core.Constants.TYPEFIELDLEN);

        server.setContext(appContext);
        server.setProcId("2520");
        server.setProcName("위택스통합연계서버");
        server.setThreadName("thr_2520");
        
        server.initial();
        
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
            
            if (type.equals("532001")) {//단건
                dataMap = FL_532001.parseBuff(buffer);

            } else if (type.equals("532002")) {//총괄
                dataMap = FL_532002.parseBuff(buffer);
                
                dataMap.setMap("EPAY_NO", dataMap.getMap("TOTPAY_NO"));
                
            } else if (type.equals("992001")) {//취소
                dataMap = FL_992001.parseBuff(buffer);
                
                //취소전문에는 필요없음....
                dataMap.setMap("EPAY_NO", " ");
                dataMap.setMap("PAY_DT" , CbUtil.getCurDateTimes().substring(0, 8));

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
                    
                    log.info(e.getMessage());
                    
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
        this.appContext = appContext;
        sqlService_cyber = (IbatisBaseService) appContext.getBean("baseService_cyber");
        
    }

    public ApplicationContext getAppContext() {
        return appContext;
    }
    
    /*트랜잭션을 실행하기 위한 함수.          현재 사용되지 않는듯 함 by 임창섭 20131114
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
    
    /*트랜잭션 없이 처리하도록 ....*/
    public void txdm2520_JobProcess(){
        
        MapForm logMap = new MapForm();
        
        String saveRet = "";
        
        try {
                        
            log.info("[=========== 여긴 txdm2520_JobProcess() :: NON트랜잭션처리영역 입니다. ========== ]");
            
            /*거래구분만 파싱한다...(거래종류를 알기 위함) --간단e납부관련 추가 전자납부번호도 파싱해야한다. 세금별 구분을 위해*/
            dataMap = chkList.parseBuff(recvBuffer);
            
            
            /* 송수신 전문 저장용*/
            logMap.setMap("SYS_DSC"  , "03"); /*위택스*/
            logMap.setMap("MSG_ID"   , dataMap.getMap("CO_TRAN")); /*전문종별*/
            
            log.debug("TXDM2520 RCV_CO_TRAN ===> "+(String) dataMap.getMap("CO_TRAN"));  
            
            logMap.setMap("RCV_MSG"  , new String(recvBuffer)); /*수신전문*/
            logMap.setMap("REG_DTM"  , CbUtil.getCurrent("yyyyMMddHHmmss"));
            
            if (((String) dataMap.getMap("CO_TRAN")).equals("532001")) { 
                /*
                 * 개별수납건에 대한 전문 처리
                 * */
                
                saveRet = saveTeleGram((String) dataMap.getMap("CO_TRAN"), recvBuffer);  /*수신전문 저장*/
                
                log.debug("TXDM2520 saveRet ===> " + saveRet);
                
                if(saveRet.equals("")) {   /*정상적인 전문인경우*/
                    
                    dataMap = FL_532001.parseBuff(recvBuffer);      /*수신전문 파싱(전체)*/
    
                    if(log.isDebugEnabled()){
                        log.debug(dataMap); 
                    }
                    
                    // 카드수납인지 계좌이체인지 구분 기능 추가
                    // 현재 전문에 판단할 수 있는 근거는 카드승인번호에 값이 있는지 없는지 이므로 고걸로만 판단한다
                    // 20110719 카드와 구분안함...
                    if(!((String) dataMap.getMap("CARD_REQ_NO")).trim().equals("")) { /* 카드승인번호가 있으면 카드수납 */
                        
                        dataMap.setMap("BNK_CD", ((String) dataMap.getMap("BANK_COD")).substring(0, 3));
                        
                    } else { /* 아니면 계좌이체 수납 */
                        
                        dataMap.setMap("BNK_CD", ((String) dataMap.getMap("BANK_COD")).substring(0, 3));
                
                    }
                    
                    // 세금별 구분 처리 간단e납부 관련하여 추가 20131104
                    dataMap.setMap("CHARGE_GB", dataMap.getMap("EPAY_NO").toString().substring(5, 6));//전자납부번호.요금구분 첫 숫자로 세금을 구분한다.
                    
                    log.debug("CHARGE_GB = " + dataMap.getMap("CHARGE_GB"));
                    
                    if (dataMap.getMap("CHARGE_GB").equals("1")) { //지방세
                        
                        We532001Service chkWeTax = new We532001Service(appContext);  /* 532001 업무처리 */
    
                        saveRet = chkWeTax.chkweTax(dataMap);
                        
                    } else if (dataMap.getMap("CHARGE_GB").equals("2")) { //세외수입
                        
                        dataMap.setMap("GB", dataMap.getMap("EPAY_NO").toString().substring(10, 11));//전자납부번호.구분으로 세금을 구분한다.
                        log.debug("세금종류구분(GB) ::: " + dataMap.getMap("GB"));
                        
//                        
//                        if (dataMap.getMap("GB").equals("0")
//                          ||dataMap.getMap("GB").equals("1")
//                          ||dataMap.getMap("GB").equals("2")
//                          ||dataMap.getMap("GB").equals("3")
//                          ||dataMap.getMap("GB").equals("4")
//                          ||dataMap.getMap("GB").equals("5")//간단e납부3차 세올, 지자체 주정차위반과태료
//                          ||dataMap.getMap("GB").equals("6")) {//간단e납부3차 세올, 지자체 교통유발부담금
                        
                            We272001Service chkBnonWeTax = new We272001Service(appContext);  /* 272001 업무처리 */
                        
                            saveRet = chkBnonWeTax.chkweTax(dataMap);
                            
//                        } else if (dataMap.getMap("GB").equals("5")) { //간단e납부3차 세올, 지자체 주정차위반과태료
//                            
//                        } else if (dataMap.getMap("GB").equals("6")) { //간단e납부3차 세올, 지자체 교통유발부담금
//                            
//                        }
                        
                    } else if (dataMap.getMap("CHARGE_GB").equals("9")) { //환경개선부담금
                        
                        We202001Service chkEnvWeTax = new We202001Service(appContext);  /* 202001 업무처리 */
                        
                        saveRet = chkEnvWeTax.chkweTax(dataMap);
                        
                    } else if (dataMap.getMap("CHARGE_GB").equals("0")) { //간단e납부3차 상하수도요금 
                        
                        We252001Service chkWaterWeTax = new We252001Service(appContext);  /* 202001 업무처리 */
                        
                        saveRet = chkWaterWeTax.chkweTax(dataMap);
                        
                    } else { // 그외 요금구분은 오류처리
                        
                        rtnList.setField("RSLT_COD"     , "44000-000");
                        rtnList.setField("RSLT_MESSAGE" , "전자납부번호 요금구분항목 오류입니다");
                    }
    
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
                        
                        dataMap.setMap("BNK_CD", ((String) dataMap.getMap("BANK_COD")).substring(0, 3));
                        
                    } else { /* 아니면 계좌이체 수납 */
                        
                        dataMap.setMap("BNK_CD", ((String) dataMap.getMap("BANK_COD")).substring(0, 3));
                
                    }
                    
//                    We532002Service chkWeTax = new We532002Service(appContext);  /* 532002 업무처리 */
//                    
//                    saveRet = chkWeTax.chkweTax(dataMap);
                  
                  // 세금별 구분 처리 간단e납부 관련하여 추가 20131104
                  dataMap.setMap("CHARGE_GB", dataMap.getMap("TOTPAY_NO").toString().substring(0, 1));//전자납부번호(총괄납부고유번호).첫 문자로 세금을 구분한다.
                  
                  log.debug("CHARGE_GB = " + dataMap.getMap("CHARGE_GB"));
                  
                  if (dataMap.getMap("CHARGE_GB").equals("T")) { //지방세
                      
                      We532002Service chkWeTax = new We532002Service(appContext);  /* 532002 업무처리 */
                      
                      saveRet = chkWeTax.chkweTax(dataMap);
                      
                  } else if (dataMap.getMap("CHARGE_GB").equals("V")) { //세외수입
                      
                      We272002Service chkBnonWeTax = new We272002Service(appContext);  /* 272002 업무처리 */
                      
                      saveRet = chkBnonWeTax.chkweTax(dataMap);
                      
                  } else if (dataMap.getMap("CHARGE_GB").equals("U")) { //환경개선부담금
                      
                      We202002Service chkEnvWeTax = new We202002Service(appContext);  /* 202002 업무처리 */
                      
                      saveRet = chkEnvWeTax.chkweTax(dataMap);
                      
                  } else if (dataMap.getMap("CHARGE_GB").equals("W")) { //상하수도
                      
                      We252002Service chkWaterWeTax = new We252002Service(appContext);  /* 202002 업무처리 */
                      
                      saveRet = chkWaterWeTax.chkweTax(dataMap);
                      
                  } else { //그외 다른 요금구분은 오류처리
                      
                      rtnList.setField("RSLT_COD"     , "44000-000");
                      rtnList.setField("RSLT_MESSAGE" , "총괄납부번호 구분항목 오류입니다");
                  }
                  
                  retBuffer = rtnList.CYB532002(saveRet);
                  
              }
                
            } else if (((String) dataMap.getMap("CO_TRAN")).equals("992001")) {
                /*
                 * 취소 전문 처리
                 * */
                
                saveRet = saveTeleGram((String) dataMap.getMap("CO_TRAN"), recvBuffer);  /*수신전문 저장*/
                saveRet = "";
                
                if(saveRet.equals("")) {   /*정상적인 전문인경우*/
                    
                    dataMap = FL_992001.parseBuff(recvBuffer);
                    
                    if(log.isDebugEnabled()){
                        log.debug(dataMap); 
                    }
                    
//                  We992001Service chkWeTax = new We992001Service(appContext);  /* 532002 업무처리 */
//                  
//                  saveRet = chkWeTax.chkweTax(dataMap);

                    
                    // 세금별 구분 처리 간단e납부 관련하여 추가 20131104 => 추후 DB화 하고 공통에서 처리토록 하자
                    //dataMap.setMap("CHARGE_GB", dataMap.getMap("GIRO_NO").toString());//취소건은 구분할 번호가 없으므로 지로번호로 구분함.
                    
                    //log.debug("CHARGE_GB = " + dataMap.getMap("CHARGE_GB"));
                    
                    strTaxGubun = (String) sqlService_cyber.queryForBean("CODMBASE.GIRO_FOR_TAX", dataMap.getMap("GIRO_NO"));  
                    
                    if ("1".equals(strTaxGubun)) { //지방세
                        
                        We992001Service chkWeTax = new We992001Service(appContext);  /* 532001 업무처리 */
    
                        saveRet = chkWeTax.chkweTax(dataMap);
                        
                    } else if ("2".equals(strTaxGubun)) { //통합세외수입
                        
                        We972001Service chkBnonWeTax = new We972001Service(appContext);  /* 272001 업무처리 */
                        
                        saveRet = chkBnonWeTax.chkweTax(dataMap);
                        
                    } else if ("3".equals(strTaxGubun)) { //환경개선부담금
                        
                        We902001Service chkEnvWeTax = new We902001Service(appContext);  /* 202001 업무처리 */
                        
                        saveRet = chkEnvWeTax.chkweTax(dataMap);
                        
                    } else if ("5".equals(strTaxGubun)) { //상하수도
                   
                       We952001Service chkWaterWeTax = new We952001Service(appContext);  /* 202001 업무처리 */
                       
                       saveRet = chkWaterWeTax.chkweTax(dataMap);
                   
                    } else { // 그외 요금구분은 오류처리
                        
                        rtnList.setField("RSLT_COD"     , "44000-101");
                        rtnList.setField("RSLT_MESSAGE" , "지로번호 오류입니다");
                    }
                    
                    retBuffer = rtnList.CYB992001(saveRet);
                    
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
            
            //종료 후 [retBuffer] 의 내용을 송신하고 마무리 한다.
            //여긴 이벤트 지역
            
            /*마지막으로 송수신전문을 저장한다...*/
            logMap.setMap("RTN_MSG" , new String(retBuffer)); /*응답전문*/
            logMap.setMap("RES_CD"  , rtnList.getField("RSLT_COD"));     /*처리결과*/
            logMap.setMap("ERR_MSG" , rtnList.getField("RSLT_MESSAGE")); /*오류메시지*/
            logMap.setMap("REG_DTM" , CbUtil.getCurrent("yyyyMMddHHmmss"));
            logMap.setMap("LAST_DTM", CbUtil.getCurrent("yyyyMMddHHmmss"));
            
            if(sqlService_cyber.queryForUpdate("CODMBASE.CODM_JUNMUN_TXRX_INSERT", logMap) > 0) {
                log.info("송수신 전문 저장완료!!!");
            }   
                

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    

    /*트랜잭션 처리          현재 사용되지 않는듯 함 by 임창섭 20131114*/
    @Override
    public void transactionJob() {
        // TODO Auto-generated method stub
        
        MapForm logMap = new MapForm();
        
        String saveRet = "";
        
        try {
                        
            log.info("[=========== 여긴 transactionJob() :: 트랜잭션처리영역 입니다. ========== ]");
            
            /*거래구분만 파싱한다...(거래종류를 알기 위함)*/
            dataMap = chkList.parseBuff(recvBuffer);
            
            
            /* 송수신 전문 저장용*/
            logMap.setMap("SYS_DSC"  , "03"); /*위택스*/
            logMap.setMap("MSG_ID"   , dataMap.getMap("CO_TRAN")); /*전문종별*/
            logMap.setMap("RCV_MSG"  , new String(recvBuffer)); /*수신전문*/
            logMap.setMap("REG_DTM"  , CbUtil.getCurrent("yyyyMMddHHmmss"));
            
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
                    // 20110719 카드와 구분안함...
                    if(!((String) dataMap.getMap("CARD_REQ_NO")).trim().equals("")) { /* 카드승인번호가 있으면 카드수납 */
                        
                        dataMap.setMap("BNK_CD", ((String) dataMap.getMap("BANK_COD")).substring(0, 3));
                        
                    } else { /* 아니면 계좌이체 수납 */
                        
                        dataMap.setMap("BNK_CD", ((String) dataMap.getMap("BANK_COD")).substring(0, 3));
                
                    }
    
                    // 세금별 구분 처리 간단e납부 관련하여 추가 20131104
                    dataMap.setMap("CHARGE_GB", dataMap.getMap("EPAY_NO").toString().substring(5, 6));//전자납부번호.요금구분 첫 숫자로 세금을 구분한다.
                    
                    log.debug("CHARGE_GB = " + dataMap.getMap("CHARGE_GB"));
                    
                    if (dataMap.getMap("CHARGE_GB").equals("1")) { //지방세
                        
                        We532001Service chkWeTax = new We532001Service(appContext);  /* 532001 업무처리 */
                        
                        saveRet = chkWeTax.chkweTax(dataMap);
                        
                    } else if (dataMap.getMap("CHARGE_GB").equals("2")) { //세외수입
                        
                        We272001Service chkBnonWeTax = new We272001Service(appContext);  /* 272001 업무처리 */
                        
                        saveRet = chkBnonWeTax.chkweTax(dataMap);
                        
                    } else if (dataMap.getMap("CHARGE_GB").equals("9")) { //환경개선부담금
                        
                        We202001Service chkEnvWeTax = new We202001Service(appContext);  /* 202001 업무처리 */
                        
                        saveRet = chkEnvWeTax.chkweTax(dataMap);
                        
                    } else if (dataMap.getMap("CHARGE_GB").equals("0")) { //상하수도
                        
                        We252001Service chkWaterWeTax = new We252001Service(appContext);  /* 202001 업무처리 */
                        
                        saveRet = chkWaterWeTax.chkweTax(dataMap);
                        
                    } else { //그외 다른 요금구분은 오류처리
                        
                        rtnList.setField("RSLT_COD"     , "44000-000");
                        rtnList.setField("RSLT_MESSAGE" , "전자납부번호 요금구분항목 오류입니다");
                    }
    
                    retBuffer = rtnList.CYB532001(saveRet);
                }
                
            } else if (((String) dataMap.getMap("CO_TRAN")).equals("532002")) {
                /*
                 * 총괄수납건에 대한 전문 처리
                 * */
                log.info("[=========== 총괄수납건에 대한 전문 처리. ========== ]");
                
                saveRet = saveTeleGram((String) dataMap.getMap("CO_TRAN"), recvBuffer);  /*수신전문 저장*/
                
                if(saveRet.equals("")) {   /*정상적인 전문인경우*/
                    
                    dataMap = FL_532002.parseBuff(recvBuffer);
                    
                    if(log.isDebugEnabled()){
                        log.debug(dataMap); 
                    }
                    
                    if(!((String) dataMap.getMap("CARD_REQ_NO")).trim().equals("")) { /* 카드승인번호가 있으면 카드수납 */
                        
                        dataMap.setMap("BNK_CD", ((String) dataMap.getMap("BANK_COD")).substring(0, 3));
                        
                    } else { /* 아니면 계좌이체 수납 */
                        
                        dataMap.setMap("BNK_CD", ((String) dataMap.getMap("BANK_COD")).substring(0, 3));
                
                    }
                    
//                    We532002Service chkweTax = new We532002Service(appContext);  /* 532002 업무처리 */
//                    
//                    saveRet = chkweTax.chkweTax(dataMap);
                    
                 // 세금별 구분 처리 간단e납부 관련하여 추가 20131104
                    dataMap.setMap("CHARGE_GB", dataMap.getMap("EPAY_NO").toString().substring(1, 1));//전자납부번호(총괄납부고유번호).첫 문자로 세금을 구분한다.
                    
                    log.debug("CHARGE_GB = " + dataMap.getMap("CHARGE_GB"));
                    
                    if (dataMap.getMap("CHARGE_GB").equals("T")) { //지방세
                        
                        We532002Service chkWeTax = new We532002Service(appContext);  /* 532002 업무처리 */
                        
                        saveRet = chkWeTax.chkweTax(dataMap);
                        
                    } else if (dataMap.getMap("CHARGE_GB").equals("V")) { //세외수입
                        
                        We272002Service chkBnonWeTax = new We272002Service(appContext);  /* 272002 업무처리 */
                        
                        saveRet = chkBnonWeTax.chkweTax(dataMap);
                        
                    } else if (dataMap.getMap("CHARGE_GB").equals("U")) { //환경개선부담금
                        
                        We202002Service chkEnvWeTax = new We202002Service(appContext);  /* 202002 업무처리 */
                        
                        saveRet = chkEnvWeTax.chkweTax(dataMap);
                        
                    } else if (dataMap.getMap("CHARGE_GB").equals("W")) { //상하수도
                        
                        We252002Service chkWaterWeTax = new We252002Service(appContext);  /* 202002 업무처리 */
                        
                        saveRet = chkWaterWeTax.chkweTax(dataMap);
                        
                    } else { //그외 다른 요금구분은 오류처리
                        
                        rtnList.setField("RSLT_COD"     , "44000-000");
                        rtnList.setField("RSLT_MESSAGE" , "총괄납부번호 구분항목 오류입니다");
                    }
                    
                    retBuffer = rtnList.CYB532002(saveRet);
                    
                }
                
                
            } else if (((String) dataMap.getMap("CO_TRAN")).equals("992001")) {
                /*
                 * 취소 전문 처리
                 * */
                log.info("[=========== 취소건에 대한 전문 처리. ========== ]");
                
                saveRet = saveTeleGram((String) dataMap.getMap("CO_TRAN"), recvBuffer);  /*수신전문 저장*/
                
                if(saveRet.equals("")) {   /*정상적인 전문인경우*/
                    
                    dataMap = FL_992001.parseBuff(recvBuffer);
                    
                    if(log.isDebugEnabled()){
                        log.debug(dataMap); 
                    }
                    
//                  We992001Service chkweTax = new We992001Service(appContext);  /* 992001 업무처리 */
//                  
//                  saveRet = chkweTax.chkweTax(dataMap);
                    
                    // 세금별 구분 처리 간단e납부 관련하여 추가 20131104
//                    dataMap.setMap("CHARGE_GB", dataMap.getMap("EPAY_NO").toString().substring(1, 1));//전자납부번호(총괄납부고유번호).첫 문자로 세금을 구분한다.
                    
                    log.debug("GIRO_NO = " + dataMap.getMap("GIRO_NO"));
                    strTaxGubun = (String) sqlService_cyber.queryForBean("CODMBASE.GIRO_FOR_TAX", dataMap.getMap("GIRO_NO"));
                    
                    if ("1".equals(strTaxGubun)) { //지방세
                        
                        We992001Service chkWeTax = new We992001Service(appContext);  /* 992001 업무처리 */
                        
                        saveRet = chkWeTax.chkweTax(dataMap);
                        
                    } else if ("2".equals(strTaxGubun)) { //통합세외수입
                        
                        We972001Service chkBnonWeTax = new We972001Service(appContext);  /* 972001 업무처리 */
                        
                        saveRet = chkBnonWeTax.chkweTax(dataMap);
                        
                    } else if ("3".equals(strTaxGubun)) { //환경개선부담금
                        
                        We902001Service chkEnvWeTax = new We902001Service(appContext);  /* 902001 업무처리 */
                        
                        saveRet = chkEnvWeTax.chkweTax(dataMap);
                        
                    } else if ("5".equals(strTaxGubun)) { //상하수도
                        
                        We952001Service chkWaterWeTax = new We952001Service(appContext);  /* 202001 업무처리 */
                        
                        saveRet = chkWaterWeTax.chkweTax(dataMap);
                    
                    } else { //그외 다른 요금구분은 오류처리
                        
                        rtnList.setField("RSLT_COD"     , "44000-000");
                        rtnList.setField("RSLT_MESSAGE" , "총괄납부번호 구분항목 오류입니다");
                    }
                    
                    retBuffer = rtnList.CYB992001(saveRet);
                    
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
            
            //종료 후 [retBuffer] 의 내용을 송신하고 마무리 한다.
            //여긴 이벤트 지역
            
            /*마지막으로 송수신전문을 저장한다...*/
            logMap.setMap("RTN_MSG" , new String(retBuffer)); /*응답전문*/
            logMap.setMap("RES_CD"  , rtnList.getField("RSLT_COD"));     /*처리결과*/
            logMap.setMap("ERR_MSG" , rtnList.getField("RSLT_MESSAGE")); /*오류메시지*/
            logMap.setMap("REG_DTM" , CbUtil.getCurrent("yyyyMMddHHmmss"));
            logMap.setMap("LAST_DTM", CbUtil.getCurrent("yyyyMMddHHmmss"));
            
            if(sqlService_cyber.queryForUpdate("CODMBASE.CODM_JUNMUN_TXRX_INSERT", logMap) > 0) {
                log.info("송수신 전문 저장완료!!!");
            }   
                

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
