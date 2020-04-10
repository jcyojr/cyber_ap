/**
 * Sample TEST
 */
package com.uc.bs.cyber.daemon.txdm2512;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.uc.core.MapForm;
import com.uc.egtob.net.ClientMessageService;
import com.uc.bs.cyber.CbUtil;

import com.uc.bs.cyber.service.bs531001.Bs531001FieldList;
import com.uc.bs.cyber.service.bs531002.Bs531002FieldList;


/**
 * @author 프리비
 *
 */
public class Txdm2512CSample  extends ClientMessageService implements Runnable {

	/**
	 * 
	 * @throws IOException
	 * @throws Exception
	 */

	private int  myId = 0; 
	
	protected Log log = LogFactory.getLog(this.getClass());
	
	private Bs531002FieldList testField = null;
	
	private Bs531001FieldList testField2 = null;
	
	
	/**
	 * 
	 * @param hostAddr
	 * @param port
	 * @throws IOException
	 * @throws Exception
	 */
	public Txdm2512CSample() {
		
		CbUtil.setupLog4jConfig(this, "log4j.tomcat.xml");
		
		testField = new Bs531002FieldList();
		
		testField2 = new Bs531001FieldList();
	}
		
	public static void main(String[] args) {
		
		int j = 0;
				
		while(true) {
			
			for(int i = 0; i<1; i++) {
				Txdm2512CSample client = new Txdm2512CSample();
				
				client.myId = j++;
				
				Thread thr = new Thread(client);
	
				thr.start();
			}
			
			
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		    break;
		}		
	}


	
	public void run() {
		// TODO Auto-generated method stub
	
		MapForm dataMap = new MapForm();
		      
		try {

			Txdm2512CSample client = new Txdm2512CSample();
			//client.Connect("127.0.0.1", 53001);       /*부산은행 테스트*/
			
			client.Connect("99.1.1.74", 41052);
			
			
			headMap = new MapForm();
			
			/*head*/
			
			headMap.setMap("LEN"            , "0250");      //전문길이
			headMap.setMap("TRAN_CODE"      , "CJVJGRDT");  //식별코드 - TRAN CODE(CJVJGRDT)
			headMap.setMap("BIZ_CODE"       , "CYBER");     //업체코드 - 은행부여 업체코드
			headMap.setMap("BNK_CODE"       , "32");        //은행코드 - 32
			headMap.setMap("TX_GUBUN"       , "0200");      //전문구분 
			headMap.setMap("PROGRAM_ID"     , "400");       //업무구분
			headMap.setMap("TRAN_CNT"       , "1");         //송신횟수
			headMap.setMap("TRAN_NO"        , "001204");    //전문번호
			headMap.setMap("TRAN_DT"        , "150213");    //전송일자
			headMap.setMap("TRAN_TM"        , "112233");    //전송시각
			headMap.setMap("RS_CODE"        , "");          //응답코드
			headMap.setMap("FILLER"         , "");          //공란
			headMap.setMap("TRAN_B_NO"      , "");          //원전문번호	
		
			
			/*data*/
			dataMap.setMap("TRN_DATE"        , "20150223");        /*거래일자        */
			dataMap.setMap("TRN_TIME"        , "112233");          /*거래시간	    */
			dataMap.setMap("VIR_ACC_NO"      , "692144000001");    /*가상계좌번호    */
			dataMap.setMap("TRN_AMT"         , "14214270");           /*거래금액        */
			dataMap.setMap("MEDA_GBN"        , "10");              /*매체구분        */
			dataMap.setMap("TRN_UNIQ_NO"     , "111111111111");    /*거래고유번호    */
			dataMap.setMap("REG_NM"          , "20110630");        /*성명            */
			dataMap.setMap("TRN_UNIQ_NO1"    , "2222222222222");   /*거래고유번호1   */
			dataMap.setMap("NEW_VIR_ACC_NO"  , "6921455000011");   /*신가상계좌번호  */
			dataMap.setMap("FILLER2"         , "");                /*공란*/
			
			
						
			/*head*/
//			headMap.setMap("LEN"            , "0250");      //전문길이
//			headMap.setMap("TRAN_CODE"      , "CJVJGRDT");  //식별코드 - TRAN CODE(CJVJGRDT)
//			headMap.setMap("BIZ_CODE"       , "CYBER");     //업체코드 - 은행부여 업체코드
//			headMap.setMap("BNK_CODE"       , "32");        //은행코드 - 32
//			headMap.setMap("TX_GUBUN"       , "0200");      //전문구분 
//			headMap.setMap("PROGRAM_ID"     , "300");       //업무구분
//			headMap.setMap("TRAN_CNT"       , "1");         //송신횟수
//			headMap.setMap("TRAN_NO"        , "1");    //전문번호
//			headMap.setMap("TRAN_DT"        , "150402");    //전송일자
//			headMap.setMap("TRAN_TM"        , "112233");    //전송시각
//			headMap.setMap("RS_CODE"        , "");          //응답코드
//			headMap.setMap("FILLER"         , "");          //공란
//			headMap.setMap("TRAN_B_NO"      , "120012");    //원전문번호	
//		
//			
//			/*data*/
//			dataMap.setMap("ACC_NO"          , "55553434");       /*계좌번호  입금계좌번호        */
//			dataMap.setMap("FILLER2"         , "");               /*공란	    */
//			dataMap.setMap("JOB_GBN"         , "20");             /*거래구분  입금 20  입금취소 51    */
//			dataMap.setMap("BANK_CODE"       , "032");            /*은행코드        */
//			dataMap.setMap("TRN_AMT"         , "285022960");          /*거래금액        */
//			dataMap.setMap("TRN_F_AMT"       , "100");            /*입금후 잔액   */
//			dataMap.setMap("GIRO_CODE"       , "0321104");        /*입금점 지로코드            */
//			dataMap.setMap("REG_NM"          , "테스트");          /*성명   */
//			dataMap.setMap("SUPYO_NO"        , "11112344455555"); /*수표번호  */
//			dataMap.setMap("CASH_AMT"        , "15300");          /*현금*/
//			dataMap.setMap("TA_SUPYO_AMT"    , "0");              /*타행 수표금액         */
//			dataMap.setMap("ETC_SUPYO_AMT"   , "0");              /*가게수표, 기타    */
//			dataMap.setMap("VIR_ACC_NO"      , "6921450000026");  /*가상계좌번호            */
//			dataMap.setMap("TRN_DATE"        , "20150402");       /*거래일자   */
//			dataMap.setMap("TRN_TIME"        , "150110");         /*거래시간  */
//			dataMap.setMap("FILLER3"         , "");               /*공란*/
			
				 		
			
			long before = System.currentTimeMillis();
			
			// 사전 조회
			client.sendData(testField.makeSendBuffer(headMap, dataMap));
			
			//입금
			//client.sendData(testField2.makeSendBuffer(headMap, dataMap));
	
			byte[] recv  = client.recv(30);
			
			long after = System.currentTimeMillis();
			
			log.info("=============================================");
			log.info("  RECV TIME== " + ((after-before)/1000.0) + "Sec ::" +  new String(recv));
			log.info("=============================================");
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			 e.printStackTrace();
			log.error("=============================================");
			log.error(" RECV 오류발생 ID=" + myId + ", MSG=" + e.getMessage());
			log.error("=============================================");
		}
	}

	/**
	 * @return the myId
	 */
	public int getMyId() {
		return myId;
	}

	/**
	 * @param myId the myId to set
	 */
	public void setMyId(int myId) {
		this.myId = myId;
	}
	
	
}
