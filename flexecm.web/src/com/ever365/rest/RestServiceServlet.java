package com.ever365.rest;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.context.ContextLoaderListener;

import com.ever365.utils.RandomCodeServlet;

public class RestServiceServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final String UTF_8 = "UTF-8";
	private static final String CONTENT_TYPE = "text/html; charset=UTF-8";
	private HttpServiceRegistry registry;
	private CookieService cookieService;
	Logger logger = Logger.getLogger(RestServiceServlet.class.getName());

	public void init(ServletConfig config) throws ServletException {
		this.registry = ((HttpServiceRegistry) ContextLoaderListener.getCurrentWebApplicationContext().getBean("http.registry"));
		Object o = ContextLoaderListener.getCurrentWebApplicationContext().getBean("rest.cookie");
		if (o != null) {
			this.cookieService = ((CookieService) o);
		}
	}

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		setUser(request);
		MethodInvocation handler = null;
		Map args = new HashMap();
		try {
			handler = getMethod(request);

			Enumeration paramNames = request.getParameterNames();
			while (paramNames.hasMoreElements()) {
				String name = (String) paramNames.nextElement();
				args.put(name,
						URLDecoder.decode(request.getParameter(name), "UTF-8"));
			}
			Object result = handler.execute(args);
			render(request, response, result);
			this.logger.info(request.getMethod() + " "
					+ request.getRequestURI() + request.getQueryString() + "  "
					+ AuthenticationUtil.getCurrentUser());
		} catch (Exception e) {
			if ((e instanceof HttpStatusException)) {
				response.sendError(((HttpStatusException) e).getCode(),
						((HttpStatusException) e).getDescription());
			} else {
				e.printStackTrace(response.getWriter());
				response.setStatus(500);
			}
		} finally {
			doCleanUp();
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

	protected void doPut(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		setUser(request);
		try {
			MethodInvocation handler = getMethod(request);
			
			Map args = new HashMap();

			if ((handler.isMultipart()) && (ServletFileUpload.isMultipartContent(request))) {
				DiskFileItemFactory factory = new DiskFileItemFactory();
				ServletFileUpload upload = new ServletFileUpload(factory);

				List<FileItem> items = upload.parseRequest(request);
				args = new HashMap();

				for (FileItem item : items)
					if (item.isFormField()) {
						args.put(item.getFieldName(), item.getString("UTF-8"));
					} else {
						args.put(item.getFieldName(), item);
						System.out.println("uploading file " + item.getName() + "  size" + item.getSize());
						args.put("name", URLDecoder.decode(item.getName(), "UTF-8"));
						args.put("size", Long.valueOf(item.getSize()));
					}
			} else {
				Enumeration paramNames = request.getParameterNames();
				while (paramNames.hasMoreElements()) {
					String name = (String) paramNames.nextElement();
					args.put(name, URLDecoder.decode(
							request.getParameter(name), "UTF-8"));
				}
			}
			
			if (handler.needRandCode() && request.getSession().getAttribute(RandomCodeServlet.LOGIN_FAILED)!=null) {
				if (request.getSession().getAttribute(RandomCodeServlet.RANDOMCODEKEY)==null) {
					throw new HttpStatusException(HttpStatus.BAD_REQUEST);
				}
				String rightCode = (String)request.getSession().getAttribute(RandomCodeServlet.RANDOMCODEKEY);
				
				System.out.println("args " + args.get("rndcode") + "    ri " + rightCode);
				if (!rightCode.equalsIgnoreCase((String)args.get("rndcode"))) {
					throw new HttpStatusException(HttpStatus.PRECONDITION_FAILED);
				}
			}
			Object result = handler.execute(args);
			if (result == null) {
				response.setStatus(200);
			} else {
				render(request, response, result);
			}
		} catch (Exception e) {
			if ((e instanceof HttpStatusException)) {
				if (((HttpStatusException) e).getCode()==HttpStatus.FORBIDDEN.value()) {
					request.getSession().setAttribute(RandomCodeServlet.LOGIN_FAILED, 1);
				}
				response.getWriter().println(extractError(e));
				response.sendError(((HttpStatusException) e).getCode(),
						((HttpStatusException) e).getDescription());
			} else {
				response.sendError(500);
				e.printStackTrace();
				response.getWriter().println(extractError(e));
			}
		} finally {
			doCleanUp();
		}
	}

	public MethodInvocation getMethod(HttpServletRequest request)
			throws UnsupportedEncodingException {
		String strPath = getServicePath(request);

		MethodInvocation handler = this.registry.getMethod(request.getMethod(),
				strPath);

		if (handler == null) {
			throw new HttpStatusException(HttpStatus.NOT_FOUND);
		}
		if ((handler.isAuthenticated())
				&& (AuthenticationUtil.getCurrentUser() == null)) {
			throw new HttpStatusException(HttpStatus.UNAUTHORIZED);
		}
		if ((handler.isRunAsAdmin()) && (!AuthenticationUtil.isAdmin())) {
			throw new HttpStatusException(HttpStatus.FORBIDDEN);
		}
		if (handler.isWebcontext()) {
			WebContext.setRemoteAddr(WebContext.getRemoteAddr(request));
			WebContext.setSessionId(request.getSession().getId());
		}
 
		if (request.getServerName().equals("127.0.0.1"))
			WebContext.setLocal(Boolean.valueOf(true));
		else {
			WebContext.setLocal(Boolean.valueOf(false));
		}

		return handler;
	}

	public void doCleanUp() {
		AuthenticationUtil.clearCurrentSecurityContext();
		WebContext.clear();
	}

	public String extractError(Exception e) {
		StackTraceElement[] trances = e.getStackTrace();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < trances.length; i++) {
			sb.append(trances[i].getClassName() + "  "
					+ trances[i].getLineNumber() + "\n");
		}
		sb.append(e.getMessage());
		return sb.toString();
	}

	private void render(HttpServletRequest request,
			HttpServletResponse response, Object result) throws IOException {
		if (result == null) {
			response.setStatus(200);
			return;
		}

		if ((result instanceof RestResult)) {
			RestResult rr = (RestResult) result;
			if (rr.getSession() != null) {
				HttpSession session = request.getSession();
				for (String key : rr.getSession().keySet()) {
					session.setAttribute(key, rr.getSession().get(key));
					if ((!key.equals(AuthenticationUtil.SESSION_CURRENT_USER)) || (this.cookieService == null))
						continue;
					if (rr.getSession().get(key) == null) {
						this.cookieService.removeCookieTicket(request, response);
					} else {
						this.cookieService.bindUserCookie(request, response, rr.getSession().get(key).toString());
					}
				}
			}

			if (rr.getRedirect() != null) {
				response.sendRedirect(rr.getRedirect());
				return;
			}
			if (rr.getResult()!=null) {
				PrintWriter pw = response.getWriter();
				pw.print(rr.getResult().toString());
			}

			response.setStatus(200);
			return;
		}

		if ((result instanceof StreamObject)) {
			handleFileDownload(request, response, (StreamObject) result);
			return;
		}

		response.setContentType("text/html; charset=UTF-8");
		PrintWriter pw = response.getWriter();
		if ((result instanceof Collection)) {
			JSONArray ja = new JSONArray((Collection) result);
			pw.print(ja.toString());
		} else if ((result instanceof Map)) {
			JSONObject jo = new JSONObject((Map) result);
			pw.print(jo.toString());
		} else {
			pw.print(result.toString());
		}
		pw.close();
	}

	public void handleFileDownload(HttpServletRequest request,
			HttpServletResponse response, StreamObject result)
			throws UnsupportedEncodingException, IOException {
		long modifiedSince = request.getDateHeader("If-Modified-Since");

		if (modifiedSince > 0L) {
			if (result.getLastModified() <= modifiedSince) {
				response.setStatus(304);
				return;
			}
		} else {
			response.setDateHeader("Last-Modified", result.getLastModified());
			response.setHeader("Cache-Control", "must-revalidate");
			response.setHeader("ETag", "\"" + result.getLastModified() + "\"");
		}

		String userAgent = request.getHeader("User-Agent");
		if (result.getFileName() != null) {
			String fileDwnName = new String(result.getFileName().getBytes(
					"UTF-8"), "ISO-8859-1");
			if ((userAgent != null) && (userAgent.contains("MSIE"))) {
				fileDwnName = new String(fileDwnName.getBytes("gb2312"),
						"ISO8859-1");
			}
			response.setHeader("Content-Disposition", "attachment; filename=\""
					+ fileDwnName + "\";");
		}

		response.setHeader("Accept-Ranges", "bytes");

		String range = request.getHeader("Content-Range");
		if (range == null) {
			range = request.getHeader("Range");
		}

		response.setContentType(result.getMimeType());
		long size = result.getSize();

		if (size != 0L) {
			response.setHeader(
					"Content-Range",
					"bytes 0-" + Long.toString(size - 1L) + "/"
							+ Long.toString(size));
			response.setContentLength(new Long(size).intValue());
			response.setHeader("Content-Length", Long.toString(size));
		}
		FileCopyUtils.copy(result.getInputStream(), response.getOutputStream());
	}
}

/*
 * Location: D:\360云盘\Desktops\T410\a.jar Qualified Name:
 * com.ever365.rest.RestServiceServlet JD-Core Version: 0.6.0
 */