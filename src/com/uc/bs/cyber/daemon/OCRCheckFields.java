/**
 * OCR_BAND_CHECK_FIELD
 */
package com.uc.bs.cyber.daemon;

/**
 * @author Administrator
 *
 */
public class OCRCheckFields {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		String OCR_org = args[0];
		String OCR_dest = "";
		String OCR_comp = "";
		int[] ocrcnt= null;
		int[] chkbitcnt= null;
		String[] ocrstr= null;
		String[] ocrband=null;
		
		OCRCheckFields ocrcf = new OCRCheckFields();

		int kind = 0;//0:표준장표, 1:구장표, 2:신장표
		if(!OCR_org.startsWith("26"))kind=2;
		else if(OCR_org.endsWith("+++"))kind=1;

		if(kind==0) ocrcnt= new int[]{2,3,1,2,3,3,4,2,1,3,6,1,11,11,1,11,10,10,11,8,1,1,1,1};
		if(kind==1) ocrcnt= new int[]{2,3,1,2,3,3,4,2,1,3,6,1,11,11,1,11,10,10,11,8,1,1,1,1};
		if(kind==2) ocrcnt= new int[]{11,4,2,6,6,2,1,1,11,8,1,1,10,2,10,2,10,2,1,2,1,12,1,1};
		
		if(kind==0) chkbitcnt= new int[]{2,11,14,20,23};
		if(kind==1) chkbitcnt= new int[]{2,11,14,20};
		if(kind==2) chkbitcnt= new int[]{6,11,18,22,23};
		
		if(kind==0) ocrstr= new String[]{"시도코드","구청코드","검1","회계코드","과목코드","세목코드","부과년도","부과월","기분코드","행정동코드","과세번호","검2","납기내총액","납기후총액","검3","본세","도시계획세","공동시설세(농특세)","지방교육세","납기일","검4","필러(7)","수납구분","검5"};
		if(kind==1) ocrstr= new String[]{"시도코드","구청코드","검1","회계코드","과목코드","세목코드","부과년도","부과월","기분코드","행정동코드","과세번호","검2","납기내총액","납기후총액","검3","본세","도시계획세","공동시설세(농특세)","지방교육세","납기일","검4","필러(+)","필러(+)","필러(+)"};
		if(kind==2) ocrstr= new String[]{"부서코드","부과년도","회계코드","세목코드","과세번호","순번","검1","고지구분","납기내총액","납기내일자","부가가치세구분","검2","국세","국세요율","시도세","시도세요율","시군구세","시군구세요율","검3","부과월","납기후처리","납기후총액","검4","검5"};
		
		ocrband=new String[ocrcnt.length];
		
		for(int j=0;j<ocrcnt.length;j++)
		{
			int sumj=0;
			for(int k=0;k<=j;k++)sumj+=ocrcnt[k];
			ocrband[j]=OCR_org.substring(sumj-ocrcnt[j],sumj);
		}
		
		if(kind==0)for(int i=0;i<chkbitcnt.length;i++)ocrband[chkbitcnt[i]]=ocrcf.checkBitStd(ocrband, i+1);
		if(kind==1)for(int i=0;i<chkbitcnt.length;i++)ocrband[chkbitcnt[i]]=ocrcf.checkBitOld(ocrband, i+1);
		if(kind==2)for(int i=0;i<chkbitcnt.length;i++)ocrband[chkbitcnt[i]]=ocrcf.checkBitNew(ocrband, i+1);

		for(int i=0;i<ocrband.length;i++)OCR_dest+=ocrband[i];

		for(int i=0;i<OCR_org.length();i++)
		{
			if(OCR_org.substring(i,i+1).equals(OCR_dest.substring(i,i+1)))OCR_comp+=" ";
			else OCR_comp+="X";
		}

		//화면출력 start
		System.out.println("OCR원본  ["+OCR_org+"]");
		System.out.println("OCR보정  ["+OCR_dest+"]");
		System.out.println("OCR비교  ["+OCR_comp+"]");
		
		for(int j=0;j<ocrcnt.length;j++)
		{
			int sumj=0;
			for(int k=0;k<=j;k++)sumj+=ocrcnt[k];
			ocrband[j]=OCR_dest.substring(sumj-ocrcnt[j],sumj);
		}
		for(int i=0;i<ocrband.length;i++)System.out.println("["+((char)(65+i))+" "+ocrstr[i]+":"+ocrcnt[i]+"] "+ocrband[i]);
		//화면출력 end
		
	}
	

	/*표준장표 : 지방세*/
	public String checkBitStd(String[] ocrband, int checkbit)
	{
		int sum = 0;
		String str = "";
		String rtn = "";
		
		if(checkbit==1)
		{
			for(int i=0; i<=1; i++)str += ocrband[i];
			for(int i=0; i<str.length();i++)
			{
				switch(i%3)
				{
					case 0: sum += Integer.parseInt(str.substring(i,i+1)) * 3; break;
					case 1: sum += Integer.parseInt(str.substring(i,i+1)) * 7; break;
					case 2: sum += Integer.parseInt(str.substring(i,i+1)) * 1; break;
				}
			}
			rtn = new Integer((10-(sum%10))%10).toString();
		}
		
		if(checkbit==2)
		{
			for(int i=3; i<=10; i++)str += ocrband[i];
			for(int i=0; i<str.length();i++)
			{
				switch(i%2)
				{
					case 0: sum += Integer.parseInt(str.substring(i,i+1)) * 1; break;
					case 1: sum += Integer.parseInt(str.substring(i,i+1)) * 2; break;
				}
			}
			rtn = new Integer((10-(sum%10))%10).toString();
		}
		
		if(checkbit==3)
		{
			for(int i=12; i<=13; i++)str += ocrband[i];
			for(int i=0; i<str.length();i++)
			{
				switch(i%3)
				{
					case 0: sum += Integer.parseInt(str.substring(i,i+1)) * 7; break;
					case 1: sum += Integer.parseInt(str.substring(i,i+1)) * 1; break;
					case 2: sum += Integer.parseInt(str.substring(i,i+1)) * 3; break;
				}
			}
			rtn = new Integer((10-(sum%10))%10).toString();
		}
		
		if(checkbit==4)
		{
			for(int i=15; i<=19; i++)str += ocrband[i];
			for(int i=0; i<str.length();i++)
			{
				switch(i%3)
				{
					case 0: sum += Integer.parseInt(str.substring(i,i+1)) * 3; break;
					case 1: sum += Integer.parseInt(str.substring(i,i+1)) * 7; break;
					case 2: sum += Integer.parseInt(str.substring(i,i+1)) * 1; break;
				}
			}
			rtn = new Integer((10-(sum%10))%10).toString();
		}
		
		if(checkbit==5)
		{
			for(int i=0; i<=20; i++)str += ocrband[i];
			for(int i=0; i<str.length();i++)
			{
				switch(i%4)
				{
					case 0: sum += Integer.parseInt(str.substring(i,i+1)) * 2; break;
					case 1: sum += Integer.parseInt(str.substring(i,i+1)) * 3; break;
					case 2: sum += Integer.parseInt(str.substring(i,i+1)) * 5; break;
					case 3: sum += Integer.parseInt(str.substring(i,i+1)) * 7; break;
				}
			}
			rtn = new Integer(sum%10).toString();
		}

		return rtn;
	}

	/*구장표 : 특별회계 (버스전용, 주거지위반 주차), 환경개선*/
	public String checkBitOld(String[] ocrband, int checkbit)
	{
		int sum = 0;
		String str = "";
		String rtn = "";
		
		if(checkbit==1)
		{
			for(int i=0; i<=1; i++)str += ocrband[i];
			for(int i=0; i<str.length();i++)
			{
				sum += Integer.parseInt(str.substring(i, i+1));	
			}
			rtn = new Integer(sum%10).toString();
		}
		
		if(checkbit==2)
		{
			for(int i=3; i<=10; i++)str += ocrband[i];
			for(int i=0; i<str.length();i++)
			{
				sum += Integer.parseInt(str.substring(i, i+1));	
			}
			rtn = new Integer((10-sum%10)%10).toString();
		}
		
		if(checkbit==3)
		{
			for(int i=12; i<=13; i++)str += ocrband[i];
			for(int i=0; i<str.length();i++)
			{
				sum += Integer.parseInt(str.substring(i, i+1));	
			}
			rtn = new Integer(sum%10).toString();
		}
		
		if(checkbit==4)
		{
			for(int i=15; i<=19; i++)str += ocrband[i];
			for(int i=0; i<str.length();i++)
			{
				sum += Integer.parseInt(str.substring(i, i+1));	
			}
			rtn = new Integer((10-(sum%10))%10).toString();
		}
		
		return rtn;
	}

	/*신장표 : 세외수입, 특별회계 (교통유발, 주정차위반)*/
	public String checkBitNew(String[] ocrband, int checkbit)
	{
		int sum = 0;
		String str = "";
		String rtn = "";

		if(checkbit==1)
		{
			for(int i=0; i<=5; i++)str += ocrband[i];
			for(int i=str.length()-1,j=0;i>=0;i--,j++)
			{
				switch(j%5)
				{
					case 0: sum += Integer.parseInt(str.substring(i,i+1)) * 1; break;
					case 1: sum += Integer.parseInt(str.substring(i,i+1)) * 7; break;
					case 2: sum += Integer.parseInt(str.substring(i,i+1)) * 5; break;
					case 3: sum += Integer.parseInt(str.substring(i,i+1)) * 3; break;
					case 4: sum += Integer.parseInt(str.substring(i,i+1)) * 2; break;
				}
			}
			rtn = new Integer(sum%10).toString();
		}
		
		if(checkbit==2)
		{
			for(int i=0; i<=10; i++)str += ocrband[i];
			for(int i=str.length()-1,j=0;i>=0;i--,j++)
			{
				switch(j%5)
				{
					case 0: sum += Integer.parseInt(str.substring(i,i+1)) * 5; break;
					case 1: sum += Integer.parseInt(str.substring(i,i+1)) * 3; break;
					case 2: sum += Integer.parseInt(str.substring(i,i+1)) * 2; break;
					case 3: sum += Integer.parseInt(str.substring(i,i+1)) * 1; break;
					case 4: sum += Integer.parseInt(str.substring(i,i+1)) * 7; break;
				}
			}
			rtn = new Integer(sum%10).toString();
		}
		
		if(checkbit==3)
		{
			for(int i=12; i<=17; i++)str += ocrband[i];
			for(int i=str.length()-1,j=0;i>=0;i--,j++)
			{
				switch(j%5)
				{
					case 0: sum += Integer.parseInt(str.substring(i,i+1)) * 3; break;
					case 1: sum += Integer.parseInt(str.substring(i,i+1)) * 2; break;
					case 2: sum += Integer.parseInt(str.substring(i,i+1)) * 1; break;
					case 3: sum += Integer.parseInt(str.substring(i,i+1)) * 7; break;
					case 4: sum += Integer.parseInt(str.substring(i,i+1)) * 5; break;
				}
			}
			rtn = new Integer(sum%10).toString();
		}
		
		if(checkbit==4)
		{
			for(int i=12; i<=21; i++)str += ocrband[i];
			for(int i=str.length()-1,j=0;i>=0;i--,j++)
			{
				switch(j%5)
				{
					case 0: sum += Integer.parseInt(str.substring(i,i+1)) * 7; break;
					case 1: sum += Integer.parseInt(str.substring(i,i+1)) * 5; break;
					case 2: sum += Integer.parseInt(str.substring(i,i+1)) * 3; break;
					case 3: sum += Integer.parseInt(str.substring(i,i+1)) * 2; break;
					case 4: sum += Integer.parseInt(str.substring(i,i+1)) * 1; break;
				}
			}
			rtn = new Integer(sum%10).toString();
		}
		
		if(checkbit==5)
		{
			for(int i=0; i<=22; i++)str += ocrband[i];
			for(int i=str.length()-1,j=0;i>=0;i--,j++)
			{
				switch(j%5)
				{
					case 0: sum += Integer.parseInt(str.substring(i,i+1)) * 1; break;
					case 1: sum += Integer.parseInt(str.substring(i,i+1)) * 7; break;
					case 2: sum += Integer.parseInt(str.substring(i,i+1)) * 5; break;
					case 3: sum += Integer.parseInt(str.substring(i,i+1)) * 3; break;
					case 4: sum += Integer.parseInt(str.substring(i,i+1)) * 2; break;
				}
			}
			rtn = new Integer(sum%10).toString();
		}
		
		return rtn;
	}	

}
