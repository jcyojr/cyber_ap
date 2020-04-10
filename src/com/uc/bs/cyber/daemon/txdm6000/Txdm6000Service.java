/**
 *  �ֽý��۸� : �λ� ���̹����漼û ��ȭ
 *  ��  ��  �� : ���ڽŰ� 
 *  ��  ��  �� : �ڵ��� ����� ����/���� �Ű� ���
 *  Ŭ����  ID : Txdm6000Service
 *  ����  �̷� :
 * ------------------------------------------------------------------------
 *  �ۼ���      	�Ҽ�  		    ����            Tag           ����
 * ------------------------------------------------------------------------
 *  yh.choi      ��ä��     2014.05.08         %01%         �����ۼ�
 *  
 */

package com.uc.bs.cyber.daemon.txdm6000;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Element;

import com.uc.bs.cyber.CbUtil;
import com.uc.core.MapForm;

public class Txdm6000Service {
	/**
	/**
	 * @param args
	 */
	
	private Log log ;
	
	public Txdm6000Service() {
		super();
		
		log = LogFactory.getLog(this.getClass());
	}
	
	public static void main(String[] args) {
		
		
	}



	public MapForm sndService(MapForm sndForm) throws Exception {
		
		log.debug("sndForm :: " + sndForm);

		TxdmSoapService soap = new TxdmSoapService();
		
		MapForm recvForm = new MapForm();
		
		Txdm6000Messages cMsg = new Txdm6000Messages(sndForm);
		
		String [][] taxVars  = cMsg.getTaxVars();

		try {
			
			// ���漼 ��������� ����
			cMsg = new Txdm6000Messages(sndForm);
						
			// getSOAPMessage : ���� �۽� & ���� ó��
			Element rcvElement = soap.getSOAPMessage(cMsg.getMessage(), "CM_TAX_F9", "CM_TAX_F9_200");
			
			//���� �� ���� �޽��� Ȯ��
			String message = soap.getValue(rcvElement, "MESSAGE");
			
			
			log.info("message===="+message);
			  
			//���ۿϷ�
			if(message.equals("SVR01")){			
				
				taxVars = Txdm6000Messages.getDocVars();
				
				for(int i= 0; i<taxVars[1].length; i++) {
					
					recvForm.setMap(taxVars[1][i], CbUtil.strEncod((soap.getValue(rcvElement, taxVars[1][i])), "KSC5601","MS949"));
				}
				
				log.info("recvForm :: "+recvForm);
			}
			//���� �����Դϴ�.
			else{
				
				taxVars = Txdm6000Messages.getDocVars();
				
				for(int i= 0; i<taxVars[1].length; i++) {
					
					recvForm.setMap(taxVars[1][i], CbUtil.strEncod((soap.getValue(rcvElement, taxVars[1][i])), "KSC5601","MS949"));
				}
				
				log.info("recvForm :: "+recvForm);
				
				log.error("���ۿ��� �Դϴ�.");
			}
			
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		
		return recvForm;
	}
}
