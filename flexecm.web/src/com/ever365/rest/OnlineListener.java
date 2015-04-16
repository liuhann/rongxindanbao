package com.ever365.rest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.http.HttpSessionAttributeListener;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
 
public class OnlineListener implements HttpSessionListener, HttpSessionAttributeListener  {

	Logger logger = Logger.getLogger(OnlineListener.class.getName());
	public static List<String> loggedUsers = Collections.synchronizedList(new ArrayList<String>());
   /**
    * 用户登录时候，把用户的信息存到userSession里
    */
   public void attributeAdded(HttpSessionBindingEvent sbe) {
	   if (AuthenticationUtil.SESSION_CURRENT_USER.equals(sbe.getName())) {
		   loggedUsers.add(sbe.getValue().toString());
		   logger.info("user logon: " + sbe.getValue().toString());
	   }
   }
   
	@Override
	public void attributeRemoved(HttpSessionBindingEvent sbe) {
		if (AuthenticationUtil.SESSION_CURRENT_USER.equals(sbe.getName())) {
			loggedUsers.remove(sbe.getValue().toString());
			logger.info("user logout: " + sbe.getValue().toString());
		}
	}
	

	@Override
	public void attributeReplaced(HttpSessionBindingEvent arg0) {
		
	}

	@Override
	public void sessionCreated(HttpSessionEvent arg0) {
		
	}

	@Override
	public void sessionDestroyed(HttpSessionEvent se) {
		if (se.getSession().getAttribute(AuthenticationUtil.SESSION_CURRENT_USER)!=null) {
			loggedUsers.remove(se.getSession().getAttribute(AuthenticationUtil.SESSION_CURRENT_USER));
			logger.info("user logout: " + se.getSession().getAttribute(AuthenticationUtil.SESSION_CURRENT_USER));
		}
	}
}
 