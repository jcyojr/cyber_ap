/**
 *  주시스템명 : 사이버지방세청 고도화
 *  업  무  명 : 지방세 납부통지전문(서구청 실시간 입금통보) - 부비카 (UCT) 하여튼 알수 없는 희한한 전문임(뭐시여?)...
 *  기  능  명 : 결재원-사이버세청 
 *               업무처리
 *               결재원에서 수신된 전문에 대해서 응답전문데이트를 생성하고 
 *               조회결과를 저장한다.
 *  클래스  ID : Bs502002Service
 *  변경  이력 :
 * ------------------------------------------------------------------------
 *  작성자     소속  		 일자      Tag   내용
 * ------------------------------------------------------------------------   
 * 황종훈   유채널(주)    2011.06.13   %01%  신규작성 
 *                        2011.07.05         추가     :황 제대로 좀 알고 짜라.
 */
package com.uc.bs.cyber.service.bs502002;

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
public class Bs502002Service {

	protected Log log = LogFactory.getLog(this.getClass());
	
	private ApplicationContext appContext = null;
	
	private IbatisBaseService sqlService_cyber = null;
		
	private Bs502002FieldList bsField = null;
	
	CbUtil cUtil = null;
	
	/**
	 * 생성자
	 */
	public Bs502002Service(ApplicationContext appContext) {
		// TODO Auto-generated constructor stub
		
		setAppContext(appContext);
		
		bsField = new Bs502002FieldList();
		
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
	 * 지방세 납부통보 처리...
	 * */
	public byte[] chk_bs_502002(MapForm headMap, MapForm mf) throws Exception {
		
				
		MapForm sendMap = new MapForm();
		
		/*지방세 고지내역조회 시 사용 */
		String resCode = "000";       /*응답코드*/
//		String giro_no = "1000685";   /*부산시지로코드*/
		String strTaxGubun = "";
		String sg_code = "26";        /*부산시기관코드*/
		
		try{
			cUtil = CbUtil.getInstance();
			
			log.info(cUtil.msgPrint("", "S", "BP502002 chk_bs_502002()", resCode));
			
			/*전문체크*/
			/*기관*/
			if(!headMap.getMap("GPUB_CODE").equals(sg_code)){
				resCode = "122";
			}
			/*발행기관지로코드*/
	        strTaxGubun = (String) sqlService_cyber.queryForBean("CODMBASE.GIRO_FOR_TAX", headMap);
//	          if(!headMap.getMap("GJIRO_NO").equals(giro_no)){
	        if(!("1".equals(strTaxGubun))){
				resCode = "123";
			}

	 		MapForm taxform = new MapForm();
	 		
			if (resCode.equals("000")) {
				
				taxform.setMap("TRDATE"    , headMap.getMap("TX_DATE"));                                          /*거래일시    */
				taxform.setMap("BGRNO"     , headMap.getMap("BCJ_NO"));                                           /*전문일련번호*/
				taxform.setMap("TRSPCO"    , headMap.getMap("PROGRAM_ID"));                                       /*전문코드    */
				taxform.setMap("SNSU"      , headMap.getMap("RS_FLAG"));                                          /*수납매체    */
				taxform.setMap("RESPCO"    ,"000" );       					                                      /*응답코드    */
				taxform.setMap("SIDO_COD"  , "26");     					                                      /*시도코드    */
				taxform.setMap("GIRO_NO"   , headMap.getMap("GJIRO_NO"));                                         /*지로코드    */
				taxform.setMap("SUM_RCP"   , mf.getMap("NAPBU_AMT"));                                             /*수납금액    */
				taxform.setMap("PAY_DT"    , mf.getMap("NAPBU_DATE"));                                            /*납부일자    */
				taxform.setMap("DRBKCO"    , CbUtil.lPadString(mf.getMap("OUTBANK_CODE").toString(), 7, '0'));    /*수납기관코드*/
				taxform.setMap("REG_NM"    , mf.getMap("NAPBU_NAME"));                                            /*납세자명    */
				taxform.setMap("OUT_DRACCN", mf.getMap("OUTACCT_NO"));                                            /*출금계좌    */
				taxform.setMap("GUBUN"     , CbUtil.lPadString(mf.getMap("GUBUN").toString(), 2, '0'));           /*구분        */  
				taxform.setMap("IN_DRACCN" , mf.getMap("INACCT_NO"));                                             /*입금계좌    */
				taxform.setMap("ETC"       , " ");        					                                      /*기타사항    */
				taxform.setMap("TRTG"      , "0");         					                                      /*전송여부    */
				taxform.setMap("TR_TG"     , "0");        					                                      /*전송구분    */
				
				try {
					
					if(sqlService_cyber.queryForUpdate("BS502002.INSERT_ET2101_TB_EPAY", taxform) <= 0){
						resCode = "093";
					}
					
				}catch (Exception e){
					if (e instanceof DuplicateKeyException){
						resCode = "094";
					} else {
						resCode = "093";
					}
				}
				     
			}

			log.info(cUtil.msgPrint("", "", "BP502002 chk_bs_502002()", resCode));

        } catch (Exception e) {
			
        	resCode = "093";
			e.printStackTrace();
			log.error("============================================");
			log.error("== chk_bs_502002 Exception(시스템) ");
			log.error("============================================");
		}
        
        /*정상이 아니면 온 전문을 그대로 돌려주고 에러코드를 셋팅해서 준다.*/
        if(!resCode.equals("000")) {
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

