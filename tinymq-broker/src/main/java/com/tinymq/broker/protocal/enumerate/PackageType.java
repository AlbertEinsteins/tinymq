package com.tinymq.broker.protocal.enumerate;

public enum PackageType {
    //发送消息
    MESSAGE_REQUEST((byte)1),
    MESSAGE_RESPONSE((byte)2),
    // 心跳包
    HEART_BEAT((byte)3);

    public byte code;
    PackageType(byte code) {
        this.code = code;
    }

    public static PackageType fromInt(int code) {
        switch (code) {
            default:
            case 1: return MESSAGE_REQUEST;
            case 2: return MESSAGE_REQUEST;
            case 3: return HEART_BEAT;
        }
    }
}
