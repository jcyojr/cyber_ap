/**
 *  주시스템명 : 사이버지방세청 고도화
 *  업  무  명 : 환경개선부담금 납부내역 상세조회 전문
 *  기  능  명 : 결제원-사이버세청 
 *               업무처리
 *               결재원에서 수신된 전문에 대해서 응답전문데이트를 생성하고 
 *               조회결과를 송신한다.
 *  클래스  ID : Kf203002Service
 *  변경  이력 :
 * ------------------------------------------------------------------------
 *  작성자     소속  		 일자      Tag   내용
 * ------------------------------------------------------------------------   
 * 소도우   유채널(주)    2011.06.13   %01%  신규작성
 *  
 */
package com.uc.bs.cyber.service.kf203002;

import java.math.BigDecimal;
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
public class Kf203002Service {

	protected Log log = LogFactory.getLog(this.getClass());
	
	private ApplicationContext appContext = null;
	
	private IbatisBaseService sqlService_cyber = null;
		
	private Kf203002FieldList kfField = null;
	
	CbUtil cUtil = null;
	
	/**
	 * 생성자
	 */
	public Kf203002Service(ApplicationContext appContext) {
		// TODO Auto-generated constructor stub
		
		setAppContext(appContext);
		
		kfField = new Kf203002FieldList();
		
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
	 * 환경개선부담금 납부내역 상세조회 처리...
	 * */
	public byte[] chk_kf_203002(MapForm headMap, MapForm mf) throws Exception {
		
				
		MapForm sendMap = new MapForm();
		
		/*환경개선부담금 납부내역조회 시 사용 */
		String resCode = "000";       /*응답코드*/
		String giro_no = "1002641";   /*부산시지로코드*/
		String sg_code = "26";        /*부산시기관코드*/
		String napkiGubun = "";

		long NAP_BFAMT = 0;
		long NAP_AFAMT = 0;
		long BONSE = 0;
		long BONSE_ADD = 0;
		
		try{
			cUtil = CbUtil.getInstance();
			
			log.info(cUtil.msgPrint("5", "S","KP203002 chk_kf_203002()", resCode));
			
		
			/*전문체크*/
			
			/*기관*/
			if(!headMap.getMap("GPUB_CODE").equals(sg_code)){
				resCode = "339";
			}
			/*발행기관지로코드*/
			if(!headMap.getMap("GJIRO_NO").equals(giro_no)){
				resCode = "324";
			}
						
			/*
			 * 전문수신 중 DB에 맵핑되는 값
			 * ETAX_NO, NAPBU_JUMIN_NO
			 * */
			
			if(mf.getMap("ETAX_NO").toString().length() > 0 && resCode.equals("000")){
				
				ArrayList<MapForm>  kfCmd203002List  =  sqlService_cyber.queryForList("KF203002.SELECT_ENV_KFPAY_DETAIL_LIST", mf);
				
				int payCnt = kfCmd203002List.size();
				
				if ( payCnt <= 0) {
					
					resCode = "312";
				} else if( payCnt > 1) {
					
					resCode = "094";
				} else {
					
					MapForm mfCmd203002List  =  kfCmd203002List.get(0);
					
					MapForm mfSendData = new MapForm();
					
					napkiGubun = cUtil.getNapGubun((String)mfCmd203002List.getMap("DUE_DT"), (String)mfCmd203002List.getMap("DUE_F_DT"));
				    
				    if(napkiGubun.equals("B")){
				    	
				    	if(mfCmd203002List.getMap("DUE_DT").equals(mfCmd203002List.getMap("DUE_F_DT"))){
				    		
				    		NAP_BFAMT = ((BigDecimal)mfCmd203002List.getMap("ENV_AMT")).longValue()+((BigDecimal)mfCmd203002List.getMap("ADD_AMT")).longValue();   // 납기내금액
		                    NAP_AFAMT = ((BigDecimal)mfCmd203002List.getMap("ENV_AMT")).longValue()+((BigDecimal)mfCmd203002List.getMap("ADD_AMT")).longValue();   // 납기후금액

		                    BONSE         = NAP_BFAMT;   // 납기내금액
		                    BONSE_ADD     = NAP_AFAMT;   // 납기후금액

				    	} else {
				    		
				    		NAP_BFAMT = ((BigDecimal)mfCmd203002List.getMap("ENV_AMT")).longValue();   // 납기내금액
		                    NAP_AFAMT = ((BigDecimal)mfCmd203002List.getMap("ENV_AMT")).longValue()+((BigDecimal)mfCmd203002List.getMap("ADD_AMT")).longValue();  // 납기후금액

		                    BONSE         = NAP_BFAMT;   
		                    BONSE_ADD     = NAP_AFAMT;   

				    	}
				    	
				    } else if(napkiGubun.equals("A")) {
				    	
				    	NAP_BFAMT = ((BigDecimal)mfCmd203002List.getMap("ENV_AMT")).longValue();   // 납기내금액
	                    NAP_AFAMT = ((BigDecimal)mfCmd203002List.getMap("ENV_AMT")).longValue()+((BigDecimal)mfCmd203002List.getMap("ADD_AMT")).longValue();  // 납기후금액

	                    BONSE         = NAP_BFAMT;   
	                    BONSE_ADD     = NAP_AFAMT;   
				    	
				    }
					
					/*조회 전문 세팅*/
					
					mfSendData.setMap("ETAXNO"           ,mf.getMap("ETAXNO"));                     /*납부번호*/
					mfSendData.setMap("NAPBU_JUMIN_NO"   ,mfCmd203002List.getMap("REG_NO"));        /*조회주민번호*/
					mfSendData.setMap("JUMINNO"          ,mfCmd203002List.getMap("REG_NM"));        /*주민번호 (DB)*/
					mfSendData.setMap("SIDO"             ,"26");                                    /*시도구분*/
					mfSendData.setMap("GU_CODE"          ,mfCmd203002List.getMap("SGG_COD"));       /*구청코드*/
					mfSendData.setMap("CONFIRM_NO1"      ,"1");
					mfSendData.setMap("HCALVAL"          ,mfCmd203002List.getMap("ACCT_COD"));      /*회계*/
					mfSendData.setMap("GWAMOK"           ,mfCmd203002List.getMap("TAX_ITEM"));      /*과세목*/
					mfSendData.setMap("TAX_YYMM"         ,(String)mfCmd203002List.getMap("TAX_YY")  +  (String)mfCmd203002List.getMap("TAX_MM"));  /*부과연월*/
					mfSendData.setMap("KIBUN"            ,mfCmd203002List.getMap("TAX_DIV"));       /*기분*/
					mfSendData.setMap("DONG_CODE"        ,mfCmd203002List.getMap("HACD"));          /*행정동*/
					mfSendData.setMap("GWASE_NO"         ,mfCmd203002List.getMap("TAX_SNO"));       /*과세번호*/
					mfSendData.setMap("CONFIRM_NO2"      ,"2");
					mfSendData.setMap("NAPBU_NAME"       ,mfCmd203002List.getMap("REG_NM"));        /*납부자명*/
					mfSendData.setMap("NAP_BFAMT"        ,NAP_BFAMT);
					mfSendData.setMap("NAP_AFAMT"        ,NAP_AFAMT);
					mfSendData.setMap("CONFIRM_NO3"      ,"3");
					mfSendData.setMap("GWASE_POINT"      ,"0");
					mfSendData.setMap("BONSE"            ,BONSE);
					mfSendData.setMap("BONSE_ADD"        ,BONSE_ADD);
					mfSendData.setMap("DOSISE"           ,mfCmd203002List.getMap("MI_AMT"));
					mfSendData.setMap("DOSISE_ADD"       ,mfCmd203002List.getMap("ENV_MIADD_AMT"));
					mfSendData.setMap("GONGDONGSE"       ,"0");
					mfSendData.setMap("GONGDONGSE_ADD"   ,"0");
					mfSendData.setMap("EDUSE"            ,"0");
					mfSendData.setMap("EDUSE_ADD"        ,"0");
					mfSendData.setMap("NAP_BFDATE"       ,mfCmd203002List.getMap("DUE_DT"));
					mfSendData.setMap("NAP_AFDATE"       ,mfCmd203002List.getMap("DUE_F_DT"));
					mfSendData.setMap("CONFIRM_NO4"      ,"4");
					mfSendData.setMap("FILLER1"          ,"0");
					mfSendData.setMap("CONFIRM_NO5"      ,"5");
					mfSendData.setMap("GWASE_DESC"       ,mfCmd203002List.getMap("MLGN_IF1"));
					mfSendData.setMap("GOJI_DATE"        ,mfCmd203002List.getMap("TAX_DT"));
					mfSendData.setMap("NAPBU_SYS"        ,mfCmd203002List.getMap("SNSU"));
					mfSendData.setMap("JIJUM_CODE"       ,mfCmd203002List.getMap("BRC_NO"));
					mfSendData.setMap("NAPBU_DATE"       ,mfCmd203002List.getMap("PAY_DT") + "000000");
					mfSendData.setMap("NAPBU_AMT"        ,mfCmd203002List.getMap("SUM_RCP"));
					mfSendData.setMap("OUTACCT_NO"       ,"");
					mfSendData.setMap("RESERVED2"        ,"");
					
				    sendMap = mfSendData;
				}
				
			} else {
				resCode = "341";  /*전자납부번호, 수용가번호 오류*/
			}
			
			log.info(cUtil.msgPrint("5", "","KP203002 chk_kf_203002()", resCode));
			
        } catch (Exception e) {
			
        	resCode = "093";  //Error : 전자납부번호, 수용가번호 오류
        	
			e.printStackTrace();
			log.error("============================================");
			log.error("== chk_kf_203002 Exception(시스템) ");
			log.error("============================================");
		}
        
        /*여기서 공통헤드를 셋팅한다. 바뀌는 부분만 다시 셋팅해서 전문을 조립*/
        headMap.setMap("RS_FLAG"   , "G");         /*지로이용기관(G) */
        headMap.setMap("RESP_CODE" , resCode);     /*응답코드*/
        headMap.setMap("GCJ_NO"    , "0" + sg_code + "0" + CbUtil.lPadString(String.valueOf(SeqNumber()), 8, '0'));     /*이용기관 일련번호*/
        headMap.setMap("TX_GUBUN"  , "0210");
        
        return kfField.makeSendBuffer(headMap, sendMap);
	}

	/*----------------------------------------------*/
	/* 일련번호 가져오기 테스트*/
	/*----------------------------------------------*/
	private int SeqNumber(){
    	return (Integer)sqlService_cyber.queryForBean("CODMBASE.CODM_GETSEQNO", "00");
    }
}
