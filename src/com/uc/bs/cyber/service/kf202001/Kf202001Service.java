/**
 *  �ֽý��۸� : ���̹����漼û ��ȭ
 *  ��  ��  �� : ������-ȯ�氳���δ�� ���ΰ������/ �����û 
 *  ��  ��  �� : ������-���̹���û 
 *               ����ó��
 *               
 *  Ŭ����  ID : Kf202001Service
 *  ����  �̷� :
 * ------------------------------------------------------------------------
 *  �ۼ���     �Ҽ�  		 ����      Tag   ����
 * ------------------------------------------------------------------------   
 * �ҵ���   ��ä��(��)    2011.06.13   %01%  �ű��ۼ�
 *  
 */
package com.uc.bs.cyber.service.kf202001;

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
public class Kf202001Service {

	protected Log log = LogFactory.getLog(this.getClass());
	
	private ApplicationContext appContext = null;
	
	private IbatisBaseService sqlService_cyber = null;
		
	private Kf202001FieldList kfField = null;
	
	private int[] svcTms = new int[2];
	
	CbUtil cUtil = null;
	
	/**
	 * ������
	 */
	public Kf202001Service(ApplicationContext appContext) {
		// TODO Auto-generated constructor stub
		
		setAppContext(appContext);
		
		kfField = new Kf202001FieldList();
		
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
	 * ȯ�氳���δ�� ���ΰ�� ���� / ���ೳ��
	 * */
	public byte[] chk_kf_202001(MapForm headMap, MapForm mf) throws Exception {
		
		/*ȯ�氳���δ�� ����������ȸ �� ��� */
		String resCode = "000";       /*�����ڵ�*/
		String giro_no = "1002641";   /*�λ�������ڵ�*/
		String sg_code = "26";        /*�λ�ñ���ڵ�*/
		int curTime = 0;

		try{
			
			/*���� �ð�*/
			svcTms[0] = 0030;
			svcTms[1] = 2200;
			
			
			cUtil = CbUtil.getInstance();
			
			log.info(cUtil.msgPrint("3", "S","KP202001 chk_kf_202001()", resCode));
			
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
			
			ArrayList<MapForm>  kfAl202001List = sqlService_cyber.queryForList("KF202001.SELECT_ENV_PAY_LIST", mf);
			
			int totCnt = kfAl202001List.size();
			
			if(totCnt == 0) {
				resCode = "000";   /*���γ�������*/
			} else {
				
				/*���γ��� ����*/
				MapForm mfCmd202001List = kfAl202001List.get(0);
				
				if ( mfCmd202001List == null  ||  mfCmd202001List.isEmpty() )   {
					/*���γ�������*/
					resCode = "000";
				} else {
					/*������ҳ����� �ִ� ���*/
					if(mfCmd202001List.getMap("SNTG").equals("9")) {  /*���*/
						
						resCode = "000";
						
					} else {
						
						resCode = "331";  /*���α����*/

					}
				}
				
			}
			/* ��ü�뺸�� ���νð� üũ�� �ǹ̰� ����.			
			curTime = Integer.parseInt(headMap.getMap("TX_DATE").toString().substring(6, 10));
			if (curTime >= svcTms[0] && curTime <= svcTms[1]) {
				
			} else {
				resCode = "092";
			}
			*/
			/*���γ����� �������� �ʰ� ��ҵ� ��� ����ó��*/
			if(resCode.equals("000")){
				
				/*�ΰ�����Ʈ �˻�*/
				ArrayList<MapForm>  alKfLevyList = sqlService_cyber.queryForList("KF201001.SELECT_ENV_SEARCH_LIST", mf);
				
				if(alKfLevyList.size() == 0) {
					
					resCode = "311";  /*������������*/
					
				} else if(alKfLevyList.size() > 1) {
					
					resCode = "094";  /*��������2�� 1���ΰ͸� ó��*/
					
				} else {
					
					MapForm mpKfLevyList = alKfLevyList.get(0);
					
					/*������������*/
					/*���ڼ������̺� ���������� ���Ѵ�. �ߺ�ó���� �����ϹǷ�...*/
					mpKfLevyList.setMap("PAY_CNT" , sqlService_cyber.getOneFieldInteger("KF202001.TX2231_TB_MaxPayCnt", mpKfLevyList));
					mpKfLevyList.setMap("SUM_RCP" , mf.getMap("NAPBU_AMT"));   /*���αݾ�*/
					mpKfLevyList.setMap("PAY_DT"  , mf.getMap("NAPBU_DATE"));  /*��������*/
					mpKfLevyList.setMap("SNTG"    , "1");                      /*���λ���*/
					mpKfLevyList.setMap("SNSU"    , "1");                      /*������ü 1 ����������*/
					mpKfLevyList.setMap("BANK_CD" , CbUtil.lPadString(mf.getMap("OUTBANK_CODE").toString(), 7, '0').substring(0,3));    /*���α��*/
					mpKfLevyList.setMap("BRC_NO"  , CbUtil.lPadString(mf.getMap("OUTBANK_CODE").toString(), 7, '0'));                   /*(����)���α��*/
					mpKfLevyList.setMap("RSN_YN"  , (mf.getMap("NAPBU_GUBUN").equals("R")) ? "Y" : "N");     /*���α��� Q����  R��ȸ*/
					mpKfLevyList.setMap("TRTG"    , "0");                      /*�ڷ����ۻ���*/
					mpKfLevyList.setMap("TMSG_NO" , headMap.getMap("BCJ_NO")); /*�������_����������ȣ*/
					
					int intLog = sqlService_cyber.queryForUpdate("KF202001.INSERT_TX2231_TB_EPAY", mpKfLevyList);
					
					if(intLog > 0) {
						
						sqlService_cyber.queryForUpdate("KF202001.UPDATE_TX2132_TB_NAPBU_INFO", mpKfLevyList);
						
						log.info("ȯ�氳���δ�� ���ڳ���ó�� �Ϸ�! [" + mf.getMap("ETAX_NO") + "]");
						
						MapForm mpSMS = new MapForm();	
						
						mpSMS.setMap("REG_NO"  , mpKfLevyList.getMap("REG_NO"));
						mpSMS.setMap("TAX_ITEM", mpKfLevyList.getMap("TAX_ITEM"));
						mpSMS.setMap("SUM_RCP" , mf.getMap("NAPBU_AMT"));
						
						try {
							sqlService_cyber.procedure("CODMBASE.SMSCALL", mpSMS);
						}catch (Exception e) {
							log.info("SMS ��ϵ����� = " + mpSMS.getMaps());
							log.info("SMS ��Ͽ��� �߻�");
						}
						
					}
				}
			}
			
			log.info(cUtil.msgPrint("3", "","KP202001 chk_kf_202001()", resCode));
			
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
        
        return kfField.makeSendBuffer(headMap, mf);
	}

	/*----------------------------------------------*/
	/* �Ϸù�ȣ �������� �׽�Ʈ*/
	/*----------------------------------------------*/
	private int SeqNumber(){
    	return (Integer)sqlService_cyber.queryForBean("CODMBASE.CODM_GETSEQNO", "00");
    }
}
