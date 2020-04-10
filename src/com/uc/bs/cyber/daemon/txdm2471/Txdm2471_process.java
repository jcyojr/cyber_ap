/**
 *  �ֽý��۸� : �λ��û ���̹� ���漼û
 *  ��  ��  �� : ����
 *  ��  ��  �� : ���������ݰ��·� ������¸� ����ܿ� ����
 *  Ŭ����  ID : Txdm2461_process : �� ó�������� �ۼ��ϴ� Ŭ����
 *  ����  �̷� :
 * ------------------------------------------------------------------------
 *  �ۼ���      	�Ҽ�  		    ����            Tag           ����
 * ------------------------------------------------------------------------
 *  �۵���       ��ä��(��)      2011.06.06         %01%         �����ۼ�
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
		// TODO Auto-generated method stub
		
		//log.debug("=====================================================================");
		//log.debug("=" + this.getClass().getName()+ " runProcess() ==");
		//log.debug("=====================================================================");
		
		/*���⼭ �ð��� �����ؾ� �Ѵ�...*/
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
			log.info("======    ���� ���� :: " + curTime + "    =======");
			log.info("========================================");
			
			do {
				//�����۾��� ����
				int procCnt = txdm2471_JobProcess();
				
				if(procCnt == 0) {
					break;	
				}

			}while(true);
			
		} else {
			
			do {
				//�����۾��� ����
				int procCnt = txdm2471_JobProcess();
				
				if(procCnt == 0) {
					break;	
				}

			}while(true);
			
			log.info("========================================");
			log.info("======    ���� ���� :: " + curTime + "    =======");
			log.info("========================================");
		}
		

	}

	/*�� ����� ���� ����...*/
	@Override
	public void setDatasrc(String datasrc) {
		// TODO Auto-generated method stub
		this.dataSource = datasrc;
	}

	/*Ʈ����� ����*/
	@Override
	public void transactionJob() {
		// TODO Auto-generated method stub
	}
	
	private int txdm2471_JobProcess() {
		
		log.info("===================���������ݰ��·� �����������=====================");
		log.info("=" + this.getClass().getName()+ " txdm2471_JobProcess() Start =");
		log.info("= govid["+this.govId+"], orgcd["+this.c_slf_org+"], orgnm["+this.c_slf_org_nm+"]");
		log.info("=====================================================================");
		
		/*Ʈ������� �����ϴ� �Ǿ����� ���⼭ �����Ѵ�...*/
		/*�Ǿ����� �����մϴ�.*/
		
		/*�⺻������ ================================================================*/
		
		int levy_cnt = 0;
		
		try{
			
			//�׽�Ʈ�ڵ�(������ô� ����)
			//resourceKey = "wsdl.url2.test";
			String resourceKey = "wsdl.system.jucha";

			// System ���� ��������
			sysDsc = CbUtil.getResource("ApplicationResource" ,resourceKey);
			
			if(sysDsc.equals("test")) resourceKey = "wsdl.url.test.jucha";
			else resourceKey = "wsdl.url.jucha." + this.c_slf_org;
			
			// ��� ���� ����
			target = CbUtil.getResource("ApplicationResource" ,resourceKey);
			
			// SRC ORGCD (6260000)
			srcOrg = CbUtil.getResource("ApplicationResource" ,"wsdl.srcid.jucha"); 
			 
			resourceKey = "wsdl.dest." + this.c_slf_org;
					
			// ��� ORGCD
			desOrg = CbUtil.getResource("ApplicationResource" ,resourceKey); 

			//�׽�Ʈ�ڵ�(������ô� ����)
			if(sysDsc.equals("test")){
				desOrg="3280000";
			}
			
			ifId_vir = CbUtil.getResource("ApplicationResource" ,"wsdl.ifid.vir.jucha"); 
			
			ifId_sunab = CbUtil.getResource("ApplicationResource" ,"wsdl.ifid.sunab.jucha");
			
			/*�⺻������ ================================================================*/
			int err1 = 0, err2 = 0, msgIdx = 0;
			
			WsdlUtil wsdl = new WsdlUtil();
			
			Document xmlDoc = null;
			
			dataMap = new MapForm();

			/**
			 * 1. ������� ä������ ����
			 */
			dataMap.setMap("SGG_COD", this.c_slf_org);  /*��û�ڵ����*/
			
			@SuppressWarnings("unused")
			String rdmKey = (String.valueOf(Math.random())).substring(2, 10);
			String msgKey = CbUtil.getCurDateTimes();
			
			/*������� ä������ ��������*/
			ArrayList<MapForm> alVirNoList =  cyberService.queryForList("TXDM2471.SELECT_VIRTUAL_ACCT_PARKFINE_LIST", dataMap);
			
			levy_cnt = alVirNoList.size();
			
			log.info("[" + this.c_slf_org_nm + "]���������ݰ��·� �������ä������ �Ǽ� = " + levy_cnt);
			
			if (levy_cnt  >  0)   {
				
				log.debug("SERVICE TIME==" + CbUtil.lPadString(String.valueOf(svcTms[0]), 4, '0') + ", " + svcTms[1]);
				
				if(log.isInfoEnabled()){
					log.info("================WSDL ����=================");
					log.info("resourceKey = [" + resourceKey + "]");
					log.info("sysDsc      = [" + sysDsc + "]");
					log.info("target      = [" + target + "]");
					log.info("srcOrg      = [" + srcOrg + "]");
					log.info("desOrg      = [" + desOrg + "]");
					log.info("ifId_vir    = [" + ifId_vir + "]");
					log.info("ifId_sunab  = [" + ifId_sunab + "]");
					log.info("================WSDL ����=================");
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
							
							mfVirNoList.setMap("SGG_TR_TG", "7");	/* ������ �ý��� �����ڵ���� */
							mfVirNoList.setMap("LVY_KEY"  , mfVirNoList.getMap("LVY_KEY"));
							
							cyberService.queryForUpdate("TXDM2471.UPDATE_TRANS_PARK_DETAIL", mfVirNoList);
							err2 ++;
							
						} else {
							
							
							String resCd = xmlDoc.getElementsByTagName("res_cnt").item(0).getFirstChild().getNodeValue();
							
							if(resCd.equals("1")) {
								
								/*************  �۽Ż��¸� UPDATE �Ѵ�. ***********/							
								mfVirNoList.setMap("SGG_TR_TG", "1");	/* ������ �ý��� �����ڵ���� */
								mfVirNoList.setMap("LVY_KEY"  , mfVirNoList.getMap("LVY_KEY"));
								
								int updCnt = cyberService.queryForUpdate("TXDM2471.UPDATE_TRANS_PARK_DETAIL", mfVirNoList);
								
								//log.debug("[C][" + c_slf_org_nm + "]������� ä���ڷ� ���ۿϷ�(" + rec_cnt + ") LVY_KEY==" + mfVirNoList.getMap("LVY_KEY") + ", VIR_ACC_NO=" + mfVirNoList.getMap("VIR_ACC_NO") + "::" + updCnt);						

							} else {  // ó�� ����
								
								log.fatal("[C]������� ä���ڷ� ���ۿ��� =" + resCd + "::" + xmlDoc.getElementsByTagName("OUT_MSG").item(0).getFirstChild().getNodeValue());
								log.fatal("    LVY_KEY==" + mfVirNoList.getMap("LVY_KEY") + ", VIR_ACC_NO=" + mfVirNoList.getMap("VIR_ACC_NO"));
								log.fatal("    MSG_KEY==" + msgKey + CbUtil.fillZero(Integer.toString(msgIdx), 8));

								mfVirNoList.setMap("SGG_TR_TG", "8");	/* ������ �ý��� ���ۻ��¼��� */
								mfVirNoList.setMap("LVY_KEY"  , mfVirNoList.getMap("LVY_KEY"));
								
								cyberService.queryForUpdate("TXDM2471.UPDATE_TRANS_PARK_DETAIL", mfVirNoList);
								
								err1++;
								
							}

						}
						
						if(msgIdx % 100 == 0) {
							
							log.info("[C]" + c_slf_org_nm + "[" + this.c_slf_org + "]������� ���� COUNT==" + msgIdx + ", ERR1==" + err1 + ", ERR2==" + err2);
							
							int curTime = Integer.parseInt(CbUtil.getCurrentTime().substring(0, 4));
							
							if(curTime > svcTms[1]){ /* 21�� 30���� ������ �׸� �����ϵ��� �Ѵ�. */
								
								//log.info("========================================");
								//log.info("======    ���� ���� :: " + curTime + "    =======");
								//log.info("========================================");
								//break;
								//��ø���
							}
							
						}
						
						
					} catch (Exception e) {
						// TODO Auto-generated catch block

						e.printStackTrace();
						log.error("����DATA = " + mfVirNoList.getMaps());
						log.error("SET DATA = " + listMap.getMaps());
						err1=9999;
						err2=9999;
					}
					
				}
				
				log.info("["+sysDsc+"][" + c_slf_org_nm + "][" + this.c_slf_org + "]::���������ݰ��·� ������� ���� COUNT==" + msgIdx + ", ERR1==" + err1 + ", ERR2==" + err2);
			}
			
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		return levy_cnt;
	}

}
