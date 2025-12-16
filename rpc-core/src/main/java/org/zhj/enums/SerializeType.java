package org.zhj.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.Arrays;

@Getter
@ToString
@AllArgsConstructor
public enum SerializeType {

    KRYO((byte) 0, "kryo"),
    JAVA((byte) 1, "java");

    private final byte code;
    private final String desc;


    public static SerializeType from(byte code) {
        return Arrays.stream(values())
                .filter(x -> x.code == code)
                .findFirst()
                .orElse(null);
    }

}
