package com.uc.bs.cyber.daemon.sample;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DiffDate {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		
		System.out.println("DIFF=" + getDiffDate("20110310", "20110614"));
		
	}
	
	
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

}
