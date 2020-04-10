/**
 *  �ֽý��۸� : �λ��û ���̹� ���漼û
 *  ��  ��  �� : ����
 *  ��  ��  �� : ȯ�氳���δ�� �ǽð� ���� ����
 *  Ŭ����  ID : Txdm2462_process : �� ó�������� �ۼ��ϴ� Ŭ����
 *  ����  �̷� :
 * ------------------------------------------------------------------------
 *  �ۼ���      	�Ҽ�  		    ����            Tag           ����
 * ------------------------------------------------------------------------
 *  ����ȯ       ��ä��(��)      2013.11.14         %01%         �����ۼ�
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
		 * 5 �и��� ����
		 */
		loopTerm = 60 * 5;
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
				//log.info("======    ���� ���� :: " + curTime + "    =======");
				//log.info("========================================");

				//do {
					//�����۾��� ����
			//		int procCnt = txdm2462_JobProcess();
					
					//if(procCnt == 0) {
					//	break;	
					//}

				//}while(true);
				
			//} else {

				//do {
					//�����۾��� ����
			
			int procCnt = txdm2462_JobProcess();
					
				//	if(procCnt == 0) {
				//		break;	
				//	}
//
				//}while(true);
				
				//log.info("========================================");
				//log.info("======    ���� ���� :: " + curTime + "    =======");
				//log.info("========================================");
			//}

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
	
	
	private int txdm2462_JobProcess() {
		
		//log.info("=======================ȯ�氳���δ�� �ǽð� ���� ó�� =======================");
		//log.info("=" + this.getClass().getName()+ " txdm2462_JobProcess() Start =");
		//log.info("=====================================================================");
		
		/*Ʈ������� �����ϴ� �Ǿ����� ���⼭ �����Ѵ�...*/
		/*�Ǿ����� �����մϴ�.*/
		
		/*�⺻������ ================================================================*/
		
		//�׽�Ʈ�ڵ�(������ô� ����)
		//String resourceKey = "wsdl.url2.test";
		String resourceKey = "wsdl.system.env";

		// System ���� ��������
		sysDsc = CbUtil.getResource("ApplicationResource" ,resourceKey);
		
		if(sysDsc.equals("test")) resourceKey = "wsdl.url.test.env";
		else resourceKey = "wsdl.url.ensntg." + this.c_slf_org;
		
		// ��� ���� ����
		target = CbUtil.getResource("ApplicationResource" ,resourceKey);
		
		// SRC ORGCD (6260000)
		srcOrg = CbUtil.getResource("ApplicationResource" ,"wsdl.srcid.env"); 
		 
		resourceKey = "wsdl.dest." + this.c_slf_org;
		
		// ��� ORGCD
		desOrg = CbUtil.getResource("ApplicationResource" ,resourceKey); 


		//ifId_vir :: SOINN00009 ����ID
		ifId_vir = CbUtil.getResource("ApplicationResource" ,"wsdl.ifid.sntg.env"); 
					
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
			
		sunapList = cyberService.queryForList("TXDM2462.getSunapDataList", dataMap);
		
		//log.info("[" + this.c_slf_org_nm + "]ȯ�氳���δ�� ���� �Ǽ� = " + sunapList.size());
		
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
				
				
				dataMap.setMap("IN_CNT"              , "1"); 
				
				MapForm listMap = new MapForm();
							
				listMap.setMap("IN_APPLY_YMD"        , mfVirNoList.getMap("IN_APPLY_YMD").toString());        // �迭��������
				listMap.setMap("IN_SF_TEAM_CODE"     , mfVirNoList.getMap("IN_SF_TEAM_CODE").toString());     // ��ġ��ü�ڵ�
				listMap.setMap("IN_TAX_CODE"         , mfVirNoList.getMap("IN_TAX_CODE").toString());         // ���� �ڵ���:12595921 �ü���:12595922
				// IN_LVY_NO :: �⵵ + 0 + ���(1,2) + 1 + �õ� + �ñ��� + ������ + ������ȣ :: 21�ڸ�
				listMap.setMap("IN_LVY_NO"           , mfVirNoList.getMap("IN_LVY_NO").toString());		      // ������ȣ
				listMap.setMap("IN_ANC_CHASU"        , mfVirNoList.getMap("IN_ANC_CHASU").toString());        // ��������
				listMap.setMap("IN_RECPT_YMD"        , mfVirNoList.getMap("IN_RECPT_YMD").toString());        // ��������
				listMap.setMap("IN_RECPT_AMT"        , mfVirNoList.getMap("IN_RECPT_AMT").toString());        // �����ݾ�
				// IN_PERD :: �⵵ + ���(1,2) 5�ڸ�
				listMap.setMap("IN_PERD"             , mfVirNoList.getMap("IN_PERD").toString());             // �ΰ����
				listMap.setMap("IN_OWNR_SID"         , mfVirNoList.getMap("IN_OWNR_SID").toString());         // �����ڹ�ȣ
				listMap.setMap("IN_RECPT_BANK_NM"    , mfVirNoList.getMap("IN_RECPT_BANK_NM").toString());    // �����
				listMap.setMap("IN_BANK_CODE"        , mfVirNoList.getMap("IN_BANK_CODE").toString());        // �����ڵ�
				listMap.setMap("IN_TRANSFER_YMD"     , mfVirNoList.getMap("IN_TRANSFER_YMD").toString());     // ��ü����
				listMap.setMap("IN_ACCT_YMD"         , mfVirNoList.getMap("IN_ACCT_YMD").toString());         // ȸ������
				listMap.setMap("IN_RECPT_GBN"        , mfVirNoList.getMap("IN_RECPT_GBN").toString());        // �������� �ñݰ�:1, �ݰ��:4, �ڵ���ü:5, �������:6, �ſ�ī��:7, ARS(�Ҿװ���):8 
				listMap.setMap("IN_RECPT_GUBUN"      , mfVirNoList.getMap("IN_RECPT_GUBUN").toString());	  // ó������ ����:1, �������:2	
				listMap.setMap("IN_RECPT_CANCEL_YMD" , mfVirNoList.getMap("IN_RECPT_CANCEL_YMD").toString()); // �����������				
				listMap.setMap("IN_RECPT_CANCEL_AMT" , mfVirNoList.getMap("IN_RECPT_CANCEL_AMT").toString()); // ������ұݾ�
							
				listMap.setMap("IN_LAST_MOD_TS"      , " ");                                                  // ó���Ͻ�         - ���Է�
				listMap.setMap("IN_RFLT_STATE_CODE"  , " ");                                                  // ����ó�������ڵ� - ���Է�
				listMap.setMap("IN_RECPT_ERR_CODE"   , " ");			                                      // ���������ڵ�     - ���Է�
				
				dataMap.setMap("list", listMap);
							
				
				try {
					msgIdx ++;
					//log.info("������ ����");
					xmlDoc  = wsdl.sendNRcv(ifId_vir, target, srcOrg, desOrg, msgKey + CbUtil.fillZero(Integer.toString(msgIdx), 8), dataMap);
					
					//log.info("xmlDoc :: " + xmlDoc);
					
					if(xmlDoc.getElementsByTagName("env:Fault") != null && xmlDoc.getElementsByTagName("env:Fault").getLength() > 0){
					
						log.info("Fault Code ==" + xmlDoc.getElementsByTagName("faultcode").item(0).getFirstChild().getNodeValue());
						log.info("Fault Mesg ==" + xmlDoc.getElementsByTagName("faultstring").item(0).getFirstChild().getNodeValue());
						log.info("���������� = " + mfVirNoList.getMaps());
						
						mfVirNoList.setMap("TRTG", "9");	/* ������ �ý��� �����ڵ���� */
						
						cyberService.queryForUpdate("TXDM2462.updateWsdlTrtgData", mfVirNoList);
						err2 ++;
						
					}else{
						
						String resCd = xmlDoc.getElementsByTagName("res_cnt").item(0).getFirstChild().getNodeValue();
						
						// �Ϸ�
						if(resCd.equals("1")) {
							
							/*************  �۽Ż��¸� UPDATE �Ѵ�. ***********/							
							mfVirNoList.setMap("TRTG", "1");	/* ������ �ý��� ���ۻ��¼��� - ���� */
							
							cyberService.queryForUpdate("TXDM2462.updateWsdlTrtgData", mfVirNoList);
							
							//log.info(" ������ �ý��� ���ۻ��¼��� TRTG == 1");
							updCnt++;

						}else{  // ó�� ����
							
							log.info("ȯ�氳���δ�� �ǽð� ���� ���������� ���� ");
							log.info("MSG_KEY=" + msgKey);
							log.info("���������� = " + mfVirNoList.getMaps());

							mfVirNoList.setMap("TRTG", "8");	/* ������ �ý��� ���ۻ��¼��� */
							cyberService.queryForUpdate("TXDM2462.updateWsdlTrtgData", mfVirNoList);
							
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
				daemonMap.setMap("DAEMON"    , "TXDM2462");
				daemonMap.setMap("DAEMON_NM" , "ȯ�氳���δ�� �ǽð�����(WSDL)����");
				daemonMap.setMap("SGG_COD"   , c_slf_org);
				daemonMap.setMap("TOTAL_CNT" , msgIdx);
				daemonMap.setMap("INSERT_CNT", updCnt);
				daemonMap.setMap("UPDATE_CNT", 0);
				daemonMap.setMap("DELETE_CNT", 0);
				daemonMap.setMap("ERROR_CNT" , err1 + err2);
				cyberService.queryForInsert("CODMBASE.InsertDaemonProcessCheck", daemonMap);
			}catch(Exception ex){
				log.debug("ȯ�氳���δ�� �ǽð�����(WSDL)���� ����� �����ϴ�. ��� ����");
			}				
			/***********************************************************************************/
		
		}else{
			
			/***********************************************************************************/
			try{
				MapForm daemonMap = new MapForm();
				daemonMap.setMap("DAEMON"    , "TXDM2462");
				daemonMap.setMap("DAEMON_NM" , "ȯ�氳���δ�� �ǽð�����(WSDL)����");
				daemonMap.setMap("SGG_COD"   , c_slf_org);
				daemonMap.setMap("TOTAL_CNT" , 0);
				daemonMap.setMap("INSERT_CNT", 0);
				daemonMap.setMap("UPDATE_CNT", 0);
				daemonMap.setMap("DELETE_CNT", 0);
				daemonMap.setMap("ERROR_CNT" , 0);
				cyberService.queryForInsert("CODMBASE.InsertDaemonProcessCheck", daemonMap);
			}catch(Exception ex){
				log.debug("ȯ�氳���δ�� �ǽð�����(WSDL)���� ����� �����ϴ�. ��� ����");
			}				
			/***********************************************************************************/
			//log.info("�ǽð� ���� ���� �ڷᰡ �����ϴ�.");
		}
		
		return sunapList.size();
		
	}

}
