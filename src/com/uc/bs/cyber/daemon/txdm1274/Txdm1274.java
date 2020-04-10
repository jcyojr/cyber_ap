/**
 *  �ֽý��۸� : �λ�� ���̹����漼û
 *  ��  ��  �� : ����
 *  ��  ��  �� : ��������� �ֹμ� ���μ��� �������� ���(��û��) ���� ���� 
 *     
 *               5�д����� ����...
 *  Ŭ����  ID : Txdm1274
 *  ���  ���� : 
 *  ����  �̷� :
 * ------------------------------------------------------------------------
 *  �ۼ���      	�Ҽ�  		    ����            Tag           ����
 * ------------------------------------------------------------------------
 *  �۵���       ��ä��(��)      2011.12.07         %01%         �����ۼ�
 */
package com.uc.bs.cyber.daemon.txdm1274;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.dao.DuplicateKeyException;

import com.uc.bs.cyber.CbUtil;
import com.uc.bs.cyber.daemon.Codm_BaseDaemon;
import com.uc.core.MapForm;
import com.uc.core.misc.XMLFileReader;
import com.uc.core.misc.XmlUtils;

/**
 * @author Administrator
 *
 */
public class Txdm1274 extends Codm_BaseDaemon{

	private static String govid = "";
	private static String orgcd = "";
	private static String orgdb = "";
	
	private int mainloop = 0;
	
	private int loop_cnt = 0, insert_cnt = 0, update_cnt = 0;
	
	private String soinFilePath = "";
	private String moveFilePath = "";
	
	ArrayList<MapForm> alSoinList;
	
	/**
	 * ������
	 */
	public Txdm1274() throws IOException, Exception {
		
		super();

		log = LogFactory.getLog(this.getClass());

		this.loopTerm = 250;	
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Txdm1274 Txdm1274;
		ApplicationContext context  = null;
		
		System.out.println("== OS Name = " + System.getProperty("os.name"));
		System.out.println("== OS Version = " + System.getProperty("os.version"));
		
		try {
			
			// Log
			Txdm1274 = new Txdm1274();
			
			CbUtil.setupLog4jConfig(Txdm1274, "log4j.xml");
			
			try {
				
				String[] xmls = XMLFileReader.getAllFilePathArray(CbUtil.getResourcePath(Txdm1274, "/"), "SERVICE.XML");

				String strSrc = CbUtil.getResourcePath(Txdm1274, "/") + "config/sqlmaps.xml";

				String strDest = CbUtil.getResourcePath(Txdm1274, "/") + "config/sqlmapConfig.xml";

				XmlUtils.setXmlAttributes(Txdm1274, strSrc, strDest, "sqlMapConfig", "sqlMap", "url", xmls);

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
		
			context = new ClassPathXmlApplicationContext("config/Tax-Spring-db.xml");
			
			Txdm1274.setProcess("1274", "��������� �ֹμ� ���μ��� �����������(��û��)", "thr_1274");  /* �������� ��� */
			
			Txdm1274.setContext(context);
			
			Thread tx1274Thread = new Thread(Txdm1274);
			
			tx1274Thread.setName("thr_1274");
			
			tx1274Thread.start();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void initProcess() throws Exception {
		// TODO Auto-generated method stub
		
		log.debug("=====================================================================");
		log.debug("== " + this.getClass().getName() +  " initProcess() ==");
		log.debug("=====================================================================");
		
		/*�ʱ� ����� 
		 * 1. Ư��¡�� ���������� �о DB�� ����Ѵ�.
		 * 2. Ư��¡�� ������ ��û���� ����Ѵ�.(��Ƽ������)
		 * */
		
		/*1. �������� ���*/
		readSoinFiles();
		
		/*2. �������� ����(��û��)*/
		int procCnt  = CbUtil.getResourceInt("ApplicationResource", "cyber.hv.count");
		
		if (log.isDebugEnabled()){
			log.debug("�⵿���μ��� ���� :: procCnt = " + procCnt);
		}
		
		threadList = new Thread[procCnt];
		
		for(int i = 0; i<procCnt; i++) {
			
			/*
			 * 1. ��û(��û)�ڵ�
			 * 2. DB��������
			 * 3. Context
			 * 4. ����
			 *    ���������� �о ��û���� �Է��Ѵ�.
			 *    ���������� ��û���� �������� �ʰ� ���� ���� �ϳ��� ���ϸ� ������
			 *    ��������� Ư��¡�� ��������
			 * */

			govid = CbUtil.getResource("ApplicationResource", "cyber.hv.gov" + i);
			orgcd = CbUtil.getResource("ApplicationResource", "cyber." + govid + ".org_cd");
			orgdb = "JI_" + orgcd;
			
			threadList[i] = new Thread(newProcess(govid, orgdb), "sub_1272thr_" + orgcd);			
			threadList[i].start();
			
		}
	}

	/*10 �� ������ thick */
	@Override
	public void mainProcess() throws Exception {
		// TODO Auto-generated method stub
		
		mainloop++;
		
		for(int i = 0; i < threadList.length; i++) {
			
			if(!threadList[i].isAlive()) {	// �ٽ� �����
				
				log.debug("=====================================================================");
				log.debug("== " + this.getClass().getName()+ " mainProcess(" + i + ") ==");
				log.debug("=====================================================================");
				
				log.debug(" Thread(" + threadList[i].getName() + ") is " + threadList[i].isAlive());
				
				govid = CbUtil.getResource("ApplicationResource", "cyber.hv.gov" + i);
				orgcd = CbUtil.getResource("ApplicationResource", "cyber." + govid + ".org_cd");
				orgdb = "JI_" + orgcd;
				
				try {
					threadList[i] = new Thread(newProcess(govid, orgdb), "sub_1271thr_" + orgcd);		
					threadList[i].start();
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}			
				
			}
			
		}
		
		/*250�� ������ ����*/
		if (mainloop >= 25) {
			
			/*�������ϵ��*/
			mainloop = 0;
			readSoinFiles();
		}
		
	}

	@Override
	public void onInterrupt() throws Exception {
		// TODO Auto-generated method stub
		if(threadList == null) return;
		
		for(int i = 0; i<threadList.length; i++) {
			threadList[i].interrupt();
		}
	}

	@Override
	public void transactionJob() {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * @param govId
	 * @return
	 * @throws Exception
	 * ���� ����...�� ȣ���Ѵ�.
	 */
	private Txdm1274_process newProcess(String govId, String orgcd) throws Exception {
		
		Txdm1274_process process = new Txdm1274_process();
		
		process.setContext(appContext);
		process.setGovId(govId);
		process.setDataSource(orgcd);
		
		process.initProcess();		
		
		return process;
	}

	
	/*
	 * 1.�ֹμ� ���μ��� ���������� �о �Ľ��Ѵ�.
	 * 2.��� �Ѵ�.(��ƼƮ����� ȣ��)
	 * */
	private void readSoinFiles(){
		
		log.info("=====================================================================");
		log.info("=" + this.getClass().getName()+ " readSoinFiles() [�ֹμ� ���μ��� �������� �Ľ�] Start =");
		log.info("=====================================================================");
		
		/*�������� �������*/
		soinFilePath = "/app/data/etaxplus-ap/recv/";
		moveFilePath = "/app/data/etaxplus-ap/archive/RCPT_SPECIAL/";
		
		int file_cnt = 0;
		
		File readFile = null;
		
		readFile = new File(soinFilePath);
	
		long queryElapse1 = 0;
		long queryElapse2 = 0;
		
		try{
			
			File[] listf = readFile.listFiles();
						
			for(int i =0 ; i < listf.length ; i++) {
				
				if(listf[i].isFile()) {
					
					file_cnt ++;
					
					if(listf[i].getName().lastIndexOf("SI22") != -1 && listf[i].getName().lastIndexOf(".OK") > 0) { /*�ֹμ� ���ռҵ��� ���ϸ� �о���δ�.*/
						
						log.info("�ֹμ� ���μ��Ҽ������� = " + listf[i].getAbsolutePath());
						log.info("�ֹμ� ���μ��Ҽ������� = " + listf[i].getName().substring(0, listf[i].getName().lastIndexOf(".")));
						
						queryElapse1 = System.currentTimeMillis();
						
						alSoinList = setFileReader(listf[i].getAbsolutePath().substring(0, listf[i].getAbsolutePath().lastIndexOf(".")));
						
						queryElapse2 = System.currentTimeMillis();
						
						log.info("�ֹμ� ���μ��� �������� �Ľ� �ð� : " + CbUtil.formatTime(queryElapse2 - queryElapse1));
						
						txdm1274_Parse_Insert(); /*���̹�DB�� �Է�*/
						
						CbUtil.copyFile(listf[i].getAbsolutePath().substring(0, listf[i].getAbsolutePath().lastIndexOf(".")), moveFilePath + listf[i].getName().substring(0, listf[i].getName().lastIndexOf(".")));
						
						File rFile = new File(listf[i].getAbsolutePath().substring(0, listf[i].getAbsolutePath().lastIndexOf(".")));
						
						if (rFile.exists()) {
							rFile.delete();
						}
						
						listf[i].delete(); /*OK ������ �����Ѵ�.*/
						
					}
				}
			}

		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	/*������ �а� ArrayList�� MapForm���·� �����Ѵ�.*/
	private ArrayList<MapForm> setFileReader(String FileName) {
		    	
    	String OS = System.getProperty("os.name");
		
		File readFile = null;
    	
		int FileLen = 0;
		
		if(OS.indexOf("AIX") != -1) {
			FileLen = 616 + 1;   /*������(616) + Line Feed(1) UNIX*/
		}else{
			FileLen = 616 + 2;   /*������(616) + Line Feed(2) WINDOWS*/
		}
    	
    	Txdm1274FieldList Soin_1274_Field = new Txdm1274FieldList();
    	
    	MapForm parseMap = new MapForm();
    	
    	ArrayList<MapForm> alRetrun = new ArrayList<MapForm>();
    	
    	try {
			
    		readFile = new File(FileName);
    		
    		int i = 0;
    		
    		if(readFile.exists()){
    	
    			log.debug("file_name = [" + readFile.getName() + "] file_size = [" + readFile.length() + "]");
    			    			
    			/*
    			 * �ѱ��� ���ԵǾ� �����Ƿ� �ݵ��
    			 * FileInputStream���� ������ �о���δ�.
    			 * */
    			FileInputStream fs = new FileInputStream(readFile);
    			
    			StringBuffer sbLine = new StringBuffer();
    			    			
    			byte[] readLine = new byte[FileLen];
    			
    			while (true){
    					
    				if(fs.read(readLine) < 0) {
    					break;
    				}

    				sbLine.append(new String(readLine));
    				        				    				         			    
    			    parseMap = Soin_1274_Field.parseBuff(readLine);

    			    alRetrun.add(parseMap);
    			    
			    	log.debug("Parse = " + parseMap.getMaps());
			    	
    			    sbLine.delete(0, FileLen);
    			    
    				i++;
    			}
    			
    			fs.close();
    		}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		return alRetrun;
    }		
	
	
	/*�Ľ��� �ڷḦ ���̹�DB�� �Է�*/
	private void txdm1274_Parse_Insert() {
		
		log.info("=====================================================================");
		log.info("=" + this.getClass().getName()+ " txdm1274_Parse_Insert() [���μ��� �������� ���] Start =");
		log.info("=====================================================================");
				
		long elapseTime1 = 0;
		long elapseTime2 = 0;
		
		int LevyCnt = 0;
		
		loop_cnt = 0; 
		insert_cnt = 0;
		update_cnt = 0;
		
		MapForm mpSoinList = new MapForm();
		
		try {

			LevyCnt = alSoinList.size();

			if (LevyCnt  >  0)   {

				elapseTime1 = System.currentTimeMillis();
				
				for (int i = 0;  i < LevyCnt;  i++)   {
					
					mpSoinList =  alSoinList.get(i);
					
					if (mpSoinList == null  ||  mpSoinList.isEmpty() )   {
						continue;
					}
					
					loop_cnt++;

					try {

						/*�ΰ�*/
						try {
							
							mpSoinList.setMap("TR_TG"     ,  "0");  /*��û���ۿ���*/
							
							this.getService().queryForInsert("TXDM1274.tx1616_man_insert_receipt", mpSoinList);
							
							insert_cnt++;
							
						}catch (Exception e){
							
							/*�ߺ��� �߻��ϰų� �������ǿ� ��߳��� ���üũ*/
							if (e instanceof DuplicateKeyException){
								
								update_cnt++;
								log.info("�� ��ϵ� ���μ��� ����");
								
							}else{
								log.error("���������� = " + mpSoinList.getMaps());
								e.printStackTrace();							
								throw (RuntimeException) e;
							}
						}

					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						throw (RuntimeException) e;
					}

				}
				
				elapseTime2 = System.currentTimeMillis();
				
				log.info("���μ��� �������ϵ�� ����ð� : " + CbUtil.formatTime(elapseTime2 - elapseTime1));
				log.info("���μ��� �������ϵ�� ó���Ǽ�:: " + loop_cnt + ", ���ó��::" + insert_cnt + ", ��ó��::" + update_cnt);
			}
			
		}catch (Exception e){
			e.printStackTrace();
			throw (RuntimeException) e;
		}
				
	}

}