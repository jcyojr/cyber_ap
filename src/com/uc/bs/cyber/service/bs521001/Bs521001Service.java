/**
 *  �ֽý��۸� : ���̹����漼û ��ȭ
 *  ��  ��  �� : ���漼 �������� ������ȸ ����
 *  ��  ��  �� : �λ�����-���̹���û 
 *               ����ó��
 *               ��������� ���ŵ� ������ ���ؼ� ������������Ʈ�� �����ϰ� 
 *               ��ȸ����� �����Ѵ�.
 *  Ŭ����  ID : Bs521001Service
 *  ����  �̷� :
 * ------------------------------------------------------------------------
 *  �ۼ���     �Ҽ�  		 ����      Tag   ����
 * ------------------------------------------------------------------------   
 * Ȳ����   �ٻ�(��)    2011.06.13   %01%  �ű��ۼ�
 * ��â��   ��ä��(��)  2013.11.04     %02%  ����e���ΰ���(���ܼ���, ȯ�氳���δ��) �߰�
 */
package com.uc.bs.cyber.service.bs521001;

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
public class Bs521001Service {

	protected Log log = LogFactory.getLog(this.getClass());
	
	private ApplicationContext appContext = null;
	
	private IbatisBaseService sqlService_cyber = null;
		
	private Bs521001FieldList kfField = null;
	
	CbUtil cUtil = null;
	
	/**
	 * ������
	 */
	public Bs521001Service(ApplicationContext appContext) {
		// TODO Auto-generated constructor stub
		
		setAppContext(appContext);
		
		kfField = new Bs521001FieldList();
		
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
	 * ���漼 �������� ������ȸ ó��...
	 * */
	public byte[] chk_bs_521001(MapForm headMap, MapForm mf) throws Exception {
		
				
		MapForm sendMap = new MapForm();
		
		/*���漼 ����������ȸ �� ��� */
		String resCode = "000";       /*�����ڵ�*/
//		String giro_no = "1000685";   /*�λ�������ڵ� : ��û*/
		String strTaxGubun = "";
		String sg_code = "26";        /*�λ�ñ���ڵ�*/
		
		String POINTNO = "";          /*������ȣ*/
		String DATANUM = "";          /*�����Ͱ���*/
		int START_IDX = 0;
		
		/*�ݺ���*/
		ArrayList<MapForm> alRepeatData = new ArrayList<MapForm>();
		
		try{
			cUtil = CbUtil.getInstance();
			
			log.info(cUtil.msgPrint("6", "S","BP521001 chk_bs_521001()", resCode));
			
			/*����üũ*/
			/*���*/
			if(!headMap.getMap("GPUB_CODE").equals(sg_code)){
				resCode = "122";
			}
			/*�����������ڵ�*/
			strTaxGubun = (String) sqlService_cyber.queryForBean("CODMBASE.GIRO_FOR_TAX", headMap);
//          if(!headMap.getMap("GJIRO_NO").equals(giro_no)){
			log.debug("--------strTaxGubun = " + strTaxGubun + " -------------Ȯ�� 20140721");
            
			if(!("1".equals(strTaxGubun))){
              resCode = "123";
            }
            
			POINTNO = (String)mf.getMap("POINT_NO"); // ������ȣ
			DATANUM = (String)mf.getMap("DATA_NUM"); // �����ͰǼ�

			if (resCode.equals("000")) {
				
				/*���漼 �󼼳����� ������ ����Ѵ�...*/
				ArrayList<MapForm>  bsCmd521001List  =  sqlService_cyber.queryForList("BS521001.SELECT_TAX_SEARCH_LIST", mf);
				log.debug("-------bsCmd521001List.size() = " +  String.valueOf(bsCmd521001List.size()) + " ---------------Ȯ�� 20140721");				
				
				if ( bsCmd521001List.size() > 0 ) {
				    
					//����e���� �׽�Ʈ�� pointno�� '0'���� �� ��� ���� Ȯ�� ����
                    if (Integer.parseInt(POINTNO)<=0) POINTNO = "001";
						START_IDX = Integer.parseInt(POINTNO) -1;
					if (START_IDX >= bsCmd521001List.size()){
						resCode = "111";
					} else {
						resCode = "000";
						
						/*������ȣ ó������*/
						if (Integer.parseInt(DATANUM) >= 10 ){
							if(Integer.parseInt(DATANUM) > bsCmd521001List.size())
								DATANUM = String.valueOf(bsCmd521001List.size());
							else
								DATANUM = "10";
			                log.debug("-------DATANUM" + Integer.parseInt(DATANUM) + "---------------ENDTERED");
			                	
						}
						else
			                DATANUM = String.valueOf( bsCmd521001List.size() - START_IDX );
			              
						/*0210 �������� ���� */
						int DATA_TOT=(bsCmd521001List.size()>999)?999:bsCmd521001List.size();
						sendMap.setMap("JUMIN_NO", mf.getMap("JUMIN_NO"));    /*�ֹι�ȣ   */
			            sendMap.setMap("DATA_TOT", DATA_TOT);   /*�����ѰǼ� */
			  			sendMap.setMap("POINT_NO", POINTNO);                  /*������ȣ   */
			  			sendMap.setMap("DATA_NUM", DATANUM);                  /*�����ͰǼ� */
			  			
//                      for ( int al_cnt = 0;  al_cnt < bsCmd521001List.size();  al_cnt++)   {
//                      for ( int al_cnt = 0;  al_cnt < Integer.parseInt(DATANUM);  al_cnt++)   {//2013-10-29 �ݰ�� �̺��� ����� ��ȭ(��ü�������� 1Kbyte����)�� �Ʒ��� ���� by ��â��
                        for ( int al_cnt = START_IDX;  al_cnt < (START_IDX + Integer.parseInt(DATANUM));  al_cnt++)  {
							
							MapForm mfCmd521001List  =  bsCmd521001List.get(al_cnt);
							
							MapForm mfRepeatData = new MapForm();
							
							/* ���ⱸ�� */
							String napGubun = cUtil.getNapkiGubun((String)mfCmd521001List.getMap("DUE_DT"));
							
							mfRepeatData.setMap("PUBGCODE"   , headMap.getMap("GPUB_CODE"));
							mfRepeatData.setMap("JIRONO"     , headMap.getMap("GJIRO_NO"));
							mfRepeatData.setMap("TAXNO"    	 , mfCmd521001List.getMap("TAXNO"));
							mfRepeatData.setMap("SEQNO"    	 , Long.parseLong(mfCmd521001List.getMap("SEQNO").toString()));
							mfRepeatData.setMap("GWA_MOK"    , mfCmd521001List.getMap("TAX_ITEM"));
							mfRepeatData.setMap("GWA_YM"   	 , mfCmd521001List.getMap("TAX_YM"));
							mfRepeatData.setMap("KIBUN"      , mfCmd521001List.getMap("TAX_DIV"));
							
							/* ���� �ְ��� */
							if (mfCmd521001List.getMap("TAX_GBN").equals("2")){
								mfRepeatData.setMap("NAP_AMT"    , mfCmd521001List.getMap("SUM_B_AMT"));
								//mfRepeatData.setMap("NAP_AMT"    , mfCmd521001List.getMap("AMT"));
								mfRepeatData.setMap("NAP_GUBUN"  , "B");
								mfRepeatData.setMap("NAP_DATE"   , mfCmd521001List.getMap("DUE_DT"));
							}
							/* ���漼 */
							else {
								if (mfCmd521001List.getMap("DLQ_DIV").equals("1") && !mfCmd521001List.getMap("TAX_DIV").equals("3")){
									/* ���αݾ� */
									mfRepeatData.setMap("NAP_AMT", cUtil.getNapAmt(mfCmd521001List.getMap("TAX_DT").toString(), napGubun
												                				, Long.parseLong(mfCmd521001List.getMap("MNTX").toString())  	//����
																				, Long.parseLong(mfCmd521001List.getMap("CPTX").toString())		//���ð�ȸ
																				, Long.parseLong(mfCmd521001List.getMap("CFTX").toString())		//�����ü���
																				, Long.parseLong(mfCmd521001List.getMap("LETX").toString())		//������
																				, Long.parseLong(mfCmd521001List.getMap("ASTX").toString())));	//��Ư��
									
									/* ���� ���� ���� */
									mfRepeatData.setMap("NAP_GUBUN"  , napGubun);
									
									/* �������� */
									if(napGubun.equals("B")){
										mfRepeatData.setMap("NAP_DATE"   , mfCmd521001List.getMap("DUE_DT"));
									} else if (napGubun.equals("A")){
										mfRepeatData.setMap("NAP_DATE"   , mfCmd521001List.getMap("DUE_F_DT"));
									}
									
								} else {
			                		/* ���αݾ� */
			                		mfRepeatData.setMap("NAP_AMT", cUtil.getNapAmt(Long.parseLong(mfCmd521001List.getMap("MNTX").toString())			//����
													                				, Long.parseLong(mfCmd521001List.getMap("MNTX_ADTX").toString())  	//���� �����
																					, Long.parseLong(mfCmd521001List.getMap("CPTX").toString())			//���ð�ȸ��
																					, Long.parseLong(mfCmd521001List.getMap("CPTX_ADTX").toString())	//���ð�ȸ�� �����
																					, Long.parseLong(mfCmd521001List.getMap("CFTX").toString())			//�����ü���
																					, Long.parseLong(mfCmd521001List.getMap("CFTX_ADTX").toString())	//�����ü��� �����
																					, Long.parseLong(mfCmd521001List.getMap("LETX").toString())			//������
																					, Long.parseLong(mfCmd521001List.getMap("LETX_ADTX").toString())	//������ �����
																					, Long.parseLong(mfCmd521001List.getMap("ASTX").toString())			//��Ư��
																				    , Long.parseLong(mfCmd521001List.getMap("ASTX_ADTX").toString())));	//��Ư�� �����
			                		/* ���⳻�ı��� */
			                		mfRepeatData.setMap("NAP_GUBUN"  , "B");
			                		/* �������� */
			                		mfRepeatData.setMap("NAP_DATE"   , mfCmd521001List.getMap("DUE_DT"));
								}
							
							}
							
							/*�ڵ���ü ��� ���� */
							mfRepeatData.setMap("AUTO_REG"   , "N");			
							
							alRepeatData.add(mfRepeatData);
														
						} 
					}
					
				}else{ 
					/* ��ȸ�Ǽ��� ���� ��� ��������
					 * */
					
					resCode = "111";  /*��ȸ��������*/
					
					sendMap.setMap("DATA_TOT" , "000");  /*�����ѰǼ� */
					sendMap.setMap("DATA_NUM" , "00");   /*�����ͰǼ� */
				} 
				
			}

			log.info(cUtil.msgPrint("6", "","BP521001 chk_bs_521001()", resCode));
			
        } catch (Exception e) {
			
        	resCode = "093";
        	
			e.printStackTrace();
			log.error("============================================");
			log.error("== chk_bs_521001 Exception(�ý���) ");
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

