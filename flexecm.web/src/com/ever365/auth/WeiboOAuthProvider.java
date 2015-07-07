 package com.ever365.auth;
 
 import com.ever365.mongo.MongoDataSource;
 import com.ever365.rest.AuthenticationUtil;
 import com.ever365.rest.RestParam;
 import com.ever365.rest.RestService;
 import com.ever365.utils.MapUtils;
 import com.ever365.utils.WebUtils;
 import com.mongodb.BasicDBObject;
 import com.mongodb.BasicDBObjectBuilder;
 import com.mongodb.DBCollection;
 import com.mongodb.DBCursor;
 import com.mongodb.DBObject;
 import java.io.PrintStream;
 import java.util.ArrayList;
 import java.util.Date;
 import java.util.HashMap;
 import java.util.List;
 import java.util.Map;
 import java.util.logging.Logger;
 import org.json.JSONException;
 import org.json.JSONObject;
 
 public class WeiboOAuthProvider
   implements OAuthProvider
 {
   public static final String COLL_WBUERS = "wbusrs";
   Logger logger = Logger.getLogger(WeiboOAuthProvider.class.getName());
   private MongoDataSource dataSource;
   public static final String LAINA = "3244273155";
   private Map<String, Map> infoCache = new HashMap();
 
   public void setDataSource(MongoDataSource dataSource)
   {
     this.dataSource = dataSource;
   }
 
   public String getCode()
   {
     return "code";
   }
 
   public Map<String, Object> authorize(String code)
   {
     String url = "https://api.weibo.com/oauth2/access_token";
     String cliend_id = "386267454";
     String client_secret = "6ad8466f1fe349cd641c9637ba0db378";
 
     String at = null;
     String uid = null;
     try {
       Map params = new HashMap();
       params.put("client_id", cliend_id);
       params.put("client_secret", client_secret);
       params.put("grant_type", "authorization_code");
       params.put("code", code);
       params.put("redirect_uri", "http://www.luckyna.com/oauth/weibo");
       JSONObject json = WebUtils.doPost(url, params);
 
       if ((json != null) && (json.has("access_token")) && (json.getString("access_token") != null)) {
         at = json.getString("access_token");
         uid = json.getString("uid");
         this.logger.info("uid=" + uid + " at:" + at);
         Map wbinfo = refreshWeiboInfo(uid, at);
         if (wbinfo == null) return null;
         asFans(uid, "3244273155", at);
         wbinfo.put("uid", uid);
         return wbinfo;
       }
     } catch (Exception e) {
       e.printStackTrace();
     }
     return null;
   }
 
   public boolean asFans(String wo, String uid, String at) {
     if (wo.equals(uid)) return true;
 
     String url = "https://api.weibo.com/2/friendships/create.json";
     Map params = new HashMap();
     params.put("access_token", at);
     params.put("uid", uid);
     JSONObject result = WebUtils.doPost(url, params);
 
     if (result.has("error")) {
       try {
         this.logger.info(wo + " fans->" + uid + "   error: " + result.getString("error"));
       } catch (JSONException localJSONException) {
       }
       return false;
     }
     return true;
   }
 
   public void init()
   {
   }
 
   public static void main(String[] args)
   {
     WeiboOAuthProvider wop = new WeiboOAuthProvider();
     System.out.println(System.currentTimeMillis() - 518400000L);
     System.out.println(new Date(1388246400000L));
   }
 
   @RestService(uri="/dfans/list", authenticated=false)
   public Map<String, Map<String, Object>> getAlldfans()
   {
     Map result = new HashMap();
     DBCursor c1 = this.dataSource.getCollection("wbusrs").find(new BasicDBObject("e", MapUtils.newMap("$ne", null)));
     while (c1.hasNext()) {
       DBObject dbo = c1.next();
       result.put(dbo.get("uid").toString(), dbo.toMap());
     }
     return result;
   }
 
   @RestService(uri="/dfans/clear", authenticated=false)
   public void clearAlldfans() {
     DBCursor c1 = this.dataSource.getCollection("wbusrs").find(new BasicDBObject("e", MapUtils.newMap("$ne", null)));
     while (c1.hasNext()) {
       DBObject dbo = c1.next();
       dbo.removeField("e");
       dbo.removeField("p");
       this.dataSource.getCollection("wbusrs").update(new BasicDBObject("uid", dbo.get("uid")), dbo, true, false);
     }
   }
 
   @RestService(uri="/user/email", authenticated=false)
   public void setUserLoginAccount(@RestParam("u") String user, @RestParam("email") String email, @RestParam("p") String p) {
     DBObject one = this.dataSource.getCollection("wbusrs").findOne(new BasicDBObject("uid", user));
     if (one != null) {
       one.put("e", email);
       one.put("p", p);
       this.dataSource.getCollection("wbusrs").update(new BasicDBObject("uid", user), one, true, false);
     } else {
       this.dataSource.getCollection("wbusrs").insert(new DBObject[] { BasicDBObjectBuilder.start()
         .add("uid", user).add("e", email).add("p", p).get() });
     }
   }
 
   @RestService(uri="/dfans/token/expired", authenticated=false)
   public List<Map> getExpiredToken() {
     List result = new ArrayList();
 
     DBCursor c1 = this.dataSource.getCollection("wbusrs").find(new BasicDBObject("e", MapUtils.newMap("$ne", null)));
     while (c1.hasNext()) {
       DBObject dbo = c1.next();
       Map m = dbo.toMap();
 
       if (m.get("rf") == null) {
         result.add(m);
       }
       else if (((Long)m.get("rf")).longValue() + 518400000L < System.currentTimeMillis()) {
         result.add(m);
       }
     }
     return result;
   }
 
   @RestService(uri="/dfans/expired", authenticated=false)
   public void setAsExpired(@RestParam("u") String user) {
     DBObject one = this.dataSource.getCollection("wbusrs").findOne(new BasicDBObject("uid", user));
     DBObject dbo = new BasicDBObject("_id", Long.valueOf(Long.parseLong(user)));
     if (one != null) {
       this.logger.info("expired ");
       dbo.put("email", one.get("e"));
     }
 
     DBObject existed = this.dataSource.getCollection("expired").findOne(dbo);
     if (existed == null) {
       existed = dbo;
       existed.put("inc", Integer.valueOf(1));
     } else {
       existed.put("inc", Integer.valueOf(((Integer)existed.get("inc")).intValue() + 1));
     }
     this.dataSource.getCollection("expired").update(dbo, existed, true, false);
   }
   @RestService(uri="/dfans/add", authenticated=true)
   public void addbillfans(String wo, String ta) {
     DBObject wodbo = this.dataSource.getCollection("wbusrs").findOne(new BasicDBObject("uid", wo));
     if (wodbo == null) {
       return;
     }
     if (wodbo.get("at") == null) {
       this.logger.info(wodbo.get("rn") + " 的 at 不存在");
     }
 
     DBObject tadbo = this.dataSource.getCollection("wbusrs").findOne(new BasicDBObject("uid", ta));
     if (tadbo == null) {
       return;
     }
     if (tadbo.get("at") == null) {
       this.logger.info(tadbo.get("rn") + " 的 at 不存在");
     }
     asFans(wo, ta, wodbo.get("at").toString());
     asFans(ta, wo, tadbo.get("at").toString());
   }
 
   public void cancelValidate() {
     DBObject one = this.dataSource.getCollection("wbusrs").findOne(new BasicDBObject("uid", AuthenticationUtil.getCurrentUser()));
     if (one != null) {
       String at = one.get("at").toString();
       WebUtils.doGet("https://api.weibo.com/oauth2/revokeoauth2?access_token=" + at);
     }
   }
 
   public String getName()
   {
     return "weibo";
   }
 
   public Map<String, Object> getCurWbInfo() {
     if (AuthenticationUtil.getCurrentUser() == null) return null;
 
     return getFullWeiboInfo(AuthenticationUtil.getCurrentUser());
   }
   @RestService(method="GET", uri="/weibo/info", authenticated=false)
   public Map<String, Object> getWeiboInfo(@RestParam("u") String user) {
     Map m = getFullWeiboInfo(user);
     if (m == null) return null;
     Map n = new HashMap(m);
 
     if ((user != null) && (!user.equals(AuthenticationUtil.getCurrentUser()))) {
       n.remove("at");
     }
     return n;
   }
 
   public Map<String, Object> getFullWeiboInfo(String user)
   {
     if (user == null) {
       user = AuthenticationUtil.getCurrentUser();
     }
     if (this.infoCache.get(user) == null) {
       DBObject one = this.dataSource.getCollection("wbusrs").findOne(new BasicDBObject("_id", Long.valueOf(Long.parseLong(user))));
       if (one != null) {
         this.infoCache.put(user, one.toMap());
       }
     }
     return (Map)this.infoCache.get(user);
   }
 
   public Map<String, Object> getWeiboInfoByRealName(String rn) {
     DBObject one = this.dataSource.getCollection("wbusrs").findOne(new BasicDBObject("rn", rn));
     return one.toMap();
   }
   @RestService(method="GET", uri="/weibo/refresh")
   public Map<String, Object> refreshWeiboInfo() {
     DBObject one = this.dataSource.getCollection("wbusrs").findOne(new BasicDBObject("uid", AuthenticationUtil.getCurrentUser()));
     if (one != null) {
       String at = one.get("at").toString();
       String uid = one.get("uid").toString();
       try
       {
         return refreshWeiboInfo(uid, at);
       } catch (JSONException e) {
         return null;
       }
     }
 
     return null;
   }
 
   public Map<String, Object> refreshWeiboInfo(String uid, String at)
     throws JSONException
   {
     JSONObject showJson = WebUtils.doGet("https://api.weibo.com/2/users/show.json?uid=" + uid + 
       "&access_token=" + at);
 
     if (showJson != null) {
       Map userMap = WebUtils.jsonObjectToMap(showJson);
 
       if (userMap.get("error") != null) {
         this.logger.info("error refresh: " + showJson.toString());
         return null;
       }
 
       if (userMap.get("screen_name") == null) {
         this.logger.info("user has no screen name: " + showJson.toString());
         return null;
       }
       userMap.put("_id", Long.valueOf(Long.parseLong(uid)));
       userMap.put("at", at);
       this.dataSource.getCollection("wbusrs").update(new BasicDBObject("_id", Long.valueOf(Long.parseLong(uid))), new BasicDBObject(userMap), true, false);
       return userMap;
     }
     return null;
   }
 }

