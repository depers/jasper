package cn.bravedawn.scheduled.markdown;

import cn.bravedawn.scheduled.dto.KeyNodeInfo;
import cn.bravedawn.web.config.GithubConfig;
import cn.bravedawn.web.util.FileUtils;
import cn.bravedawn.web.util.SpringContextUtil;
import org.commonmark.node.AbstractVisitor;
import org.commonmark.node.BlockQuote;
import org.commonmark.node.Image;
import org.commonmark.node.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @author : depers
 * @program : jasper
 * @description:
 * @date : Created in 2023/5/24 21:30
 */


public class KeyNodeVisitor extends AbstractVisitor {

    private static final Logger log = LoggerFactory.getLogger(KeyNodeVisitor.class);

    // 关键字节点
    private KeyNodeInfo keyNodeInfo;

    // 配置信息
    private static final String imageStorePath;
    private static final String repoDownloadUrl;

    static {
        GithubConfig githubConfig = (GithubConfig) SpringContextUtil.getBean("githubConfig");
        imageStorePath = githubConfig.getImageStorePath();
        repoDownloadUrl = githubConfig.getRepoDownloadUrl();
    }

    /**
     * 处理图片的地址
     * @param image 图片
     */
    @Override
    public void visit(Image image) {
        log.info("下载图片, url={}, title={}.", image.getDestination(), image.getTitle());
        String title = ((Text)image.getFirstChild()).getLiteral();
        String fileSuffix = FileUtils.getFileSuffix(image.getDestination());
        String filePath = imageStorePath + title + "_" + System.nanoTime() + fileSuffix;
        String url = repoDownloadUrl + image.getDestination();
        try {
            FileUtils.downloadWithJavaNIO(url, filePath);
        } catch (IOException e) {
            log.error("下载图片失败", e);
        }

    }

    /**
     * 处理关键字和文章简介这两部分内容
     * @param blockQuote 块引用
     */
    @Override
    public void visit(BlockQuote blockQuote) {
        Text keywordNode = (Text)blockQuote.getFirstChild().getFirstChild();
        Text introNode = (Text) blockQuote.getNext().getFirstChild().getFirstChild();
        if (keywordNode != null && introNode != null) {
            keyNodeInfo =  new KeyNodeInfo(keywordNode.getLiteral(), introNode.getLiteral());
        }
        log.info("解析块引用文件, keyNodeInfo={}", keyNodeInfo);
    }

    public KeyNodeInfo getKeyNodeInfo() {
        return keyNodeInfo;
    }
}
