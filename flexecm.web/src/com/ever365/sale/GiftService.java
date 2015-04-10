/*     */ package com.ever365.sale;
/*     */ 
/*     */ import com.ever365.auth.WeiboOAuthProvider;
/*     */ import com.ever365.mongo.MongoDataSource;
/*     */ import com.ever365.rest.AuthenticationUtil;
/*     */ import com.ever365.rest.HttpStatus;
/*     */ import com.ever365.rest.HttpStatusException;
/*     */ import com.ever365.rest.RestParam;
/*     */ import com.ever365.rest.RestResult;
/*     */ import com.ever365.rest.RestService;
/*     */ import com.ever365.utils.MapUtils;
/*     */ import com.ever365.utils.StringUtils;
/*     */ import com.ever365.utils.WebUtils;
/*     */ import com.mongodb.BasicDBObject;
/*     */ import com.mongodb.BasicDBObjectBuilder;
/*     */ import com.mongodb.DBCollection;
/*     */ import com.mongodb.DBCursor;
/*     */ import com.mongodb.DBObject;
/*     */ import java.io.PrintStream;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.Date;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Timer;
/*     */ import java.util.TimerTask;
/*     */ import java.util.logging.Logger;
/*     */ import java.util.regex.Pattern;
/*     */ import org.json.JSONObject;
/*     */ 
/*     */ public class GiftService
/*     */ {
/*  38 */   private static Map<String, Integer> GIFT_INC_MAP = MapUtils.newMap("g", Integer.valueOf(1));
/*  39 */   private static Map<String, Integer> LUCK_INC_MAP = MapUtils.newMap("l", Integer.valueOf(1));
/*  40 */   private static Map<String, Integer> REPOST_INC_MAP = MapUtils.newMap("r", Integer.valueOf(1));
/*     */   public static final String COLL_PRESENTS = "gifts";
/*     */   public static final String COLL_STATUS = "status";
/*     */   private static final String COLL_REPOSTS = "reposts";
/*  46 */   private String seed = "212000";
/*     */   private MongoDataSource dataSource;
/*     */   private WeiboOAuthProvider weiboOAuthProvider;
/*  52 */   Logger logger = Logger.getLogger(GiftService.class.getName());
/*     */ 
/* 388 */   private List<Map> timelines = null;
/*     */ 
/* 434 */   List<Map<String, Object>> daren = null;
/*     */ 
/* 483 */   List<Map<String, Object>> finished = null;
/*     */ 
/* 606 */   private static Long INTEVAL_VERY_SHORT = Long.valueOf(60000L);
/* 607 */   private static Long INTEVAL_SHORT = Long.valueOf(300000L);
/* 608 */   private static Long INTEVAL_LONG = Long.valueOf(600000L);
/* 609 */   private static Long INTEVAL_SUPER_LONG = Long.valueOf(1800000L);
/*     */ 
/* 750 */   private static String[] ML = "Jan_Feb_Mar_Apr_May_Jun_Jul_Aug_Sep_Oct_Nov_Dec".split("_");
/*     */ 
/*     */   public void setWeiboOAuthProvider(WeiboOAuthProvider weiboOAuthProvider)
/*     */   {
/*  55 */     this.weiboOAuthProvider = weiboOAuthProvider;
/*     */   }
/*     */ 
/*     */   public void setDataSource(MongoDataSource dataSource) {
/*  59 */     this.dataSource = dataSource;
/*     */   }
/*     */ 
/*     */   public void setSeed(String seed) {
/*  63 */     this.seed = seed;
/*     */   }
/*     */ 
/*     */   public static void main(String[] args) {
/*  67 */     Pattern AT_PATTERN = Pattern.compile("@+:");
/*  68 */     String ss = "回复@SoFlower手工坊:好啊！！@SoFlower手工坊 [鼓掌]//@SoFlower手工坊:节后第一天上班，同志们打了鸡血了木有~感觉月底抽奖太漫长了，等月中新品一出来，我们马上抽奖好么？！";
/*  69 */     String vv = ss.replaceAll("@([^@ ]*)[,:!\\s]", "");
/*  70 */     System.out.println(vv);
/*     */   }
/*     */   @RestService(uri="/gift/add", method="POST")
/*     */   public String addGift(Map map) {
/*  75 */     DBCollection coll = this.dataSource.getCollection("gifts");
/*     */ 
/*  77 */     if (map.get("wbid") == null) {
/*  78 */       throw new HttpStatusException(HttpStatus.BAD_REQUEST);
/*     */     }
/*     */ 
/*  81 */     DBObject query = new BasicDBObject("_id", Long.valueOf(Long.parseLong(map.get("wbid").toString())));
/*     */ 
/*  83 */     if (coll.findOne(query) == null)
/*     */     {
/*  85 */       String uid = (String)map.get("from");
/*  86 */       this.weiboOAuthProvider.getFullWeiboInfo(uid);
/*     */ 
/*  90 */       DBObject dbo = new BasicDBObject("_id", Long.valueOf(Long.parseLong(map.get("wbid").toString())));
/*  91 */       map.put("active", Boolean.valueOf(true));
/*     */ 
/*  93 */       dbo.putAll(map);
/*     */ 
/*  95 */       dbo.put("created", Long.valueOf(Long.parseLong(map.get("created").toString())));
/*  96 */       dbo.put("importor", AuthenticationUtil.getCurrentUser());
/*  97 */       dbo.removeField("wbid");
/*     */ 
/*  99 */       this.timelines = null;
/* 100 */       this.daren = null;
/* 101 */       coll.insert(new DBObject[] { dbo });
/* 102 */       increase("status", (String)map.get("from"), GIFT_INC_MAP);
/* 103 */       return "0";
/*     */     }
/* 105 */     return "1";
/*     */   }
/*     */ 
/*     */   @RestService(uri="/gift/import")
/*     */   public Integer importGift()
/*     */   {
/* 112 */     Map uInfo = this.weiboOAuthProvider.getFullWeiboInfo(null);
/*     */ 
/* 114 */     String timelineUrl = "https://api.weibo.com/2/statuses/user_timeline.json?access_token=" + uInfo.get("at");
/*     */ 
/* 116 */     Map mytimelines = WebUtils.jsonObjectToMap(WebUtils.doGet(timelineUrl));
/*     */ 
/* 118 */     int added = 0;
/* 119 */     Collection status = (Collection)mytimelines.get("statuses");
/* 120 */     Iterator ri = status.iterator();
/*     */ 
/* 122 */     while (ri.hasNext()) {
/* 123 */       Map repostedWeibo = (Map)ri.next();
/*     */ 
/* 125 */       if ((repostedWeibo.get("retweeted_status") == null) || (!repostedWeibo.get("text").toString().contains("#抽奖时间#"))) continue;
/*     */       try {
/* 127 */         Map originalWeibo = (Map)repostedWeibo.get("retweeted_status");
/* 128 */         Map gift = new HashMap();
/*     */ 
/* 130 */         gift.put("wbid", originalWeibo.get("id").toString());
/* 131 */         gift.put("mpic", originalWeibo.get("bmiddle_pic"));
/* 132 */         gift.put("lpic", originalWeibo.get("original_pic"));
/* 133 */         gift.put("pic", originalWeibo.get("thumbnail_pic"));
/* 134 */         gift.put("text", originalWeibo.get("text"));
/*     */ 
/* 136 */         String text = repostedWeibo.get("text").toString();
/*     */ 
/* 138 */         gift.put("until", StringUtils.middle(text, "#抽奖时间#", ","));
/* 139 */         gift.put("prize", StringUtils.middle(text, "#奖品#", ","));
/*     */ 
/* 141 */         Map user = (Map)originalWeibo.get("user");
/* 142 */         gift.put("sn", user.get("screen_name"));
/* 143 */         gift.put("head", user.get("avatar_large"));
/* 144 */         gift.put("desc", user.get("description"));
/* 145 */         gift.put("gender", user.get("gender"));
/* 146 */         gift.put("from", user.get("id").toString());
/* 147 */         gift.put("created", parseTime(originalWeibo.get("created_at").toString()));
/* 148 */         String i = addGift(gift);
/* 149 */         if (!i.equals("0")) continue; added++;
/*     */       }
/*     */       catch (Exception localException)
/*     */       {
/*     */       }
/*     */     }
/* 155 */     return Integer.valueOf(added);
/*     */   }
/*     */   @RestService(uri="/repost/add", method="POST")
/*     */   public void addRepost(Map map) {
/* 160 */     String uid = map.get("uid").toString();
/* 161 */     this.weiboOAuthProvider.getFullWeiboInfo(uid);
/*     */ 
/* 167 */     map.remove("sn");
/* 168 */     map.remove("head");
/*     */ 
/* 170 */     DBCollection coll = this.dataSource.getCollection("reposts");
/*     */ 
/* 172 */     BasicDBObject bdo = new BasicDBObject("_id", Long.valueOf(Long.parseLong(map.get("rid").toString())));
/* 173 */     bdo.putAll(map);
/* 174 */     bdo.put("uid", Long.valueOf(Long.parseLong(uid)));
/* 175 */     bdo.put("wbid", Long.valueOf(Long.parseLong(map.get("wbid").toString())));
/* 176 */     bdo.put("created", Long.valueOf(Long.parseLong(map.get("created").toString())));
/* 177 */     bdo.remove("rid");
/*     */ 
/* 179 */     increase("status", uid, REPOST_INC_MAP);
/* 180 */     coll.update(new BasicDBObject("_id", Long.valueOf(Long.parseLong(map.get("rid").toString()))), 
/* 181 */       bdo, true, false);
/*     */   }
/*     */   @RestService(uri="/repost/local/add", method="POST")
/*     */   public void addLocalRepost(Map map) {
/* 186 */     addRepost(map);
/* 187 */     this.timelines = null;
/*     */     try {
/* 189 */       Long wbid = Long.valueOf(Long.parseLong(map.get("wbid").toString()));
/*     */ 
/* 191 */       Map setMap = new HashMap();
/*     */ 
/* 193 */       DBObject updateWeibo = new BasicDBObject();
/*     */ 
/* 195 */       long count = this.dataSource.getCollection("reposts").count(new BasicDBObject("wbid", wbid));
/* 196 */       setMap.put("reposts", Long.valueOf(count));
/* 197 */       updateWeibo.put("$set", setMap);
/*     */ 
/* 199 */       DBCollection coll = this.dataSource.getCollection("gifts");
/* 200 */       coll.update(new BasicDBObject("_id", wbid), updateWeibo);
/*     */     } catch (Exception e) {
/* 202 */       e.printStackTrace();
/* 203 */       this.logger.info("Error  add Local Repost");
/*     */     }
/*     */   }
/*     */ 
/*     */   @RestService(uri="/repost/mine", authenticated=true)
/*     */   public List<Map<String, Object>> getMyRepostsDetail()
/*     */   {
/* 214 */     if (AuthenticationUtil.getCurrentUser() == null) {
/* 215 */       return null;
/*     */     }
/* 217 */     DBCollection coll = this.dataSource.getCollection("reposts");
/* 218 */     DBCursor cursor = coll.find(new BasicDBObject("uid", Long.valueOf(Long.parseLong(AuthenticationUtil.getCurrentUser())))).limit(100);
/* 219 */     List result = new ArrayList();
/* 220 */     while (cursor.hasNext()) {
/* 221 */       DBObject dbo = cursor.next();
/* 222 */       Map m = dbo.toMap();
/* 223 */       Object wo = m.get("wbid");
/* 224 */       if (wo != null) {
/* 225 */         if ((wo instanceof Long)) {
/* 226 */           DBCollection giftcoll = this.dataSource.getCollection("gifts");
/* 227 */           DBObject weibo = giftcoll.findOne(new BasicDBObject("_id", (Long)wo));
/* 228 */           m.put("weibo", weibo.toMap());
/*     */         }
/* 230 */         result.add(m);
/*     */       }
/*     */     }
/* 232 */     return result;
/*     */   }
/*     */ 
/*     */   public List<Map<String, Object>> getMyReposts()
/*     */   {
/* 240 */     if (AuthenticationUtil.getCurrentUser() == null) {
/* 241 */       return null;
/*     */     }
/* 243 */     DBCollection coll = this.dataSource.getCollection("reposts");
/* 244 */     DBCursor cursor = coll.find(new BasicDBObject("uid", Long.valueOf(Long.parseLong(AuthenticationUtil.getCurrentUser())))).limit(100);
/* 245 */     List result = new ArrayList();
/* 246 */     while (cursor.hasNext()) {
/* 247 */       DBObject dbo = cursor.next();
/* 248 */       Map m = dbo.toMap();
/* 249 */       result.add(m);
/*     */     }
/* 251 */     return result;
/*     */   }
/*     */ 
/*     */   @RestService(uri="/repost", method="GET", authenticated=false)
/*     */   public Map<String, Object> getReposts(@RestParam("wbid") String wbid, @RestParam("skip") Integer skip, @RestParam("limit") Integer limit, @RestParam("force") Integer force) {
/* 257 */     DBObject query = new BasicDBObject("_id", Long.valueOf(Long.parseLong(wbid)));
/* 258 */     DBObject weiboDbo = this.dataSource.getCollection("gifts").findOne(query);
/* 259 */     if (weiboDbo == null) {
/* 260 */       return null;
/*     */     }
/*     */ 
/* 263 */     if (force.intValue() == 1) {
/* 264 */       doFetchRepost(weiboDbo, 1);
/* 265 */       weiboDbo = this.dataSource.getCollection("gifts").findOne(query);
/*     */     }
/* 267 */     if (force.intValue() == 2) {
/* 268 */       weiboDbo.put("since_id", null);
/* 269 */       this.dataSource.getCollection("gifts").update(query, weiboDbo);
/* 270 */       doFetchRepost(weiboDbo, 1);
/* 271 */       weiboDbo = this.dataSource.getCollection("gifts").findOne(query);
/*     */     }
/*     */ 
/* 274 */     Map result = new HashMap();
/* 275 */     result.putAll(weiboDbo.toMap());
/* 276 */     DBCursor cursor = this.dataSource.getCollection("reposts").find(new BasicDBObject("wbid", Long.valueOf(Long.parseLong(wbid))));
/* 277 */     cursor.sort(new BasicDBObject("_id", Integer.valueOf(-1)));
/* 278 */     cursor.skip(skip.intValue()).limit(limit.intValue());
/* 279 */     List list = new ArrayList();
/* 280 */     while (cursor.hasNext()) {
/* 281 */       Map repost = cursor.next().toMap();
/*     */ 
/* 284 */       list.add(repost);
/*     */     }
/* 286 */     result.put("cu", AuthenticationUtil.getCurrentUser());
/* 287 */     result.put("list", list);
/* 288 */     result.put("seed", getSeeds());
/* 289 */     result.put("from", this.weiboOAuthProvider.getWeiboInfo((String)result.get("from")));
/* 290 */     return result;
/*     */   }
/*     */   @RestService(uri="/repost/some", method="GET", authenticated=false)
/*     */   public List<Map<String, Object>> getReposts(@RestParam("wbid") String wbid, @RestParam("list") String list) {
/* 295 */     DBObject query = new BasicDBObject("_id", Long.valueOf(Long.parseLong(wbid)));
/* 296 */     DBObject weiboDbo = this.dataSource.getCollection("gifts").findOne(query);
/* 297 */     if (weiboDbo == null) {
/* 298 */       return null;
/*     */     }
/*     */ 
/* 301 */     String[] seqs = list.split(",");
/* 302 */     List result = new ArrayList();
/*     */ 
/* 304 */     for (int i = 0; i < seqs.length; i++) {
/* 305 */       DBCursor cursor = this.dataSource.getCollection("reposts").find(new BasicDBObject("wbid", Long.valueOf(Long.parseLong(wbid))));
/*     */ 
/* 307 */       cursor.skip(Integer.parseInt(seqs[i]) - 1);
/*     */ 
/* 309 */       if (cursor.hasNext()) {
/* 310 */         Map repost = cursor.next().toMap();
/* 311 */         Map userInfo = this.weiboOAuthProvider.getWeiboInfo(repost.get("uid").toString());
/* 312 */         repost.putAll(userInfo);
/* 313 */         result.add(repost);
/*     */       }
/* 315 */       cursor.close();
/*     */     }
/* 317 */     return result;
/*     */   }
/*     */   @RestService(uri="/repost/export", authenticated=true)
/*     */   public Collection<Map<String, Object>> exportReposts(@RestParam("wbid") String wbid) {
/* 322 */     DBCursor cursor = this.dataSource.getCollection("reposts").find(new BasicDBObject("wbid", Long.valueOf(Long.parseLong(wbid))));
/* 323 */     cursor.sort(new BasicDBObject("_id", Integer.valueOf(1)));
/*     */ 
/* 325 */     List result = new ArrayList();
/* 326 */     ArrayList keys = new ArrayList();
/*     */ 
/* 328 */     while (cursor.hasNext()) {
/* 329 */       DBObject dbo = cursor.next();
/* 330 */       Map map = dbo.toMap();
/*     */ 
/* 332 */       Long uid = (Long)dbo.get("uid");
/* 333 */       if (!keys.contains(uid)) {
/* 334 */         keys.add(uid);
/* 335 */         keys.add(uid);
/* 336 */         Map winfo = this.weiboOAuthProvider.getWeiboInfo(uid.toString());
/* 337 */         map.putAll(winfo);
/* 338 */         result.add(map);
/*     */       }
/*     */     }
/* 341 */     return result;
/*     */   }
/*     */ 
/*     */   @RestService(uri="/luckiers", method="POST")
/*     */   public void postLuckier(@RestParam("gift") String giftId, @RestParam("result") String result, @RestParam("luckier") String luckier)
/*     */   {
/* 349 */     DBObject query = new BasicDBObject("_id", Long.valueOf(Long.parseLong(giftId)));
/* 350 */     DBObject weiboDbo = this.dataSource.getCollection("gifts").findOne(query);
/* 351 */     if (weiboDbo == null) {
/* 352 */       return;
/*     */     }
/* 354 */     weiboDbo.put("active", Boolean.valueOf(false));
/* 355 */     weiboDbo.put("result", result);
/* 356 */     weiboDbo.put("luckier", luckier);
/*     */ 
/* 358 */     this.dataSource.getCollection("gifts").update(query, weiboDbo);
/* 359 */     this.finished = null;
/*     */   }
/*     */   @RestService(uri="/gift/of", authenticated=false)
/*     */   public Map<String, Object> getGiftList(@RestParam(value="from", required=false) String from) {
/* 364 */     String uid = from;
/* 365 */     if (uid == null) {
/* 366 */       uid = AuthenticationUtil.getCurrentUser();
/*     */     }
/* 368 */     Map winfo = new HashMap();
/* 369 */     winfo.putAll(this.weiboOAuthProvider.getFullWeiboInfo(uid));
/* 370 */     winfo.remove("at");
/* 371 */     winfo.put("seed", getSeeds());
/* 372 */     winfo.putAll(getLaiStatus(uid));
/*     */ 
/* 374 */     List list = new ArrayList();
/*     */ 
/* 376 */     DBCursor cursor = this.dataSource.getCollection("gifts").find(new BasicDBObject("from", uid).append("active", Boolean.valueOf(true))).sort(new BasicDBObject("_id", Integer.valueOf(-1)));
/*     */ 
/* 378 */     while (cursor.hasNext()) {
/* 379 */       list.add(cursor.next().toMap());
/*     */     }
/*     */ 
/* 382 */     winfo.put("cu", this.weiboOAuthProvider.getFullWeiboInfo(null));
/* 383 */     winfo.put("list", list);
/* 384 */     winfo.put("mine", getMyReposts());
/* 385 */     return winfo;
/*     */   }
/*     */ 
/*     */   @RestService(uri="/gift/index", authenticated=false)
/*     */   public Map<String, Object> getGiftIndex()
/*     */   {
/* 392 */     Map index = new HashMap();
/* 393 */     index.put("cu", AuthenticationUtil.getCurrentUser());
/* 394 */     if (this.timelines == null) {
/* 395 */       this.timelines = getTimeLines(null);
/*     */     }
/* 397 */     index.put("tl", this.timelines);
/* 398 */     index.put("t", Long.valueOf(System.currentTimeMillis()));
/* 399 */     index.put("seed", getSeeds());
/*     */ 
/* 401 */     if (AuthenticationUtil.getCurrentUser() != null)
/*     */       try {
/* 403 */         index.put("info", this.weiboOAuthProvider.getCurWbInfo());
/*     */       }
/*     */       catch (Exception localException)
/*     */       {
/*     */       }
/* 408 */     index.put("status", getStatus());
/*     */ 
/* 424 */     index.put("notice", getFinishedGifts());
/*     */ 
/* 427 */     return index;
/*     */   }
/*     */ 
/*     */   public List<Map> getTimelines() {
/* 431 */     return this.timelines;
/*     */   }
/*     */ 
/*     */   public List<Map<String, Object>> getGiftDaren()
/*     */   {
/* 436 */     DBCursor cur = this.dataSource.getCollection("status").find().sort(new BasicDBObject("g", Integer.valueOf(-1))).limit(3);
/* 437 */     List daren = new ArrayList();
/* 438 */     while (cur.hasNext()) {
/* 439 */       Map m = cur.next().toMap();
/* 440 */       m.putAll(this.weiboOAuthProvider.getWeiboInfo(m.get("_id").toString()));
/* 441 */       daren.add(m);
/*     */     }
/* 443 */     return daren;
/*     */   }
/*     */   @RestService(uri="/gift/index/recent", authenticated=false)
/*     */   public List<Map<String, Object>> getRecentRepost() {
/* 448 */     DBCursor cursor = this.dataSource.getCollection("reposts").find().sort(new BasicDBObject("$natural", Integer.valueOf(-1))).limit(4);
/* 449 */     List recents = new ArrayList();
/* 450 */     while (cursor.hasNext()) {
/* 451 */       Map m = cursor.next().toMap();
/* 452 */       m.putAll(this.weiboOAuthProvider.getWeiboInfo(m.get("uid").toString()));
/*     */ 
/* 454 */       recents.add(m);
/*     */     }
/* 456 */     return recents;
/*     */   }
/*     */   @RestService(uri="/gift/tl", authenticated=false)
/*     */   public List<Map> getTimeLines(@RestParam(value="skip", required=false) String skip) {
/* 461 */     List result = new ArrayList();
/*     */ 
/* 463 */     DBCursor cursor = this.dataSource.getCollection("gifts").find(new BasicDBObject("active", Boolean.valueOf(true))).sort(new BasicDBObject("_id", Integer.valueOf(-1)));
/*     */ 
/* 465 */     while (cursor.hasNext()) {
/* 466 */       result.add(cursor.next().toMap());
/*     */     }
/* 468 */     return result;
/*     */   }
/*     */   @RestService(uri="/gift/all", authenticated=false)
/*     */   public List<Map> getAllGift() {
/* 473 */     List result = new ArrayList();
/*     */ 
/* 475 */     DBCursor cursor = this.dataSource.getCollection("gifts").find().sort(new BasicDBObject("_id", Integer.valueOf(-1)));
/*     */ 
/* 477 */     while (cursor.hasNext()) {
/* 478 */       result.add(cursor.next().toMap());
/*     */     }
/* 480 */     return result;
/*     */   }
/*     */ 
/*     */   @RestService(uri="/gift/finished", authenticated=false)
/*     */   public List<Map<String, Object>> getFinishedGifts()
/*     */   {
/* 488 */     if (this.finished != null) return this.finished;
/*     */ 
/* 490 */     List result = new ArrayList();
/* 491 */     BasicDBObject query = new BasicDBObject("luckier", MapUtils.newMap("$ne", null));
/* 492 */     DBCursor cursor = this.dataSource.getCollection("gifts").find(query).sort(new BasicDBObject("_id", Integer.valueOf(-1))).limit(10);
/* 493 */     while (cursor.hasNext()) {
/* 494 */       DBObject n = cursor.next();
/*     */       try {
/* 496 */         String name = (String)n.get("luckier");
/*     */ 
/* 498 */         Map uinfo = this.weiboOAuthProvider.getWeiboInfoByRealName(name.substring(1));
/* 499 */         if (uinfo != null) {
/* 500 */           n.put("lkhead", uinfo.get("al"));
/*     */         }
/*     */       }
/*     */       catch (Exception localException)
/*     */       {
/*     */       }
/*     */ 
/* 507 */       result.add(n.toMap());
/*     */     }
/* 509 */     this.finished = result;
/* 510 */     return this.finished;
/*     */   }
/*     */   @RestService(uri="/gift/remove", method="GET", runAsAdmin=true)
/*     */   public void removeGift(@RestParam("id") String giftId) {
/* 515 */     DBObject gift = this.dataSource.getCollection("gifts").findOne(new BasicDBObject("_id", Long.valueOf(Long.parseLong(giftId))));
/*     */ 
/* 517 */     if (gift == null) return;
/*     */ 
/* 519 */     if ((gift.get("from").equals(AuthenticationUtil.getCurrentUser())) || ("3244273155".equals(gift.get("importor")))) {
/* 520 */       this.dataSource.getCollection("reposts").remove(new BasicDBObject("wbid", Long.valueOf(Long.parseLong(giftId))));
/* 521 */       this.dataSource.getCollection("gifts").remove(new BasicDBObject("_id", Long.valueOf(Long.parseLong(giftId))));
/* 522 */       this.timelines = null;
/*     */     }
/*     */   }
/*     */ 
/*     */   @RestService(uri="/gift/close", method="POST")
/*     */   public void closeGift(@RestParam("id") String giftId) {
/* 529 */     DBObject gift = this.dataSource.getCollection("gifts").findOne(new BasicDBObject("_id", Long.valueOf(Long.parseLong(giftId))));
/*     */ 
/* 531 */     if (gift == null) return;
/*     */ 
/* 533 */     if ((gift.get("from").equals(AuthenticationUtil.getCurrentUser())) || ("3244273155".equals(gift.get("importor")))) {
/* 534 */       this.dataSource.getCollection("gifts").update(new BasicDBObject("_id", Long.valueOf(Long.parseLong(giftId))), 
/* 535 */         BasicDBObjectBuilder.start("$set", MapUtils.newMap("active", Boolean.valueOf(false))).get());
/* 536 */       this.timelines = null;
/*     */     }
/*     */   }
/*     */ 
/*     */   @RestService(method="GET", uri="/logout", authenticated=false)
/*     */   public RestResult logout(@RestParam("re") String redirect) {
/* 543 */     RestResult rr = new RestResult();
/* 544 */     Map session = new HashMap();
/* 545 */     session.put(AuthenticationUtil.SESSION_CURRENT_USER, null);
/* 546 */     rr.setSession(session);
/* 547 */     if (redirect == null)
/* 548 */       rr.setRedirect("/");
/*     */     else {
/* 550 */       rr.setRedirect(redirect);
/*     */     }
/* 552 */     return rr;
/*     */   }
/*     */   @RestService(method="GET", uri="/redirect", authenticated=false)
/*     */   public RestResult redirectLogin(@RestParam("re") String redirect, @RestParam("display") String display) {
/* 557 */     RestResult rr = new RestResult();
/* 558 */     Map session = new HashMap();
/* 559 */     session.put("oauth_redirect", redirect);
/* 560 */     rr.setSession(session);
/* 561 */     String url = "https://api.weibo.com/oauth2/authorize?client_id=386267454&response_type=code&redirect_uri=http://www.luckyna.com/oauth/weibo";
/*     */ 
/* 563 */     if (display != null) {
/* 564 */       url = url + "&display=" + display;
/*     */     }
/* 566 */     rr.setRedirect(url);
/* 567 */     return rr;
/*     */   }
/*     */ 
/*     */   public Map<String, Object> getStatus()
/*     */   {
/* 572 */     if (AuthenticationUtil.getCurrentUser() == null) {
/* 573 */       return null;
/*     */     }
/* 575 */     BasicDBObject query = new BasicDBObject("_id", Long.valueOf(Long.parseLong(AuthenticationUtil.getCurrentUser())));
/* 576 */     DBObject dbo = this.dataSource.getCollection("status").findOne(query);
/*     */ 
/* 578 */     if (dbo != null) {
/* 579 */       return dbo.toMap();
/*     */     }
/* 581 */     return null;
/*     */   }
/*     */ 
/*     */   public void increase(String coll, String user, Map<String, Integer> m)
/*     */   {
/* 586 */     BasicDBObject query = new BasicDBObject("_id", Long.valueOf(Long.parseLong(user)));
/* 587 */     DBObject update = new BasicDBObject();
/* 588 */     update.put("$inc", m);
/* 589 */     DBObject dbo = this.dataSource.getCollection(coll).findAndModify(query, update);
/*     */   }
/*     */ 
/*     */   public Map<String, Object> getLaiStatus(String userId) {
/* 593 */     DBObject dbo = this.dataSource.getCollection("status").findOne(new BasicDBObject("usr", userId));
/* 594 */     if (dbo != null) {
/* 595 */       return dbo.toMap();
/*     */     }
/* 597 */     return Collections.EMPTY_MAP;
/*     */   }
/*     */ 
/*     */   public void init()
/*     */   {
/* 602 */     Timer timer = new Timer();
/* 603 */     timer.schedule(new FetchRepostTask(), 30000L, 120000L);
/*     */   }
/*     */ 
/*     */   private Integer doFetchRepost(DBObject sourceWeibo, int page)
/*     */   {
/* 655 */     long since_id = 0L;
/*     */ 
/* 657 */     if (sourceWeibo.get("since_id") != null) {
/* 658 */       since_id = ((Long)sourceWeibo.get("since_id")).longValue();
/*     */     }
/*     */ 
/* 661 */     String importer = (String)sourceWeibo.get("importor");
/*     */ 
/* 663 */     Map uInfo = this.weiboOAuthProvider.getFullWeiboInfo(importer);
/*     */ 
/* 665 */     if ((uInfo == null) || (uInfo.get("at") == null)) {
/* 666 */       this.logger.info("Unable to import, reason:  no importor " + importer + " for " + sourceWeibo.get("wbid"));
/* 667 */       return Integer.valueOf(0);
/*     */     }
/*     */ 
/* 670 */     String url = "https://api.weibo.com/2/statuses/repost_timeline.json?access_token=" + uInfo.get("at") + 
/* 671 */       "&id=" + sourceWeibo.get("_id") + 
/* 672 */       "&count=200" + 
/* 673 */       "&page=" + page + 
/* 674 */       "&since_id=" + since_id;
/* 675 */     JSONObject jso = WebUtils.doGet(url);
/*     */ 
/* 677 */     if (jso == null) {
/* 678 */       return Integer.valueOf(0);
/*     */     }
/*     */ 
/* 681 */     Map result = WebUtils.jsonObjectToMap(jso);
/*     */ 
/* 683 */     if (result.get("reposts") == null) {
/* 684 */       if (result.get("error") != null) {
/* 685 */         this.logger.info(">fetch error " + result.get("error"));
/*     */       }
/* 687 */       return Integer.valueOf(0);
/*     */     }
/*     */ 
/* 690 */     int fetched = 0;
/* 691 */     Collection reposts = (Collection)result.get("reposts");
/* 692 */     Iterator ri = reposts.iterator();
/*     */ 
/* 694 */     while (ri.hasNext()) {
/* 695 */       Map repostedWeibo = (Map)ri.next();
/* 696 */       Map userField = (Map)repostedWeibo.get("user");
/* 697 */       Map repost = new HashMap();
/*     */ 
/* 699 */       Long s = (Long)repostedWeibo.get("id");
/* 700 */       if (s.longValue() > since_id) {
/* 701 */         since_id = s.longValue();
/*     */       }
/*     */ 
/* 704 */       repost.put("rid", repostedWeibo.get("id"));
/* 705 */       repost.put("wbid", sourceWeibo.get("_id"));
/* 706 */       repost.put("text", repostedWeibo.get("text"));
/* 707 */       repost.put("created", parseTime(repostedWeibo.get("created_at").toString()));
/* 708 */       repost.put("uid", userField.get("id"));
/* 709 */       repost.put("sn", userField.get("screen_name"));
/* 710 */       repost.put("head", userField.get("avatar_large"));
/*     */ 
/* 712 */       addRepost(repost);
/*     */     }
/* 714 */     fetched = reposts.size();
/*     */ 
/* 716 */     if (fetched >= 195) {
/* 717 */       fetched += doFetchRepost(sourceWeibo, page + 1).intValue();
/*     */     }
/*     */ 
/* 721 */     if (page > 1) return Integer.valueOf(fetched);
/*     */ 
/* 723 */     DBObject updateWeibo = new BasicDBObject();
/*     */ 
/* 725 */     Map setMap = new HashMap();
/* 726 */     setMap.put("since_id", Long.valueOf(since_id));
/*     */ 
/* 728 */     long count = this.dataSource.getCollection("reposts").count(new BasicDBObject("wbid", sourceWeibo.get("_id")));
/* 729 */     setMap.put("reposts", Long.valueOf(count));
/* 730 */     setMap.put("refreshed", Long.valueOf(System.currentTimeMillis()));
/* 731 */     updateWeibo.put("$set", setMap);
/*     */ 
/* 733 */     DBCollection coll = this.dataSource.getCollection("gifts");
/* 734 */     coll.update(new BasicDBObject("_id", sourceWeibo.get("_id")), updateWeibo);
/* 735 */     this.timelines = null;
/* 736 */     return Integer.valueOf(fetched);
/*     */   }
/*     */   @RestService(uri="/3d", authenticated=false)
/*     */   public String getSeeds() {
/* 741 */     return this.seed;
/*     */   }
/*     */   @RestService(uri="/3d/set", authenticated=false)
/*     */   public String getSeeds(@RestParam("seed") String sse) {
/* 746 */     this.seed = sse;
/* 747 */     return this.seed;
/*     */   }
/*     */ 
/*     */   private Long parseTime(String t)
/*     */   {
/* 753 */     String[] parts = t.split(" ");
/* 754 */     Integer year = new Integer(parts[5]);
/*     */ 
/* 756 */     Integer month = indexOf(ML, parts[1]);
/* 757 */     Integer date = new Integer(parts[2]);
/* 758 */     String[] seqs = parts[3].split(":");
/*     */ 
/* 760 */     Integer hrs = new Integer(seqs[0]);
/* 761 */     Integer mins = new Integer(seqs[1]);
/* 762 */     Integer sec = new Integer(seqs[2]);
/*     */ 
/* 764 */     Date d = new Date(year.intValue() - 1900, month.intValue(), date.intValue(), hrs.intValue(), mins.intValue(), sec.intValue());
/* 765 */     return Long.valueOf(d.getTime());
/*     */   }
/*     */ 
/*     */   private Integer indexOf(String[] ml, String month) {
/* 769 */     for (int i = 0; i < ml.length; i++) {
/* 770 */       if (month.equals(ml[i])) {
/* 771 */         return Integer.valueOf(i);
/*     */       }
/*     */     }
/* 774 */     return Integer.valueOf(-1);
/*     */   }
/*     */ 
/*     */   class FetchRepostTask extends TimerTask
/*     */   {
/* 614 */     Map<String, Long> nextIvc = new HashMap();
/* 615 */     Map<String, Long> lastInteval = new HashMap();
/*     */ 
/*     */     FetchRepostTask() {
/*     */     }
/* 619 */     public void run() { DBCursor cursor = GiftService.this.dataSource.getCollection("gifts").find(new BasicDBObject("active", Boolean.valueOf(true)));
/*     */ 
/* 621 */       while (cursor.hasNext()) {
/* 622 */         DBObject dbo = cursor.next();
/*     */ 
/* 624 */         String wbid = dbo.get("_id").toString();
/*     */ 
/* 626 */         if ((this.nextIvc.get(wbid) != null) && (((Long)this.nextIvc.get(wbid)).longValue() >= System.currentTimeMillis()))
/*     */           continue;
/* 628 */         int got = GiftService.this.doFetchRepost(dbo, 1).intValue();
/*     */ 
/* 630 */         if (got >= 200)
/* 631 */           this.lastInteval.put(wbid, GiftService.INTEVAL_VERY_SHORT);
/* 632 */         else if (got >= 100)
/* 633 */           this.lastInteval.put(wbid, GiftService.INTEVAL_SHORT);
/* 634 */         else if (got >= 50)
/* 635 */           this.lastInteval.put(wbid, GiftService.INTEVAL_LONG);
/* 636 */         else if (got >= 10)
/* 637 */           this.lastInteval.put(wbid, GiftService.INTEVAL_SUPER_LONG);
/*     */         else {
/* 639 */           this.lastInteval.put(wbid, GiftService.INTEVAL_SUPER_LONG);
/*     */         }
/* 641 */         this.nextIvc.put(wbid, Long.valueOf(System.currentTimeMillis() + ((Long)this.lastInteval.get(wbid)).longValue()));
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           D:\360云盘\Desktops\T410\a.jar
 * Qualified Name:     com.ever365.sale.GiftService
 * JD-Core Version:    0.6.0
 */