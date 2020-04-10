/**
 *  주시스템명 : 사이버지방세청 고도화
 *  업  무  명 : 지방세 납부내역 상세조회 전문
 *  기  능  명 : 부산은행-사이버세청 
 *               업무처리
 *               결재원에서 수신된 전문에 대해서 응답전문데이트를 생성하고 
 *               조회결과를 저장한다.
 *  클래스  ID : Bs523002Service
 *  변경  이력 :
 * ------------------------------------------------------------------------
 *  작성자     소속  		 일자      Tag   내용
 * ------------------------------------------------------------------------   
 * 황종훈   다산(주)    2011.06.13   %01%  신규작성
 *  
 */
package com.uc.bs.cyber.service.bs523002;

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
public class Bs523002Service {

	protected Log log = LogFactory.getLog(this.getClass());
	
	private ApplicationContext appContext = null;
	
	private IbatisBaseService sqlService_cyber = null;
		
	private Bs523002FieldList bsField = null;
	
	CbUtil cUtil = null;
	
	/**
	 * 생성자
	 */
	public Bs523002Service(ApplicationContext appContext) {
		// TODO Auto-generated constructor stub
		
		setAppContext(appContext);
		
		bsField = new Bs523002FieldList();
		
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
	public byte[] chk_bs_523002(MapForm headMap, MapForm mf) throws Exception {
		
				
		MapForm sendMap = new MapForm();
		
		/*지방세 고지내역조회 시 사용 */
		String resCode = "000";       /*응답코드*/
//		String giro_no = "1000685";   /*부산시지로코드*/
		String strTaxGubun = "";
		String sg_code = "26";        /*부산시기관코드*/
		
		try{
			cUtil = CbUtil.getInstance();
			
			log.info(cUtil.msgPrint("9", "S","BP523002 chk_bs_523002()", resCode));
			
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

            if(!("".equals(mf.getMap("NAPBU_JUMIN_NO").toString().trim()))){
                resCode = "326";//납부자주민번호 조회 불가
            }
			/* 조회조건 자르기(납부번호 잘라서 조회조건 만든다) */
			mf.setMap("SGG_COD", mf.getMap("ETAXNO").toString().substring(0, 3)); 	//구청코드
			mf.setMap("ACCT_COD", mf.getMap("ETAXNO").toString().substring(4, 6));  //회계코드
			mf.setMap("TAX_ITEM", mf.getMap("ETAXNO").toString().substring(6, 12)); //과목/세목코드
			mf.setMap("TAX_YY", mf.getMap("ETAXNO").toString().substring(12, 16));  //과세년도
			mf.setMap("TAX_MM", mf.getMap("ETAXNO").toString().substring(16, 18));	//과세월
			mf.setMap("TAX_DIV", mf.getMap("ETAXNO").toString().substring(18, 19));	//기분코드
			mf.setMap("HACD", mf.getMap("ETAXNO").toString().substring(19, 22));	//행정동코드
			mf.setMap("TAX_SNO", mf.getMap("ETAXNO").toString().substring(22, 28));	//과세번호
			
			ArrayList<MapForm>  bsCmd523002List  =  sqlService_cyber.queryForList("BS523002.SELECT_RECIP_LIST", mf);
			
			log.debug("TAXNO = [" + mf.getMap("ETAXNO") + "]");
			//log.debug("JUMIN_NO = [" + mf.getMap("JUMIN_NO") + "]");
			
			if(bsCmd523002List.size() <= 0){
				resCode = "112";  // Error : 납부내역없음
			} else if(bsCmd523002List.size() > 1){
				resCode = "094";  // Error : 조회 2건 이상
			} else {
				
				if ( bsCmd523002List.size() > 0 ) {
					
					resCode = "000";
					
					MapForm mfCmd521001List  =  bsCmd523002List.get(0);
					
					/*
					 *  앞부분 공통 
					 */
					
					/* 납부번호 */
					sendMap.setMap("ETAXNO", mfCmd521001List.getMap("TAX_NO"));
					/* 일련번호 */
					sendMap.setMap("SEQNO", mf.getMap("SEQNO"));
					/* 주민(법인,사업자) 번호 */
					//sendMap.setMap("NAPBU_JUMIN_NO", mfCmd521001List.getMap("REG_NO"));
					/* 주민(법인,사업자) 번호 */
					//sendMap.setMap("JUMIN_NO", mfCmd521001List.getMap("REG_NO"));
					/* 시도코드 */
					sendMap.setMap("SIDO", "26");
					/* 부과기관 */
					sendMap.setMap("GU_CODE", mfCmd521001List.getMap("SGG_COD"));
					/* 검증번호 1 */
					sendMap.setMap("CHK1", mfCmd521001List.getMap("TAX_NO").toString().substring(3, 4)); // 이것도 이상함
					/* 회계 */
					sendMap.setMap("HCALVAL", mfCmd521001List.getMap("ACCT_COD"));	
					/* 과목세목 */
					sendMap.setMap("GWA_MOK", mfCmd521001List.getMap("TAX_ITEM"));
					/* 과세년월 */
					sendMap.setMap("TAX_YYMM", mfCmd521001List.getMap("TAX_YM"));
					/* 기분 */
					sendMap.setMap("KIBUN", mfCmd521001List.getMap("TAX_DIV"));
					/* 행정동 */
					sendMap.setMap("DONG_CODE", mfCmd521001List.getMap("HACD"));
					/* 과세번호 */
					sendMap.setMap("GWASE_NO", mfCmd521001List.getMap("TAX_SNO"));
					/* 검증번호 2 */
					sendMap.setMap("CHK2", mfCmd521001List.getMap("TAX_NO").toString().substring(mfCmd521001List.getMap("TAX_NO").toString().length() - 1)); 
					/* 납부자성명 */
					sendMap.setMap("NAP_NAME", mfCmd521001List.getMap("REG_NM"));
					
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
		                
		                /* 검증번호 4 */
		                sendMap.setMap("CHK4", cUtil.getGum4(mf.getMap("MNTX").toString(), mf.getMap("CPTX").toString()
		                										, mf.getMap("CFTX").toString(), mf.getMap("LETX").toString(), mf.getMap("TAX_YM").toString()));
		                /* 필러 */
		                sendMap.setMap("FILLER", "");
		                /* 검증번호 5 */
		                sendMap.setMap("CHK5", "");
		                /* 과세사항 + 과세표준설명  */
		                sendMap.setMap("MLGN", mfCmd521001List.getMap("MLGN"));
		                /* 부과일자 */
		                sendMap.setMap("GIGI_DATE", mfCmd521001List.getMap("TAX_DT"));
		                /* 납부이용시스템 */
		                sendMap.setMap("NAPBU_SYS", mfCmd521001List.getMap("NAPBU_SYS"));
		                /* 수납은행점별코드 */
		                sendMap.setMap("BANK_CD", mfCmd521001List.getMap("BRC_NO"));
		                /* 납부일시 */
		                sendMap.setMap("RECIP_DATE", mfCmd521001List.getMap("PAY_DT") + "000000");
		                /* 납부금액 */
		                sendMap.setMap("RECIP_AMT", mfCmd521001List.getMap("PAY_AMT"));
		                /* 출금계좌번호 */
		                sendMap.setMap("OUTACCT_NO", "");
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
	                		sendMap.setMap("CFTX_ADTX", cUtil.getGasanAmt(napGubun
	                				                                    , mfCmd521001List.getMap("TAX_DT").toString()
	                				                                    , Long.parseLong(mfCmd521001List.getMap("CFTX").toString()) + cUtil.getGasanAmtNont(napGubun, mfCmd521001List.getMap("TAX_DT").toString()
	                													, Long.parseLong(mfCmd521001List.getMap("ASTX").toString()))));
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
		                								   , mfCmd521001List.getMap("DUE_DT").toString()));
		                /* 필러 */
		                sendMap.setMap("FILLER", "");
		                /* 검증번호 5 */
		                sendMap.setMap("CHK5", "");
		                /* 과세사항 + 과세표준설명  */
		                sendMap.setMap("MLGN", mfCmd521001List.getMap("MLGN"));
		                /* 부과일자 */
		                sendMap.setMap("GOJICR_DATE", mfCmd521001List.getMap("TAX_DT"));
		                /* 납부이용시스템 */
		                sendMap.setMap("NAPBU_SYS", mfCmd521001List.getMap("NAPBU_SYS"));
		                /* 수납은행점별코드 */
		                sendMap.setMap("BANK_CD", mfCmd521001List.getMap("BRC_NO"));
		                /* 납부일시 */
		                sendMap.setMap("RECIP_DATE", mfCmd521001List.getMap("PAY_DT") + "000000");
		                /* 납부금액 */
		                sendMap.setMap("RECIP_AMT", mfCmd521001List.getMap("PAY_AMT"));
		                /* 출금계좌번호 */
		                sendMap.setMap("OUTACCT_NO", "");
		                /* 예비정보 2 */
		                sendMap.setMap("FIELD2", "");
		                
		                
		                /*
		                 * 지방세 끝
		                 */                
	                }
					
				}else{ 
					/* 조회건수가 없는 경우 전문생성
					 * */
					
					resCode = "112";  /*조회내역없음*/
					
				} 
				
			}

			log.info(cUtil.msgPrint("9", "","BP523002 chk_bs_523002()", resCode));
			
        } catch (Exception e) {
        	
			resCode = "093";
			
			e.printStackTrace();
			log.error("============================================");
			log.error("== chk_bs_523002 Exception(시스템) ");
			log.error("============================================");
		}
        
        if(!resCode.equals("000")){
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

