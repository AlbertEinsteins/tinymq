package com.tinymq.broker.factory;

import java.util.concurrent.ConcurrentHashMap;

public class SingletonFactory {
    private SingletonFactory() {
    }

    private static final ConcurrentHashMap<Class<?>, Object> map = new ConcurrentHashMap<Class<?>, Object>();

    public <T> T getInstance(Class<T> tClass) throws IllegalAccessException {
        Object o = map.get(tClass);
        if(o == null) {
            synchronized (SingletonFactory.class) {
                o = map.get(tClass);
                if(o == null) {
                    try {
                        o = tClass.newInstance();
                    } catch (InstantiationException e) {
                        throw new RuntimeException(e);
                    }
                    map.put(tClass, o);
                }
            }
        }
        return (T) o;
    }
}
