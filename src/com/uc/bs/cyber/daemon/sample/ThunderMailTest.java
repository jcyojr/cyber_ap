package com.uc.bs.cyber.daemon.sample;

import tm.massmail.sendapi.ThunderMassMailSender;
import tm.massmail.sendapi.ThunderMassMail;

public class ThunderMailTest {

	public static void main(String[] args) {
		
		ThunderMassMailSender tms = new ThunderMassMailSender(); 
		ThunderMassMail tm = new ThunderMassMail();               

		tm.setThunderMailURL("210.103.81.88:8080");			//�������UI url
		tm.setWriter ("etax");					//���� �ۼ��� ID

		tm.setMailTitle("[$name]�� ���ڰ��� �׽�Ʈ�Դϴ�");			//���� ����
		tm.setSenderEmail("localtax@bs21.net"); 			//������ ��� �̸���
		tm.setSenderName("���̹����漼û");				//������ ��� �̸�
		tm.setReceiverName("[$name] ��");				//�޴� ��� �̸�
		
		tm.setContentType("template");
		
		//���� ���� Ÿ�� content: ���� ���� ���� ����, template: ���� ���ø� ���
		// tm.setMailContent("[$name]���� ������ �������� �����մϴ�."); 
		//setContentType�� content �϶� ���� ���� �ʼ�

		tm.setTemplate_id("1");
		
		tm.setTargetType("string");	
		//����� ��� ���� 
		//string : string���·� �Ķ���Ϳ� ���� ����, query : ����� ���� ������ ����
		tm.setTargetString("�̸���,�����ڸ�freeb73@naver.com,���Ϩ�daewan@uchannel.co.kr,������daewan@nate.com,����Ʈ��dasansystem@gmail.com,ǥ���Ѩ�hani@uchannel.co.kr,ǥ���Ѩ�0172521281@paran.com,�۹ڻ�");   			
		//setTargetString�� ��string�� �� �� ����� ���� �ʼ�

		tm.setFileOneToOne("[$email]���̸��ϩ�[$name]�������ڸ�");   
		//�ϴ���ġȯ ����
		//��� ������ �ϴ��� ġȯ��
		//[$email],[$cellPhone],[$name],[$customerID],[$etc1]~[$etc16]

		// System.out.println("TM==" + tm.getMailContent());
		
		String result = tms.send(tm);			//���� �߼�, ���� ��� ����
		
		System.out.println("MAIL SEND RESULT==" + result);
		
	}

}
