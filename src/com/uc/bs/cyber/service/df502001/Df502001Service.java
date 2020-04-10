/**
 *  �ֽý��۸� : ���̹����漼û ��ȭ
 *  ��  ��  �� : ���漼 ������������
 *  ��  ��  �� : ������(����)-���̹���û 
 *               ����ó��
 *               ������(����)���� ���ŵ� ������ ���ؼ� ������������Ʈ�� �����ϰ� 
 *               ��ȸ����� �����Ѵ�.
 *  Ŭ����  ID : Df502001Service
 *  ����  �̷� :
 * ------------------------------------------------------------------------
 *  �ۼ���     �Ҽ�  		 ����      Tag   ����
 * ------------------------------------------------------------------------   
 * ��â��   ��ä��(��)    2013.08.13   %01%  �ű��ۼ�
 *  
 */
package com.uc.bs.cyber.service.df502001;

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
public class Df502001Service {

	protected Log log = LogFactory.getLog(this.getClass());
	
	private ApplicationContext appContext = null;
	
	private IbatisBaseService sqlService_cyber = null;
		
	private Df202001FieldList dfField = null;
	
	private int[] svcTms = new int[2];
	
	CbUtil cUtil = null;
	
	/**
	 * ������
	 */
	public Df502001Service(ApplicationContext appContext) {
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
	 * ���漼 �����뺸 ó��...
	 * */
	public byte[] chk_df_502001(MapForm headMap, MapForm mf) throws Exception {
		
		/*
		 * ������ �����ð� : 07:00 ~ 22:00
		 * */
				
		MapForm sendMap = new MapForm();
		
		/*���ܼ��� ����������ȸ �� ��� */
		String resCode = "0000";       /*�����ڵ�*/
		String tx_id = "SN2601";
		String sg_code = "26";        /*�λ�ñ���ڵ�*/
		int curTime = 0;
       
		try{
			
			/*���� �ð�*/
			svcTms[0] = 0700;
			svcTms[1] = 2200;
			
			cUtil = CbUtil.getInstance();
			
			log.info(cUtil.msgPrint("", "S", "DP502001 chk_df_502001()", resCode));
			
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
			
			if(resCode.equals("0000")){
				
				/* ��ȸ���� �ڸ���(���ι�ȣ �߶� ��ȸ���� �����) */
			    mf.setMap("SGG_COD" , mf.getMap("TAX_NO").toString().substring(2, 5));    //��û�ڵ�
	            mf.setMap("ACCT_COD", mf.getMap("TAX_NO").toString().substring(6, 8));    //ȸ���ڵ�
	            mf.setMap("TAX_ITEM", mf.getMap("TAX_NO").toString().substring(8, 14));   //����/�����ڵ�
	            mf.setMap("TAX_YY"  , mf.getMap("TAX_NO").toString().substring(14, 18));  //�����⵵
	            mf.setMap("TAX_MM"  , mf.getMap("TAX_NO").toString().substring(18, 20));  //������
	            mf.setMap("TAX_DIV" , mf.getMap("TAX_NO").toString().substring(20, 21));  //����ڵ�
	            mf.setMap("HACD"    , mf.getMap("TAX_NO").toString().substring(21, 24));  //�������ڵ�
	            mf.setMap("TAX_SNO" , mf.getMap("TAX_NO").toString().substring(24, 30));  //������ȣ
				
				log.debug("TAX_NO   = [" + mf.getMap("TAX_NO") + "]");
				log.debug("EPAY_NO  = [" + mf.getMap("EPAY_NO") + "]");
				log.debug("SGG_COD  = [" + mf.getMap("SGG_COD") + "]");
				log.debug("ACCT_COD = [" + mf.getMap("ACCT_COD") + "]");
				log.debug("TAX_ITEM = [" + mf.getMap("TAX_ITEM") + "]");
				log.debug("TAX_YY   = [" + mf.getMap("TAX_YY") + "]");
				log.debug("TAX_MM   = [" + mf.getMap("TAX_MM") + "]");
				log.debug("TAX_DIV  = [" + mf.getMap("TAX_DIV") + "]");
				log.debug("HACD     = [" + mf.getMap("HACD") + "]");
				log.debug("TAX_SNO  = [" + mf.getMap("TAX_SNO") + "]");
				
				/*���漼 �ΰ����� ��ȸ*/
				ArrayList<MapForm>  dfCmd502001List  =  sqlService_cyber.queryForList("DF502001.SELECT_TAX_SEARCH_LIST", mf);
				
				log.info("dfCmd502001List.size() = [" + dfCmd502001List.size() +"]");
				
		        /* Error : ������������ */
				if(dfCmd502001List.size() <= 0){
					resCode = "5020";  
				} 
				/* Error : �������� 2�� �̻� */
				else if (dfCmd502001List.size() > 1) {
					resCode = "5060";
				} else {
					
					/* ��ȸ��� ��� ���� */
					MapForm taxform = new MapForm();
					
					if ( dfCmd502001List.size() > 0 ) {
						
						taxform = dfCmd502001List.get(0);

						String napkiGubun = cUtil.getNapkiGubun(taxform.getMap("DUE_DT").toString());
						
						/* ����, �ְ��� */
						if(taxform.getMap("TAX_GBN").equals("2")){
							taxform.setMap("SUM_RCP", taxform.getMap("SUM_B_AMT"));
						}
		                /* ���漼 */
		                else {
		                	/* ü��(DLQ_DIV= 1:�ΰ� 2:ü��)�и��� ����� 3(�ڳ�) �ƴҶ� ���⳻�ݾ� �� �����ıݾ� ��� */
		                	if (taxform.getMap("DLQ_DIV").equals("1") && !taxform.getMap("TAX_DIV").equals("3")){
		                		
		                		/* ���αݾ� */
		                		taxform.setMap("SUM_RCP", cUtil.getNapAmt(taxform.getMap("TAX_DT").toString()                       //�ΰ�����
		        														, napkiGubun												//���⳻�� ����
		        														, Long.parseLong(taxform.getMap("MNTX").toString())			//����
																		, Long.parseLong(taxform.getMap("CPTX").toString())			//���ð�ȸ��
																		, Long.parseLong(taxform.getMap("CFTX").toString())			//�����ü���
																		, Long.parseLong(taxform.getMap("LETX").toString())			//������
																		, Long.parseLong(taxform.getMap("ASTX").toString())));		//��Ư��
		                		
		                	} else {
		                		/* ���αݾ� */
		                		taxform.setMap("SUM_RCP", cUtil.getNapAmt(Long.parseLong(taxform.getMap("MNTX").toString())			//����
		        														, Long.parseLong(taxform.getMap("MNTX_ADTX").toString())	//���� �����
																		, Long.parseLong(taxform.getMap("CPTX").toString())			//���ð�ȸ��
																		, Long.parseLong(taxform.getMap("CPTX_ADTX").toString())	//���ð�ȸ�� �����
																		, Long.parseLong(taxform.getMap("CFTX").toString())			//�����ü���
																		, Long.parseLong(taxform.getMap("CFTX_ADTX").toString())	//�����ü��� �����
																		, Long.parseLong(taxform.getMap("LETX").toString())			//������
																		, Long.parseLong(taxform.getMap("LETX_ADTX").toString())	//������ �����
																		, Long.parseLong(taxform.getMap("ASTX").toString())			//��Ư��
		                												, Long.parseLong(taxform.getMap("ASTX_ADTX").toString())));	//��Ư�� �����
		                		/* ������ ������ �ݾ� �״�� ����ȴ� */
		                	}
		                	
		                }

			            if (Long.parseLong(mf.getMap("NAPBU_AMT").toString()) == Long.parseLong(taxform.getMap("SUM_RCP").toString())) {
			            
    						taxform.setMap("SGG_COD" , taxform.getMap("SGG_COD"));  /*��û�ڵ� */
    						taxform.setMap("ACCT_COD", taxform.getMap("ACCT_COD")); /*ȸ���ڵ� */
    						taxform.setMap("TAX_ITEM", taxform.getMap("TAX_ITEM")); /*������   */
    					    taxform.setMap("TAX_YY"  , taxform.getMap("TAX_YY"));   /*�ΰ��⵵ */
    					    taxform.setMap("TAX_MM"  , taxform.getMap("TAX_MM"));   /*�ΰ���   */
    					    taxform.setMap("TAX_DIV" , taxform.getMap("TAX_DIV"));  /*���     */
    					    taxform.setMap("HACD"    , taxform.getMap("HACD"));     /*������   */
    					    taxform.setMap("TAX_SNO" , taxform.getMap("TAX_SNO"));  /*������ȣ */
    					    taxform.setMap("RCP_CNT" , taxform.getMap("RCP_CNT"));  /*�г����� */
    					    taxform.setMap("SNTG"    , "1");                        /*����FLAG */
    					    taxform.setMap("SUM_RCP" , taxform.getMap("SUM_RCP"));  /*�����ݾ� */
    					    taxform.setMap("PAY_DT"  , mf.getMap("NAPBU_DATE"));    /*�������� */
    					    taxform.setMap("SNSU"    , mf.getMap("NAPBU_SNSU"));                        /*��������(�ݰ��:'1', �λ�����:'3', ī��:'2') */
    					    //���� ���α���� ������ H�� �Ǿ� �ֱ⶧���� ��'0'�� �����ǹǷ� �ݵ�� �߰���...
    					    taxform.setMap("BANK_CD", headMap.getMap("BANK_CODE"));   /*���α�� */
    					    taxform.setMap("BRC_NO" , CbUtil.rPadString(headMap.getMap("BANK_CODE").toString(), 7, '0'));    /*��������ڵ�    */
    					    taxform.setMap("TMSG_NO", headMap.getMap("BCJ_NO"));    /*�������_����������ȣ */   
    					    taxform.setMap("RSN_YN" , "N");
    					    
    					    int pay_cnt = 0, intLog = 0, pay_gbn = 0;
    					    
    					    /**
    						 * 20110726 : �ڵ���ü ��ϰ��� ����ó���Ұ�
    						 */
    						if(!taxform.getMap("AUTO_TRNF_YN").equals("Y")){
    							
    							/* ���漼 ����ó�� */
    						    if(taxform.getMap("TAX_GBN").equals("1")){
    						    	
    						    	/*��� ��ü�� ���� ���ΰǼ��� �ִ��� Ȯ��(�ߺ����������ϹǷ� ���������� ����*/
    						    	pay_gbn = sqlService_cyber.getOneFieldInteger("DF502001.TX1201_TB_PAY", taxform);
    						    	
    						    	pay_cnt = pay_gbn;
    						    	
    						    	//if (pay_gbn == 0) {
    						    	//	pay_cnt = 0;
    						    	//} else {
    						    	//	pay_cnt = sqlService_cyber.getOneFieldInteger("DF502001.TX1201_TB_MaxPayCnt", taxform);
    						    	//}
    
    						    	taxform.setMap("PAY_CNT", pay_cnt);
    						    	
    						    	intLog = sqlService_cyber.queryForUpdate("DF502001.INSERT_TX1201_TB_EPAY", taxform);
    						    	
    								if(intLog > 0) {
    									
    									sqlService_cyber.queryForUpdate("DF502001.UPDATE_TX1102_TB_NAPBU_INFO", taxform);
    									
    									log.info("�� ���漼 ���ڳ���ó�� �Ϸ�! [" + mf.getMap("TAX_NO") + "]");
    									
//    									MapForm mpSMS = new MapForm();
//    									
//    									mpSMS.setMap("REG_NO"  , taxform.getMap("REG_NO"));
//    									mpSMS.setMap("TAX_ITEM", taxform.getMap("TAX_ITEM"));
//    									mpSMS.setMap("SUM_RCP" , taxform.getMap("SUM_RCP"));
//    									
//    									try {
//    										sqlService_cyber.procedure("CODMBASE.SMSCALL", mpSMS);
//    									}catch (Exception e) {
//    										log.info("SMS ��ϵ����� = " + mpSMS.getMaps());
//    										log.info("SMS ��Ͽ��� �߻�");
//    									}
//    									
    								}
    						    }
    						    /* ����, �ְ��� ����ó�� */
    						    else if (taxform.getMap("TAX_GBN").equals("2")){
    						    	
    						    	pay_gbn = sqlService_cyber.getOneFieldInteger("DF502001.TX2221_TB_PAY", taxform);
    						    	
    						    	pay_cnt = pay_gbn;
    						    	
    						    	//if (pay_gbn == 0) {
    						    	//	pay_cnt = 0;
    						    	//} else {
    						    	//	pay_cnt = sqlService_cyber.getOneFieldInteger("DF502001.TX2221_TB_MaxPayCnt", taxform);
    						    	//}
    						    		
    						    	taxform.setMap("PAY_CNT", pay_cnt);
    						    	
    						    	intLog = sqlService_cyber.queryForUpdate("DF502001.INSERT_TX2221_TB_EPAY", taxform);
    						    	
    								if(intLog > 0) {
    									
    									sqlService_cyber.queryForUpdate("DF502001.UPDATE_TX2122_TB_NAPBU_INFO", taxform);
    									
    									log.info("�� ����. �ְ��� ���ڳ���ó�� �Ϸ�! [" + mf.getMap("TAX_NO") + "]");
    									
//    									MapForm mpSMS = new MapForm();	
//    									
//    									mpSMS.setMap("REG_NO"  , taxform.getMap("REG_NO"));
//    									mpSMS.setMap("TAX_ITEM", taxform.getMap("TAX_ITEM"));
//    									mpSMS.setMap("SUM_RCP" , taxform.getMap("SUM_RCP"));
//    									
//    									try {
//    //										sqlService_cyber.procedure("CODMBASE.SMSCALL", mpSMS);
//    									}catch (Exception e) {
//    										log.info("SMS ��ϵ����� = " + mpSMS.getMaps());
//    										log.info("SMS ��Ͽ��� �߻�");
//    									}
    								}
    						    }
    						} else {
    							
    							resCode = "7000";
    							//�ڵ���ü������ ����ó�� �Ұ�...
    						}
			            } else {
                            resCode = "4000";        // ���αݾ� Ʋ��
                        }
                        
					} else { 
						/* ��ȸ�Ǽ��� ���� ��� �������� */
						resCode = "5020";  /*��ȸ��������*/
						
					} 
					
				}
				
			}

			log.info(cUtil.msgPrint("", "", "DP502001 chk_df_502001()", resCode));
			
        } catch (Exception e) {
			
        	//20110829 �߰�
        	resCode = "9090";
        	
			e.printStackTrace();
			log.error("============================================");
			log.error("== chk_df_502001 Exception(�ý���) ");
			log.error("============================================");
		}
        
//        if(!resCode.equals("0000")) {
//        	sendMap = mf;
//        }
        
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

