/**
 *  �ֽý��۸� : �λ� ���̹����漼û ��ȭ
 *  ��  ��  �� : ���ڽŰ� 
 *  ��  ��  �� : ����ҵ漼 Ư��¡�� �Ű� ���
 *  Ŭ����  ID : Tax008Service
 *  ����  �̷� :
 * ------------------------------------------------------------------------
 *  �ۼ���      	�Ҽ�  		    ����            Tag           ����
 * ------------------------------------------------------------------------
 *  YHCHOI       ��ä��        2015.01.13         %01%         �����ۼ�
 *
 */

package com.uc.bs.cyber.daemon.txdm2610;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Element;

import com.uc.bs.cyber.CbUtil;
import com.uc.bs.cyber.SoapUtil;
import com.uc.core.MapForm;

public class Tax008Service {
	/**
	/**
	 * @param args
	 */
	
	private Log log ;
	
	public Tax008Service() {
		super();
		
		log = LogFactory.getLog(this.getClass());
	}
	
	public static void main(String[] args) {
		
		
	}

	 
	public MapForm sndService(StringBuffer sndForm, String SGG_COD) throws Exception {
		
		SoapUtil soap = new SoapUtil();
		MapForm recvForm = new MapForm(); 
		
		try {
				
			//log.debug("Tax008Service  sndService  SGG_COD : " + SGG_COD);
			// getSOAPMessage : ���� �۽� & ���� ó��
			Element rcvElement = soap.getSOAPMessage(sndForm, "CM_TAX_J2", "CM_TAX_J2_005", SGG_COD);
			
			//���� �� ���� �޽��� Ȯ��
			String message = soap.getValue(rcvElement, "MESSAGE");
			
            log.info("message===="+message);

			if(message.equals("SVR01")){			//�����϶�  Ȯ�� �ʿ�
			
			//  =���� ������=
				recvForm.setMap("MESSAGE"        ,  soap.getValue(rcvElement, "MESSAGE")  );      // ó������
				recvForm.setMap("tax_gubun"      ,  "140004" );      // ó������
				recvForm.setMap("CUD_OPT"        ,  soap.getValue(rcvElement, "CUD_OPT")  );		  //(1)ó������(1:�ű�,2:����,3:����  ��,������ �������ٸ�)  
				recvForm.setMap("DLQ_DIV"        ,  soap.getValue(rcvElement, "DLQ_DIV")  ); 		  // (1)�̳�ü������(1:�̳�(�ΰ�/����) , 2:ü��) 
				recvForm.setMap("LVY_DIV"        ,  soap.getValue(rcvElement, "LVY_DIV") );    		  // (2)�ΰ�����(���ں�ǥ���ڵ�(�����,���ú�,���ͳݽŰ���,��))
				recvForm.setMap("TAX_GDS"        ,  CbUtil.strEncod(soap.getValue(rcvElement, "TAX_GDS").toString(),"KSC5601","MS949").trim());     		  // (100)�������
				recvForm.setMap("TAX_NO"         ,  soap.getValue(rcvElement, "TAX_NO") );      	  // (31)������ȣ
				recvForm.setMap("EPAY_NO"        ,  soap.getValue(rcvElement, "EPAY_NO")  );    	  // (19)���ڳ��ι�ȣ     
				recvForm.setMap("REG_NO"         ,  soap.getValue(rcvElement, "REG_NO") );      	  // �ֹ�-���ι�ȣ
				recvForm.setMap("DPNM"           ,  CbUtil.strEncod(soap.getValue(rcvElement, "REG_NM").toString(),"KSC5601","MS949").trim());      	  // ����(���θ�)
				recvForm.setMap("BIZ_NO"         ,  soap.getValue(rcvElement, "BIZ_NO") );      	  // (10)����ڹ�ȣ
				recvForm.setMap("CMP_NM"         ,  CbUtil.strEncod(soap.getValue(rcvElement, "CMP_NM").toString(),"KSC5601","MS949").trim());         // ��ȣ
				recvForm.setMap("SIDO_COD"       ,  "26" );     								      // (2)�õ��ڵ�
				recvForm.setMap("SGG_COD"        ,  soap.getValue(rcvElement, "SGG_COD")  );    	  // (3)����ڵ�
				recvForm.setMap("CHK1"           ,  soap.getValue(rcvElement, "CHK1") );        	  // ��1
			    recvForm.setMap("ACCT_COD"       ,  soap.getValue(rcvElement, "ACCT_COD"    ) );      // ȸ���ڵ�
			    recvForm.setMap("TAX_ITEM"       ,  soap.getValue(rcvElement, "TAX_ITEM"    ) );      // �����ڵ�
			    recvForm.setMap("TAX_YY"         ,  soap.getValue(rcvElement, "TAX_YY"      ) );      // �����⵵
			    recvForm.setMap("TAX_MM"         ,  soap.getValue(rcvElement, "TAX_MM"      ) );      // ������
			    recvForm.setMap("TAX_DIV"        ,  soap.getValue(rcvElement, "TAX_DIV"     ) );      // ��������
			    recvForm.setMap("HACD"      	 ,  soap.getValue(rcvElement, "ADONG_COD"   ) );      // ������
			    recvForm.setMap("TAX_SNO"        ,  soap.getValue(rcvElement, "TAX_SNO"     ) );      // ������ȣ(6)  TAX_NO�� ���ڸ�
			    recvForm.setMap("CHK2"           ,  soap.getValue(rcvElement, "CHK2"        ) );      // ��2
			    recvForm.setMap("SUM_B_AMT"      ,  soap.getValue(rcvElement, "SUM_B_AMT"   ) );      // ���⳻�Ѿ�
			    recvForm.setMap("SUM_F_AMT"      ,  soap.getValue(rcvElement, "SUM_F_AMT"   ) );      // �������Ѿ�
			    recvForm.setMap("CHK3"           ,  soap.getValue(rcvElement, "CHK3"        ) );      // ��3
			    recvForm.setMap("MNTX"           ,  soap.getValue(rcvElement, "MNTX"        ) );      // ����
			    recvForm.setMap("CPTX"           ,  soap.getValue(rcvElement, "CPTX"        ) );      // ���ð�ȹ��
			    recvForm.setMap("CFTX_ASTX"      ,  soap.getValue(rcvElement, "CFTX_ASTX"   ) );      // ����/��Ư��
			    recvForm.setMap("LETX"           ,  soap.getValue(rcvElement, "LETX"        ) );      // ������
			    recvForm.setMap("DUE_DT"         ,  soap.getValue(rcvElement, "DUE_DT"      ) );      // ������
			    recvForm.setMap("CHK4"           ,  soap.getValue(rcvElement, "CHK4"        ) );      // ��4
			    recvForm.setMap("FILLER"         ,  soap.getValue(rcvElement, "FILLER"      ) );      // �ʷ�
			    recvForm.setMap("GOJI_DIV"       ,  soap.getValue(rcvElement, "GOJI_DIV"    ) );      // ��������
			    recvForm.setMap("CHK5"           ,  soap.getValue(rcvElement, "CHK5"        ) );      // ��5
			    recvForm.setMap("TAX_STD"        ,  soap.getValue(rcvElement,"TAX_STD"     ) );      // ����ǥ�ؾ�
			    recvForm.setMap("MNTX_ADTX"      ,  soap.getValue(rcvElement,"MNTX_ADTX"   ) );      // ���� �����
			    recvForm.setMap("CPTX_ADTX"      ,  soap.getValue(rcvElement,"CPTX_ADTX"   ) );      // ���ð�ȹ�� �����
			    recvForm.setMap("CFTX_ADTX"      ,  soap.getValue(rcvElement,"CFTX_ADTX"   ) );      // �����ü��� �����
			    recvForm.setMap("LETX_ADTX"      ,  soap.getValue(rcvElement,"LETX_ADTX"   ) );      // ������ �����
			    recvForm.setMap("ASTX_ADTX"      ,  soap.getValue(rcvElement,"ASTX_ADTX"   ) );      // ��Ư�� �����
			    recvForm.setMap("TAX_STD_DIS"    ,  soap.getValue(rcvElement,"TAX_STD_DIS" ) );      // ����ǥ�ؼ���
			    recvForm.setMap("CRE_DT"         ,  soap.getValue(rcvElement,"CRE_DT"      ) );      // �����߻���
			    recvForm.setMap("AUTO_TRNF_YN"   ,  soap.getValue(rcvElement,"AUTO_TRNF_YN") );      // �ڵ���ü����
			    recvForm.setMap("DUE_DF_OPT"     ,  soap.getValue(rcvElement,"DUE_DF_OPT"  ) );      // ���⳻�ı���
			    recvForm.setMap("GIROSGG_COD"    ,  soap.getValue(rcvElement,"GIROSGG_COD" ) );      // �������з��ڵ�
			    recvForm.setMap("GIRO_NO"        ,  soap.getValue(rcvElement,"GIRO_NO"     ) );      // ���ι�ȣ
			    recvForm.setMap("DUE_F_DT"       ,  soap.getValue(rcvElement,"DUE_F_DT"    ) );      // ������(������)
			    recvForm.setMap("CHRG_NM"        ,  CbUtil.strEncod(soap.getValue(rcvElement,"CHRG_NM").toString(),"KSC5601","MS949").trim());        // ����������̸�
			    recvForm.setMap("CHRG_TEL"       ,  soap.getValue(rcvElement,"CHRG_TEL"    ) );      // ��������� ��ȭ��ȣ
			    recvForm.setMap("CHG_DT"         ,  soap.getValue(rcvElement,"CHG_DT"      ) );      // �����Ͻ�
			    recvForm.setMap("PAY_DT"         ,  soap.getValue(rcvElement,"PAY_DT"      ) );      // ��������
			    
                //log.debug("=================recvForm================"+recvForm);
                
			}else {
				// ���� �ۼ��� �����Դϴ�.
	            log.error("���� �ۼ��� �����Դϴ�.");
	            recvForm=null;
			}
		} catch (Exception e) {
			
			e.printStackTrace();
			System.out.println(e.toString());
		}
		return recvForm;
		
	}
}
