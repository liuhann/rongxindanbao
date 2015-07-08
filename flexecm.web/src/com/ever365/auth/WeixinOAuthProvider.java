package com.ever365.auth;

import com.ever365.utils.WebUtils;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class WeixinOAuthProvider implements OAuthProvider {

    Logger logger = Logger.getLogger(WeixinOAuthProvider.class.getName());

    @Override
    public String getCode() {
        return null;
    }

    @Override
    public Map<String, Object> authorize(String code) {
        String url = "https://api.weixin.qq.com/sns/oauth2/access_token";
        String cliend_id = "wxaeeab45e6d45524b";
        String client_secret = "8c1ad314d1b5cc2508ecb1d1042afe5e";

        try {
            logger.info("requesting weixin for at " + code) ;
            JSONObject jso = WebUtils.doGet(url + "?appid=" + cliend_id + "&secret=" + client_secret
                    + "&code=" + code + "&grant_type=authorization_code");
            if (jso.has("errcode")) {
                logger.info("error" + jso.toString());
                return null;
            } else {
                return WebUtils.jsonObjectToMap(jso);
            }
        } catch (Exception e ) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean binding() {
        return true;
    }

    @Override
    public String getName() {
        return "weixin";
    }
}
