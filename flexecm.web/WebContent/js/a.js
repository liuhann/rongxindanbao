var $ = function(b) {
    return document.getElementById(b)
};
var log = function(a) {
    if (window.console) {
        console.log(a)
    }
};
if (typeof jc === "undefined") {
    jc = {}
}
if (!jc.log) {
    jc.log = function() {}
}
var ec = {
    _cache: {},
    wap: false,
    web: true,
    code: "",
    loginFlag: false,
    activityCode: "cw"
};
String.prototype.endWith = function(a) {
    if (a == null || a == "" || this.length == 0 || a.length > this.length) {
        return false
    }
    if (this.substring(this.length - a.length) == a) {
        return true
    } else {
        return false
    }
    return true
};
String.prototype.startWith = function(a) {
    if (a == null || a == "" || this.length == 0 || a.length > this.length) {
        return false
    }
    if (this.substr(0, a.length) == a) {
        return true
    } else {
        return false
    }
    return true
};
ec.util = {
    toHtml: function(b) {
        return b.replace(/[<>&"']/g,
        function(a) {
            return "&#x" + a.charCodeAt(0).toString(16) + ";"
        })
    },
    isFunction: function(a) {
        return Object.prototype.toString.call(a) === "[object Function]"
    },
    getParamObj: function(c) {
        var e = {},
        c = (typeof c === "string") ? c: "";
        c.replace(/([^?=&#]+)=([^&]*)/g,
        function(f, b, a) {
            e[b] = a
        });
        return e
    },
    getRandom: function(c, a) {
        var b = a - c;
        var j = Math.random();
        return (c + Math.round(j * b))
    },
    addScriptTag: function(src, fn) {
        if (!src) {
            return
        }
        with(document) {
            0[(getElementsByTagName("head")[0] || body).appendChild(createElement("script")).src = src]
        }
        if (fn && ec.util.isFunction(fn)) {
            setTimeout(fn, 100)
        }
    },
    ajax: {
        xhr: function() {
            if (window.location.protocol !== "file:") {
                try {
                    return new XMLHttpRequest()
                } catch(a) {}
                try {
                    return new ActiveXObject("Msxml2.XMLHTTP")
                } catch(a) {
                    return new ActiveXObject("Microsoft.XMLHTTP")
                }
            }
        },
        open: function(a) {
            if (!a.url || typeof a.url !== "string" || a.url.length < 5) {
                return
            }
            var c = this.xhr(),
            b = ec.util.isFunction;
            if (c) {
                var e = null;
                a.type = a.type || "GET";
                a.dataType = a.dataType || "json";
                a.async = a.async || true;
                if (a.data && typeof a.data !== "string") {
                    var l = [];
                    for (key in a.data) {
                        l.push(key + "=" + a.data[key])
                    }
                    e = l.join("&")
                }
                if (a.type === "GET" && e) {
                    a.url = a.url + ((a.url.indexOf("?") < 0) ? "?": "&") + e;
                    e = null
                }
                c.open(a.type, a.url, a.async);
                c.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
                c.onreadystatechange = function() {
                    if (c.readyState == 4) {
                        if (c.status == 200) {
                            if (a.success && b(a.success)) {
                                var f;
                                if (a.dataType === "json") {
                                    try {
                                        f = JSON.parse(c.responseText)
                                    } catch(g) {
                                        f = (new Function("return " + c.responseText))()
                                    }
                                } else {
                                    f = c.responseText
                                }
                                a.success(f)
                            }
                            return
                        }
                        if (a.error && b(a.error)) {
                            a.error()
                        }
                    }
                };
                c.send(e)
            }
        },
        getJson: function(a) {
            if (a.funName) {
                window[a.funName] = a.success
            }
            ec.util.addScriptTag(a.url)
        }
    },
    cookie: {
        get: function(j) {
            var h = null;
            if (document.cookie && document.cookie != "") {
                var m = document.cookie.split(";");
                for (var l = 0; l < m.length; l++) {
                    var g = (m[l] || "").replace(/^(\s| )+|(\s| )+$/g, "");
                    if (g.substring(0, j.length + 1) == (j + "=")) {
                        var k = function(b) {
                            b = b.replace(/\+/g, " ");
                            var c = '()<>@,;:\\"/[]?={}';
                            for (var a = 0; a < c.length; a++) {
                                if (b.indexOf(c.charAt(a)) != -1) {
                                    if (b.startWith('"')) {
                                        b = b.substring(1)
                                    }
                                    if (b.endWith('"')) {
                                        b = b.substring(0, b.length - 1)
                                    }
                                    break
                                }
                            }
                            return decodeURIComponent(b)
                        };
                        h = k(g.substring(j.length + 1));
                        break
                    }
                }
            }
            return h
        },
        set: function(n, l, o) {
            o = o || {};
            if (l === null) {
                l = "";
                o.expires = -1
            }
            var q = "";
            if (o.expires && (typeof o.expires == "number" || o.expires.toUTCString)) {
                var p;
                if (typeof o.expires == "number") {
                    p = new Date();
                    p.setTime(p.getTime() + (o.expires * 24 * 60 * 60 * 1000))
                } else {
                    p = o.expires
                }
                q = "; expires=" + p.toUTCString()
            }
            var j = "; path=" + (o.path || "/");
            var t = ".vmall.com";
            var m = "; domain=" + t;
            var k = o.secure ? "; secure": "";
            document.cookie = [n, "=", encodeURIComponent(l), q, j, m, k].join("")
        },
        remove: function(b) {
            this.set(b, null)
        }
    },
    countdown: function(k, r) {
        var o = $(k),
        j = ec._cache[k + r.startTime],
        m,
        p = r.now - new Date().getTime(),
        q = 0,
        n = function() {
            q = Math.round((r.startTime - new Date().getTime() - p) / 1000);
            q = q <= 0 ? 0 : q
        },
        l = function() {
            n();
            if (q <= 0) {
                q = 0
            }
            m = {
                day: Math.floor(q / (24 * 60 * 60)),
                hour: (r.html.indexOf("{#day}") >= 0) ? Math.floor(q / 60 / 60) % 24 : Math.floor(q / 60 / 60),
                minute: Math.floor(q / 60) % 60,
                second: q % 60
            };
            var a = r.html.replace(/{#day}/g, m.day).replace(/{#hours}/g, m.hour > 9 ? m.hour: "0" + m.hour).replace(/{#minutes}/g, m.minute > 9 ? m.minute: "0" + m.minute).replace(/{#seconds}/g, m.second > 9 ? m.second: "0" + m.second);
            o.innerHTML = a;
            return (q <= 0) ? false: true
        };
        n();
        clearInterval(j);
        if (!l()) {
            if (r.callback) {
                r.callback(r)
            }
            return
        }
        j = setInterval(function() {
            if (!l()) {
                if (r.callback) {
                    r.callback(r)
                }
                clearInterval(j)
            }
        },
        1000);
        ec._cache[k + r.startTime] = j
    }
};
ec.fixed = function(b, f) {
    var b = $(b),
    e = document.compatMode == "CSS1Compat" ? document.documentElement: document.body,
    h = b.offsetTop + e.offsetTop,
    g = 0;
    var j = !!window.ActiveXObject;
    var a = j && !window.XMLHttpRequest;
    var k = j && !!document.documentMode;
    var v = j && !a && !k;
    if (a) {
        var c;
        window.onscroll = function() {
            window.clearTimeout(c);
            c = window.setTimeout(function() {
                g = e.scrollTop;
                if (g >= h) {
                    b.setAttribute("style", "bottom:10px");
                    return
                }
                b.setAttribute("style", " ")
            },
            60)
        }
    }
    $("tools-nav-service-robotim").setAttribute("href", "http://robotim.vmall.com/live800/chatClient/chatbox.jsp?companyID=8922&configID=10&skillId=17&enterurl=" + encodeURIComponent(window.location.href) + "&k=1&remark=")
};
var _paq = _paq || [];
ec.analytics = function(b) {
    if (b.bd) {
        var c = "http://hm.baidu.com/h.js?82d2186024cf7459f80be3ff94bea77f";
        ec.util.addScriptTag(c)
    }
    if (b.bi) {
        _paq.push(["setTrackerUrl", "http://datacollect.hicloud.com:28080/webv1"]);
        _paq.push(["setSiteId", "sale.vmall.com"]);
        _paq.push(["setCustomVariable", 1, "cid", (ec.util.cookie.get("cps_id") || ""), "page"]);
        _paq.push(["setCustomVariable", 2, "direct", (ec.util.cookie.get("cps_direct") || ""), "page"]);
        _paq.push(["setCustomVariable", 4, "wi", (ec.util.cookie.get("cps_wi") || ""), "page"]);
        _paq.push(["trackPageView"]);
        var a = "http://res.vmall.com/bi/hianalytics.js?20140414";
        ec.util.addScriptTag(a)
    }
    ec.util.addScriptTag("http://libs.vmall.com/jc/20140116/jc.js")
};
ec.ajax = function(a) {
    ec.util.ajax.open(a)
};
ec.getJson = function(a) {
    ec.util.ajax.getJson(a)
}; (function() {
    var a = ["uid", "user", "name", "ts", "valid", "sign", "cid", "wi", "ticket"],
    b = ec.util.cookie;
    ec.account = {
        isLogin: function() {
            var g = b.get("uid"),
            c = b.get("user"),
            e = b.get("name");
            return (g && c && e)
        },
        setLoginInfo: function() {
            var c;
            c = ec.util.getParamObj(location.hash);
            for (i = 0; i < a.length; i += 1) {
                if (c[a[i]]) {
                    b.set(a[i], decodeURIComponent(c[a[i]]))
                }
            }
            if (location.hash.length > 0) {
                location.href = "http://sale.vmall.com/cw1008.html"
            }
        },
        getLoginInfo: function() {
            var e = b.get("name"),
            c = "";
            $("login-btn-2").style.display = "none";
            c = '<p class="event-honor8-user-info">欢迎您，' + e + '<a href="javascript:;" title="退出登录" class="event-honor8-quit" onclick="ec.account.logout(); return false;">退出登录</a></p>';
            $("loginInfo").innerHTML = c;
            ec.loginFlag = true
        },
        getLoginPars: function() {
            var c = "";
            for (i = 0; i < a.length; i += 1) {
                var e = b.get(a[i]);
                if (e) {
                    c += "&" + a[i] + "=" + encodeURIComponent(e)
                }
            }
            return c
        },
        logout: function() {
            for (i = 0; i < a.length; i += 1) {
                ec.util.cookie.remove(a[i])
            }
            ec.util.cookie.remove("hasImg");
            ec.util.cookie.remove("isQueue");
            ec.util.cookie.remove("queueSign");
            location.href = "http://hwid1.vmall.com:8080/casserver/logout?service=http://sale.vmall.com/cw1008.html"
        },
        init: function() {
            if (this.isLogin()) {
                this.getLoginInfo();
                return
            }
            this.setLoginInfo()
        }
    }
})();
ec.getSkuList = function(j) {
    var b = [],
    c = 0,
    a;
    for (key in ec.skuList) {
        a = $("sku-" + key);
        if (ec.skuList[key]["select"]) {
            b.push(ec.skuList[key]["id"]);
            if (a) {
                a.style.display = ""
            }
            c += ec.skuList[key]["price"]
        } else {
            if (a) {
                a.style.display = "none"
            }
        }
    }
    return (j == "sku") ? b: c
};
ec.selectSku = function(f, h) {
    var g = $("prod-" + f),
    j = $("checkbox-" + f);
    classNameStr = (!h) ? "event-honor8-fit-current": "event-honor8-version-active";
    if (h) {
        for (i = 1; i <= 5; i++) {
            if (i != f) {
                ec.skuList[i]["select"] = false;
                $("prod-" + i).className = " "
            }
        }
        ec.skuList[f]["select"] = true;
        g.className = classNameStr;
        return
    }
    if (g.className == classNameStr) {
        g.className = " ";
        ec.skuList[f]["select"] = j.checked = false
    } else {
        g.className = classNameStr;
        ec.skuList[f]["select"] = j.checked = true
    }
};
ec.lazy = true;
ec.isBuy = true;
ec.buy = function(a) {
    if (!ec.lazy) {
        return
    }
    if (!ec.account.isLogin()) {
        alert("请先登录");
        return
    }
    if (!ec.skuList[1]["select"] && !ec.skuList[2]["select"] && !ec.skuList[3]["select"] && !ec.skuList[4]["select"] && !ec.skuList[5]["select"]) {
        alert("请选择您要的荣耀畅玩版套餐");
        return
    }
    jc.log({
        c: 103
    });
    ec.lazy = false;
    var b = a,
    c = "callbackjsonp";
    b.className = "event-honor8-btn-ready";
    b.innerHTML = "正在提交请求";
    b.setAttribute("title", "正在提交请求");
    var j = ec.util.getRandom(1, 10);
    ec.getconfirmUrl = "http://cfm" + ((j < 10) ? "0" + j: j) + ".vmall.com/cw-getconfirm.json";
    ec.buyError = setTimeout(function() {
        jc.log({
            c: 101
        });
        var e = new Date().getTime();
        alert("抢购人数太多，请稍候！");
        jc.log({
            c: 102,
            t: new Date().getTime() - e
        })
    },
    1000 * 6);
    ec.getJson({
        url: ec.getconfirmUrl + "?" + new Date().getTime(),
        funName: c,
        success: function(g) {
            try {
                delete window[c]
            } catch(h) {
                window[c] = null
            }
            ec.isBuy = true;
            if (!g || !g.url || !g.status || g.status < 1) {
                clearTimeout(ec.buyError);
                setTimeout(function() {
                    alert("活动未开始，请校准本机时间到北京时间后再刷新页面")
                },
                1000 * 3);
                return
            }
            if (b.skuList) {
                var f = "",
                e = (ec.skuList[1]["select"] && b.skuList[1] || ec.skuList[2]["select"] && b.skuList[2] || ec.skuList[3]["select"] && b.skuList[3] || ec.skuList[4]["select"] && b.skuList[4] || ec.skuList[5]["select"] && b.skuList[5]) ? true: false;
                if (!e) {
                    ec.isBuy = false;
                    clearTimeout(ec.buyError);
                    if (ec.skuList[1]["select"]) {
                        f = "白色移动版"
                    } else {
                        if (ec.skuList[2]["select"]) {
                            f = "黑色移动版"
                        } else {
                            if (ec.skuList[3]["select"]) {
                                f = "白色版超值套餐一"
                            } else {
                                if (ec.skuList[4]["select"]) {
                                    f = "白色版超值套餐二"
                                } else {
                                    f = "白色版超值套餐三"
                                }
                            }
                        }
                    }
                    alert("对不起，您选择的" + f + "已卖完，请选择另一个版本！");
                    return
                }
            }
            if (g.status == 2) {
                clearTimeout(ec.buyError);
                alert("活动已结束");
                ec.getStatus(2);
                return
            }
            ec.creatOrderUrl = g.url;
            $("totalPrice").innerHTML = parseFloat(ec.getSkuList()).toFixed(2)
        }
    });
    setTimeout(function() {
        ec.lazy = true;
        b.className = "event-honor8-btn-go";
        b.innerHTML = "预约用户申购";
        b.setAttribute("title", "预约用户申购")
    },
    1000 * 5)
};
ec.sblazy = true;
ec.submit = function(b) {
    if (!ec.creatOrderUrl) {
        return
    }
    if (!ec.sblazy) {
        return
    }
    if (!$("answer").value) {
        alert("请输入答案！");
        return
    }
    jc.log({
        c: 104
    });
    ec.sblazy = false;
    var p = b;
    p.className = "event-honor8-shoppingCar-ready";
    p.innerHTML = "正在提交请求";
    p.setAttribute("title", "正在提交请求");
    var g = new Date();
    g = g.getFullYear() + "-" + (g.getMonth() + 1) + "-" + g.getDate() + " " + g.getHours() + ":" + g.getMinutes() + ":" + g.getSeconds();
    var a = "callback" + new Date().getTime(),
    c = ec.getSkuList("sku"),
    e = ec.account.getLoginPars(),
    f = ec.creatOrderUrl + "?nowTime=" + g + "&callback=" + a;
    f = f + "&skuId=" + c.join("&skuId=") + ec.code + e + "&a=" + ec.activityCode;
    ec.submitError = setTimeout(function() {
        jc.log({
            c: 101
        });
        var h = new Date().getTime();
        alert("抢购人数太多，请稍候！");
        jc.log({
            c: 102,
            t: new Date().getTime() - h
        })
    },
    1000 * 6);
    setTimeout(function() {
        ec.getJson({
            url: f,
            funName: a,
            success: function(h) {
                try {
                    delete window[a]
                } catch(j) {
                    window[a] = null
                }
                clearTimeout(ec.submitError);
                if (!h.success) {
                    if (!h.code || h.code < 1 || h.code > 5) {
                        h.code = 5
                    }
                    var k = ["", "mod-1", "mod-1", "mod-2", "mod-3", "mod-ready"];
                    if (h.msg != undefined && (h.code === "1" || h.code === "2")) {
                        $("msg_mod-1").innerHTML = h.msg
                    }
                    ec.showMod(k[h.code] || "");
                    return
                }
                ec.showMod("mod-success")
            }
        })
    },
    1000 * 2);
    setTimeout(function() {
        ec.sblazy = true;
        p.className = "event-honor8-shoppingCar-btn";
        p.innerHTML = "确    定";
        p.setAttribute("title", "确定")
    },
    1000 * 5)
};
ec.getStatus = function(b) {
    var a = "",
    c = $("event-honor8-dec");
    switch (b) {
    case 1:
        c.style.display = "block";
        a = '<a id="btn-go" href="javascript:;" title="预约用户申购" class="event-honor8-btn-go" onclick="ec.buy(this); return false;">预约用户申购</a>';
        break;
    case 2:
        d.style.display = "none";
        b.style.display = "block";
        a = '<a href="javascript:;" title="已售完" class="event-honor8-btn-end">已售完</a>';
        break;
    default:
        a = '<a href="javascript:;" title="即将发售" class="event-honor8-btn-ready">即将发售</a>';
        break
    }
    $("event-honor8-btn").innerHTML = a
};
ec.time = function(b, a) {
    ec.util.countdown("countdown", {
        html: "<b>{#hours}</b>小时<b>{#minutes}</b>分<b>{#seconds}</b>秒",
        now: b.getTime(),
        startTime: a.getTime(),
        callback: function(c) {
            ec.getStatus(1);
            delete ec._cache["countdown" + a.getTime()]
        }
    })
};
ec.showMod = function(b) {
    if (b == "mod-confirm") {
        if (!ec.isBuy) {
            return
        }
    }
    var a, c;
    if (!b) {
        c = ec.util.getParamObj(location.hash);
        b = (c.code && $("mod-" + c.code)) ? "mod-" + c.code: "mod-ready"
    }
    if (ec.showPrevModId) {
        $(ec.showPrevModId).style.display = "none"
    }
    a = $(b);
    a.style.display = "block";
    ec.showPrevModId = b;
    if (b == "mod-ready") {
        var j = $("btn-go");
        if (!j) {
            return
        }
        ec.lazy = true;
        j.className = "event-honor8-btn-go";
        j.innerHTML = "预约用户申购";
        j.setAttribute("title", "预约用户申购")
    }
};
ec.setLoginUrl = function(a) {
    if (!a) {
        return
    }
    if ($("login-btn-1")) {
        $("login-btn-1").href = a
    }
    $("login-btn-2").href = a
};
ec.resetChceckbox = function() {
    setTimeout(function() {
        for (key in ec.skuList) {
            if ($("checkbox-" + key)) {
                $("checkbox-" + key).checked = false
            }
        }
    },
    100)
};
ec.load = function() {
    var c = !-[1, ];
    var a = new Date().getTime(),
    b = new Date();
    b.setFullYear(2014, 3, 15);
    b.setHours(10);
    b.setMinutes(15);
    b.setSeconds(0);
    document.onkeydown = function(e) {
        var f = (c) ? event.keyCode: e.which;
        if (f == 116 && a <= b.getTime()) {
            jc.log({
                c: 100
            });
            document.body.style.display = "none";
            setTimeout(function() {
                document.body.style.display = "block"
            },
            Math.floor(Math.random() * 1000 + 50));
            if (c) {
                event.keyCode = 0;
                event.cancelBubble = true
            } else {
                e.preventDefault()
            }
            return false
        }
    };
    setTimeout(function() {
        if (ec.util.cookie.get("uid") && ec.util.cookie.get("hasImg") != "1") {
            var e = $("1px");
            e.src = e.attributes["data-lazy-src"].value;
            e.attributes["data-lazy-src"].value = "";
            ec.util.cookie.set("hasImg", "1")
        }
    },
    500);
    $("address_btn").href = $("address_btn").href + "?t=" + a;
    $("address_btn-2").href = $("address_btn-2").href + "?t=" + a;
    if (ec.account.isLogin() && ec.util.cookie.get("isQueue") && ec.util.cookie.get("isQueue") == "1") {
        $("address_btn-2").style.display = "block"
    }
    setTimeout(function() {
        if (!ec.util.cookie.get("isQueue") && ec.account.isLogin()) {
            ec.queueUrl = "http://yy.vmall.com/ivy/isqueue.jsp";
            var e = "callbackqueue";
            ec.getJson({
                url: ec.queueUrl + "?uid=" + ec.util.cookie.get("uid"),
                funName: e,
                success: function(f) {
                    try {
                        delete window[e]
                    } catch(h) {
                        window[e] = null
                    }
                    if (f) {
                        if (f.isqueue && f.isqueue == 1) {
                            ec.util.cookie.set("isQueue", "1");
                            ec.util.cookie.set("queueSign", f.queueSign)
                        } else {
                            ec.util.cookie.set("isQueue", "0");
                            $("notQueueMsg").style.display = "block";
                            var j = $("btn-go");
                            if (j) {
                                j.className = "event-honor8-btn-ready";
                                j.onclick = Function()
                            }
                        }
                    }
                }
            })
        }
    },
    100);
    if (ec.util.cookie.get("isQueue") && ec.util.cookie.get("isQueue") == "0") {
        $("notQueueMsg").style.display = "block";
        var a = $("btn-go");
        if (a) {
            a.className = "event-honor8-btn-ready";
            a.onclick = Function()
        }
        $("address_btn-2").style.display = "none"
    }
};
ec.login = function() {

	
	

    if (ec.account.isLogin()) {
        alert("您已经登录系统了！")
    } else {
        location.href = $("login-btn-2").href
    }
};

setTimeout(function() {
    callbackjsonp((function(i) {
        var y = 0;
        var C = "";
        var H = 8;
        var w = "/kfag0415/";
        var G = ec.util.cookie;
        var J = G.get;
        var P = G.set;
        var p = J("sign");
        function O(ab) {
            return A(z(D(ab), ab.length * H))
        }
        function b(ab) {
            return h(z(D(ab), ab.length * H))
        }
        function c(ab, ac) {
            return A(aa(ab, ac))
        }
        function l(ab, ac) {
            return h(aa(ab, ac))
        }
        function N(ab) {
            return A(z(D(ab), ab.length * H))
        }
        function e() {
            return O("abc") == "900150983cd24fb0d6963f7d28e17f72"
        }
        function z(al, ag) {
            al[ag >> 5] |= 128 << ((ag) % 32);
            al[(((ag + 64) >>> 9) << 4) + 14] = ag;
            var ak = 1732584193;
            var aj = -271733879;
            var ai = -1732584194;
            var ah = 271733878;
            for (var ad = 0; ad < al.length; ad += 16) {
                var af = ak;
                var ae = aj;
                var ac = ai;
                var ab = ah;
                ak = q(ak, aj, ai, ah, al[ad + 0], 7, -680876936);
                ah = q(ah, ak, aj, ai, al[ad + 1], 12, -389564586);
                ai = q(ai, ah, ak, aj, al[ad + 2], 17, 606105819);
                aj = q(aj, ai, ah, ak, al[ad + 3], 22, -1044525330);
                ak = q(ak, aj, ai, ah, al[ad + 4], 7, -176418897);
                ah = q(ah, ak, aj, ai, al[ad + 5], 12, 1200080426);
                ai = q(ai, ah, ak, aj, al[ad + 6], 17, -1473231341);
                aj = q(aj, ai, ah, ak, al[ad + 7], 22, -45705983);
                ak = q(ak, aj, ai, ah, al[ad + 8], 7, 1770035416);
                ah = q(ah, ak, aj, ai, al[ad + 9], 12, -1958414417);
                ai = q(ai, ah, ak, aj, al[ad + 10], 17, -42063);
                aj = q(aj, ai, ah, ak, al[ad + 11], 22, -1990404162);
                ak = q(ak, aj, ai, ah, al[ad + 12], 7, 1804603682);
                ah = q(ah, ak, aj, ai, al[ad + 13], 12, -40341101);
                ai = q(ai, ah, ak, aj, al[ad + 14], 17, -1502002290);
                aj = q(aj, ai, ah, ak, al[ad + 15], 22, 1236535329);
                ak = X(ak, aj, ai, ah, al[ad + 1], 5, -165796510);
                ah = X(ah, ak, aj, ai, al[ad + 6], 9, -1069501632);
                ai = X(ai, ah, ak, aj, al[ad + 11], 14, 643717713);
                aj = X(aj, ai, ah, ak, al[ad + 0], 20, -373897302);
                ak = X(ak, aj, ai, ah, al[ad + 5], 5, -701558691);
                ah = X(ah, ak, aj, ai, al[ad + 10], 9, 38016083);
                ai = X(ai, ah, ak, aj, al[ad + 15], 14, -660478335);
                aj = X(aj, ai, ah, ak, al[ad + 4], 20, -405537848);
                ak = X(ak, aj, ai, ah, al[ad + 9], 5, 568446438);
                ah = X(ah, ak, aj, ai, al[ad + 14], 9, -1019803690);
                ai = X(ai, ah, ak, aj, al[ad + 3], 14, -187363961);
                aj = X(aj, ai, ah, ak, al[ad + 8], 20, 1163531501);
                ak = X(ak, aj, ai, ah, al[ad + 13], 5, -1444681467);
                ah = X(ah, ak, aj, ai, al[ad + 2], 9, -51403784);
                ai = X(ai, ah, ak, aj, al[ad + 7], 14, 1735328473);
                aj = X(aj, ai, ah, ak, al[ad + 12], 20, -1926607734);
                ak = I(ak, aj, ai, ah, al[ad + 5], 4, -378558);
                ah = I(ah, ak, aj, ai, al[ad + 8], 11, -2022574463);
                ai = I(ai, ah, ak, aj, al[ad + 11], 16, 1839030562);
                aj = I(aj, ai, ah, ak, al[ad + 14], 23, -35309556);
                ak = I(ak, aj, ai, ah, al[ad + 1], 4, -1530992060);
                ah = I(ah, ak, aj, ai, al[ad + 4], 11, 1272893353);
                ai = I(ai, ah, ak, aj, al[ad + 7], 16, -155497632);
                aj = I(aj, ai, ah, ak, al[ad + 10], 23, -1094730640);
                ak = I(ak, aj, ai, ah, al[ad + 13], 4, 681279174);
                ah = I(ah, ak, aj, ai, al[ad + 0], 11, -358537222);
                ai = I(ai, ah, ak, aj, al[ad + 3], 16, -722521979);
                aj = I(aj, ai, ah, ak, al[ad + 6], 23, 76029189);
                ak = I(ak, aj, ai, ah, al[ad + 9], 4, -640364487);
                ah = I(ah, ak, aj, ai, al[ad + 12], 11, -421815835);
                ai = I(ai, ah, ak, aj, al[ad + 15], 16, 530742520);
                aj = I(aj, ai, ah, ak, al[ad + 2], 23, -995338651);
                ak = o(ak, aj, ai, ah, al[ad + 0], 6, -198630844);
                ah = o(ah, ak, aj, ai, al[ad + 7], 10, 1126891415);
                ai = o(ai, ah, ak, aj, al[ad + 14], 15, -1416354905);
                aj = o(aj, ai, ah, ak, al[ad + 5], 21, -57434055);
                ak = o(ak, aj, ai, ah, al[ad + 12], 6, 1700485571);
                ah = o(ah, ak, aj, ai, al[ad + 3], 10, -1894986606);
                ai = o(ai, ah, ak, aj, al[ad + 10], 15, -1051523);
                aj = o(aj, ai, ah, ak, al[ad + 1], 21, -2054922799);
                ak = o(ak, aj, ai, ah, al[ad + 8], 6, 1873313359);
                ah = o(ah, ak, aj, ai, al[ad + 15], 10, -30611744);
                ai = o(ai, ah, ak, aj, al[ad + 6], 15, -1560198380);
                aj = o(aj, ai, ah, ak, al[ad + 13], 21, 1309151649);
                ak = o(ak, aj, ai, ah, al[ad + 4], 6, -145523070);
                ah = o(ah, ak, aj, ai, al[ad + 11], 10, -1120210379);
                ai = o(ai, ah, ak, aj, al[ad + 2], 15, 718787259);
                aj = o(aj, ai, ah, ak, al[ad + 9], 21, -343485551);
                ak = u(ak, af);
                aj = u(aj, ae);
                ai = u(ai, ac);
                ah = u(ah, ab)
            }
            return Array(ak, aj, ai, ah)
        }
        function k(ag, ad, ac, ab, af, ae) {
            return u(f(u(u(ad, ag), u(ab, ae)), af), ac)
        }
        function q(ad, ac, ah, ag, ab, af, ae) {
            return k((ac & ah) | ((~ac) & ag), ad, ac, ab, af, ae)
        }
        function X(ad, ac, ah, ag, ab, af, ae) {
            return k((ac & ag) | (ah & (~ag)), ad, ac, ab, af, ae)
        }
        function I(ad, ac, ah, ag, ab, af, ae) {
            return k(ac ^ ah ^ ag, ad, ac, ab, af, ae)
        }
        function o(ad, ac, ah, ag, ab, af, ae) {
            return k(ah ^ (ac | (~ag)), ad, ac, ab, af, ae)
        }
        function aa(ad, ag) {
            var af = D(ad);
            if (af.length > 16) {
                af = z(af, ad.length * H)
            }
            var ab = Array(16),
            ae = Array(16);
            for (var ac = 0; ac < 16; ac++) {
                ab[ac] = af[ac] ^ 909522486;
                ae[ac] = af[ac] ^ 1549556828
            }
            var ah = z(ab.concat(D(ag)), 512 + ag.length * H);
            return z(ae.concat(ah), 512 + 128)
        }
        function u(ab, ae) {
            var ad = (ab & 65535) + (ae & 65535);
            var ac = (ab >> 16) + (ae >> 16) + (ad >> 16);
            return (ac << 16) | (ad & 65535)
        }
        function f(ab, ac) {
            return (ab << ac) | (ab >>> (32 - ac))
        }
        function D(ae) {
            var ad = Array();
            var ab = (1 << H) - 1;
            for (var ac = 0; ac < ae.length * H; ac += H) {
                ad[ac >> 5] |= (ae.charCodeAt(ac / H) & ab) << (ac % 32)
            }
            return ad
        }
        function A(ad) {
            var ac = y ? "0123456789ABCDEF": "0123456789abcdef";
            var ae = "";
            for (var ab = 0; ab < ad.length * 4; ab++) {
                ae += ac.charAt((ad[ab >> 2] >> ((ab % 4) * 8 + 4)) & 15) + ac.charAt((ad[ab >> 2] >> ((ab % 4) * 8)) & 15)
            }
            return ae
        }
        function h(ae) {
            var ad = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";
            var ag = "";
            for (var ac = 0; ac < ae.length * 4; ac += 3) {
                var af = (((ae[ac >> 2] >> 8 * (ac % 4)) & 255) << 16) | (((ae[ac + 1 >> 2] >> 8 * ((ac + 1) % 4)) & 255) << 8) | ((ae[ac + 2 >> 2] >> 8 * ((ac + 2) % 4)) & 255);
                for (var ab = 0; ab < 4; ab++) {
                    if (ac * 8 + ab * 6 > ae.length * 32) {
                        ag += C
                    } else {
                        ag += ad.charAt((af >> 6 * (3 - ab)) & 63)
                    }
                }
            }
            return ag
        }
        function E(ag) {
            var ae = Array(256);
            var af = Array(256);
            var ad, ab, ac;
            for (ad = 0; ad < 256; ad++) {
                ae[ad] = ag.charCodeAt(ad % ag.length);
                af[ad] = ad
            }
            for (ad = 0, ab = 0; ad < 256; ad++) {
                ab = (ab + af[ad] + ae[ad]) % 256;
                ac = af[ad];
                af[ad] = af[ab];
                af[ab] = ac
            }
            return af
        }
        function r(ah, ag) {
            var af = 0,
            ad = 0,
            ae;
            for (var ab = 0; ab < ah.length; ab++) {
                var af = (af + 1) % 256;
                var ad = (ad + ag[af]) % 256;
                ae = ag[af];
                ag[af] = ag[ad];
                ag[ad] = ae;
                var ac = (ag[af] + (ag[ad] % 256)) % 256;
                ah[ab] = ah[ab] ^ ag[ac]
            }
            return ah
        }
        function R(af, ae) {
            ae = decodeURIComponent(ae);
            var ac = E(af);
            var ad = Array(ae.length);
            for (var ab = 0; ab < ae.length; ab++) {
                ad[ab] = ae.charCodeAt(ab)
            }
            r(ad, ac);
            for (var ab = 0; ab < ad.length; ab++) {
                ad[ab] = String.fromCharCode(ad[ab])
            }
            return encodeURIComponent(ad.join(""))
        }
        function v(aj, ad, an) {
            ad = ad || document;
            an = an || "*";
            var ae = aj.split(" "),
            ac = "",
            ai = "http://www.w3.org/1999/xhtml",
            al = (document.documentElement.namespaceURI === ai) ? ai: null,
            af = [],
            ab,
            am;
            for (var ag = 0,
            ah = ae.length; ag < ah; ag += 1) {
                ac += "[contains(concat(' ', @class, ' '), ' " + ae[ag] + " ')]"
            }
            try {
                ab = document.evaluate(".//" + an + ac, ad, al, 0, null)
            } catch(ak) {
                ab = document.evaluate(".//" + an + ac, ad, null, 0, null)
            }
            while ((am = ab.iterateNext())) {
                af.push(am)
            }
            return af
        }
        var S = J("uid");
        var T = J("ts");
        var g = document.getElementById("tools-nav").className;
        var V = O(S + "" + T + "abcde").substr(8, 6);
        V = O(V + g + ec.activityCode).substr(8, 6);
        var m = (parseInt(V, 16) % 20000) + 10000;
        var Y = (parseInt(V, 16) % 99);
        var d = Math.floor(Math.random() * 1000) % 100;
        var n = "";
        if (Math.floor(Math.random() * 1000) % 2) {
            n = (m - d) + " + " + d + " = "
        } else {
            n = (m + d) + " - " + d + " = "
        }
        var Q = O("uid" + m + "ts");
        var L = "http://www.vmall.com/ccode/" + Q.substr(0, 2) + "/" + Q + ".jpg";
        var t = '<span style="color:red">请阅读题目后回答：</span><br><br><span style="vertical-align:middle;">小明问小斐：“有很多汉字是由三个一模一样的部分组成，这样的字你能说出来几个？”小斐说：“10个。三个石叫磊，三个火叫焱，三个人叫众，三个鬼叫救命，三个木叫森，三个日叫晶，三个金叫鑫，三个口叫品，三个牛叫犇。”<br><br>';
        var s = new Array("问题：小斐的答案中，三个石叫什么?", "问题：小斐的答案中，三个火叫什么?", "问题：小斐的答案中，三个人叫什么?", "问题：小斐的答案中，三个木叫什么?", "问题：小斐的答案中，三个日叫什么?", "问题：小斐的答案中，三个金叫什么?", "问题：小斐的答案中，三个口叫什么?", "问题：小斐的答案中，三个牛叫什么?");
        var a = (parseInt(V, 16) % 8);
        var M = s[a];
        var U = '<div style="color: rgb(46, 46, 46); font-size: 1.5em; margin-bottom: -1.25em; margin-top: 1.75em;">' + t + M + '  <input type="text" style="width:60px;vertical-align:middle; width:3.5em;padding:.3em;" id="answer"><br/><br/></div>';
        var K = '<div style="color: rgb(46, 46, 46); display: inline; font-size: 18px; vertical-align: middle; margin-right: 15px;">' + t + M + '  <input type="text" style="width:60px;vertical-align:middle;padding:.3em;" id="answer"></div>';
        var F = "sh0ppingCart";
        var Z = document.getElementById(F);
        if (ec.web) {
            Z.innerHTML = K
        } else {
            Z.innerHTML = U
        }
        if (typeof ec.sale === "undefined") {
            var B = ec.submit;
            ec.submit = function(ac) {
                var ab = document.getElementById("answer").value;
                ec.code = "&code=" + ab;
                B(ac)
            }
        } else {
            var B = ec.sale.submit;
            ec.sale.submit = function(ac) {
                var ab = document.getElementById("answer").value;
                ec.code = "&code=" + ab;
                B(ac)
            }
        }
        var W = new Date().getTime();
        var j = "/order/z/";
        var x = R(j, F);
        i.ev = x;
        P("__uxmd", W + "-" + O(W + R(j, x) + "CaptchaImg" + p));
        i.url = i.url.replace(/\/z\//, w);
        return i
    })({
        url: "http://ord01.vmall.com/order/z/createOrder.do",
        status: "1",
        skuList: {
            "1": true,
            "2": true,
            "3": true,
            "4": true,
            "5": true,
            "6": true
        }
    }))
},
500);
setTimeout(function() {
    clearTimeout(ec.buyError);
    ec.showMod("mod-confirm")
},
500);