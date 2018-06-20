package com.ce.query;

import com.ce.query.contract.IRow;
import com.ce.query.lib.ConvertUtil;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Row implements IRow {

    /**
     *
     */
    private static final long serialVersionUID = 2965345640259342192L;
    private Map<String, Object> data = new HashMap<String, Object>();

    public Boolean getAsBoolean(String key) {
        Object o = this.get(key);
        return ConvertUtil.asBoolean(o);
    }

    public Integer getAsInteger(String key) {
        Object o = this.get(key);
        return ConvertUtil.asInteger(o);
    }

    public Long getAsLong(String key) {
        Object o = this.get(key);
        return ConvertUtil.asLong(o);
    }

    public Double getAsDouble(String key) {
        Object o = this.get(key);
        return ConvertUtil.asDouble(o);
    }

    public String getAsString(String key) {
        Object o = this.get(key);
        return ConvertUtil.asString(o);
    }

    public Timestamp getAsTimestamp(String key) {
        return ConvertUtil.asTimestamp(this.get(key));
    }

    @Override
    public int size() {
        return data.size();
    }

    @Override
    public boolean isEmpty() {
        return data.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return data.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return data.containsValue(value);
    }

    @Override
    public Object get(Object key) {
        return data.get(key);
    }

    @Override
    public Object put(String key, Object value) {
        return data.put(key, value);
    }

    @Override
    public Object remove(Object key) {
        return data.remove(key);
    }

    @Override
    public void putAll(Map<? extends String, ?> m) {
        data.putAll(m);
    }

    @Override
    public void clear() {
        data.clear();
    }

    @Override
    public Set<String> keySet() {
        return data.keySet();
    }

    @Override
    public Collection<Object> values() {
        return data.values();
    }

    @Override
    public Set<Entry<String, Object>> entrySet() {
        return data.entrySet();
    }
}
