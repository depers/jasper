package cn.bravedawn.scheduled.analysis;

import cn.bravedawn.scheduled.constant.PullDataRepos;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author : depers
 * @program : jasper
 * @date : Created in 2024/2/23 21:24
 */

@Component
public class PullDataFactory {

    @Autowired
    private GiteePullData giteePullData;

    @Autowired
    private GithubPullData githubPullData;

    private PullDataFactory(){}

    public PullData getStrategy(String repo){
        return StringUtils.equals(repo, PullDataRepos.GITHUB_REPO) ? githubPullData : giteePullData;
    }

}
