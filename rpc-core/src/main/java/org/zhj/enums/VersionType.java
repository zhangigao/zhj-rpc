package org.zhj.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public enum VersionType {

    VERSION1((byte) 1, "v1.0.0");


    private final byte code;
    private final String desc;
}
