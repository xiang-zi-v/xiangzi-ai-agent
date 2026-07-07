package com.xiangzi.xiangziaiagent.tools;

import cn.hutool.core.date.DateUtil;
import org.springframework.ai.tool.annotation.Tool;

public class TimeOutTool {


    @Tool(description = "get now of time")
    public String getNowTime() {
        return DateUtil.now();
    }


}
