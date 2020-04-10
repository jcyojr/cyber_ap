/**
 *  주시스템명 : 사이버지방세청 고도화
 *  업  무  명 : 실시간납부결과통지
 *  기  능  명 : 위택스-사이버세청 
 *               업무처리
 *  클래스  ID : We532001Service
 *  변경  이력 :
 * ------------------------------------------------------------------------
 *  작성자     소속  		 일자      Tag   내용
 * ------------------------------------------------------------------------   
 * 소도우   유채널(주)  2011.04.24     %01%  신규작성
 *  
 */
package com.uc.bs.cyber.service.we532001;

import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;

import com.uc.core.MapForm;
import com.uc.core.spring.service.IbatisBaseService;


/**
 * @author Administrator
 */
public class We532001Service {

	protected Log log = LogFactory.getLog(this.getClass());
	
	private ApplicationContext appContext = null;
	
	private IbatisBaseService sqlService_cyber = null;
		
	/**
	 * 생성자
	 */
	public We532001Service(ApplicationContext context) {
		// TODO Auto-generated constructor stub
		setAppContext(context);
		
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
	 * 부과자료를 조회하고 수납처리한다...
	 * */
	public String chkweTax(MapForm mf) throws Exception {
		
		String retStr = "";
		
		try{
			
			ArrayList<MapForm>  weCmdLevyList  =  sqlService_cyber.queryForList("WE532001.TX1102_TB_Only_Select", mf);
			
			MapForm mfLevyList = new MapForm();
						
			if (weCmdLevyList.size()  ==  1)   {
				
				if(log.isDebugEnabled()){
					log.info("============================================");
					log.info("== CYB532001 chkweTax 부과조회 정상!! ( 총 1건)   ");
					log.info("============================================");
				}
				
				/* 부가자료가 1건 존재하므로 정상
				 * 1. 전자수납테이블에 (위택스분)부가자료를 INSERT한다.
				 * 2. 기존 지방세 부가 상세 테이블의 수납Flag에 업데이트 한다.
				 * */
				
				/**
				 * (참고)
				 * 20110830 
				 * 비동기방식에 따른 이중납부처리를 위해서 위택스에서는 수납처리후(계정처리) 통보만 받기 때문에
				 * 비택스에 처리내역이 있다 할지라도 사이버 전자수납 테이블에 수납처리한다..
				 * 추 후에 취소처리 위함. 
				 */

				mfLevyList = weCmdLevyList.get(0);
				
				/*전자수납테이블에 수납순번을 구한다. 중복처리가 가능하므로...*/
				mf.setMap("PAY_CNT", sqlService_cyber.getOneFieldInteger("WE532001.TX1201_TB_MaxPayCnt", mfLevyList));
				
				/*연계전문변경으로 인해 카드사와 은행 모두 납부기관코드는 수납점 앞 3자리로 변경됨 : 20110711*/
				/*20110816 다시 번복됨 */
				//if(!((String) mf.getMap("CARD_REQ_NO")).trim().equals("")) { // 카드승인번호가 있으면 카드수납
		        //    mf.setMap("BNK_CD", ((String) mf.getMap("BANK_COD")).trim().substring(2, 5));
		        //} else { // 아니면 계좌이체 수납 
		        //    mf.setMap("BNK_CD", ((String) mf.getMap("BANK_COD")).trim().substring(0, 3));
		        //}
				/*20110830 적용*/
				mf.setMap("BNK_CD", ((String) mf.getMap("BANK_COD")).trim().substring(0, 3));
				
				/*예약납부처리여부*/
				mf.setMap("RSN_YN", (mf.getMap("PAY_FORM").equals("R")) ? "Y" : "N"); 
				
				/*전자수납테이블 INSERT : 이중수납을 고려하므로 수납처리여부를 확인하지 않고 INSERT */
				sqlService_cyber.queryForInsert("WE532001.TX1201_TB_Insert", mf);
				
				if(!(mfLevyList.getMap("SNTG").equals("1") || mfLevyList.getMap("SNTG").equals("2"))) {  /*이미수납이 되어 있는 경우임...(아마 부산은행 인터넷뱅킹에서) 제외*/
					
					/*지방세 부과상세정보 업데이트*/
					sqlService_cyber.queryForUpdate("WE532001.TX1102_TB_Update", mf);
					
					/*세목별 전자신고 테이블 수납정보 업데이트*/
					if(mfLevyList.getMap("TAX_ITEM").equals("140004")){ /*지방소득세 특별징수*/
						
						sqlService_cyber.queryForUpdate("WE532001.ES1111_TB_Update", mfLevyList);
						
					} else if(mfLevyList.getMap("TAX_ITEM").equals("140001")){ /*지방소득세 종합소득*/
						
						sqlService_cyber.queryForUpdate("WE532001.ES1121_TB_Update", mfLevyList);
						
					} else if(mfLevyList.getMap("TAX_ITEM").equals("140002")){ /*지방소득세 양도소득*/
						
						sqlService_cyber.queryForUpdate("WE532001.ES1131_TB_Update", mfLevyList);
						
					} else if(mfLevyList.getMap("TAX_ITEM").equals("140003")){ /*지방소득세 법인세분*/
						
					    //2015년 부터 기존신고화면으로 신고를 안한다면, 기존 테이블로 업데이트 할 필요는 없을 듯
                        sqlService_cyber.queryForUpdate("WE532001.ES1141_TB_Update", mfLevyList);
                        
                        //2015.03.24 신규테이블 변경 적용
                        sqlService_cyber.queryForUpdate("WE532001.ES5001_TB_Update", mfLevyList);
						
					} else if(mfLevyList.getMap("TAX_ITEM").equals("140011") || 
							  mfLevyList.getMap("TAX_ITEM").equals("104009")){ /*지방소득세 종업원분,주민세 재산분*/
						
						sqlService_cyber.queryForUpdate("WE532001.ES1151_TB_Update", mfLevyList);
						
					} else if(mfLevyList.getMap("TAX_ITEM").equals("114002")){ /*등록세면허세*/
						
						sqlService_cyber.queryForUpdate("WE532001.ES1161_TB_Update", mfLevyList);
						
					} else if(mfLevyList.getMap("TAX_ITEM").equals("109000")){ /*레저세*/
						
						sqlService_cyber.queryForUpdate("WE532001.ES1171_TB_Update", mfLevyList);
						
					} else if(mfLevyList.getMap("TAX_ITEM").equals("135001")){ /*지방소득세 지역자원개발세 전자신고분*/
						
						sqlService_cyber.queryForUpdate("WE532001.ES1181_TB_Update", mfLevyList);
						
					} 
					
				} else {
					log.info("기 수납처리된 건입니다. 전자수납테이블에 이중납부여를 확인하세요!");
					log.info("전자수납테이블에 수납처리되었습니다.");
				}
				
				
				/*통합납부이므로 전체로 보낸다...*/
//				MapForm mpSMS = new MapForm();	
//				
//				mpSMS.setMap("REG_NO"  , mf.getMap("PAY_REG_NO"));
//				mpSMS.setMap("TAX_ITEM", "888888"); /*일괄납부*/
//				mpSMS.setMap("SUM_RCP" , mf.getMap("SUM_RCP"));
//				
//				try {
//					sqlService_cyber.procedure("CODMBASE.SMSCALL", mpSMS);
//				}catch (Exception e) {
//					log.info("SMS 등록데이터 = " + mpSMS.getMaps());
//					log.info("SMS 등록오류 발생");
//				}
				
				retStr = "44100-000";
				
			} else if (weCmdLevyList.size()  ==  0)   {
				
				if(log.isDebugEnabled()){
					log.info("============================================");
					log.info("== CYB532001 chkweTax 부과조회 오류!! 전자납부번호(" + mf.getMap("EPAY_NO") + ")( 총 " + 0 + "건)   ");
					log.info("============================================");
				}
				
				retStr = "44100-101";
				
			} else {
				
				//if(log.isDebugEnabled()){
					log.info("============================================");
					log.info("== CYB532001 chkweTax 부과조회 오류!! 전자납부번호(" + mf.getMap("EPAY_NO") + ")( 총 " + weCmdLevyList.size() + "건)   ");
					log.info("============================================");
				//}
				
				retStr = "44100-201";
			}
			
			
		} catch (Exception e) {
			
			e.printStackTrace();
			
			log.info("============================================");
			log.info("== CYB532001 chkweTax Exception(시스템) ");
			log.info("============================================");

			retStr = "44100-201";
			
		}
		
        return retStr;
	}

	
}
