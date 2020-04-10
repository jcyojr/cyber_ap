/**
 * Sample TEST
 */
package com.uc.bs.cyber.daemon.txdm2530;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.uc.core.MapForm;
import com.uc.egtob.net.ClientMessageService;
import com.uc.bs.cyber.CbUtil;

/**
 * @author 프리비
 *
 */
public class Txdm2530CSample  extends ClientMessageService implements Runnable {

	/**
	 * 
	 * @throws IOException
	 * @throws Exception
	 */

	private int  myId = 0; 
	
	protected Log log = LogFactory.getLog(this.getClass());
	
	/**
	 * 
	 * @param hostAddr
	 * @param port
	 * @throws IOException
	 * @throws Exception
	 */
	public Txdm2530CSample() {
		
		CbUtil.setupLog4jConfig(this, "log4j.tomcat.xml");

	}
		
	public static void main(String[] args) {
		
		int j = 0;
				
		while(true) {
			
			for(int i = 0; i<1; i++) {
				
				Txdm2530CSample client = new Txdm2530CSample();
				
				client.myId = j++;
				
				Thread thr = new Thread(client);
	
				thr.start();
				
			}
			
			
			try {
				
				Thread.sleep(100);
				
				break;
				
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		
		}		
	}

	public void run() {
		// TODO Auto-generated method stub
		
		MapForm dataMap = new MapForm();
		
        String sendDa = "";
        
		try {

			Txdm2530CSample client = new Txdm2530CSample();
			client.Connect("127.0.0.1", 9382);       /*위택스 테스트*/
			
			//sendDa = "21                              손은희                                                                          1801110240414   6048136070      608823부산 남구 문현동                                                                                                                265-1                                                                                                                           26290eun0205@chol.com                                                                                    Y0516359981                                                                                                                      01093953990                                                                                                                     (주)대현어패럴(DAE HYUN APPAREL)0020027200504187219530,ou=DAE HYUN APPAREL,ou=WOORI,ou=corporation,o=yessign,c=kr                                                                                                                                               대현어패럴                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                      대현어패럴                                                                                                                                              ";
			
			//long before = System.currentTimeMillis();
		
			//client.sendData(sendDa.getBytes());
			
			//client.sendData(fieldList.getBuff(dataMap));
			
			//client.sendData("0001425320011102241014300CT0000017640EP002483032261000685710228196667226470110100420090  000000000021880201102240842828             CQ              000115992001IGN1102241008230230500000800EP00248284826100068502386076303092037727023010000078110224100821000000000006170S 0001425320011102241014300CT0000017640EP002483032261000685710228196667226470110100420090  000000000021880201102240842828             CQ              0001425320011102241014300CT0000017640EP002483032261000685710228196667226470110100420090  000000000021880201102240842828             CQ              0001425320011102241014300CT0000017640EP002483032261000685710228196667226470110100420090  000000000021880201102240842828             CQ              0001425320011102241014300CT0000017640EP002483032261000685710228196667226470110100420090  000000000021880201102240842828             CQ              0001425320011102241014300CT0000017640EP002483032261000685710228196667226470110100420090  000000000021880201102240842828             CQ              0001425320011102241014300CT0000017640EP002483032261000685710228196667226470110100420090  000000000021880201102240842828             CQ              0001425320011102241014300CT0000017640EP002483032261000685710228196667226470110100420090  000000000021880201102240842828             CQ              0001425320011102241014300CT0000017640EP002483032261000685710228196667226470110100420090  000000000021880201102240842828             CQ              0001425320011102241014300CT0000017640EP002483032261000685710228196667226470110100420090  000000000021880201102240842828             CQ              0001425320011102241014300CT0000017640EP002483032261000685710228196667226470110100420090  000000000021880201102240842828             CQ              0001425320011102241014300CT0000017640EP002483032261000685710228196667226470110100420090  000000000021880201102240842828             CQ              0001425320011102241014300CT0000017640EP002483032261000685710228196667226470110100420090  000000000021880201102240842828             CQ              0001425320011102241014300CT0000017640EP002483032261000685710228196667226470110100420090  000000000021880201102240842828             CQ              0001425320011102241014300CT0000017640EP002483032261000685710228196667226470110100420090  000000000021880201102240842828             CQ              0001425320011102241014300CT0000017640EP002483032261000685710228196667226470110100420090  000000000021880201102240842828             CQ              0001425320011102241014300CT0000017640EP002483032261000685710228196667226470110100420090  000000000021880201102240842828             CQ              0001425320011102241014300CT0000017640EP002483032261000685710228196667226470110100420090  000000000021880201102240842828             CQ              0001425320011102241014300CT0000017640EP002483032261000685710228196667226470110100420090  000000000021880201102240842828             CQ              0001425320011102241014300CT0000017640EP002483032261000685710228196667226470110100420090  000000000021880201102240842828             CQ              0001425320011102241014300CT0000017640EP002483032261000685710228196667226470110100420090  000000000021880201102240842828             CQ              0001425320011102241014300CT0000017640EP002483032261000685710228196667226470110100420090  000000000021880201102240842828             CQ              0001425320011102241014300CT0000017640EP002483032261000685710228196667226470110100420090  000000000021880201102240842828             CQ              0001425320011102241014300CT0000017640EP002483032261000685710228196667226470110100420090  000000000021880201102240842828             CQ              0001425320011102241014300CT0000017640EP002483032261000685710228196667226470110100420090  000000000021880201102240842828             CQ              0001425320011102241014300CT0000017640EP002483032261000685710228196667226470110100420090  000000000021880201102240842828             CQ              0001425320011102241014300CT0000017640EP002483032261000685710228196667226470110100420090  000000000021880201102240842828             CQ              0001425320011102241014300CT0000017640EP002483032261000685710228196667226470110100420090  000000000021880201102240842828             CQ              0001425320011102241014300CT0000017640EP002483032261000685710228196667226470110100420090  000000000021880201102240842828             CQ              0001425320011102241014300CT0000017640EP002483032261000685710228196667226470110100420090  000000000021880201102240842828             CQ              0001425320011102241014300CT0000017640EP002483032261000685710228196667226470110100420090  000000000021880201102240842828             CQ              0001425320011102241014300CT0000017640EP002483032261000685710228196667226470110100420090  000000000021880201102240842828             CQ              0001425320011102241014300CT0000017640EP002483032261000685710228196667226470110100420090  000000000021880201102240842828             CQ              0001425320011102241014300CT0000017640EP002483032261000685710228196667226470110100420090  000000000021880201102240842828             CQ              0001425320011102241014300CT0000017640EP002483032261000685710228196667226470110100420090  000000000021880201102240842828             CQ              0001425320011102241014300CT0000017640EP002483032261000685710228196667226470110100420090  000000000021880201102240842828             CQ              0001425320011102241014300CT0000017640EP002483032261000685710228196667226470110100420090  000000000021880201102240842828             CQ              0001425320011102241014300CT0000017640EP002483032261000685710228196667226470110100420090  000000000021880201102240842828             CQ              0001425320011102241014300CT0000017640EP002483032261000685710228196667226470110100420090  000000000021880201102240842828             CQ              0001425320011102241014300CT0000017640EP002483032261000685710228196667226470110100420090  000000000021880201102240842828             CQ              0001425320011102241014300CT0000017640EP002483032261000685710228196667226470110100420090  000000000021880201102240842828             CQ              ".getBytes());
			
			//byte[] recv  = client.recv(30);
			
			//long after = System.currentTimeMillis();
			
			//log.info("=============================================");
			//log.info("  RECV TIME== " + ((after-before)/1000.0) + "Sec ::" +  new String(recv));
			//log.info("=============================================");
			
			//client.Disconnect();
			

			String NanSu = "MIIFmAQQgRWIA42lMkqTj2hrH0O1HwQGY2xpZW50MAsGCSqGSIb3DQEBBQOBgQBQpg+4SuOBAbHMhWyfYV9DuBBK7kItmr65wPgcHKOK+0cwbpiHqFK1ZIqkevKlarahKdNcg21DsNx5RTfeD4CDs6u+HI8uSddvgvevEYKvrjpvJvRaYSu90Rwdbgj3dgryP4iEBMB1kXJ0VfuB08M+u78ffqKn2sEApQ2BQVfiuTCCBOkwggPRoAMCAQICBAQTH7UwDQYJKoZIhvcNAQEFBQAwTzELMAkGA1UEBhMCS1IxEjAQBgNVBAoMCVNpZ25Lb3JlYTEVMBMGA1UECwwMQWNjcmVkaXRlZENBMRUwEwYDVQQDDAxTaWduS29yZWEgQ0EwHhcNMTEwNjA3MDQyMTI4WhcNMTIwNjA3MTQ1OTU5WjBwMQswCQYDVQQGEwJLUjESMBAGA1UECgwJU2lnbktvcmVhMREwDwYDVQQLDAhwZXJzb25hbDEMMAoGA1UECwwDU0hCMSwwKgYDVQQDDCPquYDrjIDsmYQoKTAwODgwMTMyMDExMDYwNzQ4ODAwMDkyMzCBnzANBgkqhkiG9w0BAQEFAAOBjQAwgYkCgYEAq0R4bdsMXqh5ioManuBV7dCTZZ3T/7HFMJWjdnYQVJPZvwjI/FHlv2sLh/hZi5gkjrXcMCupYtijkB0zuJ+pizU/G8FTyjgg75FfiusoJ0xJARMLXAxq/hcq758tYZFEADls/ger5bu1C1Hlg4dixfjZLxpzBE1y6NXV0tqm9p8CAwEAAaOCAi4wggIqMIGPBgNVHSMEgYcwgYSAFI2qIAjwieARQbx/pI4qxEBeylY6oWikZjBkMQswCQYDVQQGEwJLUjENMAsGA1UECgwES0lTQTEuMCwGA1UECwwlS29yZWEgQ2VydGlmaWNhdGlvbiBBdXRob3JpdHkgQ2VudHJhbDEWMBQGA1UEAwwNS0lTQSBSb290Q0EgMYICJ18wHQYDVR0OBBYEFF5Em85BvAdiA5FG2d1M9sPsBWi2MA4GA1UdDwEB/wQEAwIGwDB5BgNVHSABAf8EbzBtMGsGCiqDGoyaRAUBAQUwXTAtBggrBgEFBQcCARYhaHR0cDovL3d3dy5zaWdua29yZWEuY29tL2Nwcy5odG1sMCwGCCsGAQUFBwICMCAeHsd0ACDHeMmdwRyylAAgrPXHeMd4yZ3BHMeFssiy5DBYBgNVHREEUTBPoE0GCSqDGoyaRAoBAaBAMD4MCeq5gOuMgOyZhDAxMC8GCiqDGoyaRAoBAQEwITAHBgUrDgMCGqAWBBTwQXbyUPAk4QwcprqXGKoXJxfkFjBbBgNVHR8EVDBSMFCgTqBMhkpsZGFwOi8vZGlyLnNpZ25rb3JlYS5jb206Mzg5L291PWRwM3AxNzM4OCxvdT1BY2NyZWRpdGVkQ0Esbz1TaWduS29yZWEsYz1LUjA1BggrBgEFBQcBAQQpMCcwJQYIKwYBBQUHMAGGGWh0dHA6Ly9vY3NwLnNpZ25rb3JlYS5jb20wDQYJKoZIhvcNAQEFBQADggEBAC7U+IyjPkU6wa0I616dnpV0dRmVBzZ+JdmJjJOIS6j1mMQurpV4adE9K9u4J05V0F3bYlwXznaVBLHYMZ6YPGw/RBm7vizcpmTESFIzOulb/t3/KBdwgjy70V8W0gu79FMnkgEaHq9XfcENeVGGZNO22scB7iqXOB+/8hWmT7YClFMY6YvL5xIEjF+ST33pqXfYv+hnxJTQuuWjureSWMgFv8oNTnzKde0hX857Q93utKA/vwL6pil8SQnUsd5r5w1nhY+LIDD61wV8VgCaUTaOAaxJc47s7cfQUw3QXR/lZVDrpdvgnae5Bmd9B3D3n0rao2Z+1RteAAYzIkIpViQ=";
			
			client.sendData(NanSu.getBytes());

			byte[] recv  = client.recv(30);
			
			log.info("=============================================");
			log.info("  RECV TIME== " +  new String(recv));
			log.info("=============================================");
			
			client.Disconnect();
			
			//client.setFileReader("20110119");
			
		//} catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			 e.printStackTrace();
			log.error("=============================================");
			log.error(" RECV 오류발생 ID=" + myId + ", MSG=" + e.getMessage());
			log.error("=============================================");
		}
	}


	/**
	 * @return the myId
	 */
	public int getMyId() {
		return myId;
	}

	/**
	 * @param myId the myId to set
	 */
	public void setMyId(int myId) {
		this.myId = myId;
	}
	
	
   
    	
    
}
