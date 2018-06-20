package com.ce.query.contract;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by dhuang on 2/26/2018.
 */
public interface IDatabaseExecutionVoid {

    void execute(Connection connection) throws SQLException;
}
