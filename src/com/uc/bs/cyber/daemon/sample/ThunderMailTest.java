package com.uc.bs.cyber.daemon.sample;

import tm.massmail.sendapi.ThunderMassMailSender;
import tm.massmail.sendapi.ThunderMassMail;

public class ThunderMailTest {

	public static void main(String[] args) {
		
		ThunderMassMailSender tms = new ThunderMassMailSender(); 
		ThunderMassMail tm = new ThunderMassMail();               

		tm.setThunderMailURL("210.103.81.88:8080");			//썬더메일UI url
		tm.setWriter ("etax");					//메일 작성자 ID

		tm.setMailTitle("[$name]님 전자고지 테스트입니다");			//메일 제목
		tm.setSenderEmail("localtax@bs21.net"); 			//보내는 사람 이메일
		tm.setSenderName("사이버지방세청");				//보내는 사람 이름
		tm.setReceiverName("[$name] 님");				//받는 사람 이름
		
		tm.setContentType("template");
		
		//메일 내용 타입 content: 메일 내용 직접 전송, template: 메일 템플릿 사용
		// tm.setMailContent("[$name]님의 생일을 진심으로 축하합니다."); 
		//setContentType이 content 일때 메일 내용 필수

		tm.setTemplate_id("1");
		
		tm.setTargetType("string");	
		//대상자 등록 유형 
		//string : string형태로 파라미터에 직접 전송, query : 대상자 추출 쿼리문 전송
		tm.setTargetString("이메일,납세자명Æfreeb73@naver.com,김대완Ædaewan@uchannel.co.kr,프리비Ædaewan@nate.com,개이트Ædasansystem@gmail.com,표승한Æhani@uchannel.co.kr,표승한Æ0172521281@paran.com,송박사");   			
		//setTargetString이 ‘string’ 일 때 대상자 정보 필수

		tm.setFileOneToOne("[$email]≠이메일ø[$name]≠납세자명");   
		//일대일치환 정보
		//사용 가능한 일대일 치환명
		//[$email],[$cellPhone],[$name],[$customerID],[$etc1]~[$etc16]

		// System.out.println("TM==" + tm.getMailContent());
		
		String result = tms.send(tm);			//메일 발송, 연동 결과 리턴
		
		System.out.println("MAIL SEND RESULT==" + result);
		
	}

}
