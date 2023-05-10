package com.yeshen.appcenter.repository.mysql;

import com.yeshen.appcenter.domain.entity.PostMeta;
import com.yeshen.appcenter.domain.entity.Terms;
import com.yeshen.appcenter.domain.entity.WPPost;
import com.yeshen.appcenter.domain.vo.response.GameDisplayRepVO;
import com.yeshen.appcenter.domain.vo.response.TypeVO;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * Date 2022/1/18/0018
 * author by HuBingKuan
 */
public interface GameDAO{
    /**
     * 返回游戏的类型
     *
     * @param postId 游戏ID
     * @return
     */
    String getType(Long postId);

    /**
     * 获取游戏簇的所有类型
     *
     * @param posts 游戏Id列表
     * @return
     */
    @MapKey("postId")
    Map<Long, TypeVO> getTypeList(@Param("posts") List<Long> posts);

    /**
     * 获得游戏下载前十名
     *
     * @return
     */
    List<GameDisplayRepVO> getGameDownloadTopTen();

    /**
     * 获得带有评分和图片的推荐游戏
     *
     * @param termName
     * @param size
     * @return
     */
    List<GameDisplayRepVO> getRecommendGamesWithGradeAndImage(@Param("termName") String termName, @Param("size") Integer size);

    /**
     * 按标签获取带有评分和图片的游戏
     *
     * @param labelName
     * @param startIndex
     * @param length
     * @return
     */
    List<GameDisplayRepVO> getGamesWithGradeAndImageByLabel(@Param("labelName") String labelName, @Param("startIndex") Integer startIndex, @Param("length") Integer length);

    /**
     * 通过游戏 ID 获取横幅图片网址
     *
     * @param id
     * @return
     */
    String getBannerImageUrlByGameId(@Param("gameId") Long id);

    /**
     * 获取详情页底部推荐游戏
     *
     * @param id
     * @return
     */
    List<GameDisplayRepVO> getBottomGameNotInGameId(@Param("gameId") Long id);

    /**
     * 按游戏 ID 获取属性
     *
     * @param gameId
     * @return
     */
    List<PostMeta> getAttributeByGameId(@Param("gameId") Long gameId);

    /**
     * 通过游戏包名获取游戏id
     *
     * @param postName
     * @return
     */
    GameDisplayRepVO getIdByPostName(@Param("postName") String postName);

    /**
     * 通过游戏 ID 获取游戏名称
     *
     * @param gameId
     * @return
     */
    GameDisplayRepVO getGameNameByGameId(@Param("gameId") Long gameId);

    /**
     * 获取所有有效的游戏包名
     * @return
     */
    List<GameDisplayRepVO> getAllEffectivePackageNames();

    /**
     * 根据关键词搜索游戏
     * @param keywords
     * @param startIndex
     * @param length
     * @return
     */
    List<GameDisplayRepVO> getGamesWithGradeAndImageByKeywords(@Param("keywords") String keywords,@Param("startIndex") Integer startIndex, @Param("length") Integer length);

    /**
     * 根据关键词搜索游戏(全文索引 中文分词器)
     * @param keywords
     * @param length
     * @return
     */
    List<GameDisplayRepVO> getGamesWithGradeAndImageByNaturalLanguageSearch(@Param("keywords") String keywords,@Param("startIndex") Integer startIndex,@Param("length") Integer length);

    /**
     * 插入wppost表
     * @param wpPost
     * @return
     */
    Long insertWPPost(@Param("app") WPPost wpPost);

    /**
     * 根据标签名获取id
     */
    Long findLabelIdByName(@Param("labelName") String labelName);

    /**
     * 插入标签到wp_terms表
     * @param terms
     * @return
     */
    Long insertWPTerms(@Param("terms") Terms terms);

    /**
     * 获取分类和标签的关联关系
     */
    Long findRelationIdByLabelId(@Param("termId")Long termId);

    /**
     * 获取根ID
     * @return
     */
    Long findRootGameCateRelationId();

    /**
     * 插入关系表(分类id与标签id中间表)
     * @param labelId
     * @param labelType
     * @param parentId
     * @return
     */
    Long insertWPTermTaxonomy(@Param("labelId") Long labelId,@Param("labelType")String labelType,@Param("parent")Long parentId);

    /**
     * 判断关系表(游戏id与分类id中间表)中是否存在该游戏
     * @param appId
     * @param relationId
     * @return
     */
    Integer existAppIdAndRelationId(@Param("appId")Long appId,@Param("relationId")Long relationId);

    Long insertWPTermRelationships(@Param("appId") Long appId,@Param("relationId")Long relationId);

    /**
     * 查询游戏标题后缀
     */
    String getWebSiteTitle();

    /**
     * 更新游戏名
     * @param postId
     * @param postTitle
     * @return
     */
    Integer updatePostTitleByPostId(@Param("postId")Long postId,@Param("postTitle")String postTitle);

    /**
     * 更新包名
     * @param postId
     * @param postName
     * @return
     */
    Integer updatePackageNameByPostId(@Param("postId")Long postId,@Param("postName")String postName);

    /**
     * 查看标题所在的文章是否已存在
     * @param postTitle
     * @return
     */
    Long getIdByTitle(@Param("postTitle")String postTitle);

    int gameNameIsExist(@Param("gameName")String gameName);

    int updatePostContent(@Param("id") Long id,@Param("content") String content);

}