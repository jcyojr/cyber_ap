/**
 *  �ֽý��۸� : ���̹����漼û ��ȭ
 *  ��  ��  �� : ���漼 ���γ��� ����ȸ ����
 *  ��  ��  �� : �λ�����-���̹���û 
 *               ����ó��
 *               ��������� ���ŵ� ������ ���ؼ� ������������Ʈ�� �����ϰ� 
 *               ��ȸ����� �����Ѵ�.
 *  Ŭ����  ID : Bs523002Service
 *  ����  �̷� :
 * ------------------------------------------------------------------------
 *  �ۼ���     �Ҽ�  		 ����      Tag   ����
 * ------------------------------------------------------------------------   
 * Ȳ����   �ٻ�(��)    2011.06.13   %01%  �ű��ۼ�
 *  
 */
package com.uc.bs.cyber.service.bs523002;

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
public class Bs523002Service {

	protected Log log = LogFactory.getLog(this.getClass());
	
	private ApplicationContext appContext = null;
	
	private IbatisBaseService sqlService_cyber = null;
		
	private Bs523002FieldList bsField = null;
	
	CbUtil cUtil = null;
	
	/**
	 * ������
	 */
	public Bs523002Service(ApplicationContext appContext) {
		// TODO Auto-generated constructor stub
		
		setAppContext(appContext);
		
		bsField = new Bs523002FieldList();
		
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
	 * ���漼 ���γ��� ����ȸ ó��...
	 * */
	public byte[] chk_bs_523002(MapForm headMap, MapForm mf) throws Exception {
		
				
		MapForm sendMap = new MapForm();
		
		/*���漼 ����������ȸ �� ��� */
		String resCode = "000";       /*�����ڵ�*/
//		String giro_no = "1000685";   /*�λ�������ڵ�*/
		String strTaxGubun = "";
		String sg_code = "26";        /*�λ�ñ���ڵ�*/
		
		try{
			cUtil = CbUtil.getInstance();
			
			log.info(cUtil.msgPrint("9", "S","BP523002 chk_bs_523002()", resCode));
			
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

            if(!("".equals(mf.getMap("NAPBU_JUMIN_NO").toString().trim()))){
                resCode = "326";//�������ֹι�ȣ ��ȸ �Ұ�
            }
			/* ��ȸ���� �ڸ���(���ι�ȣ �߶� ��ȸ���� �����) */
			mf.setMap("SGG_COD", mf.getMap("ETAXNO").toString().substring(0, 3)); 	//��û�ڵ�
			mf.setMap("ACCT_COD", mf.getMap("ETAXNO").toString().substring(4, 6));  //ȸ���ڵ�
			mf.setMap("TAX_ITEM", mf.getMap("ETAXNO").toString().substring(6, 12)); //����/�����ڵ�
			mf.setMap("TAX_YY", mf.getMap("ETAXNO").toString().substring(12, 16));  //�����⵵
			mf.setMap("TAX_MM", mf.getMap("ETAXNO").toString().substring(16, 18));	//������
			mf.setMap("TAX_DIV", mf.getMap("ETAXNO").toString().substring(18, 19));	//����ڵ�
			mf.setMap("HACD", mf.getMap("ETAXNO").toString().substring(19, 22));	//�������ڵ�
			mf.setMap("TAX_SNO", mf.getMap("ETAXNO").toString().substring(22, 28));	//������ȣ
			
			ArrayList<MapForm>  bsCmd523002List  =  sqlService_cyber.queryForList("BS523002.SELECT_RECIP_LIST", mf);
			
			log.debug("TAXNO = [" + mf.getMap("ETAXNO") + "]");
			//log.debug("JUMIN_NO = [" + mf.getMap("JUMIN_NO") + "]");
			
			if(bsCmd523002List.size() <= 0){
				resCode = "112";  // Error : ���γ�������
			} else if(bsCmd523002List.size() > 1){
				resCode = "094";  // Error : ��ȸ 2�� �̻�
			} else {
				
				if ( bsCmd523002List.size() > 0 ) {
					
					resCode = "000";
					
					MapForm mfCmd521001List  =  bsCmd523002List.get(0);
					
					/*
					 *  �պκ� ���� 
					 */
					
					/* ���ι�ȣ */
					sendMap.setMap("ETAXNO", mfCmd521001List.getMap("TAX_NO"));
					/* �Ϸù�ȣ */
					sendMap.setMap("SEQNO", mf.getMap("SEQNO"));
					/* �ֹ�(����,�����) ��ȣ */
					//sendMap.setMap("NAPBU_JUMIN_NO", mfCmd521001List.getMap("REG_NO"));
					/* �ֹ�(����,�����) ��ȣ */
					//sendMap.setMap("JUMIN_NO", mfCmd521001List.getMap("REG_NO"));
					/* �õ��ڵ� */
					sendMap.setMap("SIDO", "26");
					/* �ΰ���� */
					sendMap.setMap("GU_CODE", mfCmd521001List.getMap("SGG_COD"));
					/* ������ȣ 1 */
					sendMap.setMap("CHK1", mfCmd521001List.getMap("TAX_NO").toString().substring(3, 4)); // �̰͵� �̻���
					/* ȸ�� */
					sendMap.setMap("HCALVAL", mfCmd521001List.getMap("ACCT_COD"));	
					/* ���񼼸� */
					sendMap.setMap("GWA_MOK", mfCmd521001List.getMap("TAX_ITEM"));
					/* ������� */
					sendMap.setMap("TAX_YYMM", mfCmd521001List.getMap("TAX_YM"));
					/* ��� */
					sendMap.setMap("KIBUN", mfCmd521001List.getMap("TAX_DIV"));
					/* ������ */
					sendMap.setMap("DONG_CODE", mfCmd521001List.getMap("HACD"));
					/* ������ȣ */
					sendMap.setMap("GWASE_NO", mfCmd521001List.getMap("TAX_SNO"));
					/* ������ȣ 2 */
					sendMap.setMap("CHK2", mfCmd521001List.getMap("TAX_NO").toString().substring(mfCmd521001List.getMap("TAX_NO").toString().length() - 1)); 
					/* �����ڼ��� */
					sendMap.setMap("NAP_NAME", mfCmd521001List.getMap("REG_NM"));
					
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
		                
		                /* ������ȣ 4 */
		                sendMap.setMap("CHK4", cUtil.getGum4(mf.getMap("MNTX").toString(), mf.getMap("CPTX").toString()
		                										, mf.getMap("CFTX").toString(), mf.getMap("LETX").toString(), mf.getMap("TAX_YM").toString()));
		                /* �ʷ� */
		                sendMap.setMap("FILLER", "");
		                /* ������ȣ 5 */
		                sendMap.setMap("CHK5", "");
		                /* �������� + ����ǥ�ؼ���  */
		                sendMap.setMap("MLGN", mfCmd521001List.getMap("MLGN"));
		                /* �ΰ����� */
		                sendMap.setMap("GIGI_DATE", mfCmd521001List.getMap("TAX_DT"));
		                /* �����̿�ý��� */
		                sendMap.setMap("NAPBU_SYS", mfCmd521001List.getMap("NAPBU_SYS"));
		                /* �������������ڵ� */
		                sendMap.setMap("BANK_CD", mfCmd521001List.getMap("BRC_NO"));
		                /* �����Ͻ� */
		                sendMap.setMap("RECIP_DATE", mfCmd521001List.getMap("PAY_DT") + "000000");
		                /* ���αݾ� */
		                sendMap.setMap("RECIP_AMT", mfCmd521001List.getMap("PAY_AMT"));
		                /* ��ݰ��¹�ȣ */
		                sendMap.setMap("OUTACCT_NO", "");
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
	                		sendMap.setMap("CFTX_ADTX", cUtil.getGasanAmt(napGubun
	                				                                    , mfCmd521001List.getMap("TAX_DT").toString()
	                				                                    , Long.parseLong(mfCmd521001List.getMap("CFTX").toString()) + cUtil.getGasanAmtNont(napGubun, mfCmd521001List.getMap("TAX_DT").toString()
	                													, Long.parseLong(mfCmd521001List.getMap("ASTX").toString()))));
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
		                								   , mfCmd521001List.getMap("DUE_DT").toString()));
		                /* �ʷ� */
		                sendMap.setMap("FILLER", "");
		                /* ������ȣ 5 */
		                sendMap.setMap("CHK5", "");
		                /* �������� + ����ǥ�ؼ���  */
		                sendMap.setMap("MLGN", mfCmd521001List.getMap("MLGN"));
		                /* �ΰ����� */
		                sendMap.setMap("GOJICR_DATE", mfCmd521001List.getMap("TAX_DT"));
		                /* �����̿�ý��� */
		                sendMap.setMap("NAPBU_SYS", mfCmd521001List.getMap("NAPBU_SYS"));
		                /* �������������ڵ� */
		                sendMap.setMap("BANK_CD", mfCmd521001List.getMap("BRC_NO"));
		                /* �����Ͻ� */
		                sendMap.setMap("RECIP_DATE", mfCmd521001List.getMap("PAY_DT") + "000000");
		                /* ���αݾ� */
		                sendMap.setMap("RECIP_AMT", mfCmd521001List.getMap("PAY_AMT"));
		                /* ��ݰ��¹�ȣ */
		                sendMap.setMap("OUTACCT_NO", "");
		                /* �������� 2 */
		                sendMap.setMap("FIELD2", "");
		                
		                
		                /*
		                 * ���漼 ��
		                 */                
	                }
					
				}else{ 
					/* ��ȸ�Ǽ��� ���� ��� ��������
					 * */
					
					resCode = "112";  /*��ȸ��������*/
					
				} 
				
			}

			log.info(cUtil.msgPrint("9", "","BP523002 chk_bs_523002()", resCode));
			
        } catch (Exception e) {
        	
			resCode = "093";
			
			e.printStackTrace();
			log.error("============================================");
			log.error("== chk_bs_523002 Exception(�ý���) ");
			log.error("============================================");
		}
        
        if(!resCode.equals("000")){
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

