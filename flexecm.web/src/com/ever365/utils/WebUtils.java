package com.ever365.utils;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.SimpleHttpConnectionManager;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.util.FileCopyUtils;

import java.io.*;
import java.util.*;
import java.util.logging.Logger;

public class WebUtils {
	private static Logger logger = Logger.getLogger(WebUtils.class.getName());

	public static JSONObject doGet(String requestUrl) {
		HttpClient httpClient = new HttpClient(new HttpClientParams(),
				new SimpleHttpConnectionManager(true));

		GetMethod getMethod = new GetMethod(requestUrl);

		JSONObject jsonObject = performGet(requestUrl, httpClient, getMethod);
		return jsonObject;
	}


	public static void multiPartPost(String url, Map<String, Object> params) throws IOException {
		PostMethod method = new PostMethod(url);

		try {
			List<Part> parts = new ArrayList<Part>();

			for (String key : params.keySet()) {
				Object value = params.get(key);
				if (value instanceof String) {
					parts.add(new StringPart(key, (String) value, method.getRequestCharSet()));
				}
				if (value instanceof File) {
					parts.add(new FilePart(key, (File) value));
				}
			}

			method.setRequestEntity(new MultipartRequestEntity(parts.toArray(new Part[parts.size()]), method.getParams()));

			HttpClient client = new HttpClient(new HttpClientParams(),
					new SimpleHttpConnectionManager(true));
			client.getHttpConnectionManager().getParams().setConnectionTimeout(50000);
			// 由于要上传的文件可能比较大 , 因此在此设置最大的连接超时时间

			int statusCode = client.executeMethod(method);

			System.out.println("Status : " + statusCode);

			String responseString = method.getResponseBodyAsString();

			System.out.println("Response : \n\n" + responseString);
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} finally {
			method.releaseConnection();
		}
	}


	public static File downloadFile(String url) {
		HttpClient httpClient = new HttpClient(new HttpClientParams(),
				new SimpleHttpConnectionManager(true));
		GetMethod getMethod = new GetMethod(url);
		logger.info("[GET]" + url);
		JSONObject jsonObject = null;
		try {
			int statusCode = httpClient.executeMethod(getMethod);
			if (statusCode != 200) {
				logger.info("Method failed: request url:" + url + "  status:" + getMethod.getStatusLine());
			}
			File temp = File.createTempFile("pattern", ".suffix");

			// 创建临时文件
			//在程序退出时删除临时文件
			FileCopyUtils.copy(getMethod.getResponseBodyAsStream(), new FileOutputStream(temp));
			logger.info("file generated " + temp.getName() + "  size: " + temp.length());
			return temp;
		} catch (HttpException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			getMethod.releaseConnection();
		}
		return null;
	}

	public static String getString(String requestUrl) {
		HttpClient httpClient = new HttpClient(new HttpClientParams(),
				new SimpleHttpConnectionManager(true));

		GetMethod getMethod = new GetMethod(requestUrl);

		int statusCode = 0;
		try {
			statusCode = httpClient.executeMethod(getMethod);
			if (statusCode != 200) {
				logger.info("Method failed: request url:" + requestUrl
						+ "  status:" + getMethod.getStatusLine());
			}
			InputStream bodyis = getMethod.getResponseBodyAsStream();
			return convertStreamToString(bodyis);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			getMethod.releaseConnection();
		}
		return null;
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
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
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
				e.printStackTrace();
			}
		} catch (HttpException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
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
			logger.info("POST: " + requestUrl );
			int statusCode = httpClient.executeMethod(postMethod);
			logger.info("status: " + statusCode);

			if (statusCode==302) {
				Header location = postMethod.getResponseHeader("Location");
				if (location!=null) {
					logger.info("redirect to" + location.getValue());
					return doGet(location.getValue());
				}

			} else if (statusCode != 200) {
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
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
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
		for (Iterator keys = jsonObject.keys(); keys.hasNext(); ) {
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

	public static void main(String[] args) {
		try {
			// 创建临时文件
			//在程序退出时删除临时文件
			Map<String, Object> params = new HashMap<String, Object>();
			File f = new File("d:/avtar.jpg");
			System.out.println(f.length());
			params.put("userAccount", "zhangting");
			params.put("money", "1000");
			params.put("alipayAccount", "ail");
			params.put("fileName",f);

			WebUtils.multiPartPost("http://61.48.147.7:8086/eLiYou/wechat/saveRecharge.do", params);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
		}
	}





}
