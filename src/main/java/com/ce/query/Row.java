package com.ce.query;

import com.ce.query.contract.IRow;
import com.ce.query.converter.ConverterManager;

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

    @Override
    public <T> T getAs(String key, Class<T> t) {
        Object o = this.get(key);
        if (o == null) return null;
        return ConverterManager.INSTANCE.generate(Object.class, t).convert(o);
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
