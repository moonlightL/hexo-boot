let GuestBookManager = (function ($) {
    let GuestBookManager = {
        init: function(nickname) {
            $("#comment-container").BeautyComment({
                title: "留言",
                subTitle: "最新留言",
                bloggerName: nickname,
                baseUrl: "/admin/assets/custom/",
                listUrl: "/guestBookList.json",
                sendUrl: "/auth/sendGuestBook.json",
                wrapClass: "ml-content",
                ajaxParams: {pageNum: 1, pageSize: 10},
                listHandler: function (resp) {
                    return {
                        totalNum: resp.data.total,
                        commentList: resp.data.list
                    }
                },
                sendHandler: function (resp) {
                    return {
                        success: resp.success,
                        message: resp.message
                    };
                }
            });
        },
    };

    // let flag = false;
    // let guestBookContainer = $("#guestBookContainer");
    // let mobile = isMobile();
    // let scrollHeight = (mobile ? 1200 : 900);
    // $(window).scroll(function(e) {
    //     let scrollTop = $(this).scrollTop();
    //     if (!flag && (scrollTop > parseInt(guestBookContainer.offset().top + guestBookContainer.height()- scrollHeight) )) {
    //         flag = true;
    //     }
    // });

    function isMobile(){
        if ( navigator.userAgent.match(/Android/i)
            || navigator.userAgent.match(/webOS/i)
            || navigator.userAgent.match(/iPhone/i)
            || navigator.userAgent.match(/iPad/i)
            || navigator.userAgent.match(/iPod/i)
            || navigator.userAgent.match(/BlackBerry/i)
            || navigator.userAgent.match(/Windows Phone/i)
        ) {
            return true;
        }

        return false;
    }

    return {
        init: GuestBookManager.init
    }

})(jQuery);