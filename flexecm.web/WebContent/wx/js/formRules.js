//验证规则
var authRules = {
    isNull: function (str) {
        return (str == "" || typeof str != "string");
    },
    betweenLength: function (str, _min, _max) {
        return (str.length >= _min && str.length <= _max);
    },
    isUid: function (str) {
        return new RegExp(authRegExp.username).test(str);
    },
    fullNumberName: function (str) {
        return new RegExp(authRegExp.fullNumber).test(str);
    },
    isPwd: function (str) {
        return /^.*([\W_a-zA-z0-9-])+.*$/i.test(str);
    },
    isPwdRepeat: function (str1, str2) {
        return (str1 == str2);
    },
    isEmail: function (str) {
        return new RegExp(authRegExp.email).test(str);
    },
    isTel: function (str) {
        return new RegExp(authRegExp.tel).test(str);
    },
    isHomeTel: function (str) {
        return new RegExp(authRegExp.homeTel).test(str);
    },
    isMobile: function (str) {
        return new RegExp(authRegExp.mobile).test(str);
    },
    checkType: function (element) {
        return (element.attr("type") == "checkbox" || element.attr("type") == "radio" || element.attr("rel") == "select");
    },
    isRealName: function (str) {
        return new RegExp(authRegExp.realname).test(str);
    },
    checkChinese:function(str)
    {
    	return new RegExp(authRegExp.chinese).test(str);
    },
    isIdcard:function(str)
    {
    	return new RegExp(authRegExp.idcard).test(str);
    },
    checkInteger1:function(str)
    {
    	return new RegExp(authRegExp.integer1).test(str);
    },
    checkMoney:function(str)
    {
    	return new RegExp(authRegExp.money).test(str);
    },
    checkUserName:function(str)
    {
        var re = /^[a-zA-z]\w{3,24}$/;
       return re.test(str);
    }
};
var authRegExp = {
    loginId: "^[a-zA-z]\w{3,24}$", //用户名
    integer: "^-?[1-9]\\d*$", //整数
    integer1: "^[1-9]\\d*$", //正整数
    integer2: "^-[1-9]\\d*$", //负整数
    number: "^([+-]?)\\d*\\.?\\d+$", //数字
    number1: "^[1-9]\\d*|0$", //正数（正整数 + 0）
    number2: "^-[1-9]\\d*|0$", //负数（负整数 + 0）
    decimal: "^([+-]?)\\d*\\.\\d+$", //浮点数
    decimal1: "^[1-9]\\d*.\\d*|0.\\d*[1-9]\\d*$", //正浮点数
    decimal2: "^-([1-9]\\d*.\\d*|0.\\d*[1-9]\\d*)$", //负浮点数
    decimal3: "^-?([1-9]\\d*.\\d*|0.\\d*[1-9]\\d*|0?.0+|0)$", //浮点数
    decimal4: "^[1-9]\\d*.\\d*|0.\\d*[1-9]\\d*|0?.0+|0$", //非负浮点数（正浮点数 + 0）
    decimal5: "^(-([1-9]\\d*.\\d*|0.\\d*[1-9]\\d*))|0?.0+|0$", //非正浮点数（负浮点数 + 0）
    money:"^[0-9]+(\.[0-9]{1,2})?$",//金额，最多保留2位小数点
    ascii: "^[\\x00-\\xFF]+$", //仅ACSII字符
    chinese: "^[\\u4e00-\\u9fa5]+$", //仅中文
    color: "^[a-fA-F0-9]{6}$", //颜色
    date: "^\\d{4}(\\-|\\/|\.)\\d{1,2}\\1\\d{1,2}$", //日期
    email: "^\\w+((-\\w+)|(\\.\\w+))*\\@[A-Za-z0-9]+((\\.|-)[A-Za-z0-9]+)*\\.[A-Za-z0-9]+$", //邮件
    idcard: "^[1-9]([0-9]{14}|[0-9]{17})$", //身份证
    idcardBest: "^[1-9]\d{5}[1-9]\d{3}((0\d)|(1[0-2]))(([0|1|2]\d)|3[0-1])\d{3}([0-9]|X)$", //权威身份证
    ip4: "^(25[0-5]|2[0-4]\\d|[0-1]\\d{2}|[1-9]?\\d)\\.(25[0-5]|2[0-4]\\d|[0-1]\\d{2}|[1-9]?\\d)\\.(25[0-5]|2[0-4]\\d|[0-1]\\d{2}|[1-9]?\\d)\\.(25[0-5]|2[0-4]\\d|[0-1]\\d{2}|[1-9]?\\d)$", //ip地址
    letter: "^[A-Za-z]+$", //字母
    letterL: "^[a-z]+$", //小写字母
    letterU: "^[A-Z]+$", //大写字母
    mobile: "^((13[0-9])|(15[^4,\\D])|(17[0-9])|(18[0-9]))\\d{8}$", //手机
    password: "^.*[A-Za-z0-9\\w_-]+.*$", //密码
    fullNumber: "^[0-9]+$", //数字
    picture: "(.*)\\.(jpg|bmp|gif|ico|pcx|jpeg|tif|png|raw|tga)$", //图片
    qq: "^[1-9]*[1-9][0-9]*$", //QQ号码
    rar: "(.*)\\.(rar|zip|7zip|tgz)$", //压缩文件
    tel: "^[0-9\-()（）]{7,18}$", //电话号码的函数(包括验证国内区号,国际区号,分机号)
    homeTel: "^(0[0-9]{2,3}\-)?([2-9][0-9]{6,7})+(\-[0-9]{1,4})?$" ,//座机(区号,座机号码,分机号码)
    url: "^http[s]?:\\/\\/([\\w-]+\\.)+[\\w-]+([\\w-./?%&=]*)?$", //url
    username: "^[A-Za-z0-9_\\-\\u4e00-\\u9fa5]+$", //用户名
    realname: "^[A-Za-z\\u4e00-\\u9fa5]+$", // 真实姓名
    zipcode: "^\\d{6}$", //邮编
    notempty: "^\\S+$" //非空
};