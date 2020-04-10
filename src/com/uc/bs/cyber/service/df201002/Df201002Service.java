/**
 *  �ֽý��۸� : ���̹����漼û ��ȭ
 *  ��  ��  �� : ȯ�氳���δ�� �������� ����ȸ ����
 *  ��  ��  �� : ������(����)-���̹���û 
 *               ����ó��
 *               ������(����)���� ���ŵ� ������ ���ؼ� ������������Ʈ�� �����ϰ� 
 *               ��ȸ����� �����Ѵ�.
 *  Ŭ����  ID : Df201002Service
 *  ����  �̷� :
 * ------------------------------------------------------------------------
 *  �ۼ���     �Ҽ�  		 ����      Tag   ����
 * ------------------------------------------------------------------------   
 * ��â��   ��ä��(��)    2013.08.13   %01%  �ű��ۼ�
 *  
 */
package com.uc.bs.cyber.service.df201002;

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
public class Df201002Service {

	protected Log log = LogFactory.getLog(this.getClass());
	
	private ApplicationContext appContext = null;
	
	private IbatisBaseService sqlService_cyber = null;
		
	private Df201002FieldList dfField = null;
	
	CbUtil cUtil = null;
	
	/**
	 * ������
	 */
	public Df201002Service(ApplicationContext appContext) {
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
	 * ȯ�氳���δ�� �������� ����ȸ ó��...
	 * */
	public byte[] chk_df_201002(MapForm headMap, MapForm mf) throws Exception {
		
				
		MapForm sendMap = new MapForm();
		
		/*ȯ�氳���δ�� ����������ȸ �� ��� */
		String resCode = "0000";      /*�����ڵ�*/
		String tx_id = "SN2601";      /*�λ���ڵ�*/
		String sg_code = "26";        /*�λ�ñ���ڵ�*/
		String napkiGubun = "";

		long NAP_BFAMT = 0;
		long NAP_AFAMT = 0;
		long NAP_AMT = 0;

		try{
			
			cUtil = CbUtil.getInstance();
			
			log.info(cUtil.msgPrint("2", "S","DP201002 chk_df_201002()", resCode));
			

			/*����üũ*/
			
			/*���*/
			if(!headMap.getMap("SIDO_CODE").equals(sg_code)){
				resCode = "1000";
			}
			/*�������ڵ�*/
			if(!headMap.getMap("TX_ID").equals(tx_id)){
				resCode = "1000";
			}
			
            mf.setMap("SGG_COD" , mf.getMap("TAX_NO").toString().substring(2, 5));    //��û�ڵ�
            mf.setMap("ACCT_COD", mf.getMap("TAX_NO").toString().substring(6, 8));    //ȸ���ڵ�
            mf.setMap("TAX_ITEM", mf.getMap("TAX_NO").toString().substring(8, 14));   //����/�����ڵ�
            mf.setMap("TAX_YY"  , mf.getMap("TAX_NO").toString().substring(14, 18));  //�����⵵
            mf.setMap("TAX_MM"  , mf.getMap("TAX_NO").toString().substring(18, 20));  //������
            mf.setMap("TAX_DIV" , mf.getMap("TAX_NO").toString().substring(20, 21));  //����ڵ�
            mf.setMap("HACD"    , mf.getMap("TAX_NO").toString().substring(21, 24));  //�������ڵ�
            mf.setMap("TAX_SNO" , mf.getMap("TAX_NO").toString().substring(24, 30));  //������ȣ
            
            log.debug("SGG_COD  = [" + mf.getMap("SGG_COD") + "]");
            log.debug("ACCT_COD = [" + mf.getMap("ACCT_COD") + "]");
            log.debug("TAX_ITEM = [" + mf.getMap("TAX_ITEM") + "]");
            log.debug("TAX_YY   = [" + mf.getMap("TAX_YY") + "]");
            log.debug("TAX_MM   = [" + mf.getMap("TAX_MM") + "]");
            log.debug("TAX_DIV  = [" + mf.getMap("TAX_DIV") + "]");
            log.debug("HACD     = [" + mf.getMap("HACD") + "]");
            log.debug("TAX_SNO  = [" + mf.getMap("TAX_SNO") + "]");
            
            ArrayList<MapForm>  dfAl202001List = sqlService_cyber.queryForList("DF202001.SELECT_ENV_PAY_LIST", mf);
            
            int payCnt = dfAl202001List.size();
            
            if(payCnt == 0) {
                resCode = "0000";   /*���γ�������*/
            } else {
                
                /*���γ��� ����*/
                MapForm mfCmd202001List = dfAl202001List.get(0);
                
                if ( mfCmd202001List == null  ||  mfCmd202001List.isEmpty() )   {
                    /*���γ�������*/
                    resCode = "0000";
                } else {
                    /*������ҳ����� �ִ� ���*/
                    if(mfCmd202001List.getMap("SNTG").equals("9")) {  /*���*/
                        resCode = "0000";
                    } else {
                        resCode = "5000";  /*���α����*/
                    }
                }
            }
            
			log.debug("EPAY_NO.length : " + mf.getMap("EPAY_NO").toString().length());
			
			if(mf.getMap("EPAY_NO").toString().length() == 19 && resCode.equals("0000")){
				
				ArrayList<MapForm>  dfCmd201002List  =  sqlService_cyber.queryForList("DF201002.SELECT_ENV_SEARCH_LIST", mf);
				
				int levyCnt = dfCmd201002List.size();
				
				if ( levyCnt <= 0) {
					resCode = "5020";  /*��ȸ��������*/
				} else if (levyCnt > 1) {
					resCode = "5060";  /*������ 2�� �̻�*/
				} else {
					
					MapForm mfCmd201002List  =  dfCmd201002List.get(0);
					
					MapForm mfSendData = new MapForm();
					
					/*��ȸ �������� ��������*/
					mfSendData.setMap("TAX_GB"               ,  mf.getMap("TAX_GB")  );        /*���ݱ����ڵ�*/
					mfSendData.setMap("TAX_NO"               ,  mf.getMap("TAX_NO")  );        /*������ȣ*/
					mfSendData.setMap("EPAY_NO"              ,  mf.getMap("EPAY_NO")  );       /*���ڳ��ι�ȣ*/
					mfSendData.setMap("NAP_NAME"             ,  mfCmd201002List.getMap("REG_NM")  );  /*������ ���� */
                    mfSendData.setMap("SGG_NAME"             ,  mfCmd201002List.getMap("SGNM")  );    /*�������(�ñ���) ��    */
                    mfSendData.setMap("TAX_NAME"             ,  mfCmd201002List.getMap("TAX_NM")  );  /*����/�����             */
                    mfSendData.setMap("TAX_YM"               ,  (String)mfCmd201002List.getMap("TAX_YY")  +  (String)mfCmd201002List.getMap("TAX_MM")  );  /*�ΰ����*/ 
					
				    napkiGubun = cUtil.getNapGubun((String)mfCmd201002List.getMap("DUE_DT"), (String)mfCmd201002List.getMap("DUE_F_DT"));
				    
				    if(napkiGubun.equals("B")){//���⳻
				    	
				    	if(mfCmd201002List.getMap("DUE_DT").equals(mfCmd201002List.getMap("DUE_F_DT"))){ 
				    		
				    		/*ü��*/
				    		NAP_BFAMT = ((BigDecimal)mfCmd201002List.getMap("ENV_AMT")).longValue()+((BigDecimal)mfCmd201002List.getMap("ADD_AMT")).longValue();   // ���⳻�ݾ�
		                    NAP_AFAMT = ((BigDecimal)mfCmd201002List.getMap("ENV_AMT")).longValue()+((BigDecimal)mfCmd201002List.getMap("ADD_AMT")).longValue();   // �����ıݾ�
		                    NAP_AMT = NAP_BFAMT;
		                    mfSendData.setMap("TAX_DIV"   ,  "0" );  /*���                    */
				    	} else {
				    		/*���⳻*/
				    		NAP_BFAMT = ((BigDecimal)mfCmd201002List.getMap("ENV_AMT")).longValue();   // ���⳻�ݾ�
		                    NAP_AFAMT = ((BigDecimal)mfCmd201002List.getMap("ENV_AMT")).longValue()+((BigDecimal)mfCmd201002List.getMap("ADD_AMT")).longValue();  // �����ıݾ�
		                    NAP_AMT = NAP_BFAMT;
		                    mfSendData.setMap("TAX_DIV"   ,  mfCmd201002List.getMap("TAX_DIV") );  /*���                    */
				    	}
				    	
				    } else if(napkiGubun.equals("A")) {
				    	/*������*/
				    	NAP_BFAMT = ((BigDecimal)mfCmd201002List.getMap("ENV_AMT")).longValue();   // ���⳻�ݾ�
	                    NAP_AFAMT = ((BigDecimal)mfCmd201002List.getMap("ENV_AMT")).longValue()+((BigDecimal)mfCmd201002List.getMap("ADD_AMT")).longValue();  // �����ıݾ�
	                    NAP_AMT = NAP_AFAMT;
	                    mfSendData.setMap("TAX_DIV"    ,  mfCmd201002List.getMap("TAX_DIV") );  /*���                    */
				    }
				    mfSendData.setMap("NAP_BFAMT"            ,  NAP_BFAMT   );
					mfSendData.setMap("NAP_AFAMT"            ,  NAP_AFAMT   );
					mfSendData.setMap("NAP_AMT"              ,  NAP_AMT     );
				    mfSendData.setMap("NAP_BFDATE"           ,  mfCmd201002List.getMap("DUE_DT")  );    /*���⳻ ����*/
				    mfSendData.setMap("NAP_AFDATE"           ,  mfCmd201002List.getMap("DUE_F_DT")  );  /*������ ����*/
				    mfSendData.setMap("TAX_DESC"             , mfCmd201002List.getMap("MLGN_IF1") + " " + mfCmd201002List.getMap("MLGN_IF2")  );
				    mfSendData.setMap("FILLER1"              ,  " "  );  /*����*/
				    
				    /*��������*/
				    sendMap = mfSendData;
				}
				
			} else {
				resCode = "2000";  /*���ڳ��ι�ȣ, ���밡��ȣ ����*/
			}
			
			log.info(cUtil.msgPrint("2", "","DP201002 chk_df_201002()", resCode));
			
        } catch (Exception e) {
			
        	resCode = "2000";  //Error : ���ڳ��ι�ȣ, ���밡��ȣ ����
        	
			e.printStackTrace();
			log.error("============================================");
			log.error("== chk_df_201002 Exception(�ý���) ");
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
