package com.ever365.fin;

import com.ever365.mongo.MongoDataSource;
import com.ever365.rest.*;
import com.ever365.utils.RandomCodeServlet;
import com.ever365.utils.WebUtils;
import com.mongodb.BasicDBObject;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class EliyouService {


    Logger logger = Logger.getLogger(EliyouService.class.getName());

    private String eliyouServer = "http://221.218.37.133:8086";

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

    @RestService(method="GET", uri="/eliyou/wx/recents", authenticated=false, rndcode=false)
    public List<Object> getRecentProjects(@RestParam(value = "maxResult") Integer max,
                                          @RestParam(value = "page") Integer page) {
        String requestUrl = eliyouServer + "/eLiYou/wechat/porductMore.do?maxResult=" + max
                + "&page=" + page;
        String json = WebUtils.getString(requestUrl);
        try {
            JSONArray ja = new JSONArray(json);
            return WebUtils.jsonArrayToList(ja);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new ArrayList<Object>(0);
    }

    @RestService(method="GET", uri="/eliyou/wx/uinfos", authenticated=true, rndcode=false)
    public Map<String, Object> getUserInfos() {
        String requestUrl = eliyouServer + "/eLiYou/wechat/rechargeIndex.do?userAccount=" + AuthenticationUtil.getCurrentUser();
        String json = WebUtils.getString(requestUrl);
        try {
            JSONObject jo = new JSONObject(json);
            return WebUtils.jsonObjectToMap(jo);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new HashMap<String,Object>(0);
    }

    private void checkPassword(String uid, String pwd) {
        String url = eliyouServer + "/eLiYou/wechat/loginCheck.do";
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("userAccount", uid);
        params.put("password", pwd);

        logger.info("login to eliyou  url=" + url + "  u:" + uid + " p:" + pwd);
        JSONObject result = WebUtils.doPost(url, params);

        logger.info("login result " + result);

        if (result!=null && result.has("msg")) {
            try {
                String e = null;
                e = result.getString("msg");
                if ("成功".equals(e)) {
                    return;
                }
            } catch (JSONException e1) {
            }
        }
        throw new HttpStatusException(HttpStatus.UNAUTHORIZED);

    }
}
