/**
 *  �ֽý��۸� : ���̹����漼û ��ȭ
 *  ��  ��  �� : ���ܼ��� �������� ����ȸ ����
 *  ��  ��  �� : ������(����)-���̹���û 
 *               ����ó��
 *               ������(����)���� ���ŵ� ������ ���ؼ� ������������Ʈ�� �����ϰ� 
 *               ��ȸ����� �����Ѵ�.
 *  Ŭ����  ID : Df271002Service
 *  ����  �̷� :
 * ------------------------------------------------------------------------
 *  �ۼ���     �Ҽ�  		 ����      Tag   ����
 * ------------------------------------------------------------------------   
 * ��â��   ��ä��(��)    2013.08.13   %01%  �ű��ۼ�
 *  
 */
package com.uc.bs.cyber.service.df271002;

import java.math.BigDecimal;
import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.dao.DuplicateKeyException;

import com.uc.core.MapForm;
import com.uc.core.spring.service.IbatisBaseService;
import com.uc.bs.cyber.CbUtil;
import com.uc.bs.cyber.service.df201002.Df201002FieldList;

/**
 * @author Administrator
 *
 */
public class Df271002Service {

	protected Log log = LogFactory.getLog(this.getClass());
	
	private ApplicationContext appContext = null;
	
	private IbatisBaseService sqlService_cyber = null;
	
	private Df201002FieldList dfField = null;
	
	CbUtil cUtil = null;
	
	/**
	 * ������
	 */
	public Df271002Service(ApplicationContext appContext) {
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
	 * ���ܼ��� �������� ����ȸ ó��...
	 * ����ȸ ȭ�� �� ������(����)���� 5000�̶�� �ⳳ�� ��� ������ 
	 * �߻��ϸ� ������(����)�� �����Ͽ� �ڷḦ �����ϵ��� �Ѵ�.
	 * */
	public byte[] chk_df_271002(MapForm headMap, MapForm mf) throws Exception {
		
		MapForm sendMap = new MapForm();
		
		/*���ܼ��� ����������ȸ �� ��� */
		String resCode = "0000";      /*�����ڵ�*/
		String tx_id = "SN2601";      /*�λ�þ�������*/
		String sg_code = "26";        /*�λ�ñ���ڵ�*/
		
		try{

			cUtil = CbUtil.getInstance();
			
			log.info(cUtil.msgPrint("2", "S","DP271002 chk_df_271002()", resCode));
						
			/*����üũ*/
			
			/*���*/
			if(!headMap.getMap("SIDO_CODE").equals(sg_code)){
				resCode = "1000";
			}
			/*�������ڵ�*/
			if(!headMap.getMap("TX_ID").equals(tx_id)){
				resCode = "1000";
			}
			
			/**
			 * ���ڳ��ι�ȣ�� 19 ��..17�ΰ�� 17 + "00" �پ ������ ���ŵ�...
			 * ���� �̷� ������ �����ϱ� ����
			 */
			if(mf.getMap("EPAY_NO").toString().length() == 17) {
				mf.setMap("EPAY_NO", mf.getMap("EPAY_NO").toString() + "00");
			}
            
            if(mf.getMap("EPAY_NO").toString().length() != 19) {
                resCode = "2000";
            }
            
            if(mf.getMap("TAX_NO").toString().length() != 31) {
                resCode = "2000";
            }
            
            mf.setMap("SGG_COD" , mf.getMap("TAX_NO").toString().substring(2, 5));    //��û�ڵ�
            mf.setMap("ACCT_COD", mf.getMap("TAX_NO").toString().substring(6, 8));    //ȸ���ڵ�
            mf.setMap("TAX_ITEM", mf.getMap("TAX_NO").toString().substring(8, 14));   //����/�����ڵ�
            mf.setMap("TAX_YY"  , mf.getMap("TAX_NO").toString().substring(14, 18));  //�����⵵
            mf.setMap("TAX_MM"  , mf.getMap("TAX_NO").toString().substring(18, 20));  //������
            mf.setMap("TAX_DIV" , mf.getMap("TAX_NO").toString().substring(20, 21));  //����ڵ�
            mf.setMap("HACD"    , mf.getMap("TAX_NO").toString().substring(21, 24));  //�������ڵ�
            mf.setMap("TAX_SNO" , mf.getMap("TAX_NO").toString().substring(24, 30));  //������ȣ
            
            /*���ܼ��� ���ڼ����� �ⳳ�γ����� ���翩�θ� Ȯ���Ѵ�.*/
            ArrayList<MapForm>  dfAl272001List = sqlService_cyber.queryForList("DF272001.SELECT_BNON_PAY_LIST", mf);
            
            int payCnt = dfAl272001List.size();
            
            log.debug("totCnt = " + payCnt);
            
            if(payCnt == 0) {
                resCode = "0000";   /*���γ�������*/
            } else {
                /*���γ��� ����*/
                MapForm mfCmd272001List = dfAl272001List.get(0);
                
                if ( mfCmd272001List == null || mfCmd272001List.isEmpty() ) {
                    /*���γ�������*/
                    resCode = "0000";
                } else {
                    /*���γ����� �ִ� ���*/
                    if(mfCmd272001List.getMap("SNTG").equals("9")) {  /*���*/
                        resCode = "0000";
                    } else {
                        resCode = "5000";  /*���α����*/
                    }
                }
            }
            
			if(mf.getMap("EPAY_NO").toString().length() == 19 && resCode.equals("0000")){
				
				//�α��۾��� : ��ȸ
//				mf.setMap("TX_GB"  , "1");
//				mf.setMap("PAY_DT" , CbUtil.getCurrentDate());
				
				ArrayList<MapForm>  dfCmd271002List  =  sqlService_cyber.queryForList("DF271002.SELECT_BNON_SEARCH_LIST", mf);
				
				log.debug("EPAY_NO = [" + mf.getMap("EPAY_NO") + "]");
//				log.debug("JUMIN_NO = [" + mf.getMap("JUMIN_NO") + "]");
				log.debug("TAX_NO = [" + mf.getMap("TAX_NO") + "]");
				log.debug("dfCmd271002List.size() = [" + dfCmd271002List.size() + "]");
				log.debug("resCode = [" + resCode + "]");
				
				int levy_cnt = dfCmd271002List.size();
				
				if (levy_cnt <= 0) {
					resCode = "5020"; /*��ȸ��������*/
				} else if (levy_cnt > 1) {
					resCode = "5060"; /*������ 2�� �̻�*/
				} else {
					
					for ( int al_cnt = 0;  al_cnt < levy_cnt;  al_cnt++)   {
						
						MapForm mfCmd271002List  =  dfCmd271002List.get(al_cnt);
												
						if ("2".equals(mfCmd271002List.getMap("TAX_GBN"))) {
			                log.debug("SGG_COD  = [" + mf.getMap("SGG_COD") + "]");
			                log.debug("ACCT_COD = [" + mf.getMap("ACCT_COD") + "]");
			                log.debug("TAX_ITEM = [" + mf.getMap("TAX_ITEM") + "]");
			                log.debug("TAX_YY   = [" + mf.getMap("TAX_YY") + "]");
			                log.debug("TAX_MM   = [" + mf.getMap("TAX_MM") + "]");
			                log.debug("TAX_DIV  = [" + mf.getMap("TAX_DIV") + "]");
			                log.debug("HACD     = [" + mf.getMap("HACD") + "]");
			                log.debug("TAX_SNO  = [" + mf.getMap("TAX_SNO") + "]");
			            }
						
						String napGubun = cUtil.getNapkiGubun((String)mfCmd271002List.getMap("DUE_DT"));
					
						/* �����ڸ��� 80����Ʈ�� ������ ��� ���ڰ� �ѱ��� ��� 40�� ���Ϸ� �ڸ��� */
						if(mfCmd271002List.getMap("REG_NM").toString().length() > 0)
						    mfCmd271002List.setMap("REG_NM", CbUtil.ksubstr(mfCmd271002List.getMap("REG_NM").toString(), 40));
						
						/* �ΰ������� 130����Ʈ�� ������ ��� ���ڰ� �ѱ��� ��� 65�� ���Ϸ� �ڸ��� */
						if(mfCmd271002List.getMap("TAX_DESC").toString().length() > 0)
						    mfCmd271002List.setMap("TAX_DESC", CbUtil.ksubstr(mfCmd271002List.getMap("TAX_DESC").toString(), 65));
						
						/*��ȸ�� ���� ����*/
						sendMap.setMap("TAX_GB"                 ,  mf.getMap("TAX_GB"));
						sendMap.setMap("TAX_NO"                 ,  mf.getMap("TAX_NO"));
						sendMap.setMap("EPAY_NO"                ,  mf.getMap("EPAY_NO"));
						sendMap.setMap("NAP_NAME"               ,  mfCmd271002List.getMap("REG_NM"));
						sendMap.setMap("SGG_NAME"               ,  mfCmd271002List.getMap("SGNM"));
						sendMap.setMap("TAX_NAME"               ,  CbUtil.ksubstr(mfCmd271002List.getMap("TAX_NM").toString(), 50)); /*50�ڸ� ���� �ʰ�*/
						sendMap.setMap("TAX_YM"                 ,  mfCmd271002List.getMap("TAX_YM"));
						
						if("1".equals(mfCmd271002List.getMap("BUGWA_STAT"))) {
						    sendMap.setMap("TAX_DIV"                ,  mfCmd271002List.getMap("TAX_DIV"));
						} else {
						    sendMap.setMap("TAX_DIV"                , "0") ;
						}
						sendMap.setMap("NAP_BFAMT"              ,  mfCmd271002List.getMap("AMT"));
						sendMap.setMap("NAP_AFAMT"              ,  mfCmd271002List.getMap("AFT_AMT"));
						
						if (napGubun.equals("B")) {
						    sendMap.setMap("NAP_AMT"              ,  mfCmd271002List.getMap("AMT"));
						} else {
						    sendMap.setMap("NAP_AMT"              ,  mfCmd271002List.getMap("AFT_AMT"));
						}
						sendMap.setMap("NAP_BFDATE"             ,  mfCmd271002List.getMap("DUE_DT"));
						sendMap.setMap("NAP_AFDATE"             ,  mfCmd271002List.getMap("DUE_F_DT"));
						sendMap.setMap("TAX_DESC"               ,  mfCmd271002List.getMap("TAX_DESC"));
						sendMap.setMap("FILLER1"                 ,  " ");
						
						//logging...
//						try{
//							//�α��۾��� : ����
//							mf.setMap("TAX_NO"      ,  mfCmd271002List.getMap("TAX_NO"));
//							mf.setMap("JUMIN_NO"    ,  mfCmd271002List.getMap("REG_NO"));
//							mf.setMap("PAY_DT"      ,  CbUtil.getCurrentDate());
//							mf.setMap("NAPBU_AMT"   ,  mfCmd271002List.getMap("AMT"));
//							mf.setMap("TX_GB"       ,  mfCmd271002List.getMap("TAX_GB"));
//							
//							sqlService_cyber.queryForUpdate("DF271002.INSERT_TX2421_TB_LOG", mf); //�������� ���������� �α�
//							
//						}catch (Exception e) {
//							
//							if (e instanceof DuplicateKeyException){
//								log.info("��ȸ TX2421_TB ���̺� �̹� ��ϵ� ������");
//							} else {
//								log.error("���� ������ = " + mf.getMaps());
//							    log.error("Logging  failed!!!");	
//							}
//						}
					}
				}
			
			} else {
				resCode = "2000";  /*���ڳ��ι�ȣ, ���밡��ȣ ����*/
			}
			
			log.info(cUtil.msgPrint("2", "","DP271002 chk_df_271002()", resCode));
			
        } catch (Exception e) {
			
        	resCode = "2000";  //Error : ���ڳ��ι�ȣ, ���밡��ȣ ����
        	
			e.printStackTrace();
			log.error("============================================");
			log.error("== chk_df_271002 Exception(�ý���) ");
			log.error("============================================");
		}
        
        /*�������� ó���� �ȵǾ��ٸ� �� ���� �״�� �����ش�.*/
        if (!resCode.equals("0000")) {
        	sendMap = mf;
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
