/**
 *  �ֽý��۸� : ���̹����漼û ��ȭ
 *  ��  ��  �� : ���� (��)��� ����
 *  ��  ��  �� : �λ�����-���̹���û 
 *               ����ó��
 *               ��������� ���ŵ� ������ ���ؼ� ������������Ʈ�� �����ϰ� 
 *               ��ȸ����� �۽��Ѵ�.
 *  Ŭ����  ID : Bs992001Service
 *  ����  �̷� :
 * ------------------------------------------------------------------------
 *  �ۼ���     �Ҽ�  		 ����      Tag   ����
 * ------------------------------------------------------------------------   
 * �۵���   �ٻ�(��)    2011.06.13   %01%  �ű��ۼ�
 * ��â��      ��ä��(��)         2013.07.02         %02%         �ּ��߰� �������  ����
 *  
 */
package com.uc.bs.cyber.service.bs992001;

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
public class Bs992001Service {

	protected Log log = LogFactory.getLog(this.getClass());
	
	private ApplicationContext appContext = null;
	
	private IbatisBaseService sqlService_cyber = null;
		
	private Bs992001FieldList kfField = null;
	
	CbUtil cUtil = null;
		  
	/**
	 * ������
	 */
	public Bs992001Service(ApplicationContext appContext) {
		// TODO Auto-generated constructor stub
		
		setAppContext(appContext);
		
		kfField = new Bs992001FieldList();
		
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
	 * ���� �����
	 * */
	public byte[] chk_bs_992001(MapForm headMap, MapForm mf) throws Exception {
		
		MapForm sendMap = new MapForm();
		
		/*���漼 ����������ȸ �� ��� */
		String resCode = "000";       /*�����ڵ�*/
		String sg_code = "26" ;       /*�λ�ñ���ڵ�*/
		String strTaxGubun = "";
		
		ArrayList<MapForm> bsCmd992001List = new ArrayList<MapForm>();
		
		try{
			cUtil = CbUtil.getInstance();
			
			log.info(cUtil.msgPrint("11", "S","BP992001 chk_bs_992001() : �������", resCode));
			
			/*���*/
			if(!headMap.getMap("GPUB_CODE").equals(sg_code)){
				resCode = "122"; 
			}
			
			strTaxGubun = (String) sqlService_cyber.queryForBean("CODMBASE.GIRO_FOR_TAX", headMap);
			
			if(resCode.equals("000")){
				
				/*������ �����ڵ忡 ���ؼ� ������ �����Ѵ�...*/
				if("3".equals(strTaxGubun)) { /*ȯ�氳��*/
					
    				log.info("����ȯ�氳�� ������� ���� ...");
    				
    				bsCmd992001List = sqlService_cyber.queryForList("BS992001.SELECT_RECIP_H_LIST", mf);
					
				} else if("1".equals(strTaxGubun)) { /*���漼*/
					
					log.info("�������漼 ������� ���� ...");
					
					bsCmd992001List = sqlService_cyber.queryForList("BS992001.SELECT_RECIP_J_LIST", mf);
					
				} else if ("2".equals(strTaxGubun)) { //���ռ��ܼ���-����e����
                 
				    log.info("���ռ��ܼ��� ������� ���� ...");
                    
                    bsCmd992001List = sqlService_cyber.queryForList("BS992001.SELECT_RECIP_O_LIST", mf);
                 
//				} else if(headMap.getMap("GJIRO_NO").equals("1500172")) { /*��Ÿ���Ա�*/
				} else if("4".equals(strTaxGubun)) { /*��Ÿ���Ա�*/
					
//					log.info("���ܼ��� ������� ���� ...");
					log.info("��Ÿ���Ա� ������� ���� ...");
					
					bsCmd992001List = sqlService_cyber.queryForList("BS992001.SELECT_RECIP_O_LIST", mf);
					
//				} else if(headMap.getMap("GJIRO_NO").equals("1004102")) { /*���ϼ���*/
				} else if("5".equals(strTaxGubun)) { /*���ϼ���*/
					
					log.info("���ϼ��� ������� ���� ...");
					
					bsCmd992001List = sqlService_cyber.queryForList("BS992001.SELECT_RECIP_S_LIST", mf);
					
				} else {
					
					/*�����������ڵ�*/
					resCode = "123";
				}
								
				int payTotCnt = bsCmd992001List.size();
				
				if (payTotCnt <= 0) {
					
					/*��ȸ��������*/
					resCode = "112";
					
				} else if (payTotCnt > 1) {
					
					/* �������_����������ȣ�� ��ȸ�ϹǷ�
					 * �̰ǿ� ���ؼ� ���� 2�� ��ȸ�� �ɼ� ����.
					 * */
					resCode = "094";
					
				} else {
					
					/*��ȸ 1��
					 *���ڼ������̺� ���� ��� �� �ΰ��������̺� ��� �Ѵ�. 
					 * */
					MapForm mfCmd992001List  =  bsCmd992001List.get(0);

					/*������ ���*/
					if(resCode.equals("000")){
						
						if(!(headMap.getMap("RS_FLAG").equals("B")|| headMap.getMap("RS_FLAG").equals("C"))){
							resCode = "312"; //�ش������� �ƴ�.(���ý�, ������)
						} else {
							
							if(mfCmd992001List.getMap("SNTG").equals("1") ){ //�����������̹Ƿ� ���� ��Ұ� ������...
								
								if(Long.parseLong(mfCmd992001List.getMap("SUM_RCP").toString()) != Long.parseLong(mf.getMap("B_NAPBU_AMT").toString())) {
									resCode = "417"; //���ŷ� ���δ�ݳ��αݾ� Ʋ��
									
								} else {
									resCode = "000";
								}
								
							} else if(mfCmd992001List.getMap("SNTG").equals("9") ){
								resCode = "132"; // ���γ��� ������Ͽ���
								
							} else {
								resCode = "411"; // ��Ҵ�� ���� �ƴ� ( �̹� �����ǰ� ��û�� �뺸 �� ���̹Ƿ� ��ҺҰ�)
							}
							
							/*�������� ��Ұ��� ���*/
							if (resCode.equals("000")) {
								
								/* ���� �������̺��� ��ȸ�Ͽ� �ٸ���ü�� ���� �������� �ִ� �� �ݵ�� Ȯ���Ѵ�.
								 * ���� �������� �ִ� ���� �ΰ����̺� ���ó���� ���� �ʴ´�.
								 * */
								
								if (mfCmd992001List.getMap("TAX_GB").equals("J")){        //���漼
									
									ArrayList<MapForm> bsPayList = sqlService_cyber.queryForList("BS992001.SELECT_PAY_J_LIST", mfCmd992001List);
									
									if(bsPayList.size() > 0 ) {
										
										if(bsPayList.size() == 1 ) {
											//�ܰ�
											if (sqlService_cyber.queryForUpdate("BS992001.UPDATE_TX1102_TB_LEVY_DETAIL", mfCmd992001List) > 0 ) {
												
												if(sqlService_cyber.queryForUpdate("BS992001.DELETE_TX1201_TB_EPAY", mfCmd992001List) == 0 ) {
													resCode = "093"; //����
												}
											} else {
												resCode = "093"; //����
											}
											
										} else {
											//�������� ��� ���ڼ������̺� ����Ѵ�.
											//��Ұ��� �������_����������ȣ�� ��ȸ�� ������ �̹Ƿ� �ش� �����ǿ� ���� �����͸� ���ó���Ѵ�.
											if(sqlService_cyber.queryForUpdate("BS992001.DELETE_TX1201_TB_EPAY", mfCmd992001List) == 0 ) {
												resCode = "093"; //����
											}
										}
										
									} else {
										resCode = "093"; //����
									}
									
									
								} else if (mfCmd992001List.getMap("TAX_GB").equals("O")){ //���ܼ����Ϲ�

									ArrayList<MapForm> bsPayList = sqlService_cyber.queryForList("BS992001.SELECT_PAY_O_LIST", mfCmd992001List);

									if(bsPayList.size() > 0 ) {

										if(bsPayList.size() == 1 ) {

											if (sqlService_cyber.queryForUpdate("BS992001.UPDATE_TX2221_TB_LEVY_DETAIL", mfCmd992001List) > 0 ) {

												if(sqlService_cyber.queryForUpdate("BS992001.DELETE_TX2122_TB_EPAY", mfCmd992001List) == 0 ) {

													resCode = "093"; //����
												}
											} else {

												resCode = "093"; //����
											}
											
										} else {

											//�������� ��� ���ڼ������̺� ����Ѵ�.
											//��Ұ��� �������_����������ȣ�� ��ȸ�� ������ �̹Ƿ� �ش� �����ǿ� ���� �����͸� ���ó���Ѵ�.
											if (sqlService_cyber.queryForUpdate("BS992001.UPDATE_TX2221_TB_LEVY_DETAIL", mfCmd992001List) == 0 ) {

												resCode = "093"; //����
											}
										}
										
									} else {

										resCode = "093"; //����
									}

								} else if (mfCmd992001List.getMap("TAX_GB").equals("S")){ //���ϼ���

									ArrayList<MapForm> bsPayList = sqlService_cyber.queryForList("BS992001.SELECT_PAY_S_LIST", mfCmd992001List);

									if(bsPayList.size() > 0 ) {

										if(bsPayList.size() == 1 ) {

											if (sqlService_cyber.queryForUpdate("BS992001.UPDATE_TX3111_TB_LEVY_DETAIL", mfCmd992001List) > 0 ) {

												if(sqlService_cyber.queryForUpdate("BS992001.DELETE_TX3211_TB_EPAY", mfCmd992001List) == 0 ) {

													resCode = "093"; //����
												}
											} else {

												resCode = "093"; //����
											}
											
										} else {

											//�������� ��� ���ڼ������̺� ����Ѵ�.
											//��Ұ��� �������_����������ȣ�� ��ȸ�� ������ �̹Ƿ� �ش� �����ǿ� ���� �����͸� ���ó���Ѵ�.
											if(sqlService_cyber.queryForUpdate("BS992001.DELETE_TX3211_TB_EPAY", mfCmd992001List) == 0 ) {

												resCode = "093"; //����
											}
										}

									} else {

										resCode = "093"; //����
									}

									
								} else if (mfCmd992001List.getMap("TAX_GB").equals("H")){ //ȯ�氳��

									ArrayList<MapForm> bsPayList = sqlService_cyber.queryForList("BS992001.SELECT_PAY_H_LIST", mfCmd992001List);

									if(bsPayList.size() > 0 ) {

										if(bsPayList.size() == 1 ) {

											if (sqlService_cyber.queryForUpdate("BS992001.UPDATE_TX2132_TB_LEVY_DETAIL", mfCmd992001List) > 0 ) {

												if(sqlService_cyber.queryForUpdate("BS992001.DELETE_TX2231_TB_EPAY", mfCmd992001List) == 0 ) {

													resCode = "093"; //����
												}
											} else {

												resCode = "093"; //����
											}
											
										} else {

											//�������� ��� ���ڼ������̺� ����Ѵ�.
											//��Ұ��� �������_����������ȣ�� ��ȸ�� ������ �̹Ƿ� �ش� �����ǿ� ���� �����͸� ���ó���Ѵ�.
											if(sqlService_cyber.queryForUpdate("BS992001.DELETE_TX2231_TB_EPAY", mfCmd992001List) == 0 ) {

												resCode = "093"; //����
											}
										}
										
									} else {

										resCode = "093"; //����
									}
									
									

								} else if (mfCmd992001List.getMap("TAX_GB").equals("T")){ //ǥ�ؼ��ܼ���

									ArrayList<MapForm> bsPayList = sqlService_cyber.queryForList("BS992001.SELECT_PAY_T_LIST", mfCmd992001List);

									if(bsPayList.size() > 0 ) {

										if(bsPayList.size() == 1 ) {

											if (sqlService_cyber.queryForUpdate("BS992001.UPDATE_TX2112_TB_LEVY_DETAIL", mfCmd992001List) > 0 ) {

												if(sqlService_cyber.queryForUpdate("BS992001.DELETE_TX2211_TB_EPAY", mfCmd992001List) == 0 ) {

													resCode = "093"; //����
												}
											} else {

												resCode = "093"; //����
											}
											
										} else {

											//�������� ��� ���ڼ������̺� ����Ѵ�.
											//��Ұ��� �������_����������ȣ�� ��ȸ�� ������ �̹Ƿ� �ش� �����ǿ� ���� �����͸� ���ó���Ѵ�.
											if(sqlService_cyber.queryForUpdate("BS992001.DELETE_TX2211_TB_EPAY", mfCmd992001List) == 0 ) {

												resCode = "093"; //����
											}
											
										}
										
									} else {

										resCode = "093"; //����
									}
									
								}
								
							}
							
						}

					}
			
				}
				
			}
			
			log.info(cUtil.msgPrint("11", "","BP992001 chk_bs_992001()", resCode));
			
        } catch (Exception e) {
			
        	resCode = "093";
        	
			e.printStackTrace();
			log.error("============================================");
			log.error("== chk_bs_992001 Exception(�ý���) ");
			log.error("============================================");
		}
        
        if(!resCode.equals("000")) {

        	sendMap = mf;
        } else {
        	if (headMap.getMap("TX_GUBUN").equals("420")) {
        		if(headMap.getMap("PROGRAM_ID").equals("992001")){ /*���� ��(���)*/
					
					if(headMap.getMap("RS_FLAG").equals("C") || headMap.getMap("RS_FLAG").equals("B")){ /* ������ �� ���� ���� ���� ����*/

        				sendMap = mf; /*��ҿ�û ������ ó�������� �Դ� ���� �״�� ���� */
					}
        		}
        	}
        }
        
        /*���⼭ ������带 �����Ѵ�. �ٲ�� �κи� �ٽ� �����ؼ� ������ ����*/
        headMap.setMap("RS_FLAG"   , "G");         /*�����̿���(G) */
        headMap.setMap("RESP_CODE" , resCode);     /*�����ڵ�*/
        headMap.setMap("GCJ_NO"    , "0" + sg_code + "0" + CbUtil.lPadString(String.valueOf(SeqNumber()), 8, '0'));     /*�̿��� �Ϸù�ȣ*/
        headMap.setMap("TX_GUBUN"  , "0430");

        return kfField.makeSendBuffer(headMap, sendMap);
	}

	/*----------------------------------------------*/
	/* �Ϸù�ȣ �������� �׽�Ʈ*/
	/*----------------------------------------------*/
	private int SeqNumber(){
    	return (Integer)sqlService_cyber.queryForBean("CODMBASE.CODM_GETSEQNO", "00");
    }
}

