/**
 *  주시스템명 : 사이버지방세청 고도화
 *  업  무  명 : 공통
 *               Server.class 에 의해서 필요한 Queue 만큼 생성된다.
 *               데이터를 수신하면 doDataRecv 호출하여 송수신데이터를 처리한다.
 *  기  능  명 : 송신 및 수신
 *  클래스  ID : RcvWorker
 *  변경  이력 :
 * ------------------------------------------------------------------------
 *  작성자      	소속  		    일자            Tag           내용
 * ------------------------------------------------------------------------
 *  Administrator   u c         2011.04.24         %01%         최초작성
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
	
	private int qCnt = 0;	// 처리건수
	
	private int stat = 0;	// 0: 대기중, 1:처리중
	
	private int rCnt = 0;   // 재시작 횟수
	
	private long procTime = System.currentTimeMillis();
	
	private int msgLen = 0;
	
	/*추상 class 구현 */
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
		try {
			
			/*
			 * 1. 데이터 수신
			 * 2. 업무처리(추상메소드)
			 * 3. 응답처리
			 * */
			while (!Thread.currentThread().isInterrupted()) {
				
				synchronized (queue) {
					while(queue.isEmpty()) {
							queue.wait();
					}
				}
				
				stat = 1;	// 처리중
				
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
				log.info("DATA를 수신했습니다 FROM::[" + socket.socket().getRemoteSocketAddress() + "]");
				log.info("=================================================");
				
				qCnt++;
				
				try {
					/**
					 *  추상메소드 호출
					 *  업무를 처리한다.
					 */
					doDataRecv(socket, array);
					
					if(retBuffer == null) retBuffer = array;
					
					log.debug("=============================================================================");
					log.debug("SOCKET=" + socket + ", BUFFER=[" + new String(retBuffer) + "]");
					
					
					if(msgLen < 10) { /*처음수신한 메세지가 10 byte 보다 적으면 의심한다...*/
						
						retBuffer = "Warning!!! Be careful!...".getBytes();
						
						server.sendNClose(socket.socket().getRemoteSocketAddress() , retBuffer);
						
					} else {
						
						if(socket.isConnected() && isResponse) {
							// 처리 후 응답을 전송한다.
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
					log.info("DATA처리 완료" + socket.socket().getRemoteSocketAddress());
				} else {
					log.info("========결과처리 응답수신 하지 않음=========");
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
			log.fatal("===== 데이터 처리중 오류 종료!!!!!!!!!!!!!!!!!!");
			log.fatal("=================================================");

		}

	}
	
	/**
	 * 수신 소켓정보를 셋팅한다..
	 * RcvServer class 에서 셋팅
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
	 * 추상메서드 선언
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
	 * @return 오류응답 전문 Return 
	 * Worker 서버에서 재정의 해서 써야한다 
	 * 오류 '0'은 Worker 를 할당 할 수 없을 경우이다
	 */
	public byte[] getErrMsg(int errno) {
		
		switch(errno) {
			case 0:
				return "Worker 프로세스를 할당할 수 없습니다".getBytes();
			default:
				return new String("오류메시지가 SET 되지 않았습니다(" + errno + ")").getBytes();
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
	
	/*추상클래스 선언*/
	public abstract void setContext(Object obj);

}
