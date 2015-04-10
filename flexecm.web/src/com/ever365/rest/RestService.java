package com.ever365.rest;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface RestService {
	String method() default "GET"; 
	String uri();  
	String[] roles() default {};
	boolean rndcode() default false;
	boolean runAsAdmin() default false;
	boolean reqireAt() default false;
	boolean multipart() default false;
	boolean authenticated() default true;
	boolean webcontext() default false;
}
