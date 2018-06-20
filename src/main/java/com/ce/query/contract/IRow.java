package com.ce.query.contract;

import java.sql.Timestamp;
import java.util.Map;

public interface IRow extends Map<String, Object> {

    Boolean getAsBoolean(String key);

    Integer getAsInteger(String key);

    Long getAsLong(String key);

    Double getAsDouble(String key);

    String getAsString(String key);

    Timestamp getAsTimestamp(String key);
}
