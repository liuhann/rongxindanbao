package com.ever365.auth;

import com.ever365.mongo.MongoDataSource;
import com.ever365.rest.AuthenticationUtil;
import com.ever365.rest.HttpStatus;
import com.ever365.rest.HttpStatusException;
import com.ever365.rest.RestParam;
import com.ever365.rest.RestResult;
import com.ever365.rest.RestService;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import java.util.HashMap;
import java.util.Map;

public class AuthorityService {
	private static final String COLL_AUTHORITIES = "authorities";
	private static final String P = "p";
	private static final String U = "u";
	public static final String ADMIN = "admin";
	private String ap;
	private MongoDataSource dataSource;
	private Map<String, OAuthProvider> authProviders;

	public void setAuthProviders(Map<String, OAuthProvider> authProviders) {
		this.authProviders = authProviders;
	}

	public void setAp(String ap) {
		this.ap = ap;
	}

	public void setDataSource(MongoDataSource dataSource) {
		this.dataSource = dataSource;
	}

	@RestService(method = "GET", uri = "/logout", authenticated = false)
	public RestResult logout(@RestParam("re") String redirect) {
		RestResult rr = new RestResult();
		Map session = new HashMap();
		session.put(AuthenticationUtil.SESSION_CURRENT_USER, null);
		rr.setSession(session);
		if (redirect == null)
			rr.setRedirect("/");
		else {
			rr.setRedirect(redirect);
		}
		return rr;
	}

	public Map<String, Object> validate(String from, String code) {
		OAuthProvider provider = (OAuthProvider) this.authProviders
				.get(from);
		if (provider == null) {
			return null;
		}
		return provider.authorize(code);
	}

	@Deprecated
	@RestService(method = "GET", uri = "/person/exist")
	public boolean personExists(@RestParam("name") String userName) {
		DBCollection acoll = getAuthorityCollection();
		return acoll.findOne(new BasicDBObject("u", userName)) != null;
	}

	public DBCollection getAuthorityCollection() {
		return this.dataSource.getCollection("authorities");
	}

	@Deprecated
	@RestService(method = "GET", uri = "/person/current", authenticated = false)
	public String getCurrentPerson() {
		return AuthenticationUtil.getCurrentUser();
	}

	@Deprecated
	@RestService(method = "POST", uri = "/person/password")
	public void modifyPassword(
			@RestParam(value = "old", required = true) String old,
			@RestParam(value = "new", required = true) String newpass) {
		if (checkPassword(AuthenticationUtil.getCurrentUser(), old)) {
			DBObject p = new BasicDBObject();
			p.put("u", AuthenticationUtil.getCurrentUser());
			p.put("p", newpass);
			getAuthorityCollection()
					.update(new BasicDBObject("u",
							AuthenticationUtil.getCurrentUser()), p);
		}
	}

	@Deprecated
	@RestService(method = "POST", uri = "/person/add", runAsAdmin = true)
	public boolean createPerson(@RestParam("userId") String userName,
			@RestParam("password") String password) {
		if (personExists(userName))
			return false;
		DBObject p = new BasicDBObject();

		p.put("u", userName);
		p.put("p", password);
		getAuthorityCollection().insert(new DBObject[] { p });
		return true;
	}

	@Deprecated
	@RestService(method = "POST", uri = "/person/remove", runAsAdmin = true)
	public void removePerson(@RestParam("id") String userName) {
		getAuthorityCollection().remove(new BasicDBObject("u", userName));
	}

	@Deprecated
	@RestService(method = "POST", uri = "/person/checkpassword", authenticated = false)
	public boolean checkPassword(@RestParam("name") String userName,
			@RestParam("password") String password) {
		DBObject one = getAuthorityCollection().findOne(
				new BasicDBObject("u", userName));
		if (one == null) {
			return false;
		}

		return password.equals(one.get("p"));
	}

	@Deprecated
	@RestService(method = "POST", uri = "/person/login", authenticated = false)
	public RestResult login(@RestParam("name") String userName,
			@RestParam("password") String password) {
		if ((userName.equals("admin")) && (password.equals(this.ap))) {
			RestResult rr = new RestResult();
			Map session = new HashMap();
			session.put(AuthenticationUtil.SESSION_CURRENT_USER,
					userName);
			rr.setSession(session);
			return rr;
		}

		DBObject one = getAuthorityCollection().findOne(
				new BasicDBObject("u", userName));
		if (one == null) {
			throw new HttpStatusException(HttpStatus.UNAUTHORIZED);
		}
		if (password.equals(one.get("p"))) {
			RestResult rr = new RestResult();
			Map session = new HashMap();
			session.put(AuthenticationUtil.SESSION_CURRENT_USER,
					userName);
			rr.setSession(session);
			return rr;
		}
		throw new HttpStatusException(HttpStatus.UNAUTHORIZED);
	}
}
