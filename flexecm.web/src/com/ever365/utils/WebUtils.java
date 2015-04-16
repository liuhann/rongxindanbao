package com.ever365.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.SimpleHttpConnectionManager;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class WebUtils {
	private static Log logger = LogFactory.getLog(WebUtils.class);

	public static JSONObject doGet(String requestUrl) {
		HttpClient httpClient = new HttpClient(new HttpClientParams(),
				new SimpleHttpConnectionManager(true));

		GetMethod getMethod = new GetMethod(requestUrl);

		JSONObject jsonObject = performGet(requestUrl, httpClient, getMethod);
		return jsonObject;
	}

	public static String doGetHTML(String requestUrl, String cookie) {
		HttpClient httpClient = new HttpClient(new HttpClientParams(),
				new SimpleHttpConnectionManager(true));

		GetMethod getMethod = new GetMethod(requestUrl);
		getMethod.setRequestHeader("Cookie", cookie);
		logger.info("[GET]" + requestUrl);
		try {
			int statusCode = httpClient.executeMethod(getMethod);
			if (statusCode != 200) {
				logger.info("Method failed: request url:" + requestUrl
						+ "  status:" + getMethod.getStatusLine());
			}
			InputStream bodyis = getMethod.getResponseBodyAsStream();

			String result = convertStreamToString(bodyis);

			String str1 = result;
			return str1;
		} catch (HttpException e) {
			logger.debug("HttpException on get url" + requestUrl);
		} catch (IOException e) {
			logger.debug("IOException on get url" + requestUrl);
		} finally {
			getMethod.releaseConnection();
		}
		return "";
	}

	public static JSONObject doGet(String requestUrl,
			Map<String, String> headers) {
		HttpClient httpClient = new HttpClient(new HttpClientParams(),
				new SimpleHttpConnectionManager(true));
		GetMethod getMethod = new GetMethod(requestUrl);

		for (String header : headers.keySet()) {
			getMethod.setRequestHeader(header, (String) headers.get(header));
		}
		JSONObject jsonObject = performGet(requestUrl, httpClient, getMethod);
		return jsonObject;
	}

	private static JSONObject performGet(String requestUrl,
			HttpClient httpClient, GetMethod getMethod) {
		logger.info("[GET]" + requestUrl);
		JSONObject jsonObject = null;
		try {
			int statusCode = httpClient.executeMethod(getMethod);
			if (statusCode != 200) {
				logger.info("Method failed: request url:" + requestUrl
						+ "  status:" + getMethod.getStatusLine());
			}
			InputStream bodyis = getMethod.getResponseBodyAsStream();

			String result = convertStreamToString(bodyis);
			try {
				jsonObject = new JSONObject(result);
			} catch (Exception e) {
				logger.debug("json invalid:" + requestUrl + "  content: "
						+ result);
			}
		} catch (HttpException e) {
			logger.debug("HttpException on get url" + requestUrl);
		} catch (IOException e) {
			logger.debug("IOException on get url" + requestUrl);
		} finally {
			getMethod.releaseConnection();
		}
		return jsonObject;
	}

	public static JSONObject doPost(String requestUrl,
			Map<String, Object> params, Map<String, String> headers) {
		HttpClient httpClient = new HttpClient(new SimpleHttpConnectionManager(
				true));
		PostMethod postMethod = new PostMethod(requestUrl);

		for (String header : headers.keySet()) {
			postMethod.setRequestHeader(header, (String) headers.get(header));
		}

		return performPost(requestUrl, params, httpClient, postMethod);
	}

	public static JSONObject doPost(String requestUrl,
			Map<String, Object> params) {
		HttpClient httpClient = new HttpClient(new SimpleHttpConnectionManager(
				true));
		PostMethod postMethod = new PostMethod(requestUrl);

		return performPost(requestUrl, params, httpClient, postMethod);
	}

	public static JSONObject performPost(String requestUrl,
			Map<String, Object> params, HttpClient httpClient,
			PostMethod postMethod) {
		for (String key : params.keySet()) {
			Object value = params.get(key);
			if (value != null) {
				postMethod.setParameter(key, value.toString());
			}
		}
		postMethod.getParams().setParameter("http.protocol.content-charset",
				"UTF-8");

		JSONObject jsonObject = null;
		try {
			int statusCode = httpClient.executeMethod(postMethod);

			if (statusCode != 200) {
				logger.info("Method failed: request url:" + requestUrl
						+ "  status:" + postMethod.getStatusLine());
			}
			byte[] responseBody = postMethod.getResponseBody();
			String result = new String(responseBody, "utf-8");
			try {
				jsonObject = new JSONObject(result);
			} catch (Exception e) {
				logger.info("json invalid:" + requestUrl + "  content: "
						+ result);
			}
		} catch (HttpException e) {
			logger.debug("HttpException on get url" + requestUrl);
		} catch (IOException e) {
			logger.debug("IOException on get url" + requestUrl);
		} finally {
			postMethod.releaseConnection();
			httpClient.getHttpConnectionManager().closeIdleConnections(0L);
		}
		return jsonObject;
	}

	public static String convertStreamToString(InputStream is) {
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					is, "UTF-8"));
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}
			String str1 = sb.toString();
			return str1;
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return null;
	}

	public static List<Object> jsonArrayToList(JSONArray jsonArray) {
		List ret = new ArrayList();
		Object value = null;
		int length = jsonArray.length();
		for (int i = 0; i < length; i++) {
			try {
				value = jsonArray.get(i);
			} catch (JSONException e) {
				System.out
						.println(" there are no value with the index in the JSONArray");
				e.printStackTrace();
				return null;
			}
			if ((value instanceof JSONArray))
				ret.add(jsonArrayToList((JSONArray) value));
			else if ((value instanceof JSONObject))
				ret.add(jsonObjectToMap((JSONObject) value));
			else {
				ret.add(value);
			}
		}

		return ret.size() != 0 ? ret : null;
	}

	public static Map<String, Object> jsonObjectToMap(JSONObject jsonObject) {
		Map ret = new HashMap();
		Object value = null;
		String key = null;
		for (Iterator keys = jsonObject.keys(); keys.hasNext();) {
			key = (String) keys.next();
			try {
				value = jsonObject.get(key);
				if (value.toString().equals("null"))
					value = null;
			} catch (JSONException e) {
				System.out.println("the key is not found in the JSONObject");
				e.printStackTrace();
				return null;
			}
			if ((value instanceof JSONArray))
				ret.put(key, jsonArrayToList((JSONArray) value));
			else if ((value instanceof JSONObject))
				ret.put(key, jsonObjectToMap((JSONObject) value));
			else {
				ret.put(key, value);
			}
		}
		return ret.size() != 0 ? ret : null;
	}

}
