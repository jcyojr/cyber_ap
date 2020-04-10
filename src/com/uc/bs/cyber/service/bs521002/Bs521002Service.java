/**
 *  주시스템명 : 사이버지방세청 고도화
 *  업  무  명 : 지방세 고지내역 상세조회 전문
 *  기  능  명 : 부산은행-사이버세청 
 *               업무처리
 *               결재원에서 수신된 전문에 대해서 응답전문데이트를 생성하고 
 *               조회결과를 저장한다.
 *  클래스  ID : Bs521001Service
 *  변경  이력 :
 * ------------------------------------------------------------------------
 *  작성자     소속  		 일자      Tag   내용
 * ------------------------------------------------------------------------   
 * 황종훈   다산(주)    2011.06.13   %01%  신규작성
 *  
 */
package com.uc.bs.cyber.service.bs521002;

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
public class Bs521002Service {

	protected Log log = LogFactory.getLog(this.getClass());
	
	private ApplicationContext appContext = null;
	
	private IbatisBaseService sqlService_cyber = null;
		
	private Bs521002FieldList bsField = null;
	
	CbUtil cUtil = null;
	
	/**
	 * 생성자
	 */
	public Bs521002Service(ApplicationContext appContext) {
		// TODO Auto-generated constructor stub
		
		setAppContext(appContext);
		
		bsField = new Bs521002FieldList();
		
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
	 * 지방세 고지내역 상세조회 처리...
	 * */
	public byte[] chk_bs_521002(MapForm headMap, MapForm mf) throws Exception {
		
				
		MapForm sendMap = new MapForm();
		
		/*지방세 고지내역조회 시 사용 */
		String resCode = "000";       /*응답코드*/
//		String giro_no = "1000685";   /*부산시지로코드*/
		String strTaxGubun = "";
		String sg_code = "26";        /*부산시기관코드*/
		
		try{
			cUtil = CbUtil.getInstance();
			
			log.info(cUtil.msgPrint("7", "S","BP521002 chk_bs_521002()", resCode));
			
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
			
			if (resCode.equals("000")) {
				
				mf.setMap("SGG_COD" , mf.getMap("TAXNO").toString().substring(0, 3)); 	//구청코드
				mf.setMap("ACCT_COD", mf.getMap("TAXNO").toString().substring(4, 6));   //회계코드
				mf.setMap("TAX_ITEM", mf.getMap("TAXNO").toString().substring(6, 12));  //과목/세목코드
				mf.setMap("TAX_YY"  , mf.getMap("TAXNO").toString().substring(12, 16)); //과세년도
				mf.setMap("TAX_MM"  , mf.getMap("TAXNO").toString().substring(16, 18));	//과세월
				mf.setMap("TAX_DIV" , mf.getMap("TAXNO").toString().substring(18, 19));	//기분코드
				mf.setMap("HACD"    , mf.getMap("TAXNO").toString().substring(19, 22));	//행정동코드
				mf.setMap("TAX_SNO" , mf.getMap("TAXNO").toString().substring(22, 28));	//과세번호
				
				
				log.info(mf.getMap("SGG_COD"));
				log.info(mf.getMap("ACCT_COD"));
				log.info(mf.getMap("TAX_ITEM"));
				log.info(mf.getMap("TAX_YY"));
				log.info(mf.getMap("TAX_MM"));
				log.info(mf.getMap("TAX_DIV"));
				log.info(mf.getMap("HACD"));
				log.info(mf.getMap("TAX_SNO"));
				
				ArrayList<MapForm>  bsCmd521002List  =  sqlService_cyber.queryForList("BS521002.SELECT_TAX_SEARCH_LIST", mf);
				
				log.debug("TAXNO = [" + mf.getMap("TAXNO") + "]");
				log.debug("JUMIN_NO = [" + mf.getMap("JUMIN_NO") + "]");
				
				if(bsCmd521002List.size() <= 0){
					resCode = "111";  // Error : 고지내역없음
				} else if (bsCmd521002List.size() > 1){
					resCode = "201";  // Error : 고지내역 2건 이상
				}
				

				if ( bsCmd521002List.size() > 0 && resCode.equals("000")) {
					
					resCode = "000";
					
					MapForm mfCmd521001List  =  bsCmd521002List.get(0);
					
					
					log.info(mfCmd521001List.getMap("SGG_COD").toString());
					log.info(mfCmd521001List.getMap("DUE_DT").toString());
					
					
					
					/*
					 *  앞부분 공통 
					 */
					
					/**
					 * 20110727 : 자동이체 등록건은 수납처리 할 수 없게 함...
					 */
					if(mfCmd521001List.getMap("AUTO_TRNF_YN").equals("Y")) {
						log.info("==============자동이체등록건(수납처리불가)================");
					}
					
					/* 자동이체 등록여부 */
					sendMap.setMap("AUTO_TRNF_YN", mfCmd521001List.getMap("AUTO_TRNF_YN"));
					
					/* 납부번호 */
					sendMap.setMap("TAXNO", mfCmd521001List.getMap("TAXNO"));

					/* 일련번호 */
					sendMap.setMap("SEQNO", mf.getMap("SEQNO"));
					/* 주민(법인,사업자) 번호 */
					sendMap.setMap("JUMIN_NO", mfCmd521001List.getMap("REG_NO"));
					/* 시도코드 */
					sendMap.setMap("SIDO", "26");
					/* 부과기관 */
					sendMap.setMap("BUGWA_GIGWAN", mfCmd521001List.getMap("SGG_COD"));
					/* 검증번호 1 */
					sendMap.setMap("CHK1", mfCmd521001List.getMap("TAXNO").toString().substring(3, 4)); // 이것도 이상함
					/* 회계 */
					sendMap.setMap("ACC_CD", mfCmd521001List.getMap("ACCT_COD"));	
					/* 과목세목 */
					sendMap.setMap("SEMOK_CD", mfCmd521001List.getMap("TAX_ITEM"));
					/* 과세년월 */
					sendMap.setMap("ACC_YM", mfCmd521001List.getMap("TAX_YM"));
					/* 기분 */
					sendMap.setMap("GIBN", mfCmd521001List.getMap("TAX_DIV"));
					/* 행정동 */
					sendMap.setMap("DONG_CD", mfCmd521001List.getMap("HACD"));
					/* 과세번호 */
					sendMap.setMap("TAX_SNO", mfCmd521001List.getMap("TAX_SNO"));
					/* 검증번호 2 */
					sendMap.setMap("CHK2", mfCmd521001List.getMap("TAXNO").toString().substring(mfCmd521001List.getMap("TAXNO").toString().length() - 1)); 
					/* 납부자성명 */
					sendMap.setMap("REG_NM", mfCmd521001List.getMap("REG_NM"));
					
					/*
					 * 앞부분 공통끝
					 */
					
					/* 납기구분 */
					String napGubun = cUtil.getNapkiGubun((String)mfCmd521001List.getMap("DUE_DT"));
					
					/* 버스, 주거지 전용 */
	                if(mfCmd521001List.getMap("TAX_GBN").equals("2")){
	                	/* 납기내금액 (납기내금액 납기후금액 일치) */
	                	sendMap.setMap("NAP_BFAMT", mfCmd521001List.getMap("AMT"));
	                	/* 납기후금액 */
	                	sendMap.setMap("NAP_AFAMT", sendMap.getMap("NAP_BFAMT"));
	                	
	                	/* 검증번호 3 */
	                	sendMap.setMap("CHK3", cUtil.getGum3(sendMap.getMap("NAP_BFAMT").toString(), sendMap.getMap("NAP_AFAMT").toString()));
	                	
	                	/* 과세표준 */
	                	sendMap.setMap("KWA_AMT", Long.parseLong(mfCmd521001List.getMap("TAX_STD").toString()));
	                	
	                	/* 세부 금액 */
	            		sendMap.setMap("MNTX", "");			//본세
	            		sendMap.setMap("MNTX_ADTX", "");	//본세 가산금
	            		sendMap.setMap("CPTX", "");			//도시계획세
	            		sendMap.setMap("CPTX_ADTX", "");	//도시계획세 가산금
	            		sendMap.setMap("CFTX", ""); 		//공동시설세/농특세
	            		sendMap.setMap("CFTX_ADTX", ""); 	//공동시설세/농특세 가산금
	            		sendMap.setMap("LETX", "");			//교육세
	            		sendMap.setMap("LETX_ADTX", "");	//교육세 가산금	                		
	                	
	                	/* 납기구분은 무조건 B */
	                	napGubun = "B";
	                	
	                	/* 납기내일자 */
	                	sendMap.setMap("DUE_DT", mfCmd521001List.getMap("DUE_DT"));
	                	/* 납기후일자 */
	                	sendMap.setMap("DUE_F_DT", mfCmd521001List.getMap("DUE_DT"));
	                	
	                	/* 검증번호 4 구하기 */
	                	sendMap.setMap("CHK4", cUtil.getGum4(mfCmd521001List.getMap("MNTX").toString(), "", "", "", mfCmd521001List.getMap("DUE_DT").toString()));
		                /* 필러 */
		                sendMap.setMap("FILLER", "");
		                /* 검증번호 5 */
		                sendMap.setMap("CHK5", "");
		                /* 과세사항 + 과세표준설명  */
		                sendMap.setMap("MLGN", "");
		                /* 부과일자 */
		                sendMap.setMap("GIGI_DATE", mfCmd521001List.getMap("TAX_DT"));
		                /* 수납은행점별코드 */
		                sendMap.setMap("BANK_CD", "");
		                /* 납부일시 */
		                sendMap.setMap("RECIP_DATE", "");
		                /* 납기구분 */
		                sendMap.setMap("NABGI_BA_GBN", napGubun);
		                /* 예비정보 2 */
		                sendMap.setMap("FIELD2", "");	
	                	
	                }
	                /* 지방세 */
	                else {
	                	/* 체납(DLQ_DIV= 1:부과 2:체납)분말고 기분이 3(자납) 아닐때 납기내금액 및 납기후금액 등등 */
	                	if (mfCmd521001List.getMap("DLQ_DIV").equals("1") && !mfCmd521001List.getMap("TAX_DIV").equals("3")){
	                		
	                		/* 납기내금액 */
	                		sendMap.setMap("NAP_BFAMT", cUtil.getNapBfAmt(Long.parseLong(mfCmd521001List.getMap("MNTX").toString())  		//본세
	                														, Long.parseLong(mfCmd521001List.getMap("CPTX").toString())		//도시계회
	                														, Long.parseLong(mfCmd521001List.getMap("CFTX").toString())		//공동시설세
	                														, Long.parseLong(mfCmd521001List.getMap("LETX").toString())		//교육세
	                														, Long.parseLong(mfCmd521001List.getMap("ASTX").toString())));	//농특세
	                		/* 납기후금액 */
	                		sendMap.setMap("NAP_AFAMT", cUtil.getNapAfAmt(mfCmd521001List.getMap("TAX_DT").toString()						//부과일자
											                				, Long.parseLong(mfCmd521001List.getMap("MNTX").toString())  	//본세
																			, Long.parseLong(mfCmd521001List.getMap("CPTX").toString())		//도시계회
																			, Long.parseLong(mfCmd521001List.getMap("CFTX").toString())		//공동시설세
																			, Long.parseLong(mfCmd521001List.getMap("LETX").toString())		//교육세
																			, Long.parseLong(mfCmd521001List.getMap("ASTX").toString())));	//농특세
		                	
			                /* 검증번호 3 */
			                sendMap.setMap("CHK3", cUtil.getGum3(sendMap.getMap("NAP_BFAMT").toString(), sendMap.getMap("NAP_AFAMT").toString()));
			                /* 과세표준액 */
			                sendMap.setMap("KWA_AMT", Long.parseLong(mfCmd521001List.getMap("TAX_STD").toString()));
	                		
	                		/* 본세 */
	                		sendMap.setMap("MNTX", mfCmd521001List.getMap("MNTX"));
	                		/* 본세 가산금 */
	                		sendMap.setMap("MNTX_ADTX", cUtil.getGasanAmt(napGubun, mfCmd521001List.getMap("TAX_DT").toString(), Long.parseLong(mfCmd521001List.getMap("MNTX").toString())));
	                		/* 도시계획세 */
	                		sendMap.setMap("CPTX", mfCmd521001List.getMap("CPTX"));
	                		/* 도시계획세 가산금 */
	                		sendMap.setMap("CPTX_ADTX", cUtil.getGasanAmt(napGubun, mfCmd521001List.getMap("TAX_DT").toString(), Long.parseLong(mfCmd521001List.getMap("CPTX").toString())));
	                		/* 공동시설세/농특세 */
	                		sendMap.setMap("CFTX", (Long.parseLong(mfCmd521001List.getMap("CFTX").toString()) + Long.parseLong(mfCmd521001List.getMap("ASTX").toString())));
	                		/* 공동시설세/농특세 가산금 */
	                		sendMap.setMap("CFTX_ADTX", cUtil.getGasanAmt(napGubun, mfCmd521001List.getMap("TAX_DT").toString(),
	                													  Long.parseLong(mfCmd521001List.getMap("CFTX").toString()) +
	                													  cUtil.getGasanAmtNont(napGubun, mfCmd521001List.getMap("TAX_DT").toString(), Long.parseLong(mfCmd521001List.getMap("ASTX").toString()))));
	                		/* 교육세 */
	                		sendMap.setMap("LETX", mfCmd521001List.getMap("LETX"));
	                		/* 교육세 가산금 */
	                		sendMap.setMap("LETX_ADTX", cUtil.getGasanAmt(napGubun, mfCmd521001List.getMap("TAX_DT").toString(), Long.parseLong(mfCmd521001List.getMap("LETX").toString())));
	                		
	                		/* 납기내일자 */
	                		sendMap.setMap("DUE_DT", mfCmd521001List.getMap("DUE_DT"));
	                		/* 납기후일자 */
	                		sendMap.setMap("DUE_F_DT", mfCmd521001List.getMap("DUE_F_DT"));
	                		
	                	} else {
	                		/* 납기내금액 */
	                		sendMap.setMap("NAP_BFAMT", cUtil.getNapBfAmt(Long.parseLong(mfCmd521001List.getMap("MNTX").toString())				//본세
											                				, Long.parseLong(mfCmd521001List.getMap("MNTX_ADTX").toString())  	//본세 가산금
																			, Long.parseLong(mfCmd521001List.getMap("CPTX").toString())			//도시계회세
																			, Long.parseLong(mfCmd521001List.getMap("CPTX_ADTX").toString())	//도시계회세 가산금
																			, Long.parseLong(mfCmd521001List.getMap("CFTX").toString())			//공동시설세
																			, Long.parseLong(mfCmd521001List.getMap("CFTX_ADTX").toString())	//공동시설세 가산금
																			, Long.parseLong(mfCmd521001List.getMap("LETX").toString())			//교육세
																			, Long.parseLong(mfCmd521001List.getMap("LETX_ADTX").toString())	//교육세 가산금
																			, Long.parseLong(mfCmd521001List.getMap("ASTX").toString())			//농특세
																		    , Long.parseLong(mfCmd521001List.getMap("ASTX_ADTX").toString())));	//농특세 가산금
	                		napGubun = "B";
	                		
	                		/* 납기후금액 (납기후 금액과 납기내 금액은 같다) */
	                		sendMap.setMap("NAP_AFAMT", sendMap.getMap("NAP_BFAMT"));
	                		
		                	
			                /* 검증번호 3 */
			                sendMap.setMap("CHK3", cUtil.getGum3(sendMap.getMap("NAP_BFAMT").toString(), sendMap.getMap("NAP_AFAMT").toString()));
			                /* 과세표준액 */
			                sendMap.setMap("KWA_AMT", Long.parseLong(mfCmd521001List.getMap("TAX_STD").toString()));
	                		
	                		/* 세부 금액 */
	                		sendMap.setMap("MNTX", mfCmd521001List.getMap("MNTX")); 			//본세
	                		sendMap.setMap("MNTX_ADTX", mfCmd521001List.getMap("MNTX_ADTX"));	//본세 가산금
	                		sendMap.setMap("CPTX", mfCmd521001List.getMap("CPTX"));				//도시계획세
	                		sendMap.setMap("CPTX_ADTX", mfCmd521001List.getMap("CPTX_ADTX"));	//도시계획세 가산금
	                		sendMap.setMap("CFTX", Long.parseLong(mfCmd521001List.getMap("CFTX").toString()) 
	                													+ Long.parseLong(mfCmd521001List.getMap("ASTX").toString())); 		//공동시설세/농특세
	                		sendMap.setMap("CFTX_ADTX", Long.parseLong(mfCmd521001List.getMap("CFTX_ADTX").toString()) 
	                													+ Long.parseLong(mfCmd521001List.getMap("ASTX_ADTX").toString())); 	//공동시설세/농특세 가산금
	                		sendMap.setMap("LETX", mfCmd521001List.getMap("LETX"));				//교육세
	                		sendMap.setMap("LETX_ADTX", mfCmd521001List.getMap("LETX_ADTX"));	//교육세 가산금
	                		
	                		/* 납기내일자 */
	                		sendMap.setMap("DUE_DT", mfCmd521001List.getMap("DUE_DT"));
	                		/* 납기후일자 */
	                		// sendMap.setMap("DUE_F_DT", mfCmd521001List.getMap("DUE_DT")); -- 납기후일자 처리오류 수정--2011.09.14 --김대완---
	                		sendMap.setMap("DUE_F_DT", mfCmd521001List.getMap("DUE_F_DT"));
	                		
	                	}
		                /*
		                 * 지방세 뒷부분 공통
		                 */

		                /* 검증번호 4 */
		                sendMap.setMap("CHK4", cUtil.getGum4(mfCmd521001List.getMap("MNTX").toString()
		                		                           , mfCmd521001List.getMap("CPTX").toString()
		                								   , mfCmd521001List.getMap("CFTX").toString()
		                								   , mfCmd521001List.getMap("LETX").toString()
		                								   , mfCmd521001List.getMap("TAX_YM").toString()));
		                /* 필러 */
		                sendMap.setMap("FILLER", "");
		                /* 검증번호 5 */
		                sendMap.setMap("CHK5", "");
		                /* 과세사항 + 과세표준설명  */
		                sendMap.setMap("MLGN", mfCmd521001List.getMap("MLGN"));
		                /* 부과일자 */
		                sendMap.setMap("GIGI_DATE", mfCmd521001List.getMap("TAX_DT"));
		                /* 수납은행점별코드 */
		                sendMap.setMap("BANK_CD", "");
		                /* 납부일시 */
		                sendMap.setMap("RECIP_DATE", "");
		                /* 납부일시 */
		                sendMap.setMap("NABGI_BA_GBN", napGubun);
		                /* 예비정보 2 */
		                sendMap.setMap("FIELD2", "");
		                /*
		                 * 지방세 끝
		                 */
	                }
					
				}else{ 
					/* 조회건수가 없는 경우 전문생성
					 * */
					
					resCode = "111";  /*조회내역없음*/
					
				} 
			}
			
			log.info(cUtil.msgPrint("7", "","BP521002 chk_bs_521002()", resCode));
			
        } catch (Exception e) {
        	
        	resCode = "093";  
        	
			e.printStackTrace();
			
			log.info(e.getMessage());
			log.error("============================================");
			log.error("== chk_bs_521002 Exception(시스템) ");
			log.error("============================================");
		}
        
        /*오류인경우 고대로 돌려줌*/
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

