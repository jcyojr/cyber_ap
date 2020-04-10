/**
 *  주시스템명 : 사이버지방세청 고도화
 *  업  무  명 : 공통
 *  기  능  명 : 예약납부(파일송수신)
 *               com.uc.bs.cyber.etax.* 자원사용
 *               
 *               결제원으로 부터 예약납부관련 파일을 전문으로 송수신하여 
 *               저장하고 결과를 처리한다.
 *               
 *  클래스  ID : Txdm2550 
 *  변경  이력 :
 * ------------------------------------------------------------------------
 *  작성자      	소속  		    일자            Tag           내용
 * ------------------------------------------------------------------------
 *  송동욱      유채널(주)         2011.07.07         %01%         최초작성
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

//소켓관련 : 공통 아님...
import com.uc.bs.cyber.CbUtil;
import com.uc.bs.cyber.etax.net.RcvWorker;
import com.uc.bs.cyber.etax.net.RcvServer;

//파일 입출력관련
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
	
	
	/* 생성자 
	 * QUEUE 용
	 * */
	public Txdm2550() throws Exception {
		super();
		// TODO Auto-generated constructor stub

		log.info("============================================");
		log.info("== new RcvWorker( )                 Start ==");
		log.info("============================================");

	}
	
	/* 생성자 
	 * 초기화용
	 * */
	public Txdm2550(int annotation) throws Exception {
		// TODO Auto-generated constructor stub
        // nothing...
		
	}	
	
	/*
	 * 트랜잭션용(사용안함)
	 * */
	public Txdm2550(ApplicationContext context, String dataSource) throws Exception {
		
		/*Context 주입...*/
		this.context = context;
		this.dataSource = dataSource;
		
	}
			
	/*생성자*/
	public Txdm2550(ApplicationContext context) throws Exception {
		
		/*Context 주입...*/
		setAppContext(context);
		
		sqlService_cyber = (IbatisBaseService) appContext.getBean("baseService_cyber");
			
		startServer();
		
	}

	/*=====================================================*/
	/*업무를 구현하시오....................................*/
	/*=====================================================*/
	/* RcvWorker 에서 수신된 소켓, 수신데이터를 처리한다...
	 * 추상메서드 구현 */
	@Override
	public void doDataRecv(SocketChannel socket, byte[] buffer)	throws Exception {
		// TODO Auto-generated method stub
		
		log.info("예약납부 파일수신...");
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
			//들어오는 전문을 모두 파일로 남김...
			
			/*
			 * 전문처리...
			 * */
			recvBuffer =  buffer;
						
			/* 수신전문분석 : 초기화 */
			KFL_2550 = new Txdm2550FieldList();
			
			cmMap = KFL_2550.parseBuff(buffer);         /*헤드와 앞 4byte만 파싱...*/
			
			log.debug("cmMap = " + cmMap.getMaps());
			
			/* 분석된 전문에 따라서 프로그램을 분기한다. */
			if(cmMap.getMap("BLK_TYPE").equals("I")) {        // INFO 메세지

				log.debug("==========================================================");
				log.debug("=========전문구분=========[" + cmMap.getMap("BLK_TYPE") + "]");
				log.debug("==========================================================");
				
				KFL_2550.Msg_fileinfo_FieldList();
				recvMap = KFL_2550.parseBuff(buffer); 
				
				/*MD5 검증하기 위해서...*/
				KFL_2550.Msg_Pub_FieldList();
				vfyMap = KFL_2550.parseBuff(buffer); 
				
				byte[] byt_md5 = new byte[1032];

				System.arraycopy(buffer, 8, byt_md5, 0, 1032);
				
				//DEBUGGING................................................................
				// 수신한 전문중 1032 bytes 만 Logging...
				//CbUtil.dumpAsHex(log, byt_md5, "수신 md5_da", 1032);
				
				// 수신한 전문중 MD5 로 encoding 된 부분만...
				//CbUtil.dumpAsHex(log, (byte[])vfyMap.getMap("MSG_MD5"), "수신 md5", 16);
				
				// 수신한 1032전문을 가지고 여기서 encoding...
				//CbUtil.dumpAsHex(log, CbUtil.Md5Sig(byt_md5), "검증 md5", 16);
				
				byte[] fileinfo = new byte[4];
				System.arraycopy(buffer, 12, fileinfo, 0, fileinfo.length);
				                      
				//수신정보로 부터 파일사이즈 및 Offset 설정
				recvMap.setMap("c_file_size", CbUtil.transLength(fileinfo, 4));
				recvMap.setMap("c_offset", 0L);
				
				log.debug("c_file_size = " + recvMap.getMap("c_file_size"));
				
				/*MD5 검증 오류 체크*/
				if (!CbUtil.bCompareTo(CbUtil.Md5Sig(byt_md5), (byte[])vfyMap.getMap("MSG_MD5"))) {
				    
					/*오류전문 셋팅*/
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
					
					/*파일정보를 확인하고 파일을 생성시킨다 (즉 껍데기만 만든다...)*/
					outFileName = outFilePath + recvMap.getMap("file_name").toString();
					String bakFileName = backFilePath + recvMap.getMap("file_name").toString();

					File outFile = new File(outFileName);
					File bakFile = new File(bakFileName);
					
					//기존파일이 존재하면...
					if (outFile.exists()) {
						
						if(recvMap.getMap("append_yn").equals("a")) {
		                    //File 존재시 Append
						
							recvMap.setMap("c_offset"  , outFile.length());
							
						} else if (recvMap.getMap("append_yn").equals("u")) {
						    //File 존재시 Update

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
						//파일 생성...
						outFile.createNewFile();
						
						bakFile.createNewFile();
					}

					/*정상이면 POS를 보내고 파일송신을 기다림...*/
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
				
			} else if(cmMap.getMap("BLK_TYPE").equals("A")) { // ACK 메세지
				
				log.info("수신정보 = " + cmMap.getMap("BLK_TYPE") + " : ACK");
				this.isResponse = false;
				
			} else if(cmMap.getMap("BLK_TYPE").equals("N")) { // NAK 메세지
				
				log.info("수신정보 = " + cmMap.getMap("BLK_TYPE") + " : NAK");
				this.isResponse = false;
				
			} else if(cmMap.getMap("BLK_TYPE").equals("C")) { // CAN 메세지

				log.info("수신정보 = " + cmMap.getMap("BLK_TYPE") + " : CAN");
				this.isResponse = false;
				
			} else if(cmMap.getMap("BLK_TYPE").equals("P")) { // POS 메세지
				
				log.info("수신정보 = " + cmMap.getMap("BLK_TYPE") + " : POS");
				this.isResponse = false;
				
				/*수신서버에서는 필요없음
				 * 단) 송신서버는 P_DATA 전문의 Flag Byte1 'E' 인 경우 수신측은 그 시점까지
				 * 받은 DATA의 길이 P_POS Block 을 이용해 송신측으로 전송한다.
				 * */
				
			} else if(cmMap.getMap("BLK_TYPE").equals("D")) { // DATA 메세지
				
				log.debug("==========================================================");
				log.debug("=========전문구분=========[" + cmMap.getMap("BLK_TYPE") + "]");
				log.debug("==========================================================");
				
				/*수신된 데이터중 파일데이터가 수신되므로 파일정보를 바탕으로 파일을 생성시킨다.*/
				
				KFL_2550.Msg_Data_FieldList();
				binMap = KFL_2550.parseBuff(buffer); 
				
				/* 확인하고 넘어가야 할 사항
				 * data field 의 실제길이 와 offset 의 길이를 다시 변경해야 하는지 ... 
				 * */
				int filesize = ((Long)recvMap.getMap("c_file_size")).intValue();
				int length   = ((Long)CbUtil.transLength((byte[])binMap.getMap("BLK_RD_LEN"), 2)).intValue();
				int offset   = ((Long)CbUtil.transLength((byte[])binMap.getMap("BLK_OFFSET"), 4)).intValue();
				
				log.debug("filesize(파일정보의파일길이)      = " + filesize);
				log.debug("length(데이터수신 실제데이터길이) = " + length);
				log.debug("offset(데이터수신 파일내  Offset) = " + offset);
				
				/* 파일이 수신된 경우 처리...*/
				outFileName = outFilePath + recvMap.getMap("file_name").toString();
				String bakFileName = backFilePath + recvMap.getMap("file_name").toString();

				// 파일정보만...
				byte[] binFile = new byte[length];
				System.arraycopy(buffer, 16, binFile, 0, length);

				log.debug("파일정보 데이터 = [" + new String(binFile) + "]");
				
				makeFile(binFile, outFileName);  //수신파일 APPEND 생성
				makeFile(binFile, bakFileName);  //백업파일 APPEND 생성
				
				/*정상이면 응답전문을 보냄*/
				/*정상이면 POS를 보내고 파일송신을 기다림...*/
				
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
					//각각  Packet 수신 후 수신결과 응답요구
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
				
				//1056 bytes 전체송신 전문생성
				System.arraycopy(MsgHead.getBytes(),    0, bres_msg, 0, MsgHead.length());
				System.arraycopy(bMd5,                  0, bres_msg, MsgHead.length(), bMd5.length);
				System.arraycopy(bMd5_Incode       ,    0, bres_msg, MsgHead.length() + bMd5.length, 16);

			} else if(cmMap.getMap("BLK_TYPE").equals("X")) { // EOF 메세지
				//ACK 응답전문을 셋팅하여 송신한다.
				
				log.debug("==========================================================");
				log.debug("=========전문구분=========[" + cmMap.getMap("BLK_TYPE") + "]");
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
					
				
				/* 여기가 파일수신의 끝인가...모르겠다...
				 * 여기서 파일 수신이 끝이라면 파일을 읽어서 DB에 저장시키는게 좋지 않을까?
				 * */				
				this.isResponse = false;  /*응답 전문을 보내지 않는다...왜냐면 주고 끊어버린다...........나쁜섹*/
				
				Jummun_insert(outFileName);
				
			}
			
			/*여기서 고민...
			 *--파일정보구분에 따른 리턴값을 잘계산하기 바란다...
			 *--멍멍멍 때리고 있다. 
			 * */
			
			this.retBuffer = bres_msg;	
			
			if(this.isResponse){
				CbUtil.dumpAsHex(log, bres_msg, "결과송신전문", 1056);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	/**
	 * 수신된 파일을 읽어서 DB에 입력한다.
	 * @param recvFNM
	 */
	public void Jummun_insert(String recvFNM){
		
		if(log.isInfoEnabled()) {
			log.info("=======================================================");
			log.info("=============  수신 파일 DB 입력 시작  ================");
			log.info("=======================================================");
		}
		
		try {

			log.info("수신파일명 = " + recvFNM);
			
			outFilePath  = "/app/data/cyber_ap/recv/";
			backFilePath = "/app/data/cyber_ap/back/";
			
			/*전영업일 가져오기...*/
			ArrayList<MapForm> alFileInfo = new ArrayList<MapForm>();
			
			MapForm mf = new MapForm();
			
			File outFile = new File(recvFNM);
			
			mf.setMap("d_cnt", 1);
			
			//영업일기준
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
					
					if(mfFileInfo.getMap("FormName").equals("GR6675")) {  /*상하수도예약납부통지*/
					
						/*================================================================*/
						/*상하수도예약납부통지*/
						/*================================================================*/
						
						log.debug("========================================== ");
						log.debug("상하수도예약납부통지 ");
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
								
								if(j == 0) { /*head 부와 trailer 부 제거*/

									mpGR6676.setMap("TOTALCNT"       , mpGR6676.getMap("TOTALCOUNT"));
									mpGR6676.setMap("TOTALAMT"       , 0);
									mpGR6676.setMap("NAPDT"          , mpGR6676.getMap("TRANSDATE"));
									
								} else if(j == (alGR6676.size() - 1))	{
									
									mpGR6676.setMap("TOTALCNT"       , mpGR6676.getMap("TOTALCOUNT"));
									mpGR6676.setMap("TOTALAMT"       , mpGR6676.getMap("TOTALPAYMONEY"));
									mpGR6676.setMap("NAPDT"          , mpGR6676.getMap("TRANSDATE"));
									
								} else {
									
									/*예약납부 파일 입력*/
									
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
					                	log.info("상하수도 예약납부파일 DB저장 완료!");
					                }
				                	
				                }catch (Exception e){
				                	
				                	/*중복이 발생하거나 제약조건에 어긋나는 경우체크*/
									if (e instanceof DuplicateKeyException){
										
										if (sqlService_cyber.queryForUpdate("TXBT2550.UPDATE_RES_RS1101_TB", mpGR6676) > 0) {
						                	log.info("상하수도 예약납부파일 DB수정 완료!");
						                }
										
									}else{
										log.error("오류데이터 = " + mpGR6676.getMaps());
										e.printStackTrace();							
										throw (RuntimeException) e;
									}
									
				                }
								
							}
						
						}
						
					} else if(mfFileInfo.getMap("FormName").equals("GR6694")) {  /*세외수입예약납부통지*/
						
						/*================================================================*/
						/*세외수입예약납부통지*/
						/*================================================================*/
						
						log.debug("========================================== ");
						log.debug("세외수입예약납부통지 ");
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
								
								if(j == 0) { /*head 부와 trailer 부 제거*/

									mpGR6694.setMap("TOTALCNT"       , mpGR6694.getMap("TOTALCOUNT"));
									mpGR6694.setMap("TOTALAMT"       , 0);
									mpGR6694.setMap("NAPDT"          , mpGR6694.getMap("TRANSDATE"));
									
								} else if(j == (alGR6694.size() - 1))	{
									
									mpGR6694.setMap("TOTALCNT"       , mpGR6694.getMap("TOTALCOUNT"));
									mpGR6694.setMap("TOTALAMT"       , mpGR6694.getMap("TOTALPAYMONEY"));
									mpGR6694.setMap("NAPDT"          , mpGR6694.getMap("TRANSDATE"));
									
								} else {
									
									/*예약납부 파일 입력*/
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
					                	log.info("세외수입 예약납부파일 DB저장 완료!");
					                }
				                	
				                }catch (Exception e){
				                	
				                	/*중복이 발생하거나 제약조건에 어긋나는 경우체크*/
									if (e instanceof DuplicateKeyException){
										
										if (sqlService_cyber.queryForUpdate("TXBT2550.UPDATE_RES_RS1101_TB", mpGR6694) > 0) {
						                	log.info("세외수입 예약납부파일 DB수정 완료!");
						                }
										
									}else{
										log.error("오류데이터 = " + mpGR6694.getMaps());
										e.printStackTrace();							
										throw (RuntimeException) e;
									}
									
				                }

							}

						}
						

					} else if(mfFileInfo.getMap("FormName").equals("GR6696")) {  /*환경개선부담금예약납부통지*/
						
						/*================================================================*/
						/*환경개선부담금예약납부통지*/
						/*================================================================*/
						
						log.debug("========================================== ");
						log.debug("환경개선부담금예약납부통지 ");
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
								
								if(j == 0) { /*head 부와 trailer 부 제거*/

									mpGR6696.setMap("TOTALCNT"       , mpGR6696.getMap("TOTALCOUNT"));
									mpGR6696.setMap("TOTALAMT"       , 0);
									mpGR6696.setMap("NAPDT"          , mpGR6696.getMap("TRANSDATE"));
									
								} else if(j == (alGR6696.size() - 1))	{
									
									mpGR6696.setMap("TOTALCNT"       , mpGR6696.getMap("TOTALCOUNT"));
									mpGR6696.setMap("TOTALAMT"       , mpGR6696.getMap("TOTALPAYMONEY"));
									mpGR6696.setMap("NAPDT"          , mpGR6696.getMap("TRANSDATE"));
									
								} else {
								
									/*예약납부 파일 입력*/
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
					                	log.info("환경개선부담금 예약납부파일 DB저장 완료!");
					                }
				                	
				                }catch (Exception e){
				                	
				                	/*중복이 발생하거나 제약조건에 어긋나는 경우체크*/
									if (e instanceof DuplicateKeyException){
										
										if (sqlService_cyber.queryForUpdate("TXBT2550.UPDATE_RES_RS1101_TB", mpGR6696) > 0) {
						                	log.info("환경개선부담금 예약납부파일 DB수정 완료!");
						                }
										
									}else{
										log.error("오류데이터 = " + mpGR6696.getMaps());
										e.printStackTrace();							
										throw (RuntimeException) e;
									}
									
				                }

							}

						}

					}
					
				}
				
			} else {
				log.info("[" + CbUtil.getCurDateTimes() + "] 금일 예약납부 처리 파일이 없습니다...");
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			 e.printStackTrace();
		}
		
		if(log.isInfoEnabled()) {
			log.info("=======================================================");
			log.info("=============결제원예약납부 DB 입력 끝 ================");
			log.info("=======================================================");
		}
		
	}
	

	/* Main Thread 
	 * 테스트 실행시만 사용합니다...
	 * */
	public static void main(String args[]) {

		ApplicationContext context  = null;
		
		try {
						
			Txdm2550 svr = new Txdm2550(0);
			
			/*Log4j 설정*/
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
	
	/* 서버 시작...*/
	private void startServer() throws Exception {
		
		/**
		 * SERVER START
		 * port     : Listen Port
         * worker   : Worker 쓰레드(class)
         * maxQue   : 최대 Queue 크기
    	 * procCnt  : 프로세스(쓰레드) 갯수
		 */
		
		int connPort =  Utils.getResourceInt("ApplicationResource", "kftc.recv_res.port");  /*51006*/
		
		//항상 수신받는 길이가 1056byte로 정해져 있음...
		RcvServer server = new RcvServer(connPort, "com.uc.bs.cyber.daemon.txdm2550.Txdm2550", 10, 1, "log4j.xml", 4, com.uc.core.Constants.TYPEFIELDLEN);

		server.setContext(appContext);
		server.setProcId("2550");
		server.setProcName("결제원파일예약납부서버");
		server.setThreadName("thr_2550");
		
		server.initial();
		
		comdThread = new Thread(server);
		
		/**
		 * 데이터 송수신을 위해서 서버를 등록해준다
		 * (서버관리데몬)을 위해서 Threading...
		 */
		comdThread.setName("2550");
		
		comdThread.start();
		
	}
	
	/*----------------------------------------------*/
	/* 일련번호 가져오기 테스트*/
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
	
	/* 트랜잭션을 실행하기 위한 함수.
	 * 함수 실행시 : transactionJob() 함수 자동 실행...
	 * transactionJob 함수 영역에서만 트랜잭션이 동작한다.
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
	
	/*트랜잭션 처리*/
	@Override
	public void transactionJob() {
		// TODO Auto-generated method stub

		try {
			
			log.info("[=========== 여긴 transactionJob() :: 트랜잭션처리영역 입니다. ========== ]");
			
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
	 * FILE 생성 및 읽기
	 *============================================
	 * */


	/**
	 * 파일생성 여부를 체크하고 파일이 존재하면 Append
	 * 그렇지 않으면 파일을 생성시킨다.
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
	 * 전문으로 부터 파일데이터를 파일에 기록한다.
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
			log.error("파일정보를 기록하는데 에러가 발생했습니다");
			e.printStackTrace();
		}

	}
	
	/**
	 * 결제원에서 수신한 파일을 읽고 파싱한다...
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
    			
    			if(mf.getMap("FormName").equals("GR6675")) { /*상하수도인경우*/
    				FileLen = 300;
    			} else {
    				FileLen = 230;
    			}
    			
    			//포멧의 길이가 230/300 Byte 이므로 230/300씩 짤라서 읽어낸다.
    			char[] readLine = new char[FileLen];
    			
    			if(mf.getMap("FormName").equals("GR6653") || 
    			   mf.getMap("FormName").equals("GR6675") || 
    			   mf.getMap("FormName").equals("GR6694") || 
    			   mf.getMap("FormName").equals("GR6696")) { /*지방세*/
    				
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
        			    parseMap.setMap("DATAGB", sbLine.substring(6, 8)); /*파일 데이터 구분*/
    			    	
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
