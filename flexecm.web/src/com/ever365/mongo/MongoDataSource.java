package com.ever365.mongo;

import com.mongodb.DBCollection;

public abstract interface MongoDataSource
{
  public abstract DBCollection getCollection(String paramString);

  public abstract DBCollection getCollection(String paramString1, String paramString2);

  public abstract void clean();
}

/* Location:           D:\360云盘\Desktops\T410\a.jar
 * Qualified Name:     com.ever365.mongo.MongoDataSource
 * JD-Core Version:    0.6.0
 */