/*
 * CBUTIL : 
 * */
package com.uc.bs.cyber;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.uc.core.MapForm;

public class CbUtil extends com.uc.core.misc.Utils{

	    private static CbUtil CbUtil;
	
	    public static CbUtil getInstance() throws Exception
	    {
	       if (CbUtil == null)
	       {
	    	  CbUtil = new CbUtil();
	       }
	       return CbUtil;
	    }

		/**
		 * �޽�忡 ���� ������ ������ּ���
		 * @param str
		 * @param result
		 * @return
		 */
		public static double nullCheck(String str, double result)
		{
			if(str==null || str.equals("null") || str.equals(""))return result;
			else return Double.parseDouble(str);
		}

		/**
		 * ���� �ý����� ����IP�� Return ���ش�
		 * @return
		 */
		public static String getServerIp() {
			// TODO Auto-generated method stub
			String addr = null;
			try {
				addr =  InetAddress.getLocalHost().getHostAddress();
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return addr;
		}
		
		
		/*���ϱ��ϱ�*/
	    public static String EndDay(String sYear, String sMonth) {
	    	
			Calendar cal = Calendar.getInstance(); 
			cal.set(Integer.parseInt(sYear), Integer.parseInt(sMonth)-1, Integer.parseInt("1")); 
			int year = cal.get ( Calendar.YEAR ); 
			int month = cal.get ( Calendar.MONTH )+1 ; 
			int startDay = cal.get(Calendar.DAY_OF_MONTH); 
			int endDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH); 
			
			//ù��
			System.out.println("start day = " + lPadString(String.valueOf(startDay), 2, '0'));
			//����
			System.out.println("end day = " + lPadString(String.valueOf(endDay), 2, '0'));
			
			return String.valueOf(year) + lPadString(String.valueOf(month), 2, '0') + lPadString(String.valueOf(endDay), 2, '0'); 
	    }
	    
		/*
		 * �ð����̸� ����ؼ� �ѷ���...
		 */
		public static long getDiffTimes(String st_dt, String ed_dt)
		{
			
			long rslt    = 0;

			try {
				if (st_dt == null)    return 0;
				if (st_dt.equals("")) return 0;
				if (Long.parseLong(st_dt) < 0) return 0;

				if (ed_dt == null)    return 0;
				if (ed_dt.equals("")) return 0;
				if (Long.parseLong(ed_dt) < 0) return 0;    		

				Calendar st_cal = new java.util.GregorianCalendar();
				st_cal.set(Integer.parseInt(st_dt.substring(0,4))
						, Integer.parseInt(st_dt.substring(4,6))
						, Integer.parseInt(st_dt.substring(6,8))
						, Integer.parseInt(st_dt.substring(8,10))
						, Integer.parseInt(st_dt.substring(10,12))
						, Integer.parseInt(st_dt.substring(12,14)));          

				Calendar ed_cal = new java.util.GregorianCalendar();
				ed_cal.set(Integer.parseInt(ed_dt.substring(0,4))
						, Integer.parseInt(ed_dt.substring(4,6))
						, Integer.parseInt(ed_dt.substring(6,8))
						, Integer.parseInt(ed_dt.substring(8,10))
						, Integer.parseInt(ed_dt.substring(10,12))
						, Integer.parseInt(ed_dt.substring(12,14)));

				rslt = ((ed_cal.getTimeInMillis() - st_cal.getTimeInMillis()) / 1000);

			} catch (Exception e) {
				return 0;
			}
			return rslt;
		}  
		
		/*
		 * ���ڸ� 12�ڷ� ǥ���Ѵ�.
		 * */
		public static String getCurrentTime12()
		{
			String returnStr = "";
	        
			Calendar cal = Calendar.getInstance();
	        
			int year = cal.get(Calendar.YEAR);
	        int month = cal.get(Calendar.MONTH)+1; 
	        int day = cal.get(Calendar.DAY_OF_MONTH); 
	        int hour = cal.get(Calendar.HOUR_OF_DAY); 
	        int minute = cal.get(Calendar.MINUTE); 
	        
	        String monthStr = ((month<10)?"0":"")+month; 
	        String dayStr = ((day<10)?"0":"")+day; 
	        String hourStr = ((hour<10)?"0":"")+hour; 
	        String minuteStr = ((minute<10)?"0":"")+minute; 
	        returnStr = year+monthStr+dayStr+hourStr+minuteStr;
	        
			return returnStr;
		}
		
		/*
		 * ���� FullTime�ð��� ������
		 * */
		public static String getCurrentTimes()
		{
		     String currentTime = null;
		     String currentDate = null;
		
		     Date date = new Date();
		     SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
		     currentTime = df.format(date);

		     Calendar cal = Calendar.getInstance();
		     SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
		     currentDate = sf.format(cal.getTime());

		     return currentDate + ' ' + currentTime;
		}
		
		/*
		 * ���� FullTime�ð��� ������ :: yyyyMMddHHmmssS
		 * */
		public static String getCurDateTimes()
		{
		     String currentTime = null;
		     String currentDate = null;
		
		     Date date = new Date();
		     SimpleDateFormat df = new SimpleDateFormat("HHmmssSSS");
		     currentTime = df.format(date);

		     Calendar cal = Calendar.getInstance();
		     SimpleDateFormat sf = new SimpleDateFormat("yyyyMMdd");
		     currentDate = sf.format(cal.getTime());

		     return currentDate + currentTime;
		}
		
		/**
		 * ������
		 * @param napgi
		 * @return
		 */
		public static String getNAPGI_DATE(Date napgi)
		{
		    String rtn_date = "";

		    try
		    {
		      rtn_date = new SimpleDateFormat("yyyyMMdd").format(napgi);

		      return rtn_date;
		    }
		    catch (Exception e)
		    {
		      return "";
		    }
		}
        
		/**
		 * ��������
		 * @param day
		 * @return
		 */
		public static String getNextDate(String day)
		{
		    String rtnDate = "";

		    try
		    {
		      Calendar calendar = Calendar.getInstance();
		      calendar.set( Integer.parseInt( day.trim().substring(0,4) ), 
		        Integer.parseInt( day.trim().substring(4,6) ) - 1, 
		        Integer.parseInt( day.trim().substring(6,8) ) );

		      calendar.add ( Calendar.DATE,1);
		      Date tomorrow = calendar.getTime();

		      rtnDate = getNAPGI_DATE(tomorrow);

		      return rtnDate;
		    }
		    catch (Exception e)
		    {
		      return "";
		    }
		}		  
		  
		/*������ ���ϱ�*/
		public String getNapGubun (String b_gymd, String a_gymd) throws Exception
		{
			String nap_gubun = "";
			String nap_bdate = "";
			String nap_adate = "";

			try{
		        nap_bdate = getChkNAPGI_DATE(b_gymd);
		        nap_adate = getChkNAPGI_DATE(a_gymd);

		        int bgymd_year = Integer.parseInt( nap_bdate.trim().substring(0,4) );
		        int bgymd_month = Integer.parseInt( nap_bdate.trim().substring(4,6) ) - 1;
		        int bgymd_date =Integer.parseInt( nap_bdate.trim().substring(6,8) );

		        int agymd_year = Integer.parseInt( nap_adate.trim().substring(0,4) );
		        int agymd_month = Integer.parseInt( nap_adate.trim().substring(4,6) ) - 1;
		        int agymd_date =Integer.parseInt( nap_adate.trim().substring(6,8) );

		        Calendar SYS_Cal = Calendar.getInstance();
		        Calendar BGYMD_Cal = Calendar.getInstance();
		        Calendar AGYMD_Cal = Calendar.getInstance();

		        BGYMD_Cal.set( bgymd_year, bgymd_month, bgymd_date );
		        AGYMD_Cal.set( agymd_year, agymd_month, agymd_date );

		        if (BGYMD_Cal.before(SYS_Cal))    // ������
		          nap_gubun = "A";
		        else                              // ���⳻
		          nap_gubun = "B";

		        System.out.println("���ⱸ�� üũ�� : "+nap_gubun);

		    }
		    catch (Exception ex)
		    {
		      System.out.println(ex);
		      ex.printStackTrace();  
		      throw new Exception("�� Err : getNAPGUBUN");
		    }
		    return nap_gubun;
		}
			
		 /**
		  * @�������� ���ϱ� ���+2, �Ͽ� +1
		  * @param ��������
		  * @return B : ���⳻ / A : ������
		  */
		public String getChkNAPGI_DATE(String nap_bdate)
		{
		    String Napgi_Date = "";

		    try
		    {
		    	Calendar calendar = Calendar.getInstance();
		    	calendar.set( Integer.parseInt( nap_bdate.trim().substring(0,4) ), 
				        Integer.parseInt( nap_bdate.trim().substring(4,6) ) - 1, 
				        Integer.parseInt( nap_bdate.trim().substring(6,8) ) );

				      // �Ͽ���
				      if (calendar.get(Calendar.DAY_OF_WEEK) == 1) 
				      {
				        calendar.add ( Calendar.DATE,1);
				        Date tomorrow = calendar.getTime();

				        Napgi_Date = getNAPGI_DATE(tomorrow);
				      }
				      // �����
				      else if (calendar.get(Calendar.DAY_OF_WEEK) == 7) 
				      {
				        calendar.add ( Calendar.DATE,2);
				        Date tomorrow = calendar.getTime();

				        Napgi_Date = getNAPGI_DATE(tomorrow);
				      }
				      else
				        Napgi_Date = nap_bdate;

				      return Napgi_Date;      
				      
		    }
		    catch (Exception e)
		    {
			      return "";
		    }
	    }	

		/**
		 * @author 
		 * @��������ڳ��� ���αݾ� ���ϱ�
		 * @param ��������, ����, ���������, ���ð�ȹ��, ���ð�ȹ�������, �ҹ�����ü���, �ҹ氡���,
		 * @param ������, ���������, ��Ư��, ��Ư�����
		 */
		public String getNapAmt ( String bymd, String napkiGubun, long bont, long dost, long sobt, long gyot, long nont ) throws Exception
		{
			String rt_NAPBU_AMT = "";      

			try
			{
				if ( napkiGubun.equals("B") )                          // ���⳻
					rt_NAPBU_AMT = getNapBfAmt(bont , dost, sobt, gyot, nont);
				else                                                               // ������
					rt_NAPBU_AMT = getNapAfAmt(bymd, bont , dost, sobt, gyot, nont);
			}
			catch (Exception ex)
			{
				System.out.println(ex);
				ex.printStackTrace();  
				throw new Exception("�� Err : getNapAmt");
			}

			return rt_NAPBU_AMT;
		}
	  
		/**
		 * ���⳻ �ݾ� 
		 * @param ����, ���ð�ȹ��, �ҹ�����ü���, ������, ��Ư��
		 * @return ���⳻ �ݾ�
		 */
		public String getNapBfAmt(long bont , long dost, long sobt, long gyot, long nont) throws Exception
		{
			try
			{
				return getNapBfAmt(bont, 0L, dost, 0L, sobt, 0L, gyot, 0L, nont, 0L);
			}
			catch (Exception ex)
			{
				ex.printStackTrace();  
				throw new Exception("�� Err : getNapBfAmt");
			}
	   
		}  
		
		/**
		 * ���⳻ �ݾ� 
		 * @param ����, ���������, ���ð�ȹ��, ���ð�ȹ�������, �ҹ�����ü���, �ҹ氡���,
		 * @param ������, ���������, ��Ư��, ��Ư�����
		 * @return ���⳻ �ݾ�
		 */
		public String getNapBfAmt(long bont, long bngt,long dost, long dsgt, long sobt, long sbgt,long gyot, long gogt, long nont, long nngt) throws Exception
		{
			long NAP_BFAMT = 0;
			String rt_NAPBU_AMT = "";

			try
			{
				NAP_BFAMT = (bont + bngt + dost + dsgt + sobt + sbgt + gyot + gogt + nont + nngt);

				rt_NAPBU_AMT = String.valueOf(NAP_BFAMT);

				// �ϴ��� ����
				int i = 0;
				i = rt_NAPBU_AMT.length();
				rt_NAPBU_AMT = rt_NAPBU_AMT.substring(0,i-1) + "0";
	  
			}
			catch (Exception ex)
			{
				ex.printStackTrace();  
				throw new Exception("�� Err : getNapBfAmt");
			}
			return rt_NAPBU_AMT;
		}	
		
		/**
		 * ������ �ݾ� (0.3% ���� 2005-12-19)
		 * @param ��������, ����, ���ð�ȹ��, �ҹ�����ü���, ������, ��Ư��
		 * @return ������ �ݾ�
		 */
		public String getNapAfAmt(String bymd, long bont , long dost, long sobt, long gyot, long nont) throws Exception
		{
			long NAP_AFAMT = 0;
			String rt_NAPBU_AMT = "";

			try
			{
				NAP_AFAMT = ( (bont + getGasanAmt("A", bymd, bont) ) + (dost + getGasanAmt("A", bymd, dost)) +
							(sobt + getGasanAmt("A", bymd, sobt)) + (gyot + getGasanAmt("A", bymd, gyot)) +
							(nont + getGasanAmtNont("A", bymd, nont)));

				rt_NAPBU_AMT = String.valueOf(NAP_AFAMT);

				// �ϴ��� ����
				int _i = 0;
				_i = rt_NAPBU_AMT.length();
				rt_NAPBU_AMT = rt_NAPBU_AMT.substring(0,_i-1) + "0";
			}
			catch (Exception ex)
			{
				ex.printStackTrace();  
				throw new Exception("�� Err : getNAP_AFAMT");
			}

			return rt_NAPBU_AMT;  
		}
		/**
		 * ������ ����ݾ� ���ϱ�
		 * @param ���⳻�� ����, ���ݾ�
		 * @return ����ݾ�
		 */
		public long getGasanAmt(String NapkiGubun, String bymd, long AMT) throws Exception
		{
			String rtn_amt = "";
			String rt_NAPBU_AMT = "";
			int index = 0;
			long BYMD = 0L;

			double gasan_amt_D = 0L;
			long gasan_amt = 0L;

			try
			{
				BYMD = Long.parseLong(bymd);

				if (NapkiGubun.equals("A"))
				{
					if (BYMD >= 20051200)        // ���꼼�� 0.3% ���� (2005�� 12�� �ΰ��� ����)
						gasan_amt_D = AMT * 0.03;
					else                                      // ���꼼�� 0.5% ����
						gasan_amt_D = AMT * 0.05;

					Double doubleVal = new Double(gasan_amt_D);
					gasan_amt = doubleVal.longValue();  

					rtn_amt = String.valueOf(gasan_amt);
					index = rtn_amt.indexOf(".");

					if (index > -1)
						rt_NAPBU_AMT = rtn_amt.substring(0,index);
					else
						rt_NAPBU_AMT = rtn_amt;
				}
				else
				{
					rt_NAPBU_AMT = "0";
				}

				// �ϴ��� ����
				int _i = 0;
				_i = rt_NAPBU_AMT.length();
				rt_NAPBU_AMT = rt_NAPBU_AMT.substring(0,_i-1) + "0";


			}
			catch (Exception ex)
			{
				System.out.println(ex);
				ex.printStackTrace();  
				throw new Exception("�� Err : getGASAN_AMT(String NAPGIGUBUN, String bymd, long AMT) ");
			}

			return Long.parseLong(rt_NAPBU_AMT);
		}
		

		/**
		 * ������ ����ݾ� ���ϱ�
		 * @param ���⳻�� ����, ���ݾ�
		 * @return ����ݾ�
		 */
		public long getGasanAmtNont(String NapkiGubun, String bymd, long AMT) throws Exception
		{
			String rtn_amt = "";
			String rt_NAPBU_AMT = "";
			int index = 0;
			long BYMD = 0L;

			double gasan_amt_D = 0L;
			long gasan_amt = 0L;

			try
			{
				BYMD = Long.parseLong(bymd);

				if (NapkiGubun.equals("A"))
				{
					if (BYMD >= 20040500)        // ���꼼�� 0.3% ���� (2005�� 12�� �ΰ��� ����)
						gasan_amt_D = AMT * 0.03;
					else                                      // ���꼼�� 0.5% ����
						gasan_amt_D = AMT * 0.05;

					Double doubleVal = new Double(gasan_amt_D);
					gasan_amt = doubleVal.longValue();  

					rtn_amt = String.valueOf(gasan_amt);
					index = rtn_amt.indexOf(".");

					if (index > -1)
						rt_NAPBU_AMT = rtn_amt.substring(0,index);
					else
						rt_NAPBU_AMT = rtn_amt;
				}
				else
				{
					rt_NAPBU_AMT = "0";    
				}

				// �ϴ��� ����
				int _i = 0;
				_i = rt_NAPBU_AMT.length();
				rt_NAPBU_AMT = rt_NAPBU_AMT.substring(0,_i-1) + "0";


			}
			catch (Exception ex)
			{
				System.out.println(ex);
				ex.printStackTrace();  
				throw new Exception("�� Err : getGasanAmtNont ");
			}

			return Long.parseLong(rt_NAPBU_AMT);
		}
		
		/**
		 * ������ȣ3 ���ϱ�
		 * @param : ���⳻�Ѿ�, �������Ѿ�
		 * @return : ������ȣ3
		 */
		public String getGum3(String NAP_AFAMT, String NAP_BFAMT) throws Exception
		{
			String gum3 = "";

			try
			{
				gum3 = String.valueOf( (RetGum(NAP_AFAMT) + RetGum(NAP_BFAMT)) % 10 );
			}
			catch (Exception ex)
			{
				System.out.println(ex);
				ex.printStackTrace();  
				throw new Exception("�� Err : getGum3");
			}

			return gum3;
		}	

		/**
		 * String ���ϱ�
		 * @param ��
		 * @return ���Ѱ�
		 */
		private int RetGum(String gum) throws Exception
		{
			String str2 = "";

			int tmp = 0;
	    
			try
			{
				for (int i = 0 ; i < gum.length(); i++)
				{
					str2 = gum.substring(i,i+1).trim();
					if (!str2.equals(""))
						tmp = tmp + Integer.parseInt(str2);
				}
	      
			}
			catch (Exception ex)
			{
				System.out.println(ex);
				ex.printStackTrace();  
				throw new Exception("�� Err : RetGum");
			}

			return tmp;
	  }
	
	  /**
	  * ������ȣ4 ���ϱ�
	  * @param ����, ���ð�ȹ��, �����ü���(+)��Ư��, ������, ������
	  * @return ������ȣ4
	  */
	  public String getGum4(String bont, String dost, String sobt_nont, String gyot, String gymd) throws Exception
	  {
		  String gum4 = "";
		  long i_temp =0;

		  try
		  {

			  i_temp =          RetGum(bont);        // ȸ��
			  i_temp = i_temp +  RetGum(dost);       // ����
			  i_temp = i_temp +  RetGum(sobt_nont);  // ����
			  i_temp = i_temp +  RetGum(gyot);       // �ΰ��⵵
			  i_temp = i_temp +  RetGum(gymd);       // ��
			  gum4 = String.valueOf(10 - (i_temp % 10));
			  gum4 = (gum4.equals("10")) ? "0" : gum4;
		  }
		  catch (Exception ex)
		  {
			  System.out.println(ex);
			  ex.printStackTrace();  
			  throw new Exception("�� Err : getGum4");
		  }

		  return gum4;
	  }
	  
	  /**
	   * @author �ſ��������(��) ������
	   * @���� ���� ����
	   * @param ��������
	   * @return B : ���⳻ / A : ������
	   */
	  public String getNapkiGubun (String gymd) throws Exception
	  {
		  String nap_gubun = "";
		  String nap_date = "";

		  try
		  {
			  nap_date = gymd.trim();

			  int gymd_year = Integer.parseInt( nap_date.trim().substring(0,4) );
			  int gymd_month = Integer.parseInt( nap_date.trim().substring(4,6) ) - 1;
			  int gymd_date =Integer.parseInt( nap_date.trim().substring(6,8) );

			  Calendar SYS_Cal = Calendar.getInstance();
			  Calendar GYMD_Cal = Calendar.getInstance();
			  GYMD_Cal.set( gymd_year, gymd_month, gymd_date );

			  if (GYMD_Cal.before(SYS_Cal))    // ������
				  nap_gubun = "A";
			  else                             // ���⳻
				  nap_gubun = "B";
		  }
		  catch (Exception ex)
		  {
			  System.out.println(ex);
			  ex.printStackTrace();  
			  throw new Exception("�� Err : getNapkiGubun");
		  }
		  return nap_gubun;
	  }	  
	  
	  /**
	   * @author �ſ��������(��) ������
	   * @ü���� ���αݾ� ���ϱ�
	   * @param ����, ���������, ���ð�ȹ��, ���ð�ȹ�������, �ҹ�����ü���, �ҹ氡���,
	   * @param ������, ���������, ��Ư��, ��Ư�����
	   */
	  public String getNapAmt(long bont, long bngt, long dost, long dsgt, long sobt, long sbgt, long gyot, long gogt, long nont, long nngt) throws Exception
	  {

		  String rt_NAPBU_AMT = "";

		  try {
			  rt_NAPBU_AMT = getNapBfAmt(bont, bngt, dost, dsgt, sobt, sbgt, gyot, gogt, nont, nngt);
		  } catch (Exception ex) {
			  System.out.println(ex);
			  ex.printStackTrace();  
			  throw new Exception("�� Err : getNapAmt");
		  }
		  return rt_NAPBU_AMT;
	  }	  
	  
	  
		/**
		 * ���ϼ��� ���밡��ȣ -> ���������� ó���� ���밡��ȣ�� ��ȯ
		 * @param type Ÿ��
		 * @param str ���밡��ȣ
		 * @return String ������ȣ1
		 */
		public String waterChgCustno(String type, String str) {
			
		    String cust_no = str;
		    String result = "";
		    
		    int gum1 = 0;

		    int totVal = 0;
		    int totVal1 = 0;

		    String tmp1 = "";
		    
		    try {
		    	
		    	tmp1 = lPadString(cust_no, 29, '0');

			    for(int i = 0; i < tmp1.length() ; i++) {
			    	
			        int val = 0;

			        val = Integer.parseInt(tmp1.substring(i,i+1));

			        if(i%2 == 0) {
			            totVal = totVal + val * 1;
			        }else{
			        	
			            if(val * 2 >= 10) {
			                String tmp2 = Integer.toString(val*2);
			                totVal = totVal + Integer.parseInt(tmp2.substring(0,1)) + Integer.parseInt(tmp2.substring(1,2));
			            } else {
			                totVal = totVal + val * 2;
			            }
			        }
			    }

			    totVal1 = totVal % 10;

			    if(totVal1 == 0) {
			        gum1 = 0;
			    } else {
			        gum1 = 10 - totVal1;
			    }

			    if(type.equals("C")) {
			        result = Integer.toString(gum1);
			    }else if(type.equals("S")) {
			        result = tmp1+Integer.toString(gum1);
			    }
		       
		    } catch (Exception ex) {
		        System.out.println(ex);
		    }

		    System.out.println("result = " + result);
		    
		    return result;

		}
		
		/*����� ����ó���� ���� �޼����� ����� Debugging*/
		/*����� �ٺ��� �̷��� ������ ���� DBó���Ѵ�.*/
		public String msgPrint(String Gubun, String Pos, String JumunNm, String resCode){
            
            /** ��� ���   **/
            String Msg = "";
            
            if(Pos.equals("S")) {
                Msg = "�ۡۡۡ� [" + JumunNm + "] �ۡۡۡ� STARTING >>>";
            } else if (Pos.equals("E")) {
                Msg = "�ۡۡۡ� [" + JumunNm + "] �ۡۡۡ� ENDING >>>";
            } else {
                
                if (Gubun.equals("1")) {
                    
                    if (resCode.equals("000"))
                        Msg = "[" + JumunNm + "] �� ������ȸ ���� ! ";
                    else if (resCode.equals("339"))
                        Msg = "[" + JumunNm + "] �� �������з��ڵ� ���� ! ";
                    else if (resCode.equals("324"))
                        Msg = "[" + JumunNm + "] �� ���ι�ȣ ���� ! ";
                    else if (resCode.equals("311"))
                        Msg = "[" + JumunNm + "] �� �������� ���� ! ";
                    else if (resCode.equals("093"))
                        Msg = "[" + JumunNm + "] �� ���� ! ";
                    
                } else if (Gubun.equals("2")) {
                
                    if (resCode.equals("000")||resCode.equals("0000"))
                        Msg = "[" + JumunNm + "] �� ����ȸ ���� ! ";
                    else if (resCode.equals("339")||resCode.equals("1000"))
                        Msg = "[" + JumunNm + "] �� �������з��ڵ� ���� ! ";
                    else if (resCode.equals("324"))
                        Msg = "[" + JumunNm + "] �� ���ι�ȣ ���� ! ";
                    else if (resCode.equals("311")||resCode.equals("5020"))
                        Msg = "[" + JumunNm + "] �� �������� ���� ! ";
                    else if (resCode.equals("341")||resCode.equals("2000"))
                        Msg = "[" + JumunNm + "] �� ���ڳ��ι�ȣ Ʋ�� ! ";
                    else if (resCode.equals("340"))
                        Msg = "[" + JumunNm + "] �� �ֹε�Ϲ�ȣ Ȯ�� ! ";
                    else if (resCode.equals("093")||resCode.equals("9090"))
                        Msg = "[" + JumunNm + "] �� ���� ! ";
                    else if (resCode.equals("094")||resCode.equals("3000")||resCode.equals("5060"))
                        Msg = "[" + JumunNm + "] �� �����Ǽ�2���̻� ! ";
                    
                } else if (Gubun.equals("3")) {
                    
                    if (resCode.equals("000")||resCode.equals("0000"))
                        Msg = "[" + JumunNm + "] �� ���οϷ� ! ";
                    else if (resCode.equals("339")||resCode.equals("1000"))
                        Msg = "[" + JumunNm + "] �� �������з��ڵ� ���� ! ";
                    else if (resCode.equals("340"))
                        Msg = "[" + JumunNm + "] �� �ֹε�Ϲ�ȣ ���� ! ";
                    else if (resCode.equals("341")||resCode.equals("2000"))
                        Msg = "[" + JumunNm + "] �� ���ڳ��ι�ȣ ���� ! ";
                    else if (resCode.equals("324"))
                        Msg = "[" + JumunNm + "] �� ���ι�ȣ ���� ! ";
                    else if (resCode.equals("311")||resCode.equals("5020"))
                        Msg = "[" + JumunNm + "] �� �������� ���� ! ";
                    else if (resCode.equals("343")||resCode.equals("4000"))
                        Msg = "[" + JumunNm + "] �� ���αݾ� Ʋ�� ! ";
                    else if (resCode.equals("331")||resCode.equals("5000"))
                        Msg = "[" + JumunNm + "] �� ���α���� ! ";
                    else if (resCode.equals("093"))
                        Msg = "[" + JumunNm + "] �� ���� ! ";
                    
                } else if (Gubun.equals("4")) {
                    
                    if (resCode.equals("000"))
                        Msg = "[" + JumunNm + "]  �� ���γ��� ������ȸ ���� ! ";
                    else if (resCode.equals("339"))
                        Msg = "[" + JumunNm + "]  �� �������з��ڵ� ���� ! ";
                    else if (resCode.equals("324"))
                        Msg = "[" + JumunNm + "]  �� ���ι�ȣ ���� ! ";
                    else if (resCode.equals("312"))
                        Msg = "[" + JumunNm + "]  �� ���γ��� ���� ! ";
                    else if (resCode.equals("093"))
                        Msg = "[" + JumunNm + "]  �� ���� ! ";
                    
                } else if (Gubun.equals("5")) {
                    
                    if (resCode.equals("000"))
                        Msg = "[" + JumunNm + "] �� ���γ��� ����ȸ ���� ! ";
                    else if (resCode.equals("339"))
                        Msg = "[" + JumunNm + "] �� �������з��ڵ� ���� ! ";
                    else if (resCode.equals("324"))
                        Msg = "[" + JumunNm + "] �� ���ι�ȣ ���� ! ";
                    else if (resCode.equals("312"))
                        Msg = "[" + JumunNm + "] �� ���γ��� ���� ! ";
                    else if (resCode.equals("341"))
                        Msg = "[" + JumunNm + "] �� ���ڳ��ι�ȣ Ʋ�� ! ";
                    else if (resCode.equals("093"))
                        Msg = "[" + JumunNm + "] �� ���� ! ";
                    else if (resCode.equals("094"))
                        Msg = "[" + JumunNm + "] �� �����Ǽ�2���̻� ! ";
                    
                } else if (Gubun.equals("6")) {  /* ���漼 �������� ������ȸ */
                    
                    if (resCode.equals("000"))
                        Msg = "[" + JumunNm + "] �� ������ȸ ���� ! ";
                    else if (resCode.equals("122"))
                        Msg = "[" + JumunNm + "] �� �������з��ڵ� ���� ! ";
                    else if (resCode.equals("123"))
                        Msg = "[" + JumunNm + "] �� ���ι�ȣ ���� ! ";
                    else if (resCode.equals("111"))
                        Msg = "[" + JumunNm + "] �� �������� ���� ! ";
                    else if (resCode.equals("093"))
                        Msg = "[" + JumunNm + "] �� ���� ! ";

                } else if (Gubun.equals("7")) {  /* ���漼 �������� ����ȸ */
                    
                    if (resCode.equals("000")||resCode.equals("0000"))
                        Msg = "[" + JumunNm + "] �� ����ȸ ���� ! ";
                    else if (resCode.equals("122")||resCode.equals("1000"))
                        Msg = "[" + JumunNm + "] �� �������з��ڵ� ���� ! ";
                    else if (resCode.equals("123"))
                        Msg = "[" + JumunNm + "] �� ���ι�ȣ ���� ! ";
                    else if (resCode.equals("111")||resCode.equals("5020"))
                        Msg = "[" + JumunNm + "] �� �������� ���� ! ";
                    else if (resCode.equals("341")||resCode.equals("2000"))
                        Msg = "[" + JumunNm + "] �� ���ڳ��ι�ȣ Ʋ�� ! ";
                    else if (resCode.equals("093")||resCode.equals("9090"))
                        Msg = "[" + JumunNm + "] �� ���� ! ";

                } else if (Gubun.equals("8")) {  /* ���漼 ���γ��� ������ȸ */
                    
                    if (resCode.equals("000"))
                        Msg = "[" + JumunNm + "] �� ���γ��� ������ȸ ���� ! ";
                    else if (resCode.equals("122"))
                        Msg = "[" + JumunNm + "] �� �������з��ڵ� ���� ! ";
                    else if (resCode.equals("340"))
                        Msg = "[" + JumunNm + "] �� �ֹε�Ϲ�ȣ ���� ! ";
                    else if (resCode.equals("123"))
                        Msg = "[" + JumunNm + "] �� ���ι�ȣ ���� ! ";
                    else if (resCode.equals("112"))
                        Msg = "[" + JumunNm + "] �� ���γ��� ���� ! ";
                    else if (resCode.equals("093"))
                        Msg = "[" + JumunNm + "] �� ���� ! ";
                    
                } else if (Gubun.equals("9")) {  /* ���漼 ���γ��� ����ȸ */
                    
                    if (resCode.equals("000"))
                        Msg = "[" + JumunNm + "] �� ���γ��� ����ȸ ���� ! ";
                    else if (resCode.equals("122"))
                        Msg = "[" + JumunNm + "] �� �������з��ڵ� ���� ! ";
                    else if (resCode.equals("123"))
                        Msg = "[" + JumunNm + "] �� ���ι�ȣ ���� ! ";
                    else if (resCode.equals("112"))
                        Msg = "[" + JumunNm + "] �� ���γ��� ���� ! ";
                    else if (resCode.equals("341"))
                        Msg = "[" + JumunNm + "] �� ���ڳ��ι�ȣ Ʋ�� ! ";
                    else if (resCode.equals("094"))
                        Msg = "[" + JumunNm + "] �� ���ΰǼ�2���̻� ! ";
                    else if (resCode.equals("093"))
                        Msg = "[" + JumunNm + "] �� ���� ! ";
                    
                } else if (Gubun.equals("10")) {  /* ���漼 ��������� */
                    
                    if (resCode.equals("000"))
                        Msg = "[" + JumunNm + "] �� ������� �Ϸ�! ";
                    else if (resCode.equals("339"))
                        Msg = "[" + JumunNm + "] �� �������з��ڵ� ���� ! ";
                    else if (resCode.equals("324"))
                        Msg = "[" + JumunNm + "] �� ���ι�ȣ ���� ! ";
                    else if (resCode.equals("412"))
                        Msg = "[" + JumunNm + "] �� ���γ��� ����! ";
                    else if (resCode.equals("413"))
                        Msg = "[" + JumunNm + "] �� ���γ��� ������Ͽ���! ";
                    else if (resCode.equals("417"))
                        Msg = "[" + JumunNm + "] �� ���αݾ� Ʋ��! ";
                    else if (resCode.equals("093"))
                        Msg = "[" + JumunNm + "] �� ����! ";
                    
                } else if (Gubun.equals("11")) {  /*�������*/
                    
                      if (resCode.equals("000"))
                          Msg = "[" + JumunNm + "] �� ������� �Ϸ� ! ";
                      else if (resCode.equals("112"))
                          Msg = "[" + JumunNm + "] �� ���γ��� ���� ! ";
                      else if (resCode.equals("312"))
                          Msg = "[" + JumunNm + "] �� ���γ��� ���� ! (Ÿ��� ���γ�����-��ҺҰ���)";
                      else if (resCode.equals("132"))
                          Msg = "[" + JumunNm + "] �� ���γ��� ������Ͽ��� ! ";
                      else if (resCode.equals("411"))
                          Msg = "[" + JumunNm + "] �� ��Ҵ�� ���� �ƴ� ! ";
                      else if (resCode.equals("417"))
                          Msg = "[" + JumunNm + "] �� ���ŷ� ���δ�ݳ��αݾ� Ʋ�� ! ";
                      else if (resCode.equals("094"))
                          Msg = "[" + JumunNm + "] �� ��ҰǼ�2���̻� ! ";
                      else if (resCode.equals("093"))
                          Msg = "[" + JumunNm + "] �� ���� ! ";

                } else {  /*����*/
                    
                    switch (Integer.parseInt(resCode)) {
                    
                    case 0: Msg = "[" + resCode + "] �� ����";
                        break;
                    case 30: Msg = "[" + resCode + "] ������ ���� (FORMAT ERROR)";
                        break;
                    case 31: Msg = "[" + resCode + "] ������ ���� ���� ����";
                        break;
                    case 91: Msg = "[" + resCode + "] ��Time-Over";
                        break;  
                    case 92: Msg = "[" + resCode + "] �ּ��� �ð� �ƴ�";
                        break;    
                    case 93: Msg = "[" + resCode + "] �ֽý��� ����";
                        break;   
                    case 94: Msg = "[" + resCode + "] ���ش� ��� ��� ���·� ��ȸ �Ұ�";
                        break;      
                    case 111: Msg = "[" + resCode + "] �ְ��� ���� ����";
                        break;      
                    case 112: Msg = "[" + resCode + "] �ֳ��� ���� ����";
                        break;      
                    case 121: Msg = "[" + resCode + "] �ֱ� ��ҵ� ������";
                        break;
                    case 122: Msg = "[" + resCode + "] �ֹ����� �з��ڵ� ����";
                        break;
                    case 123: Msg = "[" + resCode + "] ������ ��ȣ ����";
                        break;      
                    case 124: Msg = "[" + resCode + "] ���ֹ�(�����)��� �ڵ� ����";
                        break;      
                    case 125: Msg = "[" + resCode + "] �ּ����Ǽ� Ʋ��";
                        break;  
                    case 126: Msg = "[" + resCode + "] �ּ����ݾ� Ʋ��";
                        break;
                    case 127: Msg = "[" + resCode + "] �ּ������� Ʋ��";
                        break;
                    case 131: Msg = "[" + resCode + "] �ֳ��γ��� �� �����Ͽ���";
                        break;
                    case 132: Msg = "[" + resCode + "] �ֳ��γ��� �� ����Ͽ���";
                        break;
                    case 191: Msg = "[" + resCode + "] ���̿����� ����";
                        break;  
                    case 201: Msg = "[" + resCode + "] �ּ��� �Ұ�(��ȿ�� ����� ������ �ƴ�)";
                        break;
                    case 311: Msg = "[" + resCode + "] �ְ������� ����";
                        break;
                    case 312: Msg = "[" + resCode + "] �ֳ��γ��� ����";
                        break;
                    case 313: Msg = "[" + resCode + "] �ֳ��� ó�� �� (���� �Ұ�)";
                        break;
                    case 314: Msg = "[" + resCode + "] ����� ó�� �� (���� �Ұ�)";
                        break;  
                    case 315: Msg = "[" + resCode + "] ���ڵ���ü�� (���� �Ұ�)";
                        break;      
                    case 321: Msg = "[" + resCode + "] ����ȸ ���� �Ⱓ ���";
                        break;      
                    case 322: Msg = "[" + resCode + "] ����ȸ �Ⱓ�� ��� ������ �� ���� �Ǽ� �ʰ�";
                        break;  
                    case 323: Msg = "[" + resCode + "] ������ ��ȣ ����";
                        break;
                    case 324: Msg = "[" + resCode + "] ������ ��ȣ ����";
                        break;      
                    case 325: Msg = "[" + resCode + "] ���ش� �̿��� ���� ����";
                        break;  
                    case 331: Msg = "[" + resCode + "] �ֱ� ���� ������";
                        break;      
                    case 332: Msg = "[" + resCode + "] ��(��ȸ) ���ŷ� ����";
                        break;      
                    case 333: Msg = "[" + resCode + "] ���ߺ� ������(������� ���� ��ȣ �ߺ�)";
                        break;
                    case 334: Msg = "[" + resCode + "] �֡����� ó�� �ߡ����� ���� �Ұ�";
                        break;      
                    case 335: Msg = "[" + resCode + "] �֡���� ó�� �ߡ����� ���� �Ұ�";
                        break;      
                    case 336: Msg = "[" + resCode + "] �֡��ڵ���ü�С����� ���� �Ұ�";
                        break;
                    case 337: Msg = "[" + resCode + "] �ְŷ� ����(���� ����) Ʋ��";
                        break;      
                    case 338: Msg = "[" + resCode + "] ��������� ���� �ڵ� Ʋ��";
                        break;  
                    case 339: Msg = "[" + resCode + "] �ֹ����� �з��ڵ� Ʋ��";
                        break;  
                    case 340: Msg = "[" + resCode + "] ���ֹ�(�����)��Ϲ�ȣ Ʋ��";
                        break;  
                    case 341: Msg = "[" + resCode + "] ������(���ͳ�)���ι�ȣ Ʋ��";
                        break;
                    case 342: Msg = "[" + resCode + "] �ְ�������ȣ Ʋ��";
                        break;  
                    case 343: Msg = "[" + resCode + "] �ֳ��� �ݾ� Ʋ��";
                        break;
                    case 344: Msg = "[" + resCode + "] �֡���� ��ü��(��Ÿ ���� ��)�� ���� �Ұ�";
                        break;
                    case 345: Msg = "[" + resCode + "] �ֳ��αݾ�Ʋ�� (�� ���� �ݾ�����)";
                        break;
                    case 360: Msg = "[" + resCode + "] �֡����� ���� ��� �ƴ�";
                        break;
                    case 361: Msg = "[" + resCode + "] �֡�¡���� ���¹�ȣ�� Ʋ��";
                        break;
                    case 362: Msg = "[" + resCode + "] �֡��Ұ����� Ʋ��";
                        break;
                    case 363: Msg = "[" + resCode + "] �֡�ȸ�� �⵵�� Ʋ��";
                        break;
                    case 364: Msg = "[" + resCode + "] �֡����� ���ѡ� ����� ���� �Ұ�";
                        break;
                    case 365: Msg = "[" + resCode + "] �֡����� Ʋ��";
                        break;
                    case 366: Msg = "[" + resCode + "] ���Է� ���� ��������� ���� �Ұ�";
                        break;
                    case 367: Msg = "[" + resCode + "] ��â���� �ش� ����� �̿��Ͽ� ����";
                        break;
                    case 368: Msg = "[" + resCode + "] ���Է� ���� ��������� ��ȸ �Ұ�(���� ����)";
                        break;
                    case 369: Msg = "[" + resCode + "] ���Է� ���� ��������� ���� �Ұ�(���� ����)";
                        break;
                    case 411: Msg = "[" + resCode + "] ����� ��� ���� �ƴ�(���� �뺸 ���� �̼���)";
                        break;
                    case 412: Msg = "[" + resCode + "] ��(����) ���ŷ� ����";
                        break;
                    case 413: Msg = "[" + resCode + "] �ֱ� ��ҵ� �ŷ���";
                        break;
                    case 414: Msg = "[" + resCode + "] �ֿ��ŷ� �ֹ�(�����)��Ϲ�ȣ Ʋ��";
                        break;
                    case 415: Msg = "[" + resCode + "] �ֿ��ŷ� ��� ���� ���� ���� ��ȣ Ʋ��";
                        break;
                    case 416: Msg = "[" + resCode + "] �ֿ��ŷ� ��� ���� ���� ���� �Ͻ� Ʋ��";
                        break;
                    case 417: Msg = "[" + resCode + "] �ֿ��ŷ� ���� �ݾ� Ʋ��";
                        break;
                    case 418: Msg = "[" + resCode + "] �ֿ��ŷ� ��� ���� ��ȣ Ʋ��";
                        break;
                    case 419: Msg = "[" + resCode + "] �ֿ��ŷ� ������� ���� �ڵ� Ʋ��";
                        break;
                    case 420: Msg = "[" + resCode + "] �ֿ��ŷ� ���� �̿� �ý��� Ʋ��";
                        break;  
                    case 421: Msg = "[" + resCode + "] ����� ���� �ȵ�";
                        break;      
                    case 440: Msg = "[" + resCode + "] ��(����) �� ���� �� �� Ʋ��";
                        break;      
                    case 441: Msg = "[" + resCode + "] ��(����) �� ���� �ݾ� Ʋ��";
                        break;      
                    case 442: Msg = "[" + resCode + "] ��(����) �� ���� �� ��, �ݾ� ��� Ʋ��";
                        break;      
                    case 443: Msg = "[" + resCode + "] ��(����) ���� ���� Ʋ��";
                        break;      
                    case 445: Msg = "[" + resCode + "] �����ڳ��ι�ȣ �ڸ��� Ʋ��(�����ι�ȣ Ȯ�ο��)";
                        break;
                    case 446: Msg = "[" + resCode + "] �����ڳ��ι�ȣ ���� Ʋ��(�뷮���ΰ�����ȣ Ȯ�ο��)";
                        break;  
                    case 447: Msg = "[" + resCode + "] ��(�뷮����) �� �����Ǽ� Ʋ��";
                        break;  
                    case 449: Msg = "[" + resCode + "] �ְ����ι�ȣ ���Է� ȸ�� �ѵ� �ʰ�";
                        break;
                    case 450: Msg = "[" + resCode + "] �ּ�ǥ ���� ���� Ʋ��";
                        break;
                    case 451: Msg = "[" + resCode + "] �ְ����ι�ȣ Ʋ��";
                        break;
                    case 470: Msg = "[" + resCode + "] �ֿ��� �Ⱓ ����";
                        break;
                    case 471: Msg = "[" + resCode + "] �ֿ��� ���� ����� �ƴ�";
                        break;
                    case 472: Msg = "[" + resCode + "] �ֱ� ���� ���� (���� �Ұ�)";
                        break;
                    case 473: Msg = "[" + resCode + "] �ֱ� ���� ���� (���� �Ұ�)";
                        break;
                    case 474: Msg = "[" + resCode + "] ���ڵ���ü�� (���� �Ұ�)";
                        break;
                    case 475: Msg = "[" + resCode + "] �ֳ��� �����(���� ����) ����(������ ���� �Ұ�)";
                        break;
                    case 480: Msg = "[" + resCode + "] ����� ���� ���� �ʰ��� ��� �Ұ�";
                        break;
                    case 490: Msg = "[" + resCode + "] �ֿ��೻�� ����";
                        break;
                    case 1000 : Msg = "[" + resCode + "] E ��������(��������,��������,�����ڵ� ����ġ,���������ڵ�)";
                        break;
                    case 1010 : Msg = "[" + resCode + "] E �����ڵ� �����Դϴ�.";
                        break;
                    case 1110 : Msg = "[" + resCode + "] E �����ȸ(010����)�� �����ϳ�, ����ȸ �� ���ΰ� �Ұ����� �����Դϴ�.";
                        break;
                    case 1020 : Msg = "[" + resCode + "] E �����ڵ� �����Դϴ�.";
                        break;
                    case 1030 : Msg = "[" + resCode + "] E �¶��� ���񽺸� �������� �ʴ� �����ڵ��Դϴ�.";
                        break;
                    case 2000 : Msg = "[" + resCode + "] E ����Ű ���� �����Դϴ�.";
                        break;
                    case 3000 : Msg = "[" + resCode + "] E �Ѽ����� Ʋ���ϴ�.";
                        break;
                    case 3010 : Msg = "[" + resCode + "] E �ѱݾ��� Ʋ���ϴ�.";
                        break;
                    case 3020 : Msg = "[" + resCode + "] E ���⳻�ݾ��� Ʋ���ϴ�.";
                        break;
                    case 3030 : Msg = "[" + resCode + "] E �����ıݾ��� Ʋ���ϴ�.";
                        break;
                    case 4000 : Msg = "[" + resCode + "] E ���αݾ��� ���� ���ݾװ� ��ġ���� �ʽ��ϴ�.";
                        break;
                    case 5000 : Msg = "[" + resCode + "] E �̹� ������ �������Դϴ�.";
                        break;
                    case 5010 : Msg = "[" + resCode + "] E ���α����� ���� �������Դϴ�.";
                        break;
                    case 5020 : Msg = "[" + resCode + "] E �����ڷᰡ �����ϴ�.";
                        break;
                    case 5030 : Msg = "[" + resCode + "] E ���ݱ����ڵ�(1���漼,2����,3�����) ����";
                        break;
                    case 5040 : Msg = "[" + resCode + "] E ����ڹ�ȣ ���� (�ش� ����ڹ�ȣ�� �������� �ʽ��ϴ�.)";
                        break;
                    case 5050 : Msg = "[" + resCode + "] E ��ȸ�������ڿ� ��ȸ������ ���� ���� (���γ��� �����ȸ��)";
                        break;
                    case 5060 : Msg = "[" + resCode + "] E ���������� 2���̻� �����մϴ�.";
                        break;
                    case 5100 : Msg = "[" + resCode + "] E ������ü ���� ����ڷ��Դϴ�.";
                        break;
                    case 5510 : Msg = "[" + resCode + "] E ���������ʵ� ����";
                        break;
                    case 5520 : Msg = "[" + resCode + "] E ��¥���� �����Դϴ�.";
                        break;
                    case 5530 : Msg = "[" + resCode + "] E ���⳻���ڰ� ���������ں��� Ů�ϴ�.";
                        break;
                    case 5540 : Msg = "[" + resCode + "] E �ݾ��� '-'�Դϴ�.";
                        break;
                    case 6000 : Msg = "[" + resCode + "] E �ش� ���� ��Ұ� �Ұ����մϴ�.";
                        break;
                    case 6010 : Msg = "[" + resCode + "] E �ش� �����ڷ�� �������� �ʽ��ϴ�.";
                        break;
                    case 7000 : Msg = "[" + resCode + "] E �ڵ���ü ���� ����ڷ��Դϴ�.";
                        break;
                    case 8000 : Msg = "[" + resCode + "] E DB�����Դϴ�.";
                        break;
                    case 9000 : Msg = "[" + resCode + "] E ���������Դϴ�.";
                        break;
                    case 9080 : Msg = "[" + resCode + "] E ���νð��� �ƴմϴ�.";
                        break;
                    case 9090 : Msg = "[" + resCode + "] E �˷����� ���� �����Դϴ�.";
                        break;
                    case 9999 : Msg = "[" + resCode + "] E ���� ����";
                        break;
                        
                    default:
                            Msg = "[" + resCode + "] ? �˼� ���� �ڵ�";
                        break;
                    }
                }
                
            }
            
            return Msg;
        }
        
		
		/*���漼����ڵ�*/
		/*������ �ٻѴ�..���߿� DBó��...*/
		public String BRC_GIRO_NO(String Key){
			
			MapForm mapGiro = new MapForm();

			mapGiro.setMap("000", "1000685"); /*��û    */
			mapGiro.setMap("110", "1005295"); /*�߱�    */
			mapGiro.setMap("140", "1005305"); /*����    */
			mapGiro.setMap("170", "1005318"); /*����    */
			mapGiro.setMap("200", "1005321"); /*������  */
			mapGiro.setMap("230", "1005334"); /*����    */
			mapGiro.setMap("260", "1005431"); /*������  */
			mapGiro.setMap("290", "1005347"); /*����    */
			mapGiro.setMap("320", "1005350"); /*�ϱ�    */
			mapGiro.setMap("350", "1005363"); /*�ؿ�뱸*/
			mapGiro.setMap("380", "1005376"); /*���ϱ�  */
			mapGiro.setMap("410", "1005389"); /*������  */
			mapGiro.setMap("440", "1005392"); /*������  */
			mapGiro.setMap("470", "1005402"); /*������  */
			mapGiro.setMap("500", "1005415"); /*������  */
			mapGiro.setMap("530", "1005428"); /*���  */
			mapGiro.setMap("710", "1005282"); /*���屺  */

			return (String)mapGiro.getMap(Key);
		}
		
		/**
		 * ������ �о Byte[]�� �����Ѵ�.
		 * @param filename    : ���� ���ϸ�(�������)
		 * @param fileLength  : ���� ���ϱ���
		 * @return            : Byte[]
		 * @throws IOException
		 */
		public static byte[] fileToByteArray(String filename, int fileLength) throws IOException {
			InputStream in = null;
			byte[] ba = null;
			try {
				in = new FileInputStream(filename);
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				byte[] b = new byte[fileLength];
				int j;
				while ((j = in.read(b)) != -1) {
					baos.write(b, 0, j);
				}
				ba = baos.toByteArray();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (in != null)
					in.close();
			}
			return ba;
		}

		/**
		 * Byte[]�� �޾Ƽ� ������ �����Ѵ�.
		 * @param b          : byte[] ������
		 * @param filename   : �������ϸ�(�������)
		 * @return           : �������� ����
		 * @throws IOException
		 */
		public static File byteArrayToFile(byte[] b, String filename) throws IOException {
			BufferedOutputStream stream = null;
			File file = new File(filename);
			try {
				FileOutputStream fos = new FileOutputStream(file);
				stream = new BufferedOutputStream(fos);
				stream.write(b);
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (stream != null)
					stream.close();
			}
			return file;
		}
		
		/**
		 * Md5 ���ڵ�
		 * @param strInput
		 * @return
		 */
		public static byte[] Md5Sig(byte[] binData) {
			MessageDigest clsMd5;
			
			try {
				
				clsMd5 = MessageDigest.getInstance("MD5");
				
			} catch (NoSuchAlgorithmException e) {
				
				return null;
			}
			
			clsMd5.update(binData);
			byte[] arrBuf = clsMd5.digest();

            return arrBuf;
		}
		
		public static String Md5String(String strInput) {
			
			MessageDigest clsMd5;
			
			try {
				
				clsMd5 = MessageDigest.getInstance("MD5");
				
			} catch (NoSuchAlgorithmException e) {
				
				return null;
			}
			
			clsMd5.update(strInput.getBytes());
			byte[] arrBuf = clsMd5.digest();
			int iLen = arrBuf.length;
			
			StringBuffer clsBuffer = new StringBuffer();
						
			for(int i = 0 ; i < iLen ; i++) {
				
				clsBuffer.append(String.format("%02X", 0xFF & arrBuf[i]));
			}
			
			return clsBuffer.toString();
			
		}

		/**
		 * ���������̸� ���ϱ� ����..
		 * @param msg
		 * @param mlen
		 * @return
		 */
		public static long transLength(byte[] msg,  int mlen) {
						
		    long len, t_len;
		    int  i, j;

		    len = 0L;

		    for(i = 0; i < mlen; i++ ) {
		    	
		        t_len = (long) msg[i];
		        
		        //unsigned : �����...
		        if(t_len < 0) {
		        	t_len = 256 + t_len;
		        }

		        for( j = 0; j < mlen - i - 1; j++ ) {
		        	t_len = t_len * 256;
		        }
		       
		        len += t_len;
		    }
		    
		    return len ;
		}
		
		public static long c_transLength(char[] msg,  int mlen) {
			
		    long len, t_len;
		    int  i, j;

		    len = 0L;

		    for(i = 0; i < mlen; i++ ) {
		    	
		        t_len = msg[i];
		        
		        //unsigned : �����...
		        if(t_len < 0) {
		        	t_len = - (t_len + t_len);
		        }

		        for( j = 0; j < mlen - i - 1; j++ ) {
		        	t_len = t_len * 256;
		        }
		        
		        len += t_len;
		    }
		    		    
		    return len ;
		}
		/**
		 * 4bytes 10�� ���̸� 2�� 4bytes���̷� ��ȯ�Ѵ�.
		 * @return
		 */
		public static byte[] setOffset(long offset)
		{
			byte[] retbyte = new byte[4];
			
			retbyte[0] = (byte) ((offset / (256*256*256)) & 0xff);
			retbyte[1] = (byte) ((offset / (256*256)) & 0xff);
			retbyte[2] = (byte) ((offset / (256)) & 0xff);
			retbyte[3] = (byte) ((offset) & 0xff);
			
			return retbyte;
		}

		public static char[] c_setOffset(long offset)
		{
			char[] retbyte = new char[4];
			
			retbyte[0] = (char) ((offset / (256*256*256))  & 0xff);
			retbyte[1] = (char) ((offset / (256*256))  & 0xff);
			retbyte[2] = (char) ((offset / (256))  & 0xff);
			retbyte[3] = (char) ((offset)  & 0xff);
			
			return retbyte;
		}
		
		/**
		 * 2bytes 10�� ���̸� 2�� 2bytes���̷� ��ȯ�Ѵ�.
		 * @return
		 */
		public static byte[] setMsgLen(long offset)
		{
			byte[] retbyte = new byte[2];
			retbyte[0] = (byte) ((offset / (256)) & 0xff);
			retbyte[1] = (byte) ((offset) & 0xff);
			
			return retbyte;
		}
		
		/**
		 * �������� ���������� ������ ���̸� ��¥���� ����Ѵ�.
		 * @param first : ��������
		 * @param end   : ��������
		 * @return
		 */
		public static int getDiffDate(String first, String end) {
			
			Calendar cal1 = Calendar.getInstance();
			
			Calendar cal2 = Calendar.getInstance();
			
			SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");			  
			  
			try {
				cal1.setTime(df.parse(first));
			
				cal2.setTime(df.parse(end));
				
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return (int) ((cal2.getTimeInMillis()- cal1.getTimeInMillis())/(1000*60*60*24)); 
			
		}
		
		/**
		 * ���������� : �����ʿ� �����̸� NULL�� ä���.
		 * @param code
		 * @param len
		 * @return
		 */
		public static byte[] rPadByte(byte[] code, int len)
		{
			
			byte[] temp = new byte[len];
			
			byte[] rTemp = code;

			int length = rTemp.length;

			if(length < len)
			{
				for (int i = length; i < len; i++)
				{	
					temp[i] = 00&0xff;				 
				}		
				System.arraycopy(rTemp, 0, temp, 0, length);
				
				return temp;
				
			} else {
				return rTemp;	
			}
		}	 
	
		/**
		 * ���������� : ���ʿ� �����̸� NULL�� ä���.
		 * @param code
		 * @param len
		 * @return
		 */
		public static byte[] lPadByte(byte[] code, int len)
		{
			byte[] temp = new byte[len];
			
			byte[] rTemp = code;

			int length = rTemp.length;
			
			if(length < len)
			{
				for (int i = 0; i < len - length; i++)
				{	
					temp[i] = 00&0xff;	
				}		
				System.arraycopy(rTemp, 0, temp, len - length, length);
				return temp;				
			} else {
				return rTemp;	
			}
		}

		/**
		 * NLLL�����
		 * @param len
		 * @return
		 */
		public static byte[] nullByte(int len)
		{
			byte[] temp = new byte[len];
			
			for (int i = 0; i < len; i++)
			{	
				temp[i] = 00&0xff;
			}		
			
			return temp;
								
		}
		
		/*byte array ��*/
		public static boolean bCompareTo(byte[] des, byte[] src) {
			
			for (int i = 0 ; i < des.length ; i++) {
				
				if(des[i] != src[i]){
					return false;
				}
				
			}
			
			return true;
		}
		
		/*�ð�ǥ��...*/
		public static String formatTime(long lTime) {
			
			int day = 0;
			
	        Calendar c = Calendar.getInstance();
	        
	        if (lTime >= 24 * 60 * 1000 * 60) {
	        	day = (int)(lTime / (24 * 60 * 1000 * 60));
	        	lTime = lTime % (24 * 60 * 1000 * 60);
	        }
	        
	        c.setTimeInMillis(lTime);
	        
	        int hour = (c.get(Calendar.HOUR) - 9);  /*ǥ�ؽ� ���� 9�ð� �����Ƿ�...*/
	        int min = c.get(Calendar.MINUTE);
	        int sec = c.get(Calendar.SECOND);
	        int msec = c.get(Calendar.MILLISECOND);
	        
	    
	        return (((day>0)? day+"�� ":"") + hour + "��(��) " + min + "�� " + sec + "." + msec + "��");
	    
		} 
		
		/**
		    * �ʵ��� Null�� üũ
			* @param    str  üũ�Ϸ��� ���ڿ�
			* @return      str  �ش簪�� NULL�� ��� ���鹮�� ""�� ó��  
			* @author    ������
			*/
			public static String checkNull(String str){
				    
				return (str == null) ? "" : str;
			}
			
		   /**
			* �ʵ��� Null�� üũ
			* @param    str  üũ�Ϸ��� ���ڿ�
			* @return   str  �ش簪�� NULL�� ��� ���鹮�� ""�� ó��  
			* @author   ������
			*/
			public static String checkNullZero(String str){
			    
				return (str == null) ? "0" : str;
			}
			
			 public static String strEncod(String str, String src, String target) throws Exception {
				 
				  return new String(str.getBytes(src), target);

		  }
			 
			public static String getCurrentDate() {
				
				String cDate ="";
				
			    Date date = new Date();
			    SimpleDateFormat sdf = new SimpleDateFormat("yyyymmdd");
			    cDate = sdf.format(date);
				
				return cDate;
			}		 
			 
		
}
