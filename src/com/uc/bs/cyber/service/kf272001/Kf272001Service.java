/**
 *  �ֽý��۸� : ���̹����漼û ��ȭ
 *  ��  ��  �� : ������-���ܼ���(��Ÿ���Ա�) ���ΰ������/ �����û 
 *  ��  ��  �� : ������-���̹���û 
 *               ����ó��
 *               
 *  Ŭ����  ID : Kf272001Service
 *  ����  �̷� :
 * ------------------------------------------------------------------------
 *  �ۼ���     �Ҽ�  		 ����      Tag   ����
 * ------------------------------------------------------------------------   
 * �۵���   ��ä��(��)    2011.06.13   %01%  �ű��ۼ�
 *  
 */
package com.uc.bs.cyber.service.kf272001;

import java.math.BigDecimal;
import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.dao.DuplicateKeyException;

import com.uc.core.MapForm;
import com.uc.core.spring.service.IbatisBaseService;
import com.uc.bs.cyber.CbUtil;

/**
 * @author Administrator
 *
 */
public class Kf272001Service {

	protected Log log = LogFactory.getLog(this.getClass());
	
	private ApplicationContext appContext = null;
	
	private IbatisBaseService sqlService_cyber = null;
		
	private Kf272001FieldList kfField = null;
	
	private int[] svcTms = new int[2];
	
	CbUtil cUtil = null;
	
	/**
	 * ������
	 */
	public Kf272001Service(ApplicationContext appContext) {
		// TODO Auto-generated constructor stub
		
		setAppContext(appContext);
		
		kfField = new Kf272001FieldList();
		
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
	 * ��Ÿ���Ա� ���ΰ�� ���� / ���ೳ��
	 * */
	public byte[] chk_kf_272001(MapForm headMap, MapForm mf) throws Exception {
		
		/*��Ÿ���Ա� ����������ȸ �� ��� */
		String resCode = "000";        /*�����ڵ�*/
//		String giro_no = "1002641";    /*�λ�������ڵ�*/
		String giro_no = "1500172";    /*�λ�������ڵ�- ����e���ν������� ��Ÿ���Ա� �����ڵ� ����*/
		String sg_code = "26";         /*�λ�ñ���ڵ�*/
		int curTime = 0;

		try{
			/*���� �ð�*/
			svcTms[0] = 0030;
			svcTms[1] = 2200;
			
			cUtil = CbUtil.getInstance();
			
			log.info(cUtil.msgPrint("3", "S","KP272001 chk_kf_272001()", resCode));

			/*����üũ*/
			
			/*���*/
			if(!headMap.getMap("GPUB_CODE").equals(sg_code)){
				resCode = "339";
			}
			/*�����������ڵ�*/
			if(!headMap.getMap("GJIRO_NO").equals(giro_no)){
				resCode = "324";
			}
			
			
			/*���ڳ��ι�ȣüũ*/
			if(mf.getMap("GRNO").toString().length() == 17) {
				mf.setMap("GRNO", mf.getMap("GRNO").toString() + "00");
			}
			
			if(mf.getMap("GRNO").toString().length() != 19) {
				resCode = "341";
			}
			
			
			/*���������� ������ ������ �޾� ��ȸ����� Ȯ���Ѵ�.*/
			ArrayList<MapForm>  kfAl272001List = sqlService_cyber.queryForList("KF272001.SELECT_BNON_PAY_LIST", mf);
			
			int totCnt = kfAl272001List.size();
			
			if(totCnt == 0) {
				resCode = "000";   /*���γ�������*/
			} else {
				
				/*���γ��� ����*/
				MapForm mfCmd272001List = kfAl272001List.get(0);
				
				if ( mfCmd272001List == null  ||  mfCmd272001List.isEmpty() )   {
					/*���γ�������*/
					resCode = "000";
				} else {
					/*���γ����� �ִ� ���*/
					if(mfCmd272001List.getMap("SNTG").equals("9")) {  /*���*/
						
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
				ArrayList<MapForm>  alKfLevyList = sqlService_cyber.queryForList("KF272001.SELECT_BNON_SEARCH_LIST", mf);

				if(alKfLevyList.size() == 0) {
					
					resCode = "311";  /*������������*/
					
				} else if(alKfLevyList.size() > 1) {
					
					resCode = "093";  /*��������2�� 1���ΰ͸� ó��*/

				} else {
					
					MapForm mpKfLevyList = alKfLevyList.get(0);

					long recip_amt = 0;
					
					/*  
					 * �����ڷ�� �ΰ��ڷ� �����ͺ�(1)
					 */
					if(cUtil.getNapGubun((String)mpKfLevyList.getMap("DUE_DT"), (String)mpKfLevyList.getMap("DUE_F_DT")).toString().equals("B")){
						recip_amt = ((BigDecimal)mpKfLevyList.getMap("AMT")).longValue(); 
					} else {
						recip_amt = ((BigDecimal)mpKfLevyList.getMap("AFTAMT")).longValue(); 
					}
					
					if(mf.getMap("NAPBU_AMT").equals(String.valueOf(recip_amt))){
						
						/* �����ڷ� �ֹ�(����.�����) ��ȣ�� �ΰ����� �ֹ�(����.�����) ��ȣ �ٸ� */ 
						if(!mf.getMap("JUMIN_NO").equals(mpKfLevyList.getMap("REG_NO"))){
							
							resCode = "340";
							
						}
						
					/* ���αݾ� Ʋ�� */
					} else {
						
						resCode = "343";
					}
					
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

							if(mpKfLevyList.getMap("TAX_GB").equals("1")) {
								rcp_cnt = sqlService_cyber.getOneFieldInteger("KF272001.TX2211_TB_MaxPayCnt", mpKfLevyList);
							} else {
								rcp_cnt = sqlService_cyber.getOneFieldInteger("KF272001.TX2221_TB_MaxPayCnt", mf);
							}

						}		
						
						mpKfLevyList.setMap("PAY_CNT" , rcp_cnt);
						mpKfLevyList.setMap("SUM_RCP" , mf.getMap("NAPBU_AMT"));     /*���αݾ�*/
						mpKfLevyList.setMap("PAY_DT"  , mf.getMap("NAPBU_DATE"));    /*��������*/
						mpKfLevyList.setMap("SNTG"    , "1");                        /*���λ���*/
						mpKfLevyList.setMap("SNSU"    , headMap.getMap("PRC_GB").equals("KP") ? "1" : "3");      /*������ü 1 ���������� 3:����*/
						mpKfLevyList.setMap("BANK_CD" , CbUtil.lPadString(mf.getMap("OUTBANK_CODE").toString(), 7, '0').substring(0,3));    /*���α��*/
						mpKfLevyList.setMap("RSN_YN"  , (mf.getMap("NAPBU_GUBUN").equals("R")) ? "Y" : "N");     /*���α��� Q����  R��ȸ*/
						mpKfLevyList.setMap("TRTG"    , "0");                        /*�ڷ����ۻ���*/
						mpKfLevyList.setMap("BRC_NO"  , CbUtil.lPadString(mf.getMap("OUTBANK_CODE").toString(), 7, '0'));  /*��������ڵ�*/
						mpKfLevyList.setMap("TMSG_NO" , headMap.getMap("BCJ_NO"));   /*�������_����������ȣ*/
						/*�����ܸ� ����*/
						mpKfLevyList.setMap("GRNO"     , mf.getMap("GRNO"));       /*���ڳ���*/
						mpKfLevyList.setMap("JUMIN_NO" , mf.getMap("JUMIN_NO"));   /*�ֹι�ȣ*/
						
						int intLog = 0;
						if(mpKfLevyList.getMap("TAX_GB").equals("1")) {
							intLog = sqlService_cyber.queryForUpdate("KF272001.INSERT_TX2211_TB_EPAY", mpKfLevyList);  /*ǥ�ؼ��ܼ���*/
						} else {
							intLog = sqlService_cyber.queryForUpdate("KF272001.INSERT_TX2221_TB_EPAY", mpKfLevyList);  /*�����ܼ��� : ��������, �ְ���*/
						}		
						if(intLog > 0) {
							
							if(mpKfLevyList.getMap("TAX_GB").equals("1")) {
								sqlService_cyber.queryForUpdate("KF272001.UPDATE_TX2112_TB_NAPBU_INFO", mpKfLevyList);
								
								log.info("���ܼ��� ���ڳ���ó�� �Ϸ�! [" + mf.getMap("GRNO") + "]");
							}else{								
								sqlService_cyber.queryForUpdate("KF272001.UPDATE_TX2122_TB_NAPBU_INFO", mpKfLevyList);
								
								log.info("�����ܼ��� ���ڳ���ó�� �Ϸ�! [" + mf.getMap("GRNO") + "]");
							}

							//�α��۾��� : ����
							//mf.setMap("TAX_NO"    , mf.getMap("TAX_NO"))
							mf.setMap("TAX_NO"    , mpKfLevyList.getMap("TAX_NO"));	//�α�������� by ��â��
							mf.setMap("JUMIN_NO"  , mf.getMap("JUMIN_NO"));
							mf.setMap("NAPBU_AMT" , mf.getMap("NAPBU_AMT"));  
							mf.setMap("PAY_DT"    , mf.getMap("NAPBU_DATE"));
							mf.setMap("TX_GB"     , mpKfLevyList.getMap("TAX_GB"));  
							
							try{
								sqlService_cyber.queryForUpdate("KF271002.INSERT_TX2421_TB_LOG", mf);
							}catch (Exception e) {
								if (e instanceof DuplicateKeyException){
									log.info("����: TX2421_TB ���̺� �̹� ��ϵ� ������");
								} else {
									log.error("���� ������ = " + mf.getMaps());
								    log.error("Logging  failed!!!");	
								}
							}
							
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
			}
			
			log.info(cUtil.msgPrint("3", "","KP272001 chk_kf_272001()", resCode));
			
        } catch (Exception e) {
			
        	resCode = "093";  //Error : ���ڳ��ι�ȣ, ���밡��ȣ ����
        	
			e.printStackTrace();
			log.error("============================================");
			log.error("== chk_kf_272001 Exception(�ý���) ");
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
