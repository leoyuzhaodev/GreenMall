/* 登录验证 */
greenMallGet("/auth/verify", {},
    function (data) {
        console.log(data);
    },
    function () {
        var parentWindow = window.top;
        parentWindow.location.href = "http://manager.greenmall.com/login.html";
    }
)