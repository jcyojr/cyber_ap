/**
 *  �ֽý��۸� : ���̹����漼û ��ȭ
 *  ��  ��  �� : ����
 *  ��  ��  �� : ���ý� ȸ�������� �����ϰ� DB�� �����Ѵ�...
 *               com.uc.bs.cyber.etax.* �ڿ����
 *  Ŭ����  ID : Txdm2540 
 *  ����  �̷� :
 * ------------------------------------------------------------------------
 *  �ۼ���      	�Ҽ�  		    ����            Tag           ����
 * ------------------------------------------------------------------------
 *  �۵���        ��ä��(��)      2011.06.26         %01%         �����ۼ�
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
	 * ������
	 */
	public Txdm2540() {
		super();
		// TODO Auto-generated constructor stub

		log.info("============================================");
		log.info("== new RcvWorker( )                 Start ==");
		log.info("============================================");
	}
	
	/* 
	 * ������ �ʱ�ȭ��
	 * */
	public Txdm2540(int annotation) throws Exception {
		// TODO Auto-generated constructor stub
        // nothing...
	}	

	/*
	 * Ʈ����ǿ�(������)
	 * */
	public Txdm2540(ApplicationContext context, String dataSource) throws Exception {
		
		/*Context ����...*/
		this.context = context;
		this.dataSource = dataSource;
		
	}
	
	/*
	 * ������
	 * */
	public Txdm2540(ApplicationContext context) throws Exception {
		
		/*Context ����...*/
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
			
			/*Log4j ����*/
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
	
	/* ���� ����...*/
	private void startServer() throws Exception {
		
		/**
		 * SERVER START
		 * port     : Listen Port
         * worker   : Worker ������(class)
         * maxQue   : �ִ� Queue ũ��
    	 * procCnt  : ���μ���(������) ����
		 */
		
		int connPort =  Utils.getResourceInt("ApplicationResource", "wetax.recv_mem.port");  /*9831*/
		
		RcvServer server = new RcvServer(connPort, "com.uc.bs.cyber.daemon.txdm2540.Txdm2540", 10, 2, "log4j.xml", 0, com.uc.core.Constants.TYPENOHEAD);
		
		server.setContext(appContext);
		server.setProcId("2540");
		server.setProcName("���ý� ȸ����������");
		server.setThreadName("thr_2540");
		server.initial();
		
		comdThread = new Thread(server);
		
		/**
		 * ������ �ۼ����� ���ؼ� ������ ������ش�
		 * (������������)�� ���ؼ� Threading...
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
		 * �������� �ʵ� ����
		 */
		this.msgField = new FieldList();
		
		msgField.add("RSLT_COD"  ,   	9,    "C");    // ����ڵ�
		msgField.add("RSLT_MSG"  ,     50,    "C");    // ����޽���
		
		if(buffer.length >= 1500) {
			
			try {
				
				TD_2540 = new Txdm2540FieldList();        /*�ʱ�ȭ*/        
				
				dataMap = TD_2540.parseBuff(buffer);      /*�������� �Ľ�(�κ�)*/
				
				/* �ۼ��� ���� �����*/
				logMap.setMap("SYS_DSC"  , "03"); /*���ý�*/
				logMap.setMap("MSG_ID"   , "Txdm2540"); /*��������*/
				logMap.setMap("RCV_MSG"  , new String(buffer)); /*��������*/
				logMap.setMap("REG_DTM"  , CbUtil.getCurrent("yyyyMMddHHmmss"));
				
				if (log.isDebugEnabled()) {
					log.debug("dataMap = " + dataMap.getMaps());
				}
				
                //usr_div���� ���ǵ��� ���� ��('90')�� ���ͼ� �Ǹ����� �κп��� NullPointerException �߻��ؼ� ����.
				//'90'�� ������ ���а����� ������ ������ �������� �ʴ� �� ���絵 ������ �� �ִٰ� �Ͽ� ó�� ���� �ǵ��� ������ ���� ó����.
				//2013.12.05 by ��â��(���ؽ� ����ǥ ����� ��ȭ��)
                String[] usrDiv = new String[2];
                
				if (dataMap.getMap("usr_div").equals("01")) { //������

					TD_2540.Txdm2540_Pri_FieldList();
					
					dataMap = TD_2540.parseBuff(buffer); /*�ٽ��ѹ��� �Ļ��ؼ� ���θ��� �����Ѵ�.*/
					
					dataMap.setMap("gbn"  , "Kor");
					dataMap.setMap("sname", dataMap.getMap("usr_nm"));
					dataMap.setMap("plgn" , "1");
					
					usrDiv[0] = "24140-000";
					usrDiv[1] = "����";
					
				}else if (dataMap.getMap("usr_div").equals("02")) { //�ܱ���
					
					TD_2540.Txdm2540_Pri_FieldList();
					
					dataMap = TD_2540.parseBuff(buffer); /*�ٽ��ѹ��� �Ļ��ؼ� ���θ��� �����Ѵ�.*/
					
					dataMap.setMap("gbn"  , "Frn");
					dataMap.setMap("sname", dataMap.getMap("usr_nm"));
					dataMap.setMap("plgn" , "1");
					
					usrDiv[0] = "24140-000";
					usrDiv[1] = "����";

				}else if (dataMap.getMap("usr_div").equals("21")) { //����
					
					if(recvlen == 1732) {
						TD_2540.Txdm2540_Crp_FieldList();
					}else{
						TD_2540.Txdm2540_Crp_short_FieldList();
					}

					dataMap = TD_2540.parseBuff(buffer); /*�ٽ��ѹ��� �Ļ��ؼ� ���θ��� �����Ѵ�.*/
					
					dataMap.setMap("gbn"  , "Crp");
					dataMap.setMap("sname", dataMap.getMap("comp_nm"));
					dataMap.setMap("plgn" , "2");
					
					usrDiv[0] = "24140-000";
					usrDiv[1] = "����";

				} else {
				    dataMap = new MapForm();
                    dataMap.setMap("RSLT_COD", "24130-301");
                    dataMap.setMap("RSLT_MSG", "����� �����Է°��� ���ǵ��� ���� ���Դϴ�.");
                    
                    usrDiv[0] = "24130-301";
                    usrDiv[1] = "����� �����Է°��� ���ǵ��� ���� ���Դϴ�.";
				}
				
				if(usrDiv[0].equals("24140-000")){
    				/**
    				 * ȸ������ �Ǹ����� ���� 
    				 */
    				ParamVO param = new ParamVO();
    				
    				param.put("REQ_TYPE", dataMap.getMap("gbn"));     //����
    				param.put("CHK_NAME", dataMap.getMap("sname"));   //�̸�/����
    				param.put("CHK_NO"  , dataMap.getMap("tpr_no"));  //�ֹι�ȣ/���ι�ȣ
				
				    //String[] weTaxChk = toWetaxSend(param);   //adaptor �� ���ؼ� ���ý��� ����
				
                    //DEBUGGING
                    String[] weTaxChk = {"24140-000", "����"};
    				
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
    						
    						/*��ϵ� ����ڰ� ���ٸ�*/
    						if (RegisterYn(dataMap.getMap("tpr_no").toString()) == 0) {
    							
    							/*�ʱⰪ ����*/
    							if(!(dataMap.getMap("email_yn").equals("1") || dataMap.getMap("email_yn").equals("0"))) {
    								dataMap.setMap("email_yn", dataMap.getMap("email_yn").equals("Y") ? "1" : "0");
    							}
    						
    							dataMap.setMap("comp_nm", "");
    							dataMap.setMap("tpr_no2", "");
    
    							log.info("----------�����ڷ�----------");
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
    							
    							if (dataMap.getMap("usr_div").equals("21")) { //����
    								log.info("------------����------------");
    								log.info("remrk    = " + dataMap.getMap("remrk").toString());
    								log.info("comp_nm  = " + dataMap.getMap("comp_nm").toString());
    								log.info("tpr_no2  = " + dataMap.getMap("tpr_no2").toString());
    							}
    							log.info("----------�Է½���----------");
    						
    							if (sqlService_cyber.queryForUpdate("TXDM2540.INSERT_ME1101_TB_FOR_WETAX_MEMBER", dataMap) > 0) {
    								dataMap = new MapForm();
    								dataMap.setMap("RSLT_COD", "24130-000");
    								dataMap.setMap("RSLT_MSG", "ȸ��������ϼ���");
    							} else {
    								dataMap = new MapForm();
    								dataMap.setMap("RSLT_COD", "24130-201");
    								dataMap.setMap("RSLT_MSG", "���� �ý��� DB ��ַ� ���� �Ұ�");
    							}
    							
    						} else {
    							
    							if (sqlService_cyber.queryForUpdate("TXDM2540.UPDATE_ME1101_TB_FOR_WETAX_MEMBER", dataMap) > 0) {
    								log.info("���ý� ȸ������ �����Ϸ�!");
    								
    								dataMap = new MapForm();
    								dataMap.setMap("RSLT_COD", "24130-000");
    								dataMap.setMap("RSLT_MSG", "ȸ��������ϼ���");
    								
    							} else {
    								dataMap = new MapForm();
    								dataMap.setMap("RSLT_COD", "24130-201");
    								dataMap.setMap("RSLT_MSG", "���� �ý��� DB ��ַ� ���� �Ұ�");
    								
    								//dataMap = new MapForm();
    								//dataMap.setMap("RSLT_COD", "24130-001");
    								//dataMap.setMap("RSLT_MSG", "������ ��ϵ� ȸ���Դϴ�");
    							}
    
    						}
    
    						
    					}catch (Exception e){
    						
    						e.printStackTrace();
    						
    						/*�ߺ��� �߻��ϰų� �������ǿ� ��߳��� ���üũ*/
    						if (e instanceof DuplicateKeyException){
    							
    							if (sqlService_cyber.queryForUpdate("TXDM2540.UPDATE_ME1101_TB_FOR_WETAX_MEMBER", dataMap) > 0) {
    								log.info("���ý� ȸ������ �����Ϸ�!");
    							} 
    							
    						}else{
    							throw e;
    						}
    						
    					}
    					
    				} else if(weTaxChk[0].equals("24140-101")){
    					
    					dataMap = new MapForm();
    					dataMap.setMap("RSLT_COD", "24140-101");
    					dataMap.setMap("RSLT_MSG", "�Ǹ� ������ �����߽��ϴ�");
    
    				} else if(weTaxChk[0].equals("24140-102")){
    					
    					dataMap = new MapForm();
    					dataMap.setMap("RSLT_COD", "24140-102");
    					dataMap.setMap("RSLT_MSG", "�Ǹ� �������� �̻����� �Ǹ����� Ȯ���� �Ұ��մϴ�");
    					
    				} else if(weTaxChk[0].equals("24140-301")){
    					
    					dataMap = new MapForm();
    					dataMap.setMap("RSLT_COD", "24140-301");
    					dataMap.setMap("RSLT_MSG", "����� �����Է°��� ���ǵ��� ���� ���Դϴ�");
    					
    				} else if(weTaxChk[0].equals("24140-302")){
    					
    					dataMap = new MapForm();
    					dataMap.setMap("RSLT_COD", "24140-302");
    					dataMap.setMap("RSLT_MSG", "�ʼ� �Է°��� ����ֽ��ϴ�");
    				}
				} else {
                    
                    dataMap = new MapForm();
                    dataMap.setMap("RSLT_COD", "24140-301");
                    dataMap.setMap("RSLT_MSG", "����� �����Է°��� ���ǵ��� ���� ���Դϴ�");
				}
				
			} catch (Exception e) {
				// �ý��� �����Դϴ�
				e.printStackTrace();
				
				dataMap = new MapForm();
				
				dataMap.setMap("RSLT_COD", "24110-201");
				dataMap.setMap("RSLT_MSG", "���νý��� ������ �߻��߽��ϴ�");
			}
			
			
		} else {
			
			dataMap = new MapForm();
			
			dataMap.setMap("RSLT_COD", "24110-102");
			dataMap.setMap("RSLT_MSG", "��û�� ȸ���������� ������ �����Դϴ�");
			
		}
		
		this.retBuffer = msgField.makeMessageByte(dataMap)	;
		
		logMap.setMap("RTN_MSG" , new String(retBuffer)); /*��������*/
		logMap.setMap("RES_CD"  , ""); /*ó�����*/
		logMap.setMap("ERR_MSG" , ""); /*�����޽���*/
		logMap.setMap("REG_DTM" , CbUtil.getCurrent("yyyyMMddHHmmss"));
		logMap.setMap("LAST_DTM", CbUtil.getCurrent("yyyyMMddHHmmss"));
		
		if(sqlService_cyber.queryForUpdate("CODMBASE.CODM_JUNMUN_TXRX_INSERT", logMap) > 0) {
			log.info("�ۼ��� ���� ����Ϸ�!!!");
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
	 * ���ý��� ���� �����͸� �����ϴ� �ƴ���
	 * @param paramVO
	 * @throws Exception
	 */
	public String[] toWetaxSend(ParamVO paramVO) throws Exception{

		// �ʱ�ȭ �������� ������ ���� �ѹ��� ������ ���ֽø� �˴ϴ�.
		System.setProperty("ITLS.DIR", CbUtil.getResourcePath(this, ""));
		
		log.info("ITLS.DIR = " + CbUtil.getResourcePath(this, ""));
		
		// ���ý����� ������ �����Ͱ� ����� ���� ����
		ResultVO2 resultVO = null;
		String sRetCode    = "";
		String sRetMsg     = "";
		String rt_msg[]    = new String[2];
		
		try {

			// ���ý��� �����ý� ȸ���������š� ����(SIDO.MEMBER01) ȣ��
			log.info("���ý� ����(LCIJ.LCIJR04) ȣ��...");
			resultVO = (ResultVO2) ConnectionManager2.execute("LCIJ.LCIJR04", paramVO);

			// ���ý� ���� ���� ����/���� ���� Ȯ��
			if (!resultVO.isSuccess()) {
				//���ý� ���� ���� ���� �� ���� �޽����� ����ϴ� ����
				System.out.println("�޼��� : " + resultVO.getMessage());
				log.error("�޼��� : " + resultVO.getMessage());

				rt_msg[0] = "24100-401";
				rt_msg[1] = "���ý� �������� ����";
			}
			
			// ���ý� ���� ���� ���� ���� Ȯ��
			if (resultVO.isOk()) {
				sRetCode = (String)(resultVO.getObject("RSLT_COD"));
				sRetMsg  = (String)(resultVO.getObject("RSLT_MESSAGE"));
				//���ΰ�� ������ �������� ��� ó�� ���� ����

				log.info("���ý� ����ȣ��(" + sRetCode + ") " + sRetMsg);

				rt_msg[0] = sRetCode;
				rt_msg[1] = sRetMsg;
				
				if(!sRetCode.equals("44130-000")) {

					System.out.println("���ý� ����ȣ�� ����[" + sRetCode + "] " + sRetMsg);
					log.error("���ý� ����ȣ�� ����[" + sRetCode + "] " + sRetMsg);
					
					rt_msg[0] = "44130-000";
					rt_msg[1] = "���ý� ����ȣ�� ����";
					
				}

			}// ���ý� ���� ����
			else {
				System.out.println("Fail");

				System.out.println("[441030-402] ���ý� ���� ����");
				log.error("[441030-402] ���ý� ���� ����");
				
				rt_msg[0] = "441030-402";
				rt_msg[1] = "���ý� ���� ����";
				
			}
			
		} catch (Exception e) {
			e.printStackTrace();

			log.error("WeTax ���� ���� ::: " + e.getMessage());

			rt_msg[0] = "441030-402";
			rt_msg[1] = "���ý� ���� ����";
			
		}
		
		return rt_msg;
	}
	
	
	/*Ʈ������� �����ϱ� ���� �Լ�.
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
	

	/*Ʈ����� ó��*/
	@Override
	public void transactionJob() {
		// TODO Auto-generated method stub

		try {
			
			log.info("[=========== ���� transactionJob() :: Ʈ�����ó������ �Դϴ�. ========== ]");
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	}
	
	/*----------------------------------------------*/
	/* �Ϸù�ȣ �������� �׽�Ʈ*/
	/*----------------------------------------------*/
    @SuppressWarnings("unused")
	private int SeqNumber(){
    	return (Integer)sqlService_cyber.queryForBean("CODMBASE.CODM_GETSEQNO", "00");
    }
    
    /*��Ͽ���Ȯ��*/
    private int RegisterYn(String reg_no){
    	return (Integer)sqlService_cyber.queryForBean("TXDM2540.SELECT_MEMBER_INFO", reg_no);
    }
    
    
}
