/**
 *  주시스템명 : 부산시청 사이버 지방세청
 *  업  무  명 : 연계
 *  기  능  명 : 교통유발부담금 가상계좌 채번및 수납결과를 사업단에 전송
 *  클래스  ID : Txdm2421_process : 실 처리로직을 작성하는 클래스
 *  변경  이력 :
 * ------------------------------------------------------------------------
 *  작성자      	소속  		    일자            Tag           내용
 * ------------------------------------------------------------------------
 *  송동욱       유채널(주)      2011.05.28         %01%         최초작성
 */
package com.uc.bs.cyber.daemon.txdm2421;

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
public class Txdm2421_process extends Codm_BaseProcess implements Codm_interface {

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
	public Txdm2421_process() {
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
	
	/*트랜잭션을 실행하기 위한 함수.*/
	@SuppressWarnings("unused")
	private void mainTransProcess(){
		
		try {
				
			setContext(appContext);
			setApp(appContext);

			UcContextHolder.setCustomerType(this.dataSource);
			
			this.startJob();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}			
		
	}
	
	/* process starting */
	@Override
	public void runProcess() throws Exception {
		// TODO Auto-generated method stub
		//log.debug("=====================================================================");
		//log.debug("=" + this.getClass().getName()+ " runProcess() ==");
		//log.debug("=====================================================================");
		

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
			
			
			int curTime = Integer.parseInt(CbUtil.getCurDateTimes().substring(0, 4));
			
			if (curTime > svcTms[0] && curTime < svcTms[1]) {

				log.info("========================================");
				log.info("======    서비스 개시 :: " + curTime + "    =======");
				log.info("========================================");
				
				//실전송업무 시작					
				txdm2421_JobProcess();	

			} else {

				log.info("========================================");
				log.info("======    서비스 종료 :: " + curTime + "    =======");
				log.info("========================================");
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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

	private int txdm2421_JobProcess() {
		
		log.debug("=======================교통유발부담금 가상계좌=======================");
		log.debug("=" + this.getClass().getName()+ " txdm2421_JobProcess() Start =");
		log.info("= govid["+this.govId+"], orgcd["+this.c_slf_org+"], orgnm["+this.c_slf_org_nm+"]");
		log.debug("=====================================================================");
		
		/*트랜잭션을 포함하는 실업무는 여기서 구현한다...*/
		/*실업무를 구현합니다.*/
		
		/*기본설정값 ================================================================*/
		
		String resourceKey = "wsdl.system.road";

		// System 구분 가져오기
		sysDsc = CbUtil.getResource("ApplicationResource" ,resourceKey);
		
		if(sysDsc.equals("test")) resourceKey = "wsdl.url.test.road";
		else resourceKey = "wsdl.url.road." + this.c_slf_org;
		
		log.info("resourceKey for target = " + resourceKey);
		// 대상 서버 정보
		target = CbUtil.getResource("ApplicationResource" ,resourceKey);
		
		// SRC ORGCD (6260000)
		srcOrg = CbUtil.getResource("ApplicationResource" ,"wsdl.srcid.road"); 
		 
		resourceKey = "wsdl.dest." + this.c_slf_org;
		
		//log.info("resourceKey for desOrg = " + resourceKey);
		
		// 대상 ORGCD
		desOrg = CbUtil.getResource("ApplicationResource" ,resourceKey); 
		
		ifId_vir = CbUtil.getResource("ApplicationResource" ,"wsdl.ifid.vir.road"); 
		
		ifId_sunab = CbUtil.getResource("ApplicationResource" ,"wsdl.ifid.sunab.road");

		
		/*기본설정값 ================================================================*/
		int err1 = 0, err2 = 0, msgIdx = 0;
		
		WsdlUtil wsdl = new WsdlUtil();
		
		Document xmlDoc = null;
		
		dataMap = new MapForm();

		/**
		 * 1. 가상계좌 채번내역 전송
		 */
		dataMap.setMap("SGG_COD", this.c_slf_org);  /*구청코드맵핑*/
		
		String msgKey = CbUtil.getCurDateTimes();
		
		/*가상계좌 채번내역 가져오기*/
		ArrayList<MapForm> alVirNoList =  cyberService.queryForList("TXDM2421.SELECT_VIRTUAL_ACCT_LIST", dataMap);
		
		log.info("[" + this.c_slf_org_nm + "] 교통유발부담금 가상계좌채번내역 건수 = " + alVirNoList.size());
		
		if (alVirNoList.size()  >  0) {
			
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
				
				MapForm listMap = new MapForm();
				
				
				try {
					
					String VIR_ACC_NO = (String) mfVirNoList.getMap("VIR_ACC_NO");
					String LVY_AM  = ((BigDecimal)mfVirNoList.getMap("LVY_AM")).toString();
					String ADD_AM  = ((BigDecimal)mfVirNoList.getMap("ADD_AM")).toString();
											
					listMap.setMap("lvy_no"       	    , mfVirNoList.getMap("LVY_NO"));
					listMap.setMap("bdng_mgt_no" 	    , mfVirNoList.getMap("BDNG_MGT_NO"));
					listMap.setMap("owner_mgt_no"	    , mfVirNoList.getMap("OWNER_MGT_NO"));
					/*가상계좌번호(15) */
					listMap.setMap("vir_acc_no"         , VIR_ACC_NO.substring(0,3)+"-"+VIR_ACC_NO.substring(3,5)+"-"+VIR_ACC_NO.substring(5,11)+"-"+VIR_ACC_NO.substring(11,12));
					listMap.setMap("lvy_am" 		    , LVY_AM);
					listMap.setMap("add_am"     	    , ADD_AM);
					listMap.setMap("lvy_state_se_code"  , "0");
					listMap.setMap("last_mod_ts"  	    , "0");
					listMap.setMap("last_cort_id"       , "0");
					
					dataMap.setMap("viraccno", listMap);
					
					msgIdx ++;
					
					xmlDoc  =  wsdl.sendNRcv(ifId_vir, target, srcOrg, desOrg, msgKey + (String.valueOf(Math.random())).substring(2, 10), dataMap);

					if(xmlDoc.getElementsByTagName("env:Fault") != null && xmlDoc.getElementsByTagName("env:Fault").getLength() > 0 ) {
					
						log.fatal("Fault Code ==" + xmlDoc.getElementsByTagName("faultcode").item(0).getFirstChild().getNodeValue());
						log.fatal("Fault Mesg ==" +xmlDoc.getElementsByTagName("faultstring").item(0).getFirstChild().getNodeValue());
						log.fatal("    LVY_KEY==" + mfVirNoList.getMap("TAX_NO") + ", VIR_ACC_NO=" + mfVirNoList.getMap("VIR_ACC_NO"));
						log.fatal("    MSG_KEY==" + msgKey);
						
						mfVirNoList.setMap("SGG_TR_TG", "7");	/* 웹서비스 시스템 오류코드셋팅 */
						cyberService.queryForUpdate("TXDM2421.UPDATE_TRANS_TRAFFIC_DETAIL", mfVirNoList);
						err2 ++;
						
					} else {

						String resCd = xmlDoc.getElementsByTagName("success").item(0).getFirstChild().getNodeValue();

						if(resCd.equals("Y")) {
							
							/*************  송신상태를 UPDATE 한다. ***********/							
							mfVirNoList.setMap("SGG_TR_TG", "1");	/* 웹서비스 시스템 전송상태셋팅 */
							int updCnt = cyberService.queryForUpdate("TXDM2421.UPDATE_TRANS_TRAFFIC_DETAIL", mfVirNoList);
							
							log.debug("[T][" + c_slf_org_nm + "] 가상계좌 채번자료 전송완료(" + rec_cnt + ") LVY_KEY==" + mfVirNoList.getMap("TAX_NO") + ", VIR_ACC_NO=" + mfVirNoList.getMap("VIR_ACC_NO") + "::" + updCnt);							
							
						} else {  // 처리 오류
							
							log.fatal("[T][" + c_slf_org_nm + "] 가상계좌 채번자료 전송오류(" + rec_cnt + ") =" + resCd + "::" + resCd);
							log.fatal("    LVY_KEY==" + mfVirNoList.getMap("TAX_NO") + ", VIR_ACC_NO=" + mfVirNoList.getMap("VIR_ACC_NO"));
							log.fatal("    MSG_KEY==" + msgKey);

							mfVirNoList.setMap("SGG_TR_TG", "8");	/* 웹서비스 시스템 전송상태셋팅 */
							cyberService.queryForUpdate("TXDM2421.UPDATE_TRANS_TRAFFIC_DETAIL", mfVirNoList);
							
							err1++;
						}
					}
					
					if(msgIdx % 500 == 0) {
						
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
					
					log.error("원장DATA = " + mfVirNoList.getMaps());
					log.error("SET DATA = " + listMap.getMaps());
					e.printStackTrace();
					err1=9999;
					err2=9999;
				}
				
			}
			
			log.info("[T]["+sysDsc+"][" + this.c_slf_org_nm + "]::교통유발부담금 가상계좌 전송 COUNT==" + msgIdx + ", ERR1==" + err1 + ", ERR2==" + err2);
		
			/***********************************************************************************/
			try{
				MapForm daemonMap = new MapForm();
				daemonMap.setMap("DAEMON"    , "TXDM2421");
				daemonMap.setMap("DAEMON_NM" , "교통유발가상계좌(WSDL)연계");
				daemonMap.setMap("SGG_COD"   , c_slf_org);
				daemonMap.setMap("TOTAL_CNT" , msgIdx);
				daemonMap.setMap("INSERT_CNT", msgIdx - err1 - err2);
				daemonMap.setMap("UPDATE_CNT", 0);
				daemonMap.setMap("DELETE_CNT", 0);
				daemonMap.setMap("ERROR_CNT" , err1 + err2);
				cyberService.queryForInsert("CODMBASE.InsertDaemonProcessCheck", daemonMap);
			}catch(Exception ex){
				log.debug("교통유발(ROTTNARECEIPTINFO)수납연계 대상이 없습니다. 등록 오류");
			}				
			/***********************************************************************************/
			
		}else{
			
			/***********************************************************************************/
			try{
				MapForm daemonMap = new MapForm();
				daemonMap.setMap("DAEMON"    , "TXDM2421");
				daemonMap.setMap("DAEMON_NM" , "교통유발가상계좌(WSDL)연계");
				daemonMap.setMap("SGG_COD"   , c_slf_org);
				daemonMap.setMap("TOTAL_CNT" , 0);
				daemonMap.setMap("INSERT_CNT", 0);
				daemonMap.setMap("UPDATE_CNT", 0);
				daemonMap.setMap("DELETE_CNT", 0);
				daemonMap.setMap("ERROR_CNT" , 0);
				cyberService.queryForInsert("CODMBASE.InsertDaemonProcessCheck", daemonMap);
			}catch(Exception ex){
				log.debug("교통유발(ROTTNARECEIPTINFO)수납연계 대상이 없습니다. 등록 오류");
			}				
			/***********************************************************************************/
			
		}
		
		return alVirNoList.size();
	}
	
	
	
}
