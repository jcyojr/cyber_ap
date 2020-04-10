/**
 *  �ֽý��۸� : ���̹����漼û ��ȭ
 *  ��  ��  �� : ȯ�氳���δ�� ���γ��� ����ȸ ����
 *  ��  ��  �� : ������-���̹���û 
 *               ����ó��
 *               ��������� ���ŵ� ������ ���ؼ� ������������Ʈ�� �����ϰ� 
 *               ��ȸ����� �۽��Ѵ�.
 *  Ŭ����  ID : Kf203002Service
 *  ����  �̷� :
 * ------------------------------------------------------------------------
 *  �ۼ���     �Ҽ�  		 ����      Tag   ����
 * ------------------------------------------------------------------------   
 * �ҵ���   ��ä��(��)    2011.06.13   %01%  �ű��ۼ�
 *  
 */
package com.uc.bs.cyber.service.kf203002;

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
public class Kf203002Service {

	protected Log log = LogFactory.getLog(this.getClass());
	
	private ApplicationContext appContext = null;
	
	private IbatisBaseService sqlService_cyber = null;
		
	private Kf203002FieldList kfField = null;
	
	CbUtil cUtil = null;
	
	/**
	 * ������
	 */
	public Kf203002Service(ApplicationContext appContext) {
		// TODO Auto-generated constructor stub
		
		setAppContext(appContext);
		
		kfField = new Kf203002FieldList();
		
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
	 * ȯ�氳���δ�� ���γ��� ����ȸ ó��...
	 * */
	public byte[] chk_kf_203002(MapForm headMap, MapForm mf) throws Exception {
		
				
		MapForm sendMap = new MapForm();
		
		/*ȯ�氳���δ�� ���γ�����ȸ �� ��� */
		String resCode = "000";       /*�����ڵ�*/
		String giro_no = "1002641";   /*�λ�������ڵ�*/
		String sg_code = "26";        /*�λ�ñ���ڵ�*/
		String napkiGubun = "";

		long NAP_BFAMT = 0;
		long NAP_AFAMT = 0;
		long BONSE = 0;
		long BONSE_ADD = 0;
		
		try{
			cUtil = CbUtil.getInstance();
			
			log.info(cUtil.msgPrint("5", "S","KP203002 chk_kf_203002()", resCode));
			
		
			/*����üũ*/
			
			/*���*/
			if(!headMap.getMap("GPUB_CODE").equals(sg_code)){
				resCode = "339";
			}
			/*�����������ڵ�*/
			if(!headMap.getMap("GJIRO_NO").equals(giro_no)){
				resCode = "324";
			}
						
			/*
			 * �������� �� DB�� ���εǴ� ��
			 * ETAX_NO, NAPBU_JUMIN_NO
			 * */
			
			if(mf.getMap("ETAX_NO").toString().length() > 0 && resCode.equals("000")){
				
				ArrayList<MapForm>  kfCmd203002List  =  sqlService_cyber.queryForList("KF203002.SELECT_ENV_KFPAY_DETAIL_LIST", mf);
				
				int payCnt = kfCmd203002List.size();
				
				if ( payCnt <= 0) {
					
					resCode = "312";
				} else if( payCnt > 1) {
					
					resCode = "094";
				} else {
					
					MapForm mfCmd203002List  =  kfCmd203002List.get(0);
					
					MapForm mfSendData = new MapForm();
					
					napkiGubun = cUtil.getNapGubun((String)mfCmd203002List.getMap("DUE_DT"), (String)mfCmd203002List.getMap("DUE_F_DT"));
				    
				    if(napkiGubun.equals("B")){
				    	
				    	if(mfCmd203002List.getMap("DUE_DT").equals(mfCmd203002List.getMap("DUE_F_DT"))){
				    		
				    		NAP_BFAMT = ((BigDecimal)mfCmd203002List.getMap("ENV_AMT")).longValue()+((BigDecimal)mfCmd203002List.getMap("ADD_AMT")).longValue();   // ���⳻�ݾ�
		                    NAP_AFAMT = ((BigDecimal)mfCmd203002List.getMap("ENV_AMT")).longValue()+((BigDecimal)mfCmd203002List.getMap("ADD_AMT")).longValue();   // �����ıݾ�

		                    BONSE         = NAP_BFAMT;   // ���⳻�ݾ�
		                    BONSE_ADD     = NAP_AFAMT;   // �����ıݾ�

				    	} else {
				    		
				    		NAP_BFAMT = ((BigDecimal)mfCmd203002List.getMap("ENV_AMT")).longValue();   // ���⳻�ݾ�
		                    NAP_AFAMT = ((BigDecimal)mfCmd203002List.getMap("ENV_AMT")).longValue()+((BigDecimal)mfCmd203002List.getMap("ADD_AMT")).longValue();  // �����ıݾ�

		                    BONSE         = NAP_BFAMT;   
		                    BONSE_ADD     = NAP_AFAMT;   

				    	}
				    	
				    } else if(napkiGubun.equals("A")) {
				    	
				    	NAP_BFAMT = ((BigDecimal)mfCmd203002List.getMap("ENV_AMT")).longValue();   // ���⳻�ݾ�
	                    NAP_AFAMT = ((BigDecimal)mfCmd203002List.getMap("ENV_AMT")).longValue()+((BigDecimal)mfCmd203002List.getMap("ADD_AMT")).longValue();  // �����ıݾ�

	                    BONSE         = NAP_BFAMT;   
	                    BONSE_ADD     = NAP_AFAMT;   
				    	
				    }
					
					/*��ȸ ���� ����*/
					
					mfSendData.setMap("ETAXNO"           ,mf.getMap("ETAXNO"));                     /*���ι�ȣ*/
					mfSendData.setMap("NAPBU_JUMIN_NO"   ,mfCmd203002List.getMap("REG_NO"));        /*��ȸ�ֹι�ȣ*/
					mfSendData.setMap("JUMINNO"          ,mfCmd203002List.getMap("REG_NM"));        /*�ֹι�ȣ (DB)*/
					mfSendData.setMap("SIDO"             ,"26");                                    /*�õ�����*/
					mfSendData.setMap("GU_CODE"          ,mfCmd203002List.getMap("SGG_COD"));       /*��û�ڵ�*/
					mfSendData.setMap("CONFIRM_NO1"      ,"1");
					mfSendData.setMap("HCALVAL"          ,mfCmd203002List.getMap("ACCT_COD"));      /*ȸ��*/
					mfSendData.setMap("GWAMOK"           ,mfCmd203002List.getMap("TAX_ITEM"));      /*������*/
					mfSendData.setMap("TAX_YYMM"         ,(String)mfCmd203002List.getMap("TAX_YY")  +  (String)mfCmd203002List.getMap("TAX_MM"));  /*�ΰ�����*/
					mfSendData.setMap("KIBUN"            ,mfCmd203002List.getMap("TAX_DIV"));       /*���*/
					mfSendData.setMap("DONG_CODE"        ,mfCmd203002List.getMap("HACD"));          /*������*/
					mfSendData.setMap("GWASE_NO"         ,mfCmd203002List.getMap("TAX_SNO"));       /*������ȣ*/
					mfSendData.setMap("CONFIRM_NO2"      ,"2");
					mfSendData.setMap("NAPBU_NAME"       ,mfCmd203002List.getMap("REG_NM"));        /*�����ڸ�*/
					mfSendData.setMap("NAP_BFAMT"        ,NAP_BFAMT);
					mfSendData.setMap("NAP_AFAMT"        ,NAP_AFAMT);
					mfSendData.setMap("CONFIRM_NO3"      ,"3");
					mfSendData.setMap("GWASE_POINT"      ,"0");
					mfSendData.setMap("BONSE"            ,BONSE);
					mfSendData.setMap("BONSE_ADD"        ,BONSE_ADD);
					mfSendData.setMap("DOSISE"           ,mfCmd203002List.getMap("MI_AMT"));
					mfSendData.setMap("DOSISE_ADD"       ,mfCmd203002List.getMap("ENV_MIADD_AMT"));
					mfSendData.setMap("GONGDONGSE"       ,"0");
					mfSendData.setMap("GONGDONGSE_ADD"   ,"0");
					mfSendData.setMap("EDUSE"            ,"0");
					mfSendData.setMap("EDUSE_ADD"        ,"0");
					mfSendData.setMap("NAP_BFDATE"       ,mfCmd203002List.getMap("DUE_DT"));
					mfSendData.setMap("NAP_AFDATE"       ,mfCmd203002List.getMap("DUE_F_DT"));
					mfSendData.setMap("CONFIRM_NO4"      ,"4");
					mfSendData.setMap("FILLER1"          ,"0");
					mfSendData.setMap("CONFIRM_NO5"      ,"5");
					mfSendData.setMap("GWASE_DESC"       ,mfCmd203002List.getMap("MLGN_IF1"));
					mfSendData.setMap("GOJI_DATE"        ,mfCmd203002List.getMap("TAX_DT"));
					mfSendData.setMap("NAPBU_SYS"        ,mfCmd203002List.getMap("SNSU"));
					mfSendData.setMap("JIJUM_CODE"       ,mfCmd203002List.getMap("BRC_NO"));
					mfSendData.setMap("NAPBU_DATE"       ,mfCmd203002List.getMap("PAY_DT") + "000000");
					mfSendData.setMap("NAPBU_AMT"        ,mfCmd203002List.getMap("SUM_RCP"));
					mfSendData.setMap("OUTACCT_NO"       ,"");
					mfSendData.setMap("RESERVED2"        ,"");
					
				    sendMap = mfSendData;
				}
				
			} else {
				resCode = "341";  /*���ڳ��ι�ȣ, ���밡��ȣ ����*/
			}
			
			log.info(cUtil.msgPrint("5", "","KP203002 chk_kf_203002()", resCode));
			
        } catch (Exception e) {
			
        	resCode = "093";  //Error : ���ڳ��ι�ȣ, ���밡��ȣ ����
        	
			e.printStackTrace();
			log.error("============================================");
			log.error("== chk_kf_203002 Exception(�ý���) ");
			log.error("============================================");
		}
        
        /*���⼭ ������带 �����Ѵ�. �ٲ�� �κи� �ٽ� �����ؼ� ������ ����*/
        headMap.setMap("RS_FLAG"   , "G");         /*�����̿���(G) */
        headMap.setMap("RESP_CODE" , resCode);     /*�����ڵ�*/
        headMap.setMap("GCJ_NO"    , "0" + sg_code + "0" + CbUtil.lPadString(String.valueOf(SeqNumber()), 8, '0'));     /*�̿��� �Ϸù�ȣ*/
        headMap.setMap("TX_GUBUN"  , "0210");
        
        return kfField.makeSendBuffer(headMap, sendMap);
	}

	/*----------------------------------------------*/
	/* �Ϸù�ȣ �������� �׽�Ʈ*/
	/*----------------------------------------------*/
	private int SeqNumber(){
    	return (Integer)sqlService_cyber.queryForBean("CODMBASE.CODM_GETSEQNO", "00");
    }
}
