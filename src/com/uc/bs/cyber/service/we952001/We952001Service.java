/**
 *  주시스템명 : 사이버지방세청 간단e납부 대응
 *  업  무  명 : 실시간납부취소통지
 *  기  능  명 : 위택스-사이버세청 
 *               업무처리
 *  클래스  ID : We952001Service
 *  변경  이력 :
 * ------------------------------------------------------------------------
 *  작성자     소속  		 일자      Tag   내용
 * ------------------------------------------------------------------------   
 * 임창섭   유채널(주)  2014.11.24     %01%  신규작성
 *  
 */
package com.uc.bs.cyber.service.we952001;
import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;

import com.uc.core.MapForm;
import com.uc.core.spring.service.IbatisBaseService;

import com.uc.bs.cyber.service.we532001.We532001FieldList;
import com.uc.bs.cyber.service.we532002.We532002FieldList;
/**
 * @author Administrator
 *
 */
public class We952001Service {

	protected Log log = LogFactory.getLog(this.getClass());
	
	private ApplicationContext appContext = null;
	
	private IbatisBaseService sqlService_cyber = null;
	
	private We532001FieldList FL_532001 = new We532001FieldList();
	private We532002FieldList FL_532002 = new We532002FieldList();
	
	/**
	 * 생성자
	 */
	public We952001Service(ApplicationContext context) {
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
	 * 취소를 위한 부과자료를 조회하고 취소처리한다.
	 * */
	public String chkweTax(MapForm mf) throws Exception {
		
        String retStr = "";
		
		try{
			/*취소전문을 받아 위택스수신전문로그를 조회한다.*/
			ArrayList<MapForm>  weCmdLevyList  =  sqlService_cyber.queryForList("WE952001.CO4102_TB_Select", mf);
			
			if (weCmdLevyList.size()  >  0)   {
				
				for ( int rec_cnt = 0; rec_cnt < weCmdLevyList.size();  rec_cnt++ )   {
					
					MapForm mfTotalLevyList =  weCmdLevyList.get(rec_cnt);
					
					/*혹시나...because of testing */
					if (mfTotalLevyList == null || mfTotalLevyList.isEmpty() )   {
						continue;
					}
					
					log.info("== CYB952001 수납취소" + (rec_cnt+1) +" ==(" + mfTotalLevyList.getMap("MSG") + ")");
					log.info("== CYB952001 수납취소" + (rec_cnt+1) +" ==(" + mfTotalLevyList.getMap("TRN_CD") + ")");
					
					String TRN_CD = mfTotalLevyList.getMap("TRN_CD").toString();
					
					//!.전자수납테이블을 삭제FLAG를 세우고 
					//!.상하수도부과상세 테이블의 수납Flag를 ='0'으로 셋팅한다...
					
					MapForm mfLog = new MapForm();
					
					MapForm mf_3111_list = new MapForm();
					
					if (TRN_CD.equals("532001")) { /* 실시간 단건납부 취소 */
						
						/*1 위택스 전문 수신로그 TABLE의 수신전문을 파싱한다. (Codm_BaseService.xml) */
						mfLog = FL_532001.parseBuff(mfTotalLevyList.getMap("MSG").toString().getBytes());
						
						/*2 수납정보를 찾는다. */
						mf_3111_list = sqlService_cyber.queryForMap("WE952001.TX3111_TB_532001_Select", mfLog);
						
						if(mf_3111_list == null || mf_3111_list.size() == 0) {
							
							retStr = "44120-101";
							
						} else {
							
							/*3 전자수납정보테이블의 간단 e납부 수납건에 대한 수납순번을 구한다. 삭제를 위해서...*/
							mf_3111_list.setMap("PAY_CNT", sqlService_cyber.getOneFieldInteger("WE952001.TX3211_TB_MaxValue", mf_3111_list));
							
							if (!((mf_3111_list.getMap("PAY_CNT").toString()).equals("9999"))) { //pay_cnt == 9999는 해당 SNSU코드의 수납건이 없다는 의미
							    /*4 상하수도부과테이블의 수납건수를 구해서 1건인 경우에만 수납FLAG를 셋팅 */
	                            if(sqlService_cyber.getOneFieldInteger("WE952001.TX3211_TB_SU_CNT", mf_3111_list) == 1) {
	                                
	                                /**
	                                 * 수납건수가 1인경우만...
	                                 * 1 이상인 경우는 중복이므로 부과상세테이블을 업데이트 하지 않는다.  
	                                 */
	                                //sqlService_cyber.queryForUpdate("WE952001.TX3111_TB_Cancle532001_Update", mfLog);
	                                sqlService_cyber.queryForUpdate("WE952001.TX3111_TB_Cancle532001_Update", mf_3111_list);
	                            }
	                            
	                            /*5 전자수납정보테이블에서 수납건을 삭제한다. */
	                            sqlService_cyber.queryForDelete("WE952001.TX3211_TB_Delete", mf_3111_list);
	                            
	                            log.info("상하수도 취소처리 완료! [" + mfLog.getMap("EPAY_NO") + "]");
							}
							log.info("상하수도 해당 간단e납부 수납건이 없으므로 패스]");
							retStr = "44120-000";
						}

					} else if (mfTotalLevyList.getMap("TRN_CD").equals("532002")) { /* 실시간 총괄납부 취소 */
						
						/*1 위택스 전문 수신로그 TABLE의 수신전문을 파싱한다. (Codm_BaseService.xml) */
						mfLog = FL_532002.parseBuff(mfTotalLevyList.getMap("MSG").toString().getBytes());
						
						/*2 수납정보를 찾는다. */
						mf_3111_list = sqlService_cyber.queryForMap("WE952001.TX3111_TB_532002_Select", mfLog);
						
						if(mf_3111_list == null || mf_3111_list.size() == 0) {
							
							retStr = "44120-101";
							
						} else {
							
							/*3 전자수납정보테이블의 수납순번을 구한다. 삭제를 위해서...*/
							mf_3111_list.setMap("PAY_CNT", sqlService_cyber.getOneFieldInteger("WE952001.TX3211_TB_MaxValue", mf_3111_list));
							
							if (!((mf_3111_list.getMap("PAY_CNT").toString()).equals("9999"))) { //pay_cnt == 9999는 해당 SNSU코드의 수납건이 없다는 의미
							    
    							/*4 상하수도부과상세테이블의 수납FLAG를 셋팅 */
    							if(sqlService_cyber.getOneFieldInteger("WE952001.TX3211_TB_SU_CNT", mf_3111_list) == 1) {
    								/*5 상하수도부과상세테이블의 수납FLAG를 셋팅 */
    								sqlService_cyber.queryForUpdate("WE952001.TX3111_TB_Cancle532002_Update", mfLog);
    							}
    							
    							/*5 전자수납 테이블에서 수납건을 삭제한다. */
    							sqlService_cyber.queryForDelete("WE952001.TX3211_TB_Delete", mf_3111_list);

                            }
							
							retStr = "44120-000";
						}

					} else {
						
						log.error("============================================");
						log.error("== CYB952001 전문코드 에러 :  "+ mfTotalLevyList.getMap("TRN_CD"));
						log.error("============================================");		
						return "44120-201";
					}
					
				}
				
			} else {
				
				log.info("============================================");
				log.info("== CYB952001 chkweTax 수납조회 오류!! ( 총 0건)   ");
				log.info("============================================");
				
				retStr = "44120-101";
			}

        } catch (Exception e) {
			
			e.printStackTrace();
			log.error("============================================");
			log.error("== CYB952001 chkweTax Exception(시스템) ");
			log.error("============================================");
			retStr =  "44120-201";
		}
		
        return retStr;	
	}
}
