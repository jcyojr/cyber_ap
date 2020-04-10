/**
 *  �ֽý��۸� : ���̹����漼û ��ȭ
 *  ��  ��  �� : ȯ�氳���δ�� �������� ��ȸ ����
 *  ��  ��  �� : �����-���̹���û 
 *               ����ó��
 *               ��������� ���ŵ� ������ ���ؼ� ������������Ʈ�� �����ϰ� 
 *               ��ȸ����� �����Ѵ�.
 *  Ŭ����  ID : Kf201001Service
 *  ����  �̷� :
 * ------------------------------------------------------------------------
 *  �ۼ���     �Ҽ�  		 ����      Tag   ����
 * ------------------------------------------------------------------------   
 * �ҵ���   ��ä��(��)    2011.06.13   %01%  �ű��ۼ�
 * ��â��   ��ä��(��)  2013.11.04     %02%  ����e���ΰ���(���ܼ���, ȯ�氳���δ��) �߰�
 */
package com.uc.bs.cyber.service.kf201001;

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
public class Kf201001Service {

	protected Log log = LogFactory.getLog(this.getClass());
	
	private ApplicationContext appContext = null;
	
	private IbatisBaseService sqlService_cyber = null;
		
	private Kf201001FieldList kfField = null;
	
	CbUtil cUtil = null;
	
	/**
	 * ������
	 */
	public Kf201001Service(ApplicationContext appContext) {
		// TODO Auto-generated constructor stub
		
		setAppContext(appContext);
		
		kfField = new Kf201001FieldList();
		
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
	 * ȯ�氳���δ�� �������� ������ȸ ó��...
	 * */
	public byte[] chk_kf_201001(MapForm headMap, MapForm mf) throws Exception {
		
				
		MapForm sendMap = new MapForm();
		
		/*ȯ�氳���δ�� ����������ȸ �� ��� */
		String resCode = "000";       /*�����ڵ�*/
		String giro_no = "1002641";   /*�λ�������ڵ�*/
		String sg_code = "26";        /*�λ�ñ���ڵ�*/
		
		String POINTNO = "";          /*������ȣ*/
		String DATANUM = "";          /*�����Ͱ���*/
		int START_IDX = 0;
		
		/*�ݺ���*/
		ArrayList<MapForm> alRepeatData = new ArrayList<MapForm>();
		
		try{
			cUtil = CbUtil.getInstance();
			
			log.info(cUtil.msgPrint("1", "S","KP201001 chk_kf_201001()", resCode));
			
			
			/*����üũ*/
			
			/*���*/
			if(!headMap.getMap("GPUB_CODE").equals(sg_code)){
				resCode = "339";
			}
			/*�����������ڵ�*/
			if(!headMap.getMap("GJIRO_NO").equals(giro_no)){
				resCode = "324";
			}
			
			/*�ֹι�ȣ*/
			if(mf.getMap("JUMIN_NO").equals("")){
				resCode = "340";
			}
			
			POINTNO = (String)mf.getMap("POINT_NO"); // ������ȣ
			DATANUM = (String)mf.getMap("DATA_NUM"); // �����ͰǼ�
			
			if(resCode.equals("000")) {

				ArrayList<MapForm>  kfCmd201001List  =  sqlService_cyber.queryForList("KF201001.SELECT_ENV_SEARCH_LIST", mf);
				
				log.debug("EPAY_NO = [" + mf.getMap("ETAX_NO") + "]");
				log.debug("JUMIN_NO = [" + mf.getMap("JUMIN_NO") + "]");
				log.debug("kfCmd201001List() = [" + kfCmd201001List.size() + "]");
				log.debug("resCode = [" + resCode + "]");
				
				if ( kfCmd201001List.size() > 0 ) {
					
					
					//log.info("1111111111111111111111111111111111111");
					
					//����e���� �׽�Ʈ�� pointno�� '0'���� �� ��� ���� Ȯ�� ����
                    if (Integer.parseInt(POINTNO)<=0) POINTNO = "001";
					
					START_IDX = Integer.parseInt(POINTNO) -1;
					
					if (START_IDX >= kfCmd201001List.size()){
						resCode = "311";
					} else {
						resCode = "000";
						
						//log.info("222222222222222222222222222");
						
						/*������ȣ ó������*/
						if (Integer.parseInt(DATANUM) > 10 && Integer.parseInt(DATANUM) < 99 )
			                DATANUM = "10";

			            if (kfCmd201001List.size() < (START_IDX+Integer.parseInt(DATANUM)) )
			                DATANUM = String.valueOf( kfCmd201001List.size() - START_IDX );

			            if (Integer.parseInt(DATANUM) > 10)
			                DATANUM = "10";
			              
			            /*0210 �������� ���� */
			            
			            //log.info("33333333333333333333333333");
			            
						sendMap.setMap("JUMIN_NO", mf.getMap("JUMIN_NO"));    /*�ֹι�ȣ   */
			            sendMap.setMap("DATA_TOT", kfCmd201001List.size());   /*�����ѰǼ� */
			  			sendMap.setMap("POINT_NO", POINTNO);                  /*������ȣ   */
			  			//log.info("444444444444444444");
			  			/*1���̻� ��ȸ�� �� ��� ��������*/
			  			sendMap.setMap("DATA_NUM", DATANUM);                  /*������ȣ �����ͰǼ� */
			  			//log.info("5555555555555555555555555555");

			  			//for ( int al_cnt = 0;  al_cnt < kfCmd201001List.size();  al_cnt++)   { //20120314 7�� ��ȸ������ ��� ���´ٰ��ؼ� ����-���
//						for ( int al_cnt =Integer.parseInt(POINTNO) ;  al_cnt < (Integer.parseInt(DATANUM) + Integer.parseInt(POINTNO));  al_cnt++)   {//2013-10-29 �ݰ�� �̺��� ����� ��ȭ(��ü�������� 1Kbyte����)�� �Ʒ��� ���� by ��â��
	                    for ( int al_cnt = START_IDX;  al_cnt < (START_IDX + Integer.parseInt(DATANUM));  al_cnt++)  {
							
							//log.info("123123123123");
							log.info(al_cnt);
							log.info(DATANUM);
							log.info(POINTNO);
							MapForm mfCmd201001List  =  kfCmd201001List.get(al_cnt - 1);
							//log.info("77777777777777777777777777777777777");
							
							MapForm mfRepeatData = new MapForm();

							//log.info("88888888888888888888888888888888888888");
							alRepeatData.add(mfRepeatData);
							//log.info("99999999999999999999999");
							
                            log.info(headMap.getMap("GPUB_CODE"));
                            log.info(headMap.getMap("GJIRO_NO"));
                            log.info(mfCmd201001List.getMap("EPAY_NO"));
                            log.info(mfCmd201001List.getMap("TAX_ITEM"));
                            log.info((String)mfCmd201001List.getMap("TAX_YY") + (String)mfCmd201001List.getMap("TAX_MM"));
                            log.info(mfCmd201001List.getMap("DLQ_DIV"));   /*�������±��� 0 ����, 4 ü��*/
                            log.info(mfCmd201001List.getMap("ENV_AMT"));
                            log.info(cUtil.getNapGubun((String)mfCmd201001List.getMap("DUE_DT"), (String)mfCmd201001List.getMap("DUE_F_DT")));
                            log.info(mfCmd201001List.getMap("DUE_DT"));
                            log.info("N");
							
							mfRepeatData.setMap("PUBG_CODE"  , headMap.getMap("GPUB_CODE"));
							mfRepeatData.setMap("GIRO_NO"    , headMap.getMap("GJIRO_NO"));
							mfRepeatData.setMap("ETAX_NO"    , mfCmd201001List.getMap("EPAY_NO"));
							mfRepeatData.setMap("GWA_MOK"    , mfCmd201001List.getMap("TAX_ITEM"));
							mfRepeatData.setMap("GWA_DATE"   , (String)mfCmd201001List.getMap("TAX_YY") + (String)mfCmd201001List.getMap("TAX_MM"));
							mfRepeatData.setMap("KIBUN"      , mfCmd201001List.getMap("DLQ_DIV"));   /*�������±��� 0 ����, 4 ü��*/
							mfRepeatData.setMap("NAP_AMT"    , mfCmd201001List.getMap("ENV_AMT"));
							mfRepeatData.setMap("NAP_GUBUN"  , cUtil.getNapGubun((String)mfCmd201001List.getMap("DUE_DT"), (String)mfCmd201001List.getMap("DUE_F_DT")));
							mfRepeatData.setMap("NAP_DATE"   , mfCmd201001List.getMap("DUE_DT"));
							mfRepeatData.setMap("AUTO_REG"   , "N");
						} 
					}
					
				}else{ 
					/* ��ȸ�Ǽ��� ���� ��� ��������
					 * */
					resCode = "311";  /*��ȸ��������*/
					
					
				} 
				
			}
			
			log.info(cUtil.msgPrint("1", "","KP201001 chk_kf_201001()", resCode));

        } catch (Exception e) {
			
			e.printStackTrace();
			log.error("============================================");
			log.error("== chk_kf_201001 Exception(�ý���) ");
			log.error("============================================");
		}
        
        /*������ �ִ� ���� ���������� �״�� ����*/
        if (!resCode.equals("000")) {
        	sendMap = mf;
        	sendMap.setMap("DATA_TOT" , "000");  /*�����ѰǼ� */
			sendMap.setMap("DATA_NUM" , "00");   /*�����ͰǼ� */
        }
        
        /*���⼭ ������带 �����Ѵ�. �ٲ�� �κи� �ٽ� �����ؼ� ������ ����*/
        headMap.setMap("RS_FLAG"   , "G");         /*�����̿���(G) */
        headMap.setMap("RESP_CODE" , resCode);     /*�����ڵ�*/
        headMap.setMap("GCJ_NO"    , "0" + sg_code + "0" + CbUtil.lPadString(String.valueOf(SeqNumber()), 8, '0'));     /*�̿��� �Ϸù�ȣ*/
        headMap.setMap("TX_GUBUN"  , "0210");
        
        return kfField.makeSendReptBuffer(headMap, sendMap, alRepeatData);
	}

	/*----------------------------------------------*/
	/* �Ϸù�ȣ �������� �׽�Ʈ*/
	/*----------------------------------------------*/
	private int SeqNumber(){
    	return (Integer)sqlService_cyber.queryForBean("CODMBASE.CODM_GETSEQNO", "00");
    }
}
