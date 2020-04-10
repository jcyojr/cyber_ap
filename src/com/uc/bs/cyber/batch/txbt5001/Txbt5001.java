/**
 *  주시스템명 : 부산시 사이버지방세청
 *  업  무  명 : 일괄납부 자동풀림
 *  기  능  명 : 충당수납자료 세입금센터 FTP 전송
 *  클래스  ID : Txbt5001
 *  변경  이력 :
 * ------------------------------------------------------------------------
 *  작성자      	소속  		    일자            Tag           내용
 * ------------------------------------------------------------------------
 *  황종훈       유채널(주)      2011.07.20         %01%         최초작성
 */
package com.uc.bs.cyber.batch.txbt5001;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.SocketException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;

import org.apache.commons.logging.LogFactory;
import org.apache.commons.net.ftp.FTPClient;
import org.springframework.beans.BeansException;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.ibatis.sqlmap.client.SqlMapException;
import com.uc.bs.cyber.CbUtil;
import com.uc.bs.cyber.batch.Txdm_BatchProcess;
import com.uc.core.MapForm;
import com.uc.core.misc.Utils;
import com.uc.core.misc.XMLFileReader;
import com.uc.core.misc.XmlUtils;
import com.uc.core.spring.service.IbatisBaseService;
import com.uc.egtob.net.FieldList;

/**
 * @author Administrator
 *
 */
public class Txbt5001 extends Txdm_BatchProcess {

	
	/**
	 * 생성자
	 */
	public Txbt5001() {
		// TODO Auto-generated constructor stub
		super(); /*생성자의 첫번째명령문*/
		
		log = LogFactory.getLog(this.getClass());
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
	  
		System.out.println("=================================================");
		System.out.println("== 충당수납내역 전송   Started");
		System.out.println("=================================================");	
		
		Txbt5001 batch;
		
		batch = new Txbt5001();

		System.out.println("== OS Name = " + System.getProperty("os.name"));
		System.out.println("== OS Version = " + System.getProperty("os.version"));
		System.out.println("== FILE Separator = " + System.getProperty("file.separator"));
		
		try {
			//Log
			
			CbUtil.setupLog4jConfig(batch, "log4j.txbt5001.xml");
			
			/**
			 * 컨텍스트 내의 *Service.xml 파일을 찾아서 sqlmapConfig.xml 파일에 등록한다
			 */
			try {
				String[] xmls = XMLFileReader.getAllFilePathArray(CbUtil.getResourcePath(batch, "/"), "SERVICE.XML");

				String strSrc = CbUtil.getResourcePath(batch, "/") + "config/sqlmaps.xml";

				String strDest = CbUtil.getResourcePath(batch, "/") + "config/sqlmapConfig.xml";

				XmlUtils.setXmlAttributes(batch, strSrc, strDest, "sqlMapConfig", "sqlMap", "url", xmls);

				System.out.println("2=========================================================");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}		

			batch.setContext(new ClassPathXmlApplicationContext("config/Single-Spring-db.xml"));

			batch.setProcess("5001", "충당수납내역 전송", "thr_5001");  /* 업무데몬 등록 */
			
			batch.context = batch.getContext();
			
			batch.startJob();

		} catch (SqlMapException se) {
			se.printStackTrace();
		} catch (BeansException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		
		System.out.println("=================================================");
		System.out.println("== 충당수납내역 전송   Ended");
		System.out.println("=================================================");	
		
	}


	
	/*[모든 업무를 여기에 구성한다...]
	 * (참고사항)
	 *  try ~ catch 문을 사용하는 경우 TRANSACTION 처리시 반드시 throw 처리를 해야 함... 
	 * */
	@Override
	public void transactionJob() {
		// TODO Auto-generated method stub
		
		try {
			
			this.cyberService = (IbatisBaseService) this.getService("baseService");
			
			DecimalFormat df = new DecimalFormat("00");
			Calendar currentCal = Calendar.getInstance();
			SimpleDateFormat time = new SimpleDateFormat( "HHmmss" );	//시스템시간
			Date date = new Date( );
		  
			currentCal.add(Calendar.DATE, 0);
		  
			String year = Integer.toString(currentCal.get(Calendar.YEAR));
			String month = df.format(currentCal.get(Calendar.MONTH)+1);
			String day = df.format(currentCal.get(Calendar.DAY_OF_MONTH));
			String totime = time.format(date);		
			
			FieldList dField;
			byte[] buffer;	
			
			String filename = year + month + day + totime;
			
			MapForm dMap = null;
	
			buffer = new byte[8 + 8 + 8 + 27 + 21 + 19 + 7 + 108]; 
			
			String path = "/app/data/cyber_ap/apprecip/send/";
			
			File outputFile = new File(path + "/" + filename);
			
			FileOutputStream outStream = new FileOutputStream(outputFile);

			String nLine= "\n";
	
			/**
			 * 일괄납부번호 목록을 가져온다...
			 */
			System.out.println("======================transactionJob4===========================");
			Iterator<MapForm> tongList = cyberService.queryForList("TXBT5001.SELECT_TX4111", null).iterator();
			System.out.println("======================transactionJob6===========================");
			
			/**
			 * 충당수납내역 목록
			 */
			 while(tongList.hasNext()) {
				 
				dMap = new MapForm();
				dField = new FieldList();
				
				MapForm tongMap =  tongList.next();
				 
				dMap.setMap("PAY_DT", tongMap.get("PAY_DT"));
				dMap.setMap("ACC_DT", tongMap.get("ACC_DT"));
				dMap.setMap("ICHE_DT", tongMap.get("ICHE_DT"));
				dMap.setMap("GWASE_NO", tongMap.get("GWASE_NO"));
				dMap.setMap("OVERPAY_NO", tongMap.get("OVERPAY_NO"));
				dMap.setMap("EPAY_NO", tongMap.get("EPAY_NO"));
				dMap.setMap("C_BANK", tongMap.get("C_BANK"));
				dMap.setMap("OCR", tongMap.get("OCR1").toString() + tongMap.get("OCR2").toString());
				
				dField.add("PAY_DT", 8, "C");
				dField.add("ACC_DT", 8, "C");
				dField.add("ICHE_DT", 8, "C");
				dField.add("GWASE_NO", 27, "C");
				dField.add("OVERPAY_NO", 21, "C");
				dField.add("EPAY_NO", 19, "C");
				dField.add("C_BANK", 7, "C");
				dField.add("OCR", 108, "C");
				 
				buffer = dField.makeMessageByte(dMap);
 
				outStream.write(buffer, 0 ,buffer.length);
				outStream.write(nLine.getBytes(), 0 ,nLine.length());
	
				/**
				 * 전송완료 플래그 업데이트
				 */
				cyberService.queryForUpdate("TXBT5001.UPDATE_TX4111", tongMap);
				 
			 }
		 
			outStream.flush();
			outStream.close();
				
			String url = Utils.getResource("ApplicationResource", "tis.ftp.ip");
			String id =  Utils.getResource("ApplicationResource", "tis.ftp.id");
			String pw =  Utils.getResource("ApplicationResource", "tis.ftp.pw");				
			String rcvdir = Utils.getResource("ApplicationResource", "tis.ftp.target");
			
			String rcvfile = filename;
			String sendfile = path + "/" + filename;
			
			boolean isSuccess = ftpSender(url, id, pw, sendfile, rcvdir, rcvfile);
			
			if(isSuccess){
				
				log.debug("FTP전송성공!!");
				
				String ok_sendfile = sendfile + ".OK";
				File okfile = new File(ok_sendfile);
				okfile.createNewFile();

				/* OK파일전송 */ 
				boolean isSuccesso = ftpSender(url, id, pw, ok_sendfile, rcvdir, filename + ".OK");
				
				if(isSuccesso){
					
					log.debug("OK파일 FTP전송성공");
					
					/* 파일삭제 및 채번 파일 백업 */ 
					okfile.delete();
					
					String fileBackupDir = Utils.getResource("ApplicationResource", "cyber.app.backup");
					
					File backFile = new File(fileBackupDir + filename);
					outputFile.renameTo(backFile);
					outputFile.delete();
					
					
				} else {
					
					log.debug("OK파일 FTP전송실패");

				}	

			} else {
				
				log.debug("FTP전송실패");

				File backFile = new File("/app/data/cyber_ap/apprecip/" + filename + ".ERR");
				outputFile.renameTo(backFile);
				outputFile.delete();
				
			}				 
			 
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	/* FTP 전송 */
	public boolean ftpSender(String url, String id, String pw, String sendfile, String rcvdir, String rcvfile) throws Exception{

		FTPClient ftp = new FTPClient();
		log.info("FTP SEND SVR=" + url + ", FILE=" + sendfile);
		
		File uploadfile = new File(sendfile);
		FileInputStream fis =  new FileInputStream(uploadfile);
 
		try {
  
			ftp.connect(url);

			if(!ftp.login(id, pw)){
				log.debug("ID또는 PW가 틀립니다.");
				return false;
			}
		   
			ftp.pasv();

			if(!ftp.changeWorkingDirectory(rcvdir)){
				log.debug("디렉토리("+rcvdir+")를 변경할 수 없습니다.");
				ftp.logout();
				ftp.disconnect();	
				fis.close();
				return false;
			}
			
			ftp.setFileType(FTPClient.BINARY_FILE_TYPE);
			
			if(!ftp.storeFile(rcvdir + "/" + rcvfile, fis)){
				log.debug("파일 전송에 실패했습니다.");
				ftp.logout();
				ftp.disconnect();
				fis.close();
				return false;
			}
			fis.close();
	
		} catch (SocketException e) {
			// TODO 자동 생성된 catch 블록
			e.printStackTrace();
			//throw e;
	
		} catch (IOException e) {
			// TODO 자동 생성된 catch 블록
			e.printStackTrace();
			//throw e;
		} catch(Exception e){
			e.printStackTrace();
			//throw e;
		} finally{
			ftp.logout();
			ftp.disconnect();
			fis = null;
		}
		
		return true;
	}	

	@Override
	public void setDatasrc(String datasrc) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void runProcess() throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void initProcess() throws Exception {
		// TODO Auto-generated method stub
		
	}

}
