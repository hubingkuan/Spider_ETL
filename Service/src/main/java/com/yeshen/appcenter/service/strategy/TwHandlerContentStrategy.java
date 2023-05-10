package com.yeshen.appcenter.service.strategy;

import com.yeshen.appcenter.config.executor.ThreadPoolTaskFuture;
import com.yeshen.appcenter.domain.constants.SystemConstant;
import com.yeshen.appcenter.domain.entity.Terms;
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
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Date 2023/01/11  17:36
 * author  by HuBingKuan
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TwHandlerContentStrategy implements HandlerPageContentStrategy, InitializingBean {
    private final GameDAO gameDAO;

    private final PostMetaDAO postMetaDAO;

    @Qualifier("mysqlTransactionTemplate")
    private final TransactionTemplate transactionTemplate;

    private final static String[] strArray=new String[]{"PC版","電腦版","下載","APK","攻略","模擬器推薦","巴哈"};

    @Override
    public String handlePageContent(String content) {
        // 闭合标签iframe
        String regx1 = "(<iframe.*?/>)";
        Pattern p = Pattern.compile(regx1);
        Matcher m = p.matcher(content);
        while (m.find()) {
            String group = m.group();
            String replace = group.substring(0, group.length() - 2) + "></iframe>";
            content = content.replace(group, replace);
        }
        // 删除小图片展示
        String regx2 = "(<div class=\"swiper mySwiperB0 mySwiper-thumb swiper-initialized swiper-horizontal swiper-pointer-events swiper-free-mode swiper-thumbs\".*?<p>)";
        p = Pattern.compile(regx2);
        m = p.matcher(content);
        while (m.find()) {
            String group = m.group();
            String replace = group.substring(0, group.length() - 3);
            content = content.replace(replace, "");
        }
        // 删除a标签
        String regx3 = "(<a href.*?>)";
        p = Pattern.compile(regx3);
        m = p.matcher(content);
        while (m.find()) {
            String group = m.group();
            content = content.replace(group, "");
        }
        content = StringUtils.removeAll(content, "</a>");
        // 追加下载图片和游戏来源字样
        content = content + "\n<a href=\"https://tw.bignox.com/tw/download/fullPackage?beta\"><img class=\"alignnone wp-image-13470 size-full\" src=\"https://res09.bignox.com/appcenter/tw/gp-game-image/d008168c2c1958d415843dd4541310c0\" alt=\"\" width=\"1280\" height=\"1104\" /></a>\n<section data-v-12600b54=\"\"></section>\n" +
                "<section data-v-12600b54=\"\"><span style=\"color: #999999;\"><a style=\"color: #999999;\" href=\"https://news.gamebase.com.tw/\"><span style=\"font-size: 8pt;\">來源:遊戲基地</span></a></span></section>";
        // 闭合div标签
        if (content.contains("<div class=\"embdscl0\"/>")) {
            content = content.replace("<div class=\"embdscl0\"/>", "<div class=\"embdscl0\"></div>");
        }
        return content;
    }

    @Override
    public void insertPageContent(GameBaseArticle article) {
        Long id = gameDAO.getIdByTitle(article.getTitle());
        String content = this.handlePageContent(article.getContent());
        String gameName = getGameName(article.getTitle());
        if (id == null) {
            log.info("开始填充文章,地区:{},标题:{}", SupportDatasourceEnum.PRD_BLOG_TW, article.getTitle());
            LocalDateTime now = LocalDateTime.now();
            transactionTemplate.execute((status) -> {
                try {
                    String postName = SystemConstant.POST_NAME_PREFIX + now.toEpochSecond(ZoneOffset.ofHours(8)) + NanoIdUtil.randomNanoId();
                    // 插入app
                    WPPost wpPost = WPPost.buildTWBasePost();
                    wpPost.setPostContent(content);
                    wpPost.setPostTitle(article.getTitle());
                    wpPost.setPostName(postName);
                    wpPost.setGuid("https://tw.bignox.com/blog/" + postName);
                    gameDAO.insertWPPost(wpPost);
                    Long insertId = wpPost.getId();
                    // 填充分类表  889是wp_terms表中的 term_id  标签是name
                    gameDAO.insertWPTermRelationships(insertId, 889L);
                    // 填充首图  13824为固定图片  wp_post中的guid
                    postMetaDAO.insertPostMeta(insertId, "_thumbnail_id", "13824");
                    // 根据标题中是否带有书名号去补充标签
                    if (gameName != null) {
                        for (final String suffix : strArray) {
                            String labelName = gameName + suffix;
                            Long labelId = gameDAO.findLabelIdByName(labelName);
                            if (labelId == null) {
                                // 新建标签
                                Terms terms = Terms.builder().name(labelName).slug(URLEncoder.encode(labelName)).termGroupId(0L).build();
                                gameDAO.insertWPTerms(terms);
                                gameDAO.insertWPTermTaxonomy(terms.getTermId(), "post_tag", 0L);
                                gameDAO.insertWPTermRelationships(insertId, terms.getTermId());
                            }else{
                                Long relationId = gameDAO.findRelationIdByLabelId(labelId);
                                gameDAO.insertWPTermRelationships(insertId, relationId);
                            }
                        }
                    }
                } catch (Exception e) {
                    // 手动回滚事务
                    //TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                    status.setRollbackOnly();
                    ThreadPoolTaskFuture.runAsync(() -> {
                        String msg = String.format("Blog入库失败提醒,地区:%s,标题:%s,错误信息:%s", SupportDatasourceEnum.PRD_BLOG_TW, article.getTitle(), ThrowableUtil.getStackTraceByPackage(e, "com.yeshen.appcenter"));
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
    public void afterPropertiesSet() {
        HandlerContentStrategyContext.registerStrategy(SupportDatasourceEnum.PRD_BLOG_TW, this);
    }

    private String getGameName(String articleTitle) {
        int count = 0;
        String result = null;
        String regx = "(?<=《).*?(?=》)";
        Pattern pattern = Pattern.compile(regx);
        Matcher matcher = pattern.matcher(articleTitle);
        while (matcher.find()) {
            if(count>0){
                return null;
            }else{
                count++;
                result = matcher.group();
            }
        }
        return result;
    }
}