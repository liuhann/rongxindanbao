package com.ever365.auth;

import com.ever365.utils.WebUtils;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class WeixinOAuthProvider implements OAuthProvider {

    @Override
    public String getCode() {
        return null;
    }

    @Override
    public Map<String, Object> authorize(String code) {

        String url = "https://api.weixin.qq.com/sns/oauth2/access_token";
        String cliend_id = "wx8a0194e17ffb8781";
        String client_secret = "bb1876512af9aab7143925030c765fb7";

        try {
            JSONObject jso = WebUtils.doGet(url + "?appid=" + cliend_id + "&secret=" + client_secret
                    + "&code=" + code + "&grant_type=authorization_code");
            if (jso.has("errcode")) {
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
    public String getName() {
        return null;
    }
}
