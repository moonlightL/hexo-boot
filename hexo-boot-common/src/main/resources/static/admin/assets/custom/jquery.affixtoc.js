;(function($) {
    "use strict";

    // AFFIXTOC CLASS DEFINITION
    // ==========================

    function AffixToc(el, options) {
        this.options = $.extend({}, AffixToc.DEFAULTS, options);
        this.$el = $(el);
        this.$affix = $(options.affixEle);
        this.$scope = $(options.scopeEle);
        this.affixEleWidth = this.$affix.width();
        this.affixEleLeft = this.$affix[0].offsetLeft;

        this.init();
    }

    AffixToc.VERSION = "1.0.1";

    AffixToc.DEFAULTS = {
        affixEle: "",  // 需固定的容器
        scopeEle: "",  // 搜索范围容器
        offset: 600,  // 滚动到当前值就固定
        affixTop: 70, // 容器固定后的 top 值
        activeColor: "#000", // 激活字体颜色
        headArr: ['h2', 'h3', 'h4', 'h5', 'h6']
    };

    AffixToc.prototype.init = function() {
        this.addStyle();
        this.findTitle();
        this.affixAndCheck();
    };

    AffixToc.prototype.addStyle = function() {
        let self = this;
        let affixPostTocClass = self.$el.selector;
        let affixEleClass = self.options.affixEle;
        let activeColor = self.options.activeColor;
        let defaultStyle = affixEleClass + ' {position: static; margin-top: 1rem; padding-bottom: 1rem;} ' + affixEleClass + '.affix {position: fixed;} ' + affixPostTocClass + ' {height: 480px; overflow: auto; } ' + affixPostTocClass + '::-webkit-scrollbar {width: 8px; height: 0; background: #ccc; border-radius: 10px } ' + affixPostTocClass + '::-webkit-scrollbar-thumb {display: block; width: 8px; margin: 0 auto; border-radius: 10px; background: #aaa } ' + affixEleClass + ' .affix-nav {padding-left: 0; list-style: none;} ' + affixEleClass + ' .affix-nav-item {position: relative; width: 100%; } ' + affixEleClass + ' .affix-nav-item .affix-nav-child {padding-left: 1rem; display: none} ' + affixEleClass + ' .affix-nav-item .affix-nav-child.active {display: block;}' + affixEleClass + ' .affix-nav-item li {list-style-type: none; } ' + affixEleClass + ' .affix-nav-item .affix-nav-link {display: block; color: #676a79; padding: 0.25rem 0.75rem;}' + affixEleClass + ' .affix-nav-item .affix-nav-link.active {color: ' + activeColor + '; font-weight: bold;}' + affixEleClass + ' .affix-nav-item .affix-nav-link.active ~ .affix-nav-child {display: block;}';
        let $head = $("head");
        let style = $("<style></style>").text(defaultStyle);
        $head.append(style);
    };

    AffixToc.prototype.findTitle = function() {
        let self = this;
        let headingsMaxDepth = 6;
        let arr = self.options.headArr;
        let headingsSelector = arr.slice(0, headingsMaxDepth).join(',');
        let headings = self.$scope.find(headingsSelector);
        self.headings = headings;
        if (!headings.length) return '';
        let listNumber = false;
        let html = "<ul class='affix-nav'>";
        let lastNumber = [0, 0, 0, 0, 0, 0];
        let firstLevel = 0;
        let lastLevel = 0;
        let i = 0;
        headings.each(function(index, domEle) {
            let level = arr.indexOf(this.localName) + 1;
            let text = $(this).text();
            let id = $(this).attr("id");
            if (id) {
                id = id.replace(/\s+/g,"-");
            } else {
                id = text.replace(/\s+/g,"-")
            }
            id = "t-" + id.replace(/\./g,"-");
            $(domEle).attr("id", id);
            lastNumber[level - 1]++;
            for (i = level; i <= 5; i++) {
                lastNumber[i] = 0
            }
            if (firstLevel) {
                for (i = level; i < lastLevel; i++) {
                    html += '</li></ul>'
                }
                if (level > lastLevel) {
                    html += '<ul class="affix-nav-child">'
                } else {
                    html += '</li>'
                }
            } else {
                firstLevel = level
            }
            html += '<li class="affix-nav-item affix-nav-level-' + level + '">';
            html += '<a class="affix-nav-link" href="#' + id + '">';
            if (listNumber) {
                html += '<span class="affix-nav-number">';
                for (i = firstLevel - 1; i < level; i++) {
                    html += lastNumber[i]
                }
                html += '</span> '
            }
            html += '<span class="affix-nav-text">' + text + '</span></a>';
            lastLevel = level
        });
        for (i = firstLevel - 1; i < lastLevel; i++) {
            html += '</li></ul>'
        }

        self.$el.html(html);
    };

    AffixToc.prototype.affixAndCheck = function() {
        let self = this;
        let allLink = self.$el.find("a");
        let allNavChild = self.$el.find("ul.affix-nav-child");
        let affix = false;
        let oldIndex = -1;

        let $affixEle = self.$affix;

        $(window).scroll(function(e) {

            let scrollTop = $(this).scrollTop();
            if (scrollTop >= self.options.offset) {
                if (!affix) {
                    affix = true;
                    self.$affix.addClass("affix");
                    $affixEle.css({top: self.options.affixTop, left: self.affixEleLeft, width: self.affixEleWidth});
                }
            } else {
                if (affix) {
                    affix = false;
                    self.$affix.removeClass("affix");
                    $affixEle.css({top: 0, left: 0});
                }
            }

            let newIndex = 0;
            $.grep(self.headings,function(domEle,index) {
                if (scrollTop >= $(domEle).offset().top - 130) {
                    newIndex = index;
                    return true;
                }
            });

            if (oldIndex == newIndex) {
                return;
            }

            oldIndex = newIndex;
            allLink.removeClass("active");
            allNavChild.removeClass("active");
            allLink.eq(newIndex).addClass("active").parents("ul.affix-nav-child").addClass("active");
        });

    };

    // AFFIXTOC PLUGIN DEFINITION
    // ===========================

    function Plugin(option) {
        return this.each(function () {
            let $this   = $(this);
            let data    = $this.data('hb.affixtoc');
            let options = typeof option == 'object' && option;

            if (!data) $this.data('hb.affixtoc', (data = new AffixToc(this, options)));
            if (typeof option == 'string') data[option]();
        });
    }

    let old = $.fn.affixToc;

    $.fn.affixToc             = Plugin;
    $.fn.affixToc.Constructor = AffixToc;

    // AFFIXTOC NO CONFLICT
    // =====================

    $.fn.affixToc.noConflict = function () {
        $.fn.affixToc = old;
        return this;
    };

})(jQuery);