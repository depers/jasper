package cn.bravedawn.scheduled.markdown;

import org.commonmark.node.Image;
import org.commonmark.node.Node;
import org.commonmark.renderer.NodeRenderer;
import org.commonmark.renderer.html.HtmlNodeRendererContext;
import org.commonmark.renderer.html.HtmlWriter;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author : depers
 * @program : jasper
 * @date : Created in 2024/4/15 21:01
 *
 * 对图片标签进行重新渲染
 */
public class ImageNodeRenderer implements NodeRenderer {

    private final HtmlWriter html;

    public ImageNodeRenderer(HtmlNodeRendererContext context) {
        this.html = context.getWriter();
    }

    @Override
    public Set<Class<? extends Node>> getNodeTypes() {
        return Collections.<Class<? extends Node>>singleton(Image.class);
    }


    @Override
    public void render(Node node) {
        Image imageNode = (Image) node;
        Map<String, String> divAttrsMap = new HashMap<>();
        divAttrsMap.put("align", "center");
        Map<String, String> imgAttriMap = new HashMap<>();
        imgAttriMap.put("alt", imageNode.getTitle());
        imgAttriMap.put("src", imageNode.getDestination());
        html.tag("div", divAttrsMap);
        html.tag("img", imgAttriMap);
        html.tag("/div");
    }
}
