package com.ever365.comment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bson.types.ObjectId;
import org.json.JSONObject;
import org.jsoup.Jsoup;

import com.ever365.auth.WeiboOAuthProvider;
import com.ever365.mongo.MongoDataSource;
import com.ever365.rest.RestParam;
import com.ever365.rest.RestService;
import com.ever365.utils.HTMLParser;
import com.ever365.utils.MapUtils;
import com.ever365.utils.StringUtils;
import com.ever365.utils.WebUtils;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.WriteResult;

public class CommentFriendService {
	private WeiboOAuthProvider weiboOAuthProvider;
	private MongoDataSource mongoDataSource;
	public Map<String, Object> front = new HashMap();

	public static Pattern urlPattern = Pattern.compile("http://[\\w./]+", 128);

	public void setMongoDataSource(MongoDataSource mongoDataSource) {
		this.mongoDataSource = mongoDataSource;
	}

	public WeiboOAuthProvider getWeiboOAuthProvider() {
		return this.weiboOAuthProvider;
	}

	public void setWeiboOAuthProvider(WeiboOAuthProvider weiboOAuthProvider) {
		this.weiboOAuthProvider = weiboOAuthProvider;
	}

	@RestService(uri = "/front")
	public Map<String, Object> getFront() {
		if (this.front.size() == 0) {
			initFront();
			return this.front;
		}
		return this.front;
	}

	public Map<String, Object> getArticle(String id) {
		DBObject article = this.mongoDataSource.getCollection("article")
				.findOne(new BasicDBObject("_id", new ObjectId(id)));
		if (article == null)
			return null;

		Map m = article.toMap();

		m.put("comment", Long.valueOf(this.mongoDataSource.getCollection(
				"comments").count(new BasicDBObject("aid", new ObjectId(id)))));

		return m;
	}

	private synchronized void initFront() {
		if (this.front.size() > 0)
			return;

		this.front.put("article", Long.valueOf(this.mongoDataSource
				.getCollection("article").count()));
		this.front.put("comment", Long.valueOf(this.mongoDataSource
				.getCollection("comments").count()));

		DBCursor cardCursor = this.mongoDataSource.getCollection("article")
				.find(new BasicDBObject("cover", MapUtils.newMap("$ne", "")))
				.limit(3);
		List cards = new ArrayList();
		while (cardCursor.hasNext()) {
			DBObject dbo = cardCursor.next();
			Map m = shortInfo(dbo);
			cards.add(m);
		}

		this.front.put("cards", cards);
		List all = new ArrayList();
		DBCursor allCursor = this.mongoDataSource.getCollection("article")
				.find().sort(new BasicDBObject("_id", Integer.valueOf(-1)));

		while (allCursor.hasNext()) {
			DBObject dbo = allCursor.next();
			Map m = shortInfo(dbo);
			all.add(m);
		}
		this.front.put("all", all);
	}

	public Map<String, Object> shortInfo(DBObject dbo) {
		Map m = new HashMap();
		m.put("author", dbo.get("user"));

		m.put("date", dbo.get("date"));
		if (dbo.get("avtar") != null) {
			m.put("avtar", dbo.get("avtar"));
		} else {
			DBObject one = this.mongoDataSource.getCollection("authors")
					.findOne(new BasicDBObject("name", m.get("author")));
			if (one != null) {
				m.put("avtar", one.get("avatar_large"));
			}
		}
		m.put("title", dbo.get("title"));
		m.put("cover", dbo.get("cover"));
		m.put("id", dbo.get("_id").toString());
		m.put("comments", Long.valueOf(this.mongoDataSource.getCollection(
				"comments").count(new BasicDBObject("aid", dbo.get("_id")))));
		String text = Jsoup.parse((String) dbo.get("html")).text();
		if (text.length() > 100)
			m.put("some", text.substring(0, 100));
		else {
			m.put("some", text);
		}
		return m;
	}

	@RestService(uri = "/url/contr", method = "POST", authenticated = true)
	public void postUrl(@RestParam("url") String url) {
		if (url == null) {
			return;
		}
		this.front.clear();
		if (url.startsWith("http://")) {
			try {
				Map article = HTMLParser.parseArticle(url);
				if (article == null)
					return;
				this.mongoDataSource.getCollection("article").update(
						new BasicDBObject("title", article.get("title")),
						new BasicDBObject(article), true, false);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else if (StringUtils.isNumber(url))
			importWeiboArticle(url);
	}

	public void importWeiboArticle(String id) {
		String at = (String) this.weiboOAuthProvider.getFullWeiboInfo(null)
				.get("at");

		String requestUrl = "https://api.weibo.com/2/statuses/show.json?access_token="
				+ at + "&id=" + id;

		JSONObject jso = WebUtils.doGet(requestUrl);

		Map m = WebUtils.jsonObjectToMap(jso);

		if (m.get("error") != null) {
			System.out.println("Error Request : " + requestUrl);
			System.out.println(jso.toString());
			return;
		}

		Map authorInfo = (Map) m.get("user");
		this.mongoDataSource.getCollection("authors").update(
				new BasicDBObject("name", authorInfo.get("name")),
				new BasicDBObject(authorInfo), true, false);

		DBCollection wbcoll = this.mongoDataSource.getCollection("wbs");
		BasicDBObject bdo = new BasicDBObject();
		bdo.putAll(m);
		wbcoll.insert(new DBObject[] { bdo });

		String text = (String) m.get("text");
		String shortUrl = extractUrl(text);

		String convertUrl = "https://api.weibo.com/2/short_url/expand.json?url_short="
				+ shortUrl + "&access_token=" + at;
		Map convertMap = WebUtils.jsonObjectToMap(WebUtils.doGet(convertUrl));

		String longUrl = (String) ((Map) ((List) convertMap.get("urls")).get(0))
				.get("url_long");

		String articleId = StringUtils.middle(longUrl, "/p/", "w");

		String h5ArticleUrl = "http://card.weibo.com/article/h5/s#cid="
				+ articleId + "&vid=&extparam=";

		System.out.println("H5文章链接是" + h5ArticleUrl);
		try {
			Map article = HTMLParser.parseArticle(h5ArticleUrl);
			if (article == null) {
				System.out.println("Error import " + h5ArticleUrl);
			}

			DBObject dbo = new BasicDBObject(article);

			article.put("avtar", ((Map) m.get("user")).get("avatar_large"));
			WriteResult r = this.mongoDataSource.getCollection("article")
					.update(new BasicDBObject("title", article.get("title")),
							new BasicDBObject(article), true, false);

			DBObject refreshed = this.mongoDataSource.getCollection("article")
					.findOne(new BasicDBObject("title", article.get("title")));

			System.out.println("文章ID" + r.getField("_id") + "   "
					+ refreshed.get("_id"));

			int page = 1;
			while (fetchComments(at, id, (ObjectId) refreshed.get("_id"),
					Integer.valueOf(page)).intValue() > 0)
				page++;
		} catch (IOException e) {
			System.out.println("获取H5的文章链接失败 地址：" + h5ArticleUrl);
		}
	}

	public Integer fetchComments(String at, String wbid, ObjectId articleId,
			Integer page) {
		String commentUrl = "https://api.weibo.com/2/comments/show.json?access_token="
				+ at + "&id=" + wbid + "&page=" + page;

		System.out.println("评论链接" + commentUrl);

		Map commentsMap = WebUtils.jsonObjectToMap(WebUtils.doGet(commentUrl));
		try {
			List<Map> commentList = (List) commentsMap.get("comments");
			for (Map comment : commentList) {
				if (comment.get("user") != null) {
					DBObject commentdbo = new BasicDBObject();
					Map userMap = (Map) comment.get("user");
					Long cid = (Long) comment.get("id");
					commentdbo.put("cid", cid);
					commentdbo
							.put("created", StringUtils
									.parseWeiboTime((String) comment
											.get("created_at")));
					commentdbo.put("text", comment.get("text"));
					commentdbo.put("aid", articleId);
					commentdbo.put("user", userMap.get("screen_name"));
					commentdbo.put("avatar", userMap.get("avatar_large"));
					this.mongoDataSource.getCollection("comments").update(
							new BasicDBObject("cid", cid), commentdbo, true,
							false);
				}
			}
			return Integer.valueOf(commentList.size());
		} catch (Exception e) {
		}
		return Integer.valueOf(0);
	}

	public String extractUrl(String text) {
		Matcher matcher = urlPattern.matcher(text);
		if (matcher.find()) {
			if (matcher.groupCount() == 0) {
				return matcher.group();
			}
			return matcher.group(0);
		}

		return null;
	}

	public static void main(String[] args) {
		String text = "我发表了文章 http://t.cn/RZaWqWwwer  宗宁：关于凡客，陈年并没有说实话 宗宁：关于凡客http://t.cn/RZaWqWwwer，陈年并没有说实话 最近看到陈年对凡客的反思很火爆，之前大家都喜欢成功的例子，现在大家都喜欢看这种失败的例子。 不过总的说来，陈年没有说实话，不能说每一句都不是实话，但关键的部分都不是实话。";

		CommentFriendService cfs = new CommentFriendService();

		System.out.println(cfs.extractUrl(text));
	}

	@RestService(uri = "/weibo/cmt/import")
	public Map<String, Object> doImport() {
		Map uInfo = this.weiboOAuthProvider.getFullWeiboInfo("5342568291");

		String timelineUrl = "https://api.weibo.com/2/statuses/user_timeline.json?access_token="
				+ uInfo.get("at");

		Map mytimelines = WebUtils.jsonObjectToMap(WebUtils.doGet(timelineUrl));

		return mytimelines;
	}
}
