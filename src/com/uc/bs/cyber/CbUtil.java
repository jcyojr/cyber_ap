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
		 * 메쏘드에 대한 설명을 기술해주세요
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
		 * 현재 시스템의 서버IP를 Return 해준다
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
		
		
		/*말일구하기*/
	    public static String EndDay(String sYear, String sMonth) {
	    	
			Calendar cal = Calendar.getInstance(); 
			cal.set(Integer.parseInt(sYear), Integer.parseInt(sMonth)-1, Integer.parseInt("1")); 
			int year = cal.get ( Calendar.YEAR ); 
			int month = cal.get ( Calendar.MONTH )+1 ; 
			int startDay = cal.get(Calendar.DAY_OF_MONTH); 
			int endDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH); 
			
			//첫일
			System.out.println("start day = " + lPadString(String.valueOf(startDay), 2, '0'));
			//말일
			System.out.println("end day = " + lPadString(String.valueOf(endDay), 2, '0'));
			
			return String.valueOf(year) + lPadString(String.valueOf(month), 2, '0') + lPadString(String.valueOf(endDay), 2, '0'); 
	    }
	    
		/*
		 * 시간차이를 계산해서 뿌려줌...
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
		 * 날자를 12자로 표현한다.
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
		 * 현재 FullTime시간을 가져옴
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
		 * 현재 FullTime시간을 가져옴 :: yyyyMMddHHmmssS
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
		 * 납기일
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
		 * 다음일자
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
		  
		/*납기일 구하기*/
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

		        if (BGYMD_Cal.before(SYS_Cal))    // 납기후
		          nap_gubun = "A";
		        else                              // 납기내
		          nap_gubun = "B";

		        System.out.println("납기구분 체크값 : "+nap_gubun);

		    }
		    catch (Exception ex)
		    {
		      System.out.println(ex);
		      ex.printStackTrace();  
		      throw new Exception("※ Err : getNAPGUBUN");
		    }
		    return nap_gubun;
		}
			
		 /**
		  * @납기일자 구하기 토욜+2, 일욜 +1
		  * @param 납기일자
		  * @return B : 납기내 / A : 납기후
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

				      // 일요일
				      if (calendar.get(Calendar.DAY_OF_WEEK) == 1) 
				      {
				        calendar.add ( Calendar.DATE,1);
				        Date tomorrow = calendar.getTime();

				        Napgi_Date = getNAPGI_DATE(tomorrow);
				      }
				      // 토요일
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
		 * @정기수시자납분 납부금액 구하기
		 * @param 납기일자, 본세, 본세가산금, 도시계획세, 도시계획세가산금, 소방공동시설세, 소방가산금,
		 * @param 교육세, 교육가산금, 농특세, 농특가산금
		 */
		public String getNapAmt ( String bymd, String napkiGubun, long bont, long dost, long sobt, long gyot, long nont ) throws Exception
		{
			String rt_NAPBU_AMT = "";      

			try
			{
				if ( napkiGubun.equals("B") )                          // 납기내
					rt_NAPBU_AMT = getNapBfAmt(bont , dost, sobt, gyot, nont);
				else                                                               // 납기후
					rt_NAPBU_AMT = getNapAfAmt(bymd, bont , dost, sobt, gyot, nont);
			}
			catch (Exception ex)
			{
				System.out.println(ex);
				ex.printStackTrace();  
				throw new Exception("※ Err : getNapAmt");
			}

			return rt_NAPBU_AMT;
		}
	  
		/**
		 * 납기내 금액 
		 * @param 본세, 도시계획세, 소방공동시설세, 교육세, 농특세
		 * @return 납기내 금액
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
				throw new Exception("※ Err : getNapBfAmt");
			}
	   
		}  
		
		/**
		 * 납기내 금액 
		 * @param 본세, 본세가산금, 도시계획세, 도시계획세가산금, 소방공동시설세, 소방가산금,
		 * @param 교육세, 교육가산금, 농특세, 농특가산금
		 * @return 납기내 금액
		 */
		public String getNapBfAmt(long bont, long bngt,long dost, long dsgt, long sobt, long sbgt,long gyot, long gogt, long nont, long nngt) throws Exception
		{
			long NAP_BFAMT = 0;
			String rt_NAPBU_AMT = "";

			try
			{
				NAP_BFAMT = (bont + bngt + dost + dsgt + sobt + sbgt + gyot + gogt + nont + nngt);

				rt_NAPBU_AMT = String.valueOf(NAP_BFAMT);

				// 일단위 절사
				int i = 0;
				i = rt_NAPBU_AMT.length();
				rt_NAPBU_AMT = rt_NAPBU_AMT.substring(0,i-1) + "0";
	  
			}
			catch (Exception ex)
			{
				ex.printStackTrace();  
				throw new Exception("※ Err : getNapBfAmt");
			}
			return rt_NAPBU_AMT;
		}	
		
		/**
		 * 납기후 금액 (0.3% 조정 2005-12-19)
		 * @param 납기일자, 본세, 도시계획세, 소방공동시설세, 교육세, 농특세
		 * @return 납기후 금액
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

				// 일단위 절사
				int _i = 0;
				_i = rt_NAPBU_AMT.length();
				rt_NAPBU_AMT = rt_NAPBU_AMT.substring(0,_i-1) + "0";
			}
			catch (Exception ex)
			{
				ex.printStackTrace();  
				throw new Exception("※ Err : getNAP_AFAMT");
			}

			return rt_NAPBU_AMT;  
		}
		/**
		 * 납기후 가산금액 구하기
		 * @param 납기내후 구분, 원금액
		 * @return 가산금액
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
					if (BYMD >= 20051200)        // 가산세율 0.3% 조정 (2005년 12월 부과분 부터)
						gasan_amt_D = AMT * 0.03;
					else                                      // 가산세율 0.5% 기존
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

				// 일단위 절사
				int _i = 0;
				_i = rt_NAPBU_AMT.length();
				rt_NAPBU_AMT = rt_NAPBU_AMT.substring(0,_i-1) + "0";


			}
			catch (Exception ex)
			{
				System.out.println(ex);
				ex.printStackTrace();  
				throw new Exception("※ Err : getGASAN_AMT(String NAPGIGUBUN, String bymd, long AMT) ");
			}

			return Long.parseLong(rt_NAPBU_AMT);
		}
		

		/**
		 * 납기후 가산금액 구하기
		 * @param 납기내후 구분, 원금액
		 * @return 가산금액
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
					if (BYMD >= 20040500)        // 가산세율 0.3% 조정 (2005년 12월 부과분 부터)
						gasan_amt_D = AMT * 0.03;
					else                                      // 가산세율 0.5% 기존
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

				// 일단위 절사
				int _i = 0;
				_i = rt_NAPBU_AMT.length();
				rt_NAPBU_AMT = rt_NAPBU_AMT.substring(0,_i-1) + "0";


			}
			catch (Exception ex)
			{
				System.out.println(ex);
				ex.printStackTrace();  
				throw new Exception("※ Err : getGasanAmtNont ");
			}

			return Long.parseLong(rt_NAPBU_AMT);
		}
		
		/**
		 * 검증번호3 구하기
		 * @param : 납기내총액, 납기후총액
		 * @return : 검증번호3
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
				throw new Exception("※ Err : getGum3");
			}

			return gum3;
		}	

		/**
		 * String 더하기
		 * @param 값
		 * @return 더한값
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
				throw new Exception("※ Err : RetGum");
			}

			return tmp;
	  }
	
	  /**
	  * 검증번호4 구하기
	  * @param 본세, 도시계획세, 공동시설세(+)농특세, 교육세, 납기일
	  * @return 검증번호4
	  */
	  public String getGum4(String bont, String dost, String sobt_nont, String gyot, String gymd) throws Exception
	  {
		  String gum4 = "";
		  long i_temp =0;

		  try
		  {

			  i_temp =          RetGum(bont);        // 회계
			  i_temp = i_temp +  RetGum(dost);       // 과목
			  i_temp = i_temp +  RetGum(sobt_nont);  // 세목
			  i_temp = i_temp +  RetGum(gyot);       // 부과년도
			  i_temp = i_temp +  RetGum(gymd);       // 월
			  gum4 = String.valueOf(10 - (i_temp % 10));
			  gum4 = (gum4.equals("10")) ? "0" : gum4;
		  }
		  catch (Exception ex)
		  {
			  System.out.println(ex);
			  ex.printStackTrace();  
			  throw new Exception("※ Err : getGum4");
		  }

		  return gum4;
	  }
	  
	  /**
	   * @author 신원정보기술(주) 이정옥
	   * @납기 내후 구분
	   * @param 납기일자
	   * @return B : 납기내 / A : 납기후
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

			  if (GYMD_Cal.before(SYS_Cal))    // 납기후
				  nap_gubun = "A";
			  else                             // 납기내
				  nap_gubun = "B";
		  }
		  catch (Exception ex)
		  {
			  System.out.println(ex);
			  ex.printStackTrace();  
			  throw new Exception("※ Err : getNapkiGubun");
		  }
		  return nap_gubun;
	  }	  
	  
	  /**
	   * @author 신원정보기술(주) 이정옥
	   * @체납분 납부금액 구하기
	   * @param 본세, 본세가산금, 도시계획세, 도시계획세가산금, 소방공동시설세, 소방가산금,
	   * @param 교육세, 교육가산금, 농특세, 농특가산금
	   */
	  public String getNapAmt(long bont, long bngt, long dost, long dsgt, long sobt, long sbgt, long gyot, long gogt, long nont, long nngt) throws Exception
	  {

		  String rt_NAPBU_AMT = "";

		  try {
			  rt_NAPBU_AMT = getNapBfAmt(bont, bngt, dost, dsgt, sobt, sbgt, gyot, gogt, nont, nngt);
		  } catch (Exception ex) {
			  System.out.println(ex);
			  ex.printStackTrace();  
			  throw new Exception("※ Err : getNapAmt");
		  }
		  return rt_NAPBU_AMT;
	  }	  
	  
	  
		/**
		 * 상하수도 수용가번호 -> 금융결제원 처리용 수용가번호로 변환
		 * @param type 타입
		 * @param str 수용가번호
		 * @return String 검증번호1
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
		
		/*결재원 수신처리에 대한 메세지를 출력함 Debugging*/
		/*현재는 바빠서 이렇게 하지만 전부 DB처리한다.*/
		public String msgPrint(String Gubun, String Pos, String JumunNm, String resCode){
            
            /** 결과 출력   **/
            String Msg = "";
            
            if(Pos.equals("S")) {
                Msg = "○○○○ [" + JumunNm + "] ○○○○ STARTING >>>";
            } else if (Pos.equals("E")) {
                Msg = "○○○○ [" + JumunNm + "] ○○○○ ENDING >>>";
            } else {
                
                if (Gubun.equals("1")) {
                    
                    if (resCode.equals("000"))
                        Msg = "[" + JumunNm + "] ○ 간략조회 정상 ! ";
                    else if (resCode.equals("339"))
                        Msg = "[" + JumunNm + "] Χ 발행기관분류코드 오류 ! ";
                    else if (resCode.equals("324"))
                        Msg = "[" + JumunNm + "] Χ 지로번호 오류 ! ";
                    else if (resCode.equals("311"))
                        Msg = "[" + JumunNm + "] Χ 고지내역 없음 ! ";
                    else if (resCode.equals("093"))
                        Msg = "[" + JumunNm + "] Χ 오류 ! ";
                    
                } else if (Gubun.equals("2")) {
                
                    if (resCode.equals("000")||resCode.equals("0000"))
                        Msg = "[" + JumunNm + "] ○ 상세조회 정상 ! ";
                    else if (resCode.equals("339")||resCode.equals("1000"))
                        Msg = "[" + JumunNm + "] Χ 발행기관분류코드 오류 ! ";
                    else if (resCode.equals("324"))
                        Msg = "[" + JumunNm + "] Χ 지로번호 오류 ! ";
                    else if (resCode.equals("311")||resCode.equals("5020"))
                        Msg = "[" + JumunNm + "] Χ 고지내역 없음 ! ";
                    else if (resCode.equals("341")||resCode.equals("2000"))
                        Msg = "[" + JumunNm + "] Χ 전자납부번호 틀림 ! ";
                    else if (resCode.equals("340"))
                        Msg = "[" + JumunNm + "] Χ 주민등록번호 확인 ! ";
                    else if (resCode.equals("093")||resCode.equals("9090"))
                        Msg = "[" + JumunNm + "] Χ 에러 ! ";
                    else if (resCode.equals("094")||resCode.equals("3000")||resCode.equals("5060"))
                        Msg = "[" + JumunNm + "] Χ 과세건수2건이상 ! ";
                    
                } else if (Gubun.equals("3")) {
                    
                    if (resCode.equals("000")||resCode.equals("0000"))
                        Msg = "[" + JumunNm + "] ○ 납부완료 ! ";
                    else if (resCode.equals("339")||resCode.equals("1000"))
                        Msg = "[" + JumunNm + "] Χ 발행기관분류코드 오류 ! ";
                    else if (resCode.equals("340"))
                        Msg = "[" + JumunNm + "] Χ 주민등록번호 오류 ! ";
                    else if (resCode.equals("341")||resCode.equals("2000"))
                        Msg = "[" + JumunNm + "] Χ 전자납부번호 오류 ! ";
                    else if (resCode.equals("324"))
                        Msg = "[" + JumunNm + "] Χ 지로번호 오류 ! ";
                    else if (resCode.equals("311")||resCode.equals("5020"))
                        Msg = "[" + JumunNm + "] Χ 고지내역 없음 ! ";
                    else if (resCode.equals("343")||resCode.equals("4000"))
                        Msg = "[" + JumunNm + "] Χ 납부금액 틀림 ! ";
                    else if (resCode.equals("331")||resCode.equals("5000"))
                        Msg = "[" + JumunNm + "] Χ 납부기수신 ! ";
                    else if (resCode.equals("093"))
                        Msg = "[" + JumunNm + "] Χ 오류 ! ";
                    
                } else if (Gubun.equals("4")) {
                    
                    if (resCode.equals("000"))
                        Msg = "[" + JumunNm + "]  ○ 납부내역 간략조회 정상 ! ";
                    else if (resCode.equals("339"))
                        Msg = "[" + JumunNm + "]  Χ 발행기관분류코드 오류 ! ";
                    else if (resCode.equals("324"))
                        Msg = "[" + JumunNm + "]  Χ 지로번호 오류 ! ";
                    else if (resCode.equals("312"))
                        Msg = "[" + JumunNm + "]  Χ 납부내역 없음 ! ";
                    else if (resCode.equals("093"))
                        Msg = "[" + JumunNm + "]  Χ 오류 ! ";
                    
                } else if (Gubun.equals("5")) {
                    
                    if (resCode.equals("000"))
                        Msg = "[" + JumunNm + "] ○ 납부내역 상세조회 정상 ! ";
                    else if (resCode.equals("339"))
                        Msg = "[" + JumunNm + "] Χ 발행기관분류코드 오류 ! ";
                    else if (resCode.equals("324"))
                        Msg = "[" + JumunNm + "] Χ 지로번호 오류 ! ";
                    else if (resCode.equals("312"))
                        Msg = "[" + JumunNm + "] Χ 납부내역 없음 ! ";
                    else if (resCode.equals("341"))
                        Msg = "[" + JumunNm + "] Χ 전자납부번호 틀림 ! ";
                    else if (resCode.equals("093"))
                        Msg = "[" + JumunNm + "] Χ 오류 ! ";
                    else if (resCode.equals("094"))
                        Msg = "[" + JumunNm + "] Χ 과세건수2건이상 ! ";
                    
                } else if (Gubun.equals("6")) {  /* 지방세 고지내역 간략조회 */
                    
                    if (resCode.equals("000"))
                        Msg = "[" + JumunNm + "] ○ 간략조회 정상 ! ";
                    else if (resCode.equals("122"))
                        Msg = "[" + JumunNm + "] Χ 발행기관분류코드 오류 ! ";
                    else if (resCode.equals("123"))
                        Msg = "[" + JumunNm + "] Χ 지로번호 오류 ! ";
                    else if (resCode.equals("111"))
                        Msg = "[" + JumunNm + "] Χ 고지내역 없음 ! ";
                    else if (resCode.equals("093"))
                        Msg = "[" + JumunNm + "] Χ 에러 ! ";

                } else if (Gubun.equals("7")) {  /* 지방세 고지내역 상세조회 */
                    
                    if (resCode.equals("000")||resCode.equals("0000"))
                        Msg = "[" + JumunNm + "] ○ 상세조회 정상 ! ";
                    else if (resCode.equals("122")||resCode.equals("1000"))
                        Msg = "[" + JumunNm + "] Χ 발행기관분류코드 오류 ! ";
                    else if (resCode.equals("123"))
                        Msg = "[" + JumunNm + "] Χ 지로번호 오류 ! ";
                    else if (resCode.equals("111")||resCode.equals("5020"))
                        Msg = "[" + JumunNm + "] Χ 고지내역 없음 ! ";
                    else if (resCode.equals("341")||resCode.equals("2000"))
                        Msg = "[" + JumunNm + "] Χ 전자납부번호 틀림 ! ";
                    else if (resCode.equals("093")||resCode.equals("9090"))
                        Msg = "[" + JumunNm + "] Χ 에러 ! ";

                } else if (Gubun.equals("8")) {  /* 지방세 납부내역 간략조회 */
                    
                    if (resCode.equals("000"))
                        Msg = "[" + JumunNm + "] ○ 납부내역 간략조회 정상 ! ";
                    else if (resCode.equals("122"))
                        Msg = "[" + JumunNm + "] Χ 발행기관분류코드 오류 ! ";
                    else if (resCode.equals("340"))
                        Msg = "[" + JumunNm + "] Χ 주민등록번호 오류 ! ";
                    else if (resCode.equals("123"))
                        Msg = "[" + JumunNm + "] Χ 지로번호 오류 ! ";
                    else if (resCode.equals("112"))
                        Msg = "[" + JumunNm + "] Χ 납부내역 없음 ! ";
                    else if (resCode.equals("093"))
                        Msg = "[" + JumunNm + "] Χ 에러 ! ";
                    
                } else if (Gubun.equals("9")) {  /* 지방세 납부내역 상세조회 */
                    
                    if (resCode.equals("000"))
                        Msg = "[" + JumunNm + "] ○ 납부내역 상세조회 정상 ! ";
                    else if (resCode.equals("122"))
                        Msg = "[" + JumunNm + "] Χ 발행기관분류코드 오류 ! ";
                    else if (resCode.equals("123"))
                        Msg = "[" + JumunNm + "] Χ 지로번호 오류 ! ";
                    else if (resCode.equals("112"))
                        Msg = "[" + JumunNm + "] Χ 납부내역 없음 ! ";
                    else if (resCode.equals("341"))
                        Msg = "[" + JumunNm + "] Χ 전자납부번호 틀림 ! ";
                    else if (resCode.equals("094"))
                        Msg = "[" + JumunNm + "] Χ 납부건수2건이상 ! ";
                    else if (resCode.equals("093"))
                        Msg = "[" + JumunNm + "] Χ 오류 ! ";
                    
                } else if (Gubun.equals("10")) {  /* 지방세 납부재취소 */
                    
                    if (resCode.equals("000"))
                        Msg = "[" + JumunNm + "] ○ 납부취소 완료! ";
                    else if (resCode.equals("339"))
                        Msg = "[" + JumunNm + "] Χ 발행기관분류코드 오류 ! ";
                    else if (resCode.equals("324"))
                        Msg = "[" + JumunNm + "] Χ 지로번호 오류 ! ";
                    else if (resCode.equals("412"))
                        Msg = "[" + JumunNm + "] Χ 납부내역 없음! ";
                    else if (resCode.equals("413"))
                        Msg = "[" + JumunNm + "] Χ 납부내역 기취소하였음! ";
                    else if (resCode.equals("417"))
                        Msg = "[" + JumunNm + "] Χ 납부금액 틀림! ";
                    else if (resCode.equals("093"))
                        Msg = "[" + JumunNm + "] Χ 에러! ";
                    
                } else if (Gubun.equals("11")) {  /*납부취소*/
                    
                      if (resCode.equals("000"))
                          Msg = "[" + JumunNm + "] ○ 납부취소 완료 ! ";
                      else if (resCode.equals("112"))
                          Msg = "[" + JumunNm + "] Χ 납부내역 없음 ! ";
                      else if (resCode.equals("312"))
                          Msg = "[" + JumunNm + "] Χ 납부내역 없음 ! (타기관 납부내역임-취소불가능)";
                      else if (resCode.equals("132"))
                          Msg = "[" + JumunNm + "] Χ 납부내역 기취소하였음 ! ";
                      else if (resCode.equals("411"))
                          Msg = "[" + JumunNm + "] Χ 취소대상 내역 아님 ! ";
                      else if (resCode.equals("417"))
                          Msg = "[" + JumunNm + "] Χ 원거래 지로대금납부금액 틀림 ! ";
                      else if (resCode.equals("094"))
                          Msg = "[" + JumunNm + "] Χ 취소건수2건이상 ! ";
                      else if (resCode.equals("093"))
                          Msg = "[" + JumunNm + "] Χ 오류 ! ";

                } else {  /*공통*/
                    
                    switch (Integer.parseInt(resCode)) {
                    
                    case 0: Msg = "[" + resCode + "] ○ 정상";
                        break;
                    case 30: Msg = "[" + resCode + "] Χ전문 오류 (FORMAT ERROR)";
                        break;
                    case 31: Msg = "[" + resCode + "] Χ전문 전송 일자 오류";
                        break;
                    case 91: Msg = "[" + resCode + "] ΧTime-Over";
                        break;  
                    case 92: Msg = "[" + resCode + "] Χ서비스 시간 아님";
                        break;    
                    case 93: Msg = "[" + resCode + "] Χ시스템 오류";
                        break;   
                    case 94: Msg = "[" + resCode + "] Χ해당 기관 장애 상태로 조회 불가";
                        break;      
                    case 111: Msg = "[" + resCode + "] Χ고지 내역 없음";
                        break;      
                    case 112: Msg = "[" + resCode + "] Χ납부 내역 없음";
                        break;      
                    case 121: Msg = "[" + resCode + "] Χ기 취소된 내역임";
                        break;
                    case 122: Msg = "[" + resCode + "] Χ발행기관 분류코드 오류";
                        break;
                    case 123: Msg = "[" + resCode + "] Χ지로 번호 오류";
                        break;      
                    case 124: Msg = "[" + resCode + "] Χ주민(사업자)등록 코드 오류";
                        break;      
                    case 125: Msg = "[" + resCode + "] Χ수납건수 틀림";
                        break;  
                    case 126: Msg = "[" + resCode + "] Χ수납금액 틀림";
                        break;
                    case 127: Msg = "[" + resCode + "] Χ수납일자 틀림";
                        break;
                    case 131: Msg = "[" + resCode + "] Χ납부내역 기 수신하였음";
                        break;
                    case 132: Msg = "[" + resCode + "] Χ납부내역 기 취소하였음";
                        break;
                    case 191: Msg = "[" + resCode + "] Χ이용기관에 문의";
                        break;  
                    case 201: Msg = "[" + resCode + "] Χ서비스 불가(유효한 사용자 정보가 아님)";
                        break;
                    case 311: Msg = "[" + resCode + "] Χ고지내역 없음";
                        break;
                    case 312: Msg = "[" + resCode + "] Χ납부내역 없음";
                        break;
                    case 313: Msg = "[" + resCode + "] Χ납부 처리 중 (납부 불가)";
                        break;
                    case 314: Msg = "[" + resCode + "] Χ취소 처리 중 (납부 불가)";
                        break;  
                    case 315: Msg = "[" + resCode + "] Χ자동이체분 (납부 불가)";
                        break;      
                    case 321: Msg = "[" + resCode + "] Χ조회 가능 기간 경과";
                        break;      
                    case 322: Msg = "[" + resCode + "] Χ조회 기간내 허용 가능한 총 납부 건수 초과";
                        break;  
                    case 323: Msg = "[" + resCode + "] Χ지정 번호 오류";
                        break;
                    case 324: Msg = "[" + resCode + "] Χ지로 번호 오류";
                        break;      
                    case 325: Msg = "[" + resCode + "] Χ해당 이용기관 정보 없음";
                        break;  
                    case 331: Msg = "[" + resCode + "] Χ기 납부 내역임";
                        break;      
                    case 332: Msg = "[" + resCode + "] Χ(조회) 원거래 없음";
                        break;      
                    case 333: Msg = "[" + resCode + "] Χ중복 납부임(출금은행 전문 번호 중복)";
                        break;
                    case 334: Msg = "[" + resCode + "] Χ‘납부 처리 중’으로 납부 불가";
                        break;      
                    case 335: Msg = "[" + resCode + "] Χ‘취소 처리 중’으로 납부 불가";
                        break;      
                    case 336: Msg = "[" + resCode + "] Χ‘자동이체분’으로 납부 불가";
                        break;
                    case 337: Msg = "[" + resCode + "] Χ거래 일자(수납 일자) 틀림";
                        break;      
                    case 338: Msg = "[" + resCode + "] Χ출금은행 점별 코드 틀림";
                        break;  
                    case 339: Msg = "[" + resCode + "] Χ발행기관 분류코드 틀림";
                        break;  
                    case 340: Msg = "[" + resCode + "] Χ주민(사업자)등록번호 틀림";
                        break;  
                    case 341: Msg = "[" + resCode + "] Χ전자(인터넷)납부번호 틀림";
                        break;
                    case 342: Msg = "[" + resCode + "] Χ고객관리번호 틀림";
                        break;  
                    case 343: Msg = "[" + resCode + "] Χ납부 금액 틀림";
                        break;
                    case 344: Msg = "[" + resCode + "] Χ‘장기 연체자(기타 사유 등)’ 납부 불가";
                        break;
                    case 345: Msg = "[" + resCode + "] Χ납부금액틀림 (원 단위 금액포함)";
                        break;
                    case 360: Msg = "[" + resCode + "] Χ‘자진 납부 대상’ 아님";
                        break;
                    case 361: Msg = "[" + resCode + "] Χ‘징수관 계좌번호’ 틀림";
                        break;
                    case 362: Msg = "[" + resCode + "] Χ‘소계정’ 틀림";
                        break;
                    case 363: Msg = "[" + resCode + "] Χ‘회계 년도’ 틀림";
                        break;
                    case 364: Msg = "[" + resCode + "] Χ‘납부 기한’ 경과로 수납 불가";
                        break;
                    case 365: Msg = "[" + resCode + "] Χ‘세목’ 틀림";
                        break;
                    case 366: Msg = "[" + resCode + "] Χ입력 내역 불충분으로 수납 불가";
                        break;
                    case 367: Msg = "[" + resCode + "] Χ창구나 해당 기관을 이용하여 납부";
                        break;
                    case 368: Msg = "[" + resCode + "] Χ입력 내역 불충분으로 조회 불가(연대 납부)";
                        break;
                    case 369: Msg = "[" + resCode + "] Χ입력 내역 불충분으로 수납 불가(연대 납부)";
                        break;
                    case 411: Msg = "[" + resCode + "] Χ취소 대상 내역 아님(납부 통보 전문 미수신)";
                        break;
                    case 412: Msg = "[" + resCode + "] Χ(납부) 원거래 없음";
                        break;
                    case 413: Msg = "[" + resCode + "] Χ기 취소된 거래임";
                        break;
                    case 414: Msg = "[" + resCode + "] Χ원거래 주민(사업자)등록번호 틀림";
                        break;
                    case 415: Msg = "[" + resCode + "] Χ원거래 출금 은행 전문 관리 번호 틀림";
                        break;
                    case 416: Msg = "[" + resCode + "] Χ원거래 출금 은행 전문 전송 일시 틀림";
                        break;
                    case 417: Msg = "[" + resCode + "] Χ원거래 납부 금액 틀림";
                        break;
                    case 418: Msg = "[" + resCode + "] Χ원거래 출금 계좌 번호 틀림";
                        break;
                    case 419: Msg = "[" + resCode + "] Χ원거래 출금은행 점별 코드 틀림";
                        break;
                    case 420: Msg = "[" + resCode + "] Χ원거래 납부 이용 시스템 틀림";
                        break;  
                    case 421: Msg = "[" + resCode + "] Χ취소 서비스 안됨";
                        break;      
                    case 440: Msg = "[" + resCode + "] Χ(집계) 총 납부 건 수 틀림";
                        break;      
                    case 441: Msg = "[" + resCode + "] Χ(집계) 총 납부 금액 틀림";
                        break;      
                    case 442: Msg = "[" + resCode + "] Χ(집계) 총 납부 건 수, 금액 모두 틀림";
                        break;      
                    case 443: Msg = "[" + resCode + "] Χ(집계) 수납 일자 틀림";
                        break;      
                    case 445: Msg = "[" + resCode + "] Χ전자납부번호 자리수 틀림(간편납부번호 확인요망)";
                        break;
                    case 446: Msg = "[" + resCode + "] Χ전자납부번호 형식 틀림(대량납부고유번호 확인요망)";
                        break;  
                    case 447: Msg = "[" + resCode + "] Χ(대량납부) 총 고지건수 틀림";
                        break;  
                    case 449: Msg = "[" + resCode + "] Χ간편납부번호 오입력 회수 한도 초과";
                        break;
                    case 450: Msg = "[" + resCode + "] Χ수표 포함 여부 틀림";
                        break;
                    case 451: Msg = "[" + resCode + "] Χ간편납부번호 틀림";
                        break;
                    case 470: Msg = "[" + resCode + "] Χ예약 기간 오류";
                        break;
                    case 471: Msg = "[" + resCode + "] Χ예약 가능 기관이 아님";
                        break;
                    case 472: Msg = "[" + resCode + "] Χ기 예약 내역 (예약 불가)";
                        break;
                    case 473: Msg = "[" + resCode + "] Χ기 납부 내역 (예약 불가)";
                        break;
                    case 474: Msg = "[" + resCode + "] Χ자동이체분 (예약 불가)";
                        break;
                    case 475: Msg = "[" + resCode + "] Χ납부 희망일(예약 일자) 오류(공휴일 예약 불가)";
                        break;
                    case 480: Msg = "[" + resCode + "] Χ취소 가능 기한 초과로 취소 불가";
                        break;
                    case 490: Msg = "[" + resCode + "] Χ예약내역 없음";
                        break;
                    case 1000 : Msg = "[" + resCode + "] E 전문에러(전문길이,전문일자,은행코드 불일치,전문구분코드)";
                        break;
                    case 1010 : Msg = "[" + resCode + "] E 세목코드 오류입니다.";
                        break;
                    case 1110 : Msg = "[" + resCode + "] E 목록조회(010전문)는 가능하나, 상세조회 및 납부가 불가능한 세목입니다.";
                        break;
                    case 1020 : Msg = "[" + resCode + "] E 은행코드 오류입니다.";
                        break;
                    case 1030 : Msg = "[" + resCode + "] E 온라인 서비스를 시행하지 않는 은행코드입니다.";
                        break;
                    case 2000 : Msg = "[" + resCode + "] E 과세키 조합 오류입니다.";
                        break;
                    case 3000 : Msg = "[" + resCode + "] E 총수량이 틀립니다.";
                        break;
                    case 3010 : Msg = "[" + resCode + "] E 총금액이 틀립니다.";
                        break;
                    case 3020 : Msg = "[" + resCode + "] E 납기내금액이 틀립니다.";
                        break;
                    case 3030 : Msg = "[" + resCode + "] E 납기후금액이 틀립니다.";
                        break;
                    case 4000 : Msg = "[" + resCode + "] E 납부금액이 납부 대상금액과 일치하지 않습니다.";
                        break;
                    case 5000 : Msg = "[" + resCode + "] E 이미 수납된 고지분입니다.";
                        break;
                    case 5010 : Msg = "[" + resCode + "] E 납부기한이 지난 고지분입니다.";
                        break;
                    case 5020 : Msg = "[" + resCode + "] E 고지자료가 없습니다.";
                        break;
                    case 5030 : Msg = "[" + resCode + "] E 세금구분코드(1지방세,2세외,3상수도) 오류";
                        break;
                    case 5040 : Msg = "[" + resCode + "] E 사업자번호 오류 (해당 사업자번호가 존재하지 않습니다.)";
                        break;
                    case 5050 : Msg = "[" + resCode + "] E 조회시작일자와 조회끝일자 범위 오류 (납부내역 목록조회시)";
                        break;
                    case 5060 : Msg = "[" + resCode + "] E 고지내역이 2건이상 존재합니다.";
                        break;
                    case 5100 : Msg = "[" + resCode + "] E 예약이체 납부 대상자료입니다.";
                        break;
                    case 5510 : Msg = "[" + resCode + "] E 고지구분필드 오류";
                        break;
                    case 5520 : Msg = "[" + resCode + "] E 날짜형식 오류입니다.";
                        break;
                    case 5530 : Msg = "[" + resCode + "] E 납기내일자가 납기후일자보다 큽니다.";
                        break;
                    case 5540 : Msg = "[" + resCode + "] E 금액이 '-'입니다.";
                        break;
                    case 6000 : Msg = "[" + resCode + "] E 해당 기기는 취소가 불가능합니다.";
                        break;
                    case 6010 : Msg = "[" + resCode + "] E 해당 수납자료는 존재하지 않습니다.";
                        break;
                    case 7000 : Msg = "[" + resCode + "] E 자동이체 납부 대상자료입니다.";
                        break;
                    case 8000 : Msg = "[" + resCode + "] E DB에러입니다.";
                        break;
                    case 9000 : Msg = "[" + resCode + "] E 서버에러입니다.";
                        break;
                    case 9080 : Msg = "[" + resCode + "] E 납부시간이 아닙니다.";
                        break;
                    case 9090 : Msg = "[" + resCode + "] E 알려지지 않은 에러입니다.";
                        break;
                    case 9999 : Msg = "[" + resCode + "] E 서비스 중지";
                        break;
                        
                    default:
                            Msg = "[" + resCode + "] ? 알수 없는 코드";
                        break;
                    }
                }
                
            }
            
            return Msg;
        }
        
		
		/*지방세기관코드*/
		/*지금은 바뿌다..나중에 DB처리...*/
		public String BRC_GIRO_NO(String Key){
			
			MapForm mapGiro = new MapForm();

			mapGiro.setMap("000", "1000685"); /*시청    */
			mapGiro.setMap("110", "1005295"); /*중구    */
			mapGiro.setMap("140", "1005305"); /*서구    */
			mapGiro.setMap("170", "1005318"); /*동구    */
			mapGiro.setMap("200", "1005321"); /*영도구  */
			mapGiro.setMap("230", "1005334"); /*진구    */
			mapGiro.setMap("260", "1005431"); /*동래구  */
			mapGiro.setMap("290", "1005347"); /*남구    */
			mapGiro.setMap("320", "1005350"); /*북구    */
			mapGiro.setMap("350", "1005363"); /*해운대구*/
			mapGiro.setMap("380", "1005376"); /*사하구  */
			mapGiro.setMap("410", "1005389"); /*금정구  */
			mapGiro.setMap("440", "1005392"); /*강서구  */
			mapGiro.setMap("470", "1005402"); /*연제구  */
			mapGiro.setMap("500", "1005415"); /*수영구  */
			mapGiro.setMap("530", "1005428"); /*사상구  */
			mapGiro.setMap("710", "1005282"); /*기장군  */

			return (String)mapGiro.getMap(Key);
		}
		
		/**
		 * 파일을 읽어서 Byte[]에 저장한다.
		 * @param filename    : 읽을 파일명(경로포함)
		 * @param fileLength  : 읽을 파일길이
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
		 * Byte[]를 받아서 파일을 생성한다.
		 * @param b          : byte[] 데이터
		 * @param filename   : 생성파일명(경로포함)
		 * @return           : 생성파일 정보
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
		 * Md5 인코딩
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
		 * 실전문길이를 구하기 위함..
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
		        
		        //unsigned : 양수만...
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
		        
		        //unsigned : 양수만...
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
		 * 4bytes 10진 길이를 2진 4bytes길이로 변환한다.
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
		 * 2bytes 10진 길이를 2진 2bytes길이로 변환한다.
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
		 * 시작일자 종료일자의 길이의 차이를 날짜수로 계산한다.
		 * @param first : 시작일자
		 * @param end   : 종료일자
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
		 * 전문조립용 : 오른쪽에 공백이면 NULL을 채운다.
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
		 * 전문조립용 : 왼쪽에 공백이면 NULL을 채운다.
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
		 * NLLL만들기
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
		
		/*byte array 비교*/
		public static boolean bCompareTo(byte[] des, byte[] src) {
			
			for (int i = 0 ; i < des.length ; i++) {
				
				if(des[i] != src[i]){
					return false;
				}
				
			}
			
			return true;
		}
		
		/*시간표시...*/
		public static String formatTime(long lTime) {
			
			int day = 0;
			
	        Calendar c = Calendar.getInstance();
	        
	        if (lTime >= 24 * 60 * 1000 * 60) {
	        	day = (int)(lTime / (24 * 60 * 1000 * 60));
	        	lTime = lTime % (24 * 60 * 1000 * 60);
	        }
	        
	        c.setTimeInMillis(lTime);
	        
	        int hour = (c.get(Calendar.HOUR) - 9);  /*표준시 보다 9시간 빠르므로...*/
	        int min = c.get(Calendar.MINUTE);
	        int sec = c.get(Calendar.SECOND);
	        int msec = c.get(Calendar.MILLISECOND);
	        
	    
	        return (((day>0)? day+"일 ":"") + hour + "시(간) " + min + "분 " + sec + "." + msec + "초");
	    
		} 
		
		/**
		    * 필드의 Null값 체크
			* @param    str  체크하려는 문자열
			* @return      str  해당값이 NULL일 경우 공백문자 ""로 처리  
			* @author    김혜란
			*/
			public static String checkNull(String str){
				    
				return (str == null) ? "" : str;
			}
			
		   /**
			* 필드의 Null값 체크
			* @param    str  체크하려는 문자열
			* @return   str  해당값이 NULL일 경우 공백문자 ""로 처리  
			* @author   김혜란
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
