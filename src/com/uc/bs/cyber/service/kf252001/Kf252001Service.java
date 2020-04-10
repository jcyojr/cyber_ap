/**
 *  �ֽý��۸� : ���̹����漼û ��ȭ
 *  ��  ��  �� : ������-���ϼ������ ���ΰ�� ���� / ���ο���
 *  ��  ��  �� : ������-���̹���û 
 *               ����ó��
 *               
 *  Ŭ����  ID : Kf252001Service
 *  ����  �̷� :
 * ------------------------------------------------------------------------
 *  �ۼ���     �Ҽ�  		 ����      Tag   ����
 * ------------------------------------------------------------------------   
 * �۵���   ��ä��(��)    2011.06.21   %01%  �ű��ۼ�
 *  
 */
package com.uc.bs.cyber.service.kf252001;

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
public class Kf252001Service {

	protected Log log = LogFactory.getLog(this.getClass());
	
	private ApplicationContext appContext = null;
	
	private IbatisBaseService sqlService_cyber = null;
		
	private Kf252001FieldList kfField = null;
	
	private int[] svcTms = new int[2];
	
	CbUtil cUtil = null;
	
	/**
	 * ������
	 */
	public Kf252001Service(ApplicationContext appContext) {
		// TODO Auto-generated constructor stub
		
		setAppContext(appContext);
		
		kfField = new Kf252001FieldList();
		
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
	 * ���ϼ������ ���ΰ�� ����
	 * */
	public byte[] chk_kf_252001(MapForm headMap, MapForm mf) throws Exception {
				
		CbUtil cUtil = null;
		
		/*���ϼ������ ����������ȸ �� ��� */
		String resCode = "000";       /*�����ڵ�*/
		String giro_no = "1004102";   /*�λ�� ���ϼ��� �����ڵ�*/
		String sg_code = "26";        /*�λ�ñ���ڵ�*/
		
		int curTime = 0;

		try{
			
			/*���� �ð�*/
			svcTms[0] = 0030;
			svcTms[1] = 2200;
			
			cUtil = CbUtil.getInstance();

			log.info(cUtil.msgPrint("3", "S","KP252001 chk_kf_252001()", resCode));
			
			/*����üũ*/
			
			/*���*/
			if(!headMap.getMap("GPUB_CODE").equals(sg_code)){
				resCode = "339";
			}
			/*�����������ڵ�*/
			if(!headMap.getMap("GJIRO_NO").equals(giro_no)){
				resCode = "324";
			}
			
			curTime = Integer.parseInt(headMap.getMap("TX_DATE").toString().substring(6, 10));
			
			if(log.isDebugEnabled()) {
				log.debug("-------------------------------------------------------------");
				log.debug("   �� ��ȸŰ : " + mf.getMap("SEARCHKEY").toString());
				log.debug("   �� �ΰ���� : " + mf.getMap("BYYYMM").toString());
				log.debug("   �� ������� �з� : " + headMap.getMap("GJIRO_NO").toString());
				log.debug("   �� ���αݾ� : " + mf.getMap("NAPBU_AMT").toString());
				log.debug("-------------------------------------------------------------");
			}
		
			/*��������*/
			String ICHENO      = "";
			String CUSTNO      = "";
			@SuppressWarnings("unused")
			String searchgubun = "";
			int rcpCount       = 0;
			int totalCount     = 0;
			
			if (mf.getMap("SEARCHKEY").toString().length() == 17) { /*�������ι�ȣ*/
				
				ICHENO = mf.getMap("SEARCHKEY").toString();
	            searchgubun = "E";
			
			} else { /*���밡��ȣ*/
				
				if (cUtil.waterChgCustno("S", mf.getMap("SEARCHKEY").toString().substring(20,29)).equals(mf.getMap("SEARCHKEY").toString())){
					
					CUSTNO = mf.getMap("SEARCHKEY").toString().substring(20,29);
	                searchgubun = "S";
					
				} else {
					
					resCode = "311";       //�������� ����
				}
				/*���⼭�� �ʿ���...*/
				mf.setMap("TAX_YY" , mf.getMap("BYYYMM").toString().substring(0, 4));
				mf.setMap("TAX_MM" , mf.getMap("BYYYMM").toString().substring(4));
			}
			
			if (CUSTNO.length() == 9 || ICHENO.length() == 17) {
				
				String napkiGubun = "";    // ���⳻�� ���� (B:���⳻, A:������)
		        String NAPAMT = "";        // ���αݾ�
		        
		        /*���������� ������ ������ �޾� ��ȸ����� Ȯ���Ѵ�.*/
				
		        /*���������� ������ ������ DB �� �����ϱ� ����.*/
		        mf.setMap("CUST_NO", CUSTNO);
				mf.setMap("EPAY_NO", ICHENO);
				
		        
				ArrayList<MapForm>  kfAl252001List = sqlService_cyber.queryForList("KF251001.SELECT_SHSD_PAY_LIST", mf);
		        
				rcpCount = kfAl252001List.size();
				
				
		        if (rcpCount < 1){
		        	resCode = "000"; //���γ�������
		        } else {

		        	/*���γ��� ����*/
					MapForm mfCmd252001List = kfAl252001List.get(0);
					
					if ( mfCmd252001List == null  ||  mfCmd252001List.isEmpty() )   {
						/*���γ�������*/
						resCode = "000";
					} else {
						/*���γ����� �ִ� ���*/
						if(mfCmd252001List.getMap("SNTG").equals("9")) {  /*���*/
							
							resCode = "000";
							
						} else {
							
							resCode = "331";  /*���α����*/

						}
					}

		        }
		        
		        if (curTime >= svcTms[0] && curTime <= svcTms[1]) {
					
				} else {
					resCode = "092";  /*���νð� �ƴ�...*/
				}

		        /*���γ����� �������� �ʰų�, ������ҵ� ��쿡 ���� ����ó��*/
		        if(resCode.equals("000")) {
		        	
		        	/*�ΰ�����Ʈ �˻�*/
					ArrayList<MapForm>  alKfLevyList = sqlService_cyber.queryForList("KF251001.SELECT_SHSD_LEVY_LIST", mf);
		        	
					totalCount = alKfLevyList.size();
					
					if (totalCount < 1)
						resCode = "311";        // Error : ������������
			        else if (totalCount > 1)
			        	resCode = "093";        // Error : �������� 2�� �̻�
			        else {                      // ���������� 1���� ��쿡�� ó��
			        	
			        	MapForm mpKfLevyList = alKfLevyList.get(0);
			        	
				        String prt_del1 = (String)mpKfLevyList.getMap("DUE_DT");         //���⳻ ������;
				        String prt_del2 = (String)mpKfLevyList.getMap("DUE_F_DT");       //������ ������;

		                //���⳻(B), ������(A) üũ
		                napkiGubun = cUtil.getNapGubun(prt_del1, prt_del2);

		                //����ó������ Set
		                long prt_amt         = ((BigDecimal)mpKfLevyList.getMap("SUM_B_AMT")).longValue();    /*���⳻*/
		                long prt_amt2        = ((BigDecimal)mpKfLevyList.getMap("SUM_F_AMT")).longValue();    /*������*/
		                
		                if(napkiGubun.equals("B"))
		                {
		                    NAPAMT = Long.toString(prt_amt);   //���⳻�ݾ�
		                }
		                else if(napkiGubun.equals("A"))
		                {
		                    NAPAMT = Long.toString(prt_amt2);  //�����ıݾ�
		                }

		                
		                if (NAPAMT.equals((String) mf.getMap("NAPBU_AMT")))
		            	    resCode = "000";        // ����
		                else
		            	    resCode = "343";        // ���αݾ� Ʋ��
		                
		                
		    	        // ��������ó��
		    	        // 1. �ΰ����� update
		    	        // 2. �������̺� insert
		    			if (resCode.equals("000")) {
		    				
		    				/*������������*/
		    				/*���ڼ������̺� ���������� ���Ѵ�. �ߺ�ó���� �����ϹǷ�...*/
		    				mpKfLevyList.setMap("PAY_CNT" , sqlService_cyber.getOneFieldInteger("KF251001.TX3211_TB_MaxPayCnt", mpKfLevyList));
		    				mpKfLevyList.setMap("SUM_RCP" , NAPAMT);                                                   /*���αݾ�*/
		    				mpKfLevyList.setMap("PAY_DT"  , mf.getMap("NAPBU_DATE"));                                  /*��������*/
		    				mpKfLevyList.setMap("SNTG"    , "1");                                                      /*���λ���*/
		    				mpKfLevyList.setMap("SNSU"    , "1");                                                      /*������ü 1 ����������*/
		    				mpKfLevyList.setMap("BANK_CD" , CbUtil.lPadString(mf.getMap("OUTBANK_CODE").toString(), 7, '0').substring(0,3));      /*���α��*/
		    				mpKfLevyList.setMap("RSN_YN"  , (mf.getMap("NAPBU_GUBUN").equals("R")) ? "Y" : "N");       /*���α��� Q����  R��ȸ*/
		    				mpKfLevyList.setMap("TRTG"    , "0");                                                      /*�ڷ����ۻ���*/
		    				mpKfLevyList.setMap("BRC_NO"  , CbUtil.lPadString(mf.getMap("OUTBANK_CODE").toString(), 7, '0'));  /*��������ڵ�*/
							mpKfLevyList.setMap("TMSG_NO" , headMap.getMap("BCJ_NO"));   /*�������_����������ȣ*/
		    				
		    				int intLog = sqlService_cyber.queryForUpdate("KF251001.INSERT_TX3211_TB_EPAY", mpKfLevyList);
		    				
		    				if(intLog > 0) {
		    					
		    					sqlService_cyber.queryForUpdate("KF251001.UPDATE_TX3111_TB_NAPBU_INFO", mpKfLevyList);
		    					
		    					log.info("���ϼ������ ���ڳ���ó�� �Ϸ�! [" + mf.getMap("SEARCHKEY") + "]");
		    					
		    					//MapForm mpSMS = new MapForm();	
								
		    					/*���ϼ����� �ֹι�ȣ�� ������..Select �Ѵ�.*/
								//mpSMS.setMap("REG_NO"  , "");
								//mpSMS.setMap("TAX_ITEM", mpKfLevyList.getMap("TAX_ITEM"));
								//mpSMS.setMap("SUM_RCP" , NAPAMT);
								
								//sqlService_cyber.procedure("CODMBASE.SMSCALL", mpSMS);
		    				}

		    			}
			            
			        }
		        	
		        }

			}
			
			log.info(cUtil.msgPrint("3","","KP252001", resCode));
			log.info("=============================================");
		
        } catch (Exception e) {
			
			e.printStackTrace();
			log.error("============================================");
			log.error("== chk_kf_252001 Exception(�ý���) ");
			log.error("============================================");
		}
        
        /*���������� ���� ������.*/
        
        /*���⼭ ������带 �����Ѵ�. �ٲ�� �κи� �ٽ� �����ؼ� ������ ����*/
        headMap.setMap("RS_FLAG"   , "G");         /*�����̿���(G) */
        headMap.setMap("RESP_CODE" , resCode);     /*�����ڵ�*/
        headMap.setMap("GCJ_NO"    , "0" + sg_code + "0" + CbUtil.lPadString(String.valueOf(SeqNumber()), 8, '0'));     /*�̿��� �Ϸù�ȣ*/
        headMap.setMap("TX_GUBUN"  , "0210");
        
        return kfField.makeSendBuffer(headMap, mf);
        
	}

	/*----------------------------------------------*/
	/* �Ϸù�ȣ �������� �׽�Ʈ*/
	/*----------------------------------------------*/
	private int SeqNumber(){
    	return (Integer)sqlService_cyber.queryForBean("CODMBASE.CODM_GETSEQNO", "00");
    }
}
