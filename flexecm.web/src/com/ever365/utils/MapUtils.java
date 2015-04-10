/*    */ package com.ever365.utils;
/*    */ 
/*    */ import java.util.HashMap;
/*    */ import java.util.Map;
/*    */ 
/*    */ public class MapUtils
/*    */ {
/*    */   public static <V, K> Map<K, V> newMap(K k, V v)
/*    */   {
/*  9 */     Map m = new HashMap(1);
/* 10 */     m.put(k, v);
/* 11 */     return m;
/*    */   }
/*    */ 
/*    */   public static <K, X> String get(Map<K, X> map, K k)
/*    */   {
/* 16 */     if (map.get(k) == null) {
/* 17 */       return null;
/*    */     }
/* 19 */     Object v = map.get(k);
/* 20 */     return v.toString();
/*    */   }
/*    */ 
/*    */   public static void putToMap(Map<String, Object> map, String outerKey, String innerKey, Object value)
/*    */   {
/* 26 */     if (map.get(outerKey) == null) {
/* 27 */       map.put(outerKey, newMap(innerKey, value));
/*    */     } else {
/* 29 */       Map innerMap = (Map)map.get(outerKey);
/* 30 */       innerMap.put(innerKey, value);
/*    */     }
/*    */   }
/*    */ }

/* Location:           D:\360云盘\Desktops\T410\a.jar
 * Qualified Name:     com.ever365.utils.MapUtils
 * JD-Core Version:    0.6.0
 */