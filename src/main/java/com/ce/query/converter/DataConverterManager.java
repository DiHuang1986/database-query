package com.ce.query.converter;

import com.ce.query.exception.ConvertException;
import com.ce.query.fromQueryer.GeneralDataConverters;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

public class DataConverterManager {

    public static final DataConverterManager INSTANCE = new DataConverterManager();

    private DataConverterManager() {
        this.register(Boolean.class, new GeneralDataConverters.BooleanDataConverter());
        this.register(Integer.class, new GeneralDataConverters.IntegerDataConverter());
        this.register(Long.class, new GeneralDataConverters.LongDataConverter());
        this.register(Double.class, new GeneralDataConverters.DoubleDataConverter());
        this.register(String.class, new GeneralDataConverters.StringDataConverter());
        this.register(Timestamp.class, new GeneralDataConverters.TimestampDataConverter());
    }

    private Map<Class<?>, IDataConverter<?>> map = new HashMap<>();

    @SuppressWarnings("unchecked")
    public <T> IDataConverter<T> lookup(Class<T> targetType) {

        IDataConverter<T> converter = null;
        for (Class<?> clazz : map.keySet()) {
            if (clazz.isAssignableFrom(targetType)) {
                converter = (IDataConverter<T>) map.get(clazz);
                break;
            }
        }

        if (converter == null) {
            throw new ConvertException("given data type " + targetType.getSimpleName() + " is not supported");
        }

        return converter;
    }

    public <T> void register(Class<T> dataType, IDataConverter<T> converter) {
        map.put(dataType, converter);
    }
}
