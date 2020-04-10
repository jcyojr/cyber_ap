/**
 *  �ֽý��۸� : ���̹����漼û ��ȭ
 *  ��  ��  �� : ������-���ϼ����������� ������ȸ 2
 *  ��  ��  �� : ������-���̹���û 
 *               ����ó��
 *               
 *  Ŭ����  ID : Kf251005Service
 *  ����  �̷� :
 * ------------------------------------------------------------------------
 *  �ۼ���     �Ҽ�  		 ����      Tag   ����
 * ------------------------------------------------------------------------   
 * �ҵ���   ��ä��(��)    2011.06.13   %01%  �ű��ۼ�
 *  
 */
package com.uc.bs.cyber.service.kf251005;

import java.math.BigDecimal;
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
public class Kf251005Service {

	protected Log log = LogFactory.getLog(this.getClass());
	
	private ApplicationContext appContext = null;
	
	private IbatisBaseService sqlService_cyber = null;
		
	private Kf251005FieldList kfField = null;
	
	CbUtil cUtil = null;
	
	/**
	 * ������
	 */
	public Kf251005Service(ApplicationContext appContext) {
		// TODO Auto-generated constructor stub
		
		setAppContext(appContext);
		
		kfField = new Kf251005FieldList();
		
	}

	/* appContext property ���� */
	public void setAppContext(ApplicationContext appContext) {
		
		this.appContext = appContext;
		
		sqlService_cyber = (IbatisBaseService) this.appContext.getBean("baseService_cyber");

	}

	public ApplicationContext getAppContext() {
		return appContext;
	}
	
	/**
	 * ���ϼ��� �������� ������ȸ ����
	 * */
	public byte[] chk_kf_251005(MapForm headMap, MapForm mf) throws Exception {
		
		MapForm sendMap = new MapForm();
		
		CbUtil cUtil = null;
		
		/*���ϼ������ ����������ȸ �� ��� */
		String resCode = "000";       /*�����ڵ�*/
		String giro_no = "1004102";   /*�λ�������ڵ�*/
		String sg_code = "26";        /*�λ�ñ���ڵ�*/
		
		String POINTNO = "";          /*������ȣ*/
		String DATANUM = "";          /*�����Ͱ���*/
		int START_IDX = 0;
		
		/*�ʿ亯����*/
		String icheno = "";
		String custno = "";
		
		
		/*�ݺ���*/
		ArrayList<MapForm> alRepeatData = new ArrayList<MapForm>();
		
		try{
			cUtil = CbUtil.getInstance();
			
			log.info(cUtil.msgPrint("2", "S","KP251005 chk_kf_251005()", resCode));
			
			/*����üũ*/
			
			/*���*/
			if(!headMap.getMap("GPUB_CODE").equals(sg_code)){
				resCode = "339";
			}
			/*�����������ڵ�*/
			if(!headMap.getMap("GJIRO_NO").equals(giro_no)){
				resCode = "324";
			}

			POINTNO =  (String)mf.getMap("POINTNO");  /*������ȣ*/
			DATANUM =  (String)mf.getMap("DATANUM");  /*�Ǽ�*/
			
			/*��ȸ���а��� ���� ��ȸ������ �Է�*/
			if (mf.getMap("SEARCHGUBUN").equals("S")) {
				
				String searchky = cUtil.waterChgCustno("S", mf.getMap("SEARCHKEY").toString().substring(20,29));
				
				if(searchky.equals(mf.getMap("SEARCHKEY").toString())) {
					
					custno = mf.getMap("SEARCHKEY").toString().substring(20,29);
					
				} else {
					
					resCode = "311";   /*�������� ����*/
 				
				}
				
			} else if (mf.getMap("SEARCHGUBUN").equals("E")) {
				
				icheno = mf.getMap("SEARCHKEY").toString();   /*���ڳ��ι�ȣ*/
				
			} else {
				
				resCode = "324";   /*��ȸ��������*/
			}
			
			mf.setMap("CUST_NO", custno);
			mf.setMap("EPAY_NO", icheno);
			
			int levyCnt = 0;
			
			/*������� �����̸�....*/
			if(resCode.equals("000")){
				/*���̹� DB�� ���Ͽ� ���������� Ȯ���Ѵ�..*/
				
				ArrayList<MapForm>  kfCmd251005List  =  sqlService_cyber.queryForList("KF251001.SELECT_SHSD_LEVY_LIST", mf);
				
				levyCnt = kfCmd251005List.size();   /*�����ѰǼ�*/
				
				if(levyCnt <= 0){
				   resCode = "311";  /*�ΰ����� ����*/

				} else {
							            
		            if (POINTNO.equals("000")) POINTNO ="001";

		            START_IDX = Integer.parseInt(POINTNO) -1;
		            
		            if (START_IDX >= levyCnt) {
		            	
		            	resCode = "311";  /*�ΰ����� ����*/  	

		            } else {
		            	
		            	resCode = "000"; 
		            	
		            	if (Integer.parseInt(DATANUM) > 10 && Integer.parseInt(DATANUM) < 99 )
		                    DATANUM = "10";

		            	if ( levyCnt < (START_IDX+Integer.parseInt(DATANUM)) )
		                    DATANUM = String.valueOf( levyCnt - START_IDX );

		            	if (Integer.parseInt(DATANUM) > 10)
		                    DATANUM = "10";    
		            	
		            	/*0210 �������� ���� */
		            	sendMap.setMap("SEARCHGUBUN", mf.getMap("SEARCHGUBUN")); /*��ȸ����*/
		            	sendMap.setMap("SEARCHKEY"  , mf.getMap("SEARCHKEY"));   /*��ȸ��ȣ*/
		    			sendMap.setMap("CUSTNO"     , mf.getMap("SEARCHKEY"));   /*���밡��ȣ  */
		            	sendMap.setMap("DATATOT"    , levyCnt);                  /*������ �Ǽ� */ 
		            	sendMap.setMap("POINTNO"    , mf.getMap("POINTNO"));     /*������ȣ    */
		            	sendMap.setMap("DATANUM"    , DATANUM);                  /*������ �Ǽ� */

		            	@SuppressWarnings("unused")
						String epay_no    = "";
		            	@SuppressWarnings("unused")
						String cust_no    = "";
		            	String gubun      = "";
		            	String napkiGubun = "";
		            	
		            	String NAPDATE    = "";
		            	String NAPGUBUN   = "";
		            	long NAPAMT       = 0;
		            	
		            	for ( int al_cnt = 0;  al_cnt < kfCmd251005List.size();  al_cnt++)   {
		            		
		            		MapForm mfCmd251005List  =  kfCmd251005List.get(al_cnt);
		            		
		            		log.debug("mfCmd251005List = " + mfCmd251005List.getMaps());
		            		
		            		MapForm mfRepeatData = new MapForm();
							
							alRepeatData.add(mfRepeatData);
		            		
							epay_no = (String) mfCmd251005List.getMap("EPAY_NO");
							cust_no = (String) mfCmd251005List.getMap("CUST_NO");
							gubun   = (String) mfCmd251005List.getMap("GUBUN");
							
							if (mfCmd251005List.getMap("GUBUN").equals("3")) { /*����*/
								gubun = "1";
							}
							
							//���⳻(B), ������(A) üũ
			                napkiGubun = cUtil.getNapGubun((String)mfCmd251005List.getMap("DUE_DT"), (String)mfCmd251005List.getMap("DUE_F_DT"));
							
			                if(napkiGubun.equals("B")){
			                	
			                	NAPDATE  = (String)mfCmd251005List.getMap("DUE_DT");
			                    NAPGUBUN = napkiGubun;                  //���⳻�ı���
			                    NAPAMT   = ((BigDecimal)mfCmd251005List.getMap("SUM_B_AMT")).longValue();  //ü�����ΰ�� ü����
			                    
			                } else if (napkiGubun.equals("A")){
			                	
			                	NAPDATE  = (String)mfCmd251005List.getMap("DUE_F_DT");
			                    NAPGUBUN = napkiGubun;                 //���⳻�ı���
			                    NAPAMT   = ((BigDecimal)mfCmd251005List.getMap("SUM_F_AMT")).longValue();
			                } else {
			                	resCode = "093"; 
			                }
			                
							mfRepeatData.setMap("DANGGUBUN"  , gubun);
							mfRepeatData.setMap("BYYYMM"     , (String)mfCmd251005List.getMap("TAX_YY") + (String)mfCmd251005List.getMap("TAX_MM"));
							mfRepeatData.setMap("NAPAMT"     , NAPAMT);
							mfRepeatData.setMap("NAPGUBUN"   , NAPGUBUN);
							mfRepeatData.setMap("NAPDATE"    , NAPDATE);
							mfRepeatData.setMap("AUTOREG"    , "N");
							mfRepeatData.setMap("ICHENO"     , (String)mfCmd251005List.getMap("EPAY_NO"));  //���ڳ��ι�ȣ
							
		            	}
   	
		            }		            	
					
				}
				
			} 
			
			log.info(cUtil.msgPrint("2", "","KP251005 chk_kf_251005()", resCode));
			
        } catch (Exception e) {
			
			e.printStackTrace();
			log.error("============================================");
			log.error("== chk_kf_251005 Exception(�ý���) ");
			log.error("============================================");
		}
        
        if(resCode.equals("311")) {
        	sendMap = mf;     /*�ΰ������� ���� ��� �µ���...*/
        }
        
        /*���⼭ ������带 �����Ѵ�. �ٲ�� �κи� �ٽ� �����ؼ� ������ ����*/
        headMap.setMap("RS_FLAG"   , "G");         /*�����̿���(G) */
        headMap.setMap("RESP_CODE" , resCode);     /*�����ڵ�*/
        headMap.setMap("GCJ_NO"    , "0" + sg_code + "0" + CbUtil.lPadString(String.valueOf(SeqNumber()), 8, '0'));     /*�̿��� �Ϸù�ȣ*/
        headMap.setMap("TX_GUBUN"  , "0210");
        
        return kfField.makeSendReptBuffer(headMap, sendMap, alRepeatData);
	}

	/*----------------------------------------------*/
	/* �Ϸù�ȣ �������� �׽�Ʈ*/
	/*----------------------------------------------*/
	private int SeqNumber(){
    	return (Integer)sqlService_cyber.queryForBean("CODMBASE.CODM_GETSEQNO", "00");
    }
}
