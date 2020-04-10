/**
 * 주시스템명        : 유채널 프레임웍
 * 업  무  명        : 공통
 * 기  능  명        : WebService 호출
 * 클래스  ID        : WsdlUtil
 * 변경이력          :
 *
 * -------------------------------------------------------------------------------
 *  작성자        소속           일  자          Tag              내용
 * -------------------------------------------------------------------------------
 *  김대완      유채널(주)     2009.10.08        %01%          신규작성     
 *  송동욱      유채널(주)     2011.05.28        %01%          Copy & modify  
 */
package com.uc.bs.cyber;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Iterator;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFactory;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.transform.dom.DOMSource;

import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.uc.core.MapForm;

public class WsdlUtil {
	
	Log log = LogFactory.getLog(this.getClass());
	
	public Document sendNRcv(String ifId, String target, String srcOrg, String desOrg, String msgKey, MapForm dataMap) throws Exception {
		
        try {
			// SOAPMessage 객체 생성
			MessageFactory messageFactory = MessageFactory.newInstance();
			SOAPMessage requestMessage = messageFactory.createMessage();
			
			// SOAPPart 객체 생성
			SOAPPart soapPart = requestMessage.getSOAPPart();
			
			// SOAPEnvelope 객체 생성
			SOAPEnvelope envelope = soapPart.getEnvelope();
			

			// 네임스페이스 지정
			envelope.addNamespaceDeclaration("xsd", "http://www.w3.org/2001/XMLSchema");
			envelope.addNamespaceDeclaration("xsi", "http://www.w3.org/2001/XMLSchema-instance");
			
			// 인코딩 방식 지정
			envelope.setEncodingStyle("http://schemas.xmlsoap.org/soap/encoding/");

			
			// SOAPFactory 객체 생성
			SOAPFactory soapFactory = SOAPFactory.newInstance();
			
			// SOAPHeader 객체 얻기
			SOAPHeader header = envelope.getHeader();
			
			
			// SOAPBody 객체 얻기
			SOAPBody body = envelope.getBody();
			

			Name docName = soapFactory.createName("DOCUMENT");
			SOAPElement docElement = body.addBodyElement(docName);			        
			
			/**
			 * IFID 
			 */
			Name ifidName = soapFactory.createName("IFID");
			SOAPElement ifidElement = docElement.addChildElement(ifidName);
			ifidElement.addTextNode(ifId);
			
			/**
			 * SRCORGCD
			 */
			Name srcorgName = soapFactory.createName("SRCORGCD");
			SOAPElement srcorgElement = docElement.addChildElement(srcorgName);
			srcorgElement.addTextNode(srcOrg);
			
			/**
			 * 
			 */
			Name tgtorgName = soapFactory.createName("TGTORGCD");
			SOAPElement tgtorgElement = docElement.addChildElement(tgtorgName);
			tgtorgElement.addTextNode(desOrg);
			
			/**
			 * 
			 */
			Name rescdName = soapFactory.createName("RESULTCODE");
			SOAPElement rescdElement = docElement.addChildElement(rescdName);
			rescdElement.addTextNode("000");
			
			/**
			 * 
			 */
			Name msgkeyName = soapFactory.createName("MSGKEY");
			SOAPElement msgkeyElement = docElement.addChildElement(msgkeyName);
			msgkeyElement.addTextNode(msgKey);
			
			/**
			 * 
			 */
			Name dataName = soapFactory.createName("DATA");
			SOAPElement dataElement = docElement.addChildElement(dataName);

			/**
			 * 
			 */
			Name msgName = soapFactory.createName("message");
			SOAPElement msgElement = dataElement.addChildElement(msgName);

			Name headName = soapFactory.createName("header");
			SOAPElement headElement = msgElement.addChildElement(headName);

			Name resCnt = soapFactory.createName("res_cnt");
			SOAPElement resElement = headElement.addChildElement(resCnt);
			
			/**
			 * 
			 */
			Name bodyName = soapFactory.createName("body");
			SOAPElement bodyElement = msgElement.addChildElement(bodyName);
			
			addNodeForMap(bodyElement, dataMap, soapFactory);
			
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			requestMessage.writeTo(os);
			os.toByteArray();
						
			try {
				log.debug("SEND MSG :: " + new String(os.toByteArray(), "UTF-8"));
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			// SOAPConnection 객체생성
			SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
			SOAPConnection connection = soapConnectionFactory.createConnection();

			// 요청 SOAP 메시지를 보내고, 응답 SOAP 메시지를 받는다.
			SOAPMessage responseMessage = connection.call(requestMessage,target);

	        connection.close();

	        os.reset();
	        
			responseMessage.writeTo(os);
			os.toByteArray();
						
			try {
				log.debug("RECV MSG :: " + new String(os.toByteArray(), "UTF-8"));
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}	        
			
			// com.sun.xml.internal.messaging.saaj.util.JAXMStreamSource

			// return  responseMessage.getSOAPPart().getChildNodes();
			
			log.debug("SOAPPart() :: " + responseMessage.getSOAPPart());

			
			return  (Document) responseMessage.getSOAPPart();
			
			/*
	        DOMSource domSource = (DOMSource)responseMessage.getSOAPPart().getContent();
			//Set the output for the transformation

            return domSource.getNode();
			*/
			// return factory.newDocumentBuilder().parse(((StreamSource)sourceContent).getInputStream());
			
		} catch (UnsupportedOperationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
			throw e;
		} catch (SOAPException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
			throw e;

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
			throw e;
		}
		
	}
	
	
	/**
	 * 
	 * @param pElement
	 * @param map
	 * @param soapFactory
	 * @throws SOAPException
	 */
	private void addNodeForMap(SOAPElement pElement, MapForm map, SOAPFactory soapFactory) throws SOAPException {
		
		Iterator<?> itr = map.getKeyList().iterator(); 
		
		while(itr.hasNext()) {
			String key = (String) itr.next();

			Object val = map.get(key);
			
			if(val instanceof MapForm) {
				SOAPElement element = pElement.addChildElement(key);
				
				addNodeForMap(element, (MapForm) val, soapFactory);
				
			} else if(val instanceof String){
				Name nodeName = soapFactory.createName(key);
				
				SOAPElement nodeElement = pElement.addChildElement(nodeName);
				
				nodeElement.addTextNode(val==null?"":(String)val);				
				
			} else {
				Name nodeName = soapFactory.createName(key);
				log.debug("NODENAME==" + nodeName);
				SOAPElement nodeElement = pElement.addChildElement(nodeName);
								
			}
			
		}		
		
	}
	
}
