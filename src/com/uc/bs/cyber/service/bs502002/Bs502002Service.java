/**
 *  �ֽý��۸� : ���̹����漼û ��ȭ
 *  ��  ��  �� : ���漼 ������������(����û �ǽð� �Ա��뺸) - �κ�ī (UCT) �Ͽ�ư �˼� ���� ������ ������(���ÿ�?)...
 *  ��  ��  �� : �����-���̹���û 
 *               ����ó��
 *               ��������� ���ŵ� ������ ���ؼ� ������������Ʈ�� �����ϰ� 
 *               ��ȸ����� �����Ѵ�.
 *  Ŭ����  ID : Bs502002Service
 *  ����  �̷� :
 * ------------------------------------------------------------------------
 *  �ۼ���     �Ҽ�  		 ����      Tag   ����
 * ------------------------------------------------------------------------   
 * Ȳ����   ��ä��(��)    2011.06.13   %01%  �ű��ۼ� 
 *                        2011.07.05         �߰�     :Ȳ ����� �� �˰� ¥��.
 */
package com.uc.bs.cyber.service.bs502002;

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
public class Bs502002Service {

	protected Log log = LogFactory.getLog(this.getClass());
	
	private ApplicationContext appContext = null;
	
	private IbatisBaseService sqlService_cyber = null;
		
	private Bs502002FieldList bsField = null;
	
	CbUtil cUtil = null;
	
	/**
	 * ������
	 */
	public Bs502002Service(ApplicationContext appContext) {
		// TODO Auto-generated constructor stub
		
		setAppContext(appContext);
		
		bsField = new Bs502002FieldList();
		
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
	 * ���漼 �����뺸 ó��...
	 * */
	public byte[] chk_bs_502002(MapForm headMap, MapForm mf) throws Exception {
		
				
		MapForm sendMap = new MapForm();
		
		/*���漼 ����������ȸ �� ��� */
		String resCode = "000";       /*�����ڵ�*/
//		String giro_no = "1000685";   /*�λ�������ڵ�*/
		String strTaxGubun = "";
		String sg_code = "26";        /*�λ�ñ���ڵ�*/
		
		try{
			cUtil = CbUtil.getInstance();
			
			log.info(cUtil.msgPrint("", "S", "BP502002 chk_bs_502002()", resCode));
			
			/*����üũ*/
			/*���*/
			if(!headMap.getMap("GPUB_CODE").equals(sg_code)){
				resCode = "122";
			}
			/*�����������ڵ�*/
	        strTaxGubun = (String) sqlService_cyber.queryForBean("CODMBASE.GIRO_FOR_TAX", headMap);
//	          if(!headMap.getMap("GJIRO_NO").equals(giro_no)){
	        if(!("1".equals(strTaxGubun))){
				resCode = "123";
			}

	 		MapForm taxform = new MapForm();
	 		
			if (resCode.equals("000")) {
				
				taxform.setMap("TRDATE"    , headMap.getMap("TX_DATE"));                                          /*�ŷ��Ͻ�    */
				taxform.setMap("BGRNO"     , headMap.getMap("BCJ_NO"));                                           /*�����Ϸù�ȣ*/
				taxform.setMap("TRSPCO"    , headMap.getMap("PROGRAM_ID"));                                       /*�����ڵ�    */
				taxform.setMap("SNSU"      , headMap.getMap("RS_FLAG"));                                          /*������ü    */
				taxform.setMap("RESPCO"    ,"000" );       					                                      /*�����ڵ�    */
				taxform.setMap("SIDO_COD"  , "26");     					                                      /*�õ��ڵ�    */
				taxform.setMap("GIRO_NO"   , headMap.getMap("GJIRO_NO"));                                         /*�����ڵ�    */
				taxform.setMap("SUM_RCP"   , mf.getMap("NAPBU_AMT"));                                             /*�����ݾ�    */
				taxform.setMap("PAY_DT"    , mf.getMap("NAPBU_DATE"));                                            /*��������    */
				taxform.setMap("DRBKCO"    , CbUtil.lPadString(mf.getMap("OUTBANK_CODE").toString(), 7, '0'));    /*��������ڵ�*/
				taxform.setMap("REG_NM"    , mf.getMap("NAPBU_NAME"));                                            /*�����ڸ�    */
				taxform.setMap("OUT_DRACCN", mf.getMap("OUTACCT_NO"));                                            /*��ݰ���    */
				taxform.setMap("GUBUN"     , CbUtil.lPadString(mf.getMap("GUBUN").toString(), 2, '0'));           /*����        */  
				taxform.setMap("IN_DRACCN" , mf.getMap("INACCT_NO"));                                             /*�Աݰ���    */
				taxform.setMap("ETC"       , " ");        					                                      /*��Ÿ����    */
				taxform.setMap("TRTG"      , "0");         					                                      /*���ۿ���    */
				taxform.setMap("TR_TG"     , "0");        					                                      /*���۱���    */
				
				try {
					
					if(sqlService_cyber.queryForUpdate("BS502002.INSERT_ET2101_TB_EPAY", taxform) <= 0){
						resCode = "093";
					}
					
				}catch (Exception e){
					if (e instanceof DuplicateKeyException){
						resCode = "094";
					} else {
						resCode = "093";
					}
				}
				     
			}

			log.info(cUtil.msgPrint("", "", "BP502002 chk_bs_502002()", resCode));

        } catch (Exception e) {
			
        	resCode = "093";
			e.printStackTrace();
			log.error("============================================");
			log.error("== chk_bs_502002 Exception(�ý���) ");
			log.error("============================================");
		}
        
        /*������ �ƴϸ� �� ������ �״�� �����ְ� �����ڵ带 �����ؼ� �ش�.*/
        if(!resCode.equals("000")) {
        	sendMap = mf;
        }
        
        /*���⼭ ������带 �����Ѵ�. �ٲ�� �κи� �ٽ� �����ؼ� ������ ����*/
        headMap.setMap("RS_FLAG"   , "G");         /*�����̿���(G) */
        headMap.setMap("RESP_CODE" , resCode);     /*�����ڵ�*/
        headMap.setMap("GCJ_NO"    , "0" + sg_code + "0" + CbUtil.lPadString(String.valueOf(SeqNumber()), 8, '0'));     /*�̿��� �Ϸù�ȣ*/
        headMap.setMap("TX_GUBUN"  , "0210");
        
        return bsField.makeSendBuffer(headMap, sendMap);
	}

	/*----------------------------------------------*/
	/* �Ϸù�ȣ �������� �׽�Ʈ*/
	/*----------------------------------------------*/
	private int SeqNumber(){
    	return (Integer)sqlService_cyber.queryForBean("CODMBASE.CODM_GETSEQNO", "00");
    }


}

