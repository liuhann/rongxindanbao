package com.ever365.rest;

import java.util.ArrayList;
import java.util.List;

public class AuthenticationUtil {
	public static String EMPTY_TENANT = "default";
	public static String SYSTEM = "system";

	public static String SESSION_ADMIN = "admin";
	public static String SESSION_CURRENT_USER = ".cu";
	public static String SESSION_CURRENT_USER_RN = ".rn";

	private static ThreadLocal<String> currentUser = new ThreadLocal();
	private static ThreadLocal<String> realName = new ThreadLocal();

	public static List<String> getCurrentAuthorities() {
		List r = new ArrayList();
		r.add((String) currentUser.get());
		return r;
	}

	public static void setRealName(String user) {
		realName.set(user);
	}

	public static String getRealName() {
		return (String) realName.get();
	}

	public static boolean isAdmin() {
		return SESSION_ADMIN.equals(currentUser.get());
	}

	public static void setCurrentUser(String user) {
		currentUser.set(user);
	}

	public static String getCurrentUser() {
		return (String) currentUser.get();
	}

	public static void clearCurrentSecurityContext() {
		currentUser.set(null);
		realName.set(null);
	}
}
