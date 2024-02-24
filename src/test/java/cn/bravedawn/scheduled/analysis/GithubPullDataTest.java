package cn.bravedawn.scheduled.analysis;

import cn.bravedawn.web.JasperApplication;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.net.URISyntaxException;

/**
 * @author : depers
 * @program : jasper
 * @date : Created in 2024/2/22 17:05
 */

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = JasperApplication.class)
public class GithubPullDataTest {

    @Autowired
    private GiteePullData giteePullData;


    @Test
    @DisplayName("Junit测试")
    public void testPullData() throws URISyntaxException, IOException {
        // String result = giteePullData.pullData("master");
        // System.out.println(result);

        giteePullData.loadData();
    }




}
