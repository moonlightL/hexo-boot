;(function($){

    "use strict";

    let defaultSetting = {
        homeUrl: "",
        homeName: "主页",
        bottom: 135,
        tabCallback: null,
        index: ""
    };

    $.fn.tab = function(options) {
        defaultSetting = $.extend(defaultSetting, options || {});
        let that = this;
        init(that);
    };

    function init(obj) {
        if (defaultSetting.homeUrl) {
            initTabContainer(obj);
        }
        bindEvent();
        $("#tab_" + defaultSetting.index).trigger("click");
    }

    function initTabContainer(obj) {
        defaultSetting.index = defaultSetting.homeUrl.replace(/\./g, '_').replace(/\//g, '_').replace(/:/g, '_').replace(/\?/g, '_').replace(/,/g, '_').replace(/=/g, '_').replace(/&/g, '_');
        let index = defaultSetting.index;
        let arr = [];
        arr.push("<div class='tab-container-menu'>");
        arr.push("<div class='tab-left'>");
        arr.push("<a href='javascript:;'><i class='fa fa-arrow-left'></i></a>");
        arr.push("</div>");
        arr.push("<div class='tab-right'>");
        arr.push("<a href='javascript:;'><i class='fa fa-arrow-right'></i></a>");
        arr.push("</div>");
        arr.push("<ul id='jquery-tabs' class='tabs'>");
        arr.push("<li class='tabs-item active' id='tab_" + index + "' data-close='false' data-index='"+ index +"' data-url='" + defaultSetting.homeUrl + "'><a href='javascript:;'>" + defaultSetting.homeName + "</a></li>");
        arr.push("</div>");
        arr.push("<div class='tab-container-content' id='tab-container-content'>");
        arr.push("<div id='iframe_tab_" + index + "' class='iframe active'>");
        arr.push("<iframe class='tab-iframe' src='" + defaultSetting.homeUrl + "' frameborder='0' border='0' width='100%' onload='changeFrameHeight(this)' scrolling='yes'></iframe>");
        arr.push("</div>");
        arr.push("</div>");

        $(obj).append(arr.join(""));
    }

    function bindEvent() {

        // 点击标签
        $(document).off("click").on("click",".tabs li", function() {
            // 切换 tab
            $(".tabs-item").removeClass("active");
            $(this).addClass("active");
            if (typeof defaultSetting.tabCallback == "function") {
                let index = $(this).data("index");
                let url = $(this).data("url");
                defaultSetting.tabCallback(url, this);
            }

            // 切换 iframe
            $(".iframe").removeClass("active");
            $("#iframe_" + $(this).attr("id")).addClass("active");

            // 检测是否需要滑动
            checkScroll($(this));
        });

        // 打开标签
        $(document).on("click","a[data-url], button[data-url]", function() {
            TabManager.openTab(this);
        });
        // $("a[data-url]").on("click", function() {
        //     TabManager.openTab(this);
        // });
    }

    // 控制选项卡滚动位置
    $(document).on('click', '.tab-left a', function () {
        let $tabContainer = $('#jquery-tabs');
        $tabContainer.animate({scrollLeft: $tabContainer.scrollLeft() - 300}, 200, function () {
            initScrollState($tabContainer);
        });
    });

    // 向右箭头
    $(document).on('click', '.tab-right a', function () {
        let $tabContainer = $('#jquery-tabs');
        $tabContainer.animate({scrollLeft: $tabContainer.scrollLeft() + 300}, 200, function () {
            initScrollState($tabContainer);
        });
    });

    function checkScroll($tab) {
        let $tabContainer = $("#jquery-tabs");
        let marginLeft = $tabContainer.css("marginLeft").replace("px", "");
        // 滚动到可视区域:在左侧
        if ($tab.position().left < marginLeft) {
            let left = $tabContainer.scrollLeft() + $tab.position().left - marginLeft;
            $tabContainer.animate({scrollLeft: left}, 200, function () {
                initScrollState($tabContainer);
            });
        }
        // 滚动到可视区域:在右侧
        if(($tab.position().left + $tab.width() - marginLeft) > document.getElementById("jquery-tabs").clientWidth) {
            let left = $tabContainer.scrollLeft() + (($tab.position().left + $tab.width() - marginLeft) - document.getElementById("jquery-tabs").clientWidth);
            $tabContainer.animate({scrollLeft: left}, 200, function () {
                initScrollState($tabContainer);
            });
        }
    }

    function initScrollState($tabContainer) {
        if ($tabContainer.length > 0) {

            if ($tabContainer.scrollLeft() === 0) {
                $('.tab-left a').removeClass('active');
            } else {
                $('.tab-left a').addClass('active');
            }

            if (($tabContainer.scrollLeft() + $tabContainer.get(0).clientWidth) >= $tabContainer.get(0).scrollWidth) {
                $('.tab-right a').removeClass('active');
            } else {
                $('.tab-right a').addClass('active');
            }
        }
    }

    window.changeFrameHeight = function(iframe) {
        iframe.height =  document.documentElement.clientHeight - defaultSetting.bottom;
    };

    window.onresize = function() {
        $(".tab-iframe").height(document.documentElement.clientHeight - defaultSetting.bottom);
        initScrollState($("#jquery-tabs"));
    };

    let TabManager = {
        openTab: function(obj) {
            // 取消激活
            $(".tabs-item").removeClass("active");
            $(".iframe").removeClass("active");

            let tabName = $(obj).data("name") || $(obj).text();
            let url = $(obj).data("url");
            let index = url.replace(/\./g, '_').replace(/\//g, '_').replace(/:/g, '_').replace(/\?/g, '_').replace(/,/g, '_').replace(/=/g, '_').replace(/&/g, '_');
            // 如果标签已存在，重新激活
            if ($("#tab_" + index).length > 0) {
                $("#tab_" + index).trigger("click");
            } else {
                // 创建 tab
                let tab = "<li class='tabs-item active' id='tab_"+index+"' data-close='true' data-index='"+ index +"' data-url='"+ url +"'><a href='javascript:;'>"+tabName+"</a></li>";
                $("#jquery-tabs").append(tab);

                // 创建 iframe
                let iframe = "<div id='iframe_tab_"+index+"' class='iframe active'><iframe class='tab-iframe' src='"+url+"' frameborder='0' width='100%' onload='changeFrameHeight(this)'></iframe></div>";
                $("#tab-container-content").append(iframe);

                // 检测是否需要滑动
                checkScroll($("#tab_" + index));
            }
        },
        closeTab: function(item) {

            if (!item.data("close")) {
                return false;
            }

            // 如果移除的是激活的标签，则激活前一个标签
            if (item.hasClass("active")) {
                item.prev().trigger("click");
            }

            $("#iframe_" + item.attr("id")).remove();
            item.remove();

            initScrollState($("#jquery-tabs"));
        }
    };

    let menu = new BootstrapMenu("#jquery-tabs li", {
        fetchElementData: function(item) {
            return item;
        },
        actions: {
            close: {
                name: "关闭",
                iconClass: "fa fa-close",
                onClick: function(item) {
                    TabManager.closeTab(item);
                }
            },
            closeOther: {
                name: "关闭其他",
                iconClass: 'fa fa-window-close',
                onClick: function(item) {
                    let index = item.attr("id");
                    $(".tabs li").each(function() {
                        if($(this).attr("id") != index) {
                            TabManager.closeTab($(this));
                        }
                    });
                }
            },
            closeAll: {
                name: "关闭全部",
                iconClass: 'fa fa-window-close-o',
                onClick: function() {
                    $(".tabs li").each(function() {
                        TabManager.closeTab($(this));
                    });
                }
            },
            closeRight: {
                name: "关闭右侧所有",
                iconClass: 'fa fa-angle-double-right',
                onClick: function(item) {
                    let index = item.attr("id");
                    $($('.tabs li').toArray().reverse()).each(function() {
                        if ($(this).attr("id") != index) {
                            TabManager.closeTab($(this));
                        } else {
                            return false;
                        }
                    });
                }
            },
            closeLeft: {
                name: "关闭左侧所有",
                iconClass: 'fa-angle-double-left',
                onClick: function(item) {
                    let index = item.attr("id");
                    $('.tabs li').each(function() {
                        if ($(this).attr("id") != index) {
                            TabManager.closeTab($(this));
                        } else {
                            return false;
                        }
                    });
                }
            },
            refresh: {
                name: "刷新",
                iconClass: 'fa fa-refresh',
                onClick: function(item) {
                    if (item.data("index") === 0) {
                        window.location.reload();
                    } else {
                        let index = item.attr("id");
                        let $iframe = $('#iframe_' + index).find('iframe');
                        $iframe.attr('src', $iframe.attr('src'));
                    }
                }
            }
        }
    });

})(jQuery);
