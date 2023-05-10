package com.yeshen.appcenter.repository.mysql;

import com.yeshen.appcenter.domain.entity.Label;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Date 2022/1/18/0018
 * author by HuBingKuan
 */
public interface LabelDAO {
    /**
     * 获取根标签 ID
     *
     * @return
     */
    Long getRootLabelId();

    /**
     * 按父 ID 获取不同的标签
     *
     * @param parentId
     * @return
     */
    List<Label> getDistinctLabelsByParentId(@Param("parentId") Long parentId);
}