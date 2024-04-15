package cn.bravedawn.scheduled.markdown;

import cn.bravedawn.scheduled.properties.GithubConfig;
import cn.bravedawn.web.util.SpringContextUtil;
import org.commonmark.node.AbstractVisitor;
import org.commonmark.node.BlockQuote;
import org.commonmark.node.Image;
import org.commonmark.node.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
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
    private List<String> keyNodeInfo = new ArrayList<>();

    // 配置信息
    private static final GithubConfig githubConfig;

    // 文章路径生成的sha值
    private String sign;
    // 存储库类型
    private String repoType;

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
        if (!image.getDestination().contains(githubConfig.getAssertUrl())) {
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
     */
    @Override
    public void visit(BlockQuote blockQuote) {
        if (keyNodeInfo.size() == 2) {
            return;
        }
        Text keywordNode = (Text)blockQuote.getFirstChild().getFirstChild();
        if (keywordNode != null) {
            keyNodeInfo.add(keywordNode.getLiteral());
        }
        // 从文档中删除
        blockQuote.unlink();
        log.info("解析块引用文件, keyNodeInfo={}", keyNodeInfo);
    }

    public List<String> getKeyNodeInfo() {
        return keyNodeInfo;
    }

}
