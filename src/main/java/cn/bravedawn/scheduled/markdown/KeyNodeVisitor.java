package cn.bravedawn.scheduled.markdown;

import cn.bravedawn.scheduled.config.GithubConfig;
import cn.bravedawn.web.util.FileUtils;
import cn.bravedawn.web.util.SpringContextUtil;
import org.commonmark.node.AbstractVisitor;
import org.commonmark.node.BlockQuote;
import org.commonmark.node.Image;
import org.commonmark.node.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
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
            // 图片标题（不包含文件后缀）
            String title = destination.substring(destination.lastIndexOf("/") + 1, destination.lastIndexOf("."));
            int index = destination.indexOf("assert") + 7;
            // 包含文件后缀的图片名称
            String fileSuffix = destination.substring(index);
            String fileName = title + "_" + sign + FileUtils.getFileSuffix(destination);
            String filePath = githubConfig.getImageStorePath() + fileName;
            log.info("需要下载图片到本地, fileName={}.", fileName);
            try {
                String url = githubConfig.getRepoDownloadUrl() + "/assert/" + fileSuffix;
                FileUtils.downloadWithJavaNIO(url, filePath);
            } catch (IOException e) {
                log.error("下载图片失败", e);
            }
            // 更新文章中的图片地址
            image.setDestination(githubConfig.getAssertUrl() + fileName);
            image.getParent().prependChild(image);
        }
    }

    /**
     * 处理关键字和文章简介这两部分内容
     * @param blockQuote 块引用
     */
    @Override
    public void visit(BlockQuote blockQuote) {
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
