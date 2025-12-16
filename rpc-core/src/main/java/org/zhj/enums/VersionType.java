package org.zhj.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.Arrays;

@Getter
@ToString
@AllArgsConstructor
public enum VersionType {

    VERSION1((byte) 1, "v1.0.0");


    private final byte code;
    private final String desc;

    public static VersionType from(byte code) {
        return Arrays.stream(values())
                .filter(x -> x.code == code)
                .findFirst()
                .orElse(null);
    }
}
