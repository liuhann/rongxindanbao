package com.ever365.fin;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.bson.types.ObjectId;

import com.ever365.auth.Tenantable;
import com.ever365.common.LocalContentStore;
import com.ever365.mongo.MongoDataSource;
import com.ever365.rest.AuthenticationUtil;
import com.ever365.rest.HttpStatus;
import com.ever365.rest.HttpStatusException;
import com.ever365.rest.RestParam;
import com.ever365.rest.RestResult;
import com.ever365.rest.RestService;
import com.ever365.rest.StreamObject;
import com.ever365.utils.EmailUtils;
import com.ever365.utils.MapUtils;
import com.ever365.utils.RandomCodeServlet;
import com.ever365.utils.StringUtils;
import com.ever365.utils.UUID;
import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

public class FinanceService implements Tenantable {

	public static final String COLL_NEWS = "news";
	public static final String COLL_TEMPORARY = "temporary";
	public static final String COLL_LOANS = "loans";
	private static final String FIN_ROOT = "/";
	
	//五种类型  用户  企业  管理员  后台用户 信贷经理
	private static final String TYPE_PERSON = "person";
	private static final String TYPE_COMPANY = "company";
	public static final String TYPE_ADMIN = "admin";
	public static final String TYPE_BACK_USER = "backuser";
	public static final String COLL_ACCOUNTS = "accounts";


	private MongoDataSource dataSource;
	private LocalContentStore contentStore;
	private String pwd = "123456";
	
	public static Map<Integer, String> PERSON_LOAN_TYPES = new HashMap<Integer, String>();
	
	static {
		PERSON_LOAN_TYPES.put(1, "购车贷款");
		PERSON_LOAN_TYPES.put(1, "购房按揭贷款");
		PERSON_LOAN_TYPES.put(1, "个人消费贷款");
	}

	public static Map<String, String> INVESTMENT_TYPES = new HashMap<String, String>();
	static {
		INVESTMENT_TYPES.put("1", "网贷类投资");
		INVESTMENT_TYPES.put("2", "银行理财");
		INVESTMENT_TYPES.put("3", "定向类理财");
	}
	
	Logger logger = Logger.getLogger(FinanceService.class.getName());
	
	public void setContentStore(LocalContentStore contentStore) {
		this.contentStore = contentStore;
	}

	public MongoDataSource getDataSource() {
		return dataSource;
	}
	
	@RestService(uri="/fin/index", authenticated=false)
	public Map<String, Object> getIndexPageInfo() {
		Map<String, Object> m =  new HashMap<String, Object>();
		
		Map<String, Object> wdFilter = new HashMap<String, Object>();
		wdFilter.put("index", "1");
		wdFilter.put("type", "1");
		m.put("wd", filterCollection("investments", wdFilter, 0, 3));
		wdFilter.put("type", "2");
		m.put("yh", filterCollection("investments", wdFilter, 0, 3));
		wdFilter.put("type", "3");
		m.put("dx", filterCollection("investments", wdFilter, 0, 3));

		Map<String, Object> loanFilter = new HashMap<String, Object>();
		loanFilter.put("audit", MapUtils.newMap("$gt", 1));
		
		m.put("rc", dataSource.filterCollectoin(COLL_LOANS, loanFilter,null, 0, 6));


		m.put("fm", filterCollection("finamarkets", null, 0, 10));

		m.put("finres", dataSource.filterCollectoin("finres",null,null,0,8));
		m.put("news", dataSource.filterCollectoin("news",null,null,0,8));
		return m;
	}
	@RestService(uri="/fin/upload", method="POST", multipart=true, authenticated=false)
	public String uploadPreview(@RestParam("file") InputStream is, @RestParam("size") Long size, @RestParam("name") String name) {
		return this.contentStore.putContent(is, "image/png", size.longValue(), name);
	}

	@RestService(uri="/fin/preview", method="GET", authenticated=false)
	public StreamObject getPreview(@RestParam("id") String id) {
		return this.contentStore.getContentData(id);
	}
	 
	public void setDataSource(MongoDataSource dataSource) {
		this.dataSource = dataSource;
	}
	
	@RestService(method="POST", uri="/fin/account/reg", authenticated=false, rndcode=true)
	public RestResult register(
			Map<String, Object> req) {
		if (AuthenticationUtil.SYSTEM.equals(req.get("loginid"))) {
			throw new HttpStatusException(HttpStatus.CONFLICT);
		}
		DBObject u = dataSource.getCollection(COLL_ACCOUNTS).findOne(new BasicDBObject("loginid", req.get("loginid")));
		
		if (u!=null) {
			throw new HttpStatusException(HttpStatus.CONFLICT);
		}
		req.remove("rndcode");
		DBObject uinf = new BasicDBObject(req);
		sendConfirmEmail(uinf);
		
		uinf.put("registered", true);
		dataSource.getCollection(COLL_ACCOUNTS).insert(uinf);
		RestResult rr = new RestResult();
		rr.setSession(AuthenticationUtil.SESSION_CURRENT_USER, uinf.get("loginid"));
		return rr;
	}

	@RestService(method="POST", uri="/fin/account/findback", authenticated=false, rndcode=true)
	public void forgetPassword(
			Map<String, Object> req) {
		if (AuthenticationUtil.SYSTEM.equals(req.get("loginid"))) {
			throw new HttpStatusException(HttpStatus.CONFLICT);
		}
		DBObject uinf = dataSource.getCollection(COLL_ACCOUNTS).findOne(new BasicDBObject("loginid", req.get("loginid")));

		if (uinf==null) {
			throw new HttpStatusException(HttpStatus.BAD_REQUEST);
		}
		if (uinf.get("email").toString().equals(req.get("email"))) {
			uinf.put("efindback", ("a" + UUID.random(100000000)).substring(1));
			update(COLL_ACCOUNTS, uinf);
			EmailUtils.sendEmail("smtp.126.com", "25","liuhann@126.com",  "overlord123!@#", "liuhann@126.com",
					"融信担保密码找回", "您的验证码是 " + uinf.get("efindback"), uinf.get("email").toString());
		} else {
			throw new HttpStatusException(HttpStatus.BAD_REQUEST);
		}
	}

	@RestService(method="POST", uri="/fin/account/resetpwd", authenticated=false, rndcode=true)
	public RestResult resetPassword(
			Map<String, Object> req) {
		if (AuthenticationUtil.SYSTEM.equals(req.get("loginid"))) {
			throw new HttpStatusException(HttpStatus.CONFLICT);
		}
		DBObject uinf = dataSource.getCollection(COLL_ACCOUNTS).findOne(new BasicDBObject("loginid", req.get("loginid")));

		if (uinf==null) {
			throw new HttpStatusException(HttpStatus.BAD_REQUEST);
		}
		if (uinf.get("email").toString().equals(req.get("email"))) {

			if (uinf.get("efindback").toString().equals(req.get("emailcode"))) {
				uinf.removeField("efindback");
				uinf.put("pwd", req.get("pwd"));
				update(COLL_ACCOUNTS, uinf);
				RestResult rr = new RestResult();
				rr.setSession(AuthenticationUtil.SESSION_CURRENT_USER, req.get("loginid").toString());
				rr.setResult(FIN_ROOT + "home.jsp");
				return rr;
			}
		}
		throw new HttpStatusException(HttpStatus.BAD_REQUEST);
	}

	private void sendConfirmEmail(DBObject uinf) {
		if (uinf.get("email")!=null) {
			if (uinf.get("ecfm")==null) {
				uinf.put("ecfm", ("a" + UUID.random(100000000)).substring(1));
			}
			EmailUtils.sendEmail("smtp.126.com", "25","liuhann@126.com",  "overlord123!@#", "liuhann@126.com", 
					"请验证您在融信担保的注册邮件地址", "您的验证码是 " + uinf.get("ecfm"), uinf.get("email").toString());
		}
	}
	
	@RestService(method="GET", uri="/fin/account/email/confirm")
	public String confirmEmail(@RestParam("ecfm")String ecfm) {
		if (ecfm==null) {
			throw new HttpStatusException(HttpStatus.BAD_REQUEST);
		}
		DBObject uinf = dataSource.getCollection(COLL_ACCOUNTS).findOne(new BasicDBObject("loginid", AuthenticationUtil.getCurrentUser()));
		
		if (ecfm.equals(uinf.get("ecfm"))) {
			uinf.put("ecfm", true);
			update(COLL_ACCOUNTS, uinf);
			return "1";
		} else {
			return "0";
		}
	}
	
	@RestService(method="GET", uri="/fin/account/email/resend")
	public void resendEmail() {
		DBObject uinf = dataSource.getCollection(COLL_ACCOUNTS).findOne(new BasicDBObject("loginid", AuthenticationUtil.getCurrentUser()));
		sendConfirmEmail(uinf);
		update(COLL_ACCOUNTS, uinf);
	}
	
	@RestService(method="POST", uri="/fin/account/update")
	public void updateAccountInfo(Map req) {
		DBObject uinf = dataSource.getCollection(COLL_ACCOUNTS).findOne(new BasicDBObject("loginid", AuthenticationUtil.getCurrentUser()));
		
		if (!uinf.get("email").equals(req.get("email"))) {
			uinf.removeField("ecfm");
			sendConfirmEmail(uinf);
		}
		uinf.putAll(req);
		uinf.put("registered", true);
		uinf.put("loginid", AuthenticationUtil.getCurrentUser());
		update(COLL_ACCOUNTS, uinf);
	}

	@RestService(method="POST", uri="/fin/account/save", authenticated=true, runAsAdmin=true)
	public String saveAccount(Map<String, Object> req) {
		checkAccountValid(req);
		if (req.get("_id")==null || "".equals(req.get("_id"))) {
			//创建账户，需要进行loginid判断
			DBObject u = dataSource.getCollection(COLL_ACCOUNTS).findOne(new BasicDBObject("loginid", req.get("loginid")));
			if (u!=null) {
				throw new HttpStatusException(HttpStatus.CONFLICT);
			}
			req.remove("_id");
			dataSource.getCollection(COLL_ACCOUNTS).insert(new BasicDBObject(req));
		} else {
			BasicDBObject bdo = new BasicDBObject(req);
			bdo.put("_id", new ObjectId(req.get("_id").toString()));
			dataSource.getCollection(COLL_ACCOUNTS).update(new BasicDBObject("_id", new ObjectId(req.get("_id").toString())), bdo);
		}
		return "1";
	}

	@RestService(method="POST", uri="/fin/account/remove", authenticated=true, runAsAdmin=true)
	public void deleteAccount(@RestParam(value="loginid")String loginid) {
		DBObject exsited = dataSource.getCollection(COLL_ACCOUNTS).findOne(new BasicDBObject("loginid", loginid));

		if (exsited.get("type").equals("admin")||exsited.get("type").equals("bkuser")) {
			dataSource.getCollection(COLL_ACCOUNTS).remove(new BasicDBObject("loginid", loginid));
		}
	}

	@RestService(method="POST", uri="/fin/account/filter", authenticated=true)
	public Map<String, Object> filterAccount(@RestParam(value="filter")Map<String, Object> filters, @RestParam(value="skip") Integer skip, @RestParam(value="limit") Integer limit ) {
		Map<String, Object> m = dataSource.filterCollectoin(COLL_ACCOUNTS, filters, null, skip, limit);
		
		if (!AuthenticationUtil.isAdmin()) {
			List<Map> list = (List<Map>) m.get("list");
			for (Map map : list) {
				map.remove("pwd");
			}
		}
		return m;
	}

	@RestService(method="POST", uri="/fin/role/add", authenticated=true, runAsAdmin=true)
	public void addRole(@RestParam(value="name")String rolename, @RestParam(value="perms")List<String> pers) {

		DBObject role = dataSource.getCollection("roles").findOne(new BasicDBObject("role", rolename));
		dataSource.getCollection("roles").update(new BasicDBObject("role", rolename),
				BasicDBObjectBuilder.start().add("role", rolename).add("pers",pers).get(), true, false);
	}

	@RestService(method="POST", uri="/fin/role/remove", authenticated=true, runAsAdmin=true)
	public void removeRole(@RestParam(value="name")String rolename) {
		dataSource.getCollection("roles").remove(new BasicDBObject("role", rolename));
	}

	@RestService(method="POST", uri="/fin/account/role", authenticated=true, runAsAdmin=true)
	public void setAccountRole(@RestParam(value="loginid")String loginid, @RestParam(value="roles")List<String> roles) {
		DBObject account = dataSource.getCollection(COLL_ACCOUNTS).findOne(new BasicDBObject("loginid", loginid));
		if (account!=null) {
			account.put("roles", roles);
			update(COLL_ACCOUNTS, account);
		}
	}

	/*
	private Map<String, Object> filterCollectoin(String collection , Map<String, Object> filters,
			Integer skip, Integer limit) {
		Map<String, Object> result = new HashMap<String, Object>();
		DBObject query = new BasicDBObject();
		if (filters!=null) {
			query.putAll(filters);
		}
		DBCursor cursor = dataSource.getCollection(collection).find(query);
		result.put("size", cursor.count());
		cursor.skip(skip).limit(limit);
		List<Map> list = new ArrayList<Map>();
		while(cursor.hasNext()) {
			Map m = cursor.next().toMap();
			m.remove("pwd");
			list.add(m);
		}
		result.put("list", list);
		return result;
	}
	*/

	@RestService(method="POST", uri="/fin/loan/request")
	public void sendLoanRequest(@RestParam(value="map") Map<String, Object> request) {
		request.put("uid", AuthenticationUtil.getCurrentUser());
		request.put("rtime", new Date().getTime());
		//request.put("email", getCurrentUser().get("email"));
		//request.put("mobile", getCurrentUser().get("mobile"));
		request.put("audit", 1);
		dataSource.getCollection(COLL_LOANS).insert(new BasicDBObject(request));
		dataSource.getCollection(COLL_TEMPORARY).remove(new BasicDBObject("uid", AuthenticationUtil.getCurrentUser()));
	}
	
	@RestService(method="POST", uri="/fin/loan/temp/save")
	public void saveLoanRequest(@RestParam(value="map") Map<String, Object> request) {
		request.put("uid", AuthenticationUtil.getCurrentUser());
		request.put("rtime", new Date().getTime());
		request.remove("_id");
		dataSource.getCollection(COLL_TEMPORARY).update(new BasicDBObject("uid", AuthenticationUtil.getCurrentUser()),
				new BasicDBObject(request), true, false);
	}

	@RestService(method="GET", uri="/fin/loan/temp/get")
	public Map<String, Object> getRecentLoan() {
		DBObject one = dataSource.getCollection(COLL_TEMPORARY).findOne(new BasicDBObject("uid", AuthenticationUtil.getCurrentUser()));
		if (one!=null) {
			return one.toMap();
		} else {
			return new HashMap<String, Object>(0);
		}
	}
	
	@RestService(method="POST", uri="/fin/loan/list")
	public Map<String, Object> getLoanRequestList(@RestParam(value="filter")Map<String, Object> filters, @RestParam(value="skip") Integer skip, @RestParam(value="limit") Integer limit ) {
		//首先这里要做一下权限检查
		return dataSource.filterCollectoin(COLL_LOANS, filters, null, skip, limit);
	}
	
	@RestService(method="POST", uri="/fin/loan/approve")
	public void approveRequest(@RestParam(value="loan") String loanid, @RestParam(value="desc")String desc) {
		
		DBObject loan = dataSource.getCollection(COLL_LOANS).findOne(new BasicDBObject("_id", new ObjectId(loanid)));
		if (loan==null) {
			throw new HttpStatusException(HttpStatus.PRECONDITION_FAILED);
		}
		
		if (loan.get("audit").toString().equals("1")) { //初审
			loan.put("audit", 2);
			loan.put("firstaudit", desc);
			loan.put("firstauditDate", new Date().getTime());
			loan.put("firstauditor", AuthenticationUtil.getCurrentUser());
		} else if (loan.get("audit").toString().equals("2")) {
			loan.put("audit", 3);
			loan.put("finalaudit", desc);
			loan.put("finalauditDate", new Date().getTime());
			loan.put("finalauditor", AuthenticationUtil.getCurrentUser());
		}
		update(COLL_LOANS, loan);
	}
	
	
	@RestService(method="POST", uri="/fin/loan/reject")
	public void rejectRequest(@RestParam(value="loan") String loanid, @RestParam(value="desc")String desc) {
		
		DBObject loan = dataSource.getCollection(COLL_LOANS).findOne(new BasicDBObject("_id", new ObjectId(loanid)));
		if (loan==null) {
			throw new HttpStatusException(HttpStatus.PRECONDITION_FAILED);
		}
		if (loan.get("audit").toString().equals("1")) { //初审
			loan.put("firstaudit", desc);
			loan.put("firstauditDate", new Date().getTime());
			loan.put("firstauditor", AuthenticationUtil.getCurrentUser());
		} else if (loan.get("audit").toString().equals("2")) {
			loan.put("finalaudit", desc);
			loan.put("finalauditDate", new Date().getTime());
			loan.put("finalauditor", AuthenticationUtil.getCurrentUser());
		}
		
		loan.put("audit", -1);
		update(COLL_LOANS, loan);
	}
	
	@RestService(method="POST", uri="/fin/loan/push")
	public void pushLoanTocreditManager(@RestParam(value="loan") String loanid,
			@RestParam(value="pushuntil")String pushuntil, 
			@RestParam(value="ids")String credits 
			) {
		
		DBObject loan = dataSource.getCollection(COLL_LOANS).findOne(new BasicDBObject("_id", new ObjectId(loanid)));
		if (loan==null) {
			throw new HttpStatusException(HttpStatus.PRECONDITION_FAILED);
		}
		
		loan.put("pushuntil", pushuntil);
		loan.put("pushcredits", credits.split(","));
		
		update(COLL_LOANS, loan);
	}
	
	@RestService(method="POST", uri="/fin/loan/intrest")
	public void placeIntrestOn(@RestParam(value="id") String id) {
		DBObject loan = dataSource.getCollection(COLL_LOANS).findOne(new BasicDBObject("_id", new ObjectId(id)));
		if (loan==null) {
			throw new HttpStatusException(HttpStatus.PRECONDITION_FAILED);
		}
		Collection<String> intrests = new ArrayList<String>();
		if (loan.containsField("intrests")) {
			intrests = (Collection)loan.get("intrests");
		}
		intrests.add(AuthenticationUtil.getCurrentUser());
		loan.put("intrests", intrests);
		update(COLL_LOANS, loan);
	}

	/*
	 设置项目进度。 （只修改1个字段）
	 */
	@RestService(method="POST", uri="/fin/loan/progress")
	public void setLoanProgress(@RestParam(value="id") String id,@RestParam(value="progress") String progress) {

		DBObject loan = dataSource.getCollection(COLL_LOANS).findOne(new BasicDBObject("_id", new ObjectId(id)));
		if (loan==null) {
			throw new HttpStatusException(HttpStatus.PRECONDITION_FAILED);
		}
		loan.put("progress", progress);
		update(COLL_LOANS, loan);
	}

	/*
	 	完成项目进度。 将audit设置为10
	 */
	@RestService(method="POST", uri="/fin/loan/finish")
	public void finishLoan(@RestParam(value="id") String id,@RestParam(value="progress") String progress) {
		DBObject loan = dataSource.getCollection(COLL_LOANS).findOne(new BasicDBObject("_id", new ObjectId(id)));
		if (loan==null) {
			throw new HttpStatusException(HttpStatus.PRECONDITION_FAILED);
		}
		loan.put("progress", progress);
		loan.put("audit", 10);
		update(COLL_LOANS, loan);
	}

	@RestService(method="GET", uri="/fin/loan/credit/list")
	public Map<String, Object> getPushedLoans() {
		Map<String, Object> filters = new HashMap<String, Object>();
		filters.put("pushcredits", AuthenticationUtil.getCurrentUser());
		return dataSource.filterCollectoin(COLL_LOANS, filters, null, 0, 100);
	}
	
	
	@RestService(method="POST", uri="/fin/content/update")
	public void updateContent(Map<String, Object> request) {
		String collectionName = request.get("collection").toString();
		request.put("updated", new Date().getTime());
		request.put("editor", AuthenticationUtil.getCurrentUser());
		update(collectionName, new BasicDBObject(request));
	}
	
	@RestService(method="POST", uri="/fin/content/list")
	public Map<String, Object> filterCollection(@RestParam(value="collection")String collection, 
			@RestParam(value="filter")Map<String, Object> filters, @RestParam(value="skip") Integer skip, @RestParam(value="limit") Integer limit) {
		Map<String, Object> sort = new HashMap<String, Object>();
		sort.put("updated", -1);
		return getDataSource().filterCollectoin(collection, filters, sort, skip, limit);
	}

	@RestService(method="POST", uri="/fin/content/remove")
	public void removeContent(@RestParam(value="collection")String collection, @RestParam(value="_id") String id) {
		dataSource.getCollection(collection).remove(new BasicDBObject("_id", new ObjectId(id)));
	}


	@RestService(method="GET", uri="/fin/content/get")
	public Map getContent(@RestParam(value="collection")String collection, @RestParam(value="_id") String id) {
		DBObject one = dataSource.getCollection(collection).findOne(new BasicDBObject("_id", new ObjectId(id)));
		return one.toMap();
	}

	@RestService(method="GET", uri="/fin/content/getbykey")
	public Map getContentByKey(@RestParam(value="collection")String collection, @RestParam(value="key") String key,
							   @RestParam(value="value") String value) {
		DBObject one = dataSource.getCollection(collection).findOne(new BasicDBObject(key, value));
		return one.toMap();
	}

	@RestService(method="POST", uri="/fin/draft/add")
	public void addDraftRequest(Map<String, Object> request) {
		request.put("updated", new Date().getTime());
		request.put("editor", AuthenticationUtil.getCurrentUser());
		update("drafts", new BasicDBObject(request));
	}


	//检查账户创建请求的合法性
	public void checkAccountValid(Map<String, Object> req) {
		//首先判断一些必填字段 （将来判断的字段会更多）
		if (req.get("loginid")==null ||  req.get("pwd")==null || req.get("loginid").equals("") || req.get("pwd").equals("")) {
			throw new HttpStatusException(HttpStatus.BAD_REQUEST);
		}
	}
	
	@RestService(method="GET", uri="/fin/logout", authenticated=false)
	public RestResult logout() {
		RestResult rr = new RestResult();
		rr.setSession(AuthenticationUtil.SESSION_CURRENT_USER, null);
		rr.setSession(RandomCodeServlet.LOGIN_FAILED, null);
		rr.setRedirect("/");
		return rr;
	}
	
	@RestService(method="GET", uri="/fin/current", authenticated=false)
	public Map<String, Object> getCurrentUser() {
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("cu", AuthenticationUtil.getCurrentUser());
		
		if (AuthenticationUtil.getCurrentUser()!=null) {
			DBObject uinf = dataSource.getCollection(COLL_ACCOUNTS).findOne(new BasicDBObject("loginid", AuthenticationUtil.getCurrentUser()));
			if (uinf!=null) {
				result.putAll(uinf.toMap());
				if ("bkuser".equals(uinf.get("type"))) { //获取用户的角色
					Object rs = uinf.get("roles");
					List<String> perms = new ArrayList<String>();
					if (rs!=null) {
						String[] roleslist = rs.toString().split(",");
						for (int i = 0; i < roleslist.length; i++) {
							DBObject role = dataSource.getCollection("roles").findOne(new BasicDBObject("role", roleslist[i]));
							if (role!=null) {

							}
						}
					}
				}
			}
		}
		return result;
	}
	
	@RestService(method="GET", uri="/fin/org/list")
	public Map<String, Object> getOrgList(@RestParam(value="skip") Integer skip) {
		Map<String, Object> result = new HashMap<String, Object>();
		DBCollection cpcoll = dataSource.getCollection(COLL_ACCOUNTS);
		
		DBObject query = new BasicDBObject("type", TYPE_COMPANY);
		
		result.put("total", cpcoll.count(query));
		
		DBCursor cursor = cpcoll.find(query).skip(skip).limit(20);
		List<Map<String, Object>> l  = new ArrayList<Map<String,Object>>();
		while(cursor.hasNext()) {
			l.add(cursor.next().toMap());
		}
		result.put("list", l);
		return result;
	}
	
	@RestService(method="POST", uri="/fin/org/request")
	public void sendRequest(Map<String, Object> request) {
		request.put("org", AuthenticationUtil.getCurrentUser());
		dataSource.getCollection("finreq").insert(new BasicDBObject(request));
	}
	
	@RestService(method="GET", uri="/fin/org/request/list")
	public  Map<String, Object> getNav(@RestParam(value="nav")String nav, @RestParam(value="start") Integer start
			,@RestParam(value="limit") Integer limit) {
		Map<String, Object> result = new HashMap<String, Object>();
		
		DBCursor cursor = dataSource.getCollection("finreq").find(BasicDBObjectBuilder.start("org", AuthenticationUtil.getCurrentUser())
				.append("nav", nav).get()).skip(start).limit(limit);
		result.put("count", dataSource.getCollection("finreq").count(BasicDBObjectBuilder.start("org", AuthenticationUtil.getCurrentUser())
				.append("nav", nav).get()));
		
		List<Map> list = new ArrayList<Map>();
		while(cursor.hasNext()) {
			list.add(cursor.next().toMap());
		}
		result.put("list", list);
		return result;
	}
	
	@RestService(method="POST", uri="/fin/system/user/add", runAsAdmin=true)
	public void addSystemUser(@RestParam(value="uid")String uid, @RestParam(value="pwd") Integer pwd) {
		dataSource.getCollection("sysuser").insert(new BasicDBObjectBuilder().start().append("uid", uid)
				.append("pwd", pwd).get());
		return;
	}
	
	@RestService(method="POST", uri="/fin/login", authenticated=false, rndcode=true)
	public RestResult login(@RestParam(value="loginid")String uid, @RestParam(value="pwd") String pwd) {
		RestResult rr = new RestResult();
		if (AuthenticationUtil.SYSTEM.equals(uid) && this.pwd.equals(pwd)) {
			rr.setSession(AuthenticationUtil.SESSION_CURRENT_USER, uid);
			rr.setResult(FIN_ROOT + "admin.jsp");
			rr.setSession(AuthenticationUtil.SESSION_ADMIN, 1);
			return rr;
		} else {
			DBObject one = dataSource.getCollection(COLL_ACCOUNTS).findOne(new BasicDBObject("loginid", uid));
			if (one==null) {
				logger.info("login with user " + uid + ": user Not Found");
				throw new HttpStatusException(HttpStatus.FORBIDDEN);
			}
			if (!one.get("pwd").equals(pwd)) {
				logger.info("login with user " + uid + ": pass not match");
				throw new HttpStatusException(HttpStatus.FORBIDDEN);
			}
			rr.setSession(AuthenticationUtil.SESSION_CURRENT_USER, uid);
			if (one.get("type").equals(TYPE_ADMIN)) {
				rr.setSession(AuthenticationUtil.SESSION_ADMIN, 1);
				rr.setResult(FIN_ROOT + "admin.jsp");
			} 
			rr.setResult(FIN_ROOT + "home.jsp");
			return rr;
		}
	}
	
	@RestService(method="GET", uri="/fin/admin/request/list", authenticated=false)
	public  Map<String, Object> appGetNav(@RestParam(value="nav")String nav, @RestParam(value="start") Integer start
			,@RestParam(value="limit") Integer limit) {
		Map<String, Object> result = new HashMap<String, Object>();
		
		DBCursor cursor = dataSource.getCollection("finreq").find(BasicDBObjectBuilder.start()
				.append("nav", nav).get()).skip(start).limit(limit);
		result.put("count", dataSource.getCollection("finreq").count(BasicDBObjectBuilder.start()
				.append("nav", nav).get()));
		
		List<Map> list = new ArrayList<Map>();
		while(cursor.hasNext()) {
			list.add(cursor.next().toMap());
		}
		
		result.put("list", list);
		return result;
	}

	@RestService(method="POST", uri="/fin/admin/request/pri", roles={"appri"}, authenticated=false)
	public void approvePrimary(@RestParam("id")String id, @RestParam(value="comment") String comment) {
		
		DBObject one = dataSource.getCollection("finreq").findOne(new BasicDBObject("_id", new ObjectId(id)));
		if (one==null) return;
		one.put("nav", "2");
		one.put("comment", comment);
		dataSource.getCollection("finreq").update(new BasicDBObject("_id", new ObjectId(id)), one);
	}
	
	@RestService(method="POST", uri="/fin/admin/request/cut", roles={"appri"}, authenticated=false)
	public void cut(@RestParam("id")String id, @RestParam(value="comment") String comment) {
		
		DBObject one = dataSource.getCollection("finreq").findOne(new BasicDBObject("_id", new ObjectId(id)));
		if (one==null) return;
		one.put("nav", "-1");
		one.put("comment", comment);
		dataSource.getCollection("finreq").update(new BasicDBObject("_id", new ObjectId(id)), one);
	}
	
	@RestService(method="POST", uri="/fin/admin/request/sec", roles={"appsec"}, authenticated=false)
	public void approveSecondary(@RestParam("id")String id, @RestParam(value="comment") String comment) {
		DBObject one = dataSource.getCollection("finreq").findOne(new BasicDBObject("_id", new ObjectId(id)));
		if (one==null) return;
		one.put("nav", "10");
		one.put("comment", comment);
		dataSource.getCollection("finreq").update(new BasicDBObject("_id", new ObjectId(id)), one);
	}
	
	public void update(String coll, DBObject dbo) {
		if (dbo.get("_id")==null) {
			dataSource.getCollection(coll).insert(dbo);
		} else {
			DBObject qr = new BasicDBObject();
			if (dbo.get("_id") instanceof String) {
				qr = new BasicDBObject("_id", new ObjectId(dbo.get("_id").toString()));
				dbo.removeField("_id");
			} else if (dbo.get("_id") instanceof ObjectId) { 
				qr = new BasicDBObject("_id", (ObjectId)dbo.get("_id"));
			}
			dataSource.getCollection(coll).update(qr, dbo, true,false);
		}
	}
}
