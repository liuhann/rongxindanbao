/*    */ package com.ever365.open;
/*    */ 
/*    */ import java.util.Iterator;
/*    */ import java.util.List;
/*    */ 
/*    */ public class CollectService
/*    */ {
/*    */   private List<Feeder> feeders;
/*    */ 
/*    */   public void init()
/*    */   {
/*    */     Feeder localFeeder;
/* 11 */     for (Iterator localIterator = this.feeders.iterator(); localIterator.hasNext(); localFeeder = (Feeder)localIterator.next());
/*    */   }
/*    */ }

/* Location:           D:\360云盘\Desktops\T410\a.jar
 * Qualified Name:     com.ever365.open.CollectService
 * JD-Core Version:    0.6.0
 */