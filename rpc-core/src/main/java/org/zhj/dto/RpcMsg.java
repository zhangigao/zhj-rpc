package org.zhj.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.zhj.enums.CompressType;
import org.zhj.enums.MsgType;
import org.zhj.enums.SerializeType;
import org.zhj.enums.VersionType;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RpcMsg implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer reqId;
    private VersionType versionType;
    private MsgType msgType;
    private SerializeType serializeType;
    private CompressType compressType;
    private Object data;

}
