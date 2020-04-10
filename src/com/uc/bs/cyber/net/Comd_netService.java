/**
 *  주시스템명 : 사이버지방세청 고도화
 *  업  무  명 : 공통
 *  기  능  명 : 부산은행 전문 송수신 서비스
 *  클래스  ID : Comd_netService
 *  변경  이력 :
 * ------------------------------------------------------------------------
 *  작성자      	소속  		    일자            Tag           내용
 * ------------------------------------------------------------------------
 *  김대완       유채널(주)      2010.11.30         %01%         최초작성
 *
 */

package com.uc.bs.cyber.net;

import java.io.IOException;
import java.net.UnknownHostException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;

import com.uc.core.MapForm;
import com.uc.core.spring.service.IbatisBaseService;
import com.uc.egtob.uccomm.UcCommClientService;

import com.uc.bs.cyber.CbUtil;
import com.uc.bs.cyber.field.Comd_WorkField;
import com.uc.bs.cyber.field.Comd_cmhd;

/**
 * @author Administrator
 *
 */
public class Comd_netService {
	
	protected UcCommClientService commService;
	
	private Comd_cmhd    bsHeader ;

	protected Comd_WorkField field ;
	
	private MapForm      bsHeadMap;

	private String       hostAddr ;
	private int          connPort;
	private String       bsSendProc;
	
	private byte[]       recvBuff; 
		
	private IbatisBaseService sqnoService = null;
	
	protected Log log = LogFactory.getLog(this.getClass());
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * 
	 * @param context
	 * @throws UnknownHostException
	 * @throws IOException
	 * @throws Exception
	 */
	public Comd_netService(ApplicationContext context) throws UnknownHostException, IOException, Exception {
		super();
	
		commService = new UcCommClientService();
		
		bsHeader = new Comd_cmhd();
		
		hostAddr  = CbUtil.getResource("ApplicationResource",     "comm.server.url");
		connPort  = CbUtil.getResourceInt("ApplicationResource", "comm.server.port");
		bsSendProc = CbUtil.getResource("ApplicationResource", "jbbk.send.id");

		// bsSendProc = EtUtil.getResource("ApplicationResource", "etax.worker.id");
		
		if(context != null) {
			sqnoService = (IbatisBaseService) context.getBean("baseService_cyber");
		}
		
		commService.Connect(hostAddr, connPort);
	
		field = new  Comd_WorkField();
		
	}


	/**
	 * 
	 * @param context :: Spring Context
	 * @param svcOpt  :: 서비스 Option  "0" : 응답수신 안함, "1" : 응답수신
	 * @throws UnknownHostException
	 * @throws IOException
	 * @throws Exception
	 */
	public Comd_netService(ApplicationContext context, String svcOpt) throws UnknownHostException, IOException, Exception {
		super();
	
		commService = new UcCommClientService();
		
		bsHeader = new Comd_cmhd();
		
		hostAddr  = CbUtil.getResource("ApplicationResource",     "comm.server.url");
		connPort  = CbUtil.getResourceInt("ApplicationResource", "comm.server.port");
		bsSendProc = CbUtil.getResource("ApplicationResource", "jbbk.send.id");

		// bsSendProc = EtUtil.getResource("ApplicationResource", "etax.worker.id");
		
		if(context != null) {
			sqnoService = (IbatisBaseService) context.getBean("baseService_cyber");
		}
		
		commService.Connect(hostAddr, connPort, svcOpt);
	
		field = new  Comd_WorkField();
		
	}	
	
	
	/**
	 * 부산은행에 데이터 송신
	 * @param buffer  :: 송신 버퍼
	 * @param svcId   :: 서비스 ID
	 * @throws IOException
	 * @throws Exception
	 */
	public void BsSend (byte[] buffer , String svcId) throws IOException, Exception {
		
		BsSend(buffer, svcId, "0000");
	}
	
	/**
	 * 부산은행에 데이터 송신
	 * @param buffer : 송신버퍼
	 * @param svcId  : 서비스 ID
	 * @param msgId  : 메시지 ID
	 * @throws IOException
	 * @throws Exception
	 */
	public void BsSend (byte[] buffer , String svcId, String msgId) throws IOException, Exception {
		/*일련번호채번*/
		int msgSeqNo = (Integer)sqnoService.queryForBean("CODMBASE.CODM_GETSEQNO", "00");
		
		BsSend(buffer, svcId, msgId, commService.getProcId(), msgSeqNo);
	}
	
	
	/**
	 * 부산은행에 데이터 송신 
	 * @param buffer  :: 송신버퍼 (시스템공통헤더 제외)
	 * @param svcId   :: 전문 서비스 ID
	 * @param msgId   :: 전문 메시지 종별
	 * @param nSeq    :: 전문일련번호
	 * @throws IOException
	 * @throws Exception
	 */
	public void BsSend (byte[] buffer , String svcId, String msgId, int nSeq) throws IOException, Exception {
		
		this.BsSend(buffer, svcId, msgId, commService.getProcId(), nSeq);
		
	}	
	
	
	/**
	 * 부산은행에 데이터 송신 
	 * @param buffer  :: 송신버퍼 (시스템공통헤더 제외)
	 * @param svcId   :: 전문 서비스 ID
	 * @param msgId   :: 전문 메시지 종별
	 * @param procId  :: 응답수신 프로세스 ID
	 * @param nSeq    :: 전문일련번호
	 * @throws IOException
	 * @throws Exception
	 */
	public void BsSend (byte[] buffer , String svcId, String msgId, String procId, int nSeq) throws IOException, Exception {
		
		
		byte[] sendBuf = bsHeader.getBuffer(svcId, msgId, Integer.toString(nSeq), procId, buffer);
		
		log.debug("SEND MESSAGE=(" + new String(sendBuf) + ")");
				
		commService.Send(bsSendProc, sendBuf);
		
	}	
	

	/**
	 * 일반데이터 송신
	 * @param buffer : 전송 Buffer
	 * @param svcId  : 서비스 ID
	 * @param msgId  : 메시지 ID
	 * @param procId : 송신 프로세스 ID
	 * @throws IOException
	 * @throws Exception
	 */
	public void Send (byte[] buffer , String svcId, String procId) throws IOException, Exception {
		
		// byte[] sendBuf = jbHeader.getBuffer(svcId, msgId, Integer.toString(msgSeqNo), commService.getProcId(), buffer);
		log.debug("SEND MESSAGE=(" + new String(buffer) + ")");		
		
		commService.Send(procId, svcId, buffer);
		
	}
	
	
	/**
	 * 데이터 수신
	 * @param timeOut : 수신시 대기시간 
	 * @return
	 * @throws Exception
	 */
	public byte[] BsRecv(int timeOut) throws Exception {
		
		byte[] buff = commService.Recv(timeOut);
		
		bsHeadMap = bsHeader.parseBuffer(buff);
				
		log.debug("JBBK HEAD=" + bsHeadMap);
		
		if(!bsHeadMap.getMap("RESP_CD").equals("000000")) { // 오류응답 수신시 Exception!!
			
			throw new Exception("오류응답:: (" + bsHeadMap.getMap("RESP_CD") +  ")" + bsHeadMap.getMap("RESP_MSG"));
			
		}
		
		recvBuff  = new byte[buff.length - bsHeader.getLen()];
		System.arraycopy(buff, bsHeader.getLen(), recvBuff, 0, recvBuff.length);
		
		return recvBuff;
	}
	
	
	/**
	 * 데이터 수신
	 * @param timeOut : 수신시 대기시간 
	 * @return
	 * @throws Exception
	 */
	public byte[] Recv(int timeOut) throws Exception {
		
		recvBuff = commService.Recv(timeOut);
				
		return recvBuff;
	}

	
	/**
	 * 소켓연결 종료
	 * @throws IOException 
	 */
	public void Close() throws IOException {
		commService.Disconnect();
	}
	
	/**
	 * @param jbHeadMap the jbHeadMap to set
	 */
	public void setBsHeadMap(MapForm jbHeadMap) {
		this.bsHeadMap = jbHeadMap;
	}

	/**
	 * @return the jbHeadMap
	 */
	public MapForm getBsHeadMap() {
		return bsHeadMap;
	}
	
	
}
