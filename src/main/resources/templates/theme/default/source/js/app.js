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
    };

    const optionEvent = function() {
        let $body = $("body");
        let $options = $('<div class="options animated fadeInRight" id="option"></div>');
        $body.append($options);

        let elements = [
            {"class": "search", "icon": "fa fa-search", "title": "搜索"},
            {"class": "change-mode", "icon": "fa fa-adjust", "title": "黑白模式"},
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
        $.getScript(APP.plugins.lazyLoad.js, function() {
            $("img.lazyload").lazyload({
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

    let clickEffect = function() {
        let $container = $("#pageContainer");
        $container.on("click", function(e) {
            let $i = $('<span class="effect animated zoomIn"></span>');
            let x = e.pageX, y = e.pageY;
            $i.css({
                "top": y - 42,
                "left": x - 42
            });

            $i.animate({
                "opacity": 0
            },600, function () {
                $i.remove();
            });

            $("body").append($i);
        })
    };

    $(function() {
        themModeEvent();
        optionEvent();
        circleMagic();
        loadLazy();
        contentWayPoint();
        clickEffect();
    });
})();