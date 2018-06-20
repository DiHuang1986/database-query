package com.ce.query.contract;

public interface IRowToEntityHandler<T> {
    T map(IRow row);
}
