/* 通用工具 */

/* 发送请求的基础url */
const baseUrl = "http://manager.greenmall.com"

/**
 * 判断字符串是否为空
 * @param obj
 * @returns {boolean}
 */
function isEmpty(obj) {
    if (obj == undefined || typeof obj == "undefined" || obj == null || obj == "") {
        return true;
    } else {
        return false;
    }
}

/**
 * 判断字符串是否为空
 * @param obj
 * @returns {boolean}
 */
function isStringEmpty(str) {
    if (str == undefined || str == null || str == "" || str.trim().length <= 0) {
        return true;
    } else {
        return false;
    }
}

/**
 * 判断对象是否为空
 * @param obj
 * @returns {boolean}
 */
function isObjectEmpty(obj) {
    if (obj == undefined || obj == null || obj == "") {
        return true;
    } else {
        return false;
    }
}


/**
 * 时间转化工具
 * 前端时间格式2020-02-11T12:24:18.000+0000转化成正常格式：2020-02-11 12:24:18
 * @param date
 * @returns {string}
 */
function renderTime(date) {
    var dateee = new Date(date).toJSON();
    return new Date(+new Date(dateee) + 8 * 3600 * 1000).toISOString().replace(/T/g, ' ').replace(/\.[\d]{3}Z/, '')
}

/**
 * 发送post请求
 * @param url
 * @param jsonData
 * @param successF
 * @param errorF
 */
function greenMallPost(url, jsonData, successF, errorF) {
    $.ajax({
        url: baseUrl + url,
        data: JSON.stringify(jsonData),
        dataType: 'json',
        type: "POST",
        contentType: "application/json;charset=utf-8",
        success: successF,
        error: errorF
    })
}

/**
 * 发送get请求
 * @param url
 * @param jsonData
 * @param successF
 * @param errorF
 */
function greenMallGet(url, jsonData, successF, errorF, isAsync) {
    $.ajax({
        url: baseUrl + url,
        data: jsonData,
        dataType: 'json',
        type: "GET",
        async: isAsync,
        success: successF,
        error: errorF
    })
}

//验证是否有特殊字符
function hasSpecialChar(t) {
    var reg = new RegExp('^([A-Za-z]|[\u4E00-\u9FA5])+$')
    return reg.test(t) ? false : true;
}

/**
 * 格式化价格
 * @param number
 * @returns {string}
 */
function gmFormatPrice(number) {
    var temp = Math.floor(parseFloat(number * 100)) / 100;
    return temp.toFixed(2);
}

/**
 * 根据物流公司的标识获取物流公司的名称
 * @param flag
 * @returns {string}
 */
function getLogisticsName(flag) {
    if (flag == "STO") {
        return "申通快递";
    } else if (flag == "YTO") {
        return "圆通";
    } else if (flag == "HTKY") {
        return "百世快递";
    } else if (flag == "HHTT") {
        return "天天快递";
    }
    return "数据不合法";
}

/**
 * 根据状态标识获取订单状态名称
 * @param flag
 * @returns {string}
 */
function getOrderStateStr(flag) {
    if (flag == 10) {
        return "完成付款/待发货";
    } else if (flag == 20) {
        return "已发货/待签收";
    } else if (flag == 30) {
        return "交易完成";
    } else if (flag == 40) {
        return "交易关闭";
    }
    return "非法数据";
}

/**
 * 根据状态标识获取订单详情状态名称
 * @param flag
 * @returns {string}
 */
function getOrderDetailStateStr(flag) {
    if (flag == 10) {
        return "正常";
    } else if (flag == 20) {
        return "退款中";
    } else if (flag == 30) {
        return "完成退款";
    } else {
        return "数据异常";
    }
}


