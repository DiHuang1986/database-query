package com.ce.query.converter;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

public class ConverterFactory {

    public static final ConverterFactory INSTANCE = new ConverterFactory();

    private ConverterFactory() {
        this.register(Object.class, Boolean.class, ObjectConverters.ObjectToBoolean.class);
        this.register(Object.class, Integer.class, ObjectConverters.ObjectToInteger.class);
        this.register(Object.class, Long.class, ObjectConverters.ObjectToLong.class);
        this.register(Object.class, Double.class, ObjectConverters.ObjectToDouble.class);
        this.register(Object.class, String.class, ObjectConverters.ObjectToString.class);
        this.register(Object.class, Timestamp.class, ObjectConverters.ObjectToTimestamp.class);
    }

    private Map<Class, Map<Class, Class<? extends IConverter>>> map = new HashMap<>();

    public <S, T> IConverter<S, T> generate(Class<S> sourceType, Class<T> targetType) {

        Map<Class, Class<? extends IConverter>> value = map.get(sourceType);
        if (value == null) {
            throw new RuntimeException("given source type " + sourceType.getSimpleName() + " is not supported");
        }

        @SuppressWarnings("unchecked")
        Class<? extends IConverter<S, T>> converterClazz = (Class<? extends IConverter<S, T>>) value.get(targetType);

        if (converterClazz == null) {
            throw new RuntimeException("given target type " + targetType.getSimpleName() + " is not supported");
        }

        try {
            return converterClazz.newInstance();
        } catch (Throwable t) {
            throw new RuntimeException("can not instantiate converter");
        }
    }

    public <S, T> void register(Class<S> sourceType, Class<T> targetType, Class<? extends IConverter<S, T>> converterClass) {
        Map<Class, Class<? extends IConverter>> value = map.computeIfAbsent(sourceType, k -> new HashMap<>());
        value.put(targetType, converterClass);
    }
}
