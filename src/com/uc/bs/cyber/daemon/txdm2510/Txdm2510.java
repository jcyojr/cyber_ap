/**
 *  �ֽý��۸� : ���̹����漼û ��ȭ
 *  ��  ��  �� : ����
 *  ��  ��  �� : ������ ���輭���� �⵿�ϰ� �ۼ��ŵ����͸� ó���Ѵ�...
 *               ������ ��û �� �����ڷḦ �����Ѵ�.
 *               com.uc.bs.cyber.etax.* �ڿ����
 *  Ŭ����  ID : Txdm2510 
 *  ����  �̷� :
 * ------------------------------------------------------------------------
 *  �ۼ���      	�Ҽ�  		    ����            Tag           ����
 * ------------------------------------------------------------------------
 *  �۵���      ��ä��(��)         2011.06.07         %01%         �����ۼ�
 *  ��â��      ��ä��(��)         2013.07.02         %02%         �ּ��߰� �������  ����
 */
package com.uc.bs.cyber.daemon.txdm2510;

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

//EGTOB FRAME �� �̿��ϱ� ����.
//import com.uc.egtob.net.server.RcvWorker;
//import com.uc.egtob.net.server.RcvServer;

public class Txdm2510 extends RcvWorker{
	
	private IbatisBaseService sqlService_cyber = null;
	private ApplicationContext appContext = null;
	private Thread comdThread = null;
	
	private Comd_WorkKfField MSG_HEAD = new Comd_WorkKfField();
	
	
//	private Kf201001FieldList KFL_201001 = new Kf201001FieldList();  //ȯ�氳���δ�� �������� ������ȸ                                      
//	private Kf201002FieldList KFL_201002 = new Kf201002FieldList();  //ȯ�氳���δ�� �������� ����ȸ                                      
//	private Kf202001FieldList KFL_202001 = new Kf202001FieldList();  //ȯ�氳���δ�� ���ΰ�� ����                                        
//	private Kf203001FieldList KFL_203001 = new Kf203001FieldList();  //ȯ�氳���δ�� ���γ��� ������ȸ                                      
//	private Kf203002FieldList KFL_203002 = new Kf203002FieldList();  //ȯ�氳���δ�� ���γ��� ����ȸ                                      
	                                                                                                                  
	private Kf251001FieldList KFL_251001 = new Kf251001FieldList();  //���ϼ��� �������� ������ȸ                                         
	private Kf251005FieldList KFL_251005 = new Kf251005FieldList();  //���ϼ��� �������� ������ȸ 2 (���ڳ��ι�ȣ�߰���)           
	private Kf251002FieldList KFL_251002 = new Kf251002FieldList();  //���ϼ��� �������� ����ȸ                                         
	private Kf252001FieldList KFL_252001 = new Kf252001FieldList();  //���ϼ��� ���ΰ�� ����                                           
	private Kf253001FieldList KFL_253001 = new Kf253001FieldList();  //���ϼ��� ���γ��� ������ȸ                                         
	private Kf253002FieldList KFL_253002 = new Kf253002FieldList();  //���ϼ��� ���γ��� ����ȸ                                         
	                                                                                                                
	private Kf271001FieldList KFL_271001 = new Kf271001FieldList();  //���ܼ��� �������� ������ȸ                                         
	private Kf271002FieldList KFL_271002 = new Kf271002FieldList();  //���ܼ��� �������� ����ȸ                                         
	private Kf272001FieldList KFL_272001 = new Kf272001FieldList();  //���ܼ��� ���ΰ�� ����                                           
	private Kf273001FieldList KFL_273001 = new Kf273001FieldList();  //���ܼ��� ���γ��� ������ȸ                                         
	private Kf273002FieldList KFL_273002 = new Kf273002FieldList();  //���ܼ��� ���γ��� ����ȸ                                         
	private Kf276001FieldList KFL_276001 = new Kf276001FieldList();  //���ܼ��� �ϰ����ΰ�� �뺸                                         
	                                                                                                                
//	private Bs521001FieldList BSL_521001 = new Bs521001FieldList();  //���漼 �������� ������ȸ                                          
//	private Bs521002FieldList BSL_521002 = new Bs521002FieldList();  //���漼 �������� ����ȸ                                          
//	private Bs523001FieldList BSL_523001 = new Bs523001FieldList();  //���漼 ���γ��� ������ȸ                                          
//	private Bs523002FieldList BSL_523002 = new Bs523002FieldList();  //���漼 ���γ��� ����ȸ                                          
	private Bs992001FieldList BSL_992001 = new Bs992001FieldList();  //���漼 ���� �����                                             
//	private Bs502001FieldList BSL_502001 = new Bs502001FieldList();  //���漼 ���ΰ�� ����                                            
//	private Bs502002FieldList BSL_502002 = new Bs502002FieldList();	 //���漼 ���ΰ��(�������Ա�) ����                                     
//	private Bs502101FieldList BSL_502101 = new Bs502101FieldList();  //���� ���γ��� ���� �������� (���ǻ���) ���� ������ ���漼�� ���γ����� ������ �� �ִ� �� ����.
	
	private MapForm dataMap    = null;
	@SuppressWarnings("unused")
	private String dataSource  = null;

	private byte[] recvBuffer;
	
	/* ������ 
	 * QUEUE ��
	 * */
	public Txdm2510() throws Exception {
		super();
		// TODO Auto-generated constructor stub

		log.info("============================================");
		log.info("== new RcvWorker( )                 Start ==");
		log.info("============================================");

		
	}
	
	/* ������ 
	 * �ʱ�ȭ��
	 * */
	public Txdm2510(int annotation) throws Exception {
		// TODO Auto-generated constructor stub
        // nothing...
		
	}	
	
	/*
	 * Ʈ����ǿ�(������)
	 * */
	public Txdm2510(ApplicationContext context, String dataSource) throws Exception {
		
		/*Context ����...*/
		this.context = context;
		this.dataSource = dataSource;
		
	}
			
	/*������*/
	public Txdm2510(ApplicationContext context) throws Exception {
		
		/*Context ����...*/
		setAppContext(context);
		
		sqlService_cyber = (IbatisBaseService) this.appContext.getBean("baseService_cyber");
			
		startServer();
		
	}

	/*=====================================================*/
	/*������ �����Ͻÿ�....................................*/
	/*=====================================================*/
	/* RcvWorker ���� ���ŵ� ����, ���ŵ����͸� ó���Ѵ�...
	 * �߻�޼��� ���� */
	@Override
	public void doDataRecv(SocketChannel socket, byte[] buffer)	throws Exception {
		// TODO Auto-generated method stub
		
		log.info("============================================");
		log.info("== doDataRecv(" + socket.socket().getInetAddress() + ") Start ==");
		log.info(" RECV[" + new String(buffer) + "]");
		log.info("============================================");
		
		CbUtil cUtil = CbUtil.getInstance();
		
		MapForm cmMap;
		MapForm logMap = new MapForm();
		recvBuffer = null;
		
		byte[] chkJummun = new byte[74];  //üũ�� ����(��������ũ�⸸ŭ)

		try {
			
			/*
			 * ����ó��...(Ʈ�����ó��)
			 * */
			recvBuffer =  buffer;
			
			if(recvBuffer.length < 74) {
				//74byte ��ŭ ä���� ������ �����Ѵ�...				
				StringBuffer sb = new StringBuffer();
				
				for(int i = 0 ; i < 74 - recvBuffer.length ; i++){
					sb.append("0");
				}
				System.arraycopy(sb.toString().getBytes(), 0, chkJummun, buffer.length, 74 - buffer.length);
				
			} else {
				System.arraycopy(buffer, 0, chkJummun, 0, 74);
			}
						
			/* �������м� */
			cmMap = MSG_HEAD.parseHeadBuffer(chkJummun);
			
			cmMap.setMap("PRC_GB", "KP");   /*������ ó�� ���� ����*/
			
			log.debug("cmMap = " + cmMap.getMaps());
			
			/* �ۼ��� ���� �����*/
			logMap.setMap("SYS_DSC"  , "01"); /*������*/
			logMap.setMap("MSG_ID"   , cmMap.getMap("PROGRAM_ID")); /*��������*/
			logMap.setMap("RCV_MSG"  , new String(buffer)); /*��������*/
			logMap.setMap("REG_DTM"  , CbUtil.getCurrent("yyyyMMddHHmmss"));
			
			/* �м��� ������ ���� ���α׷��� �б��Ѵ�. */
			if(cmMap.getMap("TX_GUBUN").equals("200")) {  /*������� ��û*/
				
//				if(cmMap.getMap("PROGRAM_ID").equals("201001")){ // ȯ�氳���δ�� ��������������ȸ :: �ݰ��-->���̹�(����ü) 
//					
//					if(cmMap.getMap("RS_FLAG").equals("C") || cmMap.getMap("RS_FLAG").equals("B")){ //������ �Ǵ� �λ������� ������ ���γ��α��(��:������)
//						
//						dataMap = KFL_201001.parseRecvBuffer(buffer);  /*���������Ľ�...*/
//						
//						log.debug("dataMap = " + dataMap.getMaps());
//						
//						Kf201001Service kf_201001 = new Kf201001Service(appContext);  /* 201001 ����ó�� */
//						
//						/*���������� ó���ϰ� ����������  �����Ѵ�.*/
//						retBuffer = kf_201001.chk_kf_201001(cmMap, dataMap);
//						
//						if(log.isDebugEnabled()){
//							log.debug("retBuffer = " + new String(retBuffer));
//						}
//						
//					} else { 
//						
//						retBuffer = mkErrJunmun(cmMap, buffer);
//					}
//					
//				}else if(cmMap.getMap("PROGRAM_ID").equals("201002")){ // ȯ�氳���δ�� ������������ȸ :: �ݰ��-->���̹�(����ü)
//					
//					if(cmMap.getMap("RS_FLAG").equals("C") || cmMap.getMap("RS_FLAG").equals("B")){ //������
//						
//						dataMap = KFL_201002.parseRecvBuffer(buffer);  /*���������Ľ�...*/
//						
//						log.debug("dataMap = " + dataMap.getMaps());
//						
//						Kf201002Service kf_201002 = new Kf201002Service(appContext);  /* 201002 ����ó�� */
//						
//						retBuffer = kf_201002.chk_kf_201002(cmMap, dataMap);
//						
//						if(log.isDebugEnabled()){
//							log.debug("retBuffer = " + new String(retBuffer));
//						}
//						
//					} else { 
//						
//					}
//					
//				}else if(cmMap.getMap("PROGRAM_ID").equals("202001")){ // ȯ�氳���δ�� ���ΰ������ / �����û :: �ݰ��-->���̹�(����ü)
//					
//					if(cmMap.getMap("RS_FLAG").equals("C") || cmMap.getMap("RS_FLAG").equals("B")){ //������
//						
//						dataMap = KFL_202001.parseRecvBuffer(buffer);  /*���������Ľ�...*/
//						
//						log.debug("dataMap = " + dataMap.getMaps());
//						
//						Kf202001Service kf_202001 = new Kf202001Service(appContext);  /* 202001 ����ó�� */
//						
//						retBuffer = kf_202001.chk_kf_202001(cmMap, dataMap);
//						
//					} else { 
//						
//						retBuffer = mkErrJunmun(cmMap, buffer);
//					}
//					
//					
//				}else if(cmMap.getMap("PROGRAM_ID").equals("203001")){ // ȯ�氳���δ�� ���γ���������ȸ :: �ݰ��-->���̹�(����ü)
//					
//					if(cmMap.getMap("RS_FLAG").equals("C") || cmMap.getMap("RS_FLAG").equals("B")){ //������ �Ǵ� �λ������� ������ ���γ��α��(��:������)
//						
//						dataMap = KFL_203001.parseRecvBuffer(buffer);  /*���������Ľ�...*/
//						
//						log.debug("dataMap = " + dataMap.getMaps());
//						
//						Kf203001Service kf_203001 = new Kf203001Service(appContext);  /* 203001 ����ó�� */
//						
//						retBuffer = kf_203001.chk_kf_203001(cmMap, dataMap);
//						
//					} else { //�λ�����
//						
//						retBuffer = mkErrJunmun(cmMap, buffer);
//					}
//					
//				}else if(cmMap.getMap("PROGRAM_ID").equals("203002")){ // ȯ�氳���δ�� ���γ�������ȸ :: �ݰ��-->���̹�(����ü)
//					
//					if(cmMap.getMap("RS_FLAG").equals("C") || cmMap.getMap("RS_FLAG").equals("B")){ //������
//						
//						dataMap = KFL_203002.parseRecvBuffer(buffer);  /*���������Ľ�...*/
//						
//						log.debug("dataMap = " + dataMap.getMaps());
//						
//						Kf203002Service kf_203002 = new Kf203002Service(appContext);  /* 203002 ����ó�� */
//						
//						retBuffer = kf_203002.chk_kf_203002(cmMap, dataMap);
//						
//					} else { 
//						
//						retBuffer = mkErrJunmun(cmMap, buffer);
//					}

//				}else 
				if(cmMap.getMap("PROGRAM_ID").equals("251001")){ // ���ϼ��� ��������������ȸ :: �ݰ��-->���̹�(����ü)
					
					if(cmMap.getMap("RS_FLAG").equals("C") || cmMap.getMap("RS_FLAG").equals("B")){ //������
						
						dataMap = KFL_251001.parseRecvBuffer(buffer);  /*���������Ľ�...*/
						
						log.debug("dataMap = " + dataMap.getMaps());
						
						Kf251001Service kf_251001 = new Kf251001Service(appContext);  /* 251001 ����ó�� */
						
						retBuffer = kf_251001.chk_kf_251001(cmMap, dataMap);
						
					} else { 
						
						retBuffer = mkErrJunmun(cmMap, buffer);
					}
					
					
				}else if(cmMap.getMap("PROGRAM_ID").equals("251005")){ // ���ϼ��� ��������������ȸ 2 :: �ݰ��-->���̹�(����ü)
					
					if(cmMap.getMap("RS_FLAG").equals("C") || cmMap.getMap("RS_FLAG").equals("B")){ //������
						
						dataMap = KFL_251005.parseRecvBuffer(buffer);  /*���������Ľ�...*/
						
						log.debug("dataMap = " + dataMap.getMaps());
						
						Kf251005Service kf_251005 = new Kf251005Service(appContext);  /* 251005 ����ó�� */
						
						retBuffer = kf_251005.chk_kf_251005(cmMap, dataMap);
						
					} else { 
						
						retBuffer = mkErrJunmun(cmMap, buffer);
					}
					
					
				}else if(cmMap.getMap("PROGRAM_ID").equals("251002")){ // ���ϼ��� ������������ȸ :: �ݰ��-->���̹�(����ü)

					if(cmMap.getMap("RS_FLAG").equals("C") || cmMap.getMap("RS_FLAG").equals("B")){ //������
						
						dataMap = KFL_251002.parseRecvBuffer(buffer);  /*���������Ľ�...*/
						
						log.debug("dataMap = " + dataMap.getMaps());
						
						Kf251002Service kf_251002 = new Kf251002Service(appContext);  /* 251002 ����ó�� */
						
						retBuffer = kf_251002.chk_kf_251002(cmMap, dataMap);
						
					} else { 
						
						retBuffer = mkErrJunmun(cmMap, buffer);
					}
					
					
				}else if(cmMap.getMap("PROGRAM_ID").equals("252001")){ // ���ϼ������ ���ΰ�� ���� :: �ݰ��-->���̹�(����ü)
					
					if(cmMap.getMap("RS_FLAG").equals("C") || cmMap.getMap("RS_FLAG").equals("B")){ //������
						
						dataMap = KFL_252001.parseRecvBuffer(buffer);  /*���������Ľ�...*/
						
						log.debug("dataMap = " + dataMap.getMaps());
						
						Kf252001Service kf_252001 = new Kf252001Service(appContext);  /* 252001 ����ó�� */
						
						retBuffer = kf_252001.chk_kf_252001(cmMap, dataMap);
						
					} else { 
						
						retBuffer = mkErrJunmun(cmMap, buffer);
					}
					
				}else if(cmMap.getMap("PROGRAM_ID").equals("253001")){ // ���ϼ��� ���γ��� ������ȸ 
				
					if(cmMap.getMap("RS_FLAG").equals("C") || cmMap.getMap("RS_FLAG").equals("B")){ //������
						
						dataMap = KFL_253001.parseRecvBuffer(buffer);  /*���������Ľ�...*/
						
						log.debug("dataMap = " + dataMap.getMaps());
						
						Kf253001Service kf_253001 = new Kf253001Service(appContext);  /* 253001 ����ó�� */
						
						retBuffer = kf_253001.chk_kf_253001(cmMap, dataMap);
						
					} else { 
						
						retBuffer = mkErrJunmun(cmMap, buffer);
					}
					
				}else if(cmMap.getMap("PROGRAM_ID").equals("253002")){ // ���ϼ������γ��� ����ȸ 
					
					if(cmMap.getMap("RS_FLAG").equals("C") || cmMap.getMap("RS_FLAG").equals("B")){ //������
						
						dataMap = KFL_253002.parseRecvBuffer(buffer);  /*���������Ľ�...*/
						
						log.debug("dataMap = " + dataMap.getMaps());
						
						Kf253002Service kf_253002 = new Kf253002Service(appContext);  /* 253001 ����ó�� */
						
						retBuffer = kf_253002.chk_kf_253002(cmMap, dataMap);
						
					} else { 
						
					}
				
				}else if(cmMap.getMap("PROGRAM_ID").equals("271001")){ // ���ܼ��� �������� ��ȸ ���� :: �ݰ��-->���̹�(����ü)   || �λ����� --> �ݰ�������н� -->���̹�
					
					/*���������� -> �������� -> by pass �ϰ� ����*/
					if(cmMap.getMap("RS_FLAG").equals("C") || cmMap.getMap("RS_FLAG").equals("B")){ //������
						
						dataMap = KFL_271001.parseRecvBuffer(buffer);  /*���������Ľ�...*/
						
						log.debug("dataMap = " + dataMap.getMaps());
						
						Kf271001Service kf_271001 = new Kf271001Service(appContext);  /* 271001 ����ó�� */
						
						retBuffer = kf_271001.chk_kf_271001(cmMap, dataMap);

					}
					
					
				}else if(cmMap.getMap("PROGRAM_ID").equals("271002")){ // ���ܼ��� �������� ����ȸ ���� :: �ݰ��-->���̹�(����ü)
					
					if(cmMap.getMap("RS_FLAG").equals("C") || cmMap.getMap("RS_FLAG").equals("B")){ //������
						
						dataMap = KFL_271002.parseRecvBuffer(buffer);  /*���������Ľ�...*/
						
						log.debug("dataMap = " + dataMap.getMaps());
						
						Kf271002Service kf_271002 = new Kf271002Service(appContext);  /* 271002 ����ó�� */
						
						retBuffer = kf_271002.chk_kf_271002(cmMap, dataMap);
						
					} else { 
						
						
					}
					
				}else if(cmMap.getMap("PROGRAM_ID").equals("272001")){ // ���ܼ��� ���ΰ�� ���� ���� :: �ݰ��-->���̹�(����ü)
					
					if(cmMap.getMap("RS_FLAG").equals("C") || cmMap.getMap("RS_FLAG").equals("B")){ //������
						
						dataMap = KFL_272001.parseRecvBuffer(buffer);  /*���������Ľ�...*/
						
						log.debug("dataMap = " + dataMap.getMaps());
						
						Kf272001Service kf_272001 = new Kf272001Service(appContext);  /* 272001 ����ó�� */
						
						retBuffer = kf_272001.chk_kf_272001(cmMap, dataMap);
						
					} else { //�߸����ŵ� ����
						
						/* ���⼱ ������������ ���ŵ� ������ �����ϸ� �ȵ�
						 * ���� ������ �����Ͽ� �ִ���..�ƴ�..�׳� ������� �ؾ���.*/
						
						retBuffer = mkErrJunmun(cmMap, buffer);
						
					}
					
				}else if(cmMap.getMap("PROGRAM_ID").equals("273001")){ // ���ܼ��� ���γ��� ������ȸ ����
					
					if(cmMap.getMap("RS_FLAG").equals("C") || cmMap.getMap("RS_FLAG").equals("B")){ //������ �� �λ�����
						
						dataMap = KFL_273001.parseRecvBuffer(buffer);  /*���������Ľ�...*/
						
						log.debug("dataMap = " + dataMap.getMaps());
						
						Kf273001Service kf_273001 = new Kf273001Service(appContext);  /* 272001 ����ó�� */
						
						retBuffer = kf_273001.chk_kf_273001(cmMap, dataMap);
						
					} else { //�߸����ŵ� ����
						
						retBuffer = mkErrJunmun(cmMap, buffer);
					}
					
				}else if(cmMap.getMap("PROGRAM_ID").equals("273002")){ // ���ܼ��� ���γ��� ����ȸ ����
					
					if(cmMap.getMap("RS_FLAG").equals("C") || cmMap.getMap("RS_FLAG").equals("B")){ //������
						
						dataMap = KFL_273002.parseRecvBuffer(buffer);  /*���������Ľ�...*/
						
						log.debug("dataMap = " + dataMap.getMaps());
						
						Kf273002Service kf_273002 = new Kf273002Service(appContext);  /* 273002 ����ó�� */
						
						retBuffer = kf_273002.chk_kf_273002(cmMap, dataMap);
						
					} else { //�߸����ŵ� ����
						
						retBuffer = mkErrJunmun(cmMap, buffer);
					}
					
				}else if(cmMap.getMap("PROGRAM_ID").equals("276001")){ // ���ܼ��� �ϰ����ΰ�� ���� ���� :: �ݰ��-->���̹�(����ü)
					
					if(cmMap.getMap("RS_FLAG").equals("C") || cmMap.getMap("RS_FLAG").equals("B")){ //������
						
						MapForm mfMap = KFL_276001.parseRecvBuffer(buffer);  /*���������Ľ�...*/
						
						dataMap = KFL_276001.parseRecvReptBuffer(buffer, Integer.parseInt(mfMap.getMap("REQ_CNT").toString()));  /*�ݺ��ΰ� �ִ� ���������Ľ�...*/
						
						log.debug("dataMap = " + dataMap.getMaps());
						
						Kf276001Service kf_276001 = new Kf276001Service(appContext);  /* 276001 ����ó�� */
						
						retBuffer = kf_276001.chk_kf_276001(cmMap, mfMap, dataMap);
						
					} else { 
						
						retBuffer = mkErrJunmun(cmMap, buffer);
					}	
					
//				}else if(cmMap.getMap("PROGRAM_ID").equals("502001")){ // ���漼 ���ΰ�� ���� ���� :: �ݰ��-->���̹�(����ü)
//						
//					if(cmMap.getMap("RS_FLAG").equals("C")){ //������
//						
//						
//					} else { //�λ�����
//						
//						dataMap = BSL_502001.parseRecvBuffer(buffer);  /*���������Ľ�...*/
//						
//						log.debug("dataMap = " + dataMap.getMaps());
//						
//						Bs502001Service bs_502001 = new Bs502001Service(appContext);  /* 272001 ����ó�� */
//						
//						retBuffer = bs_502001.chk_bs_502001(cmMap, dataMap);
//						
//					}	
//					
//				}else if(cmMap.getMap("PROGRAM_ID").equals("502002")){ // ���漼 ������������
//					
//					if(cmMap.getMap("RS_FLAG").equals("C")){ //������
//						
//						
//					} else { //�λ�����
//						
//						dataMap = BSL_502002.parseRecvBuffer(buffer);  /*���������Ľ�...*/
//						
//						log.debug("dataMap = " + dataMap.getMaps());
//						
//						Bs502002Service bs_502002 = new Bs502002Service(appContext);  /* 502002 ����ó�� */
//						
//						retBuffer = bs_502002.chk_bs_502002(cmMap, dataMap);
//						
//					}	
//					
//				}else if(cmMap.getMap("PROGRAM_ID").equals("502101")){ // ���� ���γ��� ����
//					
//					if(cmMap.getMap("RS_FLAG").equals("C")){ //������
//						
//						
//					} else { //�λ�����
//						
//						dataMap = BSL_502101.parseRecvBuffer(buffer);  /*���������Ľ�...*/
//						
//						log.debug("dataMap = " + dataMap.getMaps());
//						
//						Bs502101Service bs_502101 = new Bs502101Service(appContext);  /* 502101 ����ó�� */
//						
//						retBuffer = bs_502101.chk_bs_502101(cmMap, dataMap);
//						
//					}	
//					
//				}else if(cmMap.getMap("PROGRAM_ID").equals("521001")){ // ���漼 ��������������ȸ  :: �ݰ��-->���̹�(����ü)
//					
//					if(cmMap.getMap("RS_FLAG").equals("C")){ //������
//						
//						
//					} else { //�λ�����
//						
//						dataMap = BSL_521001.parseRecvBuffer(buffer);  /*���������Ľ�...*/
//						
//						log.debug("dataMap = " + dataMap.getMaps());
//						
//						Bs521001Service bs_521001 = new Bs521001Service(appContext);  /* 521001 ����ó�� */
//						
//						retBuffer = bs_521001.chk_bs_521001(cmMap, dataMap);
//						
//					}
//					
//				}else if(cmMap.getMap("PROGRAM_ID").equals("521002")){ // ���漼 ������������ȸ  :: �ݰ��-->���̹�(����ü)
//					
//					if(cmMap.getMap("RS_FLAG").equals("C")){ //������
//						
//						
//					} else { //�λ�����
//						
//						dataMap = BSL_521002.parseRecvBuffer(buffer);  /*���������Ľ�...*/
//						
//						log.debug("dataMap = " + dataMap.getMaps());
//						
//						Bs521002Service bs_521002 = new Bs521002Service(appContext);  /* 521002 ����ó�� */
//						
//						retBuffer = bs_521002.chk_bs_521002(cmMap, dataMap);
//						
//					}
//					
//				}else if(cmMap.getMap("PROGRAM_ID").equals("523001")){ // ���漼 ���γ��� ������ȸ 
//					
//					if(cmMap.getMap("RS_FLAG").equals("C")){ //������
//						
//						
//					} else { //�λ�����
//						
//						dataMap = BSL_523001.parseRecvBuffer(buffer);  /*���������Ľ�...*/
//						
//						log.debug("dataMap = " + dataMap.getMaps());
//						
//						Bs523001Service bs_523001 = new Bs523001Service(appContext);  /* 523001 ����ó�� */
//						
//						retBuffer = bs_523001.chk_bs_523001(cmMap, dataMap);
//						
//					}
//					
//				}else if(cmMap.getMap("PROGRAM_ID").equals("523002")){ // ���漼 ���γ��� ����ȸ 
//					
//					if(cmMap.getMap("RS_FLAG").equals("C")){ //������
//						
//						
//					} else { //�λ�����
//						
//						dataMap = BSL_523002.parseRecvBuffer(buffer);  /*���������Ľ�...*/
//						
//						log.debug("dataMap = " + dataMap.getMaps());
//						
//						Bs523002Service bs_523002 = new Bs523002Service(appContext);  /* 523002 ����ó�� */
//						
//						retBuffer = bs_523002.chk_bs_523002(cmMap, dataMap);
//						
//					}
					
				} 	
				
				
			} else if(cmMap.getMap("TX_GUBUN").equals("210")) { /*������� ��û����*/
				
			} else if(cmMap.getMap("TX_GUBUN").equals("420")) { /*������� ��û*/
				
				if(cmMap.getMap("PROGRAM_ID").equals("992001")){ /*���� ��(���)*/
					
					if(cmMap.getMap("RS_FLAG").equals("C") || cmMap.getMap("RS_FLAG").equals("B")){ /* ������ �� ���ý� ���� ���� ����*/

						dataMap = BSL_992001.parseRecvBuffer(buffer);  /*���������Ľ�...*/
						
						log.debug("dataMap = " + dataMap.getMaps());
						
						Bs992001Service bs_992001 = new Bs992001Service(appContext);  /* 523002 ����ó�� */
						
						retBuffer = bs_992001.chk_bs_992001(cmMap, dataMap);
					}
					
				}

			} else if(cmMap.getMap("TX_GUBUN").equals("430")) { /*������� ��û����*/

			} else if(cmMap.getMap("TX_GUBUN").equals("700")) { /*�������� ����*/

			} else if(cmMap.getMap("TX_GUBUN").equals("710")) { /*�������� ����*/

			} else if(cmMap.getMap("TX_GUBUN").equals("800")) { /*��Ÿ����� ����(��û)*/
				
				cmMap.setMap("RS_FLAG"   , "G");       /*�����̿���(G) */
				cmMap.setMap("RESP_CODE" , "000");     /*�����ڵ�*/
		        cmMap.setMap("GCJ_NO"    , "0260" + CbUtil.lPadString(String.valueOf(SeqNumber()), 8, '0'));     /*�̿��� �Ϸù�ȣ*/
		        cmMap.setMap("TX_GUBUN"  , "0810");
				
				retBuffer = MSG_HEAD.makeSendBuffer(cmMap, dataMap);
				

			} else if(cmMap.getMap("TX_GUBUN").equals("810")) { /*��Ÿ����� ����(����)*/

			} else {
				log.error("Invalid format ��������(" + buffer.length + ") = [" + new String(buffer, 0, buffer.length < 260? buffer.length:260) + "]");
				
				retBuffer = buffer;
			}
			
			
			/*Ʈ����� ����...*/
			//DB�Է°��� �ִ� ���...
			//mainTransProcess();
			
			/*���������� �ۼ��������� �����Ѵ�...*/
			if(!cmMap.getMap("TX_GUBUN").equals("0810")) {
				
				if(retBuffer.length >= 2000) {
					log.info("������������� 2000bytes �̻�: 2000bytes �� ��ϳ���");
					logMap.setMap("RTN_MSG" , new String(retBuffer).substring(0, 2000)); /*��������*/
				} else {
					logMap.setMap("RTN_MSG" , new String(retBuffer)); /*��������*/
				}
				
				logMap.setMap("RES_CD"  , new String(retBuffer).substring(24, 27)); /*ó�����*/
				logMap.setMap("ERR_MSG" , cUtil.msgPrint("", "", "", new String(retBuffer).substring(24, 27))); /*�����޽���*/
				logMap.setMap("REG_DTM" , CbUtil.getCurrent("yyyyMMddHHmmss"));
				logMap.setMap("LAST_DTM", CbUtil.getCurrent("yyyyMMddHHmmss"));
				
				if(sqlService_cyber.queryForUpdate("CODMBASE.CODM_JUNMUN_TXRX_INSERT_CO5110", logMap) > 0) {
					log.info("�ۼ��� ���� ����Ϸ�!!!");
				}
			}
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
			
			/*������ �߻��� ���...Ư�� ������ �̻��� ��� ���� �����ش�.*/
			retBuffer = buffer;
		} finally {
			
			/*���������� ������ ���� ���� */
			//System.gc();
		}

	}
	
	/*���������� ����� ������ �ش�.*/
    private byte[] mkErrJunmun(MapForm mpHead, byte[] rv_data){
    
    	try {
    		
    		mpHead.setMap("RS_FLAG"   , "G");       /*�����̿���(G) */
    		mpHead.setMap("RESP_CODE" , "201");     /*�����ڵ� : ���� �Ұ�*/
    		mpHead.setMap("GCJ_NO"    , "0260" + CbUtil.lPadString(String.valueOf(SeqNumber()), 8, '0'));     /*�̿��� �Ϸù�ȣ*/

    		byte[] b_head = MSG_HEAD.makeSendBuffer(mpHead, dataMap);
    		
    		System.arraycopy(b_head, 0, rv_data, 0, 74);
    		
    		
    	}catch (Exception e) {
			e.printStackTrace();
		}

    	return rv_data;
    }
	
	/* Main Thread 
	 * �׽�Ʈ ����ø� ����մϴ�...
	 * */
	public static void main(String args[]) {

		ApplicationContext context  = null;
		
		try {

			Txdm2510 svr = new Txdm2510(0);
			
			/*Log4j ����*/
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
		
		int connPort =  Utils.getResourceInt("ApplicationResource", "kftc.recv.port");  /*51001*/
		
		RcvServer server = new RcvServer(connPort, "com.uc.bs.cyber.daemon.txdm2510.Txdm2510", 30, 10, "log4j.xml", 4 ,com.uc.core.Constants.TYPEFIELDLEN);

		server.setContext(appContext);
		server.setProcId("2510");
		server.setProcName("���������տ��輭��");
		server.setThreadName("thr_2510");
		
		server.initial();
		
		comdThread = new Thread(server);
		
		/**
		 * ������ �ۼ����� ���ؼ� ������ ������ش�
		 * (������������)�� ���ؼ� Threading...
		 */
		comdThread.setName("2510");
		
		comdThread.start();
		
	}
	
	/*----------------------------------------------*/
	/* �Ϸù�ȣ �������� �׽�Ʈ*/
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

	@Override
	public void setContext(Object obj) {
		// TODO Auto-generated method stub
		this.appContext = (ApplicationContext) obj;
		sqlService_cyber = (IbatisBaseService) appContext.getBean("baseService_cyber");

	}


}
