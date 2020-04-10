/**
 *  주시스템명 : 부산 사이버지방세청 고도화
 *  업  무  명 : 전자신고 
 *  기  능  명 : 지방소득세 특별징수 신고 등록
 *  클래스  ID : Tax008Service
 *  변경  이력 :
 * ------------------------------------------------------------------------
 *  작성자      	소속  		    일자            Tag           내용
 * ------------------------------------------------------------------------
 *  YHCHOI       유채널        2015.01.13         %01%         최초작성
 *
 */

package com.uc.bs.cyber.daemon.txdm2610;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Element;

import com.uc.bs.cyber.CbUtil;
import com.uc.bs.cyber.SoapUtil;
import com.uc.core.MapForm;

public class Tax008Service {
	/**
	/**
	 * @param args
	 */
	
	private Log log ;
	
	public Tax008Service() {
		super();
		
		log = LogFactory.getLog(this.getClass());
	}
	
	public static void main(String[] args) {
		
		
	}

	 
	public MapForm sndService(StringBuffer sndForm, String SGG_COD) throws Exception {
		
		SoapUtil soap = new SoapUtil();
		MapForm recvForm = new MapForm(); 
		
		try {
				
			//log.debug("Tax008Service  sndService  SGG_COD : " + SGG_COD);
			// getSOAPMessage : 전문 송신 & 수신 처리
			Element rcvElement = soap.getSOAPMessage(sndForm, "CM_TAX_J2", "CM_TAX_J2_005", SGG_COD);
			
			//전송 후 응답 메시지 확인
			String message = soap.getValue(rcvElement, "MESSAGE");
			
            log.info("message===="+message);

			if(message.equals("SVR01")){			//성공일때  확인 필요
			
			//  =수신 데이터=
				recvForm.setMap("MESSAGE"        ,  soap.getValue(rcvElement, "MESSAGE")  );      // 처리구분
				recvForm.setMap("tax_gubun"      ,  "140004" );      // 처리구분
				recvForm.setMap("CUD_OPT"        ,  soap.getValue(rcvElement, "CUD_OPT")  );		  //(1)처리구분(1:신규,2:수정,3:삭제  단,삭제는 수납과다름)  
				recvForm.setMap("DLQ_DIV"        ,  soap.getValue(rcvElement, "DLQ_DIV")  ); 		  // (1)미납체납구분(1:미납(부과/정기) , 2:체납) 
				recvForm.setMap("LVY_DIV"        ,  soap.getValue(rcvElement, "LVY_DIV") );    		  // (2)부과구분(행자부표준코드(정기분,수시분,인터넷신고납부,…))
				recvForm.setMap("TAX_GDS"        ,  CbUtil.strEncod(soap.getValue(rcvElement, "TAX_GDS").toString(),"KSC5601","MS949").trim());     		  // (100)과세대상
				recvForm.setMap("TAX_NO"         ,  soap.getValue(rcvElement, "TAX_NO") );      	  // (31)납세번호
				recvForm.setMap("EPAY_NO"        ,  soap.getValue(rcvElement, "EPAY_NO")  );    	  // (19)전자납부번호     
				recvForm.setMap("REG_NO"         ,  soap.getValue(rcvElement, "REG_NO") );      	  // 주민-법인번호
				recvForm.setMap("DPNM"           ,  CbUtil.strEncod(soap.getValue(rcvElement, "REG_NM").toString(),"KSC5601","MS949").trim());      	  // 성명(법인명)
				recvForm.setMap("BIZ_NO"         ,  soap.getValue(rcvElement, "BIZ_NO") );      	  // (10)사업자번호
				recvForm.setMap("CMP_NM"         ,  CbUtil.strEncod(soap.getValue(rcvElement, "CMP_NM").toString(),"KSC5601","MS949").trim());         // 상호
				recvForm.setMap("SIDO_COD"       ,  "26" );     								      // (2)시도코드
				recvForm.setMap("SGG_COD"        ,  soap.getValue(rcvElement, "SGG_COD")  );    	  // (3)기관코드
				recvForm.setMap("CHK1"           ,  soap.getValue(rcvElement, "CHK1") );        	  // 검1
			    recvForm.setMap("ACCT_COD"       ,  soap.getValue(rcvElement, "ACCT_COD"    ) );      // 회계코드
			    recvForm.setMap("TAX_ITEM"       ,  soap.getValue(rcvElement, "TAX_ITEM"    ) );      // 과목코드
			    recvForm.setMap("TAX_YY"         ,  soap.getValue(rcvElement, "TAX_YY"      ) );      // 과세년도
			    recvForm.setMap("TAX_MM"         ,  soap.getValue(rcvElement, "TAX_MM"      ) );      // 과세월
			    recvForm.setMap("TAX_DIV"        ,  soap.getValue(rcvElement, "TAX_DIV"     ) );      // 과세구분
			    recvForm.setMap("HACD"      	 ,  soap.getValue(rcvElement, "ADONG_COD"   ) );      // 과세동
			    recvForm.setMap("TAX_SNO"        ,  soap.getValue(rcvElement, "TAX_SNO"     ) );      // 과세번호(6)  TAX_NO의 끝자리
			    recvForm.setMap("CHK2"           ,  soap.getValue(rcvElement, "CHK2"        ) );      // 검2
			    recvForm.setMap("SUM_B_AMT"      ,  soap.getValue(rcvElement, "SUM_B_AMT"   ) );      // 납기내총액
			    recvForm.setMap("SUM_F_AMT"      ,  soap.getValue(rcvElement, "SUM_F_AMT"   ) );      // 납기후총액
			    recvForm.setMap("CHK3"           ,  soap.getValue(rcvElement, "CHK3"        ) );      // 검3
			    recvForm.setMap("MNTX"           ,  soap.getValue(rcvElement, "MNTX"        ) );      // 본세
			    recvForm.setMap("CPTX"           ,  soap.getValue(rcvElement, "CPTX"        ) );      // 도시계획세
			    recvForm.setMap("CFTX_ASTX"      ,  soap.getValue(rcvElement, "CFTX_ASTX"   ) );      // 공동/농특세
			    recvForm.setMap("LETX"           ,  soap.getValue(rcvElement, "LETX"        ) );      // 교육세
			    recvForm.setMap("DUE_DT"         ,  soap.getValue(rcvElement, "DUE_DT"      ) );      // 납기일
			    recvForm.setMap("CHK4"           ,  soap.getValue(rcvElement, "CHK4"        ) );      // 검4
			    recvForm.setMap("FILLER"         ,  soap.getValue(rcvElement, "FILLER"      ) );      // 필러
			    recvForm.setMap("GOJI_DIV"       ,  soap.getValue(rcvElement, "GOJI_DIV"    ) );      // 고지구분
			    recvForm.setMap("CHK5"           ,  soap.getValue(rcvElement, "CHK5"        ) );      // 검5
			    recvForm.setMap("TAX_STD"        ,  soap.getValue(rcvElement,"TAX_STD"     ) );      // 과세표준액
			    recvForm.setMap("MNTX_ADTX"      ,  soap.getValue(rcvElement,"MNTX_ADTX"   ) );      // 본세 가산금
			    recvForm.setMap("CPTX_ADTX"      ,  soap.getValue(rcvElement,"CPTX_ADTX"   ) );      // 도시계획세 가산금
			    recvForm.setMap("CFTX_ADTX"      ,  soap.getValue(rcvElement,"CFTX_ADTX"   ) );      // 공동시설세 가산금
			    recvForm.setMap("LETX_ADTX"      ,  soap.getValue(rcvElement,"LETX_ADTX"   ) );      // 교육세 가산금
			    recvForm.setMap("ASTX_ADTX"      ,  soap.getValue(rcvElement,"ASTX_ADTX"   ) );      // 농특세 가산금
			    recvForm.setMap("TAX_STD_DIS"    ,  soap.getValue(rcvElement,"TAX_STD_DIS" ) );      // 과세표준설명
			    recvForm.setMap("CRE_DT"         ,  soap.getValue(rcvElement,"CRE_DT"      ) );      // 고지발생일
			    recvForm.setMap("AUTO_TRNF_YN"   ,  soap.getValue(rcvElement,"AUTO_TRNF_YN") );      // 자동이체여부
			    recvForm.setMap("DUE_DF_OPT"     ,  soap.getValue(rcvElement,"DUE_DF_OPT"  ) );      // 납기내후구분
			    recvForm.setMap("GIROSGG_COD"    ,  soap.getValue(rcvElement,"GIROSGG_COD" ) );      // 발행기관분류코드
			    recvForm.setMap("GIRO_NO"        ,  soap.getValue(rcvElement,"GIRO_NO"     ) );      // 지로번호
			    recvForm.setMap("DUE_F_DT"       ,  soap.getValue(rcvElement,"DUE_F_DT"    ) );      // 납기일(납기후)
			    recvForm.setMap("CHRG_NM"        ,  CbUtil.strEncod(soap.getValue(rcvElement,"CHRG_NM").toString(),"KSC5601","MS949").trim());        // 업무담당자이름
			    recvForm.setMap("CHRG_TEL"       ,  soap.getValue(rcvElement,"CHRG_TEL"    ) );      // 업무담당자 전화번호
			    recvForm.setMap("CHG_DT"         ,  soap.getValue(rcvElement,"CHG_DT"      ) );      // 수정일시
			    recvForm.setMap("PAY_DT"         ,  soap.getValue(rcvElement,"PAY_DT"      ) );      // 수납일자
			    
                //log.debug("=================recvForm================"+recvForm);
                
			}else {
				// 전문 송수신 오류입니다.
	            log.error("전문 송수신 오류입니다.");
	            recvForm=null;
			}
		} catch (Exception e) {
			
			e.printStackTrace();
			System.out.println(e.toString());
		}
		return recvForm;
		
	}
}
