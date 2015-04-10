package com.ever365.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;

import org.springframework.util.FileCopyUtils;

import com.baidu.inf.iis.bcs.BaiduBCS;
import com.baidu.inf.iis.bcs.auth.BCSCredentials;
import com.baidu.inf.iis.bcs.model.DownloadObject;
import com.baidu.inf.iis.bcs.model.ObjectMetadata;
import com.baidu.inf.iis.bcs.model.Resource;
import com.baidu.inf.iis.bcs.request.CopyObjectRequest;
import com.baidu.inf.iis.bcs.request.DeleteObjectRequest;
import com.baidu.inf.iis.bcs.request.GetObjectRequest;
import com.baidu.inf.iis.bcs.request.PutObjectRequest;
import com.baidu.inf.iis.bcs.response.BaiduBCSResponse;
import com.ever365.rest.StreamObject;

public class ContentStore {
	private static final String SLASH = "/";
	private String appKey;
	private String appSecret;
	private String bcsHost;
	private String bucketName;
	private BaiduBCS baiduBCS;
	private String localPath;
	private static Logger logger = Logger.getLogger(ContentStore.class
			.getName());

	public void setLocalPath(String localPath) {
		this.localPath = localPath;
	}

	public void setBucketName(String bucketName) {
		this.bucketName = bucketName;
	}

	public void setAppKey(String appKey) {
		this.appKey = appKey;
	}

	public void setBcsHost(String bcsHost) {
		this.bcsHost = bcsHost;
	}

	public void setAppSecret(String appSecret) {
		this.appSecret = appSecret;
	}

	public void copyContent(String uid, String newUid) {
		if (this.localPath != null) {
			File f = new File(this.localPath, uid);
			if (f.exists())
				try {
					putContent(uid, new FileInputStream(f), null, f.length());
				} catch (FileNotFoundException localFileNotFoundException) {
				}
		} else {
			CopyObjectRequest copyObjectRequest = new CopyObjectRequest(
					new Resource(this.bucketName, uid), new Resource(
							this.bucketName, newUid));
			getBCS().copyObject(copyObjectRequest);
		}
	}

	public void deleteContent(String uid) {
		if (this.localPath != null) {
			File f = new File(this.localPath, uid);
			if (f.exists())
				f.delete();
		} else {
			DeleteObjectRequest deleteObjectRequest = new DeleteObjectRequest(
					this.bucketName, uid);
			getBCS().deleteObject(deleteObjectRequest);
		}
	}

	public StreamObject getContentData(String uid) {
		StreamObject so = new StreamObject();
		if (this.localPath != null) {
			File f = new File(this.localPath, uid);
			if (f.exists()) {
				try {
					so.setFileName(f.getName());
					so.setInputStream(new FileInputStream(f));
					so.setLastModified(f.lastModified());
					so.setSize(f.length());
					return so;
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
			}
			return null;
		}
		if (!uid.startsWith("/")) {
			uid = "/" + uid;
		}
		GetObjectRequest getObjectRequest = new GetObjectRequest(
				this.bucketName, uid);
		BaiduBCSResponse result = getBCS().getObject(getObjectRequest);
		so.setInputStream(((DownloadObject) result.getResult()).getContent());
		so.setLastModified(((DownloadObject) result.getResult())
				.getObjectMetadata().getLastModified().getTime());
		so.setSize(((DownloadObject) result.getResult()).getObjectMetadata()
				.getContentLength());
		return so;
	}

	public String putContent(String uid, InputStream inputStream,
			String contentType, long size) {
		if (this.localPath != null) {
			File f = new File(this.localPath, uid);
			try {
				if (!f.getParentFile().exists()) {
					f.getParentFile().mkdirs();
				}
				boolean created = f.createNewFile();
				if (!created) {
					throw new IOException("File can not be created");
				}
				FileCopyUtils.copy(inputStream, new FileOutputStream(f));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return uid;
		}
		ObjectMetadata objectMetadata = new ObjectMetadata();
		objectMetadata.setContentType(contentType);
		objectMetadata.setContentLength(size);
		String object = "/" + uid;

		BaiduBCS baiduBCS = getBCS();

		PutObjectRequest request = new PutObjectRequest(this.bucketName,
				object, inputStream, objectMetadata);
		ObjectMetadata result = (ObjectMetadata) baiduBCS.putObject(request)
				.getResult();
		return object;
	}

	public BaiduBCS getBCS() {
		if (this.baiduBCS == null) {
			synchronized (this) {
				if (this.baiduBCS == null) {
					BCSCredentials credentials = new BCSCredentials(
							this.appKey, this.appSecret);
					this.baiduBCS = new BaiduBCS(credentials, this.bcsHost);
					this.baiduBCS.setDefaultEncoding("UTF-8");
				}
			}
		}
		return this.baiduBCS;
	}
}
