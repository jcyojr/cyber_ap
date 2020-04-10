/**
 *  �ֽý��۸� : ���̹����漼û ��ȭ
 *  ��  ��  �� : ����
 *  ��  ��  �� : ���ೳ��(���ϼۼ���)
 *               com.uc.bs.cyber.etax.* �ڿ����
 *               
 *               ���������� ���� ���ೳ�ΰ��� ������ �������� �ۼ����Ͽ� 
 *               �����ϰ� ����� ó���Ѵ�.
 *               
 *  Ŭ����  ID : Txdm2550 
 *  ����  �̷� :
 * ------------------------------------------------------------------------
 *  �ۼ���      	�Ҽ�  		    ����            Tag           ����
 * ------------------------------------------------------------------------
 *  �۵���      ��ä��(��)         2011.07.07         %01%         �����ۼ�
 */
package com.uc.bs.cyber.daemon.txdm2550;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.dao.DuplicateKeyException;

import com.uc.core.MapForm;
import com.uc.core.misc.Utils;
import com.uc.core.misc.XMLFileReader;
import com.uc.core.misc.XmlUtils;
import com.uc.core.spring.service.IbatisBaseService;
import com.uc.core.spring.service.UcContextHolder;

import java.nio.channels.SocketChannel;
import java.util.ArrayList;

//���ϰ��� : ���� �ƴ�...
import com.uc.bs.cyber.CbUtil;
import com.uc.bs.cyber.etax.net.RcvWorker;
import com.uc.bs.cyber.etax.net.RcvServer;

//���� ����°���
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.RandomAccessFile;

public class Txdm2550 extends RcvWorker{
	
	private IbatisBaseService sqlService_cyber = null;
	private ApplicationContext appContext = null;
	private Thread comdThread = null;
	
	private Txdm2550FieldList KFL_2550 = null;
	
	private MapForm recvMap    = null;
	private MapForm sendMap    = new MapForm();
	
	@SuppressWarnings("unused")
	private String dataSource  = null;
	@SuppressWarnings("unused")
	private byte[] recvBuffer;
	
	private String outFilePath  = "";
	private String backFilePath = "";
	private String outFileName  = "";
	
	
	/* ������ 
	 * QUEUE ��
	 * */
	public Txdm2550() throws Exception {
		super();
		// TODO Auto-generated constructor stub

		log.info("============================================");
		log.info("== new RcvWorker( )                 Start ==");
		log.info("============================================");

	}
	
	/* ������ 
	 * �ʱ�ȭ��
	 * */
	public Txdm2550(int annotation) throws Exception {
		// TODO Auto-generated constructor stub
        // nothing...
		
	}	
	
	/*
	 * Ʈ����ǿ�(������)
	 * */
	public Txdm2550(ApplicationContext context, String dataSource) throws Exception {
		
		/*Context ����...*/
		this.context = context;
		this.dataSource = dataSource;
		
	}
			
	/*������*/
	public Txdm2550(ApplicationContext context) throws Exception {
		
		/*Context ����...*/
		setAppContext(context);
		
		sqlService_cyber = (IbatisBaseService) appContext.getBean("baseService_cyber");
			
		startServer();
		
	}

	/*=====================================================*/
	/*������ �����Ͻÿ�....................................*/
	/*=====================================================*/
	/* RcvWorker ���� ���ŵ� ����, ���ŵ����͸� ó���Ѵ�...
	 * �߻�޼��� ���� */
	@Override
	public void doDataRecv(SocketChannel socket, byte[] buffer)	throws Exception {
		// TODO Auto-generated method stub
		
		log.info("���ೳ�� ���ϼ���...");
		log.info("============================================");
		log.info("== doDataRecv(" + socket.socket().getInetAddress() + ") Start ==");
		log.info(" RECV[" + new String(buffer) + "]");
		log.info("============================================");
		
		this.isResponse = true;
		
		outFilePath  = "/app/data/cyber_ap/recv/";
		backFilePath = "/app/data/cyber_ap/back/";

		String jummunP = "/app/data/cyber_ap/rawdata/";
		String jummunF = jummunP + CbUtil.getCurrentTime12() + CbUtil.lPadString(String.valueOf(SeqNumber()), 7, '0') + ".dat";
		
		MapForm cmMap;
		MapForm binMap;
		MapForm vfyMap;

		String MsgHead = "1052F   ";
		
		byte[] bres_msg = new byte[1056];
		
		recvBuffer = null;

		try {
			
			/*debugging*/
			makeFile(buffer, jummunF);
			//������ ������ ��� ���Ϸ� ����...
			
			/*
			 * ����ó��...
			 * */
			recvBuffer =  buffer;
						
			/* ���������м� : �ʱ�ȭ */
			KFL_2550 = new Txdm2550FieldList();
			
			cmMap = KFL_2550.parseBuff(buffer);         /*���� �� 4byte�� �Ľ�...*/
			
			log.debug("cmMap = " + cmMap.getMaps());
			
			/* �м��� ������ ���� ���α׷��� �б��Ѵ�. */
			if(cmMap.getMap("BLK_TYPE").equals("I")) {        // INFO �޼���

				log.debug("==========================================================");
				log.debug("=========��������=========[" + cmMap.getMap("BLK_TYPE") + "]");
				log.debug("==========================================================");
				
				KFL_2550.Msg_fileinfo_FieldList();
				recvMap = KFL_2550.parseBuff(buffer); 
				
				/*MD5 �����ϱ� ���ؼ�...*/
				KFL_2550.Msg_Pub_FieldList();
				vfyMap = KFL_2550.parseBuff(buffer); 
				
				byte[] byt_md5 = new byte[1032];

				System.arraycopy(buffer, 8, byt_md5, 0, 1032);
				
				//DEBUGGING................................................................
				// ������ ������ 1032 bytes �� Logging...
				//CbUtil.dumpAsHex(log, byt_md5, "���� md5_da", 1032);
				
				// ������ ������ MD5 �� encoding �� �κи�...
				//CbUtil.dumpAsHex(log, (byte[])vfyMap.getMap("MSG_MD5"), "���� md5", 16);
				
				// ������ 1032������ ������ ���⼭ encoding...
				//CbUtil.dumpAsHex(log, CbUtil.Md5Sig(byt_md5), "���� md5", 16);
				
				byte[] fileinfo = new byte[4];
				System.arraycopy(buffer, 12, fileinfo, 0, fileinfo.length);
				                      
				//���������� ���� ���ϻ����� �� Offset ����
				recvMap.setMap("c_file_size", CbUtil.transLength(fileinfo, 4));
				recvMap.setMap("c_offset", 0L);
				
				log.debug("c_file_size = " + recvMap.getMap("c_file_size"));
				
				/*MD5 ���� ���� üũ*/
				if (!CbUtil.bCompareTo(CbUtil.Md5Sig(byt_md5), (byte[])vfyMap.getMap("MSG_MD5"))) {
				    
					/*�������� ����*/
					KFL_2550.Msg_Can_FieldList();
					  
					sendMap = KFL_2550.parseBuff(buffer); 
					
					sendMap.setMap("BLK_TYPE"       , "C" );
					sendMap.setMap("BLK_FLAG_GB"    , "E" );
					sendMap.setMap("BLK_CAN_CD"     , "11");
					sendMap.setMap("BLK_CAN_MSG_DAT", "INVALID MD5 SIGNATURE");
					
					byte[] blk_type        = sendMap.getMap("BLK_TYPE").toString().getBytes();
					byte[] blk_flag_gb     = sendMap.getMap("BLK_FLAG_GB").toString().getBytes();
					byte[] blk_can_cd      = sendMap.getMap("BLK_CAN_CD").toString().getBytes();
					byte[] blk_can_msg_len = CbUtil.setOffset(sendMap.getMap("BLK_CAN_MSG_LEN").toString().length());
					byte[] blk_can_msg_dat = CbUtil.rPadByte(sendMap.getMap("BLK_CAN_MSG_DAT").toString().getBytes(), 1024);
					
					byte[] bMd5 = new byte[1032];
					
					System.arraycopy(blk_type,        0, bMd5, 0, blk_type.length);
					System.arraycopy(blk_flag_gb,     0, bMd5, blk_type.length, blk_flag_gb.length);
					System.arraycopy(blk_can_cd,      0, bMd5, blk_type.length + blk_flag_gb.length, blk_can_cd.length);
					System.arraycopy(blk_can_msg_len, 0, bMd5, blk_type.length + blk_flag_gb.length + blk_can_cd.length, blk_can_msg_len.length);
					System.arraycopy(blk_can_msg_dat, 0, bMd5, blk_type.length + blk_flag_gb.length + blk_can_cd.length + blk_can_msg_len.length, blk_can_msg_dat.length);
					
					byte[] bMd5_Incode = new byte[16];
					System.arraycopy(CbUtil.Md5Sig(bMd5), 0, bMd5_Incode,0, 16);
					
					sendMap.setMap("MSG_MD5", bMd5_Incode);
					
					System.arraycopy(MsgHead.getBytes(),    0, bres_msg, 0, MsgHead.length());
					System.arraycopy(bMd5,                  0, bres_msg, MsgHead.length(), bMd5.length);
					System.arraycopy(bMd5_Incode       ,    0, bres_msg, MsgHead.length() + bMd5.length, 16);

				} else {
					
					/*���������� Ȯ���ϰ� ������ ������Ų�� (�� �����⸸ �����...)*/
					outFileName = outFilePath + recvMap.getMap("file_name").toString();
					String bakFileName = backFilePath + recvMap.getMap("file_name").toString();

					File outFile = new File(outFileName);
					File bakFile = new File(bakFileName);
					
					//���������� �����ϸ�...
					if (outFile.exists()) {
						
						if(recvMap.getMap("append_yn").equals("a")) {
		                    //File ����� Append
						
							recvMap.setMap("c_offset"  , outFile.length());
							
						} else if (recvMap.getMap("append_yn").equals("u")) {
						    //File ����� Update

							if(!outFile.exists()){
								outFile.createNewFile();
							} else {
								outFile.delete();
								outFile.createNewFile();
							}
							if(!bakFile.exists()) {
								bakFile.createNewFile();
							} else {
								bakFile.delete();
								bakFile.createNewFile();
							}
							
						}

					} else {
						//���� ����...
						outFile.createNewFile();
						
						bakFile.createNewFile();
					}

					/*�����̸� POS�� ������ ���ϼ۽��� ��ٸ�...*/
					KFL_2550.Msg_Pos_FieldList();
					sendMap = KFL_2550.parseBuff(buffer); 
					
					sendMap.setMap("BLK_TYPE",   "P" );
					
					byte[] blk_type        = sendMap.getMap("BLK_TYPE").toString().getBytes();
					byte[] blk_offset      = new byte[4];  
					
					System.arraycopy(CbUtil.setOffset((Long)recvMap.getMap("c_offset")), 0, blk_offset, 0, blk_offset.length);
					
					byte[] bMd5 = new byte[1032];
					
					System.arraycopy(blk_type,              0, bMd5, 0, blk_type.length);
					System.arraycopy(CbUtil.nullByte(4),    0, bMd5, blk_type.length, 3);
					System.arraycopy(blk_offset,            0, bMd5, blk_type.length + 3, blk_offset.length);
					System.arraycopy(CbUtil.nullByte(1024), 0, bMd5, blk_type.length + 3 + blk_offset.length, 1024);

					byte[] bMd5_Incode = new byte[16];
					System.arraycopy(CbUtil.Md5Sig(bMd5), 0, bMd5_Incode,0, 16);
					
					sendMap.setMap("MSG_MD5", bMd5_Incode);
					
					System.arraycopy(MsgHead.getBytes(),    0, bres_msg, 0, MsgHead.length());
					System.arraycopy(bMd5,                  0, bres_msg, MsgHead.length(), bMd5.length);
					System.arraycopy(bMd5_Incode        ,   0, bres_msg, MsgHead.length() + bMd5.length, 16);
					
				}
				
			} else if(cmMap.getMap("BLK_TYPE").equals("A")) { // ACK �޼���
				
				log.info("�������� = " + cmMap.getMap("BLK_TYPE") + " : ACK");
				this.isResponse = false;
				
			} else if(cmMap.getMap("BLK_TYPE").equals("N")) { // NAK �޼���
				
				log.info("�������� = " + cmMap.getMap("BLK_TYPE") + " : NAK");
				this.isResponse = false;
				
			} else if(cmMap.getMap("BLK_TYPE").equals("C")) { // CAN �޼���

				log.info("�������� = " + cmMap.getMap("BLK_TYPE") + " : CAN");
				this.isResponse = false;
				
			} else if(cmMap.getMap("BLK_TYPE").equals("P")) { // POS �޼���
				
				log.info("�������� = " + cmMap.getMap("BLK_TYPE") + " : POS");
				this.isResponse = false;
				
				/*���ż��������� �ʿ����
				 * ��) �۽ż����� P_DATA ������ Flag Byte1 'E' �� ��� �������� �� ��������
				 * ���� DATA�� ���� P_POS Block �� �̿��� �۽������� �����Ѵ�.
				 * */
				
			} else if(cmMap.getMap("BLK_TYPE").equals("D")) { // DATA �޼���
				
				log.debug("==========================================================");
				log.debug("=========��������=========[" + cmMap.getMap("BLK_TYPE") + "]");
				log.debug("==========================================================");
				
				/*���ŵ� �������� ���ϵ����Ͱ� ���ŵǹǷ� ���������� �������� ������ ������Ų��.*/
				
				KFL_2550.Msg_Data_FieldList();
				binMap = KFL_2550.parseBuff(buffer); 
				
				/* Ȯ���ϰ� �Ѿ�� �� ����
				 * data field �� �������� �� offset �� ���̸� �ٽ� �����ؾ� �ϴ��� ... 
				 * */
				int filesize = ((Long)recvMap.getMap("c_file_size")).intValue();
				int length   = ((Long)CbUtil.transLength((byte[])binMap.getMap("BLK_RD_LEN"), 2)).intValue();
				int offset   = ((Long)CbUtil.transLength((byte[])binMap.getMap("BLK_OFFSET"), 4)).intValue();
				
				log.debug("filesize(�������������ϱ���)      = " + filesize);
				log.debug("length(�����ͼ��� ���������ͱ���) = " + length);
				log.debug("offset(�����ͼ��� ���ϳ�  Offset) = " + offset);
				
				/* ������ ���ŵ� ��� ó��...*/
				outFileName = outFilePath + recvMap.getMap("file_name").toString();
				String bakFileName = backFilePath + recvMap.getMap("file_name").toString();

				// ����������...
				byte[] binFile = new byte[length];
				System.arraycopy(buffer, 16, binFile, 0, length);

				log.debug("�������� ������ = [" + new String(binFile) + "]");
				
				makeFile(binFile, outFileName);  //�������� APPEND ����
				makeFile(binFile, bakFileName);  //������� APPEND ����
				
				/*�����̸� ���������� ����*/
				/*�����̸� POS�� ������ ���ϼ۽��� ��ٸ�...*/
				
				KFL_2550.Msg_Pos_FieldList();
				sendMap = KFL_2550.parseBuff(buffer); 
				
				File outFile = new File(outFileName);
				
				if(binMap.getMap("BLK_FLAG_GB").equals("G")) {
					//NON-Stop 
					if(!outFile.exists()) {
						sendMap.setMap("BLK_OFFSET"     ,  CbUtil.setOffset(0L));
					} else {
						sendMap.setMap("BLK_OFFSET"     ,  CbUtil.setOffset(outFile.length()));
					}

				} else if (binMap.getMap("BLK_FLAG_GB").equals("E")) {
					//����  Packet ���� �� ���Ű�� ����䱸
					sendMap.setMap("BLK_OFFSET"     ,  CbUtil.setOffset(outFile.length()));
				}
				
				sendMap.setMap("BLK_TYPE"       , "P" );
				sendMap.setMap("BLK_FLAG"       , " " );
				sendMap.setMap("BLK_DATA"       , " " );
				
				byte[] blk_type        = sendMap.getMap("BLK_TYPE").toString().getBytes();
				byte[] blk_offset      = (byte[])sendMap.getMap("BLK_OFFSET");
				
				byte[] bMd5 = new byte[1032];
				
				System.arraycopy(blk_type,              0, bMd5, 0, blk_type.length);
				System.arraycopy(CbUtil.nullByte(3),    0, bMd5, blk_type.length, 3);
				System.arraycopy(blk_offset,            0, bMd5, blk_type.length + 3, 4);
				System.arraycopy(CbUtil.nullByte(1024), 0, bMd5, blk_type.length + 3 + 4, 1024);
			
				//1032 bytes MD5 encoding...
				byte[] bMd5_Incode = new byte[16];
				System.arraycopy(CbUtil.Md5Sig(bMd5), 0, bMd5_Incode,0, 16);
				
				sendMap.setMap("MSG_MD5", bMd5_Incode);
				
				//1056 bytes ��ü�۽� ��������
				System.arraycopy(MsgHead.getBytes(),    0, bres_msg, 0, MsgHead.length());
				System.arraycopy(bMd5,                  0, bres_msg, MsgHead.length(), bMd5.length);
				System.arraycopy(bMd5_Incode       ,    0, bres_msg, MsgHead.length() + bMd5.length, 16);

			} else if(cmMap.getMap("BLK_TYPE").equals("X")) { // EOF �޼���
				//ACK ���������� �����Ͽ� �۽��Ѵ�.
				
				log.debug("==========================================================");
				log.debug("=========��������=========[" + cmMap.getMap("BLK_TYPE") + "]");
				log.debug("==========================================================");
				
				KFL_2550.Msg_Ack_FieldList();
				sendMap = KFL_2550.parseBuff(buffer); 
				
				sendMap.setMap("BLK_TYPE"       , "A" );
				sendMap.setMap("BLK_FLAG1"      , " " );
				sendMap.setMap("BLK_FLAG2"      , "X" );
				sendMap.setMap("BLK_FLAG3"      , " " );
				sendMap.setMap("BLK_OFFSET"     ,  0  );
				sendMap.setMap("BLK_DATA"       , " " );
				
				byte[] blk_type        = sendMap.getMap("BLK_TYPE").toString().getBytes();
				byte[] blk_flag2       = sendMap.getMap("BLK_FLAG2").toString().getBytes();
				
				byte[] bMd5 = new byte[1032];
				
				System.arraycopy(blk_type,              0, bMd5, 0, blk_type.length);
				System.arraycopy(CbUtil.nullByte(1),    0, bMd5, blk_type.length, 1);
				System.arraycopy(blk_flag2,             0, bMd5, blk_type.length + 1, 1);
				System.arraycopy(CbUtil.nullByte(1),    0, bMd5, blk_type.length + 1 + 1, 1);
				System.arraycopy(CbUtil.nullByte(4),    0, bMd5, blk_type.length + 1 + 1 + 1, 4);
				System.arraycopy(CbUtil.nullByte(1024), 0, bMd5, blk_type.length + 1 + 1 + 1 + 4, 1024);
				
				byte[] bMd5_Incode = new byte[16];
				System.arraycopy(CbUtil.Md5Sig(bMd5), 0, bMd5_Incode,0, 16);
				
				sendMap.setMap("MSG_MD5", bMd5_Incode);
				
				System.arraycopy(MsgHead.getBytes(),    0, bres_msg, 0, MsgHead.length());
				System.arraycopy(bMd5,                  0, bres_msg, MsgHead.length(), bMd5.length);
				System.arraycopy(bMd5_Incode        ,   0, bres_msg, MsgHead.length() + bMd5.length, 16);
					
				
				/* ���Ⱑ ���ϼ����� ���ΰ�...�𸣰ڴ�...
				 * ���⼭ ���� ������ ���̶�� ������ �о DB�� �����Ű�°� ���� ������?
				 * */				
				this.isResponse = false;  /*���� ������ ������ �ʴ´�...�ֳĸ� �ְ� ���������...........���ۼ�*/
				
				Jummun_insert(outFileName);
				
			}
			
			/*���⼭ ���...
			 *--�����������п� ���� ���ϰ��� �߰���ϱ� �ٶ���...
			 *--�۸۸� ������ �ִ�. 
			 * */
			
			this.retBuffer = bres_msg;	
			
			if(this.isResponse){
				CbUtil.dumpAsHex(log, bres_msg, "����۽�����", 1056);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	/**
	 * ���ŵ� ������ �о DB�� �Է��Ѵ�.
	 * @param recvFNM
	 */
	public void Jummun_insert(String recvFNM){
		
		if(log.isInfoEnabled()) {
			log.info("=======================================================");
			log.info("=============  ���� ���� DB �Է� ����  ================");
			log.info("=======================================================");
		}
		
		try {

			log.info("�������ϸ� = " + recvFNM);
			
			outFilePath  = "/app/data/cyber_ap/recv/";
			backFilePath = "/app/data/cyber_ap/back/";
			
			/*�������� ��������...*/
			ArrayList<MapForm> alFileInfo = new ArrayList<MapForm>();
			
			MapForm mf = new MapForm();
			
			File outFile = new File(recvFNM);
			
			mf.setMap("d_cnt", 1);
			
			//�����ϱ���
			//String JobDate = CbUtil.getAddDate(-1);
			String JobDate = (String)sqlService_cyber.queryForBean("TXBT2550.GET_GYMD_B", mf);
			
			log.info("============================================");
			log.info("CUR_DATE = " + CbUtil.getCurrentDate() + "] JOB_DATE = [" + JobDate + "]");
			log.info("============================================");
			
			mf = new MapForm();		
            
			if(outFile.exists()) {
				
				alFileInfo.add(mf);
	            mf.setMap("FileName", outFile.getName());
				mf.setMap("proc_ymd", outFile.getName().substring(15));
				mf.setMap("FormName", outFile.getName().substring(0, 6));
				mf.setMap("AbsolutePath", outFile.getAbsolutePath());
			
			}

			log.debug("============================================ ");
			log.debug("Curent File CNT = " + alFileInfo.size());
			log.debug("============================================ ");
			
			if(alFileInfo.size() > 0 ) {
				
				for ( int i = 0;  i < alFileInfo.size();  i++)   {
					
					MapForm mfFileInfo =  alFileInfo.get(i);
					
					log.info("FormName = [" + mfFileInfo.getMap("FormName") + "] FileName = [" + mfFileInfo.getMap("FileName") + "]");
					
					if(mfFileInfo.getMap("FormName").equals("GR6675")) {  /*���ϼ������ೳ������*/
					
						/*================================================================*/
						/*���ϼ������ೳ������*/
						/*================================================================*/
						
						log.debug("========================================== ");
						log.debug("���ϼ������ೳ������ ");
						log.debug("========================================== ");
						
						ArrayList<MapForm> alGR6676 = setFileReader(mfFileInfo);
						
						if (alGR6676.size() > 0) {

							for(int j =0 ; j < alGR6676.size() ; j++) {
								
								MapForm mpGR6676 = alGR6676.get(j);
								
								mpGR6676.setMap("PROC_YMD"    , mf.getMap("proc_ymd"));
								mpGR6676.setMap("TAX_GB"      , "S");
								mpGR6676.setMap("BIZ_GB"      , mpGR6676.getMap("BUSINESSGUBUN"));
								mpGR6676.setMap("DAT_GB"      , mpGR6676.getMap("DATAGUBUN"));
								mpGR6676.setMap("SEQNO"       , mpGR6676.getMap("SEQNO"));
								
								if(j == 0) { /*head �ο� trailer �� ����*/

									mpGR6676.setMap("TOTALCNT"       , mpGR6676.getMap("TOTALCOUNT"));
									mpGR6676.setMap("TOTALAMT"       , 0);
									mpGR6676.setMap("NAPDT"          , mpGR6676.getMap("TRANSDATE"));
									
								} else if(j == (alGR6676.size() - 1))	{
									
									mpGR6676.setMap("TOTALCNT"       , mpGR6676.getMap("TOTALCOUNT"));
									mpGR6676.setMap("TOTALAMT"       , mpGR6676.getMap("TOTALPAYMONEY"));
									mpGR6676.setMap("NAPDT"          , mpGR6676.getMap("TRANSDATE"));
									
								} else {
									
									/*���ೳ�� ���� �Է�*/
									
									mpGR6676.setMap("GIRONO"      , mpGR6676.getMap("JIRONO"));
									mpGR6676.setMap("REG_NO"      , "");
									mpGR6676.setMap("TAX_NO"      , "");
									mpGR6676.setMap("KMGO_CD"     , mpGR6676.getMap("KUMGOCD"));
					                mpGR6676.setMap("SU_BRC"      , mpGR6676.getMap("SUNAPJUMCODE"));
					                mpGR6676.setMap("OCR_BD"      , mpGR6676.getMap("OCR_BAND"));
					                mpGR6676.setMap("PROC_GB"     , mpGR6676.getMap("PROCGUBUN"));
					                mpGR6676.setMap("FEE_AMT"     , mpGR6676.getMap("SUSU_AMT"));
					                mpGR6676.setMap("CUST_NO"     , mpGR6676.getMap("CUSTNO"));
					                mpGR6676.setMap("DANG_MON"    , mpGR6676.getMap("DANGMON"));
					                
								}
				                
				                try {
				                	
				                	if (sqlService_cyber.queryForUpdate("TXBT2550.INSERT_RES_RS1101_TB", mpGR6676) > 0) {
					                	log.info("���ϼ��� ���ೳ������ DB���� �Ϸ�!");
					                }
				                	
				                }catch (Exception e){
				                	
				                	/*�ߺ��� �߻��ϰų� �������ǿ� ��߳��� ���üũ*/
									if (e instanceof DuplicateKeyException){
										
										if (sqlService_cyber.queryForUpdate("TXBT2550.UPDATE_RES_RS1101_TB", mpGR6676) > 0) {
						                	log.info("���ϼ��� ���ೳ������ DB���� �Ϸ�!");
						                }
										
									}else{
										log.error("���������� = " + mpGR6676.getMaps());
										e.printStackTrace();							
										throw (RuntimeException) e;
									}
									
				                }
								
							}
						
						}
						
					} else if(mfFileInfo.getMap("FormName").equals("GR6694")) {  /*���ܼ��Կ��ೳ������*/
						
						/*================================================================*/
						/*���ܼ��Կ��ೳ������*/
						/*================================================================*/
						
						log.debug("========================================== ");
						log.debug("���ܼ��Կ��ೳ������ ");
						log.debug("========================================== ");
						
						ArrayList<MapForm> alGR6694 = setFileReader(mfFileInfo);
						
						if (alGR6694.size() > 0) {
							
							
							for(int j =0 ; j < alGR6694.size() ; j++) {
								
								MapForm mpGR6694 = alGR6694.get(j);
								
								mpGR6694.setMap("PROC_YMD"    , mf.getMap("proc_ymd"));
								mpGR6694.setMap("TAX_GB"      , "O");
								mpGR6694.setMap("BIZ_GB"      , mpGR6694.getMap("BUSINESSGUBUN"));
								mpGR6694.setMap("DAT_GB"      , mpGR6694.getMap("DATAGUBUN"));
								mpGR6694.setMap("SEQNO"       , mpGR6694.getMap("SEQNO"));
								
								if(j == 0) { /*head �ο� trailer �� ����*/

									mpGR6694.setMap("TOTALCNT"       , mpGR6694.getMap("TOTALCOUNT"));
									mpGR6694.setMap("TOTALAMT"       , 0);
									mpGR6694.setMap("NAPDT"          , mpGR6694.getMap("TRANSDATE"));
									
								} else if(j == (alGR6694.size() - 1))	{
									
									mpGR6694.setMap("TOTALCNT"       , mpGR6694.getMap("TOTALCOUNT"));
									mpGR6694.setMap("TOTALAMT"       , mpGR6694.getMap("TOTALPAYMONEY"));
									mpGR6694.setMap("NAPDT"          , mpGR6694.getMap("TRANSDATE"));
									
								} else {
									
									/*���ೳ�� ���� �Է�*/
					                mpGR6694.setMap("GIRONO"      , mpGR6694.getMap("JIRONO"));
					                mpGR6694.setMap("REG_NO"      , mpGR6694.getMap("REG_NO"));
					                mpGR6694.setMap("TAX_NO"      , mpGR6694.getMap("EPAY_NO"));
					                mpGR6694.setMap("KMGO_CD"     , mpGR6694.getMap("KUMGOCD"));
					                mpGR6694.setMap("SU_BRC"      , mpGR6694.getMap("SUNAPJUMCODE"));
					                mpGR6694.setMap("OCR_BD"      , mpGR6694.getMap("OCR_BAND"));
					                mpGR6694.setMap("PROC_GB"     , mpGR6694.getMap("PROCGUBUN"));
					                mpGR6694.setMap("FEE_AMT"     , mpGR6694.getMap("SUSU_AMT"));
					                mpGR6694.setMap("CUST_NO"     , "");
					                mpGR6694.setMap("EPAY_NO"     , mpGR6694.getMap("EPAY_NO"));
					                mpGR6694.setMap("DANG_MON"    , "");
					                mpGR6694.setMap("TAX_YM"      , "");
								
								}
								
				                try {
				                	
				                	if (sqlService_cyber.queryForUpdate("TXBT2550.INSERT_RES_RS1101_TB", mpGR6694) > 0) {
					                	log.info("���ܼ��� ���ೳ������ DB���� �Ϸ�!");
					                }
				                	
				                }catch (Exception e){
				                	
				                	/*�ߺ��� �߻��ϰų� �������ǿ� ��߳��� ���üũ*/
									if (e instanceof DuplicateKeyException){
										
										if (sqlService_cyber.queryForUpdate("TXBT2550.UPDATE_RES_RS1101_TB", mpGR6694) > 0) {
						                	log.info("���ܼ��� ���ೳ������ DB���� �Ϸ�!");
						                }
										
									}else{
										log.error("���������� = " + mpGR6694.getMaps());
										e.printStackTrace();							
										throw (RuntimeException) e;
									}
									
				                }

							}

						}
						

					} else if(mfFileInfo.getMap("FormName").equals("GR6696")) {  /*ȯ�氳���δ�ݿ��ೳ������*/
						
						/*================================================================*/
						/*ȯ�氳���δ�ݿ��ೳ������*/
						/*================================================================*/
						
						log.debug("========================================== ");
						log.debug("ȯ�氳���δ�ݿ��ೳ������ ");
						log.debug("========================================== ");
						
						ArrayList<MapForm> alGR6696 = setFileReader(mfFileInfo);
						
						if (alGR6696.size() > 0) {
							
							for(int j =0 ; j < alGR6696.size() ; j++) {
								
								MapForm mpGR6696 = alGR6696.get(j);
								
								mpGR6696.setMap("PROC_YMD"    , mf.getMap("proc_ymd"));
								mpGR6696.setMap("TAX_GB"      , "H");
								mpGR6696.setMap("BIZ_GB"      , mpGR6696.getMap("BUSINESSGUBUN"));
								mpGR6696.setMap("DAT_GB"      , mpGR6696.getMap("DATAGUBUN"));
								mpGR6696.setMap("SEQNO"       , mpGR6696.getMap("SEQNO"));
								
								if(j == 0) { /*head �ο� trailer �� ����*/

									mpGR6696.setMap("TOTALCNT"       , mpGR6696.getMap("TOTALCOUNT"));
									mpGR6696.setMap("TOTALAMT"       , 0);
									mpGR6696.setMap("NAPDT"          , mpGR6696.getMap("TRANSDATE"));
									
								} else if(j == (alGR6696.size() - 1))	{
									
									mpGR6696.setMap("TOTALCNT"       , mpGR6696.getMap("TOTALCOUNT"));
									mpGR6696.setMap("TOTALAMT"       , mpGR6696.getMap("TOTALPAYMONEY"));
									mpGR6696.setMap("NAPDT"          , mpGR6696.getMap("TRANSDATE"));
									
								} else {
								
									/*���ೳ�� ���� �Է�*/
									mpGR6696.setMap("GIRONO"      , mpGR6696.getMap("JIRONO"));
									mpGR6696.setMap("REG_NO"      , mpGR6696.getMap("REG_NO"));
									mpGR6696.setMap("TAX_NO"      , mpGR6696.getMap("TAX_NO"));
									mpGR6696.setMap("KMGO_CD"     , mpGR6696.getMap("KUMGOCD"));
					                mpGR6696.setMap("SU_BRC"      , mpGR6696.getMap("SUNAPJUMCODE"));
					                mpGR6696.setMap("OCR_BD"      , mpGR6696.getMap("OCR_BAND"));
					                mpGR6696.setMap("PROC_GB"     , mpGR6696.getMap("PROCGUBUN"));
					                mpGR6696.setMap("FEE_AMT"     , mpGR6696.getMap("SUSU_AMT"));
					                mpGR6696.setMap("CUST_NO"     , "");
					                mpGR6696.setMap("EPAY_NO"     , "");
					                mpGR6696.setMap("DANG_MON"    , "");
					                mpGR6696.setMap("TAX_YM"      , "");
				                
								}
								
				                try {
				                	
				                	if (sqlService_cyber.queryForUpdate("TXBT2550.INSERT_RES_RS1101_TB", mpGR6696) > 0) {
					                	log.info("ȯ�氳���δ�� ���ೳ������ DB���� �Ϸ�!");
					                }
				                	
				                }catch (Exception e){
				                	
				                	/*�ߺ��� �߻��ϰų� �������ǿ� ��߳��� ���üũ*/
									if (e instanceof DuplicateKeyException){
										
										if (sqlService_cyber.queryForUpdate("TXBT2550.UPDATE_RES_RS1101_TB", mpGR6696) > 0) {
						                	log.info("ȯ�氳���δ�� ���ೳ������ DB���� �Ϸ�!");
						                }
										
									}else{
										log.error("���������� = " + mpGR6696.getMaps());
										e.printStackTrace();							
										throw (RuntimeException) e;
									}
									
				                }

							}

						}

					}
					
				}
				
			} else {
				log.info("[" + CbUtil.getCurDateTimes() + "] ���� ���ೳ�� ó�� ������ �����ϴ�...");
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			 e.printStackTrace();
		}
		
		if(log.isInfoEnabled()) {
			log.info("=======================================================");
			log.info("=============���������ೳ�� DB �Է� �� ================");
			log.info("=======================================================");
		}
		
	}
	

	/* Main Thread 
	 * �׽�Ʈ ����ø� ����մϴ�...
	 * */
	public static void main(String args[]) {

		ApplicationContext context  = null;
		
		try {
						
			Txdm2550 svr = new Txdm2550(0);
			
			/*Log4j ����*/
			CbUtil.setupLog4jConfig(svr, "log4j.xml");
			
			String[] xmls = XMLFileReader.getAllFilePathArray(Utils.getResourcePath(svr, "/"), "SERVICE.XML");

			String strSrc = Utils.getResourcePath(svr, "/") + "config/sqlmaps.xml";

			String strDest = Utils.getResourcePath(svr, "/") + "config/sqlmapConfig.xml";

			XmlUtils.setXmlAttributes(svr, strSrc, strDest, "sqlMapConfig", "sqlMap", "url", xmls);

			context = new ClassPathXmlApplicationContext("config/Spring-db.xml");
			
			svr.setAppContext(context);
			
			svr.sqlService_cyber = (IbatisBaseService) svr.getAppContext().getBean("baseService_cyber");
			
			svr.startServer();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	/* ���� ����...*/
	private void startServer() throws Exception {
		
		/**
		 * SERVER START
		 * port     : Listen Port
         * worker   : Worker ������(class)
         * maxQue   : �ִ� Queue ũ��
    	 * procCnt  : ���μ���(������) ����
		 */
		
		int connPort =  Utils.getResourceInt("ApplicationResource", "kftc.recv_res.port");  /*51006*/
		
		//�׻� ���Ź޴� ���̰� 1056byte�� ������ ����...
		RcvServer server = new RcvServer(connPort, "com.uc.bs.cyber.daemon.txdm2550.Txdm2550", 10, 1, "log4j.xml", 4, com.uc.core.Constants.TYPEFIELDLEN);

		server.setContext(appContext);
		server.setProcId("2550");
		server.setProcName("���������Ͽ��ೳ�μ���");
		server.setThreadName("thr_2550");
		
		server.initial();
		
		comdThread = new Thread(server);
		
		/**
		 * ������ �ۼ����� ���ؼ� ������ ������ش�
		 * (������������)�� ���ؼ� Threading...
		 */
		comdThread.setName("2550");
		
		comdThread.start();
		
	}
	
	/*----------------------------------------------*/
	/* �Ϸù�ȣ �������� �׽�Ʈ*/
	/*----------------------------------------------*/
	private int SeqNumber(){
    	return (Integer)sqlService_cyber.queryForBean("CODMBASE.CODM_GETSEQNO", "00");
    }
	
	/*--------------------------------------------------------------*/
	/*--------------------property set -----------------------------*/
	/*--------------------------------------------------------------*/
	public void setAppContext(ApplicationContext appContext) {
		 this.appContext = appContext;
	}

	public ApplicationContext getAppContext() {
		return appContext;
	}
	
	/* Ʈ������� �����ϱ� ���� �Լ�.
	 * �Լ� ����� : transactionJob() �Լ� �ڵ� ����...
	 * transactionJob �Լ� ���������� Ʈ������� �����Ѵ�.
	 * */
	@SuppressWarnings("unused")
	private void mainTransProcess(){

		try {
			
			this.context = appContext;
			
			UcContextHolder.setCustomerType("LT_etax");
						
			this.startJob();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}			
		
	}
	
	/*Ʈ����� ó��*/
	@Override
	public void transactionJob() {
		// TODO Auto-generated method stub

		try {
			
			log.info("[=========== ���� transactionJob() :: Ʈ�����ó������ �Դϴ�. ========== ]");
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	}

	@Override
	public void setContext(Object obj) {
		// TODO Auto-generated method stub
		this.appContext = (ApplicationContext) obj;
		
		sqlService_cyber = (IbatisBaseService) appContext.getBean("baseService_cyber");
	}

	/*============================================
	 * FILE ���� �� �б�
	 *============================================
	 * */


	/**
	 * ���ϻ��� ���θ� üũ�ϰ� ������ �����ϸ� Append
	 * �׷��� ������ ������ ������Ų��.
	 */
	private RandomAccessFile getCurrentFile(String mkFileName) throws Exception	{
		
		RandomAccessFile raFile = null;
		
		File file = new File(mkFileName);

		if(file.exists()) {
			if(raFile == null) {
				raFile = new RandomAccessFile(file, "rw");
			}
			raFile.seek(raFile.length());
		} else {
			if(raFile != null) {
				raFile.close();
			}
				
			raFile = new RandomAccessFile(file, "rw");
		}

		return raFile;
	
    }
	
	/**
	 * �������� ���� ���ϵ����͸� ���Ͽ� ����Ѵ�.
	 * @param msg
	 * @param mkFileName
	 */
	private synchronized void makeFile(byte[] msg, String mkFileName) 
	{
		try	{
		
			RandomAccessFile rafile = getCurrentFile(mkFileName);
			rafile.write(msg);
			rafile.close();
			
		} catch (Exception e) {
			log.error("���������� ����ϴµ� ������ �߻��߽��ϴ�");
			e.printStackTrace();
		}

	}
	
	/**
	 * ���������� ������ ������ �а� �Ľ��Ѵ�...
	 * */
	private ArrayList<MapForm> setFileReader(MapForm mf) {
		
    	String outFilePath = mf.getMap("AbsolutePath").toString();
    	
    	String recvFileNm = "";
    	
    	File readFile = null;
    	
    	int FileLen = 0;
    	
    	Txdm2550FieldList kf2550_Field = new Txdm2550FieldList();
    	
    	MapForm parseMap = new MapForm();
    	
    	ArrayList<MapForm> alRetrun = new ArrayList<MapForm>();
    	
    	try {
			
    		recvFileNm = outFilePath;
    		
    		readFile = new File(recvFileNm);
    		
    		int i = 0;
    		
    		if(readFile.exists()){
    	
    			log.debug("file_name = [" + readFile.getName() + "] file_size = [" + readFile.length() + "]");
    			
    			BufferedReader br = new BufferedReader(new FileReader(readFile));
    			
    			StringBuffer sbLine = new StringBuffer();
    			
    			if(mf.getMap("FormName").equals("GR6675")) { /*���ϼ����ΰ��*/
    				FileLen = 300;
    			} else {
    				FileLen = 230;
    			}
    			
    			//������ ���̰� 230/300 Byte �̹Ƿ� 230/300�� ©�� �о��.
    			char[] readLine = new char[FileLen];
    			
    			if(mf.getMap("FormName").equals("GR6653") || 
    			   mf.getMap("FormName").equals("GR6675") || 
    			   mf.getMap("FormName").equals("GR6694") || 
    			   mf.getMap("FormName").equals("GR6696")) { /*���漼*/
    				
    				while (true){
	    				
        				if (br.read(readLine) < 0){
        					break;
        				}
        				
        				sbLine.append(new String(readLine));
        				        				
        			    log.debug("i = ["+ i +"]" + sbLine.toString());	
        				         			    
        			    if (sbLine.substring(6, 8).equals("11")) {
        			    	
        			    	if(mf.getMap("FormName").equals("GR6653")) {
        			    		kf2550_Field.GR6653_Head_FieldList(); 
        			    	} else if(mf.getMap("FormName").equals("GR6675")) {
        			    		kf2550_Field.GR6675_Head_FieldList(); 
        			    	} else if(mf.getMap("FormName").equals("GR6694")) {
        			    		kf2550_Field.GR6694_Head_FieldList(); 
        			    	} else if(mf.getMap("FormName").equals("GR6696")) {
        			    		kf2550_Field.GR6696_Head_FieldList(); 
        			    	}

        			    }else if (sbLine.substring(6, 8).equals("22")) {
        			    	
        			    	if(mf.getMap("FormName").equals("GR6653")) {
        			    		kf2550_Field.GR6653_Data_FieldList(); 
        			    	} else if(mf.getMap("FormName").equals("GR6675")) {
        			    		kf2550_Field.GR6675_Data_FieldList(); 
        			    	} else if(mf.getMap("FormName").equals("GR6694")) {
        			    		kf2550_Field.GR6694_Data_FieldList(); 
        			    	} else if(mf.getMap("FormName").equals("GR6696")) {
        			    		kf2550_Field.GR6696_Data_FieldList(); 
        			    	}
        			    	
        			    }else if (sbLine.substring(6, 8).equals("33")) {
        			    	
        			    	if(mf.getMap("FormName").equals("GR6653")) {
        			    		kf2550_Field.GR6653_Tailer_FieldList(); 
        			    	} else if(mf.getMap("FormName").equals("GR6675")) {
        			    		kf2550_Field.GR6675_Tailer_FieldList(); 
        			    	} else if(mf.getMap("FormName").equals("GR6694")) {
        			    		kf2550_Field.GR6694_Tailer_FieldList(); 
        			    	} else if(mf.getMap("FormName").equals("GR6696")) {
        			    		kf2550_Field.GR6696_Tailer_FieldList(); 
        			    	}
        			    }
        			    	
        			    parseMap = kf2550_Field.parseBuff(sbLine.toString().getBytes());
        			    parseMap.setMap("DATAGB", sbLine.substring(6, 8)); /*���� ������ ����*/
    			    	
        			    alRetrun.add(parseMap);
        			    
    			    	log.debug("Parse = " + parseMap.getMaps());
    			    	
        			    sbLine.delete(0, FileLen);
        			    
        				i++;
        			}
    				
    			}
    			br.close();
    			
    		}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		return alRetrun;
    }	

	
}
