package org.zhj.exception;

/**
 * @author 15566
 * @version 0.0.1
 * @description: TODO
 * @date 2025/12/16 13:12
 */
public class RpcException extends RuntimeException {

    public RpcException() {
        super();
    }

    public RpcException(String message) {
        super(message);
    }

    public RpcException(String message, Throwable cause) {
        super(message, cause);
    }

    public RpcException(Throwable cause) {
        super(cause);
    }
}
