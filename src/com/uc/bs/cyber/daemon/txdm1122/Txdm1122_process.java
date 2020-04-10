/**
 *  �ֽý��۸� : �λ��û ���̹� ���漼û
 *  ��  ��  �� : ����
 *  ��  ��  �� : �������ä���ڷ� ��û�� ����
 *  Ŭ����  ID : Txdm1122_process : �� ó�������� �ۼ��ϴ� Ŭ����
 *  ����  �̷� :
 * ------------------------------------------------------------------------
 *  �ۼ���      	�Ҽ�  		    ����            Tag           ���� 
 * ------------------------------------------------------------------------
 *  ��õ       ��ä��(��)      2013.11.28         %01%         �����ۼ�
 */
package com.uc.bs.cyber.daemon.txdm1122;

import java.util.ArrayList;
import java.util.Date;

import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.dao.DuplicateKeyException;

import com.uc.bs.cyber.CbUtil;
import com.uc.bs.cyber.daemon.Codm_BaseProcess;
import com.uc.bs.cyber.daemon.Codm_interface;
import com.uc.core.MapForm;
import com.uc.core.spring.service.UcContextHolder;

public class Txdm1122_process extends Codm_BaseProcess implements Codm_interface {

	private MapForm dataMap = null;
	private MapForm dataMap_604 = null;
	private int insert_cnt = 0, update_cnt = 0, remote_up_cnt = 0;
	
	/*Ʈ������� ���Ͽ� */
	MapForm virListRow = null;
	private MapForm virAccMapForm_604 = null;
	
	/**
	 * 
	 */
	public Txdm1122_process() {
		// TODO Auto-generated constructor stub
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
		// TODO Auto-generated method stub
		//log.info("=====================================================================");
		//log.info("=" + this.getClass().getName()+ " runProcess() ==");
		//log.info("=====================================================================");

		/*Ʈ����� ���� ����*/
		mainTransProcess();

	}

	/*Ʈ������� �����ϱ� ���� �Լ�.*/
	private void mainTransProcess(){
		
		log.info("=====================================================================");
		log.info("=[�������ä��(����test)-[" + c_slf_org_nm + "] �ڷ�۽�] Start =");
		log.info("=====================================================================");
		try {
				
			setContext(appContext);
			setApp(appContext);

			UcContextHolder.setCustomerType(this.dataSource);
						
			dataMap = new MapForm();  /*�ʱ�ȭ*/
			
			log.info("this.c_slf_org ======================= chuntest [" + this.c_slf_org + "]");

			dataMap.setMap("SGG_COD",  710);     //this.c_slf_org = 710      
			//dataMap.setMap("PAGE_PER_CNT"   ,  500);           /*�������� ����*/
			
			do {
				
				int virCnt = cyberService.getOneFieldInteger("TXDM1122.SELECT_VIR_COUNT", dataMap);
				
				//log.info("[�������ä��-[" + c_slf_org_nm + "]] PAGE_PER_CNT(" + dataMap.getMap("PAGE_PER_CNT") + ") �� Page���� =" + virCnt);
				log.info("[�������ä��(����test)-[" + c_slf_org_nm + "]] �Ǽ�(" + virCnt + ")");
				
				if(virCnt == 0) break;
				
				else this.startJob(); //job�� ����
				
				if(cyberService.getOneFieldInteger("TXDM1122.SELECT_VIR_COUNT", dataMap) == 0) break;
				
			} while(true);
					
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
			
		/*---------------------------------------------------------*/
		/*1. �ΰ������ڷ� ���� ó��.                               */
		/*---------------------------------------------------------*/
		txdm1122_JobProcess();
	
	}

	private int txdm1122_JobProcess() {
		
		log.info("=====================================================================");
		log.info("=" + this.getClass().getName()+ " txdm1122_JobProcess()[�������ä��(����-�����μ��� �����)-[" + c_slf_org_nm + "] �ڷ�����] Start =");
		log.info("= govid["+this.govId+"], orgcd["+this.c_slf_org+"]");
		log.info("=====================================================================");
		
		/*�ʱ�ȭ*/
		insert_cnt = 0;
		update_cnt = 0; 
		remote_up_cnt = 0;
	
		
		/*���� �ʱ�ȭ*/
		virListRow = new MapForm();

		int nul_cnt = 0;
		int cnt = 0;
		int tot_size = 0;
		
		long elapseTime1 = 0;
		long elapseTime2 = 0;
		
		elapseTime1 = System.currentTimeMillis();

		/*---------------------------------------------------------*/
		/*1. �ΰ������ڷ� ���� ó��.                               */
		/*---------------------------------------------------------*/
		
		try {
			//log.info("111=====================================================================");
			dataMap.setMap("SGG_COD",  710);
			//log.info("222====" + dataMap.getMaps() + "=================================================================");
			dataMap_604 = new MapForm();
			dataMap_604.setMap("C_SLF_ORG", 26710);
            //log.info("333=====================================================================");
			/*�������̺� �ΰ��ڷ� SELECT ����(NIS)*/
			ArrayList<MapForm> virAccList =  cyberService.queryForList("TXDM1122.SELECT_VIR_LIST", dataMap); //������¹�ȣ �ʿ�
			
			//int test =  cyberService.getOneFieldInteger("TXDM1122.SELECT_604_test", dataMap_604); //������¹�ȣ �ʿ�
			//ArrayList<MapForm> virAccList_604 =  cyberService.queryForList("TXDM1122.SELECT_VIR_LIST_604", dataMap_604); 
			//log.info("test(count)=====" + test);
			tot_size = virAccList.size();
			c_slf_org_nm = "���屺";
		    log.info("[" + c_slf_org_nm + "]�������ä���ڷ� �Ǽ�(���� C_SLF_ORG= 26710) = [" + tot_size + "]");
			
			if (tot_size  >  0)   { //tx1102_tb ���̺� ������ ������¹�ȣ�� ������, 
				
				/* �������ä���ڷḦ �ش� ��û�� fetch */
				for (cnt = 0;  cnt < tot_size;  cnt++)   {
					virListRow = new MapForm();
					virListRow =  (MapForm)virAccList.get(cnt);
					/*log.info("virListRow�� �� Ȯ�� C_SLF_ORG=" + virListRow.getMap("C_SLF_ORG"));
					log.info("virListRow�� �� Ȯ�� C_SSEMOK=" + virListRow.getMap("C_SSEMOK"));
					log.info("virListRow�� �� Ȯ�� YY_GWASE=" + virListRow.getMap("YY_GWASE"));
					log.info("virListRow�� �� Ȯ�� MM_GWASE=" + virListRow.getMap("MM_GWASE"));
					log.info("virListRow�� �� Ȯ�� V_GWASE=" + virListRow.getMap("V_GWASE"));
					log.info("virListRow�� �� Ȯ�� C_DONG=" + virListRow.getMap("C_DONG"));					
					log.info("virListRow�� �� Ȯ�� S_GWASE=" + virListRow.getMap("S_GWASE"));							
					log.info("virListRow�� �� Ȯ�� VIR_ACC=" + virListRow.getMap("VIR_ACC"));					
					*/
					virAccMapForm_604 = new MapForm();
					ArrayList<MapForm> virAccList_604_test = govService.queryForList("TXDM1122.SELECT_VIR_LIST_604", virListRow); 
					if(virAccList_604_test.size()>0){
					virAccMapForm_604 = (MapForm)virAccList_604_test.get(0);
					log.info("604�� �ΰ��ڷ� ���� : ������ȣ =" + virListRow.getMap("V_GWASE") +"������¹�ȣ =" + virListRow.getMap("VIR_ACC"));
					}
					else{
						//log.info("604�� �ΰ��ڷ� ����");	
						continue;
					}
					//log.info("V_GWASE ==============" + virListRow.getMap("V_GWASE"));
					log.info("SELECT_VIR_LIST_604 ���");
					/*Ȥ�ó�...because of testing */
					if (virListRow == null  ||  virListRow.isEmpty() )   {
						nul_cnt++;
						continue;
					}
					
					
					if(virAccMapForm_604.size() > 0){ //tx1102_tb�� 604���̺� ��ġ�Ǵ� ���� ���� ��
						/*�⺻ Default �� ���� */
						//Date date = new Date(); 
						virListRow.setMap("SGG_TR_TG"   , "1" );                       /*��û����ó������*/
						//virListRow.setMap("UPD_DT_2","");
						//virListRow.setMap("UPD_DT_2","");						
						//virAccMapForm_604.setMap("UPD_USR_2","������2");
						//virListRow.setMap("END_DT_2","");
						//virAccMapForm_604.setMap("VIR_COM_2","�λ�����");						
						//log.info("virAccMapForm_604.setMap ���");
						//log.info("virAccMapForm_604=" + virAccMapForm_604.getMaps());
						//log.info("virListRow=" + virListRow.getMaps());
						
						/*if(virAccMapForm_604.getMap("VIR_ACC") == null){ //���� ó�� ������� ������
							try {
								log.info("���� ó�� ������� ���� ���");
								cyberService.queryForUpdate("TXDM1122.VIRACC_UPDATE_FIRST", virListRow);
								
							} catch (Exception e) {
								log.error("����������(VIRACC_UPDATE_FIRST) = " + virListRow.getMaps());
								throw (RuntimeException) e;
							}
							
						}else{ //������� ���� ó���� ��
*/							
						
						
					if(virAccMapForm_604.getMap("VIR_ACC_2")== null){ //�������ڰ� ���� �� => ó�� ������� ���ķ� ������ ������¹�ȣ�� ���� ��
							log.info("virAccMapForm_604.getMap(VIR_ACC_2)== null");							
							log.info("dataMap.getMap(sgg_cod)=" + dataMap.getMap("SGG_COD"));		  
					/*if(dataMap.getMap("SGG_COD")=="710"){  //���屺�� ���, 			
*/					
					
							try{
								log.info("������Ʈ VIRACC2_UPDATE_FIRST ���");
								govService.queryForUpdate("TXDM1122.VIRACC2_UPDATE_FIRST", virListRow);
								update_cnt++;
								    
							} catch (Exception sub_e) {
								log.error("����������(VIRACC2_UPDATE_FIRST) = " + virListRow.getMaps());									
									sub_e.printStackTrace();
									throw (RuntimeException) sub_e;
							}							
					
					/*}*/
					
					}else{ //�������ڰ� ���� ��(������¸� ���� ������Ʈ �Ѱ� �ƴ� ��) 
						log.info("������¸� ���� ������Ʈ �Ѱ� �ƴ� �� else�� ���");
						log.info("dataMap.getMap(sgg_cod)=" + dataMap.getMap("SGG_COD"));	
						/*if(dataMap.getMap("SGG_COD")=="710"){  //���屺�� ���, 	
*/							try{
								log.info("TXDM1122.VIRACC2_UPDATE_NOT_FIRST ���");
								log.info("VIR_ACC=" + virListRow.getMap("VIR_ACC"));
								govService.queryForUpdate("TXDM1122.VIRACC2_UPDATE_NOT_FIRST", virListRow);
								update_cnt++;
								    
							} catch (Exception sub_e) {
								log.error("����������(VIRACC2_UPDATE_NOT_FIRST) = " + virListRow.getMaps());									
									sub_e.printStackTrace();
									throw (RuntimeException) sub_e;
							}	
						/*}*/
					}
					
						/*} ���� ó�� ������� �����϶� if�� �ݱ�*/
					try{
						
						/* ���� �����̺� ������� ���� �Ϸ� ������Ʈ */
						log.info("SGG_TR_TG�� 1�� ������Ʈ");
						remote_up_cnt += cyberService.queryForUpdate("TXDM1122.UPDATE_COMPLETE", virListRow);
						    
					} catch (Exception e) {
						
						log.error("���������� = " + virListRow.getMaps());
						log.error(e.getMessage());
						e.printStackTrace();
						throw (RuntimeException) e;
						
					}
					
					}else{ //tx1102_tb�� 604���̺� ��ġ�Ǵ� ���� ���� ��
						//scon604�� �ΰ��ڷᰡ ����
						System.out.println("scon604�� �ΰ��ڷᰡ ����");
						 log.info("scon604�� �ΰ��ڷᰡ ����");
					}
					
				}
				
				log.info("[" + c_slf_org_nm + "]�������ä�� �Ǽ� [" + cnt + "] ��ϰǼ� [" + insert_cnt +"] �����Ǽ� [" + update_cnt + "]");
				log.info("���漼�����̺������Ʈ ["+remote_up_cnt+"], ��ó�� �ΰ��Ǽ� [" + nul_cnt + "]");
			}

			elapseTime2 = System.currentTimeMillis();
			
			log.info("[" + c_slf_org_nm + "]�������ä�� ���� �ð�("+tot_size+") : " + CbUtil.formatTime(elapseTime2 - elapseTime1));
					
		} catch (Exception e) {
		    log.error("���� ������11 = " + virListRow.getMaps());
			throw (RuntimeException) e;
		}
		
		return tot_size;
	}
}
