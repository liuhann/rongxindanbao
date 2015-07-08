package com.ever365.rest;

import com.ever365.mongo.MongoDataSource;
import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.UUID;

public class CookieService {
    private MongoDataSource dataSource;
    public static final String ARG_TICKET = "365ticket";

    public void setDataSource(MongoDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Cookie setCookieTicket(HttpServletResponse resp) {
        Cookie cookie = new Cookie("365ticket", UUID.randomUUID().toString());
        cookie.setMaxAge(51840000);
        cookie.setPath("/");
        resp.addCookie(cookie);
        return cookie;
    }

    public String getCookieTicket(HttpServletRequest httpReq) {
        Cookie[] cookies = httpReq.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("365ticket")) {
                    return cookie.getValue();
                }
            }
        }
        return httpReq.getParameter("365ticket");
    }

    public void removeCookieTicket(HttpServletRequest request, HttpServletResponse response) {
        String ticket = getCookieTicket(request);
        if (ticket != null) {
            DBCollection cookiesCol = this.dataSource.getCollection("cookies");
            cookiesCol.remove(new BasicDBObject("ticket", ticket));
        }

        Cookie[] cookies = request.getCookies();
        if (cookies != null)
            for (Cookie cookie : cookies) {
                cookie.setValue("");
                cookie.setMaxAge(0);
                cookie.setPath("/");
                response.addCookie(cookie);
            }
    }

    public String getCurrentUser(HttpServletRequest request) {
        String ticket = getCookieTicket(request);
        if (ticket == null) return null;

        DBCollection cookiesCol = this.dataSource.getCollection("cookies");
        DBObject ticDoc = cookiesCol.findOne(new BasicDBObject("ticket", ticket));
        if (ticDoc == null) {
            return null;
        }
        return (String) ticDoc.get("user");
    }

    public String getCurrentAccessToken(HttpServletRequest request) {
        String ticket = getCookieTicket(request);
        if (ticket == null) return null;

        DBCollection cookiesCol = this.dataSource.getCollection("cookies");
        DBObject ticDoc = cookiesCol.findOne(new BasicDBObject("ticket", ticket));
        if (ticDoc == null) {
            return null;
        }
        return (String) ticDoc.get("at");
    }

    public void bindUserCookie(HttpServletRequest request, HttpServletResponse response, String username) {
        String ticket = getCookieTicket(request);
        DBCollection cookiesCol = this.dataSource.getCollection("cookies");
        if (ticket != null) {
            DBObject ticDoc = cookiesCol.findOne(new BasicDBObject("ticket", ticket));
            if (ticDoc == null) {
                cookiesCol.insert(new DBObject[]{BasicDBObjectBuilder.start()
                        .add("user", username).add("ticket", ticket)
                        .add("remote", request.getRemoteAddr())
                        .add("created", new Date()).get()});
            } else {
                ticDoc.put("user", username);
                cookiesCol.update(new BasicDBObject("ticket", ticket), ticDoc);
            }
        } else {
            Cookie newCookie = setCookieTicket(response);
            ticket = newCookie.getValue();
            cookiesCol.insert(new DBObject[]{BasicDBObjectBuilder.start()
                    .add("user", username).add("ticket", ticket)
                    .add("remote", WebContext.getRemoteAddr(request))
                    .add("agent", request.getHeader("User-Agent"))
                    .add("created", new Date()).get()});
        }
    }
}

