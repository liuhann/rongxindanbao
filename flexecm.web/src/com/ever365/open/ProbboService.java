package com.ever365.open;

import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.ever365.mongo.MongoDataSource;
import com.ever365.rest.RestParam;
import com.ever365.rest.RestService;
import com.ever365.utils.WebUtils;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public class ProbboService {
	
	private MongoDataSource dataSource = null;
	
	public MongoDataSource getDataSource() {
		return dataSource;
	}
	
	public void setDataSource(MongoDataSource dataSource) {
		this.dataSource = dataSource;
	}

	@RestService(method="POST", uri="/probbo/add" , authenticated=false)
	public void updateProbbo(@RestParam(value="json")String json) {
		JSONObject jso;
		try {
			jso = new JSONObject(json);
			Map<String, Object> m = WebUtils.jsonObjectToMap(jso);
			dataSource.getCollection("probbos").insert(new BasicDBObject(m));
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	@RestService(method="GET", uri="/probbo/get", authenticated=false)
	public Map<String, Object> getProbbo(@RestParam(value="id")String id) {
		DBObject one = dataSource.getCollection("probbos").findOne(new BasicDBObject("id", id));
		if (one!=null) {
			return one.toMap();
		} else {
			return null;
		}
	}
}