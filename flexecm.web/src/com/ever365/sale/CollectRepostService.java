/*     */ package com.ever365.sale;
/*     */ 
/*     */ import com.ever365.auth.WeiboOAuthProvider;
/*     */ import com.ever365.mongo.MongoDataSource;
/*     */ import com.ever365.rest.AuthenticationUtil;
/*     */ import com.ever365.rest.HttpStatus;
/*     */ import com.ever365.rest.HttpStatusException;
/*     */ import com.ever365.rest.RestParam;
/*     */ import com.ever365.rest.RestService;
/*     */ import com.ever365.utils.MapUtils;
/*     */ import com.ever365.utils.StringUtils;
/*     */ import com.ever365.utils.WebUtils;
/*     */ import com.mongodb.BasicDBObject;
/*     */ import com.mongodb.BasicDBObjectBuilder;
/*     */ import com.mongodb.DBCollection;
/*     */ import com.mongodb.DBCursor;
/*     */ import com.mongodb.DBObject;
/*     */ import java.text.SimpleDateFormat;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.Date;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.logging.Logger;
/*     */ import org.json.JSONObject;
/*     */ 
/*     */ public class CollectRepostService
/*     */ {
/*     */   private WeiboOAuthProvider weiboOAuthProvider;
/*     */   private MongoDataSource dataSource;
/*  39 */   Logger logger = Logger.getLogger(CollectRepostService.class.getName());
/*     */ 
/*  49 */   private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
/*     */ 
/* 110 */   private static Map<String, Integer> GIFT_INC_MAP = MapUtils.newMap("repost", Integer.valueOf(1));
/*     */ 
/* 163 */   private Map<String, Integer> pushMap = MapUtils.newMap("push", Integer.valueOf(1));
/*     */ 
/*     */   public void setDataSource(MongoDataSource dataSource)
/*     */   {
/*  42 */     this.dataSource = dataSource;
/*     */   }
/*     */ 
/*     */   public void setWeiboOAuthProvider(WeiboOAuthProvider weiboOAuthProvider) {
/*  46 */     this.weiboOAuthProvider = weiboOAuthProvider;
/*     */   }
/*     */ 
/*     */   @RestService(uri="/collect/import")
/*     */   public String importGift()
/*     */   {
/*  54 */     if (!"3244273155".equals(AuthenticationUtil.getCurrentUser())) {
/*  55 */       return "-1";
/*     */     }
/*     */ 
/*  58 */     Map uInfo = this.weiboOAuthProvider.getCurWbInfo();
/*     */ 
/*  60 */     String timelineUrl = "https://api.weibo.com/2/statuses/user_timeline.json?access_token=" + uInfo.get("at");
/*     */ 
/*  62 */     Map mytimelines = WebUtils.jsonObjectToMap(WebUtils.doGet(timelineUrl));
/*     */ 
/*  64 */     Collection status = (Collection)mytimelines.get("statuses");
/*  65 */     Iterator ri = status.iterator();
/*     */ 
/*  67 */     while (ri.hasNext()) {
/*  68 */       Map repostedWeibo = (Map)ri.next();
/*     */ 
/*  70 */       if ((repostedWeibo.get("retweeted_status") == null) || (!repostedWeibo.get("text").toString().contains("#截止#"))) continue;
/*     */       try {
/*  72 */         Map originalWeibo = (Map)repostedWeibo.get("retweeted_status");
/*     */ 
/*  74 */         if (this.dataSource.getCollection("collects").findOne(
/*  75 */           new BasicDBObject("wbid", Long.valueOf(Long.parseLong(originalWeibo.get("id").toString())))) != null)
/*     */         {
/*     */           continue;
/*     */         }
/*  79 */         Map collect = new HashMap();
/*  80 */         collect.put("_id", repostedWeibo.get("id"));
/*  81 */         collect.put("wbid", originalWeibo.get("id"));
/*  82 */         collect.put("mpic", originalWeibo.get("bmiddle_pic"));
/*  83 */         collect.put("lpic", originalWeibo.get("original_pic"));
/*  84 */         collect.put("pic", originalWeibo.get("thumbnail_pic"));
/*  85 */         collect.put("text", originalWeibo.get("text"));
/*     */ 
/*  87 */         String text = repostedWeibo.get("text").toString();
/*  88 */         String until = StringUtils.middle(text, "#截止#", ",");
/*  89 */         Date date = this.sdf.parse(until);
/*  90 */         collect.put("until", Long.valueOf(date.getTime()));
/*  91 */         collect.put("prize", StringUtils.middle(text, "#奖品#", ","));
/*  92 */         collect.put("full", new Integer(StringUtils.middle(text, "#最小转发#", ",")));
/*  93 */         collect.put("push", Integer.valueOf(0));
/*  94 */         Map user = (Map)originalWeibo.get("user");
/*  95 */         collect.put("sn", user.get("screen_name"));
/*  96 */         collect.put("head", user.get("avatar_large"));
/*  97 */         collect.put("desc", user.get("description"));
/*  98 */         collect.put("gender", user.get("gender"));
/*  99 */         collect.put("from", user.get("id").toString());
/* 100 */         collect.put("repost", Integer.valueOf(0));
/* 101 */         this.dataSource.getCollection("collects").insert(new DBObject[] { new BasicDBObject(collect) });
/*     */       } catch (Exception e) {
/* 103 */         e.printStackTrace();
/*     */       }
/*     */     }
/*     */ 
/* 107 */     return "OK";
/*     */   }
/*     */ 
/*     */   @RestService(uri="/collect/add")
/*     */   public synchronized String addCollect(@RestParam("id") String id)
/*     */   {
/* 114 */     if (id == null) throw new HttpStatusException(HttpStatus.BAD_REQUEST);
/*     */ 
/* 117 */     DBObject collect = getCollect(id);
/*     */ 
/* 119 */     Long wbid = (Long)collect.get("wbid");
/* 120 */     Map curWbInfo = this.weiboOAuthProvider.getCurWbInfo();
/*     */ 
/* 123 */     DBObject exsit = this.dataSource.getCollection("collect.reposts").findOne(BasicDBObjectBuilder.start().add("wbid", wbid).add("u", curWbInfo.get("_id")).get());
/* 124 */     if (exsit != null)
/*     */     {
/* 126 */       return (String)exsit.get("code");
/*     */     }
/*     */ 
/* 129 */     checkUserLevel(curWbInfo);
/*     */ 
/* 132 */     DBCursor latest = this.dataSource.getCollection("collect.reposts").find(new BasicDBObject("u", curWbInfo.get("_id"))).sort(new BasicDBObject("t", Integer.valueOf(-1))).limit(1);
/* 133 */     if (latest.hasNext()) {
/* 134 */       Long time = (Long)latest.next().get("t");
/* 135 */       if (System.currentTimeMillis() - time.longValue() < 10800000L) {
/* 136 */         return "-1";
/*     */       }
/*     */     }
/*     */ 
/* 140 */     Integer current = (Integer)collect.get("repost");
/* 141 */     if (current.intValue() >= 1000) {
/* 142 */       return "1000";
/*     */     }
/*     */ 
/* 145 */     collect.put("repost", Integer.valueOf(current.intValue() + 1));
/*     */ 
/* 147 */     String codeDsp = String.valueOf(1000 + current.intValue() + 1).substring(1);
/* 148 */     String content = "我的转发码是" + codeDsp;
/* 149 */     doRepost(wbid, content, (String)curWbInfo.get("at"));
/* 150 */     this.dataSource.getCollection("collect.reposts").insert(new DBObject[] { BasicDBObjectBuilder.start().add("wbid", wbid)
/* 151 */       .add("code", codeDsp).add("u", curWbInfo.get("_id")).add("t", Long.valueOf(System.currentTimeMillis())).get() });
/* 152 */     this.dataSource.getCollection("collects").update(new BasicDBObject("_id", Long.valueOf(Long.parseLong(id))), collect);
/* 153 */     return codeDsp;
/*     */   }
/*     */ 
/*     */   public DBObject getCollect(String id) {
/* 157 */     DBObject collect = this.dataSource.getCollection("collects").findOne(new BasicDBObject("_id", Long.valueOf(Long.parseLong(id))));
/*     */ 
/* 159 */     if (collect == null) throw new HttpStatusException(HttpStatus.NOT_FOUND);
/* 160 */     return collect;
/*     */   }
/*     */ 
/*     */   @RestService(uri="/collect/push", authenticated=true)
/*     */   public void push(@RestParam("id") String id) {
/* 166 */     if (id == null) throw new HttpStatusException(HttpStatus.BAD_REQUEST);
/*     */ 
/* 168 */     DBObject collect = getCollect(id);
/* 169 */     Long wbid = (Long)collect.get("wbid");
/* 170 */     Map curWbInfo = this.weiboOAuthProvider.getCurWbInfo();
/* 171 */     doRepost(wbid, "转发微博。", (String)curWbInfo.get("at"));
/* 172 */     DBObject update = new BasicDBObject();
/* 173 */     update.put("$inc", this.pushMap);
/* 174 */     this.dataSource.getCollection("collects").findAndModify(new BasicDBObject("_id", Long.valueOf(Long.parseLong(id))), update);
/*     */   }
/*     */   @RestService(uri="/collect/index", authenticated=false)
/*     */   public Map<String, Object> getCollectIndex() {
/* 179 */     Map result = new HashMap();
/* 180 */     result.put("list", getUnfinishedCollect());
/* 181 */     result.put("cu", this.weiboOAuthProvider.getCurWbInfo());
/* 182 */     return result;
/*     */   }
/*     */ 
/*     */   public List<Map<String, Object>> getUnfinishedCollect() {
/* 186 */     List result = new ArrayList();
/* 187 */     DBCursor cursor = this.dataSource.getCollection("collects").find(new BasicDBObject("repost", MapUtils.newMap("$lt", Integer.valueOf(1000)))).sort(new BasicDBObject("until", Integer.valueOf(1)));
/* 188 */     while (cursor.hasNext()) {
/* 189 */       result.add(cursor.next().toMap());
/*     */     }
/* 191 */     return result;
/*     */   }
/*     */ 
/*     */   private void checkUserLevel(Map<String, Object> curWbInfo)
/*     */   {
/*     */   }
/*     */ 
/*     */   public boolean asFans(String wo, String uid, String at) {
/* 199 */     String url = "https://api.weibo.com/2/friendships/create.json";
/* 200 */     Map params = new HashMap();
/* 201 */     params.put("access_token", at);
/* 202 */     params.put("uid", uid);
/* 203 */     JSONObject result = WebUtils.doPost(url, params);
/* 204 */     if (result.has("error")) {
/* 205 */       return false;
/*     */     }
/* 207 */     this.logger.info(wo + " fans->" + uid + " sucesss!");
/* 208 */     return true;
/*     */   }
/*     */ 
/*     */   public JSONObject doRepost(Long id, String content, String token)
/*     */   {
/* 213 */     Map params = new HashMap();
/*     */ 
/* 215 */     if ((token == null) || (id == null)) return null;
/*     */ 
/* 217 */     params.put("access_token", token);
/* 218 */     params.put("id", id);
/* 219 */     params.put("is_comment", Integer.valueOf(3));
/* 220 */     params.put("status", content);
/*     */ 
/* 222 */     JSONObject result = WebUtils.doPost("https://api.weibo.com/2/statuses/repost.json", params);
/*     */ 
/* 224 */     return result;
/*     */   }
/*     */ }

/* Location:           D:\360云盘\Desktops\T410\a.jar
 * Qualified Name:     com.ever365.sale.CollectRepostService
 * JD-Core Version:    0.6.0
 */