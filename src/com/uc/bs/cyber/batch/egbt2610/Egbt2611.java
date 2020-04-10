/**
 *  주시스템명 : 부산시 사이버지방세청
 *  업  무  명 : 배치
 *  기  능  명 : 전자고지 자료구축
 *               배치로 구동되며 저녁 9시에 한번만 
 *  클래스  ID : Egbt2610
 *  변경  이력 :
 * ------------------------------------------------------------------------
 *  작성자      	소속  		    일자            Tag           내용
 * ------------------------------------------------------------------------
 *  김대완       유채널(주)      2011.05.30         %01%         최초작성
 */
package com.uc.bs.cyber.batch.egbt2610;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import tm.massmail.sendapi.ThunderMassMail;
import tm.massmail.sendapi.ThunderMassMailSender;

import antlr.collections.List;

import com.ibatis.sqlmap.client.SqlMapException;
import com.uc.bs.cyber.CbUtil;
import com.uc.core.MapForm;
import com.uc.core.misc.XMLFileReader;
import com.uc.core.misc.XmlUtils;
import com.uc.core.spring.service.IbatisService;
/**
 * @author Administrator
 *
 */
public class Egbt2611 {
	
	private Log log ;

	private ApplicationContext context  = null;
	
	private IbatisService sqlService    = null;
	
	private ArrayList<String> sgList = null;

	private MapForm paramMap;
	/**
	 * 
	 */
	public Egbt2611() {
		// TODO Auto-generated constructor stub
		super();
		
		log = LogFactory.getLog(this.getClass());

		String strSgList = CbUtil.getResource("ApplicationResource", "cyber.lt.sglist");
		
		StringTokenizer tok = new StringTokenizer(strSgList, ",");
		
		
		/**
		 * 
		 */
		sgList =  new ArrayList<String>();
		
		while(tok.hasMoreElements()) {
		
			sgList.add( tok.nextToken());
		}
		
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		
		if(args.length != 4) {
			System.err.println("부과전송 파라메터 입력안됨!!");
			
			System.err.println(" 사용법 :: Egbt2611  [부과년도] [부과월] [고지구분(1:전자고지, 2:자동이체안내)] [전자고지순번] ");
			
			return;
		}
		
		System.out.println("=========================================================");
		System.out.println("===   사이버지방세청 전자고지 자료구축및 연계  Start   ==");
		System.out.println("=========================================================");	
		
		Egbt2611 batch = new Egbt2611();
		
		MapForm paramMap = new MapForm();
		
		paramMap.setMap("NOTI_YY", args[0]);  // 부과년
		paramMap.setMap("NOTI_MM", args[1]);  // 부과월
		//paramMap.setMap("KWA_CD", args[2]);   // 과목코드
		paramMap.setMap("NOTI_GB", args[2]);  // 고지구분(1:전자고지, 2:자동이체안내)
		//paramMap.setMap("DUE_DT", args[4]);   // 납기일자(영업일)
		paramMap.setMap("NOTI_SNO", Long.parseLong(args[3])); // 전자고지일련번호(구축된 일련번호)
		
		batch.paramMap = paramMap;
		
		System.out.println("== OS Name = " + System.getProperty("os.name"));
		System.out.println("== OS Version = " + System.getProperty("os.version"));
		
		try {
			//Log
			
			CbUtil.setupLog4jConfig(batch, "log4j.egbt2611.xml");
			
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
		
			batch.context = new ClassPathXmlApplicationContext("config/Single-Spring-db.xml");


			batch.mainProcess();			
			
			
		} catch (SqlMapException se) {
			se.printStackTrace();
		} catch (BeansException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}		
		
		System.out.println("=========================================================");
		System.out.println("===    사이버지방세청 전자고지 자료구축및 연계  End    ==");
		System.out.println("=========================================================");	


	}
	
	/**
	 * 메인 처리
	 */
	private void mainProcess() {
		
		
		if(sqlService == null) sqlService = (IbatisService) context.getBean("ibatisService");
		
		// 3. 전자고지 자료를 발송한다..
	
		try {
			sendEnotify();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	
	/**
	 * 썬더메일에 전자고지 자료 전송및 결과 Update
	 * @throws Exception
	 */
	private void sendEnotify() throws Exception {
	
		String taxNm = sqlService.getOneFieldString("EGBT2610.SELECT_CO3301_KWANAME", paramMap.getMap("NOTI_MM"));
		
		ThunderMassMailSender tms = new ThunderMassMailSender(); 
		ThunderMassMail tm = new ThunderMassMail();               

		tm.setThunderMailURL(CbUtil.getResource("ApplicationResource", "enoti.thundermail.url"));			//썬더메일UI url
		tm.setWriter (CbUtil.getResource("ApplicationResource", "enoti.thundermail.user"));					//메일 작성자 ID

		tm.setContentType("template");
		
		//메일 내용 타입 content: 메일 내용 직접 전송, template: 메일 템플릿 사용
		// tm.setMailContent("[$name]님의 생일을 진심으로 축하합니다."); 
		//setContentType이 content 일때 메일 내용 필수

		tm.setSenderEmail(CbUtil.getResource("ApplicationResource", "enoti.thundermail.smail")); 			//보내는 사람 이메일
		tm.setSenderName(CbUtil.getResource("ApplicationResource", "enoti.thundermail.sname"));				//보내는 사람 이름
		tm.setReceiverName("[$name]");				//받는 사람 이름
		
		String NOTI_GB=(String)paramMap.getMap("NOTI_GB"); //고지구분

		//전자고지
		if(NOTI_GB.equals("1")) {
			tm.setMailTitle("[부산광역시 ETAX] [$name]님 " 
					+ paramMap.getMap("NOTI_YY") + "년 " 
					+ paramMap.getMap("NOTI_MM") + "월 "
					+ taxNm + " 정기분 전자고지 안내");			//메일 제목
			//tm.setTemplate_id(CbUtil.getResource("ApplicationResource", "enoti.thundermail.template4"  ));
			tm.setTemplate_id("12");
		}
		
		//자동이체
		if(NOTI_GB.equals("2")) {
			tm.setMailTitle("[부산광역시 ETAX] [$name]님 " 
					+ paramMap.getMap("NOTI_YY") + "년 " 
					+ paramMap.getMap("NOTI_MM") + "월 "
					+ taxNm + " 정기분 자동이체 안내");			//메일 제목
			//tm.setTemplate_id(CbUtil.getResource("ApplicationResource", "enoti.thundermail.template5"  ));
			tm.setTemplate_id("13");
			
		}

		// tm.setContentType("content"); 
		
		
		tm.setTargetType("string");	

		//일대일치환 정보
		//사용 가능한 일대일 치환명
		//[$email],[$cellPhone],[$name],[$customerID],[$etc1]~[$etc16]
		// tm.setFileOneToOne("[$email]≠이메일ø[$name]≠납세자명");
		//tm.setFileOneToOne("[$email]≠이메일ø[$name]≠납세자명ø[$etc1]≠과세기관ø[$etc2]≠전자납부번호ø[$etc3]≠과세대상ø[$etc4]≠납기일자");
		tm.setFileOneToOne("[$email]≠이메일ø[$name]≠납세자명ø[$etc1]≠과세기관ø[$etc2]≠전자납부번호ø[$etc3]≠납부금액ø[$etc4]≠납기일자ø[$etc5]≠과세대상");
		
		//대상자 등록 유형 
		//string : string형태로 파라미터에 직접 전송, query : 대상자 추출 쿼리문 전송
		
		/**
		 * 전송이 완료될때까지 반복한다...
		 */
		while(sqlService.queryForUpdate("EGBT2610.UPDATE_ME1151_SENDING", paramMap) > 0) {
			//StringBuffer targetStr = new StringBuffer("이메일,납세자명,과세기관,전자납부번호,과세대상,납기일자");
			StringBuffer targetStr = new StringBuffer("이메일,납세자명,과세기관,전자납부번호,납부금액,납기일자,과세대상");
			
			ArrayList<MapForm> sendList = sqlService.queryForList("EGBT2610.SELECT_ME1151", paramMap);
			
			Iterator<MapForm> sendLoop = sendList.iterator();
			
			while(sendLoop.hasNext()) {
				MapForm sendMap  = sendLoop.next();
				
				targetStr.append("Æ" + sendMap.getMap("E_MAIL") + "," + sendMap.getMap("MEM_NM") + "," + sendMap.getMap("NOTI_STD"));
				
			}
			
			tm.setTargetString(targetStr.toString());
			String result = tms.send(tm);			//메일 발송, 연동 결과 리턴
			
			log.debug("MAIL SEND STR=" + targetStr);
			
			// System.out.println("MAIL SEND RESULT==" + result);
			StringTokenizer tok = new StringTokenizer(result, "||");
			
			paramMap.setMap("TR_RES", tok.nextToken());
			if(tok.hasMoreTokens())	paramMap.setMap("MNG_NO", tok.nextToken());
			
			if(paramMap.getMap("TR_RES").equals("-100")) paramMap.setMap("TRTG", "1");
			else paramMap.setMap("TRTG", "9");
			
			// 썬더메일 연계전송 결과 Update
			int sendCnt = sqlService.queryForUpdate("EGBT2610.UPDATE_ME1151_SENDRESULT", paramMap);

			log.info("메일발송 결과="+ result + " 건수==" + sendCnt);
		}
	
	}
	
	
}
