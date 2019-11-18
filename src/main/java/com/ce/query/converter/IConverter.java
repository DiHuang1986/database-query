package com.ce.query.converter;

public interface IConverter<S, T> {
    T convert(S s);
}
