package com.ever365.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.logging.Logger;

import org.springframework.util.FileCopyUtils;

import com.ever365.rest.StreamObject;
import com.ever365.utils.UUID;

public class LocalContentStore {
	private static final String SLASH = "/";
	private String localPath;
	private static Logger logger = Logger.getLogger(LocalContentStore.class
			.getName());

	public void setLocalPath(String localPath) {
		this.localPath = localPath;
	}

	public String copyContent(String uid) {
		if (this.localPath != null) {
			File f = new File(this.localPath + uid);
			if (f.exists())
				try {
					return putContent(new FileInputStream(f), null, f.length());
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
					so.setFileName(f.getName());
					so.setInputStream(new FileInputStream(f));
					so.setLastModified(f.lastModified());
					so.setSize(f.length());
					so.setRaw(f);
					return so;
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
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

	public String putContent(InputStream inputStream, String contentType, long size) {
		if (this.localPath != null) {
			
			Date d = new Date();
			
			String path = "/" + d.getYear() + "/" + d.getMonth() + "/" + d.getDate(); 
			String uid = path + "/" + UUID.generate();
			
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
