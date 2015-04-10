 package com.ever365.rest;
 
 public enum HttpStatus
 {
   CONTINUE(100), 
 
   SWITCHING_PROTOCOLS(101), 
 
   PROCESSING(102), 
 
   OK(200), 
 
   CREATED(201), 
 
   ACCEPTED(202), 
 
   NON_AUTHORITATIVE_INFORMATION(203), 
 
   NO_CONTENT(204), 
 
   RESET_CONTENT(205), 
 
   PARTIAL_CONTENT(206), 
 
   MULTI_STATUS(207), 
 
   ALREADY_REPORTED(208), 
 
   IM_USED(226), 
 
   MULTIPLE_CHOICES(300), 
 
   MOVED_PERMANENTLY(301), 
 
   FOUND(302), 
 
   MOVED_TEMPORARILY(302), 
 
   SEE_OTHER(303), 
 
   NOT_MODIFIED(304), 
 
   USE_PROXY(305), 
 
   TEMPORARY_REDIRECT(307), 
 
   BAD_REQUEST(400), 
 
   UNAUTHORIZED(401), 
 
   PAYMENT_REQUIRED(402), 
 
   FORBIDDEN(403), 
 
   NOT_FOUND(404), 
 
   METHOD_NOT_ALLOWED(405), 
 
   NOT_ACCEPTABLE(406), 
 
   PROXY_AUTHENTICATION_REQUIRED(407), 
 
   REQUEST_TIMEOUT(408), 
 
   CONFLICT(409), 
 
   GONE(410), 
 
   LENGTH_REQUIRED(411), 
 
   PRECONDITION_FAILED(412), 
 
   REQUEST_ENTITY_TOO_LARGE(413), 
 
   REQUEST_URI_TOO_LONG(414), 
 
   UNSUPPORTED_MEDIA_TYPE(415), 
 
   REQUESTED_RANGE_NOT_SATISFIABLE(416), 
 
   EXPECTATION_FAILED(417), 
 
   INSUFFICIENT_SPACE_ON_RESOURCE(419), 
 
   METHOD_FAILURE(420), 
 
   DESTINATION_LOCKED(421), 
 
   UNPROCESSABLE_ENTITY(422), 
 
   LOCKED(423), 
 
   FAILED_DEPENDENCY(424), 
 
   UPGRADE_REQUIRED(426), 
 
   INTERNAL_SERVER_ERROR(500), 
 
   NOT_IMPLEMENTED(501), 
 
   BAD_GATEWAY(502), 
 
   SERVICE_UNAVAILABLE(503), 
 
   GATEWAY_TIMEOUT(504), 
 
   HTTP_VERSION_NOT_SUPPORTED(505), 
 
   VARIANT_ALSO_NEGOTIATES(506), 
 
   INSUFFICIENT_STORAGE(507), 
 
   LOOP_DETECTED(508), 
 
   NOT_EXTENDED(510);
 
   private final int value;
 
   private HttpStatus(int value)
   {
     this.value = value;
   }
 
   public int value()
   {
     return this.value;
   }
 
  
   public String toString()
   {
     return Integer.toString(this.value);
   }
 
   public static HttpStatus valueOf(int statusCode)
   {
     for (HttpStatus status : values()) {
       if (status.value == statusCode) {
         return status;
       }
     }
     throw new IllegalArgumentException("No matching constant for [" + statusCode + "]");
   }
 
   public static enum Series
   {
     INFORMATIONAL(1), 
     SUCCESSFUL(2), 
     REDIRECTION(3), 
     CLIENT_ERROR(4), 
     SERVER_ERROR(5);
 
     private final int value;
 
     private Series(int value) { this.value = value;
     }
 
     public int value()
     {
       return this.value;
     }
 
     private static Series valueOf(HttpStatus status) {
       int seriesCode = status.value() / 100;
       for (Series series : values()) {
         if (series.value == seriesCode) {
           return series;
         }
       }
       throw new IllegalArgumentException("No matching constant for [" + status + "]");
     }
   }
 }

/* Location:           D:\360云盘\Desktops\T410\a.jar
 * Qualified Name:     com.ever365.rest.HttpStatus
 * JD-Core Version:    0.6.0
 */