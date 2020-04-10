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
 * @author ������
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
			//client.Connect("127.0.0.1", 53001);       /*�λ����� �׽�Ʈ*/
			
			client.Connect("99.1.1.74", 41052);
			
			
			headMap = new MapForm();
			
			/*head*/
			
			headMap.setMap("LEN"            , "0250");      //��������
			headMap.setMap("TRAN_CODE"      , "CJVJGRDT");  //�ĺ��ڵ� - TRAN CODE(CJVJGRDT)
			headMap.setMap("BIZ_CODE"       , "CYBER");     //��ü�ڵ� - ����ο� ��ü�ڵ�
			headMap.setMap("BNK_CODE"       , "32");        //�����ڵ� - 32
			headMap.setMap("TX_GUBUN"       , "0200");      //�������� 
			headMap.setMap("PROGRAM_ID"     , "400");       //��������
			headMap.setMap("TRAN_CNT"       , "1");         //�۽�Ƚ��
			headMap.setMap("TRAN_NO"        , "001204");    //������ȣ
			headMap.setMap("TRAN_DT"        , "150213");    //��������
			headMap.setMap("TRAN_TM"        , "112233");    //���۽ð�
			headMap.setMap("RS_CODE"        , "");          //�����ڵ�
			headMap.setMap("FILLER"         , "");          //����
			headMap.setMap("TRAN_B_NO"      , "");          //��������ȣ	
		
			
			/*data*/
			dataMap.setMap("TRN_DATE"        , "20150223");        /*�ŷ�����        */
			dataMap.setMap("TRN_TIME"        , "112233");          /*�ŷ��ð�	    */
			dataMap.setMap("VIR_ACC_NO"      , "692144000001");    /*������¹�ȣ    */
			dataMap.setMap("TRN_AMT"         , "14214270");           /*�ŷ��ݾ�        */
			dataMap.setMap("MEDA_GBN"        , "10");              /*��ü����        */
			dataMap.setMap("TRN_UNIQ_NO"     , "111111111111");    /*�ŷ�������ȣ    */
			dataMap.setMap("REG_NM"          , "20110630");        /*����            */
			dataMap.setMap("TRN_UNIQ_NO1"    , "2222222222222");   /*�ŷ�������ȣ1   */
			dataMap.setMap("NEW_VIR_ACC_NO"  , "6921455000011");   /*�Ű�����¹�ȣ  */
			dataMap.setMap("FILLER2"         , "");                /*����*/
			
			
						
			/*head*/
//			headMap.setMap("LEN"            , "0250");      //��������
//			headMap.setMap("TRAN_CODE"      , "CJVJGRDT");  //�ĺ��ڵ� - TRAN CODE(CJVJGRDT)
//			headMap.setMap("BIZ_CODE"       , "CYBER");     //��ü�ڵ� - ����ο� ��ü�ڵ�
//			headMap.setMap("BNK_CODE"       , "32");        //�����ڵ� - 32
//			headMap.setMap("TX_GUBUN"       , "0200");      //�������� 
//			headMap.setMap("PROGRAM_ID"     , "300");       //��������
//			headMap.setMap("TRAN_CNT"       , "1");         //�۽�Ƚ��
//			headMap.setMap("TRAN_NO"        , "1");    //������ȣ
//			headMap.setMap("TRAN_DT"        , "150402");    //��������
//			headMap.setMap("TRAN_TM"        , "112233");    //���۽ð�
//			headMap.setMap("RS_CODE"        , "");          //�����ڵ�
//			headMap.setMap("FILLER"         , "");          //����
//			headMap.setMap("TRAN_B_NO"      , "120012");    //��������ȣ	
//		
//			
//			/*data*/
//			dataMap.setMap("ACC_NO"          , "55553434");       /*���¹�ȣ  �Աݰ��¹�ȣ        */
//			dataMap.setMap("FILLER2"         , "");               /*����	    */
//			dataMap.setMap("JOB_GBN"         , "20");             /*�ŷ�����  �Ա� 20  �Ա���� 51    */
//			dataMap.setMap("BANK_CODE"       , "032");            /*�����ڵ�        */
//			dataMap.setMap("TRN_AMT"         , "285022960");          /*�ŷ��ݾ�        */
//			dataMap.setMap("TRN_F_AMT"       , "100");            /*�Ա��� �ܾ�   */
//			dataMap.setMap("GIRO_CODE"       , "0321104");        /*�Ա��� �����ڵ�            */
//			dataMap.setMap("REG_NM"          , "�׽�Ʈ");          /*����   */
//			dataMap.setMap("SUPYO_NO"        , "11112344455555"); /*��ǥ��ȣ  */
//			dataMap.setMap("CASH_AMT"        , "15300");          /*����*/
//			dataMap.setMap("TA_SUPYO_AMT"    , "0");              /*Ÿ�� ��ǥ�ݾ�         */
//			dataMap.setMap("ETC_SUPYO_AMT"   , "0");              /*���Լ�ǥ, ��Ÿ    */
//			dataMap.setMap("VIR_ACC_NO"      , "6921450000026");  /*������¹�ȣ            */
//			dataMap.setMap("TRN_DATE"        , "20150402");       /*�ŷ�����   */
//			dataMap.setMap("TRN_TIME"        , "150110");         /*�ŷ��ð�  */
//			dataMap.setMap("FILLER3"         , "");               /*����*/
			
				 		
			
			long before = System.currentTimeMillis();
			
			// ���� ��ȸ
			client.sendData(testField.makeSendBuffer(headMap, dataMap));
			
			//�Ա�
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
			log.error(" RECV �����߻� ID=" + myId + ", MSG=" + e.getMessage());
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
