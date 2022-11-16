package com.tinymq.broker.serializer;

public interface Serializer {

    <T >byte[] serialize(T obj);

    <T> T deserialize(byte[] objData, Class<T> cls);
}
