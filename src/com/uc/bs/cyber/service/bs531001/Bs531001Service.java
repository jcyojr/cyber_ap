/**
 *  주시스템명 : 사이버지방세청 고도화
 *  업  무  명 : BS 가상계좌 거래내역 전문
 *  기  능  명 : 부산은행-사이버세청 
 *               부산은행에서 수신된 전문에 대해서 상수도 및 기타세외수입 입금처리 
 *  클래스  ID : Bs531001Service
 *  변경  이력 :
 * ------------------------------------------------------------------------
 *  작성자     소속  		 일자      Tag   내용
 * ------------------------------------------------------------------------   
 *  최유환     유채널    2015.01.28   %01%  신규작성
 *  
 */
package com.uc.bs.cyber.service.bs531001;

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
public class Bs531001Service {

	protected Log log = LogFactory.getLog(this.getClass());
	
	private ApplicationContext appContext = null;
	
	private IbatisBaseService sqlService_cyber = null;
		
	private Bs531001FieldList bsField = null;
	
	CbUtil cUtil = null;
	
	/**
	 * 생성자
	 */
	public Bs531001Service(ApplicationContext appContext) {
		// TODO Auto-generated constructor stub
		
		setAppContext(appContext);
		
		bsField = new Bs531001FieldList();
		
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
	public byte[] chk_bs_531001(MapForm headMap, MapForm mf) throws Exception {
		
		/*****************************************
		 * 0000 정상
		 * 7011 해당계좌번호 없음
		 * 7012 입금금액 불일치
		 * 7013 해당계좌 입금불가
		 * 7014 해당기관 장애
		 * 7015 기타 오류
		 * */
		
		//정상처리여부		
		boolean proc = false;
		ArrayList<MapForm> list = new ArrayList<MapForm>();
		MapForm amtCheck = null;
		//MapForm sendMap = new MapForm();
		
		//입금거래 입금취소거래 구분
		String job_gbn = "";
		//String tax_gbn = "";
		
		try{
			cUtil = CbUtil.getInstance();
			
			//JOB_GBN  20 : 입금   51 : 입금취소
			job_gbn = mf.getMap("JOB_GBN").toString();
			
			long bAmt = Long.parseLong(mf.getMap("TRN_AMT").toString());
			
			String acc_no = mf.getMap("VIR_ACC_NO").toString();
			String vir_gbn = "";
			String vir_gbn2 = "";
			if(acc_no.length() != 13){
				//해당 가상계좌번호 없음
				log.debug("가상계좌 길이 오류 13자리가 아닙니다. :: " + acc_no.length());
				headMap.setMap("RS_CODE" , "7011");            
		        return bsField.makeSendBuffer(headMap, mf);
			}else{
				vir_gbn = acc_no.substring(4, 6);
				vir_gbn2 = acc_no.substring(4, 8);
				log.debug("가상계좌 구분  44 : 상수도 , 4500 : 열요금  :: " + vir_gbn + "  " + vir_gbn2);
			}
			
			//TAX_GBN   상수도, 열요금 구분   가상계좌 영역대로 분리
			//tax_gbn = mf.getMap("VIR_ACC_NO").toString().substring(0,1);
			
			//입금거래
			if(job_gbn.equals("20")){
						
				log.debug("입금거래  :: " + job_gbn);
				//입금처리 대상이 있는지 한번더 확인
				
				// 가상계좌 번호로 미납 건 여부 및 건수 확인
				if(vir_gbn.equals("44")){
					// 상수도 ****-44-*******
					log.debug("상수도 부과자료 조회 합니다.");
					// 상수도 부과자료 조회
					list = sqlService_cyber.queryForList("BS531001.getSudoSunapList", mf);
					
				}else if(vir_gbn2.equals("4500")){
					// 열요금 ****-4500-***** 1만개 부여 
					log.debug("열요금 부과자료를 조회합니다.");
					// 열요금 부과자료 조회
					list = sqlService_cyber.queryForList("BS531001.getEtcSunapList", mf);
					
				}else{
					log.debug("가상계좌 상수도 열요금 구분 오류 입니다. vir_gbn : " + vir_gbn + "  " + vir_gbn2);
					headMap.setMap("RS_CODE"   , "7011");     /*응답코드*/  
			        headMap.setMap("TX_GUBUN"  , "0210");
			        
			        return bsField.makeSendBuffer(headMap, mf);
				}
				
								
				if(list.size() < 1 ){
					//7011 : 해당 계좌번호 없음
					log.debug("7011 : 해당 계좌번호 없음  list.size() < 1 입니다." + list.size());
					headMap.setMap("RS_CODE"   ,  "7011");     
			        headMap.setMap("TX_GUBUN"  ,  "0210");			        
			        return bsField.makeSendBuffer(headMap, mf);
				}
				
					
				log.debug("가상계좌로 조회한 부과자료와 처리금액 비교하여 수납처리 합니다.");
				for(int i = 0; i < list.size(); i++){
					amtCheck = new MapForm();
					amtCheck = list.get(i);
					//가상계좌로 조회한 부과자료 와 처리금액 비교하여 수납처리
					long sumamt = Long.parseLong(amtCheck.getMap("SUM_AMT").toString());
					
					//입금처리 해야할 건이 있으면 수납처리 후 for 문 끝
					if(sumamt == bAmt){
					 	log.debug("입금금액 일치  :: " + sumamt);
						/*
						 * GUBUN   1 : 독촉분   2 : 정기분   3 : 합계     4 : 수시분     5 : 열요금 단건   6: 열요금 합계
						 * */  
						
					 	//가상계좌번호 전문일련번호 저장
						amtCheck.setMap("BS_TRANS_NO", headMap.getMap("TRAN_NO"));
						amtCheck.setMap("VIR_ACC_NO", mf.getMap("VIR_ACC_NO"));
						
						//상수도 인지 열요금 인지 확인
						//열요금 단건 처리
						if(amtCheck.getMap("GUBUN").equals("5")){
							//열요금 단건수납처리
							log.debug("열요금 단건수납처리  :: " + amtCheck.getMap("GUBUN"));
							sqlService_cyber.queryForInsert("BS531001.insertEtcSunap", amtCheck);
							sqlService_cyber.queryForUpdate("BS531001.updateEtcBugwaSunap", amtCheck);
																			
							proc = true;
							break;
						}
						//열요금 합계 처리
						else if(amtCheck.getMap("GUBUN").equals("6")){
							//열요금 합계수납처리
							log.debug("열요금 합계수납처리  :: " + amtCheck.getMap("GUBUN"));				
						
							if(sqlService_cyber.queryForUpdate("BS531001.insertEtcAllSunap", amtCheck) > 0){
								sqlService_cyber.queryForUpdate("BS531001.updateEtcAllBugwaSunap", amtCheck);
							}
		
							proc = true;
							break;
						}
						else if(amtCheck.getMap("GUBUN").equals("3")){
							//상수도 전체 수납
							log.debug("상수도 전체 수납  :: " + amtCheck.getMap("GUBUN"));
							//부과자료 수납처리
							sqlService_cyber.queryForInsert("BS531001.insertSudoAllSunapProc", amtCheck);
							sqlService_cyber.queryForUpdate("BS531001.updateSudoAllBugwaSunapProc", amtCheck);
							
							proc = true;
							break;
						}else if(amtCheck.getMap("GUBUN").equals("1") || amtCheck.getMap("GUBUN").equals("2") || amtCheck.getMap("GUBUN").equals("4")){
							//상수도 일부 수납
							log.debug("상수도 일부 수납  :: " + amtCheck.getMap("GUBUN"));
							//부과자료 수납처리
							sqlService_cyber.queryForInsert("BS531001.insertSudoSunapData", amtCheck);
							sqlService_cyber.queryForUpdate("BS531001.updateSudoBugwaSunapData", amtCheck);			
							proc = true;
							break;
						}else{
							//7012 : 입금금액 불일치 
							log.debug("GUBUN 오류  :: " + amtCheck.getMap("GUBUN"));
							headMap.setMap("RS_CODE"   ,  "7012");     
					        headMap.setMap("TX_GUBUN"  ,  "0210");			        
					        return bsField.makeSendBuffer(headMap, mf);
						}
						
					}
					
				}
				
			}
			//압금취소거래
			else if(job_gbn.equals("51")){
				
				log.debug("입금취소거래 51 ::  " + job_gbn);
				
				MapForm smf = new MapForm();
				//원거래 전문번호
				smf.setMap("BS_TRANS_NO", headMap.getMap("TRAN_B_NO"));
				smf.setMap("TRANS_NO", headMap.getMap("TRAN_NO"));
				smf.setMap("SUM_RCP", mf.getMap("TRN_AMT"));
				
				//입금취소 거래 여부 확인
				//list = sqlService_cyber.queryForList("BS531001.getSudoSunapCancelList", smf);
				
				// 가상계좌 번호로 미납 건 여부 및 건수 확인
				if(vir_gbn.equals("44")){
					// 상수도 ****-44-*******
					log.debug("상수도 부과자료 조회 합니다.");
					// 상수도 부과자료 조회
					list = sqlService_cyber.queryForList("BS531001.getSudoSunapCancelList", smf);
					
					//취소건이 없을경우 에러 
					if(list.size() < 1){
						//7012 : 입금 취소금액 불일치 
						log.debug("상수도 입금취소 list.size() < 1 입니다. ::  " + list.size());
						headMap.setMap("RS_CODE"   ,  "7012");     
				        headMap.setMap("TX_GUBUN"  ,  "0210");			        
				        return bsField.makeSendBuffer(headMap, mf);
					}
					
					//입금취소 처리
					log.debug("상수도 수납테이블의 삭제 목록으로 부과 및 수납 테이블 수납취소 처리");
					for(int i = 0; i < list.size(); i++){
						amtCheck = new MapForm();
						amtCheck = list.get(i);
						
						amtCheck.setMap("BS_TRANS_NO", headMap.getMap("TRAN_NO"));
						
						log.debug("상수도 수납취소 자료 ("+i+") 시작 :: " + amtCheck);
						//수납취소 후 완료 전문 전달
						if(sqlService_cyber.queryForUpdate("BS531001.updateSunapCancel", amtCheck) > 0){
							sqlService_cyber.queryForUpdate("BS531001.updateBugwaSunapCancel", amtCheck);
						}else{
							log.debug("상수도 수납취소 자료 ("+i+") :: 실패 " + amtCheck);
						}
						
						log.debug("상수도 수납취소 자료 ("+i+") :: 완료");
					}
					
					proc = true;
					
				}else if(vir_gbn2.equals("4500")){
					// 열요금 ****-4500-***** 1만개 부여 
					log.debug("열요금 부과자료를 조회합니다.");
					// 열요금 부과자료 조회
					list = sqlService_cyber.queryForList("BS531001.getEtcSunapCancelList", smf);
					
					//취소건이 없을경우 에러 
					if(list.size() < 1){
						//7012 : 입금 취소금액 불일치 
						log.debug("열요금 입금취소 list.size() < 1 입니다. ::  " + list.size());
						headMap.setMap("RS_CODE"   ,  "7012");     
				        headMap.setMap("TX_GUBUN"  ,  "0210");			        
				        return bsField.makeSendBuffer(headMap, mf);
					}
					
					//입금취소 처리
					log.debug("열요금 수납테이블의 삭제 목록으로 부과 및 수납 테이블 수납취소 처리");
					for(int i = 0; i < list.size(); i++){
						amtCheck = new MapForm();
						amtCheck = list.get(i);
						
						amtCheck.setMap("BS_TRANS_NO", headMap.getMap("TRAN_NO"));
						
						log.debug("열요금 수납취소 자료 ("+i+") 시작 :: " + amtCheck);
						//수납취소 후 완료 전문 전달
						if(sqlService_cyber.queryForUpdate("BS531001.updateEtcSunapCancel", amtCheck) > 0){
							sqlService_cyber.queryForUpdate("BS531001.updateEtcBugwaSunapCancel", amtCheck);
						}else{
							log.debug("열요금 수납취소 자료 ("+i+") :: 실패 " + amtCheck);
						}
						
						log.debug("열요금 수납취소 자료 ("+i+") :: 완료");
					}
					
					proc = true;
					
					
				}else{
					log.debug("가상계좌 상수도 열요금 구분 오류 입니다. vir_gbn : " + vir_gbn + "  " + vir_gbn2);
					headMap.setMap("RS_CODE"   , "7011");     /*응답코드*/  
			        headMap.setMap("TX_GUBUN"  , "0210");
			        
			        return bsField.makeSendBuffer(headMap, mf);
				}
				

			}else{
				//거래구분 오류
				log.debug("거래구분 오류 입니다. :: " + job_gbn);
				/*응답코드  7015 :: 기타 오류*/
			    headMap.setMap("RS_CODE"   ,  "7015");     
		        headMap.setMap("TX_GUBUN"  ,  "0210");		        
		        return bsField.makeSendBuffer(headMap, mf);
			}
			
			
        } catch (Exception e) {
			
			e.printStackTrace();
			log.debug("===== Exception 발생 =======");
			
			headMap.setMap("RS_CODE"   ,  "7015");     /*응답코드*/
		    headMap.setMap("TX_GUBUN"  ,  "0210");
		    return bsField.makeSendBuffer(headMap, mf);
		}
       
		if(proc){
			log.debug("proc = true  :: " + proc);
			headMap.setMap("RS_CODE" , "0000");
		}else{
			log.debug("proc = false  :: " + proc);
			headMap.setMap("RS_CODE" , "7012");
		}        
        headMap.setMap("TX_GUBUN"  , "0210");  
        log.debug("===== 처리완료 =======");
        log.debug("===== headMap :: " + headMap);
        return bsField.makeSendBuffer(headMap, mf);
	}

	

}

