package com.yeshen.appcenter.service.strategy;

import com.yeshen.appcenter.config.executor.ThreadPoolTaskFuture;
import com.yeshen.appcenter.domain.constants.SystemConstant;
import com.yeshen.appcenter.domain.entity.WPPost;
import com.yeshen.appcenter.domain.enums.SupportDatasourceEnum;
import com.yeshen.appcenter.domain.vo.response.GameBaseArticle;
import com.yeshen.appcenter.repository.mysql.GameDAO;
import com.yeshen.appcenter.repository.mysql.PostMetaDAO;
import com.yeshen.appcenter.utils.DingTalkUtil;
import com.yeshen.appcenter.utils.NanoIdUtil;
import com.yeshen.appcenter.utils.ThrowableUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * Date 2023/01/11  17:36
 * author  by HuBingKuan
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class UsHandlerContentStrategy implements HandlerPageContentStrategy, InitializingBean {
    private final GameDAO gameDAO;

    private final PostMetaDAO postMetaDAO;

    @Qualifier("mysqlTransactionTemplate")
    private final TransactionTemplate transactionTemplate;

    @Override
    public String handlePageContent(String content) {
/*        // 利用正则删除a标签
        String regx1 = "(<a.*?>)";
        Pattern p = Pattern.compile(regx1);
        Matcher m = p.matcher(content);
        while (m.find()) {
            String group = m.group();
            content = content.replace(group, "");
        }
        content = StringUtils.removeAll(content, "</a>");*/
        // 利用jsoup删除a标签中的href
        final Document document = Jsoup.parse(content);
        final Elements elements = document.select("a[href]");
        for (Element el : elements) {
            el.removeAttr("href");
        }
        // 给图片添加width属性 避免图片过大
        Elements imgs = document.getElementsByTag("img");
        for (Element img : imgs) {
            String width = img.attr("width");
            if (StringUtils.isEmpty(width)) {
                // 改变图片大小
                img.attr("width", "500px");
            }
        }
        // 改变a标签样式
        Elements aElements = document.getElementsByTag("a");
        for (Element aElement : aElements) {
            aElement.attr("style", "text-decoration:none;color:black;");
        }
        content = document.body().html();
        // 删除空行  注意需要带2个转义符号
        content = org.apache.commons.lang3.StringUtils.removeAll(content, "\\n");
        // 追加图片
        content = content + "<p>NoxPlayer is a free Android emulator for playing mobile games on PC and Mac, supporting Android 5, 7, 8, and 9, and compatible with Intel, AMD, and Apple processors. You can run NoxPlayer perfectly on commonly-used operating systems like Windows 7, 8, 10, 11, and iOS.</p>" +
                "<br><p>Visit our site to learn more about NoxPlayer : <a href=\"www.bignox.com\">www.bignox.com</a></p><br><a href=\"https://en.bignox.com/en/download/fullPackage?formal\"><img class=\"alignnone wp-image-13470 size-full\" src=\"https://res09.bignox.com/appcenter/en/gp-game-image/fc69ed2b06f33aaf3d9ebe89e051f3d4\" alt=\"\" width=\"1280\" height=\"1104\" /></a>\n<section data-v-12600b54=\"\"></section>\n" +
                "<section data-v-12600b54=\"\"><span style=\"color: #999999;\"><a style=\"color: #999999;\" href=\"https://www.gamespot.com/\"><span style=\"font-size: 8pt;\">Source:Gamespot</span></a></span></section>";
        return content;
    }

    @Override
    public void insertPageContent(GameBaseArticle article) {
        Long id = gameDAO.getIdByTitle(article.getTitle());
        String content = this.handlePageContent(article.getContent());
        if (id == null) {
            log.info("开始填充文章,地区:{},标题:{}", SupportDatasourceEnum.PRD_BLOG_EN, article.getTitle());
            LocalDateTime now = LocalDateTime.now();
            transactionTemplate.execute((status) -> {
                try {
                    String postName = SystemConstant.POST_NAME_PREFIX + now.toEpochSecond(ZoneOffset.ofHours(8)) + NanoIdUtil.randomNanoId();
                    // 插入app
                    WPPost wpPost = WPPost.buildUSBasePost();
                    wpPost.setPostContent(content);
                    wpPost.setPostTitle(article.getTitle());
                    wpPost.setPostName(postName);
                    wpPost.setGuid("https://www.bignox.com/blog/" + postName);
                    gameDAO.insertWPPost(wpPost);
                    Long insertId = wpPost.getId();
                    // 填充分类表  291是wp_terms表中的 term_id  标签是name news
                    gameDAO.insertWPTermRelationships(insertId, 291L);
                    // 填充首图  5为固定图片  wp_post中id为5
                    postMetaDAO.insertPostMeta(insertId, "_thumbnail_id", "5");
                } catch (Exception e) {
                    // 手动回滚事务
                    //TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                    status.setRollbackOnly();
                    ThreadPoolTaskFuture.runAsync(() -> {
                        String msg = String.format("Blog入库失败提醒,地区:%s,标题:%s,错误信息:%s", SupportDatasourceEnum.PRD_BLOG_EN, article.getTitle(), ThrowableUtil.getStackTraceByPackage(e, "com.yeshen.appcenter"));
                        log.warn(msg);
                        DingTalkUtil.sendMessage(msg, true);
                    });
                }
                return null;
            });
        } else {
            log.info("文章已存在,标题:{}", article.getTitle());
            gameDAO.updatePostContent(id, content);
        }
        log.info("填充文章完成,文章标题:{}", article.getTitle());
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        HandlerContentStrategyContext.registerStrategy(SupportDatasourceEnum.PRD_BLOG_EN, this);
    }
}