/**
 *  �ֽý��۸� : ���̹����漼û ��ȭ
 *  ��  ��  �� : ���ܼ��� �������� ����ȸ ����
 *  ��  ��  �� : ������-���̹���û 
 *               ����ó��
 *               ��������� ���ŵ� ������ ���ؼ� ������������Ʈ�� �����ϰ� 
 *               ��ȸ����� �����Ѵ�.
 *  Ŭ����  ID : Kf271002Service
 *  ����  �̷� :
 * ------------------------------------------------------------------------
 *  �ۼ���     �Ҽ�  		 ����      Tag   ����
 * ------------------------------------------------------------------------   
 * �ҵ���   ��ä��(��)    2011.06.13   %01%  �ű��ۼ�
 *  
 */
package com.uc.bs.cyber.service.kf271002;

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
public class Kf271002Service {

	protected Log log = LogFactory.getLog(this.getClass());
	
	private ApplicationContext appContext = null;
	
	private IbatisBaseService sqlService_cyber = null;
		
	private Kf271002FieldList kfField = null;
	
	CbUtil cUtil = null;
	
	/**
	 * ������
	 */
	public Kf271002Service(ApplicationContext appContext) {
		// TODO Auto-generated constructor stub
		
		setAppContext(appContext);
		
		kfField = new Kf271002FieldList();
		
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
	 * ���ܼ��� �������� ����ȸ ó��...
	 * ����ȸ ȭ�� �� ������ȭ�鿡�� 331�̶�� �ⳳ�� ��� ������ 
	 * �߻��ϸ� �������� �����Ͽ� �ڷḦ �����ϵ��� �Ѵ�.
	 * */
	public byte[] chk_kf_271002(MapForm headMap, MapForm mf) throws Exception {
		
		MapForm sendMap = new MapForm();
		
		/*���ܼ��� ����������ȸ �� ��� */
		String resCode = "000";       /*�����ڵ�*/
		String giro_no = "1500172";   /*�λ�������ڵ�*/
		String sg_code = "26";        /*�λ�ñ���ڵ�*/
		
		try{

			cUtil = CbUtil.getInstance();
			
			log.info(cUtil.msgPrint("2", "S","KP271002 chk_kf_271002()", resCode));
						
			/*����üũ*/
			
			/*���*/
			if(!headMap.getMap("GPUB_CODE").equals(sg_code)){
				resCode = "339";
			}
			/*�����������ڵ�*/
			if(!headMap.getMap("GJIRO_NO").equals(giro_no)){
				resCode = "324";
			}
			
			log.debug("S_GBN[��ȸ����] = [" + mf.getMap("S_GBN") + "]");
			/* G : ������ȣ E:���ڳ��ι�ȣ
			 * ���缭��ô� G, ��Ÿ������ E�� ó����...
			 * ���� G ������ ���ŵǸ� ó���� �ؾ��ұ� ����...��ε�...
			 * ������ ó������...
			 * */
			
			/**
			 * �������� ������� ���ܼ����� ��� ���ڳ��ι�ȣ�� 17 ��...
			 * �׿ܴ� 17 + "00" �پ ������ ���ŵ�...
			 * ���� �̷� ������ �����ϱ� ����
			 */
			if(mf.getMap("GRNO").toString().length() == 17) {
				mf.setMap("GRNO", mf.getMap("GRNO").toString() + "00");
			}
			log.debug("�α�Ȯ�� 1111");
			if(mf.getMap("GRNO").toString().length() == 19 && resCode.equals("000")){
				log.debug("�α�Ȯ�� 2222");
				
				//�α��۾��� : ��ȸ
				mf.setMap("TX_GB"  , "1");  
				mf.setMap("PAY_DT" , CbUtil.getCurrentDate());
				
				log.debug("�α�Ȯ�� 3333");
				ArrayList<MapForm>  kfCmd271002List  =  sqlService_cyber.queryForList("KF271002.SELECT_BNON_SEARCH_LIST", mf);
				log.debug("�α�Ȯ�� 4444");
				
				log.debug("GRNO = [" + mf.getMap("GRNO") + "]");
				log.debug("JUMIN_NO = [" + mf.getMap("JUMIN_NO") + "]");
				log.debug("TAX_NO = [" + mf.getMap("TAX_NO") + "]");
				log.debug("kfCmd271002List.size() = [" + kfCmd271002List.size() + "]");
				log.debug("resCode = [" + resCode + "]");
				
				
				
				int levy_cnt = kfCmd271002List.size();
				
				if (levy_cnt <= 0) {
					resCode = "311"; /*��ȸ��������*/
				} else if (levy_cnt > 1) {
					resCode = "094"; /*������ 2�� �̻�*/
				} else {
					
					for ( int al_cnt = 0;  al_cnt < levy_cnt;  al_cnt++)   {
						
						MapForm mfCmd271002List  =  kfCmd271002List.get(al_cnt);
												
						long gukamt = 0, gukamt_add = 0, sido_amt = 0, sido_amt_add = 0, sigungu_amt = 0, sigungu_amt_add = 0;
						
						/* ���� �ü� �ñ����� ���п� ���� �ݾ����� */
						if(mfCmd271002List.getMap("GBN").equals("1")){
							gukamt = ((BigDecimal)mfCmd271002List.getMap("AMT")).longValue();
							gukamt_add = ((BigDecimal)mfCmd271002List.getMap("AFT_AMT")).longValue();
						} else if (mfCmd271002List.getMap("GBN").equals("2")){
							sido_amt = ((BigDecimal)mfCmd271002List.getMap("AMT")).longValue();
							sido_amt_add = ((BigDecimal)mfCmd271002List.getMap("AFT_AMT")).longValue();					
						} else if (mfCmd271002List.getMap("GBN").equals("3")){
							sigungu_amt = ((BigDecimal)mfCmd271002List.getMap("AMT")).longValue();
							sigungu_amt_add = ((BigDecimal)mfCmd271002List.getMap("AFT_AMT")).longValue();			
						} else if (mfCmd271002List.getMap("GBN").equals("4")){
							/*�����ܼ��� �߰�*/
							/*20110714*/
                            /*�� Ư��ȸ��� �õ����� ������ ����...*/
						}
					
						/* �����ڸ��� 40����Ʈ�� ������ ��� ���ڰ� �ѱ��� ��� 20�� ���Ϸ� �ڸ��� */
						if(mfCmd271002List.getMap("REG_NM").toString().length() > 0)
							mfCmd271002List.setMap("REG_NM", CbUtil.ksubstr(mfCmd271002List.getMap("REG_NM").toString(), 20));
						
						/*��ȸ ���� ����*/
						sendMap.setMap("S_GBN"                  ,  mf.getMap("S_GBN"));
						sendMap.setMap("TAX_NO"                 ,  mfCmd271002List.getMap("TAX_NO"));
						sendMap.setMap("GRNO"                   ,  mf.getMap("GRNO"));
						sendMap.setMap("JUMIN_NO"               ,  mfCmd271002List.getMap("REG_NO"));
						sendMap.setMap("FIELD1"                 ,  "");
						sendMap.setMap("BUGWA_GB"               ,  mfCmd271002List.getMap("BUGWA_STAT"));
						sendMap.setMap("SEMOK_CD"               ,  mfCmd271002List.getMap("TAX_ITEM"));
						sendMap.setMap("SEMOK_NM"               ,  CbUtil.ksubstr(mfCmd271002List.getMap("TAX_NM").toString(), 50)); /*50�ڸ� ���� �ʰ�*/
						sendMap.setMap("GBN"                    ,  mfCmd271002List.getMap("GBN"));
						sendMap.setMap("OCR_BD"                 ,  mfCmd271002List.getMap("OCR_BD"));
						sendMap.setMap("NAP_NAME"               ,  mfCmd271002List.getMap("REG_NM"));
						sendMap.setMap("NAP_BFAMT"              ,  mfCmd271002List.getMap("AMT"));
						sendMap.setMap("NAP_AFAMT"              ,  mfCmd271002List.getMap("AFT_AMT"));
						sendMap.setMap("GUKAMT"                 ,  gukamt);
						sendMap.setMap("GUKAMT_ADD"             ,  gukamt_add);
						sendMap.setMap("SIDO_AMT"               ,  sido_amt);
						sendMap.setMap("SIDO_AMT_ADD"           ,  sido_amt_add);
						sendMap.setMap("SIGUNGU_AMT"            ,  sigungu_amt);
						sendMap.setMap("SIGUNGU_AMT_ADD"        ,  sigungu_amt_add);
						sendMap.setMap("BUNAP_AMT"              ,  "");
						sendMap.setMap("BUNAP_AMT_ADD"          ,  "");
						sendMap.setMap("FIELD2"                 ,  "");
						sendMap.setMap("NAP_BFDATE"             ,  mfCmd271002List.getMap("DUE_DT"));
						sendMap.setMap("NAP_AFDATE"             ,  mfCmd271002List.getMap("DUE_F_DT"));
						sendMap.setMap("GWASE_ITEM"             ,  mfCmd271002List.getMap("TAX_GDS"));
						sendMap.setMap("BUGWA_TAB"              ,  " ");
						sendMap.setMap("GOJI_DATE"              ,  mfCmd271002List.getMap("GOJI_DATE"));
						sendMap.setMap("OUTO_ICHE_GB"           ,  "N");
						sendMap.setMap("SUNAB_BANK_CD"          ,  "");
						sendMap.setMap("RECIP_DATE"             ,  "0");
						sendMap.setMap("NAPGI_BA_GB"            ,  cUtil.getNapGubun((String)mfCmd271002List.getMap("DUE_DT"), (String)mfCmd271002List.getMap("DUE_F_DT")));
						sendMap.setMap("FILED3"                 ,  "");

						
						//logging...
						try{
							//�α��۾��� : ����
							mf.setMap("TAX_NO"      ,  mfCmd271002List.getMap("TAX_NO"));
							mf.setMap("JUMIN_NO"    ,  mfCmd271002List.getMap("REG_NO"));
							mf.setMap("PAY_DT"      ,  CbUtil.getCurrentDate());
							mf.setMap("NAPBU_AMT"   ,  mfCmd271002List.getMap("AMT"));
							mf.setMap("TX_GB"       ,  mfCmd271002List.getMap("TAX_GB"));
							
							sqlService_cyber.queryForUpdate("KF271002.INSERT_TX2421_TB_LOG", mf);
							
						}catch (Exception e) {
							
							if (e instanceof DuplicateKeyException){
								log.info("��ȸ TX2421_TB ���̺� �̹� ��ϵ� ������");
							} else {
								log.error("���� ������ = " + mf.getMaps());
							    log.error("Logging  failed!!!");	
							}
						}
					} 
				}
			
				
			
			} else {
				resCode = "341";  /*���ڳ��ι�ȣ, ���밡��ȣ ����*/
			}
			
			log.info(cUtil.msgPrint("2", "","KP271002 chk_kf_271002()", resCode));
			
        } catch (Exception e) {
			
        	resCode = "093";  //Error : ���ڳ��ι�ȣ, ���밡��ȣ ����
        	
			e.printStackTrace();
			log.error("============================================");
			log.error("== chk_kf_271002 Exception(�ý���) ");
			log.error("============================================");
		}
        
        /*�������� ó���� �ȵǾ��ٸ� �� ���� �״�� �����ش�.*/
        if (!resCode.equals("000")) {
        	sendMap = mf;
        }
        
        /*���⼭ ������带 �����Ѵ�. �ٲ�� �κи� �ٽ� �����ؼ� ������ ����*/
        headMap.setMap("RS_FLAG"   , "G");         /*�����̿���(G) */
        headMap.setMap("RESP_CODE" , resCode);     /*�����ڵ�*/
        headMap.setMap("GCJ_NO"    , "0" + sg_code + "0" + CbUtil.lPadString(String.valueOf(SeqNumber()), 8, '0'));     /*�̿��� �Ϸù�ȣ*/
        headMap.setMap("TX_GUBUN"  , "0210");
        
        return kfField.makeSendBuffer(headMap, sendMap);
	}

	/*----------------------------------------------*/
	/* �Ϸù�ȣ �������� �׽�Ʈ*/
	/*----------------------------------------------*/
	private int SeqNumber(){
    	return (Integer)sqlService_cyber.queryForBean("CODMBASE.CODM_GETSEQNO", "00");
    }
}
