/*    */ package com.ever365.rest;
/*    */ 
/*    */ public class HttpStatusException extends RuntimeException
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   private String description;
/*    */   private HttpStatus status;
/*    */   public static final int BAD_REQUEST = 400;
/*    */   public static final int NOT_FOUND = 404;
/*    */   public static final int CONFLICT = 409;
/*    */ 
/*    */   public HttpStatusException(HttpStatus status)
/*    */   {
/* 11 */     this.status = status;
/*    */   }
/*    */ 
/*    */   public HttpStatusException(HttpStatus hs, Exception e)
/*    */   {
/* 21 */     this.status = hs;
/* 22 */     if ((e instanceof HttpStatusException)) {
/* 23 */       this.description = ((HttpStatusException)e).getDescription();
/*    */     }
/*    */     else {
/* 26 */       StackTraceElement[] trances = e.getStackTrace();
/* 27 */       StringBuffer sb = new StringBuffer();
/*    */ 
/* 29 */       for (int i = 0; i < trances.length; i++) {
/* 30 */         sb.append(trances[i].getClassName() + " " + trances[i].getMethodName() + "   " + trances[i].getLineNumber());
/* 31 */         sb.append("\n\r");
/*    */       }
/* 33 */       this.description = sb.toString();
/*    */     }
/*    */   }
/*    */ 
/*    */   public int getCode() {
/* 38 */     return this.status.value();
/*    */   }
/*    */ 
/*    */   public String getDescription() {
/* 42 */     return this.description;
/*    */   }
/*    */ 
/*    */   public String getName() {
/* 46 */     return null;
/*    */   }
/*    */ 
/*    */   public String getUri()
/*    */   {
/* 51 */     return null;
/*    */   }
/*    */ }

/* Location:           D:\360云盘\Desktops\T410\a.jar
 * Qualified Name:     com.ever365.rest.HttpStatusException
 * JD-Core Version:    0.6.0
 */