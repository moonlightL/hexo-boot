let GuestBookManager = (function ($) {
    let GuestBookManager = {
        init: function() {
            let flag = false;
            let $footer = $("#footer-copyright");
            let top = parseInt($footer.offset().top);
            let winHeight = $(window).height();
            if (top > winHeight) {
                $(window).scroll(function(e) {
                    let scrollTop = $(this).scrollTop();
                    if (!flag && (winHeight + scrollTop >= top)) {
                        // 获取留言列表
                        flag = true;
                        GuestBookManager.initComment();
                    }
                });
            } else {
                GuestBookManager.initComment();
            }
        },
        initComment: function () {
            $("#comment-container").BeautyComment({
                title: "留言",
                subTitle: "最新留言",
                baseUrl: "/admin/assets/custom/",
                listUrl: "/guestBookList.json",
                sendUrl: "/auth/sendGuestBook.json",
                wrapClass: "ml-content",
                ajaxParams: {pageNum: 1, pageSize: 10},
                listHandler: function (resp) {
                    return {
                        totalNum: resp.data.totalNum,
                        commentList: resp.data.commentList,
                        commentShowType: resp.data.commentShowType
                    }
                },
                sendHandler: function (resp) {
                    return {
                        success: resp.success,
                        message: resp.message
                    };
                }
            });
        }
    };

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