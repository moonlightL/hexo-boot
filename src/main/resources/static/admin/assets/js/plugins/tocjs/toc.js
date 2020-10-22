function tocHelper(className) {
    var headingsMaxDepth = 6;
    var arr = ['h2', 'h3', 'h4', 'h5', 'h6'];
    var headingsSelector = arr.slice(0, headingsMaxDepth).join(',');
    var headings = $(headingsSelector);

    if (!headings.length) return '';

    var listNumber = false;
    var result = '<ol class='+className+'>';
    var lastNumber = [0, 0, 0, 0, 0, 0];
    var firstLevel = 0;
    var lastLevel = 0;
    var i = 0;

    headings.each(function(index, domEle) {

        if (!$(domEle).hasClass("post-title")) {
            var level = arr.indexOf(this.localName) + 1;
            var id = $(this).text().replace(/\s+/g,"-");
            var text = $(this).text();

            lastNumber[level - 1]++;

            for (i = level; i <= 5; i++) {
                lastNumber[i] = 0;
            }

            if (firstLevel) {
                for (i = level; i < lastLevel; i++) {
                    result += '</li></ol>';
                }

                if (level > lastLevel) {
                    result += '<ol class="'+className+'-child">';
                } else {
                    result += '</li>';
                }
            } else {
                firstLevel = level;
            }

            result += '<li class="'+className+'-item '+className+'-level-'+level+'">';
            result += '<a class="'+className+'-link" href="#'+id+'">';

            if (listNumber) {
                result += '<span class="'+className+'-number">';

                for (i = firstLevel - 1; i < level; i++) {
                    result +=lastNumber[i];
                }

                result += '</span> ';
            }

            result += '<span class="'+className+'-text">'+text+'</span></a>';

            lastLevel = level;
        }

    });

    for (i = firstLevel - 1; i < lastLevel; i++) {
        result += '</li></ol>';
    }

    return result;
}