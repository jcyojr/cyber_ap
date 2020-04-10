/**
 * 
 */
package com.uc.bs.cyber;

/**
 * @author Administrator
 *
 */
public class CbConst extends com.uc.core.Constants{
	
	/**
	 * 프로젝트 시작시 아래의 3개 값을 변경하여 사용하세요
	 */
	public static final int SERVICE_MODE = 1;		// 서비스 모드 0:기본, 1:데이터 저장, 2: 테스트데이터 조회
	
	/***************************************************
	 * 시스템 코드
	 */
	public static final String SYS_DSC_CORE   = "01";		// 코어시스템
	public static final String SYS_DSC_CTAG   = "02";		// 시청Agent
	public static final String SYS_DSC_KGIS   = "03";		// 금고시스템
	public static final String SYS_DSC_KGCF   = "04";		// CashFlow

	/**
	 * 송수신 구분코드
	 */
	public static final String TRS_DSC_SEND   = "01";		// 송신
	public static final String TRS_DSC_RECV   = "02";		// 수신
	
	/**
	 * 파일 처리상태코드 
	 */
	public static final String FILE_STS_DSC_REQ   = "00";		// 송신요청
	public static final String FILE_STS_DSC_RCV   = "01";		// 송신요청
	public static final String FILE_STS_DSC_ING   = "02";		// 송신요청
	public static final String FILE_STS_DSC_DON   = "03";		// 송신요청
	public static final String FILE_STS_DSC_ERR   = "09";		// 송신요청
	
	/**
	 * 파일종류 코드 
	 */
	public static final String FILE_KIND_DSC01	= "01";		// 지방세고지파일
	public static final String FILE_KIND_DSC03 	= "03";		// 상하수도고지파일
	public static final String FILE_KIND_DSC04   = "04";	// 지방세 수납대사파일
	public static final String FILE_KIND_DSC05   = "05";	// 상하수도 수납파일
	public static final String FILE_KIND_DSC06   = "06";	// 지방세 일괄고지파일
	public static final String FILE_KIND_DSC07   = "07";	// 상하수도 수납통지파일	
	public static final String FILE_KIND_DSC99   = "99";    // 영업일 파일
	
	public static final String FILE_KIND_DSC11   = "11";    // 일계/월계 일괄처리
	public static final String FILE_KIND_DSC12   = "12";    // 자금운용계좌내역 일괄처리
	public static final String FILE_KIND_DSC13   = "13";    // 운용계좌정보파일
	public static final String FILE_KIND_DSC14   = "14";    // 금고코드정보파일
	public static final String FILE_KIND_DSC15   = "15";    // 거래내역파일
	public static final String FILE_KIND_DSC16   = "16";    // 기산일경정파일
	public static final String FILE_KIND_DSC17   = "17";    // 요구불계좌 거래내역
	
	/* OCR */
	public static final String FILE_KIND_DSC20   = "20";    // 전자수납분파일
	public static final String FILE_KIND_DSC21   = "21";    // 이체파일
	
	/**
	 * Login ID 
	 */
    public static final String SAVE_USER_ID_KEY       = "user_id";
	public static final String SAVE_USER_ID_CHECK_KEY = "save_id";
    public static final String LOGIN_USER_ID          = "login_user_id";
    
    /**
     *  전문 거래구분코드
     */
	public static final String TR_KIND_REQ = "0200";	// 요구
	public static final String TR_KIND_RES = "0210";	// 응답
	public static final String TR_KIND_RREQ = "0400";	// 미처리확인요구
	public static final String TR_KIND_RRES = "0410";	// 미처리확인응답	
	
	
	/* 은행코드 */
	public static final String BANK_CD = "032";
	
	public static final int   TRS_STS_CD_OK      = 10;
	public static final int   TRS_STS_CD_ER      = 20;
	
	public static final Object SHPROC_DSC_REG     = "01";
	public static final Object SHPROC_DSC_FORMAT  = "02";
	public static final Object SHPROC_DSC_GJERR   = "03";
	public static final Object SHPROC_DSC_GJEND   = "04";
	public static final Object SHPROC_DSC_SEND    = "05";
	
	/**
	 * 가상계좌 업무구분코드 지방세:01, 세외수입:02
	 */
	public static final String VAJOB_DSC_LT       = "01";
	public static final String VAJOB_DSC_SO       = "02";
}

