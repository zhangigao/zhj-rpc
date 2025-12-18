package org.zhj.api;

import org.zhj.annotation.Breaker;
import org.zhj.annotation.Retry;
import org.zhj.entity.User;
import org.zhj.exception.RpcException;

public interface UserService {

    //@Retry(value = RpcException.class)
    @Breaker(window = 30000, failureThreshold = 10,successRate = 0.5)
    User getUser(Long id);

}
