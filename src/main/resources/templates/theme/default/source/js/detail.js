let CommentManager = (function ($) {

    let CommentManager = {
        init: function (nickname, postId, comment) {
            let flag = false;
            let postContainer = $("#postContainer");
            let mobile = isMobile();
            let scrollHeight = (mobile ? 1200 : 850);
            $(window).scroll(function(e) {
                let scrollTop = $(this).scrollTop();
                if (!flag && (scrollTop > parseInt(postContainer.offset().top + postContainer.height() - scrollHeight))) {
                    // 获取评论列表
                    flag = true;
                    CommentManager.initComment(nickname, postId, comment);
                    CommentManager.bindEvent(postId);
                }
            });
        },
        initComment: function (nickname, postId, comment) {
            $("#comment-container").BeautyComment({
                title: "评论",
                subTitle: "最新评论",
                bloggerName: nickname,
                baseUrl: "/admin/assets/custom/",
                listUrl: "/commentList.json",
                sendUrl: "/auth/sendComment.json",
                wrapClass: "ml-content",
                canComment: comment,
                ajaxParams: {postId: postId, pageNum: 1, pageSize: 10},
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
        },
        bindEvent: function (postId) {
            // 点赞
            $("#priseBtn").on("click",function () {
                if (sessionStorage.getItem("hasPrize" + postId)) {
                    return;
                }

                $.post("/praisePost/" + postId, null, function (resp) {
                    if (resp.success) {
                        $("#prizeCount").text(resp.data);
                        sessionStorage.setItem("hasPrize" + postId, "y");
                    }
                },"json");

            });
        }

    };



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
    $("#shareOpenBtn").on("click",function () {
       let shareBtns = $("#shareBtns");
       if (shareBtns.hasClass("share-open")) {
           shareBtns.removeClass("share-open");
       } else {
           shareBtns.addClass("share-open");
       }
    });

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
        init: CommentManager.init
    }

})(jQuery);