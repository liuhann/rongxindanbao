 package com.ever365.mongo;
 
 import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import com.ever365.rest.HttpStatus;
import com.ever365.rest.HttpStatusException;
import com.mongodb.BasicDBObject;
import com.mongodb.CommandResult;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
 
 public class LocalMongoDataSource
   implements MongoDataSource
 {
   public static final String NULL = "";
   Logger logger = Logger.getLogger(LocalMongoDataSource.class.getName());
 
   private Map<String, DB> dbconnections = new HashMap();
   private String db;
   private String host;
   private String port;
   private String username;
   private String password;
   private int connectionPerhost = 20;
   private long checkInteval = 60000L;
 
   public void setConnectionPerhost(int connectionPerhost) {
     this.connectionPerhost = connectionPerhost;
   }
 
   public void setHost(String host) {
     this.host = host;
   }
 
   public void setPort(String port) {
     this.port = port;
   }
 
   public void setUsername(String username) {
     this.username = username;
   }
 
   public void setPassword(String password) {
     this.password = password;
   }
 
   public void setDb(String db) {
     this.db = db;
   }
 
   public DBCollection getCollection(String name)
   {
     if (this.db == null) {
       this.db = "ever365db";
     }
     return getCollection(this.db, name);
   }
 
   public DBCollection getCollection(String dbName, String collName)
   {
     if (this.dbconnections.get(dbName) == null) {
       synchronized (this) {
         if (this.dbconnections.get(dbName) == null) {
           try {
             if (this.host == null) {
               this.host = "127.0.0.1";
             }
             if (this.port == null) {
               this.port = "27017";
             }
             String serverName = this.host + ":" + this.port;
 
             MongoClientOptions mo = new MongoClientOptions.Builder().connectionsPerHost(this.connectionPerhost).cursorFinalizerEnabled(false).build();
             if ((this.username != null) && (this.password != null) && (!this.username.equals("")) && (!this.password.equals(""))) {
               MongoCredential cred = MongoCredential.createMongoCRCredential(this.username, dbName, this.password.toCharArray());
               MongoClient mongoClient = new MongoClient(new ServerAddress(serverName), Arrays.asList(new MongoCredential[] { cred }), mo);
               DB mongoDB = mongoClient.getDB(dbName);
               mongoDB.authenticate(this.username, this.password.toCharArray());
               this.logger.info(System.currentTimeMillis() + "   init connection ok..");
               this.dbconnections.put(dbName, mongoDB);
             } else {
               MongoClient mongoClient = new MongoClient(serverName, mo);
               mongoClient.getDB(dbName).command("ping");
               this.dbconnections.put(dbName, mongoClient.getDB(dbName));
             }
           } catch (Exception e) {
             this.logger.info(System.currentTimeMillis() + "  init connection fail .." + e.getMessage());
           }
         }
       }
     }
     try {
    	 DB pools = (DB)this.dbconnections.get(dbName);
    	 if (pools == null) {
    		 return getCollection(dbName);
    	 }
    	 CommandResult r = pools.command("ping");
       if (!r.ok()) throw new HttpStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
       return pools.getCollection(collName);
     } catch (Exception e) {
    	 this.logger.info(System.currentTimeMillis() + "  ping fail, goto re-init" + e.getMessage());
    	 this.dbconnections.remove(dbName);
     }
     return getCollection(dbName);
   }
   
   public Map<String, Object> filterCollectoin(String collection , Map<String, Object> filters, Map<String, Object> sort,
			Integer skip, Integer limit) {

	   if (limit==0) {
		   limit = 50;
	   }
	   
	   Map<String, Object> result = new HashMap<String, Object>();
		
	   DBObject query = new BasicDBObject();
		
	   if (filters!=null) {
		   query.putAll(filters);
	   }

	   DBCursor cursor = getCollection(collection).find(query).skip(skip).limit(limit);
		
	   if (sort!=null) {
		   cursor.sort(new BasicDBObject(sort));
	   } else {
		   cursor.sort(new BasicDBObject("_id", -1));
	   }
		
	   result.put("size", cursor.count());
	   cursor.skip(skip).limit(limit);
	   List<Map> list = new ArrayList<Map>();
	   while(cursor.hasNext()) {
		   Map m = cursor.next().toMap();
		   list.add(m);
	   }
	   result.put("start", skip);
	   result.put("per", limit);
	   result.put("list", list);
	   return result;
	}

   
   public void clean()
   {
     Set<String> allcollections = ((DB)this.dbconnections.get(this.db)).getCollectionNames();
     for (String collection : allcollections)
       if (!collection.startsWith("system."))
         getCollection(collection).drop();
   }
 }
