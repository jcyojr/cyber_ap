/**
 *  주시스템명 : 부산시 사이버지방세청
 *  업  무  명 : 연계
 *  기  능  명 : 수기고지서 주민세 종합소득할 수납내역 등록(구청별) 연계 메인 
 *     
 *               5분단위로 동작...
 *  클래스  ID : Txdm1272
 *  사용  여부 : 
 *  변경  이력 :
 * ------------------------------------------------------------------------
 *  작성자      	소속  		    일자            Tag           내용
 * ------------------------------------------------------------------------
 *  송동욱       유채널(주)      2011.12.07         %01%         최초작성
 */
package com.uc.bs.cyber.daemon.txdm1272;

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
public class Txdm1272 extends Codm_BaseDaemon{

	private static String govid = "";
	private static String orgcd = "";
	private static String orgdb = "";
	
	private int mainloop = 0;
	
	private int loop_cnt = 0, insert_cnt = 0, update_cnt = 0;
	
	private String soinFilePath = "";
	private String moveFilePath = "";
	
	ArrayList<MapForm> alSoinList;
	
	/**
	 * 생성자
	 */
	public Txdm1272() throws IOException, Exception {
		
		super();

		log = LogFactory.getLog(this.getClass());

		this.loopTerm = 250;	
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Txdm1272 Txdm1272;
		ApplicationContext context  = null;
		
		System.out.println("== OS Name = " + System.getProperty("os.name"));
		System.out.println("== OS Version = " + System.getProperty("os.version"));
		
		try {
			
			// Log
			Txdm1272 = new Txdm1272();
			
			CbUtil.setupLog4jConfig(Txdm1272, "log4j.xml");
			
			try {
				
				String[] xmls = XMLFileReader.getAllFilePathArray(CbUtil.getResourcePath(Txdm1272, "/"), "SERVICE.XML");

				String strSrc = CbUtil.getResourcePath(Txdm1272, "/") + "config/sqlmaps.xml";

				String strDest = CbUtil.getResourcePath(Txdm1272, "/") + "config/sqlmapConfig.xml";

				XmlUtils.setXmlAttributes(Txdm1272, strSrc, strDest, "sqlMapConfig", "sqlMap", "url", xmls);

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
		
			context = new ClassPathXmlApplicationContext("config/Tax-Spring-db.xml");
			
			Txdm1272.setProcess("1272", "수기고지서 주민세 종합소득할 수납내역등록(구청별)", "thr_1272");  /* 업무데몬 등록 */
			
			Txdm1272.setContext(context);
			
			Thread tx1272Thread = new Thread(Txdm1272);
			
			tx1272Thread.setName("thr_1272");
			
			tx1272Thread.start();
			
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
		
		/*초기 실행시 
		 * 1. 특별징수 소인파일을 읽어서 DB에 등록한다.
		 * 2. 특별징수 내역을 구청별로 등록한다.(멀티쓰레드)
		 * */
		
		/*1. 소인파일 등록*/
		readSoinFiles();
		
		/*2. 소인파일 전송(구청별)*/
		int procCnt  = CbUtil.getResourceInt("ApplicationResource", "cyber.hv.count");
		
		if (log.isDebugEnabled()){
			log.debug("기동프로세스 갯수 :: procCnt = " + procCnt);
		}
		
		threadList = new Thread[procCnt];
		
		for(int i = 0; i<procCnt; i++) {
			
			/*
			 * 1. 시청(구청)코드
			 * 2. DB연결정보
			 * 3. Context
			 * 4. 설명
			 *    소인파일을 읽어서 구청별로 입력한다.
			 *    소인파일은 구청별로 생성되지 않고 세목에 따라 하나의 파일만 생성됨
			 *    수기고지서 특별징수 소인파일
			 * */

			govid = CbUtil.getResource("ApplicationResource", "cyber.hv.gov" + i);
			orgcd = CbUtil.getResource("ApplicationResource", "cyber." + govid + ".org_cd");
			orgdb = "JI_" + orgcd;
			
			threadList[i] = new Thread(newProcess(govid, orgdb), "sub_1272thr_" + orgcd);			
			threadList[i].start();
			
		}
	}

	/*10 초 단위로 thick */
	@Override
	public void mainProcess() throws Exception {
		// TODO Auto-generated method stub
		
		mainloop++;
		
		for(int i = 0; i < threadList.length; i++) {
			
			if(!threadList[i].isAlive()) {	// 다시 살려라
				
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
		
		/*250초 단위로 실행*/
		if (mainloop >= 25) {
			
			/*소인파일등록*/
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
	 * 메인 로직...을 호출한다.
	 */
	private Txdm1272_process newProcess(String govId, String orgcd) throws Exception {
		
		Txdm1272_process process = new Txdm1272_process();
		
		process.setContext(appContext);
		process.setGovId(govId);
		process.setDataSource(orgcd);
		
		process.initProcess();		
		
		return process;
	}

	
	/*
	 * 1.주민세 종합소득할 소인파일을 읽어서 파싱한다.
	 * 2.등록 한다.(멀티트랜잭션 호출)
	 * */
	private void readSoinFiles(){
		
		log.info("=====================================================================");
		log.info("=" + this.getClass().getName()+ " readSoinFiles() [주민세 종합소득할 소인파일 파싱] Start =");
		log.info("=====================================================================");
		
		/*소인파일 경로지정*/
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
					
					if(listf[i].getName().lastIndexOf("SI23") != -1 && listf[i].getName().lastIndexOf(".OK") > 0) { /*주민세 종합소득할 파일만 읽어들인다.*/
						
						log.info("주민세 종합소득할소인파일 = " + listf[i].getAbsolutePath());
						log.info("주민세 종합소득할소인파일 = " + listf[i].getName().substring(0, listf[i].getName().lastIndexOf(".")));
						
						queryElapse1 = System.currentTimeMillis();
						
						alSoinList = setFileReader(listf[i].getAbsolutePath().substring(0, listf[i].getAbsolutePath().lastIndexOf(".")));
						
						queryElapse2 = System.currentTimeMillis();
						
						log.info("주민세 종합소득할 소인파일 파싱 시간 : " + CbUtil.formatTime(queryElapse2 - queryElapse1));
						
						txdm1272_Parse_Insert(); /*사이버DB에 입력*/
						
						CbUtil.copyFile(listf[i].getAbsolutePath().substring(0, listf[i].getAbsolutePath().lastIndexOf(".")), moveFilePath + listf[i].getName().substring(0, listf[i].getName().lastIndexOf(".")));
						
						File rFile = new File(listf[i].getAbsolutePath().substring(0, listf[i].getAbsolutePath().lastIndexOf(".")));
						
						if (rFile.exists()) {
							rFile.delete();
						}
						
						listf[i].delete(); /*OK 파일을 삭제한다.*/
						
					}
				}
			}

		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	/*파일을 읽고 ArrayList에 MapForm형태로 저장한다.*/
	private ArrayList<MapForm> setFileReader(String FileName) {
		    	
    	String OS = System.getProperty("os.name");
		
		File readFile = null;
    	
		int FileLen = 0;
		
		if(OS.indexOf("AIX") != -1) {
			FileLen = 512 + 1;   /*데이터(1198) + Line Feed(1) UNIX*/
		}else{
			FileLen = 512 + 2;   /*데이터(1198) + Line Feed(2) WINDOWS*/
		}
    	
    	Txdm1272FieldList Soin_1272_Field = new Txdm1272FieldList();
    	
    	MapForm parseMap = new MapForm();
    	
    	ArrayList<MapForm> alRetrun = new ArrayList<MapForm>();
    	
    	try {
			
    		readFile = new File(FileName);
    		
    		int i = 0;
    		
    		if(readFile.exists()){
    	
    			log.debug("file_name = [" + readFile.getName() + "] file_size = [" + readFile.length() + "]");
    			    			
    			/*
    			 * 한글이 포함되어 있으므로 반드시
    			 * FileInputStream으로 파일을 읽어들인다.
    			 * */
    			FileInputStream fs = new FileInputStream(readFile);
    			
    			StringBuffer sbLine = new StringBuffer();
    			    			
    			byte[] readLine = new byte[FileLen];
    			
    			while (true){
    					
    				if(fs.read(readLine) < 0) {
    					break;
    				}

    				sbLine.append(new String(readLine));
    				        				    				         			    
    			    parseMap = Soin_1272_Field.parseBuff(readLine);

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
	
	
	/*파싱한 자료를 사이버DB에 입력*/
	private void txdm1272_Parse_Insert() {
		
		log.info("=====================================================================");
		log.info("=" + this.getClass().getName()+ " txdm1272_Parse_Insert() [종합소득할 소인파일 등록] Start =");
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

						/*부과*/
						try {
							
							mpSoinList.setMap("TR_TG"     ,  "0");  /*구청전송여부*/
							
							this.getService().queryForInsert("TXDM1272.tx1612_man_insert_receipt", mpSoinList);
							
							insert_cnt++;
							
						}catch (Exception e){
							
							/*중복이 발생하거나 제약조건에 어긋나는 경우체크*/
							if (e instanceof DuplicateKeyException){
								
								update_cnt++;
								log.info("기 등록된 특별징수 소인");
								
							}else{
								log.error("오류데이터 = " + mpSoinList.getMaps());
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
				
				log.info("종합소득할 소인파일등록 연계시간 : " + CbUtil.formatTime(elapseTime2 - elapseTime1));
				log.info("종합소득할 소인파일등록 처리건수:: " + loop_cnt + ", 등록처리::" + insert_cnt + ", 기처리::" + update_cnt);
			}
			
		}catch (Exception e){
			e.printStackTrace();
			throw (RuntimeException) e;
		}
				
	}

}
