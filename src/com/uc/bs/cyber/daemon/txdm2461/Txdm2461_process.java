/**
 *  주시스템명 : 부산시청 사이버 지방세청
 *  업  무  명 : 연계
 *  기  능  명 : 환경개선부담금 가상계좌를 사업단에 전송
 *  클래스  ID : Txdm2461_process : 실 처리로직을 작성하는 클래스
 *  변경  이력 :
 * ------------------------------------------------------------------------
 *  작성자      	소속  		    일자            Tag           내용
 * ------------------------------------------------------------------------
 *  송동욱       유채널(주)      2011.06.06         %01%         최초작성
 */
package com.uc.bs.cyber.daemon.txdm2461;

import java.math.BigDecimal;
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
public class Txdm2461_process extends Codm_BaseProcess implements Codm_interface {


	private MapForm dataMap            = null;

	private String    sysDsc      = null;
	private String    target      = null;
	private String    ifId_vir    = null;
	private String    ifId_sunab  = null;
	private String    srcOrg      = null;
	private String    desOrg      = null;
	private String    svcTm       = null;
	
	private int[] svcTms = new int[2];
	
	/**
	 * 
	 */
	public Txdm2461_process() {

		super();
		
		log = LogFactory.getLog(this.getClass());
		
		/**
		 * 10 분마다 돈다
		 */
		loopTerm = 60 * 10;
	}
	/*Context 주입용*/
	public void setApp(ApplicationContext context) {
		this.context = context;
	}
	
	
	/* process starting */
	public void runProcess() throws Exception {
		
		//log.debug("=====================================================================");
		//log.debug("=" + this.getClass().getName()+ " runProcess() ==");
		//log.debug("=====================================================================");
		
		try {
			
//			do {
//				//실전송업무 시작
//				int procCnt = txdm2461_JobProcess();
//				
//				if(procCnt == 0) {
//					break;	
//				}
//
//			}while(true);
			
			txdm2461_JobProcess();

		} catch (Exception e) {

			e.printStackTrace();
		}			


	}

	/*현 사용할 일이 없넹...*/
	public void setDatasrc(String datasrc) {

		this.dataSource = datasrc;
	}

	/*트랜잭션 구성*/
	public void transactionJob() {
		
		
	}
	
	private int txdm2461_JobProcess() {
		
		log.info("=======================환경개선부담금 가상계좌=======================");
		log.info("=" + this.getClass().getName()+ " txdm2461_JobProcess() Start =");
		log.info("= govid["+this.govId+"], orgcd["+this.c_slf_org+"], orgnm[" + this.c_slf_org_nm + "]");
		log.info("=====================================================================");
		
		/*트랜잭션을 포함하는 실업무는 여기서 구현한다...*/
		/*실업무를 구현합니다.*/
		
		/*기본설정값 ================================================================*/
		
		//테스트코드(실제운영시는 삭제)
		//resourceKey = "wsdl.url2.test";
		String resourceKey = "wsdl.system.env";

		// System 구분 가져오기
		sysDsc = CbUtil.getResource("ApplicationResource" ,resourceKey);
		
		if(sysDsc.equals("test")) resourceKey = "wsdl.url.test.env";
		else resourceKey = "wsdl.url.env." + this.c_slf_org;
		
		// 대상 서버 정보
		target = CbUtil.getResource("ApplicationResource" ,resourceKey);
		
		// SRC ORGCD (6260000)
		srcOrg = CbUtil.getResource("ApplicationResource" ,"wsdl.srcid.env"); 
		 
		resourceKey = "wsdl.dest." + this.c_slf_org;
		
		// 대상 ORGCD
		desOrg = CbUtil.getResource("ApplicationResource" ,resourceKey); 

		//테스트코드(실제운영시는 삭제)
		if(sysDsc.equals("test")){
			desOrg="3280000";
		}
		
		ifId_vir = CbUtil.getResource("ApplicationResource" ,"wsdl.ifid.vir.env"); 
		
		ifId_sunab = CbUtil.getResource("ApplicationResource" ,"wsdl.ifid.sunab.env");
				
		/*기본설정값 ================================================================*/
		int err1 = 0, err2 = 0, msgIdx = 0;
		
		String SF_TEAM_CODE  = "";
		String DEAL_GBN_CODE = "";
		String TAX_CODE      = "";
		String LVY_YMD       = "";
		String LVY_GIBN      = "";
		String LVY_ADDR_CODE = "";
		String LVY_SNO   = "";
		String ANC_CHASU = "";
		String CNAPG     = "";
		String CRE_YMD   = "";
		String ACC_NO    = "";
		String VIR_NO    = "";

		WsdlUtil wsdl = new WsdlUtil();
		
		Document xmlDoc = null;
		
		dataMap = new MapForm();

		/**
		 * 1. 가상계좌 채번내역 전송
		 */
		
		//구청별로 가져오지 않고 전체를 가져와서 5분단위로 전송함...
		dataMap.setMap("SGG_COD", this.c_slf_org);  /*구청코드맵핑*/
		
		@SuppressWarnings("unused")
		String rdmKey = (String.valueOf(Math.random())).substring(2, 10);
		String msgKey = CbUtil.getCurDateTimes();

		/*가상계좌 채번내역 가져오기*/
		ArrayList<MapForm> alVirNoList =  cyberService.queryForList("TXDM2461.SELECT_VIRTUAL_ACCT_ENVLEVY_LIST", dataMap);
		
		log.info("[" + this.c_slf_org_nm + "]환경개선부담금 가상계좌채번내역 건수 = " + alVirNoList.size());
		
		if (alVirNoList.size()  >  0)   {
			
			log.debug("SERVICE TIME==" + svcTms[0] + ", " + svcTms[1]);
			
			if(log.isInfoEnabled()){
				log.info("================WSDL 정보=================");
				log.info("resourceKey = [" + resourceKey + "]");
				log.info("sysDsc      = [" + sysDsc + "]");
				log.info("target      = [" + target + "]");
				log.info("srcOrg      = [" + srcOrg + "]");
				log.info("desOrg      = [" + desOrg + "]");
				log.info("ifId_vir    = [" + ifId_vir + "]");
				log.info("ifId_sunab  = [" + ifId_sunab + "]");
				log.info("================WSDL 정보=================");
			}
			
			for (int rec_cnt = 0;  rec_cnt < alVirNoList.size();  rec_cnt++)   {
				
				dataMap = new MapForm();
				
				MapForm mfVirNoList =  alVirNoList.get(rec_cnt);
				
				if (mfVirNoList == null  ||  mfVirNoList.isEmpty() )   {
					continue;
				}
				
				ACC_NO = mfVirNoList.getMap("ACC_NO").toString().trim();
				SF_TEAM_CODE  = mfVirNoList.getMap("SF_TEAM_CODE").toString();
				DEAL_GBN_CODE = mfVirNoList.getMap("DEAL_GBN_CODE").toString();
				TAX_CODE = mfVirNoList.getMap("TAX_CODE").toString();
				LVY_YMD  = mfVirNoList.getMap("LVY_YMD").toString();
				LVY_GIBN = mfVirNoList.getMap("LVY_GIBN").toString();
				LVY_ADDR_CODE = mfVirNoList.getMap("LVY_ADDR_CODE").toString();
				LVY_SNO   = mfVirNoList.getMap("LVY_SNO").toString();
				ANC_CHASU = ((BigDecimal)mfVirNoList.getMap("ANC_CHASU")).toString();
				CNAPG     = mfVirNoList.getMap("CNAPG").toString();
				CRE_YMD   = mfVirNoList.getMap("CRE_YMD").toString();
				VIR_NO    = ACC_NO.substring(0,3)+"-"+ACC_NO.substring(3,5)+"-"+ACC_NO.substring(5,11)+"-"+ACC_NO.substring(11,12);
				
				dataMap.setMap("in_CNT"            , "1"); 
				dataMap.setMap("in_sf_team_code"   , SF_TEAM_CODE);  //자치단체코드(7)
				dataMap.setMap("in_deal_gbn_code"  , DEAL_GBN_CODE); //처리구분(1)

				MapForm listMap = new MapForm();

				listMap.setMap("in_TAX_CODE"       , TAX_CODE);      //세목(6)
				listMap.setMap("in_LVY_YMD"        , LVY_YMD);       //부과년도(4)
				listMap.setMap("in_LVY_GIBN"       , LVY_GIBN);      //기분(1)
				listMap.setMap("in_LVY_ADDR_CODE"  , LVY_ADDR_CODE); //법정동코드(8)
				listMap.setMap("in_LVY_SNO"        , LVY_SNO);       //과세번호(6)
				listMap.setMap("in_ANC_CHASU"      , ANC_CHASU);     //SENO(3)
				listMap.setMap("in_CNAPG"          , CNAPG);         //체납여부(1)
				listMap.setMap("in_VRE_YMD"        , CRE_YMD);       //생성일자(8)
				listMap.setMap("in_ACC_NO"         , VIR_NO);        //가상계좌번호(15)
				listMap.setMap("in_RCR_NO"         , "billing");     //작업자(20)
				
				dataMap.setMap("list", listMap);
				
				try {
					msgIdx ++;
					
					xmlDoc  = wsdl.sendNRcv(ifId_vir, target, srcOrg, desOrg, msgKey + CbUtil.fillZero(Integer.toString(msgIdx), 8), dataMap);
					
					if(xmlDoc.getElementsByTagName("env:Fault") != null && xmlDoc.getElementsByTagName("env:Fault").getLength() > 0 ) {
					
						log.fatal("Fault Code ==" + xmlDoc.getElementsByTagName("faultcode").item(0).getFirstChild().getNodeValue());
						log.fatal("Fault Mesg ==" + xmlDoc.getElementsByTagName("faultstring").item(0).getFirstChild().getNodeValue());
						log.fatal("VIR_ACC_NO=" + ACC_NO);
						
						mfVirNoList.setMap("SGG_TR_TG", "7");	/* 웹서비스 시스템 오류코드셋팅 */
						mfVirNoList.setMap("SGG_COD"  , this.c_slf_org);
						mfVirNoList.setMap("ACC_NO"   , ACC_NO);

						cyberService.queryForUpdate("TXDM2461.UPDATE_TRANS_ENV_DETAIL", mfVirNoList);
						err2 ++;
						
					} else {
						
						String resCd = xmlDoc.getElementsByTagName("res_cnt").item(0).getFirstChild().getNodeValue();
						
						if(resCd.equals("1")) {
							
							/*************  송신상태를 UPDATE 한다. ***********/							
							mfVirNoList.setMap("SGG_TR_TG", "1");	/* 웹서비스 시스템 전송상태셋팅 */
							mfVirNoList.setMap("SGG_COD"  , this.c_slf_org);
							mfVirNoList.setMap("ACC_NO"   , ACC_NO);
							
							int updCnt = cyberService.queryForUpdate("TXDM2461.UPDATE_TRANS_ENV_DETAIL", mfVirNoList);
							
							//log.debug("[H][" + this.c_slf_org_nm + "]가상계좌 채번자료 전송완료(" + rec_cnt + ")  VIR_ACC_NO=" + ACC_NO + "::" + updCnt);											

						} else {  // 처리 오류
							
							log.fatal("[H][" + this.c_slf_org_nm + "]가상계좌 채번자료 전송오류 =" + resCd + "::");
							log.fatal("VIR_ACC_NO=" + ACC_NO);
							log.fatal("MSG_KEY=" + msgKey);

							mfVirNoList.setMap("SGG_TR_TG", "8");	/* 웹서비스 시스템 전송상태셋팅 */
							cyberService.queryForUpdate("TXDM2461.UPDATE_TRANS_ENV_DETAIL", mfVirNoList);
							
							err1++;
							
						}

					}
					
					if(msgIdx % 100 == 0) {
						
						log.info("[H][" + this.c_slf_org_nm + "][" + this.c_slf_org + "]가상계좌 전송 COUNT==" + msgIdx + ", ERR1==" + err1 + ", ERR2==" + err2);
						
						int curTime = Integer.parseInt(CbUtil.getCurrentTime().substring(0, 4));
						
						if(curTime > svcTms[1]){ /* 21시 30분이 지나면 그만 전송하도록 한다. */
							
							//log.info("========================================");
							//log.info("======    서비스 종료 :: " + curTime + "    =======");
							//log.info("========================================");
							
							//일단 계속전송 황종훈
							//break;
						}
						
					}
					
					
				} catch (Exception e) {
	
					log.error("오류데이터 = " + mfVirNoList.getMaps());
					e.printStackTrace();
					err1=9999;
					err2=9999;
				}
				
			}
			
			log.info("["+sysDsc+"][" + this.c_slf_org_nm + "][" + this.c_slf_org + "]::환경개선부담금 가상계좌 전송 COUNT==" + msgIdx + ", ERR1==" + err1 + ", ERR2==" + err2);
		}
		
		return alVirNoList.size();
		
	}

}
