 package com.ever365.rest;
 
 import java.io.File;
import java.io.InputStream;
 
 public class StreamObject
 {
   private long lastModified;
   private String mimeType;
   private String fileName;
   private InputStream inputStream;
   private long size;
   
   private File raw;
 
   public File getRaw() {
	   return raw;
   }
   
   public void setRaw(File raw) {
	   this.raw = raw;
   }
	public long getSize()
   {
/* 13 */     return this.size;
   }
   public void setSize(long size) {
/* 16 */     this.size = size;
   }
   public long getLastModified() {
/* 19 */     return this.lastModified;
   }
   public void setLastModified(long lastModified) {
/* 22 */     this.lastModified = lastModified;
   }
   public String getMimeType() {
/* 25 */     return this.mimeType;
   }
   public void setMimeType(String mimeType) {
/* 28 */     this.mimeType = mimeType;
   }
   public String getFileName() {
/* 31 */     return this.fileName;
   }
   public void setFileName(String fileName) {
/* 34 */     this.fileName = fileName;
   }
   public InputStream getInputStream() {
/* 37 */     return this.inputStream;
   }
   public void setInputStream(InputStream inputStream) {
/* 40 */     this.inputStream = inputStream;
   }
 }

/* Location:           D:\360云盘\Desktops\T410\a.jar
 * Qualified Name:     com.ever365.rest.StreamObject
 * JD-Core Version:    0.6.0
 */