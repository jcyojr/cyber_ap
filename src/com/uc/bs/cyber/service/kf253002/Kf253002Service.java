/**
 *  �ֽý��۸� : ���̹����漼û ��ȭ
 *  ��  ��  �� : ���ϼ������γ��� ����ȸ 
 *  ��  ��  �� : ������-���̹���û 
 *               ����ó��
 *               ��������� ���ŵ� ������ ���ؼ� ������������Ʈ�� �����ϰ� 
 *               ��ȸ����� �۽��Ѵ�.
 *  Ŭ����  ID : Kf253002Service
 *  ����  �̷� :
 * ------------------------------------------------------------------------
 *  �ۼ���     �Ҽ�  		 ����      Tag   ����
 * ------------------------------------------------------------------------   
 * �ҵ���   ��ä��(��)    2011.06.13   %01%  �ű��ۼ�
 *  
 */
package com.uc.bs.cyber.service.kf253002;

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
public class Kf253002Service {

	protected Log log = LogFactory.getLog(this.getClass());
	
	private ApplicationContext appContext = null;
	
	private IbatisBaseService sqlService_cyber = null;
		
	private Kf253002FieldList kfField = null;
	
	CbUtil cUtil = null;
	
	/**
	 * ������
	 */
	public Kf253002Service(ApplicationContext appContext) {
		// TODO Auto-generated constructor stub
		
		setAppContext(appContext);
		
		kfField = new Kf253002FieldList();
		
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
	 * ���ϼ����������� ���γ��� ����ȸ ó��...
	 * */
	public byte[] chk_kf_253002(MapForm headMap, MapForm mf) throws Exception {
		
				
		MapForm sendMap = new MapForm();
		
		/*���ϼ��� ���γ�����ȸ �� ��� */
		String resCode = "000";       /*�����ڵ�*/
		String giro_no = "1004102";   /*�λ�������ڵ�*/
		String sg_code = "26";        /*�λ�ñ���ڵ�*/
		String napkiGubun = "";
		
		try{
			cUtil = CbUtil.getInstance();
			
			log.info(cUtil.msgPrint("5", "S","KP253002 chk_kf_253002()", resCode));
			
			/*����üũ*/
			
			/*�������з��ڵ� ����*/
			if(!headMap.getMap("GPUB_CODE").equals(sg_code)){
				resCode = "339";  
			}
			/*���ι�ȣ ����*/
			if(!headMap.getMap("GJIRO_NO").equals(giro_no)){
				resCode = "324";
			}
						
			/*
			 * �������� ��ȸ����
			 * �������ֹι�ȣ(������)�ֳĸ� ���̹����忡 ���⶧��
			 * ���밡��ȣ
			 * �ΰ����
			 * �������
			 * */
			
			if(log.isDebugEnabled()) {
				log.debug("JUMINNO = [" + mf.getMap("JUMINNO").toString() + "]");
				log.debug("CUSTNO = [" + mf.getMap("CUSTNO").toString() + "]");
				log.debug("BYYYMM = [" + mf.getMap("BYYYMM").toString() + "]");
				log.debug("DANGGUBUN = [" + mf.getMap("DANGGUBUN").toString() + "]");
			}
			
			/*���γ����� ��ȸ������ �����Ƿ� �ִ°��� �״�� �����.*/
			
			/*�������� ���� ���� ����*/
			mf.setMap("CUST_NO" , mf.getMap("CUSTNO").toString().substring(20,29));
			mf.setMap("TAX_YY"  , mf.getMap("BYYYMM").toString().substring(0, 4));
			mf.setMap("TAX_MM"  , mf.getMap("BYYYMM").toString().substring(4));
			
			int levyCnt = 0;
			
			if(mf.getMap("CUSTNO").toString().length() == 9 ) {
				
				ArrayList<MapForm>  kfCmd253002List  =  sqlService_cyber.queryForList("KF253002.SELECT_SHSD_KFPAY_DETAIL_LIST", mf);
				
				levyCnt = kfCmd253002List.size();   /*�����ѰǼ�*/
				
				if(levyCnt <= 0){
					resCode = "312";   /*��ȸ���� ����*/
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
				    String NAPGUBUN             = "";       // ���⳻�ı����ڵ�

					MapForm mpCmd253002List = kfCmd253002List.get(0);

					String dang = mf.getMap("DANGGUBUN").toString();
					
					if (dang.equals("")) {
						dang   =  mpCmd253002List.getMap("GUBUN").toString().substring(1);
					}
					
	                //�������� SET
	                BNAPDATE = (String) mpCmd253002List.getMap("DUE_DT");         //���⳻ ������
	                ANAPDATE = (String) mpCmd253002List.getMap("DUE_F_DT");       //������ ������
					
	                ADDR     = (String) mpCmd253002List.getMap("ADDRESS");        //�ּ�  
	                
	                //���⳻(B), ������(A) üũ
	                napkiGubun = cUtil.getNapGubun(BNAPDATE, ANAPDATE);

	                NAPGUBUN = napkiGubun;
	                
					if(dang.equals("1") || dang.equals("3")) {
						
						if(log.isDebugEnabled()){
							log.debug("����/���� Starting");
						}

						
		                //�����Ѿ� SET
		                BNAPAMT  = ((BigDecimal)mpCmd253002List.getMap("SUM_B_AMT")).longValue();         //���⳻ �ݾ�
		                ANAPAMT  = ((BigDecimal)mpCmd253002List.getMap("SUM_F_AMT")).longValue();         //������ �ݾ�
						
		                GUM2 = "";
		                
		                //���μ��αݾ� SET
		                BSAMT    = ((BigDecimal)mpCmd253002List.getMap("FEE_AMT1")).longValue();         //���⳻ ������ݾ�
		                BHAMT    = ((BigDecimal)mpCmd253002List.getMap("FEE_AMT2")).longValue();         //���⳻ �ϼ����ݾ�
		                BGAMT    = ((BigDecimal)mpCmd253002List.getMap("FEE_AMT4")).longValue();         //���⳻ ���ϼ��ݾ�
		                BMAMT    = ((BigDecimal)mpCmd253002List.getMap("FEE_AMT3")).longValue();         //���⳻ ���̿�ݾ�
		                ASAMT    = ((BigDecimal)mpCmd253002List.getMap("FEE_AMT1_H")).longValue();         //������ ������ݾ�
		                AHAMT    = ((BigDecimal)mpCmd253002List.getMap("FEE_AMT2_H")).longValue();         //������ �ϼ����ݾ�
		                AGAMT    = ((BigDecimal)mpCmd253002List.getMap("FEE_AMT4_H")).longValue();         //������ ���ϼ��ݾ�
		                AMAMT    = ((BigDecimal)mpCmd253002List.getMap("FEE_AMT3_H")).longValue();         //������ ���̿�ݾ�

		                GUM3 = "";

		                CNAPTERM = "";  //ü���Ⱓ

		                USETERM  = (String) mpCmd253002List.getMap("USE_STT_DATE") + (String) mpCmd253002List.getMap("USE_END_DATE");          //���Ⱓ

						
					} else if (dang.equals("2")){
						
						if(log.isDebugEnabled()){
							log.debug("ü�� Starting");
						}
						
						
						//�������� SET
		                //���⳻�ΰ�� ���⳻ ���� SET, �������� ��� ������ ���� SET
		                //���⳻(B), ������(A) üũ
						if(NAPGUBUN.equals("B")) {
		                    BNAPDATE = (String) mpCmd253002List.getMap("DUE_DT");         //������ ������
		                    ANAPDATE = BNAPDATE;
		                } else  {
		                    BNAPDATE = (String) mpCmd253002List.getMap("DUE_F_DT");       //������ ������
		                    ANAPDATE = BNAPDATE;
		                }
						
						//ü�����ΰ�� ������ ���⳻�������� ���α��� ����
		                NAPGUBUN = "B";

		                //�����Ѿ� SET
		                BNAPAMT  = ((BigDecimal)mpCmd253002List.getMap("SUM_B_AMT")).longValue();          //���⳻ �ݾ�
		                ANAPAMT  = 0;

		                GUM2 = "";

		                //���μ��αݾ� SET
		                BSAMT    = ((BigDecimal)mpCmd253002List.getMap("FEE_AMT1")).longValue();         //���⳻ ������ݾ�
		                BHAMT    = ((BigDecimal)mpCmd253002List.getMap("FEE_AMT2")).longValue();         //���⳻ �ϼ����ݾ�
		                BGAMT    = ((BigDecimal)mpCmd253002List.getMap("FEE_AMT4")).longValue();         //���⳻ ���ϼ��ݾ�
		                BMAMT    = ((BigDecimal)mpCmd253002List.getMap("FEE_AMT3")).longValue();         //���⳻ ���̿�ݾ�
		                ASAMT    = 0;
		                AHAMT    = 0;
		                AGAMT    = 0;
		                AMAMT    = 0;

		                GUM3 = "";

		                CNAPTERM = (String) mpCmd253002List.getMap("USE_STT_DATE") + (String) mpCmd253002List.getMap("USE_END_DATE");          //ü���Ⱓ
		                USETERM = CNAPTERM;          //���Ⱓ

		           
					}
		
					sendMap.setMap("JUMINNO"            , mf.getMap("JUMINNO"));                    /*�ֹι�ȣ                                */
					sendMap.setMap("CUSTNO"             , mf.getMap("CUSTNO"));                     /*���밡��ȣ/���ڳ��ι�ȣ                           */
					sendMap.setMap("BYYYMM"             , mf.getMap("BYYYMM"));                     /*�ΰ� ���                                         */
					sendMap.setMap("DANGGUBUN"          , dang);                                    /*��� ����                                         */
					sendMap.setMap("NPNO"               , mpCmd253002List.getMap("PRT_NPNO"));      /*���� ��ȣ                                         */
					sendMap.setMap("NAME"               , mpCmd253002List.getMap("REG_NM"));        /*����                                              */
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
					sendMap.setMap("SNAP_BANK_CODE"     , mpCmd253002List.getMap("BRC_NO"));        /*�������� ���� �ڵ�                                */
					sendMap.setMap("SNAP_SYMD"          , mpCmd253002List.getMap("PAY_DT") + "000000");  /*���� �Ͻ�                                    */
					sendMap.setMap("NAPGU_AMT"          , mpCmd253002List.getMap("SUM_RCP"));       /*���αݾ�                                          */
					sendMap.setMap("OUT_ACCTNO"         , "");                                      /*��ݰ��¹�ȣ                                      */
					sendMap.setMap("ETC1"               , "");                                      /*���� ���� FIELD                                   */
				
				}
				
				
			} else {
				resCode = "341";  /*���ڳ��ι�ȣ, ���밡��ȣ ����*/
			}
			
			log.info(cUtil.msgPrint("5", "","KP253002 chk_kf_253002()", resCode));
			
        } catch (Exception e) {
			
        	resCode = "093";  //Error : ���ڳ��ι�ȣ, ���밡��ȣ ����
        	
			e.printStackTrace();
			log.error("============================================");
			log.error("== chk_kf_253002 Exception(�ý���) ");
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
