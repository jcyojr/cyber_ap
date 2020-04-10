/**
 *  주시스템명 : 부산시 사이버지방세청
 *  업  무  명 : 일괄납부 자동풀림
 *  기  능  명 : 일괄납부 신청된 내역중 당일에 납부되지 않은 내역을 해제한다
 *               배치로 구동되며 자정에 한번만 
 *  클래스  ID : Txbt2650
 *  변경  이력 :
 * ------------------------------------------------------------------------
 *  작성자      	소속  		    일자            Tag           내용
 * ------------------------------------------------------------------------
 *  김대완       유채널(주)      2011.07.20         %01%         최초작성
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
	 * 생성자
	 */
	public Txbt2650() {
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
		System.out.println("== 사이버지방세청 일괄납부 자동풀림   Started");
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

			batch.setProcess("2650", "일괄납부 자동풀림", "thr_2650");  /* 업무데몬 등록 */
			
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
		System.out.println("== 사이버지방세청 일괄납부 자동풀림   Ended");
		System.out.println("=================================================");	
		
	}


	
	/*[모든 업무를 여기에 구성한다...]
	 * 1. 부과자료 연계 처리
	 * 2. 부가가치세 업무처리
	 * 3. 수납자료삭제처리 업무(FLAG)
	 * 
	 * (참고사항)
	 *  try ~ catch 문을 사용하는 경우 TRANSACTION 처리시 반드시 throw 처리를 해야 함... 
	 * */
	@Override
	public void transactionJob() {
		// TODO Auto-generated method stub
		
		this.cyberService = (IbatisBaseService) this.getService("baseService");

		/**
		 * 일괄납부번호 목록을 가져온다...
		 */
		Iterator<MapForm> tongList = cyberService.queryForList("TXBT2650.SELECT_TX1301", null).iterator();
		
		/**
		 * 일괄납부 목록만큼 반복
		 */
		 while(tongList.hasNext()) {
			 
			 MapForm tongMap =  tongList.next();
			 log.debug("===============================================");
			 log.info("일괄납부 취소목록::" + tongMap);
			 log.debug("===============================================");
			 /**
			  * 일괄납부 상세목록을 가져온다
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
						 * 지방세 개별내역을 UPDATE 한다
						 */
						cyberService.queryForUpdate("TXBT2650.UPDATE_TX1102", detMap);

					} else if (tongMap.getMap("JOB_GB").equals("1")) {
						/**
						 * 환경개선부담금 개별내역을 UPDATE 한다
						 */
						cyberService.queryForUpdate("TXBT2650.UPDATE_TX2132", detMap);
					}
				}
			}
			 if(tongMap.getMap("JOB_GB").equals("0")||tongMap.getMap("JOB_GB").equals("1")){
			 /**
			  * 일괄납부 상세내역을 UPDATE 한다
			  */
			 cyberService.queryForUpdate("TXBT2650.UPDATE_TX1302", tongMap);
			 }
			 /**
			  * 일괄납부 내역을 UPDATE 한다
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
