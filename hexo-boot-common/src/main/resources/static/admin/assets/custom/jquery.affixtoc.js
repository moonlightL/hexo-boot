;(function($) {
    "use strict";

    $.fn.affixToc = function(params) {
        let options = $.extend(AffixToc.DEFAULT_OPTIONS, params || {});
        return new AffixToc(this, options);
    };

    let AffixToc = function(el, options) {
        this.options = options;
        this.$el = $(el);
        this.$affix = $(options.affixEle);
        this.$scope = $(options.scopeEle);
        this.init();
    };

    AffixToc.prototype.init = function() {
        this.addStyle();
        this.findTitle();
        this.affixAndCheck();
    };

    AffixToc.prototype.addStyle = function() {
        let self = this;
        let affixEleClass = self.options.affixEle;
        let activeColor = self.options.activeColor;
        let defaultStyle = affixEleClass + ' {position: static; margin-top: 1rem; padding-bottom: 1rem;} ' + affixEleClass + '.affix {position: fixed !important; top: 64px; margin-top: 0; width: 276px; } @media screen and (max-width: 768px) {' + affixEleClass + ' {display: none!important;}}' + affixEleClass + ' .post-toc {height: 480px; overflow: auto; } ' + affixEleClass + ' .post-toc::-webkit-scrollbar {width: 8px; height: 0; background: #ccc; border-radius: 10px } ' + affixEleClass + ' .post-toc::-webkit-scrollbar-thumb {display: block; width: 8px; margin: 0 auto; border-radius: 10px; background: #aaa } ' + affixEleClass + ' .nav {padding-left: 0; list-style: none;} ' + affixEleClass + ' .nav-item {position: relative; width: 100%; } ' + affixEleClass + ' .nav-item .nav-child {padding-left: 1rem; display: none} ' + affixEleClass + ' .nav-item .nav-child.active {display: block;}' + affixEleClass + ' .nav-item li {list-style-type: none; } ' + affixEleClass + ' .nav-item .nav-link.active {color: ' + activeColor + '}' + affixEleClass + ' .nav-item .nav-link.active ~ .nav-child {display: block;}';
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
        let html = "<ul class='nav'>";
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
                    html += '<ul class="nav-child">'
                } else {
                    html += '</li>'
                }
            } else {
                firstLevel = level
            }
            html += '<li class="nav-item nav-level-' + level + '">';
            html += '<a class="nav-link" href="#' + id + '">';
            if (listNumber) {
                html += '<span class="nav-number">';
                for (i = firstLevel - 1; i < level; i++) {
                    html += lastNumber[i]
                }
                html += '</span> '
            }
            html += '<span class="nav-text">' + text + '</span></a>';
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
        let allNavChild = self.$el.find("ul.nav-child");
        let affix = false;
        let oldIndex = -1;
        $(window).scroll(function(e) {
            let scrollTop = $(this).scrollTop();
            if (scrollTop >= self.options.offset) {
                if (!affix) {
                    affix = true;
                    self.$affix.addClass("affix");
                }
            } else {
                if (affix) {
                    affix = false;
                    self.$affix.removeClass("affix");
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
            allLink.eq(newIndex).addClass("active").parents("ul.nav-child").addClass("active");
        });
    };

    AffixToc.DEFAULT_OPTIONS = {
        affixEle: "",  // 需固定的容器
        scopeEle: "",  // 搜索范围容器
        offset: 600,  // 滚动到当前值就固定
        activeColor: "#0f6fec", // 激活字体颜色
        headArr: ['h2', 'h3', 'h4', 'h5', 'h6']
    };


})(jQuery);