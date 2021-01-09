;(function() {
    let base = "/theme";
    let CURRENT_MODE = "current_mode";

    let checkTheme = function() {
        let mode = sessionStorage.getItem(CURRENT_MODE);
        if (!mode) {
            let hour = (new Date()).getHours();
            mode = (hour >= 6 && hour < 18 ? "light" : "dark");
        }
        $("html").attr("mode", mode);

        if (mode === "light") {
            $(".mode-container").html("<div id='modeBtn' class='mode'><i class='fa fa-moon-o'></i></div>");
        } else {
            $(".mode-container").html("<div id='modeBtn' class='mode'><i class='fa fa-sun-o'></i></div>");
        }
    };

    let changeMode = function() {
        $("#modeBtn").on("click", function () {
            let $html = $("html");
            let mode = ($html.attr("mode") === "light" ? "dark" : "light");
            sessionStorage.setItem(CURRENT_MODE, mode);
            $html.attr("mode", mode);
            if (mode === "light") {
                $(this).html("<i class='fa fa-moon-o'></i>");
            } else {
                $(this).html("<i class='fa fa-sun-o'></i>");
            }
        });
    };

    // 返回顶部
    let goBack = function() {
        let toTop = $("#toTop");
        $(window).scroll(function(e) {
            let scrollTop = $(this).scrollTop();

            if (scrollTop > 500) {
                toTop.removeClass("to-hide");
            } else {
                if (!toTop.hasClass("to-hide")) {
                    toTop.addClass("to-hide");
                }
            }
        });
        toTop.on("click",function() {
            $('html, body').animate({
                scrollTop: $('html').offset().top
            }, 500);
        });
    };

    // 搜索
    let search = function () {
    $("#real-time-search-container").RealTimeSearch({
        searchId: "search-btn",
        listUrl: "/postList.json"
    });
    };

    // 气泡
    let circleMagic = function() {
        $('.image-content').circleMagic({
            radius: 16,
            density: .1,
            color: 'rgba(255,255,255, .4)',
            clearOffset: .3
        });
    };

    // 图片懒加载
    let lazyImage = function() {
        $("img.lazy").lazyload({
            placeholder : base + "/default/source/images/loading.jpg",
            effect: "fadeIn"
        });
    };

    // 滚动
    let contentWayPoint = function () {
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
    };

    let clickEffect = function() {
        let $container = $("#pageContainer");
        $container.on("click", function(e) {
            let $i = $('<b></b>').text('❤');
            let x = e.pageX, y = e.pageY;
            $i.css({
                "z-index": 9999,
                "top": y - 20,
                "left": x,
                "position": "absolute",
                "color": '#ec4444',
                "font-size": 14,
            });

            $i.animate({
                "top": y - 180,
                "opacity": 0
            }, 1500, function() {
                $i.remove();
            });

            $("body").append($i);
        })
    };

    $(function() {
        checkTheme();
        changeMode();
        goBack();
        search();
        lazyImage();
        circleMagic();
        contentWayPoint();
        clickEffect();
  });

})();

