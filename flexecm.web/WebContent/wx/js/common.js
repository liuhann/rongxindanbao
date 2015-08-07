/**
 * Created by Administrator on 2015/7/2.
 */
$(function() {
    var size = $(window).width() / 19;
    $("html").css("font-size", size + "px");
    $("body").css("font-size", size + "px");
    setInputClearable();
    //$(".wrapper").show();
    //$("body").append($('<div class="loader">正在加载...</div>'));
});


function display() {
    $(".loading").hide();
    $(".wrapper").show();
}


function getCurrentUser(cb) {
    $.getJSON("/service/fin/current", {}, function(d) {
        if (d.cu==null) {
            if (location.href.indexOf("login.html")==-1) {
                //首先尝试用微信账号登陆, 在回调中， 如果未绑定上会跳转到登陆页面
                location.href =
                    "https://open.weixin.qq.com/connect/oauth2/authorize?appid=wxaeeab45e6d45524b&redirect_uri="
                    + encodeURI("http://eliyou.luckyna.com/oauth/wx")
                    + "&response_type=code&scope=snsapi_base&state=" + location.pathname;
            }
        } else {
            $(".uname").html(d.cu);
            if (cb!=null) {
                cb();
            }
        }
    });
}

function setInputClearable() {

    $("input.cleanable").each(function() {

        $(this).next("span.cleanable").click(function() {
            $(this).prev("input").val("");
            $(this).hide();
        });
    })
    $("input.cleanable").focus(function() {
        if ($(this).val()!="") {
         $(this).next("span.cleanable").show();
        } else {
            $(this).next("span.cleanable").hide();
        }
    });
    $("input.cleanable").blur(function() {
        if ($(this).val()!="") {
            $(this).next("span.cleanable").show();
        } else {
            $(this).next("span.cleanable").hide();
        }
    });
}

function getStatus() {
    $.getJSON("/service/eliyou/wx/uinfos", {}, function(data){
        display();
        if (data.result=="success") {
            runToNumber($(".gained"),data.ljsy);
            runToNumber($(".showtotal"),data.zhzzc);
            runToNumber($(".mecapital"),data.dsbj);
            runToNumber($(".meprofit"),data.dssy);
            runToNumber($(".lockmoney"),data.djje);
            runToNumber($(".allinvest"),data.ljtz);
            runToNumber($(".ramaintotal"),data.kyje);
        }
        if (!data.moneyMoreAccount) {
            $(".meremains").hide();
            $(".regqian").show();
        }
    });
}

function registerQianDuoduo() {
    $.post("/service/eliyou/qiandd/register", {}, function(data) {
        var regInfo = JSON.parse(data);

        $("#LoanPlatformAccount").val(regInfo.obj.loanPlatformAccount);
        $("#PlatformMoneymoremore").val(regInfo.obj.platformMoneymoremore);
        $("#ReturnURL").val(regInfo.obj.returnURL);
        $("#SignInfo").val(regInfo.obj.signInfo);
        $("#NotifyURL").val(regInfo.obj.notifyURL);

        $("#form1").submit();
    });
}

function getRecents(max, page, cb) {

    $(".more").html("加载中......");
    $(".more").unbind();

    var repayInstalTypes = ["个月","天"];
    $.getJSON("/service/eliyou/wx/recents", {
        'maxResult': max,
        'page': page
    }, function(data){
        display();
        for(var i=0;i<data.length; i++) {
            $(".recents").after(cloned);

            var cloned = $(".template").clone().removeClass("template").addClass("project").show();
            cloned.data("project", data[i]);

            if (data[i].projectName) { //项目名称
                cloned.find(".title .name").html(data[i].projectName);
            }

            if (data[i].guaranteeCompanyModel && data[i].guaranteeCompanyModel.guaranteeCoName) {//担保机构
                cloned.find(".guarantee").html(data[i].guaranteeCompanyModel.guaranteeCoName);
            }

            if (data[i].investorRateYear) {
                cloned.find(".desc .profit span").html(data[i].investorRateYear); //年化
            }

            if (data[i].repayInstalType && data[i].repayInstalNum) { //借款期
                cloned.find(".desc .dura").html(data[i].repayInstalNum + repayInstalTypes[data[i].repayInstalType-1]); //还款期
                //cloned.find(".title .guarantee").html(data[i].guaranteeCompanyModel.guaranteeCoName);
            }

            if (data[i].capitalSumLower) {  //贷款金额
                cloned.find(".desc .total").html(formatMoney(data[i].capitalSumLower));
            }

            if (data[i].repayTypeModel) {  //还款方式
                cloned.find(".retmethod").html(data[i].repayTypeModel.typeName);
            }

            if (data[i].schedule) {  //申购进度
                //var num = data[i].schedule * 2.5;
                //cloned.find("svg").css("stroke-dasharray", num + "%,250%")
                cloned.find(".percent").html(data[i].schedule);
            }

            cloned.bindtouch(function(){
                /*
                $(".wrapper .recents, .wrapper .project,.wrapper .more").remove();
                $(".wrapper .head").after($(".projectInfo"));
                $(".projectInfo").show();
                $(".rbox .c").css("background", "-webkit-gradient(linear,0% 20%, 0% 100%, from(#FFFFFF), to(#C5DCFC), color-stop(0.02,#C5DCFC)) #fff");
                $(".wrapper").addClass("pd");

                initProjectInfo($(this).data("project"), $(".projectInfo"));
                */
                location.href = "project.html?id=" + $(this).data("project").id;

            });
        }
        //setTimeout(function() {
            $(".circle2").each(function(){
                var num = parseInt($(this).find("span.percent").html()) * 2.5;
                //$(this).find("svg").delay(500).velocity({ "stroke-dasharray": num + "%,250%"});
                $(this).find("svg").css("stroke-dasharray", num + "%,250%")
            });
        //}, 300);

        if (data.length>0) {
            $(".more").html("-查看更多-");
            $(".more").bindtouch(function() {
                getRecents(max, page+1);
            });
        } else {
            $(".more").html("没有更多的项目了 -->");
            $(".more").unbind();
        }
        /*
        $(".circle2").each(function(){
            var num = parseInt($(this).find("span.percent").html()) * 2.5;
            $(this).find("svg").css("stroke-dasharray", num+ "%,250%")
        });
        */
    });
}

function logout() {
    $.getJSON("/service/eliyou/wx/logout", {}, function() {
        location.href = "/wx/login.html";
    }).fail(function() {
        location.href = "/wx/login.html";
    })
}

function login() {

    $(".wrapper").hide();
    $(".loading").html("正在登录中...").show();
    $.post("/service/eliyou/wx/login", {
        "loginid":$("#loginid").val(),
        "pwd": $("#pwd").val()
    }, function () {
        if($("#bindwx").prop("checked")) {
            location.href =
                "https://open.weixin.qq.com/connect/oauth2/authorize?appid=wxaeeab45e6d45524b&redirect_uri="
                    + encodeURI("http://eliyou.luckyna.com/oauth/wx")
                    + "&response_type=code&scope=snsapi_base&state=/wx/me.html";
        } else {
            location.href = "me.html";
        }
    }).fail(function(error){
        $(".wrapper").show();
        $(".loading").hide();
        $(".login-form .error").show();
    });
}

function register() {
    var req = {
        "loginid":$("#loginid").val(),
        "pwd": $("#pwd").val(),
        "ufcode": $("#ufcode").val()
    };
    $.post("/service/eliyou/wx/register", req, function () {
        if($("#bindwx").prop("checked")) {
            location.href =
                "https://open.weixin.qq.com/connect/oauth2/authorize?appid=wxaeeab45e6d45524b&redirect_uri="
                + encodeURI("http://eliyou.luckyna.com/oauth/wx")
                + "&response_type=code&scope=snsapi_base&state=/wx/me.html";
        } else {
            location.href = "me.html";
        }
    }).fail(function(error){
        alert(JSON.stringify(error));
        $(".login-form .error").show();
    });
}

function runToNumber(div, money) {
    if (money==0) {
        $(div).html(formatMoney(money));
        return;
    }
    var stacks = [];
    for(var i=10; i>=1; i--) {
        stacks.push(money/10 *i);
    }
    showStacks(stacks, div);
}

function showStacks(stacks, div) {
    if (stacks.length==0) return;
    var p = stacks.pop();
    $(div).html(formatMoney(p));
    setTimeout(function() {
        showStacks(stacks, div);
    }, 50);
}

function msg(s) {
    alert(s);
}

function isPlusNumber(str) {
    var r = /^[0-9]*[1-9][0-9]*$/;
    return r.test(str);
}

function formatMoney(number, places, symbol, thousand, decimal) {
    number = number || 0;
    places = !isNaN(places = Math.abs(places)) ? places : 2;
    symbol = symbol !== undefined ? symbol : "";
    thousand = thousand || ",";
    decimal = decimal || ".";
    var negative = number < 0 ? "-" : "",
        i = parseInt(number = Math.abs(+number || 0).toFixed(places), 10) + "",
        j = (j = i.length) > 3 ? j % 3 : 0;
    return symbol + negative + (j ? i.substr(0, j) + thousand : "") + i.substr(j).replace(/(\d{3})(?=\d)/g, "$1" + thousand) + (places ? decimal + Math.abs(number - i).toFixed(places).slice(2) : "");
}



Date.prototype.format = function(fmt) {
    var o = {
        "M+" : this.getMonth()+1, //月份
        "d+" : this.getDate(), //日
        "h+" : this.getHours()%12 == 0 ? 12 : this.getHours()%12, //小时
        "H+" : this.getHours(), //小时
        "m+" : this.getMinutes(), //分
        "s+" : this.getSeconds(), //秒
        "q+" : Math.floor((this.getMonth()+3)/3), //季度
        "S" : this.getMilliseconds() //毫秒
    };
    var week = {
        "0" : "/u65e5",
        "1" : "/u4e00",
        "2" : "/u4e8c",
        "3" : "/u4e09",
        "4" : "/u56db",
        "5" : "/u4e94",
        "6" : "/u516d"
    };
    if(/(y+)/.test(fmt)){
        fmt=fmt.replace(RegExp.$1, (this.getFullYear()+"").substr(4 - RegExp.$1.length));
    }
    if(/(E+)/.test(fmt)){
        fmt=fmt.replace(RegExp.$1, ((RegExp.$1.length>1) ? (RegExp.$1.length>2 ? "/u661f/u671f" : "/u5468") : "")+week[this.getDay()+""]);
    }
    for(var k in o){
        if(new RegExp("("+ k +")").test(fmt)){
            fmt = fmt.replace(RegExp.$1, (RegExp.$1.length==1) ? (o[k]) : (("00"+ o[k]).substr((""+ o[k]).length)));
        }
    }
    return fmt;
}

$.fn.bindtouch = function(cb) {
    attachEvent($(this), cb);
};

function attachEvent(src, cb) {
    $(src).unbind();
    var isTouchDevice = 'ontouchstart' in window || navigator.msMaxTouchPoints;
    if (isTouchDevice) {
        $(src).bind("touchstart", function(event) {
            $(this).data("touchon", true);
            $(this).addClass("pressed");
        });
        $(src).bind("touchend", function() {
            $(this).removeClass("pressed");
            if ($(this).data("touchon")) {
                cb.bind(this)();
            }
            $(this).data("touchon", false);
        });
        $(src).bind("touchmove", function() {
            $(this).data("touchon", false);
            $(this).removeClass("pressed");
        });
    } else {
        $(src).bind("mousedown", function() {
            $(this).addClass("pressed");
            $(this).data("touchon", true);
        });
        $(src).bind("mouseup", function() {
            $(this).removeClass("pressed");
            $(this).data("touchon", false);
            cb.bind(this)();
        });
    }
}

function querystring(key) {
    var re=new RegExp('(?:\\?|&)'+key+'=(.*?)(?=&|$)','gi');
    var r=[], m;
    while ((m=re.exec(document.location.search)) != null) r.push(m[1]);
    return r;
}
