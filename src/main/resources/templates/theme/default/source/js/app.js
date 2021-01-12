;(function() {

    let APP = {
        plugins: {
            lazyLoad: {
                js: baseLink + "/source/js/jquery.lazyload.min.js"
            }
        }
    };

    console.log("%c Theme." + themeName + " v" + version + " %c https://www.extlight.com/ ", "color: white; background: #e9546b; padding:5px 0;", "padding:4px;border:1px solid #e9546b;");

    let CURRENT_MODE = "current_mode";
    const themModeEvent = function() {
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

    const changeModeEvent = function() {
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

    const toTopEvent = function() {
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

    const circleMagic = function() {
        $('.image-content').circleMagic({
            radius: 16,
            density: .1,
            color: 'rgba(255,255,255, .4)',
            clearOffset: .3
        });
    };

    const loadLazy = function() {
        $.getScript(APP.plugins.lazyLoad.js, function() {
            $("img.lazy").lazyload({
                placeholder : baseLink + "/source/images/loading.jpg",
                effect: "fadeIn"
            });
        })
    };

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

    const searchEvent = function() {
        let $body = $("body");
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

        $("#searchBtn").off("click").on("click", function() {
            $('#modal-iframe').iziModal('open');
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
            }, 800, function() {
                $i.remove();
            });

            $("body").append($i);
        })
    };

    $(function() {
        themModeEvent();
        changeModeEvent();
        toTopEvent();
        circleMagic();
        loadLazy();
        contentWayPoint();
        searchEvent();
        clickEffect();
    });
})();