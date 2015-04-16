package com.ever365.mongo;

import java.util.Map;

import com.mongodb.DBCollection;

public abstract interface MongoDataSource
{
  public abstract DBCollection getCollection(String collection);

  public abstract DBCollection getCollection(String dbname, String collection);

  public abstract void clean();
  
  public Map<String, Object> filterCollectoin(String collection , Map<String, Object> filters, Map<String, Object> sort,
			Integer skip, Integer limit);
}
