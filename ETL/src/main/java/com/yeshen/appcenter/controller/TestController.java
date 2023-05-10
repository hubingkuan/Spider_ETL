package com.yeshen.appcenter.controller;

import com.yeshen.appcenter.alioss.OssModule;
import com.yeshen.appcenter.domain.common.ResultVO;
import com.yeshen.appcenter.domain.vo.response.ImageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Date 2023/02/01  16:16
 * author  by HuBingKuan
 */
@RestController
@RequestMapping("/test")
public class TestController {
    @Autowired
    private OssModule ossModule;

    @PostMapping("/upload2Oss")
    public ResultVO CrawlingTaptapApp() {
        ImageInfo imageInfo = ossModule.uploadPhotoByUrl("en", "C:\\Users\\Nox123\\Desktop\\enp.jpg");
        System.out.println(imageInfo);
        return ResultVO.createSuccess(imageInfo);
    }
}