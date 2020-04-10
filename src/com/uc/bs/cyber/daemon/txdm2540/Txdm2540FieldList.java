/**
 *  주시스템명 : 사이버지방세청 고도화
 *  업  무  명 : 위택스 연계
 *  기  능  명 : 위택스-사이버세청 전문 송수신 헤더
 *               위택스 회원정보수신 전문
 *  클래스  ID : Txdm2540FieldList
 *  변경  이력 :
 * ------------------------------------------------------------------------
 *  작성자     소속  		 일자      Tag   내용
 * ------------------------------------------------------------------------   
 * 소도우   유채널(주)  2011.06.26     %01%  신규작성
 *  
 */
package com.uc.bs.cyber.daemon.txdm2540;

import com.uc.egtob.net.FieldList;
import com.uc.core.MapForm;
/**
 * @author Administrator
 *
 */
public class Txdm2540FieldList {
			
	private MapForm dataMap = null;
	
	private FieldList field ;
	
	private int     len =  0;
	
	/**
	 * 생성자
	 * 위택스 회원정보수신 전문
	 * 참고) 공통전문없음
	 */
	public Txdm2540FieldList() {
		// TODO Auto-generated constructor stub
		
		field = new FieldList();

		field.add("usr_div"  ,     32,    "C");    //1 사용자구분 [ 내국인(개인,개인사업자):01 ||||||||외국인(개인,개인사업자):02||||||||법인:21 ]
		field.add("usr_nm"   ,     80,    "C");    //2 성명/대표자
		field.add("tpr_no"   ,     16,    "C");    //3 주민등록번호/법인번호
		field.add("bz_no"    ,     16,    "C");    //4 사업자등록번호
		field.add("zip_no"   ,      6,    "C");    //5 우편번호
		
		len = field.getFieldListLen();

		this.dataMap = new MapForm();

	}
	
	public void Txdm2540_Crp_FieldList() {
		// TODO Auto-generated constructor stub
		
		field = new FieldList();

		field.add("usr_div"  ,     32,    "C");    //1 사용자구분 [ 내국인(개인,개인사업자):01 ||||||||외국인(개인,개인사업자):02||||||||법인:21 ]
		field.add("usr_nm"   ,     80,    "C");    //2 성명/대표자
		field.add("tpr_no"   ,     16,    "C");    //3 주민등록번호/법인번호
		field.add("bz_no"    ,     16,    "C");    //4 사업자등록번호
		field.add("zip_no"   ,      6,    "C");    //5 우편번호
		field.add("addr1"    ,    128,    "C");    //6 주소
		field.add("addr2"    ,    128,    "C");    //7 상세주소
		field.add("sido_cod" ,      2,    "C");    //8 시도코드
		field.add("sgg_cod"  ,      3,    "C");    //9 시군구코드
		field.add("email"    ,    100,    "C");    //10 이메일주소
		field.add("email_yn" ,      1,    "C");    //11 메일링서비스
		field.add("tel_no"   ,    128,    "C");    //12 전화번호
		field.add("mo_tel"   ,    128,    "C");    //13 모바일번호
		field.add("dn"       ,    256,    "C");    //14 인증서정보
		field.add("crp_nm"   ,     80,    "C");    //15 법인명
		field.add("remrk"    ,    512,    "C");    //16 비고
		field.add("comp_nm"  ,    100,    "C");    //17 회사명
		field.add("tpr_no2"  ,     16,    "C");    //18 대표자주민번호
		
		len = field.getFieldListLen();

		this.dataMap = new MapForm();

	}
	
	public void Txdm2540_Crp_short_FieldList() {
		// TODO Auto-generated constructor stub
		
		field = new FieldList();

		field.add("usr_div"  ,     32,    "C");    //1 사용자구분 [ 내국인(개인,개인사업자):01 ||||||||외국인(개인,개인사업자):02||||||||법인:21 ]
		field.add("usr_nm"   ,     80,    "C");    //2 성명/대표자
		field.add("tpr_no"   ,     16,    "C");    //3 주민등록번호/법인번호
		field.add("bz_no"    ,     16,    "C");    //4 사업자등록번호
		field.add("zip_no"   ,      6,    "C");    //5 우편번호
		field.add("addr1"    ,    128,    "C");    //6 주소
		field.add("addr2"    ,    128,    "C");    //7 상세주소
		field.add("sido_cod" ,      2,    "C");    //8 시도코드
		field.add("sgg_cod"  ,      3,    "C");    //9 시군구코드
		field.add("email"    ,    100,    "C");    //10 이메일주소
		field.add("email_yn" ,      1,    "C");    //11 메일링서비스
		field.add("tel_no"   ,    128,    "C");    //12 전화번호
		field.add("mo_tel"   ,    128,    "C");    //13 모바일번호
		field.add("dn"       ,    256,    "C");    //14 인증서정보
		field.add("crp_nm"   ,     80,    "C");    //15 법인명
		field.add("remrk"    ,    512,    "C");    //16 비고
		field.add("comp_nm"  ,    29,     "C");    //17 회사명

		
		len = field.getFieldListLen();

		this.dataMap = new MapForm();

	}	
	
	public void Txdm2540_Pri_FieldList() {
		// TODO Auto-generated constructor stub
		
		field = new FieldList();

		field.add("usr_div"  ,     32,    "C");    //1 사용자구분 [ 내국인(개인,개인사업자):01 ||||||||외국인(개인,개인사업자):02||||||||법인:21 ]
		field.add("usr_nm"   ,     80,    "C");    //2 성명/대표자
		field.add("tpr_no"   ,     16,    "C");    //3 주민등록번호/법인번호
		field.add("bz_no"    ,     16,    "C");    //4 사업자등록번호
		field.add("zip_no"   ,      6,    "C");    //5 우편번호
		field.add("addr1"    ,    128,    "C");    //6 주소
		field.add("addr2"    ,    128,    "C");    //7 상세주소
		field.add("sido_cod" ,      2,    "C");    //8 시도코드
		field.add("sgg_cod"  ,      3,    "C");    //9 시군구코드
		field.add("email"    ,    100,    "C");    //10 이메일주소
		field.add("email_yn" ,      1,    "C");    //11 메일링서비스
		field.add("tel_no"   ,    128,    "C");    //12 전화번호
		field.add("mo_tel"   ,    128,    "C");    //13 모바일번호
		field.add("dn"       ,    256,    "C");    //14 인증서정보
		field.add("crp_nm"   ,     80,    "C");    //15 법인명
		

		
		len = field.getFieldListLen();

		this.dataMap = new MapForm();

	}	
	/**
	 * 전문 엔진에 송신할 헤더 생성
	 */
	public byte[] getBuff(MapForm mapForm) throws Exception{
		
		dataMap = mapForm;

		return getBuff();
	}
	
	/**
	 * 
	 * @param msgType
	 * @param srcId
	 * @return
	 * @throws Exception
	 */
	public byte[] getBuff() throws Exception{

		byte[] headBuf ;
				
		headBuf =  field.makeMessageByte(dataMap);
		
		return headBuf;		
		
	}
	
	/**
	 * 전문파싱...
	 * @param buffer
	 * @return
	 * @throws Exception
	 */
	public MapForm parseBuff(byte[] buffer) throws Exception{

		try {
			dataMap = field.parseMessage(buffer, 0);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw e;
		}

		return dataMap;
	}

	public int getLen() {
		// TODO Auto-generated method stub
		return len;
	}


	public FieldList getFieldList() {
		// TODO Auto-generated method stub
		return field;
	}
	
	/**
	 * 
	 * @param fldName
	 * @return
	 */
	public String getField(String fldName) {
		
		return (String) dataMap.getMap(fldName);
	}

	/**
	 * 
	 * @param key
	 * @param val
	 */
	public void setField(String key, String val) {
		// TODO Auto-generated method stub
		this.dataMap.setMap(key, val);
	}

	public MapForm getDataMap() {
		// TODO Auto-generated method stub
		return this.dataMap;
	}

	/**
	 * 
	 * @param mapForm
	 */
	public void setDataMap(MapForm mapForm) {
		// TODO Auto-generated method stub
		this.dataMap = mapForm;
	}
	

}
