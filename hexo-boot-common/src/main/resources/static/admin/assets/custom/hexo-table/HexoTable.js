/**
 *  Author: MoonlightL
 *  Description: 仿 BootStrap Table 编写的表格插件，仅限于本项目使用！！！
 */
(function($) {

    "use strict";

    let HexoTable = function(el, options) {
        this.options = options;
        this.$el = $(el);
        this.$container = $(el).parent(),
        // 分页信息
        this.pageInfo = {};
        // 行数据映射(选中行记录 id -> 行数据)
        this.dataMap = {};
        // 选中行记录 id
        this.selectedDataIds = [];
        this.init();
    };

    // 初始化 table
    HexoTable.prototype.init = function(params) {
        this.initContainer();
        this.initBody();
        this.sendRequest(params);
    };

    // 初始化容器
    HexoTable.prototype.initContainer = function() {
        let self = this;
        if (self.options.height) {
            self.$container.css("height", self.options.height - 26);
        }
    };

    // 初始化 body
    HexoTable.prototype.initBody = function() {
        let self = this;
        let $div = $('<div class="hexo-table-container" style="overflow: auto;height: '+ (self.options.height - 160) +'px"></div>');
        let $table = $('<table class="table table-striped table-vcenter hexo-table-content"></table>');
        self.$body = $('<tbody class="hexo-table-tbody"></tbody>');
        $table.append(self.$body);
        $div.append($table);
        self.$container.append($div);
    };

    // 发送请求
    HexoTable.prototype.sendRequest = function(params) {
        let self = this;

        if (!self.options.url) {
            console.error("url 参数为空");
            return;
        }

        $.ajax({
            type: self.options.type,
            url: self.options.url,
            data: params ? params.ajaxParams : self.options.ajaxParams,
            dataType: "JSON",
            success: function(resp) {
                resp = self.options.responseHandler(resp);

                if (!resp.success) {
                    console.error("请求失败");
                    return;
                }

                self.pageInfo = resp.data;
                self.renderTable();
                self.renderPagination();
            },
            error: function() {
                console.error("请求异常，请查看网络状态");
            }
        });

    };

    // 渲染表格
    HexoTable.prototype.renderTable = function() {
        let self = this;
        self.$body.html("");

        let tr = self.$el.find("tr").eq(0);
        if (!tr) {
            console.error("没有配置表头");
            return;
        }

        self.$el.find("input[type='checkbox']").prop("checked", false);

        let fieldArr = [];
        let formatterArr = [];
        let $thArr = $(tr).find("th");
        $thArr.each(function(index, domEle) {
            fieldArr[index] = "undefined";

            let fieldName = $(domEle).data("field");
            if (fieldName) {
                fieldArr[index] = fieldName;
            } else {
                let checkbox = $(domEle).data("checkbox");
                if (checkbox) {
                    fieldArr[index] = "checkbox";
                }
            }

            let formatter = $(domEle).data("formatter");
            if (formatter) {
                formatterArr[index] = formatter;
            }

        });

        let list = self.pageInfo.list;
        if (!list || list.length == 0) {
            self.$body.html("<tr align='center'><td style='text-align: center!important;' colspan='" + fieldArr.length + "'><div style='padding:18px'>暂无数据</div></td></tr>");
            return;
        }

        // 缓存数据
        self.cacheData(list);

        let dataArr = [];
        for (let i = 0; i < list.length; i ++) {
            let obj = list[i];
            let tr = ["<tr>"];
            for (let j = 0; j < fieldArr.length; j ++) {
                let th = $thArr[j];
                if (fieldArr[j] == "checkbox") {
                    tr.push("<td width='"+($(th).attr('width'))+"' class='text-center'><label class='css-control css-control-primary css-checkbox' for='c_" + obj.id + "'><input type='checkbox' name='hexo-checkbox' class='css-control-input' id='c_" + obj.id + "'><span class='css-control-indicator'></span><label</td>")
                } else {
                    let fieldName = fieldArr[j];
                    let fieldValue = obj[fieldName];
                    let formatter = formatterArr[j];
                    if (typeof window[formatter] == "function") {
                        fieldValue = window[formatter](fieldValue, obj);
                    }

                    tr.push("<td width='"+($(th).attr('width'))+"'>" + fieldValue + "</td>")
                }
            }
            tr.push("</tr>");
            dataArr.push(tr.join(""));
        }

        self.$body.html(dataArr.join(""));

        self.registerRowClickEvent();

        // 调整两个 table 宽度
        let parentHeight = $(".hexo-table-container").height();
        let contentHeight = $(".hexo-table-content").height();
        if(contentHeight > parentHeight){
            self.$el.width($(".hexo-table-content").width());
        }

    };

    // 缓存数据
    HexoTable.prototype.cacheData = function(data) {
        let self = this;
        $.each(data, function(index, item) {
            self.dataMap["c_" + item.id] = item;
        });
    };

    // 初始化分页条
    HexoTable.prototype.renderPagination = function() {
        let self = this;
        let pageInfo = self.pageInfo;
        if (!pageInfo.list || pageInfo.list.length == 0) {
            return;
        }

        let pageArr = [];
        pageArr.push("<div class='page-info'>");
        pageArr.push("<div class='left page-info-record'>");

        pageArr.push("<span>当前页：" + pageInfo.pageNum + "，总共 " + pageInfo.total + " 条记录，每页显示 " + pageInfo.pageSize + " 条记录</span>");
        pageArr.push("</div>");

        pageArr.push("<div class='right page-info-num'>");
        pageArr.push("<button data-num='" + (pageInfo.prePage) +"' class='"+(pageInfo.hasPreviousPage ? 'btn btn-default' : 'btn btn-default disabled')+"'><i class='fa fa-angle-double-left'></i></button>");
        for (let i = 0; i < pageInfo.navigatepageNums.length; i ++) {
            let pageNum = pageInfo.navigatepageNums[i];
            pageArr.push("<button class='"+(pageNum == pageInfo.pageNum ? 'btn btn-default active' : 'btn btn-default')+"' data-num='" + pageNum + "'>" + pageNum + "</button>");
        }
        pageArr.push("<button data-num='" + (pageInfo.nextPage) + "' class='"+(pageInfo.hasNextPage ? 'btn btn-default' : 'btn btn-default disabled')+"'><i class='fa fa-angle-double-right'></i></button>");
        pageArr.push("</div>");
        pageArr.push("</div>");

        self.$container.append(pageArr.join(""));

        self.registerPageClickEvent();
    };

    // ############################# 【注册事件相关 START 】 ######################################

    // 注册行点击事件
    HexoTable.prototype.registerRowClickEvent = function() {
        let self = this;

        // 点击单元格事件
        self.$container.find("tbody").find("td").off("click").on("click", function() {
            let $td = $(this),
                $tr = $td.parent();
            let $checkbox = $tr.find("input[name='hexo-checkbox']");
            let checked = $checkbox.prop("checked");
            $checkbox.prop("checked", !checked);

            let id = $checkbox.attr("id");
            let index = $.inArray(id, self.selectedDataIds);
            if (checked) {
                // 移除
                if (index > -1) {
                    self.selectedDataIds.splice(index, 1);
                }
            } else {
                // 添加
                if (index < 0) {
                    self.selectedDataIds.push(id);
                }
            }

            // 判断是否勾选全选框
            self.$el.find("input[type='checkbox']").prop("checked", Object.keys(self.dataMap).length == self.selectedDataIds.length);
        });

        // 点击全选 checkbox 事件
        self.$el.find("input[type='checkbox']")
            .prop("checked", false)
            .off("click").on("click", function() {
            let val = $(this).prop("checked");
            $("input[name='hexo-checkbox']").prop("checked", val).trigger("click");
        });
    };

    HexoTable.prototype.registerPageClickEvent = function() {
        let self = this;
        let pages = $(".page-info-num").find("button");
        pages.off("click").on("click", function(e) {

            let num = $(e.target).data("num");
            if (!num || num < 1 ) {
                console.warn("=====当前列表不能翻页====");
                return;
            }

            self.options.ajaxParams = $.extend({}, self.options.ajaxParams, {pageNum: num});
            self.refresh(self.options);
        });
    };

    // ############################# 【注册事件相关 END 】 ######################################


    // ############################# 【对外 API START 】 ######################################

    // 获取被选中的数据
    HexoTable.prototype.getSelections = function() {
        let self = this;
        if (self.selectedDataIds) {
            let result = [];
            $.each(self.selectedDataIds, function(index, id) {
                let checkRow = self.dataMap[id];
                result.push(checkRow);
            });
            return result;
        }
    };

    // 获取参数
    HexoTable.prototype.getOptions = function() {
        let options = $.extend({}, this.options);
        return $.extend(true, {}, options);
    };

    // 销毁 table
    HexoTable.prototype.destroy = function() {
        this.$container.find(".hexo-table-container").remove();
        this.$container.find(".page-info").remove();
        this.pageInfo = {};
        this.list = [];
        this.dataMap = {};
        this.selectedDataIds = [];
    };

    // 刷新
    HexoTable.prototype.refresh = function(params) {
        this.destroy();
        this.init(params);
    };

    // 刷新参数
    HexoTable.prototype.refreshOptions = function(params) {
        this.options.ajaxParams = params.ajaxParams;
        this.destroy();
        this.init(params);
    };

    // ############################# 【对外 API END 】 ######################################


    HexoTable.DEFAULT_OPTIONS = {
        id: "list-table",
        url: "",
        type: "GET",
        ajaxParams: {pageNum: 1, pageSize: 10},
        responseHandler: function(resp) {
            return resp;
        },
    };

    HexoTable.METHODS = [
        "getSelections",
        "getOptions",
        "refresh",
        "refreshOptions",
        "destroy"
    ];

    $.fn.HexoTable = function(params) {
        let value,
            args = Array.prototype.slice.call(arguments, 1);
        this.each(function() {
            let $this = $(this),
                data = $this.data('hexo.table'),
                options = $.extend({}, HexoTable.DEFAULT_OPTIONS, params);

            if (typeof params === 'string') {

                if ($.inArray(params, HexoTable.METHODS) < 0) {
                    throw new Error("Unknown method: " + params);
                }

                if (!data) {
                    return;
                }

                value = data[params].apply(data, args);
                if (params === 'destroy') {
                    $this.removeData('hexo.table');
                }
            }

            if (!data) {
                $this.data('hexo.table', (data = new HexoTable(this, options)));
            }
        });

        return typeof value === 'undefined' ? this : value;
    };

})(jQuery);