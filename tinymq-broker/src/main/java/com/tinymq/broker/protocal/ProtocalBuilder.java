package com.tinymq.broker.protocal;

import com.tinymq.broker.protocal.enumerate.PackageType;
import com.tinymq.broker.protocal.enumerate.SerializeType;

import java.nio.ByteBuffer;

/**
 * |------------------------------| (magic number 4B)
 * |------------------------------| (|version|direction|serial|reserve|) 4B
 * |------------------------------| (dataLen) 4B
 * |------------------------------| (data)
 */
public class ProtocalBuilder {
    private static final byte DEFAULT_VERSION = 1;

    private ByteBuffer buffer;

    private static final int DEFAULT_CAPACITY = 1 << 10;

    private final int dataOffset = 4 + 1 + 1 + 1 + 1 + 4;

    public ProtocalBuilder() {
        this.buffer = ByteBuffer.allocate(DEFAULT_CAPACITY);
    }
    public ProtocalBuilder(int bufferCapacity) {
        this.buffer = ByteBuffer.allocate(bufferCapacity + dataOffset);
    }

    private void write(int magicNumber, byte packageType, byte serialType, byte[] data) {
        if(buffer.capacity() < dataOffset + data.length) {
            throw new RuntimeException("the protocal build buffer size is lower than the data, check the buffer size");
        }

        buffer.clear();
        buffer.putInt(magicNumber);
        buffer.put(DEFAULT_VERSION);
        buffer.put(packageType);
        buffer.put(serialType);

        buffer.put((byte) 0);     // 第8个字节保留

        buffer.putInt(data.length);
        for (byte dt : data) {
            buffer.put(dt);
        }
    }

    /**
     * 对外提供两个方法，将MPackage编码为byte[]数组, 和将byte[]解码为Mpackage
     */
    public byte[] encodePackage(MPackage mPackage) {
        write(MPackage.MAGIC_NUMBER, mPackage.getPackageType().code, mPackage.getSerializeType().code,
                mPackage.getBody());
        buffer.flip();
        byte[] bytes = new byte[mPackage.getBody().length + dataOffset];
        buffer.get(bytes, 0, bytes.length);
        return bytes;
    }

    public MPackage decodePackage(byte[] mPackage) {
        int magicNumber;
        byte version;
        byte packageType;
        byte serialType;
        byte[] body;

        ByteBuffer wrap = ByteBuffer.wrap(mPackage);
        magicNumber = wrap.get();
        if(magicNumber != MPackage.MAGIC_NUMBER) {
            throw new RuntimeException("magic number rejected, the package is the wrong package.");
        }
        version = wrap.get();
        packageType = wrap.get();
        serialType = wrap.get();
        wrap.get();
        int dataLen = wrap.getInt();
        body = new byte[dataLen];
        wrap.get(body, dataOffset - 1, dataLen);
        return MPackage.build(PackageType.fromInt(packageType),
                SerializeType.fromByteCode(serialType), body);
    }
}
