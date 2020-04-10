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

		int kind = 0;//0:ǥ����ǥ, 1:����ǥ, 2:����ǥ
		if(!OCR_org.startsWith("26"))kind=2;
		else if(OCR_org.endsWith("+++"))kind=1;

		if(kind==0) ocrcnt= new int[]{2,3,1,2,3,3,4,2,1,3,6,1,11,11,1,11,10,10,11,8,1,1,1,1};
		if(kind==1) ocrcnt= new int[]{2,3,1,2,3,3,4,2,1,3,6,1,11,11,1,11,10,10,11,8,1,1,1,1};
		if(kind==2) ocrcnt= new int[]{11,4,2,6,6,2,1,1,11,8,1,1,10,2,10,2,10,2,1,2,1,12,1,1};
		
		if(kind==0) chkbitcnt= new int[]{2,11,14,20,23};
		if(kind==1) chkbitcnt= new int[]{2,11,14,20};
		if(kind==2) chkbitcnt= new int[]{6,11,18,22,23};
		
		if(kind==0) ocrstr= new String[]{"�õ��ڵ�","��û�ڵ�","��1","ȸ���ڵ�","�����ڵ�","�����ڵ�","�ΰ��⵵","�ΰ���","����ڵ�","�������ڵ�","������ȣ","��2","���⳻�Ѿ�","�������Ѿ�","��3","����","���ð�ȹ��","�����ü���(��Ư��)","���汳����","������","��4","�ʷ�(7)","��������","��5"};
		if(kind==1) ocrstr= new String[]{"�õ��ڵ�","��û�ڵ�","��1","ȸ���ڵ�","�����ڵ�","�����ڵ�","�ΰ��⵵","�ΰ���","����ڵ�","�������ڵ�","������ȣ","��2","���⳻�Ѿ�","�������Ѿ�","��3","����","���ð�ȹ��","�����ü���(��Ư��)","���汳����","������","��4","�ʷ�(+)","�ʷ�(+)","�ʷ�(+)"};
		if(kind==2) ocrstr= new String[]{"�μ��ڵ�","�ΰ��⵵","ȸ���ڵ�","�����ڵ�","������ȣ","����","��1","��������","���⳻�Ѿ�","���⳻����","�ΰ���ġ������","��2","����","��������","�õ���","�õ�������","�ñ�����","�ñ���������","��3","�ΰ���","������ó��","�������Ѿ�","��4","��5"};
		
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

		//ȭ����� start
		System.out.println("OCR����  ["+OCR_org+"]");
		System.out.println("OCR����  ["+OCR_dest+"]");
		System.out.println("OCR��  ["+OCR_comp+"]");
		
		for(int j=0;j<ocrcnt.length;j++)
		{
			int sumj=0;
			for(int k=0;k<=j;k++)sumj+=ocrcnt[k];
			ocrband[j]=OCR_dest.substring(sumj-ocrcnt[j],sumj);
		}
		for(int i=0;i<ocrband.length;i++)System.out.println("["+((char)(65+i))+" "+ocrstr[i]+":"+ocrcnt[i]+"] "+ocrband[i]);
		//ȭ����� end
		
	}
	

	/*ǥ����ǥ : ���漼*/
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

	/*����ǥ : Ư��ȸ�� (��������, �ְ������� ����), ȯ�氳��*/
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

	/*����ǥ : ���ܼ���, Ư��ȸ�� (��������, ����������)*/
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
