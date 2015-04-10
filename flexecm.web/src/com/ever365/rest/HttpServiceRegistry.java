package com.ever365.rest;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class HttpServiceRegistry {
	public static final String METHOD_POST = "post";
	public static final String METHOD_GET = "get";
	private Map<String, MethodInvocation> posts = new HashMap();

	private Map<String, MethodInvocation> gets = new HashMap();

	public MethodInvocation getGet(String getUri) {
		return (MethodInvocation) this.gets.get(getUri);
	}

	public MethodInvocation getPost(String postUri) {
		return (MethodInvocation) this.posts.get(postUri);
	}

	public MethodInvocation getMethod(String method, String uri) {
		if ("get".equalsIgnoreCase(method)) {
			return (MethodInvocation) this.gets.get(uri);
		}

		if ("post".equalsIgnoreCase(method)) {
			return (MethodInvocation) this.posts.get(uri);
		}

		return null;
	}

	public void setInjectedResouce(Object object) {
		Method[] methods = object.getClass().getMethods();
		for (Method method : methods) {
			if (method.getAnnotation(RestService.class) != null) {
				RestService rs = (RestService) method
						.getAnnotation(RestService.class);
				MethodInvocation mi = new MethodInvocation();
				mi.setMethod(method);
				mi.setService(object);
				mi.setRunAsAdmin(rs.runAsAdmin());
				mi.setRandcode(rs.rndcode());
				mi.setMultipart(rs.multipart());
				mi.setUri(rs.uri());
				mi.setAuthenticated(rs.authenticated());
				mi.setWebcontext(rs.webcontext());
				mi.setRequireAt(rs.reqireAt());
				Annotation[][] paramAnno = method.getParameterAnnotations();
				Class[] paramTypes = method.getParameterTypes();

				for (int i = 0; i < paramAnno.length; i++) {
					if ((paramAnno[i].length == 1)
							&& ((paramAnno[i][0] instanceof RestParam))) {
						mi.pushParam(((RestParam) paramAnno[i][0]).value(),
								paramTypes[i]);
						mi.pushParamRequired(
								((RestParam) paramAnno[i][0]).value(),
								((RestParam) paramAnno[i][0]).required());
					}
				}
				if (("post".equalsIgnoreCase(rs.method()))
						&& (rs.uri() != null) && (!rs.uri().equals(""))) {
					MethodInvocation methodInvocation = (MethodInvocation) this.posts
							.get(rs.uri());

					if (methodInvocation != null) {
						throw new RuntimeException("uri:" + rs.uri()
								+ "already been injected by other class ");
					}
					this.posts.put(rs.uri(), mi);
				}
				if (("get".equalsIgnoreCase(rs.method())) && (rs.uri() != null)
						&& (!rs.uri().equals(""))) {
					MethodInvocation methodInvocation = (MethodInvocation) this.gets
							.get(rs.uri());

					if (methodInvocation != null) {
						throw new RuntimeException("uri:" + rs.uri()
								+ "already been injected by other class ");
					}
					this.gets.put(rs.uri(), mi);
				}
			}
		}
	}

	public void setInjectedServices(List<Object> injectedServices) {
		for (Iterator localIterator = injectedServices.iterator(); localIterator
				.hasNext();) {
			Object object = localIterator.next();
			setInjectedResouce(object);
		}
	}
}
