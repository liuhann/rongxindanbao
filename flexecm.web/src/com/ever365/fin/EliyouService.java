package com.ever365.fin;

import com.ever365.mongo.MongoDataSource;
import com.ever365.rest.*;
import com.ever365.utils.RandomCodeServlet;
import com.ever365.utils.StringUtils;
import com.mongodb.BasicDBObject;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import com.ever365.utils.WebUtils;
import org.springframework.util.FileCopyUtils;

public class EliyouService {

    Logger logger = Logger.getLogger(EliyouService.class.getName());

    private String eliyouServer = "http://61.48.62.99:8086";

    private MongoDataSource dataSource;

    private Long refreshTime = 60 * 60 * 1000L;

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


    @RestService(method="GET", uri="/eliyou/wx/signature", authenticated=false)
    public Map<String, Object> getWeixinConfigs(@RestParam(value = "url")String url) {
        String jsapiTicket = getWeixinJSTicket();
        if (jsapiTicket!=null) {
            logger.info("jsapi ticket: " + jsapiTicket);
            String noncestr = StringUtils.getRandString(10);
            String timestamp = new Long(System.currentTimeMillis()).toString();
            Map<String, Object> result = new HashMap<String, Object>();
            try {
                String signature = StringUtils.sha1("jsapi_ticket=" + jsapiTicket + "&noncestr=" + noncestr + "&timestamp=" + timestamp + "&url=" + url);
                logger.info("sig: " + signature + "   ts: " + timestamp + "   ns: " + noncestr);
                result.put("timestamp", timestamp);
                result.put("nonceStr", noncestr);
                result.put("signature", signature);
                return result;
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return new HashMap<String,Object>(0);
    }

    private Long tokenTime = 0L;
    private String weixinToken = null;
    @RestService(method="GET", uri="/eliyou/wx/token", authenticated=false)
    public synchronized String getWeixinToken() {
        if (System.currentTimeMillis()-tokenTime > refreshTime) {
            //重新获取签名
            String tokenUrl = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=wxaeeab45e6d45524b&secret=8c1ad314d1b5cc2508ecb1d1042afe5e";
            JSONObject tokenJson = WebUtils.doGet(tokenUrl);

            logger.info("token result " + tokenJson.toString());
            if (tokenJson!=null && tokenJson.has("access_token")) {
                try {
                    weixinToken = tokenJson.getString("access_token");
                    tokenTime = System.currentTimeMillis();
                } catch (JSONException e) {
                    e.printStackTrace();
                    weixinToken = null;
                    tokenTime = 0L;
                }
            }
        }
        return weixinToken;
    }

    private Long ticketTime = 0L;
    private String jsapiTicket = null;
    @RestService(method="GET", uri="/eliyou/wx/js/ticket", authenticated=false)
    public synchronized String getWeixinJSTicket() {
        if (System.currentTimeMillis()-ticketTime > refreshTime) {
            try {
                String apiTicketUrl = "https://api.weixin.qq.com/cgi-bin/ticket/getticket?access_token=" + getWeixinToken() + "&type=jsapi";
                JSONObject ticketJSON = WebUtils.doGet(apiTicketUrl);
                logger.info("ticket result  " + ticketJSON.toString());
                if (ticketJSON!=null && ticketJSON.has("ticket")) {
                    jsapiTicket = ticketJSON.getString("ticket");
                    ticketTime = System.currentTimeMillis();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                jsapiTicket = null;
                ticketTime = 0L;
            }
        }
        return jsapiTicket;
    }

    @RestService(method="POST", uri="/eliyou/alipay/add", authenticated=true, rndcode=false)
    public void postAlipay(@RestParam(value="picid")String picid, @RestParam(value="money")Integer money,
                           @RestParam(value="alipay")String account) {
        //首先下载文件
        File temp = WebUtils.downloadFile("http://file.api.weixin.qq.com/cgi-bin/media/get?access_token=" + getWeixinToken() + "&media_id=" + picid);
        try {
            // 创建临时文件
            //在程序退出时删除临时文件
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("userAccount", AuthenticationUtil.getCurrentUser());
            params.put("money", money.toString());
            params.put("alipayAccount", account);
            params.put("fileName", temp);
            WebUtils.multiPartPost(eliyouServer + "/eLiYou/wechat/saveRecharge.do", params);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (temp!=null) {
                temp.delete();
            }
        }
    }

    @RestService(method="POST", uri="/eliyou/recharge", authenticated=false, rndcode=false)
    public Map<String, Object> saveRacharge(@RestParam(value="money")Integer money) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("userAccount", AuthenticationUtil.getCurrentUser());
        params.put("money", money.toString());
        params.put("url", "http://eliyou.luckyna.com/wx/me.html");
        JSONObject result = WebUtils.doPost(eliyouServer + "/eLiYou/wechat/savewyRecharge.do", params);

        logger.info(result.toString());
        return WebUtils.jsonObjectToMap(result);
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
