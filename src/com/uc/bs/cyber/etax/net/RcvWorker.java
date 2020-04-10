/**
 *  �ֽý��۸� : ���̹����漼û ��ȭ
 *  ��  ��  �� : ����
 *               Server.class �� ���ؼ� �ʿ��� Queue ��ŭ �����ȴ�.
 *               �����͸� �����ϸ� doDataRecv ȣ���Ͽ� �ۼ��ŵ����͸� ó���Ѵ�.
 *  ��  ��  �� : �۽� �� ����
 *  Ŭ����  ID : RcvWorker
 *  ����  �̷� :
 * ------------------------------------------------------------------------
 *  �ۼ���      	�Ҽ�  		    ����            Tag           ����
 * ------------------------------------------------------------------------
 *  Administrator   u c         2011.04.24         %01%         �����ۼ�
 */
package com.uc.bs.cyber.etax.net;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.uc.core.MapForm;
import com.uc.core.spring.service.TransactionJob;

import com.uc.egtob.net.UcServerSocket;

public abstract class RcvWorker extends TransactionJob implements Runnable, Cloneable  {

	protected Log log = LogFactory.getLog(this.getClass());
	protected byte[] retBuffer = null;
	protected int iq = 0;   // debugging q 
	protected boolean isResponse = true;
	
	private UcServerSocket server = null;
	
	private List<MapForm> queue = new LinkedList<MapForm>();
	
	private int qCnt = 0;	// ó���Ǽ�
	
	private int stat = 0;	// 0: �����, 1:ó����
	
	private int rCnt = 0;   // ����� Ƚ��
	
	private long procTime = System.currentTimeMillis();
	
	private int msgLen = 0;
	
	/*�߻� class ���� */
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
		try {
			
			/*
			 * 1. ������ ����
			 * 2. ����ó��(�߻�޼ҵ�)
			 * 3. ����ó��
			 * */
			while (!Thread.currentThread().isInterrupted()) {
				
				synchronized (queue) {
					while(queue.isEmpty()) {
							queue.wait();
					}
				}
				
				stat = 1;	// ó����
				
				procTime = System.currentTimeMillis();
				
				retBuffer = null;
				
				MapForm recvMap = (MapForm) queue.remove(0);
				
				if(recvMap == null) {
					log.info("============== DATA NULL(recvMap) ================");
				}
				
				SocketChannel socket = (SocketChannel) recvMap.getMap("socket");

				byte[] array        = (byte[]) recvMap.getMap("buffer");
				
				msgLen = array.length;
				
				log.info("=================================================");
				log.info("DATA�� �����߽��ϴ� FROM::[" + socket.socket().getRemoteSocketAddress() + "]");
				log.info("=================================================");
				
				qCnt++;
				
				try {
					/**
					 *  �߻�޼ҵ� ȣ��
					 *  ������ ó���Ѵ�.
					 */
					doDataRecv(socket, array);
					
					if(retBuffer == null) retBuffer = array;
					
					log.debug("=============================================================================");
					log.debug("SOCKET=" + socket + ", BUFFER=[" + new String(retBuffer) + "]");
					
					
					if(msgLen < 10) { /*ó�������� �޼����� 10 byte ���� ������ �ǽ��Ѵ�...*/
						
						retBuffer = "Warning!!! Be careful!...".getBytes();
						
						server.sendNClose(socket.socket().getRemoteSocketAddress() , retBuffer);
						
					} else {
						
						if(socket.isConnected() && isResponse) {
							// ó�� �� ������ �����Ѵ�.
							server.send(socket.socket().getRemoteSocketAddress() , retBuffer);
							
						} else {
							
							log.error("=================================================");
							log.error(" Socket is Close!!::" + socket);
							log.error("=================================================");
						}
					}

					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					if(retBuffer != null && socket.isConnected()) server.send(socket.socket().getRemoteSocketAddress() , retBuffer);
					
				} catch (SQLException se) {
					Thread.currentThread().interrupt();
					
					break;
				} catch (Exception ee){
					ee.printStackTrace();
					
					if(retBuffer != null) server.send(socket.socket().getRemoteSocketAddress() , retBuffer);

				}
				
				log.info("=================================================");
				if(isResponse) {
					log.info("RETURN==[" + new String(retBuffer) + "]");
					log.info("DATAó�� �Ϸ�" + socket.socket().getRemoteSocketAddress());
				} else {
					log.info("========���ó�� ������� ���� ����=========");
				}
				log.info("=================================================");

				stat = 0;
				
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			log.error(e.getMessage());
			
		} finally {
			log.fatal("=================================================");
			log.fatal("===== ������ ó���� ���� ����!!!!!!!!!!!!!!!!!!");
			log.fatal("=================================================");

		}

	}
	
	/**
	 * ���� ���������� �����Ѵ�..
	 * RcvServer class ���� ����
	 * @param queue the queue to set
	 */
	public void setQueue(MapForm recvMap) {
		
		synchronized(queue) {
			queue.add(recvMap);
			queue.notify();
		}
	}
	
	/**
	 * @return
	 */
	public int queueSize () {
		
		return queue.size() ;
	}
	
	/**
	 * @param server
	 */
	public void setServer(UcServerSocket server) {
		this.server = server;
		
	}
	
	/**
	 * �߻�޼��� ����
	 * @param buffer
	 */
	public abstract void doDataRecv (SocketChannel socket, byte[] buffer) throws Exception ;

	/**
	 * @return
	 */
	public List<MapForm> getQueue() {
		// TODO Auto-generated method stub
		return queue;
	}
	
	/**
	 * 
	 */
	public void removeQueue() {
		// TODO Auto-generated method stub
		this.queue = null;
	}
	
	/**
	 * 
	 * @param errno
	 * @return �������� ���� Return 
	 * Worker �������� ������ �ؼ� ����Ѵ� 
	 * ���� '0'�� Worker �� �Ҵ� �� �� ���� ����̴�
	 */
	public byte[] getErrMsg(int errno) {
		
		switch(errno) {
			case 0:
				return "Worker ���μ����� �Ҵ��� �� �����ϴ�".getBytes();
			default:
				return new String("�����޽����� SET ���� �ʾҽ��ϴ�(" + errno + ")").getBytes();
		}
		
	}
	
	/**
	 * 
	 * @return
	 */
	public int getStat()
	{
		return this.stat;
	}
	
	/**
	 * 
	 * @return
	 */
	public long getProcTime() {
		
		return this.procTime;
	}


	/**
	 * @param rCnt the rCnt to set
	 */
	public void setrCnt(int rCnt) {
		this.rCnt = rCnt;
	}


	/**
	 * @return the rCnt
	 */
	public int getrCnt() {
		return rCnt;
	}


	/**
	 * @return the qCnt
	 */
	public int getqCnt() {
		return qCnt;
	}

	/**
	 * @param qCnt the qCnt to set
	 */
	public void setqCnt(int qCnt) {
		this.qCnt = qCnt;
	}
	
	/*�߻�Ŭ���� ����*/
	public abstract void setContext(Object obj);

}
