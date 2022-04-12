;(function($) {
    "use strict";

    let defaultSetting = {
        tocNav: "tocNav",
        tocContainer: "#tocContainer",
        postContainer: "#postContainer", // 文章内容容器ID
        offsetTop: 0 //目录容器顶部的偏移量
    };

    $.fn.autoToc = function(options) {
        defaultSetting = $.extend(defaultSetting, options || {});

        function init() {
            method.createToc();
            method.bindClick();
            method.calcPositon();
        }

         init();
    };

    let method = {
        createToc:  function() {
            let arr = ["<ul id='"+defaultSetting.tocNav+"' class='toc-nav'>"];
            $(defaultSetting.postContainer).find("h2").each(function(indx1, e1) {
                arr.push("<li><a href='javascript:void(0)'>" + $(e1).text() + "</a>");
                if ($(e1).nextUntil("h2", "h3").size() > 0) {
                    arr.push("<ul class='toc-second hide'>");
                    $(e1).nextUntil("h2", "h3").each(function(indx2, e2) {
                        arr.push("<li><a href='javascript:void(0)'>" + $(e2).text() + "</a></li>");
                    });
                    arr.push("</ul>")
                }
                arr.push("</li>");
            });
            arr.push("</ul>");
            $(defaultSetting.tocContainer).html(arr.join(""));
        },
        bindClick: function() {
            let titleArr = $(defaultSetting.postContainer).find("h2,h3");
            let categoriesNav = $("#" + defaultSetting.tocNav);
            categoriesNav.find("a").each(function(index, domEle) {
                $(domEle).on("click", function() {
                    $(window).scrollTop(titleArr.eq(index).offset().top);
                });
            });
        },
        calcPositon: function() {
            let categoriesNav = $("#" + defaultSetting.tocNav);
            let postContainer = $(defaultSetting.postContainer);
            let tocList = postContainer.find("h2,h3");
            // 滚动事件
            $(window).scroll(function(e) {
                let scrollTop = $(this).scrollTop();
                if (scrollTop + categoriesNav.height() >= postContainer.offset().top + postContainer.height() + 200) {
                    return;
                }

                // 判断是否滚动
                scrollTop > defaultSetting.offsetTop ? categoriesNav.css("top", scrollTop - defaultSetting.offsetTop) : categoriesNav.css("top", 0);

                // 确认当前滚动的目录索引
                let current = 0;
                $.grep(tocList,function(domEle,index) {
                   if (scrollTop >= $(domEle).offset().top - 100) {
                        current = index;
                        return true;
                    }
                });

                // 恢复默认状态
                categoriesNav.find("li").removeClass("active");
                $(".toc-second").addClass("hide");
                 // 激活状态
                let currentToc = categoriesNav.find("li").eq(current);
                currentToc.addClass("active");
                if (!currentToc.parent("ul").hasClass(".toc-nav")) {
                    currentToc.parent("ul").removeClass("hide");
                    currentToc.parent("ul").parent("li").addClass("active");
                }

                // 判断是否需要展开
                if (currentToc.find("ul").size() > 0) {
                    currentToc.find("ul").removeClass("hide");
                }
            });
        }
    }

})(jQuery)
