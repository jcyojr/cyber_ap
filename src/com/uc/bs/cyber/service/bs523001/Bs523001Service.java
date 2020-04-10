/**
 *  �ֽý��۸� : ���̹����漼û ��ȭ
 *  ��  ��  �� : ���漼 ���γ��� ������ȸ ����
 *  ��  ��  �� : �λ�����-���̹���û 
 *               ����ó��
 *               ��������� ���ŵ� ������ ���ؼ� ������������Ʈ�� �����ϰ� 
 *               ��ȸ����� �����Ѵ�.
 *  Ŭ����  ID : Bs523001Service
 *  ����  �̷� :
 * ------------------------------------------------------------------------
 *  �ۼ���     �Ҽ�  		 ����      Tag   ����
 * ------------------------------------------------------------------------   
 * Ȳ����   �ٻ�(��)    2011.06.13   %01%  �ű��ۼ�
 * ��â��   ��ä��(��)  2013.11.04     %02%  ����e���ΰ���(���ܼ���, ȯ�氳���δ��) �߰�
 */
package com.uc.bs.cyber.service.bs523001;

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
public class Bs523001Service {

	protected Log log = LogFactory.getLog(this.getClass());
	
	private ApplicationContext appContext = null;
	
	private IbatisBaseService sqlService_cyber = null;
		
	private Bs523001FieldList kfField = null;
	
	CbUtil cUtil = null;
	
	/**
	 * ������
	 */
	public Bs523001Service(ApplicationContext appContext) {
		// TODO Auto-generated constructor stub
		
		setAppContext(appContext);
		
		kfField = new Bs523001FieldList();
		
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
	 * ���漼 ���γ��� ������ȸ ó��...
	 * */
	public byte[] chk_bs_523001(MapForm headMap, MapForm mf) throws Exception {
		
		MapForm sendMap = new MapForm();
		
		/*���漼 ����������ȸ �� ��� */
		String resCode = "000";       /*�����ڵ�*/
//		String giro_no = "1000685";   /*�λ�������ڵ�*/
		String strTaxGubun = "";
		String sg_code = "26";        /*�λ�ñ���ڵ�*/
		
		String POINTNO = "";          /*������ȣ*/
		String DATANUM = "";          /*�����Ͱ���*/
		int START_IDX  = 0;
		
		/*�ݺ���*/
		ArrayList<MapForm> alRepeatData = new ArrayList<MapForm>();
		
		try{
			cUtil = CbUtil.getInstance();
			
			log.info(cUtil.msgPrint("8", "S","BP523001 chk_bs_523001()", resCode));
			
			/*����üũ*/
			
			/*���*/
			if(!headMap.getMap("GPUB_CODE").equals(sg_code)){
				resCode = "122";
			}
			/*�����������ڵ�*/
			strTaxGubun = (String) sqlService_cyber.queryForBean("CODMBASE.GIRO_FOR_TAX", headMap);
//          if(!headMap.getMap("GJIRO_NO").equals(giro_no)){
            if(!("1".equals(strTaxGubun))){
              resCode = "123";
            }
			
			/*�ֹι�ȣ*/
			//if(mf.getMap("JUMINNO").toString().length() != 13){
			//	resCode = "340";
			//}
			//���ι�ȣ ����� ��ȣ �ֹι�ȣ �̷��� ���� ���� üũ���� ����...
			
			POINTNO = (String)mf.getMap("POINTNO"); // ������ȣ
			DATANUM = (String)mf.getMap("DATANUM"); // �����ͰǼ�

			if(resCode.equals("000")) {
				
				ArrayList<MapForm>  bsCmd523001List  =  sqlService_cyber.queryForList("BS523001.SELECT_RECIP_LIST", mf);
				
				if ( bsCmd523001List.size() > 0 ) {
					
				  //����e���� �׽�Ʈ�� pointno�� '0'���� �� ��� ���� Ȯ�� ����
                    if (Integer.parseInt(POINTNO)<=0) POINTNO = "001";
					
					START_IDX = Integer.parseInt(POINTNO) -1;
					
					if (START_IDX >= bsCmd523001List.size()){
						resCode = "112";
					} else {
						resCode = "000";
						
						/*������ȣ ó������*/
						if (Integer.parseInt(DATANUM) > 10 )
			                DATANUM = "10";
						else if ( bsCmd523001List.size() < (START_IDX+Integer.parseInt(DATANUM)) )
			                DATANUM = String.valueOf( bsCmd523001List.size() - START_IDX );
			              
						/*0210 �������� ���� */
						sendMap.setMap("JUMINNO", mf.getMap("JUMINNO"));     /*�ֹι�ȣ   */
			            sendMap.setMap("DATATOT", bsCmd523001List.size());   /*�����ѰǼ� */
			  			sendMap.setMap("POINTNO", POINTNO);                  /*������ȣ   */
						
			  			/*1���̻� ��ȸ�� �� ��� ��������*/
			  			sendMap.setMap("DATANUM", DATANUM);                  /*�����ͰǼ� */

//                      for ( int al_cnt = 0;  al_cnt < bsCmd523001List.size();  al_cnt++)   {
//                      for ( int al_cnt = 0;  al_cnt < Integer.parseInt(DATANUM);  al_cnt++)   {//2013-10-29 �ݰ�� �̺��� ����� ��ȭ(��ü�������� 1Kbyte����)�� �Ʒ��� ���� by ��â��
                        for ( int al_cnt = START_IDX;  al_cnt < (START_IDX + Integer.parseInt(DATANUM));  al_cnt++) {
							
							MapForm mfCmd523001List  =  bsCmd523001List.get(al_cnt);
							
							MapForm mfRepeatData = new MapForm();
							
							mfRepeatData.setMap("PUBGCODE"   , headMap.getMap("GPUB_CODE"));
							mfRepeatData.setMap("JIRONO"     , headMap.getMap("GJIRO_NO"));
							mfRepeatData.setMap("ETAXNO"     , mfCmd523001List.getMap("ETAXNO"));
							mfRepeatData.setMap("GWAMOK"     , mfCmd523001List.getMap("TAX_ITEM"));
							mfRepeatData.setMap("GWADATE"    , (String)mfCmd523001List.getMap("TAX_YY") + (String)mfCmd523001List.getMap("TAX_MM"));
							mfRepeatData.setMap("KIBUN"      , mfCmd523001List.getMap("TAX_DIV"));
							mfRepeatData.setMap("NAPAMT"     , mfCmd523001List.getMap("SUTT"));
							mfRepeatData.setMap("NAPBU_DATE" , mfCmd523001List.getMap("PAY_DT"));
							mfRepeatData.setMap("NAPBU_SYS"  , mfCmd523001List.getMap("���νý���"));
							mfRepeatData.setMap("JIJUM_CODE" , mfCmd523001List.getMap("BANK_CODE"));
							
							alRepeatData.add(mfRepeatData);

						} 
					}
					
				}else{ 
					/* ��ȸ�Ǽ��� ���� ��� ��������
					 * */
					
					resCode = "112";  /*��ȸ��������*/
					
				} 
				
			}
			
			log.info(cUtil.msgPrint("8", "","BP523001 chk_bs_523001()", resCode));
			
        } catch (Exception e) {
			
        	resCode = "093";
        	
			e.printStackTrace();
			log.error("============================================");
			log.error("== chk_bs_523001 Exception(�ý���) ");
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
