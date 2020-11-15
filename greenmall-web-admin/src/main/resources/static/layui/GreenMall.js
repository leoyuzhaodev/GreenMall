/* 通用工具 */

/* 发送请求的基础url */
const baseUrl = "http://manager.greenmall.com"

/**
 * 判断字符串是否为空
 * @param obj
 * @returns {boolean}
 */
function isEmpty(obj) {
    if (typeof obj == "undefined" || obj == null || obj == "") {
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