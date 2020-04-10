/**
 *  �ֽý��۸� : �λ�� ���̹����漼û
 *  ��  ��  �� : �ϰ����� �ڵ�Ǯ��
 *  ��  ��  �� : �������ڷ� ���Աݼ��� FTP ����
 *  Ŭ����  ID : Txbt5001
 *  ����  �̷� :
 * ------------------------------------------------------------------------
 *  �ۼ���      	�Ҽ�  		    ����            Tag           ����
 * ------------------------------------------------------------------------
 *  Ȳ����       ��ä��(��)      2011.07.20         %01%         �����ۼ�
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
	 * ������
	 */
	public Txbt5001() {
		// TODO Auto-generated constructor stub
		super(); /*�������� ù��°��ɹ�*/
		
		log = LogFactory.getLog(this.getClass());
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
	  
		System.out.println("=================================================");
		System.out.println("== ���������� ����   Started");
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
			 * ���ؽ�Ʈ ���� *Service.xml ������ ã�Ƽ� sqlmapConfig.xml ���Ͽ� ����Ѵ�
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

			batch.setProcess("5001", "���������� ����", "thr_5001");  /* �������� ��� */
			
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
		System.out.println("== ���������� ����   Ended");
		System.out.println("=================================================");	
		
	}


	
	/*[��� ������ ���⿡ �����Ѵ�...]
	 * (�������)
	 *  try ~ catch ���� ����ϴ� ��� TRANSACTION ó���� �ݵ�� throw ó���� �ؾ� ��... 
	 * */
	@Override
	public void transactionJob() {
		// TODO Auto-generated method stub
		
		try {
			
			this.cyberService = (IbatisBaseService) this.getService("baseService");
			
			DecimalFormat df = new DecimalFormat("00");
			Calendar currentCal = Calendar.getInstance();
			SimpleDateFormat time = new SimpleDateFormat( "HHmmss" );	//�ý��۽ð�
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
			 * �ϰ����ι�ȣ ����� �����´�...
			 */
			System.out.println("======================transactionJob4===========================");
			Iterator<MapForm> tongList = cyberService.queryForList("TXBT5001.SELECT_TX4111", null).iterator();
			System.out.println("======================transactionJob6===========================");
			
			/**
			 * ���������� ���
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
				 * ���ۿϷ� �÷��� ������Ʈ
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
				
				log.debug("FTP���ۼ���!!");
				
				String ok_sendfile = sendfile + ".OK";
				File okfile = new File(ok_sendfile);
				okfile.createNewFile();

				/* OK�������� */ 
				boolean isSuccesso = ftpSender(url, id, pw, ok_sendfile, rcvdir, filename + ".OK");
				
				if(isSuccesso){
					
					log.debug("OK���� FTP���ۼ���");
					
					/* ���ϻ��� �� ä�� ���� ��� */ 
					okfile.delete();
					
					String fileBackupDir = Utils.getResource("ApplicationResource", "cyber.app.backup");
					
					File backFile = new File(fileBackupDir + filename);
					outputFile.renameTo(backFile);
					outputFile.delete();
					
					
				} else {
					
					log.debug("OK���� FTP���۽���");

				}	

			} else {
				
				log.debug("FTP���۽���");

				File backFile = new File("/app/data/cyber_ap/apprecip/" + filename + ".ERR");
				outputFile.renameTo(backFile);
				outputFile.delete();
				
			}				 
			 
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	/* FTP ���� */
	public boolean ftpSender(String url, String id, String pw, String sendfile, String rcvdir, String rcvfile) throws Exception{

		FTPClient ftp = new FTPClient();
		log.info("FTP SEND SVR=" + url + ", FILE=" + sendfile);
		
		File uploadfile = new File(sendfile);
		FileInputStream fis =  new FileInputStream(uploadfile);
 
		try {
  
			ftp.connect(url);

			if(!ftp.login(id, pw)){
				log.debug("ID�Ǵ� PW�� Ʋ���ϴ�.");
				return false;
			}
		   
			ftp.pasv();

			if(!ftp.changeWorkingDirectory(rcvdir)){
				log.debug("���丮("+rcvdir+")�� ������ �� �����ϴ�.");
				ftp.logout();
				ftp.disconnect();	
				fis.close();
				return false;
			}
			
			ftp.setFileType(FTPClient.BINARY_FILE_TYPE);
			
			if(!ftp.storeFile(rcvdir + "/" + rcvfile, fis)){
				log.debug("���� ���ۿ� �����߽��ϴ�.");
				ftp.logout();
				ftp.disconnect();
				fis.close();
				return false;
			}
			fis.close();
	
		} catch (SocketException e) {
			// TODO �ڵ� ������ catch ���
			e.printStackTrace();
			//throw e;
	
		} catch (IOException e) {
			// TODO �ڵ� ������ catch ���
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
