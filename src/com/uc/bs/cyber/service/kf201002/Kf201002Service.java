/**
 *  �ֽý��۸� : ���̹����漼û ��ȭ
 *  ��  ��  �� : ȯ�氳���δ�� �������� ����ȸ ����
 *  ��  ��  �� : ������-���̹���û 
 *               ����ó��
 *               ��������� ���ŵ� ������ ���ؼ� ������������Ʈ�� �����ϰ� 
 *               ��ȸ����� �����Ѵ�.
 *  Ŭ����  ID : Kf201002Service
 *  ����  �̷� :
 * ------------------------------------------------------------------------
 *  �ۼ���     �Ҽ�  		 ����      Tag   ����
 * ------------------------------------------------------------------------   
 * �ҵ���   ��ä��(��)    2011.06.13   %01%  �ű��ۼ�
 *  
 */
package com.uc.bs.cyber.service.kf201002;

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
public class Kf201002Service {

	protected Log log = LogFactory.getLog(this.getClass());
	
	private ApplicationContext appContext = null;
	
	private IbatisBaseService sqlService_cyber = null;
		
	private Kf201002FieldList kfField = null;
	
	CbUtil cUtil = null;
	
	/**
	 * ������
	 */
	public Kf201002Service(ApplicationContext appContext) {
		// TODO Auto-generated constructor stub
		
		setAppContext(appContext);
		
		kfField = new Kf201002FieldList();
		
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
	public byte[] chk_kf_201002(MapForm headMap, MapForm mf) throws Exception {
		
				
		MapForm sendMap = new MapForm();
		
		/*ȯ�氳���δ�� ����������ȸ �� ��� */
		String resCode = "000";       /*�����ڵ�*/
		String giro_no = "1002641";   /*�λ�������ڵ�*/
		String sg_code = "26";        /*�λ�ñ���ڵ�*/
		String napkiGubun = "";

		long NAP_BFAMT = 0;
		long NAP_AFAMT = 0;
		long BONSE = 0;
		long BONSE_ADD = 0;
		
		String NP_BAF_GUBUN = "";
		String JA_GOGI_GUBUN = "";
		
		try{
			
			cUtil = CbUtil.getInstance();
			
			log.info(cUtil.msgPrint("2", "S","KP201002 chk_kf_201002()", resCode));
			

			/*����üũ*/
			
			/*���*/
			if(!headMap.getMap("GPUB_CODE").equals(sg_code)){
				resCode = "339";
			}
			/*�����������ڵ�*/
			if(!headMap.getMap("GJIRO_NO").equals(giro_no)){
				resCode = "324";
			}
						
			if(mf.getMap("ETAX_NO").toString().length() == 17 && resCode.equals("000")){
				
				ArrayList<MapForm>  kfCmd201002List  =  sqlService_cyber.queryForList("KF201001.SELECT_ENV_SEARCH_LIST", mf);
				
				int levyCnt = kfCmd201002List.size();
				
				if ( levyCnt <= 0) {
					resCode = "311";  /*��ȸ��������*/
				} else if (levyCnt > 1) {
					resCode = "094";  /*������ 2�� �̻�*/
				} else {
					
					MapForm mfCmd201002List  =  kfCmd201002List.get(0);
					
					MapForm mfSendData = new MapForm();
					
					/*��ȸ ���� ����*/
					mfSendData.setMap("ETAX_NO"              ,  mf.getMap("ETAX_NO")  ); 
					mfSendData.setMap("JUMIN_NO"             ,  mfCmd201002List.getMap("REG_NO")  ); 
					mfSendData.setMap("SIDO"                 ,  "26" );                                /*�õ�����*/
					mfSendData.setMap("GU_CODE"              ,  mfCmd201002List.getMap("SGG_COD") );   /*��û�ڵ�*/
					mfSendData.setMap("CONFIRM_NO1"          ,  "1"  ); 
					mfSendData.setMap("HCALVAL"              ,  mfCmd201002List.getMap("ACCT_COD") );  /*ȸ��*/
					mfSendData.setMap("GWA_MOK"              ,  mfCmd201002List.getMap("TAX_ITEM") );  /*������*/
					mfSendData.setMap("TAX_YYMM"             ,  (String)mfCmd201002List.getMap("TAX_YY")  +  (String)mfCmd201002List.getMap("TAX_MM")  );  /*�ΰ����*/ 
					mfSendData.setMap("KIBUN"                ,  mfCmd201002List.getMap("TAX_DIV")  );  /*���*/
					mfSendData.setMap("DONG_CODE"            ,  mfCmd201002List.getMap("HACD")  );     /*������*/
					mfSendData.setMap("GWASE_NO"             ,  mfCmd201002List.getMap("TAX_SNO")  );
					mfSendData.setMap("CONFIRM_NO2"          ,  "2"  );
					mfSendData.setMap("NAP_NAME"             ,  mfCmd201002List.getMap("REG_NM")  );
					
				    napkiGubun = cUtil.getNapGubun((String)mfCmd201002List.getMap("DUE_DT"), (String)mfCmd201002List.getMap("DUE_F_DT"));
				    
				    if(napkiGubun.equals("B")){
				    	
				    	//if(mfCmd201002List.getMap("DUE_DT").equals(mfCmd201002List.getMap("DUE_F_DT"))){ 
					    if(!mfCmd201002List.getMap("DLQ_DIV").equals("0")){
				    		
				    		/*ü��*/
				    		NAP_BFAMT = ((BigDecimal)mfCmd201002List.getMap("ENV_AMT")).longValue()+((BigDecimal)mfCmd201002List.getMap("ADD_AMT")).longValue();   // ���⳻�ݾ�
		                    NAP_AFAMT = ((BigDecimal)mfCmd201002List.getMap("ENV_AMT")).longValue()+((BigDecimal)mfCmd201002List.getMap("ADD_AMT")).longValue();   // �����ıݾ�
		                    NP_BAF_GUBUN = napkiGubun;
		                    JA_GOGI_GUBUN = "4";
		                    BONSE         = NAP_BFAMT;   // ���⳻�ݾ�
		                    BONSE_ADD     = NAP_AFAMT;   // �����ıݾ�

				    	} else {
				    		/*���⳻*/
				    		NAP_BFAMT = ((BigDecimal)mfCmd201002List.getMap("ENV_AMT")).longValue();   // ���⳻�ݾ�
		                    NAP_AFAMT = ((BigDecimal)mfCmd201002List.getMap("ENV_AMT")).longValue()+((BigDecimal)mfCmd201002List.getMap("ADD_AMT")).longValue();  // �����ıݾ�
		                    NP_BAF_GUBUN = napkiGubun;
		                    JA_GOGI_GUBUN = "0";
		                    BONSE         = NAP_BFAMT;   
		                    BONSE_ADD     = NAP_AFAMT;   

				    	}
				    	
				    } else if(napkiGubun.equals("A")) {
				    	/*������*/
				    	NAP_BFAMT = ((BigDecimal)mfCmd201002List.getMap("ENV_AMT")).longValue();   // ���⳻�ݾ�
	                    NAP_AFAMT = ((BigDecimal)mfCmd201002List.getMap("ENV_AMT")).longValue()+((BigDecimal)mfCmd201002List.getMap("ADD_AMT")).longValue();  // �����ıݾ�
	                    NP_BAF_GUBUN = napkiGubun;
	                    JA_GOGI_GUBUN = "4";
	                    BONSE         = NAP_BFAMT;   
	                    BONSE_ADD     = NAP_AFAMT;   
				    	
				    }
				    mfSendData.setMap("NAP_BFAMT"            ,  NAP_BFAMT   );
					mfSendData.setMap("NAP_AFAMT"            ,  NAP_AFAMT   );
				    mfSendData.setMap("CONFIRM_NO3"          ,  "3"  );
				    mfSendData.setMap("GWASE_RULE"           ,  "0"  );  /*�ӽ÷� ����ǥ���� 0����  SET*/
				    mfSendData.setMap("BONSE"                ,  BONSE  );
				    mfSendData.setMap("BONSE_ADD"            ,  BONSE_ADD  );
				    mfSendData.setMap("DOSISE"               ,  mfCmd201002List.getMap("MI_AMT")  );
				    mfSendData.setMap("DOSISE_ADD"           ,  mfCmd201002List.getMap("ENV_MIADD_AMT")  );
				    mfSendData.setMap("GONGDONGSE"           ,  "0"  );
				    mfSendData.setMap("GONGDONGSE_ADD"       ,  "0"  );
				    mfSendData.setMap("EDUSE"                ,  "0"  );
				    mfSendData.setMap("EDUSE_ADD"            ,  "0"  );
				    mfSendData.setMap("NAP_BFDATE"           ,  mfCmd201002List.getMap("DUE_DT")  );
				    mfSendData.setMap("NAP_AFDATE"           ,  mfCmd201002List.getMap("DUE_F_DT")  );
				    mfSendData.setMap("CONFIRM_NO4"          ,  "4"  );
				    mfSendData.setMap("FILLER1"              ,  "0"  );
				    mfSendData.setMap("CONFIRM_NO5"          ,  "5"  );
				    mfSendData.setMap("GWASE_DESC"           ,  mfCmd201002List.getMap("MLGN_IF1")  );
				    mfSendData.setMap("GWASE_PUB_DESC"       ,  mfCmd201002List.getMap("MLGN_IF2")  );
				    mfSendData.setMap("GOJICR_DATE"          ,  mfCmd201002List.getMap("TAX_DT")  );
				    mfSendData.setMap("JADONG_YN"            ,  "N"  );
				    mfSendData.setMap("JIJUM_CODE"           ,  "0"  );
				    mfSendData.setMap("NAPBU_DATE"           ,  "0"  );
				    mfSendData.setMap("NP_BAF_GUBUN"         ,  NP_BAF_GUBUN  );
				    mfSendData.setMap("TAX_GOGI_GUBUN"       ,  "1"  );
				    mfSendData.setMap("JA_GOGI_GUBUN"        ,  JA_GOGI_GUBUN );
				    mfSendData.setMap("RESERV2"              ,  " "  );  /*����*/
				    
				    /*��������*/
				    sendMap = mfSendData;
				}
				
			} else {
				resCode = "341";  /*���ڳ��ι�ȣ, ���밡��ȣ ����*/
			}
			
			log.info(cUtil.msgPrint("2", "","KP201002 chk_kf_201002()", resCode));
			
        } catch (Exception e) {
			
        	resCode = "093";  //Error : ���ڳ��ι�ȣ, ���밡��ȣ ����
        	
			e.printStackTrace();
			
			log.error("============================================");
			log.error("== chk_kf_201002 Exception(�ý���) ");
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
    	return  (Integer)sqlService_cyber.queryForBean("CODMBASE.CODM_GETSEQNO", "00");
    }
}
 