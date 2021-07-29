package com.jlee.data.configurer;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 添加和更新时自动填充的字段
 * <p>字段上加注解是为了在适当时候（插入或者更新）预留sql字段，因为mybatis-plus 默认 当字段为 null 时不进行更新，真正的填充还需要 MetaObjectHandler 的配合</p>
 */
@Component
public class AuditMetaObjectHandler implements MetaObjectHandler {
    public AuditMetaObjectHandler() {
    }

    @Override
    public void insertFill(MetaObject metaObject) {
        this.strictInsertFill(metaObject, "isDeleted", () -> false, Boolean.class);
        this.strictInsertFill(metaObject, "createTime", LocalDateTime::now, LocalDateTime.class);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        this.strictUpdateFill(metaObject, "updateTime", LocalDateTime::now, LocalDateTime.class);
    }
}