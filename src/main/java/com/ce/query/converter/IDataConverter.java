package com.ce.query.converter;

public interface IDataConverter<T> {

    T fromRaw(Object o);

    Object toRaw(T t);
}
