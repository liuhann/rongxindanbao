package com.ever365.fin;

import com.ever365.mongo.MongoDataSource;
import com.ever365.rest.*;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public class EliyouService {

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

        if(AuthenticationUtil.getCurrentUser()==null) {
            dataSource.getCollection("wxbinding").findOne(new BasicDBObject("openid", ""));


        } else {

        }

        return rr;
    }

    private void checkPassword(String uid, String pwd) {

    }
}
