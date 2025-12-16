package org.zhj.registry;

import org.zhj.dto.RpcReq;

import java.net.InetSocketAddress;

public interface ServiceDiscovery {

    InetSocketAddress lookupService(RpcReq req);
}
