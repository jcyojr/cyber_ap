 package com.uc.bs.cyber.daemon.txdm6000;


import com.uc.core.MapForm;


	public class Txdm6000Messages{
		
		private String[][] taxVars;
		private StringBuffer reqXml;
		
		public Txdm6000Messages(MapForm prop) throws Exception{
			
			this.taxVars = getDocVars();

			reqXml= new StringBuffer("<?xml version='1.0' encoding = 'euc-kr' ?>");

			reqXml.append("<LTIS>");
			reqXml.append("<COMMON name='ComModel'></COMMON>");
			reqXml.append("<CONTENT>");
					
			//System.out.println("=======eventId======="+(String) prop.getMap("tax_gubun").toString());
					
			reqXml.append("<DATA name='OCetaxInfo'>");		
								
			for(int i=0; i< taxVars[0].length; i++){
				System.out.println("taxVars=" + taxVars[0][i]);
				reqXml.append("<"+taxVars[0][i]+"><![CDATA["+(prop.getMap(taxVars[0][i]) == null?"":prop.getMap(taxVars[0][i]).toString()).replaceAll(",","")+"]]></"+taxVars[0][i]+">");
			}	
			reqXml.append("</DATA>");
			reqXml.append("<LIST>");
			reqXml.append("<RECORD name='OCetaxInfo'>");
			reqXml.append("<SS_NO>1</SS_NO>");
			reqXml.append("<SS_TPR_NO><![CDATA["+prop.getMap("TPR_NO").toString()+"]]></SS_TPR_NO>");
			reqXml.append("<TPR_NM><![CDATA["+prop.getMap("TPR_NM").toString()+"]]></TPR_NM>");
			reqXml.append("<Q_TOT>100.00</Q_TOT>");
			reqXml.append("<Q_DIV>100.00</Q_DIV>");
			reqXml.append("<CNTC_RESULT_CODE>00</CNTC_RESULT_CODE>");		
			reqXml.append("</RECORD>");
			reqXml.append("</LIST>");
			reqXml.append("</CONTENT>");
			reqXml.append("</LTIS>");
					
	    }

		public StringBuffer getMessage(){

			return  reqXml;
		}

		public String[][] getTaxVars(){
			
			return this.taxVars;
		}
		
		public static String[][] getDocVars(){
			return CM_TAX_F9_200;
		}
		
		public static final String CM_TAX_F9_200[][] = new String[][]{
			{"SNO","TPR_NO","TPR_NM","C_CO_KND","NAPSEJA_TEL","C_ZIP","ADDR_CUR","C_HDONG_OBJ","CN_AGENT","TEL_AGENT","C_SLF_ORG_OBJ",
				"C_BDONG_OBJ","C_RAW_LEE_OBJ","C_SPE_JI_OBJ","ADDR_BON_OBJ","ADDR_BU_OBJ","RD_MGT_SN","BD_JIHA","BD_MA_SN","BD_SB_SN",
				"ADDR_EXT","ADDR_OBJ","C_GET_REG","D_GET","D_SLF_REP","N_CAR","N_CHADAE","C_CAR_KND","C_CAR","CN_CAR_USER_REG",
				"X_USE_BF","TYP_YY","Q_GET_CC","Q_LOADAGE","Q_RIDER","R_GET_COSM","CAR_ZIPGB","X_APLY_RMN","V_APLY_GPYO",
				"C_GWASE_RATE_REG","R_RMN","A_REP_GPYO","HMMD_SE_CODE","Q_CNT","SS_CNT"},
			{"MESSAGE"}
		};
		
}
	
