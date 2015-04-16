/*     */ package com.ever365.utils;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.PrintStream;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import org.json.JSONException;
/*     */ import org.jsoup.Connection;
/*     */ import org.jsoup.Jsoup;
/*     */ import org.jsoup.nodes.Document;
/*     */ import org.jsoup.nodes.Element;
/*     */ import org.jsoup.select.Elements;
/*     */ 
/*     */ public class HTMLParser
/*     */ {
/*     */   public static final String FROM = "from";
/*     */   public static final String SIZE = "size";
/*     */   public static final String TITLE = "title";
/*     */ 
/*     */   public static void main(String[] args)
/*     */     throws JSONException
/*     */   {
/*     */     try
/*     */     {
/*  22 */       parseArticle("http://card.weibo.com/article/h5/s#cid=1001603798307410458767&vid=&extparam=");
/*     */     } catch (IOException e) {
/*  24 */       e.printStackTrace();
/*     */     }
/*     */   }
/*     */ 
/*     */   public static Map<String, Object> parse115(String url)
/*     */   {
/*  49 */     Map result = new HashMap();
/*     */     try {
/*  51 */       Document doc = Jsoup.connect(url).get();
/*  52 */       Elements p = doc.select("div.spree-info");
/*     */ 
/*  54 */       result.put("title", p.select("span").attr("title"));
/*     */ 
/*  56 */       result.put("size", StringUtils.tofileSize(
/*  57 */         StringUtils.middle(p.select("em").html(), "：", "B")));
/*     */ 
/*  59 */       result.put("from", "115");
/*  60 */       return result;
/*     */     } catch (IOException localIOException) {
/*     */     } catch (Exception localException) {
/*     */     }
/*  64 */     return result;
/*     */   }
/*     */ 
/*     */   public static Map<String, Object> parseBaiduPan(String url) {
/*  68 */     Map result = new HashMap();
/*     */     try {
/*  70 */       Document doc = Jsoup.connect(url).get();
/*  71 */       result.put("title", doc.select("h2.b-fl").attr("title"));
/*  72 */       result.put("size", StringUtils.tofileSize(
/*  73 */         StringUtils.middle(doc.select("#downFileButtom b").last().html(), "(", ")")));
/*     */ 
/*  75 */       result.put("from", "baidu");
/*     */     } catch (IOException localIOException) {
/*     */     } catch (Exception localException) {
/*     */     }
/*  79 */     return result;
/*     */   }
/*     */ 
/*     */   public static Map<String, Object> parseArticle(String url) throws IOException
/*     */   {
/*  84 */     Map result = new HashMap();
/*     */ 
/*  86 */     if (url.indexOf("weixin") > -1) {
/*  87 */       Document doc = Jsoup.connect(url).get();
/*     */ 
/*  89 */       String source = doc.select("#media").html();
/*  90 */       String cover = StringUtils.middle(source, "cover = \"", "\"");
/*     */ 
/*  92 */       System.out.println("src: " + cover);
/*  93 */       String title = doc.select("#activity-name").first().html().trim();
/*     */ 
/*  95 */       System.out.println("title: " + title);
/*  96 */       String user = doc.select("#post-user").html();
/*  97 */       System.out.println("poster: " + user);
/*     */ 
/*  99 */       String date = doc.select("#post-date").html();
/* 100 */       System.out.println("date: " + date);
/* 101 */       Element element = doc.select("#js_content").first();
/*     */ 
/* 103 */       result.put("title", title);
/* 104 */       result.put("user", user);
/* 105 */       result.put("cover", cover);
/* 106 */       result.put("date", date);
/* 107 */       result.put("html", element.html());
/*     */     }
/*     */ 
/* 110 */     if (url.indexOf("weibo.com") > -1) {
/* 111 */       String articleId = StringUtils.middle(url, "cid=", "&");
/*     */ 
/* 113 */       String jsonUrl = "http://card.weibo.com/article/aj/articleshow?cid=" + articleId + "&vid=&extparam=";
/*     */ 
/* 115 */       System.out.println("文章JSON地址 " + jsonUrl);
/* 116 */       Map m = WebUtils.jsonObjectToMap(WebUtils.doGet(jsonUrl));
/*     */ 
/* 118 */       if ("ok".equalsIgnoreCase((String)m.get("msg"))) {
/* 119 */         String htmlContent = (String)((Map)m.get("data")).get("article");
/*     */ 
/* 121 */         Document doc = Jsoup.parse(htmlContent);
/* 122 */         String title = doc.select("h1.title").html();
/* 123 */         String date = doc.select("span.time").html();
/* 124 */         String user = doc.select("span.from a").html();
/*     */ 
/* 126 */         System.out.println("title : " + title + "   date : " + date + "  user: " + user);
/*     */ 
/* 128 */         Elements wbaContent = doc.select("div.WBA_content");
/* 129 */         Element element = wbaContent.first();
/*     */ 
/* 131 */         Elements span = element.child(0).select("span");
/* 132 */         String cover = span.attr("node-data");
/* 133 */         String html = element.html();
/*     */ 
/* 135 */         result.put("title", title);
/* 136 */         result.put("user", user);
/* 137 */         result.put("cover", cover);
/* 138 */         result.put("date", date);
/* 139 */         result.put("html", html);
/*     */       }
/*     */     }
/*     */ 
/* 143 */     return result;
/*     */   }
/*     */ }

/* Location:           D:\360云盘\Desktops\T410\a.jar
 * Qualified Name:     com.ever365.utils.HTMLParser
 * JD-Core Version:    0.6.0
 */