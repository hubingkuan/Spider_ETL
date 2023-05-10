package com.yeshen.appcenter.controller;

import com.yeshen.appcenter.domain.vo.request.GameSearchQueryVO;
import com.yeshen.appcenter.domain.vo.request.LabelGamesListReqVO;
import com.yeshen.appcenter.domain.vo.response.GameDetailRepVO;
import com.yeshen.appcenter.domain.vo.response.GameDisplayRepVO;
import com.yeshen.appcenter.domain.vo.response.LabelGameDisplayListRepVO;
import com.yeshen.appcenter.service.GameService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotBlank;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

/**
 * Date 2022/1/19/0019
 * author by HuBingKuan
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/game")
@RequiredArgsConstructor
public class GameController {
    private final GameService gameService;

    /**
     * 搜索游戏
     *
     * @param query
     * @return
     */
    @PostMapping("/search")
    public Map<String,Object> searchGame(@RequestBody @Validated GameSearchQueryVO query) {
        return gameService.searchGame(query);
    }


    /**
     * 根据标签的slug获取游戏
     */
    @GetMapping("/label/{labelName}")
    public LabelGameDisplayListRepVO getGameAndBannerByLabel(
            @NotBlank(message = "Label cannot be empty") @PathVariable("labelName") String name) throws UnsupportedEncodingException {
        if (!name.startsWith("%")) {
            name = URLEncoder.encode(name, "UTF-8");
        }
        return gameService.getGameAndBannerByLabel(name);
    }

    /**
     * 加载更多游戏
     */
    @PostMapping("/loadMore")
    public List<GameDisplayRepVO> getGameByLabel(@RequestBody @Validated LabelGamesListReqVO req) {
        return gameService.getGameByLabel(req);
    }

    /**
     * 根据游戏包名格式化获取游详情
     */
    @GetMapping("/detail/{postName}")
    public GameDetailRepVO getGameDetailByGameId(
            @NotBlank(message = "postName cannot be empty") @PathVariable("postName") String postName) throws UnsupportedEncodingException {
        if (!postName.startsWith("%")) {
            postName = URLEncoder.encode(postName, "UTF-8");
        }
        return gameService.getGameDetailByPostName(postName);
    }

    /**
     * 底部热门游戏查询,排除自身游戏
     */
    @GetMapping("/bottom/{postName}")
    public List<GameDisplayRepVO> getBottomGameNotInGameId(
            @NotBlank(message = "postName cannot be empty") @PathVariable("postName") String postName) throws UnsupportedEncodingException {
        if (!postName.startsWith("%")) {
            postName = URLEncoder.encode(postName, "UTF-8");
        }
        return gameService.getBottomGameNotInGameId(postName);
    }
}