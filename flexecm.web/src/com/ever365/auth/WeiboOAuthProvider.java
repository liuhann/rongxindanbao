/*     */ package com.ever365.auth;
/*     */ 
/*     */ import com.ever365.mongo.MongoDataSource;
/*     */ import com.ever365.rest.AuthenticationUtil;
/*     */ import com.ever365.rest.RestParam;
/*     */ import com.ever365.rest.RestService;
/*     */ import com.ever365.utils.MapUtils;
/*     */ import com.ever365.utils.WebUtils;
/*     */ import com.mongodb.BasicDBObject;
/*     */ import com.mongodb.BasicDBObjectBuilder;
/*     */ import com.mongodb.DBCollection;
/*     */ import com.mongodb.DBCursor;
/*     */ import com.mongodb.DBObject;
/*     */ import java.io.PrintStream;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Date;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.logging.Logger;
/*     */ import org.json.JSONException;
/*     */ import org.json.JSONObject;
/*     */ 
/*     */ public class WeiboOAuthProvider
/*     */   implements OAuthProvider
/*     */ {
/*     */   public static final String COLL_WBUERS = "wbusrs";
/*  28 */   Logger logger = Logger.getLogger(WeiboOAuthProvider.class.getName());
/*     */   private MongoDataSource dataSource;
/*     */   public static final String LAINA = "3244273155";
/* 240 */   private Map<String, Map> infoCache = new HashMap();
/*     */ 
/*     */   public void setDataSource(MongoDataSource dataSource)
/*     */   {
/*  32 */     this.dataSource = dataSource;
/*     */   }
/*     */ 
/*     */   public String getCode()
/*     */   {
/*  41 */     return "code";
/*     */   }
/*     */ 
/*     */   public Map<String, Object> authorize(String code)
/*     */   {
/*  48 */     String url = "https://api.weibo.com/oauth2/access_token";
/*  49 */     String cliend_id = "386267454";
/*  50 */     String client_secret = "6ad8466f1fe349cd641c9637ba0db378";
/*     */ 
/*  52 */     String at = null;
/*  53 */     String uid = null;
/*     */     try {
/*  55 */       Map params = new HashMap();
/*  56 */       params.put("client_id", cliend_id);
/*  57 */       params.put("client_secret", client_secret);
/*  58 */       params.put("grant_type", "authorization_code");
/*  59 */       params.put("code", code);
/*  60 */       params.put("redirect_uri", "http://www.luckyna.com/oauth/weibo");
/*  61 */       JSONObject json = WebUtils.doPost(url, params);
/*     */ 
/*  63 */       if ((json != null) && (json.has("access_token")) && (json.getString("access_token") != null)) {
/*  64 */         at = json.getString("access_token");
/*  65 */         uid = json.getString("uid");
/*  66 */         this.logger.info("uid=" + uid + " at:" + at);
/*  67 */         Map wbinfo = refreshWeiboInfo(uid, at);
/*  68 */         if (wbinfo == null) return null;
/*  69 */         asFans(uid, "3244273155", at);
/*  70 */         wbinfo.put("uid", uid);
/*  71 */         return wbinfo;
/*     */       }
/*     */     } catch (Exception e) {
/*  74 */       e.printStackTrace();
/*     */     }
/*  76 */     return null;
/*     */   }
/*     */ 
/*     */   public boolean asFans(String wo, String uid, String at) {
/*  80 */     if (wo.equals(uid)) return true;
/*     */ 
/*  82 */     String url = "https://api.weibo.com/2/friendships/create.json";
/*  83 */     Map params = new HashMap();
/*  84 */     params.put("access_token", at);
/*  85 */     params.put("uid", uid);
/*  86 */     JSONObject result = WebUtils.doPost(url, params);
/*     */ 
/*  88 */     if (result.has("error")) {
/*     */       try {
/*  90 */         this.logger.info(wo + " fans->" + uid + "   error: " + result.getString("error"));
/*     */       } catch (JSONException localJSONException) {
/*     */       }
/*  93 */       return false;
/*     */     }
/*  95 */     return true;
/*     */   }
/*     */ 
/*     */   public void init()
/*     */   {
/*     */   }
/*     */ 
/*     */   public static void main(String[] args)
/*     */   {
/* 104 */     WeiboOAuthProvider wop = new WeiboOAuthProvider();
/* 105 */     System.out.println(System.currentTimeMillis() - 518400000L);
/* 106 */     System.out.println(new Date(1388246400000L));
/*     */   }
/*     */ 
/*     */   @RestService(uri="/dfans/list", authenticated=false)
/*     */   public Map<String, Map<String, Object>> getAlldfans()
/*     */   {
/* 113 */     Map result = new HashMap();
/* 114 */     DBCursor c1 = this.dataSource.getCollection("wbusrs").find(new BasicDBObject("e", MapUtils.newMap("$ne", null)));
/* 115 */     while (c1.hasNext()) {
/* 116 */       DBObject dbo = c1.next();
/* 117 */       result.put(dbo.get("uid").toString(), dbo.toMap());
/*     */     }
/* 119 */     return result;
/*     */   }
/*     */ 
/*     */   @RestService(uri="/dfans/clear", authenticated=false)
/*     */   public void clearAlldfans() {
/* 125 */     DBCursor c1 = this.dataSource.getCollection("wbusrs").find(new BasicDBObject("e", MapUtils.newMap("$ne", null)));
/* 126 */     while (c1.hasNext()) {
/* 127 */       DBObject dbo = c1.next();
/* 128 */       dbo.removeField("e");
/* 129 */       dbo.removeField("p");
/* 130 */       this.dataSource.getCollection("wbusrs").update(new BasicDBObject("uid", dbo.get("uid")), dbo, true, false);
/*     */     }
/*     */   }
/*     */ 
/*     */   @RestService(uri="/user/email", authenticated=false)
/*     */   public void setUserLoginAccount(@RestParam("u") String user, @RestParam("email") String email, @RestParam("p") String p) {
/* 136 */     DBObject one = this.dataSource.getCollection("wbusrs").findOne(new BasicDBObject("uid", user));
/* 137 */     if (one != null) {
/* 138 */       one.put("e", email);
/* 139 */       one.put("p", p);
/* 140 */       this.dataSource.getCollection("wbusrs").update(new BasicDBObject("uid", user), one, true, false);
/*     */     } else {
/* 142 */       this.dataSource.getCollection("wbusrs").insert(new DBObject[] { BasicDBObjectBuilder.start()
/* 143 */         .add("uid", user).add("e", email).add("p", p).get() });
/*     */     }
/*     */   }
/*     */ 
/*     */   @RestService(uri="/dfans/token/expired", authenticated=false)
/*     */   public List<Map> getExpiredToken() {
/* 149 */     List result = new ArrayList();
/*     */ 
/* 151 */     DBCursor c1 = this.dataSource.getCollection("wbusrs").find(new BasicDBObject("e", MapUtils.newMap("$ne", null)));
/* 152 */     while (c1.hasNext()) {
/* 153 */       DBObject dbo = c1.next();
/* 154 */       Map m = dbo.toMap();
/*     */ 
/* 156 */       if (m.get("rf") == null) {
/* 157 */         result.add(m);
/*     */       }
/* 161 */       else if (((Long)m.get("rf")).longValue() + 518400000L < System.currentTimeMillis()) {
/* 162 */         result.add(m);
/*     */       }
/*     */     }
/* 165 */     return result;
/*     */   }
/*     */ 
/*     */   @RestService(uri="/dfans/expired", authenticated=false)
/*     */   public void setAsExpired(@RestParam("u") String user) {
/* 171 */     DBObject one = this.dataSource.getCollection("wbusrs").findOne(new BasicDBObject("uid", user));
/* 172 */     DBObject dbo = new BasicDBObject("_id", Long.valueOf(Long.parseLong(user)));
/* 173 */     if (one != null) {
/* 174 */       this.logger.info("expired ");
/* 175 */       dbo.put("email", one.get("e"));
/*     */     }
/*     */ 
/* 178 */     DBObject existed = this.dataSource.getCollection("expired").findOne(dbo);
/* 179 */     if (existed == null) {
/* 180 */       existed = dbo;
/* 181 */       existed.put("inc", Integer.valueOf(1));
/*     */     } else {
/* 183 */       existed.put("inc", Integer.valueOf(((Integer)existed.get("inc")).intValue() + 1));
/*     */     }
/* 185 */     this.dataSource.getCollection("expired").update(dbo, existed, true, false);
/*     */   }
/*     */   @RestService(uri="/dfans/add", authenticated=true)
/*     */   public void addbillfans(String wo, String ta) {
/* 190 */     DBObject wodbo = this.dataSource.getCollection("wbusrs").findOne(new BasicDBObject("uid", wo));
/* 191 */     if (wodbo == null) {
/* 192 */       return;
/*     */     }
/* 194 */     if (wodbo.get("at") == null) {
/* 195 */       this.logger.info(wodbo.get("rn") + " 的 at 不存在");
/*     */     }
/*     */ 
/* 198 */     DBObject tadbo = this.dataSource.getCollection("wbusrs").findOne(new BasicDBObject("uid", ta));
/* 199 */     if (tadbo == null) {
/* 200 */       return;
/*     */     }
/* 202 */     if (tadbo.get("at") == null) {
/* 203 */       this.logger.info(tadbo.get("rn") + " 的 at 不存在");
/*     */     }
/* 205 */     asFans(wo, ta, wodbo.get("at").toString());
/* 206 */     asFans(ta, wo, tadbo.get("at").toString());
/*     */   }
/*     */ 
/*     */   public void cancelValidate() {
/* 210 */     DBObject one = this.dataSource.getCollection("wbusrs").findOne(new BasicDBObject("uid", AuthenticationUtil.getCurrentUser()));
/* 211 */     if (one != null) {
/* 212 */       String at = one.get("at").toString();
/* 213 */       WebUtils.doGet("https://api.weibo.com/oauth2/revokeoauth2?access_token=" + at);
/*     */     }
/*     */   }
/*     */ 
/*     */   public String getName()
/*     */   {
/* 219 */     return "weibo";
/*     */   }
/*     */ 
/*     */   public Map<String, Object> getCurWbInfo() {
/* 223 */     if (AuthenticationUtil.getCurrentUser() == null) return null;
/*     */ 
/* 225 */     return getFullWeiboInfo(AuthenticationUtil.getCurrentUser());
/*     */   }
/*     */   @RestService(method="GET", uri="/weibo/info", authenticated=false)
/*     */   public Map<String, Object> getWeiboInfo(@RestParam("u") String user) {
/* 230 */     Map m = getFullWeiboInfo(user);
/* 231 */     if (m == null) return null;
/* 232 */     Map n = new HashMap(m);
/*     */ 
/* 234 */     if ((user != null) && (!user.equals(AuthenticationUtil.getCurrentUser()))) {
/* 235 */       n.remove("at");
/*     */     }
/* 237 */     return n;
/*     */   }
/*     */ 
/*     */   public Map<String, Object> getFullWeiboInfo(String user)
/*     */   {
/* 243 */     if (user == null) {
/* 244 */       user = AuthenticationUtil.getCurrentUser();
/*     */     }
/* 246 */     if (this.infoCache.get(user) == null) {
/* 247 */       DBObject one = this.dataSource.getCollection("wbusrs").findOne(new BasicDBObject("_id", Long.valueOf(Long.parseLong(user))));
/* 248 */       if (one != null) {
/* 249 */         this.infoCache.put(user, one.toMap());
/*     */       }
/*     */     }
/* 252 */     return (Map)this.infoCache.get(user);
/*     */   }
/*     */ 
/*     */   public Map<String, Object> getWeiboInfoByRealName(String rn) {
/* 256 */     DBObject one = this.dataSource.getCollection("wbusrs").findOne(new BasicDBObject("rn", rn));
/* 257 */     return one.toMap();
/*     */   }
/*     */   @RestService(method="GET", uri="/weibo/refresh")
/*     */   public Map<String, Object> refreshWeiboInfo() {
/* 262 */     DBObject one = this.dataSource.getCollection("wbusrs").findOne(new BasicDBObject("uid", AuthenticationUtil.getCurrentUser()));
/* 263 */     if (one != null) {
/* 264 */       String at = one.get("at").toString();
/* 265 */       String uid = one.get("uid").toString();
/*     */       try
/*     */       {
/* 268 */         return refreshWeiboInfo(uid, at);
/*     */       } catch (JSONException e) {
/* 270 */         return null;
/*     */       }
/*     */     }
/*     */ 
/* 274 */     return null;
/*     */   }
/*     */ 
/*     */   public Map<String, Object> refreshWeiboInfo(String uid, String at)
/*     */     throws JSONException
/*     */   {
/* 306 */     JSONObject showJson = WebUtils.doGet("https://api.weibo.com/2/users/show.json?uid=" + uid + 
/* 307 */       "&access_token=" + at);
/*     */ 
/* 309 */     if (showJson != null) {
/* 310 */       Map userMap = WebUtils.jsonObjectToMap(showJson);
/*     */ 
/* 312 */       if (userMap.get("error") != null) {
/* 313 */         this.logger.info("error refresh: " + showJson.toString());
/* 314 */         return null;
/*     */       }
/*     */ 
/* 317 */       if (userMap.get("screen_name") == null) {
/* 318 */         this.logger.info("user has no screen name: " + showJson.toString());
/* 319 */         return null;
/*     */       }
/* 321 */       userMap.put("_id", Long.valueOf(Long.parseLong(uid)));
/* 322 */       userMap.put("at", at);
/* 323 */       this.dataSource.getCollection("wbusrs").update(new BasicDBObject("_id", Long.valueOf(Long.parseLong(uid))), new BasicDBObject(userMap), true, false);
/* 324 */       return userMap;
/*     */     }
/* 326 */     return null;
/*     */   }
/*     */ }

/* Location:           D:\360云盘\Desktops\T410\a.jar
 * Qualified Name:     com.ever365.auth.WeiboOAuthProvider
 * JD-Core Version:    0.6.0
 */