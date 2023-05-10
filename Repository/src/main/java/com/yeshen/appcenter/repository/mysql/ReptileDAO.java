package com.yeshen.appcenter.repository.mysql;

import com.yeshen.appcenter.domain.vo.response.ReptileApp;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Date 2022/07/08  16:37
 * author  by HuBingKuan
 */
public interface ReptileDAO {
    List<ReptileApp> getReptileAppListByRegion(@Param("region") String region,@Param("day") Integer day);

    String getLogoUrlByPackageAndRegion(@Param("packageName")String packageName,@Param("region")String region);

    ReptileApp getReptileAppByPackageAndRegion(@Param("packageName")String packageName,@Param("region")String region);
}