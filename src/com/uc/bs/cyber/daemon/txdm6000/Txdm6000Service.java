/**
 *  주시스템명 : 부산 사이버지방세청 고도화
 *  업  무  명 : 전자신고 
 *  기  능  명 : 자동차 저당권 설정/말소 신고 등록
 *  클래스  ID : Txdm6000Service
 *  변경  이력 :
 * ------------------------------------------------------------------------
 *  작성자      	소속  		    일자            Tag           내용
 * ------------------------------------------------------------------------
 *  yh.choi      유채널     2014.05.08         %01%         최초작성
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
			
			// 지방세 사업단으로 전송
			cMsg = new Txdm6000Messages(sndForm);
						
			// getSOAPMessage : 전문 송신 & 수신 처리
			Element rcvElement = soap.getSOAPMessage(cMsg.getMessage(), "CM_TAX_F9", "CM_TAX_F9_200");
			
			//전송 후 응답 메시지 확인
			String message = soap.getValue(rcvElement, "MESSAGE");
			
			
			log.info("message===="+message);
			  
			//전송완료
			if(message.equals("SVR01")){			
				
				taxVars = Txdm6000Messages.getDocVars();
				
				for(int i= 0; i<taxVars[1].length; i++) {
					
					recvForm.setMap(taxVars[1][i], CbUtil.strEncod((soap.getValue(rcvElement, taxVars[1][i])), "KSC5601","MS949"));
				}
				
				log.info("recvForm :: "+recvForm);
			}
			//전송 오류입니다.
			else{
				
				taxVars = Txdm6000Messages.getDocVars();
				
				for(int i= 0; i<taxVars[1].length; i++) {
					
					recvForm.setMap(taxVars[1][i], CbUtil.strEncod((soap.getValue(rcvElement, taxVars[1][i])), "KSC5601","MS949"));
				}
				
				log.info("recvForm :: "+recvForm);
				
				log.error("전송오류 입니다.");
			}
			
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		
		return recvForm;
	}
}
