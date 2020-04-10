/**
 * Sample TEST
 */
package com.uc.bs.cyber.daemon.txdm2540;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/*
import com.uc.bs.cyber.service.we532001.We532001FieldList;
import com.uc.bs.cyber.service.we532002.We532002FieldList;
import com.uc.bs.cyber.service.we992001.We992001FieldList;
*/

import com.uc.core.MapForm;
import com.uc.egtob.net.ClientMessageService;
import com.uc.bs.cyber.CbUtil;


import com.uc.bs.cyber.service.kf201001.Kf201001FieldList;
import com.uc.bs.cyber.service.kf201002.Kf201002FieldList;
import com.uc.bs.cyber.service.kf202001.Kf202001FieldList;
import com.uc.bs.cyber.service.kf203001.Kf203001FieldList;

import com.uc.bs.cyber.service.kf251001.Kf251001FieldList;
import com.uc.bs.cyber.service.kf251005.Kf251005FieldList;
import com.uc.bs.cyber.service.kf251002.Kf251002FieldList;
import com.uc.bs.cyber.service.kf252001.Kf252001FieldList;
import com.uc.bs.cyber.service.kf271001.Kf271001FieldList;
import com.uc.bs.cyber.service.kf271002.Kf271002FieldList;
import com.uc.bs.cyber.service.kf272001.Kf272001FieldList;
import com.uc.bs.cyber.service.kf276001.Kf276001FieldList;

import com.uc.bs.cyber.service.bs521001.Bs521001FieldList;
import com.uc.bs.cyber.service.bs521002.Bs521002FieldList;
import com.uc.bs.cyber.service.bs523001.Bs523001FieldList;
import com.uc.bs.cyber.service.bs523002.Bs523002FieldList;
import com.uc.bs.cyber.service.bs992001.Bs992001FieldList;
import com.uc.bs.cyber.service.bs502001.Bs502001FieldList;

import com.uc.bs.cyber.service.kf201001.Kf201001Service;
import com.uc.bs.cyber.service.kf201002.Kf201002Service;
import com.uc.bs.cyber.service.kf202001.Kf202001Service;
import com.uc.bs.cyber.service.kf203001.Kf203001Service;

import com.uc.bs.cyber.service.kf251001.Kf251001Service;
import com.uc.bs.cyber.service.kf251002.Kf251002Service;
import com.uc.bs.cyber.service.kf251005.Kf251005Service;
import com.uc.bs.cyber.service.kf252001.Kf252001Service;

import com.uc.bs.cyber.service.kf271001.Kf271001Service;
import com.uc.bs.cyber.service.kf271002.Kf271002Service;
import com.uc.bs.cyber.service.kf272001.Kf272001Service;
import com.uc.bs.cyber.service.kf276001.Kf276001Service;

import com.uc.bs.cyber.service.bs521001.Bs521001Service;
import com.uc.bs.cyber.service.bs521002.Bs521002Service;
import com.uc.bs.cyber.service.bs523001.Bs523001Service;
import com.uc.bs.cyber.service.bs523002.Bs523002Service;
import com.uc.bs.cyber.service.bs502001.Bs502001Service;
import com.uc.bs.cyber.service.bs992001.Bs992001Service;

import com.uc.bs.cyber.service.bs502101.*;

/**
 * @author ������
 *
 */
public class Txdm2540CSample  extends ClientMessageService implements Runnable {

	/**
	 * 
	 * @throws IOException
	 * @throws Exception
	 */

	private int  myId = 0; 
	
	protected Log log = LogFactory.getLog(this.getClass());
	
	private Bs502101FieldList bsField = null;
	
	
	/**
	 * 
	 * @param hostAddr
	 * @param port
	 * @throws IOException
	 * @throws Exception
	 */
	public Txdm2540CSample() {
		
		CbUtil.setupLog4jConfig(this, "log4j.tomcat.xml");
		
		bsField = new Bs502101FieldList();
	}
		
	public static void main(String[] args) {
		
		int j = 0;
				
		while(true) {
			
			for(int i = 0; i<1; i++) {
				Txdm2540CSample client = new Txdm2540CSample();
				
				client.myId = j++;
				
				Thread thr = new Thread(client);
	
				thr.start();
			}
			
			
			try {
				Thread.sleep(20000);
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

			Txdm2540CSample client = new Txdm2540CSample();
			client.Connect("127.0.0.1", 9831);       /*���ý� �׽�Ʈ*/
			
			//sendDa = "01                              ����ȣ                                                                          7502021122632                   619963�λ� ���屺 ������ �����                                                                                                       1322���� �����ŵ����Ĺи��� 106�� 603ȣ                                                                                         26710saihoky@naver.com                                                                                   Y                                                                                                                                01075647522                                                                                                                     ����ȣ()0020042200702117038134,ou=WOORI,ou=personal4IB,o=yessign,c=kr                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                   ";
			sendDa = "21                              ������                                                                          1801110240414   6048136070      608823�λ� ���� ������                                                                                                                265-1                                                                                                                           26290eun0205@chol.com                                                                                    Y0516359981                                                                                                                      01093953990                                                                                                                     (��)�������з�(DAE HYUN APPAREL)0020027200504187219530,ou=DAE HYUN APPAREL,ou=WOORI,ou=corporation,o=yessign,c=kr                                                                                                                                               �������з�                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                      �������з�                                                                                                                                              ";
			//sendDa = "01                              ���ֿ�                                                                          7203292075015                   612030�λ� �ؿ�뱸 �µ�                                                                                                              SK VIEW ����Ʈ 105-2004                                                                                                         26350kjyoun72@naver.com                                                                                  N07075658866                                                                                                                     01092085787                                                                                                                     ���ֿ�-4234515,ou=HTS,ou=�츮����,ou=����,o=SignKorea,c=KR                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                              ";
			
			
			sendDa = "01                              ������                                                                          7802091830621                   614090�λ� �λ����� �ξϵ�                                                                                                            9-3 �ϵ��̶��־���Ʈ 102�� 701ȣ                                                                                                26230wsjeong78@yahoo.co.kr                                                                               Y0518026583                                                                                                                      01054906583                                                                                                                     ������(JEONG WON SUHN)00320492009050400000254,ou=PSB,ou=personal4IB,o=yessign,c=kr                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                         ";
			
			sendDa = "01                              ������                                                                          8211242120710                   604823�λ� ���ϱ� �ٴ뵿                                                                                                              944-11                                                                                                                          263801124jihyelee@gmail.com                                                                              N0519076100                                                                                                                      01096910502                                                                                                                     ����()0020044200705112522554,ou=WOORI,ou=personal4IB,o=yessign,c=kr                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                 ";
			
			long before = System.currentTimeMillis();
		
			client.sendData(sendDa.getBytes());
			
			//client.sendData(fieldList.getBuff(dataMap));
			
			//client.sendData("0001425320011102241014300CT0000017640EP002483032261000685710228196667226470110100420090  000000000021880201102240842828             CQ              000115992001IGN1102241008230230500000800EP00248284826100068502386076303092037727023010000078110224100821000000000006170S 0001425320011102241014300CT0000017640EP002483032261000685710228196667226470110100420090  000000000021880201102240842828             CQ              0001425320011102241014300CT0000017640EP002483032261000685710228196667226470110100420090  000000000021880201102240842828             CQ              0001425320011102241014300CT0000017640EP002483032261000685710228196667226470110100420090  000000000021880201102240842828             CQ              0001425320011102241014300CT0000017640EP002483032261000685710228196667226470110100420090  000000000021880201102240842828             CQ              0001425320011102241014300CT0000017640EP002483032261000685710228196667226470110100420090  000000000021880201102240842828             CQ              0001425320011102241014300CT0000017640EP002483032261000685710228196667226470110100420090  000000000021880201102240842828             CQ              0001425320011102241014300CT0000017640EP002483032261000685710228196667226470110100420090  000000000021880201102240842828             CQ              0001425320011102241014300CT0000017640EP002483032261000685710228196667226470110100420090  000000000021880201102240842828             CQ              0001425320011102241014300CT0000017640EP002483032261000685710228196667226470110100420090  000000000021880201102240842828             CQ              0001425320011102241014300CT0000017640EP002483032261000685710228196667226470110100420090  000000000021880201102240842828             CQ              0001425320011102241014300CT0000017640EP002483032261000685710228196667226470110100420090  000000000021880201102240842828             CQ              0001425320011102241014300CT0000017640EP002483032261000685710228196667226470110100420090  000000000021880201102240842828             CQ              0001425320011102241014300CT0000017640EP002483032261000685710228196667226470110100420090  000000000021880201102240842828             CQ              0001425320011102241014300CT0000017640EP002483032261000685710228196667226470110100420090  000000000021880201102240842828             CQ              0001425320011102241014300CT0000017640EP002483032261000685710228196667226470110100420090  000000000021880201102240842828             CQ              0001425320011102241014300CT0000017640EP002483032261000685710228196667226470110100420090  000000000021880201102240842828             CQ              0001425320011102241014300CT0000017640EP002483032261000685710228196667226470110100420090  000000000021880201102240842828             CQ              0001425320011102241014300CT0000017640EP002483032261000685710228196667226470110100420090  000000000021880201102240842828             CQ              0001425320011102241014300CT0000017640EP002483032261000685710228196667226470110100420090  000000000021880201102240842828             CQ              0001425320011102241014300CT0000017640EP002483032261000685710228196667226470110100420090  000000000021880201102240842828             CQ              0001425320011102241014300CT0000017640EP002483032261000685710228196667226470110100420090  000000000021880201102240842828             CQ              0001425320011102241014300CT0000017640EP002483032261000685710228196667226470110100420090  000000000021880201102240842828             CQ              0001425320011102241014300CT0000017640EP002483032261000685710228196667226470110100420090  000000000021880201102240842828             CQ              0001425320011102241014300CT0000017640EP002483032261000685710228196667226470110100420090  000000000021880201102240842828             CQ              0001425320011102241014300CT0000017640EP002483032261000685710228196667226470110100420090  000000000021880201102240842828             CQ              0001425320011102241014300CT0000017640EP002483032261000685710228196667226470110100420090  000000000021880201102240842828             CQ              0001425320011102241014300CT0000017640EP002483032261000685710228196667226470110100420090  000000000021880201102240842828             CQ              0001425320011102241014300CT0000017640EP002483032261000685710228196667226470110100420090  000000000021880201102240842828             CQ              0001425320011102241014300CT0000017640EP002483032261000685710228196667226470110100420090  000000000021880201102240842828             CQ              0001425320011102241014300CT0000017640EP002483032261000685710228196667226470110100420090  000000000021880201102240842828             CQ              0001425320011102241014300CT0000017640EP002483032261000685710228196667226470110100420090  000000000021880201102240842828             CQ              0001425320011102241014300CT0000017640EP002483032261000685710228196667226470110100420090  000000000021880201102240842828             CQ              0001425320011102241014300CT0000017640EP002483032261000685710228196667226470110100420090  000000000021880201102240842828             CQ              0001425320011102241014300CT0000017640EP002483032261000685710228196667226470110100420090  000000000021880201102240842828             CQ              0001425320011102241014300CT0000017640EP002483032261000685710228196667226470110100420090  000000000021880201102240842828             CQ              0001425320011102241014300CT0000017640EP002483032261000685710228196667226470110100420090  000000000021880201102240842828             CQ              0001425320011102241014300CT0000017640EP002483032261000685710228196667226470110100420090  000000000021880201102240842828             CQ              0001425320011102241014300CT0000017640EP002483032261000685710228196667226470110100420090  000000000021880201102240842828             CQ              0001425320011102241014300CT0000017640EP002483032261000685710228196667226470110100420090  000000000021880201102240842828             CQ              0001425320011102241014300CT0000017640EP002483032261000685710228196667226470110100420090  000000000021880201102240842828             CQ              ".getBytes());
			
			byte[] recv  = client.recv(30);
			
			long after = System.currentTimeMillis();
			
			log.info("=============================================");
			log.info("  RECV TIME== " + ((after-before)/1000.0) + "Sec ::" +  new String(recv));
			log.info("=============================================");
			
			client.Disconnect();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			 e.printStackTrace();
			log.error("=============================================");
			log.error(" RECV �����߻� ID=" + myId + ", MSG=" + e.getMessage());
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
