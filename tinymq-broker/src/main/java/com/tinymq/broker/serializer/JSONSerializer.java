package com.tinymq.broker.serializer;

import com.alibaba.fastjson2.JSONObject;
import com.tinymq.broker.protocal.enumerate.SerializeType;
import io.netty.util.CharsetUtil;

import java.nio.charset.Charset;

// 对
public class JSONSerializer implements Serializer {
    public SerializeType serializeType = SerializeType.JSON_SERIALIZE;
    private static final Charset DEFAULT_CHARSET = CharsetUtil.UTF_8;

    @Override
    public <T> byte[] serialize(T obj) {
        String jsonStr = JSONObject.toJSONString(obj);
        return jsonStr.getBytes(DEFAULT_CHARSET);
    }


    @Override
    public <T> T deserialize(byte[] objData, Class<T> cls) {
        String jsonStr = new String(objData, DEFAULT_CHARSET);
        return JSONObject.parseObject(jsonStr, cls);
    }
}
