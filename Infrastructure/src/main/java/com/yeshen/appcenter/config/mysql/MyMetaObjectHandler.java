package com.yeshen.appcenter.config.mysql;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * @author by HuBingKuan
 * @Date 2022/2/11/0011
 * update(T t,Wrapper updateWrapper)时t不能为空,否则自动填充失效
 * 实体类字段使用注解@TableField(fill = FieldFill.INSERT_UPDATE)
 */
public class MyMetaObjectHandler implements MetaObjectHandler {
    @Override
    public void insertFill(MetaObject metaObject) {
        if (metaObject.hasGetter("createTime") && metaObject.hasSetter("createTime")) {
            if (metaObject.getGetterType("createTime").equals(LocalDateTime.class)) {
                this.strictInsertFill(metaObject, "createTime", () -> LocalDateTime.now(), LocalDateTime.class);
            }
            if (metaObject.getGetterType("createTime").equals(Long.class)) {
                this.strictInsertFill(metaObject, "createTime", () -> Instant.now().toEpochMilli(), Long.class);
            }
            if (metaObject.getGetterType("createTime").equals(Date.class)) {
                this.strictInsertFill(metaObject, "createTime", () -> new Date(), Date.class);
            }

        }
        if (metaObject.hasGetter("updateTime") && metaObject.hasSetter("updateTime")) {
            if (metaObject.getGetterType("updateTime").equals(LocalDateTime.class)) {
                this.strictInsertFill(metaObject, "updateTime", () -> LocalDateTime.now(), LocalDateTime.class);
            }
            if (metaObject.getGetterType("updateTime").equals(Long.class)) {
                this.strictInsertFill(metaObject, "updateTime", () -> Instant.now().toEpochMilli(), Long.class);
            }
            if (metaObject.getGetterType("updateTime").equals(Date.class)) {
                this.strictInsertFill(metaObject, "updateTime", () -> new Date(), Date.class);
            }
        }
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        if (metaObject.hasGetter("updateTime") && metaObject.hasSetter("updateTime")) {
            if (metaObject.getGetterType("updateTime").equals(LocalDateTime.class)) {
                this.strictInsertFill(metaObject, "updateTime", () -> LocalDateTime.now(), LocalDateTime.class);
            }
            if (metaObject.getGetterType("updateTime").equals(Long.class)) {
                this.strictInsertFill(metaObject, "updateTime", () -> Instant.now().toEpochMilli(), Long.class);
            }
            if (metaObject.getGetterType("updateTime").equals(Date.class)) {
                this.strictInsertFill(metaObject, "updateTime", () -> new Date(), Date.class);
            }
        }
    }
}