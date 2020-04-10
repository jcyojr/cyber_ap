/**
 * Sample TEST
 */
package com.uc.bs.cyber.daemon.txdm2550;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.dao.DuplicateKeyException;

import com.uc.core.MapForm;
import com.uc.core.misc.Utils;
import com.uc.core.misc.XMLFileReader;
import com.uc.core.misc.XmlUtils;
import com.uc.core.spring.service.IbatisBaseService;
import com.uc.egtob.net.ClientMessageService;
import com.uc.bs.cyber.CbUtil;

/**
 * @author 프리비
 *
 */
public class Txdm2550CSample  extends ClientMessageService implements Runnable {

	private IbatisBaseService sqlService_cyber = null;
	
	private ApplicationContext appContext = null;
	
	private int  myId = 0; 
	
	protected Log log = LogFactory.getLog(this.getClass());
	
	private Txdm2550FieldList kfField = null;
	
	CbUtil cUtil = null;
	
	private byte[] recvBuffer;
	private String outFilePath  = "";
	private String backFilePath = "";
	private String sndFilePath  = "";
	
	
	/**
	 * 
	 * @param hostAddr
	 * @param port
	 * @throws IOException
	 * @throws Exception
	 */
	public Txdm2550CSample() {
		
		CbUtil.setupLog4jConfig(this, "log4j.txdm2560.xml");
		
		kfField = new Txdm2550FieldList();
	}
		
	public static void main(String[] args) {
		
		ApplicationContext context  = null;
		
		Txdm2550CSample client = new Txdm2550CSample();
		
		try {
			
			String[] xmls = XMLFileReader.getAllFilePathArray(Utils.getResourcePath(client, "/"), "SERVICE.XML");

			String strSrc = Utils.getResourcePath(client, "/") + "config/sqlmaps.xml";

			String strDest = Utils.getResourcePath(client, "/") + "config/sqlmapConfig.xml";

			XmlUtils.setXmlAttributes(client, strSrc, strDest, "sqlMapConfig", "sqlMap", "url", xmls);

			context = new ClassPathXmlApplicationContext("config/Seoi-Spring-db.xml");
			
			client.setAppContext(context);
						
			Thread thr = new Thread(client);
			
			thr.start();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	/*-------------------------------------------------------*/
	/*--------------------property set ----------------------*/
	/*-------------------------------------------------------*/
	public void setAppContext(ApplicationContext appContext) {
		 this.appContext = appContext;
	}

	public ApplicationContext getAppContext() {
		return appContext;
	}
	
	public void run() {
		// TODO Auto-generated method stub

		sqlService_cyber = (IbatisBaseService) this.appContext.getBean("baseService_cyber");
		
		try {

			cUtil = CbUtil.getInstance();
			
			Txdm2550CSample client = new Txdm2550CSample();

			sndFilePath = "/app/cyber_ap/data/recv/";
			
			File outFile = new File(sndFilePath);
			
			File[] listf = outFile.listFiles();
			
			for(int i = 0 ; i < listf.length; i++){
				
				//if(listf[i].getName().substring(15).equals("20110805")) {
					
				if(listf[i].isFile()) {
					
					log.info("filename = " + listf[i].getName());
					log.info("filename = " + listf[i].getAbsolutePath());
					
					log.info("listf = [" + i + "] = " + listf[i].getName() + " || " + listf[i].getName().substring(15) + " || " + listf[i].getAbsolutePath());
					
					
					
					//kf_rev_file_transefer(client, listf[i].getAbsolutePath());
					
					log.info(not_in_semok());
					
					//Jummun_insert(listf[i].getAbsolutePath());
				}
					
					
				//}
				
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
	
	
	/**
	 * 결제원으로 예약파일을 전송한다....
	 * @return
	 */
	@SuppressWarnings("unused")
	private boolean kf_rev_file_transefer(Txdm2550CSample client, String kf_snd_file_nm) {
		
		
		log.info("=====================kf_rev_file_transefer()시작=======================");
		
		int connPort = 0;
		long filesz = 0;
		String connIP = "";
		boolean isConnect = false;
		
		int series_cnt = 0;  /*연속으로 전송하는 경우 : 8번마다 검증요청*/
		int timeout    = 30;
		
		String MsgHead = "1052F   ";
		
		byte[] resv_msg = new byte[1056];
		
		Txdm2550FieldList KFL_2550 = null;
		
		MapForm cmMap;
		
		try {
		
			outFilePath  = "/app/cyber_ap/recvdata/";
			backFilePath = "/app/cyber_ap/backup/";

			connPort =  Utils.getResourceInt("ApplicationResource", "kftc.recv_res.port");
			connIP   =  Utils.getResource("ApplicationResource", "kftc.recv_res.ip");
			
			try {
				
				client.Connect(connIP, connPort);       /*결제원*/
				
				isConnect = true;
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				
				isConnect = false;
			}

			if(!isConnect) {
				
				log.info("Soket 연결 오류 :: IP[" + connIP + "] PORT[" + connPort + "]");
				
			} else {
				
				/*전송준비 전문 (P_INFO) 전문생성 -> 전송할 파일정보 전송*/
				File outFile = new File(kf_snd_file_nm);
				
				if(outFile.exists()) {
					filesz = outFile.length();
					
					log.debug("파일존재 : " + outFile.getName());
					log.debug("파일길이 : " + filesz);
					
				} else {
					throw new Exception(outFile.getName() + " 존재하지 않습니다. 파일을 확인하세요.");
				}
				
				byte[] snd_type     = "I".getBytes();
				byte[] snd_flag1    = "E".getBytes();
				byte[] snd_flag2    = "u".getBytes();
				byte[] snd_flag3    = CbUtil.nullByte(1);
				byte[] snd_filesize = CbUtil.setOffset(filesz);
				byte[] snd_userid   = CbUtil.nullByte(64);
				byte[] snd_pass     = CbUtil.nullByte(64);
				byte[] snd_filenm   = CbUtil.rPadByte(outFile.getName().getBytes(), 128);
				byte[] snd_reser    = CbUtil.nullByte(768);
				
				byte[] bMd5 = new byte[1032];
				
				System.arraycopy(snd_type,      0, bMd5, 0, snd_type.length);
				System.arraycopy(snd_flag1,     0, bMd5, snd_type.length, snd_flag1.length);
				System.arraycopy(snd_flag2,     0, bMd5, snd_type.length + snd_flag1.length, snd_flag2.length);
				System.arraycopy(snd_flag3,     0, bMd5, snd_type.length + snd_flag1.length + snd_flag2.length, snd_flag3.length);
				System.arraycopy(snd_filesize,  0, bMd5, snd_type.length + snd_flag1.length + snd_flag2.length + snd_flag3.length, snd_filesize.length);
				System.arraycopy(snd_userid,    0, bMd5, snd_type.length + snd_flag1.length + snd_flag2.length + snd_flag3.length + snd_filesize.length, snd_userid.length);
				System.arraycopy(snd_pass,      0, bMd5, snd_type.length + snd_flag1.length + snd_flag2.length + snd_flag3.length + snd_filesize.length + snd_userid.length, snd_pass.length);
				System.arraycopy(snd_filenm,    0, bMd5, snd_type.length + snd_flag1.length + snd_flag2.length + snd_flag3.length + snd_filesize.length + snd_userid.length + snd_pass.length, snd_filenm.length);
				System.arraycopy(snd_reser,     0, bMd5, snd_type.length + snd_flag1.length + snd_flag2.length + snd_flag3.length + snd_filesize.length + snd_userid.length + snd_pass.length + snd_filenm.length, snd_reser.length);
				
				byte[] bMd5_Incode = new byte[16];
				System.arraycopy(CbUtil.Md5Sig(bMd5), 0, bMd5_Incode,0, 16);
				
				System.arraycopy(MsgHead.getBytes(),  0, resv_msg, 0, MsgHead.length());
				System.arraycopy(bMd5,                0, resv_msg, MsgHead.length(), bMd5.length);
				System.arraycopy(bMd5_Incode,         0, resv_msg, MsgHead.length() + bMd5.length, 16);
				
				/*결제원으로 전문(파일정보)을 전송한다...*/
				log.debug("파일 정보 송신...");
				client.sendData(resv_msg);
				
				/*전송결과 수신을 기다리는다..30초까지...*/
				byte[] recv  = client.recv(timeout);
				
				log.debug("=========================================");
				log.debug("파일 정보 송신에 대한 응답수신...");
				log.debug("=========================================");
				
				/* 수신전문분석 : 초기화 */
				KFL_2550 = new Txdm2550FieldList();
				
				cmMap = KFL_2550.parseBuff(recv);         /*헤드와 앞 4byte만 파싱...*/
				
				log.debug("cmMap = [" + cmMap.getMaps() + "]");
			
				if(cmMap.getMap("BLK_TYPE").equals("P")) { /*P_POS 전문*/
				
					long p_ins = 0;
					
					do{
		
						/*다시 수신되는 경우 재 파싱...*/
						cmMap = KFL_2550.parseBuff(recv);  
						
						if(series_cnt > 0) {
							log.debug("=========================================");
							log.debug("재요청인 경우 [" + series_cnt + "]");
							log.debug("cmMap = [" + cmMap.getMaps() + "]");
							log.debug("=========================================");
						}
						
						/*P인경우 데이터를 전송한다...*/
						byte[] offset  = new byte[4];
						
						char[] rContent = new char[1024];   /*한번전송할 수 있는 최대 사이즈는 1024*/
						
						System.arraycopy(recv, 12, offset, 0, offset.length);
						
						long lOffset = CbUtil.transLength(offset, 4);
						
						log.info("수신된 Offset = [" + lOffset + "]");
						
						StringBuffer sbLine = new StringBuffer();
						
						String s_yn = "";
						
						if(filesz > 1024) s_yn = "G"; else s_yn = "E";
						
						if(series_cnt > 0 && series_cnt % 8 == 0) {
							/*8개의 패킷을 전송한 경우 전송안정성을 위하여 ACK를 요구하는 E Mode 를 전송함...*/
							s_yn = "E";
							series_cnt = 0;
						}
						
						log.info("s_yn = [" + s_yn + "]");
						
						/*파일을 읽어서 파일전송전문을 생성시킨다.*/
						BufferedReader br = new BufferedReader(new FileReader(kf_snd_file_nm));
											
						br.skip(lOffset);
						
						int readLen = br.read(rContent);
						
						log.info("READ SIZE = [" +readLen + "]");
						
						br.close();

						sbLine.append(new String(rContent));

	                    log.debug("읽은파일의 내용 = [" + sbLine.toString() + "]");
	                    
	                    long offset_infile = lOffset;
	                    long prt_filesize = (long)((int)(filesz - lOffset) > 1024 ? 1024 : (int)(filesz - lOffset));
	                    
	                    log.debug("파일내 offset = [" + offset_infile + "]");
	                    log.debug("파일 block    = [" + prt_filesize + "]");
	                    
						byte[] f_type     = "D".getBytes();
						byte[] f_flag1    = s_yn.getBytes();
						byte[] f_flag2    = CbUtil.setMsgLen(prt_filesize);
						byte[] f_offset   = CbUtil.setOffset(offset_infile);
						byte[] f_data     = CbUtil.rPadByte(sbLine.toString().getBytes(), 1024);
						
						byte[] f_bMd5 = new byte[1032];
						
						System.arraycopy(f_type,       0, f_bMd5, 0, f_type.length);
						System.arraycopy(f_flag1,      0, f_bMd5, f_type.length, f_flag1.length);
						System.arraycopy(f_flag2,      0, f_bMd5, f_type.length + f_flag1.length, f_flag2.length);
						System.arraycopy(f_offset,     0, f_bMd5, f_type.length + f_flag1.length + f_flag2.length, f_offset.length);
						System.arraycopy(f_data,       0, f_bMd5, f_type.length + f_flag1.length + f_flag2.length + f_offset.length, f_data.length);
						
						byte[] f_bMd5_Incode = new byte[16];
						System.arraycopy(CbUtil.Md5Sig(f_bMd5), 0, f_bMd5_Incode,0, 16);
						
						resv_msg = new byte[1056];
						
						System.arraycopy(MsgHead.getBytes(),    0, resv_msg, 0, MsgHead.length());
						System.arraycopy(f_bMd5,                0, resv_msg, MsgHead.length(), f_bMd5.length);
						System.arraycopy(f_bMd5_Incode,         0, resv_msg, MsgHead.length() + f_bMd5.length, 16);
						
						/*결제원으로 전문(파일정보)을 전송한다...*/
						client.sendData(resv_msg);
						
						if(s_yn.equals("G")) {
							
							p_ins += prt_filesize;
							
							System.arraycopy(CbUtil.setOffset(p_ins), 0, recv, 12, 4);
							
							if(filesz == p_ins) {
								//정상적으로 파일을 보냈다고 확인함...
								//EOF를 전송한다...
								bMd5 = new byte[1032];
								
								System.arraycopy("X".getBytes(),        0, bMd5, 0, 1);
								System.arraycopy(CbUtil.nullByte(1),    0, bMd5, 1, 1);
								System.arraycopy(CbUtil.nullByte(1),    0, bMd5, 2, 1);
								System.arraycopy(CbUtil.nullByte(1029), 0, bMd5, 3, 1029);
								
								bMd5_Incode = new byte[16];
								
								System.arraycopy(CbUtil.Md5Sig(bMd5), 0, bMd5_Incode,0, 16);
							
								resv_msg = new byte[1056];
								
								System.arraycopy(MsgHead.getBytes(),    0, resv_msg, 0, MsgHead.length());
								System.arraycopy(bMd5,                  0, resv_msg, MsgHead.length(), bMd5.length);
								System.arraycopy(bMd5_Incode        ,   0, resv_msg, MsgHead.length() + bMd5.length, 16);
								
								/*파일전송 종료에 대한 메세지 전송*/
								client.sendData(resv_msg);
								
								log.info("파일송신 끝[" + kf_snd_file_nm + "]");
								
								break;
	
							} else {
								log.info("파일송신 재요청 :: [" + p_ins + "]");
							}

						} else {
							
							/*전송결과 수신을 기다린다...30초까지...*/
							recv  = client.recv(timeout);
							
							/*전송에 후 수신전문분석*/
							cmMap = KFL_2550.parseBuff(recv);         /*헤드와 앞 4byte만 파싱...*/
							
							log.debug("cmMap = " + cmMap.getMaps());
						}
						
						/*파일을 주고 받는경우 파일의 사이즈가 1024보다 큰 경우는 주고받고를 계속해야 함....*/
						
						if(cmMap.getMap("BLK_TYPE").equals("P")) {
							
							/*원래는 수신받은 전문을 MD5 검증해야 하는데 일단 귀찮아서 못하겠음...이단은 누가 알아서 하도록...*/
							
							/*결재원에서 남은 파일을 다시 요청한 경우 이므로 : 계속해서 파일을 전송한다...*/
							System.arraycopy(recv, 12, offset, 0, offset.length);
							lOffset = CbUtil.transLength(offset, 4);
							
							if(filesz == lOffset) {
								//정상적으로 파일을 보냈다고 확인함...
								//EOF를 전송한다...
								bMd5 = new byte[1032];
								
								System.arraycopy("X".getBytes(),        0, bMd5, 0, 1);
								System.arraycopy(CbUtil.nullByte(1),    0, bMd5, 1, 1);
								System.arraycopy(CbUtil.nullByte(1),    0, bMd5, 2, 1);
								System.arraycopy(CbUtil.nullByte(1029), 0, bMd5, 3, 1029);
								
								bMd5_Incode = new byte[16];
								
								System.arraycopy(CbUtil.Md5Sig(bMd5), 0, bMd5_Incode,0, 16);
							
								resv_msg = new byte[1056];
								
								System.arraycopy(MsgHead.getBytes(),    0, resv_msg, 0, MsgHead.length());
								System.arraycopy(bMd5,                  0, resv_msg, MsgHead.length(), bMd5.length);
								System.arraycopy(bMd5_Incode        ,   0, resv_msg, MsgHead.length() + bMd5.length, 16);
								
								/*파일전송 종료에 대한 메세지 전송*/
								client.sendData(resv_msg);
								
								log.info("파일송신 끝[" + kf_snd_file_nm + "]");
								
								break;
								
								
							} else {
								log.info("파일송신 재요청 :: [" + lOffset + "]");
							}


						} else if(cmMap.getMap("BLK_TYPE").equals("X")) {
							
							/*파일송신에 대한 수신완료 메세지를 수신하였으므로 전송종료*/

							bMd5 = new byte[1032];
							
							System.arraycopy("A".getBytes(),        0, bMd5, 0, 1);
							System.arraycopy(CbUtil.nullByte(1),    0, bMd5, 1, 1);
							System.arraycopy("X".getBytes(),        0, bMd5, 2, 1);
							System.arraycopy(CbUtil.nullByte(1029), 0, bMd5, 3, 1029);
							
							bMd5_Incode = new byte[16];
							
							System.arraycopy(CbUtil.Md5Sig(bMd5), 0, bMd5_Incode,0, 16);
						
							resv_msg = new byte[1056];
							
							System.arraycopy(MsgHead.getBytes(),    0, resv_msg, 0, MsgHead.length());
							System.arraycopy(bMd5,                  0, resv_msg, MsgHead.length(), bMd5.length);
							System.arraycopy(bMd5_Incode        ,   0, resv_msg, MsgHead.length() + bMd5.length, 16);
							
							/*파일전송 종료에 대한 메세지 전송*/
							client.sendData(resv_msg);
							
							log.info("파일송신 완료[" + kf_snd_file_nm + "]");
							
							break;
						} else if(cmMap.getMap("BLK_TYPE").equals("C")) {
							
							
							KFL_2550.Msg_Can_FieldList();
							cmMap = KFL_2550.parseBuff(recv);  
							
							log.info(cmMap);
							
							break;
						}

						series_cnt ++;
						
					}while(true);
					
					
				} else if(cmMap.getMap("BLK_TYPE").equals("X")) {
					
					log.info("수신정보 = " + cmMap.getMap("BLK_TYPE") + " : EOF");
					
				} else if(cmMap.getMap("BLK_TYPE").equals("C")) {
					
					log.info("수신정보 = " + cmMap.getMap("BLK_TYPE") + " : CAN");
				}
		
			}
			
			if(isConnect) client.Disconnect();

			log.info("=====================kf_rev_file_transefer() 끝=======================");
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
			try {
				if(isConnect) client.Disconnect();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		
		return true;
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
	@SuppressWarnings("unused")
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
    			
    			StringBuffer  sbLine = new StringBuffer();
    			
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
				mf.setMap("IcheDate", outFile.getName().substring(15));
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
								
								mpGR6676.setMap("PROC_YMD"    , mf.getMap("IcheDate"));
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
								
								mpGR6694.setMap("PROC_YMD"    , mf.getMap("IcheDate"));
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
					                mpGR6694.setMap("TAX_NO"      , mpGR6694.getMap("TAX_NO"));
					                mpGR6694.setMap("KMGO_CD"     , mpGR6694.getMap("KUMGOCD"));
					                mpGR6694.setMap("SU_BRC"      , mpGR6694.getMap("SUNAPJUMCODE"));
					                mpGR6694.setMap("OCR_BD"      , mpGR6694.getMap("OCR_BAND"));
					                mpGR6694.setMap("PROC_GB"     , mpGR6694.getMap("PROCGUBUN"));
					                mpGR6694.setMap("FEE_AMT"     , mpGR6694.getMap("SUSU_AMT"));
					                mpGR6694.setMap("CUST_NO"     , "");
					                mpGR6694.setMap("EPAY_NO"     , "");
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
								
								mpGR6696.setMap("PROC_YMD"    , mf.getMap("IcheDate"));
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
	
	/*세외수입에서 제외시켜야 할 과목*/
	private String not_in_semok() {
	
		StringBuffer sb = new StringBuffer();
		String Retval = "";
		try {
	
			ArrayList<MapForm> alNoSemokList =  sqlService_cyber.queryForList("CODMBASE.NOSEOI", null);
			
			if (alNoSemokList.size()  >  0)   {
				
				for ( int rec_cnt = 0;  rec_cnt < alNoSemokList.size();  rec_cnt++)   {
					
					MapForm mpNoSemokList =  alNoSemokList.get(rec_cnt);
					
					/*혹시나...because of testing */
					if (mpNoSemokList == null  ||  mpNoSemokList.isEmpty() )   {
						continue;
					}
					
					sb.append("'").append(mpNoSemokList.getMap("SEMOK")).append("'").append(",");

				}
				
				Retval = sb.toString().substring(0, sb.toString().length() -1);
				
			} 
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	
		return Retval;
	}
}
