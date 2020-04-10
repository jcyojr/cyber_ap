/**
 *  주시스템명 : 사이버지방세청 고도화
 *  업  무  명 : 공통 납부내역 정정
 *  기  능  명 : 부산은행-사이버세청 
 *               업무처리
 *               결재원에서 수신된 전문에 대해서 납부내역을 정정하고 응답전문 송신...
 *  클래스  ID : Bs502101Service
 *  변경  이력 :
 * ------------------------------------------------------------------------
 *  작성자     소속  		 일자      Tag   내용
 * ------------------------------------------------------------------------   
 * 송동욱   유채널(주)    2011.06.24   %01%  신규작성
 *  
 */
package com.uc.bs.cyber.service.bs502101;

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
public class Bs502101Service {

	protected Log log = LogFactory.getLog(this.getClass());
	
	private ApplicationContext appContext = null;
	
	private IbatisBaseService sqlService_cyber = null;
		
	private Bs502101FieldList bsField = null;
	
	CbUtil cUtil = null;
	
	/**
	 * 생성자
	 */
	public Bs502101Service(ApplicationContext appContext) {
		// TODO Auto-generated constructor stub
		
		setAppContext(appContext);
		
		bsField = new Bs502101FieldList();
		
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
	 * 납부내역정정 통보 처리...
	 * */
	public byte[] chk_bs_502101(MapForm headMap, MapForm mf) throws Exception {
		
				
		MapForm sendMap = new MapForm();
		
		/*세외수입 고지내역조회 시 사용 */
		String resCode = "000";       /*응답코드*/
//		String giro_no = "1000685";   /*부산시지로코드*/
		String strTaxGubun = "";
		String sg_code = "26";        /*부산시기관코드*/
		
		try{
			cUtil = CbUtil.getInstance();
			
			log.info(cUtil.msgPrint("", "S", "BP502101 chk_bs_502101()", resCode));
			
			/*전문체크*/
			/*기관*/
			if(!headMap.getMap("GPUB_CODE").equals(sg_code)){
				resCode = "122";
			}
			/*발행기관지로코드*/
			strTaxGubun = (String) sqlService_cyber.queryForBean("CODMBASE.GIRO_FOR_TAX", headMap);
//          if(!headMap.getMap("GJIRO_NO").equals(giro_no)){
            if(!("1".equals(strTaxGubun))){
              resCode = "123";
            }
			
			log.debug("JUMIN_NO = [" + mf.getMap("JUMIN_NO") + "]");
			log.debug("EPAY_NO  = [" + mf.getMap("EPAY_NO") + "]");
			
			/*현재까지 정상이면*/
			if(resCode.equals("000")) {
				
				/*ㅅ ㅏ이버세청에 납부내역이 있는지 검사한다.*/
				ArrayList<MapForm>  bsCmd502101List  =  sqlService_cyber.queryForList("BS502101.SELECT_RECIP_INFO", mf);
				
				log.info("bsCmd502101List() = [" + bsCmd502101List.size() +"]");
				
		        /* Error : 납부내역없음 */
				if(bsCmd502101List.size() <= 0){
					resCode = "112";  
				} 
				/* Error : 납부내역 2건 이상 */
				else if (bsCmd502101List.size() > 1) {
					resCode = "201";  //서비스불가
				} else {
					
					MapForm mfCmd502101List  =  bsCmd502101List.get(0);
					
					if(mfCmd502101List.getMap("TAX_GB").equals("J")) {
						
						mfCmd502101List.setMap("SGG_COD" , mfCmd502101List.getMap("ETAXNO").toString().substring(0, 3)); 	//구청코드
						mfCmd502101List.setMap("ACCT_COD", mfCmd502101List.getMap("ETAXNO").toString().substring(4, 6));    //회계코드
						mfCmd502101List.setMap("TAX_ITEM", mfCmd502101List.getMap("ETAXNO").toString().substring(6, 12));   //과목/세목코드
						mfCmd502101List.setMap("TAX_YY"  , mfCmd502101List.getMap("ETAXNO").toString().substring(12, 16));  //과세년도
						mfCmd502101List.setMap("TAX_MM"  , mfCmd502101List.getMap("ETAXNO").toString().substring(16, 18));	//과세월
						mfCmd502101List.setMap("TAX_DIV" , mfCmd502101List.getMap("ETAXNO").toString().substring(18, 19));	//기분코드
						mfCmd502101List.setMap("HACD"    , mfCmd502101List.getMap("ETAXNO").toString().substring(19, 22));	//행정동코드
						mfCmd502101List.setMap("TAX_SNO" , mfCmd502101List.getMap("ETAXNO").toString().substring(22, 28));	//과세번호
						mfCmd502101List.setMap("PAY_CNT" , 0);	
						
						if (sqlService_cyber.queryForUpdate("BS502101.UPDATE_TX1201_TB_EPAY", mfCmd502101List) == 0 ) {//지방세전자수납 update
							resCode = "093"; //오류
						}
						
					} else if(mfCmd502101List.getMap("TAX_GB").equals("C")) {
						
						if (sqlService_cyber.queryForUpdate("BS502101.UPDATE_TX2211_TB_EPAY", mfCmd502101List) == 0 ) {//표준세외수입 전자수납 update
							resCode = "093"; //오류
						} 

					}
				
				}
					
			}
			
			log.info(cUtil.msgPrint("", "", "BP502101 chk_bs_502101()", resCode));

        } catch (Exception e) {
			
			e.printStackTrace();
			log.error("============================================");
			log.error("== chk_bs_502101 Exception(시스템) ");
			log.error("============================================");
		}
        
        if (!resCode.equals("000")) {
        	sendMap = mf;
        }
        
        /*여기서 공통헤드를 셋팅한다. 바뀌는 부분만 다시 셋팅해서 전문을 조립*/
        headMap.setMap("RS_FLAG"   , "G");         /*지로이용기관(G) */
        headMap.setMap("RESP_CODE" , resCode);     /*응답코드*/
        headMap.setMap("GCJ_NO"    , "0" + sg_code + "0" + CbUtil.lPadString(String.valueOf(SeqNumber()), 8, '0'));     /*이용기관 일련번호*/
        headMap.setMap("TX_GUBUN"  , "0210");
        
        return bsField.makeSendBuffer(headMap, sendMap);
	}

	/*----------------------------------------------*/
	/* 일련번호 가져오기 테스트*/
	/*----------------------------------------------*/
	private int SeqNumber(){
    	return (Integer)sqlService_cyber.queryForBean("CODMBASE.CODM_GETSEQNO", "00");
    }


}

