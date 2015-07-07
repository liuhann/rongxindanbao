package com.ever365.auth;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.context.ContextLoaderListener;

import com.ever365.rest.AuthenticationUtil;
import com.ever365.rest.CookieService;

public class OAuthServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private CookieService cookieService;
	private AuthorityService authorityService;
	public static final String SESSION_REDIRECT = "oauth_redirect";
	private Map<String, OAuthProvider> providers = new HashMap();

	public void init() throws ServletException {
		super.init();
		Object o = ContextLoaderListener.getCurrentWebApplicationContext()
				.getBean("rest.cookie");
		if (o != null) {
			this.cookieService = ((CookieService) o);
		}
		this.authorityService = ((AuthorityService) ContextLoaderListener
				.getCurrentWebApplicationContext().getBean("rest.authority"));
		this.providers.put("/wx", new WeixinOAuthProvider());
	}

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String servletPath = getServicePath(request);
		try {
			if (request.getParameter("code")==null) {
				response.sendRedirect("/error.html");
			}
			setUser(request);

			Map detail = this.authorityService.validate(servletPath,
					request.getParameter("code"));

			if (detail == null) {
				response.sendRedirect("/error/401.html");
				return;
			}
			String userId = (String) detail.get("uid");
			request.getSession().setAttribute(
					AuthenticationUtil.SESSION_CURRENT_USER, userId);
			AuthenticationUtil.setCurrentUser(userId);
			if (this.cookieService != null)
				this.cookieService.bindUserCookie(request, response, userId);
		} catch (Throwable t) {
			System.out.println(t.getLocalizedMessage());
		}

		if (request.getSession().getAttribute("oauth_redirect") != null) {
			Object redirect = request.getSession().getAttribute(
					"oauth_redirect");
			request.getSession().removeAttribute("oauth_redirect");
			response.sendRedirect(redirect.toString());
		} else {
			response.sendRedirect("/");
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
			AuthenticationUtil.setCurrentUser((String) user);
		} else if (this.cookieService != null) {
			user = this.cookieService.getCurrentUser(request);
			if (user != null) {
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
			request.getSession().setAttribute("oauth_redirect",
					request.getParameter("redirect"));
	}
}
