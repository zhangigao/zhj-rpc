package org.zhj.dto;

import cn.hutool.core.util.StrUtil;
import io.netty.util.internal.StringUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Author 86155
 * @Date 2025/8/25
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RpcReq implements Serializable {
    private static final long serialVersionUID = 1L;

    private String reqId;
    private String interfaceName;
    private String methodName;
    private Object[] params;
    private Class<?>[] paramTypes;
    private String version = "";
    private String group = "";

    public String getServiceName() {
        return this.interfaceName +
                StrUtil.blankToDefault(this.group, StrUtil.EMPTY) +
                StrUtil.blankToDefault(this.version, StrUtil.EMPTY);
    }
}
