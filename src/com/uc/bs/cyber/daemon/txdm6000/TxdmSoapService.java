package com.uc.bs.cyber.daemon.txdm6000;

import java.io.ByteArrayInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPBodyElement;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPFactory;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.uc.bs.cyber.CbUtil;

public class TxdmSoapService 
{

	Element elmnt=null;

	private Log log ;

    public Element getSOAPMessage(StringBuffer reqXml,String usecaseId, String eventId) throws Exception 
    {


    	String mode=CbUtil.getResource("ApplicationResource", "tbl.mode");
        String target = "";
        
    	if(mode.equals("TEST")) target = CbUtil.getResource("ApplicationResource", "tbl.wsdl.target.test");
    	else target = CbUtil.getResource("ApplicationResource", "tbl.wsdl.target.sido");
    	
        log.info("target :  "+target);

        TxdmSoapService st = new TxdmSoapService();

        // SOAPMessage ��ü ����
        MessageFactory messageFactory = MessageFactory.newInstance();
        SOAPMessage requestMessage = messageFactory.createMessage();
        
        // SOAPPart ��ü ����
        SOAPPart soapPart = requestMessage.getSOAPPart();
        
        // SOAPEnvelope ��ü ����
        SOAPEnvelope envelope = soapPart.getEnvelope();
        
        // ���ӽ����̽� ����
        envelope.addNamespaceDeclaration("xsd", "http://www.w3.org/2001/XMLSchema");
        envelope.addNamespaceDeclaration("xsi", "http://www.w3.org/2001/XMLSchema-instance");
        
        // ���ڵ� ��� ����
        envelope.setEncodingStyle("http://schemas.xmlsoap.org/soap/encoding/");
        
        // SOAPFactory ��ü ����
        SOAPFactory soapFactory = SOAPFactory.newInstance();
        
        // SOAPHeader ��ü ���
        // SOAPHeader header = envelope.getHeader();
        
        // SOAPBody ��ü ���
        SOAPBody body = envelope.getBody();
        
        // �ٵ� ��Ʈ�� ����

        String xml = reqXml.toString();

        // �̹� envelope���� ���ӽ����̽��� ���������Ƿ� ���������ڴ� null �̴�.
        Name bodyName = soapFactory.createName("Execute", "ns1", "http://ws.disc.com");
        SOAPBodyElement bodyElement = body.addBodyElement(bodyName);
        
        // �ٵ� ��Ʈ�� �ڽ� ������Ʈ ����
        Name childName = soapFactory.createName("usecaseId");
        SOAPElement childElement = bodyElement.addChildElement(childName);
        Name typeName = soapFactory.createName("type", "xsi", null);
        childElement.addAttribute(typeName, "xsd:string");
        childElement.addTextNode(usecaseId);

        Name childName1 = soapFactory.createName("eventId");
        SOAPElement childElement1 = bodyElement.addChildElement(childName1);
        // Name typeName1 = soapFactory.createName("type", "xsi", null);
        childElement1.addAttribute(typeName, "xsd:string");
        childElement1.addTextNode(eventId);

        Name childName2 = soapFactory.createName("reqXml");
        SOAPElement childElement2 = bodyElement.addChildElement(childName2);
        // Name typeName2 = soapFactory.createName("type", "xsi", null);
        childElement2.addAttribute(typeName, "xsd:string");
        childElement2.addTextNode(xml);

        // ���ݱ��� ������ XML SOAP �޽����� ȭ�鿡 ����Ѵ�.
        log.debug("---------------------������ �޼���--------------------------------");
        requestMessage.writeTo(System.out);
        
        System.out.println();
        log.debug(requestMessage.getSOAPPart().getContent());
        log.debug("-----------------------------------------------------");

        // SOAPConnection ��ü����
        SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
       
        SOAPConnection connection = soapConnectionFactory.createConnection();
      
        
        // ��û SOAP �޽����� ������, ���� SOAP �޽����� �޴´�.
        SOAPMessage responseMessage = connection.call(requestMessage,target);
        
        log.info("After connection.call " + responseMessage);
        log.debug("=============================================================");
        responseMessage.writeTo(System.out);
        System.out.println();
        log.info("CONTENT===" + responseMessage.getSOAPPart().getContent());
        log.debug("=============================================================");
        
        // NodeList nl = responseMessage.getSOAPPart().getChildNodes();
        
        // DOMSource domSource = (DOMSource)responseMessage.getSOAPPart().getContent();
        
        // Node node = responseMessage.getSOAPPart().
   
        // Node node = domSource.getNode();
        Node node = null;

        // NodeList nl = null;
 
        
        String returnXML = "";
    
        
        NodeList nl = responseMessage.getSOAPPart().getChildNodes();
        
        while(true)
        {
        	
        	if(node == null) {
                node = nl.item(nl.getLength()-1);
        	} else if(node.getNodeName().equals("ExecuteReturn")) {
                nl = node.getChildNodes();
                node = nl.item(nl.getLength()-1);
                returnXML = node.getNodeValue();
                break;
            }
            else
            {
                nl = node.getChildNodes();
                node = nl.item(nl.getLength()-1);
            }
        }


        log.info("ExecuteReturn Return XML :"+returnXML.toString());

        Document doc = null;
    	DocumentBuilderFactory dbf = 	DocumentBuilderFactory.newInstance();
    	DocumentBuilder db = dbf.newDocumentBuilder();

        doc = db.parse(new ByteArrayInputStream(returnXML.getBytes()));

        doc.getDocumentElement().normalize();

        //xml elements
        NodeList nodeLst = null;
//      String elmr[] = {"COMMON", "RECORD"};
        String elmr[] = {"LTIS"};

        //xml file reader
        for (int r = 0; r < elmr.length; r++)
        {
            nodeLst = doc.getElementsByTagName(elmr[r]);

//          if (elmr[r].equals("RECORD"))
            if (elmr[r].equals("LTIS"))
            {
                for (int s = 0; s < nodeLst.getLength(); s++)
                {

                    Node nd = nodeLst.item(s);

                     if (nd.getNodeType() == Node.ELEMENT_NODE)
                     {
                        elmnt = (Element) nd;

                        log.debug("LTIS ELEMENT �� �����ͼ� �׽�Ʈ");
                        log.debug(st.getValue(elmnt, "MESSAGE"));

                     }
                }
            }
/*
            else if(elmr[r].equals("COMMON"))
            {
                for (int s = 0; s < nodeLst.getLength(); s++)
                {

                    Node nd = nodeLst.item(s);

                     if (nd.getNodeType() == Node.ELEMENT_NODE)
                     {
                        elmnt = (Element) nd;

                        log.debug(st.getValue(elmnt, "MESSAGE"));
                     }
                }
            }
*/
        }

        connection.close();
        return elmnt;
    }

    public TxdmSoapService() {
		super();
		
		log = LogFactory.getLog(this.getClass());
		
		// TODO Auto-generated constructor stub
	}

	// element value out
    public String getValue(Element elmnt, String element) {
         Object ob = null;
         NodeList elmntLst = elmnt.getElementsByTagName(element);
         Element inElmnt = (Element) elmntLst.item(0);
         NodeList val = inElmnt.getChildNodes();


        if((Node)val.item(0) != null)
        {
             ob = ((Node) val.item(0)).getNodeValue();
         }else {
             ob = "";
         }

         return (String)ob;
    }

}