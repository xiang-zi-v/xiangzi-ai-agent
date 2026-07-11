package com.xiangzi.xiangziaiagent.agent;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class XiangziManusTest {

    @Resource
    private XiangziManus xiangziManus;

    @Test
    void run() {
        String userPrompt = """  
        查询青岛崂山今天的天气,
        并以 .pdf 格式输出""";
        String answer = xiangziManus.run(userPrompt);
        Assertions.assertNotNull(answer);
    }


}
