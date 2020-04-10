/**
 *  주시스템명 : 부산시청 사이버 지방세청
 *  업  무  명 : 연계
 *  기  능  명 : 교통유발부담금 실시간 수납 전송
 *  클래스  ID : Txdm2422_process : 실 처리로직을 작성하는 클래스
 *  변경  이력 :
 * ------------------------------------------------------------------------
 *  작성자      	소속  		    일자            Tag           내용
 * ------------------------------------------------------------------------
 *  전영진       유채널(주)      2014.11.27         %01%         최초작성
 *  
 */
package com.uc.bs.cyber.daemon.txdm2422;

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
public class Txdm2422_process extends Codm_BaseProcess implements Codm_interface {


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
	public Txdm2422_process() {
		
		super();
		
		log = LogFactory.getLog(this.getClass());
		
		/**
		 * 5 분마다 돈다
		 */
		loopTerm = 300;
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
				svcTm = CbUtil.getResource("ApplicationResource" ,"wsdl.svctime.road");
				
				StringTokenizer tok = new StringTokenizer(svcTm, "~");	
				
				svcTms[0] = Integer.parseInt(tok.nextToken());
				svcTms[1] = Integer.parseInt(tok.nextToken());
				
			} catch (Exception e) {
				
				e.printStackTrace();
				
				svcTms[0] = 0500;
				svcTms[1] = 2130;
			}			

			int procCnt = txdm2422_JobProcess();
					


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
	
	
	private int txdm2422_JobProcess() {
		
		log.info("=======================교통유발부담금 실시간 수납 처리 =======================");
		log.info("=" + this.getClass().getName()+ " txdm2422_JobProcess() Start =");
		log.info("=====================================================================");
		
		/*트랜잭션을 포함하는 실업무는 여기서 구현한다...*/
		/*실업무를 구현합니다.*/
		
		/*기본설정값 ================================================================*/
		
		//테스트코드(실제운영시는 삭제)
		//String resourceKey = "wsdl.url2.test";
		String resourceKey = "wsdl.system.road";

		// System 구분 가져오기
		sysDsc = CbUtil.getResource("ApplicationResource" ,resourceKey);
		
		if(sysDsc.equals("test")) resourceKey = "wsdl.url.test.road";
		else resourceKey = "wsdl.url.rosntg." + this.c_slf_org;
		
		// 대상 서버 정보
		target = CbUtil.getResource("ApplicationResource" ,resourceKey);
		
		// SRC ORGCD (6260000)
		srcOrg = CbUtil.getResource("ApplicationResource" ,"wsdl.srcid.road"); 
		 
		resourceKey = "wsdl.dest." + this.c_slf_org;
		
		// 대상 ORGCD
		desOrg = CbUtil.getResource("ApplicationResource" ,resourceKey); 


		//ifId_vir :: NTDNN00099 연계ID
		ifId_vir = CbUtil.getResource("ApplicationResource" ,"wsdl.ifid.sntg.road"); 
					
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
			
		sunapList = cyberService.queryForList("TXDM2422.getSunapDataList", dataMap);
		
		log.info("[" + this.c_slf_org_nm + "]교통유발부담금 수납 건수 = " + sunapList.size());
		
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
				
				
				MapForm listMap = new MapForm();
							
				listMap.setMap("sf_team_code"     , mfVirNoList.getMap("SF_TEAM_CODE").toString()); // 자치단체코드
				listMap.setMap("taxin_code"       , mfVirNoList.getMap("TAXIN_CODE").toString());      // 세목
				listMap.setMap("lvy_no"           , mfVirNoList.getMap("LVY_NO").toString());		   // 과세번호     
				listMap.setMap("anc_chasu"        , mfVirNoList.getMap("ANC_CHASU").toString());       // 고지차수     
				listMap.setMap("recpt_chasu"      , mfVirNoList.getMap("RECPT_CHASU").toString());     // 수납차수     
				listMap.setMap("recpt_ymd"        , mfVirNoList.getMap("RECPT_YMD").toString());       // 수납일자     
				listMap.setMap("recpt_am"         , mfVirNoList.getMap("RECPT_AM").toString());        // 수납금액     
				listMap.setMap("lvy_yy"           , mfVirNoList.getMap("LVY_YY").toString());          // 부과년도     
				listMap.setMap("lvy_mm"           , mfVirNoList.getMap("LVY_MM").toString());          // 부과월       
				listMap.setMap("dep_code"         , mfVirNoList.getMap("DEP_CODE").toString());        // 부서코드     
				listMap.setMap("gubun"            , mfVirNoList.getMap("GUBUN").toString());           // 기분         
				listMap.setMap("owner_sid"        , mfVirNoList.getMap("OWNER_SID").toString());       // 납부자번호   
				listMap.setMap("recpt_bank_nm"    , mfVirNoList.getMap("RECPT_BANK_NM").toString());   // 은행명       
				listMap.setMap("transfer_ymd"     , mfVirNoList.getMap("TRANSFER_YMD").toString());	   // 이체일자	    
				listMap.setMap("acct_ymd"         , mfVirNoList.getMap("ACCT_YMD").toString());        // 회계일자			
				listMap.setMap("recpt_gubun"      , mfVirNoList.getMap("RECPT_GUBUN").toString());     // 처리구분     
				listMap.setMap("recpt_cancel_ymd" , mfVirNoList.getMap("RECPT_CANCEL_YMD").toString());// 수납취소일자 
				listMap.setMap("recpt_cancel_am"  , mfVirNoList.getMap("RECPT_CANCEL_AM").toString()); // 수납취소금액 
				listMap.setMap("last_mod_ts"      , mfVirNoList.getMap("LAST_MOD_TS").toString());     // 수정일자     
				listMap.setMap("recpt_sr_code"    , mfVirNoList.getMap("RECPT_SR_CODE").toString());   // 수납종류코드
				listMap.setMap("asgnmt_list1"     , mfVirNoList.getMap("ASGNMT_LIST1").toString());    // 비고1
				listMap.setMap("asgnmt_list2"     , mfVirNoList.getMap("ASGNMT_LIST2").toString());    // 비고2
				listMap.setMap("asgnmt_list3"     , mfVirNoList.getMap("ASGNMT_LIST3").toString());    // 비고3
				listMap.setMap("asgnmt_list4"     , mfVirNoList.getMap("ASGNMT_LIST4").toString());    // 비고4
				listMap.setMap("asgnmt_list5"     , mfVirNoList.getMap("ASGNMT_LIST5").toString());    // 비고5
				
				dataMap.setMap("list", listMap);
				
				try {
					msgIdx ++;
					log.info("교통유발부담금 웹서비스 시작");
					xmlDoc  =  wsdl.sendNRcv(ifId_vir, target, srcOrg, desOrg, msgKey + (String.valueOf(Math.random())).substring(2, 10), listMap);		
					
					log.info("xmlDoc :: " + xmlDoc);
					
					String resCd = xmlDoc.getElementsByTagName("success").item(0).getFirstChild().getNodeValue();
					
					log.info("resCd ==> " + resCd);

					if(xmlDoc.getElementsByTagName("env:fault") != null && xmlDoc.getElementsByTagName("env:fault").getLength() > 0){
					
						log.info("Fault Code ==" + xmlDoc.getElementsByTagName("faultcode").item(0).getFirstChild().getNodeValue());
						log.info("Fault Mesg ==" + xmlDoc.getElementsByTagName("faultstring").item(0).getFirstChild().getNodeValue());
						log.info("오류데이터 = " + mfVirNoList.getMaps());
						log.info("교통유발부담금 실시간 수납 웹서비스 시스템 오류코드");					
						
						mfVirNoList.setMap("TRTG", "9");	/* 웹서비스 시스템 오류코드셋팅 */
						
						cyberService.queryForUpdate("TXDM2422.updateWsdlTrtgData", mfVirNoList);
						err2 ++;
						
					}else{						

						// 완료
						if(resCd.equals("Y")) {
							
							/*************  송신상태를 UPDATE 한다. ***********/							
							mfVirNoList.setMap("TRTG", "1");	/* 웹서비스 시스템 전송상태셋팅 - 성공 */
							
							cyberService.queryForUpdate("TXDM2422.updateWsdlTrtgData", mfVirNoList);
							
							//log.info(" 웹서비스 시스템 전송상태셋팅 TRTG == 1");
							updCnt++;

						}else{  // 처리 오류
							
							log.info("교통유발부담금 실시간 수납 웹서비스전송 에러 ");
							log.info("MSG_KEY=" + msgKey);
							log.info("오류데이터 = " + mfVirNoList.getMaps());

							mfVirNoList.setMap("TRTG", "8");	/* 웹서비스 시스템 전송상태셋팅 */
							cyberService.queryForUpdate("TXDM2422.updateWsdlTrtgData", mfVirNoList);
							
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
				daemonMap.setMap("DAEMON"    , "TXDM2422");
				daemonMap.setMap("DAEMON_NM" , "교통유발부담금 실시간수납(WSDL)전송");
				daemonMap.setMap("SGG_COD"   , c_slf_org);
				daemonMap.setMap("TOTAL_CNT" , msgIdx);
				daemonMap.setMap("INSERT_CNT", updCnt);
				daemonMap.setMap("UPDATE_CNT", 0);
				daemonMap.setMap("DELETE_CNT", 0);
				daemonMap.setMap("ERROR_CNT" , err1 + err2);
				cyberService.queryForInsert("CODMBASE.InsertDaemonProcessCheck", daemonMap);
			}catch(Exception ex){
				log.debug("교통유발부담금 실시간수납(WSDL)전송 로그 등록 오류");
			}				
			/***********************************************************************************/
			
		}else{
			
			/***********************************************************************************/
			try{
				MapForm daemonMap = new MapForm();
				daemonMap.setMap("DAEMON"    , "TXDM2422");
				daemonMap.setMap("DAEMON_NM" , "교통유발부담금 실시간수납(WSDL)전송");
				daemonMap.setMap("SGG_COD"   , c_slf_org);
				daemonMap.setMap("TOTAL_CNT" , 0);
				daemonMap.setMap("INSERT_CNT", 0);
				daemonMap.setMap("UPDATE_CNT", 0);
				daemonMap.setMap("DELETE_CNT", 0);
				daemonMap.setMap("ERROR_CNT" , 0);
				cyberService.queryForInsert("CODMBASE.InsertDaemonProcessCheck", daemonMap);
			}catch(Exception ex){
				log.debug("교통유발부담금 실시간수납(WSDL)전송 대상이 없습니다. 등록 오류");
			}				
			/***********************************************************************************/
			
			//log.info("실시간 수납 전송 자료가 없습니다.");
		}
		
		return sunapList.size();
		
	}

}
