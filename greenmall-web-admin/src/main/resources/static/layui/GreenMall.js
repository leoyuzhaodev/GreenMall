/* 通用工具 */

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