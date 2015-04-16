/*    */ package com.ever365.mongo;
/*    */ 
/*    */ import com.ever365.utils.MapUtils;
/*    */ import com.mongodb.BasicDBObject;
/*    */ import com.mongodb.DBCollection;
/*    */ import com.mongodb.DBObject;
/*    */ import java.util.Map;
/*    */ 
/*    */ public class AutoIncrementingHelper
/*    */ {
/*    */   private MongoDataSource dataSource;
/* 16 */   private Map<String, Integer> incMap = MapUtils.newMap("seq", Integer.valueOf(1));
/*    */ 
/*    */   public void setDataSource(MongoDataSource dataSource) {
/* 19 */     this.dataSource = dataSource;
/*    */   }
/*    */ 
/*    */   public void initIncreasor(String name) {
/*    */     try {
/* 24 */       if (this.dataSource.getCollection("counters").findOne(new BasicDBObject("_id", name)) == null) {
/* 25 */         DBObject dbo = new BasicDBObject();
/* 26 */         dbo.put("_id", name);
/* 27 */         dbo.put("seq", Long.valueOf(2L));
/* 28 */         this.dataSource.getCollection("counters").insert(new DBObject[] { dbo });
/*    */       }
/*    */     }
/*    */     catch (Exception localException) {
/*    */     }
/*    */   }
/*    */ 
/*    */   public Long getNextSequence(String name) {
/*    */     try {
/* 37 */       BasicDBObject query = new BasicDBObject("_id", name);
/* 38 */       DBObject update = new BasicDBObject();
/* 39 */       update.put("$inc", this.incMap);
/* 40 */       DBObject dbo = this.dataSource.getCollection("counters").findAndModify(query, update);
/* 41 */       return (Long)dbo.get("seq");
/*    */     } catch (Exception e) {
/* 43 */       initIncreasor(name);
/* 44 */     }return Long.valueOf(1L);
/*    */   }
/*    */ 
/*    */   public Long getCurrentSequence(String name)
/*    */   {
/* 49 */     BasicDBObject query = new BasicDBObject("_id", name);
/*    */ 
/* 51 */     DBObject q = this.dataSource.getCollection("counters").findOne(query);
/* 52 */     if (q == null) {
/* 53 */       initIncreasor(name);
/* 54 */       return Long.valueOf(1L);
/*    */     }
/* 56 */     return (Long)q.get("seq");
/*    */   }
/*    */ }

/* Location:           D:\360云盘\Desktops\T410\a.jar
 * Qualified Name:     com.ever365.mongo.AutoIncrementingHelper
 * JD-Core Version:    0.6.0
 */