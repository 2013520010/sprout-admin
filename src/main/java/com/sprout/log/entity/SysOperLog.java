package com.sprout.log.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 操作日志表 sys_oper_log
 */
@Data
@TableName("sys_oper_log")
public class SysOperLog implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 日志 ID */
    @TableId
    private Long operId;

    /** 操作模块 */
    private String title;

    /** 业务类型（0其它 1新增 2修改 3删除 4查询 5导出 6导入） */
    private Integer businessType;

    /** 请求方法 */
    private String method;

    /** 请求方式（GET/POST...） */
    private String requestMethod;

    /** 操作人员 */
    private String operName;

    /** 请求 URL */
    private String operUrl;

    /** 操作 IP */
    private String operIp;

    /** 请求参数 */
    private String operParam;

    /** 返回结果 */
    private String jsonResult;

    /** 操作状态（0成功 1失败） */
    private Integer status;

    /** 错误消息 */
    private String errorMsg;

    /** 操作时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime operTime;

    /** 消耗时间（毫秒） */
    private Long costTime;
}
