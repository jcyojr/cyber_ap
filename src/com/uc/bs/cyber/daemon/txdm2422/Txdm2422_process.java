/**
 *  �ֽý��۸� : �λ��û ���̹� ���漼û
 *  ��  ��  �� : ����
 *  ��  ��  �� : �������ߺδ�� �ǽð� ���� ����
 *  Ŭ����  ID : Txdm2422_process : �� ó�������� �ۼ��ϴ� Ŭ����
 *  ����  �̷� :
 * ------------------------------------------------------------------------
 *  �ۼ���      	�Ҽ�  		    ����            Tag           ����
 * ------------------------------------------------------------------------
 *  ������       ��ä��(��)      2014.11.27         %01%         �����ۼ�
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
		 * 5 �и��� ����
		 */
		loopTerm = 300;
	}
	
	/*Context ���Կ�*/
	public void setApp(ApplicationContext context) {
		this.context = context;
	}
	
	
	/* process starting */
	@Override
	public void runProcess() throws Exception {
		

		try {
			
			
			/*���⼭ �ð��� �����ؾ� �Ѵ�...*/
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

	/*�� ����� ���� ����...*/
	@Override
	public void setDatasrc(String datasrc) {

		this.dataSource = datasrc;
	}

	/*Ʈ����� ����*/
	@Override
	public void transactionJob() {
			
	}
	
	
	private int txdm2422_JobProcess() {
		
		log.info("=======================�������ߺδ�� �ǽð� ���� ó�� =======================");
		log.info("=" + this.getClass().getName()+ " txdm2422_JobProcess() Start =");
		log.info("=====================================================================");
		
		/*Ʈ������� �����ϴ� �Ǿ����� ���⼭ �����Ѵ�...*/
		/*�Ǿ����� �����մϴ�.*/
		
		/*�⺻������ ================================================================*/
		
		//�׽�Ʈ�ڵ�(������ô� ����)
		//String resourceKey = "wsdl.url2.test";
		String resourceKey = "wsdl.system.road";

		// System ���� ��������
		sysDsc = CbUtil.getResource("ApplicationResource" ,resourceKey);
		
		if(sysDsc.equals("test")) resourceKey = "wsdl.url.test.road";
		else resourceKey = "wsdl.url.rosntg." + this.c_slf_org;
		
		// ��� ���� ����
		target = CbUtil.getResource("ApplicationResource" ,resourceKey);
		
		// SRC ORGCD (6260000)
		srcOrg = CbUtil.getResource("ApplicationResource" ,"wsdl.srcid.road"); 
		 
		resourceKey = "wsdl.dest." + this.c_slf_org;
		
		// ��� ORGCD
		desOrg = CbUtil.getResource("ApplicationResource" ,resourceKey); 


		//ifId_vir :: NTDNN00099 ����ID
		ifId_vir = CbUtil.getResource("ApplicationResource" ,"wsdl.ifid.sntg.road"); 
					
		/*�⺻������ ================================================================*/
		int err1 = 0, err2 = 0, msgIdx = 0;
		int updCnt = 0;

		WsdlUtil wsdl = new WsdlUtil();
		
		Document xmlDoc = null;
		
		dataMap = new MapForm();

		/**
		 * 1. �ǽð� �����ڷ� ����
		 */
		
		//��û���� �������� �ʰ� ��ü�� �����ͼ� 5�д����� ������...
		dataMap.setMap("SGG_COD", this.c_slf_org);  /*��û�ڵ����*/
		dataMap.setMap("TRTG", "1");
		
		@SuppressWarnings("unused")
		String rdmKey = (String.valueOf(Math.random())).substring(2, 10);
		String msgKey = CbUtil.getCurDateTimes();

		/*�ű� ���� �����۰�*/
		ArrayList<MapForm> sunapList = new ArrayList<MapForm>();
			
		sunapList = cyberService.queryForList("TXDM2422.getSunapDataList", dataMap);
		
		log.info("[" + this.c_slf_org_nm + "]�������ߺδ�� ���� �Ǽ� = " + sunapList.size());
		
		if(sunapList.size() > 0){
			
			//log.debug("SERVICE TIME==" + svcTms[0] + ", " + svcTms[1]);
			
			if(log.isInfoEnabled()){
				log.info("================WSDL ����=================");
				log.info("resourceKey = [" + resourceKey + "]");
				log.info("sysDsc      = [" + sysDsc + "]");
				log.info("target      = [" + target + "]");
				log.info("srcOrg      = [" + srcOrg + "]");
				log.info("desOrg      = [" + desOrg + "]");
				log.info("ifId_vir    = [" + ifId_vir + "]");				
				log.info("================WSDL ����=================");
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
							
				listMap.setMap("sf_team_code"     , mfVirNoList.getMap("SF_TEAM_CODE").toString()); // ��ġ��ü�ڵ�
				listMap.setMap("taxin_code"       , mfVirNoList.getMap("TAXIN_CODE").toString());      // ����
				listMap.setMap("lvy_no"           , mfVirNoList.getMap("LVY_NO").toString());		   // ������ȣ     
				listMap.setMap("anc_chasu"        , mfVirNoList.getMap("ANC_CHASU").toString());       // ��������     
				listMap.setMap("recpt_chasu"      , mfVirNoList.getMap("RECPT_CHASU").toString());     // ��������     
				listMap.setMap("recpt_ymd"        , mfVirNoList.getMap("RECPT_YMD").toString());       // ��������     
				listMap.setMap("recpt_am"         , mfVirNoList.getMap("RECPT_AM").toString());        // �����ݾ�     
				listMap.setMap("lvy_yy"           , mfVirNoList.getMap("LVY_YY").toString());          // �ΰ��⵵     
				listMap.setMap("lvy_mm"           , mfVirNoList.getMap("LVY_MM").toString());          // �ΰ���       
				listMap.setMap("dep_code"         , mfVirNoList.getMap("DEP_CODE").toString());        // �μ��ڵ�     
				listMap.setMap("gubun"            , mfVirNoList.getMap("GUBUN").toString());           // ���         
				listMap.setMap("owner_sid"        , mfVirNoList.getMap("OWNER_SID").toString());       // �����ڹ�ȣ   
				listMap.setMap("recpt_bank_nm"    , mfVirNoList.getMap("RECPT_BANK_NM").toString());   // �����       
				listMap.setMap("transfer_ymd"     , mfVirNoList.getMap("TRANSFER_YMD").toString());	   // ��ü����	    
				listMap.setMap("acct_ymd"         , mfVirNoList.getMap("ACCT_YMD").toString());        // ȸ������			
				listMap.setMap("recpt_gubun"      , mfVirNoList.getMap("RECPT_GUBUN").toString());     // ó������     
				listMap.setMap("recpt_cancel_ymd" , mfVirNoList.getMap("RECPT_CANCEL_YMD").toString());// ����������� 
				listMap.setMap("recpt_cancel_am"  , mfVirNoList.getMap("RECPT_CANCEL_AM").toString()); // ������ұݾ� 
				listMap.setMap("last_mod_ts"      , mfVirNoList.getMap("LAST_MOD_TS").toString());     // ��������     
				listMap.setMap("recpt_sr_code"    , mfVirNoList.getMap("RECPT_SR_CODE").toString());   // ���������ڵ�
				listMap.setMap("asgnmt_list1"     , mfVirNoList.getMap("ASGNMT_LIST1").toString());    // ���1
				listMap.setMap("asgnmt_list2"     , mfVirNoList.getMap("ASGNMT_LIST2").toString());    // ���2
				listMap.setMap("asgnmt_list3"     , mfVirNoList.getMap("ASGNMT_LIST3").toString());    // ���3
				listMap.setMap("asgnmt_list4"     , mfVirNoList.getMap("ASGNMT_LIST4").toString());    // ���4
				listMap.setMap("asgnmt_list5"     , mfVirNoList.getMap("ASGNMT_LIST5").toString());    // ���5
				
				dataMap.setMap("list", listMap);
				
				try {
					msgIdx ++;
					log.info("�������ߺδ�� ������ ����");
					xmlDoc  =  wsdl.sendNRcv(ifId_vir, target, srcOrg, desOrg, msgKey + (String.valueOf(Math.random())).substring(2, 10), listMap);		
					
					log.info("xmlDoc :: " + xmlDoc);
					
					String resCd = xmlDoc.getElementsByTagName("success").item(0).getFirstChild().getNodeValue();
					
					log.info("resCd ==> " + resCd);

					if(xmlDoc.getElementsByTagName("env:fault") != null && xmlDoc.getElementsByTagName("env:fault").getLength() > 0){
					
						log.info("Fault Code ==" + xmlDoc.getElementsByTagName("faultcode").item(0).getFirstChild().getNodeValue());
						log.info("Fault Mesg ==" + xmlDoc.getElementsByTagName("faultstring").item(0).getFirstChild().getNodeValue());
						log.info("���������� = " + mfVirNoList.getMaps());
						log.info("�������ߺδ�� �ǽð� ���� ������ �ý��� �����ڵ�");					
						
						mfVirNoList.setMap("TRTG", "9");	/* ������ �ý��� �����ڵ���� */
						
						cyberService.queryForUpdate("TXDM2422.updateWsdlTrtgData", mfVirNoList);
						err2 ++;
						
					}else{						

						// �Ϸ�
						if(resCd.equals("Y")) {
							
							/*************  �۽Ż��¸� UPDATE �Ѵ�. ***********/							
							mfVirNoList.setMap("TRTG", "1");	/* ������ �ý��� ���ۻ��¼��� - ���� */
							
							cyberService.queryForUpdate("TXDM2422.updateWsdlTrtgData", mfVirNoList);
							
							//log.info(" ������ �ý��� ���ۻ��¼��� TRTG == 1");
							updCnt++;

						}else{  // ó�� ����
							
							log.info("�������ߺδ�� �ǽð� ���� ���������� ���� ");
							log.info("MSG_KEY=" + msgKey);
							log.info("���������� = " + mfVirNoList.getMaps());

							mfVirNoList.setMap("TRTG", "8");	/* ������ �ý��� ���ۻ��¼��� */
							cyberService.queryForUpdate("TXDM2422.updateWsdlTrtgData", mfVirNoList);
							
							err1++;
							
						}

					}

					
				} catch (Exception e) {
					log.error("���������� = " + mfVirNoList.getMaps());
					e.printStackTrace();
				}
				
			}
			
			log.info("�ǽð����� ���� COUNT(��ü)==" + msgIdx + ", �Ϸ� : "+updCnt+", ERR1(�����񽺿���)==" + err1 + ", ERR2(�����ڵ弼��)==" + err2);
			
			/***********************************************************************************/
			try{
				MapForm daemonMap = new MapForm();
				daemonMap.setMap("DAEMON"    , "TXDM2422");
				daemonMap.setMap("DAEMON_NM" , "�������ߺδ�� �ǽð�����(WSDL)����");
				daemonMap.setMap("SGG_COD"   , c_slf_org);
				daemonMap.setMap("TOTAL_CNT" , msgIdx);
				daemonMap.setMap("INSERT_CNT", updCnt);
				daemonMap.setMap("UPDATE_CNT", 0);
				daemonMap.setMap("DELETE_CNT", 0);
				daemonMap.setMap("ERROR_CNT" , err1 + err2);
				cyberService.queryForInsert("CODMBASE.InsertDaemonProcessCheck", daemonMap);
			}catch(Exception ex){
				log.debug("�������ߺδ�� �ǽð�����(WSDL)���� �α� ��� ����");
			}				
			/***********************************************************************************/
			
		}else{
			
			/***********************************************************************************/
			try{
				MapForm daemonMap = new MapForm();
				daemonMap.setMap("DAEMON"    , "TXDM2422");
				daemonMap.setMap("DAEMON_NM" , "�������ߺδ�� �ǽð�����(WSDL)����");
				daemonMap.setMap("SGG_COD"   , c_slf_org);
				daemonMap.setMap("TOTAL_CNT" , 0);
				daemonMap.setMap("INSERT_CNT", 0);
				daemonMap.setMap("UPDATE_CNT", 0);
				daemonMap.setMap("DELETE_CNT", 0);
				daemonMap.setMap("ERROR_CNT" , 0);
				cyberService.queryForInsert("CODMBASE.InsertDaemonProcessCheck", daemonMap);
			}catch(Exception ex){
				log.debug("�������ߺδ�� �ǽð�����(WSDL)���� ����� �����ϴ�. ��� ����");
			}				
			/***********************************************************************************/
			
			//log.info("�ǽð� ���� ���� �ڷᰡ �����ϴ�.");
		}
		
		return sunapList.size();
		
	}

}
