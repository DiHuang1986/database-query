package com.ce.query.contract;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by dhuang on 2/23/2018.
 */
public interface IDatabaseExecution<T> {

    /**
     * execution of the database
     * @param connection
     * @return
     * @throws SQLException
     */
    T execute(Connection connection) throws SQLException;
}
