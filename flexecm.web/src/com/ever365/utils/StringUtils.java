/*     */ package com.ever365.utils;
/*     */ 
/*     */ import java.io.BufferedReader;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.InputStreamReader;
/*     */ import java.io.PrintStream;
/*     */ import java.text.DecimalFormat;
/*     */ import java.text.ParseException;
/*     */ import java.text.SimpleDateFormat;
/*     */ import java.util.Date;
/*     */ 
/*     */ public class StringUtils
/*     */ {
/*  37 */   public static DecimalFormat df2 = new DecimalFormat("###.00");
/*     */ 
/*  51 */   public static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
/*     */ 
/* 120 */   private static String[] ML = "Jan_Feb_Mar_Apr_May_Jun_Jul_Aug_Sep_Oct_Nov_Dec".split("_");
/*     */ 
/*     */   public static final Long tofileSize(String size)
/*     */   {
/*     */     try
/*     */     {
/*  17 */       if ((size.endsWith("M")) || (size.endsWith("m"))) {
/*  18 */         Float f = Float.valueOf(new Float(size.substring(0, size.length() - 1)).floatValue() * 1024.0F * 1024.0F);
/*  19 */         return Long.valueOf(f.longValue());
/*  20 */       }if ((size.endsWith("K")) || (size.endsWith("k"))) {
/*  21 */         Float f = Float.valueOf(new Float(size.substring(0, size.length() - 1)).floatValue() * 1024.0F);
/*  22 */         return Long.valueOf(f.longValue());
/*  23 */       }if ((size.endsWith("G")) || (size.endsWith("g"))) {
/*  24 */         Float f = Float.valueOf(new Float(size.substring(0, size.length() - 1)).floatValue() * 1024.0F * 1024.0F * 1024.0F);
/*  25 */         return Long.valueOf(f.longValue());
/*  26 */       }if ((size.endsWith("B")) || (size.endsWith("b"))) {
/*  27 */         Float f = new Float(size.substring(0, size.length() - 1));
/*  28 */         return Long.valueOf(f.longValue());
/*     */       }
/*  30 */       return new Long(size);
/*     */     } catch (Exception e) {
/*     */     }
/*  33 */     return Long.valueOf(0L);
/*     */   }
/*     */ 
/*     */   public static final String formateFileSize(Long size)
/*     */   {
/*  39 */     if (size.longValue() > 1073741824L) {
/*  40 */       return df2.format(size.longValue() / 1073741824L) + "G";
/*     */     }
/*  42 */     if (size.longValue() > 1048576L) {
/*  43 */       return df2.format(size.longValue() / 1048576L) + " M";
/*     */     }
/*  45 */     if (size.longValue() > 1024L) {
/*  46 */       return df2.format(size.longValue() / 1024L) + " K";
/*     */     }
/*  48 */     return size + "B";
/*     */   }
/*     */ 
/*     */   public static final String formateDate(Date source)
/*     */   {
/*  53 */     return sdf.format(source);
/*     */   }
/*     */   public static final Date parseDate(String source) {
/*     */     try {
/*  57 */       return sdf.parse(source); } catch (ParseException e) {
/*     */     }
/*  59 */     return new Date();
/*     */   }
/*     */ 
/*     */   public static final String getFileName(String file)
/*     */   {
/*  65 */     if (file == null) return "";
/*  66 */     int dot = file.lastIndexOf(".");
/*  67 */     if (dot > -1) {
/*  68 */       return file.substring(0, dot);
/*     */     }
/*  70 */     return file;
/*     */   }
/*     */ 
/*     */   public static final String middle(String source, String start, String end)
/*     */   {
/*  75 */     int posx = source.indexOf(start);
/*     */ 
/*  77 */     if (posx > -1) {
/*  78 */       posx += start.length();
/*     */     }
/*     */ 
/*  81 */     int posy = source.indexOf(end, posx);
/*     */ 
/*  83 */     if (posy == -1) {
/*  84 */       return source.substring(posx);
/*     */     }
/*  86 */     return source.substring(posx, posy);
/*     */   }
/*     */ 
/*     */   public static String convertStreamToString(InputStream is)
/*     */   {
/*     */     try {
/*  92 */       BufferedReader reader = new BufferedReader(
/*  93 */         new InputStreamReader(is, 
/*  93 */         "UTF-8"));
/*  94 */       StringBuilder sb = new StringBuilder();
/*  95 */       String line = null;
/*  96 */       while ((line = reader.readLine()) != null) {
/*  97 */         sb.append(line + "/n");
/*     */       }
/*  99 */       String str1 = sb.toString();
/*     */       return str1;
/*     */     } catch (IOException e) {
/* 101 */       e.printStackTrace();
/*     */     } finally {
/*     */       try {
/* 104 */         is.close();
/*     */       } catch (IOException e) {
/* 106 */         e.printStackTrace();
/*     */       }
/*     */     }
/* 109 */     return null;
/*     */   }
/*     */ 
/*     */   public static boolean isNumber(String s) {
/* 113 */     return s.matches("[0-9]+");
/*     */   }
/*     */ 
/*     */   public static void main(String[] args) {
/* 117 */     System.out.println(parseWeiboTime("Wed Jun 01 00:50:25 +0800 2011"));
/*     */   }
/*     */ 
/*     */   public static Long parseWeiboTime(String t)
/*     */   {
/* 122 */     String[] parts = t.split(" ");
/* 123 */     Integer year = new Integer(parts[5]);
/*     */ 
/* 125 */     Integer month = indexOf(ML, parts[1]);
/* 126 */     Integer date = new Integer(parts[2]);
/* 127 */     String[] seqs = parts[3].split(":");
/*     */ 
/* 129 */     Integer hrs = new Integer(seqs[0]);
/* 130 */     Integer mins = new Integer(seqs[1]);
/* 131 */     Integer sec = new Integer(seqs[2]);
/*     */ 
/* 133 */     Date d = new Date(year.intValue() - 1900, month.intValue(), date.intValue(), hrs.intValue(), mins.intValue(), sec.intValue());
/* 134 */     return Long.valueOf(d.getTime());
/*     */   }
/*     */ 
/*     */   public static Integer indexOf(String[] ml, String month) {
/* 138 */     for (int i = 0; i < ml.length; i++) {
/* 139 */       if (month.equals(ml[i])) {
/* 140 */         return Integer.valueOf(i);
/*     */       }
/*     */     }
/* 143 */     return Integer.valueOf(-1);
/*     */   }
/*     */ }

/* Location:           D:\360云盘\Desktops\T410\a.jar
 * Qualified Name:     com.ever365.utils.StringUtils
 * JD-Core Version:    0.6.0
 */