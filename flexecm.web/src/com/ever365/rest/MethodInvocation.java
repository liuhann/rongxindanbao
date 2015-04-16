 package com.ever365.rest;
 
 import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.fileupload.FileItem;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
 
 public class MethodInvocation
 {
   private static final String NULL = "null";
   private Method method;
   private String uri;
   private Object service;
   private boolean runAsAdmin;
   private boolean multipart;
   private boolean authenticated;
   private boolean cached;
   private boolean webcontext;
   private boolean requireAt;
   private boolean randcode;
   private LinkedHashMap<String, Class> paramsMap = new LinkedHashMap();
   private LinkedHashMap<String, Boolean> paramsRequired = new LinkedHashMap();
 
   SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
 
   
   public boolean needRandCode() {
	return randcode;
   }
	public void setRandcode(boolean randcode) {
		this.randcode = randcode;
	}
	public boolean isRequireAt()
   {
     return this.requireAt;
   }
   public void setRequireAt(boolean requireAt) {
     this.requireAt = requireAt;
   }
   public boolean isWebcontext() {
     return this.webcontext;
   }
   public void setWebcontext(boolean webcontext) {
     this.webcontext = webcontext;
   }
   public boolean isAuthenticated() {
     return this.authenticated;
   }
   public void setAuthenticated(boolean authenticated) {
     this.authenticated = authenticated;
   }
 
   public boolean isCached() {
     return this.cached;
   }
 
   public void setCached(boolean cached) {
     this.cached = cached;
   }
 
   public Method getMethod()
   {
     return this.method;
   }
 
   public void pushParam(String name, Class type) {
     this.paramsMap.put(name, type);
   }
   public void pushParamRequired(String name, boolean required) {
     if (required)
       this.paramsRequired.put(name, Boolean.valueOf(required));
   }
 
   public Object execute(Map<String, Object> map)
   {
     return internalExecute(map);
   }
 
   public Object internalExecute(Map<String, Object> map) {
     Object[] methodParams = getMethodParams(map);
     try {
       return this.method.invoke(this.service, methodParams);
     } catch (IllegalArgumentException e) {
       throw new HttpStatusException(HttpStatus.BAD_REQUEST);
     } catch (IllegalAccessException e) {
       throw new HttpStatusException(HttpStatus.METHOD_FAILURE);
     } catch (InvocationTargetException e) {
       if ((e.getTargetException() != null) && ((e.getTargetException() instanceof RuntimeException))) {
         throw ((RuntimeException)e.getTargetException());
       }
     }
 
     return null;
   }
 
   public Object[] getMethodParams(Map<String, Object> map)
   {
     if ((this.paramsMap.size() == 0) && (this.method.getParameterTypes().length == 1)) {
       return new Object[] { map };
     }
 
     Set<String> requiredParams = this.paramsRequired.keySet();
     for (String key : requiredParams) {
       if (map.get(key) == null) {
         System.out.println("Post key Required: " + key);
         throw new HttpStatusException(HttpStatus.BAD_REQUEST);
       }
     }
 
     Set<Entry<String, Class>> es = this.paramsMap.entrySet();
     Object[] methodParams = new Object[es.size()];
     int i = 0;
     for (Map.Entry entry : es) {
       methodParams[i] = convert((Class)entry.getValue(), map.get(entry.getKey()));
       i++;
     }
     return methodParams;
   }
 
   public Object convert(Class clazz, Object obj)
   {
     if ((obj == null) || ("null".equals(obj)) || ("".equals(obj))) {
       if (clazz.equals(Boolean.TYPE)) {
         return Boolean.FALSE;
       }
       if (clazz.equals(Integer.class)) {
         return Integer.valueOf(0);
       }
       if (clazz.equals(Long.class)) {
         return Long.valueOf(0L);
       }
       return null;
     }
     if (clazz.isInstance(obj)) {
       return obj;
     }
     if ((clazz == Boolean.class) && ((obj instanceof String))) {
       return Boolean.valueOf((String)obj);
     }
     if ((clazz.getName().equals("int")) || (clazz == Integer.class)) {
       return new Integer(obj.toString());
     }
     if ((clazz.getName().equals("long")) || (clazz == Long.class)) {
       return new Long(obj.toString());
     }
 
     if ((clazz == List.class) && ((obj instanceof String))) {
       try {
         JSONArray jsa = new JSONArray((String)obj);
         return jsonArrayToList(jsa);
       } catch (JSONException e) {
         return null;
       }
     }
 
     if ((clazz == Date.class) && ((obj instanceof String))) {
       try {
         return this.dateformat.parse((String)obj);
       } catch (ParseException e) {
         throw new HttpStatusException(HttpStatus.BAD_REQUEST);
       }
     }
 
     if ((clazz == Map.class) && ((obj instanceof String))) {
       try
       {
         JSONObject jsonObject = new JSONObject((String)obj);
         return jsonObjectToMap(jsonObject);
       } catch (JSONException e) {
    	   e.printStackTrace();
         return null;
       }
     }
     if ((clazz == InputStream.class) && ((obj instanceof FileItem))) {
       FileItem fi = (FileItem)obj;
       try {
         return fi.getInputStream();
       } catch (IOException e) {
         throw new HttpStatusException(HttpStatus.REQUEST_TIMEOUT);
       }
     }
 
     System.out.println("Param not match: " + clazz + "-->" + obj);
 
     throw new HttpStatusException(HttpStatus.BAD_REQUEST);
   }
 
   public void setMethod(Method method)
   {
     this.method = method;
   }
 
   public Object getService() {
     return this.service;
   }
 
   public void setService(Object service) {
     this.service = service;
   }
 
   public boolean isRunAsAdmin() {
     return this.runAsAdmin;
   }
 
   public void setRunAsAdmin(boolean runAsAdmin) {
     this.runAsAdmin = runAsAdmin;
   }
 
   public boolean isMultipart() {
     return this.multipart;
   }
 
   public void setMultipart(boolean multipart) {
     this.multipart = multipart;
   }
 
   public String getUri() {
     return this.uri;
   }
 
   public void setUri(String uri) {
     this.uri = uri;
   }
 
   public static List<Object> jsonArrayToList(JSONArray jsonArray) {
     List ret = new ArrayList();
     Object value = null;
     int length = jsonArray.length();
     for (int i = 0; i < length; i++) {
       try {
         value = jsonArray.get(i);
       } catch (JSONException e) {
         System.out.println(" there are no value with the index in the JSONArray");
         e.printStackTrace();
         return null;
       }
       if ((value instanceof JSONArray))
         ret.add(jsonArrayToList((JSONArray)value));
       else if ((value instanceof JSONObject))
         ret.add(jsonObjectToMap((JSONObject)value));
       else {
         ret.add(value);
       }
     }
 
     return ret.size() != 0 ? ret : Collections.emptyList();
   }
 
   public static Map<String, Object> jsonObjectToMap(JSONObject jsonObject) {
     Map ret = new HashMap();
     Object value = null;
     String key = null;
     for (Iterator keys = jsonObject.keys(); keys.hasNext(); ) {
       key = (String)keys.next();
       try {
         value = jsonObject.get(key);
       } catch (JSONException e) {
         System.out.println("the key is not found in the JSONObject");
         e.printStackTrace();
         return null;
       }
       if ((value instanceof JSONArray))
         ret.put(key, jsonArrayToList((JSONArray)value));
       else if ((value instanceof JSONObject))
         ret.put(key, jsonObjectToMap((JSONObject)value));
       else {
         ret.put(key, value);
       }
 
     }
 
     return ret.size() != 0 ? ret : null;
   }
 }

/* Location:           D:\360云盘\Desktops\T410\a.jar
 * Qualified Name:     com.ever365.rest.MethodInvocation
 * JD-Core Version:    0.6.0
 */