/**
 *  �ֽý��۸� : �λ��û ���̹� ���漼û
 *  ��  ��  �� : ����
 *  ��  ��  �� : ���������ݰ��·� �ǽð� ���� ����
 *  Ŭ����  ID : Txdm2472_process : �� ó�������� �ۼ��ϴ� Ŭ����
 *  ����  �̷� :
 * ------------------------------------------------------------------------
 *  �ۼ���      	�Ҽ�  		    ����            Tag           ����
 * ------------------------------------------------------------------------
 *  ������       ��ä��(��)      2014.11.27         %01%         �����ۼ�
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
		 * 5 �и��� ����
		 */
		loopTerm = 60*5;
	}
	
	/*Context ���Կ�*/
	public void setApp(ApplicationContext context) {
		this.context = context;
	}
	
	
	/* process starting */
	@Override
	public void runProcess() throws Exception {

		mainTransProcess();
	}

	/*Ʈ������� �����ϱ� ���� �Լ�.*/
	private void mainTransProcess(){

		try {
				
			setContext(appContext);
			setApp(appContext);

			UcContextHolder.setCustomerType(this.dataSource);

			// System ���� ��������
			sysDsc = CbUtil.getResource("ApplicationResource","wsdl.system.jucha");
			// ��� ���� ����
			target = CbUtil.getResource("ApplicationResource","wsdl.url.jusntg."+this.c_slf_org);
			// SRC ORGCD (6260000)
			srcOrg = CbUtil.getResource("ApplicationResource","wsdl.srcid.jucha");
			// ��� ORGCD
			desOrg = CbUtil.getResource("ApplicationResource","wsdl.dest."+this.c_slf_org); 
			//ifId_vir :: NTDNN00005 ����ID
			ifId_vir = CbUtil.getResource("ApplicationResource","wsdl.ifid.sntg.jucha"); 
			/*
			if(log.isInfoEnabled()){
				log.info("================WSDL ����=================");
				log.info("c_slf_org = [" + this.c_slf_org + "]");
				log.info("sysDsc      = [" + sysDsc + "]");
				log.info("target      = [" + target + "]");
				log.info("srcOrg      = [" + srcOrg + "]");
				log.info("desOrg      = [" + desOrg + "]");
				log.info("ifId_vir    = [" + ifId_vir + "]");				
				log.info("================WSDL ����=================");
			}
			*/
			
			dataMap = new MapForm();  /*�ʱ�ȭ*/
			dataMap.setMap("SGG_COD", this.c_slf_org);

			int page=0;
			try{
				page = cyberService.getOneFieldInteger("TXDM2472.SELECT_TX2211_CNT", dataMap);
				log.info("[" + this.c_slf_org_nm + "] ���������ݰ��·� ���� �Ǽ� = "+page);
			} catch (Exception sub_e) {
				log.error("[" + this.c_slf_org + "] SELECT_TX2211_CNT ����");
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
					log.error("[" + this.c_slf_org + "] SELECT_TRN_SNO ����");
					sub_e.printStackTrace();
				}

				try{
					cyberService.queryForUpdate("TXDM2472.UPDATE_TX2211_CNT", dataMap);
				} catch (Exception sub_e) {
					log.error("[" + this.c_slf_org + "] UPDATE_TX2211_CNT ����");
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

	/*Ʈ����� ����*/
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

		/*�ű� ���� �����۰�*/
		ArrayList<MapForm> sunapList = new ArrayList<MapForm>();
		
		try {
			sunapList = cyberService.queryForList("TXDM2472.SELECT_TX2211_LIST", dataMap);
		} catch (Exception sub_e) {
			log.error("[" + this.c_slf_org + "] SELECT_TX2211_LIST ����");
			sub_e.printStackTrace();
		}
		
		if(sunapList.size()>0){

			for(int i=0;i<sunapList.size();i++){

				MapForm mfVirNoList=sunapList.get(i);
				
				if(mfVirNoList==null || mfVirNoList.isEmpty()){
					log.info("ó�� �ȵǰ� �ѱ�");
					continue;
				}
				
				String TAX_NO=mfVirNoList.getMap("TAX_NO").toString();
				
				if(mfVirNoList.getMap("A_VIOL_YM").toString().equals("X")||mfVirNoList.getMap("A_VIOL_MAN_SNO").toString().equals("X")){

					log.info("���������ݰ��·� VIOL_YM, VIOL_MAN_SNO ���� �ڷ�, ���������� ���ۺҰ��ڷ� ["+TAX_NO+"]");
					mfVirNoList.setMap("TRTG", "7");
					err7++;
				}else{

					MapForm wsdlMap = new MapForm();
					MapForm wsdlListMap = new MapForm();
					
					wsdlMap.setMap("in_cnt"              , "1"); // ���۰���
					wsdlListMap.setMap("in_sf_team_code"     , mfVirNoList.getMap("IN_SF_TEAM_CODE").toString());     // ��ġ��ü�ڵ�
	                wsdlListMap.setMap("in_deal_gbn_code"    , mfVirNoList.getMap("IN_DEAL_GBN_CODE").toString());    // ó������			
	                wsdlListMap.setMap("in_anc_chasu"        , mfVirNoList.getMap("IN_ANC_CHASU").toString());        // ��������
	                wsdlListMap.setMap("in_tax_code"         , mfVirNoList.getMap("IN_TAX_CODE").toString());         // ����
	                wsdlListMap.setMap("in_lvy_no"           , mfVirNoList.getMap("IN_LVY_NO").toString());		      // ������ȣ
	                wsdlListMap.setMap("in_anc_chasu"        , mfVirNoList.getMap("IN_ANC_CHASU").toString());        // ��������
	                wsdlListMap.setMap("in_recpt_chasu"      , mfVirNoList.getMap("IN_RECPT_CHASU").toString());      // ��������
	                wsdlListMap.setMap("in_recpt_ymd"        , mfVirNoList.getMap("IN_RECPT_YMD").toString());        // ��������
					wsdlListMap.setMap("in_recpt_amt"        , mfVirNoList.getMap("IN_RECPT_AMT").toString());        // �����ݾ�
					wsdlListMap.setMap("in_lvy_yy"           , mfVirNoList.getMap("IN_LVY_YY").toString());           // �ΰ��⵵
					wsdlListMap.setMap("in_lvy_mon"          , mfVirNoList.getMap("IN_LVY_MON").toString());          // �ΰ���
					wsdlListMap.setMap("in_lvy_dep_code"     , mfVirNoList.getMap("IN_LVY_DEP_CODE").toString());     // �μ��ڵ�
					wsdlListMap.setMap("in_lvy_gbn"          , mfVirNoList.getMap("IN_LVY_GBN").toString());          // ��������
					wsdlListMap.setMap("in_rgt_mbd_reg_no"   , mfVirNoList.getMap("IN_RGT_MBD_REG_NO").toString());   // �����ڹ�ȣ
					wsdlListMap.setMap("in_bank_nm"          , mfVirNoList.getMap("IN_BANK_NM").toString());          // ����� 
					wsdlListMap.setMap("in_iche_ymd"         , mfVirNoList.getMap("IN_ICHE_YMD").toString());	      // ��ü����	
					wsdlListMap.setMap("in_acct_ymd"         , mfVirNoList.getMap("IN_ACCT_YMD").toString());         // ȸ������				
					wsdlListMap.setMap("in_recpt_cancel_ymd" , mfVirNoList.getMap("IN_RECPT_CANCEL_YMD").toString()); // �����������
					wsdlListMap.setMap("in_recpt_cancel_amt" , mfVirNoList.getMap("IN_RECPT_CANCEL_AMT").toString()); // ������ұݾ�
					wsdlListMap.setMap("in_vir_acc_no"       , mfVirNoList.getMap("IN_VIR_ACC_NO").toString());       // ������¹�ȣ
					wsdlListMap.setMap("in_viol_ym"          , mfVirNoList.getMap("IN_VIOL_YM").toString());          // ������
					wsdlListMap.setMap("in_viol_man_sno"     , mfVirNoList.getMap("IN_VIOL_MAN_SNO").toString());     // �����Ϸù�ȣ	

					wsdlMap.setMap("list", wsdlListMap);
					
					try {
						msgIdx++;
						xmlDoc =  wsdl.sendNRcv(ifId_vir, target, srcOrg, desOrg, msgKey + CbUtil.fillZero(Integer.toString(msgIdx), 8), wsdlMap); 
						//log.info("xmlDoc :: " + xmlDoc);
						
						if(xmlDoc.getElementsByTagName("env:Fault") != null && xmlDoc.getElementsByTagName("env:Fault").getLength() > 0){
						
							//log.info("Fault Code ==" + xmlDoc.getElementsByTagName("faultcode").item(0).getFirstChild().getNodeValue());
							//log.info("Fault Mesg ==" + xmlDoc.getElementsByTagName("faultstring").item(0).getFirstChild().getNodeValue());
							//log.info("���������� = " + mfVirNoList.getMaps());
							//log.info("���������ݰ��·� �ǽð� ���� ������ �ý��� �����ڵ�");
							log.info("["+TAX_NO+"] WSDL ���� �ý��ۿ���, xmlDoc :"+xmlDoc);
							mfVirNoList.setMap("TRTG", "9");	/* ������ �ý��� �����ڵ���� */
							err9++;
							
						}else{
							
							String resCd = xmlDoc.getElementsByTagName("res_cnt").item(0).getFirstChild().getNodeValue();
							if(resCd.equals("1")) {
								mfVirNoList.setMap("TRTG","1");
								updCnt++;
							}else{// ó�� ����
								//log.info("MSG_KEY=" + msgKey);
								//log.info("���������� = " + mfVirNoList.getMaps());
								log.info("["+TAX_NO+"] WSDL ���ۿ���, �����ڵ�(resCd):" + resCd);

								mfVirNoList.setMap("TRTG","8");	/* ������ �ý��� ���ۻ��¼��� */
								err8++;
							}
						}
						
						// TRTG ������Ʈ
						try {
							cyberService.queryForUpdate("TXDM2472.UPDATE_TX2211_TRTG", mfVirNoList);
						} catch (Exception sub_e) {
							log.error("[" + this.c_slf_org + "] UPDATE_TX2211_TRTG ����");
							sub_e.printStackTrace();
						}
						
					} catch (Exception e) {
						log.error("���������� = " + mfVirNoList.getMaps());
						e.printStackTrace();
					}
				}
			}
			
			log.info("�ǽð����� ����> ��ü:"+msgIdx+", �Ϸ�:"+updCnt+", ���ۺҰ��ڷ�:"+err7+", WSDL���ۿ���(�����):" + err8+", WSDL�ý��ۿ���:" + err9);

		}

	}

}

