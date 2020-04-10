/**
 *  주시스템명 : 사이버지방세청 고도화
 *  업  무  명 : 공통
 *  기  능  명 : 데몬공통 Interface
 *  클래스  ID : Codm_interface
 *  변경  이력 :
 * ------------------------------------------------------------------------
 *  작성자      	소속  		    일자            Tag           내용
 * ------------------------------------------------------------------------
 *  Administrator   u c         2011.04.24         %01%         최초작성
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
