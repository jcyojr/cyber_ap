/**
 *  주시스템명 : 사이버지방세청 고도화
 *  업  무  명 : 입금계좌 사전 수취조회 전문
 *  기  능  명 : 부산은행-사이버세청 
 *              수신 전문을 분석하고 가상계좌의 입금 가능 여부를 확인 한다. 
 *  클래스  ID : Bs531002Service
 *  변경  이력 :
 * ------------------------------------------------------------------------
 *  작성자     소속  		 일자      Tag   내용
 * ------------------------------------------------------------------------   
 *  최유환   유채널      2015.01.19   %01%  신규작성
 *  
 */
package com.uc.bs.cyber.service.bs531002;

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
public class Bs531002Service {

	protected Log log = LogFactory.getLog(this.getClass());
	
	private ApplicationContext appContext = null;
	
	private IbatisBaseService sqlService_cyber = null;
		
	private Bs531002FieldList bsField = null;
	
	CbUtil cUtil = null;
	
	/**
	 * 생성자
	 */
	public Bs531002Service(ApplicationContext appContext) {
		// TODO Auto-generated constructor stub
		
		setAppContext(appContext);
		
		bsField = new Bs531002FieldList();
		
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
	 * 지방세 납부내역 상세조회 처리...
	 * */
	public byte[] chk_bs_531002(MapForm headMap, MapForm mf) throws Exception {
		
		
		/*****************************************
		 * 0000 정상
		 * 7011 해당계좌번호 없음
		 * 7012 입금금액 불일치
		 * 7013 해당계좌 입금불가
		 * 7014 해당기관 장애
		 * 7015 기타 오류
		 * */
		ArrayList<MapForm> list = new ArrayList<MapForm>();		
		MapForm sendMap = new MapForm();
		MapForm checkMap = null;
		boolean rsCheck = false;
		/*상수도 및 열요금 사전조회 */                                                                 
		try{
			
			/*전문체크*/
			String vir_acc_no = mf.getMap("NEW_VIR_ACC_NO").toString();
			log.debug("신 가상계좌번호 확인 : " + vir_acc_no);
			long trn_amt = Long.parseLong(mf.getMap("TRN_AMT").toString());
			log.debug("거래금액 확인 : " + trn_amt);
			
			if(trn_amt < 10){
				log.debug("금액이 10원 미만입니다.");
				headMap.setMap("RS_CODE" , "7012");            
		        return bsField.makeSendBuffer(headMap, mf);
			}
			
			// 상수도 및 특별회계 구분 - 가상계좌 영역대로 비교  상수도 : 5,  특별 : 4
			String vir_gbn = "";
			String vir_gbn2 = "";
						
			if(vir_acc_no.length() != 13){
				//해당 가상계좌번호 없음
				log.debug("가상계좌 길이 오류 13자리가 아닙니다. :: " + vir_acc_no.length());
				headMap.setMap("RS_CODE" , "7011");            
		        return bsField.makeSendBuffer(headMap, mf);
			}else{
				vir_gbn = vir_acc_no.substring(4, 6);
				vir_gbn2 = vir_acc_no.substring(4, 8);
				log.debug("가상계좌 구분  44 : 상수도 , 4500 : 열요금  :: " + vir_gbn + "  " + vir_gbn2);
			}
			
			//상수도, 열요금 
			
			// 가상계좌 번호로 미납 건 여부 및 건수 확인
			if(vir_gbn.equals("44")){
				// 상수도 ****-44-*******
				log.debug("상수도 부과자료 조회 합니다.");
				// 상수도 부과자료 조회
				list = sqlService_cyber.queryForList("BS531002.getSudoBugwaList", mf);
				
			}else if(vir_gbn2.equals("4500")){
				// 열요금 ****-4500-***** 1만개 부여 
				log.debug("열요금 부과자료를 조회합니다.");
				// 세외수입 부과자료 조회
				list = sqlService_cyber.queryForList("BS531002.getEtcBugwaList", mf);
				
			}else{
				log.debug("가상계좌 상수도 열요금 구분 오류 입니다. vir_gbn : " + vir_gbn + "  " + vir_gbn2);
				headMap.setMap("RS_CODE"   , "7011");     /*응답코드*/  
		        headMap.setMap("TX_GUBUN"  , "0210");
		        
		        return bsField.makeSendBuffer(headMap, mf);
			}
			
			//가상계좌 조회 
			if(list.size() < 1){
				log.debug("해당 가상계좌의 list.size() < 1 입니다. ");
				headMap.setMap("RS_CODE"   , "7011");     /*응답코드*/  
		        headMap.setMap("TX_GUBUN"  , "0210");
		        
		        return bsField.makeSendBuffer(headMap, mf);
			}
			
			for(int i = 0; i < list.size(); i++){
				checkMap = new MapForm();
				checkMap = list.get(i);
				
				log.debug("목록의 값을 확인 합니다. " + checkMap);
				long checkAmt = Long.parseLong(checkMap.getMap("SUM_AMT").toString());
				
				if(checkAmt == trn_amt){
					log.debug("금액이 일치합니다.");
					mf.setMap("REG_NM", checkMap.getMap("REG_NM"));
					rsCheck = true;
					break;
				}
				log.debug("금액이 일치하지 않습니다.");
				
			}
			
			
        } catch (Exception e) {
        			
			e.printStackTrace();
			log.error("============================================");
			log.error("== chk_bs_523002 Exception(시스템) ");
			log.error("============================================");
		}
		
        if(rsCheck){
        	log.debug("정상처리 rsCheck = " + rsCheck);
        	headMap.setMap("RS_CODE" , "0000"); 
        	
        }else{
        	//입금금액 불일치
        	log.debug("금액 불일치 rsCheck = " + rsCheck);
        	headMap.setMap("RS_CODE" , "7012");      
        }     
        headMap.setMap("TX_GUBUN"  , "0210");
        
        return bsField.makeSendBuffer(headMap, mf);
	}


}

