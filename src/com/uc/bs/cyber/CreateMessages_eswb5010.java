 package com.uc.bs.cyber;

 import com.uc.core.MapForm;


	public class CreateMessages_eswb5010
	{
		private String[][] taxVars;
		private StringBuffer reqXml;
		
		public CreateMessages_eswb5010(MapForm prop) throws Exception{
			
			this.taxVars=DocumentVars.getDocVars((String) prop.getMap("tax_gubun"));

                    reqXml= new StringBuffer("<?xml version='1.0' encoding = 'euc-kr' ?>");

					reqXml.append("<LTIS>");
					reqXml.append("<COMMON></COMMON>");
					reqXml.append("<CONTENT>");
					
					System.out.println("=======eventId======="+(String) prop.getMap("tax_gubun").toString());
					
					if( prop.getMap("tax_gubun").toString().equals("0F9100")){
						reqXml.append("<DATA name='OCetaxInfo'>");
					}else if(prop.getMap("tax_gubun").toString().equals("104009")){
						reqXml.append("<DATA name='ThrSInfo'>");
					}else{
						reqXml.append("<DATA name='Thr53PInfo'>");
					}
					


				
			for(int i=0; i< taxVars[0].length; i++){
				System.out.println("taxVars=" + taxVars[0][i]);
				reqXml.append("<"+taxVars[0][i]+"><![CDATA["+(prop.getMap(taxVars[0][i]) == null?" ":prop.getMap(taxVars[0][i]).toString()).replaceAll(",","")+"]]></"+taxVars[0][i]+">");

				}				

					reqXml.append("</DATA>");
					reqXml.append("</CONTENT>");
					reqXml.append("</LTIS>");
					
	    }

		public StringBuffer getMessage(){

			return  reqXml;
		}

		public String[][] getTaxVars(){
			return this.taxVars;
		}
}
	
