/**
 *  �ֽý��۸� : �λ�� ���̹����漼û
 *  ��  ��  �� : �ϰ����� �ڵ�Ǯ��
 *  ��  ��  �� : �ϰ����� ��û�� ������ ���Ͽ� ���ε��� ���� ������ �����Ѵ�
 *               ��ġ�� �����Ǹ� ������ �ѹ��� 
 *  Ŭ����  ID : Txbt2650
 *  ����  �̷� :
 * ------------------------------------------------------------------------
 *  �ۼ���      	�Ҽ�  		    ����            Tag           ����
 * ------------------------------------------------------------------------
 *  ����       ��ä��(��)      2011.07.20         %01%         �����ۼ�
 */
package com.uc.bs.cyber.batch.txbt2650;

import java.util.Iterator;

import org.apache.commons.logging.LogFactory;
import org.apache.log4j.xml.DOMConfigurator;
import org.springframework.beans.BeansException;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.ibatis.sqlmap.client.SqlMapException;
import com.uc.bs.cyber.CbUtil;
import com.uc.bs.cyber.batch.Txdm_BatchProcess;
import com.uc.core.MapForm;
import com.uc.core.misc.XMLFileReader;
import com.uc.core.misc.XmlUtils;
import com.uc.core.spring.service.IbatisBaseService;
import com.uc.core.spring.service.IbatisService;

/**
 * @author Administrator
 *
 */
public class Txbt2650 extends Txdm_BatchProcess {

	
	/**
	 * ������
	 */
	public Txbt2650() {
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
		System.out.println("== ���̹����漼û �ϰ����� �ڵ�Ǯ��   Started");
		System.out.println("=================================================");	
		
		Txbt2650 batch;
		
		batch = new Txbt2650();

		
		System.out.println("== OS Name = " + System.getProperty("os.name"));
		System.out.println("== OS Version = " + System.getProperty("os.version"));
		System.out.println("== FILE Separator = " + System.getProperty("file.separator"));
		
		try {
			//Log
			
			//CbUtil.setupLog4jConfig(batch, "log4j.txbt2650.xml");
			//C:/workspace/cyber_ap/classes/
			DOMConfigurator.configure("/workspace/cyber_ap/classes/log4j.txbt2650.xml");
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

			batch.setProcess("2650", "�ϰ����� �ڵ�Ǯ��", "thr_2650");  /* �������� ��� */
			
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
		System.out.println("== ���̹����漼û �ϰ����� �ڵ�Ǯ��   Ended");
		System.out.println("=================================================");	
		
	}


	
	/*[��� ������ ���⿡ �����Ѵ�...]
	 * 1. �ΰ��ڷ� ���� ó��
	 * 2. �ΰ���ġ�� ����ó��
	 * 3. �����ڷ����ó�� ����(FLAG)
	 * 
	 * (�������)
	 *  try ~ catch ���� ����ϴ� ��� TRANSACTION ó���� �ݵ�� throw ó���� �ؾ� ��... 
	 * */
	@Override
	public void transactionJob() {
		// TODO Auto-generated method stub
		
		this.cyberService = (IbatisBaseService) this.getService("baseService");

		/**
		 * �ϰ����ι�ȣ ����� �����´�...
		 */
		Iterator<MapForm> tongList = cyberService.queryForList("TXBT2650.SELECT_TX1301", null).iterator();
		
		/**
		 * �ϰ����� ��ϸ�ŭ �ݺ�
		 */
		 while(tongList.hasNext()) {
			 
			 MapForm tongMap =  tongList.next();
			 log.debug("===============================================");
			 log.info("�ϰ����� ��Ҹ��::" + tongMap);
			 log.debug("===============================================");
			 /**
			  * �ϰ����� �󼼸���� �����´�
			  */
			if (tongMap.getMap("JOB_GB").equals("2")) {
				Iterator<MapForm> seoiList = cyberService.queryForList("TXBT2650.SELECT_TX2312", tongMap).iterator();
				while (seoiList.hasNext()) {
					MapForm seoiMap = seoiList.next();
					cyberService.queryForUpdate("TXBT2650.UPDATE_TX2112", seoiMap);
				}
			} else {
				Iterator<MapForm> detList = cyberService.queryForList("TXBT2650.SELECT_TX1302", tongMap).iterator();
				while (detList.hasNext()) {
					MapForm detMap = detList.next();
					if (tongMap.getMap("JOB_GB").equals("0")) {
						/**
						 * ���漼 ���������� UPDATE �Ѵ�
						 */
						cyberService.queryForUpdate("TXBT2650.UPDATE_TX1102", detMap);

					} else if (tongMap.getMap("JOB_GB").equals("1")) {
						/**
						 * ȯ�氳���δ�� ���������� UPDATE �Ѵ�
						 */
						cyberService.queryForUpdate("TXBT2650.UPDATE_TX2132", detMap);
					}
				}
			}
			 if(tongMap.getMap("JOB_GB").equals("0")||tongMap.getMap("JOB_GB").equals("1")){
			 /**
			  * �ϰ����� �󼼳����� UPDATE �Ѵ�
			  */
			 cyberService.queryForUpdate("TXBT2650.UPDATE_TX1302", tongMap);
			 }
			 /**
			  * �ϰ����� ������ UPDATE �Ѵ�
			  */
			 cyberService.queryForUpdate("TXBT2650.UPDATE_TX1301", tongMap);
		 }
		
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
