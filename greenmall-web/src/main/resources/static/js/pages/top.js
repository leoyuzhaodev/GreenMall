const gmTop = {
    template: "<div class='hmtop' id='GMHead'>\
    <!--顶部导航条 -->\
    <div class='am-container header'>\
        <ul class='message-l'>\
            <div class='topMessage'>\
                <div v-if='isLogin == 0' class='menu-hd'>\
                    <a href='http://www.greenmall.com/login.html' target='_top' class='h'>亲，请登录</a>\
                    <a href='http://www.greenmall.com/register.html' target='_top'>免费注册</a>\
                </div>\
                <div v-else class='menu-hd'>\
                    <a href='javascript:void(0)' target='_top' class='h'>欢迎【用户名：{{user.username}}】来到绿色商城</a>\
                </div>\
            </div>\
        </ul>\
        <ul class='message-r'>\
            <div class='topMessage home'>\
                <div class='menu-hd'><a href='http://www.greenmall.com/' target='_top' class='h'>商城首页</a></div>\
            </div>\
            <div class='topMessage my-shangcheng'>\
                <div class='menu-hd MyShangcheng'>\
                    <a style='cursor: pointer' @click='goMyPage(1)'>\
                        <i class='am-icon-user am-icon-fw'></i>个人中心\
                    </a>\
                </div>\
            </div>\
            <div class='topMessage mini-cart'>\
                <div class='menu-hd'>\
                <a style='cursor: pointer' @click='goMyPage(2)' id='mc-menu-hd' >\
                    <i class='am-icon-shopping-cart  am-icon-fw'></i>\
                    <span>购物车</span>\
                    </a>\
                </div>\
            </div>\
            <div class='topMessage favorite'>\
                <div class='menu-hd'>\
                    <a style='cursor: pointer' @click='goMyPage(3)'>\
                        <i class='am-icon-heart am-icon-fw'></i>\
                        <span>收藏夹</span>\
                    </a>\
                </div>\
            </div>\
            <div v-if='isLogin == 1' class='topMessage favorite'>\
                <div class='menu-hd'>\
                    <a style='cursor: pointer' @click='logout'>\
                        <span style='color: red'>退出登录</span>\
                    </a>\
                </div>\
            </div>\
        </ul>\
    </div>\
    <!--悬浮搜索框-->\
    <div class='nav white'>\
        <div class='logo'><img src='http://www.greenmall.com/images/gmlogo.png'/></div>\
        <div class='logoBig'>\
            <li>\
            <a href='http://www.greenmall.com/'><img style='margin-top: 15px;' src='http://www.greenmall.com/images/gm_logo.png'/></a>\
            </li>\
        </div>\
        <div class='search-bar pr'>\
            <a name='key' href='http://www.greenmall.com/search.html'></a>\
            <form action='http://www.greenmall.com/search.html'>\
                <input id='searchInput' v-model='key' name='key' type='text' placeholder='搜索'\
                       autocomplete='off'>\
                <input id='ai-topsearch' @click='search' class='submit am-btn' value='搜索' index='1' type='button'>\
            </form>\
        </div>\
    </div>\
    <div class='clear'></div>\
</div>\
    ",
    name: 'gm-top',
    data() {
        return {
            gm,
            key: "",
            query: location.search,
            isLogin: 0, // 0：未登录 1：登录
            // 记录登录的用户信息
            user: {}
        }
    },
    created() {
        // 1，获取搜索参数
        this.key = this.getUrlParam("key");

        // 2，加载用户信息
        this.loadPersonData();
    },
    methods: {
        /* 跳转到搜索页面 */
        search() {
            window.location = 'search.html?key=' + this.key;
        },
        /* 获取请求url中的参数 */
        getUrlParam: function (name) {
            var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)", "i");
            var r = window.location.search.substr(1).match(reg);
            if (r != null) {
                return decodeURI(r[2]);
            }
            return null;
        },
        /* 加载用户信息 */
        loadPersonData() {
            gm.http.get("/auth/verify")
                .then(resp => {
                    if (resp.status === 200) {
                        this.isLogin = 1;
                        this.user = resp.data;
                    } else {
                        return null;
                    }
                })
                .catch(() => {
                    return null;
                })
        },
        /* 去往指定页面 */
        goMyPage(pn) {
            // 1，个人中心 2，购物车 3，收藏夹
            if (this.isLogin == 0) {
                alert("您还没有登录，请登录之后做相关操作。")
                return;
            }
            // 2，去往指定页面
            if (pn === 1) {
                window.location.href = "http://www.greenmall.com/personalcenter.html"
            } else if (pn === 2) {
                window.location.href = "http://www.greenmall.com/shopcart.html"
            } else if (pn === 3) {
                window.location.href = "http://www.greenmall.com/personalcenter.html?open=fcollection"
            }
        },
        /* 退出登录 */
        logout() {
            if (this.isLogin == 0) {
                alert("您还没有登录呢...")
            } else {
                gm.http.get("/user/auth/logout")
                    .then(resp => {
                        window.top.location.href = "http://www.greenmall.com/login.html";
                    })
                    .catch(() => {
                        return null;
                    })
            }
        }
    }
}
export default gmTop;