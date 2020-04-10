/**
 *  주시스템명 : 부산 사이버지방세청 고도화
 *  업  무  명 : 전자신고 
 *  기  능  명 : 지방소득세 종합소득  신고 등록
 *  클래스  ID : Tax004Service
 *  변경  이력 :
 * ------------------------------------------------------------------------
 *  작성자      	소속  		    일자            Tag           내용
 * ------------------------------------------------------------------------
 *  박시현       다산시스템     2011.05.13         %01%         최초작성
 *  
 */

package com.uc.bs.cyber.daemon.txdm2610;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Element;

import com.uc.bs.cyber.CreateMessages_eswb5010;
import com.uc.bs.cyber.CbUtil;
import com.uc.bs.cyber.DocumentVars;
import com.uc.bs.cyber.SoapUtil;
import com.uc.core.MapForm;

public class Tax009Service {
	/**
	/**
	 * @param args
	 */
	
	private Log log ;
	
	public Tax009Service() {
		super();
		
		log = LogFactory.getLog(this.getClass());
	}
	
	public static void main(String[] args) {
		
		
	}



	public MapForm sndService(MapForm sndForm) throws Exception {
		
		String SGG_COD = sndForm.get("SGG_COD").toString();
		
		SoapUtil soap = new SoapUtil();
		MapForm recvForm = new MapForm();
		
		CreateMessages_eswb5010 cMsg = new CreateMessages_eswb5010(sndForm);
		
		String [][] taxVars  = cMsg.getTaxVars();

		try {
			
			log.info("주민세 재산분 send msg [" + sndForm + "]");
			
			// 지방세 사업단으로 전송
			cMsg = new CreateMessages_eswb5010(sndForm);
						
			// getSOAPMessage : 전문 송신 & 수신 처리
			Element rcvElement = soap.getSOAPMessage(cMsg.getMessage(), "CM_TAX_53", "CM_TAX_53_165", SGG_COD);
			
			//전송 후 응답 메시지 확인
			String message = soap.getValue(rcvElement, "MESSAGE");
			
			
			log.info("message====  " + message);
			  
			if(message.equals("SVR01")){			//성공일때 확인 필요
				
				taxVars = DocumentVars.getDocVars("104009");
				
				for(int i= 0; i<taxVars[1].length; i++) {
					
					recvForm.setMap(taxVars[1][i], CbUtil.strEncod((soap.getValue(rcvElement, taxVars[1][i])), "KSC5601","MS949"));
				}
				// insertForm에서 필요한 값을 recvForm에 맵핑 
				recvForm.setMap("tax_gubun"      ,   "104009" );      						// 처리구분
				recvForm.setMap("TAX_GDS"        ,   CbUtil.strEncod(recvForm.getMap("TAX_GDS").toString(),"KSC5601","MS949").trim());  // 과세대상
	            recvForm.setMap("DPNM"           ,   CbUtil.strEncod(recvForm.getMap("REG_NM").toString(),"KSC5601","MS949").trim());   // 성명(법인명)
	            recvForm.setMap("CMP_NM"         ,   CbUtil.strEncod(recvForm.getMap("CMP_NM").toString(),"KSC5601","MS949").trim());   // 상호
	            recvForm.setMap("CHRG_NM"        ,   CbUtil.strEncod(recvForm.getMap("CHRG_NM").toString(),"KSC5601","MS949").trim());  // 업무담당자이름
	            recvForm.setMap("CHG_DT"         ,   CbUtil.convertDate(recvForm.getMap("CHG_DT").toString()));        				    // 수정일자
	            recvForm.setMap("BIZ_GRNO"       ,   CbUtil.convertDate(recvForm.getMap("GIRO_NO").toString()).trim());        			// 대장관리번호
	            recvForm.setMap("B_TAX_NO"       ,   recvForm.getMap("TAX_NO"       ) );      // (31)납세번호
	            
                /*
                recvForm.setMap("CUD_OPT"         ,   sndForm.get("CUD_OPT"     ) );     	 // 처리구분
                recvForm.setMap("DLQ_DIV"         ,   sndForm.get("DLQ_DIV"     ) );      	 // 미납체납구분
                recvForm.setMap("LVY_DIV"         ,   insertForm.get("LVY_DIV"     ) );      // 부과구분
                recvForm.setMap("TAX_GDS"         ,   insertForm.get("TAX_GDS"     ) );      // 과세대상
                recvForm.setMap("TAX_NO"          ,   insertForm.get("TAX_NO"      ) );      // 납세번호
                recvForm.setMap("EPAY_NO"         ,   insertForm.get("EPAY_NO"     ) );      // 전자납부번호
                recvForm.setMap("REG_NO"          ,   insertForm.get("REG_NO"      ) );      // 주민-법인번호
                recvForm.setMap("REG_NM"          ,   insertForm.get("REG_NM"      ) );      // 성명(법인명)
                recvForm.setMap("BIZ_NO"          ,   insertForm.get("BIZ_NO"      ) );      // 사업자번호
                recvForm.setMap("CMP_NM"          ,   insertForm.get("CMP_NM"      ) );      // 상호
                recvForm.setMap("SIDO_COD"        ,   insertForm.get("SIDO_COD"    ) );      // 시도코드
                recvForm.setMap("SGG_COD"         ,   insertForm.get("SGG_COD"     ) );      // 기관코드
                recvForm.setMap("CHK1"            ,   insertForm.get("CHK1"        ) );      // 검1
                recvForm.setMap("ACCT_COD"        ,   insertForm.get("ACCT_COD"    ) );      // 회계코드
                recvForm.setMap("TAX_ITEM"        ,   insertForm.get("TAX_ITEM"    ) );      // 과목코드
                recvForm.setMap("TAX_YY"          ,   insertForm.get("TAX_YY"      ) );      // 과세년도
                recvForm.setMap("TAX_MM"          ,   insertForm.get("TAX_MM"      ) );      // 과세월
                recvForm.setMap("TAX_DIV"         ,   insertForm.get("TAX_DIV"     ) );      // 과세구분
                recvForm.setMap("ADONG_COD"       ,   insertForm.get("ADONG_COD"   ) );      // 과세동
                recvForm.setMap("TAX_SNO"         ,   insertForm.get("TAX_SNO"     ) );      // 과세번호
                recvForm.setMap("CHK2"            ,   insertForm.get("CHK2"        ) );      // 검2
                recvForm.setMap("SUM_B_AMT"       ,   insertForm.get("SUM_B_AMT"   ) );      // 납기내총액
                recvForm.setMap("SUM_F_AMT"       ,   insertForm.get("SUM_F_AMT"   ) );      // 납기후총액
                recvForm.setMap("CHK3"            ,   insertForm.get("CHK3"        ) );      // 검3
                recvForm.setMap("MNTX"            ,   insertForm.get("MNTX"        ) );      // 본세
                recvForm.setMap("CPTX"            ,   insertForm.get("CPTX"        ) );      // 도시계획세
                recvForm.setMap("CFTX_ASTX"       ,   insertForm.get("CFTX_ASTX"   ) );      // 공동/농특세
                recvForm.setMap("LETX"            ,   insertForm.get("LETX"        ) );      // 교육세
                recvForm.setMap("DUE_DT"          ,   insertForm.get("DUE_DT"      ) );      // 납기일
                recvForm.setMap("CHK4"            ,   insertForm.get("CHK4"        ) );      // 검4
                recvForm.setMap("FILLER"          ,   insertForm.get("FILLER"      ) );      // 필러
                recvForm.setMap("GOJI_DIV"        ,   insertForm.get("GOJI_DIV"    ) );      // 고지구분
                recvForm.setMap("CHK5"            ,   insertForm.get("CHK5"        ) );      // 검5
                recvForm.setMap("TAX_STD"         ,   insertForm.get("TAX_STD"     ) );      // 과세표준액
                recvForm.setMap("MNTX_ADTX"       ,   insertForm.get("MNTX_ADTX"   ) );      // 본세 가산금
                recvForm.setMap("CPTX_ADTX"       ,   insertForm.get("CPTX_ADTX"   ) );      // 도시계획세 가산금
                recvForm.setMap("CFTX_ADTX"       ,   insertForm.get("CFTX_ADTX"   ) );      // 공동시설세 가산금
                recvForm.setMap("LETX_ADTX"       ,   insertForm.get("LETX_ADTX"   ) );      // 교육세 가산금
                recvForm.setMap("ASTX_ADTX"       ,   insertForm.get("ASTX_ADTX"   ) );      // 농특세 가산금
                recvForm.setMap("TAX_STD_DIS"     ,   insertForm.get("TAX_STD_DIS" ) );      // 과세표준설명
                recvForm.setMap("CRE_DT"          ,   insertForm.get("CRE_DT"      ) );      // 고지발생일
                recvForm.setMap("AUTO_TRNF_YN"    ,   insertForm.get("AUTO_TRNF_YN") );      // 자동이체여부
                recvForm.setMap("DUE_DF_OPT"      ,   insertForm.get("DUE_DF_OPT"  ) );      // 납기내후구분
                recvForm.setMap("GIROSGG_COD"     ,   insertForm.get("GIROSGG_COD" ) );      // 발행기관분류코드
                recvForm.setMap("GIRO_NO"         ,   insertForm.get("GIRO_NO"     ) );      // 지로번호
                recvForm.setMap("DUE_F_DT"        ,   insertForm.get("DUE_F_DT"    ) );      // 납기일(납기후)
                recvForm.setMap("CHRG_NM"         ,   insertForm.get("CHRG_NM"     ) );      // 업무담당자 성명
                recvForm.setMap("CHRG_TEL"        ,   insertForm.get("CHRG_TEL"    ) );      // 업무담당자 전화번호
                recvForm.setMap("CHG_DT"          ,   insertForm.get("CHG_DT"      ) );      // 수정일시
                recvForm.setMap("PAY_DT"          ,   insertForm.get("PAY_DT"      ) );      // 수납일자                
                */
                
			}else {
				// 전문 송수신 오류입니다.
				log.error("전문 송수신 오류입니다.");
			}
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		
		return recvForm;
	}
}
