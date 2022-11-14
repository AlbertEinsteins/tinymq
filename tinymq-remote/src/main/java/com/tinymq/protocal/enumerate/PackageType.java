package com.tinymq.protocal.enumerate;

public enum PackageType {
    // rpc包
    MESSAGE_REQUEST(1),
    // 心跳包
    HEART_BEAT(10);

    public int code;
    PackageType(int code) {
        this.code = code;
    }

    public static PackageType fromInt(int code) {
        switch (code) {
            default:
            case 1: return MESSAGE_REQUEST;
            case 10: return HEART_BEAT;
        }
    }
}
