/**
 *  �ֽý��۸� : ���̹����漼û ��ȭ
 *  ��  ��  �� : ���� ���γ��� ����
 *  ��  ��  �� : �λ�����-���̹���û 
 *               ����ó��
 *               ��������� ���ŵ� ������ ���ؼ� ���γ����� �����ϰ� �������� �۽�...
 *  Ŭ����  ID : Bs502101Service
 *  ����  �̷� :
 * ------------------------------------------------------------------------
 *  �ۼ���     �Ҽ�  		 ����      Tag   ����
 * ------------------------------------------------------------------------   
 * �۵���   ��ä��(��)    2011.06.24   %01%  �ű��ۼ�
 *  
 */
package com.uc.bs.cyber.service.bs502101;

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
public class Bs502101Service {

	protected Log log = LogFactory.getLog(this.getClass());
	
	private ApplicationContext appContext = null;
	
	private IbatisBaseService sqlService_cyber = null;
		
	private Bs502101FieldList bsField = null;
	
	CbUtil cUtil = null;
	
	/**
	 * ������
	 */
	public Bs502101Service(ApplicationContext appContext) {
		// TODO Auto-generated constructor stub
		
		setAppContext(appContext);
		
		bsField = new Bs502101FieldList();
		
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
	 * ���γ������� �뺸 ó��...
	 * */
	public byte[] chk_bs_502101(MapForm headMap, MapForm mf) throws Exception {
		
				
		MapForm sendMap = new MapForm();
		
		/*���ܼ��� ����������ȸ �� ��� */
		String resCode = "000";       /*�����ڵ�*/
//		String giro_no = "1000685";   /*�λ�������ڵ�*/
		String strTaxGubun = "";
		String sg_code = "26";        /*�λ�ñ���ڵ�*/
		
		try{
			cUtil = CbUtil.getInstance();
			
			log.info(cUtil.msgPrint("", "S", "BP502101 chk_bs_502101()", resCode));
			
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
			
			log.debug("JUMIN_NO = [" + mf.getMap("JUMIN_NO") + "]");
			log.debug("EPAY_NO  = [" + mf.getMap("EPAY_NO") + "]");
			
			/*������� �����̸�*/
			if(resCode.equals("000")) {
				
				/*�� ���̹���û�� ���γ����� �ִ��� �˻��Ѵ�.*/
				ArrayList<MapForm>  bsCmd502101List  =  sqlService_cyber.queryForList("BS502101.SELECT_RECIP_INFO", mf);
				
				log.info("bsCmd502101List() = [" + bsCmd502101List.size() +"]");
				
		        /* Error : ���γ������� */
				if(bsCmd502101List.size() <= 0){
					resCode = "112";  
				} 
				/* Error : ���γ��� 2�� �̻� */
				else if (bsCmd502101List.size() > 1) {
					resCode = "201";  //���񽺺Ұ�
				} else {
					
					MapForm mfCmd502101List  =  bsCmd502101List.get(0);
					
					if(mfCmd502101List.getMap("TAX_GB").equals("J")) {
						
						mfCmd502101List.setMap("SGG_COD" , mfCmd502101List.getMap("ETAXNO").toString().substring(0, 3)); 	//��û�ڵ�
						mfCmd502101List.setMap("ACCT_COD", mfCmd502101List.getMap("ETAXNO").toString().substring(4, 6));    //ȸ���ڵ�
						mfCmd502101List.setMap("TAX_ITEM", mfCmd502101List.getMap("ETAXNO").toString().substring(6, 12));   //����/�����ڵ�
						mfCmd502101List.setMap("TAX_YY"  , mfCmd502101List.getMap("ETAXNO").toString().substring(12, 16));  //�����⵵
						mfCmd502101List.setMap("TAX_MM"  , mfCmd502101List.getMap("ETAXNO").toString().substring(16, 18));	//������
						mfCmd502101List.setMap("TAX_DIV" , mfCmd502101List.getMap("ETAXNO").toString().substring(18, 19));	//����ڵ�
						mfCmd502101List.setMap("HACD"    , mfCmd502101List.getMap("ETAXNO").toString().substring(19, 22));	//�������ڵ�
						mfCmd502101List.setMap("TAX_SNO" , mfCmd502101List.getMap("ETAXNO").toString().substring(22, 28));	//������ȣ
						mfCmd502101List.setMap("PAY_CNT" , 0);	
						
						if (sqlService_cyber.queryForUpdate("BS502101.UPDATE_TX1201_TB_EPAY", mfCmd502101List) == 0 ) {//���漼���ڼ��� update
							resCode = "093"; //����
						}
						
					} else if(mfCmd502101List.getMap("TAX_GB").equals("C")) {
						
						if (sqlService_cyber.queryForUpdate("BS502101.UPDATE_TX2211_TB_EPAY", mfCmd502101List) == 0 ) {//ǥ�ؼ��ܼ��� ���ڼ��� update
							resCode = "093"; //����
						} 

					}
				
				}
					
			}
			
			log.info(cUtil.msgPrint("", "", "BP502101 chk_bs_502101()", resCode));

        } catch (Exception e) {
			
			e.printStackTrace();
			log.error("============================================");
			log.error("== chk_bs_502101 Exception(�ý���) ");
			log.error("============================================");
		}
        
        if (!resCode.equals("000")) {
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

