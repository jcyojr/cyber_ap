/**
 * ���������� �������� ����
 * ���� ���� ���ȭ ���Ѷ�..
 */
package com.uc.bs.cyber.batch.txbt2550;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.uc.bs.cyber.CbUtil;
import com.uc.bs.cyber.daemon.txdm2550.Txdm2550FieldList;
import com.uc.core.MapForm;
import com.uc.core.misc.Utils;
import com.uc.core.misc.XMLFileReader;
import com.uc.core.misc.XmlUtils;
import com.uc.core.spring.service.IbatisBaseService;
import com.uc.egtob.net.ClientMessageService;
import java.io.*;
import java.math.BigDecimal;
import java.util.ArrayList;

/**
 * @author Administrator
 *
 */
public class Txbt2550 extends ClientMessageService implements Runnable {

	protected Log log = LogFactory.getLog(this.getClass());
	
	private IbatisBaseService  sqlService_cyber = null;
	
	private ApplicationContext  appContext = null;
	
	private byte[] recvBuffer;
	private String outFilePath  = "";
	private String backFilePath = "";
	private String sndFilePath  = "";
	
	/**
	 * 
	 */
	public Txbt2550() {
		// TODO Auto-generated constructor stub
		
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		System.out.println("=================================================");
		System.out.println("���̹����漼û ���ೳ������ ���� Client Started");
		System.out.println("=================================================");	
		
		Txbt2550 client;
		
		ApplicationContext context  = null;
		
		try {
			// Log
			client = new Txbt2550();

			try {
				
				CbUtil.setupLog4jConfig(client, "log4j.txbt2550.xml");
				
				String[] xmls = XMLFileReader.getAllFilePathArray(CbUtil.getResourcePath(client, "/"), "SERVICE.XML");

				String strSrc = CbUtil.getResourcePath(client, "/") + "config/sqlmaps.xml";

				String strDest = CbUtil.getResourcePath(client, "/") + "config/sqlmapConfig.xml";

				XmlUtils.setXmlAttributes(client, strSrc, strDest, "sqlMapConfig", "sqlMap", "url", xmls);

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
		
			context = new ClassPathXmlApplicationContext("config/Seoi-Spring-db.xml");
			
			client.setContext(context);
			
			Thread thr = new Thread(client, "thr_2550");
			
			thr.start();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	public Object getService(String beanName) {
		// TODO Auto-generated method stub
		return appContext.getBean(beanName);
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
		if(log.isInfoEnabled()) {
			log.info("=======================================================");
			log.info("=============���������ೳ�� �������� ��================");
			log.info("=======================================================");
		}
		
		sqlService_cyber = (IbatisBaseService) this.appContext.getBean("baseService_cyber");
		
		try {
			
			Txbt2550 client = new Txbt2550();
			
			outFilePath  = "/app/data/cyber_ap/recv/";
			backFilePath = "/app/data/cyber_ap/back/";
			sndFilePath  = "/app/data/cyber_ap/send/";
			
			/*1. ���ŵ� ������ ���������� ���ϸ��� ������ ã�ƿ´�...*/
			
			/*2. ���ŵ� ���������� ������ �о ����� ���Ͽ� ������ ���� ���¸� �����Ѵ�...*/
			
			/*3. 
			 *   ���������� �� ������ ���������� ���� �۽��Ѵ�...
			 *   �� ������ �۽��ϴ� ��� 1056 bytes ������ ��űԾ��� �����Ѵ�...
			 * */
			
			/*�������� ��������...*/
			MapForm mf = new MapForm();

			mf.setMap("d_cnt" , 1);
			mf.setMap("CUR_DT", CbUtil.getCurrentDate());
			
			int gymd_yn = (Integer)sqlService_cyber.queryForBean("TXBT2550.GET_GYMD_YN", mf);
			
			/*�������� ��츸...*/
			if (gymd_yn == 1) {
				
				/*�Ϳ����ϱ���*/
				String BefrDate = (String)sqlService_cyber.queryForBean("TXBT2550.GET_GYMD_A", mf);
				String JobDate  = (String)sqlService_cyber.queryForBean("TXBT2550.GET_GYMD_B", mf);
				
				mf.setMap("ACCTDT"    , BefrDate);
				mf.setMap("TRANSDATE" , JobDate);
				
				log.info("============================================");
				log.info("CUR_DATE = [" + CbUtil.getCurrentDate() + "] �������� = [" + JobDate + "] �Ϳ����� = [" + BefrDate + "]");
				log.info("============================================");
				
				regist_state_batch("S");
				
				/*���ϼ��� ���ೳ�� ���� ����*/
				res_sanghasudo_file_transfer(client, mf);
				
				/*���ܼ��� ���ೳ�� ���� ����*/
				res_nontax_receipt_file_transfer(client, mf);
				
				/*ȯ�氳�� ���ೳ�� ���� ����:: ��޾��Ѵ�...*/
				//res_env_improvement_file_transfer(client, mf);
				
				regist_state_batch("E");
				
			} else {
				
				log.info("============================================");
				log.info("CUR_DATE = [" + CbUtil.getCurrentDate() + "] �������� �ƴϹǷ� �������� ����...");
				log.info("============================================");
			}
			
			
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			 e.printStackTrace();
		}
		
		if(log.isInfoEnabled()) {
			log.info("=======================================================");
			log.info("=============���������ೳ�� �������� ��================");
			log.info("=======================================================");
		}
	}
	
	public void setContext(ApplicationContext context) {
		// TODO Auto-generated method stub
		this.appContext = context;
	}	
	
	/**
	 * ���ϼ��� ���ೳ������ ����
	 * @param client
	 * @param mf
	 */
	private void res_sanghasudo_file_transfer(Txbt2550 client, MapForm mf){
		
		
		ArrayList<MapForm> alJunmunInfo = null;
		
		try {
			
			CbUtil cUtil = CbUtil.getInstance();
			
			/*���ϼ���*/
			mf.setMap("TAX_GB" , "S");
			
			/*���ϼ��� ���ೳ�� �������ų����� �����´�.*/			
			alJunmunInfo = sqlService_cyber.queryForList("TXBT2550.SELECT_JUNMUN_LIST", mf);
			
			if (alJunmunInfo.size()  >  0)   {
				
				log.info("==========================================");
				log.info("===============  ���ϼ���  ===============");
				log.info("==========================================");
				
				Txdm2550FieldList KFL_2550 = new Txdm2550FieldList();
				
				StringBuffer sbHead = new StringBuffer();
				StringBuffer sbTail = new StringBuffer();
				StringBuffer sbData = new StringBuffer();
				
				MapForm mpHead = new MapForm();
				MapForm mpTail = new MapForm();
				MapForm mpData = new MapForm();
				
				int  inc = 0;
				long amt = 0;
				
				for ( int rec_cnt = 0;  rec_cnt < alJunmunInfo.size();  rec_cnt++)   {
					
					MapForm mfJunmunInfo =  alJunmunInfo.get(rec_cnt);
					
					/*Ȥ�ó�...because of testing */
					if (mfJunmunInfo == null  ||  mfJunmunInfo.isEmpty() )   {
						continue;
					}
					
					/*DATA ��*/
					KFL_2550.GR6676_Data_FieldList();  /*������� Data ����*/
												
					//����ó��(����) �Ǿ��ٸ�...
					if (sqlService_cyber.getOneFieldInteger("TXBT2550.SELECT_SHSD_KFPAY_CNT", mfJunmunInfo) > 0 ) {
						
						if(mfJunmunInfo.getMap("ACCTDT").equals(mf.getMap("ACCTDT"))) {
							
							inc++;
							
							MapForm mp_S = sqlService_cyber.queryForMap("TXBT2550.SELECT_SHSD_KFPAY_LIST", mfJunmunInfo);
							
							//����ó�� ��������
						    String BNAPDATE             = "";       // ���⳻ ������
						    long BNAPAMT                = 0 ;       // ���⳻ �ݾ�
						    long ANAPAMT                = 0 ;       // ������ �ݾ�
						    String GUM2                 = "";       // ������ȣ2
						    long BSAMT                  = 0 ;       // ����� ���⳻ �ݾ�
						    long BHAMT                  = 0 ;       // �ϼ��� ���⳻ �ݾ�
						    long BGAMT                  = 0 ;       // ���ϼ��� ���⳻ �ݾ�
						    long BMAMT                  = 0 ;       // ���̿�δ�� ���⳻ �ݾ�
						    long ASAMT                  = 0 ;       // ����� ������ �ݾ�      
						    long AHAMT                  = 0 ;       // �ϼ��� ������ �ݾ�      
						    long AGAMT                  = 0 ;       // ���ϼ��� ������ �ݾ�    
						    long AMAMT                  = 0 ;       // ���̿�δ�� ������ �ݾ�
						    String ANAPDATE             = "";       // ������ ������
						    String GUM3                 = "";       // ������ȣ3
						    String CNAPTERM             = "";       // ü���Ⱓ
						    String ADDR                 = "";       // �ּ�
						    String USETERM              = "";       // ���Ⱓ
						    String NAPGUBUN             = "";       // ���⳻����
						    String NapJUMMun            = "";
						    String napkiGubun           = "";
						    
						    String dang = mp_S.getMap("GUBUN").toString().substring(1);										
						
			                //�������� SET
			                BNAPDATE = (String) mp_S.getMap("DUE_DT");         //���⳻ ������
			                ANAPDATE = (String) mp_S.getMap("DUE_F_DT");       //������ ������
							
			                ADDR     = (String) mp_S.getMap("ADDRESS");        //�ּ�  
			                
			                //���⳻(B), ������(A) üũ
			                napkiGubun = cUtil.getNapGubun(BNAPDATE, ANAPDATE);

			                NAPGUBUN = napkiGubun;
			                
			                if(napkiGubun.equals("B")) {
			                	NapJUMMun = "1";
			                }else{
			                	NapJUMMun = "2";
			                }
			                
							if(dang.equals("1") || dang.equals("3")) {
								
								if(log.isDebugEnabled()){
									log.debug("����/���� ���ΰ��");
								}

								
				                //�����Ѿ� SET
				                BNAPAMT  = ((BigDecimal)mp_S.getMap("SUM_B_AMT")).longValue();         //���⳻ �ݾ�
				                ANAPAMT  = ((BigDecimal)mp_S.getMap("SUM_F_AMT")).longValue();         //������ �ݾ�
								
				                GUM2 = "";
				                
				                //���μ��αݾ� SET
				                BSAMT    = ((BigDecimal)mp_S.getMap("FEE_AMT1")).longValue();         //���⳻ ������ݾ�
				                BHAMT    = ((BigDecimal)mp_S.getMap("FEE_AMT2")).longValue();         //���⳻ �ϼ����ݾ�
				                BGAMT    = ((BigDecimal)mp_S.getMap("FEE_AMT4")).longValue();         //���⳻ ���ϼ��ݾ�
				                BMAMT    = ((BigDecimal)mp_S.getMap("FEE_AMT3")).longValue();         //���⳻ ���̿�ݾ�
				                ASAMT    = ((BigDecimal)mp_S.getMap("FEE_AMT1_H")).longValue();         //������ ������ݾ�
				                AHAMT    = ((BigDecimal)mp_S.getMap("FEE_AMT2_H")).longValue();         //������ �ϼ����ݾ�
				                AGAMT    = ((BigDecimal)mp_S.getMap("FEE_AMT4_H")).longValue();         //������ ���ϼ��ݾ�
				                AMAMT    = ((BigDecimal)mp_S.getMap("FEE_AMT3_H")).longValue();         //������ ���̿�ݾ�

				                GUM3 = "";

				                CNAPTERM = "";  //ü���Ⱓ

				                USETERM  = (String) mp_S.getMap("USE_STT_DATE") + (String) mp_S.getMap("USE_END_DATE");          //���Ⱓ

								
							} else if (dang.equals("2")){
								
								if(log.isDebugEnabled()){
									log.debug("ü������ ���");
								}
								
								
								//�������� SET
				                //���⳻�ΰ�� ���⳻ ���� SET, �������� ��� ������ ���� SET
				                //���⳻(B), ������(A) üũ
								if(NAPGUBUN.equals("B")) {
				                    BNAPDATE = (String) mp_S.getMap("DUE_DT");         //������ ������
				                    ANAPDATE = BNAPDATE;
				                } else  {
				                    BNAPDATE = (String) mp_S.getMap("DUE_F_DT");       //������ ������
				                    ANAPDATE = BNAPDATE;
				                }
								
								//ü�����ΰ�� ������ ���⳻�������� ���α��� ����
				                NAPGUBUN = "B";

				                NapJUMMun = "3";  /*ü���� 3*/
				                
				                //�����Ѿ� SET
				                BNAPAMT  = ((BigDecimal)mp_S.getMap("SUM_B_AMT")).longValue();        //���⳻ �ݾ�
				                ANAPAMT  = 0;

				                GUM2 = "";

				                //���μ��αݾ� SET
				                BSAMT    = ((BigDecimal)mp_S.getMap("FEE_AMT1")).longValue();         //���⳻ ������ݾ�
				                BHAMT    = ((BigDecimal)mp_S.getMap("FEE_AMT2")).longValue();         //���⳻ �ϼ����ݾ�
				                BGAMT    = ((BigDecimal)mp_S.getMap("FEE_AMT4")).longValue();         //���⳻ ���ϼ��ݾ�
				                BMAMT    = ((BigDecimal)mp_S.getMap("FEE_AMT3")).longValue();         //���⳻ ���̿�ݾ�
				                ASAMT    = 0;
				                AHAMT    = 0;
				                AGAMT    = 0;
				                AMAMT    = 0;

				                GUM3 = "";

				                CNAPTERM = (String) mp_S.getMap("USE_STT_DATE") + (String) mp_S.getMap("USE_END_DATE");          //ü���Ⱓ
				                USETERM = CNAPTERM;          //���Ⱓ

							}
							
							mpData.setMap("BUSINESSGUBUN"     , "GR6676");                                            /*��������            */
							mpData.setMap("DATAGUBUN"         , "22");                                                /*�����ͱ���          */
							mpData.setMap("SEQNO"             , CbUtil.lPadString(String.valueOf(inc), 7, '0'));      /*�Ϸù�ȣ            */
							mpData.setMap("PUBGCODE"          , mfJunmunInfo.getMap("PUBGCODE"));                     /*�������з��ڵ�    */
							mpData.setMap("JIRONO"            , mfJunmunInfo.getMap("GIRONO"));                       /*������Ȯ�ι� ���� */
							mpData.setMap("CUSTNO"            , mp_S.getMap("CUST_NO"));                              /*���밡��ȣ          */
							mpData.setMap("TAX_YM"            , mp_S.getMap("TAX_YM"));                               /*�ΰ����            */
							mpData.setMap("DANGGUBUN"         , mp_S.getMap("GUBUN").toString().substring(1));        /*��� ����                                         */
							mpData.setMap("NPNO"              , mp_S.getMap("PRT_NPNO"));                             /*���� ��ȣ                                         */
							mpData.setMap("REGNM"             , mp_S.getMap("REG_NM"));                               /*����                */
					        mpData.setMap("BNAPDATE"          , BNAPDATE);                                            /*���⳻ ������              | ���� ������          */
							mpData.setMap("BNAPAMT"           , BNAPAMT);                                             /*���⳻ �ݾ�                | ü����               */
							mpData.setMap("ANAPAMT"           , ANAPAMT);                                             /*������ �ݾ�                                       */
							mpData.setMap("GUM2"              , GUM2);                                                /*������ȣ 2                                        */
							mpData.setMap("BSAMT"             , BSAMT);                                               /*��������⳻�ݾ�           |  ����� ü����       */
							mpData.setMap("BHAMT"             , BHAMT);                                               /*�ϼ������⳻�ݾ�           |  �ϼ��� ü����       */
							mpData.setMap("BGAMT"             , BGAMT);                                               /*���ϼ����⳻�ݾ�           |  ���ϼ� ü����       */
							mpData.setMap("BMAMT"             , BMAMT);                                               /*���̿�δ�ݳ��⳻�ݾ�     |  ���̿�δ��ü����  */
							mpData.setMap("ASAMT"             , ASAMT);                                               /*����������ıݾ�                                  */
							mpData.setMap("AHAMT"             , AHAMT);                                               /*�ϼ��������ıݾ�                                  */
							mpData.setMap("AGAMT"             , AGAMT);                                               /*���ϼ������ıݾ�                                  */
							mpData.setMap("AMAMT"             , AMAMT);                                               /*���̿�δ�ݳ����ıݾ�                            */
							mpData.setMap("ANAPDATE"          , ANAPDATE);                                            /*������ ������                                     */
							mpData.setMap("GUM3"              , GUM3);                                                /*������ȣ 3                                        */
							mpData.setMap("CNAPTERM"          , CNAPTERM);                                            /*ü�� �Ⱓ                                         */
							mpData.setMap("ADDR"              , ADDR);                                                /*�ּ�                                              */
							mpData.setMap("USETERM"           , USETERM);                                             /*��� �Ⱓ                                         */
							mpData.setMap("AUTOREG"           , "N");                                                 /*�ڵ���ü ��� ����                                */
							mpData.setMap("SNAP_BANK_CODE"    , mp_S.getMap("BRC_NO"));                               /*�������� ���� �ڵ�                                */
							mpData.setMap("SNAP_SYMD"         , mp_S.getMap("SUNAPDT"));                              /*���� �Ͻ�                                         */
							mpData.setMap("NAPGUBUN"          , NAPGUBUN);                                            /*���� ���� ����(���⳻B��)                         */
							mpData.setMap("ETC1"              , " ");                                                 /*���� ���� FIELD                                   */
							mpData.setMap("CUST_ADMIN_NUM"    , " ");                                                 /*��������ȣ                                      */
							mpData.setMap("OCR"               , mp_S.getMap("OCR_BD"));                               /*OCR BAND                                          */
							mpData.setMap("PRCGB"             , "D");                                                 /*ó������            */
							mpData.setMap("FILLER"            , " ");                                                 /*FILLER              */
							
							/*������ ���̳ʸ� ������*/
							sbData.append(new String(KFL_2550.getBuff(mpData)));
							
						}

						
					} else {
						
						//�ݾ��� Ʋ�� ���...
						MapForm mp_S = sqlService_cyber.queryForMap("TXBT2550.SELECT_SHSD_LEVY_LIST", mfJunmunInfo);
						
						if(mp_S.size() > 0) { // �����Ͱ� �����ϸ�...
							
							if(mfJunmunInfo.getMap("ACCTDT").equals(mf.getMap("ACCTDT"))) {
								
								inc++;
								
								//����ó�� ��������
							    String BNAPDATE             = "";       // ���⳻ ������
							    long BNAPAMT                = 0 ;       // ���⳻ �ݾ�
							    long ANAPAMT                = 0 ;       // ������ �ݾ�
							    String GUM2                 = "";       // ������ȣ2
							    long BSAMT                  = 0 ;       // ����� ���⳻ �ݾ�
							    long BHAMT                  = 0 ;       // �ϼ��� ���⳻ �ݾ�
							    long BGAMT                  = 0 ;       // ���ϼ��� ���⳻ �ݾ�
							    long BMAMT                  = 0 ;       // ���̿�δ�� ���⳻ �ݾ�
							    long ASAMT                  = 0 ;       // ����� ������ �ݾ�      
							    long AHAMT                  = 0 ;       // �ϼ��� ������ �ݾ�      
							    long AGAMT                  = 0 ;       // ���ϼ��� ������ �ݾ�    
							    long AMAMT                  = 0 ;       // ���̿�δ�� ������ �ݾ�
							    String ANAPDATE             = "";       // ������ ������
							    String GUM3                 = "";       // ������ȣ3
							    String CNAPTERM             = "";       // ü���Ⱓ
							    String ADDR                 = "";       // �ּ�
							    String USETERM              = "";       // ���Ⱓ
							    String AUTOREG              = "";       // �ڵ���ü��Ͽ���
							    String NAPGUBUN             = "";       // ���⳻�ı����ڵ�
							    String CUST_ADMIN_NUM       = "";       // ��������ȣ
							    String OCR                  = "";       // OCR
							    String NapJUMMun            = "";
							    String napkiGubun           = "";
								
								String dang = mp_S.getMap("GUBUN").toString().substring(1);
						
				                //�������� SET
				                BNAPDATE = (String) mp_S.getMap("DUE_DT");         //���⳻ ������
				                ANAPDATE = (String) mp_S.getMap("DUE_F_DT");       //������ ������
								
				                ADDR     = (String) mp_S.getMap("ADDRESS");        //�ּ�  
				                OCR      = (String) mp_S.getMap("OCR_1") + (String) mp_S.getMap("OCR_2");
				                
				                //���⳻(B), ������(A) üũ
				                napkiGubun = cUtil.getNapGubun(BNAPDATE, ANAPDATE);

				                NAPGUBUN = napkiGubun;
				                
				                if(napkiGubun.equals("B")) {
				                	NapJUMMun = "1";
				                }else{
				                	NapJUMMun = "2";
				                }
				                
				                CUST_ADMIN_NUM = (String) mp_S.getMap("CUST_NO"); 
				                
								if(dang.equals("1") || dang.equals("3")) {
									
									if(log.isDebugEnabled()){
										log.debug("����/����");
									}

					                //�����Ѿ� SET
					                BNAPAMT  = ((BigDecimal)mp_S.getMap("SUM_B_AMT")).longValue();         //���⳻ �ݾ�
					                ANAPAMT  = ((BigDecimal)mp_S.getMap("SUM_F_AMT")).longValue();         //������ �ݾ�
									
					                GUM2 = "";
					                
					                //���μ��αݾ� SET
					                BSAMT    = ((BigDecimal)mp_S.getMap("FEE_AMT1")).longValue();         //���⳻ ������ݾ�
					                BHAMT    = ((BigDecimal)mp_S.getMap("FEE_AMT2")).longValue();         //���⳻ �ϼ����ݾ�
					                BGAMT    = ((BigDecimal)mp_S.getMap("FEE_AMT4")).longValue();         //���⳻ ���ϼ��ݾ�
					                BMAMT    = ((BigDecimal)mp_S.getMap("FEE_AMT3")).longValue();         //���⳻ ���̿�ݾ�
					                ASAMT    = ((BigDecimal)mp_S.getMap("FEE_AMT1_H")).longValue();         //������ ������ݾ�
					                AHAMT    = ((BigDecimal)mp_S.getMap("FEE_AMT2_H")).longValue();         //������ �ϼ����ݾ�
					                AGAMT    = ((BigDecimal)mp_S.getMap("FEE_AMT4_H")).longValue();         //������ ���ϼ��ݾ�
					                AMAMT    = ((BigDecimal)mp_S.getMap("FEE_AMT3_H")).longValue();         //������ ���̿�ݾ�

					                GUM3 = "";

					                CNAPTERM = "0";  //ü���Ⱓ

					                USETERM  = (String) mp_S.getMap("USE_STT_DATE") + (String) mp_S.getMap("USE_END_DATE");          //���Ⱓ

					                AUTOREG  = "N";      //�ڵ���ü ��Ͽ���

					                
					                
								} else if (dang.equals("2")){
									
									if(log.isDebugEnabled()){
										log.debug("ü��");
									}
									
									
									//�������� SET
					                //���⳻�ΰ�� ���⳻ ���� SET, �������� ��� ������ ���� SET
					                //���⳻(B), ������(A) üũ
									if(NAPGUBUN.equals("B"))		                {
					                    BNAPDATE = (String) mp_S.getMap("DUE_DT");         //������ ������
					                    ANAPDATE = BNAPDATE;
					                } else  {
					                    BNAPDATE = (String) mp_S.getMap("DUE_F_DT");       //������ ������
					                    ANAPDATE = BNAPDATE;
					                }
									
									//ü�����ΰ�� ������ ���⳻�������� ���α��� ����
					                NAPGUBUN = "B";

					                //�����Ѿ� SET
					                BNAPAMT  = ((BigDecimal)mp_S.getMap("SUM_B_AMT")).longValue();          //���⳻ �ݾ�
					                ANAPAMT  = 0;

					                GUM2 = "";

					                //���μ��αݾ� SET
					                BSAMT    = ((BigDecimal)mp_S.getMap("FEE_AMT1")).longValue();         //���⳻ ������ݾ�
					                BHAMT    = ((BigDecimal)mp_S.getMap("FEE_AMT2")).longValue();         //���⳻ �ϼ����ݾ�
					                BGAMT    = ((BigDecimal)mp_S.getMap("FEE_AMT4")).longValue();         //���⳻ ���ϼ��ݾ�
					                BMAMT    = ((BigDecimal)mp_S.getMap("FEE_AMT3")).longValue();         //���⳻ ���̿�ݾ�
					                ASAMT    = 0;
					                AHAMT    = 0;
					                AGAMT    = 0;
					                AMAMT    = 0;

					                GUM3 = "";

					                CNAPTERM = (String) mp_S.getMap("NOT_STT_DATE") + (String) mp_S.getMap("NOT_END_DATE");          //ü���Ⱓ
					                USETERM = CNAPTERM;          //���Ⱓ

					                AUTOREG = "N";      //�ڵ���ü ��Ͽ���

								}
								
								mpData.setMap("BUSINESSGUBUN"     , "GR6676");                                            /*��������            */
								mpData.setMap("DATAGUBUN"         , "22");                                                /*�����ͱ���          */
								mpData.setMap("SEQNO"             , CbUtil.lPadString(String.valueOf(inc), 7, '0'));      /*�Ϸù�ȣ            */
								mpData.setMap("PUBGCODE"          , mfJunmunInfo.getMap("PUBGCODE"));                     /*�������з��ڵ�    */
								mpData.setMap("JIRONO"            , mfJunmunInfo.getMap("GIRONO"));                       /*������Ȯ�ι� ���� */
								mpData.setMap("CUSTNO"            , mp_S.getMap("CUST_NO"));                              /*���밡��ȣ          */
								mpData.setMap("TAX_YM"            , mp_S.getMap("TAX_YM"));                               /*�ΰ����            */
								mpData.setMap("DANGGUBUN"         , mp_S.getMap("GUBUN").toString().substring(1));        /*��� ����                                         */
								mpData.setMap("NPNO"              , mp_S.getMap("PRT_NPNO"));                             /*���� ��ȣ                                         */
								mpData.setMap("REGNM"             , mp_S.getMap("REG_NM"));                               /*����                */
						        mpData.setMap("BNAPDATE"          , BNAPDATE);                                            /*���⳻ ������              | ���� ������          */
								mpData.setMap("BNAPAMT"           , BNAPAMT);                                             /*���⳻ �ݾ�                | ü����               */
								mpData.setMap("ANAPAMT"           , ANAPAMT);                                             /*������ �ݾ�                                       */
								mpData.setMap("GUM2"              , GUM2);                                                /*������ȣ 2                                        */
								mpData.setMap("BSAMT"             , BSAMT);                                               /*��������⳻�ݾ�           |  ����� ü����       */
								mpData.setMap("BHAMT"             , BHAMT);                                               /*�ϼ������⳻�ݾ�           |  �ϼ��� ü����       */
								mpData.setMap("BGAMT"             , BGAMT);                                               /*���ϼ����⳻�ݾ�           |  ���ϼ� ü����       */
								mpData.setMap("BMAMT"             , BMAMT);                                               /*���̿�δ�ݳ��⳻�ݾ�     |  ���̿�δ��ü����  */
								mpData.setMap("ASAMT"             , ASAMT);                                               /*����������ıݾ�                                  */
								mpData.setMap("AHAMT"             , AHAMT);                                               /*�ϼ��������ıݾ�                                  */
								mpData.setMap("AGAMT"             , AGAMT);                                               /*���ϼ������ıݾ�                                  */
								mpData.setMap("AMAMT"             , AMAMT);                                               /*���̿�δ�ݳ����ıݾ�                            */
								mpData.setMap("ANAPDATE"          , ANAPDATE);                                            /*������ ������                                     */
								mpData.setMap("GUM3"              , GUM3);                                                /*������ȣ 3                                        */
								mpData.setMap("CNAPTERM"          , CNAPTERM);                                            /*ü�� �Ⱓ                                         */
								mpData.setMap("ADDR"              , ADDR);                                                /*�ּ�                                              */
								mpData.setMap("USETERM"           , USETERM);                                             /*��� �Ⱓ                                         */
								mpData.setMap("AUTOREG"           , "N");                                                 /*�ڵ���ü ��� ����                                */
								mpData.setMap("SNAP_BANK_CODE"    , mp_S.getMap("BRC_NO"));                               /*�������� ���� �ڵ�                                */
								mpData.setMap("SNAP_SYMD"         , mp_S.getMap("SUNAPDT"));                              /*���� �Ͻ�                                         */
								mpData.setMap("NAPGUBUN"          , NapJUMMun);                                           /*���� ���� ����                                    */
								mpData.setMap("ETC1"              , " ");                                                 /*���� ���� FIELD                                   */
								mpData.setMap("CUST_ADMIN_NUM"    , " ");                                                 /*��������ȣ                                      */
								mpData.setMap("OCR"               , mp_S.getMap("OCR_BD"));                               /*OCR BAND                                          */
								mpData.setMap("FILLER"            , " ");                                                 /*FILLER              */
								
								if(mfJunmunInfo.getMap("NAPGB").equals(1) || mfJunmunInfo.getMap("NAPGB").equals(3)) { //���⳻�Ǵ� ���⹫��
									
									if(BNAPAMT != Long.getLong(mfJunmunInfo.getMap("NAPBU_AMT").toString())) {
										//�ݾ��� Ʋ����� : ������� U:����
										mpData.setMap("PRCGB"          ,"U");    /*ó������*/
									}
									
									amt += BNAPAMT;
									
								}else if(mfJunmunInfo.getMap("NAPGB").equals(2)) { //������
									
									if(ANAPAMT != Long.getLong(mfJunmunInfo.getMap("NAPBU_AMT").toString())) {
										//�ݾ��� Ʋ����� : ������� U:����
										mpData.setMap("PRCGB"          ,"U");    /*ó������*/
									}
									
									amt += ANAPAMT;
								}
								
								sbData.append(new String(KFL_2550.getBuff(mpData)));
							}
							
						}
						
					}					

					if(rec_cnt == alJunmunInfo.size() -1) {
						
						/*�̶��� HEAD�ΰ� TAILER �θ� �����.*/
						mpHead.setMap("BUSINESSGUBUN"  ,"GR6676");                       /*��������          */
						mpHead.setMap("DATAGUBUN"      ,"11");                           /*�����ͱ���        */
						mpHead.setMap("SEQNO"          ,"0000000");                      /*�Ϸù�ȣ          */
						mpHead.setMap("BANKCODE"       ,"011");                          /*(����)�����ڵ�    */
						mpHead.setMap("TOTALCOUNT"     , 0 );                            /*�� DATA RECORD �� */
						mpHead.setMap("TRANSDATE"      ,mf.getMap("ACCTDT"));            /*��������          */
						mpHead.setMap("FILLER"         ," ");              

						/*Trailer ��*/
						mpTail.setMap("BUSINESSGUBUN"  ,"GR6676");                       /*��������          */
						mpTail.setMap("DATAGUBUN"      ,"33");                           /*�����ͱ���        */
						mpTail.setMap("SEQNO"          ,"9999999");                      /*�Ϸù�ȣ          */
						mpTail.setMap("BANKCODE"       ,"011");                          /*(����)�����ڵ�    */
						mpTail.setMap("TOTALCOUNT"     , 0 );                            /*�� DATA RECORD �� */
						mpTail.setMap("TOTALPAYMONEY"  , 0 );                            /*�����ݾ�          */
						mpTail.setMap("FILLER"         ," ");                            /*FILLER            */	

						/*������ ������Ų��.*/
						if(mpHead.toString().length() > 0 || mpTail.toString().length() > 0){
							
							String filedata = "";
							
							mpHead.setMap("TOTALCOUNT"     ,inc); 
							mpTail.setMap("TOTALCOUNT"     ,inc);                            
							mpTail.setMap("TOTALPAYMONEY"  ,amt);                            
							
							KFL_2550.GR6676_Head_FieldList();  /*������� Head ����*/
							sbHead.append(new String(KFL_2550.getBuff(mpHead)));

							KFL_2550.GR6676_Tailer_FieldList();/*������� Tail ����*/
							sbTail.append(new String(KFL_2550.getBuff(mpTail)));
							
							filedata = sbHead.toString() + sbData.toString() + sbTail.toString();
							String sndFile = sndFilePath + "GR6676261004102" + mpHead.getMap("TRANSDATE").toString();
							
							log.debug("filedata.size = [" + filedata.length() + "]");
							log.debug("filedata = [" + filedata + "]");
							
							makeFile(filedata.getBytes(), sndFile, "new");
							
							/*������ ��������ٸ� ���������� �����Ѵ�.*/
							kf_rev_file_transefer(client, sndFile);
							
						}
					}
				}
				
			} else {
				/*
				 * ������ ���ų����� ���ٸ� �������̶� ������ �ϳ�...???
				 * */
				
				String filedata = "";
				
				Txdm2550FieldList KFL_2550 = new Txdm2550FieldList();
				
				StringBuffer sbHead = new StringBuffer();
				StringBuffer sbTail = new StringBuffer();
				
				MapForm mpHead = new MapForm();
				MapForm mpTail = new MapForm();
				
				/*�̶��� HEAD�ΰ� TAILER �θ� �����.*/
				mpHead.setMap("BUSINESSGUBUN"  ,"GR6676");                       /*��������          */
				mpHead.setMap("DATAGUBUN"      ,"11");                           /*�����ͱ���        */
				mpHead.setMap("SEQNO"          ,"0000000");                      /*�Ϸù�ȣ          */
				mpHead.setMap("BANKCODE"       ,"011");                          /*(����)�����ڵ�    */
				mpHead.setMap("TOTALCOUNT"     , 0 );                            /*�� DATA RECORD �� */
				mpHead.setMap("TRANSDATE"      ,mf.getMap("ACCTDT"));            /*��������          */
				mpHead.setMap("FILLER"         ," ");              

				/*Trailer ��*/
				mpTail.setMap("BUSINESSGUBUN"  ,"GR6676");                       /*��������          */
				mpTail.setMap("DATAGUBUN"      ,"33");                           /*�����ͱ���        */
				mpTail.setMap("SEQNO"          ,"9999999");                      /*�Ϸù�ȣ          */
				mpTail.setMap("BANKCODE"       ,"011");                          /*(����)�����ڵ�    */
				mpTail.setMap("TOTALCOUNT"     , 0 );                            /*�� DATA RECORD �� */
				mpTail.setMap("TOTALPAYMONEY"  , 0 );                            /*�����ݾ�          */
				mpTail.setMap("FILLER"         ," ");                            /*FILLER            */	
				
				KFL_2550.GR6676_Head_FieldList();  /*������� Head ����*/
				sbHead.append(new String(KFL_2550.getBuff(mpHead)));

				KFL_2550.GR6676_Tailer_FieldList();/*������� Tail ����*/
				sbTail.append(new String(KFL_2550.getBuff(mpTail)));
				
				filedata = sbHead.toString() + sbTail.toString();
				String sndFile = sndFilePath + "GR6676261004102" + mpHead.getMap("TRANSDATE").toString();
				
				log.debug("filedata.size = [" + filedata.length() + "]");
				log.debug("filedata = [" + filedata + "]");
				
				makeFile(filedata.getBytes(), sndFile, "new");
				
				/*������ ��������ٸ� ���������� �����Ѵ�.*/
				kf_rev_file_transefer(client, sndFile);
				
			}
			

		}catch (Exception e) {
			// TODO Auto-generated catch block
			 e.printStackTrace();
		}
	}
	
	/**
	 * ���ܼ��� ���ೳ�� ���� ����
	 * @param client
	 * @param mf
	 */
	private void res_nontax_receipt_file_transfer(Txbt2550 client, MapForm mf){
		
		ArrayList<MapForm> alJunmunInfo = null;
		
		try {
			
			CbUtil cUtil = CbUtil.getInstance();
			
			/*���ܼ���*/
			mf.setMap("TAX_GB" , "O");
			
			/*�������ų����� �����´�...*/			
			alJunmunInfo = sqlService_cyber.queryForList("TXBT2550.SELECT_JUNMUN_LIST", mf);
			
			if (alJunmunInfo.size()  >  0)   {
				
				Txdm2550FieldList KFL_2550 = new Txdm2550FieldList();
				
				StringBuffer sbHead = new StringBuffer();
				StringBuffer sbTail = new StringBuffer();
				StringBuffer sbData = new StringBuffer();
				
				MapForm mpHead = new MapForm();
				MapForm mpTail = new MapForm();
				MapForm mpData = new MapForm();
				
				int inc = 0;
				long amt = 0;
				
				for ( int rec_cnt = 0;  rec_cnt < alJunmunInfo.size();  rec_cnt++)   {
					
					MapForm mfJunmunInfo =  alJunmunInfo.get(rec_cnt);
					
					/*Ȥ�ó�...because of testing */
					if (mfJunmunInfo == null  ||  mfJunmunInfo.isEmpty() )   {
						continue;
					}
					
					KFL_2550.GR6695_Data_FieldList();  /*������� Data ����*/
					
					mfJunmunInfo.setMap("TAX_NO", mfJunmunInfo.getMap("OCR_BD").toString().substring(0, 31));
					
					//����ó��(����) �Ǿ��ٸ�...(���ೳ�ΰ� �ƴϰ� ����ó���� ���̾�� ��...)
					if (sqlService_cyber.getOneFieldInteger("TXBT2550.SELECT_O_RECIP_CNT", mfJunmunInfo) > 0 ) {
						
						if(mfJunmunInfo.getMap("ACCTDT").equals(mf.getMap("ACCTDT"))) {
							
							inc++;
							
							MapForm mp_O = sqlService_cyber.queryForMap("TXBT2550.SELECT_O_RECIP_LIST", mfJunmunInfo);
							
							String Nap = cUtil.getNapGubun((String)mp_O.getMap("DUE_DT"), (String)mp_O.getMap("DUE_F_DT"));
							if (Nap.equals("B")) {
								Nap = "1";
								amt += Long.parseLong(mp_O.getMap("AMT").toString());
							} else {
								Nap = "2";
								amt += Long.parseLong(mp_O.getMap("AFT_AMT").toString());
							}
							
							/*���ǻ���) ���ೳ�������� ���⳻�� ���� ������ B�μ��� : 20110930 ������ �̺�������*/
							Nap = "B";
							
							mpData.setMap("BUSINESSGUBUN"       ,  "GR6695"  );                                        /*��������            */
							mpData.setMap("DATAGUBUN"           ,  "22"  );                                            /*�����ͱ���          */
							mpData.setMap("SEQNO"               ,  CbUtil.lPadString(String.valueOf(inc), 7, '0')  );  /*�Ϸù�ȣ            */
							mpData.setMap("PUBGCODE"            ,  mfJunmunInfo.getMap("PUBGCODE")  );                 /*�������з��ڵ�    */
							mpData.setMap("JIRONO"              ,  mfJunmunInfo.getMap("GIRONO")  );                   /*������Ȯ�ι� ���� */
							mpData.setMap("EPAY_NO"             ,  mp_O.getMap("EPAY_NO")  );                          /*���ڳ��ι�ȣ        */
							mpData.setMap("REG_NO"              ,  mp_O.getMap("REG_NO")  );                           /*�ֹι�ȣ            */
							mpData.setMap("FIELD1"              ,  " "  );                                             /*���� ���� FIELD 1   */
							mpData.setMap("BUGWA_GB"            ,  mp_O.getMap("TAX_DIV")  );                          /*�������� 		     */
							mpData.setMap("SEMOK_CD"            ,  mp_O.getMap("TAX_ITEM")  );                         /*����/����           */
							mpData.setMap("SEMOK_NM"            ,  mp_O.getMap("TAX_NM")  );                           /*����/�����         */
							mpData.setMap("GBN"                 ,  mp_O.getMap("GBN")  );                              /*����                */
							mpData.setMap("OCR_BD"              ,  mp_O.getMap("OCR_BD") );                            /*���                */
							mpData.setMap("NAP_NAME"            ,  mp_O.getMap("REG_NM")  );                           /*������ ����         */
							mpData.setMap("NAP_BFAMT"           ,  mp_O.getMap("AMT")  );                              /*���⳻ �ݾ�         */
							mpData.setMap("NAP_AFAMT"           ,  mp_O.getMap("AFT_AMT")  );                          /*������ �ݾ�         */
							mpData.setMap("GUKAMT"          	,  mp_O.getMap("NATN_TAX")  );                         /*���� 	             */
							mpData.setMap("GUKAMT_ADD"          ,  mp_O.getMap("NATN_RATE")  );                        /*���� �����         */
							mpData.setMap("SIDO_AMT"            ,  mp_O.getMap("SIDO_TAX")  );                         /*�õ���              */
							mpData.setMap("SIDO_AMT_ADD"        ,  mp_O.getMap("SIDO_RATE")  );                        /*�õ��� �����       */
							mpData.setMap("SIGUNGU_AMT"         ,  mp_O.getMap("SIGUNGU_TAX")  );                      /*�ñ�����      	     */
							mpData.setMap("SIGUNGU_AMT_ADD"     ,  mp_O.getMap("SIGUNGU_RATE")  );                     /*�ñ����� �����   	 */
							mpData.setMap("BUNAP_AMT"           ,  "0"  );                                             /*�г�����/���       */
							mpData.setMap("BUNAP_AMT_ADD"       ,  "0"  );                                             /*�г�����/��� �����*/
							mpData.setMap("FIELD2"              ,  " "  );                                             /*���� ���� FIELD 2   */
							mpData.setMap("NAP_BFDATE"          ,  mp_O.getMap("DUE_DT")  );                           /*������ (���⳻)     */
							mpData.setMap("NAP_AFDATE"          ,  mp_O.getMap("DUE_F_DT")  );                         /*������ (������)     */
							mpData.setMap("GWASE_ITEM"          ,  mp_O.getMap("TAX_GDS")  );                          /*�������            */
							mpData.setMap("BUGWA_TAB"           ,  mp_O.getMap("TAX_DESC")  );                         /*�ΰ�����            */
							mpData.setMap("GOJI_DATE"   		,  mp_O.getMap("BUGWA_DT")  );                         /*�����ڷ� �߻�����   */
							mpData.setMap("OUTO_ICHE_GB"        ,  "N"  );                                             /*�ڵ���ü��Ͽ���    */
							mpData.setMap("SUNAB_BANK_CD"       ,  mp_O.getMap("BRC_NO")  );                           /*�������� ���� �ڵ�  */
							mpData.setMap("RECIP_DATE"          ,  mp_O.getMap("PAY_DT").toString() + "000000"  );     /*�����Ͻ�			 */
							mpData.setMap("NAPGI_BA_GB"         ,  Nap  );                                             /*���⳻�ı���        */
							mpData.setMap("PRCGB"               ,  "D"  );                                             /*ó������            */
							mpData.setMap("FILED3"          	,  " "  );                                             /*�������� FIELD 3    */		

							/*������ ���̳ʸ� ������*/
							sbData.append(new String(KFL_2550.getBuff(mpData)));
							
						}

					} else {
						
						
						if(mfJunmunInfo.getMap("OCR_BD").toString().substring(0, 2).equals("26")) {   /*�����ܼ��� ��ǥ*/
							
							mfJunmunInfo.setMap("TAX_NO"   , mfJunmunInfo.getMap("OCR_BD").toString().substring(2, 31));
							
							log.info("����ǥ TAX_NO = " + mfJunmunInfo.getMap("TAX_NO"));

						} else {
							
							mfJunmunInfo.setMap("TAX_NO"   , mfJunmunInfo.getMap("OCR_BD").toString().substring(0, 31));
						}
						
						mfJunmunInfo.setMap("SGG_COD"  , mfJunmunInfo.getMap("TAX_NO").toString().substring(0, 3)); 	//��û�ڵ�
						mfJunmunInfo.setMap("ACCT_COD" , mfJunmunInfo.getMap("TAX_NO").toString().substring(4, 6));     //ȸ���ڵ�
						mfJunmunInfo.setMap("TAX_ITEM" , mfJunmunInfo.getMap("TAX_NO").toString().substring(6, 12));    //����/�����ڵ�
						mfJunmunInfo.setMap("TAX_YY"   , mfJunmunInfo.getMap("TAX_NO").toString().substring(12, 16));   //�����⵵
						mfJunmunInfo.setMap("TAX_MM"   , mfJunmunInfo.getMap("TAX_NO").toString().substring(16, 18));	//������
						mfJunmunInfo.setMap("TAX_DIV"  , mfJunmunInfo.getMap("TAX_NO").toString().substring(18, 19));	//����ڵ�
						mfJunmunInfo.setMap("HACD"     , mfJunmunInfo.getMap("TAX_NO").toString().substring(19, 22));	//�������ڵ�
						mfJunmunInfo.setMap("TAX_SNO"  , mfJunmunInfo.getMap("TAX_NO").toString().substring(22, 28));	//������ȣ
						
						// �ݾ׺�
						MapForm mp_O = sqlService_cyber.queryForMap("TXBT2550.SELECT_O_SEARCH_LIST", mfJunmunInfo);
						
						if(mp_O.size() > 0) {
							
							if(mfJunmunInfo.getMap("ACCTDT").equals(mf.getMap("ACCTDT"))) {
								
								inc++;
								
								mpData.setMap("BUSINESSGUBUN"       ,  mfJunmunInfo.getMap("BUSINESSGUBUN")  );            /*��������            */
								mpData.setMap("DATAGUBUN"           ,  "22"  );                                            /*�����ͱ���          */
								mpData.setMap("SEQNO"               ,  CbUtil.lPadString(String.valueOf(inc), 7, '0')  );  /*�Ϸù�ȣ            */
								mpData.setMap("PUBGCODE"            ,  mfJunmunInfo.getMap("PUBGCODE")  );                 /*�������з��ڵ�    */
								mpData.setMap("JIRONO"              ,  mfJunmunInfo.getMap("GIRONO")  );                   /*������Ȯ�ι� ���� */
								mpData.setMap("EPAY_NO"             ,  mp_O.getMap("EPAY_NO")  );                          /*���ڳ��ι�ȣ        */
								mpData.setMap("REG_NO"              ,  mp_O.getMap("REG_NO")  );                           /*�ֹι�ȣ            */
								mpData.setMap("FIELD1"              ,  " "  );                                             /*���� ���� FIELD 1   */
								mpData.setMap("BUGWA_GB"            ,  mp_O.getMap("TAX_DIV")  );                          /*�������� 		     */
								mpData.setMap("SEMOK_CD"            ,  mp_O.getMap("TAX_ITEM")  );                         /*����/����           */
								mpData.setMap("SEMOK_NM"            ,  mp_O.getMap("TAX_NM")  );                           /*����/�����         */
								mpData.setMap("GBN"                 ,  mp_O.getMap("GBN")  );                              /*����                */
								mpData.setMap("OCR_BD"              ,  mp_O.getMap("OCR_BD") );                            /*���                */
								mpData.setMap("NAP_NAME"            ,  mp_O.getMap("REG_NM")  );                           /*������ ����         */
								mpData.setMap("NAP_BFAMT"           ,  mp_O.getMap("AMT")  );                              /*���⳻ �ݾ�         */
								mpData.setMap("NAP_AFAMT"           ,  mp_O.getMap("AFT_AMT")  );                          /*������ �ݾ�         */
								mpData.setMap("GUKAMT"          	,  mp_O.getMap("NATN_TAX")  );                         /*���� 	             */
								mpData.setMap("GUKAMT_ADD"          ,  mp_O.getMap("NATN_RATE")  );                        /*���� �����         */
								mpData.setMap("SIDO_AMT"            ,  mp_O.getMap("SIDO_TAX")  );                         /*�õ���              */
								mpData.setMap("SIDO_AMT_ADD"        ,  mp_O.getMap("SIDO_RATE")  );                        /*�õ��� �����       */
								mpData.setMap("SIGUNGU_AMT"         ,  mp_O.getMap("SIGUNGU_TAX")  );                      /*�ñ�����      	     */
								mpData.setMap("SIGUNGU_AMT_ADD"     ,  mp_O.getMap("SIGUNGU_RATE")  );                     /*�ñ����� �����   	 */
								mpData.setMap("BUNAP_AMT"           ,  "0"  );                                             /*�г�����/���       */
								mpData.setMap("BUNAP_AMT_ADD"       ,  "0"  );                                             /*�г�����/��� �����*/
								mpData.setMap("FIELD2"              ,  " "  );                                             /*���� ���� FIELD 2   */
								mpData.setMap("NAP_BFDATE"          ,  mp_O.getMap("DUE_DT")  );                           /*������ (���⳻)     */
								mpData.setMap("NAP_AFDATE"          ,  mp_O.getMap("DUE_F_DT")  );                         /*������ (������)     */
								mpData.setMap("GWASE_ITEM"          ,  mp_O.getMap("TAX_GDS")  );                          /*�������            */
								mpData.setMap("BUGWA_TAB"           ,  mp_O.getMap("TAX_DESC")  );                         /*�ΰ�����            */
								mpData.setMap("GOJI_DATE"   		,  mp_O.getMap("BUGWA_DT")  );                         /*�����ڷ� �߻�����   */
								mpData.setMap("OUTO_ICHE_GB"        ,  "N"  );                                             /*�ڵ���ü��Ͽ���    */
								mpData.setMap("SUNAB_BANK_CD"       ,  "0"  );                                             /*�������� ���� �ڵ�  */
								mpData.setMap("RECIP_DATE"          ,  "0"  );                                             /*�����Ͻ�			 */
								mpData.setMap("NAPGI_BA_GB"         ,  "B"  );                                             /*���⳻�ı���        */
								mpData.setMap("FILED3"          	,  " "  );                                             /*�������� FIELD 3    */		

								if(mfJunmunInfo.getMap("NAPGB").equals(1) || mfJunmunInfo.getMap("NAPGB").equals(3)) { //���⳻�Ǵ� ���⹫��
									
									if(Long.getLong(mp_O.getMap("AMT").toString()) != Long.getLong(mfJunmunInfo.getMap("NAPBU_AMT").toString())) {
										//�ݾ��� Ʋ����� : ������� U:����
										mpData.setMap("PRCGB"          ,"U");    /*ó������*/
									}
									
									amt += Long.parseLong(mp_O.getMap("AMT").toString());
									
								}else if(mfJunmunInfo.getMap("NAPGB").equals(2)) { //������ (�Ͼ�� ����..: ���������� ���⳻�� ��û�����ϴٰ� ��)
									
									if(Long.getLong(mp_O.getMap("AFT_AMT").toString()) != Long.getLong(mfJunmunInfo.getMap("NAPBU_AMT").toString())) {
										//�ݾ��� Ʋ����� : ������� U:����
										mpData.setMap("PRCGB"          ,"U");    /*ó������*/
									}
									
									amt += Long.parseLong(mp_O.getMap("AFT_AMT").toString());
								}
								
								sbData.append(new String(KFL_2550.getBuff(mpData)));
								
							}

						}
						
					}
					
					if(rec_cnt == alJunmunInfo.size() -1) {
						
						/*�̶��� HEAD�ΰ� TAILER �θ� �����.*/
						mpHead.setMap("BUSINESSGUBUN"  ,"GR6695");                       /*��������         */
						mpHead.setMap("DATAGUBUN"      ,"11");                           /*�����ͱ���       */
						mpHead.setMap("SEQNO"          ,"0000000");                      /*�Ϸù�ȣ         */
						mpHead.setMap("BANKCODE"       ,"032");                          /*(����)�����ڵ�   */
						mpHead.setMap("TOTALCOUNT"     , 0 );                            /*�� DATA RECORD ��*/
						mpHead.setMap("TRANSDATE"      ,mf.getMap("ACCTDT"));            /*��������         */
						mpHead.setMap("FILLER"         ," ");              

						/*Trailer ��*/
						mpTail.setMap("BUSINESSGUBUN"  ,"GR6695");                       /*��������         */
						mpTail.setMap("DATAGUBUN"      ,"33");                           /*�����ͱ���       */
						mpTail.setMap("SEQNO"          ,"9999999");                      /*�Ϸù�ȣ         */
						mpTail.setMap("BANKCODE"       ,"032");                          /*(����)�����ڵ�   */
						mpTail.setMap("TOTALCOUNT"     , 0 );                            /*�� DATA RECORD ��*/
						mpTail.setMap("TOTALPAYMONEY"  , 0 );                            /*�����ݾ�         */
						mpTail.setMap("FILLER"         ," ");                            /*FILLER           */	
						
						/*������ ������Ų��.*/
						if(mpHead.toString().length() > 0 || mpTail.toString().length() > 0){
							
							String filedata = "";
							
							mpHead.setMap("TOTALCOUNT"     ,inc); 
							mpTail.setMap("TOTALCOUNT"     ,inc);                            
							mpTail.setMap("TOTALPAYMONEY"  ,amt);                            
							
							
							KFL_2550.GR6695_Head_FieldList();  /*������� Head ����*/
							sbHead.append(new String(KFL_2550.getBuff(mpHead)));

							KFL_2550.GR6695_Tailer_FieldList();/*������� Tail ����*/
							sbTail.append(new String(KFL_2550.getBuff(mpTail)));
							
							filedata = sbHead.toString() + sbData.toString() + sbTail.toString();
							String sndFile = sndFilePath + "GR6695261500172" + mpHead.getMap("TRANSDATE").toString();
							
							log.debug("filedata.size = [" + filedata.length() + "]");
							log.debug("filedata = [" + filedata + "]");
							
							makeFile(filedata.getBytes(), sndFile, "new");
							
							/*������ ��������ٸ� ���������� �����Ѵ�.*/
							kf_rev_file_transefer(client, sndFile);
							
						}
						
					}
					
				}
				
			} else {
				
				/*���ܼ��� ���ೳ�� ��� �� ���Ϸ��� �����´�.*/			
				String filedata = "";
				
				Txdm2550FieldList KFL_2550 = new Txdm2550FieldList();
				
				StringBuffer sbHead = new StringBuffer();
				StringBuffer sbTail = new StringBuffer();
				
				MapForm mpHead = new MapForm();
				MapForm mpTail = new MapForm();
				
				/*�̶��� HEAD�ΰ� TAILER �θ� �����.*/
				mpHead.setMap("BUSINESSGUBUN"  ,"GR6695");                       /*��������         */
				mpHead.setMap("DATAGUBUN"      ,"11");                           /*�����ͱ���       */
				mpHead.setMap("SEQNO"          ,"0000000");                      /*�Ϸù�ȣ         */
				mpHead.setMap("BANKCODE"       ,"032");                          /*(����)�����ڵ�   */
				mpHead.setMap("TOTALCOUNT"     , 0 );                            /*�� DATA RECORD ��*/
				mpHead.setMap("TRANSDATE"      ,mf.getMap("ACCTDT"));            /*��������         */
				mpHead.setMap("FILLER"         ," ");              

				/*Trailer ��*/
				mpTail.setMap("BUSINESSGUBUN"  ,"GR6695");                       /*��������         */
				mpTail.setMap("DATAGUBUN"      ,"33");                           /*�����ͱ���       */
				mpTail.setMap("SEQNO"          ,"9999999");                      /*�Ϸù�ȣ         */
				mpTail.setMap("BANKCODE"       ,"032");                          /*(����)�����ڵ�   */
				mpTail.setMap("TOTALCOUNT"     , 0 );                            /*�� DATA RECORD ��*/
				mpTail.setMap("TOTALPAYMONEY"  , 0 );                            /*�����ݾ�         */
				mpTail.setMap("FILLER"         ," ");                            /*FILLER           */	
				
				KFL_2550.GR6695_Head_FieldList();  /*������� Head ����*/
				sbHead.append(new String(KFL_2550.getBuff(mpHead)));

				KFL_2550.GR6695_Tailer_FieldList();/*������� Tail ����*/
				sbTail.append(new String(KFL_2550.getBuff(mpTail)));
				
				filedata = sbHead.toString() + sbTail.toString();
				String sndFile = sndFilePath + "GR6695261500172" + mpHead.getMap("TRANSDATE").toString();
				
				log.debug("filedata.size = [" + filedata.length() + "]");
				log.debug("filedata = [" + filedata + "]");
				
				makeFile(filedata.getBytes(), sndFile, "new");
				
				/*������ ��������ٸ� ���������� �����Ѵ�.*/
				kf_rev_file_transefer(client, sndFile);

			}

		}catch (Exception e) {
			// TODO Auto-generated catch block
			 e.printStackTrace();
		}
	}

	/**
	 * ȯ�氳���δ�� ���ೳ������ ����
	 * @param client
	 * @param mf
	 */
	@SuppressWarnings("unused")
	private void res_env_improvement_file_transfer(Txbt2550 client, MapForm mf){
		
		ArrayList<MapForm> alJunmunInfo = null;
		
		try {
			
			CbUtil cUtil = CbUtil.getInstance();
			
			mf.setMap("TAX_GB" , "H");
			
			/*�ΰ�����*/			
			alJunmunInfo = sqlService_cyber.queryForList("TXBT2550.SELECT_JUNMUN_LIST", mf);
			
			if (alJunmunInfo.size()  >  0)   {
				
				log.debug("========================================== ");
				log.debug("ȯ�氳���δ�ݿ��ೳ������ ");
				log.debug("========================================== ");
				
				Txdm2550FieldList KFL_2550 = new Txdm2550FieldList();
				
				StringBuffer sbHead = new StringBuffer();
				StringBuffer sbTail = new StringBuffer();
				StringBuffer sbData = new StringBuffer();
				
				MapForm mpHead = new MapForm();
				MapForm mpTail = new MapForm();
				MapForm mpData = new MapForm();
				
				int inc = 0;
				long amt = 0;
				
				for ( int rec_cnt = 0;  rec_cnt < alJunmunInfo.size();  rec_cnt++)   {
					
					MapForm mfJunmunInfo =  alJunmunInfo.get(rec_cnt);
					
					/*Ȥ�ó�...because of testing */
					if (mfJunmunInfo == null  ||  mfJunmunInfo.isEmpty() )   {
						continue;
					}
					
					KFL_2550.GR6697_Data_FieldList();  /*������� Data ����*/
					
					long NAP_BFAMT = 0;
					long NAP_AFAMT = 0;
					long BONSE     = 0;
					long BONSE_ADD = 0;
					String napkiGubun    = "";
					String NP_BAF_GUBUN  = "";
					String JA_GOGI_GUBUN = "";
					String NapGn = "";

					//����ó��(����) �Ǿ��ٸ�...
					if (sqlService_cyber.getOneFieldInteger("TXBT2550.SELECT_ENV_KFPAY_CNT", mfJunmunInfo) > 0 ) {
						
						if(mfJunmunInfo.getMap("ACCTDT").equals(mf.getMap("ACCTDT"))) {
							
							inc++;
							
							MapForm mp_H = sqlService_cyber.queryForMap("TXBT2550.SELECT_ENV_KFPAY_LIST", mfJunmunInfo);
							
							napkiGubun = cUtil.getNapGubun((String)mp_H.getMap("DUE_DT"), (String)mp_H.getMap("DUE_F_DT"));
						    
						    if(napkiGubun.equals("B")){
						    	
						    	NapGn = "1";
						    	
						    	if(mp_H.getMap("DUE_DT").equals(mp_H.getMap("DUE_F_DT"))){
						    		
						    		NAP_BFAMT = ((BigDecimal)mp_H.getMap("ENV_AMT")).longValue()+((BigDecimal)mp_H.getMap("ADD_AMT")).longValue();   // ���⳻�ݾ�
				                    NAP_AFAMT = ((BigDecimal)mp_H.getMap("ENV_AMT")).longValue()+((BigDecimal)mp_H.getMap("ADD_AMT")).longValue();   // �����ıݾ�

				                    BONSE         = NAP_BFAMT;   // ���⳻�ݾ�
				                    BONSE_ADD     = NAP_AFAMT;   // �����ıݾ�

						    	} else {
						    		
						    		NAP_BFAMT = ((BigDecimal)mp_H.getMap("ENV_AMT")).longValue();   // ���⳻�ݾ�
				                    NAP_AFAMT = ((BigDecimal)mp_H.getMap("ENV_AMT")).longValue()+((BigDecimal)mp_H.getMap("ADD_AMT")).longValue();  // �����ıݾ�

				                    BONSE         = NAP_BFAMT;   
				                    BONSE_ADD     = NAP_AFAMT;   

						    	}
						    	
						    } else if(napkiGubun.equals("A")) {
						    	
						    	NapGn = "2";
						    	
						    	NAP_BFAMT = ((BigDecimal)mp_H.getMap("ENV_AMT")).longValue();   // ���⳻�ݾ�
			                    NAP_AFAMT = ((BigDecimal)mp_H.getMap("ENV_AMT")).longValue()+((BigDecimal)mp_H.getMap("ADD_AMT")).longValue();  // �����ıݾ�

			                    BONSE         = NAP_BFAMT;   
			                    BONSE_ADD     = NAP_AFAMT;   
						    	
						    }

						    mpData.setMap("BUSINESSGUBUN"       ,  mfJunmunInfo.getMap("BUSINESSGUBUN")  );            /*��������            */
							mpData.setMap("DATAGUBUN"           ,  mfJunmunInfo.getMap("DATAGUBUN")  );                /*�����ͱ���          */
							mpData.setMap("SEQNO"               ,  CbUtil.lPadString(String.valueOf(inc), 7, '0')  );  /*�Ϸù�ȣ            */
							mpData.setMap("PUBGCODE"            ,  mfJunmunInfo.getMap("PUBGCODE")  );                 /*�������з��ڵ�    */
							mpData.setMap("JIRONO"              ,  mfJunmunInfo.getMap("JIRONO")  );                   /*������Ȯ�ι� ���� */
							mpData.setMap("ETAX_NO"             ,  mp_H.getMap("EPAY_NO")  );                          /*���ڳ��ι�ȣ              */
							mpData.setMap("JUMIN_NO"            ,  mp_H.getMap("REG_NO")  );                           /*�ֹ�(�����,����)��Ϲ�ȣ */
							mpData.setMap("SIDO"                ,  "26"  );                                            /*�õ�                      */
							mpData.setMap("GU_CODE"             ,  mp_H.getMap("SGG_COD")  );                          /*�������(�ñ���)          */
							mpData.setMap("CONFIRM_NO1"         ,  "1"  );                                             /*������ȣ 1                */
							mpData.setMap("HCALVAL"             ,  mp_H.getMap("ACCT_COD")  );                         /*ȸ��                      */
							mpData.setMap("GWA_MOK"             ,  mp_H.getMap("TAX_ITEM")  );                         /*����/����                 */
							mpData.setMap("TAX_YYMM"            ,  mp_H.getMap("TAX_YM")  );                           /*�⵵/���                 */
							mpData.setMap("KIBUN"               ,  mp_H.getMap("TAX_DIV")  );                          /*����                      */
							mpData.setMap("DONG_CODE"           ,  mp_H.getMap("HACD")  );                             /*������(���鵿)            */
							mpData.setMap("GWASE_NO"            ,  mp_H.getMap("TAX_SNO")  );                          /*���� ��ȣ                 */
							mpData.setMap("CONFIRM_NO2"         ,  "2"  );                                             /*������ȣ 2                */
							mpData.setMap("NAP_NAME"            ,  mp_H.getMap("REG_NM")  );                           /*������ ����               */
							mpData.setMap("NAP_BFAMT"           ,  NAP_BFAMT  );                                       /*���⳻ �ݾ�               */
							mpData.setMap("NAP_AFAMT"           ,  NAP_AFAMT  );                                       /*������ �ݾ�               */
						    mpData.setMap("CONFIRM_NO3"         ,  "3"  );                                             /*������ȣ 3                */
						    mpData.setMap("GWASE_RULE"          ,  "0"  );                                             /*���� ǥ��                 */
						    mpData.setMap("BONSE"               ,  BONSE  );                                           /*�δ��                    */
						    mpData.setMap("BONSE_ADD"           ,  BONSE_ADD  );                                       /*�δ�� �����             */
						    mpData.setMap("DOSISE"              ,  mp_H.getMap("MI_AMT")  );                           /*�̼� �δ��               */
						    mpData.setMap("DOSISE_ADD"          ,  mp_H.getMap("ENV_MIADD_AMT")  );                    /*�̼� �δ�� �����        */
						    mpData.setMap("GONGDONGSE"          ,  "0"  );                                             /*���� ���� FIELD 1         */
						    mpData.setMap("GONGDONGSE_ADD"      ,  "0"  );                                             /*���� ���� FIELD 2         */
						    mpData.setMap("EDUSE"               ,  "0"  );                                             /*���� ���� FIELD 3         */
						    mpData.setMap("EDUSE_ADD"           ,  "0"  );                                             /*���� ���� FIELD 4         */
						    mpData.setMap("NAP_BFDATE"          ,  mp_H.getMap("DUE_DT")  );                           /*������ (���⳻)           */
						    mpData.setMap("NAP_AFDATE"          ,  mp_H.getMap("DUE_F_DT")  );                         /*������ (������)           */
						    mpData.setMap("CONFIRM_NO4"         ,  "4"  );                                             /*������ȣ 4                */
						    mpData.setMap("FILLER1"             ,  "0"  );                                             /*�ʷ�                      */
						    mpData.setMap("CONFIRM_NO5"         ,  "5"  );                                             /*������ȣ 5                */
						    mpData.setMap("GWASE_DESC"          ,  mp_H.getMap("MLGN_IF1")  );                         /*���� ����                 */
						    mpData.setMap("GWASE_PUB_DESC"      ,  mp_H.getMap("MLGN_IF2")  );                         /*���� ǥ�� ����            */
						    mpData.setMap("GOJICR_DATE"         ,  mp_H.getMap("TAX_DT")  );                           /*�����ڷ� �߻�����         */
						    mpData.setMap("JADONG_YN"           ,  "N"  );                                             /*�ڵ���ü ��� ����        */
						    mpData.setMap("JIJUM_CODE"          ,  mp_H.getMap("BRC_NO")  );                           /*�������� ���� �ڵ�        */
						    mpData.setMap("NAPBU_DATE"          ,  mp_H.getMap("REG_DT")  );                           /*���� �Ͻ�                 */
						    mpData.setMap("NP_BAF_GUBUN"        ,  NapGn  );                                           /*���� ���� ����            */
						    mpData.setMap("TAX_GOGI_GUBUN"      ,  " "  );                                             /*���� ���� ����            */
						    mpData.setMap("JA_GOGI_GUBUN"       ,  " "  );                                             /*��ǥ ���� ����            */
						    mpData.setMap("PRCGB"               ,  "D"  );                                             /*ó������                  */
						    mpData.setMap("RESERV2"             ,  " "  );                                             /*���� ���� FIELD 5         */
						    
						    sbData.append(new String(KFL_2550.getBuff(mpData)));
							
						}
						

					} else {

						MapForm mp_H = sqlService_cyber.queryForMap("TXBT2550.SELECT_ENV_LEVY_LIST", mfJunmunInfo);
						
						if(mp_H.size() > 0) {
							
							if(mfJunmunInfo.getMap("ACCTDT").equals(mf.getMap("ACCTDT"))) {
								
								inc++;
								
								napkiGubun = cUtil.getNapGubun((String)mp_H.getMap("DUE_DT"), (String)mp_H.getMap("DUE_F_DT"));
							    
							    if(napkiGubun.equals("B")){
							    	
							    	NapGn = "1";
							    	
							    	if(mp_H.getMap("DUE_DT").equals(mp_H.getMap("DUE_F_DT"))){
							    		
							    		NAP_BFAMT = ((BigDecimal)mp_H.getMap("ENV_AMT")).longValue()+((BigDecimal)mp_H.getMap("ADD_AMT")).longValue();   // ���⳻�ݾ�
					                    NAP_AFAMT = ((BigDecimal)mp_H.getMap("ENV_AMT")).longValue()+((BigDecimal)mp_H.getMap("ADD_AMT")).longValue();   // �����ıݾ�
					                    NP_BAF_GUBUN = napkiGubun;
					                    JA_GOGI_GUBUN = "4";
					                    BONSE         = NAP_BFAMT;   // ���⳻�ݾ�
					                    BONSE_ADD     = NAP_AFAMT;   // �����ıݾ�

							    	} else {
							    		
							    		NAP_BFAMT = ((BigDecimal)mp_H.getMap("ENV_AMT")).longValue();   // ���⳻�ݾ�
					                    NAP_AFAMT = ((BigDecimal)mp_H.getMap("ENV_AMT")).longValue()+((BigDecimal)mp_H.getMap("ADD_AMT")).longValue();  // �����ıݾ�
					                    NP_BAF_GUBUN = napkiGubun;
					                    JA_GOGI_GUBUN = "4";
					                    BONSE         = NAP_BFAMT;   
					                    BONSE_ADD     = NAP_AFAMT;   

							    	}
							    	
							    } else if(napkiGubun.equals("A")) {
							    	
							    	NapGn = "2";
							    	
							    	NAP_BFAMT = ((BigDecimal)mp_H.getMap("ENV_AMT")).longValue();   // ���⳻�ݾ�
				                    NAP_AFAMT = ((BigDecimal)mp_H.getMap("ENV_AMT")).longValue()+((BigDecimal)mp_H.getMap("ADD_AMT")).longValue();  // �����ıݾ�
				                    NP_BAF_GUBUN = napkiGubun;
				                    JA_GOGI_GUBUN = "4";
				                    BONSE         = NAP_BFAMT;   
				                    BONSE_ADD     = NAP_AFAMT;   
							    	
							    }
							    
							    mpData.setMap("BUSINESSGUBUN"       ,  mfJunmunInfo.getMap("BUSINESSGUBUN")            );  /*��������            */
								mpData.setMap("DATAGUBUN"           ,  mfJunmunInfo.getMap("DATAGUBUN")                );  /*�����ͱ���          */
								mpData.setMap("SEQNO"               ,  CbUtil.lPadString(String.valueOf(inc), 7, '0')  );  /*�Ϸù�ȣ            */
								mpData.setMap("PUBGCODE"            ,  mfJunmunInfo.getMap("PUBGCODE")                 );  /*�������з��ڵ�    */
								mpData.setMap("JIRONO"              ,  mfJunmunInfo.getMap("JIRONO")                   );  /*������Ȯ�ι� ���� */
								mpData.setMap("ETAX_NO"             ,  mp_H.getMap("EPAY_NO")                          );  /*���ڳ��ι�ȣ              */
								mpData.setMap("JUMIN_NO"            ,  mp_H.getMap("REG_NO")                           );  /*�ֹ�(�����,����)��Ϲ�ȣ */
								mpData.setMap("SIDO"                ,  "26"                                            );  /*�õ�                      */
								mpData.setMap("GU_CODE"             ,  mp_H.getMap("SGG_COD")                          );  /*�������(�ñ���)          */
								mpData.setMap("CONFIRM_NO1"         ,  "1"                                             );  /*������ȣ 1                */
								mpData.setMap("HCALVAL"             ,  mp_H.getMap("ACCT_COD")                         );  /*ȸ��                      */
								mpData.setMap("GWA_MOK"             ,  mp_H.getMap("TAX_ITEM")                         );  /*����/����                 */
								mpData.setMap("TAX_YYMM"            ,  mp_H.getMap("TAX_YM")                           );  /*�⵵/���                 */
								mpData.setMap("KIBUN"               ,  mp_H.getMap("TAX_DIV")                          );  /*����                      */
								mpData.setMap("DONG_CODE"           ,  mp_H.getMap("HACD")                             );  /*������(���鵿)            */
								mpData.setMap("GWASE_NO"            ,  mp_H.getMap("TAX_SNO")                          );  /*���� ��ȣ                 */
								mpData.setMap("CONFIRM_NO2"         ,  "2"                                             );  /*������ȣ 2                */
								mpData.setMap("NAP_NAME"            ,  mp_H.getMap("REG_NM")                           );  /*������ ����               */
								mpData.setMap("NAP_BFAMT"           ,  NAP_BFAMT                                       );  /*���⳻ �ݾ�               */
								mpData.setMap("NAP_AFAMT"           ,  NAP_AFAMT                                       );  /*������ �ݾ�               */
							    mpData.setMap("CONFIRM_NO3"         ,  "3"                                             );  /*������ȣ 3                */
							    mpData.setMap("GWASE_RULE"          ,  "0"                                             );  /*���� ǥ��                 */
							    mpData.setMap("BONSE"               ,  BONSE                                           );  /*�δ��                    */
							    mpData.setMap("BONSE_ADD"           ,  BONSE_ADD  );                                       /*�δ�� �����             */
							    mpData.setMap("DOSISE"              ,  mp_H.getMap("MI_AMT")  );                           /*�̼� �δ��               */
							    mpData.setMap("DOSISE_ADD"          ,  mp_H.getMap("ENV_MIADD_AMT")  );                    /*�̼� �δ�� �����        */
							    mpData.setMap("GONGDONGSE"          ,  "0"  );                                             /*���� ���� FIELD 1         */
							    mpData.setMap("GONGDONGSE_ADD"      ,  "0"  );                                             /*���� ���� FIELD 2         */
							    mpData.setMap("EDUSE"               ,  "0"  );                                             /*���� ���� FIELD 3         */
							    mpData.setMap("EDUSE_ADD"           ,  "0"  );                                             /*���� ���� FIELD 4         */
							    mpData.setMap("NAP_BFDATE"          ,  mp_H.getMap("DUE_DT")  );                           /*������ (���⳻)           */
							    mpData.setMap("NAP_AFDATE"          ,  mp_H.getMap("DUE_F_DT")  );                         /*������ (������)           */
							    mpData.setMap("CONFIRM_NO4"         ,  "4"  );                                             /*������ȣ 4                */
							    mpData.setMap("FILLER1"             ,  "0"  );                                             /*�ʷ�                      */
							    mpData.setMap("CONFIRM_NO5"         ,  "5"  );                                             /*������ȣ 5                */
							    mpData.setMap("GWASE_DESC"          ,  mp_H.getMap("MLGN_IF1")  );                         /*���� ����                 */
							    mpData.setMap("GWASE_PUB_DESC"      ,  mp_H.getMap("MLGN_IF2")  );                         /*���� ǥ�� ����            */
							    mpData.setMap("GOJICR_DATE"         ,  mp_H.getMap("TAX_DT")  );                           /*�����ڷ� �߻�����         */
							    mpData.setMap("JADONG_YN"           ,  "N"  );                                             /*�ڵ���ü ��� ����        */
							    mpData.setMap("JIJUM_CODE"          ,  "0"  );                                             /*�������� ���� �ڵ�        */
							    mpData.setMap("NAPBU_DATE"          ,  "0"  );                                             /*���� �Ͻ�                 */
							    mpData.setMap("NP_BAF_GUBUN"        ,  NapGn  );                                           /*���� ���� ����            */
							    mpData.setMap("TAX_GOGI_GUBUN"      ,  " "  );                                             /*���� ���� ����            */
							    mpData.setMap("JA_GOGI_GUBUN"       ,  " "  );                                             /*��ǥ ���� ����            */
							    mpData.setMap("PRCGB"               ,  "D"  );                                             /*ó������                  */
							    mpData.setMap("RESERV2"             ,  " "  );                                             /*���� ���� FIELD 5         */
							    
							    if(NapGn.equals("1"))  { //���⳻�Ǵ� ���⹫��
									
									if(NAP_BFAMT != Long.getLong(mfJunmunInfo.getMap("NAPBU_AMT").toString())) {
										//�ݾ��� Ʋ����� : ������� U:����
										mpData.setMap("PRCGB"          ,"U");    /*ó������*/
									}
									
									amt += NAP_BFAMT;
									
								}else if(NapGn.equals("2")) { //������
									
									if(NAP_AFAMT != Long.getLong(mfJunmunInfo.getMap("NAPBU_AMT").toString())) {
										//�ݾ��� Ʋ����� : ������� U:����
										mpData.setMap("PRCGB"          ,"U");    /*ó������*/
									}
									
									amt += NAP_AFAMT;
								}
								
								sbData.append(new String(KFL_2550.getBuff(mpData)));
								
							}
							
						}
					
					}
					
					if(rec_cnt == alJunmunInfo.size() -1) {
						
						/*�̶��� HEAD�ΰ� TAILER �θ� �����.*/
						mpHead.setMap("BUSINESSGUBUN"  ,"GR6697");                       /*��������         */
						mpHead.setMap("DATAGUBUN"      ,"11");                           /*�����ͱ���       */
						mpHead.setMap("SEQNO"          ,"0000000");                      /*�Ϸù�ȣ         */
						mpHead.setMap("BANKCODE"       ,"000");                          /*(����)�����ڵ�   */
						mpHead.setMap("TOTALCOUNT"     , 0 );                            /*�� DATA RECORD ��*/
						mpHead.setMap("TRANSDATE"      ,mf.getMap("ACCTDT"));            /*��������         */
						mpHead.setMap("FILLER"         ," ");              

						/*Trailer ��*/
						mpTail.setMap("BUSINESSGUBUN"  ,"GR6697");                       /*��������         */
						mpTail.setMap("DATAGUBUN"      ,"33");                           /*�����ͱ���       */
						mpTail.setMap("SEQNO"          ,"9999999");                      /*�Ϸù�ȣ         */
						mpTail.setMap("BANKCODE"       ,"000");                          /*(����)�����ڵ�   */
						mpTail.setMap("TOTALCOUNT"     , 0 );                            /*�� DATA RECORD ��*/
						mpTail.setMap("TOTALPAYMONEY"  , 0 );                            /*�����ݾ�         */
						mpTail.setMap("FILLER"         ," ");                            /*FILLER           */	
						
						/*������ ������Ų��.*/
						if(mpHead.toString().length() > 0 || mpTail.toString().length() > 0){
							
							String filedata = "";
							
							mpHead.setMap("TOTALCOUNT"     ,inc); 
							mpTail.setMap("TOTALCOUNT"     ,inc);                            
							mpTail.setMap("TOTALPAYMONEY"  ,amt);                            
							
							KFL_2550.GR6697_Head_FieldList();  /*������� Head ����*/
							sbHead.append(new String(KFL_2550.getBuff(mpHead)));

							KFL_2550.GR6697_Tailer_FieldList();/*������� Tail ����*/
							sbTail.append(new String(KFL_2550.getBuff(mpTail)));
							
							filedata = sbHead.toString() + sbData.toString() + sbTail.toString();
							String sndFile = sndFilePath + "GR6697261002641" + mpHead.getMap("ACCTDT").toString();
							
							log.debug("filedata.size = [" + filedata.length() + "]");
							log.debug("filedata = [" + filedata + "]");
							
							makeFile(filedata.getBytes(), sndFile, "new");
							
							/*������ ��������ٸ� ���������� �����Ѵ�.*/
							kf_rev_file_transefer(client, sndFile);
							
						}
						
					}
					
				}
				
			} else {
				
				/*ȯ�氳���δ�� ���ೳ�� ��� �� ���Ϸ��� �����´�.*/			
				alJunmunInfo = sqlService_cyber.queryForList("TXBT2550.SELECT_JUNMUN_BASIC", mf);
				
				String filedata = "";
				
				Txdm2550FieldList KFL_2550 = new Txdm2550FieldList();
				
				StringBuffer sbHead = new StringBuffer();
				StringBuffer sbTail = new StringBuffer();
				
				MapForm mpHead = new MapForm();
				MapForm mpTail = new MapForm();
				
				/*�̶��� HEAD�ΰ� TAILER �θ� �����.*/
				mpHead.setMap("BUSINESSGUBUN"  ,"GR6697");                       /*��������         */
				mpHead.setMap("DATAGUBUN"      ,"11");                           /*�����ͱ���       */
				mpHead.setMap("SEQNO"          ,"0000000");                      /*�Ϸù�ȣ         */
				mpHead.setMap("BANKCODE"       ,"000");                          /*(����)�����ڵ�   */
				mpHead.setMap("TOTALCOUNT"     , 0 );                            /*�� DATA RECORD ��*/
				mpHead.setMap("TRANSDATE"      ,mf.getMap("ACCTDT"));            /*��������         */
				mpHead.setMap("FILLER"         ," ");              

				/*Trailer ��*/
				mpTail.setMap("BUSINESSGUBUN"  ,"GR6697");                       /*��������         */
				mpTail.setMap("DATAGUBUN"      ,"33");                           /*�����ͱ���       */
				mpTail.setMap("SEQNO"          ,"9999999");                      /*�Ϸù�ȣ         */
				mpTail.setMap("BANKCODE"       ,"000");                          /*(����)�����ڵ�   */
				mpTail.setMap("TOTALCOUNT"     , 0 );                            /*�� DATA RECORD ��*/
				mpTail.setMap("TOTALPAYMONEY"  , 0 );                            /*�����ݾ�         */
				mpTail.setMap("FILLER"         ," ");                            /*FILLER           */	
				
				KFL_2550.GR6697_Head_FieldList();  /*������� Head ����*/
				sbHead.append(new String(KFL_2550.getBuff(mpHead)));

				KFL_2550.GR6697_Tailer_FieldList();/*������� Tail ����*/
				sbTail.append(new String(KFL_2550.getBuff(mpTail)));
				
				filedata = sbHead.toString() + sbTail.toString();
				String sndFile = sndFilePath + "GR6697261002641" + mpHead.getMap("TRANSDATE").toString();
				
				log.debug("filedata.size = [" + filedata.length() + "]");
				log.debug("filedata = [" + filedata + "]");
				
				makeFile(filedata.getBytes(), sndFile, "new");
				
				/*������ ��������ٸ� ���������� �����Ѵ�.*/
				kf_rev_file_transefer(client, sndFile);
				
			}
			
			
		}catch (Exception e) {
			// TODO Auto-generated catch block
			 e.printStackTrace();
		}
		
	}
	
	/**
	 * ���������� ������ ������ �а� �Ľ��Ѵ�...
	 * */
	@SuppressWarnings("unused")
	private ArrayList<MapForm> setFileReader(MapForm mf) {
		
    	String outFilePath = mf.getMap("AbsolutePath").toString();
    	
    	String recvFileNm = "";
    	
    	File readFile = null;
    	
    	int FileLen = 0;
    	
    	Txdm2550FieldList kf2550_Field = new Txdm2550FieldList();
    	
    	MapForm parseMap = new MapForm();
    	
    	ArrayList<MapForm> alRetrun = new ArrayList<MapForm>();
    	
    	try {
			
    		recvFileNm = outFilePath;
    		
    		readFile = new File(recvFileNm);
    		
    		int i = 0;
    		
    		if(readFile.exists()){
    	
    			log.debug("file_name = [" + readFile.getName() + "] file_size = [" + readFile.length() + "]");
    			
    			BufferedReader br = new BufferedReader(new FileReader(readFile));
    			
    			StringBuffer  sbLine = new StringBuffer();
    			
    			if(mf.getMap("FormName").equals("GR6675")) { /*���ϼ����ΰ��*/
    				FileLen = 300;
    			} else {
    				FileLen = 230;
    			}
    			
    			//������ ���̰� 230/300 Byte �̹Ƿ� 230/300�� ©�� �о��.
    			char[] readLine = new char[FileLen];
    			
    			if(mf.getMap("FormName").equals("GR6653") || 
    			   mf.getMap("FormName").equals("GR6675") || 
    			   mf.getMap("FormName").equals("GR6694") || 
    			   mf.getMap("FormName").equals("GR6696")) { /*���漼*/
    				
    				while (true){
	    				
        				if (br.read(readLine) < 0){
        					break;
        				}
        				
        				sbLine.append(new String(readLine));
        				        				
        			    log.debug("i = ["+ i +"]" + sbLine.toString());	
        				         			    
        			    if (sbLine.substring(6, 8).equals("11")) {
        			    	
        			    	if(mf.getMap("FormName").equals("GR6653")) {
        			    		kf2550_Field.GR6653_Head_FieldList(); 
        			    	} else if(mf.getMap("FormName").equals("GR6675")) {
        			    		kf2550_Field.GR6675_Head_FieldList(); 
        			    	} else if(mf.getMap("FormName").equals("GR6694")) {
        			    		kf2550_Field.GR6694_Head_FieldList(); 
        			    	} else if(mf.getMap("FormName").equals("GR6696")) {
        			    		kf2550_Field.GR6696_Head_FieldList(); 
        			    	}

        			    }else if (sbLine.substring(6, 8).equals("22")) {
        			    	
        			    	if(mf.getMap("FormName").equals("GR6653")) {
        			    		kf2550_Field.GR6653_Data_FieldList(); 
        			    	} else if(mf.getMap("FormName").equals("GR6675")) {
        			    		kf2550_Field.GR6675_Data_FieldList(); 
        			    	} else if(mf.getMap("FormName").equals("GR6694")) {
        			    		kf2550_Field.GR6694_Data_FieldList(); 
        			    	} else if(mf.getMap("FormName").equals("GR6696")) {
        			    		kf2550_Field.GR6696_Data_FieldList(); 
        			    	}
        			    	
        			    }else if (sbLine.substring(6, 8).equals("33")) {
        			    	
        			    	if(mf.getMap("FormName").equals("GR6653")) {
        			    		kf2550_Field.GR6653_Tailer_FieldList(); 
        			    	} else if(mf.getMap("FormName").equals("GR6675")) {
        			    		kf2550_Field.GR6675_Tailer_FieldList(); 
        			    	} else if(mf.getMap("FormName").equals("GR6694")) {
        			    		kf2550_Field.GR6694_Tailer_FieldList(); 
        			    	} else if(mf.getMap("FormName").equals("GR6696")) {
        			    		kf2550_Field.GR6696_Tailer_FieldList(); 
        			    	}
        			    }
        			    	
        			    parseMap = kf2550_Field.parseBuff(sbLine.toString().getBytes());
        			    parseMap.setMap("DATAGB", sbLine.substring(6, 8)); /*���� ������ ����*/
    			    	
        			    alRetrun.add(parseMap);
        			    
    			    	log.debug("Parse = " + parseMap.getMaps());
    			    	
        			    sbLine.delete(0, FileLen);
        			    
        				i++;
        			}
    				
    			}
    			
    			br.close();
    			
    		}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		return alRetrun;
    }
	
	/**
	 * ���������� ���������� �����Ѵ�....
	 * @return
	 */
	private boolean kf_rev_file_transefer(Txbt2550 client, String kf_snd_file_nm) {
		
		
		log.info("=====================kf_rev_file_transefer()����=======================");
		
		int connPort = 0;
		long filesz = 0;
		String connIP = "";
		boolean isConnect = false;
		
		int series_cnt = 0;  /*�������� �����ϴ� ��� : 8������ ������û*/
		int timeout    = 30;
		
		String MsgHead = "1052F   ";
		
		byte[] resv_msg = new byte[1056];
		
		Txdm2550FieldList KFL_2550 = null;
		
		MapForm cmMap;
		
		try {
		
			outFilePath  = "/app/cyber_ap/recvdata/";
			backFilePath = "/app/cyber_ap/backup/";

			connPort =  Utils.getResourceInt("ApplicationResource", "kftc.recv_res.port");
			connIP   =  Utils.getResource("ApplicationResource", "kftc.recv_res.ip");
			
			try {
				
				client.Connect(connIP, connPort);       /*������*/
				
				isConnect = true;
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				
				isConnect = false;
			}

			if(!isConnect) {
				
				log.info("Soket ���� ���� :: IP[" + connIP + "] PORT[" + connPort + "]");
				
			} else {
				
				/*�����غ� ���� (P_INFO) �������� -> ������ �������� ����*/
				File outFile = new File(kf_snd_file_nm);

				
				if(outFile.exists()) {
					filesz = outFile.length();
					
					log.debug("�������� : " + outFile.getName());
					log.debug("���ϱ��� : " + filesz);
					
				} else {
					throw new Exception(outFile.getName() + " �������� �ʽ��ϴ�. ������ Ȯ���ϼ���.");
				}
				
				byte[] snd_type     = "I".getBytes();
				byte[] snd_flag1    = "E".getBytes();
				byte[] snd_flag2    = "u".getBytes();
				byte[] snd_flag3    = CbUtil.nullByte(1);
				byte[] snd_filesize = CbUtil.setOffset(filesz);
				byte[] snd_userid   = CbUtil.nullByte(64);
				byte[] snd_pass     = CbUtil.nullByte(64);
				byte[] snd_filenm   = CbUtil.rPadByte(outFile.getName().getBytes(), 128);
				byte[] snd_reser    = CbUtil.nullByte(768);
				
				byte[] bMd5 = new byte[1032];
				
				System.arraycopy(snd_type,      0, bMd5, 0, snd_type.length);
				System.arraycopy(snd_flag1,     0, bMd5, snd_type.length, snd_flag1.length);
				System.arraycopy(snd_flag2,     0, bMd5, snd_type.length + snd_flag1.length, snd_flag2.length);
				System.arraycopy(snd_flag3,     0, bMd5, snd_type.length + snd_flag1.length + snd_flag2.length, snd_flag3.length);
				System.arraycopy(snd_filesize,  0, bMd5, snd_type.length + snd_flag1.length + snd_flag2.length + snd_flag3.length, snd_filesize.length);
				System.arraycopy(snd_userid,    0, bMd5, snd_type.length + snd_flag1.length + snd_flag2.length + snd_flag3.length + snd_filesize.length, snd_userid.length);
				System.arraycopy(snd_pass,      0, bMd5, snd_type.length + snd_flag1.length + snd_flag2.length + snd_flag3.length + snd_filesize.length + snd_userid.length, snd_pass.length);
				System.arraycopy(snd_filenm,    0, bMd5, snd_type.length + snd_flag1.length + snd_flag2.length + snd_flag3.length + snd_filesize.length + snd_userid.length + snd_pass.length, snd_filenm.length);
				System.arraycopy(snd_reser,     0, bMd5, snd_type.length + snd_flag1.length + snd_flag2.length + snd_flag3.length + snd_filesize.length + snd_userid.length + snd_pass.length + snd_filenm.length, snd_reser.length);
				
				byte[] bMd5_Incode = new byte[16];
				System.arraycopy(CbUtil.Md5Sig(bMd5), 0, bMd5_Incode,0, 16);
				
				System.arraycopy(MsgHead.getBytes(),  0, resv_msg, 0, MsgHead.length());
				System.arraycopy(bMd5,                0, resv_msg, MsgHead.length(), bMd5.length);
				System.arraycopy(bMd5_Incode,         0, resv_msg, MsgHead.length() + bMd5.length, 16);
				
				/*���������� ����(��������)�� �����Ѵ�...*/
				log.debug("���� ���� �۽�...");
				client.sendData(resv_msg);
				
				/*���۰�� ������ ��ٸ��´�..30�ʱ���...*/
				byte[] recv  = client.recv(timeout);
				
				log.debug("=========================================");
				log.debug("���� ���� �۽ſ� ���� �������...");
				log.debug("=========================================");
				
				/* ���������м� : �ʱ�ȭ */
				KFL_2550 = new Txdm2550FieldList();
				
				cmMap = KFL_2550.parseBuff(recv);         /*���� �� 4byte�� �Ľ�...*/
				
				log.debug("cmMap = [" + cmMap.getMaps() + "]");
			
				if(cmMap.getMap("BLK_TYPE").equals("P")) { /*P_POS ����*/
				
					long p_ins = 0;
					
					do{
		
						/*�ٽ� ���ŵǴ� ��� �� �Ľ�...*/
						cmMap = KFL_2550.parseBuff(recv);  
						
						if(series_cnt > 0) {
							log.debug("=========================================");
							log.debug("���û�� ��� [" + series_cnt + "]");
							log.debug("cmMap = [" + cmMap.getMaps() + "]");
							log.debug("=========================================");
						}
						
						/*P�ΰ�� �����͸� �����Ѵ�...*/
						byte[] offset  = new byte[4];
						
						byte[] bContent = new byte[1024];   /*�ѹ������� �� �ִ� �ִ� ������� 1024*/
						
						System.arraycopy(recv, 12, offset, 0, offset.length);
						
						long lOffset = CbUtil.transLength(offset, 4);
						
						log.info("���ŵ� Offset = [" + lOffset + "]");
						
						StringBuffer sbLine = new StringBuffer();
						
						String s_yn = "";
						
						if(filesz > 1024) s_yn = "G"; else s_yn = "E";
						
						if(series_cnt > 0 && series_cnt % 8 == 0) {
							/*8���� ��Ŷ�� ������ ��� ���۾������� ���Ͽ� ACK�� �䱸�ϴ� E Mode �� ������...*/
							s_yn = "E";
							series_cnt = 0;
						}
						
						log.info("s_yn = [" + s_yn + "]");
						
						/*����) �ѱ��� �����Ƿ� ������ ������ ���̳ʸ��� �д´�...
						 *      �� BufferedReader() string �� char �� �д� ���� �����Ѵ�. */
						
						/*������ �о �������������� ������Ų��.*/
						FileInputStream fs = new FileInputStream(kf_snd_file_nm);
						/*========================================================*/
						/*������ ���� ��� offset ���̸�ŭ skip �ϰ� ���̸�ŭ �о����.*/
						fs.skip(lOffset);
				
						int readLen = fs.read(bContent);
						/*========================================================*/
						log.info("READ SIZE = [" +readLen + "]");
						
						fs.close();

						sbLine.append(new String(bContent));

	                    log.debug("���������� ���� = [" + sbLine.toString() + "]");
	                    
	                    long offset_infile = lOffset;
	                    long prt_filesize = (long)((int)(filesz - lOffset) > 1024 ? 1024 : (int)(filesz - lOffset));
	                    
	                    log.debug("���ϳ� offset = [" + offset_infile + "]");
	                    log.debug("���� block    = [" + prt_filesize + "]");
	                    
						byte[] f_type     = "D".getBytes();
						byte[] f_flag1    = s_yn.getBytes();
						byte[] f_flag2    = CbUtil.setMsgLen(prt_filesize);
						byte[] f_offset   = CbUtil.setOffset(offset_infile);
						byte[] f_data     = CbUtil.rPadByte(bContent, 1024);
						
						byte[] f_bMd5 = new byte[1032];
						
						System.arraycopy(f_type,       0, f_bMd5, 0, f_type.length);
						System.arraycopy(f_flag1,      0, f_bMd5, f_type.length, f_flag1.length);
						System.arraycopy(f_flag2,      0, f_bMd5, f_type.length + f_flag1.length, f_flag2.length);
						System.arraycopy(f_offset,     0, f_bMd5, f_type.length + f_flag1.length + f_flag2.length, f_offset.length);
						System.arraycopy(f_data,       0, f_bMd5, f_type.length + f_flag1.length + f_flag2.length + f_offset.length, f_data.length);
						
						byte[] f_bMd5_Incode = new byte[16];
						System.arraycopy(CbUtil.Md5Sig(f_bMd5), 0, f_bMd5_Incode,0, 16);
						
						resv_msg = new byte[1056];
						
						System.arraycopy(MsgHead.getBytes(),    0, resv_msg, 0, MsgHead.length());
						System.arraycopy(f_bMd5,                0, resv_msg, MsgHead.length(), f_bMd5.length);
						System.arraycopy(f_bMd5_Incode,         0, resv_msg, MsgHead.length() + f_bMd5.length, 16);
						
						/*���������� ����(��������)�� �����Ѵ�...*/
						client.sendData(resv_msg);
						
						if(s_yn.equals("G")) {
							
							p_ins += prt_filesize;
							
							System.arraycopy(CbUtil.setOffset(p_ins), 0, recv, 12, 4);
							
							if(filesz == p_ins) {
								//���������� ������ ���´ٰ� Ȯ����...
								//EOF�� �����Ѵ�...
								bMd5 = new byte[1032];
								
								System.arraycopy("X".getBytes(),        0, bMd5, 0, 1);
								System.arraycopy(CbUtil.nullByte(1),    0, bMd5, 1, 1);
								System.arraycopy(CbUtil.nullByte(1),    0, bMd5, 2, 1);
								System.arraycopy(CbUtil.nullByte(1029), 0, bMd5, 3, 1029);
								
								bMd5_Incode = new byte[16];
								
								System.arraycopy(CbUtil.Md5Sig(bMd5), 0, bMd5_Incode,0, 16);
							
								resv_msg = new byte[1056];
								
								System.arraycopy(MsgHead.getBytes(),    0, resv_msg, 0, MsgHead.length());
								System.arraycopy(bMd5,                  0, resv_msg, MsgHead.length(), bMd5.length);
								System.arraycopy(bMd5_Incode        ,   0, resv_msg, MsgHead.length() + bMd5.length, 16);
								
								/*�������� ���ῡ ���� �޼��� ����*/
								client.sendData(resv_msg);
								
								log.info("���ϼ۽� ��[" + kf_snd_file_nm + "]");
								
								break;
	
							} else {
								log.info("���ϼ۽� ���û :: [" + p_ins + "]");
							}

						} else {
							
							/*���۰�� ������ ��ٸ���...30�ʱ���...*/
							recv  = client.recv(timeout);
							
							/*���ۿ� �� ���������м�*/
							cmMap = KFL_2550.parseBuff(recv);         /*���� �� 4byte�� �Ľ�...*/
							
							log.debug("cmMap = " + cmMap.getMaps());
						}
						
						/*������ �ְ� �޴°�� ������ ����� 1024���� ū ���� �ְ�ް� ����ؾ� ��....*/
						
						if(cmMap.getMap("BLK_TYPE").equals("P")) {
							
							/*������ ���Ź��� ������ MD5 �����ؾ� �ϴµ� �ϴ� �����Ƽ� ���ϰ���...�̴��� ���� �˾Ƽ� �ϵ���...*/
							
							/*��������� ���� ������ �ٽ� ��û�� ��� �̹Ƿ� : ����ؼ� ������ �����Ѵ�...*/
							System.arraycopy(recv, 12, offset, 0, offset.length);
							lOffset = CbUtil.transLength(offset, 4);
							
							if(filesz == lOffset) {
								//���������� ������ ���´ٰ� Ȯ����...
								//EOF�� �����Ѵ�...
								bMd5 = new byte[1032];
								
								System.arraycopy("X".getBytes(),        0, bMd5, 0, 1);
								System.arraycopy(CbUtil.nullByte(1),    0, bMd5, 1, 1);
								System.arraycopy(CbUtil.nullByte(1),    0, bMd5, 2, 1);
								System.arraycopy(CbUtil.nullByte(1029), 0, bMd5, 3, 1029);
								
								bMd5_Incode = new byte[16];
								
								System.arraycopy(CbUtil.Md5Sig(bMd5), 0, bMd5_Incode,0, 16);
							
								resv_msg = new byte[1056];
								
								System.arraycopy(MsgHead.getBytes(),    0, resv_msg, 0, MsgHead.length());
								System.arraycopy(bMd5,                  0, resv_msg, MsgHead.length(), bMd5.length);
								System.arraycopy(bMd5_Incode        ,   0, resv_msg, MsgHead.length() + bMd5.length, 16);
								
								/*�������� ���ῡ ���� �޼��� ����*/
								client.sendData(resv_msg);
								
								log.info("���ϼ۽� ��[" + kf_snd_file_nm + "]");
								
								break;
								
								
							} else {
								log.info("���ϼ۽� ���û :: [" + lOffset + "]");
							}


						} else if(cmMap.getMap("BLK_TYPE").equals("X")) {
							
							/*���ϼ۽ſ� ���� ���ſϷ� �޼����� �����Ͽ����Ƿ� ��������*/

							bMd5 = new byte[1032];
							
							System.arraycopy("A".getBytes(),        0, bMd5, 0, 1);
							System.arraycopy(CbUtil.nullByte(1),    0, bMd5, 1, 1);
							System.arraycopy("X".getBytes(),        0, bMd5, 2, 1);
							System.arraycopy(CbUtil.nullByte(1029), 0, bMd5, 3, 1029);
							
							bMd5_Incode = new byte[16];
							
							System.arraycopy(CbUtil.Md5Sig(bMd5), 0, bMd5_Incode,0, 16);
						
							resv_msg = new byte[1056];
							
							System.arraycopy(MsgHead.getBytes(),    0, resv_msg, 0, MsgHead.length());
							System.arraycopy(bMd5,                  0, resv_msg, MsgHead.length(), bMd5.length);
							System.arraycopy(bMd5_Incode        ,   0, resv_msg, MsgHead.length() + bMd5.length, 16);
							
							/*�������� ���ῡ ���� �޼��� ����*/
							client.sendData(resv_msg);
							
							log.info("���ϼ۽� �Ϸ�[" + kf_snd_file_nm + "]");
							
							break;
						} else if(cmMap.getMap("BLK_TYPE").equals("C")) {
							
							
							KFL_2550.Msg_Can_FieldList();
							cmMap = KFL_2550.parseBuff(recv);  
							
							log.info(cmMap);
							
							break;
						}

						series_cnt ++;
						
					}while(true);
					
					
					
				} else if(cmMap.getMap("BLK_TYPE").equals("X")) {
					
					log.info("�������� = " + cmMap.getMap("BLK_TYPE") + " : EOF");
					
				} else if(cmMap.getMap("BLK_TYPE").equals("C")) {
					
					log.info("�������� = " + cmMap.getMap("BLK_TYPE") + " : CAN");
				}
		
			}
			
			if(isConnect) client.Disconnect();

			/*���������� ������ ���*/
			//CbUtil.copyFile(kf_snd_file_nm, kf_snd_file_nm);
			
			log.info("=====================kf_rev_file_transefer() ��=======================");
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
			try {
				if(isConnect) client.Disconnect();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
				
		}

		return true;
	}
	
	
	/*============================================
	 * FILE ���� �� �б�
	 *============================================
	 * */
	/**
	 * ���ϻ��� ���θ� üũ�ϰ� ������ �����ϸ� Append
	 * �׷��� ������ ������ ������Ų��.
	 */
	private RandomAccessFile getCurrentFile(String mkFileName, String file_attrib) throws Exception	{
		
		RandomAccessFile raFile = null;
		
		File file = new File(mkFileName);

		if(file.exists() && file_attrib.equals("append")) {
			if(raFile == null) {
				raFile = new RandomAccessFile(file, "rw");
			}
			raFile.seek(raFile.length());
		} else {
			if(raFile != null) {
				raFile.close();
			}
			
			if(file.exists()) {
				file.delete();
			}

			raFile = new RandomAccessFile(file, "rw");
		}

		return raFile;
	
    }
	
	/**
	 * �������� ���� ���ϵ����͸� ���Ͽ� ����Ѵ�.
	 * @param msg
	 * @param mkFileName
	 */
	private synchronized void makeFile(byte[] msg, String mkFileName, String file_attrib) 
	{
		try	{
		
			RandomAccessFile rafile = getCurrentFile(mkFileName, file_attrib);
			rafile.write(msg);
			rafile.close();
			
		} catch (Exception e) {
			log.error("���������� ����ϴµ� ������ �߻��߽��ϴ�");
			e.printStackTrace();
		}

	}
	
	
	/*��ġ���� ���*/
	public void regist_state_batch(String Gubun) {
		// TODO Auto-generated method stub
		
		MapForm sysMap = new MapForm();
		
		try {
			
			sysMap.setMap("SGG_COD"      , "000");                                /*��û�ڵ� : �λ��(626), ���̹���û(000)*/
			sysMap.setMap("PROCESS_ID"   , "2555");                               /*���μ��� ID*/
			sysMap.setMap("PROCESS_NM"   , "���ೳ�� ��������");                  /*���μ��� ��*/
			sysMap.setMap("THREAD_NM"    , "thr_2555");                           /*������ ��*/
			sysMap.setMap("BIGO"         , CbUtil.getServerIp());                 /*��� : ���༭��IP�� �����Ѵ�. */
			sysMap.setMap("MANE_STAT"    , "1");                                  /*�⵿����*/
			sysMap.setMap("MANAGE_CD"    , "2");                                  /*�������� 1 : ���� 2: ��ġ*/
			sysMap.setMap("STT_DT"       , CbUtil.getCurrent("yyyyMMddHHmmss"));  /*�����Ͻ� */
			sysMap.setMap("END_DT"       , "");                                   /*�����Ͻ�*/
			sysMap.setMap("REG_DTM"      , CbUtil.getCurrent("yyyyMMddHHmmss"));  /*����Ͻ� : ó���ѹ���...*/
			sysMap.setMap("LAST_DTM"     , CbUtil.getCurrent("yyyyMMddHHmmss"));  /*���������Ͻ�*/
			sysMap.setMap("LAST_TIMEMIL" , System.currentTimeMillis());
			
			/**
			 * ����������� ���
			 * TEST�� ��츸 ����
			 */
			
			if(Gubun.equals("S")) {
				
				try{
					if(sqlService_cyber.queryForUpdate("CODMBASE.CODMSYSTUpdate", sysMap) < 1) {
						sqlService_cyber.queryForInsert("CODMBASE.CODMSYSTInsert", sysMap);
					}
				}catch (Exception e) {
					e.printStackTrace();
				}
				
			} else if(Gubun.equals("E")){
				
				sysMap.setMap("END_DT"        , CbUtil.getCurrent("yyyyMMddHHmmss"));  /*�����Ͻ�*/
				sysMap.setMap("LAST_DTM"      , CbUtil.getCurrent("yyyyMMddHHmmss"));
				sysMap.setMap("LAST_TIMEMIL"  , System.currentTimeMillis());
				
				/*���� ������¸� ������Ʈ �Ѵ�...*/
				sqlService_cyber.queryForUpdate("CODMBASE.CODMSYSTUpdate", sysMap);
			}
				
			Thread.sleep(1000);
			
		
		} catch (Exception e) {
			
			e.printStackTrace();
			
		}
		
		log.info("================================================");
		log.info("== " + Thread.currentThread().getName() + " PROCESS Terminated!!");
		log.info("================================================");
	}	
	
}
