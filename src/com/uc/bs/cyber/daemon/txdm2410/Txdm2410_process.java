/**
 *  주시스템명 : 부산시청 사이버 지방세청 
 *  업  무  명 : 연계
 *  기  능  명 : 표준세외수입 부과자료연계
 *  클래스  ID : Txdm2410_process : 실 처리로직을 작성하는 클래스 
 *  변경  이력 :
 * ------------------------------------------------------------------------
 *  작성자      	소속  		    일자            Tag           내용
 * ------------------------------------------------------------------------
 *  송동욱       유채널(주)      2011.05.11         %01%         최초작성
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
 * 20110919 송
 * 속도가 넘 느리다 
 * 방식을 좀 달리 해야 겠음...
 * 일단 전체카운터를 구하고 
 * 100~1000단위로 페이지를 나누고 
 * 페이지별 갯수만큼 한꺼번에 처리하도록 트랜잭션을 구성한다...
 * 
 */
public class Txdm2410_process extends Codm_BaseProcess implements Codm_interface {

	private MapForm dataMap            = null;
	private int insert_cnt = 0, update_cnt = 0, delete_cnt = 0, remote_up_cnt = 0, vat_cnt = 0, est_vat_del = 0;
    private int p_del_cnt = 0;
	
	/*트랜잭션을 위하여 */
	MapForm gblNidLevyRows    = null;
	
	/**
	 * 
	 */
	public Txdm2410_process() {
		// TODO Auto-generated constructor stub
		super();
		
		log = LogFactory.getLog(this.getClass());
		
		/**
		 * 5 분마다 돈다
		 */
		loopTerm = 300;
	}

	/*Context 주입용*/
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

		/*트랜잭션 업무 구현*/
		mainTransProcess();

	}

	/*트랜잭션을 실행하기 위한 함수.*/
	private void mainTransProcess(){
		
		log.info("=====================================================================");
		log.info("=[표준세외수입-[" + c_slf_org_nm + "] 부과자료연계] Start =");
		log.info("=====================================================================");
		
		/* 처리로직 : 20110920 : 표차장님 의견
		 * 1. 시청인경우 개별로 부가가치세가 부가되고 다음 통합으로 부가한 경우
		 * 2. 시청은 개별부가된 부가가치세의 처리구분은(3)으로 변경시키므로
		 * 3. 부가 연계시 처리구분이(3)인것을 물리적으로 삭제하고 
		 * 4. 그외 부가자료는 로직에 따라 연계하여 처리한다.
		 * 5. 구청의 로직과 구별한다.
		 * */
		
		try {
				
			setContext(appContext);
			setApp(appContext);

			UcContextHolder.setCustomerType(this.dataSource);
						
			dataMap = new MapForm();  /*초기화*/
			
			log.info("this.c_slf_org ======================= [" + this.c_slf_org + "]");

			dataMap.setMap("BS_NOT_IN_SEMOK",  not_in_semok());
			dataMap.setMap("SG_COD",  this.c_slf_org);
			
//			do {
//				int page = govService.getOneFieldInteger("TXDM2410.SELECT_O_LEVY_CNT", dataMap);
//				log.info("[표준세외수입-[" + c_slf_org_nm + "]] CNT = [" + page + "]");
//				if(page == 0) break;
//				this.startJob();               /*멀티 트랜잭션 호출*/
//				if(govService.getOneFieldInteger("TXDM2410.SELECT_O_LEVY_CNT", dataMap) == 0) {
//					break;
//				}
//			}while(true);

			int page = govService.getOneFieldInteger("TXDM2410.SELECT_O_LEVY_CNT", dataMap);
			log.info("[표준세외수입-[" + c_slf_org_nm + "]] CNT = [" + page + "]");
			if(page>0) this.startJob();

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
			
		/*---------------------------------------------------------*/
		/*1. 부가연계자료 업무 처리.                               */
		/*---------------------------------------------------------*/
		txdm2410_JobProcess();
	
	}
	
	
    /*표준세외수입...연계(시청만)*/
	private int txdm2410_JobProcess() {
		
		log.info("=====================================================================");
		log.info("=" + this.getClass().getName()+ " txdm2410_JobProcess()[표준세외수입-[" + c_slf_org_nm + "] 부과자료연계] Start =");
		log.info("= govid["+this.govId+"], orgcd["+this.c_slf_org+"]");
		log.info("=====================================================================");
		
		/*초기화*/
		
		insert_cnt = 0;
		update_cnt = 0; 
		delete_cnt = 0;
		remote_up_cnt = 0;
		vat_cnt       = 0;
		est_vat_del   = 0;
		p_del_cnt     = 0;
		
		/*전역 초기화*/
		gblNidLevyRows = new MapForm();

		/*트랜잭션을 포함하는 실업무는 여기서 구현한다...*/
		/*실업무를 구현합니다.*/
		
		/*==============================================================*/
		/* 세외수입 부과자료 등록 (처리구분 : 1 신규, 2 수정, 3 삭제)
		 *  처리구분이 3일때는 삭제하고
		 *  1 이거나 2일때는 INSERT한다
		 *  INSERT 하는중에 자료 중복이되면 UPDATE
		 *  LIST EPAY_NO (전자납부번호)에 전자납부번호를 담는다.
		 */
		int rec_cnt = 0;
		int nul_cnt = 0;
		
		int tot_size = 0;
		
		long elapseTime1 = 0;
		long elapseTime2 = 0;
		
		elapseTime1 = System.currentTimeMillis();

		/*---------------------------------------------------------*/
		/*1. 부가연계자료 업무 처리.                               */
		/*---------------------------------------------------------*/
		
		try {

			dataMap.setMap("SGG_COD",  this.c_slf_org);

			/*연계테이블 부과자료 SELECT 쿼리(NIS)*/
			ArrayList<MapForm> alNidLevyList =  govService.queryForList("TXDM2410.SELECT_O_LEVY_LIST", dataMap);

			
			tot_size = alNidLevyList.size();
			
		    log.info("[" + c_slf_org_nm + "]세외수입부과연계자료 건수 = [" + tot_size + "]");
			
			if (tot_size  >  0)   {
				
				/*전자납부자료 1건씩 fetch 처리 */
				for ( rec_cnt = 0;  rec_cnt < tot_size;  rec_cnt++)   {
					
					gblNidLevyRows =  alNidLevyList.get(rec_cnt);
					
					/*혹시나...because of testing */
					if (gblNidLevyRows == null  ||  gblNidLevyRows.isEmpty() )   {
						nul_cnt++;
						continue;
					}
					
					long vat_amt = ((BigDecimal) gblNidLevyRows.getMap("VAT_TAX")).longValue();
					
					/*기본 Default 값 설정 */
					gblNidLevyRows.setMap("BU_ADD_YN"   , (vat_amt > 0) ? "Y" : "N" ); /*부가가치세구분               */
					gblNidLevyRows.setMap("TAX_CNT"     ,  0  );                       /*부과순번 0                   */
					gblNidLevyRows.setMap("PROC_CLS"    , "1" );                       /*가상계좌채번구번 default '1' */
					gblNidLevyRows.setMap("DEL_YN"      , "N" );                       /*삭제여부         default 'N' */
					gblNidLevyRows.setMap("SGG_TR_TG"   , "0" );                       /*구청전송처리구분*/
					
					/* 신규, 수정, 삭제 구분 */
					String trt_sp = (String) gblNidLevyRows.getMap("TRT_SP");   /*처리구분 1:신규, 2:수정, 3:삭제 (단, 삭제는 수납과 다름)*/
					
					gblNidLevyRows.setMap("CUD_OPT", trt_sp);                   /*자료등록구분*/
					
					long natn_tax = ((BigDecimal) gblNidLevyRows.getMap("NATN_TAX")).longValue();
					long sido_tax = ((BigDecimal) gblNidLevyRows.getMap("SIDO_TAX")).longValue();
					long gu_tax   = ((BigDecimal) gblNidLevyRows.getMap("SIGUNGU_TAX")).longValue();
					long add_tax  = ((BigDecimal) gblNidLevyRows.getMap("AFTPAYMENT_DATE1")).longValue();
					
					/* 부가 금액이 0 인경우 자료 삭제 */
					//if((natn_tax + sido_tax + gu_tax + add_tax == 0) || !(gblNidLevyRows.getMap("PAY_DT").toString().equals(" "))) {
					if((natn_tax + sido_tax + gu_tax + add_tax == 0)|| trt_sp.equals("3")) {

						
						/*==========삭제 루틴===========*/
						/*flag 처리*/
						
						try{
							
							if (cyberService.queryForDelete("TXDM2410.UPDATE_DEL_INFO_DETAIL", gblNidLevyRows) > 0) {
								delete_cnt++;
							}
							    
						}catch (Exception sub_e){
							log.error("UPDATE_DEL_INFO_DETAIL 오류데이터 = " + gblNidLevyRows.getMaps());
							sub_e.printStackTrace();
							//throw (RuntimeException) sub_e;
						}
						
					} else {
						
						/* 처리구분 1 신규 2 수정 3 삭제 */
						if(trt_sp.equals("1") || trt_sp.equals("2")) {
							
							/*=========== INSERT ===========*/
							try {

								cyberService.queryForUpdate("TXDM2410.INSERT_PUB_O_LEVY", gblNidLevyRows);
								
								cyberService.queryForUpdate("TXDM2410.INSERT_PUB_O_LEVY_DETAIL", gblNidLevyRows);
								
								insert_cnt++;
								
							}catch (Exception e){
								
								/*중복이 발생하거나 제약조건에 어긋나는 경우체크*/
								if (e instanceof DuplicateKeyException){
									
									try{
										
										cyberService.queryForUpdate("TXDM2410.UPDATE_PUB_O_LEVY_DETAIL", gblNidLevyRows);
										
										cyberService.queryForUpdate("TXDM2410.UPDATE_PUB_O_LEVY", gblNidLevyRows);
										
										update_cnt++;
										    
									}catch (Exception sub_e){
										log.error("UPDATE_PUB_O_LEVY_ 오류데이터 = " + gblNidLevyRows.getMaps());
										sub_e.printStackTrace();
										throw (RuntimeException) sub_e;
									}
									
								} else {
									
									log.error("INSERT_PUB_O_LEVY_ 오류데이터 = " + gblNidLevyRows.getMaps());
									log.error(e.getMessage());
									e.printStackTrace();							
									throw (RuntimeException) e;
									
								}
								
							}
							
						} else {
				             
							/*==========삭제 루틴===========*/
							/*부가가치세의 경우 개별이 통합으로 부과되면 연계자료에 '3'으로 셋팅되므로 여기서 삭제된다...*/
							/*물리적으로 삭제*/
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
								log.error("UPDATE_DEL_INFO_DETAIL 오류데이터 = " + gblNidLevyRows.getMaps());
								sub_e.printStackTrace();
								//throw (RuntimeException) sub_e;
							}							

							
						}

					}

					try{
						
						/*고지원장 업데이트*/
						remote_up_cnt +=govService.queryForUpdate("TXDM2410.UPDATE_O_ORG_TBL", gblNidLevyRows);
						 
					}catch (Exception sub_e){
						log.error("UPDATE_O_ORG_TBL 오류데이터 = " + gblNidLevyRows.getMaps());
						sub_e.printStackTrace();
						//throw (RuntimeException) sub_e;
					}	
					
					if(rec_cnt % 500 == 0) {
						log.info("[H][" + this.c_slf_org_nm + "][" + this.c_slf_org + "]세외수입부과자료처리중건수 = [" + rec_cnt + "]");
					}
					
				}
				
				log.info("[" + c_slf_org_nm + "]세외수입부과자료연계 건수 [" + rec_cnt + "] 등록건수 [" + insert_cnt +"] 수정건수 [" + update_cnt + "] 삭제건수 [" + delete_cnt + "] 물리적삭제 [" + p_del_cnt + "]");
				log.info("고지원장업데이트 ["+remote_up_cnt+"], 미처리 부과건수 [" + nul_cnt + "]");
			}

			
			elapseTime2 = System.currentTimeMillis();
			
			log.info("[" + c_slf_org_nm + "]세외수입 부과자료연계 시간("+tot_size+") : " + CbUtil.formatTime(elapseTime2 - elapseTime1));
					
		} catch (Exception e) {
		    log.error("SELECT_O_LEVY_LIST 오류 데이터 = " + gblNidLevyRows.getMaps());
			throw (RuntimeException) e;
		}
		
		return tot_size;
	}
	
	/*
	 * 부가세 업데이트
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
			
			/*부과금액 및 OCR밴드를 업데이트 한다.*/
			int up_de_cnt = cyberService.queryForUpdate("TXDM2410.UPDATE_VAT_AMT_DETAIL_SAVE", mapUpdate);
			int up_cnt = cyberService.queryForUpdate("TXDM2410.UPDATE_VAT_AMT_SAVE", mapUpdate);
					
			if(up_de_cnt > 0 && up_cnt > 0) {
				vat_cnt++;
			}
			
			
		} catch (Exception e) {
			log.error("vatUpdate 오류 = " + mapUpdate.getMaps());
			e.printStackTrace();
		}
	}
	
	/*세외수입에서 제외시켜야 할 과목*/
	private String not_in_semok() {
	
		StringBuffer sb = new StringBuffer();
		String Retval = "";
		try {
	
			ArrayList<MapForm> alNoSemokList =  cyberService.queryForList("CODMBASE.NOSEOI", null);
			
			if (alNoSemokList.size()  >  0)   {
				
				for ( int rec_cnt = 0;  rec_cnt < alNoSemokList.size();  rec_cnt++)   {
					
					MapForm mpNoSemokList =  alNoSemokList.get(rec_cnt);
					
					/*혹시나...because of testing */
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
	 * OCR_BAND 검증 및 체크
	 * */
	public String ocrCheck(String ocrStr)
	{
		String OCR_org = ocrStr;
		String OCR_dest = "";
		String OCR_comp = "";
		int[] ocrcnt= null;
		int[] chkbitcnt= null;
		String[] ocrband=null;
		
		int kind = 0;  /*0:표준장표, 1:구장표, 2:신장표*/
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
	 * 체크비트 검증
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
	 * OCR_BAND 체크비트 계산
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
	 * OCR_BAND 체크 비트 계산
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
