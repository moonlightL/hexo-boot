;(function() {

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

    let circleMagic = function() {
        $('.image-content').circleMagic({
            radius: 16,
            density: .1,
            color: 'rgba(255,255,255, .4)',
            clearOffset: .3
        });
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
                            if (effect) {
                                el.addClass(effect + ' animated');
                            } else {
                                el.addClass('fadeInUp animated');
                            }
                            el.removeClass('item-animate');
                        }, k * 200, 'easeInOutExpo');
                    });
                }, 100);
            }
        }, {
            offset: '85%'
        });
    };

    $(function() {
        goBack();
        search();
        circleMagic();
        contentWayPoint();
  });

})();

