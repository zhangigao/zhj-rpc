package org.zhj.serialize.impl;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import lombok.extern.slf4j.Slf4j;
import org.zhj.dto.RpcReq;
import org.zhj.dto.RpcResp;
import org.zhj.serialize.Serializer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

@Slf4j
public class KryoSerializer implements Serializer {
    // kryo线程不安全
    private static final ThreadLocal<Kryo> KRYO_THREAD_LOCAL = ThreadLocal.withInitial(() -> {
        Kryo kryo = new Kryo();
        kryo.register(RpcReq.class);
        kryo.register(RpcResp.class);
        return kryo;
    });

    @Override
    public byte[] serialize(Object obj) {
        try (ByteArrayOutputStream oos = new ByteArrayOutputStream();
             Output output = new Output(oos)) {
            KRYO_THREAD_LOCAL.get().writeObject(output, obj);
            output.flush();
            return oos.toByteArray();
        } catch (IOException e) {
            log.error("kryo序列化失败", e);
            throw new RuntimeException(e);
        } finally {
            KRYO_THREAD_LOCAL.remove();
        }
    }
    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) {
        try (ByteArrayInputStream is = new ByteArrayInputStream(bytes);
             Input input = new Input(is)) {
            return KRYO_THREAD_LOCAL.get().readObject(input, clazz);
        } catch (IOException e) {
            log.error("kryo反序列化失败", e);
            throw new RuntimeException(e);
        } finally {
            KRYO_THREAD_LOCAL.remove();
        }
    }
}
