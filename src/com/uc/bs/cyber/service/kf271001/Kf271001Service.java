/**
 *  주시스템명 : 사이버지방세청 고도화
 *  업  무  명 : 세외수입 고지내역 조회 전문
 *  기  능  명 : 결재원-사이버세청 
 *               업무처리
 *               결재원에서 수신된 전문에 대해서 응답전문데이트를 생성하고 
 *               조회결과를 저장한다.
 *  클래스  ID : Kf271001Service
 *  변경  이력 :
 * ------------------------------------------------------------------------
 *  작성자     소속  		 일자      Tag   내용
 * ------------------------------------------------------------------------   
 * 소도우   유채널(주)    2011.06.13   %01%  신규작성
 * 임창섭   유채널(주)  2013.11.04     %02%  간단e납부관련(세외수입, 환경개선부담금) 추가
 */
package com.uc.bs.cyber.service.kf271001;

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
public class Kf271001Service {

	protected Log log = LogFactory.getLog(this.getClass());
	
	private ApplicationContext appContext = null;
	
	private IbatisBaseService sqlService_cyber = null;
		
	private Kf271001FieldList kfField = null;
	
	CbUtil cUtil = null;
	
	/**
	 * 생성자
	 */
	public Kf271001Service(ApplicationContext appContext) {
		// TODO Auto-generated constructor stub
		
		setAppContext(appContext);
		
		kfField = new Kf271001FieldList();
		
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
	 * 세외수입 고지내역 간략조회 처리...
	 * */
	public byte[] chk_kf_271001(MapForm headMap, MapForm mf) throws Exception {
		
				
		MapForm sendMap = new MapForm();
		
		/*세외수입 고지내역조회 시 사용 */
		String resCode = "000";       /*응답코드*/
		String giro_no = "1500172";   /*부산시지로코드*/
		String sg_code = "26";        /*부산시기관코드*/
		
		String POINTNO = "";          /*지정번호*/
		String DATANUM = "";          /*데이터갯수*/
		int START_IDX = 0;
		
		/*반복부*/
		ArrayList<MapForm> alRepeatData = new ArrayList<MapForm>();
		
		try{
			cUtil = CbUtil.getInstance();
			
			log.info(cUtil.msgPrint("1","S","KP271001 chk_kf_271001()", resCode));
			
			/*기관*/
			if(!headMap.getMap("GPUB_CODE").equals(sg_code)){
				resCode = "339";
			}
			/*발행기관지로코드*/
			if(!headMap.getMap("GJIRO_NO").equals(giro_no)){
				resCode = "324";
			}
			log.info("mf.getMap(JUMIN_NO) = " + mf.getMap("JUMIN_NO"));
			/*실명번호 존재여부체크*/
            //if("".equals(mf.getMap("JUMIN_NO").toString().trim()) || mf.getMap("JUMIN_NO").toString().trim()==null){
            if(mf.getMap("JUMIN_NO").toString().trim().length() < 10 || mf.getMap("JUMIN_NO").toString().trim()==null){
                resCode = "340";
            }
            
            
		
			if(resCode.equals("000")) {
				
				ArrayList<MapForm>  kfCmd271001List  =  sqlService_cyber.queryForList("KF271001.SELECT_BNON_SEARCH_LIST", mf);
				
				POINTNO = (String)mf.getMap("POINT_NO"); // 지정번호
				DATANUM = (String)mf.getMap("DATA_NUM"); // 데이터건수
				log.info("응답코드 000(정상)");
				if ( kfCmd271001List.size() > 0) {
					
				  //간단e납부 테스트시 pointno가 '0'으로 올 경우 에러 확인 수정
                    if (Integer.parseInt(POINTNO)<=0) POINTNO = "001";
					
					START_IDX = Integer.parseInt(POINTNO) -1;
					
					if (START_IDX >= kfCmd271001List.size()){
						resCode = "311";
					} else {
						resCode = "000";
						
						/*지정번호 처리로직*/
						if (Integer.parseInt(DATANUM) > 9 && Integer.parseInt(DATANUM) < 99 )
			                DATANUM = "9";

			            if ( kfCmd271001List.size() < (START_IDX + Integer.parseInt(DATANUM)) )
			                DATANUM = String.valueOf( kfCmd271001List.size() - START_IDX );

			            if (Integer.parseInt(DATANUM) > 9)
			                DATANUM = "9";
			              
			            /*0210 응답전문 생성 */
						sendMap.setMap("JUMIN_NO", mf.getMap("JUMIN_NO"));    /*주민번호   */
			            sendMap.setMap("DATA_TOT", kfCmd271001List.size());   /*고지총건수 */
			  			sendMap.setMap("POINT_NO", POINTNO);                  /*지정번호   */
			  			
			  			/*1건이상 조회가 된 경우 전문생성*/
			  			sendMap.setMap("DATA_NUM", kfCmd271001List.size());   /*데이터건수 */

						//for ( int al_cnt = 0;  al_cnt < kfCmd271001List.size();  al_cnt++)   {//2013-10-29 금결원 이보람 과장과 통화(전체전문길이 1Kbyte제한)후 아래로 수정 by 임창섭
		                for ( int al_cnt = START_IDX;  al_cnt < (START_IDX + Integer.parseInt(DATANUM));  al_cnt++)  {
							
							MapForm mfCmd271001List  =  kfCmd271001List.get(al_cnt);
							
							MapForm mfRepeatData = new MapForm();
							
							mfRepeatData.setMap("PUBGCODE"   , headMap.getMap("GPUB_CODE"));
							mfRepeatData.setMap("JIRONO"     , headMap.getMap("GJIRO_NO"));
							mfRepeatData.setMap("TAX_NO"     , mfCmd271001List.getMap("TAX_NO"));
							mfRepeatData.setMap("GRNO"    	 , (mfCmd271001List.getMap("EPAY_NO").toString().length() == 17) ? mfCmd271001List.getMap("EPAY_NO") + "00" : mfCmd271001List.getMap("EPAY_NO"));
							mfRepeatData.setMap("GWA_MOK"    , mfCmd271001List.getMap("TAX_ITEM"));
							mfRepeatData.setMap("GWA_DATE"   , mfCmd271001List.getMap("TAX_YM"));
							mfRepeatData.setMap("KIBUN"      , mfCmd271001List.getMap("BUGWA_STAT"));
							mfRepeatData.setMap("NAP_AMT"    , mfCmd271001List.getMap("BNON_AMT"));
							mfRepeatData.setMap("NAP_GUBUN"  , cUtil.getNapGubun((String)mfCmd271001List.getMap("DUE_DT"), (String)mfCmd271001List.getMap("DUE_F_DT")));
							mfRepeatData.setMap("NAP_DATE"   , mfCmd271001List.getMap("GYMD"));
							mfRepeatData.setMap("AUTO_REG"   , "N");
							
							alRepeatData.add(mfRepeatData);
							
							log.debug("mfCmd271001List.getMap(TAX_NO) = [" + mfCmd271001List.getMap("TAX_NO") + "]");
							
						} 
					}
					
				}else{ 
					/* 조회건수가 없는 경우 전문생성
					 * */
					
					resCode = "311";  /*조회내역없음*/

				} 
				
				
			}
			
			log.info(cUtil.msgPrint("1","","KP271001", resCode));
			
        } catch (Exception e) {
			
			e.printStackTrace();
			log.error(e.getMessage()+"Error");
			log.error("============================================");
			log.error("== chk_kf_271001 Exception(시스템) ");
			log.error("============================================");
		}
        
        if(!resCode.equals("000")) {
        	sendMap = mf;     /*비정상적인 경우 온 전문 그대로...*/
        	sendMap.setMap("DATA_TOT" , "000");  /*고지총건수 */
			sendMap.setMap("DATA_NUM" , "00");   /*데이터건수 */
        }
        
        /*여기서 공통헤드를 셋팅한다. 바뀌는 부분만 다시 셋팅해서 전문을 조립*/
        headMap.setMap("RS_FLAG"   , "G");         /*지로이용기관(G) */
        headMap.setMap("RESP_CODE" , resCode);     /*응답코드*/
        headMap.setMap("GCJ_NO"    , "0" + sg_code + "0" + CbUtil.lPadString(String.valueOf(SeqNumber()), 8, '0'));     /*이용기관 일련번호*/
        headMap.setMap("TX_GUBUN"  , "0210");
        
        return kfField.makeSendReptBuffer(headMap, sendMap, alRepeatData);
	}

	/*----------------------------------------------*/
	/* 일련번호 가져오기 테스트*/
	/*----------------------------------------------*/
	private int SeqNumber(){
    	return (Integer)sqlService_cyber.queryForBean("CODMBASE.CODM_GETSEQNO", "00");
    }
}

