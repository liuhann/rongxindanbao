 package com.ever365.utils;
 
 import java.io.BufferedReader;
 import java.io.IOException;
 import java.io.InputStream;
 import java.io.InputStreamReader;
 import java.io.PrintStream;
 import java.security.MessageDigest;
 import java.security.NoSuchAlgorithmException;
 import java.text.DecimalFormat;
 import java.text.ParseException;
 import java.text.SimpleDateFormat;
 import java.util.Date;
 import java.util.Random;

 public class StringUtils
 {
   public static DecimalFormat df2 = new DecimalFormat("###.00");
 
   public static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
 
   private static String[] ML = "Jan_Feb_Mar_Apr_May_Jun_Jul_Aug_Sep_Oct_Nov_Dec".split("_");
 
   public static final Long tofileSize(String size)
   {
     try
     {
       if ((size.endsWith("M")) || (size.endsWith("m"))) {
         Float f = Float.valueOf(new Float(size.substring(0, size.length() - 1)).floatValue() * 1024.0F * 1024.0F);
         return Long.valueOf(f.longValue());
       }if ((size.endsWith("K")) || (size.endsWith("k"))) {
         Float f = Float.valueOf(new Float(size.substring(0, size.length() - 1)).floatValue() * 1024.0F);
         return Long.valueOf(f.longValue());
       }if ((size.endsWith("G")) || (size.endsWith("g"))) {
         Float f = Float.valueOf(new Float(size.substring(0, size.length() - 1)).floatValue() * 1024.0F * 1024.0F * 1024.0F);
         return Long.valueOf(f.longValue());
       }if ((size.endsWith("B")) || (size.endsWith("b"))) {
         Float f = new Float(size.substring(0, size.length() - 1));
         return Long.valueOf(f.longValue());
       }
       return new Long(size);
     } catch (Exception e) {
     }
     return Long.valueOf(0L);
   }
 
   public static final String formateFileSize(Long size)
   {
     if (size.longValue() > 1073741824L) {
       return df2.format(size.longValue() / 1073741824L) + "G";
     }
     if (size.longValue() > 1048576L) {
       return df2.format(size.longValue() / 1048576L) + " M";
     }
     if (size.longValue() > 1024L) {
       return df2.format(size.longValue() / 1024L) + " K";
     }
     return size + "B";
   }
 
   public static final String formateDate(Date source)
   {
     return sdf.format(source);
   }
   public static final Date parseDate(String source) {
     try {
       return sdf.parse(source); } catch (ParseException e) {
     }
     return new Date();
   }
 
   public static final String getFileName(String file)
   {
     if (file == null) return "";
     int dot = file.lastIndexOf(".");
     if (dot > -1) {
       return file.substring(0, dot);
     }
     return file;
   }
 
   public static final String middle(String source, String start, String end)
   {
     int posx = source.indexOf(start);
 
     if (posx > -1) {
       posx += start.length();
     }
 
     int posy = source.indexOf(end, posx);
 
     if (posy == -1) {
       return source.substring(posx);
     }
     return source.substring(posx, posy);
   }
 
   public static String convertStreamToString(InputStream is)
   {
     try {
       BufferedReader reader = new BufferedReader(
         new InputStreamReader(is, 
         "UTF-8"));
       StringBuilder sb = new StringBuilder();
       String line = null;
       while ((line = reader.readLine()) != null) {
         sb.append(line + "/n");
       }
       String str1 = sb.toString();
       return str1;
     } catch (IOException e) {
       e.printStackTrace();
     } finally {
       try {
         is.close();
       } catch (IOException e) {
         e.printStackTrace();
       }
     }
     return null;
   }
 
   public static boolean isNumber(String s) {
     return s.matches("[0-9]+");
   }
 
   public static void main(String[] args) {
     System.out.println(parseWeiboTime("Wed Jun 01 00:50:25 +0800 2011"));
   }
 
   public static Long parseWeiboTime(String t)
   {
     String[] parts = t.split(" ");
     Integer year = new Integer(parts[5]);
 
     Integer month = indexOf(ML, parts[1]);
     Integer date = new Integer(parts[2]);
     String[] seqs = parts[3].split(":");
 
     Integer hrs = new Integer(seqs[0]);
     Integer mins = new Integer(seqs[1]);
     Integer sec = new Integer(seqs[2]);
 
     Date d = new Date(year.intValue() - 1900, month.intValue(), date.intValue(), hrs.intValue(), mins.intValue(), sec.intValue());
     return Long.valueOf(d.getTime());
   }
 
   public static Integer indexOf(String[] ml, String month) {
     for (int i = 0; i < ml.length; i++) {
       if (month.equals(ml[i])) {
         return Integer.valueOf(i);
       }
     }
     return Integer.valueOf(-1);
   }

   public static String sha1(String input) throws NoSuchAlgorithmException {
     MessageDigest mDigest = MessageDigest.getInstance("SHA1");
     byte[] result = mDigest.digest(input.getBytes());
     StringBuffer sb = new StringBuffer();
     for (int i = 0; i < result.length; i++) {
       sb.append(Integer.toString((result[i] & 0xff) + 0x100, 16).substring(1));
     }
     return sb.toString();
   }

     private static String randString = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";//随机产生的字符串
     public static String getRandString(Integer length) {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for(int i=0; i<length; i++) {
            sb.append(randString.charAt(random.nextInt(randString.length())));
        }
         return sb.toString();
     }
 }

