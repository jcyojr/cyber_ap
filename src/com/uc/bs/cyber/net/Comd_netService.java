/**
 *  �ֽý��۸� : ���̹����漼û ��ȭ
 *  ��  ��  �� : ����
 *  ��  ��  �� : �λ����� ���� �ۼ��� ����
 *  Ŭ����  ID : Comd_netService
 *  ����  �̷� :
 * ------------------------------------------------------------------------
 *  �ۼ���      	�Ҽ�  		    ����            Tag           ����
 * ------------------------------------------------------------------------
 *  ����       ��ä��(��)      2010.11.30         %01%         �����ۼ�
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
	 * @param svcOpt  :: ���� Option  "0" : ������� ����, "1" : �������
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
	 * �λ����࿡ ������ �۽�
	 * @param buffer  :: �۽� ����
	 * @param svcId   :: ���� ID
	 * @throws IOException
	 * @throws Exception
	 */
	public void BsSend (byte[] buffer , String svcId) throws IOException, Exception {
		
		BsSend(buffer, svcId, "0000");
	}
	
	/**
	 * �λ����࿡ ������ �۽�
	 * @param buffer : �۽Ź���
	 * @param svcId  : ���� ID
	 * @param msgId  : �޽��� ID
	 * @throws IOException
	 * @throws Exception
	 */
	public void BsSend (byte[] buffer , String svcId, String msgId) throws IOException, Exception {
		/*�Ϸù�ȣä��*/
		int msgSeqNo = (Integer)sqnoService.queryForBean("CODMBASE.CODM_GETSEQNO", "00");
		
		BsSend(buffer, svcId, msgId, commService.getProcId(), msgSeqNo);
	}
	
	
	/**
	 * �λ����࿡ ������ �۽� 
	 * @param buffer  :: �۽Ź��� (�ý��۰������ ����)
	 * @param svcId   :: ���� ���� ID
	 * @param msgId   :: ���� �޽��� ����
	 * @param nSeq    :: �����Ϸù�ȣ
	 * @throws IOException
	 * @throws Exception
	 */
	public void BsSend (byte[] buffer , String svcId, String msgId, int nSeq) throws IOException, Exception {
		
		this.BsSend(buffer, svcId, msgId, commService.getProcId(), nSeq);
		
	}	
	
	
	/**
	 * �λ����࿡ ������ �۽� 
	 * @param buffer  :: �۽Ź��� (�ý��۰������ ����)
	 * @param svcId   :: ���� ���� ID
	 * @param msgId   :: ���� �޽��� ����
	 * @param procId  :: ������� ���μ��� ID
	 * @param nSeq    :: �����Ϸù�ȣ
	 * @throws IOException
	 * @throws Exception
	 */
	public void BsSend (byte[] buffer , String svcId, String msgId, String procId, int nSeq) throws IOException, Exception {
		
		
		byte[] sendBuf = bsHeader.getBuffer(svcId, msgId, Integer.toString(nSeq), procId, buffer);
		
		log.debug("SEND MESSAGE=(" + new String(sendBuf) + ")");
				
		commService.Send(bsSendProc, sendBuf);
		
	}	
	

	/**
	 * �Ϲݵ����� �۽�
	 * @param buffer : ���� Buffer
	 * @param svcId  : ���� ID
	 * @param msgId  : �޽��� ID
	 * @param procId : �۽� ���μ��� ID
	 * @throws IOException
	 * @throws Exception
	 */
	public void Send (byte[] buffer , String svcId, String procId) throws IOException, Exception {
		
		// byte[] sendBuf = jbHeader.getBuffer(svcId, msgId, Integer.toString(msgSeqNo), commService.getProcId(), buffer);
		log.debug("SEND MESSAGE=(" + new String(buffer) + ")");		
		
		commService.Send(procId, svcId, buffer);
		
	}
	
	
	/**
	 * ������ ����
	 * @param timeOut : ���Ž� ���ð� 
	 * @return
	 * @throws Exception
	 */
	public byte[] BsRecv(int timeOut) throws Exception {
		
		byte[] buff = commService.Recv(timeOut);
		
		bsHeadMap = bsHeader.parseBuffer(buff);
				
		log.debug("JBBK HEAD=" + bsHeadMap);
		
		if(!bsHeadMap.getMap("RESP_CD").equals("000000")) { // �������� ���Ž� Exception!!
			
			throw new Exception("��������:: (" + bsHeadMap.getMap("RESP_CD") +  ")" + bsHeadMap.getMap("RESP_MSG"));
			
		}
		
		recvBuff  = new byte[buff.length - bsHeader.getLen()];
		System.arraycopy(buff, bsHeader.getLen(), recvBuff, 0, recvBuff.length);
		
		return recvBuff;
	}
	
	
	/**
	 * ������ ����
	 * @param timeOut : ���Ž� ���ð� 
	 * @return
	 * @throws Exception
	 */
	public byte[] Recv(int timeOut) throws Exception {
		
		recvBuff = commService.Recv(timeOut);
				
		return recvBuff;
	}

	
	/**
	 * ���Ͽ��� ����
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
