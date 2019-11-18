package com.ce.query.contract;

import java.util.Map;

public interface IRow extends Map<String, Object> {
    <T> T getAs(String key, Class<T> t);
}
