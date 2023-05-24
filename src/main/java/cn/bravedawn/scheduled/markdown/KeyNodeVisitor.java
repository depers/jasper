package cn.bravedawn.scheduled.markdown;

import cn.bravedawn.scheduled.dto.KeyNodeInfo;
import org.commonmark.node.AbstractVisitor;
import org.commonmark.node.BlockQuote;
import org.commonmark.node.Image;

/**
 * @author : depers
 * @program : jasper
 * @description:
 * @date : Created in 2023/5/24 21:30
 */
public class KeyNodeVisitor extends AbstractVisitor {

    // 关键字节点
    private KeyNodeInfo keyNodeInfo;


    /**
     * 处理图片的地址
     * @param image 图片
     */
    @Override
    public void visit(Image image) {


    }

    /**
     * 处理关键字和文章简介这两部分内容
     * @param blockQuote 块引用
     */
    @Override
    public void visit(BlockQuote blockQuote) {


    }
}
