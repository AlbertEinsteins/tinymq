package com.tinymq.broker.protocal;

import com.tinymq.broker.protocal.enumerate.PackageType;

public class Request {
    // 该请求的类型
    private PackageType packageType;
    // 发送的消息内容
    private Object object;

    public Request(PackageType packageType, Object object) {
        this.packageType = packageType;
        this.object = object;
    }

    public PackageType getPackageType() {
        return packageType;
    }

    public void setPackageType(PackageType packageType) {
        this.packageType = packageType;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }
}
