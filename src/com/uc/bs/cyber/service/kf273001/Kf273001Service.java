/**
 *  �ֽý��۸� : ���̹����漼û ��ȭ
 *  ��  ��  �� : ���ܼ��� ���γ��� ������ȸ ����
 *  ��  ��  �� : �λ�����-���̹���û 
 *               ����ó��
 *               ��������� ���ŵ� ������ ���ؼ� ������������Ʈ�� �����ϰ� 
 *               ��ȸ����� �����Ѵ�.
 *  Ŭ����  ID : Kf273001Service
 *  ����  �̷� :
 * ------------------------------------------------------------------------
 *  �ۼ���     �Ҽ�  		 ����      Tag   ����
 * ------------------------------------------------------------------------   
 * Ȳ����   �ٻ�(��)    2011.06.13   %01%  �ű��ۼ�
 * ��â��   ��ä��(��)  2013.11.04     %02%  ����e���ΰ���(���ܼ���, ȯ�氳���δ��) �߰�
 */
package com.uc.bs.cyber.service.kf273001;

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
public class Kf273001Service {

	protected Log log = LogFactory.getLog(this.getClass());
	
	private ApplicationContext appContext = null;
	
	private IbatisBaseService sqlService_cyber = null;
		
	private Kf273001FieldList kfField = null;
	
	CbUtil cUtil = null;
	
	/**
	 * ������
	 */
	public Kf273001Service(ApplicationContext appContext) {
		// TODO Auto-generated constructor stub
		
		setAppContext(appContext);
		
		kfField = new Kf273001FieldList();
		
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
	 * ��Ÿ���Ա� ���γ��� ������ȸ ó��...
	 * */
	public byte[] chk_kf_273001(MapForm headMap, MapForm mf) throws Exception {
		
		MapForm sendMap = new MapForm();
		
		/*��Ÿ���Ա� ����������ȸ �� ��� */
		String resCode = "000";       /*�����ڵ�*/
//		String giro_no = "1000685";   /*�λ�������ڵ�*/ // ���� �������� �ٱžߴ� 
		String giro_no = "1500172";    /*�λ�������ڵ�- ����e���ν������� ��Ÿ���Ա� �����ڵ� ����*/
		String sg_code = "26";        /*�λ�ñ���ڵ�*/
		
		String POINTNO = "";          /*������ȣ*/
		String DATANUM = "";          /*�����Ͱ���*/
		int START_IDX  = 0;
		
		/*�ݺ���*/
		ArrayList<MapForm> alRepeatData = new ArrayList<MapForm>();
		
		try{
			cUtil = CbUtil.getInstance();
			
			log.info(cUtil.msgPrint("8", "S","BP273001 chk_kf_273001()", resCode));
			
			/*����üũ*/
			
			/*���*/
			if(!headMap.getMap("GPUB_CODE").equals(sg_code)){
				resCode = "122";
			}
			/*�����������ڵ�*/
			if(!headMap.getMap("GJIRO_NO").equals(giro_no)){
				resCode = "123";
			}
			
			/*�ֹι�ȣ*/
			if(mf.getMap("JUMINNO").toString().length() != 13){
				resCode = "340";
			}
			
			POINTNO = (String)mf.getMap("POINTNO"); // ������ȣ
			DATANUM = (String)mf.getMap("DATANUM"); // �����ͰǼ�

			if(resCode.equals("000")) {
				
				ArrayList<MapForm>  bsCmd273001List  =  sqlService_cyber.queryForList("KF273001.SELECT_RECIP_LIST", mf);
				
				if ( bsCmd273001List.size() > 0 ) {
					
				  //����e���� �׽�Ʈ�� pointno�� '0'���� �� ��� ���� Ȯ�� ����
                    if (Integer.parseInt(POINTNO)<=0) POINTNO = "001";
					
					START_IDX = Integer.parseInt(POINTNO) -1;
					
					if (START_IDX >= bsCmd273001List.size()){
						resCode = "112";
					} else {
						resCode = "000";
						
						/*������ȣ ó������*/
						if (Integer.parseInt(DATANUM) > 10 )
			                DATANUM = "10";
						else if ( bsCmd273001List.size() < (START_IDX+Integer.parseInt(DATANUM)) )
			                DATANUM = String.valueOf( bsCmd273001List.size() - START_IDX );
			              
						/*0210 �������� ���� */
						sendMap.setMap("JUMINNO" , mf.getMap("JUMINNO"));     /*�ֹι�ȣ   */
			            sendMap.setMap("DATATOT" , bsCmd273001List.size());   /*�����ѰǼ� */
			  			sendMap.setMap("POINTNO" , POINTNO);                  /*������ȣ   */
						
			  			/*1���̻� ��ȸ�� �� ��� ��������*/
			  			sendMap.setMap("DATANUM", DATANUM);                  /*�����ͰǼ� */

			  			//for ( int al_cnt = Integer.parseInt(POINTNO);  al_cnt < (Integer.parseInt(POINTNO) +Integer.parseInt(DATANUM));  al_cnt++)   {//2013-10-29 �ݰ�� �̺��� ����� ��ȭ(��ü�������� 1Kbyte����)�� �Ʒ��� ���� by ��â��
		                for ( int al_cnt = START_IDX;  al_cnt < (START_IDX + Integer.parseInt(DATANUM));  al_cnt++)  {
							
							MapForm mfCmd273001List  =  bsCmd273001List.get(al_cnt);
							
							MapForm kfRepeatData = new MapForm();
							
							kfRepeatData.setMap("PUBGCODE"   , headMap.getMap("GPUB_CODE"));
							kfRepeatData.setMap("JIRONO"     , headMap.getMap("GIRO_NO"));
							kfRepeatData.setMap("ETAXNO"     , mfCmd273001List.getMap("TAX_NO"));
							kfRepeatData.setMap("ETAXNO"     , mfCmd273001List.getMap("EPAY_NO"));
							kfRepeatData.setMap("GWAMOK"     , mfCmd273001List.getMap("TAX_ITEM"));
							kfRepeatData.setMap("GWADATE"    , mfCmd273001List.getMap("TAX_YM"));
							kfRepeatData.setMap("KIBUN"      , mfCmd273001List.getMap("TAX_DIV"));
							kfRepeatData.setMap("NAPAMT"     , mfCmd273001List.getMap("SUTT"));
							kfRepeatData.setMap("NAPBU_DATE" , mfCmd273001List.getMap("PAY_DT"));
							kfRepeatData.setMap("NAPBU_SYS"  , mfCmd273001List.getMap("NAPBU_SYS"));
							kfRepeatData.setMap("JIJUM_CODE" , mfCmd273001List.getMap("BANK_CD"));
							
							alRepeatData.add(kfRepeatData);

						} 
					}
					
				}else{ 
					/* ��ȸ�Ǽ��� ���� ��� ��������
					 * */
					
					resCode = "112";  /*��ȸ��������*/
				
				} 
				
			}
			
			log.info(cUtil.msgPrint("8", "","BP273001 chk_kf_273001()", resCode));
			
        } catch (Exception e) {
			
			e.printStackTrace();
			log.error("============================================");
			log.error("== chk_kf_273001 Exception(�ý���) ");
			log.error("============================================");
		}
        
        if(!resCode.equals("000")) {
        	sendMap = mf;
        	sendMap.setMap("DATATOT"  , "000");  /*�����ѰǼ� */
			sendMap.setMap("DATANUM"  , "00");   /*�����ͰǼ� */
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
