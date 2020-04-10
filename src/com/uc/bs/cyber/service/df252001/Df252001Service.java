/**
 *  �ֽý��۸� : ���̹����漼û ��ȭ
 *  ��  ��  �� : ������(����)-���ϼ������ ���ΰ�� ���� / ���ο���
 *  ��  ��  �� : ������(����)-���̹���û 
 *               ����ó��
 *               
 *  Ŭ����  ID : Df252001Service
 *  ����  �̷� :
 * ------------------------------------------------------------------------
 *  �ۼ���     �Ҽ�  		 ����      Tag   ����
 * ------------------------------------------------------------------------   
 * ��â��   ��ä��(��)    2013.08.13   %01%  �ű��ۼ�
 *  
 */
package com.uc.bs.cyber.service.df252001;

import java.math.BigDecimal;
import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;

import com.uc.core.MapForm;
import com.uc.core.spring.service.IbatisBaseService;
import com.uc.bs.cyber.CbUtil;
import com.uc.bs.cyber.service.df202001.Df202001FieldList;

/**
 * @author Administrator
 *
 */
public class Df252001Service {

	protected Log log = LogFactory.getLog(this.getClass());
	
	private ApplicationContext appContext = null;
	
	private IbatisBaseService sqlService_cyber = null;
		
	private Df202001FieldList dfField = null;
	
	private int[] svcTms = new int[2];
	
	CbUtil cUtil = null;
	
	/**
	 * ������
	 */
	public Df252001Service(ApplicationContext appContext) {
		// TODO Auto-generated constructor stub
		
		setAppContext(appContext);
		
		dfField = new Df202001FieldList();
		
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
	 * ���ϼ������ ���ΰ�� ����
	 * */
	public byte[] chk_df_252001(MapForm headMap, MapForm mf) throws Exception {
				
		CbUtil cUtil = null;
		
		/*���ϼ������ ����������ȸ �� ��� */
		String resCode = "0000";      /*�����ڵ�*/
		String tx_id = "SN2601";   /*�λ�þ��������ڵ�*/
		String sg_code = "26";        /*�λ�ñ���ڵ�*/
		
		int curTime = 0;

		try{
			
			/*���� �ð�*/
			svcTms[0] = 0700;
			svcTms[1] = 2200;
			
			cUtil = CbUtil.getInstance();

			log.info(cUtil.msgPrint("3", "S","DP252001 chk_df_252001()", resCode));
			
			/*����üũ*/
			
			/*���*/
			if(!headMap.getMap("SIDO_CODE").equals(sg_code)){
                resCode = "1000";
			}
			/*�����������ڵ�*/
			if(!headMap.getMap("TX_ID").equals(tx_id)){
				resCode = "1000";
			}
			
			if(mf.getMap("EPAY_NO").toString().length() == 17) {
			    mf.setMap("EPAY_NO", mf.getMap("EPAY_NO").toString() + "00");
	        } else if (mf.getMap("EPAY_NO").toString().length() == 19) {
	            
	        } else {
	            resCode = "2000";
	        }
			
            try {
                curTime = Integer.parseInt(headMap.getMap("TX_DATETIME").toString().substring(8, 12));
                
                if (curTime >= svcTms[0] && curTime <= svcTms[1]) {
                    
                } else {
                    resCode = "9080";  /*���νð� �ƴ�...*/
                }
                
            } catch(Exception e) {
                resCode = "5520"; /*��¥ ���� ����*/
            }

			if(log.isDebugEnabled()) {
				log.debug("-------------------------------------------------------------");
				log.debug("   �� ������ȣ : " + mf.getMap("TAX_NO").toString());
				log.debug("   �� ���ڳ��ι�ȣ : " + mf.getMap("EPAY_NO").toString());
				log.debug("   �� ������� �з� : " + headMap.getMap("BANK_CODE").toString());
				log.debug("   �� ���αݾ� : " + mf.getMap("NAPBU_AMT").toString());
				log.debug("   �� �������� : " + mf.getMap("NAPBU_DATE").toString());
				log.debug("-------------------------------------------------------------");
			}
			
			@SuppressWarnings("unused")
			String searchgubun = "";
			int rcpCount       = 0;
			int totalCount     = 0;
			
			/*������(����)���� ������ ������ DB �� �����ϱ� ����.*/
	        mf.setMap("PRT_NO"     , CbUtil.getCurrent("yyyyMMdd").substring(0, 2) + mf.getMap("TAX_NO").toString().substring(0, 12));

			String napkiGubun = "";    // ���⳻�� ���� (B:���⳻, A:������)
	        String NAPAMT = "";        // ���αݾ�
			
	        /*�ⳳ�γ��� ���翩�� Ȯ��*/
	        try {
    			ArrayList<MapForm>  dfAl252001List = sqlService_cyber.queryForList("DF251002.SELECT_SHSD_PAY_LIST", mf);
    	        
    			rcpCount = dfAl252001List.size();
    			
    	        if (rcpCount < 1){
    	        	resCode = "0000"; //���γ�������
    	        } else {
    	        	/*���γ��� ����*/
    				MapForm mfCmd252001List = dfAl252001List.get(0);
    				
    				if ( mfCmd252001List == null || mfCmd252001List.isEmpty() ) {
    					/*���γ�������*/
    					resCode = "0000";
    				} else {
    					/*���γ����� �ִ� ���*/
    					if(mfCmd252001List.getMap("SNTG").equals("9")) {  /*���*/
    						resCode = "0000";
    					} else {
    						resCode = "5000";  /*�̹̼����� ������*/
    					}
    				}
    	        }
	        } catch (Exception e) {
	            resCode = "8000";
                log.info("���ϼ������ �ⳳ�ο��� ��ȸ Exception! [" + mf.getMap("ETAX_NO") + "]");
	        }
		        
	        /*���γ����� �������� �ʰų�, ������ҵ� ��쿡 ���� ����ó��*/
	        if(resCode.equals("0000")) {
	        	
	        	/*�ΰ����� ��ȸ*/
	            ArrayList<MapForm>  alDfLevyList = null;
	            try {
	                alDfLevyList = sqlService_cyber.queryForList("DF251002.SELECT_SHSD_LEVY_LIST", mf);
	            } catch(Exception e) {
	                resCode = "8000";
	                log.info("���ϼ������ �ΰ����� ��ȸ Exception! [" + mf.getMap("ETAX_NO") + "]");
                }
	            
				totalCount = alDfLevyList.size();
				
				if (totalCount < 1)
					resCode = "5020";        // Error : ������������
		        else if (totalCount > 1)
		        	resCode = "3000";        // Error : �������� 2�� �̻�
		        else {                       // ���������� 1���� ��쿡�� ó��
		        	
		            String CURDATE = (String) CbUtil.getCurrentDate().substring(0, 8);
		            log.debug("CURDATE = " + CURDATE );
		            
		        	MapForm mpDfLevyList = alDfLevyList.get(0);
		        	
			        String prt_del1 = (String)mpDfLevyList.getMap("DUE_DT");         //���⳻ ������;
			        String prt_del2 = (String)mpDfLevyList.getMap("DUE_F_DT");       //������ ������;
			        
			        if (CbUtil.getInt(CURDATE) > CbUtil.getInt(prt_del2)) {
                        resCode = "5010";   //�������ڰ� ���� ������
                    } else {

    	                //���⳻(B), ������(A) üũ
    	                napkiGubun = cUtil.getNapGubun(prt_del1, prt_del2);
    
    	                //����ó������ Set
    	                long prt_amt         = ((BigDecimal)mpDfLevyList.getMap("SUM_B_AMT")).longValue();    /*���⳻*/
    	                long prt_amt2        = ((BigDecimal)mpDfLevyList.getMap("SUM_F_AMT")).longValue();    /*������*/
    	                
    	                if(napkiGubun.equals("B"))
    	                {
    	                    NAPAMT = Long.toString(prt_amt);   //���⳻�ݾ�
    	                }
    	                else if(napkiGubun.equals("A"))
    	                {
    	                    NAPAMT = Long.toString(prt_amt2);  //�����ıݾ�
    	                }
    	                
    	                if (NAPAMT.equals((String) mf.getMap("NAPBU_AMT")))
    	            	    resCode = "0000";        // ����
    	                else
    	            	    resCode = "4000";        // ���αݾ� Ʋ��
    	                
    	    	        // ��������ó��
    	    	        // 1. �ΰ����� update
    	    	        // 2. �������̺� insert
    	    			if (resCode.equals("0000")) {
    	    				
    	    				/*������������*/
    	    				/*���ڼ������̺� ���������� ���Ѵ�. �ߺ�ó���� �����ϹǷ�...*/
    	    				mpDfLevyList.setMap("PAY_CNT"     , sqlService_cyber.getOneFieldInteger("DF251002.TX3211_TB_MaxPayCnt", mpDfLevyList));
    	    				mpDfLevyList.setMap("SUM_RCP"     , NAPAMT);                             /*���αݾ�*/
    	    				mpDfLevyList.setMap("PAY_DT"      , mf.getMap("NAPBU_DATE"));            /*��������*/
    	    				mpDfLevyList.setMap("SNTG"        , "1");                                /*���λ���*/
    	    				mpDfLevyList.setMap("SNSU"        , mf.getMap("NAPBU_SNSU"));                                /*������ü 1 ����������, 2 ī��, 3:����, 7:��ü*/
    	    				mpDfLevyList.setMap("RSN_YN"      , "N");                                /*���α��� Q����  R��ȸ*/
    	    				/*mpDfLevyList.setMap("TRTG"        , "0");                                �ڷ����ۻ���*/
    	    				mpDfLevyList.setMap("BANK_CD"     , headMap.getMap("BANK_CODE"));             /*���α�� -ī��*/
    	    				mpDfLevyList.setMap("BRC_NO"      , CbUtil.rPadString(headMap.getMap("BANK_CODE").toString(), 7, '0'));  /*��������ڵ�*/
    						mpDfLevyList.setMap("TMSG_NO"     , headMap.getMap("BCJ_NO"));           /*�������_����������ȣ*/
    	    				
    						try {
    						    
    		    				int intLog = sqlService_cyber.queryForUpdate("DF251002.INSERT_TX3211_TB_EPAY", mpDfLevyList);
    		    				
    		    				if(intLog > 0) {
    		    					
    		    					sqlService_cyber.queryForUpdate("DF251002.UPDATE_TX3111_TB_NAPBU_INFO", mpDfLevyList);
    		    					
    		    					log.info("�� ���ϼ������ ���ڳ���ó�� �Ϸ�! [" + mf.getMap("EPAY_NO") + "]");
    		    					
    		    					//MapForm mpSMS = new MapForm();	
    								
    		    					/*���ϼ����� �ֹι�ȣ�� ������..Select �Ѵ�.*/
    								//mpSMS.setMap("REG_NO"  , "");
    								//mpSMS.setMap("TAX_ITEM", mpKfLevyList.getMap("TAX_ITEM"));
    								//mpSMS.setMap("SUM_RCP" , NAPAMT);
    								
    								//sqlService_cyber.procedure("CODMBASE.SMSCALL", mpSMS);
    		    				} 
    						}catch(Exception e) {
    						    resCode = "8000";
    						    log.info("���ϼ������ ���ڳ��� ó�� Exception! [" + mf.getMap("ETAX_NO") + "]");
    	    				}
    	    			}
                    }
		        }
	        }
			
			log.info(cUtil.msgPrint("3","","DP252001", resCode));
			log.info("=============================================");
		
        } catch (Exception e) {
            resCode = "9090";
            
			e.printStackTrace();
			log.error("============================================");
			log.error("== chk_df_252001 Exception(�ý���) ");
			log.error("============================================");
		}
        
        /*���������� ���� ������.*/
        
        /*���⼭ ������带 �����Ѵ�. �ٲ�� �κи� �ٽ� �����ؼ� ������ ����*/
        headMap.setMap("RESP_CODE" , resCode);     /*�����ڵ�*/
        headMap.setMap("TX_GUBUN"  , "060");
        
        return dfField.makeSendBuffer(headMap, mf);
        
	}

	/*----------------------------------------------*/
	/* �Ϸù�ȣ �������� �׽�Ʈ*/
	/*----------------------------------------------*/
	private int SeqNumber(){
    	return (Integer)sqlService_cyber.queryForBean("CODMBASE.CODM_GETSEQNO", "00");
    }
}
