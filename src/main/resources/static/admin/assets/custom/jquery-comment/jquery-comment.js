/**
 *  Author: MoonlightL
 *  Description: 评论区插件
 */
;(function($) {

    "use strict";

    let BeautyComment = function(el, options) {
        this.options = options;
        this.$el = $(el);
        this.$container = this.$el;
        this.visitor = ($.cookie("visitor") ? JSON.parse($.cookie("visitor")) : null);
        this.init();
    };

    BeautyComment.prototype.init = function(params, msgObj) {
        this.initEmoji();
        this.initCommentData();
        this.initCommentList();
        this.listRequest(params, msgObj);
    };

    // ############################# 【初始化相关 START 】 ######################################

    BeautyComment.prototype.initEmoji = function () {
        let self = this;
        self.emojiArr = [
            {'title':'微笑','url':'weixiao.gif'},
            {'title':'嘻嘻','url':'xixi.gif'},
            {'title':'哈哈','url':'haha.gif'},
            {'title':'可爱','url':'keai.gif'},
            {'title':'可怜','url':'kelian.gif'},
            {'title':'挖鼻','url':'wabi.gif'},
            {'title':'吃惊','url':'chijing.gif'},
            {'title':'害羞','url':'haixiu.gif'},
            {'title':'挤眼','url':'jiyan.gif'},
            {'title':'闭嘴','url':'bizui.gif'},
            {'title':'鄙视','url':'bishi.gif'},
            {'title':'爱你','url':'aini.gif'},
            {'title':'泪','url':'lei.gif'},
            {'title':'偷笑','url':'touxiao.gif'},
            {'title':'亲亲','url':'qinqin.gif'},
            {'title':'生病','url':'shengbing.gif'},
            {'title':'太开心','url':'taikaixin.gif'},
            {'title':'白眼','url':'baiyan.gif'},
            {'title':'右哼哼','url':'youhengheng.gif'},
            {'title':'左哼哼','url':'zuohengheng.gif'},
            {'title':'嘘','url':'xu.gif'},
            {'title':'衰','url':'shuai.gif'},
            {'title':'吐','url':'tu.gif'},
            {'title':'哈欠','url':'haqian.gif'},
            {'title':'抱抱','url':'baobao.gif'},
            {'title':'怒','url':'nu.gif'},
            {'title':'疑问','url':'yiwen.gif'},
            {'title':'馋嘴','url':'chanzui.gif'},
            {'title':'拜拜','url':'baibai.gif'},
            {'title':'思考','url':'sikao.gif'},
            {'title':'汗','url':'han.gif'},
            {'title':'困','url':'kun.gif'},
            {'title':'睡','url':'shui.gif'},
            {'title':'钱','url':'qian.gif'},
            {'title':'失望','url':'shiwang.gif'},
            {'title':'酷','url':'ku.gif'},
            {'title':'色','url':'se.gif'},
            {'title':'哼','url':'heng.gif'},
            {'title':'鼓掌','url':'guzhang.gif'},
            {'title':'晕','url':'yun.gif'},
            {'title':'悲伤','url':'beishang.gif'},
            {'title':'抓狂','url':'zhuakuang.gif'},
            {'title':'黑线','url':'heixian.gif'},
            {'title':'阴险','url':'yinxian.gif'},
            {'title':'怒骂','url':'numa.gif'},
            {'title':'互粉','url':'hufen.gif'},
            {'title':'书呆子','url':'shudaizi.gif'},
            {'title':'愤怒','url':'fennu.gif'},
            {'title':'感冒','url':'ganmao.gif'},
            {'title':'心','url':'xin.gif'},
            {'title':'伤心','url':'shangxin.gif'},
            {'title':'猪','url':'zhu.gif'},
            {'title':'熊猫','url':'xiongmao.gif'},
            {'title':'兔子','url':'tuzi.gif'},
            {'title':'OK','url':'ok.gif'},
            {'title':'耶','url':'ye.gif'},
            {'title':'GOOD','url':'good.gif'},
            {'title':'NO','url':'no.gif'},
            {'title':'赞','url':'zan.gif'},
            {'title':'来','url':'lai.gif'},
            {'title':'弱','url':'ruo.gif'},
            {'title':'草泥马','url':'caonima.gif'},
            {'title':'神马','url':'shenma.gif'},
            {'title':'囧','url':'jiong.gif'},
            {'title':'浮云','url':'fuyun.gif'},
            {'title':'给力','url':'geili.gif'},
            {'title':'围观','url':'weiguan.gif'},
            {'title':'威武','url':'weiwu.gif'},
            {'title':'话筒','url':'huatong.gif'},
            {'title':'蜡烛','url':'lazhu.gif'},
            {'title':'蛋糕','url':'dangao.gif'},
            {'title':'发红包','url':'fahongbao.gif'}
        ];
    };

    BeautyComment.prototype.initCommentData = function () {
        let self = this;
        let baseUrl = self.options.baseUrl;
        let htmlArr = [];
        if (self.options.wrapClass) {
            htmlArr.push('<div class="'+ self.options.wrapClass +'">');
        }
        htmlArr.push('<div class="comment-data">');
        htmlArr.push('<div class="comment-data-title">');
        htmlArr.push('<h2>'+ self.options.title);
        htmlArr.push('<span class="comment-num-content">(共 <b id="commentNum">0</b> 条'+ self.options.title +')</span></h2>');
        htmlArr.push('<a href="javascript:void(0)">'+ (self.visitor ? "欢迎归来" : "未登记") +'</a>');
        htmlArr.push('</div>');
        htmlArr.push('<div class="comment-data-body">');
        htmlArr.push('<div class="comment-data-body-info">');
        if (self.visitor) {
            htmlArr.push('<img id="user_avatar" src="' + (baseUrl + "/jquery-comment/image/avatar/" + self.visitor.avatar) + '" />');
        } else {
            htmlArr.push('<img id="user_avatar" src="'+ baseUrl + '/jquery-comment/image/avatar/default_avatar.jpg" />');
            htmlArr.push('<a class="change-avatar" href="javascript:void(0)">换一张</a>');
        }
        htmlArr.push('</div>');
        let nickname = (self.visitor ? self.visitor.nickname : "游客");
        htmlArr.push('<div class="comment-data-body-content" data-label="'+ nickname +':">');
        if (!self.options.canComment) {
            htmlArr.push('<div class="comment-mask">该文章已关闭评论功能</div>');
        }
        htmlArr.push('<textarea class="textarea form-control" name="content" placeholder="'+ self.options.title +'内容(必填)" rows="4" ></textarea>');
        htmlArr.push('<div class="comment-data-body-btns">');
        htmlArr.push('<div class="emoji-container">');
        htmlArr.push('<div class="emoji-container-btn">☺</div>');
        htmlArr.push('<div class="emoji-content">');
        htmlArr.push('<div class="emoji-content-title">');
        htmlArr.push('<a class="emoji-close-btn" href="javascript:void(0)">x</a>');
        htmlArr.push('</div>');
        htmlArr.push('<div class="emoji-content-items">');
        htmlArr.push('<ul>');
        let emojiArr = self.emojiArr;
        let emojiCache = [];
        $.each(emojiArr,function(index,item){
            emojiCache[item.title] = self.options.baseUrl + "/jquery-comment/image/emoji/" + item.url;
            htmlArr.push('<li title="'+ item.title +'"><img src="'+ emojiCache[item.title] +'"/></li>');
        });
        self.emojiCache = emojiCache;
        htmlArr.push('</ul>');
        htmlArr.push('</div>');
        htmlArr.push('</div>');
        htmlArr.push('</div>');
        if (!self.visitor) {
            htmlArr.push('<div class="input-container">');
            htmlArr.push('<div class="input-container-btn">昵称 <span>▲</span></div>');
            htmlArr.push('<div class="input-content">');
            htmlArr.push('昵称:<input type="text" name="nickname" placeholder="必填">');
            htmlArr.push('邮箱:<input type="text" name="email" placeholder="必填">');
            htmlArr.push('主页:<input type="text" name="homePage" placeholder="选填">');
            htmlArr.push('</div>');
            htmlArr.push('</div>');
        }
        htmlArr.push('<button type="submit" class="send-message-btn">'+ self.options.title +'一下</button>');
        htmlArr.push('</div>');
        htmlArr.push('</div>');
        htmlArr.push('</div>');
        htmlArr.push('<div id="comment-msg" class="comment-msg"></div>');
        htmlArr.push('</div>');
        if (self.options.wrapClass) {
            htmlArr.push('</div>');
        }

        self.$container.append(htmlArr.join(""));

        if (self.options.canComment) {
            self.registerCommentDataEvent();
        }
    };

    BeautyComment.prototype.initCommentList = function () {
        let self = this;
        let htmlArr = [];
        if (self.options.wrapClass) {
            htmlArr.push('<div class="'+ self.options.wrapClass +'">');
        }
        htmlArr.push('<div class="comment-list">');
        htmlArr.push('<div class="comment-list-title">');
        htmlArr.push('<h2>'+ self.options.subTitle +'</h2>');
        htmlArr.push('</div>');
        htmlArr.push('<div class="comment-list-body">');
        htmlArr.push('</div>');
        htmlArr.push('</div>');
        if (self.options.wrapClass) {
            htmlArr.push('</div>');
        }

        self.$container.append(htmlArr.join(""));

        if (self.options.canComment) {
            self.registerCommentListEvent();
        }
    };

    // 请求评论列表
    BeautyComment.prototype.listRequest = function (params, msgObj) {
        let self = this;

        if (!self.options.listUrl) {
            console.error("listUrl 参数为空");
            return;
        }

        $.ajax({
            type: "GET",
            url: self.options.listUrl,
            data: params ? params.ajaxParams : self.options.ajaxParams,
            dataType: "JSON",
            success: function(resp) {
                resp = self.options.listHandler(resp);
                self.commentData = resp;
                self.renderCommentList(msgObj);
                self.renderPagination();
            },
            error: function() {
                console.error("请求异常，请查看网络状态");
            }
        });

    };

    BeautyComment.prototype.renderCommentList = function(msgObj) {
        let self = this;
        let $commentList = self.$el.find(".comment-list");
        let $commentListBody = $commentList.find(".comment-list-body");
        if (msgObj) {
            self.showAlertMessage(msgObj.type, msgObj.message);
        } else {
            if (!self.commentData || self.commentData.totalNum === 0) {
                $commentListBody.html("<div class='comment_send_info'><span class='glyphicon glyphicon-exclamation-sign' aria-hidden='true'></span> <strong>沙发位空缺，快来抢呀 ~~</strong></div>");
                return;
            }
        }

        self.$el.find(".comment-data").find("#commentNum").text(self.commentData.totalNum);
        let htmlArr = [];
        let list = self.commentData.commentList;
        let commentShowType = self.commentData.commentShowType;
        for (let i = 0; i < list.length; i++) {
            let comment = list[i];
            htmlArr.push('<div class="comment-list-item">');
            htmlArr.push('<div class="avatar">');
            htmlArr.push('<img id="'+ comment.id +'" src="'+ comment.avatar +'" />');
            htmlArr.push('</div>');
            htmlArr.push('<div class="info">');
            if (comment.blogger) {
                htmlArr.push('<div class="name"><span class="blog-master">博主</span> <b>'+ comment.nickname +'</b> <span class="area hidden-xs">['+ comment.ipInfo +'网友]</span></div>');
            } else {
                htmlArr.push('<div class="name"><b>'+ comment.nickname +'</b> <span class="area hidden-xs">['+ comment.ipInfo +'网友]</span></div>');
            }
            htmlArr.push('<div class="date">'+ comment.timeDesc +'</div>');
            htmlArr.push('<div class="content">');
            htmlArr.push('<p>'+ self.formatContent(comment.content) +'</p>');

            if (commentShowType === "singleRow") {
                let parent = comment.parent;
                if (parent) {
                    htmlArr.push('<blockquote class="original-content">');
                    htmlArr.push('<p><b>@'+ parent.nickname +':</b> '+ self.formatContent(parent.content) +'</p>');
                    htmlArr.push('</blockquote>');
                }
            } else {
                if (comment.replyList && comment.replyList.length > 0) {
                    htmlArr.push('<div class="reply-list-body">');
                    for (let j = 0; j < comment.replyList.length; j++) {
                        let replyComment = comment.replyList[j];
                        htmlArr.push('<div class="comment-list-item">');
                        htmlArr.push('<div class="avatar">');
                        htmlArr.push('<img id="'+ replyComment.id +'" src="'+ replyComment.avatar +'" />');
                        htmlArr.push('</div>');
                        htmlArr.push('<div class="info">');
                        if (replyComment.blogger) {
                            htmlArr.push('<div class="name"><span class="blog-master">博主</span> <b>'+ replyComment.nickname +'</b> <span class="area hidden-xs">['+ replyComment.ipInfo +'网友]</span></div>');
                        } else {
                            htmlArr.push('<div class="name"><b>'+ replyComment.nickname +'</b> <span class="area hidden-xs">['+ replyComment.ipInfo +'网友]</span></div>');
                        }
                        htmlArr.push('<div class="date">'+ replyComment.timeDesc +'</div>');
                        htmlArr.push('<div class="content">');
                        htmlArr.push('<p><b>@'+ replyComment.sourceNickname +'</b>&nbsp;&nbsp;'+ self.formatContent(replyComment.content) +'</p>');
                        htmlArr.push('</div>');
                        htmlArr.push('<div class="reply pull-right"><a class="reply-btn" href="javascript:void(0)" data-comment-id="'+ replyComment.id +'" data-nickname="'+ replyComment.nickname +'">回复</a></div>');
                        htmlArr.push('</div>');
                        htmlArr.push('</div>');
                    }
                    htmlArr.push('</div>');
                }
            }

            htmlArr.push('</div>');
            htmlArr.push('<div class="reply pull-right"><a class="reply-btn" href="javascript:void(0)" data-comment-id="'+ comment.id +'" data-nickname="'+ comment.nickname +'">回复</a></div>');
            htmlArr.push('</div>');
            htmlArr.push('</div>');
        }

        $commentListBody.html(htmlArr.join(""));
    };

    BeautyComment.prototype.renderPagination = function() {
        let self = this;
        if (!self.commentData || self.commentData.totalNum === 0) {
            return;
        }
        let ajaxParams = self.options.ajaxParams;
        let pageNum = ajaxParams.pageNum;
        let pageSize = ajaxParams.pageSize;
        let totalNum = self.commentData.totalNum;
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

        let $commentList = self.$el.find(".comment-list");
        let $commentListBody = $commentList.find(".comment-list-body");

        let htmlArr = [];
        htmlArr.push('<div class="comment-list-page-bar">');
        htmlArr.push('<ul>');
        htmlArr.push('<li><a href="javascript:void(0)" data-num="'+ (pageNum - 1) +'" class="'+ (hasPrev ? "" : "disabled") +'"><</a></li>');
        for (let i = 0; i < pageBar.length; i ++) {
            let page = pageBar[i];
            htmlArr.push('<li class="'+ (pageNum === page ? "active" : "hidden-xs") +'"><a href="javascript:void(0)" data-num="'+ page +'" class="' + (pageNum === page ? "disabled" : "")  +'">'+ page +'</a></li>');
        }
        htmlArr.push('<li><a href="javascript:void(0)" data-num="'+ (pageNum + 1) +'" class="' + (hasNext ? "" : "disabled")  +'">></a></li>');
        htmlArr.push('</ul>');
        htmlArr.push('</div>');

        $commentListBody.append(htmlArr.join(""));

        self.registerPageClickEvent();
    };

    // ############################# 【初始化相关 END 】 ######################################



    // ############################# 【注册事件相关 START 】 ######################################


    BeautyComment.prototype.registerCommentDataEvent = function() {
        let self = this;
        let baseUrl = self.options.baseUrl;
        // 换头像，打开表情框，打开昵称框事件
        $(document).on("click", ".change-avatar,.emoji-container-btn,.emoji-close-btn,.input-container-btn", function (e) {
            let $target = $(e.target);
            if ($target.hasClass("change-avatar")) {
                let num = Math.floor(Math.random() * 36);
                $target.prev("img").attr("src", baseUrl + "/jquery-comment/image/avatar/avatar_" + num + ".jpg");
                self.avatar = "avatar_" + num + ".jpg";
            } else if ($target.hasClass("emoji-container-btn")) {
                $target.siblings("div").toggleClass("big");
            } else if ($target.hasClass("emoji-close-btn")) {
                $target.parents(".emoji-content").toggleClass("big");
            } else if ($target.hasClass("input-container-btn")) {
                $target.siblings("div").toggleClass("big");
            }
        });

        // 表情选择事件
        $(document).on("click", ".emoji-content-items ul li", function () {
            let title = $(this).attr("title");
            let textarea = $(this).parents(".comment-data-body-content").find("textarea");
            textarea.val(textarea.val() + "[" + title + "]");
        });

        // 发送评论事件
        $(document).on("click", "button.send-message-btn", function () {
            let $commentDataBody = $(this).parents(".comment-data-body");
            let nickname,email,homePage,avatar;
            let visitorStr = $.cookie("visitor");
            if (visitorStr) {
                let visitor = JSON.parse(visitorStr);
                nickname = visitor.nickname;
                email = visitor.email;
                homePage = visitor.homePage;
                avatar = visitor.avatar;
            } else {
                nickname = $commentDataBody.find("input[name='nickname']").val();
                email = $commentDataBody.find("input[name='email']").val();
                homePage = $commentDataBody.find("input[name='homePage']").val();
                avatar = self.avatar || "default_avatar.jpg";
            }

            let content = $commentDataBody.find("textarea").val();
            if (!nickname || !email || !content) {
                self.showAlertMessage(2, "必填项不能为空!");
                return;
            }

            let replyBtn = $commentDataBody.prev(".reply").find("a.reply-btn");
            let parameter = {
                "avatar": avatar,
                "nickname": self.checkSafe(nickname),
                "email": self.checkSafe(email),
                "content": self.checkSafe(content),
                "homePage": self.checkSafe(homePage),
                "postId": self.options.ajaxParams.postId || 0,
                "pId": replyBtn.length > 0 ? replyBtn.data("commentId") : 0
            };

            $.ajax({
                url: self.options.sendUrl,
                type: "POST",
                data: parameter,
                success: function (resp) {
                    resp = self.options.sendHandler(resp);
                    let msgObj;
                    if (resp.success) {
                        $.cookie("visitor", JSON.stringify(parameter), { expires: 60, path: '/' });
                        self.visitor = JSON.parse($.cookie("visitor"));
                        msgObj = {type: 1, message: self.options.title + "成功~~"};
                        self.refresh(self.options, msgObj);
                    } else {
                        self.showAlertMessage(2, resp.message)
                    }
                },
                error: function () {
                    console.error("请求异常，请查看网络状态");
                }
            })

        });
    };

    BeautyComment.prototype.registerCommentListEvent = function() {
        $(document).on("click", ".comment-list-body a.reply-btn", function () {
            let infoDiv = $(this).parent().parent();
            if (!infoDiv.hasClass("reply")) {
                // 移除其他评论框
                let $commentItem = $(".comment-list-item");
                $commentItem.find(".comment-data-body").remove();
                let $info = $commentItem.find(".info");
                $info.removeClass("reply").find("a.reply-btn").text("回复");

                // 修改当前评论区状态
                $(this).text("取消");
                let nickname = $(this).data("nickname");
                let $cloneBody = $(".comment-data .comment-data-body").clone(true);
                $cloneBody.find("textarea").attr("placeholder", "@" + nickname).val("");
                infoDiv.addClass("reply").parent().append($cloneBody);

            } else {
                $(this).text("回复");
                infoDiv.removeClass("reply");
                infoDiv.parent().find(".comment-data-body").remove();
            }
        });

    };

    BeautyComment.prototype.registerPageClickEvent = function() {
        let self = this;
        let pages = $(".comment-list-page-bar").find("a");
        pages.off("click").on("click", function(e) {

            let num = $(e.target).data("num");
            if (!num || num < 1 ) {
                console.warn("=====当前列表不能翻页====");
                return;
            }

            if ($(e.target).hasClass("disabled")) {
                console.warn("=====当前列表不能翻页====");
                return;
            }

            self.options.ajaxParams = $.extend({}, self.options.ajaxParams, {pageNum: num});
            self.refresh(self.options);
        });
    };

    $(document).on("click", function (e) {
        let $target = $(e.target);

        if ($target.hasClass("emoji-container-btn")) {
            return;
        }

        if ($target.hasClass("input-container-btn")) {
            return;
        }

        if ($target.parents(".emoji-content").length > 0) {
            return;
        }

        if ($target.parents(".input-content").length > 0) {
            return;
        }

        $(".emoji-content").removeClass("big");
        $(".input-content").removeClass("big");
    });

    // ############################# 【注册事件相关 END 】 ######################################

    // ############################# 【工具类相关 END 】 ######################################

    // 转换表情
    BeautyComment.prototype.formatContent = function(content) {
        let self = this;
        let emojiCache = self.emojiCache;
        let list = content.match(/\[[\u4e00-\u9fa5]*\w*\]/g);
        let filter = /[\[\]]/g;
        let title;
        if (list) {
            for(let i = 0; i < list.length; i++){
                title = list[i].replace(filter,'');
                if(emojiCache[title]) {
                    content = content.replace(list[i],' <img src="'+ emojiCache[title] +'"/> ');
                }
            }
        }
        return content;
    };

    // 提示内容
    BeautyComment.prototype.showAlertMessage = function(type, msg) {
        let self = this;
        let container = self.$el.find("#comment-msg");
        let color = (type === 1) ? "comment_send_success" : ((type === 2) ? "comment_send_fail" : "comment_send_info");
        let html =  "<div class='" + color + "'><span class='glyphicon glyphicon-exclamation-sign' aria-hidden='true'></span> <strong>"+ msg +"</strong></div>";
        container.html(html);
    };

    // 过滤敏感内容
    BeautyComment.prototype.checkSafe = function(content) {
        if(!content) {
            return "";
        }
        return content.replace(/</g,'&lt;').replace(/>/g,'&gt;').replace(/"/g, "&quot;").replace(/'/g, "&#039;");
    };

    // 销毁
    BeautyComment.prototype.destroy = function() {
        let self = this;
        if (self.options.wrapClass) {
            self.$container.find("." + self.options.wrapClass).remove();
        } else {
            self.$container.find(".comment-data").remove();
            self.$container.find(".comment-list").remove();
        }
        $(document).off("click");
    };

    // 刷新
    BeautyComment.prototype.refresh = function(params, msgObj) {
        this.destroy();
        this.init(params, msgObj);
    };

    // ############################# 【工具类相关 END 】 ######################################


    BeautyComment.DEFAULT_OPTIONS = {
        id: "comment-container",                 // 评论区容器 id
        title: "评论",                           // 标题，如：评论、留言
        subTitle: "",                     // 子标题
        baseUrl: "",                             // jquery-comment 父级目录路径
        listUrl: "",                             // 评论列表请求地址
        sendUrl: "",                             // 发送评论请求地址
        ajaxParams: {pageNum: 1, pageSize: 10},  // 评论列表请求参数
        wrapClass: "",                           // 包裹样式
        canComment: true,                        // 是否开启评论
        listHandler: function(resp) {            // 评论列表请求响应数据处理
            return resp;
        },
        sendHandler: function (resp) {           // 发送评论请求响应数据处理
            return resp;
        }
    };

    BeautyComment.METHODS = [
        "refresh",
        "destroy",
        "formatContent"
    ];

    $.fn.BeautyComment = function(params) {
        let value,
            args = Array.prototype.slice.call(arguments, 1);
        this.each(function() {
            let $this = $(this),
                data = $this.data('beauty.comment'),
                options = $.extend({}, BeautyComment.DEFAULT_OPTIONS, params);

            if (typeof params === 'string') {

                if ($.inArray(params, BeautyComment.METHODS) < 0) {
                    throw new Error("Unknown method: " + params);
                }

                if (!data) {
                    return;
                }

                value = data[params].apply(data, args);
                if (params === 'destroy') {
                    $this.removeData('beauty.comment');
                }
            }

            if (!data) {
                $this.data('beauty.comment', (data = new BeautyComment(this, options)));
            }
        });

        return typeof value === 'undefined' ? this : value;
    };

})(jQuery);