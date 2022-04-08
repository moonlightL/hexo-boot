;(function($) {
    let $installForm = $("#installForm");
    let installFormValidator = $installForm.validate({
        errorClass: 'invalid-feedback animated fadeInDown',
        errorElement: 'div',
        errorPlacement: (error, e) => {
            jQuery(e).parents('.form-group').append(error);
        },
        highlight: e => {
            jQuery(e).closest('.form-group').removeClass('is-invalid').addClass('is-invalid');
        },
        success: e => {
            jQuery(e).closest('.form-group').removeClass('is-invalid');
            jQuery(e).remove();
        },
        rules: {
            'username': {
                required: true,
                minlength: 4
            },
            'password': {
                required: true,
                minlength: 6
            },
            'confirm-password': {
                required: true,
                minlength: 6,
                equalTo: "#install-password"
            }
        },
        messages: {
            'username': {
                required: '请输入用户名',
                minlength: '用户名长度至少4位数'
            },
            'password': {
                required: '请输入密码',
                minlength: '密码长度至少6位数'
            },
            'confirm-password': {
                required: '请输入确认密码',
                minlength: '密码长度至少6位数',
                equalTo: "两次密码不一致"
            }
        },
        submitHandler: function (form) {
            $("#submitBtn").prop("disabled", true);
            $.ajax({
                type: "POST",
                url: "/admin/install.json",
                data: $installForm.serialize(),
                success: function (resp) {
                    if (resp.success) {
                        swal({
                            title: "",
                            text: "安装成功",
                            type: "success",
                            confirmButtonColor: "#DD6B55",
                            confirmButtonText: "确定",
                            onClose: function () {
                                window.location.href = resp.data;
                            }
                        });
                    } else {
                        swal({
                            title: "",
                            text: resp.message,
                            type: "error",
                            confirmButtonColor: "#DD6B55",
                            confirmButtonText: "确定"
                        });
                    }
                }
            });
        }
    });

    $('.js-wizard-validation-classic').bootstrapWizard({
        tabClass: 'nav nav-tabs',
        nextSelector: '[data-wizard="next"]',
        previousSelector: '[data-wizard="prev"]',
        finishSelector: '[data-wizard="finish"]',
        onTabShow: (tab, navigation, index) => {
            let percent = ((index + 1) / navigation.find('li').length) * 100;
            let progress = navigation.parents('.block').find('[data-wizard="progress"] > .progress-bar');
            if (progress.length) {
                progress.css({ width: percent + 1 + '%' });
            }
        },
        onNext: (tab, navigation, index) => {
            if(!$installForm.valid()) {
                installFormValidator.focusInvalid();
                return false;
            }
        },
        onTabClick: (tab, navigation, index) => {
            jQuery('a', navigation).blur();
            return false;
        }
    });
})(jQuery);