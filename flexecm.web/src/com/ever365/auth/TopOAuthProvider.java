 package com.ever365.auth;
 
 import com.ever365.utils.WebUtils;
 import java.io.UnsupportedEncodingException;
 import java.net.URLDecoder;
 import java.util.HashMap;
 import java.util.Map;
 import org.json.JSONObject;
 
 public class TopOAuthProvider
   implements OAuthProvider
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
 
/* 27 */     props.put("code", code);
/* 28 */     props.put("client_id", "21796075");
/* 29 */     props.put("client_secret", "f681d9f9210f16bcaa12f32e69407947");
/* 30 */     props.put("redirect_uri", "http://godbuy.ever365.com/oauth/top");
/* 31 */     props.put("view", "web");
 
/* 33 */     JSONObject jso = WebUtils.doPost(url, props);
 
/* 35 */     Map map = WebUtils.jsonObjectToMap(jso);
     try
     {
/* 38 */       map.put("uid", URLDecoder.decode(map.get("taobao_user_nick").toString(), "UTF-8"));
/* 39 */       map.put("at", map.get("access_token"));
     } catch (UnsupportedEncodingException localUnsupportedEncodingException) {
     }
/* 42 */     return map;
   }
 
   public String getName()
   {
/* 47 */     return "top";
   }
 }

/* Location:           D:\360云盘\Desktops\T410\a.jar
 * Qualified Name:     com.ever365.auth.TopOAuthProvider
 * JD-Core Version:    0.6.0
 */