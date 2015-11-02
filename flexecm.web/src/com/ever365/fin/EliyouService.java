package com.ever365.fin;

import com.ever365.auth.OAuthServlet;
import com.ever365.mongo.MongoDataSource;
import com.ever365.rest.*;
import com.ever365.utils.MapUtils;
import com.ever365.utils.RandomCodeServlet;
import com.ever365.utils.StringUtils;
import com.ever365.utils.WebUtils;
import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.logging.Logger;

public class EliyouService {

    Logger logger = Logger.getLogger(EliyouService.class.getName());

    private String eliyouServer = "http://123.57.54.16:8080/eliyou/wechat";

    private String weixinServer = "http://eliyou.luckyna.com";

    private MongoDataSource dataSource;

    private Long refreshTime = 60 * 60 * 1000L;

    public String tRechargeOK = "X519Mnuma8Sij9j0hRC8nn_FyBQLJ9P7BJzbn-NzE_Y";
    public String tInvestSucess = "JMvfH3xhLUlF-cZ5OQYVSCTB2MTR0_24Wt-orolCnOU";
    public String tWeixinBinding = "73Mk9OPU-vBR2V288TWuP28fxrMLL5Wm9RpXh94XO5Y";

    public void setDataSource(MongoDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void setEliyouServer(String eliyouServer) {
        this.eliyouServer = eliyouServer;
    }

    public void setWeixinServer(String weixinServer) {
        this.weixinServer = weixinServer;
    }

    public void setRefreshTime(Long refreshTime) {
        this.refreshTime = refreshTime;
    }

    @RestService(method="POST", uri="/eliyou/wx/login", authenticated=false, rndcode=false)
    public RestResult login(@RestParam(value="loginid")String uid, @RestParam(value="pwd") String pwd) {
        RestResult rr = new RestResult();
        if (uid==null || pwd==null) {
            throw new HttpStatusException(HttpStatus.BAD_REQUEST);
        }
        uid = checkPassword(uid, pwd);
        rr.setSession(AuthenticationUtil.SESSION_CURRENT_USER, uid);
        return rr;
    }

    @RestService(method="POST", uri="/eliyou/wx/register", authenticated=false, rndcode=false)
    public RestResult register(@RestParam(value="loginid")String uid, @RestParam(value="pwd") String pwd
            ,@RestParam(value="ufcode") String ufcode) {
        RestResult rr = new RestResult();
        if (uid==null || pwd==null) {
            throw new HttpStatusException(HttpStatus.BAD_REQUEST);
        }

        String url = eliyouServer + "/register.do";
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("userid", uid);
        params.put("password", pwd);
        if (ufcode==null || !ufcode.equals("")) {
            params.put("ufcode", ufcode);
        }

        logger.info("register to eliyou  url=" + url + "  u:" + uid + " p:" + pwd + " ufcode: " + ufcode);

        JSONObject result = WebUtils.doPost(url, params);

        logger.info("login result " + result.toString());

        if (result!=null && result.has("code")) {
            try {
                String e = null;
                e = result.getString("code");
                if ("01".equals(e)) {
                    rr.setSession(AuthenticationUtil.SESSION_CURRENT_USER, uid);
                    rr.setSession(OAuthServlet.OAUTH_REDIRECT, "/wx/openqdd.html");
                    rr.setResult("OK");
                    return rr;
                } else {
                    rr.setResult(result.getString("msg"));
                    return rr;

                    //throw new HttpStatusException(HttpStatus.CONFLICT, result.getString("msg"));
                }
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
        }
        throw new HttpStatusException(HttpStatus.CONFLICT);
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

        String requestUrl = eliyouServer + "/porductMore.do?maxResult=" + max  + "&page=" + page;

        logger.info("list products : " + requestUrl);

        String json = WebUtils.getString(requestUrl);
        try {
            JSONArray ja = new JSONArray(json);
            return WebUtils.jsonArrayToList(ja);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new ArrayList<Object>(0);
    }

    @RestService(method="GET", uri="/eliyou/project/detail", authenticated=false, rndcode=false)
    public Map<String,Object> getProjectDetail(@RestParam(value = "id") String id) throws UnsupportedEncodingException {
        String requestUrl = eliyouServer + "/detail.do?id=" + id;
        String json = WebUtils.getString(requestUrl);
        try {
            JSONObject jo = new JSONObject(json);
            Map<String, Object> result = WebUtils.jsonObjectToMap(jo);

            if (AuthenticationUtil.getCurrentUser()==null) {
                result.put("uinf", null);
            } else {
                result.put("uinf", getUserInfos());
            }
            result.put("now", System.currentTimeMillis());
            return result;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new HashMap(0);
    }

    @RestService(method="GET", uri="/eliyou/project/invest", authenticated=true, rndcode=false)
    public Map<String,Object> invest(@RestParam(value = "id") String id,@RestParam(value = "money") String money) {
        String requestUrl = eliyouServer + "/addInvest.do";

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("userAccount", AuthenticationUtil.getCurrentUser());
        params.put("money", money);
        params.put("projectId", id);
        params.put("url", weixinServer + "/service/eliyou/invest/back");
        JSONObject result = WebUtils.doPost(eliyouServer + "/addInvest.do", params);

        logger.info(result.toString());
        dataSource.getCollection("invests").update(new BasicDBObject("user", AuthenticationUtil.getCurrentUser()),
                BasicDBObjectBuilder.start("user",AuthenticationUtil.getCurrentUser()).add("project",id).add("money", money)
                .add("time", System.currentTimeMillis()).get(), true, false);
        return WebUtils.jsonObjectToMap(result);
    }

    @RestService(method="POST", uri="/eliyou/invest/back", authenticated=false, rndcode=false)
    public RestResult investCallBack(Map result) throws UnsupportedEncodingException {
        logger.info("recharge callback " + new JSONObject(result).toString());
        RestResult rr = new RestResult();

        logger.info("ResultCode: " + result.get("ResultCode"));
        DBObject one = dataSource.getCollection("invests").findOne(new BasicDBObject("user", AuthenticationUtil.getCurrentUser()));

        if ("88".equals(result.get("ResultCode"))) {

            List<Map<String, String>> datas = new ArrayList();
            datas.add(MapUtils.tribleMap("key","first","value", "恭喜您抢投成功，您已成功投资，明日开始计息。", "color", "#173177"));
            datas.add(MapUtils.tribleMap("key","keyword1","value",(String)result.get("Amount"), "color", "#173177"));
            datas.add(MapUtils.tribleMap("key","keyword2","value",StringUtils.formateDate(new Date()), "color", "#173177"));
            datas.add(MapUtils.tribleMap("key","remark","value","赶快去投资吧！", "color", "#173177"));

            DBCursor cur = dataSource.getCollection("weixin").find(new BasicDBObject("userId", AuthenticationUtil.getCurrentUser()));
            while(cur.hasNext()) {
                DBObject dbo = cur.next();
                sendWeixinTemplateInfo(dbo.get("openid").toString(), tRechargeOK, datas);
            }

            if (one!=null) {
                rr.setRedirect("/wx/project.html?id=" + one.get("project").toString() + "&invest=1");
            } else {
                rr.setRedirect("/wx/recents.html");
            }
        } else {
            if (one!=null) {
                rr.setRedirect("/wx/project.html?id=" + one.get("project").toString() + "&investfail=1");
            } else {
                rr.setRedirect("/wx/recents.html");
            }
        }
        return rr;
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
            WebUtils.multiPartPost(eliyouServer + "/saveRecharge.do", params);
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
        params.put("url", weixinServer + "/service/eliyou/recharge/back");
        JSONObject result = WebUtils.doPost(eliyouServer + "/savewyRecharge.do", params);

        if (result==null) {
            return MapUtils.newMap("error", (Object)"json invalid");
        }
        logger.info(result.toString());
        return WebUtils.jsonObjectToMap(result);
    }

    @RestService(method="POST", uri="/eliyou/recharge/back", authenticated=false, rndcode=false)
    public RestResult rechageCallBack(Map result) throws UnsupportedEncodingException {
        logger.info("recharge callback " + new JSONObject(result).toString());
        RestResult rr = new RestResult();

        logger.info("ResultCode: " + result.get("ResultCode"));

        if ("88".equals(result.get("ResultCode"))) {
            List<Map<String, String>> datas = new ArrayList();
            datas.add(MapUtils.tribleMap("key","first","value", "您的乾多多账户充值成功", "color", "#173177"));
            datas.add(MapUtils.tribleMap("key","keyword1","value",(String)result.get("Amount"), "color", "#173177"));
            datas.add(MapUtils.tribleMap("key","keyword2","value",StringUtils.formateDate(new Date()), "color", "#173177"));
            datas.add(MapUtils.tribleMap("key","remark","value","赶快去投资吧！", "color", "#173177"));

            DBCursor cur = dataSource.getCollection("weixin").find(new BasicDBObject("userId", AuthenticationUtil.getCurrentUser()));
            while(cur.hasNext()) {
                DBObject dbo = cur.next();
                sendWeixinTemplateInfo(dbo.get("openid").toString(), tRechargeOK, datas);
            }

            rr.setRedirect("/wx/rechargeok.html");
        } else {
            rr.setRedirect("/wx/rechargefail.html");
        }
        return rr;
    }

    @RestService(method="POST", uri="/eliyou/qiandd/register", authenticated=true, rndcode=false)
    public Map<String, Object> registerQianDD(@RestParam(value="return") String returnUrl) {
        if (returnUrl==null) {
            returnUrl = "/wx/recents.html";
        }
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("userAccount", AuthenticationUtil.getCurrentUser());
        params.put("url", weixinServer + returnUrl);
        JSONObject result = WebUtils.doPost(eliyouServer + "/registerBind.do", params);

        logger.info(result.toString());
        return WebUtils.jsonObjectToMap(result);
    }

    @RestService(method="GET", uri="/eliyou/wx/uinfos", authenticated=false)
    public Map<String, Object> getUserInfos() throws UnsupportedEncodingException {
        if (AuthenticationUtil.getCurrentUser()==null) {
            return new HashMap<String,Object>(0);
        }


        String requestUrl = eliyouServer + "/rechargeIndex.do?userAccount=" + URLEncoder.encode(AuthenticationUtil.getCurrentUser(), "UTF-8");
        String json = WebUtils.getString(requestUrl);
        try {
            JSONObject jo = new JSONObject(json);
            Map<String, Object> map = WebUtils.jsonObjectToMap(jo);
            map.put("cu", AuthenticationUtil.getCurrentUser());

            DBObject one = dataSource.getCollection("weixin").findOne(new BasicDBObject("userId", AuthenticationUtil.getCurrentUser()));
            if (one!=null) {
                map.put("openid", one.get("openid"));
            }

            return map;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new HashMap<String,Object>(0);
    }

    @RestService(method="GET", uri="/eliyou/wx/hongbao/add", authenticated=false)
    public Map<String, Object> addRedPackage(@RestParam(value="mobile") String mobile, @RestParam(value="total") String total){
        Map<String, Object> rp = getPackageByMobileNO(mobile);
        if ("01".equals(rp.get("code"))) {
            return rp;
        }
        String requestUrl = eliyouServer + "/addRedPacket.do?mobileNo=" + mobile + "&moneyTotal=" + total;
        String json = WebUtils.getString(requestUrl);
        try {
            JSONObject jo = new JSONObject(json);
            Map<String, Object> map = WebUtils.jsonObjectToMap(jo);
            return map;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new HashMap<String,Object>(0);
    }

    @RestService(method="GET", uri="/eliyou/wx/hongbao/get", authenticated=false)
    public Map<String, Object> getPackageByMobileNO(@RestParam(value="mobile") String mobile){
        String requestUrl = eliyouServer + "/getByMobileNo.do?mobileNo=" + mobile;
        String json = WebUtils.getString(requestUrl);
        try {
            JSONObject jo = new JSONObject(json);
            Map<String, Object> map = WebUtils.jsonObjectToMap(jo);
            return map;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new HashMap<String,Object>(0);
    }

    @RestService(method="GET", uri="/eliyou/wx/hongbao/use", authenticated=false)
    public Map<String, Object> transferPackage(@RestParam(value="id") String id){
        String requestUrl = eliyouServer + "/toTransfer.do?id=" + id;
        String json = WebUtils.getString(requestUrl);
        try {
            JSONObject jo = new JSONObject(json);
            Map<String, Object> map = WebUtils.jsonObjectToMap(jo);
            return map;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new HashMap<String,Object>(0);
    }

    public void sendWeixinBindingNotify(String user) {
        try {
            List<Map<String, String>> datas = new ArrayList<Map<String, String>>();
            datas.add(MapUtils.tribleMap("key","first","value", "您好，恭喜您账户绑定成功！", "color", "#173177"));
            datas.add(MapUtils.tribleMap("key","name1","value","e利友", "color", "#173177"));
            datas.add(MapUtils.tribleMap("key","name2","value","微信", "color", "#173177"));
            datas.add(MapUtils.tribleMap("key","time","value",StringUtils.formateDate(new Date()), "color", "#173177"));
            datas.add(MapUtils.tribleMap("key","remark","value","您可以使用下方微信菜单进行更多体验。如需解绑，点击退出账户即可！ ", "color", "#173177"));

            DBCursor cur = dataSource.getCollection("weixin").find(new BasicDBObject("userId", user));
            while(cur.hasNext()) {
                DBObject dbo = cur.next();
                logger.info("send binding info for " + user + "  openid: " + dbo.get("openid").toString());
                sendWeixinTemplateInfo(dbo.get("openid").toString(), tWeixinBinding, datas);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void sendWeixinTemplateInfo(String toUser, String templateId, List<Map<String,String>> datas) {

        Map<String, Object> content = new HashMap<String,Object>();
        content.put("touser", toUser);
        content.put("template_id", templateId);
        content.put("url", weixinServer + "/wx/me.html");
        content.put("topcolor", "FF0000");

        Map<String, Object> data = new HashMap<String, Object>();

        for(Map<String,String> tk: datas) {
            String key = tk.get("key");
            tk.remove("key");
            data.put(key, tk);
        }
        content.put("data", data);
        String url= "https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=" + getWeixinToken();
        String body = new JSONObject(content).toString();
        logger.info("send template: url="  + url + " body " + body);
        WebUtils.doPost(url, body);
    }

    private String checkPassword(String uid, String pwd) {
        String url = eliyouServer + "/loginCheck.do";
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
                    if (result.has("userAccount")) {
                        return result.getString("userAccount");
                    } else {
                        return uid;
                    }
                }
            } catch (JSONException e1){
            }
        }
        throw new HttpStatusException(HttpStatus.UNAUTHORIZED);
    }

}
