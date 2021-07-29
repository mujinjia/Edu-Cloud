package com.jlee.demo.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.jlee.data.AbstractDataEntity;

/**
 * @author lijia
 * @since 2021/7/30
 */
public class AbstractEntity extends AbstractDataEntity {
    @TableId(type = IdType.AUTO)
    private Integer id;
}
