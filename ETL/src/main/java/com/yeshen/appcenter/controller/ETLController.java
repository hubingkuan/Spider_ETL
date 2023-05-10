package com.yeshen.appcenter.controller;

import com.yeshen.appcenter.domain.common.ResultVO;
import com.yeshen.appcenter.domain.vo.response.GameBaseArticle;
import com.yeshen.appcenter.service.ETLService;
import com.yeshen.appcenter.utils.SpringContextUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

/**
 * Date 2022/07/07  19:14
 * author  by HuBingKuan
 */
@RestController
@RequestMapping("/etl")
@RequiredArgsConstructor
public class ETLController {
    private final ETLService etlService;

    /**
     * 通过爬虫库中的app录入到模拟器官网游戏中心
     * @param isoCode
     * @param day
     * @return
     */
    @GetMapping("/fillingWordPressApp")
    public ResultVO fillingWordPressApp(@RequestParam("isoCode")String isoCode,@RequestParam(name = "day",defaultValue = "1",required = false)String day) {
        return ResultVO.createSuccess(etlService.fillingWordPressApp(isoCode,day));
    }


    /**
     * 抓取指定的包名游戏入库
     * @param isoCode
     * @param packageName
     * @return
     */
    @GetMapping("/fillingAppByPackageAndRegion")
    public ResultVO fillingAppByPackageAndRegion(@RequestParam("isoCode")String isoCode,@RequestParam(name = "packageName")String packageName) {
        etlService.fillingAppByPackageAndRegion(isoCode,packageName);
        return ResultVO.createSuccess();
    }

    /**
     * 爬取游戏基地内容到blog
     * @return
     */
    @PostMapping("/fillingGameBase")
    public ResultVO fillingWordPressApp(@RequestBody List<GameBaseArticle> gameBaseArticles){
        return ResultVO.createSuccess(etlService.fillingGameBase(gameBaseArticles));
    }

    /**
     * 调用接口去更新游戏icon
     * @param isoCode
     * @param packageName
     * @return
     */
    @GetMapping("/updateIcon")
    public ResultVO updateIcon(@RequestParam("isoCode")String isoCode,@RequestParam("package")String packageName) {
        return ResultVO.createSuccess(etlService.updateIcon(isoCode,packageName));
    }


    /**
     * 检查线程池状态
     */
    @GetMapping("/lookStatus")
    public ResultVO lookStatus() {
        HashMap<String, String> map = new HashMap<>();
        String tomcatStatus=SpringContextUtil.getTomcatExecutorStatus();
        String threadPoolStatus= SpringContextUtil.getMyThreadPoolStatus();
        map.put("TomcatThreadPool",tomcatStatus);
        map.put("ServiceThreadPool",threadPoolStatus);
        return ResultVO.createSuccess(map);
    }

    /**
     * taptap爬虫
     * @return
     * @throws InterruptedException
     */
    @GetMapping("/CrawlingTaptapApp")
    public ResultVO CrawlingTaptapApp() throws InterruptedException {
        etlService.CrawlingTaptapApp();
        return ResultVO.createSuccess();
    }
}