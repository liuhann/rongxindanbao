 package com.ever365.dido;
 
 import com.ever365.mongo.MongoDataSource;
 import com.ever365.rest.AuthenticationUtil;
 import com.ever365.rest.HttpStatus;
 import com.ever365.rest.HttpStatusException;
 import com.ever365.rest.RestParam;
 import com.ever365.rest.RestResult;
 import com.ever365.rest.RestService;
 import com.ever365.rest.WebContext;
 import com.ever365.sale.CollectRepostService;
 import com.ever365.utils.MapUtils;
 import com.ever365.utils.WebUtils;
 import com.mongodb.BasicDBObject;
 import com.mongodb.BasicDBObjectBuilder;
 import com.mongodb.DBCollection;
 import com.mongodb.DBCursor;
 import com.mongodb.DBObject;
 import java.util.ArrayList;
 import java.util.Date;
 import java.util.HashMap;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 import java.util.logging.Logger;
 import org.json.JSONArray;
 import org.json.JSONException;
 
 public class DidoService
 {
   private static final String COLL_TAGS = "tags";
   private static final String COLL_TODOS = "todos";
   private static final String COLL_TIMES = "times";
   private static final String COLL_USERS = "users";
   private MongoDataSource dataSource;
   Logger logger = Logger.getLogger(CollectRepostService.class.getName());
 
   public void setDataSource(MongoDataSource dataSource) {
     this.dataSource = dataSource;
   }
   @RestService(uri="/dido/current", webcontext=true)
   public Map getCurentDido() {
     DBObject u = this.dataSource.getCollection("users").findOne(new BasicDBObject("_id", AuthenticationUtil.getCurrentUser()));
     if (u == null) {
       throw new HttpStatusException(HttpStatus.UNAUTHORIZED);
     }
     return u.toMap();
   }
   @RestService(uri="/dido/list", authenticated=true, method="GET", webcontext=true)
   public List<Map> getList(@RestParam("coll") String coll) {
     DBCursor cursor = this.dataSource.getCollection(coll).find(new BasicDBObject("u", AuthenticationUtil.getCurrentUser()));
     List result = new ArrayList();
     while (cursor.hasNext()) {
       Map m = cursor.next().toMap();
       m.remove("_id");
       m.remove("___s");
       m.remove("___id");
       result.add(m);
     }
     return result;
   }
 
   @RestService(uri="/dido/backup", authenticated=true, method="POST", webcontext=true)
   public Long backUp(@RestParam("time") String time, @RestParam("todo") String todo, @RestParam("type") String type)
   {
     backup(time, "times");
 
     backup(todo, "todos");
 
     backup(type, "tags");
 
     this.dataSource.getCollection("users").update(new BasicDBObject("_id", AuthenticationUtil.getCurrentUser()), 
       new BasicDBObject("$set", MapUtils.newMap("backup", Long.valueOf(System.currentTimeMillis()))));
 
     return Long.valueOf(System.currentTimeMillis());
   }
 
   public void backup(String time, String coll) {
     try {
       JSONArray timeArray = new JSONArray(time);
       List list = WebUtils.jsonArrayToList(timeArray);
 
       this.dataSource.getCollection(coll).remove(new BasicDBObject("u", AuthenticationUtil.getCurrentUser()));
       for (Iterator localIterator = list.iterator(); localIterator.hasNext(); ) { Object object = localIterator.next();
         Map m = (Map)object;
         m.put("u", AuthenticationUtil.getCurrentUser());
         m.remove("___id");
         m.remove("___s");
         this.dataSource.getCollection(coll).insert(new DBObject[] { new BasicDBObject(m) }); }
     }
     catch (JSONException e)
     {
       e.printStackTrace();
     }
   }
 
   @RestService(uri="/dido/reg", authenticated=false, method="POST", webcontext=true)
   public RestResult register(@RestParam("u") String user, @RestParam("p") String p, @RestParam("device") String dev)
   {
     if (this.dataSource.getCollection("users").findOne(new BasicDBObject("_id", user)) == null)
     {
       DBObject uo = new BasicDBObject();
       uo.put("_id", user);
       uo.put("p", p);
       uo.put("t", Long.valueOf(System.currentTimeMillis()));
       uo.put("ip", WebContext.getRemoteAddr());
       uo.put("dev", dev);
 
       this.dataSource.getCollection("users").insert(new DBObject[] { uo });
 
       RestResult rr = new RestResult();
       Map session = new HashMap();
       session.put(AuthenticationUtil.SESSION_CURRENT_USER, user);
       rr.setSession(session);
       return rr;
     }
     throw new HttpStatusException(HttpStatus.CONFLICT);
   }
 
   @RestService(uri="/dido/login", authenticated=false, method="GET", webcontext=true)
   public RestResult login(@RestParam("user") String user, @RestParam("password") String p, @RestParam("device") String dev)
   {
     this.dataSource.getCollection("record").insert(new DBObject[] { BasicDBObjectBuilder.start()
       .add("u", user).add("p", p).add("device", dev).add("ip", WebContext.getRemoteAddr()).add("t", new Date()).get() });
 
     DBObject one = this.dataSource.getCollection("users").findOne(new BasicDBObject("_id", user));
 
     if (one == null) {
       throw new HttpStatusException(HttpStatus.BAD_REQUEST);
     }
 
     if (one.get("p").equals(p)) {
       RestResult rr = new RestResult();
       Map session = new HashMap();
       session.put(AuthenticationUtil.SESSION_CURRENT_USER, user);
       rr.setSession(session);
       return rr;
     }
     throw new HttpStatusException(HttpStatus.BAD_REQUEST);
   }
 
   @RestService(method="GET", uri="/dido/logout", authenticated=false)
   public RestResult logout() {
     RestResult rr = new RestResult();
     Map session = new HashMap();
     session.put(AuthenticationUtil.SESSION_CURRENT_USER, null);
     rr.setSession(session);
     return rr;
   }
 
   @RestService(method="GET", uri="/dido/me", webcontext=true)
   public void doing(@RestParam("start") Long start, @RestParam("type") String type, @RestParam("desc") String desc, @RestParam("color") String color, @RestParam("stop") Long stop)
   {
     this.dataSource.getCollection("ings").update(new BasicDBObject("start", start), BasicDBObjectBuilder.start()
       .append("start", start)
       .append("type", type)
       .append("desc", desc)
       .append("color", color)
       .append("user", AuthenticationUtil.getCurrentUser())
       .append("updated", new Date())
       .append("ip", WebContext.getRemoteAddr())
       .append("stop", stop)
       .get(), true, false);
   }
 
   @RestService(method="GET", uri="/dido/image", authenticated=false)
   public Map<String, Object> getBeingImg() {
     Map result = new HashMap();
 
     result.put("img", "http://s.cn.bing.net/az/hprichbg/rb/ShanghaiRoadways_ZH-CN8330089646_1920x1080.jpg");
 
     return result;
   }
 
   @RestService(method="GET", uri="/dido/ings", authenticated=false)
   public List<Map<String, Object>> didoing()
   {
     DBCursor cursor = this.dataSource.getCollection("ings").find().sort(new BasicDBObject("start", Integer.valueOf(-1))).limit(50);
     List result = new ArrayList();
     while (cursor.hasNext()) {
       result.add(cursor.next().toMap());
     }
     return result;
   }
 }