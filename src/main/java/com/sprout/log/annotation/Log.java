package com.sprout.log.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 操作日志注解
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Log {

    /** 操作模块名称 */
    String title() default "";

    /** 业务类型（0其它 1新增 2修改 3删除 4查询 5导出 6导入） */
    int businessType() default 0;
}
