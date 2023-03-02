/**
 * 用原生js重写评论区插件（统一文章评论和留言请求接口）
 */
! function(win, func) {
    "object" == typeof exports && "object" == typeof module ? module.exports = t() : "function" == typeof define && define.amd ? define([], t) : "object" == typeof exports ? exports.HbComment = func() : win.HbComment = func()
}(window, (function(){

    let HbComment = function() {};

    HbComment.DEFAULT_OPTIONS = {
        el: ".hb-comment",                        // 评论区容器
        title: "评论",                           // 标题，如：评论、留言
        subTitle: "最新评论",                     // 子标题
        baseUrl: "",                             // hb-comment 父级目录路径
        listUrl: "/commentList.json",            // 评论列表请求地址
        sendUrl: "/auth/sendComment.json",       // 发送评论请求地址
        ajaxParams: {pageNum: 1, pageSize: 10},  // 评论列表请求参数
        wrapClass: "",                           // 包裹样式
        canComment: true,                        // 是否开启评论
        listHandler: function(resp) {            // 评论列表请求响应数据处理
            return {
                totalNum: resp.data.totalNum,
                commentList: resp.data.commentList,
                commentShowType: resp.data.commentShowType
            }
        },
        sendHandler: function (resp) {           // 发送评论请求响应数据处理
            return resp;
        }
    };

    HbComment.prototype.init = function(options, isRefresh) {
        if (isRefresh) {
            this.initW();
            this.initR();
            return;
        }

        let panel = document.querySelector(".hb-w");
        if (panel != null) {
            return;
        }

        this.options = Object.assign(HbComment.DEFAULT_OPTIONS, options);
        this.el = document.querySelector(options.el);
        if (!this.el) {
            return;
        }

        this.$container = this.el;
        let cookie = getCookie("visitor");
        this.visitor = cookie ? JSON.parse(getCookie("visitor")) : null;

        this.loadResource();
        this.initEmoji();
        this.initW();
        this.initR();
    };

    HbComment.prototype.loadResource = function() {
        let headNode = document.querySelector("head");
        let commentLink = getElementByClassName(headNode, "hb-comment-css");
        if (commentLink) {
            return;
        }
        let self = this;
        let linkElement = document.createElement("link");
        linkElement.rel = "stylesheet";
        linkElement.href = self.options.baseUrl + '/hb-comment/hb-comment.css';
        linkElement.className = "hb-comment-css";
        document.querySelector("head").appendChild(linkElement);
    }

    HbComment.prototype.initEmoji = function () {
        let self = this;
        HbComment.emojiCache = [];

        self.emojiManager = {
            emojiArr: [],
            typeArr: ["QQ表情", "微博表情", "贴吧表情"]
        };

        self.emojiManager.emojiArr[0] = [
            {'title':'q_微笑','url':'qq/weixiao.gif'},
            {'title':'q_撇嘴','url':'qq/piezui.gif'},
            {'title':'q_色','url':'qq/se.gif'},
            {'title':'q_发呆','url':'qq/fadai.gif'},
            {'title':'q_得意','url':'qq/deyi.gif'},
            {'title':'q_流泪','url':'qq/liulei.gif'},
            {'title':'q_害羞','url':'qq/haixiu.gif'},
            {'title':'q_闭嘴','url':'qq/bizui.gif'},
            {'title':'q_睡','url':'qq/shui.gif'},
            {'title':'q_大哭','url':'qq/daku.gif'},
            {'title':'q_尴尬','url':'qq/ganga.gif'},
            {'title':'q_发怒','url':'qq/fanu.gif'},
            {'title':'q_调皮','url':'qq/tiaopi.gif'},
            {'title':'q_呲牙','url':'qq/ciya.gif'},
            {'title':'q_惊讶','url':'qq/jingya.gif'},
            {'title':'q_难过','url':'qq/nanguo.gif'},
            {'title':'q_酷','url':'qq/ku.gif'},
            {'title':'q_冷汗','url':'qq/lenghan.gif'},
            {'title':'q_抓狂','url':'qq/zhuakuang.gif'},
            {'title':'q_吐','url':'qq/tu.gif'},
            {'title':'q_偷笑','url':'qq/touxiao.gif'},
            {'title':'q_可爱','url':'qq/keai.gif'},
            {'title':'q_白眼','url':'qq/baiyan.gif'},
            {'title':'q_傲慢','url':'qq/aoman.gif'},
            {'title':'q_饥饿','url':'qq/jie.gif'},
            {'title':'q_困','url':'qq/kun.gif'},
            {'title':'q_惊恐','url':'qq/jingkong.gif'},
            {'title':'q_流汗','url':'qq/liuhan.gif'},
            {'title':'q_憨笑','url':'qq/hanxiao.gif'},
            {'title':'q_悠闲','url':'qq/youxian.gif'},
            {'title':'q_奋斗','url':'qq/fendou.gif'},
            {'title':'q_咒骂','url':'qq/zhouma.gif'},
            {'title':'q_疑问','url':'qq/yiwen.gif'},
            {'title':'q_嘘','url':'qq/xu.gif'},
            {'title':'q_晕','url':'qq/yun.gif'},
            {'title':'q_折磨','url':'qq/zhemo.gif'},
            {'title':'q_衰','url':'qq/shuai.gif'},
            {'title':'q_骷髅','url':'qq/kulou.gif'},
            {'title':'q_敲打','url':'qq/qiaoda.gif'},
            {'title':'q_再见','url':'qq/zaijian.gif'},
            {'title':'q_擦汗','url':'qq/cahan.gif'},
            {'title':'q_抠鼻','url':'qq/koubi.gif'},
            {'title':'q_鼓掌','url':'qq/guzhang.gif'},
            {'title':'q_糗大了','url':'qq/qiudale.gif'},
            {'title':'q_坏笑','url':'qq/huaixiao.gif'},
            {'title':'q_左哼哼','url':'qq/zuohengheng.gif'},
            {'title':'q_右哼哼','url':'qq/youhengheng.gif'},
            {'title':'q_哈欠','url':'qq/haqian.gif'},
            {'title':'q_鄙视','url':'qq/bishi.gif'},
            {'title':'q_委屈','url':'qq/weiqu.gif'},
            {'title':'q_快哭了','url':'qq/kuaikule.gif'},
            {'title':'q_阴险','url':'qq/yinxian.gif'},
            {'title':'q_左亲亲','url':'qq/zuoqinqin.gif'},
            {'title':'q_吓','url':'qq/xia.gif'},
            {'title':'q_可怜','url':'qq/kelian.gif'},
            {'title':'q_菜刀','url':'qq/caidao.gif'},
            {'title':'q_啤酒','url':'qq/pijiu.gif'},
            {'title':'q_咖啡','url':'qq/kafei.gif'},
            {'title':'q_饭','url':'qq/fan.gif'},
            {'title':'q_猪头','url':'qq/zhutou.gif'},
            {'title':'q_玫瑰','url':'qq/meigui.gif'},
            {'title':'q_凋谢','url':'qq/diaoxie.gif'},
            {'title':'q_示爱','url':'qq/shiai.gif'},
            {'title':'q_爱心','url':'qq/aixin.gif'},
            {'title':'q_心碎','url':'qq/xinsui.gif'},
            {'title':'q_蛋糕','url':'qq/dangao.gif'},
            {'title':'q_闪电','url':'qq/shandian.gif'},
            {'title':'q_炸弹','url':'qq/zhadan.gif'},
            {'title':'q_刀','url':'qq/dao.gif'},
            {'title':'q_足球','url':'qq/zuqiu.gif'},
            {'title':'q_瓢虫','url':'qq/piaochong.gif'},
            {'title':'q_便便','url':'qq/bianbian.gif'},
            {'title':'q_拥抱','url':'qq/yongbao.gif'},
            {'title':'q_赞','url':'qq/zan.gif'},
            {'title':'q_踩','url':'qq/cai.gif'},
            {'title':'q_握手','url':'qq/woshou.gif'},
            {'title':'q_胜利','url':'qq/shengli.gif'},
            {'title':'q_抱拳','url':'qq/baoquan.gif'},
            {'title':'q_勾引','url':'qq/gouyin.gif'},
            {'title':'q_拳头','url':'qq/quantou.gif'},
            {'title':'q_差劲','url':'qq/chajin.gif'},
            {'title':'q_爱你','url':'qq/aini.gif'},
            {'title':'q_NO','url':'qq/NO.gif'},
            {'title':'q_OK','url':'qq/OK.gif'},
            {'title':'q_跳跳','url':'qq/tiaotiao.gif'},
            {'title':'q_发抖','url':'qq/fadou.gif'},
            {'title':'q_恼火','url':'qq/naohuo.gif'},
            {'title':'q_磕头','url':'qq/ketou.gif'},
            {'title':'q_回头','url':'qq/huitou.gif'},
            {'title':'q_激动','url':'qq/jidong.gif'},
            {'title':'q_街舞','url':'qq/jiewu.gif'},
        ];
        self.emojiManager.emojiArr[1] = [
            {'title':'微笑','url':'weibo/weixiao.gif'},
            {'title':'嘻嘻','url':'weibo/xixi.gif'},
            {'title':'哈哈','url':'weibo/haha.gif'},
            {'title':'可爱','url':'weibo/keai.gif'},
            {'title':'可怜','url':'weibo/kelian.gif'},
            {'title':'挖鼻','url':'weibo/wabi.gif'},
            {'title':'吃惊','url':'weibo/chijing.gif'},
            {'title':'害羞','url':'weibo/haixiu.gif'},
            {'title':'挤眼','url':'weibo/jiyan.gif'},
            {'title':'闭嘴','url':'weibo/bizui.gif'},
            {'title':'鄙视','url':'weibo/bishi.gif'},
            {'title':'爱你','url':'weibo/aini.gif'},
            {'title':'泪','url':'weibo/lei.gif'},
            {'title':'偷笑','url':'weibo/touxiao.gif'},
            {'title':'亲亲','url':'weibo/qinqin.gif'},
            {'title':'生病','url':'weibo/shengbing.gif'},
            {'title':'太开心','url':'weibo/taikaixin.gif'},
            {'title':'白眼','url':'weibo/baiyan.gif'},
            {'title':'右哼哼','url':'weibo/youhengheng.gif'},
            {'title':'左哼哼','url':'weibo/zuohengheng.gif'},
            {'title':'嘘','url':'weibo/xu.gif'},
            {'title':'衰','url':'weibo/shuai.gif'},
            {'title':'吐','url':'weibo/tu.gif'},
            {'title':'哈欠','url':'weibo/haqian.gif'},
            {'title':'抱抱','url':'weibo/baobao.gif'},
            {'title':'怒','url':'weibo/nu.gif'},
            {'title':'疑问','url':'weibo/yiwen.gif'},
            {'title':'馋嘴','url':'weibo/chanzui.gif'},
            {'title':'拜拜','url':'weibo/baibai.gif'},
            {'title':'思考','url':'weibo/sikao.gif'},
            {'title':'汗','url':'weibo/han.gif'},
            {'title':'困','url':'weibo/kun.gif'},
            {'title':'睡','url':'weibo/shui.gif'},
            {'title':'钱','url':'weibo/qian.gif'},
            {'title':'失望','url':'weibo/shiwang.gif'},
            {'title':'酷','url':'weibo/ku.gif'},
            {'title':'色','url':'weibo/se.gif'},
            {'title':'哼','url':'weibo/heng.gif'},
            {'title':'鼓掌','url':'weibo/guzhang.gif'},
            {'title':'晕','url':'weibo/yun.gif'},
            {'title':'悲伤','url':'weibo/beishang.gif'},
            {'title':'抓狂','url':'weibo/zhuakuang.gif'},
            {'title':'黑线','url':'weibo/heixian.gif'},
            {'title':'阴险','url':'weibo/yinxian.gif'},
            {'title':'怒骂','url':'weibo/numa.gif'},
            {'title':'互粉','url':'weibo/hufen.gif'},
            {'title':'书呆子','url':'weibo/shudaizi.gif'},
            {'title':'愤怒','url':'weibo/fennu.gif'},
            {'title':'感冒','url':'weibo/ganmao.gif'},
            {'title':'心','url':'weibo/xin.gif'},
            {'title':'伤心','url':'weibo/shangxin.gif'},
            {'title':'猪','url':'weibo/zhu.gif'},
            {'title':'熊猫','url':'weibo/xiongmao.gif'},
            {'title':'兔子','url':'weibo/tuzi.gif'},
            {'title':'OK','url':'weibo/ok.gif'},
            {'title':'耶','url':'weibo/ye.gif'},
            {'title':'GOOD','url':'weibo/good.gif'},
            {'title':'NO','url':'weibo/no.gif'},
            {'title':'赞','url':'weibo/zan.gif'},
            {'title':'来','url':'weibo/lai.gif'},
            {'title':'弱','url':'weibo/ruo.gif'},
            {'title':'草泥马','url':'weibo/caonima.gif'},
            {'title':'神马','url':'weibo/shenma.gif'},
            {'title':'囧','url':'weibo/jiong.gif'},
            {'title':'浮云','url':'weibo/fuyun.gif'},
            {'title':'给力','url':'weibo/geili.gif'},
            {'title':'围观','url':'weibo/weiguan.gif'},
            {'title':'威武','url':'weibo/weiwu.gif'},
            {'title':'话筒','url':'weibo/huatong.gif'},
            {'title':'蜡烛','url':'weibo/lazhu.gif'},
            {'title':'蛋糕','url':'weibo/dangao.gif'},
            {'title':'发红包','url':'weibo/fahongbao.gif'}
        ];
        self.emojiManager.emojiArr[2] = [
            {'title': '_呵呵', 'url': 'tieba/hehe.jpg'},
            {'title': '_哈哈', 'url': 'tieba/haha.jpg'},
            {'title': '_吐舌', 'url': 'tieba/tushe.jpg'},
            {'title': '_啊', 'url': 'tieba/a.jpg'},
            {'title': '_酷', 'url': 'tieba/ku.jpg'},
            {'title': '_怒', 'url': 'tieba/nu.jpg'},
            {'title': '_开心', 'url': 'tieba/kaixin.jpg'},
            {'title': '_汗', 'url': 'tieba/han.jpg'},
            {'title': '_泪', 'url': 'tieba/lei.jpg'},
            {'title': '_黑线', 'url': 'tieba/heixian.jpg'},
            {'title': '_鄙视', 'url': 'tieba/bishi.jpg'},
            {'title': '_不高兴', 'url': 'tieba/bugaoxing.jpg'},
            {'title': '_真棒', 'url': 'tieba/zhenbang.jpg'},
            {'title': '_钱', 'url': 'tieba/qian.jpg'},
            {'title': '_疑问', 'url': 'tieba/yiwen.jpg'},
            {'title': '_阴脸', 'url': 'tieba/yinxian.jpg'},
            {'title': '_吐', 'url': 'tieba/tu.jpg'},
            {'title': '_咦', 'url': 'tieba/yi.jpg'},
            {'title': '_委屈', 'url': 'tieba/weiqu.jpg'},
            {'title': '_花心', 'url': 'tieba/huaxin.jpg'},
            {'title': '_呼~', 'url': 'tieba/hu.jpg'},
            {'title': '_笑脸', 'url': 'tieba/xiaonian.jpg'},
            {'title': '_冷', 'url': 'tieba/leng.jpg'},
            {'title': '_太开心', 'url': 'tieba/taikaixin.jpg'},
            {'title': '_滑稽', 'url': 'tieba/huaji.jpg'},
            {'title': '_勉强', 'url': 'tieba/mianqiang.jpg'},
            {'title': '_狂汗', 'url': 'tieba/kuanghan.jpg'},
            {'title': '_乖', 'url': 'tieba/guai.jpg'},
            {'title': '_睡觉', 'url': 'tieba/shuijiao.jpg'},
            {'title': '_惊哭', 'url': 'tieba/jinku.jpg'},
            {'title': '_生气', 'url': 'tieba/shengqi.jpg'},
            {'title': '_惊讶', 'url': 'tieba/jinya.jpg'},
            {'title': '_喷', 'url': 'tieba/pen.jpg'},
            {'title': '_爱心', 'url': 'tieba/aixin.jpg'},
            {'title': '_心碎', 'url': 'tieba/xinsui.jpg'},
            {'title': '_玫瑰', 'url': 'tieba/meigui.jpg'},
            {'title': '_礼物', 'url': 'tieba/liwu.jpg'},
            {'title': '_彩虹', 'url': 'tieba/caihong.jpg'},
            {'title': '_星星月亮', 'url': 'tieba/xxyl.jpg'},
            {'title': '_太阳', 'url': 'tieba/taiyang.jpg'},
            {'title': '_钱币', 'url': 'tieba/qianbi.jpg'},
            {'title': '_灯泡', 'url': 'tieba/dengpao.jpg'},
            {'title': '_茶杯', 'url': 'tieba/chabei.jpg'},
            {'title': '_蛋糕', 'url': 'tieba/dangao.jpg'},
            {'title': '_音乐', 'url': 'tieba/yinyue.jpg'},
            {'title': '_haha', 'url': 'tieba/haha2.jpg'},
            {'title': '_胜利', 'url': 'tieba/shenli.jpg'},
            {'title': '_大拇指', 'url': 'tieba/damuzhi.jpg'},
            {'title': '_弱', 'url': 'tieba/ruo.jpg'},
            {'title': '_OK', 'url': 'tieba/OK.jpg'},
        ];

        for (let i = 0; i < self.emojiManager.emojiArr.length; i++) {
            self.emojiManager.emojiArr[i].forEach(function(item, index) {
                HbComment.emojiCache[item.title] = self.options.baseUrl + "/hb-comment/image/" + item.url;
            });
        }

    };

    HbComment.prototype.initW = function () {
        let self = this;
        let baseUrl = self.options.baseUrl;
        let htmlArr = [];
        if (self.options.wrapClass) {
            htmlArr.push('<div class="'+ self.options.wrapClass +'">');
        }
        htmlArr.push('<div class="hb-w">');
        htmlArr.push('<div class="hb-w-head">');
        htmlArr.push('<h2 class="h2">' + self.options.title + '</h2> ' + (self.visitor ? '（欢迎归来）' : '') + ' <span title="评论信息使用cookie技术存储,用户根据情况自行处理">🛎️</span>');
        htmlArr.push('</div>');

        htmlArr.push('<div class="hb-w-body">');
        htmlArr.push('<div class="hb-w-body-item">');
        htmlArr.push('<div class="avatar">');
        if (self.visitor) {
            htmlArr.push('<img src="'+ self.visitor.avatar + '" class="hb_avatar" width="48" height="48">');
        } else {
            htmlArr.push('<img src="'+ baseUrl + '/hb-comment/image/avatar/default_avatar.jpg" class="hb_avatar" width="48" height="48">');
            htmlArr.push("<a href='javascript:void(0)' class='change_avatar'>切换</a>")
        }
        htmlArr.push('</div>');
        htmlArr.push('<div class="hb-main">');
        htmlArr.push('<div class="hb-comment-info">');
        if (self.visitor) {
            htmlArr.push('<span class="label-item"><span class="label">*邮箱</span><input type="text" name="email" value="' + self.visitor.email + '" class="hb_email" readonly="readonly" placeholder="必填,qq邮箱可自动获取头像和昵称"></span>');
            htmlArr.push('<span class="label-item"><span class="label">*昵称</span><input type="text" name="nickname" value="' + self.visitor.nickname + '" class="hb_nickname" readonly="readonly" placeholder="必填"></span>');
            htmlArr.push('<span class="label-item"><span class="label"> 主页</span><input type="text" name="homePage" value="' + self.visitor.homePage + '" class="hb_home_page" placeholder="选填"></span>');
        } else {
            htmlArr.push('<span class="label-item"><span class="label">*邮箱</span><input type="text" name="email" class="hb_email" placeholder="qq邮箱可获取头像和昵称"></span>');
            htmlArr.push('<span class="label-item"><span class="label">*昵称</span><input type="text" name="nickname" class="hb_nickname" placeholder="昵称"></span>');
            htmlArr.push('<span class="label-item"><span class="label"> 主页</span><input type="text" name="homePage" class="hb_home_page" placeholder="主页"></span>');
        }
        htmlArr.push('</div>');
        htmlArr.push('<div class="hb-comment-content">');
        if (self.options.canComment) {
            htmlArr.push('<textarea name="content" class="hb_content" placeholder="写点内容吧~"></textarea>');
        } else {
            htmlArr.push('<textarea name="content" class="hb_content" placeholder="本篇文章已关闭评论" disabled="disabled"></textarea>');
        }
        htmlArr.push('</div>');
        htmlArr.push('<div class="hb-comment-help">');
        htmlArr.push('<a title="表情" class="emoji-btn" href="javascript:void(0)">☺</a>');
        if (self.options.canComment) {
            htmlArr.push('<button type="button" class="send-btn"><span class="glyphicon glyphicon-send"></span> 发送</button>');
        } else {
            htmlArr.push('<button type="button" class="send-btn"><span class="glyphicon glyphicon-send"></span> 禁用</button>');
        }
        htmlArr.push('</div>');
        htmlArr.push('</div>');
        htmlArr.push('</div>');
        htmlArr.push('</div>');
        htmlArr.push('</div>');

        if (self.options.wrapClass) {
            htmlArr.push('</div>');
        }

        self.$container.insertAdjacentHTML('beforeend', htmlArr.join(""));

        if (self.options.canComment) {
            self.registerWEvent(document.querySelector(".hb-w-body"));
        }
    };

    HbComment.prototype.initR = function () {
        let self = this;
        let htmlArr = [];
        if (self.options.wrapClass) {
            htmlArr.push('<div class="'+ self.options.wrapClass +'">');
        }
        htmlArr.push('<div class="hb-r">');
        htmlArr.push('<div class="hb-r-head">');
        htmlArr.push('<h2 class="h2">'+ self.options.subTitle +' <span id="commentNum"></span></h2>');
        htmlArr.push('</div>');
        htmlArr.push('<div class="hb-r-body" id="hbRBody">');
        htmlArr.push('</div>');
        htmlArr.push('</div>');
        if (self.options.wrapClass) {
            htmlArr.push('</div>');
        }

        self.$container.insertAdjacentHTML('beforeend', htmlArr.join(""));

        self.getCommentList(self.options.ajaxParams);
    }

    HbComment.prototype.getCommentList = function(ajaxParams) {
        let self = this;

        if (!self.options.listUrl) {
            console.error("listUrl 参数为空");
            return;
        }

        let newAjaxParams = Object.assign(self.options.ajaxParams, {page: window.location.pathname})

        sendRequest({
            type: "GET",
            url: self.options.listUrl + "?" + formatData(newAjaxParams),
            success: function(resp) {
                let transformResp = self.options.listHandler(resp);
                self.commentData = transformResp;
                self.renderCommentList();
                self.renderPagination();
            }
        });
    }

    HbComment.prototype.renderCommentList = function() {
        let self = this;
        let commentListBody = document.querySelector("#hbRBody");

        if (!self.commentData || self.commentData.totalNum === 0) {
            let htmlArr = ["<div class='comment-send-info'><span class='glyphicon glyphicon-exclamation-sign' aria-hidden='true'></span> <strong>沙发位空缺，快来抢呀 ~~</strong></div>"];
            commentListBody.insertAdjacentHTML('beforeend', htmlArr.join(""));
            return;
        }

        document.querySelector("#commentNum").innerHTML = "(" + self.commentData.totalNum + " 条)";

        let htmlArr = [];
        let list = self.commentData.commentList;
        let commentShowType = self.commentData.commentShowType;
        for (let i = 0; i < list.length; i++) {
            let comment = list[i];
            htmlArr.push('<div class="hb-r-body-box"><div class="hb-r-body-item">');
            htmlArr.push('<div class="avatar">');
            htmlArr.push('<img id="'+ comment.id +'" src="'+ comment.avatar +'" width="48" height="48">');
            htmlArr.push('</div>');
            htmlArr.push('<div class="hb-main">');
            htmlArr.push('<div class="info">');
            let bloggerHtml = comment.blogger ? ' <span class="blogger">博主</span>' : '';
            bloggerHtml += '<span> • ' + comment.timeDesc + '</span>';
            if (comment.homePage) {
                htmlArr.push('<div class="nickname"><a href="' + comment.homePage + '" target="_blank" title="跳至">'+ comment.nickname  + bloggerHtml + '</a></div>');
            } else {
                htmlArr.push('<div class="nickname">'+ comment.nickname + bloggerHtml + '</div>');
            }
            if (self.options.canComment) {
                htmlArr.push('<div class="action"><a href="javascript:void(0)" title="回复" class="action-reply" data-comment-id="'+ comment.id +'" data-nickname="'+ comment.nickname +'">回复</a></div>');
            }
            htmlArr.push('</div>');
            htmlArr.push('<div class="content">');
            htmlArr.push('<p>'+ formatContent(comment.content) +'</p>');
            htmlArr.push('</div>');
            if (commentShowType === "singleRow") {
                let parent = comment.parent;
                if (parent) {
                    let bloggerHtml = parent.blogger ? ' <span class="blogger">[博主]</span>' : '';
                    htmlArr.push('<blockquote class="original-content">');
                    htmlArr.push('<p><b>'+ parent.nickname + bloggerHtml + ':</b> <br>'+ formatContent(parent.content) +'</p>');
                    htmlArr.push('</blockquote>');
                }
                htmlArr.push('<div class="extras">');
                htmlArr.push('<span class="os-name">' + comment.osName + ' </span><span class="browser">' + comment.browser + ' </span>');
                htmlArr.push('</div>');
            } else {
                htmlArr.push('<div class="extras">');
                htmlArr.push('<span class="os-name">' + comment.osName + ' </span><span class="browser">' + comment.browser + ' </span>');
                htmlArr.push('</div>');
                if (comment.replyList && comment.replyList.length > 0) {
                    htmlArr.push('<div class="replies">');
                    for (let j = 0; j < comment.replyList.length; j++) {
                        let replyComment = comment.replyList[j];
                        htmlArr.push('<div class="hb-r-body-item">');
                        htmlArr.push('<div class="avatar">');
                        htmlArr.push('<img id="'+ replyComment.id +'" src="'+ replyComment.avatar +'" width="48" height="48">');
                        htmlArr.push('</div>');
                        htmlArr.push('<div class="hb-main">');
                        htmlArr.push('<div class="info">');
                        let bloggerHtml = replyComment.blogger ? ' <span class="blogger">博主</span>' : '';
                        bloggerHtml += '<span> • ' + replyComment.timeDesc + '</span>';
                        htmlArr.push('<div class="nickname">'+ replyComment.nickname + bloggerHtml + '</div>');
                        if (self.options.canComment) {
                            htmlArr.push('<div class="action"><a href="javascript:void(0)" title="回复" class="action-reply" data-comment-id="'+ replyComment.id +'" data-nickname="'+ replyComment.nickname +'">回复</a></div>');
                        }
                        htmlArr.push('</div>');
                        htmlArr.push('<div class="content">');
                        htmlArr.push('<p><b>@'+ replyComment.sourceNickname +'</b>&nbsp;&nbsp;'+ formatContent(replyComment.content) +'</p>');
                        htmlArr.push('</div>');
                        htmlArr.push('<div class="extras">');
                        htmlArr.push('<span class="os-name">' + comment.osName + ' </span><span class="browser">' + comment.browser + ' </span>');
                        htmlArr.push('</div></div></div>');
                    }
                    htmlArr.push('</div>');
                }
            }
            htmlArr.push('</div></div></div>');
        }

        commentListBody.insertAdjacentHTML('beforeend', htmlArr.join(""));

        if (self.options.canComment) {
            self.registerREvent();
        }

    }

    HbComment.prototype.renderPagination = function() {
        let self = this;

        if (!self.commentData || self.commentData.totalNum === 0) {
            return;
        }
        let ajaxParams = self.options.ajaxParams;
        let pageNum = parseInt(ajaxParams.pageNum);
        let pageSize = parseInt(ajaxParams.pageSize);
        let totalNum = parseInt(self.commentData.totalNum);
        let navigatePages = 10;
        let pages;
        if (totalNum % pageSize === 0) {
            pages = totalNum / pageSize;
        } else {
            pages = Math.floor(totalNum / pageSize) + 1;
        }

        let startPage, endPage;
        if (pages <= navigatePages) {
            startPage = 1;
            endPage = pages;
        } else {
            startPage = pageNum - 4;
            endPage =  pageNum + 5;

            if (startPage < 1) {
                startPage = 1;
                endPage = 10;
            }

            if (endPage > pages) {
                endPage = pages;
                startPage = pages - 9;
            }
        }

        let pageBar = [];
        let index = 0;
        for (let i = startPage; i <= endPage; i++) {
            pageBar[index++] = i;
        }

        let hasPrev = (pageNum > 1);
        let hasNext = pageNum < pages;

        let commentListBody = document.querySelector("#hbRBody");

        let htmlArr = [];
        htmlArr.push('<div class="hb-r-pagination">');
        htmlArr.push('<ul id="pagination-bar">');
        htmlArr.push('<li><a href="javascript:void(0)" data-num="'+ (pageNum - 1) +'" class="hb-page '+ (hasPrev ? "" : "disabled") +'"><</a></li>');
        for (let i = 0; i < pageBar.length; i ++) {
            let page = parseInt(pageBar[i]);
            htmlArr.push('<li class="'+ (pageNum == page ? "active" : "hidden-xs") +'"><a href="javascript:void(0)" data-num="'+ page +'" class="hb-page ' + (pageNum == page ? "disabled" : "")  +'">'+ page +'</a></li>');
        }
        htmlArr.push('<li><a href="javascript:void(0)" data-num="'+ (pageNum + 1) +'" class="hb-page ' + (hasNext ? "" : "disabled")  +'">></a></li>');
        htmlArr.push('</ul>');
        htmlArr.push('</div>');

        commentListBody.insertAdjacentHTML('beforeend', htmlArr.join(""));

        self.registerPageClickEvent(endPage);
    }

    // ############################# 【注册事件相关 START 】 ######################################

    HbComment.prototype.registerWEvent = function(commentBody) {
        let self = this;

        if (!self.visitor) {
            let email = getElementByClassName(commentBody, "hb_email");
            email.addEventListener("blur", function() {
                let emailValue = this.value;
                if (emailValue.indexOf("@") == -1) {
                    return;
                }

                let qq = emailValue.split("@")[0];
                if (isNaN(qq)) {
                    return;
                }

                sendRequest({
                    type: "GET",
                    url: "/getQQInfo/" + qq,
                    success: function(resp) {
                        getElementByClassName(commentBody, "hb_avatar").setAttribute("src", resp.data.avatar);
                        getElementByClassName(commentBody, "hb_nickname").value = resp.data.name;
                    }
                })
            });
        }

        let changeAvatar = getElementByClassName(commentBody, "change_avatar");
        if (changeAvatar) {
            changeAvatar.addEventListener("click", function() {
                let imgNode = this.previousSibling;
                let num = Math.floor(Math.random() * 36);
                imgNode.setAttribute("src", self.options.baseUrl + "/hb-comment/image/avatar/avatar_" + num + ".jpg")
            });
        }

        let emoji = getElementByClassName(commentBody, "emoji-btn");
        let helpNode = emoji.parentNode;
        emoji.addEventListener("click", function() {
            let emojiPanel = getElementByClassName(helpNode, "emoji-panel");
            if (emojiPanel) {
                helpNode.removeChild(emojiPanel);
                return;
            }

            let htmlArr = ['<div class="emoji-panel emoji">'];
            let emojiManager = self.emojiManager;
            let emojiArr = emojiManager.emojiArr;
            for (let i = 0; i < emojiArr.length; i++) {
                let emojiArrElements = emojiArr[i];
                let active = (i == 0 ? ' active' : '');
                htmlArr.push('<ul id="emoji_ul_' + i +'" class="emoji emoji-ul ' + active + '">');
                emojiArrElements.forEach(function(item, index) {
                    htmlArr.push('<li title="'+ item.title +'" class="emoji emoji-item"><img class="emoji" src="'+ HbComment.emojiCache[item.title] +'" /></li>');
                });
                htmlArr.push('</ul>');
            }
            htmlArr.push('<div class="emoji-tab-box emoji">');
            for (let i = 0; i < emojiArr.length; i++) {
                let active = (i == 0 ? ' active' : '');
                htmlArr.push('<span id="emoji_tab_' + i + '" class="emoji emoji-tab ' + active + '">' + emojiManager.typeArr[i] + '</span>');
            }
            htmlArr.push('</div>')
            htmlArr.push('</div>');
            helpNode.insertAdjacentHTML('beforeend', htmlArr.join(""));

            let emojiUls = getElementsByClassName(helpNode, "emoji-ul");
            for (let i = 0; i < emojiArr.length; i++) {
                emojiUls[i].addEventListener("click", function(e) {
                    if (e.target.nodeName == "IMG") {
                        let title = e.target.parentNode.title;
                        let textArea = getElementByClassName(helpNode.parentNode,"hb_content");
                        textArea.value = textArea.value + "[" + title + "]";
                    }
                });
            }

            let emojiTabs = document.querySelectorAll(".emoji-tab");
            let tabLength = emojiTabs.length;
            for (let i = 0; i < tabLength; i++) {
                emojiTabs[i].addEventListener("click", function(e) {
                    if (this.className.indexOf("active") > -1) {
                        return;
                    }

                    for (let i = 0; i < tabLength; i++) {
                        document.querySelector("#emoji_ul_" + i).setAttribute("class", "emoji-ul");
                        document.querySelector("#emoji_tab_" + i).setAttribute("class", "emoji-tab");
                    }

                    let arr = this.id.split("_");
                    let num = arr[arr.length - 1];

                    document.querySelector("#emoji_ul_" + num).setAttribute("class", "emoji-ul active");
                    document.querySelector("#emoji_tab_" + num).setAttribute("class", "emoji-tab active");
                });
            }
        });

        document.addEventListener("click", function(e) {
            if (e.target.className.indexOf("emoji") > -1) {
                return;
            }

            let emojiPanel = getElementByClassName(helpNode, "emoji-panel");
            if (emojiPanel) {
                helpNode.removeChild(emojiPanel);
            }
        });

        let send = getElementByClassName(commentBody, "send-btn");
        send.addEventListener("click", function() {
            let that = this;
            let nickname,email,homePage,avatarVal;
            if (!self.visitor) {
                nickname = getElementByClassName(commentBody, "hb_nickname").value;
                email = getElementByClassName(commentBody, "hb_email").value;
                avatarVal = getElementByClassName(commentBody, "hb_avatar").getAttribute("src");
                homePage = getElementByClassName(commentBody, "hb_home_page").value;
            } else {
                nickname = self.visitor.nickname;
                email = self.visitor.email;
                avatarVal = self.visitor.avatar;
                homePage = self.visitor.homePage || getElementByClassName(commentBody, "hb_home_page").value;
            }

            let content = getElementByClassName(commentBody, "hb_content").value;

            if (!nickname || !email) {
                showTip("邮箱地址和昵称不能为空");
                return;
            }

            if (!content) {
                showTip("评论内容不能为空");
                return;
            }

            let data = {
                "avatar": avatarVal,
                "nickname": checkSafe(nickname),
                "email": checkSafe(email),
                "content": checkSafe(content).replace(/\n/g,'<br/>'),
                "homePage": checkSafe(homePage),
                "pId": that.dataset.commentPid || 0,
                "sourceNickname": that.dataset.sourceNickname || "",
                "page": window.location.pathname
            };

            if (!self.options.sendUrl) {
                console.error("请求地址为空!");
                return;
            }

            sendRequest({
                type: "POST",
                url: self.options.sendUrl,
                data: formatData(data),
                success: function(resp) {
                    if (resp.success) {
                        showTip("评论成功");
                        data.content = "";
                        data.page = "";
                        setCookie("visitor", JSON.stringify(data), 60);
                        self.visitor = data;
                        self.refresh(self.options, true);
                    } else {
                        showTip(resp.message, 4000, function() {
                            if (resp.code == 2005) {
                                window.location.reload();
                            }
                        });
                    }
                }
            });
        });
    }

    HbComment.prototype.registerPageClickEvent = function(endPage) {
        let self = this;
        let pageBar = document.querySelector("#pagination-bar");
        pageBar.addEventListener("click", function(e) {
            let target = e.target;
            if (target.className.indexOf("hb-page") > -1) {
                let num = target.dataset.num;
                if (!num || num < 1 ) {
                    return;
                }

                if (target.className.indexOf("disabled") > -1 || num > endPage) {
                    return;
                }

                self.options.ajaxParams = Object.assign(self.options.ajaxParams, {pageNum: num});
                self.refresh(self.options, true);
            }
        });
    };

    HbComment.prototype.registerREvent = function() {
        let self = this;
        let replyArr = document.querySelectorAll(".action-reply");
        let hbW = document.querySelector(".hb-w");
        for (let i = 0; i < replyArr.length; i++) {
            let replyBtn = replyArr[i];
            let commentId = replyBtn.dataset.commentId;
            let sourceNickname = replyBtn.dataset.nickname;
            replyBtn.addEventListener("click", function() {

                let openEle = document.querySelector(".action-reply.open");
                if (openEle && openEle != replyBtn) {
                    openEle.setAttribute("class", "action-reply");
                }

                let infoNode = replyBtn.parentNode.parentNode;
                let cloneEle = document.querySelector(".hb-w-body.clone");
                let index = replyBtn.className.indexOf("open");
                if (index == -1) {
                    replyBtn.setAttribute("class", "action-reply open");
                    if (cloneEle) {
                        cloneEle.remove();
                    }

                    let wBody = getElementByClassName(hbW,"hb-w-body");
                    let cloneCommentBody = wBody.cloneNode(true);
                    cloneCommentBody.setAttribute("class", "hb-w-body clone");
                    let sendBtn = getElementByClassName(cloneCommentBody, "send-btn");
                    sendBtn.dataset.commentPid = commentId;
                    sendBtn.dataset.sourceNickname = sourceNickname;
                    infoNode.parentNode.parentNode.insertAdjacentElement('afterend', cloneCommentBody);
                    cloneCommentBody.scrollIntoView({behavior: "smooth",block: "center",inline: "nearest"});

                    getElementByClassName(cloneCommentBody, "hb_content").setAttribute("placeholder", "@" + sourceNickname);
                    self.registerWEvent(cloneCommentBody);
                } else {
                    replyBtn.setAttribute("class", "action-reply");
                    if (cloneEle) {
                        cloneEle.remove();
                    }
                }
            });
        }
    }

    // ############################# 【注册事件相关 END 】 ######################################


    HbComment.prototype.destroy = function() {
        let self = this;
        let el = document.querySelector(".hb-comment");
        let first = el.firstElementChild;
        let last = el.lastElementChild;
        el.removeChild(first);
        el.removeChild(last);
    };

    HbComment.prototype.refresh = function(params) {
        let self = this;
        self.destroy();
        self.init(params, true);
    };


    // ############################# 【工具函数相关 START 】 ######################################

    // 发送请求
    function sendRequest(params) {

        if (!params.url) {
            console.error("url 参数为空");
            return;
        }

        let xmlHttp = new XMLHttpRequest();
        xmlHttp.open(params.type, params.url, true);
        xmlHttp.setRequestHeader("Content-type","application/x-www-form-urlencoded");
        xmlHttp.send(params.data);
        xmlHttp.onreadystatechange = function() {
            if (this.readyState == 4 && this.status == 200) {
                let resp = JSON.parse(xmlHttp.responseText);
                params.success(resp);
            }
        };
    };

    function formatData(data) {
        let result = "";
        for (let key in data) {
            result += key + "=" + encodeURIComponent(data[key]) + "&";
        }
        result = result.substring(0, result.length - 1);
        return result;
    }

    function formatContent(content) {
        let emojiCache = HbComment.emojiCache;
        let list = content.match(/\[\w*[\u4e00-\u9fa5]*\w*\]/g);
        let filter = /[\[\]]/g;
        let title;
        if (list) {
            for (let i = 0; i < list.length; i++){
                title = list[i].replace(filter, '');
                if (emojiCache[title]) {
                    content = content.replace(list[i],' <img src="'+ emojiCache[title] +'"/> ');
                }
            }
        }

        return content.replace(/&lt;br\/&gt;/g, "<br/>");
    };

    function checkSafe(content) {
        if (!content) {
            return "";
        }
        return content.replace(/</g,'&lt;').replace(/>/g,'&gt;').replace(/"/g, "&quot;").replace(/'/g, "&#039;");
    };

    function transformList(list) {
        let arr = [];
        for(let i = 0; i < list.length; i++) {
            arr.push(list[i]);
        }
        return arr;
    }

    function getElementByClassName(parentNode, childClassName) {
        let arr = findByClassName(parentNode, childClassName, []);
        if (arr && arr.length > 0) {
            return arr[0];
        }
    }

    function getElementsByClassName(parentNode, childClassName) {
        return findByClassName(parentNode, childClassName, []);
    }

    function findByClassName(parentNode, childClassName, arr) {
        if (parentNode.className && parentNode.className.indexOf(childClassName) > -1) {
            arr.push(parentNode)
            return parentNode;
        } else {
            let nodes = parentNode.childNodes;
            nodes = transformList(nodes).filter(function(item){
                if(item.nodeType !== 3) {
                    return item;
                }
            });

            if (nodes.length > 0) {
                nodes.forEach(function(item) {
                    findByClassName(item, childClassName, arr);
                });
            }
        }
        return arr;
    }

    function setCookie(cname, cvalue, exdays) {
        let d = new Date();
        d.setTime(d.getTime() + (exdays*24*60*60*1000));
        let expires = "expires="+ d.toUTCString();
        document.cookie = cname + "=" + cvalue + ";" + expires + ";path=/";
    }

    function getCookie(cname) {
        let name = cname + "=";
        let decodedCookie = decodeURIComponent(document.cookie);
        let ca = decodedCookie.split(';');
        for(let i = 0; i <ca.length; i++) {
            let c = ca[i];
            while (c.charAt(0) == ' ') {
                c = c.substring(1);
            }
            if (c.indexOf(name) == 0) {
                return c.substring(name.length, c.length);
            }
        }
        return "";
    }

    function showTip(content, second, fn) {
        let timeout = (second || 3000);
        let tipNode = document.createElement("div");
        tipNode.className = "hb-comment-tip";
        tipNode.innerHTML = content;
        document.body.appendChild(tipNode);

        setTimeout(function() {
            document.body.removeChild(tipNode);
            if (typeof fn == "function") {
                fn();
            }
        }, timeout);

    }

    // ############################# 【工具类相关 END 】 ######################################


    return new HbComment();
}));
