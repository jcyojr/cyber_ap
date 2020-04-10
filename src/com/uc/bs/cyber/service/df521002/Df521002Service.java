/**
 *  �ֽý��۸� : ���̹����漼û ��ȭ
 *  ��  ��  �� : ���漼 �������� ����ȸ ����
 *  ��  ��  �� : ������(����)-���̹���û 
 *  ����ó��   :
 *               ������(����)���� ���ŵ� ������ ���ؼ� ������������Ʈ�� �����ϰ� 
 *               ��ȸ����� �����Ѵ�.
 *  Ŭ����  ID : Df521001Service
 *  ����  �̷� :
 * ------------------------------------------------------------------------
 *  �ۼ���     �Ҽ�  		 ����      Tag   ����
 * ------------------------------------------------------------------------   
 * ��â��  ��ä��(��)    2013.08.13   %01%  �ű��ۼ�
 *  
 */
package com.uc.bs.cyber.service.df521002;

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
public class Df521002Service {

	protected Log log = LogFactory.getLog(this.getClass());
	
	private ApplicationContext appContext = null;
	
	private IbatisBaseService sqlService_cyber = null;
		
	private Df201002FieldList dfField = null;
	
	CbUtil cUtil = null;
	
	/**
	 * ������
	 */
	public Df521002Service(ApplicationContext appContext) {
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
	 * ���漼 �������� ����ȸ ó��...
	 * */
	public byte[] chk_df_521002(MapForm headMap, MapForm mf) throws Exception {
		
				
		MapForm sendMap = new MapForm();
		
		/*���漼 ����������ȸ �� ��� */
		String resCode = "0000";      /*�����ڵ�*/
//		String giro_no = "1000685";   /*�λ�������ڵ�*/
		String tx_id = "SN2601";
		String sg_code = "26";        /*�λ�ñ���ڵ�*/
		
		try{
			cUtil = CbUtil.getInstance();
			
			log.info(cUtil.msgPrint("7", "S","DP521002 chk_df_521002()", resCode));
			
			/*����üũ*/
			/*���*/
			if(!headMap.getMap("SIDO_CODE").equals(sg_code)){
				resCode = "1000";
			}
			/*�����������ڵ�*/
			if(!headMap.getMap("TX_ID").equals(tx_id)){
				resCode = "1000";
			}
			
			if(mf.getMap("EPAY_NO").toString().length() != 19) {
                resCode = "2000";
            }
            
            if(mf.getMap("TAX_NO").toString().length() != 31) {
                resCode = "2000";
            }
            
			if (resCode.equals("0000")) {

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
                
	            
	            /*���漼 ���ڼ����� ��ȸ�Ͽ� �ⳳ�ο��θ� Ȯ���Ѵ�.*/
                ArrayList<MapForm>  dfCmdEpayList  =  sqlService_cyber.queryForList("DF521002.SELECT_TAX_PAY_LIST", mf);
                
                int payCnt = dfCmdEpayList.size();
                
                log.debug("payCnt = " + payCnt);
                
                if(payCnt == 0) {
                    resCode = "0000";   /*���γ�������*/
                } else {
                    /*���γ��� ����*/
                    MapForm mfCmdEpayList = dfCmdEpayList.get(0);
                    
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
                
                /*���漼 �ΰ������� ��ȸ�Ѵ�.*/
	            ArrayList<MapForm>  dfCmd521002List  =  sqlService_cyber.queryForList("DF521002.SELECT_TAX_SEARCH_LIST", mf);
                
                log.debug("TAX_NO = [" + mf.getMap("TAX_NO") + "]");
                log.debug("EPAY_NO = [" + mf.getMap("EPAY_NO") + "]");
                
                if(dfCmd521002List.size() <= 0){
                    resCode = "5020";  // Error : ������������
                } else if (dfCmd521002List.size() > 1){
                    resCode = "5060";  // Error : �������� 2�� �̻�
                }
                
				if ( dfCmd521002List.size() > 0 && resCode.equals("0000")) {
					
					resCode = "0000";
					
					MapForm mfCmd521002List  =  dfCmd521002List.get(0);
					
					/*
					 *  �պκ� ���� 
					 */
					
					/**
					 * 20110727 : �ڵ���ü ��ϰ��� ����ó�� �� �� ���� ��...
					 */
					if(mfCmd521002List.getMap("AUTO_TRNF_YN").equals("Y")) {
						log.info("==============�ڵ���ü��ϰ�(����ó���Ұ�)================");
//						resCode = "7000";
					}
					
					/* �ڵ���ü ��Ͽ��� */
//					sendMap.setMap("AUTO_TRNF_YN", mfCmd521002List.getMap("AUTO_TRNF_YN"));
					/* ���ι�ȣ */
					sendMap.setMap("TAX_GB", mf.getMap("TAX_GB"));
					/* �Ϸù�ȣ */
					sendMap.setMap("TAX_NO", mf.getMap("TAX_NO"));
					/* �ֹ�(����,�����) ��ȣ */
					sendMap.setMap("EPAY_NO", mf.getMap("EPAY_NO"));
	                /* �����ڼ��� */
                    sendMap.setMap("NAP_NAME", mfCmd521002List.getMap("REG_NM"));
					/* �ΰ���� ��*/
					sendMap.setMap("SGG_NAME", mfCmd521002List.getMap("SGNM"));
//					/* ���񼼸�� */
//					sendMap.setMap("SEMOK_CD", mfCmd521002List.getMap("TAX_ITEM"));
					sendMap.setMap("TAX_NAME", mfCmd521002List.getMap("TAX_NM"));
					/* ������� */
					sendMap.setMap("TAX_YM", mfCmd521002List.getMap("TAX_YM"));
					/* ��� */
					sendMap.setMap("TAX_DIV", mfCmd521002List.getMap("TAX_DIV"));
					
					/*
					 * �պκ� ���볡
					 */
					
					/* ���ⱸ�� */
					String napGubun = cUtil.getNapkiGubun((String)mfCmd521002List.getMap("DUE_DT"));
					
					/* ����, �ְ��� ���� */
	                if(mfCmd521002List.getMap("TAX_GBN").equals("2")){
	                	/* ���⳻�ݾ� (���⳻�ݾ� �����ıݾ� ��ġ) */
	                	sendMap.setMap("NAP_BFAMT", mfCmd521002List.getMap("AMT"));
	                	/* �����ıݾ� */
	                	sendMap.setMap("NAP_AFAMT", sendMap.getMap("NAP_BFAMT"));
	                	/* ���� �����ؾ��� �ݾ� (������ �ݾװ� ���⳻ �ݾ��� ����) */
	                	sendMap.setMap("NAP_AMT", sendMap.getMap("NAP_BFAMT"));
	                	
	                	/* ���ⱸ���� ������ B */
	                	napGubun = "B";
	                	
	                	/* ���⳻���� */
	                	sendMap.setMap("NAP_BFDATE", mfCmd521002List.getMap("DUE_DT"));
	                	/* ���������� */
	                	sendMap.setMap("NAP_AFDATE", mfCmd521002List.getMap("DUE_F_DT"));
	                	/*�ΰ�����*/
	                	sendMap.setMap("TAX_DESC", mfCmd521002List.getMap("MLGN"));
	                	/* �������� 2 */
		                sendMap.setMap("FILLER1", "");	
	                	
	                }
	                /* ���漼 */
	                else {
	                	/* ü��(DLQ_DIV= 1:�ΰ� 2:ü��)�и��� ����� 3(�ڳ�) �ƴҶ� ���⳻�ݾ� �� �����ıݾ� ��� */
	                	if (mfCmd521002List.getMap("DLQ_DIV").equals("1") && !mfCmd521002List.getMap("TAX_DIV").equals("3")){
	                		
	                	    
	                		/* ���⳻�ݾ� */
	                		sendMap.setMap("NAP_BFAMT", cUtil.getNapBfAmt(Long.parseLong(mfCmd521002List.getMap("MNTX").toString())  	//����
	                													, Long.parseLong(mfCmd521002List.getMap("CPTX").toString())		//���ð�ȸ
	                													, Long.parseLong(mfCmd521002List.getMap("CFTX").toString())		//�����ü���
	                													, Long.parseLong(mfCmd521002List.getMap("LETX").toString())		//������
	                													, Long.parseLong(mfCmd521002List.getMap("ASTX").toString())));	//��Ư��
	                		/* �����ıݾ� */
	                		sendMap.setMap("NAP_AFAMT", cUtil.getNapAfAmt(mfCmd521002List.getMap("TAX_DT").toString()					//�ΰ�����
											                			, Long.parseLong(mfCmd521002List.getMap("MNTX").toString())  	//����
																		, Long.parseLong(mfCmd521002List.getMap("CPTX").toString())		//���ð�ȸ
																		, Long.parseLong(mfCmd521002List.getMap("CFTX").toString())		//�����ü���
																		, Long.parseLong(mfCmd521002List.getMap("LETX").toString())		//������
																		, Long.parseLong(mfCmd521002List.getMap("ASTX").toString())));	//��Ư��
                            /* ���� �����ؾ��� �ݾ�*/
                            if ("B".equals(napGubun)) {
                                sendMap.setMap("NAP_AMT", sendMap.getMap("NAP_BFAMT"));
                            } else {
                                sendMap.setMap("NAP_AMT", sendMap.getMap("NAP_AFAMT"));
                            }
	                		
	                		/* ���⳻���� */
	                		sendMap.setMap("NAP_BFDATE", mfCmd521002List.getMap("DUE_DT"));
	                		/* ���������� */
	                		sendMap.setMap("NAP_AFDATE", mfCmd521002List.getMap("DUE_F_DT"));
	                		
	                	} else {
	                		/* ���⳻�ݾ� */
	                		sendMap.setMap("NAP_BFAMT", cUtil.getNapBfAmt(Long.parseLong(mfCmd521002List.getMap("MNTX").toString())			//����
											                		    , Long.parseLong(mfCmd521002List.getMap("MNTX_ADTX").toString())  	//���� �����
																		, Long.parseLong(mfCmd521002List.getMap("CPTX").toString())			//���ð�ȸ��
																		, Long.parseLong(mfCmd521002List.getMap("CPTX_ADTX").toString())	//���ð�ȸ�� �����
																		, Long.parseLong(mfCmd521002List.getMap("CFTX").toString())			//�����ü���
																		, Long.parseLong(mfCmd521002List.getMap("CFTX_ADTX").toString())	//�����ü��� �����
																		, Long.parseLong(mfCmd521002List.getMap("LETX").toString())			//������
																		, Long.parseLong(mfCmd521002List.getMap("LETX_ADTX").toString())	//������ �����
																		, Long.parseLong(mfCmd521002List.getMap("ASTX").toString())			//��Ư��
																		, Long.parseLong(mfCmd521002List.getMap("ASTX_ADTX").toString())));	//��Ư�� �����
	                		napGubun = "B";
	                		
	                		/* �����ıݾ� (������ �ݾװ� ���⳻ �ݾ��� ����) */
	                		sendMap.setMap("NAP_AFAMT", sendMap.getMap("NAP_BFAMT"));
                            /* ���� �����ؾ��� �ݾ� (������ �ݾװ� ���⳻ �ݾ��� ����) */
                            sendMap.setMap("NAP_AMT", sendMap.getMap("NAP_BFAMT"));
	                		/* ���⳻���� */
	                		sendMap.setMap("NAP_BFDATE", mfCmd521002List.getMap("DUE_DT"));
	                		/* ���������� */
	                		// sendMap.setMap("DUE_F_DT", mfCmd521002List.getMap("DUE_DT")); -- ���������� ó������ ����--2011.09.14 --����---
	                		sendMap.setMap("NAP_AFDATE", mfCmd521002List.getMap("DUE_F_DT"));
	                		
	                	}
		                /*
		                 * ���漼 �޺κ� ����
		                 */
	                	if (mfCmd521002List.getMap("DLQ_DIV").equals("2")) {
	                	    sendMap.setMap("TAX_DIV", "0");   //ü�����϶�
	                	}
		                /* �������� + ����ǥ�ؼ���  */
		                sendMap.setMap("TAX_DESC", mfCmd521002List.getMap("MLGN"));
		                /* �������� 2 */
		                sendMap.setMap("FILLER1", "");
		                /*
		                 * ���漼 ��
		                 */
	                }
					
				} else { 
					/* ��ȸ�Ǽ��� ���� ��� ��������
					 * */
					resCode = "5020";  /*��ȸ��������*/
					
				} 
			}
			
			log.info(cUtil.msgPrint("7", "","DP521002 chk_df_521002()", resCode));
			
        } catch (Exception e) {
        	
        	resCode = "9090";  
        	
			e.printStackTrace();
			log.error("============================================");
			log.error("== chk_df_521002 Exception(�ý���) ");
			log.error("============================================");
		}
        
        /*�����ΰ�� ���� ������*/
        if(!resCode.equals("0000")) {
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

