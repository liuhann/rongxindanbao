package com.ever365.auth;

public interface Tenantable {

	public static ThreadLocal<String> tenant = new ThreadLocal<String>();
	public static ThreadLocal<String> employer = new ThreadLocal<String>();
}
