/**
 *  �ֽý��۸� : ���̹����漼û ��ȭ
 *  ��  ��  �� : ���ϼ����������� ����ȸ 
 *  ��  ��  �� : ������(����)-���̹���û 
 *               ����ó��
 *               ������(����)���� ���ŵ� ������ ���ؼ� ������������Ʈ�� �����ϰ� 
 *               ��ȸ����� �����Ѵ�.
 *  Ŭ����  ID : Df251002Service
 *  ����  �̷� :
 * ------------------------------------------------------------------------
 *  �ۼ���     �Ҽ�  		 ����      Tag   ����
 * ------------------------------------------------------------------------   
 * ��â��   ��ä��(��)    2013.09.13   %01%  ���� 
 */
package com.uc.bs.cyber.service.df251002;

import java.math.BigDecimal;
import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;

import com.uc.core.MapForm;
import com.uc.core.spring.service.IbatisBaseService;
import com.uc.bs.cyber.CbUtil;
import com.uc.bs.cyber.service.df201002.Df201002FieldList;

/**
 * @author Administrator
 *
 */
public class Df251002Service {

	protected Log log = LogFactory.getLog(this.getClass());
	
	private ApplicationContext appContext = null;
	
	private IbatisBaseService sqlService_cyber = null;
		
	private Df201002FieldList dfField = null;
	
	CbUtil cUtil = null;
	
	/**
	 * ������
	 */
	public Df251002Service(ApplicationContext appContext) {
		// TODO Auto-generated constructor stub
		
		setAppContext(appContext);
		
		dfField = new Df201002FieldList();
		
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
	public byte[] chk_df_251002(MapForm headMap, MapForm mf) throws Exception {
		
				
		MapForm sendMap = new MapForm();
		
		/*���ϼ��� ����������ȸ �� ��� */
		String resCode = "0000";      /*�����ڵ�*/
		String tx_id = "SN2601";      /*�λ���ڵ�*/
		String sg_code = "26";        /*�λ�ñ���ڵ�*/
		String napkiGubun = "";
		
		try{
			cUtil = CbUtil.getInstance();
			
			log.info(cUtil.msgPrint("2", "S","DP251002 chk_df_251002()", resCode));
			
			/*����üũ*/
			
			/*�������з��ڵ� ����*/
			if(!headMap.getMap("SIDO_CODE").equals(sg_code)){
                resCode = "1000";
			}
			/*���ι�ȣ ����*/
			if(!headMap.getMap("TX_ID").equals(tx_id)){
				resCode = "1000";
			}

            mf.setMap("PRT_NO"     , CbUtil.getCurrent("yyyyMMdd").substring(0, 2) + mf.getMap("TAX_NO").toString().substring(0, 12));

            log.debug("PRT_NO = [" + mf.getMap("PRT_NO") + "]");
			
            /* ���γ��� ���翩�� Ȯ�� ��ȸ */
            ArrayList<MapForm>  dfAl251002List  =  sqlService_cyber.queryForList("DF251002.SELECT_SHSD_PAY_LIST", mf);
            
            int payCnt = dfAl251002List.size(); /*�����ѰǼ�*/
            
            log.debug("payCnt = " + payCnt);
            
            if(payCnt == 0) {
                resCode = "0000";   /*���γ�������*/
            } else {
                /*���γ��� ����*/
                MapForm mfCmdEpayList = dfAl251002List.get(0);
                
                if ( mfCmdEpayList == null || mfCmdEpayList.isEmpty() ) {
                    /*���γ�������*/
                    resCode = "0000";
                } else {
                    /*���γ����� �ִ� ���*/
                    if(mfCmdEpayList.getMap("SNTG").equals("9")) {  /*���*/
                        resCode = "0000";
                    } else {
                        resCode = "5000";  /*���α����*/
                    }
                }
            }
            
			int levyCnt = 0;
		
			if(resCode.equals("0000")) {
			    
			    if(mf.getMap("EPAY_NO").toString().length() == 17) {
	                mf.setMap("EPAY_NO", mf.getMap("EPAY_NO").toString() + "00");
	            }
		
				if(mf.getMap("EPAY_NO").toString().length() == 19) {
					
					ArrayList<MapForm>  dfCmd251002List  =  sqlService_cyber.queryForList("DF251002.SELECT_SHSD_LEVY_LIST", mf);
					
					levyCnt = dfCmd251002List.size();   /*�����ѰǼ�*/
					
					if(levyCnt <= 0){
						resCode = "5020";   /*��ȸ���� ����*/
					} else if ( levyCnt > 1 ) {
						resCode = "5060";   /*�����Ǽ�2���̻�*/
					} else {
						
						resCode = "0000";

						//����ó�� ��������
					    String BNAPDATE             = "";       // ���⳻ ������
					    long BNAPAMT                = 0 ;       // ���⳻ �ݾ�
					    long ANAPAMT                = 0 ;       // ������ �ݾ�
					    long NAPAMT                 = 0 ;       // �������� �ؾ��� �ݾ�
					    String ANAPDATE             = "";       // ������ ������
					    String CNAPTERM             = "";       // ü���Ⱓ
					    String ADDR                 = "";       // �ּ�
					    String USETERM              = "";       // ���Ⱓ
					    String AUTOREG              = "";       // �ڵ���ü��Ͽ���
//					    String SNAP_BANK_CODE       = "";       // �������� �ڵ�
//					    String SNAP_SYMD            = "";       // ��������
					    String NAPGUBUN             = "";       // ���⳻�ı����ڵ�
					    String SGG_NM               = "���������";
					    
					    String CURDATE = (String) CbUtil.getCurrentDate().substring(0, 8);
					    log.debug("CURDATE = " + CURDATE );
						
					    MapForm mpCmd251002List = dfCmd251002List.get(0);

						//�������� SET
                        BNAPDATE = (String) mpCmd251002List.getMap("DUE_DT");         //���⳻ ������
                        ANAPDATE = (String) mpCmd251002List.getMap("DUE_F_DT");       //������ ������
                        
                        //��ȸ���ڿ� ���Ͽ� ������ ������ �̳� �ڷḸ ó���Ѵ�.
                        if (CbUtil.getInt(CURDATE) > CbUtil.getInt(ANAPDATE)) {
                            resCode = "5010";   //�������ڰ� ���� ������
                        } else {
                            
    						String dang   = (String) mpCmd251002List.getMap("GUBUN");    //�������
    						String prt_gb   = (String) mpCmd251002List.getMap("PRT_GB"); //��������
						
    //		                ADDR     = (String) mpCmd251002List.getMap("ADDRESS");        //�ּ�  
    //		                OCR      = (String) mpCmd251002List.getMap("OCR_1") + (String) mpCmd251002List.getMap("OCR_2");
    		                
    		                //���⳻(B), ������(A) üũ
    		                napkiGubun = cUtil.getNapGubun(BNAPDATE, ANAPDATE);
    		                
    		                log.debug("napkiGubun = " + napkiGubun );
    
    		                NAPGUBUN = napkiGubun;
    		                
    						if(dang.equals("1") || dang.equals("3")) {
    							
    							if(log.isDebugEnabled()){
    								log.debug("����/���� Starting");
    							}
    							
    			                //�����Ѿ� SET
    			                BNAPAMT  = ((BigDecimal)mpCmd251002List.getMap("SUM_B_AMT")).longValue();         //���⳻ �ݾ�
    			                ANAPAMT  = ((BigDecimal)mpCmd251002List.getMap("SUM_F_AMT")).longValue();         //������ �ݾ�
    							
    			                /* ���� �����ؾ��� �ݾ�*/
                                if ("B".equals(NAPGUBUN)) {
                                    if ("2".equals(prt_gb)) {
                                        resCode = "7000";   //�ڵ���ü ���δ�� �ڷ��Դϴ�.
                                        NAPAMT = BNAPAMT;
                                    } else {
                                        NAPAMT = BNAPAMT;
                                    }
                                } else {
                                    NAPAMT = ANAPAMT;
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
    			                BNAPAMT  = ((BigDecimal)mpCmd251002List.getMap("SUM_B_AMT")).longValue();       //���⳻ �ݾ�
    			                ANAPAMT  = 0;
    			                NAPAMT   = BNAPAMT;
    			                CNAPTERM = (String) mpCmd251002List.getMap("NOT_STT_DATE") + " ~ " + (String) mpCmd251002List.getMap("NOT_END_DATE");          //ü���Ⱓ
    			                USETERM = CNAPTERM;          //���Ⱓ
    
    			                AUTOREG = "N";      //�ڵ���ü ��Ͽ���
    							
    						}
    						
    						String KIBUN = "";
    						
    						if(dang.equals("1")) {
                                KIBUN = "1";
                            }else if (dang.equals("2")) {
                                KIBUN = "0";
    						}else if (dang.equals("3")) {
    						    KIBUN = "2";
    						}
    						
    						String napName = (String) mpCmd251002List.getMap("REG_NM") +"  "+ (String) mpCmd251002List.getMap("ADDRESS");
    						/* �����ڸ��� 80����Ʈ�� ������ ��� ���ڰ� �ѱ��� ��� 40�� ���Ϸ� �ڸ��� */
                            if(napName.length() > 0)
                                sendMap.setMap("NAP_NAME"           , CbUtil.ksubstr(napName, 40));
                            else 
                                sendMap.setMap("NAP_NAME"           , napName);
                            
    						sendMap.setMap("TAX_GB"             , mf.getMap("TAX_GB"));  /*���� ����(3:�����)*/
    						sendMap.setMap("TAX_NO"             , mf.getMap("TAX_NO"));  /*���ι�ȣ             */
    						sendMap.setMap("EPAY_NO"            , mf.getMap("EPAY_NO")); /*���밡��ȣ/���ڳ��ι�ȣ */
    //						sendMap.setMap("NAP_NAME"           , mpCmd251002List.getMap("REG_NM") +"  "+ mpCmd251002List.getMap("ADDRESS").toString());        /*���� + �ּ� */
    						sendMap.setMap("SGG_NAME"           , SGG_NM);  //CbUtil.k2u("���������")
    						sendMap.setMap("TAX_NAME"           , mpCmd251002List.getMap("TAX_NOTICE_TITLE"));
    						sendMap.setMap("TAX_YM"             , (String)mpCmd251002List.getMap("TAX_YY") + (String)mpCmd251002List.getMap("TAX_MM")); /*�ΰ� ���*/
    						sendMap.setMap("TAX_DIV"            , KIBUN);     /*���*/
    						sendMap.setMap("NAP_BFDATE"         , BNAPDATE);  /*���⳻ ������   | ���� ������ */
    						sendMap.setMap("NAP_BFAMT"          , BNAPAMT);   /*���⳻ �ݾ�     | ü����      */
    						sendMap.setMap("NAP_AFDATE"         , ANAPDATE);  /*������ ������*/
    						sendMap.setMap("NAP_AFAMT"          , ANAPAMT);   /*������ �ݾ�*/
    						sendMap.setMap("NAP_AMT"            , NAPAMT);    /*���� �����ؾ��� �ݾ�*/
    						sendMap.setMap("TAX_DESC"           , USETERM);   /*���� ����*/
    						sendMap.setMap("FILLER1"            , " ");       /*      */
		                }
						
					}
					
				} else {
					resCode = "2000";  /*���ڳ��ι�ȣ, ���밡��ȣ ����*/
				}
				
			}

			log.info(cUtil.msgPrint("2", "","DP251002 chk_df_251002()", resCode));
			
        } catch (Exception e) {
			
        	resCode = "9090";  //Error : ���ڳ��ι�ȣ, ���밡��ȣ ����
        	
			e.printStackTrace();
			log.error("============================================");
			log.error("== chk_df_251002 Exception(�ý���) ");
			log.error("============================================");
		}
        
        /*���⼭ ������带 �����Ѵ�. �ٲ�� �κи� �ٽ� �����ؼ� ������ ����*/
        headMap.setMap("RESP_CODE" , resCode);     /*�����ڵ�*/
        headMap.setMap("TX_GUBUN"  , "040");
        
        return dfField.makeSendBuffer(headMap, sendMap);
	}

	/*----------------------------------------------*/
	/* �Ϸù�ȣ �������� �׽�Ʈ*/
	/*----------------------------------------------*/
	private int SeqNumber(){
    	return (Integer)sqlService_cyber.queryForBean("CODMBASE.CODM_GETSEQNO", "00");
    }
}
