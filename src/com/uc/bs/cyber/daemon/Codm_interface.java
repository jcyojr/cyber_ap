/**
 *  �ֽý��۸� : ���̹����漼û ��ȭ
 *  ��  ��  �� : ����
 *  ��  ��  �� : ������� Interface
 *  Ŭ����  ID : Codm_interface
 *  ����  �̷� :
 * ------------------------------------------------------------------------
 *  �ۼ���      	�Ҽ�  		    ����            Tag           ����
 * ------------------------------------------------------------------------
 *  Administrator   u c         2011.04.24         %01%         �����ۼ�
 *
 */
package com.uc.bs.cyber.daemon;
import org.springframework.context.ApplicationContext;

public interface Codm_interface {
	
	public void setContext(ApplicationContext context);

	public ApplicationContext getContext() ;	

    public Object getService(String beanName);
     
    public void initProcess() throws Exception;
}
