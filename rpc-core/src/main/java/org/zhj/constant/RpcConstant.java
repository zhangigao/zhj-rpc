package org.zhj.constant;

public class RpcConstant {
    public static final String ZK_HOST = "127.0.0.1";
    public static final int ZK_PORT = 2181;
    public static final String ZK_REGISTRY_PATH = "/rpc";
    public static final int SERVER_PORT = 8888;
    public static final String RESPONSE_KET = "RpcResp";
    public static final byte[] RPC_MAGIC_CODE = new byte[]{'z', 'r', 'p', 'c'};
    public static final int REQ_HEAD_LEN = 16;
    public static final int REQ_MAX_LEN = 1024 * 1024;

}
