package com.ever365.utils;

import org.apache.commons.httpclient.HttpClientError;

import com.ever365.mongo.MongoDataSource;
import com.ever365.rest.AuthenticationUtil;
import com.ever365.rest.HttpStatus;
import com.ever365.rest.HttpStatusException;
import com.ever365.rest.RestParam;
import com.ever365.rest.RestResult;
import com.ever365.rest.RestService;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public class EmailConfirmer {

	private MongoDataSource dataSource;

	public void setDataSource(MongoDataSource dataSource) {
		this.dataSource = dataSource;
	}
	
	
}
