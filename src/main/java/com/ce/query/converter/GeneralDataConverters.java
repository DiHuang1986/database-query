package com.ce.query.fromQueryer;

import com.ce.query.converter.IDataConverter;
import com.ce.query.exception.ConvertException;

import java.sql.Timestamp;

public interface GeneralDataConverters {

    class BooleanDataConverter implements IDataConverter<Boolean> {
        @Override
        public Boolean fromRaw(Object obj) {
            if (obj == null) return null;
            if (obj instanceof Boolean) {
                return (Boolean) obj;
            }
            throw new ConvertException("not a boolean type: " + obj.getClass().getName());
        }

        @Override
        public Object toRaw(Boolean aBoolean) {
            return aBoolean;
        }
    }

    class IntegerDataConverter implements IDataConverter<Integer> {
        @Override
        public Integer fromRaw(Object obj) {
            if (obj == null) return null;

            if (obj instanceof Integer) {
                return (Integer) obj;
            }
            if (obj instanceof Number) {
                return ((Number) obj).intValue();
            }

            throw new ConvertException("not a integer type: " + obj.getClass().getName());
        }

        @Override
        public Object toRaw(Integer integer) {
            return integer;
        }
    }

    class LongDataConverter implements IDataConverter<Long> {
        @Override
        public Long fromRaw(Object obj) {
            if (obj == null) return null;

            if (obj instanceof Long) {
                return (Long) obj;
            }
            if (obj instanceof Number) {
                return ((Number) obj).longValue();
            }

            throw new ConvertException("not a long type: " + obj.getClass().getName());
        }

        @Override
        public Object toRaw(Long aLong) {
            return aLong;
        }
    }

    class DoubleDataConverter implements IDataConverter<Double> {
        @Override
        public Double fromRaw(Object obj) {
            if (obj == null) return null;

            if (obj instanceof Double) {
                return (Double) obj;
            }
            if (obj instanceof Number) {
                return ((Number) obj).doubleValue();
            }

            throw new ConvertException("not a double type: " + obj.getClass().getName());
        }

        @Override
        public Object toRaw(Double aDouble) {
            return aDouble;
        }
    }

    class StringDataConverter implements IDataConverter<String> {
        @Override
        public String fromRaw(Object obj) {
            if (obj == null) return null;
            if (obj instanceof String) {
                return (String) obj;
            }
            throw new ConvertException("not a string type: " + obj.getClass().getName());
        }

        @Override
        public Object toRaw(String s) {
            return s;
        }
    }

    class TimestampDataConverter implements IDataConverter<Timestamp> {
        @Override
        public Timestamp fromRaw(Object obj) {
            if (obj == null) return null;

            if (obj instanceof Timestamp) {
                return (Timestamp) obj;
            }
            throw new ConvertException("not a timestamp type: " + obj.getClass().getName());
        }

        @Override
        public Object toRaw(Timestamp timestamp) {
            return timestamp;
        }
    }
}
