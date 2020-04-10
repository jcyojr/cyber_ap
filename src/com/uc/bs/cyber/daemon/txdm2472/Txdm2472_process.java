/**
 *  주시스템명 : 부산시청 사이버 지방세청
 *  업  무  명 : 연계
 *  기  능  명 : 주정차위반과태료 실시간 수납 전송
 *  클래스  ID : Txdm2472_process : 실 처리로직을 작성하는 클래스
 *  변경  이력 :
 * ------------------------------------------------------------------------
 *  작성자      	소속  		    일자            Tag           내용
 * ------------------------------------------------------------------------
 *  전영진       유채널(주)      2014.11.27         %01%         최초작성
 *  
 */
package com.uc.bs.cyber.daemon.txdm2472;

import java.util.ArrayList;

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
public class Txdm2472_process extends Codm_BaseProcess implements Codm_interface {


	private MapForm dataMap   = null;

	private String sysDsc     = null;
	private String target     = null;
	private String ifId_vir   = null;
	private String srcOrg     = null;
	private String desOrg     = null;

	public Txdm2472_process() {
		
		super();
		
		log = LogFactory.getLog(this.getClass());
		
		/**
		 * 5 분마다 돈다
		 */
		loopTerm = 60*5;
	}
	
	/*Context 주입용*/
	public void setApp(ApplicationContext context) {
		this.context = context;
	}
	
	
	/* process starting */
	@Override
	public void runProcess() throws Exception {

		mainTransProcess();
	}

	/*트랜잭션을 실행하기 위한 함수.*/
	private void mainTransProcess(){

		try {
				
			setContext(appContext);
			setApp(appContext);

			UcContextHolder.setCustomerType(this.dataSource);

			// System 구분 가져오기
			sysDsc = CbUtil.getResource("ApplicationResource","wsdl.system.jucha");
			// 대상 서버 정보
			target = CbUtil.getResource("ApplicationResource","wsdl.url.jusntg."+this.c_slf_org);
			// SRC ORGCD (6260000)
			srcOrg = CbUtil.getResource("ApplicationResource","wsdl.srcid.jucha");
			// 대상 ORGCD
			desOrg = CbUtil.getResource("ApplicationResource","wsdl.dest."+this.c_slf_org); 
			//ifId_vir :: NTDNN00005 연계ID
			ifId_vir = CbUtil.getResource("ApplicationResource","wsdl.ifid.sntg.jucha"); 
			/*
			if(log.isInfoEnabled()){
				log.info("================WSDL 정보=================");
				log.info("c_slf_org = [" + this.c_slf_org + "]");
				log.info("sysDsc      = [" + sysDsc + "]");
				log.info("target      = [" + target + "]");
				log.info("srcOrg      = [" + srcOrg + "]");
				log.info("desOrg      = [" + desOrg + "]");
				log.info("ifId_vir    = [" + ifId_vir + "]");				
				log.info("================WSDL 정보=================");
			}
			*/
			
			dataMap = new MapForm();  /*초기화*/
			dataMap.setMap("SGG_COD", this.c_slf_org);

			int page=0;
			try{
				page = cyberService.getOneFieldInteger("TXDM2472.SELECT_TX2211_CNT", dataMap);
				log.info("[" + this.c_slf_org_nm + "] 주정차위반과태료 수납 건수 = "+page);
			} catch (Exception sub_e) {
				log.error("[" + this.c_slf_org + "] SELECT_TX2211_CNT 오류");
				sub_e.printStackTrace();
			}

			if(!(
					  this.c_slf_org.equals("330")
					||this.c_slf_org.equals("331")
				))page=0;
			
			if(page>0){
				try{
					dataMap.setMap("TRN_SNO", Long.parseLong(cyberService.getOneFieldString("TXDM2472.SELECT_TRN_SNO", dataMap)));
				} catch (Exception sub_e) {
					log.error("[" + this.c_slf_org + "] SELECT_TRN_SNO 오류");
					sub_e.printStackTrace();
				}

				try{
					cyberService.queryForUpdate("TXDM2472.UPDATE_TX2211_CNT", dataMap);
				} catch (Exception sub_e) {
					log.error("[" + this.c_slf_org + "] UPDATE_TX2211_CNT 오류");
					sub_e.printStackTrace();
				}

				this.startJob();
			}
					
		} catch (Exception e) {
			e.printStackTrace();
		}			
	}

	@Override
	public void setDatasrc(String datasrc) {

		this.dataSource = datasrc;
	}

	/*트랜잭션 구성*/
	@Override
	public void transactionJob() {
		txdm2472_JobProcess();
	}
	
	private void txdm2472_JobProcess() {
		log.info("[" + this.c_slf_org + "] txdm2472_JobProcess() Start =======");

		int err7=0,err8=0,err9=0,msgIdx=0,updCnt=0;

		WsdlUtil wsdl = new WsdlUtil();
		Document xmlDoc = null;

		@SuppressWarnings("unused")
		String rdmKey = (String.valueOf(Math.random())).substring(2, 10);
		String msgKey = CbUtil.getCurDateTimes();

		/*신규 수납 미전송건*/
		ArrayList<MapForm> sunapList = new ArrayList<MapForm>();
		
		try {
			sunapList = cyberService.queryForList("TXDM2472.SELECT_TX2211_LIST", dataMap);
		} catch (Exception sub_e) {
			log.error("[" + this.c_slf_org + "] SELECT_TX2211_LIST 오류");
			sub_e.printStackTrace();
		}
		
		if(sunapList.size()>0){

			for(int i=0;i<sunapList.size();i++){

				MapForm mfVirNoList=sunapList.get(i);
				
				if(mfVirNoList==null || mfVirNoList.isEmpty()){
					log.info("처리 안되고 넘김");
					continue;
				}
				
				String TAX_NO=mfVirNoList.getMap("TAX_NO").toString();
				
				if(mfVirNoList.getMap("A_VIOL_YM").toString().equals("X")||mfVirNoList.getMap("A_VIOL_MAN_SNO").toString().equals("X")){

					log.info("주정차위반과태료 VIOL_YM, VIOL_MAN_SNO 없는 자료, 사전고지분 전송불가자료 ["+TAX_NO+"]");
					mfVirNoList.setMap("TRTG", "7");
					err7++;
				}else{

					MapForm wsdlMap = new MapForm();
					MapForm wsdlListMap = new MapForm();
					
					wsdlMap.setMap("in_cnt"              , "1"); // 전송갯수
					wsdlListMap.setMap("in_sf_team_code"     , mfVirNoList.getMap("IN_SF_TEAM_CODE").toString());     // 자치단체코드
	                wsdlListMap.setMap("in_deal_gbn_code"    , mfVirNoList.getMap("IN_DEAL_GBN_CODE").toString());    // 처리구분			
	                wsdlListMap.setMap("in_anc_chasu"        , mfVirNoList.getMap("IN_ANC_CHASU").toString());        // 고지차수
	                wsdlListMap.setMap("in_tax_code"         , mfVirNoList.getMap("IN_TAX_CODE").toString());         // 세목
	                wsdlListMap.setMap("in_lvy_no"           , mfVirNoList.getMap("IN_LVY_NO").toString());		      // 과세번호
	                wsdlListMap.setMap("in_anc_chasu"        , mfVirNoList.getMap("IN_ANC_CHASU").toString());        // 고지차수
	                wsdlListMap.setMap("in_recpt_chasu"      , mfVirNoList.getMap("IN_RECPT_CHASU").toString());      // 수납차수
	                wsdlListMap.setMap("in_recpt_ymd"        , mfVirNoList.getMap("IN_RECPT_YMD").toString());        // 수납일자
					wsdlListMap.setMap("in_recpt_amt"        , mfVirNoList.getMap("IN_RECPT_AMT").toString());        // 수납금액
					wsdlListMap.setMap("in_lvy_yy"           , mfVirNoList.getMap("IN_LVY_YY").toString());           // 부과년도
					wsdlListMap.setMap("in_lvy_mon"          , mfVirNoList.getMap("IN_LVY_MON").toString());          // 부과월
					wsdlListMap.setMap("in_lvy_dep_code"     , mfVirNoList.getMap("IN_LVY_DEP_CODE").toString());     // 부서코드
					wsdlListMap.setMap("in_lvy_gbn"          , mfVirNoList.getMap("IN_LVY_GBN").toString());          // 고지구분
					wsdlListMap.setMap("in_rgt_mbd_reg_no"   , mfVirNoList.getMap("IN_RGT_MBD_REG_NO").toString());   // 납부자번호
					wsdlListMap.setMap("in_bank_nm"          , mfVirNoList.getMap("IN_BANK_NM").toString());          // 은행명 
					wsdlListMap.setMap("in_iche_ymd"         , mfVirNoList.getMap("IN_ICHE_YMD").toString());	      // 이체일자	
					wsdlListMap.setMap("in_acct_ymd"         , mfVirNoList.getMap("IN_ACCT_YMD").toString());         // 회계일자				
					wsdlListMap.setMap("in_recpt_cancel_ymd" , mfVirNoList.getMap("IN_RECPT_CANCEL_YMD").toString()); // 수납취소일자
					wsdlListMap.setMap("in_recpt_cancel_amt" , mfVirNoList.getMap("IN_RECPT_CANCEL_AMT").toString()); // 수납취소금액
					wsdlListMap.setMap("in_vir_acc_no"       , mfVirNoList.getMap("IN_VIR_ACC_NO").toString());       // 가상계좌번호
					wsdlListMap.setMap("in_viol_ym"          , mfVirNoList.getMap("IN_VIOL_YM").toString());          // 관리월
					wsdlListMap.setMap("in_viol_man_sno"     , mfVirNoList.getMap("IN_VIOL_MAN_SNO").toString());     // 관리일련번호	

					wsdlMap.setMap("list", wsdlListMap);
					
					try {
						msgIdx++;
						xmlDoc =  wsdl.sendNRcv(ifId_vir, target, srcOrg, desOrg, msgKey + CbUtil.fillZero(Integer.toString(msgIdx), 8), wsdlMap); 
						//log.info("xmlDoc :: " + xmlDoc);
						
						if(xmlDoc.getElementsByTagName("env:Fault") != null && xmlDoc.getElementsByTagName("env:Fault").getLength() > 0){
						
							//log.info("Fault Code ==" + xmlDoc.getElementsByTagName("faultcode").item(0).getFirstChild().getNodeValue());
							//log.info("Fault Mesg ==" + xmlDoc.getElementsByTagName("faultstring").item(0).getFirstChild().getNodeValue());
							//log.info("오류데이터 = " + mfVirNoList.getMaps());
							//log.info("주정차위반과태료 실시간 수납 웹서비스 시스템 오류코드");
							log.info("["+TAX_NO+"] WSDL 전송 시스템오류, xmlDoc :"+xmlDoc);
							mfVirNoList.setMap("TRTG", "9");	/* 웹서비스 시스템 오류코드셋팅 */
							err9++;
							
						}else{
							
							String resCd = xmlDoc.getElementsByTagName("res_cnt").item(0).getFirstChild().getNodeValue();
							if(resCd.equals("1")) {
								mfVirNoList.setMap("TRTG","1");
								updCnt++;
							}else{// 처리 오류
								//log.info("MSG_KEY=" + msgKey);
								//log.info("오류데이터 = " + mfVirNoList.getMaps());
								log.info("["+TAX_NO+"] WSDL 전송오류, 응답코드(resCd):" + resCd);

								mfVirNoList.setMap("TRTG","8");	/* 웹서비스 시스템 전송상태셋팅 */
								err8++;
							}
						}
						
						// TRTG 업데이트
						try {
							cyberService.queryForUpdate("TXDM2472.UPDATE_TX2211_TRTG", mfVirNoList);
						} catch (Exception sub_e) {
							log.error("[" + this.c_slf_org + "] UPDATE_TX2211_TRTG 오류");
							sub_e.printStackTrace();
						}
						
					} catch (Exception e) {
						log.error("오류데이터 = " + mfVirNoList.getMaps());
						e.printStackTrace();
					}
				}
			}
			
			log.info("실시간수납 전송> 전체:"+msgIdx+", 완료:"+updCnt+", 전송불가자료:"+err7+", WSDL전송오류(기수납):" + err8+", WSDL시스템오류:" + err9);

		}

	}

}

