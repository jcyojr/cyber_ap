/**
 *  주시스템명 : 사이버지방세청 고도화
 *  업  무  명 : 세외수입 고지내역 상세조회 전문
 *  기  능  명 : 결제원-사이버세청 
 *               업무처리
 *               결재원에서 수신된 전문에 대해서 응답전문데이트를 생성하고 
 *               조회결과를 저장한다.
 *  클래스  ID : Kf271002Service
 *  변경  이력 :
 * ------------------------------------------------------------------------
 *  작성자     소속  		 일자      Tag   내용
 * ------------------------------------------------------------------------   
 * 소도우   유채널(주)    2011.06.13   %01%  신규작성
 *  
 */
package com.uc.bs.cyber.service.kf271002;

import java.math.BigDecimal;
import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.dao.DuplicateKeyException;

import com.uc.core.MapForm;
import com.uc.core.spring.service.IbatisBaseService;
import com.uc.bs.cyber.CbUtil;

/**
 * @author Administrator
 *
 */
public class Kf271002Service {

	protected Log log = LogFactory.getLog(this.getClass());
	
	private ApplicationContext appContext = null;
	
	private IbatisBaseService sqlService_cyber = null;
		
	private Kf271002FieldList kfField = null;
	
	CbUtil cUtil = null;
	
	/**
	 * 생성자
	 */
	public Kf271002Service(ApplicationContext appContext) {
		// TODO Auto-generated constructor stub
		
		setAppContext(appContext);
		
		kfField = new Kf271002FieldList();
		
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
	 * 세외수입 고지내역 상세조회 처리...
	 * 상세조회 화면 즉 결제원화면에서 331이라는 기납부 라고 에러가 
	 * 발생하면 결제원에 문의하여 자료를 삭제하도록 한다.
	 * */
	public byte[] chk_kf_271002(MapForm headMap, MapForm mf) throws Exception {
		
		MapForm sendMap = new MapForm();
		
		/*세외수입 고지내역조회 시 사용 */
		String resCode = "000";       /*응답코드*/
		String giro_no = "1500172";   /*부산시지로코드*/
		String sg_code = "26";        /*부산시기관코드*/
		
		try{

			cUtil = CbUtil.getInstance();
			
			log.info(cUtil.msgPrint("2", "S","KP271002 chk_kf_271002()", resCode));
						
			/*전문체크*/
			
			/*기관*/
			if(!headMap.getMap("GPUB_CODE").equals(sg_code)){
				resCode = "339";
			}
			/*발행기관지로코드*/
			if(!headMap.getMap("GJIRO_NO").equals(giro_no)){
				resCode = "324";
			}
			
			log.debug("S_GBN[조회구분] = [" + mf.getMap("S_GBN") + "]");
			/* G : 과세번호 E:전자납부번호
			 * 현재서울시는 G, 기타지역은 E만 처리함...
			 * 따라서 G 전문이 수신되면 처리를 해야할까 말까...고민됨...
			 * 지금은 처리안함...
			 * */
			
			/**
			 * 국민은행 같은경우 세외수입의 경우 전자납부번호가 17 임...
			 * 그외는 17 + "00" 붙어서 전문이 수신됨...
			 * 따라서 이런 오류를 방지하기 위함
			 */
			if(mf.getMap("GRNO").toString().length() == 17) {
				mf.setMap("GRNO", mf.getMap("GRNO").toString() + "00");
			}
			log.debug("로그확인 1111");
			if(mf.getMap("GRNO").toString().length() == 19 && resCode.equals("000")){
				log.debug("로그확인 2222");
				
				//로깅작업용 : 조회
				mf.setMap("TX_GB"  , "1");  
				mf.setMap("PAY_DT" , CbUtil.getCurrentDate());
				
				log.debug("로그확인 3333");
				ArrayList<MapForm>  kfCmd271002List  =  sqlService_cyber.queryForList("KF271002.SELECT_BNON_SEARCH_LIST", mf);
				log.debug("로그확인 4444");
				
				log.debug("GRNO = [" + mf.getMap("GRNO") + "]");
				log.debug("JUMIN_NO = [" + mf.getMap("JUMIN_NO") + "]");
				log.debug("TAX_NO = [" + mf.getMap("TAX_NO") + "]");
				log.debug("kfCmd271002List.size() = [" + kfCmd271002List.size() + "]");
				log.debug("resCode = [" + resCode + "]");
				
				
				
				int levy_cnt = kfCmd271002List.size();
				
				if (levy_cnt <= 0) {
					resCode = "311"; /*조회내역없음*/
				} else if (levy_cnt > 1) {
					resCode = "094"; /*고지서 2건 이상*/
				} else {
					
					for ( int al_cnt = 0;  al_cnt < levy_cnt;  al_cnt++)   {
						
						MapForm mfCmd271002List  =  kfCmd271002List.get(al_cnt);
												
						long gukamt = 0, gukamt_add = 0, sido_amt = 0, sido_amt_add = 0, sigungu_amt = 0, sigungu_amt_add = 0;
						
						/* 국세 시세 시군구세 구분에 따라 금액적용 */
						if(mfCmd271002List.getMap("GBN").equals("1")){
							gukamt = ((BigDecimal)mfCmd271002List.getMap("AMT")).longValue();
							gukamt_add = ((BigDecimal)mfCmd271002List.getMap("AFT_AMT")).longValue();
						} else if (mfCmd271002List.getMap("GBN").equals("2")){
							sido_amt = ((BigDecimal)mfCmd271002List.getMap("AMT")).longValue();
							sido_amt_add = ((BigDecimal)mfCmd271002List.getMap("AFT_AMT")).longValue();					
						} else if (mfCmd271002List.getMap("GBN").equals("3")){
							sigungu_amt = ((BigDecimal)mfCmd271002List.getMap("AMT")).longValue();
							sigungu_amt_add = ((BigDecimal)mfCmd271002List.getMap("AFT_AMT")).longValue();			
						} else if (mfCmd271002List.getMap("GBN").equals("4")){
							/*구세외수입 추가*/
							/*20110714*/
                            /*즉 특별회계는 시도국세 구분이 없음...*/
						}
					
						/* 납세자명이 40바이트기 때문에 모든 글자가 한글이 경우 20자 이하로 자른다 */
						if(mfCmd271002List.getMap("REG_NM").toString().length() > 0)
							mfCmd271002List.setMap("REG_NM", CbUtil.ksubstr(mfCmd271002List.getMap("REG_NM").toString(), 20));
						
						/*조회 전문 세팅*/
						sendMap.setMap("S_GBN"                  ,  mf.getMap("S_GBN"));
						sendMap.setMap("TAX_NO"                 ,  mfCmd271002List.getMap("TAX_NO"));
						sendMap.setMap("GRNO"                   ,  mf.getMap("GRNO"));
						sendMap.setMap("JUMIN_NO"               ,  mfCmd271002List.getMap("REG_NO"));
						sendMap.setMap("FIELD1"                 ,  "");
						sendMap.setMap("BUGWA_GB"               ,  mfCmd271002List.getMap("BUGWA_STAT"));
						sendMap.setMap("SEMOK_CD"               ,  mfCmd271002List.getMap("TAX_ITEM"));
						sendMap.setMap("SEMOK_NM"               ,  CbUtil.ksubstr(mfCmd271002List.getMap("TAX_NM").toString(), 50)); /*50자를 넘지 않게*/
						sendMap.setMap("GBN"                    ,  mfCmd271002List.getMap("GBN"));
						sendMap.setMap("OCR_BD"                 ,  mfCmd271002List.getMap("OCR_BD"));
						sendMap.setMap("NAP_NAME"               ,  mfCmd271002List.getMap("REG_NM"));
						sendMap.setMap("NAP_BFAMT"              ,  mfCmd271002List.getMap("AMT"));
						sendMap.setMap("NAP_AFAMT"              ,  mfCmd271002List.getMap("AFT_AMT"));
						sendMap.setMap("GUKAMT"                 ,  gukamt);
						sendMap.setMap("GUKAMT_ADD"             ,  gukamt_add);
						sendMap.setMap("SIDO_AMT"               ,  sido_amt);
						sendMap.setMap("SIDO_AMT_ADD"           ,  sido_amt_add);
						sendMap.setMap("SIGUNGU_AMT"            ,  sigungu_amt);
						sendMap.setMap("SIGUNGU_AMT_ADD"        ,  sigungu_amt_add);
						sendMap.setMap("BUNAP_AMT"              ,  "");
						sendMap.setMap("BUNAP_AMT_ADD"          ,  "");
						sendMap.setMap("FIELD2"                 ,  "");
						sendMap.setMap("NAP_BFDATE"             ,  mfCmd271002List.getMap("DUE_DT"));
						sendMap.setMap("NAP_AFDATE"             ,  mfCmd271002List.getMap("DUE_F_DT"));
						sendMap.setMap("GWASE_ITEM"             ,  mfCmd271002List.getMap("TAX_GDS"));
						sendMap.setMap("BUGWA_TAB"              ,  " ");
						sendMap.setMap("GOJI_DATE"              ,  mfCmd271002List.getMap("GOJI_DATE"));
						sendMap.setMap("OUTO_ICHE_GB"           ,  "N");
						sendMap.setMap("SUNAB_BANK_CD"          ,  "");
						sendMap.setMap("RECIP_DATE"             ,  "0");
						sendMap.setMap("NAPGI_BA_GB"            ,  cUtil.getNapGubun((String)mfCmd271002List.getMap("DUE_DT"), (String)mfCmd271002List.getMap("DUE_F_DT")));
						sendMap.setMap("FILED3"                 ,  "");

						
						//logging...
						try{
							//로깅작업용 : 수납
							mf.setMap("TAX_NO"      ,  mfCmd271002List.getMap("TAX_NO"));
							mf.setMap("JUMIN_NO"    ,  mfCmd271002List.getMap("REG_NO"));
							mf.setMap("PAY_DT"      ,  CbUtil.getCurrentDate());
							mf.setMap("NAPBU_AMT"   ,  mfCmd271002List.getMap("AMT"));
							mf.setMap("TX_GB"       ,  mfCmd271002List.getMap("TAX_GB"));
							
							sqlService_cyber.queryForUpdate("KF271002.INSERT_TX2421_TB_LOG", mf);
							
						}catch (Exception e) {
							
							if (e instanceof DuplicateKeyException){
								log.info("조회 TX2421_TB 테이블에 이미 기록된 데이터");
							} else {
								log.error("오류 데이터 = " + mf.getMaps());
							    log.error("Logging  failed!!!");	
							}
						}
					} 
				}
			
				
			
			} else {
				resCode = "341";  /*전자납부번호, 수용가번호 오류*/
			}
			
			log.info(cUtil.msgPrint("2", "","KP271002 chk_kf_271002()", resCode));
			
        } catch (Exception e) {
			
        	resCode = "093";  //Error : 전자납부번호, 수용가번호 오류
        	
			e.printStackTrace();
			log.error("============================================");
			log.error("== chk_kf_271002 Exception(시스템) ");
			log.error("============================================");
		}
        
        /*정상적인 처리가 안되었다면 온 전문 그대로 돌려준다.*/
        if (!resCode.equals("000")) {
        	sendMap = mf;
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
