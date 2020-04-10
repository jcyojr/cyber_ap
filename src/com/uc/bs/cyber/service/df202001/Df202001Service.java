/**
 *  �ֽý��۸� : ���̹����漼û ��ȭ
 *  ��  ��  �� : ������(����)-ȯ�氳���δ�� ���ΰ������ / �����û 
 *  ��  ��  �� : ������(����)-���̹���û 
 *               ����ó��
 *               
 *  Ŭ����  ID : Df202001Service
 *  ����  �̷� :
 * ------------------------------------------------------------------------
 *  �ۼ���     �Ҽ�  		 ����      Tag   ����
 * ------------------------------------------------------------------------   
 * ��â��   ��ä��(��)    2013.08.13   %01%  �ű��ۼ�
 *  
 */
package com.uc.bs.cyber.service.df202001;

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
public class Df202001Service {

	protected Log log = LogFactory.getLog(this.getClass());
	
	private ApplicationContext appContext = null;
	
	private IbatisBaseService sqlService_cyber = null;
		
	private Df202001FieldList dfField = null;
	
	private int[] svcTms = new int[2];
	
	CbUtil cUtil = null;
	
	/**
	 * ������
	 */
	public Df202001Service(ApplicationContext appContext) {
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
	 * ȯ�氳���δ�� ���ΰ�� ���� / ���ೳ��
	 * */
	public byte[] chk_df_202001(MapForm headMap, MapForm mf) throws Exception {
		
		/*ȯ�氳���δ�� ����������ȸ �� ��� */
		String resCode = "0000";      /*�����ڵ�*/
		String tx_id = "SN2601";   /*�λ�������ڵ�*/
		String sg_code = "26";        /*�λ�ñ���ڵ�*/
		int curTime = 0;

		try{
			
			/*���� �ð�*/
			svcTms[0] = 0700;
			svcTms[1] = 2200;
			
			cUtil = CbUtil.getInstance();
			
			log.info(cUtil.msgPrint("3", "S","DP202001 chk_df_202001()", resCode));
			
			/*����üũ*/
			
			/*���*/
			if(!headMap.getMap("SIDO_CODE").equals(sg_code)){
				resCode = "1000";
			}
			/*�������ڵ�*/
			if(!headMap.getMap("TX_ID").equals(tx_id)){
				resCode = "1000";
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
            
			/*���γ����� �������� �ʰ� ��ҵ� ��� ����ó��*/
			if(resCode.equals("0000")){
				
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
	            
	            /*�ⳳ�� ���� Ȯ��*/
	            ArrayList<MapForm>  dfAl202001List = sqlService_cyber.queryForList("DF202001.SELECT_ENV_PAY_LIST", mf);
	            
	            log.info("ȯ�氳���δ�� �ⳳ�ο��� ��ȸ �Ϸ�! [" + mf.getMap("ETAX_NO") + "]");
	            
	            int totCnt = dfAl202001List.size();
	            
	            if(totCnt == 0) {
	                resCode = "0000";   /*���γ�������*/
	            } else {
	                
	                /*���� ����*/
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
	            
				/*�ΰ���������Ʈ �˻�*/
				ArrayList<MapForm>  alDfLevyList = sqlService_cyber.queryForList("DF202001.SELECT_ENV_SEARCH_LIST", mf);
				
				log.info("ȯ�氳���δ�� �ΰ����� ��ȸ �Ϸ�! [" + mf.getMap("ETAX_NO") + "]");
				
				if(alDfLevyList.size() == 0) {
					
					resCode = "5020";  /*������������*/
					
				} else if(alDfLevyList.size() > 1) {
					
					resCode = "5060";  /*��������2��, 1���ΰ͸� ó��*/
					
				} else {
					
					MapForm mpDfLevyList = alDfLevyList.get(0);
					
					/*������������*/
					/*���ڼ������̺� ���������� ���Ѵ�. �ߺ�ó���� �����ϹǷ�...*/
					mpDfLevyList.setMap("PAY_CNT" , sqlService_cyber.getOneFieldInteger("DF202001.TX2231_TB_MaxPayCnt", mpDfLevyList));
					mpDfLevyList.setMap("SUM_RCP" , mf.getMap("NAPBU_AMT"));   /*���αݾ�*/
					mpDfLevyList.setMap("PAY_DT"  , mf.getMap("NAPBU_DATE"));  /*��������*/
					mpDfLevyList.setMap("SNTG"    , "1");                      /*���λ���*/
					mpDfLevyList.setMap("SNSU"    , mf.getMap("NAPBU_SNSU"));                      /*�������� 1:���������� 2:ī�� 3:����*/
                    mpDfLevyList.setMap("BANK_CD" , headMap.getMap("BANK_CODE"));    /*���α��*/
                    mpDfLevyList.setMap("BRC_NO"  , CbUtil.rPadString(headMap.getMap("BANK_CODE").toString(), 7, '0'));   /*(����)���α��*/
                    mpDfLevyList.setMap("RSN_YN"  , "N");                      /*���α��� Y:����  N:��ȸ*/
					mpDfLevyList.setMap("TRTG"    , "0");                      /*�ڷ����ۻ���*/
					mpDfLevyList.setMap("TMSG_NO" , headMap.getMap("BCJ_NO")); /*�������_����������ȣ*/
					
					int intLog = sqlService_cyber.queryForUpdate("DF202001.INSERT_TX2231_TB_EPAY", mpDfLevyList);
					
					if(intLog > 0) {
						
						sqlService_cyber.queryForUpdate("DF202001.UPDATE_TX2132_TB_NAPBU_INFO", mpDfLevyList);
						
						log.info("��ȯ�氳���δ�� ���ڳ���ó�� �Ϸ�! [" + mf.getMap("ETAX_NO") + "]");
						
//						MapForm mpSMS = new MapForm();	
//						
//						mpSMS.setMap("REG_NO"  , mpDfLevyList.getMap("REG_NO"));
//						mpSMS.setMap("TAX_ITEM", mpDfLevyList.getMap("TAX_ITEM"));
//						mpSMS.setMap("SUM_RCP" , mf.getMap("NAPBU_AMT"));
//						
//						try {
//							sqlService_cyber.procedure("CODMBASE.SMSCALL", mpSMS);
//						}catch (Exception e) {
//							log.info("SMS ��ϵ����� = " + mpSMS.getMaps());
//							log.info("SMS ��Ͽ��� �߻�");
//						}
						
					}
				}
			}
			
			log.info(cUtil.msgPrint("3", "","DP202001 chk_df_202001()", resCode));
			
        } catch (Exception e) {
			
        	resCode = "9090";  //Error : 
        	
			e.printStackTrace();
			log.error("============================================");
			log.error("== chk_df_201002 Exception(�ý���) ");
			log.error("============================================");
		}
        
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
