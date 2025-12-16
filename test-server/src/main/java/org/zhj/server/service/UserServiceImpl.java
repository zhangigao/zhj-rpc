package org.zhj.server.service;

import org.zhj.api.UserService;
import org.zhj.entity.User;

/**
 * @Author 86155
 * @Date 2025/8/25
 */
public class UserServiceImpl implements UserService {
    @Override
    public User getUser(Long id) {
        assert id != null;
        return User.builder().id(id).name("zhj").age(18).build();
    }
}
