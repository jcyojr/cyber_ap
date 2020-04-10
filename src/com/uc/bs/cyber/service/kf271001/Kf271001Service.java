/**
 *  �ֽý��۸� : ���̹����漼û ��ȭ
 *  ��  ��  �� : ���ܼ��� �������� ��ȸ ����
 *  ��  ��  �� : �����-���̹���û 
 *               ����ó��
 *               ��������� ���ŵ� ������ ���ؼ� ������������Ʈ�� �����ϰ� 
 *               ��ȸ����� �����Ѵ�.
 *  Ŭ����  ID : Kf271001Service
 *  ����  �̷� :
 * ------------------------------------------------------------------------
 *  �ۼ���     �Ҽ�  		 ����      Tag   ����
 * ------------------------------------------------------------------------   
 * �ҵ���   ��ä��(��)    2011.06.13   %01%  �ű��ۼ�
 * ��â��   ��ä��(��)  2013.11.04     %02%  ����e���ΰ���(���ܼ���, ȯ�氳���δ��) �߰�
 */
package com.uc.bs.cyber.service.kf271001;

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
public class Kf271001Service {

	protected Log log = LogFactory.getLog(this.getClass());
	
	private ApplicationContext appContext = null;
	
	private IbatisBaseService sqlService_cyber = null;
		
	private Kf271001FieldList kfField = null;
	
	CbUtil cUtil = null;
	
	/**
	 * ������
	 */
	public Kf271001Service(ApplicationContext appContext) {
		// TODO Auto-generated constructor stub
		
		setAppContext(appContext);
		
		kfField = new Kf271001FieldList();
		
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
	 * ���ܼ��� �������� ������ȸ ó��...
	 * */
	public byte[] chk_kf_271001(MapForm headMap, MapForm mf) throws Exception {
		
				
		MapForm sendMap = new MapForm();
		
		/*���ܼ��� ����������ȸ �� ��� */
		String resCode = "000";       /*�����ڵ�*/
		String giro_no = "1500172";   /*�λ�������ڵ�*/
		String sg_code = "26";        /*�λ�ñ���ڵ�*/
		
		String POINTNO = "";          /*������ȣ*/
		String DATANUM = "";          /*�����Ͱ���*/
		int START_IDX = 0;
		
		/*�ݺ���*/
		ArrayList<MapForm> alRepeatData = new ArrayList<MapForm>();
		
		try{
			cUtil = CbUtil.getInstance();
			
			log.info(cUtil.msgPrint("1","S","KP271001 chk_kf_271001()", resCode));
			
			/*���*/
			if(!headMap.getMap("GPUB_CODE").equals(sg_code)){
				resCode = "339";
			}
			/*�����������ڵ�*/
			if(!headMap.getMap("GJIRO_NO").equals(giro_no)){
				resCode = "324";
			}
			log.info("mf.getMap(JUMIN_NO) = " + mf.getMap("JUMIN_NO"));
			/*�Ǹ��ȣ ���翩��üũ*/
            //if("".equals(mf.getMap("JUMIN_NO").toString().trim()) || mf.getMap("JUMIN_NO").toString().trim()==null){
            if(mf.getMap("JUMIN_NO").toString().trim().length() < 10 || mf.getMap("JUMIN_NO").toString().trim()==null){
                resCode = "340";
            }
            
            
		
			if(resCode.equals("000")) {
				
				ArrayList<MapForm>  kfCmd271001List  =  sqlService_cyber.queryForList("KF271001.SELECT_BNON_SEARCH_LIST", mf);
				
				POINTNO = (String)mf.getMap("POINT_NO"); // ������ȣ
				DATANUM = (String)mf.getMap("DATA_NUM"); // �����ͰǼ�
				log.info("�����ڵ� 000(����)");
				if ( kfCmd271001List.size() > 0) {
					
				  //����e���� �׽�Ʈ�� pointno�� '0'���� �� ��� ���� Ȯ�� ����
                    if (Integer.parseInt(POINTNO)<=0) POINTNO = "001";
					
					START_IDX = Integer.parseInt(POINTNO) -1;
					
					if (START_IDX >= kfCmd271001List.size()){
						resCode = "311";
					} else {
						resCode = "000";
						
						/*������ȣ ó������*/
						if (Integer.parseInt(DATANUM) > 9 && Integer.parseInt(DATANUM) < 99 )
			                DATANUM = "9";

			            if ( kfCmd271001List.size() < (START_IDX + Integer.parseInt(DATANUM)) )
			                DATANUM = String.valueOf( kfCmd271001List.size() - START_IDX );

			            if (Integer.parseInt(DATANUM) > 9)
			                DATANUM = "9";
			              
			            /*0210 �������� ���� */
						sendMap.setMap("JUMIN_NO", mf.getMap("JUMIN_NO"));    /*�ֹι�ȣ   */
			            sendMap.setMap("DATA_TOT", kfCmd271001List.size());   /*�����ѰǼ� */
			  			sendMap.setMap("POINT_NO", POINTNO);                  /*������ȣ   */
			  			
			  			/*1���̻� ��ȸ�� �� ��� ��������*/
			  			sendMap.setMap("DATA_NUM", kfCmd271001List.size());   /*�����ͰǼ� */

						//for ( int al_cnt = 0;  al_cnt < kfCmd271001List.size();  al_cnt++)   {//2013-10-29 �ݰ�� �̺��� ����� ��ȭ(��ü�������� 1Kbyte����)�� �Ʒ��� ���� by ��â��
		                for ( int al_cnt = START_IDX;  al_cnt < (START_IDX + Integer.parseInt(DATANUM));  al_cnt++)  {
							
							MapForm mfCmd271001List  =  kfCmd271001List.get(al_cnt);
							
							MapForm mfRepeatData = new MapForm();
							
							mfRepeatData.setMap("PUBGCODE"   , headMap.getMap("GPUB_CODE"));
							mfRepeatData.setMap("JIRONO"     , headMap.getMap("GJIRO_NO"));
							mfRepeatData.setMap("TAX_NO"     , mfCmd271001List.getMap("TAX_NO"));
							mfRepeatData.setMap("GRNO"    	 , (mfCmd271001List.getMap("EPAY_NO").toString().length() == 17) ? mfCmd271001List.getMap("EPAY_NO") + "00" : mfCmd271001List.getMap("EPAY_NO"));
							mfRepeatData.setMap("GWA_MOK"    , mfCmd271001List.getMap("TAX_ITEM"));
							mfRepeatData.setMap("GWA_DATE"   , mfCmd271001List.getMap("TAX_YM"));
							mfRepeatData.setMap("KIBUN"      , mfCmd271001List.getMap("BUGWA_STAT"));
							mfRepeatData.setMap("NAP_AMT"    , mfCmd271001List.getMap("BNON_AMT"));
							mfRepeatData.setMap("NAP_GUBUN"  , cUtil.getNapGubun((String)mfCmd271001List.getMap("DUE_DT"), (String)mfCmd271001List.getMap("DUE_F_DT")));
							mfRepeatData.setMap("NAP_DATE"   , mfCmd271001List.getMap("GYMD"));
							mfRepeatData.setMap("AUTO_REG"   , "N");
							
							alRepeatData.add(mfRepeatData);
							
							log.debug("mfCmd271001List.getMap(TAX_NO) = [" + mfCmd271001List.getMap("TAX_NO") + "]");
							
						} 
					}
					
				}else{ 
					/* ��ȸ�Ǽ��� ���� ��� ��������
					 * */
					
					resCode = "311";  /*��ȸ��������*/

				} 
				
				
			}
			
			log.info(cUtil.msgPrint("1","","KP271001", resCode));
			
        } catch (Exception e) {
			
			e.printStackTrace();
			log.error(e.getMessage()+"Error");
			log.error("============================================");
			log.error("== chk_kf_271001 Exception(�ý���) ");
			log.error("============================================");
		}
        
        if(!resCode.equals("000")) {
        	sendMap = mf;     /*���������� ��� �� ���� �״��...*/
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

