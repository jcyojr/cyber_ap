/**
 *  �ֽý��۸� : �λ� ���̹����漼û ��ȭ
 *  ��  ��  �� : ���ڽŰ� 
 *  ��  ��  �� : ����ҵ漼 ���ռҵ�  �Ű� ���
 *  Ŭ����  ID : Tax004Service
 *  ����  �̷� :
 * ------------------------------------------------------------------------
 *  �ۼ���      	�Ҽ�  		    ����            Tag           ����
 * ------------------------------------------------------------------------
 *  �ڽ���       �ٻ�ý���     2011.05.13         %01%         �����ۼ�
 *  
 */

package com.uc.bs.cyber.daemon.txdm2610;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Element;

import com.uc.bs.cyber.CreateMessages_eswb5010;
import com.uc.bs.cyber.CbUtil;
import com.uc.bs.cyber.DocumentVars;
import com.uc.bs.cyber.SoapUtil;
import com.uc.core.MapForm;

public class Tax009Service {
	/**
	/**
	 * @param args
	 */
	
	private Log log ;
	
	public Tax009Service() {
		super();
		
		log = LogFactory.getLog(this.getClass());
	}
	
	public static void main(String[] args) {
		
		
	}



	public MapForm sndService(MapForm sndForm) throws Exception {
		
		String SGG_COD = sndForm.get("SGG_COD").toString();
		
		SoapUtil soap = new SoapUtil();
		MapForm recvForm = new MapForm();
		
		CreateMessages_eswb5010 cMsg = new CreateMessages_eswb5010(sndForm);
		
		String [][] taxVars  = cMsg.getTaxVars();

		try {
			
			log.info("�ֹμ� ���� send msg [" + sndForm + "]");
			
			// ���漼 ��������� ����
			cMsg = new CreateMessages_eswb5010(sndForm);
						
			// getSOAPMessage : ���� �۽� & ���� ó��
			Element rcvElement = soap.getSOAPMessage(cMsg.getMessage(), "CM_TAX_53", "CM_TAX_53_165", SGG_COD);
			
			//���� �� ���� �޽��� Ȯ��
			String message = soap.getValue(rcvElement, "MESSAGE");
			
			
			log.info("message====  " + message);
			  
			if(message.equals("SVR01")){			//�����϶� Ȯ�� �ʿ�
				
				taxVars = DocumentVars.getDocVars("104009");
				
				for(int i= 0; i<taxVars[1].length; i++) {
					
					recvForm.setMap(taxVars[1][i], CbUtil.strEncod((soap.getValue(rcvElement, taxVars[1][i])), "KSC5601","MS949"));
				}
				// insertForm���� �ʿ��� ���� recvForm�� ���� 
				recvForm.setMap("tax_gubun"      ,   "104009" );      						// ó������
				recvForm.setMap("TAX_GDS"        ,   CbUtil.strEncod(recvForm.getMap("TAX_GDS").toString(),"KSC5601","MS949").trim());  // �������
	            recvForm.setMap("DPNM"           ,   CbUtil.strEncod(recvForm.getMap("REG_NM").toString(),"KSC5601","MS949").trim());   // ����(���θ�)
	            recvForm.setMap("CMP_NM"         ,   CbUtil.strEncod(recvForm.getMap("CMP_NM").toString(),"KSC5601","MS949").trim());   // ��ȣ
	            recvForm.setMap("CHRG_NM"        ,   CbUtil.strEncod(recvForm.getMap("CHRG_NM").toString(),"KSC5601","MS949").trim());  // ����������̸�
	            recvForm.setMap("CHG_DT"         ,   CbUtil.convertDate(recvForm.getMap("CHG_DT").toString()));        				    // ��������
	            recvForm.setMap("BIZ_GRNO"       ,   CbUtil.convertDate(recvForm.getMap("GIRO_NO").toString()).trim());        			// ���������ȣ
	            recvForm.setMap("B_TAX_NO"       ,   recvForm.getMap("TAX_NO"       ) );      // (31)������ȣ
	            
                /*
                recvForm.setMap("CUD_OPT"         ,   sndForm.get("CUD_OPT"     ) );     	 // ó������
                recvForm.setMap("DLQ_DIV"         ,   sndForm.get("DLQ_DIV"     ) );      	 // �̳�ü������
                recvForm.setMap("LVY_DIV"         ,   insertForm.get("LVY_DIV"     ) );      // �ΰ�����
                recvForm.setMap("TAX_GDS"         ,   insertForm.get("TAX_GDS"     ) );      // �������
                recvForm.setMap("TAX_NO"          ,   insertForm.get("TAX_NO"      ) );      // ������ȣ
                recvForm.setMap("EPAY_NO"         ,   insertForm.get("EPAY_NO"     ) );      // ���ڳ��ι�ȣ
                recvForm.setMap("REG_NO"          ,   insertForm.get("REG_NO"      ) );      // �ֹ�-���ι�ȣ
                recvForm.setMap("REG_NM"          ,   insertForm.get("REG_NM"      ) );      // ����(���θ�)
                recvForm.setMap("BIZ_NO"          ,   insertForm.get("BIZ_NO"      ) );      // ����ڹ�ȣ
                recvForm.setMap("CMP_NM"          ,   insertForm.get("CMP_NM"      ) );      // ��ȣ
                recvForm.setMap("SIDO_COD"        ,   insertForm.get("SIDO_COD"    ) );      // �õ��ڵ�
                recvForm.setMap("SGG_COD"         ,   insertForm.get("SGG_COD"     ) );      // ����ڵ�
                recvForm.setMap("CHK1"            ,   insertForm.get("CHK1"        ) );      // ��1
                recvForm.setMap("ACCT_COD"        ,   insertForm.get("ACCT_COD"    ) );      // ȸ���ڵ�
                recvForm.setMap("TAX_ITEM"        ,   insertForm.get("TAX_ITEM"    ) );      // �����ڵ�
                recvForm.setMap("TAX_YY"          ,   insertForm.get("TAX_YY"      ) );      // �����⵵
                recvForm.setMap("TAX_MM"          ,   insertForm.get("TAX_MM"      ) );      // ������
                recvForm.setMap("TAX_DIV"         ,   insertForm.get("TAX_DIV"     ) );      // ��������
                recvForm.setMap("ADONG_COD"       ,   insertForm.get("ADONG_COD"   ) );      // ������
                recvForm.setMap("TAX_SNO"         ,   insertForm.get("TAX_SNO"     ) );      // ������ȣ
                recvForm.setMap("CHK2"            ,   insertForm.get("CHK2"        ) );      // ��2
                recvForm.setMap("SUM_B_AMT"       ,   insertForm.get("SUM_B_AMT"   ) );      // ���⳻�Ѿ�
                recvForm.setMap("SUM_F_AMT"       ,   insertForm.get("SUM_F_AMT"   ) );      // �������Ѿ�
                recvForm.setMap("CHK3"            ,   insertForm.get("CHK3"        ) );      // ��3
                recvForm.setMap("MNTX"            ,   insertForm.get("MNTX"        ) );      // ����
                recvForm.setMap("CPTX"            ,   insertForm.get("CPTX"        ) );      // ���ð�ȹ��
                recvForm.setMap("CFTX_ASTX"       ,   insertForm.get("CFTX_ASTX"   ) );      // ����/��Ư��
                recvForm.setMap("LETX"            ,   insertForm.get("LETX"        ) );      // ������
                recvForm.setMap("DUE_DT"          ,   insertForm.get("DUE_DT"      ) );      // ������
                recvForm.setMap("CHK4"            ,   insertForm.get("CHK4"        ) );      // ��4
                recvForm.setMap("FILLER"          ,   insertForm.get("FILLER"      ) );      // �ʷ�
                recvForm.setMap("GOJI_DIV"        ,   insertForm.get("GOJI_DIV"    ) );      // ��������
                recvForm.setMap("CHK5"            ,   insertForm.get("CHK5"        ) );      // ��5
                recvForm.setMap("TAX_STD"         ,   insertForm.get("TAX_STD"     ) );      // ����ǥ�ؾ�
                recvForm.setMap("MNTX_ADTX"       ,   insertForm.get("MNTX_ADTX"   ) );      // ���� �����
                recvForm.setMap("CPTX_ADTX"       ,   insertForm.get("CPTX_ADTX"   ) );      // ���ð�ȹ�� �����
                recvForm.setMap("CFTX_ADTX"       ,   insertForm.get("CFTX_ADTX"   ) );      // �����ü��� �����
                recvForm.setMap("LETX_ADTX"       ,   insertForm.get("LETX_ADTX"   ) );      // ������ �����
                recvForm.setMap("ASTX_ADTX"       ,   insertForm.get("ASTX_ADTX"   ) );      // ��Ư�� �����
                recvForm.setMap("TAX_STD_DIS"     ,   insertForm.get("TAX_STD_DIS" ) );      // ����ǥ�ؼ���
                recvForm.setMap("CRE_DT"          ,   insertForm.get("CRE_DT"      ) );      // �����߻���
                recvForm.setMap("AUTO_TRNF_YN"    ,   insertForm.get("AUTO_TRNF_YN") );      // �ڵ���ü����
                recvForm.setMap("DUE_DF_OPT"      ,   insertForm.get("DUE_DF_OPT"  ) );      // ���⳻�ı���
                recvForm.setMap("GIROSGG_COD"     ,   insertForm.get("GIROSGG_COD" ) );      // �������з��ڵ�
                recvForm.setMap("GIRO_NO"         ,   insertForm.get("GIRO_NO"     ) );      // ���ι�ȣ
                recvForm.setMap("DUE_F_DT"        ,   insertForm.get("DUE_F_DT"    ) );      // ������(������)
                recvForm.setMap("CHRG_NM"         ,   insertForm.get("CHRG_NM"     ) );      // ��������� ����
                recvForm.setMap("CHRG_TEL"        ,   insertForm.get("CHRG_TEL"    ) );      // ��������� ��ȭ��ȣ
                recvForm.setMap("CHG_DT"          ,   insertForm.get("CHG_DT"      ) );      // �����Ͻ�
                recvForm.setMap("PAY_DT"          ,   insertForm.get("PAY_DT"      ) );      // ��������                
                */
                
			}else {
				// ���� �ۼ��� �����Դϴ�.
				log.error("���� �ۼ��� �����Դϴ�.");
			}
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		
		return recvForm;
	}
}
