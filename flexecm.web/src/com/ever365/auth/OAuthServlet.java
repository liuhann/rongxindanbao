package com.ever365.auth;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ever365.mongo.MongoDataSource;
import com.ever365.rest.HttpStatus;
import com.ever365.rest.HttpStatusException;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import org.springframework.web.context.ContextLoaderListener;

import com.ever365.rest.AuthenticationUtil;
import com.ever365.rest.CookieService;


/***
 * For Weixin Only!!!
 */
public class OAuthServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private CookieService cookieService;
	public static final String OAUTH_REDIRECT = "oauth_redirect";
	private Map<String, OAuthProvider> providers = new HashMap();
	private MongoDataSource dataSource = null;
	Logger logger = Logger.getLogger(OAuthServlet.class.getName());

	public void init() throws ServletException {
		super.init();
		this.cookieService = ContextLoaderListener.getCurrentWebApplicationContext().getBean("rest.cookie", CookieService.class);
		this.dataSource = ContextLoaderListener.getCurrentWebApplicationContext().getBean("bindingdb", MongoDataSource.class);
		this.providers.put("/wx", new WeixinOAuthProvider());
	}

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		logger.info("OAuthServlet: " + request.getRequestURI() + "? " + request.getQueryString());
		String servletPath = getServicePath(request);

		try {
			if (request.getParameter("code")==null) {
				response.sendRedirect("/error.html");
			}
			OAuthProvider pr = (OAuthProvider) this.providers.get(servletPath);
			if (pr == null) {// 不支持这个平台地址的oauth登录
				throw new HttpStatusException(HttpStatus.BAD_REQUEST);
			}
			Map authDetail = pr.authorize(request.getParameter("code"));
			if (authDetail == null) {// 验证失败
				throw new HttpStatusException(HttpStatus.UNAUTHORIZED);
			}
			String userId = null;
			if (pr.binding()) { //如果要和平台账号绑定
				setUser(request); //首先设置平台账号信息

				if (AuthenticationUtil.getCurrentUser()!=null) {
					userId = AuthenticationUtil.getCurrentUser();
					//当前用户已登录， 执行绑定操作
					logger.info("binding user  with openid: " +  AuthenticationUtil.getCurrentUser()
					+ "-->" + authDetail.get("openid"));
					DBObject binding = new BasicDBObject();
					binding.put("openid", authDetail.get("openid"));
					binding.put("userId", AuthenticationUtil.getCurrentUser());
					dataSource.getCollection(pr.getName()).update(
						new BasicDBObject("openid", authDetail.get("openid")), binding, true, false);
				} else {
					//处理用户未登录， 用openid来登录系统
					logger.info("get user  with openid: " + authDetail.get("openid"));
					DBObject one = dataSource.getCollection(pr.getName()).findOne(
							new BasicDBObject("openid", authDetail.get("openid"))
					);
					if (one==null) {
						response.sendRedirect("/wx/login.html");
						return;
					}
					userId = (String)one.get("userId");
					logger.info("userId : " + userId);
				}
			} else {
				userId = (String) authDetail.get(OAuthProvider.USERID);
			}
			request.getSession().setAttribute(AuthenticationUtil.SESSION_CURRENT_USER, userId);
			AuthenticationUtil.setCurrentUser(userId);
			if (this.cookieService != null) {
				this.cookieService.bindUserCookie(request, response, userId);
			}

			if (request.getSession().getAttribute(OAUTH_REDIRECT) != null) {
				Object redirect = request.getSession().getAttribute(
						OAUTH_REDIRECT);
				request.getSession().removeAttribute(OAUTH_REDIRECT);
				logger.info("redirect to "  + redirect.toString());
				response.sendRedirect(redirect.toString());
				return;
			}

			if ("/wx".equals(servletPath)) {
				if (request.getParameter("state")!=null) {
					logger.info("redirect to "  + request.getParameter("state"));
					response.sendRedirect(request.getParameter("state"));
				} else {
					response.sendRedirect("/wx/me.html");
				}
				return;
			}

			response.sendRedirect("/wx/me.html");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setUser(HttpServletRequest request) {
		String tuser = request.getParameter("user");
		if ((tuser != null) && (tuser.startsWith("__"))) {
			AuthenticationUtil.setCurrentUser(tuser);
			return;
		}
		AuthenticationUtil.setCurrentUser(null);

		Object user = request.getSession().getAttribute(
				AuthenticationUtil.SESSION_CURRENT_USER);
		if (user != null) {
			logger.info("User from session: " + user);
			AuthenticationUtil.setCurrentUser((String) user);
		} else if (this.cookieService != null) {
			user = this.cookieService.getCurrentUser(request);
			if (user != null) {
				logger.info("User from cookie: " + user);
				AuthenticationUtil.setCurrentUser((String) user);
				request.getSession().setAttribute(
						AuthenticationUtil.SESSION_CURRENT_USER, user);
			}
		}
	}


	public String getServicePath(HttpServletRequest request)
			throws UnsupportedEncodingException {
		String strPath = URLDecoder.decode(request.getRequestURI(), "UTF-8");
		String servletPath = request.getServletPath();

		int rootPos = strPath.indexOf(servletPath);
		if (rootPos != -1)
			strPath = strPath.substring(rootPos + servletPath.length());
		return strPath;
	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		if (request.getParameter("redirect") != null)
			request.getSession().setAttribute(OAUTH_REDIRECT,
					request.getParameter("redirect"));
	}
}
