package com.yeshen.appcenter.repository.mysql;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yeshen.appcenter.domain.entity.Banner;
import com.yeshen.appcenter.domain.vo.response.BannerVO;
import org.apache.ibatis.cursor.Cursor;

import java.util.List;

/**
 * Date 2022/1/18/0018
 * author by HuBingKuan
 */
public interface BannerDAO extends BaseMapper<Banner> {
    /**
     * 获取banner列表
     *
     * @return 返回当前地区的Banner列表
     */
    List<BannerVO> getBannerList();

    /**
     * 流式查询测试
     * @return
     */
    Cursor<BannerVO> getBannerList2();
}