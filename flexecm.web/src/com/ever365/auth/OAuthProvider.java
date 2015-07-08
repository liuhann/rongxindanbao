package com.ever365.auth;

import java.util.Map;

public abstract interface OAuthProvider
{
  public static final String CODE = "code";
  public static final String ACCESS_TOKEN = "at";
  public static final String USERID = "uid";
  public static final String REAL_NAME = "rn";

  public abstract String getCode();

  public abstract Map<String, Object> authorize(String paramString);

  public abstract boolean binding();

  public abstract String getName();
}