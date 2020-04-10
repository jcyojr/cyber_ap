
/*
//////////////////////////////////////////////////\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
 DATE : 2008.10.11
 SUBJECT : Connecting to wetax webservice
 :::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
 Author : Shinwook kim 
 webspace<blog> : http://tkfkd.tistory.com 
 E-mail : smilek@uctech.net
\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\//////////////////////////////////////////////////
*/

package com.uc.bs.cyber;

public class DocumentVars{
	
	//지방소득세 특별징수 -- 수정완료
	public static final String CM_TAX_115[][] = new String[][]{
		{"SIDO_COD", "SGG_COD", "TAX_ITEM", "TAX_YYMM", "TAX_DIV", "ADONG_COD", "TAX_DT", "REG_NO", 
			"REG_NM", "TPR_COD", "LDONG_COD", "BIZ_ZIP_NO", "BIZ_ZIP_ADDR", "BIZ_ADDR", "MO_TEL", 
			"BIZ_TEL", "BIZ_NO", "CMP_NM", "REQ_DIV", "RVSN_YYMM", "SUP_YYMM", "DUE_DT", "YY_TRTN", 
			"YY_MRTN", "YY_RRTN", "OUT_TAMT", "OUT_MAMT", "OUT_RAMT", 
			"EMP_CNT_1", "INCOMTAX_1", "RSTX_1", 
			"EMP_CNT_2", "INCOMTAX_2", "RSTX_2",
			"EMP_CNT_3", "INCOMTAX_3", "RSTX_3", 
			"EMP_CNT_4", "INCOMTAX_4", "RSTX_4", 
			"EMP_CNT_5", "INCOMTAX_5", "RSTX_5",
			"EMP_CNT_6", "INCOMTAX_6", "RSTX_6", 
			"EMP_CNT_7", "INCOMTAX_7", "RSTX_7", 
			"EMP_CNT_8", "INCOMTAX_8", "RSTX_8", 
			"EMP_CNT_9", "INCOMTAX_9", "RSTX_9", 
			"EMP_CNT_11","INCOMTAX_11", "RSTX_11",
			"RDT_RSTX", "RDT_ADTX", 
			"INCOMTAX_10", "RSTX_10", "ADTX_10", 
			"ADTX_AM","DLQ_ADTX","DLQ_CNT",
			"MM_RTN", "PAY_RSTX", "PAY_ADTX", "TAX_RT", "ADTX_YN", 
			"TOT_ADTX", "TOT_AMT", "F_DUE_DT", "RPT_REG_NO", "RPT_NM", "RPT_TEL", "RPT_ID", "MEMO", "RPT_SYSTEM", 
			"ADD_MM_RTN", "ADD_MM_AAMT", "ADD_YY_TRTN", "ADD_YY_TAMT", "ADD_ETC_RTN", "ADD_RDT_ADTX", "ADD_SUM_RTN", 
			"ADD_SUM_AAMT", "ADD_OUT_AMT", "ADD_TOT_AMT", "ADD_OUT_SAMT", "MINU_YN", "SINGO_DIV", 
			"B_TAX_NO", "B_EPAY_NO", "CHG_MEMO", "CHG_REASON", "REASON_DT", "ETC_MEMO", "SUNAP_DT"},			
		{"MESSAGE","CUD_OPT", "DLQ_DIV", "LVY_DIV", "TAX_GDS", "TAX_NO", "EPAY_NO", "REG_NO", "REG_NM", "BIZ_NO", 
				"CMP_NM", "SIDO_COD", "SGG_COD", "CHK1", "ACCT_COD", "TAX_ITEM", "TAX_YY", "TAX_MM", "TAX_DIV", 
				"ADONG_COD", "TAX_SNO", "CHK2", "SUM_B_AMT", "SUM_F_AMT", "CHK3", "MNTX", "CPTX", "CFTX_ASTX", "LETX", 
				"DUE_DT", "CHK4", "FILLER", "GOJI_DIV", "CHK5", "TAX_STD", "MNTX_ADTX", "CPTX_ADTX", "CFTX_ADTX", "LETX_ADTX", 
				"ASTX_ADTX", "TAX_STD_DIS", "CRE_DT", "AUTO_TRNF_YN", "DUE_DF_OPT", "GIROSGG_COD", "GIRO_NO", "DUE_F_DT", 
				"CHRG_NM", "CHRG_TEL", "CHG_DT", "PAY_DT"}
	};
	
	//지방소득세 종합소득 -- 수정완료
	public static final String CM_TAX_120[][] = new String[][]{
		{"SIDO_COD", "SGG_COD", "TAX_ITEM", "TAX_YYMM", "TAX_DIV", "ADONG_COD", "TAX_DT", "REG_NO", "REG_NM", "TPR_COD", "LDONG_COD", 
			"BIZ_ZIP_NO", "BIZ_ZIP_ADDR", "BIZ_ADDR", "MO_TEL", "BIZ_TEL", "BIZ_NO", "CMP_NM", "RVSN_YY", "C_REQ_DT", "DUE_DT", "REQ_DIV", 
			"INCTX", "RSTX_INC", "RADTX", "PADTX", "TAX_RT", "ADTX_YN", "DLQ_CNT", "TOT_ADTX", "TOT_AMT", "F_DUE_DT", "RPT_REG_NO", "RPT_NM", 
			"RPT_TEL", "RPT_ID", "RPT_ADMIN", "RPT_SYSTEM", "SINGO_DIV", "B_TAX_NO", "B_EPAY_NO", "CHG_MEMO", "CHG_REASON", "REASON_DT", 
			"ETC_MEMO", "SUNAP_DT", "EVI_DOC1", "EVI_DOC_URL1", "EVI_DOC2", "EVI_DOC_URL2", "EVI_DOC3", "EVI_DOC_URL3"},
			
			{"MESSAGE","CUD_OPT", "DLQ_DIV", "LVY_DIV", "TAX_GDS", "TAX_NO", "EPAY_NO", "REG_NO", "REG_NM", "BIZ_NO", "CMP_NM", "SIDO_COD", 
				"SGG_COD", "CHK1", "ACCT_COD", "TAX_ITEM", "TAX_YY", "TAX_MM", "TAX_DIV", "ADONG_COD", "TAX_SNO", "CHK2", "SUM_B_AMT", 
				"SUM_F_AMT", "CHK3", "MNTX", "CPTX", "CFTX_ASTX", "LETX", "DUE_DT", "CHK4", "FILLER", "GOJI_DIV", "CHK5", "TAX_STD", 
				"MNTX_ADTX", "CPTX_ADTX", "CFTX_ADTX", "LETX_ADTX", "ASTX_ADTX", "TAX_STD_DIS", "CRE_DT", "AUTO_TRNF_YN", "DUE_DF_OPT", 
				"GIROSGG_COD", "GIRO_NO", "DUE_F_DT", "CHRG_NM", "CHRG_TEL", "CHG_DT", "PAY_DT"}		
	};
	
	
	
	//지방소득세 양도소득 -- 수정완료
	public static final String CM_TAX_130[][] = new String[][]{
		{"SIDO_COD", "SGG_COD", "TAX_ITEM", "TAX_YYMM", "TAX_DIV", "ADONG_COD", "TAX_DT", "REG_NO", "REG_NM", "TPR_COD", 
		 "LDONG_COD", "ZIP_NO", "ZIP_ADDR", "OTH_ADDR", "MO_TEL", "TEL", "BIZ_NO", "CMP_NM", "RVSN_YY", "DUE_DT", "REQ_DIV", 
		 "RTN_INC_DT", "RTN_ZIP_NO", "RTN_ADDR", "RTNTX", "RSTX_RTN", "RADTX", "PADTX", "TAX_RT", "ADTX_YN", "DLQ_CNT", "TOT_ADTX", 
		 "TOT_AMT", "F_DUE_DT", "RPT_REG_NO", "RPT_NM", "RPT_TEL", "RPT_ID", "RPT_ADMIN", "RPT_SYSTEM", "SINGO_DIV", "B_TAX_NO", 
		 "B_EPAY_NO", "CHG_MEMO", "CHG_REASON", "REASON_DT", "ETC_MEMO", "SUNAP_DT", "EVI_DOC1", "EVI_DOC_URL1", "EVI_DOC2", 
		 "EVI_DOC_URL2", "EVI_DOC3", "EVI_DOC_URL3"},
		 {"MESSAGE","CUD_OPT", "DLQ_DIV", "LVY_DIV", "TAX_GDS", "TAX_NO", "EPAY_NO", "REG_NO", "REG_NM", "BIZ_NO", "CMP_NM", "SIDO_COD", 
			 "SGG_COD", "CHK1", "ACCT_COD", "TAX_ITEM", "TAX_YY", "TAX_MM", "TAX_DIV", "ADONG_COD", "TAX_SNO", "CHK2", "SUM_B_AMT", 
			 "SUM_F_AMT", "CHK3", "MNTX", "CPTX", "CFTX_ASTX", "LETX", "DUE_DT", "CHK4", "FILLER", "GOJI_DIV", "CHK5", "TAX_STD", 
			 "MNTX_ADTX", "CPTX_ADTX", "CFTX_ADTX", "LETX_ADTX", "ASTX_ADTX", "TAX_STD_DIS", "CRE_DT", "AUTO_TRNF_YN", "DUE_DF_OPT", 
			 "GIROSGG_COD", "GIRO_NO", "DUE_F_DT", "CHRG_NM", "CHRG_TEL", "CHG_DT", "PAY_DT" 
		 }
	};
	
	
	//지방소득세 법인세분 -- 수정완료
	public static final String CM_TAX_140[][] = new String[][]{
		{"SIDO_COD","SGG_COD","TAX_ITEM","TAX_YYMM","TAX_DIV","ADONG_COD","TAX_DT","REG_NO","REG_NM","TPR_COD","LDONG_COD",
			"BIZ_ZIP_NO","BIZ_ZIP_ADDR","BIZ_ADDR","MO_TEL","BIZ_TEL",	"BIZ_NO","CMP_NM","CMPTX_KD","REQ_KD_DT","DUE_DT","REQ_DIV",
			"RVSN_S_DT","RVSN_E_DT","TOT_EMP_CNT","TOT_B_AREA","IN_EMP_CNT","IN_B_ADRE","TOT_CMPTX","TOT_RSTX","PDIV_RT","CMPTX",
			"RSTX_CMP","RADTX","PADTX","TAX_RT","ADTX_YN","DLQ_CNT","TOT_ADTX","TOT_AMT","F_DUE_DT","RPT_REG_NO","RPT_NM","RPT_TEL",
			"RPT_ID","RPT_ADMIN","RPT_SYSTEM", "SINGO_DIV","B_TAX_NO","B_EPAY_NO", "CHG_MEMO", "CHG_REASON", "REASON_DT", "ETC_MEMO", 
			"SUNAP_DT", "EVI_DOC1", "EVI_DOC_URL1", "EVI_DOC2","EVI_DOC_URL2", "EVI_DOC3", "EVI_DOC_URL3","C_TAXADD_R","C_TAXADD_GUBUN","RADTX_RT","F_SINGO_STD"},
		{"MESSAGE","CUD_OPT","DLQ_DIV","LVY_DIV","TAX_GDS","TAX_NO","EPAY_NO","REG_NO","REG_NM","BIZ_NO","CMP_NM","SIDO_COD","SGG_COD",
			"CHK1","ACCT_COD","TAX_ITEM","TAX_YY","TAX_MM","TAX_DIV","ADONG_COD","TAX_SNO","CHK2","SUM_B_AMT","SUM_F_AMT","CHK3",
			"MNTX","CPTX","CFTX_ASTX","LETX","DUE_DT","CHK4","FILLER","GOJI_DIV","CHK5","TAX_STD","MNTX_ADTX","CPTX_ADTX","CFTX_ADTX",
			"LETX_ADTX","ASTX_ADTX","TAX_STD_DIS","CRE_DT","AUTO_TRNF_YN","DUE_DF_OPT","GIROSGG_COD","GIRO_NO","DUE_F_DT","CHRG_NM",
			"CHRG_TEL","CHG_DT","PAY_DT"}
	};

	//지방소득세 종업원분  -- 수정완료
	public static final String CM_TAX_150[][] = new String[][]{
		{"SIDO_COD","SGG_COD","TAX_ITEM","TAX_YYMM","TAX_DIV","ADONG_COD","TAX_DT","REG_NO","REG_NM","TPR_COD","LDONG_COD",
			"ZIP_NO","ZIP_ADDR","OTH_ADDR","MO_TEL","TEL","BIZ_NO","CMP_NM","BIZ_ZIP_NO","BIZ_ZIP_ADDR","BIZ_ADDR","RVSN_YYMM","SAL_DT",
			"DUE_DT","F_DUE_DT","EMP_CNT","TXE_EMP_CNT","SAL","TXE_SAL","TAX_STD","RDX_COD","RDX_R","RDX_AMT","R_P_AMT","TAX_RT","ADTX_YN",
			"DLQ_CNT","RADTX","PADTX","TOT_ADTX","TOT_AMT","RPT_REG_NO","RPT_NM","RPT_TEL","RPT_ID","RPT_ADMIN","RPT_SYSTEM",
			"SINGO_DIV","B_TAX_NO","B_EPAY_NO", "CHG_MEMO", "CHG_REASON", "REASON_DT", "ETC_MEMO", "SUNAP_DT", "EVI_DOC1", "EVI_DOC_URL1", 
			"EVI_DOC2","EVI_DOC_URL2", "EVI_DOC3", "EVI_DOC_URL3","BFE_EMP_CNT","MON_SAL","RDX_SAL","C_TAXADD_R","C_TAXADD_GUBUN","RADTX_RT","F_SINGO_STD","RDX_EMP_CNT","RDX_COD_GBN"},
		{"MESSAGE","CUD_OPT","DLQ_DIV","LVY_DIV","TAX_GDS","TAX_NO","EPAY_NO","REG_NO","REG_NM","BIZ_NO","CMP_NM","SIDO_COD","SGG_COD",
			"CHK1","ACCT_COD","TAX_ITEM","TAX_YY","TAX_MM","TAX_DIV","ADONG_COD","TAX_SNO","CHK2","SUM_B_AMT","SUM_F_AMT","CHK3",
			"MNTX","CPTX","CFTX_ASTX","LETX","DUE_DT","CHK4","FILLER","GOJI_DIV","CHK5","TAX_STD","MNTX_ADTX","CPTX_ADTX","CFTX_ADTX",
			"LETX_ADTX","ASTX_ADTX","TAX_STD_DIS","CRE_DT","AUTO_TRNF_YN","DUE_DF_OPT","GIROSGG_COD","GIRO_NO","DUE_F_DT","CHRG_NM",
			"CHRG_TEL","CHG_DT","PAY_DT"}
	};

	//주민세 재산분 -- 수정완료
	public static final String CM_TAX_160[][] = new String[][]{
		{"SIDO_COD","SGG_COD","TAX_ITEM","TAX_YYMM","TAX_DIV","ADONG_COD","TAX_DT","REG_NO","REG_NM","TPR_COD","LDONG_COD",
			"ZIP_NO","ZIP_ADDR","OTH_ADDR","MO_TEL","TEL","BIZ_NO","CMP_NM","BIZ_ZIP_NO","BIZ_ZIP_ADDR","BIZ_ADDR","RVSN_YY",
			"DUE_DT","F_DUE_DT","US_AREA","TXE_AREA","TXE_AREA_R","TAX_AREA","TAX_STD","POL_YN","RDX_COD","RDX_R","RDX_AMT",
			"TAX_RT","R_P_AMT","ADTX_YN","DLQ_CNT","RADTX","PADTX","TOT_ADTX","TOT_AMT","RPT_REG_NO","RPT_NM","RPT_TEL",
			"RPT_ID","RPT_ADMIN","RPT_SYSTEM","SINGO_DIV","B_TAX_NO","B_EPAY_NO", "CHG_MEMO", "CHG_REASON", "REASON_DT", "ETC_MEMO", 
			"SUNAP_DT", "EVI_DOC1", "EVI_DOC_URL1","EVI_DOC2","EVI_DOC_URL2", "EVI_DOC3", "EVI_DOC_URL3","C_TAXADD_R","C_TAXADD_GUBUN","RADTX_RT","F_SINGO_STD"},
		{"MESSAGE","CUD_OPT","DLQ_DIV","LVY_DIV","TAX_GDS","TAX_NO","EPAY_NO","REG_NO","REG_NM","BIZ_NO","CMP_NM","SIDO_COD","SGG_COD",
			"CHK1","ACCT_COD","TAX_ITEM","TAX_YY","TAX_MM","TAX_DIV","ADONG_COD","TAX_SNO","CHK2","SUM_B_AMT","SUM_F_AMT","CHK3",
			"MNTX","CPTX","CFTX_ASTX","LETX","DUE_DT","CHK4","FILLER","GOJI_DIV","CHK5","TAX_STD","MNTX_ADTX","CPTX_ADTX","CFTX_ADTX",
			"LETX_ADTX","ASTX_ADTX","TAX_STD_DIS","CRE_DT","AUTO_TRNF_YN","DUE_DF_OPT","GIROSGG_COD","GIRO_NO","DUE_F_DT","CHRG_NM",
			"CHRG_TEL","CHG_DT","PAY_DT"}
	};


	
	public static String[][] getDocVars(String docType){

		if(docType.equals("104008")||docType.equals("140004")){    //특별징수
			return CM_TAX_115;
			
		}else if(docType.equals("104004")||docType.equals("140001")){     // 종합소득세할
			 return CM_TAX_120;

		}else if(docType.equals("104005")||docType.equals("140002")){     // 양도소득세할
			 return CM_TAX_130;

		}else if(docType.equals("104006")||docType.equals("140003")){     // 법인세할
			 return CM_TAX_140;

		}else if(docType.equals("132002")||docType.equals("104011")){     // 종업원할
			 return CM_TAX_150;

		}else if(docType.equals("132001")||docType.equals("104009")){     // 재산할
			 return CM_TAX_160;

		}else{
			return null;
		}

	}

  
	public static String checkNull(String data){
		return (data == null || data.equals(""))?"empaty":data;
	}
	
}


