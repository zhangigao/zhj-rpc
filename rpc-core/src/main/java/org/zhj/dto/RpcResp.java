package org.zhj.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.zhj.enums.RpcRespStatus;

import java.io.Serializable;

/**
 * @Author 86155
 * @Date 2025/8/25
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RpcResp<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    private String reqId;
    private Integer code;
    private String msg;
    private T data;

    public static <T> RpcResp<T> success(String reqId, T data) {
        return RpcResp.<T>builder()
                .reqId(reqId)
                .code(200)
                .data(data).build();
    }

    public static <T> RpcResp<T> fail(String reqId, Integer code, String msg) {
        return RpcResp.<T>builder()
                .reqId(reqId)
                .code(code)
                .msg(msg).build();
    }

    public static <T> RpcResp<T> fail(String reqId) {
        return RpcResp.<T>builder()
                .reqId(reqId)
                .code(RpcRespStatus.FAIL.getCode())
                .msg(RpcRespStatus.FAIL.getMsg()).build();
    }

}
