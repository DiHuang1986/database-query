package com.ce.query.lib;

import com.ce.query.exception.ConvertException;

import java.sql.Timestamp;

public interface ConvertUtil {
    static <T> T as(Object o, Class<T> clazz) {
        if (clazz == Boolean.class) {
            return (T) asBoolean(o);
        }
        if (clazz == Double.class) {
            return (T) asDouble(o);
        }
        if (clazz == Integer.class) {
            return (T) asInteger(o);
        }
        if (clazz == Long.class) {
            return (T) asLong(o);
        }
        if (clazz == String.class) {
            return (T) asString(o);
        }
        if (clazz == Timestamp.class) {
            return (T) asTimestamp(o);
        }
        throw new ConvertException(String.format("Class [%s] not supported yet in as method", clazz.getName()));
    }

    static Boolean asBoolean(Object obj) {
        if (obj == null) return null;
        if (obj instanceof Boolean) {
            return (Boolean) obj;
        }
        throw new ConvertException("not a boolean type: " + obj.getClass().getName());
    }

    static Integer asInteger(Object obj) {
        if (obj == null) return null;

        if (obj instanceof Integer) {
            return (Integer) obj;
        }
        if (obj instanceof Number) {
            return ((Number) obj).intValue();
        }

        throw new ConvertException("not a integer type: " + obj.getClass().getName());
    }

    static Long asLong(Object obj) {
        if (obj == null) return null;

        if (obj instanceof Long) {
            return (Long) obj;
        }
        if (obj instanceof Number) {
            return ((Number) obj).longValue();
        }

        throw new ConvertException("not a long type: " + obj.getClass().getName());
    }

    static Double asDouble(Object obj) {
        if (obj == null) return null;

        if (obj instanceof Double) {
            return (Double) obj;
        }
        if (obj instanceof Number) {
            return ((Number) obj).doubleValue();
        }
        
        throw new ConvertException("not a double type: " + obj.getClass().getName());
    }

    static String asString(Object obj) {
        if (obj == null) return null;
        if (obj instanceof String) {
            return (String) obj;
        }
        throw new ConvertException("not a string type: " + obj.getClass().getName());
    }

    static Timestamp asTimestamp(Object obj) {
        if (obj == null) return null;

        if (obj instanceof Timestamp) {
            return (Timestamp) obj;
        }
        throw new ConvertException("not a timestamp type: " + obj.getClass().getName());
    }
}
