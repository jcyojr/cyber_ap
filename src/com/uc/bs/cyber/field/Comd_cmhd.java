/**
 *  주시스템명 : 사이버지방세청 고도화
 *  업  무  명 : 공통
 *  기  능  명 : 부산은행 대외계연계 시스템 공통전문
 *  클래스  ID : Comd_cmhd
 *  변경  이력 :
 * ------------------------------------------------------------------------
 *  작성자      	소속  		    일자            Tag           내용
 * ------------------------------------------------------------------------
 *  김대완       유채널(주)      2010.11.30         %01%         최초작성
 *
 */

package com.uc.bs.cyber.field;

import com.uc.core.MapForm;
import com.uc.egtob.net.FieldList;
import com.uc.bs.cyber.CbUtil;

public class Comd_cmhd {

	private FieldList fieldList ;
	
	private int      len   = 0;
	/**
	 * 생성자
	 */
	public Comd_cmhd() {

		fieldList = new FieldList();
		
		fieldList.add("LENGTH",   	6,    "H");    // 전문길이
		fieldList.add("SYS_ID",   	4,    "C");    // 전문구분자
		fieldList.add("SR_FLAG",   	1,    "C");    // 요청/응답 구분
		fieldList.add("TR_TIME",   	14,   "C");    // 전문발생시간
		fieldList.add("SVC_ID",   	4,    "C");    // 서비스ID 
		fieldList.add("MSG_ID",  	4,    "C");    // 전문구분번호
		fieldList.add("TR_SEQ",   	10,   "H");    // 거래관리번호
		fieldList.add("RESP_CD",   	6,    "C");    // 응답코드
		fieldList.add("RESP_MSG",   80,   "C");    // 응답메시지 
		fieldList.add("USER_AREA",  20,   "C");    // User_Area 
		fieldList.add("FILLER",   	11,   "C");    // 예비영역
   
		
		len = fieldList.getFieldListLen();
	}
	

	/**
	 * 전문송신 버퍼 생성
	 * @param svcId
	 * @param msgId
	 * @param trSeq
	 * @param procId
	 * @param buffSize
	 * @return
	 * @throws Exception
	 */
	public byte[] getHeadBuffer(String svcId, String msgId, String trSeq, String procId,  int buffSize) throws Exception{
		
		
		MapForm headMap = new MapForm();
	
		headMap.setMap("LENGTH"		,  Integer.toString(buffSize + fieldList.getFieldListLen()- 6));  // 전문길이 
		headMap.setMap("SYS_ID"		,  "ESCH"    );  // 전문구분자 
		headMap.setMap("SR_FLAG"	,  "S"       );  // 요청/응답 구분 
		headMap.setMap("TR_TIME"	,  CbUtil.getCurrent("yyyyMMddHHmmss") );  // 전문발생시간 
		headMap.setMap("SVC_ID"		,  svcId    );  // 서비스ID 
		headMap.setMap("MSG_ID"		,  msgId    );  // 메시지ID 
		headMap.setMap("TR_SEQ"		,  trSeq    );  // 거래관리번호 
		headMap.setMap("RESP_CD"	,  "000000");	// 응답코드 
		headMap.setMap("RESP_MSG"	,  " "); 		// 응답메시지
		headMap.setMap("USER_AREA"	,  procId);  	// 고유영역 
		headMap.setMap("FILLER"		,  " "); 		// 예비영역

		
		return fieldList.makeMessageByte(headMap);
		
	}
	
	/**
	 * 전문송신 버퍼 생성
	 * @param svcId
	 * @param msgId
	 * @param trSeq
	 * @param procId
	 * @param buffSize
	 * @return
	 * @throws Exception
	 */
	public byte[] getHeadBuffer(String svcId, String msgId, String trSeq, String procId,  int buffSize, String sys_id) throws Exception{
		
		
		MapForm headMap = new MapForm();
	
		headMap.setMap("LENGTH"		,  Integer.toString(buffSize + fieldList.getFieldListLen()- 6));  // 전문길이 
		headMap.setMap("SYS_ID"		,  sys_id    );  // 전문구분자 
		headMap.setMap("SR_FLAG"	,  "S"       );  // 요청/응답 구분 
		headMap.setMap("TR_TIME"	,  CbUtil.getCurrent("yyyyMMddHHmmss") );  // 전문발생시간 
		headMap.setMap("SVC_ID"		,  svcId    );  // 서비스ID 
		headMap.setMap("MSG_ID"		,  msgId    );  // 메시지ID 
		headMap.setMap("TR_SEQ"		,  trSeq    );  // 거래관리번호 
		headMap.setMap("RESP_CD"	,  "000000");	// 응답코드 
		headMap.setMap("RESP_MSG"	,  " "); 		// 응답메시지
		headMap.setMap("USER_AREA"	,  procId);  	// 고유영역 
		headMap.setMap("FILLER"		,  " "); 		// 예비영역

		
		return fieldList.makeMessageByte(headMap);
		
	}
	
	/**
	 * @param svcId :: 전문 서비스ID
	 * @param msgId :: 전문 메시지ID
	 * @param trSeq :: 전문일련번호 
	 * @param procId :: 응답받을 프로세스ID (사용자영역)
	 * @param sendBuff :: 송신 버퍼
	 * @return
	 * @throws Exception
	 */
	public byte[] getBuffer(String svcId, String msgId, String trSeq, String procId,  byte[] buff) throws Exception{
		
		byte[] headBuff = getHeadBuffer(svcId, msgId, trSeq, procId, buff.length);
		
		byte[] retBuff = new byte[getLen() + buff.length];
		
		System.arraycopy(headBuff, 0, retBuff, 0, headBuff.length);

		System.arraycopy(buff, 0, retBuff, headBuff.length, buff.length);
		
		return retBuff;

	}

	
	/**
	 * @param svcId :: 전문 서비스ID
	 * @param msgId :: 전문 메시지ID
	 * @param trSeq :: 전문일련번호 
	 * @param procId :: 응답받을 프로세스ID (사용자영역)
	 * @param sendBuff :: 송신 버퍼
	 * @return
	 * @throws Exception
	 */
	public byte[] getBuffer(String svcId, String msgId, String trSeq, String procId,  byte[] buff, String sys_id) throws Exception{
		
		byte[] headBuff = getHeadBuffer(svcId, msgId, trSeq, procId, buff.length, sys_id);
		
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
		
		/**
		 * 2011.02.07 -- 김대완 --
		 * 업무단에서 실수로 응답전문 생성시 조건에 맞지않으면 오류로 처리하도록 수정
		 */
		if(!headMap.getMap("SYS_ID").equals("JBBK") || !headMap.getMap("SR_FLAG").equals("S")) {
			
			throw new Exception("(" + headMap.getMap("SYS_ID") + "_" + headMap.getMap("SR_FLAG") + ") 에 대한 응답전문을 만들 수 없습니다");
		}
		
		headMap.setMap("LENGTH"		    , Integer.toString(realLen + fieldList.getFieldListLen()- 6));  // 전문길이 Length 6은 빼준다 
		headMap.setMap("SR_FLAG"		, "R"       );  // 요청/응답 구분 
		headMap.setMap("TR_TIME"		, CbUtil.getCurrent("yyyyMMddHHmmss") );  // 전문발생시간  
		headMap.setMap("RESP_CD"		, resCd);  // 응답코드 
		headMap.setMap("RESP_MSG"		, resMsg); 		// 응답메시지

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
