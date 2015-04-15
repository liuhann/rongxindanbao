/*     */ package com.ever365.sale;
/*     */ 
/*     */ import com.ever365.common.LocalContentStore;
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
/*     */ import com.ever365.utils.MapUtils;
/*     */ import com.ever365.utils.StringUtils;
/*     */ import com.mongodb.BasicDBObject;
/*     */ import com.mongodb.BasicDBObjectBuilder;
/*     */ import com.mongodb.DBCollection;
/*     */ import com.mongodb.DBCursor;
/*     */ import com.mongodb.DBObject;
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.Date;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import org.bson.types.ObjectId;
/*     */ import org.springframework.util.FileCopyUtils;
/*     */ 
/*     */ public class SellerService
/*     */ {
/*     */   private static final String BOOK_COUNT_PREFIX = "B";
/*     */   private static final String DEALS_COUNT_PREFIX = "D";
/*     */   private static final String COLL_DEALS = "deals";
/*     */   private static final String COLL_BOOKS = "books";
/*     */   private static final String COLL_SALES = "sales";
/*     */   private static final String COLL_SELLER = "sellers";
/*     */   private static final String FIELD_BOOK_TIME = "t";
/*     */   private static final String FIELD_BOOK_USER = "u";
/*     */   private static final String FIELD_BOOK_SALE_ID = "m";
/*     */   private static final String FIELD_ONSALE_TIME = "time";
/*     */   private static final String FIELD_PRICE = "price";
/*     */   private static final String FIELD_TITLE = "title";
/*     */   private static final String FIELD_SELLER = "seller";
/*     */   private static final String FIELD_BOOK_CODE = "o";
/*     */   private static final String FIELD_COUNT = "count";
/*     */   private static final String FIELD_IS_ONLINE = "online";
/*     */   private static final String FIELD_SALE_ID = "seq";
/*     */   private static final String FIELD_CONTENT = "content";
/*     */   private static final String FIELD_ID = "_id";
/*     */   private static final String STRING_EMPTY = "";
/*     */   private MongoDataSource dataSource;
/*     */   private LocalContentStore contentStore;
/*     */   private AutoIncrementingHelper incrementingHelper;
/*     */   private RepostService repostService;
/* 460 */   public Map<Integer, Map> saleShortCache = new HashMap();
/* 461 */   public Map<Integer, Integer> saleCountCache = new HashMap();
/*     */ 
/*     */   public void setIncrementingHelper(AutoIncrementingHelper incrementingHelper)
/*     */   {
/*  72 */     this.incrementingHelper = incrementingHelper;
/*  73 */     incrementingHelper.initIncreasor("sales");
/*     */   }
/*     */ 
/*     */   public void setRepostService(RepostService repostService) {
/*  77 */     this.repostService = repostService;
/*     */   }
/*     */ 
/*     */   public void setContentStore(LocalContentStore contentStore) {
/*  81 */     this.contentStore = contentStore;
/*     */   }
/*     */ 
/*     */   public void setDataSource(MongoDataSource dataSource) {
/*  85 */     this.dataSource = dataSource;
/*     */   }
/*     */ 
/*     */   @RestService(method="POST", uri="/seller/register", authenticated=false)
/*     */   public RestResult register(@RestParam(required=true, value="name") String name, @RestParam(required=true, value="shop") String shop, @RestParam(required=true, value="pass") String pass, @RestParam(required=true, value="user") String user, @RestParam(required=true, value="phone") String phone, @RestParam(required=true, value="email") String email, @RestParam(required=true, value="other") String other)
/*     */   {
/*  97 */     DBCollection coll = this.dataSource.getCollection("sellers");
/*     */ 
/*  99 */     DBObject exist = coll.findOne(new BasicDBObject("name", name));
/* 100 */     if (exist != null) {
/* 101 */       throw new HttpStatusException(HttpStatus.CONFLICT);
/*     */     }
/* 103 */     DBObject dbo = new BasicDBObject();
/* 104 */     dbo.put("name", name);
/* 105 */     dbo.put("shop", shop);
/* 106 */     dbo.put("pass", pass);
/* 107 */     dbo.put("user", user);
/* 108 */     dbo.put("phone", phone);
/* 109 */     dbo.put("email", email);
/* 110 */     dbo.put("other", other);
/* 111 */     coll.insert(new DBObject[] { dbo });
/* 112 */     RestResult rr = new RestResult();
/* 113 */     Map session = new HashMap();
/* 114 */     session.put(AuthenticationUtil.SESSION_CURRENT_USER, name);
/* 115 */     rr.setSession(session);
/* 116 */     return rr;
/*     */   }
/*     */   @RestService(method="GET", uri="/seller/info")
/*     */   public Map<String, Object> getSellerInfo() {
/* 121 */     DBCollection coll = this.dataSource.getCollection("sellers");
/* 122 */     DBObject exist = coll.findOne(new BasicDBObject("uid", AuthenticationUtil.getCurrentUser()));
/* 123 */     Map r = new HashMap();
/* 124 */     r.put("rn", AuthenticationUtil.getRealName());
/* 125 */     r.put("cu", AuthenticationUtil.getCurrentUser());
/* 126 */     if (exist == null) {
/* 127 */       return r;
/*     */     }
/* 129 */     r.putAll(exist.toMap());
/*     */ 
/* 131 */     return r;
/*     */   }
/*     */ 
/*     */   @RestService(method="POST", uri="/seller/info")
/*     */   public Map setSellerInfo(@RestParam(required=true, value="name") String name, @RestParam(required=true, value="shop") String shop, @RestParam(required=true, value="contact") String user, @RestParam(required=true, value="mobile") String mobile)
/*     */   {
/* 140 */     DBCollection coll = this.dataSource.getCollection("sellers");
/*     */ 
/* 142 */     DBObject dbo = new BasicDBObject();
/* 143 */     dbo.put("name", name);
/* 144 */     dbo.put("shop", shop);
/* 145 */     dbo.put("user", user);
/* 146 */     dbo.put("mobile", mobile);
/* 147 */     dbo.put("uid", AuthenticationUtil.getCurrentUser());
/* 148 */     coll.update(new BasicDBObject("uid", AuthenticationUtil.getCurrentUser()), dbo, true, false);
/* 149 */     return dbo.toMap();
/*     */   }
/*     */ 
/*     */   @RestService(method="POST", uri="/seller/password")
/*     */   public Integer setSellerPwd(@RestParam(required=true, value="old") String oldpass, @RestParam(required=true, value="new") String newpass) {
/* 155 */     DBCollection coll = this.dataSource.getCollection("sellers");
/*     */ 
/* 157 */     DBObject dbo = coll.findOne(new BasicDBObject("name", AuthenticationUtil.getCurrentUser()));
/* 158 */     if (dbo == null) {
/* 159 */       throw new HttpStatusException(HttpStatus.NOT_FOUND);
/*     */     }
/*     */ 
/* 162 */     if (oldpass.equals(dbo.get("pass"))) {
/* 163 */       dbo.put("pass", newpass);
/* 164 */       coll.update(new BasicDBObject("name", AuthenticationUtil.getCurrentUser()), dbo);
/* 165 */       return Integer.valueOf(1);
/*     */     }
/* 167 */     return Integer.valueOf(-1);
/*     */   }
/*     */ 
/*     */   @RestService(method="POST", uri="/seller/login", authenticated=false)
/*     */   public RestResult login(@RestParam(required=true, value="name") String name, @RestParam(required=true, value="pass") String pass)
/*     */   {
/* 175 */     DBCollection coll = this.dataSource.getCollection("sellers");
/*     */ 
/* 177 */     DBObject exist = coll.findOne(new BasicDBObject("name", name));
/* 178 */     if (exist == null) {
/* 179 */       throw new HttpStatusException(HttpStatus.BAD_REQUEST);
/*     */     }
/*     */ 
/* 182 */     if (pass.equals(exist.get("pass"))) {
/* 183 */       RestResult rr = new RestResult();
/* 184 */       Map session = new HashMap();
/* 185 */       session.put(AuthenticationUtil.SESSION_CURRENT_USER, name);
/* 186 */       rr.setSession(session);
/* 187 */       return rr;
/*     */     }
/* 189 */     throw new HttpStatusException(HttpStatus.BAD_REQUEST);
/*     */   }
/*     */ 
/*     */   @RestService(method="POST", uri="/seller/request")
/*     */   public void request(@RestParam(required=false, value="id") String id, @RestParam(required=true, value="title") String title, @RestParam(required=false, value="subtitle") String subtitle, @RestParam(required=true, value="url") String url, @RestParam(required=true, value="count") String count, @RestParam(required=true, value="price") String price, @RestParam(required=true, value="oprice") String oprice, @RestParam(required=true, value="time") String time, @RestParam(required=true, value="until") String until, @RestParam(required=false, value="preview") String preview, @RestParam(required=true, value="content") String content)
/*     */   {
/*     */     try
/*     */     {
/* 208 */       DBCollection coll = this.dataSource.getCollection("sales");
/* 209 */       DBObject dbo = new BasicDBObject();
/* 210 */       dbo.put("seller", AuthenticationUtil.getCurrentUser());
/*     */ 
/* 212 */       dbo.put("title", title);
/* 213 */       dbo.put("subtitle", subtitle);
/* 214 */       dbo.put("count", new Integer(count));
/* 215 */       dbo.put("url", url);
/* 216 */       dbo.put("price", price);
/* 217 */       dbo.put("time", Long.valueOf(StringUtils.parseDate(time).getTime()));
/* 218 */       dbo.put("online", Boolean.valueOf(false));
/* 219 */       dbo.put("oprice", oprice);
/* 220 */       dbo.put("preview", preview);
/*     */ 
/* 222 */       String contentId = java.util.UUID.randomUUID().toString();
/*     */ 
/* 224 */       dbo.put("content", contentId);
/*     */ 
/* 226 */       if (until.equals(""))
/* 227 */         dbo.put("until", "");
/*     */       else {
/* 229 */         dbo.put("until", Long.valueOf(StringUtils.parseDate(until).getTime()));
/*     */       }
/*     */       try
/*     */       {
/* 233 */         this.contentStore.putContent(contentId, new ByteArrayInputStream(content.getBytes("utf-8")), "text/html", content.length());
/*     */       } catch (UnsupportedEncodingException e) {
/* 235 */         e.printStackTrace();
/*     */       }
/*     */ 
/* 238 */       if (id == null)
/*     */       {
/* 240 */         dbo.put("seq", this.incrementingHelper.getNextSequence("sales"));
/* 241 */         coll.insert(new DBObject[] { dbo });
/*     */       } else {
/* 243 */         DBObject exsit = coll.findOne(new BasicDBObject("_id", new ObjectId(id)));
/*     */ 
/* 245 */         if (exsit == null) {
/* 246 */           throw new HttpStatusException(HttpStatus.BAD_REQUEST);
/*     */         }
/* 248 */         dbo.put("seq", exsit.get("seq"));
/* 249 */         coll.update(new BasicDBObject("_id", new ObjectId(id)), dbo, true, false);
/*     */       }
/*     */     } catch (Exception e) {
/* 252 */       throw new HttpStatusException(HttpStatus.BAD_REQUEST);
/*     */     }
/*     */   }
/*     */ 
/*     */   @RestService(method="GET", uri="/seller/request/list")
/*     */   public List<Map<String, Object>> getRequesting() {
/* 258 */     DBCollection coll = this.dataSource.getCollection("sales");
/* 259 */     DBObject dbo = new BasicDBObject();
/* 260 */     dbo.put("seller", AuthenticationUtil.getCurrentUser());
/* 261 */     dbo.put("online", Boolean.valueOf(false));
/*     */ 
/* 263 */     List result = new ArrayList();
/*     */ 
/* 265 */     DBCursor cursor = coll.find(dbo);
/*     */ 
/* 267 */     while (cursor.hasNext()) {
/* 268 */       DBObject o = cursor.next();
/* 269 */       Map m = o.toMap();
/* 270 */       Long n = this.incrementingHelper.getCurrentSequence("B" + m.get("seq"));
/* 271 */       m.put("books", n);
/* 272 */       result.add(m);
/*     */     }
/* 274 */     return result;
/*     */   }
/*     */   @RestService(method="GET", uri="/seller/online/list")
/*     */   public List<Map<String, Object>> getOnlines() {
/* 279 */     DBCollection coll = this.dataSource.getCollection("sales");
/* 280 */     DBObject dbo = new BasicDBObject();
/* 281 */     dbo.put("seller", AuthenticationUtil.getCurrentUser());
/* 282 */     dbo.put("online", Boolean.valueOf(true));
/* 283 */     dbo.put("time", MapUtils.newMap("$gt", Long.valueOf(System.currentTimeMillis())));
/*     */ 
/* 285 */     List result = new ArrayList();
/*     */ 
/* 287 */     DBCursor cursor = coll.find(dbo);
/*     */ 
/* 289 */     while (cursor.hasNext()) {
/* 290 */       DBObject o = cursor.next();
/* 291 */       Map m = o.toMap();
/* 292 */       Long n = this.incrementingHelper.getCurrentSequence("B" + m.get("seq"));
/* 293 */       m.put("books", n);
/* 294 */       result.add(m);
/*     */     }
/* 296 */     return result;
/*     */   }
/*     */   @RestService(method="GET", uri="/seller/finished/list")
/*     */   public List<Map<String, Object>> getFinished() {
/* 301 */     DBCollection coll = this.dataSource.getCollection("sales");
/* 302 */     DBObject dbo = new BasicDBObject();
/* 303 */     dbo.put("seller", AuthenticationUtil.getCurrentUser());
/* 304 */     dbo.put("online", Boolean.valueOf(true));
/* 305 */     dbo.put("time", MapUtils.newMap("$lt", Long.valueOf(System.currentTimeMillis())));
/*     */ 
/* 307 */     List result = new ArrayList();
/*     */ 
/* 309 */     DBCursor cursor = coll.find(dbo);
/*     */ 
/* 311 */     while (cursor.hasNext()) {
/* 312 */       DBObject o = cursor.next();
/* 313 */       Map m = o.toMap();
/* 314 */       Long n = this.incrementingHelper.getCurrentSequence("B" + m.get("seq"));
/* 315 */       m.put("books", n);
/* 316 */       result.add(m);
/*     */     }
/* 318 */     return result;
/*     */   }
/*     */ 
/*     */   @RestService(method="GET", uri="/seller/sale")
/*     */   public Map<String, Object> getRequest(@RestParam(required=true, value="id") String id) {
/* 324 */     DBCollection coll = this.dataSource.getCollection("sales");
/* 325 */     DBObject dbo = new BasicDBObject();
/* 326 */     dbo.put("_id", new ObjectId(id));
/* 327 */     DBObject e = coll.findOne(dbo);
/*     */ 
/* 329 */     if (e == null) throw new HttpStatusException(HttpStatus.NOT_FOUND);
/*     */ 
/* 331 */     Map m = e.toMap();
/* 332 */     Long n = this.incrementingHelper.getCurrentSequence("B" + m.get("seq"));
/* 333 */     m.put("books", n);
/* 334 */     m.put("content", getContent(e.get("content").toString()));
/* 335 */     return m;
/*     */   }
/*     */   @RestService(uri="/admin/sale", method="GET", authenticated=true)
/*     */   public Map<String, Object> getFullSaleInfo(@RestParam("id") Integer id) {
/*     */     try {
/* 341 */       Map m = getSaleBySeq(id);
/* 342 */       if (!m.get("seller").equals(AuthenticationUtil.getCurrentUser())) {
/* 343 */         m.remove("count");
/* 344 */         m.remove("_id");
/*     */       }
/* 346 */       return m; } catch (Exception e) {
/*     */     }
/* 348 */     throw new HttpStatusException(HttpStatus.NOT_FOUND);
/*     */   }
/*     */ 
/*     */   @RestService(method="GET", uri="/seller/sale/content")
/*     */   public String getContent(@RestParam(required=true, value="id") String id)
/*     */   {
/* 355 */     StreamObject so = this.contentStore.getContentData(id);
/*     */     try
/*     */     {
/* 358 */       byte[] bytes = FileCopyUtils.copyToByteArray(so.getInputStream());
/* 359 */       return new String(bytes, "UTF-8"); } catch (IOException e) {
/*     */     }
/* 361 */     throw new HttpStatusException(HttpStatus.PRECONDITION_FAILED);
/*     */   }
/*     */ 
/*     */   @RestService(method="POST", uri="/seller/sale/drop")
/*     */   public void dropSale(@RestParam(required=true, value="id") String id) {
/* 367 */     DBCollection coll = this.dataSource.getCollection("sales");
/* 368 */     DBObject dbo = new BasicDBObject();
/* 369 */     dbo.put("_id", new ObjectId(id));
/* 370 */     DBObject e = coll.findOne(dbo);
/* 371 */     if ((!Boolean.TRUE.equals(e.get("online"))) && (AuthenticationUtil.getCurrentUser().equals(e.get("seller"))))
/* 372 */       coll.remove(dbo);
/*     */   }
/*     */ 
/*     */   @RestService(uri="/preview/attach", method="POST", multipart=true)
/*     */   public String uploadPreview(@RestParam("file") InputStream is, @RestParam("size") Long size) {
/* 378 */     String uid = java.util.UUID.randomUUID().toString();
/* 379 */     this.contentStore.putContent(uid, is, "image/png", size.longValue());
/* 380 */     return uid;
/*     */   }
/*     */   @RestService(uri="/preview", method="GET", authenticated=false)
/*     */   public StreamObject getPreview(@RestParam("id") String id) {
/* 385 */     return this.contentStore.getContentData(id);
/*     */   }
/*     */ 
/*     */   @RestService(method="GET", uri="/seller/books")
/*     */   public Map<String, Object> getBooksList(@RestParam(required=true, value="id") Integer id, @RestParam(required=true, value="skip") Integer skip, @RestParam(required=true, value="limit") Integer limit)
/*     */   {
/* 392 */     checkOwnable(id);
/*     */ 
/* 394 */     Map result = new HashMap();
/* 395 */     DBCursor cursor = this.dataSource.getCollection("books").find(new BasicDBObject("m", id));
/*     */ 
/* 397 */     result.put("total", Integer.valueOf(cursor.count()));
/* 398 */     cursor.skip(skip.intValue()).limit(limit.intValue());
/*     */ 
/* 400 */     List list = new ArrayList();
/* 401 */     while (cursor.hasNext()) {
/* 402 */       list.add(cursor.next().toMap());
/*     */     }
/* 404 */     result.put("list", list);
/* 405 */     return result;
/*     */   }
/*     */ 
/*     */   public void checkOwnable(Integer id) {
/* 409 */     DBCollection coll = this.dataSource.getCollection("sales");
/* 410 */     DBObject dbo = new BasicDBObject();
/* 411 */     dbo.put("seq", id);
/* 412 */     DBObject e = coll.findOne(dbo);
/*     */ 
/* 414 */     if (e == null) {
/* 415 */       throw new HttpStatusException(HttpStatus.NOT_FOUND);
/*     */     }
/* 417 */     if ((!AuthenticationUtil.getCurrentUser().equals(e.get("seller"))) && (!AuthenticationUtil.isAdmin()))
/* 418 */       throw new HttpStatusException(HttpStatus.NOT_FOUND);
/*     */   }
/*     */ 
/*     */   @RestService(method="GET", uri="/seller/deals")
/*     */   public List<Map<String, Object>> getDeals(@RestParam(required=true, value="id") Integer id) {
/* 424 */     checkOwnable(id);
/*     */ 
/* 426 */     List list = new ArrayList();
/* 427 */     DBCursor cursor = this.dataSource.getCollection("deals").find(new BasicDBObject("m", id));
/*     */ 
/* 429 */     while (cursor.hasNext()) {
/* 430 */       list.add(cursor.next().toMap());
/*     */     }
/* 432 */     return list;
/*     */   }
/*     */ 
/*     */   @RestService(uri="/sale", method="GET", authenticated=false)
/*     */   public Map<String, Object> getSale(@RestParam("id") Integer id) {
/*     */     try {
/* 439 */       Map m = new HashMap();
/* 440 */       m.putAll(getSaleShortInfo(id));
/* 441 */       if (m == null) {
/* 442 */         throw new HttpStatusException(HttpStatus.NOT_FOUND);
/*     */       }
/* 444 */       if (AuthenticationUtil.getCurrentUser() != null) {
/* 445 */         m.putAll(bookDetail(id));
/*     */       }
/*     */ 
/* 448 */       if (((Long)m.get("time")).longValue() < System.currentTimeMillis()) {
/* 449 */         if (getSaleCount(id).intValue() < 0) {
/* 450 */           m.put("f", Boolean.valueOf(true));
/*     */         }
/* 452 */         m.put("d", getDealCode(id));
/*     */       }
/* 454 */       return m; } catch (Exception e) {
/*     */     }
/* 456 */     throw new HttpStatusException(HttpStatus.NOT_FOUND);
/*     */   }
/*     */ 
/*     */   public Integer getSaleCount(Integer id)
/*     */   {
/* 464 */     if (this.saleCountCache.get(id) == null) {
/* 465 */       DBCollection coll = this.dataSource.getCollection("sales");
/* 466 */       DBObject dbo = new BasicDBObject();
/* 467 */       dbo.put("seq", id);
/* 468 */       DBObject e = coll.findOne(dbo);
/* 469 */       if (e == null) {
/* 470 */         return null;
/*     */       }
/* 472 */       Long bn = this.incrementingHelper.getCurrentSequence("D" + id);
/*     */ 
/* 474 */       if (bn.intValue() >= ((Integer)e.get("count")).intValue())
/* 475 */         this.saleCountCache.put(id, Integer.valueOf(-1));
/*     */       else {
/* 477 */         this.saleCountCache.put(id, (Integer)e.get("count"));
/*     */       }
/*     */     }
/* 480 */     return (Integer)this.saleCountCache.get(id);
/*     */   }
/*     */ 
/*     */   public Map getSaleShortInfo(Integer id) {
/* 484 */     if (this.saleShortCache.get(id) == null) {
/* 485 */       DBCollection coll = this.dataSource.getCollection("sales");
/* 486 */       DBObject dbo = new BasicDBObject();
/* 487 */       dbo.put("seq", id);
/* 488 */       DBObject e = coll.findOne(dbo);
/* 489 */       if (e == null) {
/* 490 */         return null;
/*     */       }
/* 492 */       Map m = e.toMap();
/* 493 */       m.remove("count");
/* 494 */       m.remove("_id");
/*     */ 
/* 496 */       this.saleShortCache.put(id, m);
/* 497 */       Long bn = this.incrementingHelper.getCurrentSequence("D" + id);
/*     */ 
/* 499 */       if (bn.intValue() >= ((Integer)e.get("count")).intValue())
/* 500 */         this.saleCountCache.put(id, Integer.valueOf(-1));
/*     */       else {
/* 502 */         this.saleCountCache.put(id, (Integer)e.get("count"));
/*     */       }
/*     */ 
/* 505 */       return m;
/*     */     }
/* 507 */     return (Map)this.saleShortCache.get(id);
/*     */   }
/*     */ 
/*     */   public Map getSaleBySeq(Integer id) {
/* 511 */     DBCollection coll = this.dataSource.getCollection("sales");
/* 512 */     DBObject dbo = new BasicDBObject();
/* 513 */     dbo.put("seq", id);
/* 514 */     DBObject e = coll.findOne(dbo);
/* 515 */     if (e == null) {
/* 516 */       throw new HttpStatusException(HttpStatus.NOT_FOUND);
/*     */     }
/* 518 */     Map m = e.toMap();
/* 519 */     m.put("content", getContent(e.get("content").toString()));
/* 520 */     return m;
/*     */   }
/*     */   @RestService(uri="/book", method="GET", authenticated=false)
/*     */   public Map<String, Object> bookDetail(@RestParam("id") Integer id) {
/* 525 */     Map m = new HashMap();
/* 526 */     m.put("cu", AuthenticationUtil.getCurrentUser());
/*     */ 
/* 528 */     if (AuthenticationUtil.getCurrentUser() == null) return m;
/*     */ 
/* 530 */     DBCollection bookColl = this.dataSource.getCollection("books");
/* 531 */     DBObject exsitQuery = BasicDBObjectBuilder.start("u", AuthenticationUtil.getCurrentUser())
/* 532 */       .add("m", id).get();
/*     */ 
/* 534 */     DBObject one = bookColl.findOne(exsitQuery);
/* 535 */     if (one != null) {
/* 536 */       m.putAll(one.toMap());
/*     */     }
/* 538 */     return m;
/*     */   }
/*     */   @RestService(uri="/books", method="GET")
/*     */   public List<Map> getMyBooks() {
/* 543 */     List m = new ArrayList();
/*     */ 
/* 545 */     DBCollection bookColl = this.dataSource.getCollection("books");
/*     */ 
/* 547 */     DBObject query = new BasicDBObject();
/* 548 */     query.put("u", AuthenticationUtil.getCurrentUser());
/*     */ 
/* 550 */     DBCursor cursor = bookColl.find(query);
/*     */ 
/* 552 */     while (cursor.hasNext()) {
/* 553 */       DBObject o = cursor.next();
/* 554 */       Map t = o.toMap();
/* 555 */       t.put("sale", getSaleShortInfo((Integer)o.get("m")));
/* 556 */       m.add(t);
/*     */     }
/* 558 */     return m;
/*     */   }
/*     */   @RestService(uri="/book", method="POST", webcontext=true)
/*     */   public Map<String, Object> book(@RestParam("id") Integer id) {
/* 563 */     DBCollection bookColl = this.dataSource.getCollection("books");
/* 564 */     DBObject exsitQuery = BasicDBObjectBuilder.start("u", AuthenticationUtil.getCurrentUser())
/* 565 */       .add("m", id).get();
/*     */ 
/* 567 */     DBObject book = new BasicDBObject();
/* 568 */     book.put("u", AuthenticationUtil.getCurrentUser());
/* 569 */     book.put("t", Long.valueOf(System.currentTimeMillis()));
/* 570 */     book.put("m", id);
/* 571 */     book.put("ip", WebContext.getRemoteAddr());
/* 572 */     Long n = this.incrementingHelper.getNextSequence("B" + id);
/*     */ 
/* 574 */     Date now = new Date();
/* 575 */     book.put("o", "M" + (1000 + id.intValue()) + now.getDate() + (1000L + n.longValue()) + now.getTime() % 1000L);
/* 576 */     bookColl.update(exsitQuery, book, true, false);
/* 577 */     return book.toMap();
/*     */   }
/*     */   @RestService(uri="/buy", method="POST", webcontext=true)
/*     */   public synchronized Map<String, Object> buy(@RestParam("id") Integer id) {
/* 582 */     Map result = new HashMap();
/*     */ 
/* 585 */     if (getSaleCount(id).intValue() == -1) {
/* 586 */       result.put("status", Integer.valueOf(410));
/* 587 */       return result;
/*     */     }
/*     */ 
/* 590 */     Object time = getSaleShortInfo(id).get("time");
/* 591 */     if ((time != null) && (((Long)time).longValue() > System.currentTimeMillis())) {
/* 592 */       result.put("status", Integer.valueOf(400));
/* 593 */       return result;
/*     */     }
/*     */ 
/* 596 */     Long bn = this.incrementingHelper.getNextSequence("D" + id);
/* 597 */     DBCollection buyCollection = this.dataSource.getCollection("deals");
/*     */ 
/* 600 */     DBObject dbo = new BasicDBObject();
/* 601 */     dbo.put("m", id);
/* 602 */     dbo.put("bn", bn);
/* 603 */     dbo.put("u", AuthenticationUtil.getCurrentUser());
/* 604 */     dbo.put("t", Long.valueOf(System.currentTimeMillis()));
/* 605 */     dbo.put("o", com.ever365.utils.UUID.generateShortUuid());
/*     */ 
/* 607 */     result.putAll(dbo.toMap());
/* 608 */     buyCollection.update(BasicDBObjectBuilder.start("m", id).add("u", AuthenticationUtil.getCurrentUser()).get(), dbo, true, false);
/* 609 */     result.put("status", Integer.valueOf(200));
/*     */ 
/* 611 */     if (bn.longValue() >= getSaleCount(id).intValue()) {
/* 612 */       this.saleCountCache.put(id, Integer.valueOf(-1));
/*     */     }
/*     */ 
/* 615 */     return result;
/*     */   }
/*     */   @RestService(uri="/deals", method="GET")
/*     */   public List<Map> getMyDeals() {
/* 620 */     List mydeals = new ArrayList();
/*     */ 
/* 622 */     DBCollection bookColl = this.dataSource.getCollection("deals");
/* 623 */     DBObject query = new BasicDBObject();
/* 624 */     query.put("u", AuthenticationUtil.getCurrentUser());
/*     */ 
/* 626 */     DBCursor cursor = bookColl.find(query);
/*     */ 
/* 628 */     while (cursor.hasNext()) {
/* 629 */       DBObject o = cursor.next();
/* 630 */       Map t = o.toMap();
/* 631 */       t.put("sale", getSaleShortInfo((Integer)o.get("m")));
/* 632 */       mydeals.add(t);
/*     */     }
/* 634 */     return mydeals;
/*     */   }
/*     */   @RestService(uri="/deals/code", method="GET")
/*     */   public String getDealCode(@RestParam("id") Integer id) {
/* 639 */     DBCollection bookColl = this.dataSource.getCollection("deals");
/* 640 */     DBObject query = new BasicDBObject();
/* 641 */     query.put("u", AuthenticationUtil.getCurrentUser());
/* 642 */     query.put("m", id);
/*     */ 
/* 644 */     DBObject one = bookColl.findOne(query);
/* 645 */     if (one == null) return null;
/* 646 */     return one.get("o").toString();
/*     */   }
/*     */   @RestService(uri="/seller/admin/request/list", method="GET", runAsAdmin=true)
/*     */   public List<Map<String, Object>> getAllRequesting() {
/* 651 */     DBCollection coll = this.dataSource.getCollection("sales");
/*     */ 
/* 653 */     DBCursor cursor = coll.find(new BasicDBObject("online", Boolean.valueOf(false)));
/* 654 */     List requesting = new ArrayList();
/* 655 */     while (cursor.hasNext()) {
/* 656 */       DBObject dbo = cursor.next();
/* 657 */       requesting.add(dbo.toMap());
/*     */     }
/* 659 */     return requesting;
/*     */   }
/*     */   @RestService(uri="/seller/admin/online/list", method="GET", runAsAdmin=true)
/*     */   public List<Map<String, Object>> getAllOnlines() {
/* 664 */     DBCollection coll = this.dataSource.getCollection("sales");
/*     */ 
/* 666 */     DBObject query = new BasicDBObject();
/* 667 */     query.put("online", Boolean.valueOf(true));
/* 668 */     query.put("time", MapUtils.newMap("$gt", Long.valueOf(System.currentTimeMillis())));
/*     */ 
/* 670 */     DBCursor cursor = coll.find(query);
/*     */ 
/* 672 */     List requesting = new ArrayList();
/* 673 */     while (cursor.hasNext()) {
/* 674 */       DBObject dbo = cursor.next();
/* 675 */       requesting.add(dbo.toMap());
/*     */     }
/* 677 */     return requesting;
/*     */   }
/*     */   @RestService(uri="/seller/admin/finished/list", method="GET", runAsAdmin=true)
/*     */   public List<Map<String, Object>> getAllFinished() {
/* 682 */     DBCollection coll = this.dataSource.getCollection("sales");
/*     */ 
/* 684 */     DBObject query = new BasicDBObject();
/* 685 */     query.put("online", Boolean.valueOf(true));
/* 686 */     query.put("time", MapUtils.newMap("$lt", Long.valueOf(System.currentTimeMillis())));
/*     */ 
/* 688 */     DBCursor cursor = coll.find(query);
/*     */ 
/* 690 */     List requesting = new ArrayList();
/* 691 */     while (cursor.hasNext()) {
/* 692 */       DBObject dbo = cursor.next();
/* 693 */       requesting.add(dbo.toMap());
/*     */     }
/* 695 */     return requesting;
/*     */   }
/*     */   @RestService(uri="/seller/admin/request/approve", method="POST", runAsAdmin=true)
/*     */   public void approve(@RestParam("id") String id, @RestParam("on") String on) {
/* 700 */     DBCollection coll = this.dataSource.getCollection("sales");
/* 701 */     DBObject sale = coll.findOne(new BasicDBObject("_id", new ObjectId(id)));
/* 702 */     if ("1".equals(on)) {
/* 703 */       sale.put("online", Boolean.valueOf(true));
/* 704 */       sale.put("seq", this.incrementingHelper.getNextSequence("sales"));
/* 705 */       this.incrementingHelper.initIncreasor("B" + sale.get("seq"));
/*     */     } else {
/* 707 */       sale.put("online", Boolean.valueOf(false));
/* 708 */       sale.removeField("seq");
/*     */     }
/* 710 */     coll.update(new BasicDBObject("_id", new ObjectId(id)), sale);
/*     */   }
/*     */   @RestService(uri="/seller/admin/recommend", method="POST", runAsAdmin=true)
/*     */   public void setRecommond(@RestParam("id") Integer id, @RestParam("set") String set) {
/* 715 */     DBCollection coll = this.dataSource.getCollection("sales");
/* 716 */     DBObject query = new BasicDBObject("seq", id);
/*     */ 
/* 718 */     if (set.equals("1")) {
/* 719 */       DBObject update = new BasicDBObject("$set", MapUtils.newMap("pushed", Boolean.valueOf(true)));
/* 720 */       coll.update(query, update);
/*     */     } else {
/* 722 */       DBObject update = new BasicDBObject("$set", MapUtils.newMap("pushed", null));
/* 723 */       coll.update(query, update);
/*     */     }
/*     */   }
/*     */ 
/*     */   @RestService(uri="/shengo/youhui", method="POST", runAsAdmin=true)
/*     */   public void addYouHui(@RestParam("uri") String uri, @RestParam("price") String price, @RestParam("title") String title, @RestParam("oprice") String oprice, @RestParam("sold") String sold, @RestParam("img") String img)
/*     */   {
/*     */   }
/*     */ 
/*     */   public Collection<Map> getWeibos()
/*     */   {
/* 735 */     return this.repostService.getTodayGifts();
/*     */   }
/*     */   @RestService(uri="/sale/recent", method="GET", authenticated=false)
/*     */   public List<Map> getRecents() {
/* 740 */     DBCollection coll = this.dataSource.getCollection("sales");
/*     */ 
/* 742 */     DBObject query = new BasicDBObject();
/*     */ 
/* 744 */     query.put("time", MapUtils.newMap("$gt", Long.valueOf(System.currentTimeMillis())));
/* 745 */     query.put("online", Boolean.valueOf(true));
/*     */ 
/* 747 */     DBCursor cursor = coll.find(query).sort(new BasicDBObject("time", Integer.valueOf(1))).limit(20);
/*     */ 
/* 749 */     List result = new ArrayList();
/* 750 */     while (cursor.hasNext()) {
/* 751 */       result.add(getSaleShortInfo(Integer.valueOf(((Long)cursor.next().get("seq")).intValue())));
/*     */     }
/* 753 */     return result;
/*     */   }
/*     */ }

/* Location:           D:\360云盘\Desktops\T410\a.jar
 * Qualified Name:     com.ever365.sale.SellerService
 * JD-Core Version:    0.6.0
 */