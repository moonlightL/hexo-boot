;(function() {

    AOS.init({
        easing: 'ease-out-back',
        duration: 1000,
        disable: 'mobile'
    });

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

    let preloader = function() {
        let tl = anime.timeline({});
        tl.add({
                targets: '.preloader',
                duration: 1,
                opacity: 1
            })
            .add({
                targets: '.circle-pulse',
                duration: 300,
                //delay: 500,
                opacity: 1,
                zIndex: '-1',
                easing: 'easeInOutQuart'
            },'+=500')
            .add({
                targets: '.preloader__progress span',
                duration: 500,
                width: '100%',
                easing: 'easeInOutQuart'
            },'-=500')
            .add({
                targets: '.preloader',
                duration: 500,
                opacity: 0,
                zIndex: '-1',
                easing: 'easeInOutQuart'
            });
    };


    $(function() {
        preloader();
        goBack();
        search();
  });

})();

