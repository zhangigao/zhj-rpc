package org.zhj.loadbalance.impl;

import cn.hutool.core.util.RandomUtil;
import org.zhj.loadbalance.LoadBalance;

import java.util.List;

public class RandomLoadBalance implements LoadBalance {
    @Override
    public <T> T select(List<T> list) {
        return RandomUtil.randomEle(list);
    }
}
