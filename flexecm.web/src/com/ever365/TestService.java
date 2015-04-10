/*     */ package com.ever365;
/*     */ 
/*     */ import java.io.BufferedReader;
/*     */ import java.io.BufferedWriter;
/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.FileWriter;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.InputStreamReader;
/*     */ import java.io.PrintStream;
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.net.URLEncoder;
/*     */ import java.text.ParseException;
/*     */ import java.text.SimpleDateFormat;
/*     */ import java.util.Date;
/*     */ import java.util.HashSet;
/*     */ import org.apache.commons.httpclient.HttpClient;
/*     */ import org.apache.commons.httpclient.HttpException;
/*     */ import org.apache.commons.httpclient.SimpleHttpConnectionManager;
/*     */ import org.apache.commons.httpclient.methods.GetMethod;
/*     */ import org.apache.commons.httpclient.params.HttpClientParams;
/*     */ 
/*     */ public class TestService
/*     */ {
/*  27 */   static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
/*     */ 
/*  83 */   public static HashSet<String> domains = new HashSet();
/*  84 */   public static HashSet<String> users = new HashSet();
/*  85 */   public static HashSet<String> urls = new HashSet();
/*  86 */   public static HashSet<String> files = new HashSet();
/*     */   private static final int BUFFER = 1024;
/*     */ 
/*     */   public static void main(String[] args)
/*     */     throws ParseException, IOException
/*     */   {
/*  31 */     Date from = dateFormat.parse("20151201");
/*  32 */     Date to = dateFormat.parse("20150105");
/*     */ 
/*  34 */     FileWriter fw = new FileWriter("d:/logs/stat.txt");
/*     */ 
/*  36 */     while (from.getTime() <= to.getTime())
/*     */     {
/*  38 */       String name = "catalina." + dateFormat.format(from) + ".log";
/*     */ 
/*  40 */       String clened = "d:/logs/cleaned/" + name;
/*     */ 
/*  42 */       File fc = new File(clened);
/*  43 */       if (fc.exists()) {
/*  44 */         fc.delete();
/*     */       }
/*     */ 
/*  47 */       convertTxtFile("d:/logs/27/" + name, clened);
/*  48 */       convertTxtFile("d:/logs/28/" + name, clened);
/*     */ 
/*  50 */       fw.append(dateFormat.format(from) + "," + domains.size() + "," + users.size());
/*     */ 
/*  52 */       from = new Date(from.getTime() + 86400000L);
/*     */ 
/*  54 */       fw.append("\n");
/*     */ 
/*  63 */       domains.clear();
/*  64 */       urls.clear();
/*  65 */       users.clear();
/*  66 */       files.clear();
/*     */     }
/*     */ 
/*  70 */     fw.close();
/*     */   }
/*     */ 
/*     */   public static void convertTxtFile(String filePath, String outFile)
/*     */   {
/*     */     try
/*     */     {
/*  90 */       String encoding = "UTF-8";
/*  91 */       File file = new File(filePath);
/*     */ 
/*  93 */       File out = new File(outFile);
/*     */ 
/*  95 */       if (!out.exists()) {
/*  96 */         out.createNewFile();
/*     */       }
/*     */ 
/*  99 */       BufferedWriter bw = new BufferedWriter(new FileWriter(out));
/*     */ 
/* 101 */       if ((file.isFile()) && (file.exists())) {
/* 102 */         InputStreamReader read = new InputStreamReader(
/* 103 */           new FileInputStream(file), encoding);
/* 104 */         BufferedReader bufferedReader = new BufferedReader(read);
/* 105 */         String lineTxt = null;
/* 106 */         while ((lineTxt = bufferedReader.readLine()) != null)
/*     */         {
/* 108 */           if (lineTxt.contains("INFO")) {
/* 109 */             bw.append(lineTxt);
/* 110 */             extracted(lineTxt);
/* 111 */             bw.append("\n");
/*     */           }
/*     */         }
/* 114 */         read.close();
/* 115 */         bw.close();
/*     */       }
/*     */     } catch (Exception e) {
/* 118 */       System.out.println("读取文件内容出错");
/* 119 */       e.printStackTrace();
/*     */     }
/*     */   }
/*     */ 
/*     */   public static String extracted(String line) {
/*     */     try {
/* 125 */       String user = middle(line, "User:", " ");
/* 126 */       String url = "";
/* 127 */       if (line.indexOf("GET") > -1)
/* 128 */         url = middle(line, "GET  ", "  ");
/*     */       else {
/* 130 */         url = middle(line, "POST ", "  ");
/*     */       }
/*     */ 
/* 133 */       String domain = "";
/* 134 */       if (line.indexOf("domainName=") > -1)
/* 135 */         domain = middle(line, "domainName=", ",");
/* 136 */       else if (line.indexOf("\"domainName\":") > -1)
/* 137 */         domain = middle(line, "\"domainName\":\"", "\"");
/*     */       else {
/* 139 */         domain = middle(user, "@", null);
/*     */       }
/* 141 */       domains.add(domain);
/* 142 */       urls.add(url);
/* 143 */       users.add(user);
/*     */ 
/* 145 */       if (line.indexOf("file/create") > -1) {
/* 146 */         String filePath = middle(line, "path=", "}") + "/" + middle(line, "{name=", ",");
/* 147 */         files.add(filePath);
/*     */       }
/* 149 */       if (line.indexOf("file/createLargeFile") > -1) {
/* 150 */         String filePath = middle(line, "path=", ",") + "/" + middle(line, "name=", ",");
/* 151 */         files.add(filePath);
/*     */       }
/* 153 */       return user + "," + url + "," + domain; } catch (Exception e) {
/*     */     }
/* 155 */     return "";
/*     */   }
/*     */ 
/*     */   public static final String middle(String source, String start, String end)
/*     */   {
/* 160 */     int posx = 0;
/* 161 */     if (start != null) {
/* 162 */       posx = source.indexOf(start);
/*     */     }
/*     */ 
/* 165 */     if (posx > -1) {
/* 166 */       posx += start.length();
/*     */     }
/* 168 */     int posy = source.length();
/* 169 */     if (end != null) {
/* 170 */       posy = source.indexOf(end, posx);
/*     */     }
/* 172 */     if (posy == -1) {
/* 173 */       posy = source.length();
/*     */     }
/* 175 */     return source.substring(posx, posy);
/*     */   }
/*     */ 
/*     */   public static void downLoadFile(String file, String date) throws UnsupportedEncodingException
/*     */   {
/* 180 */     if (file.startsWith("/admin/org")) return;
/*     */ 
/* 182 */     String user = middle(file, "/", "/");
/* 183 */     String domain = middle(user, "@", null);
/*     */ 
/* 185 */     HttpClient hc = new HttpClient(new HttpClientParams(), new SimpleHttpConnectionManager(true));
/* 186 */     String url = "http://58.221.79.28/space/rest/server/file/directdownload?path=" + 
/* 187 */       URLEncoder.encode(file, "UTF-8") + "&domainName=" + domain + "&userId=" + user;
/*     */ 
/* 189 */     GetMethod gm = new GetMethod(url);
/*     */     try {
/* 191 */       hc.executeMethod(gm);
/*     */ 
/* 193 */       File target = new File("d:/logs/files/", date);
/*     */ 
/* 195 */       if (!target.exists()) {
/* 196 */         target.mkdir();
/*     */       }
/*     */ 
/* 199 */       String fileName = file.substring(file.lastIndexOf("/") + 1);
/*     */ 
/* 201 */       System.out.println("downLoad File d:/logs/files/" + date + "/" + fileName);
/*     */ 
/* 203 */       InputStream in = gm.getResponseBodyAsStream();
/*     */ 
/* 205 */       FileOutputStream out = new FileOutputStream(new File("d:/logs/files/" + date + "/" + fileName));
/*     */ 
/* 207 */       byte[] b = new byte[1024];
/* 208 */       int len = 0;
/* 209 */       while ((len = in.read(b)) != -1) {
/* 210 */         out.write(b, 0, len);
/*     */       }
/* 212 */       in.close();
/* 213 */       out.close();
/*     */     } catch (UnsupportedEncodingException e) {
/* 215 */       e.printStackTrace();
/*     */     } catch (HttpException e) {
/* 217 */       e.printStackTrace();
/*     */     } catch (IOException e) {
/* 219 */       e.printStackTrace();
/*     */     } finally {
/* 221 */       gm.releaseConnection();
/*     */     }
/*     */   }
/*     */ }

/* Location:           D:\360云盘\Desktops\T410\a.jar
 * Qualified Name:     com.ever365.TestService
 * JD-Core Version:    0.6.0
 */