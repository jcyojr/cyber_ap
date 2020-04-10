/**
 *  �ֽý��۸� : ���̹����漼û ��ȭ
 *  ��  ��  �� : ���漼 ������������
 *  ��  ��  �� : �����-���̹���û 
 *               ����ó��
 *               ��������� ���ŵ� ������ ���ؼ� ������������Ʈ�� �����ϰ� 
 *               ��ȸ����� �����Ѵ�.
 *  Ŭ����  ID : Bs502001Service
 *  ����  �̷� :
 * ------------------------------------------------------------------------
 *  �ۼ���     �Ҽ�  		 ����      Tag   ����
 * ------------------------------------------------------------------------   
 * Ȳ����   ��ä��(��)    2011.06.13   %01%  �ű��ۼ�
 *  
 */
package com.uc.bs.cyber.service.bs502001;

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
public class Bs502001Service {

	protected Log log = LogFactory.getLog(this.getClass());
	
	private ApplicationContext appContext = null;
	
	private IbatisBaseService sqlService_cyber = null;
		
	private Bs502001FieldList bsField = null;
	
	private int[] svcTms = new int[2];
	
	CbUtil cUtil = null;
	
	/**
	 * ������
	 */
	public Bs502001Service(ApplicationContext appContext) {
		// TODO Auto-generated constructor stub
		
		setAppContext(appContext);
		
		bsField = new Bs502001FieldList();
		
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
	public byte[] chk_bs_502001(MapForm headMap, MapForm mf) throws Exception {
		
		/**
		 * (�������)
		 * ���ý�(�λ�����)�� ���ý��� ����ó�� ����� ���̷� ����
		 * �ߺ������� �߻��� ������ ����.
		 * ���� ���ý������� �������� ������ �ش� �����ǿ� ���� ����������� �۽��ϰ�
		 * ������ �Ұ��Ѵ�.
		 * ��) ���ý����� ������ ���� ��� �λ����࿡�� ������ �̷������ ������ �������� 
		 * ������ ó���Ѵ�. 
		 * ���Ŀ� ������  �ΰ��ڷ��� ������ �����ϱ� ����.
		 * ���ý� -- ����(����ó������) �� �Ϲ����� �뺸(��������� ������ ���� ����)
		 * ���ý� -- �۽� �� �۽������� ���� ����ó�� ��
		 * 
		 */
		
		/*
		 * ���ý� �����ð� : 07:00 ~ 22:00
		 * */
				
		MapForm sendMap = new MapForm();
		
		/*���ܼ��� ����������ȸ �� ��� */
		String resCode = "000";       /*�����ڵ�*/
//		String giro_no = "1000685";   /*�λ�������ڵ�*/
		String strTaxGubun = "";
		String sg_code = "26";        /*�λ�ñ���ڵ�*/
		int curTime = 0;
       
		try{
			
			/*���� �ð�*/
			svcTms[0] = 0700;
			svcTms[1] = 2200;
			
			cUtil = CbUtil.getInstance();
			
			log.info(cUtil.msgPrint("", "S", "BP502001 chk_bs_502001()", resCode));
			
			
			/*����üũ*/
			/*���*/
			if(!headMap.getMap("GPUB_CODE").equals(sg_code)){
				resCode = "122";
			}
			/*�����������ڵ�*/
			strTaxGubun = (String) sqlService_cyber.queryForBean("CODMBASE.GIRO_FOR_TAX", headMap);
//			if(!headMap.getMap("GJIRO_NO").equals(giro_no)){
			if(!("1".equals(strTaxGubun))){
				resCode = "123";
			}
			/* ��ü�뺸�� ���νð� üũ�� �ǹ̰� ����.
			curTime = Integer.parseInt(headMap.getMap("TX_DATE").toString().substring(6, 10));
			
			if (curTime >= svcTms[0] && curTime <= svcTms[1]) {
				
			} else {
				resCode = "092";
			}
			 */			
			if(resCode.equals("000")){
				
				/* ��ȸ���� �ڸ���(���ι�ȣ �߶� ��ȸ���� �����) */
				mf.setMap("SGG_COD", mf.getMap("ETAXNO").toString().substring(0, 3)); 	//��û�ڵ�
				mf.setMap("ACCT_COD", mf.getMap("ETAXNO").toString().substring(4, 6));  //ȸ���ڵ�
				mf.setMap("TAX_ITEM", mf.getMap("ETAXNO").toString().substring(6, 12)); //����/�����ڵ�
				mf.setMap("TAX_YY", mf.getMap("ETAXNO").toString().substring(12, 16));  //�����⵵
				mf.setMap("TAX_MM", mf.getMap("ETAXNO").toString().substring(16, 18));	//������
				mf.setMap("TAX_DIV", mf.getMap("ETAXNO").toString().substring(18, 19));	//����ڵ�
				mf.setMap("HACD", mf.getMap("ETAXNO").toString().substring(19, 22));	//�������ڵ�
				mf.setMap("TAX_SNO", mf.getMap("ETAXNO").toString().substring(22, 28));	//������ȣ
				
				log.debug("TAXNO    = [" + mf.getMap("ETAXNO") + "]");
				log.debug("JUMIN_NO = [" + mf.getMap("JUMIN_NO") + "]");
				log.debug("SGG_COD  = [" + mf.getMap("SGG_COD") + "]");
				log.debug("ACCT_COD = [" + mf.getMap("ACCT_COD") + "]");
				log.debug("TAX_ITEM = [" + mf.getMap("TAX_ITEM") + "]");
				log.debug("TAX_YY   = [" + mf.getMap("TAX_YY") + "]");
				log.debug("TAX_MM   = [" + mf.getMap("TAX_MM") + "]");
				log.debug("TAX_DIV  = [" + mf.getMap("TAX_DIV") + "]");
				log.debug("HACD     = [" + mf.getMap("HACD") + "]");
				log.debug("TAX_SNO  = [" + mf.getMap("TAX_SNO") + "]");
				
				ArrayList<MapForm>  bsCmd502001List  =  sqlService_cyber.queryForList("BS502001.SELECT_TAX_SEARCH_LIST", mf);
				
				log.info("bsCmd502001List.size() = [" + bsCmd502001List.size() +"]");
				
		        /* Error : ������������ */
				if(bsCmd502001List.size() <= 0){
					resCode = "111";  
				} 
				/* Error : �������� 2�� �̻� */
				else if (bsCmd502001List.size() > 1) {
					resCode = "201";  
				} else {
					
					/* ��ȸ��� ��� ���� */
					MapForm taxform = new MapForm();
					
					if ( bsCmd502001List.size() > 0 ) {
						
						taxform = bsCmd502001List.get(0);

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
		                		/*
		                		/* ���� ����� 
		                		taxform.setMap("MNTX_ADTX", cUtil.getGasanAmt(napkiGubun
		                													, taxform.getMap("TAX_YY").toString() + taxform.getMap("TAX_MM").toString() 
		                													, Long.parseLong(taxform.getMap("MNTX").toString())));
		                		/* ���ð�ȹ�� ����� 
		                		taxform.setMap("CPTX_ADTX", cUtil.getGasanAmt(napkiGubun
																			, taxform.getMap("TAX_YY").toString() + taxform.getMap("TAX_MM").toString() 
																			, Long.parseLong(taxform.getMap("CPTX").toString())));
		                		/* �����ü��� ����� 
		                		taxform.setMap("CFTX_ADTX", cUtil.getGasanAmt(napkiGubun
																			, taxform.getMap("TAX_YY").toString() + taxform.getMap("TAX_MM").toString() 
																			, Long.parseLong(taxform.getMap("CFTX").toString())));
		                		/* ������ ����� 
		                		taxform.setMap("LETX_ADTX", cUtil.getGasanAmt(napkiGubun
																			, taxform.getMap("TAX_YY").toString() + taxform.getMap("TAX_MM").toString() 
																			, Long.parseLong(taxform.getMap("LETX").toString())));
		                		/* ��Ư�� ����� 
		                		taxform.setMap("ASTX_ADTX", cUtil.getGasanAmt(napkiGubun
																		, taxform.getMap("TAX_YY").toString() + taxform.getMap("TAX_MM").toString() 
																			, Long.parseLong(taxform.getMap("ASTX").toString())));  
								*/						
		                		
		                	} else {
		                		/* ���αݾ� */
		                		taxform.setMap("SUM_RCP", cUtil.getNapAmt(Long.parseLong(taxform.getMap("MNTX").toString())				//����
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
						
						
						/*
			            if (Long.parseLong(mf.getMap("NAPBU_AMT").toString()) == Long.parseLong(taxform.getMap("SUM_RCP").toString()))
			            	resCode = "000";        // ����
			            else
			            	resCode = "126";        // ���αݾ� Ʋ��
			           	*/
			            
						taxform.setMap("JUMIN_NO", taxform.getMap("REG_NO"));   /*�ֹι��ι�ȣ */
						taxform.setMap("SGG_COD" , taxform.getMap("SGG_COD"));  /*��û�ڵ� */     
						taxform.setMap("ACCT_COD", taxform.getMap("ACCT_COD")); /*ȸ���ڵ� */   
						taxform.setMap("TAX_ITEM", taxform.getMap("TAX_ITEM")); /*������   */  
					    taxform.setMap("TAX_YY", taxform.getMap("TAX_YY"));     /*�ΰ��⵵ */    
					    taxform.setMap("TAX_MM", taxform.getMap("TAX_MM"));     /*�ΰ���   */  
					    taxform.setMap("TAX_DIV", taxform.getMap("TAX_DIV"));   /*���     */ 
					    taxform.setMap("HACD", taxform.getMap("HACD"));       	/*������   */ 
					    taxform.setMap("TAX_SNO", taxform.getMap("TAX_SNO"));   /*������ȣ */   
					    taxform.setMap("RCP_CNT", taxform.getMap("RCP_CNT"));   /*�г����� */   
					    taxform.setMap("SNTG", "1");       						/*����FLAG */    
					    taxform.setMap("SUM_RCP", taxform.getMap("SUM_RCP"));  	/*�����ݾ� */   
					    taxform.setMap("PAY_DT" , mf.getMap("NAPBU_DATE"));     /*�������� */     
					    taxform.setMap("SNSU", "3");       						/*��������(�λ����� = '3') */    
					    //���� ���α���� ������ H�� �Ǿ� �ֱ⶧���� ��'0'�� �����ǹǷ� �ݵ�� �߰���...
					    taxform.setMap("BANK_CD", CbUtil.lPadString(mf.getMap("OUTBANK_CODE").toString(), 7, '0').substring(0, 3));   /*���α�� */     
					    taxform.setMap("BRC_NO" , CbUtil.lPadString(mf.getMap("OUTBANK_CODE").toString(), 7, '0'));    /*��������ڵ�    */  
					    taxform.setMap("TMSG_NO", headMap.getMap("BCJ_NO"));    /*�������_����������ȣ */   
					    taxform.setMap("RSN_YN" , (mf.getMap("NAPBU_GUBUN").equals("R")) ? "Y" : "N");       
					    
					    int pay_cnt = 0, intLog = 0, pay_gbn = 0;
					    
					    /**
						 * 20110726 : �ڵ���ü ��ϰ��� ����ó���Ұ�
						 */
						if(!taxform.getMap("AUTO_TRNF_YN").equals("Y")) {
							
							/* ���漼 ����ó�� */
						    if(taxform.getMap("TAX_GBN").equals("1")){
						    	
						    	/*��� ��ü�� ���� ���ΰǼ��� �ִ��� Ȯ��*/
						    	pay_gbn = sqlService_cyber.getOneFieldInteger("BS502001.TX1201_TB_PAY", taxform);
						    	
						    	pay_cnt = pay_gbn;
						    	
						    	//if (pay_gbn == 0) {
						    	//	pay_cnt = 0;
						    	//} else {
						    	//	pay_cnt = sqlService_cyber.getOneFieldInteger("BS502001.TX1201_TB_MaxPayCnt", taxform);
						    	//}

						    	taxform.setMap("PAY_CNT", pay_cnt);
						    	
						    	intLog = sqlService_cyber.queryForUpdate("BS502001.INSERT_TX1201_TB_EPAY", taxform);
						    	
								if(intLog > 0) {
									
									sqlService_cyber.queryForUpdate("BS502001.UPDATE_TX1102_TB_NAPBU_INFO", taxform);
									
									log.info("���漼 ���ڳ���ó�� �Ϸ�! [" + mf.getMap("ETAXNO") + "]");
									
									
									MapForm mpSMS = new MapForm();	
									
									mpSMS.setMap("REG_NO"  , taxform.getMap("REG_NO"));
									mpSMS.setMap("TAX_ITEM", taxform.getMap("TAX_ITEM"));
									mpSMS.setMap("SUM_RCP" , taxform.getMap("SUM_RCP"));
									
									try {
										sqlService_cyber.procedure("CODMBASE.SMSCALL", mpSMS);
									}catch (Exception e) {
										log.info("SMS ��ϵ����� = " + mpSMS.getMaps());
										log.info("SMS ��Ͽ��� �߻�");
									}
									
								}
						    }
						    /* ����, �ְ��� ����ó�� */
						    else if (taxform.getMap("TAX_GBN").equals("2")){
						    	
						    	pay_gbn = sqlService_cyber.getOneFieldInteger("BS502001.TX2221_TB_PAY", taxform);
						    	
						    	pay_cnt = pay_gbn;
						    	
						    	//if (pay_gbn == 0) {
						    	//	pay_cnt = 0;
						    	//} else {
						    	//	pay_cnt = sqlService_cyber.getOneFieldInteger("BS502001.TX2221_TB_MaxPayCnt", taxform);
						    	//}
						    		
						    	taxform.setMap("PAY_CNT", pay_cnt);
						    	
						    	intLog = sqlService_cyber.queryForUpdate("BS502001.INSERT_TX2221_TB_EPAY", taxform);
						    	
								if(intLog > 0) {
									
									sqlService_cyber.queryForUpdate("BS502001.UPDATE_TX2122_TB_NAPBU_INFO", taxform);
									
									log.info("����. �ְ��� ���ڳ���ó�� �Ϸ�! [" + mf.getMap("ETAXNO") + "]");
									
									MapForm mpSMS = new MapForm();	
									
									mpSMS.setMap("REG_NO"  , taxform.getMap("REG_NO"));
									mpSMS.setMap("TAX_ITEM", taxform.getMap("TAX_ITEM"));
									mpSMS.setMap("SUM_RCP" , taxform.getMap("SUM_RCP"));
									
									try {
										sqlService_cyber.procedure("CODMBASE.SMSCALL", mpSMS);
									}catch (Exception e) {
										log.info("SMS ��ϵ����� = " + mpSMS.getMaps());
										log.info("SMS ��Ͽ��� �߻�");
									}
								}    	
						    	
						    }	    
						    
						} else {
							
							resCode = "336";
							//�ڵ���ü������ ����ó�� �Ұ�...
						}
					           
			            
					}else{ 
						/* ��ȸ�Ǽ��� ���� ��� ��������
						 * */
						resCode = "111";  /*��ȸ��������*/
						
					} 
					
				}
				
			}

			log.info(cUtil.msgPrint("", "", "BP502001 chk_bs_502001()", resCode));
			
        } catch (Exception e) {
			
        	//20110829 �߰�
        	resCode = "093";
        	
			e.printStackTrace();
			log.error("============================================");
			log.error("== chk_bs_502001 Exception(�ý���) ");
			log.error("============================================");
		}
        
        if(!resCode.equals("000")) {
        	sendMap = mf;
        }
        
        /*���⼭ ������带 �����Ѵ�. �ٲ�� �κи� �ٽ� �����ؼ� ������ ����*/
        headMap.setMap("RS_FLAG"   , "G");         /*�����̿���(G) */
        headMap.setMap("RESP_CODE" , resCode);     /*�����ڵ�*/
        headMap.setMap("GCJ_NO"    , "0" + sg_code + "0" + CbUtil.lPadString(String.valueOf(SeqNumber()), 8, '0'));     /*�̿��� �Ϸù�ȣ*/
        headMap.setMap("TX_GUBUN"  , "0210");
        
        return bsField.makeSendBuffer(headMap, sendMap);
	}

	/*----------------------------------------------*/
	/* �Ϸù�ȣ �������� �׽�Ʈ*/
	/*----------------------------------------------*/
	private int SeqNumber(){
    	return (Integer)sqlService_cyber.queryForBean("CODMBASE.CODM_GETSEQNO", "00");
    }

}

