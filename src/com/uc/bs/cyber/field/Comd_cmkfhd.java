/**
 *  주시스템명 : 사이버지방세청 고도화
 *  업  무  명 : 공통
 *  기  능  명 : 사이버 - 결제원 공통전문
 *  클래스  ID : Comd_cmkfhd (사용안함)
 *  변경  이력 :
 * ------------------------------------------------------------------------
 *  작성자      	소속  		    일자            Tag           내용
 * ------------------------------------------------------------------------
 *  송동욱       유채널(주)      2011.06.09         %01%         최초작성
 *
 */

package com.uc.bs.cyber.field;

import com.uc.core.MapForm;
import com.uc.egtob.net.FieldList;
import com.uc.bs.cyber.CbUtil;
/**
 * @author Administrator
 *
 */
public class Comd_cmkfhd {
	
	private FieldList fieldList ;
	
	private int      len   = 0;
	/**
	 * 생성자 : 공통전문 셋팅 : 파싱하기 위함...
	 */
	public Comd_cmkfhd() {
		// TODO Auto-generated constructor stub
		
		/*공통전문 셋*/
		fieldList = new FieldList();
				
		fieldList.add("LEN"              , 4  ,  "H"); //전문길이
		fieldList.add("TX_ID"            , 3  ,  "C"); //"IGN" 셋팅 :업무구분
		fieldList.add("BNK_CODE"         , 3  ,  "H"); //은행/센터코드
		fieldList.add("TX_GUBUN"         , 4  ,  "H"); //전문종별구분코드
		fieldList.add("PROGRAM_ID"       , 6  ,  "H"); //"전문의 거래 종류를 구분한다.모든 전문에 반드시 SET해 주어야 한다." 
		fieldList.add("STS_CODE"         , 3  ,  "H"); //전문 Format Error 발생시 오류가 발생된 전문의 항목번호
		fieldList.add("RS_FLAG"          , 1  ,  "C"); //출금 은행(B), 센터(C), 인터넷지로이용기관(G)
		fieldList.add("RESP_CODE"        , 3  ,  "C"); //"BLANK"
		fieldList.add("TX_DATE"          , 12 ,  "H"); //"YYMMDDhhmmss"
		fieldList.add("BCJ_NO"           , 12 ,  "C"); //[은행:금융기관공동코드(3) / 센터:“0CT”] + “0” + 일련번호(8자리)
		fieldList.add("GCJ_NO"           , 12 ,  "C"); //[이용기관:“0”+발행기관분류코드(2) / 센터:“0CT”] + “0” + 일련번호(8)
		fieldList.add("GPUB_CODE"        , 2  ,  "H"); //89:통합지방세입금 (대량납부 및 일괄취소)
		fieldList.add("GJIRO_NO"         , 7  ,  "H"); //"0000000"
		fieldList.add("FILLER"           , 2  ,  "H"); //예비영역
		
		len = fieldList.getFieldListLen();
		
	}

	/**
	 * 전문송신 버퍼 생성 : 송신시 공통전문을 셋팅한다.
	 * @param taxgb
	 * @param giroId
	 * @param trSeq
	 * @param procId
	 * @param sts_cd
	 * @param buffSize
	 * @return
	 * @throws Exception
	 */
	public byte[] getHeadBuffer(String taxgb, String giroId, String trRes, String procId, String sts_cd, String bcj_no, String gcj_no, int buffSize) throws Exception{
		
		MapForm headMap = new MapForm();
		                                                              
		headMap.setMap("LEN"              , Integer.toString(buffSize + fieldList.getFieldListLen()- 4));  //전문길이
		headMap.setMap("TX_ID"            , "IGN"      );  //"IGN" 셋팅                                                                
		headMap.setMap("BNK_CODE"         , "099"      );  //3 (지자체는 "099")                                                                          
		headMap.setMap("TX_GUBUN"         , taxgb      );  //4  
		headMap.setMap("STS_CODE"         , sts_cd     );  //3  전문 Format Error 발생시 오류가 발생된 전문의 항목번호        
		headMap.setMap("PROGRAM_ID"       , procId     );  //6  전문의 거래 종류를 구분한다.모든 전문에 반드시 SET해 주어야 한다.  
		headMap.setMap("RS_FLAG"          , "G"        );  //1  출금 은행(B), 센터(C), 인터넷지로이용기관(G)                              
		headMap.setMap("RESP_CODE"        , trRes      );  //3  BLANK                                                                   
		headMap.setMap("TX_DATE"          , CbUtil.getCurrent("yyMMddHHmmss"));  //12 "YYMMDDhhmmss"                                                            
		headMap.setMap("BCJ_NO"           , bcj_no     );  //12 [은행:금융기관공동코드(3) / 센터:“0CT”] + “0” + 일련번호(8자리)       
		headMap.setMap("GCJ_NO"           , gcj_no     );  //12 [이용기관:“0”+발행기관분류코드(2) / 센터:“0CT”] + “0” + 일련번호(8) 
		headMap.setMap("GPUB_CODE"        , "26"       );  //2  89:통합지방세입금 (대량납부 및 일괄취소)                                  
		headMap.setMap("GJIRO_NO"         , "1000685"  );  //7 "0000000"                                                                 
		headMap.setMap("FILLER"           , "00"       );  //2 예비영역  

		return fieldList.makeMessageByte(headMap);
		
	}
		
	/**
	 * @param taxgb :: 전문 구분
	 * @param giroId :: 전문 이용기관
	 * @param trSeq :: 전문일련번호 
	 * @param procId :: 응답받을 프로세스ID (사용자영역)
	 * @param sts_cd ::
	 * @param sendBuff :: 송신 버퍼
	 * @return
	 * @throws Exception
	 */
	public byte[] getBuffer(String taxgb, String giroId, String trSeq, String procId, String sts_cd, String bcj_no, String gcj_no, byte[] buff) throws Exception{
		
		byte[] headBuff = getHeadBuffer(taxgb, giroId, trSeq, procId, sts_cd, bcj_no, gcj_no, buff.length);
		
		byte[] retBuff = new byte[getLen() + buff.length];
		
		System.arraycopy(headBuff, 0, retBuff, 0, headBuff.length);

		System.arraycopy(buff, 0, retBuff, headBuff.length, buff.length);
		
		return retBuff;

	}

	/**
	 * 응답전문 생성
	 * @param headMap  :: 시스템공통 Header Map
	 * @param resCd    :: 응답코드
	 * @param resMsg   :: 오류메시지
	 * @param resBuff  :: 전송할 버퍼
	 * @param buffLen  :: 전송할 버퍼 길이
	 * @return
	 * @throws Exception 
	 */
	public byte[] getResBuffer(MapForm headMap, String resCd, String resMsg, byte[] resBuff, int buffLen) throws Exception {
		
		int realLen = buffLen>resBuff.length?resBuff.length:buffLen;
		
		byte[] retBuff = new byte[fieldList.getFieldListLen() + realLen];
		
		headMap.setMap("LEN"		    , Integer.toString(realLen + fieldList.getFieldListLen()- 4));  // 전문길이 Length 4은 빼준다 
		headMap.setMap("TX_DATE"		, CbUtil.getCurrent("yyyyMMddHHmmss") );                        // 전문발생시간  
		headMap.setMap("RESP_CODE"		, resCd);                                                       // 응답코드 

		byte[] headBuff =  fieldList.makeMessageByte(headMap);
		
		System.arraycopy(headBuff, 0, retBuff, 0, headBuff.length);
		
		System.arraycopy(resBuff, 0, retBuff, headBuff.length, realLen);
		
		return retBuff;
	}	
	
	/**
	 * 응답용 전문생성
	 * @param headMap
	 * @param resCd
	 * @param resBuff
	 * @return
	 * @throws Exception 
	 */
	public byte[] getResBuffer(MapForm headMap, String resCd, String resMsg, byte[] resBuff) throws Exception {
		
		return getResBuffer(headMap, resCd, resMsg, resBuff, resBuff.length);
	}
	
	/**
	 * 전문버퍼를 Parsing 하여 MapForm에 담아줌
	 * @param buffer 
	 * @return
	 * @throws Exception
	 */
	public MapForm parseBuffer(byte[] buffer) throws Exception{
		
			MapForm headMap = null;

			try {
				headMap = fieldList.parseMessage(buffer, 0);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}

		return headMap;
	}


	/**
	 * 
	 * @param buffer
	 * @param position
	 * @return
	 * @throws Exception
	 */
	public MapForm parseBuffer(byte[] buffer, int position) throws Exception{
		
		MapForm headMap = null;

		try {
			headMap = fieldList.parseMessage(buffer, position);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

		return headMap;
	}	
	
	/**
	 * 
	 * @return
	 */
	public int getLen() {
		// TODO Auto-generated method stub
		return this.len;
		
	}


	/**
	 * 
	 * @return
	 */
	public FieldList getFieldList() {
		// TODO Auto-generated method stub
		return this.fieldList;
	}
	
}
