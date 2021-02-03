function initComment() {
    $("#about-comment").BeautyComment({
        title: "留言",
        subTitle: "最新留言",
        baseUrl: baseLink + "/source/js",
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