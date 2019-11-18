package com.ce.query.converter;

import com.ce.query.exception.ConvertException;

import java.sql.Timestamp;

public interface ObjectConverters {
    class ObjectToBoolean implements IConverter<Object, Boolean> {

        @Override
        public Boolean convert(Object obj) {
            if (obj == null) return null;
            if (obj instanceof Boolean) {
                return (Boolean) obj;
            }
            throw new ConvertException("not a boolean type: " + obj.getClass().getName());
        }
    }

    class ObjectToInteger implements IConverter<Object, Integer> {
        @Override
        public Integer convert(Object obj) {
            if (obj == null) return null;

            if (obj instanceof Integer) {
                return (Integer) obj;
            }
            if (obj instanceof Number) {
                return ((Number) obj).intValue();
            }

            throw new ConvertException("not a integer type: " + obj.getClass().getName());
        }
    }

    class ObjectToLong implements IConverter<Object, Long> {
        @Override
        public Long convert(Object obj) {
            if (obj == null) return null;

            if (obj instanceof Long) {
                return (Long) obj;
            }
            if (obj instanceof Number) {
                return ((Number) obj).longValue();
            }

            throw new ConvertException("not a long type: " + obj.getClass().getName());
        }
    }

    class ObjectToDouble implements IConverter<Object, Double> {
        @Override
        public Double convert(Object obj) {
            if (obj == null) return null;

            if (obj instanceof Double) {
                return (Double) obj;
            }
            if (obj instanceof Number) {
                return ((Number) obj).doubleValue();
            }

            throw new ConvertException("not a double type: " + obj.getClass().getName());
        }
    }

    class ObjectToString implements IConverter<Object, String> {
        @Override
        public String convert(Object obj) {
            if (obj == null) return null;
            if (obj instanceof String) {
                return (String) obj;
            }
            throw new ConvertException("not a string type: " + obj.getClass().getName());
        }
    }

    class ObjectToTimestamp implements IConverter<Object, Timestamp> {
        @Override
        public Timestamp convert(Object obj) {
            if (obj == null) return null;

            if (obj instanceof Timestamp) {
                return (Timestamp) obj;
            }
            throw new ConvertException("not a timestamp type: " + obj.getClass().getName());
        }
    }
}
