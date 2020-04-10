/**
 * ��ġ STARTING
 */
package com.uc.bs.cyber.starter;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.uc.bs.cyber.CbUtil;
import com.uc.bs.cyber.batch.Txdm_BatchProcess;
import com.uc.bs.cyber.daemon.Codm_interface;
import com.uc.core.misc.Utils;
import com.uc.core.misc.XMLFileReader;
import com.uc.core.misc.XmlUtils;
import com.uc.core.spring.service.IbatisBaseService;
import com.uc.core.spring.service.UcContextHolder;

/**
 * @author Administrator
 *
 */
public class Codm_BatchStart implements Runnable{

	private static String ClsName = "com.uc.bs.cyber.starter.Codm_BatchStart";

	private static IbatisBaseService  sqlService_cyber = null;
	private static IbatisBaseService  sqlService = null;
	
	private static Log log = LogFactory.getLog(ClsName);
	
	private ApplicationContext  appContext = null;
	private String dataSource  = null;
	
	
	private static String  procName = "���̹����漼û ��ġ����";
	private static String  procId   = "1000";


	public Codm_BatchStart() {
		// TODO Auto-generated constructor stub
		log.debug("Batch Starting...");
	}
	
	public void setApp(ApplicationContext context, String dataSrc) {
		
		this.appContext = context;
		
		this.dataSource = dataSrc;

	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ApplicationContext context  = null;
		
		Codm_BatchStart coBatchStart = new Codm_BatchStart();
		
		/*Log4j ����*/
		CbUtil.setupLog4jConfig(coBatchStart, "log4j.tomcat.xml");
		
		log.info("=======================================");
		log.info("== CyberTaxSystem Batch TEST Started ==");
		log.info("=======================================");
	
		try {
			
			try {
				String[] xmls = XMLFileReader.getAllFilePathArray(Utils.getResourcePath(coBatchStart, "/"), "SERVICE.XML");

				String strSrc = Utils.getResourcePath(coBatchStart, "/") + "config/sqlmaps.xml";

				String strDest = Utils.getResourcePath(coBatchStart, "/") + "config/sqlmapConfig.xml";

				XmlUtils.setXmlAttributes(coBatchStart, strSrc, strDest, "sqlMapConfig", "sqlMap", "url", xmls);

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
		
			context = new ClassPathXmlApplicationContext("config/Spring-db.xml");

			coBatchStart.setApp(context, "LT_etax");
			
			new Thread(coBatchStart, "Main Batch Administrator").start();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
		UcContextHolder.setCustomerType(this.dataSource);
		
		sqlService = (IbatisBaseService) this.appContext.getBean("baseService");	
		
		sqlService_cyber = (IbatisBaseService) this.appContext.getBean("baseService_cyber");
		
		log.info("=======================================");
		log.info("==    Main Batch Thread Started      ==");
		log.info("=======================================");


		/*---------------------------------*/
		/*���⼭ ���� ������ġ�� ���۽�Ų��.*/
		/*---------------------------------*/
		
		Thread[] batchThread = null;	
		
		try {
			/*----------------------------------------------*/
			/*1. ������ ��ġ�� �⵿��Ų��.                  */
			/*----------------------------------------------*/
			
			/* ���� ���� Spring ���ؽ�Ʈ�� �������ش� */
			batchThread = new Thread[CbUtil.getResourceInt("ApplicationResource", "cyber.batch.count")];

			log.info("=========================��ġ �⵿ ����========================");

			StringBuffer sb_key = new StringBuffer();
			
			for(int i=0; i < batchThread.length; i++) {
										
				sb_key.append("cyber.batch.class" + i);
				
				Codm_interface batch = (Codm_interface) Class.forName(CbUtil.getResource("ApplicationResource", sb_key.toString())).newInstance();		
				
				batch.setContext(appContext);
				
				sb_key.delete(0, sb_key.length());
				
				/*
				 * ����������...�����....
				 * daemon.initProcess();
				 * */
				batchThread[i] = new Thread((Runnable) batch);
				
				sb_key.append("cyber.batch.name" + i);
				
				String proc_id = CbUtil.getResource("ApplicationResource", sb_key.toString());
				
				sb_key.delete(0, sb_key.length());
				
				if(batch instanceof Txdm_BatchProcess) { /*�Ϲݵ����� ��� ������ �̸��� ID�� SET ���ش�*/
					
					sb_key.append("cyber.batch.title" + i);
					String proc_nm = CbUtil.getResource("ApplicationResource", sb_key.toString());
					sb_key.delete(0, sb_key.length());
					
					log.info("====��ġ [" + i + "][" + proc_id + "][" + proc_nm + " starting!!!");
				} 
				
				batchThread[i].setName(proc_id);
				batchThread[i].start();
				
			}
				
			log.info("=========================��ġ �⵿ �� =========================");
						
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		} finally {
			log.info("=== BATCH(" + procId + "::" + procName + ") STATE IS DOWN!!!");
		}		

	}
	
	/**
	 * ����ð�...
	 * @return
	 */
	@SuppressWarnings("unused")
	private String batch_process(String t_argm) {
		
		String strRet = "";
				
		try {
			/*
			 * ����ð�
			 * */
			strRet = (String) sqlService_cyber.queryForBean("CODMBASE.CODM_CUR_TIMES", t_argm);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
						
		}
		return strRet;	
	}
	
	@SuppressWarnings("unused")
	private int SeqNumber(){
    	return (Integer)sqlService_cyber.queryForBean("CODMBASE.CODM_GETSEQNO", "00");
    }
}
