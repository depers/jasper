package cn.bravedawn.scheduled.markdown;

import cn.bravedawn.scheduled.properties.GithubConfig;
import cn.bravedawn.web.util.SpringContextUtil;
import org.apache.commons.lang.StringUtils;
import org.commonmark.Extension;
import org.commonmark.ext.gfm.tables.TableBlock;
import org.commonmark.ext.gfm.tables.TablesExtension;
import org.commonmark.node.*;
import org.commonmark.renderer.text.TextContentRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author : depers
 * @program : jasper
 * @description:
 * @date : Created in 2023/5/24 21:30
 */

public class KeyNodeVisitor extends AbstractVisitor {

    private static final Logger log = LoggerFactory.getLogger(KeyNodeVisitor.class);

    // 关键字节点
    @Deprecated
    private List<String> keyNodeInfo = new ArrayList<>();

    // 配置信息
    private static final GithubConfig githubConfig;

    // 文章路径生成的sha值
    private String sign;
    // 存储库类型
    private String repoType;
    // 文章标题
    private String artTile;
    // 文章标签
    private String artTags;
    // 文章背景描述
    private String artBackground;
    // 是否前端展示
    private String artIsShow;
    // 作者
    private String artAuthor;

    static {
        githubConfig = (GithubConfig) SpringContextUtil.getBean("githubConfig");
    }

    public KeyNodeVisitor(String sha, String repoType) {
        this.sign = sha;
        this.repoType = repoType;
    }

    /**
     * 处理图片的地址
     * @param image 图片
     */
    @Override
    public void visit(Image image) {
        String destination = image.getDestination();
        log.info("下载图片, url={}", destination);
        if (StringUtils.isNotBlank(destination) && !destination.contains(githubConfig.getAssertUrl())) {
            // 更新文章中的图片地址
            int index = destination.indexOf("assert") + 7;
            // 包含文件后缀的图片名称
            String fileSuffix = destination.substring(index);
            image.setDestination(githubConfig.getAssertUrl() + fileSuffix);
            image.setTitle(fileSuffix.substring(0, fileSuffix.lastIndexOf(".")));
            image.getParent().prependChild(image);
        }
    }

    /**
     * 处理关键字和文章简介这两部分内容
     * @param blockQuote 块引用
     * @see KeyNodeVisitor#visit(CustomBlock)
     */
    @Override
    @Deprecated
    public void visit(BlockQuote blockQuote) {
        // if (keyNodeInfo.size() == 2) {
        //     return;
        // }
        // Text keywordNode = (Text)blockQuote.getFirstChild().getFirstChild();
        // if (keywordNode != null) {
        //     keyNodeInfo.add(keywordNode.getLiteral());
        // }
        // // 从文档中删除
        // blockQuote.unlink();
        // log.info("解析块引用文件, keyNodeInfo={}", keyNodeInfo);
    }


    @Override
    public void visit(CustomBlock customBlock) {
        if (customBlock instanceof TableBlock) {
            TableBlock tableBlock = (TableBlock) customBlock;

            // 配置表格扩展
            List<Extension> extensions = Arrays.asList(TablesExtension.create());
            TextContentRenderer textContentRenderer = TextContentRenderer.builder().extensions(extensions).build();
            String render = textContentRenderer.render(tableBlock);
            List<String> tableList = Arrays.asList(render.split("\n"));
            // 处理表头信息
            List<String> tableCell = Arrays.asList(tableList.get(1).split("\\|"));
            if (tableCell.size() != 5 || tableCell.contains("")) {
                log.error("文章元数据信息不完整, tableCell={}", tableCell);
                throw new IllegalArgumentException("文章元数据信息不全");
            }

            artTile = tableCell.get(0).trim();
            artTags = tableCell.get(1).trim();
            artBackground = tableCell.get(2).trim();
            artAuthor = tableCell.get(3).trim();
            artIsShow = tableCell.get(4).trim();
            log.info("表格信息解析结果，, artTile={}, artTags={}, artBackground={}, artIsShow={}", artTile, artTags, artBackground, artIsShow);
            // 从文档正文中删除表格内容
            tableBlock.unlink();
        }

    }

    @Deprecated
    public List<String> getKeyNodeInfo() {
        return keyNodeInfo;
    }

    public String getArtTile() {
        return artTile;
    }

    public String getArtTags() {
        return artTags;
    }

    public String getArtBackground() {
        return artBackground;
    }

    public String getArtIsShow() {
        return artIsShow;
    }

    public String getArtAuthor() {
        return artAuthor;
    }
}
