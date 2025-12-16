package org.zhj.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RpcRespStatus {

    SUCCESS(200, "成功"),
    FAIL(500, "失败");

    private final Integer code;
    private final String msg;


    public static boolean isSuccess(Integer code) {
        return SUCCESS.getCode().equals(code);
    }

    public static boolean isFail(Integer code) {
        return !isSuccess(code);
    }
}


