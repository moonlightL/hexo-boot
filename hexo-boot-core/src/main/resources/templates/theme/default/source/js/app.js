;(function($) {

    let APP = {
        plugins: {
            APlayer: {
                css: baseLink + "/source/js/APlayer/APlayer.min.css",
                js: baseLink + "/source/js/APlayer/APlayer.min.js"
            },
            highlight: {
                js: baseLink + "/source/js/highlightjs/highlight.pack.js"
            },
            lazyLoad: {
                js: baseLink + "/source/js/jquery.lazyload.min.js"
            },
            share: {
                js: baseLink + "/source/js/overshare/js/social-share.min.js"
            },
            toc: {
                css: baseLink + "/source/js/autoToc/jquery.autoToc.css",
                js: baseLink + "/source/js/autoToc/jquery.autoToc.js"
            },
            wayPoints: {
                js: baseLink + "/source/js/jquery.waypoints.min.js"
            }
        }
    };

    console.log("%c Theme." + themeName + " v" + version + " %c https://www.extlight.com/ ", "color: white; background: #e9546b; padding:5px 0;", "padding:4px;border:1px solid #e9546b;");

    $.ajaxSetup({
        cache: true
    });

    const loadResource = function() {
        let APlayer = APP.plugins.APlayer;
        $('head').append('<link href="' + APlayer.css + '" rel="stylesheet" type="text/css" />');
        $.getScript(APlayer.js);
    };

    let CURRENT_MODE = "current_mode";
    const themModeEvent = function() {
        let mode = sessionStorage.getItem(CURRENT_MODE);
        if (!mode) {
            let hour = (new Date()).getHours();
            mode = (hour >= 6 && hour < 18 ? "light" : "dark");
        }
        $("html").attr("mode", mode);
    };

    const optionEvent = function() {
        let $body = $("body");
        let $options = $('<div class="options animated fadeInRight" id="option"></div>');
        $body.append($options);

        let elements = [
            {"class": "search", "icon": "fa fa-search", "title": "搜索"},
            {"class": "change-mode", "icon": "fa fa-adjust", "title": "黑白模式"},
            {"class": "music", "icon": "fa fa-music", "title": "播放音乐"},
            {"class": "up", "icon": "fa fa-arrow-up", "title": "回到顶部"}
        ];

        let htmArr = [];
        for (let i = 0; i < elements.length; i++) {
            let ele = elements[i];
            htmArr.push('<div class="option-item '+ ele.class +'" title="'+ele.title+'"> <i class="' + ele.icon+'"></i> </div> ');
        }

        $options.append(htmArr.join(""));

        let $iframe = $('<div id="modal-iframe" class="iziModal light"></div>');
        $body.append($iframe);

        $("#modal-iframe").iziModal({
            iframe: true,
            headerColor: "rgb(76, 175, 80)",
            title: '<i class="fa fa-search"></i> 站内搜索' ,
            width: 620,
            iframeHeight: 360,
            iframeURL: "/search/"
        });

        $(".options .search").off("click").on("click", function() {
            $('#modal-iframe').iziModal('open');
        });

        $(".options .change-mode").off("click").on("click", function () {
            let $html = $("html");
            let mode = ($html.attr("mode") === "light" ? "dark" : "light");
            sessionStorage.setItem(CURRENT_MODE, mode);
            $html.attr("mode", mode);
        });

        $(".options .music").off("click").on("click", function () {
            let $aplayer = $("#aplayer");
            if ($aplayer.hasClass("inited")) {
                $aplayer.toggleClass("show");
                return;
            }

            $.ajax({
                url: "/musicList.json",
                type: "GET",
                dataType: "JSON",
                success: function (resp) {
                    if (resp.success) {
                        if (resp.data.length === 0) {
                            layer.msg("博主未上传音乐资源");
                            return;
                        }

                        $aplayer.toggleClass("show");
                        new APlayer({
                            container: $aplayer.get(0),
                            fixed: true,
                            listFolded: true,
                            listMaxHeight: "120px",
                            autoplay: true,
                            audio: resp.data
                        });
                        $aplayer.addClass("inited");
                    } else {
                        layer.msg("加载数据异常")
                    }
                }
            });
        });

        $(".options .up").off("click").on("click",function() {
            $('html, body').animate({
                scrollTop: $('html').offset().top
            }, 500);
        });
    };

    const circleMagic = function() {
        $('.image-content').circleMagic({
            radius: 16,
            density: .1,
            color: 'rgba(255,255,255, .4)',
            clearOffset: .3
        });
    };

    const loadLazy = function() {
        $.getScript(APP.plugins.lazyLoad.js, function(e) {
            $("img.lazyload").lazyload({
                placeholder : baseLink + "/source/images/loading.jpg",
                effect: "fadeIn"
            });
        })
    };

    const contentWayPoint = function () {
        $.getScript(APP.plugins.wayPoints.js, function() {
            let i = 0;
            $('.animate-box').waypoint(function (direction) {
                if (direction === 'down' && !$(this.element).hasClass('animated')) {
                    i++;
                    $(this.element).addClass('item-animate');
                    setTimeout(function () {
                        $('body .animate-box.item-animate').each(function (k) {
                            let el = $(this);
                            setTimeout(function () {
                                let effect = el.data('animate-effect');
                                effect = effect || 'fadeInUp';
                                el.addClass(effect + ' animated visible');
                                el.removeClass('item-animate');
                            }, k * 200, 'easeInOutExpo');
                        });
                    }, 100);
                }
            }, {
                offset: '85%'
            });
        });
    };

    const postEvent = function() {
        let $detail = $("#post-content");
        if ($detail.length > 0) {
            $.getScript(APP.plugins.highlight.js, function () {
                document.querySelectorAll('figure span').forEach((block) => {
                    hljs.highlightBlock(block);
                });
            });

            // 目录
            let Toc = APP.plugins.toc;
            let $head = $('head');
            if ($head.find("#toc-css").length == 0) {
                $head.append('<link id="toc-css" href="' + Toc.css + '" rel="stylesheet" type="text/css" />');
            }

            $.getScript(Toc.js, function () {
                $("#tocContainer").autoToc({offsetTop: 520});
            });

            // 点赞
            $("#priseBtn").on("click",function () {
                let postId = $(this).data("id");
                let key = "post-hasPrize" + postId;
                if (sessionStorage.getItem(key)) {
                    layer.msg("已点赞");
                    return;
                }

                $.post("/praisePost/" + postId, null, function (resp) {
                    if (resp.success) {
                        $("#prizeCount").text(resp.data);
                        sessionStorage.setItem(key, "y");
                        layer.msg("谢谢点赞");
                    }
                },"json");

            });

            // 打赏
            $("#showRewardImg").on("click", function () {
                let rewardImgArea = $("#rewardImgArea");
                if (rewardImgArea.hasClass("hide")) {
                    rewardImgArea.removeClass("hide");
                    rewardImgArea.slideDown("slow");
                } else {
                    rewardImgArea.addClass("hide");
                    rewardImgArea.slideUp("slow");
                }
            });

            // 分享
            $.getScript(APP.plugins.share.js, function () {
                $("#shareOpenBtn").on("click",function () {
                    let shareBtns = $("#shareBtns");
                    if (shareBtns.hasClass("share-open")) {
                        shareBtns.removeClass("share-open");
                    } else {
                        shareBtns.addClass("share-open");
                    }
                });
            });
        }
    };

    const dynamicEvent = function() {
        let $dynamic = $("#dynamic-content");
        if ($dynamic.length > 0) {
            $(".praise").off("click").on("click",function () {
                let that = this;
                let id = $(this).data("id");
                let key = "dynamic-hasPrize" + id;
                if (sessionStorage.getItem(key)) {
                    layer.msg("已点赞");
                    return;
                }

                $.post("/praiseDynamic/" + id, null, function (resp) {
                    if (resp.success) {
                        $(that).find(".praise-num").text(resp.data);
                        $(that).find(".fa").css("color", "red");
                        sessionStorage.setItem(key, "y");
                        layer.msg("点赞成功");
                    }
                },"json");

            });
        }
    };

    const pjaxEvent = function() {
        $(document).pjax('a[data-pjax]', '#wrap', {fragment: '#wrap', timeout: 8000});
        $(document).on('pjax:send', function() { NProgress.start();});
        $(document).on('pjax:complete',   function(e) {
            circleMagic();
            contentWayPoint();
            dynamicEvent();
            postEvent();
            let $navBar = $("#navbar");
            let $arr = $navBar.find("ul.menu>li");
            $arr.removeClass("active");
            let $target = $navBar.find("ul.menu>li>a").filter("[href='" + window.location.pathname + "']");
            $target.parent("li").addClass("active");
            NProgress.done();
        });
        $(document).on('pjax:end', function() { loadLazy(); contentWayPoint(); });
    };

    $(function() {
        themModeEvent();
        optionEvent();
        circleMagic();
        loadLazy();
        contentWayPoint();
        dynamicEvent();
        postEvent();
        if (openPjax === "true") {
            pjaxEvent();
        }
        loadResource();
    });
})(jQuery);