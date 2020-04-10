/**
 *  �ֽý��۸� : ���̹����漼û ��ȭ
 *  ��  ��  �� : ���ϼ��� ���γ��� ������ȸ 
 *  ��  ��  �� : �����-���̹���û 
 *               ����ó��
 *               ��������� ���ŵ� ������ ���ؼ� �������������͸� �����ϰ� 
 *               �۽��Ѵ�.
 *  Ŭ����  ID : Kf253001Service
 *  ����  �̷� :
 * ------------------------------------------------------------------------
 *  �ۼ���     �Ҽ�  		 ����      Tag   ����
 * ------------------------------------------------------------------------   
 * �ҵ���   ��ä��(��)    2011.06.13   %01%  �ű��ۼ�
 * ��â��   ��ä��(��)  2013.11.04     %02%  ����e���ΰ���(���ܼ���, ȯ�氳���δ��) �߰�
 */
package com.uc.bs.cyber.service.kf253001;

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
public class Kf253001Service {

	protected Log log = LogFactory.getLog(this.getClass());
	
	private ApplicationContext appContext = null;
	
	private IbatisBaseService sqlService_cyber = null;
		
	private Kf253001FieldList kfField = null;
	
	CbUtil cUtil = null;
	
	/**
	 * ������
	 */
	public Kf253001Service(ApplicationContext appContext) {
		// TODO Auto-generated constructor stub
		
		setAppContext(appContext);
		
		kfField = new Kf253001FieldList();
		
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
	 * ���ϼ��� �������� ������ȸ ó��...
	 * */
	public byte[] chk_kf_253001(MapForm headMap, MapForm mf) throws Exception {
			
		MapForm sendMap = new MapForm();
		
		/*���ϼ��� ����������ȸ �� ��� */
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
			
			log.info(cUtil.msgPrint("4", "S","KP253001 chk_kf_253001()", resCode));
			
			/*����üũ*/
			
			/*���*/
			if(!headMap.getMap("GPUB_CODE").equals(sg_code)){
				resCode = "339";
			}
			/*�����������ڵ�*/
			if(!headMap.getMap("GJIRO_NO").equals(giro_no)){
				resCode = "324";
			}
			
			if(resCode.equals("000")) {
				
				POINTNO = (String)mf.getMap("POINTNO"); // ������ȣ
				DATANUM = (String)mf.getMap("DATANUM"); // �����ͰǼ�

				ArrayList<MapForm>  kfCmd253001List  =  sqlService_cyber.queryForList("KF253001.SELECT_SHSD_KFPAY_LIST", mf);
				
				log.debug("JUMINNO = [" + mf.getMap("JUMINNO") + "]");
				log.debug("GPUB_CODE = [" + mf.getMap("GPUB_CODE") + "]");
				log.debug("GJIRO_NO = [" + mf.getMap("GJIRO_NO") + "]");
				log.debug("kfCmd253001List() = [" + kfCmd253001List.size() + "]");
				
				if ( kfCmd253001List.size() > 0 && resCode.equals("000")) {
					
				  //����e���� �׽�Ʈ�� pointno�� '0'���� �� ��� ���� Ȯ�� ����
                    if (Integer.parseInt(POINTNO)<=0) POINTNO = "001";
					
					START_IDX = Integer.parseInt(POINTNO) -1;
					
					if (START_IDX >= kfCmd253001List.size()){
						resCode = "312";
					} else {
						resCode = "000";
						
						/*������ȣ ó������*/
						if (Integer.parseInt(DATANUM) > 10 && Integer.parseInt(DATANUM) < 99 )
			                DATANUM = "10";

			            if ( kfCmd253001List.size() < (START_IDX+Integer.parseInt(DATANUM)) )
			                DATANUM = String.valueOf( kfCmd253001List.size() - START_IDX );

			            if (Integer.parseInt(DATANUM) > 10)
			                DATANUM = "10";
			              
			            /*0210 �������� ���� */
						sendMap.setMap("JUMINNO"   , mf.getMap("JUMINNO"));      /*�ֹι�ȣ   */
						sendMap.setMap("IN_STDATE" , mf.getMap("IN_STDATE"));    /*����   */
						sendMap.setMap("IN_ENDATE" , mf.getMap("IN_ENDATE"));    /*�Ϸ�   */
			            sendMap.setMap("DATATOT"   , kfCmd253001List.size());    /*�����ѰǼ� */
			  			sendMap.setMap("POINTNO"   , POINTNO);                   /*������ȣ   */
						sendMap.setMap("DATANUM"   , DATANUM);                   /*������ȣ�� �����ͰǼ� */

						//for ( int al_cnt = 0;  al_cnt < kfCmd253001List.size();  al_cnt++)   {//2013-10-29 �ݰ�� �̺��� ����� ��ȭ(��ü�������� 1Kbyte����)�� �Ʒ��� ���� by ��â��
		                for ( int al_cnt = START_IDX;  al_cnt < (START_IDX + Integer.parseInt(DATANUM));  al_cnt++)  {
							
							MapForm mfCmd203001List  =  kfCmd253001List.get(al_cnt);
							
							MapForm mfRepeatData = new MapForm();
							
							alRepeatData.add(mfRepeatData);
							
							/*��� ����         */
							/*�ΰ� ���         */
							/*�ΰ� ���         */
							/*���� �ݾ�         */
							/*���� ����         */
							/*���� �̿� �ý���  */ 
							/*�������� ���� �ڵ�*/ 
							mfRepeatData.setMap("DANGGUBUN"   , mfCmd203001List.getMap("GUBUN"));
							mfRepeatData.setMap("CUSTNO"      , mfCmd203001List.getMap("CUST_NO"));
							mfRepeatData.setMap("BYYYMM"      , (String)mfCmd203001List.getMap("TAX_YY") + (String)mfCmd203001List.getMap("TAX_MM"));
							mfRepeatData.setMap("NAPBU_AMT"   , mfCmd203001List.getMap("SUM_RCP"));
							mfRepeatData.setMap("NAPBU_DATE"  , mfCmd203001List.getMap("PAY_DT"));
							mfRepeatData.setMap("NAPBU_SYS"   , mfCmd203001List.getMap("SNSU"));
							mfRepeatData.setMap("JIJUM_CODE"  , mfCmd203001List.getMap("BRC_NO"));							

						} 
					}

				}else{ 
					/* ��ȸ�Ǽ��� ���� ��� ��������
					 * */
					
					resCode = "312";  /*��ȸ��������*/
					sendMap.setMap("DATATOT" , "000");    /*�����ѰǼ� */
					sendMap.setMap("DATANUM" , "00");     /*�����ͰǼ� */
				} 
				
			}
			
			log.info(cUtil.msgPrint("4", "","KP253001 chk_kf_203001()", resCode));

        } catch (Exception e) {
			
			e.printStackTrace();
			log.error("============================================");
			log.error("== chk_kf_253001 Exception(�ý���) ");
			log.error("============================================");
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
