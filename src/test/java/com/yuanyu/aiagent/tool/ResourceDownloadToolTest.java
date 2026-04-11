package com.yuanyu.aiagent.tool;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ResourceDownloadToolTest {

    @Test
    void downloadResource() {
        ResourceDownloadTool resourceDownloadTool = new ResourceDownloadTool();
        String result = resourceDownloadTool.downloadResource("https://i0.hdslb.com/bfs/static/jinkela/long/images/favicon.ico", "b站小电视.ico");
        Assertions.assertNotNull(result);
    }
}