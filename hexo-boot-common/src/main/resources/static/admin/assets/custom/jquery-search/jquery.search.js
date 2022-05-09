/**
 *  Author: MoonlightL
 *  Description: 文章搜索插件
 */
;(function($) {

    "use strict";

    let RealTimeSearch = function(el, options) {
        this.data = null;
        this.options = options;
        this.$el = $(el);
        this.$container = this.$el;
        this.init();
    };

    RealTimeSearch.prototype.init = function() {
        this.initPanel();
    };

    RealTimeSearch.prototype.initPanel = function() {
        let self = this;
        let htmlArr = [];
        if (self.options.searchId) {
            htmlArr.push('<div class="real-time-search-btn"><i class="fa fa-search"></i></div>');
        }

        htmlArr.push('<div class="real-time-search-body">');
        htmlArr.push('<div class="real-time-search-mask"></div>');
        htmlArr.push('<input type="text" id="keyword-input" placeholder="输入关键字" data-list=".highlight_list" autocomplete="off">');
        htmlArr.push('<ul class="highlight_list" id="real-time-search-content"></ul>');
        htmlArr.push('</div>');

        self.$container.html(htmlArr.join(""));
        self.registerClickEvent();
    };

    RealTimeSearch.prototype.registerClickEvent = function() {
        let self = this;

        let $mask = self.$container.find(".real-time-search-mask");

        let $searchBtn = self.$container.find(".real-time-search-btn");
        if ($searchBtn.length === 0) {
            $searchBtn = $(document).find("[data-real-time-search]");
        }

        if ($searchBtn.length === 0) {
            return;
        }

        let $body = self.$container.find(".real-time-search-body");
        $searchBtn.off("click").on("click", function () {
            if ($body.css("opacity") === "0") {
                $body.addClass("real-time-search-body-show");
                $mask.show();
                self.listRequest();
            } else {
                $body.removeClass("real-time-search-body-show");
                $mask.hide();
            }
        });

        $mask.off("click").on("click", function () {
            $body.removeClass("real-time-search-body-show");
            $(this).hide();
        });
    };

    RealTimeSearch.prototype.listRequest = function(params) {
        let self = this;
        if (self.data) {
            return;
        }

        if (!self.options.listUrl) {
            console.error("listUrl 参数为空");
            return;
        }

        $.ajax({
            type: "GET",
            url: self.options.listUrl,
            data: params ? params.ajaxParams : self.options.ajaxParams,
            dataType: "JSON",
            success: function(resp) {
                resp = self.options.listHandler(resp);
                if (resp.success) {
                    self.data = resp.data;
                    let htmlArr = [];
                    for(let i = 0; i < self.data.length; i++) {
                        let post = self.data[i];
                        htmlArr.push("<li><img src='" + post.coverUrl + "' width='32p' height='32'/><a href='/" + post.link + "'>" + post.title + "</a></li>");
                    }
                    self.$container.find("#real-time-search-content").html(htmlArr.join(""));

                    self.$container.find("#keyword-input").hideseek({
                        highlight: true
                    });
                }
            },
            error: function() {
                console.error("请求异常，请查看网络状态");
            }
        });

    };

    RealTimeSearch.DEFAULT_OPTIONS = {
        id: "real-time-search-container",        // 搜索容器 id
        searchId: "",                            // 搜索按钮容器 id
        listUrl: "",                             // 请求数据 url
        ajaxParams: {},                          // 请求参数
        wrapClass: "",                           // 包裹样式
        listHandler: function(resp) {            // 请求响应数据处理
            return resp;
        }
    };

    RealTimeSearch.METHODS = [
        "refresh",
        "destroy"
    ];

    $.fn.RealTimeSearch = function(params) {
        let value,
            args = Array.prototype.slice.call(arguments, 1);
        this.each(function() {
            let $this = $(this),
                data = $this.data('real.search'),
                options = $.extend({}, RealTimeSearch.DEFAULT_OPTIONS, params);

            if (typeof params === 'string') {

                if ($.inArray(params, RealTimeSearch.METHODS) < 0) {
                    throw new Error("Unknown method: " + params);
                }

                if (!data) {
                    return;
                }

                value = data[params].apply(data, args);
                if (params === 'destroy') {
                    $this.removeData('real.search');
                }
            }

            if (!data) {
                $this.data('real.search', (data = new RealTimeSearch(this, options)));
            }
        });

        return typeof value === 'undefined' ? this : value;
    };

})(jQuery);