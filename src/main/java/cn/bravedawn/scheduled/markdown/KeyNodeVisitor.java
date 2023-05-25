package cn.bravedawn.scheduled.markdown;

import cn.bravedawn.scheduled.dto.KeyNodeInfo;
import cn.bravedawn.web.config.GithubConfig;
import cn.bravedawn.web.util.FileUtils;
import cn.bravedawn.web.util.HttpUtils;
import org.commonmark.node.AbstractVisitor;
import org.commonmark.node.BlockQuote;
import org.commonmark.node.Image;
import org.commonmark.node.Text;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * @author : depers
 * @program : jasper
 * @description:
 * @date : Created in 2023/5/24 21:30
 */

@Component
public class KeyNodeVisitor extends AbstractVisitor {

    @Autowired
    private GithubConfig githubConfig;

    // 关键字节点
    private KeyNodeInfo keyNodeInfo;

    /**
     * 处理图片的地址
     * @param image 图片
     */
    @Override
    public void visit(Image image) {
        InputStream inputStream = HttpUtils.downloadFile(githubConfig.getRepoDownloadUrl() + image.getDestination());
        String title = ((Text)image.getFirstChild()).getLiteral();
        String fileSuffix = FileUtils.getFileSuffix(image.getDestination());
        String filePath = githubConfig.getImageStorePath() + title + "_" + System.nanoTime() + fileSuffix;
        if (inputStream != null) {
            FileUtils.storeFile(inputStream, filePath);
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

    }

    public KeyNodeInfo getKeyNodeInfo() {
        return keyNodeInfo;
    }
}
