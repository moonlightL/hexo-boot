/**
 *  Author: MoonlightL
 *  Description: 工具
 */
;(function ($) {

    "use strict";

    let emojiManager = {
        emojiArr: [],
        typeArr: ["QQ表情", "微博表情", "贴吧表情"]
    };

    emojiManager.emojiArr[0] = [
        {'title':'q_微笑','url':'qq/weixiao.gif'},
        {'title':'q_撇嘴','url':'qq/piezui.gif'},
        {'title':'q_色','url':'qq/se.gif'},
        {'title':'q_发呆','url':'qq/fadai.gif'},
        {'title':'q_得意','url':'qq/deyi.gif'},
        {'title':'q_流泪','url':'qq/liulei.gif'},
        {'title':'q_害羞','url':'qq/haixiu.gif'},
        {'title':'q_闭嘴','url':'qq/bizui.gif'},
        {'title':'q_睡','url':'qq/shui.gif'},
        {'title':'q_大哭','url':'qq/daku.gif'},
        {'title':'q_尴尬','url':'qq/ganga.gif'},
        {'title':'q_发怒','url':'qq/fanu.gif'},
        {'title':'q_调皮','url':'qq/tiaopi.gif'},
        {'title':'q_呲牙','url':'qq/ciya.gif'},
        {'title':'q_惊讶','url':'qq/jingya.gif'},
        {'title':'q_难过','url':'qq/nanguo.gif'},
        {'title':'q_酷','url':'qq/ku.gif'},
        {'title':'q_冷汗','url':'qq/lenghan.gif'},
        {'title':'q_抓狂','url':'qq/zhuakuang.gif'},
        {'title':'q_吐','url':'qq/tu.gif'},
        {'title':'q_偷笑','url':'qq/touxiao.gif'},
        {'title':'q_可爱','url':'qq/keai.gif'},
        {'title':'q_白眼','url':'qq/baiyan.gif'},
        {'title':'q_傲慢','url':'qq/aoman.gif'},
        {'title':'q_饥饿','url':'qq/jie.gif'},
        {'title':'q_困','url':'qq/kun.gif'},
        {'title':'q_惊恐','url':'qq/jingkong.gif'},
        {'title':'q_流汗','url':'qq/liuhan.gif'},
        {'title':'q_憨笑','url':'qq/hanxiao.gif'},
        {'title':'q_悠闲','url':'qq/youxian.gif'},
        {'title':'q_奋斗','url':'qq/fendou.gif'},
        {'title':'q_咒骂','url':'qq/zhouma.gif'},
        {'title':'q_疑问','url':'qq/yiwen.gif'},
        {'title':'q_嘘','url':'qq/xu.gif'},
        {'title':'q_晕','url':'qq/yun.gif'},
        {'title':'q_折磨','url':'qq/zhemo.gif'},
        {'title':'q_衰','url':'qq/shuai.gif'},
        {'title':'q_骷髅','url':'qq/kulou.gif'},
        {'title':'q_敲打','url':'qq/qiaoda.gif'},
        {'title':'q_再见','url':'qq/zaijian.gif'},
        {'title':'q_擦汗','url':'qq/cahan.gif'},
        {'title':'q_抠鼻','url':'qq/koubi.gif'},
        {'title':'q_鼓掌','url':'qq/guzhang.gif'},
        {'title':'q_糗大了','url':'qq/qiudale.gif'},
        {'title':'q_坏笑','url':'qq/huaixiao.gif'},
        {'title':'q_左哼哼','url':'qq/zuohengheng.gif'},
        {'title':'q_右哼哼','url':'qq/youhengheng.gif'},
        {'title':'q_哈欠','url':'qq/haqian.gif'},
        {'title':'q_鄙视','url':'qq/bishi.gif'},
        {'title':'q_委屈','url':'qq/weiqu.gif'},
        {'title':'q_快哭了','url':'qq/kuaikule.gif'},
        {'title':'q_阴险','url':'qq/yinxian.gif'},
        {'title':'q_左亲亲','url':'qq/zuoqinqin.gif'},
        {'title':'q_吓','url':'qq/xia.gif'},
        {'title':'q_可怜','url':'qq/kelian.gif'},
        {'title':'q_菜刀','url':'qq/caidao.gif'},
        {'title':'q_啤酒','url':'qq/pijiu.gif'},
        {'title':'q_咖啡','url':'qq/kafei.gif'},
        {'title':'q_饭','url':'qq/fan.gif'},
        {'title':'q_猪头','url':'qq/zhutou.gif'},
        {'title':'q_玫瑰','url':'qq/meigui.gif'},
        {'title':'q_凋谢','url':'qq/diaoxie.gif'},
        {'title':'q_示爱','url':'qq/shiai.gif'},
        {'title':'q_爱心','url':'qq/aixin.gif'},
        {'title':'q_心碎','url':'qq/xinsui.gif'},
        {'title':'q_蛋糕','url':'qq/dangao.gif'},
        {'title':'q_闪电','url':'qq/shandian.gif'},
        {'title':'q_炸弹','url':'qq/zhadan.gif'},
        {'title':'q_刀','url':'qq/dao.gif'},
        {'title':'q_足球','url':'qq/zuqiu.gif'},
        {'title':'q_瓢虫','url':'qq/piaochong.gif'},
        {'title':'q_便便','url':'qq/bianbian.gif'},
        {'title':'q_拥抱','url':'qq/yongbao.gif'},
        {'title':'q_赞','url':'qq/zan.gif'},
        {'title':'q_踩','url':'qq/cai.gif'},
        {'title':'q_握手','url':'qq/woshou.gif'},
        {'title':'q_胜利','url':'qq/shengli.gif'},
        {'title':'q_抱拳','url':'qq/baoquan.gif'},
        {'title':'q_勾引','url':'qq/gouyin.gif'},
        {'title':'q_拳头','url':'qq/quantou.gif'},
        {'title':'q_差劲','url':'qq/chajin.gif'},
        {'title':'q_爱你','url':'qq/aini.gif'},
        {'title':'q_NO','url':'qq/NO.gif'},
        {'title':'q_OK','url':'qq/OK.gif'},
        {'title':'q_跳跳','url':'qq/tiaotiao.gif'},
        {'title':'q_发抖','url':'qq/fadou.gif'},
        {'title':'q_恼火','url':'qq/naohuo.gif'},
        {'title':'q_磕头','url':'qq/ketou.gif'},
        {'title':'q_回头','url':'qq/huitou.gif'},
        {'title':'q_激动','url':'qq/jidong.gif'},
        {'title':'q_街舞','url':'qq/jiewu.gif'},
    ];
    emojiManager.emojiArr[1] = [
        {'title':'微笑','url':'weibo/weixiao.gif'},
        {'title':'嘻嘻','url':'weibo/xixi.gif'},
        {'title':'哈哈','url':'weibo/haha.gif'},
        {'title':'可爱','url':'weibo/keai.gif'},
        {'title':'可怜','url':'weibo/kelian.gif'},
        {'title':'挖鼻','url':'weibo/wabi.gif'},
        {'title':'吃惊','url':'weibo/chijing.gif'},
        {'title':'害羞','url':'weibo/haixiu.gif'},
        {'title':'挤眼','url':'weibo/jiyan.gif'},
        {'title':'闭嘴','url':'weibo/bizui.gif'},
        {'title':'鄙视','url':'weibo/bishi.gif'},
        {'title':'爱你','url':'weibo/aini.gif'},
        {'title':'泪','url':'weibo/lei.gif'},
        {'title':'偷笑','url':'weibo/touxiao.gif'},
        {'title':'亲亲','url':'weibo/qinqin.gif'},
        {'title':'生病','url':'weibo/shengbing.gif'},
        {'title':'太开心','url':'weibo/taikaixin.gif'},
        {'title':'白眼','url':'weibo/baiyan.gif'},
        {'title':'右哼哼','url':'weibo/youhengheng.gif'},
        {'title':'左哼哼','url':'weibo/zuohengheng.gif'},
        {'title':'嘘','url':'weibo/xu.gif'},
        {'title':'衰','url':'weibo/shuai.gif'},
        {'title':'吐','url':'weibo/tu.gif'},
        {'title':'哈欠','url':'weibo/haqian.gif'},
        {'title':'抱抱','url':'weibo/baobao.gif'},
        {'title':'怒','url':'weibo/nu.gif'},
        {'title':'疑问','url':'weibo/yiwen.gif'},
        {'title':'馋嘴','url':'weibo/chanzui.gif'},
        {'title':'拜拜','url':'weibo/baibai.gif'},
        {'title':'思考','url':'weibo/sikao.gif'},
        {'title':'汗','url':'weibo/han.gif'},
        {'title':'困','url':'weibo/kun.gif'},
        {'title':'睡','url':'weibo/shui.gif'},
        {'title':'钱','url':'weibo/qian.gif'},
        {'title':'失望','url':'weibo/shiwang.gif'},
        {'title':'酷','url':'weibo/ku.gif'},
        {'title':'色','url':'weibo/se.gif'},
        {'title':'哼','url':'weibo/heng.gif'},
        {'title':'鼓掌','url':'weibo/guzhang.gif'},
        {'title':'晕','url':'weibo/yun.gif'},
        {'title':'悲伤','url':'weibo/beishang.gif'},
        {'title':'抓狂','url':'weibo/zhuakuang.gif'},
        {'title':'黑线','url':'weibo/heixian.gif'},
        {'title':'阴险','url':'weibo/yinxian.gif'},
        {'title':'怒骂','url':'weibo/numa.gif'},
        {'title':'互粉','url':'weibo/hufen.gif'},
        {'title':'书呆子','url':'weibo/shudaizi.gif'},
        {'title':'愤怒','url':'weibo/fennu.gif'},
        {'title':'感冒','url':'weibo/ganmao.gif'},
        {'title':'心','url':'weibo/xin.gif'},
        {'title':'伤心','url':'weibo/shangxin.gif'},
        {'title':'猪','url':'weibo/zhu.gif'},
        {'title':'熊猫','url':'weibo/xiongmao.gif'},
        {'title':'兔子','url':'weibo/tuzi.gif'},
        {'title':'OK','url':'weibo/ok.gif'},
        {'title':'耶','url':'weibo/ye.gif'},
        {'title':'GOOD','url':'weibo/good.gif'},
        {'title':'NO','url':'weibo/no.gif'},
        {'title':'赞','url':'weibo/zan.gif'},
        {'title':'来','url':'weibo/lai.gif'},
        {'title':'弱','url':'weibo/ruo.gif'},
        {'title':'草泥马','url':'weibo/caonima.gif'},
        {'title':'神马','url':'weibo/shenma.gif'},
        {'title':'囧','url':'weibo/jiong.gif'},
        {'title':'浮云','url':'weibo/fuyun.gif'},
        {'title':'给力','url':'weibo/geili.gif'},
        {'title':'围观','url':'weibo/weiguan.gif'},
        {'title':'威武','url':'weibo/weiwu.gif'},
        {'title':'话筒','url':'weibo/huatong.gif'},
        {'title':'蜡烛','url':'weibo/lazhu.gif'},
        {'title':'蛋糕','url':'weibo/dangao.gif'},
        {'title':'发红包','url':'weibo/fahongbao.gif'}
    ];
    emojiManager.emojiArr[2] = [
        {'title': '_呵呵', 'url': 'tieba/hehe.jpg'},
        {'title': '_哈哈', 'url': 'tieba/haha.jpg'},
        {'title': '_吐舌', 'url': 'tieba/tushe.jpg'},
        {'title': '_啊', 'url': 'tieba/a.jpg'},
        {'title': '_酷', 'url': 'tieba/ku.jpg'},
        {'title': '_怒', 'url': 'tieba/nu.jpg'},
        {'title': '_开心', 'url': 'tieba/kaixin.jpg'},
        {'title': '_汗', 'url': 'tieba/han.jpg'},
        {'title': '_泪', 'url': 'tieba/lei.jpg'},
        {'title': '_黑线', 'url': 'tieba/heixian.jpg'},
        {'title': '_鄙视', 'url': 'tieba/bishi.jpg'},
        {'title': '_不高兴', 'url': 'tieba/bugaoxing.jpg'},
        {'title': '_真棒', 'url': 'tieba/zhenbang.jpg'},
        {'title': '_钱', 'url': 'tieba/qian.jpg'},
        {'title': '_疑问', 'url': 'tieba/yiwen.jpg'},
        {'title': '_阴脸', 'url': 'tieba/yinxian.jpg'},
        {'title': '_吐', 'url': 'tieba/tu.jpg'},
        {'title': '_咦', 'url': 'tieba/yi.jpg'},
        {'title': '_委屈', 'url': 'tieba/weiqu.jpg'},
        {'title': '_花心', 'url': 'tieba/huaxin.jpg'},
        {'title': '_呼~', 'url': 'tieba/hu.jpg'},
        {'title': '_笑脸', 'url': 'tieba/xiaonian.jpg'},
        {'title': '_冷', 'url': 'tieba/leng.jpg'},
        {'title': '_太开心', 'url': 'tieba/taikaixin.jpg'},
        {'title': '_滑稽', 'url': 'tieba/huaji.jpg'},
        {'title': '_勉强', 'url': 'tieba/mianqiang.jpg'},
        {'title': '_狂汗', 'url': 'tieba/kuanghan.jpg'},
        {'title': '_乖', 'url': 'tieba/guai.jpg'},
        {'title': '_睡觉', 'url': 'tieba/shuijiao.jpg'},
        {'title': '_惊哭', 'url': 'tieba/jinku.jpg'},
        {'title': '_生气', 'url': 'tieba/shengqi.jpg'},
        {'title': '_惊讶', 'url': 'tieba/jinya.jpg'},
        {'title': '_喷', 'url': 'tieba/pen.jpg'},
        {'title': '_爱心', 'url': 'tieba/aixin.jpg'},
        {'title': '_心碎', 'url': 'tieba/xinsui.jpg'},
        {'title': '_玫瑰', 'url': 'tieba/meigui.jpg'},
        {'title': '_礼物', 'url': 'tieba/liwu.jpg'},
        {'title': '_彩虹', 'url': 'tieba/caihong.jpg'},
        {'title': '_星星月亮', 'url': 'tieba/xxyl.jpg'},
        {'title': '_太阳', 'url': 'tieba/taiyang.jpg'},
        {'title': '_钱币', 'url': 'tieba/qianbi.jpg'},
        {'title': '_灯泡', 'url': 'tieba/dengpao.jpg'},
        {'title': '_茶杯', 'url': 'tieba/chabei.jpg'},
        {'title': '_蛋糕', 'url': 'tieba/dangao.jpg'},
        {'title': '_音乐', 'url': 'tieba/yinyue.jpg'},
        {'title': '_haha', 'url': 'tieba/haha2.jpg'},
        {'title': '_胜利', 'url': 'tieba/shenli.jpg'},
        {'title': '_大拇指', 'url': 'tieba/damuzhi.jpg'},
        {'title': '_弱', 'url': 'tieba/ruo.jpg'},
        {'title': '_OK', 'url': 'tieba/OK.jpg'},
    ];

    let emojiCache = [];

    $.extend({
        getEmoji: function (baseUrl) {
            for (let i = 0; i < emojiManager.emojiArr.length; i++) {
                emojiManager.emojiArr[i].forEach(function(item, index) {
                    emojiCache[item.title] = baseUrl + "/hb-comment/image/" + item.url;
                });
            }
            return emojiCache;
        },
        createEmojiPanel: function(options) {

            let $emojiPanel = $(".emoji-panel");
            if ($emojiPanel.length > 0) {
                $emojiPanel.remove();
                return;
            }

            if (emojiCache.length === 0) {
                emojiCache = $.getEmoji(options.baseUrl);
            }

            let $target = options.target;
            let $parent = $target.parent();

            if ($(".emoji-container").length == 0) {
                let $children = $parent.children();
                $parent.append($("<div class='emoji-container'></div>").append($children));
            }

            $target.addClass("emoji_btn emoji");

            let htmlArr = ['<div class="emoji-panel emoji"><div class="emoji" style="height: 2rem;background-color: #8fabbb"></div>'];
            let emojiArr = emojiManager.emojiArr;
            for (let i = 0; i < emojiArr.length; i++) {
                let emojiArrElements = emojiArr[i];
                let active = (i == 0 ? ' active' : '');
                htmlArr.push('<ul id="emoji_ul_' + i +'" class="emoji emoji-ul ' + active + '">');
                emojiArrElements.forEach(function(item, index) {
                    htmlArr.push('<li title="'+ item.title +'" class="emoji emoji-item"><img class="emoji" src="'+ emojiCache[item.title] +'"/></li>');
                });
                htmlArr.push('</ul>');
            }
            htmlArr.push('<div style="clear: both;border-top: 1px solid #f1f1f1;height: 2.5rem;" class="emoji">');
            for (let i = 0; i < emojiArr.length; i++) {
                let active = (i == 0 ? ' active' : '');
                htmlArr.push('<span id="emoji_tab_' + i + '" class="emoji emoji-tab ' + active + '">' + emojiManager.typeArr[i] + '</span>');
            }
            htmlArr.push('</div>')
            htmlArr.push('</div>');

            $(".emoji-container").append(htmlArr.join(""));

            $(".emoji-ul").on("click", function(e) {
                if (e.target.nodeName == "IMG") {
                    let title = e.target.parentNode.title;
                    let $textArea = $(options.textarea);
                    $textArea.val($textArea.val() + "[" + title + "]");
                }
            });

            let $emojiTab = $(".emoji-tab");
            $emojiTab.on("click", function() {
                if ($(this).hasClass("active")) {
                    return;
                }

                $(".emoji-ul").removeClass("active");
                $emojiTab.removeClass("active");

                let arr = $(this).attr("id").split("_");
                let num = arr[arr.length - 1];

                $("#emoji_ul_" + num).addClass("active");
                $(this).addClass("active");
            });

            $(document).on("click", function(e) {
                let $target = $(e.target);
                if ($target.hasClass("emoji")) {
                    return;
                }
                $(".emoji-panel").remove();
            });
        },
        formatContent: function (content, baseUrl) {
            if (emojiCache.length === 0) {
                emojiCache = $.getEmoji(baseUrl);
            }
            let list = content.match(/\[\w*[\u4e00-\u9fa5]*\w*\]/g);
            let filter = /[\[\]]/g;
            let title;
            if (list) {
                for(let i = 0; i < list.length; i++){
                    title = list[i].replace(filter,'');
                    if(emojiCache[title]) {
                        content = content.replace(list[i],' <img src="'+ emojiCache[title] +'"/> ');
                    }
                }
            }
            return content.replace(/&lt;br\/&gt;/g, "<br/>");
        }
    });

})(jQuery);