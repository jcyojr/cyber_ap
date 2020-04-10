/**
 *  주시스템명 : 사이버지방세청 고도화
 *  업  무  명 : 결제원-환경개선부담금 납부결과통지/ 예약신청 
 *  기  능  명 : 결제원-사이버세청 
 *               업무처리
 *               
 *  클래스  ID : Kf202001Service
 *  변경  이력 :
 * ------------------------------------------------------------------------
 *  작성자     소속  		 일자      Tag   내용
 * ------------------------------------------------------------------------   
 * 소도우   유채널(주)    2011.06.13   %01%  신규작성
 *  
 */
package com.uc.bs.cyber.service.kf202001;

import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;

import com.uc.core.MapForm;
import com.uc.core.spring.service.IbatisBaseService;
import com.uc.bs.cyber.CbUtil;

/**
 * @author Administrator
 *
 */
public class Kf202001Service {

	protected Log log = LogFactory.getLog(this.getClass());
	
	private ApplicationContext appContext = null;
	
	private IbatisBaseService sqlService_cyber = null;
		
	private Kf202001FieldList kfField = null;
	
	private int[] svcTms = new int[2];
	
	CbUtil cUtil = null;
	
	/**
	 * 생성자
	 */
	public Kf202001Service(ApplicationContext appContext) {
		// TODO Auto-generated constructor stub
		
		setAppContext(appContext);
		
		kfField = new Kf202001FieldList();
		
	}

	/* appContext property 생성 */
	public void setAppContext(ApplicationContext appContext) {
		
		this.appContext = appContext;
		
		sqlService_cyber = (IbatisBaseService) this.appContext.getBean("baseService_cyber");

	}

	public ApplicationContext getAppContext() {
		return appContext;
	}
	
	/**
	 * 환경개선부담금 납부결과 통지 / 예약납부
	 * */
	public byte[] chk_kf_202001(MapForm headMap, MapForm mf) throws Exception {
		
		/*환경개선부담금 고지내역조회 시 사용 */
		String resCode = "000";       /*응답코드*/
		String giro_no = "1002641";   /*부산시지로코드*/
		String sg_code = "26";        /*부산시기관코드*/
		int curTime = 0;

		try{
			
			/*서비스 시간*/
			svcTms[0] = 0030;
			svcTms[1] = 2200;
			
			
			cUtil = CbUtil.getInstance();
			
			log.info(cUtil.msgPrint("3", "S","KP202001 chk_kf_202001()", resCode));
			
			/*전문체크*/
			
			/*기관*/
			if(!headMap.getMap("GPUB_CODE").equals(sg_code)){
				resCode = "339";
			}
			/*발행기관지로코드*/
			if(!headMap.getMap("GJIRO_NO").equals(giro_no)){
				resCode = "324";
			}


			/*결제원에서 수신한 전문을 받아 조회결과를 확인한다.*/
			
			ArrayList<MapForm>  kfAl202001List = sqlService_cyber.queryForList("KF202001.SELECT_ENV_PAY_LIST", mf);
			
			int totCnt = kfAl202001List.size();
			
			if(totCnt == 0) {
				resCode = "000";   /*납부내역없음*/
			} else {
				
				/*납부내역 있음*/
				MapForm mfCmd202001List = kfAl202001List.get(0);
				
				if ( mfCmd202001List == null  ||  mfCmd202001List.isEmpty() )   {
					/*납부내역없음*/
					resCode = "000";
				} else {
					/*납부취소내역이 있는 경우*/
					if(mfCmd202001List.getMap("SNTG").equals("9")) {  /*취소*/
						
						resCode = "000";
						
					} else {
						
						resCode = "331";  /*납부기수신*/

					}
				}
				
			}
			/* 이체통보에 납부시간 체크는 의미가 없다.			
			curTime = Integer.parseInt(headMap.getMap("TX_DATE").toString().substring(6, 10));
			if (curTime >= svcTms[0] && curTime <= svcTms[1]) {
				
			} else {
				resCode = "092";
			}
			*/
			/*납부내역이 존재하지 않고 취소된 경우 납부처리*/
			if(resCode.equals("000")){
				
				/*부과리스트 검색*/
				ArrayList<MapForm>  alKfLevyList = sqlService_cyber.queryForList("KF201001.SELECT_ENV_SEARCH_LIST", mf);
				
				if(alKfLevyList.size() == 0) {
					
					resCode = "311";  /*고지내역없음*/
					
				} else if(alKfLevyList.size() > 1) {
					
					resCode = "094";  /*고지내역2건 1건인것만 처리*/
					
				} else {
					
					MapForm mpKfLevyList = alKfLevyList.get(0);
					
					/*전문정보조립*/
					/*전자수납테이블에 수납순번을 구한다. 중복처리가 가능하므로...*/
					mpKfLevyList.setMap("PAY_CNT" , sqlService_cyber.getOneFieldInteger("KF202001.TX2231_TB_MaxPayCnt", mpKfLevyList));
					mpKfLevyList.setMap("SUM_RCP" , mf.getMap("NAPBU_AMT"));   /*납부금액*/
					mpKfLevyList.setMap("PAY_DT"  , mf.getMap("NAPBU_DATE"));  /*수납일자*/
					mpKfLevyList.setMap("SNTG"    , "1");                      /*납부상태*/
					mpKfLevyList.setMap("SNSU"    , "1");                      /*수납매체 1 금융결제원*/
					mpKfLevyList.setMap("BANK_CD" , CbUtil.lPadString(mf.getMap("OUTBANK_CODE").toString(), 7, '0').substring(0,3));    /*납부기관*/
					mpKfLevyList.setMap("BRC_NO"  , CbUtil.lPadString(mf.getMap("OUTBANK_CODE").toString(), 7, '0'));                   /*(수납)납부기관*/
					mpKfLevyList.setMap("RSN_YN"  , (mf.getMap("NAPBU_GUBUN").equals("R")) ? "Y" : "N");     /*납부구분 Q예약  R조회*/
					mpKfLevyList.setMap("TRTG"    , "0");                      /*자료전송상태*/
					mpKfLevyList.setMap("TMSG_NO" , headMap.getMap("BCJ_NO")); /*출금은행_전문관리번호*/
					
					int intLog = sqlService_cyber.queryForUpdate("KF202001.INSERT_TX2231_TB_EPAY", mpKfLevyList);
					
					if(intLog > 0) {
						
						sqlService_cyber.queryForUpdate("KF202001.UPDATE_TX2132_TB_NAPBU_INFO", mpKfLevyList);
						
						log.info("환경개선부담금 전자납부처리 완료! [" + mf.getMap("ETAX_NO") + "]");
						
						MapForm mpSMS = new MapForm();	
						
						mpSMS.setMap("REG_NO"  , mpKfLevyList.getMap("REG_NO"));
						mpSMS.setMap("TAX_ITEM", mpKfLevyList.getMap("TAX_ITEM"));
						mpSMS.setMap("SUM_RCP" , mf.getMap("NAPBU_AMT"));
						
						try {
							sqlService_cyber.procedure("CODMBASE.SMSCALL", mpSMS);
						}catch (Exception e) {
							log.info("SMS 등록데이터 = " + mpSMS.getMaps());
							log.info("SMS 등록오류 발생");
						}
						
					}
				}
			}
			
			log.info(cUtil.msgPrint("3", "","KP202001 chk_kf_202001()", resCode));
			
        } catch (Exception e) {
			
        	resCode = "093";  //Error : 전자납부번호, 수용가번호 오류
        	
			e.printStackTrace();
			log.error("============================================");
			log.error("== chk_kf_201002 Exception(시스템) ");
			log.error("============================================");
		}
        
        /*여기서 공통헤드를 셋팅한다. 바뀌는 부분만 다시 셋팅해서 전문을 조립*/
        headMap.setMap("RS_FLAG"   , "G");         /*지로이용기관(G) */
        headMap.setMap("RESP_CODE" , resCode);     /*응답코드*/
        headMap.setMap("GCJ_NO"    , "0" + sg_code + "0" + CbUtil.lPadString(String.valueOf(SeqNumber()), 8, '0'));     /*이용기관 일련번호*/
        headMap.setMap("TX_GUBUN"  , "0210");
        
        return kfField.makeSendBuffer(headMap, mf);
	}

	/*----------------------------------------------*/
	/* 일련번호 가져오기 테스트*/
	/*----------------------------------------------*/
	private int SeqNumber(){
    	return (Integer)sqlService_cyber.queryForBean("CODMBASE.CODM_GETSEQNO", "00");
    }
}
