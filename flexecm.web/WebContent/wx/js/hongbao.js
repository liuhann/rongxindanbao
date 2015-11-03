/**
 * Created by Administrator on 2015/11/2.
 */


var RedPackageHandler = (function() {

    function addHongbao(mobile, money, cb) {
        if (!isMobile(mobile)) {
            return cb();
        }
        $.getJSON("/service/eliyou/wx/hongbao/add", {
            'money': money,
            'mobile': mobile
        }, function(rr) {
            cb(rr);
        });
    }

    function useHongbao(id, cb) {
        $.getJSON("/service/eliyou/wx/hongbao/use", {
            'id': id
        }, function(rr) {
            cb(rr);
        });
    }

    function getHongbao(mobile, cb) {
        $.getJSON("/service/eliyou/wx/hongbao/get", {
            'mobile': mobile
        }, function(rr) {
            cb(rr);
        });
    }

    function isMobile(str) {
        var reg = /^(13|14|15|18|17)\d{9}$/;
        return reg.test(str);
    }

    return {
        addHongbao: addHongbao,
        useHongbao: useHongbao ,
        getHongbao: getHongbao
    }
}());
