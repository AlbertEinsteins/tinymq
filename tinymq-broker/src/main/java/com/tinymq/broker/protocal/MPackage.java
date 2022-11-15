package com.tinymq.broker.protocal;

import com.tinymq.broker.protocal.enumerate.PackageType;
import com.tinymq.broker.protocal.enumerate.SerializeType;

import java.io.Serializable;

public class MPackage implements Serializable {
    private int magicNumber;
    private byte version;
    private PackageType packageType;
    private SerializeType serializeType;
    private byte[] body;

    public MPackage(int magicNumber, byte version, PackageType packageType, SerializeType serializeType, byte[] body) {
        this.magicNumber = magicNumber;
        this.version = version;
        this.packageType = packageType;
        this.serializeType = serializeType;
        this.body = body;
    }
    public static MPackage build(int magicNumber, byte version, PackageType packageType, SerializeType serializeType, byte[] body) {
        return new MPackage(magicNumber, version, packageType, serializeType, body);
    }


    public int getMagicNumber() {
        return magicNumber;
    }

    public void setMagicNumber(int magicNumber) {
        this.magicNumber = magicNumber;
    }

    public byte getVersion() {
        return version;
    }

    public void setVersion(byte version) {
        this.version = version;
    }

    public PackageType getPackageType() {
        return packageType;
    }

    public void setPackageType(PackageType packageType) {
        this.packageType = packageType;
    }

    public SerializeType getSerializeType() {
        return serializeType;
    }

    public void setSerializeType(SerializeType serializeType) {
        this.serializeType = serializeType;
    }

    public byte[] getBody() {
        return body;
    }

    public void setBody(byte[] body) {
        this.body = body;
    }
}
