/**
 * Sample TEST
 */
package com.uc.bs.cyber.daemon.txdm2520;

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
 * @author 
 *
 */
public class Txdm2520CSample  extends ClientMessageService implements Runnable {

	/**
	 * 
	 * @throws IOException
	 * @throws Exception
	 */
	private int  myId = 0; 
	
	protected Log log = LogFactory.getLog(this.getClass());
	
	private Bs502101FieldList bsField = null;
	
	private int[] svcTms = new int[2];
	
	/**
	 * 
	 * @param hostAddr
	 * @param port
	 * @throws IOException
	 * @throws Exception
	 */
	public Txdm2520CSample() {
		
		CbUtil.setupLog4jConfig(this, "log4j.tomcat.xml");
		
		bsField = new Bs502101FieldList();
	}
		
	public static void main(String[] args) {
		
		int j = 0;
				
		while(true) {
			
			for(int i = 0; i<1; i++) {
				Txdm2520CSample client = new Txdm2520CSample();
				
				client.myId = j++;
				
				Thread thr = new Thread(client);
	
				thr.start();
			}
			
			
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		    break;
		}		
	}


	
	public void run() {
		// TODO Auto-generated method stub
		
		//We532001FieldList fieldList = new We532001FieldList();
		//We532002FieldList fieldList = new We532002FieldList();
		
		MapForm dataMap = new MapForm();
		
        String sendDa = "";
        
		try {

			Txdm2520CSample client = new Txdm2520CSample();
			client.Connect("127.0.0.1", 9385);
			
			sendDa = "000115992001IGN1109151606140320G00032870EP082122182261005334032021449081011086340320G0003138110915155920000000000006170SQ";
			
			client.sendData(sendDa.getBytes());
			
			byte[] recv  = client.recv(30);
						
			log.info("=============================================");
			log.info(new String(recv));
			log.info("=============================================");
			
			client.Disconnect();
			
		} catch (IOException e) {
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
