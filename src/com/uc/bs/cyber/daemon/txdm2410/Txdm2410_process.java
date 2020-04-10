/**
 *  �ֽý��۸� : �λ��û ���̹� ���漼û 
 *  ��  ��  �� : ����
 *  ��  ��  �� : ǥ�ؼ��ܼ��� �ΰ��ڷῬ��
 *  Ŭ����  ID : Txdm2410_process : �� ó�������� �ۼ��ϴ� Ŭ���� 
 *  ����  �̷� :
 * ------------------------------------------------------------------------
 *  �ۼ���      	�Ҽ�  		    ����            Tag           ����
 * ------------------------------------------------------------------------
 *  �۵���       ��ä��(��)      2011.05.11         %01%         �����ۼ�
 */
package com.uc.bs.cyber.daemon.txdm2410;

import java.math.BigDecimal;
import java.util.ArrayList;

import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.dao.DuplicateKeyException;

import com.uc.bs.cyber.CbUtil;
import com.uc.bs.cyber.daemon.Codm_BaseProcess;
import com.uc.bs.cyber.daemon.Codm_interface;
import com.uc.core.MapForm;
import com.uc.core.spring.service.UcContextHolder;

/**
 * @author Administrator
 * 20110919 ��
 * �ӵ��� �� ������ 
 * ����� �� �޸� �ؾ� ����...
 * �ϴ� ��üī���͸� ���ϰ� 
 * 100~1000������ �������� ������ 
 * �������� ������ŭ �Ѳ����� ó���ϵ��� Ʈ������� �����Ѵ�...
 * 
 */
public class Txdm2410_process extends Codm_BaseProcess implements Codm_interface {

	private MapForm dataMap            = null;
	private int insert_cnt = 0, update_cnt = 0, delete_cnt = 0, remote_up_cnt = 0, vat_cnt = 0, est_vat_del = 0;
    private int p_del_cnt = 0;
	
	/*Ʈ������� ���Ͽ� */
	MapForm gblNidLevyRows    = null;
	
	/**
	 * 
	 */
	public Txdm2410_process() {
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
		log.info("=[ǥ�ؼ��ܼ���-[" + c_slf_org_nm + "] �ΰ��ڷῬ��] Start =");
		log.info("=====================================================================");
		
		/* ó������ : 20110920 : ǥ����� �ǰ�
		 * 1. ��û�ΰ�� ������ �ΰ���ġ���� �ΰ��ǰ� ���� �������� �ΰ��� ���
		 * 2. ��û�� �����ΰ��� �ΰ���ġ���� ó��������(3)���� �����Ű�Ƿ�
		 * 3. �ΰ� ����� ó��������(3)�ΰ��� ���������� �����ϰ� 
		 * 4. �׿� �ΰ��ڷ�� ������ ���� �����Ͽ� ó���Ѵ�.
		 * 5. ��û�� ������ �����Ѵ�.
		 * */
		
		try {
				
			setContext(appContext);
			setApp(appContext);

			UcContextHolder.setCustomerType(this.dataSource);
						
			dataMap = new MapForm();  /*�ʱ�ȭ*/
			
			log.info("this.c_slf_org ======================= [" + this.c_slf_org + "]");

			dataMap.setMap("BS_NOT_IN_SEMOK",  not_in_semok());
			dataMap.setMap("SG_COD",  this.c_slf_org);
			
//			do {
//				int page = govService.getOneFieldInteger("TXDM2410.SELECT_O_LEVY_CNT", dataMap);
//				log.info("[ǥ�ؼ��ܼ���-[" + c_slf_org_nm + "]] CNT = [" + page + "]");
//				if(page == 0) break;
//				this.startJob();               /*��Ƽ Ʈ����� ȣ��*/
//				if(govService.getOneFieldInteger("TXDM2410.SELECT_O_LEVY_CNT", dataMap) == 0) {
//					break;
//				}
//			}while(true);

			int page = govService.getOneFieldInteger("TXDM2410.SELECT_O_LEVY_CNT", dataMap);
			log.info("[ǥ�ؼ��ܼ���-[" + c_slf_org_nm + "]] CNT = [" + page + "]");
			if(page>0) this.startJob();

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
		txdm2410_JobProcess();
	
	}
	
	
    /*ǥ�ؼ��ܼ���...����(��û��)*/
	private int txdm2410_JobProcess() {
		
		log.info("=====================================================================");
		log.info("=" + this.getClass().getName()+ " txdm2410_JobProcess()[ǥ�ؼ��ܼ���-[" + c_slf_org_nm + "] �ΰ��ڷῬ��] Start =");
		log.info("= govid["+this.govId+"], orgcd["+this.c_slf_org+"]");
		log.info("=====================================================================");
		
		/*�ʱ�ȭ*/
		
		insert_cnt = 0;
		update_cnt = 0; 
		delete_cnt = 0;
		remote_up_cnt = 0;
		vat_cnt       = 0;
		est_vat_del   = 0;
		p_del_cnt     = 0;
		
		/*���� �ʱ�ȭ*/
		gblNidLevyRows = new MapForm();

		/*Ʈ������� �����ϴ� �Ǿ����� ���⼭ �����Ѵ�...*/
		/*�Ǿ����� �����մϴ�.*/
		
		/*==============================================================*/
		/* ���ܼ��� �ΰ��ڷ� ��� (ó������ : 1 �ű�, 2 ����, 3 ����)
		 *  ó�������� 3�϶��� �����ϰ�
		 *  1 �̰ų� 2�϶��� INSERT�Ѵ�
		 *  INSERT �ϴ��߿� �ڷ� �ߺ��̵Ǹ� UPDATE
		 *  LIST EPAY_NO (���ڳ��ι�ȣ)�� ���ڳ��ι�ȣ�� ��´�.
		 */
		int rec_cnt = 0;
		int nul_cnt = 0;
		
		int tot_size = 0;
		
		long elapseTime1 = 0;
		long elapseTime2 = 0;
		
		elapseTime1 = System.currentTimeMillis();

		/*---------------------------------------------------------*/
		/*1. �ΰ������ڷ� ���� ó��.                               */
		/*---------------------------------------------------------*/
		
		try {

			dataMap.setMap("SGG_COD",  this.c_slf_org);

			/*�������̺� �ΰ��ڷ� SELECT ����(NIS)*/
			ArrayList<MapForm> alNidLevyList =  govService.queryForList("TXDM2410.SELECT_O_LEVY_LIST", dataMap);

			
			tot_size = alNidLevyList.size();
			
		    log.info("[" + c_slf_org_nm + "]���ܼ��Ժΰ������ڷ� �Ǽ� = [" + tot_size + "]");
			
			if (tot_size  >  0)   {
				
				/*���ڳ����ڷ� 1�Ǿ� fetch ó�� */
				for ( rec_cnt = 0;  rec_cnt < tot_size;  rec_cnt++)   {
					
					gblNidLevyRows =  alNidLevyList.get(rec_cnt);
					
					/*Ȥ�ó�...because of testing */
					if (gblNidLevyRows == null  ||  gblNidLevyRows.isEmpty() )   {
						nul_cnt++;
						continue;
					}
					
					long vat_amt = ((BigDecimal) gblNidLevyRows.getMap("VAT_TAX")).longValue();
					
					/*�⺻ Default �� ���� */
					gblNidLevyRows.setMap("BU_ADD_YN"   , (vat_amt > 0) ? "Y" : "N" ); /*�ΰ���ġ������               */
					gblNidLevyRows.setMap("TAX_CNT"     ,  0  );                       /*�ΰ����� 0                   */
					gblNidLevyRows.setMap("PROC_CLS"    , "1" );                       /*�������ä������ default '1' */
					gblNidLevyRows.setMap("DEL_YN"      , "N" );                       /*��������         default 'N' */
					gblNidLevyRows.setMap("SGG_TR_TG"   , "0" );                       /*��û����ó������*/
					
					/* �ű�, ����, ���� ���� */
					String trt_sp = (String) gblNidLevyRows.getMap("TRT_SP");   /*ó������ 1:�ű�, 2:����, 3:���� (��, ������ ������ �ٸ�)*/
					
					gblNidLevyRows.setMap("CUD_OPT", trt_sp);                   /*�ڷ��ϱ���*/
					
					long natn_tax = ((BigDecimal) gblNidLevyRows.getMap("NATN_TAX")).longValue();
					long sido_tax = ((BigDecimal) gblNidLevyRows.getMap("SIDO_TAX")).longValue();
					long gu_tax   = ((BigDecimal) gblNidLevyRows.getMap("SIGUNGU_TAX")).longValue();
					long add_tax  = ((BigDecimal) gblNidLevyRows.getMap("AFTPAYMENT_DATE1")).longValue();
					
					/* �ΰ� �ݾ��� 0 �ΰ�� �ڷ� ���� */
					//if((natn_tax + sido_tax + gu_tax + add_tax == 0) || !(gblNidLevyRows.getMap("PAY_DT").toString().equals(" "))) {
					if((natn_tax + sido_tax + gu_tax + add_tax == 0)|| trt_sp.equals("3")) {

						
						/*==========���� ��ƾ===========*/
						/*flag ó��*/
						
						try{
							
							if (cyberService.queryForDelete("TXDM2410.UPDATE_DEL_INFO_DETAIL", gblNidLevyRows) > 0) {
								delete_cnt++;
							}
							    
						}catch (Exception sub_e){
							log.error("UPDATE_DEL_INFO_DETAIL ���������� = " + gblNidLevyRows.getMaps());
							sub_e.printStackTrace();
							//throw (RuntimeException) sub_e;
						}
						
					} else {
						
						/* ó������ 1 �ű� 2 ���� 3 ���� */
						if(trt_sp.equals("1") || trt_sp.equals("2")) {
							
							/*=========== INSERT ===========*/
							try {

								cyberService.queryForUpdate("TXDM2410.INSERT_PUB_O_LEVY", gblNidLevyRows);
								
								cyberService.queryForUpdate("TXDM2410.INSERT_PUB_O_LEVY_DETAIL", gblNidLevyRows);
								
								insert_cnt++;
								
							}catch (Exception e){
								
								/*�ߺ��� �߻��ϰų� �������ǿ� ��߳��� ���üũ*/
								if (e instanceof DuplicateKeyException){
									
									try{
										
										cyberService.queryForUpdate("TXDM2410.UPDATE_PUB_O_LEVY_DETAIL", gblNidLevyRows);
										
										cyberService.queryForUpdate("TXDM2410.UPDATE_PUB_O_LEVY", gblNidLevyRows);
										
										update_cnt++;
										    
									}catch (Exception sub_e){
										log.error("UPDATE_PUB_O_LEVY_ ���������� = " + gblNidLevyRows.getMaps());
										sub_e.printStackTrace();
										throw (RuntimeException) sub_e;
									}
									
								} else {
									
									log.error("INSERT_PUB_O_LEVY_ ���������� = " + gblNidLevyRows.getMaps());
									log.error(e.getMessage());
									e.printStackTrace();							
									throw (RuntimeException) e;
									
								}
								
							}
							
						} else {
				             
							/*==========���� ��ƾ===========*/
							/*�ΰ���ġ���� ��� ������ �������� �ΰ��Ǹ� �����ڷῡ '3'���� ���õǹǷ� ���⼭ �����ȴ�...*/
							/*���������� ����*/
							try{
/*
								if (cyberService.queryForDelete("TXDM2410.DELETE_EXT_DETAIL_INFO", gblNidLevyRows) > 0 ){
									cyberService.queryForDelete("TXDM2410.DELETE_EXT_INFO", gblNidLevyRows);
									p_del_cnt ++;
								}
*/
								if (cyberService.queryForDelete("TXDM2410.UPDATE_DEL_INFO_DETAIL", gblNidLevyRows) > 0) {
									p_del_cnt ++;
								}
								
							}catch (Exception sub_e){
								log.error("UPDATE_DEL_INFO_DETAIL ���������� = " + gblNidLevyRows.getMaps());
								sub_e.printStackTrace();
								//throw (RuntimeException) sub_e;
							}							

							
						}

					}

					try{
						
						/*�������� ������Ʈ*/
						remote_up_cnt +=govService.queryForUpdate("TXDM2410.UPDATE_O_ORG_TBL", gblNidLevyRows);
						 
					}catch (Exception sub_e){
						log.error("UPDATE_O_ORG_TBL ���������� = " + gblNidLevyRows.getMaps());
						sub_e.printStackTrace();
						//throw (RuntimeException) sub_e;
					}	
					
					if(rec_cnt % 500 == 0) {
						log.info("[H][" + this.c_slf_org_nm + "][" + this.c_slf_org + "]���ܼ��Ժΰ��ڷ�ó���߰Ǽ� = [" + rec_cnt + "]");
					}
					
				}
				
				log.info("[" + c_slf_org_nm + "]���ܼ��Ժΰ��ڷῬ�� �Ǽ� [" + rec_cnt + "] ��ϰǼ� [" + insert_cnt +"] �����Ǽ� [" + update_cnt + "] �����Ǽ� [" + delete_cnt + "] ���������� [" + p_del_cnt + "]");
				log.info("�������������Ʈ ["+remote_up_cnt+"], ��ó�� �ΰ��Ǽ� [" + nul_cnt + "]");
			}

			
			elapseTime2 = System.currentTimeMillis();
			
			log.info("[" + c_slf_org_nm + "]���ܼ��� �ΰ��ڷῬ�� �ð�("+tot_size+") : " + CbUtil.formatTime(elapseTime2 - elapseTime1));
					
		} catch (Exception e) {
		    log.error("SELECT_O_LEVY_LIST ���� ������ = " + gblNidLevyRows.getMaps());
			throw (RuntimeException) e;
		}
		
		return tot_size;
	}
	
	/*
	 * �ΰ��� ������Ʈ
	 * */
	@SuppressWarnings("unused")
	private void vatUpdate(MapForm mapUpdate) {
		
		try {
			/* VAT START GOGOGOGO!!! */
			int del_cnt = 0;
			int del_de_cnt = cyberService.queryForDelete("TXDM2410.DELETE_EXT_VAT_DETAIL_INFO", mapUpdate);
			if (del_de_cnt > 0) {
				del_cnt = cyberService.queryForDelete("TXDM2410.DELETE_EXT_VAT_INFO", mapUpdate);
			}
			
			if(del_de_cnt > 0 && del_cnt > 0) {
				est_vat_del++;
			}
			
			/*�ΰ��ݾ� �� OCR��带 ������Ʈ �Ѵ�.*/
			int up_de_cnt = cyberService.queryForUpdate("TXDM2410.UPDATE_VAT_AMT_DETAIL_SAVE", mapUpdate);
			int up_cnt = cyberService.queryForUpdate("TXDM2410.UPDATE_VAT_AMT_SAVE", mapUpdate);
					
			if(up_de_cnt > 0 && up_cnt > 0) {
				vat_cnt++;
			}
			
			
		} catch (Exception e) {
			log.error("vatUpdate ���� = " + mapUpdate.getMaps());
			e.printStackTrace();
		}
	}
	
	/*���ܼ��Կ��� ���ܽ��Ѿ� �� ����*/
	private String not_in_semok() {
	
		StringBuffer sb = new StringBuffer();
		String Retval = "";
		try {
	
			ArrayList<MapForm> alNoSemokList =  cyberService.queryForList("CODMBASE.NOSEOI", null);
			
			if (alNoSemokList.size()  >  0)   {
				
				for ( int rec_cnt = 0;  rec_cnt < alNoSemokList.size();  rec_cnt++)   {
					
					MapForm mpNoSemokList =  alNoSemokList.get(rec_cnt);
					
					/*Ȥ�ó�...because of testing */
					if (mpNoSemokList == null  ||  mpNoSemokList.isEmpty() )   {
						continue;
					}
					
					sb.append("'").append(mpNoSemokList.getMap("SEMOK")).append("'").append(",");

				}
				
				Retval = sb.toString().substring(0, sb.toString().length() -1);
				
			} 
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	
		return Retval;
	}

	/*
	 * OCR_BAND ���� �� üũ
	 * */
	public String ocrCheck(String ocrStr)
	{
		String OCR_org = ocrStr;
		String OCR_dest = "";
		String OCR_comp = "";
		int[] ocrcnt= null;
		int[] chkbitcnt= null;
		String[] ocrband=null;
		
		int kind = 0;  /*0:ǥ����ǥ, 1:����ǥ, 2:����ǥ*/
		if(!OCR_org.startsWith("26"))kind=2;
		else if(OCR_org.endsWith("+++"))kind=1;

		if(kind==0) ocrcnt= new int[]{2,3,1,2,3,3,4,2,1,3,6,1,11,11,1,11,10,10,11,8,1,1,1,1};
		if(kind==1) ocrcnt= new int[]{2,3,1,2,3,3,4,2,1,3,6,1,11,11,1,11,10,10,11,8,1,1,1,1};
		if(kind==2) ocrcnt= new int[]{11,4,2,6,6,2,1,1,11,8,1,1,10,2,10,2,10,2,1,2,1,12,1,1};
		
		if(kind==0) chkbitcnt= new int[]{2,11,14,20,23};
		if(kind==1) chkbitcnt= new int[]{2,11,14,20};
		if(kind==2) chkbitcnt= new int[]{6,11,18,22,23};
		
		ocrband=new String[ocrcnt.length];
		
		for(int j=0;j<ocrcnt.length;j++)
		{
			int sumj=0;
			for(int k=0;k<=j;k++)sumj+=ocrcnt[k];
			ocrband[j]=OCR_org.substring(sumj-ocrcnt[j],sumj);
		}
		
		if(kind==0)for(int i=0;i<chkbitcnt.length;i++)ocrband[chkbitcnt[i]]=checkBitStd(ocrband, i+1);
		if(kind==1)for(int i=0;i<chkbitcnt.length;i++)ocrband[chkbitcnt[i]]=checkBitOld(ocrband, i+1);
		if(kind==2)for(int i=0;i<chkbitcnt.length;i++)ocrband[chkbitcnt[i]]=checkBitNew(ocrband, i+1);

		for(int i=0;i<ocrband.length;i++)OCR_dest+=ocrband[i];

		for(int i=0;i<OCR_org.length();i++)
		{
			if(OCR_org.substring(i,i+1).equals(OCR_dest.substring(i,i+1)))OCR_comp+=" ";
			else OCR_comp+="X";
		}
		return OCR_dest;
	}

	/*
	 * üũ��Ʈ ����
	 * */
	public String checkBitStd(String[] ocrband, int checkbit)
	{
		int sum = 0;
		String str = "";
		String rtn = "";
		
		if(checkbit==1)
		{
			for(int i=0; i<=1; i++)str += ocrband[i];
			for(int i=0; i<str.length();i++)
			{
				switch(i%3)
				{
					case 0: sum += Integer.parseInt(str.substring(i,i+1)) * 3; break;
					case 1: sum += Integer.parseInt(str.substring(i,i+1)) * 7; break;
					case 2: sum += Integer.parseInt(str.substring(i,i+1)) * 1; break;
				}
			}
			rtn = new Integer((10-(sum%10))%10).toString();
		}
		
		if(checkbit==2)
		{
			for(int i=3; i<=10; i++)str += ocrband[i];
			for(int i=0; i<str.length();i++)
			{
				switch(i%2)
				{
					case 0: sum += Integer.parseInt(str.substring(i,i+1)) * 1; break;
					case 1: sum += Integer.parseInt(str.substring(i,i+1)) * 2; break;
				}
			}
			rtn = new Integer((10-(sum%10))%10).toString();
		}
		
		if(checkbit==3)
		{
			for(int i=12; i<=13; i++)str += ocrband[i];
			for(int i=0; i<str.length();i++)
			{
				switch(i%3)
				{
					case 0: sum += Integer.parseInt(str.substring(i,i+1)) * 7; break;
					case 1: sum += Integer.parseInt(str.substring(i,i+1)) * 1; break;
					case 2: sum += Integer.parseInt(str.substring(i,i+1)) * 3; break;
				}
			}
			rtn = new Integer((10-(sum%10))%10).toString();
		}
		
		if(checkbit==4)
		{
			for(int i=15; i<=19; i++)str += ocrband[i];
			for(int i=0; i<str.length();i++)
			{
				switch(i%3)
				{
					case 0: sum += Integer.parseInt(str.substring(i,i+1)) * 3; break;
					case 1: sum += Integer.parseInt(str.substring(i,i+1)) * 7; break;
					case 2: sum += Integer.parseInt(str.substring(i,i+1)) * 1; break;
				}
			}
			rtn = new Integer((10-(sum%10))%10).toString();
		}
		
		if(checkbit==5)
		{
			for(int i=0; i<=20; i++)str += ocrband[i];
			for(int i=0; i<str.length();i++)
			{
				switch(i%4)
				{
					case 0: sum += Integer.parseInt(str.substring(i,i+1)) * 2; break;
					case 1: sum += Integer.parseInt(str.substring(i,i+1)) * 3; break;
					case 2: sum += Integer.parseInt(str.substring(i,i+1)) * 5; break;
					case 3: sum += Integer.parseInt(str.substring(i,i+1)) * 7; break;
				}
			}
			rtn = new Integer(sum%10).toString();
		}

		return rtn;
	}

	/*
	 * OCR_BAND üũ��Ʈ ���
	 * */
	public String checkBitOld(String[] ocrband, int checkbit)
	{
		int sum = 0;
		String str = "";
		String rtn = "";
		
		if(checkbit==1)
		{
			for(int i=0; i<=1; i++)str += ocrband[i];
			for(int i=0; i<str.length();i++)
			{
				sum += Integer.parseInt(str.substring(i, i+1));	
			}
			rtn = new Integer(sum%10).toString();
		}
		
		if(checkbit==2)
		{
			for(int i=3; i<=10; i++)str += ocrband[i];
			for(int i=0; i<str.length();i++)
			{
				sum += Integer.parseInt(str.substring(i, i+1));	
			}
			rtn = new Integer((10-sum%10)%10).toString();
		}
		
		if(checkbit==3)
		{
			for(int i=12; i<=13; i++)str += ocrband[i];
			for(int i=0; i<str.length();i++)
			{
				sum += Integer.parseInt(str.substring(i, i+1));	
			}
			rtn = new Integer(sum%10).toString();
		}
		
		if(checkbit==4)
		{
			for(int i=15; i<=19; i++)str += ocrband[i];
			for(int i=0; i<str.length();i++)
			{
				sum += Integer.parseInt(str.substring(i, i+1));	
			}
			rtn = new Integer((10-(sum%10))%10).toString();
		}
		
		return rtn;
	}
    
	/*
	 * OCR_BAND üũ ��Ʈ ���
	 * */
	public String checkBitNew(String[] ocrband, int checkbit)
	{
		int sum = 0;
		String str = "";
		String rtn = "";
		
		if(checkbit==1)
		{
			for(int i=0; i<=5; i++)str += ocrband[i];
			for(int i=0; i<str.length();i++)
			{
				switch(i%5)
				{
					case 0: sum += Integer.parseInt(str.substring(i,i+1)) * 1; break;
					case 1: sum += Integer.parseInt(str.substring(i,i+1)) * 7; break;
					case 2: sum += Integer.parseInt(str.substring(i,i+1)) * 5; break;
					case 3: sum += Integer.parseInt(str.substring(i,i+1)) * 3; break;
					case 4: sum += Integer.parseInt(str.substring(i,i+1)) * 2; break;
				}
			}
			rtn = new Integer(sum%10).toString();
		}
		
		if(checkbit==2)
		{
			for(int i=0; i<=10; i++)str += ocrband[i];
			for(int i=0; i<str.length();i++)
			{
				switch(i%5)
				{
					case 0: sum += Integer.parseInt(str.substring(i,i+1)) * 5; break;
					case 1: sum += Integer.parseInt(str.substring(i,i+1)) * 3; break;
					case 2: sum += Integer.parseInt(str.substring(i,i+1)) * 2; break;
					case 3: sum += Integer.parseInt(str.substring(i,i+1)) * 1; break;
					case 4: sum += Integer.parseInt(str.substring(i,i+1)) * 7; break;
				}
			}
			rtn = new Integer(sum%10).toString();
		}
		
		if(checkbit==3)
		{
			for(int i=12; i<=17; i++)str += ocrband[i];
			for(int i=0; i<str.length();i++)
			{
				switch(i%5)
				{
					case 0: sum += Integer.parseInt(str.substring(i,i+1)) * 3; break;
					case 1: sum += Integer.parseInt(str.substring(i,i+1)) * 2; break;
					case 2: sum += Integer.parseInt(str.substring(i,i+1)) * 1; break;
					case 3: sum += Integer.parseInt(str.substring(i,i+1)) * 7; break;
					case 4: sum += Integer.parseInt(str.substring(i,i+1)) * 5; break;
				}
			}
			rtn = new Integer(sum%10).toString();
		}
		
		if(checkbit==4)
		{
			for(int i=12; i<=21; i++)str += ocrband[i];
			for(int i=0; i<str.length();i++)
			{
				switch(i%5)
				{
					case 0: sum += Integer.parseInt(str.substring(i,i+1)) * 7; break;
					case 1: sum += Integer.parseInt(str.substring(i,i+1)) * 5; break;
					case 2: sum += Integer.parseInt(str.substring(i,i+1)) * 3; break;
					case 3: sum += Integer.parseInt(str.substring(i,i+1)) * 2; break;
					case 4: sum += Integer.parseInt(str.substring(i,i+1)) * 1; break;
				}
			}
			rtn = new Integer(sum%10).toString();
		}
		
		if(checkbit==5)
		{
			for(int i=0; i<=22; i++)str += ocrband[i];
			for(int i=0; i<str.length();i++)
			{
				switch(i%5)
				{
					case 0: sum += Integer.parseInt(str.substring(i,i+1)) * 1; break;
					case 1: sum += Integer.parseInt(str.substring(i,i+1)) * 7; break;
					case 2: sum += Integer.parseInt(str.substring(i,i+1)) * 5; break;
					case 3: sum += Integer.parseInt(str.substring(i,i+1)) * 3; break;
					case 4: sum += Integer.parseInt(str.substring(i,i+1)) * 2; break;
				}
			}
			rtn = new Integer(sum%10).toString();
		}
		
		return rtn;
	}
		
	
}
