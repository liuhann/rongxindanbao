package com.ever365.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.logging.Logger;

import com.ever365.mongo.MongoDataSource;
import com.ever365.rest.AuthenticationUtil;
import com.ever365.utils.StringUtils;
import com.ever365.utils.WebUtils;
import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBObject;
import org.springframework.util.FileCopyUtils;

import com.ever365.rest.StreamObject;
import com.ever365.utils.UUID;

public class LocalContentStore {
	private static final String SLASH = "/";
	private String localPath;
	private static Logger logger = Logger.getLogger(LocalContentStore.class
			.getName());
	private MongoDataSource dataSource = null;
	public void setLocalPath(String localPath) {
		this.localPath = localPath;
	}

	public void setDataSource(MongoDataSource dataSource) {
		this.dataSource = dataSource;
	}

	public String copyContent(String uid) {
		if (this.localPath != null) {
			File f = new File(this.localPath + uid);
			if (f.exists())
				try {
					return putContent(new FileInputStream(f), null, f.length(), null);
				} catch (FileNotFoundException localFileNotFoundException) {
				}
		} 
		return null;
	}

	public void deleteContent(String uid) {
		if (this.localPath != null) {
			File f = new File(this.localPath, uid);
			if (f.exists())
				f.delete();
		} else {
			
		}
	}

	public StreamObject getContentData(String uid) {
		StreamObject so = new StreamObject();
		if (this.localPath != null) {
			File f = new File(this.localPath +  uid);
			if (f.exists()) {
				try {
					DBObject dbo = dataSource.getCollection("contents").findOne(new BasicDBObject("url", uid));
					so.setInputStream(new FileInputStream(f));
					if (dbo!=null) {
						so.setFileName((String)dbo.get("name"));
						so.setLastModified((Long)dbo.get("undated"));
						so.setMimeType((String)dbo.get("type"));
					}
					so.setSize(f.length());
					so.setRaw(f);
					return so;
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				logger.info("File Not found "  + this.localPath +  uid);
			}
		}
		return null;
	}
	
	public File getEmptyFile() {
		if (this.localPath != null) {
			Date d = new Date();
			String path = "/" + d.getYear() + "/" + d.getMonth() + "/" + d.getDate(); 
			String uid = path + "/" + UUID.generate();
			File f = new File(this.localPath + uid);
			if (!f.getParentFile().exists()) {
				f.getParentFile().mkdirs();
			}
			boolean created;
			try {
				created = f.createNewFile();
				if (!created) {
					throw new IOException("File can not be created");
				}
				return f;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public String putContent(InputStream inputStream, String contentType, long size, String name) {
		if (this.localPath != null) {
			Date d = new Date();
			String path = "/" + d.getYear() + "/" + d.getMonth() + "/" + d.getDate() + "/" + StringUtils.getRandString(5);
			String uid = path + "/file.data";
			
			File f = new File(this.localPath + uid);
			try {
				if (!f.getParentFile().exists()) {
					f.getParentFile().mkdirs();
				}
				boolean created = f.createNewFile();
				if (!created) {
					throw new IOException("File can not be created");
				}
				FileCopyUtils.copy(inputStream, new FileOutputStream(f));
				dataSource.getCollection("contents").insert(BasicDBObjectBuilder.start().append("undated", System.currentTimeMillis())
						.append("user", AuthenticationUtil.getCurrentUser())
						.append("name", name).append("type", contentType).append("size", size).append("url", uid).get());
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return uid;
		} else {
			return "";
		}
	}
	
}
