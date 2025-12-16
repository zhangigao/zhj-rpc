package org.zhj.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public enum SerializeType {

    KRYO((byte) 0, "kryo"),
    JAVA((byte) 1, "java");

    private final byte code;
    private final String desc;


}
