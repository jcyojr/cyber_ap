/**
 *  �ֽý��۸� : ���̹����漼û ��ȭ
 *  ��  ��  �� : ������-���ܼ��� ���ΰ������/ �����û 
 *  ��  ��  �� : ������-���̹���û 
 *               ����ó��
 *               
 *  Ŭ����  ID : Df272001Service
 *  ����  �̷� :
 * ------------------------------------------------------------------------
 *  �ۼ���     �Ҽ�  		 ����      Tag   ����
 * ------------------------------------------------------------------------   
 * ��â��   ��ä��(��)    2013.08.13   %01%  �ű��ۼ�
 *  
 */
package com.uc.bs.cyber.service.df272001;

import java.math.BigDecimal;
import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.dao.DuplicateKeyException;

import com.uc.core.MapForm;
import com.uc.core.spring.service.IbatisBaseService;
import com.uc.bs.cyber.CbUtil;
import com.uc.bs.cyber.service.df202001.Df202001FieldList;

/**
 * @author Administrator
 *
 */
public class Df272001Service {

	protected Log log = LogFactory.getLog(this.getClass());
	
	private ApplicationContext appContext = null;
	
	private IbatisBaseService sqlService_cyber = null;
		
	private Df202001FieldList dfField = null;
	
	private int[] svcTms = new int[2];
	
	CbUtil cUtil = null;
	
	/**
	 * ������
	 */
	public Df272001Service(ApplicationContext appContext) {
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
	 * ���ܼ��� ���ΰ�� ���� 
	 * */
	public byte[] chk_df_272001(MapForm headMap, MapForm mf) throws Exception {
		
		/*���ܼ��� ����������ȸ �� ��� */
		String resCode = "0000";        /*�����ڵ�*/
		String tx_id = "SN2601";    /*�λ�þ��������ڵ�*/
		String sg_code = "26";         /*�λ�ñ���ڵ�*/
		int curTime = 0;

		try{
			/*���� �ð�*/
			svcTms[0] = 7000;
			svcTms[1] = 2200;
			
			cUtil = CbUtil.getInstance();
			
			log.info(cUtil.msgPrint("3", "S","DP272001 chk_df_272001()", resCode));

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
			}
			
			if(mf.getMap("EPAY_NO").toString().length() != 19) {
				resCode = "2000";
			}
			
			if(mf.getMap("TAX_NO").toString().length() != 31) {
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

			/*������(����)���� ������ ������ �޾� ��������� ���θ� Ȯ���Ѵ�.*/
            int totCnt = 0;
            try {
    			ArrayList<MapForm>  dfAl272001List = sqlService_cyber.queryForList("DF272001.SELECT_BNON_PAY_LIST", mf);
    			
    			totCnt = dfAl272001List.size();
    			
    			log.debug("totCnt = " + totCnt);
    			
    			if(totCnt == 0) {
    				resCode = "0000";   /*���γ�������*/
    			} else {
    				/*���γ��� ����*/
    				MapForm mfCmd272001List = dfAl272001List.get(0);
    				
    				if ( mfCmd272001List == null  ||  mfCmd272001List.isEmpty() )   {
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
            } catch (Exception e) {
                resCode = "8000";
                log.info("���ܼ��� �ⳳ�ο��� ��ȸ Exception! [" + mf.getMap("ETAX_NO") + "]");
            }
			
			/*���γ����� �������� �ʰų� ��ҵ� ��� ����ó��*/
			if(resCode.equals("0000")){
				/*�ΰ�����Ʈ �˻�*/
				ArrayList<MapForm>  alDfLevyList = sqlService_cyber.queryForList("DF272001.SELECT_BNON_SEARCH_LIST", mf);
				
				if(alDfLevyList.size() == 0) {
					resCode = "5020";  /*������������*/
				} else if(alDfLevyList.size() > 1) {
					resCode = "5060";  /*��������2�� 1���ΰ͸� ó��*/
				} else {
					MapForm mpDfLevyList = alDfLevyList.get(0);
					
					long rcp_amt = 0;
					
					/*  
					 * �����ڷ�� �ΰ��ڷ� �����ͺ�(1)
					 */
					if(cUtil.getNapGubun((String)mpDfLevyList.getMap("DUE_DT"), (String)mpDfLevyList.getMap("DUE_F_DT")).toString().equals("B")){
					    rcp_amt = ((BigDecimal)mpDfLevyList.getMap("AMT")).longValue(); 
					} else {
						rcp_amt = ((BigDecimal)mpDfLevyList.getMap("AFTAMT")).longValue(); 
					}
					
					if(mf.getMap("NAPBU_AMT").equals(String.valueOf(rcp_amt))){ //���δ�� �ݾװ� ������ �ݾ� ��
							resCode = "0000";
					/* ���αݾ� Ʋ�� */
					} else {
						resCode = "4000";
					}
					
					if (resCode.equals("0000")) {
						/*
						 * �� ��(1)
						 */
					    log.debug("mpDfLevyList  = "  + mpDfLevyList.getMaps());
						/*������������*/
						/*���ڼ������̺� ���������� ���Ѵ�. �ߺ�ó���� �����ϹǷ�...*/
						
						int rcp_cnt = 0;
						
						if(totCnt == 0){
							rcp_cnt = 0;
						} else {
							
							if(mpDfLevyList.getMap("TAX_GBN").equals("1")) { //ǥ�ؼ��ܼ��� ���ڼ���
							    log.debug("��  ǥ�ؼ��ܼ��� MaxPayCnt ");
								rcp_cnt = sqlService_cyber.getOneFieldInteger("DF272001.TX2211_TB_MaxPayCnt", mpDfLevyList);
							} else {
							    log.debug("��  �����ܼ��� MaxPayCnt ");
							    mpDfLevyList.setMap("TAX_NO" , mf.getMap("TAX_NO"));
								rcp_cnt = sqlService_cyber.getOneFieldInteger("DF272001.TX2221_TB_MaxPayCnt", mpDfLevyList);
							}
							
							log.debug("��  ���ܼ��� MaxPayCnt : " + Integer.toString(rcp_cnt));
						}
						
						mpDfLevyList.setMap("PAY_CNT" , rcp_cnt);
						mpDfLevyList.setMap("SUM_RCP" , mf.getMap("NAPBU_AMT"));     /*���αݾ�*/
						mpDfLevyList.setMap("PAY_DT"  , mf.getMap("NAPBU_DATE"));    /*��������*/
						mpDfLevyList.setMap("SNTG"    , "1");                        /*���λ���*/
						mpDfLevyList.setMap("SNSU"    , mf.getMap("NAPBU_SNSU"));      /*�������� 1:���������� 2:ī�� 3:����*/
						mpDfLevyList.setMap("BANK_CD" , headMap.getMap("BANK_CODE"));    /*���α��*/
	                    mpDfLevyList.setMap("BRC_NO"  , CbUtil.rPadString(headMap.getMap("BANK_CODE").toString(), 7, '0'));   /*(����)���α�������ڵ�*/
	                    mpDfLevyList.setMap("RSN_YN"  , "N");                      /*���α��� Y:����  N:��ȸ*/
						mpDfLevyList.setMap("TRTG"    , "0");                        /*�ڷ����ۻ���*/						
						mpDfLevyList.setMap("TMSG_NO" , headMap.getMap("BCJ_NO"));   /*�������_����������ȣ*/
						
						mpDfLevyList.setMap("EPAY_NO" , mf.getMap("EPAY_NO"));       /*���ڳ���*/
						mpDfLevyList.setMap("TAX_NO"  , mf.getMap("TAX_NO"));       /*������ȣ*/
						
						/*�����ܸ� ����*/
						if(mpDfLevyList.getMap("TAX_GBN").equals("2")) {
    		                mpDfLevyList.setMap("SGG_COD" , mf.getMap("TAX_NO").toString().substring(2, 5));    //��û�ڵ�
    		                mpDfLevyList.setMap("ACCT_COD", mf.getMap("TAX_NO").toString().substring(6, 8));    //ȸ���ڵ�
    		                mpDfLevyList.setMap("TAX_ITEM", mf.getMap("TAX_NO").toString().substring(8, 14));   //����/�����ڵ�
    		                mpDfLevyList.setMap("TAX_YY"  , mf.getMap("TAX_NO").toString().substring(14, 18));  //�����⵵
    		                mpDfLevyList.setMap("TAX_MM"  , mf.getMap("TAX_NO").toString().substring(18, 20));  //������
    		                mpDfLevyList.setMap("TAX_DIV" , mf.getMap("TAX_NO").toString().substring(20, 21));  //����ڵ�
    		                mpDfLevyList.setMap("HACD"    , mf.getMap("TAX_NO").toString().substring(21, 24));  //�������ڵ�
    		                mpDfLevyList.setMap("TAX_SNO" , mf.getMap("TAX_NO").toString().substring(24, 30));  //������ȣ
    		                
    		                log.debug("SGG_COD  = [" + mpDfLevyList.getMap("SGG_COD") + "]");
    		                log.debug("ACCT_COD = [" + mpDfLevyList.getMap("ACCT_COD") + "]");
    		                log.debug("TAX_ITEM = [" + mpDfLevyList.getMap("TAX_ITEM") + "]");
    		                log.debug("TAX_YY   = [" + mpDfLevyList.getMap("TAX_YY") + "]");
    		                log.debug("TAX_MM   = [" + mpDfLevyList.getMap("TAX_MM") + "]");
    		                log.debug("TAX_DIV  = [" + mpDfLevyList.getMap("TAX_DIV") + "]");
    		                log.debug("HACD     = [" + mpDfLevyList.getMap("HACD") + "]");
    		                log.debug("TAX_SNO  = [" + mpDfLevyList.getMap("TAX_SNO") + "]");
						}
						
						int intLog = 0;
						
						try {
    						if(mpDfLevyList.getMap("TAX_GBN").equals("1")) {
    							intLog = sqlService_cyber.queryForUpdate("DF272001.INSERT_TX2211_TB_EPAY", mpDfLevyList);  /*ǥ�ؼ��ܼ���*/
    						} else {
    							intLog = sqlService_cyber.queryForUpdate("DF272001.INSERT_TX2221_TB_EPAY", mpDfLevyList);  /*�����ܼ��� : ��������, �ְ���*/
    						}
    
    						if(intLog > 0) {
    							
    							if(mpDfLevyList.getMap("TAX_GBN").equals("1")) {
    								sqlService_cyber.queryForUpdate("DF272001.UPDATE_TX2112_TB_NAPBU_INFO", mpDfLevyList);
    								
    								log.info("�� ǥ�ؼ��ܼ��� ���ڳ���ó�� �Ϸ�! [" + mf.getMap("EPAY_NO") + "]");
    							}else{
    								
    								sqlService_cyber.queryForUpdate("DF272001.UPDATE_TX2122_TB_NAPBU_INFO", mpDfLevyList);
    								
    								log.info("�� �����ܼ��� ���ڳ���ó�� �Ϸ�! [" + mf.getMap("EPAY_NO") + "]");
    							}
    						}
						} catch (Exception e) {
						    resCode = "8000";
						    log.info(" ǥ�ؼ��ܼ��� ���ڳ���ó�� Exception! [" + mf.getMap("EPAY_NO") + "]");
						}

							//������ ������ȣ�� �ٸ����� ���� �α��۾��� : ����
							//mf.setMap("TAX_NO"    , mf.getMap("TAX_NO"))
//							mf.setMap("TAX_NO"    , mpDfLevyList.getMap("TAX_NO"));	//�α�������� by ��â��
//							mf.setMap("JUMIN_NO"  , mpDfLevyList.getMap("JUMIN_NO"));
//							mf.setMap("NAPBU_AMT" , mf.getMap("NAPBU_AMT"));  
//							mf.setMap("PAY_DT"    , mf.getMap("NAPBU_DATE"));
//							mf.setMap("TX_GB"     , mpDfLevyList.getMap("TAX_GBN"));  
//							
//							try{
//								sqlService_cyber.queryForUpdate("DF271002.INSERT_TX2421_TB_LOG", mf);//������������������α�
//							}catch (Exception e) {
//								if (e instanceof DuplicateKeyException){
//									log.info("����: TX2421_TB ���̺� �̹� ��ϵ� ������");
//								} else {
//									log.error("���� ������ = " + mf.getMaps());
//								    log.error("Logging  failed!!!");	
//								}
//							}
							
//							MapForm mpSMS = new MapForm();
//							
//							mpSMS.setMap("REG_NO"  , mpDfLevyList.getMap("REG_NO"));
//							mpSMS.setMap("TAX_ITEM", mpDfLevyList.getMap("TAX_ITEM"));
//							mpSMS.setMap("SUM_RCP" , mf.getMap("NAPBU_AMT"));
//							
//							try {
////								sqlService_cyber.procedure("CODMBASE.SMSCALL", mpSMS);
//							}catch (Exception e) {
//								log.info("SMS ��ϵ����� = " + mpSMS.getMaps());
//								log.info("SMS ��Ͽ��� �߻�");
//							}
//						}
					}
				}
			}
			
			log.info(cUtil.msgPrint("3", "","DP272001 chk_df_272001()", resCode));
			
        } catch (Exception e) {
			
        	resCode = "9090";  //Error : �˷����� ���� ����
        	
			e.printStackTrace();
			log.error("============================================");
			log.error("== chk_df_272001 Exception(�ý���) ");
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
