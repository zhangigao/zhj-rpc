package org.zhj.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Breaker {

    int failureThreshold() default 20;
    double successRate() default 0.7;
    long window() default 10000;
}
