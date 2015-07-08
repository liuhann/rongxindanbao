 package com.ever365.auth;
 
 import com.ever365.utils.WebUtils;
 import java.io.UnsupportedEncodingException;
 import java.net.URLDecoder;
 import java.util.HashMap;
 import java.util.Map;
 import org.json.JSONObject;
 
 public class TopOAuthProvider implements OAuthProvider
 {
   private static final String TAOBAO_USER_NICK = "taobao_user_nick";
 
   public String getCode()
   {
     return "code";
   }
 
   public Map<String, Object> authorize(String code)
   {
     String url = "https://oauth.taobao.com/token";
    Map props = new HashMap();
     props.put("grant_type", "authorization_code");
 
     props.put("code", code);
     props.put("client_id", "21796075");
     props.put("client_secret", "f681d9f9210f16bcaa12f32e69407947");
     props.put("redirect_uri", "http://godbuy.ever365.com/oauth/top");
     props.put("view", "web");
 
     JSONObject jso = WebUtils.doPost(url, props);
 
     Map map = WebUtils.jsonObjectToMap(jso);
     try
     {
       map.put("uid", URLDecoder.decode(map.get("taobao_user_nick").toString(), "UTF-8"));
       map.put("at", map.get("access_token"));
     } catch (UnsupportedEncodingException localUnsupportedEncodingException) {
     }
     return map;
   }

     @Override
     public boolean binding() {
         return false;
     }

     public String getName()
   {
     return "top";
   }
 }

