;(function($) {

    $.extend({
        hexo: {
            table: {
                options: {
                    id: "listTable", // table id
                    baseUrl: "",
                    addUIUrl: "addUI.html", // 新增页面地址
                    editUIUrl: "editUI/{id}.html", // 编辑页面地址
                    detailUIUrl: "detailUI/{id}.html", // 详情页面地址
                    listUrl: "list.json", // 列表数据地址
                    removeUrl: "remove.json", // 删除地址
                    queryFormId: "queryForm", // 搜索框表单 id
                    toolbar: "#toolbar",
                    height: $(window.parent).height() - 330,
                    parentId: ""
                },
                init: function(options) {
                    $.extend($.hexo.table.options, options || {});

                    // 创建表格
                    $("#" + $.hexo.table.options.id).HexoTable({
                        url: $.hexo.table.getListUrl(),
                        height: $.hexo.table.options.height
                    });

                    // 绑定工具栏按钮事件
                    $.hexo.event.bind();

                },
                getListUrl: function() {
                    return $.hexo.table.formatUrl($.hexo.table.options.listUrl);
                },
                getAddUIUrl: function() {
                    return $.hexo.table.formatUrl($.hexo.table.options.addUIUrl);
                },
                getEditUrl: function(id) {
                    return $.hexo.table.formatUrl($.hexo.table.options.editUIUrl.replace(/{id}/, id));
                },
                getRemoveUrl: function() {
                    return $.hexo.table.formatUrl($.hexo.table.options.removeUrl);
                },
                getDetailUIUrl: function(id) {
                    return $.hexo.table.formatUrl($.hexo.table.options.detailUIUrl.replace(/{id}/, id));
                },
                formatUrl: function(url) {
                    return ($.hexo.table.options.baseUrl || BASE_URL) + "/" + url;
                },
                getSelections: function() {
                    return $("#" + $.hexo.table.options.id).HexoTable("getSelections");
                },
                refreshData: function() {
                    $("#" + $.hexo.table.options.id).HexoTable("refresh");
                },
                refreshOption: function(options) {
                    $("#" + $.hexo.table.options.id).HexoTable("refreshOptions", options);
                }
            },
            event: {
                bind: function() {
                    $($.hexo.table.options.toolbar).find("button").off("click").on("click", function(e) {
                        let $target = $(this);
                        if ($target.hasClass("hexo-add")) {
                            $.hexo.action.showAddUI($target);
                        } else if ($target.hasClass("hexo-edit")) {
                            $.hexo.action.showEditUI(null, $target);
                        } else if ($target.hasClass("hexo-remove")) {
                            $.hexo.action.remove();
                        } else if ($target.hasClass("hexo-refresh")) {
                            $.hexo.table.refreshData();
                        } else if ($target.hasClass("hexo-query")) {
                            let $area = $("#" + $target.data("queryArea"));
                            if ($area.hasClass("hide")) {
                                $area.slideDown("slow");
                                $area.removeClass("hide");
                            } else {
                                $area.addClass("hide");
                                $area.slideUp("slow");
                            }
                        }
                    });

                    $.hexo.action.query();
                }
            },
            action: {
                showAddUI: function ($target) {
                    if (!$.hexo.table.options.addUIUrl) {
                        $.hexo.modal.msg("未设置新增页面地址");
                        return;
                    }
                    $.hexo.modal.window("新增", $.hexo.table.getAddUIUrl(), $target);
                },
                showEditUI: function (id, $target) {
                    if (!$.hexo.table.options.editUIUrl) {
                        $.hexo.modal.msg("未设置编辑页面地址");
                        return;
                    }

                    if (!id) {
                        let selections = $.hexo.table.getSelections();
                        if (selections.length === 0) {
                            $.hexo.modal.tip("请选择记录进行编辑操作");
                            return;
                        }

                        if (selections.length > 1) {
                            $.hexo.modal.tip("只能选择一条记录进行编辑操作");
                            return;
                        }

                        id = selections[0].id
                    }

                    $.hexo.modal.window("编辑", $.hexo.table.getEditUrl(id), $target);
                },
                save: function (formId, formData, fn) {
                    $('#' + formId).bootstrapValidator().off('success.form.bv').on('success.form.bv', function(e) {
                        e.preventDefault();

                        let $form = $(e.target);
                        let paramObj = {};
                        paramObj.url = $form.attr('action');

                        if (formData) {
                            paramObj.params = formData;
                        } else {
                            paramObj.params = $form.serialize();
                        }

                        if (typeof fn != "function") {
                            fn = function(resp) {
                                $.hexo.modal.tip("操作成功", "success", function () {
                                    window.parent.$.hexo.modal.close();
                                    window.parent.$.hexo.table.refreshData();
                                });
                            }
                        }
                        paramObj.callback = fn;
                        $.hexo.action.sendRequest(paramObj);
                    }).on('status.field.bv', function(e, data) {
                        if (data.bv.getSubmitButton()) {
                            data.bv.disableSubmitButtons(false);
                        }
                    });
                },
                remove: function(id, fn) {
                    if (!$.hexo.table.options.removeUrl) {
                        $.hexo.modal.tip("未设置删除地址");
                        return;
                    }

                    let idArr = [];
                    if (!id) {
                        let selections = $.hexo.table.getSelections();
                        if (selections.length === 0) {
                            $.hexo.modal.tip("请选择记录进行删除操作");
                            return;
                        }

                        for(let i = 0,len = selections.length; i < len; i++) {
                            idArr.push(selections[i].id);
                        }
                    } else {
                        idArr.push(id);
                    }

                    $.hexo.modal.confirm("确定要删除该记录吗？", function() {
                        $.hexo.action.sendRequest({
                            url: $.hexo.table.getRemoveUrl(),
                            params: {idStr: idArr.join(",")},
                            callback: function(resp) {
                                if (typeof fn == "function") {
                                    fn(resp);
                                } else {
                                    $.hexo.modal.tip("刪除成功", "success", function() {
                                        $.hexo.table.refreshData();
                                    });
                                }
                            }
                        });
                    });
                },
                query: function () {
                    let $queryForm = $("#" + $.hexo.table.options.queryFormId);
                    let $queryBtn = $queryForm.find(".hexo-query-submit");
                    $queryBtn.off("click").on("click", function(e) {
                        let formParamArr = $queryForm.serializeArray();
                        let parameter = {};
                        for (let i = 0, len = formParamArr.length; i < len; i++) {
                            parameter[formParamArr[i].name] = formParamArr[i].value;
                        }
                        let options = $("#" + $.hexo.table.options.id).HexoTable('getOptions');
                        options.ajaxParams = $.extend({}, options.ajaxParams, parameter);
                        $.hexo.table.refreshOption(options);
                    });

                    let $resetBtn = $queryForm.find(".hexo-reset");
                    $resetBtn.off("click").on("click", function () {
                        $queryForm.get(0).reset();
                    });
                },
                sendRequest: function (paramObj) {
                    $.ajax({
                       type: paramObj.type || "POST",
                       url: paramObj.url,
                       data: paramObj.params,
                       // processData : !paramObj.formData,
                       // contentType : !paramObj.formData,
                       success: function (resp) {
                           if (resp.success) {
                               if (typeof paramObj.callback == "function") {
                                   paramObj.callback(resp);
                               } else {
                                   $.hexo.modal.tip(resp.message, "success");
                               }
                           } else {
                                $.hexo.modal.tip(resp.message, "error");
                           }
                       },
                        error: function (xhr) {
                            if (xhr.responseJSON) {
                                $.hexo.modal.tip(xhr.responseJSON.error, "error");
                            }
                        }
                    });
                },
                download: function(url, type) {
                    $.fileDownload(url,{
                        httpMethod: type || 'GET',
                        // data:data,
                        failCallback: function (responseHtml, url, error) {
                            console.error(url)
                            $.hexo.modal.tip("下载失败，请检查图床管理类型和该文件存储位置是否相符", "error");
                        }
                    });
                }
            },
            modal: {
                // type: warning,error,success,info
                tip: function(msg, type, callback) {
                    swal({
                        title: "",
                        text: msg,
                        type: type || "success",
                        confirmButtonColor: "#DD6B55",
                        confirmButtonText: "确定",
                        onClose: function () {
                            if (typeof callback == 'function') {
                                callback();
                            }
                        }
                    });
                },
                confirm: function(content, callback) {
                    swal({
                        title: content,
                        type: "warning",
                        showCancelButton: true,
                        confirmButtonColor: "#DD6B55",
                        cancelButtonText: "取消",
                        confirmButtonText: "确定"
                    }).then(function(dismiss) {
                        if (dismiss.value === true && typeof callback == 'function') {
                            callback();
                        }
                    });
                },
                _window: function(title, url, width, height, isFull) {
                    $("#modal-iframe").iziModal({
                        iframe: true,
                        headerColor: "#3f9ce8",
                        title: '<i class="fa fa-edit"></i> ' + title || title,
                        width: width,
                        iframeHeight: height,
                        iframeURL: url,
                        fullscreen: true,
                        openFullscreen: isFull,
                        onClosed: function () {
                            $('#modal-iframe').iziModal('destroy');
                        }
                    });

                    $('#modal-iframe').iziModal('open');
                },
                window: function(title, url, $target) {
                    let width = $target.data("width") || 800;
                    let height = $target.data("height") || $(window).height() - 80;
                    let isFull = $target.data("full");

                    if (navigator.userAgent.match(/(iPhone|iPod|Android|ios)/i)) {
                        width = 'auto';
                        height = 'auto';
                    }

                    $.hexo.modal._window(title, url, width, height, isFull);
                },
                close: function () {
                    $('#modal-iframe').iziModal('close');
                }
            },
            storage: {
                set: function(k ,v) {
                    if (typeof v != "string") {
                        v = JSON.stringify(v);
                    }
                    localStorage.setItem(k, v);
                },
                get: function(k) {
                    return localStorage.getItem(k);
                },
                remove: function(k) {
                    localStorage.removeItem(k);
                },
                clear: function() {
                    localStorage.clear();
                }
            }
        }
    });


})(jQuery);

$.ajaxSetup({
    dataType: "JSON",
    headers: {'x-requested-with': 'XMLHttpRequest'},
    cache: false,
    xhrFields: { withCredentials: true },
    crossDomain: true,
    complete: function(xhr) {
        if (xhr.responseJSON) {
            if (xhr.responseJSON.code && xhr.responseJSON.code != 200) {
                $.hexo.modal.tip(xhr.responseJSON.message, "error", function() {
                    if (xhr.responseJSON.code == 1003 || xhr.responseJSON.code == 1004) {
                        window.parent.location.href = "/admin/login.html";
                    }
                });
            }
        }
    }
});