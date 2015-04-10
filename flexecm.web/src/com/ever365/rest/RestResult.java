package com.ever365.rest;

import java.util.HashMap;
import java.util.Map;

public class RestResult {
	private Map<String, Object> session;
	private String redirect;
	private Object result;

	public Object getResult() {
		return result;
	}

	public void setResult(Object result) {
		this.result = result;
	}

	public Map<String, Object> getSession() {
		return this.session;
	}
	
	public void setSession(String key, Object value) {
		if (this.session==null) {
			this.session = new HashMap<String, Object>();
		}
		this.session.put(key, value);
	}

	public void setSession(Map<String, Object> session) {
		this.session = session;
	}

	public String getRedirect() {
		return this.redirect;
	}

	public void setRedirect(String redirect) {
		this.redirect = redirect;
	}
}
