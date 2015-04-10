/*     */ package com.ever365.sale;
/*     */ 
/*     */ import com.ever365.auth.WeiboOAuthProvider;
/*     */ import com.ever365.common.ContentStore;
/*     */ import com.ever365.mongo.AutoIncrementingHelper;
/*     */ import com.ever365.mongo.MongoDataSource;
/*     */ import com.ever365.rest.AuthenticationUtil;
/*     */ import com.ever365.rest.HttpStatus;
/*     */ import com.ever365.rest.HttpStatusException;
/*     */ import com.ever365.rest.RestParam;
/*     */ import com.ever365.rest.RestResult;
/*     */ import com.ever365.rest.RestService;
/*     */ import com.ever365.rest.StreamObject;
/*     */ import com.ever365.rest.WebContext;
/*     */ import com.ever365.utils.StringUtils;
/*     */ import com.ever365.utils.WebUtils;
/*     */ import com.mongodb.BasicDBObject;
/*     */ import com.mongodb.BasicDBObjectBuilder;
/*     */ import com.mongodb.DBCollection;
/*     */ import com.mongodb.DBCursor;
/*     */ import com.mongodb.DBObject;
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.net.URLEncoder;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.logging.Logger;
/*     */ import org.bson.types.ObjectId;
/*     */ import org.json.JSONException;
/*     */ import org.json.JSONObject;
/*     */ 
/*     */ public class RepostService
/*     */ {
/*     */   private static final String COLL_PRESENTS = "gifts";
/*     */   private static final String SESSIONID = "sessionid";
/*  43 */   public static final Integer GIFT_NOT_APPROVED = Integer.valueOf(0);
/*  44 */   public static final Integer GIFT_APPROVED = Integer.valueOf(1);
/*  45 */   public static final Integer GIFT_POSTED = Integer.valueOf(2);
/*  46 */   public static final Integer GIFT_FINISHED = Integer.valueOf(3);
/*     */ 
/*  48 */   Logger logger = Logger.getLogger(RepostService.class.getName());
/*     */   private MongoDataSource dataSource;
/*     */   private ContentStore contentStore;
/*     */   private AutoIncrementingHelper incrementingHelper;
/*     */   private WeiboOAuthProvider weiboOAuthProvider;
/* 358 */   private Map<Integer, Map> postCache = new HashMap();
/*     */ 
/*     */   public void setWeiboOAuthProvider(WeiboOAuthProvider weiboOAuthProvider)
/*     */   {
/*  56 */     this.weiboOAuthProvider = weiboOAuthProvider;
/*     */   }
/*     */   public void setContentStore(ContentStore contentStore) {
/*  59 */     this.contentStore = contentStore;
/*     */   }
/*     */   public void setDataSource(MongoDataSource dataSource) {
/*  62 */     this.dataSource = dataSource;
/*     */   }
/*     */ 
/*     */   public void setIncrementingHelper(AutoIncrementingHelper incrementingHelper) {
/*  66 */     this.incrementingHelper = incrementingHelper;
/*     */   }
/*     */ 
/*     */   public void init() {
/*  70 */     DBCollection coll = this.dataSource.getCollection("gifts");
/*  71 */     coll.ensureIndex("seq");
/*  72 */     coll.ensureIndex("status");
/*     */ 
/*  74 */     DBCollection wbs = this.dataSource.getCollection("wbs");
/*     */ 
/*  76 */     wbs.ensureIndex("ctime");
/*     */   }
/*     */ 
/*     */   @RestService(method="GET", uri="/user/info")
/*     */   public Map<String, Object> getUserInfos() {
/*  82 */     Map winfo = new HashMap();
/*  83 */     winfo.putAll(this.weiboOAuthProvider.getWeiboInfo(null));
/*  84 */     winfo.put("cu", AuthenticationUtil.getCurrentUser());
/*     */ 
/*  86 */     return winfo;
/*     */   }
/*     */ 
/*     */   @RestService(method="POST", uri="/seller/present/add")
/*     */   public void request(@RestParam(required=true, value="url") String url, @RestParam(required=true, value="per") Integer per, @RestParam(required=true, value="total") Integer total, @RestParam(required=true, value="desc") String desc, @RestParam(required=true, value="dav") String dav, @RestParam(required=true, value="fans") Integer fans, @RestParam(required=false, value="preview") String preview)
/*     */   {
/*     */     try
/*     */     {
/* 107 */       DBCollection coll = this.dataSource.getCollection("gifts");
/* 108 */       DBObject dbo = new BasicDBObject();
/* 109 */       dbo.put("url", url);
/* 110 */       dbo.put("per", per);
/* 111 */       dbo.put("seller", this.weiboOAuthProvider.getWeiboInfo(null).get("rn"));
/* 112 */       dbo.put("sid", AuthenticationUtil.getCurrentUser());
/* 113 */       dbo.put("total", total);
/* 114 */       dbo.put("desc", desc);
/* 115 */       dbo.put("preview", preview);
/* 116 */       dbo.put("dav", dav);
/* 117 */       dbo.put("fans", fans);
/*     */ 
/* 119 */       dbo.put("seq", this.incrementingHelper.getNextSequence("weibo"));
/* 120 */       dbo.put("status", GIFT_NOT_APPROVED);
/*     */ 
/* 122 */       coll.insert(new DBObject[] { dbo });
/*     */     } catch (Exception e) {
/* 124 */       throw new HttpStatusException(HttpStatus.BAD_REQUEST);
/*     */     }
/*     */   }
/*     */ 
/*     */   @RestService(method="GET", uri="/seller/present")
/*     */   public List<Map<String, Object>> getMyPresents()
/*     */   {
/* 134 */     DBCollection coll = this.dataSource.getCollection("gifts");
/* 135 */     BasicDBObject query = new BasicDBObject("sid", AuthenticationUtil.getCurrentUser());
/*     */ 
/* 137 */     List list = new ArrayList();
/* 138 */     DBCursor cursor = coll.find(query);
/*     */ 
/* 140 */     while (cursor.hasNext()) {
/* 141 */       list.add(cursor.next().toMap());
/*     */     }
/* 143 */     return list;
/*     */   }
/*     */ 
/*     */   @RestService(method="POST", uri="/seller/present/remove")
/*     */   public void removeMyPresent(@RestParam("id") String id)
/*     */   {
/* 152 */     BasicDBObject query = new BasicDBObject("_id", new ObjectId(id));
/* 153 */     query.put("sid", AuthenticationUtil.getCurrentUser());
/* 154 */     query.put("status", GIFT_NOT_APPROVED);
/*     */ 
/* 156 */     DBCollection coll = this.dataSource.getCollection("gifts");
/* 157 */     coll.remove(query);
/*     */   }
/*     */ 
/*     */   @RestService(method="GET", uri="/present/list")
/*     */   public List<Map<String, Object>> getPresents()
/*     */   {
/* 166 */     DBCollection coll = this.dataSource.getCollection("gifts");
/* 167 */     BasicDBObject query = new BasicDBObject("status", GIFT_APPROVED);
/*     */ 
/* 169 */     List list = new ArrayList();
/* 170 */     DBCursor cursor = coll.find(query);
/*     */ 
/* 172 */     while (cursor.hasNext()) {
/* 173 */       list.add(cursor.next().toMap());
/*     */     }
/* 175 */     return list;
/*     */   }
/*     */ 
/*     */   @RestService(method="GET", uri="/present/today")
/*     */   public Collection<Map> getTodayGifts()
/*     */   {
/* 184 */     if (this.postCache.keySet().size() == 0) {
/* 185 */       DBCollection coll = this.dataSource.getCollection("gifts");
/* 186 */       BasicDBObject query = new BasicDBObject("status", GIFT_POSTED);
/* 187 */       List list = new ArrayList();
/* 188 */       DBCursor cursor = coll.find(query);
/*     */ 
/* 190 */       while (cursor.hasNext()) {
/* 191 */         Map m = cursor.next().toMap();
/* 192 */         Integer seq = Integer.valueOf(((Long)m.get("seq")).intValue());
/* 193 */         m.put("count", this.incrementingHelper.getCurrentSequence("repost" + seq));
/* 194 */         this.postCache.put(seq, m);
/*     */       }
/*     */     }
/* 197 */     return this.postCache.values();
/*     */   }
/*     */ 
/*     */   @RestService(method="GET", uri="/dav/is")
/*     */   public String isDAV() {
/* 203 */     DBCollection wcoll = this.dataSource.getCollection("davs");
/*     */ 
/* 205 */     Object realName = this.weiboOAuthProvider.getFullWeiboInfo(null).get("rn");
/*     */ 
/* 207 */     DBObject one = wcoll.findOne(new BasicDBObject("name", realName));
/* 208 */     if (one != null) {
/* 209 */       return "1";
/*     */     }
/* 211 */     return "0";
/*     */   }
/*     */ 
/*     */   @RestService(method="POST", uri="/dav/add")
/*     */   public void addV(@RestParam(required=true, value="code") String code)
/*     */   {
/*     */     try
/*     */     {
/* 231 */       DBCollection coll = this.dataSource.getCollection("vcodes");
/* 232 */       DBObject one = coll.findOne(new BasicDBObject("code", code));
/*     */ 
/* 234 */       if (one != null) {
/* 235 */         DBCollection wcoll = this.dataSource.getCollection("davs");
/* 236 */         wcoll.insert(new DBObject[] { new BasicDBObject("name", AuthenticationUtil.getCurrentUser()) });
/* 237 */         coll.remove(new BasicDBObject("code", code));
/*     */       }
/* 239 */       return; } catch (Exception e) {
/*     */     }
/* 241 */     throw new HttpStatusException(HttpStatus.BAD_REQUEST);
/*     */   }
/*     */ 
/*     */   @RestService(method="GET", uri="/dav/info")
/*     */   public Map<String, Object> getCurrentDAV()
/*     */   {
/* 248 */     DBCollection coll = this.dataSource.getCollection("davs");
/* 249 */     DBObject one = coll.findOne(new BasicDBObject("name", AuthenticationUtil.getCurrentUser()));
/* 250 */     if (one == null) {
/* 251 */       throw new HttpStatusException(HttpStatus.FORBIDDEN);
/*     */     }
/* 253 */     return one.toMap();
/*     */   }
/*     */ 
/*     */   @RestService(method="GET", uri="/dav/mypost")
/*     */   public List<Map<String, Object>> getMyPosts() {
/* 259 */     DBCollection coll = this.dataSource.getCollection("gifts");
/* 260 */     BasicDBObject query = new BasicDBObject();
/* 261 */     query.put("dav", AuthenticationUtil.getCurrentUser());
/*     */ 
/* 263 */     DBCursor cursor = coll.find(query);
/* 264 */     List r = new ArrayList();
/* 265 */     while (cursor.hasNext()) {
/* 266 */       r.add(cursor.next().toMap());
/*     */     }
/* 268 */     return r;
/*     */   }
/*     */ 
/*     */   @RestService(method="POST", uri="/dav/post", reqireAt=true, webcontext=true)
/*     */   public synchronized void postWeibo(@RestParam("id") String mid, @RestParam("msg") String msg) {
/* 274 */     if (isDAV().equals("0")) {
/* 275 */       throw new HttpStatusException(HttpStatus.BAD_REQUEST);
/*     */     }
/*     */ 
/* 278 */     DBCollection coll = this.dataSource.getCollection("gifts");
/*     */ 
/* 280 */     BasicDBObject query = new BasicDBObject("_id", new ObjectId(mid));
/* 281 */     query.put("status", GIFT_APPROVED);
/*     */ 
/* 283 */     DBObject gift = coll.findOne(query);
/*     */ 
/* 285 */     if (gift == null) throw new HttpStatusException(HttpStatus.LOCKED);
/*     */ 
/* 287 */     Map params = new HashMap();
/* 288 */     if (gift.get("preview") != null)
/*     */     {
/* 290 */       StreamObject cd = this.contentStore.getContentData((String)gift.get("preview"));
/* 291 */       params.put("pic", cd.getInputStream());
/* 292 */       params.put("size", Long.valueOf(cd.getSize()));
/*     */     } else {
/* 294 */       throw new HttpStatusException(HttpStatus.PRECONDITION_FAILED);
/*     */     }
/*     */     try
/*     */     {
/* 298 */       params.put("access_token", this.weiboOAuthProvider.getFullWeiboInfo(null).get("at"));
/* 299 */       params.put("status", URLEncoder.encode(msg, "UTF-8"));
/*     */     }
/*     */     catch (UnsupportedEncodingException localUnsupportedEncodingException)
/*     */     {
/*     */     }
/* 304 */     Map set = new HashMap();
/* 305 */     if (WebContext.isLocal()) {
/* 306 */       set.put("msg", msg);
/*     */     } else {
/* 308 */       JSONObject result = WebUtils.doPost("https://upload.api.weibo.com/2/statuses/upload.json", params);
/*     */ 
/* 310 */       Map weibo = WebUtils.jsonObjectToMap(result);
/*     */ 
/* 312 */       if (weibo.get("id") == null) {
/* 313 */         throw new HttpStatusException(HttpStatus.NOT_ACCEPTABLE);
/*     */       }
/* 315 */       this.dataSource.getCollection("weibos").insert(new DBObject[] { new BasicDBObject(weibo) });
/* 316 */       set.put("wid", weibo.get("id"));
/* 317 */       set.put("dav", this.weiboOAuthProvider.getFullWeiboInfo(null).get("rn"));
/* 318 */       set.put("msg", weibo.get("text"));
/* 319 */       set.put("tn", weibo.get("thumbnail_pic"));
/*     */     }
/* 321 */     set.put("status", GIFT_POSTED);
/* 322 */     set.put("time", Long.valueOf(System.currentTimeMillis()));
/* 323 */     BasicDBObject update = new BasicDBObject();
/* 324 */     update.put("$set", set);
/* 325 */     coll.update(query, update);
/*     */   }
/*     */ 
/*     */   @RestService(method="GET", uri="/dav/i", webcontext=true, authenticated=false)
/*     */   public Map<String, Object> getRepost(@RestParam("i") String seq) {
/* 331 */     Integer i = Integer.valueOf(Integer.parseInt(seq));
/*     */ 
/* 333 */     Map post = getPost(i);
/*     */ 
/* 335 */     if (post == null) throw new HttpStatusException(HttpStatus.NOT_FOUND);
/*     */ 
/* 338 */     Map result = new HashMap();
/* 339 */     result.putAll(post);
/*     */ 
/* 341 */     if (AuthenticationUtil.getCurrentUser() != null) {
/* 342 */       DBCollection reposts = this.dataSource.getCollection("reposts");
/*     */ 
/* 345 */       DBObject repost = reposts.findOne(BasicDBObjectBuilder.start().add("seq", i)
/* 346 */         .add("u", AuthenticationUtil.getCurrentUser()).get());
/* 347 */       if (repost != null) {
/* 348 */         result.put("code", repost.get("code"));
/*     */       }
/* 350 */       result.put("cu", AuthenticationUtil.getCurrentUser());
/*     */     }
/*     */ 
/* 353 */     result.put("weibos", getTodayGifts());
/* 354 */     return result;
/*     */   }
/*     */ 
/*     */   @RestService(method="GET", uri="/dav/post/get", authenticated=false)
/*     */   public Map<String, Object> getPost(@RestParam("i") Integer seq)
/*     */   {
/* 363 */     if (this.postCache.get(seq) == null) {
/* 364 */       DBCollection coll = this.dataSource.getCollection("gifts");
/* 365 */       BasicDBObject query = new BasicDBObject("seq", seq);
/* 366 */       DBObject gift = coll.findOne(query);
/* 367 */       if (gift != null) {
/* 368 */         Map map = gift.toMap();
/* 369 */         map.put("count", this.incrementingHelper.getCurrentSequence("repost" + seq));
/* 370 */         this.postCache.put(seq, gift.toMap());
/*     */       }
/*     */     }
/* 373 */     return (Map)this.postCache.get(seq);
/*     */   }
/*     */ 
/*     */   @RestService(method="POST", uri="/dav/repost/book", authenticated=false, webcontext=true)
/*     */   public void markAsBook(@RestParam("i") String seq) {
/* 379 */     DBCollection coll = this.dataSource.getCollection("reposts");
/* 380 */     coll.insert(new DBObject[] { new BasicDBObject("sessionid", WebContext.getSessionID()) });
/*     */   }
/*     */ 
/*     */   @RestService(method="POST", uri="/dav/repost")
/*     */   public Map<String, Object> doRepost(@RestParam("i") String seq, @RestParam("msg") String msg)
/*     */   {
/* 387 */     Map post = getPost(new Integer(seq));
/* 388 */     if (post == null) {
/* 389 */       throw new HttpStatusException(HttpStatus.BAD_REQUEST);
/*     */     }
/* 391 */     DBCollection reposts = this.dataSource.getCollection("reposts");
/* 392 */     DBObject one = reposts.findOne(BasicDBObjectBuilder.start().add("seq", new Integer(seq)).add("u", AuthenticationUtil.getCurrentUser()).get());
/*     */ 
/* 394 */     if (one != null) {
/* 395 */       return one.toMap();
/*     */     }
/*     */ 
/* 398 */     DBObject dbo = new BasicDBObject();
/*     */ 
/* 400 */     dbo.put("seq", new Integer(seq));
/* 401 */     dbo.put("code", this.incrementingHelper.getNextSequence("repost-" + seq));
/* 402 */     dbo.put("u", AuthenticationUtil.getCurrentUser());
/* 403 */     dbo.put("t", Long.valueOf(System.currentTimeMillis()));
/*     */ 
/* 405 */     if (!WebContext.isLocal()) {
/* 406 */       if (post.get("wid") == null) throw new HttpStatusException(HttpStatus.BAD_REQUEST);
/* 407 */       Map params = new HashMap();
/*     */ 
/* 409 */       params.put("access_token", this.weiboOAuthProvider.getFullWeiboInfo(null).get("at"));
/* 410 */       params.put("id", post.get("wid"));
/* 411 */       params.put("is_comment", Integer.valueOf(3));
/*     */ 
/* 421 */       this.logger.info("REPOST: " + msg + "  " + post.get("wid") + "  " + params.get("access_token"));
/*     */ 
/* 423 */       JSONObject result = WebUtils.doPost("https://api.weibo.com/2/statuses/repost.json", params);
/*     */ 
/* 425 */       if (result.has("id"))
/*     */         try {
/* 427 */           dbo.put("wid", Long.valueOf(result.getLong("id")));
/*     */         }
/*     */         catch (JSONException localJSONException) {
/*     */         }
/*     */       else {
/* 432 */         throw new HttpStatusException(HttpStatus.FORBIDDEN);
/*     */       }
/*     */     }
/*     */ 
/* 436 */     reposts.insert(new DBObject[] { dbo });
/*     */ 
/* 438 */     Long next = this.incrementingHelper.getNextSequence("repost" + seq);
/* 439 */     post.put("count", next);
/*     */ 
/* 441 */     return dbo.toMap();
/*     */   }
/*     */   @RestService(uri="/repost/my", method="GET")
/*     */   public List<Map<String, Object>> getMyReposts() {
/* 446 */     DBCollection reposts = this.dataSource.getCollection("reposts");
/* 447 */     if (AuthenticationUtil.getCurrentUser() == null) return Collections.EMPTY_LIST;
/* 448 */     DBCursor cur = reposts.find(BasicDBObjectBuilder.start().add("u", AuthenticationUtil.getCurrentUser()).get());
/*     */ 
/* 450 */     List result = new ArrayList();
/* 451 */     while (cur.hasNext()) {
/* 452 */       DBObject o = cur.next();
/* 453 */       Map m = o.toMap();
/* 454 */       m.put("post", getPost((Integer)m.get("seq")));
/* 455 */       result.add(m);
/*     */     }
/* 457 */     return result;
/*     */   }
/*     */   @RestService(uri="/repost/gift", method="GET")
/*     */   public List<Map<String, Object>> getMyWinReposts() {
/* 462 */     DBCollection reposts = this.dataSource.getCollection("repostwins");
/* 463 */     if (AuthenticationUtil.getCurrentUser() == null) return Collections.EMPTY_LIST;
/* 464 */     DBCursor cur = reposts.find(BasicDBObjectBuilder.start().add("u", AuthenticationUtil.getCurrentUser()).get());
/*     */ 
/* 466 */     List result = new ArrayList();
/* 467 */     while (cur.hasNext()) {
/* 468 */       DBObject o = cur.next();
/* 469 */       Map m = o.toMap();
/* 470 */       m.put("post", getPost((Integer)m.get("seq")));
/* 471 */       result.add(m);
/*     */     }
/* 473 */     return result;
/*     */   }
/*     */ 
/*     */   @RestService(uri="/repost/receiveLocation", method="GET")
/*     */   public Map<String, Object> getMyPostLocation() {
/* 479 */     DBCollection locations = this.dataSource.getCollection("location");
/*     */ 
/* 481 */     DBObject post = locations.findOne(new BasicDBObject("u", AuthenticationUtil.getCurrentUser()));
/* 482 */     if (post == null) {
/* 483 */       return Collections.EMPTY_MAP;
/*     */     }
/* 485 */     return post.toMap();
/*     */   }
/*     */ 
/*     */   @RestService(uri="/repost/receiveLocation", method="POST")
/*     */   public void updateMyPostLocation(@RestParam("rn") String rn, @RestParam("detail") String detail, @RestParam("mobile") String mobile, @RestParam("phone") String phone)
/*     */   {
/* 493 */     DBCollection locations = this.dataSource.getCollection("location");
/*     */ 
/* 495 */     DBObject post = new BasicDBObject();
/* 496 */     post.put("u", AuthenticationUtil.getCurrentUser());
/* 497 */     post.put("rn", rn);
/* 498 */     post.put("detail", detail);
/* 499 */     post.put("mobile", mobile);
/* 500 */     post.put("phone", phone);
/* 501 */     locations.update(new BasicDBObject("u", AuthenticationUtil.getCurrentUser()), post, true, false);
/*     */   }
/*     */   @RestService(uri="/weibo/cancel", method="GET")
/*     */   public RestResult cancelWeibo() {
/* 506 */     this.weiboOAuthProvider.cancelValidate();
/* 507 */     RestResult rr = new RestResult();
/* 508 */     Map session = new HashMap();
/* 509 */     session.put(AuthenticationUtil.SESSION_CURRENT_USER, null);
/* 510 */     rr.setSession(session);
/* 511 */     rr.setRedirect("/");
/* 512 */     return rr;
/*     */   }
/*     */ 
/*     */   @RestService(uri="/weibo/gift/import", method="POST")
/*     */   public Map<String, Object> importWeibo(@RestParam("wbid") String id, @RestParam("ctime") String ctime, @RestParam("resp") String resp) {
/* 518 */     String at = (String)this.weiboOAuthProvider.getFullWeiboInfo(null).get("at");
/* 519 */     String requestUrl = "https://api.weibo.com/2/statuses/show.json?access_token=" + at + "&id=" + id;
/*     */ 
/* 521 */     this.logger.info("import url " + requestUrl);
/* 522 */     JSONObject jso = WebUtils.doGet(requestUrl);
/*     */ 
/* 524 */     Map m = WebUtils.jsonObjectToMap(jso);
/*     */ 
/* 526 */     if (m.get("error") != null) {
/* 527 */       return m;
/*     */     }
/*     */ 
/* 530 */     DBCollection wbcoll = this.dataSource.getCollection("wbs");
/*     */ 
/* 532 */     BasicDBObject bdo = new BasicDBObject();
/* 533 */     bdo.putAll(m);
/* 534 */     bdo.put("ctime", StringUtils.parseDate(ctime));
/* 535 */     bdo.put("index", Boolean.valueOf(resp));
/* 536 */     wbcoll.insert(new DBObject[] { bdo });
/*     */ 
/* 548 */     return m;
/*     */   }
/*     */ 
/*     */   @RestService(uri="/weibo/gift/index", method="GET")
/*     */   public List<Map> getWeiboIndex() {
/* 554 */     return null;
/*     */   }
/*     */ }

/* Location:           D:\360云盘\Desktops\T410\a.jar
 * Qualified Name:     com.ever365.sale.RepostService
 * JD-Core Version:    0.6.0
 */