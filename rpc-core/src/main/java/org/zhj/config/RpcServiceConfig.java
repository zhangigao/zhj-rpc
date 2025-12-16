package org.zhj.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.List;

/**
 * @Author 86155
 * @Date 2025/8/27
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RpcServiceConfig {

    private String version = "";
    private String group = "";
    private Object service;

    public RpcServiceConfig(Object service) {
        this.service = service;
    }

    public List<String> rpcServiceNames() {
        return interfaceNames().stream()
                .map(interfaceName -> interfaceName + this.group + this.version)
                .toList();
    }

    private List<String> interfaceNames() {
        return Arrays.stream(this.service.getClass().getInterfaces())
                .map(Class::getCanonicalName)
                .toList();
    }

}
