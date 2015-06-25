package com.ever365.common;

import java.io.InputStream;
import java.util.Date;
import java.util.Map;

import com.ever365.mongo.MongoDataSource;
import com.ever365.rest.RestParam;
import com.ever365.rest.RestService;
import com.ever365.rest.StreamObject;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public class MediaService {

	public static final String COLL_ATTACHMENT = "attachments";
	
	private MongoDataSource dataSource;
	private LocalContentStore contentStore;
	
	public void setContentStore(LocalContentStore contentStore) {
		this.contentStore = contentStore;
	}

	public void setDataSource(MongoDataSource dataSource) {
		this.dataSource = dataSource;
	}
	
	public MongoDataSource getDataSource() {
		return dataSource;
	}

	@RestService(uri="/attachz/upload", method="POST", multipart=true)
	public String uploadFile(@RestParam("file") InputStream is,@RestParam("size") Long size, @RestParam("name") String name ) {
		String uid = this.contentStore.putContent(is, "image/png", size.longValue(), name);
		
		DBObject dbo = new BasicDBObject();
		dbo.put("uid", uid);
		dbo.put("size", size);
		dbo.put("created", new Date().getTime());

		dataSource.getCollection(COLL_ATTACHMENT).insert(dbo);
		/*
		try {
			Builder<File> of = Thumbnails.of(contentStore.getContentData(uid).getRaw()).size(100, 100);
			File thumbFile = contentStore.getEmptyFile();
			of.toFile(thumbFile);
			dbo.put("thumb100x100", thumbFile.getPath());
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("upload file " + dbo.toString());
		*/
		
		return uid;
	}
	@RestService(uri="/attachz/remove", method="POST")
	public void remove(@RestParam("uid") String uid) {
		dataSource.getCollection(COLL_ATTACHMENT).remove(new BasicDBObject("uid", uid));
		contentStore.deleteContent(uid);
	}
	
	@RestService(uri="/attachz/upload/list", method="GET", authenticated=true)
	public Map<String, Object> getUploads(@RestParam(value="skip") Integer skip, @RestParam(value="limit") Integer limit) {
		return dataSource.filterCollectoin(COLL_ATTACHMENT, null, null, skip, limit);
	}
	
	@RestService(uri="/attachz/preview", method="GET", authenticated=false)
	public StreamObject getPreview(@RestParam("id") String id) {
		return this.contentStore.getContentData(id);
	}
	
	public void update(String coll, DBObject dbo) {
		if (dbo.get("_id")==null) {
			dataSource.getCollection(coll).insert(dbo);
		} else {
			dataSource.getCollection(coll).update(new BasicDBObject("_id", dbo.get("_id")), dbo);
		}
	}
}
