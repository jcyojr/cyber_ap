/**
 *  주시스템명 : 부산시청 사이버 지방세청
 *  업  무  명 : 연계
 *  기  능  명 : 환경개선부담금 실시간 수납 전송
 *  클래스  ID : Txdm2462_process : 실 처리로직을 작성하는 클래스
 *  변경  이력 :
 * ------------------------------------------------------------------------
 *  작성자      	소속  		    일자            Tag           내용
 * ------------------------------------------------------------------------
 *  최유환       유채널(주)      2013.11.14         %01%         최초작성
 *  
 */
package com.uc.bs.cyber.daemon.txdm2462;

import java.util.ArrayList;
import java.util.StringTokenizer;

import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.w3c.dom.Document;

import com.uc.bs.cyber.CbUtil;
import com.uc.bs.cyber.WsdlUtil;
import com.uc.bs.cyber.daemon.Codm_BaseProcess;
import com.uc.bs.cyber.daemon.Codm_interface;
import com.uc.core.MapForm;
import com.uc.core.spring.service.UcContextHolder;


/**
 * @author Administrator
 *
 */
public class Txdm2462_process extends Codm_BaseProcess implements Codm_interface {


	private MapForm dataMap   = null;

	private String sysDsc     = null;
	private String target     = null;
	private String ifId_vir   = null;
	private String srcOrg     = null;
	private String desOrg     = null;
	private String svcTm      = null;
	
	private int[] svcTms = new int[2];
	
	/**
	 * 
	 */
	public Txdm2462_process() {
		
		super();
		
		log = LogFactory.getLog(this.getClass());
		
		/**
		 * 5 분마다 돈다
		 */
		loopTerm = 60 * 5;
	}
	
	/*Context 주입용*/
	public void setApp(ApplicationContext context) {
		this.context = context;
	}
	
	
	/* process starting */
	@Override
	public void runProcess() throws Exception {
		

		try {
			
			
			/*여기서 시간을 조절해야 한다...*/
			try{
				svcTm = CbUtil.getResource("ApplicationResource" ,"wsdl.svctime.env");
				
				StringTokenizer tok = new StringTokenizer(svcTm, "~");	
				
				svcTms[0] = Integer.parseInt(tok.nextToken());
				svcTms[1] = Integer.parseInt(tok.nextToken());
				
			} catch (Exception e) {
				
				e.printStackTrace();
				
				svcTms[0] = 0500;
				svcTms[1] = 2130;
			}
			
			
			//int curTime = Integer.parseInt(CbUtil.getCurDateTimes().substring(0, 4));
			
			//if (curTime > svcTms[0] && curTime < svcTms[1]) {

				//log.info("========================================");
				//log.info("======    서비스 개시 :: " + curTime + "    =======");
				//log.info("========================================");

				//do {
					//실전송업무 시작
			//		int procCnt = txdm2462_JobProcess();
					
					//if(procCnt == 0) {
					//	break;	
					//}

				//}while(true);
				
			//} else {

				//do {
					//실전송업무 시작
			
			int procCnt = txdm2462_JobProcess();
					
				//	if(procCnt == 0) {
				//		break;	
				//	}
//
				//}while(true);
				
				//log.info("========================================");
				//log.info("======    서비스 종료 :: " + curTime + "    =======");
				//log.info("========================================");
			//}

		} catch (Exception e) {
		
			e.printStackTrace();
		}			


	}

	/*현 사용할 일이 없넹...*/
	@Override
	public void setDatasrc(String datasrc) {

		this.dataSource = datasrc;
	}

	/*트랜잭션 구성*/
	@Override
	public void transactionJob() {
			
	}
	
	
	private int txdm2462_JobProcess() {
		
		//log.info("=======================환경개선부담금 실시간 수납 처리 =======================");
		//log.info("=" + this.getClass().getName()+ " txdm2462_JobProcess() Start =");
		//log.info("=====================================================================");
		
		/*트랜잭션을 포함하는 실업무는 여기서 구현한다...*/
		/*실업무를 구현합니다.*/
		
		/*기본설정값 ================================================================*/
		
		//테스트코드(실제운영시는 삭제)
		//String resourceKey = "wsdl.url2.test";
		String resourceKey = "wsdl.system.env";

		// System 구분 가져오기
		sysDsc = CbUtil.getResource("ApplicationResource" ,resourceKey);
		
		if(sysDsc.equals("test")) resourceKey = "wsdl.url.test.env";
		else resourceKey = "wsdl.url.ensntg." + this.c_slf_org;
		
		// 대상 서버 정보
		target = CbUtil.getResource("ApplicationResource" ,resourceKey);
		
		// SRC ORGCD (6260000)
		srcOrg = CbUtil.getResource("ApplicationResource" ,"wsdl.srcid.env"); 
		 
		resourceKey = "wsdl.dest." + this.c_slf_org;
		
		// 대상 ORGCD
		desOrg = CbUtil.getResource("ApplicationResource" ,resourceKey); 


		//ifId_vir :: SOINN00009 연계ID
		ifId_vir = CbUtil.getResource("ApplicationResource" ,"wsdl.ifid.sntg.env"); 
					
		/*기본설정값 ================================================================*/
		int err1 = 0, err2 = 0, msgIdx = 0;
		int updCnt = 0;

		WsdlUtil wsdl = new WsdlUtil();
		
		Document xmlDoc = null;
		
		dataMap = new MapForm();

		/**
		 * 1. 실시간 수납자료 전송
		 */
		
		//구청별로 가져오지 않고 전체를 가져와서 5분단위로 전송함...
		dataMap.setMap("SGG_COD", this.c_slf_org);  /*구청코드맵핑*/
		dataMap.setMap("TRTG", "1");
		
		@SuppressWarnings("unused")
		String rdmKey = (String.valueOf(Math.random())).substring(2, 10);
		String msgKey = CbUtil.getCurDateTimes();

		/*신규 수납 미전송건*/
		ArrayList<MapForm> sunapList = new ArrayList<MapForm>();
			
		sunapList = cyberService.queryForList("TXDM2462.getSunapDataList", dataMap);
		
		//log.info("[" + this.c_slf_org_nm + "]환경개선부담금 수납 건수 = " + sunapList.size());
		
		if(sunapList.size() > 0){
			
			//log.debug("SERVICE TIME==" + svcTms[0] + ", " + svcTms[1]);
			
			if(log.isInfoEnabled()){
				log.info("================WSDL 정보=================");
				log.info("resourceKey = [" + resourceKey + "]");
				log.info("sysDsc      = [" + sysDsc + "]");
				log.info("target      = [" + target + "]");
				log.info("srcOrg      = [" + srcOrg + "]");
				log.info("desOrg      = [" + desOrg + "]");
				log.info("ifId_vir    = [" + ifId_vir + "]");				
				log.info("================WSDL 정보=================");
			}
			
			for(int i = 0; i < sunapList.size(); i++){
				
				dataMap = new MapForm();
				
				//log.info("sunapList :: " + sunapList);
				
				MapForm mfVirNoList = sunapList.get(i);
				
				//log.info("mfVirNoList :: " + mfVirNoList);
				
				if(mfVirNoList==null || mfVirNoList.isEmpty()){
					continue;
				}
				
				
				dataMap.setMap("IN_CNT"              , "1"); 
				
				MapForm listMap = new MapForm();
							
				listMap.setMap("IN_APPLY_YMD"        , mfVirNoList.getMap("IN_APPLY_YMD").toString());        // 배열접수일자
				listMap.setMap("IN_SF_TEAM_CODE"     , mfVirNoList.getMap("IN_SF_TEAM_CODE").toString());     // 자치단체코드
				listMap.setMap("IN_TAX_CODE"         , mfVirNoList.getMap("IN_TAX_CODE").toString());         // 세목 자동차:12595921 시설물:12595922
				// IN_LVY_NO :: 년도 + 0 + 기분(1,2) + 1 + 시도 + 시구군 + 행정동 + 과세번호 :: 21자리
				listMap.setMap("IN_LVY_NO"           , mfVirNoList.getMap("IN_LVY_NO").toString());		      // 과세번호
				listMap.setMap("IN_ANC_CHASU"        , mfVirNoList.getMap("IN_ANC_CHASU").toString());        // 고지차수
				listMap.setMap("IN_RECPT_YMD"        , mfVirNoList.getMap("IN_RECPT_YMD").toString());        // 수납일자
				listMap.setMap("IN_RECPT_AMT"        , mfVirNoList.getMap("IN_RECPT_AMT").toString());        // 수납금액
				// IN_PERD :: 년도 + 기분(1,2) 5자리
				listMap.setMap("IN_PERD"             , mfVirNoList.getMap("IN_PERD").toString());             // 부과기분
				listMap.setMap("IN_OWNR_SID"         , mfVirNoList.getMap("IN_OWNR_SID").toString());         // 납부자번호
				listMap.setMap("IN_RECPT_BANK_NM"    , mfVirNoList.getMap("IN_RECPT_BANK_NM").toString());    // 은행명
				listMap.setMap("IN_BANK_CODE"        , mfVirNoList.getMap("IN_BANK_CODE").toString());        // 은행코드
				listMap.setMap("IN_TRANSFER_YMD"     , mfVirNoList.getMap("IN_TRANSFER_YMD").toString());     // 이체일자
				listMap.setMap("IN_ACCT_YMD"         , mfVirNoList.getMap("IN_ACCT_YMD").toString());         // 회계일자
				listMap.setMap("IN_RECPT_GBN"        , mfVirNoList.getMap("IN_RECPT_GBN").toString());        // 수납구분 시금고:1, 금결원:4, 자동이체:5, 가상계좌:6, 신용카드:7, ARS(소액결제):8 
				listMap.setMap("IN_RECPT_GUBUN"      , mfVirNoList.getMap("IN_RECPT_GUBUN").toString());	  // 처리구분 수납:1, 수납취소:2	
				listMap.setMap("IN_RECPT_CANCEL_YMD" , mfVirNoList.getMap("IN_RECPT_CANCEL_YMD").toString()); // 수납취소일자				
				listMap.setMap("IN_RECPT_CANCEL_AMT" , mfVirNoList.getMap("IN_RECPT_CANCEL_AMT").toString()); // 수납취소금액
							
				listMap.setMap("IN_LAST_MOD_TS"      , " ");                                                  // 처리일시         - 미입력
				listMap.setMap("IN_RFLT_STATE_CODE"  , " ");                                                  // 수납처리구분코드 - 미입력
				listMap.setMap("IN_RECPT_ERR_CODE"   , " ");			                                      // 수납에러코드     - 미입력
				
				dataMap.setMap("list", listMap);
							
				
				try {
					msgIdx ++;
					//log.info("웹서비스 시작");
					xmlDoc  = wsdl.sendNRcv(ifId_vir, target, srcOrg, desOrg, msgKey + CbUtil.fillZero(Integer.toString(msgIdx), 8), dataMap);
					
					//log.info("xmlDoc :: " + xmlDoc);
					
					if(xmlDoc.getElementsByTagName("env:Fault") != null && xmlDoc.getElementsByTagName("env:Fault").getLength() > 0){
					
						log.info("Fault Code ==" + xmlDoc.getElementsByTagName("faultcode").item(0).getFirstChild().getNodeValue());
						log.info("Fault Mesg ==" + xmlDoc.getElementsByTagName("faultstring").item(0).getFirstChild().getNodeValue());
						log.info("오류데이터 = " + mfVirNoList.getMaps());
						
						mfVirNoList.setMap("TRTG", "9");	/* 웹서비스 시스템 오류코드셋팅 */
						
						cyberService.queryForUpdate("TXDM2462.updateWsdlTrtgData", mfVirNoList);
						err2 ++;
						
					}else{
						
						String resCd = xmlDoc.getElementsByTagName("res_cnt").item(0).getFirstChild().getNodeValue();
						
						// 완료
						if(resCd.equals("1")) {
							
							/*************  송신상태를 UPDATE 한다. ***********/							
							mfVirNoList.setMap("TRTG", "1");	/* 웹서비스 시스템 전송상태셋팅 - 성공 */
							
							cyberService.queryForUpdate("TXDM2462.updateWsdlTrtgData", mfVirNoList);
							
							//log.info(" 웹서비스 시스템 전송상태셋팅 TRTG == 1");
							updCnt++;

						}else{  // 처리 오류
							
							log.info("환경개선부담금 실시간 수납 웹서비스전송 에러 ");
							log.info("MSG_KEY=" + msgKey);
							log.info("오류데이터 = " + mfVirNoList.getMaps());

							mfVirNoList.setMap("TRTG", "8");	/* 웹서비스 시스템 전송상태셋팅 */
							cyberService.queryForUpdate("TXDM2462.updateWsdlTrtgData", mfVirNoList);
							
							err1++;
							
						}

					}

					
				} catch (Exception e) {
					log.error("오류데이터 = " + mfVirNoList.getMaps());
					e.printStackTrace();
				}
				
			}
			
			log.info("실시간수납 전송 COUNT(전체)==" + msgIdx + ", 완료 : "+updCnt+", ERR1(웹서비스오류)==" + err1 + ", ERR2(오류코드세팅)==" + err2);
			
			/***********************************************************************************/
			try{
				MapForm daemonMap = new MapForm();
				daemonMap.setMap("DAEMON"    , "TXDM2462");
				daemonMap.setMap("DAEMON_NM" , "환경개선부담금 실시간수납(WSDL)전송");
				daemonMap.setMap("SGG_COD"   , c_slf_org);
				daemonMap.setMap("TOTAL_CNT" , msgIdx);
				daemonMap.setMap("INSERT_CNT", updCnt);
				daemonMap.setMap("UPDATE_CNT", 0);
				daemonMap.setMap("DELETE_CNT", 0);
				daemonMap.setMap("ERROR_CNT" , err1 + err2);
				cyberService.queryForInsert("CODMBASE.InsertDaemonProcessCheck", daemonMap);
			}catch(Exception ex){
				log.debug("환경개선부담금 실시간수납(WSDL)전송 대상이 없습니다. 등록 오류");
			}				
			/***********************************************************************************/
		
		}else{
			
			/***********************************************************************************/
			try{
				MapForm daemonMap = new MapForm();
				daemonMap.setMap("DAEMON"    , "TXDM2462");
				daemonMap.setMap("DAEMON_NM" , "환경개선부담금 실시간수납(WSDL)전송");
				daemonMap.setMap("SGG_COD"   , c_slf_org);
				daemonMap.setMap("TOTAL_CNT" , 0);
				daemonMap.setMap("INSERT_CNT", 0);
				daemonMap.setMap("UPDATE_CNT", 0);
				daemonMap.setMap("DELETE_CNT", 0);
				daemonMap.setMap("ERROR_CNT" , 0);
				cyberService.queryForInsert("CODMBASE.InsertDaemonProcessCheck", daemonMap);
			}catch(Exception ex){
				log.debug("환경개선부담금 실시간수납(WSDL)전송 대상이 없습니다. 등록 오류");
			}				
			/***********************************************************************************/
			//log.info("실시간 수납 전송 자료가 없습니다.");
		}
		
		return sunapList.size();
		
	}

}
