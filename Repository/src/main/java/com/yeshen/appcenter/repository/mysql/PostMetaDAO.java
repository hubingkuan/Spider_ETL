package com.yeshen.appcenter.repository.mysql;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yeshen.appcenter.domain.entity.PostMeta;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * Date 2022/1/19 19:28
 * author by YangGuo
 */
public interface PostMetaDAO extends BaseMapper<PostMeta> {
    /**
     * 根据提供的游戏Id列表和指定Key，获取PostMeta列表
     *
     * @param postIds
     * @param metaKey
     * @return
     */
    @SuppressWarnings("MybatisMapperMethodInspection")
    @MapKey("postId")
    Map<Long, PostMeta> getPostMetaList(@Param("postIds") List<Long> postIds, @Param("metaKey") String metaKey);

    Long getPostIdByMetaKeyAndMetaValue(@Param("metaKey") String metaKey,@Param("metaValue")String metaValue);

    Integer updateMetaValueByPostIdAndMetaKey(@Param("postId") Long postId, @Param("metaKey") String metaKey, @Param("metaValue") String metaValue);

    Integer insertPostMeta(@Param("postId") Long postId,@Param("metaKey")String metaKey,@Param("metaValue")String metaValue);

    String getMetaValueByMetaKeyAndPostId(@Param("postId") Long postId, @Param("metaKey") String metaKey);
}