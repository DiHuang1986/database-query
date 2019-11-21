package com.ce.query.converter;

import com.ce.query.exception.ConvertException;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

public class ConverterManager {

    public static final ConverterManager INSTANCE = new ConverterManager();

    private ConverterManager() {
        this.register(Object.class, Boolean.class, new ObjectConverters.ObjectToBoolean());
        this.register(Object.class, Integer.class, new ObjectConverters.ObjectToInteger());
        this.register(Object.class, Long.class, new ObjectConverters.ObjectToLong());
        this.register(Object.class, Double.class, new ObjectConverters.ObjectToDouble());
        this.register(Object.class, String.class, new ObjectConverters.ObjectToString());
        this.register(Object.class, Timestamp.class, new ObjectConverters.ObjectToTimestamp());
    }

    private Map<Class, Map<Class, IConverter>> map = new HashMap<>();

    @SuppressWarnings("unchecked")
    public <S, T> IConverter<S, T> generate(Class<S> sourceType, Class<T> targetType) {

        Map<Class, IConverter> value = map.get(sourceType);
        if (value == null) {
            throw new ConvertException("given source type " + sourceType.getSimpleName() + " is not supported");
        }

        IConverter<S, T> converter = value.get(targetType);

        if (converter == null) {
            throw new ConvertException("given target type " + targetType.getSimpleName() + " is not supported");
        }

        return converter;
    }

    public <S, T> void register(Class<S> sourceType, Class<T> targetType, IConverter<S, T> converter) {
        Map<Class, IConverter> value = map.computeIfAbsent(sourceType, k -> new HashMap<>());
        value.put(targetType, converter);
    }
}
