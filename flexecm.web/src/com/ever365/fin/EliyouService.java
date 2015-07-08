package com.ever365.fin;

import com.ever365.mongo.MongoDataSource;
import com.ever365.rest.*;
import com.ever365.utils.RandomCodeServlet;
import com.ever365.utils.WebUtils;
import com.mongodb.BasicDBObject;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class EliyouService {

    Logger logger = Logger.getLogger(EliyouService.class.getName());

    private MongoDataSource dataSource;

    public void setDataSource(MongoDataSource dataSource) {
        this.dataSource = dataSource;
    }

    @RestService(method="POST", uri="/eliyou/wx/login", authenticated=false, rndcode=false)
    public RestResult login(@RestParam(value="loginid")String uid, @RestParam(value="pwd") String pwd) {
        RestResult rr = new RestResult();
        if (uid==null || pwd==null) {
            throw new HttpStatusException(HttpStatus.BAD_REQUEST);
        }

        checkPassword(uid, pwd);
        rr.setSession(AuthenticationUtil.SESSION_CURRENT_USER, uid);
        return rr;
    }

    @RestService(method="GET", uri="/eliyou/wx/logout", authenticated=true, rndcode=false)
    public RestResult logout() {
        RestResult rr = new RestResult();
        dataSource.getCollection("weixin").remove(new BasicDBObject("userId", AuthenticationUtil.getCurrentUser()));
        rr.setSession(AuthenticationUtil.SESSION_CURRENT_USER, null);
        rr.setSession(RandomCodeServlet.LOGIN_FAILED, null);
        rr.setRedirect("/wx/login.html");
        return rr;
    }

    private void checkPassword(String uid, String pwd) {
        String url = "http://111.199.15.26:8086/eLiYou/wechat/loginCheck.do";
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("userAccount", uid);
        params.put("password", pwd);

        logger.info("login to eliyou  url=" + url + "  u:" + uid + " p:" + pwd);
        JSONObject result = WebUtils.doPost(url, params);

        logger.info("login result " + result);

        if (result.has("result")) {
            try {
                String e = null;
                e = result.getString("result");
                if ("success".equals(e)) {
                    return;
                }
            } catch (JSONException e1) {
            }
        }
        throw new HttpStatusException(HttpStatus.UNAUTHORIZED);

    }
}
