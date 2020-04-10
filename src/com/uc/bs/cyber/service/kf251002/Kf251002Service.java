/**
 *  �ֽý��۸� : ���̹����漼û ��ȭ
 *  ��  ��  �� : ���ϼ����������� ����ȸ 
 *  ��  ��  �� : ������-���̹���û 
 *               ����ó��
 *               ��������� ���ŵ� ������ ���ؼ� ������������Ʈ�� �����ϰ� 
 *               ��ȸ����� �����Ѵ�.
 *  Ŭ����  ID : Kf251002Service
 *  ����  �̷� :
 * ------------------------------------------------------------------------
 *  �ۼ���     �Ҽ�  		 ����      Tag   ����
 * ------------------------------------------------------------------------   
 * �ҵ���   ��ä��(��)    2011.06.13   %01%  �ű��ۼ�
 *  
 */
package com.uc.bs.cyber.service.kf251002;

import java.math.BigDecimal;
import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;

import com.uc.core.MapForm;
import com.uc.core.spring.service.IbatisBaseService;
import com.uc.bs.cyber.CbUtil;

/**
 * @author Administrator
 *
 */
public class Kf251002Service {

	protected Log log = LogFactory.getLog(this.getClass());
	
	private ApplicationContext appContext = null;
	
	private IbatisBaseService sqlService_cyber = null;
		
	private Kf251002FieldList kfField = null;
	
	CbUtil cUtil = null;
	
	/**
	 * ������
	 */
	public Kf251002Service(ApplicationContext appContext) {
		// TODO Auto-generated constructor stub
		
		setAppContext(appContext);
		
		kfField = new Kf251002FieldList();
		
	}

	/* appContext property ���� */
	public void setAppContext(ApplicationContext appContext) {
		
		this.appContext = appContext;
		
		sqlService_cyber = (IbatisBaseService) this.appContext.getBean("baseService_cyber");

	}

	public ApplicationContext getAppContext() {
		return appContext;
	}
	
	/**
	 * ���ϼ����������� �������� ����ȸ ó��...
	 * */
	public byte[] chk_kf_251002(MapForm headMap, MapForm mf) throws Exception {
		
				
		MapForm sendMap = new MapForm();
		
		/*���ϼ��� ����������ȸ �� ��� */
		String resCode = "000";       /*�����ڵ�*/
		String giro_no = "1004102";   /*�λ�������ڵ�*/
		String sg_code = "26";        /*�λ�ñ���ڵ�*/
		String napkiGubun = "";

		/*�ʿ亯����*/
		String icheno = "";
		String custno = "";
		
		try{
			cUtil = CbUtil.getInstance();
			
			log.info(cUtil.msgPrint("2", "S","KP251002 chk_kf_251002()", resCode));
			
			/*����üũ*/
			
			/*�������з��ڵ� ����*/
			if(!headMap.getMap("GPUB_CODE").equals(sg_code)){
				resCode = "339";  
			}
			/*���ι�ȣ ����*/
			if(!headMap.getMap("GJIRO_NO").equals(giro_no)){
				resCode = "324";
			}
						
			/*��ȸ���а��� ���� ��ȸ������ �Է�*/
			if (mf.getMap("SEARCHGUBUN").equals("S")) {  /*���밡��ȣ�� ����ؼ� ��ȸ��...*/
				
				if(mf.getMap("SEARCHKEY").toString().length() != 30) {
					
					resCode = "311";   /*�������� ����*/
				} else {
					
					String searchky = cUtil.waterChgCustno("S", mf.getMap("SEARCHKEY").toString().substring(20,29));
					
					if(searchky.equals(mf.getMap("SEARCHKEY").toString())) {
						
						custno = mf.getMap("SEARCHKEY").toString().substring(20, 29); /*���ڳ��ι�ȣ�� �����ؾ� ��.. : ������ �׷���*/
						
						log.info("SEARCHGUBUN = [" + mf.getMap("SEARCHGUBUN") + "]");
						log.info("custno = [" + custno + "] - ���밡��ȣ");
						
					} else {
						
						resCode = "311";   /*�������� ����*/
					}
					
				}
				
				/*���ڳ��ι�ȣ�� ��츸 : ���밡��ȣ�� ���� �ʿ����...*/
				mf.setMap("TAX_YY" , mf.getMap("BYYYMM").toString().substring(0, 4));
				mf.setMap("TAX_MM" , mf.getMap("BYYYMM").toString().substring(4));
				
				
			} else if (mf.getMap("SEARCHGUBUN").equals("E")) { /*���ڳ��ι�ȣ�� ����ؼ� ��ȸ��...*/
				
				icheno = mf.getMap("SEARCHKEY").toString();   /*���밡��ȣ�� ����*/
				
				log.info("SEARCHGUBUN = [" + mf.getMap("SEARCHGUBUN") + "]");
				log.info("icheno = [" + icheno + "] - ���ڳ��ι�ȣ");
								
			} else {
				
				resCode = "341";   /*[341] ������(���ͳ�)���ι�ȣ Ʋ��*/
			}
			
			log.debug("custno = " + custno);
			
			/*�������� ���� ���� ����*/
			mf.setMap("CUST_NO", custno);
			mf.setMap("EPAY_NO", icheno);
			
			int levyCnt = 0;
		
			if(resCode.equals("000")) {
		
				if(custno.length() == 9 || icheno.length() == 17) {
					
					ArrayList<MapForm>  kfCmd251002List  =  sqlService_cyber.queryForList("KF251001.SELECT_SHSD_LEVY_LIST", mf);
					
					levyCnt = kfCmd251002List.size();   /*�����ѰǼ�*/
					
					if(levyCnt <= 0){
						resCode = "311";   /*��ȸ���� ����*/
					} else if ( levyCnt > 1 ) {
						resCode = "094";   /*�����Ǽ�2���̻�*/
					} else {
						
						resCode = "000";
						
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
					    String SNAP_BANK_CODE       = "";       // �������� �ڵ�
					    String SNAP_SYMD            = "";       // ��������
					    String NAPGUBUN             = "";       // ���⳻�ı����ڵ�
					    String ETC1                 = "";       // �����ʵ�1
					    String CUST_ADMIN_NUM       = "";       // ��������ȣ
					    String OCR                  = "";       // OCR

						MapForm mpCmd251002List = kfCmd251002List.get(0);

						String Epay_no = (String) mpCmd251002List.getMap("EPAY_NO");  /*���ڳ��ι�ȣ*/
						String Cust_no = (String) mpCmd251002List.getMap("CUST_NO");  /*���밡��ȣ*/
						
						String dang = mf.getMap("DANGGUBUN").toString();
						
						if (dang.equals("")) {
							dang   = (String) mpCmd251002List.getMap("GUBUN");
						}
						
		                //�������� SET
		                BNAPDATE = (String) mpCmd251002List.getMap("DUE_DT");         //���⳻ ������
		                ANAPDATE = (String) mpCmd251002List.getMap("DUE_F_DT");       //������ ������
						
		                ADDR     = (String) mpCmd251002List.getMap("ADDRESS");        //�ּ�  
		                OCR      = (String) mpCmd251002List.getMap("OCR_1") + (String) mpCmd251002List.getMap("OCR_2");
		                
		                //���⳻(B), ������(A) üũ
		                napkiGubun = cUtil.getNapGubun(BNAPDATE, ANAPDATE);

		                NAPGUBUN = napkiGubun;
		                
						if(dang.equals("1") || dang.equals("3")) {
							
							if(log.isDebugEnabled()){
								log.debug("����/���� Starting");
							}

							
			                //�����Ѿ� SET
			                BNAPAMT  = ((BigDecimal)mpCmd251002List.getMap("SUM_B_AMT")).longValue();         //���⳻ �ݾ�
			                ANAPAMT  = ((BigDecimal)mpCmd251002List.getMap("SUM_F_AMT")).longValue();         //������ �ݾ�
							
			                GUM2 = "";
			                
			                //���μ��αݾ� SET
			                BSAMT    = ((BigDecimal)mpCmd251002List.getMap("FEE_AMT1")).longValue();         //���⳻ ������ݾ�
			                BHAMT    = ((BigDecimal)mpCmd251002List.getMap("FEE_AMT2")).longValue();         //���⳻ �ϼ����ݾ�
			                BGAMT    = ((BigDecimal)mpCmd251002List.getMap("FEE_AMT4")).longValue();         //���⳻ ���ϼ��ݾ�
			                BMAMT    = ((BigDecimal)mpCmd251002List.getMap("FEE_AMT3")).longValue();         //���⳻ ���̿�ݾ�
			                ASAMT    = ((BigDecimal)mpCmd251002List.getMap("FEE_AMT1_H")).longValue();         //������ ������ݾ�
			                AHAMT    = ((BigDecimal)mpCmd251002List.getMap("FEE_AMT2_H")).longValue();         //������ �ϼ����ݾ�
			                AGAMT    = ((BigDecimal)mpCmd251002List.getMap("FEE_AMT4_H")).longValue();         //������ ���ϼ��ݾ�
			                AMAMT    = ((BigDecimal)mpCmd251002List.getMap("FEE_AMT3_H")).longValue();         //������ ���̿�ݾ�

			                GUM3 = "";

			                CNAPTERM = "0";  //ü���Ⱓ

			                USETERM  = (String) mpCmd251002List.getMap("USE_STT_DATE") + (String) mpCmd251002List.getMap("USE_END_DATE");          //���Ⱓ

			                AUTOREG  = "N";      //�ڵ���ü ��Ͽ���


			                /*
			                 * �۽������� ��������ȣ ���� ��
			                 * S ���밡��ȣ�� ��ȸ�� ��� : ���ڳ��ι�ȣ
			                 * E ���ڳ��ι�ȣ�� ��ȸ�� ��� : ���밡��ȣ�� �����ؼ� �����ش�.
			                 * */
			                if (mf.getMap("SEARCHGUBUN").equals("S")) {
			                	
			                	/*��������ȣ : S : ���ڳ��ι�ȣ ���� */
			                	CUST_ADMIN_NUM = Epay_no;
			                	
			                } else if (mf.getMap("SEARCHGUBUN").equals("E")) {
			                	
			                	/*��������ȣ : E : ���밡��ȣ */
			                	CUST_ADMIN_NUM = cUtil.waterChgCustno("S", Cust_no); 
			                    
			                } else {
			                	
			                	resCode = "094";
			                }
							
						} else if (dang.equals("2")){
							
							if(log.isDebugEnabled()){
								log.debug("ü�� Starting");
							}
							
							
							//�������� SET
			                //���⳻�ΰ�� ���⳻ ���� SET, �������� ��� ������ ���� SET
			                //���⳻(B), ������(A) üũ
							if(NAPGUBUN.equals("B"))		                {
			                    BNAPDATE = (String) mpCmd251002List.getMap("DUE_DT");         //������ ������
			                    ANAPDATE = BNAPDATE;
			                } else  {
			                    BNAPDATE = (String) mpCmd251002List.getMap("DUE_F_DT");       //������ ������
			                    ANAPDATE = BNAPDATE;
			                }
							
							//ü�����ΰ�� ������ ���⳻�������� ���α��� ����
			                NAPGUBUN = "B";

			                //�����Ѿ� SET
			                BNAPAMT  = ((BigDecimal)mpCmd251002List.getMap("SUM_B_AMT")).longValue();          //���⳻ �ݾ�
			                ANAPAMT  = 0;

			                GUM2 = "";

			                //���μ��αݾ� SET
			                BSAMT    = ((BigDecimal)mpCmd251002List.getMap("FEE_AMT1")).longValue();         //���⳻ ������ݾ�
			                BHAMT    = ((BigDecimal)mpCmd251002List.getMap("FEE_AMT2")).longValue();         //���⳻ �ϼ����ݾ�
			                BGAMT    = ((BigDecimal)mpCmd251002List.getMap("FEE_AMT4")).longValue();         //���⳻ ���ϼ��ݾ�
			                BMAMT    = ((BigDecimal)mpCmd251002List.getMap("FEE_AMT3")).longValue();         //���⳻ ���̿�ݾ�
			                ASAMT    = 0;
			                AHAMT    = 0;
			                AGAMT    = 0;
			                AMAMT    = 0;

			                GUM3 = "";

			                CNAPTERM = (String) mpCmd251002List.getMap("NOT_STT_DATE") + (String) mpCmd251002List.getMap("NOT_END_DATE");          //ü���Ⱓ
			                USETERM = CNAPTERM;          //���Ⱓ

			                  

			                AUTOREG = "N";      //�ڵ���ü ��Ͽ���
							
			                if(mf.getMap("SEARCHGUBUN").equals("S")) 
			                {
			                    CUST_ADMIN_NUM = Epay_no;
			                }
			                else if(mf.getMap("SEARCHGUBUN").equals("E")) 
			                {
			                    CUST_ADMIN_NUM = cUtil.waterChgCustno("S", Cust_no); 
			                }
			                else
			                {
			                	resCode = "094";        // Error : ���⳻�İ� ���� ����
			                }
							
						}
		
						sendMap.setMap("SEARCHGUBUN"        , mf.getMap("SEARCHGUBUN"));                /*��ȸ ����('S'/'E')                                */
						sendMap.setMap("SEARCHKEY"          , mf.getMap("SEARCHKEY"));                  /*���밡��ȣ/���ڳ��ι�ȣ                           */
						sendMap.setMap("BYYYMM"             , (String)mpCmd251002List.getMap("TAX_YY") + (String)mpCmd251002List.getMap("TAX_MM")); /*�ΰ� ���*/
						sendMap.setMap("DANGGUBUN"          , dang);                                    /*��� ����                                         */
						sendMap.setMap("NPNO"               , mpCmd251002List.getMap("PRT_NPNO").toString());      /*���� ��ȣ                              */
						sendMap.setMap("NAME"               , mpCmd251002List.getMap("REG_NM"));        /*����                                              */
						sendMap.setMap("BNAPDATE"           , BNAPDATE);                                /*���⳻ ������              | ���� ������          */
						sendMap.setMap("BNAPAMT"            , BNAPAMT);                                 /*���⳻ �ݾ�                | ü����               */
						sendMap.setMap("ANAPAMT"            , ANAPAMT);                                 /*������ �ݾ�                                       */
						sendMap.setMap("GUM2"               , GUM2);                                    /*������ȣ 2                                        */
						sendMap.setMap("BSAMT"              , BSAMT);                                   /*��������⳻�ݾ�           |  ����� ü����       */
						sendMap.setMap("BHAMT"              , BHAMT);                                   /*�ϼ������⳻�ݾ�           |  �ϼ��� ü����       */
						sendMap.setMap("BGAMT"              , BGAMT);                                   /*���ϼ����⳻�ݾ�           |  ���ϼ� ü����       */
						sendMap.setMap("BMAMT"              , BMAMT);                                   /*���̿�δ�ݳ��⳻�ݾ�     |  ���̿�δ��ü����  */
						sendMap.setMap("ASAMT"              , ASAMT);                                   /*����������ıݾ�                                  */
						sendMap.setMap("AHAMT"              , AHAMT);                                   /*�ϼ��������ıݾ�                                  */
						sendMap.setMap("AGAMT"              , AGAMT);                                   /*���ϼ������ıݾ�                                  */
						sendMap.setMap("AMAMT"              , AMAMT);                                   /*���̿�δ�ݳ����ıݾ�                            */
						sendMap.setMap("ANAPDATE"           , ANAPDATE);                                /*������ ������                                     */
						sendMap.setMap("GUM3"               , GUM3);                                    /*������ȣ 3                                        */
						sendMap.setMap("CNAPTERM"           , CNAPTERM);                                /*ü�� �Ⱓ                                         */
						sendMap.setMap("ADDR"               , ADDR);                                    /*�ּ�                                              */
						sendMap.setMap("USETERM"            , USETERM);                                 /*��� �Ⱓ                                         */
						sendMap.setMap("AUTOREG"            , AUTOREG);                                 /*�ڵ���ü ��� ����                                */
						sendMap.setMap("SNAP_BANK_CODE"     , SNAP_BANK_CODE);                          /*�������� ���� �ڵ�                                */
						sendMap.setMap("SNAP_SYMD"          , SNAP_SYMD);                               /*���� �Ͻ�                                         */
						sendMap.setMap("NAPGUBUN"           , NAPGUBUN);                                /*���� ���� ����                                    */
						sendMap.setMap("ETC1"               , ETC1);                                    /*���� ���� FIELD                                   */
						sendMap.setMap("CUST_ADMIN_NUM"     , CUST_ADMIN_NUM);                          /*��������ȣ                                      */
						sendMap.setMap("OCR"                , OCR);                                     /*OCR BAND                                          */
						
					}
					
					
				} else {
					resCode = "341";  /*���ڳ��ι�ȣ, ���밡��ȣ ����*/
				}
				
			}

			log.info(cUtil.msgPrint("2", "","KP251002 chk_kf_251002()", resCode));
			
        } catch (Exception e) {
			
        	resCode = "093";  //Error : ���ڳ��ι�ȣ, ���밡��ȣ ����
        	
			e.printStackTrace();
			log.error("============================================");
			log.error("== chk_kf_251002 Exception(�ý���) ");
			log.error("============================================");
		}
        
        /*���⼭ ������带 �����Ѵ�. �ٲ�� �κи� �ٽ� �����ؼ� ������ ����*/
        headMap.setMap("RS_FLAG"   , "G");         /*�����̿���(G) */
        headMap.setMap("RESP_CODE" , resCode);     /*�����ڵ�*/
        headMap.setMap("GCJ_NO"    , "0" + sg_code + "0" + CbUtil.lPadString(String.valueOf(SeqNumber()), 8, '0'));     /*�̿��� �Ϸù�ȣ*/
        headMap.setMap("TX_GUBUN"  , "0210");
        
        return kfField.makeSendBuffer(headMap, sendMap);
	}

	/*----------------------------------------------*/
	/* �Ϸù�ȣ �������� �׽�Ʈ*/
	/*----------------------------------------------*/
	private int SeqNumber(){
    	return (Integer)sqlService_cyber.queryForBean("CODMBASE.CODM_GETSEQNO", "00");
    }
}
