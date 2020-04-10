/**
 *  �ֽý��۸� : ���̹����漼û ��ȭ
 *  ��  ��  �� : ���漼 �������� ����ȸ ����
 *  ��  ��  �� : �λ�����-���̹���û 
 *               ����ó��
 *               ��������� ���ŵ� ������ ���ؼ� ������������Ʈ�� �����ϰ� 
 *               ��ȸ����� �����Ѵ�.
 *  Ŭ����  ID : Bs521001Service
 *  ����  �̷� :
 * ------------------------------------------------------------------------
 *  �ۼ���     �Ҽ�  		 ����      Tag   ����
 * ------------------------------------------------------------------------   
 * Ȳ����   �ٻ�(��)    2011.06.13   %01%  �ű��ۼ�
 *  
 */
package com.uc.bs.cyber.service.bs521002;

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
public class Bs521002Service {

	protected Log log = LogFactory.getLog(this.getClass());
	
	private ApplicationContext appContext = null;
	
	private IbatisBaseService sqlService_cyber = null;
		
	private Bs521002FieldList bsField = null;
	
	CbUtil cUtil = null;
	
	/**
	 * ������
	 */
	public Bs521002Service(ApplicationContext appContext) {
		// TODO Auto-generated constructor stub
		
		setAppContext(appContext);
		
		bsField = new Bs521002FieldList();
		
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
	public byte[] chk_bs_521002(MapForm headMap, MapForm mf) throws Exception {
		
				
		MapForm sendMap = new MapForm();
		
		/*���漼 ����������ȸ �� ��� */
		String resCode = "000";       /*�����ڵ�*/
//		String giro_no = "1000685";   /*�λ�������ڵ�*/
		String strTaxGubun = "";
		String sg_code = "26";        /*�λ�ñ���ڵ�*/
		
		try{
			cUtil = CbUtil.getInstance();
			
			log.info(cUtil.msgPrint("7", "S","BP521002 chk_bs_521002()", resCode));
			
			/*����üũ*/
			/*���*/
			if(!headMap.getMap("GPUB_CODE").equals(sg_code)){
				resCode = "122";
			}
			/*�����������ڵ�*/
			strTaxGubun = (String) sqlService_cyber.queryForBean("CODMBASE.GIRO_FOR_TAX", headMap);
//          if(!headMap.getMap("GJIRO_NO").equals(giro_no)){
            if(!("1".equals(strTaxGubun))){
              resCode = "123";
            }
			
			if (resCode.equals("000")) {
				
				mf.setMap("SGG_COD" , mf.getMap("TAXNO").toString().substring(0, 3)); 	//��û�ڵ�
				mf.setMap("ACCT_COD", mf.getMap("TAXNO").toString().substring(4, 6));   //ȸ���ڵ�
				mf.setMap("TAX_ITEM", mf.getMap("TAXNO").toString().substring(6, 12));  //����/�����ڵ�
				mf.setMap("TAX_YY"  , mf.getMap("TAXNO").toString().substring(12, 16)); //�����⵵
				mf.setMap("TAX_MM"  , mf.getMap("TAXNO").toString().substring(16, 18));	//������
				mf.setMap("TAX_DIV" , mf.getMap("TAXNO").toString().substring(18, 19));	//����ڵ�
				mf.setMap("HACD"    , mf.getMap("TAXNO").toString().substring(19, 22));	//�������ڵ�
				mf.setMap("TAX_SNO" , mf.getMap("TAXNO").toString().substring(22, 28));	//������ȣ
				
				
				log.info(mf.getMap("SGG_COD"));
				log.info(mf.getMap("ACCT_COD"));
				log.info(mf.getMap("TAX_ITEM"));
				log.info(mf.getMap("TAX_YY"));
				log.info(mf.getMap("TAX_MM"));
				log.info(mf.getMap("TAX_DIV"));
				log.info(mf.getMap("HACD"));
				log.info(mf.getMap("TAX_SNO"));
				
				ArrayList<MapForm>  bsCmd521002List  =  sqlService_cyber.queryForList("BS521002.SELECT_TAX_SEARCH_LIST", mf);
				
				log.debug("TAXNO = [" + mf.getMap("TAXNO") + "]");
				log.debug("JUMIN_NO = [" + mf.getMap("JUMIN_NO") + "]");
				
				if(bsCmd521002List.size() <= 0){
					resCode = "111";  // Error : ������������
				} else if (bsCmd521002List.size() > 1){
					resCode = "201";  // Error : �������� 2�� �̻�
				}
				

				if ( bsCmd521002List.size() > 0 && resCode.equals("000")) {
					
					resCode = "000";
					
					MapForm mfCmd521001List  =  bsCmd521002List.get(0);
					
					
					log.info(mfCmd521001List.getMap("SGG_COD").toString());
					log.info(mfCmd521001List.getMap("DUE_DT").toString());
					
					
					
					/*
					 *  �պκ� ���� 
					 */
					
					/**
					 * 20110727 : �ڵ���ü ��ϰ��� ����ó�� �� �� ���� ��...
					 */
					if(mfCmd521001List.getMap("AUTO_TRNF_YN").equals("Y")) {
						log.info("==============�ڵ���ü��ϰ�(����ó���Ұ�)================");
					}
					
					/* �ڵ���ü ��Ͽ��� */
					sendMap.setMap("AUTO_TRNF_YN", mfCmd521001List.getMap("AUTO_TRNF_YN"));
					
					/* ���ι�ȣ */
					sendMap.setMap("TAXNO", mfCmd521001List.getMap("TAXNO"));

					/* �Ϸù�ȣ */
					sendMap.setMap("SEQNO", mf.getMap("SEQNO"));
					/* �ֹ�(����,�����) ��ȣ */
					sendMap.setMap("JUMIN_NO", mfCmd521001List.getMap("REG_NO"));
					/* �õ��ڵ� */
					sendMap.setMap("SIDO", "26");
					/* �ΰ���� */
					sendMap.setMap("BUGWA_GIGWAN", mfCmd521001List.getMap("SGG_COD"));
					/* ������ȣ 1 */
					sendMap.setMap("CHK1", mfCmd521001List.getMap("TAXNO").toString().substring(3, 4)); // �̰͵� �̻���
					/* ȸ�� */
					sendMap.setMap("ACC_CD", mfCmd521001List.getMap("ACCT_COD"));	
					/* ���񼼸� */
					sendMap.setMap("SEMOK_CD", mfCmd521001List.getMap("TAX_ITEM"));
					/* ������� */
					sendMap.setMap("ACC_YM", mfCmd521001List.getMap("TAX_YM"));
					/* ��� */
					sendMap.setMap("GIBN", mfCmd521001List.getMap("TAX_DIV"));
					/* ������ */
					sendMap.setMap("DONG_CD", mfCmd521001List.getMap("HACD"));
					/* ������ȣ */
					sendMap.setMap("TAX_SNO", mfCmd521001List.getMap("TAX_SNO"));
					/* ������ȣ 2 */
					sendMap.setMap("CHK2", mfCmd521001List.getMap("TAXNO").toString().substring(mfCmd521001List.getMap("TAXNO").toString().length() - 1)); 
					/* �����ڼ��� */
					sendMap.setMap("REG_NM", mfCmd521001List.getMap("REG_NM"));
					
					/*
					 * �պκ� ���볡
					 */
					
					/* ���ⱸ�� */
					String napGubun = cUtil.getNapkiGubun((String)mfCmd521001List.getMap("DUE_DT"));
					
					/* ����, �ְ��� ���� */
	                if(mfCmd521001List.getMap("TAX_GBN").equals("2")){
	                	/* ���⳻�ݾ� (���⳻�ݾ� �����ıݾ� ��ġ) */
	                	sendMap.setMap("NAP_BFAMT", mfCmd521001List.getMap("AMT"));
	                	/* �����ıݾ� */
	                	sendMap.setMap("NAP_AFAMT", sendMap.getMap("NAP_BFAMT"));
	                	
	                	/* ������ȣ 3 */
	                	sendMap.setMap("CHK3", cUtil.getGum3(sendMap.getMap("NAP_BFAMT").toString(), sendMap.getMap("NAP_AFAMT").toString()));
	                	
	                	/* ����ǥ�� */
	                	sendMap.setMap("KWA_AMT", Long.parseLong(mfCmd521001List.getMap("TAX_STD").toString()));
	                	
	                	/* ���� �ݾ� */
	            		sendMap.setMap("MNTX", "");			//����
	            		sendMap.setMap("MNTX_ADTX", "");	//���� �����
	            		sendMap.setMap("CPTX", "");			//���ð�ȹ��
	            		sendMap.setMap("CPTX_ADTX", "");	//���ð�ȹ�� �����
	            		sendMap.setMap("CFTX", ""); 		//�����ü���/��Ư��
	            		sendMap.setMap("CFTX_ADTX", ""); 	//�����ü���/��Ư�� �����
	            		sendMap.setMap("LETX", "");			//������
	            		sendMap.setMap("LETX_ADTX", "");	//������ �����	                		
	                	
	                	/* ���ⱸ���� ������ B */
	                	napGubun = "B";
	                	
	                	/* ���⳻���� */
	                	sendMap.setMap("DUE_DT", mfCmd521001List.getMap("DUE_DT"));
	                	/* ���������� */
	                	sendMap.setMap("DUE_F_DT", mfCmd521001List.getMap("DUE_DT"));
	                	
	                	/* ������ȣ 4 ���ϱ� */
	                	sendMap.setMap("CHK4", cUtil.getGum4(mfCmd521001List.getMap("MNTX").toString(), "", "", "", mfCmd521001List.getMap("DUE_DT").toString()));
		                /* �ʷ� */
		                sendMap.setMap("FILLER", "");
		                /* ������ȣ 5 */
		                sendMap.setMap("CHK5", "");
		                /* �������� + ����ǥ�ؼ���  */
		                sendMap.setMap("MLGN", "");
		                /* �ΰ����� */
		                sendMap.setMap("GIGI_DATE", mfCmd521001List.getMap("TAX_DT"));
		                /* �������������ڵ� */
		                sendMap.setMap("BANK_CD", "");
		                /* �����Ͻ� */
		                sendMap.setMap("RECIP_DATE", "");
		                /* ���ⱸ�� */
		                sendMap.setMap("NABGI_BA_GBN", napGubun);
		                /* �������� 2 */
		                sendMap.setMap("FIELD2", "");	
	                	
	                }
	                /* ���漼 */
	                else {
	                	/* ü��(DLQ_DIV= 1:�ΰ� 2:ü��)�и��� ����� 3(�ڳ�) �ƴҶ� ���⳻�ݾ� �� �����ıݾ� ��� */
	                	if (mfCmd521001List.getMap("DLQ_DIV").equals("1") && !mfCmd521001List.getMap("TAX_DIV").equals("3")){
	                		
	                		/* ���⳻�ݾ� */
	                		sendMap.setMap("NAP_BFAMT", cUtil.getNapBfAmt(Long.parseLong(mfCmd521001List.getMap("MNTX").toString())  		//����
	                														, Long.parseLong(mfCmd521001List.getMap("CPTX").toString())		//���ð�ȸ
	                														, Long.parseLong(mfCmd521001List.getMap("CFTX").toString())		//�����ü���
	                														, Long.parseLong(mfCmd521001List.getMap("LETX").toString())		//������
	                														, Long.parseLong(mfCmd521001List.getMap("ASTX").toString())));	//��Ư��
	                		/* �����ıݾ� */
	                		sendMap.setMap("NAP_AFAMT", cUtil.getNapAfAmt(mfCmd521001List.getMap("TAX_DT").toString()						//�ΰ�����
											                				, Long.parseLong(mfCmd521001List.getMap("MNTX").toString())  	//����
																			, Long.parseLong(mfCmd521001List.getMap("CPTX").toString())		//���ð�ȸ
																			, Long.parseLong(mfCmd521001List.getMap("CFTX").toString())		//�����ü���
																			, Long.parseLong(mfCmd521001List.getMap("LETX").toString())		//������
																			, Long.parseLong(mfCmd521001List.getMap("ASTX").toString())));	//��Ư��
		                	
			                /* ������ȣ 3 */
			                sendMap.setMap("CHK3", cUtil.getGum3(sendMap.getMap("NAP_BFAMT").toString(), sendMap.getMap("NAP_AFAMT").toString()));
			                /* ����ǥ�ؾ� */
			                sendMap.setMap("KWA_AMT", Long.parseLong(mfCmd521001List.getMap("TAX_STD").toString()));
	                		
	                		/* ���� */
	                		sendMap.setMap("MNTX", mfCmd521001List.getMap("MNTX"));
	                		/* ���� ����� */
	                		sendMap.setMap("MNTX_ADTX", cUtil.getGasanAmt(napGubun, mfCmd521001List.getMap("TAX_DT").toString(), Long.parseLong(mfCmd521001List.getMap("MNTX").toString())));
	                		/* ���ð�ȹ�� */
	                		sendMap.setMap("CPTX", mfCmd521001List.getMap("CPTX"));
	                		/* ���ð�ȹ�� ����� */
	                		sendMap.setMap("CPTX_ADTX", cUtil.getGasanAmt(napGubun, mfCmd521001List.getMap("TAX_DT").toString(), Long.parseLong(mfCmd521001List.getMap("CPTX").toString())));
	                		/* �����ü���/��Ư�� */
	                		sendMap.setMap("CFTX", (Long.parseLong(mfCmd521001List.getMap("CFTX").toString()) + Long.parseLong(mfCmd521001List.getMap("ASTX").toString())));
	                		/* �����ü���/��Ư�� ����� */
	                		sendMap.setMap("CFTX_ADTX", cUtil.getGasanAmt(napGubun, mfCmd521001List.getMap("TAX_DT").toString(),
	                													  Long.parseLong(mfCmd521001List.getMap("CFTX").toString()) +
	                													  cUtil.getGasanAmtNont(napGubun, mfCmd521001List.getMap("TAX_DT").toString(), Long.parseLong(mfCmd521001List.getMap("ASTX").toString()))));
	                		/* ������ */
	                		sendMap.setMap("LETX", mfCmd521001List.getMap("LETX"));
	                		/* ������ ����� */
	                		sendMap.setMap("LETX_ADTX", cUtil.getGasanAmt(napGubun, mfCmd521001List.getMap("TAX_DT").toString(), Long.parseLong(mfCmd521001List.getMap("LETX").toString())));
	                		
	                		/* ���⳻���� */
	                		sendMap.setMap("DUE_DT", mfCmd521001List.getMap("DUE_DT"));
	                		/* ���������� */
	                		sendMap.setMap("DUE_F_DT", mfCmd521001List.getMap("DUE_F_DT"));
	                		
	                	} else {
	                		/* ���⳻�ݾ� */
	                		sendMap.setMap("NAP_BFAMT", cUtil.getNapBfAmt(Long.parseLong(mfCmd521001List.getMap("MNTX").toString())				//����
											                				, Long.parseLong(mfCmd521001List.getMap("MNTX_ADTX").toString())  	//���� �����
																			, Long.parseLong(mfCmd521001List.getMap("CPTX").toString())			//���ð�ȸ��
																			, Long.parseLong(mfCmd521001List.getMap("CPTX_ADTX").toString())	//���ð�ȸ�� �����
																			, Long.parseLong(mfCmd521001List.getMap("CFTX").toString())			//�����ü���
																			, Long.parseLong(mfCmd521001List.getMap("CFTX_ADTX").toString())	//�����ü��� �����
																			, Long.parseLong(mfCmd521001List.getMap("LETX").toString())			//������
																			, Long.parseLong(mfCmd521001List.getMap("LETX_ADTX").toString())	//������ �����
																			, Long.parseLong(mfCmd521001List.getMap("ASTX").toString())			//��Ư��
																		    , Long.parseLong(mfCmd521001List.getMap("ASTX_ADTX").toString())));	//��Ư�� �����
	                		napGubun = "B";
	                		
	                		/* �����ıݾ� (������ �ݾװ� ���⳻ �ݾ��� ����) */
	                		sendMap.setMap("NAP_AFAMT", sendMap.getMap("NAP_BFAMT"));
	                		
		                	
			                /* ������ȣ 3 */
			                sendMap.setMap("CHK3", cUtil.getGum3(sendMap.getMap("NAP_BFAMT").toString(), sendMap.getMap("NAP_AFAMT").toString()));
			                /* ����ǥ�ؾ� */
			                sendMap.setMap("KWA_AMT", Long.parseLong(mfCmd521001List.getMap("TAX_STD").toString()));
	                		
	                		/* ���� �ݾ� */
	                		sendMap.setMap("MNTX", mfCmd521001List.getMap("MNTX")); 			//����
	                		sendMap.setMap("MNTX_ADTX", mfCmd521001List.getMap("MNTX_ADTX"));	//���� �����
	                		sendMap.setMap("CPTX", mfCmd521001List.getMap("CPTX"));				//���ð�ȹ��
	                		sendMap.setMap("CPTX_ADTX", mfCmd521001List.getMap("CPTX_ADTX"));	//���ð�ȹ�� �����
	                		sendMap.setMap("CFTX", Long.parseLong(mfCmd521001List.getMap("CFTX").toString()) 
	                													+ Long.parseLong(mfCmd521001List.getMap("ASTX").toString())); 		//�����ü���/��Ư��
	                		sendMap.setMap("CFTX_ADTX", Long.parseLong(mfCmd521001List.getMap("CFTX_ADTX").toString()) 
	                													+ Long.parseLong(mfCmd521001List.getMap("ASTX_ADTX").toString())); 	//�����ü���/��Ư�� �����
	                		sendMap.setMap("LETX", mfCmd521001List.getMap("LETX"));				//������
	                		sendMap.setMap("LETX_ADTX", mfCmd521001List.getMap("LETX_ADTX"));	//������ �����
	                		
	                		/* ���⳻���� */
	                		sendMap.setMap("DUE_DT", mfCmd521001List.getMap("DUE_DT"));
	                		/* ���������� */
	                		// sendMap.setMap("DUE_F_DT", mfCmd521001List.getMap("DUE_DT")); -- ���������� ó������ ����--2011.09.14 --����---
	                		sendMap.setMap("DUE_F_DT", mfCmd521001List.getMap("DUE_F_DT"));
	                		
	                	}
		                /*
		                 * ���漼 �޺κ� ����
		                 */

		                /* ������ȣ 4 */
		                sendMap.setMap("CHK4", cUtil.getGum4(mfCmd521001List.getMap("MNTX").toString()
		                		                           , mfCmd521001List.getMap("CPTX").toString()
		                								   , mfCmd521001List.getMap("CFTX").toString()
		                								   , mfCmd521001List.getMap("LETX").toString()
		                								   , mfCmd521001List.getMap("TAX_YM").toString()));
		                /* �ʷ� */
		                sendMap.setMap("FILLER", "");
		                /* ������ȣ 5 */
		                sendMap.setMap("CHK5", "");
		                /* �������� + ����ǥ�ؼ���  */
		                sendMap.setMap("MLGN", mfCmd521001List.getMap("MLGN"));
		                /* �ΰ����� */
		                sendMap.setMap("GIGI_DATE", mfCmd521001List.getMap("TAX_DT"));
		                /* �������������ڵ� */
		                sendMap.setMap("BANK_CD", "");
		                /* �����Ͻ� */
		                sendMap.setMap("RECIP_DATE", "");
		                /* �����Ͻ� */
		                sendMap.setMap("NABGI_BA_GBN", napGubun);
		                /* �������� 2 */
		                sendMap.setMap("FIELD2", "");
		                /*
		                 * ���漼 ��
		                 */
	                }
					
				}else{ 
					/* ��ȸ�Ǽ��� ���� ��� ��������
					 * */
					
					resCode = "111";  /*��ȸ��������*/
					
				} 
			}
			
			log.info(cUtil.msgPrint("7", "","BP521002 chk_bs_521002()", resCode));
			
        } catch (Exception e) {
        	
        	resCode = "093";  
        	
			e.printStackTrace();
			
			log.info(e.getMessage());
			log.error("============================================");
			log.error("== chk_bs_521002 Exception(�ý���) ");
			log.error("============================================");
		}
        
        /*�����ΰ�� ���� ������*/
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

