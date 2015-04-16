/*   */ package com.ever365.rest;
/*   */ 
/*   */ public class TestService
/*   */ {
/*   */   @RestService(uri="/hello", method="GET")
/*   */   public String Hello()
/*   */   {
/* 7 */     return "hello";
/*   */   }
/*   */ }

/* Location:           D:\360云盘\Desktops\T410\a.jar
 * Qualified Name:     com.ever365.rest.TestService
 * JD-Core Version:    0.6.0
 */