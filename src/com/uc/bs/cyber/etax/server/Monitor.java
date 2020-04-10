/**
 * TESTING ...
 */
package com.uc.bs.cyber.etax.server;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.uc.egtob.net.ClientMessageService;
import com.uc.bs.cyber.CbUtil;
/**
 * @author 프리비
 *
 */
public class Monitor extends ClientMessageService implements Runnable {

	/**
	 * 
	 * @throws IOException
	 * @throws Exception
	 */
	protected Log log = LogFactory.getLog(this.getClass());
	
	private String hostAddr = null;
	private int  port = 0;
	
	
	/**
	 * 
	 * @param hostAddr
	 * @param port
	 * @throws IOException
	 * @throws Exception
	 */
	public Monitor() {
		
		CbUtil.setupLog4jConfig(this, "log4j.tomcat.xml");
		log.info("============ Monitor() 생성자 시작 =============");
	}
		
	
	public static void main(String[] args) {
		
		Monitor client = new Monitor();

		if (args.length == 0) {
			client.hostAddr = "localhost";
			client.port = 9383;
		} else if (args.length == 1) {
			client.port = Integer.parseInt(args[0]);
		} else {
			client.hostAddr = args[0];
			client.port = Integer.parseInt(args[1]);
		}

		Thread thr = new Thread(client);
        thr.setName("monitor");
		thr.start();

	}


	public void run() {
		// TODO Auto-generated method stub
		
		while (true){

			try {
	
				this.Connect(this.hostAddr, this.port);
				
				//this.sendData("000007MONITORISNICEISGOOD".getBytes());
				
				this.sendData("000007MONITOR".getBytes());

				byte[] recv  = this.recv(30);
										
				log.info("recv = " + new String(recv));
				
				this.Disconnect();
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}
	
}
