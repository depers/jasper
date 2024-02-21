package cn.bravedawn.scheduled.analysis;

import cn.bravedawn.scheduled.dto.GithubContent;

import java.util.List;

/**
 * @author : depers
 * @program : jasper
 * @date : Created in 2024/2/21 11:42
 */
public interface PullData {

    List<GithubContent> loadData();
}
