package com.light.hexo.common.util;

import com.light.hexo.business.portal.constant.PageConstant;
import fr.brouillard.oss.commonmark.ext.notifications.NotificationsExtension;
import org.apache.commons.lang3.StringUtils;
import org.commonmark.Extension;
import org.commonmark.ext.gfm.tables.TableBlock;
import org.commonmark.ext.gfm.tables.TableBody;
import org.commonmark.ext.gfm.tables.TableRow;
import org.commonmark.ext.gfm.tables.TablesExtension;
import org.commonmark.ext.heading.anchor.HeadingAnchorExtension;
import org.commonmark.node.*;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.NodeRenderer;
import org.commonmark.renderer.html.HtmlRenderer;
import org.commonmark.renderer.html.HtmlWriter;

import java.util.*;

/**
 * @Author MoonlightL
 * @ClassName: MarkdownUtil
 * @ProjectName hexo-boot
 * @Description: markdown 工具
 * @DateTime 2020/7/29 16:55
 */
public class MarkdownUtil {

    public static final List<Extension> EXTENSIONS = Arrays.asList(TablesExtension.create(), HeadingAnchorExtension.create(), NotificationsExtension.create());

    private static final Parser PARSER = Parser.builder().extensions(EXTENSIONS).build();

    private static final HtmlRenderer RENDERER = HtmlRenderer.builder().extensions(EXTENSIONS)
            .nodeRendererFactory(context -> new NodeRenderer() {

                @Override
                public Set<Class<? extends Node>> getNodeTypes() {
                    return Collections.singleton(FencedCodeBlock.class);
                }

                @Override
                public void render(Node node) {

                    HtmlWriter html = context.getWriter();
                    FencedCodeBlock codeBlock = (FencedCodeBlock) node;
                    html.line();
                    html.tag("figure", new HashMap<String, String>(1){{put("class", "highlight " + codeBlock.getInfo());}});
                    html.tag("table");
                    html.tag("tbody");
                    html.tag("tr");

                    String data = codeBlock.getLiteral();
                    String[] split = data.split("\n");
                    int linNum = 1;
                    html.tag("td", new HashMap<String, String>(1){{put("class", "gutter");}});
                    html.tag("pre");
                    for (String s : split) {
                        html.tag("span", new HashMap<String, String>(1){{put("class", "line");}});
                        html.text((linNum++) + "");
                        html.tag("/span");
                        html.tag("br");
                    }
                    html.tag("/pre");
                    html.tag("/td");

                    html.tag("td", new HashMap<String, String>(1){{put("class", "code");}});
                    html.tag("pre");
                    for (String s : split) {
                        html.tag("span", new HashMap<String, String>(1){{put("class", "line");}});
                        html.text(s);
                        html.tag("/span");
                        html.tag("br");
                    }
                    html.tag("/pre");
                    html.tag("/td");

                    html.tag("/tr");
                    html.tag("/tbody");
                    html.tag("/table");
                    html.tag("/figure");
                    html.line();
                }
            })
            .nodeRendererFactory(context -> new NodeRenderer() {

                @Override
                public Set<Class<? extends Node>> getNodeTypes() {
                    return Collections.singleton(Link.class);
                }

                @Override
                public void render(Node node) {
                    HtmlWriter html = context.getWriter();
                    Link link = (Link) node;
                    html.line();
                    Map<String, String> attrMap = new HashMap<>();
                    attrMap.put("href", link.getDestination());
                    attrMap.put("target", "_blank");
                    html.tag("a", attrMap);
                    Text firstChild = (Text) link.getFirstChild();
                    html.text(firstChild.getLiteral());
                    html.tag("/a");
                    html.line();
                }
            })
            .nodeRendererFactory(context -> new NodeRenderer() {

                @Override
                public Set<Class<? extends Node>> getNodeTypes() {
                    return Collections.singleton(Image.class);
                }

                @Override
                public void render(Node node) {
                    HtmlWriter html = context.getWriter();
                    Image image = (Image) node;
                    html.line();
                    String destination = image.getDestination();
                    Map<String, String> attrMap = new HashMap<>();
                    attrMap.put("class", "fancybox");
                    attrMap.put("href", destination);
                    attrMap.put("data-fancybox", "gallery");
                    html.tag("a", attrMap);
                    html.tag("img", new HashMap<String, String>(1){{put("data-original", destination);put("class", "lazyload");}});
                    html.tag("/a");
                    html.line();
                }
            })
            .attributeProviderFactory(context -> (node, tagName, attributes) -> {
                if (node instanceof Heading) {
                    Text firstChild = (Text) node.getFirstChild();
                    String id = firstChild.getLiteral().replace("(","")
                                                       .replace(")", "")
                                                       .replace(" ", "");
                    attributes.put("id", id);
                } else if (node instanceof TableBlock) {
                    attributes.put("class", "table");
                }
            })
            .build();

    /**
     * markdown 转 html
     * @param id        文章 id
     * @param markdown  文章内容
     * @param type      类型 1：列表 2：详情
     * @return
     */
    public static String md2html(Long id, String markdown, Integer type) {
        if (StringUtils.isBlank(markdown)) {
            return "";
        }

        String key = PageConstant.MARKDOWN_KEY + ":" + id + ":" + type;
        String result = CacheUtil.get(key);
        if (StringUtils.isBlank(result)) {
            Node document = PARSER.parse(markdown);
            result = RENDERER.render(document);
            CacheUtil.put(key, result, 3 * 60 * 1000);
        }

        return result;
    }

    public static String md2html(String markdown) {
        Node document = PARSER.parse(markdown);
        return RENDERER.render(document);
    }

    private MarkdownUtil() {}
}
