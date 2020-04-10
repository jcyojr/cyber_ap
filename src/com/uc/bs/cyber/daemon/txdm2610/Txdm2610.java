/**
                                                               *  �ֽý��۸� : ���̹����漼û ��ȭ
 *  ��  ��  �� : ����
 *  ��  ��  �� : ������(����) ���輭���� �⵿�ϰ� �ۼ��ŵ����͸� ó���Ѵ�...
 *            ������(����) ��û �� �����ڷḦ �����Ѵ�.
 *            com.uc.bs.cyber.etax.* �ڿ����
 *  Ŭ����  ID : Txdm2610 
 *  ����  �̷� :
 * ------------------------------------------------------------------------
 *  �ۼ���      	�Ҽ�  		    ����            Tag           ����
 * ------------------------------------------------------------------------
 *  ��â��      ��ä��(��)         2013.08.12         %01%         �����ۼ�
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

//EGTOB FRAME �� �̿��ϱ� ����.
//import com.uc.egtob.net.server.RcvWorker;
//import com.uc.egtob.net.server.RcvServer;

public class Txdm2610 extends RcvWorker{
	
	private IbatisBaseService sqlService_cyber = null;
	private ApplicationContext appContext = null;
	private Thread comdThread = null;
	
	private Comd_WorkDfField MSG_HEAD = new Comd_WorkDfField(); //��������� --
	
	private Df201002FieldList DFL_201002 = new Df201002FieldList();	//ȯ�氳���δ�� �������� ����ȸ --
	private Df202001FieldList DFL_202001 = new Df202001FieldList();	//ȯ�氳���δ�� ���ΰ�� ����     --

	private MapForm dataMap    = null;
	@SuppressWarnings("unused")
	private String dataSource  = null;

	private byte[] recvBuffer;
	
	
	/*
	 * 20141001 ��������� �Ű� ���� �߰�
	 * */
	// ���������� ����� ó�� ����
    // 0 : ���ڽŰ� �Ϸ�  5 : ������ ó�� ����   9 : ��������
	private String state = "0";
	//�������� �����ڵ�
	private String RE_CO = "0000";			
	// ���� ���� ����
	private String err_msg;
    //���� ó�� 
    private MapForm taxMap    = null;
			
	//�������� ����ȸ --
	private Df533001FieldList GBN_LIST =   new Df533001FieldList();	
	//����ҵ漼 ���� ����ȸ --
	private Df533001Tax004FieldList DFL_004 =   new Df533001Tax004FieldList();
	//����ҵ漼 ��� ����ȸ --
	private Df533001Tax005FieldList DFL_005 =   new Df533001Tax005FieldList();	
	//����ҵ漼 Ư��¡�� ����ȸ --
	private Df533001Tax008FieldList DFL_008 =   new Df533001Tax008FieldList();	
	//�ֹμ� ���� ����ȸ
	private Df533001Tax009FieldList DFL_009 =   new Df533001Tax009FieldList();
	//�ֹμ� �������� ����ȸ
	private Df533001Tax011FieldList DFL_011 =   new Df533001Tax011FieldList();
	
	
	/* ������ 
	 * QUEUE ��
	 * */
	public Txdm2610() throws Exception {
		super();

		log.info("============================================");
		log.info("== new RcvWorker( )                 Start ==");
		log.info("============================================");

	}
	
	/* ������ 
	 * �ʱ�ȭ��
	 * */
	public Txdm2610(int annotation) throws Exception {
        // nothing...
		
	}	
	
	/*
	 * Ʈ����ǿ�(������)
	 * */
	public Txdm2610(ApplicationContext context, String dataSource) throws Exception {
		
		/*Context ����...*/
		this.context = context;
		this.dataSource = dataSource;
		
	}
			
	/*������*/
	public Txdm2610(ApplicationContext context) throws Exception {
		
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
		
		log.info("============================================");
		log.info("== doDataRecv(" + socket.socket().getInetAddress() + ") Start ==");
		log.info(" RECV====[" + new String(buffer) + "]");
		log.info("============================================");
		
		CbUtil cUtil = CbUtil.getInstance();
		
		MapForm cmMap;
		MapForm logMap = new MapForm();
		recvBuffer = null;
		
		byte[] chkJummun = new byte[70];  //üũ�� ����(��������ũ�⸸ŭ)

		try {
			
			/*
			 * ����ó��...(Ʈ�����ó��)
			 * */
			recvBuffer =  buffer;
			
			if(recvBuffer.length < 70) {
				//70byte ��ŭ ä���� ������ �����Ѵ�...				
				StringBuffer sb = new StringBuffer();
				
				for(int i = 0 ; i < 70 - recvBuffer.length ; i++){
					sb.append("0");
				}
				System.arraycopy(sb.toString().getBytes(), 0, chkJummun, buffer.length, 70 - buffer.length);
				
			} else {
				System.arraycopy(buffer, 0, chkJummun, 0, 70);
			}
						
			/* �������м� */
			cmMap = MSG_HEAD.parseHeadBuffer(chkJummun);
			
			cmMap.setMap("PRC_GB", "DP");   /*������(����) ó�� ���� ����*/
						
			//log.debug("cmMap = " + cmMap.getMaps());
			
			/* �ۼ��� ���� �����*/
			logMap.setMap("SYS_DSC"  , "04"); /*������(����)-ī��*/
			logMap.setMap("MSG_ID"   , cmMap.getMap("MEDIA_CODE").toString() + cmMap.getMap("TX_GUBUN").toString() + cmMap.getMap("BANK_CODE").toString()); /*��������*/
			logMap.setMap("RCV_MSG"  , new String(buffer)); /*��������*/
			logMap.setMap("REG_DTM"  , CbUtil.getCurrent("yyyyMMddHHmmss"));
			
			/* �м��� ������ ���� ���α׷��� �б��Ѵ�. */
			if(cmMap.getMap("TX_GUBUN").equals("030")) {  /*����ȸ ��û����*/
			
				dataMap = DFL_201002.parseRecvBuffer(buffer);  /*���������Ľ�...*/
				
				if (dataMap.getMap("TAX_GB").equals("2")) {// ���ܼ��� �������� ����ȸ ���� :: ������(����)-->���̹�(����ü)
				
					dataMap.setMap("SEMOK_GB", dataMap.getMap("EPAY_NO").toString().substring(5, 6));//���ڳ��ι�ȣ��ݱ��� ù���ڷ� ������ �����Ѵ�.
					
					log.debug("SEMOK_GB = " + dataMap.getMap("SEMOK_GB"));
					
					if(dataMap.getMap("SEMOK_GB").equals("9")){ // ȯ�氳���δ�� ������������ȸ :: ������(����)-->���̹�(����ü)
						
						if(cmMap.getMap("RS_FLAG").equals("DZ")){ //������(����)
							
							log.debug("��  ȯ�氳���δ�� ����ȸ dataMap = " + dataMap.getMaps());
							
							Df201002Service df_201002 = new Df201002Service(appContext);  /* 201002 ����ó�� */
							
							retBuffer = df_201002.chk_df_201002(cmMap, dataMap);
							
							if(log.isDebugEnabled()){
								log.debug("retBuffer = " + new String(retBuffer));
							}
							
						} else { 
						    retBuffer = mkErrJunmun(cmMap, buffer);
						}
						
					} else { // ȯ�氳���δ�ݿ� ���ܼ��� �������� ����ȸ ���� :: ������(����)-->���̹�(����ü)
						
						if(cmMap.getMap("RS_FLAG").equals("DZ")){ //������(����)
							
							log.debug("��  ���ܼ��� ����ȸ dataMap = " + dataMap.getMaps());
							
							Df271002Service df_271002 = new Df271002Service(appContext);  /* 271002 ����ó�� */
							
							retBuffer = df_271002.chk_df_271002(cmMap, dataMap);
							
						} else { 
						    retBuffer = mkErrJunmun(cmMap, buffer);
						}
					}
					
				}else if(dataMap.getMap("TAX_GB").equals("3")){ // ���ϼ��� ������������ȸ :: ������(����)-->���̹�(����ü)

					if(cmMap.getMap("RS_FLAG").equals("DZ")){ //������(����)
						
						log.debug("��  ���ϼ��� ����ȸ dataMap = " + dataMap.getMaps());
						
						Df251002Service df_251002 = new Df251002Service(appContext);  /* 251002 ����ó�� */
						
						retBuffer = df_251002.chk_df_251002(cmMap, dataMap);
						
					} else { 
						
						retBuffer = mkErrJunmun(cmMap, buffer);
					}
					
				}else if(dataMap.getMap("TAX_GB").equals("1")){ // ���漼 ������������ȸ  :: ������(����)-->���̹�(����ü)
					
					if(cmMap.getMap("RS_FLAG").equals("DZ")){ //������(����)
						
						log.debug("��  ���漼 ����ȸ dataMap = " + dataMap.getMaps());
						
						Df521002Service df_521002 = new Df521002Service(appContext);  /* 521002 ����ó�� */
						
						retBuffer = df_521002.chk_df_521002(cmMap, dataMap);						
										
					} else { 
                        retBuffer = mkErrJunmun(cmMap, buffer);
                    }

				} 	
					
			} else if(cmMap.getMap("TX_GUBUN").equals("040")) { /*����ȸ ��û����*/
				
					
			} else if(cmMap.getMap("TX_GUBUN").equals("050")) { /*����ó�� ��û*/

				dataMap = DFL_202001.parseRecvBuffer(buffer);  /*���������Ľ�...*/
				
				if (dataMap.getMap("TAX_GB").equals("2")) {// ���ܼ��� ����ó�� ��û���� :: ������(����)-->���̹�(����ü)
					
					dataMap.setMap("SEMOK_GB", dataMap.getMap("EPAY_NO").toString().substring(5, 6));//���ڳ��ι�ȣ��ݱ��� ù���ڷ� ������ �����Ѵ�.
					
					log.debug("SEMOK_GB = " + dataMap.getMap("SEMOK_GB"));					
					
					if(dataMap.getMap("SEMOK_GB").equals("9")){ // ȯ�氳���δ�� ���ΰ������:: ������(����)-->���̹�(����ü)
						
						if(cmMap.getMap("RS_FLAG").equals("DZ")){ //������(����)
							
							log.debug("��  ȯ�氳���δ�� ���ο�û dataMap = " + dataMap.getMaps());
							
							Df202001Service df_202001 = new Df202001Service(appContext);  /* 202001 ����ó�� */
							
							retBuffer = df_202001.chk_df_202001(cmMap, dataMap);
							
						} else { 
							
							retBuffer = mkErrJunmun(cmMap, buffer);
						}
					}else {
						
						if(cmMap.getMap("RS_FLAG").equals("DZ")){ //������(����)
							
							log.debug("��  ���ܼ��� ���ο�û dataMap = " + dataMap.getMaps());
							
							Df272001Service df_272001 = new Df272001Service(appContext);  /* 272001 ����ó�� */
							
							retBuffer = df_272001.chk_df_272001(cmMap, dataMap);
							
						} else { //�߸����ŵ� ����
							
							/* ���⼱ ������(����)������ ���ŵ� ������ �����ϸ� �ȵ�
							 * ���� ������ �����Ͽ� �ִ���..�ƴ�..�׳� ������� �ؾ���.*/
							
							retBuffer = mkErrJunmun(cmMap, buffer);
							
						}
					}
								
				}else if(dataMap.getMap("TAX_GB").equals("3")){ // ���ϼ������ ���ΰ�� ���� :: ������(����)-->���̹�(����ü)
					
					if(cmMap.getMap("RS_FLAG").equals("DZ")){ //������(����)
						
						log.debug("��  ���ϼ��� ���ο�û dataMap = " + dataMap.getMaps());
						
						Df252001Service df_252001 = new Df252001Service(appContext);  /* 252001 ����ó�� */
						
						retBuffer = df_252001.chk_df_252001(cmMap, dataMap);
						
					} else { 
						
						retBuffer = mkErrJunmun(cmMap, buffer);
					}
					
				}else if(dataMap.getMap("TAX_GB").equals("1")){ // ���漼 ���ΰ�� ���� ���� :: ������(����)-->���̹�(����ü)

				    if(cmMap.getMap("RS_FLAG").equals("DZ")){ //������(����)

						log.debug("��  ���漼 ���ο�û dataMap = " + dataMap.getMaps());
						
						Df502001Service df_502001 = new Df502001Service(appContext);  /* 272001 ����ó�� */
						
						retBuffer = df_502001.chk_df_502001(cmMap, dataMap);
									
					} else { //�߸����ŵ� ����
						
						/* ���⼱ ������(����)������ ���ŵ� ������ �����ϸ� �ȵ�
						 * ���� ������ �����Ͽ� �ִ���..�ƴ�..�׳� ������� �ؾ���.*/
						
						retBuffer = mkErrJunmun(cmMap, buffer);	
					}
				}
							
//			} else if(cmMap.getMap("TX_GUBUN").equals("060")) { /*����ó�� ��û����*/

//			} else if(cmMap.getMap("TX_GUBUN").equals("700")) { /*�������� ����*/
//
//			} else if(cmMap.getMap("TX_GUBUN").equals("710")) { /*�������� ����*/

			} else if(cmMap.getMap("TX_GUBUN").equals("800")) { /*��Ÿ����� ����(��û)*/
				
				cmMap.setMap("RESP_CODE" , "0000");     /*�����ڵ�*/
		        cmMap.setMap("TX_GUBUN"  , "090");
				
				retBuffer = MSG_HEAD.makeSendBuffer(cmMap, dataMap);
				
			} else if(cmMap.getMap("TX_GUBUN").equals("090")) { /*��Ÿ����� ����(����)*/

			} 
			
			/*
			 * �ٷΰ��� �ۼ��� ���� ó��
			 * ���� �и� �Ű� ���� ó���Ѵ�. 
			 * ���������ϼ� = 0, ����� = 0
			 */
			else if(cmMap.getMap("TX_GUBUN").equals("150")) { 

				//log.debug("buffer :: "+buffer);
				/*���������Ľ�...*/
				dataMap = GBN_LIST.parseRecvBuffer(buffer);  
				
				//log.debug("����Ȯ�� dataMap :: "+dataMap);
				
					
				// �ֹμ� ����
				if(dataMap.getMap("TAX_ITEM").equals("140001")){
						
					/*���������Ľ�...*/
					taxMap = DFL_004.parseRecvBuffer(buffer);  
					
					//����������ȣ
					taxMap.setMap("JM_NO", cmMap.getMap("BCJ_NO").toString());
					//����(ī��)�ڵ�, ��ȸ�ÿ� "000"
					taxMap.setMap("BANK_CD", cmMap.getMap("BANK_CODE").toString());
					//�����Ͻ� - "YYYYMMDDhhmmss"
					taxMap.setMap("TX_DATETIME", cmMap.getMap("TX_DATETIME").toString());
						
					//log.debug("�����Ľ�Ȯ�� taxMap :: "+taxMap);
						
					if(taxMap.getMap("NAPBU_GB").equals("1")){
						
						log.info("���ռҵ� �Ű� ����");
						//�������� �ϼ��� 0���� ũ�� ���� ó��.20141001 - ǥ�ؿ� �Ű� �Ҽ� ����.
						if(Integer.parseInt(taxMap.getMap("DLQ_CNT").toString()) > 0 || Integer.parseInt(taxMap.getMap("GAST").toString()) > 0
								|| CbUtil.checkNull(taxMap.getMap("RVSN_YY").toString()) == null || CbUtil.checkNull(taxMap.getMap("RVSN_YY").toString()).equals("")){
							log.debug("DLQ_CNT �Ǵ� GAST �� 0 ���� ũ�� ���� ó��");
							cmMap.setMap("RESP_CODE" , "5010");     /*�����ڵ� 5010 ���α����� ���� �������Դϴ�.*/
							cmMap.setMap("TX_GUBUN"  , "160");
							retBuffer = mkBaroSunapErrJunmun(cmMap, buffer);
						}else{
							// ǥ�����漼 ����					
							retBuffer = sendJobTax004(cmMap, taxMap);
						}
												
					}else if(taxMap.getMap("NAPBU_GB").equals("2")){
						log.info("���ռҵ� �������� ����");
						//����ó��
						retBuffer = tax004SunapProcess(cmMap, taxMap, dataMap);
						
						log.info("send buffer :: " + retBuffer);
					}else{
						//���α��� ����
						log.debug("���α��� " + taxMap.getMap("NAPBU_GB").toString() +" ���� �Դϴ�. " );
						cmMap.setMap("RESP_CODE" , "5510");     /*�����ڵ� 5510 ���������ʵ���� �Դϴ�.*/
						cmMap.setMap("TX_GUBUN"  , "160");
						retBuffer = mkBaroSunapErrJunmun(cmMap, buffer);
					}
						
					
				}
				// �ֹμ� ���
				else if(dataMap.getMap("TAX_ITEM").equals("140002")){
						
					taxMap = DFL_005.parseRecvBuffer(buffer);  /*���������Ľ�...*/
						
					taxMap.setMap("JM_NO", cmMap.getMap("BCJ_NO").toString());
					taxMap.setMap("BANK_CD", cmMap.getMap("BANK_CODE").toString());
					taxMap.setMap("TX_DATETIME", cmMap.getMap("TX_DATETIME").toString());
					    
					//log.debug("�����Ľ�Ȯ�� taxMap :: "+taxMap);
						
						
					if(taxMap.getMap("NAPBU_GB").equals("1")){
						
						//���������ϼ�, ������� 0���� ũ�� ���� ó��
						if(Integer.parseInt(taxMap.getMap("DLQ_CNT").toString()) > 0 || Integer.parseInt(taxMap.getMap("GAST").toString()) > 0
								|| CbUtil.checkNull(taxMap.getMap("RVSN_YY").toString()) == null || CbUtil.checkNull(taxMap.getMap("RVSN_YY").toString()).equals("")){
							log.debug("DLQ_CNT �Ǵ� GAST �� 0 ���� ũ�� ���� ó��");
							cmMap.setMap("RESP_CODE" , "1000");     /*�����ڵ� 3010 �ѱݾ��� Ʋ���ϴ�.*/
							cmMap.setMap("TX_GUBUN"  , "160");
							retBuffer = mkBaroSunapErrJunmun(cmMap, buffer);
						}else{
							// ǥ�����漼 ����					
							retBuffer = sendJobTax005(cmMap, taxMap);
						}
						
						
					}else if(taxMap.getMap("NAPBU_GB").equals("2")){
						//����ó��
						log.info("�絵�ҵ� �������� ����");
						retBuffer = tax005SunapProcess(cmMap, taxMap, dataMap);
						log.info("send buffer :: " + retBuffer);
					}else{
						//���α��� ����
						log.debug("���α��� " + taxMap.getMap("NAPBU_GB").toString() +" ���� �Դϴ�. " );
						cmMap.setMap("RESP_CODE" , "5510");     /*�����ڵ� 5510 ���������ʵ���� �Դϴ�.*/
						cmMap.setMap("TX_GUBUN"  , "160");
						retBuffer = mkBaroSunapErrJunmun(cmMap, buffer);
					}		
						
	                    
				}
				//�ֹμ� ����
				else if(dataMap.getMap("TAX_ITEM").equals("104009")){
						
					taxMap = DFL_009.parseRecvBuffer(buffer);  /*���������Ľ�...*/
					
					taxMap.setMap("JM_NO", cmMap.getMap("BCJ_NO").toString());
					taxMap.setMap("BANK_CD", cmMap.getMap("BANK_CODE").toString());
					taxMap.setMap("TX_DATETIME", cmMap.getMap("TX_DATETIME").toString());
					    
					//log.debug("�����Ľ�Ȯ�� taxMap :: "+taxMap);
											
					if(taxMap.getMap("NAPBU_GB").equals("1")){
						
						//���������ϼ�, ������� 0���� ũ�� ���� ó��
						if(Integer.parseInt(taxMap.getMap("DLQ_CNT").toString()) > 0 || Integer.parseInt(taxMap.getMap("NGAS").toString()) > 0 || Integer.parseInt(taxMap.getMap("GSEK").toString()) > 0
								|| CbUtil.checkNull(taxMap.getMap("RVSN_YY").toString()) == null || CbUtil.checkNull(taxMap.getMap("RVSN_YY").toString()).equals("")){
							log.debug("DLQ_CNT �Ǵ� NGAS, GSEK �� 0 ���� ũ�� ���� ó��");
							cmMap.setMap("RESP_CODE" , "1000");     /*�����ڵ� 3010 �ѱݾ��� Ʋ���ϴ�.*/
							cmMap.setMap("TX_GUBUN"  , "160");
							retBuffer = mkBaroSunapErrJunmun(cmMap, buffer);
						}else{
							// ǥ�����漼 ����					
							retBuffer = sendJobTax009(cmMap, taxMap);
						}
						
						
					}else if(taxMap.getMap("NAPBU_GB").equals("2")){
						//����ó��
						log.info("�ֹμ� ���� �������� ����");
						retBuffer = tax009SunapProcess(cmMap, taxMap, dataMap);
						log.info("send buffer :: " + retBuffer);
					}else{
						//���α��� ����
						log.debug("���α��� " + taxMap.getMap("NAPBU_GB").toString() +" ���� �Դϴ�. " );
						cmMap.setMap("RESP_CODE" , "5510");     /*�����ڵ� 5510 ���������ʵ���� �Դϴ�.*/
						cmMap.setMap("TX_GUBUN"  , "160");
						retBuffer = mkBaroSunapErrJunmun(cmMap, buffer);
					}		
					
				}
				//�ֹμ� ��������
                else if(dataMap.getMap("TAX_ITEM").equals("140011")){
						
                	taxMap = DFL_011.parseRecvBuffer(buffer);  /*���������Ľ�...*/
					
					taxMap.setMap("JM_NO", cmMap.getMap("BCJ_NO").toString());
					taxMap.setMap("BANK_CD", cmMap.getMap("BANK_CODE").toString());
					taxMap.setMap("TX_DATETIME", cmMap.getMap("TX_DATETIME").toString());
					    
					//log.debug("�����Ľ�Ȯ�� taxMap :: "+taxMap);
						
					if(taxMap.getMap("NAPBU_GB").equals("1")){
						
						//�������� �ϼ�, ������� 0���� ũ�� ���� ó��
						if(Integer.parseInt(taxMap.getMap("DLQ_CNT").toString()) > 0 || Integer.parseInt(taxMap.getMap("NGAS").toString()) > 0 || Integer.parseInt(taxMap.getMap("GSEK").toString()) > 0
								|| CbUtil.checkNull(taxMap.getMap("RVSN_YY").toString()) == null || CbUtil.checkNull(taxMap.getMap("RVSN_YY").toString()).equals("")){
							log.debug("DLQ_CNT �Ǵ� NGAS, GSEK �� 0 ���� ũ�� ���� ó��");
							cmMap.setMap("RESP_CODE" , "1000");     /*�����ڵ� 3010 �ѱݾ��� Ʋ���ϴ�.*/
							cmMap.setMap("TX_GUBUN"  , "160");
							retBuffer = mkBaroSunapErrJunmun(cmMap, buffer);
						}else{
							// ǥ�����漼 ����					
							retBuffer = sendJobTax011(cmMap, taxMap);
						}
												
					}else if(taxMap.getMap("NAPBU_GB").equals("2")){
						//����ó��
						log.info("�ֹμ� �������� �������� ����");
						retBuffer = tax011SunapProcess(cmMap, taxMap, dataMap);
						log.info("send buffer :: " + retBuffer);
					}else{
						//���α��� ����
						log.debug("���α��� " + taxMap.getMap("NAPBU_GB").toString() +" ���� �Դϴ�. " );
						cmMap.setMap("RESP_CODE" , "5510");     /*�����ڵ� 5510 ���������ʵ���� �Դϴ�.*/
						cmMap.setMap("TX_GUBUN"  , "160");
						retBuffer = mkBaroSunapErrJunmun(cmMap, buffer);
					}		
					
				}else{
						
					// ȸ���ڵ� ����ġ
					log.error("Invalid format ��������(" + buffer.length + ") = [" + new String(buffer, 0, buffer.length < 260? buffer.length:260) + "]");
						
					cmMap.setMap("RESP_CODE" , "1010");     /*�����ڵ� 1010 �����ڵ� ����.*/
					cmMap.setMap("TX_GUBUN"  , "160");
					    
					retBuffer = mkBaroSunapErrJunmun(cmMap, buffer);
						
				}						

			} 
			// Ư��¡�� ���ڽŰ� �� ���� ó��
			else if(cmMap.getMap("TX_GUBUN").equals("250")){
				
				/*���������Ľ�...*/
				taxMap = DFL_008.parseRecvBuffer(buffer);  
				
				//����������ȣ
				taxMap.setMap("JM_NO", cmMap.getMap("BCJ_NO").toString());
				//����(ī��)�ڵ�, ��ȸ�ÿ� "000"
				taxMap.setMap("BANK_CD", cmMap.getMap("BANK_CODE").toString());
				//�����Ͻ� - "YYYYMMDDhhmmss"
				taxMap.setMap("TX_DATETIME", cmMap.getMap("TX_DATETIME").toString());
					
				//log.debug("�����Ľ�Ȯ�� taxMap :: "+taxMap);
				
				//Ư��¡��
				if(taxMap.getMap("TAX_ITEM").equals("140004")){
					
					if(taxMap.getMap("NAPBU_GB").equals("1")){
						
						log.info("Ư��¡�� �Ű� ����");
						//�������� �ϼ��� 0���� ũ�� ���� ó��.20141001 - ǥ�ؿ� �Ű� �Ҽ� ����.
						//if(Integer.parseInt(taxMap.getMap("DLQ_CNT").toString()) > 0 || Integer.parseInt(taxMap.getMap("GAST").toString()) > 0
						//		|| CbUtil.checkNull(taxMap.getMap("RVSN_YY").toString()) == null || CbUtil.checkNull(taxMap.getMap("RVSN_YY").toString()).equals("")){
						//	log.debug("DLQ_CNT �Ǵ� GAST �� 0 ���� ũ�� ���� ó��");
						//	cmMap.setMap("RESP_CODE" , "5010");     /*�����ڵ� 5010 ���α����� ���� �������Դϴ�.*/
						//	cmMap.setMap("TX_GUBUN"  , "260");
						//	retBuffer = mkBaroSunapErrJunmun(cmMap, buffer);
						//}else{
							
						//}
						
						// ǥ�����漼 ����					
						retBuffer = sendJobTax008(cmMap, taxMap);
												
					}else if(taxMap.getMap("NAPBU_GB").equals("2")){
						log.info("Ư��¡�� �������� ����");
						//����ó��
						retBuffer = tax008SunapProcess(cmMap, taxMap, dataMap);
						
						//log.info("send buffer :: " + retBuffer);
					}else{
						//���α��� ����
						log.debug("���α��� " + taxMap.getMap("NAPBU_GB").toString() +" ���� �Դϴ�. " );
						cmMap.setMap("RESP_CODE" , "5510");     /*�����ڵ� 5510 ���������ʵ���� �Դϴ�.*/
						cmMap.setMap("TX_GUBUN"  , "260");
						retBuffer = mkBaroSunapErrJunmun(cmMap, buffer);
					}
					
				}else{
					
					//�����ڵ� ����ġ
					log.debug("TAX_ITEM " + taxMap.getMap("TAX_ITEM").toString() +" ���� �Դϴ�. " );
					cmMap.setMap("RESP_CODE" , "1010");     /*�����ڵ� 1010 �����ڵ� ����.*/
					cmMap.setMap("TX_GUBUN"  , "260");
					retBuffer = mkBaroSunapErrJunmun(cmMap, buffer);					
				}
				
			}
			
			else {
				log.error("Invalid format ��������(" + buffer.length + ") = [" + new String(buffer, 0, buffer.length < 260? buffer.length:260) + "]");
				
				retBuffer = buffer;
			}
			
			
			/*Ʈ����� ����...*/
			//DB�Է°��� �ִ� ���...
			//mainTransProcess();
			
			/*���������� �ۼ��������� �����Ѵ�...*/
			if(!cmMap.getMap("TX_GUBUN").equals("090")) {
				
				if(retBuffer.length >= 2000) {
					log.info("������������� 2000bytes �̻�: 2000bytes �� ��ϳ���");
					logMap.setMap("RTN_MSG" , new String(retBuffer).substring(0, 2000)); /*��������*/
				} else {
					logMap.setMap("RTN_MSG" , new String(retBuffer)); /*��������*/
				}
				
				logMap.setMap("RES_CD"  , new String(retBuffer).substring(29, 33)); /*ó�����*/
				logMap.setMap("ERR_MSG" , cUtil.msgPrint("", "", "", new String(retBuffer).substring(29, 33))); /*�����޽���*/
				logMap.setMap("REG_DTM" , CbUtil.getCurrent("yyyyMMddHHmmss"));
				logMap.setMap("LAST_DTM", CbUtil.getCurrent("yyyyMMddHHmmss"));
				
				if(sqlService_cyber.queryForUpdate("CODMBASE.CODM_JUNMUN_TXRX_INSERT_CO5111", logMap) > 0) {
					log.info("�ۼ��� ���� ����Ϸ�!!!");
				}
			}
				
		} catch (Exception e) {
			e.printStackTrace();
			
			cmMap = MSG_HEAD.parseHeadBuffer(chkJummun);

			log.debug("........������ �̻��� ��� ���� �����ش�.");
			/*������ �߻��� ���...Ư�� ������ �̻��� ��� ���� �����ش�.*/
			retBuffer = mkErrJunmun(cmMap, buffer);
		} finally {
			
			/*���������� ������ ���� ���� */
			//System.gc();
		}

	}
	
	/*���������� ����� ������ �ش�.*/
    private byte[] mkBaroSunapErrJunmun(MapForm mpHead, byte[] rv_data){
    
    	try {
    		
    		byte[] b_head = GBN_LIST.makeSendBuffer(mpHead, dataMap);
    		
    		System.arraycopy(b_head, 0, rv_data, 0, 70);
    		
    		
    	}catch (Exception e) {
			e.printStackTrace();
		}

    	return rv_data;
    }
    
	
	/*���������� ����� ������ �ش�.*/
    private byte[] mkErrJunmun(MapForm mpHead, byte[] rv_data){
    
    	try {
    		
    		mpHead.setMap("RESP_CODE" , "1000");     /*�����ڵ� : ��������*/

    		byte[] b_head = MSG_HEAD.makeSendBuffer(mpHead, dataMap);
    		
    		System.arraycopy(b_head, 0, rv_data, 0, 70);
    		
    		
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

			Txdm2610 svr = new Txdm2610(0);
			
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
		
		int connPort =  Utils.getResourceInt("ApplicationResource", "dftc.recv.port");  /*54001*/
		
		RcvServer server = new RcvServer(connPort, "com.uc.bs.cyber.daemon.txdm2610.Txdm2610", 30, 10, "log4j.xml", 4 ,com.uc.core.Constants.TYPEFIELDLEN);

		server.setContext(appContext);
		server.setProcId("2610");
		server.setProcName("������(����)���輭��");
		server.setThreadName("thr_2610");
		
		server.initial();
		
		comdThread = new Thread(server);
		
		/**
		 * ������ �ۼ����� ���ؼ� ������ ������ش�
		 * (������������)�� ���ؼ� Threading...
		 */
		comdThread.setName("2610");
		
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
		
			e.printStackTrace();
		}			
		
	}
	

	/*Ʈ����� ó��*/
	@Override
	public void transactionJob() {

		try {
			
			log.info("[=========== ���� transactionJob() :: Ʈ�����ó������ �Դϴ�. ========== ]");
			
		} catch (Exception e) {
		
			e.printStackTrace();
		}
	
	}

	@Override
	public void setContext(Object obj) {
		
		this.appContext = (ApplicationContext) obj;
		sqlService_cyber = (IbatisBaseService) appContext.getBean("baseService_cyber");

	}
	
	
	// taxMap �ʱ�ȭ
	private void taxMapFormInit(MapForm cmMap) {
				
		taxMap = new MapForm();
				
		// �߰�
	    taxMap.setMap("RTN_ZIP_CD", "");  // �絵�����ּ� �����ȣ - �絵�ҵ�
	    taxMap.setMap("RTN_ADDR",   "");  // �絵�����ּ� ��     - �絵�ҵ�
		taxMap.setMap("JSDT",       "");  // �ͼӻ���Ⱓ ����     - ���μ�
		taxMap.setMap("JEDT",       "");  // �ͼӻ���Ⱓ ����     - ���μ�
		taxMap.setMap("TJUS",        0);  // ������(��ü)          - ���μ�
		taxMap.setMap("JUWS",        0);  // ������(�ñ���)        - ���μ�
		taxMap.setMap("TCNT",        0);  // ���๰(��ü)          - ���μ�
		taxMap.setMap("SASU",        0);  // ���๰(�ñ���)        - ���μ�
	    taxMap.setMap("RADTX",       0);  // �Ű�Ҽ��ǰ��꼼      - ���μ�
		taxMap.setMap("NGAS",        0);  // ���κҼ��ǰ��꼼      - ���μ�
		taxMap.setMap("JYMD",       "");  // �޿�������            - Ư��¡��
		taxMap.setMap("RVSN_YY",    "");  // �ͼӳ⵵              - ���ռҵ�
		taxMap.setMap("RVSN_MM",    "");  // �ͼӿ�                - Ư��¡��
		taxMap.setMap("WANT",        0);  // ��(�ο�)              - Ư��¡��
		taxMap.setMap("GWAT",        0);  // ��(����ǥ��)          - Ư��¡��
	    taxMap.setMap("TAXT",        0);  // ��(�ֹμ�)            - Ư��¡��
		taxMap.setMap("HAMT",        0);  // ȯ���Ѿ�              - Ư��¡��
		taxMap.setMap("DLQ_CNT",     0);  // ���������ϼ�          - ��,��,��
		taxMap.setMap("F_DUE_DT",   "");  // ���� ���α���         - ��,��,��
		taxMap.setMap("BCJ_NO", cmMap.getMap("BCJ_NO").toString());
	    taxMap.setMap("BANK_CODE", cmMap.getMap("BANK_CODE").toString());
		taxMap.setMap("TX_DATETIME", cmMap.getMap("TX_DATETIME").toString());
			    
		//������ �߰�
		taxMap.setMap("SUP_DT",     "");  // �޿�������          - ������
			    
		log.debug("BCJ_NO" + cmMap.getMap("BCJ_NO").toString());
		/*
		headField.add("LEN"              , 4  ,  "H"); //��������
	    headField.add("TX_ID"            , 6  ,  "C"); //�������� - SN2601:�λ��
		headField.add("TX_DATETIME"      , 14 ,  "C"); //�����Ͻ� - "YYYYMMDDhhmmss"
		headField.add("TX_GUBUN"         , 3  ,  "C"); //�������������ڵ� "030:����ȸ��û, 040:����ȸ��û ����, 050:����ó����û, 060:����ó����û ����
		headField.add("RS_FLAG"          , 2  ,  "C"); //���Ӱ�α����ڵ� - ����(DZ)
		headField.add("RESP_CODE"        , 4  ,  "C"); //����ڵ� - ��ȸ�ÿ� "0000"
		headField.add("SIDO_CODE"        , 2  ,  "C"); //�õ������ڵ� - 26:�λ��
		headField.add("BANK_CODE"        , 3  ,  "C"); //����(ī��)�ڵ�, ��ȸ�ÿ� "000"
		headField.add("BCJ_NO"           , 12 ,  "C"); //����������ȣ(���ں� ����������ȣ)
		headField.add("MEDIA_CODE"       , 3  ,  "C"); //��ü�����ڵ� - 270: ����������
		headField.add("RESERVE1"         , 17 ,  "C"); //���񿵿�
		*/	
	}
			
	// ����ҵ漼 ���ռҵ�� ���ڽŰ� ǥ�����漼 ����  cmMap, dataMap, taxMap
	private byte[] sendJobTax004(MapForm cmMap, MapForm taxMap) {
			
		/******************************************************
		* ǥ�� ����� �ڷ� ���� ó��  START
		* *****************************************************/
	    MapForm   sndForm  = new  MapForm();
	    	
	    try{
	    	sndForm.setMap("tax_gubun"	   ,   "140001"); //�ý�����
	        sndForm.setMap("SIDO_COD"      ,   "26"); // (2)  �õ��ڵ�   -   ������ġ��ü �ʼ����а����� ���
	        sndForm.setMap("SGG_COD"       ,   CbUtil.checkNull(taxMap.getMap("SGG_COD").toString())); // (3)  �ñ����ڵ�   -   ������ġ��ü �ʼ����а����� ���
	        sndForm.setMap("TAX_ITEM"      ,   "140001"); // (6)  �����ڵ�
	        sndForm.setMap("TAX_YYMM"      ,   taxMap.getMap("TAX_YY").toString()+taxMap.getMap("TAX_MM").toString()); // (6)  �������
	        sndForm.setMap("TAX_DIV"       ,   "3"); // (1)  ��������   -   3:�Ű��
	           
	        //log.debug("=========������=============="+taxMap.getMap("HACD").toString());
	            
	        String bucd = getLdongCode(taxMap.getMap("SGG_COD").toString(), taxMap.getMap("HACD").toString());
	        //log.debug("=========������=============="+bucd);
	        	
	        //String address = getAddress(taxMap.getMap("ZIP_CD").toString());	
	        //taxMap.setMap("ADDRESS", address);
	        //String address = "busansi donggu test";
	        //log.debug("=========address=============="+address);
	        
	        sndForm.setMap("ADONG_COD"     ,   CbUtil.checkNull(taxMap.getMap("HACD").toString())); // (3)  ����������    
	        String TAX_DT = taxMap.getMap("TAX_YY").toString()+taxMap.getMap("TAX_MM").toString()+taxMap.getMap("TAX_DD").toString();   
	        sndForm.setMap("TAX_DT"        ,   TAX_DT); // (8)  �Ű�����
	        sndForm.setMap("REG_NO"        ,   CbUtil.checkNull(taxMap.getMap("REG_NO").toString())); // (13) ������ �ֹ�/���ι�ȣ    
	        sndForm.setMap("REG_NM"        ,   CbUtil.checkNull(taxMap.getMap("DPNM").toString())); // (80) ������ ����
	        sndForm.setMap("TPR_COD"       ,   (taxMap.getMap("DP_GBN").equals("0"))?"00":"20"); // (2)  �����ڵ�   -   ���ں� ǥ���ڵ�- �����ǹ���(����.����)�ڵ�
	        sndForm.setMap("LDONG_COD"     ,   CbUtil.checkNull(bucd) + "00"); // (5)  ����� ������
	        sndForm.setMap("BIZ_ZIP_NO"    ,   CbUtil.checkNull(taxMap.getMap("ZIP_CD").toString())); // (6)  ����� �����ȣ
	        sndForm.setMap("BIZ_ZIP_ADDR"  ,   CbUtil.checkNull(taxMap.getMap("ADDR").toString()));    // (60) ����� �����ּ�
	        String  addrdt = " ";
	        if(taxMap.getMap("ADDR_DT").toString()==null || taxMap.getMap("ADDR_DT").toString().equals("")){
	            addrdt = " ";
	        }else{
	            addrdt = taxMap.getMap("ADDR_DT").toString();
	        }
	        sndForm.setMap("BIZ_ADDR"      ,   addrdt);    // (100)����� ���ּ�
	        sndForm.setMap("MO_TEL"        ,   CbUtil.checkNull(taxMap.getMap("PHONE_NO").toString()));    // (16) �ڵ�����ȣ
	        sndForm.setMap("BIZ_TEL"       ,   CbUtil.checkNull(taxMap.getMap("TEL_NO").toString()));    // (16) ����� ��ȭ��ȣ
	       
	        //����ڹ�ȣ
	    	sndForm.setMap("BIZ_NO"        ,   CbUtil.checkNull(taxMap.getMap("SAUP_NO").toString()));  // N(10) ����ڹ�ȣ	 
	        sndForm.setMap("CMP_NM"        ,   CbUtil.checkNull(taxMap.getMap("CMP_NM").toString()));    // (100)��ȣ��
	        sndForm.setMap("RVSN_YY"       ,   CbUtil.checkNull(taxMap.getMap("RVSN_YY").toString()));    // (4)  �ͼӳ⵵
	        sndForm.setMap("C_REQ_DT"      ,   "");    // (8)  �����Ű�����   -   �Ű� ������ "�����Ű�" �� ��츸 ���� ������
	        sndForm.setMap("DUE_DT"        ,   taxMap.getMap("DUE_DT").toString());    // (8)  ��������
	        sndForm.setMap("REQ_DIV"       ,   "1");    // (1)  �Ű���   -    1:Ȯ���Ű�,2:�����Ű�
	                    
	        sndForm.setMap("INCTX"         ,   CbUtil.checkNull(taxMap.getMap("KJSA").toString()));    // (14) ���ռҵ漼
	        sndForm.setMap("RSTX_INC"      ,   CbUtil.checkNull(taxMap.getMap("JMSA").toString()));    // (14) ����ҵ漼 ���ռҵ漼��
	        sndForm.setMap("RADTX"         ,   "0");    // (14) �Ű�Ҽ��� ���꼼
	        sndForm.setMap("PADTX"         ,   "0");    // (14) ���κҼ��� ���꼼
	        sndForm.setMap("TAX_RT"        ,   "10");    // (4)  ����   -   õ����
	        sndForm.setMap("ADTX_YN"       ,   "2");    // (1)  ���꼼����   -    1:�� 2:��
	        sndForm.setMap("DLQ_CNT"       ,   "0");    // (14) ���������ϼ�
	        sndForm.setMap("TOT_ADTX"      ,   "0");    // (14) ���꼼 �հ�
	        sndForm.setMap("TOT_AMT"       ,   CbUtil.checkNull(taxMap.getMap("SUM_RCP").toString()));    // (14) �ѳ��μ���   -   ����ҵ漼 ���ռҵ漼�� + ���꼼 �հ�
	        	
	        sndForm.setMap("F_DUE_DT"      ,   CbUtil.checkNull(taxMap.getMap("DUE_DT").toString()));   // (8)  ���ʳ���
	        sndForm.setMap("RPT_REG_NO"    ,   taxMap.getMap("REG_NO").toString());    // (13) �Ű��� �ֹ�/���ι�ȣ
	        sndForm.setMap("RPT_NM"        ,   taxMap.getMap("DPNM").toString());    // (80) �Ű��� ����/���θ�
	        sndForm.setMap("RPT_TEL"       ,   taxMap.getMap("PHONE_NO").toString());    // (16) �Ű��� ��ȭ��ȣ   -   �ڵ��� �Ǵ� ��ȭ��ȣ
	        sndForm.setMap("RPT_ID"        ,   "ETAX9"  																														);    // (20) �Ű���ID
	        sndForm.setMap("RPT_ADMIN"     ,   taxMap.getMap("DPNM").toString());    // (60) �Ű��� ���/�μ���
	        sndForm.setMap("RPT_SYSTEM"    ,   "ETAX");    // (5)  �Ű� �ý��� ����   -   WETAX : WETAX, ETAX: ETAX
	           
	        sndForm.setMap("SINGO_DIV"     ,   "1");    // (1)  �Ű���   -   ���ʽŰ�:1 , �����Ű�:2
	        sndForm.setMap("B_TAX_NO"      ,   "");    // (31) ���ʽŰ�� ������ȣ   -   �����Ű�� �ʼ�
	        sndForm.setMap("B_EPAY_NO"     ,   "");    // (19) ���ʽŰ�� ���ڳ��ι�ȣ   -   �����Ű�� �ʼ�
	        sndForm.setMap("CHG_MEMO"      ,   "");    // (2)  �����Ű���   -   �����Ű���ʼ�
	    								//                      - ���ҽŰ� : 11
	    								//                      - ȯ�޼��� �ʰ� : 12
	    							    //                      - ���괩���� ���� ���ҽŰ� : 13
	    								//                      - ��Ÿ : 91"
	        sndForm.setMap("CHG_REASON"    ,   "");    // (2)  �����Ű����   -   �����Ű���ʼ�
	    							    //                      - ����ǥ���� ���ҽŰ� : 21
	    								//                      - ���⼼���� ���ҽŰ� : 22
	    							    //                      - ȯ�޼��� �ʰ� : 23
	    								//                      - Ư��¡���ǹ����� ����������� ���� :24
	    								//                      - ��Ÿ : 92"
	        sndForm.setMap("REASON_DT"     ,   "");    // (8)  �����߻���   -   �����Ű���ʼ�
	        sndForm.setMap("ETC_MEMO"      ,   "");    // (200)���   -   �����Ű�� ���
	        sndForm.setMap("SUNAP_DT"      ,   "");    // (8)  ��������   -   �����Ű�� ���
	        sndForm.setMap("EVI_DOC1"      ,   "" );    // (100)������
	        sndForm.setMap("EVI_DOC_URL1"  ,   "" );    // (100)������ȸURL
	        sndForm.setMap("EVI_DOC2"      ,   "" );    // (100)������2
	        sndForm.setMap("EVI_DOC_URL2"  ,   "" );    // (100)������ȸURL2
	        sndForm.setMap("EVI_DOC3"      ,   "" );    // (100)������2
	        sndForm.setMap("EVI_DOC_URL3"  ,   "" );    // (100)������ȸURL2
	    	    
	    	//log.debug("sndForm :: " + sndForm);
	    	    
	    	    
	    	try {
	    	    	
	    	    // ����ҵ漼 ���ռҵ�� �Ű� �ۼ��� ���� ȣ��
	    	    Tax004Service  soapservice = new  Tax004Service();
	    	    	
	    	    MapForm recvForm = null;
	    	    	
	    	    try{
	    	    	recvForm = soapservice.sndService(sndForm);
	    	    }catch(Exception e){
	    	    	e.printStackTrace();
	    	    	log.debug(" soapservice ����ҵ漼 ���ռҵ�� ���ڽŰ� ���� ");	   
	    	    	//RE_CO = "1000";
	    	    	cmMap.setMap("RESP_CODE" , "1000");     /*�����ڵ�*/
		    		cmMap.setMap("TX_GUBUN"  , "160");
		    		return DFL_005.makeSendBuffer(cmMap, taxMap);
	    	    }
	    	    	
	    	    //log.debug("****recvForm*********"+recvForm+"******************* ");
	    	    taxMap.putAll(recvForm);
	    	    	
	    	    //log.debug("***************************************************"+taxMap.getMap("MESSAGE").toString()+"*���� SVR01�̸� ����� *******************************************");
	    	    if(taxMap.getMap("MESSAGE").equals("SVR01")){
	    	    		
	    	    	log.debug("************ ���ռҵ� �Ű� ����ϱ� *********");
	    	    		 
	    	    	if(taxMap.getMap("EPAY_NO").equals("") || taxMap.getMap("EPAY_NO").toString() == null){
	    	    		cmMap.setMap("RESP_CODE" , "3010");     /*�����ڵ� */
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
	    	    	//	sqlService_cyber.queryForInsert("TXDM2610.insertEs1121Tax004", taxMap);//�μ�Ʈ
	    	    	//}catch(Exception e){
	    	    	//	e.printStackTrace();
	    	    	//	log.debug("��� ���� : " + taxMap);   	    			
	    	    	//	cmMap.setMap("RESP_CODE" , "8000");     /*�����ڵ�*/
	        		//   cmMap.setMap("TX_GUBUN"  , "160");
	        		//    return DFL_004.makeSendBuffer(cmMap, taxMap);    	    			
	    	    	//}	    		
	    	    	
	    	    }else{	    		
	    	    	log.debug("************ ����ҵ漼 ���ռҵ�� ������ ���� *********");
	    	    	log.debug("************ MESSAGE ****** : " + taxMap.getMap("MESSAGE"));
	    	    		
	    	    	cmMap.setMap("RESP_CODE" , "9000");     /*�����ڵ�*/
	    			cmMap.setMap("TX_GUBUN"  , "160");
	    			return DFL_004.makeSendBuffer(cmMap, taxMap);
	    	    }
	    	    		    	
	    	    	
	    	} catch(Exception e) {
	    	    log.debug("����ҵ漼 ���ռҵ�� ���ڽŰ� ���� �ۼ��ſ��� ");
	    	    cmMap.setMap("RESP_CODE" , "9000");     /*�����ڵ�*/
				cmMap.setMap("TX_GUBUN"  , "160");
				return DFL_004.makeSendBuffer(cmMap, taxMap);  
	    	} finally {
	    	    
	        }
	    
	    	/******************************************************
	        * ǥ�� ����� �ڷ� ���� ó��  END
	        * *****************************************************/
	    		
	        // �޾ƿ� ���� ����
	    	taxMap.setMap("STATE", state);
	    	taxMap.setMap("ERR_MSG", err_msg);
	        
	    	try{
	    	    //���� ����
	    	    sqlService_cyber.queryForInsert("TXDM2610.insertBaroPayData", taxMap);
	    	}catch(Exception e){
	    	    e.printStackTrace();
	    	    log.debug("�������̺� ��� ����");   	    	
	    	    cmMap.setMap("RESP_CODE" , "8000");     /*�����ڵ�*/
				cmMap.setMap("TX_GUBUN"  , "160");
				return DFL_004.makeSendBuffer(cmMap, taxMap);  
	    	}
	    	    
	    	//TAX_SNO,EPAY_NO,TAX_NO ���� Ȯ��
	    	if(taxMap.getMap("EPAY_NO") == "" || taxMap.getMap("EPAY_NO") == null){
	    	    //RE_CO = "1000";
	    	    cmMap.setMap("RESP_CODE" , "1000");     /*�����ڵ�*/
				cmMap.setMap("TX_GUBUN"  , "160");
				return DFL_004.makeSendBuffer(cmMap, taxMap);
	    	}
			
	    	
	    }catch(Exception e){
			e.printStackTrace();
			//RE_CO = "1000";
			log.debug("���� ���� ���� ");   	    	
    	    cmMap.setMap("RESP_CODE" , "1000");     /*�����ڵ�*/
			cmMap.setMap("TX_GUBUN"  , "160");
			return DFL_004.makeSendBuffer(cmMap, taxMap);  
		}
	    		    
		// ���� �� ����
		cmMap.setMap("RESP_CODE" , "0000");     /*�����ڵ�*/
		cmMap.setMap("TX_GUBUN"  , "160");
		return DFL_004.makeSendBuffer(cmMap, taxMap);		
	}
	    
	    

	// �ֹμ� ��� ���ڽŰ� ǥ�����漼 ����
	private byte[] sendJobTax005(MapForm cmMap, MapForm taxMap) {
			
		/******************************************************
		* ǥ�� ����� �ڷ� ���� ó��  START
		* *****************************************************/
		MapForm   sndForm  = new  MapForm();
			
		try{
			sndForm.setMap("tax_gubun"	  ,   "140002");	  //�ý�����
			sndForm.setMap("SIDO_COD"     ,   "26");    // �õ��ڵ�
			sndForm.setMap("SGG_COD"      ,   CbUtil.checkNull(taxMap.getMap("SGG_COD").toString()));    // �ñ����ڵ�
			sndForm.setMap("TAX_ITEM"     ,   "140002");    // �����ڵ�
			sndForm.setMap("TAX_YYMM"     ,   CbUtil.checkNull(taxMap.getMap("TAX_YY").toString()+taxMap.getMap("TAX_MM").toString()));    // �������
			sndForm.setMap("TAX_DIV"      ,   "3");    // ��������
		    	
		    String bucd = getLdongCode(taxMap.getMap("SGG_COD").toString(), taxMap.getMap("HACD").toString());   	
		
		    //String address = getAddress(taxMap.getMap("ZIP_CD").toString());	    	
		    //taxMap.setMap("ADDRESS", address);
		    	
			sndForm.setMap("ADONG_COD"    ,   CbUtil.checkNull(taxMap.getMap("HACD").toString()));    // ����������
			      
			sndForm.setMap("TAX_DT"       ,   taxMap.getMap("TAX_YY").toString()+taxMap.getMap("TAX_MM").toString()+taxMap.getMap("TAX_DD").toString());    // �Ű�����
			sndForm.setMap("REG_NO"       ,   CbUtil.checkNull(taxMap.getMap("REG_NO").toString()));  // ������ �ֹ�/���ι�ȣ
			sndForm.setMap("REG_NM"       ,   CbUtil.checkNull(taxMap.getMap("DPNM").toString()));    // ������ ����
			sndForm.setMap("TPR_COD"      ,   (taxMap.getMap("DP_GBN").equals("0"))?"00":"20");    // �����ڵ�
			sndForm.setMap("LDONG_COD"    ,   CbUtil.checkNull(bucd) + "00");    // ������ ������
			sndForm.setMap("ZIP_NO"       ,   CbUtil.checkNull(taxMap.getMap("ZIP_CD").toString()));    // ������ �����ȣ
			sndForm.setMap("ZIP_ADDR"     ,   CbUtil.checkNull(taxMap.getMap("ADDR").toString()));    // ������ �����ּ�
			sndForm.setMap("OTH_ADDR"     ,   " " + CbUtil.checkNull(taxMap.getMap("ADDR_DT").toString()));    // ������ ���ּ�
			sndForm.setMap("MO_TEL"       ,   CbUtil.checkNull(taxMap.getMap("PHONE_NO").toString()));    // ������ �ڵ�����ȣ
			sndForm.setMap("TEL"          ,   CbUtil.checkNull(taxMap.getMap("TEL_NO").toString()));    // ������ ��ȭ��ȣ
			sndForm.setMap("BIZ_NO"       ,   CbUtil.checkNull(taxMap.getMap("SAUP_NO").toString()));    // ������ ����ڹ�ȣ
			sndForm.setMap("CMP_NM"       ,   CbUtil.checkNull(taxMap.getMap("DPNM").toString()));    // ��ȣ��
			sndForm.setMap("RVSN_YY"      ,   CbUtil.checkNull(taxMap.getMap("RVSN_YY").toString()));    // �ͼӳ⵵
			sndForm.setMap("DUE_DT"       ,   CbUtil.checkNull(taxMap.getMap("DUE_DT").toString()));    // ��������
			sndForm.setMap("REQ_DIV"      ,   "11");    // �Ű��� 11 Ȯ���Ű� 13 �����Ű� 14 �����Ű�
			sndForm.setMap("RTN_INC_DT"   ,   taxMap.getMap("TAX_YY").toString()+taxMap.getMap("TAX_MM").toString()+taxMap.getMap("TAX_DD").toString());    // �絵�ҵ�����
			sndForm.setMap("RTN_ZIP_NO"   ,   CbUtil.checkNull(taxMap.getMap("RTN_ZIP_CD").toString()));    // �絵������ �����ȣ
			sndForm.setMap("RTN_ADDR"     ,   CbUtil.checkNull(taxMap.getMap("RTN_ADDR").toString()));    // �絵������ �ּ�
			sndForm.setMap("RTNTX"        ,   CbUtil.checkNull(taxMap.getMap("KJSA").toString()));    // �絵�ҵ漼
			sndForm.setMap("RSTX_RTN"     ,   CbUtil.checkNull(taxMap.getMap("JMSA").toString()));    // ����ҵ漼 �絵�ҵ漼��
			sndForm.setMap("RADTX"        ,   "0");    // �Ű�Ҽ��� ���꼼
			sndForm.setMap("PADTX"        ,   "0");    // ���κҼ��� ���꼼
			sndForm.setMap("TAX_RT"       ,   "10");    // ����
			sndForm.setMap("ADTX_YN"      ,   "2");    // ���꼼����
			sndForm.setMap("DLQ_CNT"      ,   "0");    // ���������ϼ�
			sndForm.setMap("TOT_ADTX"     ,   "0");    // ���꼼 �հ�
			sndForm.setMap("TOT_AMT"      ,   CbUtil.checkNull(taxMap.getMap("SUM_RCP").toString()));    // �ѳ��μ���
			sndForm.setMap("F_DUE_DT"     ,   CbUtil.checkNull(taxMap.getMap("DUE_DT").toString()));    // ���ʳ���
			sndForm.setMap("RPT_REG_NO"   ,   taxMap.getMap("REG_NO").toString());    // �Ű��� �ֹ�/���ι�ȣ
			sndForm.setMap("RPT_NM"       ,   CbUtil.checkNull(taxMap.getMap("DPNM").toString()));    // �Ű��� ����/���θ�
			sndForm.setMap("RPT_TEL"      ,   CbUtil.checkNull(taxMap.getMap("PHONE_NO").toString()));    // �Ű��� ��ȭ��ȣ
			sndForm.setMap("RPT_ID"       ,   "ETAX9");    // �Ű���ID
			sndForm.setMap("RPT_ADMIN"    ,   CbUtil.checkNull(taxMap.getMap("DPNM").toString()));    // �Ű��� ���/�μ���
			sndForm.setMap("RPT_SYSTEM"   ,   "ETAX");    // �Ű� �ý��� ����
			sndForm.setMap("SINGO_DIV"    ,   "");    // �Ű���
			sndForm.setMap("B_TAX_NO"     ,   "");    // ���ʽŰ�� ������ȣ
			sndForm.setMap("B_EPAY_NO"    ,   "");    // ���ʽŰ�� ���ڳ��ι�ȣ
			sndForm.setMap("CHG_MEMO"     ,   "");    // �����Ű���
			sndForm.setMap("CHG_REASON"   ,   "");    // �����Ű����
			sndForm.setMap("REASON_DT"    ,   "");    // �����߻���
			sndForm.setMap("ETC_MEMO"     ,   "");    // ���
			sndForm.setMap("SUNAP_DT"     ,   "");    // ��������
			sndForm.setMap("EVI_DOC1"     ,   "");    // ������
			sndForm.setMap("EVI_DOC_URL1" ,   "");    // ������ȸURL
			sndForm.setMap("EVI_DOC2"     ,   "");    // ������2
			sndForm.setMap("EVI_DOC_URL2" ,   "");    // ������ȸURL2
			sndForm.setMap("EVI_DOC3"     ,   "");    // ������2
			sndForm.setMap("EVI_DOC_URL3" ,   "");    // ������ȸURL2
			    
			//log.debug(sndForm);
			    
			//�Ű���
			if(sndForm.getMap("REQ_DIV").equals("13")){	//�����Ű�
				taxMap.setMap("SGGB", "1");
			}
			if(sndForm.getMap("REQ_DIV").equals("11")){	//Ȯ���Ű�
				taxMap.setMap("SGGB", "2");
			}
				
			try {
			    // ���漼 �絵�ҵ�� �Ű� �ۼ��� ���� ȣ��
			    Tax005Service  soapservice = new  Tax005Service();
			    	
			    //log.debug("****sndForm*********"+sndForm+"******************* ");
			    	
			    MapForm recvForm = null;
			    	
			    try{
			    	recvForm = soapservice.sndService(sndForm);
			    }catch(Exception e){
			    	e.printStackTrace();
			    	//RE_CO = "1000";
			    	cmMap.setMap("RESP_CODE" , "1000");     /*�����ڵ�*/
		    		cmMap.setMap("TX_GUBUN"  , "160");
		    		return DFL_005.makeSendBuffer(cmMap, taxMap);	
			    }
			    	
			    //log.debug("****recvForm*********"+recvForm+"******************* ");
			    	
			    taxMap.putAll(recvForm);
			    	
			    if(taxMap.getMap("MESSAGE").equals("SVR01")){		
			    		
			    	if(taxMap.getMap("EPAY_NO").equals("") || taxMap.getMap("EPAY_NO").toString() == null){
	    	    		//���αݾ��� �Ҿ��� ��� ���ڳ��ι�ȣ�� ������ �Ҿ�´�.--����ó��
			    		cmMap.setMap("RESP_CODE" , "3010");     /*�����ڵ� */
			    		cmMap.setMap("TX_GUBUN"  , "160");
			    		return DFL_005.makeSendBuffer(cmMap, taxMap);
	    	    	}
			    	taxMap.setMap("BUCD", bucd);
			    	taxMap.setMap("YYMD", sndForm.getMap("TAX_DT").toString());
			    	taxMap.setMap("REQ_SNMH", taxMap.getMap("DPNM").toString());
			    	taxMap.setMap("REQ_NM", taxMap.getMap("DPNM").toString());
			    	taxMap.setMap("F_DUE_DT", taxMap.getMap("DUE_DT").toString());
			    			
			    	log.debug("************ �絵�ҵ�� �Ű� ����ϱ� *********");
			    	//log.debug("**** taxMap *" + taxMap + "******************* ");
			    		

			    }else{

			    	log.debug("************ ������ ���� *********");
			    	cmMap.setMap("RESP_CODE" , "9000");     /*�����ڵ�*/
		    		cmMap.setMap("TX_GUBUN"  , "160");
		    		return DFL_005.makeSendBuffer(cmMap, taxMap);
			    }    
			    	
			} catch(Exception e) {
			    	
			    log.debug("�絵�ҵ�� �Ű� ���� �ۼ��ſ��� ");
			    cmMap.setMap("RESP_CODE" , "9000");     /*�����ڵ�*/
	    		cmMap.setMap("TX_GUBUN"  , "160");
	    		return DFL_005.makeSendBuffer(cmMap, taxMap);
			} finally {
			    	//clear();
			}
				
		    /******************************************************
		    * ǥ�� ����� �ڷ� ���� ó��  END
			* *****************************************************/
			    
			// �޾ƿ� ���� ����
			taxMap.setMap("STATE", state);
			    		        
			try{
			    sqlService_cyber.queryForInsert("TXDM2610.insertBaroPayData", taxMap);
			}catch(Exception e){
			    e.printStackTrace();
			    log.debug("�������̺� ��� ����");
			    cmMap.setMap("RESP_CODE" , "8000");     /*�����ڵ�*/
	    		cmMap.setMap("TX_GUBUN"  , "160");
	    		return DFL_005.makeSendBuffer(cmMap, taxMap);
			}
			    
		}catch(Exception e){
				
			e.printStackTrace();
			cmMap.setMap("RESP_CODE" , "1000");     /*�����ڵ�*/
			cmMap.setMap("TX_GUBUN"  , "160");
			return DFL_005.makeSendBuffer(cmMap, taxMap);
		}
		    
		// ���� �� ����
		cmMap.setMap("RESP_CODE" , "0000");     /*�����ڵ�*/
		cmMap.setMap("TX_GUBUN"  , "160");
		return DFL_005.makeSendBuffer(cmMap, taxMap);
	}

	
	// �ֹμ� Ư��¡�� ���ڽŰ� ǥ�����漼 ����
	private byte[] sendJobTax008(MapForm cmMap, MapForm taxMap) {

		
		log.debug("******************************************************");
		log.debug("* ǥ�� ����� Ư��¡�� �Ű� ���� ó��  START");
		log.debug("******************************************************");
		//log.debug("taxMap :: " + taxMap);
		
		/*������ ����*/
		//�� �ο�
		
		String ADONG_COD = "";
		String BUCD = "";
		long tot_emp = 0L;
		long tot_std = 0L;
		long tot_tax = 0L;
		
		String INTX = "";
		String TOT_ADTX = "";
		
		try{
		
			//log.debug("�� �ο�  üũ ����");
			tot_emp = Long.parseLong(CbUtil.checkNullZero(taxMap.getMap("TXTP_EMP11").toString())) + Long.parseLong(CbUtil.checkNullZero(taxMap.getMap("TXTP_EMP12").toString()))
		             + Long.parseLong(CbUtil.checkNullZero(taxMap.getMap("TXTP_EMP13").toString())) + Long.parseLong(CbUtil.checkNullZero(taxMap.getMap("TXTP_EMP14").toString()))
		             + Long.parseLong(CbUtil.checkNullZero(taxMap.getMap("TXTP_EMP16").toString())) + Long.parseLong(CbUtil.checkNullZero(taxMap.getMap("TXTP_EMP17").toString()))
		             + Long.parseLong(CbUtil.checkNullZero(taxMap.getMap("TXTP_EMP21").toString())) + Long.parseLong(CbUtil.checkNullZero(taxMap.getMap("TXTP_EMP22").toString()))
		             + Long.parseLong(CbUtil.checkNullZero(taxMap.getMap("TXTP_EMP31").toString())) + Long.parseLong(CbUtil.checkNullZero(taxMap.getMap("TXTP_EMP32").toString())) + Long.parseLong(CbUtil.checkNullZero(taxMap.getMap("TXTP_EMP34").toString()))
		             + Long.parseLong(CbUtil.checkNullZero(taxMap.getMap("TXTP_EMP33").toString())) + Long.parseLong(CbUtil.checkNullZero(taxMap.getMap("TXTP_EMP91").toString()));
		
			//log.debug("�� �ο�  üũ :: "  + tot_emp);
			//�� ����ǥ��
			//log.debug("�� ����ǥ��  üũ ����");
			tot_std = Long.parseLong(CbUtil.checkNullZero(taxMap.getMap("TXTP_STD11").toString())) + Long.parseLong(CbUtil.checkNullZero(taxMap.getMap("TXTP_STD12").toString()))
	                 + Long.parseLong(CbUtil.checkNullZero(taxMap.getMap("TXTP_STD13").toString())) + Long.parseLong(CbUtil.checkNullZero(taxMap.getMap("TXTP_STD14").toString()))
	                 + Long.parseLong(CbUtil.checkNullZero(taxMap.getMap("TXTP_STD16").toString())) + Long.parseLong(CbUtil.checkNullZero(taxMap.getMap("TXTP_STD17").toString()))
	                 + Long.parseLong(CbUtil.checkNullZero(taxMap.getMap("TXTP_STD21").toString())) + Long.parseLong(CbUtil.checkNullZero(taxMap.getMap("TXTP_STD22").toString()))
	                 + Long.parseLong(CbUtil.checkNullZero(taxMap.getMap("TXTP_STD31").toString())) + Long.parseLong(CbUtil.checkNullZero(taxMap.getMap("TXTP_STD32").toString())) + Long.parseLong(CbUtil.checkNullZero(taxMap.getMap("TXTP_STD34").toString()))
	                 + Long.parseLong(CbUtil.checkNullZero(taxMap.getMap("TXTP_STD33").toString())) + Long.parseLong(CbUtil.checkNullZero(taxMap.getMap("TXTP_STD91").toString()));
			//log.debug("�� ����ǥ��  üũ ::  " + tot_std);
		
			//�� ����ҵ漼             
			//log.debug("�� ����ҵ漼  üũ ����");
			tot_tax = Long.parseLong(CbUtil.checkNullZero(taxMap.getMap("TXTP_INTX11").toString())) + Long.parseLong(CbUtil.checkNullZero(taxMap.getMap("TXTP_INTX12").toString()))
                     + Long.parseLong(CbUtil.checkNullZero(taxMap.getMap("TXTP_INTX13").toString())) + Long.parseLong(CbUtil.checkNullZero(taxMap.getMap("TXTP_INTX14").toString()))
                     + Long.parseLong(CbUtil.checkNullZero(taxMap.getMap("TXTP_INTX16").toString())) + Long.parseLong(CbUtil.checkNullZero(taxMap.getMap("TXTP_INTX17").toString()))
                     + Long.parseLong(CbUtil.checkNullZero(taxMap.getMap("TXTP_INTX21").toString())) + Long.parseLong(CbUtil.checkNullZero(taxMap.getMap("TXTP_INTX22").toString()))
                     + Long.parseLong(CbUtil.checkNullZero(taxMap.getMap("TXTP_INTX31").toString())) + Long.parseLong(CbUtil.checkNullZero(taxMap.getMap("TXTP_INTX32").toString())) + Long.parseLong(CbUtil.checkNullZero(taxMap.getMap("TXTP_INTX34").toString()))
                     + Long.parseLong(CbUtil.checkNullZero(taxMap.getMap("TXTP_INTX33").toString())) + Long.parseLong(CbUtil.checkNullZero(taxMap.getMap("TXTP_INTX91").toString()));
	       
			//log.debug("�� ����ҵ漼  üũ :: " + tot_tax);
		
		
//			if(tot_emp < 1){
//				log.debug("�� �ο� ���� :: " + tot_emp);
//				cmMap.setMap("RESP_CODE" , "1000");     /*�����ڵ�*/
//				cmMap.setMap("TX_GUBUN"  , "260");
//				return DFL_008.makeSendBuffer(cmMap, taxMap);
//			}
//		
//			if(tot_std < 1){
//				log.debug("�� ����ǥ�� ���� :: " + tot_std);
//				cmMap.setMap("RESP_CODE" , "1000");     /*�����ڵ�*/
//				cmMap.setMap("TX_GUBUN"  , "260");
//				return DFL_008.makeSendBuffer(cmMap, taxMap);
//			}
//		
//			if(tot_tax < 1){
//				log.debug("�� ����ҵ漼 ���� :: " + tot_tax);
//				cmMap.setMap("RESP_CODE" , "1000");     /*�����ڵ�*/
//				cmMap.setMap("TX_GUBUN"  , "260");
//				return DFL_008.makeSendBuffer(cmMap, taxMap);
//			}
		
			// ��ȭ��ȣ Ȯ�� - �ʼ�
			if(CbUtil.nullChk(taxMap.getMap("BIZ_TEL").toString()).length() < 6){
				log.debug("��ȭ��ȣ(BIZ_TEL) �ʼ��� ���� :: " + CbUtil.nullChk(taxMap.getMap("BIZ_TEL").toString()));
				cmMap.setMap("RESP_CODE" , "1000");     /*�����ڵ�*/
				cmMap.setMap("TX_GUBUN"  , "260");
				return DFL_008.makeSendBuffer(cmMap, taxMap);
			}
			
			//�ݱ� Ȯ��
			if(CbUtil.nullChk(taxMap.getMap("REQ_DIV")).equals("2")){
				String sup_mm = CbUtil.nullChk(taxMap.getMap("SUP_YYMM").toString()).substring(4);
				if(sup_mm.equals("06")||sup_mm.equals("12")){
					log.debug("�ݱ⼱��");
				}else{
					log.debug("�ݱ⼱�� ���� :: " + CbUtil.nullChk(taxMap.getMap("SUP_YYMM").toString()));
				}
			}
			//�ֹι�ȣ ���� üũ
			if(CbUtil.nullChk(taxMap.getMap("REG_NO").toString()).length() < 13){
				log.debug("�ֹι�ȣ ���� :: " + CbUtil.nullChk(taxMap.getMap("REG_NO").toString()));
				cmMap.setMap("RESP_CODE" , "1000");     /*�����ڵ�*/
				cmMap.setMap("TX_GUBUN"  , "260");
				return DFL_008.makeSendBuffer(cmMap, taxMap);
			}
			//�ͼӳ�� üũ
			if(CbUtil.nullChk(taxMap.getMap("RVSN_YYMM").toString()).length() < 6){
				log.debug("�ͼӳ�� ���� :: " + CbUtil.nullChk(taxMap.getMap("RVSN_YYMM").toString()));
				cmMap.setMap("RESP_CODE" , "1000");     /*�����ڵ�*/
				cmMap.setMap("TX_GUBUN"  , "260");
				return DFL_008.makeSendBuffer(cmMap, taxMap);
			}
				
			//�������ڵ� ���ϱ�
			ADONG_COD = getHacdCode(CbUtil.nullChk(taxMap.getMap("SGG_COD").toString()),CbUtil.nullChk(taxMap.getMap("LDONG_COD").toString()));   	
			if(ADONG_COD == null || ADONG_COD.equals("")){
				ADONG_COD = "000";
			}
			//�������ڵ�
			BUCD = CbUtil.nullChk(taxMap.getMap("LDONG_COD").toString()) + "00";
		
			log.debug("�������ڵ�  :: " + ADONG_COD + "�������ڵ�  :: " + BUCD);
		
			//����������ҵ漼 INTX, �����Ұ��꼼 TOT_ADTX  ���ϱ�
			//����������ҵ漼 = �����ѱݾ� - ���꼼    :  INTX = ADD_TOT_AMT - PAY_ADTX
			//�����Ұ��꼼 = �����ѱݾ� - ����������ҵ漼    : TOT_ADTX = ADD_TOT_AMT - INTX			
			long tempINTX = Long.parseLong(taxMap.getMap("ADD_TOT_AMT").toString()) - Long.parseLong(taxMap.getMap("PAY_ADTX").toString());
			long tempTOT_ADTX = Long.parseLong(taxMap.getMap("ADD_TOT_AMT").toString()) - tempINTX;
			INTX = Long.toString(tempINTX);
			TOT_ADTX = Long.toString(tempTOT_ADTX);
			log.debug("����������ҵ漼 :: " + INTX + "  �����Ұ��꼼 :: " + TOT_ADTX);
		
		}catch(Exception e){
			e.printStackTrace();
			cmMap.setMap("RESP_CODE" , "1000");     /*�����ڵ�*/
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
	        reqXml.append("<TXTP_CD><![CDATA[11]]></TXTP_CD>");  //���ڼҵ�
	        reqXml.append("<TXTP_EMP><![CDATA[" + CbUtil.nullChk(taxMap.getMap("TXTP_EMP11").toString())  + "]]></TXTP_EMP>"); //�ο�
	        reqXml.append("<TXTP_STD><![CDATA[" + CbUtil.nullChk(taxMap.getMap("TXTP_STD11").toString())  + "]]></TXTP_STD>"); //����ǥ��
	        reqXml.append("<TXTP_INTX><![CDATA["+ CbUtil.nullChk(taxMap.getMap("TXTP_INTX11").toString()) + "]]></TXTP_INTX>");//����ҵ漼	        	        
	        reqXml.append("</RECORD>");
	        reqXml.append("<RECORD name='ThrJ2PInfo'>");
	        reqXml.append("<TXTP_CD><![CDATA[12]]></TXTP_CD>");  //���ҵ�
	        reqXml.append("<TXTP_EMP><![CDATA[" + CbUtil.nullChk(taxMap.getMap("TXTP_EMP12").toString())  + "]]></TXTP_EMP>"); //�ο�
	        reqXml.append("<TXTP_STD><![CDATA[" + CbUtil.nullChk(taxMap.getMap("TXTP_STD12").toString())  + "]]></TXTP_STD>"); //����ǥ��
	        reqXml.append("<TXTP_INTX><![CDATA["+ CbUtil.nullChk(taxMap.getMap("TXTP_INTX12").toString()) + "]]></TXTP_INTX>");//����ҵ漼
	        reqXml.append("</RECORD>");
	        reqXml.append("<RECORD name='ThrJ2PInfo'>");
	        reqXml.append("<TXTP_CD><![CDATA[13]]></TXTP_CD>");  //����ҵ�
	        reqXml.append("<TXTP_EMP><![CDATA[" + CbUtil.nullChk(taxMap.getMap("TXTP_EMP13").toString())  + "]]></TXTP_EMP>"); //�ο�
	        reqXml.append("<TXTP_STD><![CDATA[" + CbUtil.nullChk(taxMap.getMap("TXTP_STD13").toString())  + "]]></TXTP_STD>"); //����ǥ��
	        reqXml.append("<TXTP_INTX><![CDATA["+ CbUtil.nullChk(taxMap.getMap("TXTP_INTX13").toString()) + "]]></TXTP_INTX>");//����ҵ漼
	        reqXml.append("</RECORD>");
	        reqXml.append("<RECORD name='ThrJ2PInfo'>");
	        reqXml.append("<TXTP_CD><![CDATA[14]]></TXTP_CD>");  //�ٷμҵ�
	        reqXml.append("<TXTP_EMP><![CDATA[" + CbUtil.nullChk(taxMap.getMap("TXTP_EMP14").toString())  + "]]></TXTP_EMP>"); //�ο�
	        reqXml.append("<TXTP_STD><![CDATA[" + CbUtil.nullChk(taxMap.getMap("TXTP_STD14").toString())  + "]]></TXTP_STD>"); //����ǥ��
	        reqXml.append("<TXTP_INTX><![CDATA["+ CbUtil.nullChk(taxMap.getMap("TXTP_INTX14").toString()) + "]]></TXTP_INTX>");//����ҵ漼
	        reqXml.append("</RECORD>");
	        reqXml.append("<RECORD name='ThrJ2PInfo'>");
	        reqXml.append("<TXTP_CD><![CDATA[16]]></TXTP_CD>");  //��Ÿ�ҵ�
	        reqXml.append("<TXTP_EMP><![CDATA[" + CbUtil.nullChk(taxMap.getMap("TXTP_EMP16").toString())  + "]]></TXTP_EMP>"); //�ο�
	        reqXml.append("<TXTP_STD><![CDATA[" + CbUtil.nullChk(taxMap.getMap("TXTP_STD16").toString())  + "]]></TXTP_STD>"); //����ǥ��
	        reqXml.append("<TXTP_INTX><![CDATA["+ CbUtil.nullChk(taxMap.getMap("TXTP_INTX16").toString()) + "]]></TXTP_INTX>");//����ҵ漼
	        reqXml.append("</RECORD>");
	        reqXml.append("<RECORD name='ThrJ2PInfo'>");
	        reqXml.append("<TXTP_CD><![CDATA[17]]></TXTP_CD>");  //���ݼҵ�
	        reqXml.append("<TXTP_EMP><![CDATA[" + CbUtil.nullChk(taxMap.getMap("TXTP_EMP17").toString())  + "]]></TXTP_EMP>"); //�ο�
	        reqXml.append("<TXTP_STD><![CDATA[" + CbUtil.nullChk(taxMap.getMap("TXTP_STD17").toString())  + "]]></TXTP_STD>"); //����ǥ��
	        reqXml.append("<TXTP_INTX><![CDATA["+ CbUtil.nullChk(taxMap.getMap("TXTP_INTX17").toString()) + "]]></TXTP_INTX>");//����ҵ漼
	        reqXml.append("</RECORD>");
	        reqXml.append("<RECORD name='ThrJ2PInfo'>");
	        reqXml.append("<TXTP_CD><![CDATA[21]]></TXTP_CD>");  //�����ҵ�
	        reqXml.append("<TXTP_EMP><![CDATA[" + CbUtil.nullChk(taxMap.getMap("TXTP_EMP21").toString())  + "]]></TXTP_EMP>"); //�ο�
	        reqXml.append("<TXTP_STD><![CDATA[" + CbUtil.nullChk(taxMap.getMap("TXTP_STD21").toString())  + "]]></TXTP_STD>"); //����ǥ��
	        reqXml.append("<TXTP_INTX><![CDATA["+ CbUtil.nullChk(taxMap.getMap("TXTP_INTX21").toString()) + "]]></TXTP_INTX>");//����ҵ漼
	        reqXml.append("</RECORD>");
	        reqXml.append("<RECORD name='ThrJ2PInfo'>");
	        reqXml.append("<TXTP_CD><![CDATA[22]]></TXTP_CD>");  //�絵�ҵ� �ҵ漼�� ��119��
	        reqXml.append("<TXTP_EMP><![CDATA[" + CbUtil.nullChk(taxMap.getMap("TXTP_EMP22").toString())  + "]]></TXTP_EMP>"); //�ο�
	        reqXml.append("<TXTP_STD><![CDATA[" + CbUtil.nullChk(taxMap.getMap("TXTP_STD22").toString())  + "]]></TXTP_STD>"); //����ǥ��
	        reqXml.append("<TXTP_INTX><![CDATA["+ CbUtil.nullChk(taxMap.getMap("TXTP_INTX22").toString()) + "]]></TXTP_INTX>");//����ҵ漼
	        reqXml.append("</RECORD>");
	        reqXml.append("<RECORD name='ThrJ2PInfo'>");
	        reqXml.append("<TXTP_CD><![CDATA[31]]></TXTP_CD>");  //�ܱ����� ���μ��� ��98��
	        reqXml.append("<TXTP_EMP><![CDATA[" + CbUtil.nullChk(taxMap.getMap("TXTP_EMP31").toString())  + "]]></TXTP_EMP>"); //�ο�
	        reqXml.append("<TXTP_STD><![CDATA[" + CbUtil.nullChk(taxMap.getMap("TXTP_STD31").toString())  + "]]></TXTP_STD>"); //����ǥ��
	        reqXml.append("<TXTP_INTX><![CDATA["+ CbUtil.nullChk(taxMap.getMap("TXTP_INTX31").toString()) + "]]></TXTP_INTX>");//����ҵ漼
	        reqXml.append("</RECORD>");
	        reqXml.append("<RECORD name='ThrJ2PInfo'>");
	        reqXml.append("<TXTP_CD><![CDATA[32]]></TXTP_CD>");  //��������
	        reqXml.append("<TXTP_EMP><![CDATA[" + CbUtil.nullChk(taxMap.getMap("TXTP_EMP32").toString())  + "]]></TXTP_EMP>"); //�ο�
	        reqXml.append("<TXTP_STD><![CDATA[" + CbUtil.nullChk(taxMap.getMap("TXTP_STD32").toString())  + "]]></TXTP_STD>"); //����ǥ��
	        reqXml.append("<TXTP_INTX><![CDATA["+ CbUtil.nullChk(taxMap.getMap("TXTP_INTX32").toString()) + "]]></TXTP_INTX>");//����ҵ漼
	        reqXml.append("</RECORD>");
	        reqXml.append("<RECORD name='ThrJ2PInfo'>");
	        reqXml.append("<TXTP_CD><![CDATA[33]]></TXTP_CD>");  //�������� ���μ��� ��73��
	        reqXml.append("<TXTP_EMP><![CDATA[" + CbUtil.nullChk(taxMap.getMap("TXTP_EMP33").toString())  + "]]></TXTP_EMP>"); //�ο�
	        reqXml.append("<TXTP_STD><![CDATA[" + CbUtil.nullChk(taxMap.getMap("TXTP_STD33").toString())  + "]]></TXTP_STD>"); //����ǥ��
	        reqXml.append("<TXTP_INTX><![CDATA["+ CbUtil.nullChk(taxMap.getMap("TXTP_INTX33").toString()) + "]]></TXTP_INTX>");//����ҵ漼
	        reqXml.append("</RECORD>");
	        reqXml.append("<RECORD name='ThrJ2PInfo'>");
	        reqXml.append("<TXTP_CD><![CDATA[91]]></TXTP_CD>");  //�ܱ������κ��� ���� �ҵ�(������)
	        reqXml.append("<TXTP_EMP><![CDATA[" + CbUtil.nullChk(taxMap.getMap("TXTP_EMP91").toString())  + "]]></TXTP_EMP>"); //�ο�
	        reqXml.append("<TXTP_STD><![CDATA[" + CbUtil.nullChk(taxMap.getMap("TXTP_STD91").toString())  + "]]></TXTP_STD>"); //����ǥ��
	        reqXml.append("<TXTP_INTX><![CDATA["+ CbUtil.nullChk(taxMap.getMap("TXTP_INTX91").toString()) + "]]></TXTP_INTX>");//����ҵ漼
	        reqXml.append("</RECORD>");
	        reqXml.append("</LIST>");
		    reqXml.append("</CONTENT>");
	        reqXml.append("</LTIS>");
		    
	        log.debug("reqXml 666 :: " + reqXml);
	        
		    try{    			    
			    // ���漼 �絵�ҵ�� �Ű� �ۼ��� ���� ȣ��
			    Tax008Service  soapservice = new  Tax008Service();
		    	
			    MapForm recvForm = null;
			    	
			    try{
			    	recvForm = soapservice.sndService(reqXml, taxMap.getMap("SGG_COD").toString());;
			    }catch(Exception e){
			    	e.printStackTrace();
			    	//RE_CO = "1000";	    
			    	cmMap.setMap("RESP_CODE" , "1000");     /*�����ڵ�*/
		    		cmMap.setMap("TX_GUBUN"  , "260");
		    		return DFL_008.makeSendBuffer(cmMap, taxMap);
			    }
			    	
			    //log.debug("****recvForm*********"+recvForm+"******************* ");
			    	
			    taxMap.putAll(recvForm);
			    			 
			    if(taxMap.getMap("MESSAGE").equals("SVR01")){
			    		
			    	if(taxMap.getMap("EPAY_NO").equals("") || taxMap.getMap("EPAY_NO").toString() == null){
	    	    		//���αݾ��� �Ҿ��� ��� ���ڳ��ι�ȣ�� ������ �Ҿ�´�.--����ó��
			    		cmMap.setMap("RESP_CODE" , "3010");     /*�����ڵ� */
			    		cmMap.setMap("TX_GUBUN"  , "260");
			    		return DFL_008.makeSendBuffer(cmMap, taxMap);
	    	    	}
			    	//taxMap.setMap("BUCD", bucd);	    		
			    	taxMap.setMap("YYMD", taxMap.getMap("TAX_DT").toString());
			    	taxMap.setMap("REQ_SNMH", taxMap.getMap("DPNM").toString());
			    	taxMap.setMap("REQ_NM", taxMap.getMap("DPNM").toString());
			    	taxMap.setMap("F_DUE_DT", taxMap.getMap("DUE_DT").toString());
			    			    		
			    	log.debug("************ Ư��¡�� �Ű� �Ϸ� *********");
			    		
			    }else{

			    	log.debug("************ Ư��¡�� ������ ���� *********");
			    	cmMap.setMap("RESP_CODE" , "9000");     /*�����ڵ�*/
		    		cmMap.setMap("TX_GUBUN"  , "260");
		    		return DFL_008.makeSendBuffer(cmMap, taxMap);
			    }    
			    	
			}catch(Exception e){
			    	
			    log.debug("Ư��¡�� �Ű� ���� �ۼ��ſ��� ");
			    cmMap.setMap("RESP_CODE" , "9000");     /*�����ڵ�*/
	    		cmMap.setMap("TX_GUBUN"  , "260");
	    		return DFL_008.makeSendBuffer(cmMap, taxMap);
			   
			}finally{
			    //clear();
			}
		    
					
			/******************************************************
			* ǥ�� ����� �ڷ� ���� ó��  END
			* *****************************************************/
				    
			// �޾ƿ� ���� ����
			taxMap.setMap("STATE", state);		    
			
			taxMap.setMap("JYMD", taxMap.getMap("SUP_YYMM"));   //���޳��
			
			taxMap.setMap("WANT", tot_emp);   //�� �ο�
			taxMap.setMap("GWAT", tot_std);   //�� ����ǥ��
			taxMap.setMap("TAXT", tot_tax);   //�� ����ҵ漼
			
			taxMap.setMap("HAMT", taxMap.getMap("ADD_SUM_RTN")); //ȯ�ޱ��հ�
			
			taxMap.setMap("SUM_RCP", taxMap.getMap("NAPBU_TAX")); //�����ѱݾ�
			
			taxMap.setMap("TAX_DD", taxMap.getMap("TX_DATETIME").toString().substring(6, 8));
			taxMap.setMap("TAX_TIME",taxMap.getMap("TX_DATETIME").toString().substring(8));
			
			try{
				sqlService_cyber.queryForInsert("TXDM2610.insertBaroPayData", taxMap);
			}catch(Exception e){
				e.printStackTrace();
				log.debug("�������̺� ��� ����");
				cmMap.setMap("RESP_CODE" , "8000");     /*�����ڵ�*/
				cmMap.setMap("TX_GUBUN"  , "260");
		    	return DFL_008.makeSendBuffer(cmMap, taxMap);
			}
		    
		    
				    
		}catch(Exception e){
					
			e.printStackTrace();
			cmMap.setMap("RESP_CODE" , "1000");     /*�����ڵ�*/
			cmMap.setMap("TX_GUBUN"  , "260");
			return DFL_008.makeSendBuffer(cmMap, taxMap);
		}
			
	
		// ���� �� ����
		cmMap.setMap("RESP_CODE" , "0000");     /*�����ڵ�*/
		cmMap.setMap("TX_GUBUN"  , "260");
		return DFL_008.makeSendBuffer(cmMap, taxMap);
	} 
		
	
	// �ֹμ� ���� ���ڽŰ� ǥ�����漼 ����
	private byte[] sendJobTax009(MapForm cmMap, MapForm taxMap) {
			    	
		/******************************************************
	    * ǥ�� ����� �ڷ� ���� ó��  START
		* *****************************************************/
		MapForm sndForm = new MapForm();
			
		try{
			sndForm.setMap("tax_gubun"	       ,   "104009"); // �ý�����
			sndForm.setMap("SIDO_COD"          ,   "26");     // �õ��ڵ�
			sndForm.setMap("SGG_COD"           ,   CbUtil.checkNull(taxMap.getMap("SGG_COD").toString())); // �ñ����ڵ�
			sndForm.setMap("TAX_ITEM"          ,   "104009");    // �����ڵ�
			sndForm.setMap("TAX_YYMM"          ,   CbUtil.checkNull(taxMap.getMap("TAX_YY").toString()+taxMap.getMap("TAX_MM").toString()));    // �������
			sndForm.setMap("TAX_DIV"           ,   "3");         // ��������
		    	
		    String bucd = getLdongCode(taxMap.getMap("SGG_COD").toString(), taxMap.getMap("HACD").toString());   	
		    	
		    //String address = getAddress(taxMap.getMap("ZIP_CD").toString());
		    	
		    //taxMap.setMap("ADDRESS", address);
		    	
			sndForm.setMap("ADONG_COD"         ,   CbUtil.checkNull(taxMap.getMap("HACD").toString()));    // ����������
			  
			sndForm.setMap("TAX_DT"            ,   taxMap.getMap("TAX_YY").toString()+taxMap.getMap("TAX_MM").toString()+taxMap.getMap("TAX_DD").toString());    // �Ű�����
			sndForm.setMap("REG_NO"            ,   CbUtil.checkNull(taxMap.getMap("REG_NO").toString()));  // ������ �ֹ�/���ι�ȣ
			sndForm.setMap("REG_NM"            ,   CbUtil.checkNull(taxMap.getMap("DPNM").toString()));    // ������ ����
			sndForm.setMap("TPR_COD"           ,   (taxMap.getMap("DP_GBN").equals("0"))?"00":"20");    // �����ڵ� DP_GBN
			sndForm.setMap("LDONG_COD"         ,   CbUtil.checkNull(bucd) + "00");    // ������ ������
			      
			sndForm.setMap("ZIP_NO"            ,   CbUtil.checkNull(taxMap.getMap("ZIP_CD").toString()));    // ������ �����ȣ
			sndForm.setMap("ZIP_ADDR"          ,   CbUtil.checkNull(taxMap.getMap("ADDR").toString()));    // ������ �����ּ�
			sndForm.setMap("OTH_ADDR"          ,   " " + CbUtil.checkNull(taxMap.getMap("ADDR_DT").toString()));     // (100)  ������ ���ּ�
			sndForm.setMap("MO_TEL"            ,   CbUtil.checkNull(taxMap.getMap("PHONE_NO").toString()));    // ������ �ڵ�����ȣ
			sndForm.setMap("TEL"               ,   CbUtil.checkNull(taxMap.getMap("TEL_NO").toString()));    // ������ ��ȭ��ȣ
			sndForm.setMap("BIZ_NO"            ,   CbUtil.checkNull(taxMap.getMap("SAUP_NO").toString()));    // ������ ����ڹ�ȣ
			sndForm.setMap("CMP_NM"            ,   CbUtil.checkNull(taxMap.getMap("CMP_NM").toString()));     // (100)  ��ȣ��
		    sndForm.setMap("BIZ_ZIP_NO"        ,   CbUtil.checkNull(taxMap.getMap("ZIP_CD").toString()));    // (6)    ���������ȣ
		    sndForm.setMap("BIZ_ZIP_ADDR"      ,   CbUtil.checkNull(taxMap.getMap("ADDR").toString()));                  // (60)   ���������ּ�
		    sndForm.setMap("BIZ_ADDR"          ,   CbUtil.checkNull(taxMap.getMap("ADDR_DT").toString()));   // (100)  �������ּ�        
		    sndForm.setMap("RVSN_YY"           ,   CbUtil.checkNull(taxMap.getMap("RVSN_YY").toString()));    // (6)    �ͼӳ⵵
		        
		    sndForm.setMap("DUE_DT"            ,   CbUtil.checkNull(taxMap.getMap("DUE_DT").toString())); 
		    sndForm.setMap("F_DUE_DT"          ,   CbUtil.checkNull(taxMap.getMap("F_DUE_DT").toString())); 
		    sndForm.setMap("US_AREA"           ,   CbUtil.checkNull(taxMap.getMap("GSGY").toString()));  //�ѻ����� 

		    sndForm.setMap("TXE_AREA"          ,   "0"); //���������
		    sndForm.setMap("TXE_AREA_R"        ,   ""); //varchar(100)�������������
		    sndForm.setMap("TAX_AREA"          ,   CbUtil.checkNull(taxMap.getMap("GSGY").toString())); //�������� 
		    sndForm.setMap("TAX_STD"           ,   CbUtil.checkNull(taxMap.getMap("GSGY").toString())); //����ǥ��
		    sndForm.setMap("POL_YN"            ,   CbUtil.checkNull(taxMap.getMap("BECH").toString())); //������������(2:�� ,1:��)
		    sndForm.setMap("RDX_COD"           ,   ""); //(�߰�)�����ڵ�
		    sndForm.setMap("RDX_R"             ,   ""); //�������
		    sndForm.setMap("RDX_AMT"           ,   "0"); //���������
		    sndForm.setMap("TAX_RT"            ,   (taxMap.getMap("BECH").equals("0"))?"250":"500"); //����
		    sndForm.setMap("R_P_AMT"           ,   CbUtil.checkNull(taxMap.getMap("SGEK").toString())); //�Ű��μ���
		    sndForm.setMap("ADTX_YN"           ,   "2"); //���꼼(2:��,1:��)
		    sndForm.setMap("DLQ_CNT"           ,   "0"); //���������ϼ�
		    sndForm.setMap("RADTX"             ,   "0"); //�Ű�Ҽ��ǰ��꼼
		    sndForm.setMap("PADTX"             ,   "0"); //���κҼ��ǰ��꼼
		    sndForm.setMap("TOT_ADTX"          ,   "0"); //���꼼�װ�
		    sndForm.setMap("TOT_AMT"           ,   CbUtil.checkNull(taxMap.getMap("SUM_RCP").toString())); //�ѳ��αݾ�
		    sndForm.setMap("RPT_REG_NO"        ,   CbUtil.checkNull(taxMap.getMap("REG_NO").toString())); //�Ű����ֹ�/���ι�ȣ
		    sndForm.setMap("RPT_NM"            ,   CbUtil.checkNull(taxMap.getMap("DPNM").toString())); //�Ű��ڼ���/���θ�
		    sndForm.setMap("RPT_TEL"           ,   CbUtil.checkNull(taxMap.getMap("TEL_NO").toString())); //�Ű�����ȭ��ȣ
		    sndForm.setMap("RPT_ID"            ,   "ETAX9"); //�Ű���ID
		    sndForm.setMap("RPT_ADMIN"         ,   ""); //�Ű��ڱ��/�μ���
		    sndForm.setMap("RPT_SYSTEM"        ,   "ETAX"); //�Ű�ý��۱���
		
		    sndForm.setMap("SINGO_DIV"         ,   "0");    // (1)      �Ű���   -  ���ʽŰ�:1 , �����Ű�:2
		    sndForm.setMap("B_TAX_NO"          ,   "");    // (31)     ���ʽŰ�� ������ȣ   -  �����Ű���ʼ�
		    sndForm.setMap("B_EPAY_NO"         ,   "");    // (19)     ���ʽŰ�� ���ڳ��ι�ȣ   -  �����Ű���ʼ�
		    sndForm.setMap("CHG_MEMO"          ,   "");    // (2)      �����Ű���   -  �����Ű���ʼ�   - ���ҽŰ� : 11   - ȯ�޼��� �ʰ� : 12   - ���괩���� ���� ���ҽŰ� : 13   - ��Ÿ : 91
		    sndForm.setMap("CHG_REASON"        ,   "");    // (2)      �����Ű����   -  �����Ű���ʼ� - ����ǥ���� ���ҽŰ� : 21 - ���⼼���� ���ҽŰ� : 22 - ȯ�޼��� �ʰ� : 23 - Ư��¡���ǹ����� ����������� ���� :24 - ��Ÿ : 92
		    sndForm.setMap("REASON_DT"         ,   "");    // (8)      �����߻���   -  �����Ű���ʼ�
		    sndForm.setMap("ETC_MEMO"          ,   "");    // (200)    ���   -  �����Ű�� ���
		    sndForm.setMap("SUNAP_DT"          ,   "");    // (8)      ��������   -  �����Ű�� ���
		    sndForm.setMap("EVI_DOC1"          ,   "");    // (100)    ������
		    sndForm.setMap("EVI_DOC_URL1"      ,   "");    // (100)    ������ȸURL
		    sndForm.setMap("EVI_DOC2"          ,   "");    // (100)    ������2
		    sndForm.setMap("EVI_DOC_URL2"      ,   "");    // (100)    ������ȸURL2
		    sndForm.setMap("EVI_DOC3"          ,   "");    // (100)    ������3
		    sndForm.setMap("EVI_DOC_URL3"      ,   "");    // (100)    ������ȸURL3																										);    // (100)    ������ȸURL2
		        
		    sndForm.setMap("C_TAXADD_R"        ,   "");    // (CHAR2)  �Ű�Ҽ��ǰ��꼼����   -  <2013�ű�>
			sndForm.setMap("C_TAXADD_GUBUN"    ,   "");    // (CHAR2)  �����Ű����   -  <2013�ű�>
			    
			    
			sndForm.setMap("RADTX_RT"          ,   "");    // (NUMBER5.4)  �Ű�Ҽ��ǰ������뼼��   -  <2013�ű�>
			//�����Ű��϶��� ������� �޿��װ� �Ѱ���
			sndForm.setMap("F_SINGO_STD"       ,   "");    // (NUMBER14)  �����Ű������ǥ   -  <2013�ű�>
			    	    
			try{    			    
			    // ���漼 �絵�ҵ�� �Ű� �ۼ��� ���� ȣ��
			    Tax009Service  soapservice = new  Tax009Service();
			    	
			    //log.debug("****sndForm*********"+sndForm+"******************* ");
			    	
			    MapForm recvForm = null;
			    	
			    try{
			    	recvForm = soapservice.sndService(sndForm);
			    }catch(Exception e){
			    	e.printStackTrace();
			    	//RE_CO = "1000";	    
			    	cmMap.setMap("RESP_CODE" , "1000");     /*�����ڵ�*/
		    		cmMap.setMap("TX_GUBUN"  , "160");
		    		return DFL_009.makeSendBuffer(cmMap, taxMap);
			    }
			    	
			    //log.debug("****recvForm*********"+recvForm+"******************* ");
			    	
			    taxMap.putAll(recvForm);
			    			 
			    if(taxMap.getMap("MESSAGE").equals("SVR01")){
			    		
			    	if(taxMap.getMap("EPAY_NO").equals("") || taxMap.getMap("EPAY_NO").toString() == null){
	    	    		//���αݾ��� �Ҿ��� ��� ���ڳ��ι�ȣ�� ������ �Ҿ�´�.--����ó��
			    		cmMap.setMap("RESP_CODE" , "3010");     /*�����ڵ� */
			    		cmMap.setMap("TX_GUBUN"  , "160");
			    		return DFL_009.makeSendBuffer(cmMap, taxMap);
	    	    	}
			    	taxMap.setMap("BUCD", bucd);	    		
			    	taxMap.setMap("YYMD", sndForm.getMap("TAX_DT").toString());
			    	taxMap.setMap("REQ_SNMH", taxMap.getMap("DPNM").toString());
			    	taxMap.setMap("REQ_NM", taxMap.getMap("DPNM").toString());
			    	taxMap.setMap("F_DUE_DT", taxMap.getMap("DUE_DT").toString());
			    			    		
			    	log.debug("************ �ֹμ� ���� �Ű� �Ϸ� *********");
			    		
			    }else{

			    	log.debug("************ �ֹμ� ���� ������ ���� *********");
			    	cmMap.setMap("RESP_CODE" , "9000");     /*�����ڵ�*/
		    		cmMap.setMap("TX_GUBUN"  , "160");
		    		return DFL_009.makeSendBuffer(cmMap, taxMap);
			    }    
			    	
			}catch(Exception e){
			    	
			    log.debug("�ֹμ� ���� �Ű� ���� �ۼ��ſ��� ");
			    cmMap.setMap("RESP_CODE" , "9000");     /*�����ڵ�*/
	    		cmMap.setMap("TX_GUBUN"  , "160");
	    		return DFL_009.makeSendBuffer(cmMap, taxMap);
			   
			}finally{
			    //clear();
			}
				
			/******************************************************
		    * ǥ�� ����� �ڷ� ���� ó��  END
			* *****************************************************/
			    
			// �޾ƿ� ���� ����
			taxMap.setMap("STATE", state);		    
			        
			try{
			    sqlService_cyber.queryForInsert("TXDM2610.insertBaroPayData", taxMap);
			}catch(Exception e){
			    e.printStackTrace();
			    log.debug("�������̺� ��� ����");
			    cmMap.setMap("RESP_CODE" , "8000");     /*�����ڵ�*/
	    		cmMap.setMap("TX_GUBUN"  , "160");
	    		return DFL_009.makeSendBuffer(cmMap, taxMap);
			}
			    
		}catch(Exception e){
				
			e.printStackTrace();
			cmMap.setMap("RESP_CODE" , "1000");     /*�����ڵ�*/
			cmMap.setMap("TX_GUBUN"  , "160");
			return DFL_009.makeSendBuffer(cmMap, taxMap);
		}
		
		    
		// ���� �� ����
		cmMap.setMap("RESP_CODE" , "0000");     /*�����ڵ�*/
		cmMap.setMap("TX_GUBUN"  , "160");
		return DFL_005.makeSendBuffer(cmMap, taxMap);
	}
	    
		
	// �ֹμ� �������� ���ڽŰ� ǥ�����漼 ����
	private byte[] sendJobTax011(MapForm cmMap, MapForm taxMap) {
			    	
		/******************************************************
		* ǥ�� ����� �ڷ� ���� ó��  START
		* *****************************************************/
		MapForm sndForm  = new MapForm();
			
		try{
			sndForm.setMap("tax_gubun"	       ,   "104011");	  //�ý�����
			sndForm.setMap("SIDO_COD"          ,   "26");    // �õ��ڵ�
			sndForm.setMap("SGG_COD"           ,   CbUtil.checkNull(taxMap.getMap("SGG_COD").toString()));    // �ñ����ڵ�
			sndForm.setMap("TAX_ITEM"          ,   "104011");    // �����ڵ�
			sndForm.setMap("TAX_YYMM"          ,   CbUtil.checkNull(taxMap.getMap("TAX_YY").toString()+taxMap.getMap("TAX_MM").toString()));    // �������
			sndForm.setMap("TAX_DIV"           ,   "3");    // ��������
		    	
		    String bucd = getLdongCode(taxMap.getMap("SGG_COD").toString(), taxMap.getMap("HACD").toString());   		    	
		    //String address = getAddress(taxMap.getMap("ZIP_CD").toString());	    	
		    //taxMap.setMap("ADDRESS", address);	    	
			sndForm.setMap("ADONG_COD"         ,   CbUtil.checkNull(taxMap.getMap("HACD").toString()));    // ����������
			  		    
			sndForm.setMap("TAX_DT"            ,   CbUtil.checkNull(taxMap.getMap("TAX_YY").toString()+taxMap.getMap("TAX_MM").toString()+taxMap.getMap("TAX_DD").toString()));    // (8)      �Ű�����
		    sndForm.setMap("TPR_COD"           ,   (taxMap.getMap("DP_GBN").equals("0"))?"00":"20");   // (2)      �����ڵ�   -  ���ں�ǥ���ڵ�(�����ǹ���(����.����)�ڵ�)
		    sndForm.setMap("REG_NO"            ,   CbUtil.checkNull(taxMap.getMap("REG_NO").toString()));    // (13)     �ֹε�Ϲ�ȣ
		    sndForm.setMap("REG_NM"            ,   CbUtil.checkNull(taxMap.getMap("DPNM").toString()));    // (80)     ����
		    sndForm.setMap("LDONG_COD"         ,   CbUtil.checkNull(bucd) + "00");    // (5)      ������������
		    sndForm.setMap("ZIP_NO"            ,   CbUtil.checkNull(taxMap.getMap("ZIP_CD").toString()));   // (6)      �����ڿ����ȣ
		    sndForm.setMap("ZIP_ADDR"          ,   CbUtil.checkNull(taxMap.getMap("ADDR").toString()));    // (60)     �����ڿ����ּ�
		    sndForm.setMap("OTH_ADDR"          ,   " " + CbUtil.checkNull(taxMap.getMap("ADDR_DT").toString()));    // (100)    �����ڻ��ּ�
		    sndForm.setMap("MO_TEL"            ,   CbUtil.checkNull(taxMap.getMap("PHONE_NO").toString()));    // (16)     �������ڵ�����ȣ
		    sndForm.setMap("TEL"               ,   CbUtil.checkNull(taxMap.getMap("TEL_NO").toString()));    // (16)     ��������ȭ��ȣ
		    sndForm.setMap("BIZ_NO"            ,   CbUtil.checkNull(taxMap.getMap("SAUP_NO").toString()));    // (10)     ����ڹ�ȣ   -  ���λ����,�����ΰ�� �ʼ�
		    sndForm.setMap("CMP_NM"            ,   CbUtil.checkNull(taxMap.getMap("CMP_NM").toString()));    // (100)    ��ȣ   -  ���λ����,�����ΰ�� �ʼ�
		    sndForm.setMap("BIZ_ZIP_NO"        ,   CbUtil.checkNull(taxMap.getMap("ZIP_CD").toString())); 
		    sndForm.setMap("BIZ_ZIP_ADDR"      ,   CbUtil.checkNull(taxMap.getMap("ADDR").toString())); 
		    sndForm.setMap("BIZ_ADDR"          ,   " ");
		        
		    sndForm.setMap("RVSN_YYMM"         ,   CbUtil.checkNull(taxMap.getMap("RVSN_YY").toString())+CbUtil.checkNull(taxMap.getMap("RVSN_MM").toString()));    // (6)    �ͼӳ��
		    sndForm.setMap("SAL_DT"            ,   CbUtil.checkNull(taxMap.getMap("SUP_DT").toString()));    // (8)    �޿���������
		    sndForm.setMap("DUE_DT"            ,   CbUtil.checkNull(taxMap.getMap("DUE_DT").toString()));    // (8)    ��������
		    sndForm.setMap("F_DUE_DT"          ,   CbUtil.checkNull(taxMap.getMap("F_DUE_DT").toString()));    // (8)    ���ʳ���   -  12�� 8�� �߰�
		    sndForm.setMap("EMP_CNT"           ,   CbUtil.checkNull(taxMap.getMap("GSJS").toString()));    // (6)    ��������(�������)
		    sndForm.setMap("TXE_EMP_CNT"       ,   "0");    // (6)    ����������������   -  �ű��߰�
		        
		    //log.info("sal dd");
		      
		    //log.info("sal :: " + Integer.parseInt(taxMap.getMap("GSGY").toString())+Integer.parseInt(taxMap.getMap("BGGY").toString()));
		        
		    sndForm.setMap("SAL"               ,   Integer.parseInt(taxMap.getMap("GSGY").toString())+Integer.parseInt(taxMap.getMap("BGGY").toString()));    // (14)   �ѱ޿���
		        
		    //log.info("sal :: " + Integer.parseInt(taxMap.getMap("GSGY").toString())+Integer.parseInt(taxMap.getMap("BGGY").toString()));
		        
		    sndForm.setMap("TXE_SAL"           ,   CbUtil.checkNull(taxMap.getMap("BGGY").toString()));    // (14)   �������� �ѱ޿���
		    sndForm.setMap("TAX_STD"           ,   CbUtil.checkNull(taxMap.getMap("GSGY").toString()));    // (14)   ����ǥ�ؾ�   -  �ѱ޿��� - �������� �ѱ޿���
		    sndForm.setMap("RDX_C0D"           ,   "");    // (10)   (�߰�)�����ڵ�   -  ���ں�ǥ���ڵ�(�߰������ڵ�)
		    sndForm.setMap("RDX_R"             ,   "");    // (100)  �������   -  size ����
		    sndForm.setMap("RDX_AMT"           ,   "0");    // (14)   ���������   -  ���鼼��
		    sndForm.setMap("R_P_AMT"           ,   CbUtil.checkNull(taxMap.getMap("SGEK").toString()));    // (14)   �Ű��μ���   -  ����ǥ�ؾ� * (0.5/100)
		    sndForm.setMap("TAX_RT"            ,   "0.005");    // (10.4) ����   -  0.005
		    sndForm.setMap("ADTX_YN"           ,   "2");    // (1)    ���꼼(2=��,1=��)   -  �����������ڰ� 0 ���� ũ�� 1, 0 �̸� 2
		    sndForm.setMap("DLQ_CNT"           ,   "0");    // (4)    ���������ϼ�   -  �������� - ���ʳ�������, ������ �ϼ����� ����
		    sndForm.setMap("RADTX"             ,   "0");    // (14)   �Ű�Ҽ��ǰ��꼼��
		    sndForm.setMap("PADTX"             ,   "0");    // (14)   ���κҼ��ǰ��꼼��
		    sndForm.setMap("TOT_ADTX"          ,   "0");    // (14)   ���꼼�װ�   -  �Ű�Ҽ��ǰ��꼼�� + ���κҼ��ǰ��꼼��
		    sndForm.setMap("TOT_AMT"           ,   CbUtil.checkNull(taxMap.getMap("SUM_RCP").toString()));    // (14)   �ѳ��αݾ�   -  �Ű��μ��� + ���꼼�װ�
		    sndForm.setMap("RPT_REG_NO"        ,   CbUtil.checkNull(taxMap.getMap("REG_NO").toString()));    // (13)   �Ű��� �ֹ�/���ι�ȣ
		    sndForm.setMap("RPT_NM"            ,   CbUtil.checkNull(taxMap.getMap("DPNM").toString()));    // (80)   �Ű��� ����/���θ�
		    sndForm.setMap("RPT_TEL"           ,   CbUtil.checkNull(taxMap.getMap("PHONE_NO").toString()));    // (16)   �Ű��� ��ȭ��ȣ   -  �ڵ��� �Ǵ� ��ȭ��ȣ
		    sndForm.setMap("RPT_ID"            ,   "ETAX9");    // (20)   �Ű���ID
		    sndForm.setMap("RPT_ADMIN"         ,   "");    // (60)   �Ű��� ���/�μ���
		    sndForm.setMap("RPT_SYSTEM"        ,   "ETAX");    // (5)    �Ű� �ý��� ����   -  WETAX : WETAX, ETAX: ETAX
		    sndForm.setMap("SGG_COD2"          ,   "");    // (3)    �ñ����ڵ�(����� ��)   -  ����� ����� ���� �õ�/�ñ����ڵ带 �ʼ��Է�
		        
		    sndForm.setMap("SINGO_DIV"         ,   "0");    // (1)    �Ű���   -  ���ʽŰ�:1 , �����Ű�:2
		    sndForm.setMap("B_TAX_NO"          ,   "");    // (31)   ���ʽŰ�� ������ȣ   -  �����Ű���ʼ�
		    sndForm.setMap("B_EPAY_NO"         ,   "");    // (19)   ���ʽŰ�� ���ڳ��ι�ȣ   -  �����Ű���ʼ�
		    sndForm.setMap("CHG_MEMO"          ,   "");    // (2)    �����Ű���   -  �����Ű���ʼ�
																										
		    sndForm.setMap("CHG_REASON"        ,   "");    // (2)    �����Ű����   -  �����Ű���ʼ�
																												                                                    
		    sndForm.setMap("REASON_DT"         ,   "");    // (8)    �����߻���   -  �����Ű���ʼ�
		    sndForm.setMap("ETC_MEMO"          ,   "");    // (200)  ���   -  �����Ű�� ���
		    sndForm.setMap("SUNAP_DT"          ,   "");    // (8)    ��������   -  �����Ű�� ���
		    sndForm.setMap("EVI_DOC1"          ,   "");    // (100)  ������   -  <�ű�>
		    sndForm.setMap("EVI_DOC_URL1"      ,   "");    // (100)  ������ȸURL   -  <�ű�>
		    sndForm.setMap("EVI_DOC2"          ,   "");    // (100)  ������2   -  <�ű�>
		    sndForm.setMap("EVI_DOC_URL2"      ,   "");    // (100)  ������ȸURL2   -  <�ű�>
		    sndForm.setMap("EVI_DOC3"          ,   "");    // (100)  ������2   -  <�ű�>
		    sndForm.setMap("EVI_DOC_URL3"      ,   "");    // (100)  ������ȸURL2   -  <�ű�>
		       
		    /*201301�߰���*/
		    sndForm.setMap("BFE_EMP_CNT"       ,   CbUtil.checkNull(taxMap.getMap("GSJS").toString()));    // (6)  ����������� �� �����������   -  <�ű�>
		        
		    //int mon_sal = ((Integer.parseInt(taxMap.getMap("GSGY").toString()) / Integer.parseInt(taxMap.getMap("GSJS").toString()))/10)*10;
		    //log.debug("mon_sal :: " + mon_sal);
		    sndForm.setMap("MON_SAL"           ,   "0");    // (14)  �� ����޿���   -  <�ű�>
		    sndForm.setMap("RDX_SAL"           ,   "0");    // (14)  ������  -  <�ű�>   
		    //�Ű�Ҽ��ǰ��꼼�� ���� ?    
		    sndForm.setMap("C_TAXADD_R"        ,   "");    // (CHAR2)  �Ű�Ҽ��ǰ��꼼����   -  <2013�ű�>
		    sndForm.setMap("C_TAXADD_GUBUN"    ,   "");    // (CHAR2)  �����Ű����   -  <2013�ű�>
		        
		    sndForm.setMap("RADTX_RT"          ,   "");    // (NUMBER5.4)  �Ű�Ҽ��ǰ������뼼��   -  <2013�ű�>
		    //�����Ű��϶��� ������� �޿��װ� �Ѱ���
		    sndForm.setMap("F_SINGO_STD"       ,   "");    // (NUMBER14)  �����Ű������ǥ   -  <2013�ű�>
		      
		    sndForm.setMap("RDX_EMP_CNT"       ,   "0");    // (NUMBER7.1)  �����ο���(�ż������ΰ�츸 ���� )  -  <2013�ű�>
		    sndForm.setMap("RDX_COD_GBN"       ,   "");    // (CHAR 2)  �������������ڵ�  -  <2013.3.8�ű�>
			       
			//log.debug(sndForm);
   
			try {
			    	    
			    // ���漼 �絵�ҵ�� �Ű� �ۼ��� ���� ȣ��
			    Tax011Service  soapservice = new  Tax011Service();
			    	
			    //log.debug("****sndForm*********"+sndForm+"******************* ");
			    	
			    MapForm recvForm = null;
			    	
			    try{
			    	recvForm = soapservice.sndService(sndForm);
			    }catch(Exception e){
			    	e.printStackTrace();
			    	//RE_CO = "1000";
			    	cmMap.setMap("RESP_CODE" , "1000");     /*�����ڵ�*/
		    		cmMap.setMap("TX_GUBUN"  , "160");
		    		return DFL_011.makeSendBuffer(cmMap, taxMap);
			    }
			    	
			    //log.debug("****recvForm*********"+recvForm+"******************* ");
			    	
			    taxMap.putAll(recvForm);
			    	
			    if(taxMap.getMap("MESSAGE").equals("SVR01")){		
			    		
			    	if(taxMap.getMap("EPAY_NO").equals("") || taxMap.getMap("EPAY_NO").toString() == null){
	    	    		//���αݾ��� �Ҿ��� ��� ���ڳ��ι�ȣ�� ������ �Ҿ�´�.--����ó��
			    		cmMap.setMap("RESP_CODE" , "3010");     /*�����ڵ� */
			    		cmMap.setMap("TX_GUBUN"  , "160");
			    		return DFL_011.makeSendBuffer(cmMap, taxMap);
	    	    	}
			    	
			    	taxMap.setMap("BUCD", bucd);	
			    	taxMap.setMap("YYMD", sndForm.getMap("TAX_DT").toString());
			    	taxMap.setMap("REQ_SNMH", taxMap.getMap("DPNM").toString());
			    	taxMap.setMap("REQ_NM", taxMap.getMap("DPNM").toString());
			    	taxMap.setMap("F_DUE_DT", taxMap.getMap("DUE_DT").toString());	
			    		
			    	log.debug("************ �ֹμ� �������� �Ű� ����ϱ� *********");
			    		
			    	log.debug("**** taxMap *" + taxMap + "******************* ");

			    }else{

			    	log.debug("************ ������ ���� *********");
			    	cmMap.setMap("RESP_CODE" , "9000");     /*�����ڵ�*/
		    		cmMap.setMap("TX_GUBUN"  , "160");
		    		return DFL_011.makeSendBuffer(cmMap, taxMap);
			    }    
			    	
			}catch(Exception e){
			    	
			    log.debug("�ֹμ� �������� �Ű� ���� �ۼ��ſ��� ");
			    cmMap.setMap("RESP_CODE" , "9000");     /*�����ڵ�*/
	    		cmMap.setMap("TX_GUBUN"  , "160");
	    		return DFL_011.makeSendBuffer(cmMap, taxMap);
			   
			}finally{
			    //clear();
			}
				
			/******************************************************
		    * ǥ�� ����� �ڷ� ���� ó��  END
			******************************************************/
			    
			// �޾ƿ� ���� ����
			taxMap.setMap("STATE", state);
			            
			try{
			    sqlService_cyber.queryForInsert("TXDM2610.insertBaroPayData", taxMap);
			}catch(Exception e){
			    e.printStackTrace();
			    log.debug("�������̺� ��� ����");
			    cmMap.setMap("RESP_CODE" , "8000");     /*�����ڵ�*/
	    		cmMap.setMap("TX_GUBUN"  , "160");
	    		return DFL_011.makeSendBuffer(cmMap, taxMap);
			}
			    
		}catch(Exception e){		
			e.printStackTrace();
			cmMap.setMap("RESP_CODE" , "1000");     /*�����ڵ�*/
			cmMap.setMap("TX_GUBUN"  , "160");
			return DFL_011.makeSendBuffer(cmMap, taxMap);
		}
		    
		// ���� �� ����
		cmMap.setMap("RESP_CODE" , "0000");     /*�����ڵ�*/
		cmMap.setMap("TX_GUBUN"  , "160");
		return DFL_011.makeSendBuffer(cmMap, taxMap);
	}
	    
	    
	
	    
	    
	// ���������� �޾Ƽ� ���ڽŰ� �����͸� �������� ����ó��
	private byte[] tax004SunapProcess(MapForm cmMap, MapForm taxMap, MapForm sendMap) {
			
	    MapForm singoData = new MapForm();
	    	
	    try{		  		
	    	int row_cnt = sqlService_cyber.getOneFieldInteger("TXDM2610.getSingoDataCheckCount", taxMap);	
	    	if(row_cnt == 1){		
	    		singoData = sqlService_cyber.queryForMap("TXDM2610.getSingoData", taxMap);
	    	}else{
	    		//���ڽŰ� ���� 
	    		//RE_CO = "5020";   //�����ڷᰡ �����ϴ�.
	    		cmMap.setMap("RESP_CODE" , "5020");     /*�����ڵ�*/
	    		cmMap.setMap("TX_GUBUN"  , "160");
	    		return DFL_004.makeSendBuffer(cmMap, sendMap);
	    	}
	    			
	    	long singoSum = Long.parseLong(singoData.getMap("SUM_RCP").toString());
	    	long sumrcp = Long.parseLong(taxMap.getMap("SUM_RCP").toString());
	    		
	    	if(singoSum != sumrcp){
	    		//RE_CO = "3010";  // �ѱ޾��� Ʋ���ϴ�.
	    		cmMap.setMap("RESP_CODE" , "3010");
	    		cmMap.setMap("TX_GUBUN"  , "160");
	    		return DFL_004.makeSendBuffer(cmMap, sendMap);
	    	}
	    		
	    	if(!singoData.getMap("TAX_NO").equals(taxMap.getMap("TAX_NO"))){
	    		//RE_CO = "3010";  // ���ι�ȣ�� Ʋ���ϴ�.
	    		cmMap.setMap("RESP_CODE" , "5020");
	    		cmMap.setMap("TX_GUBUN"  , "160");
	    		return DFL_004.makeSendBuffer(cmMap, sendMap);
	    	}
	    		
	    	if(!singoData.getMap("EPAY_NO").equals(taxMap.getMap("EPAY_NO"))){
	    		//RE_CO = "3010";  // ���ڳ��ι�ȣ�� Ʋ���ϴ�.
	    		cmMap.setMap("RESP_CODE" , "5020");
	    		cmMap.setMap("TX_GUBUN"  , "160");
	    		return DFL_004.makeSendBuffer(cmMap, sendMap);
	    	}
	    		
	    	if(!singoData.getMap("REG_NO").equals(taxMap.getMap("REG_NO"))){
	    		//RE_CO = "3010";  // �����ڹ�ȣ�� Ʋ���ϴ�.
	    		cmMap.setMap("RESP_CODE" , "5020");
	    		cmMap.setMap("TX_GUBUN"  , "160");
	    		return DFL_004.makeSendBuffer(cmMap, sendMap);
	    	}
	    		
	    	String TAX_DT = taxMap.getMap("TAX_YY").toString()+taxMap.getMap("TAX_MM").toString()+taxMap.getMap("TAX_DD").toString(); 
	    	if(!singoData.getMap("TAX_DT").equals(TAX_DT)){
	    		//RE_CO = "3010";  // ����ϰ� Ʋ���ϴ�.
	    		cmMap.setMap("RESP_CODE" , "5020");
	    		cmMap.setMap("TX_GUBUN"  , "160");
	    		return DFL_004.makeSendBuffer(cmMap, sendMap);
	    	}
	    			
	    	int updateCount = 0;
	    	try{
	    		updateCount = sqlService_cyber.queryForUpdate("TXDM2610.updateSinGoDataForSunap", taxMap);
	    	}catch(Exception e){
	    		e.printStackTrace();
	    		cmMap.setMap("RESP_CODE" , "8000");     /*�����ڵ�*/
	    		cmMap.setMap("TX_GUBUN"  , "160");
	    		return DFL_004.makeSendBuffer(cmMap, sendMap);
	    	}
	    		
	    	if(updateCount == 0){
	    		//RE_CO = "8000";  //��񿡷�
	    		cmMap.setMap("RESP_CODE" , "8000");     /*�����ڵ�*/
	    		cmMap.setMap("TX_GUBUN"  , "160");
	    		return DFL_004.makeSendBuffer(cmMap, sendMap);
	    	}
	    			
	    }catch(Exception e){
	    	e.printStackTrace();
	    	cmMap.setMap("RESP_CODE" , "9000");     /*�����ڵ�*/
			cmMap.setMap("TX_GUBUN"  , "160");
			return DFL_004.makeSendBuffer(cmMap, sendMap);
	    }
	    	
	    // ���� �� ����
		cmMap.setMap("RESP_CODE" , "0000");     /*�����ڵ�*/
		cmMap.setMap("TX_GUBUN"  , "160");
		    		
		//log.debug("cmMap " + cmMap);
		//log.debug("     ");
		//log.debug("  sendMap   " + sendMap);
		    	    
		return DFL_004.makeSendBuffer(cmMap, sendMap);
	}
	        
	// ���������� �޾Ƽ� ���ڽŰ� �����͸� �������� ����ó��
	private byte[] tax005SunapProcess(MapForm cmMap, MapForm taxMap, MapForm sendMap) {
			
	    MapForm singoData = new MapForm();
	    	
	    try{	  		
	    	int row_cnt = sqlService_cyber.getOneFieldInteger("TXDM2610.getSingoDataCheckCount", taxMap);
	    		
	    	if(row_cnt == 1){
		
	    		singoData = sqlService_cyber.queryForMap("TXDM2610.getSingoData", taxMap);
	    		
	    	}else{
	    		//���ڽŰ� ���� 
	    		//RE_CO = "5020";   //�����ڷᰡ �����ϴ�.
	    		cmMap.setMap("RESP_CODE" , "5020");     /*�����ڵ�*/
	    		cmMap.setMap("TX_GUBUN"  , "160");
	    		return DFL_005.makeSendBuffer(cmMap, sendMap);
	    	}
	    		
	    		
	    	long singoSum = Long.parseLong(singoData.getMap("SUM_RCP").toString());
	    	long sumrcp = Long.parseLong(taxMap.getMap("SUM_RCP").toString());
	    		
	    	if(singoSum != sumrcp){
	    		//RE_CO = "3010";  // �ѱ޾��� Ʋ���ϴ�.
	    		cmMap.setMap("RESP_CODE" , "3010");
	    		cmMap.setMap("TX_GUBUN"  , "160");
	    		return DFL_005.makeSendBuffer(cmMap, sendMap);
	    	}
	    		
	    	if(!singoData.getMap("TAX_NO").equals(taxMap.getMap("TAX_NO"))){
	    		//RE_CO = "3010";  // ���ι�ȣ�� Ʋ���ϴ�.
	    		cmMap.setMap("RESP_CODE" , "5020");
	    		cmMap.setMap("TX_GUBUN"  , "160");
	    		return DFL_005.makeSendBuffer(cmMap, sendMap);
	    	}
	    		
	    	if(!singoData.getMap("EPAY_NO").equals(taxMap.getMap("EPAY_NO"))){
	    		//RE_CO = "3010";  // ���ڳ��ι�ȣ�� Ʋ���ϴ�.
	    		cmMap.setMap("RESP_CODE" , "5020");
	    		cmMap.setMap("TX_GUBUN"  , "160");
	    		return DFL_005.makeSendBuffer(cmMap, sendMap);
	    	}
	    		
	    	if(!singoData.getMap("REG_NO").equals(taxMap.getMap("REG_NO"))){
	    		//RE_CO = "3010";  // �ֹι�ȣ�� Ʋ���ϴ�.
	    		cmMap.setMap("RESP_CODE" , "5020");
	    		cmMap.setMap("TX_GUBUN"  , "160");
	    		return DFL_005.makeSendBuffer(cmMap, sendMap);
	    	}
	    		
	    	String TAX_DT = taxMap.getMap("TAX_YY").toString()+taxMap.getMap("TAX_MM").toString()+taxMap.getMap("TAX_DD").toString(); 
	    	if(!singoData.getMap("TAX_DT").equals(TAX_DT)){
	    		//RE_CO = "3010";  // ����ϰ� Ʋ���ϴ�.
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
	    		//RE_CO = "8000";  //��񿡷�
	    		cmMap.setMap("RESP_CODE" , "8000");     /*�����ڵ�*/
	    		cmMap.setMap("TX_GUBUN"  , "160");
	    		return DFL_005.makeSendBuffer(cmMap, sendMap);
	    	}
	    			
	    }catch(Exception e){
	    	e.printStackTrace();
	    	cmMap.setMap("RESP_CODE" , "9000");     /*�����ڵ�*/
    		cmMap.setMap("TX_GUBUN"  , "160");
    		return DFL_005.makeSendBuffer(cmMap, sendMap);
	    }
	    	
	    // ���� �� ����
		cmMap.setMap("RESP_CODE" , "0000");     /*�����ڵ�*/
		cmMap.setMap("TX_GUBUN"  , "160");
		return DFL_005.makeSendBuffer(cmMap, sendMap);
	}
	
	
	// ���������� �޾Ƽ� ���ڽŰ� �����͸� �������� ����ó��
	private byte[] tax008SunapProcess(MapForm cmMap, MapForm taxMap, MapForm sendMap) {
				
		MapForm singoData = new MapForm();
		    	
		try{	  		
		    	
			int row_cnt = sqlService_cyber.getOneFieldInteger("TXDM2610.getSingoDataCheckCount", taxMap);
		    		
		    if(row_cnt == 1){
		    		
		    	singoData = sqlService_cyber.queryForMap("TXDM2610.getSingoData", taxMap);
		    		
		    }else{
		    	//���ڽŰ� ���� 
		    	//RE_CO = "5020";   //�����ڷᰡ �����ϴ�.
		    	cmMap.setMap("RESP_CODE" , "5020");     /*�����ڵ�*/
		    	cmMap.setMap("TX_GUBUN"  , "260");
		    	return DFL_009.makeSendBuffer(cmMap, sendMap);
		    }
		    			
		    long singoSum = Long.parseLong(singoData.getMap("NAPBU_TAX").toString());
		    long sumrcp = Long.parseLong(taxMap.getMap("NAPBU_TAX").toString());
		    		
		    if(singoSum != sumrcp){
		    	//RE_CO = "3010";  // �ѱ޾��� Ʋ���ϴ�.
		    	cmMap.setMap("RESP_CODE" , "3010");
		    	cmMap.setMap("TX_GUBUN"  , "260");
		    	return DFL_008.makeSendBuffer(cmMap, sendMap);
		    }
		    		
		    if(!singoData.getMap("TAX_NO").equals(taxMap.getMap("TAX_NO"))){
		    	//RE_CO = "3010";  // ���ι�ȣ�� Ʋ���ϴ�.
		    	cmMap.setMap("RESP_CODE" , "5020");
		    	cmMap.setMap("TX_GUBUN"  , "260");
		    	return DFL_008.makeSendBuffer(cmMap, sendMap);
		    }
		    		
		    if(!singoData.getMap("EPAY_NO").equals(taxMap.getMap("EPAY_NO"))){
		    	//RE_CO = "3010";  // ���ڳ��ι�ȣ�� Ʋ���ϴ�.
		    	cmMap.setMap("RESP_CODE" , "5020");
		    	cmMap.setMap("TX_GUBUN"  , "260");
		    	return DFL_008.makeSendBuffer(cmMap, sendMap);
		    }
		    		
		    if(!singoData.getMap("REG_NO").equals(taxMap.getMap("REG_NO"))){
		    	//RE_CO = "3010";  // �ֹι�ȣ�� Ʋ���ϴ�.
		    	cmMap.setMap("RESP_CODE" , "5020");
		    	cmMap.setMap("TX_GUBUN"  , "260");
		    	return DFL_008.makeSendBuffer(cmMap, sendMap);
		    }
		    		
		    //String TAX_DT = taxMap.getMap("TAX_YY").toString()+taxMap.getMap("TAX_MM").toString()+taxMap.getMap("TAX_DD").toString(); 
		    //if(!singoData.getMap("TAX_DT").equals(TAX_DT)){
		    	//RE_CO = "3010";  // ����ϰ� Ʋ���ϴ�.
		    //	cmMap.setMap("RESP_CODE" , "5020");
		    //	cmMap.setMap("TX_GUBUN"  , "260");
		    //	return DFL_008.makeSendBuffer(cmMap, sendMap);
		    //}
		    		    			
		    try{
		    	sqlService_cyber.queryForUpdate("TXDM2610.updateSinGoDataForSunap", taxMap);
		    }catch(Exception e){
		    	e.printStackTrace();
		    	//RE_CO = "8000";
		    	// ���� �� ����
		    	cmMap.setMap("RESP_CODE" , "8000");     /*�����ڵ�*/
		    	cmMap.setMap("TX_GUBUN"  , "260");
		    	return DFL_008.makeSendBuffer(cmMap, sendMap);
		    }
		    		
		    			
	
		}catch(Exception e){
		    e.printStackTrace();
		    // ���� �� ����
			cmMap.setMap("RESP_CODE" , "9000");     /*�����ڵ�*/
			cmMap.setMap("TX_GUBUN"  , "260");
			return DFL_008.makeSendBuffer(cmMap, sendMap);
	
		}
		    	
		// ���� �� ����
		cmMap.setMap("RESP_CODE" , "0000");     /*�����ڵ�*/
		cmMap.setMap("TX_GUBUN"  , "260");
		return DFL_008.makeSendBuffer(cmMap, sendMap);
	}
	    
	    
	// ���������� �޾Ƽ� ���ڽŰ� �����͸� �������� ����ó��
	private byte[] tax009SunapProcess(MapForm cmMap, MapForm taxMap, MapForm sendMap) {
			
	    MapForm singoData = new MapForm();
	    	
	    try{	  		
	    	int row_cnt = sqlService_cyber.getOneFieldInteger("TXDM2610.getSingoDataCheckCount", taxMap);
	    		
	    	if(row_cnt == 1){
	    		
	    		singoData = sqlService_cyber.queryForMap("TXDM2610.getSingoData", taxMap);
	    		
	    	}else{
	    		//���ڽŰ� ���� 
	    		//RE_CO = "5020";   //�����ڷᰡ �����ϴ�.
	    		cmMap.setMap("RESP_CODE" , "5020");     /*�����ڵ�*/
	    		cmMap.setMap("TX_GUBUN"  , "160");
	    		return DFL_009.makeSendBuffer(cmMap, sendMap);
	    	}
	    			
	    	long singoSum = Long.parseLong(singoData.getMap("SUM_RCP").toString());
	    	long sumrcp = Long.parseLong(taxMap.getMap("SUM_RCP").toString());
	    		
	    	if(singoSum != sumrcp){
	    		//RE_CO = "3010";  // �ѱ޾��� Ʋ���ϴ�.
	    		cmMap.setMap("RESP_CODE" , "3010");
	    		cmMap.setMap("TX_GUBUN"  , "160");
	    		return DFL_009.makeSendBuffer(cmMap, sendMap);
	    	}
	    		
	    	if(!singoData.getMap("TAX_NO").equals(taxMap.getMap("TAX_NO"))){
	    		//RE_CO = "3010";  // ���ι�ȣ�� Ʋ���ϴ�.
	    		cmMap.setMap("RESP_CODE" , "5020");
	    		cmMap.setMap("TX_GUBUN"  , "160");
	    		return DFL_009.makeSendBuffer(cmMap, sendMap);
	    	}
	    		
	    	if(!singoData.getMap("EPAY_NO").equals(taxMap.getMap("EPAY_NO"))){
	    		//RE_CO = "3010";  // ���ڳ��ι�ȣ�� Ʋ���ϴ�.
	    		cmMap.setMap("RESP_CODE" , "5020");
	    		cmMap.setMap("TX_GUBUN"  , "160");
	    		return DFL_009.makeSendBuffer(cmMap, sendMap);
	    	}
	    		
	    	if(!singoData.getMap("REG_NO").equals(taxMap.getMap("REG_NO"))){
	    		//RE_CO = "3010";  // �ֹι�ȣ�� Ʋ���ϴ�.
	    		cmMap.setMap("RESP_CODE" , "5020");
	    		cmMap.setMap("TX_GUBUN"  , "160");
	    		return DFL_009.makeSendBuffer(cmMap, sendMap);
	    	}
	    		
	    	String TAX_DT = taxMap.getMap("TAX_YY").toString()+taxMap.getMap("TAX_MM").toString()+taxMap.getMap("TAX_DD").toString(); 
	    	if(!singoData.getMap("TAX_DT").equals(TAX_DT)){
	    		//RE_CO = "3010";  // ����ϰ� Ʋ���ϴ�.
	    		cmMap.setMap("RESP_CODE" , "5020");
	    		cmMap.setMap("TX_GUBUN"  , "160");
	    		return DFL_009.makeSendBuffer(cmMap, sendMap);
	    	}
	    		    			
	    	try{
	    		sqlService_cyber.queryForUpdate("TXDM2610.updateSinGoDataForSunap", taxMap);
	    	}catch(Exception e){
	    		e.printStackTrace();
	    		//RE_CO = "8000";
	    		// ���� �� ����
	    		cmMap.setMap("RESP_CODE" , "8000");     /*�����ڵ�*/
	    		cmMap.setMap("TX_GUBUN"  , "160");
	    		return DFL_009.makeSendBuffer(cmMap, sendMap);
	    	}
	    		
	    			
	    }catch(Exception e){
	    	e.printStackTrace();
	    	// ���� �� ����
			cmMap.setMap("RESP_CODE" , "9000");     /*�����ڵ�*/
			cmMap.setMap("TX_GUBUN"  , "160");
			return DFL_009.makeSendBuffer(cmMap, sendMap);
	    }
	    	
	    // ���� �� ����
		cmMap.setMap("RESP_CODE" , "0000");     /*�����ڵ�*/
		cmMap.setMap("TX_GUBUN"  , "160");
		return DFL_009.makeSendBuffer(cmMap, sendMap);
	}
	    
	    
	// ���������� �޾Ƽ� ���ڽŰ� �����͸� �������� ����ó��
	private byte[] tax011SunapProcess(MapForm cmMap, MapForm taxMap, MapForm sendMap) {
			
	    MapForm singoData = new MapForm();
	    	
	    try{	  		
	    	int row_cnt = sqlService_cyber.getOneFieldInteger("TXDM2610.getSingoDataCheckCount", taxMap);
	    		
	    	if(row_cnt == 1){
	    			
	    		singoData = sqlService_cyber.queryForMap("TXDM2610.getSingoData", taxMap);
	    		
	    	}else{
	    		//���ڽŰ� ���� 
	    		//RE_CO = "5020";   //�����ڷᰡ �����ϴ�.
	    		cmMap.setMap("RESP_CODE" , "5020");     /*�����ڵ�*/
	    		cmMap.setMap("TX_GUBUN"  , "160");
	    		return DFL_011.makeSendBuffer(cmMap, sendMap);
	    	}
	    		
	    	long singoSum = Long.parseLong(singoData.getMap("SUM_RCP").toString());
	    	long sumrcp = Long.parseLong(taxMap.getMap("SUM_RCP").toString());
	    		
	    	if(singoSum != sumrcp){
	    		//RE_CO = "3010";  // �ѱ޾��� Ʋ���ϴ�.
	    		cmMap.setMap("RESP_CODE" , "3010");
	    		cmMap.setMap("TX_GUBUN"  , "160");
	    		return DFL_011.makeSendBuffer(cmMap, sendMap);
	    	}
	    		
	    	if(!singoData.getMap("TAX_NO").equals(taxMap.getMap("TAX_NO"))){
	    		//RE_CO = "3010";  // ���ι�ȣ Ʋ���ϴ�.
	    		cmMap.setMap("RESP_CODE" , "5020");
	    		cmMap.setMap("TX_GUBUN"  , "160");
	    		return DFL_011.makeSendBuffer(cmMap, sendMap);
	    	}
	    		
	    	if(!singoData.getMap("EPAY_NO").equals(taxMap.getMap("EPAY_NO"))){
	    		//RE_CO = "3010";  // ���ڳ��ι�ȣ�� Ʋ���ϴ�.
	    		cmMap.setMap("RESP_CODE" , "5020");
	    		cmMap.setMap("TX_GUBUN"  , "160");
	    		return DFL_011.makeSendBuffer(cmMap, sendMap);
	    	}
	    		
	    	if(!singoData.getMap("REG_NO").equals(taxMap.getMap("REG_NO"))){
	    		//RE_CO = "3010";  // �ֹι�ȣ�� Ʋ���ϴ�.
	    		cmMap.setMap("RESP_CODE" , "5020");
	    		cmMap.setMap("TX_GUBUN"  , "160");
	    		return DFL_011.makeSendBuffer(cmMap, sendMap);
	    	}
	    		
	    	String TAX_DT = taxMap.getMap("TAX_YY").toString()+taxMap.getMap("TAX_MM").toString()+taxMap.getMap("TAX_DD").toString(); 
	    	if(!singoData.getMap("TAX_DT").equals(TAX_DT)){
	    		//RE_CO = "3010";  // ������� Ʋ���ϴ�.
	    		cmMap.setMap("RESP_CODE" , "5020");
	    		cmMap.setMap("TX_GUBUN"  , "160");
	    		return DFL_011.makeSendBuffer(cmMap, sendMap);
	    	}
	      		
	    	try{
	    		sqlService_cyber.queryForUpdate("TXDM2610.updateSinGoDataForSunap", taxMap);
	    	}catch(Exception e){
	    		e.printStackTrace();	
	    		//RE_CO = "8000";
	    		// ���� �� ����
	    		cmMap.setMap("RESP_CODE" , "8000");     /*�����ڵ�*/
	    		cmMap.setMap("TX_GUBUN"  , "160");
	    		return DFL_011.makeSendBuffer(cmMap, sendMap);
	    	}
	    		
	    		    	
	    }catch(Exception e){
	    	e.printStackTrace();
	    	// ���� �� ����
			cmMap.setMap("RESP_CODE" , "9000");     /*�����ڵ�*/
			cmMap.setMap("TX_GUBUN"  , "160");
			return DFL_011.makeSendBuffer(cmMap, sendMap);
	    }
	    	
	    // ���� �� ����
		cmMap.setMap("RESP_CODE" , "0000");     /*�����ڵ�*/
		cmMap.setMap("TX_GUBUN"  , "160");
		return DFL_011.makeSendBuffer(cmMap, sendMap);
	}

	// �������ڵ�
	private String getLdongCode(String sgg_cod, String hacd){		    	
		String ldong_cd = null;	
		MapForm mf = new MapForm();
		mf.setMap("SIDO_CD", "26");
		mf.setMap("SGG_CD", sgg_cod);
		mf.setMap("HACD", hacd);   	
		try{    		
		    ldong_cd = sqlService_cyber.getOneFieldString("TXDM2610.getLdongCode", mf);
		}catch(Exception e){
		    log.debug("�������ڵ� ����");
		    e.printStackTrace();
		}	    	
		return ldong_cd;
	}
	
	//Ư��¡�� �������ڵ� ���ϱ�
	private String getHacdCode(String sgg_cod, String bucd) {
		String hacd = null;
		MapForm mf = new MapForm();
		mf.setMap("SIDO_CD", "26");
		mf.setMap("SGG_CD", sgg_cod);
		mf.setMap("BUCD", bucd);
		try{
			hacd = sqlService_cyber.getOneFieldString("TXDM2610.getHacdCode", mf);
		}catch(Exception e){
			log.debug("�������ڵ� ����");
			e.printStackTrace();
		}
		return hacd;
	}
		    
	//�ּһ� �˻� - ������	    
	private String getAddress(String post) {
				
		String address = null;
		    	
		MapForm addr = new MapForm();
		addr.setMap("POST", post);
		    	
		try{    		
		    address = sqlService_cyber.getOneFieldString("TXDM2610.getAddress", addr);
		    //address = CbUtil.strEncod(address, "MS949", "KSC5601");
		}catch(Exception e){
		    log.debug("�ּҰ˻� ����");
		}
		    	
		return address;
	}
    
	


}
