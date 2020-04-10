/**
 *  주시스템명 : 부산시청 사이버 지방세청
 *  업  무  명 : 연계
 *  기  능  명 : 주정차위반과태료 가상계좌를 사업단에 전송
 *  클래스  ID : Txdm2461_process : 실 처리로직을 작성하는 클래스
 *  변경  이력 :
 * ------------------------------------------------------------------------
 *  작성자      	소속  		    일자            Tag           내용
 * ------------------------------------------------------------------------
 *  송동욱       유채널(주)      2011.06.06         %01%         최초작성
 */
package com.uc.bs.cyber.daemon.txdm2471;

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

/**
 * @author Administrator
 *
 */
public class Txdm2471_process extends Codm_BaseProcess implements Codm_interface {


	private MapForm dataMap       = null;

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
	public Txdm2471_process() {
		// TODO Auto-generated constructor stub
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
		// TODO Auto-generated method stub
		
		//log.debug("=====================================================================");
		//log.debug("=" + this.getClass().getName()+ " runProcess() ==");
		//log.debug("=====================================================================");
		
		/*여기서 시간을 조절해야 한다...*/
		try{
			svcTm = CbUtil.getResource("ApplicationResource" ,"wsdl.svctime.jucha");
			
			StringTokenizer tok = new StringTokenizer(svcTm, "~");	
			
			svcTms[0] = Integer.parseInt(tok.nextToken());
			svcTms[1] = Integer.parseInt(tok.nextToken());
			
		} catch (Exception e) {
			
			e.printStackTrace();
			
			svcTms[0] = 0500;
			svcTms[1] = 2130;
		}
		
		int curTime = Integer.parseInt(CbUtil.getCurDateTimes().substring(0, 4));
		
		if (curTime > svcTms[0] && curTime < svcTms[1]) {

			log.info("========================================");
			log.info("======    서비스 개시 :: " + curTime + "    =======");
			log.info("========================================");
			
			do {
				//실전송업무 시작
				int procCnt = txdm2471_JobProcess();
				
				if(procCnt == 0) {
					break;	
				}

			}while(true);
			
		} else {
			
			do {
				//실전송업무 시작
				int procCnt = txdm2471_JobProcess();
				
				if(procCnt == 0) {
					break;	
				}

			}while(true);
			
			log.info("========================================");
			log.info("======    서비스 종료 :: " + curTime + "    =======");
			log.info("========================================");
		}
		

	}

	/*현 사용할 일이 없넹...*/
	@Override
	public void setDatasrc(String datasrc) {
		// TODO Auto-generated method stub
		this.dataSource = datasrc;
	}

	/*트랜잭션 구성*/
	@Override
	public void transactionJob() {
		// TODO Auto-generated method stub
	}
	
	private int txdm2471_JobProcess() {
		
		log.info("===================주정차위반과태료 가상계좌전송=====================");
		log.info("=" + this.getClass().getName()+ " txdm2471_JobProcess() Start =");
		log.info("= govid["+this.govId+"], orgcd["+this.c_slf_org+"], orgnm["+this.c_slf_org_nm+"]");
		log.info("=====================================================================");
		
		/*트랜잭션을 포함하는 실업무는 여기서 구현한다...*/
		/*실업무를 구현합니다.*/
		
		/*기본설정값 ================================================================*/
		
		int levy_cnt = 0;
		
		try{
			
			//테스트코드(실제운영시는 삭제)
			//resourceKey = "wsdl.url2.test";
			String resourceKey = "wsdl.system.jucha";

			// System 구분 가져오기
			sysDsc = CbUtil.getResource("ApplicationResource" ,resourceKey);
			
			if(sysDsc.equals("test")) resourceKey = "wsdl.url.test.jucha";
			else resourceKey = "wsdl.url.jucha." + this.c_slf_org;
			
			// 대상 서버 정보
			target = CbUtil.getResource("ApplicationResource" ,resourceKey);
			
			// SRC ORGCD (6260000)
			srcOrg = CbUtil.getResource("ApplicationResource" ,"wsdl.srcid.jucha"); 
			 
			resourceKey = "wsdl.dest." + this.c_slf_org;
					
			// 대상 ORGCD
			desOrg = CbUtil.getResource("ApplicationResource" ,resourceKey); 

			//테스트코드(실제운영시는 삭제)
			if(sysDsc.equals("test")){
				desOrg="3280000";
			}
			
			ifId_vir = CbUtil.getResource("ApplicationResource" ,"wsdl.ifid.vir.jucha"); 
			
			ifId_sunab = CbUtil.getResource("ApplicationResource" ,"wsdl.ifid.sunab.jucha");
			
			/*기본설정값 ================================================================*/
			int err1 = 0, err2 = 0, msgIdx = 0;
			
			WsdlUtil wsdl = new WsdlUtil();
			
			Document xmlDoc = null;
			
			dataMap = new MapForm();

			/**
			 * 1. 가상계좌 채번내역 전송
			 */
			dataMap.setMap("SGG_COD", this.c_slf_org);  /*구청코드맵핑*/
			
			@SuppressWarnings("unused")
			String rdmKey = (String.valueOf(Math.random())).substring(2, 10);
			String msgKey = CbUtil.getCurDateTimes();
			
			/*가상계좌 채번내역 가져오기*/
			ArrayList<MapForm> alVirNoList =  cyberService.queryForList("TXDM2471.SELECT_VIRTUAL_ACCT_PARKFINE_LIST", dataMap);
			
			levy_cnt = alVirNoList.size();
			
			log.info("[" + this.c_slf_org_nm + "]주정차위반과태료 가상계좌채번내역 건수 = " + levy_cnt);
			
			if (levy_cnt  >  0)   {
				
				log.debug("SERVICE TIME==" + CbUtil.lPadString(String.valueOf(svcTms[0]), 4, '0') + ", " + svcTms[1]);
				
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
				
				for (int rec_cnt = 0;  rec_cnt < levy_cnt;  rec_cnt++)   {
					
					dataMap = new MapForm();
					
					MapForm mfVirNoList =  alVirNoList.get(rec_cnt);
					
					if (mfVirNoList == null  ||  mfVirNoList.isEmpty() )   {
						continue;
					}
					
					dataMap.setMap("in_cnt"            , "1"); 
					dataMap.setMap("in_sf_team_code"   , "26");
					dataMap.setMap("in_deal_gbn_code"  , mfVirNoList.getMap("DEAL_GBN"));
					dataMap.setMap("in_last_cort_id"   , "");
					dataMap.setMap("in_thap_gbn"       , mfVirNoList.getMap("THAP_GBN"));

					MapForm listMap = new MapForm();

					listMap.setMap("in_tax_code"       , mfVirNoList.getMap("TAX_CD"));
					listMap.setMap("in_lvy_no"         , mfVirNoList.getMap("LVY_NO"));
					listMap.setMap("in_anc_chasu"      , mfVirNoList.getMap("DIVIDED_PAYMENT_SEQNUM"));
					listMap.setMap("in_lvy_gbn"        , mfVirNoList.getMap("LVY_GBN"));
					listMap.setMap("in_rgt_mbd_reg_no" , mfVirNoList.getMap("RLNO"));
					listMap.setMap("in_vir_acc_no"     , mfVirNoList.getMap("VIR_ACC_NO"));
					listMap.setMap("in_bank_nm"        , mfVirNoList.getMap("BNK_NM"));
					listMap.setMap("in_cre_ymd"        , mfVirNoList.getMap("CUR_YMD"));
					listMap.setMap("in_mod_ymd"        , mfVirNoList.getMap("CUR_YMD"));
					listMap.setMap("in_xpire_ymd"      , "99999999");
					listMap.setMap("in_viol_ym"        , mfVirNoList.getMap("VIOL_YM"));
					listMap.setMap("in_viol_man_sno"   , mfVirNoList.getMap("VIOL_SNO"));

					dataMap.setMap("list", listMap);
					
					dataMap.setMap("in_sf_team_code"   , desOrg);
					
					try {
						
						msgIdx ++;
						
						xmlDoc =  wsdl.sendNRcv(ifId_vir, target, srcOrg, desOrg, msgKey + CbUtil.fillZero(Integer.toString(msgIdx), 8), dataMap); 
								
						if(xmlDoc.getElementsByTagName("env:Fault") != null && xmlDoc.getElementsByTagName("env:Fault").getLength() > 0 ) {
						
							log.fatal("Fault Code ==" + xmlDoc.getElementsByTagName("faultcode").item(0).getFirstChild().getNodeValue());
							log.fatal("Fault Mesg ==" + xmlDoc.getElementsByTagName("faultstring").item(0).getFirstChild().getNodeValue());
							log.fatal("    LVY_KEY==" + mfVirNoList.getMap("LVY_KEY") + ", VIR_ACC_NO=" + mfVirNoList.getMap("VIR_ACC_NO"));
							log.fatal("    MSG_KEY==" + msgKey + CbUtil.fillZero(Integer.toString(msgIdx), 8));
							
							mfVirNoList.setMap("SGG_TR_TG", "7");	/* 웹서비스 시스템 오류코드셋팅 */
							mfVirNoList.setMap("LVY_KEY"  , mfVirNoList.getMap("LVY_KEY"));
							
							cyberService.queryForUpdate("TXDM2471.UPDATE_TRANS_PARK_DETAIL", mfVirNoList);
							err2 ++;
							
						} else {
							
							
							String resCd = xmlDoc.getElementsByTagName("res_cnt").item(0).getFirstChild().getNodeValue();
							
							if(resCd.equals("1")) {
								
								/*************  송신상태를 UPDATE 한다. ***********/							
								mfVirNoList.setMap("SGG_TR_TG", "1");	/* 웹서비스 시스템 오류코드셋팅 */
								mfVirNoList.setMap("LVY_KEY"  , mfVirNoList.getMap("LVY_KEY"));
								
								int updCnt = cyberService.queryForUpdate("TXDM2471.UPDATE_TRANS_PARK_DETAIL", mfVirNoList);
								
								//log.debug("[C][" + c_slf_org_nm + "]가상계좌 채번자료 전송완료(" + rec_cnt + ") LVY_KEY==" + mfVirNoList.getMap("LVY_KEY") + ", VIR_ACC_NO=" + mfVirNoList.getMap("VIR_ACC_NO") + "::" + updCnt);						

							} else {  // 처리 오류
								
								log.fatal("[C]가상계좌 채번자료 전송오류 =" + resCd + "::" + xmlDoc.getElementsByTagName("OUT_MSG").item(0).getFirstChild().getNodeValue());
								log.fatal("    LVY_KEY==" + mfVirNoList.getMap("LVY_KEY") + ", VIR_ACC_NO=" + mfVirNoList.getMap("VIR_ACC_NO"));
								log.fatal("    MSG_KEY==" + msgKey + CbUtil.fillZero(Integer.toString(msgIdx), 8));

								mfVirNoList.setMap("SGG_TR_TG", "8");	/* 웹서비스 시스템 전송상태셋팅 */
								mfVirNoList.setMap("LVY_KEY"  , mfVirNoList.getMap("LVY_KEY"));
								
								cyberService.queryForUpdate("TXDM2471.UPDATE_TRANS_PARK_DETAIL", mfVirNoList);
								
								err1++;
								
							}

						}
						
						if(msgIdx % 100 == 0) {
							
							log.info("[C]" + c_slf_org_nm + "[" + this.c_slf_org + "]가상계좌 전송 COUNT==" + msgIdx + ", ERR1==" + err1 + ", ERR2==" + err2);
							
							int curTime = Integer.parseInt(CbUtil.getCurrentTime().substring(0, 4));
							
							if(curTime > svcTms[1]){ /* 21시 30분이 지나면 그만 전송하도록 한다. */
								
								//log.info("========================================");
								//log.info("======    서비스 종료 :: " + curTime + "    =======");
								//log.info("========================================");
								//break;
								//잠시막음
							}
							
						}
						
						
					} catch (Exception e) {
						// TODO Auto-generated catch block

						e.printStackTrace();
						log.error("원장DATA = " + mfVirNoList.getMaps());
						log.error("SET DATA = " + listMap.getMaps());
						err1=9999;
						err2=9999;
					}
					
				}
				
				log.info("["+sysDsc+"][" + c_slf_org_nm + "][" + this.c_slf_org + "]::주정차위반과태료 가상계좌 전송 COUNT==" + msgIdx + ", ERR1==" + err1 + ", ERR2==" + err2);
			}
			
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		return levy_cnt;
	}

}
