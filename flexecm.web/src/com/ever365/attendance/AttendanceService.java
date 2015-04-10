package com.ever365.attendance;

import java.util.Map;

import com.ever365.auth.Tenantable;
import com.ever365.mongo.MongoDataSource;
import com.ever365.rest.RestParam;
import com.ever365.rest.RestService;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public class AttendanceService implements Tenantable {

	private MongoDataSource dataSource;
	
	public MongoDataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(MongoDataSource dataSource) {
		this.dataSource = dataSource;
	}

	@RestService(method="POST", uri="/fa/org/reg")
	private void orgRegister(
			Map<String, Object> req 
			) {
		dataSource.getCollection("company").insert(new BasicDBObject(req));
	}
	
	@RestService(method="GET", uri="/fa/org/login")
	private String login(
			@RestParam(value="uid") String uid, @RestParam(value="pwd") String pwd   
			) {
		DBObject u = dataSource.getCollection("company").findOne(new BasicDBObject("uid", uid));
		
		if (u.get("pwd").equals(pwd)) {
			return "1";
		}
		return "0";
	}
	
	
	
}
