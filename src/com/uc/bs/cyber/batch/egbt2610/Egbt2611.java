/**
 *  �ֽý��۸� : �λ�� ���̹����漼û
 *  ��  ��  �� : ��ġ
 *  ��  ��  �� : ���ڰ��� �ڷᱸ��
 *               ��ġ�� �����Ǹ� ���� 9�ÿ� �ѹ��� 
 *  Ŭ����  ID : Egbt2610
 *  ����  �̷� :
 * ------------------------------------------------------------------------
 *  �ۼ���      	�Ҽ�  		    ����            Tag           ����
 * ------------------------------------------------------------------------
 *  ����       ��ä��(��)      2011.05.30         %01%         �����ۼ�
 */
package com.uc.bs.cyber.batch.egbt2610;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import tm.massmail.sendapi.ThunderMassMail;
import tm.massmail.sendapi.ThunderMassMailSender;

import antlr.collections.List;

import com.ibatis.sqlmap.client.SqlMapException;
import com.uc.bs.cyber.CbUtil;
import com.uc.core.MapForm;
import com.uc.core.misc.XMLFileReader;
import com.uc.core.misc.XmlUtils;
import com.uc.core.spring.service.IbatisService;
/**
 * @author Administrator
 *
 */
public class Egbt2611 {
	
	private Log log ;

	private ApplicationContext context  = null;
	
	private IbatisService sqlService    = null;
	
	private ArrayList<String> sgList = null;

	private MapForm paramMap;
	/**
	 * 
	 */
	public Egbt2611() {
		// TODO Auto-generated constructor stub
		super();
		
		log = LogFactory.getLog(this.getClass());

		String strSgList = CbUtil.getResource("ApplicationResource", "cyber.lt.sglist");
		
		StringTokenizer tok = new StringTokenizer(strSgList, ",");
		
		
		/**
		 * 
		 */
		sgList =  new ArrayList<String>();
		
		while(tok.hasMoreElements()) {
		
			sgList.add( tok.nextToken());
		}
		
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		
		if(args.length != 4) {
			System.err.println("�ΰ����� �Ķ���� �Է¾ȵ�!!");
			
			System.err.println(" ���� :: Egbt2611  [�ΰ��⵵] [�ΰ���] [��������(1:���ڰ���, 2:�ڵ���ü�ȳ�)] [���ڰ�������] ");
			
			return;
		}
		
		System.out.println("=========================================================");
		System.out.println("===   ���̹����漼û ���ڰ��� �ڷᱸ��� ����  Start   ==");
		System.out.println("=========================================================");	
		
		Egbt2611 batch = new Egbt2611();
		
		MapForm paramMap = new MapForm();
		
		paramMap.setMap("NOTI_YY", args[0]);  // �ΰ���
		paramMap.setMap("NOTI_MM", args[1]);  // �ΰ���
		//paramMap.setMap("KWA_CD", args[2]);   // �����ڵ�
		paramMap.setMap("NOTI_GB", args[2]);  // ��������(1:���ڰ���, 2:�ڵ���ü�ȳ�)
		//paramMap.setMap("DUE_DT", args[4]);   // ��������(������)
		paramMap.setMap("NOTI_SNO", Long.parseLong(args[3])); // ���ڰ����Ϸù�ȣ(����� �Ϸù�ȣ)
		
		batch.paramMap = paramMap;
		
		System.out.println("== OS Name = " + System.getProperty("os.name"));
		System.out.println("== OS Version = " + System.getProperty("os.version"));
		
		try {
			//Log
			
			CbUtil.setupLog4jConfig(batch, "log4j.egbt2611.xml");
			
			/**
			 * ���ؽ�Ʈ ���� *Service.xml ������ ã�Ƽ� sqlmapConfig.xml ���Ͽ� ����Ѵ�
			 */
			try {
				String[] xmls = XMLFileReader.getAllFilePathArray(CbUtil.getResourcePath(batch, "/"), "SERVICE.XML");

				String strSrc = CbUtil.getResourcePath(batch, "/") + "config/sqlmaps.xml";

				String strDest = CbUtil.getResourcePath(batch, "/") + "config/sqlmapConfig.xml";

				XmlUtils.setXmlAttributes(batch, strSrc, strDest, "sqlMapConfig", "sqlMap", "url", xmls);

				System.out.println("2=========================================================");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
		
			batch.context = new ClassPathXmlApplicationContext("config/Single-Spring-db.xml");


			batch.mainProcess();			
			
			
		} catch (SqlMapException se) {
			se.printStackTrace();
		} catch (BeansException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}		
		
		System.out.println("=========================================================");
		System.out.println("===    ���̹����漼û ���ڰ��� �ڷᱸ��� ����  End    ==");
		System.out.println("=========================================================");	


	}
	
	/**
	 * ���� ó��
	 */
	private void mainProcess() {
		
		
		if(sqlService == null) sqlService = (IbatisService) context.getBean("ibatisService");
		
		// 3. ���ڰ��� �ڷḦ �߼��Ѵ�..
	
		try {
			sendEnotify();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	
	/**
	 * ������Ͽ� ���ڰ��� �ڷ� ���۹� ��� Update
	 * @throws Exception
	 */
	private void sendEnotify() throws Exception {
	
		String taxNm = sqlService.getOneFieldString("EGBT2610.SELECT_CO3301_KWANAME", paramMap.getMap("NOTI_MM"));
		
		ThunderMassMailSender tms = new ThunderMassMailSender(); 
		ThunderMassMail tm = new ThunderMassMail();               

		tm.setThunderMailURL(CbUtil.getResource("ApplicationResource", "enoti.thundermail.url"));			//�������UI url
		tm.setWriter (CbUtil.getResource("ApplicationResource", "enoti.thundermail.user"));					//���� �ۼ��� ID

		tm.setContentType("template");
		
		//���� ���� Ÿ�� content: ���� ���� ���� ����, template: ���� ���ø� ���
		// tm.setMailContent("[$name]���� ������ �������� �����մϴ�."); 
		//setContentType�� content �϶� ���� ���� �ʼ�

		tm.setSenderEmail(CbUtil.getResource("ApplicationResource", "enoti.thundermail.smail")); 			//������ ��� �̸���
		tm.setSenderName(CbUtil.getResource("ApplicationResource", "enoti.thundermail.sname"));				//������ ��� �̸�
		tm.setReceiverName("[$name]");				//�޴� ��� �̸�
		
		String NOTI_GB=(String)paramMap.getMap("NOTI_GB"); //��������

		//���ڰ���
		if(NOTI_GB.equals("1")) {
			tm.setMailTitle("[�λ걤���� ETAX] [$name]�� " 
					+ paramMap.getMap("NOTI_YY") + "�� " 
					+ paramMap.getMap("NOTI_MM") + "�� "
					+ taxNm + " ����� ���ڰ��� �ȳ�");			//���� ����
			//tm.setTemplate_id(CbUtil.getResource("ApplicationResource", "enoti.thundermail.template4"  ));
			tm.setTemplate_id("12");
		}
		
		//�ڵ���ü
		if(NOTI_GB.equals("2")) {
			tm.setMailTitle("[�λ걤���� ETAX] [$name]�� " 
					+ paramMap.getMap("NOTI_YY") + "�� " 
					+ paramMap.getMap("NOTI_MM") + "�� "
					+ taxNm + " ����� �ڵ���ü �ȳ�");			//���� ����
			//tm.setTemplate_id(CbUtil.getResource("ApplicationResource", "enoti.thundermail.template5"  ));
			tm.setTemplate_id("13");
			
		}

		// tm.setContentType("content"); 
		
		
		tm.setTargetType("string");	

		//�ϴ���ġȯ ����
		//��� ������ �ϴ��� ġȯ��
		//[$email],[$cellPhone],[$name],[$customerID],[$etc1]~[$etc16]
		// tm.setFileOneToOne("[$email]���̸��ϩ�[$name]�������ڸ�");
		//tm.setFileOneToOne("[$email]���̸��ϩ�[$name]�������ڸ�[$etc1]�����������[$etc2]�����ڳ��ι�ȣ��[$etc3]���������[$etc4]����������");
		tm.setFileOneToOne("[$email]���̸��ϩ�[$name]�������ڸ�[$etc1]�����������[$etc2]�����ڳ��ι�ȣ��[$etc3]�����αݾש�[$etc4]���������ک�[$etc5]���������");
		
		//����� ��� ���� 
		//string : string���·� �Ķ���Ϳ� ���� ����, query : ����� ���� ������ ����
		
		/**
		 * ������ �Ϸ�ɶ����� �ݺ��Ѵ�...
		 */
		while(sqlService.queryForUpdate("EGBT2610.UPDATE_ME1151_SENDING", paramMap) > 0) {
			//StringBuffer targetStr = new StringBuffer("�̸���,�����ڸ�,�������,���ڳ��ι�ȣ,�������,��������");
			StringBuffer targetStr = new StringBuffer("�̸���,�����ڸ�,�������,���ڳ��ι�ȣ,���αݾ�,��������,�������");
			
			ArrayList<MapForm> sendList = sqlService.queryForList("EGBT2610.SELECT_ME1151", paramMap);
			
			Iterator<MapForm> sendLoop = sendList.iterator();
			
			while(sendLoop.hasNext()) {
				MapForm sendMap  = sendLoop.next();
				
				targetStr.append("��" + sendMap.getMap("E_MAIL") + "," + sendMap.getMap("MEM_NM") + "," + sendMap.getMap("NOTI_STD"));
				
			}
			
			tm.setTargetString(targetStr.toString());
			String result = tms.send(tm);			//���� �߼�, ���� ��� ����
			
			log.debug("MAIL SEND STR=" + targetStr);
			
			// System.out.println("MAIL SEND RESULT==" + result);
			StringTokenizer tok = new StringTokenizer(result, "||");
			
			paramMap.setMap("TR_RES", tok.nextToken());
			if(tok.hasMoreTokens())	paramMap.setMap("MNG_NO", tok.nextToken());
			
			if(paramMap.getMap("TR_RES").equals("-100")) paramMap.setMap("TRTG", "1");
			else paramMap.setMap("TRTG", "9");
			
			// ������� �������� ��� Update
			int sendCnt = sqlService.queryForUpdate("EGBT2610.UPDATE_ME1151_SENDRESULT", paramMap);

			log.info("���Ϲ߼� ���="+ result + " �Ǽ�==" + sendCnt);
		}
	
	}
	
	
}
