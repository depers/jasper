package cn.bravedawn.web.common;

import com.github.pagehelper.PageInfo;

import java.util.List;

/**
 * @author : depers
 * @program : jasper
 * @description:
 * @date : Created in 2023/4/4 22:12
 */
public class CommonPageResult {

    private int total;
    private int pageNum;
    private int pageSize;
    private int totalPage;
    private int prePage;
    private int nextPage;
    private List<?> list;

    private CommonPageResult(){}

    public List<?> getList() {
        return list;
    }

    public void setList(List<?> list) {
        this.list = list;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getPageNum() {
        return pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }

    public int getPrePage() {
        return prePage;
    }

    public void setPrePage(int prePage) {
        this.prePage = prePage;
    }

    public int getNextPage() {
        return nextPage;
    }

    public void setNextPage(int nextPage) {
        this.nextPage = nextPage;
    }

    public static CommonPageResult init(PageInfo pageInfo) {
        CommonPageResult result = new CommonPageResult();
        result.total = (int) pageInfo.getTotal();
        result.pageNum = pageInfo.getPageNum();
        result.pageSize = pageInfo.getPageSize();
        result.totalPage = pageInfo.getPages();
        result.prePage = pageInfo.getPrePage();
        result.nextPage = pageInfo.getNextPage();
        return result;
    }

}
