function initComment(postId, comment) {
    $("#detail-comment").BeautyComment({
        title: "评论",
        subTitle: "最新评论",
        baseUrl: baseLink + "/source/js",
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
}