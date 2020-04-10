/**
 *  �ֽý��۸� : ���̹����漼û ��ȭ
 *  ��  ��  �� : ������-���ܼ��� �ϰ����ΰ������/ �����û 
 *  ��  ��  �� : ������-���̹���û 
 *               ����ó��
 *               
 *  Ŭ����  ID : Kf276001Service
 *  ����  �̷� :
 * ------------------------------------------------------------------------
 *  �ۼ���     �Ҽ�  		 ����      Tag   ����
 * ------------------------------------------------------------------------   
 * �ҵ���   ��ä��(��)    2011.06.13   %01%  �ű��ۼ�
 *  
 */
package com.uc.bs.cyber.service.kf276001;

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
public class Kf276001Service {

	protected Log log = LogFactory.getLog(this.getClass());
	
	private ApplicationContext appContext = null;
	
	private IbatisBaseService sqlService_cyber = null;
		
	private Kf276001FieldList kfField = null;
	
	CbUtil cUtil = null;
	
	/**
	 * ������
	 */
	public Kf276001Service(ApplicationContext appContext) {
		// TODO Auto-generated constructor stub
		
		setAppContext(appContext);
		
		kfField = new Kf276001FieldList();
		
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
	 * ��Ÿ���Ա� �ϰ����ΰ�� ���� / ���ೳ��
	 * */
	@SuppressWarnings("unchecked")
	public byte[] chk_kf_276001(MapForm headMap, MapForm mf, MapForm repeatMf) throws Exception {
		
		/*��Ÿ���Ա� ����������ȸ �� ��� */
		String resCode = "000";       /*�����ڵ�*/
//		String giro_no = "1002641";   /*�λ�������ڵ�*/
		String giro_no = "1500172";   /*�λ�������ڵ�- ����e���ν������� ��Ÿ���Ա� �����ڵ� ����*/
		String sg_code = "26";        /*�λ�ñ���ڵ�*/

		/*�����*/
		MapForm mfData = new MapForm();
		/*�ݺ���*/
		ArrayList<MapForm> alRepeatData = new ArrayList<MapForm>();
		
		try{
			cUtil = CbUtil.getInstance();
			
			log.info(cUtil.msgPrint("3", "S","KP276001 chk_kf_276001()", resCode));

			/*����üũ*/
			
			/*���*/
			if(!headMap.getMap("GPUB_CODE").equals(sg_code)){
				resCode = "339";
			}
			/*�����������ڵ�*/
			if(!headMap.getMap("GJIRO_NO").equals(giro_no)){
				resCode = "324";
			}
						
			/*���������� ������ ������ �޾� ��ȸ����� Ȯ���Ѵ�.*/
			ArrayList<MapForm>  recvMap = (ArrayList<MapForm>)repeatMf.getMap("repetList");

			mfData = mf;
			
			for (int i = 0; i < recvMap.size(); i++){
				
				MapForm recvResultMap = new MapForm();
				
				/* ���� �����Ϳ� �ݺ��κ� ������ */
				recvResultMap = recvMap.get(i);
				
				/* ���� ������ �ݺ��κ��� ������ŵ����Ϳ� ���ڳ��ι�ȣ �� */
				mfData.setMap("EPAYNO", recvResultMap.getMap("EPAYNO"));		

				ArrayList<MapForm>  kfAl276001List = sqlService_cyber.queryForList("KF276001.SELECT_BNON_PAY_LIST", mfData);
				
				int totCnt = kfAl276001List.size();
				
				if(totCnt == 0) {
					resCode = "000";   /*���γ�������*/
				} else {
					
					/*���γ��� ����*/
					MapForm mfCmd276001List = kfAl276001List.get(0);
					
					if ( mfCmd276001List == null  ||  mfCmd276001List.isEmpty() )   {
						/*���γ�������*/
						resCode = "000";
					} else {
						/*���γ����� �ִ� ���*/
						if(mfCmd276001List.getMap("SNTG").equals("9")) {  /*���*/
							
							resCode = "000";
							
						} else {
							
							resCode = "331";  /*���α����*/
	
						}
					}
					
				}
				
				/*���γ����� �������� �ʰ� ��ҵ� ��� ����ó��*/
				if(resCode.equals("000")){
					
					/*�ΰ�����Ʈ �˻�*/
					ArrayList<MapForm>  alKfLevyList = sqlService_cyber.queryForList("KF276001.SELECT_BNON_SEARCH_LIST", mfData);
					
					if(alKfLevyList.size() == 0) {
						
						resCode = "311";  /*������������*/
						
					} else if(alKfLevyList.size() > 1) {
						
						resCode = "094";  /*��������2�� 1���ΰ͸� ó��*/
						
					} else {
						
						MapForm mpKfLevyList = alKfLevyList.get(0);
	
						long recip_amt = 0;
						
						/*  
						 * �����ڷ�� �ΰ��ڷ� �����ͺ�(1)
						 */
						if(cUtil.getNapGubun((String)mpKfLevyList.getMap("DUE_DT"), (String)mpKfLevyList.getMap("DUE_F_DT")).toString().equals("B")){
							recip_amt = ((BigDecimal)mpKfLevyList.getMap("AMT")).longValue(); 
						} else recip_amt = ((BigDecimal)mpKfLevyList.getMap("AFTAMT")).longValue(); 
						
						if(recvResultMap.getMap("NAPBU_AMT").equals(String.valueOf(recip_amt))){
							
							/* �����ڷ� �ֹ�(����.�����) ��ȣ�� �ΰ����� �ֹ�(����.�����) ��ȣ �ٸ� */ 
							if(mfData.getMap("JUMINNO").equals(mpKfLevyList.getMap("REG_NO"))){
								
								resCode = "340";
								
							}
							/* �����ڷ� ���ڳ��ι�ȣ�� �ΰ����� ���ڳ��ι�ȣ �ٸ� */
							if(mfData.getMap("EPAYNO").equals(mpKfLevyList.getMap("EPAY_NO"))){
								
								resCode = "341";
								
							}
							
						/* ���αݾ� Ʋ�� */
						} else resCode = "343";
						
						if (resCode.equals("000")) {
							/*
							 * �� ��(1)
							 */
							
							/*������������*/
							/*���ڼ������̺� ���������� ���Ѵ�. �ߺ�ó���� �����ϹǷ�...*/
							int rcp_cnt = 0;
							
							if(totCnt == 0){
								rcp_cnt = 0;
							} else {
								rcp_cnt = sqlService_cyber.getOneFieldInteger("KF276001.TX2211_TB_MaxPayCnt", mfData);
							}
							
							mpKfLevyList.setMap("PAY_CNT" , rcp_cnt);
							mpKfLevyList.setMap("SUM_RCP" , recvResultMap.getMap("NAPBU_AMT"));   /*���αݾ�*/
							mpKfLevyList.setMap("PAY_DT"  , recvResultMap.getMap("PAY_DT"));  /*��������*/
							mpKfLevyList.setMap("SNTG"    , "1");                      /*���λ���*/
							mpKfLevyList.setMap("SNSU"    , "1");                      /*������ü 1 ����������*/
							mpKfLevyList.setMap("BANK_CD" , CbUtil.lPadString(mfData.getMap("BANK_CD").toString(), 7, '0').substring(0,3));      /*���α��*/
							mpKfLevyList.setMap("BRC_NO"  , CbUtil.lPadString(mfData.getMap("BANK_CD").toString(), 7, '0'));                                /*(����)���α��*/
							mpKfLevyList.setMap("RSN_YN"  , (mfData.getMap("NAPBU_GB").equals("R")) ? "Y" : "N");     /*���α��� Q����  R��ȸ*/
							mpKfLevyList.setMap("TRTG"    , "0");                      /*�ڷ����ۻ���*/
							mpKfLevyList.setMap("TMSG_NO" , headMap.getMap("BCJ_NO")); /*�������_����������ȣ*/
							
							
							int intLog = sqlService_cyber.queryForUpdate("KF276001.INSERT_TX2211_TB_EPAY", mpKfLevyList);
							
							if(intLog > 0) {
								
								if(mpKfLevyList.getMap("TAX_GB").equals("1")) {
									
									sqlService_cyber.queryForUpdate("KF276001.UPDATE_TX2112_TB_NAPBU_INFO", mpKfLevyList);
									
									log.info("���ܼ��� �ϰ����ڳ���ó�� �Ϸ�! [" + mfData.getMap("EPAYNO") + "]");
									
								} else {
									
									sqlService_cyber.queryForUpdate("KF276001.UPDATE_TX2122_TB_NAPBU_INFO", mpKfLevyList);
									
									log.info("�����ܼ��� ���ڳ���ó�� �Ϸ�! [" + mf.getMap("GRNO") + "]");
									
								}
								
								
								
								MapForm mpSMS = new MapForm();	
								
								mpSMS.setMap("REG_NO"  , mpKfLevyList.getMap("REG_NO"));
								mpSMS.setMap("TAX_ITEM", mpKfLevyList.getMap("TAX_ITEM"));
								mpSMS.setMap("SUM_RCP" , recvResultMap.getMap("NAPBU_AMT"));
								
								try {
									sqlService_cyber.procedure("CODMBASE.SMSCALL", mpSMS);
								}catch (Exception e) {
									log.info("SMS ��ϵ����� = " + mpSMS.getMaps());
									log.info("SMS ��Ͽ��� �߻�");
								}
							}
						}
					}

				}
				/* �����ڵ� �� */
				recvResultMap.setMap("TREAT_CODE", resCode);
				
				alRepeatData.add(recvResultMap);
			}
			
			log.info(cUtil.msgPrint("3", "","KP276001 chk_kf_276001()", resCode));
			
        } catch (Exception e) {
			
        	resCode = "093";  //Error : ���ڳ��ι�ȣ, ���밡��ȣ ����
        	
			e.printStackTrace();
			log.error("============================================");
			log.error("== chk_kf_276001 Exception(�ý���) ");
			log.error("============================================");
		}
        
        /*���⼭ ������带 �����Ѵ�. �ٲ�� �κи� �ٽ� �����ؼ� ������ ����*/
        headMap.setMap("RS_FLAG"   , "G");         /*�����̿���(G) */
        headMap.setMap("RESP_CODE" , resCode);     /*�����ڵ�*/
        headMap.setMap("GCJ_NO"    , "0" + sg_code + "0" + CbUtil.lPadString(String.valueOf(SeqNumber()), 8, '0'));     /*�̿��� �Ϸù�ȣ*/
        headMap.setMap("TX_GUBUN"  , "0210");
        
        return kfField.makeSendReptBuffer(headMap, mfData, alRepeatData);
	}

	/*----------------------------------------------*/
	/* �Ϸù�ȣ �������� �׽�Ʈ*/
	/*----------------------------------------------*/
	private int SeqNumber(){
    	return (Integer)sqlService_cyber.queryForBean("CODMBASE.CODM_GETSEQNO", "00");
    }
}
