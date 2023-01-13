/**
 *  Author: MoonlightL
 *  Description: 视图切换插件，半成品
 */
;(function($) {

    "use strict";

    let ToggleView = function(options) {
        this.options = options;
        this.init();
        this.bindEvent();
    };

    ToggleView.prototype.init = function() {
       let key = this.options.key;
        let value = $.hexo.storage.get(key) || "view-list";
        $("#" + value).addClass("active");
        if (value == "view-list") {
            this.initTable(value);
        } else {
            this.initGrid(value);
        }
    };

    ToggleView.prototype.bindEvent = function() {
        let key = this.options.key;
        let viewObj = this;
        $(".btn-view").off("click").on("click", function() {
            let that = $(this);
            if (that.hasClass("active")) {
                return;
            }

            let id = that.attr("id");

            // 切换样式
            $(".btn-view").removeClass("active");
            that.addClass("active");
            $.hexo.storage.set(key, id);

            // 视图数据
            $(".data-view").hide();

            if (id == "view-list") {
                viewObj.initTable(id);
            } else {
                viewObj.initGrid(id);
            }
        });
    };

    ToggleView.prototype.initTable = function(id) {
        this.options.initTable(id);
    };

    ToggleView.prototype.initGrid = function(id) {
      this.options.initGrid(id);
    };

    ToggleView.DEFAULT_OPTIONS = {
        key: "",
        baseUrl: "",
        initTable: function(id) {
            $("#data-" + id).show();
            $("#toolbar").show();
            let tableContainer = $(".hexo-table-container");
            if (tableContainer.length == 0) {
                $.hexo.table.init({
                    baseUrl: ToggleView.DEFAULT_OPTIONS.baseUrl,
                    height: $(window.parent).height() - 283
                });
            } else {
                $.hexo.table.refreshData();
            }
        },
        initGrid: function(id) {}
    };

    $.ToggleView = function(options) {
        return new ToggleView($.extend(ToggleView.DEFAULT_OPTIONS, options || {}));
    };

})(jQuery);