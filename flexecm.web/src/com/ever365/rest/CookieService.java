/*     */ package com.ever365.rest;
/*     */ 
/*     */ import com.ever365.mongo.MongoDataSource;
/*     */ import com.mongodb.BasicDBObject;
/*     */ import com.mongodb.BasicDBObjectBuilder;
/*     */ import com.mongodb.DBCollection;
/*     */ import com.mongodb.DBObject;
/*     */ import java.util.Date;
/*     */ import java.util.UUID;
/*     */ import javax.servlet.http.Cookie;
/*     */ import javax.servlet.http.HttpServletRequest;
/*     */ import javax.servlet.http.HttpServletResponse;
/*     */ 
/*     */ public class CookieService
/*     */ {
/*     */   private MongoDataSource dataSource;
/*     */   public static final String ARG_TICKET = "365ticket";
/*     */ 
/*     */   public void setDataSource(MongoDataSource dataSource)
/*     */   {
/*  22 */     this.dataSource = dataSource;
/*     */   }
/*     */ 
/*     */   public Cookie setCookieTicket(HttpServletResponse resp) {
/*  26 */     Cookie cookie = new Cookie("365ticket", UUID.randomUUID().toString());
/*  27 */     cookie.setMaxAge(51840000);
/*  28 */     cookie.setPath("/");
/*  29 */     resp.addCookie(cookie);
/*  30 */     return cookie;
/*     */   }
/*     */ 
/*     */   public String getCookieTicket(HttpServletRequest httpReq)
/*     */   {
/*  35 */     Cookie[] cookies = httpReq.getCookies();
/*  36 */     if (cookies != null) {
/*  37 */       for (Cookie cookie : cookies) {
/*  38 */         if (cookie.getName().equals("365ticket")) {
/*  39 */           return cookie.getValue();
/*     */         }
/*     */       }
/*     */     }
/*  43 */     return httpReq.getParameter("365ticket");
/*     */   }
/*     */ 
/*     */   public void removeCookieTicket(HttpServletRequest request, HttpServletResponse response) {
/*  47 */     String ticket = getCookieTicket(request);
/*  48 */     if (ticket != null) {
/*  49 */       DBCollection cookiesCol = this.dataSource.getCollection("cookies");
/*  50 */       cookiesCol.remove(new BasicDBObject("ticket", ticket));
/*     */     }
/*     */ 
/*  53 */     Cookie[] cookies = request.getCookies();
/*  54 */     if (cookies != null)
/*  55 */       for (Cookie cookie : cookies) {
/*  56 */         cookie.setValue("");
/*  57 */         cookie.setMaxAge(0);
/*  58 */         cookie.setPath("/");
/*  59 */         response.addCookie(cookie);
/*     */       }
/*     */   }
/*     */ 
/*     */   public String getCurrentUser(HttpServletRequest request)
/*     */   {
/*  65 */     String ticket = getCookieTicket(request);
/*  66 */     if (ticket == null) return null;
/*     */ 
/*  68 */     DBCollection cookiesCol = this.dataSource.getCollection("cookies");
/*  69 */     DBObject ticDoc = cookiesCol.findOne(new BasicDBObject("ticket", ticket));
/*  70 */     if (ticDoc == null) {
/*  71 */       return null;
/*     */     }
/*  73 */     return (String)ticDoc.get("user");
/*     */   }
/*     */ 
/*     */   public String getCurrentAccessToken(HttpServletRequest request)
/*     */   {
/*  79 */     String ticket = getCookieTicket(request);
/*  80 */     if (ticket == null) return null;
/*     */ 
/*  82 */     DBCollection cookiesCol = this.dataSource.getCollection("cookies");
/*  83 */     DBObject ticDoc = cookiesCol.findOne(new BasicDBObject("ticket", ticket));
/*  84 */     if (ticDoc == null) {
/*  85 */       return null;
/*     */     }
/*  87 */     return (String)ticDoc.get("at");
/*     */   }
/*     */ 
/*     */   public void bindUserCookie(HttpServletRequest request, HttpServletResponse response, String username)
/*     */   {
/* 100 */     String ticket = getCookieTicket(request);
/* 101 */     DBCollection cookiesCol = this.dataSource.getCollection("cookies");
/* 102 */     if (ticket != null) {
/* 103 */       DBObject ticDoc = cookiesCol.findOne(new BasicDBObject("ticket", ticket));
/* 104 */       if (ticDoc == null) {
/* 105 */         cookiesCol.insert(new DBObject[] { BasicDBObjectBuilder.start()
/* 106 */           .add("user", username).add("ticket", ticket)
/* 107 */           .add("remote", request.getRemoteAddr())
/* 108 */           .add("created", new Date()).get() });
/*     */       } else {
/* 110 */         ticDoc.put("user", username);
/* 111 */         cookiesCol.update(new BasicDBObject("ticket", ticket), ticDoc);
/*     */       }
/*     */     } else {
/* 114 */       Cookie newCookie = setCookieTicket(response);
/* 115 */       ticket = newCookie.getValue();
/* 116 */       cookiesCol.insert(new DBObject[] { BasicDBObjectBuilder.start()
/* 117 */         .add("user", username).add("ticket", ticket)
/* 118 */         .add("remote", WebContext.getRemoteAddr(request))
/* 119 */         .add("agent", request.getHeader("User-Agent"))
/* 120 */         .add("created", new Date()).get() });
/*     */     }
/*     */   }
/*     */ }

/* Location:           D:\360云盘\Desktops\T410\a.jar
 * Qualified Name:     com.ever365.rest.CookieService
 * JD-Core Version:    0.6.0
 */