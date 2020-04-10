/**
 *  �ֽý��۸� : ���̹����漼û ��ȭ
 *  ��  ��  �� : ����
 *  ��  ��  �� : ���ý� ���ռ������輭���� �⵿�ϰ� �ۼ��ŵ����͸� ó���Ѵ�...
 *               com.uc.bs.cyber.etax.* �ڿ����
 *  Ŭ����  ID : Txdm2520 
 *  ����  �̷� :
 * ------------------------------------------------------------------------
 *  �ۼ���         �Ҽ�              ����            Tag           ����
 * ------------------------------------------------------------------------
 *  �۵���      ��ä��(��)         2011.04.24         %01%         �����ۼ�
 *  ��â��      ��ä��(��)         2014.11.20         %02%         ����e���� ����(���ܼ���, ȯ�氳���δ��) �б� �߰� �� ����
 *  ��â��      ��ä��(��)         2014.11.20         %02%         ����e���� ����(���ܼ���, ȯ�氳���δ��) �б� �߰� �� ����
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
//����e���� 2������ �߰�
import com.uc.bs.cyber.service.we202001.We202001Service;
import com.uc.bs.cyber.service.we202002.We202002Service;
import com.uc.bs.cyber.service.we252001.We252001Service;    //����e���� 3������ �߰� ���ϼ���
import com.uc.bs.cyber.service.we252002.We252002Service;
import com.uc.bs.cyber.service.we272001.We272001Service;
import com.uc.bs.cyber.service.we272002.We272002Service;
import com.uc.bs.cyber.service.we902001.We902001Service;
import com.uc.bs.cyber.service.we952001.We952001Service;
import com.uc.bs.cyber.service.we972001.We972001Service;


//EGTOB FRAME �� �̿��ϱ� ����.
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
    
    /* ������ 
     * QUEUE ��
     * */
    public Txdm2520() throws Exception {
        super();
        // TODO Auto-generated constructor stub

        log.info("============================================");
        log.info("== new RcvWorker( )                 Start ==");
        log.info("============================================");

        
    }
    
    /* ������ 
     * �ʱ�ȭ��
     * */
    public Txdm2520(int annotation) throws Exception {
        // TODO Auto-generated constructor stub
        // nothing...
        
    }   
    
    /*
     * Ʈ����ǿ�(������)
     * */
    public Txdm2520(ApplicationContext context, String dataSource) throws Exception {
        
        /*Context ����...*/
        this.context = context;
        this.dataSource = dataSource;
        
    }
            
    /*������*/
    public Txdm2520(ApplicationContext context) throws Exception {
        
        /*Context ����...*/
        setAppContext(context);
        
        sqlService_cyber = (IbatisBaseService) appContext.getBean("baseService_cyber");
        
        startServer();
        
    }

    /*=====================================================*/
    /*������ �����Ͻÿ�....................................*/
    /*=====================================================*/
    /* RcvWorker ���� ���ŵ� ����, ���ŵ����͸� ó���Ѵ�...
     * �߻�޼��� ����*/
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
             * ����ó��...(Ʈ�����ó��)
             * */
            recvBuffer =  buffer;
                        
            /*Ʈ����� ����...*/
            //mainTransProcess();
            
            /*NONƮ�����*/
            txdm2520_JobProcess();
            
        } catch (Exception e) {
            
            e.printStackTrace();
        
            /*������ �߻��� ���...Ư�� ������ �̻��� ��� ���� �����ش�.*/
            retBuffer = buffer;
            
        } finally {
        
            /*���������� ������ ���� ���� */
            //System.runFinalization();
            
        }

    }
    
    
    /* Main Thread 
     * �׽�Ʈ ����ø� ����մϴ�...
     * */
    public static void main(String args[]) {

        ApplicationContext context  = null;
        
        try {

            Txdm2520 svr = new Txdm2520(0);
            
            /*Log4j ����*/
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
    
    /* ���� ����...*/
    private void startServer() throws Exception {
        
        /**
         * SERVER START
         * port     : Listen Port
         * worker   : Worker ������(class)
         * maxQue   : �ִ� Queue ũ��
         * procCnt  : ���μ���(������) ����
         */
        
        int connPort =  Utils.getResourceInt("ApplicationResource", "wetax.recv.port");  /*9983*/
        
        RcvServer server = new RcvServer(connPort, "com.uc.bs.cyber.daemon.txdm2520.Txdm2520", 30, 20, "log4j.xml", 6, com.uc.core.Constants.TYPEFIELDLEN);

        server.setContext(appContext);
        server.setProcId("2520");
        server.setProcName("���ý����տ��輭��");
        server.setThreadName("thr_2520");
        
        server.initial();
        
        comdThread = new Thread(server);
        
        /**
         * ������ �ۼ����� ���ؼ� ������ ������ش�
         * (������������)�� ���ؼ� Threading...
         */
        comdThread.setName("2520");
        
        comdThread.start();
        
    }
    
    /*----------------------------------------------*/
    /* �Ϸù�ȣ �������� �׽�Ʈ*/
    /*----------------------------------------------*/
    @SuppressWarnings("unused")
    private int SeqNumber(){
        return (Integer)sqlService_cyber.queryForBean("CODMBASE.CODM_GETSEQNO", "00");
    }
    

    /*
     * ���������� DB�� �����Ѵ�...
     * @param type : ��������
     * @param buffer : �ۼ�������
     * */
    public String saveTeleGram(String type, byte[] buffer) throws Exception {
        
        log.debug("===============JUNMUN Save()================");
        
        String retStr = ""; 
        
        try {
            
            if (type.equals("532001")) {//�ܰ�
                dataMap = FL_532001.parseBuff(buffer);

            } else if (type.equals("532002")) {//�Ѱ�
                dataMap = FL_532002.parseBuff(buffer);
                
                dataMap.setMap("EPAY_NO", dataMap.getMap("TOTPAY_NO"));
                
            } else if (type.equals("992001")) {//���
                dataMap = FL_992001.parseBuff(buffer);
                
                //����������� �ʿ����....
                dataMap.setMap("EPAY_NO", " ");
                dataMap.setMap("PAY_DT" , CbUtil.getCurDateTimes().substring(0, 8));

            } else {
                
            }
            
            dataMap.setMap("MSG", new String(buffer));
            
            if (log.isDebugEnabled()){
                log.debug(dataMap);
            }
            
            
            /*���������� �����Ѵ�...*/
            sqlService_cyber.queryForInsert("CODMBASE.CODM_JUNMUN_LOG_SAVE", dataMap);
            
            
        } catch (DuplicateKeyException dke) { /* �ߺ��� �߻��� ��� */
            
            dke.printStackTrace();
            
            dataMap.setMap("RSLT_COD", "44100-100");
            dataMap.setMap("RSLT_MESSAGE", "��������!! �̹� ��ϵ� �����Դϴ�");
            
            retBuffer = rtnList.getBuff(dataMap);
            retStr = "44100-100";
            
        } catch (SQLException se) {
            se.printStackTrace();
            
            try {

                if(se.getErrorCode() == 1) {
                    
                    dataMap.setMap("RSLT_COD", "44100-100");
                    dataMap.setMap("RSLT_MESSAGE", "��������!! �̹� ��ϵ� �����Դϴ�");
                    
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
    
    /*Ʈ������� �����ϱ� ���� �Լ�.          ���� ������ �ʴµ� �� by ��â�� 20131114
     * -- �Լ� ����� : transactionJob() �Լ� �ڵ� ����...
     * -- transactionJob �Լ� ���������� Ʈ������� �����Ѵ�.
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
    
    /*Ʈ����� ���� ó���ϵ��� ....*/
    public void txdm2520_JobProcess(){
        
        MapForm logMap = new MapForm();
        
        String saveRet = "";
        
        try {
                        
            log.info("[=========== ���� txdm2520_JobProcess() :: NONƮ�����ó������ �Դϴ�. ========== ]");
            
            /*�ŷ����и� �Ľ��Ѵ�...(�ŷ������� �˱� ����) --����e���ΰ��� �߰� ���ڳ��ι�ȣ�� �Ľ��ؾ��Ѵ�. ���ݺ� ������ ����*/
            dataMap = chkList.parseBuff(recvBuffer);
            
            
            /* �ۼ��� ���� �����*/
            logMap.setMap("SYS_DSC"  , "03"); /*���ý�*/
            logMap.setMap("MSG_ID"   , dataMap.getMap("CO_TRAN")); /*��������*/
            
            log.debug("TXDM2520 RCV_CO_TRAN ===> "+(String) dataMap.getMap("CO_TRAN"));  
            
            logMap.setMap("RCV_MSG"  , new String(recvBuffer)); /*��������*/
            logMap.setMap("REG_DTM"  , CbUtil.getCurrent("yyyyMMddHHmmss"));
            
            if (((String) dataMap.getMap("CO_TRAN")).equals("532001")) { 
                /*
                 * ���������ǿ� ���� ���� ó��
                 * */
                
                saveRet = saveTeleGram((String) dataMap.getMap("CO_TRAN"), recvBuffer);  /*�������� ����*/
                
                log.debug("TXDM2520 saveRet ===> " + saveRet);
                
                if(saveRet.equals("")) {   /*�������� �����ΰ��*/
                    
                    dataMap = FL_532001.parseBuff(recvBuffer);      /*�������� �Ľ�(��ü)*/
    
                    if(log.isDebugEnabled()){
                        log.debug(dataMap); 
                    }
                    
                    // ī��������� ������ü���� ���� ��� �߰�
                    // ���� ������ �Ǵ��� �� �ִ� �ٰŴ� ī����ι�ȣ�� ���� �ִ��� ������ �̹Ƿ� ��ɷθ� �Ǵ��Ѵ�
                    // 20110719 ī��� ���о���...
                    if(!((String) dataMap.getMap("CARD_REQ_NO")).trim().equals("")) { /* ī����ι�ȣ�� ������ ī����� */
                        
                        dataMap.setMap("BNK_CD", ((String) dataMap.getMap("BANK_COD")).substring(0, 3));
                        
                    } else { /* �ƴϸ� ������ü ���� */
                        
                        dataMap.setMap("BNK_CD", ((String) dataMap.getMap("BANK_COD")).substring(0, 3));
                
                    }
                    
                    // ���ݺ� ���� ó�� ����e���� �����Ͽ� �߰� 20131104
                    dataMap.setMap("CHARGE_GB", dataMap.getMap("EPAY_NO").toString().substring(5, 6));//���ڳ��ι�ȣ.��ݱ��� ù ���ڷ� ������ �����Ѵ�.
                    
                    log.debug("CHARGE_GB = " + dataMap.getMap("CHARGE_GB"));
                    
                    if (dataMap.getMap("CHARGE_GB").equals("1")) { //���漼
                        
                        We532001Service chkWeTax = new We532001Service(appContext);  /* 532001 ����ó�� */
    
                        saveRet = chkWeTax.chkweTax(dataMap);
                        
                    } else if (dataMap.getMap("CHARGE_GB").equals("2")) { //���ܼ���
                        
                        dataMap.setMap("GB", dataMap.getMap("EPAY_NO").toString().substring(10, 11));//���ڳ��ι�ȣ.�������� ������ �����Ѵ�.
                        log.debug("������������(GB) ::: " + dataMap.getMap("GB"));
                        
//                        
//                        if (dataMap.getMap("GB").equals("0")
//                          ||dataMap.getMap("GB").equals("1")
//                          ||dataMap.getMap("GB").equals("2")
//                          ||dataMap.getMap("GB").equals("3")
//                          ||dataMap.getMap("GB").equals("4")
//                          ||dataMap.getMap("GB").equals("5")//����e����3�� ����, ����ü ���������ݰ��·�
//                          ||dataMap.getMap("GB").equals("6")) {//����e����3�� ����, ����ü �������ߺδ��
                        
                            We272001Service chkBnonWeTax = new We272001Service(appContext);  /* 272001 ����ó�� */
                        
                            saveRet = chkBnonWeTax.chkweTax(dataMap);
                            
//                        } else if (dataMap.getMap("GB").equals("5")) { //����e����3�� ����, ����ü ���������ݰ��·�
//                            
//                        } else if (dataMap.getMap("GB").equals("6")) { //����e����3�� ����, ����ü �������ߺδ��
//                            
//                        }
                        
                    } else if (dataMap.getMap("CHARGE_GB").equals("9")) { //ȯ�氳���δ��
                        
                        We202001Service chkEnvWeTax = new We202001Service(appContext);  /* 202001 ����ó�� */
                        
                        saveRet = chkEnvWeTax.chkweTax(dataMap);
                        
                    } else if (dataMap.getMap("CHARGE_GB").equals("0")) { //����e����3�� ���ϼ������ 
                        
                        We252001Service chkWaterWeTax = new We252001Service(appContext);  /* 202001 ����ó�� */
                        
                        saveRet = chkWaterWeTax.chkweTax(dataMap);
                        
                    } else { // �׿� ��ݱ����� ����ó��
                        
                        rtnList.setField("RSLT_COD"     , "44000-000");
                        rtnList.setField("RSLT_MESSAGE" , "���ڳ��ι�ȣ ��ݱ����׸� �����Դϴ�");
                    }
    
                    retBuffer = rtnList.CYB532001(saveRet);
                }
                
            } else if (((String) dataMap.getMap("CO_TRAN")).equals("532002")) {
                /*
                 * �Ѱ������ǿ� ���� ���� ó��
                 * */
                
                saveRet = saveTeleGram((String) dataMap.getMap("CO_TRAN"), recvBuffer);  /*�������� ����*/
                
                if(saveRet.equals("")) {   /*�������� �����ΰ��*/
                    
                    dataMap = FL_532002.parseBuff(recvBuffer);
                    
                    if(log.isDebugEnabled()){
                        log.debug(dataMap); 
                    }
                    
                    if(!((String) dataMap.getMap("CARD_REQ_NO")).trim().equals("")) { /* ī����ι�ȣ�� ������ ī����� */
                        
                        dataMap.setMap("BNK_CD", ((String) dataMap.getMap("BANK_COD")).substring(0, 3));
                        
                    } else { /* �ƴϸ� ������ü ���� */
                        
                        dataMap.setMap("BNK_CD", ((String) dataMap.getMap("BANK_COD")).substring(0, 3));
                
                    }
                    
//                    We532002Service chkWeTax = new We532002Service(appContext);  /* 532002 ����ó�� */
//                    
//                    saveRet = chkWeTax.chkweTax(dataMap);
                  
                  // ���ݺ� ���� ó�� ����e���� �����Ͽ� �߰� 20131104
                  dataMap.setMap("CHARGE_GB", dataMap.getMap("TOTPAY_NO").toString().substring(0, 1));//���ڳ��ι�ȣ(�Ѱ����ΰ�����ȣ).ù ���ڷ� ������ �����Ѵ�.
                  
                  log.debug("CHARGE_GB = " + dataMap.getMap("CHARGE_GB"));
                  
                  if (dataMap.getMap("CHARGE_GB").equals("T")) { //���漼
                      
                      We532002Service chkWeTax = new We532002Service(appContext);  /* 532002 ����ó�� */
                      
                      saveRet = chkWeTax.chkweTax(dataMap);
                      
                  } else if (dataMap.getMap("CHARGE_GB").equals("V")) { //���ܼ���
                      
                      We272002Service chkBnonWeTax = new We272002Service(appContext);  /* 272002 ����ó�� */
                      
                      saveRet = chkBnonWeTax.chkweTax(dataMap);
                      
                  } else if (dataMap.getMap("CHARGE_GB").equals("U")) { //ȯ�氳���δ��
                      
                      We202002Service chkEnvWeTax = new We202002Service(appContext);  /* 202002 ����ó�� */
                      
                      saveRet = chkEnvWeTax.chkweTax(dataMap);
                      
                  } else if (dataMap.getMap("CHARGE_GB").equals("W")) { //���ϼ���
                      
                      We252002Service chkWaterWeTax = new We252002Service(appContext);  /* 202002 ����ó�� */
                      
                      saveRet = chkWaterWeTax.chkweTax(dataMap);
                      
                  } else { //�׿� �ٸ� ��ݱ����� ����ó��
                      
                      rtnList.setField("RSLT_COD"     , "44000-000");
                      rtnList.setField("RSLT_MESSAGE" , "�Ѱ����ι�ȣ �����׸� �����Դϴ�");
                  }
                  
                  retBuffer = rtnList.CYB532002(saveRet);
                  
              }
                
            } else if (((String) dataMap.getMap("CO_TRAN")).equals("992001")) {
                /*
                 * ��� ���� ó��
                 * */
                
                saveRet = saveTeleGram((String) dataMap.getMap("CO_TRAN"), recvBuffer);  /*�������� ����*/
                saveRet = "";
                
                if(saveRet.equals("")) {   /*�������� �����ΰ��*/
                    
                    dataMap = FL_992001.parseBuff(recvBuffer);
                    
                    if(log.isDebugEnabled()){
                        log.debug(dataMap); 
                    }
                    
//                  We992001Service chkWeTax = new We992001Service(appContext);  /* 532002 ����ó�� */
//                  
//                  saveRet = chkWeTax.chkweTax(dataMap);

                    
                    // ���ݺ� ���� ó�� ����e���� �����Ͽ� �߰� 20131104 => ���� DBȭ �ϰ� ���뿡�� ó����� ����
                    //dataMap.setMap("CHARGE_GB", dataMap.getMap("GIRO_NO").toString());//��Ұ��� ������ ��ȣ�� �����Ƿ� ���ι�ȣ�� ������.
                    
                    //log.debug("CHARGE_GB = " + dataMap.getMap("CHARGE_GB"));
                    
                    strTaxGubun = (String) sqlService_cyber.queryForBean("CODMBASE.GIRO_FOR_TAX", dataMap.getMap("GIRO_NO"));  
                    
                    if ("1".equals(strTaxGubun)) { //���漼
                        
                        We992001Service chkWeTax = new We992001Service(appContext);  /* 532001 ����ó�� */
    
                        saveRet = chkWeTax.chkweTax(dataMap);
                        
                    } else if ("2".equals(strTaxGubun)) { //���ռ��ܼ���
                        
                        We972001Service chkBnonWeTax = new We972001Service(appContext);  /* 272001 ����ó�� */
                        
                        saveRet = chkBnonWeTax.chkweTax(dataMap);
                        
                    } else if ("3".equals(strTaxGubun)) { //ȯ�氳���δ��
                        
                        We902001Service chkEnvWeTax = new We902001Service(appContext);  /* 202001 ����ó�� */
                        
                        saveRet = chkEnvWeTax.chkweTax(dataMap);
                        
                    } else if ("5".equals(strTaxGubun)) { //���ϼ���
                   
                       We952001Service chkWaterWeTax = new We952001Service(appContext);  /* 202001 ����ó�� */
                       
                       saveRet = chkWaterWeTax.chkweTax(dataMap);
                   
                    } else { // �׿� ��ݱ����� ����ó��
                        
                        rtnList.setField("RSLT_COD"     , "44000-101");
                        rtnList.setField("RSLT_MESSAGE" , "���ι�ȣ �����Դϴ�");
                    }
                    
                    retBuffer = rtnList.CYB992001(saveRet);
                    
                }
                
                
            } else {
                /*
                 * �׿� �ٸ� ���������� �� ��� ����ó��...
                 * */
                
                rtnList.setField("RSLT_COD"     , "44000-000");
                rtnList.setField("RSLT_MESSAGE" , "�ŷ������ڵ� �����Դϴ�");
                retBuffer = (byte[]) rtnList.getBuff();
                 
                log.error("============================================");
                log.error("== doDataRecv() Error End �ŷ������ڵ� ���� ==");
                log.error("============================================");
                
                return;

            }
            
            //���� �� [retBuffer] �� ������ �۽��ϰ� ������ �Ѵ�.
            //���� �̺�Ʈ ����
            
            /*���������� �ۼ��������� �����Ѵ�...*/
            logMap.setMap("RTN_MSG" , new String(retBuffer)); /*��������*/
            logMap.setMap("RES_CD"  , rtnList.getField("RSLT_COD"));     /*ó�����*/
            logMap.setMap("ERR_MSG" , rtnList.getField("RSLT_MESSAGE")); /*�����޽���*/
            logMap.setMap("REG_DTM" , CbUtil.getCurrent("yyyyMMddHHmmss"));
            logMap.setMap("LAST_DTM", CbUtil.getCurrent("yyyyMMddHHmmss"));
            
            if(sqlService_cyber.queryForUpdate("CODMBASE.CODM_JUNMUN_TXRX_INSERT", logMap) > 0) {
                log.info("�ۼ��� ���� ����Ϸ�!!!");
            }   
                

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    

    /*Ʈ����� ó��          ���� ������ �ʴµ� �� by ��â�� 20131114*/
    @Override
    public void transactionJob() {
        // TODO Auto-generated method stub
        
        MapForm logMap = new MapForm();
        
        String saveRet = "";
        
        try {
                        
            log.info("[=========== ���� transactionJob() :: Ʈ�����ó������ �Դϴ�. ========== ]");
            
            /*�ŷ����и� �Ľ��Ѵ�...(�ŷ������� �˱� ����)*/
            dataMap = chkList.parseBuff(recvBuffer);
            
            
            /* �ۼ��� ���� �����*/
            logMap.setMap("SYS_DSC"  , "03"); /*���ý�*/
            logMap.setMap("MSG_ID"   , dataMap.getMap("CO_TRAN")); /*��������*/
            logMap.setMap("RCV_MSG"  , new String(recvBuffer)); /*��������*/
            logMap.setMap("REG_DTM"  , CbUtil.getCurrent("yyyyMMddHHmmss"));
            
            if (((String) dataMap.getMap("CO_TRAN")).equals("532001")) { 
                /*
                 * ���������ǿ� ���� ���� ó��
                 * */
                
                saveRet = saveTeleGram((String) dataMap.getMap("CO_TRAN"), recvBuffer);  /*�������� ����*/
                
                if(saveRet.equals("")) {   /*�������� �����ΰ��*/
                    
                    dataMap = FL_532001.parseBuff(recvBuffer);      /*�������� �Ľ�(��ü)*/
    
                    if(log.isDebugEnabled()){
                        log.debug(dataMap); 
                    }
                    
                    // ī��������� ������ü���� ���� ��� �߰�
                    // ���� ������ �Ǵ��� �� �ִ� �ٰŴ� ī����ι�ȣ�� ���� �ִ��� ������ �̹Ƿ� ��ɷθ� �Ǵ��Ѵ�
                    // 20110719 ī��� ���о���...
                    if(!((String) dataMap.getMap("CARD_REQ_NO")).trim().equals("")) { /* ī����ι�ȣ�� ������ ī����� */
                        
                        dataMap.setMap("BNK_CD", ((String) dataMap.getMap("BANK_COD")).substring(0, 3));
                        
                    } else { /* �ƴϸ� ������ü ���� */
                        
                        dataMap.setMap("BNK_CD", ((String) dataMap.getMap("BANK_COD")).substring(0, 3));
                
                    }
    
                    // ���ݺ� ���� ó�� ����e���� �����Ͽ� �߰� 20131104
                    dataMap.setMap("CHARGE_GB", dataMap.getMap("EPAY_NO").toString().substring(5, 6));//���ڳ��ι�ȣ.��ݱ��� ù ���ڷ� ������ �����Ѵ�.
                    
                    log.debug("CHARGE_GB = " + dataMap.getMap("CHARGE_GB"));
                    
                    if (dataMap.getMap("CHARGE_GB").equals("1")) { //���漼
                        
                        We532001Service chkWeTax = new We532001Service(appContext);  /* 532001 ����ó�� */
                        
                        saveRet = chkWeTax.chkweTax(dataMap);
                        
                    } else if (dataMap.getMap("CHARGE_GB").equals("2")) { //���ܼ���
                        
                        We272001Service chkBnonWeTax = new We272001Service(appContext);  /* 272001 ����ó�� */
                        
                        saveRet = chkBnonWeTax.chkweTax(dataMap);
                        
                    } else if (dataMap.getMap("CHARGE_GB").equals("9")) { //ȯ�氳���δ��
                        
                        We202001Service chkEnvWeTax = new We202001Service(appContext);  /* 202001 ����ó�� */
                        
                        saveRet = chkEnvWeTax.chkweTax(dataMap);
                        
                    } else if (dataMap.getMap("CHARGE_GB").equals("0")) { //���ϼ���
                        
                        We252001Service chkWaterWeTax = new We252001Service(appContext);  /* 202001 ����ó�� */
                        
                        saveRet = chkWaterWeTax.chkweTax(dataMap);
                        
                    } else { //�׿� �ٸ� ��ݱ����� ����ó��
                        
                        rtnList.setField("RSLT_COD"     , "44000-000");
                        rtnList.setField("RSLT_MESSAGE" , "���ڳ��ι�ȣ ��ݱ����׸� �����Դϴ�");
                    }
    
                    retBuffer = rtnList.CYB532001(saveRet);
                }
                
            } else if (((String) dataMap.getMap("CO_TRAN")).equals("532002")) {
                /*
                 * �Ѱ������ǿ� ���� ���� ó��
                 * */
                log.info("[=========== �Ѱ������ǿ� ���� ���� ó��. ========== ]");
                
                saveRet = saveTeleGram((String) dataMap.getMap("CO_TRAN"), recvBuffer);  /*�������� ����*/
                
                if(saveRet.equals("")) {   /*�������� �����ΰ��*/
                    
                    dataMap = FL_532002.parseBuff(recvBuffer);
                    
                    if(log.isDebugEnabled()){
                        log.debug(dataMap); 
                    }
                    
                    if(!((String) dataMap.getMap("CARD_REQ_NO")).trim().equals("")) { /* ī����ι�ȣ�� ������ ī����� */
                        
                        dataMap.setMap("BNK_CD", ((String) dataMap.getMap("BANK_COD")).substring(0, 3));
                        
                    } else { /* �ƴϸ� ������ü ���� */
                        
                        dataMap.setMap("BNK_CD", ((String) dataMap.getMap("BANK_COD")).substring(0, 3));
                
                    }
                    
//                    We532002Service chkweTax = new We532002Service(appContext);  /* 532002 ����ó�� */
//                    
//                    saveRet = chkweTax.chkweTax(dataMap);
                    
                 // ���ݺ� ���� ó�� ����e���� �����Ͽ� �߰� 20131104
                    dataMap.setMap("CHARGE_GB", dataMap.getMap("EPAY_NO").toString().substring(1, 1));//���ڳ��ι�ȣ(�Ѱ����ΰ�����ȣ).ù ���ڷ� ������ �����Ѵ�.
                    
                    log.debug("CHARGE_GB = " + dataMap.getMap("CHARGE_GB"));
                    
                    if (dataMap.getMap("CHARGE_GB").equals("T")) { //���漼
                        
                        We532002Service chkWeTax = new We532002Service(appContext);  /* 532002 ����ó�� */
                        
                        saveRet = chkWeTax.chkweTax(dataMap);
                        
                    } else if (dataMap.getMap("CHARGE_GB").equals("V")) { //���ܼ���
                        
                        We272002Service chkBnonWeTax = new We272002Service(appContext);  /* 272002 ����ó�� */
                        
                        saveRet = chkBnonWeTax.chkweTax(dataMap);
                        
                    } else if (dataMap.getMap("CHARGE_GB").equals("U")) { //ȯ�氳���δ��
                        
                        We202002Service chkEnvWeTax = new We202002Service(appContext);  /* 202002 ����ó�� */
                        
                        saveRet = chkEnvWeTax.chkweTax(dataMap);
                        
                    } else if (dataMap.getMap("CHARGE_GB").equals("W")) { //���ϼ���
                        
                        We252002Service chkWaterWeTax = new We252002Service(appContext);  /* 202002 ����ó�� */
                        
                        saveRet = chkWaterWeTax.chkweTax(dataMap);
                        
                    } else { //�׿� �ٸ� ��ݱ����� ����ó��
                        
                        rtnList.setField("RSLT_COD"     , "44000-000");
                        rtnList.setField("RSLT_MESSAGE" , "�Ѱ����ι�ȣ �����׸� �����Դϴ�");
                    }
                    
                    retBuffer = rtnList.CYB532002(saveRet);
                    
                }
                
                
            } else if (((String) dataMap.getMap("CO_TRAN")).equals("992001")) {
                /*
                 * ��� ���� ó��
                 * */
                log.info("[=========== ��Ұǿ� ���� ���� ó��. ========== ]");
                
                saveRet = saveTeleGram((String) dataMap.getMap("CO_TRAN"), recvBuffer);  /*�������� ����*/
                
                if(saveRet.equals("")) {   /*�������� �����ΰ��*/
                    
                    dataMap = FL_992001.parseBuff(recvBuffer);
                    
                    if(log.isDebugEnabled()){
                        log.debug(dataMap); 
                    }
                    
//                  We992001Service chkweTax = new We992001Service(appContext);  /* 992001 ����ó�� */
//                  
//                  saveRet = chkweTax.chkweTax(dataMap);
                    
                    // ���ݺ� ���� ó�� ����e���� �����Ͽ� �߰� 20131104
//                    dataMap.setMap("CHARGE_GB", dataMap.getMap("EPAY_NO").toString().substring(1, 1));//���ڳ��ι�ȣ(�Ѱ����ΰ�����ȣ).ù ���ڷ� ������ �����Ѵ�.
                    
                    log.debug("GIRO_NO = " + dataMap.getMap("GIRO_NO"));
                    strTaxGubun = (String) sqlService_cyber.queryForBean("CODMBASE.GIRO_FOR_TAX", dataMap.getMap("GIRO_NO"));
                    
                    if ("1".equals(strTaxGubun)) { //���漼
                        
                        We992001Service chkWeTax = new We992001Service(appContext);  /* 992001 ����ó�� */
                        
                        saveRet = chkWeTax.chkweTax(dataMap);
                        
                    } else if ("2".equals(strTaxGubun)) { //���ռ��ܼ���
                        
                        We972001Service chkBnonWeTax = new We972001Service(appContext);  /* 972001 ����ó�� */
                        
                        saveRet = chkBnonWeTax.chkweTax(dataMap);
                        
                    } else if ("3".equals(strTaxGubun)) { //ȯ�氳���δ��
                        
                        We902001Service chkEnvWeTax = new We902001Service(appContext);  /* 902001 ����ó�� */
                        
                        saveRet = chkEnvWeTax.chkweTax(dataMap);
                        
                    } else if ("5".equals(strTaxGubun)) { //���ϼ���
                        
                        We952001Service chkWaterWeTax = new We952001Service(appContext);  /* 202001 ����ó�� */
                        
                        saveRet = chkWaterWeTax.chkweTax(dataMap);
                    
                    } else { //�׿� �ٸ� ��ݱ����� ����ó��
                        
                        rtnList.setField("RSLT_COD"     , "44000-000");
                        rtnList.setField("RSLT_MESSAGE" , "�Ѱ����ι�ȣ �����׸� �����Դϴ�");
                    }
                    
                    retBuffer = rtnList.CYB992001(saveRet);
                    
                }
                
                
            } else {
                /*
                 * �׿� �ٸ� ���������� �� ��� ����ó��...
                 * */
                
                rtnList.setField("RSLT_COD"     , "44000-000");
                rtnList.setField("RSLT_MESSAGE" , "�ŷ������ڵ� �����Դϴ�");
                retBuffer = (byte[]) rtnList.getBuff();
                 
                log.error("============================================");
                log.error("== doDataRecv() Error End �ŷ������ڵ� ���� ==");
                log.error("============================================");
                
                return;

            }
            
            //���� �� [retBuffer] �� ������ �۽��ϰ� ������ �Ѵ�.
            //���� �̺�Ʈ ����
            
            /*���������� �ۼ��������� �����Ѵ�...*/
            logMap.setMap("RTN_MSG" , new String(retBuffer)); /*��������*/
            logMap.setMap("RES_CD"  , rtnList.getField("RSLT_COD"));     /*ó�����*/
            logMap.setMap("ERR_MSG" , rtnList.getField("RSLT_MESSAGE")); /*�����޽���*/
            logMap.setMap("REG_DTM" , CbUtil.getCurrent("yyyyMMddHHmmss"));
            logMap.setMap("LAST_DTM", CbUtil.getCurrent("yyyyMMddHHmmss"));
            
            if(sqlService_cyber.queryForUpdate("CODMBASE.CODM_JUNMUN_TXRX_INSERT", logMap) > 0) {
                log.info("�ۼ��� ���� ����Ϸ�!!!");
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
