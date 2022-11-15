package com.tinymq.broker.protocal.enumerate;

/**
 * 通信过程的序列化类型
 */
public enum SerializeType {
    // JSO序列化
    JSON_SERIALIZE((byte)1),
    // Hessian序列化
    HESSIAN_SERIALIZE((byte)2),
    // JDK 提供的序列化
    JDK_SERIALIZE((byte)3),
    // Google Protobuf 序列化
    PROTOBUF_SERIALIZE((byte)4);


    public byte code;
    SerializeType(byte code) {
        this.code = code;
    }


    public static SerializeType fromByteCode(byte code) {
        switch (code) {
            default:
            case (1 << 2): return SerializeType.JDK_SERIALIZE;
            case (1): return JSON_SERIALIZE;
            case (1 << 1): return HESSIAN_SERIALIZE;
            case (1 << 3): return PROTOBUF_SERIALIZE;
        }
    }
}
