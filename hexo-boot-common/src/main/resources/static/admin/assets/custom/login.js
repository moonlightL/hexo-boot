;(function($) {

    if (window.parent != window) {
        window.parent.location.href = "/admin/login.html";
    }

    $("#verify-code-img").on("click", function() {
        $(this).attr("src", "/admin/captcha.jpg?id=" + Math.random());
    });

    $("#verify-code-img").trigger("click");

    $("#loginForm").validate({
        errorClass: 'invalid-feedback animated fadeInDown',
        errorElement: 'div',
        errorPlacement: (error, e) => {
            jQuery(e).parents('.form-group > div').append(error);
        },
        highlight: e => {
            jQuery(e).closest('.form-group').removeClass('is-invalid').addClass('is-invalid');
        },
        success: e => {
            jQuery(e).closest('.form-group').removeClass('is-invalid');
            jQuery(e).remove();
        },
        rules: {
            'login-username': {
                required: true,
                minlength: 4
            },
            'login-password': {
                required: true,
                minlength: 6
            },
            'login-verifyCode': {
                required: true
            }
        },
        messages: {
            'login-username': {
                required: '请输入用户名',
                minlength: '用户名长度至少4位数'
            },
            'login-password': {
                required: '请输入密码',
                minlength: '密码长度至少6位数'
            },
            'login-verifyCode': {
                required: '请输入验证码',
            }
        },
        submitHandler: function (form) {
            let username = $("#login-username").val();
            let password = $("#login-password").val();
            let verifyCode = $("#login-verifyCode").val();
            $.ajax({
                type: "POST",
                url: "/admin/login.json",
                data: {username: username, password: password, verifyCode: verifyCode},
                success: function (resp) {
                    if (resp.success) {
                        window.location.href = resp.data;
                    } else {
                        swal({
                            title: "登录信息",
                            text: resp.message,
                            type: "error",
                            confirmButtonColor: "#DD6B55",
                            confirmButtonText: "确定"
                        });

                        if (resp.code !== 600) {
                            $("#verify-code-img").trigger("click");
                        }
                    }
                }
            });
        }
    });

})(jQuery);