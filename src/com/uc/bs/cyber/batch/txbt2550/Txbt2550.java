/**
 * 결제원으로 예약파일 전송
 * 누가 제발 모듈화 시켜라..
 */
package com.uc.bs.cyber.batch.txbt2550;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.uc.bs.cyber.CbUtil;
import com.uc.bs.cyber.daemon.txdm2550.Txdm2550FieldList;
import com.uc.core.MapForm;
import com.uc.core.misc.Utils;
import com.uc.core.misc.XMLFileReader;
import com.uc.core.misc.XmlUtils;
import com.uc.core.spring.service.IbatisBaseService;
import com.uc.egtob.net.ClientMessageService;
import java.io.*;
import java.math.BigDecimal;
import java.util.ArrayList;

/**
 * @author Administrator
 *
 */
public class Txbt2550 extends ClientMessageService implements Runnable {

	protected Log log = LogFactory.getLog(this.getClass());
	
	private IbatisBaseService  sqlService_cyber = null;
	
	private ApplicationContext  appContext = null;
	
	private byte[] recvBuffer;
	private String outFilePath  = "";
	private String backFilePath = "";
	private String sndFilePath  = "";
	
	/**
	 * 
	 */
	public Txbt2550() {
		// TODO Auto-generated constructor stub
		
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		System.out.println("=================================================");
		System.out.println("사이버지방세청 예약납부파일 연계 Client Started");
		System.out.println("=================================================");	
		
		Txbt2550 client;
		
		ApplicationContext context  = null;
		
		try {
			// Log
			client = new Txbt2550();

			try {
				
				CbUtil.setupLog4jConfig(client, "log4j.txbt2550.xml");
				
				String[] xmls = XMLFileReader.getAllFilePathArray(CbUtil.getResourcePath(client, "/"), "SERVICE.XML");

				String strSrc = CbUtil.getResourcePath(client, "/") + "config/sqlmaps.xml";

				String strDest = CbUtil.getResourcePath(client, "/") + "config/sqlmapConfig.xml";

				XmlUtils.setXmlAttributes(client, strSrc, strDest, "sqlMapConfig", "sqlMap", "url", xmls);

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
		
			context = new ClassPathXmlApplicationContext("config/Seoi-Spring-db.xml");
			
			client.setContext(context);
			
			Thread thr = new Thread(client, "thr_2550");
			
			thr.start();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	public Object getService(String beanName) {
		// TODO Auto-generated method stub
		return appContext.getBean(beanName);
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
		if(log.isInfoEnabled()) {
			log.info("=======================================================");
			log.info("=============결제원예약납부 파일전송 시================");
			log.info("=======================================================");
		}
		
		sqlService_cyber = (IbatisBaseService) this.appContext.getBean("baseService_cyber");
		
		try {
			
			Txbt2550 client = new Txbt2550();
			
			outFilePath  = "/app/data/cyber_ap/recv/";
			backFilePath = "/app/data/cyber_ap/back/";
			sndFilePath  = "/app/data/cyber_ap/send/";
			
			/*1. 수신된 파일중 전영업일의 파일명을 모조리 찾아온다...*/
			
			/*2. 수신된 전영업일의 파일을 읽어서 원장과 비교하여 수납된 파일 상태를 변경한다...*/
			
			/*3. 
			 *   파일정리가 된 파일은 결제원으로 전문 송신한다...
			 *   단 파일을 송신하는 경우 1056 bytes 단위로 통신규약대로 전송한다...
			 * */
			
			/*전영업일 가져오기...*/
			MapForm mf = new MapForm();

			mf.setMap("d_cnt" , 1);
			mf.setMap("CUR_DT", CbUtil.getCurrentDate());
			
			int gymd_yn = (Integer)sqlService_cyber.queryForBean("TXBT2550.GET_GYMD_YN", mf);
			
			/*영업일인 경우만...*/
			if (gymd_yn == 1) {
				
				/*익영업일기준*/
				String BefrDate = (String)sqlService_cyber.queryForBean("TXBT2550.GET_GYMD_A", mf);
				String JobDate  = (String)sqlService_cyber.queryForBean("TXBT2550.GET_GYMD_B", mf);
				
				mf.setMap("ACCTDT"    , BefrDate);
				mf.setMap("TRANSDATE" , JobDate);
				
				log.info("============================================");
				log.info("CUR_DATE = [" + CbUtil.getCurrentDate() + "] 전영업일 = [" + JobDate + "] 익영업일 = [" + BefrDate + "]");
				log.info("============================================");
				
				regist_state_batch("S");
				
				/*상하수도 예약납부 파일 전송*/
				res_sanghasudo_file_transfer(client, mf);
				
				/*세외수입 예약납부 파일 전송*/
				res_nontax_receipt_file_transfer(client, mf);
				
				/*환경개선 예약납부 파일 전송:: 취급안한다...*/
				//res_env_improvement_file_transfer(client, mf);
				
				regist_state_batch("E");
				
			} else {
				
				log.info("============================================");
				log.info("CUR_DATE = [" + CbUtil.getCurrentDate() + "] 영업일이 아니므로 전송하지 않음...");
				log.info("============================================");
			}
			
			
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			 e.printStackTrace();
		}
		
		if(log.isInfoEnabled()) {
			log.info("=======================================================");
			log.info("=============결제원예약납부 파일전송 끝================");
			log.info("=======================================================");
		}
	}
	
	public void setContext(ApplicationContext context) {
		// TODO Auto-generated method stub
		this.appContext = context;
	}	
	
	/**
	 * 상하수도 예약납부파일 전송
	 * @param client
	 * @param mf
	 */
	private void res_sanghasudo_file_transfer(Txbt2550 client, MapForm mf){
		
		
		ArrayList<MapForm> alJunmunInfo = null;
		
		try {
			
			CbUtil cUtil = CbUtil.getInstance();
			
			/*상하수도*/
			mf.setMap("TAX_GB" , "S");
			
			/*상하수도 예약납부 전문수신내역을 가져온다.*/			
			alJunmunInfo = sqlService_cyber.queryForList("TXBT2550.SELECT_JUNMUN_LIST", mf);
			
			if (alJunmunInfo.size()  >  0)   {
				
				log.info("==========================================");
				log.info("===============  상하수도  ===============");
				log.info("==========================================");
				
				Txdm2550FieldList KFL_2550 = new Txdm2550FieldList();
				
				StringBuffer sbHead = new StringBuffer();
				StringBuffer sbTail = new StringBuffer();
				StringBuffer sbData = new StringBuffer();
				
				MapForm mpHead = new MapForm();
				MapForm mpTail = new MapForm();
				MapForm mpData = new MapForm();
				
				int  inc = 0;
				long amt = 0;
				
				for ( int rec_cnt = 0;  rec_cnt < alJunmunInfo.size();  rec_cnt++)   {
					
					MapForm mfJunmunInfo =  alJunmunInfo.get(rec_cnt);
					
					/*혹시나...because of testing */
					if (mfJunmunInfo == null  ||  mfJunmunInfo.isEmpty() )   {
						continue;
					}
					
					/*DATA 부*/
					KFL_2550.GR6676_Data_FieldList();  /*변경고지 Data 전문*/
												
					//수납처리(납부) 되었다면...
					if (sqlService_cyber.getOneFieldInteger("TXBT2550.SELECT_SHSD_KFPAY_CNT", mfJunmunInfo) > 0 ) {
						
						if(mfJunmunInfo.getMap("ACCTDT").equals(mf.getMap("ACCTDT"))) {
							
							inc++;
							
							MapForm mp_S = sqlService_cyber.queryForMap("TXBT2550.SELECT_SHSD_KFPAY_LIST", mfJunmunInfo);
							
							//전문처리 변수선언
						    String BNAPDATE             = "";       // 납기내 납기일
						    long BNAPAMT                = 0 ;       // 납기내 금액
						    long ANAPAMT                = 0 ;       // 납기후 금액
						    String GUM2                 = "";       // 검증번호2
						    long BSAMT                  = 0 ;       // 상수도 납기내 금액
						    long BHAMT                  = 0 ;       // 하수도 납기내 금액
						    long BGAMT                  = 0 ;       // 지하수도 납기내 금액
						    long BMAMT                  = 0 ;       // 물이용부담금 납기내 금액
						    long ASAMT                  = 0 ;       // 상수도 납기후 금액      
						    long AHAMT                  = 0 ;       // 하수도 납기후 금액      
						    long AGAMT                  = 0 ;       // 지하수도 납기후 금액    
						    long AMAMT                  = 0 ;       // 물이용부담금 납기후 금액
						    String ANAPDATE             = "";       // 납기후 납기일
						    String GUM3                 = "";       // 검증번호3
						    String CNAPTERM             = "";       // 체납기간
						    String ADDR                 = "";       // 주소
						    String USETERM              = "";       // 사용기간
						    String NAPGUBUN             = "";       // 납기내구분
						    String NapJUMMun            = "";
						    String napkiGubun           = "";
						    
						    String dang = mp_S.getMap("GUBUN").toString().substring(1);										
						
			                //납기일자 SET
			                BNAPDATE = (String) mp_S.getMap("DUE_DT");         //납기내 납기일
			                ANAPDATE = (String) mp_S.getMap("DUE_F_DT");       //납기후 납기일
							
			                ADDR     = (String) mp_S.getMap("ADDRESS");        //주소  
			                
			                //납기내(B), 납기후(A) 체크
			                napkiGubun = cUtil.getNapGubun(BNAPDATE, ANAPDATE);

			                NAPGUBUN = napkiGubun;
			                
			                if(napkiGubun.equals("B")) {
			                	NapJUMMun = "1";
			                }else{
			                	NapJUMMun = "2";
			                }
			                
							if(dang.equals("1") || dang.equals("3")) {
								
								if(log.isDebugEnabled()){
									log.debug("정기/수시 분인경우");
								}

								
				                //납부총액 SET
				                BNAPAMT  = ((BigDecimal)mp_S.getMap("SUM_B_AMT")).longValue();         //납기내 금액
				                ANAPAMT  = ((BigDecimal)mp_S.getMap("SUM_F_AMT")).longValue();         //납기후 금액
								
				                GUM2 = "";
				                
				                //납부세부금액 SET
				                BSAMT    = ((BigDecimal)mp_S.getMap("FEE_AMT1")).longValue();         //납기내 상수도금액
				                BHAMT    = ((BigDecimal)mp_S.getMap("FEE_AMT2")).longValue();         //납기내 하수도금액
				                BGAMT    = ((BigDecimal)mp_S.getMap("FEE_AMT4")).longValue();         //납기내 지하수금액
				                BMAMT    = ((BigDecimal)mp_S.getMap("FEE_AMT3")).longValue();         //납기내 물이용금액
				                ASAMT    = ((BigDecimal)mp_S.getMap("FEE_AMT1_H")).longValue();         //납기후 상수도금액
				                AHAMT    = ((BigDecimal)mp_S.getMap("FEE_AMT2_H")).longValue();         //납기후 하수도금액
				                AGAMT    = ((BigDecimal)mp_S.getMap("FEE_AMT4_H")).longValue();         //납기후 지하수금액
				                AMAMT    = ((BigDecimal)mp_S.getMap("FEE_AMT3_H")).longValue();         //납기후 물이용금액

				                GUM3 = "";

				                CNAPTERM = "";  //체납기간

				                USETERM  = (String) mp_S.getMap("USE_STT_DATE") + (String) mp_S.getMap("USE_END_DATE");          //사용기간

								
							} else if (dang.equals("2")){
								
								if(log.isDebugEnabled()){
									log.debug("체납분인 경우");
								}
								
								
								//납기일자 SET
				                //납기내인경우 납기내 일자 SET, 납기후인 경우 납기후 일자 SET
				                //납기내(B), 납기후(A) 체크
								if(NAPGUBUN.equals("B")) {
				                    BNAPDATE = (String) mp_S.getMap("DUE_DT");         //납기후 납기일
				                    ANAPDATE = BNAPDATE;
				                } else  {
				                    BNAPDATE = (String) mp_S.getMap("DUE_F_DT");       //납기후 납기일
				                    ANAPDATE = BNAPDATE;
				                }
								
								//체납분인경우 무조건 납기내기한으로 납부구분 설정
				                NAPGUBUN = "B";

				                NapJUMMun = "3";  /*체납은 3*/
				                
				                //납부총액 SET
				                BNAPAMT  = ((BigDecimal)mp_S.getMap("SUM_B_AMT")).longValue();        //납기내 금액
				                ANAPAMT  = 0;

				                GUM2 = "";

				                //납부세부금액 SET
				                BSAMT    = ((BigDecimal)mp_S.getMap("FEE_AMT1")).longValue();         //납기내 상수도금액
				                BHAMT    = ((BigDecimal)mp_S.getMap("FEE_AMT2")).longValue();         //납기내 하수도금액
				                BGAMT    = ((BigDecimal)mp_S.getMap("FEE_AMT4")).longValue();         //납기내 지하수금액
				                BMAMT    = ((BigDecimal)mp_S.getMap("FEE_AMT3")).longValue();         //납기내 물이용금액
				                ASAMT    = 0;
				                AHAMT    = 0;
				                AGAMT    = 0;
				                AMAMT    = 0;

				                GUM3 = "";

				                CNAPTERM = (String) mp_S.getMap("USE_STT_DATE") + (String) mp_S.getMap("USE_END_DATE");          //체납기간
				                USETERM = CNAPTERM;          //사용기간

							}
							
							mpData.setMap("BUSINESSGUBUN"     , "GR6676");                                            /*업무구분            */
							mpData.setMap("DATAGUBUN"         , "22");                                                /*데이터구분          */
							mpData.setMap("SEQNO"             , CbUtil.lPadString(String.valueOf(inc), 7, '0'));      /*일련번호            */
							mpData.setMap("PUBGCODE"          , mfJunmunInfo.getMap("PUBGCODE"));                     /*발행기관분류코드    */
							mpData.setMap("JIRONO"            , mfJunmunInfo.getMap("GIRONO"));                       /*발행기관확인및 검증 */
							mpData.setMap("CUSTNO"            , mp_S.getMap("CUST_NO"));                              /*수용가번호          */
							mpData.setMap("TAX_YM"            , mp_S.getMap("TAX_YM"));                               /*부과년월            */
							mpData.setMap("DANGGUBUN"         , mp_S.getMap("GUBUN").toString().substring(1));        /*당월 구분                                         */
							mpData.setMap("NPNO"              , mp_S.getMap("PRT_NPNO"));                             /*관리 번호                                         */
							mpData.setMap("REGNM"             , mp_S.getMap("REG_NM"));                               /*성명                */
					        mpData.setMap("BNAPDATE"          , BNAPDATE);                                            /*납기내 납기일              | 납부 마감일          */
							mpData.setMap("BNAPAMT"           , BNAPAMT);                                             /*납기내 금액                | 체납액               */
							mpData.setMap("ANAPAMT"           , ANAPAMT);                                             /*납기후 금액                                       */
							mpData.setMap("GUM2"              , GUM2);                                                /*검증번호 2                                        */
							mpData.setMap("BSAMT"             , BSAMT);                                               /*상수도납기내금액           |  상수도 체납액       */
							mpData.setMap("BHAMT"             , BHAMT);                                               /*하수도납기내금액           |  하수도 체납액       */
							mpData.setMap("BGAMT"             , BGAMT);                                               /*지하수납기내금액           |  지하수 체납액       */
							mpData.setMap("BMAMT"             , BMAMT);                                               /*물이용부담금납기내금액     |  물이용부담금체납액  */
							mpData.setMap("ASAMT"             , ASAMT);                                               /*상수도납기후금액                                  */
							mpData.setMap("AHAMT"             , AHAMT);                                               /*하수도납기후금액                                  */
							mpData.setMap("AGAMT"             , AGAMT);                                               /*지하수납기후금액                                  */
							mpData.setMap("AMAMT"             , AMAMT);                                               /*물이용부담금납기후금액                            */
							mpData.setMap("ANAPDATE"          , ANAPDATE);                                            /*납기후 납기일                                     */
							mpData.setMap("GUM3"              , GUM3);                                                /*검증번호 3                                        */
							mpData.setMap("CNAPTERM"          , CNAPTERM);                                            /*체납 기간                                         */
							mpData.setMap("ADDR"              , ADDR);                                                /*주소                                              */
							mpData.setMap("USETERM"           , USETERM);                                             /*사용 기간                                         */
							mpData.setMap("AUTOREG"           , "N");                                                 /*자동이체 등록 여부                                */
							mpData.setMap("SNAP_BANK_CODE"    , mp_S.getMap("BRC_NO"));                               /*수납은행 점별 코드                                */
							mpData.setMap("SNAP_SYMD"         , mp_S.getMap("SUNAPDT"));                              /*납부 일시                                         */
							mpData.setMap("NAPGUBUN"          , NAPGUBUN);                                            /*납기 내후 구분(납기내B만)                         */
							mpData.setMap("ETC1"              , " ");                                                 /*예비 정보 FIELD                                   */
							mpData.setMap("CUST_ADMIN_NUM"    , " ");                                                 /*고객관리번호                                      */
							mpData.setMap("OCR"               , mp_S.getMap("OCR_BD"));                               /*OCR BAND                                          */
							mpData.setMap("PRCGB"             , "D");                                                 /*처리구분            */
							mpData.setMap("FILLER"            , " ");                                                 /*FILLER              */
							
							/*파일의 바이너리 데이터*/
							sbData.append(new String(KFL_2550.getBuff(mpData)));
							
						}

						
					} else {
						
						//금액이 틀린 경우...
						MapForm mp_S = sqlService_cyber.queryForMap("TXBT2550.SELECT_SHSD_LEVY_LIST", mfJunmunInfo);
						
						if(mp_S.size() > 0) { // 데이터가 존재하면...
							
							if(mfJunmunInfo.getMap("ACCTDT").equals(mf.getMap("ACCTDT"))) {
								
								inc++;
								
								//전문처리 변수선언
							    String BNAPDATE             = "";       // 납기내 납기일
							    long BNAPAMT                = 0 ;       // 납기내 금액
							    long ANAPAMT                = 0 ;       // 납기후 금액
							    String GUM2                 = "";       // 검증번호2
							    long BSAMT                  = 0 ;       // 상수도 납기내 금액
							    long BHAMT                  = 0 ;       // 하수도 납기내 금액
							    long BGAMT                  = 0 ;       // 지하수도 납기내 금액
							    long BMAMT                  = 0 ;       // 물이용부담금 납기내 금액
							    long ASAMT                  = 0 ;       // 상수도 납기후 금액      
							    long AHAMT                  = 0 ;       // 하수도 납기후 금액      
							    long AGAMT                  = 0 ;       // 지하수도 납기후 금액    
							    long AMAMT                  = 0 ;       // 물이용부담금 납기후 금액
							    String ANAPDATE             = "";       // 납기후 납기일
							    String GUM3                 = "";       // 검증번호3
							    String CNAPTERM             = "";       // 체납기간
							    String ADDR                 = "";       // 주소
							    String USETERM              = "";       // 사용기간
							    String AUTOREG              = "";       // 자동이체등록여부
							    String NAPGUBUN             = "";       // 납기내후구분코드
							    String CUST_ADMIN_NUM       = "";       // 고객관리번호
							    String OCR                  = "";       // OCR
							    String NapJUMMun            = "";
							    String napkiGubun           = "";
								
								String dang = mp_S.getMap("GUBUN").toString().substring(1);
						
				                //납기일자 SET
				                BNAPDATE = (String) mp_S.getMap("DUE_DT");         //납기내 납기일
				                ANAPDATE = (String) mp_S.getMap("DUE_F_DT");       //납기후 납기일
								
				                ADDR     = (String) mp_S.getMap("ADDRESS");        //주소  
				                OCR      = (String) mp_S.getMap("OCR_1") + (String) mp_S.getMap("OCR_2");
				                
				                //납기내(B), 납기후(A) 체크
				                napkiGubun = cUtil.getNapGubun(BNAPDATE, ANAPDATE);

				                NAPGUBUN = napkiGubun;
				                
				                if(napkiGubun.equals("B")) {
				                	NapJUMMun = "1";
				                }else{
				                	NapJUMMun = "2";
				                }
				                
				                CUST_ADMIN_NUM = (String) mp_S.getMap("CUST_NO"); 
				                
								if(dang.equals("1") || dang.equals("3")) {
									
									if(log.isDebugEnabled()){
										log.debug("정기/수시");
									}

					                //납부총액 SET
					                BNAPAMT  = ((BigDecimal)mp_S.getMap("SUM_B_AMT")).longValue();         //납기내 금액
					                ANAPAMT  = ((BigDecimal)mp_S.getMap("SUM_F_AMT")).longValue();         //납기후 금액
									
					                GUM2 = "";
					                
					                //납부세부금액 SET
					                BSAMT    = ((BigDecimal)mp_S.getMap("FEE_AMT1")).longValue();         //납기내 상수도금액
					                BHAMT    = ((BigDecimal)mp_S.getMap("FEE_AMT2")).longValue();         //납기내 하수도금액
					                BGAMT    = ((BigDecimal)mp_S.getMap("FEE_AMT4")).longValue();         //납기내 지하수금액
					                BMAMT    = ((BigDecimal)mp_S.getMap("FEE_AMT3")).longValue();         //납기내 물이용금액
					                ASAMT    = ((BigDecimal)mp_S.getMap("FEE_AMT1_H")).longValue();         //납기후 상수도금액
					                AHAMT    = ((BigDecimal)mp_S.getMap("FEE_AMT2_H")).longValue();         //납기후 하수도금액
					                AGAMT    = ((BigDecimal)mp_S.getMap("FEE_AMT4_H")).longValue();         //납기후 지하수금액
					                AMAMT    = ((BigDecimal)mp_S.getMap("FEE_AMT3_H")).longValue();         //납기후 물이용금액

					                GUM3 = "";

					                CNAPTERM = "0";  //체납기간

					                USETERM  = (String) mp_S.getMap("USE_STT_DATE") + (String) mp_S.getMap("USE_END_DATE");          //사용기간

					                AUTOREG  = "N";      //자동이체 등록여부

					                
					                
								} else if (dang.equals("2")){
									
									if(log.isDebugEnabled()){
										log.debug("체납");
									}
									
									
									//납기일자 SET
					                //납기내인경우 납기내 일자 SET, 납기후인 경우 납기후 일자 SET
					                //납기내(B), 납기후(A) 체크
									if(NAPGUBUN.equals("B"))		                {
					                    BNAPDATE = (String) mp_S.getMap("DUE_DT");         //납기후 납기일
					                    ANAPDATE = BNAPDATE;
					                } else  {
					                    BNAPDATE = (String) mp_S.getMap("DUE_F_DT");       //납기후 납기일
					                    ANAPDATE = BNAPDATE;
					                }
									
									//체납분인경우 무조건 납기내기한으로 납부구분 설정
					                NAPGUBUN = "B";

					                //납부총액 SET
					                BNAPAMT  = ((BigDecimal)mp_S.getMap("SUM_B_AMT")).longValue();          //납기내 금액
					                ANAPAMT  = 0;

					                GUM2 = "";

					                //납부세부금액 SET
					                BSAMT    = ((BigDecimal)mp_S.getMap("FEE_AMT1")).longValue();         //납기내 상수도금액
					                BHAMT    = ((BigDecimal)mp_S.getMap("FEE_AMT2")).longValue();         //납기내 하수도금액
					                BGAMT    = ((BigDecimal)mp_S.getMap("FEE_AMT4")).longValue();         //납기내 지하수금액
					                BMAMT    = ((BigDecimal)mp_S.getMap("FEE_AMT3")).longValue();         //납기내 물이용금액
					                ASAMT    = 0;
					                AHAMT    = 0;
					                AGAMT    = 0;
					                AMAMT    = 0;

					                GUM3 = "";

					                CNAPTERM = (String) mp_S.getMap("NOT_STT_DATE") + (String) mp_S.getMap("NOT_END_DATE");          //체납기간
					                USETERM = CNAPTERM;          //사용기간

					                AUTOREG = "N";      //자동이체 등록여부

								}
								
								mpData.setMap("BUSINESSGUBUN"     , "GR6676");                                            /*업무구분            */
								mpData.setMap("DATAGUBUN"         , "22");                                                /*데이터구분          */
								mpData.setMap("SEQNO"             , CbUtil.lPadString(String.valueOf(inc), 7, '0'));      /*일련번호            */
								mpData.setMap("PUBGCODE"          , mfJunmunInfo.getMap("PUBGCODE"));                     /*발행기관분류코드    */
								mpData.setMap("JIRONO"            , mfJunmunInfo.getMap("GIRONO"));                       /*발행기관확인및 검증 */
								mpData.setMap("CUSTNO"            , mp_S.getMap("CUST_NO"));                              /*수용가번호          */
								mpData.setMap("TAX_YM"            , mp_S.getMap("TAX_YM"));                               /*부과년월            */
								mpData.setMap("DANGGUBUN"         , mp_S.getMap("GUBUN").toString().substring(1));        /*당월 구분                                         */
								mpData.setMap("NPNO"              , mp_S.getMap("PRT_NPNO"));                             /*관리 번호                                         */
								mpData.setMap("REGNM"             , mp_S.getMap("REG_NM"));                               /*성명                */
						        mpData.setMap("BNAPDATE"          , BNAPDATE);                                            /*납기내 납기일              | 납부 마감일          */
								mpData.setMap("BNAPAMT"           , BNAPAMT);                                             /*납기내 금액                | 체납액               */
								mpData.setMap("ANAPAMT"           , ANAPAMT);                                             /*납기후 금액                                       */
								mpData.setMap("GUM2"              , GUM2);                                                /*검증번호 2                                        */
								mpData.setMap("BSAMT"             , BSAMT);                                               /*상수도납기내금액           |  상수도 체납액       */
								mpData.setMap("BHAMT"             , BHAMT);                                               /*하수도납기내금액           |  하수도 체납액       */
								mpData.setMap("BGAMT"             , BGAMT);                                               /*지하수납기내금액           |  지하수 체납액       */
								mpData.setMap("BMAMT"             , BMAMT);                                               /*물이용부담금납기내금액     |  물이용부담금체납액  */
								mpData.setMap("ASAMT"             , ASAMT);                                               /*상수도납기후금액                                  */
								mpData.setMap("AHAMT"             , AHAMT);                                               /*하수도납기후금액                                  */
								mpData.setMap("AGAMT"             , AGAMT);                                               /*지하수납기후금액                                  */
								mpData.setMap("AMAMT"             , AMAMT);                                               /*물이용부담금납기후금액                            */
								mpData.setMap("ANAPDATE"          , ANAPDATE);                                            /*납기후 납기일                                     */
								mpData.setMap("GUM3"              , GUM3);                                                /*검증번호 3                                        */
								mpData.setMap("CNAPTERM"          , CNAPTERM);                                            /*체납 기간                                         */
								mpData.setMap("ADDR"              , ADDR);                                                /*주소                                              */
								mpData.setMap("USETERM"           , USETERM);                                             /*사용 기간                                         */
								mpData.setMap("AUTOREG"           , "N");                                                 /*자동이체 등록 여부                                */
								mpData.setMap("SNAP_BANK_CODE"    , mp_S.getMap("BRC_NO"));                               /*수납은행 점별 코드                                */
								mpData.setMap("SNAP_SYMD"         , mp_S.getMap("SUNAPDT"));                              /*납부 일시                                         */
								mpData.setMap("NAPGUBUN"          , NapJUMMun);                                           /*납기 내후 구분                                    */
								mpData.setMap("ETC1"              , " ");                                                 /*예비 정보 FIELD                                   */
								mpData.setMap("CUST_ADMIN_NUM"    , " ");                                                 /*고객관리번호                                      */
								mpData.setMap("OCR"               , mp_S.getMap("OCR_BD"));                               /*OCR BAND                                          */
								mpData.setMap("FILLER"            , " ");                                                 /*FILLER              */
								
								if(mfJunmunInfo.getMap("NAPGB").equals(1) || mfJunmunInfo.getMap("NAPGB").equals(3)) { //납기내또는 납기무관
									
									if(BNAPAMT != Long.getLong(mfJunmunInfo.getMap("NAPBU_AMT").toString())) {
										//금액이 틀린경우 : 변경고지 U:변경
										mpData.setMap("PRCGB"          ,"U");    /*처리구분*/
									}
									
									amt += BNAPAMT;
									
								}else if(mfJunmunInfo.getMap("NAPGB").equals(2)) { //납기후
									
									if(ANAPAMT != Long.getLong(mfJunmunInfo.getMap("NAPBU_AMT").toString())) {
										//금액이 틀린경우 : 변경고지 U:변경
										mpData.setMap("PRCGB"          ,"U");    /*처리구분*/
									}
									
									amt += ANAPAMT;
								}
								
								sbData.append(new String(KFL_2550.getBuff(mpData)));
							}
							
						}
						
					}					

					if(rec_cnt == alJunmunInfo.size() -1) {
						
						/*이때는 HEAD부과 TAILER 부만 만든다.*/
						mpHead.setMap("BUSINESSGUBUN"  ,"GR6676");                       /*업무구분          */
						mpHead.setMap("DATAGUBUN"      ,"11");                           /*데이터구분        */
						mpHead.setMap("SEQNO"          ,"0000000");                      /*일련번호          */
						mpHead.setMap("BANKCODE"       ,"011");                          /*(저축)은행코드    */
						mpHead.setMap("TOTALCOUNT"     , 0 );                            /*총 DATA RECORD 수 */
						mpHead.setMap("TRANSDATE"      ,mf.getMap("ACCTDT"));            /*수납일자          */
						mpHead.setMap("FILLER"         ," ");              

						/*Trailer 부*/
						mpTail.setMap("BUSINESSGUBUN"  ,"GR6676");                       /*업무구분          */
						mpTail.setMap("DATAGUBUN"      ,"33");                           /*데이터구분        */
						mpTail.setMap("SEQNO"          ,"9999999");                      /*일련번호          */
						mpTail.setMap("BANKCODE"       ,"011");                          /*(저축)은행코드    */
						mpTail.setMap("TOTALCOUNT"     , 0 );                            /*총 DATA RECORD 수 */
						mpTail.setMap("TOTALPAYMONEY"  , 0 );                            /*수납금액          */
						mpTail.setMap("FILLER"         ," ");                            /*FILLER            */	

						/*파일을 생성시킨다.*/
						if(mpHead.toString().length() > 0 || mpTail.toString().length() > 0){
							
							String filedata = "";
							
							mpHead.setMap("TOTALCOUNT"     ,inc); 
							mpTail.setMap("TOTALCOUNT"     ,inc);                            
							mpTail.setMap("TOTALPAYMONEY"  ,amt);                            
							
							KFL_2550.GR6676_Head_FieldList();  /*변경고지 Head 전문*/
							sbHead.append(new String(KFL_2550.getBuff(mpHead)));

							KFL_2550.GR6676_Tailer_FieldList();/*변경고지 Tail 전문*/
							sbTail.append(new String(KFL_2550.getBuff(mpTail)));
							
							filedata = sbHead.toString() + sbData.toString() + sbTail.toString();
							String sndFile = sndFilePath + "GR6676261004102" + mpHead.getMap("TRANSDATE").toString();
							
							log.debug("filedata.size = [" + filedata.length() + "]");
							log.debug("filedata = [" + filedata + "]");
							
							makeFile(filedata.getBytes(), sndFile, "new");
							
							/*파일이 만들어졌다면 결제원으로 전송한다.*/
							kf_rev_file_transefer(client, sndFile);
							
						}
					}
				}
				
			} else {
				/*
				 * 데이터 수신내역이 없다면 빈파일이라도 만들어야 하나...???
				 * */
				
				String filedata = "";
				
				Txdm2550FieldList KFL_2550 = new Txdm2550FieldList();
				
				StringBuffer sbHead = new StringBuffer();
				StringBuffer sbTail = new StringBuffer();
				
				MapForm mpHead = new MapForm();
				MapForm mpTail = new MapForm();
				
				/*이때는 HEAD부과 TAILER 부만 만든다.*/
				mpHead.setMap("BUSINESSGUBUN"  ,"GR6676");                       /*업무구분          */
				mpHead.setMap("DATAGUBUN"      ,"11");                           /*데이터구분        */
				mpHead.setMap("SEQNO"          ,"0000000");                      /*일련번호          */
				mpHead.setMap("BANKCODE"       ,"011");                          /*(저축)은행코드    */
				mpHead.setMap("TOTALCOUNT"     , 0 );                            /*총 DATA RECORD 수 */
				mpHead.setMap("TRANSDATE"      ,mf.getMap("ACCTDT"));            /*수납일자          */
				mpHead.setMap("FILLER"         ," ");              

				/*Trailer 부*/
				mpTail.setMap("BUSINESSGUBUN"  ,"GR6676");                       /*업무구분          */
				mpTail.setMap("DATAGUBUN"      ,"33");                           /*데이터구분        */
				mpTail.setMap("SEQNO"          ,"9999999");                      /*일련번호          */
				mpTail.setMap("BANKCODE"       ,"011");                          /*(저축)은행코드    */
				mpTail.setMap("TOTALCOUNT"     , 0 );                            /*총 DATA RECORD 수 */
				mpTail.setMap("TOTALPAYMONEY"  , 0 );                            /*수납금액          */
				mpTail.setMap("FILLER"         ," ");                            /*FILLER            */	
				
				KFL_2550.GR6676_Head_FieldList();  /*변경고지 Head 전문*/
				sbHead.append(new String(KFL_2550.getBuff(mpHead)));

				KFL_2550.GR6676_Tailer_FieldList();/*변경고지 Tail 전문*/
				sbTail.append(new String(KFL_2550.getBuff(mpTail)));
				
				filedata = sbHead.toString() + sbTail.toString();
				String sndFile = sndFilePath + "GR6676261004102" + mpHead.getMap("TRANSDATE").toString();
				
				log.debug("filedata.size = [" + filedata.length() + "]");
				log.debug("filedata = [" + filedata + "]");
				
				makeFile(filedata.getBytes(), sndFile, "new");
				
				/*파일이 만들어졌다면 결제원으로 전송한다.*/
				kf_rev_file_transefer(client, sndFile);
				
			}
			

		}catch (Exception e) {
			// TODO Auto-generated catch block
			 e.printStackTrace();
		}
	}
	
	/**
	 * 세외수입 예약납부 파일 전송
	 * @param client
	 * @param mf
	 */
	private void res_nontax_receipt_file_transfer(Txbt2550 client, MapForm mf){
		
		ArrayList<MapForm> alJunmunInfo = null;
		
		try {
			
			CbUtil cUtil = CbUtil.getInstance();
			
			/*세외수입*/
			mf.setMap("TAX_GB" , "O");
			
			/*전문수신내역을 가져온다...*/			
			alJunmunInfo = sqlService_cyber.queryForList("TXBT2550.SELECT_JUNMUN_LIST", mf);
			
			if (alJunmunInfo.size()  >  0)   {
				
				Txdm2550FieldList KFL_2550 = new Txdm2550FieldList();
				
				StringBuffer sbHead = new StringBuffer();
				StringBuffer sbTail = new StringBuffer();
				StringBuffer sbData = new StringBuffer();
				
				MapForm mpHead = new MapForm();
				MapForm mpTail = new MapForm();
				MapForm mpData = new MapForm();
				
				int inc = 0;
				long amt = 0;
				
				for ( int rec_cnt = 0;  rec_cnt < alJunmunInfo.size();  rec_cnt++)   {
					
					MapForm mfJunmunInfo =  alJunmunInfo.get(rec_cnt);
					
					/*혹시나...because of testing */
					if (mfJunmunInfo == null  ||  mfJunmunInfo.isEmpty() )   {
						continue;
					}
					
					KFL_2550.GR6695_Data_FieldList();  /*변경고지 Data 전문*/
					
					mfJunmunInfo.setMap("TAX_NO", mfJunmunInfo.getMap("OCR_BD").toString().substring(0, 31));
					
					//수납처리(납부) 되었다면...(예약납부가 아니고 수납처리된 건이어야 함...)
					if (sqlService_cyber.getOneFieldInteger("TXBT2550.SELECT_O_RECIP_CNT", mfJunmunInfo) > 0 ) {
						
						if(mfJunmunInfo.getMap("ACCTDT").equals(mf.getMap("ACCTDT"))) {
							
							inc++;
							
							MapForm mp_O = sqlService_cyber.queryForMap("TXBT2550.SELECT_O_RECIP_LIST", mfJunmunInfo);
							
							String Nap = cUtil.getNapGubun((String)mp_O.getMap("DUE_DT"), (String)mp_O.getMap("DUE_F_DT"));
							if (Nap.equals("B")) {
								Nap = "1";
								amt += Long.parseLong(mp_O.getMap("AMT").toString());
							} else {
								Nap = "2";
								amt += Long.parseLong(mp_O.getMap("AFT_AMT").toString());
							}
							
							/*주의사항) 예약납부파일은 납기내만 따라서 무조건 B로셋팅 : 20110930 결제원 이보람과장*/
							Nap = "B";
							
							mpData.setMap("BUSINESSGUBUN"       ,  "GR6695"  );                                        /*업무구분            */
							mpData.setMap("DATAGUBUN"           ,  "22"  );                                            /*데이터구분          */
							mpData.setMap("SEQNO"               ,  CbUtil.lPadString(String.valueOf(inc), 7, '0')  );  /*일련번호            */
							mpData.setMap("PUBGCODE"            ,  mfJunmunInfo.getMap("PUBGCODE")  );                 /*발행기관분류코드    */
							mpData.setMap("JIRONO"              ,  mfJunmunInfo.getMap("GIRONO")  );                   /*발행기관확인및 검증 */
							mpData.setMap("EPAY_NO"             ,  mp_O.getMap("EPAY_NO")  );                          /*전자납부번호        */
							mpData.setMap("REG_NO"              ,  mp_O.getMap("REG_NO")  );                           /*주민번호            */
							mpData.setMap("FIELD1"              ,  " "  );                                             /*예비 정보 FIELD 1   */
							mpData.setMap("BUGWA_GB"            ,  mp_O.getMap("TAX_DIV")  );                          /*고지구분 		     */
							mpData.setMap("SEMOK_CD"            ,  mp_O.getMap("TAX_ITEM")  );                         /*과목/세목           */
							mpData.setMap("SEMOK_NM"            ,  mp_O.getMap("TAX_NM")  );                           /*과목/세목명         */
							mpData.setMap("GBN"                 ,  mp_O.getMap("GBN")  );                              /*구분                */
							mpData.setMap("OCR_BD"              ,  mp_O.getMap("OCR_BD") );                            /*밴드                */
							mpData.setMap("NAP_NAME"            ,  mp_O.getMap("REG_NM")  );                           /*납부자 성명         */
							mpData.setMap("NAP_BFAMT"           ,  mp_O.getMap("AMT")  );                              /*납기내 금액         */
							mpData.setMap("NAP_AFAMT"           ,  mp_O.getMap("AFT_AMT")  );                          /*납기후 금액         */
							mpData.setMap("GUKAMT"          	,  mp_O.getMap("NATN_TAX")  );                         /*국세 	             */
							mpData.setMap("GUKAMT_ADD"          ,  mp_O.getMap("NATN_RATE")  );                        /*국세 가산금         */
							mpData.setMap("SIDO_AMT"            ,  mp_O.getMap("SIDO_TAX")  );                         /*시도세              */
							mpData.setMap("SIDO_AMT_ADD"        ,  mp_O.getMap("SIDO_RATE")  );                        /*시도세 가산금       */
							mpData.setMap("SIGUNGU_AMT"         ,  mp_O.getMap("SIGUNGU_TAX")  );                      /*시군구세      	     */
							mpData.setMap("SIGUNGU_AMT_ADD"     ,  mp_O.getMap("SIGUNGU_RATE")  );                     /*시군구세 가산금   	 */
							mpData.setMap("BUNAP_AMT"           ,  "0"  );                                             /*분납이자/기금       */
							mpData.setMap("BUNAP_AMT_ADD"       ,  "0"  );                                             /*분납이자/기금 가산금*/
							mpData.setMap("FIELD2"              ,  " "  );                                             /*예비 정보 FIELD 2   */
							mpData.setMap("NAP_BFDATE"          ,  mp_O.getMap("DUE_DT")  );                           /*납기일 (납기내)     */
							mpData.setMap("NAP_AFDATE"          ,  mp_O.getMap("DUE_F_DT")  );                         /*납기일 (납기후)     */
							mpData.setMap("GWASE_ITEM"          ,  mp_O.getMap("TAX_GDS")  );                          /*과세대상            */
							mpData.setMap("BUGWA_TAB"           ,  mp_O.getMap("TAX_DESC")  );                         /*부과내역            */
							mpData.setMap("GOJI_DATE"   		,  mp_O.getMap("BUGWA_DT")  );                         /*고지자료 발생일자   */
							mpData.setMap("OUTO_ICHE_GB"        ,  "N"  );                                             /*자동이체등록여부    */
							mpData.setMap("SUNAB_BANK_CD"       ,  mp_O.getMap("BRC_NO")  );                           /*수납은행 점별 코드  */
							mpData.setMap("RECIP_DATE"          ,  mp_O.getMap("PAY_DT").toString() + "000000"  );     /*납부일시			 */
							mpData.setMap("NAPGI_BA_GB"         ,  Nap  );                                             /*납기내후구분        */
							mpData.setMap("PRCGB"               ,  "D"  );                                             /*처리구분            */
							mpData.setMap("FILED3"          	,  " "  );                                             /*예비정보 FIELD 3    */		

							/*파일의 바이너리 데이터*/
							sbData.append(new String(KFL_2550.getBuff(mpData)));
							
						}

					} else {
						
						
						if(mfJunmunInfo.getMap("OCR_BD").toString().substring(0, 2).equals("26")) {   /*구세외수입 장표*/
							
							mfJunmunInfo.setMap("TAX_NO"   , mfJunmunInfo.getMap("OCR_BD").toString().substring(2, 31));
							
							log.info("구장표 TAX_NO = " + mfJunmunInfo.getMap("TAX_NO"));

						} else {
							
							mfJunmunInfo.setMap("TAX_NO"   , mfJunmunInfo.getMap("OCR_BD").toString().substring(0, 31));
						}
						
						mfJunmunInfo.setMap("SGG_COD"  , mfJunmunInfo.getMap("TAX_NO").toString().substring(0, 3)); 	//구청코드
						mfJunmunInfo.setMap("ACCT_COD" , mfJunmunInfo.getMap("TAX_NO").toString().substring(4, 6));     //회계코드
						mfJunmunInfo.setMap("TAX_ITEM" , mfJunmunInfo.getMap("TAX_NO").toString().substring(6, 12));    //과목/세목코드
						mfJunmunInfo.setMap("TAX_YY"   , mfJunmunInfo.getMap("TAX_NO").toString().substring(12, 16));   //과세년도
						mfJunmunInfo.setMap("TAX_MM"   , mfJunmunInfo.getMap("TAX_NO").toString().substring(16, 18));	//과세월
						mfJunmunInfo.setMap("TAX_DIV"  , mfJunmunInfo.getMap("TAX_NO").toString().substring(18, 19));	//기분코드
						mfJunmunInfo.setMap("HACD"     , mfJunmunInfo.getMap("TAX_NO").toString().substring(19, 22));	//행정동코드
						mfJunmunInfo.setMap("TAX_SNO"  , mfJunmunInfo.getMap("TAX_NO").toString().substring(22, 28));	//과세번호
						
						// 금액비교
						MapForm mp_O = sqlService_cyber.queryForMap("TXBT2550.SELECT_O_SEARCH_LIST", mfJunmunInfo);
						
						if(mp_O.size() > 0) {
							
							if(mfJunmunInfo.getMap("ACCTDT").equals(mf.getMap("ACCTDT"))) {
								
								inc++;
								
								mpData.setMap("BUSINESSGUBUN"       ,  mfJunmunInfo.getMap("BUSINESSGUBUN")  );            /*업무구분            */
								mpData.setMap("DATAGUBUN"           ,  "22"  );                                            /*데이터구분          */
								mpData.setMap("SEQNO"               ,  CbUtil.lPadString(String.valueOf(inc), 7, '0')  );  /*일련번호            */
								mpData.setMap("PUBGCODE"            ,  mfJunmunInfo.getMap("PUBGCODE")  );                 /*발행기관분류코드    */
								mpData.setMap("JIRONO"              ,  mfJunmunInfo.getMap("GIRONO")  );                   /*발행기관확인및 검증 */
								mpData.setMap("EPAY_NO"             ,  mp_O.getMap("EPAY_NO")  );                          /*전자납부번호        */
								mpData.setMap("REG_NO"              ,  mp_O.getMap("REG_NO")  );                           /*주민번호            */
								mpData.setMap("FIELD1"              ,  " "  );                                             /*예비 정보 FIELD 1   */
								mpData.setMap("BUGWA_GB"            ,  mp_O.getMap("TAX_DIV")  );                          /*고지구분 		     */
								mpData.setMap("SEMOK_CD"            ,  mp_O.getMap("TAX_ITEM")  );                         /*과목/세목           */
								mpData.setMap("SEMOK_NM"            ,  mp_O.getMap("TAX_NM")  );                           /*과목/세목명         */
								mpData.setMap("GBN"                 ,  mp_O.getMap("GBN")  );                              /*구분                */
								mpData.setMap("OCR_BD"              ,  mp_O.getMap("OCR_BD") );                            /*밴드                */
								mpData.setMap("NAP_NAME"            ,  mp_O.getMap("REG_NM")  );                           /*납부자 성명         */
								mpData.setMap("NAP_BFAMT"           ,  mp_O.getMap("AMT")  );                              /*납기내 금액         */
								mpData.setMap("NAP_AFAMT"           ,  mp_O.getMap("AFT_AMT")  );                          /*납기후 금액         */
								mpData.setMap("GUKAMT"          	,  mp_O.getMap("NATN_TAX")  );                         /*국세 	             */
								mpData.setMap("GUKAMT_ADD"          ,  mp_O.getMap("NATN_RATE")  );                        /*국세 가산금         */
								mpData.setMap("SIDO_AMT"            ,  mp_O.getMap("SIDO_TAX")  );                         /*시도세              */
								mpData.setMap("SIDO_AMT_ADD"        ,  mp_O.getMap("SIDO_RATE")  );                        /*시도세 가산금       */
								mpData.setMap("SIGUNGU_AMT"         ,  mp_O.getMap("SIGUNGU_TAX")  );                      /*시군구세      	     */
								mpData.setMap("SIGUNGU_AMT_ADD"     ,  mp_O.getMap("SIGUNGU_RATE")  );                     /*시군구세 가산금   	 */
								mpData.setMap("BUNAP_AMT"           ,  "0"  );                                             /*분납이자/기금       */
								mpData.setMap("BUNAP_AMT_ADD"       ,  "0"  );                                             /*분납이자/기금 가산금*/
								mpData.setMap("FIELD2"              ,  " "  );                                             /*예비 정보 FIELD 2   */
								mpData.setMap("NAP_BFDATE"          ,  mp_O.getMap("DUE_DT")  );                           /*납기일 (납기내)     */
								mpData.setMap("NAP_AFDATE"          ,  mp_O.getMap("DUE_F_DT")  );                         /*납기일 (납기후)     */
								mpData.setMap("GWASE_ITEM"          ,  mp_O.getMap("TAX_GDS")  );                          /*과세대상            */
								mpData.setMap("BUGWA_TAB"           ,  mp_O.getMap("TAX_DESC")  );                         /*부과내역            */
								mpData.setMap("GOJI_DATE"   		,  mp_O.getMap("BUGWA_DT")  );                         /*고지자료 발생일자   */
								mpData.setMap("OUTO_ICHE_GB"        ,  "N"  );                                             /*자동이체등록여부    */
								mpData.setMap("SUNAB_BANK_CD"       ,  "0"  );                                             /*수납은행 점별 코드  */
								mpData.setMap("RECIP_DATE"          ,  "0"  );                                             /*납부일시			 */
								mpData.setMap("NAPGI_BA_GB"         ,  "B"  );                                             /*납기내후구분        */
								mpData.setMap("FILED3"          	,  " "  );                                             /*예비정보 FIELD 3    */		

								if(mfJunmunInfo.getMap("NAPGB").equals(1) || mfJunmunInfo.getMap("NAPGB").equals(3)) { //납기내또는 납기무관
									
									if(Long.getLong(mp_O.getMap("AMT").toString()) != Long.getLong(mfJunmunInfo.getMap("NAPBU_AMT").toString())) {
										//금액이 틀린경우 : 변경고지 U:변경
										mpData.setMap("PRCGB"          ,"U");    /*처리구분*/
									}
									
									amt += Long.parseLong(mp_O.getMap("AMT").toString());
									
								}else if(mfJunmunInfo.getMap("NAPGB").equals(2)) { //납기후 (일어날수 없음..: 결제원에서 납기내만 신청가능하다고 함)
									
									if(Long.getLong(mp_O.getMap("AFT_AMT").toString()) != Long.getLong(mfJunmunInfo.getMap("NAPBU_AMT").toString())) {
										//금액이 틀린경우 : 변경고지 U:변경
										mpData.setMap("PRCGB"          ,"U");    /*처리구분*/
									}
									
									amt += Long.parseLong(mp_O.getMap("AFT_AMT").toString());
								}
								
								sbData.append(new String(KFL_2550.getBuff(mpData)));
								
							}

						}
						
					}
					
					if(rec_cnt == alJunmunInfo.size() -1) {
						
						/*이때는 HEAD부과 TAILER 부만 만든다.*/
						mpHead.setMap("BUSINESSGUBUN"  ,"GR6695");                       /*업무구분         */
						mpHead.setMap("DATAGUBUN"      ,"11");                           /*데이터구분       */
						mpHead.setMap("SEQNO"          ,"0000000");                      /*일련번호         */
						mpHead.setMap("BANKCODE"       ,"032");                          /*(저축)은행코드   */
						mpHead.setMap("TOTALCOUNT"     , 0 );                            /*총 DATA RECORD 수*/
						mpHead.setMap("TRANSDATE"      ,mf.getMap("ACCTDT"));            /*수납일자         */
						mpHead.setMap("FILLER"         ," ");              

						/*Trailer 부*/
						mpTail.setMap("BUSINESSGUBUN"  ,"GR6695");                       /*업무구분         */
						mpTail.setMap("DATAGUBUN"      ,"33");                           /*데이터구분       */
						mpTail.setMap("SEQNO"          ,"9999999");                      /*일련번호         */
						mpTail.setMap("BANKCODE"       ,"032");                          /*(저축)은행코드   */
						mpTail.setMap("TOTALCOUNT"     , 0 );                            /*총 DATA RECORD 수*/
						mpTail.setMap("TOTALPAYMONEY"  , 0 );                            /*수납금액         */
						mpTail.setMap("FILLER"         ," ");                            /*FILLER           */	
						
						/*파일을 생성시킨다.*/
						if(mpHead.toString().length() > 0 || mpTail.toString().length() > 0){
							
							String filedata = "";
							
							mpHead.setMap("TOTALCOUNT"     ,inc); 
							mpTail.setMap("TOTALCOUNT"     ,inc);                            
							mpTail.setMap("TOTALPAYMONEY"  ,amt);                            
							
							
							KFL_2550.GR6695_Head_FieldList();  /*변경고지 Head 전문*/
							sbHead.append(new String(KFL_2550.getBuff(mpHead)));

							KFL_2550.GR6695_Tailer_FieldList();/*변경고지 Tail 전문*/
							sbTail.append(new String(KFL_2550.getBuff(mpTail)));
							
							filedata = sbHead.toString() + sbData.toString() + sbTail.toString();
							String sndFile = sndFilePath + "GR6695261500172" + mpHead.getMap("TRANSDATE").toString();
							
							log.debug("filedata.size = [" + filedata.length() + "]");
							log.debug("filedata = [" + filedata + "]");
							
							makeFile(filedata.getBytes(), sndFile, "new");
							
							/*파일이 만들어졌다면 결제원으로 전송한다.*/
							kf_rev_file_transefer(client, sndFile);
							
						}
						
					}
					
				}
				
			} else {
				
				/*세외수입 예약납부 헤드 및 테일러를 가져온다.*/			
				String filedata = "";
				
				Txdm2550FieldList KFL_2550 = new Txdm2550FieldList();
				
				StringBuffer sbHead = new StringBuffer();
				StringBuffer sbTail = new StringBuffer();
				
				MapForm mpHead = new MapForm();
				MapForm mpTail = new MapForm();
				
				/*이때는 HEAD부과 TAILER 부만 만든다.*/
				mpHead.setMap("BUSINESSGUBUN"  ,"GR6695");                       /*업무구분         */
				mpHead.setMap("DATAGUBUN"      ,"11");                           /*데이터구분       */
				mpHead.setMap("SEQNO"          ,"0000000");                      /*일련번호         */
				mpHead.setMap("BANKCODE"       ,"032");                          /*(저축)은행코드   */
				mpHead.setMap("TOTALCOUNT"     , 0 );                            /*총 DATA RECORD 수*/
				mpHead.setMap("TRANSDATE"      ,mf.getMap("ACCTDT"));            /*수납일자         */
				mpHead.setMap("FILLER"         ," ");              

				/*Trailer 부*/
				mpTail.setMap("BUSINESSGUBUN"  ,"GR6695");                       /*업무구분         */
				mpTail.setMap("DATAGUBUN"      ,"33");                           /*데이터구분       */
				mpTail.setMap("SEQNO"          ,"9999999");                      /*일련번호         */
				mpTail.setMap("BANKCODE"       ,"032");                          /*(저축)은행코드   */
				mpTail.setMap("TOTALCOUNT"     , 0 );                            /*총 DATA RECORD 수*/
				mpTail.setMap("TOTALPAYMONEY"  , 0 );                            /*수납금액         */
				mpTail.setMap("FILLER"         ," ");                            /*FILLER           */	
				
				KFL_2550.GR6695_Head_FieldList();  /*변경고지 Head 전문*/
				sbHead.append(new String(KFL_2550.getBuff(mpHead)));

				KFL_2550.GR6695_Tailer_FieldList();/*변경고지 Tail 전문*/
				sbTail.append(new String(KFL_2550.getBuff(mpTail)));
				
				filedata = sbHead.toString() + sbTail.toString();
				String sndFile = sndFilePath + "GR6695261500172" + mpHead.getMap("TRANSDATE").toString();
				
				log.debug("filedata.size = [" + filedata.length() + "]");
				log.debug("filedata = [" + filedata + "]");
				
				makeFile(filedata.getBytes(), sndFile, "new");
				
				/*파일이 만들어졌다면 결제원으로 전송한다.*/
				kf_rev_file_transefer(client, sndFile);

			}

		}catch (Exception e) {
			// TODO Auto-generated catch block
			 e.printStackTrace();
		}
	}

	/**
	 * 환경개선부담금 예약납부파일 전송
	 * @param client
	 * @param mf
	 */
	@SuppressWarnings("unused")
	private void res_env_improvement_file_transfer(Txbt2550 client, MapForm mf){
		
		ArrayList<MapForm> alJunmunInfo = null;
		
		try {
			
			CbUtil cUtil = CbUtil.getInstance();
			
			mf.setMap("TAX_GB" , "H");
			
			/*부과정보*/			
			alJunmunInfo = sqlService_cyber.queryForList("TXBT2550.SELECT_JUNMUN_LIST", mf);
			
			if (alJunmunInfo.size()  >  0)   {
				
				log.debug("========================================== ");
				log.debug("환경개선부담금예약납부통지 ");
				log.debug("========================================== ");
				
				Txdm2550FieldList KFL_2550 = new Txdm2550FieldList();
				
				StringBuffer sbHead = new StringBuffer();
				StringBuffer sbTail = new StringBuffer();
				StringBuffer sbData = new StringBuffer();
				
				MapForm mpHead = new MapForm();
				MapForm mpTail = new MapForm();
				MapForm mpData = new MapForm();
				
				int inc = 0;
				long amt = 0;
				
				for ( int rec_cnt = 0;  rec_cnt < alJunmunInfo.size();  rec_cnt++)   {
					
					MapForm mfJunmunInfo =  alJunmunInfo.get(rec_cnt);
					
					/*혹시나...because of testing */
					if (mfJunmunInfo == null  ||  mfJunmunInfo.isEmpty() )   {
						continue;
					}
					
					KFL_2550.GR6697_Data_FieldList();  /*변경고지 Data 전문*/
					
					long NAP_BFAMT = 0;
					long NAP_AFAMT = 0;
					long BONSE     = 0;
					long BONSE_ADD = 0;
					String napkiGubun    = "";
					String NP_BAF_GUBUN  = "";
					String JA_GOGI_GUBUN = "";
					String NapGn = "";

					//수납처리(납부) 되었다면...
					if (sqlService_cyber.getOneFieldInteger("TXBT2550.SELECT_ENV_KFPAY_CNT", mfJunmunInfo) > 0 ) {
						
						if(mfJunmunInfo.getMap("ACCTDT").equals(mf.getMap("ACCTDT"))) {
							
							inc++;
							
							MapForm mp_H = sqlService_cyber.queryForMap("TXBT2550.SELECT_ENV_KFPAY_LIST", mfJunmunInfo);
							
							napkiGubun = cUtil.getNapGubun((String)mp_H.getMap("DUE_DT"), (String)mp_H.getMap("DUE_F_DT"));
						    
						    if(napkiGubun.equals("B")){
						    	
						    	NapGn = "1";
						    	
						    	if(mp_H.getMap("DUE_DT").equals(mp_H.getMap("DUE_F_DT"))){
						    		
						    		NAP_BFAMT = ((BigDecimal)mp_H.getMap("ENV_AMT")).longValue()+((BigDecimal)mp_H.getMap("ADD_AMT")).longValue();   // 납기내금액
				                    NAP_AFAMT = ((BigDecimal)mp_H.getMap("ENV_AMT")).longValue()+((BigDecimal)mp_H.getMap("ADD_AMT")).longValue();   // 납기후금액

				                    BONSE         = NAP_BFAMT;   // 납기내금액
				                    BONSE_ADD     = NAP_AFAMT;   // 납기후금액

						    	} else {
						    		
						    		NAP_BFAMT = ((BigDecimal)mp_H.getMap("ENV_AMT")).longValue();   // 납기내금액
				                    NAP_AFAMT = ((BigDecimal)mp_H.getMap("ENV_AMT")).longValue()+((BigDecimal)mp_H.getMap("ADD_AMT")).longValue();  // 납기후금액

				                    BONSE         = NAP_BFAMT;   
				                    BONSE_ADD     = NAP_AFAMT;   

						    	}
						    	
						    } else if(napkiGubun.equals("A")) {
						    	
						    	NapGn = "2";
						    	
						    	NAP_BFAMT = ((BigDecimal)mp_H.getMap("ENV_AMT")).longValue();   // 납기내금액
			                    NAP_AFAMT = ((BigDecimal)mp_H.getMap("ENV_AMT")).longValue()+((BigDecimal)mp_H.getMap("ADD_AMT")).longValue();  // 납기후금액

			                    BONSE         = NAP_BFAMT;   
			                    BONSE_ADD     = NAP_AFAMT;   
						    	
						    }

						    mpData.setMap("BUSINESSGUBUN"       ,  mfJunmunInfo.getMap("BUSINESSGUBUN")  );            /*업무구분            */
							mpData.setMap("DATAGUBUN"           ,  mfJunmunInfo.getMap("DATAGUBUN")  );                /*데이터구분          */
							mpData.setMap("SEQNO"               ,  CbUtil.lPadString(String.valueOf(inc), 7, '0')  );  /*일련번호            */
							mpData.setMap("PUBGCODE"            ,  mfJunmunInfo.getMap("PUBGCODE")  );                 /*발행기관분류코드    */
							mpData.setMap("JIRONO"              ,  mfJunmunInfo.getMap("JIRONO")  );                   /*발행기관확인및 검증 */
							mpData.setMap("ETAX_NO"             ,  mp_H.getMap("EPAY_NO")  );                          /*전자납부번호              */
							mpData.setMap("JUMIN_NO"            ,  mp_H.getMap("REG_NO")  );                           /*주민(사업자,법인)등록번호 */
							mpData.setMap("SIDO"                ,  "26"  );                                            /*시도                      */
							mpData.setMap("GU_CODE"             ,  mp_H.getMap("SGG_COD")  );                          /*과세기관(시군구)          */
							mpData.setMap("CONFIRM_NO1"         ,  "1"  );                                             /*검증번호 1                */
							mpData.setMap("HCALVAL"             ,  mp_H.getMap("ACCT_COD")  );                         /*회계                      */
							mpData.setMap("GWA_MOK"             ,  mp_H.getMap("TAX_ITEM")  );                         /*과목/세목                 */
							mpData.setMap("TAX_YYMM"            ,  mp_H.getMap("TAX_YM")  );                           /*년도/기분                 */
							mpData.setMap("KIBUN"               ,  mp_H.getMap("TAX_DIV")  );                          /*구분                      */
							mpData.setMap("DONG_CODE"           ,  mp_H.getMap("HACD")  );                             /*행정동(읍면동)            */
							mpData.setMap("GWASE_NO"            ,  mp_H.getMap("TAX_SNO")  );                          /*관리 번호                 */
							mpData.setMap("CONFIRM_NO2"         ,  "2"  );                                             /*검증번호 2                */
							mpData.setMap("NAP_NAME"            ,  mp_H.getMap("REG_NM")  );                           /*납부자 성명               */
							mpData.setMap("NAP_BFAMT"           ,  NAP_BFAMT  );                                       /*납기내 금액               */
							mpData.setMap("NAP_AFAMT"           ,  NAP_AFAMT  );                                       /*납기후 금액               */
						    mpData.setMap("CONFIRM_NO3"         ,  "3"  );                                             /*검증번호 3                */
						    mpData.setMap("GWASE_RULE"          ,  "0"  );                                             /*과세 표준                 */
						    mpData.setMap("BONSE"               ,  BONSE  );                                           /*부담금                    */
						    mpData.setMap("BONSE_ADD"           ,  BONSE_ADD  );                                       /*부담금 가산금             */
						    mpData.setMap("DOSISE"              ,  mp_H.getMap("MI_AMT")  );                           /*미수 부담금               */
						    mpData.setMap("DOSISE_ADD"          ,  mp_H.getMap("ENV_MIADD_AMT")  );                    /*미수 부담금 가산금        */
						    mpData.setMap("GONGDONGSE"          ,  "0"  );                                             /*예비 정보 FIELD 1         */
						    mpData.setMap("GONGDONGSE_ADD"      ,  "0"  );                                             /*예비 정보 FIELD 2         */
						    mpData.setMap("EDUSE"               ,  "0"  );                                             /*예비 정보 FIELD 3         */
						    mpData.setMap("EDUSE_ADD"           ,  "0"  );                                             /*예비 정보 FIELD 4         */
						    mpData.setMap("NAP_BFDATE"          ,  mp_H.getMap("DUE_DT")  );                           /*납기일 (납기내)           */
						    mpData.setMap("NAP_AFDATE"          ,  mp_H.getMap("DUE_F_DT")  );                         /*납기일 (납기후)           */
						    mpData.setMap("CONFIRM_NO4"         ,  "4"  );                                             /*검증번호 4                */
						    mpData.setMap("FILLER1"             ,  "0"  );                                             /*필러                      */
						    mpData.setMap("CONFIRM_NO5"         ,  "5"  );                                             /*검증번호 5                */
						    mpData.setMap("GWASE_DESC"          ,  mp_H.getMap("MLGN_IF1")  );                         /*과세 사항                 */
						    mpData.setMap("GWASE_PUB_DESC"      ,  mp_H.getMap("MLGN_IF2")  );                         /*과세 표준 설명            */
						    mpData.setMap("GOJICR_DATE"         ,  mp_H.getMap("TAX_DT")  );                           /*고지자료 발생일자         */
						    mpData.setMap("JADONG_YN"           ,  "N"  );                                             /*자동이체 등록 여부        */
						    mpData.setMap("JIJUM_CODE"          ,  mp_H.getMap("BRC_NO")  );                           /*수납은행 점별 코드        */
						    mpData.setMap("NAPBU_DATE"          ,  mp_H.getMap("REG_DT")  );                           /*납부 일시                 */
						    mpData.setMap("NP_BAF_GUBUN"        ,  NapGn  );                                           /*납기 내후 구분            */
						    mpData.setMap("TAX_GOGI_GUBUN"      ,  " "  );                                             /*세금 종류 구분            */
						    mpData.setMap("JA_GOGI_GUBUN"       ,  " "  );                                             /*장표 고지 형태            */
						    mpData.setMap("PRCGB"               ,  "D"  );                                             /*처리구분                  */
						    mpData.setMap("RESERV2"             ,  " "  );                                             /*예비 정보 FIELD 5         */
						    
						    sbData.append(new String(KFL_2550.getBuff(mpData)));
							
						}
						

					} else {

						MapForm mp_H = sqlService_cyber.queryForMap("TXBT2550.SELECT_ENV_LEVY_LIST", mfJunmunInfo);
						
						if(mp_H.size() > 0) {
							
							if(mfJunmunInfo.getMap("ACCTDT").equals(mf.getMap("ACCTDT"))) {
								
								inc++;
								
								napkiGubun = cUtil.getNapGubun((String)mp_H.getMap("DUE_DT"), (String)mp_H.getMap("DUE_F_DT"));
							    
							    if(napkiGubun.equals("B")){
							    	
							    	NapGn = "1";
							    	
							    	if(mp_H.getMap("DUE_DT").equals(mp_H.getMap("DUE_F_DT"))){
							    		
							    		NAP_BFAMT = ((BigDecimal)mp_H.getMap("ENV_AMT")).longValue()+((BigDecimal)mp_H.getMap("ADD_AMT")).longValue();   // 납기내금액
					                    NAP_AFAMT = ((BigDecimal)mp_H.getMap("ENV_AMT")).longValue()+((BigDecimal)mp_H.getMap("ADD_AMT")).longValue();   // 납기후금액
					                    NP_BAF_GUBUN = napkiGubun;
					                    JA_GOGI_GUBUN = "4";
					                    BONSE         = NAP_BFAMT;   // 납기내금액
					                    BONSE_ADD     = NAP_AFAMT;   // 납기후금액

							    	} else {
							    		
							    		NAP_BFAMT = ((BigDecimal)mp_H.getMap("ENV_AMT")).longValue();   // 납기내금액
					                    NAP_AFAMT = ((BigDecimal)mp_H.getMap("ENV_AMT")).longValue()+((BigDecimal)mp_H.getMap("ADD_AMT")).longValue();  // 납기후금액
					                    NP_BAF_GUBUN = napkiGubun;
					                    JA_GOGI_GUBUN = "4";
					                    BONSE         = NAP_BFAMT;   
					                    BONSE_ADD     = NAP_AFAMT;   

							    	}
							    	
							    } else if(napkiGubun.equals("A")) {
							    	
							    	NapGn = "2";
							    	
							    	NAP_BFAMT = ((BigDecimal)mp_H.getMap("ENV_AMT")).longValue();   // 납기내금액
				                    NAP_AFAMT = ((BigDecimal)mp_H.getMap("ENV_AMT")).longValue()+((BigDecimal)mp_H.getMap("ADD_AMT")).longValue();  // 납기후금액
				                    NP_BAF_GUBUN = napkiGubun;
				                    JA_GOGI_GUBUN = "4";
				                    BONSE         = NAP_BFAMT;   
				                    BONSE_ADD     = NAP_AFAMT;   
							    	
							    }
							    
							    mpData.setMap("BUSINESSGUBUN"       ,  mfJunmunInfo.getMap("BUSINESSGUBUN")            );  /*업무구분            */
								mpData.setMap("DATAGUBUN"           ,  mfJunmunInfo.getMap("DATAGUBUN")                );  /*데이터구분          */
								mpData.setMap("SEQNO"               ,  CbUtil.lPadString(String.valueOf(inc), 7, '0')  );  /*일련번호            */
								mpData.setMap("PUBGCODE"            ,  mfJunmunInfo.getMap("PUBGCODE")                 );  /*발행기관분류코드    */
								mpData.setMap("JIRONO"              ,  mfJunmunInfo.getMap("JIRONO")                   );  /*발행기관확인및 검증 */
								mpData.setMap("ETAX_NO"             ,  mp_H.getMap("EPAY_NO")                          );  /*전자납부번호              */
								mpData.setMap("JUMIN_NO"            ,  mp_H.getMap("REG_NO")                           );  /*주민(사업자,법인)등록번호 */
								mpData.setMap("SIDO"                ,  "26"                                            );  /*시도                      */
								mpData.setMap("GU_CODE"             ,  mp_H.getMap("SGG_COD")                          );  /*과세기관(시군구)          */
								mpData.setMap("CONFIRM_NO1"         ,  "1"                                             );  /*검증번호 1                */
								mpData.setMap("HCALVAL"             ,  mp_H.getMap("ACCT_COD")                         );  /*회계                      */
								mpData.setMap("GWA_MOK"             ,  mp_H.getMap("TAX_ITEM")                         );  /*과목/세목                 */
								mpData.setMap("TAX_YYMM"            ,  mp_H.getMap("TAX_YM")                           );  /*년도/기분                 */
								mpData.setMap("KIBUN"               ,  mp_H.getMap("TAX_DIV")                          );  /*구분                      */
								mpData.setMap("DONG_CODE"           ,  mp_H.getMap("HACD")                             );  /*행정동(읍면동)            */
								mpData.setMap("GWASE_NO"            ,  mp_H.getMap("TAX_SNO")                          );  /*관리 번호                 */
								mpData.setMap("CONFIRM_NO2"         ,  "2"                                             );  /*검증번호 2                */
								mpData.setMap("NAP_NAME"            ,  mp_H.getMap("REG_NM")                           );  /*납부자 성명               */
								mpData.setMap("NAP_BFAMT"           ,  NAP_BFAMT                                       );  /*납기내 금액               */
								mpData.setMap("NAP_AFAMT"           ,  NAP_AFAMT                                       );  /*납기후 금액               */
							    mpData.setMap("CONFIRM_NO3"         ,  "3"                                             );  /*검증번호 3                */
							    mpData.setMap("GWASE_RULE"          ,  "0"                                             );  /*과세 표준                 */
							    mpData.setMap("BONSE"               ,  BONSE                                           );  /*부담금                    */
							    mpData.setMap("BONSE_ADD"           ,  BONSE_ADD  );                                       /*부담금 가산금             */
							    mpData.setMap("DOSISE"              ,  mp_H.getMap("MI_AMT")  );                           /*미수 부담금               */
							    mpData.setMap("DOSISE_ADD"          ,  mp_H.getMap("ENV_MIADD_AMT")  );                    /*미수 부담금 가산금        */
							    mpData.setMap("GONGDONGSE"          ,  "0"  );                                             /*예비 정보 FIELD 1         */
							    mpData.setMap("GONGDONGSE_ADD"      ,  "0"  );                                             /*예비 정보 FIELD 2         */
							    mpData.setMap("EDUSE"               ,  "0"  );                                             /*예비 정보 FIELD 3         */
							    mpData.setMap("EDUSE_ADD"           ,  "0"  );                                             /*예비 정보 FIELD 4         */
							    mpData.setMap("NAP_BFDATE"          ,  mp_H.getMap("DUE_DT")  );                           /*납기일 (납기내)           */
							    mpData.setMap("NAP_AFDATE"          ,  mp_H.getMap("DUE_F_DT")  );                         /*납기일 (납기후)           */
							    mpData.setMap("CONFIRM_NO4"         ,  "4"  );                                             /*검증번호 4                */
							    mpData.setMap("FILLER1"             ,  "0"  );                                             /*필러                      */
							    mpData.setMap("CONFIRM_NO5"         ,  "5"  );                                             /*검증번호 5                */
							    mpData.setMap("GWASE_DESC"          ,  mp_H.getMap("MLGN_IF1")  );                         /*과세 사항                 */
							    mpData.setMap("GWASE_PUB_DESC"      ,  mp_H.getMap("MLGN_IF2")  );                         /*과세 표준 설명            */
							    mpData.setMap("GOJICR_DATE"         ,  mp_H.getMap("TAX_DT")  );                           /*고지자료 발생일자         */
							    mpData.setMap("JADONG_YN"           ,  "N"  );                                             /*자동이체 등록 여부        */
							    mpData.setMap("JIJUM_CODE"          ,  "0"  );                                             /*수납은행 점별 코드        */
							    mpData.setMap("NAPBU_DATE"          ,  "0"  );                                             /*납부 일시                 */
							    mpData.setMap("NP_BAF_GUBUN"        ,  NapGn  );                                           /*납기 내후 구분            */
							    mpData.setMap("TAX_GOGI_GUBUN"      ,  " "  );                                             /*세금 종류 구분            */
							    mpData.setMap("JA_GOGI_GUBUN"       ,  " "  );                                             /*장표 고지 형태            */
							    mpData.setMap("PRCGB"               ,  "D"  );                                             /*처리구분                  */
							    mpData.setMap("RESERV2"             ,  " "  );                                             /*예비 정보 FIELD 5         */
							    
							    if(NapGn.equals("1"))  { //납기내또는 납기무관
									
									if(NAP_BFAMT != Long.getLong(mfJunmunInfo.getMap("NAPBU_AMT").toString())) {
										//금액이 틀린경우 : 변경고지 U:변경
										mpData.setMap("PRCGB"          ,"U");    /*처리구분*/
									}
									
									amt += NAP_BFAMT;
									
								}else if(NapGn.equals("2")) { //납기후
									
									if(NAP_AFAMT != Long.getLong(mfJunmunInfo.getMap("NAPBU_AMT").toString())) {
										//금액이 틀린경우 : 변경고지 U:변경
										mpData.setMap("PRCGB"          ,"U");    /*처리구분*/
									}
									
									amt += NAP_AFAMT;
								}
								
								sbData.append(new String(KFL_2550.getBuff(mpData)));
								
							}
							
						}
					
					}
					
					if(rec_cnt == alJunmunInfo.size() -1) {
						
						/*이때는 HEAD부과 TAILER 부만 만든다.*/
						mpHead.setMap("BUSINESSGUBUN"  ,"GR6697");                       /*업무구분         */
						mpHead.setMap("DATAGUBUN"      ,"11");                           /*데이터구분       */
						mpHead.setMap("SEQNO"          ,"0000000");                      /*일련번호         */
						mpHead.setMap("BANKCODE"       ,"000");                          /*(저축)은행코드   */
						mpHead.setMap("TOTALCOUNT"     , 0 );                            /*총 DATA RECORD 수*/
						mpHead.setMap("TRANSDATE"      ,mf.getMap("ACCTDT"));            /*수납일자         */
						mpHead.setMap("FILLER"         ," ");              

						/*Trailer 부*/
						mpTail.setMap("BUSINESSGUBUN"  ,"GR6697");                       /*업무구분         */
						mpTail.setMap("DATAGUBUN"      ,"33");                           /*데이터구분       */
						mpTail.setMap("SEQNO"          ,"9999999");                      /*일련번호         */
						mpTail.setMap("BANKCODE"       ,"000");                          /*(저축)은행코드   */
						mpTail.setMap("TOTALCOUNT"     , 0 );                            /*총 DATA RECORD 수*/
						mpTail.setMap("TOTALPAYMONEY"  , 0 );                            /*수납금액         */
						mpTail.setMap("FILLER"         ," ");                            /*FILLER           */	
						
						/*파일을 생성시킨다.*/
						if(mpHead.toString().length() > 0 || mpTail.toString().length() > 0){
							
							String filedata = "";
							
							mpHead.setMap("TOTALCOUNT"     ,inc); 
							mpTail.setMap("TOTALCOUNT"     ,inc);                            
							mpTail.setMap("TOTALPAYMONEY"  ,amt);                            
							
							KFL_2550.GR6697_Head_FieldList();  /*변경고지 Head 전문*/
							sbHead.append(new String(KFL_2550.getBuff(mpHead)));

							KFL_2550.GR6697_Tailer_FieldList();/*변경고지 Tail 전문*/
							sbTail.append(new String(KFL_2550.getBuff(mpTail)));
							
							filedata = sbHead.toString() + sbData.toString() + sbTail.toString();
							String sndFile = sndFilePath + "GR6697261002641" + mpHead.getMap("ACCTDT").toString();
							
							log.debug("filedata.size = [" + filedata.length() + "]");
							log.debug("filedata = [" + filedata + "]");
							
							makeFile(filedata.getBytes(), sndFile, "new");
							
							/*파일이 만들어졌다면 결제원으로 전송한다.*/
							kf_rev_file_transefer(client, sndFile);
							
						}
						
					}
					
				}
				
			} else {
				
				/*환경개선부담금 예약납부 헤드 및 테일러를 가져온다.*/			
				alJunmunInfo = sqlService_cyber.queryForList("TXBT2550.SELECT_JUNMUN_BASIC", mf);
				
				String filedata = "";
				
				Txdm2550FieldList KFL_2550 = new Txdm2550FieldList();
				
				StringBuffer sbHead = new StringBuffer();
				StringBuffer sbTail = new StringBuffer();
				
				MapForm mpHead = new MapForm();
				MapForm mpTail = new MapForm();
				
				/*이때는 HEAD부과 TAILER 부만 만든다.*/
				mpHead.setMap("BUSINESSGUBUN"  ,"GR6697");                       /*업무구분         */
				mpHead.setMap("DATAGUBUN"      ,"11");                           /*데이터구분       */
				mpHead.setMap("SEQNO"          ,"0000000");                      /*일련번호         */
				mpHead.setMap("BANKCODE"       ,"000");                          /*(저축)은행코드   */
				mpHead.setMap("TOTALCOUNT"     , 0 );                            /*총 DATA RECORD 수*/
				mpHead.setMap("TRANSDATE"      ,mf.getMap("ACCTDT"));            /*수납일자         */
				mpHead.setMap("FILLER"         ," ");              

				/*Trailer 부*/
				mpTail.setMap("BUSINESSGUBUN"  ,"GR6697");                       /*업무구분         */
				mpTail.setMap("DATAGUBUN"      ,"33");                           /*데이터구분       */
				mpTail.setMap("SEQNO"          ,"9999999");                      /*일련번호         */
				mpTail.setMap("BANKCODE"       ,"000");                          /*(저축)은행코드   */
				mpTail.setMap("TOTALCOUNT"     , 0 );                            /*총 DATA RECORD 수*/
				mpTail.setMap("TOTALPAYMONEY"  , 0 );                            /*수납금액         */
				mpTail.setMap("FILLER"         ," ");                            /*FILLER           */	
				
				KFL_2550.GR6697_Head_FieldList();  /*변경고지 Head 전문*/
				sbHead.append(new String(KFL_2550.getBuff(mpHead)));

				KFL_2550.GR6697_Tailer_FieldList();/*변경고지 Tail 전문*/
				sbTail.append(new String(KFL_2550.getBuff(mpTail)));
				
				filedata = sbHead.toString() + sbTail.toString();
				String sndFile = sndFilePath + "GR6697261002641" + mpHead.getMap("TRANSDATE").toString();
				
				log.debug("filedata.size = [" + filedata.length() + "]");
				log.debug("filedata = [" + filedata + "]");
				
				makeFile(filedata.getBytes(), sndFile, "new");
				
				/*파일이 만들어졌다면 결제원으로 전송한다.*/
				kf_rev_file_transefer(client, sndFile);
				
			}
			
			
		}catch (Exception e) {
			// TODO Auto-generated catch block
			 e.printStackTrace();
		}
		
	}
	
	/**
	 * 결제원에서 수신한 파일을 읽고 파싱한다...
	 * */
	@SuppressWarnings("unused")
	private ArrayList<MapForm> setFileReader(MapForm mf) {
		
    	String outFilePath = mf.getMap("AbsolutePath").toString();
    	
    	String recvFileNm = "";
    	
    	File readFile = null;
    	
    	int FileLen = 0;
    	
    	Txdm2550FieldList kf2550_Field = new Txdm2550FieldList();
    	
    	MapForm parseMap = new MapForm();
    	
    	ArrayList<MapForm> alRetrun = new ArrayList<MapForm>();
    	
    	try {
			
    		recvFileNm = outFilePath;
    		
    		readFile = new File(recvFileNm);
    		
    		int i = 0;
    		
    		if(readFile.exists()){
    	
    			log.debug("file_name = [" + readFile.getName() + "] file_size = [" + readFile.length() + "]");
    			
    			BufferedReader br = new BufferedReader(new FileReader(readFile));
    			
    			StringBuffer  sbLine = new StringBuffer();
    			
    			if(mf.getMap("FormName").equals("GR6675")) { /*상하수도인경우*/
    				FileLen = 300;
    			} else {
    				FileLen = 230;
    			}
    			
    			//포멧의 길이가 230/300 Byte 이므로 230/300씩 짤라서 읽어낸다.
    			char[] readLine = new char[FileLen];
    			
    			if(mf.getMap("FormName").equals("GR6653") || 
    			   mf.getMap("FormName").equals("GR6675") || 
    			   mf.getMap("FormName").equals("GR6694") || 
    			   mf.getMap("FormName").equals("GR6696")) { /*지방세*/
    				
    				while (true){
	    				
        				if (br.read(readLine) < 0){
        					break;
        				}
        				
        				sbLine.append(new String(readLine));
        				        				
        			    log.debug("i = ["+ i +"]" + sbLine.toString());	
        				         			    
        			    if (sbLine.substring(6, 8).equals("11")) {
        			    	
        			    	if(mf.getMap("FormName").equals("GR6653")) {
        			    		kf2550_Field.GR6653_Head_FieldList(); 
        			    	} else if(mf.getMap("FormName").equals("GR6675")) {
        			    		kf2550_Field.GR6675_Head_FieldList(); 
        			    	} else if(mf.getMap("FormName").equals("GR6694")) {
        			    		kf2550_Field.GR6694_Head_FieldList(); 
        			    	} else if(mf.getMap("FormName").equals("GR6696")) {
        			    		kf2550_Field.GR6696_Head_FieldList(); 
        			    	}

        			    }else if (sbLine.substring(6, 8).equals("22")) {
        			    	
        			    	if(mf.getMap("FormName").equals("GR6653")) {
        			    		kf2550_Field.GR6653_Data_FieldList(); 
        			    	} else if(mf.getMap("FormName").equals("GR6675")) {
        			    		kf2550_Field.GR6675_Data_FieldList(); 
        			    	} else if(mf.getMap("FormName").equals("GR6694")) {
        			    		kf2550_Field.GR6694_Data_FieldList(); 
        			    	} else if(mf.getMap("FormName").equals("GR6696")) {
        			    		kf2550_Field.GR6696_Data_FieldList(); 
        			    	}
        			    	
        			    }else if (sbLine.substring(6, 8).equals("33")) {
        			    	
        			    	if(mf.getMap("FormName").equals("GR6653")) {
        			    		kf2550_Field.GR6653_Tailer_FieldList(); 
        			    	} else if(mf.getMap("FormName").equals("GR6675")) {
        			    		kf2550_Field.GR6675_Tailer_FieldList(); 
        			    	} else if(mf.getMap("FormName").equals("GR6694")) {
        			    		kf2550_Field.GR6694_Tailer_FieldList(); 
        			    	} else if(mf.getMap("FormName").equals("GR6696")) {
        			    		kf2550_Field.GR6696_Tailer_FieldList(); 
        			    	}
        			    }
        			    	
        			    parseMap = kf2550_Field.parseBuff(sbLine.toString().getBytes());
        			    parseMap.setMap("DATAGB", sbLine.substring(6, 8)); /*파일 데이터 구분*/
    			    	
        			    alRetrun.add(parseMap);
        			    
    			    	log.debug("Parse = " + parseMap.getMaps());
    			    	
        			    sbLine.delete(0, FileLen);
        			    
        				i++;
        			}
    				
    			}
    			
    			br.close();
    			
    		}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		return alRetrun;
    }
	
	/**
	 * 결제원으로 예약파일을 전송한다....
	 * @return
	 */
	private boolean kf_rev_file_transefer(Txbt2550 client, String kf_snd_file_nm) {
		
		
		log.info("=====================kf_rev_file_transefer()시작=======================");
		
		int connPort = 0;
		long filesz = 0;
		String connIP = "";
		boolean isConnect = false;
		
		int series_cnt = 0;  /*연속으로 전송하는 경우 : 8번마다 검증요청*/
		int timeout    = 30;
		
		String MsgHead = "1052F   ";
		
		byte[] resv_msg = new byte[1056];
		
		Txdm2550FieldList KFL_2550 = null;
		
		MapForm cmMap;
		
		try {
		
			outFilePath  = "/app/cyber_ap/recvdata/";
			backFilePath = "/app/cyber_ap/backup/";

			connPort =  Utils.getResourceInt("ApplicationResource", "kftc.recv_res.port");
			connIP   =  Utils.getResource("ApplicationResource", "kftc.recv_res.ip");
			
			try {
				
				client.Connect(connIP, connPort);       /*결제원*/
				
				isConnect = true;
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				
				isConnect = false;
			}

			if(!isConnect) {
				
				log.info("Soket 연결 오류 :: IP[" + connIP + "] PORT[" + connPort + "]");
				
			} else {
				
				/*전송준비 전문 (P_INFO) 전문생성 -> 전송할 파일정보 전송*/
				File outFile = new File(kf_snd_file_nm);

				
				if(outFile.exists()) {
					filesz = outFile.length();
					
					log.debug("파일존재 : " + outFile.getName());
					log.debug("파일길이 : " + filesz);
					
				} else {
					throw new Exception(outFile.getName() + " 존재하지 않습니다. 파일을 확인하세요.");
				}
				
				byte[] snd_type     = "I".getBytes();
				byte[] snd_flag1    = "E".getBytes();
				byte[] snd_flag2    = "u".getBytes();
				byte[] snd_flag3    = CbUtil.nullByte(1);
				byte[] snd_filesize = CbUtil.setOffset(filesz);
				byte[] snd_userid   = CbUtil.nullByte(64);
				byte[] snd_pass     = CbUtil.nullByte(64);
				byte[] snd_filenm   = CbUtil.rPadByte(outFile.getName().getBytes(), 128);
				byte[] snd_reser    = CbUtil.nullByte(768);
				
				byte[] bMd5 = new byte[1032];
				
				System.arraycopy(snd_type,      0, bMd5, 0, snd_type.length);
				System.arraycopy(snd_flag1,     0, bMd5, snd_type.length, snd_flag1.length);
				System.arraycopy(snd_flag2,     0, bMd5, snd_type.length + snd_flag1.length, snd_flag2.length);
				System.arraycopy(snd_flag3,     0, bMd5, snd_type.length + snd_flag1.length + snd_flag2.length, snd_flag3.length);
				System.arraycopy(snd_filesize,  0, bMd5, snd_type.length + snd_flag1.length + snd_flag2.length + snd_flag3.length, snd_filesize.length);
				System.arraycopy(snd_userid,    0, bMd5, snd_type.length + snd_flag1.length + snd_flag2.length + snd_flag3.length + snd_filesize.length, snd_userid.length);
				System.arraycopy(snd_pass,      0, bMd5, snd_type.length + snd_flag1.length + snd_flag2.length + snd_flag3.length + snd_filesize.length + snd_userid.length, snd_pass.length);
				System.arraycopy(snd_filenm,    0, bMd5, snd_type.length + snd_flag1.length + snd_flag2.length + snd_flag3.length + snd_filesize.length + snd_userid.length + snd_pass.length, snd_filenm.length);
				System.arraycopy(snd_reser,     0, bMd5, snd_type.length + snd_flag1.length + snd_flag2.length + snd_flag3.length + snd_filesize.length + snd_userid.length + snd_pass.length + snd_filenm.length, snd_reser.length);
				
				byte[] bMd5_Incode = new byte[16];
				System.arraycopy(CbUtil.Md5Sig(bMd5), 0, bMd5_Incode,0, 16);
				
				System.arraycopy(MsgHead.getBytes(),  0, resv_msg, 0, MsgHead.length());
				System.arraycopy(bMd5,                0, resv_msg, MsgHead.length(), bMd5.length);
				System.arraycopy(bMd5_Incode,         0, resv_msg, MsgHead.length() + bMd5.length, 16);
				
				/*결제원으로 전문(파일정보)을 전송한다...*/
				log.debug("파일 정보 송신...");
				client.sendData(resv_msg);
				
				/*전송결과 수신을 기다리는다..30초까지...*/
				byte[] recv  = client.recv(timeout);
				
				log.debug("=========================================");
				log.debug("파일 정보 송신에 대한 응답수신...");
				log.debug("=========================================");
				
				/* 수신전문분석 : 초기화 */
				KFL_2550 = new Txdm2550FieldList();
				
				cmMap = KFL_2550.parseBuff(recv);         /*헤드와 앞 4byte만 파싱...*/
				
				log.debug("cmMap = [" + cmMap.getMaps() + "]");
			
				if(cmMap.getMap("BLK_TYPE").equals("P")) { /*P_POS 전문*/
				
					long p_ins = 0;
					
					do{
		
						/*다시 수신되는 경우 재 파싱...*/
						cmMap = KFL_2550.parseBuff(recv);  
						
						if(series_cnt > 0) {
							log.debug("=========================================");
							log.debug("재요청인 경우 [" + series_cnt + "]");
							log.debug("cmMap = [" + cmMap.getMaps() + "]");
							log.debug("=========================================");
						}
						
						/*P인경우 데이터를 전송한다...*/
						byte[] offset  = new byte[4];
						
						byte[] bContent = new byte[1024];   /*한번전송할 수 있는 최대 사이즈는 1024*/
						
						System.arraycopy(recv, 12, offset, 0, offset.length);
						
						long lOffset = CbUtil.transLength(offset, 4);
						
						log.info("수신된 Offset = [" + lOffset + "]");
						
						StringBuffer sbLine = new StringBuffer();
						
						String s_yn = "";
						
						if(filesz > 1024) s_yn = "G"; else s_yn = "E";
						
						if(series_cnt > 0 && series_cnt % 8 == 0) {
							/*8개의 패킷을 전송한 경우 전송안정성을 위하여 ACK를 요구하는 E Mode 를 전송함...*/
							s_yn = "E";
							series_cnt = 0;
						}
						
						log.info("s_yn = [" + s_yn + "]");
						
						/*주의) 한글이 있으므로 읽을때 무조건 바이너리로 읽는다...
						 *      즉 BufferedReader() string 및 char 로 읽는 것은 사용안한다. */
						
						/*파일을 읽어서 파일전송전문을 생성시킨다.*/
						FileInputStream fs = new FileInputStream(kf_snd_file_nm);
						/*========================================================*/
						/*파일을 읽을 경우 offset 길이만큼 skip 하고 길이만큼 읽어들임.*/
						fs.skip(lOffset);
				
						int readLen = fs.read(bContent);
						/*========================================================*/
						log.info("READ SIZE = [" +readLen + "]");
						
						fs.close();

						sbLine.append(new String(bContent));

	                    log.debug("읽은파일의 내용 = [" + sbLine.toString() + "]");
	                    
	                    long offset_infile = lOffset;
	                    long prt_filesize = (long)((int)(filesz - lOffset) > 1024 ? 1024 : (int)(filesz - lOffset));
	                    
	                    log.debug("파일내 offset = [" + offset_infile + "]");
	                    log.debug("파일 block    = [" + prt_filesize + "]");
	                    
						byte[] f_type     = "D".getBytes();
						byte[] f_flag1    = s_yn.getBytes();
						byte[] f_flag2    = CbUtil.setMsgLen(prt_filesize);
						byte[] f_offset   = CbUtil.setOffset(offset_infile);
						byte[] f_data     = CbUtil.rPadByte(bContent, 1024);
						
						byte[] f_bMd5 = new byte[1032];
						
						System.arraycopy(f_type,       0, f_bMd5, 0, f_type.length);
						System.arraycopy(f_flag1,      0, f_bMd5, f_type.length, f_flag1.length);
						System.arraycopy(f_flag2,      0, f_bMd5, f_type.length + f_flag1.length, f_flag2.length);
						System.arraycopy(f_offset,     0, f_bMd5, f_type.length + f_flag1.length + f_flag2.length, f_offset.length);
						System.arraycopy(f_data,       0, f_bMd5, f_type.length + f_flag1.length + f_flag2.length + f_offset.length, f_data.length);
						
						byte[] f_bMd5_Incode = new byte[16];
						System.arraycopy(CbUtil.Md5Sig(f_bMd5), 0, f_bMd5_Incode,0, 16);
						
						resv_msg = new byte[1056];
						
						System.arraycopy(MsgHead.getBytes(),    0, resv_msg, 0, MsgHead.length());
						System.arraycopy(f_bMd5,                0, resv_msg, MsgHead.length(), f_bMd5.length);
						System.arraycopy(f_bMd5_Incode,         0, resv_msg, MsgHead.length() + f_bMd5.length, 16);
						
						/*결제원으로 전문(파일정보)을 전송한다...*/
						client.sendData(resv_msg);
						
						if(s_yn.equals("G")) {
							
							p_ins += prt_filesize;
							
							System.arraycopy(CbUtil.setOffset(p_ins), 0, recv, 12, 4);
							
							if(filesz == p_ins) {
								//정상적으로 파일을 보냈다고 확인함...
								//EOF를 전송한다...
								bMd5 = new byte[1032];
								
								System.arraycopy("X".getBytes(),        0, bMd5, 0, 1);
								System.arraycopy(CbUtil.nullByte(1),    0, bMd5, 1, 1);
								System.arraycopy(CbUtil.nullByte(1),    0, bMd5, 2, 1);
								System.arraycopy(CbUtil.nullByte(1029), 0, bMd5, 3, 1029);
								
								bMd5_Incode = new byte[16];
								
								System.arraycopy(CbUtil.Md5Sig(bMd5), 0, bMd5_Incode,0, 16);
							
								resv_msg = new byte[1056];
								
								System.arraycopy(MsgHead.getBytes(),    0, resv_msg, 0, MsgHead.length());
								System.arraycopy(bMd5,                  0, resv_msg, MsgHead.length(), bMd5.length);
								System.arraycopy(bMd5_Incode        ,   0, resv_msg, MsgHead.length() + bMd5.length, 16);
								
								/*파일전송 종료에 대한 메세지 전송*/
								client.sendData(resv_msg);
								
								log.info("파일송신 끝[" + kf_snd_file_nm + "]");
								
								break;
	
							} else {
								log.info("파일송신 재요청 :: [" + p_ins + "]");
							}

						} else {
							
							/*전송결과 수신을 기다린다...30초까지...*/
							recv  = client.recv(timeout);
							
							/*전송에 후 수신전문분석*/
							cmMap = KFL_2550.parseBuff(recv);         /*헤드와 앞 4byte만 파싱...*/
							
							log.debug("cmMap = " + cmMap.getMaps());
						}
						
						/*파일을 주고 받는경우 파일의 사이즈가 1024보다 큰 경우는 주고받고를 계속해야 함....*/
						
						if(cmMap.getMap("BLK_TYPE").equals("P")) {
							
							/*원래는 수신받은 전문을 MD5 검증해야 하는데 일단 귀찮아서 못하겠음...이단은 누가 알아서 하도록...*/
							
							/*결재원에서 남은 파일을 다시 요청한 경우 이므로 : 계속해서 파일을 전송한다...*/
							System.arraycopy(recv, 12, offset, 0, offset.length);
							lOffset = CbUtil.transLength(offset, 4);
							
							if(filesz == lOffset) {
								//정상적으로 파일을 보냈다고 확인함...
								//EOF를 전송한다...
								bMd5 = new byte[1032];
								
								System.arraycopy("X".getBytes(),        0, bMd5, 0, 1);
								System.arraycopy(CbUtil.nullByte(1),    0, bMd5, 1, 1);
								System.arraycopy(CbUtil.nullByte(1),    0, bMd5, 2, 1);
								System.arraycopy(CbUtil.nullByte(1029), 0, bMd5, 3, 1029);
								
								bMd5_Incode = new byte[16];
								
								System.arraycopy(CbUtil.Md5Sig(bMd5), 0, bMd5_Incode,0, 16);
							
								resv_msg = new byte[1056];
								
								System.arraycopy(MsgHead.getBytes(),    0, resv_msg, 0, MsgHead.length());
								System.arraycopy(bMd5,                  0, resv_msg, MsgHead.length(), bMd5.length);
								System.arraycopy(bMd5_Incode        ,   0, resv_msg, MsgHead.length() + bMd5.length, 16);
								
								/*파일전송 종료에 대한 메세지 전송*/
								client.sendData(resv_msg);
								
								log.info("파일송신 끝[" + kf_snd_file_nm + "]");
								
								break;
								
								
							} else {
								log.info("파일송신 재요청 :: [" + lOffset + "]");
							}


						} else if(cmMap.getMap("BLK_TYPE").equals("X")) {
							
							/*파일송신에 대한 수신완료 메세지를 수신하였으므로 전송종료*/

							bMd5 = new byte[1032];
							
							System.arraycopy("A".getBytes(),        0, bMd5, 0, 1);
							System.arraycopy(CbUtil.nullByte(1),    0, bMd5, 1, 1);
							System.arraycopy("X".getBytes(),        0, bMd5, 2, 1);
							System.arraycopy(CbUtil.nullByte(1029), 0, bMd5, 3, 1029);
							
							bMd5_Incode = new byte[16];
							
							System.arraycopy(CbUtil.Md5Sig(bMd5), 0, bMd5_Incode,0, 16);
						
							resv_msg = new byte[1056];
							
							System.arraycopy(MsgHead.getBytes(),    0, resv_msg, 0, MsgHead.length());
							System.arraycopy(bMd5,                  0, resv_msg, MsgHead.length(), bMd5.length);
							System.arraycopy(bMd5_Incode        ,   0, resv_msg, MsgHead.length() + bMd5.length, 16);
							
							/*파일전송 종료에 대한 메세지 전송*/
							client.sendData(resv_msg);
							
							log.info("파일송신 완료[" + kf_snd_file_nm + "]");
							
							break;
						} else if(cmMap.getMap("BLK_TYPE").equals("C")) {
							
							
							KFL_2550.Msg_Can_FieldList();
							cmMap = KFL_2550.parseBuff(recv);  
							
							log.info(cmMap);
							
							break;
						}

						series_cnt ++;
						
					}while(true);
					
					
					
				} else if(cmMap.getMap("BLK_TYPE").equals("X")) {
					
					log.info("수신정보 = " + cmMap.getMap("BLK_TYPE") + " : EOF");
					
				} else if(cmMap.getMap("BLK_TYPE").equals("C")) {
					
					log.info("수신정보 = " + cmMap.getMap("BLK_TYPE") + " : CAN");
				}
		
			}
			
			if(isConnect) client.Disconnect();

			/*정상적으로 전송한 경우*/
			//CbUtil.copyFile(kf_snd_file_nm, kf_snd_file_nm);
			
			log.info("=====================kf_rev_file_transefer() 끝=======================");
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
			try {
				if(isConnect) client.Disconnect();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
				
		}

		return true;
	}
	
	
	/*============================================
	 * FILE 생성 및 읽기
	 *============================================
	 * */
	/**
	 * 파일생성 여부를 체크하고 파일이 존재하면 Append
	 * 그렇지 않으면 파일을 생성시킨다.
	 */
	private RandomAccessFile getCurrentFile(String mkFileName, String file_attrib) throws Exception	{
		
		RandomAccessFile raFile = null;
		
		File file = new File(mkFileName);

		if(file.exists() && file_attrib.equals("append")) {
			if(raFile == null) {
				raFile = new RandomAccessFile(file, "rw");
			}
			raFile.seek(raFile.length());
		} else {
			if(raFile != null) {
				raFile.close();
			}
			
			if(file.exists()) {
				file.delete();
			}

			raFile = new RandomAccessFile(file, "rw");
		}

		return raFile;
	
    }
	
	/**
	 * 전문으로 부터 파일데이터를 파일에 기록한다.
	 * @param msg
	 * @param mkFileName
	 */
	private synchronized void makeFile(byte[] msg, String mkFileName, String file_attrib) 
	{
		try	{
		
			RandomAccessFile rafile = getCurrentFile(mkFileName, file_attrib);
			rafile.write(msg);
			rafile.close();
			
		} catch (Exception e) {
			log.error("파일정보를 기록하는데 에러가 발생했습니다");
			e.printStackTrace();
		}

	}
	
	
	/*배치상태 등록*/
	public void regist_state_batch(String Gubun) {
		// TODO Auto-generated method stub
		
		MapForm sysMap = new MapForm();
		
		try {
			
			sysMap.setMap("SGG_COD"      , "000");                                /*구청코드 : 부산시(626), 사이버세청(000)*/
			sysMap.setMap("PROCESS_ID"   , "2555");                               /*프로세스 ID*/
			sysMap.setMap("PROCESS_NM"   , "예약납부 파일전송");                  /*프로세스 명*/
			sysMap.setMap("THREAD_NM"    , "thr_2555");                           /*쓰레드 명*/
			sysMap.setMap("BIGO"         , CbUtil.getServerIp());                 /*비고 : 실행서버IP를 셋팅한다. */
			sysMap.setMap("MANE_STAT"    , "1");                                  /*기동상태*/
			sysMap.setMap("MANAGE_CD"    , "2");                                  /*관리구분 1 : 데몬 2: 배치*/
			sysMap.setMap("STT_DT"       , CbUtil.getCurrent("yyyyMMddHHmmss"));  /*시작일시 */
			sysMap.setMap("END_DT"       , "");                                   /*종료일시*/
			sysMap.setMap("REG_DTM"      , CbUtil.getCurrent("yyyyMMddHHmmss"));  /*등록일시 : 처음한번만...*/
			sysMap.setMap("LAST_DTM"     , CbUtil.getCurrent("yyyyMMddHHmmss"));  /*최종수정일시*/
			sysMap.setMap("LAST_TIMEMIL" , System.currentTimeMillis());
			
			/**
			 * 데몬상태정보 등록
			 * TEST의 경우만 막음
			 */
			
			if(Gubun.equals("S")) {
				
				try{
					if(sqlService_cyber.queryForUpdate("CODMBASE.CODMSYSTUpdate", sysMap) < 1) {
						sqlService_cyber.queryForInsert("CODMBASE.CODMSYSTInsert", sysMap);
					}
				}catch (Exception e) {
					e.printStackTrace();
				}
				
			} else if(Gubun.equals("E")){
				
				sysMap.setMap("END_DT"        , CbUtil.getCurrent("yyyyMMddHHmmss"));  /*종료일시*/
				sysMap.setMap("LAST_DTM"      , CbUtil.getCurrent("yyyyMMddHHmmss"));
				sysMap.setMap("LAST_TIMEMIL"  , System.currentTimeMillis());
				
				/*서버 데몬상태를 업데이트 한다...*/
				sqlService_cyber.queryForUpdate("CODMBASE.CODMSYSTUpdate", sysMap);
			}
				
			Thread.sleep(1000);
			
		
		} catch (Exception e) {
			
			e.printStackTrace();
			
		}
		
		log.info("================================================");
		log.info("== " + Thread.currentThread().getName() + " PROCESS Terminated!!");
		log.info("================================================");
	}	
	
}
